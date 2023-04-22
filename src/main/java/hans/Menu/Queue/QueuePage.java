package hans.Menu.Queue;

import hans.App;
import hans.Subtitles.SubtitlesState;
import hans.ControlTooltip;
import hans.MediaItems.MediaUtilities;
import hans.Menu.MenuController;
import hans.Menu.MenuState;
import hans.Menu.QueueItemContextMenu;
import hans.SVG;
import hans.PlaybackSettings.PlaybackSettingsState;
import hans.Utilities;
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
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.SVGPath;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.util.List;


public class QueuePage {

    MenuController menuController;

    VBox queueWrapper = new VBox();
    VBox queueBar = new VBox();
    public ScrollPane queueScroll = new ScrollPane();
    VBox queueContent = new VBox();

    public QueueBox queueBox;

    FileChooser fileChooser = new FileChooser();
    DirectoryChooser folderChooser = new DirectoryChooser();

    public ControlTooltip shuffleTooltip, addTooltip, addOptionsTooltip;

    StackPane queueBarButtonWrapper = new StackPane();
    HBox queueBarButtonContainer = new HBox();
    public Label queueBarTitle = new Label("Play queue");

    StackPane multiselectPane = new StackPane();
    HBox selectionContainer = new HBox();
    Label multiselectLabel = new Label();
    Label bulletinLabel = new Label("•");
    Label clearSelectionLabel = new Label("Clear");
    Button removeButton = new Button("Remove");
    Region removeIcon = new Region();
    SVGPath removeSVG = new SVGPath();

    public ObservableList<QueueItem> selectedItems = FXCollections.observableArrayList();
    public BooleanProperty selectionActive = new SimpleBooleanProperty();

    public Button clearQueueButton = new Button();
    SVGPath clearSVG = new SVGPath();
    Region clearIcon = new Region();

    public Button shuffleToggle = new Button();
    SVGPath shuffleSVG = new SVGPath();
    public Region shuffleIcon = new Region();

    public HBox addButtonContainer = new HBox();

    Button addButton = new Button();
    SVGPath folderSVG = new SVGPath();
    Region folderIcon = new Region();

    Button addOptionsButton = new Button();
    SVGPath chevronDownSVG = new SVGPath();
    Region chevronDownIcon = new Region();
    public AddOptionsContextMenu addOptionsContextMenu;

    public QueueItemContextMenu activeQueueItemContextMenu;


    // the lower bound of the bottom drag detection area
    DoubleProperty lowerBottomBound = new SimpleDoubleProperty();

    public final Timeline scrollTimeline = new Timeline();
    private double scrollVelocity = 0;
    private final int scrollSpeed = 6;

    boolean extended = false;


    StackPane scrollUpButtonContainer = new StackPane();
    Button scrollUpButton = new Button();
    Region scrollUpIcon = new Region();
    SVGPath arrowDownSVG = new SVGPath();
    SVGPath arrowUpSVG = new SVGPath();

    StackPane scrollDownButtonContainer = new StackPane();
    Button scrollDownButton = new Button();
    Region scrollDownIcon = new Region();

