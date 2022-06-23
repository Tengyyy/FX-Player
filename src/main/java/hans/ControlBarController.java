package hans;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.Animation;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

public class ControlBarController implements Initializable {

    @FXML
    VBox controlBars;

    @FXML
    StackPane controlBarWrapper;

    @FXML
    Button fullScreenButton, playButton, volumeButton, settingsButton, nextVideoButton, captionsButton, previousVideoButton;

    @FXML
    public Slider volumeSlider, durationSlider;

    @FXML
    public ProgressBar volumeTrack, durationTrack;

    @FXML
    StackPane volumeSliderPane, previousVideoPane, playButtonPane, nextVideoPane, volumeButtonPane, captionsButtonPane, settingsButtonPane, fullScreenButtonPane, durationPane;

    @FXML
    Label durationLabel;

    @FXML
    Line captionsButtonLine;

    @FXML
    public Region previousVideoIcon, playIcon, nextVideoIcon, volumeIcon, captionsIcon, settingsIcon, fullScreenIcon;

    @FXML
    public HBox settingsBox1;


    SVGPath previousVideoSVG, playSVG, pauseSVG, replaySVG, nextVideoSVG, highVolumeSVG, lowVolumeSVG, volumeMutedSVG, captionsSVG, settingsSVG, maximizeSVG, minimizeSVG;

    MainController mainController;
    SettingsController settingsController;
    MenuController menuController;
    CaptionsController captionsController;


    public double volumeValue;

    public boolean muted = false;
    boolean isExited = true;
    boolean showingTimeLeft = false;
    boolean durationSliderHover = false;
    boolean controlBarOpen = false;


    // variables to keep track of whether mouse is hovering any control button
    boolean playButtonHover = false;
    boolean nextVideoButtonHover = false;
    boolean volumeButtonHover = false;
    boolean captionsButtonHover = false;
    boolean settingsButtonHover = false;
    boolean fullScreenButtonHover = false;


    MouseEventTracker mouseEventTracker;

    ControlTooltip play, mute, settings, fullScreen, exitFullScreen, captions, nextVideoTooltip, previousVideoTooltip;

    MediaInterface mediaInterface;

    ScaleTransition fullScreenButtonScaleTransition;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Platform.runLater(() -> {
            play = new ControlTooltip("Play (k)", playButton, controlBarWrapper, 0, false);
            mute = new ControlTooltip("Mute (m)", volumeButton, controlBarWrapper, 0, false);
            settings = new ControlTooltip("Settings (s)", settingsButton, controlBarWrapper, 0, false);
            exitFullScreen = new ControlTooltip("Exit full screen (f)", fullScreenButton, controlBarWrapper, 0, false);
            fullScreen = new ControlTooltip("Full screen (f)", fullScreenButton, controlBarWrapper, 0, false);
            nextVideoTooltip = new ControlTooltip("Next video (SHIFT + N)", nextVideoButton, controlBarWrapper, 0, false);
            previousVideoTooltip = new ControlTooltip("Previous video (SHIFT + P)", previousVideoButton, controlBarWrapper, 0, false);
            captions = new ControlTooltip("Subtitles/CC not selected", captionsButton, controlBarWrapper, 0, false);
        });

        previousVideoSVG = new SVGPath();
        previousVideoSVG.setContent(App.svgMap.get(SVG.PREVIOUS_VIDEO));

        playSVG = new SVGPath();
        playSVG.setContent(App.svgMap.get(SVG.PLAY));

        pauseSVG = new SVGPath();
        pauseSVG.setContent(App.svgMap.get(SVG.PAUSE));

        replaySVG = new SVGPath();
        replaySVG.setContent(App.svgMap.get(SVG.REPLAY));

        nextVideoSVG = new SVGPath();
        nextVideoSVG.setContent(App.svgMap.get(SVG.NEXT_VIDEO));

        highVolumeSVG = new SVGPath();
        highVolumeSVG.setContent(App.svgMap.get(SVG.VOLUME_HIGH));

