package hans.MediaItems;

import hans.MainController;
import hans.Utilities;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.bytedeco.javacv.FFmpegFrameGrabber;


import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.*;

public class WavItem implements MediaItem {


    File file;
    File subtitles;
    boolean subtitlesOn = false;
    Color backgroundColor = null;
    Duration duration;

    Image cover;
    Image placeholderCover;

    MainController mainController;

    boolean hasCover;

    Map<String, String> mediaInformation = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    Map<String, String> mediaDetails = new HashMap<>();

    public WavItem(File file, MainController mainController) {
        this.file = file;
        this.mainController = mainController;

        try {
            FFmpegFrameGrabber fFmpegFrameGrabber = new FFmpegFrameGrabber(file);


            fFmpegFrameGrabber.start();
            duration = Duration.seconds((int) (fFmpegFrameGrabber.getLengthInAudioFrames() / fFmpegFrameGrabber.getAudioFrameRate()));


            mediaInformation.putAll(fFmpegFrameGrabber.getMetadata());


            mediaDetails.put("size", Utilities.formatFileSize(file.length()));
            mediaDetails.put("name", file.getName());
            mediaDetails.put("path", file.getAbsolutePath());
            mediaDetails.put("modified", DateFormat.getDateInstance().format(new Date(file.lastModified())));
            mediaDetails.put("hasVideo", "false");
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


            fFmpegFrameGrabber.stop();
            fFmpegFrameGrabber.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        hasCover = cover != null;
        if(cover != null) backgroundColor = Utilities.findDominantColor(cover);

        placeholderCover = new Image(Objects.requireNonNull(Objects.requireNonNull(mainController.getClass().getResource("images/musicGraphic.png")).toExternalForm()));
    }

    public WavItem(WavItem wavItem, MainController mainController){
        this.mainController = mainController;

        this.file = wavItem.getFile();
        duration = wavItem.getDuration();
        cover = wavItem.getCover();
        placeholderCover = wavItem.getPlaceholderCover();
        subtitles = wavItem.getSubtitles();
        backgroundColor = wavItem.getCoverBackgroundColor();
        hasCover = wavItem.hasCover();
        mediaInformation = wavItem.getMediaInformation();
        mediaDetails = wavItem.getMediaDetails();
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
    public boolean setMediaInformation(Map<String, String> map, boolean updateFile) {
        return false;
    }

    @Override
    public Map<String, String> getMediaDetails() {
        return mediaDetails;
    }

    @Override
    public void setMediaDetails(Map<String, String> map) {
        mediaDetails = map;
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
    public boolean setCover(File imagePath, Image image, Color color, boolean updateFile) {
        cover = image;

        return false;
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
    public boolean hasVideo() {
        return false;
    }

    @Override
    public boolean hasCover() {
        return hasCover;
    }

    @Override
    public Image getPlaceholderCover() {
        return placeholderCover;
    }

    @Override
    public void setPlaceHolderCover(Image image) {
        placeholderCover = image;
    }

}