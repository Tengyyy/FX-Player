package hans.PlaybackSettings;

import hans.VerticalProgressBar;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class EqualizerSlider {

    EqualizerController equalizerController;

    VBox sliderWrapper = new VBox();
    StackPane sliderPane = new StackPane();
    Slider slider = new Slider();
    VerticalProgressBar sliderTrack = new VerticalProgressBar(5, 158);

    HBox labelWrapper = new HBox();
    Label label = new Label();

    EqualizerSlider(EqualizerController equalizerController, String text){
        this.equalizerController = equalizerController;

        sliderWrapper.setMinSize(45, 200);
        sliderWrapper.setPrefSize(45, 200);
        sliderWrapper.setMaxSize(45, 200);
        sliderWrapper.getChildren().addAll(sliderPane, labelWrapper);

        sliderPane.setMinSize(45, 180);
        sliderPane.setPrefSize(45, 180);
        sliderPane.setMaxSize(45, 180);
        sliderPane.getChildren().addAll(sliderTrack.getProgressHolder(), slider);

        sliderTrack.getProgressBar().setProgress(0.5);
        sliderTrack.getProgressBar().getStyleClass().add("equalizer-slider-track");
        sliderTrack.getProgressBar().setProgress(0.5);

        slider.getStyleClass().add("equalizer-slider");
        slider.setMin(-20);
        slider.setMax(20);
        slider.setValue(0);
        slider.setOrientation(Orientation.VERTICAL);
        slider.setMinHeight(170);
        slider.setPrefHeight(170);
        slider.setMaxHeight(170);

        slider.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            sliderTrack.getProgressBar().setProgress((newValue.doubleValue() + 20) / 40);
            if(slider.isValueChanging() && !equalizerController.comboBox.getValue().equals("Custom")) equalizerController.comboBox.setValue("Custom");

            if(slider.isValueChanging() && equalizerController.moveSlidersTogether) moveNearbySliders(equalizerController.sliders.indexOf(this), oldValue.doubleValue(), newValue.doubleValue());
        });

        slider.valueChangingProperty().addListener((observableValue, oldValue, newValue) -> {
            equalizerController.sliderActive = newValue;

            if(!newValue){
                float[] amps = new float[10];

                for(int i = 0; i < 10; i++){
                    amps[i] = (float) equalizerController.sliders.get(i).slider.getValue();
                }

                equalizerController.playbackSettingsController.mediaInterface.embeddedMediaPlayer.audio().equalizer().setAmps(amps);
            }
        });

        labelWrapper.getChildren().add(label);
        labelWrapper.setMinSize(45, 20);
        labelWrapper.setPrefSize(45, 20);
        labelWrapper.setMaxSize(45, 20);
        labelWrapper.setAlignment(Pos.CENTER);
        label.setText(text);
        label.getStyleClass().add("equalizer-label");


        equalizerController.sliderBox.getChildren().add(sliderWrapper);
    }


    private void moveNearbySliders(int index, double oldValue, double newValue){
        for(int i = 0; i < 10; i++){

            if(i == index) continue;

             Slider slider = equalizerController.sliders.get(i).slider;
            double weightedValue = Math.abs(newValue - oldValue) / ((Math.pow((Math.abs(index - i) + 1), 2) * 2));

            slider.setValue(slider.getValue() - (slider.getValue() - newValue) * weightedValue);
        }
    }
}
