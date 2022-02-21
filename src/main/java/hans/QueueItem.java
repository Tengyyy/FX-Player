package hans;


import com.jfoenix.controls.JFXButton;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
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

    Media videoItem; // the video file that this media object represents
    File videoFile; // need to create file instance aswell to correctly retrieve filename of the media object

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

    int itemHeight = 64;
    double textHeight = 21.09375;


    QueueItem(Media videoItem, MenuController menuController, MediaInterface mediaInterface) {

        this.videoItem = videoItem;
        this.menuController = menuController;

        videoFile = new File(videoItem.getSource().replaceAll("%20", " "));

        videoIndex = menuController.queue.size() + 1;

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

        playText.setText(String.valueOf(videoIndex));
        playText.setId("playText");
        playText.setMouseTransparent(true);

        playButton.setPrefWidth(40);
        playButton.setPrefHeight(40);
        playButton.getStyleClass().add("playButton");
        playButton.setCursor(Cursor.HAND);


        SVGPath playSVG = new SVGPath();
        playSVG.setContent("M 94.585,67.086 63.001,44.44 c -3.369,-2.416 -8.059,-0.008 -8.059,4.138 v 45.293 c 0,4.146 4.69,6.554 8.059,4.138 L 94.584,75.362 c 2.834,-2.031 2.834,-6.244 10e-4,-8.276 z M 142.411,68.9 C 141.216,31.48 110.968,1.233 73.549,0.038 53.188,-0.608 34.139,7.142 20.061,21.677 6.527,35.65 -0.584,54.071 0.038,73.549 c 1.194,37.419 31.442,67.667 68.861,68.861 0.779,0.025 1.551,0.037 2.325,0.037 19.454,0 37.624,-7.698 51.163,-21.676 13.534,-13.972 20.646,-32.394 20.024,-51.871 z m -30.798,41.436 c -10.688,11.035 -25.032,17.112 -40.389,17.112 -0.614,0 -1.228,-0.01 -1.847,-0.029 C 39.845,126.476 15.973,102.604 15.029,73.071 14.538,57.689 20.151,43.143 30.835,32.113 41.523,21.078 55.867,15.001 71.224,15.001 c 0.614,0 1.228,0.01 1.847,0.029 29.532,0.943 53.404,24.815 54.348,54.348 0.491,15.382 -5.123,29.928 -15.806,40.958 z");

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


            SVGPath removeSVG = new SVGPath();
            removeSVG.setContent("M55.931,47.463L94.306,9.09c0.826-0.827,0.826-2.167,0-2.994L88.833,0.62C88.436,0.224,87.896,0,87.335,0c-0.562,0-1.101,0.224-1.498,0.62L47.463,38.994L9.089,0.62c-0.795-0.795-2.202-0.794-2.995,0L0.622,6.096c-0.827,0.827-0.827,2.167,0,2.994l38.374,38.373L0.622,85.836c-0.827,0.827-0.827,2.167,0,2.994l5.473,5.476c0.397,0.396,0.936,0.62,1.498,0.62s1.1-0.224,1.497-0.62l38.374-38.374l38.374,38.374c0.397,0.396,0.937,0.62,1.498,0.62s1.101-0.224,1.498-0.62l5.473-5.476c0.826-0.827,0.826-2.167,0-2.994L55.931,47.463z");

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

            SVGPath optionsSVG = new SVGPath();
            optionsSVG.setContent("m 36.3,86.3 a 13.7,13.7 0 1 0 27.4,0 13.7,13.7 0 1 0 -27.4,0 m 0,-36.3 a 13.7,13.7 0 1 0 27.4,0 13.7,13.7 0 1 0 -27.4,0 m 0,-36.3 a 13.7,13.7 0 1 0 27.4,0 13.7,13.7 0 1 0 -27.4,0");

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
                playText.setVisible(false);
                playIcon.setVisible(true);
                this.setStyle("-fx-background-color: #3C3C3C;");


            });

            this.setOnMouseExited((e) -> {
                playText.setVisible(true);
                playIcon.setVisible(false);
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

                //TODO: unplayed/played list and stop the mediaplayer if this video was playing
                mediaInterface.videoList.remove(videoItem);


                menuController.queue.remove(this);
                menuController.queueBox.getChildren().remove(this);

                // updates video indexes
                for(QueueItem queueItem : menuController.queue){
                    queueItem.videoIndex = menuController.queue.indexOf(queueItem) + 1;
                    queueItem.playText.setText(String.valueOf(queueItem.videoIndex));
                }

            });


            menuController.queue.add(this);
            menuController.queueBox.getChildren().add(this);
            mediaInterface.videoList.add(videoItem);

            pause = new ControlTooltip("Pause video", playButton, false, new VBox(), 1000);
            play = new ControlTooltip("Play video", playButton, false, new VBox(), 1000);
            remove = new ControlTooltip("Remove video", removeButton, false, new VBox(), 1000);
            options = new ControlTooltip("Options", optionsButton, false, new VBox(), 1000);


        }


}
