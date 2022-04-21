package hans;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

public class SeekIndicator {

    MainController mainController;
    Region arrow1, arrow2, arrow3;
    StackPane wrapper, background, arrowContainer;
    SVGPath iconPath;
    Label time;
    boolean forward; // false = seek-backwards-indicator, true = seek-forwards-indicator

    FadeTransition arrow1on, arrow1off, arrow2on, arrow2off, arrow3on;
    ParallelTransition arrows1and2, arrows2and3;
    SequentialTransition animation;


    SeekIndicator(MainController mainController, boolean forward){
        this.mainController = mainController;
        this.forward = forward;

        wrapper = new StackPane();
        wrapper.setBackground(Background.EMPTY);
        wrapper.setMouseTransparent(true);
        wrapper.setVisible(false);

        Platform.runLater(() -> {
            if(forward) wrapper.translateXProperty().bind(mainController.mediaViewInnerWrapper.widthProperty().divide(4));
            else wrapper.translateXProperty().bind(mainController.mediaViewInnerWrapper.widthProperty().divide(4).multiply(-1));
        });


        StackPane.setAlignment(wrapper, Pos.CENTER);

        background = new StackPane();
        background.setMinSize(150 * mainController.sizeMultiplier.doubleValue(),150 * mainController.sizeMultiplier.doubleValue());
        background.setPrefSize(150 * mainController.sizeMultiplier.doubleValue(),150 * mainController.sizeMultiplier.doubleValue());
        background.setMaxSize(150 * mainController.sizeMultiplier.doubleValue(),150 * mainController.sizeMultiplier.doubleValue());
        background.setEffect(new DropShadow());
        background.setOpacity(0.3);
        background.setStyle("-fx-background-color: black; -fx-background-radius: 500;");
        StackPane.setAlignment(background, Pos.CENTER);

        iconPath = new SVGPath();
        iconPath.setContent(App.svgMap.get(SVG.PLAY));

        arrow1 = new Region();
        arrow1.setMinSize(15 * mainController.sizeMultiplier.doubleValue(),20 * mainController.sizeMultiplier.doubleValue());
        arrow1.setPrefSize(15 * mainController.sizeMultiplier.doubleValue(),20 * mainController.sizeMultiplier.doubleValue());
        arrow1.setMaxSize(15 * mainController.sizeMultiplier.doubleValue(),20 * mainController.sizeMultiplier.doubleValue());
        arrow1.setStyle("-fx-background-color: white;");
        arrow1.setShape(iconPath);
        arrow1.setOpacity(0.6);
        StackPane.setAlignment(arrow1, Pos.CENTER_LEFT);

        arrow2 = new Region();
        arrow2.setMinSize(15 * mainController.sizeMultiplier.doubleValue(), 20 * mainController.sizeMultiplier.doubleValue());
        arrow2.setPrefSize(15 * mainController.sizeMultiplier.doubleValue(),20 * mainController.sizeMultiplier.doubleValue());
        arrow2.setMaxSize(15 * mainController.sizeMultiplier.doubleValue(),20 * mainController.sizeMultiplier.doubleValue());
        arrow2.setStyle("-fx-background-color: white;");
        arrow2.setShape(iconPath);
        arrow2.setOpacity(0.6);
        StackPane.setAlignment(arrow2, Pos.CENTER);

        arrow3 = new Region();
        arrow3.setMinSize(15 * mainController.sizeMultiplier.doubleValue(),20 * mainController.sizeMultiplier.doubleValue());
        arrow3.setPrefSize(15 * mainController.sizeMultiplier.doubleValue(),20* mainController.sizeMultiplier.doubleValue());
        arrow3.setMaxSize(15 * mainController.sizeMultiplier.doubleValue(),20 * mainController.sizeMultiplier.doubleValue());
        arrow3.setStyle("-fx-background-color: white;");
        arrow3.setShape(iconPath);
        arrow3.setOpacity(0.6);
        StackPane.setAlignment(arrow3, Pos.CENTER_RIGHT);

        arrowContainer = new StackPane();
        arrowContainer.setMinSize(73 * mainController.sizeMultiplier.doubleValue(),20 * mainController.sizeMultiplier.doubleValue());
        arrowContainer.setPrefSize(73 * mainController.sizeMultiplier.doubleValue(),20 * mainController.sizeMultiplier.doubleValue());
        arrowContainer.setMaxSize(73 * mainController.sizeMultiplier.doubleValue(),20 * mainController.sizeMultiplier.doubleValue());
        arrowContainer.setTranslateY(-14 * mainController.sizeMultiplier.doubleValue());
        if(!forward) arrowContainer.setRotate(180);
        arrowContainer.getChildren().addAll(arrow1, arrow2, arrow3);
        StackPane.setAlignment(arrowContainer, Pos.CENTER);

        time = new Label();
        time.setStyle("-fx-font-family: \"Roboto\"; -fx-font-size: 13; -fx-text-fill: #cfcfcf;");
        time.setTranslateY(21 * mainController.sizeMultiplier.doubleValue());
        StackPane.setAlignment(time, Pos.CENTER);


        wrapper.getChildren().addAll(background, arrowContainer, time);

        arrow1on = AnimationsClass.fadeAnimation(300, arrow1, 0.6, 1, false, 1, false);
        arrow1off = AnimationsClass.fadeAnimation(300, arrow1, 1, 0.6, false, 1, false);

        arrow2on = AnimationsClass.fadeAnimation(300, arrow2, 0.6, 1, false, 1, false);
        arrow2off = AnimationsClass.fadeAnimation(300, arrow2, 1, 0.6, false, 1, false);

        arrow3on = AnimationsClass.fadeAnimation(300, arrow3, 0.6, 1, false, 1, false);

        arrows1and2 = AnimationsClass.parallelAnimation(false, arrow1off, arrow2on);
        arrows2and3 = AnimationsClass.parallelAnimation(false, arrow2off, arrow3on);

        animation = AnimationsClass.sequentialAnimation(false, arrow1on, arrows1and2, arrows2and3);
        animation.setOnFinished((e) -> {
            arrow1.setOpacity(0.6);
            arrow2.setOpacity(0.6);
            arrow3.setOpacity(0.6);
            wrapper.setVisible(false);
        });

        mainController.mediaViewInnerWrapper.getChildren().add(wrapper);

    }

