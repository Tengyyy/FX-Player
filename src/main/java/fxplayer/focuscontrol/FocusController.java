package fxplayer.focuscontrol;

import fxplayer.MainController;
import fxplayer.menu.MenuController;
import fxplayer.menu.MenuState;
import fxplayer.playbackSettings.PlaybackSettingsController;
import fxplayer.playbackSettings.PlaybackSettingsState;
import fxplayer.subtitles.SubtitlesController;
import fxplayer.subtitles.SubtitlesState;
import fxplayer.windows.WindowController;
import fxplayer.windows.WindowState;

public class FocusController {

    MainController mainController;
    MenuController menuController;
    PlaybackSettingsController playbackSettingsController;
    SubtitlesController subtitlesController;
    WindowController windowController;

    public FocusController(MainController mainController){
        this.mainController = mainController;
        this.menuController = mainController.getMenuController();
        this.playbackSettingsController = mainController.getPlaybackSettingsController();
        this.subtitlesController = mainController.getSubtitlesController();
        this.windowController = mainController.windowController;
    }

    public void focusForward(){
        if(windowController.windowState != WindowState.CLOSED)
            windowController.handleFocusForward();
        else if(playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED)
            playbackSettingsController.handleFocusForward();
        else if(subtitlesController.subtitlesState != SubtitlesState.CLOSED)
            subtitlesController.handleFocusForward();
        else if(menuController.menuState != MenuState.CLOSED && !menuController.menuInTransition)
            menuController.handleFocusForward();

    }

    public void focusBackward(){
        if(windowController.windowState != WindowState.CLOSED)
            windowController.handleFocusBackward();
        else if(playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED)
            playbackSettingsController.handleFocusBackward();
        else if(subtitlesController.subtitlesState != SubtitlesState.CLOSED)
            subtitlesController.handleFocusBackward();
        else if(menuController.menuState != MenuState.CLOSED && !menuController.menuInTransition)
            menuController.handleFocusBackward();
    }
}
