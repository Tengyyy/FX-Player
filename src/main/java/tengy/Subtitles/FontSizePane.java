package tengy.Subtitles;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import tengy.*;
import tengy.PlaybackSettings.CheckTab;
import tengy.PlaybackSettings.PlaybackSettingsController;
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

import static tengy.Utilities.keyboardFocusOff;
import static tengy.Utilities.keyboardFocusOn;

public class FontSizePane {

    SubtitlesController subtitlesController;
    SubtitlesOptionsPane subtitlesOptionsPane;

    ScrollPane scrollPane = new ScrollPane();

    VBox fontSizeBox = new VBox();
    HBox fontSizeTitle = new HBox();

    Button backButton = new Button();
    Region fontSizeBackIcon = new Region();
    SVGPath backSVG = new SVGPath();

    Label fontSizeTitleLabel = new Label();

    CheckTab _50Tab, _75Tab, _100Tab, _150Tab, _200Tab, _300Tab, _400Tab;

    List<Node> focusNodes = new ArrayList<>();

    IntegerProperty focus = new SimpleIntegerProperty(-1);

    ArrayList<CheckTab> checkTabs = new ArrayList<>();


    FontSizePane(SubtitlesController subtitlesController, SubtitlesOptionsPane subtitlesOptionsPane){
        this.subtitlesController = subtitlesController;
        this.subtitlesOptionsPane = subtitlesOptionsPane;

        backSVG.setContent(SVG.CHEVRON_LEFT.getContent());

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("settingsScroll");
        scrollPane.setPrefSize(200, 314);
        scrollPane.setMaxSize(200, 314);
        scrollPane.setContent(fontSizeBox);
        scrollPane.setVisible(false);
        scrollPane.setMouseTransparent(true);
        scrollPane.setFitToWidth(true);

        StackPane.setAlignment(scrollPane, Pos.BOTTOM_RIGHT);
        fontSizeBox.setAlignment(Pos.BOTTOM_CENTER);


        fontSizeBox.setPrefSize(200, 311);
        fontSizeBox.setMaxSize(200, 311);
        fontSizeBox.setPadding(new Insets(0, 0, 8, 0));
        fontSizeBox.getChildren().add(fontSizeTitle);
        fontSizeBox.setFillWidth(true);

        fontSizeTitle.setPrefSize(200, 48);
        fontSizeTitle.setMaxSize(200, 48);
        fontSizeTitle.setPadding(new Insets(0, 10, 0, 10));
        fontSizeTitle.setAlignment(Pos.CENTER_LEFT);
        VBox.setMargin(fontSizeTitle, new Insets(0, 0, 10, 0));

        fontSizeTitle.getStyleClass().add("settingsPaneTitle");
        fontSizeTitle.getChildren().addAll(backButton, fontSizeTitleLabel);

        backButton.setMinSize(30, 40);
        backButton.setPrefSize(30, 40);
        backButton.setMaxSize(30, 40);
        backButton.setFocusTraversable(false);
        backButton.getStyleClass().addAll("transparentButton", "settingsMenuButton");
        backButton.setGraphic(fontSizeBackIcon);
        backButton.setOnAction((e) -> closeFontSizePane());
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

        fontSizeBackIcon.setMinSize(8, 13);
        fontSizeBackIcon.setPrefSize(8, 13);
        fontSizeBackIcon.setMaxSize(8, 13);
        fontSizeBackIcon.getStyleClass().add("graphic");
        fontSizeBackIcon.setShape(backSVG);

        fontSizeTitleLabel.setMinHeight(40);
        fontSizeTitleLabel.setPrefHeight(40);
        fontSizeTitleLabel.setMaxHeight(40);
        fontSizeTitleLabel.setText("Font size");
        fontSizeTitleLabel.setPadding(new Insets(0, 0, 0, 4));
        fontSizeTitleLabel.setCursor(Cursor.HAND);
        fontSizeTitleLabel.getStyleClass().add("settingsPaneText");
        fontSizeTitleLabel.setOnMouseClicked((e) -> closeFontSizePane());

        _50Tab = new CheckTab(false, "50%", focus, 1, () -> this.press_50Tab(false));
        _75Tab = new CheckTab(false, "75%", focus, 2, () -> this.press_75Tab(false));
        _100Tab = new CheckTab(false, "100%", focus, 3, () -> this.press_100Tab(false));
        _150Tab = new CheckTab(false, "150%", focus, 4, () -> this.press_150Tab(false));
        _200Tab = new CheckTab(false, "200%", focus, 5, () -> this.press_200Tab(false));
        _300Tab = new CheckTab(false, "300%", focus, 6, () -> this.press_300Tab(false));
        _400Tab = new CheckTab(false, "400%", focus, 7, () -> this.press_400Tab(false));

        fontSizeBox.getChildren().addAll(_50Tab, _75Tab, _100Tab, _150Tab, _200Tab, _300Tab, _400Tab);
        checkTabs.add(_50Tab);
        checkTabs.add(_75Tab);
        checkTabs.add(_100Tab);
        checkTabs.add(_150Tab);
        checkTabs.add(_200Tab);
        checkTabs.add(_300Tab);
        checkTabs.add(_400Tab);

        focusNodes.add(backButton);
        focusNodes.add(_50Tab);
        focusNodes.add(_75Tab);
        focusNodes.add(_100Tab);
        focusNodes.add(_150Tab);
        focusNodes.add(_200Tab);
        focusNodes.add(_300Tab);
        focusNodes.add(_400Tab);

        subtitlesController.subtitlesPane.getChildren().add(scrollPane);
    }


