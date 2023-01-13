package hans;

import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class DurationTrack {

    public ProgressBar progressBar;

    double startTime;
    double endTime;

    public DurationTrack(double startTime, double endTime){
        this.startTime = startTime;
        this.endTime = endTime;

        progressBar = new ProgressBar(0);
        HBox.setHgrow(progressBar, Priority.ALWAYS);
        progressBar.setPrefWidth(600);
        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.setPrefHeight(4);
        progressBar.setMaxHeight(4);
        progressBar.setFocusTraversable(false);
        progressBar.getStyleClass().add("durationTrack");
    }


    public void bindWidth(HBox container, double value){
        progressBar.prefWidthProperty().bind(container.widthProperty().multiply(value));
    }
}
