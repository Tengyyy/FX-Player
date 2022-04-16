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
    StackPane background;
    StackPane wrapper;
    Region icon;
    SVGPath iconPath;

    ScaleTransition scaleTransition;
    FadeTransition fadeTransition1, fadeTransition2;
    ParallelTransition parallelTransition;

    SimpleDoubleProperty sizeMultiplier = new SimpleDoubleProperty();

    ActionIndicator(MainController mainController){

        this.mainController = mainController;

        wrapper = new StackPane();
        wrapper.setBackground(Background.EMPTY);
        wrapper.setMouseTransparent(true);
        wrapper.setVisible(false);
        StackPane.setAlignment(wrapper, Pos.CENTER);

        sizeMultiplier.set(0.7);

        Platform.runLater(() -> {
            mainController.mediaViewWrapper.widthProperty().addListener((observableValue, oldValue, newValue) -> {
                if(oldValue.doubleValue() < 1200 && newValue.doubleValue() >= 1200){
                    sizeMultiplier.set(1);
                    if(wrapper.isVisible()) updateSize();
                }
                else if(oldValue.doubleValue() >= 1200 & newValue.doubleValue() < 1200){
                    sizeMultiplier.set(0.7);
                    if(wrapper.isVisible()) updateSize();
                }
            });
        });

        background = new StackPane();
        background.setMinSize(100 * sizeMultiplier.doubleValue(),100 * sizeMultiplier.doubleValue());
        background.setPrefSize(100 * sizeMultiplier.doubleValue(),100 * sizeMultiplier.doubleValue());
        background.setMaxSize(100 * sizeMultiplier.doubleValue(),100 * sizeMultiplier.doubleValue());
        background.setEffect(new DropShadow());
        background.setOpacity(0.6);
        background.setStyle("-fx-background-color: black; -fx-background-radius: 500;");
        StackPane.setAlignment(background, Pos.CENTER);

        iconPath = new SVGPath();

        icon = new Region();
        icon.setMinSize(50 * sizeMultiplier.doubleValue(),50 * sizeMultiplier.doubleValue());
        icon.setPrefSize(50 * sizeMultiplier.doubleValue(),50 * sizeMultiplier.doubleValue());
        icon.setMaxSize(50 * sizeMultiplier.doubleValue(),50 * sizeMultiplier.doubleValue());
        icon.setStyle("-fx-background-color: white;");
        icon.setOpacity(1);
        StackPane.setAlignment(icon, Pos.CENTER);

        wrapper.getChildren().addAll(background, icon);

        mainController.mediaViewWrapper.getChildren().add(wrapper);

        scaleTransition = AnimationsClass.scaleAnimation(800, wrapper, 1, 1.5, 1, 1.5, false, 1, false);
        fadeTransition1 = AnimationsClass.fadeAnimation(800, background, 0.6, 0, false, 1, false);
        fadeTransition2 = AnimationsClass.fadeAnimation(800, icon, 1, 0, false, 1, false);
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

        background.setMinSize(100 * sizeMultiplier.doubleValue(),100 * sizeMultiplier.doubleValue());
        background.setPrefSize(100 * sizeMultiplier.doubleValue(),100 * sizeMultiplier.doubleValue());
        background.setMaxSize(100 * sizeMultiplier.doubleValue(),100 * sizeMultiplier.doubleValue());

        if(iconPath.getContent() == App.svgMap.get(PLAY)){
            icon.setMinSize(30 * sizeMultiplier.doubleValue(), 30 * sizeMultiplier.doubleValue());
            icon.setPrefSize(30 * sizeMultiplier.doubleValue(), 30 * sizeMultiplier.doubleValue());
            icon.setMaxSize(30 * sizeMultiplier.doubleValue(), 30 * sizeMultiplier.doubleValue());
            icon.setTranslateX(icon.getWidth() / 10);
        }
        else if(iconPath.getContent() == App.svgMap.get(PAUSE) || iconPath.getContent() == App.svgMap.get(VOLUME_MUTED) || iconPath.getContent() == App.svgMap.get(VOLUME_HIGH)){
            icon.setMinSize(30 * sizeMultiplier.doubleValue(), 30 * sizeMultiplier.doubleValue());
            icon.setPrefSize(30 * sizeMultiplier.doubleValue(), 30 * sizeMultiplier.doubleValue());
            icon.setMaxSize(30 * sizeMultiplier.doubleValue(), 30 * sizeMultiplier.doubleValue());
            icon.setTranslateX(0);
        }
        else {
            System.out.println("test");
            icon.setMinSize(40 * sizeMultiplier.doubleValue(), 40 * sizeMultiplier.doubleValue());
            icon.setPrefSize(40 * sizeMultiplier.doubleValue(), 40 * sizeMultiplier.doubleValue());
            icon.setMaxSize(40 * sizeMultiplier.doubleValue(), 40 * sizeMultiplier.doubleValue());
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
