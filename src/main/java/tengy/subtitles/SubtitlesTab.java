package tengy.subtitles;

import javafx.css.PseudoClass;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import tengy.SVG;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;

import java.io.File;

import static tengy.Utilities.keyboardFocusOff;

public class SubtitlesTab extends HBox {

    SubtitlesController subtitlesController;
    SubtitlesHome subtitlesHome;

    File subtitleFile;

    StackPane checkIconPane = new StackPane();
    public Region checkIcon = new Region();

    SVGPath checkSVG = new SVGPath(), removeSVG = new SVGPath();

    Region removeIcon = new Region();
    Button removeButton = new Button();
    public Label valueLabel = new Label();

    public boolean selected = false;

    public boolean removable;

    boolean pressed = false;

    SubtitlesTab(SubtitlesController subtitlesController, SubtitlesHome subtitlesHome, String value, File file, boolean removable){

        this.subtitlesController = subtitlesController;
        this.subtitlesHome = subtitlesHome;
        this.subtitleFile = file;
        this.removable = removable;

        checkSVG.setContent(SVG.CHECK.getContent());
        removeSVG.setContent(SVG.CLOSE.getContent());

        this.setPrefSize(295, 35);
        this.setMaxSize(295, 35);

        this.setPadding(new Insets(0, 10, 0, 10));
        this.setFocusTraversable(false);
        this.getStyleClass().add("settingsPaneTab");

        this.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                subtitlesHome.focus.set(subtitlesHome.focusNodes.indexOf(this));
            }
            else {
                subtitlesHome.focus.set(-1);
                pressed = false;
                this.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
            }
        });

        this.setOnMousePressed(e -> {
            if(e.getTarget().equals(removeButton)){
                System.out.println("test");
            }
        });


        this.setOnMouseClicked(e -> {
            selectSubtitles(true);
        });
        this.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;

            pressed = true;
            this.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);

            e.consume();
        });

        this.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;

            if(pressed) selectSubtitles(true);

            pressed = false;
            this.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);

            e.consume();
        });

        checkIconPane.setMinSize(30, 35);
        checkIconPane.setPrefSize(30, 35);
        checkIconPane.setMaxSize(30, 35);
        checkIconPane.setPadding(new Insets(0, 5, 0, 0));
        checkIconPane.getChildren().add(checkIcon);
        checkIconPane.setMouseTransparent(true);

        checkIcon.setMinSize(14, 11);
        checkIcon.setPrefSize(14, 11);
        checkIcon.setMaxSize(14, 11);
        checkIcon.setShape(checkSVG);
        checkIcon.getStyleClass().add("settingsPaneIcon");
        checkIcon.setVisible(false);

        valueLabel.setFont(new Font(15));
        valueLabel.setMinHeight(35);
        valueLabel.setPrefHeight(35);
        valueLabel.setMaxHeight(35);
        valueLabel.setText(value);
        valueLabel.getStyleClass().add("settingsPaneText");
        valueLabel.setMouseTransparent(true);

        if(removable){
            valueLabel.setPrefWidth(210);
            valueLabel.setMaxWidth(210);

            removeButton.setMinSize(35, 35);
            removeButton.setPrefSize(35, 35);
            removeButton.setMaxSize(35, 35);
            removeButton.setGraphic(removeIcon);
            removeButton.setOnAction(e -> removeItem());
            removeButton.setFocusTraversable(false);
            removeButton.getStyleClass().addAll("transparentButton", "settingsMenuButton");
            removeButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
                if(newValue) subtitlesHome.focus.set(subtitlesHome.focusNodes.indexOf(removeButton));
                else {
                    keyboardFocusOff(removeButton);
                    subtitlesHome.focus.set(-1);
                }
            });

            removeButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
                if(e.getCode() != KeyCode.SPACE) return;
                removeButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
            });

            removeButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
                if(e.getCode() != KeyCode.SPACE) return;
                removeButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
            });

            removeIcon.setMinSize(16, 16);
            removeIcon.setPrefSize(16, 16);
            removeIcon.setMaxSize(16, 16);
            removeIcon.setShape(removeSVG);
            removeIcon.getStyleClass().add("graphic");
            removeIcon.setMouseTransparent(true);

            this.getChildren().addAll(checkIconPane, valueLabel, removeButton);
        }
        else {
            valueLabel.setPrefWidth(245);
            valueLabel.setMaxWidth(245);

            this.getChildren().addAll(checkIconPane, valueLabel);
        }

        double height = subtitlesHome.subtitlesWrapper.getPrefHeight();
        subtitlesHome.subtitlesWrapper.setPrefHeight(height + 35);
        subtitlesHome.subtitlesWrapper.setMaxHeight(height + 35);

        subtitlesHome.scrollPane.setPrefHeight(Math.min(height + 38, 300));
        subtitlesHome.scrollPane.setMaxHeight(Math.min(height + 38, 300));

        if(subtitlesController.subtitlesState == SubtitlesState.HOME_OPEN || subtitlesController.subtitlesState == SubtitlesState.CLOSED){
            subtitlesController.clip.setHeight(Math.min(height + 38, 300));
        }
    }

    public void selectSubtitles(boolean checkSelected){
        if(selected){
            if(!checkSelected) return;
            subtitlesController.removeSubtitles();
            selected = false;
            checkIcon.setVisible(false);
        }
        else {
            for(SubtitlesTab subtitlesTab : subtitlesHome.subtitlesTabs){
                subtitlesTab.checkIcon.setVisible(false);
                subtitlesTab.selected = false;
            }

            checkIcon.setVisible(true);
            selected = true;
            subtitlesController.loadSubtitles(this.subtitleFile);
        }
    }

    public void removeItem(){

        if(selected){
            subtitlesController.removeSubtitles();
        }

        subtitlesHome.subtitlesTabs.remove(this);
        if(subtitlesHome.subtitlesTabs.isEmpty()){
            subtitlesHome.subtitlesChooserTab.setStyle("-fx-border-width: 0;");
        }

        subtitlesHome.focusNodes.remove(this);
        if(removable) subtitlesHome.focusNodes.remove(removeButton);

        boolean removed = subtitlesHome.subtitlesWrapper.getChildren().remove(this);
        if(removed){
            double height = subtitlesHome.subtitlesWrapper.getPrefHeight();

            subtitlesHome.subtitlesWrapper.setPrefHeight(height - 35);
            subtitlesHome.subtitlesWrapper.setMaxHeight(height - 35);

            subtitlesHome.scrollPane.setPrefHeight(Math.min(300,height - 32));
            subtitlesHome.scrollPane.setMaxHeight(Math.min(300, height - 32));

            if(subtitlesController.subtitlesState == SubtitlesState.CLOSED || subtitlesController.subtitlesState == SubtitlesState.HOME_OPEN){
                subtitlesController.clip.setHeight(Math.min(300,height - 32));
            }
        }
    }
}
