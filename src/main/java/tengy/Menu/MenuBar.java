package tengy.Menu;

import tengy.Menu.Settings.Action;
import tengy.Subtitles.SubtitlesState;
import tengy.SVG;
import tengy.PlaybackSettings.PlaybackSettingsState;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

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


    MenuBar(MenuController menuController, StackPane sideBar){
        this.menuController = menuController;
        this.sideBar = sideBar;

        queueButton = new MenuBarButton(menuController, SVG.QUEUE.getContent(), 19, 14, "Play queue");
        historyButton = new MenuBarButton(menuController, SVG.HISTORY.getContent(), 19, 16, "Recent media");
        musicLibraryButton = new MenuBarButton(menuController, SVG.MUSIC.getContent(), 19, 18, "Music library");
        playlistsButton = new MenuBarButton(menuController, SVG.PLAYLIST.getContent(), 19, 21, "Playlists");
        settingsButton = new MenuBarButton(menuController, SVG.SETTINGS.getContent(), 19, 19, "Settings");


        queueButton.button.setOnAction(e -> {

            if(menuController.extended.get()){
                if(menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) menuController.subtitlesController.closeSubtitles();
                if(menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) menuController.playbackSettingsController.closeSettings();
            }

            if(menuController.menuState == MenuState.QUEUE_OPEN) return;

            menuController.queuePage.enter();
        });

        historyButton.button.setOnAction(e -> {

            if(menuController.extended.get()){
                if(menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) menuController.subtitlesController.closeSubtitles();
                if(menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) menuController.playbackSettingsController.closeSettings();
            }

            if(menuController.menuState == MenuState.RECENT_MEDIA_OPEN) return;

            menuController.recentMediaPage.enter();
        });

        musicLibraryButton.button.setOnAction(e -> {

            if(menuController.extended.get()){
                if(menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) menuController.subtitlesController.closeSubtitles();
                if(menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) menuController.playbackSettingsController.closeSettings();
            }

            if(menuController.menuState == MenuState.MUSIC_LIBRARY_OPEN) return;

            menuController.musicLibraryPage.enter();
        });

        playlistsButton.button.setOnAction(e -> {

            if(menuController.extended.get()){
                if(menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) menuController.subtitlesController.closeSubtitles();
                if(menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) menuController.playbackSettingsController.closeSettings();
            }

            if(menuController.menuState == MenuState.PLAYLISTS_OPEN) return;

            menuController.playlistsPage.enter();
        });

        settingsButton.button.setOnAction(e -> {

            if(menuController.extended.get()){
                if(menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) menuController.subtitlesController.closeSubtitles();
                if(menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) menuController.playbackSettingsController.closeSettings();
            }

            if(menuController.menuState == MenuState.SETTINGS_OPEN) return;

            menuController.settingsPage.enter();
        });


        sideBar.getChildren().addAll(topBar, settingsButton);
        sideBar.setPadding(new Insets(40, 0, 20, 0));

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
}