    public void setText(String text){
        time.setText(text);
    }

    public void setVisible(boolean visible){
        wrapper.setVisible(visible);
    }

    public void reset(){
        if(animation.getStatus() == Animation.Status.RUNNING) animation.stop();
        arrow1.setOpacity(0.6);
        arrow2.setOpacity(0.6);
        arrow3.setOpacity(0.6);
    }

    public void animate(){
        animation.playFromStart();
    }

    public void resize(){
        background.setMinSize(150 * mainController.sizeMultiplier.doubleValue(),150 * mainController.sizeMultiplier.doubleValue());
        background.setPrefSize(150 * mainController.sizeMultiplier.doubleValue(),150 * mainController.sizeMultiplier.doubleValue());
        background.setMaxSize(150 * mainController.sizeMultiplier.doubleValue(),150 * mainController.sizeMultiplier.doubleValue());

        arrow1.setMinSize(15 * mainController.sizeMultiplier.doubleValue(),20 * mainController.sizeMultiplier.doubleValue());
        arrow1.setPrefSize(15 * mainController.sizeMultiplier.doubleValue(),20 * mainController.sizeMultiplier.doubleValue());
        arrow1.setMaxSize(15 * mainController.sizeMultiplier.doubleValue(),20 * mainController.sizeMultiplier.doubleValue());

        arrow2.setMinSize(15 * mainController.sizeMultiplier.doubleValue(),20 * mainController.sizeMultiplier.doubleValue());
        arrow2.setPrefSize(15 * mainController.sizeMultiplier.doubleValue(),20 * mainController.sizeMultiplier.doubleValue());
        arrow2.setMaxSize(15 * mainController.sizeMultiplier.doubleValue(),20 * mainController.sizeMultiplier.doubleValue());

        arrow3.setMinSize(15 * mainController.sizeMultiplier.doubleValue(),20 * mainController.sizeMultiplier.doubleValue());
        arrow3.setPrefSize(15 * mainController.sizeMultiplier.doubleValue(),20 * mainController.sizeMultiplier.doubleValue());
        arrow3.setMaxSize(15 * mainController.sizeMultiplier.doubleValue(),20 * mainController.sizeMultiplier.doubleValue());

        arrowContainer.setMinSize(73 * mainController.sizeMultiplier.doubleValue(),20 * mainController.sizeMultiplier.doubleValue());
        arrowContainer.setPrefSize(73 * mainController.sizeMultiplier.doubleValue(),20 * mainController.sizeMultiplier.doubleValue());
        arrowContainer.setMaxSize(73 * mainController.sizeMultiplier.doubleValue(),20 * mainController.sizeMultiplier.doubleValue());
        arrowContainer.setTranslateY(-14 * mainController.sizeMultiplier.doubleValue());

        if(mainController.sizeMultiplier.doubleValue() == 0.7) time.setStyle("-fx-font-family: \"Roboto\"; -fx-font-size: 13; -fx-text-fill: #cfcfcf;");
        else time.setStyle("-fx-font-family: \"Roboto\"; -fx-font-size: 16; -fx-text-fill: #cfcfcf;");
        time.setTranslateY(21 * mainController.sizeMultiplier.doubleValue());

    }
}
