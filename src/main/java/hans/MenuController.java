package hans;


import java.io.File;
import java.net.URL;

import java.util.ArrayList;
import java.util.ResourceBundle;


import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
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
    TextFlow addVideosText;

    @FXML
    Text addVideosBoldText, addVideosNormalText;

    @FXML
    Region svgShape;
    boolean queueTabOpen = false;
    boolean tabAnimationInProgress = false;

    String activeLine = "-fx-background-color: red";
    String inactiveLine = "-fx-background-color: transparent";

    FileChooser fileChooser = new FileChooser();

    ArrayList<File> dragBoardFiles;
    ArrayList<File> dragBoardVideos = new ArrayList<File>();


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

    }

    public void openQueueTab(){
        if(queueTabOpen || tabAnimationInProgress) return;
        tabAnimationInProgress = true;

        queueLine.setStyle(activeLine);
        addLine.setStyle(inactiveLine);


        AnimationsClass.openQueueTab(addPane, queuePane, this);

    }

    public void openAddVideosTab(){
        if(!queueTabOpen || tabAnimationInProgress) return;
        tabAnimationInProgress = true;

        addLine.setStyle(activeLine);
        queueLine.setStyle(inactiveLine);

        AnimationsClass.openAddVideosTab(addPane, queuePane, this);
    }

    public void openVideoChooser(){
        System.out.println("hmmm");

        File selectedFile = fileChooser.showOpenDialog(App.stage);

        if(selectedFile != null){
            // add video to queue
            System.out.println("Added video to queue");
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

    }

    public void addPaneDragOver(DragEvent e){
        if(!dragBoardVideos.isEmpty()){
            e.acceptTransferModes(TransferMode.COPY);
        }
    }

    public void addPaneDragDropped(DragEvent e){
        // clear dragBoardVideos
        // add mp4 files to mediainterface queue, create queue objects in the menu, show popup indicating how many videos were added to the queue and a blinking indicator inside the queue tab button to show how many new videos have been to the queue in total

    }

    public void addPaneDragExited(){
        // clear dragBoardVideos
        // reset the addpane animation/styling
    }
}


