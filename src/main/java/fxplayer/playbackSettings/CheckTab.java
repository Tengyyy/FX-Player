package fxplayer.playbackSettings;

import javafx.beans.property.IntegerProperty;
import javafx.css.PseudoClass;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import fxplayer.SVG;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;

public class CheckTab extends HBox {

    StackPane checkIconPane = new StackPane();
    public Region checkIcon = new Region();
    SVGPath checkSVG = new SVGPath();

    Label valueLabel = new Label();

    boolean pressed = false;

    public CheckTab(boolean selected, String value, IntegerProperty focusProperty, int focusValue, Runnable action){

        checkSVG.setContent(SVG.CHECK.getContent());

        this.setPrefSize(200, 35);
        this.setMaxSize(200, 35);

        this.setPadding(new Insets(0, 10, 0, 10));
        this.getStyleClass().add("settingsPaneTab");
        this.setCursor(Cursor.HAND);
        this.setFocusTraversable(false);

        this.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue) focusProperty.set(focusValue);
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

            if(pressed){
                action.run();
            }

            pressed = false;
            this.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);

            e.consume();
        });


        checkIconPane.setMinSize(30, 35);
        checkIconPane.setPrefSize(30, 35);
        checkIconPane.setMaxSize(30, 35);
        checkIconPane.setPadding(new Insets(0, 5, 0, 0));
        checkIconPane.getChildren().add(checkIcon);

        checkIcon.setMinSize(14, 11);
        checkIcon.setPrefSize(14, 11);
        checkIcon.setMaxSize(14, 11);
        checkIcon.setShape(checkSVG);
        checkIcon.getStyleClass().add("settingsPaneIcon");
        checkIcon.setVisible(selected);

        valueLabel.setFont(new Font(15));
        valueLabel.setPrefHeight(35);
        valueLabel.setPrefWidth(140);
        valueLabel.setText(value);
        valueLabel.getStyleClass().add("settingsPaneText");

        this.getChildren().addAll(checkIconPane, valueLabel);
    }
}
