package hans.Settings;

import hans.*;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

public class CustomSpeedPane {


    double formattedSliderValue = 1;
    double lastCustomValue = 1;

    PlaybackSpeedController playbackSpeedController;

    VBox customSpeedBox = new VBox();

    HBox customSpeedTitle = new HBox();
    StackPane customSpeedBackPane = new StackPane();
    Region customSpeedBackIcon = new Region();
    Label customSpeedTitleLabel = new Label();
    SVGPath backSVG = new SVGPath();

    StackPane sliderPane = new StackPane();
    public Slider customSpeedSlider = new Slider();
    ProgressBar sliderTrack = new ProgressBar();

    Label customSpeedLabel = new Label();


    CustomSpeedPane(PlaybackSpeedController playbackSpeedController){
        this.playbackSpeedController = playbackSpeedController;

        backSVG.setContent(App.svgMap.get(SVG.CHEVRON_LEFT));

        customSpeedBox.setPrefSize(235, 150);
        customSpeedBox.setMaxSize(235, 150);
        customSpeedBox.getChildren().addAll(customSpeedTitle, sliderPane, customSpeedLabel);
        customSpeedBox.setAlignment(Pos.BOTTOM_CENTER);
        StackPane.setAlignment(customSpeedBox, Pos.BOTTOM_RIGHT);

        customSpeedBox.setVisible(false);
        customSpeedBox.setMouseTransparent(true);

        customSpeedTitle.setMinSize(235, 48);
        customSpeedTitle.setPrefSize(235, 48);
        customSpeedTitle.setMaxSize(235, 48);
        customSpeedTitle.setPadding(new Insets(0, 10, 0, 10));
        customSpeedTitle.setAlignment(Pos.CENTER_LEFT);
        VBox.setMargin(customSpeedTitle, new Insets(0, 0, 20, 0));

        customSpeedTitle.getStyleClass().add("settingsPaneTitle");
        customSpeedTitle.getChildren().addAll(customSpeedBackPane, customSpeedTitleLabel);

        customSpeedBackPane.setMinSize(24, 40);
        customSpeedBackPane.setPrefSize(24, 40);
        customSpeedBackPane.setMaxSize(24, 40);
        customSpeedBackPane.setCursor(Cursor.HAND);
        customSpeedBackPane.getChildren().add(customSpeedBackIcon);
        customSpeedBackPane.setOnMouseClicked((e) -> closeCustomSpeed());

        customSpeedBackIcon.setMinSize(8, 13);
        customSpeedBackIcon.setPrefSize(8, 13);
        customSpeedBackIcon.setMaxSize(8, 13);
        customSpeedBackIcon.getStyleClass().add("settingsPaneIcon");
        customSpeedBackIcon.setShape(backSVG);

        customSpeedTitleLabel.setMinHeight(40);
        customSpeedTitleLabel.setPrefHeight(40);
        customSpeedTitleLabel.setMaxHeight(40);
        customSpeedTitleLabel.setText("Custom");
        customSpeedTitleLabel.setCursor(Cursor.HAND);
        customSpeedTitleLabel.getStyleClass().add("settingsPaneText");
        customSpeedTitleLabel.setOnMouseClicked((e) -> closeCustomSpeed());

        sliderPane.setMinSize(235, 30);
        sliderPane.setPrefSize(235, 30);
        sliderPane.setMaxSize(235, 30);
        sliderPane.getChildren().addAll(sliderTrack, customSpeedSlider);

        sliderTrack.setMinSize(138, 7);
        sliderTrack.setPrefSize(138,7);
        sliderTrack.setMaxSize(138, 7);
        sliderTrack.setProgress(0.75/1.75);
        sliderTrack.getStyleClass().add("customSliderTrack");

        customSpeedSlider.getStyleClass().add("customSlider");
        customSpeedSlider.setMin(0.25);
        customSpeedSlider.setMax(2);
        customSpeedSlider.setValue(1);
        customSpeedSlider.setBlockIncrement(0.05);
        customSpeedSlider.setMinWidth(150);
        customSpeedSlider.setPrefWidth(150);
        customSpeedSlider.setMaxWidth(150);

        customSpeedSlider.valueProperty().addListener((observableValue, oldValue, newValue) -> {

            formattedSliderValue = Math.floor(newValue.doubleValue() * 20) / 20; // floored to .05 precision

            double progress = (newValue.doubleValue() - 0.25) / 1.75; // adjust the slider scale ( 0.25 - 2 ) to match with the progress bar scale ( 0 - 1 )

            sliderTrack.setProgress(progress);

            customSpeedLabel.setText(playbackSpeedController.df.format(formattedSliderValue) + "x");

            if (formattedSliderValue * 4 != Math.round(formattedSliderValue * 4)) { // current slider value is not one of the default playback speed selections, which means a new custom speed tab has to be created or the value of the existing custom speed tab has to be updated

                lastCustomValue = formattedSliderValue;

                if(playbackSpeedController.playbackSpeedPane.customSpeedTab == null) new PlaybackSpeedTab(playbackSpeedController, playbackSpeedController.playbackSpeedPane, true);
                else playbackSpeedController.playbackSpeedPane.customSpeedTab.updateValue(formattedSliderValue);

            }
        });


        customSpeedSlider.valueChangingProperty().addListener((observableValue, oldValue, newValue) -> {
            // this is where we update the scrollvalue, mediaplayer speedrate etc
            if(newValue) return;

            playbackSpeedController.setSpeed(formattedSliderValue); // updates mediaplayer playback speed
            playbackSpeedController.updateTabs(formattedSliderValue);

        });

        customSpeedSlider.setOnMousePressed((e) -> customSpeedSlider.setValueChanging(true));
        customSpeedSlider.setOnMouseReleased((e) -> customSpeedSlider.setValueChanging(false));

        customSpeedLabel.setText("1x");
        customSpeedLabel.getStyleClass().add("settingsPaneText");
        customSpeedLabel.setId("customSpeedValue");
        VBox.setMargin(customSpeedLabel, new Insets(10, 0, 20, 0));

        playbackSpeedController.settingsController.settingsPane.getChildren().add(customSpeedBox);


    }

