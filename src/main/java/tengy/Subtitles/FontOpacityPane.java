package tengy.Subtitles;

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

public class FontOpacityPane {

    SubtitlesController subtitlesController;
    SubtitlesOptionsPane subtitlesOptionsPane;

    ScrollPane scrollPane = new ScrollPane();

    VBox opacityBox = new VBox();
    HBox opacityTitle = new HBox();

    StackPane opacityBackPane = new StackPane();
    Region opacityBackIcon = new Region();
    SVGPath backSVG = new SVGPath();

    Label opacityTitleLabel = new Label();

    CheckTab _25Tab, _50Tab, _75Tab, _100Tab;

    ArrayList<CheckTab> checkTabs = new ArrayList<>();

    FontOpacityPane(SubtitlesController subtitlesController, SubtitlesOptionsPane subtitlesOptionsPane){
        this.subtitlesController = subtitlesController;
        this.subtitlesOptionsPane = subtitlesOptionsPane;

        backSVG.setContent(SVG.CHEVRON_LEFT.getContent());

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("settingsScroll");
        scrollPane.setPrefSize(200, 209);
        scrollPane.setMaxSize(200, 209);
        scrollPane.setContent(opacityBox);
        scrollPane.setVisible(false);
        scrollPane.setMouseTransparent(true);
        scrollPane.setFitToWidth(true);

        StackPane.setAlignment(scrollPane, Pos.BOTTOM_RIGHT);
        opacityBox.setAlignment(Pos.BOTTOM_CENTER);


        opacityBox.setPrefSize(200, 206);
        opacityBox.setMaxSize(200, 206);
        opacityBox.setPadding(new Insets(0, 0, 8, 0));
        opacityBox.getChildren().add(opacityTitle);
        opacityBox.setFillWidth(true);

        opacityTitle.setPrefSize(200, 48);
        opacityTitle.setMaxSize(200, 48);
        opacityTitle.setPadding(new Insets(0, 10, 0, 10));
        opacityTitle.setAlignment(Pos.CENTER_LEFT);
        VBox.setMargin(opacityTitle, new Insets(0, 0, 10, 0));

        opacityTitle.getStyleClass().add("settingsPaneTitle");
        opacityTitle.getChildren().addAll(opacityBackPane, opacityTitleLabel);

        opacityBackPane.setMinSize(24, 40);
        opacityBackPane.setPrefSize(24, 40);
        opacityBackPane.setMaxSize(24, 40);
        opacityBackPane.getChildren().add(opacityBackIcon);
        opacityBackPane.setCursor(Cursor.HAND);
        opacityBackPane.setOnMouseClicked((e) -> closeOpacityPane());

        opacityBackIcon.setMinSize(8, 13);
        opacityBackIcon.setPrefSize(8, 13);
        opacityBackIcon.setMaxSize(8, 13);
        opacityBackIcon.getStyleClass().add("settingsPaneIcon");
        opacityBackIcon.setShape(backSVG);

        opacityTitleLabel.setMinHeight(40);
        opacityTitleLabel.setPrefHeight(40);
        opacityTitleLabel.setMaxHeight(40);
        opacityTitleLabel.setText("Opacity");
        opacityTitleLabel.setCursor(Cursor.HAND);
        opacityTitleLabel.getStyleClass().add("settingsPaneText");
        opacityTitleLabel.setOnMouseClicked((e) -> closeOpacityPane());

        _25Tab = new CheckTab(false, "25%");
        _50Tab = new CheckTab(false, "50%");
        _75Tab = new CheckTab(false, "75%");
        _100Tab = new CheckTab(false, "100%");

        opacityBox.getChildren().addAll(_25Tab, _50Tab, _75Tab, _100Tab);
        checkTabs.add(_25Tab);
        checkTabs.add(_50Tab);
        checkTabs.add(_75Tab);
        checkTabs.add(_100Tab);

        _25Tab.setOnMouseClicked(e -> press_25Tab(false));
        _50Tab.setOnMouseClicked(e -> press_50Tab(false));
        _75Tab.setOnMouseClicked(e -> press_75Tab(false));
        _100Tab.setOnMouseClicked(e -> press_100Tab(false));

        subtitlesController.subtitlesPane.getChildren().add(scrollPane);
    }


    public void closeOpacityPane(){
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

        subtitlesOptionsPane.fontOpacityTab.subText.setText(displayText);

        subtitlesController.subtitlesBox.currentTextOpacity.set(newValue);
        subtitlesController.mainController.pref.preferences.putDouble(SubtitlesBox.SUBTITLES_TEXT_OPACITY, newValue);

        subtitlesController.subtitlesBox.showCaptions();
    }

    private void initializeValue(double newValue, String displayText){
        subtitlesOptionsPane.fontOpacityTab.subText.setText(displayText);
        subtitlesController.subtitlesBox.currentTextOpacity.set(newValue);
    }

    public void setInitialValue(double textOpacity){
        if(textOpacity == 0.25) press_25Tab(true);
        else if(textOpacity == 0.5) press_50Tab(true);
        else if(textOpacity == 0.75) press_75Tab(true);
        else press_100Tab(true);
    }

    public void press_25Tab(boolean initial){
        if(initial) initializeValue(0.25, "25%");
        else updateValue(0.25, "25%");

        _25Tab.checkIcon.setVisible(true);
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
}




