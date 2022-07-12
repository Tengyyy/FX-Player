package hans;

import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
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
    PlaybackOptionsController playbackOptionsController;
    PlaybackSpeedController playbackSpeedController;
    CaptionsController captionsController;

    StackPane settingsBuffer = new StackPane();
    StackPane settingsPane = new StackPane();
    StackPane settingsBackground = new StackPane();


    Rectangle clip = new Rectangle();

    BooleanProperty animating = new SimpleBooleanProperty(); // animating state of the settings pane


    SettingsState settingsState = SettingsState.CLOSED;

    static final int ANIMATION_SPEED = 200;

    SettingsController(MainController mainController, ControlBarController controlBarController, MenuController menuController){

        this.mainController = mainController;
        this.controlBarController = controlBarController;
        this.menuController = menuController;

        animating.set(false);

        settingsBuffer.setPrefSize(235, 156);
        settingsBuffer.setMaxWidth(260);
        settingsBuffer.setClip(clip);
        settingsBuffer.getChildren().add(settingsBackground);
        settingsBuffer.setMouseTransparent(true);
        settingsBackground.setId("settingsBackground");
        settingsBackground.setVisible(false);
        settingsBackground.setMouseTransparent(true);
        StackPane.setMargin(settingsBuffer, new Insets(0, 20, 80, 0));
        StackPane.setAlignment(settingsBackground, Pos.BOTTOM_RIGHT);


        Platform.runLater(() -> {
            settingsBuffer.maxHeightProperty().bind(Bindings.subtract(mainController.mediaViewHeight, 120));
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
    }


    public void init(MediaInterface mediaInterface, CaptionsController captionsController){
        this.mediaInterface = mediaInterface;
        this.captionsController = captionsController;
    }

    public void openSettings(){

        if(animating.get()) return;

        AnimationsClass.rotateTransition(200, controlBarController.settingsIcon, 0, 45, false, 1, true);

        settingsState = SettingsState.HOME_OPEN;

        if(controlBarController.captions.isShowing()) controlBarController.captions.hide();
        if(controlBarController.settings.isShowing()) controlBarController.settings.hide();
        if(controlBarController.miniplayer.isShowing()) controlBarController.miniplayer.hide();
        if(controlBarController.fullScreen.isShowing()) controlBarController.fullScreen.hide();
        if(controlBarController.exitFullScreen.isShowing()) controlBarController.exitFullScreen.hide();


        controlBarController.captionsButton.setOnMouseEntered(null);
        controlBarController.settingsButton.setOnMouseEntered(null);
        controlBarController.miniplayerButton.setOnMouseEntered(null);
        controlBarController.fullScreenButton.setOnMouseEntered(null);

        settingsBuffer.setMouseTransparent(false);
        settingsBackground.setVisible(true);
        settingsBackground.setMouseTransparent(false);
        settingsHomeController.settingsHome.setVisible(true);
        settingsHomeController.settingsHome.setMouseTransparent(false);

        TranslateTransition backgroundTranslate = new TranslateTransition(Duration.millis(ANIMATION_SPEED), settingsBackground);
        backgroundTranslate.setFromY(settingsHomeController.settingsHome.getHeight());
        backgroundTranslate.setToY(0);

        TranslateTransition homeTranslate = new TranslateTransition(Duration.millis(ANIMATION_SPEED), settingsHomeController.settingsHome);
        homeTranslate.setFromY(settingsHomeController.settingsHome.getHeight());
        homeTranslate.setToY(0);

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, homeTranslate);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> animating.set(false));
        parallelTransition.play();
        animating.set(true);
    }

    public void closeSettings(){

        if(animating.get()) return;

        AnimationsClass.rotateTransition(200, controlBarController.settingsIcon, 45, 0, false, 1, true);


        if (controlBarController.settingsButtonHover) {
            controlBarController.settings = new ControlTooltip("Settings (s)", controlBarController.settingsButton, controlBarController.controlBarWrapper, 0, false);
            controlBarController.settings.showTooltip();

            controlBarController.miniplayer = new ControlTooltip("Miniplayer (i)", controlBarController.miniplayerButton, controlBarController.controlBarWrapper, 0, false);

            if(captionsController.captionsSelected) controlBarController.captions = new ControlTooltip("Subtitles/closed captions (c)", controlBarController.captionsButton, controlBarController.controlBarWrapper, 0, false);
            else controlBarController.captions = new ControlTooltip("Subtitles/CC not selected", controlBarController.captionsButton, controlBarController.controlBarWrapper, 0, false);

            if (App.fullScreen)
                controlBarController.exitFullScreen = new ControlTooltip("Exit full screen (f)", controlBarController.fullScreenButton, controlBarController.controlBarWrapper, 0, false);
            else
                controlBarController.fullScreen = new ControlTooltip("Full screen (f)", controlBarController.fullScreenButton, controlBarController.controlBarWrapper, 0, false);
        } else if (controlBarController.captionsButtonHover) {
            if(captionsController.captionsSelected) controlBarController.captions = new ControlTooltip("Subtitles/closed captions (c)", controlBarController.captionsButton, controlBarController.controlBarWrapper, 0, false);
            else controlBarController.captions = new ControlTooltip("Subtitles/CC not selected", controlBarController.captionsButton, controlBarController.controlBarWrapper, 0, false);

            controlBarController.captions.showTooltip();

            controlBarController.miniplayer = new ControlTooltip("Miniplayer (i)", controlBarController.miniplayerButton, controlBarController.controlBarWrapper, 0, false);
            controlBarController.settings = new ControlTooltip("Settings (s)", controlBarController.settingsButton, controlBarController.controlBarWrapper, 0, false);

            if (App.fullScreen)
                controlBarController.exitFullScreen = new ControlTooltip("Exit full screen (f)", controlBarController.fullScreenButton, controlBarController.controlBarWrapper, 0, false);
            else
                controlBarController.fullScreen = new ControlTooltip("Full screen (f)", controlBarController.fullScreenButton, controlBarController.controlBarWrapper, 0, false);
        }
        else if(controlBarController.miniplayerButtonHover){
            if (App.fullScreen) {
                controlBarController.exitFullScreen = new ControlTooltip("Exit full screen (f)", controlBarController.fullScreenButton, controlBarController.controlBarWrapper, 0, false);
            } else {
                controlBarController.fullScreen = new ControlTooltip("Full screen (f)", controlBarController.fullScreenButton, controlBarController.controlBarWrapper, 0, false);
            }

            if(captionsController.captionsSelected) controlBarController.captions = new ControlTooltip("Subtitles/closed captions (c)", controlBarController.captionsButton, controlBarController.controlBarWrapper, 0, false);
            else controlBarController.captions = new ControlTooltip("Subtitles/CC not selected", controlBarController.captionsButton, controlBarController.controlBarWrapper, 0, false);

            controlBarController.miniplayer = new ControlTooltip("Miniplayer (i)", controlBarController.miniplayerButton, controlBarController.controlBarWrapper, 0, false);
            controlBarController.miniplayer.showTooltip();

            controlBarController.settings = new ControlTooltip("Settings (s)", controlBarController.settingsButton, controlBarController.controlBarWrapper, 0, false);
        }
        else if (controlBarController.fullScreenButtonHover) {
            if (App.fullScreen) {
                controlBarController.exitFullScreen = new ControlTooltip("Exit full screen (f)", controlBarController.fullScreenButton, controlBarController.controlBarWrapper, 0, false);
                controlBarController.exitFullScreen.showTooltip();
            } else {
                controlBarController.fullScreen = new ControlTooltip("Full screen (f)", controlBarController.fullScreenButton, controlBarController.controlBarWrapper, 0, false);
                controlBarController.fullScreen.showTooltip();
            }

            if(captionsController.captionsSelected) controlBarController.captions = new ControlTooltip("Subtitles/closed captions (c)", controlBarController.captionsButton, controlBarController.controlBarWrapper, 0, false);
            else controlBarController.captions = new ControlTooltip("Subtitles/CC not selected", controlBarController.captionsButton, controlBarController.controlBarWrapper, 0, false);

            controlBarController.miniplayer = new ControlTooltip("Miniplayer (i)", controlBarController.miniplayerButton, controlBarController.controlBarWrapper, 0, false);
            controlBarController.settings = new ControlTooltip("Settings (s)", controlBarController.settingsButton, controlBarController.controlBarWrapper, 0, false);
        } else {
            if(captionsController.captionsSelected) controlBarController.captions = new ControlTooltip("Subtitles/closed captions (c)", controlBarController.captionsButton, controlBarController.controlBarWrapper, 0, false);
            else controlBarController.captions = new ControlTooltip("Subtitles/CC not selected", controlBarController.captionsButton, controlBarController.controlBarWrapper, 0, false);

            controlBarController.settings = new ControlTooltip("Settings (s)", controlBarController.settingsButton, controlBarController.controlBarWrapper, 0, false);

            controlBarController.miniplayer = new ControlTooltip("Miniplayer (i)", controlBarController.miniplayerButton, controlBarController.controlBarWrapper, 0, false);

            if (App.fullScreen)
                controlBarController.exitFullScreen = new ControlTooltip("Exit full screen (f)", controlBarController.fullScreenButton, controlBarController.controlBarWrapper, 0, false);
            else
                controlBarController.fullScreen = new ControlTooltip("Full screen (f)", controlBarController.fullScreenButton, controlBarController.controlBarWrapper, 0, false);
        }

        switch(settingsState){
            case HOME_OPEN: closeSettingsFromHome();
            break;
            case PLAYBACK_SPEED_OPEN: closeSettingsFromPlaybackSpeed();
            break;
            case PLAYBACK_OPTIONS_OPEN: closeSettingsFromPlaybackOptions();
            break;
            case CUSTOM_SPEED_OPEN: closeSettingsFromCustomSpeed();
            break;
            case CAPTIONS_PANE_OPEN: closeSettingsFromCaptions();
            break;
            case CAPTIONS_OPTIONS_OPEN: closeSettingsFromCaptionsOptions();
            break;
            case FONT_FAMILY_OPEN: closeSettingsFromFontFamily();
            break;
            case FONT_COLOR_OPEN: closeSettingsFromFontColor();
            break;
            case FONT_SIZE_OPEN: closeSettingsFromFontSize();
            break;
            case TEXT_ALIGNMENT_OPEN: closeSettingsFromTextAlignment();
            break;
            case BACKGROUND_COLOR_OPEN: closeSettingsFromBackgroundColor();
            break;
            case BACKGROUND_OPACITY_OPEN: closeSettingsFromBackgroundOpacity();
            break;
            case LINE_SPACING_OPEN: closeSettingsFromLineSpacing();
            break;
            case OPACITY_OPEN: closeSettingsFromOpacity();
            break;
            default: break;
        }

        settingsState = SettingsState.CLOSED;
    }


    public void closeSettingsFromHome(){
        TranslateTransition backgroundTranslate = new TranslateTransition(Duration.millis(ANIMATION_SPEED), settingsBackground);
        backgroundTranslate.setFromY(0);
        backgroundTranslate.setToY(settingsHomeController.settingsHome.getHeight());

        TranslateTransition homeTransition = new TranslateTransition(Duration.millis(ANIMATION_SPEED), settingsHomeController.settingsHome);
        homeTransition.setFromY(0);
        homeTransition.setToY(settingsHomeController.settingsHome.getHeight());

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, homeTransition);
        parallelTransition.setOnFinished((e) -> {
            animating.set(false);

            settingsBuffer.setMouseTransparent(true);
            settingsBackground.setVisible(false);
            settingsBackground.setMouseTransparent(true);
            settingsHomeController.settingsHome.setVisible(false);
            settingsHomeController.settingsHome.setMouseTransparent(true);
        });
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.play();
        animating.set(true);
    }

    public void closeSettingsFromPlaybackOptions(){
        TranslateTransition backgroundTranslate = new TranslateTransition(Duration.millis(ANIMATION_SPEED), settingsBackground);
        backgroundTranslate.setFromY(0);
        backgroundTranslate.setToY(playbackOptionsController.playbackOptionsBox.getHeight());

        TranslateTransition playbackOptionsTransition = new TranslateTransition(Duration.millis(ANIMATION_SPEED), playbackOptionsController.playbackOptionsBox);
        playbackOptionsTransition.setFromY(0);
        playbackOptionsTransition.setToY(playbackOptionsController.playbackOptionsBox.getHeight());

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, playbackOptionsTransition);
        parallelTransition.setOnFinished((e) -> {
            animating.set(false);

            settingsBuffer.setMouseTransparent(true);
            settingsBackground.setVisible(false);
            settingsBackground.setMouseTransparent(true);
            playbackOptionsController.playbackOptionsBox.setVisible(false);
            playbackOptionsController.playbackOptionsBox.setMouseTransparent(true);
            playbackOptionsController.playbackOptionsBox.setTranslateY(0);
            clip.setHeight(settingsHomeController.settingsHome.getHeight());
        });

        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.play();
        animating.set(true);
    }

    public void closeSettingsFromPlaybackSpeed(){
        TranslateTransition backgroundTranslate = new TranslateTransition(Duration.millis(ANIMATION_SPEED), settingsBackground);
        backgroundTranslate.setFromY(0);
        backgroundTranslate.setToY(playbackSpeedController.playbackSpeedPane.scrollPane.getHeight());

        TranslateTransition playbackSpeedTransition = new TranslateTransition(Duration.millis(ANIMATION_SPEED), playbackSpeedController.playbackSpeedPane.scrollPane);
        playbackSpeedTransition.setFromY(0);
        playbackSpeedTransition.setToY(playbackSpeedController.playbackSpeedPane.scrollPane.getHeight());

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, playbackSpeedTransition);
        parallelTransition.setOnFinished((e) -> {
            animating.set(false);

            settingsBuffer.setMouseTransparent(true);
            settingsBackground.setVisible(false);
            settingsBackground.setMouseTransparent(true);
            playbackSpeedController.playbackSpeedPane.scrollPane.setVisible(false);
            playbackSpeedController.playbackSpeedPane.scrollPane.setMouseTransparent(true);
            playbackSpeedController.playbackSpeedPane.scrollPane.setTranslateY(0);
            clip.setHeight(settingsHomeController.settingsHome.getHeight());
        });

        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.play();
        animating.set(true);
    }

    public void closeSettingsFromCustomSpeed(){
        TranslateTransition backgroundTranslate = new TranslateTransition(Duration.millis(ANIMATION_SPEED), settingsBackground);
        backgroundTranslate.setFromY(0);
        backgroundTranslate.setToY(playbackSpeedController.customSpeedPane.customSpeedBox.getHeight());

        TranslateTransition customTransition = new TranslateTransition(Duration.millis(ANIMATION_SPEED), playbackSpeedController.customSpeedPane.customSpeedBox);
        customTransition.setFromY(0);
        customTransition.setToY(playbackSpeedController.customSpeedPane.customSpeedBox.getHeight());

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, customTransition);
        parallelTransition.setOnFinished((e) -> {
            animating.set(false);

            settingsBuffer.setMouseTransparent(true);
            settingsBackground.setVisible(false);
            settingsBackground.setMouseTransparent(true);
            playbackSpeedController.customSpeedPane.customSpeedBox.setVisible(false);
            playbackSpeedController.customSpeedPane.customSpeedBox.setMouseTransparent(true);
            playbackSpeedController.customSpeedPane.customSpeedBox.setTranslateY(0);
            clip.setHeight(settingsHomeController.settingsHome.getHeight());
        });

        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.play();
        animating.set(true);
    }

    public void closeSettingsFromCaptions(){
        TranslateTransition backgroundTranslate = new TranslateTransition(Duration.millis(ANIMATION_SPEED), settingsBackground);
        backgroundTranslate.setFromY(0);
        backgroundTranslate.setToY(playbackOptionsController.playbackOptionsBox.getHeight());

        TranslateTransition captionsPaneTransition = new TranslateTransition(Duration.millis(ANIMATION_SPEED), captionsController.captionsPane.captionsBox);
        captionsPaneTransition.setFromY(0);
        captionsPaneTransition.setToY(captionsController.captionsPane.captionsBox.getHeight());

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, captionsPaneTransition);
        parallelTransition.setOnFinished((e) -> {
            animating.set(false);

            settingsBuffer.setMouseTransparent(true);
            settingsBackground.setVisible(false);
            settingsBackground.setMouseTransparent(true);
            captionsController.captionsPane.captionsBox.setVisible(false);
            captionsController.captionsPane.captionsBox.setMouseTransparent(true);
            captionsController.captionsPane.captionsBox.setTranslateY(0);
            clip.setHeight(settingsHomeController.settingsHome.getHeight());
        });

        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.play();
        animating.set(true);
    }

    public void closeSettingsFromCaptionsOptions(){
        TranslateTransition backgroundTranslate = new TranslateTransition(Duration.millis(ANIMATION_SPEED), settingsBackground);
        backgroundTranslate.setFromY(0);
        backgroundTranslate.setToY(captionsController.captionsOptionsPane.scrollPane.getHeight());

        TranslateTransition captionsOptionsTransition = new TranslateTransition(Duration.millis(ANIMATION_SPEED), captionsController.captionsOptionsPane.scrollPane);
        captionsOptionsTransition.setFromY(0);
        captionsOptionsTransition.setToY(captionsController.captionsOptionsPane.scrollPane.getHeight());

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, captionsOptionsTransition);
        parallelTransition.setOnFinished((e) -> {
            animating.set(false);

            settingsBuffer.setMouseTransparent(true);
            settingsBackground.setVisible(false);
            settingsBackground.setMouseTransparent(true);
            captionsController.captionsOptionsPane.scrollPane.setVisible(false);
            captionsController.captionsOptionsPane.scrollPane.setMouseTransparent(true);
            captionsController.captionsOptionsPane.scrollPane.setTranslateY(0);
            clip.setHeight(settingsHomeController.settingsHome.getHeight());
            clip.setWidth(settingsHomeController.settingsHome.getWidth());
        });

        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.play();
        animating.set(true);
    }

    public void closeSettingsFromFontFamily(){
        TranslateTransition backgroundTranslate = new TranslateTransition(Duration.millis(ANIMATION_SPEED), settingsBackground);
        backgroundTranslate.setFromY(0);
        backgroundTranslate.setToY(captionsController.captionsOptionsPane.fontFamilyPane.scrollPane.getHeight());

        TranslateTransition fontFamilyTransition = new TranslateTransition(Duration.millis(ANIMATION_SPEED), captionsController.captionsOptionsPane.fontFamilyPane.scrollPane);
        fontFamilyTransition.setFromY(0);
        fontFamilyTransition.setToY(captionsController.captionsOptionsPane.fontFamilyPane.scrollPane.getHeight());

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, fontFamilyTransition);
        parallelTransition.setOnFinished((e) -> {
            animating.set(false);

            settingsBuffer.setMouseTransparent(true);
            settingsBackground.setVisible(false);
            settingsBackground.setMouseTransparent(true);
            captionsController.captionsOptionsPane.fontFamilyPane.scrollPane.setVisible(false);
            captionsController.captionsOptionsPane.fontFamilyPane.scrollPane.setMouseTransparent(true);
            captionsController.captionsOptionsPane.fontFamilyPane.scrollPane.setTranslateY(0);
            clip.setHeight(settingsHomeController.settingsHome.getHeight());
            clip.setWidth(settingsHomeController.settingsHome.getWidth());
        });

        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.play();
        animating.set(true);
    }

    public void closeSettingsFromFontColor(){
        TranslateTransition backgroundTranslate = new TranslateTransition(Duration.millis(ANIMATION_SPEED), settingsBackground);
        backgroundTranslate.setFromY(0);
        backgroundTranslate.setToY(captionsController.captionsOptionsPane.fontColorPane.scrollPane.getHeight());

        TranslateTransition fontColorTransition = new TranslateTransition(Duration.millis(ANIMATION_SPEED), captionsController.captionsOptionsPane.fontColorPane.scrollPane);
        fontColorTransition.setFromY(0);
        fontColorTransition.setToY(captionsController.captionsOptionsPane.fontColorPane.scrollPane.getHeight());

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, fontColorTransition);
        parallelTransition.setOnFinished((e) -> {
            animating.set(false);

            settingsBuffer.setMouseTransparent(true);
            settingsBackground.setVisible(false);
            settingsBackground.setMouseTransparent(true);
            captionsController.captionsOptionsPane.fontColorPane.scrollPane.setVisible(false);
            captionsController.captionsOptionsPane.fontColorPane.scrollPane.setMouseTransparent(true);
            captionsController.captionsOptionsPane.fontColorPane.scrollPane.setTranslateY(0);
            clip.setHeight(settingsHomeController.settingsHome.getHeight());
            clip.setWidth(settingsHomeController.settingsHome.getWidth());
        });

        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.play();
        animating.set(true);
    }

    public void closeSettingsFromFontSize(){
        TranslateTransition backgroundTranslate = new TranslateTransition(Duration.millis(ANIMATION_SPEED), settingsBackground);
        backgroundTranslate.setFromY(0);
        backgroundTranslate.setToY(captionsController.captionsOptionsPane.fontSizePane.scrollPane.getHeight());

        TranslateTransition fontSizeTransition = new TranslateTransition(Duration.millis(ANIMATION_SPEED), captionsController.captionsOptionsPane.fontSizePane.scrollPane);
        fontSizeTransition.setFromY(0);
        fontSizeTransition.setToY(captionsController.captionsOptionsPane.fontSizePane.scrollPane.getHeight());

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, fontSizeTransition);
        parallelTransition.setOnFinished((e) -> {
            animating.set(false);

            settingsBuffer.setMouseTransparent(true);
            settingsBackground.setVisible(false);
            settingsBackground.setMouseTransparent(true);
            captionsController.captionsOptionsPane.fontSizePane.scrollPane.setVisible(false);
            captionsController.captionsOptionsPane.fontSizePane.scrollPane.setMouseTransparent(true);
            captionsController.captionsOptionsPane.fontSizePane.scrollPane.setTranslateY(0);
            clip.setHeight(settingsHomeController.settingsHome.getHeight());
            clip.setWidth(settingsHomeController.settingsHome.getWidth());
        });

        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.play();
        animating.set(true);
    }

    public void closeSettingsFromTextAlignment(){
        TranslateTransition backgroundTranslate = new TranslateTransition(Duration.millis(ANIMATION_SPEED), settingsBackground);
        backgroundTranslate.setFromY(0);
        backgroundTranslate.setToY(captionsController.captionsOptionsPane.textAlignmentPane.scrollPane.getHeight());

        TranslateTransition textAlignmentTransition = new TranslateTransition(Duration.millis(ANIMATION_SPEED), captionsController.captionsOptionsPane.textAlignmentPane.scrollPane);
        textAlignmentTransition.setFromY(0);
        textAlignmentTransition.setToY(captionsController.captionsOptionsPane.textAlignmentPane.scrollPane.getHeight());

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, textAlignmentTransition);
        parallelTransition.setOnFinished((e) -> {
            animating.set(false);

            settingsBuffer.setMouseTransparent(true);
            settingsBackground.setVisible(false);
            settingsBackground.setMouseTransparent(true);
            captionsController.captionsOptionsPane.textAlignmentPane.scrollPane.setVisible(false);
            captionsController.captionsOptionsPane.textAlignmentPane.scrollPane.setMouseTransparent(true);
            captionsController.captionsOptionsPane.textAlignmentPane.scrollPane.setTranslateY(0);
            clip.setHeight(settingsHomeController.settingsHome.getHeight());
            clip.setWidth(settingsHomeController.settingsHome.getWidth());
        });

        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.play();
        animating.set(true);
    }

    public void closeSettingsFromBackgroundColor(){
        TranslateTransition backgroundTranslate = new TranslateTransition(Duration.millis(ANIMATION_SPEED), settingsBackground);
        backgroundTranslate.setFromY(0);
        backgroundTranslate.setToY(captionsController.captionsOptionsPane.backgroundColorPane.scrollPane.getHeight());

        TranslateTransition backgroundColorTransition = new TranslateTransition(Duration.millis(ANIMATION_SPEED), captionsController.captionsOptionsPane.backgroundColorPane.scrollPane);
        backgroundColorTransition.setFromY(0);
        backgroundColorTransition.setToY(captionsController.captionsOptionsPane.backgroundColorPane.scrollPane.getHeight());

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, backgroundColorTransition);
        parallelTransition.setOnFinished((e) -> {
            animating.set(false);

            settingsBuffer.setMouseTransparent(true);
            settingsBackground.setVisible(false);
            settingsBackground.setMouseTransparent(true);
            captionsController.captionsOptionsPane.backgroundColorPane.scrollPane.setVisible(false);
            captionsController.captionsOptionsPane.backgroundColorPane.scrollPane.setMouseTransparent(true);
            captionsController.captionsOptionsPane.backgroundColorPane.scrollPane.setTranslateY(0);
            clip.setHeight(settingsHomeController.settingsHome.getHeight());
            clip.setWidth(settingsHomeController.settingsHome.getWidth());
        });

        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.play();
        animating.set(true);
    }

    public void closeSettingsFromBackgroundOpacity(){
        TranslateTransition backgroundTranslate = new TranslateTransition(Duration.millis(ANIMATION_SPEED), settingsBackground);
        backgroundTranslate.setFromY(0);
        backgroundTranslate.setToY(captionsController.captionsOptionsPane.backgroundOpacityPane.scrollPane.getHeight());

        TranslateTransition backgroundOpacityTransition = new TranslateTransition(Duration.millis(ANIMATION_SPEED), captionsController.captionsOptionsPane.backgroundOpacityPane.scrollPane);
        backgroundOpacityTransition.setFromY(0);
        backgroundOpacityTransition.setToY(captionsController.captionsOptionsPane.backgroundOpacityPane.scrollPane.getHeight());

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, backgroundOpacityTransition);
        parallelTransition.setOnFinished((e) -> {
            animating.set(false);

            settingsBuffer.setMouseTransparent(true);
            settingsBackground.setVisible(false);
            settingsBackground.setMouseTransparent(true);
            captionsController.captionsOptionsPane.backgroundOpacityPane.scrollPane.setVisible(false);
            captionsController.captionsOptionsPane.backgroundOpacityPane.scrollPane.setMouseTransparent(true);
            captionsController.captionsOptionsPane.backgroundOpacityPane.scrollPane.setTranslateY(0);
            clip.setHeight(settingsHomeController.settingsHome.getHeight());
            clip.setWidth(settingsHomeController.settingsHome.getWidth());
        });

        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.play();
        animating.set(true);
    }

    public void closeSettingsFromLineSpacing(){
        TranslateTransition backgroundTranslate = new TranslateTransition(Duration.millis(ANIMATION_SPEED), settingsBackground);
        backgroundTranslate.setFromY(0);
        backgroundTranslate.setToY(captionsController.captionsOptionsPane.lineSpacingPane.scrollPane.getHeight());

        TranslateTransition lineSpacingTransition = new TranslateTransition(Duration.millis(ANIMATION_SPEED), captionsController.captionsOptionsPane.lineSpacingPane.scrollPane);
        lineSpacingTransition.setFromY(0);
        lineSpacingTransition.setToY(captionsController.captionsOptionsPane.lineSpacingPane.scrollPane.getHeight());

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, lineSpacingTransition);
        parallelTransition.setOnFinished((e) -> {
            animating.set(false);

            settingsBuffer.setMouseTransparent(true);
            settingsBackground.setVisible(false);
            settingsBackground.setMouseTransparent(true);
            captionsController.captionsOptionsPane.lineSpacingPane.scrollPane.setVisible(false);
            captionsController.captionsOptionsPane.lineSpacingPane.scrollPane.setMouseTransparent(true);
            captionsController.captionsOptionsPane.lineSpacingPane.scrollPane.setTranslateY(0);
            clip.setHeight(settingsHomeController.settingsHome.getHeight());
            clip.setWidth(settingsHomeController.settingsHome.getWidth());
        });

        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.play();
        animating.set(true);
    }

    public void closeSettingsFromOpacity(){
        TranslateTransition backgroundTranslate = new TranslateTransition(Duration.millis(ANIMATION_SPEED), settingsBackground);
        backgroundTranslate.setFromY(0);
        backgroundTranslate.setToY(captionsController.captionsOptionsPane.fontOpacityPane.scrollPane.getHeight());

        TranslateTransition opacityTransition = new TranslateTransition(Duration.millis(ANIMATION_SPEED), captionsController.captionsOptionsPane.fontOpacityPane.scrollPane);
        opacityTransition.setFromY(0);
        opacityTransition.setToY(captionsController.captionsOptionsPane.fontOpacityPane.scrollPane.getHeight());

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, opacityTransition);
        parallelTransition.setOnFinished((e) -> {
            animating.set(false);

            settingsBuffer.setMouseTransparent(true);
            settingsBackground.setVisible(false);
            settingsBackground.setMouseTransparent(true);
            captionsController.captionsOptionsPane.fontOpacityPane.scrollPane.setVisible(false);
            captionsController.captionsOptionsPane.fontOpacityPane.scrollPane.setMouseTransparent(true);
            captionsController.captionsOptionsPane.fontOpacityPane.scrollPane.setTranslateY(0);
            clip.setHeight(settingsHomeController.settingsHome.getHeight());
            clip.setWidth(settingsHomeController.settingsHome.getWidth());
        });

        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.play();
        animating.set(true);
    }

}