    public void closeCustomSpeed(){
        if(playbackSpeedController.settingsController.animating.get()) return;

        playbackSpeedController.settingsController.settingsState = SettingsState.PLAYBACK_SPEED_OPEN;

        playbackSpeedController.playbackSpeedPane.scrollPane.setVisible(true);
        playbackSpeedController.playbackSpeedPane.scrollPane.setMouseTransparent(false);

        Timeline clipTimeline = new Timeline();
        clipTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(playbackSpeedController.settingsController.clip.heightProperty(), playbackSpeedController.playbackSpeedPane.scrollPane.getHeight())));

        TranslateTransition customTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), customSpeedBox);
        customTransition.setFromX(0);
        customTransition.setToX(playbackSpeedController.playbackSpeedPane.scrollPane.getWidth());

        TranslateTransition speedTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), playbackSpeedController.playbackSpeedPane.scrollPane);
        speedTransition.setFromX(-playbackSpeedController.playbackSpeedPane.scrollPane.getWidth());
        speedTransition.setToX(0);


        ParallelTransition parallelTransition = new ParallelTransition(clipTimeline, customTransition, speedTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            playbackSpeedController.settingsController.animating.set(false);
            customSpeedBox.setVisible(false);
            customSpeedBox.setMouseTransparent(true);
            customSpeedBox.setTranslateX(0);
            playbackSpeedController.settingsController.clip.setHeight(playbackSpeedController.playbackSpeedPane.scrollPane.getPrefHeight());
        });

        parallelTransition.play();
        playbackSpeedController.settingsController.animating.set(true);

    }
}
