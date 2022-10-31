package hans.Menu;


import com.jfoenix.controls.JFXButton;
import hans.*;
import hans.MediaItems.*;
import hans.Menu.MetadataEdit.MetadataEditPage;
import hans.Settings.SettingsController;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
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
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;




public class MenuController implements Initializable {


    @FXML
    public
    StackPane menu, confirmationWindowContainer;

    @FXML
    public HBox notificationPane;

    @FXML
    StackPane dragPane;

    @FXML
    Label notificationText;

    @FXML
    public ScrollPane queueScroll, metadataEditScroll, technicalDetailsScroll;




    SVGPath addVideoIconSVG, closeIconSVG;


    VBox menuContent = new VBox();

    public MainController mainController;
    ControlBarController controlBarController;
    SettingsController settingsController;
    public CaptionsController captionsController;
    MediaInterface mediaInterface;

    public MetadataEditPage metadataEditPage;
    public TechnicalDetailsPage technicalDetailsPage;

    FileChooser fileChooser = new FileChooser();


    public MenuState menuState = MenuState.CLOSED;
    public boolean menuNotificationOpen = false;
    public PauseTransition closeTimer;

    public boolean menuInTransition = false;

    boolean historyButtonEnabled = false;
    boolean historyButtonHover = false;

    final double MIN_WIDTH = 450;

    ControlTooltip addMediaTooltip;
    ControlTooltip clearQueueTooltip;
    ControlTooltip historyTooltip;
    public ControlTooltip shuffleTooltip;

    DragResizer dragResizer;

    StackPane currentHeader, historyWrapper, historyButtonWrapper;

    HBox queueHeader, historyHeader;


    Label historyText, historySizeText, currentVideoText, queueText;

    public ArrayList<Animation> animationsInProgress = new ArrayList<>();

    public QueueBox queueBox;
    public HistoryBox historyBox;
    public ActiveBox activeBox;

    public ObservableList<QueueItem> queue = FXCollections.observableArrayList();
    public ArrayList<HistoryItem> history = new ArrayList<>();
    public ActiveItem activeItem = null;

    JFXButton historyButton = new JFXButton();
    JFXButton clearQueueButton = new JFXButton();


    StackPane shuffleTogglePane = new StackPane();
    JFXButton shuffleToggle = new JFXButton();
    Region shuffleIcon = new Region();
    public Circle shuffleDot = new Circle();


    StackPane addButtonPane = new StackPane();
    JFXButton addButton = new JFXButton();
    Region addIcon = new Region();

    Region historyIcon = new Region();

    SVGPath historyIconPath = new SVGPath();
    SVGPath shufflePath = new SVGPath();


    StackPane closeButtonBar = new StackPane();
    StackPane closeButtonPane = new StackPane();

    Button closeButton = new Button();
    Region closeIcon = new Region();


    VBox confirmationWindow = new VBox();
    SVGPath confirmationWindowSVG = new SVGPath();
    Region confirmationWindowIcon = new Region();
    Label confirmationWindowTitle = new Label(), confirmationWindowText = new Label();
    StackPane confirmationWindowButtonContainer = new StackPane();
    JFXButton confirmationWindowMainButton = new JFXButton(), confirmationWindowSecondaryButton = new JFXButton();

    public MenuItemOptionsPopUp activeMenuItemOptionsPopUp;

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

        metadataEditPage = new MetadataEditPage(this);
        technicalDetailsPage = new TechnicalDetailsPage(this);

        queue.addListener((ListChangeListener<QueueItem>) change -> {

            clearQueueButton.setDisable(queue.isEmpty());

            for(QueueItem queueItem : queue){
                queueItem.updateIndex(queue.indexOf(queueItem));
            }
        });


        fileChooser.setTitle("Open video");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All supported formats", "*.mp4", "*.avi", "*.mkv", "*.flv", "*.mov", "*.mp3", "*.flac", "*.wav"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Videos", "*.mp4", "*.avi", "*.mkv", "*.flv", "*.mov"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Audio", "*.mp3", "*.flac", "*.wav"));

       historyText = new Label();
       historyText.setText("History");
       historyText.getStyleClass().add("menuBoxTitle");
       historyText.setPrefWidth(65);


       historySizeText = new Label();
       historySizeText.setText("(0 items)");
       historySizeText.setId("historySizeText");
       historySizeText.setMaxWidth(Double.MAX_VALUE);
       HBox.setHgrow(historySizeText, Priority.ALWAYS);


