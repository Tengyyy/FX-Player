package hans;


import java.net.URL;

import java.util.ArrayList;
import java.util.ResourceBundle;


import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.*;


public class MenuController implements Initializable {

    @FXML
    GridPane menuPane;

    @FXML
    JFXButton queueButton, addButton;

    @FXML
    StackPane menuContent;

    @FXML
    Pane queueLine, addLine;

    @FXML
    AnchorPane addPane, queuePane;

    @FXML
    VBox addBox, queueBox;

    @FXML
    ScrollPane queueScroll;

    ArrayList<Tab> tabs = new ArrayList<Tab>();

    boolean queueTabOpen = false;

    String activeLine = "-fx-background-color: red";
    String inactiveLine = "-fx-background-color: transparent";


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        addLine.setStyle(activeLine);
        queueLine.setStyle(inactiveLine);

        Platform.runLater(() -> {
            queuePane.translateXProperty().unbind();
            queuePane.translateXProperty().bind(queuePane.getScene().widthProperty().multiply(-1));
        });

    }

    public void openQueueTab(){
        if(queueTabOpen) return;

        queueLine.setStyle(activeLine);
        addLine.setStyle(inactiveLine);


        AnimationsClass.openQueueTab(addPane, queuePane, this);

    }

    public void openAddVideosTab(){
        if(!queueTabOpen) return;

        addLine.setStyle(activeLine);
        queueLine.setStyle(inactiveLine);

        AnimationsClass.openAddVideosTab(addPane, queuePane, this);
    }

}


