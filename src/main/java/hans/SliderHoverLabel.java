package hans;


import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class SliderHoverLabel {

    ControlBarController controlBarController;


    public Label timeLabel = new Label();
    public Label chapterlabel = new Label();


    SliderHoverLabel(StackPane parent, ControlBarController controlBarController, boolean miniplayer){
        this.controlBarController = controlBarController;

        timeLabel.setTextFill(Color.WHITE);

        timeLabel.setEffect(new DropShadow());

        timeLabel.setStyle("-fx-font-family: Roboto Medium; -fx-font-size: 15");
        timeLabel.setMouseTransparent(true);
        timeLabel.setBackground(Background.EMPTY);
        timeLabel.setText("00:00");

        if(miniplayer) timeLabel.setTranslateY(-35);
        else timeLabel.setTranslateY(-75);
        timeLabel.setPadding(Insets.EMPTY);
        timeLabel.setAlignment(Pos.CENTER);
        timeLabel.setVisible(false);

        StackPane.setAlignment(timeLabel, Pos.BOTTOM_CENTER);


        chapterlabel.setTextFill(Color.WHITE);

        chapterlabel.setEffect(new DropShadow());

        chapterlabel.setStyle("-fx-font-family: Roboto Medium; -fx-font-size: 14");
        chapterlabel.setMouseTransparent(true);
        chapterlabel.setBackground(Background.EMPTY);
        chapterlabel.setTranslateY(-105);
        chapterlabel.setPadding(Insets.EMPTY);
        chapterlabel.setAlignment(Pos.CENTER);
        chapterlabel.setVisible(false);
        chapterlabel.setText("Chapter test");

        StackPane.setAlignment(chapterlabel, Pos.BOTTOM_CENTER);

        if(miniplayer) parent.getChildren().add(timeLabel);
        else parent.getChildren().addAll(timeLabel, chapterlabel);

    }
}
