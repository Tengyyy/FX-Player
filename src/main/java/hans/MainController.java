package hans;

import java.io.File;
import java.net.URL;

import java.text.SimpleDateFormat;
import java.util.*;


import hans.MediaItems.*;
import hans.Menu.ActiveItem;
import hans.Menu.MenuController;
import hans.Menu.MenuState;
import hans.Settings.SettingsController;
import hans.Settings.SettingsState;
import javafx.animation.Animation;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;


import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;


import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;
import uk.co.caprica.vlcj.javafx.videosurface.ImageViewVideoSurface;

import static hans.SVG.*;


public class MainController implements Initializable {

    @FXML
    public ImageView videoImageView;


    @FXML
    StackPane outerPane, videoImageViewWrapper, videoImageViewInnerWrapper;


    @FXML
    private ControlBarController controlBarController;

    @FXML
    private MenuController menuController;

    SettingsController settingsController;
    CaptionsController captionsController;

    MediaInterface mediaInterface;

    DoubleProperty videoImageViewWidth;
    public DoubleProperty videoImageViewHeight;



    ControlTooltip openMenuTooltip, viewMetadataTooltip;

    SVGPath menuSVG;

    ActionIndicator actionIndicator;
    SeekIndicator forwardsIndicator, backwardsIndicator;
    ValueIndicator valueIndicator;

    SimpleDoubleProperty sizeMultiplier = new SimpleDoubleProperty();
    SimpleDoubleProperty heightMultiplier = new SimpleDoubleProperty();

    public Label miniplayerActiveText = new Label();

    boolean seekingWithKeys = false; // if true, show miniplayer progressbar

    public boolean miniplayerActive = false;
    Miniplayer miniplayer;

    ChangeListener<? super Number> widthListener;
    ChangeListener<? super Number> heightListener;

    ChangeListener<? super Number> widthListenerForTitle;


    public PlaybackOptionsPopUp playbackOptionsPopUp;

    String snapshotDirectory;

    StackPane videoTitleBox = new StackPane();
    Label videoTitleLabel = new Label();


    public SliderHoverLabel sliderHoverLabel;
    public SliderHoverPreview sliderHoverPreview;


    StackPane videoTitleBackground = new StackPane();
    Button menuButton = new Button();
    StackPane menuButtonPane = new StackPane();
    Region menuIcon = new Region();

    SVGPath metadataPath = new SVGPath();
    StackPane metadataButtonPane = new StackPane();
    Button metadataButton = new Button();
    Region metadataIcon = new Region();


    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

        settingsController = new SettingsController(this, controlBarController, menuController);
        mediaInterface = new MediaInterface(this, controlBarController, settingsController, menuController);
        captionsController = new CaptionsController(settingsController, this, mediaInterface, controlBarController, menuController);


        controlBarController.init(this, settingsController, menuController, mediaInterface, captionsController); // shares references of all the controllers between eachother
        menuController.init(this, controlBarController, settingsController, mediaInterface, captionsController);
        settingsController.init(mediaInterface, captionsController);

        mediaInterface.init();

        sliderHoverLabel = new SliderHoverLabel(videoImageViewWrapper, controlBarController, false);
        sliderHoverPreview = new SliderHoverPreview(videoImageViewWrapper, controlBarController);

        videoImageViewWrapper.getChildren().add(2, settingsController.settingsBuffer);

        playbackOptionsPopUp = new PlaybackOptionsPopUp(settingsController);

        snapshotDirectory = System.getProperty("user.home").concat("/vlcj-snapshots/");


        // declaring media control images
        menuSVG = new SVGPath();
        menuSVG.setContent(App.svgMap.get(MENU));

        sizeMultiplier.set(0.65);

        actionIndicator = new ActionIndicator(this);
        forwardsIndicator = new SeekIndicator(this, true);
        backwardsIndicator = new SeekIndicator(this, false);
        valueIndicator = new ValueIndicator(this);


        // Make mediaView adjust to frame size

        videoImageViewWidth = videoImageView.fitWidthProperty();
        videoImageViewHeight = videoImageView.fitHeightProperty();
        videoImageViewWidth.bind(videoImageViewInnerWrapper.widthProperty());
        Platform.runLater(() -> videoImageViewHeight.bind(videoImageViewInnerWrapper.getScene().heightProperty()));

        videoImageView.setPreserveRatio(true);


        //hide controlbar when mouse exits window
        Platform.runLater(() ->{
            videoImageViewWrapper.getScene().setOnMouseExited(e -> {
                if(!playbackOptionsPopUp.isShowing()) controlBarController.mouseEventTracker.hide();
            });
        });



        videoImageViewWrapper.setStyle("-fx-background-color: rgb(0,0,0)");
        videoImageViewInnerWrapper.setStyle("-fx-background-color: rgb(0,0,0)");


        miniplayerActiveText.setText("Media active in miniplayer");
        miniplayerActiveText.setId("mediaViewText");
        miniplayerActiveText.setBackground(Background.EMPTY);
        miniplayerActiveText.setMouseTransparent(true);
        miniplayerActiveText.setVisible(false);
        StackPane.setAlignment(miniplayerActiveText, Pos.CENTER);

        videoImageViewInnerWrapper.getChildren().add(miniplayerActiveText);

        Platform.runLater(() -> {            // needs to be run later so that the rest of the app can load in and this tooltip popup has a parent window to be associated with
            openMenuTooltip = new ControlTooltip(this,"Open menu (q)", menuButton, 0, false, true);
            viewMetadataTooltip = new ControlTooltip(this,"Media information", metadataButton, 0, false, true);

            videoImageViewWrapper.sceneProperty().get().widthProperty().addListener((observableValue, oldValue, newValue) -> {
                if(newValue.doubleValue() < menuController.menu.getMaxWidth()){
                    menuController.menu.setMaxWidth(newValue.doubleValue());
                }
            });


        });

        videoImageViewInnerWrapper.getChildren().add(1, controlBarController.controlBarBackground);

