package hans;

import hans.Subtitles.SubtitlesState;
import hans.Menu.ExpandableTextArea;
import hans.Menu.Settings.Action;
import hans.Menu.Settings.ControlsSection;
import hans.PlaybackSettings.PlaybackSettingsState;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.controlsfx.control.CheckComboBox;

import java.util.*;

public class HotkeyController {

    public Map<String, Action> keybindActionMap = new HashMap<>();

    public HashMap<Action, KeyCode[]> actionKeybindMap = new HashMap<>();

    private static final List<KeyCode> mediaKeys = List.of(KeyCode.LEFT, KeyCode.RIGHT, KeyCode.J, KeyCode.L, KeyCode.REWIND, KeyCode.FAST_FWD, KeyCode.DIGIT0, KeyCode.DIGIT1, KeyCode.DIGIT2, KeyCode.DIGIT3, KeyCode.DIGIT4, KeyCode.DIGIT5, KeyCode.DIGIT6, KeyCode.DIGIT7, KeyCode.DIGIT8, KeyCode.DIGIT9, KeyCode.HOME, KeyCode.END);

    public static final List<KeyCode> invalidKeys = List.of(KeyCode.CONTROL, KeyCode.ALT, KeyCode.SHIFT, KeyCode.TAB, KeyCode.FAST_FWD, KeyCode.REWIND, KeyCode.MUTE, KeyCode.PLAY, KeyCode.PAUSE, KeyCode.TRACK_PREV, KeyCode.TRACK_NEXT, KeyCode.F11, KeyCode.ESCAPE, KeyCode.ENTER);

    MainController mainController;

    private boolean keybindChangeActive = false;

