package fxplayer.playbackSettings;

import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;


public class PlaybackOptionsTab extends HBox {

    PlaybackOptionsController playbackOptionsController;

    Label titleLabel = new Label();
    public MFXToggleButton toggle = new MFXToggleButton();

    boolean pressed = false;

    PlaybackOptionsTab(PlaybackOptionsController playbackOptionsController, String titleText, int focusValue){

        this.playbackOptionsController = playbackOptionsController;

        this.setMinSize(235, 35);
        this.setPrefSize(235, 35);
        this.setMaxSize(235, 35);
        this.getStyleClass().add("settingsPaneTab");
        this.setPadding(new Insets(0, 10, 0, 10));
        this.setAlignment(Pos.CENTER_LEFT);
        this.getChildren().addAll(titleLabel, toggle);
        this.setCursor(Cursor.HAND);

        this.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue) playbackOptionsController.focus.set(focusValue);
            else {
                playbackOptionsController.focus.set(-1);
                pressed = false;
                this.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
            }
        });

        this.setOnMouseClicked(e -> {
            toggle.fire();
        });

        this.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;

            pressed = true;
            this.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);

            e.consume();
        });

        this.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;

            if(pressed){
                toggle.fire();
            }

            pressed = false;
            this.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);

            e.consume();
        });

        this.setOnMouseEntered(e -> toggle.pseudoClassStateChanged(PseudoClass.getPseudoClass("hover"), true));
        this.setOnMouseExited(e -> toggle.pseudoClassStateChanged(PseudoClass.getPseudoClass("hover"), false));


        titleLabel.setMinSize(165, 35);
        titleLabel.setPrefSize(165, 35);
        titleLabel.setMaxSize(165, 35);
        titleLabel.getStyleClass().add("settingsPaneText");
        titleLabel.setText(titleText);

        toggle.setMinSize(50, 35);
        toggle.setPrefSize(50, 35);
        toggle.setMaxSize(50, 35);
        toggle.setLength(38);
        toggle.setRadius(10);
        toggle.setFocusTraversable(false);
        toggle.setMouseTransparent(true);

        playbackOptionsController.playbackOptionsBox.getChildren().add(this);

    }

}
