package hans.MediaItems;

import hans.MainController;
import hans.MediaItems.MediaItem;
import hans.Utilities;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.JavaFXFrameConverter;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.bytedeco.ffmpeg.global.avformat.AV_DISPOSITION_ATTACHED_PIC;
import static org.bytedeco.ffmpeg.global.avformat.AV_DISPOSITION_DEFAULT;

public class FlvItem implements MediaItem {


    File file;
    File subtitles;
    boolean subtitlesOn = false;
    Color backgroundColor = null;
    Duration duration;


    Image cover;
    Image placeholderCover;

    boolean hasVideo;
    MainController mainController;

    Map<String, String> mediaInformation = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    Map<String, String> mediaDetails = new HashMap<>();


    public FlvItem(File file, MainController mainController){
        this.file = file;
        this.mainController = mainController;

        try {
            FFmpegFrameGrabber fFmpegFrameGrabber = new FFmpegFrameGrabber(file);

            fFmpegFrameGrabber.setVideoStream(0);
            fFmpegFrameGrabber.setVideoDisposition(AV_DISPOSITION_DEFAULT);

            fFmpegFrameGrabber.start();

            hasVideo = fFmpegFrameGrabber.hasVideo();

            if(hasVideo) cover = Utilities.grabMiddleFrame(file);
            if(cover != null) backgroundColor = Utilities.findDominantColor(cover);

            if(hasVideo) duration = Duration.seconds(fFmpegFrameGrabber.getLengthInFrames() / fFmpegFrameGrabber.getFrameRate());
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
            if(fFmpegFrameGrabber.hasVideo()){
                mediaDetails.put("resolution", fFmpegFrameGrabber.getImageWidth() + "×" + fFmpegFrameGrabber.getImageHeight());
                if(fFmpegFrameGrabber.getVideoBitrate() == 0 && fFmpegFrameGrabber.getAudioBitrate() != 0){
                    mediaDetails.put("videoBitrate", Utilities.formatBitrate((long) ((file.length() * 8 - fFmpegFrameGrabber.getAudioBitrate() * duration.toSeconds())/duration.toSeconds())));
                }
            }

            fFmpegFrameGrabber.stop();
            fFmpegFrameGrabber.close();

            placeholderCover = new Image(Objects.requireNonNull(Objects.requireNonNull(mainController.getClass().getResource("images/videoGraphic.png")).toExternalForm()));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public FlvItem(FlvItem flvItem, MainController mainController){
        this.mainController = mainController;

        this.file = flvItem.getFile();
        duration = flvItem.getDuration();
        cover = flvItem.getCover();
        placeholderCover = flvItem.getPlaceholderCover();
        subtitles = flvItem.getSubtitles();
        backgroundColor = flvItem.getCoverBackgroundColor();
        mediaInformation = flvItem.getMediaInformation();
        mediaDetails = flvItem.getMediaDetails();
        hasVideo = flvItem.hasVideo();
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

        if(updateFile){
            ArrayList<String> arguments = new ArrayList<>();
            String ffmpeg = Loader.load(org.bytedeco.ffmpeg.ffmpeg.class);

            arguments.add(ffmpeg);
            arguments.add("-i");
            arguments.add(file.getAbsolutePath());

            arguments.add("-map");
            arguments.add("0");

            arguments.add("-map_metadata:g");
            arguments.add("-1");

            if(!map.isEmpty()){
                for(Map.Entry<String, String> entry : map.entrySet()){
                    arguments.add("-metadata");
                    arguments.add(entry.getKey() + "=" + entry.getValue());
                }
            }
            arguments.add("-c");
            arguments.add("copy");

            String outputPath = file.getParent() + "/" + new SimpleDateFormat("dd-MM-yyyy HH-mm-ss").format(new Date()) + ".flv";

            arguments.add(outputPath);
            ProcessBuilder pb = new ProcessBuilder(arguments);

            try {
                pb.inheritIO().start().waitFor();

                //overwrite curr file with new file, if its playing, stop it, rewrite and then start playing again and seek to same time
                if(mainController.getMenuController().activeItem != null && mainController.getMenuController().activeItem.getMediaItem().getFile().getAbsolutePath().equals(file.getAbsolutePath())){
                    mainController.getMediaInterface().resetMediaPlayer(true);

                    boolean deleteSuccess = file.delete();
                    if(deleteSuccess){
                        File tempFile = new File(outputPath);
                        boolean renameSuccess = tempFile.renameTo(file);
                        if(!renameSuccess){
                            throw new IOException("Failed to rename new file");
                        }
                    }
                    else throw new IOException("Failed to delete old file");


                    mainController.getMediaInterface().createMedia(mainController.getMenuController().activeItem);
                }
                else {
                    boolean deleteSuccess = file.delete();
                    if(deleteSuccess){
                        File tempFile = new File(outputPath);
                        boolean renameSuccess = tempFile.renameTo(file);
                        if(!renameSuccess){
                            throw new IOException("Failed to rename new file");
                        }
                    }
                    else throw new IOException("Failed to delete old file");
                }

                mediaDetails.put("size", Utilities.formatFileSize(file.length()));
                mediaDetails.put("modified", DateFormat.getDateInstance().format(new Date(file.lastModified())));

                mediaInformation = map;
                return true;



            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        else {

            mediaInformation = map;
            return true;
        }
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
    public boolean setCover(File imagePath, Image image, Color color, boolean updateFile) {
        return false;
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
    public boolean hasVideo() {
        return hasVideo;
    }

    @Override
    public boolean hasCover() {
        return false;
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