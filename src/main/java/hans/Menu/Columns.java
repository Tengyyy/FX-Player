package hans.Menu;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class Columns extends StackPane {

    // three white columns, hbox spacing a few px, hbox margin bottom around 10 px

    ParallelTransition parallelTransition;
    Timeline column1Animation = new Timeline();
    Timeline column2Animation = new Timeline();
    Timeline column3Animation = new Timeline();

    Pane column1, column2, column3;

    static final double ANIMATION_SPEED = 200;

    Columns(){
        this.setMinSize(18, 20);
        this.setPrefSize(18, 20);
        this.setMaxSize(18, 20);
        this.setBackground(Background.EMPTY);


        column1 = new StackPane();
        column1.setMinSize(4, 2);
        column1.setMaxSize(4, 2);
        column1.getStyleClass().add("column");
        StackPane.setAlignment(column1, Pos.BOTTOM_LEFT);

        column2 = new StackPane();
        column2.setMinSize(4, 2);
        column2.setMaxSize(4, 2);
        column2.getStyleClass().add("column");
        StackPane.setAlignment(column2, Pos.BOTTOM_CENTER);


        column3 = new StackPane();
        column3.setMinSize(4, 2);
        column3.setMaxSize(4, 2);
        column3.getStyleClass().add("column");
        StackPane.setAlignment(column3, Pos.BOTTOM_RIGHT);



        this.getChildren().addAll(column1, column2, column3);


        column1Animation = new Timeline(
                new KeyFrame(Duration.millis(150), new KeyValue(column1.maxHeightProperty(),12, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.millis(250), new KeyValue(column1.maxHeightProperty(),3, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.millis(600), new KeyValue(column1.maxHeightProperty(),18, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.millis(850), new KeyValue(column1.maxHeightProperty(),6, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.millis(1200), new KeyValue(column1.maxHeightProperty(),15, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.millis(1450), new KeyValue(column1.maxHeightProperty(),2, Interpolator.EASE_BOTH))
                );


        column2Animation = new Timeline(
                new KeyFrame(Duration.millis(250), new KeyValue(column2.maxHeightProperty(),6, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.millis(400), new KeyValue(column2.maxHeightProperty(),3, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.millis(750), new KeyValue(column2.maxHeightProperty(),14, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.millis(900), new KeyValue(column2.maxHeightProperty(),10, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.millis(1150), new KeyValue(column2.maxHeightProperty(),19, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.millis(1450), new KeyValue(column2.maxHeightProperty(),8, Interpolator.EASE_BOTH))
        );

        column3Animation = new Timeline(
                new KeyFrame(Duration.millis(450), new KeyValue(column3.maxHeightProperty(),18, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.millis(700), new KeyValue(column3.maxHeightProperty(),10, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.millis(850), new KeyValue(column3.maxHeightProperty(),12, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.millis(1000), new KeyValue(column3.maxHeightProperty(),7, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.millis(1250), new KeyValue(column3.maxHeightProperty(),12, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.millis(1450), new KeyValue(column3.maxHeightProperty(),4, Interpolator.EASE_BOTH))
        );


    }


    public void play(){

        if(parallelTransition != null && parallelTransition.getStatus() == Animation.Status.RUNNING) parallelTransition.stop();

        parallelTransition = new ParallelTransition(column1Animation, column2Animation, column3Animation);
        parallelTransition.setCycleCount(Animation.INDEFINITE);
        parallelTransition.setAutoReverse(true);
        parallelTransition.playFromStart();
    }


    public void pause(){
        // pause the animation and reset the height of all columns to like 2-3 px
        if(parallelTransition != null && parallelTransition.getStatus() == Animation.Status.RUNNING) parallelTransition.stop();

        column1.setMinSize(4, 2);
        column1.setMaxSize(4, 2);

        column2.setMinSize(4, 2);
        column2.setMaxSize(4, 2);

        column3.setMinSize(4, 2);
        column3.setMaxSize(4, 2);
    }
}
