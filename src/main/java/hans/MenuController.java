package hans;


import java.io.File;
import java.io.IOException;
import java.net.URL;


import java.util.ArrayList;
import java.util.ResourceBundle;


import animatefx.animation.FadeIn;
import animatefx.animation.FadeOut;
import animatefx.animation.SlideOutLeft;
import com.jfoenix.controls.JFXButton;
import eu.iamgio.animated.AnimationPair;


import javafx.animation.Animation;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import eu.iamgio.animated.AnimatedVBox;
import animatefx.animation.FadeInUp;
import javafx.util.Duration;




public class MenuController implements Initializable {

    @FXML
    JFXButton addVideoButton, clearQueueButton, appSettingsButton;

    @FXML
    Button closeButton;

    @FXML
    StackPane menu, notificationPane, addVideoButtonPane, clearQueueButtonPane, appSettingsButtonPane, closeButtonPane, dragPane;

    @FXML
    Text notificationText;

    @FXML
    ScrollPane queueScroll;

    @FXML
    Region addVideoIcon, clearQueueIcon, appSettingsIcon, closeIcon, dragIcon;

    SVGPath addVideoIconSVG, clearQueueIconSVG, appSettingsIconSVG, closeIconSVG, dragSVG;



    AnimatedVBox queueBox = new AnimatedVBox(new AnimationPair(new FadeIn(), new FadeOut()).setSpeed(3, 3));


    MainController mainController;
    ControlBarController controlBarController;
    SettingsController settingsController;
    MediaInterface mediaInterface;

    QueueItem activeItem;

    FileChooser fileChooser = new FileChooser();

    ArrayList<QueueItem> queue = new ArrayList<>();

    boolean menuOpen;
    boolean menuNotificationOpen = false;
    PauseTransition closeTimer;

    boolean menuInTransition = false;

    double prefWidth = 350;

    ControlTooltip addMediaTooltip, clearQueueTooltip, closeMenuTooltip, appSettingsTooltip;

    DragResizer dragResizer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        fileChooser.setTitle("Open video");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Videos", "*.mp4"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Audio", "*.mp3"));


       queueBox.setPadding(new Insets(10, 0, 0, 0));
       queueBox.setId("queueBox");
       queueBox.setAlignment(Pos.TOP_CENTER);
       queueScroll.setContent(queueBox);


        menu.setMinWidth(0);

        closeTimer = new PauseTransition(Duration.millis(3000));
        closeTimer.setOnFinished((e) -> AnimationsClass.closeMenuNotification(this));

        notificationPane.setOpacity(0);
        menu.setMouseTransparent(true);
        Rectangle menuClip = new Rectangle();
        menuClip.widthProperty().bind(menu.widthProperty());
        menuClip.heightProperty().bind(menu.heightProperty());
        menu.setClip(menuClip);

        addVideoIconSVG = new SVGPath();
        addVideoIconSVG.setContent(App.svgMap.get(SVG.PLUS));
        addVideoIcon.setShape(addVideoIconSVG);

        clearQueueIconSVG = new SVGPath();
        clearQueueIconSVG.setContent(App.svgMap.get(SVG.CLEAR_QUEUE));
        clearQueueIcon.setShape(clearQueueIconSVG);

        appSettingsIconSVG = new SVGPath();
        appSettingsIconSVG.setContent(App.svgMap.get(SVG.SETTINGS));
        appSettingsIcon.setShape(appSettingsIconSVG);

        closeIconSVG = new SVGPath();
        closeIconSVG.setContent(App.svgMap.get(SVG.CLOSE));
        closeIcon.setShape(closeIconSVG);

        dragSVG = new SVGPath();
        dragSVG.setContent(App.svgMap.get(SVG.OPTIONS));
        dragIcon.setShape(dragSVG);

        dragResizer = new DragResizer(this);

        Platform.runLater(() -> {
            menu.maxWidthProperty().bind(menu.sceneProperty().get().widthProperty());
            closeMenuTooltip = new ControlTooltip("Close menu (q)", closeButton, new VBox(), 1000, true);
            addMediaTooltip = new ControlTooltip("Add media", addVideoButton, new VBox(), 1000, true);
            clearQueueTooltip = new ControlTooltip("Clear queue", clearQueueButton, new VBox(), 1000, true);
            appSettingsTooltip = new ControlTooltip("App settings", appSettingsButton, new VBox(), 1000, true);
        });

}


    public void openVideoChooser() throws IOException {

        File selectedFile = fileChooser.showOpenDialog(menu.getScene().getWindow());

        if(selectedFile != null){

            MediaItem temp = null;

            if(Utilities.getFileExtension(selectedFile).equals("mp4")) temp = new Mp4Item(selectedFile);
            else if(Utilities.getFileExtension(selectedFile).equals("mp3")) temp = new Mp3Item(selectedFile);


            mediaInterface.videoList.add(temp);
            mediaInterface.unplayedVideoList.add(temp);

            new QueueItem(temp, this, mediaInterface);

        }
    }

    public void clearQueue(){
        queue.clear();
        queueBox.getChildren().clear();
    }


    public void closeMenu(){
        if(!menuInTransition) {
            if(dragResizer.dragging == true) {
                dragResizer.dragging = false;
                dragPane.setCursor(Cursor.DEFAULT);
            }
            menuInTransition = true;
            menu.setMinWidth(0);
            menuOpen = false;
            notificationPane.setOpacity(0);
            menu.setMouseTransparent(true);
            if (closeMenuTooltip.countdown.getStatus() == Animation.Status.RUNNING) closeMenuTooltip.countdown.stop();
            AnimationsClass.closeMenu(mainController, this);
        }
    }


    public void init(MainController mainController, ControlBarController controlBarController, SettingsController settingsController, MediaInterface mediaInterface){
        this.mainController = mainController;
        this.controlBarController = controlBarController;
        this.settingsController = settingsController;
        this.mediaInterface = mediaInterface;
    }
}



