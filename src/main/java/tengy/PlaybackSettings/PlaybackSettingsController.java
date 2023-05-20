package tengy.PlaybackSettings;

import tengy.*;
import tengy.Subtitles.SubtitlesState;
import tengy.Menu.MenuController;
import tengy.Subtitles.SubtitlesController;
import tengy.Menu.MenuState;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import uk.co.caprica.vlcj.player.base.TrackDescription;

import java.util.List;

public class PlaybackSettingsController {

    MainController mainController;
    ControlBarController controlBarController;
    MenuController menuController;

    MediaInterface mediaInterface;

    PlaybackSettingsHomeController playbackSettingsHomeController;
    public PlaybackOptionsController playbackOptionsController;
    public PlaybackSpeedController playbackSpeedController;
    public EqualizerController equalizerController;
    public VideoTrackChooserController videoTrackChooserController;
    public AudioTrackChooserController audioTrackChooserController;
    SubtitlesController subtitlesController;

    public StackPane playbackSettingsBuffer = new StackPane();
    StackPane playbackSettingsPane = new StackPane();
    StackPane playbackSettingsBackground = new StackPane();


    Rectangle clip = new Rectangle();

    public BooleanProperty animating = new SimpleBooleanProperty(); // animating state of the settings pane


    public PlaybackSettingsState playbackSettingsState = PlaybackSettingsState.CLOSED;

    public static final int ANIMATION_SPEED = 200;

    public PlaybackSettingsController(MainController mainController, ControlBarController controlBarController, MenuController menuController){

        this.mainController = mainController;
        this.controlBarController = controlBarController;
        this.menuController = menuController;

        animating.set(false);

        playbackSettingsBuffer.setPrefSize(235, 156);
        playbackSettingsBuffer.setMaxWidth(550);
        playbackSettingsBuffer.setClip(clip);
        playbackSettingsBuffer.getChildren().add(playbackSettingsBackground);
        playbackSettingsBuffer.setMouseTransparent(true);
        playbackSettingsBackground.getStyleClass().add("settingsBackground");
        playbackSettingsBackground.setVisible(false);
        playbackSettingsBackground.setMouseTransparent(true);
        playbackSettingsBackground.setOpacity(0);
        StackPane.setMargin(playbackSettingsBuffer, new Insets(0, 20, 80, 0));
        StackPane.setAlignment(playbackSettingsBackground, Pos.BOTTOM_RIGHT);


        Platform.runLater(() -> {
            playbackSettingsBuffer.maxHeightProperty().bind(Bindings.min(Bindings.subtract(mainController.videoImageView.fitHeightProperty(), 120), 400));
            clip.setHeight(playbackSettingsHomeController.playbackSettingsHomeScroll.getHeight());
            clip.translateYProperty().bind(Bindings.subtract(playbackSettingsBuffer.heightProperty(), clip.heightProperty()));
            playbackSettingsBackground.maxHeightProperty().bind(clip.heightProperty());

            clip.setWidth(playbackSettingsHomeController.playbackSettingsHomeScroll.getWidth());
            clip.translateXProperty().bind(Bindings.subtract(playbackSettingsBuffer.widthProperty(), clip.widthProperty()));
            playbackSettingsBackground.maxWidthProperty().bind(clip.widthProperty());
        });

        playbackSettingsBuffer.setPickOnBounds(false);
        playbackSettingsBuffer.getChildren().add(playbackSettingsPane);
        StackPane.setAlignment(playbackSettingsBuffer, Pos.BOTTOM_RIGHT);

        playbackSettingsPane.setPrefSize(235, 156);

        playbackSettingsHomeController = new PlaybackSettingsHomeController(this);
        playbackOptionsController = new PlaybackOptionsController(this);
        playbackSpeedController = new PlaybackSpeedController(this);
        equalizerController = new EqualizerController(this);
        videoTrackChooserController = new VideoTrackChooserController(this);
        audioTrackChooserController = new AudioTrackChooserController(this);
    }


    public void init(MediaInterface mediaInterface, SubtitlesController subtitlesController){
        this.mediaInterface = mediaInterface;
        this.subtitlesController = subtitlesController;
    }

