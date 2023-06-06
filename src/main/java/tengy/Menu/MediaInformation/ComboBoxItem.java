package tengy.Menu.MediaInformation;


import tengy.Subtitles.SubtitlesState;
import tengy.PlaybackSettings.PlaybackSettingsState;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ComboBoxItem extends VBox{

    Label label;
    ComboBox<String> comboBox;

    MediaInformationPage mediaInformationPage;

    ComboBoxItem(MediaInformationPage mediaInformationPage, VBox parent, boolean add, String initialValue, String... values) {

        this.mediaInformationPage = mediaInformationPage;

        label = new Label("Media type");
        label.getStyleClass().add("metadataKey");
        VBox.setMargin(label, new Insets(0, 0, 3, 0));

        comboBox = new ComboBox<>();
        comboBox.setMinHeight(36);
        comboBox.setPrefHeight(36);
        comboBox.setMaxHeight(36);
        for (String value : values) {
            comboBox.getItems().add(value);
        }


        comboBox.focusedProperty().addListener((observableValue, aBoolean, newValue) -> {
            if(newValue){
                if(mediaInformationPage.menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) mediaInformationPage.menuController.subtitlesController.closeSubtitles();
                if(mediaInformationPage.menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) mediaInformationPage.menuController.playbackSettingsController.closeSettings();
            }
        });

        comboBox.setValue(initialValue);
        comboBox.valueProperty().addListener((observableValue, oldValue, newValue) -> mediaInformationPage.changesMade.set(true));

        this.getChildren().addAll(label, comboBox);
        if(add) parent.getChildren().add(this);
    }
}
