package tengy.PlaybackSettings;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.MenuButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;

import java.nio.charset.StandardCharsets;

public class PresetsButton extends MenuButton {

    StringProperty valueProperty = new SimpleStringProperty();

    CustomMenuItem customMenuItem = new CustomMenuItem();
    ScrollPane scrollPane = new ScrollPane();
    VBox content = new VBox();

    PresetsButton(){

        this.setFocusTraversable(false);
        this.setPrefSize(150, 35);
        this.setMaxSize(150, 35);
        this.getStyleClass().add("custom-menu-button");

        this.setOnMouseClicked(e -> this.requestFocus());

        this.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;

            this.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        this.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;

            this.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });

        valueProperty.addListener((observableValue, oldValue, newValue) -> {
            this.setText(newValue);
        });



        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setPrefSize(140, 200);
        scrollPane.setMaxSize(140, 200);
        scrollPane.setBackground(Background.EMPTY);
        scrollPane.setContent(content);
        scrollPane.getStyleClass().add("context-menu-scroll");

        content.setFillWidth(true);
        content.setBackground(Background.EMPTY);

        customMenuItem.setContent(scrollPane);

        this.getItems().add(customMenuItem);

    }


    public void addAll(String... presets) {
        for(String preset : presets){
            PresetItem presetItem = new PresetItem(preset);
            presetItem.selected.addListener((observableValue, oldValue, newValue) -> {
                if(newValue){
                    if(valueProperty.getValue() != null){
                        for(Node node : content.getChildren()){
                            PresetItem presetItem1 = (PresetItem) node;
                            if(presetItem1.value.equals(valueProperty.getValue())) {
                                presetItem1.unselect();
                                break;
                            }
                        }
                    }

                    valueProperty.set(presetItem.value);
                }
            });

            content.getChildren().add(presetItem);
        }
    }

    public StringProperty valueProperty(){
        return valueProperty;
    }


    public void setValue(String string){
        for(Node node : content.getChildren()){
            PresetItem presetItem = (PresetItem) node;

            if(presetItem.value.equals(string)){
                presetItem.select();
                break;
            }
        }
    }

    public String getValue(){
        return valueProperty.getValue();
    }
}
