package hans.MediaItems;

import com.github.kokorin.jaffree.LogLevel;
import com.github.kokorin.jaffree.Rational;
import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.ffmpeg.*;
import com.github.kokorin.jaffree.ffmpeg.UrlInput;
import com.github.kokorin.jaffree.ffprobe.*;
import com.github.kokorin.jaffree.ffprobe.Stream;
import hans.MainController;
import hans.Utilities;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javafx.util.Pair;

import java.io.*;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;



public class Mp4Item implements MediaItem {

    File file;

    Color backgroundColor = null;

    Duration duration = null;

    boolean hasVideo = false;
    boolean hasAudio = false;
    boolean hasCover = false;

    MainController mainController;


    Map<String, String> mediaInformation = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    Map<String, String> mediaDetails = new HashMap<>();

    Image cover = null;
    Image placeholderCover = null;

    File newCover = null;
    Color newColor = null;
    boolean coverRemoved = false;


    int width = 0;
    int height = 0;
    int audioChannels = 0;


    Map<String, ArrayList<Map<String, String>>> log;

    FFprobeResult probeResult = null;

    int numberOfNonPictureVideoStreams = 0;

    public Mp4Item(File file, MainController mainController) {
        this.file = file;
        this.mainController = mainController;

        log = Utilities.parseLog(Utilities.getLog(file.getAbsolutePath()));

        probeResult = FFprobe.atPath()
                .setShowStreams(true)
                .setShowFormat(true)
                .setInput(file.getAbsolutePath())
                .setLogLevel(LogLevel.VERBOSE)
                .execute();

        Pair<Boolean, Image> pair = MediaUtilities.getCover(probeResult, file);
        this.cover = pair.getValue();
        this.hasCover = pair.getKey();

        Stream videoStream = null;
        Stream audioStream = null;

        int firstVideoStreamIndex = -1;
        int firstAudioStreamIndex = -1;

        for(Stream stream : probeResult.getStreams()){
            if(stream.getCodecType() == StreamType.VIDEO && stream.getDisposition().getDefault() == 1 && stream.getDisposition().getAttachedPic() == 0){
                videoStream = stream;
                numberOfNonPictureVideoStreams++;
            }
            else if(stream.getCodecType() == StreamType.AUDIO && stream.getDisposition().getDefault() == 1){
                audioStream = stream;
            }
            else if(stream.getCodecType() == StreamType.VIDEO && stream.getDisposition().getAttachedPic() == 0){
                if(firstVideoStreamIndex == -1) firstVideoStreamIndex  = stream.getIndex();
                numberOfNonPictureVideoStreams++;
            }
            else if(stream.getCodecType() == StreamType.AUDIO){
                if(firstAudioStreamIndex == -1) firstAudioStreamIndex  = stream.getIndex();
            }
        }

        if(videoStream == null && firstVideoStreamIndex != -1) videoStream = probeResult.getStreams().get(firstVideoStreamIndex);
        if(audioStream == null && firstAudioStreamIndex != -1) audioStream = probeResult.getStreams().get(firstAudioStreamIndex);

        hasVideo = videoStream != null;
        hasAudio = audioStream != null;

        if(cover != null) backgroundColor = Utilities.findDominantColor(cover);

        if(videoStream != null){
            this.width = videoStream.getWidth();
            this.height = videoStream.getHeight();

            duration = Duration.seconds(videoStream.getDuration(TimeUnit.SECONDS));
            Rational rational = videoStream.getAvgFrameRate();
            if(rational != null) mediaDetails.put("frameRate", rational.intValue() + " fps");
            Long bitrate = videoStream.getBitRate();
            if(bitrate != null) mediaDetails.put("videoBitrate", Utilities.formatBitrate(bitrate));
            if(videoStream.getWidth() != null && videoStream.getHeight() != null) mediaDetails.put("resolution", this.width + "x" + this.height);
            String videoCodec = videoStream.getCodecName();
            if(videoCodec != null) mediaDetails.put("videoCodec", videoCodec);
            if(probeResult.getFormat() != null) mediaDetails.put("format", probeResult.getFormat().getFormatName());
        }

        if(audioStream != null){
            if(duration == null) duration = Duration.seconds(audioStream.getDuration(TimeUnit.SECONDS));
            Integer channels = audioStream.getChannels();
            if(channels != null){
                audioChannels = channels;
                String channelLayout = audioStream.getChannelLayout();
                if(channelLayout != null) mediaDetails.put("audioChannels", audioChannels + " (" + channelLayout + ")");
                else mediaDetails.put("audioChannels", String.valueOf(audioChannels));
            }


            String audioCodec = audioStream.getCodecName();
            if(audioCodec != null) mediaDetails.put("audioCodec", audioCodec);

            Long bitrate = audioStream.getBitRate();
            if(bitrate != null) mediaDetails.put("audioBitrate", Utilities.formatBitrate(bitrate));

            Integer sampleRate = audioStream.getSampleRate();
            if(sampleRate != null) mediaDetails.put("sampleRate", NumberFormat.getInstance().format(sampleRate) + " Hz");
        }

        mediaDetails.put("size", Utilities.formatFileSize(file.length()));
        mediaDetails.put("name", file.getName());
        mediaDetails.put("path", file.getAbsolutePath());
        mediaDetails.put("modified", DateFormat.getDateInstance().format(new Date(file.lastModified())));
        mediaDetails.put("hasVideo", String.valueOf(videoStream != null));
        mediaDetails.put("hasAudio", String.valueOf(audioStream != null));
        mediaDetails.put("duration", Utilities.getTime(duration));

        //mediaInformation.putAll(fFmpegFrameGrabber.getMetadata());

        if(mediaInformation.containsKey("media_type") && mediaInformation.get("media_type").equals("6")) placeholderCover = new Image(Objects.requireNonNull(Objects.requireNonNull(mainController.getClass().getResource("images/music.png")).toExternalForm()));
        else if(mediaInformation.containsKey("media_type") && mediaInformation.get("media_type").equals("21")) placeholderCover = new Image(Objects.requireNonNull(Objects.requireNonNull(mainController.getClass().getResource("images/podcast.png")).toExternalForm()));
        else placeholderCover = new Image(Objects.requireNonNull(Objects.requireNonNull(mainController.getClass().getResource("images/video.png")).toExternalForm()));
    }

