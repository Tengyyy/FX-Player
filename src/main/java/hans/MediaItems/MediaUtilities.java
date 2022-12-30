package hans.MediaItems;


import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.ffmpeg.*;
import com.github.kokorin.jaffree.ffprobe.FFprobeResult;
import com.github.kokorin.jaffree.ffprobe.Stream;
import javafx.scene.image.Image;
import javafx.util.Pair;

import java.io.*;

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
                    .addInput(UrlInput.fromUrl(file.getAbsolutePath()))
                    .addArguments("-map", "0:" + coverIndex + "?")
                    .addArguments("-map_metadata", "-1")
                    .addArguments("-frames:v", "1")
                    .addArguments("-update", "1")
                    .addOutput(
                            PipeOutput.pumpTo(outputStream)
                                .setFormat("image2")
                                .setCodec("0", "png")
                    )
                    .execute();

        }
        else if(defaultVideoIndex != -1){
            Long duration = ffProbeResult.getStreams().get(defaultVideoIndex).getDuration(TimeUnit.MILLISECONDS);
            if(duration == null) return new Pair<>(false, null);

            FFmpeg.atPath()
                    .addInput(UrlInput.fromUrl(file.getAbsolutePath())
                            .setPosition(duration/2, TimeUnit.MILLISECONDS)
                    )
                    .addArguments("-map", "0:" + defaultVideoIndex + "?")
                    .addArguments("-map_metadata", "-1")
                    .addArguments("-frames:v", "1")
                    .addArguments("-update", "1")
                    .addOutput(
                            PipeOutput.pumpTo(outputStream)
                                    .setFormat("image2")
                                    .setCodec("0", "png")
                    )
                    .execute();
        }
        else {
            Long duration = ffProbeResult.getStreams().get(firstVideoIndex).getDuration(TimeUnit.MILLISECONDS);
            if(duration == null) return new Pair<>(false, null);

            FFmpeg.atPath()
                    .addInput(UrlInput.fromUrl(file.getAbsolutePath())
                            .setPosition(duration/2, TimeUnit.MILLISECONDS)
                    )
                    .addArguments("-map", "0:" + firstVideoIndex + "?")
                    .addArguments("-map_metadata", "-1")
                    .addArguments("-frames:v", "1")
                    .addArguments("-update", "1")
                    .addOutput(
                            PipeOutput.pumpTo(outputStream)
                                    .setFormat("image2")
                                    .setCodec("0", "png")
                    )
                    .execute();
        }

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());


        Image cover = new Image(inputStream);

        try {
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return new Pair<>(hasCover, cover);
    }
}























