package hans;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;

import java.util.Objects;

import static hans.SVG.*;

public class ActionIndicator {

    MainController mainController;
    StackPane background, wrapper;
    Region icon;
    SVGPath iconPath;

    ScaleTransition scaleTransition;
    FadeTransition fadeTransition1, fadeTransition2;
    ParallelTransition parallelTransition;

    ActionIndicator(MainController mainController){

        this.mainController = mainController;

        wrapper = new StackPane();
        wrapper.setBackground(Background.EMPTY);
        wrapper.setMouseTransparent(true);
        wrapper.setVisible(false);
        StackPane.setAlignment(wrapper, Pos.CENTER);

        background = new StackPane();
        background.setMinSize(100 * mainController.sizeMultiplier.doubleValue(),100 * mainController.sizeMultiplier.doubleValue());
        background.setPrefSize(100 * mainController.sizeMultiplier.doubleValue(),100 * mainController.sizeMultiplier.doubleValue());
        background.setMaxSize(100 * mainController.sizeMultiplier.doubleValue(),100 * mainController.sizeMultiplier.doubleValue());
        background.setEffect(new DropShadow());
        background.setOpacity(0.6);
        background.setStyle("-fx-background-color: black; -fx-background-radius: 500;");
        StackPane.setAlignment(background, Pos.CENTER);

        iconPath = new SVGPath();

        icon = new Region();
        icon.setMinSize(50 * mainController.sizeMultiplier.doubleValue(),50 * mainController.sizeMultiplier.doubleValue());
        icon.setPrefSize(50 * mainController.sizeMultiplier.doubleValue(),50 * mainController.sizeMultiplier.doubleValue());
        icon.setMaxSize(50 * mainController.sizeMultiplier.doubleValue(),50 * mainController.sizeMultiplier.doubleValue());
        icon.setStyle("-fx-background-color: white;");
        icon.setOpacity(0.9);
        StackPane.setAlignment(icon, Pos.CENTER);

        wrapper.getChildren().addAll(background, icon);

        mainController.videoImageViewInnerWrapper.getChildren().add(wrapper);

        scaleTransition = AnimationsClass.scaleAnimation(600, wrapper, 1, 1.5, 1, 1.5, false, 1, false);
        fadeTransition1 = AnimationsClass.fadeAnimation(600, background, 0.6, 0, false, 1, false);
        fadeTransition2 = AnimationsClass.fadeAnimation(600, icon, 0.9, 0, false, 1, false);
        parallelTransition = AnimationsClass.parallelAnimation(false, scaleTransition, fadeTransition1, fadeTransition2);
        parallelTransition.setOnFinished((e) -> {
            wrapper.setVisible(false);
            wrapper.setScaleX(1);
            wrapper.setScaleY(1);
            background.setOpacity(0.6);
            icon.setOpacity(1);
        });

    }

    public void setIcon(SVG svg){
        iconPath.setContent(svg.getContent());
        icon.setShape(iconPath);
        updateSize();
    }

    public void updateSize(){

        background.setMinSize(100 * mainController.sizeMultiplier.doubleValue(),100 * mainController.sizeMultiplier.doubleValue());
        background.setPrefSize(100 * mainController.sizeMultiplier.doubleValue(),100 * mainController.sizeMultiplier.doubleValue());
        background.setMaxSize(100 * mainController.sizeMultiplier.doubleValue(),100 * mainController.sizeMultiplier.doubleValue());

        if(Objects.equals(iconPath.getContent(), PLAY.getContent())){
            icon.setMinSize(30 * mainController.sizeMultiplier.doubleValue(), 30 * mainController.sizeMultiplier.doubleValue());
            icon.setPrefSize(30 * mainController.sizeMultiplier.doubleValue(), 30 * mainController.sizeMultiplier.doubleValue());
            icon.setMaxSize(30 * mainController.sizeMultiplier.doubleValue(), 30 * mainController.sizeMultiplier.doubleValue());
            icon.setTranslateX(icon.getWidth() / 10);
        }
        else if(Objects.equals(iconPath.getContent(), PAUSE.getContent()) || Objects.equals(iconPath.getContent(), VOLUME_MUTED.getContent()) || Objects.equals(iconPath.getContent(), VOLUME_HIGH.getContent()) || Objects.equals(iconPath.getContent(), PREVIOUS_VIDEO.getContent()) || Objects.equals(iconPath.getContent(), NEXT_VIDEO.getContent())){
            icon.setMinSize(30 * mainController.sizeMultiplier.doubleValue(), 30 * mainController.sizeMultiplier.doubleValue());
            icon.setPrefSize(30 * mainController.sizeMultiplier.doubleValue(), 30 * mainController.sizeMultiplier.doubleValue());
            icon.setMaxSize(30 * mainController.sizeMultiplier.doubleValue(), 30 * mainController.sizeMultiplier.doubleValue());
            icon.setTranslateX(0);
        }
        else if(Objects.equals(iconPath.getContent(), VOLUME_LOW.getContent())){
            icon.setMinSize(22 * mainController.sizeMultiplier.doubleValue(), 30 * mainController.sizeMultiplier.doubleValue());
            icon.setPrefSize(22 * mainController.sizeMultiplier.doubleValue(), 30 * mainController.sizeMultiplier.doubleValue());
            icon.setMaxSize(22 * mainController.sizeMultiplier.doubleValue(), 30 * mainController.sizeMultiplier.doubleValue());
            icon.setTranslateX(-4 * mainController.sizeMultiplier.doubleValue());
        }
        else if(Objects.equals(iconPath.getContent(), FORWARD.getContent()) || Objects.equals(iconPath.getContent(), REWIND.getContent())){
            icon.setMinSize(35 * mainController.sizeMultiplier.doubleValue(), 23 * mainController.sizeMultiplier.doubleValue());
            icon.setPrefSize(35 * mainController.sizeMultiplier.doubleValue(), 23 * mainController.sizeMultiplier.doubleValue());
            icon.setMaxSize(35 * mainController.sizeMultiplier.doubleValue(), 23 * mainController.sizeMultiplier.doubleValue());
            if(Objects.equals(iconPath.getContent(), FORWARD.getContent())) icon.setTranslateX(icon.getWidth() / 10);
            else icon.setTranslateX(-icon.getWidth() / 10);
        }
        else {
            icon.setMinSize(40 * mainController.sizeMultiplier.doubleValue(), 40 * mainController.sizeMultiplier.doubleValue());
            icon.setPrefSize(40 * mainController.sizeMultiplier.doubleValue(), 40 * mainController.sizeMultiplier.doubleValue());
            icon.setMaxSize(40 * mainController.sizeMultiplier.doubleValue(), 40 * mainController.sizeMultiplier.doubleValue());
            icon.setTranslateX(0);
        }
    }

    public void setVisible(boolean value){
        wrapper.setVisible(value);
    }

    public void animate(){
        if(parallelTransition.getStatus() == Animation.Status.RUNNING){
            parallelTransition.stop();
        }
        parallelTransition.playFromStart();


    }


    public void moveToMiniplayer(){
        mainController.videoImageViewInnerWrapper.getChildren().remove(wrapper);
        mainController.miniplayer.miniplayerController.videoImageViewInnerWrapper.getChildren().add(wrapper);
    }

    public void moveToMainplayer(){
        mainController.miniplayer.miniplayerController.videoImageViewInnerWrapper.getChildren().remove(wrapper);
        mainController.videoImageViewInnerWrapper.getChildren().add(wrapper);
    }

}
