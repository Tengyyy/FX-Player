package hans;

import javafx.scene.layout.StackPane;

public class QueueLine extends StackPane {

    QueueBox queueBox;


    QueueLine(QueueBox queueBox){

        this.queueBox = queueBox;

        this.setMinHeight(3);
        this.setPrefHeight(3);
        this.setMaxHeight(3);

        this.setId("queueLine");
    }

    public void setPosition(int index){
        queueBox.getChildren().remove(this);

        if(index == -1) queueBox.getChildren().add(this);
        else queueBox.getChildren().add(index, this);
    }

}
