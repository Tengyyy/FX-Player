package hans.Menu.MetadataEdit;

import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ComboBoxItem extends VBox{

    Label label;
    ComboBox<String> comboBox;


    ComboBoxItem(VBox parent, boolean add, String initialValue, String... values) {
        label = new Label("Media type");
        label.getStyleClass().add("metadataKey");
        VBox.setMargin(label, new Insets(0, 0, 3, 0));

        comboBox = new ComboBox<>();
        comboBox.setMinHeight(36);
        comboBox.setPrefHeight(36);
        comboBox.setMaxHeight(36);
        for (String value : values) {
            comboBox.getItems().add(value);
        }

        comboBox.setValue(initialValue);

        this.getChildren().addAll(label, comboBox);
        if(add) parent.getChildren().add(this);
    }
}
