package hans;

import hans.Captions.CaptionsState;
import hans.Menu.ExpandableTextArea;
import hans.Menu.Settings.Action;
import hans.Settings.SettingsState;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.controlsfx.control.CheckComboBox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.util.Map;

public class HotkeyController {

    public static final Map<String, Action> keybindActionMap = Map.ofEntries(
            Map.entry("[SPACE]", Action.PLAY_PAUSE1),
            Map.entry("[K]", Action.PLAY_PAUSE2),
            Map.entry("[M]", Action.MUTE),
            Map.entry("[UP]", Action.VOLUME_UP5),
            Map.entry("[DOWN]", Action.VOLUME_DOWN5),
            Map.entry("[SHIFT, UP]", Action.VOLUME_UP1),
            Map.entry("[SHIFT, DOWN]", Action.VOLUME_DOWN1),
            Map.entry("[RIGHT]", Action.FORWARD5),
            Map.entry("[LEFT]", Action.REWIND5),
            Map.entry("[L]", Action.FORWARD10),
            Map.entry("[J]", Action.REWIND10),
            Map.entry("[PERIOD]", Action.FRAME_FORWARD),
            Map.entry("[COMMA]", Action.FRAME_BACKWARD),
            Map.entry("[DIGIT0]", Action.SEEK0),
            Map.entry("[DIGIT1]", Action.SEEK10),
            Map.entry("[DIGIT2]", Action.SEEK20),
            Map.entry("[DIGIT3]", Action.SEEK30),
            Map.entry("[DIGIT4]", Action.SEEK40),
            Map.entry("[DIGIT5]", Action.SEEK50),
            Map.entry("[DIGIT6]", Action.SEEK60),
            Map.entry("[DIGIT7]", Action.SEEK70),
            Map.entry("[DIGIT8]", Action.SEEK80),
            Map.entry("[DIGIT9]", Action.SEEK90),
            Map.entry("[SHIFT, PERIOD]", Action.PLAYBACK_SPEED_UP25),
            Map.entry("[SHIFT, COMMA]", Action.PLAYBACK_SPEED_DOWN25),
            Map.entry("[CONTROL, SHIFT, PERIOD]", Action.PLAYBACK_SPEED_UP5),
            Map.entry("[CONTROL, SHIFT, COMMA]", Action.PLAYBACK_SPEED_DOWN5),
            Map.entry("[SHIFT, N]", Action.NEXT),
            Map.entry("[SHIFT, P]", Action.PREVIOUS),
            Map.entry("[END]", Action.END),
            Map.entry("[F]", Action.FULLSCREEN),
            Map.entry("[F12]", Action.SNAPSHOT),
            Map.entry("[I]", Action.MINIPLAYER),
            Map.entry("[C]", Action.SUBTITLES),
            Map.entry("[S]", Action.PLAYBACK_SETTINGS),
            Map.entry("[Q]", Action.MENU),
            Map.entry("[CONTROL, SHIFT, C]", Action.CLEAR_QUEUE),
            Map.entry("[CONTROL, S]", Action.SHUFFLE),
            Map.entry("[CONTROL, A]", Action.AUTOPLAY),
            Map.entry("[CONTROL, L]", Action.LOOP),
            Map.entry("[CONTROL, SHIFT, Q]", Action.OPEN_QUEUE),
            Map.entry("[CONTROL, SHIFT, R]", Action.OPEN_RECENT_MEDIA),
            Map.entry("[CONTROL, SHIFT, M]", Action.OPEN_MUSIC_LIBRARY),
            Map.entry("[CONTROL, SHIFT, P]", Action.OPEN_PLAYLISTS),
            Map.entry("[CONTROL, SHIFT, S]", Action.OPEN_SETTINGS)
    );

    public static final Map<Action, KeyCode[]> actionKeybindMap = Map.ofEntries(
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

    private static final List<KeyCode> mediaKeys = List.of(KeyCode.LEFT, KeyCode.RIGHT, KeyCode.J, KeyCode.L, KeyCode.REWIND, KeyCode.FAST_FWD, KeyCode.DIGIT0, KeyCode.DIGIT1, KeyCode.DIGIT2, KeyCode.DIGIT3, KeyCode.DIGIT4, KeyCode.DIGIT5, KeyCode.DIGIT6, KeyCode.DIGIT7, KeyCode.DIGIT8, KeyCode.DIGIT9, KeyCode.HOME, KeyCode.END);

    public static final List<KeyCode> invalidKeys = List.of(KeyCode.CONTROL, KeyCode.ALT, KeyCode.SHIFT, KeyCode.TAB, KeyCode.FAST_FWD, KeyCode.REWIND, KeyCode.MUTE, KeyCode.PLAY, KeyCode.PAUSE, KeyCode.TRACK_PREV, KeyCode.TRACK_NEXT, KeyCode.F11, KeyCode.ESCAPE, KeyCode.ENTER);

    MainController mainController;

    private boolean keybindChangeActive = false;

    HotkeyController(MainController mainController){
        this.mainController = mainController;
    }


    public void handleKeyPress(KeyEvent event){

        if(keybindChangeActive) {
            mainController.hotkeyChangeWindow.updateHotkey(eventToKeyCodeArray(event));
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
            case F11 -> {
                mainController.FULLSCREENAction();
                return;
            }
            case ESCAPE -> {
                mainController.pressESCAPE();
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

            if(event.getCode() == KeyCode.SPACE && event.getTarget() instanceof CheckComboBox<?> && mainController.captionsController.captionsState == CaptionsState.OPENSUBTITLES_OPEN){
                mainController.captionsController.openSubtitlesPane.languageBox.show();
                event.consume();
                return;
            }

            if(event.getCode() == KeyCode.RIGHT && mainController.settingsController.settingsState == SettingsState.CUSTOM_SPEED_OPEN){
                // if custom speed pane is open, dont seek video with arrows
                mainController.settingsController.playbackSpeedController.customSpeedPane.customSpeedSlider.setValueChanging(true);
                mainController.settingsController.playbackSpeedController.customSpeedPane.customSpeedSlider.setValue(mainController.settingsController.playbackSpeedController.customSpeedPane.customSpeedSlider.getValue() + 0.05);
                event.consume();
                return;
            }


            if(event.getCode() == KeyCode.LEFT && mainController.settingsController.settingsState == SettingsState.CUSTOM_SPEED_OPEN){
                // if custom speed pane is open, dont seek video with arrows
                mainController.settingsController.playbackSpeedController.customSpeedPane.customSpeedSlider.setValueChanging(true);
                mainController.settingsController.playbackSpeedController.customSpeedPane.customSpeedSlider.setValue(mainController.settingsController.playbackSpeedController.customSpeedPane.customSpeedSlider.getValue() - 0.05);
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
}
