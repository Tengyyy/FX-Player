package hans.MediaItems;

import hans.MainController;
import hans.Utilities;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.images.Artwork;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.*;

public class AudioItem implements MediaItem {


    File file;
    File subtitles;
    boolean subtitlesOn = false;
    Color backgroundColor;
    Duration duration;

    Image cover;
    Image placeholderCover;

    MainController mainController;

    boolean hasCover;

    Map<FieldKey, String> mediaInformation = new HashMap<>();
    Map<String, String> mediaDetails = new HashMap<>();


    public AudioItem(File file, MainController mainController){
        this.file = file;
        this.mainController = mainController;


        try {
            AudioFile f = AudioFileIO.read(file);

            duration = Duration.seconds(f.getAudioHeader().getTrackLength());

            Tag tag = f.getTag();
            Artwork artwork = tag.getFirstArtwork();
            if(artwork != null){
                byte[] coverBinaryData = artwork.getBinaryData();
                cover = new Image(new ByteArrayInputStream(coverBinaryData));
            }

            mediaInformation.put(FieldKey.TITLE, tag.getFirst(FieldKey.TITLE));
            mediaInformation.put(FieldKey.ARTIST, tag.getFirst(FieldKey.ARTIST));
            mediaInformation.put(FieldKey.ALBUM, tag.getFirst(FieldKey.ALBUM));
            mediaInformation.put(FieldKey.ALBUM_ARTIST, tag.getFirst(FieldKey.ALBUM_ARTIST));
            mediaInformation.put(FieldKey.TRACK, tag.getFirst(FieldKey.TRACK));
            mediaInformation.put(FieldKey.TRACK_TOTAL, tag.getFirst(FieldKey.TRACK_TOTAL));
            mediaInformation.put(FieldKey.YEAR, tag.getFirst(FieldKey.YEAR));
            mediaInformation.put(FieldKey.GENRE, tag.getFirst(FieldKey.GENRE));
            mediaInformation.put(FieldKey.COMMENT, tag.getFirst(FieldKey.COMMENT));
            mediaInformation.put(FieldKey.COMPOSER, tag.getFirst(FieldKey.COMPOSER));
            mediaInformation.put(FieldKey.DISC_NO, tag.getFirst(FieldKey.DISC_NO));
            mediaInformation.put(FieldKey.DISC_TOTAL, tag.getFirst(FieldKey.DISC_TOTAL));
            mediaInformation.put(FieldKey.LYRICS, tag.getFirst(FieldKey.LYRICS));
            mediaInformation.put(FieldKey.LANGUAGE, tag.getFirst(FieldKey.LANGUAGE));
            mediaInformation.put(FieldKey.RECORD_LABEL, tag.getFirst(FieldKey.RECORD_LABEL));


            mediaDetails.put("size", Utilities.formatFileSize(file.length()));
            mediaDetails.put("name", file.getName());
            mediaDetails.put("path", file.getAbsolutePath());
            mediaDetails.put("modified", DateFormat.getDateInstance().format(new Date(file.lastModified())));
            mediaDetails.put("hasVideo", "false");
            mediaDetails.put("hasAudio", "true");
            mediaDetails.put("audioChannels", f.getAudioHeader().getChannels());
            mediaDetails.put("audioCodec", f.getAudioHeader().getEncodingType());
            mediaDetails.put("audioBitrate", Utilities.formatBitrate(f.getAudioHeader().getBitRateAsNumber() * 1000));
            mediaDetails.put("audioBitDepth", f.getAudioHeader().getBitsPerSample() + " bits");
            mediaDetails.put("duration", Utilities.getTime(duration));
            mediaDetails.put("format", f.getAudioHeader().getFormat());
            mediaDetails.put("sampleRate", NumberFormat.getInstance().format(f.getAudioHeader().getSampleRateAsNumber()) + " Hz");

        } catch (IOException | CannotReadException | TagException | ReadOnlyFileException | InvalidAudioFrameException e) {
            throw new RuntimeException(e);
        }


        hasCover = cover != null;
        if(cover != null) backgroundColor = Utilities.findDominantColor(cover);

        placeholderCover = new Image(Objects.requireNonNull(Objects.requireNonNull(mainController.getClass().getResource("images/musicGraphic.png")).toExternalForm()));

    }

    @Override
    public float getFrameDuration() {
        return 0;
    }

    @Override
    public Map<FieldKey, String> getMediaInformation() {
        return mediaInformation;
    }

    @Override
    public Map<String, String> getMediaDetails() {
        return mediaDetails;
    }


    @Override
    public File getFile() {return this.file;}

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
        return false;
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
}
