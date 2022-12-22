package hans.Settings;

import hans.App;
import hans.SVG;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import javafx.animation.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

import java.util.ArrayList;

public class EqualizerController {

    SettingsController settingsController;


    ScrollPane scrollPane = new ScrollPane();
    VBox equalizerBox = new VBox();

    HBox titleBox = new HBox();
    StackPane backIconPane = new StackPane();
    Region backIcon = new Region();
    SVGPath backSVG = new SVGPath();

    HBox titleLabelWrapper = new HBox();
    Label titleLabel = new Label();
    ComboBox<String> comboBox = new ComboBox<>();


    HBox sliderBox = new HBox();

    VBox labelBox = new VBox();

    ArrayList<EqualizerSlider> sliders = new ArrayList<>();

    public boolean sliderActive = false;

    HBox checkBoxContainer = new HBox();
    MFXCheckbox checkbox = new MFXCheckbox("Move nearby sliders together");
    boolean moveSlidersTogether = true;

    final float[] flatEQ = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    final float[] classicalEQ = {0, 0, 0, 0, 0, 0, -7.2F, -7.2F, -7.2F, -9.6F};
    final float[] clubEQ = {0, 0, 8, 5.6F, 5.6F, 5.6F, 3.2F, 0, 0, 0};
    final float[] danceEQ = {9.6F, 7.2F, 2.4F, 0, 0, -5.6F, -7.2F, -7.2F, 0, 0};
    final float[] fullBassEQ = {-8, 9.6F, 9.6F, 5.6F, 1.6F, -4, -8, -10.3F, -11.2F, -11.2F};
    final float[] fullTrebleEQ = {-9.6F, -9.6F, -9.6F, -4, 2.4F, 11.2F, 16, 16, 16, 16.7F};
    final float[] headphonesEQ = {4.8F, 11.2F, 5.6F, -3.2F, -2.4f, 1.6F, 4.8F, 9.6F, 12.8F, 14.4F};
    final float[] largeHallEQ = {10.3F, 10.3F, 5.6F, 5.6F, 0, -4.8F, -4.8F, -4.8F, 0, 0};
    final float[] liveEQ = {-4.8F, 0, 4, 5.6F, 5.6F, 5.6F, 4, 2.4F, 2.4F, 2.4F};
    final float[] partyEQ = {7.2F, 7.2F, 0, 0, 0, 0, 0, 0, 7.2F, 7.2F};
    final float[] popEQ = {-1.6F, 4.8F, 7.2F, 8, 5.6F, 0, -2.4F, -2.4F, -1.6F, -1.6F};
    final float[] rockEQ = {8, 4.8F, -5.6F, -8, -3.2F, 4, 8.8F, 11.2F, 11.2F, 11.2F};
    final float[] softEQ = {4.8F, 1.6F, 0, -2.4F, 0, 4, 8, 9.6F, 11.2F, 12};
    final float[] technoEQ = {8, 5.6F, 0, -5.6F, -4.8F, 0, 8, 9.6F, 9.6F, 8.8F};