        lowVolumeSVG = new SVGPath();
        lowVolumeSVG.setContent(App.svgMap.get(SVG.VOLUME_LOW));

        volumeMutedSVG = new SVGPath();
        volumeMutedSVG.setContent(App.svgMap.get(SVG.VOLUME_MUTED));

        captionsSVG = new SVGPath();
        captionsSVG.setContent(App.svgMap.get(SVG.CAPTIONS));

        settingsSVG = new SVGPath();
        settingsSVG.setContent(App.svgMap.get(SVG.SETTINGS));

        maximizeSVG = new SVGPath();
        maximizeSVG.setContent(App.svgMap.get(SVG.MAXIMIZE));

        minimizeSVG = new SVGPath();
        minimizeSVG.setContent(App.svgMap.get(SVG.MINIMIZE));

        volumeSliderPane.setClip(new Rectangle(68, 30));

        volumeSlider.setTranslateX(-60);
        volumeTrack.setTranslateX(-60);

        durationLabel.setTranslateX(-60);

        controlBarWrapper.setTranslateY(70);

        durationPane.setMouseTransparent(true);

        durationLabel.setOnMouseClicked((e) -> toggleDurationLabel());

        previousVideoIcon.setShape(previousVideoSVG);
        playIcon.setShape(playSVG);
        nextVideoIcon.setShape(nextVideoSVG);
        volumeIcon.setShape(lowVolumeSVG);

        volumeIcon.setPrefSize(15, 18);
        volumeIcon.setTranslateX(-3);

        captionsIcon.setShape(captionsSVG);
        settingsIcon.setShape(settingsSVG);
        fullScreenIcon.setShape(maximizeSVG);

        captionsIcon.getStyleClass().add("controlIconDisabled");


        playButton.setBackground(Background.EMPTY);

        nextVideoButton.setBackground(Background.EMPTY);


        playButton.setOnAction((e) -> playButtonClick1());

        fullScreenButton.setBackground(Background.EMPTY);

        settingsButton.setBackground(Background.EMPTY);

        volumeButton.setBackground(Background.EMPTY);

        captionsButton.setBackground(Background.EMPTY);


