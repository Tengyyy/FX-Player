package hans;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;

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
        icon.setOpacity(1);
        StackPane.setAlignment(icon, Pos.CENTER);

        wrapper.getChildren().addAll(background, icon);

        mainController.mediaViewInnerWrapper.getChildren().add(wrapper);

        scaleTransition = AnimationsClass.scaleAnimation(600, wrapper, 1, 1.5, 1, 1.5, false, 1, false);
        fadeTransition1 = AnimationsClass.fadeAnimation(600, background, 0.3, 0, false, 1, false);
        fadeTransition2 = AnimationsClass.fadeAnimation(600, icon, 0.8, 0, false, 1, false);
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
        iconPath.setContent(App.svgMap.get(svg));
        icon.setShape(iconPath);
        updateSize();
    }

    public void updateSize(){

        background.setMinSize(100 * mainController.sizeMultiplier.doubleValue(),100 * mainController.sizeMultiplier.doubleValue());
        background.setPrefSize(100 * mainController.sizeMultiplier.doubleValue(),100 * mainController.sizeMultiplier.doubleValue());
        background.setMaxSize(100 * mainController.sizeMultiplier.doubleValue(),100 * mainController.sizeMultiplier.doubleValue());

        if(iconPath.getContent() == App.svgMap.get(PLAY)){
            icon.setMinSize(30 * mainController.sizeMultiplier.doubleValue(), 30 * mainController.sizeMultiplier.doubleValue());
            icon.setPrefSize(30 * mainController.sizeMultiplier.doubleValue(), 30 * mainController.sizeMultiplier.doubleValue());
            icon.setMaxSize(30 * mainController.sizeMultiplier.doubleValue(), 30 * mainController.sizeMultiplier.doubleValue());
            icon.setTranslateX(icon.getWidth() / 10);
        }
        else if(iconPath.getContent() == App.svgMap.get(PAUSE) || iconPath.getContent() == App.svgMap.get(VOLUME_MUTED) || iconPath.getContent() == App.svgMap.get(VOLUME_HIGH)){
            icon.setMinSize(30 * mainController.sizeMultiplier.doubleValue(), 30 * mainController.sizeMultiplier.doubleValue());
            icon.setPrefSize(30 * mainController.sizeMultiplier.doubleValue(), 30 * mainController.sizeMultiplier.doubleValue());
            icon.setMaxSize(30 * mainController.sizeMultiplier.doubleValue(), 30 * mainController.sizeMultiplier.doubleValue());
            icon.setTranslateX(0);
        }
        else {
            System.out.println("test");
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

}
