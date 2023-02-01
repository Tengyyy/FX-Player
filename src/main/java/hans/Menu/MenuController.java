package hans.Menu;


import com.jfoenix.controls.JFXButton;
import hans.*;
import hans.Chapters.ChapterController;
import hans.MediaItems.MediaUtilities;
import hans.Menu.MetadataEdit.MetadataEditPage;
import hans.Settings.SettingsController;
import hans.Captions.CaptionsController;
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
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;




public class MenuController implements Initializable {


    @FXML
    public
    StackPane menu, menuInnerWrapper;

    @FXML
    StackPane dragPane;

    @FXML
    public ScrollPane queueScroll, metadataEditScroll, technicalDetailsScroll, chapterScroll;

    @FXML
    public VBox queueWrapper, queueBar;

    VBox queueContent = new VBox();

    public MainController mainController;
    public ControlBarController controlBarController;
    SettingsController settingsController;
    public CaptionsController captionsController;
    public MediaInterface mediaInterface;

    public ChapterController chapterController;

    public MetadataEditPage metadataEditPage;
    public TechnicalDetailsPage technicalDetailsPage;

    FileChooser fileChooser = new FileChooser();
    DirectoryChooser folderChooser = new DirectoryChooser();


    public MenuState menuState = MenuState.CLOSED;

    public boolean menuInTransition = false;

    final double MIN_WIDTH = 450;

    public ControlTooltip shuffleTooltip, addTooltip, addOptionsTooltip;

    DragResizer dragResizer;

    public QueueBox queueBox;

    public BooleanProperty activeMediaItemGenerated = new SimpleBooleanProperty(false);

    StackPane queueBarButtonWrapper = new StackPane();
    HBox queueBarButtonContainer = new HBox();
    Label queueBarTitle = new Label("Play queue");

    JFXButton clearQueueButton = new JFXButton();
    SVGPath clearSVG = new SVGPath();
    Region clearIcon = new Region();

    public JFXButton shuffleToggle = new JFXButton();
    SVGPath shuffleSVG = new SVGPath();
    public Region shuffleIcon = new Region();

    HBox addButtonContainer = new HBox();

    JFXButton addButton = new JFXButton();
    SVGPath folderSVG = new SVGPath();
    Region folderIcon = new Region();

    JFXButton addOptionsButton = new JFXButton();
    SVGPath chevronDownSVG = new SVGPath();
    Region chevronDownIcon = new Region();
    AddOptionsContextMenu addOptionsContextMenu;


    public MenuItemContextMenu activeMenuItemContextMenu;

    // the lower bound of the bottom drag detection area
    DoubleProperty lowerBottomBound = new SimpleDoubleProperty();

    public final Timeline scrollTimeline = new Timeline();
    private double scrollVelocity = 0;
    private final int scrollSpeed = 4;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        queueBox = new QueueBox(this);
        metadataEditPage = new MetadataEditPage(this);
        technicalDetailsPage = new TechnicalDetailsPage(this);


        fileChooser.setTitle("Add file(s) to play queue");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All supported formats", "*.mp4", "*.avi", "*.mkv", "*.flv", "*.mov", "*.mp3", "*.flac", "*.wav", "*.ogg", "*.opus", "*.aiff", "*.m4a", "*.wma", "*.aac"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Videos", "*.mp4", "*.avi", "*.mkv", "*.flv", "*.mov"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Audio", "*.mp3", "*.flac", "*.wav", "*.ogg", "*.opus", "*.aiff", "*.m4a", "*.wma", "*.aac"));

        folderChooser.setTitle("Add folder to play queue");

        queueBarTitle.setId("queueTitle");

        shuffleSVG.setContent(App.svgMap.get(SVG.SHUFFLE));

        shuffleIcon.setShape(shuffleSVG);
        shuffleIcon.setPrefSize(14, 14);
        shuffleIcon.setMaxSize(14, 14);
        shuffleIcon.setMouseTransparent(true);
        shuffleIcon.getStyleClass().addAll("menuIcon", "graphic");
        shuffleToggle.setCursor(Cursor.HAND);
        shuffleToggle.getStyleClass().add("menuButton");
        shuffleToggle.setText("Shuffle");
        shuffleToggle.setRipplerFill(Color.WHITE);
        shuffleToggle.setGraphic(shuffleIcon);
        shuffleToggle.setRipplerFill(Color.TRANSPARENT);

