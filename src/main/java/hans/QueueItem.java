package hans;


import com.jfoenix.controls.JFXButton;
import javafx.scene.control.Button;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;

public class QueueItem extends GridPane {

    // layout constraints for the video item
    ColumnConstraints column1 = new ColumnConstraints(80,80,80);
    ColumnConstraints column2 = new ColumnConstraints(0,100,Double.MAX_VALUE);
    ColumnConstraints column3 = new ColumnConstraints(64,64,64);

    Button playButton = new Button();
    Text videoTitle = new Text();
    JFXButton optionsButton = new JFXButton();

    QueueItem(){
        column2.setHgrow(Priority.ALWAYS); // makes the middle column (video title text) to take up all available space
        this.getColumnConstraints().addAll(column1, column2, column3);
    }



}
