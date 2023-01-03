package hans.Menu.MetadataEdit;

import hans.MediaItems.MediaItem;
import javafx.scene.layout.VBox;

import java.util.*;

public class Mp3EditItem implements MetadataEditItem{

    MetadataEditPage metadataEditPage;
    MediaItem mediaItem;

    TextAreaItem titleItem = null;
    TextAreaItem artistItem = null;
    TextAreaItem albumItem = null;
    DatePickerItem releaseDateItem = null;
    TwoSpinnerItem trackItem = null;
    TwoSpinnerItem discItem = null;
    TextAreaItem albumArtistItem = null;
    TextAreaItem composerItem = null;
    TextAreaItem producerItem = null;
    TextAreaItem publisherItem = null;
    TextAreaItem genreItem = null;
    TextAreaItem lyricsItem = null;
    TextAreaItem commentItem = null;


    VBox content = new VBox();

    Map<String, String> metadata;


    Mp3EditItem(MetadataEditPage metadataEditPage, MediaItem mediaItem){
        this.metadataEditPage = metadataEditPage;
        this.mediaItem = mediaItem;

        content.setSpacing(15);

        metadata = mediaItem.getMediaInformation();

        if(metadata != null) {
            titleItem = new TextAreaItem(metadataEditPage, "Title", metadata.containsKey("title") && !metadata.get("title").isBlank() ? metadata.get("title") : "", content, true);
            artistItem = new TextAreaItem(metadataEditPage, "Artist", metadata.containsKey("artist") && !metadata.get("artist").isBlank() ? metadata.get("artist") : "", content, true);
            albumItem = new TextAreaItem(metadataEditPage, "Album", metadata.containsKey("album") && !metadata.get("album").isBlank() ? metadata.get("album") : "", content, true);
            releaseDateItem = new DatePickerItem(metadataEditPage, metadata.containsKey("date") && !metadata.get("date").isBlank() ? metadata.get("date") : "", content, true);

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
            trackItem = new TwoSpinnerItem(metadataEditPage, "Track number", track, trackTotal, content, true);

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
            discItem = new TwoSpinnerItem(metadataEditPage, "Disc number", disc, discTotal, content, true);
            albumArtistItem = new TextAreaItem(metadataEditPage, "Album artist", metadata.containsKey("album_artist") && !metadata.get("album_artist").isBlank() ? metadata.get("album_artist") : "", content, true);
            composerItem = new TextAreaItem(metadataEditPage, "Composer", metadata.containsKey("composer") && !metadata.get("composer").isBlank() ? metadata.get("composer") : "", content, true);
            producerItem = new TextAreaItem(metadataEditPage, "Producer",  metadata.containsKey("producer") && !metadata.get("producer").isBlank() ? metadata.get("producer") : "", content, true);
            publisherItem = new TextAreaItem(metadataEditPage, "Publisher", metadata.containsKey("publisher") && !metadata.get("publisher").isBlank() ? metadata.get("publisher") : "", content, true);
            genreItem = new TextAreaItem(metadataEditPage, "Genre", metadata.containsKey("genre") && !metadata.get("genre").isBlank() ? metadata.get("genre") : "", content, true);
            lyricsItem = new TextAreaItem(metadataEditPage, "Lyrics", metadata.containsKey("lyrics") && !metadata.get("lyrics").isBlank() ? metadata.get("lyrics") : "", content, true);
            commentItem = new TextAreaItem(metadataEditPage, "Comment", metadata.containsKey("comment") && !metadata.get("comment").isBlank() ? metadata.get("comment") : "", content, true);
        }

        metadataEditPage.textBox.getChildren().add(content);
    }

    @Override
    public Map<String, String> saveMetadata(){
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

        if(discItem.numberSpinner1.spinner.getValue() != 0){
            if(discItem.numberSpinner2.spinner.getValue() != 0) mediaInformation.put("disc", discItem.numberSpinner1.spinner.getValue() + "/" + discItem.numberSpinner2.spinner.getValue());
            else mediaInformation.put("disc", String.valueOf(discItem.numberSpinner1.spinner.getValue()));
        }

        mediaInformation.put("album_artist", albumArtistItem.textArea.getText());
        mediaInformation.put("composer", composerItem.textArea.getText());
        mediaInformation.put("producer", producerItem.textArea.getText());
        mediaInformation.put("publisher", publisherItem.textArea.getText());
        mediaInformation.put("genre", genreItem.textArea.getText());
        mediaInformation.put("lyrics", lyricsItem.textArea.getText());
        mediaInformation.put("comment", commentItem.textArea.getText());

        return mediaInformation;
    }
}

