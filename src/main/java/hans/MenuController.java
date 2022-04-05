package hans;


import java.io.File;
import java.io.IOException;
import java.net.URL;


import java.util.ArrayList;
import java.util.ResourceBundle;


import animatefx.animation.SlideOutLeft;


import com.jfoenix.controls.JFXButton;
import eu.iamgio.animated.AnimationPair;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import eu.iamgio.animated.AnimatedVBox;
import animatefx.animation.FadeInUp;
import javafx.util.Duration;




public class MenuController implements Initializable {

    @FXML
    JFXButton addButton;

    @FXML
    StackPane menu, notificationPane;

    @FXML
    Text notificationText;

    @FXML
    ScrollPane queueScroll;



    AnimatedVBox queueBox = new AnimatedVBox(new AnimationPair(new FadeInUp(), new SlideOutLeft()).setSpeed(3, 3));


    MainController mainController;
    ControlBarController controlBarController;
    SettingsController settingsController;
    MediaInterface mediaInterface;

    QueueItem activeItem;

    FileChooser fileChooser = new FileChooser();

    ArrayList<QueueItem> queue = new ArrayList<>();

    boolean menuOpen;
    boolean menuNotificationOpen = false;
    PauseTransition closeTimer;

    double prefWidth = 350;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        fileChooser.setTitle("Open video");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Videos", "*.mp4"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Audio", "*.mp3"));


       queueBox.setSpacing(10);
       queueBox.setPadding(new Insets(20, 20, 20, 20));
       queueBox.setId("queueBox");
       queueBox.setAlignment(Pos.TOP_CENTER);
       queueScroll.setContent(queueBox);

       // creates queue items for all the items in the videolist when opening the menu



        closeTimer = new PauseTransition(Duration.millis(3000));
        closeTimer.setOnFinished((e) -> AnimationsClass.closeMenuNotification(this));


    }


    public void openVideoChooser() throws IOException {

        File selectedFile = fileChooser.showOpenDialog(menu.getScene().getWindow());

        if(selectedFile != null){

            MediaItem temp = null;

            if(Utilities.getFileExtension(selectedFile).equals("mp4")) temp = new Mp4Item(selectedFile);
            else if(Utilities.getFileExtension(selectedFile).equals("mp3")) temp = new Mp3Item(selectedFile);


            mediaInterface.videoList.add(temp);
            mediaInterface.unplayedVideoList.add(temp);

            new QueueItem(temp, this, mediaInterface);

        }
    }


    public void init(MainController mainController, ControlBarController controlBarController, SettingsController settingsController, MediaInterface mediaInterface){
        this.mainController = mainController;
        this.controlBarController = controlBarController;
        this.settingsController = settingsController;
        this.mediaInterface = mediaInterface;
    }
}



