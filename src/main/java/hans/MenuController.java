package hans;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class MenuController implements Initializable {

    @FXML
    Pane menuBackgroundPane;

    @FXML
    ScrollPane menuScroll;

    @FXML
    VBox menuContainer;

    @FXML
    Text dragDropText;


    // list of all the HBoxes inside the menu, each representing a video in the queue
    ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();

    boolean dragFinished = false;
    boolean dragStarted = false;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // fit scrollpane to whole scene
        menuScroll.prefWidthProperty().bind(Bindings.selectDouble(menuScroll.sceneProperty(), "width"));
        menuScroll.prefHeightProperty().bind(Bindings.selectDouble(menuScroll.sceneProperty(), "height"));

        menuContainer.setStyle("-fx-border-width: 2; -fx-border-radius: 0; -fx-border-style: dashed; -fx-border-color: #F3F1F5;");

        dragDropText.wrappingWidthProperty().bind(Bindings.subtract(menuContainer.widthProperty(), 30));

    }

    public void menuDragOver(DragEvent e) {
        if (e.getDragboard().hasFiles() && dragStarted) {
            e.acceptTransferModes(TransferMode.ANY);
        } else {
            e.consume();
        }
    }

    public void menuDragDropped(DragEvent e) {
        dragStarted = false;
    }

    public void menuDragEntered(DragEvent e) {

        dragStarted = true;

        List<File> dragBoardFiles = e.getDragboard().getFiles();
        List<File> dragBoardVideos = new ArrayList<File>();

        for (File file : dragBoardFiles) {
            if (Utilities.getFileExtension(file).equals("mp4")) {
                dragBoardVideos.add(file);
            }
        }

        if (!dragBoardVideos.isEmpty()) {

            if (menuItems.isEmpty()) {
                menuContainer.setStyle("-fx-border-width: 0;");
                menuContainer.getChildren().remove(0);
                menuContainer.setAlignment(Pos.TOP_CENTER);
            }

            for (File file : dragBoardVideos) {
                MenuItem menuItem = new MenuItem(menuScroll, file);
                menuItems.add(menuItem);
                menuContainer.getChildren().add(menuItem);
            }

        } else {
            e.consume();
        }
    }
}
