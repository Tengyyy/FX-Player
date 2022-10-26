package hans.Menu.MetadataEdit;

import hans.Menu.ExpandableTextArea;
import javafx.geometry.Insets;
import javafx.scene.control.Label;

import javafx.scene.layout.VBox;

public class TextAreaItem extends VBox{
    Label label;
    ExpandableTextArea textArea;


    TextAreaItem(String key, String value, VBox parent, boolean add){
        label = new Label(key);
        VBox.setMargin(label, new Insets(0, 0, 3, 0));
        label.getStyleClass().add("metadataKey");

        textArea = new ExpandableTextArea();
        textArea.setText(value);

        this.getChildren().addAll(label, textArea);
        if(add) parent.getChildren().add(this);
    }
}
