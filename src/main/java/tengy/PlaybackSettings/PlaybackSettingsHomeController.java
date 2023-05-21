package tengy.PlaybackSettings;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;
import tengy.*;
import tengy.Menu.Queue.QueueItem;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static tengy.Utilities.keyboardFocusOn;

public class PlaybackSettingsHomeController {

    PlaybackSettingsController playbackSettingsController;

    ScrollPane playbackSettingsHomeScroll = new ScrollPane();
    VBox playbackSettingsHomeBox = new VBox();


    SVGPath videoSVG = new SVGPath();
    SVGPath audioSVG = new SVGPath();
    SVGPath tuneSVG = new SVGPath();
    SVGPath shuffleSVG = new SVGPath();
    SVGPath repeatSVG = new SVGPath();
    SVGPath repeatOnceSVG = new SVGPath();
    SVGPath magnifySVG = new SVGPath();
    SVGPath speedSVG = new SVGPath();
    SVGPath equalizerSVG = new SVGPath();

    PlaybackSettingsHomeTab videoTrackTab, audioTrackTab, playbackOptionsTab, playbackSpeedTab, equalizerTab, videoSelectionTab;


    FileChooser fileChooser;

    List<Node> focusNodes = new ArrayList<>();
    IntegerProperty focus = new SimpleIntegerProperty(-1);

    PlaybackSettingsHomeController(PlaybackSettingsController playbackSettingsController){
        this.playbackSettingsController = playbackSettingsController;

        fileChooser = new FileChooser();
        fileChooser.setTitle("Select media");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All supported formats", "*.mp4", "*.avi", "*.mkv", "*.flv", "*.mov", "*.mp3", "*.flac", "*.wav", "*.ogg", "*.opus", "*.aiff", "*.m4a", "*.wma", "*.aac"),
                new FileChooser.ExtensionFilter("Videos", "*.mp4", "*.avi", "*.mkv", "*.flv", "*.mov"),
                new FileChooser.ExtensionFilter("Audio", "*.mp3", "*.flac", "*.wav", "*.ogg", "*.opus", "*.aiff", "*.m4a", "*.wma", "*.aac"));

        videoSVG.setContent(SVG.VIDEO.getContent());
        audioSVG.setContent(SVG.AUDIO.getContent());
        shuffleSVG.setContent(SVG.SHUFFLE.getContent());
        repeatSVG.setContent(SVG.REPEAT.getContent());
        repeatOnceSVG.setContent(SVG.REPEAT_ONCE.getContent());
        magnifySVG.setContent(SVG.MAGNIFY.getContent());
        speedSVG.setContent(SVG.SPEED.getContent());
        equalizerSVG.setContent(SVG.TUNE_VERTICAL.getContent());
        tuneSVG.setContent(SVG.TUNE.getContent());

        playbackSettingsHomeScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        playbackSettingsHomeScroll.getStyleClass().add("settingsScroll");
        playbackSettingsHomeScroll.setPrefSize(235, 159);
        playbackSettingsHomeScroll.setMaxSize(235, 159);
        playbackSettingsHomeScroll.setContent(playbackSettingsHomeBox);
        playbackSettingsHomeScroll.setVisible(false);
        playbackSettingsHomeScroll.setMouseTransparent(true);
        playbackSettingsHomeScroll.setFitToWidth(true);

        StackPane.setAlignment(playbackSettingsHomeScroll, Pos.BOTTOM_RIGHT);

        playbackSettingsController.playbackSettingsPane.getChildren().add(playbackSettingsHomeScroll);


        playbackSettingsHomeBox.setPrefSize(235, 156);
        playbackSettingsHomeBox.setMaxSize(235, 156);
        playbackSettingsHomeBox.setPadding(new Insets(8, 0, 8, 0));
        playbackSettingsHomeBox.setAlignment(Pos.BOTTOM_CENTER);


