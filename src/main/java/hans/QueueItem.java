package hans;


import com.jfoenix.controls.JFXButton;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.*;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.util.Duration;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class QueueItem extends GridPane {

    // layout constraints for the video item
    ColumnConstraints column1 = new ColumnConstraints(60,60,60);
    ColumnConstraints column2 = new ColumnConstraints(0,100,Double.MAX_VALUE);
    ColumnConstraints column3 = new ColumnConstraints(45,45,45);
    ColumnConstraints column4 = new ColumnConstraints(45,45,45);

    RowConstraints row = new RowConstraints(60, 60, 60);

    Button playButton = new Button();
    Text videoTitle = new Text();

    StackPane videoTitleWrapper = new StackPane();

    JFXButton optionsButton = new JFXButton();

    JFXButton removeButton = new JFXButton();

    Media videoItem;
    File videoFile;// the video file that the above media object represents

    MenuController menuController;

    Region optionsIcon;

    Region playIcon;

    Region removeIcon;

    StackPane playButtonWrapper = new StackPane();
    StackPane removeButtonWrapper = new StackPane();
    StackPane optionsButtonWrapper = new StackPane();


    Text playText = new Text();

    int videoIndex;

    ControlTooltip play, pause, remove, options;

    Timeline marqueeTimeline;

    BooleanProperty titleHover = new SimpleBooleanProperty(false);

    PauseTransition countdown;

    boolean isActive = false;
    boolean mouseHover = false;

    int itemHeight = 64;
    double textHeight = 21.09375;

    String playPath = "M12,20C7.59,20 4,16.41 4,12C4,7.59 7.59,4 12,4C16.41,4 20,7.59 20,12C20,16.41 16.41,20 12,20M12,2A10,10 0 0,0 2,12A10,10 0 0,0 12,22A10,10 0 0,0 22,12A10,10 0 0,0 12,2M10,16.5L16,12L10,7.5V16.5Z";
    String pausePath = "M13,16V8H15V16H13M9,16V8H11V16H9M12,2A10,10 0 0,1 22,12A10,10 0 0,1 12,22A10,10 0 0,1 2,12A10,10 0 0,1 12,2M12,4A8,8 0 0,0 4,12A8,8 0 0,0 12,20A8,8 0 0,0 20,12A8,8 0 0,0 12,4Z";
    String removePath = "M19,6.41L17.59,5L12,10.59L6.41,5L5,6.41L10.59,12L5,17.59L6.41,19L12,13.41L17.59,19L19,17.59L13.41,12L19,6.41Z";
    String optionsPath = "M12,16A2,2 0 0,1 14,18A2,2 0 0,1 12,20A2,2 0 0,1 10,18A2,2 0 0,1 12,16M12,10A2,2 0 0,1 14,12A2,2 0 0,1 12,14A2,2 0 0,1 10,12A2,2 0 0,1 12,10M12,4A2,2 0 0,1 14,6A2,2 0 0,1 12,8A2,2 0 0,1 10,6A2,2 0 0,1 12,4Z";

    SVGPath playSVG, pauseSVG, removeSVG, optionsSVG;

    // the options popup for this queue item
    QueueItemOptionsPopUp optionsPopUp;

    QueueItem(Media videoItem, MenuController menuController, MediaInterface mediaInterface) {

        this.videoItem = videoItem;
        this.menuController = menuController;

        videoIndex = menuController.queue.size() + 1;

        Platform.runLater(() -> optionsPopUp = new QueueItemOptionsPopUp(this));

        column2.setHgrow(Priority.ALWAYS); // makes the middle column (video title text) to take up all available space
        this.getColumnConstraints().addAll(column1, column2, column3, column4);
        this.getRowConstraints().add(row);

        GridPane.setValignment(playButtonWrapper, VPos.CENTER);
        GridPane.setValignment(videoTitleWrapper, VPos.CENTER);
        GridPane.setValignment(removeButtonWrapper, VPos.CENTER);
        GridPane.setValignment(optionsButtonWrapper, VPos.CENTER);

        GridPane.setHalignment(playButtonWrapper, HPos.CENTER);
        GridPane.setHalignment(videoTitleWrapper, HPos.LEFT);
        GridPane.setHalignment(optionsButtonWrapper, HPos.CENTER);

        this.getStyleClass().add("queueItem");

        this.setBorder(new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, new CornerRadii(15),new BorderWidths(2))));

        playText.setText(String.valueOf(videoIndex));
        playText.setId("playText");
        playText.setMouseTransparent(true);

        playButton.setPrefWidth(40);
        playButton.setPrefHeight(40);
        playButton.getStyleClass().add("playButton");
        playButton.setCursor(Cursor.HAND);


        playSVG = new SVGPath();
        playSVG.setContent(playPath);

        pauseSVG = new SVGPath();
        pauseSVG.setContent(pausePath);

        removeSVG = new SVGPath();
        removeSVG.setContent(removePath);

        optionsSVG = new SVGPath();
        optionsSVG.setContent(optionsPath);

        playIcon = new Region();
        playIcon.setShape(playSVG);
        playIcon.setMinSize(40, 40);
        playIcon.setPrefSize(40, 40);
        playIcon.setMaxSize(40, 40);
        playIcon.setMouseTransparent(true);
        playIcon.setId("playIcon");
        playIcon.setVisible(false);

        playButtonWrapper.getChildren().addAll(playText, playButton, playIcon);

        videoTitle.getStyleClass().add("videoTitle");

        videoFile = new File(videoItem.getSource().replaceAll("%20", " "));
        videoTitle.setText(videoFile.getName());
        videoTitle.setManaged(false);
        videoTitle.setLayoutY(32 + 21.09375 / 4);


        Rectangle clip = new Rectangle(videoTitleWrapper.getWidth(), videoTitleWrapper.getHeight());
        clip.widthProperty().bind(videoTitleWrapper.widthProperty());
        clip.heightProperty().bind(videoTitleWrapper.heightProperty());

        videoTitleWrapper.setAlignment(Pos.CENTER_LEFT);
        videoTitleWrapper.setClip(clip);
        videoTitleWrapper.setPrefHeight(60);
        videoTitleWrapper.getChildren().add(videoTitle);
        GridPane.setMargin(videoTitleWrapper, new Insets(0, 0, 0, 10));

            removeButton.setPrefWidth(35);
            removeButton.setPrefHeight(35);
            removeButton.setRipplerFill(Color.WHITE);
            removeButton.getStyleClass().add("removeButton");
            removeButton.setCursor(Cursor.HAND);
            removeButton.setOpacity(0);

            removeIcon = new Region();
            removeIcon.setShape(removeSVG);
            removeIcon.setMinSize(20, 20);
            removeIcon.setPrefSize(20, 20);
            removeIcon.setMaxSize(20, 20);
            removeIcon.setMouseTransparent(true);
            removeIcon.setId("removeIcon");

            removeButtonWrapper.getChildren().addAll(removeButton, removeIcon);

            optionsButton.setPrefWidth(35);
            optionsButton.setPrefHeight(35);
            optionsButton.setRipplerFill(Color.WHITE);
            optionsButton.getStyleClass().add("optionsButton");
            optionsButton.setCursor(Cursor.HAND);
            optionsButton.setOpacity(0);

            optionsButton.setOnAction((e) -> {
                optionsPopUp.showOptions();
            });

            this.setOnContextMenuRequested(e ->
                optionsPopUp.show(this, e.getScreenX(), e.getScreenY()));

            optionsIcon = new Region();
            optionsIcon.setShape(optionsSVG);
            optionsIcon.setMinSize(6, 23);
            optionsIcon.setPrefSize(6, 23);
            optionsIcon.setMaxSize(6, 23);
            optionsIcon.setMouseTransparent(true);
            optionsIcon.setId("optionsIcon");

            optionsButtonWrapper.getChildren().addAll(optionsButton, optionsIcon);


            this.add(playButtonWrapper, 0, 0);
            this.add(videoTitleWrapper, 1, 0);
            this.add(removeButtonWrapper, 2, 0);
            this.add(optionsButtonWrapper, 3, 0);

            titleHover.addListener((obs,wasHover, isHover) -> {
                if(isHover){
                    if (marqueeTimeline == null) {
                        marqueeTimeline = new Timeline();
                        AnimationsClass.marquee(videoTitle, videoTitleWrapper, 0.5, marqueeTimeline, titleHover, 10);
                    } else if (marqueeTimeline.getStatus() != Animation.Status.RUNNING && videoTitle.getLayoutBounds().getWidth() > videoTitleWrapper.getWidth()) {
                        marqueeTimeline.play();
                    }
                }
            });

        countdown = new PauseTransition(Duration.millis(1000));
        countdown.setOnFinished((e) -> {
            titleHover.set(true);
        });


            this.setOnMouseEntered((e) -> {
                mouseHover = true;

                if(!isActive) {
                    playText.setVisible(false);
                    playIcon.setVisible(true);
                }
                this.setStyle("-fx-background-color: #3C3C3C;");


            });

            this.setOnMouseExited((e) -> {
                mouseHover = false;
                if(!isActive) {
                    playText.setVisible(true);
                    playIcon.setVisible(false);
                }
                this.setStyle("-fx-background-color: #2C2C2C;");

                titleHover.set(false);
                if(countdown.getStatus() == Animation.Status.RUNNING) {
                    countdown.stop();
                }
            });

            videoTitleWrapper.setOnMouseEntered((e) -> {
                if(marqueeTimeline != null && marqueeTimeline.getStatus() == Animation.Status.RUNNING) titleHover.set(true);
                else countdown.playFromStart();

                videoTitle.setUnderline(true);
            });

            videoTitleWrapper.setOnMouseExited((e) -> videoTitle.setUnderline(false));



        playButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> {
               // AnimationsClass.queuePlayHoverOn(playButtonWrapper);

                AnimationsClass.scaleAnimation(100, playButtonWrapper, 1, 1.1, 1, 1.1, false, 1, true);
            });

            playButton.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> {
                //AnimationsClass.queuePlayHoverOff(playButtonWrapper);

                AnimationsClass.scaleAnimation(100, playButtonWrapper, 1.1, 1, 1.1, 1, false, 1, true);

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
                if(menuController.activeItem == this) menuController.activeItem = null;

                if(mediaInterface.currentVideo == this.videoItem) {
                    mediaInterface.resetMediaPlayer();
                    mediaInterface.playedVideoList.remove(mediaInterface.playedVideoList.size() - 1);
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

                // updates video indexes
                for(QueueItem queueItem : menuController.queue){
                    queueItem.videoIndex = menuController.queue.indexOf(queueItem) + 1;
                    queueItem.playText.setText(String.valueOf(queueItem.videoIndex));
                }
            });

            menuController.queue.add(this);
            menuController.queueBox.getChildren().add(this);

            play = new ControlTooltip("Play video", playButton, false, new VBox(), 1000);
            remove = new ControlTooltip("Remove video", removeButton, false, new VBox(), 1000);
            options = new ControlTooltip("Options", optionsButton, false, new VBox(), 1000);


        }

        public void setActive() {

        if(!isActive){
            if (menuController.activeItem != null) {
                menuController.activeItem.isActive = false;
                menuController.activeItem.setBorder(new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, new CornerRadii(15), new BorderWidths(2))));
                menuController.activeItem.playIcon.setShape(menuController.activeItem.playSVG);
                if (!menuController.activeItem.mouseHover) {
                    menuController.activeItem.playText.setVisible(true);
                    menuController.activeItem.playIcon.setVisible(false);
                }
            }
            isActive = true;
            menuController.activeItem = this;
            this.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(15), new BorderWidths(2))));
            playText.setVisible(false);
            playIcon.setVisible(true);
        }
    }
    
    public void showMetadata(){
        System.out.println("Showing metadata");
    }

    public void playNext(){
        menuController.mediaInterface.setNextMedia(this.videoItem);
        System.out.println(videoFile.getName() + " will play next.");
    }
}
