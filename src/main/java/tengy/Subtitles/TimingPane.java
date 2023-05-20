package tengy.Subtitles;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
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
import tengy.ControlTooltip;
import tengy.PlaybackSettings.PlaybackSettingsController;
import tengy.SVG;
import tengy.Subtitles.Tasks.SubtitleTimingTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static tengy.Utilities.keyboardFocusOff;
import static tengy.Utilities.keyboardFocusOn;

public class TimingPane {

    VBox container = new VBox();

    HBox titlePane = new HBox();

    Button backButton = new Button();
    Region backIcon = new Region();
    Label titleLabel = new Label();
    SVGPath backSVG = new SVGPath();

    StackPane sliderPane = new StackPane();
    public Slider slider = new Slider();
    ProgressBar sliderTrack = new ProgressBar();

    HBox textFieldContainer = new HBox();
    public TextField textField = new TextField();
    Label label = new Label("s");

    HBox saveButtonContainer = new HBox();
    public Button saveButton = new Button("Save");
    ControlTooltip saveButtonTooltip = null;

    SubtitlesHome subtitlesHome;
    SubtitlesController subtitlesController;

    int MAX_WIDTH = 150;

    List<Node> focusNodes = new ArrayList<>();
    IntegerProperty focus = new SimpleIntegerProperty(-1);


