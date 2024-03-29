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
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

import static fxplayer.Utilities.keyboardFocusOff;
import static fxplayer.Utilities.keyboardFocusOn;

public class BackgroundOpacityPane {

    SubtitlesController subtitlesController;
    SubtitlesOptionsPane subtitlesOptionsPane;

    ScrollPane scrollPane = new ScrollPane();

    VBox backgroundOpacityBox = new VBox();
    HBox backgroundOpacityTitle = new HBox();

    Button backButton = new Button();
    Region backgroundOpacityBackIcon = new Region();
    SVGPath backSVG = new SVGPath();

    Label backgroundOpacityTitleLabel = new Label();

    CheckTab _0Tab, _25Tab, _50Tab, _75Tab, _100Tab;


    List<Node> focusNodes = new ArrayList<>();

    IntegerProperty focus = new SimpleIntegerProperty(-1);

    ArrayList<CheckTab> checkTabs = new ArrayList<>();

    BackgroundOpacityPane(SubtitlesController subtitlesController, SubtitlesOptionsPane subtitlesOptionsPane){
        this.subtitlesController = subtitlesController;
        this.subtitlesOptionsPane = subtitlesOptionsPane;

        backSVG.setContent(SVG.CHEVRON_LEFT.getContent());

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("settingsScroll");
        scrollPane.setPrefSize(200, 244);
        scrollPane.setMaxSize(200, 244);
        scrollPane.setContent(backgroundOpacityBox);
        scrollPane.setVisible(false);
        scrollPane.setMouseTransparent(true);
        scrollPane.setFitToWidth(true);

        StackPane.setAlignment(scrollPane, Pos.BOTTOM_RIGHT);
        backgroundOpacityBox.setAlignment(Pos.BOTTOM_CENTER);


        backgroundOpacityBox.setPrefSize(200, 241);
        backgroundOpacityBox.setMaxSize(200, 241);
        backgroundOpacityBox.setPadding(new Insets(0, 0, 8, 0));
        backgroundOpacityBox.getChildren().add(backgroundOpacityTitle);
        backgroundOpacityBox.setFillWidth(true);

        backgroundOpacityTitle.setPrefSize(200, 48);
        backgroundOpacityTitle.setMaxSize(200, 48);
        backgroundOpacityTitle.setPadding(new Insets(0, 10, 0, 10));
        backgroundOpacityTitle.setAlignment(Pos.CENTER_LEFT);
        VBox.setMargin(backgroundOpacityTitle, new Insets(0, 0, 10, 0));

        backgroundOpacityTitle.getStyleClass().add("settingsPaneTitle");
        backgroundOpacityTitle.getChildren().addAll(backButton, backgroundOpacityTitleLabel);


        backButton.setMinSize(30, 40);
        backButton.setPrefSize(30, 40);
        backButton.setMaxSize(30, 40);
        backButton.getStyleClass().addAll("transparentButton", "settingsMenuButton");
        backButton.setGraphic(backgroundOpacityBackIcon);
        backButton.setFocusTraversable(false);
        backButton.setOnAction((e) -> closeBackgroundOpacityPane());
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

        backgroundOpacityBackIcon.setMinSize(8, 13);
        backgroundOpacityBackIcon.setPrefSize(8, 13);
        backgroundOpacityBackIcon.setMaxSize(8, 13);
        backgroundOpacityBackIcon.getStyleClass().add("graphic");
        backgroundOpacityBackIcon.setShape(backSVG);

        backgroundOpacityTitleLabel.setMinHeight(40);
        backgroundOpacityTitleLabel.setPrefHeight(40);
        backgroundOpacityTitleLabel.setMaxHeight(40);
        backgroundOpacityTitleLabel.setText("Background opacity");
        backgroundOpacityTitleLabel.setCursor(Cursor.HAND);
        backgroundOpacityTitleLabel.setPadding(new Insets(0, 0, 0, 4));
        backgroundOpacityTitleLabel.getStyleClass().add("settingsPaneText");
        backgroundOpacityTitleLabel.setOnMouseClicked((e) -> closeBackgroundOpacityPane());

        _0Tab = new CheckTab(false, "0%", focus, 1, () -> this.press_0Tab(false));
        _25Tab = new CheckTab(false, "25%", focus, 2, () -> this.press_25Tab(false));
        _50Tab = new CheckTab(false, "50%", focus, 3, () -> this.press_50Tab(false));
        _75Tab = new CheckTab(false, "75%", focus, 4, () -> this.press_75Tab(false));
        _100Tab = new CheckTab(false, "100%", focus, 5, () -> this.press_100Tab(false));

        backgroundOpacityBox.getChildren().addAll(_0Tab, _25Tab, _50Tab, _75Tab, _100Tab);

        checkTabs.add(_0Tab);
        checkTabs.add(_25Tab);
        checkTabs.add(_50Tab);
        checkTabs.add(_75Tab);
        checkTabs.add(_100Tab);

        focusNodes.add(backButton);
        focusNodes.add(_0Tab);
        focusNodes.add(_25Tab);
        focusNodes.add(_50Tab);
        focusNodes.add(_75Tab);
        focusNodes.add(_100Tab);


        subtitlesController.subtitlesPane.getChildren().add(scrollPane);
    }


