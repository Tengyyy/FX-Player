package tengy;


import com.goxr3plus.fxborderlessscene.borderless.BorderlessScene;
import tengy.Menu.MenuController;
import tengy.PlaybackSettings.PlaybackSettingsController;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Objects;

public class Miniplayer {

    MainController mainController;
    ControlBarController controlBarController;
    MenuController menuController;
    MediaInterface mediaInterface;
    PlaybackSettingsController playbackSettingsController;

    Stage stage;
    BorderlessScene scene;

    public MiniplayerController miniplayerController;

    final int MIN_WIDTH = 300;
    final int MIN_HEIGHT = 200;
    final int MAX_WIDTH = 900;
    final int MAX_HEIGHT = 700;

    Miniplayer(MainController mainController, ControlBarController controlBarController, MenuController menuController, MediaInterface mediaInterface, PlaybackSettingsController playbackSettingsController){

        this.mainController = mainController;
        this.controlBarController = controlBarController;
        this.menuController = menuController;
        this.mediaInterface = mediaInterface;
        this.playbackSettingsController = playbackSettingsController;

        miniplayerController = new MiniplayerController(mainController, controlBarController, menuController, mediaInterface, this);

        stage = new Stage(StageStyle.UNDECORATED);
        stage.setMinWidth(MIN_WIDTH);
        stage.setMinHeight(MIN_HEIGHT);
        stage.setMaxWidth(MAX_WIDTH);
        stage.setMaxHeight(MAX_HEIGHT);

        scene = new BorderlessScene(stage, StageStyle.TRANSPARENT, miniplayerController.videoImageViewWrapper);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles/miniplayer.css")).toExternalForm());


        miniplayerController.initActions();

        stage.setAlwaysOnTop(true);
        stage.setTitle("Miniplayer");
        stage.setScene(scene);
        stage.setWidth(700);
        stage.setHeight(500);

        stage.setY(Screen.getPrimary().getBounds().getMinY());
        stage.setX(Math.max(0, Screen.getPrimary().getBounds().getMaxX() - 700));

        stage.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
           mainController.hotkeyController.handleMiniplayerKeyPress(event);
        });

        stage.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
            mainController.hotkeyController.handleKeyRelease(event);
        });

        stage.setOnCloseRequest(event -> {
            mainController.closeMiniplayer();
        });

        stage.show();
    }


}
