package tengy.windows.mediaInformation.components;

import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import tengy.skins.HighlightedComboBoxSkin;
import tengy.windows.mediaInformation.MediaInformationWindow;

public class ComboBoxItem extends VBox implements Component{

    Label label;
    public ComboBox<String> comboBox;

    MediaInformationWindow mediaInformationWindow;

    public ComboBoxItem(MediaInformationWindow mediaInformationWindow, VBox parent, boolean add, String initialValue, String... values) {

        this.mediaInformationWindow = mediaInformationWindow;

        label = new Label("Media type");
        label.getStyleClass().add("metadataKey");
        VBox.setMargin(label, new Insets(0, 0, 3, 0));

        comboBox = new ComboBox<>();
        comboBox.setSkin(new HighlightedComboBoxSkin(comboBox));
        comboBox.getStyleClass().add("customCombo");
        comboBox.setMinHeight(36);
        comboBox.setPrefHeight(36);
        comboBox.setMaxHeight(36);
        comboBox.setPrefWidth(200);

        for (String value : values) {
            comboBox.getItems().add(value);
        }

        comboBox.setValue(initialValue);
        comboBox.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            mediaInformationWindow.changesMade.set(true);
            mediaInformationWindow.saveAllowed.set(true);
        });

        this.getChildren().addAll(label, comboBox);
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
