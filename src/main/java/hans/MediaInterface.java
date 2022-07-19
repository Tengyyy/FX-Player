package hans;


import javafx.animation.PauseTransition;
import javafx.application.Platform;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.SubtitleTrack;
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
    BooleanProperty playing = new SimpleBooleanProperty(false); // is mediaplayer currently playing
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

        
        playing.addListener((observableValue, oldValue, newValue) -> {
            if(!menuController.mediaActive.get()) return;

            if(newValue) menuController.activeItem.columns.play();
            else menuController.activeItem.columns.pause();
        });
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
                if(menuController.mediaActive.get()) {
                    menuController.activeItem.playIcon.setShape(menuController.activeItem.pauseSVG);
                    menuController.activeItem.play.updateText("Pause video");

                    if(menuController.historyBox.index != -1){
                        HistoryItem historyItem = menuController.history.get(menuController.historyBox.index);
                        historyItem.playIcon.setShape(historyItem.pauseSVG);
                        historyItem.play.updateText("Pause video");
                    }
                }

                if (!controlBarController.durationSlider.isValueChanging()) {

                    controlBarController.playIcon.setShape(controlBarController.pauseSVG);
                    controlBarController.playIcon.setPrefSize(20, 20);


                    playing.set(true);
                    mediaPlayer.play();

                    controlBarController.play.updateText("Pause (k)");

                    if(mainController.miniplayerActive){
                        mainController.miniplayer.miniplayerController.play();
                    }

                }
            } else {
                controlBarController.playIcon.setShape(controlBarController.playSVG);
                controlBarController.playIcon.setPrefSize(20, 20);

                playing.set(false);

                controlBarController.play.updateText("Play (k)");

                if(mainController.miniplayerActive){
                    mainController.miniplayer.miniplayerController.pause();
                }

                if(menuController.mediaActive.get()) {
                    menuController.activeItem.playIcon.setShape(menuController.activeItem.playSVG);
                    menuController.activeItem.play.updateText("Play video");

                    if(menuController.historyBox.index != -1){
                        HistoryItem historyItem = menuController.history.get(menuController.historyBox.index);
                        historyItem.playIcon.setShape(historyItem.playSVG);
                        historyItem.play.updateText("Play video");
                    }
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
            playing.set(false);
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


        if ((!settingsController.playbackOptionsController.shuffleOn && !settingsController.playbackOptionsController.loopOn && !settingsController.playbackOptionsController.autoplayOn) || (settingsController.playbackOptionsController.loopOn && seekedToEnd)) {
            defaultEnd();

        } else if (settingsController.playbackOptionsController.loopOn) {
            controlBarController.mouseEventTracker.move();

            // restart current video
            mediaPlayer.stop();

        }
        else if (settingsController.playbackOptionsController.shuffleOn || settingsController.playbackOptionsController.autoplayOn) {
            if((menuController.historyBox.index == -1 || menuController.historyBox.index >= menuController.history.size() -1) && menuController.queue.isEmpty()) defaultEnd();
            else requestNext();

        }

    }

    public void createMediaPlayer(MenuObject menuObject) {

        MediaItem mediaItem = menuObject.getMediaItem();

        // resets all media state variables before creating a new player
        atEnd = false;
        seekedToEnd = false;
        playing.set(false);
        wasPlaying = false;

        mediaPlayer = new MediaPlayer(mediaItem.getMedia());

        if(mediaItem.getSubtitles() != null){
            settingsController.captionsController.loadCaptions(mediaItem.getSubtitles(), mediaItem.getSubtitlesOn());
        }
        else if(settingsController.captionsController.captionsSelected){
            mediaItem.setSubtitles(settingsController.captionsController.captionsFile);
            if(menuController.captionsController.captionsOn.get()) mediaItem.setSubtitlesOn(true);
            if(menuController.activeItem != null && !menuController.activeItem.subTextWrapper.getChildren().contains(menuController.activeItem.captionsPane)) menuController.activeItem.subTextWrapper.getChildren().add(0, menuController.activeItem.captionsPane);
        }

        controlBarController.durationSlider.setValue(0);

        if(mainController.miniplayerActive){
            mainController.miniplayer.miniplayerController.mediaView.setMediaPlayer(mediaPlayer);
            mainController.miniplayerActiveText.setVisible(true);
        }
        else {
            mainController.mediaView.setMediaPlayer(mediaPlayer);
        }

        if(mainController.miniplayerActive && !mainController.miniplayer.miniplayerController.playButtonEnabled) mainController.miniplayer.miniplayerController.enablePlayButton();
        if(!controlBarController.playButtonEnabled) controlBarController.enablePlayButton();

        if((menuController.historyBox.index == -1  || menuController.historyBox.index == menuController.history.size() -1) && menuController.queue.isEmpty()){
            if(menuController.mainController.miniplayerActive && menuController.mainController.miniplayer.miniplayerController.nextVideoButtonEnabled) menuController.mainController.miniplayer.miniplayerController.disableNextVideoButton();
            if(controlBarController.nextVideoButtonEnabled) controlBarController.disableNextVideoButton();
        }
        else if(menuController.historyBox.index != -1 && menuController.historyBox.index < menuController.history.size() -1){
            if(menuController.mainController.miniplayerActive && !menuController.mainController.miniplayer.miniplayerController.nextVideoButtonEnabled) menuController.mainController.miniplayer.miniplayerController.enableNextVideoButton();
            if(!controlBarController.nextVideoButtonEnabled) controlBarController.enableNextVideoButton();
        }


        if(menuController.history.isEmpty() || menuController.historyBox.index == 0 && controlBarController.durationSlider.getValue() <= 5){
            if(menuController.mainController.miniplayerActive && menuController.mainController.miniplayer.miniplayerController.previousVideoButtonEnabled) menuController.mainController.miniplayer.miniplayerController.disablePreviousVideoButton();
            if(controlBarController.previousVideoButtonEnabled) controlBarController.disablePreviousVideoButton();
        }
        else if(!menuController.history.isEmpty() && (menuController.historyBox.index == -1 || menuController.historyBox.index > 0)){
            if(menuController.mainController.miniplayerActive && !menuController.mainController.miniplayer.miniplayerController.previousVideoButtonEnabled) menuController.mainController.miniplayer.miniplayerController.enablePreviousVideoButton();
            if(!controlBarController.previousVideoButtonEnabled) controlBarController.enablePreviousVideoButton();
        }



        App.setFrameDuration(mediaItem.getFrameDuration());

        // update video name field in settings pane and the stage title with the new video
        Platform.runLater(() -> {
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


            //////////////////////             SUBTITLES              //////////////////////
            if(!menuController.captionsController.subtitles.isEmpty() &&
                    menuController.captionsController.captionsPosition >= 0 &&
                    menuController.captionsController.captionsPosition < menuController.captionsController.subtitles.size() &&
                    menuController.captionsController.captionsOn.get() &&
                    !menuController.captionsController.captionsDragActive){


                if(newTime.toMillis() >= menuController.captionsController.subtitles.get(menuController.captionsController.captionsPosition).timeIn && newTime.toMillis() < menuController.captionsController.subtitles.get(menuController.captionsController.captionsPosition).timeOut && !menuController.captionsController.showedCurrentCaption){
                    String text = menuController.captionsController.subtitles.get(menuController.captionsController.captionsPosition).text;

                    // if the subtitle contains a new line character then split the subtitle into two and add the part after the new line onto another label

                    String[] subtitleLines = Utilities.splitLines(text);

                    if(subtitleLines.length == 2){
                        menuController.captionsController.captionsLabel1.setOpacity(1);
                        menuController.captionsController.captionsLabel2.setOpacity(1);
                        menuController.captionsController.captionsLabel1.setText(subtitleLines[0]);
                        menuController.captionsController.captionsLabel2.setText(subtitleLines[1]);
                    }
                    else {
                        menuController.captionsController.captionsLabel1.setOpacity(0);
                        menuController.captionsController.captionsLabel2.setOpacity(1);
                        menuController.captionsController.captionsLabel2.setText(subtitleLines[0]);
                    }

                    menuController.captionsController.showedCurrentCaption = true;
                }
                else if((newTime.toMillis() >= menuController.captionsController.subtitles.get(menuController.captionsController.captionsPosition).timeOut && menuController.captionsController.captionsPosition >= menuController.captionsController.subtitles.size() - 1) || (newTime.toMillis() >= menuController.captionsController.subtitles.get(menuController.captionsController.captionsPosition).timeOut && newTime.toMillis() < menuController.captionsController.subtitles.get(menuController.captionsController.captionsPosition + 1).timeIn)){
                    menuController.captionsController.captionsLabel1.setOpacity(0);
                    menuController.captionsController.captionsLabel2.setOpacity(0);
                }
                else if(newTime.toMillis() < menuController.captionsController.subtitles.get(menuController.captionsController.captionsPosition).timeIn && menuController.captionsController.captionsPosition > 0){
                    do {
                        menuController.captionsController.captionsPosition--;
                        menuController.captionsController.showedCurrentCaption = false;
                    }
                    while (newTime.toMillis() < menuController.captionsController.subtitles.get(menuController.captionsController.captionsPosition).timeIn && menuController.captionsController.captionsPosition > 0);
                }
                else if(menuController.captionsController.captionsPosition <  menuController.captionsController.subtitles.size() - 1 && newTime.toMillis() >= menuController.captionsController.subtitles.get(menuController.captionsController.captionsPosition + 1).timeIn){
                    do {
                        menuController.captionsController.captionsPosition++;
                        menuController.captionsController.showedCurrentCaption = false;
                    }
                    while (menuController.captionsController.captionsPosition <  menuController.captionsController.subtitles.size() - 1 && newTime.toMillis() >= menuController.captionsController.subtitles.get(menuController.captionsController.captionsPosition + 1).timeIn);
                }
            }
        });


        mediaPlayer.setOnReady(() -> {

            mediaPlayer.setVolume(controlBarController.volumeSlider.getValue() / 100);

            controlBarController.play();

            controlBarController.durationSlider.setMax(Math.floor(mediaItem.getMedia().getDuration().toSeconds()));

            TimerTask setRate = new TimerTask() {

                @Override
                public void run() {
                    mediaPlayer.setRate(settingsController.playbackSpeedController.speed);
                }
            };

            Timer timer = new Timer();

            timer.schedule(setRate, 200);
        });

    }

    public void resetMediaPlayer(){

        if(mediaPlayer != null) mediaPlayer.dispose();
        mainController.mediaView.setMediaPlayer(null);
        if(mainController.miniplayerActive){
            mainController.miniplayer.miniplayerController.mediaView.setMediaPlayer(null);
            if(mainController.miniplayer.miniplayerController.playButtonEnabled) mainController.miniplayer.miniplayerController.disablePlayButton();
        }

        if(controlBarController.playButtonEnabled) controlBarController.disablePlayButton();

        controlBarController.durationSlider.setValue(0);

        App.setFrameDuration(1 / 30);
        App.stage.setTitle("MP4 Player");

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
        controlBarController.playIcon.setPrefSize(24, 24);


        menuController.activeItem.playIcon.setShape(menuController.activeItem.playSVG);
        menuController.activeItem.play.updateText("Play video");

        if(menuController.historyBox.index != -1){
            HistoryItem historyItem = menuController.history.get(menuController.historyBox.index);
            historyItem.playIcon.setShape(historyItem.playSVG);
            historyItem.play.updateText("Play video");
        }

        controlBarController.play.updateText("Replay (k)");

        if(mainController.miniplayerActive) mainController.miniplayer.miniplayerController.end();

        controlBarController.playButton.setOnAction((e) -> controlBarController.playButtonClick2());

        if (!controlBarController.controlBarOpen) {
            controlBarController.displayControls();
        }

    }
}
