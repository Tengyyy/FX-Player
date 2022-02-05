package hans;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MediaInterface {

    MainController mainController;
    ControlBarController controlBarController;
    SettingsController settingsController;


    ArrayList<File> videoQueue = new ArrayList<File>();

    Media media;
    MediaPlayer mediaPlayer;

    // Variables to keep track of mediaplayer status:
    boolean playing = false; // is mediaplayer currently playing
    boolean wasPlaying = false; // was mediaplayer playing before a seeking action occurred
    public boolean atEnd = false; // is mediaplayer at the end of the video
    public boolean seekedToEnd = false; // true = video was seeked to the end; false = video naturally reached the end or the video is still playing
    ////////////////////////////////////////////////

    MediaInterface(MainController mainController, ControlBarController controlBarController, SettingsController settingsController){
        this.mainController = mainController;
        this.controlBarController = controlBarController;
        this.settingsController = settingsController;
    }

    public void updateMedia(double newValue) {
        if (!controlBarController.showingTimeLeft)
            Utilities.setCurrentTimeLabel(controlBarController.durationLabel, mediaPlayer, media);
        else
            Utilities.setTimeLeftLabel(controlBarController.durationLabel, mediaPlayer, media);

        if (atEnd) {
            atEnd = false;
            seekedToEnd = false;

            if (wasPlaying) {
                if (!controlBarController.durationSlider.isValueChanging()) {
                    controlBarController.playLogo.setImage(controlBarController.pauseImage);

                    playing = true;
                    mediaPlayer.play();

                    if (controlBarController.play.isShowing() || controlBarController.replay.isShowing()) {
                        controlBarController.play.hide();
                        controlBarController.replay.hide();
                        controlBarController.pause = new ControlTooltip("Pause (k)", controlBarController.playButton, false, controlBarController.controlBar);
                        controlBarController.pause.showTooltip();
                    } else {
                        controlBarController.pause = new ControlTooltip("Pause (k)", controlBarController.playButton, false, controlBarController.controlBar);
                    }
                }
            } else {
                controlBarController.playLogo.setImage(controlBarController.playImage);
                playing = false;

                if (controlBarController.pause.isShowing() || controlBarController.replay.isShowing()) {
                    controlBarController.pause.hide();
                    controlBarController.replay.hide();
                    controlBarController.play = new ControlTooltip("Play (k)", controlBarController.playButton, false, controlBarController.controlBar);
                    controlBarController.play.showTooltip();
                } else {
                    controlBarController.play = new ControlTooltip("Play (k)", controlBarController.playButton, false, controlBarController.controlBar);
                }

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

        controlBarController.durationTrack.setProgress(controlBarController.durationSlider.getValue() / controlBarController.durationSlider.getMax());


    }

    public void endMedia() {

        if ((!settingsController.shuffleOn && !settingsController.loopOn && !settingsController.autoplayOn) || (settingsController.loopOn && seekedToEnd)) {
            controlBarController.durationSlider.setValue(controlBarController.durationSlider.getMax());

            controlBarController.durationLabel.textProperty().unbind();
            controlBarController.durationLabel.setText(Utilities.getTime(new Duration(controlBarController.durationSlider.getMax() * 1000)) + "/" + Utilities.getTime(media.getDuration()));


            controlBarController.playLogo.setImage(new Image(controlBarController.replayFile.toURI().toString()));

            if (controlBarController.play.isShowing() || controlBarController.pause.isShowing()) {
                controlBarController.play.hide();
                controlBarController.pause.hide();
                controlBarController.replay = new ControlTooltip("Replay (k)", controlBarController.playButton, false, controlBarController.controlBar);
                controlBarController.replay.showTooltip();
            } else {
                controlBarController.replay = new ControlTooltip("Replay (k)", controlBarController.playButton, false, controlBarController.controlBar);
            }

            controlBarController.playButton.setOnAction((e) -> controlBarController.playButtonClick2());

            if (!controlBarController.controlBarOpen) {
                controlBarController.displayControls();
            }


        } else if (settingsController.loopOn && !seekedToEnd) {
            // restart current video


            mediaPlayer.stop();

        } else if (settingsController.shuffleOn) {

            //if(!controlBarController.controlBarOpen) {
            //	controlBarController.displayControls();
            //}

        } else if (settingsController.autoplayOn) {
            // play next song in queue/directory

            //if(!controlBarController.controlBarOpen) {
            //	controlBarController.displayControls();
            //}
        }

    }

    public void createMediaPlayer(File file) {

        controlBarController.durationSlider.setValue(0);

        media = new Media(file.toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mainController.mediaView.setMediaPlayer(mediaPlayer);

        mediaPlayer.currentTimeProperty().addListener((observableValue, oldTime, newTime) -> {
            if (!controlBarController.showingTimeLeft)
                Utilities.setCurrentTimeLabel(controlBarController.durationLabel, mediaPlayer, media);
            else
                Utilities.setTimeLeftLabel(controlBarController.durationLabel, mediaPlayer, media);

            if (!controlBarController.durationSlider.isValueChanging()) {
                controlBarController.durationSlider.setValue(newTime.toSeconds());
            }

        });


        mediaPlayer.setOnReady(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub

                mediaPlayer.setVolume(/*controlBarController.volumeSlider.getValue() / 100*/ 0);

                controlBarController.play();

                controlBarController.durationSlider.setMax(Math.floor(media.getDuration().toSeconds()));

                TimerTask setRate = new TimerTask() {

                    @Override
                    public void run() {

                        switch (settingsController.playbackSpeedTracker) {
                            case 0:
                                mediaPlayer.setRate(settingsController.formattedValue);
                                break;
                            case 1:
                                mediaPlayer.setRate(0.25);
                                break;
                            case 2:
                                mediaPlayer.setRate(0.5);
                                break;
                            case 3:
                                mediaPlayer.setRate(0.75);
                                break;
                            case 4:
                                mediaPlayer.setRate(1);
                                break;
                            case 5:
                                mediaPlayer.setRate(1.25);
                                break;
                            case 6:
                                mediaPlayer.setRate(1.5);
                                break;
                            case 7:
                                mediaPlayer.setRate(1.75);
                                break;
                            case 8:
                                mediaPlayer.setRate(2);
                                break;
                            default:
                                break;
                        }
                    }

                };

                Timer timer = new Timer();

                // this is mega stupid but it works
                timer.schedule(setRate, 200);
            }

        });

    }
}
