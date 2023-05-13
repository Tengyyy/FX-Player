package tengy.Menu.MediaInformation;

import tengy.Utilities;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;

public class M4aItem implements MediaInformationItem {

    MediaInformationPage mediaInformationPage;

    TextAreaItem titleItem = null;
    TextAreaItem artistItem = null;
    TwoSpinnerItem trackItem = null;
    TextAreaItem albumItem = null;
    TextAreaItem albumArtistItem = null;
    TextAreaItem composerItem = null;
    TextAreaItem genreItem = null;
    TextAreaItem descriptionItem = null;
    TextAreaItem lyricsItem = null;
    DatePickerItem releaseDateItem = null;
    TextAreaItem commentItem = null;

    VBox content = new VBox();

    Map<String, String> metadata;



    M4aItem(MediaInformationPage mediaInformationPage, Map<String, String> metadata){
        this.mediaInformationPage = mediaInformationPage;
        this.metadata = metadata;

        content.setSpacing(15);

        if(metadata != null) {
            titleItem = new TextAreaItem(mediaInformationPage, "Title", metadata.containsKey("title") && !metadata.get("title").isBlank() ? metadata.get("title") : "", content, true);

            artistItem = new TextAreaItem(mediaInformationPage, "Artist", metadata.containsKey("artist") && !metadata.get("artist").isBlank() ? metadata.get("artist") : "", content, true);

            albumItem = new TextAreaItem(mediaInformationPage, "Album", metadata.containsKey("album") && !metadata.get("album").isBlank() ? metadata.get("album") : "", content, true);

            String[] trackString = Utilities.splitString(metadata.getOrDefault("track", ""));
            trackItem = new TwoSpinnerItem(mediaInformationPage, "Track number", trackString[0], trackString[1], content, true);

            releaseDateItem = new DatePickerItem(mediaInformationPage, metadata.containsKey("date") && !metadata.get("date").isBlank() ? metadata.get("date") : "", content, true);

            albumArtistItem = new TextAreaItem(mediaInformationPage, "Album artist", metadata.containsKey("album_artist") && !metadata.get("album_artist").isBlank() ? metadata.get("album_artist") : "", content, true);

            composerItem = new TextAreaItem(mediaInformationPage, "Composer", metadata.containsKey("composer") && !metadata.get("composer").isBlank() ? metadata.get("composer") : "", content, true);

            genreItem = new TextAreaItem(mediaInformationPage, "Genre", metadata.containsKey("genre") && !metadata.get("genre").isBlank() ? metadata.get("genre") : "", content, true);

            lyricsItem = new TextAreaItem(mediaInformationPage, "Lyrics", metadata.containsKey("lyrics") && !metadata.get("lyrics").isBlank() ? metadata.get("lyrics") : "", content, true);

            descriptionItem = new TextAreaItem(mediaInformationPage, "Description", metadata.containsKey("description") && !metadata.get("description").isBlank() ? metadata.get("description") : "", content, true);

            commentItem = new TextAreaItem(mediaInformationPage, "Comment", metadata.containsKey("comment") && !metadata.get("comment").isBlank() ? metadata.get("comment") : "", content, true);
        }

        mediaInformationPage.textBox.getChildren().add(content);
    }

    @Override
    public Map<String, String> createMetadataMap(){
        Map<String, String> mediaInformation = new HashMap<>();

        if(!titleItem.textArea.getText().isBlank()) mediaInformation.put("title", titleItem.textArea.getText());
        if(!artistItem.textArea.getText().isBlank()) mediaInformation.put("artist", artistItem.textArea.getText());


        if(!composerItem.textArea.getText().isBlank()) mediaInformation.put("composer", composerItem.textArea.getText());
        if(!albumArtistItem.textArea.getText().isBlank()) mediaInformation.put("album_artist", albumArtistItem.textArea.getText());
        if(!genreItem.textArea.getText().isBlank()) mediaInformation.put("genre", genreItem.textArea.getText());
        if(!descriptionItem.textArea.getText().isBlank()) mediaInformation.put("description", descriptionItem.textArea.getText());

        if(!lyricsItem.textArea.getText().isBlank()) mediaInformation.put("lyrics", lyricsItem.textArea.getText());
        if(!albumItem.textArea.getText().isBlank()) mediaInformation.put("album", albumItem.textArea.getText());
        if(trackItem.numberSpinner1.spinner.getValue() != 0){
            String trackString = String.valueOf(trackItem.numberSpinner1.spinner.getValue());
            if(trackItem.numberSpinner2.spinner.getValue() != 0) trackString = trackString.concat("/" + trackItem.numberSpinner2.spinner.getValue());
            mediaInformation.put("track", trackString);
        }

        if(releaseDateItem.datePicker.getValue() != null) mediaInformation.put("date", releaseDateItem.datePicker.getValue().format(releaseDateItem.dateTimeFormatter));
        if(!commentItem.textArea.getText().isBlank()) mediaInformation.put("comment", commentItem.textArea.getText());

        return mediaInformation;

    }
}

