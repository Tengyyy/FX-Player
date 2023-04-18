package hans.PlaybackSettings;

import hans.*;
import hans.Subtitles.SubtitlesState;
import hans.Menu.MenuController;
import hans.Subtitles.SubtitlesController;
import hans.Menu.MenuState;
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

public class PlaybackSettingsController {

    MainController mainController;
    ControlBarController controlBarController;
    MenuController menuController;

    MediaInterface mediaInterface;

    PlaybackSettingsHomeController playbackSettingsHomeController;
    public PlaybackOptionsController playbackOptionsController;
    public PlaybackSpeedController playbackSpeedController;
    public EqualizerController equalizerController;
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
            clip.setHeight(playbackSettingsHomeController.playbackSettingsHome.getHeight());
            clip.translateYProperty().bind(Bindings.subtract(playbackSettingsBuffer.heightProperty(), clip.heightProperty()));
            playbackSettingsBackground.maxHeightProperty().bind(clip.heightProperty());

            clip.setWidth(playbackSettingsHomeController.playbackSettingsHome.getWidth());
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

        mainController.sliderHoverLabel.timeLabel.setVisible(false);
        mainController.sliderHoverLabel.chapterlabel.setVisible(false);
        mainController.sliderHoverPreview.pane.setVisible(false);

        if(controlBarController.subtitles.isShowing()) controlBarController.subtitles.hide();
        if(controlBarController.settings.isShowing()) controlBarController.settings.hide();
        if(controlBarController.miniplayer.isShowing()) controlBarController.miniplayer.hide();
        if(controlBarController.fullScreen.isShowing()) controlBarController.fullScreen.hide();


        controlBarController.subtitlesButton.setOnMouseEntered(null);
        controlBarController.settingsButton.setOnMouseEntered(null);
        controlBarController.miniplayerButton.setOnMouseEntered(null);
        controlBarController.fullScreenButton.setOnMouseEntered(null);


        playbackSettingsBuffer.setMouseTransparent(false);
        playbackSettingsBackground.setVisible(true);
        playbackSettingsBackground.setMouseTransparent(false);
        playbackSettingsHomeController.playbackSettingsHome.setVisible(true);
        playbackSettingsHomeController.playbackSettingsHome.setMouseTransparent(false);

        FadeTransition backgroundTranslate = new FadeTransition(Duration.millis(ANIMATION_SPEED), playbackSettingsBackground);
        backgroundTranslate.setFromValue(0);
        backgroundTranslate.setToValue(1);

        FadeTransition homeTranslate = new FadeTransition(Duration.millis(ANIMATION_SPEED), playbackSettingsHomeController.playbackSettingsHome);
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


