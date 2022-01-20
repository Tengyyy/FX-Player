package hans;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;


import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.layout.Background;

import javafx.scene.layout.StackPane;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.media.SubtitleTrack;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

public class MainController implements Initializable {

    @FXML
    public MediaView mediaView;

    @FXML
    Button menuButton;


    @FXML
    ImageView menuIcon;

    @FXML
    StackPane pane;

    @FXML
    private ControlBarController controlBarController;

    @FXML
    private SettingsController settingsController;

    MenuController menuController;


    // custom playback speed selection box that will be created if the user selects a custom speed using the slider

    private File file;
    public Media media;
    public MediaPlayer mediaPlayer;


    // Variables to keep track of mediaplayer status:
    boolean playing = false; // is mediaplayer currently playing
    boolean wasPlaying = false; // was mediaplayer playing before a seeking action occurred
    public boolean atEnd = false; // is mediaplayer at the end of the video
    public boolean seekedToEnd = false; // true = video was seeked to the end; false = video naturally reached the end or the video is still playing
    ////////////////////////////////////////////////

    DoubleProperty mediaViewWidth;
    DoubleProperty mediaViewHeight;

    Image menuImage;

    Image menuCloseImage;

    private File menuFile, menuCloseFile;

    boolean running = false; // media running status


    boolean captionsOpen = false;

    boolean menuOpen = false;


    // counter to keep track of the current node that has focus (used for focus traversing with tab and shift+tab)
    public int focusNodeTracker = 0;

    SubtitleTrack subtitles;

    ControlTooltip menuTooltip;

    Stage menuStage = new Stage();


    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

        controlBarController.init(this, settingsController); // shares references of all the controllers between eachother
        settingsController.init(this, controlBarController);

        file = new File("src/main/resources/hans/hey.mp4");

        // declaring media control images
        menuFile = new File("src/main/resources/hans/images/menuFile.png");
        menuCloseFile = new File("src/main/resources/hans/images/menuCloseFile.png");

        menuImage = new Image(menuFile.toURI().toString());
        menuCloseImage = new Image(menuCloseFile.toURI().toString());

