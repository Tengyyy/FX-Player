package hans.Menu.MetadataEdit;

import hans.MediaItems.MediaItem;
import javafx.scene.layout.VBox;

import java.util.*;

public class FlacEditItem implements MetadataEditItem{

    MetadataEditPage metadataEditPage;

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
    TextAreaItem languageItem = null;
    TextAreaItem lyricsItem = null;
    TextAreaItem commentItem = null;


    VBox content = new VBox();

    Map<String, String> metadata;


    FlacEditItem(MetadataEditPage metadataEditPage, Map<String, String> metadata){
        this.metadataEditPage = metadataEditPage;
        this.metadata = metadata;

        content.setSpacing(15);


        if(metadata != null) {
            titleItem = new TextAreaItem(metadataEditPage, "Title", metadata.containsKey("TITLE") && !metadata.get("TITLE").isBlank() ? metadata.get("TITLE") : "", content, true);
            artistItem = new TextAreaItem(metadataEditPage, "Artist", metadata.containsKey("ARTIST") && !metadata.get("ARTIST").isBlank() ? metadata.get("ARTIST") : "", content, true);
            albumItem = new TextAreaItem(metadataEditPage, "Album", metadata.containsKey("ALBUM") && !metadata.get("ALBUM").isBlank() ? metadata.get("ALBUM") : "", content, true);
            releaseDateItem = new DatePickerItem(metadataEditPage, metadata.containsKey("DATE") && !metadata.get("DATE").isBlank() ? metadata.get("DATE") : "", content, true);


            trackItem = new TwoSpinnerItem(metadataEditPage, "Track number", metadata.containsKey("track") && !metadata.get("track").isBlank() ? metadata.get("track") : "", metadata.containsKey("TRACKTOTAL") && !metadata.get("TRACKTOTAL").isBlank() ? metadata.get("TRACKTOTAL") : "", content, true);

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
            composerItem = new TextAreaItem(metadataEditPage, "Composer", metadata.containsKey("COMPOSER") && !metadata.get("COMPOSER").isBlank() ? metadata.get("COMPOSER") : "", content, true);
            producerItem = new TextAreaItem(metadataEditPage, "Producer",  metadata.containsKey("PRODUCER") && !metadata.get("PRODUCER").isBlank() ? metadata.get("PRODUCER") : "", content, true);
            publisherItem = new TextAreaItem(metadataEditPage, "Publisher", metadata.containsKey("PUBLISHER") && !metadata.get("PUBLISHER").isBlank() ? metadata.get("PUBLISHER") : "", content, true);
            genreItem = new TextAreaItem(metadataEditPage, "Genre", metadata.containsKey("GENRE") && !metadata.get("GENRE").isBlank() ? metadata.get("GENRE") : "", content, true);
            languageItem = new TextAreaItem(metadataEditPage, "Language", metadata.containsKey("LANGUAGE") && !metadata.get("LANGUAGE").isBlank() ? metadata.get("LANGUAGE") : "", content, true);
            lyricsItem = new TextAreaItem(metadataEditPage, "Lyrics", metadata.containsKey("LYRICS") && !metadata.get("LYRICS").isBlank() ? metadata.get("LYRICS") : "", content, true);
            commentItem = new TextAreaItem(metadataEditPage, "Comment", metadata.containsKey("comment") && !metadata.get("comment").isBlank() ? metadata.get("comment") : "", content, true);
        }

        metadataEditPage.textBox.getChildren().add(content);
    }

    @Override
    public Map<String, String> createMetadataMap(){
        Map<String, String> mediaInformation = new HashMap<>();

        mediaInformation.put("TITLE", titleItem.textArea.getText());
        mediaInformation.put("ARTIST", artistItem.textArea.getText());
        mediaInformation.put("ALBUM", albumItem.textArea.getText());
        if(releaseDateItem.datePicker.getValue() != null) mediaInformation.put("DATE", releaseDateItem.datePicker.getValue().format(releaseDateItem.dateTimeFormatter));
        else mediaInformation.put("DATE", "");

        mediaInformation.put("track", String.valueOf(trackItem.numberSpinner1.spinner.getValue()));
        mediaInformation.put("TRACKTOTAL", String.valueOf(trackItem.numberSpinner2.spinner.getValue()));


        if(discItem.numberSpinner1.spinner.getValue() != 0){
            if(discItem.numberSpinner2.spinner.getValue() != 0) mediaInformation.put("disc", discItem.numberSpinner1.spinner.getValue() + "/" + discItem.numberSpinner2.spinner.getValue());
            else mediaInformation.put("disc", String.valueOf(discItem.numberSpinner1.spinner.getValue()));
        }

        mediaInformation.put("album_artist", albumArtistItem.textArea.getText());
        mediaInformation.put("COMPOSER", composerItem.textArea.getText());
        mediaInformation.put("PRODUCER", producerItem.textArea.getText());
        mediaInformation.put("PUBLISHER", publisherItem.textArea.getText());
        mediaInformation.put("GENRE", genreItem.textArea.getText());
        mediaInformation.put("LANGUAGE", languageItem.textArea.getText());
        mediaInformation.put("LYRICS", lyricsItem.textArea.getText());
        mediaInformation.put("comment", commentItem.textArea.getText());

        return mediaInformation;
    }
}

