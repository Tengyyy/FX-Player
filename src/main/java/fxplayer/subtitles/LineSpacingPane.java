package fxplayer.subtitles;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import fxplayer.*;
import fxplayer.playbackSettings.CheckTab;
import fxplayer.playbackSettings.PlaybackSettingsController;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

import static fxplayer.Utilities.keyboardFocusOff;
import static fxplayer.Utilities.keyboardFocusOn;

public class LineSpacingPane {

    SubtitlesController subtitlesController;
    SubtitlesOptionsPane subtitlesOptionsPane;

    ScrollPane scrollPane = new ScrollPane();

    VBox lineSpacingBox = new VBox();
    HBox lineSpacingTitle = new HBox();

    Button backButton = new Button();
    Region lineSpacingBackIcon = new Region();
    SVGPath backSVG = new SVGPath();

    Label lineSpacingTitleLabel = new Label();

    CheckTab _0Tab, _50Tab, _75Tab, _100Tab, _125Tab, _150Tab, _200Tab;

    List<Node> focusNodes = new ArrayList<>();

    IntegerProperty focus = new SimpleIntegerProperty(-1);

    ArrayList<CheckTab> checkTabs = new ArrayList<>();

    LineSpacingPane(SubtitlesController subtitlesController, SubtitlesOptionsPane subtitlesOptionsPane){
        this.subtitlesController = subtitlesController;
        this.subtitlesOptionsPane = subtitlesOptionsPane;

        backSVG.setContent(SVG.CHEVRON_LEFT.getContent());

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("settingsScroll");
        scrollPane.setPrefSize(200, 314);
        scrollPane.setMaxSize(200, 314);
        scrollPane.setContent(lineSpacingBox);
        scrollPane.setVisible(false);
        scrollPane.setMouseTransparent(true);
        scrollPane.setFitToWidth(true);

        StackPane.setAlignment(scrollPane, Pos.BOTTOM_RIGHT);
        lineSpacingBox.setAlignment(Pos.BOTTOM_CENTER);


        lineSpacingBox.setPrefSize(200, 311);
        lineSpacingBox.setMaxSize(200, 311);
        lineSpacingBox.setPadding(new Insets(0, 0, 8, 0));
        lineSpacingBox.setFillWidth(true);
        lineSpacingBox.getChildren().add(lineSpacingTitle);

        lineSpacingTitle.setPrefSize(200, 48);
        lineSpacingTitle.setMaxSize(200, 48);
        lineSpacingTitle.setPadding(new Insets(0, 10, 0, 10));
        VBox.setMargin(lineSpacingTitle, new Insets(0, 0, 10, 0));
        lineSpacingTitle.setAlignment(Pos.CENTER_LEFT);

        lineSpacingTitle.getStyleClass().add("settingsPaneTitle");
        lineSpacingTitle.getChildren().addAll(backButton, lineSpacingTitleLabel);

        backButton.setMinSize(30, 40);
        backButton.setPrefSize(30, 40);
        backButton.setMaxSize(30, 40);
        backButton.setFocusTraversable(false);
        backButton.getStyleClass().addAll("transparentButton", "settingsMenuButton");
        backButton.setGraphic(lineSpacingBackIcon);
        backButton.setOnAction((e) -> closeLineSpacingPane());
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

        lineSpacingBackIcon.setMinSize(8, 13);
        lineSpacingBackIcon.setPrefSize(8, 13);
        lineSpacingBackIcon.setMaxSize(8, 13);
        lineSpacingBackIcon.getStyleClass().add("graphic");
        lineSpacingBackIcon.setShape(backSVG);

        lineSpacingTitleLabel.setMinHeight(40);
        lineSpacingTitleLabel.setPrefHeight(40);
        lineSpacingTitleLabel.setMaxHeight(40);
        lineSpacingTitleLabel.setText("Line spacing");
        lineSpacingTitleLabel.setCursor(Cursor.HAND);
        lineSpacingTitleLabel.setPadding(new Insets(0, 0, 0, 4));
        lineSpacingTitleLabel.getStyleClass().add("settingsPaneText");
        lineSpacingTitleLabel.setOnMouseClicked((e) -> closeLineSpacingPane());

        _0Tab = new CheckTab(false, "0%", focus, 1, () -> this.press_0Tab(false));
        _50Tab = new CheckTab(false, "50%", focus, 2, () -> this.press_50Tab(false));
        _75Tab = new CheckTab(false, "75%", focus, 3, () -> this.press_75Tab(false));
        _100Tab = new CheckTab(false, "100%", focus, 4, () -> this.press_100Tab(false));
        _125Tab = new CheckTab(false, "125%", focus, 5, () -> this.press_125Tab(false));
        _150Tab = new CheckTab(false, "150%", focus, 6, () -> this.press_150Tab(false));
        _200Tab = new CheckTab(false, "200%", focus, 7, () -> this.press_200Tab(false));

        lineSpacingBox.getChildren().addAll(_0Tab, _50Tab, _75Tab, _100Tab, _125Tab, _150Tab, _200Tab);
        checkTabs.add(_0Tab);
        checkTabs.add(_50Tab);
        checkTabs.add(_75Tab);
        checkTabs.add(_100Tab);
        checkTabs.add(_125Tab);
        checkTabs.add(_150Tab);
        checkTabs.add(_200Tab);

        focusNodes.add(backButton);
        focusNodes.add(_0Tab);
        focusNodes.add(_50Tab);
        focusNodes.add(_75Tab);
        focusNodes.add(_100Tab);
        focusNodes.add(_125Tab);
        focusNodes.add(_150Tab);
        focusNodes.add(_200Tab);


        subtitlesController.subtitlesPane.getChildren().add(scrollPane);
    }


