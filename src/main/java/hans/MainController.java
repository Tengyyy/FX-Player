package hans;

import hans.Captions.CaptionsController;
import hans.Captions.CaptionsState;
import hans.Chapters.ChapterController;
import hans.MediaItems.MediaUtilities;
import hans.Menu.MenuController;
import hans.Menu.MenuState;
import hans.Menu.QueueItem;
import hans.Settings.SettingsController;
import hans.Settings.SettingsState;
import javafx.animation.Animation;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;
import uk.co.caprica.vlcj.javafx.videosurface.ImageViewVideoSurface;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import static hans.SVG.*;


public class MainController implements Initializable {

    @FXML
    public ImageView videoImageView, seekImageView, coverBackground, coverImage;

    @FXML
    StackPane outerPane;
    @FXML
    public
    StackPane videoImageViewWrapper;
    @FXML
    public StackPane videoImageViewInnerWrapper, addYoutubeVideoWindowContainer;
    @FXML
    StackPane coverImageContainer, coverFilter, coverImageWrapper;

    @FXML
    private ControlBarController controlBarController;

    @FXML
    private MenuController menuController;

    SettingsController settingsController;
    CaptionsController captionsController;

    MediaInterface mediaInterface;

    public ChapterController chapterController;

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
    public Miniplayer miniplayer;

    ChangeListener<? super Number> widthListener;
    ChangeListener<? super Number> heightListener;

    ChangeListener<? super Number> widthListenerForTitle;


    String snapshotDirectory;

    StackPane videoTitleBox = new StackPane();
    public Label videoTitleLabel = new Label();


    public SliderHoverLabel sliderHoverLabel;
    public SliderHoverPreview sliderHoverPreview;


    StackPane videoTitleBackground = new StackPane();
    Button menuButton = new Button();
    public StackPane menuButtonPane = new StackPane();
    Region menuIcon = new Region();

    SVGPath metadataPath = new SVGPath();
    StackPane metadataButtonPane = new StackPane();
    Button metadataButton = new Button();
    Region metadataIcon = new Region();

    WindowsTaskBarController windowsTaskBarController;

    public AddYoutubeVideoWindow addYoutubeVideoWindow;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

        settingsController = new SettingsController(this, controlBarController, menuController);
        captionsController = new CaptionsController(settingsController, this, controlBarController, menuController);
        mediaInterface = new MediaInterface(this, controlBarController, settingsController, menuController, captionsController);
        chapterController = new ChapterController(this, controlBarController, menuController, mediaInterface);

        controlBarController.init(this, settingsController, menuController, mediaInterface, captionsController, chapterController); // shares references of all the controllers between eachother
        menuController.init(this, controlBarController, settingsController, mediaInterface, captionsController, chapterController);
        settingsController.init(mediaInterface, captionsController);
        captionsController.init(mediaInterface);
        mediaInterface.init(chapterController);

        addYoutubeVideoWindow = new AddYoutubeVideoWindow(this);

        sliderHoverLabel = new SliderHoverLabel(videoImageViewWrapper, controlBarController, false);
        sliderHoverPreview = new SliderHoverPreview(videoImageViewWrapper, controlBarController);

        videoImageViewWrapper.getChildren().add(2, captionsController.captionsBuffer);
        videoImageViewWrapper.getChildren().add(3, settingsController.settingsBuffer);


        snapshotDirectory = System.getProperty("user.home").concat("/FXPlayer/screenshots/");


        // declaring media control images
        menuSVG = new SVGPath();
        menuSVG.setContent(App.svgMap.get(MENU));

        sizeMultiplier.set(0.65);

        actionIndicator = new ActionIndicator(this);
        forwardsIndicator = new SeekIndicator(this, true);
        backwardsIndicator = new SeekIndicator(this, false);
        valueIndicator = new ValueIndicator(this);


        // Make mediaView adjust to frame size

        videoImageView.fitWidthProperty().bind(videoImageViewInnerWrapper.widthProperty());
        seekImageView.fitWidthProperty().bind(videoImageViewInnerWrapper.widthProperty());
        Platform.runLater(() -> {
            videoImageView.fitHeightProperty().bind(videoImageViewInnerWrapper.getScene().heightProperty());
            seekImageView.fitHeightProperty().bind(videoImageViewInnerWrapper.getScene().heightProperty());
        });

        videoImageView.setPreserveRatio(true);


        //hide controlbar when mouse exits window
        Platform.runLater(() -> videoImageViewWrapper.getScene().setOnMouseExited(e -> controlBarController.mouseEventTracker.hide()));

        videoImageViewWrapper.setStyle("-fx-background-color: rgb(0,0,0)");
        videoImageViewInnerWrapper.setStyle("-fx-background-color: rgb(0,0,0)");


        miniplayerActiveText.setText("Media active in miniplayer");
        miniplayerActiveText.setId("mediaViewText");
        miniplayerActiveText.setBackground(Background.EMPTY);
        miniplayerActiveText.setMouseTransparent(true);
        miniplayerActiveText.setVisible(false);
        StackPane.setAlignment(miniplayerActiveText, Pos.CENTER);

        videoImageViewInnerWrapper.getChildren().addAll(controlBarController.controlBarBackground, videoTitleBackground, miniplayerActiveText, videoTitleBox);

        Platform.runLater(() -> {            // needs to be run later so that the rest of the app can load in and this tooltip popup has a parent window to be associated with
            openMenuTooltip = new ControlTooltip(this,"Open menu (q)", menuButton, 0, TooltipType.MENU_TOOLTIP);
            viewMetadataTooltip = new ControlTooltip(this,"Media metadata", metadataButton, 0, TooltipType.MENU_TOOLTIP);

            videoImageViewWrapper.sceneProperty().get().widthProperty().addListener((observableValue, oldValue, newValue) -> {
                if(newValue.doubleValue() < menuController.menu.getMaxWidth()){
                    menuController.menu.setMaxWidth(newValue.doubleValue());
                }
            });


        });

