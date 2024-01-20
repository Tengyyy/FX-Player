package tengy.menu.Settings;

import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.beans.property.BooleanProperty;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;
import tengy.playbackSettings.PlaybackSettingsState;
import tengy.subtitles.SubtitlesState;

import static tengy.Utilities.keyboardFocusOff;


public class Toggle extends StackPane{

    SVGPath iconPath = new SVGPath();
    Region icon = new Region();
    Label label = new Label();
    MFXToggleButton toggleButton = new MFXToggleButton();

    Label stateLabel = new Label("Off");

    boolean pressed = false;
    boolean togglePressed = false;

    String offText = "Off";
    String onText = "On";


    Toggle(SettingsPage settingsPage, String iconContent, String text, BooleanProperty booleanProperty,SettingsSection section, int sectionFocusValue, int focusValue){
        this.getChildren().addAll(icon, label, stateLabel, toggleButton);
        this.setPadding(new Insets(20, 10, 20, 10));
        this.getStyleClass().addAll("highlightedSection", "settingsToggle");

        this.setOnMouseClicked(e -> {
            if(settingsPage.menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) settingsPage.menuController.subtitlesController.closeSubtitles();
            if(settingsPage.menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) settingsPage.menuController.playbackSettingsController.closeSettings();
            this.requestFocus();

            e.consume();
        });

        this.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                this.pseudoClassStateChanged(PseudoClass.getPseudoClass("focus"), true);
                settingsPage.focus.set(sectionFocusValue);
                section.setFocus(focusValue);
            }
            else {
                keyboardFocusOff(this);
                this.pseudoClassStateChanged(PseudoClass.getPseudoClass("focus"), false);
                pressed = false;
                settingsPage.focus.set(-1);
                section.setFocus(-1);
            }
        });

        this.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            this.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
            pressed = true;

            e.consume();
        });

        this.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            this.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);

            if(pressed) toggleButton.fire();

            pressed = false;

            e.consume();
        });

        iconPath.setContent(iconContent);

        icon.setShape(iconPath);
        icon.getStyleClass().add("menuIcon");
        icon.setPrefSize(17, 17);
        icon.setMaxSize(17, 17);
        icon.setMouseTransparent(true);
        StackPane.setMargin(icon, new Insets(0, 0, 0, 8));
        StackPane.setAlignment(icon, Pos.CENTER_LEFT);

        label.setText(text);
        label.getStyleClass().add("toggleText");
        StackPane.setMargin(label, new Insets(0, 0, 0, 35));
        StackPane.setAlignment(label, Pos.CENTER_LEFT);

        stateLabel.getStyleClass().add("toggleText");
        stateLabel.setPrefWidth(35);
        stateLabel.setMouseTransparent(true);
        StackPane.setAlignment(stateLabel, Pos.CENTER_RIGHT);
        StackPane.setMargin(stateLabel, new Insets(0, 50, 0, 0));


        toggleButton.selectedProperty().bindBidirectional(booleanProperty);
        toggleButton.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue) stateLabel.setText(onText);
            else stateLabel.setText(offText);
        });

        toggleButton.setRadius(10);
        toggleButton.setCursor(Cursor.HAND);
        toggleButton.setFocusTraversable(false);
        toggleButton.setOnAction(e -> {
            toggleButton.requestFocus();
            if(settingsPage.menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) settingsPage.menuController.subtitlesController.closeSubtitles();
            if(settingsPage.menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) settingsPage.menuController.playbackSettingsController.closeSettings();
        });

        toggleButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            this.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
            
            togglePressed = true;

            e.consume();
        });

        toggleButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {

            if(e.getCode() != KeyCode.SPACE) return;
            this.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);

            if(togglePressed) toggleButton.fire();

            togglePressed = false;

            e.consume();
        });

        toggleButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {

            this.pseudoClassStateChanged(PseudoClass.getPseudoClass("focus"), newValue);
            
            if(newValue){
                settingsPage.focus.set(sectionFocusValue);
                section.setFocus(focusValue);
            }
            else {
                settingsPage.focus.set(-1);
                section.setFocus(-1);

                togglePressed = false;
            }
        });

        StackPane.setAlignment(toggleButton, Pos.CENTER_RIGHT);
    }

    public void setText(String offText, String onText){
        this.offText = offText;
        this.onText = onText;

        if(toggleButton.isSelected()) stateLabel.setText(onText);
        else stateLabel.setText(offText);
    }
}