    HotkeyController(MainController mainController){
        this.mainController = mainController;

        keybindActionMap.put("[SPACE]", Action.PLAY_PAUSE1);
        keybindActionMap.put("[K]", Action.PLAY_PAUSE2);
        keybindActionMap.put("[M]", Action.MUTE);
        keybindActionMap.put("[UP]", Action.VOLUME_UP5);
        keybindActionMap.put("[DOWN]", Action.VOLUME_DOWN5);
        keybindActionMap.put("[SHIFT, UP]", Action.VOLUME_UP1);
        keybindActionMap.put("[SHIFT, DOWN]", Action.VOLUME_DOWN1);
        keybindActionMap.put("[RIGHT]", Action.FORWARD5);
        keybindActionMap.put("[LEFT]", Action.REWIND5);
        keybindActionMap.put("[L]", Action.FORWARD10);
        keybindActionMap.put("[J]", Action.REWIND10);
        keybindActionMap.put("[PERIOD]", Action.FRAME_FORWARD);
        keybindActionMap.put("[COMMA]", Action.FRAME_BACKWARD);
        keybindActionMap.put("[DIGIT0]", Action.SEEK0);
        keybindActionMap.put("[DIGIT1]", Action.SEEK10);
        keybindActionMap.put("[DIGIT2]", Action.SEEK20);
        keybindActionMap.put("[DIGIT3]", Action.SEEK30);
        keybindActionMap.put("[DIGIT4]", Action.SEEK40);
        keybindActionMap.put("[DIGIT5]", Action.SEEK50);
        keybindActionMap.put("[DIGIT6]", Action.SEEK60);
        keybindActionMap.put("[DIGIT7]", Action.SEEK70);
        keybindActionMap.put("[DIGIT8]", Action.SEEK80);
        keybindActionMap.put("[DIGIT9]", Action.SEEK90);
        keybindActionMap.put("[SHIFT, PERIOD]", Action.PLAYBACK_SPEED_UP25);
        keybindActionMap.put("[SHIFT, COMMA]", Action.PLAYBACK_SPEED_DOWN25);
        keybindActionMap.put("[CONTROL, SHIFT, PERIOD]", Action.PLAYBACK_SPEED_UP5);
        keybindActionMap.put("[CONTROL, SHIFT, COMMA]", Action.PLAYBACK_SPEED_DOWN5);
        keybindActionMap.put("[SHIFT, N]", Action.NEXT);
        keybindActionMap.put("[SHIFT, P]", Action.PREVIOUS);
        keybindActionMap.put("[END]", Action.END);
        keybindActionMap.put("[F]", Action.FULLSCREEN);
        keybindActionMap.put("[F12]", Action.SNAPSHOT);
        keybindActionMap.put("[I]", Action.MINIPLAYER);
        keybindActionMap.put("[C]", Action.SUBTITLES);
        keybindActionMap.put("[S]", Action.PLAYBACK_SETTINGS);
        keybindActionMap.put("[Q]", Action.MENU);
        keybindActionMap.put("[CONTROL, SHIFT, C]", Action.CLEAR_QUEUE);
        keybindActionMap.put("[CONTROL, S]", Action.SHUFFLE);
        keybindActionMap.put("[CONTROL, A]", Action.AUTOPLAY);
        keybindActionMap.put("[CONTROL, L]", Action.LOOP);
        keybindActionMap.put("[CONTROL, SHIFT, Q]", Action.OPEN_QUEUE);
        keybindActionMap.put("[CONTROL, SHIFT, R]", Action.OPEN_RECENT_MEDIA);
        keybindActionMap.put("[CONTROL, SHIFT, M]", Action.OPEN_MUSIC_LIBRARY);
        keybindActionMap.put("[CONTROL, SHIFT, P]", Action.OPEN_PLAYLISTS);
        keybindActionMap.put("[CONTROL, SHIFT, S]", Action.OPEN_SETTINGS);


        actionKeybindMap.put(Action.PLAY_PAUSE1, new KeyCode[]{KeyCode.SPACE});
        actionKeybindMap.put(Action.PLAY_PAUSE2, new KeyCode[]{KeyCode.K});
        actionKeybindMap.put(Action.MUTE, new KeyCode[]{KeyCode.M});
        actionKeybindMap.put(Action.VOLUME_UP5, new KeyCode[]{KeyCode.UP});
        actionKeybindMap.put(Action.VOLUME_DOWN5, new KeyCode[]{KeyCode.DOWN});
        actionKeybindMap.put(Action.VOLUME_UP1, new KeyCode[]{KeyCode.SHIFT, KeyCode.UP});
        actionKeybindMap.put(Action.VOLUME_DOWN1, new KeyCode[]{KeyCode.SHIFT, KeyCode.DOWN});
        actionKeybindMap.put(Action.FORWARD5, new KeyCode[]{KeyCode.RIGHT});
        actionKeybindMap.put(Action.REWIND5, new KeyCode[]{KeyCode.LEFT});
        actionKeybindMap.put(Action.FORWARD10, new KeyCode[]{KeyCode.L});
        actionKeybindMap.put(Action.REWIND10, new KeyCode[]{KeyCode.J});
        actionKeybindMap.put(Action.FRAME_FORWARD, new KeyCode[]{KeyCode.PERIOD});
        actionKeybindMap.put(Action.FRAME_BACKWARD, new KeyCode[]{KeyCode.COMMA});
        actionKeybindMap.put(Action.SEEK0, new KeyCode[]{KeyCode.DIGIT0});
        actionKeybindMap.put(Action.SEEK10, new KeyCode[]{KeyCode.DIGIT1});
        actionKeybindMap.put(Action.SEEK20, new KeyCode[]{KeyCode.DIGIT2});
        actionKeybindMap.put(Action.SEEK30, new KeyCode[]{KeyCode.DIGIT3});
        actionKeybindMap.put(Action.SEEK40, new KeyCode[]{KeyCode.DIGIT4});
        actionKeybindMap.put(Action.SEEK50, new KeyCode[]{KeyCode.DIGIT5});
        actionKeybindMap.put(Action.SEEK60, new KeyCode[]{KeyCode.DIGIT6});
        actionKeybindMap.put(Action.SEEK70, new KeyCode[]{KeyCode.DIGIT7});
        actionKeybindMap.put(Action.SEEK80, new KeyCode[]{KeyCode.DIGIT8});
        actionKeybindMap.put(Action.SEEK90, new KeyCode[]{KeyCode.DIGIT9});
        actionKeybindMap.put(Action.PLAYBACK_SPEED_UP25, new KeyCode[]{KeyCode.SHIFT, KeyCode.PERIOD});
        actionKeybindMap.put(Action.PLAYBACK_SPEED_DOWN25, new KeyCode[]{KeyCode.SHIFT, KeyCode.COMMA});
        actionKeybindMap.put(Action.PLAYBACK_SPEED_UP5, new KeyCode[]{KeyCode.CONTROL, KeyCode.SHIFT, KeyCode.PERIOD});
        actionKeybindMap.put(Action.PLAYBACK_SPEED_DOWN5, new KeyCode[]{KeyCode.CONTROL, KeyCode.SHIFT, KeyCode.COMMA});
        actionKeybindMap.put(Action.NEXT, new KeyCode[]{KeyCode.SHIFT, KeyCode.N});
        actionKeybindMap.put(Action.PREVIOUS, new KeyCode[]{KeyCode.SHIFT, KeyCode.P});
        actionKeybindMap.put(Action.END, new KeyCode[]{KeyCode.END});
        actionKeybindMap.put(Action.FULLSCREEN, new KeyCode[]{KeyCode.F});
        actionKeybindMap.put(Action.SNAPSHOT, new KeyCode[]{KeyCode.F12});
        actionKeybindMap.put(Action.MINIPLAYER, new KeyCode[]{KeyCode.I});
        actionKeybindMap.put(Action.SUBTITLES, new KeyCode[]{KeyCode.C});
        actionKeybindMap.put(Action.PLAYBACK_SETTINGS, new KeyCode[]{KeyCode.S});
        actionKeybindMap.put(Action.MENU, new KeyCode[]{KeyCode.Q});
        actionKeybindMap.put(Action.CLEAR_QUEUE, new KeyCode[]{KeyCode.CONTROL, KeyCode.SHIFT, KeyCode.C});
        actionKeybindMap.put(Action.SHUFFLE, new KeyCode[]{KeyCode.CONTROL, KeyCode.S});
        actionKeybindMap.put(Action.AUTOPLAY, new KeyCode[]{KeyCode.CONTROL, KeyCode.A});
        actionKeybindMap.put(Action.LOOP, new KeyCode[]{KeyCode.CONTROL, KeyCode.L});
        actionKeybindMap.put(Action.OPEN_QUEUE, new KeyCode[]{KeyCode.CONTROL, KeyCode.SHIFT, KeyCode.Q});
        actionKeybindMap.put(Action.OPEN_RECENT_MEDIA, new KeyCode[]{KeyCode.CONTROL, KeyCode.SHIFT, KeyCode.R});
        actionKeybindMap.put(Action.OPEN_MUSIC_LIBRARY, new KeyCode[]{KeyCode.CONTROL, KeyCode.SHIFT, KeyCode.M});
        actionKeybindMap.put(Action.OPEN_PLAYLISTS, new KeyCode[]{KeyCode.CONTROL, KeyCode.SHIFT, KeyCode.P});
        actionKeybindMap.put(Action.OPEN_SETTINGS, new KeyCode[]{KeyCode.CONTROL, KeyCode.SHIFT, KeyCode.S});
    }


