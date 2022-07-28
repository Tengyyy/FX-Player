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
            if(forward) wrapper.translateXProperty().bind(mainController.videoImageViewInnerWrapper.widthProperty().divide(4));
            else wrapper.translateXProperty().bind(mainController.videoImageViewInnerWrapper.widthProperty().divide(4).multiply(-1));
        });


        StackPane.setAlignment(wrapper, Pos.CENTER);

        background = new StackPane();
        background.setMinSize(150 * mainController.sizeMultiplier.doubleValue(),150 * mainController.sizeMultiplier.doubleValue());
        background.setPrefSize(150 * mainController.sizeMultiplier.doubleValue(),150 * mainController.sizeMultiplier.doubleValue());
        background.setMaxSize(150 * mainController.sizeMultiplier.doubleValue(),150 * mainController.sizeMultiplier.doubleValue());
        background.setEffect(new DropShadow());
        background.setOpacity(0.6);
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
        arrow1.setOpacity(0.2);
        StackPane.setAlignment(arrow1, Pos.CENTER_LEFT);

        arrow2 = new Region();
        arrow2.setMinSize(15 * mainController.sizeMultiplier.doubleValue(), 20 * mainController.sizeMultiplier.doubleValue());
        arrow2.setPrefSize(15 * mainController.sizeMultiplier.doubleValue(),20 * mainController.sizeMultiplier.doubleValue());
        arrow2.setMaxSize(15 * mainController.sizeMultiplier.doubleValue(),20 * mainController.sizeMultiplier.doubleValue());
        arrow2.setStyle("-fx-background-color: white;");
        arrow2.setShape(iconPath);
        arrow2.setOpacity(0.2);
        StackPane.setAlignment(arrow2, Pos.CENTER);

        arrow3 = new Region();
        arrow3.setMinSize(15 * mainController.sizeMultiplier.doubleValue(),20 * mainController.sizeMultiplier.doubleValue());
        arrow3.setPrefSize(15 * mainController.sizeMultiplier.doubleValue(),20* mainController.sizeMultiplier.doubleValue());
        arrow3.setMaxSize(15 * mainController.sizeMultiplier.doubleValue(),20 * mainController.sizeMultiplier.doubleValue());
        arrow3.setStyle("-fx-background-color: white;");
        arrow3.setShape(iconPath);
        arrow3.setOpacity(0.2);
        StackPane.setAlignment(arrow3, Pos.CENTER_RIGHT);

        arrowContainer = new StackPane();
        arrowContainer.setMinSize(73 * mainController.sizeMultiplier.doubleValue(),20 * mainController.sizeMultiplier.doubleValue());
        arrowContainer.setPrefSize(73 * mainController.sizeMultiplier.doubleValue(),20 * mainController.sizeMultiplier.doubleValue());
        arrowContainer.setMaxSize(73 * mainController.sizeMultiplier.doubleValue(),20 * mainController.sizeMultiplier.doubleValue());
        arrowContainer.setTranslateY(-16 * mainController.sizeMultiplier.doubleValue());
        if(!forward) arrowContainer.setRotate(180);
        arrowContainer.getChildren().addAll(arrow1, arrow2, arrow3);
        StackPane.setAlignment(arrowContainer, Pos.CENTER);

        time = new Label();
        time.setStyle("-fx-font-family: \"Roboto\"; -fx-font-size: 12; -fx-text-fill: #cfcfcf;");
        time.setTranslateY(24 * mainController.sizeMultiplier.doubleValue());
        StackPane.setAlignment(time, Pos.CENTER);


        wrapper.getChildren().addAll(background, arrowContainer, time);

        arrow1on = AnimationsClass.fadeAnimation(300, arrow1, 0.2, 1, false, 1, false);
        arrow1off = AnimationsClass.fadeAnimation(300, arrow1, 1, 0.2, false, 1, false);

        arrow2on = AnimationsClass.fadeAnimation(300, arrow2, 0.2, 1, false, 1, false);
        arrow2off = AnimationsClass.fadeAnimation(300, arrow2, 1, 0.2, false, 1, false);

        arrow3on = AnimationsClass.fadeAnimation(300, arrow3, 0.2, 1, false, 1, false);

        arrows1and2 = AnimationsClass.parallelAnimation(false, arrow1off, arrow2on);
        arrows2and3 = AnimationsClass.parallelAnimation(false, arrow2off, arrow3on);

        animation = AnimationsClass.sequentialAnimation(false, arrow1on, arrows1and2, arrows2and3);
        animation.setOnFinished((e) -> {
            arrow1.setOpacity(0.2);
            arrow2.setOpacity(0.2);
            arrow3.setOpacity(0.2);
            wrapper.setVisible(false);
        });

        mainController.videoImageViewInnerWrapper.getChildren().add(wrapper);

    }

    public void setText(String text){
        time.setText(text);
    }

    public void setVisible(boolean visible){
        wrapper.setVisible(visible);
    }

    public void reset(){
        if(animation.getStatus() == Animation.Status.RUNNING) animation.stop();
        arrow1.setOpacity(0.2);
        arrow2.setOpacity(0.2);
        arrow3.setOpacity(0.2);
    }

    public void animate(){
        animation.playFromStart();
    }

    public void resize(){

        double miniplayerMultiplier = 1;
        if(mainController.miniplayerActive) miniplayerMultiplier = 1.35;
        else miniplayerMultiplier = 1;

        background.setMinSize(150 * mainController.sizeMultiplier.doubleValue(),150 * mainController.sizeMultiplier.doubleValue());
        background.setPrefSize(150 * mainController.sizeMultiplier.doubleValue(),150 * mainController.sizeMultiplier.doubleValue());
        background.setMaxSize(150 * mainController.sizeMultiplier.doubleValue(),150 * mainController.sizeMultiplier.doubleValue());

        arrow1.setMinSize(15 * mainController.sizeMultiplier.doubleValue() * miniplayerMultiplier,20 * mainController.sizeMultiplier.doubleValue() * miniplayerMultiplier);
        arrow1.setPrefSize(15 * mainController.sizeMultiplier.doubleValue() * miniplayerMultiplier,20 * mainController.sizeMultiplier.doubleValue() * miniplayerMultiplier);
        arrow1.setMaxSize(15 * mainController.sizeMultiplier.doubleValue() * miniplayerMultiplier,20 * mainController.sizeMultiplier.doubleValue() * miniplayerMultiplier);

        arrow2.setMinSize(15 * mainController.sizeMultiplier.doubleValue() * miniplayerMultiplier,20 * mainController.sizeMultiplier.doubleValue() * miniplayerMultiplier);
        arrow2.setPrefSize(15 * mainController.sizeMultiplier.doubleValue() * miniplayerMultiplier,20 * mainController.sizeMultiplier.doubleValue() * miniplayerMultiplier);
        arrow2.setMaxSize(15 * mainController.sizeMultiplier.doubleValue() * miniplayerMultiplier,20 * mainController.sizeMultiplier.doubleValue() * miniplayerMultiplier);

        arrow3.setMinSize(15 * mainController.sizeMultiplier.doubleValue() * miniplayerMultiplier,20 * mainController.sizeMultiplier.doubleValue() * miniplayerMultiplier);
        arrow3.setPrefSize(15 * mainController.sizeMultiplier.doubleValue() * miniplayerMultiplier,20 * mainController.sizeMultiplier.doubleValue() * miniplayerMultiplier);
        arrow3.setMaxSize(15 * mainController.sizeMultiplier.doubleValue() * miniplayerMultiplier,20 * mainController.sizeMultiplier.doubleValue() * miniplayerMultiplier);

        arrowContainer.setMinSize(73 * mainController.sizeMultiplier.doubleValue() * miniplayerMultiplier,20 * mainController.sizeMultiplier.doubleValue() * miniplayerMultiplier);
        arrowContainer.setPrefSize(73 * mainController.sizeMultiplier.doubleValue() * miniplayerMultiplier,20 * mainController.sizeMultiplier.doubleValue() * miniplayerMultiplier);
        arrowContainer.setMaxSize(73 * mainController.sizeMultiplier.doubleValue() * miniplayerMultiplier,20 * mainController.sizeMultiplier.doubleValue() * miniplayerMultiplier);
        if(!mainController.miniplayerActive) arrowContainer.setTranslateY(-16 * mainController.sizeMultiplier.doubleValue());
        else arrowContainer.setTranslateY(0);


        if(mainController.sizeMultiplier.doubleValue() == 0.55){
            time.setStyle("-fx-font-family: \"Roboto\"; -fx-font-size: 12; -fx-text-fill: #cfcfcf;");
        }
        else if(mainController.sizeMultiplier.doubleValue() == 0.65){
            time.setStyle("-fx-font-family: \"Roboto\"; -fx-font-size: 14; -fx-text-fill: #cfcfcf;");
        }
        else if(mainController.sizeMultiplier.doubleValue() == 0.8){
            time.setStyle("-fx-font-family: \"Roboto\"; -fx-font-size: 15; -fx-text-fill: #cfcfcf;");
        }
        else if(mainController.sizeMultiplier.doubleValue() == 1){
            time.setStyle("-fx-font-family: \"Roboto\"; -fx-font-size: 17; -fx-text-fill: #cfcfcf;");
        }
        else if(mainController.sizeMultiplier.doubleValue() == 1.2){
            time.setStyle("-fx-font-family: \"Roboto\"; -fx-font-size: 20; -fx-text-fill: #cfcfcf;");
        }

        time.setTranslateY(24 * mainController.sizeMultiplier.doubleValue());

    }

    public void moveToMiniplayer(){
        mainController.videoImageViewInnerWrapper.getChildren().remove(wrapper);
        mainController.miniplayer.miniplayerController.videoImageViewInnerWrapper.getChildren().add(wrapper);

        time.setVisible(false);

        wrapper.translateXProperty().unbind();

        Platform.runLater(() -> {
            if(forward) wrapper.translateXProperty().bind(mainController.miniplayer.miniplayerController.videoImageViewInnerWrapper.widthProperty().divide(4));
            else wrapper.translateXProperty().bind(mainController.miniplayer.miniplayerController.videoImageViewInnerWrapper.widthProperty().divide(4).multiply(-1));
        });
    }

    public void moveToMainplayer(){
        mainController.miniplayer.miniplayerController.videoImageViewInnerWrapper.getChildren().remove(wrapper);
        mainController.videoImageViewInnerWrapper.getChildren().add(wrapper);

        time.setVisible(true);

        wrapper.translateXProperty().unbind();

        Platform.runLater(() -> {
            if(forward) wrapper.translateXProperty().bind(mainController.videoImageViewInnerWrapper.widthProperty().divide(4));
            else wrapper.translateXProperty().bind(mainController.videoImageViewInnerWrapper.widthProperty().divide(4).multiply(-1));
        });
    }
}
