package tengy.PlaybackSettings;

import tengy.SVG;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import uk.co.caprica.vlcj.player.base.TrackDescription;

public class AudioTrackTab extends HBox {

    AudioTrackChooserController audioTrackChooserController;


    StackPane checkIconPane = new StackPane();
    public Region checkIcon = new Region();
    SVGPath checkSVG = new SVGPath();

    Label valueLabel = new Label();

    public int id;

    public AudioTrackTab(AudioTrackChooserController audioTrackChooserController, TrackDescription trackDescription, boolean isActive){

        this.audioTrackChooserController = audioTrackChooserController;

        checkSVG.setContent(SVG.CHECK.getContent());

        this.setPrefSize(235, 35);
        this.setMaxSize(235, 35);

        this.setPadding(new Insets(0, 10, 0, 10));

        this.getStyleClass().add("settingsPaneTab");

        this.setCursor(Cursor.HAND);

        this.id = trackDescription.id();

        this.setOnMouseClicked(e -> {
            if(audioTrackChooserController.selectedTab == this || audioTrackChooserController.playbackSettingsController.menuController.queuePage.queueBox.activeItem.get() == null || !audioTrackChooserController.playbackSettingsController.menuController.queuePage.queueBox.activeItem.get().getMediaItemGenerated().get()) return;

            audioTrackChooserController.selectedTab.unselect();

            audioTrackChooserController.selectedTab = this;
            checkIcon.setVisible(true);
            audioTrackChooserController.playbackSettingsController.mediaInterface.setAudioTrack(trackDescription.id());
        });

        checkIconPane.setMinSize(30, 35);
        checkIconPane.setPrefSize(30, 35);
        checkIconPane.setMaxSize(30, 35);
        checkIconPane.setPadding(new Insets(0, 5, 0, 0));
        checkIconPane.getChildren().add(checkIcon);

        checkIcon.setMinSize(14, 11);
        checkIcon.setPrefSize(14, 11);
        checkIcon.setMaxSize(14, 11);
        checkIcon.setShape(checkSVG);
        checkIcon.getStyleClass().add("settingsPaneIcon");
        checkIcon.setVisible(isActive);

        valueLabel.setFont(new Font(15));
        valueLabel.setPrefHeight(35);
        valueLabel.setPrefWidth(140);
        valueLabel.setText(trackDescription.description());
        valueLabel.getStyleClass().add("settingsPaneText");

        this.getChildren().addAll(checkIconPane, valueLabel);

        audioTrackChooserController.audioTrackChooserBox.getChildren().add(this);
    }

    public void unselect(){
        checkIcon.setVisible(false);
    }
}