    public void handleKeyPress(KeyEvent event){

        if(event.getCode() == KeyCode.ESCAPE){
            mainController.pressESCAPE();
            return;
        }
        else if(event.getCode() == KeyCode.F11){
            mainController.FULLSCREENAction();
            return;
        }

        if(keybindChangeActive) {
            mainController.hotkeyChangeWindow.updateHotkey(eventToKeyCodeArray(event));
            event.consume();
            return;
        }

        // universal keybinds that the user cant change
        switch (event.getCode()){
            case TAB -> {
                mainController.pressTAB(event);
                return;
            }
            case FAST_FWD -> {
                mainController.FORWARD10Action();
                return;
            }
            case REWIND -> {
                mainController.REWIND10Action();
                return;
            }
            case MUTE -> {
                mainController.MUTEAction();
                return;
            }
            case PLAY, PAUSE -> {
                mainController.PLAY_PAUSEAction();
                return;
            }
            case TRACK_PREV ->  {
                mainController.PREVIOUSAction();
                return;
            }
            case TRACK_NEXT -> {
                mainController.NEXTAction();
                return;
            }
            case ENTER -> {
                mainController.pressEnter();
                return;
            }
        }



        if(        !(event.getTarget() instanceof ExpandableTextArea)
                && !(event.getTarget() instanceof TextField)
                && !(event.getTarget() instanceof DatePicker)
                && !(event.getTarget() instanceof Spinner)){

            if(event.getCode() == KeyCode.SPACE && event.getTarget() instanceof CheckComboBox<?> && mainController.subtitlesController.subtitlesState == SubtitlesState.OPENSUBTITLES_OPEN){
                mainController.subtitlesController.openSubtitlesPane.languageBox.show();
                event.consume();
                return;
            }

            if(event.getCode() == KeyCode.RIGHT && mainController.playbackSettingsController.playbackSettingsState == PlaybackSettingsState.CUSTOM_SPEED_OPEN){
                // if custom speed pane is open, dont seek video with arrows
                mainController.playbackSettingsController.playbackSpeedController.customSpeedPane.customSpeedSlider.setValueChanging(true);
                mainController.playbackSettingsController.playbackSpeedController.customSpeedPane.customSpeedSlider.setValue(mainController.playbackSettingsController.playbackSpeedController.customSpeedPane.customSpeedSlider.getValue() + 0.05);
                event.consume();
                return;
            }


            if(event.getCode() == KeyCode.LEFT && mainController.playbackSettingsController.playbackSettingsState == PlaybackSettingsState.CUSTOM_SPEED_OPEN){
                // if custom speed pane is open, dont seek video with arrows
                mainController.playbackSettingsController.playbackSpeedController.customSpeedPane.customSpeedSlider.setValueChanging(true);
                mainController.playbackSettingsController.playbackSpeedController.customSpeedPane.customSpeedSlider.setValue(mainController.playbackSettingsController.playbackSpeedController.customSpeedPane.customSpeedSlider.getValue() - 0.05);
                event.consume();
                return;
            }


            if(event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.RIGHT){
                if(mainController.getControlBarController().volumeSlider.isFocused()) return;
            }

            KeyCode[] keyCodes = eventToKeyCodeArray(event);
            String keyCodesString = Arrays.toString(keyCodes);


            if(keybindActionMap.containsKey(keyCodesString)){

                event.consume();

                Action action = keybindActionMap.get(keyCodesString);

                switch(action){
                    case PLAY_PAUSE1, PLAY_PAUSE2 -> mainController.PLAY_PAUSEAction();
                    case MUTE -> mainController.MUTEAction();
                    case VOLUME_UP5 -> mainController.VOLUME_UP5Action();
                    case VOLUME_DOWN5 -> mainController.VOLUME_DOWN5Action();
                    case VOLUME_UP1 -> mainController.VOLUME_UP1Action();
                    case VOLUME_DOWN1 -> mainController.VOLUME_DOWN1Action();
                    case FORWARD5 -> mainController.FORWARD5Action();
                    case REWIND5 -> mainController.REWIND5Action();
                    case FORWARD10 -> mainController.FORWARD10Action();
                    case REWIND10 -> mainController.REWIND10Action();
                    case FRAME_FORWARD -> mainController.FRAME_FORWARDAction();
                    case FRAME_BACKWARD -> mainController.FRAME_BACKWARDAction();
                    case SEEK0 -> mainController.SEEK0Action();
                    case SEEK10 -> mainController.SEEK10Action();
                    case SEEK20 -> mainController.SEEK20Action();
                    case SEEK30 -> mainController.SEEK30Action();
                    case SEEK40 -> mainController.SEEK40Action();
                    case SEEK50 -> mainController.SEEK50Action();
                    case SEEK60 -> mainController.SEEK60Action();
                    case SEEK70 -> mainController.SEEK70Action();
                    case SEEK80 -> mainController.SEEK80Action();
                    case SEEK90 -> mainController.SEEK90Action();
                    case PLAYBACK_SPEED_UP25 -> mainController.PLAYBACK_SPEED_UP25Action();
                    case PLAYBACK_SPEED_DOWN25 -> mainController.PLAYBACK_SPEED_DOWN25Action();
                    case PLAYBACK_SPEED_UP5 -> mainController.PLAYBACK_SPEED_UP5Action();
                    case PLAYBACK_SPEED_DOWN5 -> mainController.PLAYBACK_SPEED_DOWN5Action();
                    case NEXT -> mainController.NEXTAction();
                    case PREVIOUS -> mainController.PREVIOUSAction();
                    case END -> mainController.ENDAction();
                    case FULLSCREEN -> mainController.FULLSCREENAction();
                    case SNAPSHOT -> mainController.SNAPSHOTAction();
                    case MINIPLAYER -> mainController.MINIPLAYERAction();
                    case SUBTITLES -> mainController.SUBTITLESAction();
                    case PLAYBACK_SETTINGS -> mainController.PLAYBACK_SETTINGSAction();
                    case MENU -> mainController.MENUAction();
                    case CLEAR_QUEUE -> mainController.CLEAR_QUEUEAction();
                    case SHUFFLE -> mainController.SHUFFLEAction();
                    case AUTOPLAY -> mainController.AUTOPLAYAction();
                    case LOOP -> mainController.LOOPAction();
                    case OPEN_QUEUE -> mainController.OPEN_QUEUEAction();
                    case OPEN_RECENT_MEDIA -> mainController.OPEN_RECENT_MEDIAAction();
                    case OPEN_MUSIC_LIBRARY -> mainController.OPEN_MUSIC_LIBRARYAction();
                    case OPEN_PLAYLISTS -> mainController.OPEN_PLAYLISTSAction();
                    case OPEN_SETTINGS -> mainController.OPEN_SETTINGSAction();
                }
            }
        }
    }


