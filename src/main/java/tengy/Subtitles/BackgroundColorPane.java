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
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

import static tengy.Utilities.keyboardFocusOff;
import static tengy.Utilities.keyboardFocusOn;

public class BackgroundColorPane {

    SubtitlesController subtitlesController;
    SubtitlesOptionsPane subtitlesOptionsPane;

    ScrollPane scrollPane = new ScrollPane();

    VBox backgroundColorBox = new VBox();
    HBox backgroundColorTitle = new HBox();

    Button backButton = new Button();
    Region backgroundColorBackIcon = new Region();
    SVGPath backSVG = new SVGPath();

    Label backgroundColorTitleLabel = new Label();

    CheckTab whiteTab, yellowTab, greenTab, cyanTab, blueTab, magentaTab, redTab, blackTab;

    List<Node> focusNodes = new ArrayList<>();

    IntegerProperty focus = new SimpleIntegerProperty(-1);

    ArrayList<CheckTab> checkTabs = new ArrayList<>();

    BackgroundColorPane(SubtitlesController subtitlesController, SubtitlesOptionsPane subtitlesOptionsPane){
        this.subtitlesController = subtitlesController;
        this.subtitlesOptionsPane = subtitlesOptionsPane;

        backSVG.setContent(SVG.CHEVRON_LEFT.getContent());

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("settingsScroll");
        scrollPane.setPrefSize(200, 349);
        scrollPane.setMaxSize(200, 349);
        scrollPane.setContent(backgroundColorBox);
        scrollPane.setVisible(false);
        scrollPane.setMouseTransparent(true);
        scrollPane.setFitToWidth(true);

        StackPane.setAlignment(scrollPane, Pos.BOTTOM_RIGHT);
        backgroundColorBox.setAlignment(Pos.BOTTOM_CENTER);


        backgroundColorBox.setPrefSize(200, 346);
        backgroundColorBox.setMaxSize(200, 346);
        backgroundColorBox.setPadding(new Insets(0, 0, 8, 0));
        backgroundColorBox.getChildren().add(backgroundColorTitle);
        backgroundColorBox.setFillWidth(true);

        backgroundColorTitle.setPrefSize(200, 48);
        backgroundColorTitle.setMaxSize(200, 48);
        backgroundColorTitle.setPadding(new Insets(0, 10, 0, 10));
        backgroundColorTitle.setAlignment(Pos.CENTER_LEFT);
        VBox.setMargin(backgroundColorTitle, new Insets(0, 0, 10, 0));

        backgroundColorTitle.getStyleClass().add("settingsPaneTitle");
        backgroundColorTitle.getChildren().addAll(backButton, backgroundColorTitleLabel);

        backButton.setMinSize(30, 40);
        backButton.setPrefSize(30, 40);
        backButton.setMaxSize(30, 40);
        backButton.setFocusTraversable(false);
        backButton.getStyleClass().addAll("transparentButton", "settingsMenuButton");
        backButton.setGraphic(backgroundColorBackIcon);
        backButton.setOnAction((e) -> closeBackgroundColorPane());
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

        backgroundColorBackIcon.setMinSize(8, 13);
        backgroundColorBackIcon.setPrefSize(8, 13);
        backgroundColorBackIcon.setMaxSize(8, 13);
        backgroundColorBackIcon.getStyleClass().add("graphic");
        backgroundColorBackIcon.setShape(backSVG);

        backgroundColorTitleLabel.setMinHeight(40);
        backgroundColorTitleLabel.setPrefHeight(40);
        backgroundColorTitleLabel.setMaxHeight(40);
        backgroundColorTitleLabel.setText("Background color");
        backgroundColorTitleLabel.setCursor(Cursor.HAND);
        backgroundColorTitleLabel.setPadding(new Insets(0, 0, 0, 4));
        backgroundColorTitleLabel.getStyleClass().add("settingsPaneText");
        backgroundColorTitleLabel.setOnMouseClicked((e) -> closeBackgroundColorPane());

        whiteTab = new CheckTab(false, "White", focus, 1, () -> this.pressWhiteTab(false));
        yellowTab = new CheckTab(false, "Yellow", focus, 2, () -> this.pressYellowTab(false));
        greenTab = new CheckTab(false, "Green", focus, 3, () -> this.pressGreenTab(false));
        cyanTab = new CheckTab(false, "Cyan", focus, 4, () -> this.pressCyanTab(false));
        blueTab = new CheckTab(false, "Blue", focus, 5, () -> this.pressBlueTab(false));
        magentaTab = new CheckTab(false, "Magenta", focus, 6, () -> this.pressMagentaTab(false));
        redTab = new CheckTab(false, "Red", focus, 7, () -> this.pressRedTab(false));
        blackTab = new CheckTab(false, "Black", focus, 8, () -> this.pressBlackTab(false));

        focusNodes.add(backButton);
        focusNodes.add(whiteTab);
        focusNodes.add(yellowTab);
        focusNodes.add(greenTab);
        focusNodes.add(cyanTab);
        focusNodes.add(blueTab);
        focusNodes.add(magentaTab);
        focusNodes.add(redTab);
        focusNodes.add(blackTab);

        backgroundColorBox.getChildren().addAll(whiteTab, yellowTab, greenTab, cyanTab, blueTab, magentaTab, redTab, blackTab);
        checkTabs.add(whiteTab);
        checkTabs.add(yellowTab);
        checkTabs.add(greenTab);
        checkTabs.add(cyanTab);
        checkTabs.add(blueTab);
        checkTabs.add(magentaTab);
        checkTabs.add(redTab);
        checkTabs.add(blackTab);

        subtitlesController.subtitlesPane.getChildren().add(scrollPane);
    }


