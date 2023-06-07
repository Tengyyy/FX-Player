package tengy.Windows.ChapterEdit;

import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import javafx.animation.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import tengy.AnimationsClass;
import tengy.Chapters.ChapterFrameGrabberTask;
import tengy.MainController;
import tengy.MediaItems.Chapter;
import tengy.MediaItems.MediaItem;
import tengy.Menu.Settings.Libraries.LibraryItem;
import tengy.SVG;
import tengy.Utilities;
import tengy.Windows.WindowController;
import tengy.Windows.WindowState;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.bytedeco.ffmpeg.global.avformat.AV_DISPOSITION_DEFAULT;
import static tengy.Utilities.keyboardFocusOff;
import static tengy.Utilities.keyboardFocusOn;

public class ChapterEditWindow {

    WindowController windowController;
    MainController mainController;

    public StackPane window = new StackPane();

    VBox windowContainer = new VBox();

    VBox titleContainer = new VBox();
    Label title = new Label("Chapters");

    StackPane loadingContainer = new StackPane();
    HBox loadingPane = new HBox();
    StackPane spinnerWrapper = new StackPane();
    MFXProgressSpinner progressSpinner = new MFXProgressSpinner();
    SVGPath checkSVG = new SVGPath();
    SVGPath crossSVG = new SVGPath();
    Region statusIcon = new Region();
    Label savingLabel = new Label("Saving chapters...");

    ScrollPane scrollPane;

    public VBox content = new VBox();

    StackPane buttonContainer = new StackPane();
    Button mainButton = new Button("Save changes");

    Region closeButtonIcon = new Region();
    SVGPath closeButtonSVG = new SVGPath();
    Button closeButton = new Button();

    HBox addButtonContainer = new HBox();
    SVGPath addSVG = new SVGPath();
    Region addIcon = new Region();
    Button addButton = new Button();

    StackPane popupContainer = new StackPane();


    public boolean showing = false;

    public List<ChapterEditItem> chapterEditItems = new ArrayList<>();

    BooleanProperty editActiveProperty = new SimpleBooleanProperty(false);
    public BooleanProperty saveAllowed = new SimpleBooleanProperty(false);

    PauseTransition showTimer = new PauseTransition(Duration.millis(2500));
    ParallelTransition showTransition = null;
    ParallelTransition hideTransition = null;

    public MediaItem mediaItem = null;

    SavePopUp savePopUp;

    public FFmpegFrameGrabber frameGrabber = null;
    public ExecutorService executorService = null;

    IntegerProperty focus = new SimpleIntegerProperty(-1);
    public List<Node> focusNodes = new ArrayList<>();