        widthListener = (observableValue, oldValue, newValue) -> {

            if(newValue.doubleValue() < 800){
                captionsController.mediaWidthMultiplier.set(0.4);
                captionsController.resizeCaptions();

                sizeMultiplier.set(0.55);
                if(actionIndicator.wrapper.isVisible()) actionIndicator.updateSize();
                forwardsIndicator.resize();
                backwardsIndicator.resize();
                valueIndicator.resize();
            }
            else if((newValue.doubleValue() >= 800 && newValue.doubleValue() < 1200)){
                captionsController.mediaWidthMultiplier.set(0.6);
                captionsController.resizeCaptions();

                sizeMultiplier.set(0.65);
                if(actionIndicator.wrapper.isVisible()) actionIndicator.updateSize();
                forwardsIndicator.resize();
                backwardsIndicator.resize();
                valueIndicator.resize();

            }
            else if((newValue.doubleValue() >= 1200 && newValue.doubleValue() < 1800)){
                captionsController.mediaWidthMultiplier.set(0.8);
                captionsController.resizeCaptions();

                sizeMultiplier.set(0.8);
                if(actionIndicator.wrapper.isVisible()) actionIndicator.updateSize();
                forwardsIndicator.resize();
                backwardsIndicator.resize();
                valueIndicator.resize();

            }
            else if((newValue.doubleValue() >= 1800 && newValue.doubleValue() < 2400)){
                captionsController.mediaWidthMultiplier.set(1.0);
                captionsController.resizeCaptions();


                sizeMultiplier.set(1);
                if(actionIndicator.wrapper.isVisible()) actionIndicator.updateSize();
                forwardsIndicator.resize();
                backwardsIndicator.resize();
                valueIndicator.resize();

            }
            else if(newValue.doubleValue() >= 2400){
                captionsController.mediaWidthMultiplier.set(1.2);
                captionsController.resizeCaptions();

                sizeMultiplier.set(1.2);
                if(actionIndicator.wrapper.isVisible()) actionIndicator.updateSize();
                forwardsIndicator.resize();
                backwardsIndicator.resize();
                valueIndicator.resize();
            }
        };

        heightListener = (observableValue, oldValue, newValue) -> {

            if(newValue.doubleValue() < 400){

                heightMultiplier.set(0.55);
                valueIndicator.reposition();

            }
            else if((newValue.doubleValue() >= 400 && newValue.doubleValue() < 600)){

                heightMultiplier.set(0.65);
                valueIndicator.reposition();

            }
            else if((newValue.doubleValue() >= 600 && newValue.doubleValue() < 900)){

                heightMultiplier.set(0.8);
                valueIndicator.reposition();

            }
            else if((newValue.doubleValue() >= 900 && newValue.doubleValue() < 1200)){

                heightMultiplier.set(1);
                valueIndicator.reposition();

            }
            else if(newValue.doubleValue() >= 1200){


                heightMultiplier.set(1.2);
                valueIndicator.reposition();
            }
        };

        videoImageViewInnerWrapper.widthProperty().addListener(widthListener);
        videoImageViewInnerWrapper.heightProperty().addListener(heightListener);

        widthListenerForTitle = (observableValue, oldValue, newValue) -> {

             if(newValue.doubleValue() < 800){
                videoTitleLabel.setStyle("-fx-font-family: Roboto Medium; -fx-font-size: 17");
            }
            else if((newValue.doubleValue() >= 800 && newValue.doubleValue() < 1200)){
                // default

                videoTitleLabel.setStyle("-fx-font-family: Roboto Medium; -fx-font-size: 20");

            }
            else if((newValue.doubleValue() >= 1200 && newValue.doubleValue() < 1800)){

                videoTitleLabel.setStyle("-fx-font-family: Roboto Medium; -fx-font-size: 22");

            }
            else if((newValue.doubleValue() >= 1800 && newValue.doubleValue() < 2400)){

                videoTitleLabel.setStyle("-fx-font-family: Roboto Medium; -fx-font-size: 25");

            }
            else if(newValue.doubleValue() >= 2400){

                videoTitleLabel.setStyle("-fx-font-family: Roboto Medium; -fx-font-size: 28");

            }
        };

        videoImageViewInnerWrapper.widthProperty().addListener(widthListenerForTitle);



        videoImageViewWrapper.setOnMouseDragOver(e -> {
            if(captionsController.captionsDragActive){
                if(e.getY() - captionsController.dragPositionY <= captionsController.minimumY) captionsController.captionsBox.setTranslateY(((captionsController.startY - captionsController.minimumY) * -1) + captionsController.startTranslateY);
                else if(e.getY() - captionsController.dragPositionY + captionsController.captionsBox.getLayoutBounds().getMaxY() > captionsController.maximumY) captionsController.captionsBox.setTranslateY(captionsController.maximumY - captionsController.startY - captionsController.captionsBox.getLayoutBounds().getMaxY() + captionsController.startTranslateY);
                else captionsController.captionsBox.setTranslateY(e.getY() - captionsController.dragPositionY - captionsController.startY + captionsController.startTranslateY);

                if(e.getX() - captionsController.dragPositionX <= captionsController.minimumX) captionsController.captionsBox.setTranslateX(((captionsController.startX - captionsController.minimumX) * -1) + captionsController.startTranslateX);
                else if(e.getX() - captionsController.dragPositionX + captionsController.captionsBox.getLayoutBounds().getMaxX() > captionsController.maximumX) captionsController.captionsBox.setTranslateX(captionsController.maximumX - captionsController.startX - captionsController.captionsBox.getLayoutBounds().getMaxX() + captionsController.startTranslateX);
                else captionsController.captionsBox.setTranslateX(e.getX() - captionsController.dragPositionX - captionsController.startX + captionsController.startTranslateX);
            }

            e.consume();
        });



        videoImageViewInnerWrapper.setOnMouseClicked(e -> {

            if (e.getClickCount() == 1) {


                mediaClick(e);

                if(e.getButton() == MouseButton.SECONDARY) e.consume();
            }
            else if (e.getClickCount() == 2 && e.getButton() == MouseButton.PRIMARY) {


                mediaClick(e);
                controlBarController.toggleFullScreen();
            }
        });


        videoTitleBackground.setPrefHeight(120);
        videoTitleBackground.setMaxHeight(120);
        videoTitleBackground.setStyle("-fx-background-color: linear-gradient(to bottom, rgba(0,0,0,0.6), rgba(0,0,0,0));");
        videoTitleBackground.setAlignment(Pos.TOP_LEFT);
        StackPane.setAlignment(videoTitleBackground, Pos.TOP_LEFT);
        videoTitleBackground.maxWidthProperty().bind(videoImageViewWrapper.widthProperty());
        videoTitleBackground.setMouseTransparent(true);

        videoTitleBox.setMinHeight(60);
        videoTitleBox.setMaxHeight(60);
        videoTitleBox.setAlignment(Pos.CENTER_LEFT);
        StackPane.setAlignment(videoTitleBox, Pos.TOP_LEFT);

        videoTitleBox.getChildren().addAll(menuButtonPane, videoTitleLabel, metadataButtonPane);

        menuButtonPane.setPrefSize(50, 50);
        menuButtonPane.setMaxSize(50, 50);
        menuButtonPane.setBackground(Background.EMPTY);
        menuButtonPane.getChildren().addAll(menuButton, menuIcon);

