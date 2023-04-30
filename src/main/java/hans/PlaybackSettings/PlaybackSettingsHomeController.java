package hans.PlaybackSettings;

import hans.*;
import hans.Menu.Queue.QueueItem;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;

public class PlaybackSettingsHomeController {

    PlaybackSettingsController playbackSettingsController;

    VBox playbackSettingsHome = new VBox();

    SVGPath tuneSVG = new SVGPath();
    SVGPath shuffleSVG = new SVGPath();
    SVGPath repeatSVG = new SVGPath();
    SVGPath repeatOnceSVG = new SVGPath();
    SVGPath magnifySVG = new SVGPath();
    SVGPath speedSVG = new SVGPath();
    SVGPath equalizerSVG = new SVGPath();

    PlaybackSettingsHomeTab playbackOptionsTab, playbackSpeedTab, equalizerTab, videoSelectionTab;


    FileChooser fileChooser;

    PlaybackSettingsHomeController(PlaybackSettingsController playbackSettingsController){
        this.playbackSettingsController = playbackSettingsController;

        fileChooser = new FileChooser();
        fileChooser.setTitle("Select media");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All supported formats", "*.mp4", "*.avi", "*.mkv", "*.flv", "*.mov", "*.mp3", "*.flac", "*.wav", "*.ogg", "*.opus", "*.aiff", "*.m4a", "*.wma", "*.aac"),
                new FileChooser.ExtensionFilter("Videos", "*.mp4", "*.avi", "*.mkv", "*.flv", "*.mov"),
                new FileChooser.ExtensionFilter("Audio", "*.mp3", "*.flac", "*.wav", "*.ogg", "*.opus", "*.aiff", "*.m4a", "*.wma", "*.aac"));

        shuffleSVG.setContent(SVG.SHUFFLE.getContent());
        repeatSVG.setContent(SVG.REPEAT.getContent());
        repeatOnceSVG.setContent(SVG.REPEAT_ONCE.getContent());
        magnifySVG.setContent(SVG.MAGNIFY.getContent());
        speedSVG.setContent(SVG.SPEED.getContent());
        equalizerSVG.setContent(SVG.TUNE_VERTICAL.getContent());
        tuneSVG.setContent(SVG.TUNE.getContent());

        playbackSettingsController.playbackSettingsPane.getChildren().add(playbackSettingsHome);
        playbackSettingsHome.setPrefSize(235, 156);
        playbackSettingsHome.setMaxSize(235, 156);
        playbackSettingsHome.setPadding(new Insets(8, 0, 8, 0));
        playbackSettingsHome.setAlignment(Pos.BOTTOM_CENTER);
        playbackSettingsHome.setVisible(false);
        playbackSettingsHome.setMouseTransparent(true);

        StackPane.setAlignment(playbackSettingsHome, Pos.BOTTOM_RIGHT);


        playbackOptionsTab = new PlaybackSettingsHomeTab(this, false, tuneSVG, "Playback options", null);
        playbackSpeedTab = new PlaybackSettingsHomeTab(this, true, speedSVG, "Playback Speed", "Normal");
        equalizerTab = new PlaybackSettingsHomeTab(this, false, equalizerSVG, "Equalizer", null);
        videoSelectionTab = new PlaybackSettingsHomeTab(this, false, magnifySVG, "Select a file to play", null);

