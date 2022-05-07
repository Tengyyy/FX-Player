package hans;


import javafx.animation.PauseTransition;
import javafx.application.Platform;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.util.*;

public class MediaInterface {

    MainController mainController;
    ControlBarController controlBarController;
    SettingsController settingsController;
    MenuController menuController;

    MediaPlayer mediaPlayer;

    // Variables to keep track of mediaplayer status:
    boolean playing = false; // is mediaplayer currently playing
    boolean wasPlaying = false; // was mediaplayer playing before a seeking action occurred
    public boolean atEnd = false; // is mediaplayer at the end of the video
    public boolean seekedToEnd = false; // true = video was seeked to the end; false = video naturally reached the end or the video is still playing
    ////////////////////////////////////////////////


    PauseTransition transitionTimer;


    MediaInterface(MainController mainController, ControlBarController controlBarController, SettingsController settingsController, MenuController menuController) {
        this.mainController = mainController;
        this.controlBarController = controlBarController;
        this.settingsController = settingsController;
        this.menuController = menuController;
    };

    public void updateMedia(double newValue) {

        if (!controlBarController.showingTimeLeft)
            Utilities.setCurrentTimeLabel(controlBarController.durationLabel, mediaPlayer, menuController.activeItem.mediaItem.getMedia());
        else
            Utilities.setTimeLeftLabel(controlBarController.durationLabel, mediaPlayer, menuController.activeItem.mediaItem.getMedia());

        if (atEnd) {
            atEnd = false;
            seekedToEnd = false;

            if (wasPlaying) {
                    menuController.activeItem.playIcon.setShape(menuController.activeItem.pauseSVG);
                    menuController.activeItem.play.updateText("Pause video");

                if (!controlBarController.durationSlider.isValueChanging()) {

                    controlBarController.playIcon.setShape(controlBarController.pauseSVG);

                    playing = true;
                    mediaPlayer.play();

                    if (controlBarController.play.isShowing() || controlBarController.replay.isShowing()) {
                        controlBarController.play.hide();
                        controlBarController.replay.hide();
                        controlBarController.pause = new ControlTooltip("Pause (k)", controlBarController.playButton, controlBarController.controlBar, 0, false);
                        controlBarController.pause.showTooltip();
                    } else {
                        controlBarController.pause = new ControlTooltip("Pause (k)", controlBarController.playButton, controlBarController.controlBar, 0, false);
                    }

                        menuController.activeItem.playIcon.setShape(menuController.activeItem.pauseSVG);
                        menuController.activeItem.play.updateText("Pause video");

                }
            } else {
                controlBarController.playIcon.setShape(controlBarController.playSVG);
                playing = false;

                if (controlBarController.pause.isShowing() || controlBarController.replay.isShowing()) {
                    controlBarController.pause.hide();
                    controlBarController.replay.hide();
                    controlBarController.play = new ControlTooltip("Play (k)", controlBarController.playButton, controlBarController.controlBar, 0, false);
                    controlBarController.play.showTooltip();
                } else {
                    controlBarController.play = new ControlTooltip("Play (k)", controlBarController.playButton, controlBarController.controlBar, 0, false);
                }

                    menuController.activeItem.playIcon.setShape(menuController.activeItem.playSVG);
                    menuController.activeItem.play.updateText("Play video");

            }
            controlBarController.playButton.setOnAction((e) -> {
                controlBarController.playButtonClick1();
            });
        } else if (newValue >= controlBarController.durationSlider.getMax()) {

            if (controlBarController.durationSlider.isValueChanging()) {
                seekedToEnd = true;
            }

            atEnd = true;
            playing = false;
            mediaPlayer.pause();
            if (!controlBarController.durationSlider.isValueChanging()) {
                endMedia();
            }
        }

        if (Math.abs(mediaPlayer.getCurrentTime().toSeconds() - newValue) > 0.5) {
            mediaPlayer.seek(Duration.seconds(newValue));
        }



    }

    public void endMedia() {


        if ((!settingsController.shuffleOn && !settingsController.loopOn && !settingsController.autoplayOn) || (settingsController.loopOn && seekedToEnd)) {

            defaultEnd();

        } else if (settingsController.loopOn) {
            controlBarController.mouseEventTracker.move();

            // restart current video
            mediaPlayer.stop();

        }
        else if (settingsController.shuffleOn || settingsController.autoplayOn) requestNext();

    }