    TimingPane(SubtitlesHome subtitlesHome, SubtitlesController subtitlesController){
        this.subtitlesHome = subtitlesHome;
        this.subtitlesController = subtitlesController;

        backSVG.setContent(SVG.CHEVRON_LEFT.getContent());

        container.setPrefSize(235, 185);
        container.setMaxSize(235, 185);
        container.getChildren().addAll(titlePane, sliderPane, textFieldContainer, saveButtonContainer);
        container.setAlignment(Pos.BOTTOM_CENTER);
        StackPane.setAlignment(container, Pos.BOTTOM_RIGHT);

        container.setVisible(false);
        container.setMouseTransparent(true);
        container.setOnMouseClicked(e -> container.requestFocus());

        titlePane.setMinSize(235, 48);
        titlePane.setPrefSize(235, 48);
        titlePane.setMaxSize(235, 48);
        titlePane.setPadding(new Insets(0, 10, 0, 10));
        VBox.setMargin(titlePane, new Insets(0, 0, 15, 0));

        titlePane.getStyleClass().add("settingsPaneTitle");
        titlePane.getChildren().addAll(backButton, titleLabel);
        titlePane.setAlignment(Pos.CENTER_LEFT);

        backButton.setMinSize(30, 40);
        backButton.setPrefSize(30, 40);
        backButton.setMaxSize(30, 40);
        backButton.getStyleClass().addAll("transparentButton", "settingsMenuButton");
        backButton.setGraphic(backIcon);
        backButton.setOnAction((e) -> closeSubtitleTiming());
        backButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue) focus.set(0);
            else {
                keyboardFocusOff(backButton);
                focus.set(-1);
            }
        });
        backButton.setFocusTraversable(false);

        backButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            backButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        backButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            backButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });

        backIcon.setMinSize(8, 13);
        backIcon.setPrefSize(8, 13);
        backIcon.setMaxSize(8, 13);
        backIcon.getStyleClass().add("graphic");
        backIcon.setShape(backSVG);

        titleLabel.setMinHeight(40);
        titleLabel.setPrefHeight(40);
        titleLabel.setMaxHeight(40);
        titleLabel.setText("Subtitle timing");
        titleLabel.setPadding(new Insets(0, 0, 0, 4));
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
        slider.setBlockIncrement(0.25);
        slider.setMinWidth(150);
        slider.setPrefWidth(150);
        slider.setMaxWidth(150);
        slider.setFocusTraversable(false);

        slider.valueProperty().addListener((observableValue, oldValue, newValue) -> {

            double progress = (newValue.doubleValue() + 10) / 20;

            sliderTrack.setProgress(progress);

            if(slider.isValueChanging()){
                textField.setText(String.format("%.2f", newValue.doubleValue()));
            }
        });


        slider.valueChangingProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                textField.setText(String.format("%.2f", slider.getValue()));
                return;
            }

            subtitlesController.subtitleDelay = (int) (-slider.getValue() * 1000);
            if(textField.getText().equals("-0.00")) textField.setText("0.00");
            saveButton.setDisable(textField.getText().equals("0.00") || !subtitlesController.subtitlesSelected.get());

        });

        slider.setOnMousePressed((e) -> slider.setValueChanging(true));
        slider.setOnMouseReleased((e) -> slider.setValueChanging(false));
        slider.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                sliderTrack.pseudoClassStateChanged(PseudoClass.getPseudoClass("focused"), true);
                focus.set(1);
            }
            else {
                sliderTrack.pseudoClassStateChanged(PseudoClass.getPseudoClass("focused"), false);
                focus.set(-1);
                keyboardFocusOff(slider);
                slider.setValueChanging(false);
            }
        });

        slider.setOnMouseEntered(e -> sliderTrack.pseudoClassStateChanged(PseudoClass.getPseudoClass("hover"), true));
        slider.setOnMouseExited(e -> sliderTrack.pseudoClassStateChanged(PseudoClass.getPseudoClass("hover"), false));


        textFieldContainer.getChildren().addAll(textField, label);
        textFieldContainer.setAlignment(Pos.CENTER);
        textFieldContainer.setSpacing(5);
        VBox.setMargin(textFieldContainer, new Insets(10, 0, 0, 0));

        textField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if(!newValue.matches("-?\\d*\\.?\\d*")) textField.setText(oldValue);
        });

        textField.setFocusTraversable(false);

        textField.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.ENTER){
                try{
                    double number = Double.parseDouble(textField.getText());
                    slider.setValue(number);
                    textField.setText(String.format("%.2f", number));
                    if(textField.getText().equals("-0.00")) textField.setText("0.00");
                    subtitlesController.subtitleDelay = (int) (-number * 1000);
                    saveButton.setDisable(textField.getText().equals("0.00")|| !subtitlesController.subtitlesSelected.get());
                }
                catch(NumberFormatException ex){
                    slider.setValue(0);
                    textField.setText("0.00");
                    saveButton.setDisable(true);
                }
            }
        });

        textField.focusedProperty().addListener((observableValue, oldValue, newValue) -> {

            if(newValue){
                focus.set(2);
            }
            else {
                keyboardFocusOff(textField);
                focus.set(-1);
                try{
                    double number = Double.parseDouble(textField.getText());
                    slider.setValue(number);
                    textField.setText(String.valueOf(number));
                    subtitlesController.subtitleDelay = (int) (-number * 1000);
                    saveButton.setDisable(number == 0 || !subtitlesController.subtitlesSelected.get());
                }
                catch(NumberFormatException ex){
                    slider.setValue(0);
                    textField.setText("0.00");
                    saveButton.setDisable(true);
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

        textField.setText("0.00");

        textField.setMaxWidth(MAX_WIDTH);
        textField.getStyleClass().add("customTextField");
        textField.setId("timingTextField");
        label.getStyleClass().add("settingsPaneText");
        label.setId("subtitleDelayLabel");

        saveButtonContainer.getChildren().add(saveButton);
        saveButtonContainer.setAlignment(Pos.CENTER_RIGHT);
        saveButtonContainer.setPadding(new Insets(5, 20, 10, 0));

        saveButton.getStyleClass().add("menuButton");
        saveButton.setId("timingSaveButton");
        saveButton.setCursor(Cursor.HAND);
        saveButton.setOnAction(e -> saveToFile());
        saveButton.setDisable(true);
        saveButton.setFocusTraversable(false);
        saveButton.disableProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue) focusNodes.remove(saveButton);
            else {
                if(!focusNodes.contains(saveButton)) focusNodes.add(saveButton);
            }
        });

        saveButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            saveButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        saveButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            saveButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });

        saveButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(3);
            }
            else {
                keyboardFocusOff(saveButton);
                focus.set(-1);
            }
        });


        subtitlesController.subtitlesPane.getChildren().add(container);

        Platform.runLater(() -> saveButtonTooltip = new ControlTooltip(subtitlesController.mainController, "Save changes to active subtitle file", "", saveButton, 1000));


        focusNodes.add(backButton);
        focusNodes.add(slider);
        focusNodes.add(textField);

    }

    public void closeSubtitleTiming(){
        if(subtitlesController.animating.get()) return;

        subtitlesController.subtitlesState = SubtitlesState.HOME_OPEN;

        subtitlesController.subtitlesHome.scrollPane.setVisible(true);
        subtitlesController.subtitlesHome.scrollPane.setMouseTransparent(false);


        Timeline clipHeightTimeline = new Timeline();
        clipHeightTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(subtitlesController.clip.heightProperty(), subtitlesController.subtitlesHome.scrollPane.getHeight())));


        Timeline clipWidthTimeline = new Timeline();
        clipWidthTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(subtitlesController.clip.widthProperty(), subtitlesController.subtitlesHome.scrollPane.getWidth())));


        TranslateTransition captionsPaneTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), subtitlesController.subtitlesHome.scrollPane);
        captionsPaneTransition.setFromX(-container.getWidth());
        captionsPaneTransition.setToX(0);

        TranslateTransition timingTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), container);
        timingTransition.setFromX(0);
        timingTransition.setToX(container.getWidth());


        ParallelTransition parallelTransition = new ParallelTransition(clipHeightTimeline, clipWidthTimeline, captionsPaneTransition, timingTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            subtitlesController.animating.set(false);
            container.setVisible(false);
            container.setMouseTransparent(true);
            container.setTranslateX(0);
        });

        parallelTransition.play();
        subtitlesController.animating.set(true);
    }

    public void saveToFile(){
        if(slider.getValue() == 0 || !subtitlesController.subtitlesSelected.get()) return;
        SubtitleTimingTask subtitleTimingTask = new SubtitleTimingTask(subtitlesController);
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(subtitleTimingTask);
        executorService.shutdown();
    }

    public void resetTiming(){
        subtitlesController.subtitleDelay = 0;
        slider.setValue(0);
        textField.setText("0.00");
        saveButton.setDisable(true);
    }

    public void focusForward(){
        int newFocus;

        if(focus.get() >= focusNodes.size() - 1 || focus.get() == -1) newFocus = 0;
        else newFocus = focus.get() + 1;

        keyboardFocusOn(focusNodes.get(newFocus));
    }

    public void focusBackward(){
        int newFocus;

        if(focus.get() == 0) newFocus = focusNodes.size() - 1;
        else if(focus.get() == -1) newFocus = 0;
        else newFocus = focus.get() - 1;

        keyboardFocusOn(focusNodes.get(newFocus));
    }
}