        if (controlBarController.settingsButtonHover) {
            controlBarController.settings = new ControlTooltip(mainController, " Playback settings (s)", controlBarController.settingsButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
            controlBarController.settings.showTooltip();

            controlBarController.miniplayer = new ControlTooltip(mainController, "Miniplayer (i)", controlBarController.miniplayerButton, 0, TooltipType.CONTROLBAR_TOOLTIP);

            controlBarController.subtitles = new ControlTooltip(mainController,"Subtitles (c)", controlBarController.subtitlesButton, 0, TooltipType.CONTROLBAR_TOOLTIP);

            if (App.fullScreen)
                controlBarController.fullScreen = new ControlTooltip(mainController,"Exit full screen (f)", controlBarController.fullScreenButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
            else
                controlBarController.fullScreen = new ControlTooltip(mainController,"Full screen (f)", controlBarController.fullScreenButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
        }
        else if (controlBarController.subtitlesButtonHover) {
            controlBarController.subtitles = new ControlTooltip(mainController,"Subtitles (c)", controlBarController.subtitlesButton, 0, TooltipType.CONTROLBAR_TOOLTIP);

            controlBarController.subtitles.showTooltip();

            controlBarController.miniplayer = new ControlTooltip(mainController,"Miniplayer (i)", controlBarController.miniplayerButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
            controlBarController.settings = new ControlTooltip(mainController,"Playback settings (s)", controlBarController.settingsButton, 0, TooltipType.CONTROLBAR_TOOLTIP);

            if (App.fullScreen)
                controlBarController.fullScreen = new ControlTooltip(mainController,"Exit full screen (f)", controlBarController.fullScreenButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
            else
                controlBarController.fullScreen = new ControlTooltip(mainController,"Full screen (f)", controlBarController.fullScreenButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
        }
        else if(controlBarController.miniplayerButtonHover){
            if (App.fullScreen) {
                controlBarController.fullScreen = new ControlTooltip(mainController,"Exit full screen (f)", controlBarController.fullScreenButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
            } else {
                controlBarController.fullScreen = new ControlTooltip(mainController,"Full screen (f)", controlBarController.fullScreenButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
            }

            controlBarController.subtitles = new ControlTooltip(mainController,"Subtitles (c)", controlBarController.subtitlesButton, 0, TooltipType.CONTROLBAR_TOOLTIP);

            controlBarController.miniplayer = new ControlTooltip(mainController,"Miniplayer (i)", controlBarController.miniplayerButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
            controlBarController.miniplayer.showTooltip();

            controlBarController.settings = new ControlTooltip(mainController,"Playback settings (s)", controlBarController.settingsButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
        }
        else if (controlBarController.fullScreenButtonHover) {
            if (App.fullScreen) {
                controlBarController.fullScreen = new ControlTooltip(mainController,"Exit full screen (f)", controlBarController.fullScreenButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
            } else {
                controlBarController.fullScreen = new ControlTooltip(mainController,"Full screen (f)", controlBarController.fullScreenButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
            }
            controlBarController.fullScreen.showTooltip();

            controlBarController.subtitles = new ControlTooltip(mainController,"Subtitles (c)", controlBarController.subtitlesButton, 0, TooltipType.CONTROLBAR_TOOLTIP);

            controlBarController.miniplayer = new ControlTooltip(mainController,"Miniplayer (i)", controlBarController.miniplayerButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
            controlBarController.settings = new ControlTooltip(mainController,"Playback settings (s)", controlBarController.settingsButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
        }
        else {
            controlBarController.subtitles = new ControlTooltip(mainController,"Subtitles (c)", controlBarController.subtitlesButton, 0, TooltipType.CONTROLBAR_TOOLTIP);

            controlBarController.settings = new ControlTooltip(mainController,"Playback settings (s)", controlBarController.settingsButton, 0, TooltipType.CONTROLBAR_TOOLTIP);

            controlBarController.miniplayer = new ControlTooltip(mainController,"Miniplayer (i)", controlBarController.miniplayerButton, 0, TooltipType.CONTROLBAR_TOOLTIP);

            if (App.fullScreen)
                controlBarController.fullScreen = new ControlTooltip(mainController,"Exit full screen (f)", controlBarController.fullScreenButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
            else
                controlBarController.fullScreen = new ControlTooltip(mainController,"Full screen (f)", controlBarController.fullScreenButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
        }

        switch (playbackSettingsState) {
            case HOME_OPEN -> closeSettingsFromHome();
            case PLAYBACK_SPEED_OPEN -> closeSettingsFromPlaybackSpeed();
            case PLAYBACK_OPTIONS_OPEN -> closeSettingsFromPlaybackOptions();
            case CUSTOM_SPEED_OPEN -> closeSettingsFromCustomSpeed();
            case EQUALIZER_OPEN -> closeSettingsFromEqualizer();
            default -> {
            }
        }

        playbackSettingsState = PlaybackSettingsState.CLOSED;

        if(controlBarController.durationSliderHover || controlBarController.durationSlider.isValueChanging()){
            mainController.sliderHoverLabel.timeLabel.setVisible(true);
            if(mainController.chapterController.activeChapter != -1) mainController.sliderHoverLabel.chapterlabel.setVisible(true);
            if(menuController.queuePage.queueBox.activeIndex.get() != -1 && menuController.queuePage.queueBox.queue.get(menuController.queuePage.queueBox.queueOrder.get(menuController.queuePage.queueBox.activeIndex.get())).getMediaItem() != null && menuController.queuePage.queueBox.queue.get(menuController.queuePage.queueBox.queueOrder.get(menuController.queuePage.queueBox.activeIndex.get())).getMediaItem().hasVideo()) mainController.sliderHoverPreview.pane.setVisible(true);
        }

    }


    public void closeSettingsFromHome(){
        FadeTransition backgroundTranslate = new FadeTransition(Duration.millis(ANIMATION_SPEED), playbackSettingsBackground);
        backgroundTranslate.setFromValue(1);
        backgroundTranslate.setToValue(0);

        FadeTransition homeTransition = new FadeTransition(Duration.millis(ANIMATION_SPEED), playbackSettingsHomeController.playbackSettingsHome);
        homeTransition.setFromValue(1);
        homeTransition.setToValue(0);

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, homeTransition);
        parallelTransition.setOnFinished((e) -> {
            animating.set(false);

            playbackSettingsBuffer.setMouseTransparent(true);
            playbackSettingsBackground.setVisible(false);
            playbackSettingsBackground.setMouseTransparent(true);
            playbackSettingsHomeController.playbackSettingsHome.setVisible(false);
            playbackSettingsHomeController.playbackSettingsHome.setOpacity(1);
            playbackSettingsHomeController.playbackSettingsHome.setMouseTransparent(true);
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
            clip.setHeight(playbackSettingsHomeController.playbackSettingsHome.getHeight());
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
            clip.setHeight(playbackSettingsHomeController.playbackSettingsHome.getHeight());
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
            clip.setHeight(playbackSettingsHomeController.playbackSettingsHome.getHeight());
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
            clip.setHeight(playbackSettingsHomeController.playbackSettingsHome.getHeight());
            clip.setWidth(playbackSettingsHomeController.playbackSettingsHome.getWidth());
        });

        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.play();
        animating.set(true);
    }

}