    public void openSettings(){

        if(animating.get() || controlBarController.volumeSlider.isValueChanging() || controlBarController.durationSlider.isValueChanging() || (menuController.menuState != MenuState.CLOSED && !menuController.extended.get())|| subtitlesController.subtitlesBox.subtitlesDragActive || subtitlesController.animating.get()) return;

        mainController.videoImageView.requestFocus();
        if(subtitlesController.subtitlesState != SubtitlesState.CLOSED) subtitlesController.closeSubtitles();

        AnimationsClass.rotateTransition(200, controlBarController.settingsIcon, 0, 45, false, 1, true);

        playbackSettingsState = PlaybackSettingsState.HOME_OPEN;

        mainController.sliderHoverBox.setVisible(false);

        controlBarController.subtitles.disableTooltip();
        controlBarController.settings.disableTooltip();
        controlBarController.fullScreen.disableTooltip();
        controlBarController.miniplayer.disableTooltip();


        playbackSettingsBuffer.setMouseTransparent(false);
        playbackSettingsBackground.setVisible(true);
        playbackSettingsBackground.setMouseTransparent(false);
        playbackSettingsHomeController.playbackSettingsHomeScroll.setVisible(true);
        playbackSettingsHomeController.playbackSettingsHomeScroll.setMouseTransparent(false);

        FadeTransition backgroundTranslate = new FadeTransition(Duration.millis(ANIMATION_SPEED), playbackSettingsBackground);
        backgroundTranslate.setFromValue(0);
        backgroundTranslate.setToValue(1);

        FadeTransition homeTranslate = new FadeTransition(Duration.millis(ANIMATION_SPEED), playbackSettingsHomeController.playbackSettingsHomeScroll);
        homeTranslate.setFromValue(0);
        homeTranslate.setToValue(1);

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, homeTranslate);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> animating.set(false));
        parallelTransition.play();
        animating.set(true);
    }

    public void closeSettings(){

        if(animating.get() || playbackSpeedController.customSpeedPane.customSpeedSlider.isValueChanging() || equalizerController.sliderActive) return;

        mainController.videoImageView.requestFocus();
        AnimationsClass.rotateTransition(200, controlBarController.settingsIcon, 45, 0, false, 1, true);

        controlBarController.settings.enableTooltip();
        controlBarController.miniplayer.enableTooltip();
        controlBarController.subtitles.enableTooltip();
        controlBarController.fullScreen.enableTooltip();

        if (controlBarController.settingsButtonHover) controlBarController.settings.mouseHover.set(true);
        else if (controlBarController.subtitlesButtonHover) controlBarController.subtitles.mouseHover.set(true);
        else if(controlBarController.miniplayerButtonHover) controlBarController.miniplayer.mouseHover.set(true);
        else if (controlBarController.fullScreenButtonHover) controlBarController.fullScreen.mouseHover.set(true);


        switch (playbackSettingsState) {
            case HOME_OPEN -> closeSettingsFromHome();
            case PLAYBACK_SPEED_OPEN -> closeSettingsFromPlaybackSpeed();
            case PLAYBACK_OPTIONS_OPEN -> closeSettingsFromPlaybackOptions();
            case CUSTOM_SPEED_OPEN -> closeSettingsFromCustomSpeed();
            case EQUALIZER_OPEN -> closeSettingsFromEqualizer();
            case VIDEO_TRACK_CHOOSER_OPEN -> closeSettingsFromVideoTrackChooser();
            case AUDIO_TRACK_CHOOSER_OPEN -> closeSettingsFromAudioTrackChooser();
        }

        playbackSettingsState = PlaybackSettingsState.CLOSED;

        if(controlBarController.durationSliderHover || controlBarController.durationSlider.isValueChanging()){
            mainController.sliderHoverBox.setVisible(true);
        }
    }


    public void closeSettingsFromHome(){
        FadeTransition backgroundTranslate = new FadeTransition(Duration.millis(ANIMATION_SPEED), playbackSettingsBackground);
        backgroundTranslate.setFromValue(1);
        backgroundTranslate.setToValue(0);

        FadeTransition homeTransition = new FadeTransition(Duration.millis(ANIMATION_SPEED), playbackSettingsHomeController.playbackSettingsHomeScroll);
        homeTransition.setFromValue(1);
        homeTransition.setToValue(0);

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, homeTransition);
        parallelTransition.setOnFinished((e) -> {
            animating.set(false);

            playbackSettingsBuffer.setMouseTransparent(true);
            playbackSettingsBackground.setVisible(false);
            playbackSettingsBackground.setMouseTransparent(true);
            playbackSettingsHomeController.playbackSettingsHomeScroll.setVisible(false);
            playbackSettingsHomeController.playbackSettingsHomeScroll.setOpacity(1);
            playbackSettingsHomeController.playbackSettingsHomeScroll.setMouseTransparent(true);
            playbackSettingsHomeController.playbackSettingsHomeScroll.setVvalue(0);
        });
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.play();
        animating.set(true);
    }

    public void closeSettingsFromPlaybackOptions(){
        FadeTransition backgroundTranslate = new FadeTransition(Duration.millis(ANIMATION_SPEED), playbackSettingsBackground);
        backgroundTranslate.setFromValue(1);
        backgroundTranslate.setToValue(0);

        FadeTransition playbackOptionsTransition = new FadeTransition(Duration.millis(ANIMATION_SPEED), playbackOptionsController.playbackOptionsBox);
        playbackOptionsTransition.setFromValue(1);
        playbackOptionsTransition.setToValue(0);

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, playbackOptionsTransition);
        parallelTransition.setOnFinished((e) -> {
            animating.set(false);

            playbackSettingsBuffer.setMouseTransparent(true);
            playbackSettingsBackground.setVisible(false);
            playbackSettingsBackground.setMouseTransparent(true);
            playbackOptionsController.playbackOptionsBox.setVisible(false);
             playbackOptionsController.playbackOptionsBox.setOpacity(1);
            playbackOptionsController.playbackOptionsBox.setMouseTransparent(true);
            clip.setHeight(playbackSettingsHomeController.playbackSettingsHomeScroll.getHeight());
        });

        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.play();
        animating.set(true);
    }

    public void closeSettingsFromPlaybackSpeed(){
        FadeTransition backgroundTranslate = new FadeTransition(Duration.millis(ANIMATION_SPEED), playbackSettingsBackground);
        backgroundTranslate.setFromValue(1);
        backgroundTranslate.setToValue(0);

        FadeTransition playbackSpeedTransition = new FadeTransition(Duration.millis(ANIMATION_SPEED), playbackSpeedController.playbackSpeedPane.scrollPane);
        playbackSpeedTransition.setFromValue(1);
        playbackSpeedTransition.setToValue(0);

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, playbackSpeedTransition);
        parallelTransition.setOnFinished((e) -> {
            animating.set(false);

            playbackSettingsBuffer.setMouseTransparent(true);
            playbackSettingsBackground.setVisible(false);
            playbackSettingsBackground.setMouseTransparent(true);
            playbackSpeedController.playbackSpeedPane.scrollPane.setVisible(false);
            playbackSpeedController.playbackSpeedPane.scrollPane.setMouseTransparent(true);
            playbackSpeedController.playbackSpeedPane.scrollPane.setOpacity(1);
            clip.setHeight(playbackSettingsHomeController.playbackSettingsHomeScroll.getHeight());
        });

        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.play();
        animating.set(true);
    }

    public void closeSettingsFromCustomSpeed(){
        FadeTransition backgroundTranslate = new FadeTransition(Duration.millis(ANIMATION_SPEED), playbackSettingsBackground);
        backgroundTranslate.setFromValue(1);
        backgroundTranslate.setToValue(0);

        FadeTransition customTransition = new FadeTransition(Duration.millis(ANIMATION_SPEED), playbackSpeedController.customSpeedPane.customSpeedBox);
        customTransition.setFromValue(1);
        customTransition.setToValue(0);

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, customTransition);
        parallelTransition.setOnFinished((e) -> {
            animating.set(false);

            playbackSettingsBuffer.setMouseTransparent(true);
            playbackSettingsBackground.setVisible(false);
            playbackSettingsBackground.setMouseTransparent(true);
            playbackSpeedController.customSpeedPane.customSpeedBox.setVisible(false);
            playbackSpeedController.customSpeedPane.customSpeedBox.setMouseTransparent(true);
            playbackSpeedController.customSpeedPane.customSpeedBox.setOpacity(1);
            clip.setHeight(playbackSettingsHomeController.playbackSettingsHomeScroll.getHeight());
        });

        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.play();
        animating.set(true);
    }

    public void closeSettingsFromEqualizer(){
        FadeTransition backgroundTranslate = new FadeTransition(Duration.millis(ANIMATION_SPEED), playbackSettingsBackground);
        backgroundTranslate.setFromValue(1);
        backgroundTranslate.setToValue(0);

        FadeTransition equalizerTransition = new FadeTransition(Duration.millis(ANIMATION_SPEED), equalizerController.scrollPane);
        equalizerTransition.setFromValue(1);
        equalizerTransition.setToValue(0);

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, equalizerTransition);
        parallelTransition.setOnFinished((e) -> {
            animating.set(false);

            playbackSettingsBuffer.setMouseTransparent(true);
            playbackSettingsBackground.setVisible(false);
            playbackSettingsBackground.setMouseTransparent(true);
            equalizerController.scrollPane.setVisible(false);
            equalizerController.scrollPane.setMouseTransparent(true);
            equalizerController.scrollPane.setOpacity(1);
            clip.setHeight(playbackSettingsHomeController.playbackSettingsHomeScroll.getHeight());
            clip.setWidth(playbackSettingsHomeController.playbackSettingsHomeScroll.getWidth());
        });

        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.play();
        animating.set(true);
    }

    public void closeSettingsFromVideoTrackChooser(){
        FadeTransition backgroundTranslate = new FadeTransition(Duration.millis(ANIMATION_SPEED), playbackSettingsBackground);
        backgroundTranslate.setFromValue(1);
        backgroundTranslate.setToValue(0);

        FadeTransition videoTrackChooserTransition = new FadeTransition(Duration.millis(ANIMATION_SPEED), videoTrackChooserController.scrollPane);
        videoTrackChooserTransition.setFromValue(1);
        videoTrackChooserTransition.setToValue(0);

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, videoTrackChooserTransition);
        parallelTransition.setOnFinished((e) -> {
            animating.set(false);

            playbackSettingsBuffer.setMouseTransparent(true);
            playbackSettingsBackground.setVisible(false);
            playbackSettingsBackground.setMouseTransparent(true);
            videoTrackChooserController.scrollPane.setVisible(false);
            videoTrackChooserController.scrollPane.setMouseTransparent(true);
            videoTrackChooserController.scrollPane.setOpacity(1);
            clip.setHeight(playbackSettingsHomeController.playbackSettingsHomeScroll.getHeight());
            clip.setWidth(playbackSettingsHomeController.playbackSettingsHomeScroll.getWidth());
        });

        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.play();
        animating.set(true);
    }

    //todo: implement
    public void closeSettingsFromAudioTrackChooser(){
        FadeTransition backgroundTranslate = new FadeTransition(Duration.millis(ANIMATION_SPEED), playbackSettingsBackground);
        backgroundTranslate.setFromValue(1);
        backgroundTranslate.setToValue(0);

        FadeTransition audioTrackChooserTransition = new FadeTransition(Duration.millis(ANIMATION_SPEED), audioTrackChooserController.scrollPane);
        audioTrackChooserTransition.setFromValue(1);
        audioTrackChooserTransition.setToValue(0);

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, audioTrackChooserTransition);
        parallelTransition.setOnFinished((e) -> {
            animating.set(false);

            playbackSettingsBuffer.setMouseTransparent(true);
            playbackSettingsBackground.setVisible(false);
            playbackSettingsBackground.setMouseTransparent(true);
            audioTrackChooserController.scrollPane.setVisible(false);
            audioTrackChooserController.scrollPane.setMouseTransparent(true);
            audioTrackChooserController.scrollPane.setOpacity(1);
            clip.setHeight(playbackSettingsHomeController.playbackSettingsHomeScroll.getHeight());
            clip.setWidth(playbackSettingsHomeController.playbackSettingsHomeScroll.getWidth());
        });

        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.play();
        animating.set(true);
    }


    public void initializeVideoTrackPage(List<TrackDescription> trackDescriptions, int activeTrack){
        playbackSettingsHomeController.addVideoTrackTab();

        videoTrackChooserController.initializeTracks(trackDescriptions, activeTrack);
    }


    public void initializeAudioTrackPage(List<TrackDescription> trackDescriptions, int activeTrack){
        playbackSettingsHomeController.addAudioTrackTab();

        audioTrackChooserController.initializeTracks(trackDescriptions, activeTrack);
    }

    public void resetTrackPages(){
        playbackSettingsHomeController.removeVideoAudioTrackTabs();

        videoTrackChooserController.clearTracks();
        audioTrackChooserController.clearTracks();
    }

    public void handleFocusForward() {
        switch (playbackSettingsState){
            case VIDEO_TRACK_CHOOSER_OPEN -> videoTrackChooserController.focusForward();
            case HOME_OPEN -> playbackSettingsHomeController.focusForward();
            case CUSTOM_SPEED_OPEN -> playbackSpeedController.customSpeedPane.focusForward();
            case AUDIO_TRACK_CHOOSER_OPEN -> audioTrackChooserController.focusForward();
            case PLAYBACK_SPEED_OPEN -> playbackSpeedController.playbackSpeedPane.focusForward();
            case EQUALIZER_OPEN -> equalizerController.focusForward();
            case PLAYBACK_OPTIONS_OPEN -> playbackOptionsController.focusForward();
        }
    }

    public void handleFocusBackward() {
        switch (playbackSettingsState){
            case VIDEO_TRACK_CHOOSER_OPEN -> videoTrackChooserController.focusBackward();
            case HOME_OPEN -> playbackSettingsHomeController.focusBackward();
            case CUSTOM_SPEED_OPEN -> playbackSpeedController.customSpeedPane.focusBackward();
            case AUDIO_TRACK_CHOOSER_OPEN -> audioTrackChooserController.focusBackward();
            case PLAYBACK_SPEED_OPEN -> playbackSpeedController.playbackSpeedPane.focusBackward();
            case EQUALIZER_OPEN -> equalizerController.focusBackward();
            case PLAYBACK_OPTIONS_OPEN -> playbackOptionsController.focusBackward();
        }
    }
}