        widthListener = (observableValue, oldValue, newValue) -> {

            if(newValue.doubleValue() < 800){
                captionsController.captionsBox.mediaWidthMultiplier.set(0.4);

                sizeMultiplier.set(0.55);
                if(actionIndicator.wrapper.isVisible()) actionIndicator.updateSize();
                forwardsIndicator.resize();
                backwardsIndicator.resize();
                valueIndicator.resize();
            }
            else if((newValue.doubleValue() >= 800 && newValue.doubleValue() < 1200)){
                captionsController.captionsBox.mediaWidthMultiplier.set(0.6);

                sizeMultiplier.set(0.65);
                if(actionIndicator.wrapper.isVisible()) actionIndicator.updateSize();
                forwardsIndicator.resize();
                backwardsIndicator.resize();
                valueIndicator.resize();

            }
            else if((newValue.doubleValue() >= 1200 && newValue.doubleValue() < 1800)){
                captionsController.captionsBox.mediaWidthMultiplier.set(0.8);

                sizeMultiplier.set(0.8);
                if(actionIndicator.wrapper.isVisible()) actionIndicator.updateSize();
                forwardsIndicator.resize();
                backwardsIndicator.resize();
                valueIndicator.resize();

            }
            else if((newValue.doubleValue() >= 1800 && newValue.doubleValue() < 2400)){
                captionsController.captionsBox.mediaWidthMultiplier.set(1.0);


                sizeMultiplier.set(1);
                if(actionIndicator.wrapper.isVisible()) actionIndicator.updateSize();
                forwardsIndicator.resize();
                backwardsIndicator.resize();
                valueIndicator.resize();

            }
            else if(newValue.doubleValue() >= 2400){
                captionsController.captionsBox.mediaWidthMultiplier.set(1.2);

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
            if(captionsController.captionsBox.captionsDragActive){
                if(e.getY() - captionsController.captionsBox.dragPositionY <= captionsController.captionsBox.minimumY) captionsController.captionsBox.captionsContainer.setTranslateY(((captionsController.captionsBox.startY - captionsController.captionsBox.minimumY) * -1) + captionsController.captionsBox.startTranslateY);
                else if(e.getY() - captionsController.captionsBox.dragPositionY + captionsController.captionsBox.captionsContainer.getLayoutBounds().getMaxY() > captionsController.captionsBox.maximumY) captionsController.captionsBox.captionsContainer.setTranslateY(captionsController.captionsBox.maximumY - captionsController.captionsBox.startY - captionsController.captionsBox.captionsContainer.getLayoutBounds().getMaxY() + captionsController.captionsBox.startTranslateY);
                else captionsController.captionsBox.captionsContainer.setTranslateY(e.getY() - captionsController.captionsBox.dragPositionY - captionsController.captionsBox.startY + captionsController.captionsBox.startTranslateY);

                if(e.getX() - captionsController.captionsBox.dragPositionX <= captionsController.captionsBox.minimumX) captionsController.captionsBox.captionsContainer.setTranslateX(((captionsController.captionsBox.startX - captionsController.captionsBox.minimumX) * -1) + captionsController.captionsBox.startTranslateX);
                else if(e.getX() - captionsController.captionsBox.dragPositionX + captionsController.captionsBox.captionsContainer.getLayoutBounds().getMaxX() > captionsController.captionsBox.maximumX) captionsController.captionsBox.captionsContainer.setTranslateX(captionsController.captionsBox.maximumX - captionsController.captionsBox.startX - captionsController.captionsBox.captionsContainer.getLayoutBounds().getMaxX() + captionsController.captionsBox.startTranslateX);
                else captionsController.captionsBox.captionsContainer.setTranslateX(e.getX() - captionsController.captionsBox.dragPositionX - captionsController.captionsBox.startX + captionsController.captionsBox.startTranslateX);
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
            if(captionsController.captionsState != CaptionsState.CLOSED) captionsController.closeCaptions();
            e.consume();
        });

        videoTitleLabel.setOnMouseEntered(e -> AnimationsClass.animateTextColor(videoTitleLabel, Color.rgb(255, 255, 255), 200));

        videoTitleLabel.setOnMouseExited(e -> AnimationsClass.animateTextColor(videoTitleLabel, Color.rgb(200, 200,200), 200));

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


        metadataButton.setPrefSize(50, 50);
        metadataButton.setMaxSize(50, 50);
        metadataButton.setBackground(Background.EMPTY);
        metadataButton.setCursor(Cursor.HAND);

        metadataPath.setContent(App.svgMap.get(INFORMATION));
        metadataIcon.setShape(metadataPath);
        metadataIcon.setPrefSize(25, 25);
        metadataIcon.setMaxSize(25, 25);
        metadataIcon.setMouseTransparent(true);
        metadataIcon.getStyleClass().add("controlIcon");

        coverImageContainer.setVisible(false);
        coverImageContainer.setMouseTransparent(true);
        coverImageContainer.setStyle("-fx-background-color: black;");
        coverImageContainer.setMinHeight(0);


        coverBackground.fitWidthProperty().bind(videoImageViewWrapper.widthProperty());
        coverBackground.fitHeightProperty().bind(videoImageViewWrapper.heightProperty());
        coverBackground.setPreserveRatio(false);
        coverBackground.setEffect(new GaussianBlur(100));


        Rectangle rectangle = new Rectangle();
        rectangle.widthProperty().bind(coverImage.fitWidthProperty());
        rectangle.heightProperty().bind(coverImage.fitHeightProperty());
        rectangle.setArcWidth(40);
        rectangle.setArcHeight(40);
        coverImage.setClip(rectangle);

        coverImageWrapper.maxWidthProperty().bind(coverImage.fitWidthProperty());
        coverImageWrapper.maxHeightProperty().bind(coverImage.fitHeightProperty());
        coverImageWrapper.setEffect(new DropShadow(20, Color.BLACK));
    }

