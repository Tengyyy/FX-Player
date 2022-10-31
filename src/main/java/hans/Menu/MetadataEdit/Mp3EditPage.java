package hans.Menu.MetadataEdit;

import hans.MediaItems.MediaItem;
import javafx.scene.layout.VBox;

import java.util.Map;

public class Mp3EditPage {

    MetadataEditPage metadataEditPage;
    MediaItem mediaItem;

    TextAreaItem titleItem = null;
    TextAreaItem artistItem = null;
    TextAreaItem albumItem = null;
    TrackFieldItem trackItem = null;
    TextFieldItem discItem = null;
    TextAreaItem albumArtistItem = null;
    TextAreaItem composerItem = null;
    TextAreaItem performerItem = null;
    TextAreaItem publisherItem = null;
    TextAreaItem genreItem = null;
    TextAreaItem languageItem = null;
    TextAreaItem releaseDateItem = null;
    TextAreaItem lyricsItem = null;


    VBox content = new VBox();

    Map<String, String> metadata;


    Mp3EditPage(MetadataEditPage metadataEditPage, MediaItem mediaItem){
        this.metadataEditPage = metadataEditPage;
        this.mediaItem = mediaItem;

        content.setSpacing(15);

        metadata = mediaItem.getMediaInformation();

        if(metadata != null) {
            titleItem = new TextAreaItem(metadataEditPage, "Title", metadata.containsKey("title") && !metadata.get("title").trim().isEmpty() ? metadata.get("title") : "", content, true);
            artistItem = new TextAreaItem(metadataEditPage, "Artist", metadata.containsKey("artist") && !metadata.get("artist").trim().isEmpty() ? metadata.get("artist") : "", content, true);
            albumItem = new TextAreaItem(metadataEditPage, "Album", metadata.containsKey("album") && !metadata.get("album").trim().isEmpty() ? metadata.get("album") : "", content, true);
            trackItem = new TrackFieldItem(metadataEditPage, metadata.containsKey("track") && !metadata.get("track").trim().isEmpty() ? metadata.get("track") : "", content, true);
            discItem = new TextFieldItem(metadataEditPage, "Disc", metadata.containsKey("disc") && !metadata.get("disc").trim().isEmpty() ? metadata.get("disc") : "", content, true);
            albumArtistItem = new TextAreaItem(metadataEditPage, "Director", metadata.containsKey("album_artist") && !metadata.get("album_artist").trim().isEmpty() ? metadata.get("album_artist") : "", content, true);
            composerItem = new TextAreaItem(metadataEditPage, "Composer", metadata.containsKey("composer") && !metadata.get("composer").trim().isEmpty() ? metadata.get("composer") : "", content, true);
            performerItem = new TextAreaItem(metadataEditPage, "Performer", metadata.containsKey("performer") && !metadata.get("performer").trim().isEmpty() ? metadata.get("performer") : "", content, true);
            publisherItem = new TextAreaItem(metadataEditPage, "Publisher", metadata.containsKey("publisher") && !metadata.get("publisher").trim().isEmpty() ? metadata.get("publisher") : "", content, true);
            genreItem = new TextAreaItem(metadataEditPage, "Genre", metadata.containsKey("genre") && !metadata.get("genre").trim().isEmpty() ? metadata.get("genre") : "", content, true);
            languageItem = new TextAreaItem(metadataEditPage, "Language", metadata.containsKey("language") && !metadata.get("language").trim().isEmpty() ? metadata.get("language") : "", content, true);
            releaseDateItem = new TextAreaItem(metadataEditPage, "Release date", metadata.containsKey("date") && !metadata.get("date").trim().isEmpty() ? metadata.get("date") : "", content, true);
            lyricsItem = new TextAreaItem(metadataEditPage, "Lyrics", metadata.containsKey("lyrics") && !metadata.get("lyrics").trim().isEmpty() ? metadata.get("lyrics") : "", content, true);
        }

        metadataEditPage.textBox.getChildren().add(content);
    }


    public void saveMetadata(){

    }
}

