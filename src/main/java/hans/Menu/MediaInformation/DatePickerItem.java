package hans.Menu.MediaInformation;

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

    MediaInformationPage mediaInformationPage;
    DateTimeFormatter dateTimeFormatter;


    DatePickerItem(MediaInformationPage mediaInformationPage, String value, VBox parent, boolean add){

        this.mediaInformationPage = mediaInformationPage;


        label = new Label("Release date");
        label.getStyleClass().add("metadataKey");
        datePicker = new DatePicker();
        datePicker.setMinHeight(36);
        datePicker.setPrefHeight(36);
        datePicker.setMaxHeight(36);
        datePicker.disableProperty().bind(mediaInformationPage.fieldsDisabledProperty);


        dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
        try {
            LocalDate date = LocalDate.parse(value, dateTimeFormatter);
            datePicker.setValue(date);
        }
        catch (DateTimeParseException ignored){
        }

        datePicker.focusedProperty().addListener((observableValue, aBoolean, newValue) -> {

            if(newValue){
                if(mediaInformationPage.menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) mediaInformationPage.menuController.subtitlesController.closeSubtitles();
                if(mediaInformationPage.menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) mediaInformationPage.menuController.playbackSettingsController.closeSettings();
            }

        });

        datePicker.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            mediaInformationPage.mediaItem.changesMade.set(true);
        });


        this.getChildren().addAll(label, datePicker);
        if(add) parent.getChildren().add(this);
    }
}