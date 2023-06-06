package tengy.Windows.ChapterEdit;

import javafx.concurrent.Task;
import tengy.MediaItems.MediaItem;


public class ChapterEditTask extends Task<Boolean> {

    MediaItem mediaItem;

    ChapterEditTask(MediaItem mediaItem){
        this.mediaItem = mediaItem;
    }

    @Override
    protected Boolean call() throws Exception {
        return mediaItem.updateChapters();
    }
}