    public void closeBackgroundOpacityPane(){
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




    public void updateValue(Color color, String displayText){

        for(CheckTab checkTab : checkTabs){
            checkTab.checkIcon.setVisible(false);
        }

        subtitlesOptionsPane.backgroundOpacityTab.subText.setText(displayText);
        subtitlesController.subtitlesBox.currentBackgroundColor.set(color);
        subtitlesController.mainController.pref.preferences.put(SubtitlesBox.SUBTITLES_BACKGROUND_COLOR, color.toString());

        subtitlesController.subtitlesBox.showCaptions();
    }

    private void initializeValue(Color color, String displayText){
        subtitlesOptionsPane.backgroundOpacityTab.subText.setText(displayText);
        subtitlesController.subtitlesBox.currentBackgroundColor.set(color);
    }

    public void setInitialValue(double opacity){
        if (opacity == 0.0) press_0Tab(true);
        else if (opacity == 0.25) press_25Tab(true);
        else if (opacity == 0.5) press_50Tab(true);
        else if (opacity == 0.75) press_75Tab(true);
        else press_100Tab(true);
    }

    public void press_0Tab(boolean initial){
        if (initial) initializeValue(Color.rgb((int) (subtitlesController.subtitlesBox.currentBackgroundColor.get().getRed() * 255), (int) (subtitlesController.subtitlesBox.currentBackgroundColor.get().getGreen() * 255), (int) (subtitlesController.subtitlesBox.currentBackgroundColor.get().getBlue() * 255), 0), "0%");
        else updateValue(Color.rgb((int) (subtitlesController.subtitlesBox.currentBackgroundColor.get().getRed() * 255), (int) (subtitlesController.subtitlesBox.currentBackgroundColor.get().getGreen() * 255), (int) (subtitlesController.subtitlesBox.currentBackgroundColor.get().getBlue() * 255), 0), "0%");

        _0Tab.checkIcon.setVisible(true);
    }

    public void press_25Tab(boolean initial){
        if (initial) initializeValue(Color.rgb((int) (subtitlesController.subtitlesBox.currentBackgroundColor.get().getRed() * 255), (int) (subtitlesController.subtitlesBox.currentBackgroundColor.get().getGreen() * 255), (int) (subtitlesController.subtitlesBox.currentBackgroundColor.get().getBlue() * 255), 0.25), "25%");
        else updateValue(Color.rgb((int) (subtitlesController.subtitlesBox.currentBackgroundColor.get().getRed() * 255), (int) (subtitlesController.subtitlesBox.currentBackgroundColor.get().getGreen() * 255), (int) (subtitlesController.subtitlesBox.currentBackgroundColor.get().getBlue() * 255), 0.25), "25%");

        _25Tab.checkIcon.setVisible(true);
    }

    public void press_50Tab(boolean initial){
        if (initial) initializeValue(Color.rgb((int) (subtitlesController.subtitlesBox.currentBackgroundColor.get().getRed() * 255), (int) (subtitlesController.subtitlesBox.currentBackgroundColor.get().getGreen() * 255), (int) (subtitlesController.subtitlesBox.currentBackgroundColor.get().getBlue() * 255), 0.5), "50%");
        else updateValue(Color.rgb((int) (subtitlesController.subtitlesBox.currentBackgroundColor.get().getRed() * 255), (int) (subtitlesController.subtitlesBox.currentBackgroundColor.get().getGreen() * 255), (int) (subtitlesController.subtitlesBox.currentBackgroundColor.get().getBlue() * 255), 0.5), "50%");

        _50Tab.checkIcon.setVisible(true);
    }

    public void press_75Tab(boolean initial){
        if (initial) initializeValue(Color.rgb((int) (subtitlesController.subtitlesBox.currentBackgroundColor.get().getRed() * 255), (int) (subtitlesController.subtitlesBox.currentBackgroundColor.get().getGreen() * 255), (int) (subtitlesController.subtitlesBox.currentBackgroundColor.get().getBlue() * 255), 0.75), "75%");
        else updateValue(Color.rgb((int) (subtitlesController.subtitlesBox.currentBackgroundColor.get().getRed() * 255), (int) (subtitlesController.subtitlesBox.currentBackgroundColor.get().getGreen() * 255), (int) (subtitlesController.subtitlesBox.currentBackgroundColor.get().getBlue() * 255), 0.75), "75%");

        _75Tab.checkIcon.setVisible(true);
    }

    public void press_100Tab(boolean initial){
        if (initial) initializeValue(Color.rgb((int) (subtitlesController.subtitlesBox.currentBackgroundColor.get().getRed() * 255), (int) (subtitlesController.subtitlesBox.currentBackgroundColor.get().getGreen() * 255), (int) (subtitlesController.subtitlesBox.currentBackgroundColor.get().getBlue() * 255)), "100%");
        else updateValue(Color.rgb((int) (subtitlesController.subtitlesBox.currentBackgroundColor.get().getRed() * 255), (int) (subtitlesController.subtitlesBox.currentBackgroundColor.get().getGreen() * 255), (int) (subtitlesController.subtitlesBox.currentBackgroundColor.get().getBlue() * 255)), "100%");

        _100Tab.checkIcon.setVisible(true);
    }

    public void focusForward() {
        int newFocus;

        if(focus.get() == 5 || focus.get() == -1) newFocus = 0;
        else newFocus = focus.get() + 1;

        keyboardFocusOn(focusNodes.get(newFocus));
        if(newFocus == 0) scrollPane.setVvalue(0);
        else Utilities.checkScrollDown(scrollPane, focusNodes.get(newFocus));
    }

    public void focusBackward() {
        int newFocus;

        if(focus.get() == 0) newFocus = 5;
        else if(focus.get() == -1) newFocus = 0;
        else newFocus = focus.get() - 1;

        keyboardFocusOn(focusNodes.get(newFocus));
        if(newFocus == 0) scrollPane.setVvalue(0);
        else Utilities.checkScrollUp(scrollPane, focusNodes.get(newFocus));
    }
}




