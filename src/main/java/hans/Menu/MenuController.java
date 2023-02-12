package hans.Menu;


import com.jfoenix.controls.JFXButton;
import hans.*;
import hans.Captions.CaptionsController;
import hans.Chapters.ChapterController;
import hans.MediaItems.MediaUtilities;
import hans.Menu.MetadataEdit.MetadataEditPage;
import hans.Settings.SettingsController;
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
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
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

    StackPane queueBarButtonWrapper = new StackPane();
    HBox queueBarButtonContainer = new HBox();
    Label queueBarTitle = new Label("Play queue");

    StackPane multiselectPane = new StackPane();
    HBox selectionContainer = new HBox();
    Label multiselectLabel = new Label();
    Label bulletinLabel = new Label("•");
    Label clearSelectionLabel = new Label("Clear");
    JFXButton removeButton = new JFXButton("Remove");
    Region removeIcon = new Region();
    SVGPath removeSVG = new SVGPath();

    public ObservableList<QueueItem> selectedItems = FXCollections.observableArrayList();
    public BooleanProperty selectionActive = new SimpleBooleanProperty();

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
    public AddOptionsContextMenu addOptionsContextMenu;


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
        VBox.setMargin(queueBarTitle, new Insets(0, 30, 0, 30));

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

        addOptionsButton.setOnMousePressed(e -> chevronDownAnimation.play());
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
        addButtonContainer.setMaxHeight(Region.USE_PREF_SIZE);
        addButtonContainer.setAlignment(Pos.CENTER);
        StackPane.setAlignment(addButtonContainer, Pos.CENTER_RIGHT);
        StackPane.setMargin(addButtonContainer, new Insets(0, 30, 0, 0));

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
        queueBarButtonContainer.setAlignment(Pos.CENTER_LEFT);
        StackPane.setAlignment(queueBarButtonContainer, Pos.CENTER_LEFT);
        StackPane.setMargin(queueBarButtonContainer, new Insets(0, 0, 0, 30));

        queueBarButtonWrapper.getChildren().addAll(queueBarButtonContainer, addButtonContainer, multiselectPane);
        queueBarButtonWrapper.setPrefHeight(80);
        queueBarButtonWrapper.setMinHeight(80);


        multiselectPane.setPrefWidth(Double.MAX_VALUE);
        multiselectPane.setId("multiselectPane");
        multiselectPane.setPrefHeight(50);
        multiselectPane.setMinHeight(50);
        multiselectPane.setMaxHeight(50);
        multiselectPane.getChildren().addAll(selectionContainer, removeButton);
        multiselectPane.setPadding(new Insets(0, 20, 0, 20));
        multiselectPane.setOpacity(0);
        multiselectPane.setMouseTransparent(true);
        StackPane.setMargin(multiselectPane, new Insets(20, 10, 20, 10));

        StackPane.setAlignment(selectionContainer, Pos.CENTER_LEFT);
        selectionContainer.getChildren().addAll(multiselectLabel, bulletinLabel, clearSelectionLabel);
        selectionContainer.setAlignment(Pos.CENTER_LEFT);
        selectionContainer.setSpacing(10);

        multiselectLabel.setText("0 items selected");
        multiselectLabel.getStyleClass().add("multiselectText");

        bulletinLabel.getStyleClass().addAll("multiselectText", "bulletin");

        clearSelectionLabel.getStyleClass().addAll("multiselectText", "clearSelection");
        clearSelectionLabel.setOnMouseEntered(e -> clearSelectionLabel.setUnderline(true));
        clearSelectionLabel.setOnMouseExited(e -> clearSelectionLabel.setUnderline(false));
        clearSelectionLabel.setOnMouseClicked(e -> {
            while(!selectedItems.isEmpty()){
              selectedItems.get(0).checkbox.setSelected(false);
            }
        });

        selectedItems.addListener((ListChangeListener<QueueItem>) change -> {
            if(!selectedItems.isEmpty() && !selectionActive.get()) selectionActive.set(true);
            else if(selectedItems.isEmpty() && selectionActive.get()) selectionActive.set(false);

            if(selectedItems.size() == 1) multiselectLabel.setText("1 item selected");
            else multiselectLabel.setText(selectedItems.size() + " items selected");
        });

        multiselectPane.mouseTransparentProperty().bind(selectionActive.not());
        queueBarButtonContainer.mouseTransparentProperty().bind(selectionActive);
        addButtonContainer.mouseTransparentProperty().bind(selectionActive);


        selectionActive.addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                FadeTransition fadeIn = new FadeTransition(Duration.millis(100), multiselectPane);
                fadeIn.setFromValue(multiselectPane.getOpacity());
                fadeIn.setToValue(1);
                FadeTransition fadeOut1 = new FadeTransition(Duration.millis(100), queueBarButtonContainer);
                fadeOut1.setFromValue(queueBarButtonContainer.getOpacity());
                fadeOut1.setToValue(0);
                FadeTransition fadeOut2 = new FadeTransition(Duration.millis(100), addButtonContainer);
                fadeOut2.setFromValue(addButtonContainer.getOpacity());
                fadeOut2.setToValue(0);
                ParallelTransition parallelTransition = new ParallelTransition(fadeIn, fadeOut1, fadeOut2);
                parallelTransition.playFromStart();

                for(QueueItem queueItem : queueBox.queue){
                    queueItem.checkbox.setVisible(true);
                    queueItem.indexLabel.setVisible(false);
                    queueItem.columns.setVisible(false);
                }
            }
            else {
                addButtonContainer.setOpacity(1);
                queueBarButtonContainer.setOpacity(1);
                multiselectPane.setOpacity(0);

                for(QueueItem queueItem : queueBox.queue){
                    if(!queueItem.mouseHover){
                        queueItem.checkbox.setVisible(false);
                        if(queueItem.isActive.get()) queueItem.columns.setVisible(true);
                        else queueItem.indexLabel.setVisible(true);
                    }
                }
            }
        });

        StackPane.setAlignment(removeButton, Pos.CENTER_RIGHT);

        removeSVG.setContent(App.svgMap.get(SVG.CLOSE));

        removeIcon.setShape(removeSVG);
        removeIcon.setPrefSize(14, 14);
        removeIcon.setMaxSize(14,14);
        removeIcon.getStyleClass().addAll("menuIcon", "graphic");
        removeIcon.setMouseTransparent(true);

        removeButton.getStyleClass().add("menuButton");
        removeButton.setRipplerFill(Color.TRANSPARENT);
        removeButton.setCursor(Cursor.HAND);
        removeButton.setGraphic(removeIcon);
        removeButton.setOnAction(e -> {
            while(!selectedItems.isEmpty()){
                selectedItems.get(0).remove();
            }
        });



        queueBar.setPadding(new Insets(20, 0, 0, 0));
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
                QueueItem queueItem = new QueueItem(file, this, mediaInterface, 0);
                if(settingsController.playbackOptionsController.shuffleOn) queueBox.addRand(queueItem);
                else queueBox.add(queueItem);
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
                    QueueItem queueItem = new QueueItem(file, this, mediaInterface, 0);
                    if(settingsController.playbackOptionsController.shuffleOn) queueBox.addRand(queueItem);
                    else queueBox.add(queueItem);
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

            if((scrollVelocity > 0 && newValue == 1.0) || (scrollVelocity < 0 && newValue == 0.0)) scrollTimeline.stop();
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



