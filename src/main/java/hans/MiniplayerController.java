package hans;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
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

    }

    public void initActions(){
        miniplayer.scene.setMoveControl(mediaViewWrapper);
        miniplayer.scene.removeDefaultCSS();
        miniplayer.scene.setSnapEnabled(false);
    }
}
