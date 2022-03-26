package hans;

import javafx.scene.media.Media;

import java.io.File;
import java.util.Map;

public class Mp3Item implements MediaItem{

    double frameRate = 30;

    File file;
    Media media;


    // ID-3 tags
    String album;
    String artist;

    Mp3Item(File file){
        this.file = file;

        media = new Media(file.toURI().toString());
    }

    @Override
    public double getFrameRate() {
        return frameRate;
    }

    @Override
    public Map getMediaInformation() {
        return null;
    }

    @Override
    public Map getMediaDetails() {
        return null;
    }

    @Override
    public Media getMedia() {
        return this.media;
    }

    @Override
    public File getFile() {return this.file;}
}
