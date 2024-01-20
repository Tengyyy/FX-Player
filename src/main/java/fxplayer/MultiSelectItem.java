package fxplayer;

import javafx.css.PseudoClass;
import javafx.scene.control.CheckBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;

public class MultiSelectItem extends HBox {
    
    CheckBox checkBox = new CheckBox();

    String value;

    boolean pressed = false;
    
    MultiSelectItem(MultiSelectButton multiSelectButton, String value){
        
        this.value = value;
        this.getStyleClass().add("multi-select-item");
        
        checkBox.setText(value);
        checkBox.setMouseTransparent(true);
        checkBox.setFocusTraversable(false);

        checkBox.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                multiSelectButton.selectedItems.add(value);
                select();
            }
            else {
                multiSelectButton.selectedItems.remove(value);
                unselect();
            }
        });
        
        this.getChildren().add(checkBox);

        this.setOnMouseClicked(e -> {
            checkBox.fire();
            this.requestFocus();
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
                checkBox.fire();
                pressed = false;
            }

            e.consume();
        });

        this.setFocusTraversable(true);
    }
    
    
    public void select(){
        this.pseudoClassStateChanged(PseudoClass.getPseudoClass("selected"), true);
    }

    public void unselect(){
        this.pseudoClassStateChanged(PseudoClass.getPseudoClass("selected"), false);
    }
    

}
