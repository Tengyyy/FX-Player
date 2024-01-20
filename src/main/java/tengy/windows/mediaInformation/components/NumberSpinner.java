package tengy.windows.mediaInformation.components;

import tengy.skins.HighlightedSpinnerSkin;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import tengy.windows.mediaInformation.MediaInformationWindow;

public class NumberSpinner{

    public Spinner<Integer> spinner;

    NumberSpinner(MediaInformationWindow mediaInformationWindow, String value) {

        SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 999);
        spinner = new Spinner<>(valueFactory);
        spinner.setSkin(new HighlightedSpinnerSkin(spinner));
        spinner.setEditable(true);
        spinner.setMinHeight(36);
        spinner.setPrefHeight(36);
        spinner.setMaxHeight(36);

        spinner.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.matches("\\d*") || newValue.length() > 3){
                spinner.getEditor().setText(oldValue);
            }
        });

        if(value != null) {
            try{
                int number = Integer.parseInt(value);
                spinner.getValueFactory().setValue(Math.min(999, Math.max(0, number)));
            }
            catch(NumberFormatException ignored){}
        }

        spinner.valueProperty().addListener((observableValue, oldValue, newValue) -> {

            if(newValue == null){
                spinner.getValueFactory().setValue(0);
            }

            mediaInformationWindow.changesMade.set(true);
            mediaInformationWindow.saveAllowed.set(true);
        });
    }
}