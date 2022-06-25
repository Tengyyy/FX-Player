package hans;

import javafx.animation.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
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

    static TranslateTransition nextVideoNotificationOnTransition;
    static FadeTransition nextVideoNotificationOffTransition;


    static final double ANIMATION_SPEED = 200;


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

    public static void displayControls(ControlBarController controlBarController, CaptionsController captionsController) {

        TranslateTransition captionsTransition = new TranslateTransition(Duration.millis(100), captionsController.captionsBox);
        captionsTransition.setCycleCount(1);
        captionsTransition.setFromY(captionsController.captionsBox.getTranslateY());

        if(captionsController.captionsLocation == Pos.BOTTOM_CENTER || captionsController.captionsLocation == Pos.BOTTOM_LEFT || captionsController.captionsLocation == Pos.BOTTOM_RIGHT){
            captionsTransition.setToY(-120);
        }
        else {
            captionsTransition.setToY(captionsController.captionsBox.getTranslateY());
        }
        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(100), controlBarController.controlBarWrapper);
        translateTransition.setFromY(controlBarController.controlBarWrapper.getTranslateY());
        translateTransition.setToY(0);
        translateTransition.setCycleCount(1);


        ParallelTransition parallelTransition = new ParallelTransition(captionsTransition, translateTransition);
        parallelTransition.setInterpolator(Interpolator.LINEAR);
        parallelTransition.setOnFinished((e) -> {
            controlBarController.controlBarOpen = true;

        });

        parallelTransition.play();

        controlBarController.mainController.menuButton.setVisible(true);
    }

    public static void hideControls(ControlBarController controlBarController, CaptionsController captionsController) {

        TranslateTransition captionsTransition = new TranslateTransition(Duration.millis(100), captionsController.captionsBox);
        captionsTransition.setCycleCount(1);
        captionsTransition.setFromY(captionsController.captionsBox.getTranslateY());

        if(captionsController.captionsLocation == Pos.BOTTOM_CENTER || captionsController.captionsLocation == Pos.BOTTOM_LEFT || captionsController.captionsLocation == Pos.BOTTOM_RIGHT){
            captionsTransition.setToY(-50);
        }
        else {
            captionsTransition.setToY(captionsController.captionsBox.getTranslateY());
        }

        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(100), controlBarController.controlBarWrapper);
        translateTransition.setFromY(controlBarController.controlBarWrapper.getTranslateY());
        translateTransition.setToY(70);
        translateTransition.setCycleCount(1);

        ParallelTransition parallelTransition = new ParallelTransition(captionsTransition, translateTransition);
        parallelTransition.setInterpolator(Interpolator.LINEAR);
        parallelTransition.setOnFinished((e) -> {
            controlBarController.controlBarOpen = false;
            controlBarController.mouseEventTracker.mouseMoving.set(false);
        });
        parallelTransition.play();

        controlBarController.mainController.menuButton.setVisible(false);
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

    public static RotateTransition rotateTransition(double duration, Node node, double fromValue, double toValue, boolean autoReverse, int cycleCount, boolean play){
        RotateTransition rotateTransition = new RotateTransition();
        rotateTransition.setDuration(Duration.millis(duration));
        rotateTransition.setNode(node);
        rotateTransition.setFromAngle(fromValue);
        rotateTransition.setToAngle(toValue);
        rotateTransition.setAutoReverse(autoReverse);
        rotateTransition.setCycleCount(cycleCount);
        if(play) rotateTransition.play();

        return rotateTransition;
    }

    public static ParallelTransition parallelAnimation(boolean play, Transition... transitions){
        ParallelTransition parallelTransition = new ParallelTransition();
        for(Transition transition :  transitions){
            parallelTransition.getChildren().add(transition);
        }

        if(play) parallelTransition.play();

        return parallelTransition;
    }

    public static SequentialTransition sequentialAnimation(boolean play, Transition... transitions){
        SequentialTransition sequentialTransition = new SequentialTransition();
        for(Transition transition :  transitions){
            sequentialTransition.getChildren().add(transition);
        }

        if(play) sequentialTransition.play();

        return sequentialTransition;
    }


    public static void openMenuNotification(MenuController menuController){

        menuController.menuNotificationOpen = true;

        if(nextVideoNotificationOffTransition != null && nextVideoNotificationOffTransition.getStatus() == Animation.Status.RUNNING){
            nextVideoNotificationOffTransition.stop();
        }

        menuController.notificationPane.setOpacity(1);

        nextVideoNotificationOnTransition = new TranslateTransition(Duration.millis(300), menuController.notificationPane);
        nextVideoNotificationOnTransition.setFromY(80);
        nextVideoNotificationOnTransition.setToY(0);
        nextVideoNotificationOnTransition.setCycleCount(1);
        nextVideoNotificationOnTransition.setInterpolator(Interpolator.EASE_OUT);
        nextVideoNotificationOnTransition.setOnFinished((e) -> {
            menuController.closeTimer.playFromStart();
        });
        nextVideoNotificationOnTransition.playFromStart();

    }

    public static void closeMenuNotification(MenuController menuController){
        menuController.menuNotificationOpen = false;
        nextVideoNotificationOffTransition = new FadeTransition(Duration.millis(400), menuController.notificationPane);
        nextVideoNotificationOffTransition.setFromValue(1);
        nextVideoNotificationOffTransition.setToValue(0);
        nextVideoNotificationOnTransition.setCycleCount(1);
        nextVideoNotificationOnTransition.setInterpolator(Interpolator.EASE_OUT);
        nextVideoNotificationOffTransition.setOnFinished((e) -> {
            menuController.notificationPane.setTranslateY(80);
        });
        nextVideoNotificationOffTransition.playFromStart();
    }


    public static void openMenu(MainController mainController, MenuController menuController){
        Timeline timeline = new Timeline();

        timeline.setCycleCount(1);
        timeline.setAutoReverse(false);
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(300), new KeyValue(menuController.menu.prefWidthProperty(),Double.min(menuController.prefWidth, menuController.menu.getMaxWidth()), Interpolator.EASE_OUT)));
        timeline.play();
        timeline.setOnFinished((e) -> {
            menuController.menu.setMinWidth(350);
            menuController.prefWidth = menuController.menu.getWidth();
            menuController.menu.setMouseTransparent(false);
            menuController.menuInTransition = false;
        });

    }

    public static void closeMenu(MainController mainController, MenuController menuController) {

        Timeline timeline = new Timeline();

        timeline.setCycleCount(1);
        timeline.setAutoReverse(false);
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(300), new KeyValue(menuController.menu.prefWidthProperty(), 0, Interpolator.EASE_OUT)));
        timeline.play();
        timeline.setOnFinished((e) -> {
            menuController.menuInTransition = false;
        });
    }

    public static FadeTransition fadeIn(Node child){
        Duration animationDuration = Duration.millis(ANIMATION_SPEED);
        FadeTransition fadeTransition = new FadeTransition(animationDuration, child);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        return fadeTransition;
    }

    public static FadeTransition fadeOut(Node child){
        Duration animationDuration = Duration.millis(ANIMATION_SPEED);
        FadeTransition fadeTransition = new FadeTransition(animationDuration, child);
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0);
        return fadeTransition;
    }

    public static TranslateTransition animateUp(Node child, double translate){
        Duration animationDuration = Duration.millis(ANIMATION_SPEED);
        TranslateTransition translateTransition = new TranslateTransition(animationDuration, child);
        translateTransition.setFromY(0);
        translateTransition.setToY(-translate);
        return translateTransition;
    }

    public static TranslateTransition animateDown(Node child, double translate){
        Duration animationDuration = Duration.millis(ANIMATION_SPEED);
        TranslateTransition translateTransition = new TranslateTransition(animationDuration, child);
        translateTransition.setFromY(0);
        translateTransition.setToY(translate);
        return translateTransition;
    }


    public static Timeline animateMinHeight(double newHeight, Region region){
        Duration animationDuration = Duration.millis(ANIMATION_SPEED);
        Timeline minTimeline = new Timeline(new KeyFrame(animationDuration,
                new KeyValue(region.minHeightProperty(),newHeight, Interpolator.EASE_BOTH)));
        return minTimeline;
    }

    public static Timeline animateMaxHeight(double newHeight, Region region){
        Duration animationDuration = Duration.millis(ANIMATION_SPEED);
        Timeline maxTimeline = new Timeline(new KeyFrame(animationDuration,
                new KeyValue(region.maxHeightProperty(),newHeight, Interpolator.EASE_BOTH)));
        return maxTimeline;
    }


    public static void AnimateBackgroundColor(Region icon, Color fromColor,Color toColor,int duration)
    {

        Rectangle rect = new Rectangle();
        rect.setFill(fromColor);

        FillTransition tr = new FillTransition();
        tr.setShape(rect);
        tr.setDuration(Duration.millis(duration));
        tr.setFromValue(fromColor);
        tr.setToValue(toColor);

        tr.setInterpolator(new Interpolator() {
            @Override
            protected double curve(double t) {
                icon.setBackground(new Background(new BackgroundFill(rect.getFill(), CornerRadii.EMPTY, Insets.EMPTY)));
                return t;
            }
        });

        tr.play();
    }
}