    public Mp4Item(Mp4Item mp4Item, MainController mainController){
        this.mainController = mainController;

        this.file = mp4Item.getFile();
        duration = mp4Item.getDuration();
        cover = mp4Item.getCover();
        placeholderCover = mp4Item.getPlaceholderCover();
        backgroundColor = mp4Item.getCoverBackgroundColor();
        hasCover = mp4Item.hasCover();
        mediaInformation = mp4Item.getMediaInformation();
        mediaDetails = mp4Item.getMediaDetails();
        hasVideo = mp4Item.hasVideo();
    }


    @Override
    public Color getCoverBackgroundColor() {
        return backgroundColor;
    }

    @Override
    public boolean hasVideo() {
        return hasVideo;
    }

    public boolean hasAudio(){
        return hasAudio;
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
            FFmpeg fFmpeg = FFmpeg.atPath()
                    .addInput(UrlInput.fromUrl(file.getAbsolutePath()));

            if(newCover != null || coverRemoved){
                if(newCover != null){
                    fFmpeg.addInput(UrlInput.fromUrl(newCover.getAbsolutePath()));
                }
                fFmpeg.addArguments("-map", "0:V?")
                        .addArguments("-map", "0:a?")
                        .addArguments("-map", "0:s?");

                if(newCover != null){
                    fFmpeg.addArguments("-map", "1");
                }
            }
            else {
                fFmpeg.addArguments("-map", "0");
            }

            fFmpeg.addArguments("-map_metadata:g", "-1");

            if(!map.isEmpty()){
                for(Map.Entry<String, String> entry : map.entrySet()){
                    fFmpeg.addArguments("-metadata", entry.getKey() + "=" + entry.getValue());
                }
            }

            fFmpeg.addArguments("-c", "copy")
                            .addArguments("-movflags", "faststart");


            if(newCover != null){
                fFmpeg.addArguments("-c:v:" + numberOfNonPictureVideoStreams, "png");
                fFmpeg.addArguments("-disposition:v:" + numberOfNonPictureVideoStreams, "attached_pic");
            }

            if(this.duration != null){
                fFmpeg.setProgressListener(progress -> {
                    double percentage = 100. * progress.getTimeMillis() / duration.toMillis();
                    System.out.println("Progress: " + percentage + "%");
                });
            }

            String outputPath = file.getParent() + "/" + new SimpleDateFormat("dd-MM-yyyy HH-mm-ss").format(new Date()) + ".mp4";

            fFmpeg.addOutput(UrlOutput.toUrl(outputPath));
            FFmpegResult fFmpegResult = fFmpeg.execute();
            System.out.println(fFmpegResult.getVideoSize());


            try {


                //overwrite curr file with new file

                /*boolean deleteSuccess = file.delete();
                if(deleteSuccess){
                    File tempFile = new File(outputPath);
                    boolean renameSuccess = tempFile.renameTo(file);
                    if(!renameSuccess){
                        throw new IOException("Failed to rename new file");
                    }
                }
                else throw new IOException("Failed to delete old file");*/


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
                    case "6" -> placeholderCover = new Image(Objects.requireNonNull(Objects.requireNonNull(mainController.getClass().getResource("images/music.png")).toExternalForm()));
                    case "21" -> placeholderCover = new Image(Objects.requireNonNull(Objects.requireNonNull(mainController.getClass().getResource("images/podcast.png")).toExternalForm()));
                    default -> placeholderCover = new Image(Objects.requireNonNull(Objects.requireNonNull(mainController.getClass().getResource("images/video.png")).toExternalForm()));
                }


                mediaInformation = map;
                return true;



            } finally {
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
        return this.file;
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
}



