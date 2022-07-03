package hans;

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


    boolean loopOn = false;
    boolean autoplayOn = false;
    boolean shuffleOn = false;

    PlaybackOptionsTab loopTab, shuffleTab, autoplayTab;

    VBox playbackOptionsBox = new VBox();

    HBox titleBox = new HBox();
    StackPane backPane = new StackPane();
    Region backIcon = new Region();
    Label titleLabel = new Label();
    SVGPath backSVG = new SVGPath();

    PlaybackOptionsController(SettingsController settingsController){
        this.settingsController = settingsController;

        backSVG.setContent(App.svgMap.get(SVG.CHEVRON_LEFT));

        //playbackOptionsBox.setMinSize(235, 171);
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

        shuffleTab.setOnMouseClicked((e) -> {
            shuffleTab.toggle.fire();

            if (loopTab.toggle.isSelected()) {
                loopTab.toggle.fire();
            }

            if (autoplayTab.toggle.isSelected()) {
                autoplayTab.toggle.fire();
            }
        });
        loopTab.setOnMouseClicked((e) -> {
            loopTab.toggle.fire();

            if (shuffleTab.toggle.isSelected()) {
                shuffleTab.toggle.fire();
            }

            if (autoplayTab.toggle.isSelected()) {
                autoplayTab.toggle.fire();
            }
        });
        autoplayTab.setOnMouseClicked((e) -> {
            autoplayTab.toggle.fire();

            if (loopTab.toggle.isSelected()) {
                loopTab.toggle.fire();
            }

            if (shuffleTab.toggle.isSelected()) {
                shuffleTab.toggle.fire();
            }
        });

        shuffleTab.toggle.setOnMouseClicked((e) -> { // in addition to the hbox, also add same logic to the switch itself
            // (minus the .fire() part cause in that case the switch would
            // toggle twice in a row)

            if (loopTab.toggle.isSelected()) {
                loopTab.toggle.fire();
            }

            if (autoplayTab.toggle.isSelected()) {
                autoplayTab.toggle.fire();
            }
        });

        loopTab.toggle.setOnMouseClicked((e) -> {

            if (shuffleTab.toggle.isSelected()) {
                shuffleTab.toggle.fire();
            }

            if (autoplayTab.toggle.isSelected()) {
                autoplayTab.toggle.fire();
            }
        });

        autoplayTab.toggle.setOnMouseClicked((e) -> {

            if (loopTab.toggle.isSelected()) {
                loopTab.toggle.fire();
            }

            if (shuffleTab.toggle.isSelected()) {
                shuffleTab.toggle.fire();
            }
        });

        shuffleTab.toggle.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // OFF
                shuffleOn = false;
                if(!autoplayTab.toggle.isSelected() && !loopTab.toggle.isSelected()) {
                    settingsController.settingsHomeController.playbackOptionsTab.mainIcon.setShape(settingsController.settingsHomeController.tuneSVG);
                }

            } else { // ON
                shuffleOn = true;
                settingsController.settingsHomeController.playbackOptionsTab.mainIcon.setShape(settingsController.settingsHomeController.shuffleSVG);

                if(!settingsController.menuController.animationsInProgress.isEmpty()) return;

                if(!settingsController.menuController.queue.isEmpty()) settingsController.menuController.queueBox.shuffle();
            }
        });

        loopTab.toggle.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // OFF
                loopOn = false;
                if(!autoplayTab.toggle.isSelected() && !shuffleTab.toggle.isSelected()) {
                    settingsController.settingsHomeController.playbackOptionsTab.mainIcon.setShape(settingsController.settingsHomeController.tuneSVG);
                }
            } else { // ON
                loopOn = true;
                settingsController.settingsHomeController.playbackOptionsTab.mainIcon.setShape(settingsController.settingsHomeController.repeatOnceSVG);
            }
        });

        autoplayTab.toggle.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // OFF

                autoplayOn = false;

                if(!shuffleTab.toggle.isSelected() && !loopTab.toggle.isSelected()) {
                    settingsController.settingsHomeController.playbackOptionsTab.mainIcon.setShape(settingsController.settingsHomeController.tuneSVG);
                }
            } else { // ON

                autoplayOn = true;
                settingsController.settingsHomeController.playbackOptionsTab.mainIcon.setShape(settingsController.settingsHomeController.repeatSVG);

            }
        });

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
