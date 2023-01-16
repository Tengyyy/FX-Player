package hans.Chapters;

import hans.MediaItems.MediaItem;
import hans.MediaItems.MediaUtilities;
import hans.Menu.MenuController;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.util.Duration;

import java.io.File;

public class ChapterFrameGrabberTask extends Task<Image> {

    MediaItem mediaItem;
    Duration time;

    public ChapterFrameGrabberTask(MediaItem mediaItem, Duration time){
        this.mediaItem = mediaItem;
        this.time = time;
    }


    @Override
    protected Image call() {
        return MediaUtilities.getVideoFrame(mediaItem.getFile(), mediaItem.videoStream.getIndex(), (long) time.toMillis(), 125, 70);
    }
}
