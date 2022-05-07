package hans;

import java.io.File;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class CustomSpeedTab extends HBox {

    SettingsController settingsController;

    Label playbackCustomCheck = new Label();
    Label playbackCustomText = new Label();

    File checkFile = new File("Images/checkFile.png");
    Image check = new Image(checkFile.toURI().toString());

    CustomSpeedTab(SettingsController settingsController, boolean selected) {

        this.settingsController = settingsController;

        this.setPrefWidth(235);
        this.setPrefHeight(50);
        this.setPadding(new Insets(0, 10, 0, 10));

        this.setOnMouseEntered((e) -> {
            Utilities.hoverEffectOn(this);
        });

        this.setOnMouseExited((e) -> {
            Utilities.hoverEffectOff(this);
        });

        playbackCustomCheck.setPrefHeight(50);
        playbackCustomCheck.setPrefWidth(29);
        playbackCustomCheck.setPadding(new Insets(0, 5, 0, 0));

        if (selected) {
            settingsController.updatePlaybackSpeed(0, settingsController.formattedValue2, playbackCustomCheck, true);
        }

        playbackCustomText.setTextFill(Color.WHITE);
        playbackCustomText.setFont(new Font(15));
        playbackCustomText.setPrefHeight(50);
        playbackCustomText.setPrefWidth(186);
        playbackCustomText.setText("Custom (" + settingsController.df.format(settingsController.formattedValue2) + ")");

        this.getChildren().addAll(playbackCustomCheck, playbackCustomText);
        settingsController.playbackSpeedPage.getChildren().add(2, this);

        this.setOnMouseClicked((e) -> {
            settingsController.updatePlaybackSpeed(0, settingsController.formattedValue2, playbackCustomCheck, false);
            if(settingsController.menuController.activeItem != null) settingsController.mediaInterface.mediaPlayer.setRate(settingsController.formattedValue2);
        });
    }
}
