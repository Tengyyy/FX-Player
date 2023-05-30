package tengy;

import tengy.Menu.MenuController;
import tengy.Menu.MenuState;
import tengy.PlaybackSettings.PlaybackSettingsController;
import tengy.PlaybackSettings.PlaybackSettingsState;
import tengy.Subtitles.SubtitlesController;
import tengy.Subtitles.SubtitlesState;
import tengy.Windows.WindowController;
import tengy.Windows.WindowState;

public class FocusController {

    MainController mainController;
    MenuController menuController;
    PlaybackSettingsController playbackSettingsController;
    SubtitlesController subtitlesController;
    WindowController windowController;

    FocusController(MainController mainController){
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
