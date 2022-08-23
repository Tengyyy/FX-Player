package hans;

import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.File;
import java.util.Map;

public class Mp3Item implements MediaItem{

    double frameRate = 30;
    float frameDuration = (float) (1 / frameRate);

    File file;
    Media media;


    // ID-3 tags
    String album;
    String artist;

    File subtitles;
    boolean subtitlesOn = false;

    Color backgroundColor = null;

    Mp3Item(File file){
        this.file = file;

        media = new Media(file.toURI().toString());
    }

    @Override
    public float getFrameDuration() {
        return frameDuration;
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

    @Override
    public File getSubtitles() {
        return subtitles;
    }

    @Override
    public boolean getSubtitlesOn() {
        return subtitlesOn;
    }

    @Override
    public void setSubtitlesOn(boolean value) {
        subtitlesOn = value;
    }


    @Override
    public Duration getDuration() {
        return null;
    }

    @Override
    public String getArtist() {
        return null;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public Image getCover() {
        return null;
    }

    @Override
    public void setSubtitles(File file){
        this.subtitles = file;
    }

    @Override
    public Color getCoverBackgroundColor() {
        return backgroundColor;
    }

    @Override
    public void setCoverBackgroundColor(Color color) {
        this.backgroundColor = color;
    }
}
