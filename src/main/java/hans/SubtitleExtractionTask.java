package hans;

import hans.Captions.CaptionsController;
import hans.Menu.ActiveItem;
import javafx.concurrent.Task;

public class SubtitleExtractionTask extends Task<Boolean> {

    ActiveItem activeItem;
    CaptionsController captionsController;


    public SubtitleExtractionTask(CaptionsController captionsController, ActiveItem activeItem){
        this.activeItem = activeItem;
        this.captionsController = captionsController;
    }

    @Override
    protected Boolean call() {

        captionsController.extractCaptions(activeItem);

        return true;
    }
}
