package hans;


import java.io.File;
import java.io.IOException;
import java.net.URL;

import java.util.ArrayList;
import java.util.ResourceBundle;


import com.jfoenix.controls.JFXButton;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;


public class MenuController implements Initializable {

    @FXML
    GridPane menuPane;

    @FXML
    JFXButton queueButton, addButton;

    @FXML
    StackPane menuContent;

    @FXML
    Pane queueLine, addLine, addBackground;

    @FXML
    AnchorPane addPane, queuePane;

    @FXML
    VBox addBox, queueBox;

    @FXML
    ScrollPane queueScroll;

    @FXML
    TextFlow addVideosText, addedVideosText1, addedVideosText2;

    @FXML
    Text addVideosBoldText, addVideosNormalText, addedVideosNormalText;

    @FXML
    Region svgShape;

    @FXML
    Label queueNotification;

    MainController mainController;
    ControlBarController controlBarController;
    SettingsController settingsController;
    MediaInterface mediaInterface;

    boolean queueTabOpen = false;
    boolean tabAnimationInProgress = false;

    String activeLine = "-fx-background-color: red";
    String inactiveLine = "-fx-background-color: transparent";

    FileChooser fileChooser = new FileChooser();

    ArrayList<File> dragBoardFiles;
    ArrayList<File> dragBoardVideos = new ArrayList<File>();

    ArrayList<QueueItem> queue = new ArrayList<QueueItem>();

    boolean videosAdded = false; // if true the user has added videos to the add pane and has yet to open the queue, which means that the text should show many videos were added

    int videosAddedCounter = 0; // keeps track of how many videos have been added, while the user hasnt openened the queue (queue tab header counter)

    //Animations//
    FadeTransition queueNotificationFade = new FadeTransition();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        fileChooser.setTitle("Open video");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Videos", "*.mp4"));

        addLine.setStyle(activeLine);
        queueLine.setStyle(inactiveLine);

        SVGPath dragAndDropIndicator1 = new SVGPath();
        dragAndDropIndicator1.setContent("m 15.27,23.707 c 0.389,0.385 1.04,0.389 1.429,0 l 6.999,-6.9 c 0.395,-0.391 0.394,-1.024 0,-1.414 -0.394,-0.391 -1.034,-0.391 -1.428,0 l -5.275,5.2 V 1 c 0,-0.552 -0.452,-1 -1.01,-1 -0.558,0 -1.01,0.448 -1.01,1 V 20.593 L 9.7,15.393 c -0.395,-0.391 -1.034,-0.391 -1.428,0 -0.395,0.391 -0.395,1.024 0,1.414 z M 31,22 c -0.552,0 -1,0.448 -1,1 v 7 H 2 V 23 C 2,22.448 1.552,22 1,22 0.448,22 0,22.448 0,23 v 8 c 0,0.552 0.448,1 1,1 h 30 c 0.552,0 1,-0.448 1,-1 v -8 c 0,-0.552 -0.448,-1 -1,-1 z");

        svgShape.setShape(dragAndDropIndicator1);
        svgShape.setStyle("-fx-background-color: white;");
        svgShape.setMinSize(128, 128);
        svgShape.setPrefSize(128, 128);
        svgShape.setMaxSize(128, 128);

        Platform.runLater(() -> {
            queuePane.translateXProperty().unbind();
            addPane.translateXProperty().unbind();
            queuePane.translateXProperty().bind(queuePane.getScene().widthProperty().multiply(-1));

        });

        addBox.setSpacing(10);

       addBox.getChildren().removeAll(addedVideosText1, addedVideosText2);

