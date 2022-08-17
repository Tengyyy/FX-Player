package hans;


import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class SliderHoverLabel {

    MainController mainController;
    ControlBarController controlBarController;


    Label label = new Label();

    SliderHoverLabel(MainController mainController, ControlBarController controlBarController){
        this.mainController = mainController;
        this.controlBarController = controlBarController;

        label.setTextFill(Color.WHITE);

        //TODO: create a stronger drop-shadow effect
        label.setEffect(new DropShadow());

        label.setStyle("-fx-font-family: Roboto Medium; -fx-font-size: 14");
        label.setMouseTransparent(true);
        label.setBackground(Background.EMPTY);
        label.setText("00:00");
        label.setTranslateY(-75);
        label.setPadding(Insets.EMPTY);
        label.setAlignment(Pos.CENTER);
        label.setVisible(false);

        StackPane.setAlignment(label, Pos.BOTTOM_CENTER);

        mainController.videoImageViewWrapper.getChildren().add(label);

    }
}
