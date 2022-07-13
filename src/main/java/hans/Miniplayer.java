package hans;


import com.goxr3plus.fxborderlessscene.borderless.BorderlessScene;
import com.goxr3plus.fxborderlessscene.borderless.CustomStage;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Miniplayer {

    MainController mainController;
    ControlBarController controlBarController;
    MenuController menuController;
    MediaInterface mediaInterface;

    Stage stage;
    BorderlessScene scene;

    MiniplayerController miniplayerController;

    final int MIN_WIDTH = 300;
    final int MIN_HEIGHT = 200;
    final int MAX_WIDTH = 900;
    final int MAX_HEIGHT = 700;

    Miniplayer(MainController mainController, ControlBarController controlBarController, MenuController menuController, MediaInterface mediaInterface){

        this.mainController = mainController;
        this.controlBarController = controlBarController;
        this.menuController = menuController;
        this.mediaInterface = mediaInterface;

        miniplayerController = new MiniplayerController(mainController, controlBarController, menuController, mediaInterface, this);

        stage = new Stage(StageStyle.UNDECORATED);
        stage.setMinWidth(MIN_WIDTH);
        stage.setMinHeight(MIN_HEIGHT);
        stage.setMaxWidth(MAX_WIDTH);
        stage.setMaxHeight(MAX_HEIGHT);

        scene = new BorderlessScene(stage, StageStyle.UNDECORATED, miniplayerController.mediaViewWrapper);

        miniplayerController.initActions();

        stage.setAlwaysOnTop(true);
        stage.setTitle("Miniplayer");
        stage.setScene(scene);
        stage.setWidth(700);
        stage.setHeight(500);

        stage.show();
    }
}