    EqualizerController(SettingsController settingsController){
        this.settingsController = settingsController;


        backSVG.setContent(App.svgMap.get(SVG.CHEVRON_LEFT));

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("settingsScroll");
        scrollPane.setPrefSize(550, 323);
        scrollPane.setMaxSize(550, 323);
        scrollPane.setContent(equalizerBox);
        scrollPane.setVisible(false);
        scrollPane.setMouseTransparent(true);

        StackPane.setAlignment(scrollPane, Pos.BOTTOM_RIGHT);
        equalizerBox.setAlignment(Pos.BOTTOM_CENTER);


        equalizerBox.setMinSize(550, 320);
        equalizerBox.setPrefSize(550, 320);
        equalizerBox.setMaxSize(550, 320);
        equalizerBox.setPadding(new Insets(8, 0, 8, 0));
        equalizerBox.getChildren().addAll(titleBox, sliderBox, checkBoxContainer);
        equalizerBox.setAlignment(Pos.TOP_CENTER);

        titleBox.setMinSize(550, 40);
        titleBox.setPrefSize(550, 40);
        titleBox.setMaxSize(550, 40);
        titleBox.setPadding(new Insets(0, 10, 0, 10));
        VBox.setMargin(titleBox, new Insets(0, 0, 10, 0));

        titleBox.getStyleClass().add("settingsPaneTitle");
        titleBox.getChildren().addAll(backIconPane, titleLabelWrapper, comboBox);

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

        titleLabelWrapper.setAlignment(Pos.CENTER_LEFT);
        titleLabelWrapper.setMinSize(340, 40);
        titleLabelWrapper.setPrefSize(340, 40);
        titleLabelWrapper.setMaxSize(340, 40);
        titleLabelWrapper.getChildren().add(titleLabel);

        titleLabel.setText("Equalizer");
        titleLabel.setCursor(Cursor.HAND);
        titleLabel.getStyleClass().add("settingsPaneText");
        titleLabel.setOnMouseClicked((e) -> closeEqualizer());

        comboBox.getItems().addAll("Flat", "Classical", "Club", "Dance", "Full bass", "Full treble", "Headphones", "Large hall", "Live", "Party", "Pop", "Rock", "Soft", "Techno", "Custom");
        comboBox.setMinSize(150, 35);
        comboBox.setPrefSize(150, 35);
        comboBox.setMaxSize(150, 35);
        comboBox.setVisibleRowCount(5);
        comboBox.setValue("Flat");

        comboBox.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            switch(newValue){
                case "Flat" -> applyPreset(flatEQ);
                case "Classical" -> applyPreset(classicalEQ);
                case "Club" -> applyPreset(clubEQ);
                case "Dance" -> applyPreset(danceEQ);
                case "Full bass" -> applyPreset(fullBassEQ);
                case "Full treble" -> applyPreset(fullTrebleEQ);
                case "Headphones" -> applyPreset(headphonesEQ);
                case "Large hall" -> applyPreset(largeHallEQ);
                case "Live" -> applyPreset(liveEQ);
                case "Party" -> applyPreset(partyEQ);
                case "Pop" -> applyPreset(popEQ);
                case "Rock" -> applyPreset(rockEQ);
                case "Soft" -> applyPreset(softEQ);
                case "Techno" -> applyPreset(technoEQ);
            }
        });


        sliderBox.setMinSize(550, 200);
        sliderBox.setPrefSize(550, 200);
        sliderBox.setMaxSize(550, 200);
        sliderBox.setPadding(new Insets(0, 5, 5, 5));
        sliderBox.setAlignment(Pos.CENTER);

        sliderBox.getChildren().add(labelBox);

        labelBox.setMinWidth(50);
        labelBox.setSpacing(19);
        labelBox.setAlignment(Pos.TOP_RIGHT);

        Label label1 = new Label("+20 dB");
        label1.setPrefHeight(20);
        label1.getStyleClass().add("equalizer-label");

        Label label2 = new Label("+10 dB");
        label2.setPrefHeight(20);
        label2.getStyleClass().add("equalizer-label");

        Label label3 = new Label("0 dB");
        label3.setPrefHeight(20);
        label3.getStyleClass().add("equalizer-label");

        Label label4 = new Label("-10 dB");
        label4.setPrefHeight(20);
        label4.getStyleClass().add("equalizer-label");

        Label label5 = new Label("-20 dB");
        label5.setPrefHeight(20);
        label5.getStyleClass().add("equalizer-label");

        labelBox.getChildren().addAll(label1, label2, label3, label4,label5);

        for(int i = 0; i < 10; i++){
            switch(i){                case 0 -> sliders.add(new EqualizerSlider(this, "50 Hz"));
                case 1 -> sliders.add(new EqualizerSlider(this, "170 Hz"));
                case 2 -> sliders.add(new EqualizerSlider(this, "310 Hz"));
                case 3 -> sliders.add(new EqualizerSlider(this, "500 Hz"));
                case 4 -> sliders.add(new EqualizerSlider(this, "1 KHz"));
                case 5 -> sliders.add(new EqualizerSlider(this, "3 KHz"));
                case 6 -> sliders.add(new EqualizerSlider(this, "5 KHz"));
                case 7 -> sliders.add(new EqualizerSlider(this, "12 KHz"));
                case 8 -> sliders.add(new EqualizerSlider(this, "14 KHz"));
                case 9 -> sliders.add(new EqualizerSlider(this, "16 KHz"));
            }
        }

        checkBoxContainer.getChildren().add(checkbox);
        checkBoxContainer.setPadding(new Insets(20, 0, 0, 40));
        checkbox.setSelected(true);
        checkbox.selectedProperty().addListener((observableValue, oldValue, newValue) -> moveSlidersTogether = newValue);


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
        homeTransition.setFromX(-scrollPane.getWidth());
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


    public void applyPreset(float[] preset){
        for(int i = 0; i < sliders.size(); i++){
            sliders.get(i).slider.setValue(preset[i]);
        }

        settingsController.mediaInterface.embeddedMediaPlayer.audio().equalizer().setAmps(preset);
    }
}