    public QueuePage(MenuController menuController){
        this.menuController = menuController;

        queueBox = new QueueBox(menuController, this);

        queueWrapper.setBackground(Background.EMPTY);

        queueBar.setFillWidth(true);

        queueScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        queueScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        queueScroll.getStyleClass().add("menuScroll");
        queueScroll.setFitToWidth(true);
        queueScroll.setFitToHeight(true);
        queueScroll.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        queueScroll.setBackground(Background.EMPTY);

        queueContent.setBackground(Background.EMPTY);

        VBox.setVgrow(queueScroll, Priority.ALWAYS);

        fileChooser.setTitle("Add file(s) to play queue");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All supported formats", "*.mp4", "*.avi", "*.mkv", "*.flv", "*.mov", "*.mp3", "*.flac", "*.wav", "*.ogg", "*.opus", "*.aiff", "*.m4a", "*.wma", "*.aac"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Videos", "*.mp4", "*.avi", "*.mkv", "*.flv", "*.mov"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Audio", "*.mp3", "*.flac", "*.wav", "*.ogg", "*.opus", "*.aiff", "*.m4a", "*.wma", "*.aac"));

        folderChooser.setTitle("Add folder to play queue");

        queueBarTitle.getStyleClass().add("menuTitle");
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
        shuffleToggle.setGraphic(shuffleIcon);

        shuffleToggle.setOnAction(e -> {

            if(menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) menuController.subtitlesController.closeSubtitles();
            if(menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) menuController.playbackSettingsController.closeSettings();

            if(activeQueueItemContextMenu != null && activeQueueItemContextMenu.showing) activeQueueItemContextMenu.hide();
            menuController.playbackSettingsController.playbackOptionsController.shuffleTab.toggle.setSelected(!menuController.playbackSettingsController.playbackOptionsController.shuffleTab.toggle.isSelected());
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

            if(menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) menuController.subtitlesController.closeSubtitles();
            if(menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) menuController.playbackSettingsController.closeSettings();


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
            if(menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) menuController.subtitlesController.closeSubtitles();
            if(menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) menuController.playbackSettingsController.closeSettings();

            if(activeQueueItemContextMenu != null && activeQueueItemContextMenu.showing) activeQueueItemContextMenu.hide();
            openVideoChooser();
        });

        clearSVG.setContent(App.svgMap.get(SVG.REMOVE));

        clearIcon.setShape(clearSVG);
        clearIcon.setPrefSize(14, 14);
        clearIcon.setMaxSize(14,14);
        clearIcon.getStyleClass().addAll("menuIcon", "graphic");
        clearIcon.setMouseTransparent(true);

        clearQueueButton.getStyleClass().add("menuButton");
        clearQueueButton.setCursor(Cursor.HAND);
        clearQueueButton.setText("Clear");
        clearQueueButton.setGraphic(clearIcon);
        clearQueueButton.setDisable(true);

        clearQueueButton.setOnAction((e) -> {
            if(menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) menuController.subtitlesController.closeSubtitles();
            if(menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) menuController.playbackSettingsController.closeSettings();

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
            if(menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) menuController.subtitlesController.closeSubtitles();
            if(menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) menuController.playbackSettingsController.closeSettings();

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
        removeButton.setCursor(Cursor.HAND);
        removeButton.setGraphic(removeIcon);
        removeButton.setOnAction(e -> {
            if(menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) menuController.subtitlesController.closeSubtitles();
            if(menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) menuController.playbackSettingsController.closeSettings();

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

        scrollTimeline.setCycleCount(Timeline.INDEFINITE);
        scrollTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(10), (ActionEvent) -> dragScroll()));


        queueScroll.addEventHandler(DragEvent.DRAG_OVER, e -> {
            // play scroll-up animation if Y coordinate is in range of 0 to 60
            // play scroll-down animation if Y coordinate is in range of max-60 to max

            // maybe make scrolling speed static and not depend on the amount of media items

            if(e.getY() <= 80){
                scrollVelocity = - scrollSpeed * (1/(queueContent.getHeight()-queueScroll.getViewportBounds().getHeight()));

                if(scrollTimeline.getStatus() != Animation.Status.RUNNING && queueScroll.getViewportBounds().getHeight() < queueContent.getHeight() && queueScroll.getVvalue() != 0.0) scrollTimeline.play();
            }
            else if(e.getY() >= lowerBottomBound.get()){
                scrollVelocity = scrollSpeed * (1/(queueContent.getHeight()-queueScroll.getViewportBounds().getHeight()));

                if(scrollTimeline.getStatus() != Animation.Status.RUNNING && queueScroll.getViewportBounds().getHeight() < queueContent.getHeight() && queueScroll.getVvalue() != 1.0) scrollTimeline.play();
            }
            else scrollVelocity = 0;


        });

        queueScroll.addEventHandler(DragEvent.DRAG_EXITED, e -> scrollTimeline.stop());

        queueScroll.addEventHandler(DragEvent.DRAG_DROPPED, e -> scrollTimeline.stop());

        lowerBottomBound.bind(queueScroll.heightProperty().subtract(80));

        queueWrapper.getChildren().addAll(queueBar, queueScroll);

        arrowDownSVG.setContent(App.svgMap.get(SVG.ARROW_DOWN));
        arrowUpSVG.setContent(App.svgMap.get(SVG.ARROW_UP));

        scrollUpIcon.setShape(arrowUpSVG);
        scrollUpIcon.getStyleClass().addAll("menuIcon", "graphic");
        scrollUpIcon.setPrefSize(12, 10);
        scrollUpIcon.setMaxSize(12, 10);

        StackPane.setAlignment(scrollUpButtonContainer, Pos.TOP_CENTER);
        StackPane.setMargin(scrollUpButtonContainer, new Insets(130, 0, 0, 0));
        scrollUpButtonContainer.setMaxHeight(20);
        scrollUpButtonContainer.getChildren().add(scrollUpButton);
        StackPane.setMargin(scrollUpButton, new Insets(5, 10, 0, 5));
        scrollUpButton.prefWidthProperty().bind(queueWrapper.widthProperty());
        scrollUpButton.setPrefHeight(15);
        scrollUpButton.getStyleClass().add("scrollToActiveButton");
        scrollUpButton.setGraphic(scrollUpIcon);
        scrollUpButton.setOnAction(e -> animateScroll());
        scrollUpButtonContainer.setVisible(false);

        scrollDownIcon.setShape(arrowDownSVG);
        scrollDownIcon.getStyleClass().addAll("menuIcon", "graphic");
        scrollDownIcon.setPrefSize(12, 10);
        scrollDownIcon.setMaxSize(12, 10);

        StackPane.setAlignment(scrollDownButtonContainer, Pos.BOTTOM_CENTER);
        scrollDownButtonContainer.getChildren().add(scrollDownButton);
        scrollDownButtonContainer.setMaxHeight(20);
        StackPane.setMargin(scrollDownButton, new Insets(0, 10, 5, 5));
        scrollDownButton.prefWidthProperty().bind(queueWrapper.widthProperty());
        scrollDownButton.setPrefHeight(15);
        scrollDownButton.getStyleClass().add("scrollToActiveButton");
        scrollDownButton.setGraphic(scrollDownIcon);
        scrollDownButton.setOnAction(e -> animateScroll());
        scrollDownButtonContainer.setVisible(false);

        queueScroll.vvalueProperty().addListener((observableValue, oldValue, newValue) -> checkScroll());


        queueScroll.heightProperty().addListener((observableValue, number, t1) -> checkScroll());




        menuController.queueContainer.getChildren().addAll(queueWrapper, scrollUpButtonContainer, scrollDownButtonContainer);

        Platform.runLater(() -> {
            addOptionsContextMenu = new AddOptionsContextMenu(this);
            shuffleTooltip = new ControlTooltip(menuController.mainController,"Shuffle is off", shuffleToggle, 1000);
            addTooltip = new ControlTooltip(menuController.mainController,"Browse for files to add to the play queue", addButton, 1000);
            addOptionsTooltip = new ControlTooltip(menuController.mainController,"More options for adding media to the play queue", addOptionsButton, 1000);
        });
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

    public void openVideoChooser() {

        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(queueScroll.getScene().getWindow());

        if(selectedFiles != null && !selectedFiles.isEmpty()){

            for(File file : selectedFiles){
                QueueItem queueItem = new QueueItem(file, this, menuController, menuController.mediaInterface, 0);
                if(menuController.playbackSettingsController.playbackOptionsController.shuffleOn) queueBox.addRand(queueItem);
                else queueBox.add(queueItem, false);
            }
        }
    }

    public void openFolderChooser(){
        File folder = folderChooser.showDialog(queueScroll.getScene().getWindow());

        if(folder != null){

            File[] files = folder.listFiles();
            if(files == null || files.length == 0) return;

            for(File file : files){
                if(MediaUtilities.mediaFormats.contains(Utilities.getFileExtension(file))){
                    QueueItem queueItem = new QueueItem(file, this, menuController, menuController.mediaInterface, 0);
                    if(menuController.playbackSettingsController.playbackOptionsController.shuffleOn) queueBox.addRand(queueItem);
                    else queueBox.add(queueItem, false);
                }
            }
        }
    }

    public void clearQueue(){
        if(activeQueueItemContextMenu != null && activeQueueItemContextMenu.showing) activeQueueItemContextMenu.hide();
        queueBox.clear();
    }

    public void extend(){
        VBox.setMargin(queueBarTitle, new Insets(20, 40, 5, 50));
        queueBar.setPadding(new Insets(35, 0, 0, 0));
        queueBarButtonWrapper.setPadding(new Insets(10, 20, 10, 20));
        queueBarButtonWrapper.setMinHeight(100);
        queueBarButtonWrapper.setPrefHeight(100);

        queueBox.extend();
    }

    public void shrink(){
        VBox.setMargin(queueBarTitle, new Insets(0, 30, 0, 30));
        queueBar.setPadding(new Insets(20, 0, 0, 0));
        queueBarButtonWrapper.setPadding(new Insets(0, 0, 0, 0));
        queueBarButtonWrapper.setMinHeight(80);
        queueBarButtonWrapper.setPrefHeight(80);

        queueBox.shrink();
    }

    public void openQueuePage(){
        scrollToActiveItem();

        menuController.queueContainer.setVisible(true);
    }

    public void closeQueuePage(){
        menuController.queueContainer.setVisible(false);
    }

    public void enter(){

        if(menuController.menuInTransition) return;

        menuController.menuBar.setActiveButton(menuController.menuBar.queueButton);

        if(menuController.menuState == MenuState.CLOSED) menuController.openMenu(MenuState.QUEUE_OPEN);
        else menuController.animateStateSwitch(MenuState.QUEUE_OPEN);
    }

    public void scrollToActiveItem(){
        if(queueBox.activeItem.get() != null){
            double heightViewPort = queueScroll.getViewportBounds().getHeight();
            double heightScrollPane = queueScroll.getContent().getBoundsInLocal().getHeight();
            double y = queueBox.activeItem.get().getBoundsInParent().getMaxY();
            if (y<(heightViewPort/2)){
                queueScroll.setVvalue(0);
            }
            else if ((y>=(heightViewPort/2))&(y<=(heightScrollPane-heightViewPort/2))){
                queueScroll.setVvalue((y-(heightViewPort/2))/(heightScrollPane-heightViewPort));
            }
            else if( y>= (heightScrollPane-(heightViewPort/2))){
                queueScroll.setVvalue(1);
            }
        }
    }

    public void checkScroll(){
        if(queueBox.activeItem.get() == null) return;

        double scroll = queueScroll.getVvalue();

        double heightViewPort = queueScroll.getViewportBounds().getHeight();
        double heightScrollPane = queueScroll.getContent().getBoundsInLocal().getHeight();
        double minY = queueBox.activeItem.get().getBoundsInParent().getMinY();
        double maxY = queueBox.activeItem.get().getBoundsInParent().getMaxY();

        double maxScroll = maxY/(heightScrollPane - heightViewPort);
        double minScroll = (minY-(heightViewPort))/(heightScrollPane-heightViewPort);

        if(scroll < minScroll){
            scrollDownButtonContainer.setVisible(true);
            scrollUpButtonContainer.setVisible(false);
        }
        else if(scroll > maxScroll){
            scrollUpButtonContainer.setVisible(true);
            scrollDownButtonContainer.setVisible(false);
        }
        else {
            scrollDownButtonContainer.setVisible(false);
            scrollUpButtonContainer.setVisible(false);
        }
    }

    public void animateScroll(){
        if(queueBox.activeItem.get() == null) return;

        double heightViewPort = queueScroll.getViewportBounds().getHeight();
        double heightScrollPane = queueScroll.getContent().getBoundsInLocal().getHeight();
        double y = queueBox.activeItem.get().getBoundsInParent().getMaxY();

        double target;

        if (y<(heightViewPort/2)){
            target = 0;
        }
        else if ((y>=(heightViewPort/2))&(y<=(heightScrollPane-heightViewPort/2))){
            target = (y-(heightViewPort/2))/(heightScrollPane-heightViewPort);
        }
        else {
            target = 1;
        }

        Timeline scrollTimeline = new Timeline(new KeyFrame(Duration.millis(500),
                new KeyValue(queueScroll.vvalueProperty(), target, Interpolator.EASE_BOTH)));

        scrollTimeline.playFromStart();
    }
}
