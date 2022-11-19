package hans;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef;
import hans.MediaItems.*;
import hans.Menu.HistoryItem;
import hans.Menu.MenuController;
import hans.Menu.MenuObject;
import hans.Menu.QueueItem;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import org.bytedeco.javacv.*;

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

    public static MediaItem searchDuplicateOrCreate(File file, MenuController menuController){
        MediaItem temp = null;
        for(QueueItem queueItem : menuController.queue){
            if(queueItem.getMediaItem().getFile().getAbsolutePath().equals(file.getAbsolutePath())){
                temp = Utilities.copyMediaItem(queueItem.getMediaItem(), menuController.mainController);
                break;
            }
        }
        if(temp == null){
            for (HistoryItem historyItem : menuController.history){
                if(historyItem.getMediaItem().getFile().getAbsolutePath().equals(file.getAbsolutePath())){
                    temp = Utilities.copyMediaItem(historyItem.getMediaItem(), menuController.mainController);
                    break;
                }
            }
        }
        if(temp == null){
            if(menuController.activeItem != null && menuController.activeItem.getMediaItem().getFile().getAbsolutePath().equals(file.getAbsolutePath())){
                temp = Utilities.copyMediaItem(menuController.activeItem.getMediaItem(), menuController.mainController);
            }
        }
        if(temp == null){
            temp = Utilities.createMediaItem(file, menuController.mainController);
        }

        return temp;
    }

    public static MediaItem createMediaItem(File file, MainController mainController){
        switch(Utilities.getFileExtension(file)){
            case "mp4": return new Mp4Item(file, mainController);
            case "mp3":
            case "flac": return new AudioItem(file, mainController);
            case "avi": return new AviItem(file, mainController);
            case "mkv": return new MkvItem(file, mainController);
            case "flv": return new FlvItem(file, mainController);
            case "mov": return new MovItem(file, mainController);
            case "wav": return new WavItem(file, mainController);
            default: return null;
        }
    }

    public static MediaItem copyMediaItem(MediaItem mediaItem, MainController mainController){
        switch(Utilities.getFileExtension(mediaItem.getFile())){
            case "mp4": return new Mp4Item((Mp4Item) mediaItem, mainController);
            case "mp3":
            case "flac": return new AudioItem((AudioItem) mediaItem, mainController);
            case "avi": return new AviItem((AviItem) mediaItem, mainController);
            case "mkv": return new MkvItem((MkvItem) mediaItem, mainController);
            case "flv": return new FlvItem((FlvItem) mediaItem, mainController);
            case "mov": return new MovItem((MovItem) mediaItem, mainController);
            case "wav": return new WavItem((WavItem) mediaItem, mainController);
            default: return null;
        }
    }

    public static String[] splitLines(String str) {
        return str.split("\\R", 2);
    }


    public static Color findDominantColor(Image image){

        double aspectRatio = image.getWidth() / image.getHeight();

        PixelReader pr = image.getPixelReader();
        Map<Color, Long> colCount = new HashMap<>();

        if(aspectRatio < (double) 16/9){
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


    public static Image grabMiddleFrame(File file){


        FFmpegFrameGrabber fFmpegFrameGrabber = new FFmpegFrameGrabber(file);


        Image image = null;

        try {

            fFmpegFrameGrabber.start();

            int totalFrames = fFmpegFrameGrabber.getLengthInFrames();

            fFmpegFrameGrabber.setFrameNumber(Math.floorDiv(totalFrames, 2));
            Frame frame = null;
            if(fFmpegFrameGrabber.hasVideo()) frame = fFmpegFrameGrabber.grabImage();

            JavaFXFrameConverter javaFXFrameConverter = new JavaFXFrameConverter();
            if(frame != null) image = javaFXFrameConverter.convert(frame);

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

    public static String[] splitString(String value){
        String[] values = new String[2];
        if(value.isBlank()){
            values[0] = "";
            values[1] = "";
        }
        else {
            if(value.contains("/")){
                values[0] = value.substring(0, value.indexOf('/'));
                values[1] = value.substring(value.indexOf('/') + 1);
            }
            else {
                values[0] = value;
                values[1] = "";
            }
        }

        return values;
    }

    public static double round (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

    public static boolean checkMap(Map<String, String> map, String key){
        return map.containsKey(key) && !map.get(key).trim().isEmpty();
    }

    public static ArrayList<MenuObject> findDuplicates(MenuObject menuObject, MenuController menuController){
        ArrayList<MenuObject> duplicates = new ArrayList<>();

        String path = menuObject.getMediaItem().getFile().getAbsolutePath();

        for(QueueItem queueItem : menuController.queue){
            if(queueItem != menuObject && queueItem.getMediaItem().getFile().getAbsolutePath().equals(path)){
                duplicates.add(queueItem);
            }
        }

        for (HistoryItem historyItem : menuController.history){
            if(historyItem != menuObject && historyItem.getMediaItem().getFile().getAbsolutePath().equals(path)){
                duplicates.add(historyItem);
            }
        }

        if(menuController.activeItem != menuObject && menuController.activeItem != null && menuController.activeItem.getMediaItem().getFile().getAbsolutePath().equals(path)){
            duplicates.add(menuController.activeItem);
        }

        return duplicates;
    }

    public static WinDef.HWND getNativeHandleForStage(Stage stage) {
        try {
            final Method getPeer = Window.class.getDeclaredMethod("getPeer", null);
            getPeer.setAccessible(true);
            final Object tkStage = getPeer.invoke(stage);
            final Method getRawHandle = tkStage.getClass().getMethod("getRawHandle");
            getRawHandle.setAccessible(true);
            final Pointer pointer = new Pointer((Long) getRawHandle.invoke(tkStage));
            return new WinDef.HWND(pointer);
        } catch (Exception ex) {
            System.err.println("Unable to determine native handle for window");
            return null;
        }
    }

}
