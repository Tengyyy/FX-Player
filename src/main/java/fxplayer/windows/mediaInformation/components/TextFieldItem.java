package fxplayer.windows.mediaInformation.components;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import fxplayer.skins.ClearableTextFieldSkin;
import fxplayer.windows.mediaInformation.MediaInformationWindow;

public class TextFieldItem extends VBox{
    Label label;
    public TextField textField;

    MediaInformationWindow mediaInformationWindow;

    public TextFieldItem(MediaInformationWindow mediaInformationWindow, String key, String value, VBox parent, boolean add){

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

        this.getChildren().addAll(label, textField);
        if(add) parent.getChildren().add(this);
    }
}