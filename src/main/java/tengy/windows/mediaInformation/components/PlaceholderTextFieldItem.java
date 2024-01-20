package tengy.windows.mediaInformation.components;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import tengy.skins.ClearableTextFieldSkin;
import tengy.windows.mediaInformation.MediaInformationWindow;

public class PlaceholderTextFieldItem extends VBox implements Component{
    Label label;
    TextField textField;

    MediaInformationWindow mediaInformationWindow;

    public PlaceholderTextFieldItem(MediaInformationWindow mediaInformationWindow, String key, String value, String placeholder, VBox parent, boolean add){

        this.mediaInformationWindow = mediaInformationWindow;

        label = new Label(key);
        VBox.setMargin(label, new Insets(0, 0, 3, 0));
        label.getStyleClass().add("metadataKey");

        textField = new TextField(value);
        textField.setSkin(new ClearableTextFieldSkin(textField));
        textField.getStyleClass().add("customTextField");
        textField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            mediaInformationWindow.changesMade.set(true);
            mediaInformationWindow.saveAllowed.set(true);
        });
        textField.setPrefHeight(36);
        textField.setMinHeight(36);
        textField.setMaxHeight(36);
        textField.setPromptText(placeholder);
        textField.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);");

        this.getChildren().addAll(label, textField);
        if(add) parent.getChildren().add(this);
    }

    @Override
    public String getKey() {
        return null;
    }

    @Override
    public String getValue() {
        return null;
    }
}