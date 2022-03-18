package hans;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class QueueItemOptionsPopUp extends ContextMenu {

    QueueItem queueItem;

    MenuItem playNext = new MenuItem("Play next");
    MenuItem metadata = new MenuItem("Show metadata");

    double buttonWidth;
    final double popUpWidth = 127; // calling getWidth on this pop-up window is inaccurate as it sometimes incorrectly shows 151, hard-coded value is used to always get the same result


    QueueItemOptionsPopUp(QueueItem queueItem){

        this.queueItem = queueItem;

        queueItem.getScene().getStylesheets().add(getClass().getResource("styles/optionsPopUp.css").toExternalForm());

        playNext.setOnAction((e) -> {
            queueItem.playNext();
        });

        metadata.setOnAction((e) -> {
            queueItem.showMetadata();
        });




        this.getItems().add(playNext);
        this.getItems().add(metadata);

        buttonWidth = queueItem.optionsButton.getWidth();

    }

    public void showOptions(){
        this.show(queueItem,
                queueItem.optionsButton.localToScreen(queueItem.optionsButton.getBoundsInLocal()).getMinX() + buttonWidth/2 - popUpWidth/2,
                queueItem.optionsButton.localToScreen(queueItem.optionsButton.getBoundsInLocal()).getMaxY() + 5);

    }

}
