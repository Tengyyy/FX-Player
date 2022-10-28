package hans.Menu.MetadataEdit;

import com.jfoenix.controls.JFXButton;
import hans.*;
import hans.Menu.ExpandableTextArea;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

public class CustomTextAreaItem extends VBox{

    TextField keyField = new TextField();

    ExpandableTextArea textArea;
    OtherEditPage otherEditPage;

    StackPane labelContainer = new StackPane();
    HBox labelBox = new HBox();
    Label label;

    Label warningLabel = new Label("*");

    SVGPath editSVG = new SVGPath();
    Region editIcon = new Region();
    StackPane editButtonPane = new StackPane();
    JFXButton editButton = new JFXButton();

    SVGPath removeSVG = new SVGPath();
    Region removeIcon = new Region();
    StackPane removeButtonPane = new StackPane();
    JFXButton removeButton = new JFXButton();

    ControlTooltip editButtonTooltip, removeButtonTooltip;


    CustomTextAreaItem(OtherEditPage otherEditPage, String key, String value, VBox parent){

        this.otherEditPage = otherEditPage;

        editSVG.setContent(App.svgMap.get(SVG.EDIT));
        removeSVG.setContent(App.svgMap.get(SVG.CLOSE));

        label = new Label(key);
        label.setOnMouseClicked(e -> {
            if(e.getClickCount() == 2) addTextField();
        });
        label.getStyleClass().add("metadataKey");

        warningLabel.getStyleClass().add("warningLabel");

        VBox.setMargin(labelBox, new Insets(0, 0, 3, 0));

        keyField.setText(key);
        keyField.setPrefHeight(20);
        keyField.setMinHeight(20);
        keyField.setMaxHeight(20);
        keyField.setTranslateX(-1);
        keyField.setTranslateY(2);
        keyField.getStyleClass().add("key-text-field");
        keyField.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.ENTER){
                if(keyField.getText().isEmpty()){
                    //TODO: show popup
                }
                else addLabel();
            }
        });
        keyField.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(!newValue){
                if(keyField.getText().isEmpty()){
                    //TODO: show popup
                }
                else addLabel();
            }
        });
        HBox.setHgrow(keyField, Priority.ALWAYS);

        editButton.setPrefWidth(30);
        editButton.setPrefHeight(30);
        editButton.setRipplerFill(Color.WHITE);
        editButton.getStyleClass().add("editButton");
        editButton.setCursor(Cursor.HAND);
        editButton.setOpacity(0);
        editButton.setText(null);

        editIcon.setShape(editSVG);
        editIcon.setMinSize(15, 15);
        editIcon.setPrefSize(15, 15);
        editIcon.setMaxSize(15, 15);
        editIcon.setMouseTransparent(true);
        editIcon.getStyleClass().add("menuIcon");

        editButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> AnimationsClass.fadeAnimation(200, editButton, 0, 1, false, 1, true));

        editButton.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> AnimationsClass.fadeAnimation(200, editButton, 1, 0, false, 1, true));

        editButton.setOnAction((e) -> addTextField());

        HBox.setMargin(editButtonPane, new Insets(0, 0, 0, 5));
        editButtonPane.getChildren().addAll(editButton, editIcon);

        removeButton.setPrefWidth(30);
        removeButton.setPrefHeight(30);
        removeButton.setRipplerFill(Color.WHITE);
        removeButton.getStyleClass().add("removeButton");
        removeButton.setCursor(Cursor.HAND);
        removeButton.setOpacity(0);
        removeButton.setText(null);

        removeIcon.setShape(removeSVG);
        removeIcon.setMinSize(18, 18);
        removeIcon.setPrefSize(18, 18);
        removeIcon.setMaxSize(18, 18);
        removeIcon.setMouseTransparent(true);
        removeIcon.getStyleClass().add("menuIcon");

        removeButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> AnimationsClass.fadeAnimation(200, removeButton, 0, 1, false, 1, true));

        removeButton.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> AnimationsClass.fadeAnimation(200, removeButton, 1, 0, false, 1, true));

        removeButton.setOnAction((e) -> {
            parent.getChildren().remove(this);
            otherEditPage.items.remove(this);
        });

        removeButtonPane.setMaxWidth(30);
        removeButtonPane.getChildren().addAll(removeButton, removeIcon);
        StackPane.setAlignment(removeButtonPane, Pos.CENTER_RIGHT);
        labelBox.getChildren().addAll(label, editButtonPane);
        StackPane.setAlignment(labelBox, Pos.CENTER_LEFT);
        labelContainer.getChildren().addAll(labelBox, removeButtonPane);
        labelContainer.setPrefWidth(Double.MAX_VALUE);

        labelBox.setPrefWidth(Double.MAX_VALUE);
        labelBox.setAlignment(Pos.CENTER_LEFT);
        labelBox.maxWidthProperty().bind(labelContainer.widthProperty().subtract(30));

        textArea = new ExpandableTextArea();
        textArea.setText(value);

        this.getChildren().addAll(labelContainer, textArea);
        parent.getChildren().add(this);

        Platform.runLater(() -> {
            editButtonTooltip = new ControlTooltip(otherEditPage.metadataEditPage.menuController.mainController, "Edit key", editButton, 1000);
            removeButtonTooltip = new ControlTooltip(otherEditPage.metadataEditPage.menuController.mainController, "Remove key", removeButton, 1000);
        });


    }

    public void addLabel(){
        label.setText(keyField.getText());
        labelBox.getChildren().remove(keyField);
        if(!labelBox.getChildren().contains(label)) labelBox.getChildren().add(label);
        if(!labelBox.getChildren().contains(editButtonPane)) labelBox.getChildren().add(editButtonPane);
    }

    public void addTextField(){
        labelBox.getChildren().removeAll(label, editButtonPane);
        if(!labelBox.getChildren().contains(keyField)) labelBox.getChildren().add(0, keyField);
        Platform.runLater(() -> {
            keyField.requestFocus();
            keyField.positionCaret(keyField.getText().length());
        });
    }
}
