package fxplayer;

import fxplayer.menu.MenuController;
import fxplayer.menu.Settings.Action;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.util.Duration;

public class MiniplayerController {

    MainController mainController;
    ControlBarController controlBarController;
    MenuController menuController;
    MediaInterface mediaInterface;
    Miniplayer miniplayer;


    public ImageView videoImageView = new ImageView();
    public ImageView seekImageView = new ImageView();

    StackPane videoImageViewWrapper = new StackPane();
    public StackPane videoImageViewInnerWrapper = new StackPane();

    public StackPane coverImageContainer = new StackPane(), coverImageWrapper = new StackPane(), coverFilter = new StackPane();
    public ImageView coverImage = new ImageView(), coverBackground = new ImageView();

    StackPane previousVideoButtonPane = new StackPane();
    StackPane playButtonPane = new StackPane();
    StackPane nextVideoButtonPane = new StackPane();
    StackPane closeButtonPane = new StackPane();

    StackPane controlsBackground = new StackPane();


    Button previousVideoButton = new Button();
    Button playButton = new Button();
    Button nextVideoButton = new Button();
    Button closeButton = new Button();


    Region previousVideoIcon = new Region();
    Region replayIcon = new Region();
    Region nextVideoIcon = new Region();
    Region closeIcon = new Region();


    double pauseCorner1X = 0;
    double pauseCorner1Y = 0;

    double pauseCorner2X = 0;
    double pauseCorner2Y = 44;

    double pauseCorner3X = 12;
    double pauseCorner3Y = 44;

    double pauseCorner4X = 12;
    double pauseCorner4Y = 0;

    double pauseCorner5X = 24;
    double pauseCorner5Y = 0;

    double pauseCorner6X = 24;
    double pauseCorner6Y = 44;

    double pauseCorner7X = 36;
    double pauseCorner7Y = 44;

    double pauseCorner8X = 36;
    double pauseCorner8Y = 0;



    double playCorner1X = 0;
    double playCorner1Y = 0;

    double playCorner2X = 0;
    double playCorner2Y = 44;

    double playCorner3X = 19;
    double playCorner3Y = 33;

    double playCorner4X = 19;
    double playCorner4Y = 11;

    double playCorner5X = 18;
    double playCorner5Y = 10.5;

    double playCorner6X = 18;
    double playCorner6Y = 33.5;

    double playCorner7X = 36;
    double playCorner7Y = 22;

    double playCorner8X = 36;
    double playCorner8Y = 22;



    MoveTo playPauseCorner1 = new MoveTo(playCorner1X, playCorner1Y);
    LineTo playPauseCorner2 = new LineTo(playCorner2X, playCorner2Y);
    LineTo playPauseCorner3 = new LineTo(playCorner3X, playCorner3Y);
    LineTo playPauseCorner4 = new LineTo(playCorner4X, playCorner4Y);
    MoveTo playPauseCorner5 = new MoveTo(playCorner5X, playCorner5Y);
    LineTo playPauseCorner6 = new LineTo(playCorner6X, playCorner6Y);
    LineTo playPauseCorner7 = new LineTo(playCorner7X, playCorner7Y);
    LineTo playPauseCorner8 = new LineTo(playCorner8X, playCorner8Y);

    Path playPauseLeftPath = new Path(playPauseCorner1, playPauseCorner2, playPauseCorner3, playPauseCorner4, new ClosePath());
    Path playPauseRightPath = new Path(playPauseCorner5, playPauseCorner6, playPauseCorner7, playPauseCorner8, new ClosePath());

    Pane playPausePane = new Pane();

    Timeline playPauseTransition = null;


    SVGPath previousVideoSVG = new SVGPath();
    SVGPath replaySVG = new SVGPath();
    SVGPath nextVideoSVG = new SVGPath();
    SVGPath closeSVG = new SVGPath();

    StackPane sliderPane = new StackPane();
    ProgressBar progressBar = new ProgressBar();
    Slider slider = new Slider();

    SliderHoverBox sliderHoverBox;

    boolean sliderHover = false;
    boolean controlsVisible = false;

    public ControlTooltip previousVideoButtonTooltip;
    public ControlTooltip playButtonTooltip;
    public ControlTooltip nextVideoButtonTooltip;

    ChangeListener<? super Number> widthListener;
    ChangeListener<? super Number> heightListener;

    boolean previousVideoButtonHover = false, playButtonHover = false, nextVideoButtonHover = false;

    boolean miniplayerHover = false;


    PauseTransition progressBarTimer = new PauseTransition(Duration.millis(1000));


    PauseTransition seekTimer = new PauseTransition(Duration.millis(50));



