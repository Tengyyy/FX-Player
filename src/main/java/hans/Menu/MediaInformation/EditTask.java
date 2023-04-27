package hans.Menu.MediaInformation;

import hans.MediaItems.MediaItem;
import javafx.concurrent.Task;

import java.io.File;

public class EditTask extends Task<Boolean> {

    MediaItem mediaItem;
    File outputFile;

    EditTask(MediaItem mediaItem){
        this.mediaItem = mediaItem;
    }

    EditTask(MediaItem mediaItem, File outputFile){
        this.mediaItem = mediaItem;
        this.outputFile = outputFile;
    }


    @Override
    protected Boolean call() {
        if(outputFile == null)
            return mediaItem.updateMetadata();
        else
            return mediaItem.createNewFile(outputFile);
    }
}