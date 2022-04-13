package hans;


import com.jfoenix.controls.JFXButton;
import javafx.animation.Animation;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.*;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.util.Duration;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class QueueItem extends GridPane {

    // layout constraints for the video item
    ColumnConstraints column1 = new ColumnConstraints(70,70,70);
    ColumnConstraints column2 = new ColumnConstraints(0,100,Double.MAX_VALUE);
    ColumnConstraints column3 = new ColumnConstraints(35,35,35);
    ColumnConstraints column4 = new ColumnConstraints(35,35,35);

    RowConstraints row1 = new RowConstraints(70, 70, 70);


    Button playButton = new Button();
    Label videoTitle = new Label();

    HBox subTextWrapper = new HBox();
    Label artist = new Label();
    Label duration = new Label();

    VBox textWrapper = new VBox();

    JFXButton optionsButton = new JFXButton();

    JFXButton removeButton = new JFXButton();

    MediaItem videoItem;

    MenuController menuController;

    Region optionsIcon;

    Region playIcon;
    ImageView coverImage = new ImageView();

    Region removeIcon;

    StackPane playButtonWrapper = new StackPane();
    StackPane removeButtonWrapper = new StackPane();
    StackPane optionsButtonWrapper = new StackPane();


    Text playText = new Text();

    int videoIndex;

    ControlTooltip play, pause, remove, options;

    boolean mouseHover = false;
    boolean isActive = false;

    int durationWidth = 70;


    SVGPath playSVG, pauseSVG, removeSVG, optionsSVG;

    // the options popup for this queue item
    QueueItemOptionsPopUp optionsPopUp;



    QueueItem(MediaItem videoItem, MenuController menuController, MediaInterface mediaInterface) {

        this.videoItem = videoItem;
        this.menuController = menuController;

        videoIndex = menuController.queue.size() + 1;

        Platform.runLater(() -> optionsPopUp = new QueueItemOptionsPopUp(this));

        column2.setHgrow(Priority.ALWAYS); // makes the middle column (video title text) to take up all available space
        this.getColumnConstraints().addAll(column1, column2, column3, column4);
        this.getRowConstraints().addAll(row1);

        GridPane.setValignment(playButtonWrapper, VPos.CENTER);
        GridPane.setValignment(textWrapper, VPos.CENTER);
        GridPane.setValignment(removeButtonWrapper, VPos.CENTER);
        GridPane.setValignment(optionsButtonWrapper, VPos.CENTER);

        GridPane.setHalignment(playButtonWrapper, HPos.CENTER);
        GridPane.setHalignment(textWrapper, HPos.LEFT);
        GridPane.setHalignment(optionsButtonWrapper, HPos.CENTER);
        GridPane.setHalignment(removeButtonWrapper, HPos.CENTER);


        this.getStyleClass().add("queueItem");


        coverImage.setFitHeight(50);
        coverImage.setFitWidth(50);
        coverImage.setSmooth(true);
        coverImage.setImage(videoItem.getCover());
        coverImage.setPreserveRatio(true);

        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(15);
        dropShadow.setSpread(0.5);

        playText.setText(String.valueOf(videoIndex));
        playText.setId("playText");
        playText.setMouseTransparent(true);
        playText.setEffect(dropShadow);

        playButton.setPrefWidth(40);
        playButton.setPrefHeight(40);
        playButton.getStyleClass().add("playButton");
        playButton.setCursor(Cursor.HAND);


        playSVG = new SVGPath();
        playSVG.setContent(App.svgMap.get(SVG.PLAY_CIRCLE));

        pauseSVG = new SVGPath();
        pauseSVG.setContent(App.svgMap.get(SVG.PAUSE_CIRCLE));

        removeSVG = new SVGPath();
        removeSVG.setContent(App.svgMap.get(SVG.REMOVE));

        optionsSVG = new SVGPath();
        optionsSVG.setContent(App.svgMap.get(SVG.OPTIONS));

        playIcon = new Region();
        playIcon.setShape(playSVG);
        playIcon.setMinSize(40, 40);
        playIcon.setPrefSize(40, 40);
        playIcon.setMaxSize(40, 40);
        playIcon.setMouseTransparent(true);
        playIcon.getStyleClass().add("menuIcon");
        playIcon.setVisible(false);
        playIcon.setEffect(dropShadow);

        playButtonWrapper.getChildren().addAll(coverImage, playText, playButton, playIcon);

        videoTitle.getStyleClass().add("videoTitle");

       if(videoItem.getTitle() == null){
            videoTitle.setText(videoItem.getFile().getName());
        }
        else {
            videoTitle.setText(videoItem.getTitle());
        }
        videoTitle.setWrapText(true);
        videoTitle.setMaxHeight(40);

        artist.setText(videoItem.getArtist());
        artist.getStyleClass().add("subText");
        artist.maxWidthProperty().bind(textWrapper.widthProperty().subtract(duration.widthProperty()));

        String formattedDuration = Utilities.getTime(videoItem.getDuration());

        if(artist.getText() != null){
            formattedDuration = " • " + formattedDuration;
        }

        if(videoItem.getDuration() != null) duration.setText(formattedDuration);
        duration.getStyleClass().add("subText");

        subTextWrapper.setAlignment(Pos.TOP_LEFT);
        subTextWrapper.getChildren().addAll(artist, duration);


        /*Rectangle clip = new Rectangle(text.getWidth(), videoTitleWrapper.getHeight());
        clip.widthProperty().bind(videoTitleWrapper.widthProperty());
        clip.heightProperty().bind(videoTitleWrapper.heightProperty());*/

        textWrapper.setAlignment(Pos.CENTER_LEFT);
        //videoTitleWrapper.setClip(clip);
        textWrapper.setPrefHeight(70);
        textWrapper.getChildren().addAll(videoTitle,subTextWrapper);

            removeButton.setPrefWidth(30);
            removeButton.setPrefHeight(30);
            removeButton.setRipplerFill(Color.WHITE);
            removeButton.getStyleClass().add("removeButton");
            removeButton.setCursor(Cursor.HAND);
            removeButton.setOpacity(0);

            removeIcon = new Region();
            removeIcon.setShape(removeSVG);
            removeIcon.setMinSize(15, 15);
            removeIcon.setPrefSize(15, 15);
            removeIcon.setMaxSize(15, 15);
            removeIcon.setMouseTransparent(true);
            removeIcon.getStyleClass().add("menuIcon");

            removeButtonWrapper.getChildren().addAll(removeButton, removeIcon);

            optionsButton.setPrefWidth(30);
            optionsButton.setPrefHeight(30);
            optionsButton.setRipplerFill(Color.WHITE);
            optionsButton.getStyleClass().add("optionsButton");
            optionsButton.setCursor(Cursor.HAND);
            optionsButton.setOpacity(0);

            optionsButton.setOnAction((e) -> {
                if(optionsPopUp.isShowing()) optionsPopUp.hide();
                else optionsPopUp.showOptions();
            });

            this.setOnMouseClicked(e -> {
               if(optionsPopUp.isShowing()) optionsPopUp.hide();
            });
            this.setOnContextMenuRequested(e -> optionsPopUp.show(this, e.getScreenX(), e.getScreenY()));

            optionsIcon = new Region();
            optionsIcon.setShape(optionsSVG);
            optionsIcon.setMinSize(4, 17);
            optionsIcon.setPrefSize(4, 17);
            optionsIcon.setMaxSize(4, 17);
            optionsIcon.setMouseTransparent(true);
            optionsIcon.getStyleClass().add("menuIcon");

            optionsButtonWrapper.getChildren().addAll(optionsButton, optionsIcon);

            this.setPadding(new Insets(0, 10, 0, 0));

            this.add(playButtonWrapper, 0, 0);
            this.add(textWrapper, 1, 0);
            this.add(removeButtonWrapper, 2, 0);
            this.add(optionsButtonWrapper, 3, 0);


            if(!menuController.queueBox.getChildren().isEmpty()){
                this.setBorder(new Border(new BorderStroke(Color.web("#BFBBBB"), Color.web("#BFBBBB"), Color.web("#BFBBBB"), Color.web("#BFBBBB"),
                        BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE, BorderStrokeStyle.NONE, BorderStrokeStyle.NONE,
                        CornerRadii.EMPTY, new BorderWidths(1), Insets.EMPTY)));
            }
            else this.setBorder(Border.EMPTY);



            this.setOnMouseEntered((e) -> {
                mouseHover = true;

                if(!isActive) {
                    playText.setVisible(false);
                    playIcon.setVisible(true);
                }
                this.setStyle("-fx-background-color: #2C2C2C;");


            });

            this.setOnMouseExited((e) -> {
                mouseHover = false;

                if(!isActive) {
                    playText.setVisible(true);
                    playIcon.setVisible(false);
                }
                this.setStyle("-fx-background-color: transparent;");

            });


        playButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> {
               // AnimationsClass.queuePlayHoverOn(playButtonWrapper);

                AnimationsClass.parallelAnimation(true,AnimationsClass.scaleAnimation(100, playIcon, 1, 1.1, 1, 1.1, false, 1, false),AnimationsClass.scaleAnimation(100, playButton, 1, 1.1, 1, 1.1, false, 1, false));

        });

            playButton.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> {
                //AnimationsClass.queuePlayHoverOff(playButtonWrapper);

                AnimationsClass.parallelAnimation(true,AnimationsClass.scaleAnimation(100, playIcon, 1.1, 1, 1.1, 1, false, 1, false),AnimationsClass.scaleAnimation(100, playButton, 1.1, 1, 1.1, 1, false, 1, false));

            });

            playButton.setOnAction((e) -> {
                if(mediaInterface.currentVideo != this.videoItem){
                    mediaInterface.resetMediaPlayer();
                    mediaInterface.playedVideoIndex = -1; // moves out of the playedVideoList
                    mediaInterface.createMediaPlayer(this.videoItem);
                }
                else {
                    if(mediaInterface.atEnd) menuController.controlBarController.replayMedia();
                    else if (mediaInterface.playing) menuController.controlBarController.pause();
                    else menuController.controlBarController.play();
                }
            });

            optionsButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> {
                AnimationsClass.fadeAnimation(200, optionsButton, 0, 1, false, 1, true);
            });

            optionsButton.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> {
                AnimationsClass.fadeAnimation(200, optionsButton, 1, 0, false, 1, true);
            });

            removeButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> {
                AnimationsClass.fadeAnimation(200, removeButton, 0, 1, false, 1, true);
            });

            removeButton.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> {
                AnimationsClass.fadeAnimation(200, removeButton, 1, 0, false, 1, true);
            });

            removeButton.setOnAction((e) -> {

                if(menuController.queueBox.getChildren().indexOf(this) == 0 && menuController.queueBox.getChildren().size() > 1){

                    QueueItem queueItem = (QueueItem) menuController.queueBox.getChildren().get(1);
                    queueItem.setBorder(Border.EMPTY);
                }

                if(menuController.activeItem == this) menuController.activeItem = null;

                if(mediaInterface.currentVideo == this.videoItem) {
                    mediaInterface.resetMediaPlayer();
                    if(mediaInterface.playedVideoIndex == -1) mediaInterface.playedVideoList.remove(mediaInterface.playedVideoList.size() - 1);
                }

                /*
                    When removing items from the playedVideoList,
                    create an array of the indexes of the items that will be removed and decrement playedVideoIndex by the amount of indexes
                    that are smaller than playedVideoIndex,
                    meaning those videos were added before the active item inside playedVideoList
                */

                mediaInterface.videoList.removeAll(Collections.singletonList(videoItem));
                mediaInterface.unplayedVideoList.removeAll(Collections.singletonList(videoItem));

                if(mediaInterface.playedVideoIndex > 0){
                    ArrayList<Integer> removedVideoIndexes = new ArrayList<>();
                    for(int i = 0; i < mediaInterface.playedVideoList.size(); i++){
                        if(mediaInterface.playedVideoList.get(i).equals(videoItem)){
                            removedVideoIndexes.add(i);
                        }
                    }

                    int playedVideoIndexCorrectionCounter = 0;

                    for(Integer i : removedVideoIndexes){
                        if(i < mediaInterface.playedVideoIndex){
                            playedVideoIndexCorrectionCounter++;
                        }
                    }
                    mediaInterface.playedVideoIndex-=playedVideoIndexCorrectionCounter;
                }

                mediaInterface.playedVideoList.removeAll(Collections.singletonList(videoItem));

                menuController.queue.remove(this);
                menuController.queueBox.getChildren().remove(this);

                mediaInterface.currentVideoIndex = mediaInterface.videoList.indexOf(mediaInterface.currentVideo);
                if(mediaInterface.lastVideoIndex != -1) mediaInterface.lastVideoIndex = mediaInterface.videoList.indexOf(mediaInterface.currentVideo);

                // updates video indexes
                for(QueueItem queueItem : menuController.queue){
                    queueItem.videoIndex = menuController.queue.indexOf(queueItem) + 1;
                    queueItem.playText.setText(String.valueOf(queueItem.videoIndex));
                }
            });

            menuController.queue.add(this);
            menuController.queueBox.getChildren().add(this);

            play = new ControlTooltip("Play video", playButton, new VBox(), 1000, false);
            remove = new ControlTooltip("Remove video", removeButton, new VBox(), 1000, false);
            options = new ControlTooltip("Options", optionsButton, new VBox(), 1000, false);

        }

        public void setActive() {

        if(!isActive){
            if (menuController.activeItem != null) {
                menuController.activeItem.setInactive();
            }
            isActive = true;
            menuController.activeItem = this;
            playText.setVisible(false);
            playIcon.setVisible(true);
        }
    }

    public void setInactive(){
        isActive = false;
        playIcon.setShape(playSVG);
        play.updateText("Play video");
        if(!mouseHover){
            playText.setVisible(true);
            playIcon.setVisible(false);
        }
    }
    
    public void showMetadata(){

        System.out.println("Showing metadata\n");

    }

    public void playNext(){
        menuController.mediaInterface.setNextMedia(this.videoItem);

            AnimationsClass.openMenuNotification(menuController);
    }
}
