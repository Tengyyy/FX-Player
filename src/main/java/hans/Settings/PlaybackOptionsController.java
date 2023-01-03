package hans.Settings;

import hans.*;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

public class PlaybackOptionsController {

    SettingsController settingsController;


    public boolean loopOn = false;
    public boolean autoplayOn = false;
    public boolean shuffleOn = false;

    public PlaybackOptionsTab loopTab;
    public PlaybackOptionsTab shuffleTab;
    public PlaybackOptionsTab autoplayTab;

    VBox playbackOptionsBox = new VBox();

    HBox titleBox = new HBox();
    StackPane backPane = new StackPane();
    Region backIcon = new Region();
    Label titleLabel = new Label();
    SVGPath backSVG = new SVGPath();

    PlaybackOptionsController(SettingsController settingsController){
        this.settingsController = settingsController;

        backSVG.setContent(App.svgMap.get(SVG.CHEVRON_LEFT));

        playbackOptionsBox.setPrefSize(235, 171);
        playbackOptionsBox.setMaxSize(235, 171);
        playbackOptionsBox.setPadding(new Insets(8, 0, 8, 0));
        playbackOptionsBox.getChildren().add(titleBox);
        StackPane.setAlignment(playbackOptionsBox, Pos.BOTTOM_RIGHT);

        playbackOptionsBox.setVisible(false);
        playbackOptionsBox.setMouseTransparent(true);

        titleBox.setMinSize(235, 40);
        titleBox.setPrefSize(235, 40);
        titleBox.setMaxSize(235, 40);
        titleBox.getStyleClass().add("settingsPaneTitle");
        titleBox.setPadding(new Insets(0, 10, 0, 10));
        VBox.setMargin(titleBox, new Insets(0, 0, 10, 0));
        titleBox.getChildren().addAll(backPane, titleLabel);

        backPane.setMinSize(24, 35);
        backPane.setPrefSize(24, 35);
        backPane.setMaxSize(24, 35);
        backPane.setCursor(Cursor.HAND);
        backPane.setOnMouseClicked((e) -> closePlaybackOptions());
        backPane.getChildren().add(backIcon);

        backIcon.setMinSize(8, 13);
        backIcon.setPrefSize(8, 13);
        backIcon.setMaxSize(8, 13);
        backIcon.getStyleClass().add("settingsPaneIcon");
        backIcon.setShape(backSVG);


        titleLabel.setMinHeight(35);
        titleLabel.setPrefHeight(35);
        titleLabel.setMaxHeight(35);
        titleLabel.setCursor(Cursor.HAND);
        titleLabel.setText("Playback options");
        titleLabel.getStyleClass().add("settingsPaneText");
        titleLabel.setAlignment(Pos.CENTER_LEFT);
        titleLabel.setOnMouseClicked((e) -> closePlaybackOptions());

        shuffleTab = new PlaybackOptionsTab(this, "Shuffle");
        loopTab = new PlaybackOptionsTab(this, "Loop video");
        autoplayTab = new PlaybackOptionsTab(this, "Autoplay");


        settingsController.settingsPane.getChildren().add(playbackOptionsBox);

        shuffleTab.setOnMouseClicked((e) -> shuffleTab.toggle.fire());

        loopTab.setOnMouseClicked((e) -> loopTab.toggle.fire());

        autoplayTab.setOnMouseClicked((e) -> autoplayTab.toggle.fire());




        shuffleTab.toggle.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // OFF
                shuffleOn = false;

                settingsController.mainController.playbackOptionsPopUp.shuffleCheckIcon.setVisible(false);

                if(!autoplayTab.toggle.isSelected() && !loopTab.toggle.isSelected()) {
                    settingsController.settingsHomeController.playbackOptionsTab.mainIcon.setShape(settingsController.settingsHomeController.tuneSVG);
                }
                else if(autoplayTab.toggle.isSelected()) settingsController.settingsHomeController.playbackOptionsTab.mainIcon.setShape(settingsController.settingsHomeController.repeatSVG);

                settingsController.menuController.shuffleTooltip.updateText("Shuffle is off");

                settingsController.menuController.shuffleDot.setOpacity(0.1);

            } else { // ON
                shuffleOn = true;

                settingsController.mainController.playbackOptionsPopUp.shuffleCheckIcon.setVisible(true);


                if(!loopTab.toggle.isSelected()) {
                    settingsController.settingsHomeController.playbackOptionsTab.mainIcon.setShape(settingsController.settingsHomeController.shuffleSVG);
                }

                settingsController.menuController.shuffleTooltip.updateText("Shuffle is on");

                if(!settingsController.menuController.animationsInProgress.isEmpty()) return;

                if(!settingsController.menuController.queue.isEmpty()) settingsController.menuController.queueBox.shuffle();

                settingsController.menuController.shuffleDot.setOpacity(1);
            }

        });

        loopTab.toggle.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // OFF
                loopOn = false;
                settingsController.mediaInterface.embeddedMediaPlayer.controls().setRepeat(false);

                settingsController.mainController.playbackOptionsPopUp.loopCheckIcon.setVisible(false);

                if(shuffleTab.toggle.isSelected()) settingsController.settingsHomeController.playbackOptionsTab.mainIcon.setShape(settingsController.settingsHomeController.shuffleSVG);
                else if(autoplayTab.toggle.isSelected()) settingsController.settingsHomeController.playbackOptionsTab.mainIcon.setShape(settingsController.settingsHomeController.repeatSVG);
                else settingsController.settingsHomeController.playbackOptionsTab.mainIcon.setShape(settingsController.settingsHomeController.tuneSVG);

            } else { // ON
                loopOn = true;
                settingsController.mediaInterface.embeddedMediaPlayer.controls().setRepeat(true);

                settingsController.mainController.playbackOptionsPopUp.loopCheckIcon.setVisible(true);

                settingsController.settingsHomeController.playbackOptionsTab.mainIcon.setShape(settingsController.settingsHomeController.repeatOnceSVG);
            }
        });

        autoplayTab.toggle.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // OFF

                autoplayOn = false;

                settingsController.mainController.playbackOptionsPopUp.autoplayCheckIcon.setVisible(false);


                if(!shuffleTab.toggle.isSelected() && !loopTab.toggle.isSelected()) {
                    settingsController.settingsHomeController.playbackOptionsTab.mainIcon.setShape(settingsController.settingsHomeController.tuneSVG);
                }
            } else { // ON

                autoplayOn = true;

                if(settingsController.mainController.playbackOptionsPopUp != null) settingsController.mainController.playbackOptionsPopUp.autoplayCheckIcon.setVisible(true);


                if(!shuffleTab.toggle.isSelected() && !loopTab.toggle.isSelected()) {
                    settingsController.settingsHomeController.playbackOptionsTab.mainIcon.setShape(settingsController.settingsHomeController.repeatSVG);
                }
            }
        });

        autoplayTab.toggle.setSelected(true);

    }


    public void closePlaybackOptions(){
        if(settingsController.animating.get()) return;

        settingsController.settingsState = SettingsState.HOME_OPEN;

        settingsController.settingsHomeController.settingsHome.setVisible(true);
        settingsController.settingsHomeController.settingsHome.setMouseTransparent(false);

        Timeline clipTimeline = new Timeline();
        clipTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(settingsController.clip.heightProperty(), settingsController.settingsHomeController.settingsHome.getHeight())));

        TranslateTransition homeTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), settingsController.settingsHomeController.settingsHome);
        homeTransition.setFromX(-settingsController.settingsHomeController.settingsHome.getWidth());
        homeTransition.setToX(0);

        TranslateTransition optionsTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), settingsController.playbackOptionsController.playbackOptionsBox);
        optionsTransition.setFromX(0);
        optionsTransition.setToX(settingsController.settingsHomeController.settingsHome.getWidth());


        ParallelTransition parallelTransition = new ParallelTransition(clipTimeline, homeTransition, optionsTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            settingsController.animating.set(false);
            playbackOptionsBox.setVisible(false);
            playbackOptionsBox.setMouseTransparent(true);
        });

        parallelTransition.play();
        settingsController.animating.set(true);

    }
}