    public void closeFontSizePane(){
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

        subtitlesOptionsPane.fontSizeTab.subText.setText(displayText);

        subtitlesController.subtitlesBox.currentFontSize.set(newValue * subtitlesController.subtitlesBox.defaultFontSize);
        subtitlesController.mainController.pref.preferences.putDouble(SubtitlesBox.SUBTITLES_FONT_SIZE, newValue);

        subtitlesController.subtitlesBox.showCaptions();
    }

    private void initializeValue(double newValue, String displayText){
        subtitlesOptionsPane.fontSizeTab.subText.setText(displayText);
        subtitlesController.subtitlesBox.currentFontSize.set(newValue * subtitlesController.subtitlesBox.defaultFontSize);
    }

    public void setInitialValue(double fontSize){
        if(fontSize == 0.75) press_50Tab(true);
        else if(fontSize == 0.875) press_75Tab(true);
        else if(fontSize == 1.0) press_100Tab(true);
        else if(fontSize == 1.25) press_150Tab(true);
        else if(fontSize == 1.5) press_200Tab(true);
        else if(fontSize == 2.0) press_300Tab(true);
        else press_400Tab(true);

    }


    public void press_50Tab(boolean initial){
        if(initial) initializeValue(0.75, "50%");
        else updateValue(0.75, "50%");

        _50Tab.checkIcon.setVisible(true);
    }

    public void press_75Tab(boolean initial){
        if(initial) initializeValue(0.875, "75%");
        else updateValue(0.875, "75%");

        _75Tab.checkIcon.setVisible(true);
    }

    public void press_100Tab(boolean initial){
        if(initial) initializeValue(1.0, "100%");
        else updateValue(1.0, "100%");

        _100Tab.checkIcon.setVisible(true);
    }

    public void press_150Tab(boolean initial){
        if(initial) initializeValue(1.25, "150%");
        else updateValue(1.25, "150%");

        _150Tab.checkIcon.setVisible(true);
    }

    public void press_200Tab(boolean initial){
        if(initial) initializeValue(1.5, "200%");
        else updateValue(1.5, "200%");

        _200Tab.checkIcon.setVisible(true);
    }

    public void press_300Tab(boolean initial){
        if(initial) initializeValue(2, "300%");
        else updateValue(2, "300%");

        _300Tab.checkIcon.setVisible(true);
    }

    public void press_400Tab(boolean initial){
        if(initial) initializeValue(2.25, "400%");
        else updateValue(2.25, "400%");

        _400Tab.checkIcon.setVisible(true);
    }

    public void focusForward() {
        int newFocus;

        if(focus.get() == 7 || focus.get() == -1) newFocus = 0;
        else newFocus = focus.get() + 1;

        keyboardFocusOn(focusNodes.get(newFocus));
        if(newFocus == 0) scrollPane.setVvalue(0);
        else Utilities.setScrollToNodeMiddle(scrollPane, focusNodes.get(newFocus));
    }

    public void focusBackward() {
        int newFocus;

        if(focus.get() == 0) newFocus = 7;
        else if(focus.get() == -1) newFocus = 0;
        else newFocus = focus.get() - 1;

        keyboardFocusOn(focusNodes.get(newFocus));
        if(newFocus == 0) scrollPane.setVvalue(0);
        else Utilities.setScrollToNodeMiddle(scrollPane, focusNodes.get(newFocus));
    }
}

