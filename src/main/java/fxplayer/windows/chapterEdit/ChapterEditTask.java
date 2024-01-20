package fxplayer.windows.chapterEdit;

import javafx.concurrent.Task;
import fxplayer.mediaItems.MediaItem;

import java.io.File;


public class ChapterEditTask extends Task<Boolean> {

    MediaItem mediaItem;
    File output = null;

    ChapterEditTask(MediaItem mediaItem){
        this.mediaItem = mediaItem;
    }

    ChapterEditTask(MediaItem mediaItem, File output){
        this.mediaItem = mediaItem;
        this.output = output;
    }

    @Override
    protected Boolean call() throws Exception {
        if(output == null) return mediaItem.updateChapters();
        else return mediaItem.updateChapters(output);
    }
}
