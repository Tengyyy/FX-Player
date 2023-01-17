package hans;

import javafx.concurrent.Task;
import javafx.scene.image.Image;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.JavaFXFrameConverter;

public class FrameGrabberTask extends Task<Image> {

    FFmpegFrameGrabber fFmpegFrameGrabber;
    ControlBarController controlBarController;


    FrameGrabberTask(FFmpegFrameGrabber fFmpegFrameGrabber, ControlBarController controlBarController){
        this.fFmpegFrameGrabber = fFmpegFrameGrabber;
        this.controlBarController = controlBarController;
    }


    @Override
    protected Image call() throws Exception {

        fFmpegFrameGrabber.setFrameNumber((int) (fFmpegFrameGrabber.getLengthInFrames() * controlBarController.lastKnownSliderHoverPosition));
        Frame frame = fFmpegFrameGrabber.grabImage();
        JavaFXFrameConverter javaFXFrameConverter = new JavaFXFrameConverter();
        return javaFXFrameConverter.convert(frame);

    }
}