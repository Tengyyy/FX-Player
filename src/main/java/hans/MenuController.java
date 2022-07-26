package hans;


import com.jfoenix.controls.JFXButton;
import io.github.palexdev.materialfx.controls.MFXCircleToggleNode;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
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
    CaptionsController captionsController;
    MediaInterface mediaInterface;

    FileChooser fileChooser = new FileChooser();


    boolean menuOpen;
    boolean menuNotificationOpen = false;
    PauseTransition closeTimer;

    boolean menuInTransition = false;

    double prefWidth = 350;

    ControlTooltip addMediaTooltip, clearQueueTooltip, closeMenuTooltip, appSettingsTooltip, historyTooltip, shuffleTooltip;

    DragResizer dragResizer;

    StackPane historyHeader, currentHeader, queueHeader, historyWrapper, historyButtonWrapper;


    Label historyText, historySizeText, currentVideoText, queueText;

    ArrayList<Animation> animationsInProgress = new ArrayList<>();

    QueueBox queueBox;
    HistoryBox historyBox;
    ActiveBox activeBox;

    ObservableList<QueueItem> queue = FXCollections.observableArrayList();
    ArrayList<HistoryItem> history = new ArrayList<>();
    ActiveItem activeItem = null;

    BooleanProperty mediaActive = new SimpleBooleanProperty(false);

    JFXButton historyButton = new JFXButton();
    JFXButton clearQueueButton = new JFXButton();

    MFXCircleToggleNode shuffleToggle = new MFXCircleToggleNode();

    Region historyIcon = new Region();
    Region shuffleIcon = new Region();

    SVGPath historyIconPath = new SVGPath();
    SVGPath shufflePath = new SVGPath();

    // the lower bound of the bottom drag detection area
    DoubleProperty lowerBottomBound = new SimpleDoubleProperty();

    private Timeline scrollTimeline = new Timeline();
    private double scrollVelocity = 0;
    private int scrollSpeed = 4;

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



        mediaActive.addListener((observableValue, oldValue, newValue) -> {
            controlBarController.durationPane.setMouseTransparent(!newValue);
            if(mainController.miniplayerActive) mainController.miniplayer.miniplayerController.sliderPane.setMouseTransparent(!newValue);
        });

        fileChooser.setTitle("Open video");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Videos", "*.mp4"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Audio", "*.mp3"));

       historyText = new Label();
       historyText.setText("History");
       historyText.getStyleClass().add("menuBoxTitle");
       StackPane.setAlignment(historyText, Pos.CENTER_LEFT);


       historySizeText = new Label();
       historySizeText.setText("(0 items)");
       historySizeText.setId("historySizeText");
       historySizeText.setTranslateX(60);
       StackPane.setAlignment(historySizeText, Pos.CENTER_LEFT);

       historyButton.setMinSize(40, 40);
       historyButton.setPrefSize(40, 40);
       historyButton.setMaxSize(40, 40);
       historyButton.setRipplerFill(Color.WHITE);
       historyButton.setId("historyButton");
       historyButton.setCursor(Cursor.HAND);
       historyButton.setButtonType(JFXButton.ButtonType.RAISED);
       historyButton.setText(null);

       StackPane.setAlignment(historyButton, Pos.CENTER);

        historyButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> historyButton.setStyle("-fx-background-color: #606060"));

        historyButton.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> historyButton.setStyle("-fx-background-color: #505050"));

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
        historyHeader.getChildren().addAll(historyText, historySizeText, historyButtonWrapper);
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

        shufflePath.setContent(App.svgMap.get(SVG.SHUFFLE));
        shuffleIcon.setPrefSize(20, 20);
        shuffleIcon.setMaxSize(20, 20);
        shuffleIcon.setId("shuffleIcon");
        shuffleIcon.setShape(shufflePath);

        shuffleToggle.setSize(20);
        shuffleToggle.setGap(0);
        shuffleToggle.setTranslateY(6);
        shuffleToggle.setGraphic(shuffleIcon);
        shuffleToggle.setCursor(Cursor.HAND);
        shuffleToggle.setId("shuffleToggle");
        shuffleToggle.setPadding(Insets.EMPTY);
        shuffleToggle.setGraphicTextGap(0);
        shuffleToggle.setTranslateX(-120);
        shuffleToggle.setFont(new Font(0));
        StackPane.setAlignment(shuffleToggle, Pos.CENTER_RIGHT);

        shuffleToggle.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            settingsController.playbackOptionsController.shuffleTab.toggle.setSelected(newValue);
        });

        clearQueueButton.setId("clearQueueButton");
        clearQueueButton.setRipplerFill(Color.WHITE);
        clearQueueButton.setCursor(Cursor.HAND);
        clearQueueButton.setText("Clear queue");
        clearQueueButton.setDisable(true);
        StackPane.setAlignment(clearQueueButton, Pos.CENTER_RIGHT);

        clearQueueButton.setOnAction((e) -> clearQueue());

        queueHeader = new StackPane();
        queueHeader.getChildren().addAll(queueText, shuffleToggle, clearQueueButton);
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

        scrollTimeline.setCycleCount(Timeline.INDEFINITE);
        scrollTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(20), (ActionEvent) -> dragScroll()));


        menu.addEventHandler(DragEvent.DRAG_OVER, e -> {
            // play scroll-up animation if Y coordinate is in range of 0 to 60
            // play scroll-down animation if Y coordinate is in range of max-60 to max



            // maybe make scrolling speed static and not depend on the amount of media items


            if(e.getY() <= 60){
                scrollVelocity = - scrollSpeed * (1/(menuContent.getHeight()-queueScroll.getViewportBounds().getHeight()));

                if(scrollTimeline.getStatus() != Animation.Status.RUNNING && queueScroll.getViewportBounds().getHeight() < menuContent.getHeight() && queueScroll.getVvalue() != 0.0) scrollTimeline.play();
            }
            else if(e.getY() >= lowerBottomBound.get()){
                scrollVelocity = scrollSpeed * (1/(menuContent.getHeight()-queueScroll.getViewportBounds().getHeight()));

                if(scrollTimeline.getStatus() != Animation.Status.RUNNING && queueScroll.getViewportBounds().getHeight() < menuContent.getHeight() && queueScroll.getVvalue() != 1.0) scrollTimeline.play();
            }
            else scrollVelocity = 0;


        });

        menu.addEventHandler(DragEvent.DRAG_EXITED, e -> scrollTimeline.stop());

        menu.addEventHandler(DragEvent.DRAG_DROPPED, e -> scrollTimeline.stop());


        menu.addEventHandler(MouseDragEvent.MOUSE_DRAG_OVER, e -> {
            // play scroll-up animation if Y coordinate is in range of 0 to 60
            // play scroll-down animation if Y coordinate is in range of max-60 to max


            // maybe make scrolling speed static and not depend on the amount of media items


            if(e.getY() <= 60){
                scrollVelocity = - scrollSpeed * (1/(menuContent.getHeight()-queueScroll.getViewportBounds().getHeight()));

                if(scrollTimeline.getStatus() != Animation.Status.RUNNING && queueScroll.getViewportBounds().getHeight() < menuContent.getHeight() && queueScroll.getVvalue() != 0.0) scrollTimeline.play();
            }
            else if(e.getY() >= lowerBottomBound.get()){
                scrollVelocity = scrollSpeed * (1/(menuContent.getHeight()-queueScroll.getViewportBounds().getHeight()));

                if(scrollTimeline.getStatus() != Animation.Status.RUNNING && queueScroll.getViewportBounds().getHeight() < menuContent.getHeight() && queueScroll.getVvalue() != 1.0) scrollTimeline.play();
            }

            else scrollVelocity = 0;

        });


        menuContent.addEventHandler(MouseDragEvent.MOUSE_DRAG_OVER, e -> {
            if(queueBox.dragActive && e.getY() <= queueBox.getBoundsInParent().getMinY()){
                queueBox.draggedNode.setTranslateY(-queueBox.draggedNode.minimumY);

                if(queueBox.draggedNode.newPosition != 0){

                    if(queue.get(queueBox.draggedNode.newPosition).getTranslateY() < 0 && !queue.get(queueBox.draggedNode.newPosition).equals(queueBox.draggedNode)){

                        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(AnimationsClass.ANIMATION_SPEED), queue.get(queueBox.draggedNode.newPosition));
                        translateTransition.setInterpolator(Interpolator.EASE_OUT);
                        translateTransition.setFromY(queue.get(queueBox.draggedNode.newPosition).getTranslateY());
                        translateTransition.setToY(0);
                        translateTransition.setOnFinished(ev -> {
                            queueBox.dragAnimationsInProgress.remove(translateTransition);
                        });
                        queueBox.dragAnimationsInProgress.add(translateTransition);
                        translateTransition.play();
                    }
                    else {

                        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(AnimationsClass.ANIMATION_SPEED), queue.get(queueBox.draggedNode.newPosition - 1));
                        translateTransition.setInterpolator(Interpolator.EASE_OUT);
                        translateTransition.setFromY(queue.get(queueBox.draggedNode.newPosition - 1).getTranslateY());
                        translateTransition.setToY(QueueItem.height);
                        translateTransition.setOnFinished(ev -> {
                            queueBox.dragAnimationsInProgress.remove(translateTransition);
                        });
                        queueBox.dragAnimationsInProgress.add(translateTransition);
                        translateTransition.play();
                    }


                    queueBox.draggedNode.newPosition = 0;
                    queueBox.draggedNode.runningTranslate = queueBox.draggedNode.getTranslateY() - (queueBox.draggedNode.newPosition - (queueBox.draggedNode.videoIndex-1)) * QueueItem.height;
                }
            }
        });

        menu.addEventHandler(MouseDragEvent.MOUSE_DRAG_EXITED, e -> {
            scrollTimeline.stop();

            if(queueBox.dragActive) {
                queueBox.draggedNode.setViewOrder(0);
                queueBox.draggedNode.setStyle("-fx-background-color: #2C2C2C;");
                queueBox.draggedNode.dragPosition = 0;
                queueBox.draggedNode.minimumY = 0;
                queueBox.draggedNode.maximumY = 0;
                queueBox.dragActive = false;


                TranslateTransition translateTransition = new TranslateTransition(Duration.millis(AnimationsClass.ANIMATION_SPEED), queueBox.draggedNode);
                translateTransition.setInterpolator(Interpolator.EASE_OUT);
                translateTransition.setFromY(queueBox.draggedNode.getTranslateY());
                translateTransition.setToY(queueBox.draggedNode.getTranslateY() - queueBox.draggedNode.runningTranslate);
                translateTransition.setOnFinished(event -> {
                    queueBox.draggedNode.setMouseTransparent(false);
                    queueBox.draggedNode.setViewOrder(1);
                    queueBox.draggedNode.setStyle("-fx-background-color: transparent;");

                    if(queue.indexOf(queueBox.draggedNode) != queueBox.draggedNode.newPosition){
                        queue.remove(queueBox.draggedNode);
                        queueBox.getChildren().remove(queueBox.draggedNode);

                        queue.add(queueBox.draggedNode.newPosition, queueBox.draggedNode);
                        queueBox.getChildren().add(queueBox.draggedNode.newPosition, queueBox.draggedNode);

                        for(QueueItem queueItem : queue){
                            queueItem.setTranslateY(0);
                        }
                    }

                    queueBox.dragAnimationsInProgress.remove(translateTransition);
                    queueBox.draggedNode = null;
                });

                translateTransition.play();
                queueBox.dragAnimationsInProgress.add(translateTransition);

            }
        });

        menu.addEventHandler(MouseDragEvent.MOUSE_DRAG_RELEASED, e -> {
            scrollTimeline.stop();

            if(queueBox.dragActive) {
                queueBox.draggedNode.setViewOrder(0);
                queueBox.draggedNode.setStyle("-fx-background-color: #2C2C2C;");
                queueBox.draggedNode.dragPosition = 0;
                queueBox.draggedNode.minimumY = 0;
                queueBox.draggedNode.maximumY = 0;
                queueBox.dragActive = false;

                TranslateTransition translateTransition = new TranslateTransition(Duration.millis(AnimationsClass.ANIMATION_SPEED), queueBox.draggedNode);
                translateTransition.setInterpolator(Interpolator.EASE_OUT);
                translateTransition.setFromY(queueBox.draggedNode.getTranslateY());
                translateTransition.setToY(queueBox.draggedNode.getTranslateY() - queueBox.draggedNode.runningTranslate);
                translateTransition.setOnFinished(event -> {
                    queueBox.draggedNode.setMouseTransparent(false);
                    queueBox.draggedNode.setViewOrder(1);
                    queueBox.draggedNode.setStyle("-fx-background-color: transparent;");

                    if(queue.indexOf(queueBox.draggedNode) != queueBox.draggedNode.newPosition){
                        queue.remove(queueBox.draggedNode);
                        queueBox.getChildren().remove(queueBox.draggedNode);

                        queue.add(queueBox.draggedNode.newPosition, queueBox.draggedNode);
                        queueBox.getChildren().add(queueBox.draggedNode.newPosition, queueBox.draggedNode);

                        for(QueueItem queueItem : queue){
                            queueItem.setTranslateY(0);
                        }
                    }

                    queueBox.dragAnimationsInProgress.remove(translateTransition);
                    queueBox.draggedNode = null;

                });

                translateTransition.play();
                queueBox.dragAnimationsInProgress.add(translateTransition);


            }
        });


        lowerBottomBound.bind(menu.heightProperty().subtract(60));

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
            historyTooltip = new ControlTooltip("Open history", historyButton, new VBox(), 1000, false);
            shuffleTooltip = new ControlTooltip("Shuffle is off", shuffleToggle, new VBox(), 1000, false);
        });
}


    public void openVideoChooser() {

        File selectedFile = fileChooser.showOpenDialog(menu.getScene().getWindow());

        if(selectedFile != null){

            notificationText.setText("Added 1 video to the queue");
            AnimationsClass.openMenuNotification(this);

            MediaItem temp = null;

            if(Utilities.getFileExtension(selectedFile).equals("mp4")) temp = new Mp4Item(selectedFile);
            else if(Utilities.getFileExtension(selectedFile).equals("mp3")) temp = new Mp3Item(selectedFile);

            assert temp != null;
            QueueItem item = new QueueItem(temp, this, mediaInterface, queueBox);

            if(settingsController.playbackOptionsController.shuffleOn){
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

            captionsController.cancelDrag();

            if(dragResizer.dragging) {
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


    private void dragScroll() {
        ScrollBar sb = getVerticalScrollbar();
        if (sb != null) {
            double newValue = sb.getValue() + scrollVelocity;
            newValue = Math.min(newValue, 1.0);
            newValue = Math.max(newValue, 0.0);


            sb.setValue(newValue);

            if(queueBox.dragActive){


                if(scrollVelocity > 0){
                    queueBox.draggedNode.setTranslateY(Math.min(queueBox.draggedNode.getTranslateY() + scrollSpeed, queueBox.draggedNode.maximumY - queueBox.draggedNode.minimumY));

                    if(queueBox.draggedNode.getTranslateY() == queueBox.draggedNode.maximumY - queueBox.draggedNode.minimumY && newValue == 1.0) scrollTimeline.stop();
                }
                else if(scrollVelocity < 0){
                    queueBox.draggedNode.setTranslateY(Math.max(queueBox.draggedNode.getTranslateY() - scrollSpeed, -queueBox.draggedNode.minimumY));

                    if(queueBox.draggedNode.getTranslateY() == -queueBox.draggedNode.minimumY && newValue == 0.0) scrollTimeline.stop();
                }

                if(scrollVelocity != 0) {
                    queueBox.draggedNode.runningTranslate = queueBox.draggedNode.getTranslateY() - (queueBox.draggedNode.newPosition - (queueBox.draggedNode.videoIndex - 1)) * QueueItem.height;

                    if (queueBox.draggedNode.runningTranslate >= QueueItem.height) {

                        if (queue.get(queueBox.draggedNode.newPosition).getTranslateY() > 0 && !queue.get(queueBox.draggedNode.newPosition).equals(queueBox.draggedNode)) {
                            TranslateTransition translateTransition = new TranslateTransition(Duration.millis(AnimationsClass.ANIMATION_SPEED), queue.get(queueBox.draggedNode.newPosition));
                            translateTransition.setInterpolator(Interpolator.EASE_OUT);
                            translateTransition.setFromY(queue.get(queueBox.draggedNode.newPosition).getTranslateY());
                            translateTransition.setToY(0);
                            translateTransition.setOnFinished(ev -> {
                                queueBox.dragAnimationsInProgress.remove(translateTransition);
                            });
                            queueBox.dragAnimationsInProgress.add(translateTransition);
                            translateTransition.play();
                        } else {
                            TranslateTransition translateTransition = new TranslateTransition(Duration.millis(AnimationsClass.ANIMATION_SPEED), queue.get(queueBox.draggedNode.newPosition + 1));
                            translateTransition.setInterpolator(Interpolator.EASE_OUT);
                            translateTransition.setFromY(queue.get(queueBox.draggedNode.newPosition + 1).getTranslateY());
                            translateTransition.setToY(-QueueItem.height);
                            translateTransition.setOnFinished(ev -> {
                                queueBox.dragAnimationsInProgress.remove(translateTransition);
                            });
                            queueBox.dragAnimationsInProgress.add(translateTransition);
                            translateTransition.play();
                        }


                        queueBox.draggedNode.newPosition += 1;
                        queueBox.draggedNode.runningTranslate = queueBox.draggedNode.getTranslateY() - (queueBox.draggedNode.newPosition - (queueBox.draggedNode.videoIndex - 1)) * QueueItem.height;
                    } else if (queueBox.draggedNode.runningTranslate <= -QueueItem.height) {

                        if (queue.get(queueBox.draggedNode.newPosition).getTranslateY() < 0 && !queue.get(queueBox.draggedNode.newPosition).equals(queueBox.draggedNode)) {

                            TranslateTransition translateTransition = new TranslateTransition(Duration.millis(AnimationsClass.ANIMATION_SPEED), queue.get(queueBox.draggedNode.newPosition));
                            translateTransition.setInterpolator(Interpolator.EASE_OUT);
                            translateTransition.setFromY(queue.get(queueBox.draggedNode.newPosition).getTranslateY());
                            translateTransition.setToY(0);
                            translateTransition.setOnFinished(ev -> {
                                queueBox.dragAnimationsInProgress.remove(translateTransition);
                            });
                            queueBox.dragAnimationsInProgress.add(translateTransition);
                            translateTransition.play();
                        } else {

                            TranslateTransition translateTransition = new TranslateTransition(Duration.millis(AnimationsClass.ANIMATION_SPEED), queue.get(queueBox.draggedNode.newPosition - 1));
                            translateTransition.setInterpolator(Interpolator.EASE_OUT);
                            translateTransition.setFromY(queue.get(queueBox.draggedNode.newPosition - 1).getTranslateY());
                            translateTransition.setToY(QueueItem.height);
                            translateTransition.setOnFinished(ev -> {
                                queueBox.dragAnimationsInProgress.remove(translateTransition);
                            });
                            queueBox.dragAnimationsInProgress.add(translateTransition);
                            translateTransition.play();
                        }


                        queueBox.draggedNode.newPosition -= 1;
                        queueBox.draggedNode.runningTranslate = queueBox.draggedNode.getTranslateY() - (queueBox.draggedNode.newPosition - (queueBox.draggedNode.videoIndex - 1)) * QueueItem.height;
                    } else {
                        if (queueBox.draggedNode.getTranslateY() == -queueBox.draggedNode.minimumY && queueBox.draggedNode.newPosition != 0) {

                            if (queue.get(queueBox.draggedNode.newPosition).getTranslateY() < 0 && !queue.get(queueBox.draggedNode.newPosition).equals(queueBox.draggedNode)) {

                                TranslateTransition translateTransition = new TranslateTransition(Duration.millis(AnimationsClass.ANIMATION_SPEED), queue.get(queueBox.draggedNode.newPosition));
                                translateTransition.setInterpolator(Interpolator.EASE_OUT);
                                translateTransition.setFromY(queue.get(queueBox.draggedNode.newPosition).getTranslateY());
                                translateTransition.setToY(0);
                                translateTransition.setOnFinished(ev -> {
                                    queueBox.dragAnimationsInProgress.remove(translateTransition);
                                });
                                queueBox.dragAnimationsInProgress.add(translateTransition);
                                translateTransition.play();
                            } else {

                                TranslateTransition translateTransition = new TranslateTransition(Duration.millis(AnimationsClass.ANIMATION_SPEED), queue.get(queueBox.draggedNode.newPosition - 1));
                                translateTransition.setInterpolator(Interpolator.EASE_OUT);
                                translateTransition.setFromY(queue.get(queueBox.draggedNode.newPosition - 1).getTranslateY());
                                translateTransition.setToY(QueueItem.height);
                                translateTransition.setOnFinished(ev -> {
                                    queueBox.dragAnimationsInProgress.remove(translateTransition);
                                });
                                queueBox.dragAnimationsInProgress.add(translateTransition);
                                translateTransition.play();
                            }


                            queueBox.draggedNode.newPosition = 0;
                            queueBox.draggedNode.runningTranslate = queueBox.draggedNode.getTranslateY() - (queueBox.draggedNode.newPosition - (queueBox.draggedNode.videoIndex - 1)) * QueueItem.height;
                        } else if (queueBox.draggedNode.getTranslateY() == queueBox.draggedNode.maximumY - queueBox.draggedNode.minimumY && queueBox.draggedNode.newPosition != queue.size() - 1) {

                            if (queue.get(queueBox.draggedNode.newPosition).getTranslateY() > 0 && !queue.get(queueBox.draggedNode.newPosition).equals(queueBox.draggedNode)) {

                                TranslateTransition translateTransition = new TranslateTransition(Duration.millis(AnimationsClass.ANIMATION_SPEED), queue.get(queueBox.draggedNode.newPosition));
                                translateTransition.setInterpolator(Interpolator.EASE_OUT);
                                translateTransition.setFromY(queue.get(queueBox.draggedNode.newPosition).getTranslateY());
                                translateTransition.setToY(0);
                                translateTransition.setOnFinished(ev -> {
                                    queueBox.dragAnimationsInProgress.remove(translateTransition);
                                });
                                queueBox.dragAnimationsInProgress.add(translateTransition);
                                translateTransition.play();
                            } else {

                                TranslateTransition translateTransition = new TranslateTransition(Duration.millis(AnimationsClass.ANIMATION_SPEED), queue.get(queueBox.draggedNode.newPosition + 1));
                                translateTransition.setInterpolator(Interpolator.EASE_OUT);
                                translateTransition.setFromY(queue.get(queueBox.draggedNode.newPosition + 1).getTranslateY());
                                translateTransition.setToY(-QueueItem.height);
                                translateTransition.setOnFinished(ev -> {
                                    queueBox.dragAnimationsInProgress.remove(translateTransition);
                                });
                                queueBox.dragAnimationsInProgress.add(translateTransition);
                                translateTransition.play();
                            }

                            queueBox.draggedNode.newPosition = queue.size() - 1;
                            queueBox.draggedNode.runningTranslate = queueBox.draggedNode.getTranslateY() - (queueBox.draggedNode.newPosition - (queueBox.draggedNode.videoIndex - 1)) * QueueItem.height;
                        }
                    }
                }
            }
            else if((scrollVelocity > 0 && newValue == 1.0) || (scrollVelocity < 0 && newValue == 0.0)) scrollTimeline.stop();
        }
    }

    private ScrollBar getVerticalScrollbar() {
        ScrollBar result = null;
        for (Node n : queueScroll.lookupAll(".scroll-bar")) {
            if (n instanceof ScrollBar) {
                ScrollBar bar = (ScrollBar) n;
                if (bar.getOrientation().equals(Orientation.VERTICAL)) {
                    result = bar;
                }
            }
        }
        return result;
    }


    public void init(MainController mainController, ControlBarController controlBarController, SettingsController settingsController, MediaInterface mediaInterface, CaptionsController captionsController){
        this.mainController = mainController;
        this.controlBarController = controlBarController;
        this.settingsController = settingsController;
        this.mediaInterface = mediaInterface;
        this.captionsController = captionsController;
    }
}



