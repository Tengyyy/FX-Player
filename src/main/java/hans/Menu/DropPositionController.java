package hans.Menu;

import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.util.Duration;

import java.util.ArrayList;

public class DropPositionController {

    QueueBox queueBox;

    int position = Integer.MAX_VALUE;
    double countingY = 0;

    PauseTransition dragTimer = new PauseTransition(Duration.millis(350));

    ArrayList<Transition> transitions = new ArrayList<>();

    DropPositionController(QueueBox queueBox){
        this.queueBox = queueBox;


        dragTimer.setOnFinished(e -> {
            if(!queueBox.dragAndDropActive.get() || queueBox.dragBoardMedia.isEmpty()) return;

            animatePosition();
        });
    }

    public void updateY(double y){
        if(countingY == 0 || Math.abs(y - countingY) > 10){
            countingY = y;
            dragTimer.playFromStart();
        }
    }

    public void updatePosition(int position){
        this.position = position;
    }

    public void animatePosition(){

        for(Transition transition : transitions) transition.stop();
        transitions.clear();

        ParallelTransition parallelTransition = new ParallelTransition();

        if(position >= queueBox.queue.size()){
            for(Node node : queueBox.getChildren()){
                if(node.getTranslateY() > 0){
                    TranslateTransition translateTransition = new TranslateTransition(Duration.millis(100), node);
                    translateTransition.setFromY(node.getTranslateY());
                    translateTransition.setToY(0);
                    parallelTransition.getChildren().add(translateTransition);
                }
            }
        }
        else {
            for(int i=0; i < queueBox.getChildren().size(); i++){
                Node node = queueBox.getChildren().get(i);

                if(i < position && node.getTranslateY() > 0){
                    TranslateTransition translateTransition = new TranslateTransition(Duration.millis(100), node);
                    translateTransition.setFromY(node.getTranslateY());
                    translateTransition.setToY(0);
                    parallelTransition.getChildren().add(translateTransition);
                }
                else if(i >= position && node.getTranslateY() < 30){
                    TranslateTransition translateTransition = new TranslateTransition(Duration.millis(100), node);
                    translateTransition.setFromY(node.getTranslateY());
                    translateTransition.setToY(30);
                    parallelTransition.getChildren().add(translateTransition);
                }
            }
        }

        if(!parallelTransition.getChildren().isEmpty()){
            transitions.add(parallelTransition);
            parallelTransition.playFromStart();
        }
    }
}
