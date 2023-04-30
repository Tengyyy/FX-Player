package hans.Subtitles;

import hans.*;
import hans.PlaybackSettings.CheckTab;
import hans.PlaybackSettings.PlaybackSettingsController;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.skin.CellSkinBase;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

import java.util.ArrayList;

public class FontFamilyPane {

    SubtitlesController subtitlesController;
    SubtitlesOptionsPane subtitlesOptionsPane;

    ScrollPane scrollPane = new ScrollPane();

    VBox fontFamilyBox = new VBox();
    HBox fontFamilyTitle = new HBox();

    StackPane fontFamilyBackPane = new StackPane();
    Region fontFamilyBackIcon = new Region();
    SVGPath backSVG = new SVGPath();

    Label fontFamilyTitleLabel = new Label();

    CheckTab sansSerifRegularTab, sansSerifMediumTab, sansSerifBoldTab, serifTab, casualTab, cursiveTab, smallCapitalsTab;

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
        fontFamilyTitle.getChildren().addAll(fontFamilyBackPane, fontFamilyTitleLabel);

        fontFamilyBackPane.setMinSize(24, 40);
        fontFamilyBackPane.setPrefSize(24, 40);
        fontFamilyBackPane.setMaxSize(24, 40);
        fontFamilyBackPane.getChildren().add(fontFamilyBackIcon);
        fontFamilyBackPane.setCursor(Cursor.HAND);
        fontFamilyBackPane.setOnMouseClicked((e) -> closeFontFamilyPane());

        fontFamilyBackIcon.setMinSize(8, 13);
        fontFamilyBackIcon.setPrefSize(8, 13);
        fontFamilyBackIcon.setMaxSize(8, 13);
        fontFamilyBackIcon.getStyleClass().add("settingsPaneIcon");
        fontFamilyBackIcon.setShape(backSVG);

        fontFamilyTitleLabel.setMinHeight(40);
        fontFamilyTitleLabel.setPrefHeight(40);
        fontFamilyTitleLabel.setMaxHeight(40);
        fontFamilyTitleLabel.setText("Font family");
        fontFamilyTitleLabel.setCursor(Cursor.HAND);
        fontFamilyTitleLabel.getStyleClass().add("settingsPaneText");
        fontFamilyTitleLabel.setOnMouseClicked((e) -> closeFontFamilyPane());

        sansSerifRegularTab = new CheckTab(false, "Sans-Serif Regular");
        sansSerifMediumTab = new CheckTab(false, "Sans-Serif Medium");
        sansSerifBoldTab = new CheckTab(false, "Sans-Serif Bold");
        serifTab = new CheckTab(false, "Serif");
        casualTab = new CheckTab(false, "Casual");
        cursiveTab = new CheckTab(false, "Cursive");
        smallCapitalsTab = new CheckTab(false, "Small Capitals");

        fontFamilyBox.getChildren().addAll(sansSerifRegularTab, sansSerifMediumTab, sansSerifBoldTab, serifTab, casualTab, cursiveTab, smallCapitalsTab);
        checkTabs.add(sansSerifRegularTab);
        checkTabs.add(sansSerifMediumTab);
        checkTabs.add(sansSerifBoldTab);
        checkTabs.add(serifTab);
        checkTabs.add(casualTab);
        checkTabs.add(cursiveTab);
        checkTabs.add(smallCapitalsTab);

        sansSerifRegularTab.setOnMouseClicked(e -> pressSansSerifRegularTab(false));
        sansSerifMediumTab.setOnMouseClicked(e -> pressSansSerifMediumTab(false));
        sansSerifBoldTab.setOnMouseClicked(e -> pressSansSerifBoldTab(false));
        serifTab.setOnMouseClicked(e -> pressSerifTab(false));
        casualTab.setOnMouseClicked(e -> pressCasualTab(false));
        cursiveTab.setOnMouseClicked(e -> pressCursiveTab(false));
        smallCapitalsTab.setOnMouseClicked(e -> pressSmallCapitalsTab(false));

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

}
