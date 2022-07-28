package hans;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;

public class MenuItemOptionsPopUp extends ContextMenu {

    MenuObject menuObject;

    MenuItem playNext = new MenuItem("Play next");
    MenuItem metadata = new MenuItem("Show metadata");
    MenuItem addSubtitles = new MenuItem("Add subtitles");

    double buttonWidth;
    final double popUpWidth = 127; // calling getWidth on this pop-up window is inaccurate as it sometimes incorrectly shows 151, hard-coded value is used to always get the same result

    Node menuObjectNode;

    FadeTransition showTransition, hideTransition;


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

        this.getStyleableNode().setOpacity(0);

    }

    public void showOptions(){
        this.show(menuObjectNode, // might not work
                menuObject.getOptionsButton().localToScreen(menuObject.getOptionsButton().getBoundsInLocal()).getMinX() + buttonWidth/2 - popUpWidth/2,
                menuObject.getOptionsButton().localToScreen(menuObject.getOptionsButton().getBoundsInLocal()).getMaxY() + 5);
    }


    @Override
    public void show(Node node, double v, double v1) {

        if(hideTransition != null && hideTransition.getStatus() == Animation.Status.RUNNING) hideTransition.stop();

        this.getStyleableNode().setOpacity(0);

        super.show(node, v, v1);
        showTransition = new FadeTransition(Duration.millis(150), this.getStyleableNode());
        showTransition.setFromValue(0);
        showTransition.setToValue(1);
        showTransition.playFromStart();

    }

    @Override
    public void hide() {


        if(showTransition != null && showTransition.getStatus() == Animation.Status.RUNNING) showTransition.stop();

        hideTransition = new FadeTransition(Duration.millis(150), this.getStyleableNode());
        hideTransition.setFromValue(1);
        hideTransition.setToValue(0);
        hideTransition.setOnFinished(e -> {
            super.hide();
        });
        hideTransition.playFromStart();

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
