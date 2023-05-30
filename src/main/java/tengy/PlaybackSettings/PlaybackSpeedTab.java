package tengy.PlaybackSettings;

import javafx.css.PseudoClass;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import tengy.SVG;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;


public class PlaybackSpeedTab extends HBox {

    PlaybackSpeedController playbackSpeedController;

    PlaybackSpeedPane playbackSpeedPane;

    StackPane checkIconPane = new StackPane();
    Region checkIcon = new Region();

    Label valueLabel = new Label();

    SVGPath checkSVG = new SVGPath();

    double speedValue;

    boolean pressed = false;

    int focusValue;


    PlaybackSpeedTab(PlaybackSpeedController playbackSpeedController, PlaybackSpeedPane playbackSpeedPane, boolean isCustom){
        this.playbackSpeedController = playbackSpeedController;
        this.playbackSpeedPane = playbackSpeedPane;

        checkSVG.setContent(SVG.CHECK.getContent());

        this.setPrefSize(250, 35);
        this.setMaxSize(250, 35);
        this.setPadding(new Insets(0, 10, 0, 10));
        this.getStyleClass().add("settingsPaneTab");
        this.setCursor(Cursor.HAND);
        this.setFocusTraversable(false);


        checkIconPane.setMinSize(30, 35);
        checkIconPane.setPrefSize(30, 35);
        checkIconPane.setMaxSize(30, 35);
        checkIconPane.setPadding(new Insets(0, 5, 0, 0));

        checkIcon.setMinSize(14, 11);
        checkIcon.setPrefSize(14, 11);
        checkIcon.setMaxSize(14, 11);
        checkIcon.setShape(checkSVG);
        checkIcon.getStyleClass().add("settingsPaneIcon");


        valueLabel.setFont(new Font(15));
        valueLabel.setPrefHeight(35);
        valueLabel.setPrefWidth(200);

        if(isCustom) speedValue = playbackSpeedController.customSpeedPane.lastCustomValue;
        else speedValue = (double) (playbackSpeedPane.speedTabs.size() + 1) / 4;

        if(isCustom) valueLabel.setText("Custom (" + playbackSpeedController.df.format(speedValue) + ")");
        else if(playbackSpeedPane.speedTabs.size() == 3) valueLabel.setText("Normal");
        else valueLabel.setText(String.valueOf(speedValue));

        valueLabel.getStyleClass().add("settingsPaneText");

        checkIconPane.getChildren().add(checkIcon);

        this.getChildren().addAll(checkIconPane, valueLabel);

        if(playbackSpeedPane.speedTabs.size() != 3){
            checkIcon.setVisible(false);
        }

        this.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue) playbackSpeedPane.focus.set(focusValue);
            else {
                playbackSpeedPane.focus.set(-1);
                pressed = false;
                this.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
            }
        });

        this.setOnMouseClicked((e) -> action());

        this.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;

            pressed = true;
            this.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);

            e.consume();
        });

        this.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;

            if(pressed){
                action();
            }

            pressed = false;
            this.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);

            e.consume();
        });

        if(isCustom){
            for(PlaybackSpeedTab playbackSpeedTab : playbackSpeedPane.speedTabs){
                playbackSpeedTab.focusValue+=1;
            }
            this.focusValue = 2;
            playbackSpeedPane.focusNodes.add(2, this);
            playbackSpeedPane.speedTabs.add(0, this);
            playbackSpeedPane.customSpeedTab = this;
            playbackSpeedPane.playbackSpeedBox.setPrefHeight(playbackSpeedPane.playbackSpeedBox.getPrefHeight() + 35);
            playbackSpeedPane.playbackSpeedBox.setMaxHeight(playbackSpeedPane.playbackSpeedBox.getMaxHeight() + 35);
            playbackSpeedPane.scrollPane.setPrefHeight(playbackSpeedPane.scrollPane.getPrefHeight() + 35);
            playbackSpeedPane.scrollPane.setMaxHeight(playbackSpeedPane.scrollPane.getMaxHeight() + 35);
            playbackSpeedPane.playbackSpeedBox.getChildren().add(1, this);
        }
        else {
            this.focusValue = playbackSpeedPane.speedTabs.size() + 2;
            playbackSpeedPane.focusNodes.add(this);
            playbackSpeedPane.speedTabs.add(this);
            playbackSpeedPane.playbackSpeedBox.getChildren().add(this);
        }

    }


    public void updateValue(double newValue){
        speedValue = newValue;
        valueLabel.setText("Custom (" + playbackSpeedController.df.format(newValue) + ")");
    }

    private void action(){
        for(PlaybackSpeedTab playbackSpeedTab : playbackSpeedPane.speedTabs){
            playbackSpeedTab.checkIcon.setVisible(false);
        }

        this.checkIcon.setVisible(true);

        playbackSpeedController.setSpeed(this.speedValue);
    }
}
