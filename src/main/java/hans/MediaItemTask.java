package hans;

import hans.MediaItems.MediaItem;
import hans.Menu.MenuController;
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
        return Utilities.searchDuplicateOrCreate(file, menuController);
    }
}
