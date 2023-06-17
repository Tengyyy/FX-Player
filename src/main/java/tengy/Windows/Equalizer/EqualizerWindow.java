package tengy.Windows.Equalizer;

import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.animation.FadeTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import tengy.*;
import tengy.PlaybackSettings.PlaybackSettingsController;
import tengy.Windows.WindowController;
import tengy.Windows.WindowState;

import java.util.ArrayList;
import java.util.List;

import static tengy.Utilities.keyboardFocusOff;
import static tengy.Utilities.keyboardFocusOn;

public class EqualizerWindow {

    WindowController windowController;
    MainController mainController;
    PlaybackSettingsController playbackSettingsController;

    public StackPane window = new StackPane();

    public ScrollPane scrollPane;
    VBox content = new VBox();

    StackPane titleContainer = new StackPane();
    Label title = new Label("Equalizer");
    Label toggleLabel = new Label("On");
    MFXToggleButton toggle = new MFXToggleButton();

    StackPane buttonContainer = new StackPane();
    Button mainButton = new Button();


    HBox presetsWrapper = new HBox();
    Label presetLabel = new Label("Preset");

    public CustomMenuButton presetsButton = new CustomMenuButton();

    HBox sliderBox = new HBox();

    VBox labelBox = new VBox();

    EqualizerSlider preampSlider;
    public ArrayList<EqualizerSlider> sliders = new ArrayList<>();

    public boolean sliderActive = false;

    HBox checkBoxContainer = new HBox();
    CheckBox checkbox = new CheckBox();
    Label checkboxLabel = new Label("Move nearby sliders together");
    boolean moveSlidersTogether = true;

