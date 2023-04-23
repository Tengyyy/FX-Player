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

    DropShadow dropShadow = new DropShadow();

    SliderHoverLabel(StackPane parent, ControlBarController controlBarController, boolean miniplayer){
        this.controlBarController = controlBarController;

        timeLabel.setTextFill(Color.WHITE);

        timeLabel.setEffect(dropShadow);

        timeLabel.getStyleClass().add("timeHoverLabel");
        timeLabel.setMouseTransparent(true);
        timeLabel.setBackground(Background.EMPTY);
        timeLabel.setText("00:00");
        timeLabel.setPadding(new Insets(2, 3, 2, 3));

        if(miniplayer) timeLabel.setTranslateY(-35);
        else timeLabel.setTranslateY(-73);
        timeLabel.setPadding(Insets.EMPTY);
        timeLabel.setAlignment(Pos.CENTER);
        timeLabel.setVisible(false);

        StackPane.setAlignment(timeLabel, Pos.BOTTOM_CENTER);


        chapterlabel.setTextFill(Color.WHITE);

        chapterlabel.setEffect(dropShadow);

        chapterlabel.getStyleClass().add("chapterHoverLabel");
        chapterlabel.setMouseTransparent(true);
        chapterlabel.setBackground(Background.EMPTY);
        chapterlabel.setTranslateY(-105);
        chapterlabel.setPadding(Insets.EMPTY);
        chapterlabel.setAlignment(Pos.CENTER);
        chapterlabel.setVisible(false);
        chapterlabel.setPadding(new Insets(2, 3, 2, 3));


        StackPane.setAlignment(chapterlabel, Pos.BOTTOM_CENTER);

        if(miniplayer) parent.getChildren().add(timeLabel);
        else parent.getChildren().addAll(timeLabel, chapterlabel);

    }

    public void setBackground(boolean on){
        if(on){
            timeLabel.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0), CornerRadii.EMPTY, Insets.EMPTY)));
            chapterlabel.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0), CornerRadii.EMPTY, Insets.EMPTY)));

            timeLabel.setEffect(null);
            chapterlabel.setEffect(null);
        }
        else {
            timeLabel.setBackground(Background.EMPTY);
            chapterlabel.setBackground(Background.EMPTY);

            timeLabel.setEffect(dropShadow);
            timeLabel.setEffect(dropShadow);
        }
    }
}
