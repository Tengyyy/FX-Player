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

import java.util.ArrayList;
import java.util.List;

public class FocusController implements FocusTraversalComponent {

    MainController mainController;
    MenuController menuController;
    PlaybackSettingsController playbackSettingsController;
    SubtitlesController subtitlesController;
    WindowController windowController;

    List<FocusSubController> focusSubControllers = new ArrayList<>();
    int focus = -1;
    List<Integer> focusTarget = null;

    public FocusController(MainController mainController) {
        this.mainController = mainController;
        this.menuController = mainController.getMenuController();
        this.playbackSettingsController = mainController.getPlaybackSettingsController();
        this.subtitlesController = mainController.getSubtitlesController();
        this.windowController = mainController.windowController;
    }

    public void focusForward() {
        if (windowController.windowState != WindowState.CLOSED)
            windowController.handleFocusForward();
        else if (playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED)
            playbackSettingsController.handleFocusForward();
        else if (subtitlesController.subtitlesState != SubtitlesState.CLOSED)
            subtitlesController.handleFocusForward();
        else if (menuController.menuState != MenuState.CLOSED && !menuController.menuInTransition)
            menuController.handleFocusForward();

    }

    public void focusBackward() {
        if (windowController.windowState != WindowState.CLOSED)
            windowController.handleFocusBackward();
        else if (playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED)
            playbackSettingsController.handleFocusBackward();
        else if (subtitlesController.subtitlesState != SubtitlesState.CLOSED)
            subtitlesController.handleFocusBackward();
        else if (menuController.menuState != MenuState.CLOSED && !menuController.menuInTransition)
            menuController.handleFocusBackward();
    }

    @Override
    public void resetFocus() {
        this.focus = -1;
    }

    public void traverseFocus() {
        if (focusTarget == null || focusTarget.isEmpty())
            return;

        this.focus = focusTarget.get(0);
        FocusSubController running = focusSubControllers.get(this.focus);
        for (int i = 1; i < focusTarget.size() - 1; i++) {
            running.setFocus(focusTarget.get(i));
            running = (FocusSubController) running.focusTraversalComponents.get(focusTarget.get(i));
        }

        running.setFocus(focusTarget.get(focusTarget.size() - 1));


        this.focusTarget = null;
    }
}
