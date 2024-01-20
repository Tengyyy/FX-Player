package fxplayer;

import fxplayer.subtitles.SubtitlesState;
import fxplayer.menu.MenuState;
import fxplayer.playbackSettings.PlaybackSettingsController;
import fxplayer.playbackSettings.PlaybackSettingsState;
import javafx.animation.PauseTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Cursor;
import javafx.util.Duration;
import fxplayer.windows.WindowState;

public class MouseEventTracker {

    static final int delay = 4; // delay how long mouse has to stay still for controlbar to hide

    MainController mainController;
    ControlBarController controlBarController;
    PlaybackSettingsController playbackSettingsController;

    BooleanProperty mouseMoving = new SimpleBooleanProperty(); // keeps track of the mouse state (have any mouse events taken place in the last x seconds or not
    PauseTransition pause;

    MouseEventTracker(MainController mainController, ControlBarController controlBarController, PlaybackSettingsController playbackSettingsController) {
        this.mainController = mainController;
        this.controlBarController = controlBarController;
        this.playbackSettingsController = playbackSettingsController;

        mouseMoving.addListener((obs, wasMoving, isNowMoving) -> {
            if (!isNowMoving) {
                if (mainController.mediaInterface.playing.get()
                        && playbackSettingsController.playbackSettingsState == PlaybackSettingsState.CLOSED
                        && mainController.subtitlesController.subtitlesState == SubtitlesState.CLOSED
                        && !controlBarController.volumeSlider.isValueChanging()
                        && !controlBarController.durationSlider.isValueChanging()
                        && !mainController.subtitlesController.subtitlesBox.subtitlesDragActive
                        && mainController.windowController.windowState == WindowState.CLOSED) {
                    if(controlBarController.titleShowing) controlBarController.hideTitle();

                    if(mainController.getMenuController().menuState == MenuState.CLOSED || !mainController.getMenuController().extended.get()) {
                        if (controlBarController.controlBarShowing) controlBarController.hideControls();
                    }

                    if(mainController.getMenuController().menuState == MenuState.CLOSED){
                        mainController.videoTitleLabel.getScene().setCursor(Cursor.NONE);
                    }
                }
            }
            else {
                mainController.videoTitleLabel.getScene().setCursor(Cursor.DEFAULT);
                if(!controlBarController.titleShowing) controlBarController.showTitle();
                if(!controlBarController.controlBarShowing) controlBarController.showControls();
            }
        });

        // countdown that changes mouseMoving property to false if it reaches the end
        pause = new PauseTransition(Duration.seconds(delay));
        pause.setOnFinished(e -> mouseMoving.set(false));
    }

    public void move() { // resets the countdown when called

        if(mainController.getMenuController().menuState != MenuState.CLOSED) return;

        mouseMoving.set(true);
        pause.playFromStart();
    }

    public void hide(){
        mouseMoving.set(false);
    }

}
