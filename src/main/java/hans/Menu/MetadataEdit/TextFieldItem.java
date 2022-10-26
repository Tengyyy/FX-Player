package hans.Menu.MetadataEdit;

import hans.Menu.ExpandableTextArea;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class TextFieldItem extends VBox{
    Label label;
    TextField textField;


    TextFieldItem(String key, String value, VBox parent, boolean add){
        label = new Label(key);
        VBox.setMargin(label, new Insets(0, 0, 3, 0));
        label.getStyleClass().add("metadataKey");

        textField = new TextField(value);
        textField.setPrefHeight(36);
        textField.setMinHeight(36);
        textField.setMaxHeight(36);

        this.getChildren().addAll(label, textField);
        if(add) parent.getChildren().add(this);
    }
}