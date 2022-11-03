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

    Map<String, String> getMediaDetails();

    File getFile();

    File getSubtitles();

    boolean getSubtitlesOn();

    void setSubtitlesOn(boolean value);

    Duration getDuration();

    Image getCover();

    void setSubtitles(File file);

    Color getCoverBackgroundColor();

    void setCoverBackgroundColor(Color color);

    boolean hasVideo();

    boolean hasCover();

    void setHasCover(boolean value);

    Image getPlaceholderCover();
    void setPlaceHolderCover(Image image);


    // 1024 is int value for attached pic disposition
}
