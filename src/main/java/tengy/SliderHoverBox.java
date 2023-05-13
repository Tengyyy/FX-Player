package tengy;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class SliderHoverBox extends VBox {

    public StackPane imagePane = new StackPane();
    StackPane imageInnerContainer = new StackPane();
    ImageView imageView = new ImageView();

    public Label timeLabel = new Label();
    public Label chapterlabel = new Label();

    DropShadow dropShadow = new DropShadow();


    boolean isMiniplayer;

    SliderHoverBox(StackPane parent, boolean isMiniplayer){

        this.isMiniplayer = isMiniplayer;

        this.setAlignment(Pos.BOTTOM_CENTER);
        this.setVisible(false);
        this.setSpacing(2);
        if(isMiniplayer) StackPane.setMargin(this, new Insets(0, 0, 50, 0));
        else StackPane.setMargin(this, new Insets(0, 0, 90, 0));

        this.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        this.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        StackPane.setAlignment(this, Pos.BOTTOM_CENTER);

        imagePane.setMouseTransparent(true);
        imagePane.setId("sliderHoverPreviewPane");
        imagePane.setPrefSize(164, 94);
        imagePane.setMaxSize(164, 94);
        imagePane.getChildren().add(imageInnerContainer);
        imagePane.setTranslateY(-3);

        imageInnerContainer.setPrefSize(160, 90);
        imageInnerContainer.setMaxSize(160, 90);

        Rectangle clip = new Rectangle();
        clip.setWidth(160);
        clip.setHeight(90);
        clip.setArcHeight(14);
        clip.setArcWidth(14);

        imageInnerContainer.setClip(clip);
        imageInnerContainer.getChildren().add(imageView);

        imageView.setMouseTransparent(true);
        imageView.setFitHeight(90);
        imageView.setFitWidth(160);
        imageView.setPreserveRatio(true);

        timeLabel.setTextFill(Color.WHITE);
        timeLabel.setEffect(dropShadow);
        timeLabel.getStyleClass().add("timeHoverLabel");
        timeLabel.setMouseTransparent(true);
        timeLabel.setBackground(Background.EMPTY);
        timeLabel.setText("00:00");
        timeLabel.setPadding(new Insets(2, 3, 2, 3));
        timeLabel.setAlignment(Pos.CENTER);

        chapterlabel.setTextFill(Color.WHITE);
        chapterlabel.setEffect(dropShadow);
        chapterlabel.getStyleClass().add("chapterHoverLabel");
        chapterlabel.setMouseTransparent(true);
        chapterlabel.setBackground(Background.EMPTY);
        chapterlabel.setAlignment(Pos.CENTER);
        chapterlabel.setPadding(new Insets(2, 3, 2, 3));

        this.getChildren().add(timeLabel);

        parent.getChildren().add(this);
    }

    public void setImage(Image image){
        if(isMiniplayer) return;

        imageView.setImage(image);
    }


    public void setBackground(boolean on){
        if(on){
            timeLabel.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.85), new CornerRadii(4), Insets.EMPTY)));
            chapterlabel.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.85), new CornerRadii(4), Insets.EMPTY)));

            timeLabel.setEffect(null);
            chapterlabel.setEffect(null);
        }
        else {
            timeLabel.setBackground(Background.EMPTY);
            chapterlabel.setBackground(Background.EMPTY);

            timeLabel.setEffect(dropShadow);
            chapterlabel.setEffect(dropShadow);
        }
    }
}
