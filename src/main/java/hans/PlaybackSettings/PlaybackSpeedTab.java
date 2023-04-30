package hans.PlaybackSettings;

import hans.App;
import hans.SVG;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;


public class PlaybackSpeedTab extends HBox {

    PlaybackSpeedController playbackSpeedController;

    PlaybackSpeedPane playbackSpeedPane;

    StackPane checkIconPane = new StackPane();
    Region checkIcon = new Region();

    Label valueLabel = new Label();

    SVGPath checkSVG = new SVGPath();

    double speedValue;


    PlaybackSpeedTab(PlaybackSpeedController playbackSpeedController, PlaybackSpeedPane playbackSpeedPane, boolean isCustom){
        this.playbackSpeedController = playbackSpeedController;
        this.playbackSpeedPane = playbackSpeedPane;

        checkSVG.setContent(SVG.CHECK.getContent());

        this.setPrefSize(235, 35);
        this.setMaxSize(235, 35);

        this.setPadding(new Insets(0, 10, 0, 10));

        this.getStyleClass().add("settingsPaneTab");

        this.setCursor(Cursor.HAND);


        checkIconPane.setMinSize(30, 35);
        checkIconPane.setPrefSize(30, 35);
        checkIconPane.setMaxSize(30, 35);
        checkIconPane.setPadding(new Insets(0, 5, 0, 0));

        checkIcon.setMinSize(14, 11);
        checkIcon.setPrefSize(14, 11);
        checkIcon.setMaxSize(14, 11);
        checkIcon.setShape(checkSVG);
        checkIcon.getStyleClass().add("settingsPaneIcon");


        valueLabel.setFont(new Font(15));
        valueLabel.setPrefHeight(35);
        valueLabel.setPrefWidth(185);

        if(isCustom) speedValue = playbackSpeedController.customSpeedPane.lastCustomValue;
        else speedValue = (double) (playbackSpeedPane.speedTabs.size() + 1) / 4;

        if(isCustom) valueLabel.setText("Custom (" + playbackSpeedController.df.format(speedValue) + ")");
        else if(playbackSpeedPane.speedTabs.size() == 3) valueLabel.setText("Normal");
        else valueLabel.setText(String.valueOf(speedValue));

        valueLabel.getStyleClass().add("settingsPaneText");

        checkIconPane.getChildren().add(checkIcon);

        this.getChildren().addAll(checkIconPane, valueLabel);

        if(playbackSpeedPane.speedTabs.size() != 3){
            checkIcon.setVisible(false);
        }

        this.setOnMouseClicked((e) -> {

            for(PlaybackSpeedTab playbackSpeedTab : playbackSpeedPane.speedTabs){
                playbackSpeedTab.checkIcon.setVisible(false);
            }
            if(playbackSpeedPane.customSpeedTab != null) playbackSpeedPane.customSpeedTab.checkIcon.setVisible(false);

            this.checkIcon.setVisible(true);

            playbackSpeedController.setSpeed(this.speedValue);
        });

        if(isCustom){
            playbackSpeedPane.speedTabs.add(0, this);
            playbackSpeedPane.customSpeedTab = this;
            playbackSpeedPane.playbackSpeedBox.setPrefHeight(playbackSpeedPane.playbackSpeedBox.getPrefHeight() + 35);
            playbackSpeedPane.playbackSpeedBox.setMaxHeight(playbackSpeedPane.playbackSpeedBox.getMaxHeight() + 35);
            playbackSpeedPane.scrollPane.setPrefHeight(playbackSpeedPane.scrollPane.getPrefHeight() + 35);
            playbackSpeedPane.scrollPane.setMaxHeight(playbackSpeedPane.scrollPane.getMaxHeight() + 35);
            playbackSpeedPane.playbackSpeedBox.getChildren().add(1, this);
        }
        else {
            playbackSpeedPane.speedTabs.add(this);
            playbackSpeedPane.playbackSpeedBox.getChildren().add(this);
        }

    }


    public void updateValue(double newValue){
        speedValue = newValue;
        valueLabel.setText("Custom (" + playbackSpeedController.df.format(newValue) + ")");
    }
}
