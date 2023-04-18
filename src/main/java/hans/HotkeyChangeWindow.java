package hans;

import com.jfoenix.controls.JFXButton;
import hans.Menu.Settings.Action;
import hans.Menu.Settings.ControlItem;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
import javafx.scene.text.TextAlignment;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HotkeyChangeWindow {

    MainController mainController;


    StackPane window = new StackPane();

    VBox windowContainer = new VBox();

    VBox titleContainer = new VBox();
    Label title = new Label("Editing hotkey for action:");
    Label actionLabel = new Label();

    VBox hotkeyContainer = new VBox();
    Label hotkeyText = new Label("Hotkey:");
    HBox hotkeyBox = new HBox();

    Label warningLabel = new Label();

    StackPane buttonContainer = new StackPane();
    Button mainButton = new Button(), secondaryButton = new Button();

    StackPane closeButtonPane = new StackPane();
    Region closeButtonIcon = new Region();
    SVGPath closeButtonSVG = new SVGPath();
    JFXButton closeButton = new JFXButton();

    boolean showing = false;
    BooleanProperty isValid = new SimpleBooleanProperty(true);

    ControlItem controlItem;
    Action action;
    KeyCode[] hotkey;

    public HotkeyChangeWindow(MainController mainController){
        this.mainController = mainController;

        mainController.popupWindowContainer.getChildren().add(window);

        window.setAlignment(Pos.TOP_LEFT);
        window.setPrefWidth(500);
        window.setMaxWidth(500);
        window.getStyleClass().add("popupWindow");
        window.setPrefHeight(Region.USE_COMPUTED_SIZE);
        window.setMaxHeight(Region.USE_PREF_SIZE);
        window.setVisible(false);
        window.getChildren().addAll(windowContainer, closeButtonPane);

        StackPane.setAlignment(closeButtonPane, Pos.TOP_RIGHT);
        closeButtonPane.setPrefSize(25, 25);
        closeButtonPane.setMaxSize(25, 25);
        closeButtonPane.getChildren().addAll(closeButton, closeButtonIcon);
        closeButtonPane.setTranslateX(5);

        closeButton.setPrefWidth(25);
        closeButton.setPrefHeight(25);
        closeButton.setRipplerFill(Color.WHITE);
        closeButton.getStyleClass().add("popupWindowCloseButton");
        closeButton.setCursor(Cursor.HAND);
        closeButton.setOpacity(0);
        closeButton.setText(null);
        closeButton.setOnAction(e -> this.hide());

        closeButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> AnimationsClass.fadeAnimation(200, closeButton, 0, 1, false, 1, true));

        closeButton.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> AnimationsClass.fadeAnimation(200, closeButton, 1, 0, false, 1, true));

        closeButtonSVG.setContent(App.svgMap.get(SVG.CLOSE));

        closeButtonIcon.setShape(closeButtonSVG);
        closeButtonIcon.setMinSize(13, 13);
        closeButtonIcon.setPrefSize(13, 13);
        closeButtonIcon.setMaxSize(13, 13);
        closeButtonIcon.setMouseTransparent(true);
        closeButtonIcon.getStyleClass().add("menuIcon");

        windowContainer.setPadding(new Insets(15, 15, 15, 15));
        windowContainer.getChildren().addAll(titleContainer, hotkeyContainer, warningLabel, buttonContainer);
        windowContainer.setSpacing(15);

        titleContainer.getChildren().addAll(title, actionLabel);
        titleContainer.setSpacing(10);

        title.getStyleClass().addAll("popupWindowTitle", "hotkeyChangeWindowTitle");

        actionLabel.getStyleClass().add("popupWindowText");

        hotkeyContainer.getChildren().addAll(hotkeyText, hotkeyBox);
        hotkeyContainer.setSpacing(10);

        hotkeyText.getStyleClass().add("popupWindowText");

        hotkeyBox.setSpacing(5);
        hotkeyBox.setAlignment(Pos.CENTER_LEFT);


        warningLabel.getStyleClass().add("popupWindowText");
        warningLabel.setVisible(false);

        buttonContainer.getChildren().addAll(mainButton, secondaryButton);
        buttonContainer.getStyleClass().add("buttonContainer");
        buttonContainer.setPadding(new Insets(20, 0, 0, 0));

        secondaryButton.setText("Cancel");
        secondaryButton.getStyleClass().add("menuButton");
        secondaryButton.setCursor(Cursor.HAND);
        secondaryButton.setOnAction(e -> this.hide());
        secondaryButton.setTextAlignment(TextAlignment.CENTER);
        secondaryButton.setPrefWidth(155);
        StackPane.setAlignment(secondaryButton, Pos.CENTER_RIGHT);

        mainButton.setText("Unset");
        mainButton.getStyleClass().add("mainButton");
        mainButton.setCursor(Cursor.HAND);
        mainButton.setTextAlignment(TextAlignment.CENTER);
        mainButton.setPrefWidth(155);
        mainButton.disableProperty().bind(isValid.not());
        StackPane.setAlignment(mainButton, Pos.CENTER_LEFT);
    }

    public void show(ControlItem controlItem){

        initializeWindow(controlItem);

        mainController.hotkeyController.setKeybindChangeActive(true);

        if(mainController.closeConfirmationWindow.showing){
            mainController.closeConfirmationWindow.window.setVisible(false);
            mainController.closeConfirmationWindow.showing = false;
        }

        if(mainController.addYoutubeVideoWindow.showing){
            mainController.addYoutubeVideoWindow.window.setVisible(false);
            mainController.addYoutubeVideoWindow.showing = false;
        }

        this.showing = true;
        window.setVisible(true);

        mainController.popupWindowContainer.setMouseTransparent(false);
        AnimationsClass.fadeAnimation(100, mainController.popupWindowContainer, 0 , 1, false, 1, true);
    }

    public void hide(){
        this.showing = false;
        this.controlItem = null;
        this.action = null;
        this.hotkey = null;

        mainController.hotkeyController.setKeybindChangeActive(false);

        mainController.popupWindowContainer.setMouseTransparent(true);
        AnimationsClass.fadeAnimation(100, mainController.popupWindowContainer, 1, 0, false, 1, true);
    }


    private void initializeWindow(ControlItem controlItem){
        this.controlItem = controlItem;
        this.action = controlItem.action;
        this.hotkey = HotkeyController.actionKeybindMap.get(action);

        warningLabel.setVisible(false);
        actionLabel.setText(action.getContent());

    }


    public void updateHotkey(KeyCode[] keyCodes){
        this.hotkey = keyCodes;

        isValid.set(false);
        warningLabel.setVisible(false);

        loadHotkeyBox();
    }

    public void checkHotkey(){
        if(this.hotkey == null || this.hotkey.length < 1) return;
        if(!HotkeyController.invalidKeys.contains(hotkey[hotkey.length -1])){

            String hotkeyString = Arrays.toString(hotkey);

            if(HotkeyController.keybindActionMap.containsKey(hotkeyString)){
                if(HotkeyController.keybindActionMap.get(hotkeyString) == this.action){
                    mainButton.setText("Unset");
                    mainButton.setOnAction(e -> unbindHotkey());

                    warningLabel.setVisible(false);
                }
                else {
                    mainButton.setText("Assign");
                    mainButton.setOnAction(e -> saveHotkey());

                    Action assignedAction = HotkeyController.keybindActionMap.get(hotkeyString);

                    warningLabel.setText("Warning. This hotkey has already been assigned to action: " + assignedAction.getContent() + ". Assign hotkey to this action instead?");
                    warningLabel.setVisible(true);
                }

            }
            else {
                mainButton.setText("Assign");
                mainButton.setOnAction(e -> saveHotkey());

                warningLabel.setVisible(false);
            }

            isValid.set(true);
        }
        else {
            isValid.set(false);
            warningLabel.setText("Error. Invalid hotkey!");
            warningLabel.setVisible(true);
        }
    }

    private void loadHotkeyBox(){

        hotkeyBox.getChildren().clear();

        for (KeyCode keyCode : hotkey) {
            Label keyLabel;
            keyLabel = new Label(ControlItem.symbols.getOrDefault(keyCode, keyCode.getName()));
            keyLabel.getStyleClass().add("keycap");

            StackPane keycapContainer = new StackPane();
            keycapContainer.getStyleClass().add("keycapContainer");
            keycapContainer.getChildren().add(keyLabel);
            keycapContainer.setPadding(new Insets(0, 0, 4, 0));
            keycapContainer.setBackground(new Background(new BackgroundFill(Color.rgb(55, 55, 55), new CornerRadii(6), Insets.EMPTY)));

            Label plus = new Label("+");
            plus.getStyleClass().add("toggleText");

            hotkeyBox.getChildren().addAll(keycapContainer, plus);
        }

        hotkeyBox.getChildren().remove(hotkeyBox.getChildren().size() - 1);
    }

    private void unbindHotkey(){

    }

    private void saveHotkey(){

    }
}
