package hans;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

    double animationSpeed = 200;
    MenuController menuController;


    ArrayList<File> dragBoardFiles = new ArrayList<>();
    ArrayList<File> dragBoardMedia = new ArrayList<>();

    boolean dragActive = false;

    QueueLine queueLine;


    QueueBox(MenuController menuController){
        this.menuController = menuController;
        this.setAlignment(Pos.TOP_CENTER);
        this.getStyleClass().add("menuBox");
        this.setId("queueBox");
        this.setFillWidth(true);
        VBox.setVgrow(this, Priority.ALWAYS);


        this.setOnDragEntered(e -> handleDragEntered(e));
        this.setOnDragOver(e -> handleDragOver(e));
        this.setOnDragDropped(e -> handleDragDropped(e));
        this.setOnDragExited(e -> handleDragExited());

        queueLine = new QueueLine(this);
    }

    public void add(int index, QueueItem child){


        if(index < 0) return;
        else if(index >= this.getChildren().size()){
            this.add(child);
            return;
        }

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

    }

    public void addRand(QueueItem child){
        Random random = new Random();
        int index = random.nextInt(menuController.queue.size() + 1);
        if(index >= menuController.queue.size()) add(child);
        else add(index, child);
    }

    public void add(QueueItem child){

        cancelDrag();

        menuController.queue.add(child);

        // add item with opacity 0, then fade it in
        if(getChildren().isEmpty()) {
            this.getChildren().add(child);
        }
        else this.getChildren().add(child);

        initialize(child);
        FadeTransition fadeTransition = AnimationsClass.fadeIn(child);
        fadeTransition.playFromStart();
    }

    public void remove(QueueItem child){
        if(menuController.queue.contains(child)){
            this.remove(menuController.queue.indexOf(child));
        }
    }

    public void remove(int index){

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
        }
    }

    //TODO: implement Collections.rotate instead of removing and adding
    public void removeAndMove(int index){

        cancelDrag();

        // removes item at index from the queuebox, moves all previous items to the bottom
        if(index < 0 || index >= getChildren().size()) return;
        if(index ==0){
            remove(index);
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

    }

    public void moveAll(int firstBound, int secondBound, int newIndex){

        cancelDrag();

        if(this.getChildren().size() < 3 ||
                firstBound == newIndex ||
                firstBound < 0 ||
                firstBound >= this.getChildren().size() - 1 ||
                newIndex < -1 ||
                newIndex >= this.getChildren().size() - 1 ||
                secondBound < 0 ||
                secondBound >= this.getChildren().size() ||
                secondBound - firstBound + newIndex >= this.getChildren().size() ||
                firstBound >= secondBound) return;

        ParallelTransition parallelTransition = new ParallelTransition();

        ArrayList<QueueItem> nodesInRange = new ArrayList<>();

        for(int i = firstBound; i <= secondBound; i++){
            // all the children that will be moved
            nodesInRange.add((QueueItem) this.getChildren().get(i));
            FadeTransition fadeTransition = AnimationsClass.fadeOut(this.getChildren().get(i));
            parallelTransition.getChildren().add(fadeTransition);
        }

        menuController.queue.removeAll(nodesInRange);
        if(newIndex == -1) menuController.queue.addAll(nodesInRange);
        else menuController.queue.addAll(newIndex, nodesInRange);

        parallelTransition.setOnFinished((e) -> {
            ArrayList<QueueItem> childrenToBeMoved = new ArrayList<>();
            ParallelTransition parallelTranslateTransition = new ParallelTransition();

            if(newIndex > firstBound || newIndex == -1){
                // move items down
                int loopTo = newIndex == -1 ? this.getChildren().size() : secondBound - firstBound + newIndex + 1;
                for(int i = secondBound + 1; i < loopTo; i++){
                    childrenToBeMoved.add((QueueItem) this.getChildren().get(i));
                    parallelTranslateTransition.getChildren().add(AnimationsClass.animateUp(this.getChildren().get(i), QueueItem.height * (secondBound - firstBound + 1)));
                }
            }
            else {
                // move items up
                for(int i = firstBound - 1; i>= newIndex; i--){
                    childrenToBeMoved.add((QueueItem) this.getChildren().get(i));
                    parallelTranslateTransition.getChildren().add(AnimationsClass.animateDown(this.getChildren().get(i), QueueItem.height * (secondBound - firstBound + 1)));
                }
            }

            parallelTranslateTransition.setOnFinished((ev) -> {
                this.getChildren().removeAll(nodesInRange);
                for (QueueItem queueItem : childrenToBeMoved) {
                    queueItem.setTranslateY(0);
                }
                if(newIndex == -1) this.getChildren().addAll(nodesInRange);
                else this.getChildren().addAll(newIndex, nodesInRange);

                ParallelTransition parallelFadeTransition = new ParallelTransition();
                for(QueueItem queueItem : nodesInRange){
                    parallelFadeTransition.getChildren().add(AnimationsClass.fadeIn(queueItem));
                }
                menuController.animationsInProgress.remove(parallelTranslateTransition);
                parallelFadeTransition.playFromStart();

            });
            menuController.animationsInProgress.remove(parallelTransition);
            menuController.animationsInProgress.add(parallelTranslateTransition);
            parallelTranslateTransition.playFromStart();
        });

        menuController.animationsInProgress.add(parallelTransition);
        parallelTransition.playFromStart();

    }

    public void addAll(Collection<? extends QueueItem> collection){

        cancelDrag();

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

    }

    public void addAll(int index, Collection<? extends QueueItem> collection) {

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
    }

    public void clear(){

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

        }
    }

    public void move(int oldIndex, int newIndex){

        cancelDrag();

        // move to bottom if newIndex = -1

        // massive guard clause
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

        if(newIndex == -1 || newIndex > oldIndex){

            FadeTransition fadeTransition = AnimationsClass.fadeOut(child);
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

            menuController.animationsInProgress.add(fadeTransition);
            fadeTransition.playFromStart();

        }

        else {
            // move item up

            FadeTransition fadeTransition = AnimationsClass.fadeOut(child);
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

            menuController.animationsInProgress.add(fadeTransition);
            fadeTransition.playFromStart();
        }

    }


    public void shuffle(){

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

            parallelFadeIn.setOnFinished(k -> {
                menuController.animationsInProgress.remove(parallelFadeIn);
            });

            menuController.animationsInProgress.add(parallelFadeIn);
            parallelFadeIn.playFromStart();

            menuController.animationsInProgress.remove(parallelFadeOut);
        });

        menuController.animationsInProgress.add(parallelFadeOut);
        parallelFadeOut.playFromStart();

    }


    public void handleDragEntered(DragEvent e) {

        dragBoardFiles = (ArrayList<File>) e.getDragboard().getFiles();

        for(File file : dragBoardFiles){
            if(Utilities.getFileExtension(file).equals("mp4") || Utilities.getFileExtension(file).equals("mp3")){

                dragBoardMedia.add(file);
            }
        }

        if(dragBoardMedia.isEmpty()) return;

        dragActive = true;
        if(!menuController.queue.isEmpty()){
            queueLine.setPosition(-1);
        }

    }

    public void handleDragOver(DragEvent e){
        if(!dragBoardMedia.isEmpty()){
            e.acceptTransferModes(TransferMode.COPY);
        }
    }

    public void handleDragDropped(DragEvent e){

        dragActive = false;

        if(dragBoardMedia.isEmpty()) return;

        // add mp4 and mp3 files to mediainterface queue, create queue objects in the menu, show popup indicating how many videos were added to the queue and a blinking indicator inside the queue tab button to show how many new videos have been to the queue in total

        ArrayList<QueueItem> newItems = new ArrayList<>();

        for(File file : dragBoardMedia){
            MediaItem temp = null;

            if(Utilities.getFileExtension(file).equals("mp4")) temp = new Mp4Item(file);
            else if(Utilities.getFileExtension(file).equals("mp3")) temp = new Mp3Item(file);

            newItems.add(new QueueItem(temp, menuController, menuController.mediaInterface, this));
        }

        addAll(getChildren().indexOf(queueLine), newItems);


        dragBoardMedia.clear();

    }

    public void handleDragExited(){
        cancelDrag();
    }


    public void cancelDrag(){
        dragActive = false;
        dragBoardMedia.clear();
        dragBoardFiles.clear();

        getChildren().remove(queueLine);
    }


    public void initialize(QueueItem queueItem){
        Platform.runLater(() -> {
            queueItem.play = new ControlTooltip("Play video", queueItem.playButton, new VBox(), 1000, false);
            queueItem.remove = new ControlTooltip("Remove video", queueItem.removeButton, new VBox(), 1000, false);
            queueItem.options = new ControlTooltip("Options", queueItem.optionsButton, new VBox(), 1000, false);
            queueItem.optionsPopUp = new MenuItemOptionsPopUp(queueItem);
        });
    }

}
