package hans;

import javafx.concurrent.Task;
import javafx.scene.image.Image;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.JavaFXFrameConverter;

public class FrameGrabberTask extends Task<Image> {

    FFmpegFrameGrabber fFmpegFrameGrabber;
    double time;


    FrameGrabberTask(FFmpegFrameGrabber fFmpegFrameGrabber, double time){
        this.fFmpegFrameGrabber = fFmpegFrameGrabber;
        this.time = time;
    }


    @Override
    protected Image call() throws Exception {
        fFmpegFrameGrabber.setFrameNumber((int) (fFmpegFrameGrabber.getLengthInFrames() * time));
        Frame frame = fFmpegFrameGrabber.grabImage();
        JavaFXFrameConverter javaFXFrameConverter = new JavaFXFrameConverter();
        return javaFXFrameConverter.convert(frame);
    }
}