    public void closeBackgroundColorPane(){
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

        subtitlesOptionsPane.backgroundColorTab.subText.setText(displayText);
        subtitlesController.subtitlesBox.currentBackgroundColor.set(color);
        subtitlesController.mainController.pref.preferences.put(SubtitlesBox.SUBTITLES_BACKGROUND_COLOR, color.toString());

        subtitlesController.subtitlesBox.showCaptions();
    }

    private void initializeValue(Color color, String displayText){
        subtitlesOptionsPane.backgroundColorTab.subText.setText(displayText);
        subtitlesController.subtitlesBox.currentBackgroundColor.set(color);
    }

    public void setInitialValue(Color color){
        int red = (int) (color.getRed() * 255);
        int green = (int) (color.getGreen() * 255);
        int blue = (int) (color.getBlue() * 255);

        if(red == 255 && green == 255 && blue == 255) pressWhiteTab(true);
        else if(red == 255 && green == 255 && blue == 0) pressYellowTab(true);
        else if(red == 0 && green == 255 && blue == 0) pressGreenTab(true);
        else if(red == 0 && green == 255 && blue == 255) pressCyanTab(true);
        else if(red == 0 && green == 0 && blue == 255) pressBlueTab(true);
        else if(red == 255 && green == 0 && blue == 255) pressMagentaTab(true);
        else if(red == 255 && green == 0 && blue == 0) pressRedTab(true);
        else pressBlackTab(true);
    }


    public void pressWhiteTab(boolean initial){
        if( initial) initializeValue(Color.rgb(255, 255, 255, subtitlesController.subtitlesBox.currentBackgroundColor.get().getOpacity()), "White");
        else updateValue(Color.rgb(255, 255, 255, subtitlesController.subtitlesBox.currentBackgroundColor.get().getOpacity()), "White");

        whiteTab.checkIcon.setVisible(true);
    }

    public void pressYellowTab(boolean initial){
        if( initial) initializeValue(Color.rgb(255, 255, 0, subtitlesController.subtitlesBox.currentBackgroundColor.get().getOpacity()), "Yellow");
        else updateValue(Color.rgb(255, 255, 0, subtitlesController.subtitlesBox.currentBackgroundColor.get().getOpacity()), "Yellow");

        yellowTab.checkIcon.setVisible(true);
    }

    public void pressGreenTab(boolean initial){
        if( initial) initializeValue(Color.rgb(0, 255, 0, subtitlesController.subtitlesBox.currentBackgroundColor.get().getOpacity()), "Green");
        else updateValue(Color.rgb(0, 255, 0, subtitlesController.subtitlesBox.currentBackgroundColor.get().getOpacity()), "Green");

        greenTab.checkIcon.setVisible(true);
    }

    public void pressCyanTab(boolean initial){
        if( initial) initializeValue(Color.rgb(0, 255, 255, subtitlesController.subtitlesBox.currentBackgroundColor.get().getOpacity()), "Cyan");
        else updateValue(Color.rgb(0, 255, 255, subtitlesController.subtitlesBox.currentBackgroundColor.get().getOpacity()), "Cyan");

        cyanTab.checkIcon.setVisible(true);
    }

    public void pressBlueTab(boolean initial){
        if( initial) initializeValue(Color.rgb(0, 0, 255, subtitlesController.subtitlesBox.currentBackgroundColor.get().getOpacity()), "Blue");
        else updateValue(Color.rgb(0, 0, 255, subtitlesController.subtitlesBox.currentBackgroundColor.get().getOpacity()), "Blue");
        blueTab.checkIcon.setVisible(true);
    }

    public void pressMagentaTab(boolean initial){
        if( initial) initializeValue(Color.rgb(255, 0, 255, subtitlesController.subtitlesBox.currentBackgroundColor.get().getOpacity()), "Magenta");
        else updateValue(Color.rgb(255, 0, 255, subtitlesController.subtitlesBox.currentBackgroundColor.get().getOpacity()), "Magenta");

        magentaTab.checkIcon.setVisible(true);
    }

    public void pressRedTab(boolean initial){
        if( initial) initializeValue(Color.rgb(255, 0, 0, subtitlesController.subtitlesBox.currentBackgroundColor.get().getOpacity()), "Red");
        else updateValue(Color.rgb(255, 0, 0, subtitlesController.subtitlesBox.currentBackgroundColor.get().getOpacity()), "Red");

        redTab.checkIcon.setVisible(true);
    }

    public void pressBlackTab(boolean initial){
        if( initial) initializeValue(Color.rgb(0, 0, 0, subtitlesController.subtitlesBox.currentBackgroundColor.get().getOpacity()), "Black");
        else updateValue(Color.rgb(0, 0, 0, subtitlesController.subtitlesBox.currentBackgroundColor.get().getOpacity()), "Black");

        blackTab.checkIcon.setVisible(true);
    }

    public void focusForward() {
        int newFocus;

        if(focus.get() == 8 || focus.get() == -1) newFocus = 0;
        else newFocus = focus.get() + 1;

        keyboardFocusOn(focusNodes.get(newFocus));
        if(newFocus == 0) scrollPane.setVvalue(0);
        else Utilities.setScroll(scrollPane, focusNodes.get(newFocus));
    }

    public void focusBackward() {
        int newFocus;

        if(focus.get() == 0) newFocus = 8;
        else if(focus.get() == -1) newFocus = 0;
        else newFocus = focus.get() - 1;

        keyboardFocusOn(focusNodes.get(newFocus));
        if(newFocus == 0) scrollPane.setVvalue(0);
        else Utilities.setScroll(scrollPane, focusNodes.get(newFocus));
    }

}


