package hans.Menu.MediaInformation;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class TwoSpinnerItem extends VBox{

    Label label;
    NumberSpinner numberSpinner1;
    NumberSpinner numberSpinner2;

    MediaInformationPage mediaInformationPage;


    TwoSpinnerItem(MediaInformationPage mediaInformationPage, String key, String value1, String value2, VBox parent, boolean add){

        this.mediaInformationPage = mediaInformationPage;


        label = new Label(key);
        label.getStyleClass().add("metadataKey");


        numberSpinner1 = new NumberSpinner(mediaInformationPage, value1);


        Label slash = new Label("/");
        slash.getStyleClass().add("metadataKey");

        numberSpinner2 = new NumberSpinner(mediaInformationPage, value2);


        HBox hBox = new HBox(numberSpinner1.spinner, slash, numberSpinner2.spinner);
        hBox.setSpacing(5);

        this.getChildren().addAll(label, hBox);
        if(add) parent.getChildren().add(this);
    }
}
