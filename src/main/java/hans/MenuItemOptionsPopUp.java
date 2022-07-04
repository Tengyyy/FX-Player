package hans;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;

import java.io.File;

public class MenuItemOptionsPopUp extends ContextMenu {

    MenuObject menuObject;

    MenuItem playNext = new MenuItem("Play next");
    MenuItem metadata = new MenuItem("Show metadata");
    MenuItem addSubtitles = new MenuItem("Add subtitles");

    double buttonWidth;
    final double popUpWidth = 127; // calling getWidth on this pop-up window is inaccurate as it sometimes incorrectly shows 151, hard-coded value is used to always get the same result

    Node menuObjectNode;


    MenuItemOptionsPopUp(MenuObject menuObject){

        this.menuObject = menuObject;

        menuObjectNode = (Node) menuObject;

        menuObjectNode.getScene().getStylesheets().add(getClass().getResource("styles/optionsPopUp.css").toExternalForm());

        playNext.setOnAction((e) -> {
            if(!menuObject.getMenuController().animationsInProgress.isEmpty()) return;
            menuObject.playNext();
        });

        metadata.setOnAction((e) -> {
            menuObject.showMetadata();
        });

        addSubtitles.setOnAction((e) -> {
            openSubtitleChooser();
        });




        this.getItems().add(playNext);
        this.getItems().add(metadata);
        this.getItems().add(addSubtitles);

        buttonWidth = menuObject.getOptionsButton().getWidth();

    }

    public void showOptions(){
        this.show(menuObjectNode, // might not work
                menuObject.getOptionsButton().localToScreen(menuObject.getOptionsButton().getBoundsInLocal()).getMinX() + buttonWidth/2 - popUpWidth/2,
                menuObject.getOptionsButton().localToScreen(menuObject.getOptionsButton().getBoundsInLocal()).getMaxY() + 5);
    }


    public void openSubtitleChooser(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select subtitles");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Subtitles", "*.srt"));
        fileChooser.setInitialDirectory(menuObject.getMediaItem().getFile().getParentFile());

        File selectedFile = fileChooser.showOpenDialog(App.stage);

        if(selectedFile != null){
            menuObject.addSubtitles(selectedFile);
        }
    }

}
