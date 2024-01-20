package tengy.subtitles;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import tengy.*;
import tengy.playbackSettings.CheckTab;
import tengy.playbackSettings.PlaybackSettingsController;
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
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

import static tengy.Utilities.keyboardFocusOff;
import static tengy.Utilities.keyboardFocusOn;

public class FontColorPane {

    SubtitlesController subtitlesController;
    SubtitlesOptionsPane subtitlesOptionsPane;

    ScrollPane scrollPane = new ScrollPane();

    VBox fontColorBox = new VBox();
    HBox fontColorTitle = new HBox();

    Button backButton = new Button();
    Region fontColorBackIcon = new Region();
    SVGPath backSVG = new SVGPath();

    Label fontColorTitleLabel = new Label();

    CheckTab whiteTab, yellowTab, greenTab, cyanTab, blueTab, magentaTab, redTab, blackTab;

    List<Node> focusNodes = new ArrayList<>();

    IntegerProperty focus = new SimpleIntegerProperty(-1);

    ArrayList<CheckTab> checkTabs = new ArrayList<>();


    FontColorPane(SubtitlesController subtitlesController, SubtitlesOptionsPane subtitlesOptionsPane){
        this.subtitlesController = subtitlesController;
        this.subtitlesOptionsPane = subtitlesOptionsPane;

        backSVG.setContent(SVG.CHEVRON_LEFT.getContent());

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("settingsScroll");
        scrollPane.setPrefSize(200, 349);
        scrollPane.setMaxSize(200, 349);
        scrollPane.setContent(fontColorBox);
        scrollPane.setVisible(false);
        scrollPane.setMouseTransparent(true);
        scrollPane.setFitToWidth(true);

        StackPane.setAlignment(scrollPane, Pos.BOTTOM_RIGHT);
        fontColorBox.setAlignment(Pos.BOTTOM_CENTER);


        fontColorBox.setPrefSize(200, 346);
        fontColorBox.setMaxSize(200, 346);
        fontColorBox.setPadding(new Insets(0, 0, 8, 0));
        fontColorBox.getChildren().add(fontColorTitle);
        fontColorBox.setFillWidth(true);

        fontColorTitle.setPrefSize(200, 48);
        fontColorTitle.setMaxSize(200, 48);
        fontColorTitle.setPadding(new Insets(0, 10, 0, 10));
        fontColorTitle.setAlignment(Pos.CENTER_LEFT);
        VBox.setMargin(fontColorTitle, new Insets(0, 0, 10, 0));

        fontColorTitle.getStyleClass().add("settingsPaneTitle");
        fontColorTitle.getChildren().addAll(backButton, fontColorTitleLabel);

        backButton.setMinSize(30, 40);
        backButton.setPrefSize(30, 40);
        backButton.setMaxSize(30, 40);
        backButton.setFocusTraversable(false);
        backButton.getStyleClass().addAll("transparentButton", "settingsMenuButton");
        backButton.setGraphic(fontColorBackIcon);
        backButton.setOnAction((e) -> closeFontColorPane());
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

        fontColorBackIcon.setMinSize(8, 13);
        fontColorBackIcon.setPrefSize(8, 13);
        fontColorBackIcon.setMaxSize(8, 13);
        fontColorBackIcon.getStyleClass().add("graphic");
        fontColorBackIcon.setShape(backSVG);

        fontColorTitleLabel.setMinHeight(40);
        fontColorTitleLabel.setPrefHeight(40);
        fontColorTitleLabel.setMaxHeight(40);
        fontColorTitleLabel.setText("Font color");
        fontColorTitleLabel.setCursor(Cursor.HAND);
        fontColorTitleLabel.setPadding(new Insets(0, 0, 0, 4));
        fontColorTitleLabel.getStyleClass().add("settingsPaneText");
        fontColorTitleLabel.setOnMouseClicked((e) -> closeFontColorPane());

        whiteTab = new CheckTab(false, "White", focus, 1, () -> this.pressWhiteTab(false));
        yellowTab = new CheckTab(false, "Yellow", focus, 2, () -> this.pressYellowTab(false));
        greenTab = new CheckTab(false, "Green", focus, 3, () -> this.pressGreenTab(false));
        cyanTab = new CheckTab(false, "Cyan", focus, 4, () -> this.pressCyanTab(false));
        blueTab = new CheckTab(false, "Blue", focus, 5, () -> this.pressBlueTab(false));
        magentaTab = new CheckTab(false, "Magenta", focus, 6, () -> this.pressMagentaTab(false));
        redTab = new CheckTab(false, "Red", focus, 7, () -> this.pressRedTab(false));
        blackTab = new CheckTab(false, "Black", focus, 8, () -> this.pressBlackTab(false));

        fontColorBox.getChildren().addAll(whiteTab, yellowTab, greenTab, cyanTab, blueTab, magentaTab, redTab, blackTab);
        checkTabs.add(whiteTab);
        checkTabs.add(yellowTab);
        checkTabs.add(greenTab);
        checkTabs.add(cyanTab);
        checkTabs.add(blueTab);
        checkTabs.add(magentaTab);
        checkTabs.add(redTab);
        checkTabs.add(blackTab);

        focusNodes.add(backButton);
        focusNodes.add(whiteTab);
        focusNodes.add(yellowTab);
        focusNodes.add(greenTab);
        focusNodes.add(cyanTab);
        focusNodes.add(blueTab);
        focusNodes.add(magentaTab);
        focusNodes.add(redTab);
        focusNodes.add(blackTab);

        subtitlesController.subtitlesPane.getChildren().add(scrollPane);
    }


