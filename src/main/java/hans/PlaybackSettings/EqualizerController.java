package hans.PlaybackSettings;

import hans.App;
import hans.SVG;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import javafx.animation.*;
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
import java.util.Arrays;

public class EqualizerController {

    PlaybackSettingsController playbackSettingsController;


    ScrollPane scrollPane = new ScrollPane();
    VBox equalizerBox = new VBox();

    HBox titleBox = new HBox();
    StackPane backIconPane = new StackPane();
    Region backIcon = new Region();
    SVGPath backSVG = new SVGPath();

    HBox titleLabelWrapper = new HBox();
    Label titleLabel = new Label();
    public ComboBox<String> comboBox = new ComboBox<>();


    HBox sliderBox = new HBox();

    VBox labelBox = new VBox();

    ArrayList<EqualizerSlider> sliders = new ArrayList<>();

    public boolean sliderActive = false;

    HBox checkBoxContainer = new HBox();
    MFXCheckbox checkbox = new MFXCheckbox("Move nearby sliders together");
    boolean moveSlidersTogether = true;

    static final double[] flatEQ = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    static final double[] classicalEQ = {0, 0, 0, 0, 0, 0, -7.2F, -7.2F, -7.2F, -9.6F};
    static final double[] clubEQ = {0, 0, 8, 5.6F, 5.6F, 5.6F, 3.2F, 0, 0, 0};
    static final double[] danceEQ = {9.6F, 7.2F, 2.4F, 0, 0, -5.6F, -7.2F, -7.2F, 0, 0};
    static final double[] fullBassEQ = {-8, 9.6F, 9.6F, 5.6F, 1.6F, -4, -8, -10.3F, -11.2F, -11.2F};
    static final double[] fullTrebleEQ = {-9.6F, -9.6F, -9.6F, -4, 2.4F, 11.2F, 16, 16, 16, 16.7F};
    static final double[] headphonesEQ = {4.8F, 11.2F, 5.6F, -3.2F, -2.4f, 1.6F, 4.8F, 9.6F, 12.8F, 14.4F};
    static final double[] largeHallEQ = {10.3F, 10.3F, 5.6F, 5.6F, 0, -4.8F, -4.8F, -4.8F, 0, 0};
    static final double[] liveEQ = {-4.8F, 0, 4, 5.6F, 5.6F, 5.6F, 4, 2.4F, 2.4F, 2.4F};
    static final double[] partyEQ = {7.2F, 7.2F, 0, 0, 0, 0, 0, 0, 7.2F, 7.2F};
    static final double[] popEQ = {-1.6F, 4.8F, 7.2F, 8, 5.6F, 0, -2.4F, -2.4F, -1.6F, -1.6F};
    static final double[] rockEQ = {8, 4.8F, -5.6F, -8, -3.2F, 4, 8.8F, 11.2F, 11.2F, 11.2F};
    static final double[] softEQ = {4.8F, 1.6F, 0, -2.4F, 0, 4, 8, 9.6F, 11.2F, 12};
    static final double[] technoEQ = {8, 5.6F, 0, -5.6F, -4.8F, 0, 8, 9.6F, 9.6F, 8.8F};

    static final String FLAT = "flat";
    static final String CLASSICAL = "classical";
    static final String CLUB = "club";
    static final String DANCE = "dance";
    static final String FULL_BASS = "full_bass";
    static final String FULL_TREBLE = "full_treble";
    static final String HEADPHONES = "headphones";
    static final String LARGE_HALL = "large_hall";
    static final String LIVE = "live";
    static final String PARTY = "party";
    static final String POP = "pop";
    static final String ROCK = "rock";
    static final String SOFT = "soft";
    static final String TECHNO = "techno";
    static final String CUSTOM = "custom";



    //preferences key constants:

    public static final String EQUALIZER_PRESET = "eq_preset";
    public static final String EQUALIZER_BAND1 = "eq_band1";
    public static final String EQUALIZER_BAND2 = "eq_band2";
    public static final String EQUALIZER_BAND3 = "eq_band3";
    public static final String EQUALIZER_BAND4 = "eq_band4";
    public static final String EQUALIZER_BAND5 = "eq_band5";
    public static final String EQUALIZER_BAND6 = "eq_band6";
    public static final String EQUALIZER_BAND7 = "eq_band7";
    public static final String EQUALIZER_BAND8 = "eq_band8";
    public static final String EQUALIZER_BAND9 = "eq_band9";
    public static final String EQUALIZER_BAND10 = "eq_band10";

