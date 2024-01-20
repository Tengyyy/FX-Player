package tengy.mediaItems;

import com.github.kokorin.jaffree.LogLevel;
import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.ffprobe.FFprobe;
import com.github.kokorin.jaffree.ffprobe.FFprobeResult;
import com.github.kokorin.jaffree.ffprobe.Stream;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import tengy.MainController;
import tengy.SVG;
import tengy.Utilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;



public class MediaItem {

    Color backgroundColor = null;

    Duration duration = null;

    boolean hasVideo = false;
    boolean hasAudio = false;
    boolean hasCover = false;

    MainController mainController;


    Map<String, String> mediaInformation = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    Image cover = null;

    public SVG icon = null;

    FFprobeResult probeResult;

    File file;

    public List<Chapter> chapters = new ArrayList<>();

    public List<Stream> videoStreams = new ArrayList<>();
    public List<Stream> audioStreams = new ArrayList<>();
    public List<Stream> subtitleStreams = new ArrayList<>();
    public List<Stream> attachmentStreams = new ArrayList<>();

    public Stream defaultVideoStream = null;
    public Stream defaultAudioStream = null;
    public Stream defaultSubtitleStream = null;

    //subtitle extraction//
    public String subtitlesGenerationTime = "";
    public BooleanProperty subtitlesExtractionInProgress = new SimpleBooleanProperty(false);
    ///////////////////////


    //Media edit variables//
    public BooleanProperty editActive = new SimpleBooleanProperty(false);
    public List<Chapter> newChapters = null;
    public Map<String, String> newMetadata = null;
    public boolean coverRemoved = false;
    public Image newCover = null;
    public File newCoverFile = null;
    public Color newColor = null;
    ///////////////////////////

    // dummy mediaItem to populate ongoing metadata proccesses list when creating new output
    public MediaItem(boolean isDummy){}

    public MediaItem(File file, MainController mainController) {
        this.file = file;
        this.mainController = mainController;


        probeResult = FFprobe.atPath(Paths.get(MediaUtilities.FFPROBE_PATH))
                .setShowChapters(true)
                .setShowStreams(true)
                .setShowFormat(true)
                .setShowData(true)
                .setInput(file.getAbsolutePath())
                .setLogLevel(LogLevel.INFO)
                .execute();

        for(com.github.kokorin.jaffree.ffprobe.Chapter chapter : probeResult.getChapters()){
            String title = chapter.getTag("title");
            Duration duration = Duration.ZERO;
            Double startTime = chapter.getStartTime();
            if(startTime != null) duration = Duration.seconds(startTime);
            if(title == null) title = "";

            chapters.add(new Chapter(title, duration));
        }


        for(Stream stream : probeResult.getStreams()){

            if(stream.getCodecType() == StreamType.VIDEO){
                videoStreams.add(stream);

                if(stream.getDisposition().getDefault() == 1 && stream.getDisposition().getAttachedPic() == 0) defaultVideoStream = stream;
            }
            else if(stream.getCodecType() == StreamType.AUDIO){
                audioStreams.add(stream);

                if(stream.getDisposition().getDefault() == 1) defaultAudioStream = stream;
            }
            else if(stream.getCodecType() == StreamType.ATTACHMENT){
                attachmentStreams.add(stream);
            }
            else if(stream.getCodecType() == StreamType.SUBTITLE){

                subtitleStreams.add(stream);

                if(stream.getDisposition().getDefault() == 1) defaultSubtitleStream = stream;
            }
        }

        if(defaultVideoStream == null && !videoStreams.isEmpty()){
            for(Stream stream: videoStreams){
                if(stream.getDisposition().getAttachedPic() == 0){
                    defaultVideoStream = stream;
                    break;
                }
            }
        }

        if(defaultAudioStream == null && !audioStreams.isEmpty()){
            defaultAudioStream = audioStreams.get(0);
        }

        if(defaultSubtitleStream == null && !subtitleStreams.isEmpty()){
            defaultSubtitleStream = subtitleStreams.get(0);
        }

        hasVideo = defaultVideoStream != null;
        hasAudio = defaultAudioStream != null;

        if(defaultVideoStream != null){
            Long durationLong = defaultVideoStream.getDuration(TimeUnit.SECONDS);
            if(durationLong != null)
                duration = Duration.seconds(durationLong);
        }

        if(defaultAudioStream != null){
            Long durationLong = defaultAudioStream.getDuration(TimeUnit.SECONDS);
            if(durationLong != null){
                if(duration == null || durationLong > duration.toSeconds())
                    duration = Duration.seconds(durationLong);
            }
        }

        if(duration == null){
            Float durationFloat = probeResult.getFormat().getDuration();
            if(durationFloat != null) this.duration = Duration.seconds(durationFloat);
        }


        String extension = Utilities.getFileExtension(this.file);

        if(extension.equals("mp4") || extension.equals("mov")){
            if(mediaInformation.containsKey("media_type") && mediaInformation.get("media_type").equals("6")) icon = SVG.MUSIC;
            else if(mediaInformation.containsKey("media_type") && mediaInformation.get("media_type").equals("21")) icon = SVG.PODCAST;
            else icon = SVG.VIDEO;
        }
        else if(extension.equals("mkv") || extension.equals("flv") || extension.equals("avi")) icon = SVG.VIDEO;
        else icon = SVG.MUSIC;

    }

