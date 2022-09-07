package hans.MediaItems;

import hans.MediaItems.MediaItem;
import hans.Utilities;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.JavaFXFrameConverter;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class MkvItem implements MediaItem {

    double frameRate = 30;
    float frameDuration = (float) (1 / frameRate);

    File file;
    File subtitles;
    boolean subtitlesOn = false;
    Color backgroundColor = null;
    Duration duration;


    Image cover;
    String title;
    String artist;

    boolean hasVideo;

    public MkvItem(File file){
        this.file = file;

        try {
            FFmpegFrameGrabber fFmpegFrameGrabber = new FFmpegFrameGrabber(file);

            fFmpegFrameGrabber.start();
            hasVideo = fFmpegFrameGrabber.hasVideo();

            if(fFmpegFrameGrabber.hasVideo()) duration = Duration.seconds(fFmpegFrameGrabber.getLengthInFrames() / fFmpegFrameGrabber.getFrameRate());
            else duration = Duration.seconds(fFmpegFrameGrabber.getLengthInAudioFrames() / fFmpegFrameGrabber.getAudioFrameRate());

            frameRate = fFmpegFrameGrabber.getFrameRate();
            frameDuration = (float) (1 / frameRate);

            Map<String, String> metadata = fFmpegFrameGrabber.getMetadata();

            if(metadata != null){
                for(Map.Entry<String, String> entry : metadata.entrySet()){
                    switch(entry.getKey()){
                        case "TITLE": title = entry.getValue();
                            break;
                        case "ARTIST": artist = entry.getValue();
                            break;
                        default:
                            break;
                    }
                }
            }

            fFmpegFrameGrabber.stop();


            fFmpegFrameGrabber.setVideoStream(2);

            fFmpegFrameGrabber.start();

            Frame frame = fFmpegFrameGrabber.grabImage();
            JavaFXFrameConverter javaFXFrameConverter = new JavaFXFrameConverter();
            if(frame != null) cover = javaFXFrameConverter.convert(frame);


            if(cover == null) cover = Utilities.grabMiddleFrame(file);


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
    public Map getMediaInformation() {
        return null;
    }

    @Override
    public Map getMediaDetails() {
        return null;
    }


    @Override
    public File getFile() {
        return file;
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
        subtitles = file;
    }

    @Override
    public Color getCoverBackgroundColor() {
        return backgroundColor;
    }

    @Override
    public void setCoverBackgroundColor(Color color) {
        backgroundColor = color;
    }

    @Override
    public boolean hasVideo() {
        return hasVideo;
    }
}