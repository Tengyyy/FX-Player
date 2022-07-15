package hans;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import java.util.*;


import io.github.palexdev.materialfx.utils.SwingFXUtils;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;


import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;


import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import javax.imageio.ImageIO;

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
    private MenuController menuController;

    SettingsController settingsController;
    CaptionsController captionsController;

    MediaInterface mediaInterface;

    DoubleProperty mediaViewWidth;
    DoubleProperty mediaViewHeight;


    // counter to keep track of the current node that has focus (used for focus traversing with tab and shift+tab)
    public int focusNodeTracker = 0;

    ControlTooltip openMenuTooltip;

    SVGPath menuSVG;

    ActionIndicator actionIndicator;
    SeekIndicator forwardsIndicator, backwardsIndicator;
    ValueIndicator valueIndicator;

    SimpleDoubleProperty sizeMultiplier = new SimpleDoubleProperty();

    StackPane whitePane = new StackPane();
    Label miniplayerActiveText = new Label();


    boolean miniplayerActive = false;
    Miniplayer miniplayer;

    ChangeListener<? super Number> widthListener;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

        settingsController = new SettingsController(this, controlBarController, menuController);
        mediaInterface = new MediaInterface(this, controlBarController, settingsController, menuController);
        captionsController = new CaptionsController(settingsController, this, mediaInterface, controlBarController, menuController);


        controlBarController.init(this, settingsController, menuController, mediaInterface, captionsController); // shares references of all the controllers between eachother
        menuController.init(this, controlBarController, settingsController, mediaInterface, captionsController);
        settingsController.init(mediaInterface, captionsController);

        mediaViewWrapper.getChildren().add(2, settingsController.settingsBuffer);


        // declaring media control images
        menuSVG = new SVGPath();
        menuSVG.setContent(App.svgMap.get(MENU));

        sizeMultiplier.set(0.65);

        actionIndicator = new ActionIndicator(this);
        forwardsIndicator = new SeekIndicator(this, true);
        backwardsIndicator = new SeekIndicator(this, false);
        valueIndicator = new ValueIndicator(this);


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

        menuButtonPane.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
            controlBarController.controlButtonHoverOn(menuButtonPane);
        });

        menuButtonPane.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            controlBarController.controlButtonHoverOff(menuButtonPane);
        });

        menuIcon.setShape(menuSVG);

        whitePane.setPrefSize(StackPane.USE_COMPUTED_SIZE, StackPane.USE_COMPUTED_SIZE);
        whitePane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        whitePane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        whitePane.setMouseTransparent(true);
        whitePane.setOpacity(0);



        miniplayerActiveText.setText("Video active in miniplayer");
        miniplayerActiveText.setId("mediaViewText");
        miniplayerActiveText.setBackground(Background.EMPTY);
        miniplayerActiveText.setMouseTransparent(true);
        miniplayerActiveText.setVisible(false);
        StackPane.setAlignment(miniplayerActiveText, Pos.CENTER);

        mediaViewWrapper.getChildren().add(whitePane);
        mediaViewInnerWrapper.getChildren().addAll(miniplayerActiveText);

        Platform.runLater(() -> {            // needs to be run later so that the rest of the app can load in and this tooltip popup has a parent window to be associated with
            openMenuTooltip = new ControlTooltip("Open menu (q)", menuButton, controlBarController.controlBarWrapper, 1000, true);

            mediaViewWrapper.sceneProperty().get().widthProperty().addListener((observableValue, oldValue, newValue) -> {
                if(newValue.doubleValue() < menuController.menu.getPrefWidth()){
                    menuController.menu.setPrefWidth(newValue.doubleValue());
                    menuController.prefWidth = newValue.doubleValue();
                }
            });


        });

        widthListener = (observableValue, oldValue, newValue) -> {

            if(oldValue.doubleValue() >= 800 && newValue.doubleValue() < 800){
                captionsController.mediaWidthMultiplier.set(0.4);
                captionsController.resizeCaptions();

                sizeMultiplier.set(0.55);
                if(actionIndicator.wrapper.isVisible()) actionIndicator.updateSize();
                forwardsIndicator.resize();
                backwardsIndicator.resize();
                valueIndicator.resize();
            }
            else if((oldValue.doubleValue() < 800 || oldValue.doubleValue() >= 1200) && (newValue.doubleValue() >= 800 && newValue.doubleValue() < 1200)){
                captionsController.mediaWidthMultiplier.set(0.6);
                captionsController.resizeCaptions();

                sizeMultiplier.set(0.65);
                if(actionIndicator.wrapper.isVisible()) actionIndicator.updateSize();
                forwardsIndicator.resize();
                backwardsIndicator.resize();
                valueIndicator.resize();

            }
            else if((oldValue.doubleValue() < 1200 || oldValue.doubleValue() >= 1800) && (newValue.doubleValue() >= 1200 && newValue.doubleValue() < 1800)){
                captionsController.mediaWidthMultiplier.set(0.8);
                captionsController.resizeCaptions();

                sizeMultiplier.set(0.8);
                if(actionIndicator.wrapper.isVisible()) actionIndicator.updateSize();
                forwardsIndicator.resize();
                backwardsIndicator.resize();
                valueIndicator.resize();

            }
            else if((oldValue.doubleValue() < 1800 || oldValue.doubleValue() >= 2400) && (newValue.doubleValue() >= 1800 && newValue.doubleValue() < 2400)){
                captionsController.mediaWidthMultiplier.set(1.0);
                captionsController.resizeCaptions();


                sizeMultiplier.set(1);
                if(actionIndicator.wrapper.isVisible()) actionIndicator.updateSize();
                forwardsIndicator.resize();
                backwardsIndicator.resize();
                valueIndicator.resize();

            }
            else if(oldValue.doubleValue() < 2400 && newValue.doubleValue() >= 2400){
                captionsController.mediaWidthMultiplier.set(1.2);
                captionsController.resizeCaptions();

                sizeMultiplier.set(1.2);
                if(actionIndicator.wrapper.isVisible()) actionIndicator.updateSize();
                forwardsIndicator.resize();
                backwardsIndicator.resize();
                valueIndicator.resize();
            }
        };

        mediaViewInnerWrapper.widthProperty().addListener(widthListener);

        mediaViewInnerWrapper.setOnMouseDragOver(e -> {
            if(captionsController.captionsDragActive){
                if(e.getY() - captionsController.dragPositionY <= captionsController.minimumY) captionsController.captionsBox.setTranslateY(((captionsController.startY - captionsController.minimumY) * -1) + captionsController.startTranslateY);
                else if(e.getY() - captionsController.dragPositionY + captionsController.captionsBox.getLayoutBounds().getMaxY() > captionsController.maximumY) captionsController.captionsBox.setTranslateY(captionsController.maximumY - captionsController.startY - captionsController.captionsBox.getLayoutBounds().getMaxY() + captionsController.startTranslateY);
                else captionsController.captionsBox.setTranslateY(e.getY() - captionsController.dragPositionY - captionsController.startY + captionsController.startTranslateY);

                if(e.getX() - captionsController.dragPositionX <= captionsController.minimumX) captionsController.captionsBox.setTranslateX(((captionsController.startX - captionsController.minimumX) * -1) + captionsController.startTranslateX);
                else if(e.getX() - captionsController.dragPositionX + captionsController.captionsBox.getLayoutBounds().getMaxX() > captionsController.maximumX) captionsController.captionsBox.setTranslateX(captionsController.maximumX - captionsController.startX - captionsController.captionsBox.getLayoutBounds().getMaxX() + captionsController.startTranslateX);
                else captionsController.captionsBox.setTranslateX(e.getX() - captionsController.dragPositionX - captionsController.startX + captionsController.startTranslateX);
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

        if (settingsController.settingsState != SettingsState.CLOSED) {
            settingsController.closeSettings();
        }
        else if(mediaInterface.mediaPlayer != null && !miniplayerActive){
            if (mediaInterface.atEnd) {
                controlBarController.replayMedia();
                actionIndicator.setIcon(REPLAY);
            } else {
                if (mediaInterface.playing.get()) {
                    controlBarController.pause();
                    actionIndicator.setIcon(PAUSE);
                } else {
                    controlBarController.play();
                    actionIndicator.setIcon(PLAY);
                }
            }
            actionIndicator.setVisible(true);
            actionIndicator.animate();
        }

        mediaView.requestFocus();
    }


    public void openMenu() {
        if(!menuController.menuInTransition) {

            captionsController.cancelDrag();

            menuController.menuInTransition = true;
            menuController.menuOpen = true;

            AnimationsClass.openMenu(this, menuController);
        }
    }

    public void handleDragEntered(DragEvent e){
        File file = e.getDragboard().getFiles().get(0);
        if(!Utilities.getFileExtension(file).equals("mp4") && !Utilities.getFileExtension(file).equals("mp3")) return;


        actionIndicator.setIcon(PLUS);
        actionIndicator.setVisible(true);

        if(settingsController.settingsState != SettingsState.CLOSED) settingsController.closeSettings();

       if(mediaInterface.playing.get())controlBarController.mouseEventTracker.hide();
       else AnimationsClass.hideControls(controlBarController, captionsController);

    }

    public void handleDragExited(DragEvent e){

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


        MediaItem temp = null;

        if(Utilities.getFileExtension(file).equals("mp4")) temp = new Mp4Item(file);
        else if(Utilities.getFileExtension(file).equals("mp3")) temp = new Mp3Item(file);

        ActiveItem activeItem = new ActiveItem(temp, menuController, mediaInterface, menuController.activeBox);
        activeItem.play(true);

    }

    public void takeScreenshot(){
        if(menuController.activeItem == null || miniplayerActive) return;

        FileChooser fileChooser = new FileChooser();

        //Set extension filter
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG file (*.png)", "*.png"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPG file (*.jpg)", "*.jpg"));

        double width = menuController.activeItem.getMediaItem().getMedia().getWidth();
        double height = menuController.activeItem.getMediaItem().getMedia().getHeight();
        double aspectRatio = width / height;

        double realWidth = Math.min(mediaView.getFitWidth(), mediaView.getFitHeight() * aspectRatio);
        double realHeight = Math.min(mediaView.getFitHeight(), mediaView.getFitWidth() / aspectRatio);

        WritableImage writableImage = new WritableImage((int) realWidth, (int)realHeight);

        mediaView.snapshot(null, writableImage);

        // Flashing screen animation
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(300), whitePane);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.setAutoReverse(true);
        fadeTransition.setCycleCount(2);
        fadeTransition.playFromStart();


        //Prompt user to select a file
        File file = fileChooser.showSaveDialog(App.stage);

        if(file != null){
            try {
                RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, new BufferedImage((int) writableImage.getWidth(), (int) writableImage.getHeight(), BufferedImage.TYPE_INT_RGB));

                //Write the snapshot to the chosen file
                ImageIO.write(renderedImage, Utilities.getFileExtension(file), file);

            } catch (IOException ex) { ex.printStackTrace(); }
        }
    }

    public void openMiniplayer(){
        miniplayerActive = true;

        mediaView.setMediaPlayer(null);
        // causes Concurrent Modification Exception
        // seems to be a JavaFX bug, have to try either creating new mediaviews every opening/closing of the miniplayer or implementing vlcj library

        mediaViewInnerWrapper.widthProperty().removeListener(widthListener);

        miniplayer = new Miniplayer(this, controlBarController, menuController, mediaInterface);

        miniplayer.miniplayerController.moveIndicators();

        if(menuController.activeItem != null){
            miniplayerActiveText.setVisible(true);
        }
    }

    public void closeMiniplayer(){

        miniplayer.miniplayerController.mediaViewInnerWrapper.widthProperty().removeListener(miniplayer.miniplayerController.widthListener);

        actionIndicator.moveToMainplayer();
        forwardsIndicator.moveToMainplayer();
        backwardsIndicator.moveToMainplayer();
        valueIndicator.moveToMainplayer();

        if(miniplayerActive && miniplayer != null && miniplayer.stage != null){
            miniplayer.stage.close();
        }

        miniplayerActive = false;

        resizeIndicators();
        mediaViewInnerWrapper.widthProperty().addListener(widthListener);

        if(menuController.activeItem != null && mediaInterface.mediaPlayer != null){
            mediaView.setMediaPlayer(mediaInterface.mediaPlayer);
            miniplayerActiveText.setVisible(false);
        }
    }

    public void resizeIndicators(){
        if(mediaViewInnerWrapper.getWidth() < 800){
            captionsController.mediaWidthMultiplier.set(0.4);
            captionsController.resizeCaptions();

            sizeMultiplier.set(0.55);
            forwardsIndicator.resize();
            backwardsIndicator.resize();
            valueIndicator.resize();
        }
        else if(mediaViewInnerWrapper.getWidth() >= 800 && mediaViewInnerWrapper.getWidth() < 1200){
            captionsController.mediaWidthMultiplier.set(0.6);
            captionsController.resizeCaptions();

            sizeMultiplier.set(0.65);
            forwardsIndicator.resize();
            backwardsIndicator.resize();
            valueIndicator.resize();

        }
        else if(mediaViewInnerWrapper.getWidth() >= 1200 && mediaViewInnerWrapper.getWidth() < 1800){
            captionsController.mediaWidthMultiplier.set(0.8);
            captionsController.resizeCaptions();

            sizeMultiplier.set(0.8);
            forwardsIndicator.resize();
            backwardsIndicator.resize();
            valueIndicator.resize();

        }
        else if(mediaViewInnerWrapper.getWidth() >= 1800 && mediaViewInnerWrapper.getWidth() < 2400){
            captionsController.mediaWidthMultiplier.set(1.0);
            captionsController.resizeCaptions();


            sizeMultiplier.set(1);
            forwardsIndicator.resize();
            backwardsIndicator.resize();
            valueIndicator.resize();

        }
        else if(mediaViewInnerWrapper.getWidth() >= 2400){
            captionsController.mediaWidthMultiplier.set(1.2);
            captionsController.resizeCaptions();

            sizeMultiplier.set(1.2);
            forwardsIndicator.resize();
            backwardsIndicator.resize();
            valueIndicator.resize();
        }
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

    public CaptionsController getCaptionsController(){
        return captionsController;
    }
}