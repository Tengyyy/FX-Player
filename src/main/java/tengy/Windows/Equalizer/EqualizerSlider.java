package tengy.Windows.Equalizer;

import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import tengy.VerticalProgressBar;
import tengy.Windows.Equalizer.EqualizerWindow;

import static tengy.Utilities.keyboardFocusOff;

public class EqualizerSlider {

    EqualizerWindow equalizerWindow;

    VBox sliderWrapper = new VBox();
    StackPane sliderPane = new StackPane();
    public Slider slider = new Slider();
    VerticalProgressBar sliderTrack = new VerticalProgressBar(9, 158);
    Line line1 = new Line();
    Line line2 = new Line();
    Line line3 = new Line();
    Line line4 = new Line();
    Line line5 = new Line();
    Line line6 = new Line();
    Line line7 = new Line();
    Line line8 = new Line();
    Line line9 = new Line();
    Line line10 = new Line();


    HBox labelWrapper = new HBox();
    Label label = new Label();

    EqualizerSlider(EqualizerWindow equalizerWindow, String text, boolean isPreamp, int index){
        this.equalizerWindow = equalizerWindow;

        sliderWrapper.setMinSize(50, 200);
        sliderWrapper.setPrefSize(50, 200);
        sliderWrapper.setMaxSize(50, 200);
        sliderWrapper.getChildren().addAll(sliderPane, labelWrapper);

        sliderPane.setMinSize(50, 180);
        sliderPane.setPrefSize(50, 180);
        sliderPane.setMaxSize(50, 180);
        sliderPane.getChildren().addAll(line1, line2, line3, line4, line5, line6, line7, line8, line9, line10, sliderTrack.getProgressHolder(), slider);

        StackPane.setAlignment(line1, Pos.CENTER_LEFT);
        line1.setStrokeWidth(1);
        line1.setStroke(Color.rgb(200, 200, 200));
        line1.setStartX(0);
        line1.setStartY(0);
        line1.setEndX(5);
        line1.setEndY(0);
        line1.setTranslateX(12);

        StackPane.setAlignment(line2, Pos.CENTER_RIGHT);
        line2.setStrokeWidth(1);
        line2.setStroke(Color.rgb(200, 200, 200));
        line2.setStartX(0);
        line2.setStartY(0);
        line2.setEndX(5);
        line2.setEndY(0);
        line2.setTranslateX(-12);

        StackPane.setAlignment(line3, Pos.TOP_LEFT);
        line3.setStrokeWidth(1);
        line3.setStroke(Color.rgb(200, 200, 200));
        line3.setStartX(0);
        line3.setStartY(0);
        line3.setEndX(5);
        line3.setEndY(0);
        line3.setTranslateX(12);
        line3.setTranslateY(14);

        StackPane.setAlignment(line4, Pos.TOP_RIGHT);
        line4.setStrokeWidth(1);
        line4.setStroke(Color.rgb(200, 200, 200));
        line4.setStartX(0);
        line4.setStartY(0);
        line4.setEndX(5);
        line4.setEndY(0);
        line4.setTranslateX(-12);
        line4.setTranslateY(14);


        StackPane.setAlignment(line5, Pos.BOTTOM_LEFT);
        line5.setStrokeWidth(1);
        line5.setStroke(Color.rgb(200, 200, 200));
        line5.setStartX(0);
        line5.setStartY(0);
        line5.setEndX(5);
        line5.setEndY(0);
        line5.setTranslateX(12);
        line5.setTranslateY(-14);

        StackPane.setAlignment(line6, Pos.BOTTOM_RIGHT);
        line6.setStrokeWidth(1);
        line6.setStroke(Color.rgb(200, 200, 200));
        line6.setStartX(0);
        line6.setStartY(0);
        line6.setEndX(5);
        line6.setEndY(0);
        line6.setTranslateX(-12);
        line6.setTranslateY(-14);


        StackPane.setAlignment(line7, Pos.TOP_LEFT);
        line7.setStrokeWidth(1);
        line7.setStroke(Color.rgb(200, 200, 200));
        line7.setStartX(0);
        line7.setStartY(0);
        line7.setEndX(5);
        line7.setEndY(0);
        line7.setTranslateX(12);
        line7.setTranslateY(50);

        StackPane.setAlignment(line8, Pos.TOP_RIGHT);
        line8.setStrokeWidth(1);
        line8.setStroke(Color.rgb(200, 200, 200));
        line8.setStartX(0);
        line8.setStartY(0);
        line8.setEndX(5);
        line8.setEndY(0);
        line8.setTranslateX(-12);
        line8.setTranslateY(50);

        StackPane.setAlignment(line9, Pos.BOTTOM_LEFT);
        line9.setStrokeWidth(1);
        line9.setStroke(Color.rgb(200, 200, 200));
        line9.setStartX(0);
        line9.setStartY(0);
        line9.setEndX(5);
        line9.setEndY(0);
        line9.setTranslateX(12);
        line9.setTranslateY(-50);

        StackPane.setAlignment(line10, Pos.BOTTOM_RIGHT);
        line10.setStrokeWidth(1);
        line10.setStroke(Color.rgb(200, 200, 200));
        line10.setStartX(0);
        line10.setStartY(0);
        line10.setEndX(5);
        line10.setEndY(0);
        line10.setTranslateX(-12);
        line10.setTranslateY(-50);

        sliderTrack.getProgressBar().setProgress(0.5);
        sliderTrack.getProgressBar().getStyleClass().add("equalizer-slider-track");
        sliderTrack.getProgressBar().setProgress(0.5);


        slider.getStyleClass().add("equalizer-slider");
        slider.setMin(-20);
        slider.setMax(20);
        slider.setValue(0);
        slider.setOrientation(Orientation.VERTICAL);
        slider.setMinHeight(172);
        slider.setPrefHeight(172);
        slider.setMaxHeight(172);
        slider.setFocusTraversable(false);
        slider.setOnMousePressed(e -> slider.setValueChanging(true));
        slider.setOnMouseReleased(e -> slider.setValueChanging(false));

        slider.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            sliderTrack.getProgressBar().setProgress((newValue.doubleValue() + 20) / 40);

            if(!isPreamp && slider.isValueChanging() && equalizerWindow.moveSlidersTogether) moveNearbySliders(index, oldValue.doubleValue(), newValue.doubleValue());
        });

        slider.valueChangingProperty().addListener((observableValue, oldValue, newValue) -> {
            equalizerWindow.sliderActive = newValue;

            if(equalizerWindow.presetsButton.getValue() == null || !equalizerWindow.presetsButton.getValue().equals("Custom")){
                equalizerWindow.presetsButton.setValue("Custom");
            }

            if(!newValue){

                if(isPreamp){
                    equalizerWindow.mainController.pref.preferences.putDouble(EqualizerWindow.EQUALIZER_PREAMP, slider.getValue());
                    equalizerWindow.mainController.getMediaInterface().embeddedMediaPlayer.audio().equalizer().setPreamp((float) slider.getValue());

                }
                else {
                    float[] amps = new float[10];

                    for (int i = 0; i < 10; i++) {
                        amps[i] = (float) equalizerWindow.sliders.get(i).slider.getValue();
                    }

                    equalizerWindow.mainController.pref.preferences.putDouble(EqualizerWindow.EQUALIZER_BAND1, amps[0]);
                    equalizerWindow.mainController.pref.preferences.putDouble(EqualizerWindow.EQUALIZER_BAND2, amps[1]);
                    equalizerWindow.mainController.pref.preferences.putDouble(EqualizerWindow.EQUALIZER_BAND3, amps[2]);
                    equalizerWindow.mainController.pref.preferences.putDouble(EqualizerWindow.EQUALIZER_BAND4, amps[3]);
                    equalizerWindow.mainController.pref.preferences.putDouble(EqualizerWindow.EQUALIZER_BAND5, amps[4]);
                    equalizerWindow.mainController.pref.preferences.putDouble(EqualizerWindow.EQUALIZER_BAND6, amps[5]);
                    equalizerWindow.mainController.pref.preferences.putDouble(EqualizerWindow.EQUALIZER_BAND7, amps[6]);
                    equalizerWindow.mainController.pref.preferences.putDouble(EqualizerWindow.EQUALIZER_BAND8, amps[7]);
                    equalizerWindow.mainController.pref.preferences.putDouble(EqualizerWindow.EQUALIZER_BAND9, amps[8]);
                    equalizerWindow.mainController.pref.preferences.putDouble(EqualizerWindow.EQUALIZER_BAND10, amps[9]);

                    equalizerWindow.mainController.getMediaInterface().embeddedMediaPlayer.audio().equalizer().setAmps(amps);
                }
            }
        });

        slider.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                sliderTrack.getProgressBar().pseudoClassStateChanged(PseudoClass.getPseudoClass("focused"), true);
                equalizerWindow.focus.set(index + 3);
            }
            else {
                sliderTrack.getProgressBar().pseudoClassStateChanged(PseudoClass.getPseudoClass("focused"), false);
                equalizerWindow.focus.set(-1);
                keyboardFocusOff(slider);
                slider.setValueChanging(false);
            }
        });

        slider.disabledProperty().addListener((observableValue, oldValue, newValue) -> {
            sliderTrack.getProgressBar().pseudoClassStateChanged(PseudoClass.getPseudoClass("disabled"), newValue);
        });

        labelWrapper.getChildren().add(label);
        labelWrapper.setMinSize(50, 20);
        labelWrapper.setPrefSize(50, 20);
        labelWrapper.setMaxSize(50, 20);
        labelWrapper.setAlignment(Pos.CENTER);
        label.setText(text);
        label.getStyleClass().add("equalizer-label");


        if(isPreamp) HBox.setMargin(sliderWrapper, new Insets(0, 15, 0 , 0));

        equalizerWindow.sliderBox.getChildren().add(sliderWrapper);
        equalizerWindow.focusNodes.add(slider);
    }


    private void moveNearbySliders(int index, double oldValue, double newValue){
        for(int i = 0; i < 10; i++){

            if(i == index) continue;

            Slider slider = equalizerWindow.sliders.get(i).slider;
            double weightedValue = Math.abs(newValue - oldValue) / ((Math.pow((Math.abs(index - i) + 1), 2) * 2));

            slider.setValue(slider.getValue() - (slider.getValue() - newValue) * weightedValue);
        }
    }

    public void disable(){
        slider.setDisable(true);
    }

    public void enable(){
        slider.setDisable(false);
    }
}
