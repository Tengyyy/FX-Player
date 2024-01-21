package fxplayer.focuscontrol;

import fxplayer.App;
import javafx.css.PseudoClass;
import javafx.scene.control.Control;

public abstract class FocusableNode implements FocusTraversalComponent {
    Control control;
    FocusSubController parent;

    protected void init(Control control, FocusSubController parent) {
        this.control = control;
        this.parent = parent;

        control.setFocusTraversable(false);
        control.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                if(App.focusController.focusTarget != null){
                    // this node gained focus through traversal and the address is known
                    // let FocusController handle setting focus indexes (address)
                    App.focusController.traverseFocus();
                }
                else {
                    // this node gained focus outside traversal system (mouse click or otherwise)
                    // find and set indexes recursively
                    parent.setFocus(this);
                }
            }
            else {
                keyboardFocusOff();
                resetFocus();
            }
        });
    }

    public void keyboardFocusOn() {
        control.requestFocus();
        control.pseudoClassStateChanged(PseudoClass.getPseudoClass("keyboardFocused"), true);
    }

    private void keyboardFocusOff() {
        control.pseudoClassStateChanged(PseudoClass.getPseudoClass("keyboardFocused"), false);
        control.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
    }

    public abstract boolean isFocusable();

    @Override
    public void resetFocus(){
        parent.resetFocus();
    }
}
