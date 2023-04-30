package hans;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.util.Objects;


public class SplashScreen {

    Scene scene;
    StackPane background = new StackPane();
    ImageView imageView = new ImageView();
    Image icon;

    SplashScreen(){

        icon = new Image(Objects.requireNonNull(getClass().getResource("images/appIcon.png")).toExternalForm());
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);
        imageView.setImage(icon);


        background.getChildren().add(imageView);
        background.setBackground(new Background(new BackgroundFill(Color.rgb(20, 20, 20), CornerRadii.EMPTY, Insets.EMPTY)));

        scene = new Scene(background, 705, 400);
    }
}
