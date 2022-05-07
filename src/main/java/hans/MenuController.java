package hans;


import com.jfoenix.controls.JFXButton;
import javafx.animation.Animation;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;




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


    VBox queueContent = new VBox();

    MainController mainController;
    ControlBarController controlBarController;
    SettingsController settingsController;
    MediaInterface mediaInterface;

    FileChooser fileChooser = new FileChooser();


    boolean menuOpen;
    boolean menuNotificationOpen = false;
    PauseTransition closeTimer;

    boolean menuInTransition = false;

    double prefWidth = 350;

    ControlTooltip addMediaTooltip, clearQueueTooltip, closeMenuTooltip, appSettingsTooltip;

    DragResizer dragResizer;

    StackPane historyHeader, currentHeader, queueHeader, historyWrapper; // valmis teha kunagi
    Label historyText, currentVideoText, queueText;

    ArrayList<Animation> animationsInProgress = new ArrayList<>();

    QueueBox queueBox;
    HistoryBox historyBox;
    ActiveBox activeBox;

    ObservableList<QueueItem> queue = FXCollections.observableArrayList();
    ArrayList<HistoryItem> history = new ArrayList<>();
    ActiveItem activeItem = null;


    @Override
    public void initialize(URL location, ResourceBundle resources) {


        historyWrapper = new StackPane();


        queueBox = new QueueBox(this);
        historyBox = new HistoryBox(this, historyWrapper);
        activeBox = new ActiveBox(this);

        queue.addListener((ListChangeListener<QueueItem>) change -> {
            for(QueueItem queueItem : queue){
                queueItem.updateIndex(queue.indexOf(queueItem));
            }
        });

        fileChooser.setTitle("Open video");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Videos", "*.mp4"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Audio", "*.mp3"));

       historyText = new Label();
       historyText.setText("History");
       StackPane.setAlignment(historyText, Pos.CENTER_LEFT);

       historyHeader = new StackPane();
       historyHeader.getChildren().add(historyText);

        //historyWrapper.setPadding(new Insets(10, 0, 0, 0));
        historyWrapper.getChildren().add(historyBox);
        historyBox.setId("historyBox");
        historyBox.setAlignment(Pos.TOP_CENTER);

        currentVideoText = new Label();
        currentVideoText.setText("Now Playing");
        StackPane.setAlignment(currentVideoText, Pos.CENTER_LEFT);

        currentHeader = new StackPane();
        currentHeader.getChildren().add(currentVideoText);

        //activeBox.setPadding(new Insets(10, 0, 0, 0));
        activeBox.setId("activeBox");
        activeBox.setAlignment(Pos.CENTER_LEFT);

        queueText = new Label();
        queueText.setText("Next in Queue");
        StackPane.setAlignment(queueText, Pos.CENTER_LEFT);

        queueHeader = new StackPane();
        queueHeader.getChildren().add(queueText);

        //queueBox.setPadding(new Insets(10, 0, 0, 0));
        queueBox.setId("queueBox");
        queueBox.setAlignment(Pos.TOP_CENTER);

        queueContent.setId("queueContent");
        queueContent.getChildren().addAll(historyHeader, historyWrapper, currentHeader, activeBox, queueHeader, queueBox);
        queueScroll.setContent(queueContent);

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

            QueueItem item = new QueueItem(temp, this, mediaInterface, queueBox);

            if(settingsController.shuffleOn){
                // add new media item to random position in queue
                queueBox.addRand(item);
            }

            else queueBox.add(item);
        }
    }

    public void clearQueue(){

        if(!queueBox.getChildren().isEmpty()) {
            queueBox.clear();
        }
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



