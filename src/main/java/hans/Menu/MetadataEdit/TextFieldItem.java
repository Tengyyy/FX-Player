package hans.Menu.MetadataEdit;

import hans.Captions.CaptionsState;
import hans.Menu.ExpandableTextArea;
import hans.Settings.SettingsState;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class TextFieldItem extends VBox{
    Label label;
    TextField textField;

    MetadataEditPage metadataEditPage;

    TextFieldItem(MetadataEditPage metadataEditPage, String key, String value, VBox parent, boolean add){

        this.metadataEditPage = metadataEditPage;

        label = new Label(key);
        VBox.setMargin(label, new Insets(0, 0, 3, 0));
        label.getStyleClass().add("metadataKey");

        textField = new TextField(value);
        textField.getStyleClass().add("customTextField");
        textField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            metadataEditPage.mediaItem.changesMade.set(true);
        });
        textField.disableProperty().bind(metadataEditPage.fieldsDisabledProperty);
        textField.setPrefHeight(36);
        textField.setMinHeight(36);
        textField.setMaxHeight(36);

        textField.focusedProperty().addListener((observableValue, aBoolean, newValue) -> {
            if(newValue && metadataEditPage.menuController.extended){
                if(metadataEditPage.menuController.captionsController.captionsState != CaptionsState.CLOSED) metadataEditPage.menuController.captionsController.closeCaptions();
                if(metadataEditPage.menuController.settingsController.settingsState != SettingsState.CLOSED) metadataEditPage.menuController.settingsController.closeSettings();
            }

        });

        this.getChildren().addAll(label, textField);
        if(add) parent.getChildren().add(this);
    }
}