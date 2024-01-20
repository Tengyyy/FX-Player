package fxplayer.windows.mediaInformation.items;

import javafx.scene.layout.VBox;
import fxplayer.windows.mediaInformation.components.DatePickerItem;
import fxplayer.windows.mediaInformation.components.DoubleSpinnerItem;
import fxplayer.windows.mediaInformation.components.TextAreaItem;
import fxplayer.windows.mediaInformation.components.TextFieldItem;
import fxplayer.windows.mediaInformation.MediaInformationWindow;

import java.util.*;

public class Mp3Item implements MediaInformationItem {

    MediaInformationWindow mediaInformationWindow;

    TextFieldItem titleItem = null;
    TextFieldItem artistItem = null;
    TextFieldItem albumItem = null;
    DatePickerItem releaseDateItem = null;
    DoubleSpinnerItem trackItem = null;
    DoubleSpinnerItem discItem = null;
    TextAreaItem albumArtistItem = null;
    TextAreaItem composerItem = null;
    TextAreaItem producerItem = null;
    TextFieldItem publisherItem = null;
    TextFieldItem genreItem = null;
    TextAreaItem lyricsItem = null;
    TextAreaItem commentItem = null;


    VBox content = new VBox();

    Map<String, String> metadata;


    public Mp3Item(MediaInformationWindow mediaInformationWindow, Map<String, String> metadata){
        this.mediaInformationWindow = mediaInformationWindow;
        this.metadata = metadata;

        content.setSpacing(15);

        if(metadata != null) {
            titleItem = new TextFieldItem(mediaInformationWindow, "Title", metadata.containsKey("title") && !metadata.get("title").isBlank() ? metadata.get("title") : "", content, true);
            artistItem = new TextFieldItem(mediaInformationWindow, "Artist", metadata.containsKey("artist") && !metadata.get("artist").isBlank() ? metadata.get("artist") : "", content, true);
            albumItem = new TextFieldItem(mediaInformationWindow, "Album", metadata.containsKey("album") && !metadata.get("album").isBlank() ? metadata.get("album") : "", content, true);
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

            String disc = "";
            String discTotal = "";
            if(metadata.containsKey("disc")){
                String discData = metadata.get("disc");
                if(discData.indexOf('/') > 0 && discData.indexOf('/') < discData.length() - 1){
                    disc = discData.substring(0, discData.indexOf('/'));
                    discTotal = discData.substring(discData.indexOf('/') + 1);
                }
                else disc = discData;
            }
            discItem = new DoubleSpinnerItem(mediaInformationWindow, "Disc number", disc, discTotal, content, true);
            albumArtistItem = new TextAreaItem(mediaInformationWindow, "Album artist", metadata.containsKey("album_artist") && !metadata.get("album_artist").isBlank() ? metadata.get("album_artist") : "", content, true);
            composerItem = new TextAreaItem(mediaInformationWindow, "Composer", metadata.containsKey("composer") && !metadata.get("composer").isBlank() ? metadata.get("composer") : "", content, true);
            producerItem = new TextAreaItem(mediaInformationWindow, "Producer",  metadata.containsKey("producer") && !metadata.get("producer").isBlank() ? metadata.get("producer") : "", content, true);
            publisherItem = new TextFieldItem(mediaInformationWindow, "Publisher", metadata.containsKey("publisher") && !metadata.get("publisher").isBlank() ? metadata.get("publisher") : "", content, true);
            genreItem = new TextFieldItem(mediaInformationWindow, "Genre", metadata.containsKey("genre") && !metadata.get("genre").isBlank() ? metadata.get("genre") : "", content, true);
            lyricsItem = new TextAreaItem(mediaInformationWindow, "Lyrics", metadata.containsKey("lyrics") && !metadata.get("lyrics").isBlank() ? metadata.get("lyrics") : "", content, true);
            commentItem = new TextAreaItem(mediaInformationWindow, "Comment", metadata.containsKey("comment") && !metadata.get("comment").isBlank() ? metadata.get("comment") : "", content, true);
        }

        mediaInformationWindow.textBox.getChildren().add(content);
    }

    @Override
    public Map<String, String> createMetadataMap(){
        Map<String, String> mediaInformation = new HashMap<>();

        mediaInformation.put("title", titleItem.textField.getText());
        mediaInformation.put("artist", artistItem.textField.getText());
        mediaInformation.put("album", albumItem.textField.getText());
        String date = releaseDateItem.parseDate();
        mediaInformation.put("date", date);

        if(trackItem.numberSpinner1.spinner.getValue() != 0){
            if(trackItem.numberSpinner2.spinner.getValue() != 0) mediaInformation.put("track", trackItem.numberSpinner1.spinner.getValue() + "/" + trackItem.numberSpinner2.spinner.getValue());
            else mediaInformation.put("track", String.valueOf(trackItem.numberSpinner1.spinner.getValue()));
        }

        if(discItem.numberSpinner1.spinner.getValue() != 0){
            if(discItem.numberSpinner2.spinner.getValue() != 0) mediaInformation.put("disc", discItem.numberSpinner1.spinner.getValue() + "/" + discItem.numberSpinner2.spinner.getValue());
            else mediaInformation.put("disc", String.valueOf(discItem.numberSpinner1.spinner.getValue()));
        }

        mediaInformation.put("album_artist", albumArtistItem.textArea.getText());
        mediaInformation.put("composer", composerItem.textArea.getText());
        mediaInformation.put("producer", producerItem.textArea.getText());
        mediaInformation.put("publisher", publisherItem.textField.getText());
        mediaInformation.put("genre", genreItem.textField.getText());
        mediaInformation.put("lyrics", lyricsItem.textArea.getText());
        mediaInformation.put("comment", commentItem.textArea.getText());

        return mediaInformation;
    }
}