        shuffleToggle.setOnAction(e -> {
            if(activeMenuItemContextMenu != null && activeMenuItemContextMenu.showing) activeMenuItemContextMenu.hide();
            settingsController.playbackOptionsController.shuffleTab.toggle.setSelected(!settingsController.playbackOptionsController.shuffleTab.toggle.isSelected());
        });

        folderSVG.setContent(App.svgMap.get(SVG.FOLDER));

        folderIcon.setShape(folderSVG);
        folderIcon.setPrefSize(14, 12);
        folderIcon.setMaxSize(14,12);
        folderIcon.getStyleClass().addAll("menuIcon", "graphic");
        folderIcon.setMouseTransparent(true);

        addButton.setCursor(Cursor.HAND);
        addButton.getStyleClass().add("menuButton");
        addButton.setId("addButton");
        addButton.setText("Add file(s)");
        addButton.setRipplerFill(Color.TRANSPARENT);
        addButton.setGraphic(folderIcon);

        chevronDownSVG.setContent(App.svgMap.get(SVG.CHEVRON_DOWN));

        chevronDownIcon.setShape(chevronDownSVG);
        chevronDownIcon.setPrefSize(14, 8);
        chevronDownIcon.setMaxSize(14,8);
        chevronDownIcon.setId("chevronDownIcon");
        chevronDownIcon.setMouseTransparent(true);

        addOptionsButton.setCursor(Cursor.HAND);
        addOptionsButton.getStyleClass().add("menuButton");
        addOptionsButton.setId("addOptionsButton");
        addOptionsButton.setRipplerFill(Color.TRANSPARENT);
        addOptionsButton.setGraphic(chevronDownIcon);

        TranslateTransition chevronDownAnimation = new TranslateTransition(Duration.millis(100), chevronDownIcon);
        chevronDownAnimation.setFromY(chevronDownIcon.getTranslateY());
        chevronDownAnimation.setToY(3);

        TranslateTransition chevronUpAnimation = new TranslateTransition(Duration.millis(100), chevronDownIcon);
        chevronUpAnimation.setFromY(3);
        chevronUpAnimation.setToY(0);

        addOptionsButton.setOnMousePressed(e -> {
            chevronDownAnimation.play();
        });
        addOptionsButton.setOnMouseReleased(e -> {
            if(chevronDownAnimation.statusProperty().get() == Animation.Status.RUNNING){
               chevronDownAnimation.setOnFinished(ev -> {
                   chevronUpAnimation.playFromStart();
                   chevronDownAnimation.setOnFinished(null);
               });
            }
            else chevronUpAnimation.playFromStart();
        });

        addOptionsButton.setOnAction(e -> {
            if(addOptionsContextMenu.showing) addOptionsContextMenu.hide();
            else addOptionsContextMenu.showOptions(true);
        });

        addButtonContainer.getChildren().addAll(addButton, addOptionsButton);
        addButtonContainer.setMaxWidth(Region.USE_PREF_SIZE);
        StackPane.setAlignment(addButtonContainer, Pos.CENTER_RIGHT);

        addButton.setOnAction(e -> {
            if(activeMenuItemContextMenu != null && activeMenuItemContextMenu.showing) activeMenuItemContextMenu.hide();
            openVideoChooser();
        });

        clearSVG.setContent(App.svgMap.get(SVG.REMOVE));

        clearIcon.setShape(clearSVG);
        clearIcon.setPrefSize(14, 14);
        clearIcon.setMaxSize(14,14);
        clearIcon.getStyleClass().addAll("menuIcon", "graphic");
        clearIcon.setMouseTransparent(true);

        clearQueueButton.getStyleClass().add("menuButton");
        clearQueueButton.setRipplerFill(Color.TRANSPARENT);
        clearQueueButton.setCursor(Cursor.HAND);
        clearQueueButton.setText("Clear");
        clearQueueButton.setGraphic(clearIcon);
        clearQueueButton.setDisable(true);

