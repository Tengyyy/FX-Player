package hans.MediaItems;

import com.github.kokorin.jaffree.ffprobe.FFprobeResult;
import hans.MainController;
import hans.Utilities;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.images.Artwork;
import org.jaudiotagger.tag.images.ArtworkFactory;
import org.jaudiotagger.tag.wav.WavTag;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.*;

public class WavItem implements MediaItem {


    File file;
    Color backgroundColor;
    Duration duration;

    Image cover;
    Image placeholderCover;

    MainController mainController;

    boolean hasCover;

    Map<String, String> mediaInformation = new HashMap<>();
    Map<String, String> mediaDetails = new HashMap<>();


    public WavItem(File file, MainController mainController){
        this.file = file;
        this.mainController = mainController;


        try {
            AudioFile f = AudioFileIO.read(file);

            duration = Duration.seconds(f.getAudioHeader().getTrackLength());

            WavTag tag = (WavTag) f.getTag();
            Artwork artwork = tag.getFirstArtwork();
            if(artwork != null){
                byte[] coverBinaryData = artwork.getBinaryData();
                cover = new Image(new ByteArrayInputStream(coverBinaryData));
            }

            mediaInformation.put("title", tag.getFirst(FieldKey.TITLE));
            mediaInformation.put("artist", tag.getFirst(FieldKey.ARTIST));
            mediaInformation.put("album", tag.getFirst(FieldKey.ALBUM));
            mediaInformation.put("album_artist", tag.getFirst(FieldKey.ALBUM_ARTIST));
            mediaInformation.put("track", tag.getFirst(FieldKey.TRACK));
            mediaInformation.put("year", tag.getFirst(FieldKey.YEAR));
            mediaInformation.put("genre", tag.getFirst(FieldKey.GENRE));
            mediaInformation.put("comment", tag.getFirst(FieldKey.COMMENT));
            mediaInformation.put("composer", tag.getFirst(FieldKey.COMPOSER));
            mediaInformation.put("record_label", tag.getFirst(FieldKey.RECORD_LABEL));


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

        if(cover != null){
            backgroundColor = MediaUtilities.findDominantColor(cover);
        }

        placeholderCover = new Image(Objects.requireNonNull(Objects.requireNonNull(mainController.getClass().getResource("images/music.png")).toExternalForm()));

    }

    public WavItem(WavItem audioItem, MainController mainController){
        this.mainController = mainController;

        this.file = audioItem.getFile();
        duration = audioItem.getDuration();
        cover = audioItem.getCover();
        placeholderCover = audioItem.getPlaceholderCover();
        backgroundColor = audioItem.getCoverBackgroundColor();
        hasCover = audioItem.hasCover();
        mediaInformation = audioItem.getMediaInformation();
        mediaDetails = audioItem.getMediaDetails();
    }

    @Override
    public Map<String, String> getMediaInformation() {
        return mediaInformation;
    }

    @Override
    public boolean setMediaInformation(Map<String, String> map, boolean updateFile) {

        if(updateFile){
            try {
                AudioFile f = AudioFileIO.read(file);
                WavTag tag = (WavTag) f.getTag();

                tag.setField(FieldKey.TITLE, map.get("title"));
                tag.setField(FieldKey.ARTIST, map.get("artist"));
                tag.setField(FieldKey.ALBUM, map.get("album"));
                tag.setField(FieldKey.ALBUM_ARTIST, map.get("album_artist"));
                tag.setField(FieldKey.TRACK, map.get("track"));
                tag.setField(FieldKey.YEAR, map.get("year"));
                tag.setField(FieldKey.GENRE, map.get("genre"));
                tag.setField(FieldKey.COMMENT, map.get("comment"));
                tag.setField(FieldKey.COMPOSER, map.get("composer"));
                tag.setField(FieldKey.RECORD_LABEL, map.get("record_label"));

                f.commit();

                mediaInformation = map;

                return true;


            } catch (CannotReadException | TagException | InvalidAudioFrameException | ReadOnlyFileException | IOException | CannotWriteException e) {
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
    public File getFile() {return this.file;}



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
            try {
                AudioFile f = AudioFileIO.read(file);
                Tag tag = f.getTag();
                if(imagePath == null) tag.deleteArtworkField();
                else {
                    tag.setField(ArtworkFactory.createArtworkFromFile(imagePath));
                }
                f.commit();

                cover = image;
                backgroundColor = color;
                hasCover = image != null;

                return true;

            } catch (CannotReadException | IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException | CannotWriteException e) {
                e.printStackTrace();
                return false;
            }
        }
        else {
            cover = image;
            backgroundColor = color;
            hasCover = image != null;

            return true;
        }
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

    @Override
    public FFprobeResult getProbeResult() {
        return null;
    }
}