    public Color getCoverBackgroundColor() {
        return backgroundColor;
    }

    public boolean hasVideo() {
        return hasVideo;
    }

    public boolean hasAudio(){
        return hasAudio;
    }

    public boolean hasCover() {
        return hasCover;
    }


    public FFprobeResult getProbeResult() {
        return probeResult;
    }


    public Map<String, String> getMediaInformation() {
        return mediaInformation;
    }

    public boolean updateMetadata() {

        Platform.runLater(() -> editActive.set(true));
        mainController.ongoingMediaEditProcesses.add(this);

        boolean metadataEditSuccess = false;

        boolean success = MediaUtilities.updateMetadata(this);

        if(success){
            //overwrite curr file with new file

            if(newCover != null){
                cover = newCover;
                hasCover = true;
                backgroundColor = newColor;
            }
            else if(coverRemoved){
                hasCover = false;
                cover = null;
                if(hasVideo){
                    int videoStreamIndex = probeResult.getStreams().indexOf(defaultVideoStream);
                    if(videoStreamIndex != -1){
                        Long durationLong = defaultVideoStream.getDuration(TimeUnit.MILLISECONDS);
                        if(durationLong == null && this.duration != null) durationLong = (long) this.duration.toMillis() ;
                        if(durationLong != null) cover = MediaUtilities.getVideoFrame(file, videoStreamIndex, durationLong/2);
                    }
                    if(cover != null) backgroundColor = MediaUtilities.findDominantColor(cover);
                    else backgroundColor = null;
                }
                else backgroundColor = null;
            }

            String extension = Utilities.getFileExtension(file);
            if(extension.equals("mp4") || extension.equals("mov")){
                switch (newMetadata.getOrDefault("media_type", null)) {
                    case "6" -> icon = SVG.MUSIC;
                    case "21" -> icon = SVG.PODCAST;
                    default -> icon = SVG.VIDEO;
                }
            }

            mediaInformation = newMetadata;
            metadataEditSuccess = true;
        }

        resetEditVariables();

        mainController.ongoingMediaEditProcesses.remove(this);

        return metadataEditSuccess;
    }

    public boolean updateMetadata(File output){
        Platform.runLater(() -> editActive.set(true));
        mainController.ongoingMediaEditProcesses.add(this);

        boolean success = MediaUtilities.createFileWithUpdatedMetadata(this, output);

        resetEditVariables();

        mainController.ongoingMediaEditProcesses.remove(this);

        return success;
    }

    public boolean updateChapters() throws IOException {

        Platform.runLater(() -> editActive.set(true));
        mainController.ongoingMediaEditProcesses.add(this);

        boolean success = MediaUtilities.updateChapters(this);

        if(success){
            chapters = newChapters;
        }

        resetEditVariables();

        mainController.ongoingMediaEditProcesses.remove(this);

        return success;
    }


    public boolean updateChapters(File output) throws IOException {

        Platform.runLater(() -> editActive.set(true));
        mainController.ongoingMediaEditProcesses.add(this);

        boolean success = MediaUtilities.applyChapters(this, output);

        resetEditVariables();

        mainController.ongoingMediaEditProcesses.remove(this);

        return success;
    }

    public File getFile() {
        return this.file;
    }

    public Duration getDuration() {
        if(duration != null) return this.duration;
        else return null;
    }

    public Image getCover(){
        return this.cover;
    }

    public void resetEditVariables(){
        Platform.runLater(() -> editActive.set(false));
        newMetadata = null;
        coverRemoved = false;
        newCover = null;
        newColor = null;
        newCoverFile = null;
        newChapters = null;
    }
}



