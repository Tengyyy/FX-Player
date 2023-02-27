package hans.Captions;

import hans.*;
import hans.Settings.SettingsController;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class TimingPane {

    VBox container = new VBox();

    HBox titlePane = new HBox();
    StackPane backIconPane = new StackPane();
    Region backIcon = new Region();
    Label titleLabel = new Label();
    SVGPath backSVG = new SVGPath();

    StackPane sliderPane = new StackPane();
    public Slider slider = new Slider();
    ProgressBar sliderTrack = new ProgressBar();

    HBox textFieldContainer = new HBox();
    TextField textField = new TextField();
    Label label = new Label("s");

    CaptionsHome captionsHome;
    CaptionsController captionsController;

    int MAX_WIDTH = 150;


    TimingPane(CaptionsHome captionsHome, CaptionsController captionsController){
        this.captionsHome = captionsHome;
        this.captionsController = captionsController;

        backSVG.setContent(App.svgMap.get(SVG.CHEVRON_LEFT));

        container.setPrefSize(235, 150);
        container.setMaxSize(235, 150);
        container.getChildren().addAll(titlePane, sliderPane, textFieldContainer);
        container.setAlignment(Pos.BOTTOM_CENTER);
        StackPane.setAlignment(container, Pos.BOTTOM_RIGHT);

        container.setVisible(false);
        container.setMouseTransparent(true);

        titlePane.setMinSize(235, 40);
        titlePane.setPrefSize(235, 40);
        titlePane.setMaxSize(235, 40);
        titlePane.setPadding(new Insets(0, 10, 0, 10));
        VBox.setMargin(titlePane, new Insets(0, 0, 20, 0));

        titlePane.getStyleClass().add("settingsPaneTitle");
        titlePane.getChildren().addAll(backIconPane, titleLabel);

        backIconPane.setMinSize(24, 40);
        backIconPane.setPrefSize(24, 40);
        backIconPane.setMaxSize(24, 40);
        backIconPane.setCursor(Cursor.HAND);
        backIconPane.getChildren().add(backIcon);
        backIconPane.setOnMouseClicked((e) -> closeSubtitleTiming());

        backIcon.setMinSize(8, 13);
        backIcon.setPrefSize(8, 13);
        backIcon.setMaxSize(8, 13);
        backIcon.getStyleClass().add("settingsPaneIcon");
        backIcon.setShape(backSVG);

        titleLabel.setMinHeight(40);
        titleLabel.setPrefHeight(40);
        titleLabel.setMaxHeight(40);
        titleLabel.setText("Subtitle timing");
        titleLabel.setCursor(Cursor.HAND);
        titleLabel.getStyleClass().add("settingsPaneText");
        titleLabel.setOnMouseClicked((e) -> closeSubtitleTiming());

        sliderPane.setMinSize(235, 30);
        sliderPane.setPrefSize(235, 30);
        sliderPane.setMaxSize(235, 30);
        sliderPane.getChildren().addAll(sliderTrack, slider);

        sliderTrack.setMinSize(138, 7);
        sliderTrack.setPrefSize(138,7);
        sliderTrack.setMaxSize(138, 7);
        sliderTrack.setProgress(0.75/1.75);
        sliderTrack.getStyleClass().add("customSliderTrack");

        slider.getStyleClass().add("customSlider");
        slider.setMin(-10);
        slider.setMax(10);
        slider.setValue(0);
        slider.setBlockIncrement(0.05);
        slider.setMinWidth(150);
        slider.setPrefWidth(150);
        slider.setMaxWidth(150);

        slider.valueProperty().addListener((observableValue, oldValue, newValue) -> {

            double progress = (newValue.doubleValue() + 10) / 20;

            sliderTrack.setProgress(progress);

            if(slider.isValueChanging()){
                textField.setText(String.format("%.2f", newValue.doubleValue()));
            }
        });


        slider.valueChangingProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue) return;

            captionsController.subtitleDelay = (int) (-slider.getValue() * 1000);

        });

        slider.setOnMousePressed((e) -> slider.setValueChanging(true));
        slider.setOnMouseReleased((e) -> slider.setValueChanging(false));

        textFieldContainer.getChildren().addAll(textField, label);
        textFieldContainer.setAlignment(Pos.CENTER);
        textFieldContainer.setSpacing(10);
        VBox.setMargin(textFieldContainer, new Insets(10, 0, 20, 0));

        textField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if(!newValue.matches("\\-?\\d*\\.?\\d*")){
                textField.setText(oldValue);
            }
        });

        textField.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.ENTER){
                try{
                    double number = Double.parseDouble(textField.getText());
                    slider.setValue(number);
                    textField.setText(String.valueOf(number));
                    captionsController.subtitleDelay = (int) (-number * 1000);
                }
                catch(NumberFormatException ex){
                    slider.setValue(0);
                    textField.setText("0.0");
                }
            }
        });

        textField.focusedProperty().addListener((observableValue, oldValue, newValue) -> {

            if(!newValue){
                try{
                    double number = Double.parseDouble(textField.getText());
                    slider.setValue(number);
                    textField.setText(String.valueOf(number));
                    captionsController.subtitleDelay = (int) (-number * 1000);

                }
                catch(NumberFormatException ex){
                    slider.setValue(0);
                    textField.setText("0.0");
                }
            }

        });

        textField.textProperty().addListener((ov, prevText, currText) -> {
            // Do this in a Platform.runLater because of Textfield has no padding at first time and so on
            Platform.runLater(() -> {
                Text text = new Text(currText);
                text.setFont(new Font("Roboto Medium", 18)); // Set the same font, so the size is the same
                double width = text.getLayoutBounds().getWidth()
                        + textField.getPadding().getLeft() + textField.getPadding().getRight() // Add the padding of the TextField
                        + 2d; // Add some spacing
                textField.setPrefWidth(width); // Set the width
                textField.positionCaret(textField.getCaretPosition()); // If you remove this line the caret flashes in the wrong spot for a moment
            });
        });

        textField.setText("0.0");

        textField.setMaxWidth(MAX_WIDTH);
        textField.getStyleClass().add("key-text-field");
        label.getStyleClass().add("settingsPaneText");
        label.setId("subtitleDelayLabel");

        captionsController.captionsPane.getChildren().add(container);


    }

    public void closeSubtitleTiming(){
        if(captionsController.animating.get()) return;

        captionsController.captionsState = CaptionsState.HOME_OPEN;

        captionsController.captionsHome.scrollPane.setVisible(true);
        captionsController.captionsHome.scrollPane.setMouseTransparent(false);


        Timeline clipHeightTimeline = new Timeline();
        clipHeightTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.clip.heightProperty(), captionsController.captionsHome.scrollPane.getHeight())));


        Timeline clipWidthTimeline = new Timeline();
        clipWidthTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.clip.widthProperty(), captionsController.captionsHome.scrollPane.getWidth())));


        TranslateTransition captionsPaneTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), captionsController.captionsHome.scrollPane);
        captionsPaneTransition.setFromX(-container.getWidth());
        captionsPaneTransition.setToX(0);

        TranslateTransition timingTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), container);
        timingTransition.setFromX(0);
        timingTransition.setToX(container.getWidth());


        ParallelTransition parallelTransition = new ParallelTransition(clipHeightTimeline, clipWidthTimeline, captionsPaneTransition, timingTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            captionsController.animating.set(false);
            container.setVisible(false);
            container.setMouseTransparent(true);
            container.setTranslateX(0);
        });

        parallelTransition.play();
        captionsController.animating.set(true);
    }
}
