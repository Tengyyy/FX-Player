package tengy;


import com.github.kokorin.jaffree.ffprobe.Stream;
import tengy.PlaybackSettings.AudioTrackTab;
import tengy.PlaybackSettings.VideoTrackTab;
import tengy.Subtitles.SubtitlesController;
import tengy.Subtitles.SubtitlesState;
import tengy.Chapters.ChapterController;
import tengy.Chapters.ChapterFrameGrabberTask;
import tengy.Chapters.ChapterItem;
import tengy.MediaItems.MediaItem;
import tengy.Menu.MenuController;
import tengy.Menu.Queue.QueueItem;
import tengy.PlaybackSettings.PlaybackSettingsController;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.util.Duration;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FrameGrabber;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.javafx.videosurface.ImageViewVideoSurface;
import uk.co.caprica.vlcj.player.base.*;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.bytedeco.ffmpeg.global.avformat.AV_DISPOSITION_DEFAULT;


public class MediaInterface {

    MainController mainController;
    ControlBarController controlBarController;
    PlaybackSettingsController playbackSettingsController;
    MenuController menuController;
    SubtitlesController subtitlesController;
    ChapterController chapterController;

    public MediaPlayerFactory mediaPlayerFactory;

    public EmbeddedMediaPlayer embeddedMediaPlayer;

    // Variables to keep track of mediaplayer status:
    public BooleanProperty mediaActive = new SimpleBooleanProperty(false); // is the mediaplayer active (is any video currently loaded in)
    public BooleanProperty playing = new SimpleBooleanProperty(false); // is mediaplayer currently playing
    public boolean wasPlaying = false; // was mediaplayer playing before a seeking action occurred
    public boolean atEnd = false; // is mediaplayer at the end of the video
    public boolean seekedToEnd = false; // true = video was seeked to the end; false = video naturally reached the end or the video is still playing
    public boolean videoDisabled = false;
    ////////////////////////////////////////////////



    double currentTime;

    public FFmpegFrameGrabber fFmpegFrameGrabber;

    FrameGrabberTask frameGrabberTask;

    public SubtitleExtractionTask subtitleExtractionTask;
    public ExecutorService executorService;

    ImageViewVideoSurface imageViewVideoSurface = null;

