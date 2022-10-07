package hans.Settings;

import hans.*;
import hans.Menu.MenuController;
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
    public CaptionsController captionsController;

    public StackPane settingsBuffer = new StackPane();
    StackPane settingsPane = new StackPane();
    StackPane settingsBackground = new StackPane();


    Rectangle clip = new Rectangle();

    BooleanProperty animating = new SimpleBooleanProperty(); // animating state of the settings pane


    public SettingsState settingsState = SettingsState.CLOSED;

    static final int ANIMATION_SPEED = 200;

    public SettingsController(MainController mainController, ControlBarController controlBarController, MenuController menuController){

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
        settingsBackground.setOpacity(0);
        StackPane.setMargin(settingsBuffer, new Insets(0, 20, 80, 0));
        StackPane.setAlignment(settingsBackground, Pos.BOTTOM_RIGHT);


        Platform.runLater(() -> {
            settingsBuffer.maxHeightProperty().bind(Bindings.subtract(mainController.videoImageViewHeight, 120));
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

        if(animating.get() || controlBarController.volumeSlider.isValueChanging() || controlBarController.durationSlider.isValueChanging() || menuController.menuState != MenuState.CLOSED || captionsController.captionsDragActive) return;

        AnimationsClass.rotateTransition(200, controlBarController.settingsIcon, 0, 45, false, 1, true);

        settingsState = SettingsState.HOME_OPEN;

        mainController.sliderHoverLabel.label.setVisible(false);
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

        if(animating.get() || playbackSpeedController.customSpeedPane.customSpeedSlider.isValueChanging()) return;

        AnimationsClass.rotateTransition(200, controlBarController.settingsIcon, 45, 0, false, 1, true);


        if (controlBarController.settingsButtonHover) {
            controlBarController.settings = new ControlTooltip(mainController, "Settings (s)", controlBarController.settingsButton, 0, true);
            controlBarController.settings.showTooltip();

            controlBarController.miniplayer = new ControlTooltip(mainController, "Miniplayer (i)", controlBarController.miniplayerButton, 0, true);

            if(captionsController.captionsSelected) controlBarController.captions = new ControlTooltip(mainController,"Subtitles/closed captions (c)", controlBarController.captionsButton, 0, true);
            else controlBarController.captions = new ControlTooltip(mainController,"Subtitles/CC not selected", controlBarController.captionsButton, 0, true);

            if (App.fullScreen)
                controlBarController.fullScreen = new ControlTooltip(mainController,"Exit full screen (f)", controlBarController.fullScreenButton, 0, true);
            else
                controlBarController.fullScreen = new ControlTooltip(mainController,"Full screen (f)", controlBarController.fullScreenButton, 0, true);
        } else if (controlBarController.captionsButtonHover) {
            if(captionsController.captionsSelected) controlBarController.captions = new ControlTooltip(mainController,"Subtitles/closed captions (c)", controlBarController.captionsButton, 0, true);
            else controlBarController.captions = new ControlTooltip(mainController,"Subtitles/CC not selected", controlBarController.captionsButton, 0, true);

            controlBarController.captions.showTooltip();

            controlBarController.miniplayer = new ControlTooltip(mainController,"Miniplayer (i)", controlBarController.miniplayerButton, 0, true);
            controlBarController.settings = new ControlTooltip(mainController,"Settings (s)", controlBarController.settingsButton, 0, true);

            if (App.fullScreen)
                controlBarController.fullScreen = new ControlTooltip(mainController,"Exit full screen (f)", controlBarController.fullScreenButton, 0, true);
            else
                controlBarController.fullScreen = new ControlTooltip(mainController,"Full screen (f)", controlBarController.fullScreenButton, 0, true);
        }
        else if(controlBarController.miniplayerButtonHover){
            if (App.fullScreen) {
                controlBarController.fullScreen = new ControlTooltip(mainController,"Exit full screen (f)", controlBarController.fullScreenButton, 0, true);
            } else {
                controlBarController.fullScreen = new ControlTooltip(mainController,"Full screen (f)", controlBarController.fullScreenButton, 0, true);
            }

            if(captionsController.captionsSelected) controlBarController.captions = new ControlTooltip(mainController,"Subtitles/closed captions (c)", controlBarController.captionsButton, 0, true);
            else controlBarController.captions = new ControlTooltip(mainController,"Subtitles/CC not selected", controlBarController.captionsButton, 0, true);

            controlBarController.miniplayer = new ControlTooltip(mainController,"Miniplayer (i)", controlBarController.miniplayerButton, 0, true);
            controlBarController.miniplayer.showTooltip();

            controlBarController.settings = new ControlTooltip(mainController,"Settings (s)", controlBarController.settingsButton, 0, true);
        }
        else if (controlBarController.fullScreenButtonHover) {
            if (App.fullScreen) {
                controlBarController.fullScreen = new ControlTooltip(mainController,"Exit full screen (f)", controlBarController.fullScreenButton, 0, true);
            } else {
                controlBarController.fullScreen = new ControlTooltip(mainController,"Full screen (f)", controlBarController.fullScreenButton, 0, true);
            }
            controlBarController.fullScreen.showTooltip();

            if(captionsController.captionsSelected) controlBarController.captions = new ControlTooltip(mainController,"Subtitles/closed captions (c)", controlBarController.captionsButton, 0, true);
            else controlBarController.captions = new ControlTooltip(mainController,"Subtitles/CC not selected", controlBarController.captionsButton, 0, true);

            controlBarController.miniplayer = new ControlTooltip(mainController,"Miniplayer (i)", controlBarController.miniplayerButton, 0, true);
            controlBarController.settings = new ControlTooltip(mainController,"Settings (s)", controlBarController.settingsButton, 0, true);
        } else {
            if(captionsController.captionsSelected) controlBarController.captions = new ControlTooltip(mainController,"Subtitles/closed captions (c)", controlBarController.captionsButton, 0, true);
            else controlBarController.captions = new ControlTooltip(mainController,"Subtitles/CC not selected", controlBarController.captionsButton, 0, true);

            controlBarController.settings = new ControlTooltip(mainController,"Settings (s)", controlBarController.settingsButton, 0, true);

            controlBarController.miniplayer = new ControlTooltip(mainController,"Miniplayer (i)", controlBarController.miniplayerButton, 0, true);

            if (App.fullScreen)
                controlBarController.fullScreen = new ControlTooltip(mainController,"Exit full screen (f)", controlBarController.fullScreenButton, 0, true);
            else
                controlBarController.fullScreen = new ControlTooltip(mainController,"Full screen (f)", controlBarController.fullScreenButton, 0, true);
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

        if(controlBarController.durationSliderHover || controlBarController.durationSlider.isValueChanging()){
            mainController.sliderHoverLabel.label.setVisible(true);
            if(menuController.activeItem != null && menuController.activeItem.getMediaItem().hasVideo()) mainController.sliderHoverPreview.pane.setVisible(true);
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

    public void closeSettingsFromCaptions(){
        FadeTransition backgroundTranslate = new FadeTransition(Duration.millis(ANIMATION_SPEED), settingsBackground);
        backgroundTranslate.setFromValue(1);
        backgroundTranslate.setToValue(0);

        FadeTransition captionsPaneTransition = new FadeTransition(Duration.millis(ANIMATION_SPEED), captionsController.captionsPane.captionsBox);
        captionsPaneTransition.setFromValue(1);
        captionsPaneTransition.setToValue(0);

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, captionsPaneTransition);
        parallelTransition.setOnFinished((e) -> {
            animating.set(false);

            settingsBuffer.setMouseTransparent(true);
            settingsBackground.setVisible(false);
            settingsBackground.setMouseTransparent(true);
            captionsController.captionsPane.captionsBox.setVisible(false);
            captionsController.captionsPane.captionsBox.setMouseTransparent(true);
            captionsController.captionsPane.captionsBox.setOpacity(1);
            clip.setHeight(settingsHomeController.settingsHome.getHeight());
        });

        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.play();
        animating.set(true);
    }

    public void closeSettingsFromCaptionsOptions(){
        FadeTransition backgroundTranslate = new FadeTransition(Duration.millis(ANIMATION_SPEED), settingsBackground);
        backgroundTranslate.setFromValue(1);
        backgroundTranslate.setToValue(0);

        FadeTransition captionsOptionsTransition = new FadeTransition(Duration.millis(ANIMATION_SPEED), captionsController.captionsOptionsPane.scrollPane);
        captionsOptionsTransition.setFromValue(1);
        captionsOptionsTransition.setToValue(0);

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, captionsOptionsTransition);
        parallelTransition.setOnFinished((e) -> {
            animating.set(false);

            settingsBuffer.setMouseTransparent(true);
            settingsBackground.setVisible(false);
            settingsBackground.setMouseTransparent(true);
            captionsController.captionsOptionsPane.scrollPane.setVisible(false);
            captionsController.captionsOptionsPane.scrollPane.setMouseTransparent(true);
            captionsController.captionsOptionsPane.scrollPane.setOpacity(1);
            clip.setHeight(settingsHomeController.settingsHome.getHeight());
            clip.setWidth(settingsHomeController.settingsHome.getWidth());
        });

        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.play();
        animating.set(true);
    }

    public void closeSettingsFromFontFamily(){
        FadeTransition backgroundTranslate = new FadeTransition(Duration.millis(ANIMATION_SPEED), settingsBackground);
        backgroundTranslate.setFromValue(1);
        backgroundTranslate.setToValue(0);

        FadeTransition fontFamilyTransition = new FadeTransition(Duration.millis(ANIMATION_SPEED), captionsController.captionsOptionsPane.fontFamilyPane.scrollPane);
        fontFamilyTransition.setFromValue(1);
        fontFamilyTransition.setToValue(0);

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, fontFamilyTransition);
        parallelTransition.setOnFinished((e) -> {
            animating.set(false);

            settingsBuffer.setMouseTransparent(true);
            settingsBackground.setVisible(false);
            settingsBackground.setMouseTransparent(true);
            captionsController.captionsOptionsPane.fontFamilyPane.scrollPane.setVisible(false);
            captionsController.captionsOptionsPane.fontFamilyPane.scrollPane.setMouseTransparent(true);
            captionsController.captionsOptionsPane.fontFamilyPane.scrollPane.setOpacity(1);
            clip.setHeight(settingsHomeController.settingsHome.getHeight());
            clip.setWidth(settingsHomeController.settingsHome.getWidth());
        });

        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.play();
        animating.set(true);
    }

    public void closeSettingsFromFontColor(){
        FadeTransition backgroundTranslate = new FadeTransition(Duration.millis(ANIMATION_SPEED), settingsBackground);
        backgroundTranslate.setFromValue(1);
        backgroundTranslate.setToValue(0);

        FadeTransition fontColorTransition = new FadeTransition(Duration.millis(ANIMATION_SPEED), captionsController.captionsOptionsPane.fontColorPane.scrollPane);
        fontColorTransition.setFromValue(1);
        fontColorTransition.setToValue(0);

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, fontColorTransition);
        parallelTransition.setOnFinished((e) -> {
            animating.set(false);

            settingsBuffer.setMouseTransparent(true);
            settingsBackground.setVisible(false);
            settingsBackground.setMouseTransparent(true);
            captionsController.captionsOptionsPane.fontColorPane.scrollPane.setVisible(false);
            captionsController.captionsOptionsPane.fontColorPane.scrollPane.setMouseTransparent(true);
            captionsController.captionsOptionsPane.fontColorPane.scrollPane.setOpacity(1);
            clip.setHeight(settingsHomeController.settingsHome.getHeight());
            clip.setWidth(settingsHomeController.settingsHome.getWidth());
        });

        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.play();
        animating.set(true);
    }

    public void closeSettingsFromFontSize(){
        FadeTransition backgroundTranslate = new FadeTransition(Duration.millis(ANIMATION_SPEED), settingsBackground);
        backgroundTranslate.setFromValue(1);
        backgroundTranslate.setToValue(0);

        FadeTransition fontSizeTransition = new FadeTransition(Duration.millis(ANIMATION_SPEED), captionsController.captionsOptionsPane.fontSizePane.scrollPane);
        fontSizeTransition.setFromValue(1);
        fontSizeTransition.setToValue(0);

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, fontSizeTransition);
        parallelTransition.setOnFinished((e) -> {
            animating.set(false);

            settingsBuffer.setMouseTransparent(true);
            settingsBackground.setVisible(false);
            settingsBackground.setMouseTransparent(true);
            captionsController.captionsOptionsPane.fontSizePane.scrollPane.setVisible(false);
            captionsController.captionsOptionsPane.fontSizePane.scrollPane.setMouseTransparent(true);
            captionsController.captionsOptionsPane.fontSizePane.scrollPane.setOpacity(1);
            clip.setHeight(settingsHomeController.settingsHome.getHeight());
            clip.setWidth(settingsHomeController.settingsHome.getWidth());
        });

        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.play();
        animating.set(true);
    }

    public void closeSettingsFromTextAlignment(){
        FadeTransition backgroundTranslate = new FadeTransition(Duration.millis(ANIMATION_SPEED), settingsBackground);
        backgroundTranslate.setFromValue(1);
        backgroundTranslate.setToValue(0);

        FadeTransition textAlignmentTransition = new FadeTransition(Duration.millis(ANIMATION_SPEED), captionsController.captionsOptionsPane.textAlignmentPane.scrollPane);
        textAlignmentTransition.setFromValue(1);
        textAlignmentTransition.setToValue(0);

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, textAlignmentTransition);
        parallelTransition.setOnFinished((e) -> {
            animating.set(false);

            settingsBuffer.setMouseTransparent(true);
            settingsBackground.setVisible(false);
            settingsBackground.setMouseTransparent(true);
            captionsController.captionsOptionsPane.textAlignmentPane.scrollPane.setVisible(false);
            captionsController.captionsOptionsPane.textAlignmentPane.scrollPane.setMouseTransparent(true);
            captionsController.captionsOptionsPane.textAlignmentPane.scrollPane.setOpacity(1);
            clip.setHeight(settingsHomeController.settingsHome.getHeight());
            clip.setWidth(settingsHomeController.settingsHome.getWidth());
        });

        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.play();
        animating.set(true);
    }

    public void closeSettingsFromBackgroundColor(){
        FadeTransition backgroundTranslate = new FadeTransition(Duration.millis(ANIMATION_SPEED), settingsBackground);
        backgroundTranslate.setFromValue(1);
        backgroundTranslate.setToValue(0);

        FadeTransition backgroundColorTransition = new FadeTransition(Duration.millis(ANIMATION_SPEED), captionsController.captionsOptionsPane.backgroundColorPane.scrollPane);
        backgroundColorTransition.setFromValue(1);
        backgroundColorTransition.setToValue(0);

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, backgroundColorTransition);
        parallelTransition.setOnFinished((e) -> {
            animating.set(false);

            settingsBuffer.setMouseTransparent(true);
            settingsBackground.setVisible(false);
            settingsBackground.setMouseTransparent(true);
            captionsController.captionsOptionsPane.backgroundColorPane.scrollPane.setVisible(false);
            captionsController.captionsOptionsPane.backgroundColorPane.scrollPane.setMouseTransparent(true);
            captionsController.captionsOptionsPane.backgroundColorPane.scrollPane.setOpacity(1);
            clip.setHeight(settingsHomeController.settingsHome.getHeight());
            clip.setWidth(settingsHomeController.settingsHome.getWidth());
        });

        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.play();
        animating.set(true);
    }

    public void closeSettingsFromBackgroundOpacity(){
        FadeTransition backgroundTranslate = new FadeTransition(Duration.millis(ANIMATION_SPEED), settingsBackground);
        backgroundTranslate.setFromValue(1);
        backgroundTranslate.setToValue(0);

        FadeTransition backgroundOpacityTransition = new FadeTransition(Duration.millis(ANIMATION_SPEED), captionsController.captionsOptionsPane.backgroundOpacityPane.scrollPane);
        backgroundOpacityTransition.setFromValue(1);
        backgroundOpacityTransition.setToValue(0);

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, backgroundOpacityTransition);
        parallelTransition.setOnFinished((e) -> {
            animating.set(false);

            settingsBuffer.setMouseTransparent(true);
            settingsBackground.setVisible(false);
            settingsBackground.setMouseTransparent(true);
            captionsController.captionsOptionsPane.backgroundOpacityPane.scrollPane.setVisible(false);
            captionsController.captionsOptionsPane.backgroundOpacityPane.scrollPane.setMouseTransparent(true);
            captionsController.captionsOptionsPane.backgroundOpacityPane.scrollPane.setOpacity(1);
            clip.setHeight(settingsHomeController.settingsHome.getHeight());
            clip.setWidth(settingsHomeController.settingsHome.getWidth());
        });

        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.play();
        animating.set(true);
    }

    public void closeSettingsFromLineSpacing(){
        FadeTransition backgroundTranslate = new FadeTransition(Duration.millis(ANIMATION_SPEED), settingsBackground);
        backgroundTranslate.setFromValue(1);
        backgroundTranslate.setToValue(0);

        FadeTransition lineSpacingTransition = new FadeTransition(Duration.millis(ANIMATION_SPEED), captionsController.captionsOptionsPane.lineSpacingPane.scrollPane);
        lineSpacingTransition.setFromValue(1);
        lineSpacingTransition.setToValue(0);

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, lineSpacingTransition);
        parallelTransition.setOnFinished((e) -> {
            animating.set(false);

            settingsBuffer.setMouseTransparent(true);
            settingsBackground.setVisible(false);
            settingsBackground.setMouseTransparent(true);
            captionsController.captionsOptionsPane.lineSpacingPane.scrollPane.setVisible(false);
            captionsController.captionsOptionsPane.lineSpacingPane.scrollPane.setMouseTransparent(true);
            captionsController.captionsOptionsPane.lineSpacingPane.scrollPane.setOpacity(1);
            clip.setHeight(settingsHomeController.settingsHome.getHeight());
            clip.setWidth(settingsHomeController.settingsHome.getWidth());
        });

        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.play();
        animating.set(true);
    }

    public void closeSettingsFromOpacity(){
        FadeTransition backgroundTranslate = new FadeTransition(Duration.millis(ANIMATION_SPEED), settingsBackground);
        backgroundTranslate.setFromValue(1);
        backgroundTranslate.setToValue(0);

        FadeTransition opacityTransition = new FadeTransition(Duration.millis(ANIMATION_SPEED), captionsController.captionsOptionsPane.fontOpacityPane.scrollPane);
        opacityTransition.setFromValue(1);
        opacityTransition.setToValue(0);

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, opacityTransition);
        parallelTransition.setOnFinished((e) -> {
            animating.set(false);

            settingsBuffer.setMouseTransparent(true);
            settingsBackground.setVisible(false);
            settingsBackground.setMouseTransparent(true);
            captionsController.captionsOptionsPane.fontOpacityPane.scrollPane.setVisible(false);
            captionsController.captionsOptionsPane.fontOpacityPane.scrollPane.setMouseTransparent(true);
            captionsController.captionsOptionsPane.fontOpacityPane.scrollPane.setOpacity(1);
            clip.setHeight(settingsHomeController.settingsHome.getHeight());
            clip.setWidth(settingsHomeController.settingsHome.getWidth());
        });

        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.play();
        animating.set(true);
    }

}