package tengy.Menu.MediaInformation;

import tengy.Subtitles.SubtitlesState;
import tengy.PlaybackSettings.PlaybackSettingsState;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

public class NumberSpinner{


    Spinner<Integer> spinner;


    NumberSpinner(MediaInformationPage mediaInformationPage, String value) {

        SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 999);
        spinner = new Spinner<>(valueFactory);
        spinner.setEditable(true);
        spinner.setMinHeight(36);
        spinner.setPrefHeight(36);
        spinner.setMaxHeight(36);
        spinner.disableProperty().bind(mediaInformationPage.fieldsDisabledProperty);

        spinner.focusedProperty().addListener((observableValue, aBoolean, newValue) -> {
            if(newValue){
                if(mediaInformationPage.menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) mediaInformationPage.menuController.subtitlesController.closeSubtitles();
                if(mediaInformationPage.menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) mediaInformationPage.menuController.playbackSettingsController.closeSettings();
            }

        });


        spinner.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.matches("\\d*") || newValue.length() > 3){
                spinner.getEditor().setText(oldValue);
            }
        });

        if(value == null){
            spinner.getValueFactory().setValue(0);
        }
        else {
            try{
                int number = Integer.parseInt(value);
                spinner.getValueFactory().setValue(Math.min(999, Math.max(0, number)));
            }
            catch(NumberFormatException e){
                spinner.getValueFactory().setValue(0);
            }
        }

        spinner.valueProperty().addListener((observableValue, oldValue, newValue) -> {

            if(newValue == null){
                spinner.getValueFactory().setValue(0);
            }

            mediaInformationPage.mediaItem.changesMade.set(true);
        });
    }
}