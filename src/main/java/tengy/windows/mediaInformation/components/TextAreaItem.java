package tengy.windows.mediaInformation.components;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import tengy.skins.ExpandableTextArea;
import tengy.windows.mediaInformation.MediaInformationWindow;

public class TextAreaItem extends VBox implements Component{
    public Label label;
    public ExpandableTextArea textArea;

    MediaInformationWindow mediaInformationWindow;

    public TextAreaItem(MediaInformationWindow mediaInformationWindow, String key, String value, VBox parent, boolean add){

        this.mediaInformationWindow = mediaInformationWindow;

        label = new Label(key);
        VBox.setMargin(label, new Insets(0, 0, 3, 0));
        label.getStyleClass().add("metadataKey");

        textArea = new ExpandableTextArea();
        textArea.initializeText(value);
        textArea.textProperty().addListener((observableValue, s, t1) -> {
            mediaInformationWindow.changesMade.set(true);
            mediaInformationWindow.saveAllowed.set(true);
        });

        this.getChildren().addAll(label, textArea);
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
