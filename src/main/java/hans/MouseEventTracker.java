package hans;

import hans.Captions.CaptionsState;
import hans.Menu.MenuState;
import hans.Settings.SettingsController;
import hans.Settings.SettingsState;
import javafx.animation.PauseTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Cursor;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MouseEventTracker {

    int delay; // delay how long mouse has to stay still for controlbar to hide

    MainController mainController;
    ControlBarController controlBarController;
    SettingsController settingsController;


    BooleanProperty mouseMoving = new SimpleBooleanProperty(); // keeps track of the mouse state (have any mouse events taken place in the last x seconds or not
    PauseTransition pause;

    MouseEventTracker(int delay, MainController mainController, ControlBarController controlBarController, SettingsController settingsController) {
        this.delay = delay;
        this.mainController = mainController;
        this.controlBarController = controlBarController;
        this.settingsController = settingsController;

        mouseMoving.addListener((obs, wasMoving, isNowMoving) -> {
            if (!isNowMoving) {
                if (mainController.mediaInterface.playing.get() && settingsController.settingsState == SettingsState.CLOSED && mainController.captionsController.captionsState == CaptionsState.CLOSED && !controlBarController.volumeSlider.isValueChanging() && !controlBarController.durationSlider.isValueChanging() && controlBarController.controlBarOpen && !mainController.captionsController.captionsBox.captionsDragActive) {
                    controlBarController.controlBarWrapper.setMouseTransparent(true);
                    AnimationsClass.hideControls(controlBarController, controlBarController.captionsController, mainController); // hides controlbar if no mouse or other relevant events have not occurred in the last 4 seconds and the video is not paused, settings page and captions page are not open and user is not seeking video or changing volume
                    mainController.videoTitleLabel.getScene().setCursor(Cursor.NONE);
                    mainController.videoTitleBox.setMouseTransparent(true);
                    mainController.captionsController.captionsBox.captionsContainer.setMouseTransparent(true);
                    System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")));
                }
            }
            else {
                if(!controlBarController.controlBarOpen) {
                    controlBarController.controlBarWrapper.setMouseTransparent(false);
                    AnimationsClass.displayControls(controlBarController, controlBarController.captionsController, mainController); // displays controlbar if the mouse starts moving or any relevant key is pressed
                    mainController.videoTitleLabel.getScene().setCursor(Cursor.DEFAULT);
                    mainController.videoTitleBox.setMouseTransparent(false);
                    if(mainController.captionsController.captionsSelected.get()) mainController.captionsController.captionsBox.captionsContainer.setMouseTransparent(false);

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
