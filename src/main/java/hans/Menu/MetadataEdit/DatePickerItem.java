package hans.Menu.MetadataEdit;

import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DatePickerItem extends VBox{

    Label label;
    DatePicker datePicker;

    MetadataEditPage metadataEditPage;
    DateTimeFormatter dateTimeFormatter;

    // all accepted date formats

    DatePickerItem(MetadataEditPage metadataEditPage, String value, VBox parent, boolean add){

        this.metadataEditPage = metadataEditPage;


        label = new Label("Release date");
        label.getStyleClass().add("metadataKey");
        datePicker = new DatePicker();
        datePicker.setMinHeight(36);
        datePicker.setPrefHeight(36);
        datePicker.setMaxHeight(36);

        dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
        try {
            LocalDate date = LocalDate.parse(value, dateTimeFormatter);
            datePicker.setValue(date);
        }
        catch (DateTimeParseException e){
            e.printStackTrace();
        }

        datePicker.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            metadataEditPage.changesMade.set(true);
        });


        this.getChildren().addAll(label, datePicker);
        if(add) parent.getChildren().add(this);
    }
}