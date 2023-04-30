package hans;


import hans.Subtitles.SubtitlesController;
import hans.Menu.MenuController;
import hans.PlaybackSettings.PlaybackSettingsController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Objects;

import static hans.SVG.*;



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
    public void start(Stage primaryStage) throws IOException {


        FXMLLoader loader = new FXMLLoader(getClass().getResource("views/Main.fxml"));

        Parent root = loader.load();

        mainController = loader.getController();

        controlBarController = mainController.getControlBarController();
        playbackSettingsController = mainController.getSettingsController();
        menuController = mainController.getMenuController();
        subtitlesController = mainController.getSubtitlesController();
        mediaInterface = mainController.getMediaInterface();
        hotkeyController = mainController.getHotkeyController();

        initializeConfig();


        Scene scene = new Scene(root, 705, 400);

        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles/application.css")).toExternalForm());


        scene.addEventFilter(MouseEvent.ANY, event -> {
            if (controlBarController.mouseEventTracker != null && event.getEventType() != MouseEvent.MOUSE_ENTERED_TARGET && event.getEventType() != MouseEvent.MOUSE_EXITED_TARGET){
                controlBarController.mouseEventTracker.move();
            }
        });


        primaryStage.setMinHeight(450);
        primaryStage.setMinWidth(705);

        App.stage = primaryStage;

        primaryStage.setFullScreenExitHint("Press Esc to exit fullscreen mode");


        primaryStage.addEventFilter(KeyEvent.KEY_PRESSED, event -> hotkeyController.handleKeyPress(event));

        primaryStage.addEventFilter(KeyEvent.KEY_RELEASED, event -> hotkeyController.handleKeyRelease(event));

        primaryStage.setScene(scene);
        primaryStage.setTitle("FXPlayer");
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("images/appIcon.png")).toExternalForm()));
        primaryStage.setMaximized(true);
        primaryStage.show();

        if(System.getProperty("os.name").toLowerCase().contains("windows")){
            isWindows = true;
            // program is being run on a windows operating system, so we can take advantage of windows features
            mainController.addTaskBarButtons();
        }

        primaryStage.setOnCloseRequest(event -> {
            if(!menuController.ongoingMetadataEditProcesses.isEmpty()){
                if(!mainController.closeConfirmationWindow.showing) mainController.closeConfirmationWindow.show();
                event.consume();
            }
            else mainController.closeApp();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }


    private void initializeConfig(){

        menuController.settingsPage.controlsSection.initializeControlsBox();

        controlBarController.loadTooltips();
        mainController.loadTooltips();
        menuController.menuBar.loadTooltips();
    }
}