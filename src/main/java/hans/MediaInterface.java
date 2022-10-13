package hans;


import hans.MediaItems.MediaItem;
import hans.Menu.HistoryItem;
import hans.Menu.MenuController;
import hans.Menu.MenuObject;
import hans.Menu.QueueItem;
import hans.Settings.SettingsController;
import javafx.animation.PauseTransition;
import javafx.application.Platform;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.image.Image;
import javafx.util.Duration;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.JavaFXFrameConverter;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import uk.co.caprica.vlcj.javafx.videosurface.ImageViewVideoSurface;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MediaInterface {

    MainController mainController;
    ControlBarController controlBarController;
    SettingsController settingsController;
    MenuController menuController;

    public MediaPlayerFactory mediaPlayerFactory;

    public EmbeddedMediaPlayer embeddedMediaPlayer;

    // Variables to keep track of mediaplayer status:
    public BooleanProperty mediaActive = new SimpleBooleanProperty(false); // is the mediaplayer active (is any video currently loaded in)
    public BooleanProperty playing = new SimpleBooleanProperty(false); // is mediaplayer currently playing
    public boolean wasPlaying = false; // was mediaplayer playing before a seeking action occurred
    public boolean atEnd = false; // is mediaplayer at the end of the video
    public boolean seekedToEnd = false; // true = video was seeked to the end; false = video naturally reached the end or the video is still playing
    ////////////////////////////////////////////////


    public PauseTransition transitionTimer;

    double currentTime;

    FFmpegFrameGrabber fFmpegFrameGrabber;

    FrameGrabberTask frameGrabberTask;


    MediaInterface(MainController mainController, ControlBarController controlBarController, SettingsController settingsController, MenuController menuController) {
        this.mainController = mainController;
        this.controlBarController = controlBarController;
        this.settingsController = settingsController;
        this.menuController = menuController;


        mediaActive.addListener((observableValue, oldValue, newValue) -> {
            controlBarController.durationPane.setMouseTransparent(!newValue);
            if(mainController.miniplayerActive) mainController.miniplayer.miniplayerController.sliderPane.setMouseTransparent(!newValue);


            if(newValue){
                if(!controlBarController.playButtonEnabled) controlBarController.enablePlayButton();
                if(mainController.miniplayerActive){
                    mainController.miniplayer.miniplayerController.enablePlayButton();
                    mainController.miniplayerActiveText.setVisible(true);
                }
            }
            else {
                if(controlBarController.playButtonEnabled) {
                    controlBarController.pause();
                    controlBarController.disablePlayButton();
                }
                if(mainController.miniplayerActive){
                    mainController.miniplayer.miniplayerController.pause();
                    mainController.miniplayer.miniplayerController.disablePlayButton();
                }
                mainController.miniplayerActiveText.setVisible(false);
            }
        });
    }

    public void init(){
        this.mediaPlayerFactory = new MediaPlayerFactory();
        this.embeddedMediaPlayer = mediaPlayerFactory.mediaPlayers().newEmbeddedMediaPlayer();


        embeddedMediaPlayer.videoSurface().set(new ImageViewVideoSurface(mainController.videoImageView));
        embeddedMediaPlayer.audio().setVolume((int) controlBarController.volumeSlider.getValue());

        embeddedMediaPlayer.events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {


            @Override
            public void finished(MediaPlayer mediaPlayer) {
                Platform.runLater(() -> controlBarController.durationSlider.setValue(controlBarController.durationSlider.getMax()));
            }

            @Override
            public void stopped(MediaPlayer mediaPlayer){
                // mediaplayer is stoppped when opening/closing miniplayer, have to make sure this code here doesnt mess with that
            }

            @Override
            public void timeChanged(MediaPlayer mediaPlayer, long newTime) {

                currentTime = newTime;

                Platform.runLater(() -> {
                    if(Math.abs(currentTime/1000 - controlBarController.durationSlider.getValue()) > 0.5)controlBarController.durationSlider.setValue((double)newTime/1000);
                });
            }

            @Override
            public void mediaPlayerReady(MediaPlayer mediaPlayer) {

                Platform.runLater(() -> {
                    controlBarController.durationSlider.setMax((double)mediaPlayer.media().info().duration()/1000);
                    if(mainController.miniplayerActive) mainController.miniplayer.miniplayerController.slider.setMax((double)mediaPlayer.media().info().duration()/1000);

                    mediaActive.set(true);

                    play();
                });

            }


        });

    }

    public void updateMedia(double newValue) {

        if (!controlBarController.showingTimeLeft)
                Utilities.setCurrentTimeLabel(controlBarController.durationLabel, controlBarController.durationSlider, Duration.millis(embeddedMediaPlayer.media().info().duration()));
            else
                Utilities.setTimeLeftLabel(controlBarController.durationLabel, controlBarController.durationSlider, Duration.millis(embeddedMediaPlayer.media().info().duration()));

            if (atEnd) {
                atEnd = false;
                seekedToEnd = false;

                if (wasPlaying && (!controlBarController.durationSlider.isValueChanging() && (!mainController.miniplayerActive || !mainController.miniplayer.miniplayerController.slider.isValueChanging()))) {

                    play();

                } else {

                    pause();

                }
            }



            // this final block will probably have to be modified
            else if (newValue >= controlBarController.durationSlider.getMax()) {
                if (controlBarController.durationSlider.isValueChanging() || (mainController.miniplayerActive && mainController.miniplayer.miniplayerController.slider.isValueChanging())) {
                    seekedToEnd = true;
                }

                atEnd = true;
                playing.set(false);

                if (!controlBarController.durationSlider.isValueChanging() && (!mainController.miniplayerActive || !mainController.miniplayer.miniplayerController.slider.isValueChanging())) {
                    endMedia();
                }
            }

            if(Math.abs(currentTime/1000 - newValue) > 0.5 || (!playing.get() && Math.abs(currentTime/1000 - newValue) >= 0.1)) {
                currentTime = newValue;
                seek(Duration.seconds(newValue));
            }
            else if(newValue == 0){
                currentTime = newValue;
                seek(Duration.ZERO);
            }



    }

    public void endMedia() {


        if ((!settingsController.playbackOptionsController.shuffleOn && !settingsController.playbackOptionsController.loopOn && !settingsController.playbackOptionsController.autoplayOn) || (settingsController.playbackOptionsController.loopOn && seekedToEnd)) {
            defaultEnd();

        } else if (settingsController.playbackOptionsController.loopOn) {
            controlBarController.mouseEventTracker.move();

            // restart current video
            embeddedMediaPlayer.controls().start();

        }
        else {
            if((menuController.historyBox.index == -1 || menuController.historyBox.index >= menuController.history.size() -1) && menuController.queue.isEmpty()) defaultEnd();
            else requestNext();

        }

    }

    public void createMedia(MenuObject menuObject) {

        MediaItem mediaItem = menuObject.getMediaItem();

        // resets all media state variables before creating a new player
        atEnd = false;
        seekedToEnd = false;
        playing.set(false);
        wasPlaying = false;
        currentTime = 0;

        fFmpegFrameGrabber = new FFmpegFrameGrabber(menuController.activeItem.getMediaItem().getFile());


        try {
            fFmpegFrameGrabber.start();
        } catch (FFmpegFrameGrabber.Exception e) {
            e.printStackTrace();
        }


        if(mediaItem.getSubtitles() != null){
            settingsController.captionsController.loadCaptions(mediaItem.getSubtitles(), mediaItem.getSubtitlesOn());
        }
        else if(settingsController.captionsController.captionsSelected){
            mediaItem.setSubtitles(settingsController.captionsController.captionsFile);
            if(menuController.captionsController.captionsOn.get()) mediaItem.setSubtitlesOn(true);
            if(menuController.activeItem != null && !menuController.activeItem.subTextWrapper.getChildren().contains(menuController.activeItem.captionsPane)) menuController.activeItem.subTextWrapper.getChildren().add(0, menuController.activeItem.captionsPane);
        }

        mainController.videoTitleLabel.setText(menuObject.getTitle());

        controlBarController.durationSlider.setValue(0);

        mainController.metadataButton.setOnAction(e -> menuObject.showMetadata());


        if(!menuObject.getMediaItem().hasVideo()){
            if(mainController.miniplayerActive){
                if(menuObject.getMediaItem().hasCover()) mainController.miniplayer.miniplayerController.videoImageView.setImage(menuObject.getMediaItem().getCover());
                else mainController.miniplayer.miniplayerController.videoImageView.setImage(menuObject.getMediaItem().getPlaceholderCover());
            }
            else {
                if(menuObject.getMediaItem().hasCover()) mainController.videoImageView.setImage(menuObject.getMediaItem().getCover());
                else mainController.videoImageView.setImage(menuObject.getMediaItem().getPlaceholderCover());
            }
        }

        controlBarController.updateButtonState();


        embeddedMediaPlayer.media().start(mediaItem.getFile().getAbsolutePath());
        embeddedMediaPlayer.audio().setVolume((int) controlBarController.volumeSlider.getValue());

    }


    public void resetMediaPlayer(boolean disableButtons){

        mainController.videoImageView.setImage(null);
        if(mainController.miniplayerActive) mainController.miniplayer.miniplayerController.videoImageView.setImage(null);

        controlBarController.durationSlider.setValue(0);

        embeddedMediaPlayer.controls().stop();


        if(controlBarController.showingTimeLeft) controlBarController.durationLabel.setText("−00:00/00:00");
        else controlBarController.durationLabel.setText("00:00/00:00");

        mainController.videoTitleLabel.setText(null);

        if(settingsController.playbackOptionsController.loopOn) settingsController.playbackOptionsController.loopTab.toggle.fire();

        mediaActive.set(false);

        mainController.sliderHoverPreview.setImage(null);

        if(disableButtons){
            controlBarController.disablePreviousVideoButton();
            controlBarController.disableNextVideoButton();
        }

        try {
            fFmpegFrameGrabber.close();
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        }
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

        controlBarController.durationLabel.setText(Utilities.getTime(new Duration(controlBarController.durationSlider.getMax() * 1000)) + "/" + Utilities.getTime(new Duration(controlBarController.durationSlider.getMax() * 1000)));

        controlBarController.mouseEventTracker.move();

        // add logic to update all the play icons
        controlBarController.end();
        if(mainController.miniplayerActive) mainController.miniplayer.miniplayerController.end();
        if(menuController.activeItem != null)menuController.activeItem.updateIconToPlay();

        playing.set(false);


    }

    public void play() {

        if(!mediaActive.get()) return;

        playing.set(true);


        embeddedMediaPlayer.controls().play();


            if(mainController.miniplayerActive) mainController.miniplayer.miniplayerController.play();

            controlBarController.play();

            if(menuController.activeItem != null) menuController.activeItem.updateIconToPause();

            wasPlaying = playing.get();

    }

    public void pause(){

        if(!mediaActive.get()) return;

        if(playing.get()) {
            playing.set(false);
            embeddedMediaPlayer.controls().pause();
        }


            if(mainController.miniplayerActive) mainController.miniplayer.miniplayerController.pause();

            controlBarController.pause();

            if(menuController.activeItem != null)menuController.activeItem.updateIconToPlay();

    }

    public void replay(){

            controlBarController.durationSlider.setValue(0);

        seek(Duration.ZERO);
        play();



        atEnd = false;
        seekedToEnd = false;
    }


    public void seek(Duration time){
        embeddedMediaPlayer.controls().setTime((long) time.toMillis());
    }

    public void changeVolume(double value){
        embeddedMediaPlayer.audio().setVolume((int) value);
    }

    public void changePlaybackSpeed(double value){
        embeddedMediaPlayer.controls().setRate((float) value);
    }

    public Duration getCurrentTime(){
        return Duration.millis(currentTime);
    }



    public void updatePreviewFrame() {

        if(frameGrabberTask != null && frameGrabberTask.isRunning()) return;

        frameGrabberTask = new FrameGrabberTask(fFmpegFrameGrabber, controlBarController);

        frameGrabberTask.setOnSucceeded((succeededEvent) -> {
            mainController.sliderHoverPreview.imageView.setImage(frameGrabberTask.getValue());
        });



        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(frameGrabberTask);
        executorService.shutdown();

    }
}
