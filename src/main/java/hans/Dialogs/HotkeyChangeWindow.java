package hans.Dialogs;

import com.jfoenix.controls.JFXButton;
import hans.*;
import hans.Menu.Settings.Action;
import hans.Menu.Settings.ControlItem;
import javafx.animation.FadeTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.util.Arrays;

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

    public boolean showing = false;
    BooleanProperty isValid = new SimpleBooleanProperty(true);

    ControlItem controlItem;
    Action action;
    public KeyCode[] hotkey;

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
        window.getChildren().addAll(windowContainer, buttonContainer, closeButtonPane);

        StackPane.setAlignment(closeButtonPane, Pos.TOP_RIGHT);
        StackPane.setMargin(closeButtonPane, new Insets(15, 15, 0 ,0));
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
        closeButton.addEventFilter(KeyEvent.ANY, Event::consume);

        closeButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> AnimationsClass.fadeAnimation(200, closeButton, 0, 1, false, 1, true));

        closeButton.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> AnimationsClass.fadeAnimation(200, closeButton, 1, 0, false, 1, true));

        closeButtonSVG.setContent(SVG.CLOSE.getContent());

        closeButtonIcon.setShape(closeButtonSVG);
        closeButtonIcon.setMinSize(13, 13);
        closeButtonIcon.setPrefSize(13, 13);
        closeButtonIcon.setMaxSize(13, 13);
        closeButtonIcon.setMouseTransparent(true);
        closeButtonIcon.getStyleClass().add("menuIcon");

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


        warningLabel.getStyleClass().add("popupWindowText");
        warningLabel.setVisible(false);
        warningLabel.setWrapText(true);
        warningLabel.setMinHeight(70);

        StackPane.setAlignment(buttonContainer, Pos.BOTTOM_CENTER);
        buttonContainer.getChildren().addAll(mainButton, secondaryButton);
        buttonContainer.getStyleClass().add("buttonContainer");
        buttonContainer.setPadding(new Insets(0, 15, 0, 15));
        buttonContainer.setPrefHeight(80);
        buttonContainer.setMaxHeight(80);

        secondaryButton.setText("Cancel");
        secondaryButton.getStyleClass().add("menuButton");
        secondaryButton.setCursor(Cursor.HAND);
        secondaryButton.setOnAction(e -> this.hide());
        secondaryButton.setTextAlignment(TextAlignment.CENTER);
        secondaryButton.setPrefWidth(155);
        secondaryButton.addEventFilter(KeyEvent.ANY, Event::consume);
        StackPane.setAlignment(secondaryButton, Pos.CENTER_RIGHT);

        mainButton.setText("Unset");
        mainButton.getStyleClass().add("menuButton");
        mainButton.setCursor(Cursor.HAND);
        mainButton.setTextAlignment(TextAlignment.CENTER);
        mainButton.setPrefWidth(155);
        mainButton.disableProperty().bind(isValid.not());
        mainButton.addEventFilter(KeyEvent.ANY, Event::consume);
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

        if(mainController.licenseWindow.showing){
            mainController.licenseWindow.window.setVisible(false);
            mainController.licenseWindow.showing = false;
        }

        if(mainController.thirdPartySoftwareWindow.showing){
            mainController.thirdPartySoftwareWindow.window.setVisible(false);
            mainController.thirdPartySoftwareWindow.showing = false;
        }

        if(mainController.technicalDetailsWindow.showing){
            mainController.technicalDetailsWindow.window.setVisible(false);
            mainController.technicalDetailsWindow.showing = false;

            mainController.technicalDetailsWindow.fileBox.getChildren().clear();
            mainController.technicalDetailsWindow.videoBox.getChildren().clear();
            mainController.technicalDetailsWindow.audioBox.getChildren().clear();
            mainController.technicalDetailsWindow.subtitlesBox.getChildren().clear();
            mainController.technicalDetailsWindow.attachmentsBox.getChildren().clear();

            mainController.technicalDetailsWindow.technicalDetailsScroll.setVvalue(0);
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

            if(mainController.hotkeyController.keybindActionMap.containsKey(hotkeyString)){
                if(mainController.hotkeyController.keybindActionMap.get(hotkeyString) == this.action){
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

                    Action assignedAction = mainController.hotkeyController.keybindActionMap.get(hotkeyString);

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

        if(mainController.hotkeyController.actionKeybindMap.get(action).length == 0) return;

        String hotkeyString = Arrays.toString(mainController.hotkeyController.actionKeybindMap.get(action));

        mainController.hotkeyController.keybindActionMap.remove(hotkeyString);
        mainController.hotkeyController.actionKeybindMap.put(action, new KeyCode[0]);
        mainController.pref.preferences.put(action.toString(), "[]");

        controlItem.keybindBox.getChildren().clear();

        controlItem.controlsSection.resetButton.setDisable(false);

        updateTooltip(action);

        hide();
    }

    private void saveHotkey(){

        if(Arrays.equals(mainController.hotkeyController.actionKeybindMap.get(action), hotkey)) return;

        String oldHotkeyString = Arrays.toString(mainController.hotkeyController.actionKeybindMap.get(action));

        mainController.hotkeyController.keybindActionMap.remove(oldHotkeyString);

        String newHotkeyString = Arrays.toString(hotkey);

        if(mainController.hotkeyController.keybindActionMap.containsKey(newHotkeyString)){
            //hotkey is already assigned to another action, have to unbind that action.
            Action duplicateAction = mainController.hotkeyController.keybindActionMap.get(newHotkeyString);
            mainController.hotkeyController.actionKeybindMap.put(duplicateAction, new KeyCode[0]);
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

        mainController.hotkeyController.keybindActionMap.put(newHotkeyString, action);
        mainController.hotkeyController.actionKeybindMap.put(action, hotkey);
        mainController.pref.preferences.put(action.toString(), newHotkeyString);

        controlItem.keybindBox.getChildren().clear();
        controlItem.loadKeyLabel(hotkey);

        controlItem.controlsSection.resetButton.setDisable(mainController.hotkeyController.isDefault());

        updateTooltip(action);

        hide();
    }


    private void updateTooltip(Action action){
        switch (action){
            case PLAY_PAUSE2 -> {
                mainController.getControlBarController().play.updateHotkeyText(mainController.hotkeyController.getHotkeyString(action));
                if(mainController.miniplayerActive) mainController.miniplayer.miniplayerController.playButtonTooltip.updateHotkeyText(mainController.hotkeyController.getHotkeyString(action));
            }
            case MUTE -> mainController.getControlBarController().mute.updateHotkeyText(mainController.hotkeyController.getHotkeyString(action));
            case NEXT -> {
                mainController.getControlBarController().nextVideoTooltip.updateHotkeyText(mainController.hotkeyController.getHotkeyString(action));
                if(mainController.miniplayerActive) mainController.miniplayer.miniplayerController.nextVideoButtonTooltip.updateHotkeyText(mainController.hotkeyController.getHotkeyString(action));
            }
            case PREVIOUS -> {
                mainController.getControlBarController().previousVideoTooltip.updateHotkeyText(mainController.hotkeyController.getHotkeyString(action));
                if(mainController.miniplayerActive) mainController.miniplayer.miniplayerController.previousVideoButtonTooltip.updateHotkeyText(mainController.hotkeyController.getHotkeyString(action));

            }
            case FULLSCREEN -> mainController.getControlBarController().fullScreen.updateHotkeyText(mainController.hotkeyController.getHotkeyString(action));
            case MINIPLAYER -> mainController.getControlBarController().miniplayer.updateHotkeyText(mainController.hotkeyController.getHotkeyString(action));
            case SUBTITLES -> mainController.getControlBarController().subtitles.updateHotkeyText(mainController.hotkeyController.getHotkeyString(action));
            case PLAYBACK_SETTINGS -> mainController.getControlBarController().settings.updateHotkeyText(mainController.hotkeyController.getHotkeyString(action));
            case MENU -> mainController.openMenuTooltip.updateHotkeyText(mainController.hotkeyController.getHotkeyString(action));
            case OPEN_QUEUE -> mainController.getMenuController().menuBar.queueButton.controlTooltip.updateHotkeyText(mainController.hotkeyController.getHotkeyString(action));
            case OPEN_RECENT_MEDIA -> mainController.getMenuController().menuBar.historyButton.controlTooltip.updateHotkeyText(mainController.hotkeyController.getHotkeyString(action));
            case OPEN_MUSIC_LIBRARY -> mainController.getMenuController().menuBar.musicLibraryButton.controlTooltip.updateHotkeyText(mainController.hotkeyController.getHotkeyString(action));
            case OPEN_PLAYLISTS -> mainController.getMenuController().menuBar.playlistsButton.controlTooltip.updateHotkeyText(mainController.hotkeyController.getHotkeyString(action));
            case OPEN_SETTINGS -> mainController.getMenuController().menuBar.settingsButton.controlTooltip.updateHotkeyText(mainController.hotkeyController.getHotkeyString(action));
        }
    }
}
