package hans;

import javafx.animation.PauseTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.TabPane;
import javafx.util.Duration;

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

                if (mainController.playing && !mainController.captionsOpen && !settingsController.settingsOpen && !controlBarController.volumeSlider.isValueChanging() && !controlBarController.durationSlider.isValueChanging() && controlBarController.controlBarOpen) {
                    AnimationsClass.hideControls(controlBarController); // hides controlbar if no mouse or other relevant events have not occurred in the last 4 seconds and the video is not paused, settings page and captions page are not open and user is not seeking video or changing volume
                }
            } else if (isNowMoving) {

                if (!controlBarController.controlBarOpen) {
                    AnimationsClass.displayControls(controlBarController); // displays controlbar if the mouse starts moving or any relevant key is pressed
                }
            }
        });

        // countdown that changes mouseMoving property to false if it reaches the end
        pause = new PauseTransition(Duration.millis(delay * 1000));
        pause.setOnFinished(e -> mouseMoving.set(false));

    }

    public void move() { // resets the countdown when called
        mouseMoving.set(true);
        pause.playFromStart();
    }

    public void hide(){
        mouseMoving.set(false);
    }

}
