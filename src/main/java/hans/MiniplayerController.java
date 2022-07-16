package hans;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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

    HBox controls = new HBox();


    StackPane previousVideoButtonPane = new StackPane();
    StackPane playButtonPane = new StackPane();
    StackPane nextVideoButtonPane = new StackPane();
    StackPane closeButtonPane = new StackPane();
    StackPane exitButtonPane = new StackPane();


    Button previousVideoButton = new Button();
    Button playButton = new Button();
    Button nextVideoButton = new Button();
    Button closeButton = new Button();
    Button exitButton = new Button();


    Region previousVideoIcon = new Region();
    Region playIcon = new Region();
    Region nextVideoIcon = new Region();
    Region closeIcon = new Region();
    Region exitIcon = new Region();

    SVGPath previousVideoSVG = new SVGPath();
    SVGPath playSVG = new SVGPath();
    SVGPath pauseSVG = new SVGPath();
    SVGPath replaySVG = new SVGPath();
    SVGPath nextVideoSVG = new SVGPath();
    SVGPath closeSVG = new SVGPath();
    SVGPath exitSVG = new SVGPath();

    DoubleProperty mediaViewWidth;
    DoubleProperty mediaViewHeight;

    ChangeListener<? super Number> widthListener;


    MiniplayerController(MainController mainController, ControlBarController controlBarController, MenuController menuController, MediaInterface mediaInterface, Miniplayer miniplayer){

        this.mainController = mainController;
        this.controlBarController = controlBarController;
        this.menuController = menuController;
        this.mediaInterface = mediaInterface;
        this.miniplayer = miniplayer;


        mediaViewWrapper.setPrefSize(500, 300);
        mediaViewWrapper.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        mediaViewWrapper.getChildren().addAll(mediaViewInnerWrapper, controls, exitButtonPane, closeButtonPane);


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
            if(oldValue.doubleValue() >= 400 && newValue.doubleValue() < 400){
                System.out.println("1");
                mainController.captionsController.mediaWidthMultiplier.set(0.3);
                mainController.captionsController.resizeCaptions();

                mainController.sizeMultiplier.set(0.35);
                if(mainController.actionIndicator.wrapper.isVisible()) mainController.actionIndicator.updateSize();
                mainController.forwardsIndicator.resize();
                mainController.backwardsIndicator.resize();
                mainController.valueIndicator.resize();
            }
            else if((oldValue.doubleValue() < 400 || oldValue.doubleValue() >= 600) && (newValue.doubleValue() >= 400 && newValue.doubleValue() < 600)){
                System.out.println("2");
                mainController.captionsController.mediaWidthMultiplier.set(0.4);
                mainController.captionsController.resizeCaptions();

                mainController.sizeMultiplier.set(0.5);
                if(mainController.actionIndicator.wrapper.isVisible()) mainController.actionIndicator.updateSize();
                mainController.forwardsIndicator.resize();
                mainController.backwardsIndicator.resize();
                mainController.valueIndicator.resize();

            }
            else if((oldValue.doubleValue() < 600 || oldValue.doubleValue() >= 800) && (newValue.doubleValue() >= 600 && newValue.doubleValue() < 800)){
                System.out.println("3");
                mainController.captionsController.mediaWidthMultiplier.set(0.55);
                mainController.captionsController.resizeCaptions();

                mainController.sizeMultiplier.set(0.6);
                if(mainController.actionIndicator.wrapper.isVisible()) mainController.actionIndicator.updateSize();
                mainController.forwardsIndicator.resize();
                mainController.backwardsIndicator.resize();
                mainController.valueIndicator.resize();

            }
            else if(oldValue.doubleValue() < 800 && newValue.doubleValue() >= 800){

                System.out.println("4");
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
}
