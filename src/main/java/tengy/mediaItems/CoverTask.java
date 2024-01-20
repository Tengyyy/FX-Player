package tengy.mediaItems;

import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.util.Pair;

public class CoverTask extends Task<Boolean> {

    MediaItem mediaItem;

    public CoverTask(MediaItem mediaItem){
        this.mediaItem = mediaItem;
    }

    @Override
    protected Boolean call() throws Exception {
        Pair<Boolean, Image> pair = MediaUtilities.getCover(mediaItem.probeResult, mediaItem.file);
        mediaItem.cover = pair.getValue();
        mediaItem.hasCover = pair.getKey();

        if(mediaItem.cover != null) mediaItem.backgroundColor = MediaUtilities.findDominantColor(mediaItem.cover);

        return true;
    }
}
