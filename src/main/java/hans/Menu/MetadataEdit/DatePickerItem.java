package hans.Menu.MetadataEdit;

import hans.Subtitles.SubtitlesState;
import hans.PlaybackSettings.PlaybackSettingsState;
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


    DatePickerItem(MetadataEditPage metadataEditPage, String value, VBox parent, boolean add){

        this.metadataEditPage = metadataEditPage;


        label = new Label("Release date");
        label.getStyleClass().add("metadataKey");
        datePicker = new DatePicker();
        datePicker.setMinHeight(36);
        datePicker.setPrefHeight(36);
        datePicker.setMaxHeight(36);
        datePicker.disableProperty().bind(metadataEditPage.fieldsDisabledProperty);


        dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
        try {
            LocalDate date = LocalDate.parse(value, dateTimeFormatter);
            datePicker.setValue(date);
        }
        catch (DateTimeParseException ignored){
        }

        datePicker.focusedProperty().addListener((observableValue, aBoolean, newValue) -> {

            if(newValue){
                if(metadataEditPage.menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) metadataEditPage.menuController.subtitlesController.closeSubtitles();
                if(metadataEditPage.menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) metadataEditPage.menuController.playbackSettingsController.closeSettings();
            }

        });

        datePicker.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            metadataEditPage.mediaItem.changesMade.set(true);
        });


        this.getChildren().addAll(label, datePicker);
        if(add) parent.getChildren().add(this);
    }
}