        volumeSlider.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> volumeSlider.setValueChanging(true));
        volumeSlider.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> volumeSlider.setValueChanging(false));


        previousVideoPane.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> controlButtonHoverOn(previousVideoPane));
        previousVideoPane.addEventHandler(MouseEvent.MOUSE_EXITED, e -> controlButtonHoverOff(previousVideoPane));

        playButtonPane.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> controlButtonHoverOn(playButtonPane));
        playButtonPane.addEventHandler(MouseEvent.MOUSE_EXITED, e -> controlButtonHoverOff(playButtonPane));

        nextVideoPane.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> controlButtonHoverOn(nextVideoPane));
        nextVideoPane.addEventHandler(MouseEvent.MOUSE_EXITED, e -> controlButtonHoverOff(nextVideoPane));

        volumeButtonPane.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> controlButtonHoverOn(volumeButtonPane));
        volumeButtonPane.addEventHandler(MouseEvent.MOUSE_EXITED, e -> controlButtonHoverOff(volumeButtonPane));

        captionsButtonPane.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> controlButtonHoverOn(captionsButtonPane));
        captionsButtonPane.addEventHandler(MouseEvent.MOUSE_EXITED, e -> controlButtonHoverOff(captionsButtonPane));

        settingsButtonPane.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> controlButtonHoverOn(settingsButtonPane));
        settingsButtonPane.addEventHandler(MouseEvent.MOUSE_EXITED, e -> controlButtonHoverOff(settingsButtonPane));

        fullScreenButtonPane.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> controlButtonHoverOn(fullScreenButtonPane));
        fullScreenButtonPane.addEventHandler(MouseEvent.MOUSE_EXITED, e -> controlButtonHoverOff(fullScreenButtonPane));



        volumeSlider.valueChangingProperty().addListener((observable, oldValue, newValue) -> {

            if (!newValue) {
                if(settingsController.settingsOpen) settingsController.closeSettings();

                if(isExited) volumeSliderExit();
            }
        });


        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {

            if(menuController.mediaActive.get()) mediaInterface.mediaPlayer.setVolume(volumeSlider.getValue() / 100);

            volumeTrack.setProgress(volumeSlider.getValue() / 100);

            if (volumeSlider.getValue() == 0) {
                volumeIcon.setShape(volumeMutedSVG);
                volumeIcon.setPrefSize(20, 20);
                volumeIcon.setTranslateX(0);
                muted = true;
                mute.updateText("Unmute (m)");
            }
            else if (volumeSlider.getValue() < 50) {
                volumeIcon.setShape(lowVolumeSVG);
                volumeIcon.setPrefSize(15, 18);
                volumeIcon.setTranslateX(-3);
                muted = false;
                mute.updateText("Mute (m)");
            }
            else {
                volumeIcon.setShape(highVolumeSVG);
                volumeIcon.setPrefSize(20, 20);
                volumeIcon.setTranslateX(0);
                muted = false;
                mute.updateText("Mute (m)");
            }
        });


        durationSlider.addEventFilter(MouseEvent.DRAG_DETECTED, e -> {
            durationSlider.setValueChanging(true);

        });
        durationSlider.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> durationSlider.setValueChanging(false));

        durationSlider.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            if (settingsController.settingsOpen) {
                settingsController.closeSettings();
            }
        });


        // this part has to be run later because the slider thumb loads in later than the slider itself
        Platform.runLater(() -> {

            durationSlider.lookup(".thumb").setScaleX(0);
            durationSlider.lookup(".thumb").setScaleY(0);

            durationSlider.lookup(".track").setCursor(Cursor.HAND);
            durationSlider.lookup(".thumb").setCursor(Cursor.HAND);


            durationSlider.setOnMouseEntered((e) -> {
                durationSliderHover = true;
                durationSliderHoverOn();
            });

            durationSlider.setOnMouseExited((e) -> {
                durationSliderHover = false;
                if (!e.isPrimaryButtonDown() && !e.isSecondaryButtonDown() && !e.isMiddleButtonDown()) {
                    durationSliderHoverOff();
                }
            });
        });

        durationSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(menuController.mediaActive.get()){

                // update subtitles here

                if(oldValue.doubleValue() <= 5 && newValue.doubleValue() > 5){
                    previousVideoTooltip.updateText("Replay");

                    previousVideoButton.setOnAction((e) -> replayMedia());
                }
                else if(oldValue.doubleValue() > 5 && newValue.doubleValue() <= 5){
                    previousVideoTooltip.updateText("Previous video (SHIFT + P)");

                    previousVideoButton.setOnAction((e) -> playPreviousMedia());
                }


                mediaInterface.updateMedia(newValue.doubleValue());
            }
            durationTrack.setProgress(durationSlider.getValue() / durationSlider.getMax());
        });

        durationSlider.valueChangingProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue) { // pause video when user starts seeking
                playIcon.setShape(playSVG);
                playIcon.setPrefSize(20, 20);
                if(menuController.mediaActive.get()) {
                    mediaInterface.mediaPlayer.pause();
                    mediaInterface.playing.set(false);
                }
                play.updateText("Play (k)");

            } else {

                if (!durationSliderHover) {
                    durationSliderHoverOff();
                }

                if(menuController.mediaActive.get()) mediaInterface.mediaPlayer.seek(Duration.seconds(durationSlider.getValue())); // seeks to exact position when user finishes dragging

                if (settingsController.settingsOpen) { // close settings pane after user finishes seeking media (if its open)
                    settingsController.closeSettings();
                }

                if (mediaInterface.atEnd) { // if user drags the duration slider to the end turn play button to replay button
                    playIcon.setShape(replaySVG);
                    playIcon.setPrefSize(24, 24);
                    playButton.setOnAction((e) -> playButtonClick2());

                    play.updateText("Replay (k)");

                    menuController.activeItem.playIcon.setShape(menuController.activeItem.playSVG);
                    menuController.activeItem.play.updateText("Play video");

                } else if (mediaInterface.wasPlaying) { // starts playing the video in the new position when user finishes seeking with the slider
                    if(menuController.mediaActive.get()) {
                        mediaInterface.mediaPlayer.play();
                        mediaInterface.playing.set(true);
                    }

                    playIcon.setShape(pauseSVG);
                    playIcon.setPrefSize(20, 20);

                    play.updateText("Pause (k)");
                }
            }
        });


    }

    public void init(MainController mainController, SettingsController settingsController, MenuController menuController, MediaInterface mediaInterface, CaptionsController captionsController) {
        this.mainController = mainController;
        this.settingsController = settingsController;
        this.menuController = menuController;
        this.mediaInterface = mediaInterface;
        this.captionsController = captionsController;

        mouseEventTracker = new MouseEventTracker(4, mainController, this, settingsController); // creates instance of the MouseEventTracker class which keeps track of when to hide and show the control-bar

    }

    public void toggleDurationLabel() {
        if (showingTimeLeft && menuController.mediaActive.get()) {
            Utilities.setCurrentTimeLabel(durationLabel, mediaInterface.mediaPlayer, menuController.activeItem.getMediaItem().getMedia());
            showingTimeLeft = false;
        } else if(!showingTimeLeft && menuController.mediaActive.get()){
            Utilities.setTimeLeftLabel(durationLabel, mediaInterface.mediaPlayer, menuController.activeItem.getMediaItem().getMedia());
            showingTimeLeft = true;
        }
    }


    public void playButtonClick1() {
        if (settingsController.settingsOpen) {
            settingsController.closeSettings();
        } else {
            if (mediaInterface.playing.get()) {
                pause();
            } else {
                play();
            }
        }
    }


    public void playButtonClick2() {
        if (settingsController.settingsOpen) {
            settingsController.closeSettings();
        } else {
            replayMedia();
            mediaInterface.seekedToEnd = false;
        }
    }


    public void play() {

        if(menuController.mediaActive.get()) {
            mediaInterface.mediaPlayer.play();

            menuController.activeItem.playIcon.setShape(menuController.activeItem.pauseSVG);
            menuController.activeItem.play.updateText("Pause video");

            if (menuController.historyBox.index != -1) {
                HistoryItem historyItem = menuController.history.get(menuController.historyBox.index);
                historyItem.playIcon.setShape(historyItem.pauseSVG);
                historyItem.play.updateText("Pause video");
            }

            playIcon.setShape(pauseSVG);
            playIcon.setPrefSize(20, 20);

            mediaInterface.playing.set(true);

            play.updateText("Pause (k)");

            mediaInterface.wasPlaying = mediaInterface.playing.get(); // updates the value of wasPlaying variable - when this method is called the
            // user really wants to play or pause the video and therefore the previous
            // wasPlaying state no longer needs to be tracked
        }
    }

    public void pause() {

        if(menuController.mediaActive.get()) {
            mediaInterface.mediaPlayer.pause();

            menuController.activeItem.playIcon.setShape(menuController.activeItem.playSVG);
            menuController.activeItem.play.updateText("Play video");

            if (menuController.historyBox.index != -1) {
                HistoryItem historyItem = menuController.history.get(menuController.historyBox.index);
                historyItem.playIcon.setShape(historyItem.playSVG);
                historyItem.play.updateText("Play video");
            }

            playIcon.setShape(playSVG);
            playIcon.setPrefSize(20, 20);

            mediaInterface.playing.set(false);

            play.updateText("Play (k)");

            mediaInterface.wasPlaying = mediaInterface.playing.get();
        }
    }


    public void replayMedia() {


        if(menuController.mediaActive.get()) {
            mediaInterface.mediaPlayer.seek(Duration.ZERO);
            mediaInterface.mediaPlayer.play();

            menuController.activeItem.playIcon.setShape(menuController.activeItem.pauseSVG);
            menuController.activeItem.play.updateText("Pause video");

            mediaInterface.playing.set(true);
            mediaInterface.atEnd = false;
            playIcon.setShape(pauseSVG);
            playIcon.setPrefSize(20, 20);

            mediaInterface.seekedToEnd = false;
            playButton.setOnAction((e) -> playButtonClick1());

            play.updateText("Pause (k)");
        }

    }

    public void enterArea() {
        if (isExited && !volumeSlider.isValueChanging()) {
            volumeSliderEnter();
        }
        isExited = false;
    }

    public void exitArea() {
        if (!volumeSlider.isValueChanging() && !isExited) {
            volumeSliderExit();
        }
        isExited = true;
    }

    public void volumeSliderEnter() {
        AnimationsClass.volumeSliderHoverOn(volumeSlider, durationLabel, volumeTrack);
    }

    public void volumeSliderExit() {
        AnimationsClass.volumeSliderHoverOff(volumeSlider, durationLabel, volumeTrack);
    }

    public void fullScreen() {
        // got to move some of this logic to the main class
        App.stage.setFullScreen(!App.stage.isFullScreen());

        if (App.stage.isFullScreen()) {
            fullScreenIcon.setShape(minimizeSVG);
            App.fullScreen = true;

            if (!settingsController.settingsOpen) {
                if (fullScreen.isShowing()) {
                    fullScreen.hide();
                    exitFullScreen = new ControlTooltip("Exit full screen (f)", fullScreenButton, controlBarWrapper, 0, false);
                    exitFullScreen.showTooltip();
                } else {
                    exitFullScreen = new ControlTooltip("Exit full screen (f)", fullScreenButton, controlBarWrapper, 0, false);
                }
            }
        } else {
            fullScreenIcon.setShape(maximizeSVG);
            App.fullScreen = false;

            if (!settingsController.settingsOpen) {
                if (exitFullScreen.isShowing()) {
                    exitFullScreen.hide();
                    fullScreen = new ControlTooltip("Full screen (f)", fullScreenButton, controlBarWrapper, 0, false);
                    fullScreen.showTooltip();
                } else {
                    fullScreen = new ControlTooltip("Full screen (f)", fullScreenButton, controlBarWrapper, 0, false);
                }
            }
        }
    }

    public void fullScreenButtonHoverOn() {
        fullScreenButtonHover = true;
        fullScreenButtonScaleTransition = AnimationsClass.scaleAnimation(200, fullScreenIcon,1, 1.3, 1, 1.3, true, 2, true);

    }

    public void fullScreenButtonHoverOff() {
        fullScreenButtonHover = false;
        if(fullScreenButtonScaleTransition != null && fullScreenButtonScaleTransition.getStatus() == Animation.Status.RUNNING) fullScreenButtonScaleTransition.stop();
        fullScreenIcon.setScaleX(1);
        fullScreenIcon.setScaleY(1);
    }


    public void volumeButtonClick() {
        if (settingsController.settingsOpen) {
            settingsController.closeSettings();
        } else {
            if (!muted)
                mute();
            else
                unmute();
        }
    }

    public void mute() {
        muted = true;
        volumeValue = volumeSlider.getValue(); //stores the value of the volumeslider before setting it to 0
        volumeSlider.setValue(0);
    }

    public void unmute() {
        muted = false;
        volumeSlider.setValue(volumeValue); // sets volume back to the value it was at before muting
    }

    public void playPreviousMedia(){
        if (settingsController.settingsOpen) {
            settingsController.closeSettings();
        } else {
            if(!menuController.animationsInProgress.isEmpty()) return;
            mediaInterface.playPrevious(); // reset styling of current active history item, decrement historyposition etc
        }
    }

    public void playNextMedia() {
        if (settingsController.settingsOpen) {
            settingsController.closeSettings();
        } else {
            if(!menuController.animationsInProgress.isEmpty()) return;
            mediaInterface.playNext();
        }
    }


    public void openCaptions() {

        if(!captionsController.captionsSelected) return;

        captionsController.captionsOn = true;

        AnimationsClass.scaleAnimation(100, captionsButtonLine, 0, 1, 1, 1, false, 1, true);
    }

    public void closeCaptions() {
        captionsController.captionsOn = false;

        AnimationsClass.scaleAnimation(100, captionsButtonLine, 1, 0, 1, 1, false, 1, true);
    }

    public void settingsButtonClick() {
        if (settingsController.settingsOpen)
            settingsController.closeSettings();
        else
            settingsController.openSettings();
    }


    public void fullScreenButtonClick() {
        if (settingsController.settingsOpen)
            settingsController.closeSettings();
        else
            fullScreen();
    }


    public void controlBarClick() {
        if (settingsController.settingsOpen) {
            settingsController.closeSettings();
        }
    }

    public void captionsButtonClick() {

        if(settingsController.settingsOpen) {
            settingsController.closeSettings();
            return;
        }

        if(!captionsController.captionsSelected) return;

        if (!captionsController.captionsOn)
            openCaptions();
        else
            closeCaptions();

        captionsController.captionsPane.captionsToggle.fire();
    }

    public void durationSliderHoverOn() {
        ScaleTransition sliderThumbHoverOn = AnimationsClass.scaleAnimation(100, durationSlider.lookup(".thumb"), durationSlider.lookup(".thumb").getScaleX(), 1, durationSlider.lookup(".thumb").getScaleY(), 1, false, 1, false);
        ScaleTransition sliderTrackHoverOn = AnimationsClass.scaleAnimation(100, durationTrack, 1, 1, durationTrack.getScaleY(), 1.6, false, 1, false);
        AnimationsClass.parallelAnimation(true, sliderThumbHoverOn, sliderTrackHoverOn);
    }


    public void durationSliderHoverOff() {
        ScaleTransition sliderThumbHoverOff = AnimationsClass.scaleAnimation(100, durationSlider.lookup(".thumb"), durationSlider.lookup(".thumb").getScaleX(), 0, durationSlider.lookup(".thumb").getScaleY(), 0, false, 1, false);
        ScaleTransition sliderTrackHoverOff = AnimationsClass.scaleAnimation(100, durationTrack, 1, 1, durationTrack.getScaleY(), 1, false, 1, false);
        AnimationsClass.parallelAnimation(true, sliderThumbHoverOff, sliderTrackHoverOff);
    }

    public void displayControls() {
        AnimationsClass.displayControls(this);
    }

    public void hideControls() {
        AnimationsClass.hideControls(this);
    }


    public void enterCaptionsButton() {
        captionsButtonHover = true;
    }

    public void exitCaptionsButton() {
        captionsButtonHover = false;
    }

    public void enterSettingsButton() {
        settingsButtonHover = true;
    }

    public void exitSettingsButton() {
        settingsButtonHover = false;
    }


    public void controlButtonHoverOn(StackPane stackPane){
        Region icon = (Region) stackPane.getChildren().get(1);

        if((stackPane.equals(captionsButtonPane) && captionsController.captionsSelected) || !stackPane.equals(captionsButtonPane)) AnimationsClass.AnimateBackgroundColor(icon, (Color) icon.getBackground().getFills().get(0).getFill(), Color.rgb(255, 255, 255), 200);
        else AnimationsClass.AnimateBackgroundColor(icon, (Color) icon.getBackground().getFills().get(0).getFill(), Color.rgb(130, 130, 130), 200);
    }


    public void controlButtonHoverOff(StackPane stackPane){
        Region icon = (Region) stackPane.getChildren().get(1);

        if((stackPane.equals(captionsButtonPane) && captionsController.captionsSelected) || !stackPane.equals(captionsButtonPane)) AnimationsClass.AnimateBackgroundColor(icon, (Color) icon.getBackground().getFills().get(0).getFill(), Color.rgb(200, 200, 200), 200);
        else AnimationsClass.AnimateBackgroundColor(icon, (Color) icon.getBackground().getFills().get(0).getFill(), Color.rgb(100, 100, 100), 200);
    }


}
