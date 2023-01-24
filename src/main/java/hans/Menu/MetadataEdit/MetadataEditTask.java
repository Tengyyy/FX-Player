package hans.Menu.MetadataEdit;

import hans.MediaItems.MediaItem;
import javafx.concurrent.Task;

import java.util.Map;

public class MetadataEditTask extends Task<Boolean> {

    MediaItem mediaItem;
    Map<String, String> mediaInformation;

    MetadataEditTask(MediaItem mediaItem, Map<String, String> mediaInformation){
        this.mediaItem = mediaItem;
        this.mediaInformation = mediaInformation;
    }


    @Override
    protected Boolean call() {
        return mediaItem.setMediaInformation(mediaInformation, true);
    }
}