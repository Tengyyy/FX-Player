package hans.Menu.MetadataEdit;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class TrackFieldItem extends VBox{

    Label label;
    TextField textField1;
    TextField textField2;

    MetadataEditPage metadataEditPage;

    TrackFieldItem(MetadataEditPage metadataEditPage, String value, VBox parent, boolean add){

        this.metadataEditPage = metadataEditPage;

        label = new Label("Track number");
        label.getStyleClass().add("metadataKey");

        String firstValue = "";
        String secondValue = "";

        if(value.contains("/") && value.indexOf('/') > 0 && value.indexOf('/') < value.length() - 1){
            firstValue = value.substring(0, value.indexOf('/'));
            secondValue = value.substring(value.indexOf('/'));
        }
        else {
            firstValue = value;
        }

        textField1 = new TextField(firstValue);
        textField1.textProperty().addListener((observableValue, oldValue, newValue) -> {
            metadataEditPage.changesMade.set(true);
        });

        textField1.setPrefHeight(36);
        textField1.setMinHeight(36);
        textField1.setMaxHeight(36);

        Label slash = new Label("/");
        slash.getStyleClass().add("metadataKey");

        textField2 = new TextField(secondValue);
        textField2.textProperty().addListener((observableValue, oldValue, newValue) -> {
            metadataEditPage.changesMade.set(true);
        });

        textField2.setPrefHeight(36);
        textField2.setMinHeight(36);
        textField2.setMaxHeight(36);

        HBox hBox = new HBox(textField1, slash, textField2);
        hBox.setSpacing(5);

        this.getChildren().addAll(label, hBox);
        if(add) parent.getChildren().add(this);
    }
}
