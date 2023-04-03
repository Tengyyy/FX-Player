package hans.Menu;

import hans.AnimationsClass;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.Background;
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

        if(queueBox.itemDragActive.get()){
            for (int i = 0; i < queueBox.draggedNode.videoIndex; i++) {
                QueueItem queueItem = queueBox.queue.get(queueBox.queueOrder.get(i));
                if(i < position && queueItem.getTranslateY() != 0){
                    TranslateTransition translateTransition = new TranslateTransition(Duration.millis(100), queueItem);
                    translateTransition.setFromY(queueItem.getTranslateY());
                    translateTransition.setToY(0);
                    parallelTransition.getChildren().add(translateTransition);
                }
                else if(i >= position && queueItem.getTranslateY() != QueueItem.height) {
                    TranslateTransition translateTransition = new TranslateTransition(Duration.millis(100), queueItem);
                    translateTransition.setFromY(queueItem.getTranslateY());
                    translateTransition.setToY(QueueItem.height);
                    parallelTransition.getChildren().add(translateTransition);
                }
            }

            for (int i = queueBox.draggedNode.videoIndex + 1; i < queueBox.queue.size(); i++) {
                QueueItem queueItem = queueBox.queue.get(queueBox.queueOrder.get(i));
                if(i < position && queueItem.getTranslateY() != -QueueItem.height){
                    TranslateTransition translateTransition = new TranslateTransition(Duration.millis(100), queueItem);
                    translateTransition.setFromY(queueItem.getTranslateY());
                    translateTransition.setToY(-QueueItem.height);
                    parallelTransition.getChildren().add(translateTransition);
                }
                else if(i >= position && queueItem.getTranslateY() != 0){
                    TranslateTransition translateTransition = new TranslateTransition(Duration.millis(100), queueItem);
                    translateTransition.setFromY(queueItem.getTranslateY());
                    translateTransition.setToY(0);
                    parallelTransition.getChildren().add(translateTransition);
                }
            }
        }
        else if(queueBox.dragAndDropActive.get()){
            if(position >= queueBox.queue.size()){
                for(QueueItem queueItem : queueBox.queue){
                    if(queueItem.getTranslateY() != 0){
                        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(100), queueItem);
                        translateTransition.setFromY(queueItem.getTranslateY());
                        translateTransition.setToY(0);
                        parallelTransition.getChildren().add(translateTransition);
                    }
                }
            }
            else {
                for(int i=0; i < queueBox.queue.size(); i++){
                    Node node = queueBox.queue.get(queueBox.queueOrder.get(i));

                    if(i < position && node.getTranslateY() > 0){
                        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(100), node);
                        translateTransition.setFromY(node.getTranslateY());
                        translateTransition.setToY(0);
                        parallelTransition.getChildren().add(translateTransition);
                    }
                    else if(i >= position && node.getTranslateY() < QueueItem.height){
                        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(100), node);

                        translateTransition.setFromY(node.getTranslateY());
                        translateTransition.setToY(QueueItem.height);
                        parallelTransition.getChildren().add(translateTransition);
                    }
                }
            }
        }

        if(!parallelTransition.getChildren().isEmpty()){

            if(queueBox.dragAndDropActive.get() && queueBox.getPadding().getBottom() == 20){
                Animation animation = new Transition() {
                    {
                        setCycleDuration(Duration.millis(100));
                    }

                    @Override
                    protected void interpolate(double progress) {
                        queueBox.setPadding(new Insets(0, 0, 20 + queueBox.queue.get(queueBox.queueOrder.get(queueBox.queueOrder.size() - 1)).getTranslateY(), 0));
                    }
                };

                parallelTransition.getChildren().add(animation);
            }

            translateTransitions.add(parallelTransition);
            parallelTransition.setOnFinished(e -> translateTransitions.remove(parallelTransition));
            parallelTransition.playFromStart();
        }
    }
}
