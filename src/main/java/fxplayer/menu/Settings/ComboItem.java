package fxplayer.menu.Settings;

import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;
import fxplayer.CustomMenuButton;

import static fxplayer.Utilities.keyboardFocusOff;

public class ComboItem extends StackPane {

    SVGPath iconPath = new SVGPath();
    Region icon = new Region();
    Label label = new Label();
    CustomMenuButton customMenuButton = new CustomMenuButton();

    ComboItem(SettingsPage settingsPage, String iconContent, String text, SettingsSection section, int sectionFocusValue, int focusValue){

        this.getChildren().addAll(icon, label, customMenuButton);
        this.setPadding(new Insets(20, 10, 20, 10));
        this.getStyleClass().addAll("highlightedSection", "settingsToggle");

        iconPath.setContent(iconContent);

        icon.setShape(iconPath);
        icon.getStyleClass().add("menuIcon");
        icon.setPrefSize(17, 17);
        icon.setMaxSize(17, 17);
        icon.setMouseTransparent(true);
        StackPane.setMargin(icon, new Insets(0, 0, 0, 8));
        StackPane.setAlignment(icon, Pos.CENTER_LEFT);


        label.setText(text);
        label.getStyleClass().add("toggleText");
        StackPane.setAlignment(label, Pos.CENTER_LEFT);
        StackPane.setMargin(label, new Insets(0, 0, 0, 35));

        StackPane.setAlignment(customMenuButton, Pos.CENTER_RIGHT);
        customMenuButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                settingsPage.focus.set(sectionFocusValue);
                section.setFocus(focusValue);
            }
            else {
                keyboardFocusOff(customMenuButton);
                settingsPage.focus.set(-1);
                section.setFocus(-1);
            }
        });

    }

    public StringProperty valueProperty(){
        return customMenuButton.valueProperty();
    }

    public void add(String value){
        customMenuButton.add(value);
    }

}
