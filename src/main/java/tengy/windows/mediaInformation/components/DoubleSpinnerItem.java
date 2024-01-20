package tengy.windows.mediaInformation.components;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import tengy.windows.mediaInformation.MediaInformationWindow;

public class DoubleSpinnerItem extends VBox implements Component{

    Label label;
    public NumberSpinner numberSpinner1;
    public NumberSpinner numberSpinner2;

    MediaInformationWindow mediaInformationWindow;


    public DoubleSpinnerItem(MediaInformationWindow mediaInformationWindow, String key, String value1, String value2, VBox parent, boolean add){

        this.mediaInformationWindow = mediaInformationWindow;


        label = new Label(key);
        label.getStyleClass().add("metadataKey");

        numberSpinner1 = new NumberSpinner(mediaInformationWindow, value1);

        Label slash = new Label("/");
        slash.getStyleClass().add("metadataSlash");

        numberSpinner2 = new NumberSpinner(mediaInformationWindow, value2);

        HBox hBox = new HBox(numberSpinner1.spinner, slash, numberSpinner2.spinner);
        hBox.setSpacing(10);

        this.getChildren().addAll(label, hBox);
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
