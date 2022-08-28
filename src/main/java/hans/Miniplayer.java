package hans;


import com.goxr3plus.fxborderlessscene.borderless.BorderlessScene;
import hans.Menu.MenuController;
import hans.Settings.SettingsController;
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
    SettingsController settingsController;

    Stage stage;
    BorderlessScene scene;

    MiniplayerController miniplayerController;

    final int MIN_WIDTH = 300;
    final int MIN_HEIGHT = 200;
    final int MAX_WIDTH = 900;
    final int MAX_HEIGHT = 700;

    Miniplayer(MainController mainController, ControlBarController controlBarController, MenuController menuController, MediaInterface mediaInterface, SettingsController settingsController){

        this.mainController = mainController;
        this.controlBarController = controlBarController;
        this.menuController = menuController;
        this.mediaInterface = mediaInterface;
        this.settingsController = settingsController;

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
            switch(event.getCode()){
                case RIGHT: miniplayerController.pressRIGHT(event);
                break;
                case LEFT: miniplayerController.pressLEFT(event);
                break;
                case UP: mainController.pressUP();
                break;
                case DOWN: mainController.pressDOWN();
                break;
                case J: mainController.pressJ();
                break;
                case K: mainController.pressK();
                break;
                case L: mainController.pressL(event);
                break;
                case P: mainController.pressP(event);
                break;
                case N: mainController.pressN(event);
                break;
                case COMMA: mainController.pressCOMMA(event);
                break;
                case PERIOD: mainController.pressPERIOD(event);
                break;
                case C: mainController.pressC();
                break;
                case M: mainController.pressM();
                break;
                case I: mainController.pressI();
                break;
                case SPACE: mainController.pressSPACE(event);
                break;
                case DIGIT1: mainController.press1();
                break;
                case DIGIT2: mainController.press2();
                break;
                case DIGIT3: mainController.press3();
                break;
                case DIGIT4: mainController.press4();
                break;
                case DIGIT5: mainController.press5();
                break;
                case DIGIT6: mainController.press6();
                break;
                case DIGIT7: mainController.press7();
                break;
                case DIGIT8: mainController.press8();
                break;
                case DIGIT9: mainController.press9();
                break;
                case DIGIT0:
                case HOME: mainController.press0();
                break;
                case END: mainController.pressEND();
            }
        });

        stage.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
            if(event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.J || event.getCode() == KeyCode.L || event.getCode() == KeyCode.DIGIT1 || event.getCode() == KeyCode.DIGIT2 || event.getCode() == KeyCode.DIGIT3 || event.getCode() == KeyCode.DIGIT4 || event.getCode() == KeyCode.DIGIT5 || event.getCode() == KeyCode.DIGIT6 || event.getCode() == KeyCode.DIGIT7 || event.getCode() == KeyCode.DIGIT8 || event.getCode() == KeyCode.DIGIT9 || event.getCode() == KeyCode.DIGIT0 || event.getCode() == KeyCode.HOME || event.getCode() == KeyCode.END){
                mainController.seekingWithKeys = false;
            }
        });

        stage.show();
    }


}