        clearQueueButton.setOnAction((e) -> {
            if(activeMenuItemContextMenu != null && activeMenuItemContextMenu.showing) activeMenuItemContextMenu.hide();
            clearQueue();
        });

        queueBarButtonContainer.setSpacing(15);
        queueBarButtonContainer.getChildren().addAll(clearQueueButton, shuffleToggle);
        StackPane.setAlignment(queueBarButtonContainer, Pos.CENTER_LEFT);

        queueBarButtonWrapper.getChildren().addAll(queueBarButtonContainer, addButtonContainer);

        queueBar.setPadding(new Insets(20, 30, 20, 30));
        queueBar.setSpacing(20);
        queueBar.setAlignment(Pos.CENTER_LEFT);
        queueBar.getChildren().addAll(queueBarTitle, queueBarButtonWrapper);

        queueBox.setAlignment(Pos.TOP_CENTER);

        queueContent.getChildren().add(queueBox);
        queueScroll.setContent(queueContent);
        queueScroll.addEventFilter(KeyEvent.ANY, e -> {
            if(e.getCode() == KeyCode.UP || e.getCode() == KeyCode.DOWN){
                e.consume();
            }
        });


        menu.setBackground(Background.EMPTY);

        queueContent.setBackground(Background.EMPTY);

        queueScroll.setBackground(Background.EMPTY);

        menu.setMaxWidth(500);

        Platform.runLater(() -> menu.setTranslateX(-menu.getWidth()));

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
                scrollVelocity = - scrollSpeed * (1/(queueContent.getHeight()-queueScroll.getViewportBounds().getHeight()));

