package tengy.playbackSettings;

import javafx.beans.property.IntegerProperty;
import javafx.css.PseudoClass;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;

import java.util.List;

public class SettingsTab extends HBox {

    boolean pressed;

    public SettingsTab(IntegerProperty focusProperty, List<Node> focusNodes, Runnable action){
        this.getStyleClass().add("settingsPaneTab");
        this.setFocusTraversable(false);
        this.setCursor(Cursor.HAND);

        this.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focusProperty.set(focusNodes.indexOf(this));
            }
            else {
                focusProperty.set(-1);
                pressed = false;
                this.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
            }
        });

        this.setOnMouseClicked(e -> action.run());
        this.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;

            pressed = true;
            this.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);

            e.consume();
        });

        this.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;

            if(pressed) action.run();

            pressed = false;
            this.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);

            e.consume();
        });
    }
}
