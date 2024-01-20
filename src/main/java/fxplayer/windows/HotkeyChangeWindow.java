package fxplayer.windows;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.css.PseudoClass;
import fxplayer.*;
import fxplayer.menu.Settings.Action;
import fxplayer.menu.Settings.ControlItem;
import javafx.animation.FadeTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static fxplayer.Utilities.keyboardFocusOff;
import static fxplayer.Utilities.keyboardFocusOn;

public class HotkeyChangeWindow {

    WindowController windowController;
    MainController mainController;
    HotkeyController hotkeyController;

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

    Region closeButtonIcon = new Region();
    SVGPath closeButtonSVG = new SVGPath();
    Button closeButton = new Button();

    public boolean showing = false;
    BooleanProperty isValid = new SimpleBooleanProperty(true);

    ControlItem controlItem;
    Action action;
    public KeyCode[] hotkey;

    IntegerProperty focus = new SimpleIntegerProperty(-1);
    List<Node> focusNodes = new ArrayList<>();

    public HotkeyChangeWindow(WindowController windowController){
        this.windowController = windowController;
        this.mainController = windowController.mainController;
        this.hotkeyController = mainController.hotkeyController;

        mainController.popupWindowContainer.getChildren().add(window);

        window.setAlignment(Pos.TOP_LEFT);
        window.setPrefWidth(500);
        window.setMaxWidth(500);
        window.getStyleClass().add("popupWindow");
        window.setPrefHeight(Region.USE_COMPUTED_SIZE);
        window.setMaxHeight(Region.USE_PREF_SIZE);
        window.setVisible(false);
        window.getChildren().addAll(windowContainer, buttonContainer, closeButton);
        window.setOnMouseClicked(e -> window.requestFocus());

        StackPane.setAlignment(closeButton, Pos.TOP_RIGHT);
        StackPane.setMargin(closeButton, new Insets(10, 10, 0 ,0));
        closeButton.setPrefWidth(25);
        closeButton.setPrefHeight(25);
        closeButton.getStyleClass().addAll("transparentButton", "popupWindowCloseButton");
        closeButton.setOnAction(e -> this.hide());
        closeButton.setFocusTraversable(false);
        closeButton.setGraphic(closeButtonIcon);
        closeButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(0);
            }
            else{
                keyboardFocusOff(closeButton);
                focus.set(-1);
            }
        });

        closeButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            closeButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        closeButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            closeButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });

        closeButtonSVG.setContent(SVG.CLOSE.getContent());

        closeButtonIcon.setShape(closeButtonSVG);
        closeButtonIcon.setMinSize(13, 13);
        closeButtonIcon.setPrefSize(13, 13);
        closeButtonIcon.setMaxSize(13, 13);
        closeButtonIcon.setMouseTransparent(true);
        closeButtonIcon.getStyleClass().add("graphic");

        windowContainer.setPadding(new Insets(15, 15, 15, 15));
        windowContainer.getChildren().addAll(titleContainer, hotkeyContainer, warningLabel);
        windowContainer.setSpacing(20);
        StackPane.setMargin(windowContainer, new Insets(0, 0, 80, 0));

        titleContainer.getChildren().addAll(title, actionLabel);
        titleContainer.setSpacing(10);

        title.getStyleClass().addAll("popupWindowTitle", "hotkeyChangeWindowTitle");

        actionLabel.getStyleClass().add("popupWindowText");

        hotkeyContainer.getChildren().addAll(hotkeyText, hotkeyBox);
        hotkeyContainer.setSpacing(10);

        hotkeyText.getStyleClass().add("popupWindowText");

        hotkeyBox.setSpacing(5);
        hotkeyBox.setAlignment(Pos.CENTER_LEFT);
        hotkeyBox.setId("hotkeyBox");
        hotkeyBox.setMinHeight(50);
        hotkeyBox.setPadding(new Insets(7, 15, 7, 15));
        hotkeyBox.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue) focus.set(1);
            else {
                keyboardFocusOff(hotkeyBox);
                focus.set(-1);
            }
        });


        warningLabel.getStyleClass().add("popupWindowText");
        warningLabel.setVisible(false);
        warningLabel.setWrapText(true);
        warningLabel.setMinHeight(80);

        StackPane.setAlignment(buttonContainer, Pos.BOTTOM_CENTER);
        buttonContainer.getChildren().addAll(mainButton, secondaryButton);
        buttonContainer.getStyleClass().add("buttonContainer");
        buttonContainer.setPadding(new Insets(0, 15, 0, 15));
        buttonContainer.setPrefHeight(80);
        buttonContainer.setMaxHeight(80);

        secondaryButton.setText("Cancel");
        secondaryButton.getStyleClass().add("menuButton");
        secondaryButton.setOnAction(e -> this.hide());
        secondaryButton.setTextAlignment(TextAlignment.CENTER);
        secondaryButton.setPrefWidth(155);
        secondaryButton.setFocusTraversable(false);
        StackPane.setAlignment(secondaryButton, Pos.CENTER_RIGHT);
        secondaryButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            secondaryButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        secondaryButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            secondaryButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });

        secondaryButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(focusNodes.size() - 1);
            }
            else {
                keyboardFocusOff(secondaryButton);
                focus.set(-1);
            }
        });

        mainButton.setText("Unset");
        mainButton.getStyleClass().add("menuButton");
        mainButton.setTextAlignment(TextAlignment.CENTER);
        mainButton.setPrefWidth(155);
        mainButton.disableProperty().bind(isValid.not());
        mainButton.disabledProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focusNodes.remove(mainButton);
                focus.set(Math.min(focus.get(), focusNodes.size() - 1));
            }
            else if(!focusNodes.contains(mainButton)){
                focusNodes.add(2, mainButton);
                if(secondaryButton.isFocused()) focus.set(3);
            }
        });
        mainButton.setFocusTraversable(false);
        mainButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            mainButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        mainButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            mainButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });

        mainButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(2);
            }
            else {
                keyboardFocusOff(mainButton);
                focus.set(-1);
            }
        });
        StackPane.setAlignment(mainButton, Pos.CENTER_LEFT);


        focusNodes.add(closeButton);
        focusNodes.add(hotkeyBox);
        focusNodes.add(mainButton);
        focusNodes.add(secondaryButton);
    }

    public void show(ControlItem controlItem){

        initializeWindow(controlItem);

        hotkeyController.setKeybindChangeActive(true);

        windowController.updateState(WindowState.HOTKEY_CHANGE_WINDOW_OPEN);


        this.showing = true;
        window.setVisible(true);

        mainController.popupWindowContainer.setMouseTransparent(false);

        window.requestFocus();

        AnimationsClass.fadeAnimation(100, mainController.popupWindowContainer, 0 , 1, false, 1, true);
    }

    public void hide(){
        this.showing = false;
        this.controlItem = null;
        this.action = null;
        this.hotkey = null;

        windowController.windowState = WindowState.CLOSED;

        hotkeyController.setKeybindChangeActive(false);

        mainController.popupWindowContainer.setMouseTransparent(true);

        FadeTransition fadeTransition = new FadeTransition(Duration.millis(100), mainController.popupWindowContainer);
        fadeTransition.setFromValue(mainController.popupWindowContainer.getOpacity());
        fadeTransition.setToValue(0);
        fadeTransition.setOnFinished(e -> window.setVisible(false));
        fadeTransition.play();    }


    private void initializeWindow(ControlItem controlItem){
        this.controlItem = controlItem;
        this.action = controlItem.action;
        this.hotkey = mainController.hotkeyController.actionKeybindMap.get(action);

        mainButton.getStyleClass().remove("mainButton");
        if(!mainButton.getStyleClass().contains("menuButton")) mainButton.getStyleClass().add("menuButton");
        isValid.set(hotkey.length > 0);
        mainButton.setText("Unset");
        mainButton.setOnAction(e -> unbindHotkey());

        loadHotkeyBox();

        warningLabel.setVisible(false);
        warningLabel.setText("");
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

            if(hotkeyController.keybindActionMap.containsKey(hotkeyString)){
                if(hotkeyController.keybindActionMap.get(hotkeyString) == this.action){
                    mainButton.setText("Unset");
                    mainButton.setOnAction(e -> unbindHotkey());
                    mainButton.getStyleClass().remove("mainButton");
                    if(!mainButton.getStyleClass().contains("menuButton")) mainButton.getStyleClass().add("menuButton");

                    warningLabel.setVisible(false);
                    warningLabel.setText("");
                }
                else {
                    mainButton.setText("Assign");
                    mainButton.getStyleClass().remove("menuButton");
                    if(!mainButton.getStyleClass().contains("mainButton")) mainButton.getStyleClass().add("mainButton");

                    mainButton.setOnAction(e -> saveHotkey());

                    Action assignedAction = hotkeyController.keybindActionMap.get(hotkeyString);

                    warningLabel.setText("Warning. This hotkey has already been assigned to action:\n" + assignedAction.getContent() + ".\nAssign hotkey to this action instead?");
                    warningLabel.setVisible(true);
                }

            }
            else {
                mainButton.setText("Assign");
                mainButton.getStyleClass().remove("menuButton");
                if(!mainButton.getStyleClass().contains("mainButton")) mainButton.getStyleClass().add("mainButton");
                mainButton.setOnAction(e -> saveHotkey());

                warningLabel.setVisible(false);
                warningLabel.setText("");
            }

            isValid.set(true);
        }
        else {
            isValid.set(false);
            mainButton.getStyleClass().remove("menuButton");
            if(!mainButton.getStyleClass().contains("mainButton")) mainButton.getStyleClass().add("mainButton");
            warningLabel.setText("Error. Invalid hotkey!");
            warningLabel.setVisible(true);
        }
    }

    private void loadHotkeyBox(){

        hotkeyBox.getChildren().clear();

        for (KeyCode keyCode : hotkey) {
            Label keyLabel;
            keyLabel = new Label(HotkeyController.symbols.getOrDefault(keyCode, keyCode.getName()));
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

        if(!hotkeyBox.getChildren().isEmpty()) hotkeyBox.getChildren().remove(hotkeyBox.getChildren().size() - 1);
    }

    private void unbindHotkey(){

        if(hotkeyController.actionKeybindMap.get(action).length == 0) return;

        String hotkeyString = Arrays.toString(hotkeyController.actionKeybindMap.get(action));

        hotkeyController.keybindActionMap.remove(hotkeyString);
        hotkeyController.actionKeybindMap.put(action, new KeyCode[0]);
        mainController.pref.preferences.put(action.toString(), "[]");

        controlItem.keybindBox.getChildren().clear();

        controlItem.controlsSection.resetButton.setDisable(false);

        updateTooltip(action);

        hide();
    }

    private void saveHotkey(){

        if(Arrays.equals(hotkeyController.actionKeybindMap.get(action), hotkey)) return;

        String oldHotkeyString = Arrays.toString(hotkeyController.actionKeybindMap.get(action));

        hotkeyController.keybindActionMap.remove(oldHotkeyString);

        String newHotkeyString = Arrays.toString(hotkey);

        if(hotkeyController.keybindActionMap.containsKey(newHotkeyString)){
            //hotkey is already assigned to another action, have to unbind that action.
            Action duplicateAction = hotkeyController.keybindActionMap.get(newHotkeyString);
            hotkeyController.actionKeybindMap.put(duplicateAction, new KeyCode[0]);
            mainController.pref.preferences.put(duplicateAction.toString(), "[]");

            updateTooltip(duplicateAction);

            for(Node node : mainController.getMenuController().settingsPage.controlsSection.controlsBox.getChildren()){
                ControlItem duplicateItem = (ControlItem) node;
                if(duplicateItem.action == duplicateAction){
                    duplicateItem.keybindBox.getChildren().clear();
                    break;
                }
            }
        }

        hotkeyController.keybindActionMap.put(newHotkeyString, action);
        hotkeyController.actionKeybindMap.put(action, hotkey);
        mainController.pref.preferences.put(action.toString(), newHotkeyString);

        controlItem.keybindBox.getChildren().clear();
        controlItem.loadKeyLabel(hotkey);

        controlItem.controlsSection.resetButton.setDisable(hotkeyController.isDefault());

        updateTooltip(action);

        hide();
    }


    private void updateTooltip(Action action){
        switch (action){
            case PLAY_PAUSE2 -> {
                mainController.getControlBarController().play.updateHotkeyText(hotkeyController.getHotkeyString(action));
                if(mainController.miniplayerActive) mainController.miniplayer.miniplayerController.playButtonTooltip.updateHotkeyText(hotkeyController.getHotkeyString(action));
            }
            case MUTE -> mainController.getControlBarController().mute.updateHotkeyText(hotkeyController.getHotkeyString(action));
            case NEXT -> {
                mainController.getControlBarController().nextVideoTooltip.updateHotkeyText(hotkeyController.getHotkeyString(action));
                if(mainController.miniplayerActive) mainController.miniplayer.miniplayerController.nextVideoButtonTooltip.updateHotkeyText(hotkeyController.getHotkeyString(action));
            }
            case PREVIOUS -> {
                mainController.getControlBarController().previousVideoTooltip.updateHotkeyText(hotkeyController.getHotkeyString(action));
                if(mainController.miniplayerActive) mainController.miniplayer.miniplayerController.previousVideoButtonTooltip.updateHotkeyText(hotkeyController.getHotkeyString(action));

            }
            case FULLSCREEN -> mainController.getControlBarController().fullScreen.updateHotkeyText(hotkeyController.getHotkeyString(action));
            case MINIPLAYER -> mainController.getControlBarController().miniplayer.updateHotkeyText(hotkeyController.getHotkeyString(action));
            case SUBTITLES -> mainController.getControlBarController().subtitles.updateHotkeyText(hotkeyController.getHotkeyString(action));
            case PLAYBACK_SETTINGS -> mainController.getControlBarController().settings.updateHotkeyText(hotkeyController.getHotkeyString(action));
            case MENU -> mainController.openMenuTooltip.updateHotkeyText(hotkeyController.getHotkeyString(action));
            case OPEN_QUEUE -> mainController.getMenuController().menuBar.queueButton.controlTooltip.updateHotkeyText(hotkeyController.getHotkeyString(action));
            case OPEN_RECENT_MEDIA -> mainController.getMenuController().menuBar.historyButton.controlTooltip.updateHotkeyText(hotkeyController.getHotkeyString(action));
            case OPEN_MUSIC_LIBRARY -> mainController.getMenuController().menuBar.musicLibraryButton.controlTooltip.updateHotkeyText(hotkeyController.getHotkeyString(action));
            case OPEN_PLAYLISTS -> mainController.getMenuController().menuBar.playlistsButton.controlTooltip.updateHotkeyText(hotkeyController.getHotkeyString(action));
            case OPEN_SETTINGS -> mainController.getMenuController().menuBar.settingsButton.controlTooltip.updateHotkeyText(hotkeyController.getHotkeyString(action));
        }
    }

    public void focusForward(){
        int newFocus;

        if(focus.get() >= focusNodes.size() - 1 || focus.get() == -1) newFocus = 0;
        else newFocus = focus.get() + 1;

        keyboardFocusOn(focusNodes.get(newFocus));
    }

    public void focusBackward(){
        int newFocus;

        if(focus.get() == 0) newFocus = focusNodes.size() - 1;
        else if(focus.get() == -1) newFocus = 0;
        else newFocus = focus.get() - 1;

        keyboardFocusOn(focusNodes.get(newFocus));
    }
}
