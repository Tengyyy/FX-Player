package hans;

import javafx.scene.image.Image;
import javafx.scene.media.Media;
import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.common.DemuxerTrack;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.containers.mp4.boxes.MetaValue;
import org.jcodec.movtool.MetadataEditor;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Mp4Item implements MediaItem{

    File file;
    Media media;

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
    int HDVideo; // Video definition (0 = SD, 1 = HD) (hdvd)
    int mediaType = 0; // (Default = Home video, 6 = Music Video, 9 = Movie, 10 = TV Show, 21 = Podcast *not supported initially) (stik)
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
    double frameRate = 30; // 30 by default, will be over-written by the metadata reader
    float frameDuration = (float) (1 / frameRate);
    double width;
    double height;

    Mp4Item(File file){
        this.file = file;

        media = new Media(file.toURI().toString());

        //metadata to retrieve: title, release date, thumbnail, cast, short description, longer description, rating, genres, parental rating, directors, producers, writers


        try {
            mediaMeta = MetadataEditor.createFrom(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*Map<String, MetaValue> keyedMeta = mediaMeta.getKeyedMeta();
        if (keyedMeta != null) {
            System.out.println("Keyed metadata:");
            for (Map.Entry<String, MetaValue> entry : keyedMeta.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        }*/

        Map<Integer, MetaValue> itunesMeta = mediaMeta.getItunesMeta();
        if (itunesMeta != null) {
            System.out.println("iTunes metadata:");
            for (Map.Entry<Integer, MetaValue> entry : itunesMeta.entrySet()) {
                String keyString = Utilities.fourccToString(entry.getKey());
                System.out.println(keyString + ": " + entry.getValue());

                switch(keyString){
                    case "©nam": ;
                    break;
                    default:
                        break;
                }
            }
        }

        // gets fps
        try {
            FrameGrab grab = FrameGrab.createFrameGrab(NIOUtils.readableChannel(file));
            DemuxerTrack vt = grab.getVideoTrack();

            int frameCount = vt.getMeta().getTotalFrames();
            double duration = vt.getMeta().getTotalDuration();
            frameRate = frameCount / duration;
            frameDuration = (float) (1 / frameRate);

        } catch (IOException | JCodecException e) {
            throw new RuntimeException(e);
        }
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
    public Media getMedia() {
        return this.media;
    }

    @Override
    public File getFile() {
        return this.file;
    }
}
