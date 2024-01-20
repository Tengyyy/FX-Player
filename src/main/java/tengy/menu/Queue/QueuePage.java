package tengy.menu.Queue;

import javafx.beans.property.*;
import javafx.css.PseudoClass;
import tengy.ControlTooltip;
import tengy.mediaItems.MediaUtilities;
import tengy.menu.FocusableMenuButton;
import tengy.menu.MenuController;
import tengy.menu.MenuState;
import tengy.menu.Settings.Action;
import tengy.playbackSettings.PlaybackSettingsState;
import tengy.SVG;
import tengy.subtitles.SubtitlesState;
import tengy.Utilities;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.SVGPath;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import tengy.windows.WindowState;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static tengy.Utilities.keyboardFocusOff;
import static tengy.Utilities.keyboardFocusOn;


public class QueuePage {

    MenuController menuController;

    VBox queueWrapper = new VBox();
    VBox queueBar = new VBox();
    public ScrollPane queueScroll;
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
    FocusableMenuButton clearSelectionButton = new FocusableMenuButton();
    FocusableMenuButton removeButton = new FocusableMenuButton();
    Region removeIcon = new Region();
    SVGPath removeSVG = new SVGPath();

    public ObservableList<QueueItem> selectedItems = FXCollections.observableArrayList();
    public BooleanProperty selectionActive = new SimpleBooleanProperty();

    public FocusableMenuButton clearQueueButton = new FocusableMenuButton();
    SVGPath clearSVG = new SVGPath();
    Region clearIcon = new Region();

    public FocusableMenuButton shuffleToggle = new FocusableMenuButton();
    SVGPath shuffleSVG = new SVGPath();
    public Region shuffleIcon = new Region();

    public HBox addButtonContainer = new HBox();

    FocusableMenuButton addButton = new FocusableMenuButton();
    SVGPath folderSVG = new SVGPath();
    Region folderIcon = new Region();

    FocusableMenuButton addOptionsButton = new FocusableMenuButton();
    SVGPath chevronDownSVG = new SVGPath();
    Region chevronDownIcon = new Region();
    public AddOptionsContextMenu addOptionsContextMenu;

    public QueueItemContextMenu activeQueueItemContextMenu;


    // the lower bound of the bottom drag detection area
    DoubleProperty lowerBottomBound = new SimpleDoubleProperty();

    public final Timeline scrollTimeline = new Timeline();
    private double scrollVelocity = 0;
    private final int scrollSpeed = 6;


    public StackPane scrollUpButtonContainer = new StackPane();
    FocusableMenuButton scrollUpButton = new FocusableMenuButton();
    Region scrollUpIcon = new Region();
    SVGPath arrowDownSVG = new SVGPath();
    SVGPath arrowUpSVG = new SVGPath();

    StackPane scrollDownButtonContainer = new StackPane();
    FocusableMenuButton scrollDownButton = new FocusableMenuButton();
    Region scrollDownIcon = new Region();

    IntegerProperty focus = new SimpleIntegerProperty(-1);
    List<Node> focusNodes = new ArrayList<>();

    boolean addOptionsPressed = false;

