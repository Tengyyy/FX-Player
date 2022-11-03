package hans.Menu.MetadataEdit;

import hans.MediaItems.MediaItem;
import javafx.scene.layout.VBox;

import java.util.Map;

public class AviEditPage {

    MetadataEditPage metadataEditPage;
    MediaItem mediaItem;

    TextAreaItem titleItem = null;
    TextAreaItem artistItem = null;
    TextAreaItem albumItem = null;
    TwoTextFieldItem trackItem = null;
    TextAreaItem genreItem = null;
    TextAreaItem languageItem = null;
    TextAreaItem releaseDateItem = null;
    TextAreaItem copyrightItem = null;
    TextAreaItem commentItem = null;

    VBox content = new VBox();

    Map<String, String> metadata;


    AviEditPage(MetadataEditPage metadataEditPage, MediaItem mediaItem){
        this.metadataEditPage = metadataEditPage;
        this.mediaItem = mediaItem;

        content.setSpacing(15);

        metadata = mediaItem.getMediaInformation();

        if(metadata != null) {
            titleItem = new TextAreaItem(metadataEditPage, "Title", metadata.containsKey("title") && !metadata.get("title").trim().isEmpty() ? metadata.get("title") : "", content, true);
            artistItem = new TextAreaItem(metadataEditPage, "Artist", metadata.containsKey("artist") && !metadata.get("artist").trim().isEmpty() ? metadata.get("artist") : "", content, true);
            albumItem = new TextAreaItem(metadataEditPage, "Album", metadata.containsKey("album") && !metadata.get("album").trim().isEmpty() ? metadata.get("album") : "", content, true);
            trackItem = new TwoTextFieldItem(metadataEditPage, "Track number", metadata.containsKey("track") && !metadata.get("track").trim().isEmpty() ? metadata.get("track") : "", content, true);
            genreItem = new TextAreaItem(metadataEditPage, "Genre", metadata.containsKey("genre") && !metadata.get("genre").trim().isEmpty() ? metadata.get("genre") : "", content, true);
            languageItem = new TextAreaItem(metadataEditPage, "Language", metadata.containsKey("language") && !metadata.get("language").trim().isEmpty() ? metadata.get("language") : "", content, true);
            releaseDateItem = new TextAreaItem(metadataEditPage, "Release date", metadata.containsKey("date") && !metadata.get("date").trim().isEmpty() ? metadata.get("date") : "", content, true);
            copyrightItem = new TextAreaItem(metadataEditPage, "Copyright", metadata.containsKey("copyright") && !metadata.get("copyright").trim().isEmpty() ? metadata.get("copyright") : "", content, true);
            commentItem = new TextAreaItem(metadataEditPage, "Comment", metadata.containsKey("comment") && !metadata.get("comment").trim().isEmpty() ? metadata.get("comment") : "", content, true);

        }

        metadataEditPage.textBox.getChildren().add(content);
    }


    public void saveMetadata(){

    }
}

