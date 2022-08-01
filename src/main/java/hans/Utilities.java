package hans;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
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


    public static void setCurrentTimeLabel(Label durationLabel, Slider slider, Duration duration) {
        durationLabel.setText(getTime(Duration.seconds(slider.getValue())) + "/" + getTime(duration));
    }

    public static void setTimeLeftLabel(Label durationLabel, Slider slider, Duration duration) {
        durationLabel.setText("−" + getTime(duration.subtract(Duration.seconds(slider.getValue()))) + "/" + getTime(duration));
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

    public static String[] splitLines(String str) {
        return str.split("\\R", 2);
    }
}
