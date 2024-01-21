package fxplayer.controls;

import fxplayer.focuscontrol.FocusableNode;
import javafx.css.PseudoClass;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;

abstract class ButtonBase extends FocusableNode {
    StackPane container;
    Region icon;
    SVGPath svgPath;
    Button button;

    protected ButtonBase(double width, double height, String text) {
        container = new StackPane();
        container.setPrefSize(width, height);

        button = new Button();
        button.setPrefSize(width, height);
        button.setMaxSize(width, height);
        button.setText(text);

        container.getChildren().add(button);

        button.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() != KeyCode.SPACE) return;
            button.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        button.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if (e.getCode() != KeyCode.SPACE) return;
            button.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });

//        closeButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
//            if (newValue) {
//                focus.set(0);
//            } else {
//                keyboardFocusOff(closeButton);
//                focus.set(-1);
//            }
//        });

        super.init(button, null);
    }

    public ButtonBase addTooltip(String text) {
        //TODO: implement
        return this;
    }

    public ButtonBase addIcon(double iconWidth, double iconHeight, String svg) {
        svgPath = new SVGPath();
        svgPath.setContent(svg);

        icon = new Region();
        icon.setShape(svgPath);
        icon.setMinSize(iconWidth, iconHeight);
        icon.setPrefSize(iconWidth, iconHeight);
        icon.setMaxSize(iconWidth, iconHeight);
        icon.setMouseTransparent(true);
        icon.getStyleClass().add("graphic");

        button.setGraphic(icon);

        return this;
    }

    @Override
    public boolean isFocusable(){
        return !button.isDisable();
    }
}
