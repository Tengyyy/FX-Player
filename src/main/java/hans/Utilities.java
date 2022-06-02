package hans;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.Callable;

import javafx.beans.binding.Bindings;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class Utilities {


    // Create neatly formatted video duration string
    public static String getTime(Duration time) {

        int hours = (int) time.toHours();
        int minutes = (int) time.toMinutes();
        int seconds = (int) time.toSeconds();


        if (seconds > 59)
            seconds = seconds % 60;
        if (minutes > 59)
            minutes = minutes % 60;
        if (hours > 59)
            hours = hours % 60;

        // Don't show the hours unless the video has been playing for an hour or longer.
        if (hours > 0)
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        else
            return String.format("%02d:%02d", minutes, seconds);
    }


    public static void setCurrentTimeLabel(Label durationLabel, MediaPlayer mediaPlayer, Media media) {
        durationLabel.setText(getTime(mediaPlayer.getCurrentTime()) + "/" + getTime(media.getDuration()));
    }

    public static void setTimeLeftLabel(Label durationLabel, MediaPlayer mediaPlayer, Media media) {
        durationLabel.setText("−" + getTime(media.getDuration().subtract(mediaPlayer.getCurrentTime())) + "/" + getTime(media.getDuration()));
    }


    // gets file extension
    public static String getFileExtension(File file) {
        String fileName = file.getName();
        int extensionIndex = fileName.lastIndexOf('.');

        if (extensionIndex > 0) {
            return fileName.substring(extensionIndex + 1);
        } else {
            return " ";
        }

    }

    // Turns four-character-code into a string
    public static String fourccToString(int key) {
        byte[] bytes = new byte[4];
        ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).putInt(key);
        return org.jcodec.platform.Platform.stringFromCharset(bytes, "iso8859-1");
    }
}
