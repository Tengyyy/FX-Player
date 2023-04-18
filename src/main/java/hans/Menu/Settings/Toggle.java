package hans.Menu.Settings;

import hans.Captions.CaptionsState;
import hans.Settings.SettingsState;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;


public class Toggle extends StackPane{

    Label label = new Label();
    MFXToggleButton toggleButton = new MFXToggleButton();

    Toggle(SettingsPage settingsPage, String text, BooleanProperty booleanProperty){
        this.getChildren().addAll(label, toggleButton);
        this.setPadding(new Insets(8, 10, 8, 10));
        this.getStyleClass().add("highlightedSection");
        this.setOnMouseClicked(e -> {
            if(settingsPage.menuController.captionsController.captionsState != CaptionsState.CLOSED) settingsPage.menuController.captionsController.closeCaptions();
            if(settingsPage.menuController.settingsController.settingsState != SettingsState.CLOSED) settingsPage.menuController.settingsController.closeSettings();
            toggleButton.fire();
        });

        label.setText(text);
        label.getStyleClass().add("toggleText");
        StackPane.setAlignment(label, Pos.CENTER_LEFT);

        toggleButton.selectedProperty().bindBidirectional(booleanProperty);
        toggleButton.setRadius(10);
        toggleButton.setOnAction(e -> {
            if(settingsPage.menuController.captionsController.captionsState != CaptionsState.CLOSED) settingsPage.menuController.captionsController.closeCaptions();
            if(settingsPage.menuController.settingsController.settingsState != SettingsState.CLOSED) settingsPage.menuController.settingsController.closeSettings();
        });
        StackPane.setAlignment(toggleButton, Pos.CENTER_RIGHT);
    }
}