    public void mediaClick(MouseEvent e) {

        // Clicking on the mediaview node will close the settings tab if its open or
        // otherwise play/pause/replay the video

        if(e.getButton() == MouseButton.SECONDARY){
            if (settingsController.settingsState != SettingsState.CLOSED) settingsController.closeSettings();
            if (captionsController.captionsState != CaptionsState.CLOSED) captionsController.closeCaptions();
            if(menuController.activeMenuItemContextMenu != null && menuController.activeMenuItemContextMenu.isShowing()) menuController.activeMenuItemContextMenu.hide();
            return;
        }

        if(menuController.activeMenuItemContextMenu != null && menuController.activeMenuItemContextMenu.isShowing()){
            return;
        }


        if(menuController.menuState != MenuState.CLOSED && !menuController.menuInTransition){
            menuController.closeMenu();
        }
        else if (settingsController.settingsState != SettingsState.CLOSED) {
            settingsController.closeSettings();
        }
        else if (captionsController.captionsState != CaptionsState.CLOSED) {
            captionsController.closeCaptions();
        }
        else if(mediaInterface.mediaActive.get()){
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

        if(menuController.menuInTransition
                || controlBarController.durationSlider.isValueChanging()
                || controlBarController.volumeSlider.isValueChanging()
                || settingsController.playbackSpeedController.customSpeedPane.customSpeedSlider.isValueChanging()
                || captionsController.captionsBox.captionsDragActive
                || settingsController.equalizerController.sliderActive) return;

        menuController.menuInTransition = true;
        menuController.menuState = MenuState.QUEUE_OPEN;

        if(menuController.queueBox.activeItem.get() != null){
            double heightViewPort = menuController.queueScroll.getViewportBounds().getHeight();
            double heightScrollPane = menuController.queueScroll.getContent().getBoundsInLocal().getHeight();
            double y = menuController.queueBox.activeItem.get().getBoundsInParent().getMaxY();
            if (y<(heightViewPort/2)){
                menuController.queueScroll.setVvalue(0);
            }
            else if ((y>=(heightViewPort/2))&(y<=(heightScrollPane-heightViewPort/2))){
                menuController.queueScroll.setVvalue((y-(heightViewPort/2))/(heightScrollPane-heightViewPort));
            }
            else if( y>= (heightScrollPane-(heightViewPort/2))){
                menuController.queueScroll.setVvalue(1);
            }
        }

        if(settingsController.settingsState != SettingsState.CLOSED) settingsController.closeSettings();
        if (captionsController.captionsState != CaptionsState.CLOSED) captionsController.closeCaptions();

        controlBarController.controlBarWrapper.setMouseTransparent(true);

        if(controlBarController.controlBarOpen) AnimationsClass.hideControls(controlBarController, captionsController, this);

        videoImageViewWrapper.getScene().setCursor(Cursor.DEFAULT);

        AnimationsClass.openMenu(menuController, this);

        captionsController.captionsBox.captionsContainer.setMouseTransparent(true);


    }

    public void handleDragEntered(DragEvent e){
        if(e.getDragboard().getFiles().isEmpty() || menuController.queueBox.itemDragActive.get() || menuController.queueBox.draggedNode != null) return;
        File file = e.getDragboard().getFiles().get(0);
        if(!MediaUtilities.mediaFormats.contains(Utilities.getFileExtension(file))) return;

        actionIndicator.setIcon(PLUS);
        actionIndicator.setVisible(true);

        if (settingsController.settingsState != SettingsState.CLOSED) settingsController.closeSettings();
        if (captionsController.captionsState != CaptionsState.CLOSED) captionsController.closeCaptions();

        AnimationsClass.hideControls(controlBarController, captionsController, this);

    }

    public void handleDragExited(){
        if(actionIndicator.parallelTransition.getStatus() != Animation.Status.RUNNING) actionIndicator.setVisible(false);
    }

    public void handleDragOver(DragEvent e){
        if(e.getDragboard().getFiles().isEmpty()) return;

        File file = e.getDragboard().getFiles().get(0);
        if(!Utilities.getFileExtension(file).equals("mp4") &&
                !Utilities.getFileExtension(file).equals("mp3") &&
                !Utilities.getFileExtension(file).equals("wav") &&
                !Utilities.getFileExtension(file).equals("mov") &&
                !Utilities.getFileExtension(file).equals("mkv") &&
                !Utilities.getFileExtension(file).equals("flv") &&
                !Utilities.getFileExtension(file).equals("flac")&&
                !Utilities.getFileExtension(file).equals("avi") &&
                !Utilities.getFileExtension(file).equals("opus") &&
                !Utilities.getFileExtension(file).equals("aiff") &&
                !Utilities.getFileExtension(file).equals("m4a") &&
                !Utilities.getFileExtension(file).equals("wma") &&
                !Utilities.getFileExtension(file).equals("aac") &&
                !Utilities.getFileExtension(file).equals("ogg")) return;

        e.acceptTransferModes(TransferMode.COPY);
    }

    public void handleDragDropped(DragEvent e){

        if(e.getDragboard().getFiles().isEmpty()) return;

        File file = e.getDragboard().getFiles().get(0);

        actionIndicator.animate();

        QueueItem queueItem = new QueueItem(file, menuController, mediaInterface, 0);
        if(menuController.queueBox.activeItem.get() != null) menuController.queueBox.add(menuController.queueBox.activeIndex.get() + 1, queueItem, false);
        else menuController.queueBox.add(0, queueItem, false);
        queueItem.play();

    }

    public void takeScreenshot(){
        if(menuController.queueBox.activeItem.get() == null || !menuController.queueBox.activeItem.get().getMediaItemGenerated().get() || miniplayerActive) return;

        // snapshot file name formatting
        String out = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss").format(new Date());
        String videoName = menuController.queueBox.activeItem.get().videoTitle.getText();

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
            if(!playValue){
                miniplayer.miniplayerController.seekImageView.setImage(videoImageView.getImage());
                miniplayer.miniplayerController.seekImageView.setVisible(true);
            }
            mediaInterface.embeddedMediaPlayer.controls().stop();
            mediaInterface.embeddedMediaPlayer.media().startPaused(menuController.queueBox.activeItem.get().getMediaItem().getFile().getAbsolutePath());
            mediaInterface.seek(Duration.seconds(controlBarController.durationSlider.getValue()));

            if (playValue) {
                mediaInterface.embeddedMediaPlayer.controls().play();
            }

            controlBarController.updateProgress(controlBarController.durationSlider.getValue()/controlBarController.durationSlider.getMax());

            if(menuController.queueBox.activeItem.get().getMediaItem() != null && !menuController.queueBox.activeItem.get().getMediaItem().hasVideo()){
                setCoverImageView(menuController.queueBox.activeItem.get());
            }
        }

        videoImageView.setImage(null);
        seekImageView.setImage(null);
        seekImageView.setVisible(false);


        miniplayer.miniplayerController.moveIndicators();
        captionsController.captionsBox.moveToMiniplayer();

        if(menuController.queueBox.activeItem.get() != null && menuController.queueBox.activeItem.get().getMediaItem().hasVideo()){
            miniplayerActiveText.setVisible(true);
        }

        controlBarController.mouseEventTracker.move();
    }

