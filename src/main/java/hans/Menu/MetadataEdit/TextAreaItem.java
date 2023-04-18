package hans.Menu.MetadataEdit;

import hans.Subtitles.SubtitlesState;
import hans.Menu.ExpandableTextArea;
import hans.PlaybackSettings.PlaybackSettingsState;
import javafx.geometry.Insets;
import javafx.scene.control.Label;

import javafx.scene.layout.VBox;

public class TextAreaItem extends VBox{
    Label label;
    ExpandableTextArea textArea;

    MetadataEditPage metadataEditPage;

    TextAreaItem(MetadataEditPage metadataEditPage, String key, String value, VBox parent, boolean add){

        this.metadataEditPage = metadataEditPage;

        label = new Label(key);
        VBox.setMargin(label, new Insets(0, 0, 3, 0));
        label.getStyleClass().add("metadataKey");

        textArea = new ExpandableTextArea();
        textArea.initializeText(value);
        textArea.textProperty().addListener((observableValue, s, t1) -> {
            metadataEditPage.mediaItem.changesMade.set(true);
        });
        textArea.disableProperty().bind(metadataEditPage.fieldsDisabledProperty);

        textArea.focusedProperty().addListener((observableValue, aBoolean, newValue) -> {
            if(newValue){
                if(metadataEditPage.menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) metadataEditPage.menuController.subtitlesController.closeSubtitles();
                if(metadataEditPage.menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) metadataEditPage.menuController.playbackSettingsController.closeSettings();
            }

        });


        this.getChildren().addAll(label, textArea);
        if(add) parent.getChildren().add(this);
    }
}