        menuButtonPane.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> controlBarController.controlButtonHoverOn(menuButtonPane));

        menuButtonPane.addEventHandler(MouseEvent.MOUSE_EXITED, e -> controlBarController.controlButtonHoverOff(menuButtonPane));


        menuButton.setPrefSize(50, 50);
        menuButton.setMaxSize(50, 50);
        menuButton.setBackground(Background.EMPTY);
        menuButton.setCursor(Cursor.HAND);
        menuButton.setOnAction(e -> openMenu());


        menuIcon.setShape(menuSVG);
        menuIcon.setPrefSize(30, 25);
        menuIcon.setMaxSize(30, 25);
        menuIcon.setMouseTransparent(true);
        menuIcon.getStyleClass().add("controlIcon");


        videoTitleLabel.setMouseTransparent(false);
        videoTitleLabel.setBackground(Background.EMPTY);
        videoTitleLabel.setText(null);
        videoTitleLabel.setTranslateX(70);
        videoTitleLabel.maxWidthProperty().bind(videoImageViewInnerWrapper.widthProperty().subtract(70).subtract(80));
        videoTitleLabel.setTextFill(Color.rgb(200, 200, 200));
        videoTitleLabel.setStyle("-fx-font-family: Roboto Medium; -fx-font-size: 20");
        videoTitleLabel.setEffect(new DropShadow());
        videoTitleLabel.setOnMouseClicked(e -> {
            if(settingsController.settingsState != SettingsState.CLOSED) settingsController.closeSettings();
            e.consume();
        });

        videoTitleLabel.setOnMouseEntered(e -> AnimationsClass.AnimateTextColor(videoTitleLabel, Color.rgb(255, 255, 255), 200));

        videoTitleLabel.setOnMouseExited(e -> AnimationsClass.AnimateTextColor(videoTitleLabel, Color.rgb(200, 200,200), 200));

        metadataButtonPane.setPrefSize(50, 50);
        metadataButtonPane.setMaxSize(50, 50);
        metadataButtonPane.setBackground(Background.EMPTY);
        metadataButtonPane.getChildren().addAll(metadataButton, metadataIcon);
        StackPane.setAlignment(metadataButtonPane, Pos.CENTER_RIGHT);
        StackPane.setMargin(metadataButtonPane, new Insets(0, 20, 0, 0));

        metadataButtonPane.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> controlBarController.controlButtonHoverOn(metadataButtonPane));

        metadataButtonPane.addEventHandler(MouseEvent.MOUSE_EXITED, e -> controlBarController.controlButtonHoverOff(metadataButtonPane));

        metadataButtonPane.setVisible(false);
        metadataButtonPane.setMouseTransparent(true);
        metadataButtonPane.visibleProperty().bind(mediaInterface.mediaActive);
        metadataButtonPane.mouseTransparentProperty().bind(mediaInterface.mediaActive.not());


        metadataButton.setPrefSize(50, 50);
        metadataButton.setMaxSize(50, 50);
        metadataButton.setBackground(Background.EMPTY);
        metadataButton.setCursor(Cursor.HAND);
        metadataButton.setOnAction(e -> {
            //TODO: find out why this still doesnt work
            if(menuController.menuInTransition) return;
            if(menuController.activeItem != null){
                openMenu();
                menuController.metadataEditPage.enterMetadataEditPage(menuController.activeItem);
            }
        });

        metadataPath.setContent(App.svgMap.get(INFORMATION));
        metadataIcon.setShape(metadataPath);
        metadataIcon.setPrefSize(25, 25);
        metadataIcon.setMaxSize(25, 25);
        metadataIcon.setMouseTransparent(true);
        metadataIcon.getStyleClass().add("controlIcon");



        videoImageViewInnerWrapper.getChildren().add(1, videoTitleBackground);
        videoImageViewInnerWrapper.getChildren().add(videoTitleBox);

    }

    public void mediaClick(MouseEvent e) {

        // Clicking on the mediaview node will close the settings tab if its open or
        // otherwise play/pause/replay the video

        if(e.getButton() == MouseButton.SECONDARY){
            // open/close loop toggle pop-up
            playbackOptionsPopUp.show(videoImageViewInnerWrapper, e.getScreenX(), e.getScreenY());


            return;
        }

        if(playbackOptionsPopUp.isShowing()){
            playbackOptionsPopUp.hide();
            return;
        }


        if(menuController.menuState == MenuState.METADATA_OPEN || menuController.menuState == MenuState.QUEUE_OPEN || menuController.menuState == MenuState.TECHNICAL_DETAILS_OPEN || (menuController.menuState == MenuState.METADATA_EDIT_OPEN && !menuController.metadataEditPage.changesMade.get())){
            menuController.closeMenu();
        }
        else if (settingsController.settingsState != SettingsState.CLOSED) {
            settingsController.closeSettings();
        }
        else if(mediaInterface.mediaActive.get() && !miniplayerActive){
            if (mediaInterface.atEnd) {
                mediaInterface.replay();
                actionIndicator.setIcon(REPLAY);
            } else {
                if (mediaInterface.playing.get()) {
                    mediaInterface.wasPlaying = false;
                    mediaInterface.pause();
                    actionIndicator.setIcon(PAUSE);
                } else {
                    mediaInterface.play();
                    actionIndicator.setIcon(PLAY);
                }
            }
            actionIndicator.setVisible(true);
            actionIndicator.animate();
        }

        videoImageView.requestFocus();
    }


    public void openMenu() {

        if(menuController.menuInTransition || controlBarController.durationSlider.isValueChanging() || controlBarController.volumeSlider.isValueChanging() || settingsController.playbackSpeedController.customSpeedPane.customSpeedSlider.isValueChanging() || captionsController.captionsDragActive) return;

        menuController.menuInTransition = true;
        menuController.menuState = MenuState.QUEUE_OPEN;

        if(settingsController.settingsState != SettingsState.CLOSED) settingsController.closeSettings();

        controlBarController.controlBarWrapper.setMouseTransparent(true);

        if(controlBarController.controlBarOpen) AnimationsClass.hideControls(controlBarController, captionsController, this);

        videoImageViewWrapper.getScene().setCursor(Cursor.DEFAULT);

        AnimationsClass.openMenu(menuController, this);

        captionsController.captionsBox.setMouseTransparent(true);


    }

    public void handleDragEntered(DragEvent e){
        File file = e.getDragboard().getFiles().get(0);
        if(!Utilities.getFileExtension(file).equals("mp4") && !Utilities.getFileExtension(file).equals("mp3") && !Utilities.getFileExtension(file).equals("wav") && !Utilities.getFileExtension(file).equals("mov") && !Utilities.getFileExtension(file).equals("mkv")&& !Utilities.getFileExtension(file).equals("flv") && !Utilities.getFileExtension(file).equals("flac")&& !Utilities.getFileExtension(file).equals("avi")) return;


        actionIndicator.setIcon(PLUS);
        actionIndicator.setVisible(true);

        if(settingsController.settingsState != SettingsState.CLOSED) settingsController.closeSettings();

       AnimationsClass.hideControls(controlBarController, captionsController, this);

    }

    public void handleDragExited(){

        if(actionIndicator.parallelTransition.getStatus() != Animation.Status.RUNNING) actionIndicator.setVisible(false);
    }

    public void handleDragOver(DragEvent e){
        File file = e.getDragboard().getFiles().get(0);
        if(!Utilities.getFileExtension(file).equals("mp4") && !Utilities.getFileExtension(file).equals("mp3") && !Utilities.getFileExtension(file).equals("wav") && !Utilities.getFileExtension(file).equals("mov") && !Utilities.getFileExtension(file).equals("mkv")&& !Utilities.getFileExtension(file).equals("flv") && !Utilities.getFileExtension(file).equals("flac")&& !Utilities.getFileExtension(file).equals("avi")) return;

        e.acceptTransferModes(TransferMode.COPY);
    }

    public void handleDragDropped(DragEvent e){

        File file = e.getDragboard().getFiles().get(0);

        MediaItem temp;

        switch(Utilities.getFileExtension(file)){
            case "mp4": temp = new Mp4Item(file, controlBarController.mainController);
                break;
            case "mp3": temp = new Mp3Item(file, controlBarController.mainController);
                break;
            case "avi": temp = new AviItem(file, controlBarController.mainController);
                break;
            case "mkv": temp = new MkvItem(file, controlBarController.mainController);
                break;
            case "flac": temp = new FlacItem(file, controlBarController.mainController);
                break;
            case "flv": temp = new FlvItem(file, controlBarController.mainController);
                break;
            case "mov": temp = new MovItem(file, controlBarController.mainController);
                break;
            case "wav": temp = new WavItem(file, controlBarController.mainController);
                break;
            default:
                return;
        }

        actionIndicator.animate();

        ActiveItem activeItem = new ActiveItem(temp, menuController, mediaInterface, menuController.activeBox);
        activeItem.play(true);

    }

    public void takeScreenshot(){
        if(menuController.activeItem == null || miniplayerActive) return;


        // snapshot file name formatting
        String out = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss").format(new Date());
        String videoName = menuController.activeItem.videoTitle.getText();

        mediaInterface.embeddedMediaPlayer.snapshots().save(new File(snapshotDirectory.concat(videoName).concat(" ").concat(out).concat(".png")));



    }

    public void openMiniplayer(){

        if(controlBarController.durationSlider.isValueChanging()) return;

        miniplayerActive = true;

        if(App.fullScreen) controlBarController.toggleFullScreen();



        videoImageViewInnerWrapper.widthProperty().removeListener(widthListener);
        videoImageViewInnerWrapper.heightProperty().removeListener(heightListener);


        miniplayer = new Miniplayer(this, controlBarController, menuController, mediaInterface, settingsController);


        mediaInterface.embeddedMediaPlayer.videoSurface().set(new ImageViewVideoSurface(miniplayer.miniplayerController.videoImageView));


        if(mediaInterface.mediaActive.get()) {
            boolean playValue = mediaInterface.playing.get();
            mediaInterface.embeddedMediaPlayer.controls().stop();
            mediaInterface.embeddedMediaPlayer.media().startPaused(menuController.activeItem.getMediaItem().getFile().getAbsolutePath());
            mediaInterface.seek(Duration.seconds(controlBarController.durationSlider.getValue()));
            if (playValue) {
                mediaInterface.embeddedMediaPlayer.controls().play();
            }
            else {
                mediaInterface.embeddedMediaPlayer.controls().nextFrame();
            }
        }

        videoImageView.setImage(null);


        miniplayer.miniplayerController.moveIndicators();
        captionsController.moveToMiniplayer();

        if(menuController.activeItem != null){
            miniplayerActiveText.setVisible(true);
        }

        controlBarController.mouseEventTracker.move();
    }

    public void closeMiniplayer(){

        if(controlBarController.durationSlider.isValueChanging()) return;

        miniplayer.miniplayerController.videoImageViewInnerWrapper.widthProperty().removeListener(miniplayer.miniplayerController.widthListener);
        miniplayer.miniplayerController.videoImageViewInnerWrapper.heightProperty().removeListener(miniplayer.miniplayerController.heightListener);


        if(miniplayerActive && miniplayer != null && miniplayer.stage != null){
            miniplayer.stage.close();
        }


        actionIndicator.moveToMainplayer();
        forwardsIndicator.moveToMainplayer();
        backwardsIndicator.moveToMainplayer();
        valueIndicator.moveToMainplayer();

        captionsController.moveToMainplayer();

        miniplayerActive = false;

        controlBarController.mouseEventTracker.move();


        resizeIndicators();
        repositionValueIndicator();

        videoImageViewInnerWrapper.widthProperty().addListener(widthListener);
        videoImageViewInnerWrapper.heightProperty().addListener(heightListener);

        miniplayerActiveText.setVisible(false);

        mediaInterface.embeddedMediaPlayer.videoSurface().set(new ImageViewVideoSurface(videoImageView));
        if(mediaInterface.mediaActive.get()) {
            boolean playValue = mediaInterface.playing.get();
            mediaInterface.embeddedMediaPlayer.controls().stop();
            mediaInterface.embeddedMediaPlayer.media().startPaused(menuController.activeItem.getMediaItem().getFile().getAbsolutePath());
            mediaInterface.seek(Duration.seconds(controlBarController.durationSlider.getValue()));
            if (playValue) {
                mediaInterface.embeddedMediaPlayer.controls().play();
            }
            else {
                mediaInterface.embeddedMediaPlayer.controls().nextFrame();
            }
        }


        App.stage.setIconified(false);
        App.stage.toFront();

    }

    public void resizeIndicators(){
        if(videoImageViewInnerWrapper.getWidth() < 800){
            captionsController.mediaWidthMultiplier.set(0.4);
            captionsController.resizeCaptions();

            sizeMultiplier.set(0.55);
            forwardsIndicator.resize();
            backwardsIndicator.resize();
            valueIndicator.resize();
        }
        else if(videoImageViewInnerWrapper.getWidth() >= 800 && videoImageViewInnerWrapper.getWidth() < 1200){
            captionsController.mediaWidthMultiplier.set(0.6);
            captionsController.resizeCaptions();

            sizeMultiplier.set(0.65);
            forwardsIndicator.resize();
            backwardsIndicator.resize();
            valueIndicator.resize();

        }
        else if(videoImageViewInnerWrapper.getWidth() >= 1200 && videoImageViewInnerWrapper.getWidth() < 1800){
            captionsController.mediaWidthMultiplier.set(0.8);
            captionsController.resizeCaptions();

            sizeMultiplier.set(0.8);
            forwardsIndicator.resize();
            backwardsIndicator.resize();
            valueIndicator.resize();

        }
        else if(videoImageViewInnerWrapper.getWidth() >= 1800 && videoImageViewInnerWrapper.getWidth() < 2400){
            captionsController.mediaWidthMultiplier.set(1.0);
            captionsController.resizeCaptions();


            sizeMultiplier.set(1);
            forwardsIndicator.resize();
            backwardsIndicator.resize();
            valueIndicator.resize();

        }
        else if(videoImageViewInnerWrapper.getWidth() >= 2400){
            captionsController.mediaWidthMultiplier.set(1.2);
            captionsController.resizeCaptions();

            sizeMultiplier.set(1.2);
            forwardsIndicator.resize();
            backwardsIndicator.resize();
            valueIndicator.resize();
        }
    }

    public void repositionValueIndicator(){
        if(videoImageViewInnerWrapper.getHeight() < 400){

            heightMultiplier.set(0.55);
            valueIndicator.reposition();

        }
        else if((videoImageViewInnerWrapper.getHeight() >= 400 && videoImageViewInnerWrapper.getHeight() < 600)){

            heightMultiplier.set(0.65);
            valueIndicator.reposition();

        }
        else if((videoImageViewInnerWrapper.getHeight() >= 600 && videoImageViewInnerWrapper.getHeight() < 900)){

            heightMultiplier.set(0.8);
            valueIndicator.reposition();

        }
        else if((videoImageViewInnerWrapper.getHeight() >= 900 && videoImageViewInnerWrapper.getHeight() < 1200)){

            heightMultiplier.set(1);
            valueIndicator.reposition();

        }
        else if(videoImageViewInnerWrapper.getHeight() >= 1200){


            heightMultiplier.set(1.2);
            valueIndicator.reposition();
        }
    }




    public void pressRIGHT(KeyEvent e){
        controlBarController.mouseEventTracker.move();

        if(settingsController.settingsState == SettingsState.CUSTOM_SPEED_OPEN){
            // if custom speed pane is open, dont seek video with arrows
            settingsController.playbackSpeedController.customSpeedPane.customSpeedSlider.setValueChanging(true);
            settingsController.playbackSpeedController.customSpeedPane.customSpeedSlider.setValue(settingsController.playbackSpeedController.customSpeedPane.customSpeedSlider.getValue() + 0.05);
            e.consume();
            return;
        }

        if (!controlBarController.volumeSlider.isFocused() && mediaInterface.mediaActive.get()) {

            if(backwardsIndicator.wrapper.isVisible()){
                backwardsIndicator.setVisible(false);
            }
            forwardsIndicator.setText("5 seconds");
            forwardsIndicator.reset();
            forwardsIndicator.setVisible(true);
            forwardsIndicator.animate();

            if (mediaInterface.getCurrentTime().toSeconds() + 5 >= controlBarController.durationSlider.getMax()) {
                mediaInterface.seekedToEnd = true;
            }

            seekingWithKeys = true;
            if(miniplayerActive) {
                miniplayer.miniplayerController.sliderPane.setVisible(true);

                if(captionsController.captionsLocation == Pos.BOTTOM_LEFT || captionsController.captionsLocation == Pos.BOTTOM_CENTER || captionsController.captionsLocation == Pos.BOTTOM_RIGHT){
                    captionsController.captionsBox.setTranslateY(-30);
                }

                miniplayer.miniplayerController.progressBarTimer.playFromStart();
            }

            controlBarController.durationSlider.setValue(controlBarController.durationSlider.getValue() + 5);

            e.consume();

        }
    }

    public void pressLEFT(KeyEvent e){
        controlBarController.mouseEventTracker.move();

        if(settingsController.settingsState == SettingsState.CUSTOM_SPEED_OPEN){
            // if custom speed pane is open, dont seek video with arrows
            settingsController.playbackSpeedController.customSpeedPane.customSpeedSlider.setValueChanging(true);
            settingsController.playbackSpeedController.customSpeedPane.customSpeedSlider.setValue(settingsController.playbackSpeedController.customSpeedPane.customSpeedSlider.getValue() - 0.05);
            e.consume();
            return;
        }

        if (!controlBarController.volumeSlider.isFocused() && mediaInterface.mediaActive.get()) {

            if(forwardsIndicator.wrapper.isVisible()){
                forwardsIndicator.setVisible(false);
            }
            backwardsIndicator.setText("5 seconds");
            backwardsIndicator.reset();
            backwardsIndicator.setVisible(true);
            backwardsIndicator.animate();

            mediaInterface.seekedToEnd = false;

            seekingWithKeys = true;
            if(miniplayerActive) {
                miniplayer.miniplayerController.sliderPane.setVisible(true);

                if(captionsController.captionsLocation == Pos.BOTTOM_LEFT || captionsController.captionsLocation == Pos.BOTTOM_CENTER || captionsController.captionsLocation == Pos.BOTTOM_RIGHT){
                    captionsController.captionsBox.setTranslateY(-30);
                }

                miniplayer.miniplayerController.progressBarTimer.playFromStart();
            }
            controlBarController.durationSlider.setValue(controlBarController.durationSlider.getValue() - 5);
            e.consume();

        }
    }

    public void pressTAB(KeyEvent event){
        controlBarController.mouseEventTracker.move();
        event.consume(); // TODO: replace builtin focus traversal with custom engine
    }

    public void pressM(){
        controlBarController.mouseEventTracker.move();
        if (!controlBarController.muted) {
            controlBarController.mute();
            actionIndicator.setIcon(VOLUME_MUTED);
        } else {
            controlBarController.unmute();
            actionIndicator.setIcon(VOLUME_HIGH);
        }

        valueIndicator.setValue((int) (controlBarController.volumeSlider.getValue()) + "%");
        valueIndicator.play();

        actionIndicator.setVisible(true);
        actionIndicator.animate();
    }

    public void pressSPACE(KeyEvent e){
        controlBarController.mouseEventTracker.move();
        if (!controlBarController.durationSlider.isValueChanging() && mediaInterface.mediaActive.get() && (!miniplayerActive || !miniplayer.miniplayerController.slider.isValueChanging())) { // wont let user play/pause video while media slider is seeking
            if (mediaInterface.atEnd) {
                mediaInterface.replay();
                actionIndicator.setIcon(REPLAY);
            } else {
                if (mediaInterface.playing.get()) {
                    mediaInterface.wasPlaying = false;
                    mediaInterface.pause();
                    actionIndicator.setIcon(PAUSE);
                } else {
                    mediaInterface.play();
                    actionIndicator.setIcon(PLAY);
                }
            }
            actionIndicator.setVisible(true);
            actionIndicator.animate();

            e.consume(); // might have to add a check to consume the space event only if any controlbar buttons are focused (might use space bar to navigate settings or menu)
        }
    }

    public void pressJ(){
        controlBarController.mouseEventTracker.move();

        if (mediaInterface.mediaActive.get()) {
            mediaInterface.seekedToEnd = false;

            if(forwardsIndicator.wrapper.isVisible()){
                forwardsIndicator.setVisible(false);
            }
            backwardsIndicator.setText("10 seconds");
            backwardsIndicator.reset();
            backwardsIndicator.setVisible(true);
            backwardsIndicator.animate();

            seekingWithKeys = true;
            if(miniplayerActive) {
                miniplayer.miniplayerController.sliderPane.setVisible(true);

                if(captionsController.captionsLocation == Pos.BOTTOM_LEFT || captionsController.captionsLocation == Pos.BOTTOM_CENTER || captionsController.captionsLocation == Pos.BOTTOM_RIGHT){
                    captionsController.captionsBox.setTranslateY(-30);
                }

                miniplayer.miniplayerController.progressBarTimer.playFromStart();
            }
            controlBarController.durationSlider.setValue(controlBarController.durationSlider.getValue() - 10.0);
        }
    }

    public void pressK(){
        controlBarController.mouseEventTracker.move();
        if (!controlBarController.durationSlider.isValueChanging() && mediaInterface.mediaActive.get() && (!miniplayerActive || !miniplayer.miniplayerController.slider.isValueChanging())) {  // wont let user play/pause video while media slider is seeking
            if (mediaInterface.atEnd) {
                mediaInterface.replay();
                actionIndicator.setIcon(REPLAY);
            } else {
                if (mediaInterface.playing.get()) {
                    mediaInterface.wasPlaying = false;
                    mediaInterface.pause();
                    actionIndicator.setIcon(PAUSE);
                } else {
                    mediaInterface.play();
                    actionIndicator.setIcon(PLAY);
                }
            }
            actionIndicator.setVisible(true);
            actionIndicator.animate();
        }
    }

    public void pressL(KeyEvent e){
        controlBarController.mouseEventTracker.move();


        if (mediaInterface.mediaActive.get()) {

            if (mediaInterface.getCurrentTime().toSeconds() + 10 >= controlBarController.durationSlider.getMax()) {
                mediaInterface.seekedToEnd = true;
            }

            if(backwardsIndicator.wrapper.isVisible()){
                backwardsIndicator.setVisible(false);
            }
            forwardsIndicator.setText("10 seconds");
            forwardsIndicator.reset();
            forwardsIndicator.setVisible(true);
            forwardsIndicator.animate();


            seekingWithKeys = true;
            if(miniplayerActive) {
                miniplayer.miniplayerController.sliderPane.setVisible(true);

                if(captionsController.captionsLocation == Pos.BOTTOM_LEFT || captionsController.captionsLocation == Pos.BOTTOM_CENTER || captionsController.captionsLocation == Pos.BOTTOM_RIGHT){
                    captionsController.captionsBox.setTranslateY(-30);
                }

                miniplayer.miniplayerController.progressBarTimer.playFromStart();
            }
            controlBarController.durationSlider.setValue(controlBarController.durationSlider.getValue() + 10);
            e.consume();

        }
    }

    public void pressCOMMA(KeyEvent e){
        controlBarController.mouseEventTracker.move();

        if(e.isShiftDown()){ // decrease playback speed by 0.25
            settingsController.playbackSpeedController.customSpeedPane.customSpeedSlider.setValueChanging(true);
            settingsController.playbackSpeedController.customSpeedPane.customSpeedSlider.setValue(settingsController.playbackSpeedController.customSpeedPane.customSpeedSlider.getValue() - 0.25);

            valueIndicator.setValue(settingsController.playbackSpeedController.df.format(settingsController.playbackSpeedController.customSpeedPane.customSpeedSlider.getValue()) + "x");
            valueIndicator.play();

            actionIndicator.setIcon(REWIND);
            actionIndicator.setVisible(true);
            actionIndicator.animate();


            // also show new playback speed as a label top center of the mediaviewpane

            e.consume();
            return;
        }

        // seek backwards by 1 frame
        if(!mediaInterface.playing.get() && mediaInterface.mediaActive.get()) {
            mediaInterface.seekedToEnd = false;
            controlBarController.durationSlider.setValue(controlBarController.durationSlider.getValue() - 0.1);
        }
        e.consume();
    }

    public void pressPERIOD(KeyEvent e){

        controlBarController.mouseEventTracker.move();

        if(e.isShiftDown()){ // increase playback speed by 0.25
            settingsController.playbackSpeedController.customSpeedPane.customSpeedSlider.setValueChanging(true);
            settingsController.playbackSpeedController.customSpeedPane.customSpeedSlider.setValue(settingsController.playbackSpeedController.customSpeedPane.customSpeedSlider.getValue() + 0.25);

            valueIndicator.setValue(settingsController.playbackSpeedController.df.format(settingsController.playbackSpeedController.customSpeedPane.customSpeedSlider.getValue()) + "x");
            valueIndicator.play();

            actionIndicator.setIcon(FORWARD);
            actionIndicator.setVisible(true);
            actionIndicator.animate();


            // also show new playback speed as a label top center of the mediaviewpane

            e.consume();
            return;
        }

        // seek forward by 1 frame
        if(!mediaInterface.playing.get() && mediaInterface.mediaActive.get()){
            if (mediaInterface.getCurrentTime().toSeconds() + 0.1 >= controlBarController.durationSlider.getMax()) {
                mediaInterface.seekedToEnd = true;
            }

            controlBarController.durationSlider.setValue(controlBarController.durationSlider.getValue() + 0.1);


        }
        e.consume();
    }

    public void pressUP(KeyEvent event){
        controlBarController.mouseEventTracker.move();

        if(event.isShiftDown()){
            // change focus between menu items
        }
        else {
            controlBarController.volumeSlider.setValue(Math.min(controlBarController.volumeSlider.getValue() + 5, 100));
            valueIndicator.setValue((int) (controlBarController.volumeSlider.getValue()) + "%");

            valueIndicator.play();

            actionIndicator.setIcon(VOLUME_HIGH);
            actionIndicator.setVisible(true);
            actionIndicator.animate();
        }

        event.consume();
    }

    public void pressDOWN(KeyEvent event){
        controlBarController.mouseEventTracker.move();

        if(event.isShiftDown()){
            // change focus between menu items
        }
        else {
            controlBarController.volumeSlider.setValue(Math.max(controlBarController.volumeSlider.getValue() - 5, 0));
            valueIndicator.setValue((int) (controlBarController.volumeSlider.getValue()) + "%");

            valueIndicator.play();

            if(controlBarController.volumeSlider.getValue() == 0) actionIndicator.setIcon(VOLUME_MUTED);
            else actionIndicator.setIcon(VOLUME_LOW);
            actionIndicator.setVisible(true);
            actionIndicator.animate();
        }

        event.consume();
    }

    public void pressI(){

        if(miniplayerActive) closeMiniplayer();
        else openMiniplayer();
    }

    public void pressQ(){

        if(playbackOptionsPopUp.isShowing()) playbackOptionsPopUp.hide();
        if(menuController.activeMenuItemOptionsPopUp != null && menuController.activeMenuItemOptionsPopUp.isShowing()) menuController.activeMenuItemOptionsPopUp.hide();

        if(menuController.menuState != MenuState.CLOSED){
            menuController.closeMenu();
            controlBarController.mouseEventTracker.move();
        }
        else openMenu();
    }

    public void pressS(){
        controlBarController.mouseEventTracker.move();

        if (settingsController.settingsState != SettingsState.CLOSED) {
            settingsController.closeSettings();
        } else {
            settingsController.openSettings();
        }
    }

    public void pressN(KeyEvent e){
        controlBarController.mouseEventTracker.move();

        if(e.isShiftDown()){

            if((menuController.historyBox.index != -1 && menuController.historyBox.index < menuController.history.size() -1) || ((menuController.historyBox.index == menuController.history.size() -1 || menuController.historyBox.index == -1) && !menuController.queue.isEmpty())){

                if(!menuController.animationsInProgress.isEmpty()) return;

                actionIndicator.setIcon(NEXT_VIDEO);
                actionIndicator.setVisible(true);
                actionIndicator.animate();

                mediaInterface.playNext();
            }

        }
    }

    public void pressP(KeyEvent e){
        controlBarController.mouseEventTracker.move();

        if(e.isShiftDown()){

            if(mediaInterface.mediaActive.get() && controlBarController.durationSlider.getValue() > 5){ // restart current video
                actionIndicator.setIcon(REPLAY);
                actionIndicator.setVisible(true);
                actionIndicator.animate();

                mediaInterface.seekedToEnd = false;
                controlBarController.durationSlider.setValue(0);

            }
            else if((!menuController.history.isEmpty() && menuController.historyBox.index == -1) || menuController.historyBox.index > 0){ // play previous video

                if(!menuController.animationsInProgress.isEmpty()) return;

                actionIndicator.setIcon(PREVIOUS_VIDEO);
                actionIndicator.setVisible(true);
                actionIndicator.animate();

                mediaInterface.playPrevious();
            }

        }
    }

    public void pressF(){
        controlBarController.mouseEventTracker.move();
        controlBarController.toggleFullScreen();

        if(playbackOptionsPopUp.isShowing()) playbackOptionsPopUp.hide();
        if(menuController.activeMenuItemOptionsPopUp != null && menuController.activeMenuItemOptionsPopUp.isShowing()) menuController.activeMenuItemOptionsPopUp.hide();
    }

    public void pressF12(){
        takeScreenshot();
        controlBarController.mouseEventTracker.move();
    }

    public void pressC(){
        controlBarController.mouseEventTracker.move();

        if(!captionsController.captionsSelected || captionsController.captionsDragActive) return;

        if (captionsController.captionsOn.get()) {
            controlBarController.closeCaptions();
        } else {
            controlBarController.openCaptions();
        }

        captionsController.captionsPane.captionsToggle.fire();
    }

    public void press1(){
        controlBarController.mouseEventTracker.move();
        if(mediaInterface.mediaActive.get()){
            controlBarController.durationSlider.setValue(controlBarController.durationSlider.getMax() * 1 / 10);
            seekingWithKeys = true;
            if(miniplayerActive) {
                miniplayer.miniplayerController.sliderPane.setVisible(true);

                if(captionsController.captionsLocation == Pos.BOTTOM_LEFT || captionsController.captionsLocation == Pos.BOTTOM_CENTER || captionsController.captionsLocation == Pos.BOTTOM_RIGHT){
                    captionsController.captionsBox.setTranslateY(-30);
                }

                miniplayer.miniplayerController.progressBarTimer.playFromStart();
            }
        }
    }

    public void press2(){
        controlBarController.mouseEventTracker.move();
        if(mediaInterface.mediaActive.get()){
            controlBarController.durationSlider.setValue(controlBarController.durationSlider.getMax() * 2 / 10);
            seekingWithKeys = true;
            if(miniplayerActive) {
                miniplayer.miniplayerController.sliderPane.setVisible(true);

                if(captionsController.captionsLocation == Pos.BOTTOM_LEFT || captionsController.captionsLocation == Pos.BOTTOM_CENTER || captionsController.captionsLocation == Pos.BOTTOM_RIGHT){
                    captionsController.captionsBox.setTranslateY(-30);
                }

                miniplayer.miniplayerController.progressBarTimer.playFromStart();
            }
        }
    }

    public void press3(){
        controlBarController.mouseEventTracker.move();
        if(mediaInterface.mediaActive.get()){
            controlBarController.durationSlider.setValue(controlBarController.durationSlider.getMax() * 3 / 10);
            seekingWithKeys = true;
            if(miniplayerActive) {
                miniplayer.miniplayerController.sliderPane.setVisible(true);

                if(captionsController.captionsLocation == Pos.BOTTOM_LEFT || captionsController.captionsLocation == Pos.BOTTOM_CENTER || captionsController.captionsLocation == Pos.BOTTOM_RIGHT){
                    captionsController.captionsBox.setTranslateY(-30);
                }

                miniplayer.miniplayerController.progressBarTimer.playFromStart();
            }
        }
    }

    public void press4(){
        controlBarController.mouseEventTracker.move();
        if(mediaInterface.mediaActive.get()){
            controlBarController.durationSlider.setValue(controlBarController.durationSlider.getMax() * 4 / 10);
            seekingWithKeys = true;
            if(miniplayerActive) {
                miniplayer.miniplayerController.sliderPane.setVisible(true);

                if(captionsController.captionsLocation == Pos.BOTTOM_LEFT || captionsController.captionsLocation == Pos.BOTTOM_CENTER || captionsController.captionsLocation == Pos.BOTTOM_RIGHT){
                    captionsController.captionsBox.setTranslateY(-30);
                }

                miniplayer.miniplayerController.progressBarTimer.playFromStart();
            }
        }
    }

    public void press5(){
        controlBarController.mouseEventTracker.move();
        if(mediaInterface.mediaActive.get()){
            controlBarController.durationSlider.setValue(controlBarController.durationSlider.getMax() * 5 / 10);
            seekingWithKeys = true;
            if(miniplayerActive) {
                miniplayer.miniplayerController.sliderPane.setVisible(true);

                if(captionsController.captionsLocation == Pos.BOTTOM_LEFT || captionsController.captionsLocation == Pos.BOTTOM_CENTER || captionsController.captionsLocation == Pos.BOTTOM_RIGHT){
                    captionsController.captionsBox.setTranslateY(-30);
                }

                miniplayer.miniplayerController.progressBarTimer.playFromStart();
            }
        }
    }

    public void press6(){
        controlBarController.mouseEventTracker.move();
        if(mediaInterface.mediaActive.get()){
            controlBarController.durationSlider.setValue(controlBarController.durationSlider.getMax() * 6 / 10);
            seekingWithKeys = true;
            if(miniplayerActive) {
                miniplayer.miniplayerController.sliderPane.setVisible(true);

                if(captionsController.captionsLocation == Pos.BOTTOM_LEFT || captionsController.captionsLocation == Pos.BOTTOM_CENTER || captionsController.captionsLocation == Pos.BOTTOM_RIGHT){
                    captionsController.captionsBox.setTranslateY(-30);
                }

                miniplayer.miniplayerController.progressBarTimer.playFromStart();
            }
        }
    }

    public void press7(){
        controlBarController.mouseEventTracker.move();
        if(mediaInterface.mediaActive.get()){
            controlBarController.durationSlider.setValue(controlBarController.durationSlider.getMax() * 7 / 10);
            seekingWithKeys = true;
            if(miniplayerActive) {
                miniplayer.miniplayerController.sliderPane.setVisible(true);

                if(captionsController.captionsLocation == Pos.BOTTOM_LEFT || captionsController.captionsLocation == Pos.BOTTOM_CENTER || captionsController.captionsLocation == Pos.BOTTOM_RIGHT){
                    captionsController.captionsBox.setTranslateY(-30);
                }

                miniplayer.miniplayerController.progressBarTimer.playFromStart();
            }
        }
    }

    public void press8(){
        controlBarController.mouseEventTracker.move();
        if(mediaInterface.mediaActive.get()){
            controlBarController.durationSlider.setValue(controlBarController.durationSlider.getMax() * 8 / 10);
            seekingWithKeys = true;
            if(miniplayerActive) {
                miniplayer.miniplayerController.sliderPane.setVisible(true);

                if(captionsController.captionsLocation == Pos.BOTTOM_LEFT || captionsController.captionsLocation == Pos.BOTTOM_CENTER || captionsController.captionsLocation == Pos.BOTTOM_RIGHT){
                    captionsController.captionsBox.setTranslateY(-30);
                }

                miniplayer.miniplayerController.progressBarTimer.playFromStart();
            }
        }
    }

    public void press9(){
        controlBarController.mouseEventTracker.move();
        if(mediaInterface.mediaActive.get()){
            controlBarController.durationSlider.setValue(controlBarController.durationSlider.getMax() * 9 / 10);
            seekingWithKeys = true;
            if(miniplayerActive) {
                miniplayer.miniplayerController.sliderPane.setVisible(true);

                if(captionsController.captionsLocation == Pos.BOTTOM_LEFT || captionsController.captionsLocation == Pos.BOTTOM_CENTER || captionsController.captionsLocation == Pos.BOTTOM_RIGHT){
                    captionsController.captionsBox.setTranslateY(-30);
                }

                miniplayer.miniplayerController.progressBarTimer.playFromStart();
            }
        }
    }

    public void press0(){
        controlBarController.mouseEventTracker.move();
        mediaInterface.seekedToEnd = false;
        if(mediaInterface.mediaActive.get()){
            seekingWithKeys = true;
            if(miniplayerActive) {
                miniplayer.miniplayerController.sliderPane.setVisible(true);

                if(captionsController.captionsLocation == Pos.BOTTOM_LEFT || captionsController.captionsLocation == Pos.BOTTOM_CENTER || captionsController.captionsLocation == Pos.BOTTOM_RIGHT){
                    captionsController.captionsBox.setTranslateY(-30);
                }

                miniplayer.miniplayerController.progressBarTimer.playFromStart();
            }
            controlBarController.durationSlider.setValue(0);
            actionIndicator.setIcon(REPLAY);
            actionIndicator.setVisible(true);
            actionIndicator.animate();
        }
    }

    public void pressESCAPE(){
        captionsController.cancelDrag();

        controlBarController.mouseEventTracker.move();
        if (settingsController.settingsState != SettingsState.CLOSED && !App.fullScreen) {
            settingsController.closeSettings();
        }
        App.fullScreen = false;

        if(playbackOptionsPopUp.isShowing()) playbackOptionsPopUp.hide();
        if(menuController.activeMenuItemOptionsPopUp != null && menuController.activeMenuItemOptionsPopUp.isShowing()) menuController.activeMenuItemOptionsPopUp.hide();

        controlBarController.fullScreenIcon.setShape(controlBarController.maximizeSVG);
        App.stage.setFullScreen(false);

        if (settingsController.settingsState == SettingsState.CLOSED)
            controlBarController.fullScreen = new ControlTooltip(this,"Full screen (f)", controlBarController.fullScreenButton, 0, true);
    }

    public void pressEND(){
        controlBarController.mouseEventTracker.move();
        mediaInterface.seekedToEnd = true;
        if(mediaInterface.mediaActive.get()){
            seekingWithKeys = true;
            if(miniplayerActive) {
                miniplayer.miniplayerController.sliderPane.setVisible(true);

                if(captionsController.captionsLocation == Pos.BOTTOM_LEFT || captionsController.captionsLocation == Pos.BOTTOM_CENTER || captionsController.captionsLocation == Pos.BOTTOM_RIGHT){
                    captionsController.captionsBox.setTranslateY(-30);
                }

                miniplayer.miniplayerController.progressBarTimer.playFromStart();
            }
            controlBarController.durationSlider.setValue(controlBarController.durationSlider.getMax());
            actionIndicator.setIcon(NEXT_VIDEO);
            actionIndicator.setVisible(true);
            actionIndicator.animate();
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