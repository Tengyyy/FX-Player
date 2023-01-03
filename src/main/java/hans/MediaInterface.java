package hans;


import hans.Captions.CaptionsController;
import hans.MediaItems.MediaItem;
import hans.Menu.*;
import hans.Settings.SettingsController;
import javafx.animation.PauseTransition;
import javafx.application.Platform;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Cursor;
import javafx.util.Duration;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FrameGrabber;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.Equalizer;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import uk.co.caprica.vlcj.javafx.videosurface.ImageViewVideoSurface;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.bytedeco.ffmpeg.global.avformat.AV_DISPOSITION_DEFAULT;


public class MediaInterface {

    MainController mainController;
    ControlBarController controlBarController;
    SettingsController settingsController;
    MenuController menuController;
    CaptionsController captionsController;

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

    public FFmpegFrameGrabber fFmpegFrameGrabber;

    FrameGrabberTask frameGrabberTask;

    public SubtitleExtractionTask subtitleExtractionTask;
    public ExecutorService executorService;


    MediaInterface(MainController mainController, ControlBarController controlBarController, SettingsController settingsController, MenuController menuController, CaptionsController captionsController) {
        this.mainController = mainController;
        this.controlBarController = controlBarController;
        this.settingsController = settingsController;
        this.menuController = menuController;
        this.captionsController = captionsController;

        mediaActive.addListener((observableValue, oldValue, newValue) -> {
            controlBarController.durationPane.setMouseTransparent(!newValue);
            if(mainController.miniplayerActive) mainController.miniplayer.miniplayerController.sliderPane.setMouseTransparent(!newValue);


            if(newValue){
                if(!controlBarController.playButtonEnabled) controlBarController.enablePlayButton();
                if(mainController.miniplayerActive){
                    mainController.miniplayer.miniplayerController.enablePlayButton();
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
            }
        });
    }

    public void init(){

        String[] VLC_GLOBAL_OPTIONS = {
                "--no-sub-autodetect-file",
                "--no-spu",
                "--disable-screensaver"
        };

        this.mediaPlayerFactory = new MediaPlayerFactory(VLC_GLOBAL_OPTIONS);
        this.embeddedMediaPlayer = mediaPlayerFactory.mediaPlayers().newEmbeddedMediaPlayer();


        embeddedMediaPlayer.videoSurface().set(new ImageViewVideoSurface(mainController.videoImageView));
        embeddedMediaPlayer.audio().setEqualizer(new Equalizer(10));
        embeddedMediaPlayer.audio().equalizer().setPreamp(0);
        embeddedMediaPlayer.audio().setVolume(50);


        embeddedMediaPlayer.events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {


            @Override
            public void finished(MediaPlayer mediaPlayer) {
                Platform.runLater(() -> controlBarController.durationSlider.setValue(controlBarController.durationSlider.getMax()));
            }

            @Override
            public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
                currentTime = newTime;

                Platform.runLater(() -> {
                    if(Math.abs(currentTime/1000 - controlBarController.durationSlider.getValue()) > 0.5) controlBarController.durationSlider.setValue((double)newTime/1000);
                });
            }

            @Override
            public void mediaPlayerReady(MediaPlayer mediaPlayer) {

                mediaActive.set(true);

                Platform.runLater(() -> {
                    mediaPlayer.audio().setVolume((int) controlBarController.volumeSlider.getValue());
                    controlBarController.durationSlider.setMax((double)mediaPlayer.media().info().duration()/1000);
                    if(mainController.miniplayerActive) mainController.miniplayer.miniplayerController.slider.setMax((double)mediaPlayer.media().info().duration()/1000);

                    Utilities.setCurrentTimeLabel(controlBarController.durationLabel, Duration.ZERO, Duration.seconds(controlBarController.durationSlider.getMax()));

                    play();

                });

            }
        });

    }

    public void updateMedia(double newValue) {

        if (!controlBarController.showingTimeLeft)
            Utilities.setCurrentTimeLabel(controlBarController.durationLabel, Duration.seconds(controlBarController.durationSlider.getValue()), Duration.seconds(controlBarController.durationSlider.getMax()));
        else
            Utilities.setTimeLeftLabel(controlBarController.durationLabel, Duration.seconds(controlBarController.durationSlider.getValue()), Duration.seconds(controlBarController.durationSlider.getMax()));

        if (newValue >= controlBarController.durationSlider.getMax()) {
            if (controlBarController.durationSlider.isValueChanging() || (mainController.miniplayerActive && mainController.miniplayer.miniplayerController.slider.isValueChanging())) {
                seekedToEnd = true;
            }
            else if(seekedToEnd){
                defaultEnd();
            }
            else endMedia();

            atEnd = true;

            currentTime = controlBarController.durationSlider.getMax();

            playing.set(false);
            embeddedMediaPlayer.controls().setPause(true);
            SleepSuppressor.allowSleep();

            seek(Duration.seconds(newValue));

        }
        else {
            if(newValue == 0){
                currentTime = 0;
                seek(Duration.ZERO);
            }
            else if(Math.abs(currentTime/1000 - newValue) > 0.5 || (!playing.get() && Math.abs(currentTime/1000 - newValue) >= 0.1)) {
                currentTime = newValue;
                seek(Duration.seconds(newValue));
            }

            if (atEnd) {
                atEnd = false;
                seekedToEnd = false;

                if (wasPlaying && (!controlBarController.durationSlider.isValueChanging() && (!mainController.miniplayerActive || !mainController.miniplayer.miniplayerController.slider.isValueChanging()))) {
                    play();
                } else {
                    pause();
                }
            }
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

    public void createMedia(ActiveItem activeItem) {

        mainController.coverImageContainer.setVisible(false);
        mainController.miniplayerActiveText.setVisible(false);

        if(mainController.miniplayerActive){
            mainController.miniplayer.miniplayerController.videoImageView.setImage(null);
            mainController.miniplayer.miniplayerController.coverImageContainer.setVisible(false);
        }

        captionsController.resetCaptions();

        MediaItem mediaItem = activeItem.getMediaItem();

        if (mediaItem != null) {
            if(mediaItem.hasVideo()){
                fFmpegFrameGrabber = new FFmpegFrameGrabber(menuController.activeItem.getMediaItem().getFile());
                fFmpegFrameGrabber.setVideoDisposition(AV_DISPOSITION_DEFAULT);

                try {
                    fFmpegFrameGrabber.start();
                } catch (FFmpegFrameGrabber.Exception e) {
                    e.printStackTrace();
                }

                if(mainController.miniplayerActive){
                    mainController.miniplayerActiveText.setVisible(true);
                }
            }
            else {
                mainController.setCoverImageView(activeItem);
            }

            mainController.videoTitleLabel.setText(activeItem.getTitle());

            executorService = Executors.newFixedThreadPool(1);
            subtitleExtractionTask = new SubtitleExtractionTask(captionsController, activeItem);
            subtitleExtractionTask.setOnSucceeded(e -> {
                if(menuController.activeItem == activeItem){
                    captionsController.createSubtitleTabs(activeItem);
                }
            });
            executorService.execute(subtitleExtractionTask);
            executorService.shutdown();
        }


        controlBarController.durationSlider.setValue(0);

        mainController.metadataButton.setOnAction(e -> {
            if(mainController.playbackOptionsPopUp.isShowing()) mainController.playbackOptionsPopUp.hide();
            if(activeItem.getMediaItem() != null) activeItem.showMetadata();
        });

        controlBarController.durationLabel.setCursor(Cursor.HAND);
        controlBarController.durationLabel.setMouseTransparent(false);


        controlBarController.updateTooltips();


        embeddedMediaPlayer.media().start(activeItem.file.getAbsolutePath());
    }


    public void resetMediaPlayer(){

        mainController.videoImageView.setImage(null);
        if(mainController.miniplayerActive){
            mainController.miniplayer.miniplayerController.videoImageView.setImage(null);
            mainController.miniplayer.miniplayerController.coverImageContainer.setVisible(false);
        }

        mainController.coverImageContainer.setVisible(false);
        mainController.miniplayerActiveText.setVisible(false);

        mediaActive.set(false);

        controlBarController.durationSlider.setValue(0);

        controlBarController.durationLabel.setCursor(Cursor.DEFAULT);
        controlBarController.durationLabel.setMouseTransparent(true);

        embeddedMediaPlayer.controls().stop();
        SleepSuppressor.allowSleep();

        if(controlBarController.showingTimeLeft) controlBarController.durationLabel.setText("−00:00/00:00");
        else controlBarController.durationLabel.setText("00:00/00:00");

        mainController.videoTitleLabel.setText(null);

        if(settingsController.playbackOptionsController.loopOn) settingsController.playbackOptionsController.loopTab.toggle.fire();

        mainController.sliderHoverPreview.setImage(null);


        controlBarController.disablePreviousVideoButton();
        controlBarController.disableNextVideoButton();

        atEnd = false;
        seekedToEnd = false;
        playing.set(false);
        wasPlaying = false;
        currentTime = 0;

        try {
            if(fFmpegFrameGrabber != null) fFmpegFrameGrabber.close();
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

            if(menuController.queueBox.dragActive){
                // cancel dragging of queueitem
                menuController.scrollTimeline.stop();

                menuController.queueBox.draggedNode.setViewOrder(1);
                menuController.queueBox.draggedNode.setStyle("-fx-background-color: transparent");
                menuController.queueBox.draggedNode.dragPosition = 0;
                menuController.queueBox.draggedNode.minimumY = 0;
                menuController.queueBox.draggedNode.maximumY = 0;
                menuController.queueBox.dragActive = false;
                menuController.queueBox.draggedNode.setMouseTransparent(false);
                menuController.queueBox.draggedNode.playIcon.setVisible(false);
                menuController.queueBox.draggedNode.indexLabel.setVisible(true);

                if(menuController.queue.indexOf(menuController.queueBox.draggedNode) != menuController.queueBox.draggedNode.newPosition){
                    menuController.queue.remove(menuController.queueBox.draggedNode);
                    menuController.queueBox.getChildren().remove(menuController.queueBox.draggedNode);

                    menuController.queue.add(menuController.queueBox.draggedNode.newPosition, menuController.queueBox.draggedNode);
                    menuController.queueBox.getChildren().add(menuController.queueBox.draggedNode.newPosition, menuController.queueBox.draggedNode);

                    for(QueueItem queueItem : menuController.queue){
                        queueItem.setTranslateY(0);
                    }
                    controlBarController.enableNextVideoButton();
                }

                menuController.queueBox.draggedNode = null;
            }
            else if (!menuController.queueBox.dragAnimationsInProgress.isEmpty()){
                for(int i = 0; i < menuController.queueBox.dragAnimationsInProgress.size(); i ++){
                    menuController.queueBox.dragAnimationsInProgress.get(i).stop();
                }
                menuController.queueBox.dragAnimationsInProgress.clear();
            }

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

        controlBarController.durationLabel.setText(Utilities.getTime(Duration.seconds(controlBarController.durationSlider.getMax())) + "/" + Utilities.getTime(Duration.seconds(controlBarController.durationSlider.getMax())));

        controlBarController.mouseEventTracker.move();

        // add logic to update all the play icons
        controlBarController.end();
        if(mainController.miniplayerActive) mainController.miniplayer.miniplayerController.end();
        if(menuController.activeItem != null) menuController.activeItem.updateIconToPlay();

        playing.set(false);


    }

    public void play() {

        if(!mediaActive.get()) return;

        if(!playing.get()){
            playing.set(true);
            embeddedMediaPlayer.controls().play();
            SleepSuppressor.preventSleep();
        }

        if(mainController.miniplayerActive) mainController.miniplayer.miniplayerController.play();

        controlBarController.play();

        if(menuController.activeItem != null) menuController.activeItem.updateIconToPause();

        wasPlaying = true;

    }

    public void pause(){

        if(!mediaActive.get()) return;

        if(playing.get()) {
            playing.set(false);
            embeddedMediaPlayer.controls().pause();
            SleepSuppressor.allowSleep();
        }


        if(mainController.miniplayerActive) mainController.miniplayer.miniplayerController.pause();

        controlBarController.pause();

        if(menuController.activeItem != null)menuController.activeItem.updateIconToPlay();

    }

    public void replay(){
        controlBarController.durationSlider.setValue(0);
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

        frameGrabberTask.setOnSucceeded((succeededEvent) -> mainController.sliderHoverPreview.imageView.setImage(frameGrabberTask.getValue()));


        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(frameGrabberTask);
        executorService.shutdown();
    }
}