       historyButton.setMinSize(40, 40);
       historyButton.setPrefSize(40, 40);
       historyButton.setMaxSize(40, 40);
       historyButton.setRipplerFill(null);
       historyButton.setId("historyButton");
       historyButton.setCursor(Cursor.HAND);
       historyButton.setButtonType(JFXButton.ButtonType.FLAT);

        historyButton.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
            historyButtonHover = true;
            AnimationsClass.AnimateBackgroundColor(historyIcon, (Color) historyIcon.getBackground().getFills().get(0).getFill(), Color.rgb(130, 130, 130), 200);
        });

        historyButton.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            historyButtonHover = false;
            AnimationsClass.AnimateBackgroundColor(historyIcon, (Color) historyIcon.getBackground().getFills().get(0).getFill(), Color.rgb(100, 100, 100), 200);
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


        historyHeader = new HBox();
        historyHeader.getChildren().addAll(historyText, historySizeText, historyButtonWrapper);
        historyHeader.setAlignment(Pos.CENTER_LEFT);
        historyHeader.setMinHeight(58);
        historyHeader.setPrefHeight(58);
        historyHeader.setMaxHeight(58);
        historyHeader.getStyleClass().add("menuBoxHeader");
        historyHeader.setId("historyHeader");


        historyWrapper.getChildren().add(historyBox);
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
        queueText.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(queueText, Priority.ALWAYS);

        shufflePath.setContent(App.svgMap.get(SVG.SHUFFLE));
        shuffleIcon.setPrefSize(20, 20);
        shuffleIcon.setMaxSize(20, 20);
        shuffleIcon.setId("shuffleIcon");
        shuffleIcon.setShape(shufflePath);
        shuffleIcon.setTranslateY(-2);
        shuffleIcon.setMouseTransparent(true);


        shuffleToggle.setCursor(Cursor.HAND);
        shuffleToggle.setId("shuffleToggle");
        shuffleToggle.setPrefSize(42, 42);
        shuffleToggle.setMaxSize(42, 42);
        shuffleToggle.setRipplerFill(Color.rgb(255,255,255,0.6));
        shuffleToggle.setOpacity(0);

        shuffleToggle.setOnAction(e -> settingsController.playbackOptionsController.shuffleTab.toggle.setSelected(!settingsController.playbackOptionsController.shuffleTab.toggle.isSelected()));

        shuffleToggle.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> AnimationsClass.fadeAnimation(200, shuffleToggle, 0, 0.5, false, 1, true));

        shuffleToggle.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> AnimationsClass.fadeAnimation(200, shuffleToggle, 0.5, 0, false, 1, true));

        shuffleDot.setFill(Color.RED);
        shuffleDot.setRadius(4);
        shuffleDot.setTranslateY(-1);
        shuffleDot.setMouseTransparent(true);
        shuffleDot.setOpacity(0.1);
        StackPane.setAlignment(shuffleDot, Pos.BOTTOM_CENTER);


        HBox.setMargin(shuffleTogglePane, new Insets(0, 10, 0, 0));
        shuffleTogglePane.setPrefSize(42, 42);
        shuffleTogglePane.setMaxSize(42, 42);
        shuffleTogglePane.getChildren().addAll(shuffleToggle, shuffleIcon, shuffleDot);


        addVideoIconSVG = new SVGPath();
        addVideoIconSVG.setContent(App.svgMap.get(SVG.PLUS));

        addIcon.setShape(addVideoIconSVG);
        addIcon.setPrefSize(20, 20);
        addIcon.setMaxSize(20,20);
        addIcon.getStyleClass().add("menuIcon");
        addIcon.setMouseTransparent(true);


        addButton.setCursor(Cursor.HAND);
        addButton.setId("addButton");
        addButton.setPrefSize(42, 42);
        addButton.setMaxSize(42, 42);
        addButton.setRipplerFill(Color.rgb(255,255,255,0.6));
        addButton.setOpacity(0);

        addButton.setOnAction(e -> openVideoChooser());

        addButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> AnimationsClass.fadeAnimation(200, addButton, 0, 0.5, false, 1, true));

        addButton.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> AnimationsClass.fadeAnimation(200, addButton, 0.5, 0, false, 1, true));

        HBox.setMargin(addButtonPane, new Insets(0, 10, 0, 0));
        addButtonPane.setPrefSize(42, 42);
        addButtonPane.setMaxSize(42, 42);
        addButtonPane.getChildren().addAll(addButton, addIcon);


        clearQueueButton.setId("clearQueueButton");
        clearQueueButton.setRipplerFill(Color.WHITE);
        clearQueueButton.setCursor(Cursor.HAND);
        clearQueueButton.setText("CLEAR");
        clearQueueButton.setDisable(true);

        clearQueueButton.setOnAction((e) -> clearQueue());

        queueHeader = new HBox();
        queueHeader.setAlignment(Pos.CENTER_LEFT);
        queueHeader.getChildren().addAll(queueText, shuffleTogglePane, addButtonPane, clearQueueButton);
        queueHeader.setMinHeight(60);
        queueHeader.setPrefHeight(60);
        queueHeader.setMaxHeight(60);
        queueHeader.getStyleClass().add("menuBoxHeader");

        queueBox.setAlignment(Pos.TOP_CENTER);


        menuContent.getChildren().addAll(closeButtonBar, historyHeader, historyWrapper, currentHeader, activeBox, queueHeader, queueBox);
        queueScroll.setContent(menuContent);
        queueScroll.addEventFilter(KeyEvent.ANY, e -> {
            if(e.getCode() == KeyCode.UP || e.getCode() == KeyCode.DOWN){
                e.consume();
            }
        });


        menu.setBackground(Background.EMPTY);

        menuContent.setBackground(Background.EMPTY);

        queueScroll.setBackground(Background.EMPTY);

        menu.setMaxWidth(500);

        Platform.runLater(() -> menu.setTranslateX(-menu.getWidth()));

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
                        translateTransition.setOnFinished(ev -> queueBox.dragAnimationsInProgress.remove(translateTransition));
                        queueBox.dragAnimationsInProgress.add(translateTransition);
                        translateTransition.play();
                    }
                    else {

                        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(AnimationsClass.ANIMATION_SPEED), queue.get(queueBox.draggedNode.newPosition - 1));
                        translateTransition.setInterpolator(Interpolator.EASE_OUT);
                        translateTransition.setFromY(queue.get(queueBox.draggedNode.newPosition - 1).getTranslateY());
                        translateTransition.setToY(QueueItem.height);
                        translateTransition.setOnFinished(ev -> queueBox.dragAnimationsInProgress.remove(translateTransition));
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
                queueBox.draggedNode.setStyle("-fx-background-color: rgba(70,70,70,0.6);");
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
                    queueBox.draggedNode.playIcon.setVisible(false);
                    queueBox.draggedNode.indexLabel.setVisible(true);

                    if(queue.indexOf(queueBox.draggedNode) != queueBox.draggedNode.newPosition){
                        queue.remove(queueBox.draggedNode);
                        queueBox.getChildren().remove(queueBox.draggedNode);

                        queue.add(queueBox.draggedNode.newPosition, queueBox.draggedNode);
                        queueBox.getChildren().add(queueBox.draggedNode.newPosition, queueBox.draggedNode);

                        for(QueueItem queueItem : queue){
                            queueItem.setTranslateY(0);
                        }
                        controlBarController.enableNextVideoButton();
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
                queueBox.draggedNode.setStyle("-fx-background-color: rgba(70,70,70,0.6);");
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

                    queueBox.draggedNode.indexLabel.setVisible(true);
                    queueBox.draggedNode.playIcon.setVisible(false);

                    if(queue.indexOf(queueBox.draggedNode) != queueBox.draggedNode.newPosition){
                        queue.remove(queueBox.draggedNode);
                        queueBox.getChildren().remove(queueBox.draggedNode);

                        queue.add(queueBox.draggedNode.newPosition, queueBox.draggedNode);
                        queueBox.getChildren().add(queueBox.draggedNode.newPosition, queueBox.draggedNode);

                        for(QueueItem queueItem : queue){
                            queueItem.setTranslateY(0);
                        }

                        controlBarController.enableNextVideoButton();

                    }

                    queueBox.dragAnimationsInProgress.remove(translateTransition);
                    queueBox.draggedNode = null;

                });

                translateTransition.play();
                queueBox.dragAnimationsInProgress.add(translateTransition);


            }
        });


        lowerBottomBound.bind(menu.heightProperty().subtract(60));


        closeIconSVG = new SVGPath();
        closeIconSVG.setContent(App.svgMap.get(SVG.CLOSE));
        closeIcon.setShape(closeIconSVG);
        closeIcon.setPrefSize(20, 20);
        closeIcon.setMaxSize(20, 20);
        closeIcon.setId("closeIcon");
        closeIcon.setMouseTransparent(true);

        closeButton.setPrefSize(40, 40);
        closeButton.setMaxSize(40, 40);
        closeButton.setCursor(Cursor.HAND);
        closeButton.setBackground(Background.EMPTY);

        closeButton.setOnAction(e -> closeMenu());

        closeButton.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> AnimationsClass.AnimateBackgroundColor(closeIcon, (Color) closeIcon.getBackground().getFills().get(0).getFill(), Color.rgb(255, 255, 255), 200));

        closeButton.addEventHandler(MouseEvent.MOUSE_EXITED, e -> AnimationsClass.AnimateBackgroundColor(closeIcon, (Color) closeIcon.getBackground().getFills().get(0).getFill(), Color.rgb(200, 200, 200), 200));

        closeButtonPane.setPrefSize(50, 50);
        closeButtonPane.setMaxSize(50, 50);
        closeButtonPane.getChildren().addAll(closeButton, closeIcon);
        StackPane.setAlignment(closeButtonPane, Pos.CENTER_RIGHT);

        closeButtonBar.setPrefHeight(60);
        closeButtonBar.setMinHeight(60);
        closeButtonBar.getChildren().add(closeButtonPane);


        dragResizer = new DragResizer(this);

        Platform.runLater(() -> {
            addMediaTooltip = new ControlTooltip(mainController,"Add media", addButton, 1000);
            clearQueueTooltip = new ControlTooltip(mainController,"Clear queue", clearQueueButton, 1000);
            shuffleTooltip = new ControlTooltip(mainController,"Shuffle is off", shuffleToggle, 1000);
        });




        metadataEditScroll.setVisible(false);
        metadataEditScroll.setBackground(Background.EMPTY);


        technicalDetailsScroll.setVisible(false);
        technicalDetailsScroll.setBackground(Background.EMPTY);

        confirmationWindowContainer.setId("confirmationWindowContainer");
        confirmationWindowContainer.getChildren().add(confirmationWindow);

        confirmationWindow.setAlignment(Pos.TOP_CENTER);
        confirmationWindow.setPrefSize(350, 250);
        confirmationWindow.setMaxSize(350, 250);
        confirmationWindow.setId("confirmationWindow");

        confirmationWindowSVG.setContent(App.svgMap.get(SVG.REMOVE));
        confirmationWindowIcon.setShape(confirmationWindowSVG);
        confirmationWindowIcon.getStyleClass().add("menuIcon");
        confirmationWindowIcon.setMinSize(35, 35);
        confirmationWindowIcon.setPrefSize(35, 35);
        confirmationWindowIcon.setMaxSize(35, 35);
        VBox.setMargin(confirmationWindowIcon, new Insets(20, 0, 20, 0));

        confirmationWindowTitle.setText("Discard Changes");
        confirmationWindowTitle.setId("confirmationWindowTitle");


        confirmationWindow.getChildren().addAll(confirmationWindowIcon, confirmationWindowTitle);

    }


    public void openVideoChooser() {

        File selectedFile = fileChooser.showOpenDialog(menu.getScene().getWindow());

        if(selectedFile != null){

            notificationText.setText("Added 1 video to the queue");
            AnimationsClass.openMenuNotification(this);

            MediaItem temp = null;

            switch(Utilities.getFileExtension(selectedFile)){
                case "mp4": temp = new Mp4Item(selectedFile, mainController);
                    break;
                case "mp3": temp = new Mp3Item(selectedFile, mainController);
                    break;
                case "avi": temp = new AviItem(selectedFile, mainController);
                    break;
                case "mkv": temp = new MkvItem(selectedFile, mainController);
                    break;
                case "flac": temp = new FlacItem(selectedFile, mainController);
                    break;
                case "flv": temp = new FlvItem(selectedFile, mainController);
                    break;
                case "mov": temp = new MovItem(selectedFile, mainController);
                    break;
                case "wav": temp = new WavItem(selectedFile, mainController);
                    break;
                default:
                    break;
            }

            QueueItem item;
            if(temp != null) {
                item = new QueueItem(temp, this, mediaInterface, queueBox);

                if (settingsController.playbackOptionsController.shuffleOn) {
                    // add new media item to random position in queue
                    queueBox.addRand(item);
                }
                else queueBox.add(item);
            }
        }
    }

    public void clearQueue(){

        if(animationsInProgress.isEmpty()) {
            queueBox.clear();
        }
    }

    public void closeMenu(){

        if(menuInTransition) return;

        if(dragResizer.dragging) {
            dragResizer.dragging = false;
            dragPane.setCursor(Cursor.DEFAULT);
        }

        menuInTransition = true;
        menuState = MenuState.CLOSED;
        notificationPane.setOpacity(0);
        menu.setMouseTransparent(true);
        AnimationsClass.closeMenu(this, mainController);
        controlBarController.mouseEventTracker.move();

        captionsController.captionsBox.setMouseTransparent(false);

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
                            translateTransition.setOnFinished(ev -> queueBox.dragAnimationsInProgress.remove(translateTransition));
                            queueBox.dragAnimationsInProgress.add(translateTransition);
                            translateTransition.play();
                        } else {
                            TranslateTransition translateTransition = new TranslateTransition(Duration.millis(AnimationsClass.ANIMATION_SPEED), queue.get(queueBox.draggedNode.newPosition + 1));
                            translateTransition.setInterpolator(Interpolator.EASE_OUT);
                            translateTransition.setFromY(queue.get(queueBox.draggedNode.newPosition + 1).getTranslateY());
                            translateTransition.setToY(-QueueItem.height);
                            translateTransition.setOnFinished(ev -> queueBox.dragAnimationsInProgress.remove(translateTransition));
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
                            translateTransition.setOnFinished(ev -> queueBox.dragAnimationsInProgress.remove(translateTransition));
                            queueBox.dragAnimationsInProgress.add(translateTransition);
                            translateTransition.play();
                        } else {

                            TranslateTransition translateTransition = new TranslateTransition(Duration.millis(AnimationsClass.ANIMATION_SPEED), queue.get(queueBox.draggedNode.newPosition - 1));
                            translateTransition.setInterpolator(Interpolator.EASE_OUT);
                            translateTransition.setFromY(queue.get(queueBox.draggedNode.newPosition - 1).getTranslateY());
                            translateTransition.setToY(QueueItem.height);
                            translateTransition.setOnFinished(ev -> queueBox.dragAnimationsInProgress.remove(translateTransition));
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
                                translateTransition.setOnFinished(ev -> queueBox.dragAnimationsInProgress.remove(translateTransition));
                                queueBox.dragAnimationsInProgress.add(translateTransition);
                                translateTransition.play();
                            } else {

                                TranslateTransition translateTransition = new TranslateTransition(Duration.millis(AnimationsClass.ANIMATION_SPEED), queue.get(queueBox.draggedNode.newPosition - 1));
                                translateTransition.setInterpolator(Interpolator.EASE_OUT);
                                translateTransition.setFromY(queue.get(queueBox.draggedNode.newPosition - 1).getTranslateY());
                                translateTransition.setToY(QueueItem.height);
                                translateTransition.setOnFinished(ev -> queueBox.dragAnimationsInProgress.remove(translateTransition));
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
                                translateTransition.setOnFinished(ev -> queueBox.dragAnimationsInProgress.remove(translateTransition));
                                queueBox.dragAnimationsInProgress.add(translateTransition);
                                translateTransition.play();
                            } else {

                                TranslateTransition translateTransition = new TranslateTransition(Duration.millis(AnimationsClass.ANIMATION_SPEED), queue.get(queueBox.draggedNode.newPosition + 1));
                                translateTransition.setInterpolator(Interpolator.EASE_OUT);
                                translateTransition.setFromY(queue.get(queueBox.draggedNode.newPosition + 1).getTranslateY());
                                translateTransition.setToY(-QueueItem.height);
                                translateTransition.setOnFinished(ev -> queueBox.dragAnimationsInProgress.remove(translateTransition));
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

    public void enableHistoryButton(){
        historyButtonEnabled = true;
        historyTooltip = new ControlTooltip(mainController,"Open history", historyButton, 1000);

        if(historyButtonHover) historyIcon.setStyle("-fx-background-color: rgb(255,255,255)");
        else historyIcon.setStyle("-fx-background-color: rgb(200,200,200)");

        historyButton.setRipplerFill(Color.WHITE);

        historyButton.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
            historyButtonHover = true;
            AnimationsClass.AnimateBackgroundColor(historyIcon, (Color) historyIcon.getBackground().getFills().get(0).getFill(), Color.rgb(255, 255, 255), 200);
        });

        historyButton.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            historyButtonHover = false;
            AnimationsClass.AnimateBackgroundColor(historyIcon, (Color) historyIcon.getBackground().getFills().get(0).getFill(), Color.rgb(200, 200, 200), 200);
        });
    }
}



