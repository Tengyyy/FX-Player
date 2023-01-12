package hans.MediaItems;


import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.ffmpeg.*;
import com.github.kokorin.jaffree.ffprobe.FFprobeResult;
import com.github.kokorin.jaffree.ffprobe.Stream;
import hans.Utilities;
import io.github.palexdev.materialfx.utils.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javafx.util.Pair;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MediaUtilities {


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

            FFmpeg.atPath()
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
            Long duration = ffProbeResult.getStreams().get(defaultVideoIndex).getDuration(TimeUnit.SECONDS);
            if(duration == null){
                Float durationFloat = ffProbeResult.getFormat().getDuration();
                if(durationFloat != null) duration = durationFloat.longValue();
            }
            if(duration == null) return new Pair<>(false, null);
            else{
                grabFrame(file, defaultVideoIndex, duration/2, outputStream);
            }

        }
        else {
            Long duration = ffProbeResult.getStreams().get(firstVideoIndex).getDuration(TimeUnit.SECONDS);
            if(duration == null){
                Float durationFloat = ffProbeResult.getFormat().getDuration();
                if(durationFloat != null) duration = durationFloat.longValue();
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

    public static boolean updateMetadata(File file, Map<String, String> metadata, boolean hasCover, Image oldCover, File newCover, boolean coverRemoved, int videoStreams, int attachmentStreams, Duration duration){

        boolean success = false;

        String extension = Utilities.getFileExtension(file);
        File currentImageFile = null;

        FFmpeg fFmpeg = FFmpeg.atPath()
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
                String outputPath = file.getParent() + "/" + new SimpleDateFormat("dd-MM-yyyy HH-mm-ss").format(new Date()) + ".png";
                File outputFile = new File(outputPath);

                try {
                    ImageIO.write(bufferedImage, "png",  outputFile);

                    currentImageFile = outputFile;

                    fFmpeg.addArguments("-attach", outputPath);
                    fFmpeg.addArguments("-metadata:s:t:" + attachmentStreams, "mimetype=image/png");
                    fFmpeg.addArguments("-metadata:s:t:" + attachmentStreams, "filename=cover.png");

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
                fFmpeg.addArguments("-metadata:s:v:"+videoStreams, "title=Album cover");
                fFmpeg.addArguments("-metadata:s:v:"+videoStreams, "comment=Cover (front)");
                fFmpeg.addArguments("-disposition:v:"+videoStreams, "attached_pic");
            }

            else if(extension.equals("mkv")){
                if(Utilities.getFileExtension(newCover).equals("png")){
                    fFmpeg.addArguments("-metadata:s:t:" + attachmentStreams, "mimetype=image/png");
                    fFmpeg.addArguments("-metadata:s:t:" + attachmentStreams, "filename=cover.png");
                }
                else {
                    fFmpeg.addArguments("-metadata:s:t:" + attachmentStreams, "mimetype=image/jpeg");
                    fFmpeg.addArguments("-metadata:s:t:" + attachmentStreams, "filename=cover.jpg");
                }
            }

        }

        if(duration != null){
            fFmpeg.setProgressListener(progress -> {
                double percentage = 100. * progress.getTimeMillis() / duration.toMillis();
                int roundPercentage = (int) Math.min(Math.max(percentage, 0), 100);
                System.out.println("Progress: " + roundPercentage + "%");
            });
        }

        String outputPath = file.getParent() + "/" + new SimpleDateFormat("dd-MM-yyyy HH-mm-ss").format(new Date()) + "." + (extension.equals("mov") ? "mp4" : extension);
        File tempFile = new File(outputPath);

        fFmpeg.addOutput(UrlOutput.toUrl(outputPath));
        FFmpegResult fFmpegResult = fFmpeg.execute();

        if(fFmpegResult.getVideoSize() > 0 || fFmpegResult.getAudioSize() > 0){
            try {
                boolean deleteSuccess = file.delete();
                if(deleteSuccess){
                    boolean renameSuccess = tempFile.renameTo(file);
                    if(!renameSuccess)throw new IOException("Failed to rename new file");

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

        if(currentImageFile != null && currentImageFile.exists()) currentImageFile.delete();

        return success;
    }

    public static void extractSubtitles(MediaItem mediaItem, String filePrefix){

        String subtitlesDirectory = System.getProperty("user.home").concat("/FXPlayer/subtitles/");
        try {
            Files.createDirectory(Paths.get(subtitlesDirectory));
        } catch (IOException ignored){
        }


        FFmpeg fFmpeg = FFmpeg.atPath()
                .addInput(UrlInput.fromUrl(mediaItem.getFile().getAbsolutePath()));

        for(int i =0; i < mediaItem.numberOfSubtitleStreams; i++){
            fFmpeg.addArguments("-map", "0:s:" + i);
            fFmpeg.addOutput(UrlOutput.toUrl(subtitlesDirectory.concat(filePrefix + i + ".srt")));
        }

        fFmpeg.execute();
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

        FFmpeg.atPath()
                .addInput(UrlInput.fromUrl(file.getAbsolutePath())
                        .setPosition(positionInMillis, TimeUnit.SECONDS)
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
}























