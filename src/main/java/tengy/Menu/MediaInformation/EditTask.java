package tengy.Menu.MediaInformation;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import tengy.MediaItems.MediaItem;
import javafx.concurrent.Task;
import tengy.MediaItems.MediaUtilities;

import java.io.File;

public class EditTask extends Task<Boolean> {

    MediaItem mediaItem;
    File outputFile;

    BooleanProperty editActiveProperty = null;

    EditTask(MediaItem mediaItem){
        this.mediaItem = mediaItem;
    }

    EditTask(MediaItem mediaItem, File outputFile, BooleanProperty editActiveProperty){
        this.mediaItem = mediaItem;
        this.outputFile = outputFile;
        this.editActiveProperty = editActiveProperty;
    }


    @Override
    protected Boolean call() {
        if(outputFile == null)
            return mediaItem.updateMetadata();
        else
            return MediaUtilities.createFileWithUpdatedMetadata(mediaItem, outputFile, editActiveProperty);
    }
}