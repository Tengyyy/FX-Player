package tengy.menu.Settings;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.css.PseudoClass;
import javafx.scene.input.KeyEvent;
import tengy.HotkeyController;
import tengy.menu.FocusableMenuButton;
import tengy.subtitles.SubtitlesState;
import tengy.playbackSettings.PlaybackSettingsState;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import tengy.Utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static tengy.Utilities.keyboardFocusOff;
import static tengy.Utilities.keyboardFocusOn;


public class ControlsSection extends VBox  implements SettingsSection{

    SettingsPage settingsPage;

    StackPane titlePane = new StackPane();
    Label title = new Label("Controls");

    StackPane controlsHeader = new StackPane();
    Label actionHeader = new Label("Action");
    Label hotkeyHeader = new Label("Hotkey");

    VBox controlsWrapper = new VBox();
    public VBox controlsBox = new VBox();
    StackPane resetBox = new StackPane();
    public FocusableMenuButton resetButton = new FocusableMenuButton();

    public static final List<Action> actionOrder = List.of(Action.PLAY_PAUSE1, Action.PLAY_PAUSE2, Action.MUTE, Action.VOLUME_UP5, Action.VOLUME_DOWN5, Action.VOLUME_UP1, Action.VOLUME_DOWN1, Action.FORWARD5, Action.REWIND5, Action.FORWARD10, Action.REWIND10, Action.FRAME_FORWARD, Action.FRAME_BACKWARD, Action.SEEK0, Action.SEEK10, Action.SEEK20, Action.SEEK30, Action.SEEK40, Action.SEEK50, Action.SEEK60, Action.SEEK70, Action.SEEK80, Action.SEEK90, Action.PLAYBACK_SPEED_UP25, Action.PLAYBACK_SPEED_DOWN25, Action.PLAYBACK_SPEED_UP5, Action.PLAYBACK_SPEED_DOWN5, Action.NEXT, Action.PREVIOUS, Action.END, Action.FULLSCREEN, Action.SNAPSHOT, Action.MINIPLAYER, Action.SUBTITLES, Action.PLAYBACK_SETTINGS, Action.MENU, Action.CLEAR_QUEUE, Action.SHUFFLE, Action.AUTOPLAY, Action.LOOP, Action.OPEN_QUEUE, Action.OPEN_RECENT_MEDIA, Action.OPEN_MUSIC_LIBRARY, Action.OPEN_PLAYLISTS, Action.OPEN_SETTINGS, Action.OPEN_EQUALIZER);


    IntegerProperty focus = new SimpleIntegerProperty(-1);
    List<Node> focusNodes = new ArrayList<>();

    ControlsSection(SettingsPage settingsPage){

        this.settingsPage = settingsPage;


        this.getChildren().addAll(titlePane, controlsWrapper, resetBox);

        titlePane.getChildren().addAll(title);
        VBox.setMargin(controlsWrapper, new Insets(20, 0, 0, 0));
        VBox.setMargin(resetBox, new Insets(15, 0, 0, 0));

        StackPane.setAlignment(title, Pos.CENTER_LEFT);
        title.getStyleClass().add("settingsSectionTitle");

        controlsWrapper.getChildren().addAll(controlsHeader, controlsBox);
        controlsWrapper.setSpacing(5);

        controlsHeader.getChildren().addAll(actionHeader, hotkeyHeader);
        controlsHeader.setPadding(new Insets(0, 0, 0, 50));

        StackPane.setAlignment(actionHeader, Pos.CENTER_LEFT);
        actionHeader.getStyleClass().add("controlsHeaderText");

        hotkeyHeader.prefWidthProperty().bind(this.widthProperty().subtract(10).divide(2));
        hotkeyHeader.maxWidthProperty().bind(this.widthProperty().subtract(10).divide(2));
        hotkeyHeader.getStyleClass().add("controlsHeaderText");
        StackPane.setAlignment(hotkeyHeader, Pos.CENTER_RIGHT);

        controlsBox.setSpacing(10);

        resetBox.getChildren().add(resetButton);
        StackPane.setAlignment(resetButton, Pos.CENTER_RIGHT);
        resetButton.getStyleClass().add("menuButton");
        resetButton.setText("Reset to default");
        resetButton.setOnAction(e -> {
            if(settingsPage.menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) settingsPage.menuController.subtitlesController.closeSubtitles();
            if(settingsPage.menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) settingsPage.menuController.playbackSettingsController.closeSettings();

            resetButton.requestFocus();
            resetToDefault();
        });
        resetButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(focusNodes.size() - 1);
                settingsPage.focus.set(3);
            }
            else {
                keyboardFocusOff(resetButton);
                focus.set(-1);
                settingsPage.focus.set(-1);
            }
        });

        resetButton.disabledProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue) focusNodes.remove(resetButton);
            else focusNodes.add(resetButton);
        });

        resetButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            resetButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        resetButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            resetButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });


    }

    private void resetToDefault(){

        settingsPage.menuController.mainController.hotkeyController.actionKeybindMap.putAll(HotkeyController.defaultControls);

        settingsPage.menuController.mainController.hotkeyController.keybindActionMap.clear();

        for(Map.Entry<Action, KeyCode[]> entry : HotkeyController.defaultControls.entrySet()){
            settingsPage.menuController.mainController.hotkeyController.keybindActionMap.put(Arrays.toString(entry.getValue()), entry.getKey());
            settingsPage.menuController.mainController.pref.preferences.put(entry.getKey().toString(), Arrays.toString(entry.getValue()));
        }

        for(Node node : controlsBox.getChildren()){
            ControlItem controlItem = (ControlItem) node;
            controlItem.keybindBox.getChildren().clear();
            controlItem.loadKeyLabel(HotkeyController.defaultControls.get(controlItem.action));
        }

        resetButton.setDisable(true);
    }

    public void initializeControlsBox(){
        for (int i = 0; i < actionOrder.size(); i++) {
            ControlItem controlItem = new ControlItem(this, actionOrder.get(i), settingsPage.menuController.mainController.hotkeyController.actionKeybindMap.get(actionOrder.get(i)), i);
            controlsBox.getChildren().add(controlItem);
            focusNodes.add(controlItem);
        }

        boolean disabled = settingsPage.menuController.mainController.hotkeyController.isDefault();
        resetButton.setDisable(disabled);

        if(!disabled) focusNodes.add(resetButton);
    }


    @Override
    public boolean focusForward(){
        if(focus.get() == focusNodes.size() - 1) return true;

        Node node = focusNodes.get(focus.get() + 1);
        keyboardFocusOn(node);
        Utilities.checkScrollDown(settingsPage.settingsScroll, node);

        return false;
    }

    @Override
    public boolean focusBackward(){

        if(focus.get() == 0) return true;
        else if(focus.get() == -1){
            Node node = focusNodes.get(focusNodes.size() - 1);
            keyboardFocusOn(node);
            Utilities.checkScrollUp(settingsPage.settingsScroll, node);
        }
        else {
            Node node = focusNodes.get(focus.get() - 1);
            keyboardFocusOn(node);
            Utilities.checkScrollUp(settingsPage.settingsScroll, node);
        }

        return false;
    }

    @Override
    public void setFocus(int value){
        this.focus.set(value);
    }
}
