package fxplayer.windows.mediaInformation.items;

import fxplayer.Utilities;
import javafx.scene.layout.VBox;
import fxplayer.windows.mediaInformation.components.DatePickerItem;
import fxplayer.windows.mediaInformation.components.DoubleSpinnerItem;
import fxplayer.windows.mediaInformation.components.TextAreaItem;
import fxplayer.windows.mediaInformation.components.TextFieldItem;
import fxplayer.windows.mediaInformation.MediaInformationWindow;

import java.util.HashMap;
import java.util.Map;

public class M4aItem implements MediaInformationItem {

    MediaInformationWindow mediaInformationWindow;

    TextFieldItem titleItem = null;
    TextFieldItem artistItem = null;
    DoubleSpinnerItem trackItem = null;
    TextFieldItem albumItem = null;
    TextAreaItem albumArtistItem = null;
    TextAreaItem composerItem = null;
    TextFieldItem genreItem = null;
    TextAreaItem descriptionItem = null;
    TextAreaItem lyricsItem = null;
    DatePickerItem releaseDateItem = null;
    TextAreaItem commentItem = null;

    VBox content = new VBox();

    Map<String, String> metadata;



    public M4aItem(MediaInformationWindow mediaInformationWindow, Map<String, String> metadata){
        this.mediaInformationWindow = mediaInformationWindow;
        this.metadata = metadata;

        content.setSpacing(15);

        if(metadata != null) {
            titleItem = new TextFieldItem(mediaInformationWindow, "Title", metadata.containsKey("title") && !metadata.get("title").isBlank() ? metadata.get("title") : "", content, true);

            artistItem = new TextFieldItem(mediaInformationWindow, "Artist", metadata.containsKey("artist") && !metadata.get("artist").isBlank() ? metadata.get("artist") : "", content, true);

            albumItem = new TextFieldItem(mediaInformationWindow, "Album", metadata.containsKey("album") && !metadata.get("album").isBlank() ? metadata.get("album") : "", content, true);

            String[] trackString = Utilities.splitString(metadata.getOrDefault("track", ""));
            trackItem = new DoubleSpinnerItem(mediaInformationWindow, "Track number", trackString[0], trackString[1], content, true);

            releaseDateItem = new DatePickerItem(mediaInformationWindow, metadata.containsKey("date") && !metadata.get("date").isBlank() ? metadata.get("date") : "", content, true);

            albumArtistItem = new TextAreaItem(mediaInformationWindow, "Album artist", metadata.containsKey("album_artist") && !metadata.get("album_artist").isBlank() ? metadata.get("album_artist") : "", content, true);

            composerItem = new TextAreaItem(mediaInformationWindow, "Composer", metadata.containsKey("composer") && !metadata.get("composer").isBlank() ? metadata.get("composer") : "", content, true);

            genreItem = new TextFieldItem(mediaInformationWindow, "Genre", metadata.containsKey("genre") && !metadata.get("genre").isBlank() ? metadata.get("genre") : "", content, true);

            lyricsItem = new TextAreaItem(mediaInformationWindow, "Lyrics", metadata.containsKey("lyrics") && !metadata.get("lyrics").isBlank() ? metadata.get("lyrics") : "", content, true);

            descriptionItem = new TextAreaItem(mediaInformationWindow, "Description", metadata.containsKey("description") && !metadata.get("description").isBlank() ? metadata.get("description") : "", content, true);

            commentItem = new TextAreaItem(mediaInformationWindow, "Comment", metadata.containsKey("comment") && !metadata.get("comment").isBlank() ? metadata.get("comment") : "", content, true);
        }

        mediaInformationWindow.textBox.getChildren().add(content);
    }

    @Override
    public Map<String, String> createMetadataMap(){
        Map<String, String> mediaInformation = new HashMap<>();

        if(!titleItem.textField.getText().isBlank()) mediaInformation.put("title", titleItem.textField.getText());
        if(!artistItem.textField.getText().isBlank()) mediaInformation.put("artist", artistItem.textField.getText());


        if(!composerItem.textArea.getText().isBlank()) mediaInformation.put("composer", composerItem.textArea.getText());
        if(!albumArtistItem.textArea.getText().isBlank()) mediaInformation.put("album_artist", albumArtistItem.textArea.getText());
        if(!genreItem.textField.getText().isBlank()) mediaInformation.put("genre", genreItem.textField.getText());
        if(!descriptionItem.textArea.getText().isBlank()) mediaInformation.put("description", descriptionItem.textArea.getText());

        if(!lyricsItem.textArea.getText().isBlank()) mediaInformation.put("lyrics", lyricsItem.textArea.getText());
        if(!albumItem.textField.getText().isBlank()) mediaInformation.put("album", albumItem.textField.getText());
        if(trackItem.numberSpinner1.spinner.getValue() != 0){
            String trackString = String.valueOf(trackItem.numberSpinner1.spinner.getValue());
            if(trackItem.numberSpinner2.spinner.getValue() != 0) trackString = trackString.concat("/" + trackItem.numberSpinner2.spinner.getValue());
            mediaInformation.put("track", trackString);
        }

        String date = releaseDateItem.parseDate();
        if(!date.isEmpty()) mediaInformation.put("date", date);
        if(!commentItem.textArea.getText().isBlank()) mediaInformation.put("comment", commentItem.textArea.getText());

        return mediaInformation;

    }
}