    static final double[] flatEQ = {12.0F, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    static final double[] classicalEQ = {12.0F, 0, 0, 0, 0, 0, 0, -7.2F, -7.2F, -7.2F, -9.6F};
    static final double[] clubEQ = {6.0F, 0, 0, 8, 5.6F, 5.6F, 5.6F, 3.2F, 0, 0, 0};
    static final double[] danceEQ = {5.0F, 9.6F, 7.2F, 2.4F, 0, 0, -5.6F, -7.2F, -7.2F, 0, 0};
    static final double[] fullBassEQ = {5.0F, -8, 9.6F, 9.6F, 5.6F, 1.6F, -4, -8, -10.3F, -11.2F, -11.2F};
    static final double[] fullTrebleEQ = {3.0F, -9.6F, -9.6F, -9.6F, -4, 2.4F, 11.2F, 16, 16, 16, 16.7F};
    static final double[] headphonesEQ = {4.0F, 4.8F, 11.2F, 5.6F, -3.2F, -2.4f, 1.6F, 4.8F, 9.6F, 12.8F, 14.4F};
    static final double[] largeHallEQ = {5.0F, 10.3F, 10.3F, 5.6F, 5.6F, 0, -4.8F, -4.8F, -4.8F, 0, 0};
    static final double[] liveEQ = {7.0F, -4.8F, 0, 4, 5.6F, 5.6F, 5.6F, 4, 2.4F, 2.4F, 2.4F};
    static final double[] partyEQ = {6.0F, 7.2F, 7.2F, 0, 0, 0, 0, 0, 0, 7.2F, 7.2F};
    static final double[] popEQ = {6.0F, -1.6F, 4.8F, 7.2F, 8, 5.6F, 0, -2.4F, -2.4F, -1.6F, -1.6F};
    static final double[] rockEQ = {5.0F, 8, 4.8F, -5.6F, -8, -3.2F, 4, 8.8F, 11.2F, 11.2F, 11.2F};
    static final double[] softEQ = {5.0F, 4.8F, 1.6F, 0, -2.4F, 0, 4, 8, 9.6F, 11.2F, 12};
    static final double[] technoEQ = {5.0F, 8, 5.6F, 0, -5.6F, -4.8F, 0, 8, 9.6F, 9.6F, 8.8F};

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
    public static final String EQUALIZER_PREAMP = "eq_preamp";
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
    public static final String EQUALIZER_ENABLED = "eq_enabled";


    public boolean showing = false;

    boolean togglePressed = false;
    

    IntegerProperty focus = new SimpleIntegerProperty(-1);
    List<Node> focusNodes = new ArrayList<>();

    public EqualizerWindow(WindowController windowController){

        this.windowController = windowController;
        this.mainController = windowController.mainController;
        this.playbackSettingsController = windowController.mainController.getPlaybackSettingsController();
        
        mainController.popupWindowContainer.getChildren().add(window);

        window.setAlignment(Pos.TOP_LEFT);
        window.setPrefWidth(670);
        window.setMaxWidth(670);
        window.prefHeightProperty().bind(Bindings.min(500, mainController.videoImageViewWrapper.heightProperty().multiply(0.9)));
        window.maxHeightProperty().bind(Bindings.min(500, mainController.videoImageViewWrapper.heightProperty().multiply(0.9)));

        window.getStyleClass().add("popupWindow");
        window.setVisible(false);
        window.setOnMouseClicked(e -> window.requestFocus());

        StackPane.setAlignment(titleContainer, Pos.TOP_CENTER);
        titleContainer.getChildren().addAll(title, toggleLabel, toggle);
        titleContainer.setPadding(new Insets(15, 25, 0, 25));
        titleContainer.setMaxHeight(80);


        StackPane.setAlignment(title, Pos.CENTER_LEFT);
        title.getStyleClass().add("popupWindowTitle");


        toggleLabel.getStyleClass().add("toggleText");
        toggleLabel.setPrefWidth(35);
        toggleLabel.setMouseTransparent(true);
        StackPane.setAlignment(toggleLabel, Pos.CENTER_RIGHT);
        StackPane.setMargin(toggleLabel, new Insets(0, 50, 0, 0));
        StackPane.setAlignment(toggle, Pos.CENTER_RIGHT);

        toggle.setRadius(10);
        toggle.setCursor(Cursor.HAND);
        toggle.setSelected(true);
        toggle.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(0);
            }
            else {
                keyboardFocusOff(toggle);
                focus.set(-1);
                togglePressed = false;
            }
        });

        toggle.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;

            togglePressed = true;

            e.consume();
        });

        toggle.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;

            if(togglePressed){
                toggle.fire();
            }

            togglePressed = false;

            e.consume();
        });

        toggle.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue) {
                toggleLabel.setText("On");
                enableEqualizer();
            }
            else {
                toggleLabel.setText("Off");
                disableEqualizer();
            }
        });

        scrollPane = new ScrollPane() {
            ScrollBar vertical;

            @Override
            protected void layoutChildren() {
                super.layoutChildren();
                if (vertical == null) {
                    vertical = (ScrollBar) lookup(".scroll-bar:vertical");
                    vertical.visibleProperty().addListener((obs, old, val) -> updatePadding(val));
                }
            }
        };

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("menuScroll");
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        scrollPane.setBackground(Background.EMPTY);
        scrollPane.setContent(content);
        StackPane.setMargin(scrollPane, new Insets(80, 0, 70, 0));

        content.setPadding(new Insets(15, 20, 25, 25));
        content.getChildren().addAll(presetsWrapper, sliderBox, checkBoxContainer);
        content.setSpacing(10);
        content.setAlignment(Pos.TOP_LEFT);


        presetsWrapper.setAlignment(Pos.CENTER_LEFT);
        presetsWrapper.setSpacing(5);
        presetsWrapper.getChildren().addAll(presetLabel, presetsButton);

        presetLabel.getStyleClass().add("settingsText");

        presetsButton.addAll("Flat", "Classical", "Club", "Dance", "Full bass", "Full treble", "Headphones", "Large hall", "Live", "Party", "Pop", "Rock", "Soft", "Techno", "Custom");

        presetsButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(1);
            }
            else {
                keyboardFocusOff(presetsButton);
                focus.set(-1);
            }
        });

        presetsButton.valueProperty().addListener((observableValue, oldValue, newValue) -> {
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
                case "Custom" -> mainController.pref.preferences.put(EQUALIZER_PRESET, CUSTOM);
            }
        });


        focusNodes.add(toggle);
        focusNodes.add(presetsButton);


        sliderBox.setAlignment(Pos.CENTER_LEFT);

        sliderBox.getChildren().add(labelBox);

        labelBox.setMinWidth(35);
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


        preampSlider = new EqualizerSlider(this, "Preamp", true, -1);
        sliders.add(new EqualizerSlider(this, "50 Hz", false, 0));
        sliders.add(new EqualizerSlider(this, "170 Hz", false, 1));
        sliders.add(new EqualizerSlider(this, "310 Hz", false, 2));
        sliders.add(new EqualizerSlider(this, "500 Hz", false, 3));
        sliders.add(new EqualizerSlider(this, "1 KHz", false, 4));
        sliders.add(new EqualizerSlider(this, "3 KHz", false, 5));
        sliders.add(new EqualizerSlider(this, "5 KHz", false, 6));
        sliders.add(new EqualizerSlider(this, "12 KHz", false, 7));
        sliders.add(new EqualizerSlider(this, "14 KHz", false, 8));
        sliders.add(new EqualizerSlider(this, "16 KHz", false, 9));

        checkBoxContainer.getChildren().addAll(checkbox, checkboxLabel);
        checkBoxContainer.setSpacing(10);
        checkBoxContainer.setPadding(new Insets(10, 0, 0, 0));
        moveSlidersTogether = mainController.pref.preferences.getBoolean(EQUALIZER_MOVE_SLIDERS_TOGETHER, true);
        checkbox.setSelected(moveSlidersTogether);


        checkbox.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            moveSlidersTogether = newValue;
            mainController.pref.preferences.putBoolean(EQUALIZER_MOVE_SLIDERS_TOGETHER, moveSlidersTogether);
        });

        checkbox.setFocusTraversable(false);
        checkbox.setCursor(Cursor.HAND);

        checkbox.setOnMouseClicked(e -> checkbox.requestFocus());

        checkbox.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue) focus.set(13);
            else {
                keyboardFocusOff(checkbox);
                focus.set(-1);
            }
        });

        checkbox.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            checkbox.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        checkbox.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            checkbox.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });

        checkboxLabel.setFocusTraversable(false);
        checkboxLabel.getStyleClass().add("settingsPaneText");
        checkboxLabel.setTextFill(Color.WHITE);


        StackPane.setAlignment(buttonContainer, Pos.BOTTOM_CENTER);
        buttonContainer.getChildren().add(mainButton);
        buttonContainer.getStyleClass().add("buttonContainer");
        buttonContainer.setPadding(new Insets(0, 15, 0, 15));
        buttonContainer.setPrefHeight(70);
        buttonContainer.setMaxHeight(70);

        mainButton.setOnAction(e -> this.hide());
        mainButton.setText("Close");
        mainButton.getStyleClass().add("menuButton");
        mainButton.setTextAlignment(TextAlignment.CENTER);
        mainButton.setPrefWidth(220);

        mainButton.setFocusTraversable(false);
        mainButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            mainButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        mainButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            mainButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });

        mainButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(14);
            }
            else {
                keyboardFocusOff(mainButton);
                focus.set(-1);
            }
        });
        StackPane.setAlignment(mainButton, Pos.CENTER_RIGHT);

        focusNodes.add(checkbox);
        focusNodes.add(mainButton);



        window.getChildren().addAll(titleContainer, scrollPane, buttonContainer);

    }

    public void show(){

        windowController.updateState(WindowState.EQUALIZER_OPEN);

        this.showing = true;
        window.setVisible(true);

        mainController.popupWindowContainer.setMouseTransparent(false);

        window.requestFocus();

        AnimationsClass.fadeAnimation(100, mainController.popupWindowContainer, 0 , 1, false, 1, true);
    }

    public void hide(){
        this.showing = false;

        windowController.windowState = WindowState.CLOSED;

        mainController.popupWindowContainer.setMouseTransparent(true);

        FadeTransition fadeTransition = new FadeTransition(Duration.millis(100), mainController.popupWindowContainer);
        fadeTransition.setFromValue(mainController.popupWindowContainer.getOpacity());
        fadeTransition.setToValue(0);
        fadeTransition.setOnFinished(e -> {
            window.setVisible(false);
            scrollPane.setVvalue(0);
        });
        fadeTransition.play();
    }

    public void loadEqualizer(){
        String preset = mainController.pref.preferences.get(EQUALIZER_PRESET, FLAT);

        switch(preset){
            case FLAT -> presetsButton.setValue("Flat");
            case CLASSICAL -> presetsButton.setValue("Classical");
            case CLUB -> presetsButton.setValue("Club");
            case DANCE -> presetsButton.setValue("Dance");
            case FULL_BASS -> presetsButton.setValue("Full bass");
            case FULL_TREBLE -> presetsButton.setValue("Full treble");
            case HEADPHONES -> presetsButton.setValue("Headphones");
            case LARGE_HALL -> presetsButton.setValue("Large hall");
            case LIVE -> presetsButton.setValue("Live");
            case PARTY -> presetsButton.setValue("Party");
            case POP -> presetsButton.setValue("Pop");
            case ROCK -> presetsButton.setValue("Rock");
            case SOFT -> presetsButton.setValue("Soft");
            case TECHNO -> presetsButton.setValue("Techno");
            case CUSTOM -> presetsButton.setValue("Custom");
        }

        if(preset.equals(CUSTOM)) {
            double[] amps = {
                mainController.pref.preferences.getDouble(EQUALIZER_PREAMP, 0),
                mainController.pref.preferences.getDouble(EQUALIZER_BAND1, 0),
                mainController.pref.preferences.getDouble(EQUALIZER_BAND2, 0),
                mainController.pref.preferences.getDouble(EQUALIZER_BAND3, 0),
                mainController.pref.preferences.getDouble(EQUALIZER_BAND4, 0),
                mainController.pref.preferences.getDouble(EQUALIZER_BAND5, 0),
                mainController.pref.preferences.getDouble(EQUALIZER_BAND6, 0),
                mainController.pref.preferences.getDouble(EQUALIZER_BAND7, 0),
                mainController.pref.preferences.getDouble(EQUALIZER_BAND8, 0),
                mainController.pref.preferences.getDouble(EQUALIZER_BAND9, 0),
                mainController.pref.preferences.getDouble(EQUALIZER_BAND10, 0)
            };

            applyPreset(amps, CUSTOM);
        }

        toggle.setSelected(mainController.pref.preferences.getBoolean(EQUALIZER_ENABLED, true));
    }


    public void applyPreset(double[] preset, String name){
        preampSlider.slider.setValue(preset[0]);
        for(int i = 0; i < sliders.size(); i++){
            sliders.get(i).slider.setValue(preset[i + 1]);
        }

        mainController.pref.preferences.put(EQUALIZER_PRESET, name);

        float[] floatArray = new float[preset.length - 1];

        for (int i = 1 ; i < preset.length; i++) {
            floatArray[i - 1] = (float) preset[i];
        }

        mainController.getMediaInterface().embeddedMediaPlayer.audio().equalizer().setPreamp((float) preset[0]);
        mainController.getMediaInterface().embeddedMediaPlayer.audio().equalizer().setAmps(floatArray);
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

    private void updatePadding(boolean value){
        if(value) content.setPadding(new Insets(15, 8, 25, 25));
        else      content.setPadding(new Insets(15, 20, 25, 25));
    }

    private void disableEqualizer(){
        focusNodes.clear();
        focusNodes.add(toggle);
        focusNodes.add(mainButton);

        presetsButton.setDisable(true);

        preampSlider.disable();
        preampSlider.line1.setStroke(Color.rgb(140,140,140));
        preampSlider.line2.setStroke(Color.rgb(140,140,140));
        preampSlider.line3.setStroke(Color.rgb(140,140,140));
        preampSlider.line4.setStroke(Color.rgb(140,140,140));
        preampSlider.line5.setStroke(Color.rgb(140,140,140));
        preampSlider.line6.setStroke(Color.rgb(140,140,140));
        preampSlider.line7.setStroke(Color.rgb(140,140,140));
        preampSlider.line8.setStroke(Color.rgb(140,140,140));
        preampSlider.line9.setStroke(Color.rgb(140,140,140));
        preampSlider.line10.setStroke(Color.rgb(140,140,140));

        for(EqualizerSlider equalizerSlider : sliders){
            equalizerSlider.disable();

            equalizerSlider.line1.setStroke(Color.rgb(140,140,140));
            equalizerSlider.line2.setStroke(Color.rgb(140,140,140));
            equalizerSlider.line3.setStroke(Color.rgb(140,140,140));
            equalizerSlider.line4.setStroke(Color.rgb(140,140,140));
            equalizerSlider.line5.setStroke(Color.rgb(140,140,140));
            equalizerSlider.line6.setStroke(Color.rgb(140,140,140));
            equalizerSlider.line7.setStroke(Color.rgb(140,140,140));
            equalizerSlider.line8.setStroke(Color.rgb(140,140,140));
            equalizerSlider.line9.setStroke(Color.rgb(140,140,140));
            equalizerSlider.line10.setStroke(Color.rgb(140,140,140));
        }

        checkbox.setDisable(true);

        mainController.pref.preferences.putBoolean(EQUALIZER_ENABLED, false);

        float[] floatArray = new float[flatEQ.length - 1];

        for (int i = 1 ; i < flatEQ.length; i++) {
            floatArray[i - 1] = (float) flatEQ[i];
        }

        mainController.getMediaInterface().embeddedMediaPlayer.audio().equalizer().setPreamp((float) flatEQ[0]);
        mainController.getMediaInterface().embeddedMediaPlayer.audio().equalizer().setAmps(floatArray);
    }

    private void enableEqualizer(){
        focusNodes.clear();
        focusNodes.add(toggle);
        focusNodes.add(presetsButton);
        focusNodes.add(preampSlider.slider);
        for(EqualizerSlider equalizerSlider : sliders){
            focusNodes.add(equalizerSlider.slider);
        }

        focusNodes.add(checkbox);
        focusNodes.add(mainButton);

        presetsButton.setDisable(false);

        preampSlider.enable();
        preampSlider.line1.setStroke(Color.rgb(200,200,200));
        preampSlider.line2.setStroke(Color.rgb(200,200,200));
        preampSlider.line3.setStroke(Color.rgb(200,200,200));
        preampSlider.line4.setStroke(Color.rgb(200,200,200));
        preampSlider.line5.setStroke(Color.rgb(200,200,200));
        preampSlider.line6.setStroke(Color.rgb(200,200,200));
        preampSlider.line7.setStroke(Color.rgb(200,200,200));
        preampSlider.line8.setStroke(Color.rgb(200,200,200));
        preampSlider.line9.setStroke(Color.rgb(200,200,200));
        preampSlider.line10.setStroke(Color.rgb(200,200,200));

        for(EqualizerSlider equalizerSlider : sliders){
            equalizerSlider.enable();

            equalizerSlider.line1.setStroke(Color.rgb(200,200,200));
            equalizerSlider.line2.setStroke(Color.rgb(200,200,200));
            equalizerSlider.line3.setStroke(Color.rgb(200,200,200));
            equalizerSlider.line4.setStroke(Color.rgb(200,200,200));
            equalizerSlider.line5.setStroke(Color.rgb(200,200,200));
            equalizerSlider.line6.setStroke(Color.rgb(200,200,200));
            equalizerSlider.line7.setStroke(Color.rgb(200,200,200));
            equalizerSlider.line8.setStroke(Color.rgb(200,200,200));
            equalizerSlider.line9.setStroke(Color.rgb(200,200,200));
            equalizerSlider.line10.setStroke(Color.rgb(200,200,200));
        }

        checkbox.setDisable(false);

        mainController.pref.preferences.putBoolean(EQUALIZER_ENABLED, true);

        float[] amps = new float[10];

        for (int i = 0; i < 10; i++) {
            amps[i] = (float) sliders.get(i).slider.getValue();
        }

        mainController.getMediaInterface().embeddedMediaPlayer.audio().equalizer().setPreamp((float) preampSlider.slider.getValue());
        mainController.getMediaInterface().embeddedMediaPlayer.audio().equalizer().setAmps(amps);
    }
}
