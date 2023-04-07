package hans.Menu.MetadataEdit;

import com.jfoenix.controls.JFXButton;
import hans.*;
import hans.Captions.CaptionsState;
import hans.Menu.ExpandableTextArea;
import hans.Settings.SettingsState;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class CustomTextAreaItem extends VBox{

    TextField keyField = new TextField();

    ExpandableTextArea textArea;
    OtherEditItem otherEditItem;

    StackPane labelContainer = new StackPane();
    HBox labelBox = new HBox();

    Label warningLabel = new Label("*");

    SVGPath editSVG = new SVGPath();
    Region editIcon = new Region();
    StackPane editButtonPane = new StackPane();
    JFXButton editButton = new JFXButton();

    SVGPath removeSVG = new SVGPath();
    Region removeIcon = new Region();
    StackPane removeButtonPane = new StackPane();
    JFXButton removeButton = new JFXButton();

    ControlTooltip editButtonTooltip, removeButtonTooltip, warningLabelTooltip;

    String duplicateString = "";


    CustomTextAreaItem(OtherEditItem otherEditItem, String key, String value){

        this.otherEditItem = otherEditItem;

        editSVG.setContent(App.svgMap.get(SVG.EDIT));
        removeSVG.setContent(App.svgMap.get(SVG.CLOSE));

        warningLabel.getStyleClass().add("warningLabel");
        warningLabel.setPadding(new Insets(5, 5, 5, 5));

        VBox.setMargin(labelBox, new Insets(0, 0, 3, 0));

        keyField.setPrefWidth(3);
        keyField.setPrefHeight(20);
        keyField.setMinHeight(20);
        keyField.setMaxHeight(20);
        keyField.setTranslateX(-1);
        keyField.setTranslateY(2);
        keyField.getStyleClass().add("key-text-field");
        keyField.maxWidthProperty().bind(labelBox.widthProperty().subtract(35));
        keyField.disableProperty().bind(otherEditItem.metadataEditPage.fieldsDisabledProperty);

        keyField.textProperty().addListener((ov, prevText, currText) -> {
            // Do this in a Platform.runLater because of Textfield has no padding at first time and so on
            otherEditItem.metadataEditPage.mediaItem.changesMade.set(true);
            Platform.runLater(() -> {
                Text text = new Text(currText);
                text.setFont(new Font("Roboto Medium", 18)); // Set the same font, so the size is the same
                double width = text.getLayoutBounds().getWidth()
                        + keyField.getPadding().getLeft() + keyField.getPadding().getRight() // Add the padding of the TextField
                        + 2d; // Add some spacing
                keyField.setPrefWidth(width); // Set the width
                keyField.positionCaret(keyField.getCaretPosition()); // If you remove this line the caret flashes in the wrong spot for a moment
            });
        });

        keyField.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.ENTER){

                if(!duplicateString.isEmpty() && !keyField.getText().equalsIgnoreCase(duplicateString)){
                    ArrayList<CustomTextAreaItem> list = findDuplicates(duplicateString);
                    if(list.size() == 1) list.get(0).removeWarningLabel();

                    duplicateString = "";
                }

                ArrayList<CustomTextAreaItem> list = findDuplicates(keyField.getText());

                if(keyField.getText().isEmpty()){
                    addEmptyKeyWarningLabel();
                }
                else if(!list.isEmpty()){
                    for(CustomTextAreaItem item : list) item.duplicateString = keyField.getText();
                    duplicateString = keyField.getText();
                    addDuplicateKeyWarningLabels(list);
                }
                else {
                   removeWarningLabel();
                }

                otherEditItem.content.requestFocus();
                addEditButton();
            }
        });

        keyField.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                    if(otherEditItem.metadataEditPage.menuController.captionsController.captionsState != CaptionsState.CLOSED) otherEditItem.metadataEditPage.menuController.captionsController.closeCaptions();
                    if(otherEditItem.metadataEditPage.menuController.settingsController.settingsState != SettingsState.CLOSED) otherEditItem.metadataEditPage.menuController.settingsController.closeSettings();

                removeEditButton();
            }
            else {
                if(!duplicateString.isEmpty() && !keyField.getText().equalsIgnoreCase(duplicateString)){
                    ArrayList<CustomTextAreaItem> list = findDuplicates(duplicateString);
                    if(list.size() == 1) list.get(0).removeWarningLabel();

                    duplicateString = "";
                }

                ArrayList<CustomTextAreaItem> list = findDuplicates(keyField.getText());

                if(keyField.getText().isEmpty()){
                    addEmptyKeyWarningLabel();
                }
                else if(!list.isEmpty()){
                    duplicateString = keyField.getText();
                    addDuplicateKeyWarningLabels(list);
                }
                else {
                    removeWarningLabel();
                }

                addEditButton();
            }
        });


        editButton.setPrefWidth(30);
        editButton.setPrefHeight(30);
        editButton.setRipplerFill(Color.WHITE);
        editButton.getStyleClass().add("editButton");
        editButton.setCursor(Cursor.HAND);
        editButton.setOpacity(0);
        editButton.setText(null);
        editButton.disableProperty().bind(otherEditItem.metadataEditPage.fieldsDisabledProperty);


        editIcon.setShape(editSVG);
        editIcon.setMinSize(15, 15);
        editIcon.setPrefSize(15, 15);
        editIcon.setMaxSize(15, 15);
        editIcon.setMouseTransparent(true);
        editIcon.getStyleClass().add("menuIcon");

        editButton.setOnAction(e -> {
            removeEditButton();
            keyField.requestFocus();
        });

        editButton.disableProperty().bind(otherEditItem.metadataEditPage.fieldsDisabledProperty);


        editButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> AnimationsClass.fadeAnimation(200, editButton, 0, 1, false, 1, true));
        editButton.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> AnimationsClass.fadeAnimation(200, editButton, 1, 0, false, 1, true));


        HBox.setMargin(editButtonPane, new Insets(0, 0, 0, 5));
        editButtonPane.getChildren().addAll(editButton, editIcon);

        removeButton.setPrefWidth(30);
        removeButton.setPrefHeight(30);
        removeButton.setRipplerFill(Color.WHITE);
        removeButton.getStyleClass().add("removeButton");
        removeButton.setCursor(Cursor.HAND);
        removeButton.setOpacity(0);
        removeButton.setText(null);
        removeButton.disableProperty().bind(otherEditItem.metadataEditPage.fieldsDisabledProperty);

        removeIcon.setShape(removeSVG);
        removeIcon.setMinSize(18, 18);
        removeIcon.setPrefSize(18, 18);
        removeIcon.setMaxSize(18, 18);
        removeIcon.setMouseTransparent(true);
        removeIcon.getStyleClass().add("menuIcon");

        removeButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> AnimationsClass.fadeAnimation(200, removeButton, 0, 1, false, 1, true));

        removeButton.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> AnimationsClass.fadeAnimation(200, removeButton, 1, 0, false, 1, true));

        removeButton.setOnAction((e) -> removeItem());

        removeButtonPane.setMaxWidth(30);
        removeButtonPane.getChildren().addAll(removeButton, removeIcon);
        StackPane.setAlignment(removeButtonPane, Pos.CENTER_RIGHT);
        labelBox.getChildren().addAll(keyField, editButtonPane);
        StackPane.setAlignment(labelBox, Pos.CENTER_LEFT);
        labelContainer.getChildren().addAll(labelBox, removeButtonPane);
        labelContainer.setPrefWidth(Double.MAX_VALUE);

        labelBox.setPrefWidth(Double.MAX_VALUE);
        labelBox.setAlignment(Pos.CENTER_LEFT);
        labelBox.maxWidthProperty().bind(labelContainer.widthProperty().subtract(30));

        textArea = new ExpandableTextArea();
        textArea.initializeText(value);
        textArea.textProperty().addListener((observableValue, oldValue, newValue) -> {
            otherEditItem.metadataEditPage.mediaItem.changesMade.set(true);
        });
        textArea.disableProperty().bind(otherEditItem.metadataEditPage.fieldsDisabledProperty);

        this.getChildren().addAll(labelContainer, textArea);
        otherEditItem.content.getChildren().add(otherEditItem.content.getChildren().indexOf(otherEditItem.addButton), this);

        Platform.runLater(() -> {
            editButtonTooltip = new ControlTooltip(otherEditItem.metadataEditPage.menuController.mainController, "Edit key", editButton, 1000);
            removeButtonTooltip = new ControlTooltip(otherEditItem.metadataEditPage.menuController.mainController, "Remove key", removeButton, 1000);
            warningLabelTooltip = new ControlTooltip(otherEditItem.metadataEditPage.menuController.mainController, "Key can not be empty", warningLabel, 0, TooltipType.MENU_TOOLTIP);
            warningLabelTooltip.getStyleClass().add("warningLabelTooltip");
            boolean changesMade = otherEditItem.metadataEditPage.mediaItem.changesMade.get();
            keyField.setText(key);
            if(!changesMade) otherEditItem.metadataEditPage.mediaItem.changesMade.set(false);
        });


    }


    public void addEmptyKeyWarningLabel(){
        if(!labelBox.getChildren().contains(warningLabel)) labelBox.getChildren().add(0, warningLabel);
        warningLabelTooltip.setText("Key can not be empty");
    }

    public void addDuplicateKeyWarningLabels(ArrayList<CustomTextAreaItem> list){
        if(!labelBox.getChildren().contains(warningLabel)) labelBox.getChildren().add(0, warningLabel);
        warningLabelTooltip.setText("Duplicate key");

        for(CustomTextAreaItem item : list){
            if(!item.labelBox.getChildren().contains(item.warningLabel))  item.labelBox.getChildren().add(0, item.warningLabel);
            item.warningLabelTooltip.setText("Duplicate key");
        }
    }

    public void removeWarningLabel(){
        labelBox.getChildren().remove(warningLabel);
    }

    public ArrayList<CustomTextAreaItem> findDuplicates(String key){
        ArrayList<CustomTextAreaItem> duplicateItems = new ArrayList<>();
        for(CustomTextAreaItem item : otherEditItem.items){

            if(item.equals(this)) continue;
            if(item.keyField.getText().equalsIgnoreCase(key)){
                duplicateItems.add(item);
            }
        }


        return duplicateItems;
    }

    public void removeItem(){

        ArrayList<CustomTextAreaItem> list = null;
        if(!keyField.getText().isEmpty()) list = findDuplicates(keyField.getText());

        if(list != null && list.size() == 1) list.get(0).removeWarningLabel();

        otherEditItem.content.getChildren().remove(this);
        otherEditItem.items.remove(this);

        otherEditItem.metadataEditPage.mediaItem.changesMade.set(true);

    }

    public void addEditButton(){
        if(!labelBox.getChildren().contains(editButtonPane)) labelBox.getChildren().add(labelBox.getChildren().indexOf(keyField) + 1, editButtonPane);
    }

    public void removeEditButton(){
        labelBox.getChildren().remove(editButtonPane);
    }
}
