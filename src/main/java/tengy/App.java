package tengy;


import javafx.scene.input.KeyCombination;
import tengy.subtitles.SubtitlesController;
import tengy.menu.MenuController;
import tengy.playbackSettings.PlaybackSettingsController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;


public class App extends Application {

    public static Stage stage;

    public static boolean fullScreen;
    public static boolean isWindows = false;


    ControlBarController controlBarController;
    PlaybackSettingsController playbackSettingsController;
    MenuController menuController;
    SubtitlesController subtitlesController;
    MainController mainController;

    MediaInterface mediaInterface;

    HotkeyController hotkeyController;


    @Override
    public void start(Stage primaryStage) {

        SplashScreen splashScreen = new SplashScreen();

        primaryStage.setMinHeight(450);
        primaryStage.setMinWidth(735);

        App.stage = primaryStage;

        primaryStage.setScene(splashScreen.scene);
        primaryStage.setTitle("FXPlayer");
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("images/appIcon.png")).toExternalForm()));
        primaryStage.show();


        Platform.runLater(() -> {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("views/Main.fxml"));

            try {
                Parent root = loader.load();

                mainController = loader.getController();

                controlBarController = mainController.getControlBarController();
                playbackSettingsController = mainController.getPlaybackSettingsController();
                menuController = mainController.getMenuController();
                subtitlesController = mainController.getSubtitlesController();
                mediaInterface = mainController.getMediaInterface();
                hotkeyController = mainController.getHotkeyController();

                initializeConfig();

                Scene scene = new Scene(root, 705, 450);

                scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles/application.css")).toExternalForm());


                scene.addEventFilter(MouseEvent.ANY, event -> {
                    if (controlBarController.mouseEventTracker != null && event.getEventType() != MouseEvent.MOUSE_ENTERED_TARGET && event.getEventType() != MouseEvent.MOUSE_EXITED_TARGET){
                        controlBarController.mouseEventTracker.move();
                    }
                });

                primaryStage.addEventFilter(KeyEvent.KEY_PRESSED, event -> hotkeyController.handleKeyPress(event));

                primaryStage.addEventFilter(KeyEvent.KEY_RELEASED, event -> hotkeyController.handleKeyRelease(event));

                if(System.getProperty("os.name").toLowerCase().contains("windows")){
                    isWindows = true;
                    // program is being run on a windows operating system, so we can take advantage of windows features
                    mainController.addTaskBarButtons();
                }

                primaryStage.setOnCloseRequest(event -> {
                    if(!mainController.ongoingMediaEditProcesses.isEmpty()){
                        if(!mainController.windowController.closeConfirmationWindow.showing) mainController.windowController.closeConfirmationWindow.show();
                        event.consume();
                    }
                    else mainController.closeApp();
                });

                primaryStage.setScene(scene);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }


    private void initializeConfig(){

        menuController.settingsPage.controlsSection.initializeControlsBox();
        mainController.windowController.openSubtitlesWindow.connectionPage.readCredentials();

        menuController.settingsPage.loadPreferences();

        controlBarController.loadTooltips();
        mainController.loadTooltips();
        menuController.menuBar.loadTooltips();
    }
}