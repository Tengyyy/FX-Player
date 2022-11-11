package hans.MediaItems;

import hans.MainController;
import hans.Utilities;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.bytedeco.javacv.*;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.*;

import static org.bytedeco.ffmpeg.global.avformat.AV_DISPOSITION_ATTACHED_PIC;
import static org.bytedeco.ffmpeg.global.avformat.AV_DISPOSITION_DEFAULT;


public class Mp4Item implements MediaItem {

    File file;

    File subtitles;
    boolean subtitlesOn;

    Color backgroundColor = null;


    Duration duration;

    boolean hasVideo;
    boolean hasCover;

    MainController mainController;


    Map<String, String> mediaInformation = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    Map<String, String> mediaDetails = new HashMap<>();

    Image cover;
    Image placeholderCover;


    public Mp4Item(File file, MainController mainController) {
        this.file = file;
        this.mainController = mainController;

        try {
            FFmpegFrameGrabber fFmpegFrameGrabber = new FFmpegFrameGrabber(file);

            fFmpegFrameGrabber.setVideoDisposition(AV_DISPOSITION_ATTACHED_PIC);
            fFmpegFrameGrabber.start();

            if(fFmpegFrameGrabber.getVideoStream() != 0){
                Frame frame = fFmpegFrameGrabber.grabImage();
                JavaFXFrameConverter javaFXFrameConverter = new JavaFXFrameConverter();
                if(frame != null) cover = javaFXFrameConverter.convert(frame);
            }

            fFmpegFrameGrabber.stop();

            fFmpegFrameGrabber.setVideoStream(0);
            fFmpegFrameGrabber.setVideoDisposition(AV_DISPOSITION_DEFAULT);

            fFmpegFrameGrabber.start();

            hasVideo = fFmpegFrameGrabber.hasVideo();

            hasCover = cover != null;
            if(!hasCover && hasVideo) cover = Utilities.grabMiddleFrame(file);
            if(cover != null) backgroundColor = Utilities.findDominantColor(cover);


            if(fFmpegFrameGrabber.hasVideo()) duration = Duration.seconds(fFmpegFrameGrabber.getLengthInFrames() / fFmpegFrameGrabber.getFrameRate());
            else duration = Duration.seconds(fFmpegFrameGrabber.getLengthInAudioFrames() / fFmpegFrameGrabber.getAudioFrameRate());


            mediaInformation.putAll(fFmpegFrameGrabber.getMetadata());

            mediaDetails.put("size", Utilities.formatFileSize(file.length()));
            mediaDetails.put("name", file.getName());
            mediaDetails.put("path", file.getAbsolutePath());
            mediaDetails.put("modified", DateFormat.getDateInstance().format(new Date(file.lastModified())));
            mediaDetails.put("hasVideo", String.valueOf(fFmpegFrameGrabber.hasVideo()));
            mediaDetails.put("hasAudio", String.valueOf(fFmpegFrameGrabber.hasAudio()));
            if(fFmpegFrameGrabber.hasAudio()){
                if(fFmpegFrameGrabber.getAudioChannels() == 2) mediaDetails.put("audioChannels", fFmpegFrameGrabber.getAudioChannels() + " (stereo)");
                else if(fFmpegFrameGrabber.getAudioChannels() == 6) mediaDetails.put("audioChannels", fFmpegFrameGrabber.getAudioChannels() + " (5.1 surround sound)");
                else if(fFmpegFrameGrabber.getAudioChannels() == 8) mediaDetails.put("audioChannels", fFmpegFrameGrabber.getAudioChannels() + " (7.1 surround sound)");
                else mediaDetails.put("audioChannels", String.valueOf(fFmpegFrameGrabber.getAudioChannels()));
            }
            if(fFmpegFrameGrabber.getAudioCodecName() != null) mediaDetails.put("audioCodec", fFmpegFrameGrabber.getAudioCodecName());
            if(fFmpegFrameGrabber.hasAudio() && fFmpegFrameGrabber.getAudioBitrate() != 0) mediaDetails.put("audioBitrate", Utilities.formatBitrate(fFmpegFrameGrabber.getAudioBitrate()));
            mediaDetails.put("duration", Utilities.getTime(duration));
            mediaDetails.put("format", fFmpegFrameGrabber.getFormat());
            if(fFmpegFrameGrabber.hasAudio()) mediaDetails.put("sampleRate", NumberFormat.getInstance().format(fFmpegFrameGrabber.getSampleRate()) + " Hz");
            if(fFmpegFrameGrabber.getVideoCodecName() != null) mediaDetails.put("videoCodec", fFmpegFrameGrabber.getVideoCodecName());
            if(fFmpegFrameGrabber.hasVideo() && fFmpegFrameGrabber.getVideoBitrate() != 0) mediaDetails.put("videoBitrate", Utilities.formatBitrate(fFmpegFrameGrabber.getVideoBitrate()));
            mediaDetails.put("frameRate", Math.round(fFmpegFrameGrabber.getFrameRate()) + " fps");
            if(fFmpegFrameGrabber.hasVideo()) mediaDetails.put("resolution", fFmpegFrameGrabber.getImageWidth() + "×" + fFmpegFrameGrabber.getImageHeight());

            fFmpegFrameGrabber.stop();
            fFmpegFrameGrabber.close();

            if(mediaInformation.containsKey("media_type") && mediaInformation.get("media_type").equals("6")) placeholderCover = new Image(Objects.requireNonNull(Objects.requireNonNull(mainController.getClass().getResource("images/musicGraphic.png")).toExternalForm()));
            else if(mediaInformation.containsKey("media_type") && mediaInformation.get("media_type").equals("21")) placeholderCover = new Image(Objects.requireNonNull(Objects.requireNonNull(mainController.getClass().getResource("images/podcastGraphic.png")).toExternalForm()));
            else placeholderCover = new Image(Objects.requireNonNull(Objects.requireNonNull(mainController.getClass().getResource("images/videoGraphic.png")).toExternalForm()));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }



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

    @Override
    public boolean hasVideo() {
        return hasVideo;
    }

    @Override
    public boolean hasCover() {
        return hasCover;
    }

    @Override
    public void setHasCover(boolean value) {
        hasCover = value;
    }

    @Override
    public Image getPlaceholderCover() {
        return placeholderCover;
    }

    @Override
    public void setPlaceHolderCover(Image image) {
        placeholderCover = image;
    }


    @Override
    public float getFrameDuration() {
        return 0;
    }

    @Override
    public Map<String, String> getMediaInformation() {
        return mediaInformation;
    }

    @Override
    public void setMediaInformation(Map<String, String> map, boolean updateFile) {

    }

    @Override
    public Map<String, String> getMediaDetails() {
        return mediaDetails;
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
        if(duration != null) return this.duration;
        else return null;
    }

    @Override
    public Image getCover(){
        return this.cover;
    }

    @Override
    public void setCover(File imagePath, Image image, boolean updateFile) {
        cover = image;
    }
}



