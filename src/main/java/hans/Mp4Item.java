package hans;

import javafx.scene.image.Image;
import javafx.scene.media.Media;
import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.common.DemuxerTrack;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.movtool.MetadataEditor;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Mp4Item implements MediaItem{

    File file;
    Media media;

    // class to retrieve and edit movie information (apple tags)
    MetadataEditor mediaMeta;


    // Apple movie tags
    String title;
    Image cover;


    // technical details of the media object (TODO: separate audio and video)
    double frameRate = 30;
    float frameDuration = (float) (1 / frameRate);
    double width;
    double height;

    Mp4Item(File file){
        this.file = file;

        media = new Media(file.toURI().toString());

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
