package hans;

import hans.Captions.CaptionsController;
import hans.Menu.QueueItem;
import javafx.concurrent.Task;

public class SubtitleExtractionTask extends Task<Boolean> {

    QueueItem queueItem;
    CaptionsController captionsController;


    public SubtitleExtractionTask(CaptionsController captionsController, QueueItem queueItem){
        this.queueItem = queueItem;
        this.captionsController = captionsController;
    }

    @Override
    protected Boolean call() {
        captionsController.extractCaptions(queueItem);
        return true;
    }
}
