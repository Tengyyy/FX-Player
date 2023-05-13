package tengy.Menu.Settings;

import tengy.HotkeyController;
import tengy.Subtitles.SubtitlesState;
import tengy.PlaybackSettings.PlaybackSettingsState;
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
            if(settingsPage.menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) settingsPage.menuController.subtitlesController.closeSubtitles();
            if(settingsPage.menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) settingsPage.menuController.playbackSettingsController.closeSettings();

            if(settingsPage.settingsMenu.showing) settingsPage.settingsMenu.hide();

            resetToDefault();
        });
        resetButton.setFocusTraversable(false);

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
        resetButton.setDisable(settingsPage.menuController.mainController.hotkeyController.isDefault());
        for(int i=0; i < actionOrder.size(); i++){
            controlsBox.getChildren().add(new ControlItem(this, actionOrder.get(i), settingsPage.menuController.mainController.hotkeyController.actionKeybindMap.get(actionOrder.get(i)), i % 2 != 0));
        }
    }
}
