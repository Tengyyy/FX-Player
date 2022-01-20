package hans;

import java.io.File;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class MenuItem extends HBox {

    ScrollPane menuScroll;

    File videoFile; // the mp4 file that this menu-item is representing

    BackgroundFill backgroundFill;
    Background background;

    MenuItem(ScrollPane menuScroll, File videoFile) {

        this.menuScroll = menuScroll;
        this.videoFile = videoFile;

        this.prefWidthProperty().bind(menuScroll.widthProperty());
        this.setPrefHeight(80);
        this.setMinHeight(80);


        // create a background fill
        this.setStyle("-fx-background-color: rgb(40,40,40); -fx-background-radius: 5;");

        Label label = new Label("SU EMA!!!!!!!");
        label.setFont(new Font(30));

        this.getChildren().add(label);


        this.setOnMouseEntered((e) -> {

            this.setStyle("-fx-background-color: rgb(50,50,50); -fx-background-radius: 5;");

        });

        this.setOnMouseExited((e) -> {
            this.setStyle("-fx-background-color: rgb(40,40,40); -fx-background-radius: 5;");
        });

    }

}
