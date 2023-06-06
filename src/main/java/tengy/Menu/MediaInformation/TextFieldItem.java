package tengy.Menu.MediaInformation;

import tengy.Subtitles.SubtitlesState;
import tengy.PlaybackSettings.PlaybackSettingsState;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class TextFieldItem extends VBox{
    Label label;
    TextField textField;

    MediaInformationPage mediaInformationPage;

    TextFieldItem(MediaInformationPage mediaInformationPage, String key, String value, VBox parent, boolean add){

        this.mediaInformationPage = mediaInformationPage;

        label = new Label(key);
        VBox.setMargin(label, new Insets(0, 0, 3, 0));
        label.getStyleClass().add("metadataKey");

        textField = new TextField(value);
        textField.getStyleClass().add("customTextField");
        textField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            mediaInformationPage.changesMade.set(true);
        });
        textField.setPrefHeight(36);
        textField.setMinHeight(36);
        textField.setMaxHeight(36);

        textField.focusedProperty().addListener((observableValue, aBoolean, newValue) -> {
            if(newValue){
                if(mediaInformationPage.menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) mediaInformationPage.menuController.subtitlesController.closeSubtitles();
                if(mediaInformationPage.menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) mediaInformationPage.menuController.playbackSettingsController.closeSettings();
            }

        });

        this.getChildren().addAll(label, textField);
        if(add) parent.getChildren().add(this);
    }
}