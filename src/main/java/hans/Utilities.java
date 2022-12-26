package hans;

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
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.JavaFXFrameConverter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
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
            case "mp4" -> new Mp4Item(file, mainController);
            case "mp3", "flac" -> new AudioItem(file, mainController);
            case "wav" -> new WavItem(file, mainController);
            case "avi" -> new AviItem(file, mainController);
            case "mkv" -> new MkvItem(file, mainController);
            case "flv" -> new FlvItem(file, mainController);
            case "mov" -> new MovItem(file, mainController);
            default -> null;
        };
    }

    public static MediaItem copyMediaItem(MediaItem mediaItem, MainController mainController){
        return switch (Utilities.getFileExtension(mediaItem.getFile())) {
            case "mp4" -> new Mp4Item((Mp4Item) mediaItem, mainController);
            case "mp3", "flac" -> new AudioItem((AudioItem) mediaItem, mainController);
            case "wav" -> new WavItem((WavItem) mediaItem, mainController);
            case "avi" -> new AviItem((AviItem) mediaItem, mainController);
            case "mkv" -> new MkvItem((MkvItem) mediaItem, mainController);
            case "flv" -> new FlvItem((FlvItem) mediaItem, mainController);
            case "mov" -> new MovItem((MovItem) mediaItem, mainController);
            default -> null;
        };
    }

    public static String[] splitLines(String str) {
        return str.split("\\R");
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



    public static String getLog(String filePath){

        String log = "";

        ArrayList<String> arguments = new ArrayList<>();
        String ffprobe = Loader.load(org.bytedeco.ffmpeg.ffprobe.class);

        arguments.add(ffprobe);
        arguments.add("-i");
        arguments.add(filePath);

        try {
            Process process = new ProcessBuilder(arguments).redirectErrorStream(true).start();
            StringBuilder strBuild = new StringBuilder();

            BufferedReader processOutputReader = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.defaultCharset()));
            String line;
            while ((line = processOutputReader.readLine()) != null) {
                strBuild.append(line).append(System.lineSeparator());
            }
            process.waitFor();
            log = strBuild.toString().trim();
            System.out.println(log);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return log;
    }

    public static Map<String, ArrayList<Map<String, String>>> parseLog(String log){
        Map<String, ArrayList<Map<String, String>>> map = new HashMap<>();
        map.put("video streams", new ArrayList<>());
        map.put("audio streams", new ArrayList<>());
        map.put("subtitle streams", new ArrayList<>());

        String[] lines = log.split(System.lineSeparator());
        for (String line : lines) {
            String strippedLine = line.strip();

            if (strippedLine.startsWith("Stream #")) {
                Map<String, String> streamInfo = new HashMap<>();

                if(strippedLine.endsWith(")")){
                    streamInfo.put("disposition", strippedLine.substring(strippedLine.lastIndexOf("(") + 1, strippedLine.length() - 1));
                    strippedLine = strippedLine.substring(0, strippedLine.lastIndexOf("("));
                }

                String infoString = strippedLine.substring(nthOccurrence(strippedLine, ":", 3) + 1).strip();
                if(infoString.contains(",")){
                    streamInfo.put("codec", infoString.substring(0, infoString.indexOf(",")).strip());
                }
                else {
                    streamInfo.put("codec", infoString);
                }
                String languageSection = strippedLine.substring(strippedLine.indexOf(":") + 1, nthOccurrence(strippedLine, ":", 2));
                if(languageSection.contains("(")){
                    String languageCode = languageSection.substring(languageSection.indexOf("(") + 1, languageSection.indexOf(")"));
                    Locale locale = Locale.forLanguageTag(languageCode.toUpperCase(Locale.ROOT));
                    streamInfo.put("language", locale.getDisplayLanguage());

                }
                else {
                    streamInfo.put("language", "Unknown");
                }



                String streamType = strippedLine.substring(nthOccurrence(strippedLine, ":", 2) + 1, nthOccurrence(strippedLine, ":", 3)).strip();
                switch (streamType) {
                    case "Video" -> map.get("video streams").add(streamInfo);
                    case "Audio" -> map.get("audio streams").add(streamInfo);
                    case "Subtitle" -> map.get("subtitle streams").add(streamInfo);
                }
            }
        }

        for(Map.Entry<String, ArrayList<Map<String, String>>> entry : map.entrySet()){
            System.out.println(entry.getKey());
            for(Map<String, String> entrymap : entry.getValue()){
                for(Map.Entry<String, String> entry2 : entrymap.entrySet()){
                    System.out.println(entry2.getKey() + ": " + entry2.getValue());
                }
            }
        }

        return map;
    }

    public static void extractSubtitles(MediaItem mediaItem){

        String subtitlesDirectory = System.getProperty("user.home").concat("/FXPlayer/subtitles/");
        try {
            Files.createDirectory(Paths.get(subtitlesDirectory));
        } catch (IOException ignored){
        }

        ArrayList<String> arguments = new ArrayList<>();
        String ffmpeg = Loader.load(org.bytedeco.ffmpeg.ffmpeg.class);

        arguments.add(ffmpeg);
        arguments.add("-i");
        arguments.add(mediaItem.getFile().getAbsolutePath());

        ArrayList<Map<String, String>> subtitleStreams = mediaItem.getLog().get("subtitle streams");

        for(int i = 0; i < subtitleStreams.size(); i++){
            arguments.add("-map");
            arguments.add("0:s:" + i);
            arguments.add(subtitlesDirectory.concat("sub" + i + ".srt"));
        }


        try {
            Process process = new ProcessBuilder(arguments).redirectErrorStream(true).start();
            StringBuilder strBuild = new StringBuilder();

            BufferedReader processOutputReader = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.defaultCharset()));
            String line;
            while ((line = processOutputReader.readLine()) != null) {
                strBuild.append(line).append(System.lineSeparator());
            }
            process.waitFor();
            String log = strBuild.toString().trim();
            System.out.println(log);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
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
