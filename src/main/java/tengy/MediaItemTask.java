package tengy;

import tengy.MediaItems.MediaItem;
import tengy.Menu.MenuController;
import javafx.concurrent.Task;

import java.io.File;

public class MediaItemTask extends Task<MediaItem> {

    File file;
    MenuController menuController;

    public MediaItemTask(File file, MenuController menuController){
        this.file = file;
        this.menuController = menuController;
    }


    @Override
    protected MediaItem call() {
        return Utilities.createMediaItem(file, menuController.mainController);
    }
}