    public QueuePage(MenuController menuController){
        this.menuController = menuController;

        queueScroll = new ScrollPane() {
            ScrollBar vertical;

            @Override
            protected void layoutChildren() {
                super.layoutChildren();
                if (vertical == null) {
                    vertical = (ScrollBar) lookup(".scroll-bar:vertical");
                    vertical.visibleProperty().addListener((obs, old, val) -> queueBox.updatePadding(val));
                }
            }
        };

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
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All supported formats", "*.mp4", "*.avi", "*.mkv", "*.flv", "*.mov", "*.mp3", "*.flac", "*.wav", "*.ogg", "*.opus", "*.aiff", "*.m4a", "*.wma", "*.aac", "*.wmv", "*.mka", "*.webm"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Videos", "*.mp4", "*.avi", "*.mkv", "*.flv", "*.mov", "*.wmv", "*.webm"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Audio", "*.mp3", "*.flac", "*.wav", "*.ogg", "*.opus", "*.aiff", "*.m4a", "*.wma", "*.aac", "*.mka"));

        folderChooser.setTitle("Add folder to play queue");

        queueBarTitle.getStyleClass().add("menuTitle");
        VBox.setMargin(queueBarTitle, new Insets(0, 30, 0, 30));

        shuffleSVG.setContent(SVG.SHUFFLE.getContent());

        shuffleIcon.setShape(shuffleSVG);
        shuffleIcon.setPrefSize(14, 14);
        shuffleIcon.setMaxSize(14, 14);
        shuffleIcon.setMouseTransparent(true);
        shuffleIcon.getStyleClass().addAll("menuIcon", "graphic");
        shuffleToggle.getStyleClass().add("menuButton");
        shuffleToggle.setText("Shuffle");
        shuffleToggle.setGraphic(shuffleIcon);

        shuffleToggle.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(focusNodes.indexOf(shuffleToggle));
            }
            else{
                keyboardFocusOff(shuffleToggle);
                focus.set(-1);
            }
        });

        shuffleToggle.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            shuffleToggle.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        shuffleToggle.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            shuffleToggle.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });

        shuffleToggle.setOnAction(e -> {

            if(menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) menuController.subtitlesController.closeSubtitles();
            if(menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) menuController.playbackSettingsController.closeSettings();

            if(activeQueueItemContextMenu != null && activeQueueItemContextMenu.showing) activeQueueItemContextMenu.hide();
            menuController.playbackSettingsController.playbackOptionsController.shuffleTab.toggle.setSelected(!menuController.playbackSettingsController.playbackOptionsController.shuffleTab.toggle.isSelected());

            shuffleToggle.requestFocus();
        });

        folderSVG.setContent(SVG.FOLDER.getContent());

        folderIcon.setShape(folderSVG);
        folderIcon.setPrefSize(14, 12);
        folderIcon.setMaxSize(14,12);
        folderIcon.getStyleClass().addAll("menuIcon", "graphic");
        folderIcon.setMouseTransparent(true);

        addButton.getStyleClass().add("menuButton");
        addButton.setId("addButton");
        addButton.setText("Add file(s)");
        addButton.setGraphic(folderIcon);
        addButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(focusNodes.indexOf(addButton));
            }
            else{
                keyboardFocusOff(addButton);
                focus.set(-1);
            }
        });

        addButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            addButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        addButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            addButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });

        chevronDownSVG.setContent(SVG.CHEVRON_DOWN.getContent());

        chevronDownIcon.setShape(chevronDownSVG);
        chevronDownIcon.setPrefSize(14, 8);
        chevronDownIcon.setMaxSize(14,8);
        chevronDownIcon.setId("chevronDownIcon");
        chevronDownIcon.setMouseTransparent(true);


        TranslateTransition chevronDownAnimation = new TranslateTransition(Duration.millis(100), chevronDownIcon);
        chevronDownAnimation.setFromY(chevronDownIcon.getTranslateY());
        chevronDownAnimation.setToY(3);

        TranslateTransition chevronUpAnimation = new TranslateTransition(Duration.millis(100), chevronDownIcon);
        chevronUpAnimation.setFromY(3);
        chevronUpAnimation.setToY(0);

        addOptionsButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            if(addOptionsPressed) return;
            addOptionsPressed = true;
            addOptionsButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);

            if(!addOptionsContextMenu.showing) chevronDownAnimation.play();
        });

        addOptionsButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;

            if(!addOptionsPressed) return;

            addOptionsPressed = false;

            addOptionsButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);

            if(chevronDownAnimation.statusProperty().get() == Animation.Status.RUNNING){
                chevronDownAnimation.setOnFinished(ev -> {
                    chevronUpAnimation.playFromStart();
                    chevronDownAnimation.setOnFinished(null);
                });
            }
            else if(chevronDownIcon.getTranslateY() != 0) chevronUpAnimation.playFromStart();
        });

        addOptionsButton.setOnMousePressed(e -> chevronDownAnimation.play());
        addOptionsButton.setOnMouseReleased(e -> {
            if(chevronDownAnimation.statusProperty().get() == Animation.Status.RUNNING){
                chevronDownAnimation.setOnFinished(ev -> {
                    chevronUpAnimation.playFromStart();
                    chevronDownAnimation.setOnFinished(null);
                });
            }
            else if(chevronDownIcon.getTranslateY() != 0) chevronUpAnimation.playFromStart();
        });

        addOptionsButton.getStyleClass().add("menuButton");
        addOptionsButton.setId("addOptionsButton");
        addOptionsButton.setGraphic(chevronDownIcon);
        addOptionsButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(focusNodes.indexOf(addOptionsButton));
            }
            else{
                keyboardFocusOff(addOptionsButton);
                focus.set(-1);

                addOptionsPressed = false;

                if(chevronDownAnimation.statusProperty().get() == Animation.Status.RUNNING){
                    chevronDownAnimation.setOnFinished(ev -> {
                        chevronUpAnimation.playFromStart();
                        chevronDownAnimation.setOnFinished(null);
                    });
                }
                else if(chevronDownIcon.getTranslateY() != 0) chevronUpAnimation.playFromStart();
            }
        });

        addOptionsButton.setOnAction(e -> {

            if(menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) menuController.subtitlesController.closeSubtitles();
            if(menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) menuController.playbackSettingsController.closeSettings();

            addOptionsButton.requestFocus();

            if(addOptionsContextMenu.showing) addOptionsContextMenu.hide();
            else if(menuController.mainController.windowController.windowState == WindowState.CLOSED) addOptionsContextMenu.showOptions(true);
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

            addButton.requestFocus();

            if(activeQueueItemContextMenu != null && activeQueueItemContextMenu.showing) activeQueueItemContextMenu.hide();
            openVideoChooser();
        });

        clearSVG.setContent(SVG.REMOVE.getContent());

        clearIcon.setShape(clearSVG);
        clearIcon.setPrefSize(14, 14);
        clearIcon.setMaxSize(14,14);
        clearIcon.getStyleClass().addAll("menuIcon", "graphic");

        clearQueueButton.getStyleClass().add("menuButton");
        clearQueueButton.setText("Clear");
        clearQueueButton.setGraphic(clearIcon);
        clearQueueButton.setDisable(true);

        clearQueueButton.disabledProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                boolean removed = focusNodes.remove(clearQueueButton);
                if(removed && focus.get() > 0) focus.set(focus.get() - 1);

                focusNodes.remove(queueBox);
            }
            else {
                if(!selectionActive.get()){
                    if(!focusNodes.contains(clearQueueButton)) focusNodes.add(0, clearQueueButton);
                    if(focus.get() >= 0) focus.set(Math.min(focus.get() + 1, focusNodes.size() - 1));
                }

                if(!focusNodes.contains(queueBox)) focusNodes.add(queueBox);
            }
        });

        clearQueueButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(focusNodes.indexOf(clearQueueButton));
            }
            else{
                keyboardFocusOff(clearQueueButton);
                focus.set(-1);
            }
        });

        clearQueueButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            clearQueueButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        clearQueueButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            clearQueueButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });

        clearQueueButton.setOnAction((e) -> {

            if(menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) menuController.subtitlesController.closeSubtitles();
            if(menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) menuController.playbackSettingsController.closeSettings();
            
            clearQueueButton.requestFocus();

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
        selectionContainer.getChildren().addAll(multiselectLabel, bulletinLabel, clearSelectionButton);
        selectionContainer.setAlignment(Pos.CENTER_LEFT);

        multiselectLabel.setText("0 items selected");
        multiselectLabel.getStyleClass().add("multiselectText");

        bulletinLabel.getStyleClass().addAll("multiselectText", "bulletin");

        clearSelectionButton.setText("Clear");
        clearSelectionButton.setAlignment(Pos.CENTER_LEFT);
        clearSelectionButton.getStyleClass().addAll("linkButton");
        clearSelectionButton.setId("clearSelectionButton");
        clearSelectionButton.setPadding(new Insets(3, 3, 3, 3));


        clearSelectionButton.setOnAction(e -> {
            if(menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) menuController.subtitlesController.closeSubtitles();
            if(menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) menuController.playbackSettingsController.closeSettings();

            clearSelectionButton.requestFocus();

            while(!selectedItems.isEmpty()){
                selectedItems.get(0).checkbox.setSelected(false);
            }
        });

        clearSelectionButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            clearSelectionButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        clearSelectionButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            clearSelectionButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });

        clearSelectionButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(focusNodes.indexOf(clearSelectionButton));
            }
            else{
                keyboardFocusOff(clearSelectionButton);
                focus.set(-1);
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

                Node focusedNode = null;

                if(focus.get() > -1 && focus.get() < focusNodes.size()){
                    focusedNode = focusNodes.get(focus.get());
                }

                focusNodes.remove(clearQueueButton);
                focusNodes.remove(shuffleToggle);
                focusNodes.remove(addButton);
                focusNodes.remove(addOptionsButton);

                if(!focusNodes.contains(clearSelectionButton)) focusNodes.add(0, clearSelectionButton);
                if(!focusNodes.contains(removeButton)) focusNodes.add(1, removeButton);

                if(focusedNode instanceof QueueBox) focus.set(focusNodes.indexOf(queueBox));
                else focus.set(-1);
            }
            else {
                addButtonContainer.setOpacity(1);
                queueBarButtonContainer.setOpacity(1);
                multiselectPane.setOpacity(0);

                for(QueueItem queueItem : queueBox.queue){
                    if(!queueItem.mouseHover && queueItem.focus.get() == -1){
                        queueItem.checkbox.setVisible(false);
                        if(queueItem.isActive.get()) queueItem.columns.setVisible(true);
                        else queueItem.indexLabel.setVisible(true);
                    }
                }

                Node focusedNode = null;

                if(focus.get() > -1 && focus.get() < focusNodes.size()){
                    focusedNode = focusNodes.get(focus.get());
                }

                focusNodes.remove(clearSelectionButton);
                focusNodes.remove(removeButton);

                if(!focusNodes.contains(addOptionsButton)) focusNodes.add(0, addOptionsButton);
                if(!focusNodes.contains(addButton)) focusNodes.add(0, addButton);
                if(!focusNodes.contains(shuffleToggle)) focusNodes.add(0, shuffleToggle);
                if(!clearQueueButton.isDisabled() && !focusNodes.contains(clearQueueButton)) focusNodes.add(0, clearQueueButton);

                if(focusedNode instanceof QueueBox) focus.set(focusNodes.indexOf(queueBox));
                else focus.set(-1);
            }
        });

        StackPane.setAlignment(removeButton, Pos.CENTER_RIGHT);

        removeSVG.setContent(SVG.REMOVE.getContent());

        removeIcon.setShape(removeSVG);
        removeIcon.setPrefSize(14, 14);
        removeIcon.setMaxSize(14,14);
        removeIcon.getStyleClass().addAll("menuIcon", "graphic");

        removeButton.getStyleClass().add("menuButton");
        removeButton.setGraphic(removeIcon);
        removeButton.setText("Remove");
        removeButton.setOnAction(e -> {

            if(menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) menuController.subtitlesController.closeSubtitles();
            if(menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) menuController.playbackSettingsController.closeSettings();

            removeButton.requestFocus();

            while(!selectedItems.isEmpty()){
                selectedItems.get(0).remove();
            }
        });

        removeButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            removeButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        removeButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            removeButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });

        removeButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(focusNodes.indexOf(removeButton));
            }
            else{
                keyboardFocusOff(removeButton);
                focus.set(-1);
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

        arrowDownSVG.setContent(SVG.ARROW_DOWN.getContent());
        arrowUpSVG.setContent(SVG.ARROW_UP.getContent());

        scrollUpIcon.setShape(arrowUpSVG);
        scrollUpIcon.getStyleClass().addAll("menuIcon", "graphic");
        scrollUpIcon.setPrefSize(12, 10);
        scrollUpIcon.setMaxSize(12, 10);

        StackPane.setAlignment(scrollUpButtonContainer, Pos.TOP_CENTER);
        StackPane.setMargin(scrollUpButtonContainer, new Insets(130, 0, 0, 0));
        scrollUpButtonContainer.setMaxHeight(20);
        scrollUpButtonContainer.getChildren().add(scrollUpButton);

        StackPane.setMargin(scrollUpButton, new Insets(5, 10, 0, 10));
        scrollUpButton.prefWidthProperty().bind(queueWrapper.widthProperty());
        scrollUpButton.setPrefHeight(15);
        scrollUpButton.getStyleClass().add("scrollToActiveButton");
        scrollUpButton.setGraphic(scrollUpIcon);
        scrollUpButton.setOnAction(e -> {
            if(menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) menuController.subtitlesController.closeSubtitles();
            if(menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) menuController.playbackSettingsController.closeSettings();

            scrollUpButton.requestFocus();
            animateScroll();
        });
        scrollUpButtonContainer.setVisible(false);
        scrollUpButtonContainer.visibleProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                if(!focusNodes.contains(scrollUpButton)){
                    if((focus.get() > -1 && focus.get() < focusNodes.size() && focusNodes.get(focus.get()) instanceof QueueBox) || scrollDownButton.isFocused()){
                        focus.set(focus.get() + 1);
                    }

                    focusNodes.add(focusNodes.indexOf(queueBox), scrollUpButton);
                }
            }
            else {
                Node focusedNode = null;
                if(focus.get() > -1 && focus.get() < focusNodes.size()) focusedNode = focusNodes.get(focus.get());
                focusNodes.remove(scrollUpButton);
                if(focusedNode instanceof QueueBox) focus.set(focusNodes.indexOf(queueBox));
                else if(scrollDownButton.isFocused()) focus.set(focusNodes.size() - 1);
            }
        });

        scrollUpButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            scrollUpButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        scrollUpButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            scrollUpButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });

        scrollUpButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(focusNodes.indexOf(scrollUpButton));
            }
            else{
                keyboardFocusOff(scrollUpButton);
                focus.set(-1);
            }
        });


        scrollDownIcon.setShape(arrowDownSVG);
        scrollDownIcon.getStyleClass().addAll("menuIcon", "graphic");
        scrollDownIcon.setPrefSize(12, 10);
        scrollDownIcon.setMaxSize(12, 10);

        StackPane.setAlignment(scrollDownButtonContainer, Pos.BOTTOM_CENTER);
        scrollDownButtonContainer.getChildren().add(scrollDownButton);
        scrollDownButtonContainer.setMaxHeight(20);

        StackPane.setMargin(scrollDownButton, new Insets(0, 10, 5, 10));
        scrollDownButton.prefWidthProperty().bind(queueWrapper.widthProperty());
        scrollDownButton.setPrefHeight(15);
        scrollDownButton.getStyleClass().add("scrollToActiveButton");
        scrollDownButton.setGraphic(scrollDownIcon);
        scrollDownButton.setOnAction(e -> {
            if(menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) menuController.subtitlesController.closeSubtitles();
            if(menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) menuController.playbackSettingsController.closeSettings();

            scrollDownButton.requestFocus();
            animateScroll();
        });

        scrollDownButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            scrollDownButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        scrollDownButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            scrollDownButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });

        scrollDownButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(focusNodes.indexOf(scrollDownButton));
            }
            else{
                keyboardFocusOff(scrollDownButton);
                focus.set(-1);
            }
        });

        scrollDownButtonContainer.setVisible(false);
        scrollDownButtonContainer.visibleProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                if(!focusNodes.contains(scrollDownButton)) focusNodes.add(scrollDownButton);
            }
            else focusNodes.remove(scrollDownButton);
        });

        queueScroll.vvalueProperty().addListener((observableValue, oldValue, newValue) -> checkScroll());

        queueScroll.heightProperty().addListener((observableValue, oldValue, newValue) -> checkScroll());


        menuController.queueContainer.getChildren().addAll(queueWrapper, scrollUpButtonContainer, scrollDownButtonContainer);

        Platform.runLater(() -> {
            addOptionsContextMenu = new AddOptionsContextMenu(this);
            shuffleTooltip = new ControlTooltip(menuController.mainController,"Shuffle is off", menuController.mainController.hotkeyController.getHotkeyString(Action.SHUFFLE), shuffleToggle, 1000);
            addTooltip = new ControlTooltip(menuController.mainController,"Browse for files to add to the play queue", "", addButton, 1000);
            addOptionsTooltip = new ControlTooltip(menuController.mainController,"More options for adding media to the play queue", "", addOptionsButton, 1000);
        });


        focusNodes.add(shuffleToggle);
        focusNodes.add(addButton);
        focusNodes.add(addOptionsButton);
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
        VBox.setMargin(queueBarTitle, new Insets(20, 10, 5, 10));
        queueBar.setPadding(new Insets(35, 40, 0, 40));
        queueBarButtonWrapper.setPadding(new Insets(10, 0, 10, 0));
        queueBarButtonWrapper.setMinHeight(100);
        queueBarButtonWrapper.setPrefHeight(100);

        multiselectPane.setMinHeight(70);
        multiselectPane.setPrefHeight(70);
        multiselectPane.setMaxHeight(70);

        StackPane.setMargin(scrollUpButton, new Insets(5, 50, 0, 50));
        StackPane.setMargin(scrollDownButton, new Insets(0, 50, 5, 50));

        queueBox.extend();
    }

    public void shrink(){
        VBox.setMargin(queueBarTitle, new Insets(0, 30, 0, 30));
        queueBar.setPadding(new Insets(20, 0, 0, 0));
        queueBarButtonWrapper.setPadding(new Insets(0, 0, 0, 0));
        queueBarButtonWrapper.setMinHeight(80);
        queueBarButtonWrapper.setPrefHeight(80);

        multiselectPane.setMinHeight(50);
        multiselectPane.setPrefHeight(50);
        multiselectPane.setMaxHeight(50);

        StackPane.setMargin(scrollUpButton, new Insets(5, 10, 0, 10));
        StackPane.setMargin(scrollDownButton, new Insets(0, 10, 5, 10));

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

        if(queueBox.itemDragActive.get() && (queueBox.draggedNode == queueBox.activeItem.get() || selectionActive.get() && selectedItems.contains(queueBox.activeItem.get()) && selectedItems.contains(queueBox.draggedNode))){
            scrollDownButtonContainer.setVisible(false);
            scrollUpButtonContainer.setVisible(false);
            return;
        }

        double scroll = queueScroll.getVvalue();

        double heightViewPort = queueScroll.getViewportBounds().getHeight();
        double heightScrollPane = queueScroll.getContent().getBoundsInLocal().getHeight();

        if(Math.abs(heightViewPort - heightScrollPane) <= QueueItem.height){
            scrollDownButtonContainer.setVisible(false);
            scrollUpButtonContainer.setVisible(false);
            return;
        }

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
        double y = queueBox.activeItem.get().getBoundsInParent().getMinY() + QueueItem.height/2.0;

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

    public void focusForward() {

        if(focus.get() < 0){
            boolean skipFocus = menuController.menuBar.focusForward();
            if(!skipFocus) return;

            keyboardFocusOn(focusNodes.get(0));
        }
        else {
            if(focus.get() > focusNodes.size() - 1) {
                keyboardFocusOn(menuController.menuBar.focusNodes.get(0));
            }
            else {
                if(focusNodes.get(focus.get()) instanceof QueueBox){
                    boolean skipFocus = queueBox.focusForward();
                    if(!skipFocus) return;

                    if(focus.get() == focusNodes.size() - 1)
                        keyboardFocusOn(menuController.menuBar.focusNodes.get(0));
                    else
                        keyboardFocusOn(focusNodes.get(focus.get() + 1));
                }
                else {
                    if(focus.get() == focusNodes.size() - 1)
                        keyboardFocusOn(menuController.menuBar.focusNodes.get(0));
                    else {
                        int newFocus = focus.get() + 1;
                        if(focusNodes.get(newFocus) instanceof QueueBox) {
                            queueBox.enterFocusStart();
                            focus.set(newFocus);
                        }
                        else keyboardFocusOn(focusNodes.get(newFocus));
                    }
                }
            }
        }
    }

    public void focusBackward() {

        if(focus.get() < 0){
            if(menuController.menuBar.focus.get() > 0) {
                menuController.menuBar.focusBackward();
            }
            else {
                if (focusNodes.get(focusNodes.size() - 1) instanceof QueueBox) {
                    queueBox.enterFocusEnd();
                    focus.set(focusNodes.size() - 1);
                }
                else keyboardFocusOn(focusNodes.get(focusNodes.size() - 1));
            }
        }
        else {
            if(focus.get() == 0)
                keyboardFocusOn(menuController.menuBar.focusNodes.get(menuController.menuBar.focusNodes.size() - 1));
            else {
                if(focusNodes.get(focus.get()) instanceof QueueBox){
                    boolean skipFocus = queueBox.focusBackward();
                    if(!skipFocus) return;

                    keyboardFocusOn(focusNodes.get(focus.get() - 1));
                }
                else {
                    int newFocus = focus.get() - 1;
                    if(focusNodes.get(newFocus) instanceof QueueBox) {
                        queueBox.enterFocusEnd();
                        focus.set(newFocus);
                    }
                    else keyboardFocusOn(focusNodes.get(newFocus));
                }
            }
        }
    }
}
