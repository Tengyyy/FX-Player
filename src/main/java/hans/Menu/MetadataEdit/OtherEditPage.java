package hans.Menu.MetadataEdit;

import hans.MediaItems.MediaItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Map;

public class OtherEditPage {

    MetadataEditPage metadataEditPage;
    MediaItem mediaItem;

    TextAreaItem titleItem = null;
    ArrayList<CustomTextAreaItem> items = new ArrayList<>();


    VBox content = new VBox();

    Map<String, String> metadata;


    OtherEditPage(MetadataEditPage metadataEditPage, MediaItem mediaItem){
        this.metadataEditPage = metadataEditPage;
        this.mediaItem = mediaItem;

        content.setSpacing(20);

        metadata = mediaItem.getMediaInformation();

        if(metadata != null) {
            titleItem = new TextAreaItem("Title", metadata.containsKey("title") && !metadata.get("title").trim().isEmpty() ? metadata.get("title") : "", content, true);


            for(Map.Entry<String, String> entry : metadata.entrySet()){
                if(!entry.getKey().equalsIgnoreCase("title") && !entry.getValue().trim().isEmpty()){
                    items.add(new CustomTextAreaItem(this, entry.getKey(), entry.getValue(), content));
                }
            }

        }

        metadataEditPage.textBox.getChildren().add(content);
    }


    public void saveMetadata(){

    }
}




