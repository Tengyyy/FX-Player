package hans.Settings;

import hans.*;
import hans.Menu.QueueItem;
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
    SVGPath equalizerSVG = new SVGPath();

    SettingsHomeTab playbackOptionsTab, playbackSpeedTab, equalizerTab, videoSelectionTab;


    FileChooser fileChooser;

    SettingsHomeController(SettingsController settingsController){
        this.settingsController = settingsController;

        fileChooser = new FileChooser();
        fileChooser.setTitle("Select media");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All supported formats", "*.mp4", "*.avi", "*.mkv", "*.flv", "*.mov", "*.mp3", "*.flac", "*.wav", "*.ogg", "*.opus", "*.aiff", "*.m4a", "*.wma", "*.aac"),
                new FileChooser.ExtensionFilter("Videos", "*.mp4", "*.avi", "*.mkv", "*.flv", "*.mov"),
                new FileChooser.ExtensionFilter("Audio", "*.mp3", "*.flac", "*.wav", "*.ogg", "*.opus", "*.aiff", "*.m4a", "*.wma", "*.aac"));

        shuffleSVG.setContent(App.svgMap.get(SVG.SHUFFLE));
        repeatSVG.setContent(App.svgMap.get(SVG.REPEAT));
        repeatOnceSVG.setContent(App.svgMap.get(SVG.REPEAT_ONCE));
        magnifySVG.setContent(App.svgMap.get(SVG.MAGNIFY));
        speedSVG.setContent(App.svgMap.get(SVG.SPEED));
        equalizerSVG.setContent(App.svgMap.get(SVG.TUNE_VERTICAL));
        tuneSVG.setContent(App.svgMap.get(SVG.TUNE));

        settingsController.settingsPane.getChildren().add(settingsHome);
        settingsHome.setPrefSize(235, 156);
        settingsHome.setMaxSize(235, 156);
        settingsHome.setPadding(new Insets(8, 0, 8, 0));
        settingsHome.setAlignment(Pos.BOTTOM_CENTER);
        settingsHome.setVisible(false);
        settingsHome.setMouseTransparent(true);

        StackPane.setAlignment(settingsHome, Pos.BOTTOM_RIGHT);


        playbackOptionsTab = new SettingsHomeTab(this, false, tuneSVG, "Playback options", null);
        playbackSpeedTab = new SettingsHomeTab(this, true, speedSVG, "Playback Speed", "Normal");
        equalizerTab = new SettingsHomeTab(this, false, equalizerSVG, "Equalizer", null);
        videoSelectionTab = new SettingsHomeTab(this, false, magnifySVG, "Select a video", null);

        playbackOptionsTab.setOnMouseClicked((e) -> openPlaybackOptionsPane());
        playbackSpeedTab.setOnMouseClicked((e) -> openPlaybackSpeedPane());
        equalizerTab.setOnMouseClicked((e) -> openEqualizer());
        videoSelectionTab.setOnMouseClicked((e) -> openVideoChooser());

    }


    public void openPlaybackOptionsPane(){
        if(settingsController.animating.get()) return;

        settingsController.settingsState = SettingsState.PLAYBACK_OPTIONS_OPEN;

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

        settingsController.settingsState = SettingsState.PLAYBACK_SPEED_OPEN;

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


    public void openEqualizer(){
        if(settingsController.animating.get()) return;

        settingsController.settingsState = SettingsState.EQUALIZER_OPEN;

        settingsController.equalizerController.scrollPane.setVisible(true);
        settingsController.equalizerController.scrollPane.setMouseTransparent(false);

        Timeline clipHeightTimeline = new Timeline();
        clipHeightTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(settingsController.clip.heightProperty(), settingsController.equalizerController.scrollPane.getHeight())));

        Timeline clipWidthTimeline = new Timeline();
        clipWidthTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(settingsController.clip.widthProperty(), settingsController.equalizerController.scrollPane.getWidth())));

        TranslateTransition homeTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), settingsHome);
        homeTransition.setFromX(0);
        homeTransition.setToX(-settingsController.equalizerController.scrollPane.getWidth());

        TranslateTransition optionsTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), settingsController.equalizerController.scrollPane);
        optionsTransition.setFromX(settingsController.equalizerController.scrollPane.getWidth());
        optionsTransition.setToX(0);


        ParallelTransition parallelTransition = new ParallelTransition(clipHeightTimeline, clipWidthTimeline, homeTransition, optionsTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            settingsController.animating.set(false);
            settingsHome.setVisible(false);
            settingsHome.setMouseTransparent(true);
            settingsHome.setTranslateX(0);
            settingsController.clip.setHeight(settingsController.equalizerController.scrollPane.getPrefHeight());
        });

        parallelTransition.play();
        settingsController.animating.set(true);
    }


    public void openVideoChooser(){
        File selectedFile = fileChooser.showOpenDialog(App.stage);

        if (selectedFile != null) {
            QueueItem queueItem = new QueueItem(selectedFile, settingsController.menuController, settingsController.mediaInterface, 0);
            if(settingsController.menuController.queueBox.activeItem.get() != null) settingsController.menuController.queueBox.add(settingsController.menuController.queueBox.activeItem.get().videoIndex + 1, queueItem, false);
            else settingsController.menuController.queueBox.add(0, queueItem, false);
            queueItem.play();
        }
    }
}
