package hans;

import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.JavaFXFrameConverter;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class MovItem implements MediaItem{

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

    String mediaType;

    MovItem(File file){
        this.file = file;

        try {
            FFmpegFrameGrabber fFmpegFrameGrabber = new FFmpegFrameGrabber(file);
            fFmpegFrameGrabber.setVideoStream(2);

            fFmpegFrameGrabber.start();
            if(fFmpegFrameGrabber.hasVideo()) duration = Duration.seconds(fFmpegFrameGrabber.getLengthInFrames() / fFmpegFrameGrabber.getFrameRate());
            else duration = Duration.seconds(fFmpegFrameGrabber.getLengthInAudioFrames() / fFmpegFrameGrabber.getAudioFrameRate());

            frameRate = fFmpegFrameGrabber.getFrameRate();
            frameDuration = (float) (1 / frameRate);

            Map<String, String> metadata = fFmpegFrameGrabber.getMetadata();

            if(metadata != null){
                for(Map.Entry<String, String> entry : metadata.entrySet()){
                    switch (entry.getKey()){
                        case "media_type":
                            switch(Integer.parseInt(entry.getValue())){
                                case 6: mediaType = "Music video";
                                    break;
                                case 9: mediaType = "Movie";
                                    break;
                                case 10: mediaType = "TV Show";
                                    break;
                                case 21: mediaType = "Podcast";
                                    break;
                                default: mediaType = "Home video";
                            }
                            break;
                        case "title": title = entry.getValue();
                            break;
                        case "artist": artist = entry.getValue();
                            break;
                        default:
                            break;
                    }
                }
            }

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
        if(mediaType != null && mediaType.equals("Music video")){
            return artist;
        }
        else return null;
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
}