    MediaInterface(MainController mainController, ControlBarController controlBarController, PlaybackSettingsController playbackSettingsController, MenuController menuController, SubtitlesController subtitlesController) {
        this.mainController = mainController;
        this.controlBarController = controlBarController;
        this.playbackSettingsController = playbackSettingsController;
        this.menuController = menuController;
        this.subtitlesController = subtitlesController;

        mediaActive.addListener((observableValue, oldValue, newValue) -> {
            controlBarController.durationPane.setMouseTransparent(!newValue);
            if(mainController.miniplayerActive) mainController.miniplayer.miniplayerController.sliderPane.setMouseTransparent(!newValue);


            if(newValue){
                if(!controlBarController.playButtonEnabled) controlBarController.enablePlayButton();
                if(mainController.miniplayerActive) mainController.miniplayer.miniplayerController.enablePlayButton();
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

    public void init(ChapterController chapterController){

        this.chapterController = chapterController;

        String[] VLC_GLOBAL_OPTIONS = {
                "--no-sub-autodetect-file",
                "--no-spu",
                "--disable-screensaver"
        };

        this.mediaPlayerFactory = new MediaPlayerFactory(VLC_GLOBAL_OPTIONS);
        this.embeddedMediaPlayer = mediaPlayerFactory.mediaPlayers().newEmbeddedMediaPlayer();

        imageViewVideoSurface = new ImageViewVideoSurface(mainController.videoImageView);

        embeddedMediaPlayer.videoSurface().set(imageViewVideoSurface);
        embeddedMediaPlayer.audio().setEqualizer(new Equalizer(10));
        embeddedMediaPlayer.audio().equalizer().setPreamp(0);
        embeddedMediaPlayer.audio().setVolume((int) controlBarController.volumeSlider.getValue());



        embeddedMediaPlayer.events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {

            @Override
            public void finished(MediaPlayer mediaPlayer) {
                Platform.runLater(() -> {

                    controlBarController.durationSlider.setValue(controlBarController.durationSlider.getMax());

                    if(!mediaActive.get()
                        || playbackSettingsController.playbackOptionsController.loopOn
                        || ((playbackSettingsController.playbackOptionsController.autoplayOn || playbackSettingsController.playbackOptionsController.shuffleOn)
                        && ((menuController.queuePage.queueBox.activeItem.get() != null && menuController.queuePage.queueBox.queue.size() > menuController.queuePage.queueBox.activeIndex.get() + 1) || menuController.queuePage.queueBox.activeItem.get() == null && ! menuController.queuePage.queueBox.queue.isEmpty())))
                            return;


                    embeddedMediaPlayer.controls().stop();

                    if(mainController.miniplayerActive){
                        if(mainController.miniplayer.miniplayerController.videoImageView.getImage() != null){
                            mainController.miniplayer.miniplayerController.seekImageView.setImage(mainController.miniplayer.miniplayerController.videoImageView.getImage());
                            mainController.miniplayer.miniplayerController.videoImageView.setVisible(false);
                            mainController.miniplayer.miniplayerController.seekImageView.setVisible(true);
                        }
                    }
                    else {
                        if(mainController.videoImageView.getImage() != null){
                            mainController.seekImageView.setImage(mainController.videoImageView.getImage());
                            mainController.videoImageView.setVisible(false);
                            mainController.seekImageView.setVisible(true);
                        }
                    }

                    embeddedMediaPlayer.media().startPaused(menuController.queuePage.queueBox.activeItem.get().file.getAbsolutePath());

                    if(playbackSettingsController.videoTrackChooserController.selectedTab != null){
                        embeddedMediaPlayer.video().setTrack(playbackSettingsController.videoTrackChooserController.selectedTab.id);
                    }

                    if(playbackSettingsController.audioTrackChooserController.selectedTab != null){
                        embeddedMediaPlayer.audio().setTrack(playbackSettingsController.audioTrackChooserController.selectedTab.id);
                    }

                });
            }

            @Override
            public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
                currentTime = newTime;

                Platform.runLater(() -> {

                    mainController.videoImageView.setVisible(true);
                    mainController.seekImageView.setVisible(false);
                    mainController.seekImageView.setImage(null);

                    if(mainController.miniplayerActive){
                        mainController.miniplayer.miniplayerController.videoImageView.setVisible(true);
                        mainController.miniplayer.miniplayerController.seekImageView.setVisible(false);
                        mainController.miniplayer.miniplayerController.seekImageView.setImage(null);
                    }

                    if(!controlBarController.durationSlider.isValueChanging() && !mainController.seekingWithKeys && Math.abs(currentTime/1000 - controlBarController.durationSlider.getValue()) > 0.2 && Math.abs(currentTime/1000 - controlBarController.durationSlider.getValue()) < 2.0 && (!mainController.miniplayerActive || !mainController.miniplayer.miniplayerController.slider.isValueChanging())) controlBarController.durationSlider.setValue((double)newTime/1000);
                });
            }

            @Override
            public void mediaPlayerReady(MediaPlayer mediaPlayer) {


                Platform.runLater(() -> {

                    if(controlBarController.durationSlider.getValue() != 0) seek(Duration.seconds(controlBarController.durationSlider.getValue()));

                    if(mediaActive.get() && menuController.queuePage.queueBox.activeItem.get() != null && menuController.queuePage.queueBox.activeItem.get().getMediaItemGenerated().get()){
                        if(mediaPlayer.audio().trackCount() > 0 && playbackSettingsController.audioTrackChooserController.selectedTab.id != mediaPlayer.audio().track()){
                            playbackSettingsController.audioTrackChooserController.selectedTab.unselect();

                            for(int i=1; i < playbackSettingsController.audioTrackChooserController.audioTrackChooserBox.getChildren().size(); i++){
                                AudioTrackTab audioTrackTab = (AudioTrackTab) playbackSettingsController.audioTrackChooserController.audioTrackChooserBox.getChildren().get(i);

                                if(audioTrackTab.id == mediaPlayer.audio().track()){
                                    playbackSettingsController.audioTrackChooserController.selectedTab = audioTrackTab;
                                    audioTrackTab.checkIcon.setVisible(true);

                                    break;
                                }
                            }
                        }

                        if(mediaPlayer.video().trackCount() > 0 && playbackSettingsController.videoTrackChooserController.selectedTab.id != mediaPlayer.video().track()){
                            playbackSettingsController.videoTrackChooserController.selectedTab.unselect();

                            if(fFmpegFrameGrabber != null) {
                                try {
                                    fFmpegFrameGrabber.stop();
                                } catch (FFmpegFrameGrabber.Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            if(mediaPlayer.video().track() == -1){

                                videoDisabled = true;
                                mainController.sliderHoverBox.getChildren().remove(mainController.sliderHoverBox.imagePane);
                            }
                            else {
                                videoDisabled = false;
                                if(menuController.settingsPage.preferencesSection.seekPreviewOn.get() && !mainController.sliderHoverBox.getChildren().contains(mainController.sliderHoverBox.imagePane)) mainController.sliderHoverBox.getChildren().add(0, mainController.sliderHoverBox.imagePane);

                                initializeFrameGrabber(menuController.queuePage.queueBox.activeItem.get().getMediaItem(), mediaPlayer.video().track());
                            }

                            for(int i=1; i < playbackSettingsController.videoTrackChooserController.videoTrackChooserBox.getChildren().size(); i++){
                                VideoTrackTab videoTrackTab = (VideoTrackTab) playbackSettingsController.videoTrackChooserController.videoTrackChooserBox.getChildren().get(i);

                                if(videoTrackTab.id == mediaPlayer.video().track()){
                                    playbackSettingsController.videoTrackChooserController.selectedTab = videoTrackTab;
                                    videoTrackTab.checkIcon.setVisible(true);

                                    break;
                                }
                            }
                        }
                    }

                    Image image = null;
                    if(mainController.videoImageView.getImage() != null){
                        image = mainController.videoImageView.getImage();
                    }
                    else if(mainController.miniplayerActive && mainController.miniplayer.miniplayerController.videoImageView.getImage() != null){
                        image = mainController.miniplayer.miniplayerController.videoImageView.getImage();
                    }

                    Double ratio = null;
                    if(image != null) ratio = image.getWidth()/image.getHeight();
                    else if(mediaPlayer.video().videoDimension() != null) ratio = mediaPlayer.video().videoDimension().getWidth()/mediaPlayer.video().videoDimension().getHeight();

                    if(ratio != null) {
                        int newWidth = (int) Math.min(160, 90 * ratio);
                        int newHeight = (int) Math.min(90, 160 / ratio);

                        if (fFmpegFrameGrabber != null) {
                            fFmpegFrameGrabber.setImageWidth(newWidth);
                            fFmpegFrameGrabber.setImageHeight(newHeight);
                        }
                    }

                    mediaPlayer.audio().setVolume((int) controlBarController.volumeSlider.getValue());
                    controlBarController.durationSlider.setMax((double) mediaPlayer.media().info().duration()/1000);
                    if(mainController.miniplayerActive) mainController.miniplayer.miniplayerController.slider.setMax((double) mediaPlayer.media().info().duration()/1000);

                    if(controlBarController.showingTimeLeft) Utilities.setTimeLeftLabel(controlBarController.durationLabel, Duration.ZERO, Duration.seconds(controlBarController.durationSlider.getMax()));
                    else Utilities.setCurrentTimeLabel(controlBarController.durationLabel, Duration.ZERO, Duration.seconds(controlBarController.durationSlider.getMax()));

                    if(!mediaActive.get()) {
                        mediaActive.set(true);

                        chapterController.initializeChapters(mediaPlayer.chapters().descriptions(), menuController.queuePage.queueBox.activeItem.get().file);

                        int activeAudioTrack = embeddedMediaPlayer.audio().track();
                        List<TrackDescription> audioTrackDescriptions = embeddedMediaPlayer.audio().trackDescriptions();
                        if (!audioTrackDescriptions.isEmpty())
                            playbackSettingsController.initializeAudioTrackPage(audioTrackDescriptions, activeAudioTrack);

                        int activeVideoTrack = embeddedMediaPlayer.video().track();
                        List<TrackDescription> videoTrackDescriptions = embeddedMediaPlayer.video().trackDescriptions();
                        if (!videoTrackDescriptions.isEmpty())
                            playbackSettingsController.initializeVideoTrackPage(videoTrackDescriptions, activeVideoTrack);

                        if (menuController.queuePage.queueBox.activeItem.get() != null && menuController.queuePage.queueBox.activeItem.get().getMediaItem() != null && menuController.queuePage.queueBox.activeItem.get().getMediaItem().hasVideo() && !menuController.chapterController.chapterPage.chapterBox.getChildren().isEmpty()) {
                            chapterController.loadFrames();
                        }
                    }

                    play(true);
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

            if (controlBarController.durationSlider.isValueChanging() || (mainController.miniplayerActive && mainController.miniplayer.miniplayerController.slider.isValueChanging())) seekedToEnd = true;
            else if(seekedToEnd) defaultEnd();
            else if(playbackSettingsController.playbackOptionsController.loopOn){
                controlBarController.durationSlider.setValue(0);
                play(true);
                return;
            }

            atEnd = true;

            currentTime = controlBarController.durationSlider.getMax();

            playing.set(false);
            embeddedMediaPlayer.controls().setPause(true);
            SleepSuppressor.allowSleep();

            if(!controlBarController.durationSlider.isValueChanging() && (!mainController.miniplayerActive || !mainController.miniplayer.miniplayerController.slider.isValueChanging())){

                if(!seekedToEnd && !playbackSettingsController.playbackOptionsController.loopOn) endMedia();
            }
        }

        else {
            if(newValue == 0){
                currentTime = 0;
                if(!controlBarController.durationSlider.isValueChanging() && (!mainController.miniplayerActive || !mainController.miniplayer.miniplayerController.slider.isValueChanging())){
                    seek(Duration.ZERO);
                }
            }
            else if(Math.abs(currentTime/1000 - newValue) > 0.5 || (!playing.get() && Math.abs(currentTime/1000 - newValue) >= 0.1)) {
                currentTime = newValue;
                if(!controlBarController.durationSlider.isValueChanging() && (!mainController.miniplayerActive || !mainController.miniplayer.miniplayerController.slider.isValueChanging())) seek(Duration.seconds(newValue));
            }

            if (atEnd) {
                atEnd = false;
                seekedToEnd = false;

                if (wasPlaying && (!controlBarController.durationSlider.isValueChanging() && (!mainController.miniplayerActive || !mainController.miniplayer.miniplayerController.slider.isValueChanging()))) play(false);
                else pause();
            }
        }

    }

    public void endMedia() {

        if ((!playbackSettingsController.playbackOptionsController.shuffleOn && !playbackSettingsController.playbackOptionsController.loopOn && !playbackSettingsController.playbackOptionsController.autoplayOn) || (playbackSettingsController.playbackOptionsController.loopOn && seekedToEnd)) {
            defaultEnd();

        } else if (playbackSettingsController.playbackOptionsController.loopOn) {
            controlBarController.mouseEventTracker.move();

            // restart current video
        }
        else {

            if(menuController.queuePage.queueBox.queue.isEmpty() || menuController.queuePage.queueBox.activeIndex.get() >= menuController.queuePage.queueBox.queue.size() - 1) defaultEnd();
            else playNext();

        }
    }

    public void createMedia(QueueItem queueItem) {

        mainController.coverImageContainer.setVisible(false);
        mainController.miniplayerActiveText.setVisible(false);

        subtitlesController.timingPane.resetTiming();

        if(mainController.miniplayerActive){
            mainController.miniplayer.miniplayerController.videoImageView.setImage(null);
            mainController.miniplayer.miniplayerController.coverImageContainer.setVisible(false);
        }

        subtitlesController.openSubtitlesPane.fileSearchLabel.setText("Current media file:\n" + queueItem.file.getName());
        subtitlesController.openSubtitlesPane.fileSearchLabelContainer.setAlignment(Pos.CENTER_LEFT);
        if(!subtitlesController.openSubtitlesPane.fileSearchLabelContainer.getChildren().contains(subtitlesController.openSubtitlesPane.fileSearchExplanationLabel)) subtitlesController.openSubtitlesPane.fileSearchLabelContainer.getChildren().add(subtitlesController.openSubtitlesPane.fileSearchExplanationLabel);

        if(!subtitlesController.openSubtitlesPane.searchInProgress.get()) subtitlesController.openSubtitlesPane.searchButton.setDisable(false);

        MediaItem mediaItem = queueItem.getMediaItem();

        if (mediaItem != null) loadMediaItem(queueItem);

        controlBarController.durationSlider.setValue(0);

        embeddedMediaPlayer.media().start(queueItem.file.getAbsolutePath());
    }


    public void resetMediaPlayer(){

        mainController.videoImageView.setImage(null);
        mainController.videoImageView.setVisible(true);
        if(mainController.miniplayerActive){
            mainController.miniplayer.miniplayerController.videoImageView.setImage(null);
            mainController.miniplayer.miniplayerController.coverImageContainer.setVisible(false);

            mainController.miniplayer.miniplayerController.seekImageView.setImage(null);
            mainController.miniplayer.miniplayerController.seekImageView.setVisible(false);
        }

        mainController.seekImageView.setImage(null);
        mainController.seekImageView.setVisible(false);

        mainController.coverImageContainer.setVisible(false);
        mainController.miniplayerActiveText.setVisible(false);

        mainController.sliderHoverBox.getChildren().remove(mainController.sliderHoverBox.imagePane);

        mediaActive.set(false);
        chapterController.resetChapters();

        controlBarController.durationSlider.setValue(0);

        embeddedMediaPlayer.controls().stop();
        SleepSuppressor.allowSleep();

        if(controlBarController.showingTimeLeft) controlBarController.durationLabel.setText("−00:00/00:00");
        else controlBarController.durationLabel.setText("00:00/00:00");

        mainController.videoTitleLabel.setText(null);
        mainController.mediaInformationButtonPane.setVisible(false);
        mainController.mediaInformationButtonPane.setMouseTransparent(true);

        if(playbackSettingsController.playbackOptionsController.loopOn) playbackSettingsController.playbackOptionsController.loopTab.toggle.fire();

        mainController.sliderHoverBox.setImage(null);

        subtitlesController.clearSubtitles();
        subtitlesController.openSubtitlesPane.fileSearchLabel.setText("Select a media file to use this feature");
        subtitlesController.openSubtitlesPane.fileSearchLabelContainer.setAlignment(Pos.CENTER);
        subtitlesController.openSubtitlesPane.fileSearchLabelContainer.getChildren().remove(subtitlesController.openSubtitlesPane.fileSearchExplanationLabel);

        if(subtitlesController.openSubtitlesPane.searchState == 2) subtitlesController.openSubtitlesPane.searchButton.setDisable(true);


        controlBarController.disablePreviousVideoButton();
        controlBarController.disableNextVideoButton();

        atEnd = false;
        seekedToEnd = false;
        playing.set(false);
        wasPlaying = false;
        currentTime = 0;

        subtitlesController.timingPane.resetTiming();

        playbackSettingsController.resetTrackPages();

        videoDisabled = false;

        try {
            if(fFmpegFrameGrabber != null) fFmpegFrameGrabber.close();
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        }
    }

    public void playNext(){

        if(menuController.queuePage.queueBox.activeItem.get() != null && menuController.queuePage.queueBox.queue.size() > menuController.queuePage.queueBox.activeIndex.get() + 1){
            controlBarController.mouseEventTracker.move();
            menuController.queuePage.queueBox.queue.get(menuController.queuePage.queueBox.queueOrder.get(menuController.queuePage.queueBox.activeIndex.get() + 1)).play();
        }
        else if(menuController.queuePage.queueBox.activeItem.get() == null && !menuController.queuePage.queueBox.queue.isEmpty()){        controlBarController.mouseEventTracker.move();
            controlBarController.mouseEventTracker.move();
            menuController.queuePage.queueBox.queue.get(menuController.queuePage.queueBox.queueOrder.get(0)).play();
        }
    }

    public void playPrevious(){

        if(menuController.queuePage.queueBox.activeItem.get() != null && menuController.queuePage.queueBox.activeIndex.get() > 0){
            controlBarController.mouseEventTracker.move();
            menuController.queuePage.queueBox.queue.get(menuController.queuePage.queueBox.queueOrder.get(menuController.queuePage.queueBox.activeIndex.get() -1)).play();
        }
    }

    public void defaultEnd(){

        controlBarController.durationSlider.setValue(controlBarController.durationSlider.getMax());

        if(controlBarController.showingTimeLeft) Utilities.setTimeLeftLabel(controlBarController.durationLabel, Duration.seconds(controlBarController.durationSlider.getMax()), Duration.seconds(controlBarController.durationSlider.getMax()));
        else Utilities.setCurrentTimeLabel(controlBarController.durationLabel, Duration.seconds(controlBarController.durationSlider.getMax()), Duration.seconds(controlBarController.durationSlider.getMax()));

        controlBarController.mouseEventTracker.move();

        controlBarController.end();
        if(mainController.miniplayerActive) mainController.miniplayer.miniplayerController.end();
        if(menuController.queuePage.queueBox.activeItem.get() != null) menuController.queuePage.queueBox.activeItem.get().columns.pause();

        playing.set(false);


    }

    public void play(boolean force) {

        if(!mediaActive.get()) return;

        if(!playing.get() || force){
            playing.set(true);
            embeddedMediaPlayer.controls().play();
            SleepSuppressor.preventSleep();
        }

        if(mainController.miniplayerActive) mainController.miniplayer.miniplayerController.play();

        controlBarController.play();

        if(menuController.queuePage.queueBox.activeItem.get() != null) menuController.queuePage.queueBox.activeItem.get().columns.play();

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

        if(menuController.queuePage.queueBox.activeItem.get() != null) menuController.queuePage.queueBox.activeItem.get().columns.pause();

    }

    public void replay(){
        controlBarController.durationSlider.setValue(0);
    }

    public void setVideoTrack(int id){

        if(mainController.miniplayerActive) {
            mainController.miniplayer.miniplayerController.videoImageView.setImage(null);
            embeddedMediaPlayer.videoSurface().set(new ImageViewVideoSurface(mainController.miniplayer.miniplayerController.videoImageView));
        }
        else {
            mainController.videoImageView.setImage(null);
            embeddedMediaPlayer.videoSurface().set(new ImageViewVideoSurface(mainController.videoImageView));
        }

        try {
            if (fFmpegFrameGrabber != null) fFmpegFrameGrabber.close();
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        }

        if(id == -1){
            videoDisabled = true;
            mainController.sliderHoverBox.getChildren().remove(mainController.sliderHoverBox.imagePane);
        }
        else {
            videoDisabled = false;
            if(menuController.settingsPage.preferencesSection.seekPreviewOn.get() && !mainController.sliderHoverBox.getChildren().contains(mainController.sliderHoverBox.imagePane)) mainController.sliderHoverBox.getChildren().add(0, mainController.sliderHoverBox.imagePane);
            initializeFrameGrabber(menuController.queuePage.queueBox.activeItem.get().getMediaItem(), id);
        }

        boolean playValue = playing.get();

        embeddedMediaPlayer.controls().stop();
        embeddedMediaPlayer.media().startPaused(menuController.queuePage.queueBox.activeItem.get().getMediaItem().getFile().getAbsolutePath());
        embeddedMediaPlayer.video().setTrack(id);

        controlBarController.durationSlider.setValue(0);

        if (playValue) embeddedMediaPlayer.controls().play();

    }

    public void setAudioTrack(int id){
        embeddedMediaPlayer.audio().setTrack(id);
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


    public void updatePreviewFrame(double time, boolean forceUpdate) {

        if(!forceUpdate && (frameGrabberTask != null && frameGrabberTask.isRunning())) return;
        if(fFmpegFrameGrabber == null) return;

        frameGrabberTask = new FrameGrabberTask(fFmpegFrameGrabber, time);

        frameGrabberTask.setOnSucceeded((succeededEvent) -> {
            Image image = frameGrabberTask.getValue();
            if(image == null) return;
            if(mainController.miniplayerActive && mainController.miniplayer.miniplayerController.slider.isValueChanging()){
                mainController.miniplayer.miniplayerController.seekImageView.setImage(image);
            }
            else {
                if(menuController.settingsPage.preferencesSection.seekPreviewOn.get()) mainController.sliderHoverBox.setImage(image);
                if(controlBarController.durationSlider.isValueChanging()){
                    if(mainController.miniplayerActive) mainController.miniplayer.miniplayerController.seekImageView.setImage(image);
                    else mainController.seekImageView.setImage(image);
                }
            }
        });

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(frameGrabberTask);
        executorService.shutdown();
    }

    public void loadMediaItem(QueueItem queueItem){

        MediaItem mediaItem = queueItem.getMediaItem();
        if(mediaItem == null) return;

        if(mediaItem.hasVideo()){
            if(!videoDisabled) {
                if (!mainController.sliderHoverBox.getChildren().contains(mainController.sliderHoverBox.imagePane)
                        && menuController.settingsPage.preferencesSection.seekPreviewOn.get())
                    mainController.sliderHoverBox.getChildren().add(0, mainController.sliderHoverBox.imagePane);

                initializeFrameGrabber(mediaItem, -1);

                if (mainController.miniplayerActive) mainController.miniplayerActiveText.setVisible(true);
            }
        }
        else mainController.setCoverImageView(queueItem);

        mainController.videoTitleLabel.setText(queueItem.getTitle());

        mainController.mediaInformationButton.setOnAction(e -> queueItem.showMetadata());
        mainController.mediaInformationButtonPane.setVisible(true);
        mainController.mediaInformationButtonPane.setMouseTransparent(false);

        if(menuController.settingsPage.subtitleSection.extractionOn.get()) {
            if (!mediaItem.subtitlesGenerationTime.isEmpty()) { // subtitle extraction has started for this mediaitem
                if (!mediaItem.subtitlesExtractionInProgress.get()) { // subtitle extraction has already been completed, can simply add caption tabs
                    subtitlesController.createSubtitleTabs(mediaItem);
                    if(mediaItem.subtitleStreams.size() == 0 && menuController.settingsPage.subtitleSection.searchOn.get()) subtitlesController.scanParentFolderForMatchingSubtitles(mediaItem);

                } else { // subtitle extraction is ongoing, have to wait for it to finish before adding caption tabs
                    mediaItem.subtitlesExtractionInProgress.addListener((observableValue, oldValue, newValue) -> {
                        if (!newValue && menuController.queuePage.queueBox.activeItem.get() == queueItem) {
                            subtitlesController.createSubtitleTabs(mediaItem);

                            if(mediaItem.subtitleStreams.size() == 0 && menuController.settingsPage.subtitleSection.searchOn.get()) subtitlesController.scanParentFolderForMatchingSubtitles(mediaItem);
                        }
                    });
                }
            }
            else { // subtitle extraction has not started, will create subtitle extraction task and on completion add subtitles
                executorService = Executors.newFixedThreadPool(1);
                subtitleExtractionTask = new SubtitleExtractionTask(subtitlesController, mediaItem);
                subtitleExtractionTask.setOnSucceeded(e -> {
                    if (subtitleExtractionTask.getValue() != null && subtitleExtractionTask.getValue() && menuController.queuePage.queueBox.activeItem.get() == queueItem){
                        subtitlesController.createSubtitleTabs(mediaItem);

                        if(mediaItem.subtitleStreams.size() == 0 && menuController.settingsPage.subtitleSection.searchOn.get()) subtitlesController.scanParentFolderForMatchingSubtitles(mediaItem);
                    }
                });
                executorService.execute(subtitleExtractionTask);
                executorService.shutdown();
            }
        }
        else if(menuController.settingsPage.subtitleSection.searchOn.get()){
            subtitlesController.scanParentFolderForMatchingSubtitles(mediaItem);
        }

        if(mediaItem.hasVideo() && !menuController.chapterController.chapterPage.chapterBox.getChildren().isEmpty()){
            chapterController.loadFrames();
        }

        if(subtitlesController.subtitlesState != SubtitlesState.OPENSUBTITLES_OPEN && subtitlesController.subtitlesState != SubtitlesState.OPENSUBTITLES_RESULTS_OPEN){
            Map<String, String> metadata = mediaItem.getMediaInformation();
            if(metadata.containsKey("title") && !metadata.get("title").isBlank()) subtitlesController.openSubtitlesPane.titleField.setText(metadata.get("title"));
            if(metadata.containsKey("season") && !metadata.get("season").isBlank()) subtitlesController.openSubtitlesPane.seasonField.setText(metadata.get("season"));
            if(metadata.containsKey("episode") && !metadata.get("episode").isBlank()) subtitlesController.openSubtitlesPane.episodeField.setText(metadata.get("episode"));
        }

    }

    public void initializeFrameGrabber(MediaItem mediaItem, int stream){
        fFmpegFrameGrabber = new FFmpegFrameGrabber(mediaItem.getFile());
        fFmpegFrameGrabber.setVideoDisposition(AV_DISPOSITION_DEFAULT);
        fFmpegFrameGrabber.setVideoOption("vcodec", "copy");
        if(stream >= 0) fFmpegFrameGrabber.setVideoStream(stream);

        double width;
        double height;
        if(mainController.miniplayerActive && mainController.miniplayer.miniplayerController.videoImageView.getImage() != null){
            width = mainController.miniplayer.miniplayerController.videoImageView.getImage().getWidth();
            height = mainController.miniplayer.miniplayerController.videoImageView.getImage().getHeight();
        }
        else if(mainController.videoImageView.getImage() != null){
            width = mainController.videoImageView.getImage().getWidth();
            height = mainController.videoImageView.getImage().getHeight();
        }
        else if(stream == -1){
            width = mediaItem.defaultVideoStream.getWidth();
            height = mediaItem.defaultVideoStream.getHeight();
        }
        else {
            Stream videoStream = mediaItem.videoStreams.get(stream);
            width = videoStream.getWidth();
            height = videoStream.getHeight();
        }
        double ratio = width / height;

        int newWidth = (int) Math.min(160, 90 * ratio);
        int newHeight = (int) Math.min(90, 160 / ratio);

        fFmpegFrameGrabber.setImageWidth(newWidth);
        fFmpegFrameGrabber.setImageHeight(newHeight);

        try {
            fFmpegFrameGrabber.start();
        } catch (FFmpegFrameGrabber.Exception e) {
            e.printStackTrace();
        }
    }
}
