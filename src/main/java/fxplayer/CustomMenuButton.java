package fxplayer;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.MenuButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;

public class CustomMenuButton extends MenuButton {

    StringProperty valueProperty = new SimpleStringProperty();

    javafx.scene.control.CustomMenuItem customMenuItem = new javafx.scene.control.CustomMenuItem();
    public ScrollPane scrollPane = new ScrollPane();
    VBox content = new VBox();

    int itemWidth = 130;

    public CustomMenuButton(){

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


    public void addAll(String... values) {
        for(String value : values){
            CustomMenuItem customMenuItem = new CustomMenuItem(value, itemWidth);
            customMenuItem.selected.addListener((observableValue, oldValue, newValue) -> {
                if(newValue){
                    if(valueProperty.getValue() != null){
                        for(Node node : content.getChildren()){
                            CustomMenuItem customMenuItem1 = (CustomMenuItem) node;
                            if(customMenuItem1.value.equals(valueProperty.getValue())) {
                                customMenuItem1.unselect();
                                break;
                            }
                        }
                    }

                    valueProperty.set(customMenuItem.value);
                }
            });

            content.getChildren().add(customMenuItem);
        }
    }
    
    public void add(String value){
        CustomMenuItem customMenuItem = new CustomMenuItem(value, itemWidth);
        customMenuItem.selected.addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                if(valueProperty.getValue() != null){
                    for(Node node : content.getChildren()){
                        CustomMenuItem customMenuItem1 = (CustomMenuItem) node;
                        if(customMenuItem1.value.equals(valueProperty.getValue())) {
                            customMenuItem1.unselect();
                            break;
                        }
                    }
                }

                valueProperty.set(customMenuItem.value);
            }
        });

        content.getChildren().add(customMenuItem);
    }

    public StringProperty valueProperty(){
        return valueProperty;
    }


    public void setValue(String string){
        for(Node node : content.getChildren()){
            CustomMenuItem customMenuItem = (CustomMenuItem) node;

            if(customMenuItem.value.equals(string)){
                customMenuItem.select();
                break;
            }
        }
    }

    public String getValue(){
        return valueProperty.getValue();
    }

    public void setContextWidth(int value){
        this.setPrefWidth(value);
        this.setMaxWidth(value);

        scrollPane.setPrefWidth(value - 5);
        scrollPane.setMaxWidth(value - 5);

        itemWidth = value - 15;
    }

    public void setContextHeight(int value){
        scrollPane.setPrefHeight(value);
        scrollPane.setMaxHeight(value);
    }

    public void setScrollOff(){
        itemWidth = itemWidth + 10;
    }

}
