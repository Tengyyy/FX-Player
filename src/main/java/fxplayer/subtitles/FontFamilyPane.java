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

public class FontFamilyPane {

    SubtitlesController subtitlesController;
    SubtitlesOptionsPane subtitlesOptionsPane;

    ScrollPane scrollPane = new ScrollPane();

    VBox fontFamilyBox = new VBox();
    HBox fontFamilyTitle = new HBox();

    Button backButton = new Button();
    Region fontFamilyBackIcon = new Region();
    SVGPath backSVG = new SVGPath();

    Label fontFamilyTitleLabel = new Label();

    CheckTab sansSerifRegularTab, sansSerifMediumTab, sansSerifBoldTab, serifTab, casualTab, cursiveTab, smallCapitalsTab;

    List<Node> focusNodes = new ArrayList<>();

    IntegerProperty focus = new SimpleIntegerProperty(-1);

    ArrayList<CheckTab> checkTabs = new ArrayList<>();

    FontFamilyPane(SubtitlesController subtitlesController, SubtitlesOptionsPane subtitlesOptionsPane){
        this.subtitlesController = subtitlesController;
        this.subtitlesOptionsPane = subtitlesOptionsPane;

        backSVG.setContent(SVG.CHEVRON_LEFT.getContent());

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("settingsScroll");
        scrollPane.setPrefSize(200, 314);
        scrollPane.setMaxSize(200, 314);
        scrollPane.setContent(fontFamilyBox);
        scrollPane.setVisible(false);
        scrollPane.setMouseTransparent(true);
        scrollPane.setFitToWidth(true);

        StackPane.setAlignment(scrollPane, Pos.BOTTOM_RIGHT);
        fontFamilyBox.setAlignment(Pos.BOTTOM_CENTER);

        fontFamilyBox.setPrefSize(200, 311);
        fontFamilyBox.setMaxSize(200, 311);
        fontFamilyBox.setPadding(new Insets(0, 0, 8, 0));
        fontFamilyBox.getChildren().add(fontFamilyTitle);
        fontFamilyBox.setFillWidth(true);

        fontFamilyTitle.setPrefSize(200, 48);
        fontFamilyTitle.setMaxSize(200, 48);
        fontFamilyTitle.setPadding(new Insets(0, 10, 0, 10));
        fontFamilyTitle.setAlignment(Pos.CENTER_LEFT);
        VBox.setMargin(fontFamilyTitle, new Insets(0, 0, 10, 0));

        fontFamilyTitle.getStyleClass().add("settingsPaneTitle");
        fontFamilyTitle.getChildren().addAll(backButton, fontFamilyTitleLabel);

        backButton.setMinSize(30, 40);
        backButton.setPrefSize(30, 40);
        backButton.setMaxSize(30, 40);
        backButton.setFocusTraversable(false);
        backButton.getStyleClass().addAll("transparentButton", "settingsMenuButton");
        backButton.setGraphic(fontFamilyBackIcon);
        backButton.setOnAction((e) -> closeFontFamilyPane());
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

        fontFamilyBackIcon.setMinSize(8, 13);
        fontFamilyBackIcon.setPrefSize(8, 13);
        fontFamilyBackIcon.setMaxSize(8, 13);
        fontFamilyBackIcon.getStyleClass().add("graphic");
        fontFamilyBackIcon.setShape(backSVG);

        fontFamilyTitleLabel.setMinHeight(40);
        fontFamilyTitleLabel.setPrefHeight(40);
        fontFamilyTitleLabel.setMaxHeight(40);
        fontFamilyTitleLabel.setText("Font family");
        fontFamilyTitleLabel.setCursor(Cursor.HAND);
        fontFamilyTitleLabel.setPadding(new Insets(0, 0, 0, 4));
        fontFamilyTitleLabel.getStyleClass().add("settingsPaneText");
        fontFamilyTitleLabel.setOnMouseClicked((e) -> closeFontFamilyPane());

        sansSerifRegularTab = new CheckTab(false, "Sans-Serif Regular", focus, 1, () -> this.pressSansSerifRegularTab(false));
        sansSerifMediumTab = new CheckTab(false, "Sans-Serif Medium", focus, 2, () -> this.pressSansSerifMediumTab(false));
        sansSerifBoldTab = new CheckTab(false, "Sans-Serif Bold", focus, 3, () -> this.pressSansSerifBoldTab(false));
        serifTab = new CheckTab(false, "Serif", focus, 4, () -> this.pressSerifTab(false));
        casualTab = new CheckTab(false, "Casual", focus, 5, () -> this.pressCasualTab(false));
        cursiveTab = new CheckTab(false, "Cursive", focus, 6, () -> this.pressCursiveTab(false));
        smallCapitalsTab = new CheckTab(false, "Small Capitals", focus, 7, () -> this.pressSmallCapitalsTab(false));

        fontFamilyBox.getChildren().addAll(sansSerifRegularTab, sansSerifMediumTab, sansSerifBoldTab, serifTab, casualTab, cursiveTab, smallCapitalsTab);

        checkTabs.add(sansSerifRegularTab);
        checkTabs.add(sansSerifMediumTab);
        checkTabs.add(sansSerifBoldTab);
        checkTabs.add(serifTab);
        checkTabs.add(casualTab);
        checkTabs.add(cursiveTab);
        checkTabs.add(smallCapitalsTab);

        focusNodes.add(backButton);
        focusNodes.add(sansSerifRegularTab);
        focusNodes.add(sansSerifMediumTab);
        focusNodes.add(sansSerifBoldTab);
        focusNodes.add(serifTab);
        focusNodes.add(casualTab);
        focusNodes.add(cursiveTab);
        focusNodes.add(smallCapitalsTab);

        subtitlesController.subtitlesPane.getChildren().add(scrollPane);
    }


