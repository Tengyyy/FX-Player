package hans;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import java.util.*;


import javafx.animation.Animation;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;


import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Background;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;


import javafx.scene.media.MediaView;
import javafx.scene.media.SubtitleTrack;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

import static hans.SVG.*;


public class MainController implements Initializable {

    @FXML
    public MediaView mediaView;

    @FXML
    Button menuButton;

    @FXML
    StackPane outerPane, menuButtonPane, mediaViewWrapper, mediaViewInnerWrapper;

    @FXML
    BorderPane mainPane;

    @FXML
    Region menuIcon;

    @FXML
    private ControlBarController controlBarController;

    @FXML
    private SettingsController settingsController;

    @FXML
    private MenuController menuController;

    MediaInterface mediaInterface;


    private File file;

    DoubleProperty mediaViewWidth;
    DoubleProperty mediaViewHeight;

    boolean running = false; // media running status


    boolean captionsOpen = false;



    // counter to keep track of the current node that has focus (used for focus traversing with tab and shift+tab)
    public int focusNodeTracker = 0;

    SubtitleTrack subtitles;

    ControlTooltip openMenuTooltip;

    SVGPath menuSVG;

    Region addVideo;
    String addVideoPath;
    SVGPath addVideoSVG;


    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

        mediaInterface = new MediaInterface(this, controlBarController, settingsController, menuController);

        controlBarController.init(this, settingsController, menuController, mediaInterface); // shares references of all the controllers between eachother
        settingsController.init(this, controlBarController, menuController, mediaInterface);
        menuController.init(this, controlBarController, settingsController, mediaInterface);

        file = new File("src/main/resources/hans/hey.mp4");

        // declaring media control images
        menuSVG = new SVGPath();
        menuSVG.setContent(App.svgMap.get(MENU));

        addVideoSVG = new SVGPath();

        addVideo = new Region();
        addVideo.setMinSize(150,150);
        addVideo.setPrefSize(150,150);
        addVideo.setMaxSize(150,150);
        addVideo.setEffect(new DropShadow());
        addVideo.setId("addVideoIcon");

        // Make mediaView adjust to frame size

        mediaViewWidth = mediaView.fitWidthProperty();
        mediaViewHeight = mediaView.fitHeightProperty();
        mediaViewWidth.bind(mediaViewInnerWrapper.widthProperty());
        Platform.runLater(() -> mediaViewHeight.bind(mediaViewInnerWrapper.getScene().heightProperty()));

        mediaView.setPreserveRatio(true);


        //video expands to take up entire window if menu is not open
        Platform.runLater(() ->{
            if(!menuController.menuOpen){
                mediaViewWrapper.prefWidthProperty().bind(mediaViewWrapper.getScene().widthProperty());
            }
        });



        mediaViewWrapper.setStyle("-fx-background-color: rgb(0,0,0)");
        mediaViewInnerWrapper.setStyle("-fx-background-color: rgb(0,0,0)");

        menuButtonPane.translateXProperty().bind(menuController.menu.widthProperty().multiply(-1));
        menuButton.setBackground(Background.EMPTY);
        menuButton.setVisible(false);


        menuIcon.setShape(menuSVG);

        Platform.runLater(() -> {
            // needs to be run later so that the rest of the app can load in and this tooltip popup has a parent window to be associated with
            openMenuTooltip = new ControlTooltip("Open menu (q)", menuButton, controlBarController.controlBar, 1000, true);

            mediaViewWrapper.sceneProperty().get().widthProperty().addListener((observableValue, oldValue, newValue) -> {
                if(newValue.doubleValue() < menuController.menu.getPrefWidth()){
                    menuController.menu.setPrefWidth(newValue.doubleValue());
                    menuController.prefWidth = newValue.doubleValue();
                }
            });
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


        mediaView.setOnMouseClicked(e -> {

            if (e.getClickCount() == 1) {
                mediaClick();
            }

            if (e.getClickCount() == 2) {
                mediaClick();
                controlBarController.fullScreen();
            }
        });


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
        if(!menuController.menuInTransition) {
            menuController.menuInTransition = true;
            menuController.menuOpen = true;
            //menuController.menu.translateXProperty().unbind();
            //mediaViewWrapper.prefWidthProperty().unbind();
            AnimationsClass.openMenu(this, menuController);
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

       if(Utilities.getFileExtension(file).equals("mp4")) addVideoPath = "M20.84 2.18L16.91 2.96L19.65 6.5L21.62 6.1L20.84 2.18M13.97 3.54L12 3.93L14.75 7.46L16.71 7.07L13.97 3.54M9.07 4.5L7.1 4.91L9.85 8.44L11.81 8.05L9.07 4.5M4.16 5.5L3.18 5.69A2 2 0 0 0 1.61 8.04L2 10L6.9 9.03L4.16 5.5M2 10V20C2 21.11 2.9 22 4 22H20C21.11 22 22 21.11 22 20V10H2Z";
       else if(Utilities.getFileExtension(file).equals("mp3")) addVideoPath = "M21,3V15.5A3.5,3.5 0 0,1 17.5,19A3.5,3.5 0 0,1 14,15.5A3.5,3.5 0 0,1 17.5,12C18.04,12 18.55,12.12 19,12.34V6.47L9,8.6V17.5A3.5,3.5 0 0,1 5.5,21A3.5,3.5 0 0,1 2,17.5A3.5,3.5 0 0,1 5.5,14C6.04,14 6.55,14.12 7,14.34V6L21,3Z";

       if(Utilities.getFileExtension(file).equals("mp4")) addVideoSVG.setContent(App.svgMap.get(FILM));
       else if(Utilities.getFileExtension(file).equals("mp3")) addVideoSVG.setContent(App.svgMap.get(MUSIC));
       addVideo.setShape(addVideoSVG);

        if(!mediaViewWrapper.getChildren().contains(addVideo)){
            mediaViewWrapper.getChildren().add(addVideo);
        }

    }

    public void handleDragExited(DragEvent e){

        mediaView.setEffect(null);

        if(mediaViewWrapper.getChildren().contains(addVideo)){
            mediaViewWrapper.getChildren().remove(addVideo);
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


        if(mediaViewWrapper.getChildren().contains(addVideo)){
            mediaViewWrapper.getChildren().remove(addVideo);
        }

        // resets video name text in the settings tab if the animations had not finished before the user already selected a new video to play
        if(settingsController.marqueeTimeline != null && settingsController.marqueeTimeline.getStatus() == Animation.Status.RUNNING) settingsController.videoNameText.setLayoutX(0);

        mediaInterface.resetMediaPlayer();
        mediaInterface.playedVideoIndex = -1;

        MediaItem temp = null;

        if(Utilities.getFileExtension(file).equals("mp4")) temp = new Mp4Item(file);
        else if(Utilities.getFileExtension(file).equals("mp3")) temp = new Mp3Item(file);

        new QueueItem(temp, menuController, mediaInterface);

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

    public MenuController getMenuController(){ return menuController;}

    public MediaInterface getMediaInterface() {
        return mediaInterface;
    }
}