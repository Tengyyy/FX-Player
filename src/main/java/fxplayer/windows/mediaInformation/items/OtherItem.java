package fxplayer.windows.mediaInformation.items;

import javafx.scene.control.Button;
import fxplayer.SVG;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import fxplayer.windows.mediaInformation.components.CustomTextAreaItem;
import fxplayer.windows.mediaInformation.components.TextFieldItem;
import fxplayer.windows.mediaInformation.MediaInformationWindow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OtherItem implements MediaInformationItem {

    public MediaInformationWindow mediaInformationWindow;

    TextFieldItem titleItem = null;
    public ArrayList<CustomTextAreaItem> items = new ArrayList<>();


    public VBox content = new VBox();


    public Button addButton = new Button();
    Region addIcon = new Region();
    SVGPath addSVG = new SVGPath();

    Map<String, String> metadata;


    public OtherItem(MediaInformationWindow mediaInformationWindow, Map<String, String> metadata){
        this.mediaInformationWindow = mediaInformationWindow;
        this.metadata = metadata;

        addSVG.setContent(SVG.PLUS.getContent());

        content.setSpacing(20);


        addButton.setText("Add key");
        addButton.getStyleClass().add("menuButton");
        addButton.setCursor(Cursor.HAND);
        VBox.setMargin(addButton, new Insets(5, 0, 15, 5));

        addIcon.setShape(addSVG);
        addIcon.getStyleClass().add("menuIcon");
        addIcon.setPrefSize(18, 18);
        addIcon.setMaxSize(18, 18);

        addButton.setGraphic(addIcon);
        addButton.setOnAction(e -> {
            CustomTextAreaItem item = new CustomTextAreaItem(this, "", "");
            items.add(item);
            item.keyField.requestFocus();
            mediaInformationWindow.scrollPane.setVvalue(1.0);
            mediaInformationWindow.changesMade.set(true);
        });


        content.getChildren().add(addButton);

        if(metadata != null) {
            titleItem = new TextFieldItem(mediaInformationWindow, "Title", metadata.containsKey("title") && !metadata.get("title").trim().isEmpty() ? metadata.get("title") : "", content, false);

            content.getChildren().add(0, titleItem);


            for(Map.Entry<String, String> entry : metadata.entrySet()){
                if(!entry.getKey().equalsIgnoreCase("title") && !entry.getKey().equalsIgnoreCase("encoder") && !entry.getValue().trim().isEmpty()){
                    items.add(new CustomTextAreaItem(this, entry.getKey(), entry.getValue()));
                }
            }

        }

        mediaInformationWindow.textBox.getChildren().add(content);
    }


    @Override
    public Map<String, String> createMetadataMap(){
        Map<String, String> mediaInformation = new HashMap<>();

        if(!titleItem.textField.getText().isBlank()){
            mediaInformation.put("title", titleItem.textField.getText());
        }
        for(CustomTextAreaItem item : items){
            if(!item.keyField.getText().isBlank() && !item.textArea.getText().isBlank()){
                mediaInformation.put(item.keyField.getText(), item.textArea.getText());
            }
        }

        return mediaInformation;
    }
}




