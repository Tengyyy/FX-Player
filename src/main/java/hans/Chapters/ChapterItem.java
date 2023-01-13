package hans.Chapters;

import hans.App;
import hans.MediaItems.MediaItem;
import hans.SVG;
import hans.Utilities;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.io.File;


public class ChapterItem extends HBox {

    File file;
    ChapterController chapterController;

    VBox textWrapper = new VBox();

    Label titleLabel = new Label();
    Label timeLabel = new Label();

    StackPane playIconPane = new StackPane();
    Region playIcon = new Region();

    Label indexLabel = new Label();

    SVGPath playSVG = new SVGPath();

    ImageView coverImage = new ImageView();
    StackPane imageWrapper = new StackPane();
    Region imageBorder = new Region();

    BooleanProperty isActive = new SimpleBooleanProperty(false);
    boolean mouseHover = false;

    int index;

    ChapterItem(ChapterController chapterController, String title, Duration startTime, Duration endTime, File file){
        this.file = file;
        this.chapterController = chapterController;

        this.setPrefHeight(90);
        this.setMaxHeight(90);
        this.setCursor(Cursor.HAND);
        this.setBackground(Background.EMPTY);
        this.setAlignment(Pos.CENTER_LEFT);
        this.setPadding(new Insets(0, 10, 0, 0));

        playSVG.setContent(App.svgMap.get(SVG.PLAY));

        this.getChildren().addAll(playIconPane, imageWrapper, textWrapper);

        this.index = chapterController.chapterPage.chapterBox.getChildren().size();

        indexLabel.setText(String.valueOf(this.index + 1));
        indexLabel.getStyleClass().add("indexLabel");
        indexLabel.setMouseTransparent(true);

        playIconPane.setPrefWidth(45);
        playIconPane.setMaxWidth(45);
        playIconPane.getChildren().addAll(indexLabel, playIcon);
        playIconPane.setMouseTransparent(true);

        playIcon.setShape(playSVG);
        playIcon.setMinSize(13, 15);
        playIcon.setPrefSize(13, 15);
        playIcon.setMaxSize(13, 15);
        playIcon.setId("playIcon");
        playIcon.setVisible(false);
        playIcon.setTranslateX(3);


        coverImage.setFitHeight(70);
        coverImage.setFitWidth(125);
        coverImage.setSmooth(true);
        coverImage.setPreserveRatio(true);


        imageWrapper.setStyle("-fx-background-color: rgba(64,64,64, 0.7);");


        imageWrapper.setPrefSize(129, 74);
        imageWrapper.setMaxSize(129, 74);
        imageWrapper.getChildren().addAll(coverImage, imageBorder);
        imageWrapper.getStyleClass().add("imageWrapper");

        imageBorder.setPrefSize(129, 74);
        imageBorder.setMaxSize(129, 74);
        imageBorder.setBackground(Background.EMPTY);
        imageBorder.getStyleClass().add("imageBorder");
        imageBorder.setMouseTransparent(true);
        imageBorder.setVisible(false);

        titleLabel.getStyleClass().add("chapterName");
        titleLabel.setWrapText(true);
        titleLabel.setMaxHeight(40);
        titleLabel.setText(title);
        titleLabel.setTextAlignment(TextAlignment.LEFT);

        timeLabel.getStyleClass().add("subText");
        timeLabel.setText(Utilities.getTime(startTime) + " - " + Utilities.getTime(endTime));
        timeLabel.setTextAlignment(TextAlignment.LEFT);

        textWrapper.setAlignment(Pos.CENTER_LEFT);
        textWrapper.setPrefHeight(70);
        textWrapper.getChildren().addAll(titleLabel,timeLabel);
        HBox.setMargin(textWrapper, new Insets(0, 0, 0, 10));

        this.setOnMouseEntered((e) -> {
            mouseHover = true;

            playIcon.setVisible(true);
            indexLabel.setVisible(false);
            this.setStyle("-fx-background-color: rgba(70,70,70,0.6);");
        });

        this.setOnMouseExited((e) -> {
            mouseHover = false;

            if (!isActive.get()){
                playIcon.setVisible(false);
                indexLabel.setVisible(true);
                this.setStyle("-fx-background-color: transparent;");
            }
            else this.setStyle("-fx-background-color: rgba(50,50,50,0.6);");
        });

        this.setOnMouseClicked(e -> {
            chapterController.setActiveChapter(this.index);
            chapterController.controlBarController.durationSlider.setValue(startTime.toSeconds());
        });
    }

    public void setActive(){

        this.isActive.set(true);
        this.playIcon.setStyle("-fx-background-color: red");
        this.playIcon.setVisible(true);
        this.indexLabel.setVisible(false);

        if(this.coverImage.getImage() != null) this.imageBorder.setVisible(true);

        if(!mouseHover) this.setStyle("-fx-background-color: rgba(50,50,50,0.6);");
    }

    public void setInactive(){
        this.isActive.set(false);
        this.playIcon.setStyle("-fx-background-color: rgb(200,200,200)");
        if(!mouseHover){
            this.playIcon.setVisible(false);
            this.indexLabel.setVisible(true);
        }
        this.setCursor(Cursor.HAND);

        this.imageBorder.setVisible(false);

        if(!mouseHover) this.setStyle("-fx-background-color: transparent;");
    }
}
