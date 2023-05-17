package tengy.Windows.ChapterEdit;

import com.github.kokorin.jaffree.ffprobe.Chapter;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;
import tengy.AnimationsClass;
import tengy.ControlTooltip;
import tengy.MediaItems.MediaItem;
import tengy.SVG;
import tengy.Utilities;

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

    StackPane removeButtonWrapper = new StackPane();
    JFXButton removeButton = new JFXButton();
    Region removeIcon = new Region();
    SVGPath removeSVG = new SVGPath();
    public ControlTooltip removeButtonTooltip;

    MediaItem mediaItem;

    boolean mouseHover = false;

    ChapterEditItem(ChapterEditWindow chapterEditWindow, Chapter chapter, MediaItem mediaItem){
        this.chapterEditWindow = chapterEditWindow;
        this.mediaItem = mediaItem;

        this.getChildren().addAll(indexLabel, imageWrapper, textFieldContainer, removeButtonWrapper);
        this.getStyleClass().add("chapterContainer");
        this.setMinHeight(95);
        this.setPrefHeight(95);
        this.setMaxHeight(95);

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
        titleField.setPromptText("Title");
        titleField.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -40%);");
        titleField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if(!Utilities.isTime(startTimeField.getText())) chapterEditWindow.saveAllowed.set(false);
            else chapterEditWindow.saveAllowed.set(!newValue.isEmpty());
        });

        titleField.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                titleFieldBorder.setVisible(false);
                this.setStyle("-fx-background-color: rgba(70,70,70,0.6);");
            }
            else {
               if(titleField.getText().isEmpty()){
                   titleFieldBorder.setVisible(true);
                   this.setStyle("-fx-background-color: rgba(50,50,50,0.6);");
               }
               else {
                   if(!mouseHover) {
                       if (titleFieldBorder.isVisible() || startTimeFieldBorder.isVisible() || startTimeField.isFocused())
                           this.setStyle("-fx-background-color: rgba(50,50,50,0.6);");
                       else
                           this.setStyle("-fx-background-color: transparent;");
                   }
               }
            }
        });

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
        startTimeField.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -40%);");
        startTimeField.textProperty().addListener((observableValue, oldValue, newValue) -> {
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
                startTimeFieldBorder.setVisible(false);
                this.setStyle("-fx-background-color: rgba(70,70,70,0.6);");
            }
            else {
                boolean isTime = Utilities.isTime(startTimeField.getText());
                if(!isTime){
                    startTimeFieldBorder.setVisible(true);
                    if(!mouseHover) this.setStyle("-fx-background-color: rgba(50,50,50,0.6);");
                    return;
                }
                else {
                    Duration time = Utilities.stringToDuration(startTimeField.getText());

                    if(time.greaterThanOrEqualTo(mediaItem.getDuration())){
                        startTimeFieldBorder.setVisible(true);

                        if(!mouseHover) this.setStyle("-fx-background-color: rgba(50,50,50,0.6);");
                    }
                }

                if(!mouseHover) {
                    if (titleFieldBorder.isVisible() || startTimeFieldBorder.isVisible() || titleField.isFocused())
                        this.setStyle("-fx-background-color: rgba(50,50,50,0.6);");
                    else
                        this.setStyle("-fx-background-color: transparent;");
                }

                startTimeField.setText(Utilities.durationToString(Utilities.stringToDuration(startTimeField.getText())));
            }
        });

        HBox.setHgrow(startTimeFieldWrapper, Priority.ALWAYS);


        startTimeFieldBorder.getStyleClass().addAll("field-border", "field-bottom-border");
        startTimeFieldBorder.setMouseTransparent(true);
        startTimeFieldBorder.setVisible(false);
        startTimeFieldBorder.prefWidthProperty().bind(startTimeFieldWrapper.widthProperty());
        startTimeFieldBorder.prefHeightProperty().bind(startTimeFieldWrapper.heightProperty());

        removeButton.setPrefWidth(30);
        removeButton.setPrefHeight(30);
        removeButton.setRipplerFill(Color.WHITE);
        removeButton.getStyleClass().add("roundButton");
        removeButton.setCursor(Cursor.HAND);
        removeButton.setOpacity(0);
        removeButton.setText(null);
        removeButton.setOnAction(e -> remove());
        removeButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> AnimationsClass.fadeAnimation(200, removeButton, 0, 1, false, 1, true));
        removeButton.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> AnimationsClass.fadeAnimation(200, removeButton, 1, 0, false, 1, true));

        removeSVG.setContent(SVG.REMOVE.getContent());
        removeIcon.setShape(removeSVG);
        removeIcon.setMinSize(15, 15);
        removeIcon.setPrefSize(15, 15);
        removeIcon.setMaxSize(15, 15);
        removeIcon.setMouseTransparent(true);
        removeIcon.getStyleClass().add("menuIcon");

        removeButtonWrapper.getChildren().addAll(removeButton, removeIcon);
        removeButtonWrapper.setPrefSize(35, 35);
        removeButtonWrapper.setMaxSize(35, 35);
        StackPane.setAlignment(removeButtonWrapper, Pos.CENTER_RIGHT);

        this.setOnMouseEntered((e) -> {
            mouseHover = true;
            this.setStyle("-fx-background-color: rgba(70,70,70,0.6);");
        });
        this.setOnMouseExited((e) -> {
            mouseHover = false;
            if(titleFieldBorder.isVisible() || startTimeFieldBorder.isVisible() || titleField.isFocused() || startTimeField.isFocused())
                this.setStyle("-fx-background-color: rgba(50,50,50,0.6);");
            else
                this.setStyle("-fx-background-color: transparent;");
        });

        Platform.runLater(() -> removeButtonTooltip = new ControlTooltip(chapterEditWindow.mainController, "Remove chapter", "", removeButton, 1000));
    }


    private void remove(){
        for(int i = index+1; i < chapterEditWindow.chapterEditItems.size(); i++){
            ChapterEditItem chapterEditItem = chapterEditWindow.chapterEditItems.get(i);
            chapterEditItem.index--;
            chapterEditItem.indexLabel.setText(String.valueOf(chapterEditItem.index+1));
        }

        chapterEditWindow.chapterEditItems.remove(this);
        chapterEditWindow.content.getChildren().remove(this);
        chapterEditWindow.saveAllowed.set(true);
    }
}
