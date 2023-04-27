package hans.Menu.MediaInformation;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class SpinnerItem extends VBox{

    Label label;
    NumberSpinner numberSpinner;

    MediaInformationPage mediaInformationPage;


    SpinnerItem(MediaInformationPage mediaInformationPage, String key, String value1, VBox parent, boolean add){

        this.mediaInformationPage = mediaInformationPage;


        label = new Label(key);
        label.getStyleClass().add("metadataKey");


        numberSpinner = new NumberSpinner(mediaInformationPage, value1);

        this.getChildren().addAll(label, numberSpinner.spinner);
        if(add) parent.getChildren().add(this);
    }
}