    public void closeFontFamilyPane(){
        if(subtitlesController.animating.get()) return;

        subtitlesController.subtitlesState = SubtitlesState.CAPTIONS_OPTIONS_OPEN;

        subtitlesController.subtitlesOptionsPane.scrollPane.setVisible(true);
        subtitlesController.subtitlesOptionsPane.scrollPane.setMouseTransparent(false);


        Timeline clipHeightTimeline = new Timeline();
        clipHeightTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(subtitlesController.clip.heightProperty(), subtitlesController.subtitlesOptionsPane.scrollPane.getHeight())));


        Timeline clipWidthTimeline = new Timeline();
        clipWidthTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(subtitlesController.clip.widthProperty(), subtitlesController.subtitlesOptionsPane.scrollPane.getWidth())));



        TranslateTransition fontFamilyTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), scrollPane);
        fontFamilyTransition.setFromX(0);
        fontFamilyTransition.setToX(scrollPane.getWidth());

        TranslateTransition captionsOptionsTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), subtitlesController.subtitlesOptionsPane.scrollPane);
        captionsOptionsTransition.setFromX(-scrollPane.getWidth());
        captionsOptionsTransition.setToX(0);


        ParallelTransition parallelTransition = new ParallelTransition(clipHeightTimeline, clipWidthTimeline, fontFamilyTransition, captionsOptionsTransition);
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

    public void updateValue(String fontName, String displayText){

        for(CheckTab checkTab : checkTabs){
            checkTab.checkIcon.setVisible(false);
        }

        subtitlesOptionsPane.fontFamilyTab.subText.setText(displayText);

        subtitlesController.subtitlesBox.currentFontFamily.set(fontName);
        subtitlesController.mainController.pref.preferences.put(SubtitlesBox.SUBTITLES_FONT_FAMILY, fontName);

        subtitlesController.subtitlesBox.showCaptions();
    }

    private void initializeValue(String fontName, String displayText){
        subtitlesOptionsPane.fontFamilyTab.subText.setText(displayText);
        subtitlesController.subtitlesBox.currentFontFamily.set(fontName);
    }
    
    public void setInitialValue(String fontFamily){
        switch(fontFamily){
            case "\"Roboto\"" -> pressSansSerifRegularTab(true);
            case "\"Roboto Medium\"" -> pressSansSerifMediumTab(true);
            case "\"Roboto Bold\"" -> pressSansSerifBoldTab(true);
            case "\"EB Garamond Medium\"" -> pressSerifTab(true);
            case "\"Comic Neue Bold\"" -> pressCasualTab(true);
            case "\"Kalam Bold\"" -> pressCursiveTab(true);
            default -> pressSmallCapitalsTab(true);
        }
    }


    public void pressSansSerifRegularTab(boolean initial){

        if(initial) initializeValue("\"Roboto\"", "Sans-Serif Regular");
        else updateValue("\"Roboto\"", "Sans-Serif Regular");

        sansSerifRegularTab.checkIcon.setVisible(true);
    }

    public void pressSansSerifMediumTab(boolean initial){
        if(initial) initializeValue("\"Roboto Medium\"", "Sans-Serif Medium");
        else updateValue("\"Roboto Medium\"", "Sans-Serif Medium");

        sansSerifMediumTab.checkIcon.setVisible(true);
    }

    public void pressSansSerifBoldTab(boolean initial){
        if (initial) initializeValue("\"Roboto Bold\"", "Sans-Serif Bold");
        else updateValue("\"Roboto Bold\"", "Sans-Serif Bold");

        sansSerifBoldTab.checkIcon.setVisible(true);
    }

    public void pressSerifTab(boolean initial){
        if(initial) initializeValue("\"EB Garamond Medium\"", "Serif");
        else updateValue("\"EB Garamond Medium\"", "Serif");

        serifTab.checkIcon.setVisible(true);
    }

    public void pressCasualTab(boolean initial){
        if(initial) initializeValue("\"Comic Neue Bold\"", "Casual");
        else updateValue("\"Comic Neue Bold\"", "Casual");

        casualTab.checkIcon.setVisible(true);
    }

    public void pressCursiveTab(boolean initial){
        if(initial) initializeValue("\"Kalam Bold\"", "Cursive");
        else updateValue("\"Kalam Bold\"", "Cursive");

        cursiveTab.checkIcon.setVisible(true);
    }

    public void pressSmallCapitalsTab(boolean initial){
        if(initial) initializeValue("\"Alegreya Sans SC Medium\"", "Small Capitals");
        else updateValue("\"Alegreya Sans SC Medium\"", "Small Capitals");

        smallCapitalsTab.checkIcon.setVisible(true);
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
