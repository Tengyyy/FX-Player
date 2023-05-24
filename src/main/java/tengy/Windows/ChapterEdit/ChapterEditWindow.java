package tengy.Windows.ChapterEdit;

import com.github.kokorin.jaffree.ffprobe.Chapter;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import tengy.*;
import javafx.animation.FadeTransition;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import tengy.Chapters.ChapterFrameGrabberTask;
import tengy.MediaItems.MediaItem;
import tengy.Windows.WindowController;
import tengy.Windows.WindowState;

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

    public BooleanProperty saveAllowed = new SimpleBooleanProperty(false);

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
        window.getChildren().addAll(windowContainer, buttonContainer, closeButton, popupContainer);


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


        content.setSpacing(15);
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
        addButton.setOnAction(e -> createChapter());
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
        mainButton.disableProperty().bind(saveAllowed.not());
        mainButton.setFocusTraversable(false);
        mainButton.setOnAction(e -> {
            boolean timestampsCorrect = checkFields();
            if(timestampsCorrect) saveChanges();
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


        popupContainer.setId("chapterPopupBackground");
        popupContainer.setOpacity(0);
        popupContainer.setMouseTransparent(true);
    }

    public void show(MediaItem mediaItem){

        windowController.updateState(WindowState.CHAPTER_EDIT_WINDOW_OPEN);

        initializeWindow(mediaItem);

        this.showing = true;
        window.setVisible(true);

        mainController.popupWindowContainer.setMouseTransparent(false);
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
        });
        fadeTransition.play();
    }

    private void initializeWindow(MediaItem mediaItem){
        if(mediaItem == null) return;

        this.mediaItem = mediaItem;

        focusNodes.add(closeButton);

        for(Chapter chapter : mediaItem.chapters){
            ChapterEditItem chapterEditItem = new ChapterEditItem(this, chapter, mediaItem);

            chapterEditItems.add(chapterEditItem);
            content.getChildren().add(chapterEditItem);

            focusNodes.add(chapterEditItem);
        }

        content.getChildren().add(addButtonContainer);
        focusNodes.add(addButton);

        initializeFrames();
    }

    private void createChapter(){
        ChapterEditItem chapterEditItem = new ChapterEditItem(this, null, mediaItem);
        chapterEditItems.add(chapterEditItem);

        content.getChildren().add(chapterEditItems.size() - 1, chapterEditItem);

        if(mainButton.disabledProperty().get()){
            focusNodes.add(focusNodes.size() - 1, chapterEditItem);
        }
        else focusNodes.add(focusNodes.size() - 2, chapterEditItem);

        if(addButton.isFocused()) focus.set(focusNodes.indexOf(addButton));

        saveAllowed.set(false);
    }

    private void saveChanges(){
        System.out.println("SAVING");
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
                        });

                        executorService.execute(chapterFrameGrabberTask);
                    }
                }
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
            Utilities.setScrollToNodeMiddle(scrollPane, chapterEditItem);
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
            Utilities.setScrollToNodeMiddle(scrollPane, chapterEditItem);
            focus.set(newFocus);
        }
        else {
            keyboardFocusOn(focusNodes.get(newFocus));
            if(focusNodes.get(newFocus) == addButton) scrollPane.setVvalue(1);
        }
    }
}
