package hans;

import java.io.File;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;

public class CustomSpeedTab extends HBox {

    SettingsController settingsController;

    StackPane playbackCustomCheckPane = new StackPane();
    Region playbackCustomCheckIcon = new Region();


    Label playbackCustomText = new Label();

    SVGPath checkSVG = new SVGPath();


    CustomSpeedTab(SettingsController settingsController, boolean selected) {

        this.settingsController = settingsController;

        checkSVG.setContent(App.svgMap.get(SVG.CHECK));

        this.setPrefWidth(235);
        this.setPrefHeight(50);
        this.setPadding(new Insets(0, 10, 0, 10));

        this.getStyleClass().add("settingsPaneTab");

        playbackCustomCheckPane.setMinSize(30, 50);
        playbackCustomCheckPane.setPrefSize(30, 50);
        playbackCustomCheckPane.setMaxSize(30, 50);

        playbackCustomCheckIcon.setMinSize(14, 11);
        playbackCustomCheckIcon.setPrefSize(14, 11);
        playbackCustomCheckIcon.setMaxSize(14, 11);
        playbackCustomCheckIcon.setShape(checkSVG);
        playbackCustomCheckIcon.getStyleClass().add("settingsPaneIcon");

        playbackCustomCheckPane.setPadding(new Insets(0, 5, 0, 0));

        if (selected) {
            settingsController.updatePlaybackSpeed(0, settingsController.formattedValue2, playbackCustomCheckIcon, true);
        }

        playbackCustomText.setFont(new Font(15));
        playbackCustomText.setPrefHeight(50);
        playbackCustomText.setPrefWidth(185);
        playbackCustomText.setText("Custom (" + settingsController.df.format(settingsController.formattedValue2) + ")");
        playbackCustomText.getStyleClass().add("settingsPaneText");

        playbackCustomCheckPane.getChildren().add(playbackCustomCheckIcon);

        this.getChildren().addAll(playbackCustomCheckPane, playbackCustomText);
        settingsController.playbackSpeedPage.getChildren().add(1, this);

        this.setOnMouseClicked((e) -> {
            settingsController.updatePlaybackSpeed(0, settingsController.formattedValue2, playbackCustomCheckIcon, false);
            if(settingsController.menuController.activeItem != null) settingsController.mediaInterface.mediaPlayer.setRate(settingsController.formattedValue2);
        });
    }
}
