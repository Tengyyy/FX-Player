package tengy.Windows.ChapterEdit;

import com.github.kokorin.jaffree.ffprobe.Chapter;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;
import tengy.AnimationsClass;
import tengy.Chapters.ChapterFrameGrabberTask;
import tengy.ControlTooltip;
import tengy.MediaItems.MediaItem;
import tengy.SVG;
import tengy.Utilities;

import java.util.ArrayList;
import java.util.List;

import static tengy.Utilities.keyboardFocusOff;
import static tengy.Utilities.keyboardFocusOn;

public class ChapterEditItem extends StackPane {

    ChapterEditWindow chapterEditWindow;

    int index;
    Label indexLabel = new Label();

    StackPane imageWrapper = new StackPane();
    ImageView coverImage = new ImageView();
    Region imageIcon = new Region();
    SVGPath imageSVG = new SVGPath();


    VBox textFieldContainer = new VBox();
    HBox titleContainer = new HBox();
    SVGPath titleSVG = new SVGPath();
    Region titleIcon = new Region();
    StackPane titleFieldWrapper = new StackPane();
    StackPane titleFieldBorder = new StackPane();
    TextField titleField = new TextField();

    HBox startTimeContainer = new HBox();
    SVGPath timerSVG = new SVGPath();
    Region timerIcon = new Region();
    StackPane startTimeFieldWrapper = new StackPane();
    StackPane startTimeFieldBorder = new StackPane();
    TextField startTimeField = new TextField();

    Button removeButton = new Button();
    Region removeIcon = new Region();
    SVGPath removeSVG = new SVGPath();
    public ControlTooltip removeButtonTooltip;

    MediaItem mediaItem;

    IntegerProperty focus = new SimpleIntegerProperty(-1);
    List<Node> focusNodes = new ArrayList<>();

    boolean timeChanged = false;