    public void closeLineSpacingPane(){
        if(subtitlesController.animating.get()) return;

        subtitlesController.subtitlesState = SubtitlesState.CAPTIONS_OPTIONS_OPEN;

        subtitlesController.subtitlesOptionsPane.scrollPane.setVisible(true);
        subtitlesController.subtitlesOptionsPane.scrollPane.setMouseTransparent(false);


        Timeline clipHeightTimeline = new Timeline();
        clipHeightTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(subtitlesController.clip.heightProperty(), subtitlesController.subtitlesOptionsPane.scrollPane.getHeight())));


        Timeline clipWidthTimeline = new Timeline();
        clipWidthTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(subtitlesController.clip.widthProperty(), subtitlesController.subtitlesOptionsPane.scrollPane.getWidth())));



        TranslateTransition fontColorTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), scrollPane);
        fontColorTransition.setFromX(0);
        fontColorTransition.setToX(scrollPane.getWidth());

        TranslateTransition captionsOptionsTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), subtitlesController.subtitlesOptionsPane.scrollPane);
        captionsOptionsTransition.setFromX(-scrollPane.getWidth());
        captionsOptionsTransition.setToX(0);


        ParallelTransition parallelTransition = new ParallelTransition(clipHeightTimeline, clipWidthTimeline, fontColorTransition, captionsOptionsTransition);
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

    public void updateValue(double newValue, String displayText){

        for(CheckTab checkTab : checkTabs){
            checkTab.checkIcon.setVisible(false);
        }

        subtitlesOptionsPane.lineSpacingTab.subText.setText(displayText);

        subtitlesController.subtitlesBox.currentSpacing.set((int) (newValue * subtitlesController.subtitlesBox.defaultSpacing));
        subtitlesController.mainController.pref.preferences.putDouble(SubtitlesBox.SUBTITLES_SPACING, newValue);

        subtitlesController.subtitlesBox.showCaptions();
    }

    private void initializeValue(double newValue, String displayText){
        subtitlesOptionsPane.lineSpacingTab.subText.setText(displayText);
        subtitlesController.subtitlesBox.currentSpacing.set((int) (newValue * subtitlesController.subtitlesBox.defaultSpacing));
    }

    public void setInitialValue(double spacing){
        if(spacing == 0.0) press_0Tab(true);
        else if(spacing == 0.5) press_50Tab(true);
        else if(spacing == 0.75) press_75Tab(true);
        else if(spacing == 1.0) press_100Tab(true);
        else if(spacing == 1.25) press_125Tab(true);
        else if(spacing == 1.5) press_150Tab(true);
        else press_200Tab(true);
    }

    public void press_0Tab(boolean initial){
        if(initial) initializeValue(0.0, "0%");
        else updateValue(0.0, "0%");

        _0Tab.checkIcon.setVisible(true);
    }

    public void press_50Tab(boolean initial){
        if(initial) initializeValue(0.5, "50%");
        else updateValue(0.5, "50%");

        _50Tab.checkIcon.setVisible(true);
    }

    public void press_75Tab(boolean initial){
        if(initial) initializeValue(0.75, "75%");
        else updateValue(0.75, "75%");

        _75Tab.checkIcon.setVisible(true);
    }

    public void press_100Tab(boolean initial){
        if(initial) initializeValue(1.0, "100%");
        else updateValue(1.0, "100%");

        _100Tab.checkIcon.setVisible(true);
    }

    public void press_125Tab(boolean initial){
        if(initial) initializeValue(1.25, "125%");
        else updateValue(1.25, "125%");

        _125Tab.checkIcon.setVisible(true);
    }

    public void press_150Tab(boolean initial){
        if(initial) initializeValue(1.5, "150%");
        else updateValue(1.5, "150%");

        _150Tab.checkIcon.setVisible(true);
    }

    public void press_200Tab(boolean initial){
        if(initial) initializeValue(2.0, "200%");
        else updateValue(2.0, "200%");

        _200Tab.checkIcon.setVisible(true);
    }

    public void focusForward() {
        int newFocus;

        if(focus.get() == 7 || focus.get() == -1) newFocus = 0;
        else newFocus = focus.get() + 1;

        keyboardFocusOn(focusNodes.get(newFocus));
        if(newFocus == 0) scrollPane.setVvalue(0);
        else Utilities.checkScrollDown(scrollPane, focusNodes.get(newFocus));
    }

    public void focusBackward() {
        int newFocus;

        if(focus.get() == 0) newFocus = 7;
        else if(focus.get() == -1) newFocus = 0;
        else newFocus = focus.get() - 1;

        keyboardFocusOn(focusNodes.get(newFocus));
        if(newFocus == 0) scrollPane.setVvalue(0);
        else Utilities.checkScrollUp(scrollPane, focusNodes.get(newFocus));
    }

}