        videoTrackTab = new PlaybackSettingsHomeTab(this, false, videoSVG, "Change video track", null, this::openVideoTrackChooser);
        audioTrackTab = new PlaybackSettingsHomeTab(this, false, audioSVG, "Change audio track", null, this::openAudioTrackChooser);
        playbackOptionsTab = new PlaybackSettingsHomeTab(this, false, tuneSVG, "Playback options", null, this::openPlaybackOptionsPane);
        playbackSpeedTab = new PlaybackSettingsHomeTab(this, true, speedSVG, "Playback Speed", "Normal", this::openPlaybackSpeedPane);
        equalizerTab = new PlaybackSettingsHomeTab(this, false, equalizerSVG, "Equalizer", null, this::openEqualizer);
        videoSelectionTab = new PlaybackSettingsHomeTab(this, false, magnifySVG, "Select a file to play", null, this::openVideoChooser);

        playbackSettingsHomeBox.getChildren().addAll(playbackOptionsTab, playbackSpeedTab, equalizerTab, videoSelectionTab);

        focusNodes.add(playbackOptionsTab);
        focusNodes.add(playbackSpeedTab);
        focusNodes.add(equalizerTab);
        focusNodes.add(videoSelectionTab);
    }


    public void openVideoTrackChooser(){
        if(playbackSettingsController.animating.get()) return;

        playbackSettingsController.playbackSettingsState = PlaybackSettingsState.VIDEO_TRACK_CHOOSER_OPEN;

        playbackSettingsController.videoTrackChooserController.scrollPane.setVisible(true);
        playbackSettingsController.videoTrackChooserController.scrollPane.setMouseTransparent(false);

        Timeline clipTimeline = new Timeline();
        clipTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(playbackSettingsController.clip.heightProperty(), playbackSettingsController.videoTrackChooserController.scrollPane.getHeight())));

        TranslateTransition homeTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), playbackSettingsHomeScroll);
        homeTransition.setFromX(0);
        homeTransition.setToX(-playbackSettingsHomeScroll.getWidth());

        TranslateTransition videoTrackChooserTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), playbackSettingsController.videoTrackChooserController.scrollPane);
        videoTrackChooserTransition.setFromX(playbackSettingsHomeScroll.getWidth());
        videoTrackChooserTransition.setToX(0);


        ParallelTransition parallelTransition = new ParallelTransition(clipTimeline, homeTransition, videoTrackChooserTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            playbackSettingsController.animating.set(false);
            playbackSettingsHomeScroll.setVisible(false);
            playbackSettingsHomeScroll.setMouseTransparent(true);
            playbackSettingsHomeScroll.setTranslateX(0);
            playbackSettingsController.clip.setHeight(playbackSettingsController.videoTrackChooserController.scrollPane.getPrefHeight());
        });

        parallelTransition.play();
        playbackSettingsController.animating.set(true);

    }


    public void openAudioTrackChooser(){
        if(playbackSettingsController.animating.get()) return;

        playbackSettingsController.playbackSettingsState = PlaybackSettingsState.AUDIO_TRACK_CHOOSER_OPEN;

        playbackSettingsController.audioTrackChooserController.scrollPane.setVisible(true);
        playbackSettingsController.audioTrackChooserController.scrollPane.setMouseTransparent(false);

        Timeline clipTimeline = new Timeline();
        clipTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(playbackSettingsController.clip.heightProperty(), playbackSettingsController.audioTrackChooserController.scrollPane.getHeight())));

        TranslateTransition homeTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), playbackSettingsHomeScroll);
        homeTransition.setFromX(0);
        homeTransition.setToX(-playbackSettingsHomeScroll.getWidth());

        TranslateTransition audioTrackChooserTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), playbackSettingsController.audioTrackChooserController.scrollPane);
        audioTrackChooserTransition.setFromX(playbackSettingsHomeScroll.getWidth());
        audioTrackChooserTransition.setToX(0);


        ParallelTransition parallelTransition = new ParallelTransition(clipTimeline, homeTransition, audioTrackChooserTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            playbackSettingsController.animating.set(false);
            playbackSettingsHomeScroll.setVisible(false);
            playbackSettingsHomeScroll.setMouseTransparent(true);
            playbackSettingsHomeScroll.setTranslateX(0);
            playbackSettingsController.clip.setHeight(playbackSettingsController.audioTrackChooserController.scrollPane.getPrefHeight());
        });

        parallelTransition.play();
        playbackSettingsController.animating.set(true);

    }


    public void openPlaybackOptionsPane(){
        if(playbackSettingsController.animating.get()) return;

        playbackSettingsController.playbackSettingsState = PlaybackSettingsState.PLAYBACK_OPTIONS_OPEN;

        playbackSettingsController.playbackOptionsController.playbackOptionsBox.setVisible(true);
        playbackSettingsController.playbackOptionsController.playbackOptionsBox.setMouseTransparent(false);

        Timeline clipTimeline = new Timeline();
        clipTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(playbackSettingsController.clip.heightProperty(), playbackSettingsController.playbackOptionsController.playbackOptionsBox.getHeight())));

        TranslateTransition homeTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), playbackSettingsHomeScroll);
        homeTransition.setFromX(0);
        homeTransition.setToX(-playbackSettingsHomeScroll.getWidth());

        TranslateTransition optionsTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), playbackSettingsController.playbackOptionsController.playbackOptionsBox);
        optionsTransition.setFromX(playbackSettingsHomeScroll.getWidth());
        optionsTransition.setToX(0);


        ParallelTransition parallelTransition = new ParallelTransition(clipTimeline, homeTransition, optionsTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            playbackSettingsController.animating.set(false);
            playbackSettingsHomeScroll.setVisible(false);
            playbackSettingsHomeScroll.setMouseTransparent(true);
            playbackSettingsHomeScroll.setTranslateX(0);
        });

        parallelTransition.play();
        playbackSettingsController.animating.set(true);

    }

    public void openPlaybackSpeedPane(){
        if(playbackSettingsController.animating.get()) return;

        playbackSettingsController.playbackSettingsState = PlaybackSettingsState.PLAYBACK_SPEED_OPEN;

        playbackSettingsController.playbackSpeedController.playbackSpeedPane.scrollPane.setVisible(true);
        playbackSettingsController.playbackSpeedController.playbackSpeedPane.scrollPane.setMouseTransparent(false);

        Timeline clipHeightTimeline = new Timeline();
        clipHeightTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(playbackSettingsController.clip.heightProperty(), playbackSettingsController.playbackSpeedController.playbackSpeedPane.scrollPane.getHeight())));

        Timeline clipWidthTimeline = new Timeline();
        clipWidthTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(playbackSettingsController.clip.widthProperty(), playbackSettingsController.playbackSpeedController.playbackSpeedPane.scrollPane.getWidth())));

        TranslateTransition homeTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), playbackSettingsHomeScroll);
        homeTransition.setFromX(0);
        homeTransition.setToX(-playbackSettingsHomeScroll.getWidth());

        TranslateTransition optionsTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), playbackSettingsController.playbackSpeedController.playbackSpeedPane.scrollPane);
        optionsTransition.setFromX(playbackSettingsHomeScroll.getWidth());
        optionsTransition.setToX(0);


        ParallelTransition parallelTransition = new ParallelTransition(clipWidthTimeline, clipHeightTimeline, homeTransition, optionsTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            playbackSettingsController.animating.set(false);
            playbackSettingsHomeScroll.setVisible(false);
            playbackSettingsHomeScroll.setMouseTransparent(true);
            playbackSettingsHomeScroll.setTranslateX(0);
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

        TranslateTransition homeTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), playbackSettingsHomeScroll);
        homeTransition.setFromX(0);
        homeTransition.setToX(-playbackSettingsController.equalizerController.scrollPane.getWidth());

        TranslateTransition optionsTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), playbackSettingsController.equalizerController.scrollPane);
        optionsTransition.setFromX(playbackSettingsController.equalizerController.scrollPane.getWidth());
        optionsTransition.setToX(0);


        ParallelTransition parallelTransition = new ParallelTransition(clipHeightTimeline, clipWidthTimeline, homeTransition, optionsTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            playbackSettingsController.animating.set(false);
            playbackSettingsHomeScroll.setVisible(false);
            playbackSettingsHomeScroll.setMouseTransparent(true);
            playbackSettingsHomeScroll.setTranslateX(0);
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

    public void addVideoTrackTab(){

        if(playbackSettingsHomeBox.getChildren().contains(videoTrackTab)) return;

        double currHeight = playbackSettingsHomeBox.getPrefHeight();

        playbackSettingsHomeBox.setPrefHeight(currHeight + 35);
        playbackSettingsHomeBox.setMaxHeight(currHeight + 35);

        playbackSettingsHomeScroll.setPrefHeight(currHeight + 38);
        playbackSettingsHomeScroll.setMaxHeight(currHeight + 38);

        if(playbackSettingsController.playbackSettingsState == PlaybackSettingsState.CLOSED || playbackSettingsController.playbackSettingsState == PlaybackSettingsState.HOME_OPEN) playbackSettingsController.clip.setHeight(currHeight + 38);

        playbackSettingsHomeBox.getChildren().add(0, videoTrackTab);

        if(!focusNodes.contains(videoTrackTab)){
            focusNodes.add(0, videoTrackTab);
            updateFocus();
        }
    }

    public void addAudioTrackTab(){

        if(playbackSettingsHomeBox.getChildren().contains(audioTrackTab)) return;

        double currHeight = playbackSettingsHomeBox.getPrefHeight();

        playbackSettingsHomeBox.setPrefHeight(currHeight + 35);
        playbackSettingsHomeBox.setMaxHeight(currHeight + 35);

        playbackSettingsHomeScroll.setPrefHeight(currHeight + 38);
        playbackSettingsHomeScroll.setMaxHeight(currHeight + 38);

        if(playbackSettingsController.playbackSettingsState == PlaybackSettingsState.CLOSED || playbackSettingsController.playbackSettingsState == PlaybackSettingsState.HOME_OPEN) playbackSettingsController.clip.setHeight(currHeight + 38);

        playbackSettingsHomeBox.getChildren().add(0, audioTrackTab);

        if(!focusNodes.contains(audioTrackTab)){
            focusNodes.add(0, audioTrackTab);
            updateFocus();
        }
    }

    public void removeVideoAudioTrackTabs(){

        boolean videoTrackTabRemoved = playbackSettingsHomeBox.getChildren().remove(videoTrackTab);

        if(videoTrackTabRemoved){
            double currHeight = playbackSettingsHomeBox.getPrefHeight();

            playbackSettingsHomeBox.setPrefHeight(currHeight - 35);
            playbackSettingsHomeBox.setMaxHeight(currHeight - 35);

            playbackSettingsHomeScroll.setPrefHeight(currHeight - 32);
            playbackSettingsHomeScroll.setMaxHeight(currHeight - 32);

            if(playbackSettingsController.playbackSettingsState == PlaybackSettingsState.CLOSED || playbackSettingsController.playbackSettingsState == PlaybackSettingsState.HOME_OPEN) playbackSettingsController.clip.setHeight(currHeight - 32);
        }

        boolean audioTrackTabRemoved = playbackSettingsHomeBox.getChildren().remove(audioTrackTab);

        if(audioTrackTabRemoved){
            double currHeight = playbackSettingsHomeBox.getPrefHeight();

            playbackSettingsHomeBox.setPrefHeight(currHeight - 35);
            playbackSettingsHomeBox.setMaxHeight(currHeight - 35);

            playbackSettingsHomeScroll.setPrefHeight(currHeight - 32);
            playbackSettingsHomeScroll.setMaxHeight(currHeight - 32);

            if(playbackSettingsController.playbackSettingsState == PlaybackSettingsState.CLOSED || playbackSettingsController.playbackSettingsState == PlaybackSettingsState.HOME_OPEN) playbackSettingsController.clip.setHeight(currHeight - 32);
        }

        focusNodes.remove(videoTrackTab);
        focusNodes.remove(audioTrackTab);

        updateFocus();
    }

    public void focusForward(){
        int newFocus;

        if(focus.get() >= focusNodes.size() - 1 || focus.get() == -1) newFocus = 0;
        else newFocus = focus.get() + 1;

        keyboardFocusOn(focusNodes.get(newFocus));
    }

    public void focusBackward(){
        int newFocus;

        if(focus.get() == 0) newFocus = focusNodes.size() - 1;
        else if(focus.get() == -1) newFocus = 0;
        else newFocus = focus.get() - 1;

        keyboardFocusOn(focusNodes.get(newFocus));
    }

    private void updateFocus(){
        for(int i=0; i < focusNodes.size(); i++){
            if(focusNodes.get(i).isFocused()){
                focus.set(i);
                break;
            }
        }
    }
}
