package fxplayer.windows.mediaInformation.components;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import fxplayer.windows.mediaInformation.MediaInformationWindow;

public class TwoSpinnerItem extends HBox{

    Label label1;
    Label label2;
    public NumberSpinner numberSpinner1;
    public NumberSpinner numberSpinner2;

    MediaInformationWindow mediaInformationWindow;


    public TwoSpinnerItem(MediaInformationWindow mediaInformationWindow, String key1, String value1, String key2, String value2, VBox parent, boolean add){

        this.mediaInformationWindow = mediaInformationWindow;

        label1 = new Label(key1);
        label1.getStyleClass().add("metadataKey");

        label2 = new Label(key2);
        label2.getStyleClass().add("metadataKey");

        numberSpinner1 = new NumberSpinner(mediaInformationWindow, value1);
        numberSpinner1.spinner.setPrefWidth(200);

        numberSpinner2 = new NumberSpinner(mediaInformationWindow, value2);
        numberSpinner2.spinner.setPrefWidth(200);

        VBox leftBox = new VBox();
        leftBox.setSpacing(3);
        leftBox.getChildren().addAll(label1, numberSpinner1.spinner);
        HBox.setHgrow(leftBox, Priority.ALWAYS);

        VBox rightBox = new VBox();
        rightBox.setSpacing(3);
        rightBox.getChildren().addAll(label2, numberSpinner2.spinner);
        HBox.setHgrow(rightBox, Priority.ALWAYS);

        this.getChildren().addAll(leftBox, rightBox);
        this.setSpacing(10);
        if(add) parent.getChildren().add(this);
    }
}
