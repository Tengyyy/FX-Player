package tengy.Menu.MediaInformation;

import tengy.MediaItems.MediaItem;
import javafx.scene.layout.VBox;

import java.util.*;

public class WavItem implements MediaInformationItem {

    MediaInformationPage mediaInformationPage;

    TextAreaItem titleItem = null;
    TextAreaItem artistItem = null;
    TextAreaItem albumItem = null;
    DatePickerItem releaseDateItem = null;
    TwoSpinnerItem trackItem = null;
    TextAreaItem genreItem = null;
    TextAreaItem commentItem = null;


    VBox content = new VBox();

    Map<String, String> metadata;


    WavItem(MediaInformationPage mediaInformationPage, Map<String, String> metadata){
        this.mediaInformationPage = mediaInformationPage;
        this.metadata = metadata;

        content.setSpacing(15);


        if(metadata != null) {
            titleItem = new TextAreaItem(mediaInformationPage, "Title", metadata.containsKey("title") && !metadata.get("title").isBlank() ? metadata.get("title") : "", content, true);
            artistItem = new TextAreaItem(mediaInformationPage, "Artist", metadata.containsKey("artist") && !metadata.get("artist").isBlank() ? metadata.get("artist") : "", content, true);
            albumItem = new TextAreaItem(mediaInformationPage, "Album", metadata.containsKey("album") && !metadata.get("album").isBlank() ? metadata.get("album") : "", content, true);
            releaseDateItem = new DatePickerItem(mediaInformationPage, metadata.containsKey("date") && !metadata.get("date").isBlank() ? metadata.get("date") : "", content, true);
            String track = "";
            String trackTotal = "";
            if(metadata.containsKey("track")){
                String trackData = metadata.get("track");
                if(trackData.contains("/") && trackData.indexOf('/') != 0 && trackData.indexOf('/') < trackData.length() - 1){
                    track = trackData.substring(0, trackData.indexOf('/'));
                    trackTotal = trackData.substring(trackData.indexOf('/') + 1);
                }
                else track = trackData;
            }
            trackItem = new TwoSpinnerItem(mediaInformationPage, "Track number", track, trackTotal, content, true);
            genreItem = new TextAreaItem(mediaInformationPage, "Genre", metadata.containsKey("genre") && !metadata.get("genre").isBlank() ? metadata.get("genre") : "", content, true);
            commentItem = new TextAreaItem(mediaInformationPage, "Comment", metadata.containsKey("comment") && !metadata.get("comment").isBlank() ? metadata.get("comment") : "", content, true);
        }

        mediaInformationPage.textBox.getChildren().add(content);
    }

    @Override
    public Map<String, String> createMetadataMap(){
        Map<String, String> mediaInformation = new HashMap<>();

        mediaInformation.put("title", titleItem.textArea.getText());
        mediaInformation.put("artist", artistItem.textArea.getText());
        mediaInformation.put("album", albumItem.textArea.getText());
        if(releaseDateItem.datePicker.getValue() != null) mediaInformation.put("date", releaseDateItem.datePicker.getValue().format(releaseDateItem.dateTimeFormatter));
        else mediaInformation.put("date", "");

        if(trackItem.numberSpinner1.spinner.getValue() != 0){
            if(trackItem.numberSpinner2.spinner.getValue() != 0) mediaInformation.put("track", trackItem.numberSpinner1.spinner.getValue() + "/" + trackItem.numberSpinner2.spinner.getValue());
            else mediaInformation.put("track", String.valueOf(trackItem.numberSpinner1.spinner.getValue()));
        }

        mediaInformation.put("genre", genreItem.textArea.getText());
        mediaInformation.put("comment", commentItem.textArea.getText());

        return mediaInformation;
    }
}

