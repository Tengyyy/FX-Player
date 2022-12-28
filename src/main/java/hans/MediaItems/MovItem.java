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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.bytedeco.ffmpeg.global.avformat.AV_DISPOSITION_ATTACHED_PIC;
import static org.bytedeco.ffmpeg.global.avformat.AV_DISPOSITION_DEFAULT;

public class MovItem implements MediaItem {


    File file;
    Color backgroundColor = null;
    Duration duration;


    Image cover;
    Image placeholderCover;

    boolean hasVideo;
    boolean hasCover;

    File newCover = null;
    Color newColor = null;
    boolean coverRemoved = false;

    MainController mainController;

    Map<String, String> mediaInformation = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    Map<String, String> mediaDetails = new HashMap<>();

    Map<String, ArrayList<Map<String, String>>> log;

    public MovItem(File file, MainController mainController){
        this.file = file;
        this.mainController = mainController;

        log = Utilities.parseLog(Utilities.getLog(file.getAbsolutePath()));

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

            if(mediaInformation.containsKey("media_type") && mediaInformation.get("media_type").equals("6")) placeholderCover = new Image(Objects.requireNonNull(Objects.requireNonNull(mainController.getClass().getResource("images/music.png")).toExternalForm()));
            else if(mediaInformation.containsKey("media_type") && mediaInformation.get("media_type").equals("21")) placeholderCover = new Image(Objects.requireNonNull(Objects.requireNonNull(mainController.getClass().getResource("images/podcast.png")).toExternalForm()));
            else placeholderCover = new Image(Objects.requireNonNull(Objects.requireNonNull(mainController.getClass().getResource("images/video.png")).toExternalForm()));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public MovItem(MovItem movItem, MainController mainController){
        this.mainController = mainController;

        this.file = movItem.getFile();
        duration = movItem.getDuration();
        cover = movItem.getCover();
        placeholderCover = movItem.getPlaceholderCover();
        backgroundColor = movItem.getCoverBackgroundColor();
        hasCover = movItem.hasCover();
        mediaInformation = movItem.getMediaInformation();
        mediaDetails = movItem.getMediaDetails();
        hasVideo = movItem.hasVideo();
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
            if(newCover != null || coverRemoved){
                if(newCover != null){
                    arguments.add("-i");
                    arguments.add(newCover.getAbsolutePath());
                }

                arguments.add("-map");
                arguments.add("0:V?");
                arguments.add("-map");
                arguments.add("0:a?");
                arguments.add("-map");
                arguments.add("0:s?");

                if(newCover != null){
                    arguments.add("-map");
                    arguments.add("1");
                }
            }
            else {
                arguments.add("-map");
                arguments.add("0");
            }
            arguments.add("-map_metadata:g");
            arguments.add("-1");

            if(!map.isEmpty()){
                //arguments.add("-movflags");
                //arguments.add("use_metadata_keys");
                for(Map.Entry<String, String> entry : map.entrySet()){
                    arguments.add("-metadata");
                    arguments.add(entry.getKey() + "=" + entry.getValue());
                }
            }
            arguments.add("-c");
            arguments.add("copy");
            arguments.add("-movflags");
            arguments.add("faststart");
            if(newCover != null){
                arguments.add("-c:v:1");
                arguments.add("png");
                arguments.add("-disposition:v:1");
                arguments.add("attached_pic");
            }

            // converts to mp4 and back to mov, have to test how reliable this actually is
            String outputPath = file.getParent() + "/" + new SimpleDateFormat("dd-MM-yyyy HH-mm-ss").format(new Date()) + ".mp4";

            arguments.add(outputPath);

            try {
                Process process = new ProcessBuilder(arguments).redirectErrorStream(true).start();
                StringBuilder strBuild = new StringBuilder();

                BufferedReader processOutputReader = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.defaultCharset()));
                String line;
                while ((line = processOutputReader.readLine()) != null) {
                    strBuild.append(line).append(System.lineSeparator());
                }
                process.waitFor();
                String output = strBuild.toString().trim();
                System.out.println(output);
                if(output.endsWith("Invalid argument") || output.endsWith("Conversion failed!") || output.endsWith("Error splitting the argument list: Option not found")){
                    //TODO: delete the empty filed that was created
                    System.out.println("Metadata update failed");
                    return false;
                }

                //overwrite curr file with new file

                boolean deleteSuccess = file.delete();
                if(deleteSuccess){
                    File tempFile = new File(outputPath);
                    boolean renameSuccess = tempFile.renameTo(file);
                    if(!renameSuccess){
                        throw new IOException("Failed to rename new file");
                    }
                }
                else throw new IOException("Failed to delete old file");



                mediaDetails.put("size", Utilities.formatFileSize(file.length()));
                mediaDetails.put("modified", DateFormat.getDateInstance().format(new Date(file.lastModified())));

                if(newCover != null){
                    cover = new Image(newCover.getAbsolutePath());
                    hasCover = true;
                    backgroundColor = newColor;
                }
                else if(coverRemoved){
                    hasCover = false;
                    cover = Utilities.grabMiddleFrame(file);
                    if(cover != null) backgroundColor = Utilities.findDominantColor(cover);
                    else backgroundColor = null;
                }

                switch (map.getOrDefault("media_type", null)) {
                    case "6" -> placeholderCover = new Image(Objects.requireNonNull(Objects.requireNonNull(mainController.getClass().getResource("images/musicGraphic.png")).toExternalForm()));
                    case "21" -> placeholderCover = new Image(Objects.requireNonNull(Objects.requireNonNull(mainController.getClass().getResource("images/podcastGraphic.png")).toExternalForm()));
                    default -> placeholderCover = new Image(Objects.requireNonNull(Objects.requireNonNull(mainController.getClass().getResource("images/videoGraphic.png")).toExternalForm()));
                }


                mediaInformation = map;
                return true;



            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
                return false;
            }
            finally {
                newCover = null;
                newColor = null;
                coverRemoved = false;
            }
        }
        else {
            newCover = null;
            newColor = null;
            coverRemoved = false;
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
    public Duration getDuration() {
        return duration;
    }

    @Override
    public Image getCover() {
        return cover;
    }

    @Override
    public boolean setCover(File imagePath, Image image, Color color, boolean updateFile) {

        if(updateFile){
            newCover = imagePath;
            newColor = color;
            coverRemoved = imagePath == null;
        }
        else {
            hasCover = image != null;
            cover = image;
            backgroundColor = color;
        }

        return true;
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

    @Override
    public Map<String, ArrayList<Map<String, String>>> getLog() {
        return log;
    }
}
