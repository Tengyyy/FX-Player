package hans;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.Animation;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;
import org.jcodec.containers.mp4.boxes.SampleSizesBox;

public class ControlBarController implements Initializable {

    @FXML
    VBox controlBar;

    @FXML
    Button fullScreenButton, playButton, volumeButton, settingsButton, nextVideoButton, captionsButton, previousVideoButton;

    @FXML
    public Slider volumeSlider, durationSlider;

    @FXML
    public ProgressBar volumeTrack, durationTrack;

    @FXML
    StackPane volumeSliderPane, previousVideoPane, playButtonPane, nextVideoPane, volumeButtonPane, captionsButtonPane, settingsButtonPane, fullScreenButtonPane;

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


    public double volumeValue;

    public boolean muted = false;
    boolean isExited = true;
    boolean sliderFocus = false;
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

    ControlTooltip play, mute, unmute, settings, fullScreen, exitFullScreen, captions, nextVideoTooltip, previousVideoTooltip;

    MediaInterface mediaInterface;

    ScaleTransition fullScreenButtonScaleTransition;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Platform.runLater(() -> {
            play = new ControlTooltip("Play (k)", playButton, controlBar, 0, false);
            unmute = new ControlTooltip("Unmute (m)", volumeButton, controlBar, 0, false);
            mute = new ControlTooltip("Mute (m)", volumeButton, controlBar, 0, false);
            settings = new ControlTooltip("Settings (s)", settingsButton, controlBar, 0, false);
            exitFullScreen = new ControlTooltip("Exit full screen (f)", fullScreenButton, controlBar, 0, false);
            fullScreen = new ControlTooltip("Full screen (f)", fullScreenButton, controlBar, 0, false);
            nextVideoTooltip = new ControlTooltip("Next video (SHIFT + N)", nextVideoButton, controlBar, 0, false);
            previousVideoTooltip = new ControlTooltip("Previous video (SHIFT + P)", previousVideoButton, controlBar, 0, false);
            captions = new ControlTooltip("Subtitles/closed captions (c)", captionsButton, controlBar, 0, false);
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
        lowVolumeSVG.setContent(App.svgMap.get(SVG.VOLUME_MUTED));

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

        volumeSliderPane.setClip(new Rectangle(60, 38.666666664));

        volumeSlider.setTranslateX(-60);
        volumeTrack.setTranslateX(-60);

        durationLabel.setTranslateX(-60);

        controlBar.setTranslateY(40);

        durationLabel.setOnMouseClicked((e) -> toggleDurationLabel());

        previousVideoIcon.setShape(previousVideoSVG);
        playIcon.setShape(playSVG);
        nextVideoIcon.setShape(nextVideoSVG);
        volumeIcon.setShape(highVolumeSVG);
        captionsIcon.setShape(captionsSVG);
        settingsIcon.setShape(settingsSVG);
        fullScreenIcon.setShape(maximizeSVG);



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

            if (!newValue && settingsController.settingsOpen) {
                settingsController.closeSettings();
            }
        });


        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {

            if(menuController.activeItem != null) mediaInterface.mediaPlayer.setVolume(volumeSlider.getValue() / 100);

            volumeTrack.setProgress(volumeSlider.getValue() / 100);

            if (volumeSlider.getValue() == 0) {
                volumeIcon.setShape(volumeMutedSVG);
                muted = true;

                if (mute.isShowing()) {
                    mute.hide();
                    unmute.hide();
                    unmute = new ControlTooltip("Unmute (m)", volumeButton, controlBar, 0, false);
                    unmute.showTooltip();
                } else {
                    unmute = new ControlTooltip("Unmute (m)", volumeButton, controlBar, 0, false);
                }

            } else if (volumeSlider.getValue() < 50) {
                volumeIcon.setShape(lowVolumeSVG);
                muted = false;

                if (mute.isShowing() || unmute.isShowing()) {
                    mute.hide();
                    unmute.hide();
                    mute = new ControlTooltip("Mute (m)", volumeButton, controlBar, 0, false);
                    mute.showTooltip();
                } else {
                    mute = new ControlTooltip("Mute (m)", volumeButton, controlBar, 0, false);
                }
            } else {
                volumeIcon.setShape(highVolumeSVG);
                muted = false;

                if (mute.isShowing() || unmute.isShowing()) {
                    mute.hide();
                    unmute.hide();
                    mute = new ControlTooltip("Mute (m)", volumeButton, controlBar, 0, false);
                    mute.showTooltip();
                } else {
                    mute = new ControlTooltip("Mute (m)", volumeButton, controlBar, 0, false);
                }
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
            if(menuController.activeItem != null) mediaInterface.updateMedia(newValue.doubleValue());
            durationTrack.setProgress(durationSlider.getValue() / durationSlider.getMax());
        });

