package hans.Menu;

import hans.App;
import hans.SVG;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;

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

        queueButton = new MenuBarButton(App.svgMap.get(SVG.QUEUE), 19, 14, "Play queue");
        historyButton = new MenuBarButton(App.svgMap.get(SVG.HISTORY), 19, 16, "Recently played");
        musicLibraryButton = new MenuBarButton(App.svgMap.get(SVG.MUSIC), 19, 18, "Music library");
        playlistsButton = new MenuBarButton(App.svgMap.get(SVG.PLAYLIST), 19, 21, "Playlists");
        settingsButton = new MenuBarButton(App.svgMap.get(SVG.SETTINGS), 19, 19, "Settings");

        queueButton.button.setOnAction(e -> {
            historyButton.setInactive();
            musicLibraryButton.setInactive();
            playlistsButton.setInactive();
            settingsButton.setInactive();
            queueButton.setActive();
        });

        historyButton.button.setOnAction(e -> {
            historyButton.setActive();
            musicLibraryButton.setInactive();
            playlistsButton.setInactive();
            settingsButton.setInactive();
            queueButton.setInactive();
        });

        musicLibraryButton.button.setOnAction(e -> {
            historyButton.setInactive();
            musicLibraryButton.setActive();
            playlistsButton.setInactive();
            settingsButton.setInactive();
            queueButton.setInactive();
        });

        playlistsButton.button.setOnAction(e -> {
            historyButton.setInactive();
            musicLibraryButton.setInactive();
            playlistsButton.setActive();
            settingsButton.setInactive();
            queueButton.setInactive();
        });

        settingsButton.button.setOnAction(e -> {
            historyButton.setInactive();
            musicLibraryButton.setInactive();
            playlistsButton.setInactive();
            settingsButton.setActive();
            queueButton.setInactive();
        });


        sideBar.getChildren().addAll(topBar, settingsButton);
        sideBar.setPadding(new Insets(20, 0, 20, 0));

        StackPane.setAlignment(topBar, Pos.TOP_LEFT);
        topBar.getChildren().addAll(queueButton, historyButton, musicLibraryButton, playlistsButton);
        topBar.setSpacing(10);

        StackPane.setAlignment(settingsButton, Pos.BOTTOM_LEFT);
    }
}
