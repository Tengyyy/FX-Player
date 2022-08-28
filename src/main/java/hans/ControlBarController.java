package hans;

import java.net.URL;
import java.util.ResourceBundle;

import hans.Menu.MenuController;
import hans.Settings.SettingsController;
import hans.Settings.SettingsState;
import javafx.animation.Animation;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

public class ControlBarController implements Initializable {

    @FXML
    VBox controlBar;

    @FXML
    public
    StackPane controlBarWrapper;

    @FXML
    public
    Button fullScreenButton;
    @FXML
    Button playButton;
    @FXML
    Button volumeButton;
    @FXML
    public
    Button settingsButton;
    @FXML
    Button nextVideoButton;
    @FXML
    public
    Button captionsButton;
    @FXML
    Button previousVideoButton;
    @FXML
    public
    Button miniplayerButton;

    @FXML
    public Slider volumeSlider, durationSlider;

    @FXML
    public ProgressBar volumeTrack, durationTrack;

    @FXML
    StackPane volumeSliderPane, previousVideoPane, playButtonPane, nextVideoPane, volumeButtonPane, captionsButtonPane, settingsButtonPane, miniplayerButtonPane, fullScreenButtonPane, durationPane;

    @FXML
    Label durationLabel;

    @FXML
    Line captionsButtonLine;

    @FXML
    public Region previousVideoIcon, playIcon, nextVideoIcon, volumeIcon, captionsIcon, settingsIcon, fullScreenIcon, miniplayerIcon;



    SVGPath previousVideoSVG, playSVG, pauseSVG, replaySVG, nextVideoSVG, highVolumeSVG, lowVolumeSVG, volumeMutedSVG, captionsSVG, settingsSVG, maximizeSVG, minimizeSVG, miniplayerSVG;

    MainController mainController;
    SettingsController settingsController;
    MenuController menuController;
    CaptionsController captionsController;


    double volumeValue;

    boolean muted = false;
    boolean isExited = true;
    boolean showingTimeLeft = false;
    public boolean durationSliderHover = false;
    boolean controlBarOpen = false;


    // variables to keep track of whether mouse is hovering any control button
    boolean previousVideoButtonHover = false;
    boolean playButtonHover = false;
    boolean nextVideoButtonHover = false;

    boolean volumeButtonHover = false;
    public boolean captionsButtonHover = false;
    public boolean settingsButtonHover = false;
    public boolean fullScreenButtonHover = false;
    public boolean miniplayerButtonHover = false;

    boolean previousVideoButtonEnabled = false;
    boolean playButtonEnabled = false;
    public boolean nextVideoButtonEnabled = false;


    MouseEventTracker mouseEventTracker;

    ControlTooltip play;
    ControlTooltip mute;
    public ControlTooltip settings;
    public ControlTooltip fullScreen;
    public ControlTooltip captions;
    ControlTooltip nextVideoTooltip;
    ControlTooltip previousVideoTooltip;
    public ControlTooltip miniplayer;

    MediaInterface mediaInterface;

    ScaleTransition fullScreenButtonScaleTransition;

    PauseTransition seekTimer = new PauseTransition(Duration.millis(50));


