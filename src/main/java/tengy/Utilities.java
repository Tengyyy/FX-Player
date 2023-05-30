package tengy;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef;
import javafx.css.PseudoClass;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import tengy.MediaItems.MediaItem;
import tengy.Menu.MenuController;
import tengy.Menu.Queue.QueueItem;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;


import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

public class Utilities {


    // Create neatly formatted video duration string
    public static String durationToString(Duration time) {

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
        durationLabel.setText(durationToString(currentTime) + "/" + durationToString(duration));
    }

    public static void setTimeLeftLabel(Label durationLabel, Duration currentTime, Duration duration) {
        durationLabel.setText("−" + durationToString(duration.subtract(currentTime)) + "/" + durationToString(duration));
    }


    // gets file extension
    public static String getFileExtension(File file) {
        String fileName = file.getName();
        int extensionIndex = fileName.lastIndexOf('.');

        if (extensionIndex > 0) {
            return fileName.substring(extensionIndex + 1);
        } else {
            return "";
        }

    }


    public static MediaItem createMediaItem(File file, MainController mainController){
        return switch (Utilities.getFileExtension(file)) {
            case "mp4", "mov", "mkv", "avi", "flv", "mp3", "flac", "wav", "ogg", "opus", "aiff", "m4a", "wma", "aac" -> new MediaItem(file, mainController);
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

    public static ArrayList<QueueItem> findDuplicates(QueueItem queueItem, MenuController menuController){
        ArrayList<QueueItem> duplicates = new ArrayList<>();

        String path = queueItem.getMediaItem().getFile().getAbsolutePath();

        for(QueueItem item : menuController.queuePage.queueBox.queue) {
            if (item != queueItem && item.getMediaItem().getFile().getAbsolutePath().equals(path)) {
                duplicates.add(item);
            }
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

    public static void openBrowser(String url){
        if(Desktop.isDesktopSupported()){
            try {
                Desktop.getDesktop().browse(new URL(url).toURI());
            } catch (IOException | URISyntaxException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static boolean isTime(String string){
        if(string.isEmpty()) return false;

        String[] args = string.split(":");

        if(args.length > 3 ) return false;

        String hourString = null;
        String minuteString = null;
        String secondString = null;

        if(args.length == 3){
            hourString = args[0];
            minuteString = args[1];
            secondString = args[2];
        }
        else if(args.length == 2){
            minuteString = args[0];
            secondString = args[1];
        }
        else if(args.length == 1){
            secondString = args[0];
        }

        try {
            if(hourString != null) Integer.parseInt(hourString);
            if(minuteString != null) Integer.parseInt(minuteString);
            if(secondString != null) Double.parseDouble(secondString);

            return true;
        }
        catch(NumberFormatException e){
            return false;
        }
    }

    public static Duration stringToDuration(String string){

        String[] args = string.split(":");

        String hourString = null;
        String minuteString = null;
        String secondString;

        int hours = 0;
        int minutes = 0;
        double seconds = 0;

        if(args.length == 3){
            hourString = args[0];
            minuteString = args[1];
            secondString = args[2];
        }
        else if(args.length == 2){
            minuteString = args[0];
            secondString = args[1];
        }
        else {
            secondString = args[0];
        }
        try {
            if(hourString != null) hours = Integer.parseInt(hourString);
            if(minuteString != null) minutes = Integer.parseInt(minuteString);
            seconds = Double.parseDouble(secondString);

            return Duration.seconds(hours * 3600 + minutes * 60 + seconds);
        }
        catch(NumberFormatException e){
            return Duration.ZERO;
        }

    }

    public static void keyboardFocusOn(Node node){
        node.requestFocus();
        node.pseudoClassStateChanged(PseudoClass.getPseudoClass("keyboardFocused"), true);
    }

    public static void keyboardFocusOff(Node node){
        node.pseudoClassStateChanged(PseudoClass.getPseudoClass("keyboardFocused"), false);
        node.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
    }

    public static void setScrollToNodeMiddle(ScrollPane scrollPane, Node node){
        double heightViewPort = scrollPane.getViewportBounds().getHeight();
        double heightScrollPane = scrollPane.getContent().getBoundsInLocal().getHeight();
        double y = node.getBoundsInParent().getMaxY();
        if (y<(heightViewPort/2)){
            scrollPane.setVvalue(0);
        }
        else if ((y>=(heightViewPort/2))&(y<=(heightScrollPane-heightViewPort/2))){
            scrollPane.setVvalue((y-(heightViewPort/2))/(heightScrollPane-heightViewPort));
        }
        else if( y>= (heightScrollPane-(heightViewPort/2))){
            scrollPane.setVvalue(1);
        }
    }

    public static void setScrollToNodeTop(ScrollPane scrollPane, Node node){
        double heightViewPort = scrollPane.getViewportBounds().getHeight();
        double heightScrollPane = scrollPane.getContent().getBoundsInLocal().getHeight();
        double y = node.getBoundsInParent().getMinY();

        scrollPane.setVvalue(y/(heightScrollPane-heightViewPort));
    }

    public static void setScrollToNodeBottom(ScrollPane scrollPane, Node node){
        double heightViewPort = scrollPane.getViewportBounds().getHeight();
        double heightScrollPane = scrollPane.getContent().getBoundsInLocal().getHeight();
        double y = node.getBoundsInParent().getMaxY();

        scrollPane.setVvalue((y - (heightViewPort))/(heightScrollPane-heightViewPort));
    }

    public static void checkScrollDown(ScrollPane scrollPane, Node node){

        Bounds scrollpaneBounds = scrollPane.localToScene(scrollPane.getBoundsInLocal());
        Bounds nodeBounds = node.localToScene(node.getBoundsInLocal());

        if(nodeBounds.getMaxY() > scrollpaneBounds.getMaxY() || nodeBounds.getMinY() < scrollpaneBounds.getMinY()) setScrollToNodeTop(scrollPane, node);
    }

    public static void checkScrollUp(ScrollPane scrollPane, Node node){

        Bounds scrollpaneBounds = scrollPane.localToScene(scrollPane.getBoundsInLocal());
        Bounds nodeBounds = node.localToScene(node.getBoundsInLocal());

        if(nodeBounds.getMinY() < scrollpaneBounds.getMinY() || nodeBounds.getMaxY() > scrollpaneBounds.getMaxY()) setScrollToNodeBottom(scrollPane, node);
    }
}
