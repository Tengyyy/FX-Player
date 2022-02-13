package hans;

import javafx.animation.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
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

    static Timeline videoNameTimeline;
    static Timeline resetTimeline;

    static KeyFrame updateFrame;

    static Timeline queueItemTimeline;
    static Timeline queueItemResetTimeline;

    static KeyFrame queueUpdateFrame;

    static ParallelTransition addPaneDragEntered;
    static ParallelTransition addPaneDragDropped;

    static FadeTransition queueNotificationFade;

    static FadeTransition optionsButtonBackgroundFadeOn;
    static FadeTransition optionsButtonBackgroundFadeOff;

    static ScaleTransition queuePlayButtonScaleOn;
    static ScaleTransition queuePlayButtonScaleOff;


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

    public static void openCaptions(Line captionLine) {
        ScaleTransition scale = new ScaleTransition(Duration.millis(100), captionLine);
        scale.setFromX(0);
        scale.setToX(1);
        scale.setCycleCount(1);
        scale.setInterpolator(Interpolator.LINEAR);
        scale.play();
    }

    public static void closeCaptions(Line captionLine) {
        ScaleTransition scale = new ScaleTransition(Duration.millis(100), captionLine);
        scale.setFromX(1);
        scale.setToX(0);
        scale.setCycleCount(1);
        scale.setInterpolator(Interpolator.LINEAR);
        scale.play();
    }

    public static void fullScreenHoverOn(ImageView fullScreenIcon) {
        fullScreenButtonScaleTransition = new ScaleTransition(Duration.millis(200), fullScreenIcon);
        fullScreenButtonScaleTransition.setCycleCount(2);
        fullScreenButtonScaleTransition.setAutoReverse(true);
        fullScreenButtonScaleTransition.setFromX(1);
        fullScreenButtonScaleTransition.setToX(1.3);
        fullScreenButtonScaleTransition.setFromY(1);
        fullScreenButtonScaleTransition.setToY(1.3);
        fullScreenButtonScaleTransition.play();
    }

    public static void fullScreenHoverOff(ImageView fullScreenIcon) {
        fullScreenButtonScaleTransition.stop();
        fullScreenIcon.setScaleX(1);
        fullScreenIcon.setScaleY(1);
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

    public static void marqueeOn(Text text, HBox parentBox) {

        if (videoNameTimeline == null) {

            videoNameTimeline = new Timeline();

            updateFrame = new KeyFrame(Duration.seconds(1 / 60d), new EventHandler<ActionEvent>() {

                private boolean rightMovement;

                @Override
                public void handle(ActionEvent event) {
                    double tW = text.getLayoutBounds().getWidth();
                    double pW = parentBox.getWidth();
                    double layoutX = text.getLayoutX();

                    if ((rightMovement && layoutX >= 0) || (!rightMovement && layoutX + tW <= pW)) {
                        // invert movement, if bounds are reached
                        rightMovement = !rightMovement;
                    }

                    // update position
                    if (rightMovement) {
                        layoutX += 0.5;
                    } else {
                        layoutX -= 0.5;
                    }
                    text.setLayoutX(layoutX);
                }
            });

            videoNameTimeline.getKeyFrames().add(updateFrame);
            videoNameTimeline.setCycleCount(Animation.INDEFINITE);
        }

        if (videoNameTimeline != null && resetTimeline != null) {
            if (videoNameTimeline.getStatus() != Animation.Status.RUNNING && resetTimeline.getStatus() != Animation.Status.RUNNING && text.getLayoutBounds().getWidth() > parentBox.getWidth()) {
                text.setLayoutX(0);
                videoNameTimeline.play();
            }
        }
    }


    public static void marqueeOff(Text text) {

        if (resetTimeline == null) {

            resetTimeline = new Timeline();

            KeyFrame resetFrame = new KeyFrame(Duration.seconds(1 / 60d), new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent event) {

                    double layoutX = text.getLayoutX();

                    if (Math.round(layoutX) == 0) {
                        resetTimeline.stop();
                    } else if (layoutX < 0)
                        layoutX += 1;


                    text.setLayoutX(layoutX);
                }
            });

            resetTimeline.getKeyFrames().add(resetFrame);
            resetTimeline.setCycleCount(Animation.INDEFINITE);
        }

        if (videoNameTimeline != null && videoNameTimeline.getStatus() == Animation.Status.RUNNING) {
            videoNameTimeline.stop();
            resetTimeline.play();
        }
    }


    public static void stopMarquee(Text videoNameText) {
        if (videoNameTimeline != null && videoNameTimeline.getStatus() == Animation.Status.RUNNING) {
            videoNameTimeline.stop();
            videoNameText.setLayoutX(0);
        } else if (resetTimeline != null && resetTimeline.getStatus() == Animation.Status.RUNNING) {
            resetTimeline.stop();
            videoNameText.setLayoutX(0);
        }
    }



    public static void durationSliderHoverOn(ProgressBar durationTrack, Slider durationSlider) {
        Timeline durationSliderTimelineOn = new Timeline();

        durationSliderTimelineOn.setCycleCount(1);
        durationSliderTimelineOn.setAutoReverse(false);
        durationSliderTimelineOn.getKeyFrames().add(new KeyFrame(Duration.millis(100), new KeyValue(durationTrack.scaleYProperty(), 1.6, Interpolator.LINEAR)));
        durationSliderTimelineOn.getKeyFrames().add(new KeyFrame(Duration.millis(100), new KeyValue(durationSlider.lookup(".thumb").scaleXProperty(), 1, Interpolator.LINEAR)));
        durationSliderTimelineOn.getKeyFrames().add(new KeyFrame(Duration.millis(100), new KeyValue(durationSlider.lookup(".thumb").scaleYProperty(), 1, Interpolator.LINEAR)));
        durationSliderTimelineOn.play();
    }

    public static void durationSliderHoverOff(ProgressBar durationTrack, Slider durationSlider) {

        Timeline durationSliderTimelineOff = new Timeline();

        durationSliderTimelineOff.setCycleCount(1);
        durationSliderTimelineOff.setAutoReverse(false);
        durationSliderTimelineOff.getKeyFrames().add(new KeyFrame(Duration.millis(100), new KeyValue(durationTrack.scaleYProperty(), 1, Interpolator.LINEAR)));
        durationSliderTimelineOff.getKeyFrames().add(new KeyFrame(Duration.millis(100), new KeyValue(durationSlider.lookup(".thumb").scaleXProperty(), 0, Interpolator.LINEAR)));
        durationSliderTimelineOff.getKeyFrames().add(new KeyFrame(Duration.millis(100), new KeyValue(durationSlider.lookup(".thumb").scaleYProperty(), 0, Interpolator.LINEAR)));
        durationSliderTimelineOff.play();
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

    public static void queueNotificationBlink(Label queueNotification){
        queueNotificationFade = new FadeTransition(Duration.millis(500), queueNotification);
        queueNotificationFade.setFromValue(1);
        queueNotificationFade.setToValue(0.0f);
        queueNotificationFade.setAutoReverse(true);
        queueNotificationFade.setCycleCount(4);
        queueNotificationFade.play();
    }

    public static void queueButtonBackgroundHoverOn(Button button){
        optionsButtonBackgroundFadeOn = new FadeTransition(Duration.millis(200), button);
        optionsButtonBackgroundFadeOn.setFromValue(0);
        optionsButtonBackgroundFadeOn.setToValue(1);
        optionsButtonBackgroundFadeOn.play();
    }

    public static void queueButtonBackgroundHoverOff(Button button){
        optionsButtonBackgroundFadeOff = new FadeTransition(Duration.millis(200), button);
        optionsButtonBackgroundFadeOff.setFromValue(1);
        optionsButtonBackgroundFadeOff.setToValue(0);
        optionsButtonBackgroundFadeOff.play();
    }

    public static void queuePlayHoverOn(StackPane playButtonWrapper){
        queuePlayButtonScaleOn = new ScaleTransition(Duration.millis(100), playButtonWrapper);
        queuePlayButtonScaleOn.setFromX(1);
        queuePlayButtonScaleOn.setToX(1.1);
        queuePlayButtonScaleOn.setFromY(1);
        queuePlayButtonScaleOn.setToY(1.1);
        queuePlayButtonScaleOn.play();
    }

    public static void queuePlayHoverOff(StackPane playButtonWrapper){
        queuePlayButtonScaleOff = new ScaleTransition(Duration.millis(100), playButtonWrapper);
        queuePlayButtonScaleOff.setFromX(1.1);
        queuePlayButtonScaleOff.setToX(1);
        queuePlayButtonScaleOff.setFromY(1.1);
        queuePlayButtonScaleOff.setToY(1);
        queuePlayButtonScaleOff.play();
    }
}
