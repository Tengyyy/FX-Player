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
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

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

    String previousVideoPath = "M6,18V6H8V18H6M9.5,12L18,6V18L9.5,12Z";
    String playPath = "M8,5.14V19.14L19,12.14L8,5.14Z";
    String pausePath = "M14,19H18V5H14M6,19H10V5H6V19Z";
    String replayPath = "M12,5V1L7,6L12,11V7A6,6 0 0,1 18,13A6,6 0 0,1 12,19A6,6 0 0,1 6,13H4A8,8 0 0,0 12,21A8,8 0 0,0 20,13A8,8 0 0,0 12,5Z";
    String nextVideoPath = "M16,18H18V6H16M6,18L14.5,12L6,6V18Z";
    String highVolumePath = "M14,3.23V5.29C16.89,6.15 19,8.83 19,12C19,15.17 16.89,17.84 14,18.7V20.77C18,19.86 21,16.28 21,12C21,7.72 18,4.14 14,3.23M16.5,12C16.5,10.23 15.5,8.71 14,7.97V16C15.5,15.29 16.5,13.76 16.5,12M3,9V15H7L12,20V4L7,9H3Z";
    String lowVolumePath = "M5,9V15H9L14,20V4L9,9M18.5,12C18.5,10.23 17.5,8.71 16,7.97V16C17.5,15.29 18.5,13.76 18.5,12Z";
    String volumeMutedPath = "M12,4L9.91,6.09L12,8.18M4.27,3L3,4.27L7.73,9H3V15H7L12,20V13.27L16.25,17.53C15.58,18.04 14.83,18.46 14,18.7V20.77C15.38,20.45 16.63,19.82 17.68,18.96L19.73,21L21,19.73L12,10.73M19,12C19,12.94 18.8,13.82 18.46,14.64L19.97,16.15C20.62,14.91 21,13.5 21,12C21,7.72 18,4.14 14,3.23V5.29C16.89,6.15 19,8.83 19,12M16.5,12C16.5,10.23 15.5,8.71 14,7.97V10.18L16.45,12.63C16.5,12.43 16.5,12.21 16.5,12Z";
    String captionsPath = "M20 4H4c-1.103 0-2 .897-2 2v12c0 1.103.897 2 2 2h16c1.103 0 2-.897 2-2V6c0-1.103-.897-2-2-2zm-9 6H8v4h3v2H8c-1.103 0-2-.897-2-2v-4c0-1.103.897-2 2-2h3v2zm7 0h-3v4h3v2h-3c-1.103 0-2-.897-2-2v-4c0-1.103.897-2 2-2h3v2z";
    String settingsPath = "M12,15.5A3.5,3.5 0 0,1 8.5,12A3.5,3.5 0 0,1 12,8.5A3.5,3.5 0 0,1 15.5,12A3.5,3.5 0 0,1 12,15.5M19.43,12.97C19.47,12.65 19.5,12.33 19.5,12C19.5,11.67 19.47,11.34 19.43,11L21.54,9.37C21.73,9.22 21.78,8.95 21.66,8.73L19.66,5.27C19.54,5.05 19.27,4.96 19.05,5.05L16.56,6.05C16.04,5.66 15.5,5.32 14.87,5.07L14.5,2.42C14.46,2.18 14.25,2 14,2H10C9.75,2 9.54,2.18 9.5,2.42L9.13,5.07C8.5,5.32 7.96,5.66 7.44,6.05L4.95,5.05C4.73,4.96 4.46,5.05 4.34,5.27L2.34,8.73C2.21,8.95 2.27,9.22 2.46,9.37L4.57,11C4.53,11.34 4.5,11.67 4.5,12C4.5,12.33 4.53,12.65 4.57,12.97L2.46,14.63C2.27,14.78 2.21,15.05 2.34,15.27L4.34,18.73C4.46,18.95 4.73,19.03 4.95,18.95L7.44,17.94C7.96,18.34 8.5,18.68 9.13,18.93L9.5,21.58C9.54,21.82 9.75,22 10,22H14C14.25,22 14.46,21.82 14.5,21.58L14.87,18.93C15.5,18.67 16.04,18.34 16.56,17.94L19.05,18.95C19.27,19.03 19.54,18.95 19.66,18.73L21.66,15.27C21.78,15.05 21.73,14.78 21.54,14.63L19.43,12.97Z";
    String maximizePath = "M5,5H10V7H7V10H5V5M14,5H19V10H17V7H14V5M17,14H19V19H14V17H17V14M10,17V19H5V14H7V17H10Z";
    String minimizePath = "M14,14H19V16H16V19H14V14M5,14H10V19H8V16H5V14M8,5H10V10H5V8H8V5M19,8V10H14V5H16V8H19Z";

    SVGPath previousVideoSVG, playSVG, pauseSVG, replaySVG, nextVideoSVG, highVolumeSVG, lowVolumeSVG, volumeMutedSVG, captionsSVG, settingsSVG, maximizeSVG, minimizeSVG;

    MainController mainController;
    SettingsController settingsController;


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

    ControlTooltip play, pause, replay, mute, unmute, settings, fullScreen, exitFullScreen, captions, nextVideoTooltip, previousVideoTooltip;

    MediaInterface mediaInterface;

    ScaleTransition fullScreenButtonScaleTransition;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Platform.runLater(() -> {
            pause = new ControlTooltip("Pause (k)", playButton, false, controlBar, 0);
            replay = new ControlTooltip("Replay (k)", playButton, false, controlBar, 0);
            play = new ControlTooltip("Play (k)", playButton, false, controlBar, 0);
            unmute = new ControlTooltip("Unmute (m)", volumeButton, false, controlBar, 0);
            mute = new ControlTooltip("Mute (m)", volumeButton, false, controlBar, 0);
            settings = new ControlTooltip("Settings (s)", settingsButton, false, controlBar, 0);
            exitFullScreen = new ControlTooltip("Exit full screen (f)", fullScreenButton, false, controlBar, 0);
            fullScreen = new ControlTooltip("Full screen (f)", fullScreenButton, false, controlBar, 0);
            nextVideoTooltip = new ControlTooltip("Next video (SHIFT + N)", nextVideoButton, false, controlBar, 0);
            previousVideoTooltip = new ControlTooltip("Previous video (SHIFT + P)", previousVideoButton, false, controlBar, 0);
            captions = new ControlTooltip("Subtitles/closed captions (c)", captionsButton, false, controlBar, 0);
        });

        previousVideoSVG = new SVGPath();
        previousVideoSVG.setContent(previousVideoPath);

        playSVG = new SVGPath();
        playSVG.setContent(playPath);

        pauseSVG = new SVGPath();
        pauseSVG.setContent(pausePath);

        replaySVG = new SVGPath();
        replaySVG.setContent(replayPath);

        nextVideoSVG = new SVGPath();
        nextVideoSVG.setContent(nextVideoPath);

        highVolumeSVG = new SVGPath();
        highVolumeSVG.setContent(highVolumePath);

        lowVolumeSVG = new SVGPath();
        lowVolumeSVG.setContent(lowVolumePath);

        volumeMutedSVG = new SVGPath();
        volumeMutedSVG.setContent(volumeMutedPath);

        captionsSVG = new SVGPath();
        captionsSVG.setContent(captionsPath);

        settingsSVG = new SVGPath();
        settingsSVG.setContent(settingsPath);

        maximizeSVG = new SVGPath();
        maximizeSVG.setContent(maximizePath);

        minimizeSVG = new SVGPath();
        minimizeSVG.setContent(minimizePath);

        volumeSliderPane.setClip(new Rectangle(60, 38.666666664));

        volumeSlider.setTranslateX(-60);
        volumeTrack.setTranslateX(-60);

        durationLabel.setTranslateX(-60);

        controlBar.setTranslateY(50);

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


        volumeSlider.valueChangingProperty().addListener((observable, oldValue, newValue) -> {

            if (!newValue && settingsController.settingsOpen) {
                settingsController.closeSettings();
            }

        });


        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {

            if(mediaInterface.currentVideo != null) mediaInterface.mediaPlayer.setVolume(volumeSlider.getValue() / 100);

            volumeTrack.setProgress(volumeSlider.getValue() / 100);

            if (volumeSlider.getValue() == 0) {
                volumeIcon.setShape(volumeMutedSVG);
                muted = true;

                if (mute.isShowing()) {
                    mute.hide();
                    unmute.hide();
                    unmute = new ControlTooltip("Unmute (m)", volumeButton, false, controlBar, 0);
                    unmute.showTooltip();
                } else {
                    unmute = new ControlTooltip("Unmute (m)", volumeButton, false, controlBar, 0);
                }

            } else if (volumeSlider.getValue() < 50) {
                volumeIcon.setShape(lowVolumeSVG);
                muted = false;

                if (mute.isShowing() || unmute.isShowing()) {
                    mute.hide();
                    unmute.hide();
                    mute = new ControlTooltip("Mute (m)", volumeButton, false, controlBar, 0);
                    mute.showTooltip();
                } else {
                    mute = new ControlTooltip("Mute (m)", volumeButton, false, controlBar, 0);
                }
            } else {
                volumeIcon.setShape(highVolumeSVG);
                muted = false;

                if (mute.isShowing() || unmute.isShowing()) {
                    mute.hide();
                    unmute.hide();
                    mute = new ControlTooltip("Mute (m)", volumeButton, false, controlBar, 0);
                    mute.showTooltip();
                } else {
                    mute = new ControlTooltip("Mute (m)", volumeButton, false, controlBar, 0);
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
            if(mediaInterface.currentVideo != null) mediaInterface.updateMedia(newValue.doubleValue());
            durationTrack.setProgress(durationSlider.getValue() / durationSlider.getMax());
        });

        // vaja ära fixida see jama
        durationSlider.valueChangingProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue) { // pause video when user starts seeking
                playIcon.setShape(pauseSVG);
                if(mediaInterface.currentVideo != null) {
                    mediaInterface.mediaPlayer.pause();
                    mediaInterface.playing = false;
                }
                if (pause.isShowing()) {
                    pause.hide();
                    play = new ControlTooltip("Play (k)", playButton, false, controlBar, 0);
                    play.showTooltip();
                } else {
                    play = new ControlTooltip("Play (k)", playButton, false, controlBar, 0);
                }
            } else {

                if (!durationSliderHover) {
                    durationSliderHoverOff();
                }

                if(mediaInterface.currentVideo != null) mediaInterface.mediaPlayer.seek(Duration.seconds(durationSlider.getValue())); // seeks to exact position when user finishes dragging

                if (settingsController.settingsOpen) { // close settings pane after user finishes seeking media (if its open)
                    settingsController.closeSettings();
                }

                if (mediaInterface.atEnd) { // if user drags the duration slider to the end turn play button to replay button
                    playIcon.setShape(replaySVG);
                    playButton.setOnAction((e) -> playButtonClick2());

                    if (play.isShowing() || pause.isShowing()) {
                        play.hide();
                        pause.hide();
                        replay = new ControlTooltip("Replay (k)", playButton, false, controlBar, 0);
                        replay.showTooltip();
                    } else {
                        replay = new ControlTooltip("Replay (k)", playButton, false, controlBar, 0);
                    }
                    if(mainController.menuController != null){
                        mainController.menuController.activeItem.playIcon.setShape(mainController.menuController.activeItem.playSVG);
                        mainController.menuController.activeItem.play.updateText("Play video");
                    }

                } else if (mediaInterface.wasPlaying) { // starts playing the video in the new position when user finishes seeking with the slider
                    if(mediaInterface.currentVideo != null) {
                        mediaInterface.mediaPlayer.play();
                        mediaInterface.playing = true;
                    }

                    playIcon.setShape(pauseSVG);

                    if (play.isShowing() || replay.isShowing()) {
                        play.hide();
                        replay.hide();
                        pause = new ControlTooltip("Pause (k)", playButton, false, controlBar, 0);
                        pause.showTooltip();
                    } else {
                        pause = new ControlTooltip("Pause (k)", playButton, false, controlBar, 0);
                    }
                }
            }
        });

        // this will all go to FocusTraversalEngine.java
        durationSlider.focusedProperty()
                .addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                    if (!newValue) {
                        durationSlider.setStyle("-fx-border-color: transparent;");
                    } else {
                        mainController.focusNodeTracker = 1;
                    }
                });

        playButton.focusedProperty()
                .addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                    if (!newValue) {
                        playButton.setStyle("-fx-border-color: transparent;");
                    } else {
                        mainController.focusNodeTracker = 2;
                    }
                });

        nextVideoButton.focusedProperty()
                .addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                    if (!newValue) {
                        nextVideoButton.setStyle("-fx-border-color: transparent;");
                    } else {
                        mainController.focusNodeTracker = 3;
                    }
                });

        volumeButton.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (!newValue) {
                volumeButton.setStyle("-fx-border-color: transparent;");
            } else {
                mainController.focusNodeTracker = 4;
            }
        });

        volumeSlider.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {

            sliderFocus = newValue;

            if (!newValue) {
                if (isExited) {
                    volumeSliderExit();
                }
                volumeSlider.setStyle("-fx-border-color: transparent;");

            } else {
                if (isExited) {
                    volumeSliderEnter();
                }
                mainController.focusNodeTracker = 5;
            }

        });

        settingsButton.focusedProperty()
                .addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                    if (!newValue) {
                        settingsButton.setStyle("-fx-border-color: transparent;");
                    } else {
                        mainController.focusNodeTracker = 6;
                    }
                });

        fullScreenButton.focusedProperty()
                .addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                    if (!newValue) {
                        fullScreenButton.setStyle("-fx-border-color: transparent;");
                    } else {
                        mainController.focusNodeTracker = 7;
                    }
                });
        //////////////////////////////////////////////////////////////////////////

    }

    public void init(MainController mainController, SettingsController settingsController, MediaInterface mediaInterface) {
        this.mainController = mainController;
        this.settingsController = settingsController;
        this.mediaInterface = mediaInterface;

        mouseEventTracker = new MouseEventTracker(4, mainController, this, settingsController); // creates instance of the MouseEventTracker class which keeps track of when to hide and show the control-bar

    }

    public void toggleDurationLabel() {
        if (showingTimeLeft && mediaInterface.currentVideo != null) {
            Utilities.setCurrentTimeLabel(durationLabel, mediaInterface.mediaPlayer, mediaInterface.currentVideo.getMedia());
            showingTimeLeft = false;
        } else if(!showingTimeLeft && mediaInterface.currentVideo != null){
            Utilities.setTimeLeftLabel(durationLabel, mediaInterface.mediaPlayer, mediaInterface.currentVideo.getMedia());
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

        if(mediaInterface.currentVideo != null) {
            mediaInterface.mediaPlayer.play();

            if(mainController.menuController != null){
                mainController.menuController.activeItem.playIcon.setShape(mainController.menuController.activeItem.pauseSVG);
                mainController.menuController.activeItem.play.updateText("Pause video");
            }
        }
        playIcon.setShape(pauseSVG);
        mediaInterface.playing = true;

        if (play.isShowing()) {
            play.hide();
            pause = new ControlTooltip("Pause (k)", playButton, false, controlBar, 0);
            pause.showTooltip();
        } else {
            pause = new ControlTooltip("Pause (k)", playButton, false, controlBar, 0);
        }

        mediaInterface.wasPlaying = mediaInterface.playing; // updates the value of wasPlaying variable - when this method is called the
        // user really wants to play or pause the video and therefore the previous
        // wasPlaying state no longer needs to be tracked
    }

    public void pause() {

        if(mediaInterface.currentVideo != null) {
            mediaInterface.mediaPlayer.pause();

            if(mainController.menuController != null){
                mainController.menuController.activeItem.playIcon.setShape(mainController.menuController.activeItem.playSVG);
                mainController.menuController.activeItem.play.updateText("Play video");
            }
        }
        playIcon.setShape(playSVG);
        mediaInterface.playing = false;

        if (pause.isShowing()) {
            pause.hide();
            play = new ControlTooltip("Play (k)", playButton, false, controlBar, 0);
            play.showTooltip();
        } else {
            play = new ControlTooltip("Play (k)", playButton, false, controlBar, 0);
        }

        mediaInterface.wasPlaying = mediaInterface.playing;
    }


    public void replayMedia() {

        if (replay.isShowing()) {
            replay.hide();
            pause = new ControlTooltip("Pause (k)", playButton, false, controlBar, 0);
            pause.showTooltip();
        } else {
            pause = new ControlTooltip("Pause (k)", playButton, false, controlBar, 0);
        }

        if(mediaInterface.currentVideo != null){
            mediaInterface.mediaPlayer.seek(Duration.ZERO);
            mediaInterface.mediaPlayer.play();

            if(mainController.menuController != null){
                mainController.menuController.activeItem.playIcon.setShape(mainController.menuController.activeItem.pauseSVG);
                mainController.menuController.activeItem.play.updateText("Pause video");
            }
        }
        mediaInterface.playing = true;
        mediaInterface.atEnd = false;
        playIcon.setShape(pauseSVG);
        mediaInterface.seekedToEnd = false;
        playButton.setOnAction((e) -> playButtonClick1());

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

            if (!mainController.captionsOpen && !settingsController.settingsOpen) {
                if (fullScreen.isShowing()) {
                    fullScreen.hide();
                    exitFullScreen = new ControlTooltip("Exit full screen (f)", fullScreenButton, false, controlBar, 0);
                    exitFullScreen.showTooltip();
                } else {
                    exitFullScreen = new ControlTooltip("Exit full screen (f)", fullScreenButton, false, controlBar, 0);
                }
            }
        } else {
            fullScreenIcon.setShape(maximizeSVG);
            App.fullScreen = false;

            if (!mainController.captionsOpen && !settingsController.settingsOpen) {
                if (exitFullScreen.isShowing()) {
                    exitFullScreen.hide();
                    fullScreen = new ControlTooltip("Full screen (f)", fullScreenButton, false, controlBar, 0);
                    fullScreen.showTooltip();
                } else {
                    fullScreen = new ControlTooltip("Full screen (f)", fullScreenButton, false, controlBar, 0);
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
        if(mediaInterface.currentVideo != null) mediaInterface.mediaPlayer.setVolume(0);
        volumeValue = volumeSlider.getValue(); //stores the value of the volumeslider before setting it to 0

        volumeSlider.setValue(0);
    }

    public void unmute() {
        muted = false;
        volumeIcon.setShape(highVolumeSVG);
        if(mediaInterface.currentVideo != null) mediaInterface.mediaPlayer.setVolume(volumeValue);
        volumeSlider.setValue(volumeValue); // sets volume back to the value it was at before muting
    }

    public void playPreviousMedia(){
        if (settingsController.settingsOpen) {
            settingsController.closeSettings();
        } else {
            if(mediaInterface.playedVideoIndex == 0 || mediaInterface.playedVideoList.isEmpty()) return;
            mediaInterface.playPrevious();
        }
    }

    public void playNextMedia() {
        if (settingsController.settingsOpen) {
            settingsController.closeSettings();
        } else {
            if(mediaInterface.videoList.size() < 2) return;
            mediaInterface.playNext();
        }
    }


    public void openCaptions() {
        mainController.captionsOpen = true;

        if (settingsController.settingsOpen) {
            settingsController.closeSettings();
        }

        AnimationsClass.scaleAnimation(100, captionsButtonLine, 0, 1, 1, 1, false, 1, true);

        if (captions.isShowing() || settings.isShowing() || fullScreen.isShowing() || exitFullScreen.isShowing()) {
            captions.hide();
            settings.hide();
            fullScreen.hide();
            exitFullScreen.hide();
        }
        captionsButton.setOnMouseEntered(null);
        settingsButton.setOnMouseEntered(null);
        fullScreenButton.setOnMouseEntered(null);
    }

    public void closeCaptions() {
        mainController.captionsOpen = false;

        if (captionsButtonHover) {
            captions = new ControlTooltip("Subtitles/closed captions (c)", captionsButton, false, controlBar, 0);
            captions.showTooltip();

            settings = new ControlTooltip("Settings (s)", settingsButton, false, controlBar, 0);

            if (App.fullScreen)
                exitFullScreen = new ControlTooltip("Exit full screen (f)", fullScreenButton, false, controlBar, 0);
            else fullScreen = new ControlTooltip("Full screen (f)", fullScreenButton, false, controlBar, 0);
        } else if (settingsButtonHover) {
            settings = new ControlTooltip("Settings (s)", settingsButton, false, controlBar, 0);
            settings.showTooltip();

            captions = new ControlTooltip("Subtitles/closed captions (c)", captionsButton, false, controlBar, 0);

            if (App.fullScreen)
                exitFullScreen = new ControlTooltip("Exit full screen (f)", fullScreenButton, false, controlBar, 0);
            else fullScreen = new ControlTooltip("Full screen (f)", fullScreenButton, false, controlBar, 0);
        } else if (fullScreenButtonHover) {
            if (App.fullScreen) {
                exitFullScreen = new ControlTooltip("Exit full screen (f)", fullScreenButton, false, controlBar, 0);
                exitFullScreen.showTooltip();
            } else {
                fullScreen = new ControlTooltip("Full screen (f)", fullScreenButton, false, controlBar, 0);
                fullScreen.showTooltip();
            }

            settings = new ControlTooltip("Settings (s)", settingsButton, false, controlBar, 0);

            captions = new ControlTooltip("Subtitles/closed captions (c)", captionsButton, false, controlBar, 0);
        } else {
            settings = new ControlTooltip("Settings (s)", settingsButton, false, controlBar, 0);

            captions = new ControlTooltip("Subtitles/closed captions (c)", captionsButton, false, controlBar, 0);

            if (App.fullScreen)
                exitFullScreen = new ControlTooltip("Exit full screen (f)", fullScreenButton, false, controlBar, 0);
            else fullScreen = new ControlTooltip("Full screen (f)", fullScreenButton, false, controlBar, 0);
        }

        //AnimationsClass.closeCaptions(captionLine);
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
        if (!mainController.captionsOpen)
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


}
