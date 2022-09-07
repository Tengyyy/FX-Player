package hans.MediaItems;

import hans.MediaItems.MediaItem;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.bytedeco.javacv.FFmpegFrameGrabber;


import java.io.File;
import java.io.IOException;
import java.util.Map;

public class WavItem implements MediaItem {

    double frameRate = 30;
    float frameDuration = (float) (1 / frameRate);

    File file;
    File subtitles;
    boolean subtitlesOn = false;
    Color backgroundColor = null;
    Duration duration;


    //Metadata tags
    String album;
    String artist;
    String title;

    Image cover;


    public WavItem(File file) {
        this.file = file;


        try {
            FFmpegFrameGrabber fFmpegFrameGrabber = new FFmpegFrameGrabber(file);


            fFmpegFrameGrabber.start();
            duration = Duration.seconds((int) (fFmpegFrameGrabber.getLengthInAudioFrames() / fFmpegFrameGrabber.getAudioFrameRate()));
            frameRate = fFmpegFrameGrabber.getAudioFrameRate();
            frameDuration = (float) (1 / frameRate);

            System.out.println(fFmpegFrameGrabber.getMetadata());

            fFmpegFrameGrabber.stop();
            fFmpegFrameGrabber.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public float getFrameDuration() {
        return frameDuration;
    }

    @Override
    public Map<String, String> getMediaInformation() {
        return null;
    }

    @Override
    public Map getMediaDetails() {
        return null;
    }


    @Override
    public File getFile() {
        return this.file;
    }

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
        return duration;
    }

    @Override
    public String getArtist() {
        return artist;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public Image getCover() {
        return cover;
    }

    @Override
    public void setSubtitles(File file) {
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

    @Override
    public boolean hasVideo() {
        return false;
    }

}