        // Make mediaView adjust to frame size
        mediaViewWidth = mediaView.fitWidthProperty();
        mediaViewHeight = mediaView.fitHeightProperty();
        mediaViewWidth.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
        mediaViewHeight.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));
        mediaView.setPreserveRatio(true);


        pane.setStyle("-fx-background-color: rgb(0,0,0)");


        menuButton.setBackground(Background.EMPTY);
        menuButton.setVisible(false);


        menuIcon.setImage(menuImage);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                // needs to be run later so that the rest of the app can load in and this tooltip popup has a parent window to be associated with
                menuTooltip = new ControlTooltip("Open menu (q)", menuButton, true, controlBarController.controlBar);
            }
        });

        mediaView.focusedProperty()
                .addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                    if (!newValue) {
                        mediaView.setStyle("-fx-border-color: transparent;");
                    } else {
                        focusNodeTracker = 0;
                    }
                });

        menuButton.focusedProperty()
                .addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                    if (!newValue) {
                        menuButton.setStyle("-fx-border-color: transparent;");
                    } else {
                        focusNodeTracker = 8;
                    }
                });

        createMediaPlayer(file);

    }

    public void mediaClick() {

        // Clicking on the mediaview node will close the settings tab if its open or
        // otherwise play/pause/replay the video

        if (settingsController.settingsOpen) {
            settingsController.closeSettings();
        } else {
            if (atEnd) {
                controlBarController.replayMedia();
            } else {
                if (playing) {
                    controlBarController.pause();
                } else {
                    controlBarController.play();
                }
            }
        }

        mediaView.requestFocus();
    }


    public void traverseFocusForwards() {

        switch (focusNodeTracker) {

            // mediaView
            case 0: {

                mediaView.setStyle("-fx-border-color: blue;");
            }
            break;

            // durationSlider
            case 1: {
                mediaView.setStyle("-fx-border-color: transparent;");
                controlBarController.durationSlider.setStyle("-fx-border-color: blue;");
            }
            break;

            // playButton
            case 2: {
                controlBarController.durationSlider.setStyle("-fx-border-color: transparent;");
                controlBarController.playButton.setStyle("-fx-border-color: blue;");
            }
            break;

            // nextVideoButton
            case 3: {
                controlBarController.playButton.setStyle("-fx-border-color: transparent;");
                controlBarController.nextVideoButton.setStyle("-fx-border-color: blue;");
            }
            break;

            // muteButton
            case 4: {
                controlBarController.nextVideoButton.setStyle("-fx-border-color: transparent;");
                controlBarController.volumeButton.setStyle("-fx-border-color: blue;");
            }
            break;

            // volumeSlider
            case 5: {
                controlBarController.volumeButton.setStyle("-fx-border-color: transparent;");
                controlBarController.volumeSlider.setStyle("-fx-border-color: blue;");
            }
            break;

            // settingsButton
            case 6: {
                controlBarController.volumeSlider.setStyle("-fx-border-color: transparent;");
                controlBarController.settingsButton.setStyle("-fx-border-color: blue;");
            }
            break;

            // fullscreenButton
            case 7: {
                controlBarController.settingsButton.setStyle("-fx-border-color: transparent;");
                controlBarController.fullScreenButton.setStyle("-fx-border-color: blue;");
            }
            break;

            // menuButton
            case 8: {
                controlBarController.fullScreenButton.setStyle("-fx-border-color: transparent;");
            }
            break;

            default:
                break;

        }

    }

    public void traverseFocusBackwards() {

        switch (focusNodeTracker) {

            // mediaView
            case 0: {
                controlBarController.durationSlider.setStyle("-fx-border-color: transparent;");
                mediaView.setStyle("-fx-border-color: blue;");
            }
            break;

            // durationSlider
            case 1: {
                controlBarController.playButton.setStyle("-fx-border-color: transparent;");
                controlBarController.durationSlider.setStyle("-fx-border-color: blue;");
            }
            break;

            // playButton
            case 2: {
                controlBarController.nextVideoButton.setStyle("-fx-border-color: transparent;");
                controlBarController.playButton.setStyle("-fx-border-color: blue;");
            }
            break;

            // nextVideoButton
            case 3: {
                controlBarController.volumeButton.setStyle("-fx-border-color: transparent;");
                controlBarController.nextVideoButton.setStyle("-fx-border-color: blue;");
            }
            break;

            // muteButton
            case 4: {
                controlBarController.volumeSlider.setStyle("-fx-border-color: transparent;");
                controlBarController.volumeButton.setStyle("-fx-border-color: blue;");
            }
            break;

            // volumeSlider
            case 5: {
                controlBarController.settingsButton.setStyle("-fx-border-color: transparent;");
                controlBarController.volumeSlider.setStyle("-fx-border-color: blue;");
            }
            break;

            // settingsButton
            case 6: {
                controlBarController.fullScreenButton.setStyle("-fx-border-color: transparent;");
                controlBarController.settingsButton.setStyle("-fx-border-color: blue;");
            }
            break;

            // fullscreenButton
            case 7: {
                controlBarController.fullScreenButton.setStyle("-fx-border-color: blue;");
            }
            break;

            // menuButton
            case 8: {
                mediaView.setStyle("-fx-border-color: transparent;");
            }
            break;

            default:
                break;

        }

    }

    public void updateMedia(double newValue) {
        if (!controlBarController.showingTimeLeft)
            Utilities.setCurrentTimeLabel(controlBarController.durationLabel, mediaPlayer, media);
        else
            Utilities.setTimeLeftLabel(controlBarController.durationLabel, mediaPlayer, media);

        if (atEnd) {
            atEnd = false;
            seekedToEnd = false;

            if (wasPlaying) {
                if (!controlBarController.durationSlider.isValueChanging()) {
                    controlBarController.playLogo.setImage(controlBarController.pauseImage);

                    playing = true;
                    mediaPlayer.play();

                    if (controlBarController.play.isShowing() || controlBarController.replay.isShowing()) {
                        controlBarController.play.hide();
                        controlBarController.replay.hide();
                        controlBarController.pause = new ControlTooltip("Pause (k)", controlBarController.playButton, false, controlBarController.controlBar);
                        controlBarController.pause.showTooltip();
                    } else {
                        controlBarController.pause = new ControlTooltip("Pause (k)", controlBarController.playButton, false, controlBarController.controlBar);
                    }
                }
            } else {
                controlBarController.playLogo.setImage(controlBarController.playImage);
                playing = false;

                if (controlBarController.pause.isShowing() || controlBarController.replay.isShowing()) {
                    controlBarController.pause.hide();
                    controlBarController.replay.hide();
                    controlBarController.play = new ControlTooltip("Play (k)", controlBarController.playButton, false, controlBarController.controlBar);
                    controlBarController.play.showTooltip();
                } else {
                    controlBarController.play = new ControlTooltip("Play (k)", controlBarController.playButton, false, controlBarController.controlBar);
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

        controlBarController.durationTrack.setProgress(controlBarController.durationSlider.getValue() / controlBarController.durationSlider.getMax());


    }

    public void endMedia() {

        if ((!settingsController.shuffleOn && !settingsController.loopOn && !settingsController.autoplayOn) || (settingsController.loopOn && seekedToEnd)) {
            controlBarController.durationSlider.setValue(controlBarController.durationSlider.getMax());

            controlBarController.durationLabel.textProperty().unbind();
            controlBarController.durationLabel.setText(Utilities.getTime(new Duration(controlBarController.durationSlider.getMax() * 1000)) + "/" + Utilities.getTime(media.getDuration()));


            controlBarController.playLogo.setImage(new Image(controlBarController.replayFile.toURI().toString()));

            if (controlBarController.play.isShowing() || controlBarController.pause.isShowing()) {
                controlBarController.play.hide();
                controlBarController.pause.hide();
                controlBarController.replay = new ControlTooltip("Replay (k)", controlBarController.playButton, false, controlBarController.controlBar);
                controlBarController.replay.showTooltip();
            } else {
                controlBarController.replay = new ControlTooltip("Replay (k)", controlBarController.playButton, false, controlBarController.controlBar);
            }

            controlBarController.playButton.setOnAction((e) -> controlBarController.playButtonClick2());

            if (!controlBarController.controlBarOpen) {
                controlBarController.displayControls();
            }


        } else if (settingsController.loopOn && !seekedToEnd) {
            // restart current video


            mediaPlayer.stop();

        } else if (settingsController.shuffleOn) {

            //if(!controlBarController.controlBarOpen) {
            //	controlBarController.displayControls();
            //}

        } else if (settingsController.autoplayOn) {
            // play next song in queue/directory

            //if(!controlBarController.controlBarOpen) {
            //	controlBarController.displayControls();
            //}
        }

    }

    public void openMenu() {
        if (!menuOpen) {
            Parent root;
            menuOpen = true;
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("views/Menu.fxml"));
                root = loader.load();

                menuController = loader.getController();

                if (menuStage == null) {
                    menuStage = new Stage();
                }

                menuStage.setTitle("Media Player Menu");
                menuStage.setX(10);
                menuStage.setY(30);

                Scene menuScene = new Scene(root, 400, 600);
                menuScene.getStylesheets().add(getClass().getResource("styles/menu.css").toExternalForm());

                menuStage.setScene(menuScene);
                menuStage.show();

                menuStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

                    @Override
                    public void handle(WindowEvent event) {
                        menuOpen = false;
                        menuStage = null;
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (!menuStage.isIconified()) { // brings the menu to the front if its behind another window
            menuStage.requestFocus();
        } else {
            menuStage.setIconified(false); // Restores the window if its iconified (hidden)
        }
    }


    public SettingsController getSettingsController() {
        return settingsController;
    }

    public ControlBarController getControlBarController() {
        return controlBarController;
    }


    public void createMediaPlayer(File file) {

        controlBarController.durationSlider.setValue(0);

        media = new Media(file.toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);

        mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observableValue, Duration oldTime, Duration newTime) {
                if (!controlBarController.showingTimeLeft)
                    Utilities.setCurrentTimeLabel(controlBarController.durationLabel, mediaPlayer, media);
                else
                    Utilities.setTimeLeftLabel(controlBarController.durationLabel, mediaPlayer, media);

                if (!controlBarController.durationSlider.isValueChanging()) {
                    controlBarController.durationSlider.setValue(newTime.toSeconds());
                }

            }
        });


        mediaPlayer.setOnReady(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub

                mediaPlayer.setVolume(controlBarController.volumeSlider.getValue() / 100);

                controlBarController.play();

                controlBarController.durationSlider.setMax(Math.floor(media.getDuration().toSeconds()));

                TimerTask setRate = new TimerTask() {

                    @Override
                    public void run() {

                        switch (settingsController.playbackSpeedTracker) {
                            case 0:
                                mediaPlayer.setRate(settingsController.formattedValue);
                                break;
                            case 1:
                                mediaPlayer.setRate(0.25);
                                break;
                            case 2:
                                mediaPlayer.setRate(0.5);
                                break;
                            case 3:
                                mediaPlayer.setRate(0.75);
                                break;
                            case 4:
                                mediaPlayer.setRate(1);
                                break;
                            case 5:
                                mediaPlayer.setRate(1.25);
                                break;
                            case 6:
                                mediaPlayer.setRate(1.5);
                                break;
                            case 7:
                                mediaPlayer.setRate(1.75);
                                break;
                            case 8:
                                mediaPlayer.setRate(2);
                                break;
                            default:
                                break;
                        }
                    }

                };

                Timer timer = new Timer();

                // this is mega stupid but it works
                timer.schedule(setRate, 200);
            }

        });

    }
}