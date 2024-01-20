package tengy.mediaItems;


import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.ffmpeg.*;
import com.github.kokorin.jaffree.ffprobe.FFprobeResult;
import com.github.kokorin.jaffree.ffprobe.Stream;
import tengy.Utilities;
import io.github.palexdev.materialfx.utils.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javafx.util.Pair;
import org.bytedeco.javacpp.Loader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class MediaUtilities {

    static String probePath = Loader.load(org.bytedeco.ffmpeg.ffprobe.class);
    public static final String FFPROBE_PATH = probePath.substring(0, probePath.length() - 12);

    static String ffmpegPath = Loader.load(org.bytedeco.ffmpeg.ffmpeg.class);
    public static final String FFMPEG_PATH = ffmpegPath.substring(0, ffmpegPath.length() - 11);

    public static final Set<String> mediaFormats = Set.of("mp4", "avi", "mkv", "flv", "mov", "mp3", "flac", "wav", "ogg", "opus", "aiff", "m4a", "wma", "aac", "wmv", "mka", "webm");



    public static Pair<Boolean, Image> getCover(FFprobeResult ffProbeResult, File file) {

        boolean hasCover = false;

        int coverIndex = -1;
        int defaultVideoIndex = -1;
        int firstVideoIndex = -1;

        for (Stream stream : ffProbeResult.getStreams()) {

            if (stream.getDisposition().getAttachedPic() == 1 && stream.getCodecType() == StreamType.VIDEO) {
                coverIndex = stream.getIndex();
                break;
            }
            else if (stream.getDisposition().getDefault() == 1 && stream.getCodecType() == StreamType.VIDEO) {
                defaultVideoIndex = stream.getIndex();
            }
            else if (stream.getCodecType() == StreamType.VIDEO) {
                firstVideoIndex = stream.getIndex();
            }
        }

        if (coverIndex == -1 && defaultVideoIndex == -1 && firstVideoIndex == -1) return new Pair<>(false, null);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();


        if (coverIndex != -1) {
            // extract image from stream with attached pic disposition

            hasCover = true;

            FFmpeg.atPath(Paths.get(FFMPEG_PATH))
                    .addInput(UrlInput.fromUrl(file.getAbsolutePath())
                    )
                    .addArguments("-map", "0:" + coverIndex + "?")
                    .addArguments("-map_metadata", "-1")
                    .addArguments("-frames:v", "1")
                    .addArguments("-update", "1")
                    .addArguments("-c", "copy")
                    .addOutput(
                            PipeOutput.pumpTo(outputStream)
                                    .setFormat("image2")
                    )
                    .execute();

        }
        else if(defaultVideoIndex != -1){
            Long duration = ffProbeResult.getStreams().get(defaultVideoIndex).getDuration(TimeUnit.MILLISECONDS);
            if(duration == null){
                Float durationFloat = ffProbeResult.getFormat().getDuration();
                if(durationFloat != null) duration = durationFloat.longValue() * 1000;
            }
            if(duration == null) return new Pair<>(false, null);
            else{
                grabFrame(file, defaultVideoIndex, duration/2, outputStream);
            }

        }
        else {
            Long duration = ffProbeResult.getStreams().get(firstVideoIndex).getDuration(TimeUnit.MILLISECONDS);
            if(duration == null){
                Float durationFloat = ffProbeResult.getFormat().getDuration();
                if(durationFloat != null) duration = durationFloat.longValue() * 1000;
            }
            if(duration == null) return new Pair<>(false, null);
            else {
                grabFrame(file, firstVideoIndex, duration/2, outputStream);
            }
        }


        Image cover = null;
        ByteArrayInputStream inputStream = null;
        if(outputStream.size() > 0){
            inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            cover = new Image(inputStream);
        }



        try {
            outputStream.close();
            if(inputStream != null) inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(cover != null)
            return new Pair<>(hasCover, cover);
        else
            return new Pair<>(false, null);
    }

    public static Image getVideoFrame(File file, int stream, long positionInMillis){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        grabFrame(file, stream, positionInMillis, outputStream);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        Image cover = new Image(inputStream);

        try {
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return cover;
    }

    public static boolean createFileWithUpdatedMetadata(MediaItem mediaItem, File outputFile) {
        File file = mediaItem.getFile();
        Map<String, String> metadata = mediaItem.newMetadata;
        boolean hasCover = mediaItem.hasCover;
        Image oldCover = mediaItem.cover;
        File newCover = mediaItem.newCoverFile;
        boolean coverRemoved = mediaItem.coverRemoved;
        Duration duration = mediaItem.duration;

        int numberOfAttachmentStreams = mediaItem.attachmentStreams.size();
        int numberOfVideoStreams = 0;

        for(Stream stream : mediaItem.videoStreams){
            if(stream.getDisposition().getAttachedPic() == 0) numberOfVideoStreams++;
        }

        String extension = Utilities.getFileExtension(file);
        File currentImageFile = null;

        FFmpeg fFmpeg = FFmpeg.atPath(Paths.get(FFMPEG_PATH))
                .addInput(UrlInput.fromUrl(file.getAbsolutePath()));

        if(newCover != null || coverRemoved){
            if(newCover != null){
                if(extension.equals("mkv")){
                    fFmpeg.addArguments("-attach", newCover.getAbsolutePath());
                }
                else {
                    fFmpeg.addInput(UrlInput.fromUrl(newCover.getAbsolutePath()));
                }
            }

            fFmpeg.addArguments("-map", "0:V?");
        }
        else {
            if(hasCover && oldCover != null && extension.equals("mkv")){
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(oldCover, null);

                String picturePath = file.getParent() + "/" + new SimpleDateFormat("dd-MM-yyyy HH-mm-ss").format(new Date()) + ".png";
                File pictureFile = new File(picturePath);

                try {
                    ImageIO.write(bufferedImage, "png",  pictureFile);

                    currentImageFile = pictureFile;

                    fFmpeg.addArguments("-attach", picturePath);
                    fFmpeg.addArguments("-metadata:s:t:" + numberOfAttachmentStreams, "mimetype=image/png");
                    fFmpeg.addArguments("-metadata:s:t:" + numberOfAttachmentStreams, "filename=cover.png");

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(extension.equals("mkv")) fFmpeg.addArguments("-map", "0:V?");
            else fFmpeg.addArguments("-map", "0:v?");
        }

        fFmpeg.addArguments("-map", "0:a?")
                .addArguments("-map", "0:s?")
                .addArguments("-map", "0:t?");

        if(newCover != null && !extension.equals("mkv")) fFmpeg.addArguments("-map", "1");

        if(extension.equals("ogg")){
            fFmpeg.addArguments("-map_metadata", "-1");
        }
        else fFmpeg.addArguments("-map_metadata:g", "-1");

        if(!metadata.isEmpty()){
            if(extension.equals("ogg") || extension.equals("opus")){
                for(Map.Entry<String, String> entry : metadata.entrySet()){
                    fFmpeg.addArguments("-metadata:s", entry.getKey() + "=" + entry.getValue());
                }
            }
            else {
                for(Map.Entry<String, String> entry : metadata.entrySet()){
                    fFmpeg.addArguments("-metadata", entry.getKey() + "=" + entry.getValue());
                }
            }

            if(extension.equals("wav")){
                fFmpeg.addArguments("-id3v2_version", "3");
            }
            else if(extension.equals("aiff") || extension.equals("aac")){
                fFmpeg.addArguments("-write_id3v2", "1");
            }
        }

        if(extension.equals("mp4") || extension.equals("mov") || extension.equals("m4a")) fFmpeg.addArguments("-movflags", "faststart");
        fFmpeg.addArguments("-c", "copy");


        //arguments.add("-bsf:v"); //fix avi files with h264 video codec
        //arguments.add("h264_mp4toannexb");
        //arguments.add("-bsf:v"); // fix avi files with hevc codec
        //arguments.add("hevc_mp4toannexb");

        if(newCover != null){
            if(extension.equals("mp4") || extension.equals("mov") || extension.equals("flac") || extension.equals("mp3") || extension.equals("m4a") || extension.equals("aiff") || extension.equals("wma")){
                fFmpeg.addArguments("-metadata:s:v:"+numberOfVideoStreams, "title=Album cover");
                fFmpeg.addArguments("-metadata:s:v:"+numberOfVideoStreams, "comment=Cover (front)");
                fFmpeg.addArguments("-disposition:v:"+numberOfVideoStreams, "attached_pic");
            }

            else if(extension.equals("mkv")){
                if(Utilities.getFileExtension(newCover).equals("png")){
                    fFmpeg.addArguments("-metadata:s:t:" + numberOfAttachmentStreams, "mimetype=image/png");
                    fFmpeg.addArguments("-metadata:s:t:" + numberOfAttachmentStreams, "filename=cover.png");
                }
                else {
                    fFmpeg.addArguments("-metadata:s:t:" + numberOfAttachmentStreams, "mimetype=image/jpeg");
                    fFmpeg.addArguments("-metadata:s:t:" + numberOfAttachmentStreams, "filename=cover.jpg");
                }
            }
        }

        fFmpeg.addOutput(UrlOutput.toUrl(outputFile.getAbsolutePath()));

        FFmpegResult fFmpegResult = fFmpeg.execute();

        if(currentImageFile != null && currentImageFile.exists()) currentImageFile.delete();

        return fFmpegResult.getAudioSize() > 0 || fFmpegResult.getVideoSize() > 0;
    }

    public static boolean updateMetadata(MediaItem mediaItem){

        File file = mediaItem.getFile();
        String extension = Utilities.getFileExtension(file);

        String outputPath = file.getParent() + "/" + new SimpleDateFormat("dd-MM-yyyy HH-mm-ss").format(new Date()) + "." + (extension.equals("mov") ? "mp4" : extension);
        File tempFile = new File(outputPath);

        boolean editSuccess = createFileWithUpdatedMetadata(mediaItem, tempFile);
        boolean success = false;

        if(editSuccess){
            try {
                boolean deleteSuccess = file.delete();
                if(deleteSuccess){
                    boolean renameSuccess = tempFile.renameTo(file);
                    if(!renameSuccess) throw new IOException("Failed to rename new file");

                    success = true;
                }
                else throw new IOException("Failed to delete old file");
            } catch (IOException e){
                e.printStackTrace();
            }
        }
        else {
            if(tempFile.exists()) tempFile.delete();
        }

        return success;
    }


    public static File extractMetadata(MediaItem mediaItem){

        String outputPath = mediaItem.file.getParent() + "/" + new SimpleDateFormat("dd-MM-yyyy HH-mm-ss").format(new Date()) + ".txt";
        File outputFile = new File(outputPath);

        FFmpeg.atPath(Paths.get(FFMPEG_PATH))
                .addInput(UrlInput.fromUrl(mediaItem.file.getAbsolutePath()))
                .addArguments("-f", "ffmetadata")
                .addOutput(UrlOutput.toUrl(outputPath))
                .execute();

        return outputFile;
    }

    public static boolean applyChapters(MediaItem mediaItem, File output) throws IOException {

        boolean success = false;
        File metadataFile = extractMetadata(mediaItem);

        if(metadataFile.exists()) {

            Path path = metadataFile.toPath();

            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            List<String> linesWithoutChapters = new ArrayList<>();

            boolean insideChapter = false;
            for (String line : lines) {
                if (line.equals("[CHAPTER]")) {
                    insideChapter = true;
                } else if (!line.isEmpty() && line.charAt(0) == '[') {
                    insideChapter = false;
                    linesWithoutChapters.add(line);
                } else if (!insideChapter) {
                    linesWithoutChapters.add(line);
                }
            }


            Files.deleteIfExists(path);
            Files.createFile(path);
            for (String line : linesWithoutChapters) {
                Files.writeString(path, line + System.lineSeparator(),
                        StandardOpenOption.APPEND);
            }

            for (int i = 0; i < mediaItem.newChapters.size(); i++) {

                Chapter chapter = mediaItem.newChapters.get(i);

                Files.writeString(path, "[CHAPTER]" + System.lineSeparator(), StandardOpenOption.APPEND);
                Files.writeString(path, "TIMEBASE=1/1000000000" + System.lineSeparator(), StandardOpenOption.APPEND);
                Files.writeString(path, "START=" + (long) chapter.startTime.toMillis() + "000000" + System.lineSeparator(), StandardOpenOption.APPEND);
                Duration endTime;
                if (i < mediaItem.newChapters.size() - 1) {
                    Chapter nextChapter = mediaItem.newChapters.get(i + 1);
                    endTime = nextChapter.startTime.subtract(Duration.millis(1));
                } else endTime = mediaItem.duration;

                Files.writeString(path, "END=" + (long) endTime.toMillis() + "000000" + System.lineSeparator(), StandardOpenOption.APPEND);
                Files.writeString(path, "title=" + chapter.title + System.lineSeparator(), StandardOpenOption.APPEND);
            }

            success = applyFFMetadataFile(mediaItem, metadataFile, output);
        }

        return success;
    }

    public static boolean updateChapters(MediaItem mediaItem) throws IOException {

        boolean success = false;
        File file = mediaItem.getFile();
        String extension = Utilities.getFileExtension(file);

        String outputPath = file.getParent() + "/" + new SimpleDateFormat("dd-MM-yyyy HH-mm-ss").format(new Date()) + "." + (extension.equals("mov") ? "mp4" : extension);
        File tempFile = new File(outputPath);

        boolean ffMetadataSuccess = applyChapters(mediaItem, tempFile);
        if (ffMetadataSuccess) {
            try {
                boolean deleteSuccess = file.delete();
                if (deleteSuccess) {
                    boolean renameSuccess = tempFile.renameTo(file);
                    if (!renameSuccess) throw new IOException("Failed to rename new file");

                    success = true;
                } else throw new IOException("Failed to delete old file");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (tempFile.exists()) tempFile.delete();
        }

        return success;
    }

    public static boolean applyFFMetadataFile(MediaItem mediaItem, File metadataFile, File outputFile) {
        File file = mediaItem.getFile();
        String extension = Utilities.getFileExtension(file);

        boolean hasCover = mediaItem.hasCover;
        Image cover = mediaItem.cover;

        File currentImageFile = null;

        FFmpeg fFmpeg = FFmpeg.atPath(Paths.get(FFMPEG_PATH))
                .addInput(UrlInput.fromUrl(file.getAbsolutePath()))
                .addInput(UrlInput.fromUrl(metadataFile.getAbsolutePath()));

        if(hasCover && cover != null && extension.equals("mkv")){
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(cover, null);

            String picturePath = file.getParent() + "/" + new SimpleDateFormat("dd-MM-yyyy HH-mm-ss").format(new Date()) + ".png";
            currentImageFile = new File(picturePath);

            try {
                ImageIO.write(bufferedImage, "png",  currentImageFile);

                int numberOfAttachmentStreams = mediaItem.attachmentStreams.size();

                fFmpeg.addArguments("-attach", picturePath);
                fFmpeg.addArguments("-metadata:s:t:" + numberOfAttachmentStreams, "mimetype=image/png");
                fFmpeg.addArguments("-metadata:s:t:" + numberOfAttachmentStreams, "filename=cover.png");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(extension.equals("mkv")) fFmpeg.addArguments("-map", "0:V?");
        else fFmpeg.addArguments("-map", "0:v?");

        fFmpeg.addArguments("-map", "0:a?")
                .addArguments("-map", "0:s?")
                .addArguments("-map", "0:t?")
                .addArguments("-map_metadata", "1")
                .addArguments("-map_chapters", "1")
                .addArguments("-c", "copy")
                .addOutput(UrlOutput.toUrl(outputFile.getAbsolutePath()));

        FFmpegResult fFmpegResult = fFmpeg.execute();

        if(currentImageFile != null && currentImageFile.exists()) currentImageFile.delete();
        if(metadataFile.exists()) metadataFile.delete();

        return fFmpegResult.getAudioSize() > 0 || fFmpegResult.getVideoSize() > 0;
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


    public static void grabFrame(File file, int streamIndex, long positionInMillis, OutputStream outputStream){

        FFmpeg.atPath(Paths.get(FFMPEG_PATH))
                .addInput(UrlInput.fromUrl(file.getAbsolutePath())
                        .setPosition(positionInMillis, TimeUnit.MILLISECONDS)
                )
                .addArguments("-map", "0:" + streamIndex + "?")
                .addArguments("-map_metadata", "-1")
                .addArguments("-frames:v", "1")
                .addArguments("-update", "1")
                .addArguments("-c", "copy")
                .addOutput(
                        PipeOutput.pumpTo(outputStream)
                                .setFormat("image2")
                                .setCodec("0", "png")
                )
                .execute();
    }

    public static void grabScaledFrame(File file, int streamIndex, long positionInMillis, OutputStream outputStream, double width, double height){
        FFmpeg.atPath(Paths.get(FFMPEG_PATH))
                .addInput(UrlInput.fromUrl(file.getAbsolutePath())
                        .setPosition(positionInMillis, TimeUnit.MILLISECONDS)
                )
                .addArguments("-map", "0:" + streamIndex + "?")
                .addArguments("-map_metadata", "-1")
                .addArguments("-frames:v", "1")
                .addArguments("-update", "1")
                .addArguments("-c", "copy")
                .addArguments("-vf", "scale=w=%f:h=%f:force_original_aspect_ratio=decrease".formatted(width, height))
                .addOutput(
                        PipeOutput.pumpTo(outputStream)
                                .setFormat("image2")
                                .setCodec("0", "png")
                )
                .execute();
    }
}























