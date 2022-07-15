package hans;

import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class ValueIndicator {

    MainController mainController;
    StackPane wrapper;
    Label text;
    PauseTransition timer;

    ValueIndicator(MainController mainController){
        this.mainController = mainController;

        wrapper = new StackPane();
        wrapper.setBackground(new Background(new BackgroundFill(Color.rgb(30,30,30,0.6), new CornerRadii(5), Insets.EMPTY)));
        wrapper.setMouseTransparent(true);
        wrapper.setVisible(false);
        wrapper.setTranslateY(50 * mainController.sizeMultiplier.doubleValue());
        wrapper.setPadding(new Insets(15 * mainController.sizeMultiplier.doubleValue(), 25 * mainController.sizeMultiplier.doubleValue(), 15 * mainController.sizeMultiplier.doubleValue(), 25 * mainController.sizeMultiplier.doubleValue()));
        StackPane.setAlignment(wrapper, Pos.TOP_CENTER);
        wrapper.setPrefSize(Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE);
        wrapper.setMinSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
        wrapper.setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);

        text = new Label();
        text.setStyle("-fx-font-family: \"Roboto\"; -fx-font-size: 22; -fx-text-fill: rgb(210,210,210);");
        StackPane.setAlignment(text, Pos.CENTER);
        wrapper.getChildren().add(text);


        timer = new PauseTransition(Duration.millis(600));
        timer.setOnFinished(e -> wrapper.setVisible(false));


        mainController.mediaViewInnerWrapper.getChildren().add(wrapper);

    }


    public void setValue(String value){
        text.setText(value);
    }

    public void play(){
        wrapper.setVisible(true);
        timer.playFromStart();
    }


    public void resize(){

        if(mainController.sizeMultiplier.doubleValue() == 0.35){
            text.setStyle("-fx-font-family: \"Roboto\"; -fx-font-size: 15; -fx-text-fill: rgb(210,210,210);");
        }
        else if(mainController.sizeMultiplier.doubleValue() == 0.5){
            text.setStyle("-fx-font-family: \"Roboto\"; -fx-font-size: 18; -fx-text-fill: rgb(210,210,210);");
        }
        else if(mainController.sizeMultiplier.doubleValue() == 0.6){
            text.setStyle("-fx-font-family: \"Roboto\"; -fx-font-size: 20; -fx-text-fill: rgb(210,210,210);");
        }
        else if(mainController.sizeMultiplier.doubleValue() == 0.7){
            text.setStyle("-fx-font-family: \"Roboto\"; -fx-font-size: 22; -fx-text-fill: rgb(210,210,210);");
        }
        else if(mainController.sizeMultiplier.doubleValue() == 0.55){
            text.setStyle("-fx-font-family: \"Roboto\"; -fx-font-size: 19; -fx-text-fill: rgb(210,210,210);");
        }
        else if(mainController.sizeMultiplier.doubleValue() == 0.65){
            text.setStyle("-fx-font-family: \"Roboto\"; -fx-font-size: 21; -fx-text-fill: rgb(210,210,210);");
        }
        else if(mainController.sizeMultiplier.doubleValue() == 0.8){
            text.setStyle("-fx-font-family: \"Roboto\"; -fx-font-size: 24; -fx-text-fill: rgb(210,210,210);");
        }
        else if(mainController.sizeMultiplier.doubleValue() == 1){
            text.setStyle("-fx-font-family: \"Roboto\"; -fx-font-size: 27; -fx-text-fill: rgb(210,210,210);");
        }
        else if(mainController.sizeMultiplier.doubleValue() == 1.2){
            text.setStyle("-fx-font-family: \"Roboto\"; -fx-font-size: 30; -fx-text-fill: rgb(210,210,210);");
        }

        wrapper.setPadding(new Insets(15 * mainController.sizeMultiplier.doubleValue(), 25 * mainController.sizeMultiplier.doubleValue(), 15 * mainController.sizeMultiplier.doubleValue(), 25 * mainController.sizeMultiplier.doubleValue()));
        wrapper.setTranslateY(50 * mainController.sizeMultiplier.doubleValue());
    }

    public void moveToMiniplayer(){
        mainController.mediaViewInnerWrapper.getChildren().remove(wrapper);
        mainController.miniplayer.miniplayerController.mediaViewInnerWrapper.getChildren().add(wrapper);
    }

    public void moveToMainplayer(){
        mainController.miniplayer.miniplayerController.mediaViewInnerWrapper.getChildren().remove(wrapper);
        mainController.mediaViewInnerWrapper.getChildren().add(wrapper);
    }

}
