package hans.Menu;

import hans.*;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;


public class QueueBox extends VBox {

    MenuController menuController;


    ArrayList<File> dragBoardFiles = new ArrayList<>();
    ArrayList<File> dragBoardMedia = new ArrayList<>();


    boolean dragAndDropActive = false;
    public boolean dragActive = false;

    public QueueItem draggedNode;
    QueueLine queueLine;

    public ObjectProperty<QueueItem> activeItem = new SimpleObjectProperty<>(null);
    public ObservableList<QueueItem> queue = FXCollections.observableArrayList();


    QueueBox(MenuController menuController){
        this.menuController = menuController;
        this.setAlignment(Pos.TOP_CENTER);
        this.setFillWidth(true);


        VBox.setVgrow(this, Priority.ALWAYS);
        this.setPadding(new Insets(0, 0, 100, 0));


        this.setOnDragEntered(this::handleDragEntered);
        this.setOnDragOver(this::handleDragOver);
        this.setOnDragDropped(this::handleDragDropped);
        this.setOnDragExited(this::handleDragExited);

        queue.addListener((ListChangeListener<QueueItem>) change -> {

            menuController.clearQueueButton.setDisable(queue.isEmpty());

            for(QueueItem queueItem : queue){
                queueItem.updateIndex(queue.indexOf(queueItem));
            }

            menuController.controlBarController.updateNextAndPreviousVideoButtons();
        });

        activeItem.addListener((observableValue, queueItem, t1) -> menuController.controlBarController.updateNextAndPreviousVideoButtons());



        this.setOnMouseDragOver((e) -> {

            if(!dragActive) return;

            if((e.getY() - draggedNode.dragPosition) <= 0) draggedNode.setTranslateY(-draggedNode.minimumY);
            else if((e.getY() - draggedNode.dragPosition) > (draggedNode.maximumY)) draggedNode.setTranslateY(draggedNode.maximumY - draggedNode.minimumY);
            else draggedNode.setTranslateY(e.getY() - draggedNode.minimumY - draggedNode.dragPosition);

            draggedNode.runningTranslate = draggedNode.getTranslateY() - (draggedNode.newPosition - draggedNode.videoIndex) * QueueItem.height;

            if(draggedNode.runningTranslate >= QueueItem.height){
                do {
                    if(queue.get(draggedNode.newPosition).getTranslateY() > 0 && !queue.get(draggedNode.newPosition).equals(draggedNode)){
                        QueueItem queueItem = queue.get(draggedNode.newPosition);
                        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(AnimationsClass.ANIMATION_SPEED), queueItem);
                        translateTransition.setInterpolator(Interpolator.EASE_OUT);
                        translateTransition.setFromY(queueItem.getTranslateY());
                        translateTransition.setToY(0);
                        translateTransition.play();
                    }
                    else {
                        QueueItem queueItem = queue.get(draggedNode.newPosition + 1);
                        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(AnimationsClass.ANIMATION_SPEED), queueItem);
                        translateTransition.setInterpolator(Interpolator.EASE_OUT);
                        translateTransition.setFromY(queueItem.getTranslateY());
                        translateTransition.setToY(-QueueItem.height);
                        translateTransition.play();
                    }


                    draggedNode.newPosition+=1;
                    draggedNode.runningTranslate = draggedNode.getTranslateY() - (draggedNode.newPosition - draggedNode.videoIndex) * QueueItem.height;
                }
                while(draggedNode.runningTranslate >= QueueItem.height);
            }
            else if(draggedNode.runningTranslate <= -QueueItem.height){
                do {
                    if(queue.get(draggedNode.newPosition).getTranslateY() < 0 && !queue.get(draggedNode.newPosition).equals(draggedNode)){

                        QueueItem queueItem = queue.get(draggedNode.newPosition);

                        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(AnimationsClass.ANIMATION_SPEED), queueItem);
                        translateTransition.setInterpolator(Interpolator.EASE_OUT);
                        translateTransition.setFromY(queueItem.getTranslateY());
                        translateTransition.setToY(0);
                        translateTransition.play();
                    }
                    else {
                        QueueItem queueItem = queue.get(draggedNode.newPosition - 1);
                        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(AnimationsClass.ANIMATION_SPEED), queueItem);
                        translateTransition.setInterpolator(Interpolator.EASE_OUT);
                        translateTransition.setFromY(queueItem.getTranslateY());
                        translateTransition.setToY(QueueItem.height);
                        translateTransition.play();
                    }


                    draggedNode.newPosition-=1;
                    draggedNode.runningTranslate = draggedNode.getTranslateY() - (draggedNode.newPosition - draggedNode.videoIndex) * QueueItem.height;
                }
                while(draggedNode.runningTranslate <= -QueueItem.height);
            }
            else {
                if(draggedNode.getTranslateY() == -draggedNode.minimumY && draggedNode.newPosition != 0){

                    if(queue.get(draggedNode.newPosition).getTranslateY() < 0 && !queue.get(draggedNode.newPosition).equals(draggedNode)){

                        QueueItem queueItem = queue.get(draggedNode.newPosition);

                        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(AnimationsClass.ANIMATION_SPEED), queueItem);
                        translateTransition.setInterpolator(Interpolator.EASE_OUT);
                        translateTransition.setFromY(queueItem.getTranslateY());
                        translateTransition.setToY(0);
                        translateTransition.play();
                    }
                    else {

                        QueueItem queueItem = queue.get(draggedNode.newPosition - 2);

                        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(AnimationsClass.ANIMATION_SPEED), queueItem);
                        translateTransition.setInterpolator(Interpolator.EASE_OUT);
                        translateTransition.setFromY(queueItem.getTranslateY());
                        translateTransition.setToY(QueueItem.height);
                        translateTransition.play();
                    }


                    draggedNode.newPosition = 0;
                    draggedNode.runningTranslate = draggedNode.getTranslateY() - (draggedNode.newPosition - draggedNode.videoIndex) * QueueItem.height;
                }
                else if(draggedNode.getTranslateY() == draggedNode.maximumY - draggedNode.minimumY && draggedNode.newPosition != queue.size() -1){

                    for(int i=draggedNode.newPosition; i < queue.size(); i++){
                        QueueItem queueItem = queue.get(i);
                        if(queueItem.equals(draggedNode)) continue;

                        if(queueItem.getTranslateY() > 0){
                            TranslateTransition translateTransition = new TranslateTransition(Duration.millis(AnimationsClass.ANIMATION_SPEED), queueItem);
                            translateTransition.setInterpolator(Interpolator.EASE_OUT);
                            translateTransition.setFromY(queueItem.getTranslateY());
                            translateTransition.setToY(0);
                            translateTransition.play();
                        }
                        else {
                            TranslateTransition translateTransition = new TranslateTransition(Duration.millis(AnimationsClass.ANIMATION_SPEED), queueItem);
                            translateTransition.setInterpolator(Interpolator.EASE_OUT);
                            translateTransition.setFromY(queueItem.getTranslateY());
                            translateTransition.setToY(-QueueItem.height);
                            translateTransition.play();
                        }
                    }

                    draggedNode.newPosition = queue.size() -1;
                    draggedNode.runningTranslate = draggedNode.getTranslateY() - (draggedNode.newPosition - draggedNode.videoIndex) * QueueItem.height;

                }
            }

        });

        queueLine = new QueueLine(this);
    }


    public void add(int index, QueueItem child){


        if(index < 0) return;
        else if(index >= this.getChildren().size()){
            this.add(child);
            return;
        }

        queue.add(index, child);

        this.getChildren().add(index, child);
        initialize(child);

        Timeline minHeightTransition = AnimationsClass.animateMinHeight(90, child);
        Timeline maxHeightTransition = AnimationsClass.animateMaxHeight(90, child);
        ParallelTransition parallelTransition = new ParallelTransition(minHeightTransition, maxHeightTransition);
        FadeTransition fadeTransition = AnimationsClass.fadeIn(child);
        SequentialTransition sequentialTransition = new SequentialTransition(parallelTransition, fadeTransition);
        sequentialTransition.playFromStart();


        if(activeItem == null || activeItem.get().videoIndex < queue.size() - 1) menuController.controlBarController.enableNextVideoButton();
    }

    public void add(QueueItem child){

        queue.add(child);

        // add item with opacity 0, then fade it in
        this.getChildren().add(child);
        initialize(child);

        Timeline minHeightTransition = AnimationsClass.animateMinHeight(90, child);
        Timeline maxHeightTransition = AnimationsClass.animateMaxHeight(90, child);
        ParallelTransition parallelTransition = new ParallelTransition(minHeightTransition, maxHeightTransition);
        FadeTransition fadeTransition = AnimationsClass.fadeIn(child);
        SequentialTransition sequentialTransition = new SequentialTransition(parallelTransition, fadeTransition);
        sequentialTransition.playFromStart();

        menuController.controlBarController.enableNextVideoButton();

    }

    public void remove(QueueItem child){
        if(queue.contains(child)){
            this.remove(queue.indexOf(child));
        }
    }

    public void remove(int index){


        if(index < 0 || queue.isEmpty() || index >= queue.size()) return;

        QueueItem queueItem = queue.get(index);

        queue.remove(index);

        queueItem.setMouseTransparent(true);

        FadeTransition fadeTransition = AnimationsClass.fadeOut(queueItem);


        Timeline minHeightTransition = AnimationsClass.animateMinHeight(0, queueItem);
        Timeline maxHeightTransition = AnimationsClass.animateMaxHeight(0, queueItem);
        ParallelTransition parallelTransition = new ParallelTransition(minHeightTransition, maxHeightTransition);
        SequentialTransition sequentialTransition = new SequentialTransition(fadeTransition, parallelTransition);
        sequentialTransition.setOnFinished(e -> this.getChildren().remove(queueItem));
        sequentialTransition.playFromStart();

    }

    public void clear(){

        if(queue.isEmpty()) return;

        queue.clear();

        ParallelTransition parallelFadeOut = new ParallelTransition();
        for(Node queueItem : this.getChildren()){
            queueItem.setMouseTransparent(true);
            FadeTransition fadeTransition = AnimationsClass.fadeOut(queueItem);
            parallelFadeOut.getChildren().add(fadeTransition);
        }

        parallelFadeOut.setOnFinished(e -> this.getChildren().clear());

        parallelFadeOut.playFromStart();
    }


    public void shuffle(){

        // fade out, shuffle, fade in

        ObservableList<QueueItem> workingCollection = FXCollections.observableArrayList(queue);
        Collections.shuffle(workingCollection);
        if(activeItem.get() != null && workingCollection.indexOf(activeItem.get()) != 0){
            workingCollection.remove(activeItem.get());
            workingCollection.add(0, activeItem.get());
        }
        queue.setAll(workingCollection);


        ParallelTransition parallelFadeOut = new ParallelTransition();

        for(Node queueItem : this.getChildren()){
            FadeTransition fadeTransition = AnimationsClass.fadeOut(queueItem);
            parallelFadeOut.getChildren().add(fadeTransition);
        }

        parallelFadeOut.setOnFinished(e -> {

            this.getChildren().setAll(workingCollection);

            ParallelTransition parallelFadeIn = new ParallelTransition();
            for(Node queueItem : this.getChildren()) {
                FadeTransition fadeTransition = AnimationsClass.fadeIn(queueItem);
                parallelFadeIn.getChildren().add(fadeTransition);
            }

            parallelFadeIn.playFromStart();
        });

        parallelFadeOut.playFromStart();
    }


    public void handleDragEntered(DragEvent e) {

        dragBoardFiles = (ArrayList<File>) e.getDragboard().getFiles();

        for(File file : dragBoardFiles){
            String extension = Utilities.getFileExtension(file);
            if(extension.equals("mp4") ||
                extension.equals("mp3") ||
                extension.equals("avi") ||
                extension.equals("flac") ||
                extension.equals("flv") ||
                extension.equals("mkv") ||
                extension.equals("mov") ||
                extension.equals("wav") ||
                extension.equals("opus") ||
                extension.equals("aiff") ||
                extension.equals("m4a") ||
                extension.equals("wma") ||
                extension.equals("aac") ||
                extension.equals("ogg"))

            {
                dragBoardMedia.add(file);
            }
        }

        if(dragBoardMedia.isEmpty()) return;

        dragAndDropActive = true;
        if(!queue.isEmpty()){
            queueLine.setPosition(-1);
        }

    }

    public void handleDragOver(DragEvent e){
        if(dragAndDropActive) {
            if (!dragBoardMedia.isEmpty()) {
                e.acceptTransferModes(TransferMode.COPY);
            }
        }
    }

    public void handleDragDropped(DragEvent e){

        dragAndDropActive = false;
        if (dragBoardMedia.isEmpty()) return;


        for (File file : dragBoardMedia) {
            this.add(this.getChildren().indexOf(queueLine), new QueueItem(file, menuController, menuController.mediaInterface));
        }

        dragBoardMedia.clear();
    }

    public void handleDragExited(DragEvent e){
        cancelDragAndDrop();
    }


    public void cancelDragAndDrop(){
        dragAndDropActive = false;
        dragBoardMedia.clear();
        dragBoardFiles.clear();

        getChildren().remove(queueLine);
    }



    public void initialize(QueueItem queueItem){
        Platform.runLater(() -> {
            queueItem.playButtonTooltip = new ControlTooltip(menuController.mainController, "Play video", queueItem.playButton, 1000);
            queueItem.removeButtonTooltip = new ControlTooltip(menuController.mainController,"Remove video", queueItem.removeButton, 1000);
            queueItem.optionsButtonTooltip = new ControlTooltip(menuController.mainController, "Options", queueItem.optionsButton, 1000);
            queueItem.menuItemContextMenu = new MenuItemContextMenu(queueItem);

            QueueItem.height = queueItem.getBoundsInParent().getHeight();

        });
    }

}
