package fxplayer.subtitles;

import javafx.css.PseudoClass;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import fxplayer.SVG;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.TextAlignment;

public class SubtitlesOptionsTab extends StackPane {

    SubtitlesController subtitlesController;
    SubtitlesOptionsPane subtitlesOptionsPane;

    HBox textContainer = new HBox();

    StackPane arrowPane = new StackPane();
    Label mainText = new Label();
    Label subText = new Label();

    Region arrowIcon = new Region();
    SVGPath arrowSVG = new SVGPath();

    boolean pressed = false;


    SubtitlesOptionsTab(SubtitlesOptionsPane subtitlesOptionsPane, SubtitlesController subtitlesController, boolean requiresSubText, boolean requiresArrow, String mainTextValue, String subTextValue, int focusValue, Runnable action){
        this.subtitlesController = subtitlesController;
        this.subtitlesOptionsPane = subtitlesOptionsPane;

        arrowSVG.setContent(SVG.CHEVRON_RIGHT.getContent());

        this.setPrefSize(270, 35);
        this.setMaxSize(270, 35);
        this.setPadding(new Insets(0, 5, 0, 10));
        this.getStyleClass().add("settingsPaneTab");
        this.setCursor(Cursor.HAND);
        this.getChildren().add(textContainer);
        this.setFocusTraversable(false);
        this.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                subtitlesOptionsPane.focus.set(focusValue);
            }
            else {
                subtitlesOptionsPane.focus.set(-1);
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

        StackPane.setAlignment(textContainer, Pos.CENTER_LEFT);
        textContainer.getChildren().add(mainText);

        if(requiresSubText) textContainer.getChildren().add(subText);
        if(requiresArrow) this.getChildren().add(arrowPane);



        mainText.setText(mainTextValue);
        mainText.getStyleClass().add("settingsPaneText");
        mainText.setPrefSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
        mainText.setMaxSize(235,35);

        if(requiresSubText){
            subText.setText(subTextValue);
            subText.setPrefHeight(35);
            Platform.runLater(() -> subText.setPrefWidth(225 - mainText.getWidth()));
            subText.setAlignment(Pos.CENTER_RIGHT);
            subText.setTextAlignment(TextAlignment.RIGHT);
            subText.getStyleClass().addAll("settingsPaneText", "settingsPaneSubText");
        }

        if(requiresArrow) {
            StackPane.setAlignment(arrowPane, Pos.CENTER_RIGHT);
            arrowPane.setMinSize(12, 35);
            arrowPane.setPrefSize(12, 35);
            arrowPane.setMaxSize(12, 35);
            arrowPane.getChildren().add(arrowIcon);

            arrowIcon.setMinSize(8, 13);
            arrowIcon.setPrefSize(8, 13);
            arrowIcon.setMaxSize(8, 13);
            arrowIcon.setShape(arrowSVG);
            arrowIcon.getStyleClass().add("settingsPaneIcon");
        }


        subtitlesOptionsPane.subtitlesOptionsBox.getChildren().add(this);
    }
}
