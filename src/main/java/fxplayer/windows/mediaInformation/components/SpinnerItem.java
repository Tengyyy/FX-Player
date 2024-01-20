package fxplayer.windows.mediaInformation.components;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import fxplayer.windows.mediaInformation.MediaInformationWindow;

public class SpinnerItem extends VBox implements Component{

    Label label;
    NumberSpinner numberSpinner;

    MediaInformationWindow mediaInformationWindow;


    SpinnerItem(MediaInformationWindow mediaInformationWindow, String key, String value1, VBox parent, boolean add){

        this.mediaInformationWindow = mediaInformationWindow;


        label = new Label(key);
        label.getStyleClass().add("metadataKey");


        numberSpinner = new NumberSpinner(mediaInformationWindow, value1);

        this.getChildren().addAll(label, numberSpinner.spinner);
        if(add) parent.getChildren().add(this);
    }

    @Override
    public String getKey() {
        return null;
    }

    @Override
    public String getValue() {
        return null;
    }
}