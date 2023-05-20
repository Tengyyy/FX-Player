package tengy.Subtitles;

import javafx.animation.*;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;
import tengy.PlaybackSettings.CheckTab;
import tengy.PlaybackSettings.PlaybackSettingsController;
import tengy.SVG;
import tengy.Utilities;

import java.util.ArrayList;
import java.util.List;

import static tengy.Utilities.keyboardFocusOff;
import static tengy.Utilities.keyboardFocusOn;

public class TextAlignmentPane {

    SubtitlesController subtitlesController;
    SubtitlesOptionsPane subtitlesOptionsPane;

    ScrollPane scrollPane = new ScrollPane();

    VBox textAlignmentBox = new VBox();
    HBox textAlignmentTitle = new HBox();

    Button backButton = new Button();
    Region textAlignmentBackIcon = new Region();
    SVGPath backSVG = new SVGPath();

    Label textAlignmentTitleLabel = new Label();

    CheckTab leftTab, centerTab, rightTab;

    List<Node> focusNodes = new ArrayList<>();

    IntegerProperty focus = new SimpleIntegerProperty(-1);

    ArrayList<CheckTab> checkTabs = new ArrayList<>();

    TextAlignmentPane(SubtitlesController subtitlesController, SubtitlesOptionsPane subtitlesOptionsPane){
        this.subtitlesController = subtitlesController;
        this.subtitlesOptionsPane = subtitlesOptionsPane;

        backSVG.setContent(SVG.CHEVRON_LEFT.getContent());

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("settingsScroll");
        scrollPane.setPrefSize(200, 174);
        scrollPane.setMaxSize(200, 174);
        scrollPane.setContent(textAlignmentBox);
        scrollPane.setVisible(false);
        scrollPane.setMouseTransparent(true);
        scrollPane.setFitToWidth(true);

        StackPane.setAlignment(scrollPane, Pos.BOTTOM_RIGHT);
        textAlignmentBox.setAlignment(Pos.BOTTOM_CENTER);


        textAlignmentBox.setPrefSize(200, 171);
        textAlignmentBox.setMaxSize(200, 171);
        textAlignmentBox.setPadding(new Insets(0, 0, 8, 0));
        textAlignmentBox.getChildren().add(textAlignmentTitle);
        textAlignmentBox.setFillWidth(true);

        textAlignmentTitle.setPrefSize(200, 48);
        textAlignmentTitle.setMaxSize(200, 48);
        textAlignmentTitle.setPadding(new Insets(0, 10, 0, 10));
        textAlignmentTitle.setAlignment(Pos.CENTER_LEFT);
        VBox.setMargin(textAlignmentTitle, new Insets(0, 0, 10, 0));

        textAlignmentTitle.getStyleClass().add("settingsPaneTitle");
        textAlignmentTitle.getChildren().addAll(backButton, textAlignmentTitleLabel);

        backButton.setMinSize(30, 40);
        backButton.setPrefSize(30, 40);
        backButton.setMaxSize(30, 40);
        backButton.setFocusTraversable(false);
        backButton.getStyleClass().addAll("transparentButton", "settingsMenuButton");
        backButton.setGraphic(textAlignmentBackIcon);
        backButton.setOnAction((e) -> closeTextAlignmentPane());
        backButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue) focus.set(0);
            else {
                keyboardFocusOff(backButton);
                focus.set(-1);
            }
        });

        backButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            backButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        backButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            backButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });

        textAlignmentBackIcon.setMinSize(8, 13);
        textAlignmentBackIcon.setPrefSize(8, 13);
        textAlignmentBackIcon.setMaxSize(8, 13);
        textAlignmentBackIcon.getStyleClass().add("graphic");
        textAlignmentBackIcon.setShape(backSVG);

        textAlignmentTitleLabel.setMinHeight(40);
        textAlignmentTitleLabel.setPrefHeight(40);
        textAlignmentTitleLabel.setMaxHeight(40);
        textAlignmentTitleLabel.setText("Text alignment");
        textAlignmentTitleLabel.setCursor(Cursor.HAND);
        textAlignmentTitleLabel.setPadding(new Insets(0, 0, 0, 4));
        textAlignmentTitleLabel.getStyleClass().add("settingsPaneText");
        textAlignmentTitleLabel.setOnMouseClicked((e) -> closeTextAlignmentPane());

        leftTab = new CheckTab(false, "Left", focus, 1, () -> this.pressLeftTab(false));
        centerTab = new CheckTab(false, "Center", focus, 2, () -> this.pressCenterTab(false));
        rightTab = new CheckTab(false, "Right", focus, 3, () -> this.pressRightTab(false));

        textAlignmentBox.getChildren().addAll(leftTab, centerTab, rightTab);
        checkTabs.add(leftTab);
        checkTabs.add(centerTab);
        checkTabs.add(rightTab);

        focusNodes.add(backButton);
        focusNodes.add(leftTab);
        focusNodes.add(centerTab);
        focusNodes.add(rightTab);

        leftTab.setOnMouseClicked(e -> pressLeftTab(false));
        centerTab.setOnMouseClicked(e -> pressCenterTab(false));
        rightTab.setOnMouseClicked(e -> pressRightTab(false));

        subtitlesController.subtitlesPane.getChildren().add(scrollPane);
    }


    public void closeTextAlignmentPane(){
        if(subtitlesController.animating.get()) return;

        subtitlesController.subtitlesState = SubtitlesState.CAPTIONS_OPTIONS_OPEN;

        subtitlesController.subtitlesOptionsPane.scrollPane.setVisible(true);
        subtitlesController.subtitlesOptionsPane.scrollPane.setMouseTransparent(false);


        Timeline clipHeightTimeline = new Timeline();
        clipHeightTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(subtitlesController.clip.heightProperty(), subtitlesController.subtitlesOptionsPane.scrollPane.getHeight())));


        Timeline clipWidthTimeline = new Timeline();
        clipWidthTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(subtitlesController.clip.widthProperty(), subtitlesController.subtitlesOptionsPane.scrollPane.getWidth())));



        TranslateTransition textAlignmentTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), scrollPane);
        textAlignmentTransition.setFromX(0);
        textAlignmentTransition.setToX(scrollPane.getWidth());

        TranslateTransition captionsOptionsTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), subtitlesController.subtitlesOptionsPane.scrollPane);
        captionsOptionsTransition.setFromX(-scrollPane.getWidth());
        captionsOptionsTransition.setToX(0);


        ParallelTransition parallelTransition = new ParallelTransition(clipHeightTimeline, clipWidthTimeline, textAlignmentTransition, captionsOptionsTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            subtitlesController.animating.set(false);
            scrollPane.setVisible(false);
            scrollPane.setMouseTransparent(true);
            scrollPane.setTranslateX(0);
            subtitlesController.clip.setHeight(subtitlesController.subtitlesOptionsPane.scrollPane.getPrefHeight());
        });

        parallelTransition.play();
        subtitlesController.animating.set(true);
    }

    public void updateValue(Pos newValue, String displayText){

        for(CheckTab checkTab : checkTabs){
            checkTab.checkIcon.setVisible(false);
        }

        subtitlesOptionsPane.textAlignmentTab.subText.setText(displayText);

        subtitlesController.subtitlesBox.currentTextAlignment.set(newValue);
        subtitlesController.mainController.pref.preferences.put(SubtitlesBox.SUBTITLES_TEXT_ALIGNMENT, newValue.toString());

        subtitlesController.subtitlesBox.showCaptions();
    }

    private void initializeValue(Pos newValue, String displayText){
        subtitlesOptionsPane.textAlignmentTab.subText.setText(displayText);
        subtitlesController.subtitlesBox.currentTextAlignment.set(newValue);
    }

    public void setInitialValue(Pos position){
        if(position.equals(Pos.CENTER_LEFT)) pressLeftTab(true);
        else if(position.equals(Pos.CENTER)) pressCenterTab(true);
        else pressRightTab(true);
    }

    public void pressLeftTab(boolean initial){
        if(initial) initializeValue(Pos.CENTER_LEFT, "Left");
        else updateValue(Pos.CENTER_LEFT, "Left");

        leftTab.checkIcon.setVisible(true);
    }

    public void pressCenterTab(boolean initial){
        if(initial) initializeValue(Pos.CENTER, "Center");
        else updateValue(Pos.CENTER, "Center");

        centerTab.checkIcon.setVisible(true);
    }

    public void pressRightTab(boolean initial){
        if(initial) initializeValue(Pos.CENTER_RIGHT, "Right");
        else updateValue(Pos.CENTER_RIGHT, "Right");

        rightTab.checkIcon.setVisible(true);
    }

    public void focusForward() {
        int newFocus;

        if(focus.get() == 3 || focus.get() == -1) newFocus = 0;
        else newFocus = focus.get() + 1;

        keyboardFocusOn(focusNodes.get(newFocus));
        if(newFocus == 0) scrollPane.setVvalue(0);
        else Utilities.setScroll(scrollPane, focusNodes.get(newFocus));
    }

    public void focusBackward() {
        int newFocus;

        if(focus.get() == 0) newFocus = 3;
        else if(focus.get() == -1) newFocus = 0;
        else newFocus = focus.get() - 1;

        keyboardFocusOn(focusNodes.get(newFocus));
        if(newFocus == 0) scrollPane.setVvalue(0);
        else Utilities.setScroll(scrollPane, focusNodes.get(newFocus));
    }

}