    public static final String EQUALIZER_MOVE_SLIDERS_TOGETHER = "eq_move_sliders_together";


    EqualizerController(PlaybackSettingsController playbackSettingsController){
        this.playbackSettingsController = playbackSettingsController;


        backSVG.setContent(App.svgMap.get(SVG.CHEVRON_LEFT));

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("settingsScroll");
        scrollPane.setPrefSize(550, 323);
        scrollPane.setMaxSize(550, 323);
        scrollPane.setContent(equalizerBox);
        scrollPane.setVisible(false);
        scrollPane.setMouseTransparent(true);
        scrollPane.setFitToWidth(true);

        StackPane.setAlignment(scrollPane, Pos.BOTTOM_RIGHT);

        equalizerBox.setPrefSize(550, 320);
        equalizerBox.setMaxSize(550, 320);
        equalizerBox.setPadding(new Insets(0, 0, 8, 0));
        equalizerBox.getChildren().addAll(titleBox, sliderBox, checkBoxContainer);
        equalizerBox.setAlignment(Pos.TOP_LEFT);
        equalizerBox.setFillWidth(true);

        titleBox.setPrefSize(550, 48);
        titleBox.setMaxSize(550, 48);
        titleBox.setPadding(new Insets(0, 10, 0, 10));
        VBox.setMargin(titleBox, new Insets(0, 0, 10, 0));

        titleBox.getStyleClass().add("settingsPaneTitle");
        titleBox.getChildren().addAll(titleLabelWrapper, comboBox);
        titleBox.setAlignment(Pos.CENTER_LEFT);

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

        StackPane.setAlignment(titleLabelWrapper, Pos.CENTER_LEFT);
        titleLabelWrapper.getChildren().addAll(backIconPane, titleLabel);
        titleLabelWrapper.setAlignment(Pos.CENTER_LEFT);
        titleLabelWrapper.setPrefWidth(365);

        titleLabel.setText("Equalizer");
        titleLabel.setCursor(Cursor.HAND);
        titleLabel.getStyleClass().add("settingsPaneText");
        titleLabel.setOnMouseClicked((e) -> closeEqualizer());





        sliderBox.setPrefSize(535, 200);
        sliderBox.setMaxSize(535, 200);
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
            switch(i){
                case 0 -> sliders.add(new EqualizerSlider(this, "50 Hz"));
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



        StackPane.setAlignment(comboBox, Pos.CENTER_RIGHT);
        comboBox.getItems().addAll("Flat", "Classical", "Club", "Dance", "Full bass", "Full treble", "Headphones", "Large hall", "Live", "Party", "Pop", "Rock", "Soft", "Techno", "Custom");
        comboBox.setPrefSize(150, 35);
        comboBox.setMaxSize(150, 35);
        comboBox.setVisibleRowCount(5);
        comboBox.setId("equalizerCombo");

        comboBox.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            switch(newValue){
                case "Flat" -> applyPreset(flatEQ, FLAT);
                case "Classical" -> applyPreset(classicalEQ, CLASSICAL);
                case "Club" -> applyPreset(clubEQ, CLUB);
                case "Dance" -> applyPreset(danceEQ, DANCE);
                case "Full bass" -> applyPreset(fullBassEQ, FULL_BASS);
                case "Full treble" -> applyPreset(fullTrebleEQ, FULL_TREBLE);
                case "Headphones" -> applyPreset(headphonesEQ, HEADPHONES);
                case "Large hall" -> applyPreset(largeHallEQ, LARGE_HALL);
                case "Live" -> applyPreset(liveEQ, LIVE);
                case "Party" -> applyPreset(partyEQ, PARTY);
                case "Pop" -> applyPreset(popEQ, POP);
                case "Rock" -> applyPreset(rockEQ, ROCK);
                case "Soft" -> applyPreset(softEQ, SOFT);
                case "Techno" -> applyPreset(technoEQ, TECHNO);
                case "Custom" -> playbackSettingsController.mainController.pref.preferences.put(EQUALIZER_PRESET, CUSTOM);
            }
        });

        checkBoxContainer.getChildren().add(checkbox);
        checkBoxContainer.setPadding(new Insets(20, 0, 0, 40));
        moveSlidersTogether = playbackSettingsController.mainController.pref.preferences.getBoolean(EQUALIZER_MOVE_SLIDERS_TOGETHER, true);
        checkbox.setSelected(moveSlidersTogether);


