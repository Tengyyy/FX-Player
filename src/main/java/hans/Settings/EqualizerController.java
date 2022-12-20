package hans.Settings;

import hans.App;
import hans.SVG;
import hans.VerticalProgressBar;
import io.github.palexdev.materialfx.controls.MFXSlider;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

public class EqualizerController {

    SettingsController settingsController;


    ScrollPane scrollPane = new ScrollPane();
    VBox equalizerBox = new VBox();

    HBox titleBox = new HBox();
    StackPane backIconPane = new StackPane();
    Region backIcon = new Region();
    Label titleLabel = new Label();
    SVGPath backSVG = new SVGPath();

    HBox contentBox = new HBox();

    EqualizerController(SettingsController settingsController){
        this.settingsController = settingsController;


        backSVG.setContent(App.svgMap.get(SVG.CHEVRON_LEFT));

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("settingsScroll");
        scrollPane.setPrefSize(500, 303);
        scrollPane.setMaxSize(500, 303);
        scrollPane.setContent(equalizerBox);
        scrollPane.setVisible(false);
        scrollPane.setMouseTransparent(true);

        StackPane.setAlignment(scrollPane, Pos.BOTTOM_RIGHT);
        equalizerBox.setAlignment(Pos.BOTTOM_CENTER);


        equalizerBox.setMinSize(500, 300);
        equalizerBox.setPrefSize(500, 300);
        equalizerBox.setMaxSize(500, 300);
        equalizerBox.setPadding(new Insets(8, 0, 8, 0));
        equalizerBox.getChildren().addAll(titleBox, contentBox);

        titleBox.setMinSize(500, 40);
        titleBox.setPrefSize(500, 40);
        titleBox.setMaxSize(500, 40);
        titleBox.setPadding(new Insets(0, 10, 0, 10));
        VBox.setMargin(titleBox, new Insets(0, 0, 10, 0));

        titleBox.getStyleClass().add("settingsPaneTitle");
        titleBox.getChildren().addAll(backIconPane, titleLabel);

        backIconPane.setMinSize(25, 40);
        backIconPane.setPrefSize(25, 40);
        backIconPane.setMaxSize(25, 40);
        backIconPane.getChildren().add(backIcon);
        backIconPane.setCursor(Cursor.HAND);
        backIconPane.setOnMouseClicked((e) -> closeEqualizer());

        backIcon.setMinSize(8, 13);
        backIcon.setPrefSize(8, 13);
        backIcon.setMaxSize(8, 13);
        backIcon.getStyleClass().add("settingsPaneIcon");
        backIcon.setShape(backSVG);

        titleLabel.setMinHeight(40);
        titleLabel.setPrefHeight(40);
        titleLabel.setMaxHeight(40);
        titleLabel.setText("Equalizer");
        titleLabel.setCursor(Cursor.HAND);
        titleLabel.getStyleClass().add("settingsPaneText");
        titleLabel.setOnMouseClicked((e) -> closeEqualizer());

        contentBox.setMinSize(500, 234);
        contentBox.setPrefSize(500, 234);
        contentBox.setMaxSize(500, 234);
        contentBox.setPadding(new Insets(0, 5, 5, 5));
        contentBox.setAlignment(Pos.CENTER_LEFT);

        for(int i = 0; i < 10; i++){

            StackPane sliderPane = new StackPane();
            Slider slider = new Slider();
            VerticalProgressBar sliderTrack = new VerticalProgressBar(7, 168);


            sliderPane.setMinSize(30, 200);
            sliderPane.setPrefSize(30, 200);
            sliderPane.setMaxSize(30, 200);
            sliderPane.getChildren().addAll(sliderTrack.getProgressHolder(), slider);

            sliderTrack.getProgressBar().setProgress(0.5);
            sliderTrack.getProgressBar().getStyleClass().add("customSpeedTrack");

            slider.getStyleClass().add("customSpeedSlider");
            slider.setMin(-12);
            slider.setMax(12);
            slider.setValue(0);
            slider.setOrientation(Orientation.VERTICAL);
            slider.setMinHeight(180);
            slider.setPrefHeight(180);
            slider.setMaxHeight(180);

            contentBox.getChildren().add(sliderPane);
        }

        settingsController.settingsPane.getChildren().add(scrollPane);

    }

    public void closeEqualizer(){
        if(settingsController.animating.get()) return;

        settingsController.settingsState = SettingsState.HOME_OPEN;

        settingsController.settingsHomeController.settingsHome.setVisible(true);
        settingsController.settingsHomeController.settingsHome.setMouseTransparent(false);

        Timeline clipHeightTimeline = new Timeline();
        clipHeightTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(settingsController.clip.heightProperty(), settingsController.settingsHomeController.settingsHome.getHeight())));

        Timeline clipWidthTimeline = new Timeline();
        clipWidthTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(settingsController.clip.widthProperty(), settingsController.settingsHomeController.settingsHome.getWidth())));


        TranslateTransition homeTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), settingsController.settingsHomeController.settingsHome);
        homeTransition.setFromX(-settingsController.settingsHomeController.settingsHome.getWidth());
        homeTransition.setToX(0);

        TranslateTransition speedTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), scrollPane);
        speedTransition.setFromX(0);
        speedTransition.setToX(scrollPane.getWidth());


        ParallelTransition parallelTransition = new ParallelTransition(clipHeightTimeline, clipWidthTimeline, homeTransition, speedTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            settingsController.animating.set(false);
            scrollPane.setVisible(false);
            scrollPane.setMouseTransparent(true);
            scrollPane.setTranslateX(0);
        });

        parallelTransition.play();
        settingsController.animating.set(true);
    }
}
