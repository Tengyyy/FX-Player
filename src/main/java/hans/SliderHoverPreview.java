package hans;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class SliderHoverPreview {


    ControlBarController controlBarController;

    StackPane pane = new StackPane();
    ImageView imageView = new ImageView();



    SliderHoverPreview(StackPane parent, ControlBarController controlBarController){
        this.controlBarController = controlBarController;


        pane.setMouseTransparent(true);
        pane.setId("sliderHoverPreviewPane");
        pane.setPrefSize(164, 94);
        pane.setMaxSize(164, 94);

        pane.getChildren().add(imageView);

        imageView.setMouseTransparent(true);
        imageView.setFitHeight(90);
        imageView.setFitWidth(160);

        pane.setTranslateY(-100);




        StackPane.setAlignment(pane, Pos.BOTTOM_CENTER);

        parent.getChildren().add(pane);
    }


    public void setImage(Image image){
        imageView.setImage(image);
    }
}
