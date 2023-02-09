package hans.Menu;

import hans.AnimationsClass;
import hans.ControlTooltip;
import hans.MediaItems.MediaUtilities;
import hans.Utilities;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


public class QueueBox extends VBox {

    MenuController menuController;


    ArrayList<File> dragBoardFiles = new ArrayList<>();
    ArrayList<File> dragBoardMedia = new ArrayList<>();


    BooleanProperty dragAndDropActive = new SimpleBooleanProperty(false);
    public boolean dragActive = false;

    public QueueItem draggedNode;

    public IntegerProperty activeIndex = new SimpleIntegerProperty(-1); // what index we are at in the queueorder arraylist
    public ObjectProperty<QueueItem> activeItem = new SimpleObjectProperty<>();

    public ArrayList<QueueItem> queue = new ArrayList<>();
    public ObservableList<Integer> queueOrder = FXCollections.observableArrayList();


    DropPositionController dropPositionController;

    QueueBox(MenuController menuController){
        this.menuController = menuController;
        this.setAlignment(Pos.TOP_CENTER);
        this.setFillWidth(true);

        dropPositionController = new DropPositionController(this);

        VBox.setVgrow(this, Priority.ALWAYS);
        this.setPadding(new Insets(10, 0, 50, 0));

        this.setOnDragEntered(this::handleDragEntered);
        this.setOnDragOver(this::handleDragOver);
        this.setOnDragDropped(this::handleDragDropped);
        this.setOnDragExited(this::handleDragExited);

        activeIndex.addListener((observableValue, oldValue, newValue) -> menuController.controlBarController.updateNextAndPreviousVideoButtons());

        dragAndDropActive.addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                ParallelTransition parallelTransition = new ParallelTransition();
                for(Node node : this.getChildren()){
                    FadeTransition fadeTransition = new FadeTransition(Duration.millis(300), node);
                    fadeTransition.setFromValue(node.getOpacity());
                    fadeTransition.setToValue(0.5);
                    parallelTransition.getChildren().add(fadeTransition);
                }

                if(!parallelTransition.getChildren().isEmpty()) parallelTransition.playFromStart();
            }
            else {
                for(Transition transition : dropPositionController.transitions){
                    transition.stop();
                }

                dropPositionController.transitions.clear();

                for(Node node : this.getChildren()){
                    node.setTranslateY(0);
                }

                dropPositionController.position = Integer.MAX_VALUE;

                ParallelTransition parallelTransition = new ParallelTransition();
                for(Node node : this.getChildren()){
                    FadeTransition fadeTransition = new FadeTransition(Duration.millis(300), node);
                    fadeTransition.setFromValue(node.getOpacity());
                    fadeTransition.setToValue(1);
                    parallelTransition.getChildren().add(fadeTransition);
                }

                if(!parallelTransition.getChildren().isEmpty()) parallelTransition.playFromStart();
            }
        });


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

    }


    public void add(int index, QueueItem child){


        if(index < 0) return;
        else if(index >= this.getChildren().size()){
            this.add(child);
            return;
        }

        if(menuController.settingsController.playbackOptionsController.shuffleOn) {
            queue.add(child);
            queueOrder.add(index, queueOrder.size());
        }
        else {
            queue.add(index, child);
            queueOrder.add(queueOrder.size());
        }

        if(activeIndex.get() != -1 && index < activeIndex.get()) activeIndex.set(activeIndex.get() + 1);

        updateQueue();

        this.getChildren().add(index, child);
        initialize(child);

        Timeline minHeightTransition = AnimationsClass.animateMinHeight(90, child);
        Timeline maxHeightTransition = AnimationsClass.animateMaxHeight(90, child);
        ParallelTransition parallelTransition = new ParallelTransition(minHeightTransition, maxHeightTransition);
        FadeTransition fadeTransition = AnimationsClass.fadeIn(child);
        SequentialTransition sequentialTransition = new SequentialTransition(parallelTransition, fadeTransition);
        sequentialTransition.playFromStart();

    }

    public void addRand(QueueItem child) {
        Random random = new Random();
        int index;
        if(activeIndex.get() == -1) index = random.nextInt(queueOrder.size() + 1);
        else if(activeIndex.get() == queueOrder.size() -1) index = queueOrder.size(); // add to the end of the queue
        else index = activeIndex.get() + 1 + random.nextInt(queueOrder.size() + 1 - activeIndex.get());

        this.add(index, child);
    }

    public void add(QueueItem child){

        queue.add(child);
        queueOrder.add(queueOrder.size());

        updateQueue();

        // add item with opacity 0, then fade it in
        this.getChildren().add(child);
        initialize(child);

        Timeline minHeightTransition = AnimationsClass.animateMinHeight(90, child);
        Timeline maxHeightTransition = AnimationsClass.animateMaxHeight(90, child);
        ParallelTransition parallelTransition = new ParallelTransition(minHeightTransition, maxHeightTransition);
        FadeTransition fadeTransition = AnimationsClass.fadeIn(child);
        SequentialTransition sequentialTransition = new SequentialTransition(parallelTransition, fadeTransition);
        sequentialTransition.playFromStart();

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
        queueOrder.remove((Integer) index);
        menuController.selectedItems.remove(queueItem);

        if(queueOrder.size() != index){
            for(int i=0; i<queueOrder.size(); i++){
                if(queueOrder.get(i) > index) queueOrder.set(i, queueOrder.get(i) -1);
            }
        }

        if(activeIndex.get() != -1 && index < activeIndex.get()) activeIndex.set(activeIndex.get() - 1);

        updateQueue();

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
        queueOrder.clear();
        menuController.selectedItems.clear();

        if(activeItem.get() != null) activeItem.get().setInactive();

        activeItem.set(null);
        activeIndex.set(-1);

        if(menuController.mediaInterface.mediaActive.get()) menuController.mediaInterface.resetMediaPlayer();

        updateQueue();

        ParallelTransition parallelFadeOut = new ParallelTransition();
        for(Node queueItem : this.getChildren()){
            queueItem.setMouseTransparent(true);
            FadeTransition fadeTransition = AnimationsClass.fadeOut(queueItem);
            parallelFadeOut.getChildren().add(fadeTransition);
        }

        parallelFadeOut.setOnFinished(e -> this.getChildren().clear());

        parallelFadeOut.playFromStart();
    }


    public void shuffleOn(){

        // fade out, shuffle, fade in

        Collections.shuffle(queueOrder);
        if(activeIndex.get() != -1 && queueOrder.indexOf(activeIndex.getValue()) != 0){
            queueOrder.remove(activeIndex.getValue());
            queueOrder.add(0, activeIndex.getValue());
        }

        if(activeIndex.get() != -1) activeIndex.set(0);

        updateQueue();

        ParallelTransition parallelFadeOut = new ParallelTransition();

        for(Node queueItem : this.getChildren()){
            FadeTransition fadeTransition = AnimationsClass.fadeOut(queueItem);
            parallelFadeOut.getChildren().add(fadeTransition);
        }

        parallelFadeOut.setOnFinished(e -> {

            this.getChildren().clear();

            for (Integer integer : queueOrder) {
                this.getChildren().add(queue.get(integer));
            }

            ParallelTransition parallelFadeIn = new ParallelTransition();
            for(Node queueItem : this.getChildren()) {
                FadeTransition fadeTransition = AnimationsClass.fadeIn(queueItem);
                parallelFadeIn.getChildren().add(fadeTransition);
            }

            parallelFadeIn.playFromStart();
        });

        parallelFadeOut.playFromStart();
    }

    public void shuffleOff(){
        int size = queueOrder.size();
        queueOrder.clear();

        for(int i=0; i<size; i++){
            queueOrder.add(i);
        }

        if(activeItem.get() != null) activeIndex.set(queue.indexOf(activeItem.get()));

        updateQueue();

        ParallelTransition parallelFadeOut = new ParallelTransition();

        for(Node queueItem : this.getChildren()){
            FadeTransition fadeTransition = AnimationsClass.fadeOut(queueItem);
            parallelFadeOut.getChildren().add(fadeTransition);
        }

        parallelFadeOut.setOnFinished(e -> {

            this.getChildren().clear();

            for (QueueItem queueItem : queue) {
                this.getChildren().add(queueItem);
            }

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

        Dragboard dragboard = e.getDragboard();

        dragBoardFiles = (ArrayList<File>) dragboard.getFiles();

        for(File file : dragBoardFiles){
            String extension = Utilities.getFileExtension(file);
            if(MediaUtilities.mediaFormats.contains(extension))
                dragBoardMedia.add(file);
        }

        if(dragBoardMedia.isEmpty()) return;

        dragAndDropActive.set(true);

        if(dragBoardMedia.size() == 1) menuController.mainController.dragViewPopup.setText("1 item");
        else menuController.mainController.dragViewPopup.setText(dragBoardMedia.size() + " items");

        if(!menuController.mainController.dragViewPopup.isShowing()) menuController.mainController.dragViewPopup.show(menuController.mainController.videoImageViewWrapper, e.getScreenX(), e.getScreenY());
    }

    public void handleDragOver(DragEvent e){
        if(dragAndDropActive.get() && !dragBoardMedia.isEmpty()) {
            e.acceptTransferModes(TransferMode.COPY);

            menuController.mainController.dragViewPopup.setPosition(e.getScreenX(), e.getScreenY());
            dropPositionController.updateY(e.getY());
        }
    }

    public void handleDragDropped(DragEvent e){

        int index = Math.min(dropPositionController.position, queue.size());

        if (dragBoardMedia.isEmpty()){
            dragAndDropActive.set(false);
            return;
        }

        double translation = 0;
        if(queue.size() > index) translation = queue.get(index).getTranslateY();

        dragAndDropActive.set(false);

        for (int i=0; i < dragBoardMedia.size(); i++) {
            QueueItem queueItem;
            if(i == 0) queueItem = new QueueItem(dragBoardMedia.get(i), menuController, menuController.mediaInterface, translation);
            else queueItem = new QueueItem(dragBoardMedia.get(i), menuController, menuController.mediaInterface, 0);

            this.add(index + i, queueItem);
        }

        dragBoardMedia.clear();
        dragBoardFiles.clear();

        if(menuController.mainController.dragViewPopup.isShowing()) menuController.mainController.dragViewPopup.hide();
    }

    public void handleDragExited(DragEvent e){
        cancelDragAndDrop();
    }


    public void cancelDragAndDrop(){
        dragAndDropActive.set(false);
        if(menuController.mainController.dragViewPopup.isShowing()) menuController.mainController.dragViewPopup.hide();
        dragBoardFiles.clear();
        dragBoardMedia.clear();
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

    private void updateQueue(){

        menuController.clearQueueButton.setDisable(queueOrder.isEmpty());

        for(int i=0; i < queueOrder.size(); i++){
            queue.get(queueOrder.get(i)).updateIndex(i);
        }

        menuController.controlBarController.updateNextAndPreviousVideoButtons();
    }

}
