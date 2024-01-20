package fxplayer.windows.mediaInformation.items;

import javafx.scene.layout.VBox;
import fxplayer.windows.mediaInformation.components.DatePickerItem;
import fxplayer.windows.mediaInformation.components.DoubleSpinnerItem;
import fxplayer.windows.mediaInformation.components.TextAreaItem;
import fxplayer.windows.mediaInformation.MediaInformationWindow;

import java.util.*;

public class WavItem implements MediaInformationItem {

    MediaInformationWindow mediaInformationWindow;

    TextAreaItem titleItem = null;
    TextAreaItem artistItem = null;
    TextAreaItem albumItem = null;
    DatePickerItem releaseDateItem = null;
    DoubleSpinnerItem trackItem = null;
    TextAreaItem genreItem = null;
    TextAreaItem commentItem = null;


    VBox content = new VBox();

    Map<String, String> metadata;


    public WavItem(MediaInformationWindow mediaInformationWindow, Map<String, String> metadata){
        this.mediaInformationWindow = mediaInformationWindow;
        this.metadata = metadata;

        content.setSpacing(15);


        if(metadata != null) {
            titleItem = new TextAreaItem(mediaInformationWindow, "Title", metadata.containsKey("title") && !metadata.get("title").isBlank() ? metadata.get("title") : "", content, true);
            artistItem = new TextAreaItem(mediaInformationWindow, "Artist", metadata.containsKey("artist") && !metadata.get("artist").isBlank() ? metadata.get("artist") : "", content, true);
            albumItem = new TextAreaItem(mediaInformationWindow, "Album", metadata.containsKey("album") && !metadata.get("album").isBlank() ? metadata.get("album") : "", content, true);
            releaseDateItem = new DatePickerItem(mediaInformationWindow, metadata.containsKey("date") && !metadata.get("date").isBlank() ? metadata.get("date") : "", content, true);
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
            trackItem = new DoubleSpinnerItem(mediaInformationWindow, "Track number", track, trackTotal, content, true);
            genreItem = new TextAreaItem(mediaInformationWindow, "Genre", metadata.containsKey("genre") && !metadata.get("genre").isBlank() ? metadata.get("genre") : "", content, true);
            commentItem = new TextAreaItem(mediaInformationWindow, "Comment", metadata.containsKey("comment") && !metadata.get("comment").isBlank() ? metadata.get("comment") : "", content, true);
        }

        mediaInformationWindow.textBox.getChildren().add(content);
    }

    @Override
    public Map<String, String> createMetadataMap(){
        Map<String, String> mediaInformation = new HashMap<>();

        mediaInformation.put("title", titleItem.textArea.getText());
        mediaInformation.put("artist", artistItem.textArea.getText());
        mediaInformation.put("album", albumItem.textArea.getText());
        String date = releaseDateItem.parseDate();
        mediaInformation.put("date", date);

        if(trackItem.numberSpinner1.spinner.getValue() != 0){
            if(trackItem.numberSpinner2.spinner.getValue() != 0) mediaInformation.put("track", trackItem.numberSpinner1.spinner.getValue() + "/" + trackItem.numberSpinner2.spinner.getValue());
            else mediaInformation.put("track", String.valueOf(trackItem.numberSpinner1.spinner.getValue()));
        }

        mediaInformation.put("genre", genreItem.textArea.getText());
        mediaInformation.put("comment", commentItem.textArea.getText());

        return mediaInformation;
    }
}

