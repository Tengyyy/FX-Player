package hans.Captions;

import hans.SRTParser.utils.SRTUtils;
import hans.Utilities;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.io.File;

public class SubtitleTimingTask extends Task<Boolean> {

    CaptionsController captionsController;

    SubtitleTimingTask(CaptionsController captionsController){
        this.captionsController = captionsController;

    }

    @Override
    protected Boolean call() {
        File subtitleFile = new File(captionsController.captionsFile.getAbsolutePath());
        String extension = Utilities.getFileExtension(subtitleFile);
        String tempFileName = subtitleFile.getName().substring(0, subtitleFile.getName().length() - extension.length() - 1) + "temp." + extension;
        File tempFile = new File(subtitleFile.getParent() + "/" + tempFileName);
        boolean fileCreationSuccess = SRTUtils.adjustDelay(captionsController.subtitles, -captionsController.subtitleDelay, tempFile);

        if(fileCreationSuccess){
            boolean deleteSuccess = subtitleFile.delete();
            if(deleteSuccess){
                boolean renameSuccess = tempFile.renameTo(subtitleFile);
                if(renameSuccess){
                    Platform.runLater(() -> {
                        captionsController.timingPane.resetTiming();
                        captionsController.loadCaptions(subtitleFile);
                    });
                }
            }
        }

        return true;
    }
}
