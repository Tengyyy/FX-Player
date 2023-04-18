package hans;

import hans.Subtitles.SubtitlesState;
import hans.Menu.MenuState;
import hans.PlaybackSettings.PlaybackSettingsController;
import hans.PlaybackSettings.PlaybackSettingsState;
import javafx.animation.PauseTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Cursor;
import javafx.util.Duration;

public class MouseEventTracker {

    int delay; // delay how long mouse has to stay still for controlbar to hide

    MainController mainController;
    ControlBarController controlBarController;
    PlaybackSettingsController playbackSettingsController;


    BooleanProperty mouseMoving = new SimpleBooleanProperty(); // keeps track of the mouse state (have any mouse events taken place in the last x seconds or not
    PauseTransition pause;

    MouseEventTracker(int delay, MainController mainController, ControlBarController controlBarController, PlaybackSettingsController playbackSettingsController) {
        this.delay = delay;
        this.mainController = mainController;
        this.controlBarController = controlBarController;
        this.playbackSettingsController = playbackSettingsController;

        mouseMoving.addListener((obs, wasMoving, isNowMoving) -> {
            if (!isNowMoving) {
                if (mainController.mediaInterface.playing.get() && playbackSettingsController.playbackSettingsState == PlaybackSettingsState.CLOSED && mainController.subtitlesController.subtitlesState == SubtitlesState.CLOSED && !controlBarController.volumeSlider.isValueChanging() && !controlBarController.durationSlider.isValueChanging() && controlBarController.controlBarOpen && !mainController.subtitlesController.subtitlesBox.subtitlesDragActive && (mainController.getMenuController().menuState == MenuState.CLOSED || !mainController.getMenuController().extended.get())) {
                    controlBarController.controlBarWrapper.setMouseTransparent(true);
                    AnimationsClass.hideControlsAndTitle(controlBarController, controlBarController.subtitlesController, mainController); // hides controlbar if no mouse or other relevant events have not occurred in the last 4 seconds and the video is not paused, settings page and captions page are not open and user is not seeking video or changing volume
                    mainController.videoTitleLabel.getScene().setCursor(Cursor.NONE);
                    mainController.videoTitleBox.setMouseTransparent(true);
                    mainController.subtitlesController.subtitlesBox.subtitlesContainer.setMouseTransparent(true);
                }
            }
            else {
                if(!controlBarController.controlBarOpen) {
                    controlBarController.controlBarWrapper.setMouseTransparent(false);
                    AnimationsClass.displayControls(controlBarController, controlBarController.subtitlesController, mainController); // displays controlbar if the mouse starts moving or any relevant key is pressed
                    AnimationsClass.displayTitle(mainController);
                    mainController.videoTitleLabel.getScene().setCursor(Cursor.DEFAULT);
                    mainController.videoTitleBox.setMouseTransparent(false);
                    if(mainController.subtitlesController.subtitlesSelected.get()) mainController.subtitlesController.subtitlesBox.subtitlesContainer.setMouseTransparent(false);

                }
            }
        });

        // countdown that changes mouseMoving property to false if it reaches the end
        pause = new PauseTransition(Duration.millis(delay * 1000));
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
