package hans.MediaItems;

import hans.MediaItems.MediaItem;
import hans.Utilities;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.bytedeco.javacv.*;
import org.jcodec.movtool.MetadataEditor;

import java.io.File;
import java.io.IOException;
import java.util.Map;



public class Mp4Item implements MediaItem {

    File file;


    File subtitles;
    boolean subtitlesOn;

    Color backgroundColor = null;

    // class to retrieve and edit movie information (apple tags)
    MetadataEditor mediaMeta;

    // General/movie meta tags
    String title; // Title of the content (©nam)
    String director; // Name of media director (©dir)
    String writer; // Name of media writer/s (©wrt)
    String producer; // Name of media producer/s (©prd)
    String performers; // Names of performers (cast) (©prf)
    String comment; // Extra client-side comments about the media item (©cmt)
    String genre; // Genre of the media item (©gen) (maybe int instead)
    String date; // Date the media content was created (©day)
    Image cover; // Media cover/thumbnail (covr)
    String description; // Media description/tagline (desc)
    String longDescription; // Longer media description/ plot (ldes)
    int HDVideo; // Video definition (0 = SD, 1 = HD) (hdvd) (turn to boolean instead)
    String mediaType;
    //int mediaType = 0; // (Default = Home video, 6 = Music Video, 9 = Movie, 10 = TV Show, 21 = Podcast *not supported initially) (stik)
    String copyright; // (©cpy)


    // TV Show meta tags
    int TVSeason; //TV Show season number (tvsn);
    int TVEpisode; // TV Show episode number (tves);
    String TVShow; // TV Show title (tvsh)
    String TVNetworkName; // Channel/streaming service this TV Show runs on (tvnn)



    // Music video meta tags
    String albumArtist; // Author of the album as a whole (aART)
    String album; // (©alb)
    String artist; // (©ART)
    String rating; // 0 = none, 1 = Explicit, 2 = Clean (rtng)
    String soundEngineer; // (©sne)
    String lyrics; // (©lyr)
    String songWriter; // (©swf)
    String composer; // (©com)
    String arranger; // (©arg)
    String recordLabelName; // (©lab)

    // technical details of the media object (TODO: separate audio and video)
    double frameRate = 30;
    float frameDuration = (float) (1 / frameRate);
    double width;
    double height;

    Duration duration;


    boolean hasVideo;

    public Mp4Item(File file) {
        this.file = file;


        try {
            FFmpegFrameGrabber fFmpegFrameGrabber = new FFmpegFrameGrabber(file);

            fFmpegFrameGrabber.start();

            hasVideo = fFmpegFrameGrabber.hasVideo();

            if(fFmpegFrameGrabber.hasVideo()) duration = Duration.seconds(fFmpegFrameGrabber.getLengthInFrames() / fFmpegFrameGrabber.getFrameRate());
            else duration = Duration.seconds(fFmpegFrameGrabber.getLengthInAudioFrames() / fFmpegFrameGrabber.getAudioFrameRate());

            frameRate = fFmpegFrameGrabber.getFrameRate();
            frameDuration = (float) (1 / frameRate);

            Map<String, String> metadata = fFmpegFrameGrabber.getMetadata();

            if(metadata != null){
                for(Map.Entry<String, String> entry : metadata.entrySet()){
                    switch (entry.getKey()){
                        case "media_type":
                            switch(Integer.parseInt(entry.getValue())){
                                case 6: mediaType = "Music video";
                                    break;
                                case 9: mediaType = "Movie";
                                    break;
                                case 10: mediaType = "TV Show";
                                    break;
                                case 21: mediaType = "Podcast";
                                    break;
                                default: mediaType = "Home video";
                            }
                            break;
                        case "title": title = entry.getValue();
                            break;
                        case "artist": artist = entry.getValue();
                            break;
                        default:
                            break;
                    }
                }
            }

            fFmpegFrameGrabber.stop();

            fFmpegFrameGrabber.setVideoStream(2);
            fFmpegFrameGrabber.start();


            Frame frame = fFmpegFrameGrabber.grabImage();
            JavaFXFrameConverter javaFXFrameConverter = new JavaFXFrameConverter();
            if(frame != null) cover = javaFXFrameConverter.convert(frame);

            if(cover ==  null){
                cover = Utilities.grabMiddleFrame(file);
            }

            fFmpegFrameGrabber.stop();
            fFmpegFrameGrabber.close();

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
    public float getFrameDuration() {
        return this.frameDuration;
    }

    @Override
    public Map getMediaInformation() {
        return null;
    }

    @Override
    public Map getMediaDetails() {
        return null;
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
    public String getArtist(){
        //only return information under the artist tag if the media type is "music video"
        //because in case of a movie or tv show the information under the artist tag
        //might have been incorrectly categorized and actually contain cast, directors etc..
        if(mediaType != null && mediaType.equals("Music video")){
            return artist;
        }
        else return null;
    }

    @Override
    public String getTitle(){
        return this.title;
    }

    @Override
    public Image getCover(){
        return this.cover;
    }
}



