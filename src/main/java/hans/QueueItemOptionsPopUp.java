package hans;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class QueueItemOptionsPopUp extends ContextMenu {

    MenuItem menuItem1 = new MenuItem("menu item 1");
    MenuItem menuItem2 = new MenuItem("menu item 2");

    QueueItemOptionsPopUp(){
        this.getItems().add(menuItem1);
        this.getItems().add(menuItem2);
    }

}
