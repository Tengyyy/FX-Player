package hans.MediaItems;

import com.github.kokorin.jaffree.LogLevel;
import com.github.kokorin.jaffree.Rational;
import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.ffprobe.FFprobe;
import com.github.kokorin.jaffree.ffprobe.FFprobeResult;
import com.github.kokorin.jaffree.ffprobe.Stream;
import hans.MainController;
import hans.Utilities;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javafx.util.Pair;

import java.io.File;
import java.text.DateFormat;
import java.text.NumberFormat;
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
    Map<String, String> mediaDetails = new HashMap<>();

    Image cover = null;
    Image placeholderCover = null;

    File newCover = null;
    Color newColor = null;
    boolean coverRemoved = false;


    int width = 0;
    int height = 0;
    int audioChannels = 0;

    FFprobeResult probeResult = null;

    int numberOfNonPictureVideoStreams = 0;
    int numberOfAttachmentStreams = 0;
    public int numberOfSubtitleStreams = 0;
    public ArrayList<String> subtitleStreamLanguages = new ArrayList<>();
    public int defaultSubtitleStream = -1;

    File file;

    Stream videoStream = null;
    Stream audioStream = null;

    public MediaItem(File file, MainController mainController) {
        this.file = file;
        this.mainController = mainController;

        probeResult = FFprobe.atPath()
                .setShowStreams(true)
                .setShowFormat(true)
                .setShowData(true)
                .setInput(file.getAbsolutePath())
                .setLogLevel(LogLevel.INFO)
                .execute();


        Pair<Boolean, Image> pair = MediaUtilities.getCover(probeResult, file);
        this.cover = pair.getValue();
        this.hasCover = pair.getKey();

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
            else if(stream.getCodecType() == StreamType.ATTACHMENT){
                numberOfAttachmentStreams++;
            }
            else if(stream.getCodecType() == StreamType.SUBTITLE){
                if(stream.getDisposition().getDefault() == 1) defaultSubtitleStream = numberOfSubtitleStreams;
                numberOfSubtitleStreams++;
                String languageCode = stream.getTag("language");
                if(languageCode == null || languageCode.equals("und")) subtitleStreamLanguages.add("Undefined");
                else {
                    Locale locale = Locale.forLanguageTag(languageCode.toUpperCase(Locale.ROOT));
                    subtitleStreamLanguages.add(locale.getDisplayLanguage());
                }
            }
        }

        if(videoStream == null && firstVideoStreamIndex != -1) videoStream = probeResult.getStreams().get(firstVideoStreamIndex);
        if(audioStream == null && firstAudioStreamIndex != -1) audioStream = probeResult.getStreams().get(firstAudioStreamIndex);

        hasVideo = videoStream != null;
        hasAudio = audioStream != null;

        if(cover != null) backgroundColor = MediaUtilities.findDominantColor(cover);

        if(videoStream != null){
            this.width = videoStream.getWidth();
            this.height = videoStream.getHeight();

            Long durationLong = videoStream.getDuration(TimeUnit.SECONDS);
            if(durationLong != null){
                duration = Duration.seconds(durationLong);
                mediaDetails.put("videoDuration", Utilities.getTime(duration));
            }

            Rational rational = videoStream.getAvgFrameRate();
            if(rational != null) mediaDetails.put("frameRate", rational.intValue() + " fps");
            Long bitrate = videoStream.getBitRate();
            if(bitrate != null) mediaDetails.put("videoBitrate", Utilities.formatBitrate(bitrate));
            if(videoStream.getWidth() != null && videoStream.getHeight() != null) mediaDetails.put("resolution", this.width + "x" + this.height);
            String videoCodec = videoStream.getCodecName();
            if(videoCodec != null) mediaDetails.put("videoCodec", videoCodec);
            String formatName = probeResult.getFormat().getFormatName();
            if(formatName != null) mediaDetails.put("format", formatName);
        }

        if(audioStream != null){
            Long durationLong = audioStream.getDuration(TimeUnit.SECONDS);
            if(durationLong != null){
                Duration audioDuration = Duration.seconds(durationLong);
                if(duration == null || durationLong > duration.toSeconds()) duration = audioDuration;
                mediaDetails.put("audioDuration", Utilities.getTime(audioDuration));
            }

            Integer channels = audioStream.getChannels();
            if(channels != null){
                audioChannels = channels;
                String channelLayout = audioStream.getChannelLayout();
                if(channelLayout != null){
                    if(Character.isDigit(channelLayout.charAt(0))) mediaDetails.put("audioChannels", channelLayout);
                    else mediaDetails.put("audioChannels", audioChannels + " (" + channelLayout + ")");
                }
                else mediaDetails.put("audioChannels", String.valueOf(audioChannels));
            }


            String audioCodec = audioStream.getCodecName();
            if(audioCodec != null) mediaDetails.put("audioCodec", audioCodec);

            Long bitrate = audioStream.getBitRate();
            Long fileBitrate = probeResult.getFormat().getBitRate();
            if(fileBitrate != null && videoStream == null && (bitrate == null || bitrate > fileBitrate)) {
                mediaDetails.put("audioBitrate", Utilities.formatBitrate(fileBitrate));
            }
            else if(bitrate != null) mediaDetails.put("audioBitrate", Utilities.formatBitrate(bitrate));

            Integer sampleRate = audioStream.getSampleRate();
            if(sampleRate != null) mediaDetails.put("sampleRate", NumberFormat.getInstance().format(sampleRate) + " Hz");

            if(videoStream == null){
                String formatName = probeResult.getFormat().getFormatName();
                if(formatName != null) mediaDetails.put("format", formatName);
            }

        }

        if(duration == null){
            Float durationFloat = probeResult.getFormat().getDuration();
            if(durationFloat != null) this.duration = Duration.seconds(durationFloat);
        }


        mediaDetails.put("size", Utilities.formatFileSize(file.length()));
        mediaDetails.put("name", file.getName());
        mediaDetails.put("path", file.getAbsolutePath());
        mediaDetails.put("modified", DateFormat.getDateInstance().format(new Date(file.lastModified())));
        mediaDetails.put("hasVideo", String.valueOf(videoStream != null));
        mediaDetails.put("hasAudio", String.valueOf(audioStream != null));
        if(duration != null) mediaDetails.put("duration", Utilities.getTime(duration));

        //mediaInformation.putAll(fFmpegFrameGrabber.getMetadata());

        String extension = Utilities.getFileExtension(this.file);

        if(extension.equals("mp4") || extension.equals("mov")){
            if(mediaInformation.containsKey("media_type") && mediaInformation.get("media_type").equals("6")) placeholderCover = new Image(Objects.requireNonNull(mainController.getClass().getResource("images/music.png")).toExternalForm());
            else if(mediaInformation.containsKey("media_type") && mediaInformation.get("media_type").equals("21")) placeholderCover = new Image(Objects.requireNonNull(mainController.getClass().getResource("images/podcast.png")).toExternalForm());
            else placeholderCover = new Image(Objects.requireNonNull(mainController.getClass().getResource("images/video.png")).toExternalForm());
        }
        else if(extension.equals("mkv") || extension.equals("flv") || extension.equals("avi")){
            placeholderCover = new Image(Objects.requireNonNull(mainController.getClass().getResource("images/video.png")).toExternalForm());
        }
        else {
            placeholderCover = new Image(Objects.requireNonNull(mainController.getClass().getResource("images/music.png")).toExternalForm());
        }
    }

    public MediaItem(MediaItem mediaItem, MainController mainController){
        this.mainController = mainController;

        file = mediaItem.file;
        duration = mediaItem.duration;
        cover = mediaItem.cover;
        placeholderCover = mediaItem.placeholderCover;
        backgroundColor = mediaItem.backgroundColor;
        hasCover = mediaItem.hasCover;
        mediaInformation = mediaItem.mediaInformation;
        mediaDetails = mediaItem.mediaDetails;
        hasVideo = mediaItem.hasVideo;
        hasAudio = mediaItem.hasAudio;
        probeResult = mediaItem.probeResult;
        width = mediaItem.width;
        height = mediaItem.height;
        audioChannels = mediaItem.audioChannels;
        numberOfNonPictureVideoStreams = mediaItem.numberOfNonPictureVideoStreams;
        numberOfAttachmentStreams = mediaItem.numberOfAttachmentStreams;
        numberOfSubtitleStreams = mediaItem.numberOfSubtitleStreams;
        videoStream = mediaItem.videoStream;
        audioStream = mediaItem.audioStream;
        subtitleStreamLanguages = mediaItem.subtitleStreamLanguages;
        defaultSubtitleStream = mediaItem.defaultSubtitleStream;
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

    public Image getPlaceholderCover() {
        return placeholderCover;
    }

    public void setPlaceHolderCover(Image image) {
        placeholderCover = image;
    }

    public FFprobeResult getProbeResult() {
        return probeResult;
    }


    public Map<String, String> getMediaInformation() {
        return mediaInformation;
    }

    public boolean setMediaInformation(Map<String, String> map, boolean updateFile) {

        boolean metadataEditSuccess = false;

        if(updateFile){

            boolean success = MediaUtilities.updateMetadata(file, map, hasCover, cover, newCover, coverRemoved, numberOfNonPictureVideoStreams, numberOfAttachmentStreams, duration);

            if(success){
                //overwrite curr file with new file

                mediaDetails.put("size", Utilities.formatFileSize(file.length()));
                mediaDetails.put("modified", DateFormat.getDateInstance().format(new Date(file.lastModified())));

                if(newCover != null){
                    cover = new Image(newCover.getAbsolutePath());
                    hasCover = true;
                    backgroundColor = newColor;
                }
                else if(coverRemoved){
                    hasCover = false;
                    cover = null;
                    if(hasVideo){
                        int videoStreamIndex = probeResult.getStreams().indexOf(this.videoStream);
                        if(videoStreamIndex != -1){
                            Long durationLong = videoStream.getDuration(TimeUnit.SECONDS);
                            if(durationLong == null && this.duration != null) durationLong = (long) this.duration.toSeconds() ;
                            if(durationLong != null) cover = MediaUtilities.getVideoFrame(file, videoStreamIndex, durationLong/2);
                        }
                        if(cover != null) backgroundColor = MediaUtilities.findDominantColor(cover);
                    }
                    else backgroundColor = null;
                }

                String extension = Utilities.getFileExtension(file);
                if(extension.equals("mp4") || extension.equals("mov")){
                    switch (map.getOrDefault("media_type", null)) {
                        case "6" -> placeholderCover = new Image(Objects.requireNonNull(mainController.getClass().getResource("images/music.png")).toExternalForm());
                        case "21" -> placeholderCover = new Image(Objects.requireNonNull(mainController.getClass().getResource("images/podcast.png")).toExternalForm());
                        default -> placeholderCover = new Image(Objects.requireNonNull(mainController.getClass().getResource("images/video.png")).toExternalForm());
                    }
                }

                mediaInformation = map;
                metadataEditSuccess = true;
            }
        }
        else {
            mediaInformation = map;
            metadataEditSuccess = true;
        }

        newCover = null;
        newColor = null;
        coverRemoved = false;

        return metadataEditSuccess;
    }

    public Map<String, String> getMediaDetails() {
        return mediaDetails;
    }

    public void setMediaDetails(Map<String, String> map) {
        mediaDetails = map;
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



