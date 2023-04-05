package hans.Menu.MetadataEdit;


import hans.Captions.CaptionsState;
import hans.Settings.SettingsState;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ComboBoxItem extends VBox{

    Label label;
    ComboBox<String> comboBox;

    MetadataEditPage metadataEditPage;

    ComboBoxItem(MetadataEditPage metadataEditPage, VBox parent, boolean add, String initialValue, String... values) {

        this.metadataEditPage = metadataEditPage;

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

        comboBox.disableProperty().bind(metadataEditPage.fieldsDisabledProperty);

        comboBox.focusedProperty().addListener((observableValue, aBoolean, newValue) -> {
            if(newValue && metadataEditPage.menuController.extended){
                if(metadataEditPage.menuController.captionsController.captionsState != CaptionsState.CLOSED) metadataEditPage.menuController.captionsController.closeCaptions();
                if(metadataEditPage.menuController.settingsController.settingsState != SettingsState.CLOSED) metadataEditPage.menuController.settingsController.closeSettings();
            }
        });

        comboBox.setValue(initialValue);
        comboBox.valueProperty().addListener((observableValue, oldValue, newValue) -> metadataEditPage.mediaItem.changesMade.set(true));

        this.getChildren().addAll(label, comboBox);
        if(add) parent.getChildren().add(this);
    }
}
