package hans;

import javafx.animation.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class AnimationsClass {

    static ScaleTransition fullScreenButtonScaleTransition;

    static TranslateTransition volumeSliderTranslateTransition1;
    static TranslateTransition volumeSliderTranslateTransition2;
    static TranslateTransition volumeSliderTranslateTransition3;


    static ParallelTransition addPaneDragEntered;
    static ParallelTransition addPaneDragDropped;



    public static void openSettings(StackPane bufferPane) {

        FadeTransition fadeTransition1 = new FadeTransition(Duration.millis(100), bufferPane);
        fadeTransition1.setFromValue(0.0f);
        fadeTransition1.setToValue(1);
        fadeTransition1.setCycleCount(1);

        TranslateTransition translateTransition1 = new TranslateTransition(Duration.millis(100), bufferPane);
        translateTransition1.setFromY(170);
        translateTransition1.setToY(0);
        translateTransition1.setCycleCount(1);

        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(fadeTransition1, translateTransition1);
        parallelTransition.setCycleCount(1);
        parallelTransition.play();
    }

    public static void closeSettings(StackPane bufferPane) {

        FadeTransition fadeTransition1 = new FadeTransition(Duration.millis(100), bufferPane);
        fadeTransition1.setFromValue(1);
        fadeTransition1.setToValue(0.0f);
        fadeTransition1.setCycleCount(1);

        TranslateTransition translateTransition1 = new TranslateTransition(Duration.millis(100), bufferPane);
        translateTransition1.setFromY(0);
        translateTransition1.setToY(bufferPane.getHeight());
        translateTransition1.setCycleCount(1);

        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(fadeTransition1, translateTransition1);
        parallelTransition.setCycleCount(1);
        parallelTransition.play();
    }


    public static void closeSettingsFromPlaybackOptions(Pane settingsBackgroundPane, StackPane playbackOptionsBuffer, StackPane bufferPane) {

        settingsBackgroundPane.prefHeightProperty().unbind();
        playbackOptionsBuffer.translateYProperty().unbind();

        FadeTransition fadeTransition1 = new FadeTransition(Duration.millis(100), playbackOptionsBuffer);
        fadeTransition1.setFromValue(1);
        fadeTransition1.setToValue(0.0f);
        fadeTransition1.setCycleCount(1);

        TranslateTransition translateTransition1 = new TranslateTransition(Duration.millis(100), playbackOptionsBuffer);
        translateTransition1.setFromY(playbackOptionsBuffer.getTranslateY());
        translateTransition1.setToY(bufferPane.getHeight());
        translateTransition1.setCycleCount(1);

        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(fadeTransition1, translateTransition1);
        parallelTransition.setCycleCount(1);
        parallelTransition.play();

        parallelTransition.setOnFinished((e) -> {
            playbackOptionsBuffer.setTranslateX(settingsBackgroundPane.getWidth());
            bufferPane.setTranslateX(0);
            settingsBackgroundPane.setPrefHeight(170);
            playbackOptionsBuffer.setTranslateY(settingsBackgroundPane.getHeight() - playbackOptionsBuffer.getHeight());
            playbackOptionsBuffer.setOpacity(1);
            playbackOptionsBuffer.translateYProperty().bind(Bindings.subtract(settingsBackgroundPane.heightProperty(), playbackOptionsBuffer.heightProperty()));
            bufferPane.setTranslateY(bufferPane.getHeight());

        });
    }


    public static void closeSettingsFromPlaybackSpeed(Pane settingsBackgroundPane, ScrollPane playbackSpeedScroll, StackPane bufferPane) {
        settingsBackgroundPane.prefHeightProperty().unbind();
        playbackSpeedScroll.translateYProperty().unbind();

        FadeTransition fadeTransition1 = new FadeTransition(Duration.millis(100), playbackSpeedScroll);
        fadeTransition1.setFromValue(1);
        fadeTransition1.setToValue(0.0f);
        fadeTransition1.setCycleCount(1);

        TranslateTransition translateTransition1 = new TranslateTransition(Duration.millis(100), playbackSpeedScroll);
        translateTransition1.setFromY(playbackSpeedScroll.getTranslateY());
        translateTransition1.setToY(bufferPane.getHeight());
        translateTransition1.setCycleCount(1);


        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(fadeTransition1, translateTransition1);
        parallelTransition.setCycleCount(1);
        parallelTransition.play();

        parallelTransition.setOnFinished((e) -> {
            playbackSpeedScroll.setTranslateX(settingsBackgroundPane.getWidth());
            bufferPane.setTranslateX(0);
            settingsBackgroundPane.setPrefHeight(170);
            playbackSpeedScroll.setTranslateY(settingsBackgroundPane.getHeight() - playbackSpeedScroll.getHeight());
            playbackSpeedScroll.setOpacity(1);
            playbackSpeedScroll.translateYProperty().bind(Bindings.subtract(settingsBackgroundPane.heightProperty(), playbackSpeedScroll.heightProperty()));
            bufferPane.setTranslateY(bufferPane.getHeight());
            playbackSpeedScroll.setVvalue(0);

        });
    }

    public static void closeSettingsFromCustomSpeed(Pane settingsBackgroundPane, ScrollPane playbackSpeedScroll, StackPane customSpeedBuffer, StackPane bufferPane) {
        settingsBackgroundPane.prefHeightProperty().unbind();
        playbackSpeedScroll.translateYProperty().unbind();

        FadeTransition fadeTransition1 = new FadeTransition(Duration.millis(100), customSpeedBuffer);
        fadeTransition1.setFromValue(1);
        fadeTransition1.setToValue(0.0f);
        fadeTransition1.setCycleCount(1);

        TranslateTransition translateTransition1 = new TranslateTransition(Duration.millis(100), customSpeedBuffer);
        translateTransition1.setFromY(0);
        translateTransition1.setToY(customSpeedBuffer.getHeight());
        translateTransition1.setCycleCount(1);

        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(fadeTransition1, translateTransition1);
        parallelTransition.setCycleCount(1);
        parallelTransition.play();

        parallelTransition.setOnFinished((e) -> {
            playbackSpeedScroll.setTranslateX(settingsBackgroundPane.getWidth());
            bufferPane.setTranslateX(0);
            settingsBackgroundPane.setPrefHeight(170);
            playbackSpeedScroll.setTranslateY(settingsBackgroundPane.getHeight() - playbackSpeedScroll.getHeight());
            playbackSpeedScroll.setOpacity(1);
            playbackSpeedScroll.translateYProperty().bind(Bindings.subtract(settingsBackgroundPane.heightProperty(), playbackSpeedScroll.heightProperty()));
            bufferPane.setTranslateY(bufferPane.getHeight());
            customSpeedBuffer.setOpacity(1);
            customSpeedBuffer.setTranslateX(settingsBackgroundPane.getWidth());
            customSpeedBuffer.setTranslateY(0);
            playbackSpeedScroll.setVvalue(0);


        });
    }

    public static void openPlaybackSpeed(StackPane bufferPane, Pane settingsBackgroundPane, ScrollPane playbackSpeedScroll, double toHeight) {

        TranslateTransition translateTransition1 = new TranslateTransition(Duration.millis(100), bufferPane);
        translateTransition1.setFromX(0);
        translateTransition1.setToX(-settingsBackgroundPane.getWidth());
        translateTransition1.setCycleCount(1);
        translateTransition1.setInterpolator(Interpolator.LINEAR);

        TranslateTransition translateTransition2 = new TranslateTransition(Duration.millis(100), playbackSpeedScroll);
        translateTransition2.setFromX(settingsBackgroundPane.getWidth());
        translateTransition2.setToX(0);
        translateTransition2.setCycleCount(1);
        translateTransition2.setInterpolator(Interpolator.LINEAR);

        Timeline settingsTimeline = new Timeline();

        settingsTimeline.setCycleCount(1);
        settingsTimeline.setAutoReverse(false);

        settingsTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(100), new KeyValue(settingsBackgroundPane.prefHeightProperty(), toHeight, Interpolator.LINEAR)));


        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(translateTransition1, translateTransition2, settingsTimeline);
        parallelTransition.setCycleCount(1);
        parallelTransition.play();


        parallelTransition.setOnFinished((e) -> {
            settingsBackgroundPane.prefHeightProperty().bind(Bindings.max(Bindings.add(playbackSpeedScroll.heightProperty(), 40), 170));
        });
    }

    public static void closePlaybackSpeed(Pane settingsBackgroundPane, ScrollPane playbackSpeedScroll, StackPane bufferPane) {

        settingsBackgroundPane.prefHeightProperty().unbind();
        settingsBackgroundPane.setPrefHeight(playbackSpeedScroll.getHeight());

        TranslateTransition translateTransition1 = new TranslateTransition(Duration.millis(100), bufferPane);
        translateTransition1.setFromX(-settingsBackgroundPane.getWidth());
        translateTransition1.setToX(0);
        translateTransition1.setCycleCount(1);
        translateTransition1.setInterpolator(Interpolator.LINEAR);

        TranslateTransition translateTransition2 = new TranslateTransition(Duration.millis(100), playbackSpeedScroll);
        translateTransition2.setFromX(0);
        translateTransition2.setToX(settingsBackgroundPane.getWidth());
        translateTransition2.setCycleCount(1);
        translateTransition2.setInterpolator(Interpolator.LINEAR);

        Timeline settingsTimeline1 = new Timeline();

        settingsTimeline1.setCycleCount(1);
        settingsTimeline1.setAutoReverse(false);
        settingsTimeline1.getKeyFrames().add(new KeyFrame(Duration.millis(100), new KeyValue(settingsBackgroundPane.prefHeightProperty(), 170, Interpolator.LINEAR)));

        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(translateTransition1, translateTransition2, settingsTimeline1);
        parallelTransition.setCycleCount(1);
        parallelTransition.play();

        parallelTransition.setOnFinished((e) -> {
            playbackSpeedScroll.setVvalue(0);
        });
    }

    public static void openCustomSpeed(Pane settingsBackgroundPane, StackPane customSpeedBuffer, ScrollPane playbackSpeedScroll) {

        settingsBackgroundPane.prefHeightProperty().unbind();

        TranslateTransition translateTransition1 = new TranslateTransition(Duration.millis(100), customSpeedBuffer);
        translateTransition1.setFromX(settingsBackgroundPane.getWidth());
        translateTransition1.setToX(0);
        translateTransition1.setCycleCount(1);
        translateTransition1.setInterpolator(Interpolator.LINEAR);

        TranslateTransition translateTransition2 = new TranslateTransition(Duration.millis(100), playbackSpeedScroll);
        translateTransition2.setFromX(0);
        translateTransition2.setToX(-playbackSpeedScroll.getWidth() - 1.5);
        translateTransition2.setCycleCount(1);
        translateTransition2.setInterpolator(Interpolator.LINEAR);

        Timeline settingsTimeline1 = new Timeline();

        settingsTimeline1.setCycleCount(1);
        settingsTimeline1.setAutoReverse(false);
        settingsTimeline1.getKeyFrames().add(new KeyFrame(Duration.millis(100), new KeyValue(settingsBackgroundPane.prefHeightProperty(), 130, Interpolator.LINEAR)));

        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(translateTransition1, translateTransition2, settingsTimeline1);
        parallelTransition.setCycleCount(1);
        parallelTransition.play();

        parallelTransition.setOnFinished((e) -> {
            playbackSpeedScroll.setVvalue(0);
        });
    }


    public static void closeCustomSpeed(StackPane customSpeedBuffer, Pane settingsBackgroundPane, ScrollPane playbackSpeedScroll, double toHeight) {

        TranslateTransition translateTransition1 = new TranslateTransition(Duration.millis(100), customSpeedBuffer);
        translateTransition1.setFromX(0);
        translateTransition1.setToX(settingsBackgroundPane.getWidth());
        translateTransition1.setCycleCount(1);
        translateTransition1.setInterpolator(Interpolator.LINEAR);

        TranslateTransition translateTransition2 = new TranslateTransition(Duration.millis(100), playbackSpeedScroll);
        translateTransition2.setFromX(-settingsBackgroundPane.getWidth());
        translateTransition2.setToX(0);
        translateTransition2.setCycleCount(1);
        translateTransition2.setInterpolator(Interpolator.LINEAR);

        Timeline settingsTimeline1 = new Timeline();
        settingsTimeline1.setCycleCount(1);
        settingsTimeline1.setAutoReverse(false);
        settingsTimeline1.getKeyFrames().add(new KeyFrame(Duration.millis(100), new KeyValue(settingsBackgroundPane.prefHeightProperty(), toHeight, Interpolator.LINEAR)));

        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(translateTransition1, translateTransition2, settingsTimeline1);
        parallelTransition.setCycleCount(1);
        parallelTransition.play();

        parallelTransition.setOnFinished((e) -> {
            settingsBackgroundPane.prefHeightProperty().bind(Bindings.add(playbackSpeedScroll.heightProperty(), 40));
        });
    }

    public static void openPlaybackOptions(Pane settingsBackgroundPane, StackPane playbackOptionsBuffer, StackPane bufferPane) {

        settingsBackgroundPane.prefHeightProperty().unbind();

        TranslateTransition translateTransition1 = new TranslateTransition(Duration.millis(100), playbackOptionsBuffer);
        translateTransition1.setFromX(settingsBackgroundPane.getWidth());
        translateTransition1.setToX(0);
        translateTransition1.setCycleCount(1);
        translateTransition1.setInterpolator(Interpolator.LINEAR);

        TranslateTransition translateTransition2 = new TranslateTransition(Duration.millis(100), bufferPane);
        translateTransition2.setFromX(0);
        translateTransition2.setToX(-settingsBackgroundPane.getWidth());
        translateTransition2.setCycleCount(1);
        translateTransition2.setInterpolator(Interpolator.LINEAR);

        Timeline settingsTimeline1 = new Timeline();

        settingsTimeline1.setCycleCount(1);
        settingsTimeline1.setAutoReverse(false);
        settingsTimeline1.getKeyFrames().add(new KeyFrame(Duration.millis(100), new KeyValue(settingsBackgroundPane.prefHeightProperty(), 230, Interpolator.LINEAR)));

        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(translateTransition1, translateTransition2, settingsTimeline1);
        parallelTransition.setCycleCount(1);
        parallelTransition.play();
    }


    public static void closePlaybackOptions(StackPane playbackOptionsBuffer, Pane settingsBackgroundPane, StackPane bufferPane) {

        TranslateTransition translateTransition1 = new TranslateTransition(Duration.millis(100), playbackOptionsBuffer);
        translateTransition1.setFromX(0);
        translateTransition1.setToX(settingsBackgroundPane.getWidth());
        translateTransition1.setCycleCount(1);
        translateTransition1.setInterpolator(Interpolator.LINEAR);

        TranslateTransition translateTransition2 = new TranslateTransition(Duration.millis(100), bufferPane);
        translateTransition2.setFromX(-settingsBackgroundPane.getWidth());
        translateTransition2.setToX(0);
        translateTransition2.setCycleCount(1);
        translateTransition2.setInterpolator(Interpolator.LINEAR);

        Timeline settingsTimeline1 = new Timeline();

        settingsTimeline1.setCycleCount(1);
        settingsTimeline1.setAutoReverse(false);
        settingsTimeline1.getKeyFrames().add(new KeyFrame(Duration.millis(100), new KeyValue(settingsBackgroundPane.prefHeightProperty(), 170, Interpolator.LINEAR)));

        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(translateTransition1, translateTransition2, settingsTimeline1);
        parallelTransition.setCycleCount(1);
        parallelTransition.play();
    }

    public static void volumeSliderHoverOn(Slider volumeSlider, Label durationLabel, ProgressBar volumeTrack) {
        volumeSliderTranslateTransition1 = new TranslateTransition(Duration.millis(100), volumeSlider);
        volumeSliderTranslateTransition1.setFromX(-60);
        volumeSliderTranslateTransition1.setToX(0);
        volumeSliderTranslateTransition1.setInterpolator(Interpolator.EASE_OUT);
        volumeSliderTranslateTransition1.play();

        volumeSliderTranslateTransition2 = new TranslateTransition(Duration.millis(100), durationLabel);
        volumeSliderTranslateTransition2.setFromX(-60);
        volumeSliderTranslateTransition2.setToX(0);
        volumeSliderTranslateTransition2.setInterpolator(Interpolator.EASE_OUT);
        volumeSliderTranslateTransition2.play();

        volumeSliderTranslateTransition3 = new TranslateTransition(Duration.millis(100), volumeTrack);
        volumeSliderTranslateTransition3.setFromX(-60);
        volumeSliderTranslateTransition3.setToX(0);
        volumeSliderTranslateTransition3.setInterpolator(Interpolator.EASE_OUT);
        volumeSliderTranslateTransition3.play();
    }

    public static void volumeSliderHoverOff(Slider volumeSlider, Label durationLabel, ProgressBar volumeTrack) {
        volumeSliderTranslateTransition1.stop();
        volumeSliderTranslateTransition1 = new TranslateTransition(Duration.millis(100), volumeSlider);
        volumeSliderTranslateTransition1.setFromX(0);
        volumeSliderTranslateTransition1.setToX(-60);
        volumeSliderTranslateTransition1.setInterpolator(Interpolator.EASE_OUT);
        volumeSliderTranslateTransition1.play();

        volumeSliderTranslateTransition2.stop();
        volumeSliderTranslateTransition2 = new TranslateTransition(Duration.millis(100), durationLabel);
        volumeSliderTranslateTransition2.setFromX(0);
        volumeSliderTranslateTransition2.setToX(-60);
        volumeSliderTranslateTransition2.setInterpolator(Interpolator.EASE_OUT);
        volumeSliderTranslateTransition2.play();

        volumeSliderTranslateTransition3.stop();
        volumeSliderTranslateTransition3 = new TranslateTransition(Duration.millis(100), volumeTrack);
        volumeSliderTranslateTransition3.setFromX(0);
        volumeSliderTranslateTransition3.setToX(-60);
        volumeSliderTranslateTransition3.setInterpolator(Interpolator.EASE_OUT);
        volumeSliderTranslateTransition3.play();
    }

    public static void displayControls(ControlBarController controlBarController) {
        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(100), controlBarController.controlBar);
        translateTransition.setFromY(50);
        translateTransition.setToY(0);
        translateTransition.setCycleCount(1);
        translateTransition.setInterpolator(Interpolator.LINEAR);
        translateTransition.play();
        translateTransition.setOnFinished((e) -> {
            controlBarController.controlBarOpen = true;

        });

        controlBarController.mainController.menuButton.setVisible(true);
    }

    public static void hideControls(ControlBarController controlBarController) {

        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(100), controlBarController.controlBar);
        translateTransition.setFromY(controlBarController.controlBar.getTranslateY());
        translateTransition.setToY(50);
        translateTransition.setCycleCount(1);
        translateTransition.setInterpolator(Interpolator.LINEAR);

        translateTransition.play();

        translateTransition.setOnFinished((e) -> {
            controlBarController.controlBarOpen = false;
            controlBarController.mouseEventTracker.mouseMoving.set(false);
        });

        controlBarController.mainController.menuButton.setVisible(false);
    }

    public static void marquee(Text text, Region parent, double speed, Timeline timeline, BooleanProperty textHover, double offset){

        KeyFrame updateFrame = new KeyFrame(Duration.seconds(1 / 60d), new EventHandler<ActionEvent>() {
                private boolean rightMovement = false;

                @Override
                public void handle(ActionEvent actionEvent) {

                    double tW = text.getLayoutBounds().getWidth();
                    double pW = parent.getWidth();
                    double layoutX = text.getLayoutX();

                    if (tW <= pW && layoutX >= 0) {
                        // stop, if the pane is large enough and the position is correct
                        text.setLayoutX(0);
                        timeline.stop();
                        rightMovement = false;
                    } else {
                        if(layoutX >= 0 && !textHover.getValue()){
                            //stops the marquee animation if it reaches the left bound and the user isnt hovering the text
                            text.setLayoutX(0);
                            timeline.stop();
                            rightMovement = false;
                        }

                        // invert movement direction if bounds are reached
                        if (rightMovement && layoutX >= offset) {
                            rightMovement = false;
                        }
                        else if(!rightMovement && layoutX + tW + offset <= pW){
                            rightMovement = true;
                        }

                        // update position
                        if(textHover.getValue()) {
                            if (rightMovement) layoutX += speed;
                            else layoutX -= speed;
                        }
                        else {
                            if(layoutX > 0){
                                layoutX -= 2*speed;
                            }
                            else {
                                layoutX += 2 * speed;
                            }
                        }
                        text.setLayoutX(layoutX);
                    }
                }
            });

            timeline.getKeyFrames().add(updateFrame);
            timeline.setCycleCount(Animation.INDEFINITE);

            if(text.getLayoutBounds().getWidth() > parent.getWidth()){
                timeline.play();
            }
    }


    public static void openQueueTab(AnchorPane addPane, AnchorPane queuePane, MenuController menuController){
        addPane.translateXProperty().unbind();
        queuePane.translateXProperty().unbind();

        TranslateTransition translateTransition1 = new TranslateTransition(Duration.millis(200), addPane);
        translateTransition1.setFromX(0);
        translateTransition1.setToX(addPane.getWidth());
        translateTransition1.setCycleCount(1);
        translateTransition1.setInterpolator(Interpolator.LINEAR);

        TranslateTransition translateTransition2 = new TranslateTransition(Duration.millis(200), queuePane);
        translateTransition2.setFromX(queuePane.getTranslateX());
        translateTransition2.setToX(0);
        translateTransition2.setCycleCount(1);
        translateTransition2.setInterpolator(Interpolator.LINEAR);

        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(translateTransition1, translateTransition2);
        parallelTransition.setCycleCount(1);
        parallelTransition.setOnFinished((e) -> {
            addPane.translateXProperty().bind(addPane.getScene().widthProperty());
            menuController.queueTabOpen = true;
            menuController.tabAnimationInProgress = false;
        });
        parallelTransition.play();
    }

    public static void openAddVideosTab(AnchorPane addPane, AnchorPane queuePane, MenuController menuController){
        addPane.translateXProperty().unbind();
        queuePane.translateXProperty().unbind();

        TranslateTransition translateTransition1 = new TranslateTransition(Duration.millis(200), addPane);
        translateTransition1.setFromX(addPane.getTranslateX());
        translateTransition1.setToX(0);
        translateTransition1.setCycleCount(1);
        translateTransition1.setInterpolator(Interpolator.LINEAR);

        TranslateTransition translateTransition2 = new TranslateTransition(Duration.millis(200), queuePane);
        translateTransition2.setFromX(0);
        translateTransition2.setToX(-queuePane.getWidth());
        translateTransition2.setCycleCount(1);
        translateTransition2.setInterpolator(Interpolator.LINEAR);

        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(translateTransition1, translateTransition2);
        parallelTransition.setCycleCount(1);
        parallelTransition.setOnFinished((e) -> {
            queuePane.translateXProperty().bind(queuePane.getScene().widthProperty().multiply(-1));
            menuController.queueTabOpen = false;
            menuController.tabAnimationInProgress = false;
        });
        parallelTransition.play();
    }

    public static void addPaneDragEntered(Pane addBackground, AnchorPane addPane){

        if(addPaneDragDropped != null){
            addPaneDragDropped.stop();
        }



        ScaleTransition addPaneScaleEntered = new ScaleTransition(Duration.millis(200), addBackground);
        addPaneScaleEntered.setCycleCount(1);
        addPaneScaleEntered.setAutoReverse(false);

        addPaneScaleEntered.setFromX(addBackground.getScaleX());
        addPaneScaleEntered.setToX((addBackground.getWidth() - 50) / addBackground.getWidth());
        addPaneScaleEntered.setFromY(addBackground.getScaleY());
        addPaneScaleEntered.setToY((addBackground.getWidth() - 50) / addBackground.getWidth());

        Rectangle rect = new Rectangle();
        rect.setFill(Color.web("#202020"));
        FillTransition tr = new FillTransition();
        tr.setShape(rect);
        tr.setDuration(Duration.millis(200));
        tr.setFromValue(Color.web("#202020"));
        tr.setToValue(Color.web("#292929"));

        tr.setInterpolator(new Interpolator() {
            @Override
            protected double curve(double t) {
                addPane.setBackground(new Background(new BackgroundFill(rect.getFill(), CornerRadii.EMPTY, Insets.EMPTY)));
                return t;
            }
        });

        addPaneDragEntered = new ParallelTransition(addPaneScaleEntered, tr);
        addPaneDragEntered.play();
    }

    public static void addPaneDragDropped(Pane addBackground, AnchorPane addPane){

        if(addPaneDragEntered != null){
            addPaneDragEntered.stop();
        }

        ScaleTransition addPaneScaleDropped = new ScaleTransition(Duration.millis(200), addBackground);
        addPaneScaleDropped.setCycleCount(1);
        addPaneScaleDropped.setAutoReverse(false);

        addPaneScaleDropped.setFromX(addBackground.getScaleX());
        addPaneScaleDropped.setToX(1);
        addPaneScaleDropped.setFromY(addBackground.getScaleY());
        addPaneScaleDropped.setToY(1);

        Rectangle rect = new Rectangle();
        rect.setFill(Color.web("#292929"));
        FillTransition tr = new FillTransition();
        tr.setShape(rect);
        tr.setDuration(Duration.millis(200));
        tr.setFromValue(Color.web("#292929"));
        tr.setToValue(Color.web("#202020"));

        tr.setInterpolator(new Interpolator() {
            @Override
            protected double curve(double t) {
                addPane.setBackground(new Background(new BackgroundFill(rect.getFill(), CornerRadii.EMPTY, Insets.EMPTY)));
                return t;
            }
        });

        addPaneDragDropped = new ParallelTransition(addPaneScaleDropped, tr);
        addPaneDragDropped.play();
    }


    public static FadeTransition fadeAnimation(double duration, Region node, double fromValue, double toValue, boolean autoReverse, int cycleCount, boolean play){
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setDuration(Duration.millis(duration));
        fadeTransition.setNode(node);
        fadeTransition.setFromValue(fromValue);
        fadeTransition.setToValue(toValue);
        fadeTransition.setAutoReverse(autoReverse);
        fadeTransition.setCycleCount(cycleCount);
        if(play) fadeTransition.play();

        return fadeTransition;
    }

    public static ScaleTransition scaleAnimation(double duration, Node node, double fromX, double toX, double fromY, double toY, boolean autoReverse, int cycleCount, boolean play){
        ScaleTransition scaleTransition = new ScaleTransition();
        scaleTransition.setDuration(Duration.millis(duration));
        scaleTransition.setNode(node);
        scaleTransition.setFromX(fromX);
        scaleTransition.setToX(toX);
        scaleTransition.setFromY(fromY);
        scaleTransition.setToY(toY);
        scaleTransition.setAutoReverse(autoReverse);
        scaleTransition.setCycleCount(cycleCount);
        if(play) scaleTransition.play();

        return scaleTransition;
    }

    public static ParallelTransition parallelAnimation(boolean play, Transition... transitions){
        ParallelTransition parallelTransition = new ParallelTransition();
        for(Transition transition :  transitions){
            parallelTransition.getChildren().add(transition);
        }

        if(play) parallelTransition.play();

        return parallelTransition;
    }

}
