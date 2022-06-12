package hans;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;

public class SettingsHomeController {

    SettingsController settingsController;

    VBox settingsHome = new VBox();

    SVGPath tuneSVG = new SVGPath();
    SVGPath shuffleSVG = new SVGPath();
    SVGPath repeatSVG = new SVGPath();
    SVGPath repeatOnceSVG = new SVGPath();
    SVGPath magnifySVG = new SVGPath();
    SVGPath speedSVG = new SVGPath();
    SVGPath captionsSVG = new SVGPath();

    SettingsHomeTab playbackOptionsTab, playbackSpeedTab, captionsTab, videoSelectionTab;


    FileChooser fileChooser;

    SettingsHomeController(SettingsController settingsController){
        this.settingsController = settingsController;

        fileChooser = new FileChooser();
        fileChooser.setTitle("Select video");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Videos", "*.mp4"));

        shuffleSVG.setContent(App.svgMap.get(SVG.SHUFFLE));
        repeatSVG.setContent(App.svgMap.get(SVG.REPEAT));
        repeatOnceSVG.setContent(App.svgMap.get(SVG.REPEAT_ONCE));
        magnifySVG.setContent(App.svgMap.get(SVG.MAGNIFY));
        speedSVG.setContent(App.svgMap.get(SVG.SPEED));
        captionsSVG.setContent(App.svgMap.get(SVG.CAPTIONS_OUTLINE));
        tuneSVG.setContent(App.svgMap.get(SVG.TUNE));

        settingsController.settingsPane.getChildren().add(settingsHome);
        settingsHome.setMinSize(235, 156);
        settingsHome.setPrefSize(235, 156);
        settingsHome.setMaxSize(235, 156);
        settingsHome.setPadding(new Insets(8, 0, 8, 0));
        settingsHome.setAlignment(Pos.BOTTOM_CENTER);
        settingsHome.setVisible(false);
        settingsHome.setMouseTransparent(true);

        StackPane.setAlignment(settingsHome, Pos.BOTTOM_CENTER);


        playbackOptionsTab = new SettingsHomeTab(this, false, tuneSVG, "Playback options", null);
        playbackSpeedTab = new SettingsHomeTab(this, true, speedSVG, "Playback Speed", "Normal");
        captionsTab = new SettingsHomeTab(this, false, captionsSVG, "Subtitles/CC", null);
        videoSelectionTab = new SettingsHomeTab(this, false, magnifySVG, "Select a video", null);

        playbackOptionsTab.setOnMouseClicked((e) -> openPlaybackOptionsPane());
        playbackSpeedTab.setOnMouseClicked((e) -> openPlaybackSpeedPane());
        captionsTab.setOnMouseClicked((e) -> openCaptionsPane());
        videoSelectionTab.setOnMouseClicked((e) -> openVideoChooser());

    }


    public void openPlaybackOptionsPane(){
        if(settingsController.animating.get()) return;

        settingsController.settingsHomeOpen = false;
        settingsController.playbackOptionsPaneOpen = true;

        settingsController.playbackOptionsController.playbackOptionsBox.setVisible(true);
        settingsController.playbackOptionsController.playbackOptionsBox.setMouseTransparent(false);

        Timeline clipTimeline = new Timeline();
        clipTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(settingsController.clip.heightProperty(), settingsController.playbackOptionsController.playbackOptionsBox.getHeight())));

        TranslateTransition homeTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), settingsHome);
        homeTransition.setFromX(0);
        homeTransition.setToX(-settingsHome.getWidth());

        TranslateTransition optionsTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), settingsController.playbackOptionsController.playbackOptionsBox);
        optionsTransition.setFromX(settingsHome.getWidth());
        optionsTransition.setToX(0);


        ParallelTransition parallelTransition = new ParallelTransition(clipTimeline, homeTransition, optionsTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            settingsController.animating.set(false);
            settingsHome.setVisible(false);
            settingsHome.setMouseTransparent(true);
            settingsHome.setTranslateX(0);
        });

        parallelTransition.play();
        settingsController.animating.set(true);

    }

    public void openPlaybackSpeedPane(){
        if(settingsController.animating.get()) return;

        settingsController.settingsHomeOpen = false;
        settingsController.playbackSpeedPaneOpen = true;

        settingsController.playbackSpeedController.playbackSpeedPane.scrollPane.setVisible(true);
        settingsController.playbackSpeedController.playbackSpeedPane.scrollPane.setMouseTransparent(false);

        Timeline clipTimeline = new Timeline();
        clipTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(settingsController.clip.heightProperty(), settingsController.playbackSpeedController.playbackSpeedPane.scrollPane.getHeight())));

        TranslateTransition homeTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), settingsHome);
        homeTransition.setFromX(0);
        homeTransition.setToX(-settingsHome.getWidth());

        TranslateTransition optionsTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), settingsController.playbackSpeedController.playbackSpeedPane.scrollPane);
        optionsTransition.setFromX(settingsHome.getWidth());
        optionsTransition.setToX(0);


        ParallelTransition parallelTransition = new ParallelTransition(clipTimeline, homeTransition, optionsTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            settingsController.animating.set(false);
            settingsHome.setVisible(false);
            settingsHome.setMouseTransparent(true);
            settingsHome.setTranslateX(0);
            settingsController.clip.setHeight(settingsController.playbackSpeedController.playbackSpeedPane.scrollPane.getPrefHeight());
        });

        parallelTransition.play();
        settingsController.animating.set(true);

    }

    public void openCaptionsPane(){

    }

    public void openVideoChooser(){
        File selectedFile = fileChooser.showOpenDialog(App.stage);

        if (selectedFile != null) {

            MediaItem temp = null;

            if(Utilities.getFileExtension(selectedFile).equals("mp4")) temp = new Mp4Item(selectedFile);
            else if(Utilities.getFileExtension(selectedFile).equals("mp3")) temp = new Mp3Item(selectedFile);

            ActiveItem activeItem = new ActiveItem(temp, settingsController.menuController, settingsController.mediaInterface, settingsController.menuController.activeBox);
            activeItem.play(true);
        }
    }
}