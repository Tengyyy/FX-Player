package hans.Menu.MetadataEdit;

import hans.Subtitles.SubtitlesState;
import hans.PlaybackSettings.PlaybackSettingsState;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
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
            if(newValue){
                if(metadataEditPage.menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) metadataEditPage.menuController.subtitlesController.closeSubtitles();
                if(metadataEditPage.menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) metadataEditPage.menuController.playbackSettingsController.closeSettings();
            }

        });

        this.getChildren().addAll(label, textField);
        if(add) parent.getChildren().add(this);
    }
}