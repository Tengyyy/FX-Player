package hans;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

import java.util.ArrayList;

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


    boolean settingsOpen = false;
    boolean settingsHomeOpen = false;
    boolean playbackSpeedPaneOpen = false;
    boolean customSpeedPaneOpen = false;
    boolean playbackOptionsPaneOpen = false;
    boolean captionsPaneOpen = false;

    Rectangle clip = new Rectangle();

    BooleanProperty animating = new SimpleBooleanProperty(); // animating state of the settings pane


    static final int ANIMATION_SPEED = 200;

    SettingsController(MainController mainController, ControlBarController controlBarController, MenuController menuController){

        this.mainController = mainController;
        this.controlBarController = controlBarController;
        this.menuController = menuController;

        animating.set(false);

        settingsBuffer.setPrefSize(235, 156);
        settingsBuffer.setMinSize(235, 156);
        settingsBuffer.setMaxWidth(235);
        settingsBuffer.setClip(clip);
        settingsBuffer.getChildren().add(settingsBackground);
        settingsBuffer.setMouseTransparent(true);
        settingsBackground.setId("settingsBackground");
        settingsBackground.setVisible(false);
        settingsBackground.setMouseTransparent(true);
        StackPane.setMargin(settingsBuffer, new Insets(0, 20, 80, 0));
        StackPane.setAlignment(settingsBackground, Pos.BOTTOM_CENTER);


        Platform.runLater(() -> {
            settingsBuffer.maxHeightProperty().bind(Bindings.subtract(mainController.mediaViewHeight, 120));
            clip.setWidth(settingsBuffer.getWidth());
            clip.setHeight(settingsHomeController.settingsHome.getHeight());
            clip.translateYProperty().bind(Bindings.subtract(settingsBuffer.heightProperty(), clip.heightProperty()));
            settingsBackground.maxHeightProperty().bind(clip.heightProperty());
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

        settingsOpen = true;
        settingsHomeOpen = true;

        if (controlBarController.captions.isShowing() || controlBarController.settings.isShowing() || controlBarController.fullScreen.isShowing() || controlBarController.exitFullScreen.isShowing()) {
            controlBarController.captions.hide();
            controlBarController.settings.hide();
            controlBarController.fullScreen.hide();
            controlBarController.exitFullScreen.hide();
        }
        controlBarController.captionsButton.setOnMouseEntered(null);
        controlBarController.settingsButton.setOnMouseEntered(null);
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


        settingsOpen = false;


        if (controlBarController.settingsButtonHover) {
            controlBarController.settings = new ControlTooltip("Settings (s)", controlBarController.settingsButton, controlBarController.controlBarWrapper, 0, false);
            controlBarController.settings.showTooltip();

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

            controlBarController.settings = new ControlTooltip("Settings (s)", controlBarController.settingsButton, controlBarController.controlBarWrapper, 0, false);

            if (App.fullScreen)
                controlBarController.exitFullScreen = new ControlTooltip("Exit full screen (f)", controlBarController.fullScreenButton, controlBarController.controlBarWrapper, 0, false);
            else
                controlBarController.fullScreen = new ControlTooltip("Full screen (f)", controlBarController.fullScreenButton, controlBarController.controlBarWrapper, 0, false);
        } else if (controlBarController.fullScreenButtonHover) {
            if (App.fullScreen) {
                controlBarController.exitFullScreen = new ControlTooltip("Exit full screen (f)", controlBarController.fullScreenButton, controlBarController.controlBarWrapper, 0, false);
                controlBarController.exitFullScreen.showTooltip();
            } else {
                controlBarController.fullScreen = new ControlTooltip("Full screen (f)", controlBarController.fullScreenButton, controlBarController.controlBarWrapper, 0, false);
                controlBarController.fullScreen.showTooltip();
            }

            if(captionsController.captionsSelected) controlBarController.captions = new ControlTooltip("Subtitles/closed captions (c)", controlBarController.captionsButton, controlBarController.controlBarWrapper, 0, false);
            else controlBarController.captions = new ControlTooltip("Subtitles/CC not selected", controlBarController.captionsButton, controlBarController.controlBarWrapper, 0, false);

            controlBarController.settings = new ControlTooltip("Settings (s)", controlBarController.settingsButton, controlBarController.controlBarWrapper, 0, false);
        } else {
            if(captionsController.captionsSelected) controlBarController.captions = new ControlTooltip("Subtitles/closed captions (c)", controlBarController.captionsButton, controlBarController.controlBarWrapper, 0, false);
            else controlBarController.captions = new ControlTooltip("Subtitles/CC not selected", controlBarController.captionsButton, controlBarController.controlBarWrapper, 0, false);

            controlBarController.settings = new ControlTooltip("Settings (s)", controlBarController.settingsButton, controlBarController.controlBarWrapper, 0, false);

            if (App.fullScreen)
                controlBarController.exitFullScreen = new ControlTooltip("Exit full screen (f)", controlBarController.fullScreenButton, controlBarController.controlBarWrapper, 0, false);
            else
                controlBarController.fullScreen = new ControlTooltip("Full screen (f)", controlBarController.fullScreenButton, controlBarController.controlBarWrapper, 0, false);
        }

        if(settingsHomeOpen){
            settingsHomeOpen = false;

            TranslateTransition backgroundTranslate = new TranslateTransition(Duration.millis(ANIMATION_SPEED), settingsBackground);
            backgroundTranslate.setFromY(0);
            backgroundTranslate.setToY(settingsHomeController.settingsHome.getHeight());

            TranslateTransition homeTranslate = new TranslateTransition(Duration.millis(ANIMATION_SPEED), settingsHomeController.settingsHome);
            homeTranslate.setFromY(0);
            homeTranslate.setToY(settingsHomeController.settingsHome.getHeight());

            ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, homeTranslate);
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
        else if(playbackOptionsPaneOpen){
            playbackOptionsPaneOpen = false;

            TranslateTransition backgroundTranslate = new TranslateTransition(Duration.millis(ANIMATION_SPEED), settingsBackground);
            backgroundTranslate.setFromY(0);
            backgroundTranslate.setToY(playbackOptionsController.playbackOptionsBox.getHeight());

            TranslateTransition playbackOptionsTranslate = new TranslateTransition(Duration.millis(ANIMATION_SPEED), playbackOptionsController.playbackOptionsBox);
            playbackOptionsTranslate.setFromY(0);
            playbackOptionsTranslate.setToY(playbackOptionsController.playbackOptionsBox.getHeight());

            ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, playbackOptionsTranslate);
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
        else if(playbackSpeedPaneOpen){
            playbackSpeedPaneOpen = false;

            TranslateTransition backgroundTranslate = new TranslateTransition(Duration.millis(ANIMATION_SPEED), settingsBackground);
            backgroundTranslate.setFromY(0);
            backgroundTranslate.setToY(playbackSpeedController.playbackSpeedPane.scrollPane.getHeight());

            TranslateTransition playbackSpeedTranslate = new TranslateTransition(Duration.millis(ANIMATION_SPEED), playbackSpeedController.playbackSpeedPane.scrollPane);
            playbackSpeedTranslate.setFromY(0);
            playbackSpeedTranslate.setToY(playbackSpeedController.playbackSpeedPane.scrollPane.getHeight());

            ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, playbackSpeedTranslate);
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
        else if(customSpeedPaneOpen){
            customSpeedPaneOpen = false;

            TranslateTransition backgroundTranslate = new TranslateTransition(Duration.millis(ANIMATION_SPEED), settingsBackground);
            backgroundTranslate.setFromY(0);
            backgroundTranslate.setToY(playbackSpeedController.customSpeedPane.customSpeedBox.getHeight());

            TranslateTransition customTranslate = new TranslateTransition(Duration.millis(ANIMATION_SPEED), playbackSpeedController.customSpeedPane.customSpeedBox);
            customTranslate.setFromY(0);
            customTranslate.setToY(playbackSpeedController.customSpeedPane.customSpeedBox.getHeight());

            ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, customTranslate);
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
        else if(captionsPaneOpen){
            captionsPaneOpen = false;

            TranslateTransition backgroundTranslate = new TranslateTransition(Duration.millis(ANIMATION_SPEED), settingsBackground);
            backgroundTranslate.setFromY(0);
            backgroundTranslate.setToY(playbackOptionsController.playbackOptionsBox.getHeight());

            TranslateTransition captionsPaneTranslate = new TranslateTransition(Duration.millis(ANIMATION_SPEED), captionsController.captionsPane.captionsBox);
            captionsPaneTranslate.setFromY(0);
            captionsPaneTranslate.setToY(captionsController.captionsPane.captionsBox.getHeight());

            ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, captionsPaneTranslate);
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

    }

}