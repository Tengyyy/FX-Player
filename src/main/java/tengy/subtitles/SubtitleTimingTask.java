package tengy.subtitles;


import tengy.srtParser.utils.SRTUtils;
import tengy.Utilities;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.io.File;

public class SubtitleTimingTask extends Task<Boolean> {

    SubtitlesController subtitlesController;

    public SubtitleTimingTask(SubtitlesController subtitlesController){
        this.subtitlesController = subtitlesController;

    }

    @Override
    protected Boolean call() {
        File subtitleFile = new File(subtitlesController.subtitlesFile.getAbsolutePath());
        String extension = Utilities.getFileExtension(subtitleFile);
        String tempFileName = subtitleFile.getName().substring(0, subtitleFile.getName().length() - extension.length() - 1) + "temp." + extension;
        File tempFile = new File(subtitleFile.getParent() + "/" + tempFileName);
        boolean fileCreationSuccess = SRTUtils.adjustDelay(subtitlesController.subtitles, -subtitlesController.subtitleDelay, tempFile);

        if(fileCreationSuccess){
            boolean deleteSuccess = subtitleFile.delete();
            if(deleteSuccess){
                boolean renameSuccess = tempFile.renameTo(subtitleFile);
                if(renameSuccess){
                    Platform.runLater(() -> {
                        subtitlesController.timingPane.resetTiming();
                        subtitlesController.loadSubtitles(subtitleFile);
                    });
                }
            }
        }

        return true;
    }
}