package fxplayer;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.css.PseudoClass;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;


public class CustomMenuItem extends HBox {

    Label text = new Label();

    boolean pressed = false;

    public String value;

    public BooleanProperty selected = new SimpleBooleanProperty(false);

    public CustomMenuItem(String value, int width){
        this.value = value;
        this.getStyleClass().add("custom-combo-menu-item");
        this.setFocusTraversable(true);

        this.setPrefWidth(width);


        this.getChildren().add(text);

        text.setMouseTransparent(true);
        text.setText(value);

        this.setOnMouseClicked(e -> {
            this.requestFocus();

            if(selected.get()) unselect();
            else select();
        });

        this.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            this.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
            pressed = true;

            e.consume();
        });

        this.addEventFilter(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;

            this.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);

            if(pressed){
                if(selected.get()) unselect();
                else select();
                pressed = false;
            }

            e.consume();
        });
    }

    public void select(){
        this.pseudoClassStateChanged(PseudoClass.getPseudoClass("selected"), true);
        selected.set(true);
    }

    public void unselect(){
        this.pseudoClassStateChanged(PseudoClass.getPseudoClass("selected"), false);
        selected.set(false);
    }
}
