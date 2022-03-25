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

public class MediaItem {

    File file;
    Media media;

    // used to retrieve/set media metadata
    MetadataEditor mediaMeta;


    // technical details of the media object (TODO: separate audio and video)
    double framerate;
    double width;
    double height;



    // Apple movie tags (filled if this media object is an MP4 file)
    String title;
    Image cover;


    // ID-3 tags (filled if this media object is an MP3 file)
    String album;
    String artist;


    MediaItem(File file) throws IOException {

        this.file = file;

        //metadata to retrieve: title, release date, thumbnail, cast, short description, longer description, rating, genres, parental rating, directors, producers, writers

       /* mediaMeta = MetadataEditor.createFrom(file);

        Map<String, MetaValue> keyedMeta = mediaMeta.getKeyedMeta();
        if (keyedMeta != null) {
            System.out.println("Keyed metadata:");
            for (Map.Entry<String, MetaValue> entry : keyedMeta.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        }

        Map<Integer, MetaValue> itunesMeta = mediaMeta.getItunesMeta();
        if (itunesMeta != null) {
            System.out.println("iTunes metadata:");
            for (Map.Entry<Integer, MetaValue> entry : itunesMeta.entrySet()) {
                System.out.println(Utilities.fourccToString(entry.getKey()) + ": " + entry.getValue());
            }
        }*/

        // gets fps
        try {
            FrameGrab grab = FrameGrab.createFrameGrab(NIOUtils.readableChannel(file));
            DemuxerTrack vt = grab.getVideoTrack();

            int frameCount = vt.getMeta().getTotalFrames();
            double duration = vt.getMeta().getTotalDuration();
            System.out.println(frameCount / duration);

        } catch (IOException | JCodecException e) {
            throw new RuntimeException(e);
        }
    }
}
