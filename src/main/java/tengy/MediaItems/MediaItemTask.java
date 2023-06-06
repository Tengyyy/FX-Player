package tengy.MediaItems;

import javafx.concurrent.Task;
import tengy.Menu.MenuController;
import tengy.Utilities;

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
