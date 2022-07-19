package hans;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;

public class MiniplayerController {

    MainController mainController;
    ControlBarController controlBarController;
    MenuController menuController;
    MediaInterface mediaInterface;
    Miniplayer miniplayer;


    MediaView mediaView = new MediaView();

    StackPane mediaViewWrapper = new StackPane();
    StackPane mediaViewInnerWrapper = new StackPane();



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

    ProgressBar progressBar = new ProgressBar();

    ControlTooltip previousVideoButtonTooltip, playButtonTooltip, nextVideoButtonTooltip;


    DoubleProperty mediaViewWidth;
    DoubleProperty mediaViewHeight;


    ChangeListener<? super Number> widthListener;

    boolean previousVideoButtonHover = false, playButtonHover = false, nextVideoButtonHover = false;
    boolean previousVideoButtonEnabled = false, playButtonEnabled = false, nextVideoButtonEnabled = false;


    MiniplayerController(MainController mainController, ControlBarController controlBarController, MenuController menuController, MediaInterface mediaInterface, Miniplayer miniplayer){

        this.mainController = mainController;
        this.controlBarController = controlBarController;
        this.menuController = menuController;
        this.mediaInterface = mediaInterface;
        this.miniplayer = miniplayer;


        mediaViewWrapper.setPrefSize(500, 300);
        mediaViewWrapper.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        mediaViewWrapper.getChildren().addAll(mediaViewInnerWrapper, controlsBackground, previousVideoButtonPane, nextVideoButtonPane, playButtonPane, closeButtonPane);

        mediaViewWrapper.setOnMouseEntered(e -> {
            showControls();
        });

        mediaViewWrapper.setOnMouseExited(e -> {
            hideControls();
        });

        mediaViewInnerWrapper.setBackground(Background.EMPTY);
        mediaViewInnerWrapper.setMouseTransparent(true);
        mediaViewInnerWrapper.getChildren().add(mediaView);

        mediaView.setPreserveRatio(true);

        mediaViewWidth = mediaView.fitWidthProperty();
        mediaViewHeight = mediaView.fitHeightProperty();
        Platform.runLater(() -> {
            mediaViewHeight.bind(mediaViewInnerWrapper.getScene().heightProperty());
            mediaViewWidth.bind(mediaViewInnerWrapper.getScene().widthProperty());
        });
        mediaView.setMouseTransparent(true);

        if(menuController.activeItem != null && mediaInterface.mediaPlayer != null){
            mediaView.setMediaPlayer(mediaInterface.mediaPlayer);
        }

        mainController.captionsController.mediaWidthMultiplier.set(0.4);
        mainController.captionsController.resizeCaptions();


        mainController.sizeMultiplier.set(0.6);
        if(mainController.actionIndicator.wrapper.isVisible()) mainController.actionIndicator.updateSize();
        mainController.forwardsIndicator.resize();
        mainController.backwardsIndicator.resize();
        mainController.valueIndicator.resize();


        widthListener = (observableValue, oldValue, newValue) -> {

            if(newValue.doubleValue() < 500 && oldValue.doubleValue() >= 500){
                reduceButtons();
            }
            else if(newValue.doubleValue() >= 500 && oldValue.doubleValue() < 500){
                enlargeButtons();
            }

            if(oldValue.doubleValue() >= 400 && newValue.doubleValue() < 400){

                mainController.captionsController.mediaWidthMultiplier.set(0.3);
                mainController.captionsController.resizeCaptions();

                mainController.sizeMultiplier.set(0.35);
                if(mainController.actionIndicator.wrapper.isVisible()) mainController.actionIndicator.updateSize();
                mainController.forwardsIndicator.resize();
                mainController.backwardsIndicator.resize();
                mainController.valueIndicator.resize();
            }
            else if((oldValue.doubleValue() < 400 || oldValue.doubleValue() >= 600) && (newValue.doubleValue() >= 400 && newValue.doubleValue() < 600)){

                mainController.captionsController.mediaWidthMultiplier.set(0.4);
                mainController.captionsController.resizeCaptions();

                mainController.sizeMultiplier.set(0.5);
                if(mainController.actionIndicator.wrapper.isVisible()) mainController.actionIndicator.updateSize();
                mainController.forwardsIndicator.resize();
                mainController.backwardsIndicator.resize();
                mainController.valueIndicator.resize();

            }
            else if((oldValue.doubleValue() < 600 || oldValue.doubleValue() >= 800) && (newValue.doubleValue() >= 600 && newValue.doubleValue() < 800)){

                mainController.captionsController.mediaWidthMultiplier.set(0.55);
                mainController.captionsController.resizeCaptions();

                mainController.sizeMultiplier.set(0.6);
                if(mainController.actionIndicator.wrapper.isVisible()) mainController.actionIndicator.updateSize();
                mainController.forwardsIndicator.resize();
                mainController.backwardsIndicator.resize();
                mainController.valueIndicator.resize();

            }
            else if(oldValue.doubleValue() < 800 && newValue.doubleValue() >= 800){

                mainController.captionsController.mediaWidthMultiplier.set(0.65);
                mainController.captionsController.resizeCaptions();

                mainController.sizeMultiplier.set(0.7);
                if(mainController.actionIndicator.wrapper.isVisible()) mainController.actionIndicator.updateSize();
                mainController.forwardsIndicator.resize();
                mainController.backwardsIndicator.resize();
                mainController.valueIndicator.resize();

            }

        };

        mediaViewInnerWrapper.widthProperty().addListener(widthListener);


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

        previousVideoButtonPane.translateXProperty().bind(mediaViewWrapper.widthProperty().multiply(-0.25));

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
                controlBarController.replayMedia();
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
            if(mediaInterface.atEnd) controlBarController.replayMedia();
            else if(mediaInterface.playing.get()) controlBarController.pause();
            else controlBarController.play();
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

        if(menuController.mediaActive.get()){
            enablePlayButton();
        }
        else {
            disablePlayButton();
        }


        nextVideoButtonPane.getChildren().addAll(nextVideoButton, nextVideoIcon);
        nextVideoButtonPane.setPrefSize(60, 60);
        nextVideoButtonPane.setMaxSize(60, 60);
        nextVideoButtonPane.translateXProperty().bind(mediaViewWrapper.widthProperty().multiply(0.25));
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
        miniplayer.scene.setMoveControl(mediaViewWrapper);
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

        if(previousVideoButtonEnabled){
            AnimationsClass.AnimateBackgroundColor(previousVideoIcon, (Color) previousVideoIcon.getBackground().getFills().get(0).getFill(), Color.rgb(255, 255, 255), 200);
        }
        else {
            AnimationsClass.AnimateBackgroundColor(previousVideoIcon, (Color) previousVideoIcon.getBackground().getFills().get(0).getFill(), Color.rgb(130, 130, 130), 200);
        }
    }

