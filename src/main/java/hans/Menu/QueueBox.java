package hans.Menu;

import hans.*;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
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

    public ArrayList<Animation> dragAnimationsInProgress = new ArrayList<>();

    boolean dragAndDropActive = false;
    public boolean dragActive = false;

    public QueueItem draggedNode;
    QueueLine queueLine;


    QueueBox(MenuController menuController){
        this.menuController = menuController;
        this.setAlignment(Pos.TOP_CENTER);
        this.setFillWidth(true);
        //this.setMinHeight(100);
        //this.setPrefHeight(Double.MAX_VALUE);
        //this.setMaxHeight(Double.MAX_VALUE);

        VBox.setVgrow(this, Priority.ALWAYS);
        this.setPadding(new Insets(0, 0, 100, 0));


        this.setOnDragEntered(this::handleDragEntered);
        this.setOnDragOver(this::handleDragOver);
        this.setOnDragDropped(this::handleDragDropped);
        this.setOnDragExited(this::handleDragExited);




        this.setOnMouseDragOver((e) -> {
            if(dragActive){
                // update draggedNode translateY


                if((e.getY() - draggedNode.dragPosition) <= 0) draggedNode.setTranslateY(-draggedNode.minimumY);
                else if((e.getY() - draggedNode.dragPosition) > (draggedNode.maximumY)) draggedNode.setTranslateY(draggedNode.maximumY - draggedNode.minimumY);
                else draggedNode.setTranslateY(e.getY() - draggedNode.minimumY - draggedNode.dragPosition);

                draggedNode.runningTranslate = draggedNode.getTranslateY() - (draggedNode.newPosition - (draggedNode.videoIndex-1)) * QueueItem.height;

                if(draggedNode.runningTranslate >= QueueItem.height){
                    do {
                        if(menuController.queue.get(draggedNode.newPosition).getTranslateY() > 0 && !menuController.queue.get(draggedNode.newPosition).equals(draggedNode)){
                            QueueItem queueItem = menuController.queue.get(draggedNode.newPosition);
                            TranslateTransition translateTransition = new TranslateTransition(Duration.millis(AnimationsClass.ANIMATION_SPEED),queueItem);
                            translateTransition.setInterpolator(Interpolator.EASE_OUT);
                            translateTransition.setFromY(queueItem.getTranslateY());
                            translateTransition.setToY(0);
                            translateTransition.setOnFinished(ev -> dragAnimationsInProgress.remove(translateTransition));
                            dragAnimationsInProgress.add(translateTransition);
                            translateTransition.play();
                        }
                        else {
                            QueueItem queueItem = menuController.queue.get(draggedNode.newPosition + 1);
                            TranslateTransition translateTransition = new TranslateTransition(Duration.millis(AnimationsClass.ANIMATION_SPEED), queueItem);
                            translateTransition.setInterpolator(Interpolator.EASE_OUT);
                            translateTransition.setFromY(queueItem.getTranslateY());
                            translateTransition.setToY(-QueueItem.height);
                            translateTransition.setOnFinished(ev -> dragAnimationsInProgress.remove(translateTransition));
                            dragAnimationsInProgress.add(translateTransition);
                            translateTransition.play();
                        }


                        draggedNode.newPosition+=1;
                        draggedNode.runningTranslate = draggedNode.getTranslateY() - (draggedNode.newPosition - (draggedNode.videoIndex-1)) * QueueItem.height;
                    }
                    while(draggedNode.runningTranslate >= QueueItem.height);
                }
                else if(draggedNode.runningTranslate <= -QueueItem.height){
                    do {
                        if(menuController.queue.get(draggedNode.newPosition).getTranslateY() < 0 && !menuController.queue.get(draggedNode.newPosition).equals(draggedNode)){

                            QueueItem queueItem = menuController.queue.get(draggedNode.newPosition);

                            TranslateTransition translateTransition = new TranslateTransition(Duration.millis(AnimationsClass.ANIMATION_SPEED), queueItem);
                            translateTransition.setInterpolator(Interpolator.EASE_OUT);
                            translateTransition.setFromY(queueItem.getTranslateY());
                            translateTransition.setToY(0);
                            translateTransition.setOnFinished(ev -> dragAnimationsInProgress.remove(translateTransition));
                            dragAnimationsInProgress.add(translateTransition);
                            translateTransition.play();
                        }
                        else {
                            QueueItem queueItem = menuController.queue.get(draggedNode.newPosition - 1);
                            TranslateTransition translateTransition = new TranslateTransition(Duration.millis(AnimationsClass.ANIMATION_SPEED), queueItem);
                            translateTransition.setInterpolator(Interpolator.EASE_OUT);
                            translateTransition.setFromY(queueItem.getTranslateY());
                            translateTransition.setToY(QueueItem.height);
                            translateTransition.setOnFinished(ev -> dragAnimationsInProgress.remove(translateTransition));
                            dragAnimationsInProgress.add(translateTransition);
                            translateTransition.play();
                        }


                        draggedNode.newPosition-=1;
                        draggedNode.runningTranslate = draggedNode.getTranslateY() - (draggedNode.newPosition - (draggedNode.videoIndex-1)) * QueueItem.height;
                    }
                    while(draggedNode.runningTranslate <= -QueueItem.height);
                }
                else {
                    if(draggedNode.getTranslateY() == -draggedNode.minimumY && draggedNode.newPosition != 0){

                        if(menuController.queue.get(draggedNode.newPosition).getTranslateY() < 0 && !menuController.queue.get(draggedNode.newPosition).equals(draggedNode)){

                            QueueItem queueItem = menuController.queue.get(draggedNode.newPosition);

                            TranslateTransition translateTransition = new TranslateTransition(Duration.millis(AnimationsClass.ANIMATION_SPEED), queueItem);
                            translateTransition.setInterpolator(Interpolator.EASE_OUT);
                            translateTransition.setFromY(queueItem.getTranslateY());
                            translateTransition.setToY(0);
                            translateTransition.setOnFinished(ev -> dragAnimationsInProgress.remove(translateTransition));
                            dragAnimationsInProgress.add(translateTransition);
                            translateTransition.play();
                        }
                        else {

                            QueueItem queueItem = menuController.queue.get(draggedNode.newPosition - 2);

                            TranslateTransition translateTransition = new TranslateTransition(Duration.millis(AnimationsClass.ANIMATION_SPEED), queueItem);
                            translateTransition.setInterpolator(Interpolator.EASE_OUT);
                            translateTransition.setFromY(queueItem.getTranslateY());
                            translateTransition.setToY(QueueItem.height);
                            translateTransition.setOnFinished(ev -> dragAnimationsInProgress.remove(translateTransition));
                            dragAnimationsInProgress.add(translateTransition);
                            translateTransition.play();
                        }


                        draggedNode.newPosition = 0;
                        draggedNode.runningTranslate = draggedNode.getTranslateY() - (draggedNode.newPosition - (draggedNode.videoIndex-1)) * QueueItem.height;
                    }
                    else if(draggedNode.getTranslateY() == draggedNode.maximumY - draggedNode.minimumY && draggedNode.newPosition != menuController.queue.size() -1){

                        for(int i=draggedNode.newPosition; i < menuController.queue.size(); i++){
                            QueueItem queueItem = menuController.queue.get(i);
                            if(queueItem.equals(draggedNode)) continue;

                            if(queueItem.getTranslateY() > 0){
                                TranslateTransition translateTransition = new TranslateTransition(Duration.millis(AnimationsClass.ANIMATION_SPEED), queueItem);
                                translateTransition.setInterpolator(Interpolator.EASE_OUT);
                                translateTransition.setFromY(queueItem.getTranslateY());
                                translateTransition.setToY(0);
                                translateTransition.setOnFinished(ev -> dragAnimationsInProgress.remove(translateTransition));
                                dragAnimationsInProgress.add(translateTransition);
                                translateTransition.play();
                            }
                            else {
                                TranslateTransition translateTransition = new TranslateTransition(Duration.millis(AnimationsClass.ANIMATION_SPEED), queueItem);
                                translateTransition.setInterpolator(Interpolator.EASE_OUT);
                                translateTransition.setFromY(queueItem.getTranslateY());
                                translateTransition.setToY(-QueueItem.height);
                                translateTransition.setOnFinished(ev -> dragAnimationsInProgress.remove(translateTransition));
                                dragAnimationsInProgress.add(translateTransition);
                                translateTransition.play();
                            }
                        }

                        draggedNode.newPosition = menuController.queue.size() -1;
                        draggedNode.runningTranslate = draggedNode.getTranslateY() - (draggedNode.newPosition - (draggedNode.videoIndex-1)) * QueueItem.height;

                    }
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

        cancelDragAndDrop();

        cancelDrag();

        menuController.queue.add(index, child);


        ArrayList<Node> childrenToBeMoved = new ArrayList<>();
        ParallelTransition parallelTransition = new ParallelTransition();

        for(int i = index; i < this.getChildren().size(); i++){
            childrenToBeMoved.add(this.getChildren().get(i));
            parallelTransition.getChildren().add(AnimationsClass.animateDown(this.getChildren().get(i), QueueItem.height));
        }

        parallelTransition.setOnFinished((ev) -> {
            this.getChildren().add(index, child);
            initialize(child);
            for(Node node : childrenToBeMoved){
                node.setTranslateY(0);
            }

            FadeTransition fadeTransition = AnimationsClass.fadeIn(child);
            menuController.animationsInProgress.remove(parallelTransition);
            fadeTransition.playFromStart();
        });


        menuController.animationsInProgress.add(parallelTransition);
        parallelTransition.playFromStart();

        menuController.controlBarController.enableNextVideoButton();
    }

    public void addRand(QueueItem child){
        Random random = new Random();
        int index = random.nextInt(menuController.queue.size() + 1);
        if(index >= menuController.queue.size()) add(child);
        else add(index, child);
    }

    public void add(QueueItem child){

        cancelDragAndDrop();

        cancelDrag();

        menuController.queue.add(child);

        // add item with opacity 0, then fade it in
        this.getChildren().add(child);

        initialize(child);
        FadeTransition fadeTransition = AnimationsClass.fadeIn(child);
        fadeTransition.playFromStart();

        menuController.controlBarController.enableNextVideoButton();

    }

    public void remove(QueueItem child, boolean updateTooltip){
        if(menuController.queue.contains(child)){
            this.remove(menuController.queue.indexOf(child), updateTooltip);
        }
    }

    public void remove(int index, boolean updateTooltip){

        cancelDragAndDrop();

        cancelDrag();

        if(index >= 0 && !this.getChildren().isEmpty() && index < this.getChildren().size()) {

            menuController.queue.remove(index);


            FadeTransition fadeTransition = AnimationsClass.fadeOut(this.getChildren().get(index));

            SequentialTransition sequentialTransition = new SequentialTransition();
            sequentialTransition.getChildren().add(fadeTransition);

            ArrayList<Node> childrenToBeMoved = new ArrayList<>();

            ParallelTransition parallelTransition = new ParallelTransition();

            if (index < this.getChildren().size() - 1) {
                // removed child was not the last inside the vbox, have to translate upwards all nodes that were below
                for (int i = index + 1; i < this.getChildren().size(); i++) {
                    childrenToBeMoved.add(this.getChildren().get(i));
                    parallelTransition.getChildren().add(AnimationsClass.animateUp(this.getChildren().get(i), QueueItem.height));
                }
            }

            sequentialTransition.getChildren().add(parallelTransition);

            sequentialTransition.setOnFinished((ev) -> {
                this.getChildren().remove(index);
                for (Node node : childrenToBeMoved) {
                    node.setTranslateY(0);
                }
                menuController.animationsInProgress.remove(sequentialTransition);
            });

            menuController.animationsInProgress.add(sequentialTransition);
            sequentialTransition.playFromStart();

            // decrease max height by 50, apply translate of -50 to all nodes below the one that will be removed and on end actually remove the node and reset translate

            if(updateTooltip) {
                if ((menuController.historyBox.index == -1 || menuController.historyBox.index == menuController.history.size() - 1) && menuController.queue.isEmpty()) {
                    if (menuController.controlBarController.nextVideoButtonEnabled)
                        menuController.controlBarController.disableNextVideoButton();
                } else menuController.controlBarController.enableNextVideoButton();
            }
        }
    }

    //TODO: implement Collections.rotate instead of removing and adding
    public void removeAndMove(int index, boolean updateTooltip){

        cancelDragAndDrop();

        cancelDrag();

        // removes item at index from the queuebox, moves all previous items to the bottom
        if(index < 0 || index >= getChildren().size()) return;
        if(index ==0){
            remove(index, updateTooltip);
            return;
        }

        ParallelTransition parallelFadeOut = new ParallelTransition();
        ParallelTransition parallelTranslate = new ParallelTransition();
        ParallelTransition parallelFadeIn = new ParallelTransition();
        ArrayList<QueueItem> itemsToBeTranslated = new ArrayList<>();
        ArrayList<QueueItem> itemsToBeMoved = new ArrayList<>();

        FadeTransition fade = AnimationsClass.fadeOut(this.getChildren().get(index));
        parallelFadeOut.getChildren().add(fade);

        for(int i = 0; i < index; i++){
            FadeTransition fadeTransition = AnimationsClass.fadeOut(getChildren().get(i));
            parallelFadeOut.getChildren().add(fadeTransition);
            itemsToBeMoved.add((QueueItem) getChildren().get(i));
        }

        menuController.queue.remove(index);
        menuController.queue.removeAll(itemsToBeMoved);
        menuController.queue.addAll(itemsToBeMoved);


        parallelFadeOut.setOnFinished(e -> {

            for(int i = index + 1; i < getChildren().size(); i++){
                itemsToBeTranslated.add((QueueItem) getChildren().get(i));
                parallelTranslate.getChildren().add(AnimationsClass.animateUp(getChildren().get(i), itemsToBeMoved.size() * QueueItem.height + QueueItem.height));
            }

            parallelTranslate.setOnFinished(k -> {

                getChildren().remove(index);
                getChildren().removeAll(itemsToBeMoved);

                for(QueueItem queueItem: itemsToBeTranslated){
                    queueItem.setTranslateY(0);
                }


                getChildren().addAll(itemsToBeMoved);



                for(QueueItem queueItem : itemsToBeMoved){
                    parallelFadeIn.getChildren().add(AnimationsClass.fadeIn(queueItem));
                }

                menuController.animationsInProgress.remove(parallelTranslate);
                parallelFadeIn.playFromStart();
            });


            menuController.animationsInProgress.remove(parallelFadeOut);
            menuController.animationsInProgress.add(parallelTranslate);
            parallelTranslate.playFromStart();
        });

        menuController.animationsInProgress.add(parallelFadeOut);
        parallelFadeOut.playFromStart();

        if(updateTooltip) {
            if ((menuController.historyBox.index == -1 || menuController.historyBox.index == menuController.history.size() - 1) && menuController.queue.isEmpty()) {
                if (menuController.controlBarController.nextVideoButtonEnabled)
                    menuController.controlBarController.disableNextVideoButton();
            } else menuController.controlBarController.enableNextVideoButton();
        }

    }

    public void addAll(Collection<? extends QueueItem> collection){

        cancelDragAndDrop();

        menuController.queue.addAll(collection);

        this.getChildren().addAll(collection);

        for(QueueItem queueItem : collection){
            initialize(queueItem);
        }
        ParallelTransition parallelTransition = new ParallelTransition();
        for(QueueItem queueItem : collection){
            parallelTransition.getChildren().add(AnimationsClass.fadeIn(queueItem));
        }

        parallelTransition.playFromStart();


        menuController.controlBarController.enableNextVideoButton();
    }

    public void addAll(int index, Collection<? extends QueueItem> collection) {

        cancelDragAndDrop();

        cancelDrag();


        if (index < -1) return;
        else if(index >= this.getChildren().size() || index == -1){
            addAll(collection);
            return;
        }

        menuController.queue.addAll(index, collection);

        ParallelTransition parallelTransition = new ParallelTransition();

        ArrayList<QueueItem> itemsToBeMoved = new ArrayList<>();

        if(index < this.getChildren().size() -1){
            // items won't be added to the last slot, have to translate items below index
            for(int i = index; i < this.getChildren().size(); i++){
                TranslateTransition translateTransition = AnimationsClass.animateDown(this.getChildren().get(i), collection.size() * QueueItem.height);
                parallelTransition.getChildren().add(translateTransition);
                itemsToBeMoved.add((QueueItem) this.getChildren().get(i));
            }
        }

        parallelTransition.setOnFinished(e -> {
            this.getChildren().addAll(index, collection);
            for(QueueItem queueItem : collection){
                initialize(queueItem);
            }

            for(QueueItem queueItem : itemsToBeMoved){
                queueItem.setTranslateY(0);
            }

            ParallelTransition parallelFadeIn = new ParallelTransition();
            for(QueueItem queueItem : collection){
                parallelFadeIn.getChildren().add(AnimationsClass.fadeIn(queueItem));
            }

            parallelFadeIn.playFromStart();
            menuController.animationsInProgress.remove(parallelTransition);
        });

        menuController.animationsInProgress.add(parallelTransition);
        parallelTransition.playFromStart();

        menuController.controlBarController.enableNextVideoButton();
    }

    public void clear(){

        cancelDragAndDrop();

        cancelDrag();

        if(!this.getChildren().isEmpty()){

            menuController.queue.clear();

            ParallelTransition parallelFadeOut = new ParallelTransition();
            for(Node queueItem : this.getChildren()){
                FadeTransition fadeTransition = AnimationsClass.fadeOut(queueItem);
                parallelFadeOut.getChildren().add(fadeTransition);
            }

            parallelFadeOut.setOnFinished(e -> {
                this.getChildren().clear();
                menuController.animationsInProgress.remove(parallelFadeOut);
            });

            menuController.animationsInProgress.add(parallelFadeOut);
            parallelFadeOut.playFromStart();

            if(menuController.historyBox.index == -1  || menuController.historyBox.index == menuController.history.size() -1){
                if(menuController.controlBarController.nextVideoButtonEnabled) menuController.controlBarController.disableNextVideoButton();
            }

        }
    }

    public void move(int oldIndex, int newIndex){

        cancelDragAndDrop();

        cancelDrag();

        // move to bottom if newIndex = -1

        if(this.getChildren().size() < 2 ||
                oldIndex == newIndex ||
                oldIndex < 0 ||
                oldIndex >= this.getChildren().size() ||
                newIndex < -1 ||
                newIndex >= this.getChildren().size() ||
                (oldIndex == this.getChildren().size() -1 && newIndex == -1)) return;

        QueueItem child = (QueueItem) this.getChildren().get(oldIndex);

        menuController.queue.remove(oldIndex);
        menuController.queue.add(newIndex, child);

        FadeTransition fadeTransition = AnimationsClass.fadeOut(child);
        if(newIndex == -1 || newIndex > oldIndex){

            fadeTransition.setOnFinished((e) -> {
                ArrayList<QueueItem> childrenToBeMoved = new ArrayList<>();
                ParallelTransition parallelTransition = new ParallelTransition();

                int loopEndIndex = newIndex == -1 ? this.getChildren().size() : newIndex + 1;

                for(int i = oldIndex + 1; i < loopEndIndex; i++){
                    childrenToBeMoved.add((QueueItem) this.getChildren().get(i));
                    parallelTransition.getChildren().add(AnimationsClass.animateUp(this.getChildren().get(i), QueueItem.height));
                }

                parallelTransition.setOnFinished((ev) -> {
                    this.getChildren().remove(oldIndex);
                    for (QueueItem queueItem : childrenToBeMoved) {
                        queueItem.setTranslateY(0);
                    }
                    if(newIndex == -1) this.getChildren().add(child);
                    else this.getChildren().add(newIndex, child);
                    FadeTransition fadeTransition1 = AnimationsClass.fadeIn(child);

                    menuController.animationsInProgress.remove(parallelTransition);
                    fadeTransition1.playFromStart();
                });

                menuController.animationsInProgress.remove(fadeTransition);
                menuController.animationsInProgress.add(parallelTransition);
                parallelTransition.playFromStart();
            });
        }

        else {
            // move item up

            fadeTransition.setOnFinished((e) -> {
                ArrayList<QueueItem> childrenToBeMoved = new ArrayList<>();
                ParallelTransition parallelTransition = new ParallelTransition();


                for(int i = newIndex; i < oldIndex; i++){
                    childrenToBeMoved.add((QueueItem) this.getChildren().get(i));
                    parallelTransition.getChildren().add(AnimationsClass.animateDown(this.getChildren().get(i), QueueItem.height));
                }

                parallelTransition.setOnFinished((ev) -> {
                    this.getChildren().remove(oldIndex);
                    for (QueueItem queueItem : childrenToBeMoved) {
                        queueItem.setTranslateY(0);
                    }

                    this.getChildren().add(newIndex, child);
                    FadeTransition fadeTransition1 = AnimationsClass.fadeIn(child);

                    menuController.animationsInProgress.remove(parallelTransition);

                    fadeTransition1.playFromStart();
                });

                menuController.animationsInProgress.remove(fadeTransition);
                menuController.animationsInProgress.add(parallelTransition);
                parallelTransition.playFromStart();
            });
        }

        menuController.animationsInProgress.add(fadeTransition);
        fadeTransition.playFromStart();

        menuController.controlBarController.enableNextVideoButton();
    }


    public void shuffle(){

        cancelDragAndDrop();

        cancelDrag();

        // fade out, shuffle, fade in

        ObservableList<QueueItem> workingCollection = FXCollections.observableArrayList(menuController.queue);
        Collections.shuffle(workingCollection);
        menuController.queue.setAll(workingCollection);


        ParallelTransition parallelFadeOut = new ParallelTransition();

        for(Node queueItem : this.getChildren()){
            FadeTransition fadeTransition = AnimationsClass.fadeOut(queueItem);
            parallelFadeOut.getChildren().add(fadeTransition);
        }

        parallelFadeOut.setOnFinished(e -> {

            this.getChildren().setAll(workingCollection);

            ParallelTransition parallelFadeIn = new ParallelTransition();
            for(Node queueItem : this.getChildren()){
                FadeTransition fadeTransition = AnimationsClass.fadeIn(queueItem);
                parallelFadeIn.getChildren().add(fadeTransition);
            }

            parallelFadeIn.setOnFinished(k -> menuController.animationsInProgress.remove(parallelFadeIn));

            menuController.animationsInProgress.add(parallelFadeIn);
            parallelFadeIn.playFromStart();

            menuController.animationsInProgress.remove(parallelFadeOut);
        });

        menuController.animationsInProgress.add(parallelFadeOut);
        parallelFadeOut.playFromStart();

        menuController.controlBarController.enableNextVideoButton();

    }


    public void handleDragEntered(DragEvent e) {

        dragBoardFiles = (ArrayList<File>) e.getDragboard().getFiles();

        for(File file : dragBoardFiles){
            if(Utilities.getFileExtension(file).equals("mp4") ||
                Utilities.getFileExtension(file).equals("mp3") ||
                Utilities.getFileExtension(file).equals("avi") ||
                Utilities.getFileExtension(file).equals("flac") ||
                Utilities.getFileExtension(file).equals("flv") ||
                Utilities.getFileExtension(file).equals("mkv") ||
                Utilities.getFileExtension(file).equals("mov") ||
                Utilities.getFileExtension(file).equals("wav"))
            {
                dragBoardMedia.add(file);
            }
        }

        if(dragBoardMedia.isEmpty()) return;

        dragAndDropActive = true;
        if(!menuController.queue.isEmpty()){
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

        if(dragBoardMedia.size() == 1) menuController.notificationText.setText("Added 1 video to the queue");
        else menuController.notificationText.setText("Added " + dragBoardMedia.size() + " videos to the queue");
        if(menuController.menuNotificationOpen) menuController.closeTimer.playFromStart();
        else AnimationsClass.openMenuNotification(menuController);


        ArrayList<QueueItem> newItems = new ArrayList<>();

        for (File file : dragBoardMedia) {
            newItems.add(new QueueItem(file, menuController, menuController.mediaInterface, this));
        }

        addAll(getChildren().indexOf(queueLine), newItems);

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


    public void cancelDrag(){

        for(Animation animation : dragAnimationsInProgress){
            animation.stop();
        }

        dragAnimationsInProgress.clear();
        
        for(QueueItem queueItem : menuController.queue){
            queueItem.setTranslateY(0);
        }

        if(draggedNode != null){
            draggedNode.setMouseTransparent(false);
            draggedNode.setViewOrder(1);
            draggedNode.setStyle("-fx-background-color: transparent;");

            draggedNode.indexLabel.setVisible(true);
            draggedNode.playIcon.setVisible(false);

            if(menuController.queue.indexOf(draggedNode) != draggedNode.newPosition){
                menuController.queue.remove(draggedNode);
                getChildren().remove(draggedNode);

                menuController.queue.add(draggedNode.newPosition, draggedNode);
                getChildren().add(draggedNode.newPosition, draggedNode);

                menuController.controlBarController.enableNextVideoButton();

            }

            draggedNode = null;
        }

        dragActive = false;


    }


    public void initialize(QueueItem queueItem){
        Platform.runLater(() -> {
            queueItem.removeButtonTooltip = new ControlTooltip(menuController.mainController,"Remove video", queueItem.removeButton, 1000);
            queueItem.optionsButtonTooltip = new ControlTooltip(menuController.mainController, "Options", queueItem.optionsButton, 1000);
            queueItem.menuItemContextMenu = new MenuItemContextMenu(queueItem);

            QueueItem.height = queueItem.getBoundsInParent().getHeight();

        });
    }

}
