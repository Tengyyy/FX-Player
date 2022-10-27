package hans.Menu.MetadataEdit;

import hans.MediaItems.MediaItem;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Map;

public class OtherEditPage {

    MetadataEditPage metadataEditPage;
    MediaItem mediaItem;

    ArrayList<TextAreaItem> items = new ArrayList<>();

    VBox content = new VBox();

    Map<String, String> metadata;


    OtherEditPage(MetadataEditPage metadataEditPage, MediaItem mediaItem){
        this.metadataEditPage = metadataEditPage;
        this.mediaItem = mediaItem;

        content.setSpacing(15);

        metadata = mediaItem.getMediaInformation();

        if(metadata != null) {

        }

        metadataEditPage.textBox.getChildren().add(content);
    }


    public void saveMetadata(){

    }
}




