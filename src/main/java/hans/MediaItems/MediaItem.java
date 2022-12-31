package hans.MediaItems;


import com.github.kokorin.jaffree.ffprobe.FFprobeResult;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

public interface MediaItem {


    Map<String, String> getMediaInformation();

    boolean setMediaInformation(Map<String, String> map, boolean updateFile);

    Map<String, String> getMediaDetails();

    void setMediaDetails(Map<String, String> map);

    File getFile();

    Duration getDuration();

    Image getCover();

    boolean setCover(File imagePath, Image image, Color color, boolean updateFile);

    Color getCoverBackgroundColor();

    boolean hasVideo();

    boolean hasCover();

    Image getPlaceholderCover();
    void setPlaceHolderCover(Image image);

    FFprobeResult getProbeResult();


    // 1024 is int value for attached pic disposition
}
