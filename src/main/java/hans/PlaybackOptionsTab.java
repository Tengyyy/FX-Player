package hans;

import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;


public class PlaybackOptionsTab extends HBox {

    PlaybackOptionsController playbackOptionsController;

    Label titleLabel = new Label();
    MFXToggleButton toggle = new MFXToggleButton();

    PlaybackOptionsTab(PlaybackOptionsController playbackOptionsController, String titleText){

        this.playbackOptionsController = playbackOptionsController;

        this.setMinSize(235, 35);
        this.setPrefSize(235, 35);
        this.setMaxSize(235, 35);
        this.getStyleClass().add("settingsPaneTab");
        this.setPadding(new Insets(0, 10, 0, 10));
        this.setAlignment(Pos.CENTER_LEFT);
        this.getChildren().addAll(titleLabel, toggle);
        this.setCursor(Cursor.HAND);

        titleLabel.setMinSize(165, 35);
        titleLabel.setPrefSize(165, 35);
        titleLabel.setMaxSize(165, 35);
        titleLabel.getStyleClass().add("settingsPaneText");
        titleLabel.setText(titleText);

        toggle.setMinSize(50, 35);
        toggle.setPrefSize(50, 35);
        toggle.setMaxSize(50, 35);
        toggle.setLength(38);
        toggle.setRadius(10);

        playbackOptionsController.playbackOptionsBox.getChildren().add(this);

    }

}