       queueNotification.setOpacity(0);

    }

    public void openQueueTab(){
        if(queueTabOpen || tabAnimationInProgress) return;
        tabAnimationInProgress = true;

        queueLine.setStyle(activeLine);
        addLine.setStyle(inactiveLine);

        if(videosAdded){
            videosAddedCounter = 0;

            if(queueNotificationFade.getStatus() == Animation.Status.RUNNING){
                queueNotificationFade.stop();
            }

            queueNotification.setOpacity(0);
        }

        AnimationsClass.openQueueTab(addPane, queuePane, this);

    }

    public void openAddVideosTab(){
        if(!queueTabOpen || tabAnimationInProgress) return;
        tabAnimationInProgress = true;

        addLine.setStyle(activeLine);
        queueLine.setStyle(inactiveLine);

        for(QueueItem queueItem : queue){
            if(queueItem.marqueeTimeline != null && queueItem.marqueeTimeline.getStatus() == Animation.Status.RUNNING){
                queueItem.videoTitle.setLayoutX(0);
                //queueItem.marqueeOnTimeline.stop(); // got to figure if not stopping this timeline causes a memory leak or not
            }
        }

        if(videosAdded) {
            videosAdded = false;
            addBox.getChildren().removeAll(addedVideosText1, addedVideosText2);
            addBox.getChildren().add(addVideosText);
        }

        AnimationsClass.openAddVideosTab(addPane, queuePane, this);
    }

    public void openVideoChooser() throws IOException {

        File selectedFile = fileChooser.showOpenDialog(addPane.getScene().getWindow());

        if(selectedFile != null){

            mediaInterface.videoList.add(selectedFile);
            mediaInterface.unplayedVideoList.add(selectedFile);

            new QueueItem(selectedFile, this);

            videosAddedCounter++;


            queueNotification.setText(String.valueOf(videosAddedCounter));
            //play notification animation (blink 3 times)
            queueNotification.setOpacity(1);

            AnimationsClass.fadeAnimation(queueNotificationFade, 500, queueNotification, 1, 0, true, 4, true);

            addedVideosNormalText.setText("Added 1 video to the queue.");

            if(!videosAdded){
                videosAdded = true;

                addBox.getChildren().remove(addVideosText);
                addBox.getChildren().addAll(addedVideosText1, addedVideosText2);
            }
        }

    }

    public void addPaneDragEntered(DragEvent e) {
       dragBoardFiles = (ArrayList<File>) e.getDragboard().getFiles();

       for(File file : dragBoardFiles){
           if(Utilities.getFileExtension(file).equals("mp4")){
               dragBoardVideos.add(file);
           }
       }

       if(dragBoardVideos.isEmpty()) return;

       //if the dragboard contains mp4 files, play the file adding animation

        AnimationsClass.addPaneDragEntered(addBackground, addPane);

    }

    public void addPaneDragOver(DragEvent e){
        if(!dragBoardVideos.isEmpty()){
            e.acceptTransferModes(TransferMode.COPY);
        }
    }

    public void addPaneDragDropped(DragEvent e){

        if(dragBoardVideos.isEmpty()) return;

        // add mp4 files to mediainterface queue, create queue objects in the menu, show popup indicating how many videos were added to the queue and a blinking indicator inside the queue tab button to show how many new videos have been to the queue in total

        mediaInterface.videoList.addAll(dragBoardVideos);
        mediaInterface.unplayedVideoList.addAll(dragBoardVideos);

        for(File vid : dragBoardVideos){
            new QueueItem(vid, this);
        }


        int dragVideosAdded = dragBoardVideos.size();
        dragBoardVideos.clear();

        videosAddedCounter += dragVideosAdded;



        queueNotification.setText(String.valueOf(videosAddedCounter));
        //play notification animation (blink 3 times)

        queueNotification.setOpacity(1);
       // AnimationsClass.queueNotificationBlink(queueNotification);
        AnimationsClass.fadeAnimation(queueNotificationFade, 500, queueNotification, 1, 0, true, 4, true);


        if(dragVideosAdded == 1) addedVideosNormalText.setText("Added 1 video to the queue.");
        else addedVideosNormalText.setText("Added " + dragVideosAdded + " videos to the queue.");

        if(!videosAdded){
            videosAdded = true;

            addBox.getChildren().remove(addVideosText);
            addBox.getChildren().addAll(addedVideosText1, addedVideosText2);
        }
        AnimationsClass.addPaneDragDropped(addBackground, addPane);
    }

    public void addPaneDragExited(){

        dragBoardVideos.clear();

        if(AnimationsClass.addPaneDragEntered != null){
            AnimationsClass.addPaneDragEntered.stop();
        }

        addBackground.setScaleX(1);
        addBackground.setScaleY(1);

        addPane.setBackground(new Background(new BackgroundFill(Color.web("#202020"), CornerRadii.EMPTY, Insets.EMPTY)));

    }

    public void init(MainController mainController, ControlBarController controlBarController, SettingsController settingsController, MediaInterface mediaInterface){
        this.mainController = mainController;
        this.controlBarController = controlBarController;
        this.settingsController = settingsController;
        this.mediaInterface = mediaInterface;
    }
}