    public void handleKeyRelease(KeyEvent event){

        if(keybindChangeActive) {
            KeyCode[] keyCodes = eventToKeyCodeArray(event);
            if(mainController.hotkeyChangeWindow.hotkey != null && mainController.hotkeyChangeWindow.hotkey.length > 0 && mainController.hotkeyChangeWindow.hotkey[mainController.hotkeyChangeWindow.hotkey.length - 1] == keyCodes[keyCodes.length -1])
                    mainController.hotkeyChangeWindow.checkHotkey();
            return;
        }

        if(mediaKeys.contains(event.getCode())) mainController.seekingWithKeys = false;
    }


    private KeyCode[] eventToKeyCodeArray(KeyEvent event){
        List<KeyCode> keyCodeList = new ArrayList<>();

        if(event.isControlDown()) keyCodeList.add(KeyCode.CONTROL);
        if(event.isShiftDown()) keyCodeList.add(KeyCode.SHIFT);
        if(event.isAltDown()) keyCodeList.add(KeyCode.ALT);

        if(!keyCodeList.contains(event.getCode())) keyCodeList.add(event.getCode());

        return keyCodeList.toArray(new KeyCode[0]);
    }

    public boolean isKeybindChangeActive(){
        return keybindChangeActive;
    }

    public void setKeybindChangeActive(boolean value){
        keybindChangeActive = value;
    }

    // check if the active keybinds are the same as the default keybinds
    public boolean isDefault() {
        if (actionKeybindMap.size() != ControlsSection.defaultControls.size()) {
            return false;
        }

        return actionKeybindMap.entrySet().stream()
                .allMatch(e -> Arrays.equals(e.getValue(), ControlsSection.defaultControls.get(e.getKey())));
    }
}
