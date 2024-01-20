package fxplayer.chapters;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import fxplayer.playbackSettings.PlaybackSettingsState;
import fxplayer.SVG;
import fxplayer.subtitles.SubtitlesState;
import fxplayer.Utilities;

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

    public ImageView coverImage = new ImageView();
    StackPane imageContainer = new StackPane();
    StackPane imageWrapper = new StackPane();
    Region imageBorder = new Region();
    SVGPath imageSVG = new SVGPath();
    Region imageIcon = new Region();

    BooleanProperty isActive = new SimpleBooleanProperty(false);
    boolean mouseHover = false;

    int index;

    public Duration startTime;
    public Duration endTime;

    static double height = 90;

    ChapterItem(ChapterController chapterController, String title, Duration startTime, Duration endTime, File file){
        this.file = file;
        this.chapterController = chapterController;

        this.startTime = startTime;
        this.endTime = endTime;

        this.setPrefHeight(height);
        this.setMaxHeight(height);
        this.setCursor(Cursor.HAND);
        this.setBackground(Background.EMPTY);
        this.setAlignment(Pos.CENTER_LEFT);

        this.getStyleClass().add("chapterItem");

        if(!chapterController.menuController.extended.get()) this.setPadding(new Insets(0, 10, 0, 0));
        else applyRoundStyling();


        playSVG.setContent(SVG.PLAY.getContent());

        this.getChildren().addAll(playIconPane, imageContainer, textWrapper);

        this.index = chapterController.chapterPage.chapterItems.size();

        indexLabel.setText(String.valueOf(this.index + 1));
        indexLabel.getStyleClass().add("indexLabel");
        indexLabel.setMouseTransparent(true);

        playIconPane.setMinWidth(45);
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
        coverImage.setVisible(false);


        imageWrapper.setStyle("-fx-background-color: rgb(30,30,30);");
        imageWrapper.setMinSize(125, 70);
        imageWrapper.setPrefSize(125, 70);
        imageWrapper.setMaxSize(125, 70);
        imageWrapper.getChildren().addAll(coverImage, imageIcon);
        imageWrapper.getStyleClass().add("imageWrapper");

        Rectangle imageWrapperClip = new Rectangle();
        imageWrapperClip.setWidth(125);
        imageWrapperClip.setHeight(70);
        imageWrapperClip.setArcWidth(20);
        imageWrapperClip.setArcHeight(20);
        imageWrapper.setClip(imageWrapperClip);

        imageSVG.setContent(SVG.IMAGE_WIDE.getContent());
        imageIcon.setShape(imageSVG);
        imageIcon.setMinSize(50, 40);
        imageIcon.setPrefSize(50, 40);
        imageIcon.setMaxSize(50, 40);
        imageIcon.getStyleClass().add("imageIcon");

        imageContainer.setMinSize(127, 72);
        imageContainer.setPrefSize(127, 72);
        imageContainer.setMaxSize(127, 72);
        imageContainer.getChildren().addAll(imageWrapper, imageBorder);
        imageContainer.setBackground(Background.EMPTY);

        imageBorder.setMinSize(127, 72);
        imageBorder.setPrefSize(127, 72);
        imageBorder.setMaxSize(127, 72);
        imageBorder.setBackground(Background.EMPTY);
        imageBorder.getStyleClass().add("imageBorder");
        imageBorder.setMouseTransparent(true);
        imageBorder.setVisible(false);

        titleLabel.getStyleClass().add("chapterName");
        titleLabel.setWrapText(true);
        titleLabel.setMaxHeight(40);
        titleLabel.setText(title);
        titleLabel.setTextAlignment(TextAlignment.LEFT);
        titleLabel.setAlignment(Pos.CENTER_LEFT);
        titleLabel.setPadding(Insets.EMPTY);

        timeLabel.getStyleClass().add("subText");
        timeLabel.setText(Utilities.durationToString(startTime) + " - " + Utilities.durationToString(endTime));
        timeLabel.setTextAlignment(TextAlignment.LEFT);
        timeLabel.setAlignment(Pos.CENTER_LEFT);


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
            if(chapterController.menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) chapterController.menuController.subtitlesController.closeSubtitles();
            if(chapterController.menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) chapterController.menuController.playbackSettingsController.closeSettings();

            chapterController.setActiveChapter(this.index);
            chapterController.controlBarController.durationSlider.setValue(startTime.toSeconds());
        });
    }

    public void setActive(){

        this.isActive.set(true);
        this.playIcon.setStyle("-fx-background-color: red");
        this.playIcon.setVisible(true);
        this.indexLabel.setVisible(false);

        this.imageBorder.setVisible(true);

        if(!mouseHover) this.setStyle("-fx-background-color: rgba(50,50,50,0.6);");
    }

    public void setInactive(){
        this.isActive.set(false);
        this.playIcon.setStyle("-fx-background-color: rgb(200,200,200)");
        if(!mouseHover){
            this.playIcon.setVisible(false);
            this.indexLabel.setVisible(true);
        }

        this.imageBorder.setVisible(false);

        if(!mouseHover) this.setStyle("-fx-background-color: transparent;");
    }

    public void updateHeight(){
        this.setMinHeight(height);
        this.setMaxHeight(height);
    }


    public void applyRoundStyling(){
        if(!this.getStyleClass().contains("chapterItemRound")) this.getStyleClass().add("chapterItemRound");
        this.setPadding(new Insets(5, 10, 0 , 0));
    }

    public void removeRoundStyling(){
        this.getStyleClass().remove("chapterItemRound");
        this.setPadding(new Insets(0, 10, 0 , 0));
    }
}
