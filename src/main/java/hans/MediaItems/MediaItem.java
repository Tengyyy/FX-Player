package hans.MediaItems;


import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.File;
import java.util.Map;

public interface MediaItem {


    float getFrameDuration();

    Map getMediaInformation();

    Map getMediaDetails();

    File getFile();

    File getSubtitles();

    boolean getSubtitlesOn();

    void setSubtitlesOn(boolean value);

    Duration getDuration();

    String getArtist();

    String getTitle();

    Image getCover();

    void setSubtitles(File file);

    Color getCoverBackgroundColor();

    void setCoverBackgroundColor(Color color);

    boolean hasVideo();
}
