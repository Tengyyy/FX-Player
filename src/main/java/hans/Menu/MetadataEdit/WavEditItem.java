package hans.Menu.MetadataEdit;

import hans.MediaItems.MediaItem;
import javafx.scene.layout.VBox;

import java.time.DateTimeException;
import java.util.*;

public class WavEditItem implements MetadataEditItem{

    MetadataEditPage metadataEditPage;
    MediaItem mediaItem;

    TextAreaItem titleItem = null;
    TextAreaItem artistItem = null;
    TextAreaItem albumItem = null;
    DatePickerItem releaseDateItem = null;
    SpinnerItem trackItem = null;
    TextAreaItem albumArtistItem = null;
    TextAreaItem composerItem = null;
    TextAreaItem publisherItem = null;
    TextAreaItem genreItem = null;
    TextAreaItem commentItem = null;


    VBox content = new VBox();

    Map<String, String> metadata;


    WavEditItem(MetadataEditPage metadataEditPage, MediaItem mediaItem){
        this.metadataEditPage = metadataEditPage;
        this.mediaItem = mediaItem;

        content.setSpacing(15);

        metadata = mediaItem.getMediaInformation();

        if(metadata != null) {
            titleItem = new TextAreaItem(metadataEditPage, "Title", metadata.get("title").isBlank() ? "" : metadata.get("title"), content, true);
            artistItem = new TextAreaItem(metadataEditPage, "Artist", metadata.get("artist").isBlank() ? "" : metadata.get("artist"), content, true);
            albumItem = new TextAreaItem(metadataEditPage, "Album", metadata.get("album").isBlank() ? "" : metadata.get("album"), content, true);
            releaseDateItem = new DatePickerItem(metadataEditPage, metadata.get("year").isBlank() ? "" : metadata.get("year"), content, true);
            trackItem = new SpinnerItem(metadataEditPage, "Track number", metadata.get("track").isBlank() ? "" : metadata.get("track"), content, true); // keep an eye on potential problems for the twofield items
            albumArtistItem = new TextAreaItem(metadataEditPage, "Album artist", metadata.get("album_artist").isBlank() ? "" : metadata.get("album_artist"), content, true);
            composerItem = new TextAreaItem(metadataEditPage, "Composer", metadata.get("composer").isBlank() ? "" : metadata.get("composer"), content, true);
            publisherItem = new TextAreaItem(metadataEditPage, "Publisher", metadata.get("record_label").isBlank() ? "" : metadata.get("record_label"), content, true);
            genreItem = new TextAreaItem(metadataEditPage, "Genre", metadata.get("genre").isBlank() ? "" : metadata.get("genre"), content, true);
            commentItem = new TextAreaItem(metadataEditPage, "Comment", metadata.get("comment").isBlank() ? "" : metadata.get("comment"), content, true);
        }

        metadataEditPage.textBox.getChildren().add(content);
    }

    @Override
    public Map<String, String> saveMetadata(){
        Map<String, String> mediaInformation = new HashMap<>();

        mediaInformation.put("title", titleItem.textArea.getText());
        mediaInformation.put("artist", artistItem.textArea.getText());
        mediaInformation.put("album", albumItem.textArea.getText());
        try {
            mediaInformation.put("year", releaseDateItem.datePicker.getValue().format(releaseDateItem.dateTimeFormatter));
        } catch (DateTimeException e){
            mediaInformation.put("year", "");
        }
        mediaInformation.put("track", String.valueOf(trackItem.numberSpinner.spinner.getValue()));
        mediaInformation.put("album_artist", albumArtistItem.textArea.getText());
        mediaInformation.put("composer", composerItem.textArea.getText());
        mediaInformation.put("record_label", publisherItem.textArea.getText());
        mediaInformation.put("genre", genreItem.textArea.getText());
        mediaInformation.put("comment", commentItem.textArea.getText());

        return mediaInformation;
    }
}

