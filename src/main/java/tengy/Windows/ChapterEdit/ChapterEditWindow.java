package tengy.Windows.ChapterEdit;

import com.github.kokorin.jaffree.ffprobe.Chapter;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.*;
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
import tengy.MediaItems.MediaItem;
import tengy.Windows.WindowController;
import tengy.Windows.WindowState;

import java.util.ArrayList;
import java.util.List;

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

    StackPane closeButtonPane = new StackPane();
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
        window.getChildren().addAll(windowContainer, buttonContainer, closeButtonPane, popupContainer);

        StackPane.setAlignment(closeButtonPane, Pos.TOP_RIGHT);
        StackPane.setMargin(closeButtonPane, new Insets(15, 15, 0 ,0));
        closeButtonPane.setPrefSize(25, 25);
        closeButtonPane.setMaxSize(25, 25);
        closeButtonPane.getChildren().addAll(closeButton, closeButtonIcon);
        closeButtonPane.setTranslateX(5);

        closeButton.setPrefWidth(25);
        closeButton.setPrefHeight(25);
        closeButton.getStyleClass().add("popupWindowCloseButton");
        closeButton.setCursor(Cursor.HAND);
        closeButton.setOpacity(0);
        closeButton.setText(null);
        closeButton.setOnAction(e -> this.hide());

        closeButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> AnimationsClass.fadeAnimation(200, closeButton, 0, 1, false, 1, true));

        closeButton.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> AnimationsClass.fadeAnimation(200, closeButton, 1, 0, false, 1, true));

        closeButtonSVG.setContent(SVG.CLOSE.getContent());

        closeButtonIcon.setShape(closeButtonSVG);
        closeButtonIcon.setMinSize(13, 13);
        closeButtonIcon.setPrefSize(13, 13);
        closeButtonIcon.setMaxSize(13, 13);
        closeButtonIcon.setMouseTransparent(true);
        closeButtonIcon.getStyleClass().add("menuIcon");


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
        addButton.getStyleClass().addAll("menuButton", "addChapterButton");
        addButton.setText("Add chapter");
        addButton.setOnAction(e -> createChapter());

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
        mainButton.setCursor(Cursor.HAND);
        mainButton.setTextAlignment(TextAlignment.CENTER);
        mainButton.setPrefWidth(230);
        mainButton.disableProperty().bind(saveAllowed.not());
        mainButton.setOnAction(e -> {
            boolean timestampsCorrect = checkFields();
            if(timestampsCorrect) saveChanges();
            else {
                saveAllowed.set(false);
                savePopUp.show();
            }
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


        mainController.popupWindowContainer.setMouseTransparent(true);

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

        for(Chapter chapter : mediaItem.chapters){
            ChapterEditItem chapterEditItem = new ChapterEditItem(this, chapter, mediaItem);

            chapterEditItems.add(chapterEditItem);
            content.getChildren().add(chapterEditItem);
        }

        content.getChildren().add(addButtonContainer);
    }

    private void createChapter(){
        ChapterEditItem chapterEditItem = new ChapterEditItem(this, null, mediaItem);
        chapterEditItems.add(chapterEditItem);

        content.getChildren().add(chapterEditItems.size() - 1, chapterEditItem);

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

                if(!chapterEditItem.mouseHover) chapterEditItem.setStyle("-fx-background-color: rgba(50,50,50,0.6);");
            }
            if(!Utilities.isTime(chapterEditItem.startTimeField.getText())){
                fieldsValid = false;
                chapterEditItem.startTimeFieldBorder.setVisible(true);

                if(!chapterEditItem.mouseHover) chapterEditItem.setStyle("-fx-background-color: rgba(50,50,50,0.6);");
            }

            if(Utilities.stringToDuration(chapterEditItem.startTimeField.getText()).greaterThanOrEqualTo(mediaItem.getDuration())){
                fieldsValid = false;
                chapterEditItem.startTimeFieldBorder.setVisible(true);

                if(!chapterEditItem.mouseHover) chapterEditItem.setStyle("-fx-background-color: rgba(50,50,50,0.6);");
            }

            if(i > 0){
                ChapterEditItem previousItem = chapterEditItems.get(i -1);
                if(Utilities.isTime(previousItem.startTimeField.getText()) && Utilities.stringToDuration(previousItem.startTimeField.getText()).greaterThanOrEqualTo(Utilities.stringToDuration(chapterEditItem.startTimeField.getText()))){
                    fieldsValid = false;
                    chapterEditItem.startTimeFieldBorder.setVisible(true);

                    if(!chapterEditItem.mouseHover) chapterEditItem.setStyle("-fx-background-color: rgba(50,50,50,0.6);");
                }
            }
        }


        return fieldsValid;
    }
}