    public void closeMiniplayer(){

        if(controlBarController.durationSlider.isValueChanging() || miniplayer.miniplayerController.slider.isValueChanging()) return;

        miniplayer.miniplayerController.videoImageViewInnerWrapper.widthProperty().removeListener(miniplayer.miniplayerController.widthListener);
        miniplayer.miniplayerController.videoImageViewInnerWrapper.heightProperty().removeListener(miniplayer.miniplayerController.heightListener);

        Image image = null;
        if(mediaInterface.mediaActive.get() && !mediaInterface.playing.get()) image = miniplayer.miniplayerController.videoImageView.getImage();

        if(miniplayerActive && miniplayer != null && miniplayer.stage != null){
            miniplayer.stage.close();
        }


        actionIndicator.moveToMainplayer();
        forwardsIndicator.moveToMainplayer();
        backwardsIndicator.moveToMainplayer();
        valueIndicator.moveToMainplayer();

        captionsController.captionsBox.moveToMainplayer();

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
            mediaInterface.embeddedMediaPlayer.media().startPaused(menuController.queueBox.activeItem.get().getMediaItem().getFile().getAbsolutePath());
            mediaInterface.seek(Duration.seconds(controlBarController.durationSlider.getValue()));

            if (playValue) mediaInterface.embeddedMediaPlayer.controls().play();
            else if (image != null) {
                seekImageView.setImage(image);
                seekImageView.setVisible(true);
            }

            controlBarController.updateProgress(controlBarController.durationSlider.getValue()/controlBarController.durationSlider.getMax());

        }