    public void previousVideoButtonHoverOff(){
        previousVideoButtonHover = false;

        if(previousVideoButtonEnabled){
            AnimationsClass.AnimateBackgroundColor(previousVideoIcon, (Color) previousVideoIcon.getBackground().getFills().get(0).getFill(), Color.rgb(200, 200, 200), 200);
        }
        else {
            AnimationsClass.AnimateBackgroundColor(previousVideoIcon, (Color) previousVideoIcon.getBackground().getFills().get(0).getFill(), Color.rgb(100, 100, 100), 200);
        }
    }

    public void playButtonHoverOn(){
        playButtonHover = true;

        if(playButtonEnabled){
            AnimationsClass.AnimateBackgroundColor(playIcon, (Color) playIcon.getBackground().getFills().get(0).getFill(), Color.rgb(255, 255, 255), 200);
        }
        else {
            AnimationsClass.AnimateBackgroundColor(playIcon, (Color) playIcon.getBackground().getFills().get(0).getFill(), Color.rgb(130, 130, 130), 200);
        }
    }

    public void playButtonHoverOff(){
        playButtonHover = false;

        if(playButtonEnabled){
            AnimationsClass.AnimateBackgroundColor(playIcon, (Color) playIcon.getBackground().getFills().get(0).getFill(), Color.rgb(200, 200, 200), 200);
        }
        else {
            AnimationsClass.AnimateBackgroundColor(playIcon, (Color) playIcon.getBackground().getFills().get(0).getFill(), Color.rgb(100, 100, 100), 200);
        }
    }

    public void nextVideoButtonHoverOn(){
        nextVideoButtonHover = true;

        if(nextVideoButtonEnabled){
            AnimationsClass.AnimateBackgroundColor(nextVideoIcon, (Color) nextVideoIcon.getBackground().getFills().get(0).getFill(), Color.rgb(255, 255, 255), 200);
        }
        else {
            AnimationsClass.AnimateBackgroundColor(nextVideoIcon, (Color) nextVideoIcon.getBackground().getFills().get(0).getFill(), Color.rgb(130, 130, 130), 200);
        }
    }

    public void nextVideoButtonHoverOff(){
        nextVideoButtonHover = false;

        if(nextVideoButtonEnabled){
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
    }

    public void hideControls(){
        controlsBackground.setVisible(false);

        closeButtonPane.setVisible(false);
        previousVideoButtonPane.setVisible(false);
        playButtonPane.setVisible(false);
        nextVideoButtonPane.setVisible(false);
    }


    public void play(){
        playIcon.setShape(pauseSVG);

        if(mediaViewInnerWrapper.getWidth() < 500){
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

        if(mediaViewInnerWrapper.getWidth() < 500){
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

        if(mediaViewInnerWrapper.getWidth() < 500){
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
        previousVideoButtonEnabled = true;

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
        previousVideoButtonEnabled = false;

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
        playButtonEnabled = true;

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
        playButtonEnabled = false;

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
        nextVideoButtonEnabled = true;

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
        nextVideoButtonEnabled = false;

        if(nextVideoButtonHover){
            nextVideoIcon.setStyle("-fx-background-color: rgb(130, 130, 130);");
        }
        else {
            nextVideoIcon.setStyle("-fx-background-color: rgb(100, 100, 100);");
        }

        nextVideoButton.setOnMouseEntered(null);
        if(nextVideoButtonHover && nextVideoButtonTooltip != null) nextVideoButtonTooltip.hide();
    }
}
