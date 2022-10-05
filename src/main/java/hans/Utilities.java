package hans;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import io.github.palexdev.materialfx.utils.SwingFXUtils;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.bytedeco.javacv.*;
import org.jcodec.movtool.MetadataEditor;

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


    public static Color findDominantColor(double realWidth, double realHeight, Image image){

        PixelReader pr = image.getPixelReader();
        Map<Color, Long> colCount = new HashMap<>();

        if(realWidth < 125){
            // scan left and right edges to find the dominant color

            for(int x = 0; x < Math.min(image.getWidth(), 5); x++) {
                for(int y = 0; y < image.getHeight(); y++) {
                    final Color col = pr.getColor(x, y);
                    if(colCount.containsKey(col)) {
                        colCount.put(col, colCount.get(col) + 1);
                    } else {
                        colCount.put(col, 1L);
                    }
                }
            }

            if(image.getWidth() > 5){
                for(int x = (int) Math.max((image.getWidth() - 5), 5); x < image.getWidth(); x++){
                    for(int y = 0; y < image.getHeight(); y++) {
                        final Color col = pr.getColor(x, y);
                        if(colCount.containsKey(col)) {
                            colCount.put(col, colCount.get(col) + 1);
                        } else {
                            colCount.put(col, 1L);
                        }
                    }
                }
            }
        }
        else {
            //scan top and bottom edges

            for(int y = 0; y < Math.min(image.getHeight(), 5); y++) {
                for(int x = 0; x < image.getWidth(); x++) {
                    final Color col = pr.getColor(x, y);
                    if(colCount.containsKey(col)) {
                        colCount.put(col, colCount.get(col) + 1);
                    } else {
                        colCount.put(col, 1L);
                    }
                }
            }

            if(image.getHeight() > 5){
                for(int y = (int) Math.max((image.getHeight() - 5), 5); y < image.getHeight(); y++){
                    for(int x = 0; x < image.getWidth(); x++) {
                        final Color col = pr.getColor(x, y);
                        if(colCount.containsKey(col)) {
                            colCount.put(col, colCount.get(col) + 1);
                        } else {
                            colCount.put(col, 1L);
                        }
                    }
                }
            }
        }


        // Return the color with the highest number of occurrences .
        return colCount.entrySet().stream().max(Map.Entry.comparingByValue()).get().getKey();
    }


    public static Image grabRandomFrame(File file){


        FFmpegFrameGrabber fFmpegFrameGrabber = new FFmpegFrameGrabber(file);


        Image image = null;

        try {

            Random random = new Random();
            fFmpegFrameGrabber.start();

            int totalFrames = fFmpegFrameGrabber.getLengthInFrames();
            int randomFrame = random.nextInt(totalFrames);

            fFmpegFrameGrabber.setFrameNumber(randomFrame);
            Frame frame = fFmpegFrameGrabber.grabImage();

            JavaFXFrameConverter javaFXFrameConverter = new JavaFXFrameConverter();
            image = javaFXFrameConverter.convert(frame);

            fFmpegFrameGrabber.stop();
            fFmpegFrameGrabber.close();


        } catch (Exception e) {
            e.printStackTrace();
        }

        return image;
    }


    public static String formatFileSize(long size){
        double countingSize = size;
        String unit = "";

        if(countingSize >= 1000){
           countingSize /= 1000;
           unit = " KB";
        }
        if(countingSize >= 1000){
            countingSize /= 1000;
            unit = " MB";
        }
        if(countingSize >= 1000) {
            countingSize /= 1000;
            unit = " GB";
        }

        return round(countingSize, 1) + unit;
    }

    public static String formatBitrate(long bits){
        double countingBits = bits;
        String unit = " bit/s";

        if(countingBits >= 1000){
            countingBits /= 1000;
            unit = " kbit/s";
        }
        if(countingBits >= 1000){
            countingBits /= 1000;
            unit = " Mbit/s";
        }

        return round(countingBits, 1) + unit;
    }

    public static double round (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

}