    MiniplayerController(MainController mainController, ControlBarController controlBarController, MenuController menuController, MediaInterface mediaInterface, Miniplayer miniplayer){

        this.mainController = mainController;
        this.controlBarController = controlBarController;
        this.menuController = menuController;
        this.mediaInterface = mediaInterface;
        this.miniplayer = miniplayer;

        videoImageViewWrapper.setPrefSize(500, 300);
        videoImageViewWrapper.setBackground(new Background(new BackgroundFill(Color.BLACK, new CornerRadii(10), Insets.EMPTY)));
        videoImageViewWrapper.getChildren().addAll(videoImageViewInnerWrapper, controlsBackground, previousVideoButtonPane, nextVideoButtonPane, playButtonPane, closeButtonPane, sliderPane);
        videoImageViewWrapper.setId("mediaViewWrapper");

        sliderHoverBox = new SliderHoverBox(videoImageViewWrapper, true);


        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(videoImageViewWrapper.widthProperty());
        clip.heightProperty().bind(videoImageViewWrapper.heightProperty());

        videoImageViewWrapper.setClip(clip);

        videoImageViewWrapper.setOnMouseEntered(e -> {
            showControls();
            miniplayerHover = true;
        });

        videoImageViewWrapper.setOnMouseExited(e -> {
            hideControls();
            miniplayerHover = false;
        });

        coverImageContainer.setVisible(false);
        coverImageContainer.setMouseTransparent(true);
        coverImageContainer.setStyle("-fx-background-color: black;");
        coverImageContainer.setMinHeight(0);
        coverImageContainer.getChildren().addAll(coverBackground, coverFilter);

        coverFilter.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
        coverFilter.getChildren().add(coverImageWrapper);

        coverImageWrapper.getChildren().add(coverImage);


        coverBackground.fitWidthProperty().bind(videoImageViewWrapper.widthProperty());
        coverBackground.fitHeightProperty().bind(videoImageViewWrapper.heightProperty());
        coverBackground.setPreserveRatio(false);


        Rectangle rectangle = new Rectangle();
        rectangle.widthProperty().bind(coverImage.fitWidthProperty());
        rectangle.heightProperty().bind(coverImage.fitHeightProperty());
        rectangle.setArcWidth(40);
        rectangle.setArcHeight(40);
        coverImage.setClip(rectangle);

        coverImageWrapper.maxWidthProperty().bind(coverImage.fitWidthProperty());
        coverImageWrapper.maxHeightProperty().bind(coverImage.fitHeightProperty());

        videoImageViewInnerWrapper.setBackground(Background.EMPTY);
        videoImageViewInnerWrapper.setMouseTransparent(true);
        videoImageViewInnerWrapper.getChildren().addAll(videoImageView, seekImageView, coverImageContainer);
        StackPane.setAlignment(videoImageViewInnerWrapper, Pos.CENTER);

        Rectangle mediaClip = new Rectangle();
        mediaClip.setArcHeight(20);
        mediaClip.setArcWidth(20);
        mediaClip.widthProperty().bind(videoImageViewInnerWrapper.widthProperty());
        mediaClip.heightProperty().bind(videoImageViewInnerWrapper.heightProperty());
        videoImageViewInnerWrapper.setClip(mediaClip);


        videoImageView.setPreserveRatio(true);

        videoImageView.fitWidthProperty().bind(videoImageViewWrapper.widthProperty().subtract(2));
        videoImageView.fitHeightProperty().bind(videoImageViewWrapper.heightProperty().subtract(2));
        videoImageView.setMouseTransparent(true);

        seekImageView.setPreserveRatio(true);
        seekImageView.setVisible(false);
        seekImageView.fitWidthProperty().bind(videoImageViewWrapper.widthProperty().subtract(2));
        seekImageView.fitHeightProperty().bind(videoImageViewWrapper.heightProperty().subtract(2));


        mainController.subtitlesController.subtitlesBox.mediaWidthMultiplier.set(0.4);


        mainController.sizeMultiplier.set(0.6);
        mainController.heightMultiplier.set(0.6);
        if(mainController.actionIndicator.wrapper.isVisible()) mainController.actionIndicator.updateSize();
        mainController.forwardsIndicator.resize();
        mainController.backwardsIndicator.resize();
        mainController.valueIndicator.resize();
        mainController.valueIndicator.reposition();

        sliderPane.setPrefHeight(16);
        sliderPane.setMaxHeight(Region.USE_PREF_SIZE);
        sliderPane.getChildren().addAll(progressBar, slider);
        sliderPane.setVisible(false);
        StackPane.setMargin(sliderPane, new Insets(0, 4, 10, 4));
        StackPane.setAlignment(sliderPane, Pos.BOTTOM_CENTER);

        progressBar.setId("durationBar");
        progressBar.setMouseTransparent(true);
        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.setPrefHeight(4);
        StackPane.setMargin(progressBar, new Insets(0, 11, 0, 12));

        progressBarTimer.setOnFinished(e -> {
            if(!miniplayerHover && !slider.isValueChanging()) {
                sliderPane.setVisible(false);
                if(mainController.subtitlesController.subtitlesBox.subtitlesLocation == Pos.BOTTOM_LEFT || mainController.subtitlesController.subtitlesBox.subtitlesLocation == Pos.BOTTOM_CENTER || mainController.subtitlesController.subtitlesBox.subtitlesLocation == Pos.BOTTOM_RIGHT){
                    mainController.subtitlesController.subtitlesBox.subtitlesContainer.setTranslateY(-10);
                }
            }
        });


        slider.setPrefHeight(16);
        slider.setMaxHeight(Region.USE_PREF_SIZE);
        slider.setMinHeight(Region.USE_PREF_SIZE);
        slider.setId("slider");
        StackPane.setMargin(slider, new Insets(0, 4, 0, 4));

        Platform.runLater(() -> {

            slider.lookup(".thumb").setScaleX(0);
            slider.lookup(".thumb").setScaleY(0);

            slider.lookup(".track").setCursor(Cursor.HAND);
            slider.lookup(".thumb").setMouseTransparent(true);

            slider.lookup(".track").addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
                if(e.getButton() == MouseButton.PRIMARY) slider.setValueChanging(true);
                else e.consume();
            });
            slider.lookup(".track").addEventFilter(MouseEvent.MOUSE_RELEASED, e -> {
                if(e.getButton() == MouseButton.PRIMARY) slider.setValueChanging(false);
            });

            slider.lookup(".track").setOnMouseMoved(e -> {

                controlBarController.previewTimer.playFromStart();

                String newTime = Utilities.durationToString(Duration.seconds((e.getX())/(slider.lookup(".track").getBoundsInLocal().getMaxX()) * slider.getMax()));
                sliderHoverBox.timeLabel.setText(newTime);

                double minTranslation = (sliderHoverBox.localToScene(sliderHoverBox.getBoundsInLocal()).getMinX() - sliderHoverBox.getTranslateX() - slider.lookup(".track").localToScene(slider.lookup(".track").getBoundsInLocal()).getMinX()) * -1;
                double maxTranslation = slider.lookup(".track").localToScene(slider.lookup(".track").getBoundsInLocal()).getMaxX() - sliderHoverBox.localToScene(sliderHoverBox.getBoundsInLocal()).getMaxX() + sliderHoverBox.getTranslateX();

                double newTranslation = Math.max(minTranslation, Math.min(maxTranslation, e.getSceneX() - (sliderHoverBox.localToScene(sliderHoverBox.getBoundsInLocal()).getMinX() + sliderHoverBox.getBoundsInLocal().getMaxX()/2) + sliderHoverBox.getTranslateX()));


                sliderHoverBox.setTranslateX(newTranslation);

                sliderHoverBox.setVisible(true);
            });

            slider.lookup(".track").setOnMouseEntered((e) -> {

                controlBarController.previewTimer.playFromStart();

                sliderHover = true;
                sliderHoverOn();

                String newTime = Utilities.durationToString(Duration.seconds(e.getX()/(slider.lookup(".track").getBoundsInLocal().getMaxX()) * slider.getMax()));
                sliderHoverBox.timeLabel.setText(newTime);

                double minTranslation = (sliderHoverBox.localToScene(sliderHoverBox.getBoundsInLocal()).getMinX() - sliderHoverBox.getTranslateX() - slider.lookup(".track").localToScene(slider.lookup(".track").getBoundsInLocal()).getMinX()) * -1;
                double maxTranslation = slider.lookup(".track").localToScene(slider.lookup(".track").getBoundsInLocal()).getMaxX() - sliderHoverBox.localToScene(sliderHoverBox.getBoundsInLocal()).getMaxX() + sliderHoverBox.getTranslateX();

                double newTranslation = Math.max(minTranslation, Math.min(maxTranslation, e.getSceneX() - (sliderHoverBox.localToScene(sliderHoverBox.getBoundsInLocal()).getMinX() + sliderHoverBox.getBoundsInLocal().getMaxX()/2) + sliderHoverBox.getTranslateX()));


                sliderHoverBox.setTranslateX(newTranslation);

                sliderHoverBox.setVisible(true);
            });

            slider.lookup(".track").setOnMouseExited((e) -> {
                sliderHover = false;
                if (!e.isPrimaryButtonDown()) {
                    sliderHoverOff();

                    sliderHoverBox.setVisible(false);
                }

            });

            slider.lookup(".track").addEventFilter(MouseEvent.MOUSE_DRAGGED, e -> {
                if(!e.isPrimaryButtonDown()){

                    controlBarController.previewTimer.playFromStart();

                    String newTime = Utilities.durationToString(Duration.seconds(e.getX()/(slider.lookup(".track").getBoundsInLocal().getMaxX()) * slider.getMax()));
                    sliderHoverBox.timeLabel.setText(newTime);

                    double minTranslation = (sliderHoverBox.localToScene(sliderHoverBox.getBoundsInLocal()).getMinX() - sliderHoverBox.getTranslateX() - slider.lookup(".track").localToScene(slider.lookup(".track").getBoundsInLocal()).getMinX()) * -1;
                    double maxTranslation = slider.lookup(".track").localToScene(slider.lookup(".track").getBoundsInLocal()).getMaxX() - sliderHoverBox.localToScene(sliderHoverBox.getBoundsInLocal()).getMaxX() + sliderHoverBox.getTranslateX();

                    double newTranslation = Math.max(minTranslation, Math.min(maxTranslation, e.getSceneX() - (sliderHoverBox.localToScene(sliderHoverBox.getBoundsInLocal()).getMinX() + sliderHoverBox.getBoundsInLocal().getMaxX()/2) + sliderHoverBox.getTranslateX()));


                    sliderHoverBox.setTranslateX(newTranslation);

                    e.consume();
                }
            });
        });

        slider.valueProperty().addListener((observableValue, oldValue, newValue) -> {

            if(slider.isValueChanging()){

                controlBarController.previewTimer.playFromStart();

                sliderHoverBox.timeLabel.setText(Utilities.durationToString(Duration.seconds(slider.getValue())));

                double minTranslation = (sliderHoverBox.localToScene(sliderHoverBox.getBoundsInLocal()).getMinX() - sliderHoverBox.getTranslateX() - slider.lookup(".track").localToScene(slider.lookup(".track").getBoundsInLocal()).getMinX()) * -1;
                double maxTranslation = slider.lookup(".track").localToScene(slider.lookup(".track").getBoundsInLocal()).getMaxX() - sliderHoverBox.localToScene(sliderHoverBox.getBoundsInLocal()).getMaxX() + sliderHoverBox.getTranslateX();
                double newTranslation = Math.max(minTranslation, Math.min(maxTranslation, slider.lookup(".track").localToScene(slider.lookup(".track").getBoundsInLocal()).getMinX() + slider.lookup(".track").getBoundsInLocal().getMaxX() * (newValue.doubleValue() / slider.getMax()) - (sliderHoverBox.localToScene(sliderHoverBox.getBoundsInLocal()).getMinX() + sliderHoverBox.getBoundsInLocal().getMaxX()/2) + sliderHoverBox.getTranslateX()));

                sliderHoverBox.setTranslateX(newTranslation);

                if(menuController.queuePage.queueBox.activeItem.get() != null
                        && menuController.queuePage.queueBox.activeItem.get().getMediaItem() != null
                        && menuController.queuePage.queueBox.activeItem.get().getMediaItem().hasVideo()
                        && !mediaInterface.videoDisabled){
                    if(controlBarController.pauseTransition != null && controlBarController.pauseTransition.getStatus() == Animation.Status.RUNNING) return;

                    controlBarController.pauseTransition = new PauseTransition(Duration.millis(50));
                    controlBarController.pauseTransition.setOnFinished(e -> mediaInterface.updatePreviewFrame(newValue.doubleValue()/slider.getMax(), false));

                    controlBarController.pauseTransition.playFromStart();
                }

            }

        });

        slider.valueProperty().bindBidirectional(controlBarController.durationSlider.valueProperty());

        slider.valueChangingProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue) { // pause video when user starts seeking

                showControls();

                if(menuController.queuePage.queueBox.activeItem.get() != null
                        && menuController.queuePage.queueBox.activeItem.get().getMediaItem() != null
                        && menuController.queuePage.queueBox.activeItem.get().getMediaItem().hasVideo()
                        && !mediaInterface.videoDisabled){
                    if(mediaInterface.playing.get()) seekImageView.setImage(videoImageView.getImage());
                    seekImageView.setVisible(true);
                    videoImageView.setVisible(false);
                }

                seekTimer.playFromStart();
                if(mediaInterface.playing.get()) mediaInterface.embeddedMediaPlayer.controls().pause();
                mediaInterface.playing.set(false);

                sliderHoverBox.timeLabel.setText(Utilities.durationToString(Duration.seconds(slider.getValue())));

                double minTranslation = (sliderHoverBox.localToScene(sliderHoverBox.getBoundsInLocal()).getMinX() - sliderHoverBox.getTranslateX() - slider.lookup(".track").localToScene(slider.lookup(".track").getBoundsInLocal()).getMinX()) * -1;
                double maxTranslation = slider.lookup(".track").localToScene(slider.lookup(".track").getBoundsInLocal()).getMaxX() - sliderHoverBox.localToScene(sliderHoverBox.getBoundsInLocal()).getMaxX() + sliderHoverBox.getTranslateX();


                double newTranslation = Math.max(minTranslation, Math.min(maxTranslation, slider.lookup(".track").localToScene(slider.lookup(".track").getBoundsInLocal()).getMinX() + slider.lookup(".track").getBoundsInLocal().getMaxX() * (slider.getValue() / slider.getMax()) - (sliderHoverBox.localToScene(sliderHoverBox.getBoundsInLocal()).getMinX() + sliderHoverBox.getBoundsInLocal().getMaxX()/2) + sliderHoverBox.getTranslateX()));


                sliderHoverBox.setTranslateX(newTranslation);
            }
            else {

                if(seekTimer.getStatus() == Animation.Status.RUNNING) seekTimer.stop();
                if(controlBarController.seekTimer.getStatus() == Animation.Status.RUNNING) controlBarController.seekTimer.stop();

                if (!sliderHover) sliderHoverOff();

                if(!miniplayerHover) hideControls();

                sliderHoverBox.setVisible(false);

                if(mediaInterface.mediaActive.get()) mediaInterface.seek(Duration.seconds(slider.getValue())); // seeks to exact position when user finishes dragging

                if (mediaInterface.atEnd) {
                    mediaInterface.defaultEnd();
                }
                else {
                    mediaInterface.seek(Duration.seconds(slider.getValue())); // seeks to exact position when user finishes dragging
                    if (mediaInterface.wasPlaying) mediaInterface.play(false);
                }
            }
        });

        slider.setMax(controlBarController.durationSlider.getMax());
        sliderPane.setMouseTransparent(!mediaInterface.mediaActive.get());
        slider.setValue(controlBarController.durationSlider.getValue());

        progressBar.progressProperty().bind(slider.valueProperty().divide(slider.maxProperty()));

        seekTimer.setOnFinished(e -> mediaInterface.pause());


        widthListener = (observableValue, oldValue, newValue) -> {

            if(newValue.doubleValue() < 500){
                reduceButtons();
            }
            else if(newValue.doubleValue() >= 500){
                enlargeButtons();
            }

            if(newValue.doubleValue() < 400){

                mainController.subtitlesController.subtitlesBox.mediaWidthMultiplier.set(0.3);

                mainController.sizeMultiplier.set(0.35);
                if(mainController.actionIndicator.wrapper.isVisible()) mainController.actionIndicator.updateSize();
                mainController.forwardsIndicator.resize();
                mainController.backwardsIndicator.resize();
                mainController.valueIndicator.resize();
            }
            else if((newValue.doubleValue() >= 400 && newValue.doubleValue() < 600)){

                mainController.subtitlesController.subtitlesBox.mediaWidthMultiplier.set(0.4);

                mainController.sizeMultiplier.set(0.5);
                if(mainController.actionIndicator.wrapper.isVisible()) mainController.actionIndicator.updateSize();
                mainController.forwardsIndicator.resize();
                mainController.backwardsIndicator.resize();
                mainController.valueIndicator.resize();

            }
            else if((newValue.doubleValue() >= 600 && newValue.doubleValue() < 800)){

                mainController.subtitlesController.subtitlesBox.mediaWidthMultiplier.set(0.55);

                mainController.sizeMultiplier.set(0.6);
                if(mainController.actionIndicator.wrapper.isVisible()) mainController.actionIndicator.updateSize();
                mainController.forwardsIndicator.resize();
                mainController.backwardsIndicator.resize();
                mainController.valueIndicator.resize();

            }
            else if(newValue.doubleValue() >= 800){

                mainController.subtitlesController.subtitlesBox.mediaWidthMultiplier.set(0.65);

                mainController.sizeMultiplier.set(0.7);
                if(mainController.actionIndicator.wrapper.isVisible()) mainController.actionIndicator.updateSize();
                mainController.forwardsIndicator.resize();
                mainController.backwardsIndicator.resize();
                mainController.valueIndicator.resize();

            }

        };

        heightListener = (observableValue, oldValue, newValue) -> {

            if(newValue.doubleValue() < 300){

                mainController.heightMultiplier.set(0.35);
                mainController.valueIndicator.reposition();
            }
            else if((newValue.doubleValue() >= 300 && newValue.doubleValue() < 400)){

                mainController.heightMultiplier.set(0.5);
                mainController.valueIndicator.reposition();

            }
            else if((newValue.doubleValue() >= 400 && newValue.doubleValue() < 550)){

                mainController.heightMultiplier.set(0.6);
                mainController.valueIndicator.reposition();

            }
            else if(newValue.doubleValue() >= 550){

                mainController.heightMultiplier.set(0.7);
                mainController.valueIndicator.reposition();

            }
        };

        videoImageViewInnerWrapper.widthProperty().addListener(widthListener);
        videoImageViewInnerWrapper.heightProperty().addListener(heightListener);


        controlsBackground.setMouseTransparent(true);
        controlsBackground.setId("controlsBackground");
        controlsBackground.setVisible(false);

        StackPane.setAlignment(closeButtonPane, Pos.TOP_RIGHT);
        StackPane.setMargin(closeButtonPane, new Insets(5, 5, 0, 0));
        closeButtonPane.setPrefSize(30, 30);
        closeButtonPane.setMaxSize(30, 30);

        closeButtonPane.setVisible(false);

        closeButtonPane.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> closeButtonHoverOn());

        closeButtonPane.addEventHandler(MouseEvent.MOUSE_EXITED, e -> closeButtonHoverOff());

        closeButtonPane.getChildren().addAll(closeButton, closeIcon);
        closeButton.setPrefSize(30, 30);
        closeButton.setCursor(Cursor.HAND);
        closeButton.setBackground(Background.EMPTY);
        closeButton.setOnAction(e -> mainController.closeMiniplayer());

        closeSVG.setContent(SVG.CLOSE.getContent());

        closeIcon.setShape(closeSVG);
        closeIcon.setMouseTransparent(true);
        closeIcon.getStyleClass().add("miniplayerIcon");
        closeIcon.setPrefSize(20, 20);
        closeIcon.setMaxSize(20, 20);
        closeIcon.setEffect(new DropShadow());


        previousVideoButtonPane.getChildren().addAll(previousVideoButton, previousVideoIcon);
        previousVideoButtonPane.setPrefSize(60, 60);
        previousVideoButtonPane.setMaxSize(60, 60);
        previousVideoButtonPane.setVisible(false);

        previousVideoButtonPane.translateXProperty().bind(videoImageViewWrapper.widthProperty().multiply(-0.25));

        previousVideoButton.setOnMouseEntered( e -> previousVideoButtonHoverOn());

        previousVideoButton.setOnMouseExited( e -> previousVideoButtonHoverOff());

        previousVideoButton.setPrefSize(60, 60);
        previousVideoButton.setBackground(Background.EMPTY);
        previousVideoButton.setCursor(Cursor.HAND);
        previousVideoButton.setOnAction(e -> {
            if(controlBarController.durationSlider.getValue() > 5) mediaInterface.replay();
            else mediaInterface.playPrevious();
        });


        previousVideoSVG.setContent(SVG.PREVIOUS_VIDEO.getContent());

        previousVideoIcon.setPrefSize(40, 40);
        previousVideoIcon.setMaxSize(40, 40);
        previousVideoIcon.setMouseTransparent(true);
        previousVideoIcon.getStyleClass().add("miniplayerIcon");
        previousVideoIcon.setShape(previousVideoSVG);


        playButtonPane.getChildren().addAll(playButton, replayIcon, playPausePane);
        playButtonPane.setPrefSize(60, 60);
        playButtonPane.setMaxSize(60, 60);
        playButtonPane.setVisible(false);


        playPausePane.setMouseTransparent(true);
        playPausePane.setPrefSize(36,44);
        playPausePane.setMaxSize(36, 44);
        playPausePane.setVisible(false);
        playPausePane.getChildren().addAll(playPauseLeftPath, playPauseRightPath);

        playPauseLeftPath.setFill(Color.rgb(100, 100, 100));
        playPauseLeftPath.setStroke(Color.TRANSPARENT);
        playPauseLeftPath.setStrokeWidth(0);

        playPauseRightPath.setStroke(Color.TRANSPARENT);
        playPauseRightPath.setStrokeWidth(0);
        playPauseRightPath.setFill(Color.rgb(100, 100, 100));


        playButton.setOnMouseEntered( e -> playButtonHoverOn());

        playButton.setOnMouseExited( e -> playButtonHoverOff());

        playButton.setPrefSize(60, 60);
        playButton.setBackground(Background.EMPTY);
        playButton.setCursor(Cursor.HAND);
        playButton.setOnAction(e -> {
            if(mediaInterface.atEnd) mediaInterface.replay();
            else if(mediaInterface.playing.get()){
                mediaInterface.wasPlaying = false;
                mediaInterface.pause();
            }
            else mediaInterface.play(false);
        });

        replaySVG.setContent(SVG.REPLAY.getContent());

        replayIcon.setPrefSize(48, 48);
        replayIcon.setMaxSize(48, 48);
        replayIcon.setMouseTransparent(true);
        replayIcon.getStyleClass().add("miniplayerIcon");
        replayIcon.setVisible(false);
        replayIcon.setShape(replaySVG);


        if(mediaInterface.atEnd){
            replayIcon.setVisible(true);
        }
        else if(mediaInterface.playing.get()){
            playPauseCorner1.setX(pauseCorner1X);
            playPauseCorner1.setY(pauseCorner1Y);

            playPauseCorner2.setX(pauseCorner2X);
            playPauseCorner2.setY(pauseCorner2Y);

            playPauseCorner3.setX(pauseCorner3X);
            playPauseCorner3.setY(pauseCorner3Y);

            playPauseCorner4.setX(pauseCorner4X);
            playPauseCorner4.setY(pauseCorner4Y);

            playPauseCorner5.setX(pauseCorner5X);
            playPauseCorner5.setY(pauseCorner5Y);

            playPauseCorner6.setX(pauseCorner6X);
            playPauseCorner6.setY(pauseCorner6Y);

            playPauseCorner7.setX(pauseCorner7X);
            playPauseCorner7.setY(pauseCorner7Y);

            playPauseCorner8.setX(pauseCorner8X);
            playPauseCorner8.setY(pauseCorner8Y);

            playPausePane.setVisible(true);
        }
        else {
            playPausePane.setVisible(true);
        }


        nextVideoButtonPane.getChildren().addAll(nextVideoButton, nextVideoIcon);
        nextVideoButtonPane.setPrefSize(60, 60);
        nextVideoButtonPane.setMaxSize(60, 60);
        nextVideoButtonPane.translateXProperty().bind(videoImageViewWrapper.widthProperty().multiply(0.25));
        nextVideoButtonPane.setVisible(false);

        nextVideoButton.setOnMouseEntered( e -> nextVideoButtonHoverOn());

        nextVideoButton.setOnMouseExited( e -> nextVideoButtonHoverOff());

        nextVideoButton.setPrefSize(60, 60);
        nextVideoButton.setBackground(Background.EMPTY);
        nextVideoButton.setCursor(Cursor.HAND);
        nextVideoButton.setOnAction(e -> mediaInterface.playNext());


        nextVideoSVG.setContent(SVG.NEXT_VIDEO.getContent());

        nextVideoIcon.setPrefSize(40, 40);
        nextVideoIcon.setMaxSize(40, 40);
        nextVideoIcon.setMouseTransparent(true);
        nextVideoIcon.getStyleClass().add("miniplayerIcon");
        nextVideoIcon.setShape(nextVideoSVG);

        Platform.runLater(this::loadTooltips);

    }

    private void loadTooltips(){

        previousVideoButtonTooltip = new ControlTooltip(mainController,"Previous video", mainController.hotkeyController.getHotkeyString(Action.PREVIOUS), previousVideoButton, 0, TooltipType.MINIPLAYER_TOOLTIP);
        playButtonTooltip = new ControlTooltip(mainController, "Play", mainController.hotkeyController.getHotkeyString(Action.PLAY_PAUSE2), playButton, 0, TooltipType.MINIPLAYER_TOOLTIP);
        nextVideoButtonTooltip = new ControlTooltip(mainController, "Next video", mainController.hotkeyController.getHotkeyString(Action.NEXT), nextVideoButton, 0, TooltipType.MINIPLAYER_TOOLTIP);


        if(menuController.queuePage.queueBox.queue.isEmpty() || menuController.queuePage.queueBox.activeItem.get() == null || (menuController.queuePage.queueBox.activeIndex.get() == 0 && controlBarController.durationSlider.getValue() <= 5)) disablePreviousVideoButton();
        else enablePreviousVideoButton();


        if(mediaInterface.mediaActive.get()) enablePlayButton();
        else disablePlayButton();

        if(menuController.queuePage.queueBox.queue.isEmpty() || (menuController.queuePage.queueBox.activeItem.get() != null && menuController.queuePage.queueBox.activeIndex.get() >= menuController.queuePage.queueBox.queue.size() - 1)) disableNextVideoButton();
        else enableNextVideoButton();
    }

    public void initActions(){
        miniplayer.scene.setMoveControl(videoImageViewWrapper);
        miniplayer.scene.removeDefaultCSS();
        miniplayer.scene.setSnapEnabled(false);
    }

    public void moveIndicators(){
        mainController.actionIndicator. moveToMiniplayer();
        mainController.forwardsIndicator.moveToMiniplayer();
        mainController.backwardsIndicator.moveToMiniplayer();
        mainController.valueIndicator.moveToMiniplayer();
    }

    public void closeButtonHoverOn(){
        AnimationsClass.animateBackgroundColor(closeIcon, (Color) closeIcon.getBackground().getFills().get(0).getFill(), Color.rgb(255, 255, 255), 200);
    }

    public void closeButtonHoverOff(){
        AnimationsClass.animateBackgroundColor(closeIcon, (Color) closeIcon.getBackground().getFills().get(0).getFill(), Color.rgb(200, 200, 200), 200);
    }

    public void previousVideoButtonHoverOn(){
        previousVideoButtonHover = true;

        if(controlBarController.previousVideoButtonEnabled){
            AnimationsClass.animateBackgroundColor(previousVideoIcon, (Color) previousVideoIcon.getBackground().getFills().get(0).getFill(), Color.rgb(255, 255, 255), 200);
        }
        else {
            AnimationsClass.animateBackgroundColor(previousVideoIcon, (Color) previousVideoIcon.getBackground().getFills().get(0).getFill(), Color.rgb(130, 130, 130), 200);
        }
    }

    public void previousVideoButtonHoverOff(){
        previousVideoButtonHover = false;

        if(controlBarController.previousVideoButtonEnabled){
            AnimationsClass.animateBackgroundColor(previousVideoIcon, (Color) previousVideoIcon.getBackground().getFills().get(0).getFill(), Color.rgb(200, 200, 200), 200);
        }
        else {
            AnimationsClass.animateBackgroundColor(previousVideoIcon, (Color) previousVideoIcon.getBackground().getFills().get(0).getFill(), Color.rgb(100, 100, 100), 200);
        }
    }

    public void playButtonHoverOn(){
        playButtonHover = true;

        Color fromColor;
        Color toColor;

        if (controlBarController.playButtonEnabled) {
            fromColor = Color.rgb(200, 200, 200);
            toColor = Color.rgb(255, 255, 255);

            playButtonTooltip.mouseHover.set(true);
        }
        else {
            fromColor = Color.rgb(120, 120, 120);
            toColor = Color.rgb(150, 150, 150);
        }

        Rectangle rect = new Rectangle();
        rect.setFill(fromColor);

        FillTransition playIconTransition = new FillTransition();
        playIconTransition.setShape(rect);
        playIconTransition.setDuration(Duration.millis(200));
        playIconTransition.setFromValue(fromColor);
        playIconTransition.setToValue(toColor);

        playIconTransition.setInterpolator(new Interpolator() {
            @Override
            protected double curve(double t) {
                replayIcon.setBackground(new Background(new BackgroundFill(rect.getFill(), CornerRadii.EMPTY, Insets.EMPTY)));
                return t;
            }
        });

        FillTransition playPauseLeftTransition = new FillTransition(Duration.millis(200), playPauseLeftPath);
        playPauseLeftTransition.setFromValue(fromColor);
        playPauseLeftTransition.setToValue(toColor);

        FillTransition playPauseRightTransition = new FillTransition(Duration.millis(200), playPauseRightPath);
        playPauseRightTransition.setFromValue(fromColor);
        playPauseRightTransition.setToValue(toColor);

        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(playIconTransition, playPauseLeftTransition, playPauseRightTransition);
        parallelTransition.play();
    }

    public void playButtonHoverOff(){
        playButtonHover = false;

        Color fromColor;
        Color toColor;

        if (controlBarController.playButtonEnabled) {
            fromColor = Color.rgb(255, 255, 255);
            toColor = Color.rgb(200, 200, 200);
        }
        else {
            fromColor = Color.rgb(150, 150, 150);
            toColor = Color.rgb(120, 120, 120);
        }

        Rectangle rect = new Rectangle();
        rect.setFill(fromColor);

        FillTransition playIconTransition = new FillTransition();
        playIconTransition.setShape(rect);
        playIconTransition.setDuration(Duration.millis(200));
        playIconTransition.setFromValue(fromColor);
        playIconTransition.setToValue(toColor);

        playIconTransition.setInterpolator(new Interpolator() {
            @Override
            protected double curve(double t) {
                replayIcon.setBackground(new Background(new BackgroundFill(rect.getFill(), CornerRadii.EMPTY, Insets.EMPTY)));
                return t;
            }
        });

        FillTransition playPauseLeftTransition = new FillTransition(Duration.millis(200), playPauseLeftPath);
        playPauseLeftTransition.setFromValue(fromColor);
        playPauseLeftTransition.setToValue(toColor);

        FillTransition playPauseRightTransition = new FillTransition(Duration.millis(200), playPauseRightPath);
        playPauseRightTransition.setFromValue(fromColor);
        playPauseRightTransition.setToValue(toColor);

        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(playIconTransition, playPauseLeftTransition, playPauseRightTransition);
        parallelTransition.play();

        playButtonTooltip.mouseHover.set(false);
    }

    public void nextVideoButtonHoverOn(){
        nextVideoButtonHover = true;

        if(controlBarController.nextVideoButtonEnabled){
            AnimationsClass.animateBackgroundColor(nextVideoIcon, (Color) nextVideoIcon.getBackground().getFills().get(0).getFill(), Color.rgb(255, 255, 255), 200);
        }
        else {
            AnimationsClass.animateBackgroundColor(nextVideoIcon, (Color) nextVideoIcon.getBackground().getFills().get(0).getFill(), Color.rgb(130, 130, 130), 200);
        }
    }

    public void nextVideoButtonHoverOff(){
        nextVideoButtonHover = false;

        if(controlBarController.nextVideoButtonEnabled){
            AnimationsClass.animateBackgroundColor(nextVideoIcon, (Color) nextVideoIcon.getBackground().getFills().get(0).getFill(), Color.rgb(200, 200, 200), 200);
        }
        else {
            AnimationsClass.animateBackgroundColor(nextVideoIcon, (Color) nextVideoIcon.getBackground().getFills().get(0).getFill(), Color.rgb(100, 100, 100), 200);
        }
    }


    public void enlargeButtons(){

        closeButtonPane.setPrefSize(30, 30);
        closeButtonPane.setMaxSize(30, 30);

        closeButton.setPrefSize(30, 30);
        closeButton.setMaxSize(30, 30);

        closeIcon.setPrefSize(20, 20);
        closeIcon.setMaxSize(20, 20);


        previousVideoButtonPane.setPrefSize(60, 60);
        previousVideoButtonPane.setMaxSize(60, 60);

        previousVideoButton.setPrefSize(60, 60);

        previousVideoIcon.setPrefSize(40, 40);
        previousVideoIcon.setMaxSize(40, 40);


        nextVideoButtonPane.setPrefSize(60, 60);
        nextVideoButtonPane.setMaxSize(60, 60);

        nextVideoButton.setPrefSize(60, 60);

        nextVideoIcon.setPrefSize(40, 40);
        nextVideoIcon.setMaxSize(40, 40);


        playButtonPane.setPrefSize(60, 60);
        playButtonPane.setMaxSize(60, 60);

        playButton.setPrefSize(60, 60);

        replayIcon.setPrefSize(48, 48);
        replayIcon.setMaxSize(48, 48);

        playPausePane.setPrefSize(36, 44);
        playPausePane.setMaxSize(36, 44);

        pauseCorner1X = 0;
        pauseCorner1Y = 0;

        pauseCorner2X = 0;
        pauseCorner2Y = 44;

        pauseCorner3X = 12;
        pauseCorner3Y = 44;

        pauseCorner4X = 12;
        pauseCorner4Y = 0;

        pauseCorner5X = 24;
        pauseCorner5Y = 0;

        pauseCorner6X = 24;
        pauseCorner6Y = 44;

        pauseCorner7X = 36;
        pauseCorner7Y = 44;

        pauseCorner8X = 36;
        pauseCorner8Y = 0;



        playCorner1X = 0;
        playCorner1Y = 0;

        playCorner2X = 0;
        playCorner2Y = 44;

        playCorner3X = 19;
        playCorner3Y = 33;

        playCorner4X = 19;
        playCorner4Y = 11;

        playCorner5X = 18;
        playCorner5Y = 10.5;

        playCorner6X = 18;
        playCorner6Y = 33.5;

        playCorner7X = 36;
        playCorner7Y = 22;

        playCorner8X = 36;
        playCorner8Y = 22;


        if(playPauseTransition != null && playPauseTransition.getStatus() == Animation.Status.RUNNING)
            playPauseTransition.stop();


        if(mediaInterface.playing.get()){
            playPauseCorner1.setX(pauseCorner1X);
            playPauseCorner1.setY(pauseCorner1Y);

            playPauseCorner2.setX(pauseCorner2X);
            playPauseCorner2.setY(pauseCorner2Y);

            playPauseCorner3.setX(pauseCorner3X);
            playPauseCorner3.setY(pauseCorner3Y);

            playPauseCorner4.setX(pauseCorner4X);
            playPauseCorner4.setY(pauseCorner4Y);

            playPauseCorner5.setX(pauseCorner5X);
            playPauseCorner5.setY(pauseCorner5Y);

            playPauseCorner6.setX(pauseCorner6X);
            playPauseCorner6.setY(pauseCorner6Y);

            playPauseCorner7.setX(pauseCorner7X);
            playPauseCorner7.setY(pauseCorner7Y);

            playPauseCorner8.setX(pauseCorner8X);
            playPauseCorner8.setY(pauseCorner8Y);

        }
        else if(!mediaInterface.atEnd){
            playPauseCorner1.setX(playCorner1X);
            playPauseCorner1.setY(playCorner1Y);

            playPauseCorner2.setX(playCorner2X);
            playPauseCorner2.setY(playCorner2Y);

            playPauseCorner3.setX(playCorner3X);
            playPauseCorner3.setY(playCorner3Y);

            playPauseCorner4.setX(playCorner4X);
            playPauseCorner4.setY(playCorner4Y);

            playPauseCorner5.setX(playCorner5X);
            playPauseCorner5.setY(playCorner5Y);

            playPauseCorner6.setX(playCorner6X);
            playPauseCorner6.setY(playCorner6Y);

            playPauseCorner7.setX(playCorner7X);
            playPauseCorner7.setY(playCorner7Y);

            playPauseCorner8.setX(playCorner8X);
            playPauseCorner8.setY(playCorner8Y);
        }
    }

    public void reduceButtons(){
        closeButtonPane.setPrefSize(25, 25);
        closeButtonPane.setMaxSize(25, 25);

        closeButton.setPrefSize(25, 25);
        closeButton.setMaxSize(25, 25);

        closeIcon.setPrefSize(16, 16);
        closeIcon.setMaxSize(16, 16);


        previousVideoButtonPane.setPrefSize(40, 40);
        previousVideoButtonPane.setMaxSize(40, 40);

        previousVideoButton.setPrefSize(40, 40);

        previousVideoIcon.setPrefSize(25, 25);
        previousVideoIcon.setMaxSize(25, 25);


        nextVideoButtonPane.setPrefSize(40, 40);
        nextVideoButtonPane.setMaxSize(40, 40);

        nextVideoButton.setPrefSize(40, 40);

        nextVideoIcon.setPrefSize(25, 25);
        nextVideoIcon.setMaxSize(25, 25);


        playButtonPane.setPrefSize(40, 40);
        playButtonPane.setMaxSize(40, 40);

        playButton.setPrefSize(40, 40);

        replayIcon.setPrefSize(32, 32);
        replayIcon.setMaxSize(32, 32);


        playPausePane.setPrefSize(24, 30);
        playPausePane.setMaxSize(24, 30);


        pauseCorner1X = 0;
        pauseCorner1Y = 0;

        pauseCorner2X = 0;
        pauseCorner2Y = 30;

        pauseCorner3X = 8;
        pauseCorner3Y = 30;

        pauseCorner4X = 8;
        pauseCorner4Y = 0;

        pauseCorner5X = 16;
        pauseCorner5Y = 0;

        pauseCorner6X = 16;
        pauseCorner6Y = 30;

        pauseCorner7X = 24;
        pauseCorner7Y = 30;

        pauseCorner8X = 24;
        pauseCorner8Y = 0;



        playCorner1X = 0;
        playCorner1Y = 0;

        playCorner2X = 0;
        playCorner2Y = 30;

        playCorner3X = 12.5;
        playCorner3Y = 22;

        playCorner4X = 12.5;
        playCorner4Y = 7.5;

        playCorner5X = 12;
        playCorner5Y = 7;

        playCorner6X = 12;
        playCorner6Y = 22.5;

        playCorner7X = 24;
        playCorner7Y = 15;

        playCorner8X = 24;
        playCorner8Y = 15;



        if(playPauseTransition != null && playPauseTransition.getStatus() == Animation.Status.RUNNING)
            playPauseTransition.stop();


        if(mediaInterface.playing.get()){
            playPauseCorner1.setX(pauseCorner1X);
            playPauseCorner1.setY(pauseCorner1Y);

            playPauseCorner2.setX(pauseCorner2X);
            playPauseCorner2.setY(pauseCorner2Y);

            playPauseCorner3.setX(pauseCorner3X);
            playPauseCorner3.setY(pauseCorner3Y);

            playPauseCorner4.setX(pauseCorner4X);
            playPauseCorner4.setY(pauseCorner4Y);

            playPauseCorner5.setX(pauseCorner5X);
            playPauseCorner5.setY(pauseCorner5Y);

            playPauseCorner6.setX(pauseCorner6X);
            playPauseCorner6.setY(pauseCorner6Y);

            playPauseCorner7.setX(pauseCorner7X);
            playPauseCorner7.setY(pauseCorner7Y);

            playPauseCorner8.setX(pauseCorner8X);
            playPauseCorner8.setY(pauseCorner8Y);

        }
        else if(!mediaInterface.atEnd){
            playPauseCorner1.setX(playCorner1X);
            playPauseCorner1.setY(playCorner1Y);

            playPauseCorner2.setX(playCorner2X);
            playPauseCorner2.setY(playCorner2Y);

            playPauseCorner3.setX(playCorner3X);
            playPauseCorner3.setY(playCorner3Y);

            playPauseCorner4.setX(playCorner4X);
            playPauseCorner4.setY(playCorner4Y);

            playPauseCorner5.setX(playCorner5X);
            playPauseCorner5.setY(playCorner5Y);

            playPauseCorner6.setX(playCorner6X);
            playPauseCorner6.setY(playCorner6Y);

            playPauseCorner7.setX(playCorner7X);
            playPauseCorner7.setY(playCorner7Y);

            playPauseCorner8.setX(playCorner8X);
            playPauseCorner8.setY(playCorner8Y);
        }
    }

    public void showControls(){

        if(!mainController.miniplayerActive) return;

        controlsVisible = true;


        controlsBackground.setVisible(true);

        closeButtonPane.setVisible(true);
        previousVideoButtonPane.setVisible(true);
        playButtonPane.setVisible(true);
        nextVideoButtonPane.setVisible(true);

        sliderPane.setVisible(true);

        if(mainController.subtitlesController.subtitlesBox.subtitlesLocation == Pos.BOTTOM_LEFT || mainController.subtitlesController.subtitlesBox.subtitlesLocation == Pos.BOTTOM_CENTER || mainController.subtitlesController.subtitlesBox.subtitlesLocation == Pos.BOTTOM_RIGHT){
            mainController.subtitlesController.subtitlesBox.subtitlesContainer.setTranslateY(-30);
        }
    }

    public void hideControls() {

        if (!mainController.seekingWithKeys && progressBarTimer.getStatus() != Animation.Status.RUNNING && !slider.isValueChanging() && mainController.miniplayerActive){

            controlsVisible = false;

            controlsBackground.setVisible(false);
            closeButtonPane.setVisible(false);
            previousVideoButtonPane.setVisible(false);
            playButtonPane.setVisible(false);
            nextVideoButtonPane.setVisible(false);
            sliderPane.setVisible(false);

            if(mainController.subtitlesController.subtitlesBox.subtitlesLocation == Pos.BOTTOM_LEFT || mainController.subtitlesController.subtitlesBox.subtitlesLocation == Pos.BOTTOM_CENTER || mainController.subtitlesController.subtitlesBox.subtitlesLocation == Pos.BOTTOM_RIGHT){
                mainController.subtitlesController.subtitlesBox.subtitlesContainer.setTranslateY(-10);
            }
        }
    }


    public void play(){

        if(playPauseTransition != null && playPauseTransition.getStatus() == Animation.Status.RUNNING)
            playPauseTransition.stop();

        if(replayIcon.isVisible() || !controlsVisible){
            replayIcon.setVisible(false);
            playPausePane.setVisible(true);

            playPauseCorner1.setX(pauseCorner1X);
            playPauseCorner1.setY(pauseCorner1Y);

            playPauseCorner2.setX(pauseCorner2X);
            playPauseCorner2.setY(pauseCorner2Y);

            playPauseCorner3.setX(pauseCorner3X);
            playPauseCorner3.setY(pauseCorner3Y);

            playPauseCorner4.setX(pauseCorner4X);
            playPauseCorner4.setY(pauseCorner4Y);

            playPauseCorner5.setX(pauseCorner5X);
            playPauseCorner5.setY(pauseCorner5Y);

            playPauseCorner6.setX(pauseCorner6X);
            playPauseCorner6.setY(pauseCorner6Y);

            playPauseCorner7.setX(pauseCorner7X);
            playPauseCorner7.setY(pauseCorner7Y);

            playPauseCorner8.setX(pauseCorner8X);
            playPauseCorner8.setY(pauseCorner8Y);
        }
        else {
            playToPauseMorph();
        }

        if(playButtonTooltip != null) playButtonTooltip.updateActionText("Pause");
    }


    public void pause(){

        if(playPauseTransition != null && playPauseTransition.getStatus() == Animation.Status.RUNNING)
            playPauseTransition.stop();

        if(replayIcon.isVisible() || !controlsVisible){
            replayIcon.setVisible(false);
            playPausePane.setVisible(true);

            playPauseCorner1.setX(playCorner1X);
            playPauseCorner1.setY(playCorner1Y);

            playPauseCorner2.setX(playCorner2X);
            playPauseCorner2.setY(playCorner2Y);

            playPauseCorner3.setX(playCorner3X);
            playPauseCorner3.setY(playCorner3Y);

            playPauseCorner4.setX(playCorner4X);
            playPauseCorner4.setY(playCorner4Y);

            playPauseCorner5.setX(playCorner5X);
            playPauseCorner5.setY(playCorner5Y);

            playPauseCorner6.setX(playCorner6X);
            playPauseCorner6.setY(playCorner6Y);

            playPauseCorner7.setX(playCorner7X);
            playPauseCorner7.setY(playCorner7Y);

            playPauseCorner8.setX(playCorner8X);
            playPauseCorner8.setY(playCorner8Y);
        }
        else {
            pauseToPlayMorph();
        }

        if(playButtonTooltip != null) playButtonTooltip.updateActionText("Play");
    }

    public void end(){

        if(playPauseTransition != null && playPauseTransition.getStatus() == Animation.Status.RUNNING)
            playPauseTransition.stop();

        playPausePane.setVisible(false);

        replayIcon.setVisible(true);

        if(playButtonTooltip != null) playButtonTooltip.updateActionText("Replay");
    }


    public void enablePreviousVideoButton(){

        if(previousVideoButtonHover){
            previousVideoIcon.setStyle("-fx-background-color: rgb(255, 255, 255);");
        }
        else {
            previousVideoIcon.setStyle("-fx-background-color: rgb(200, 200, 200);");
        }

        if(controlBarController.durationSlider.getValue() > 5) {
            previousVideoButtonTooltip.updateActionText("Replay");
            previousVideoButtonTooltip.updateHotkeyText("");
        }
        else {
            previousVideoButtonTooltip.updateActionText("Previous video");
            previousVideoButtonTooltip.updateHotkeyText(mainController.hotkeyController.getHotkeyString(Action.PREVIOUS));
        }

        previousVideoButtonTooltip.enableTooltip();
        if(previousVideoButtonHover) previousVideoButtonTooltip.mouseHover.set(true);


    }

    public void disablePreviousVideoButton(){

        if(previousVideoButtonHover){
            previousVideoIcon.setStyle("-fx-background-color: rgb(130, 130, 130);");
        }
        else {
            previousVideoIcon.setStyle("-fx-background-color: rgb(100, 100, 100);");
        }

        previousVideoButtonTooltip.disableTooltip();
    }

    public void enablePlayButton(){

        if (playButtonHover) {
            replayIcon.setBackground(new Background(new BackgroundFill(Color.rgb(255, 255, 255), CornerRadii.EMPTY, Insets.EMPTY)));
            playPauseLeftPath.setFill(Color.rgb(255, 255, 255));
            playPauseRightPath.setFill(Color.rgb(255, 255, 255));
        }
        else {
            replayIcon.setBackground(new Background(new BackgroundFill(Color.rgb(200, 200, 200), CornerRadii.EMPTY, Insets.EMPTY)));
            playPauseLeftPath.setFill(Color.rgb(200, 200, 200));
            playPauseRightPath.setFill(Color.rgb(200, 200, 200));
        }

        if(mediaInterface.atEnd) playButtonTooltip.updateActionText("Replay");
        else if(mediaInterface.playing.get()) playButtonTooltip.updateActionText("Pause");
        else playButtonTooltip.updateActionText("Play");

        playButtonTooltip.enableTooltip();
        if(playButtonHover) playButtonTooltip.mouseHover.set(true);
    }

    public void disablePlayButton(){

        if (playButtonHover) {
            replayIcon.setBackground(new Background(new BackgroundFill(Color.rgb(150, 150, 150), CornerRadii.EMPTY, Insets.EMPTY)));
            playPauseLeftPath.setFill(Color.rgb(150, 150, 150));
            playPauseRightPath.setFill(Color.rgb(150, 150, 150));
        }
        else {
            replayIcon.setBackground(new Background(new BackgroundFill(Color.rgb(120, 120, 120), CornerRadii.EMPTY, Insets.EMPTY)));
            playPauseLeftPath.setFill(Color.rgb(120, 120, 120));
            playPauseRightPath.setFill(Color.rgb(120, 120, 120));
        }

        playButtonTooltip.disableTooltip();
    }

    public void enableNextVideoButton(){

        if(nextVideoButtonHover){
            nextVideoIcon.setStyle("-fx-background-color: rgb(255, 255, 255);");
        }
        else {
            nextVideoIcon.setStyle("-fx-background-color: rgb(200, 200, 200);");
        }

        nextVideoButtonTooltip.enableTooltip();
        if(nextVideoButtonHover) nextVideoButtonTooltip.mouseHover.set(true);
    }

    public void disableNextVideoButton(){

        if(nextVideoButtonHover){
            nextVideoIcon.setStyle("-fx-background-color: rgb(130, 130, 130);");
        }
        else {
            nextVideoIcon.setStyle("-fx-background-color: rgb(100, 100, 100);");
        }

        nextVideoButtonTooltip.disableTooltip();
    }

    public void sliderHoverOn() {
        ScaleTransition sliderThumbHoverOn = AnimationsClass.scaleAnimation(100, slider.lookup(".thumb"), slider.lookup(".thumb").getScaleX(), 1, slider.lookup(".thumb").getScaleY(), 1, false, 1, false);
        ScaleTransition sliderTrackHoverOn = AnimationsClass.scaleAnimation(100, progressBar, 1, 1, progressBar.getScaleY(), 1.6, false, 1, false);
        AnimationsClass.parallelAnimation(true, sliderThumbHoverOn, sliderTrackHoverOn);
    }


    public void sliderHoverOff() {
        ScaleTransition sliderThumbHoverOff = AnimationsClass.scaleAnimation(100, slider.lookup(".thumb"), slider.lookup(".thumb").getScaleX(), 0, slider.lookup(".thumb").getScaleY(), 0, false, 1, false);
        ScaleTransition sliderTrackHoverOff = AnimationsClass.scaleAnimation(100, progressBar, 1, 1, progressBar.getScaleY(), 1, false, 1, false);
        AnimationsClass.parallelAnimation(true, sliderThumbHoverOff, sliderTrackHoverOff);
    }


    private void playToPauseMorph(){

        replayIcon.setVisible(false);
        playPausePane.setVisible(true);

        playPauseTransition = new Timeline(
                new KeyFrame(Duration.millis(250),
                        new KeyValue(playPauseCorner1.xProperty(), pauseCorner1X),
                        new KeyValue(playPauseCorner1.yProperty(), pauseCorner1Y),

                        new KeyValue(playPauseCorner2.xProperty(), pauseCorner2X),
                        new KeyValue(playPauseCorner2.yProperty(), pauseCorner2Y),

                        new KeyValue(playPauseCorner3.xProperty(), pauseCorner3X),
                        new KeyValue(playPauseCorner3.yProperty(), pauseCorner3Y),

                        new KeyValue(playPauseCorner4.xProperty(), pauseCorner4X),
                        new KeyValue(playPauseCorner4.yProperty(), pauseCorner4Y),

                        new KeyValue(playPauseCorner5.xProperty(), pauseCorner5X),
                        new KeyValue(playPauseCorner5.yProperty(), pauseCorner5Y),

                        new KeyValue(playPauseCorner6.xProperty(), pauseCorner6X),
                        new KeyValue(playPauseCorner6.yProperty(), pauseCorner6Y),

                        new KeyValue(playPauseCorner7.xProperty(), pauseCorner7X),
                        new KeyValue(playPauseCorner7.yProperty(), pauseCorner7Y),

                        new KeyValue(playPauseCorner8.xProperty(), pauseCorner8X),
                        new KeyValue(playPauseCorner8.yProperty(), pauseCorner8Y))
        );

        playPauseTransition.play();
    }

    private void pauseToPlayMorph(){

        replayIcon.setVisible(false);
        playPausePane.setVisible(true);

        playPauseTransition = new Timeline(
                new KeyFrame(Duration.millis(250),
                        new KeyValue(playPauseCorner1.xProperty(), playCorner1X),
                        new KeyValue(playPauseCorner1.yProperty(), playCorner1Y),

                        new KeyValue(playPauseCorner2.xProperty(), playCorner2X),
                        new KeyValue(playPauseCorner2.yProperty(), playCorner2Y),

                        new KeyValue(playPauseCorner3.xProperty(), playCorner3X),
                        new KeyValue(playPauseCorner3.yProperty(), playCorner3Y),

                        new KeyValue(playPauseCorner4.xProperty(), playCorner4X),
                        new KeyValue(playPauseCorner4.yProperty(), playCorner4Y),

                        new KeyValue(playPauseCorner5.xProperty(), playCorner5X),
                        new KeyValue(playPauseCorner5.yProperty(), playCorner5Y),

                        new KeyValue(playPauseCorner6.xProperty(), playCorner6X),
                        new KeyValue(playPauseCorner6.yProperty(), playCorner6Y),

                        new KeyValue(playPauseCorner7.xProperty(), playCorner7X),
                        new KeyValue(playPauseCorner7.yProperty(), playCorner7Y),

                        new KeyValue(playPauseCorner8.xProperty(), playCorner8X),
                        new KeyValue(playPauseCorner8.yProperty(), playCorner8Y))
        );

        playPauseTransition.play();
    }
}
