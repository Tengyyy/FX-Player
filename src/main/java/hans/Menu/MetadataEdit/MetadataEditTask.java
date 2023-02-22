package hans.Menu.MetadataEdit;

import hans.MediaItems.MediaItem;
import javafx.concurrent.Task;

import java.util.Map;

public class MetadataEditTask extends Task<Boolean> {

    MediaItem mediaItem;

    MetadataEditTask(MediaItem mediaItem){
        this.mediaItem = mediaItem;
    }


    @Override
    protected Boolean call() {
        return mediaItem.updateMetadata();
    }
}