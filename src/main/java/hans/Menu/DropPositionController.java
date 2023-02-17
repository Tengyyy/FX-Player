package hans.Menu;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.util.Duration;

import java.util.ArrayList;

public class DropPositionController {

    QueueBox queueBox;

    int position = Integer.MAX_VALUE;
    double countingY = 0;

    PauseTransition dragTimer = new PauseTransition(Duration.millis(350));

    ArrayList<Transition> translateTransitions = new ArrayList<>();
    ArrayList<Transition> removeTransitions = new ArrayList<>();

    DropPositionController(QueueBox queueBox){
        this.queueBox = queueBox;


        dragTimer.setOnFinished(e -> {
            if(!queueBox.dragAndDropActive.get() && !queueBox.itemDragActive.get()) return;

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

        if(!translateTransitions.isEmpty()){
            for(Transition transition : translateTransitions) transition.stop();
            translateTransitions.clear();
        }


        ParallelTransition parallelTransition = new ParallelTransition();

        if(position >= queueBox.queue.size()){
            for(Node node : queueBox.getChildren()){
                if(node.getTranslateY() > 0){
                    TranslateTransition translateTransition = new TranslateTransition(Duration.millis(100), node);
                    translateTransition.setInterpolator(Interpolator.EASE_BOTH);
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
                    translateTransition.setInterpolator(Interpolator.EASE_BOTH);
                    translateTransition.setFromY(node.getTranslateY());
                    translateTransition.setToY(0);
                    parallelTransition.getChildren().add(translateTransition);
                }
                else if(i >= position && node.getTranslateY() < 90){
                    TranslateTransition translateTransition = new TranslateTransition(Duration.millis(100), node);
                    translateTransition.setInterpolator(Interpolator.EASE_BOTH);

                    translateTransition.setFromY(node.getTranslateY());
                    translateTransition.setToY(90);
                    parallelTransition.getChildren().add(translateTransition);
                }
            }
        }

        if(!parallelTransition.getChildren().isEmpty()){
            translateTransitions.add(parallelTransition);
            parallelTransition.setOnFinished(e -> translateTransitions.remove(parallelTransition));
            parallelTransition.playFromStart();
        }
    }

    public void setPosition(){
        if(position >= queueBox.queue.size()){
            for(Node node : queueBox.getChildren()){
                node.setTranslateY(0);
            }
        }
        else {
            for(int i=0; i < queueBox.getChildren().size(); i++){
                Node node = queueBox.getChildren().get(i);

                if(i < position)node.setTranslateY(0);
                else node.setTranslateY(90);
            }
        }

    }
}