                if(scrollTimeline.getStatus() != Animation.Status.RUNNING && queueScroll.getViewportBounds().getHeight() < queueContent.getHeight() && queueScroll.getVvalue() != 0.0) scrollTimeline.play();
            }
            else if(e.getY() >= lowerBottomBound.get()){
                scrollVelocity = scrollSpeed * (1/(queueContent.getHeight()-queueScroll.getViewportBounds().getHeight()));

                if(scrollTimeline.getStatus() != Animation.Status.RUNNING && queueScroll.getViewportBounds().getHeight() < queueContent.getHeight() && queueScroll.getVvalue() != 1.0) scrollTimeline.play();
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
                scrollVelocity = - scrollSpeed * (1/(queueContent.getHeight()-queueScroll.getViewportBounds().getHeight()));

                if(scrollTimeline.getStatus() != Animation.Status.RUNNING && queueScroll.getViewportBounds().getHeight() < queueContent.getHeight() && queueScroll.getVvalue() != 0.0) scrollTimeline.play();
            }
            else if(e.getY() >= lowerBottomBound.get()){
                scrollVelocity = scrollSpeed * (1/(queueContent.getHeight()-queueScroll.getViewportBounds().getHeight()));

                if(scrollTimeline.getStatus() != Animation.Status.RUNNING && queueScroll.getViewportBounds().getHeight() < queueContent.getHeight() && queueScroll.getVvalue() != 1.0) scrollTimeline.play();
            }

            else scrollVelocity = 0;

        });


        queueContent.addEventHandler(MouseDragEvent.MOUSE_DRAG_OVER, e -> {
            if(queueBox.dragActive && e.getY() <= queueBox.getBoundsInParent().getMinY()){
                queueBox.draggedNode.setTranslateY(-queueBox.draggedNode.minimumY);

                if(queueBox.draggedNode.newPosition != 0){


                    for(int i=0; i<=queueBox.draggedNode.newPosition; i++){

                        QueueItem queueItem = queueBox.queue.get(i);
                        if(queueItem.equals(queueBox.draggedNode)) continue;

                        if(queueItem.getTranslateY() < 0){
                            TranslateTransition translateTransition = new TranslateTransition(Duration.millis(AnimationsClass.ANIMATION_SPEED), queueItem);
                            translateTransition.setInterpolator(Interpolator.EASE_OUT);
                            translateTransition.setFromY(queueItem.getTranslateY());
                            translateTransition.setToY(0);
                            translateTransition.play();
                        }
                        else {
                            TranslateTransition translateTransition = new TranslateTransition(Duration.millis(AnimationsClass.ANIMATION_SPEED), queueItem);
                            translateTransition.setInterpolator(Interpolator.EASE_OUT);
                            translateTransition.setFromY(queueItem.getTranslateY());
                            translateTransition.setToY(QueueItem.height);
                            translateTransition.play();
                        }
                    }

                    queueBox.draggedNode.newPosition = 0;
                    queueBox.draggedNode.runningTranslate = queueBox.draggedNode.getTranslateY() + queueBox.draggedNode.videoIndex * QueueItem.height;
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
                double toValue = queueBox.draggedNode.getTranslateY() - queueBox.draggedNode.runningTranslate;
                translateTransition.setFromY(queueBox.draggedNode.getTranslateY());
                translateTransition.setToY(toValue);


                translateTransition.setOnFinished(event -> {
                    if(queueBox.draggedNode == null) return;
                    queueBox.draggedNode.setMouseTransparent(false);
                    queueBox.draggedNode.setViewOrder(1);
                    queueBox.draggedNode.setStyle("-fx-background-color: transparent;");
                    queueBox.draggedNode.playIcon.setVisible(false);
                    queueBox.draggedNode.indexLabel.setVisible(true);

                    if(queueBox.queue.indexOf(queueBox.draggedNode) != queueBox.draggedNode.newPosition){
                        queueBox.queue.remove(queueBox.draggedNode);
                        queueBox.getChildren().remove(queueBox.draggedNode);

                        queueBox.queue.add(queueBox.draggedNode.newPosition, queueBox.draggedNode);
                        queueBox.getChildren().add(queueBox.draggedNode.newPosition, queueBox.draggedNode);

                        for(QueueItem queueItem : queueBox.queue){
                            queueItem.setTranslateY(0);
                        }
                        controlBarController.enableNextVideoButton();
                    }

                    queueBox.draggedNode = null;
                });

                translateTransition.play();

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
                    if(queueBox.draggedNode == null) return;
                    queueBox.draggedNode.setMouseTransparent(false);
                    queueBox.draggedNode.setViewOrder(1);
                    queueBox.draggedNode.setStyle("-fx-background-color: transparent;");

                    queueBox.draggedNode.indexLabel.setVisible(true);
                    queueBox.draggedNode.playIcon.setVisible(false);

                    if(queueBox.queue.indexOf(queueBox.draggedNode) != queueBox.draggedNode.newPosition){
                        queueBox.queue.remove(queueBox.draggedNode);
                        queueBox.getChildren().remove(queueBox.draggedNode);

                        queueBox.queue.add(queueBox.draggedNode.newPosition, queueBox.draggedNode);
                        queueBox.getChildren().add(queueBox.draggedNode.newPosition, queueBox.draggedNode);

                        for(QueueItem queueItem : queueBox.queue){
                            queueItem.setTranslateY(0);
                        }

                        controlBarController.enableNextVideoButton();

                    }

                    queueBox.draggedNode = null;

                });

                translateTransition.play();


            }
        });

        lowerBottomBound.bind(menu.heightProperty().subtract(60));

        dragResizer = new DragResizer(this);

        Platform.runLater(() -> {
            addOptionsContextMenu = new AddOptionsContextMenu(this);
            shuffleTooltip = new ControlTooltip(mainController,"Shuffle is off", shuffleToggle, 1000);
            addTooltip = new ControlTooltip(mainController,"Browse for files to add to the play queue", addButton, 1000);
            addOptionsTooltip = new ControlTooltip(mainController,"More options for adding media to the play queue", addOptionsButton, 1000);

        });

        metadataEditScroll.setVisible(false);
        metadataEditScroll.setBackground(Background.EMPTY);


        technicalDetailsScroll.setVisible(false);
        technicalDetailsScroll.setBackground(Background.EMPTY);

        chapterScroll.setVisible(false);
        chapterScroll.setBackground(Background.EMPTY);
    }

    public void openVideoChooser() {

        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(menu.getScene().getWindow());

        if(selectedFiles != null && !selectedFiles.isEmpty()){

            for(File file : selectedFiles){
                QueueItem queueItem = new QueueItem(file, this, mediaInterface);
                queueBox.add(queueItem);
            }
        }
    }

    public void openFolderChooser(){
        File folder = folderChooser.showDialog(menu.getScene().getWindow());

        if(folder != null){

            File[] files = folder.listFiles();
            if(files == null || files.length == 0) return;

            for(File file : files){
                if(MediaUtilities.mediaFormats.contains(Utilities.getFileExtension(file))){
                    QueueItem queueItem = new QueueItem(file, this, mediaInterface);
                    queueBox.add(queueItem);
                }
            }
        }
    }

    public void clearQueue(){
        queueBox.clear();
    }

    public void closeMenu(){

        if(menuInTransition) return;

        if(dragResizer.dragging) {
            dragResizer.dragging = false;
            dragPane.setCursor(Cursor.DEFAULT);
        }

        menuInTransition = true;
        menuState = MenuState.CLOSED;
        menu.setMouseTransparent(true);
        AnimationsClass.closeMenu(this, mainController);
        controlBarController.mouseEventTracker.move();

        captionsController.captionsBox.captionsContainer.setMouseTransparent(false);

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
                    queueBox.draggedNode.runningTranslate = queueBox.draggedNode.getTranslateY() - (queueBox.draggedNode.newPosition - queueBox.draggedNode.videoIndex) * QueueItem.height;

                    if (queueBox.draggedNode.runningTranslate >= QueueItem.height) {

                        if (queueBox.queue.get(queueBox.draggedNode.newPosition).getTranslateY() > 0 && !queueBox.queue.get(queueBox.draggedNode.newPosition).equals(queueBox.draggedNode)) {
                            TranslateTransition translateTransition = new TranslateTransition(Duration.millis(AnimationsClass.ANIMATION_SPEED), queueBox.queue.get(queueBox.draggedNode.newPosition));
                            translateTransition.setInterpolator(Interpolator.EASE_OUT);
                            translateTransition.setFromY(queueBox.queue.get(queueBox.draggedNode.newPosition).getTranslateY());
                            translateTransition.setToY(0);
                            translateTransition.play();
                        } else {
                            TranslateTransition translateTransition = new TranslateTransition(Duration.millis(AnimationsClass.ANIMATION_SPEED), queueBox.queue.get(queueBox.draggedNode.newPosition + 1));
                            translateTransition.setInterpolator(Interpolator.EASE_OUT);
                            translateTransition.setFromY(queueBox.queue.get(queueBox.draggedNode.newPosition + 1).getTranslateY());
                            translateTransition.setToY(-QueueItem.height);
                            translateTransition.play();
                        }


                        queueBox.draggedNode.newPosition += 1;
                        queueBox.draggedNode.runningTranslate = queueBox.draggedNode.getTranslateY() - (queueBox.draggedNode.newPosition - queueBox.draggedNode.videoIndex) * QueueItem.height;
                    } else if (queueBox.draggedNode.runningTranslate <= -QueueItem.height) {

                        if (queueBox.queue.get(queueBox.draggedNode.newPosition).getTranslateY() < 0 && !queueBox.queue.get(queueBox.draggedNode.newPosition).equals(queueBox.draggedNode)) {

                            TranslateTransition translateTransition = new TranslateTransition(Duration.millis(AnimationsClass.ANIMATION_SPEED), queueBox.queue.get(queueBox.draggedNode.newPosition));
                            translateTransition.setInterpolator(Interpolator.EASE_OUT);
                            translateTransition.setFromY(queueBox.queue.get(queueBox.draggedNode.newPosition).getTranslateY());
                            translateTransition.setToY(0);
                            translateTransition.play();
                        } else {

                            TranslateTransition translateTransition = new TranslateTransition(Duration.millis(AnimationsClass.ANIMATION_SPEED), queueBox.queue.get(queueBox.draggedNode.newPosition - 1));
                            translateTransition.setInterpolator(Interpolator.EASE_OUT);
                            translateTransition.setFromY(queueBox.queue.get(queueBox.draggedNode.newPosition - 1).getTranslateY());
                            translateTransition.setToY(QueueItem.height);
                            translateTransition.play();
                        }


                        queueBox.draggedNode.newPosition -= 1;
                        queueBox.draggedNode.runningTranslate = queueBox.draggedNode.getTranslateY() - (queueBox.draggedNode.newPosition - queueBox.draggedNode.videoIndex) * QueueItem.height;
                    } else {
                        if (queueBox.draggedNode.getTranslateY() == -queueBox.draggedNode.minimumY && queueBox.draggedNode.newPosition != 0) {

                            if (queueBox.queue.get(queueBox.draggedNode.newPosition).getTranslateY() < 0 && !queueBox.queue.get(queueBox.draggedNode.newPosition).equals(queueBox.draggedNode)) {

                                TranslateTransition translateTransition = new TranslateTransition(Duration.millis(AnimationsClass.ANIMATION_SPEED), queueBox.queue.get(queueBox.draggedNode.newPosition));
                                translateTransition.setInterpolator(Interpolator.EASE_OUT);
                                translateTransition.setFromY(queueBox.queue.get(queueBox.draggedNode.newPosition).getTranslateY());
                                translateTransition.setToY(0);
                                translateTransition.play();
                            } else {

                                TranslateTransition translateTransition = new TranslateTransition(Duration.millis(AnimationsClass.ANIMATION_SPEED), queueBox.queue.get(queueBox.draggedNode.newPosition - 1));
                                translateTransition.setInterpolator(Interpolator.EASE_OUT);
                                translateTransition.setFromY(queueBox.queue.get(queueBox.draggedNode.newPosition - 1).getTranslateY());
                                translateTransition.setToY(QueueItem.height);
                                translateTransition.play();
                            }


                            queueBox.draggedNode.newPosition = 0;
                            queueBox.draggedNode.runningTranslate = queueBox.draggedNode.getTranslateY() - (queueBox.draggedNode.newPosition - queueBox.draggedNode.videoIndex) * QueueItem.height;
                        } else if (queueBox.draggedNode.getTranslateY() == queueBox.draggedNode.maximumY - queueBox.draggedNode.minimumY && queueBox.draggedNode.newPosition != queueBox.queue.size() - 1) {

                            if (queueBox.queue.get(queueBox.draggedNode.newPosition).getTranslateY() > 0 && !queueBox.queue.get(queueBox.draggedNode.newPosition).equals(queueBox.draggedNode)) {

                                TranslateTransition translateTransition = new TranslateTransition(Duration.millis(AnimationsClass.ANIMATION_SPEED), queueBox.queue.get(queueBox.draggedNode.newPosition));
                                translateTransition.setInterpolator(Interpolator.EASE_OUT);
                                translateTransition.setFromY(queueBox.queue.get(queueBox.draggedNode.newPosition).getTranslateY());
                                translateTransition.setToY(0);
                                translateTransition.play();
                            } else {

                                TranslateTransition translateTransition = new TranslateTransition(Duration.millis(AnimationsClass.ANIMATION_SPEED), queueBox.queue.get(queueBox.draggedNode.newPosition + 1));
                                translateTransition.setInterpolator(Interpolator.EASE_OUT);
                                translateTransition.setFromY(queueBox.queue.get(queueBox.draggedNode.newPosition + 1).getTranslateY());
                                translateTransition.setToY(-QueueItem.height);
                                translateTransition.play();
                            }

                            queueBox.draggedNode.newPosition = queueBox.queue.size() - 1;
                            queueBox.draggedNode.runningTranslate = queueBox.draggedNode.getTranslateY() - (queueBox.draggedNode.newPosition - queueBox.draggedNode.videoIndex) * QueueItem.height;
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
            if (n instanceof ScrollBar bar) {
                if (bar.getOrientation().equals(Orientation.VERTICAL)) {
                    result = bar;
                }
            }
        }
        return result;
    }


    public void init(MainController mainController, ControlBarController controlBarController, SettingsController settingsController, MediaInterface mediaInterface, CaptionsController captionsController, ChapterController chapterController){
        this.mainController = mainController;
        this.controlBarController = controlBarController;
        this.settingsController = settingsController;
        this.mediaInterface = mediaInterface;
        this.captionsController = captionsController;
        this.chapterController = chapterController;
    }
}