    public void closeFontColorPane(){
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

        subtitlesOptionsPane.fontColorTab.subText.setText(displayText);

        subtitlesController.subtitlesBox.currentTextColor.set(color);
        subtitlesController.mainController.pref.preferences.put(SubtitlesBox.SUBTITLES_TEXT_COLOR, color.toString());

        subtitlesController.subtitlesBox.showCaptions();
    }

    private void initializeValue(Color color, String displayText){
        subtitlesOptionsPane.fontColorTab.subText.setText(displayText);
        subtitlesController.subtitlesBox.currentTextColor.set(color);
    }

    public void setInitialValue(Color color){
        if(color.equals(Color.WHITE)) pressWhiteTab(true);
        else if(color.equals(Color.YELLOW)) pressYellowTab(true);
        else if(color.equals(Color.LIME)) pressGreenTab(true);
        else if(color.equals(Color.CYAN)) pressCyanTab(true);
        else if(color.equals(Color.BLUE)) pressBlueTab(true);
        else if(color.equals(Color.MAGENTA)) pressMagentaTab(true);
        else if(color.equals(Color.RED)) pressRedTab(true);
        else pressBlackTab(true);

    }


    public void pressWhiteTab(boolean initial){
        if(initial) initializeValue(Color.WHITE, "White");
        else updateValue(Color.WHITE, "White");

        whiteTab.checkIcon.setVisible(true);
    }

    public void pressYellowTab(boolean initial){
        if(initial) initializeValue(Color.YELLOW, "Yellow");
        else updateValue(Color.YELLOW, "Yellow");

        yellowTab.checkIcon.setVisible(true);
    }

    public void pressGreenTab(boolean initial){
        if(initial) initializeValue(Color.LIME, "Green");
        else updateValue(Color.LIME, "Green");

        greenTab.checkIcon.setVisible(true);
    }

    public void pressCyanTab(boolean initial){
        if(initial) initializeValue(Color.CYAN, "Cyan");
        else updateValue(Color.CYAN, "Cyan");

        cyanTab.checkIcon.setVisible(true);
    }

    public void pressBlueTab(boolean initial){
        if(initial) initializeValue(Color.BLUE, "Blue");
        else updateValue(Color.BLUE, "Blue");

        blueTab.checkIcon.setVisible(true);
    }

    public void pressMagentaTab(boolean initial){
        if(initial) initializeValue(Color.MAGENTA, "Magenta");
        else updateValue(Color.MAGENTA, "Magenta");

        magentaTab.checkIcon.setVisible(true);
    }

    public void pressRedTab(boolean initial){
        if(initial) initializeValue(Color.RED, "Red");
        else updateValue(Color.RED, "Red");

        redTab.checkIcon.setVisible(true);
    }

    public void pressBlackTab(boolean initial){
        if(initial) initializeValue(Color.BLACK, "Black");
        else updateValue(Color.BLACK, "Black");

        blackTab.checkIcon.setVisible(true);
    }

    public void focusForward() {
        int newFocus;

        if(focus.get() == 8 || focus.get() == -1) newFocus = 0;
        else newFocus = focus.get() + 1;

        keyboardFocusOn(focusNodes.get(newFocus));
        if(newFocus == 0) scrollPane.setVvalue(0);
        else Utilities.checkScrollDown(scrollPane, focusNodes.get(newFocus));
    }

    public void focusBackward() {
        int newFocus;

        if(focus.get() == 0) newFocus = 8;
        else if(focus.get() == -1) newFocus = 0;
        else newFocus = focus.get() - 1;

        keyboardFocusOn(focusNodes.get(newFocus));
        if(newFocus == 0) scrollPane.setVvalue(0);
        else Utilities.checkScrollUp(scrollPane, focusNodes.get(newFocus));
    }
}
