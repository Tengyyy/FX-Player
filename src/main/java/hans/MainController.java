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

    ActionIndicator actionIndicator;
    SeekIndicator forwardsIndicator, backwardsIndicator;

    SimpleDoubleProperty sizeMultiplier = new SimpleDoubleProperty();

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

        sizeMultiplier.set(0.7);

        actionIndicator = new ActionIndicator(this);
        forwardsIndicator = new SeekIndicator(this, true);
        backwardsIndicator = new SeekIndicator(this, false);


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

                mediaViewInnerWrapper.widthProperty().addListener((observableValue, oldValue, newValue) -> {
                    if(oldValue.doubleValue() < 1200 && newValue.doubleValue() >= 1200){
                        sizeMultiplier.set(1);
                        if(actionIndicator.wrapper.isVisible()) actionIndicator.updateSize();
                        forwardsIndicator.resize();
                        backwardsIndicator.resize();
                    }
                    else if(oldValue.doubleValue() >= 1200 & newValue.doubleValue() < 1200){
                        sizeMultiplier.set(0.7);
                        if(actionIndicator.wrapper.isVisible()) actionIndicator.updateSize();
                        forwardsIndicator.resize();
                        backwardsIndicator.resize();
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
                actionIndicator.setIcon(PLAY);
                actionIndicator.setVisible(true);
                actionIndicator.animate();
            } else {
                if (mediaInterface.playing) {
                    controlBarController.pause();
                    actionIndicator.setIcon(PAUSE);
                    actionIndicator.setVisible(true);
                    actionIndicator.animate();
                } else {
                    controlBarController.play();
                    actionIndicator.setIcon(PLAY);
                    actionIndicator.setVisible(true);
                    actionIndicator.animate();
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


        actionIndicator.setIcon(PLUS);
        actionIndicator.setVisible(true);

        if(settingsController.settingsOpen) settingsController.closeSettings();
        else if(captionsOpen) controlBarController.closeCaptions();

       if(mediaInterface.playing)controlBarController.mouseEventTracker.hide();
       else AnimationsClass.hideControls(controlBarController);

    }

    public void handleDragExited(DragEvent e){

        //mediaView.setEffect(null);
        if(actionIndicator.parallelTransition.getStatus() != Animation.Status.RUNNING) actionIndicator.setVisible(false);
    }

    public void handleDragOver(DragEvent e){
        File file = e.getDragboard().getFiles().get(0);
        if(!Utilities.getFileExtension(file).equals("mp4") && !Utilities.getFileExtension(file).equals("mp3")) return;

        e.acceptTransferModes(TransferMode.COPY);
    }

    public void handleDragDropped(DragEvent e){
        //mediaView.setEffect(null);
        File file = e.getDragboard().getFiles().get(0);

        /* return statement */
        if(!Utilities.getFileExtension(file).equals("mp4") && !Utilities.getFileExtension(file).equals("mp3")) return;

        actionIndicator.animate();


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