        checkbox.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            moveSlidersTogether = newValue;
            playbackSettingsController.mainController.pref.preferences.putBoolean(EQUALIZER_MOVE_SLIDERS_TOGETHER, moveSlidersTogether);
        });


        playbackSettingsController.playbackSettingsPane.getChildren().add(scrollPane);
    }

    public void closeEqualizer(){
        if(playbackSettingsController.animating.get()) return;

        playbackSettingsController.playbackSettingsState = PlaybackSettingsState.HOME_OPEN;

        playbackSettingsController.playbackSettingsHomeController.playbackSettingsHome.setVisible(true);
        playbackSettingsController.playbackSettingsHomeController.playbackSettingsHome.setMouseTransparent(false);

        Timeline clipHeightTimeline = new Timeline();
        clipHeightTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(playbackSettingsController.clip.heightProperty(), playbackSettingsController.playbackSettingsHomeController.playbackSettingsHome.getHeight())));

        Timeline clipWidthTimeline = new Timeline();
        clipWidthTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(playbackSettingsController.clip.widthProperty(), playbackSettingsController.playbackSettingsHomeController.playbackSettingsHome.getWidth())));


        TranslateTransition homeTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), playbackSettingsController.playbackSettingsHomeController.playbackSettingsHome);
        homeTransition.setFromX(-scrollPane.getWidth());
        homeTransition.setToX(0);

        TranslateTransition speedTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), scrollPane);
        speedTransition.setFromX(0);
        speedTransition.setToX(scrollPane.getWidth());


        ParallelTransition parallelTransition = new ParallelTransition(clipHeightTimeline, clipWidthTimeline, homeTransition, speedTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            playbackSettingsController.animating.set(false);
            scrollPane.setVisible(false);
            scrollPane.setMouseTransparent(true);
            scrollPane.setTranslateX(0);
        });

        parallelTransition.play();
        playbackSettingsController.animating.set(true);
    }


    public void loadEqualizer(){
        String preset = playbackSettingsController.mainController.pref.preferences.get(EQUALIZER_PRESET, FLAT);

        if(!preset.equals(CUSTOM)){
            switch(preset){
                case FLAT -> comboBox.setValue("Flat");
                case CLASSICAL -> comboBox.setValue("Classical");
                case CLUB -> comboBox.setValue("Club");
                case DANCE -> comboBox.setValue("Dance");
                case FULL_BASS -> comboBox.setValue("Full bass");
                case FULL_TREBLE -> comboBox.setValue("Full treble");
                case HEADPHONES -> comboBox.setValue("Headphones");
                case LARGE_HALL -> comboBox.setValue("Large hall");
                case LIVE -> comboBox.setValue("Live");
                case PARTY -> comboBox.setValue("Party");
                case POP -> comboBox.setValue("Pop");
                case ROCK -> comboBox.setValue("Rock");
                case SOFT -> comboBox.setValue("Soft");
                case TECHNO -> comboBox.setValue("Techno");
            }
        }
        else {
            double[] amps = {
                    playbackSettingsController.mainController.pref.preferences.getDouble(EQUALIZER_BAND1, 0),
                    playbackSettingsController.mainController.pref.preferences.getDouble(EQUALIZER_BAND2, 0),
                    playbackSettingsController.mainController.pref.preferences.getDouble(EQUALIZER_BAND3, 0),
                    playbackSettingsController.mainController.pref.preferences.getDouble(EQUALIZER_BAND4, 0),
                    playbackSettingsController.mainController.pref.preferences.getDouble(EQUALIZER_BAND5, 0),
                    playbackSettingsController.mainController.pref.preferences.getDouble(EQUALIZER_BAND6, 0),
                    playbackSettingsController.mainController.pref.preferences.getDouble(EQUALIZER_BAND7, 0),
                    playbackSettingsController.mainController.pref.preferences.getDouble(EQUALIZER_BAND8, 0),
                    playbackSettingsController.mainController.pref.preferences.getDouble(EQUALIZER_BAND9, 0),
                    playbackSettingsController.mainController.pref.preferences.getDouble(EQUALIZER_BAND10, 0)
            };

            applyPreset(amps, CUSTOM);
        }
    }


    public void applyPreset(double[] preset, String name){
        for(int i = 0; i < sliders.size(); i++){
            sliders.get(i).slider.setValue(preset[i]);
        }

        playbackSettingsController.mainController.pref.preferences.put(EQUALIZER_PRESET, name);

        float[] floatArray = new float[preset.length];
        for (int i = 0 ; i < preset.length; i++)
        {
            floatArray[i] = (float) preset[i];
        }

        playbackSettingsController.mediaInterface.embeddedMediaPlayer.audio().equalizer().setAmps(floatArray);
    }
}
