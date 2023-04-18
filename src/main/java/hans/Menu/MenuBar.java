package hans.Menu;

import hans.App;
import hans.Captions.CaptionsState;
import hans.SVG;
import hans.Settings.SettingsState;
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

        queueButton = new MenuBarButton(menuController, App.svgMap.get(SVG.QUEUE), 19, 14, "Play queue", "Play queue");
        historyButton = new MenuBarButton(menuController, App.svgMap.get(SVG.HISTORY), 19, 16, "Recent media", "Recent media");
        musicLibraryButton = new MenuBarButton(menuController, App.svgMap.get(SVG.MUSIC), 19, 18, "Music library", "Music library");
        playlistsButton = new MenuBarButton(menuController, App.svgMap.get(SVG.PLAYLIST), 19, 21, "Playlists", "Playlists");
        settingsButton = new MenuBarButton(menuController, App.svgMap.get(SVG.SETTINGS), 19, 19, "Settings", "Settings");


        queueButton.button.setOnAction(e -> {

            if(menuController.extended.get()){
                if(menuController.captionsController.captionsState != CaptionsState.CLOSED) menuController.captionsController.closeCaptions();
                if(menuController.settingsController.settingsState != SettingsState.CLOSED) menuController.settingsController.closeSettings();
            }

            if(menuController.menuState == MenuState.QUEUE_OPEN) return;

            menuController.queuePage.enter();
        });

        historyButton.button.setOnAction(e -> {

            if(menuController.extended.get()){
                if(menuController.captionsController.captionsState != CaptionsState.CLOSED) menuController.captionsController.closeCaptions();
                if(menuController.settingsController.settingsState != SettingsState.CLOSED) menuController.settingsController.closeSettings();
            }

            if(menuController.menuState == MenuState.RECENT_MEDIA_OPEN) return;

            menuController.recentMediaPage.enter();
        });

        musicLibraryButton.button.setOnAction(e -> {

            if(menuController.extended.get()){
                if(menuController.captionsController.captionsState != CaptionsState.CLOSED) menuController.captionsController.closeCaptions();
                if(menuController.settingsController.settingsState != SettingsState.CLOSED) menuController.settingsController.closeSettings();
            }

            if(menuController.menuState == MenuState.MUSIC_LIBRARY_OPEN) return;

            menuController.musicLibraryPage.enter();
        });

        playlistsButton.button.setOnAction(e -> {

            if(menuController.extended.get()){
                if(menuController.captionsController.captionsState != CaptionsState.CLOSED) menuController.captionsController.closeCaptions();
                if(menuController.settingsController.settingsState != SettingsState.CLOSED) menuController.settingsController.closeSettings();
            }

            if(menuController.menuState == MenuState.PLAYLISTS_OPEN) return;

            menuController.playlistsPage.enter();
        });

        settingsButton.button.setOnAction(e -> {

            if(menuController.extended.get()){
                if(menuController.captionsController.captionsState != CaptionsState.CLOSED) menuController.captionsController.closeCaptions();
                if(menuController.settingsController.settingsState != SettingsState.CLOSED) menuController.settingsController.closeSettings();
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
}