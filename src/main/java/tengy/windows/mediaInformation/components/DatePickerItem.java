package tengy.windows.mediaInformation.components;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import tengy.skins.HighlightedTextFieldSkin;
import tengy.windows.mediaInformation.MediaInformationWindow;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DatePickerItem extends VBox implements Component {

    Label label;

    TextField yearField = new TextField();
    TextField monthField = new TextField();
    TextField dayField = new TextField();

    MediaInformationWindow mediaInformationWindow;
    DateTimeFormatter dateTimeFormatter;

    public DatePickerItem(MediaInformationWindow mediaInformationWindow, String key, String value, VBox parent, boolean add){
        this(mediaInformationWindow, value, parent, add);
        label.setText(key);
    }

    public DatePickerItem(MediaInformationWindow mediaInformationWindow, String value, VBox parent, boolean add) {

        this.mediaInformationWindow = mediaInformationWindow;

        label = new Label("Release date");
        label.getStyleClass().add("metadataKey");

        yearField.setSkin(new HighlightedTextFieldSkin(yearField));
        yearField.getStyleClass().add("customTextField");
        yearField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                yearField.setText(oldValue);
                return;
            }

            monthField.setDisable(newValue.isEmpty());
            dayField.setDisable(newValue.isEmpty());

            mediaInformationWindow.changesMade.set(true);
            mediaInformationWindow.saveAllowed.set(true);
        });
        yearField.setPrefHeight(36);
        yearField.setMinHeight(36);
        yearField.setMaxHeight(36);
        yearField.setPromptText("Year");
        yearField.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);");
        HBox.setHgrow(yearField, Priority.ALWAYS);

        monthField.setSkin(new HighlightedTextFieldSkin(monthField));
        monthField.getStyleClass().add("customTextField");
        monthField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                monthField.setText(oldValue);
                return;
            }

            mediaInformationWindow.changesMade.set(true);
            mediaInformationWindow.saveAllowed.set(true);
        });
        monthField.setPrefHeight(36);
        monthField.setMinHeight(36);
        monthField.setMaxHeight(36);
        monthField.setPromptText("Month");
        monthField.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);");
        monthField.setDisable(true);
        HBox.setHgrow(monthField, Priority.ALWAYS);


        dayField.setSkin(new HighlightedTextFieldSkin(dayField));
        dayField.getStyleClass().add("customTextField");
        dayField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                dayField.setText(oldValue);
                return;
            }

            mediaInformationWindow.changesMade.set(true);
            mediaInformationWindow.saveAllowed.set(true);
        });
        dayField.setPrefHeight(36);
        dayField.setMinHeight(36);
        dayField.setMaxHeight(36);
        dayField.setPromptText("Day");
        dayField.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);");
        dayField.setDisable(true);
        HBox.setHgrow(dayField, Priority.ALWAYS);


        dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE;

        try {
            LocalDate date = LocalDate.parse(value, dateTimeFormatter);
            yearField.setText(String.valueOf(date.getYear()));
            monthField.setText(String.valueOf(date.getMonthValue()));
            dayField.setText(String.valueOf(date.getDayOfMonth()));
        } catch (DateTimeParseException ignored) {
            try {
                Integer year = Integer.parseInt(value);
                yearField.setText(String.valueOf(year));
            } catch (NumberFormatException ignored1) {
            }
        }

        HBox fieldBox = new HBox();
        fieldBox.setSpacing(5);
        fieldBox.getChildren().addAll(yearField, monthField, dayField);

        this.getChildren().addAll(label, fieldBox);
        this.setSpacing(3);
        if (add) parent.getChildren().add(this);
    }

    public String parseDate() {
        String date = yearField.getText();
        if (!monthField.getText().isEmpty()) {
            date = date + "-" + monthField.getText();
            if (!dayField.getText().isEmpty()) {
                date = date + "-" + dayField.getText();
            }
        }

        try {
            LocalDate localDate = LocalDate.parse(date, dateTimeFormatter);
            return localDate.format(dateTimeFormatter);
        } catch (DateTimeParseException ignored) {
            try {
                return String.valueOf(Integer.parseInt(yearField.getText()));
            } catch (NumberFormatException e) {
                return yearField.getText();
            }
        }
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