    StackPane controlBarBackground = new StackPane();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Platform.runLater(() -> {
            mute = new ControlTooltip("Mute (m)", volumeButton, controlBarWrapper, 0, false);
            settings = new ControlTooltip("Settings (s)", settingsButton, controlBarWrapper, 0, false);
            fullScreen = new ControlTooltip("Full screen (f)", fullScreenButton, controlBarWrapper, 0, false);
            captions = new ControlTooltip("Subtitles/CC not selected", captionsButton, controlBarWrapper, 0, false);
            miniplayer = new ControlTooltip("Miniplayer (i)", miniplayerButton, controlBarWrapper, 0, false);
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

        miniplayerSVG = new SVGPath();
        miniplayerSVG.setContent(App.svgMap.get(SVG.MINIPLAYER));

        volumeSliderPane.setClip(new Rectangle(68, 30));

        volumeSlider.setTranslateX(-60);
        volumeTrack.setTranslateX(-60);

        durationLabel.setTranslateX(-60);


        durationPane.setMouseTransparent(true);

        durationLabel.setOnMouseClicked((e) -> toggleDurationLabel());


        controlBarBackground.setStyle("-fx-background-color: linear-gradient(to top, rgba(0,0,0,0.8), rgba(0,0,0,0));");
        controlBarBackground.setMouseTransparent(true);
        controlBarBackground.setPrefHeight(200);
        controlBarBackground.setMaxHeight(200);
        StackPane.setAlignment(controlBarBackground, Pos.BOTTOM_CENTER);

        previousVideoIcon.setShape(previousVideoSVG);
        playIcon.setShape(playSVG);
        nextVideoIcon.setShape(nextVideoSVG);
        volumeIcon.setShape(lowVolumeSVG);

        volumeIcon.setPrefSize(15, 18);
        volumeIcon.setTranslateX(-3);

        captionsIcon.setShape(captionsSVG);
        settingsIcon.setShape(settingsSVG);

        miniplayerIcon.setShape(miniplayerSVG);
        fullScreenIcon.setShape(maximizeSVG);

        captionsIcon.getStyleClass().add("controlIconDisabled");


        playButton.setBackground(Background.EMPTY);
        nextVideoButton.setBackground(Background.EMPTY);
        miniplayerButton.setBackground(Background.EMPTY);
        fullScreenButton.setBackground(Background.EMPTY);
        settingsButton.setBackground(Background.EMPTY);
        volumeButton.setBackground(Background.EMPTY);
        captionsButton.setBackground(Background.EMPTY);

        previousVideoButton.setOnAction(e -> previousVideoButtonClick());
        playButton.setOnAction((e) -> playButtonClick());
        nextVideoButton.setOnAction(e -> nextVideoButtonClick());
        volumeButton.setOnAction(e -> volumeButtonClick());
        captionsButton.setOnAction(e -> captionsButtonClick());
        settingsButton.setOnAction(e -> settingsButtonClick());
        miniplayerButton.setOnAction(e -> miniplayerButtonClick());
        fullScreenButton.setOnAction(e -> fullScreenButtonClick());


        volumeSlider.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> volumeSlider.setValueChanging(true));
        volumeSlider.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> volumeSlider.setValueChanging(false));


