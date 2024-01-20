package tengy;

import tengy.subtitles.SubtitlesController;
import tengy.mediaItems.MediaItem;
import javafx.concurrent.Task;

public class SubtitleExtractionTask extends Task<Boolean> {

    MediaItem mediaItem;
    SubtitlesController subtitlesController;


    public SubtitleExtractionTask(SubtitlesController subtitlesController, MediaItem mediaItem){
        this.mediaItem = mediaItem;
        this.subtitlesController = subtitlesController;
    }

    @Override
    protected Boolean call() {
        return subtitlesController.extractSubtitles(mediaItem);
    }
}