        App.stage.setIconified(false);
        App.stage.toFront();
    }

    public void resizeIndicators(){
        if(videoImageViewInnerWrapper.getWidth() < 800){
            captionsController.captionsBox.mediaWidthMultiplier.set(0.4);

            sizeMultiplier.set(0.55);
            forwardsIndicator.resize();
            backwardsIndicator.resize();
            valueIndicator.resize();
        }
        else if(videoImageViewInnerWrapper.getWidth() >= 800 && videoImageViewInnerWrapper.getWidth() < 1200){
            captionsController.captionsBox.mediaWidthMultiplier.set(0.6);

            sizeMultiplier.set(0.65);
            forwardsIndicator.resize();
            backwardsIndicator.resize();
            valueIndicator.resize();

        }
        else if(videoImageViewInnerWrapper.getWidth() >= 1200 && videoImageViewInnerWrapper.getWidth() < 1800){
            captionsController.captionsBox.mediaWidthMultiplier.set(0.8);

            sizeMultiplier.set(0.8);
            forwardsIndicator.resize();
            backwardsIndicator.resize();
            valueIndicator.resize();

        }
        else if(videoImageViewInnerWrapper.getWidth() >= 1800 && videoImageViewInnerWrapper.getWidth() < 2400){
            captionsController.captionsBox.mediaWidthMultiplier.set(1.0);


            sizeMultiplier.set(1);
            forwardsIndicator.resize();
            backwardsIndicator.resize();
            valueIndicator.resize();

        }
        else if(videoImageViewInnerWrapper.getWidth() >= 2400){
            captionsController.captionsBox.mediaWidthMultiplier.set(1.2);

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


    public void setCoverImageView(QueueItem queueItem){


        Image image;
        if(queueItem.getMediaItem().hasCover()){
            image = queueItem.getMediaItem().getCover();
        }
        else image = queueItem.getMediaItem().getPlaceholderCover();

        double width = image.getWidth();
        double height = image.getHeight();
        double ratio = width/height;

        if(queueItem.getMediaItem().hasCover()){
            coverBackground.setImage(image);
        }
        else {
            coverBackground.setImage(null);
        }

        coverImage.fitWidthProperty().bind(Bindings.min(width*2, Bindings.min(videoImageViewWrapper.widthProperty().multiply(0.6), videoImageViewWrapper.heightProperty().multiply(0.6).multiply(ratio))));
        coverImage.fitHeightProperty().bind(Bindings.min(height*2, coverImage.fitWidthProperty().divide(ratio)));
        coverImage.setImage(image);

        coverImageContainer.setVisible(true);

        if(miniplayerActive){

            if(queueItem.getMediaItem().hasCover()){
                miniplayer.miniplayerController.coverBackground.setImage(image);
            }
            else {
                miniplayer.miniplayerController.coverBackground.setImage(null);
            }

            miniplayer.miniplayerController.coverImage.fitWidthProperty().bind(Bindings.min(width*2, Bindings.min(miniplayer.miniplayerController.videoImageViewWrapper.widthProperty().multiply(0.7), miniplayer.miniplayerController.videoImageViewWrapper.heightProperty().multiply(0.7).multiply(ratio))));
            miniplayer.miniplayerController.coverImage.fitHeightProperty().bind(Bindings.min(height*2, miniplayer.miniplayerController.coverImage.fitWidthProperty().divide(ratio)));
            miniplayer.miniplayerController.coverImage.setImage(image);

            miniplayer.miniplayerController.coverImageContainer.setVisible(true);
        }

    }

    public void addTaskBarButtons(){
        //windowsTaskBarController = new WindowsTaskBarController(menuController, mediaInterface);
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

            if (controlBarController.durationSlider.getValue() + 5 >= controlBarController.durationSlider.getMax()) {
                mediaInterface.seekedToEnd = true;
            }

            seekingWithKeys = true;
            if(miniplayerActive) {
                miniplayer.miniplayerController.sliderPane.setVisible(true);

                if(captionsController.captionsBox.captionsLocation == Pos.BOTTOM_LEFT || captionsController.captionsBox.captionsLocation == Pos.BOTTOM_CENTER || captionsController.captionsBox.captionsLocation == Pos.BOTTOM_RIGHT){
                    captionsController.captionsBox.captionsContainer.setTranslateY(-30);
                }

                miniplayer.miniplayerController.progressBarTimer.playFromStart();
            }

            controlBarController.durationSlider.setValue(controlBarController.durationSlider.getValue() + 5);

            if(!controlBarController.durationSlider.isValueChanging() && (!miniplayerActive || !miniplayer.miniplayerController.slider.isValueChanging())){
                seekImageView.setImage(null);
                seekImageView.setVisible(false);

                if(miniplayerActive){
                    miniplayer.miniplayerController.videoImageView.setVisible(true);
                    miniplayer.miniplayerController.seekImageView.setVisible(false);
                    miniplayer.miniplayerController.seekImageView.setImage(null);
                }
                else videoImageView.setVisible(true);
            }

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

                if(captionsController.captionsBox.captionsLocation == Pos.BOTTOM_LEFT || captionsController.captionsBox.captionsLocation == Pos.BOTTOM_CENTER || captionsController.captionsBox.captionsLocation == Pos.BOTTOM_RIGHT){
                    captionsController.captionsBox.captionsContainer.setTranslateY(-30);
                }

                miniplayer.miniplayerController.progressBarTimer.playFromStart();
            }
            controlBarController.durationSlider.setValue(controlBarController.durationSlider.getValue() - 5);

            if(!controlBarController.durationSlider.isValueChanging() && (!miniplayerActive || !miniplayer.miniplayerController.slider.isValueChanging())){
                seekImageView.setImage(null);
                seekImageView.setVisible(false);

                if(miniplayerActive){
                    miniplayer.miniplayerController.videoImageView.setVisible(true);
                    miniplayer.miniplayerController.seekImageView.setVisible(false);
                    miniplayer.miniplayerController.seekImageView.setImage(null);
                }
                else videoImageView.setVisible(true);
            }


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

                if(captionsController.captionsBox.captionsLocation == Pos.BOTTOM_LEFT || captionsController.captionsBox.captionsLocation == Pos.BOTTOM_CENTER || captionsController.captionsBox.captionsLocation == Pos.BOTTOM_RIGHT){
                    captionsController.captionsBox.captionsContainer.setTranslateY(-30);
                }

                miniplayer.miniplayerController.progressBarTimer.playFromStart();
            }
            controlBarController.durationSlider.setValue(controlBarController.durationSlider.getValue() - 10.0);

            if(!controlBarController.durationSlider.isValueChanging() && (!miniplayerActive || !miniplayer.miniplayerController.slider.isValueChanging())){
                seekImageView.setImage(null);
                seekImageView.setVisible(false);

                if(miniplayerActive){
                    miniplayer.miniplayerController.videoImageView.setVisible(true);
                    miniplayer.miniplayerController.seekImageView.setVisible(false);
                    miniplayer.miniplayerController.seekImageView.setImage(null);
                }
                else videoImageView.setVisible(true);
            }
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

                if(captionsController.captionsBox.captionsLocation == Pos.BOTTOM_LEFT || captionsController.captionsBox.captionsLocation == Pos.BOTTOM_CENTER || captionsController.captionsBox.captionsLocation == Pos.BOTTOM_RIGHT){
                    captionsController.captionsBox.captionsContainer.setTranslateY(-30);
                }

                miniplayer.miniplayerController.progressBarTimer.playFromStart();
            }
            controlBarController.durationSlider.setValue(controlBarController.durationSlider.getValue() + 10);

            if(!controlBarController.durationSlider.isValueChanging() && (!miniplayerActive || !miniplayer.miniplayerController.slider.isValueChanging())){
                seekImageView.setImage(null);
                seekImageView.setVisible(false);

                if(miniplayerActive){
                    miniplayer.miniplayerController.videoImageView.setVisible(true);
                    miniplayer.miniplayerController.seekImageView.setVisible(false);
                    miniplayer.miniplayerController.seekImageView.setImage(null);
                }
                else videoImageView.setVisible(true);
            }

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


            e.consume();
            return;
        }

        // seek backwards by 100 milliseconds
        if(!mediaInterface.playing.get() && mediaInterface.mediaActive.get()) {
            mediaInterface.seekedToEnd = false;
            controlBarController.durationSlider.setValue(controlBarController.durationSlider.getValue() - 0.1);

            if(!controlBarController.durationSlider.isValueChanging() && (!miniplayerActive || !miniplayer.miniplayerController.slider.isValueChanging())){
                seekImageView.setImage(null);
                seekImageView.setVisible(false);

                if(miniplayerActive){
                    miniplayer.miniplayerController.videoImageView.setVisible(true);
                    miniplayer.miniplayerController.seekImageView.setVisible(false);
                    miniplayer.miniplayerController.seekImageView.setImage(null);
                }
                else videoImageView.setVisible(true);
            }
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

        // seek forward by 100 milliseconds
        if(!mediaInterface.playing.get() && mediaInterface.mediaActive.get()){
            if (mediaInterface.getCurrentTime().toSeconds() + 0.1 >= controlBarController.durationSlider.getMax()) {
                mediaInterface.seekedToEnd = true;
            }

            controlBarController.durationSlider.setValue(controlBarController.durationSlider.getValue() + 0.1);


            if(!controlBarController.durationSlider.isValueChanging() && (!miniplayerActive || !miniplayer.miniplayerController.slider.isValueChanging())){
                seekImageView.setImage(null);
                seekImageView.setVisible(false);

                if(miniplayerActive){
                    miniplayer.miniplayerController.videoImageView.setVisible(true);
                    miniplayer.miniplayerController.seekImageView.setVisible(false);
                    miniplayer.miniplayerController.seekImageView.setImage(null);
                }
                else videoImageView.setVisible(true);
            }
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

        if(addYoutubeVideoWindow.showing || menuController.queueBox.itemDragActive.get()) return;

        if(menuController.activeMenuItemContextMenu != null && menuController.activeMenuItemContextMenu.showing) menuController.activeMenuItemContextMenu.hide();
        if(menuController.addOptionsContextMenu.showing) menuController.addOptionsContextMenu.hide();

        if(menuController.menuState != MenuState.CLOSED && !menuController.menuInTransition){
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
            pressNextTrack();
        }
    }

    public void pressP(KeyEvent e){
        controlBarController.mouseEventTracker.move();

        if(e.isShiftDown()){
            pressPreviousTrack();
        }
    }

    public void pressPreviousTrack(){
        controlBarController.mouseEventTracker.move();

        if(mediaInterface.mediaActive.get() && controlBarController.durationSlider.getValue() > 5){ // restart current video
            actionIndicator.setIcon(REPLAY);
            actionIndicator.setVisible(true);
            actionIndicator.animate();

            mediaInterface.seekedToEnd = false;
            controlBarController.durationSlider.setValue(0);

        }
        else if(menuController.queueBox.activeItem.get() != null && menuController.queueBox.activeIndex.get() > 0){ // play previous video

            if(menuController.queueBox.itemDragActive.get()) return;

            actionIndicator.setIcon(PREVIOUS_VIDEO);
            actionIndicator.setVisible(true);
            actionIndicator.animate();

            mediaInterface.playPrevious();
        }
    }

    public void pressNextTrack(){
        controlBarController.mouseEventTracker.move();

        if(!menuController.queueBox.queue.isEmpty() && (menuController.queueBox.activeItem.get() == null || menuController.queueBox.queue.size() > menuController.queueBox.activeIndex.get() + 1)){

            if(menuController.queueBox.itemDragActive.get()) return;

            actionIndicator.setIcon(NEXT_VIDEO);
            actionIndicator.setVisible(true);
            actionIndicator.animate();

            mediaInterface.playNext();
        }
    }

    public void pressF(){
        controlBarController.mouseEventTracker.move();
        controlBarController.toggleFullScreen();

        if(menuController.activeMenuItemContextMenu != null && menuController.activeMenuItemContextMenu.showing) menuController.activeMenuItemContextMenu.hide();
        if(menuController.addOptionsContextMenu.showing) menuController.addOptionsContextMenu.hide();
    }

    public void pressF12(){
        takeScreenshot();
        controlBarController.mouseEventTracker.move();
    }

    public void pressC(){
        controlBarController.mouseEventTracker.move();

        if(captionsController.captionsState != CaptionsState.CLOSED) captionsController.closeCaptions();
        else captionsController.openCaptions();
    }

    public void press1(){
        controlBarController.mouseEventTracker.move();
        if(mediaInterface.mediaActive.get()){
            controlBarController.durationSlider.setValue(controlBarController.durationSlider.getMax() * 1 / 10);

            if(!controlBarController.durationSlider.isValueChanging() && (!miniplayerActive || !miniplayer.miniplayerController.slider.isValueChanging())){
                seekImageView.setImage(null);
                seekImageView.setVisible(false);

                if(miniplayerActive){
                    miniplayer.miniplayerController.videoImageView.setVisible(true);
                    miniplayer.miniplayerController.seekImageView.setVisible(false);
                    miniplayer.miniplayerController.seekImageView.setImage(null);
                }
                else videoImageView.setVisible(true);
            }

            seekingWithKeys = true;
            if(miniplayerActive) {
                miniplayer.miniplayerController.sliderPane.setVisible(true);

                if(captionsController.captionsBox.captionsLocation == Pos.BOTTOM_LEFT || captionsController.captionsBox.captionsLocation == Pos.BOTTOM_CENTER || captionsController.captionsBox.captionsLocation == Pos.BOTTOM_RIGHT){
                    captionsController.captionsBox.captionsContainer.setTranslateY(-30);
                }

                miniplayer.miniplayerController.progressBarTimer.playFromStart();
            }
        }
    }

    public void press2(){
        controlBarController.mouseEventTracker.move();
        if(mediaInterface.mediaActive.get()){
            controlBarController.durationSlider.setValue(controlBarController.durationSlider.getMax() * 2 / 10);

            if(!controlBarController.durationSlider.isValueChanging() && (!miniplayerActive || !miniplayer.miniplayerController.slider.isValueChanging())){
                seekImageView.setImage(null);
                seekImageView.setVisible(false);

                if(miniplayerActive){
                    miniplayer.miniplayerController.videoImageView.setVisible(true);
                    miniplayer.miniplayerController.seekImageView.setVisible(false);
                    miniplayer.miniplayerController.seekImageView.setImage(null);
                }
                else videoImageView.setVisible(true);
            }

            seekingWithKeys = true;
            if(miniplayerActive) {
                miniplayer.miniplayerController.sliderPane.setVisible(true);

                if(captionsController.captionsBox.captionsLocation == Pos.BOTTOM_LEFT || captionsController.captionsBox.captionsLocation == Pos.BOTTOM_CENTER || captionsController.captionsBox.captionsLocation == Pos.BOTTOM_RIGHT){
                    captionsController.captionsBox.captionsContainer.setTranslateY(-30);
                }

                miniplayer.miniplayerController.progressBarTimer.playFromStart();
            }
        }
    }

    public void press3(){
        controlBarController.mouseEventTracker.move();
        if(mediaInterface.mediaActive.get()){
            controlBarController.durationSlider.setValue(controlBarController.durationSlider.getMax() * 3 / 10);

            if(!controlBarController.durationSlider.isValueChanging() && (!miniplayerActive || !miniplayer.miniplayerController.slider.isValueChanging())){
                seekImageView.setImage(null);
                seekImageView.setVisible(false);

                if(miniplayerActive){
                    miniplayer.miniplayerController.videoImageView.setVisible(true);
                    miniplayer.miniplayerController.seekImageView.setVisible(false);
                    miniplayer.miniplayerController.seekImageView.setImage(null);
                }
                else videoImageView.setVisible(true);
            }

            seekingWithKeys = true;
            if(miniplayerActive) {
                miniplayer.miniplayerController.sliderPane.setVisible(true);

                if(captionsController.captionsBox.captionsLocation == Pos.BOTTOM_LEFT || captionsController.captionsBox.captionsLocation == Pos.BOTTOM_CENTER || captionsController.captionsBox.captionsLocation == Pos.BOTTOM_RIGHT){
                    captionsController.captionsBox.captionsContainer.setTranslateY(-30);
                }

                miniplayer.miniplayerController.progressBarTimer.playFromStart();
            }
        }
    }

    public void press4(){
        controlBarController.mouseEventTracker.move();
        if(mediaInterface.mediaActive.get()){
            controlBarController.durationSlider.setValue(controlBarController.durationSlider.getMax() * 4 / 10);

            if(!controlBarController.durationSlider.isValueChanging() && (!miniplayerActive || !miniplayer.miniplayerController.slider.isValueChanging())){
                seekImageView.setImage(null);
                seekImageView.setVisible(false);

                if(miniplayerActive){
                    miniplayer.miniplayerController.videoImageView.setVisible(true);
                    miniplayer.miniplayerController.seekImageView.setVisible(false);
                    miniplayer.miniplayerController.seekImageView.setImage(null);
                }
                else videoImageView.setVisible(true);
            }

            seekingWithKeys = true;
            if(miniplayerActive) {
                miniplayer.miniplayerController.sliderPane.setVisible(true);

                if(captionsController.captionsBox.captionsLocation == Pos.BOTTOM_LEFT || captionsController.captionsBox.captionsLocation == Pos.BOTTOM_CENTER || captionsController.captionsBox.captionsLocation == Pos.BOTTOM_RIGHT){
                    captionsController.captionsBox.captionsContainer.setTranslateY(-30);
                }

                miniplayer.miniplayerController.progressBarTimer.playFromStart();
            }
        }
    }

    public void press5(){
        controlBarController.mouseEventTracker.move();
        if(mediaInterface.mediaActive.get()){
            controlBarController.durationSlider.setValue(controlBarController.durationSlider.getMax() * 5 / 10);

            if(!controlBarController.durationSlider.isValueChanging() && (!miniplayerActive || !miniplayer.miniplayerController.slider.isValueChanging())){
                seekImageView.setImage(null);
                seekImageView.setVisible(false);

                if(miniplayerActive){
                    miniplayer.miniplayerController.videoImageView.setVisible(true);
                    miniplayer.miniplayerController.seekImageView.setVisible(false);
                    miniplayer.miniplayerController.seekImageView.setImage(null);
                }
                else videoImageView.setVisible(true);
            }

            seekingWithKeys = true;
            if(miniplayerActive) {
                miniplayer.miniplayerController.sliderPane.setVisible(true);

                if(captionsController.captionsBox.captionsLocation == Pos.BOTTOM_LEFT || captionsController.captionsBox.captionsLocation == Pos.BOTTOM_CENTER || captionsController.captionsBox.captionsLocation == Pos.BOTTOM_RIGHT){
                    captionsController.captionsBox.captionsContainer.setTranslateY(-30);
                }

                miniplayer.miniplayerController.progressBarTimer.playFromStart();
            }
        }
    }

    public void press6(){
        controlBarController.mouseEventTracker.move();
        if(mediaInterface.mediaActive.get()){
            controlBarController.durationSlider.setValue(controlBarController.durationSlider.getMax() * 6 / 10);

            if(!controlBarController.durationSlider.isValueChanging() && (!miniplayerActive || !miniplayer.miniplayerController.slider.isValueChanging())){
                seekImageView.setImage(null);
                seekImageView.setVisible(false);

                if(miniplayerActive){
                    miniplayer.miniplayerController.videoImageView.setVisible(true);
                    miniplayer.miniplayerController.seekImageView.setVisible(false);
                    miniplayer.miniplayerController.seekImageView.setImage(null);
                }
                else videoImageView.setVisible(true);
            }

            seekingWithKeys = true;
            if(miniplayerActive) {
                miniplayer.miniplayerController.sliderPane.setVisible(true);

                if(captionsController.captionsBox.captionsLocation == Pos.BOTTOM_LEFT || captionsController.captionsBox.captionsLocation == Pos.BOTTOM_CENTER || captionsController.captionsBox.captionsLocation == Pos.BOTTOM_RIGHT){
                    captionsController.captionsBox.captionsContainer.setTranslateY(-30);
                }

                miniplayer.miniplayerController.progressBarTimer.playFromStart();
            }
        }
    }

    public void press7(){
        controlBarController.mouseEventTracker.move();
        if(mediaInterface.mediaActive.get()){
            controlBarController.durationSlider.setValue(controlBarController.durationSlider.getMax() * 7 / 10);

            if(!controlBarController.durationSlider.isValueChanging() && (!miniplayerActive || !miniplayer.miniplayerController.slider.isValueChanging())){
                seekImageView.setImage(null);
                seekImageView.setVisible(false);

                if(miniplayerActive){
                    miniplayer.miniplayerController.videoImageView.setVisible(true);
                    miniplayer.miniplayerController.seekImageView.setVisible(false);
                    miniplayer.miniplayerController.seekImageView.setImage(null);
                }
                else videoImageView.setVisible(true);
            }

            seekingWithKeys = true;
            if(miniplayerActive) {
                miniplayer.miniplayerController.sliderPane.setVisible(true);

                if(captionsController.captionsBox.captionsLocation == Pos.BOTTOM_LEFT || captionsController.captionsBox.captionsLocation == Pos.BOTTOM_CENTER || captionsController.captionsBox.captionsLocation == Pos.BOTTOM_RIGHT){
                    captionsController.captionsBox.captionsContainer.setTranslateY(-30);
                }

                miniplayer.miniplayerController.progressBarTimer.playFromStart();
            }
        }
    }

    public void press8(){
        controlBarController.mouseEventTracker.move();
        if(mediaInterface.mediaActive.get()){
            controlBarController.durationSlider.setValue(controlBarController.durationSlider.getMax() * 8 / 10);

            if(!controlBarController.durationSlider.isValueChanging() && (!miniplayerActive || !miniplayer.miniplayerController.slider.isValueChanging())){
                seekImageView.setImage(null);
                seekImageView.setVisible(false);

                if(miniplayerActive){
                    miniplayer.miniplayerController.videoImageView.setVisible(true);
                    miniplayer.miniplayerController.seekImageView.setVisible(false);
                    miniplayer.miniplayerController.seekImageView.setImage(null);
                }
                else videoImageView.setVisible(true);
            }

            seekingWithKeys = true;
            if(miniplayerActive) {
                miniplayer.miniplayerController.sliderPane.setVisible(true);

                if(captionsController.captionsBox.captionsLocation == Pos.BOTTOM_LEFT || captionsController.captionsBox.captionsLocation == Pos.BOTTOM_CENTER || captionsController.captionsBox.captionsLocation == Pos.BOTTOM_RIGHT){
                    captionsController.captionsBox.captionsContainer.setTranslateY(-30);
                }

                miniplayer.miniplayerController.progressBarTimer.playFromStart();
            }
        }
    }

    public void press9(){
        controlBarController.mouseEventTracker.move();
        if(mediaInterface.mediaActive.get()){
            controlBarController.durationSlider.setValue(controlBarController.durationSlider.getMax() * 9 / 10);

            if(!controlBarController.durationSlider.isValueChanging() && (!miniplayerActive || !miniplayer.miniplayerController.slider.isValueChanging())){
                seekImageView.setImage(null);
                seekImageView.setVisible(false);

                if(miniplayerActive){
                    miniplayer.miniplayerController.videoImageView.setVisible(true);
                    miniplayer.miniplayerController.seekImageView.setVisible(false);
                    miniplayer.miniplayerController.seekImageView.setImage(null);
                }
                else videoImageView.setVisible(true);
            }

            seekingWithKeys = true;
            if(miniplayerActive) {
                miniplayer.miniplayerController.sliderPane.setVisible(true);

                if(captionsController.captionsBox.captionsLocation == Pos.BOTTOM_LEFT || captionsController.captionsBox.captionsLocation == Pos.BOTTOM_CENTER || captionsController.captionsBox.captionsLocation == Pos.BOTTOM_RIGHT){
                    captionsController.captionsBox.captionsContainer.setTranslateY(-30);
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

                if(captionsController.captionsBox.captionsLocation == Pos.BOTTOM_LEFT || captionsController.captionsBox.captionsLocation == Pos.BOTTOM_CENTER || captionsController.captionsBox.captionsLocation == Pos.BOTTOM_RIGHT){
                    captionsController.captionsBox.captionsContainer.setTranslateY(-30);
                }

                miniplayer.miniplayerController.progressBarTimer.playFromStart();
            }
            controlBarController.durationSlider.setValue(0);

            if(!controlBarController.durationSlider.isValueChanging() && (!miniplayerActive || !miniplayer.miniplayerController.slider.isValueChanging())){
                seekImageView.setImage(null);
                seekImageView.setVisible(false);

                if(miniplayerActive){
                    miniplayer.miniplayerController.videoImageView.setVisible(true);
                    miniplayer.miniplayerController.seekImageView.setVisible(false);
                    miniplayer.miniplayerController.seekImageView.setImage(null);
                }
                else videoImageView.setVisible(true);
            }

            actionIndicator.setIcon(REPLAY);
            actionIndicator.setVisible(true);
            actionIndicator.animate();
        }
    }

    public void pressESCAPE(){
        captionsController.captionsBox.cancelDrag();

        controlBarController.mouseEventTracker.move();
        if (settingsController.settingsState != SettingsState.CLOSED && !App.fullScreen) {
            settingsController.closeSettings();
        }
        else if (captionsController.captionsState != CaptionsState.CLOSED && !App.fullScreen) {
            captionsController.closeCaptions();
        }

        App.fullScreen = false;

        if(menuController.activeMenuItemContextMenu != null && menuController.activeMenuItemContextMenu.showing) menuController.activeMenuItemContextMenu.hide();
        if(menuController.addOptionsContextMenu.showing) menuController.addOptionsContextMenu.hide();

        controlBarController.fullScreenIcon.setShape(controlBarController.maximizeSVG);
        App.stage.setFullScreen(false);

        if (settingsController.settingsState == SettingsState.CLOSED && captionsController.captionsState == CaptionsState.CLOSED)
            controlBarController.fullScreen = new ControlTooltip(this,"Full screen (f)", controlBarController.fullScreenButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
    }

    public void pressEND(){
        controlBarController.mouseEventTracker.move();
        mediaInterface.seekedToEnd = true;
        if(mediaInterface.mediaActive.get()){
            seekingWithKeys = true;
            if(miniplayerActive) {
                miniplayer.miniplayerController.sliderPane.setVisible(true);

                if(captionsController.captionsBox.captionsLocation == Pos.BOTTOM_LEFT || captionsController.captionsBox.captionsLocation == Pos.BOTTOM_CENTER || captionsController.captionsBox.captionsLocation == Pos.BOTTOM_RIGHT){
                    captionsController.captionsBox.captionsContainer.setTranslateY(-30);
                }

                miniplayer.miniplayerController.progressBarTimer.playFromStart();
            }
            controlBarController.durationSlider.setValue(controlBarController.durationSlider.getMax());

            if(!controlBarController.durationSlider.isValueChanging() && (!miniplayerActive || !miniplayer.miniplayerController.slider.isValueChanging())){
                seekImageView.setImage(null);
                seekImageView.setVisible(false);

                if(miniplayerActive){
                    miniplayer.miniplayerController.videoImageView.setVisible(true);
                    miniplayer.miniplayerController.seekImageView.setVisible(false);
                    miniplayer.miniplayerController.seekImageView.setImage(null);
                }
                else videoImageView.setVisible(true);
            }

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