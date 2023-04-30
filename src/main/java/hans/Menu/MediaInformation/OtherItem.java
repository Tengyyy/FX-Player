package hans.Menu.MediaInformation;

import com.jfoenix.controls.JFXButton;
import hans.App;
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

public class OtherItem implements MediaInformationItem {

    MediaInformationPage mediaInformationPage;

    TextAreaItem titleItem = null;
    ArrayList<CustomTextAreaItem> items = new ArrayList<>();


    VBox content = new VBox();


    JFXButton addButton = new JFXButton();
    Region addIcon = new Region();
    SVGPath addSVG = new SVGPath();

    Map<String, String> metadata;


    OtherItem(MediaInformationPage mediaInformationPage, Map<String, String> metadata){
        this.mediaInformationPage = mediaInformationPage;
        this.metadata = metadata;

        addSVG.setContent(SVG.PLUS.getContent());

        content.setSpacing(20);


        addButton.setText("Add key");
        addButton.getStyleClass().add("menuButton");
        addButton.setCursor(Cursor.HAND);
        VBox.setMargin(addButton, new Insets(5, 0, 15, 5));
        addButton.setRipplerFill(Color.TRANSPARENT);

        addIcon.setShape(addSVG);
        addIcon.getStyleClass().add("menuIcon");
        addIcon.setPrefSize(18, 18);
        addIcon.setMaxSize(18, 18);

        addButton.setGraphic(addIcon);
        addButton.setOnAction(e -> {
            CustomTextAreaItem item = new CustomTextAreaItem(this, "", "");
            items.add(item);
            item.keyField.requestFocus();
            mediaInformationPage.mediaInformationScroll.setVvalue(1.0);
            mediaInformationPage.mediaItem.changesMade.set(true);
        });
        addButton.disableProperty().bind(mediaInformationPage.fieldsDisabledProperty);


        content.getChildren().add(addButton);

        if(metadata != null) {
            titleItem = new TextAreaItem(mediaInformationPage, "Title", metadata.containsKey("title") && !metadata.get("title").trim().isEmpty() ? metadata.get("title") : "", content, false);

            content.getChildren().add(0, titleItem);


            for(Map.Entry<String, String> entry : metadata.entrySet()){
                if(!entry.getKey().equalsIgnoreCase("title") && !entry.getKey().equalsIgnoreCase("encoder") && !entry.getValue().trim().isEmpty()){
                    items.add(new CustomTextAreaItem(this, entry.getKey(), entry.getValue()));
                }
            }

        }

        mediaInformationPage.textBox.getChildren().add(content);
    }


    @Override
    public Map<String, String> createMetadataMap(){
        Map<String, String> mediaInformation = new HashMap<>();

        if(!titleItem.textArea.getText().isBlank()){
            mediaInformation.put("title", titleItem.textArea.getText());
        }
        for(CustomTextAreaItem item : items){
            if(!item.keyField.getText().isBlank() && !item.textArea.getText().isBlank()){
                mediaInformation.put(item.keyField.getText(), item.textArea.getText());
            }
        }

        return mediaInformation;
    }
}




