package hans.Settings;

import hans.*;
import hans.Captions.CaptionsState;
import hans.Menu.MenuController;
import hans.Captions.CaptionsController;
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

public class SettingsController {

    MainController mainController;
    ControlBarController controlBarController;
    MenuController menuController;

    MediaInterface mediaInterface;

    SettingsHomeController settingsHomeController;
    public PlaybackOptionsController playbackOptionsController;
    public PlaybackSpeedController playbackSpeedController;
    public EqualizerController equalizerController;
    CaptionsController captionsController;

    public StackPane settingsBuffer = new StackPane();
    StackPane settingsPane = new StackPane();
    StackPane settingsBackground = new StackPane();


    Rectangle clip = new Rectangle();

    public BooleanProperty animating = new SimpleBooleanProperty(); // animating state of the settings pane


    public SettingsState settingsState = SettingsState.CLOSED;

    public static final int ANIMATION_SPEED = 200;

    public SettingsController(MainController mainController, ControlBarController controlBarController, MenuController menuController){

        this.mainController = mainController;
        this.controlBarController = controlBarController;
        this.menuController = menuController;

        animating.set(false);

        settingsBuffer.setPrefSize(235, 156);
        settingsBuffer.setMaxWidth(550);
        settingsBuffer.setClip(clip);
        settingsBuffer.getChildren().add(settingsBackground);
        settingsBuffer.setMouseTransparent(true);
        settingsBackground.getStyleClass().add("settingsBackground");
        settingsBackground.setVisible(false);
        settingsBackground.setMouseTransparent(true);
        settingsBackground.setOpacity(0);
        StackPane.setMargin(settingsBuffer, new Insets(0, 20, 80, 0));
        StackPane.setAlignment(settingsBackground, Pos.BOTTOM_RIGHT);


        Platform.runLater(() -> {
            settingsBuffer.maxHeightProperty().bind(Bindings.min(Bindings.subtract(mainController.videoImageView.fitHeightProperty(), 120), 400));
            clip.setHeight(settingsHomeController.settingsHome.getHeight());
            clip.translateYProperty().bind(Bindings.subtract(settingsBuffer.heightProperty(), clip.heightProperty()));
            settingsBackground.maxHeightProperty().bind(clip.heightProperty());

            clip.setWidth(settingsHomeController.settingsHome.getWidth());
            clip.translateXProperty().bind(Bindings.subtract(settingsBuffer.widthProperty(), clip.widthProperty()));
            settingsBackground.maxWidthProperty().bind(clip.widthProperty());
        });

        settingsBuffer.setPickOnBounds(false);
        settingsBuffer.getChildren().add(settingsPane);
        StackPane.setAlignment(settingsBuffer, Pos.BOTTOM_RIGHT);

        settingsPane.setPrefSize(235, 156);

        settingsHomeController = new SettingsHomeController(this);
        playbackOptionsController = new PlaybackOptionsController(this);
        playbackSpeedController = new PlaybackSpeedController(this);
        equalizerController = new EqualizerController(this);
    }


    public void init(MediaInterface mediaInterface, CaptionsController captionsController){
        this.mediaInterface = mediaInterface;
        this.captionsController = captionsController;
    }