        playbackOptionsTab.setOnMouseClicked((e) -> openPlaybackOptionsPane());
        playbackSpeedTab.setOnMouseClicked((e) -> openPlaybackSpeedPane());
        equalizerTab.setOnMouseClicked((e) -> openEqualizer());
        videoSelectionTab.setOnMouseClicked((e) -> openVideoChooser());

    }


    public void openPlaybackOptionsPane(){
        if(playbackSettingsController.animating.get()) return;

        playbackSettingsController.playbackSettingsState = PlaybackSettingsState.PLAYBACK_OPTIONS_OPEN;

        playbackSettingsController.playbackOptionsController.playbackOptionsBox.setVisible(true);
        playbackSettingsController.playbackOptionsController.playbackOptionsBox.setMouseTransparent(false);

        Timeline clipTimeline = new Timeline();
        clipTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(playbackSettingsController.clip.heightProperty(), playbackSettingsController.playbackOptionsController.playbackOptionsBox.getHeight())));

        TranslateTransition homeTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), playbackSettingsHome);
        homeTransition.setFromX(0);
        homeTransition.setToX(-playbackSettingsHome.getWidth());

        TranslateTransition optionsTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), playbackSettingsController.playbackOptionsController.playbackOptionsBox);
        optionsTransition.setFromX(playbackSettingsHome.getWidth());
        optionsTransition.setToX(0);


        ParallelTransition parallelTransition = new ParallelTransition(clipTimeline, homeTransition, optionsTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            playbackSettingsController.animating.set(false);
            playbackSettingsHome.setVisible(false);
            playbackSettingsHome.setMouseTransparent(true);
            playbackSettingsHome.setTranslateX(0);
        });

        parallelTransition.play();
        playbackSettingsController.animating.set(true);

    }

    public void openPlaybackSpeedPane(){
        if(playbackSettingsController.animating.get()) return;

        playbackSettingsController.playbackSettingsState = PlaybackSettingsState.PLAYBACK_SPEED_OPEN;

        playbackSettingsController.playbackSpeedController.playbackSpeedPane.scrollPane.setVisible(true);
        playbackSettingsController.playbackSpeedController.playbackSpeedPane.scrollPane.setMouseTransparent(false);

        Timeline clipTimeline = new Timeline();
        clipTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(playbackSettingsController.clip.heightProperty(), playbackSettingsController.playbackSpeedController.playbackSpeedPane.scrollPane.getHeight())));

        TranslateTransition homeTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), playbackSettingsHome);
        homeTransition.setFromX(0);
        homeTransition.setToX(-playbackSettingsHome.getWidth());

        TranslateTransition optionsTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), playbackSettingsController.playbackSpeedController.playbackSpeedPane.scrollPane);
        optionsTransition.setFromX(playbackSettingsHome.getWidth());
        optionsTransition.setToX(0);


        ParallelTransition parallelTransition = new ParallelTransition(clipTimeline, homeTransition, optionsTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            playbackSettingsController.animating.set(false);
            playbackSettingsHome.setVisible(false);
            playbackSettingsHome.setMouseTransparent(true);
            playbackSettingsHome.setTranslateX(0);
            playbackSettingsController.clip.setHeight(playbackSettingsController.playbackSpeedController.playbackSpeedPane.scrollPane.getPrefHeight());
        });

        parallelTransition.play();
        playbackSettingsController.animating.set(true);

    }


    public void openEqualizer(){
        if(playbackSettingsController.animating.get()) return;

        playbackSettingsController.playbackSettingsState = PlaybackSettingsState.EQUALIZER_OPEN;

        playbackSettingsController.equalizerController.scrollPane.setVisible(true);
        playbackSettingsController.equalizerController.scrollPane.setMouseTransparent(false);

        Timeline clipHeightTimeline = new Timeline();
        clipHeightTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(playbackSettingsController.clip.heightProperty(), playbackSettingsController.equalizerController.scrollPane.getHeight())));

        Timeline clipWidthTimeline = new Timeline();
        clipWidthTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(playbackSettingsController.clip.widthProperty(), playbackSettingsController.equalizerController.scrollPane.getWidth())));

        TranslateTransition homeTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), playbackSettingsHome);
        homeTransition.setFromX(0);
        homeTransition.setToX(-playbackSettingsController.equalizerController.scrollPane.getWidth());

        TranslateTransition optionsTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), playbackSettingsController.equalizerController.scrollPane);
        optionsTransition.setFromX(playbackSettingsController.equalizerController.scrollPane.getWidth());
        optionsTransition.setToX(0);


        ParallelTransition parallelTransition = new ParallelTransition(clipHeightTimeline, clipWidthTimeline, homeTransition, optionsTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            playbackSettingsController.animating.set(false);
            playbackSettingsHome.setVisible(false);
            playbackSettingsHome.setMouseTransparent(true);
            playbackSettingsHome.setTranslateX(0);
            playbackSettingsController.clip.setHeight(playbackSettingsController.equalizerController.scrollPane.getPrefHeight());
        });

        parallelTransition.play();
        playbackSettingsController.animating.set(true);
    }


    public void openVideoChooser(){
        File selectedFile = fileChooser.showOpenDialog(App.stage);

        if (selectedFile != null) {
            QueueItem queueItem = new QueueItem(selectedFile, playbackSettingsController.menuController.queuePage, playbackSettingsController.menuController, playbackSettingsController.mediaInterface, 0);
            if(playbackSettingsController.menuController.queuePage.queueBox.activeItem.get() != null) playbackSettingsController.menuController.queuePage.queueBox.add(playbackSettingsController.menuController.queuePage.queueBox.activeItem.get().videoIndex + 1, queueItem, false);
            else playbackSettingsController.menuController.queuePage.queueBox.add(0, queueItem, false);
            queueItem.play();
        }
    }
}
