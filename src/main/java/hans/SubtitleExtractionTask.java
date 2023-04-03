package hans;

import hans.Captions.CaptionsController;
import hans.MediaItems.MediaItem;
import javafx.concurrent.Task;

public class SubtitleExtractionTask extends Task<Boolean> {

    MediaItem mediaItem;
    CaptionsController captionsController;


    public SubtitleExtractionTask(CaptionsController captionsController, MediaItem mediaItem){
        this.mediaItem = mediaItem;
        this.captionsController = captionsController;
    }

    @Override
    protected Boolean call() {
        return captionsController.extractCaptions(mediaItem);
    }
}
