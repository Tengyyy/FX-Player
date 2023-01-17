package hans;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef;
import hans.MediaItems.MediaItem;
import hans.Menu.HistoryItem;
import hans.Menu.MenuController;
import hans.Menu.MenuObject;
import hans.Menu.QueueItem;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;

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


    public static void setCurrentTimeLabel(Label durationLabel, Duration currentTime, Duration duration) {
        durationLabel.setText(getTime(currentTime) + "/" + getTime(duration));
    }

    public static void setTimeLeftLabel(Label durationLabel, Duration currentTime, Duration duration) {
        durationLabel.setText("−" + getTime(duration.subtract(currentTime)) + "/" + getTime(duration));
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
            if(queueItem.getMediaItem() != null && queueItem.getMediaItem().getFile().getAbsolutePath().equals(file.getAbsolutePath())){
                temp = Utilities.copyMediaItem(queueItem.getMediaItem(), menuController.mainController);
                break;
            }
        }
        if(temp == null){
            for (HistoryItem historyItem : menuController.history){
                if(historyItem.getMediaItem() != null && historyItem.getMediaItem().getFile().getAbsolutePath().equals(file.getAbsolutePath())){
                    temp = Utilities.copyMediaItem(historyItem.getMediaItem(), menuController.mainController);
                    break;
                }
            }
        }
        if(temp == null){
            if(menuController.activeItem != null && menuController.activeItem.getMediaItem() != null && menuController.activeItem.getMediaItem().getFile().getAbsolutePath().equals(file.getAbsolutePath())){
                temp = Utilities.copyMediaItem(menuController.activeItem.getMediaItem(), menuController.mainController);
            }
        }
        if(temp == null){
            temp = Utilities.createMediaItem(file, menuController.mainController);
        }

        return temp;
    }

    public static MediaItem createMediaItem(File file, MainController mainController){
        return switch (Utilities.getFileExtension(file)) {
            case "mp4", "mov", "mkv", "avi", "flv", "mp3", "flac", "wav", "ogg", "opus", "aiff", "m4a", "wma", "aac" -> new MediaItem(file, mainController);
            default -> null;
        };
    }

    public static MediaItem copyMediaItem(MediaItem mediaItem, MainController mainController){
        return switch (Utilities.getFileExtension(mediaItem.getFile())) {
            case "mp4", "mov", "mkv", "avi", "flv", "mp3", "flac", "wav", "ogg", "opus", "aiff", "m4a", "wma", "aac"  -> new MediaItem(mediaItem, mainController);
            default -> null;
        };
    }

    public static String[] splitLines(String str) {
        return str.split("\\R");
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

    public static void cleanDirectory(String path){
        File directory = new File(path);
        File[] files = directory.listFiles();

        if(files != null){
            for(File file : files){
                file.delete();
            }
        }
    }

    public static int nthOccurrence(String str1, String str2, int n) {

        String tempStr = str1;
        int tempIndex = -1;
        int finalIndex = 0;
        for(int occurrence = 0; occurrence < n ; ++occurrence){
            tempIndex = tempStr.indexOf(str2);
            if(tempIndex==-1){
                finalIndex = 0;
                break;
            }
            tempStr = tempStr.substring(++tempIndex);
            finalIndex+=tempIndex;
        }
        return --finalIndex;
    }
}
