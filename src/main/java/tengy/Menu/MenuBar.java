package tengy.Menu;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Node;
import tengy.Menu.Settings.Action;
import tengy.Subtitles.SubtitlesState;
import tengy.SVG;
import tengy.PlaybackSettings.PlaybackSettingsState;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

import static tengy.Utilities.keyboardFocusOn;

public class MenuBar {

    MenuController menuController;

    StackPane sideBar;

    VBox topBar = new VBox();

    public MenuBarButton queueButton;
    public MenuBarButton historyButton;
    public MenuBarButton musicLibraryButton;
    public MenuBarButton playlistsButton;
    public MenuBarButton settingsButton;

    MenuBarButton activeButton = null;

    public IntegerProperty focus = new SimpleIntegerProperty(-1);
    public List<Node> focusNodes = new ArrayList<>();


    MenuBar(MenuController menuController, StackPane sideBar){
        this.menuController = menuController;
        this.sideBar = sideBar;

        queueButton = new MenuBarButton(menuController, this, SVG.QUEUE.getContent(), 19, 14, "Play queue");
        historyButton = new MenuBarButton(menuController, this, SVG.HISTORY.getContent(), 19, 16, "Recent media");
        musicLibraryButton = new MenuBarButton(menuController, this, SVG.MUSIC.getContent(), 19, 18, "Music library");
        playlistsButton = new MenuBarButton(menuController, this, SVG.PLAYLIST.getContent(), 19, 21, "Playlists");
        settingsButton = new MenuBarButton(menuController, this, SVG.SETTINGS.getContent(), 19, 19, "Settings");


        queueButton.button.setOnAction(e -> {
            
            queueButton.button.requestFocus();

            if(menuController.extended.get()){
                if(menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) menuController.subtitlesController.closeSubtitles();
                if(menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) menuController.playbackSettingsController.closeSettings();
            }

            if(menuController.menuState == MenuState.QUEUE_OPEN) return;

            menuController.queuePage.enter();
        });

        historyButton.button.setOnAction(e -> {

            historyButton.button.requestFocus();

            if(menuController.extended.get()){
                if(menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) menuController.subtitlesController.closeSubtitles();
                if(menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) menuController.playbackSettingsController.closeSettings();
            }

            if(menuController.menuState == MenuState.RECENT_MEDIA_OPEN) return;

            menuController.recentMediaPage.enter();
        });

        musicLibraryButton.button.setOnAction(e -> {

            musicLibraryButton.button.requestFocus();

            if(menuController.extended.get()){
                if(menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) menuController.subtitlesController.closeSubtitles();
                if(menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) menuController.playbackSettingsController.closeSettings();
            }

            if(menuController.menuState == MenuState.MUSIC_LIBRARY_OPEN) return;

            menuController.musicLibraryPage.enter();
        });

        playlistsButton.button.setOnAction(e -> {

            playlistsButton.button.requestFocus();

            if(menuController.extended.get()){
                if(menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) menuController.subtitlesController.closeSubtitles();
                if(menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) menuController.playbackSettingsController.closeSettings();
            }

            if(menuController.menuState == MenuState.PLAYLISTS_OPEN) return;

            menuController.playlistsPage.enter();
        });

        settingsButton.button.setOnAction(e -> {

            settingsButton.button.requestFocus();

            if(menuController.extended.get()){
                if(menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) menuController.subtitlesController.closeSubtitles();
                if(menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) menuController.playbackSettingsController.closeSettings();
            }

            if(menuController.menuState == MenuState.SETTINGS_OPEN) return;

            menuController.settingsPage.enter();
        });

        focusNodes.add(queueButton.button);
        focusNodes.add(historyButton.button);
        focusNodes.add(musicLibraryButton.button);
        focusNodes.add(playlistsButton.button);
        focusNodes.add(settingsButton.button);


        sideBar.getChildren().addAll(topBar, settingsButton);
        sideBar.setPadding(new Insets(40, 0, 20, 0));
        sideBar.setOnMouseClicked(e -> sideBar.requestFocus());

        StackPane.setAlignment(topBar, Pos.TOP_LEFT);
        topBar.getChildren().addAll(queueButton, historyButton, musicLibraryButton, playlistsButton);
        topBar.setSpacing(10);

        StackPane.setAlignment(settingsButton, Pos.BOTTOM_LEFT);
    }

    public void extend(){
        StackPane.setMargin(menuController.menuContent, new Insets(0, 0, 0, 300));
        sideBar.setPrefWidth(300);

        queueButton.extend();
        historyButton.extend();
        musicLibraryButton.extend();
        playlistsButton.extend();
        settingsButton.extend();
    }

    public void shrink(){
        StackPane.setMargin(menuController.menuContent, new Insets(0, 0, 0, 50));
        sideBar.setPrefWidth(50);

        queueButton.shrink();
        historyButton.shrink();
        musicLibraryButton.shrink();
        playlistsButton.shrink();
        settingsButton.shrink();
    }

    public void setActiveButton(MenuBarButton button){
        if(activeButton != null) activeButton.setInactive();
        activeButton = button;
        if(button != null) button.setActive();
    }

    public void loadTooltips(){
        queueButton.loadTooltip("Play queue", menuController.mainController.hotkeyController.getHotkeyString(Action.OPEN_QUEUE));
        historyButton.loadTooltip("Recent media", menuController.mainController.hotkeyController.getHotkeyString(Action.OPEN_RECENT_MEDIA));
        musicLibraryButton.loadTooltip("Music library", menuController.mainController.hotkeyController.getHotkeyString(Action.OPEN_MUSIC_LIBRARY));
        playlistsButton.loadTooltip("Playlists", menuController.mainController.hotkeyController.getHotkeyString(Action.OPEN_PLAYLISTS));
        settingsButton.loadTooltip("Settings", menuController.mainController.hotkeyController.getHotkeyString(Action.OPEN_SETTINGS));
    }

    public boolean focusForward() {

        if(focus.get() >= focusNodes.size() - 1)
            return true;


        int newFocus;

        if(focus.get() <= -1) newFocus = 0;
        else newFocus = focus.get() + 1;

        keyboardFocusOn(focusNodes.get(newFocus));

        return false;
    }

    public boolean focusBackward() {
        if(focus.get() == 0)
            return true;

        int newFocus;

        if(focus.get() > focusNodes.size() - 1 || focus.get() == -1) newFocus = focusNodes.size() - 1;
        else newFocus = focus.get() - 1;

        keyboardFocusOn(focusNodes.get(newFocus));

        return false;
    }
}