    public void openSettings(){

        if(animating.get() || controlBarController.volumeSlider.isValueChanging() || controlBarController.durationSlider.isValueChanging() || menuController.menuState != MenuState.CLOSED || captionsController.captionsBox.captionsDragActive || captionsController.animating.get()) return;

        mainController.videoImageView.requestFocus();
        if(captionsController.captionsState != CaptionsState.CLOSED) captionsController.closeCaptions();

        AnimationsClass.rotateTransition(200, controlBarController.settingsIcon, 0, 45, false, 1, true);

        settingsState = SettingsState.HOME_OPEN;

        mainController.sliderHoverLabel.timeLabel.setVisible(false);
        mainController.sliderHoverLabel.chapterlabel.setVisible(false);
        mainController.sliderHoverPreview.pane.setVisible(false);

        if(controlBarController.captions.isShowing()) controlBarController.captions.hide();
        if(controlBarController.settings.isShowing()) controlBarController.settings.hide();
        if(controlBarController.miniplayer.isShowing()) controlBarController.miniplayer.hide();
        if(controlBarController.fullScreen.isShowing()) controlBarController.fullScreen.hide();


        controlBarController.captionsButton.setOnMouseEntered(null);
        controlBarController.settingsButton.setOnMouseEntered(null);
        controlBarController.miniplayerButton.setOnMouseEntered(null);
        controlBarController.fullScreenButton.setOnMouseEntered(null);

        settingsBuffer.setMouseTransparent(false);
        settingsBackground.setVisible(true);
        settingsBackground.setMouseTransparent(false);
        settingsHomeController.settingsHome.setVisible(true);
        settingsHomeController.settingsHome.setMouseTransparent(false);

        FadeTransition backgroundTranslate = new FadeTransition(Duration.millis(ANIMATION_SPEED), settingsBackground);
        backgroundTranslate.setFromValue(0);
        backgroundTranslate.setToValue(1);

        FadeTransition homeTranslate = new FadeTransition(Duration.millis(ANIMATION_SPEED), settingsHomeController.settingsHome);
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
            controlBarController.settings = new ControlTooltip(mainController, "Settings (s)", controlBarController.settingsButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
            controlBarController.settings.showTooltip();

            controlBarController.miniplayer = new ControlTooltip(mainController, "Miniplayer (i)", controlBarController.miniplayerButton, 0, TooltipType.CONTROLBAR_TOOLTIP);

            controlBarController.captions = new ControlTooltip(mainController,"Subtitles/closed captions (c)", controlBarController.captionsButton, 0, TooltipType.CONTROLBAR_TOOLTIP);

            if (App.fullScreen)
                controlBarController.fullScreen = new ControlTooltip(mainController,"Exit full screen (f)", controlBarController.fullScreenButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
            else
                controlBarController.fullScreen = new ControlTooltip(mainController,"Full screen (f)", controlBarController.fullScreenButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
        }
        else if (controlBarController.captionsButtonHover) {
            controlBarController.captions = new ControlTooltip(mainController,"Subtitles/closed captions (c)", controlBarController.captionsButton, 0, TooltipType.CONTROLBAR_TOOLTIP);

            controlBarController.captions.showTooltip();

            controlBarController.miniplayer = new ControlTooltip(mainController,"Miniplayer (i)", controlBarController.miniplayerButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
            controlBarController.settings = new ControlTooltip(mainController,"Settings (s)", controlBarController.settingsButton, 0, TooltipType.CONTROLBAR_TOOLTIP);

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

            controlBarController.captions = new ControlTooltip(mainController,"Subtitles/closed captions (c)", controlBarController.captionsButton, 0, TooltipType.CONTROLBAR_TOOLTIP);

            controlBarController.miniplayer = new ControlTooltip(mainController,"Miniplayer (i)", controlBarController.miniplayerButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
            controlBarController.miniplayer.showTooltip();

            controlBarController.settings = new ControlTooltip(mainController,"Settings (s)", controlBarController.settingsButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
        }
        else if (controlBarController.fullScreenButtonHover) {
            if (App.fullScreen) {
                controlBarController.fullScreen = new ControlTooltip(mainController,"Exit full screen (f)", controlBarController.fullScreenButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
            } else {
                controlBarController.fullScreen = new ControlTooltip(mainController,"Full screen (f)", controlBarController.fullScreenButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
            }
            controlBarController.fullScreen.showTooltip();

            controlBarController.captions = new ControlTooltip(mainController,"Subtitles/closed captions (c)", controlBarController.captionsButton, 0, TooltipType.CONTROLBAR_TOOLTIP);

            controlBarController.miniplayer = new ControlTooltip(mainController,"Miniplayer (i)", controlBarController.miniplayerButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
            controlBarController.settings = new ControlTooltip(mainController,"Settings (s)", controlBarController.settingsButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
        }
        else {
            controlBarController.captions = new ControlTooltip(mainController,"Subtitles/closed captions (c)", controlBarController.captionsButton, 0, TooltipType.CONTROLBAR_TOOLTIP);

            controlBarController.settings = new ControlTooltip(mainController,"Settings (s)", controlBarController.settingsButton, 0, TooltipType.CONTROLBAR_TOOLTIP);

            controlBarController.miniplayer = new ControlTooltip(mainController,"Miniplayer (i)", controlBarController.miniplayerButton, 0, TooltipType.CONTROLBAR_TOOLTIP);

            if (App.fullScreen)
                controlBarController.fullScreen = new ControlTooltip(mainController,"Exit full screen (f)", controlBarController.fullScreenButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
            else
                controlBarController.fullScreen = new ControlTooltip(mainController,"Full screen (f)", controlBarController.fullScreenButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
        }

        switch (settingsState) {
            case HOME_OPEN -> closeSettingsFromHome();
            case PLAYBACK_SPEED_OPEN -> closeSettingsFromPlaybackSpeed();
            case PLAYBACK_OPTIONS_OPEN -> closeSettingsFromPlaybackOptions();
            case CUSTOM_SPEED_OPEN -> closeSettingsFromCustomSpeed();
            case EQUALIZER_OPEN -> closeSettingsFromEqualizer();
            default -> {
            }
        }

        settingsState = SettingsState.CLOSED;

        if(controlBarController.durationSliderHover || controlBarController.durationSlider.isValueChanging()){
            mainController.sliderHoverLabel.timeLabel.setVisible(true);
            if(mainController.chapterController.activeChapter != -1) mainController.sliderHoverLabel.chapterlabel.setVisible(true);
            if(menuController.queueBox.activeIndex.get() != -1 && menuController.queueBox.queue.get(menuController.queueBox.queueOrder.get(menuController.queueBox.activeIndex.get())).getMediaItem() != null && menuController.queueBox.queue.get(menuController.queueBox.queueOrder.get(menuController.queueBox.activeIndex.get())).getMediaItem().hasVideo()) mainController.sliderHoverPreview.pane.setVisible(true);
        }

    }


    public void closeSettingsFromHome(){
        FadeTransition backgroundTranslate = new FadeTransition(Duration.millis(ANIMATION_SPEED), settingsBackground);
        backgroundTranslate.setFromValue(1);
        backgroundTranslate.setToValue(0);

        FadeTransition homeTransition = new FadeTransition(Duration.millis(ANIMATION_SPEED), settingsHomeController.settingsHome);
        homeTransition.setFromValue(1);
        homeTransition.setToValue(0);

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, homeTransition);
        parallelTransition.setOnFinished((e) -> {
            animating.set(false);

            settingsBuffer.setMouseTransparent(true);
            settingsBackground.setVisible(false);
            settingsBackground.setMouseTransparent(true);
            settingsHomeController.settingsHome.setVisible(false);
            settingsHomeController.settingsHome.setOpacity(1);
            settingsHomeController.settingsHome.setMouseTransparent(true);
        });
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.play();
        animating.set(true);
    }

    public void closeSettingsFromPlaybackOptions(){
        FadeTransition backgroundTranslate = new FadeTransition(Duration.millis(ANIMATION_SPEED), settingsBackground);
        backgroundTranslate.setFromValue(1);
        backgroundTranslate.setToValue(0);

        FadeTransition playbackOptionsTransition = new FadeTransition(Duration.millis(ANIMATION_SPEED), playbackOptionsController.playbackOptionsBox);
        playbackOptionsTransition.setFromValue(1);
        playbackOptionsTransition.setToValue(0);

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, playbackOptionsTransition);
        parallelTransition.setOnFinished((e) -> {
            animating.set(false);

            settingsBuffer.setMouseTransparent(true);
            settingsBackground.setVisible(false);
            settingsBackground.setMouseTransparent(true);
            playbackOptionsController.playbackOptionsBox.setVisible(false);
             playbackOptionsController.playbackOptionsBox.setOpacity(1);
            playbackOptionsController.playbackOptionsBox.setMouseTransparent(true);
            clip.setHeight(settingsHomeController.settingsHome.getHeight());
        });

        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.play();
        animating.set(true);
    }

    public void closeSettingsFromPlaybackSpeed(){
        FadeTransition backgroundTranslate = new FadeTransition(Duration.millis(ANIMATION_SPEED), settingsBackground);
        backgroundTranslate.setFromValue(1);
        backgroundTranslate.setToValue(0);

        FadeTransition playbackSpeedTransition = new FadeTransition(Duration.millis(ANIMATION_SPEED), playbackSpeedController.playbackSpeedPane.scrollPane);
        playbackSpeedTransition.setFromValue(1);
        playbackSpeedTransition.setToValue(0);

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, playbackSpeedTransition);
        parallelTransition.setOnFinished((e) -> {
            animating.set(false);

            settingsBuffer.setMouseTransparent(true);
            settingsBackground.setVisible(false);
            settingsBackground.setMouseTransparent(true);
            playbackSpeedController.playbackSpeedPane.scrollPane.setVisible(false);
            playbackSpeedController.playbackSpeedPane.scrollPane.setMouseTransparent(true);
            playbackSpeedController.playbackSpeedPane.scrollPane.setOpacity(1);
            clip.setHeight(settingsHomeController.settingsHome.getHeight());
        });

        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.play();
        animating.set(true);
    }

    public void closeSettingsFromCustomSpeed(){
        FadeTransition backgroundTranslate = new FadeTransition(Duration.millis(ANIMATION_SPEED), settingsBackground);
        backgroundTranslate.setFromValue(1);
        backgroundTranslate.setToValue(0);

        FadeTransition customTransition = new FadeTransition(Duration.millis(ANIMATION_SPEED), playbackSpeedController.customSpeedPane.customSpeedBox);
        customTransition.setFromValue(1);
        customTransition.setToValue(0);

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, customTransition);
        parallelTransition.setOnFinished((e) -> {
            animating.set(false);

            settingsBuffer.setMouseTransparent(true);
            settingsBackground.setVisible(false);
            settingsBackground.setMouseTransparent(true);
            playbackSpeedController.customSpeedPane.customSpeedBox.setVisible(false);
            playbackSpeedController.customSpeedPane.customSpeedBox.setMouseTransparent(true);
            playbackSpeedController.customSpeedPane.customSpeedBox.setOpacity(1);
            clip.setHeight(settingsHomeController.settingsHome.getHeight());
        });

        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.play();
        animating.set(true);
    }

    public void closeSettingsFromEqualizer(){
        FadeTransition backgroundTranslate = new FadeTransition(Duration.millis(ANIMATION_SPEED), settingsBackground);
        backgroundTranslate.setFromValue(1);
        backgroundTranslate.setToValue(0);

        FadeTransition equalizerTransition = new FadeTransition(Duration.millis(ANIMATION_SPEED), equalizerController.scrollPane);
        equalizerTransition.setFromValue(1);
        equalizerTransition.setToValue(0);

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, equalizerTransition);
        parallelTransition.setOnFinished((e) -> {
            animating.set(false);

            settingsBuffer.setMouseTransparent(true);
            settingsBackground.setVisible(false);
            settingsBackground.setMouseTransparent(true);
            equalizerController.scrollPane.setVisible(false);
            equalizerController.scrollPane.setMouseTransparent(true);
            equalizerController.scrollPane.setOpacity(1);
            clip.setHeight(settingsHomeController.settingsHome.getHeight());
            clip.setWidth(settingsHomeController.settingsHome.getWidth());
        });

        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.play();
        animating.set(true);
    }

}