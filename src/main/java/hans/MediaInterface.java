package hans;


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

    // all videos that have been added to the queue or directly to the player
    List<Media> videoList = new ArrayList<>();


    // videoList minus the videos that have already been played
    List<Media> unplayedVideoList = new ArrayList<>();


    //if this is null, mediainterface will select a video from unplayedvideolist to play next, else this video will be played
    Media nextMedia;


    // contains all the videos that have been played, in the order that they were played (necessary to navigate videos with the control arrows)
    List<Media> playedVideoList = new ArrayList<>();



    // keeps track of position inside the video history list, if -1 the user is not currently inside the played video list (hasnt used the back arrow to play previous videos)
    int playedVideoIndex = -1;

    Media currentVideo;
    File currentFile; // create file-type object of the video aswell to get name of the video

    int currentVideoIndex = -1;

    int lastVideoIndex = -1; // will be set only if loading a video from the nextMedia variable (to store the position the queue was at before jumping to nextMedia

    MediaPlayer mediaPlayer;

    // Variables to keep track of mediaplayer status:
    boolean playing = false; // is mediaplayer currently playing
    boolean wasPlaying = false; // was mediaplayer playing before a seeking action occurred
    public boolean atEnd = false; // is mediaplayer at the end of the video
    public boolean seekedToEnd = false; // true = video was seeked to the end; false = video naturally reached the end or the video is still playing
    ////////////////////////////////////////////////

    MediaInterface(MainController mainController, ControlBarController controlBarController, SettingsController settingsController) {
        this.mainController = mainController;
        this.controlBarController = controlBarController;
        this.settingsController = settingsController;
    };

    public void updateMedia(double newValue) {

        if (!controlBarController.showingTimeLeft)
            Utilities.setCurrentTimeLabel(controlBarController.durationLabel, mediaPlayer, currentVideo);
        else
            Utilities.setTimeLeftLabel(controlBarController.durationLabel, mediaPlayer, currentVideo);

        if (atEnd) {
            atEnd = false;
            seekedToEnd = false;

            if (wasPlaying) {
                if(mainController.menuController != null) {
                    mainController.menuController.activeItem.playIcon.setShape(mainController.menuController.activeItem.pauseSVG);
                    mainController.menuController.activeItem.play.updateText("Pause video");
                }

                if (!controlBarController.durationSlider.isValueChanging()) {

                    controlBarController.playIcon.setShape(controlBarController.pauseSVG);

                    playing = true;
                    mediaPlayer.play();

                    if (controlBarController.play.isShowing() || controlBarController.replay.isShowing()) {
                        controlBarController.play.hide();
                        controlBarController.replay.hide();
                        controlBarController.pause = new ControlTooltip("Pause (k)", controlBarController.playButton, false, controlBarController.controlBar, 0);
                        controlBarController.pause.showTooltip();
                    } else {
                        controlBarController.pause = new ControlTooltip("Pause (k)", controlBarController.playButton, false, controlBarController.controlBar, 0);
                    }

                    if(mainController.menuController != null){
                        mainController.menuController.activeItem.playIcon.setShape(mainController.menuController.activeItem.pauseSVG);
                        mainController.menuController.activeItem.play.updateText("Pause video");
                    }

                }
            } else {
                controlBarController.playIcon.setShape(controlBarController.playSVG);
                playing = false;

                if (controlBarController.pause.isShowing() || controlBarController.replay.isShowing()) {
                    controlBarController.pause.hide();
                    controlBarController.replay.hide();
                    controlBarController.play = new ControlTooltip("Play (k)", controlBarController.playButton, false, controlBarController.controlBar, 0);
                    controlBarController.play.showTooltip();
                } else {
                    controlBarController.play = new ControlTooltip("Play (k)", controlBarController.playButton, false, controlBarController.controlBar, 0);
                }

                if(mainController.menuController != null){
                    mainController.menuController.activeItem.playIcon.setShape(mainController.menuController.activeItem.playSVG);
                    mainController.menuController.activeItem.play.updateText("Play video");
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



    }

    public void endMedia() {


        if ((!settingsController.shuffleOn && !settingsController.loopOn && !settingsController.autoplayOn) || (settingsController.loopOn && seekedToEnd)) {
            controlBarController.durationSlider.setValue(controlBarController.durationSlider.getMax());

            controlBarController.durationLabel.textProperty().unbind();
            controlBarController.durationLabel.setText(Utilities.getTime(new Duration(controlBarController.durationSlider.getMax() * 1000)) + "/" + Utilities.getTime(currentVideo.getDuration()));


            controlBarController.playIcon.setShape(controlBarController.replaySVG);

            if(mainController.menuController != null){
                mainController.menuController.activeItem.playIcon.setShape(mainController.menuController.activeItem.playSVG);
                mainController.menuController.activeItem.play.updateText("Play video");
            }

            if (controlBarController.play.isShowing() || controlBarController.pause.isShowing()) {
                controlBarController.play.hide();
                controlBarController.pause.hide();
                controlBarController.replay = new ControlTooltip("Replay (k)", controlBarController.playButton, false, controlBarController.controlBar, 0);
                controlBarController.replay.showTooltip();
            } else {
                controlBarController.replay = new ControlTooltip("Replay (k)", controlBarController.playButton, false, controlBarController.controlBar, 0);
            }

            controlBarController.playButton.setOnAction((e) -> controlBarController.playButtonClick2());

            if (!controlBarController.controlBarOpen) {
                controlBarController.displayControls();
            }


        } else if (settingsController.loopOn) {
            controlBarController.mouseEventTracker.move();

            // restart current video
            mediaPlayer.stop();

        }
        else if (settingsController.shuffleOn || settingsController.autoplayOn) playNext();

    }

    public void createMediaPlayer(Media media) {

        this.currentVideo = media;

        currentVideoIndex = videoList.indexOf(media);

        if(mainController.menuController != null){
            mainController.menuController.queue.get(currentVideoIndex).setActive();
        }

        // resets all media state variables before creating a new player
        atEnd = false;
        seekedToEnd = false;
        playing = false;
        wasPlaying = false;

        controlBarController.durationSlider.setValue(0);

        if (unplayedVideoList.contains(currentVideo)) unplayedVideoList.remove(currentVideo);

        mediaPlayer = new MediaPlayer(currentVideo);


        mainController.mediaView.setMediaPlayer(mediaPlayer);

        // update video name field in settings pane and the stage title with the new video
        Platform.runLater(() -> {
            currentFile = new File(currentVideo.getSource().replaceAll("%20", " "));
            settingsController.videoNameText.setText(currentFile.getName()); // updates video name text in settings pane and window title with filename
            App.stage.setTitle(currentFile.getName());
        });

        mediaPlayer.currentTimeProperty().addListener((observableValue, oldTime, newTime) -> {
            if (!controlBarController.showingTimeLeft)
                Utilities.setCurrentTimeLabel(controlBarController.durationLabel, mediaPlayer, currentVideo);
            else
                Utilities.setTimeLeftLabel(controlBarController.durationLabel, mediaPlayer, currentVideo);

            if (!controlBarController.durationSlider.isValueChanging()) {
                controlBarController.durationSlider.setValue(newTime.toSeconds());
            }

        });


        mediaPlayer.setOnReady(() -> {

            //System.out.println(mediaPlayer.getMedia().getMetadata());

            mediaPlayer.setVolume(controlBarController.volumeSlider.getValue() / 100);

            controlBarController.play();

            controlBarController.durationSlider.setMax(Math.floor(currentVideo.getDuration().toSeconds()));

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
        if(playedVideoIndex == -1) {
            if(currentVideo != null) playedVideoList.add(currentVideo);
        }

        currentVideo = null;

        currentVideoIndex = -1;

        if(mediaPlayer != null) mediaPlayer.dispose();
        mainController.mediaView.setMediaPlayer(null);

        controlBarController.durationSlider.setValue(0);

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

    public void playNext(){
        controlBarController.mouseEventTracker.move();

        int temp = currentVideoIndex; // saves the currentVideoIndex to a temporary variable because the next line resets currentVideoIndex to null
        resetMediaPlayer();

        if(nextMedia != null){
            lastVideoIndex = temp;
            createMediaPlayer(nextMedia);
            nextMedia = null;
            return;
        }
        else if(playedVideoIndex != -1 && playedVideoIndex < playedVideoList.size() - 1){
            // play next video inside playedVideoList
            playedVideoIndex+=1;
            createMediaPlayer(playedVideoList.get(playedVideoIndex));
        }
        else {
            playedVideoIndex = -1;
            if(settingsController.shuffleOn) playRandom();
            else if(lastVideoIndex != -1) autoplay(lastVideoIndex); // pass the copy of currentVideoIndex to autplay method
            else autoplay(temp);
        }

        lastVideoIndex = -1;
    }

    public void playPrevious(){
        controlBarController.mouseEventTracker.move();
        System.out.println(playedVideoList.size());

        resetMediaPlayer();

        if(playedVideoIndex == -1){
            playedVideoList.remove(playedVideoList.size() - 1);
            playedVideoIndex = playedVideoList.size() - 1;
        }
        else playedVideoIndex--;
        createMediaPlayer(playedVideoList.get(playedVideoIndex));
    }

    public void playRandom() {
        if(unplayedVideoList.isEmpty()){
            for(Media media : videoList){
                unplayedVideoList.add(media);
            }
        }
        Random random = new Random();
        int randomIndex = random.nextInt(unplayedVideoList.size());
        createMediaPlayer(unplayedVideoList.get(randomIndex));
    }

    public void autoplay(int wasCurrentVideo){
        if(videoList.size() > wasCurrentVideo + 1){ // get next video inside the videoList and play it
            createMediaPlayer(videoList.get(wasCurrentVideo + 1));
        }
        else { // current video is last inside the videoLis
               // get the first video inside videoList and play it
            createMediaPlayer(videoList.get(0));
        }
    }

    public void setNextMedia(Media media){
        nextMedia = media;
    }

}
