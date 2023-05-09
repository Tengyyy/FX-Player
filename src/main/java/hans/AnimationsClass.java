package hans;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.List;

public class AnimationsClass {

    public static final double ANIMATION_SPEED = 200;

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

    public static void rotateTransition(double duration, Node node, double fromValue, double toValue, boolean autoReverse, int cycleCount, boolean play){
        RotateTransition rotateTransition = new RotateTransition();
        rotateTransition.setDuration(Duration.millis(duration));
        rotateTransition.setNode(node);
        rotateTransition.setFromAngle(fromValue);
        rotateTransition.setToAngle(toValue);
        rotateTransition.setAutoReverse(autoReverse);
        rotateTransition.setCycleCount(cycleCount);
        if(play) rotateTransition.play();

    }

    public static void parallelAnimation(boolean play, List<Transition> transitions){
        ParallelTransition parallelTransition = new ParallelTransition();
        for(Transition transition :  transitions){
            if(transition != null) parallelTransition.getChildren().add(transition);
        }

        if(play) parallelTransition.play();

    }

    public static ParallelTransition parallelAnimation(boolean play, Transition... transitions){
        ParallelTransition parallelTransition = new ParallelTransition();
        for(Transition transition :  transitions){
            if(transition != null) parallelTransition.getChildren().add(transition);
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

    public static FadeTransition fadeIn(Node child){
        Duration animationDuration = Duration.millis(ANIMATION_SPEED);
        FadeTransition fadeTransition = new FadeTransition(animationDuration, child);
        fadeTransition.setFromValue(child.getOpacity());
        fadeTransition.setToValue(1);
        return fadeTransition;
    }

    public static FadeTransition fadeOut(Node child){
        Duration animationDuration = Duration.millis(ANIMATION_SPEED);
        FadeTransition fadeTransition = new FadeTransition(animationDuration, child);
        fadeTransition.setFromValue(child.getOpacity());
        fadeTransition.setToValue(0);
        return fadeTransition;
    }

    public static Timeline animateMinHeight(double newHeight, Region region){
        Duration animationDuration = Duration.millis(ANIMATION_SPEED);
        return new Timeline(new KeyFrame(animationDuration,
                new KeyValue(region.minHeightProperty(),newHeight, Interpolator.EASE_BOTH)));
    }

    public static Timeline animateMaxHeight(double newHeight, Region region){
        Duration animationDuration = Duration.millis(ANIMATION_SPEED);
        return new Timeline(new KeyFrame(animationDuration,
                new KeyValue(region.maxHeightProperty(),newHeight, Interpolator.EASE_BOTH)));
    }


    public static void animateBackgroundColor(Region icon, Color fromColor, Color toColor, int duration) {

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



    public static void animateTextColor(Label label, Color toColor, int duration) {
        Duration animationDuration = Duration.millis(duration);
        Timeline timeline = new Timeline(new KeyFrame(animationDuration,
                new KeyValue(label.textFillProperty(), toColor, Interpolator.LINEAR)));

        timeline.play();

    }
}