        durationSlider.valueChangingProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue) { // pause video when user starts seeking
                playIcon.setShape(playSVG);
                if(menuController.activeItem != null) {
                    mediaInterface.mediaPlayer.pause();
                    mediaInterface.playing = false;
                }
                play.updateText("Play (k)");

            } else {

                if (!durationSliderHover) {
                    durationSliderHoverOff();
                }

                if(menuController.activeItem != null) mediaInterface.mediaPlayer.seek(Duration.seconds(durationSlider.getValue())); // seeks to exact position when user finishes dragging

                if (settingsController.settingsOpen) { // close settings pane after user finishes seeking media (if its open)
                    settingsController.closeSettings();
                }

                if (mediaInterface.atEnd) { // if user drags the duration slider to the end turn play button to replay button
                    playIcon.setShape(replaySVG);
                    playButton.setOnAction((e) -> playButtonClick2());

                    play.updateText("Replay (k)");

                    menuController.activeItem.playIcon.setShape(menuController.activeItem.playSVG);
                    menuController.activeItem.play.updateText("Play video");

                } else if (mediaInterface.wasPlaying) { // starts playing the video in the new position when user finishes seeking with the slider
                    if(menuController.activeItem != null) {
                        mediaInterface.mediaPlayer.play();
                        mediaInterface.playing = true;
                    }

                    playIcon.setShape(pauseSVG);

                    play.updateText("Pause (k)");
                }
            }
        });


    }

    public void init(MainController mainController, SettingsController settingsController, MenuController menuController, MediaInterface mediaInterface) {
        this.mainController = mainController;
        this.settingsController = settingsController;
        this.menuController = menuController;
        this.mediaInterface = mediaInterface;

        mouseEventTracker = new MouseEventTracker(4, mainController, this, settingsController); // creates instance of the MouseEventTracker class which keeps track of when to hide and show the control-bar

    }

    public void toggleDurationLabel() {
        if (showingTimeLeft && menuController.activeItem != null) {
            Utilities.setCurrentTimeLabel(durationLabel, mediaInterface.mediaPlayer, menuController.activeItem.getMediaItem().getMedia());
            showingTimeLeft = false;
        } else if(!showingTimeLeft && menuController.activeItem != null){
            Utilities.setTimeLeftLabel(durationLabel, mediaInterface.mediaPlayer, menuController.activeItem.getMediaItem().getMedia());
            showingTimeLeft = true;
        }
    }


    public void playButtonClick1() {
        if (settingsController.settingsOpen) {
            settingsController.closeSettings();
        } else {
            if (mediaInterface.playing) {
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

        if(menuController.activeItem != null) {
            mediaInterface.mediaPlayer.play();

            menuController.activeItem.playIcon.setShape(menuController.activeItem.pauseSVG);
            menuController.activeItem.play.updateText("Pause video");
            menuController.activeItem.columns.play();

            if (menuController.historyBox.index != -1) {
                HistoryItem historyItem = menuController.history.get(menuController.historyBox.index);
                historyItem.playIcon.setShape(historyItem.pauseSVG);
                historyItem.play.updateText("Pause video");
            }

            playIcon.setShape(pauseSVG);
            mediaInterface.playing = true;

            play.updateText("Pause (k)");

            mediaInterface.wasPlaying = mediaInterface.playing; // updates the value of wasPlaying variable - when this method is called the
            // user really wants to play or pause the video and therefore the previous
            // wasPlaying state no longer needs to be tracked
        }
    }

    public void pause() {

        if(menuController.activeItem != null) {
            mediaInterface.mediaPlayer.pause();

            menuController.activeItem.playIcon.setShape(menuController.activeItem.playSVG);
            menuController.activeItem.play.updateText("Play video");
            menuController.activeItem.columns.pause();

            if (menuController.historyBox.index != -1) {
                HistoryItem historyItem = menuController.history.get(menuController.historyBox.index);
                historyItem.playIcon.setShape(historyItem.playSVG);
                historyItem.play.updateText("Play video");
            }

            playIcon.setShape(playSVG);
            mediaInterface.playing = false;

            play.updateText("Play (k)");

            mediaInterface.wasPlaying = mediaInterface.playing;
        }
    }


    public void replayMedia() {


        if(menuController.activeItem != null) {
            mediaInterface.mediaPlayer.seek(Duration.ZERO);
            mediaInterface.mediaPlayer.play();

            menuController.activeItem.playIcon.setShape(menuController.activeItem.pauseSVG);
            menuController.activeItem.play.updateText("Pause video");

            mediaInterface.playing = true;
            mediaInterface.atEnd = false;
            playIcon.setShape(pauseSVG);
            mediaInterface.seekedToEnd = false;
            playButton.setOnAction((e) -> playButtonClick1());

            play.updateText("Pause (k)");
        }

    }

    public void enterArea() {
        if (isExited && !sliderFocus) {
            volumeSliderEnter();
        }
        isExited = false;
    }

    public void exitArea() {
        if (!sliderFocus && !isExited) {
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
                    exitFullScreen = new ControlTooltip("Exit full screen (f)", fullScreenButton, controlBar, 0, false);
                    exitFullScreen.showTooltip();
                } else {
                    exitFullScreen = new ControlTooltip("Exit full screen (f)", fullScreenButton, controlBar, 0, false);
                }
            }
        } else {
            fullScreenIcon.setShape(maximizeSVG);
            App.fullScreen = false;

            if (!settingsController.settingsOpen) {
                if (exitFullScreen.isShowing()) {
                    exitFullScreen.hide();
                    fullScreen = new ControlTooltip("Full screen (f)", fullScreenButton, controlBar, 0, false);
                    fullScreen.showTooltip();
                } else {
                    fullScreen = new ControlTooltip("Full screen (f)", fullScreenButton, controlBar, 0, false);
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
        volumeIcon.setShape(volumeMutedSVG);
        if(menuController.activeItem != null) mediaInterface.mediaPlayer.setVolume(0);
        volumeValue = volumeSlider.getValue(); //stores the value of the volumeslider before setting it to 0

        volumeSlider.setValue(0);
    }

    public void unmute() {
        muted = false;
        volumeIcon.setShape(highVolumeSVG);
        if(menuController.activeItem != null) mediaInterface.mediaPlayer.setVolume(volumeValue);
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
        mainController.captionsOn = true;

        if (settingsController.settingsOpen) {
            settingsController.closeSettings();
        }

        AnimationsClass.scaleAnimation(100, captionsButtonLine, 0, 1, 1, 1, false, 1, true);

    }

    public void closeCaptions() {
        mainController.captionsOn = false;

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
        if (!mainController.captionsOn)
            openCaptions();
        else
            closeCaptions();
    }

    public void durationSliderHoverOn() {
        ScaleTransition sliderThumbHoverOn = AnimationsClass.scaleAnimation(100, durationSlider.lookup(".thumb"), 0, 1, 0, 1, false, 1, false);
        ScaleTransition sliderTrackHoverOn = AnimationsClass.scaleAnimation(100, durationTrack, 1, 1, 1, 1.6, false, 1, false);
        AnimationsClass.parallelAnimation(true, sliderThumbHoverOn, sliderTrackHoverOn);
    }


    public void durationSliderHoverOff() {
        ScaleTransition sliderThumbHoverOff = AnimationsClass.scaleAnimation(100, durationSlider.lookup(".thumb"), 1, 0, 1, 0, false, 1, false);
        ScaleTransition sliderTrackHoverOff = AnimationsClass.scaleAnimation(100, durationTrack, 1, 1, 1.6, 1, false, 1, false);
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

        AnimationsClass.AnimateBackgroundColor(icon, (Color) icon.getBackground().getFills().get(0).getFill(), Color.rgb(255, 255, 255), 200);
    }


    public void controlButtonHoverOff(StackPane stackPane){
        Region icon = (Region) stackPane.getChildren().get(1);

        AnimationsClass.AnimateBackgroundColor(icon, (Color) icon.getBackground().getFills().get(0).getFill(), Color.rgb(200, 200, 200), 200);

    }



}
