package hans;


import com.goxr3plus.fxborderlessscene.borderless.BorderlessScene;
import hans.Menu.MenuController;
import hans.PlaybackSettings.PlaybackSettingsController;
import javafx.scene.input.KeyCode;
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

        /*stage.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            switch (event.getCode()) {
                case RIGHT -> miniplayerController.pressRIGHT(event);
                case LEFT -> miniplayerController.pressLEFT(event);
                case UP -> mainController.pressUP(event);
                case DOWN -> mainController.pressDOWN(event);
                case J, REWIND -> mainController.pressJ();
                case L, FAST_FWD -> mainController.pressL(event);
                case P -> mainController.pressP(event);
                case N -> mainController.pressN(event);
                case COMMA -> mainController.pressCOMMA(event);
                case PERIOD -> mainController.pressPERIOD(event);
                case M -> mainController.pressM();
                case I -> mainController.pressI();
                case SPACE, K, PLAY, PAUSE -> mainController.pressSPACE(event);
                case DIGIT1 -> mainController.press1();
                case DIGIT2 -> mainController.press2();
                case DIGIT3 -> mainController.press3();
                case DIGIT4 -> mainController.press4();
                case DIGIT5 -> mainController.press5();
                case DIGIT6 -> mainController.press6();
                case DIGIT7 -> mainController.press7();
                case DIGIT8 -> mainController.press8();
                case DIGIT9 -> mainController.press9();
                case DIGIT0, HOME -> mainController.press0();
                case END -> mainController.pressEND();
                case TRACK_PREV -> mainController.pressPreviousTrack();
                case TRACK_NEXT -> mainController.pressNextTrack();
            }
        });*/

        stage.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
            if(event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.J || event.getCode() == KeyCode.L || event.getCode() == KeyCode.REWIND || event.getCode() == KeyCode.FAST_FWD || event.getCode() == KeyCode.DIGIT1 || event.getCode() == KeyCode.DIGIT2 || event.getCode() == KeyCode.DIGIT3 || event.getCode() == KeyCode.DIGIT4 || event.getCode() == KeyCode.DIGIT5 || event.getCode() == KeyCode.DIGIT6 || event.getCode() == KeyCode.DIGIT7 || event.getCode() == KeyCode.DIGIT8 || event.getCode() == KeyCode.DIGIT9 || event.getCode() == KeyCode.DIGIT0 || event.getCode() == KeyCode.HOME || event.getCode() == KeyCode.END){
                mainController.seekingWithKeys = false;
            }
        });

        stage.setOnCloseRequest(event -> {
            mainController.closeMiniplayer();
        });

        stage.show();
    }


}
