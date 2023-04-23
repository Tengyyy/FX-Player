package hans;

import hans.Dialogs.*;
import hans.Menu.Settings.Action;
import hans.Subtitles.SubtitlesController;
import hans.Subtitles.SubtitlesState;
import hans.Chapters.ChapterController;
import hans.MediaItems.MediaUtilities;
import hans.Menu.MenuController;
import hans.Menu.MenuState;
import hans.Menu.Queue.QueueItem;
import hans.PlaybackSettings.PlaybackSettingsController;
import hans.PlaybackSettings.PlaybackSettingsState;
import javafx.animation.Animation;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
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
    public StackPane videoImageViewInnerWrapper, popupWindowContainer;
    @FXML
    StackPane coverImageContainer, coverFilter, coverImageWrapper;

    @FXML
    private ControlBarController controlBarController;

    @FXML
    private MenuController menuController;

    PlaybackSettingsController playbackSettingsController;
    SubtitlesController subtitlesController;

    MediaInterface mediaInterface;

    public HotkeyController hotkeyController;

    public ChapterController chapterController;

    public ControlTooltip openMenuTooltip;
    ControlTooltip viewMetadataTooltip;

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

    public StackPane videoTitleBox = new StackPane();
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
    public CloseConfirmationWindow closeConfirmationWindow;
    public HotkeyChangeWindow hotkeyChangeWindow;
    public LicenseWindow licenseWindow;
    public ThirdPartySoftwareWindow thirdPartySoftwareWindow;


    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

        playbackSettingsController = new PlaybackSettingsController(this, controlBarController, menuController);
        subtitlesController = new SubtitlesController(playbackSettingsController, this, controlBarController, menuController);
        mediaInterface = new MediaInterface(this, controlBarController, playbackSettingsController, menuController, subtitlesController);
        chapterController = new ChapterController(this, controlBarController, menuController, mediaInterface);

        controlBarController.init(this, playbackSettingsController, menuController, mediaInterface, subtitlesController, chapterController); // shares references of all the controllers between eachother
        menuController.init(this, controlBarController, playbackSettingsController, mediaInterface, subtitlesController, chapterController);
        playbackSettingsController.init(mediaInterface, subtitlesController);
        subtitlesController.init(mediaInterface);
        mediaInterface.init(chapterController);

        popupWindowContainer.setId("popupWindowContainer");
        popupWindowContainer.setOpacity(0);
        popupWindowContainer.setMouseTransparent(true);

        addYoutubeVideoWindow = new AddYoutubeVideoWindow(this);
        closeConfirmationWindow = new CloseConfirmationWindow(this);
        hotkeyChangeWindow = new HotkeyChangeWindow(this);
        licenseWindow = new LicenseWindow(this);
        thirdPartySoftwareWindow = new ThirdPartySoftwareWindow(this);

        sliderHoverLabel = new SliderHoverLabel(videoImageViewWrapper, controlBarController, false);
        sliderHoverPreview = new SliderHoverPreview(videoImageViewWrapper, controlBarController);

        videoImageViewWrapper.getChildren().add(5, subtitlesController.subtitlesBuffer);
        videoImageViewWrapper.getChildren().add(6, playbackSettingsController.playbackSettingsBuffer);


        snapshotDirectory = System.getProperty("user.home").concat("/FXPlayer/screenshots/");


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
        videoImageViewInnerWrapper.setViewOrder(Double.MAX_VALUE);


        miniplayerActiveText.setText("Media active in miniplayer");
        miniplayerActiveText.setId("mediaViewText");
        miniplayerActiveText.setBackground(Background.EMPTY);
        miniplayerActiveText.setMouseTransparent(true);
        miniplayerActiveText.setVisible(false);
        StackPane.setAlignment(miniplayerActiveText, Pos.CENTER);

        videoImageViewInnerWrapper.getChildren().addAll(controlBarController.controlBarBackground, videoTitleBackground, miniplayerActiveText, videoTitleBox);

        Platform.runLater(() -> {
            videoImageViewWrapper.sceneProperty().get().widthProperty().addListener((observableValue, oldValue, newValue) -> {
                double newWidth = Math.max(menuController.MIN_WIDTH, (newValue.doubleValue() + 30)/2);
                if(!menuController.extended.get() && newWidth < menuController.menu.getMaxWidth()){
                    menuController.menu.setMaxWidth(newWidth);
                    menuController.menu.setPrefWidth(newWidth);
                }
            });
        });

        widthListener = (observableValue, oldValue, newValue) -> {

            if(newValue.doubleValue() < 800){
                subtitlesController.subtitlesBox.mediaWidthMultiplier.set(0.4);

                sizeMultiplier.set(0.55);
                if(actionIndicator.wrapper.isVisible()) actionIndicator.updateSize();
                forwardsIndicator.resize();
                backwardsIndicator.resize();
                valueIndicator.resize();
            }
            else if((newValue.doubleValue() >= 800 && newValue.doubleValue() < 1200)){
                subtitlesController.subtitlesBox.mediaWidthMultiplier.set(0.6);

                sizeMultiplier.set(0.65);
                if(actionIndicator.wrapper.isVisible()) actionIndicator.updateSize();
                forwardsIndicator.resize();
                backwardsIndicator.resize();
                valueIndicator.resize();

            }
            else if((newValue.doubleValue() >= 1200 && newValue.doubleValue() < 1800)){
                subtitlesController.subtitlesBox.mediaWidthMultiplier.set(0.8);

                sizeMultiplier.set(0.8);
                if(actionIndicator.wrapper.isVisible()) actionIndicator.updateSize();
                forwardsIndicator.resize();
                backwardsIndicator.resize();
                valueIndicator.resize();

            }
            else if((newValue.doubleValue() >= 1800 && newValue.doubleValue() < 2400)){
                subtitlesController.subtitlesBox.mediaWidthMultiplier.set(1.0);


                sizeMultiplier.set(1);
                if(actionIndicator.wrapper.isVisible()) actionIndicator.updateSize();
                forwardsIndicator.resize();
                backwardsIndicator.resize();
                valueIndicator.resize();

            }
            else if(newValue.doubleValue() >= 2400){
                subtitlesController.subtitlesBox.mediaWidthMultiplier.set(1.2);

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
            if(subtitlesController.subtitlesBox.subtitlesDragActive){
                if(e.getY() - subtitlesController.subtitlesBox.dragPositionY <= subtitlesController.subtitlesBox.minimumY) subtitlesController.subtitlesBox.subtitlesContainer.setTranslateY(((subtitlesController.subtitlesBox.startY - subtitlesController.subtitlesBox.minimumY) * -1) + subtitlesController.subtitlesBox.startTranslateY);
                else if(e.getY() - subtitlesController.subtitlesBox.dragPositionY + subtitlesController.subtitlesBox.subtitlesContainer.getLayoutBounds().getMaxY() > subtitlesController.subtitlesBox.maximumY) subtitlesController.subtitlesBox.subtitlesContainer.setTranslateY(subtitlesController.subtitlesBox.maximumY - subtitlesController.subtitlesBox.startY - subtitlesController.subtitlesBox.subtitlesContainer.getLayoutBounds().getMaxY() + subtitlesController.subtitlesBox.startTranslateY);
                else subtitlesController.subtitlesBox.subtitlesContainer.setTranslateY(e.getY() - subtitlesController.subtitlesBox.dragPositionY - subtitlesController.subtitlesBox.startY + subtitlesController.subtitlesBox.startTranslateY);

                if(e.getX() - subtitlesController.subtitlesBox.dragPositionX <= subtitlesController.subtitlesBox.minimumX) subtitlesController.subtitlesBox.subtitlesContainer.setTranslateX(((subtitlesController.subtitlesBox.startX - subtitlesController.subtitlesBox.minimumX) * -1) + subtitlesController.subtitlesBox.startTranslateX);
                else if(e.getX() - subtitlesController.subtitlesBox.dragPositionX + subtitlesController.subtitlesBox.subtitlesContainer.getLayoutBounds().getMaxX() > subtitlesController.subtitlesBox.maximumX) subtitlesController.subtitlesBox.subtitlesContainer.setTranslateX(subtitlesController.subtitlesBox.maximumX - subtitlesController.subtitlesBox.startX - subtitlesController.subtitlesBox.subtitlesContainer.getLayoutBounds().getMaxX() + subtitlesController.subtitlesBox.startTranslateX);
                else subtitlesController.subtitlesBox.subtitlesContainer.setTranslateX(e.getX() - subtitlesController.subtitlesBox.dragPositionX - subtitlesController.subtitlesBox.startX + subtitlesController.subtitlesBox.startTranslateX);
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
        menuButton.setOnAction(e -> menuController.queuePage.enter());


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
            if(playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) playbackSettingsController.closeSettings();
            if(subtitlesController.subtitlesState != SubtitlesState.CLOSED) subtitlesController.closeSubtitles();
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
            if (playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) playbackSettingsController.closeSettings();
            if (subtitlesController.subtitlesState != SubtitlesState.CLOSED) subtitlesController.closeSubtitles();
            if(menuController.queuePage.activeQueueItemContextMenu != null && menuController.queuePage.activeQueueItemContextMenu.isShowing()) menuController.queuePage.activeQueueItemContextMenu.hide();
            return;
        }

        if(menuController.queuePage.activeQueueItemContextMenu != null && menuController.queuePage.activeQueueItemContextMenu.isShowing()){
            return;
        }


        if(menuController.menuState != MenuState.CLOSED && !menuController.menuInTransition){
            menuController.closeMenu();
        }
        else if (playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) {
            playbackSettingsController.closeSettings();
        }
        else if (subtitlesController.subtitlesState != SubtitlesState.CLOSED) {
            subtitlesController.closeSubtitles();
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

    public void handleDragEntered(DragEvent e){
        if(e.getDragboard().getFiles().isEmpty() || menuController.queuePage.queueBox.itemDragActive.get() || menuController.queuePage.queueBox.draggedNode != null) return;
        File file = e.getDragboard().getFiles().get(0);
        if(!MediaUtilities.mediaFormats.contains(Utilities.getFileExtension(file))) return;

        actionIndicator.setIcon(PLUS);
        actionIndicator.setVisible(true);

        if (playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) playbackSettingsController.closeSettings();
        if (subtitlesController.subtitlesState != SubtitlesState.CLOSED) subtitlesController.closeSubtitles();

        AnimationsClass.hideControlsAndTitle(controlBarController, subtitlesController, this);

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

        QueueItem queueItem = new QueueItem(file, menuController.queuePage, menuController, mediaInterface, 0);
        if(menuController.queuePage.queueBox.activeItem.get() != null) menuController.queuePage.queueBox.add(menuController.queuePage.queueBox.activeIndex.get() + 1, queueItem, false);
        else menuController.queuePage.queueBox.add(0, queueItem, false);
        queueItem.play();

    }

    public void takeScreenshot(){
        if(menuController.queuePage.queueBox.activeItem.get() == null || !menuController.queuePage.queueBox.activeItem.get().getMediaItemGenerated().get() || miniplayerActive) return;

        // snapshot file name formatting
        String out = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss").format(new Date());
        String videoName = menuController.queuePage.queueBox.activeItem.get().videoTitle.getText();

        mediaInterface.embeddedMediaPlayer.snapshots().save(new File(snapshotDirectory.concat(videoName).concat(" ").concat(out).concat(".png")));
    }

    public void openMiniplayer(){

        if(controlBarController.durationSlider.isValueChanging()) return;

        miniplayerActive = true;

        if(App.fullScreen) controlBarController.toggleFullScreen();

        videoImageViewInnerWrapper.widthProperty().removeListener(widthListener);
        videoImageViewInnerWrapper.heightProperty().removeListener(heightListener);


        miniplayer = new Miniplayer(this, controlBarController, menuController, mediaInterface, playbackSettingsController);
        miniplayer.miniplayerController.videoImageView.requestFocus();


        mediaInterface.embeddedMediaPlayer.videoSurface().set(new ImageViewVideoSurface(miniplayer.miniplayerController.videoImageView));


        if(mediaInterface.mediaActive.get()) {
            boolean playValue = mediaInterface.playing.get();
            if(!playValue){
                miniplayer.miniplayerController.seekImageView.setImage(videoImageView.getImage());
                miniplayer.miniplayerController.seekImageView.setVisible(true);
            }
            mediaInterface.embeddedMediaPlayer.controls().stop();
            mediaInterface.embeddedMediaPlayer.media().startPaused(menuController.queuePage.queueBox.activeItem.get().getMediaItem().getFile().getAbsolutePath());
            mediaInterface.seek(Duration.seconds(controlBarController.durationSlider.getValue()));

            if (playValue) {
                mediaInterface.embeddedMediaPlayer.controls().play();
            }

            controlBarController.updateProgress(controlBarController.durationSlider.getValue()/controlBarController.durationSlider.getMax());

            if(menuController.queuePage.queueBox.activeItem.get().getMediaItem() != null && !menuController.queuePage.queueBox.activeItem.get().getMediaItem().hasVideo()){
                setCoverImageView(menuController.queuePage.queueBox.activeItem.get());
            }
        }

        videoImageView.setImage(null);
        seekImageView.setImage(null);
        seekImageView.setVisible(false);


        miniplayer.miniplayerController.moveIndicators();
        subtitlesController.subtitlesBox.moveToMiniplayer();

        if(menuController.queuePage.queueBox.activeItem.get() != null && menuController.queuePage.queueBox.activeItem.get().getMediaItem().hasVideo()){
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

        subtitlesController.subtitlesBox.moveToMainplayer();

        miniplayerActive = false;

        controlBarController.mouseEventTracker.move();


        resizeIndicators();
        repositionValueIndicator();
        videoImageView.requestFocus();

        videoImageViewInnerWrapper.widthProperty().addListener(widthListener);
        videoImageViewInnerWrapper.heightProperty().addListener(heightListener);

        miniplayerActiveText.setVisible(false);

        mediaInterface.embeddedMediaPlayer.videoSurface().set(new ImageViewVideoSurface(videoImageView));
        if(mediaInterface.mediaActive.get()) {
            boolean playValue = mediaInterface.playing.get();
            mediaInterface.embeddedMediaPlayer.controls().stop();
            mediaInterface.embeddedMediaPlayer.media().startPaused(menuController.queuePage.queueBox.activeItem.get().getMediaItem().getFile().getAbsolutePath());
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
            subtitlesController.subtitlesBox.mediaWidthMultiplier.set(0.4);

            sizeMultiplier.set(0.55);
            forwardsIndicator.resize();
            backwardsIndicator.resize();
            valueIndicator.resize();
        }
        else if(videoImageViewInnerWrapper.getWidth() >= 800 && videoImageViewInnerWrapper.getWidth() < 1200){
            subtitlesController.subtitlesBox.mediaWidthMultiplier.set(0.6);

            sizeMultiplier.set(0.65);
            forwardsIndicator.resize();
            backwardsIndicator.resize();
            valueIndicator.resize();

        }
        else if(videoImageViewInnerWrapper.getWidth() >= 1200 && videoImageViewInnerWrapper.getWidth() < 1800){
            subtitlesController.subtitlesBox.mediaWidthMultiplier.set(0.8);

            sizeMultiplier.set(0.8);
            forwardsIndicator.resize();
            backwardsIndicator.resize();
            valueIndicator.resize();

        }
        else if(videoImageViewInnerWrapper.getWidth() >= 1800 && videoImageViewInnerWrapper.getWidth() < 2400){
            subtitlesController.subtitlesBox.mediaWidthMultiplier.set(1.0);


            sizeMultiplier.set(1);
            forwardsIndicator.resize();
            backwardsIndicator.resize();
            valueIndicator.resize();

        }
        else if(videoImageViewInnerWrapper.getWidth() >= 2400){
            subtitlesController.subtitlesBox.mediaWidthMultiplier.set(1.2);

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

    public void pressTAB(KeyEvent event){
        controlBarController.mouseEventTracker.move();

        if(subtitlesController.subtitlesState == SubtitlesState.OPENSUBTITLES_OPEN){
            if(event.isShiftDown()) subtitlesController.openSubtitlesPane.focusBackward();
            else subtitlesController.openSubtitlesPane.focusForward();
        }

        event.consume();
    }

    public void pressESCAPE(){
        subtitlesController.subtitlesBox.cancelDrag();

        controlBarController.mouseEventTracker.move();
        if (playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED && !App.fullScreen) {
            playbackSettingsController.closeSettings();
        }
        else if (subtitlesController.subtitlesState != SubtitlesState.CLOSED && !App.fullScreen) {
            subtitlesController.closeSubtitles();
        }
        else if(!App.fullScreen && menuController.settingsPage.settingsMenu.showing) menuController.settingsPage.settingsMenu.hide();

        App.fullScreen = false;

        if(menuController.queuePage.activeQueueItemContextMenu != null && menuController.queuePage.activeQueueItemContextMenu.showing) menuController.queuePage.activeQueueItemContextMenu.hide();
        if(menuController.queuePage.addOptionsContextMenu.showing) menuController.queuePage.addOptionsContextMenu.hide();
        if(subtitlesController.openSubtitlesPane.searchOptionsContextMenu.showing) subtitlesController.openSubtitlesPane.searchOptionsContextMenu.hide();

        controlBarController.fullScreenIcon.setShape(controlBarController.maximizeSVG);
        App.stage.setFullScreen(false);

        if (playbackSettingsController.playbackSettingsState == PlaybackSettingsState.CLOSED && subtitlesController.subtitlesState == SubtitlesState.CLOSED)
            controlBarController.fullScreen.updateActionText("Full screen");
    }

    public void pressEnter(){
        if(subtitlesController.subtitlesState != SubtitlesState.OPENSUBTITLES_OPEN) return;

        subtitlesController.openSubtitlesPane.searchButton.fire();
    }

    public void PLAY_PAUSEAction(){
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
        }
    }

    public void MUTEAction(){
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


    public void VOLUME_UP5Action(){
        controlBarController.mouseEventTracker.move();

        controlBarController.volumeSlider.setValue(Math.min(controlBarController.volumeSlider.getValue() + 5, 100));
        valueIndicator.setValue((int) (controlBarController.volumeSlider.getValue()) + "%");

        valueIndicator.play();

        actionIndicator.setIcon(VOLUME_HIGH);
        actionIndicator.setVisible(true);
        actionIndicator.animate();
    }

    public void VOLUME_DOWN5Action(){
        controlBarController.mouseEventTracker.move();

        controlBarController.volumeSlider.setValue(Math.max(controlBarController.volumeSlider.getValue() - 5, 0));
        valueIndicator.setValue((int) (controlBarController.volumeSlider.getValue()) + "%");

        valueIndicator.play();

        if(controlBarController.volumeSlider.getValue() == 0) actionIndicator.setIcon(VOLUME_MUTED);
        else actionIndicator.setIcon(VOLUME_LOW);
        actionIndicator.setVisible(true);
        actionIndicator.animate();
    }

    public void VOLUME_UP1Action(){
        controlBarController.mouseEventTracker.move();

        controlBarController.volumeSlider.setValue(Math.min(controlBarController.volumeSlider.getValue() + 1, 100));
        valueIndicator.setValue((int) (controlBarController.volumeSlider.getValue()) + "%");

        valueIndicator.play();

        actionIndicator.setIcon(VOLUME_HIGH);
        actionIndicator.setVisible(true);
        actionIndicator.animate();
    }

    public void VOLUME_DOWN1Action(){
        controlBarController.mouseEventTracker.move();

        controlBarController.volumeSlider.setValue(Math.max(controlBarController.volumeSlider.getValue() - 1, 0));
        valueIndicator.setValue((int) (controlBarController.volumeSlider.getValue()) + "%");

        valueIndicator.play();

        if(controlBarController.volumeSlider.getValue() == 0) actionIndicator.setIcon(VOLUME_MUTED);
        else actionIndicator.setIcon(VOLUME_LOW);
        actionIndicator.setVisible(true);
        actionIndicator.animate();
    }


    public void FORWARD5Action(){
        controlBarController.mouseEventTracker.move();

        if (mediaInterface.mediaActive.get()) {

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

                if(subtitlesController.subtitlesBox.subtitlesLocation == Pos.BOTTOM_LEFT || subtitlesController.subtitlesBox.subtitlesLocation == Pos.BOTTOM_CENTER || subtitlesController.subtitlesBox.subtitlesLocation == Pos.BOTTOM_RIGHT){
                    subtitlesController.subtitlesBox.subtitlesContainer.setTranslateY(-30);
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
        }
    }

    public void REWIND5Action(){
        controlBarController.mouseEventTracker.move();

        if (mediaInterface.mediaActive.get()) {

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

                if(subtitlesController.subtitlesBox.subtitlesLocation == Pos.BOTTOM_LEFT || subtitlesController.subtitlesBox.subtitlesLocation == Pos.BOTTOM_CENTER || subtitlesController.subtitlesBox.subtitlesLocation == Pos.BOTTOM_RIGHT){
                    subtitlesController.subtitlesBox.subtitlesContainer.setTranslateY(-30);
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
        }
    }


    public void FORWARD10Action(){
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

                if(subtitlesController.subtitlesBox.subtitlesLocation == Pos.BOTTOM_LEFT || subtitlesController.subtitlesBox.subtitlesLocation == Pos.BOTTOM_CENTER || subtitlesController.subtitlesBox.subtitlesLocation == Pos.BOTTOM_RIGHT){
                    subtitlesController.subtitlesBox.subtitlesContainer.setTranslateY(-30);
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
        }
    }


    public void REWIND10Action(){
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

                if(subtitlesController.subtitlesBox.subtitlesLocation == Pos.BOTTOM_LEFT || subtitlesController.subtitlesBox.subtitlesLocation == Pos.BOTTOM_CENTER || subtitlesController.subtitlesBox.subtitlesLocation == Pos.BOTTOM_RIGHT){
                    subtitlesController.subtitlesBox.subtitlesContainer.setTranslateY(-30);
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

    public void FRAME_FORWARDAction(){

        controlBarController.mouseEventTracker.move();

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
    }

    public void FRAME_BACKWARDAction(){
        controlBarController.mouseEventTracker.move();

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
    }

    public void SEEK0Action(){
        controlBarController.mouseEventTracker.move();
        mediaInterface.seekedToEnd = false;
        if(mediaInterface.mediaActive.get()){
            seekingWithKeys = true;
            if(miniplayerActive) {
                miniplayer.miniplayerController.sliderPane.setVisible(true);

                if(subtitlesController.subtitlesBox.subtitlesLocation == Pos.BOTTOM_LEFT || subtitlesController.subtitlesBox.subtitlesLocation == Pos.BOTTOM_CENTER || subtitlesController.subtitlesBox.subtitlesLocation == Pos.BOTTOM_RIGHT){
                    subtitlesController.subtitlesBox.subtitlesContainer.setTranslateY(-30);
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

    public void SEEK10Action(){
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

                if(subtitlesController.subtitlesBox.subtitlesLocation == Pos.BOTTOM_LEFT || subtitlesController.subtitlesBox.subtitlesLocation == Pos.BOTTOM_CENTER || subtitlesController.subtitlesBox.subtitlesLocation == Pos.BOTTOM_RIGHT){
                    subtitlesController.subtitlesBox.subtitlesContainer.setTranslateY(-30);
                }

                miniplayer.miniplayerController.progressBarTimer.playFromStart();
            }
        }
    }

    public void SEEK20Action(){
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

                if(subtitlesController.subtitlesBox.subtitlesLocation == Pos.BOTTOM_LEFT || subtitlesController.subtitlesBox.subtitlesLocation == Pos.BOTTOM_CENTER || subtitlesController.subtitlesBox.subtitlesLocation == Pos.BOTTOM_RIGHT){
                    subtitlesController.subtitlesBox.subtitlesContainer.setTranslateY(-30);
                }

                miniplayer.miniplayerController.progressBarTimer.playFromStart();
            }
        }
    }

    public void SEEK30Action(){
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

                if(subtitlesController.subtitlesBox.subtitlesLocation == Pos.BOTTOM_LEFT || subtitlesController.subtitlesBox.subtitlesLocation == Pos.BOTTOM_CENTER || subtitlesController.subtitlesBox.subtitlesLocation == Pos.BOTTOM_RIGHT){
                    subtitlesController.subtitlesBox.subtitlesContainer.setTranslateY(-30);
                }

                miniplayer.miniplayerController.progressBarTimer.playFromStart();
            }
        }
    }

    public void SEEK40Action(){
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

                if(subtitlesController.subtitlesBox.subtitlesLocation == Pos.BOTTOM_LEFT || subtitlesController.subtitlesBox.subtitlesLocation == Pos.BOTTOM_CENTER || subtitlesController.subtitlesBox.subtitlesLocation == Pos.BOTTOM_RIGHT){
                    subtitlesController.subtitlesBox.subtitlesContainer.setTranslateY(-30);
                }

                miniplayer.miniplayerController.progressBarTimer.playFromStart();
            }
        }
    }

    public void SEEK50Action(){
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

                if(subtitlesController.subtitlesBox.subtitlesLocation == Pos.BOTTOM_LEFT || subtitlesController.subtitlesBox.subtitlesLocation == Pos.BOTTOM_CENTER || subtitlesController.subtitlesBox.subtitlesLocation == Pos.BOTTOM_RIGHT){
                    subtitlesController.subtitlesBox.subtitlesContainer.setTranslateY(-30);
                }

                miniplayer.miniplayerController.progressBarTimer.playFromStart();
            }
        }
    }

    public void SEEK60Action(){
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

                if(subtitlesController.subtitlesBox.subtitlesLocation == Pos.BOTTOM_LEFT || subtitlesController.subtitlesBox.subtitlesLocation == Pos.BOTTOM_CENTER || subtitlesController.subtitlesBox.subtitlesLocation == Pos.BOTTOM_RIGHT){
                    subtitlesController.subtitlesBox.subtitlesContainer.setTranslateY(-30);
                }

                miniplayer.miniplayerController.progressBarTimer.playFromStart();
            }
        }
    }

    public void SEEK70Action(){
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

                if(subtitlesController.subtitlesBox.subtitlesLocation == Pos.BOTTOM_LEFT || subtitlesController.subtitlesBox.subtitlesLocation == Pos.BOTTOM_CENTER || subtitlesController.subtitlesBox.subtitlesLocation == Pos.BOTTOM_RIGHT){
                    subtitlesController.subtitlesBox.subtitlesContainer.setTranslateY(-30);
                }

                miniplayer.miniplayerController.progressBarTimer.playFromStart();
            }
        }
    }

    public void SEEK80Action(){
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

                if(subtitlesController.subtitlesBox.subtitlesLocation == Pos.BOTTOM_LEFT || subtitlesController.subtitlesBox.subtitlesLocation == Pos.BOTTOM_CENTER || subtitlesController.subtitlesBox.subtitlesLocation == Pos.BOTTOM_RIGHT){
                    subtitlesController.subtitlesBox.subtitlesContainer.setTranslateY(-30);
                }

                miniplayer.miniplayerController.progressBarTimer.playFromStart();
            }
        }
    }

    public void SEEK90Action(){
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

                if(subtitlesController.subtitlesBox.subtitlesLocation == Pos.BOTTOM_LEFT || subtitlesController.subtitlesBox.subtitlesLocation == Pos.BOTTOM_CENTER || subtitlesController.subtitlesBox.subtitlesLocation == Pos.BOTTOM_RIGHT){
                    subtitlesController.subtitlesBox.subtitlesContainer.setTranslateY(-30);
                }

                miniplayer.miniplayerController.progressBarTimer.playFromStart();
            }
        }
    }

    public void PLAYBACK_SPEED_UP25Action(){

        playbackSettingsController.playbackSpeedController.customSpeedPane.customSpeedSlider.setValue(playbackSettingsController.playbackSpeedController.customSpeedPane.customSpeedSlider.getValue() + 0.25);

        valueIndicator.setValue(playbackSettingsController.playbackSpeedController.df.format(playbackSettingsController.playbackSpeedController.customSpeedPane.customSpeedSlider.getValue()) + "x");
        valueIndicator.play();

        actionIndicator.setIcon(FORWARD);
        actionIndicator.setVisible(true);
        actionIndicator.animate();
    }

    public void PLAYBACK_SPEED_DOWN25Action(){

        playbackSettingsController.playbackSpeedController.customSpeedPane.customSpeedSlider.setValue(playbackSettingsController.playbackSpeedController.customSpeedPane.customSpeedSlider.getValue() - 0.25);

        valueIndicator.setValue(playbackSettingsController.playbackSpeedController.df.format(playbackSettingsController.playbackSpeedController.customSpeedPane.customSpeedSlider.getValue()) + "x");
        valueIndicator.play();

        actionIndicator.setIcon(REWIND);
        actionIndicator.setVisible(true);
        actionIndicator.animate();
    }

    public void PLAYBACK_SPEED_UP5Action(){

        playbackSettingsController.playbackSpeedController.customSpeedPane.customSpeedSlider.setValue(playbackSettingsController.playbackSpeedController.customSpeedPane.customSpeedSlider.getValue() + 0.05);

        valueIndicator.setValue(playbackSettingsController.playbackSpeedController.df.format(playbackSettingsController.playbackSpeedController.customSpeedPane.customSpeedSlider.getValue()) + "x");
        valueIndicator.play();

        actionIndicator.setIcon(FORWARD);
        actionIndicator.setVisible(true);
        actionIndicator.animate();
    }

    public void PLAYBACK_SPEED_DOWN5Action(){

        playbackSettingsController.playbackSpeedController.customSpeedPane.customSpeedSlider.setValue(playbackSettingsController.playbackSpeedController.customSpeedPane.customSpeedSlider.getValue() - 0.05);

        valueIndicator.setValue(playbackSettingsController.playbackSpeedController.df.format(playbackSettingsController.playbackSpeedController.customSpeedPane.customSpeedSlider.getValue()) + "x");
        valueIndicator.play();

        actionIndicator.setIcon(REWIND);
        actionIndicator.setVisible(true);
        actionIndicator.animate();
    }

    public void NEXTAction(){
        controlBarController.mouseEventTracker.move();

        if(menuController.queuePage.queueBox.itemDragActive.get()) return;

        if(!menuController.queuePage.queueBox.queue.isEmpty() && (menuController.queuePage.queueBox.activeItem.get() == null || menuController.queuePage.queueBox.queue.size() > menuController.queuePage.queueBox.activeIndex.get() + 1)){

            actionIndicator.setIcon(NEXT_VIDEO);
            actionIndicator.setVisible(true);
            actionIndicator.animate();

            mediaInterface.playNext();
        }
    }

    public void PREVIOUSAction(){
        controlBarController.mouseEventTracker.move();

        if(mediaInterface.mediaActive.get() && controlBarController.durationSlider.getValue() > 5){ // restart current video
            actionIndicator.setIcon(REPLAY);
            actionIndicator.setVisible(true);
            actionIndicator.animate();

            mediaInterface.seekedToEnd = false;
            controlBarController.durationSlider.setValue(0);

        }
        else if(menuController.queuePage.queueBox.activeItem.get() != null && menuController.queuePage.queueBox.activeIndex.get() > 0){ // play previous video

            if(menuController.queuePage.queueBox.itemDragActive.get()) return;

            actionIndicator.setIcon(PREVIOUS_VIDEO);
            actionIndicator.setVisible(true);
            actionIndicator.animate();

            mediaInterface.playPrevious();
        }
    }

    public void ENDAction(){
        controlBarController.mouseEventTracker.move();
        mediaInterface.seekedToEnd = true;
        if(mediaInterface.mediaActive.get()){
            seekingWithKeys = true;
            if(miniplayerActive) {
                miniplayer.miniplayerController.sliderPane.setVisible(true);

                if(subtitlesController.subtitlesBox.subtitlesLocation == Pos.BOTTOM_LEFT || subtitlesController.subtitlesBox.subtitlesLocation == Pos.BOTTOM_CENTER || subtitlesController.subtitlesBox.subtitlesLocation == Pos.BOTTOM_RIGHT){
                    subtitlesController.subtitlesBox.subtitlesContainer.setTranslateY(-30);
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

    public void FULLSCREENAction(){

        controlBarController.mouseEventTracker.move();
        controlBarController.toggleFullScreen();

        if(menuController.queuePage.activeQueueItemContextMenu != null && menuController.queuePage.activeQueueItemContextMenu.showing) menuController.queuePage.activeQueueItemContextMenu.hide();
        if(menuController.queuePage.addOptionsContextMenu.showing) menuController.queuePage.addOptionsContextMenu.hide();
        if(subtitlesController.openSubtitlesPane.searchOptionsContextMenu.showing) subtitlesController.openSubtitlesPane.searchOptionsContextMenu.hide();
        if(menuController.settingsPage.settingsMenu.showing) menuController.settingsPage.settingsMenu.hide();

    }

    public void SNAPSHOTAction(){
        takeScreenshot();
        controlBarController.mouseEventTracker.move();
    }



    public void MINIPLAYERAction(){

        if(miniplayerActive) closeMiniplayer();
        else openMiniplayer();

        if(subtitlesController.openSubtitlesPane.searchOptionsContextMenu.showing) subtitlesController.openSubtitlesPane.searchOptionsContextMenu.hide();
        if(menuController.queuePage.activeQueueItemContextMenu != null && menuController.queuePage.activeQueueItemContextMenu.showing) menuController.queuePage.activeQueueItemContextMenu.hide();
        if(menuController.queuePage.addOptionsContextMenu.showing) menuController.queuePage.addOptionsContextMenu.hide();
    }


    public void SUBTITLESAction() {
        controlBarController.mouseEventTracker.move();

        if (menuController.queuePage.activeQueueItemContextMenu != null && menuController.queuePage.activeQueueItemContextMenu.showing)
            menuController.queuePage.activeQueueItemContextMenu.hide();
        if (menuController.queuePage.addOptionsContextMenu.showing)
            menuController.queuePage.addOptionsContextMenu.hide();
        if (subtitlesController.openSubtitlesPane.searchOptionsContextMenu.showing)
            subtitlesController.openSubtitlesPane.searchOptionsContextMenu.hide();

        if (subtitlesController.subtitlesState != SubtitlesState.CLOSED) subtitlesController.closeSubtitles();
        else subtitlesController.openSubtitles();
    }


    public void PLAYBACK_SETTINGSAction(){
        controlBarController.mouseEventTracker.move();

        if (playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) {
            playbackSettingsController.closeSettings();
        } else {
            playbackSettingsController.openSettings();
        }

        if(subtitlesController.openSubtitlesPane.searchOptionsContextMenu.showing) subtitlesController.openSubtitlesPane.searchOptionsContextMenu.hide();
    }


    public void MENUAction(){

        if(addYoutubeVideoWindow.showing || hotkeyChangeWindow.showing || menuController.queuePage.queueBox.itemDragActive.get()) return;

        if(menuController.queuePage.activeQueueItemContextMenu != null && menuController.queuePage.activeQueueItemContextMenu.showing) menuController.queuePage.activeQueueItemContextMenu.hide();
        if(menuController.queuePage.addOptionsContextMenu.showing) menuController.queuePage.addOptionsContextMenu.hide();
        if(subtitlesController.openSubtitlesPane.searchOptionsContextMenu.showing) subtitlesController.openSubtitlesPane.searchOptionsContextMenu.hide();

        if(!menuController.menuInTransition){
            if(menuController.menuState != MenuState.CLOSED) menuController.closeMenu();
            else menuController.queuePage.enter();
        }
    }

    public void CLEAR_QUEUEAction(){

        controlBarController.mouseEventTracker.move();

        if(menuController.queuePage.queueBox.queue.isEmpty()) return;

        actionIndicator.setIcon(QUEUE_CLEAR);
        actionIndicator.setVisible(true);
        actionIndicator.animate();

        menuController.queuePage.clearQueue();
    }

    public void SHUFFLEAction(){

        controlBarController.mouseEventTracker.move();

        if(playbackSettingsController.playbackOptionsController.shuffleTab.toggle.isSelected())
            actionIndicator.setIcon(SHUFFLE_OFF);
        else actionIndicator.setIcon(SHUFFLE);

        actionIndicator.setVisible(true);
        actionIndicator.animate();

        playbackSettingsController.playbackOptionsController.shuffleTab.toggle.setSelected(!playbackSettingsController.playbackOptionsController.shuffleTab.toggle.isSelected());
    }

    public void AUTOPLAYAction(){

        controlBarController.mouseEventTracker.move();

        if(playbackSettingsController.playbackOptionsController.autoplayTab.toggle.isSelected())
            actionIndicator.setIcon(REPEAT_OFF);
        else actionIndicator.setIcon(REPEAT);

        actionIndicator.setVisible(true);
        actionIndicator.animate();

        playbackSettingsController.playbackOptionsController.autoplayTab.toggle.setSelected(!playbackSettingsController.playbackOptionsController.autoplayTab.toggle.isSelected());
    }

    public void LOOPAction(){

        controlBarController.mouseEventTracker.move();

        if(playbackSettingsController.playbackOptionsController.loopTab.toggle.isSelected())
            actionIndicator.setIcon(REPEAT_ONCE_OFF);
        else actionIndicator.setIcon(REPEAT_ONCE);

        actionIndicator.setVisible(true);
        actionIndicator.animate();

        playbackSettingsController.playbackOptionsController.loopTab.toggle.setSelected(!playbackSettingsController.playbackOptionsController.loopTab.toggle.isSelected());
    }

    public void OPEN_QUEUEAction(){

        if(addYoutubeVideoWindow.showing || hotkeyChangeWindow.showing || menuController.queuePage.queueBox.itemDragActive.get() || menuController.menuState == MenuState.QUEUE_OPEN) return;

        controlBarController.mouseEventTracker.move();

        if(menuController.queuePage.activeQueueItemContextMenu != null && menuController.queuePage.activeQueueItemContextMenu.showing) menuController.queuePage.activeQueueItemContextMenu.hide();
        if(menuController.queuePage.addOptionsContextMenu.showing) menuController.queuePage.addOptionsContextMenu.hide();
        if(subtitlesController.openSubtitlesPane.searchOptionsContextMenu.showing) subtitlesController.openSubtitlesPane.searchOptionsContextMenu.hide();

        menuController.queuePage.enter();
    }

    public void OPEN_RECENT_MEDIAAction(){
        if(addYoutubeVideoWindow.showing || hotkeyChangeWindow.showing || menuController.queuePage.queueBox.itemDragActive.get() || menuController.menuState == MenuState.RECENT_MEDIA_OPEN) return;

        controlBarController.mouseEventTracker.move();

        if(menuController.queuePage.activeQueueItemContextMenu != null && menuController.queuePage.activeQueueItemContextMenu.showing) menuController.queuePage.activeQueueItemContextMenu.hide();
        if(menuController.queuePage.addOptionsContextMenu.showing) menuController.queuePage.addOptionsContextMenu.hide();
        if(subtitlesController.openSubtitlesPane.searchOptionsContextMenu.showing) subtitlesController.openSubtitlesPane.searchOptionsContextMenu.hide();

        menuController.recentMediaPage.enter();
    }

    public void OPEN_MUSIC_LIBRARYAction(){

        if(addYoutubeVideoWindow.showing || hotkeyChangeWindow.showing || menuController.queuePage.queueBox.itemDragActive.get() || menuController.menuState == MenuState.MUSIC_LIBRARY_OPEN) return;

        controlBarController.mouseEventTracker.move();

        if(menuController.queuePage.activeQueueItemContextMenu != null && menuController.queuePage.activeQueueItemContextMenu.showing) menuController.queuePage.activeQueueItemContextMenu.hide();
        if(menuController.queuePage.addOptionsContextMenu.showing) menuController.queuePage.addOptionsContextMenu.hide();
        if(subtitlesController.openSubtitlesPane.searchOptionsContextMenu.showing) subtitlesController.openSubtitlesPane.searchOptionsContextMenu.hide();

        menuController.musicLibraryPage.enter();
    }

    public void OPEN_PLAYLISTSAction(){

        if(addYoutubeVideoWindow.showing || hotkeyChangeWindow.showing || menuController.queuePage.queueBox.itemDragActive.get() || menuController.menuState == MenuState.PLAYLISTS_OPEN) return;

        controlBarController.mouseEventTracker.move();

        if(menuController.queuePage.activeQueueItemContextMenu != null && menuController.queuePage.activeQueueItemContextMenu.showing) menuController.queuePage.activeQueueItemContextMenu.hide();
        if(menuController.queuePage.addOptionsContextMenu.showing) menuController.queuePage.addOptionsContextMenu.hide();
        if(subtitlesController.openSubtitlesPane.searchOptionsContextMenu.showing) subtitlesController.openSubtitlesPane.searchOptionsContextMenu.hide();

        menuController.playlistsPage.enter();
    }

    public void OPEN_SETTINGSAction(){

        if(addYoutubeVideoWindow.showing || hotkeyChangeWindow.showing || menuController.queuePage.queueBox.itemDragActive.get() || menuController.menuState == MenuState.SETTINGS_OPEN) return;

        controlBarController.mouseEventTracker.move();

        if(menuController.queuePage.activeQueueItemContextMenu != null && menuController.queuePage.activeQueueItemContextMenu.showing) menuController.queuePage.activeQueueItemContextMenu.hide();
        if(menuController.queuePage.addOptionsContextMenu.showing) menuController.queuePage.addOptionsContextMenu.hide();
        if(subtitlesController.openSubtitlesPane.searchOptionsContextMenu.showing) subtitlesController.openSubtitlesPane.searchOptionsContextMenu.hide();

        menuController.settingsPage.enter();
    }

    public PlaybackSettingsController getSettingsController() {
        return playbackSettingsController;
    }

    public ControlBarController getControlBarController() {
        return controlBarController;
    }

    public MenuController getMenuController(){ return menuController;}

    public MediaInterface getMediaInterface() {
        return mediaInterface;
    }

    public SubtitlesController getSubtitlesController(){
        return subtitlesController;
    }

    public void closeApp(){
        subtitlesController.resetSubtitles();

        mediaInterface.embeddedMediaPlayer.release();
        SleepSuppressor.allowSleep();

        Platform.exit();
        System.exit(0);
    }

    public void loadTooltips(){
        openMenuTooltip = new ControlTooltip(this,"Open menu", hotkeyController.getHotkeyString(Action.MENU), menuButton, 0, TooltipType.MENU_TOOLTIP);
        viewMetadataTooltip = new ControlTooltip(this,"Media metadata", "", metadataButton, 0, TooltipType.MENU_TOOLTIP);
    }
}