package fxplayer.playbackSettings;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import fxplayer.*;
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

import java.util.ArrayList;
import java.util.List;

import static fxplayer.Utilities.keyboardFocusOff;
import static fxplayer.Utilities.keyboardFocusOn;

public class CustomSpeedPane {


    public double formattedSliderValue = 1;
    double lastCustomValue = 1;

    PlaybackSpeedController playbackSpeedController;

    VBox customSpeedBox = new VBox();

    HBox customSpeedTitle = new HBox();
    Button backButton = new Button();
    Region customSpeedBackIcon = new Region();
    Label customSpeedTitleLabel = new Label();
    SVGPath backSVG = new SVGPath();

    StackPane sliderPane = new StackPane();
    public Slider customSpeedSlider = new Slider();
    ProgressBar sliderTrack = new ProgressBar();

    Label customSpeedLabel = new Label();

    List<Node> focusNodes = new ArrayList<>();
    IntegerProperty focus = new SimpleIntegerProperty(-1);

    CustomSpeedPane(PlaybackSpeedController playbackSpeedController){
        this.playbackSpeedController = playbackSpeedController;

        backSVG.setContent(SVG.CHEVRON_LEFT.getContent());

        customSpeedBox.setPrefSize(235, 150);
        customSpeedBox.setMaxSize(235, 150);
        customSpeedBox.getChildren().addAll(customSpeedTitle, sliderPane, customSpeedLabel);
        customSpeedBox.setAlignment(Pos.BOTTOM_CENTER);
        StackPane.setAlignment(customSpeedBox, Pos.BOTTOM_RIGHT);

        customSpeedBox.setVisible(false);
        customSpeedBox.setOnMouseClicked(e -> customSpeedBox.requestFocus());

        customSpeedTitle.setMinSize(235, 48);
        customSpeedTitle.setPrefSize(235, 48);
        customSpeedTitle.setMaxSize(235, 48);
        customSpeedTitle.setPadding(new Insets(0, 10, 0, 10));
        customSpeedTitle.setAlignment(Pos.CENTER_LEFT);
        VBox.setMargin(customSpeedTitle, new Insets(0, 0, 20, 0));

        customSpeedTitle.getStyleClass().add("settingsPaneTitle");
        customSpeedTitle.getChildren().addAll(backButton, customSpeedTitleLabel);

        backButton.setMinSize(30, 40);
        backButton.setPrefSize(30, 40);
        backButton.setMaxSize(30, 40);
        backButton.setFocusTraversable(false);
        backButton.getStyleClass().addAll("transparentButton", "settingsMenuButton");
        backButton.setGraphic(customSpeedBackIcon);
        backButton.setOnAction((e) -> closeCustomSpeed());
        backButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue) focus.set(0);
            else {
                keyboardFocusOff(backButton);
                focus.set(-1);
            }
        });

        backButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            backButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        backButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            backButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });

        customSpeedBackIcon.setMinSize(8, 13);
        customSpeedBackIcon.setPrefSize(8, 13);
        customSpeedBackIcon.setMaxSize(8, 13);
        customSpeedBackIcon.getStyleClass().add("graphic");
        customSpeedBackIcon.setShape(backSVG);

        customSpeedTitleLabel.setMinHeight(40);
        customSpeedTitleLabel.setPrefHeight(40);
        customSpeedTitleLabel.setMaxHeight(40);
        customSpeedTitleLabel.setText("Custom");
        customSpeedTitleLabel.setCursor(Cursor.HAND);
        customSpeedTitleLabel.getStyleClass().add("settingsPaneText");
        customSpeedTitleLabel.setOnMouseClicked((e) -> closeCustomSpeed());
        customSpeedTitleLabel.setPadding(new Insets(0, 0, 0, 4));


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
        customSpeedSlider.setFocusTraversable(false);

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

        customSpeedSlider.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                sliderTrack.pseudoClassStateChanged(PseudoClass.getPseudoClass("focused"), true);
                focus.set(1);
            }
            else {
                sliderTrack.pseudoClassStateChanged(PseudoClass.getPseudoClass("focused"), false);
                focus.set(-1);
                keyboardFocusOff(customSpeedSlider);
                customSpeedSlider.setValueChanging(false);
            }
        });

        customSpeedSlider.setOnMouseEntered(e -> sliderTrack.pseudoClassStateChanged(PseudoClass.getPseudoClass("hover"), true));
        customSpeedSlider.setOnMouseExited(e -> sliderTrack.pseudoClassStateChanged(PseudoClass.getPseudoClass("hover"), false));


        customSpeedLabel.setText("1x");
        customSpeedLabel.getStyleClass().add("settingsPaneText");
        customSpeedLabel.setId("customSpeedValue");
        VBox.setMargin(customSpeedLabel, new Insets(10, 0, 20, 0));

        playbackSpeedController.playbackSettingsController.playbackSettingsPane.getChildren().add(customSpeedBox);

        focusNodes.add(backButton);
        focusNodes.add(customSpeedSlider);

    }

    public void closeCustomSpeed(){
        if(playbackSpeedController.playbackSettingsController.animating.get()) return;

        playbackSpeedController.playbackSettingsController.playbackSettingsState = PlaybackSettingsState.PLAYBACK_SPEED_OPEN;

        playbackSpeedController.playbackSpeedPane.scrollPane.setVisible(true);
        playbackSpeedController.playbackSpeedPane.scrollPane.setMouseTransparent(false);

        Timeline clipWidthTimeline = new Timeline();
        clipWidthTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(playbackSpeedController.playbackSettingsController.clip.widthProperty(), playbackSpeedController.playbackSpeedPane.scrollPane.getWidth())));

        Timeline clipHeightTimeline = new Timeline();
        clipHeightTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(playbackSpeedController.playbackSettingsController.clip.heightProperty(), playbackSpeedController.playbackSpeedPane.scrollPane.getHeight())));

        TranslateTransition customTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), customSpeedBox);
        customTransition.setFromX(0);
        customTransition.setToX(playbackSpeedController.playbackSpeedPane.scrollPane.getWidth());

        TranslateTransition speedTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), playbackSpeedController.playbackSpeedPane.scrollPane);
        speedTransition.setFromX(-playbackSpeedController.playbackSpeedPane.scrollPane.getWidth());
        speedTransition.setToX(0);


        ParallelTransition parallelTransition = new ParallelTransition(clipWidthTimeline, clipHeightTimeline, customTransition, speedTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            playbackSpeedController.playbackSettingsController.animating.set(false);
            customSpeedBox.setVisible(false);
            customSpeedBox.setMouseTransparent(true);
            customSpeedBox.setTranslateX(0);
            playbackSpeedController.playbackSettingsController.clip.setHeight(playbackSpeedController.playbackSpeedPane.scrollPane.getPrefHeight());
        });

        parallelTransition.play();
        playbackSpeedController.playbackSettingsController.animating.set(true);

    }

    public void focusForward(){
        int newFocus;

        if(focus.get() >= 1 || focus.get() == -1) newFocus = 0;
        else newFocus = focus.get() + 1;

        keyboardFocusOn(focusNodes.get(newFocus));
    }

    public void focusBackward(){
        int newFocus;

        if(focus.get() == 0) newFocus = 1;
        else if(focus.get() == -1) newFocus = 0;
        else newFocus = focus.get() - 1;

        keyboardFocusOn(focusNodes.get(newFocus));
    }
}