    public void createMediaPlayer(MenuObject menuObject) {

        MediaItem mediaItem = menuObject.getMediaItem();

        // resets all media state variables before creating a new player
        atEnd = false;
        seekedToEnd = false;
        playing = false;
        wasPlaying = false;

        controlBarController.durationSlider.setValue(0);


        mediaPlayer = new MediaPlayer(mediaItem.getMedia());


        mainController.mediaView.setMediaPlayer(mediaPlayer);
        App.setFrameDuration(mediaItem.getFrameDuration());

        // update video name field in settings pane and the stage title with the new video
        Platform.runLater(() -> {
            settingsController.videoNameText.setText(mediaItem.getFile().getName()); // updates video name text in settings pane and window title with filename
            App.stage.setTitle(mediaItem.getFile().getName());
        });

        mediaPlayer.currentTimeProperty().addListener((observableValue, oldTime, newTime) -> {
            if (!controlBarController.showingTimeLeft)
                Utilities.setCurrentTimeLabel(controlBarController.durationLabel, mediaPlayer, mediaItem.getMedia());
            else
                Utilities.setTimeLeftLabel(controlBarController.durationLabel, mediaPlayer, mediaItem.getMedia());

            if (!controlBarController.durationSlider.isValueChanging()) {
                controlBarController.durationSlider.setValue(newTime.toSeconds());
            }

        });


        mediaPlayer.setOnReady(() -> {

            mediaPlayer.setVolume(controlBarController.volumeSlider.getValue() / 100);

            controlBarController.play();

            controlBarController.durationSlider.setMax(Math.floor(mediaItem.getMedia().getDuration().toSeconds()));

            TimerTask setRate = new TimerTask() {

                @Override
                public void run() {
                    if(settingsController.playbackSpeedTracker == 0) mediaPlayer.setRate(settingsController.formattedValue);
                    else mediaPlayer.setRate(settingsController.playbackSpeedTracker / 4);
                }
            };

            Timer timer = new Timer();

            timer.schedule(setRate, 200);
        });

    }

    public void resetMediaPlayer(){

        if(mediaPlayer != null) mediaPlayer.dispose();
        mainController.mediaView.setMediaPlayer(null);

        controlBarController.durationSlider.setValue(0);

        App.setFrameDuration(1 / 30);
        App.stage.setTitle("MP4 Player");

        settingsController.videoNameText.setLayoutX(0);
        settingsController.videoNameText.setText("Select a video");

        if(controlBarController.showingTimeLeft) controlBarController.durationLabel.setText("−00:00/00:00");
        else controlBarController.durationLabel.setText("00:00/00:00");
    }

    public void addVideo(Media media){

    }

    public void addUnplayedVideo(Media media){

    }

    public void addPlayedVideo(Media Media){

    }

    public void removeVideo(Media media){
    }

    public void removePlayedVideo(Media media){

    }

    public void removeUnplayedVideo(Media media){

    }


    public void requestNext(){
        // called when current video reaches the end
        // if animationsInProgress list is empty, play next video, otherwise start a 1 second timer, at the end of which
        // check again if any animations are in progress, if there are, just end the video.
        // stop timer if user changes video while pausetransition is playing

        if(menuController.animationsInProgress.isEmpty()){
            playNext();
        }
        else {
            transitionTimer = new PauseTransition(Duration.millis(1000));
            transitionTimer.setOnFinished((e) -> {
                if(menuController.animationsInProgress.isEmpty()) playNext();
                else defaultEnd();
            });

            transitionTimer.playFromStart();
        }
    }

    public void playNext(){

        controlBarController.mouseEventTracker.move();

        if(menuController.historyBox.index != -1 && menuController.historyBox.index < menuController.history.size() -1){
            // play next video inside history
            HistoryItem historyItem =  menuController.history.get(menuController.historyBox.index + 1);
            historyItem.play();

        }
        else if((menuController.historyBox.index == menuController.history.size() -1 || menuController.historyBox.index == -1) && !menuController.queue.isEmpty()) {
            // play first item in queue

            QueueItem queueItem = menuController.queue.get(0);
            queueItem.play(true);

        }
    }

    public void playPrevious(){

        controlBarController.mouseEventTracker.move();

        if(!menuController.history.isEmpty() && menuController.historyBox.index == -1){
            // play most recent item in history
            HistoryItem historyItem = menuController.history.get(menuController.history.size() -1);
            historyItem.play();
        }
        else if(menuController.historyBox.index > 0){
            // play previous item
            HistoryItem historyItem = menuController.history.get(menuController.historyBox.index -1);
            historyItem.play();
        }
    }

    public void defaultEnd(){
        controlBarController.durationSlider.setValue(controlBarController.durationSlider.getMax());

        controlBarController.durationLabel.textProperty().unbind();
        controlBarController.durationLabel.setText(Utilities.getTime(new Duration(controlBarController.durationSlider.getMax() * 1000)) + "/" + Utilities.getTime(menuController.activeItem.mediaItem.getMedia().getDuration()));


        controlBarController.playIcon.setShape(controlBarController.replaySVG);

        menuController.activeItem.playIcon.setShape(menuController.activeItem.playSVG);
        menuController.activeItem.play.updateText("Play video");

        if (controlBarController.play.isShowing() || controlBarController.pause.isShowing()) {
            controlBarController.play.hide();
            controlBarController.pause.hide();
            controlBarController.replay = new ControlTooltip("Replay (k)", controlBarController.playButton, controlBarController.controlBar, 0, false);
            controlBarController.replay.showTooltip();
        } else {
            controlBarController.replay = new ControlTooltip("Replay (k)", controlBarController.playButton, controlBarController.controlBar, 0, false);
        }

        controlBarController.playButton.setOnAction((e) -> controlBarController.playButtonClick2());

        if (!controlBarController.controlBarOpen) {
            controlBarController.displayControls();
        }

    }
}
