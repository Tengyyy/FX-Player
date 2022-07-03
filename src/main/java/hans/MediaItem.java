package hans;


import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.util.Duration;

import java.io.File;
import java.util.Map;

public interface MediaItem {


    float getFrameDuration();

    Map getMediaInformation();

    Map getMediaDetails();

    Media getMedia();

    File getFile();

    File getSubtitles();

    boolean getSubtitlesOn();

    void setSubtitlesOn(boolean value);

    Duration getDuration();

    String getArtist();

    String getTitle();

    Image getCover();

    void setSubtitles(File file);
}
