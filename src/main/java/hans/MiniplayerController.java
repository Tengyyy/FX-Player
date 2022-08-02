package hans;

import javafx.animation.Animation;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MiniplayerController {

    MainController mainController;
    ControlBarController controlBarController;
    MenuController menuController;
    MediaInterface mediaInterface;
    Miniplayer miniplayer;


    ImageView videoImageView = new ImageView();

    StackPane videoImageViewWrapper = new StackPane();
    StackPane videoImageViewInnerWrapper = new StackPane();



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
    Region playIcon = new Region();
    Region nextVideoIcon = new Region();
    Region closeIcon = new Region();

    SVGPath previousVideoSVG = new SVGPath();
    SVGPath playSVG = new SVGPath();
    SVGPath pauseSVG = new SVGPath();
    SVGPath replaySVG = new SVGPath();
    SVGPath nextVideoSVG = new SVGPath();
    SVGPath closeSVG = new SVGPath();

    StackPane sliderPane = new StackPane();
    ProgressBar progressBar = new ProgressBar();
    Slider slider = new Slider();

    boolean sliderHover = false;

    ControlTooltip previousVideoButtonTooltip, playButtonTooltip, nextVideoButtonTooltip;


    DoubleProperty videoImageViewWidth;
    DoubleProperty videoImageViewHeight;


    ChangeListener<? super Number> widthListener;

    boolean previousVideoButtonHover = false, playButtonHover = false, nextVideoButtonHover = false;

    boolean miniplayerHover = false;


    PauseTransition progressBarTimer = new PauseTransition(Duration.millis(1000));

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

        videoImageViewInnerWrapper.setBackground(Background.EMPTY);
        videoImageViewInnerWrapper.setMouseTransparent(true);
        videoImageViewInnerWrapper.getChildren().add(videoImageView);
        StackPane.setAlignment(videoImageViewInnerWrapper, Pos.CENTER);

        Rectangle mediaClip = new Rectangle();
        mediaClip.setArcHeight(20);
        mediaClip.setArcWidth(20);
        mediaClip.widthProperty().bind(videoImageViewInnerWrapper.widthProperty());
        mediaClip.heightProperty().bind(videoImageViewInnerWrapper.heightProperty());
        videoImageViewInnerWrapper.setClip(mediaClip);


        videoImageView.setPreserveRatio(true);

        videoImageViewWidth = videoImageView.fitWidthProperty();
        videoImageViewHeight = videoImageView.fitHeightProperty();
        Platform.runLater(() -> {
            videoImageViewWidth.bind(videoImageViewWrapper.widthProperty().subtract(2));
            videoImageViewHeight.bind(videoImageViewWrapper.heightProperty().subtract(2));
        });
        videoImageView.setMouseTransparent(true);


        mainController.captionsController.mediaWidthMultiplier.set(0.4);
        mainController.captionsController.resizeCaptions();


        mainController.sizeMultiplier.set(0.6);
        if(mainController.actionIndicator.wrapper.isVisible()) mainController.actionIndicator.updateSize();
        mainController.forwardsIndicator.resize();
        mainController.backwardsIndicator.resize();
        mainController.valueIndicator.resize();

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
        progressBar.setProgress(0);
        StackPane.setMargin(progressBar, new Insets(0, 11, 0, 12));

        progressBarTimer.setOnFinished(e -> {
            if(!miniplayerHover && !slider.isValueChanging()) sliderPane.setVisible(false);
        });


        slider.setPrefHeight(16);
        slider.setMaxHeight(Region.USE_PREF_SIZE);
        slider.setMinHeight(Region.USE_PREF_SIZE);
        slider.setId("slider");
        StackPane.setMargin(slider, new Insets(0, 4, 0, 4));

        slider.addEventFilter(MouseEvent.DRAG_DETECTED, e -> {
            slider.setValueChanging(true);

        });
        slider.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> slider.setValueChanging(false));

        Platform.runLater(() -> {

            slider.lookup(".thumb").setScaleX(0);
            slider.lookup(".thumb").setScaleY(0);

            slider.lookup(".track").setCursor(Cursor.HAND);
            slider.lookup(".thumb").setCursor(Cursor.HAND);


        });

        slider.setOnMouseEntered((e) -> {
            sliderHover = true;
            sliderHoverOn();
        });


        slider.setOnMouseExited((e) -> {
            sliderHover = false;
            if (!e.isPrimaryButtonDown() && !e.isSecondaryButtonDown() && !e.isMiddleButtonDown()) {
                sliderHoverOff();
            }
        });

        slider.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            if(controlBarController.durationSlider.getValue() != newValue.doubleValue()) controlBarController.durationSlider.setValue(newValue.doubleValue());
        });

        slider.valueChangingProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue) { // pause video when user starts seeking

                showControls();

                mediaInterface.pause();

            } else {

                if (!sliderHover) sliderHoverOff();
                if(!miniplayerHover) hideControls();

                if(mediaInterface.mediaActive.get()) mediaInterface.seek(Duration.seconds(slider.getValue())); // seeks to exact position when user finishes dragging


                if (mediaInterface.atEnd) { // if user drags the duration slider to the end turn play button to replay button
                    mediaInterface.endMedia();

                } else if (mediaInterface.wasPlaying) { // starts playing the video in the new position when user finishes seeking with the slider
                    mediaInterface.play();
                }
            }
        });

        slider.setMax(controlBarController.durationSlider.getMax());
        sliderPane.setMouseTransparent(!mediaInterface.mediaActive.get());
        slider.setValue(controlBarController.durationSlider.getValue());

        progressBar.setProgress(controlBarController.durationSlider.getValue() / controlBarController.durationSlider.getMax());



        widthListener = (observableValue, oldValue, newValue) -> {

            if(newValue.doubleValue() < 500){
                reduceButtons();
            }
            else if(newValue.doubleValue() >= 500){
                enlargeButtons();
            }

            if(newValue.doubleValue() < 400){

                mainController.captionsController.mediaWidthMultiplier.set(0.3);
                mainController.captionsController.resizeCaptions();

                mainController.sizeMultiplier.set(0.35);
                if(mainController.actionIndicator.wrapper.isVisible()) mainController.actionIndicator.updateSize();
                mainController.forwardsIndicator.resize();
                mainController.backwardsIndicator.resize();
                mainController.valueIndicator.resize();
            }
            else if((newValue.doubleValue() >= 400 && newValue.doubleValue() < 600)){

                mainController.captionsController.mediaWidthMultiplier.set(0.4);
                mainController.captionsController.resizeCaptions();

                mainController.sizeMultiplier.set(0.5);
                if(mainController.actionIndicator.wrapper.isVisible()) mainController.actionIndicator.updateSize();
                mainController.forwardsIndicator.resize();
                mainController.backwardsIndicator.resize();
                mainController.valueIndicator.resize();

            }
            else if((newValue.doubleValue() >= 600 && newValue.doubleValue() < 800)){

                mainController.captionsController.mediaWidthMultiplier.set(0.55);
                mainController.captionsController.resizeCaptions();

                mainController.sizeMultiplier.set(0.6);
                if(mainController.actionIndicator.wrapper.isVisible()) mainController.actionIndicator.updateSize();
                mainController.forwardsIndicator.resize();
                mainController.backwardsIndicator.resize();
                mainController.valueIndicator.resize();

            }
            else if(newValue.doubleValue() >= 800){

                mainController.captionsController.mediaWidthMultiplier.set(0.65);
                mainController.captionsController.resizeCaptions();

                mainController.sizeMultiplier.set(0.7);
                if(mainController.actionIndicator.wrapper.isVisible()) mainController.actionIndicator.updateSize();
                mainController.forwardsIndicator.resize();
                mainController.backwardsIndicator.resize();
                mainController.valueIndicator.resize();

            }

        };

        videoImageViewInnerWrapper.widthProperty().addListener(widthListener);


        controlsBackground.setMouseTransparent(true);
        controlsBackground.setId("controlsBackground");
        controlsBackground.setVisible(false);

        StackPane.setAlignment(closeButtonPane, Pos.TOP_RIGHT);
        StackPane.setMargin(closeButtonPane, new Insets(5, 5, 0, 0));
        closeButtonPane.setPrefSize(30, 30);
        closeButtonPane.setMaxSize(30, 30);

        closeButtonPane.setVisible(false);

        closeButtonPane.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
        closeButtonHoverOn();
        });

        closeButtonPane.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            closeButtonHoverOff();
        });

        closeButtonPane.getChildren().addAll(closeButton, closeIcon);
        closeButton.setPrefSize(30, 30);
        closeButton.setCursor(Cursor.HAND);
        closeButton.setBackground(Background.EMPTY);
        closeButton.setOnAction(e -> {
            mainController.closeMiniplayer();
        });

        closeSVG.setContent(App.svgMap.get(SVG.CLOSE));

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

        previousVideoButtonPane.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
            previousVideoButtonHoverOn();
        });

        previousVideoButtonPane.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            previousVideoButtonHoverOff();
        });

        previousVideoButton.setPrefSize(60, 60);
        previousVideoButton.setBackground(Background.EMPTY);
        previousVideoButton.setCursor(Cursor.HAND);
        previousVideoButton.setOnAction(e -> {
            if(controlBarController.durationSlider.getValue() > 5){
                mediaInterface.replay();
            }
            else {
                if(!menuController.animationsInProgress.isEmpty()) return;
                mediaInterface.playPrevious();
            }
        });


        previousVideoSVG.setContent(App.svgMap.get(SVG.PREVIOUS_VIDEO));

        previousVideoIcon.setPrefSize(40, 40);
        previousVideoIcon.setMaxSize(40, 40);
        previousVideoIcon.setMouseTransparent(true);
        previousVideoIcon.getStyleClass().add("miniplayerIcon");
        previousVideoIcon.setShape(previousVideoSVG);
        previousVideoIcon.setEffect(new DropShadow());

        if((menuController.history.isEmpty() || menuController.historyBox.index == 0) && controlBarController.durationSlider.getValue() <= 5){
            disablePreviousVideoButton();
        }
        else {
            enablePreviousVideoButton();
        }


        playButtonPane.getChildren().addAll(playButton, playIcon);
        playButtonPane.setPrefSize(60, 60);
        playButtonPane.setMaxSize(60, 60);
        playButtonPane.setVisible(false);

        playButtonPane.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
            playButtonHoverOn();
        });

        playButtonPane.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            playButtonHoverOff();
        });

        playButton.setPrefSize(60, 60);
        playButton.setBackground(Background.EMPTY);
        playButton.setCursor(Cursor.HAND);
        playButton.setOnAction(e -> {
            if(mediaInterface.atEnd) mediaInterface.replay();
            else if(mediaInterface.playing.get()){
                mediaInterface.wasPlaying = false;
                mediaInterface.pause();
            }
            else mediaInterface.play();
        });


        playSVG.setContent(App.svgMap.get(SVG.PLAY));
        pauseSVG.setContent(App.svgMap.get(SVG.PAUSE));
        replaySVG.setContent(App.svgMap.get(SVG.REPLAY));

        playIcon.setPrefSize(40, 40);
        playIcon.setMaxSize(40, 40);
        playIcon.setMouseTransparent(true);
        playIcon.getStyleClass().add("miniplayerIcon");
        playIcon.setEffect(new DropShadow());
        if(mediaInterface.atEnd){
            playIcon.setShape(replaySVG);
            playIcon.setPrefSize(48, 48);
            playIcon.setMaxSize(48, 48);
        }
        else if(mediaInterface.playing.get()) playIcon.setShape(pauseSVG);
        else playIcon.setShape(playSVG);

        if(mediaInterface.mediaActive.get()){
            enablePlayButton();
        }
        else {
            disablePlayButton();
        }


        nextVideoButtonPane.getChildren().addAll(nextVideoButton, nextVideoIcon);
        nextVideoButtonPane.setPrefSize(60, 60);
        nextVideoButtonPane.setMaxSize(60, 60);
        nextVideoButtonPane.translateXProperty().bind(videoImageViewWrapper.widthProperty().multiply(0.25));
        nextVideoButtonPane.setVisible(false);

        nextVideoButtonPane.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
            nextVideoButtonHoverOn();
        });

        nextVideoButtonPane.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            nextVideoButtonHoverOff();
        });

        nextVideoButton.setPrefSize(60, 60);
        nextVideoButton.setBackground(Background.EMPTY);
        nextVideoButton.setCursor(Cursor.HAND);
        nextVideoButton.setOnAction(e -> {
            if(!menuController.animationsInProgress.isEmpty()) return;

            mediaInterface.playNext();
        });


        nextVideoSVG.setContent(App.svgMap.get(SVG.NEXT_VIDEO));

        nextVideoIcon.setPrefSize(40, 40);
        nextVideoIcon.setMaxSize(40, 40);
        nextVideoIcon.setMouseTransparent(true);
        nextVideoIcon.getStyleClass().add("miniplayerIcon");
        nextVideoIcon.setShape(nextVideoSVG);
        nextVideoIcon.setEffect(new DropShadow());

        if(menuController.queue.isEmpty() && (menuController.historyBox.index == -1 || menuController.historyBox.index == menuController.history.size() -1)){
            disableNextVideoButton();
        }
        else {
            enableNextVideoButton();
        }

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
        AnimationsClass.AnimateBackgroundColor(closeIcon, (Color) closeIcon.getBackground().getFills().get(0).getFill(), Color.rgb(255, 255, 255), 200);
    }

    public void closeButtonHoverOff(){
        AnimationsClass.AnimateBackgroundColor(closeIcon, (Color) closeIcon.getBackground().getFills().get(0).getFill(), Color.rgb(200, 200, 200), 200);
    }

    public void previousVideoButtonHoverOn(){
        previousVideoButtonHover = true;

        if(controlBarController.previousVideoButtonEnabled){
            AnimationsClass.AnimateBackgroundColor(previousVideoIcon, (Color) previousVideoIcon.getBackground().getFills().get(0).getFill(), Color.rgb(255, 255, 255), 200);
        }
        else {
            AnimationsClass.AnimateBackgroundColor(previousVideoIcon, (Color) previousVideoIcon.getBackground().getFills().get(0).getFill(), Color.rgb(130, 130, 130), 200);
        }
    }

    public void previousVideoButtonHoverOff(){
        previousVideoButtonHover = false;

        if(controlBarController.previousVideoButtonEnabled){
            AnimationsClass.AnimateBackgroundColor(previousVideoIcon, (Color) previousVideoIcon.getBackground().getFills().get(0).getFill(), Color.rgb(200, 200, 200), 200);
        }
        else {
            AnimationsClass.AnimateBackgroundColor(previousVideoIcon, (Color) previousVideoIcon.getBackground().getFills().get(0).getFill(), Color.rgb(100, 100, 100), 200);
        }
    }

    public void playButtonHoverOn(){
        playButtonHover = true;

        if(controlBarController.playButtonEnabled){
            AnimationsClass.AnimateBackgroundColor(playIcon, (Color) playIcon.getBackground().getFills().get(0).getFill(), Color.rgb(255, 255, 255), 200);
        }
        else {
            AnimationsClass.AnimateBackgroundColor(playIcon, (Color) playIcon.getBackground().getFills().get(0).getFill(), Color.rgb(130, 130, 130), 200);
        }
    }

    public void playButtonHoverOff(){
        playButtonHover = false;

        if(controlBarController.playButtonEnabled){
            AnimationsClass.AnimateBackgroundColor(playIcon, (Color) playIcon.getBackground().getFills().get(0).getFill(), Color.rgb(200, 200, 200), 200);
        }
        else {
            AnimationsClass.AnimateBackgroundColor(playIcon, (Color) playIcon.getBackground().getFills().get(0).getFill(), Color.rgb(100, 100, 100), 200);
        }
    }

    public void nextVideoButtonHoverOn(){
        nextVideoButtonHover = true;

        if(controlBarController.nextVideoButtonEnabled){
            AnimationsClass.AnimateBackgroundColor(nextVideoIcon, (Color) nextVideoIcon.getBackground().getFills().get(0).getFill(), Color.rgb(255, 255, 255), 200);
        }
        else {
            AnimationsClass.AnimateBackgroundColor(nextVideoIcon, (Color) nextVideoIcon.getBackground().getFills().get(0).getFill(), Color.rgb(130, 130, 130), 200);
        }
    }

    public void nextVideoButtonHoverOff(){
        nextVideoButtonHover = false;

        if(controlBarController.nextVideoButtonEnabled){
            AnimationsClass.AnimateBackgroundColor(nextVideoIcon, (Color) nextVideoIcon.getBackground().getFills().get(0).getFill(), Color.rgb(200, 200, 200), 200);
        }
        else {
            AnimationsClass.AnimateBackgroundColor(nextVideoIcon, (Color) nextVideoIcon.getBackground().getFills().get(0).getFill(), Color.rgb(100, 100, 100), 200);
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

        if(mediaInterface.atEnd){
            playIcon.setPrefSize(48, 48);
            playIcon.setMaxSize(48, 48);
        }
        else {
            playIcon.setPrefSize(40, 40);
            playIcon.setMaxSize(40, 40);
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

        if(mediaInterface.atEnd){
            playIcon.setPrefSize(30, 30);
            playIcon.setMaxSize(30, 30);
        }
        else {
            playIcon.setPrefSize(25, 25);
            playIcon.setMaxSize(25, 25);
        }
    }

    public void showControls(){
        controlsBackground.setVisible(true);

        closeButtonPane.setVisible(true);
        previousVideoButtonPane.setVisible(true);
        playButtonPane.setVisible(true);
        nextVideoButtonPane.setVisible(true);

        sliderPane.setVisible(true);
    }

    public void hideControls() {

        if (!mainController.seekingWithKeys && progressBarTimer.getStatus() != Animation.Status.RUNNING && !slider.isValueChanging()){

            controlsBackground.setVisible(false);
            closeButtonPane.setVisible(false);
            previousVideoButtonPane.setVisible(false);
            playButtonPane.setVisible(false);
            nextVideoButtonPane.setVisible(false);
            sliderPane.setVisible(false);
        }
    }


    public void play(){
        playIcon.setShape(pauseSVG);

        if(videoImageViewInnerWrapper.getWidth() < 500){
            playIcon.setPrefSize(25, 25);
            playIcon.setMaxSize( 25, 25);
        }
        else {
            playIcon.setPrefSize(40, 40);
            playIcon.setMaxSize(40, 40);
        }

        playButtonTooltip.updateText("Pause (k)");
    }


    public void pause(){
        playIcon.setShape(playSVG);

        if(videoImageViewInnerWrapper.getWidth() < 500){
            playIcon.setPrefSize(25, 25);
            playIcon.setMaxSize( 25, 25);
        }
        else {
            playIcon.setPrefSize(40, 40);
            playIcon.setMaxSize(40, 40);
        }

        playButtonTooltip.updateText("Play (k)");
    }

    public void end(){
        playIcon.setShape(replaySVG);

        if(videoImageViewInnerWrapper.getWidth() < 500){
            playIcon.setPrefSize(30, 30);
            playIcon.setMaxSize( 30, 30);
        }
        else {
            playIcon.setPrefSize(48, 48);
            playIcon.setMaxSize(48, 48);
        }

        playButtonTooltip.updateText("Replay (k)");
    }


    public void enablePreviousVideoButton(){

        if(previousVideoButtonHover){
            previousVideoIcon.setStyle("-fx-background-color: rgb(255, 255, 255);");
        }
        else {
            previousVideoIcon.setStyle("-fx-background-color: rgb(200, 200, 200);");
        }

        Platform.runLater(() -> {
            if(controlBarController.durationSlider.getValue() > 5) previousVideoButtonTooltip = new ControlTooltip("Replay", previousVideoButton, new VBox(), 0, false);
            else previousVideoButtonTooltip = new ControlTooltip("Previous video (SHIFT + P", previousVideoButton, new VBox(), 0, false);

            if(previousVideoButtonHover) previousVideoButtonTooltip.showTooltip();
        });


    }

    public void disablePreviousVideoButton(){

        if(previousVideoButtonHover){
            previousVideoIcon.setStyle("-fx-background-color: rgb(130, 130, 130);");
        }
        else {
            previousVideoIcon.setStyle("-fx-background-color: rgb(100, 100, 100);");
        }

        previousVideoButton.setOnMouseEntered(null);
        if(previousVideoButtonHover && previousVideoButtonTooltip != null) previousVideoButtonTooltip.hide();
    }

    public void enablePlayButton(){

        if(playButtonHover){
            playIcon.setStyle("-fx-background-color: rgb(255, 255, 255);");
        }
        else {
            playIcon.setStyle("-fx-background-color: rgb(200, 200, 200);");
        }

        Platform.runLater(() -> {
            if(mediaInterface.atEnd) playButtonTooltip = new ControlTooltip("Replay (k)", playButton, new VBox(), 0 , false);
            else if(mediaInterface.playing.get()) playButtonTooltip = new ControlTooltip("Pause (k)", playButton, new VBox(), 0, false);
            else playButtonTooltip = new ControlTooltip("Play (k)", playButton, new VBox(), 0, false);

            if(playButtonHover) playButtonTooltip.showTooltip();
        });
    }

    public void disablePlayButton(){

        if(playButtonHover){
            playIcon.setStyle("-fx-background-color: rgb(130, 130, 130);");
        }
        else {
            playIcon.setStyle("-fx-background-color: rgb(100, 100, 100);");
        }

        playButton.setOnMouseEntered(null);
        if(playButtonHover && playButtonTooltip != null) playButtonTooltip.hide();
    }

    public void enableNextVideoButton(){

        if(nextVideoButtonHover){
            nextVideoIcon.setStyle("-fx-background-color: rgb(255, 255, 255);");
        }
        else {
            nextVideoIcon.setStyle("-fx-background-color: rgb(200, 200, 200);");
        }

        Platform.runLater(() -> {
            nextVideoButtonTooltip = new ControlTooltip("Next video (SHIFT + N)", nextVideoButton, new VBox(), 0, false);

            if(nextVideoButtonHover) nextVideoButtonTooltip.showTooltip();
        });
    }

    public void disableNextVideoButton(){

        if(nextVideoButtonHover){
            nextVideoIcon.setStyle("-fx-background-color: rgb(130, 130, 130);");
        }
        else {
            nextVideoIcon.setStyle("-fx-background-color: rgb(100, 100, 100);");
        }

        nextVideoButton.setOnMouseEntered(null);
        if(nextVideoButtonHover && nextVideoButtonTooltip != null) nextVideoButtonTooltip.hide();
    }

    public void pressLEFT(KeyEvent e){
        controlBarController.mouseEventTracker.move();

        if (mediaInterface.mediaActive.get()) {

            if(mainController.forwardsIndicator.wrapper.isVisible()){
                mainController.forwardsIndicator.setVisible(false);
            }
            mainController.backwardsIndicator.setText("5 seconds");
            mainController.backwardsIndicator.reset();
            mainController.backwardsIndicator.setVisible(true);
            mainController.backwardsIndicator.animate();

            mediaInterface.seekedToEnd = false;

            mainController.seekingWithKeys = true;
            sliderPane.setVisible(true);
            progressBarTimer.playFromStart();
            controlBarController.durationSlider.setValue(controlBarController.durationSlider.getValue() - 5);
            e.consume();

        }
    }

    public void pressRIGHT(KeyEvent e){
        controlBarController.mouseEventTracker.move();

        if (mediaInterface.mediaActive.get()) {

            if(mainController.backwardsIndicator.wrapper.isVisible()){
                mainController.backwardsIndicator.setVisible(false);
            }
            mainController.forwardsIndicator.setText("5 seconds");
            mainController.forwardsIndicator.reset();
            mainController.forwardsIndicator.setVisible(true);
            mainController.forwardsIndicator.animate();

            if(mediaInterface.getCurrentTime().toSeconds() + 5 >= controlBarController.durationSlider.getMax()) {
                mediaInterface.seekedToEnd = true;
            }

            mainController.seekingWithKeys = true;
            sliderPane.setVisible(true);
            progressBarTimer.playFromStart();
            controlBarController.durationSlider.setValue(controlBarController.durationSlider.getValue() + 5);

            e.consume();

        }
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
}
