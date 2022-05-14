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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
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
    JFXButton addVideoButton, appSettingsButton;

    @FXML
    Button closeButton;

    @FXML
    StackPane menu, notificationPane, addVideoButtonPane, appSettingsButtonPane, closeButtonPane, dragPane;

    @FXML
    Text notificationText;

    @FXML
    ScrollPane queueScroll;

    @FXML
    Region addVideoIcon, appSettingsIcon, closeIcon, dragIcon;


    SVGPath addVideoIconSVG, appSettingsIconSVG, closeIconSVG, dragSVG;


    VBox menuContent = new VBox();

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

    StackPane historyHeader, currentHeader, queueHeader, historyWrapper, historyButtonWrapper; // valmis teha kunagi
    Label historyText, currentVideoText, queueText;

    ArrayList<Animation> animationsInProgress = new ArrayList<>();

    QueueBox queueBox;
    HistoryBox historyBox;
    ActiveBox activeBox;

    ObservableList<QueueItem> queue = FXCollections.observableArrayList();
    ArrayList<HistoryItem> history = new ArrayList<>();
    ActiveItem activeItem = null;

    JFXButton historyButton = new JFXButton();
    JFXButton clearQueueButton = new JFXButton();

    Region historyIcon = new Region();

    SVGPath historyIconPath = new SVGPath();


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        historyWrapper = new StackPane();
        Rectangle rectangle = new Rectangle();
        rectangle.heightProperty().bind(historyWrapper.heightProperty());
        rectangle.widthProperty().bind(historyWrapper.widthProperty());
        historyWrapper.setClip(rectangle);
        historyWrapper.setMinHeight(0);
        historyWrapper.setMaxHeight(0);

        queueBox = new QueueBox(this);
        historyBox = new HistoryBox(this, historyWrapper);
        activeBox = new ActiveBox(this);

        queue.addListener((ListChangeListener<QueueItem>) change -> {

            clearQueueButton.setDisable(queue.isEmpty());

            for(QueueItem queueItem : queue){
                queueItem.updateIndex(queue.indexOf(queueItem));
            }
        });

        fileChooser.setTitle("Open video");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Videos", "*.mp4"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Audio", "*.mp3"));

       historyText = new Label();
       historyText.setText("History");
       historyText.getStyleClass().add("menuBoxTitle");
       StackPane.setAlignment(historyText, Pos.CENTER_LEFT);

        historyButton.setMinSize(40, 40);
        historyButton.setPrefSize(40, 40);
        historyButton.setMaxSize(40, 40);
        historyButton.setRipplerFill(Color.WHITE);
        historyButton.setId("historyButton");
        historyButton.setCursor(Cursor.HAND);
        historyButton.setButtonType(JFXButton.ButtonType.RAISED);
        StackPane.setAlignment(historyButton, Pos.CENTER);

        historyButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> {
            historyButton.setStyle("-fx-background-color: #606060");
        });

        historyButton.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> {
            historyButton.setStyle("-fx-background-color: #505050");
        });

        historyButton.setOnAction((e) -> {
            if(historyBox.open) historyBox.close();
            else historyBox.open();
        });


        historyIcon.setMinSize(15, 10);
        historyIcon.setPrefSize(15, 10);
        historyIcon.setMaxSize(15, 10);
        historyIcon.setMouseTransparent(true);
        historyIcon.setId("historyIcon");

        StackPane.setAlignment(historyIcon, Pos.CENTER);
        historyIconPath.setContent(App.svgMap.get(SVG.CHEVRON_DOWN));
        historyIcon.setShape(historyIconPath);

        historyButtonWrapper = new StackPane();
        historyButtonWrapper.setMinSize(40, 40);
        historyButtonWrapper.setPrefSize(40, 40);
        historyButtonWrapper.setMaxSize(40, 40);
        historyButtonWrapper.getChildren().addAll(historyButton, historyIcon);
        StackPane.setAlignment(historyButtonWrapper, Pos.CENTER_RIGHT);


        historyHeader = new StackPane();
        historyHeader.getChildren().addAll(historyText, historyButtonWrapper);
       historyHeader.setMinHeight(58);
       historyHeader.setPrefHeight(58);
       historyHeader.setMaxHeight(58);
       historyHeader.getStyleClass().add("menuBoxHeader");
       historyHeader.setId("historyHeader");

        historyHeader.setBorder(new Border(new BorderStroke(Color.web("#909090"), Color.web("#909090"), Color.web("#909090"), Color.web("#909090"),
                BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY, new BorderWidths(1), new Insets(0, 1,0,1))));


        historyWrapper.getChildren().addAll(historyBox);
        historyBox.setAlignment(Pos.TOP_CENTER);

        currentVideoText = new Label();
        currentVideoText.setText("Now Playing");
        currentVideoText.getStyleClass().add("menuBoxTitle");
        StackPane.setAlignment(currentVideoText, Pos.CENTER_LEFT);

        currentHeader = new StackPane();
        currentHeader.getChildren().add(currentVideoText);
        currentHeader.setMinHeight(60);
        currentHeader.setPrefHeight(60);
        currentHeader.setMaxHeight(60);
        currentHeader.getStyleClass().add("menuBoxHeader");


        activeBox.setAlignment(Pos.CENTER_LEFT);

        queueText = new Label();
        queueText.setText("Next in Queue");
        queueText.getStyleClass().add("menuBoxTitle");
        StackPane.setAlignment(queueText, Pos.CENTER_LEFT);

        clearQueueButton.setId("clearQueueButton");
        clearQueueButton.setRipplerFill(Color.WHITE);
        clearQueueButton.setCursor(Cursor.HAND);
        clearQueueButton.setText("Clear queue");
        clearQueueButton.setDisable(true);
        StackPane.setAlignment(clearQueueButton, Pos.CENTER_RIGHT);

        clearQueueButton.setOnAction((e) -> {
            clearQueue();
        });

        queueHeader = new StackPane();
        queueHeader.getChildren().addAll(queueText, clearQueueButton);
        queueHeader.setMinHeight(60);
        queueHeader.setPrefHeight(60);
        queueHeader.setMaxHeight(60);
        queueHeader.getStyleClass().add("menuBoxHeader");

        queueBox.setAlignment(Pos.TOP_CENTER);

        menuContent.setId("menuContent");
        menuContent.getChildren().addAll(historyHeader, historyWrapper, currentHeader, activeBox, queueHeader, queueBox);
        queueScroll.setContent(menuContent);

        menu.setMinWidth(0);

        closeTimer = new PauseTransition(Duration.millis(3000));
        closeTimer.setOnFinished((e) -> AnimationsClass.closeMenuNotification(this));

        notificationPane.setOpacity(0);
        notificationPane.setMouseTransparent(true);
        menu.setMouseTransparent(true);
        Rectangle menuClip = new Rectangle();
        menuClip.widthProperty().bind(menu.widthProperty());
        menuClip.heightProperty().bind(menu.heightProperty());
        menu.setClip(menuClip);

        addVideoIconSVG = new SVGPath();
        addVideoIconSVG.setContent(App.svgMap.get(SVG.PLUS));
        addVideoIcon.setShape(addVideoIconSVG);

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
            clearQueueTooltip = new ControlTooltip("Clear queue", clearQueueButton, new VBox(), 1000, false);
            appSettingsTooltip = new ControlTooltip("App settings", appSettingsButton, new VBox(), 1000, true);
        });
}


    public void openVideoChooser() {

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

        if(animationsInProgress.isEmpty()) {
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