        previousVideoPane.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> previousVideoButtonHoverOn());
        previousVideoPane.addEventHandler(MouseEvent.MOUSE_EXITED, e -> previousVideoButtonHoverOff());

        playButtonPane.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> playButtonHoverOn());
        playButtonPane.addEventHandler(MouseEvent.MOUSE_EXITED, e -> playButtonHoverOff());

        nextVideoPane.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> nextVideoButtonHoverOn());
        nextVideoPane.addEventHandler(MouseEvent.MOUSE_EXITED, e -> nextVideoButtonHoverOff());

        volumeButtonPane.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> controlButtonHoverOn(volumeButtonPane));
        volumeButtonPane.addEventHandler(MouseEvent.MOUSE_EXITED, e -> controlButtonHoverOff(volumeButtonPane));

        captionsButtonPane.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> controlButtonHoverOn(captionsButtonPane));
        captionsButtonPane.addEventHandler(MouseEvent.MOUSE_EXITED, e -> controlButtonHoverOff(captionsButtonPane));

        settingsButtonPane.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> controlButtonHoverOn(settingsButtonPane));
        settingsButtonPane.addEventHandler(MouseEvent.MOUSE_EXITED, e -> controlButtonHoverOff(settingsButtonPane));

        miniplayerButtonPane.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> controlButtonHoverOn(miniplayerButtonPane));
        miniplayerButtonPane.addEventHandler(MouseEvent.MOUSE_EXITED, e -> controlButtonHoverOff(miniplayerButtonPane));

        fullScreenButtonPane.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> controlButtonHoverOn(fullScreenButtonPane));
        fullScreenButtonPane.addEventHandler(MouseEvent.MOUSE_EXITED, e -> controlButtonHoverOff(fullScreenButtonPane));

        Platform.runLater(() -> {
            disablePreviousVideoButton();
            disablePlayButton();
            disableNextVideoButton();
        });


        volumeSlider.valueChangingProperty().addListener((observable, oldValue, newValue) -> {

            if (!newValue) {
                if(settingsController.settingsState != SettingsState.CLOSED) settingsController.closeSettings();

                if(isExited) volumeSliderExit();
            }
        });


        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {

            mediaInterface.changeVolume(newValue.doubleValue());
            volumeTrack.setProgress(volumeSlider.getValue() / 100);

            if (newValue.doubleValue() == 0) {
                volumeIcon.setShape(volumeMutedSVG);
                volumeIcon.setPrefSize(20, 20);
                volumeIcon.setTranslateX(0);
                muted = true;
                mute.updateText("Unmute (m)");
            }
            else if (newValue.doubleValue() < 50) {
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

        durationSlider.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            if (settingsController.settingsState != SettingsState.CLOSED) {
                settingsController.closeSettings();
            }
        });


        // this part has to be run later because the slider thumb loads in later than the slider itself
        Platform.runLater(() -> {

            durationSlider.lookup(".thumb").setScaleX(0);
            durationSlider.lookup(".thumb").setScaleY(0);

            durationSlider.lookup(".thumb").setMouseTransparent(true);

            durationSlider.lookup(".track").setCursor(Cursor.HAND);

            durationSlider.lookup(".track").addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
                if(e.getButton() == MouseButton.PRIMARY) durationSlider.setValueChanging(true);
            });
            durationSlider.lookup(".track").addEventFilter(MouseEvent.MOUSE_RELEASED, e -> {
                if(e.getButton() == MouseButton.PRIMARY) durationSlider.setValueChanging(false);
            });

            durationSlider.lookup(".track").setOnMouseMoved(e -> {


                double minTranslation = (mainController.sliderHoverLabel.label.localToScene(mainController.sliderHoverLabel.label.getBoundsInLocal()).getMinX() - mainController.sliderHoverLabel.label.getTranslateX() - durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMinX()) * -1;
                double maxTranslation = durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMaxX() - mainController.sliderHoverLabel.label.localToScene(mainController.sliderHoverLabel.label.getBoundsInLocal()).getMaxX() + mainController.sliderHoverLabel.label.getTranslateX();

                double newTranslation = Math.max(minTranslation, Math.min(maxTranslation, e.getSceneX() - (mainController.sliderHoverLabel.label.localToScene(mainController.sliderHoverLabel.label.getBoundsInLocal()).getMinX() + mainController.sliderHoverLabel.label.getBoundsInLocal().getMaxX()/2) + mainController.sliderHoverLabel.label.getTranslateX() - 4));


                mainController.sliderHoverLabel.label.setTranslateX(newTranslation);

                String newTime = Utilities.getTime(Duration.seconds((e.getX())/(durationSlider.lookup(".track").getBoundsInLocal().getMaxX()) * durationSlider.getMax()));
                mainController.sliderHoverLabel.label.setText(newTime);
            });

            durationSlider.lookup(".track").setOnMouseEntered((e) -> {
                durationSliderHover = true;
                durationSliderHoverOn();


                double minTranslation = (mainController.sliderHoverLabel.label.localToScene(mainController.sliderHoverLabel.label.getBoundsInLocal()).getMinX() - mainController.sliderHoverLabel.label.getTranslateX() - durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMinX()) * -1;
                double maxTranslation = durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMaxX() - mainController.sliderHoverLabel.label.localToScene(mainController.sliderHoverLabel.label.getBoundsInLocal()).getMaxX() + mainController.sliderHoverLabel.label.getTranslateX();

                double newTranslation = Math.max(minTranslation, Math.min(maxTranslation, e.getSceneX() - (mainController.sliderHoverLabel.label.localToScene(mainController.sliderHoverLabel.label.getBoundsInLocal()).getMinX() + mainController.sliderHoverLabel.label.getBoundsInLocal().getMaxX()/2) + mainController.sliderHoverLabel.label.getTranslateX() - 4));


                mainController.sliderHoverLabel.label.setTranslateX(newTranslation);
                String newTime = Utilities.getTime(Duration.seconds(e.getX()/(durationSlider.lookup(".track").getBoundsInLocal().getMaxX()) * durationSlider.getMax()));
                mainController.sliderHoverLabel.label.setText(newTime);

                if(settingsController.settingsState == SettingsState.CLOSED) mainController.sliderHoverLabel.label.setVisible(true);
            });

            durationSlider.lookup(".track").setOnMouseExited((e) -> {
                durationSliderHover = false;

                if (!e.isPrimaryButtonDown()) {
                    durationSliderHoverOff();
                    mainController.sliderHoverLabel.label.setVisible(false);
                }
            });

            durationSlider.lookup(".track").addEventFilter(MouseEvent.MOUSE_DRAGGED, e -> {
                if(!e.isPrimaryButtonDown()){

                    double minTranslation = (mainController.sliderHoverLabel.label.localToScene(mainController.sliderHoverLabel.label.getBoundsInLocal()).getMinX() - mainController.sliderHoverLabel.label.getTranslateX() - durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMinX()) * -1;
                    double maxTranslation = durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMaxX() - mainController.sliderHoverLabel.label.localToScene(mainController.sliderHoverLabel.label.getBoundsInLocal()).getMaxX() + mainController.sliderHoverLabel.label.getTranslateX();

                    double newTranslation = Math.max(minTranslation, Math.min(maxTranslation, e.getSceneX() - (mainController.sliderHoverLabel.label.localToScene(mainController.sliderHoverLabel.label.getBoundsInLocal()).getMinX() + mainController.sliderHoverLabel.label.getBoundsInLocal().getMaxX()/2) + mainController.sliderHoverLabel.label.getTranslateX() - 4));


                    mainController.sliderHoverLabel.label.setTranslateX(newTranslation);
                    String newTime = Utilities.getTime(Duration.seconds(e.getX()/(durationSlider.lookup(".track").getBoundsInLocal().getMaxX()) * durationSlider.getMax()));
                    mainController.sliderHoverLabel.label.setText(newTime);


                    e.consume();
                }
            });


        });


        durationSlider.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
            if(e.getButton() != MouseButton.PRIMARY) e.consume();
        });

        durationSlider.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> {
            if(e.getButton() != MouseButton.PRIMARY) e.consume();
        });



        durationSlider.valueProperty().addListener((observable, oldValue, newValue) -> {


            if(mediaInterface.mediaActive.get()){


                if(oldValue.doubleValue() <= 5 && newValue.doubleValue() > 5){

                    previousVideoButton.setOnAction((e) -> mediaInterface.replay());


                    if(!previousVideoButtonEnabled) enablePreviousVideoButton();
                    else {
                        previousVideoTooltip.updateText("Replay");
                        if(mainController.miniplayerActive) mainController.miniplayer.miniplayerController.previousVideoButtonTooltip.updateText("Replay");
                    }
                }
                else if(oldValue.doubleValue() > 5 && newValue.doubleValue() <= 5){

                    previousVideoButton.setOnAction((e) -> previousVideoButtonClick());


                    if(menuController.history.isEmpty() || menuController.historyBox.index == 0){
                        if(previousVideoButtonEnabled) disablePreviousVideoButton();
                    }
                    else {
                        if(mainController.miniplayerActive) mainController.miniplayer.miniplayerController.previousVideoButtonTooltip.updateText("Previous video (SHIFT + N)");
                        previousVideoTooltip.updateText("Previous video (SHIFT + N)");
                    }

                }


                mediaInterface.updateMedia(newValue.doubleValue());

                if(durationSlider.isValueChanging() && !mainController.seekingWithKeys){
                    double minTranslation = (mainController.sliderHoverLabel.label.localToScene(mainController.sliderHoverLabel.label.getBoundsInLocal()).getMinX() - mainController.sliderHoverLabel.label.getTranslateX() - durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMinX()) * -1;
                    double maxTranslation = durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMaxX() - mainController.sliderHoverLabel.label.localToScene(mainController.sliderHoverLabel.label.getBoundsInLocal()).getMaxX() + mainController.sliderHoverLabel.label.getTranslateX();


                    double newTranslation = Math.max(minTranslation, Math.min(maxTranslation, durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMinX() + durationSlider.lookup(".track").getBoundsInLocal().getMaxX() * (newValue.doubleValue() / durationSlider.getMax()) - (mainController.sliderHoverLabel.label.localToScene(mainController.sliderHoverLabel.label.getBoundsInLocal()).getMinX() + mainController.sliderHoverLabel.label.getBoundsInLocal().getMaxX()/2) + mainController.sliderHoverLabel.label.getTranslateX() - 4));


                    mainController.sliderHoverLabel.label.setTranslateX(newTranslation);

                    mainController.sliderHoverLabel.label.setText(Utilities.getTime(Duration.seconds(durationSlider.getValue())));
                }
            }
            durationTrack.setProgress(durationSlider.getValue() / durationSlider.getMax());

            captionsController.updateCaptions(newValue.doubleValue() * 1000 + 1000);


            if(mainController.miniplayerActive){
                mainController.miniplayer.miniplayerController.progressBar.setProgress(durationSlider.getValue() / durationSlider.getMax());
                if(mainController.miniplayer.miniplayerController.slider.getValue() != newValue.doubleValue()){
                    mainController.miniplayer.miniplayerController.slider.setValue(newValue.doubleValue());
                }
            }
        });

        durationSlider.valueChangingProperty().addListener((observable, oldValue, newValue) -> {


            if (newValue) { // pause video when user starts seeking
                seekTimer.playFromStart();
                if(mediaInterface.playing.get()) mediaInterface.embeddedMediaPlayer.controls().pause();
                mediaInterface.playing.set(false);

                double minTranslation = (mainController.sliderHoverLabel.label.localToScene(mainController.sliderHoverLabel.label.getBoundsInLocal()).getMinX() - mainController.sliderHoverLabel.label.getTranslateX() - durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMinX()) * -1;
                double maxTranslation = durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMaxX() - mainController.sliderHoverLabel.label.localToScene(mainController.sliderHoverLabel.label.getBoundsInLocal()).getMaxX() + mainController.sliderHoverLabel.label.getTranslateX();


                double newTranslation = Math.max(minTranslation, Math.min(maxTranslation, durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMinX() + durationSlider.lookup(".track").getBoundsInLocal().getMaxX() * (durationSlider.getValue() / durationSlider.getMax()) - (mainController.sliderHoverLabel.label.localToScene(mainController.sliderHoverLabel.label.getBoundsInLocal()).getMinX() + mainController.sliderHoverLabel.label.getBoundsInLocal().getMaxX()/2) + mainController.sliderHoverLabel.label.getTranslateX() - 4));


                mainController.sliderHoverLabel.label.setTranslateX(newTranslation);

                mainController.sliderHoverLabel.label.setText(Utilities.getTime(Duration.seconds(durationSlider.getValue())));

            }
            else {

                if(!durationSliderHover && settingsController.settingsState == SettingsState.CLOSED) mainController.sliderHoverLabel.label.setVisible(false);

                if(seekTimer.getStatus() == Animation.Status.RUNNING) seekTimer.stop();
                if(mainController.miniplayerActive && mainController.miniplayer.miniplayerController.seekTimer.getStatus() == Animation.Status.RUNNING) mainController.miniplayer.miniplayerController.seekTimer.stop();

                if (!durationSliderHover) {
                    durationSliderHoverOff();
                }


                if (settingsController.settingsState != SettingsState.CLOSED) { // close settings pane after user finishes seeking media (if its open)
                    settingsController.closeSettings();
                }

                if (mediaInterface.atEnd) {
                    mediaInterface.endMedia();
                }
                else if (mediaInterface.wasPlaying) { // starts playing the video in the new position when user finishes seeking with the slider
                    mediaInterface.play();
                    mediaInterface.seek(Duration.seconds(durationSlider.getValue())); // seeks to exact position when user finishes dragging
                }
            }
        });


        seekTimer.setOnFinished(e -> mediaInterface.pause());

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
        if (showingTimeLeft && mediaInterface.mediaActive.get()) {
            Utilities.setCurrentTimeLabel(durationLabel, durationSlider, Duration.millis(mediaInterface.embeddedMediaPlayer.media().info().duration()));
            showingTimeLeft = false;
        } else if(!showingTimeLeft && mediaInterface.mediaActive.get()){
            Utilities.setTimeLeftLabel(durationLabel, durationSlider, Duration.millis(mediaInterface.embeddedMediaPlayer.media().info().duration()));
            showingTimeLeft = true;
        }
    }


    public void playButtonClick() {
        if (settingsController.settingsState != SettingsState.CLOSED) {
            settingsController.closeSettings();
        } else {

            if(mediaInterface.atEnd) mediaInterface.replay();
            else if(mediaInterface.playing.get()){
                mediaInterface.wasPlaying = false;
                mediaInterface.pause();
            }
            else mediaInterface.play();

        }
    }



    public void play() {

        playIcon.setShape(pauseSVG);
        playIcon.setPrefSize(20, 20);
        if(play != null) play.updateText("Pause (k)");
    }

    public void pause() {

        playIcon.setShape(playSVG);
        playIcon.setPrefSize(20, 20);

        if(play != null) play.updateText("Play (k)");
    }

    public void end(){
        playIcon.setShape(replaySVG);
        playIcon.setPrefSize(24, 24);

        if(play != null) play.updateText("Replay (k)");
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

    public void toggleFullScreen() {

        captionsController.cancelDrag();
        App.stage.setFullScreen(!App.stage.isFullScreen());

        if (App.stage.isFullScreen()) {
            fullScreenIcon.setShape(minimizeSVG);
            App.fullScreen = true;

            if (settingsController.settingsState == SettingsState.CLOSED) {
                if (fullScreen.isShowing()) {
                    fullScreen.hide();
                    fullScreen = new ControlTooltip("Exit full screen (f)", fullScreenButton, controlBarWrapper, 0, false);
                    fullScreen.showTooltip();
                } else {
                    fullScreen = new ControlTooltip("Exit full screen (f)", fullScreenButton, controlBarWrapper, 0, false);
                }
            }
        } else {
            fullScreenIcon.setShape(maximizeSVG);
            App.fullScreen = false;

            if (settingsController.settingsState == SettingsState.CLOSED) {
                if (fullScreen.isShowing()) {
                    fullScreen.hide();
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

    public void miniplayerButtonHoverOn(){
        miniplayerButtonHover = true;
    }

    public void miniplayerButtonHoverOff(){
        miniplayerButtonHover = false;
    }


    public void volumeButtonClick() {
        if (settingsController.settingsState != SettingsState.CLOSED) {
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


    public void previousVideoButtonClick(){
        if (settingsController.settingsState != SettingsState.CLOSED) {
            settingsController.closeSettings();
        } else {
            if(!menuController.animationsInProgress.isEmpty()) return;
            mediaInterface.playPrevious(); // reset styling of current active history item, decrement historyposition etc
        }
    }

    public void nextVideoButtonClick() {
        if (settingsController.settingsState != SettingsState.CLOSED) {
            settingsController.closeSettings();
        } else {
            if(!menuController.animationsInProgress.isEmpty()) return;
            mediaInterface.playNext();
        }
    }


    public void openCaptions() {

        if(!captionsController.captionsSelected) return;

        captionsController.captionsOn.set(true);

        AnimationsClass.scaleAnimation(100, captionsButtonLine, 0, 1, 1, 1, false, 1, true);
    }

    public void closeCaptions() {
        captionsController.captionsOn.set(false);

        AnimationsClass.scaleAnimation(100, captionsButtonLine, 1, 0, 1, 1, false, 1, true);
    }

    public void settingsButtonClick() {
        if (settingsController.settingsState != SettingsState.CLOSED)
            settingsController.closeSettings();
        else
            settingsController.openSettings();
    }


    public void fullScreenButtonClick() {
        if (settingsController.settingsState != SettingsState.CLOSED)
            settingsController.closeSettings();
        else
            toggleFullScreen();
    }

    public void miniplayerButtonClick(){

        if(settingsController.settingsState != SettingsState.CLOSED){
            settingsController.closeSettings();
            return;
        }

        if(mainController.miniplayerActive) mainController.closeMiniplayer();
        else mainController.openMiniplayer();
    }


    public void controlBarClick() {
        if (settingsController.settingsState != SettingsState.CLOSED) {
            settingsController.closeSettings();
        }
    }

    public void captionsButtonClick() {

        if(settingsController.settingsState != SettingsState.CLOSED) {
            settingsController.closeSettings();
            return;
        }

        if(!captionsController.captionsSelected) return;

        if (!captionsController.captionsOn.get())
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

        if(!stackPane.equals(captionsButtonPane) || captionsController.captionsSelected) AnimationsClass.AnimateBackgroundColor(icon, Color.rgb(200, 200, 200), Color.rgb(255, 255, 255), 200);
        else AnimationsClass.AnimateBackgroundColor(icon, Color.rgb(120, 120, 120), Color.rgb(150, 150, 150), 200);
    }


    public void controlButtonHoverOff(StackPane stackPane){
        Region icon = (Region) stackPane.getChildren().get(1);

        if(!stackPane.equals(captionsButtonPane) || captionsController.captionsSelected) AnimationsClass.AnimateBackgroundColor(icon, Color.rgb(255, 255, 255), Color.rgb(200, 200, 200), 200);
        else AnimationsClass.AnimateBackgroundColor(icon, Color.rgb(150, 150, 150), Color.rgb(120, 120, 120), 200);
    }

    public void previousVideoButtonHoverOn(){
        previousVideoButtonHover = true;

        if(previousVideoButtonEnabled){
            AnimationsClass.AnimateBackgroundColor(previousVideoIcon, Color.rgb(200, 200, 200), Color.rgb(255, 255, 255), 200);
        }
        else {
            AnimationsClass.AnimateBackgroundColor(previousVideoIcon, Color.rgb(120, 120, 120), Color.rgb(150, 150, 150), 200);
        }
    }

    public void previousVideoButtonHoverOff(){
        previousVideoButtonHover = false;

        if(previousVideoButtonEnabled){
            AnimationsClass.AnimateBackgroundColor(previousVideoIcon, Color.rgb(255, 255, 255), Color.rgb(200, 200, 200), 200);
        }
        else {
            AnimationsClass.AnimateBackgroundColor(previousVideoIcon, Color.rgb(150, 150, 150), Color.rgb(120, 120, 120), 200);
        }
    }

    public void playButtonHoverOn(){
        playButtonHover = true;

        if(playButtonEnabled){
            AnimationsClass.AnimateBackgroundColor(playIcon, Color.rgb(200, 200, 200), Color.rgb(255, 255, 255), 200);
        }
        else {
            AnimationsClass.AnimateBackgroundColor(playIcon, Color.rgb(120, 120, 120), Color.rgb(150, 150, 150), 200);
        }
    }

    public void playButtonHoverOff(){
        playButtonHover = false;

        if(playButtonEnabled){
            AnimationsClass.AnimateBackgroundColor(playIcon, Color.rgb(255, 255, 255), Color.rgb(200, 200, 200), 200);
        }
        else {
            AnimationsClass.AnimateBackgroundColor(playIcon, Color.rgb(150, 150, 150), Color.rgb(120, 120, 120), 200);
        }
    }

    public void nextVideoButtonHoverOn(){
        nextVideoButtonHover = true;

        if(nextVideoButtonEnabled){
            AnimationsClass.AnimateBackgroundColor(nextVideoIcon, Color.rgb(200, 200, 200), Color.rgb(255, 255, 255), 200);
        }
        else {
            AnimationsClass.AnimateBackgroundColor(nextVideoIcon, Color.rgb(120, 120, 120), Color.rgb(150, 150, 150), 200);

        }
    }

    public void nextVideoButtonHoverOff(){
        nextVideoButtonHover = false;

        if(nextVideoButtonEnabled){
            AnimationsClass.AnimateBackgroundColor(nextVideoIcon, Color.rgb(255, 255, 255), Color.rgb(200, 200, 200), 200);
        }
        else {
            AnimationsClass.AnimateBackgroundColor(nextVideoIcon, Color.rgb(150, 150, 150), Color.rgb(120, 120, 120), 200);
        }
    }

    public void enablePreviousVideoButton(){
        previousVideoButtonEnabled = true;

        if(previousVideoButtonHover){
            previousVideoIcon.setStyle("-fx-background-color: rgb(255, 255, 255);");
        }
        else {
            previousVideoIcon.setStyle("-fx-background-color: rgb(200, 200, 200);");
        }

        if(mainController.miniplayerActive) mainController.miniplayer.miniplayerController.enablePreviousVideoButton();

        Platform.runLater(() -> {
            if(durationSlider.getValue() > 5) previousVideoTooltip = new ControlTooltip("Replay", previousVideoButton, controlBarWrapper, 0, false);
            else previousVideoTooltip = new ControlTooltip("Previous video (SHIFT + P", previousVideoButton, controlBarWrapper, 0, false);

            if(previousVideoButtonHover) previousVideoTooltip.showTooltip();
        });
    }

    public void disablePreviousVideoButton(){
        previousVideoButtonEnabled = false;

        if(previousVideoButtonHover){
            previousVideoIcon.setStyle("-fx-background-color: rgb(150, 150, 150);");
        }
        else {
            previousVideoIcon.setStyle("-fx-background-color: rgb(120, 120, 120);");
        }

        if(mainController.miniplayerActive) mainController.miniplayer.miniplayerController.disablePreviousVideoButton();


        previousVideoButton.setOnMouseEntered(null);
        if(previousVideoButtonHover && previousVideoTooltip != null) previousVideoTooltip.hide();
    }

    public void enablePlayButton(){
        playButtonEnabled = true;

        if(playButtonHover){
            playIcon.setStyle("-fx-background-color: rgb(255, 255, 255);");
        }
        else {
            playIcon.setStyle("-fx-background-color: rgb(200, 200, 200);");
        }


        Platform.runLater(() -> {
            if(mediaInterface.atEnd) play = new ControlTooltip("Replay (k)", playButton, controlBarWrapper, 0 , false);
            else if(mediaInterface.playing.get()) play = new ControlTooltip("Pause (k)", playButton, controlBarWrapper, 0, false);
            else play = new ControlTooltip("Play (k)", playButton, controlBarWrapper, 0, false);

            if(playButtonHover) play.showTooltip();
        });
    }

    public void disablePlayButton(){
        playButtonEnabled = false;

        if(playButtonHover){
            playIcon.setStyle("-fx-background-color: rgb(150, 150, 150);");
        }
        else {
            playIcon.setStyle("-fx-background-color: rgb(120, 120, 120);");
        }


        playButton.setOnMouseEntered(null);
        if(playButtonHover && play != null) play.hide();
    }

    public void enableNextVideoButton(){
        nextVideoButtonEnabled = true;

        if(nextVideoButtonHover){
            nextVideoIcon.setStyle("-fx-background-color: rgb(255, 255, 255);");
        }
        else {
            nextVideoIcon.setStyle("-fx-background-color: rgb(200, 200, 200);");
        }

        if(mainController.miniplayerActive) mainController.miniplayer.miniplayerController.enableNextVideoButton();

        Platform.runLater(() -> {
            nextVideoTooltip = new ControlTooltip("Next video (SHIFT + N)", nextVideoButton, controlBarWrapper, 0, false);

            if(nextVideoButtonHover) nextVideoTooltip.showTooltip();
        });
    }

    public void disableNextVideoButton(){
        nextVideoButtonEnabled = false;

        if(nextVideoButtonHover){
            nextVideoIcon.setStyle("-fx-background-color: rgb(150, 150, 150);");
        }
        else {
            nextVideoIcon.setStyle("-fx-background-color: rgb(120, 120, 120);");
        }

        if(mainController.miniplayerActive) mainController.miniplayer.miniplayerController.disableNextVideoButton();

        nextVideoButton.setOnMouseEntered(null);
        if(nextVideoButtonHover && nextVideoTooltip != null) nextVideoTooltip.hide();
    }





}
