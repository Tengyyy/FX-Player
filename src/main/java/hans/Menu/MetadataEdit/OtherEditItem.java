package hans.Menu.MetadataEdit;

import com.jfoenix.controls.JFXButton;
import hans.App;
import hans.MediaItems.MediaItem;
import hans.SVG;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OtherEditItem implements MetadataEditItem{

    MetadataEditPage metadataEditPage;
    MediaItem mediaItem;

    TextAreaItem titleItem = null;
    ArrayList<CustomTextAreaItem> items = new ArrayList<>();


    VBox content = new VBox();


    JFXButton addButton = new JFXButton();
    Region addIcon = new Region();
    SVGPath addSVG = new SVGPath();

    Map<String, String> metadata;


    OtherEditItem(MetadataEditPage metadataEditPage, MediaItem mediaItem){
        this.metadataEditPage = metadataEditPage;
        this.mediaItem = mediaItem;

        addSVG.setContent(App.svgMap.get(SVG.PLUS));

        content.setSpacing(20);

        metadata = mediaItem.getMediaInformation();

        addButton.setText("Add key");
        addButton.getStyleClass().add("secondaryButton");
        addButton.setCursor(Cursor.HAND);
        VBox.setMargin(addButton, new Insets(5, 0, 15, 5));
        addButton.setPadding(new Insets(8, 10, 8, 10));
        addButton.setRipplerFill(Color.WHITE);

        addIcon.setShape(addSVG);
        addIcon.getStyleClass().add("menuIcon");
        addIcon.setPrefSize(18, 18);
        addIcon.setMaxSize(18, 18);

        addButton.setGraphic(addIcon);
        addButton.setOnAction(e -> {
            CustomTextAreaItem item = new CustomTextAreaItem(this, "", "");
            items.add(item);
            item.keyField.requestFocus();
            metadataEditPage.menuController.metadataEditScroll.setVvalue(1.0);
            metadataEditPage.changesMade.set(true);
        });

        content.getChildren().add(addButton);

        if(metadata != null) {
            titleItem = new TextAreaItem(metadataEditPage, "Title", metadata.containsKey("title") && !metadata.get("title").trim().isEmpty() ? metadata.get("title") : "", content, false);
            content.getChildren().add(0, titleItem);


            for(Map.Entry<String, String> entry : metadata.entrySet()){
                if(!entry.getKey().equalsIgnoreCase("title") && !entry.getValue().trim().isEmpty()){
                    items.add(new CustomTextAreaItem(this, entry.getKey(), entry.getValue()));
                }
            }

        }

        metadataEditPage.textBox.getChildren().add(content);
    }


    @Override
    public Map<String, String> saveMetadata(){
        Map<String, String> mediaInformation = new HashMap<>();

        for(CustomTextAreaItem item : items){
            if(!item.keyField.getText().isBlank() && !item.textArea.getText().isBlank()){
                mediaInformation.put(item.keyField.getText(), item.textArea.getText());
            }
        }

        return mediaInformation;
    }
}