    public ChapterEditWindow(WindowController windowController){
        this.windowController = windowController;
        this.mainController = windowController.mainController;

        savePopUp = new SavePopUp(this);

        mainController.popupWindowContainer.getChildren().add(window);

        window.setAlignment(Pos.TOP_LEFT);

        window.prefWidthProperty().bind(Bindings.max(500, Bindings.min(700, mainController.videoImageViewWrapper.widthProperty().multiply(0.5))));
        window.maxWidthProperty().bind(Bindings.max(500, Bindings.min(700, mainController.videoImageViewWrapper.widthProperty().multiply(0.5))));

        window.prefHeightProperty().bind(Bindings.max(350, Bindings.min(1000, mainController.videoImageViewWrapper.heightProperty().multiply(0.8))));
        window.maxHeightProperty().bind(Bindings.max(350, Bindings.min(1000, mainController.videoImageViewWrapper.heightProperty().multiply(0.8))));

        window.getStyleClass().add("chapterWindow");
        window.setVisible(false);
        window.getChildren().addAll(windowContainer, buttonContainer, loadingContainer, closeButton, popupContainer);
        window.setOnMouseClicked(e -> window.requestFocus());

        StackPane.setAlignment(closeButton, Pos.TOP_RIGHT);
        StackPane.setMargin(closeButton, new Insets(10, 10, 0 ,0));
        closeButton.setPrefSize(25, 25);
        closeButton.getStyleClass().addAll("transparentButton", "popupWindowCloseButton");
        closeButton.setOnAction(e -> this.hide());
        closeButton.setFocusTraversable(false);
        closeButton.setGraphic(closeButtonIcon);
        closeButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(0);
            }
            else{
                keyboardFocusOff(closeButton);
                focus.set(-1);
            }
        });

        closeButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            closeButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        closeButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            closeButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });

        closeButtonSVG.setContent(SVG.CLOSE.getContent());

        closeButtonIcon.setShape(closeButtonSVG);
        closeButtonIcon.setMinSize(13, 13);
        closeButtonIcon.setPrefSize(13, 13);
        closeButtonIcon.setMaxSize(13, 13);
        closeButtonIcon.setMouseTransparent(true);
        closeButtonIcon.getStyleClass().add("graphic");

        content.setPadding(new Insets(15, 30, 15, 15));

        addButtonContainer.setAlignment(Pos.CENTER_LEFT);
        addButtonContainer.getChildren().add(addButton);
        VBox.setMargin(addButtonContainer, new Insets(20, 0, 0, 0 ));

        addSVG.setContent(SVG.PLUS.getContent());
        addIcon.setShape(addSVG);
        addIcon.setPrefSize(14, 14);
        addIcon.setMaxSize(14, 14);
        addIcon.getStyleClass().addAll("menuIcon", "graphic");

        addButton.setGraphic(addIcon);
        addButton.getStyleClass().add("menuButton");
        addButton.setText("Add chapter");
        addButton.setFocusTraversable(false);
        addButton.setOnAction(e -> {
            addButton.requestFocus();
            createChapter();
        });
        addButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue) {
                if(mainButton.isDisabled()) focus.set(focusNodes.size() - 1);
                else focus.set(focusNodes.size() - 2);
            }
            else {
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

        scrollPane = new ScrollPane() {
            ScrollBar vertical;

            @Override
            protected void layoutChildren() {
                super.layoutChildren();
                if (vertical == null) {
                    vertical = (ScrollBar) lookup(".scroll-bar:vertical");
                    vertical.visibleProperty().addListener((obs, old, val) -> updatePadding(val));
                }
            }
        };


        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("menuScroll");
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        scrollPane.setBackground(Background.EMPTY);
        scrollPane.setContent(content);


        windowContainer.setPadding(new Insets(15, 0, 0, 15));
        windowContainer.getChildren().addAll(titleContainer, scrollPane);
        windowContainer.setSpacing(20);
        StackPane.setMargin(windowContainer, new Insets(0, 0, 70, 0));

        titleContainer.getChildren().addAll(title);
        titleContainer.setPadding(new Insets(5, 0, 5, 0));

        title.getStyleClass().addAll("popupWindowTitle", "chapterWindowTitle");

        StackPane.setAlignment(buttonContainer, Pos.BOTTOM_CENTER);
        buttonContainer.getChildren().add(mainButton);
        buttonContainer.getStyleClass().add("buttonContainer");
        buttonContainer.setPadding(new Insets(0, 15, 0, 15));
        buttonContainer.setPrefHeight(70);
        buttonContainer.setMaxHeight(70);

        mainButton.getStyleClass().add("mainButton");
        mainButton.setTextAlignment(TextAlignment.CENTER);
        mainButton.setPrefWidth(230);
        mainButton.disableProperty().bind(saveAllowed.not().or(editActiveProperty));
        mainButton.setFocusTraversable(false);
        mainButton.setOnAction(e -> {
            mainButton.requestFocus();
            boolean timestampsCorrect = checkFields();
            if(timestampsCorrect) {
                saveChanges();
            }
            else {
                saveAllowed.set(false);
                savePopUp.show();
            }
        });

        mainButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue) {
                focus.set(focusNodes.size() - 1);
            }
            else {
                keyboardFocusOff(mainButton);
                focus.set(-1);
            }
        });

        mainButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            mainButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        mainButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            mainButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });

        mainButton.disabledProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue) focusNodes.remove(mainButton);
            else if(!focusNodes.contains(mainButton)) focusNodes.add(mainButton);
        });

        StackPane.setAlignment(mainButton, Pos.CENTER_RIGHT);

        StackPane.setAlignment(loadingContainer, Pos.BOTTOM_CENTER);
        StackPane.setMargin(loadingContainer, new Insets(0, 0, 80, 0));
        loadingContainer.prefWidthProperty().bind(Bindings.max(350, Bindings.min(600, window.widthProperty().multiply(0.8))));
        loadingContainer.maxWidthProperty().bind(Bindings.max(350, Bindings.min(600, window.widthProperty().multiply(0.8))));
        loadingContainer.setPrefHeight(50);
        loadingContainer.setMaxHeight(50);
        loadingContainer.setVisible(false);

        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(loadingContainer.widthProperty());
        clip.setHeight(50);

        loadingContainer.setClip(clip);
        loadingContainer.getChildren().add(loadingPane);

        loadingPane.prefWidthProperty().bind(loadingContainer.widthProperty());
        loadingPane.maxWidthProperty().bind(loadingContainer.widthProperty());
        loadingPane.setAlignment(Pos.CENTER_LEFT);
        loadingPane.setSpacing(20);
        loadingPane.setPadding(new Insets(10, 20, 10, 20));
        loadingPane.getChildren().addAll(spinnerWrapper, savingLabel);
        loadingPane.getStyleClass().add("savePopup");
        loadingPane.setPrefHeight(50);
        loadingPane.setMaxHeight(50);
        loadingPane.setTranslateY(50);
        loadingPane.setOpacity(0.3);


        spinnerWrapper.getChildren().addAll(progressSpinner, statusIcon);

        checkSVG.setContent(SVG.CHECK.getContent());
        crossSVG.setContent(SVG.CLOSE.getContent());

        statusIcon.setShape(checkSVG);
        statusIcon.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
        statusIcon.setPrefSize(20, 14);
        statusIcon.setMaxSize(20, 14);
        statusIcon.setVisible(false);

        progressSpinner.setRadius(10);
        progressSpinner.setColor1(Color.RED);
        progressSpinner.setColor2(Color.RED);
        progressSpinner.setColor3(Color.RED);
        progressSpinner.setColor4(Color.RED);



        savingLabel.getStyleClass().add("settingsText");

        popupContainer.setId("chapterPopupBackground");
        popupContainer.setOpacity(0);
        popupContainer.setMouseTransparent(true);

        showTimer.setOnFinished(e -> {
            if(showTransition != null && showTransition.getStatus() == Animation.Status.RUNNING) showTransition.stop();
            if(hideTransition != null && hideTransition.getStatus() == Animation.Status.RUNNING) hideTransition.stop();
            hideLoadingSpinner();
        });

        editActiveProperty.addListener((observableValue, oldValue, newValue) -> {

            if(newValue){
                setSpinnerToLoading();
                if(showTimer.getStatus() == Animation.Status.RUNNING) showTimer.stop();
                if(showTransition != null && showTransition.getStatus() == Animation.Status.RUNNING) showTransition.stop();
                if(hideTransition != null && hideTransition.getStatus() == Animation.Status.RUNNING) hideTransition.stop();

                showLoadingSpinner();
            }
            else {
                if(showTimer.getStatus() == Animation.Status.RUNNING) showTimer.stop();
                if(hideTransition != null && hideTransition.getStatus() == Animation.Status.RUNNING) hideTransition.stop();

                if(mediaItem == null){
                    if(showTransition != null && showTransition.getStatus() == Animation.Status.RUNNING) showTransition.stop();
                    loadingContainer.setVisible(false);
                }
            }
        });
    }

    public void show(MediaItem mediaItem){

        windowController.updateState(WindowState.CHAPTER_EDIT_WINDOW_OPEN);

        initializeWindow(mediaItem);

        this.showing = true;
        window.setVisible(true);

        mainController.popupWindowContainer.setMouseTransparent(false);

        window.requestFocus();

        AnimationsClass.fadeAnimation(100, mainController.popupWindowContainer, 0 , 1, false, 1, true);
    }

    public void hide(){
        this.showing = false;

        this.mediaItem = null;
        windowController.windowState = WindowState.CLOSED;

        focusNodes.clear();


        mainController.popupWindowContainer.setMouseTransparent(true);

        if(frameGrabber != null) {
            try {
                frameGrabber.stop();
            } catch (FFmpegFrameGrabber.Exception e) {
                e.printStackTrace();
            }
        }

        frameGrabber = null;

        if(executorService != null && !executorService.isShutdown()) executorService.shutdown();
        executorService = null;

        FadeTransition fadeTransition = new FadeTransition(Duration.millis(100), mainController.popupWindowContainer);
        fadeTransition.setFromValue(mainController.popupWindowContainer.getOpacity());
        fadeTransition.setToValue(0);
        fadeTransition.setOnFinished(e -> {
            window.setVisible(false);
            scrollPane.setVvalue(0);
            chapterEditItems.clear();
            saveAllowed.set(false);
            content.getChildren().clear();
            editActiveProperty.set(false);
        });
        fadeTransition.play();
    }

    private void initializeWindow(MediaItem mediaItem){
        if(mediaItem == null) return;

        this.mediaItem = mediaItem;


        focusNodes.add(closeButton);

        if(mediaItem.editActive.get()){
            editActiveProperty.set(true);

            for(Chapter chapter : mediaItem.newChapters){
                ChapterEditItem chapterEditItem = new ChapterEditItem(this, chapter, mediaItem);

                chapterEditItem.setMinHeight(105);
                chapterEditItem.setMaxHeight(105);
                chapterEditItem.setOpacity(1);

                chapterEditItems.add(chapterEditItem);
                content.getChildren().add(chapterEditItem);

                focusNodes.add(chapterEditItem);
            }
        }
        else {
            for(Chapter chapter : mediaItem.chapters){
                ChapterEditItem chapterEditItem = new ChapterEditItem(this, chapter, mediaItem);

                chapterEditItem.setMinHeight(105);
                chapterEditItem.setMaxHeight(105);
                chapterEditItem.setOpacity(1);

                chapterEditItems.add(chapterEditItem);
                content.getChildren().add(chapterEditItem);

                focusNodes.add(chapterEditItem);
            }
        }


        content.getChildren().add(addButtonContainer);
        focusNodes.add(addButton);

        initializeFrames();
    }

    private void createChapter(){
        ChapterEditItem chapterEditItem = new ChapterEditItem(this, null, mediaItem);
        if(mediaItem.hasVideo() && chapterEditItems.isEmpty()){
            chapterEditItem.updateFrame(Duration.ZERO);
        }
        else if(!mediaItem.hasVideo() && mediaItem.hasCover()){
            chapterEditItem.setCover(mediaItem.getCover());
        }
        chapterEditItems.add(chapterEditItem);

        if(mainButton.disabledProperty().get()){
            focusNodes.add(focusNodes.size() - 1, chapterEditItem);
        }
        else focusNodes.add(focusNodes.size() - 2, chapterEditItem);

        content.getChildren().add(chapterEditItems.size() - 1, chapterEditItem);

        Timeline itemMin = AnimationsClass.animateMinHeight(105, chapterEditItem);
        Timeline itemMax = AnimationsClass.animateMaxHeight(105, chapterEditItem);

        ParallelTransition parallelTransition = new ParallelTransition(itemMin, itemMax);

        parallelTransition.setOnFinished(e -> {
            FadeTransition fadeTransition = AnimationsClass.fadeAnimation(300, chapterEditItem, chapterEditItem.getOpacity(), 1, false, 1, false);
            fadeTransition.playFromStart();
        });

        parallelTransition.playFromStart();

        if(addButton.isFocused()) focus.set(focusNodes.indexOf(addButton));

        saveAllowed.set(false);
    }


    private void saveChanges() {

        if(!saveAllowed.get() || editActiveProperty.get()) return;

        mediaItem.newChapters = createChapters();
        saveAllowed.set(false);
        editActiveProperty.set(true);

        if(mainController.getMenuController().queuePage.queueBox.activeItem.get() != null && mainController.getMenuController().queuePage.queueBox.activeItem.get().getMediaItem() == mediaItem){
            mainController.getMediaInterface().resetMediaPlayer();
        }

        if(frameGrabber != null){
            try {
                frameGrabber.stop();
            }
            catch (FFmpegFrameGrabber.Exception ignored) {}
        }

        ChapterEditTask chapterEditTask = new ChapterEditTask(mediaItem);
        chapterEditTask.setOnSucceeded(e -> {

            if(chapterEditTask.mediaItem == mediaItem) {
                editActiveProperty.set(false);
                if (chapterEditTask.getValue()) setSpinnerToDone();
                else setSpinnerToFailed();

                showTimer.playFromStart();

                if(frameGrabber != null && mediaItem.hasVideo()) {
                    try {frameGrabber.start();}
                    catch (FFmpegFrameGrabber.Exception ignored) {}
                }
            }

            if(mainController.getMenuController().queuePage.queueBox.activeItem.get() != null && mainController.getMenuController().queuePage.queueBox.activeItem.get().getMediaItem() == chapterEditTask.mediaItem)
                mainController.getMediaInterface().createMedia(mainController.getMenuController().queuePage.queueBox.activeItem.get());
        });

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(chapterEditTask);
        executorService.shutdown();
    }

    private void updatePadding(boolean value){
        if(value) content.setPadding(new Insets(15, 18, 15, 15));
        else      content.setPadding(new Insets(15, 30, 15, 15));
    }

    public boolean checkFields(){

        boolean fieldsValid = true;

        for(int i = 0; i < chapterEditItems.size(); i++){
            ChapterEditItem chapterEditItem = chapterEditItems.get(i);
            if(chapterEditItem.titleField.getText().isEmpty()){
                fieldsValid = false;
                chapterEditItem.titleFieldBorder.setVisible(true);
                chapterEditItem.titleIcon.setStyle("-fx-background-color: red;");

                chapterEditItem.highlightOn();
            }
            if(!Utilities.isTime(chapterEditItem.startTimeField.getText())){
                fieldsValid = false;
                chapterEditItem.startTimeFieldBorder.setVisible(true);
                chapterEditItem.timerIcon.setStyle("-fx-background-color: red;");



                chapterEditItem.highlightOn();
            }

            if(Utilities.stringToDuration(chapterEditItem.startTimeField.getText()).greaterThanOrEqualTo(mediaItem.getDuration())){
                fieldsValid = false;
                chapterEditItem.startTimeFieldBorder.setVisible(true);
                chapterEditItem.timerIcon.setStyle("-fx-background-color: red;");

                chapterEditItem.highlightOn();
            }

            if(i > 0){
                ChapterEditItem previousItem = chapterEditItems.get(i -1);
                if(Utilities.isTime(previousItem.startTimeField.getText()) && Utilities.stringToDuration(previousItem.startTimeField.getText()).greaterThanOrEqualTo(Utilities.stringToDuration(chapterEditItem.startTimeField.getText()))){
                    fieldsValid = false;
                    chapterEditItem.startTimeFieldBorder.setVisible(true);
                    chapterEditItem.timerIcon.setStyle("-fx-background-color: red;");

                    chapterEditItem.highlightOn();
                }
            }
        }


        return fieldsValid;
    }

    public void initializeFrames(){
        if(mediaItem.hasVideo()){
            frameGrabber = new FFmpegFrameGrabber(mediaItem.getFile());
            frameGrabber.setVideoDisposition(AV_DISPOSITION_DEFAULT);
            frameGrabber.setVideoOption("vcodec", "copy");

            Integer width = mediaItem.defaultVideoStream.getWidth();
            Integer height = mediaItem.defaultVideoStream.getHeight();

            if(width != null && height != null){
                double ratio = (double) width / height;

                int newWidth = (int) Math.min(160, 90 * ratio);
                int newHeight = (int) Math.min(90, 160 / ratio);

                frameGrabber.setImageWidth(newWidth);
                frameGrabber.setImageHeight(newHeight);
            }

            try {
                frameGrabber.start();
            } catch (FFmpegFrameGrabber.Exception e) {
                e.printStackTrace();
            }

            executorService = Executors.newFixedThreadPool(1);

            for(ChapterEditItem chapterEditItem : chapterEditItems){
                if(Utilities.isTime(chapterEditItem.startTimeField.getText())){
                    Duration startTime = Utilities.stringToDuration(chapterEditItem.startTimeField.getText());
                    if(startTime.lessThan(mediaItem.getDuration())){
                        ChapterFrameGrabberTask chapterFrameGrabberTask;
                        if (startTime.greaterThan(Duration.ZERO))
                            chapterFrameGrabberTask = new ChapterFrameGrabberTask(frameGrabber, startTime.toSeconds() / mediaItem.getDuration().toSeconds());
                        else {
                            chapterFrameGrabberTask = new ChapterFrameGrabberTask(frameGrabber, (Math.min(mediaItem.getDuration().toSeconds() / 10, 1)) / mediaItem.getDuration().toSeconds());
                        }

                        chapterFrameGrabberTask.setOnSucceeded(e -> {
                            chapterEditItem.coverImage.setImage(chapterFrameGrabberTask.getValue());
                            chapterEditItem.coverImage.setVisible(true);
                            chapterEditItem.imageIcon.setVisible(false);
                            chapterEditItem.imageWrapper.setStyle("-fx-background-color: black;");
                        });

                        executorService.execute(chapterFrameGrabberTask);
                    }
                }
            }
        }
        else if(mediaItem.hasCover()){
            for(ChapterEditItem chapterEditItem : chapterEditItems){
                chapterEditItem.setCover(mediaItem.getCover());
            }
        }
    }


    public void focusForward(){

        if(savePopUp.showing){
            savePopUp.changeFocus();
            return;
        }

        if(focus.get() > 0 && focus.get() < focusNodes.size() - 1){
            if(focusNodes.get(focus.get()) instanceof ChapterEditItem chapterEditItem){
                boolean skipFocus = chapterEditItem.focusForward();
                if(!skipFocus) return;
            }
        }

        int newFocus;

        if(focus.get() >= focusNodes.size() - 1 || focus.get() == -1) newFocus = 0;
        else newFocus = focus.get() + 1;


        if(focusNodes.get(newFocus) instanceof ChapterEditItem chapterEditItem){
            keyboardFocusOn(chapterEditItem.titleField);
            Utilities.checkScrollDown(scrollPane, chapterEditItem);
            focus.set(newFocus);
        }
        else{
            keyboardFocusOn(focusNodes.get(newFocus));
            if(focusNodes.get(newFocus) == addButton) scrollPane.setVvalue(1);
        }
    }

    public void focusBackward(){

        if(savePopUp.showing){
            savePopUp.changeFocus();
            return;
        }

        if(focus.get() > 0 && focus.get() < focusNodes.size() - 1){
            if(focusNodes.get(focus.get()) instanceof ChapterEditItem chapterEditItem){
                boolean skipFocus = chapterEditItem.focusBackward();
                if(!skipFocus) return;
            }
        }

        int newFocus;

        if(focus.get() == 0) newFocus = focusNodes.size() - 1;
        else if(focus.get() == -1) newFocus = 0;
        else newFocus = focus.get() - 1;

        if(focusNodes.get(newFocus) instanceof ChapterEditItem chapterEditItem) {
            keyboardFocusOn(chapterEditItem.removeButton);
            Utilities.checkScrollUp(scrollPane, chapterEditItem);
            focus.set(newFocus);
        }
        else {
            keyboardFocusOn(focusNodes.get(newFocus));
            if(focusNodes.get(newFocus) == addButton) scrollPane.setVvalue(1);
        }
    }

    private void setSpinnerToLoading(){
        savingLabel.setText("Saving chapters...");
        statusIcon.setVisible(false);
        progressSpinner.setVisible(true);
    }

    private void setSpinnerToDone(){
        savingLabel.setText("Chapters saved");
        progressSpinner.setVisible(false);
        statusIcon.setPrefSize(20, 14);
        statusIcon.setMaxSize(20, 14);
        statusIcon.setShape(checkSVG);
        statusIcon.setVisible(true);
    }

    private void setSpinnerToFailed(){
        savingLabel.setText("Failed to save chapters");
        progressSpinner.setVisible(false);
        statusIcon.setPrefSize(19, 19);
        statusIcon.setMaxSize(19, 19);
        statusIcon.setShape(crossSVG);
        statusIcon.setVisible(true);
    }

    private void showLoadingSpinner(){

        FadeTransition fadeTransition = new FadeTransition(Duration.millis(250), loadingPane);
        fadeTransition.setFromValue(0.3);
        fadeTransition.setToValue(1);

        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(250), loadingPane);
        translateTransition.setFromY(50);
        translateTransition.setToY(0);

        showTransition = new ParallelTransition(fadeTransition, translateTransition);

        loadingContainer.setVisible(true);
        showTransition.play();
    }


    private void hideLoadingSpinner(){

        FadeTransition fadeTransition = new FadeTransition(Duration.millis(250), loadingPane);
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0.3);

        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(250), loadingPane);
        translateTransition.setFromY(0);
        translateTransition.setToY(50);

        hideTransition = new ParallelTransition(fadeTransition, translateTransition);
        hideTransition.setOnFinished(e -> loadingContainer.setVisible(false));
        hideTransition.play();
    }

    private List<Chapter> createChapters(){
        List<Chapter> chapters = new ArrayList<>();

        for(ChapterEditItem chapterEditItem : chapterEditItems){
            String title = chapterEditItem.titleField.getText();
            Duration startTime = Utilities.stringToDuration(chapterEditItem.startTimeField.getText());
            chapters.add(new Chapter(title, startTime));
        }

        return chapters;
    }

    public void remove(ChapterEditItem chapterEditItem){
        for(int i = chapterEditItem.index + 1; i < chapterEditItems.size(); i++){
            ChapterEditItem item = chapterEditItems.get(i);
            item.index--;
            item.indexLabel.setText(String.valueOf(item.index+1));
            if(i == 1){
                item.setUneditable();
            }
        }

        chapterEditItems.remove(chapterEditItem);
        focusNodes.remove(chapterEditItem);
        chapterEditItem.setMouseTransparent(true);

        saveAllowed.set(true);

        FadeTransition fadeTransition = AnimationsClass.fadeAnimation(300, chapterEditItem, chapterEditItem.getOpacity(), 0, false, 1, false);
        fadeTransition.setOnFinished(e -> {

            Timeline itemMin = AnimationsClass.animateMinHeight(0, chapterEditItem);
            Timeline itemMax = AnimationsClass.animateMaxHeight(0, chapterEditItem);

            ParallelTransition parallelTransition = new ParallelTransition(itemMin, itemMax);
            parallelTransition.setOnFinished(j -> {
                content.getChildren().remove(chapterEditItem);

            });
            parallelTransition.playFromStart();
        });

        fadeTransition.playFromStart();


    }
}
