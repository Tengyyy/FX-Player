package hans;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import java.util.Map;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;


import javafx.animation.Animation;
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

import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Background;

import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.media.SubtitleTrack;
import javafx.scene.shape.SVGPath;
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
    StackPane pane, menuButtonPane;

    @FXML
    Region menuIcon;

    @FXML
    private ControlBarController controlBarController;

    @FXML
    private SettingsController settingsController;

    MenuController menuController;

    MediaInterface mediaInterface;


    // custom playback speed selection box that will be created if the user selects a custom speed using the slider

    private File file;

    DoubleProperty mediaViewWidth;
    DoubleProperty mediaViewHeight;

    boolean running = false; // media running status


    boolean captionsOpen = false;

    boolean menuOpen = false;


    // counter to keep track of the current node that has focus (used for focus traversing with tab and shift+tab)
    public int focusNodeTracker = 0;

    SubtitleTrack subtitles;

    ControlTooltip menuTooltip;

    Stage menuStage = new Stage();

    Image addVideoImage = new Image(new File("src/main/resources/hans/images/addVideo.png").toURI().toString());
    ImageView addVideo;

    String menuPath = "M3,6H21V8H3V6M3,11H21V13H3V11M3,16H21V18H3V16Z";
    SVGPath menuSVG;


    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

        mediaInterface = new MediaInterface(this, controlBarController, settingsController);

        controlBarController.init(this, settingsController, mediaInterface); // shares references of all the controllers between eachother
        settingsController.init(this, controlBarController, mediaInterface);

        file = new File("src/main/resources/hans/hey.mp4");

        // declaring media control images
        menuSVG = new SVGPath();
        menuSVG.setContent(menuPath);

        // Make mediaView adjust to frame size
        mediaViewWidth = mediaView.fitWidthProperty();
        mediaViewHeight = mediaView.fitHeightProperty();
        mediaViewWidth.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
        mediaViewHeight.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));
        mediaView.setPreserveRatio(true);


        pane.setStyle("-fx-background-color: rgb(0,0,0)");


        menuButton.setBackground(Background.EMPTY);
        menuButton.setVisible(false);


        menuIcon.setShape(menuSVG);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                // needs to be run later so that the rest of the app can load in and this tooltip popup has a parent window to be associated with
                menuTooltip = new ControlTooltip("Open menu (q)", menuButton, true, controlBarController.controlBar, 0);
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

        // mediaInterface.createMediaPlayer(new Media(file.toURI().toString()));

    }

    public void mediaClick() {

        // Clicking on the mediaview node will close the settings tab if its open or
        // otherwise play/pause/replay the video

        if (settingsController.settingsOpen) {
            settingsController.closeSettings();
        } else {
            if (mediaInterface.atEnd) {
                controlBarController.replayMedia();
            } else {
                if (mediaInterface.playing) {
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

    public void openMenu() {


        if (!menuOpen) {
            Parent root;
            menuOpen = true;
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("views/Menu.fxml"));
                root = loader.load();

                menuController = loader.getController();

                menuController.init(this, controlBarController, settingsController, mediaInterface);

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

    public void handleDragEntered(DragEvent e){
        File file = e.getDragboard().getFiles().get(0);
        if(!Utilities.getFileExtension(file).equals("mp4") && !Utilities.getFileExtension(file).equals("mp3")) return;

        mediaView.setEffect(new GaussianBlur(30));

        if(settingsController.settingsOpen) settingsController.closeSettings();
        else if(captionsOpen) controlBarController.closeCaptions();

       if(mediaInterface.playing)controlBarController.mouseEventTracker.hide();
       else AnimationsClass.hideControls(controlBarController);

        addVideo = new ImageView(addVideoImage);
        addVideo.setFitWidth(150);
        addVideo.setFitHeight(150);
        addVideo.setEffect(new DropShadow());
        if(pane.getChildren().size() == 4){
            pane.getChildren().add(addVideo);
        }

    }

    public void handleDragExited(DragEvent e){
        mediaView.setEffect(null);

        if(pane.getChildren().size() == 5){
            pane.getChildren().remove(4);
        }
    }

    public void handleDragOver(DragEvent e){
        File file = e.getDragboard().getFiles().get(0);
        if(!Utilities.getFileExtension(file).equals("mp4") && !Utilities.getFileExtension(file).equals("mp3")) return;

        e.acceptTransferModes(TransferMode.COPY);
    }

    public void handleDragDropped(DragEvent e){
        mediaView.setEffect(null);
        File file = e.getDragboard().getFiles().get(0);

        /* return statement */
        if(!Utilities.getFileExtension(file).equals("mp4") && !Utilities.getFileExtension(file).equals("mp3")) return;


        if(pane.getChildren().size() == 5){
            pane.getChildren().remove(4);
        }

        // resets video name text in the settings tab if the animations had not finished before the user already selected a new video to play
        if(settingsController.marqueeTimeline != null && settingsController.marqueeTimeline.getStatus() == Animation.Status.RUNNING) settingsController.videoNameText.setLayoutX(0);

        mediaInterface.resetMediaPlayer();
        mediaInterface.playedVideoIndex = -1;

        MediaItem temp = null;

        if(Utilities.getFileExtension(file).equals("mp4")) temp = new Mp4Item(file);
        else if(Utilities.getFileExtension(file).equals("mp3")) temp = new Mp3Item(file);

        if(menuController != null) {
            new QueueItem(temp, menuController, mediaInterface);
        }
        mediaInterface.videoList.add(temp);
        mediaInterface.unplayedVideoList.add(temp);
        mediaInterface.createMediaPlayer(temp);

    }


    public SettingsController getSettingsController() {
        return settingsController;
    }

    public ControlBarController getControlBarController() {
        return controlBarController;
    }

    public MediaInterface getMediaInterface() {
        return mediaInterface;
    }
}