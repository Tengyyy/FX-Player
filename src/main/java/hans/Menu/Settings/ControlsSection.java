package hans.Menu.Settings;

import hans.Captions.CaptionsState;
import hans.Settings.SettingsState;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class ControlsSection extends VBox {

    SettingsPage settingsPage;

    StackPane titlePane = new StackPane();
    Label title = new Label("Controls");

    StackPane controlsHeader = new StackPane();
    Label actionHeader = new Label("Action");
    Label hotkeyHeader = new Label("Hotkey");

    VBox controlsWrapper = new VBox();
    public VBox controlsBox = new VBox();
    StackPane resetBox = new StackPane();
    public Button resetButton = new Button("Reset to default");

    public static final Map<Action, KeyCode[]> defaultControls = Map.ofEntries(
        Map.entry(Action.PLAY_PAUSE1, new KeyCode[]{KeyCode.SPACE}),
        Map.entry(Action.PLAY_PAUSE2, new KeyCode[]{KeyCode.K}),
        Map.entry(Action.MUTE, new KeyCode[]{KeyCode.M}),
        Map.entry(Action.VOLUME_UP5, new KeyCode[]{KeyCode.UP}),
        Map.entry(Action.VOLUME_DOWN5, new KeyCode[]{KeyCode.DOWN}),
        Map.entry(Action.VOLUME_UP1, new KeyCode[]{KeyCode.SHIFT, KeyCode.UP}),
        Map.entry(Action.VOLUME_DOWN1, new KeyCode[]{KeyCode.SHIFT, KeyCode.DOWN}),
        Map.entry(Action.FORWARD5, new KeyCode[]{KeyCode.RIGHT}),
        Map.entry(Action.REWIND5, new KeyCode[]{KeyCode.LEFT}),
        Map.entry(Action.FORWARD10, new KeyCode[]{KeyCode.L}),
        Map.entry(Action.REWIND10, new KeyCode[]{KeyCode.J}),
        Map.entry(Action.FRAME_FORWARD, new KeyCode[]{KeyCode.PERIOD}),
        Map.entry(Action.FRAME_BACKWARD, new KeyCode[]{KeyCode.COMMA}),
        Map.entry(Action.SEEK0, new KeyCode[]{KeyCode.DIGIT0}),
        Map.entry(Action.SEEK10, new KeyCode[]{KeyCode.DIGIT1}),
        Map.entry(Action.SEEK20, new KeyCode[]{KeyCode.DIGIT2}),
        Map.entry(Action.SEEK30, new KeyCode[]{KeyCode.DIGIT3}),
        Map.entry(Action.SEEK40, new KeyCode[]{KeyCode.DIGIT4}),
        Map.entry(Action.SEEK50, new KeyCode[]{KeyCode.DIGIT5}),
        Map.entry(Action.SEEK60, new KeyCode[]{KeyCode.DIGIT6}),
        Map.entry(Action.SEEK70, new KeyCode[]{KeyCode.DIGIT7}),
        Map.entry(Action.SEEK80, new KeyCode[]{KeyCode.DIGIT8}),
        Map.entry(Action.SEEK90, new KeyCode[]{KeyCode.DIGIT9}),
        Map.entry(Action.PLAYBACK_SPEED_UP25, new KeyCode[]{KeyCode.SHIFT, KeyCode.PERIOD}),
        Map.entry(Action.PLAYBACK_SPEED_DOWN25, new KeyCode[]{KeyCode.SHIFT, KeyCode.COMMA}),
        Map.entry(Action.PLAYBACK_SPEED_UP5, new KeyCode[]{KeyCode.CONTROL, KeyCode.SHIFT, KeyCode.PERIOD}),
        Map.entry(Action.PLAYBACK_SPEED_DOWN5, new KeyCode[]{KeyCode.CONTROL, KeyCode.SHIFT, KeyCode.COMMA}),
        Map.entry(Action.NEXT, new KeyCode[]{KeyCode.SHIFT, KeyCode.N}),
        Map.entry(Action.PREVIOUS, new KeyCode[]{KeyCode.SHIFT, KeyCode.P}),
        Map.entry(Action.END, new KeyCode[]{KeyCode.END}),
        Map.entry(Action.FULLSCREEN, new KeyCode[]{KeyCode.F}),
        Map.entry(Action.SNAPSHOT, new KeyCode[]{KeyCode.F12}),
        Map.entry(Action.MINIPLAYER, new KeyCode[]{KeyCode.I}),
        Map.entry(Action.SUBTITLES, new KeyCode[]{KeyCode.C}),
        Map.entry(Action.PLAYBACK_SETTINGS, new KeyCode[]{KeyCode.S}),
        Map.entry(Action.MENU, new KeyCode[]{KeyCode.Q}),
        Map.entry(Action.CLEAR_QUEUE, new KeyCode[]{KeyCode.CONTROL, KeyCode.SHIFT, KeyCode.C}),
        Map.entry(Action.SHUFFLE, new KeyCode[]{KeyCode.CONTROL, KeyCode.S}),
        Map.entry(Action.AUTOPLAY, new KeyCode[]{KeyCode.CONTROL, KeyCode.A}),
        Map.entry(Action.LOOP, new KeyCode[]{KeyCode.CONTROL, KeyCode.L}),
        Map.entry(Action.OPEN_QUEUE, new KeyCode[]{KeyCode.CONTROL, KeyCode.SHIFT, KeyCode.Q}),
        Map.entry(Action.OPEN_RECENT_MEDIA, new KeyCode[]{KeyCode.CONTROL, KeyCode.SHIFT, KeyCode.R}),
        Map.entry(Action.OPEN_MUSIC_LIBRARY, new KeyCode[]{KeyCode.CONTROL, KeyCode.SHIFT, KeyCode.M}),
        Map.entry(Action.OPEN_PLAYLISTS, new KeyCode[]{KeyCode.CONTROL, KeyCode.SHIFT, KeyCode.P}),
        Map.entry(Action.OPEN_SETTINGS, new KeyCode[]{KeyCode.CONTROL, KeyCode.SHIFT, KeyCode.S})
    );

    public static final List<Action> actionOrder = List.of(Action.PLAY_PAUSE1, Action.PLAY_PAUSE2, Action.MUTE, Action.VOLUME_UP5, Action.VOLUME_DOWN5, Action.VOLUME_UP1, Action.VOLUME_DOWN1, Action.FORWARD5, Action.REWIND5, Action.FORWARD10, Action.REWIND10, Action.FRAME_FORWARD, Action.FRAME_BACKWARD, Action.SEEK0, Action.SEEK10, Action.SEEK20, Action.SEEK30, Action.SEEK40, Action.SEEK50, Action.SEEK60, Action.SEEK70, Action.SEEK80, Action.SEEK90, Action.PLAYBACK_SPEED_UP25, Action.PLAYBACK_SPEED_DOWN25, Action.PLAYBACK_SPEED_UP5, Action.PLAYBACK_SPEED_DOWN5, Action.NEXT, Action.PREVIOUS, Action.END, Action.FULLSCREEN, Action.SNAPSHOT, Action.MINIPLAYER, Action.SUBTITLES, Action.PLAYBACK_SETTINGS, Action.MENU, Action.CLEAR_QUEUE, Action.SHUFFLE, Action.AUTOPLAY, Action.LOOP, Action.OPEN_QUEUE, Action.OPEN_RECENT_MEDIA, Action.OPEN_MUSIC_LIBRARY, Action.OPEN_PLAYLISTS, Action.OPEN_SETTINGS);

    ControlsSection(SettingsPage settingsPage){

        this.settingsPage = settingsPage;


        this.getChildren().addAll(titlePane, controlsWrapper, resetBox);
        this.setSpacing(20);

        VBox.setMargin(titlePane, new Insets(20, 0, 0, 0));
        titlePane.getChildren().addAll(title);

        StackPane.setAlignment(title, Pos.CENTER_LEFT);
        title.getStyleClass().add("settingsSectionTitle");

        controlsWrapper.getChildren().addAll(controlsHeader, controlsBox);
        controlsWrapper.setSpacing(10);

        controlsHeader.getChildren().addAll(actionHeader, hotkeyHeader);
        controlsHeader.setPadding(new Insets(0, 0, 0, 50));

        StackPane.setAlignment(actionHeader, Pos.CENTER_LEFT);
        actionHeader.getStyleClass().add("settingsText");

        hotkeyHeader.prefWidthProperty().bind(this.widthProperty().subtract(10).divide(2));
        hotkeyHeader.maxWidthProperty().bind(this.widthProperty().subtract(10).divide(2));
        hotkeyHeader.getStyleClass().add("settingsText");
        StackPane.setAlignment(hotkeyHeader, Pos.CENTER_RIGHT);


        controlsBox.getStyleClass().add("borderedSection");

        resetBox.getChildren().add(resetButton);
        StackPane.setAlignment(resetButton, Pos.CENTER_RIGHT);
        resetButton.setCursor(Cursor.HAND);
        resetButton.getStyleClass().add("menuButton");
        resetButton.setOnAction(e -> {
            if(settingsPage.menuController.captionsController.captionsState != CaptionsState.CLOSED) settingsPage.menuController.captionsController.closeCaptions();
            if(settingsPage.menuController.settingsController.settingsState != SettingsState.CLOSED) settingsPage.menuController.settingsController.closeSettings();
            resetToDefault();
        });
        resetButton.setFocusTraversable(false);

    }

    private void resetToDefault(){

        settingsPage.menuController.mainController.hotkeyController.actionKeybindMap.putAll(defaultControls);

        settingsPage.menuController.mainController.hotkeyController.keybindActionMap.clear();

        for(Map.Entry<Action, KeyCode[]> entry : defaultControls.entrySet()){
            settingsPage.menuController.mainController.hotkeyController.keybindActionMap.put(Arrays.toString(entry.getValue()), entry.getKey());
        }

        for(Node node : controlsBox.getChildren()){
            ControlItem controlItem = (ControlItem) node;
            controlItem.keybindBox.getChildren().clear();
            controlItem.loadKeyLabel(defaultControls.get(controlItem.action));
        }

        resetButton.setDisable(true);
    }

    public void initializeControlsBox(){
        resetButton.setDisable(settingsPage.menuController.mainController.hotkeyController.isDefault());
        for(int i=0; i < actionOrder.size(); i++){
            controlsBox.getChildren().add(new ControlItem(this, actionOrder.get(i), settingsPage.menuController.mainController.hotkeyController.actionKeybindMap.get(actionOrder.get(i)), i % 2 != 0));
        }
    }
}
