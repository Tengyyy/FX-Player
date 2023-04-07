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

    MenuBarButton queueButton;
    MenuBarButton historyButton;
    MenuBarButton musicLibraryButton;
    MenuBarButton playlistsButton;
    MenuBarButton settingsButton;


    MenuBar(MenuController menuController, StackPane sideBar){
        this.menuController = menuController;
        this.sideBar = sideBar;

        queueButton = new MenuBarButton(menuController, App.svgMap.get(SVG.QUEUE), 19, 14, "Play queue", "Play queue");
        historyButton = new MenuBarButton(menuController, App.svgMap.get(SVG.HISTORY), 19, 16, "Recent media", "Recent media");
        musicLibraryButton = new MenuBarButton(menuController, App.svgMap.get(SVG.MUSIC), 19, 18, "Music library", "Music library");
        playlistsButton = new MenuBarButton(menuController, App.svgMap.get(SVG.PLAYLIST), 19, 21, "Playlists", "Playlists");
        settingsButton = new MenuBarButton(menuController, App.svgMap.get(SVG.SETTINGS), 19, 19, "Settings", "Settings");

        queueButton.button.setOnAction(e -> {
            if(menuController.extended){
                if(menuController.captionsController.captionsState != CaptionsState.CLOSED) menuController.captionsController.closeCaptions();
                if(menuController.settingsController.settingsState != SettingsState.CLOSED) menuController.settingsController.closeSettings();
            }

            historyButton.setInactive();
            musicLibraryButton.setInactive();
            playlistsButton.setInactive();
            settingsButton.setInactive();
            queueButton.setActive();
        });

        queueButton.setActive();

        historyButton.button.setOnAction(e -> {
            if(menuController.extended){
                if(menuController.captionsController.captionsState != CaptionsState.CLOSED) menuController.captionsController.closeCaptions();
                if(menuController.settingsController.settingsState != SettingsState.CLOSED) menuController.settingsController.closeSettings();
            }

            historyButton.setActive();
            musicLibraryButton.setInactive();
            playlistsButton.setInactive();
            settingsButton.setInactive();
            queueButton.setInactive();
        });

        musicLibraryButton.button.setOnAction(e -> {
            if(menuController.extended){
                if(menuController.captionsController.captionsState != CaptionsState.CLOSED) menuController.captionsController.closeCaptions();
                if(menuController.settingsController.settingsState != SettingsState.CLOSED) menuController.settingsController.closeSettings();
            }

            historyButton.setInactive();
            musicLibraryButton.setActive();
            playlistsButton.setInactive();
            settingsButton.setInactive();
            queueButton.setInactive();
        });

        playlistsButton.button.setOnAction(e -> {
            if(menuController.extended){
                if(menuController.captionsController.captionsState != CaptionsState.CLOSED) menuController.captionsController.closeCaptions();
                if(menuController.settingsController.settingsState != SettingsState.CLOSED) menuController.settingsController.closeSettings();
            }

            historyButton.setInactive();
            musicLibraryButton.setInactive();
            playlistsButton.setActive();
            settingsButton.setInactive();
            queueButton.setInactive();
        });

        settingsButton.button.setOnAction(e -> {
            if(menuController.extended){
                if(menuController.captionsController.captionsState != CaptionsState.CLOSED) menuController.captionsController.closeCaptions();
                if(menuController.settingsController.settingsState != SettingsState.CLOSED) menuController.settingsController.closeSettings();
            }

            historyButton.setInactive();
            musicLibraryButton.setInactive();
            playlistsButton.setInactive();
            settingsButton.setActive();
            queueButton.setInactive();
        });


        sideBar.getChildren().addAll(topBar, settingsButton);
        sideBar.setPadding(new Insets(40, 0, 20, 0));

        StackPane.setAlignment(topBar, Pos.TOP_LEFT);
        topBar.getChildren().addAll(queueButton, historyButton, musicLibraryButton, playlistsButton);
        topBar.setSpacing(10);

        StackPane.setAlignment(settingsButton, Pos.BOTTOM_LEFT);
    }

    public void extend(){
        sideBar.setPrefWidth(300);

        queueButton.extend();
        historyButton.extend();
        musicLibraryButton.extend();
        playlistsButton.extend();
        settingsButton.extend();
    }

    public void shrink(){
        sideBar.setPrefWidth(50);

        queueButton.shrink();
        historyButton.shrink();
        musicLibraryButton.shrink();
        playlistsButton.shrink();
        settingsButton.shrink();
    }
}
