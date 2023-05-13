package tengy;

import tengy.Subtitles.SubtitlesState;
import tengy.Menu.ExpandableTextArea;
import tengy.Menu.Settings.Action;
import tengy.PlaybackSettings.PlaybackSettingsState;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.controlsfx.control.CheckComboBox;

import java.util.*;

public class HotkeyController {

    Pref pref;

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

    public static final Map<KeyCode, String> symbols = Map.ofEntries(
            Map.entry(KeyCode.RIGHT, "\u2192"),
            Map.entry(KeyCode.UP, "\u2191"),
            Map.entry(KeyCode.LEFT, "\u2190"),
            Map.entry(KeyCode.DOWN, "\u2193"),
            Map.entry(KeyCode.COMMA, ","),
            Map.entry(KeyCode.PERIOD, "."),
            Map.entry(KeyCode.SLASH, "/"),
            Map.entry(KeyCode.BACK_SLASH, "\\"),
            Map.entry(KeyCode.QUOTE, "'"),
            Map.entry(KeyCode.SEMICOLON, ";"),
            Map.entry(KeyCode.COLON, ":"),
            Map.entry(KeyCode.EQUALS, "="),
            Map.entry(KeyCode.MINUS, "-"),
            Map.entry(KeyCode.PLUS, "+"),
            Map.entry(KeyCode.AMPERSAND, "&"),
            Map.entry(KeyCode.OPEN_BRACKET, "["),
            Map.entry(KeyCode.CLOSE_BRACKET, "]"),
            Map.entry(KeyCode.BACK_QUOTE, "`"),
            Map.entry(KeyCode.LEFT_PARENTHESIS, "("),
            Map.entry(KeyCode.RIGHT_PARENTHESIS, ")")
    );


    public Map<String, Action> keybindActionMap = new HashMap<>();

    public HashMap<Action, KeyCode[]> actionKeybindMap = new HashMap<>();

    private static final List<KeyCode> mediaKeys = List.of(KeyCode.LEFT, KeyCode.RIGHT, KeyCode.J, KeyCode.L, KeyCode.REWIND, KeyCode.FAST_FWD, KeyCode.DIGIT0, KeyCode.DIGIT1, KeyCode.DIGIT2, KeyCode.DIGIT3, KeyCode.DIGIT4, KeyCode.DIGIT5, KeyCode.DIGIT6, KeyCode.DIGIT7, KeyCode.DIGIT8, KeyCode.DIGIT9, KeyCode.HOME, KeyCode.END);

    public static final List<KeyCode> invalidKeys = List.of(KeyCode.CONTROL, KeyCode.ALT, KeyCode.SHIFT, KeyCode.TAB, KeyCode.FAST_FWD, KeyCode.REWIND, KeyCode.MUTE, KeyCode.PLAY, KeyCode.PAUSE, KeyCode.TRACK_PREV, KeyCode.TRACK_NEXT, KeyCode.F11, KeyCode.ESCAPE, KeyCode.ENTER);

    MainController mainController;

    private boolean keybindChangeActive = false;

    HotkeyController(MainController mainController, Pref pref){

        this.mainController = mainController;
        this.pref = pref;

        for(Action action : Action.values()){
            String keycodesString = pref.preferences.get(action.toString(), "");
            actionKeybindMap.put(action, stringToKeyCodeArray(keycodesString));

            if(keycodesString.length() > 2) keybindActionMap.put(keycodesString, action);
        }

        if(keybindActionMap.isEmpty()){
            // this is likely this first time user opens the app, will load default keybinds and save them to preferences

            actionKeybindMap.putAll(defaultControls);
            for(Map.Entry<Action, KeyCode[]> entry : defaultControls.entrySet()){
                keybindActionMap.put(Arrays.toString(entry.getValue()), entry.getKey());
                pref.preferences.put(entry.getKey().toString(), Arrays.toString(entry.getValue()));
            }
        }
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
            mainController.windowController.hotkeyChangeWindow.updateHotkey(eventToKeyCodeArray(event));
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




    public void handleMiniplayerKeyPress(KeyEvent event){

        if(keybindChangeActive) {
            event.consume();
            return;
        }

        // universal keybinds that the user cant change
        switch (event.getCode()){
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
                case MINIPLAYER -> mainController.MINIPLAYERAction();
                case SUBTITLES -> mainController.SUBTITLESAction();
                case SHUFFLE -> mainController.SHUFFLEAction();
                case AUTOPLAY -> mainController.AUTOPLAYAction();
                case LOOP -> mainController.LOOPAction();
            }
        }
    }




    public void handleKeyRelease(KeyEvent event){

        if(keybindChangeActive) {
            KeyCode[] keyCodes = eventToKeyCodeArray(event);
            if(mainController.windowController.hotkeyChangeWindow.hotkey != null && mainController.windowController.hotkeyChangeWindow.hotkey.length > 0 && mainController.windowController.hotkeyChangeWindow.hotkey[mainController.windowController.hotkeyChangeWindow.hotkey.length - 1] == keyCodes[keyCodes.length -1])
                    mainController.windowController.hotkeyChangeWindow.checkHotkey();
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


    public KeyCode[] stringToKeyCodeArray(String string){

        if(string.length() <= 2) return new KeyCode[0];
        String[] keyCodeStrings = string.substring(1, string.length() - 1).split(", ");

        return Arrays.stream(keyCodeStrings).map(KeyCode::valueOf).toArray(KeyCode[]::new);
    }

    public boolean isKeybindChangeActive(){
        return keybindChangeActive;
    }

    public void setKeybindChangeActive(boolean value){
        keybindChangeActive = value;
    }

    // check if the active keybinds are the same as the default keybinds
    public boolean isDefault() {
        if (actionKeybindMap.size() != defaultControls.size()) {
            return false;
        }

        return actionKeybindMap.entrySet().stream()
                .allMatch(e -> Arrays.equals(e.getValue(), defaultControls.get(e.getKey())));
    }




    public String getHotkeyString(Action action){
        if(!actionKeybindMap.containsKey(action)) return "";
        else {
            KeyCode[] keyCodes = actionKeybindMap.get(action);
            if(keyCodes.length == 0) return "";

            StringBuilder hotkeyStringBuilder = new StringBuilder(" (");

            for(KeyCode keyCode : keyCodes){
                if(symbols.containsKey(keyCode)) hotkeyStringBuilder.append(symbols.get(keyCode));
                else hotkeyStringBuilder.append(keyCode.getName());

                hotkeyStringBuilder.append(" + ");
            }

            hotkeyStringBuilder.delete(hotkeyStringBuilder.length() - 3, hotkeyStringBuilder.length());

            hotkeyStringBuilder.append(")");

            return hotkeyStringBuilder.toString();
        }
    }

}
