package hans.Menu.MetadataEdit;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class SpinnerItem extends VBox{

    Label label;
    NumberSpinner numberSpinner;

    MetadataEditPage metadataEditPage;


    SpinnerItem(MetadataEditPage metadataEditPage, String key, String value1, VBox parent, boolean add){

        this.metadataEditPage = metadataEditPage;


        label = new Label(key);
        label.getStyleClass().add("metadataKey");


        numberSpinner = new NumberSpinner(metadataEditPage, value1);

        this.getChildren().addAll(label, numberSpinner.spinner);
        if(add) parent.getChildren().add(this);
    }
}