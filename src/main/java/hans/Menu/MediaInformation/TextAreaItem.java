package hans.Menu.MediaInformation;

import hans.Subtitles.SubtitlesState;
import hans.Menu.ExpandableTextArea;
import hans.PlaybackSettings.PlaybackSettingsState;
import javafx.geometry.Insets;
import javafx.scene.control.Label;

import javafx.scene.layout.VBox;

public class TextAreaItem extends VBox{
    Label label;
    ExpandableTextArea textArea;

    MediaInformationPage mediaInformationPage;

    TextAreaItem(MediaInformationPage mediaInformationPage, String key, String value, VBox parent, boolean add){

        this.mediaInformationPage = mediaInformationPage;

        label = new Label(key);
        VBox.setMargin(label, new Insets(0, 0, 3, 0));
        label.getStyleClass().add("metadataKey");

        textArea = new ExpandableTextArea();
        textArea.initializeText(value);
        textArea.textProperty().addListener((observableValue, s, t1) -> {
            mediaInformationPage.mediaItem.changesMade.set(true);
        });
        textArea.disableProperty().bind(mediaInformationPage.fieldsDisabledProperty);

        textArea.focusedProperty().addListener((observableValue, aBoolean, newValue) -> {
            if(newValue){
                if(mediaInformationPage.menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) mediaInformationPage.menuController.subtitlesController.closeSubtitles();
                if(mediaInformationPage.menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) mediaInformationPage.menuController.playbackSettingsController.closeSettings();
            }

        });


        this.getChildren().addAll(label, textArea);
        if(add) parent.getChildren().add(this);
    }
}
