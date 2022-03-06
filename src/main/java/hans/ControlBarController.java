package hans;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.Animation;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
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
    ImageView settingsIcon;

    @FXML
    ImageView nextVideoIcon;

    @FXML
    ImageView captionsIcon;

    @FXML
    public Slider volumeSlider;

    @FXML
    public ProgressBar volumeTrack;

    @FXML
    public Slider durationSlider;

    @FXML
    public ProgressBar durationTrack;

    @FXML
    StackPane volumeSliderPane, durationPane;

    @FXML
    Label durationLabel;

    @FXML
    Line captionLine;

    @FXML
    ImageView playLogo;

    @FXML
    public ImageView fullScreenIcon;

    @FXML
    public ImageView volumeIcon;

    @FXML
    public Pane playButtonPane, nextVideoPane, volumeButtonPane, captionsButtonPane, settingsButtonPane, fullScreenButtonPane;

    @FXML
    public StackPane previousVideoPane;

    @FXML
    public Region previousVideoIcon;

    @FXML
            public HBox settingsBox1;


    MainController mainController;
    SettingsController settingsController;


    public Image maximize;

    Image minimize;

    public Image volumeUp;

    Image volumeDown;

    public Image volumeMute;

    Image settingsEnter;

    Image settingsExit;

    Image settingsImage;

    Image nextVideo;

    Image captionsImage;

    public double volumeValue;


    private File maximizeFile, minimizeFile;

    File playFile;

    File pauseFile;


    private File volumeUpFile;
    private File volumeDownFile;
    private File volumeMuteFile;

    Image playImage;
    Image pauseImage;

    Image replayImage;

    File settingsEnterFile;

    File settingsExitFile;

    private File settingsImageFile;

    private File captionsFile;

    File replayFile;

    private File nextVideoFile;


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

            settingsBox1.getChildren().removeAll(previousVideoPane,nextVideoPane);
        });


        volumeSliderPane.setClip(new Rectangle(60, 38.666666664));

        volumeSlider.setTranslateX(-60);
        volumeTrack.setTranslateX(-60);

        durationLabel.setTranslateX(-60);

        controlBar.setTranslateY(50);

        durationLabel.setOnMouseClicked((e) -> toggleDurationLabel());


        maximizeFile = new File("src/main/resources/hans/images/maximizeFile.png");
        minimizeFile = new File("src/main/resources/hans/images/minimizeFile.png");
        playFile = new File("src/main/resources/hans/images/play.png");
        volumeUpFile = new File("src/main/resources/hans/images/volumeUpFile.png");
        volumeDownFile = new File("src/main/resources/hans/images/volumeDownFile.png");
        volumeMuteFile = new File("src/main/resources/hans/images/volumeMuteFile.png");
        replayFile = new File("src/main/resources/hans/images/replay.png");
        pauseFile = new File("src/main/resources/hans/images/pause.png");
        settingsImageFile = new File("src/main/resources/hans/images/settingsImageFile.png");
        captionsFile = new File("src/main/resources/hans/images/captionsFile.png");
        nextVideoFile = new File("src/main/resources/hans/images/nextVideoFile.png");

        settingsEnterFile = new File("src/main/resources/hans/images/settingsEnterFile.gif");
        settingsExitFile = new File("src/main/resources/hans/images/settingsExitFile.gif");


        nextVideo = new Image(nextVideoFile.toURI().toString());
        maximize = new Image(maximizeFile.toURI().toString());
        minimize = new Image(minimizeFile.toURI().toString());
        playImage = new Image(playFile.toURI().toString());
        pauseImage = new Image(pauseFile.toURI().toString());
        replayImage = new Image(replayFile.toURI().toString());
        volumeUp = new Image(volumeUpFile.toURI().toString());
        volumeDown = new Image(volumeDownFile.toURI().toString());
        volumeMute = new Image(volumeMuteFile.toURI().toString());
        settingsImage = new Image(settingsImageFile.toURI().toString());
        captionsImage = new Image(captionsFile.toURI().toString());

        SVGPath previousVideoSVG = new SVGPath();
        previousVideoSVG.setContent("M 5.188,0.033 H 2.53 c -0.172,0 -0.315,0.182 -0.315,0.401 V 17.37 c 0,0.221 0.143,0.403 0.315,0.403 h 2.657 c 0.174,0 0.315,-0.183 0.315,-0.403 V 0.434 C 5.503,0.215 5.361,0.033 5.188,0.033 Z M 15.363,0.042 C 15.224,-0.028 15.06,-0.009 14.939,0.085 L 4.163,8.587 C 4.069,8.664 4.01,8.78 4.01,8.9 c 0,0.119 0.059,0.24 0.153,0.314 l 10.776,8.502 c 0.071,0.057 0.162,0.086 0.249,0.086 l 0.175,-0.039 c 0.139,-0.064 0.225,-0.207 0.225,-0.361 V 0.403 c 0,-0.154 -0.086,-0.296 -0.225,-0.361 z");
        previousVideoIcon.setShape(previousVideoSVG);

        playLogo.setImage(playImage);
        playButton.setBackground(Background.EMPTY);

        nextVideoButton.setBackground(Background.EMPTY);
        nextVideoIcon.setImage(nextVideo);


        playButton.setOnAction((e) -> playButtonClick1());

        fullScreenIcon.setImage(maximize);
        fullScreenButton.setBackground(Background.EMPTY);

        settingsButton.setBackground(Background.EMPTY);
        settingsIcon.setImage(settingsImage);

        volumeButton.setBackground(Background.EMPTY);
        volumeIcon.setImage(volumeUp);

        captionsButton.setBackground(Background.EMPTY);
        captionsIcon.setImage(captionsImage);


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
                volumeIcon.setImage(volumeMute);
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
                volumeIcon.setImage(volumeDown);
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
                volumeIcon.setImage(volumeUp);
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
                playLogo.setImage(playImage);
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
                    playLogo.setImage(replayImage);
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

                    playLogo.setImage(pauseImage);

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
            Utilities.setCurrentTimeLabel(durationLabel, mediaInterface.mediaPlayer, mediaInterface.currentVideo);
            showingTimeLeft = false;
        } else if(!showingTimeLeft && mediaInterface.currentVideo != null){
            Utilities.setTimeLeftLabel(durationLabel, mediaInterface.mediaPlayer, mediaInterface.currentVideo);
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
        playLogo.setImage(pauseImage);
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
        playLogo.setImage(playImage);
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
        playLogo.setImage(pauseImage);
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
            fullScreenIcon.setImage(minimize);
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
            fullScreenIcon.setImage(maximize);
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
        volumeIcon.setImage(volumeMute);
        if(mediaInterface.currentVideo != null) mediaInterface.mediaPlayer.setVolume(0);
        volumeValue = volumeSlider.getValue(); //stores the value of the volumeslider before setting it to 0

        volumeSlider.setValue(0);
    }

    public void unmute() {
        muted = false;
        volumeIcon.setImage(volumeUp);
        if(mediaInterface.currentVideo != null) mediaInterface.mediaPlayer.setVolume(volumeValue);
        volumeSlider.setValue(volumeValue); // sets volume back to the value it was at before muting
    }

    public void playPreviousMedia(){
        if (settingsController.settingsOpen) {
            settingsController.closeSettings();
        } else {
            mediaInterface.playPrevious();
        }
    }

    public void playNextMedia() {
        if (settingsController.settingsOpen) {
            settingsController.closeSettings();
        } else {
            mediaInterface.playNext();
        }
    }


    public void openCaptions() {
        mainController.captionsOpen = true;

        if (settingsController.settingsOpen) {
            settingsController.closeSettings();
        }

        AnimationsClass.scaleAnimation(100, captionLine, 0, 1, 1, 1, false, 1, true);

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
        AnimationsClass.scaleAnimation(100, captionLine, 1, 0, 1, 1, false, 1, true);

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

    public void removePreviousVideoButton(){
        settingsBox1.getChildren().remove(previousVideoPane);
    }

    public void addPreviousVideoButton(){
        if(!settingsBox1.getChildren().contains(previousVideoPane)) settingsBox1.getChildren().add(0, previousVideoPane);
    }

    public void removeNextVideoButton(){
        settingsBox1.getChildren().remove(nextVideoPane);
    }

    public void addNextVideoButton(){
        if(!settingsBox1.getChildren().contains(nextVideoPane)) settingsBox1.getChildren().add(1, nextVideoPane);
    }

}
