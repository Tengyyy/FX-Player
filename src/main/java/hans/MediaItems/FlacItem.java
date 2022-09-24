package hans.MediaItems;

import hans.MainController;
import hans.MediaItems.MediaItem;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.bytedeco.javacv.FFmpegFrameGrabber;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FlacItem implements MediaItem {

    double frameRate = 30;
    float frameDuration = (float) (1 / frameRate);

    File file;
    File subtitles;
    boolean subtitlesOn = false;
    Color backgroundColor = null;
    Duration duration;

    Image cover;

    MainController mainController;

    Map<String, String> mediaInformation = new HashMap<>();

    public FlacItem(File file, MainController mainController) {
        this.file = file;
        this.mainController = mainController;

        try {
            FFmpegFrameGrabber fFmpegFrameGrabber = new FFmpegFrameGrabber(file);


            fFmpegFrameGrabber.start();


            duration = Duration.seconds((int) (fFmpegFrameGrabber.getLengthInAudioFrames() / fFmpegFrameGrabber.getAudioFrameRate()));
            frameRate = fFmpegFrameGrabber.getAudioFrameRate();
            frameDuration = (float) (1 / frameRate);

            for(Map.Entry<String, String> entry : fFmpegFrameGrabber.getMetadata().entrySet()){
                mediaInformation.put(entry.getKey().toLowerCase(), entry.getValue());
            }


            fFmpegFrameGrabber.stop();
            fFmpegFrameGrabber.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if(cover == null){
            cover = new Image(Objects.requireNonNull(Objects.requireNonNull(mainController.getClass().getResource("images/default.png")).toExternalForm()));
            backgroundColor = Color.rgb(254, 200, 149);
        }
    }

    @Override
    public float getFrameDuration() {
        return frameDuration;
    }

    @Override
    public Map<String, String> getMediaInformation() {
        return mediaInformation;
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