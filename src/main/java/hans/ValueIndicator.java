package hans;

import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class ValueIndicator {

    MainController mainController;
    StackPane wrapper;
    Label text;
    PauseTransition timer;

    ValueIndicator(MainController mainController){
        this.mainController = mainController;

        wrapper = new StackPane();
        wrapper.setId("valueIndicatorBackground");
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
        if(mainController.sizeMultiplier.doubleValue() == 0.7) text.setStyle("-fx-font-family: \"Roboto\"; -fx-font-size: 22; -fx-text-fill: rgb(210,210,210);");
        else text.setStyle("-fx-font-family: \"Roboto\"; -fx-font-size: 28; -fx-text-fill: rgb(210,210,210);");

        wrapper.setPadding(new Insets(15 * mainController.sizeMultiplier.doubleValue(), 25 * mainController.sizeMultiplier.doubleValue(), 15 * mainController.sizeMultiplier.doubleValue(), 25 * mainController.sizeMultiplier.doubleValue()));
        wrapper.setTranslateY(50 * mainController.sizeMultiplier.doubleValue());
    }

}
