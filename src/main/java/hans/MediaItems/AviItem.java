package hans.MediaItems;

import hans.MainController;
import hans.MediaItems.MediaItem;
import hans.Utilities;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.bytedeco.javacv.FFmpegFrameGrabber;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.*;

public class AviItem implements MediaItem {

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

    MainController mainController;

    Map<String, String> mediaInformation = new HashMap<>();
    Map<String, String> mediaDetails = new HashMap<>();


    public AviItem(File file, MainController mainController){
        this.file = file;
        this.mainController = mainController;

        try {
            FFmpegFrameGrabber fFmpegFrameGrabber = new FFmpegFrameGrabber(file);


            fFmpegFrameGrabber.start();

            hasVideo = fFmpegFrameGrabber.hasVideo();

            if(fFmpegFrameGrabber.hasVideo()) duration = Duration.seconds(fFmpegFrameGrabber.getLengthInFrames() / fFmpegFrameGrabber.getFrameRate());
            else duration = Duration.seconds(fFmpegFrameGrabber.getLengthInAudioFrames() / fFmpegFrameGrabber.getAudioFrameRate());

            frameRate = fFmpegFrameGrabber.getFrameRate();
            frameDuration = (float) (1 / frameRate);

            for(Map.Entry<String, String> entry : fFmpegFrameGrabber.getMetadata().entrySet()){
                mediaInformation.put(entry.getKey().toLowerCase(), entry.getValue());
            }


            if(cover == null) cover = Utilities.grabRandomFrame(file);

            System.out.println("Video codec name: " + fFmpegFrameGrabber.getVideoCodecName());
            System.out.println("Video bitrate: " + fFmpegFrameGrabber.getVideoBitrate());
            System.out.println("Video codec: " + fFmpegFrameGrabber.getVideoCodec());
            System.out.println("Streams: " + fFmpegFrameGrabber.getVideoStream());
            System.out.println("Video framerate: " + fFmpegFrameGrabber.getFrameRate());
            System.out.println("Sample rate: " + fFmpegFrameGrabber.getSampleRate());
            System.out.println("Format: " + fFmpegFrameGrabber.getFormat());
            System.out.println("Aspect ratio: " + fFmpegFrameGrabber.getAspectRatio());
            System.out.println("Image height: " + fFmpegFrameGrabber.getImageHeight());
            System.out.println("Image width: " + fFmpegFrameGrabber.getImageWidth());
            System.out.println("Length in frames: " + fFmpegFrameGrabber.getLengthInFrames());
            System.out.println("Audio framerate: " + fFmpegFrameGrabber.getAudioFrameRate());
            System.out.println("Audio bitrate: " + fFmpegFrameGrabber.getAudioBitrate());
            System.out.println("Audio codec name: " + fFmpegFrameGrabber.getAudioCodecName());
            System.out.println("Audio codec: " + fFmpegFrameGrabber.getAudioCodecName());
            System.out.println("Audio channels: " + fFmpegFrameGrabber.getAudioChannels());

            mediaDetails.put("size", Utilities.formatFileSize(file.length()));
            mediaDetails.put("name", file.getName());
            mediaDetails.put("path", file.getAbsolutePath());
            mediaDetails.put("modified", DateFormat.getDateInstance().format(new Date(file.lastModified())));


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
        return mediaInformation;
    }

    @Override
    public Map<String, String> getMediaDetails() {
        return mediaDetails;
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