    ChapterEditItem(ChapterEditWindow chapterEditWindow, Chapter chapter, MediaItem mediaItem){
        this.chapterEditWindow = chapterEditWindow;
        this.mediaItem = mediaItem;

        this.getChildren().addAll(indexLabel, imageWrapper, textFieldContainer, removeButton);
        this.getStyleClass().add("chapterContainer");
        this.setMinHeight(95);
        this.setPrefHeight(95);
        this.setMaxHeight(95);
        this.setFocusTraversable(false);
        this.setOnMouseClicked(e -> this.requestFocus());
        this.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue)
                chapterEditWindow.focus.set(index + 1);
            else chapterEditWindow.focus.set(-1);
        });

        StackPane.setAlignment(indexLabel, Pos.CENTER_LEFT);
        index = chapterEditWindow.chapterEditItems.size();

        indexLabel.setText(String.valueOf((index + 1)));
        indexLabel.getStyleClass().add("indexLabel");
        indexLabel.setMouseTransparent(true);
        indexLabel.setPrefSize(45, 95);
        indexLabel.setMaxSize(45, 95);
        indexLabel.setAlignment(Pos.CENTER);

        imageWrapper.setStyle("-fx-background-color: rgb(30,30,30); -fx-background-radius: 5;");
        imageWrapper.setPrefSize(125, 72);
        imageWrapper.setMaxSize(125, 72);
        imageWrapper.getChildren().addAll(coverImage, imageIcon);
        imageWrapper.getStyleClass().add("imageWrapper");
        StackPane.setAlignment(imageWrapper, Pos.CENTER_LEFT);
        StackPane.setMargin(imageWrapper, new Insets(0, 0, 0, 47));

        Rectangle imageWrapperClip = new Rectangle();
        imageWrapperClip.setWidth(125);
        imageWrapperClip.setHeight(70);
        imageWrapperClip.setArcWidth(20);
        imageWrapperClip.setArcHeight(20);

        imageWrapper.setClip(imageWrapperClip);


        coverImage.setFitHeight(70);
        coverImage.setFitWidth(125);
        coverImage.setSmooth(true);
        coverImage.setPreserveRatio(true);
        coverImage.setVisible(false);

        imageSVG.setContent(SVG.IMAGE_WIDE.getContent());
        imageIcon.setShape(imageSVG);
        imageIcon.setPrefSize(50, 40);
        imageIcon.setMaxSize(50, 40);
        imageIcon.getStyleClass().add("imageIcon");

        textFieldContainer.setTranslateY(-4);
        textFieldContainer.setSpacing(10);
        textFieldContainer.getChildren().addAll(titleContainer, startTimeContainer);
        textFieldContainer.setFillWidth(true);
        textFieldContainer.setAlignment(Pos.CENTER_LEFT);
        StackPane.setMargin(textFieldContainer, new Insets(0, 53, 0, 185));


        titleContainer.getChildren().addAll(titleIcon, titleFieldWrapper);
        titleContainer.setSpacing(8);
        titleContainer.setAlignment(Pos.CENTER_LEFT);

        titleSVG.setContent(SVG.TITLE.getContent());
        titleIcon.setShape(titleSVG);
        titleIcon.setMinSize(15, 15);
        titleIcon.setPrefSize(15, 15);
        titleIcon.setMaxSize(15 , 15);
        titleIcon.getStyleClass().add("menuIcon");

        titleFieldWrapper.getChildren().addAll(titleField, titleFieldBorder);

        if(chapter != null){
            String title = chapter.getTag("title");
            titleField.setText(title);

            Double startTime = chapter.getStartTime();
            if(startTime != null) startTimeField.setText(Utilities.durationToString(Duration.seconds(startTime)));
        }

        titleField.getStyleClass().addAll("customTextField", "chapterField");
        titleField.setPrefHeight(30);
        titleField.setMinHeight(30);
        titleField.setMaxHeight(30);
        titleField.setFocusTraversable(false);
        titleField.setPromptText("Title");
        titleField.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -40%);");
        titleField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if(!Utilities.isTime(startTimeField.getText())) chapterEditWindow.saveAllowed.set(false);
            else chapterEditWindow.saveAllowed.set(!newValue.isEmpty());
        });

        titleField.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                chapterEditWindow.focus.set(index + 1);
                focus.set(0);
                titleFieldBorder.setVisible(false);
                titleIcon.setStyle("-fx-background-color: white;");
                focusOn();
                highlightOff();
            }
            else {
                chapterEditWindow.focus.set(-1);
                focus.set(-1);
                keyboardFocusOff(titleField);
                focusOff();
               if(titleField.getText().isEmpty()){
                   titleFieldBorder.setVisible(true);
                   titleIcon.setStyle("-fx-background-color: red;");
                   highlightOn();
               }
               else {
                   if (titleFieldBorder.isVisible() || startTimeFieldBorder.isVisible() || startTimeField.isFocused())
                       highlightOn();
                   else
                       highlightOff();
               }
            }
        });

        focusNodes.add(titleField);

        HBox.setHgrow(titleFieldWrapper, Priority.ALWAYS);

        titleFieldBorder.getStyleClass().addAll("field-border", "field-bottom-border");
        titleFieldBorder.setMouseTransparent(true);
        titleFieldBorder.setVisible(false);
        titleFieldBorder.prefWidthProperty().bind(titleFieldWrapper.widthProperty());
        titleFieldBorder.prefHeightProperty().bind(titleFieldWrapper.heightProperty());

        startTimeContainer.getChildren().addAll(timerIcon, startTimeFieldWrapper);
        startTimeContainer.setSpacing(6);
        startTimeContainer.setAlignment(Pos.CENTER_LEFT);

        timerSVG.setContent(SVG.TIMER.getContent());
        timerIcon.setShape(timerSVG);
        timerIcon.setMinSize(17, 17);
        timerIcon.setPrefSize(17, 17);
        timerIcon.setMaxSize(17, 17);
        timerIcon.setTranslateY(-1);
        timerIcon.getStyleClass().add("menuIcon");

        startTimeFieldWrapper.getChildren().addAll(startTimeField, startTimeFieldBorder);
        startTimeFieldWrapper.setMaxWidth(150);

        startTimeField.getStyleClass().addAll("customTextField", "chapterField");
        startTimeField.setPromptText("hh:mm:ss");
        startTimeField.setPrefHeight(30);
        startTimeField.setMinHeight(30);
        startTimeField.setMaxHeight(30);
        startTimeField.setFocusTraversable(false);
        startTimeField.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -40%);");

        if(index == 0){
            startTimeField.setText("00:00");
            startTimeField.setEditable(false);
        }
        else focusNodes.add(startTimeField);

        startTimeField.setOnKeyTyped(e -> {
            if(e.getCode() == KeyCode.ENTER) this.requestFocus();
        });

        startTimeField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            timeChanged = true;

            if(titleField.getText().isEmpty()) chapterEditWindow.saveAllowed.set(false);
            else {
                boolean isTime = Utilities.isTime(newValue);
                if(!isTime) chapterEditWindow.saveAllowed.set(false);
                else {
                    Duration time = Utilities.stringToDuration(startTimeField.getText());

                    chapterEditWindow.saveAllowed.set(!time.greaterThanOrEqualTo(mediaItem.getDuration()));
                }
            }
        });

        startTimeField.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                chapterEditWindow.focus.set(index + 1);
                focus.set(1);
                startTimeFieldBorder.setVisible(false);
                timerIcon.setStyle("-fx-background-color: white;");

                focusOn();
                highlightOff();
            }
            else {
                chapterEditWindow.focus.set(-1);
                focus.set(-1);
                keyboardFocusOff(startTimeField);
                focusOff();
                boolean isTime = Utilities.isTime(startTimeField.getText());
                if(!isTime){
                    startTimeFieldBorder.setVisible(true);
                    timerIcon.setStyle("-fx-background-color: red;");

                    coverImage.setImage(null);
                    coverImage.setVisible(false);
                    imageIcon.setVisible(true);

                    highlightOn();
                    return;
                }
                else {
                    Duration time = Utilities.stringToDuration(startTimeField.getText());

                    if(time.greaterThanOrEqualTo(mediaItem.getDuration())){
                        startTimeFieldBorder.setVisible(true);
                        timerIcon.setStyle("-fx-background-color: red;");

                        highlightOn();

                        coverImage.setImage(null);
                        coverImage.setVisible(false);
                        imageIcon.setVisible(true);
                    }
                    else if(timeChanged){
                        updateFrame(time);
                    }
                }

                if (titleFieldBorder.isVisible() || startTimeFieldBorder.isVisible() || titleField.isFocused())
                    highlightOn();
                else
                    highlightOff();

                startTimeField.setText(Utilities.durationToString(Utilities.stringToDuration(startTimeField.getText())));
            }
            
            timeChanged = false;
        });

        HBox.setHgrow(startTimeFieldWrapper, Priority.ALWAYS);


        startTimeFieldBorder.getStyleClass().addAll("field-border", "field-bottom-border");
        startTimeFieldBorder.setMouseTransparent(true);
        startTimeFieldBorder.setVisible(false);
        startTimeFieldBorder.prefWidthProperty().bind(startTimeFieldWrapper.widthProperty());
        startTimeFieldBorder.prefHeightProperty().bind(startTimeFieldWrapper.heightProperty());

        StackPane.setAlignment(removeButton, Pos.CENTER_RIGHT);
        removeButton.setPrefSize(30, 30);
        removeButton.setMaxSize(30, 30);
        removeButton.getStyleClass().add("roundButton");
        removeButton.setOnAction(e -> remove());
        removeButton.setFocusTraversable(false);
        removeButton.setGraphic(removeIcon);
        removeButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue) {
                chapterEditWindow.focus.set(index + 1);
                focus.set(focusNodes.size() - 1);
                focusOn();
            }
            else {
                chapterEditWindow.focus.set(-1);
                keyboardFocusOff(removeButton);
                focus.set(-1);
                focusOff();
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

        focusNodes.add(removeButton);

        removeSVG.setContent(SVG.REMOVE.getContent());
        removeIcon.setShape(removeSVG);
        removeIcon.setMinSize(15, 15);
        removeIcon.setPrefSize(15, 15);
        removeIcon.setMaxSize(15, 15);
        removeIcon.getStyleClass().add("graphic");

        Platform.runLater(() -> removeButtonTooltip = new ControlTooltip(chapterEditWindow.mainController, "Remove chapter", "", removeButton, 1000));
    }


    private void remove(){
        for(int i = index+1; i < chapterEditWindow.chapterEditItems.size(); i++){
            ChapterEditItem chapterEditItem = chapterEditWindow.chapterEditItems.get(i);
            chapterEditItem.index--;
            chapterEditItem.indexLabel.setText(String.valueOf(chapterEditItem.index+1));
            if(i == 1){
                chapterEditItem.setUneditable();
            }
        }



        chapterEditWindow.chapterEditItems.remove(this);
        chapterEditWindow.content.getChildren().remove(this);
        chapterEditWindow.focusNodes.remove(this);

        if(index > 0){
            chapterEditWindow.focus.set(index);
        }

        chapterEditWindow.saveAllowed.set(true);
    }

    private void setUneditable(){
        startTimeField.setText("00:00");
        startTimeField.setEditable(false);
        startTimeFieldBorder.setVisible(false);
        timerIcon.setStyle("-fx-background-color: white;");
        updateFrame(Duration.ZERO);

        focusNodes.remove(startTimeField);
    }

    private void updateFrame(Duration duration){
        coverImage.setImage(null);
        coverImage.setVisible(false);
        imageIcon.setVisible(true);

        ChapterFrameGrabberTask chapterFrameGrabberTask;
        if (duration.greaterThan(Duration.ZERO))
            chapterFrameGrabberTask = new ChapterFrameGrabberTask(chapterEditWindow.frameGrabber, duration.toSeconds() / mediaItem.getDuration().toSeconds());
        else {
            chapterFrameGrabberTask = new ChapterFrameGrabberTask(chapterEditWindow.frameGrabber, (Math.min(mediaItem.getDuration().toSeconds() / 10, 1)) / mediaItem.getDuration().toSeconds());
        }

        chapterFrameGrabberTask.setOnSucceeded(e -> {
            coverImage.setImage(chapterFrameGrabberTask.getValue());
            coverImage.setVisible(true);
            imageIcon.setVisible(false);
        });

        chapterEditWindow.executorService.execute(chapterFrameGrabberTask);
    }

    public void focusOn(){
        this.pseudoClassStateChanged(PseudoClass.getPseudoClass("focus"), true);
    }

    public void focusOff(){
        this.pseudoClassStateChanged(PseudoClass.getPseudoClass("focus"), false);
    }
    
    public void highlightOn(){
        this.pseudoClassStateChanged(PseudoClass.getPseudoClass("highlighted"), true);
    }
    
    public void highlightOff(){
        this.pseudoClassStateChanged(PseudoClass.getPseudoClass("highlighted"), false);
    }

    public boolean focusForward(){

        if(focus.get() >= focusNodes.size() - 1)
            return true;

        int newFocus;

        if(focus.get() < 0) newFocus = 0;
        else newFocus = focus.get() + 1;

        keyboardFocusOn(focusNodes.get(newFocus));
        return false;
    }

    public boolean focusBackward(){
        if(focus.get() == 0)
            return true;

        int newFocus;

        if(focus.get() < 0) newFocus = focusNodes.size() -1;
        else newFocus = focus.get() -1;

        keyboardFocusOn(focusNodes.get(newFocus));
        return false;
    }
}
