package hans.Menu.Queue;

import hans.AnimationsClass;
import hans.ControlTooltip;
import hans.MediaItems.MediaUtilities;
import hans.Menu.MenuController;
import hans.Menu.QueueItemContextMenu;
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
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class QueueBox extends VBox {

    QueuePage queuePage;
    MenuController menuController;


    List<File> dragBoardFiles = new ArrayList<>();
    ArrayList<File> dragBoardMedia = new ArrayList<>();


    public BooleanProperty dragAndDropActive = new SimpleBooleanProperty(false);
    public BooleanProperty itemDragActive = new SimpleBooleanProperty(false);

    public QueueItem draggedNode = null;

    public IntegerProperty activeIndex = new SimpleIntegerProperty(-1); // what index we are at in the queueorder arraylist
    public ObjectProperty<QueueItem> activeItem = new SimpleObjectProperty<>();

    public ArrayList<QueueItem> queue = new ArrayList<>();
    public ObservableList<Integer> queueOrder = FXCollections.observableArrayList();


    public DropPositionController dropPositionController;


    public QueueBox(MenuController menuController, QueuePage queuePage){

        this.queuePage = queuePage;
        this.menuController = menuController;

        this.setAlignment(Pos.TOP_CENTER);
        this.setFillWidth(true);

        dropPositionController = new DropPositionController(this);
        VBox.setVgrow(this, Priority.ALWAYS);
        this.setPadding(new Insets(0, 0, 20, 0));



        this.setOnDragEntered(this::handleDragEntered);
        this.setOnDragOver(this::handleDragOver);
        this.setOnDragDropped(this::handleDragDropped);
        this.setOnDragExited(this::handleDragExited);


        activeIndex.addListener((observableValue, oldValue, newValue) -> menuController.controlBarController.updateNextAndPreviousVideoButtons());

        dragAndDropActive.addListener((observableValue, oldValue, newValue) -> {
            if(newValue) startDragAction();
            else stopDragAction();
        });

        itemDragActive.addListener((observableValue, oldValue, newValue) -> {
            if(newValue) startDragAction();
            else stopDragAction();
        });
    }

    public void add(int index, QueueItem child, boolean isDrag){


        if(index < 0) return;
        else if(index >= this.getChildren().size()){
            this.add(child, isDrag);
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

        if(child.isActive.get()) activeIndex.set(index);
        else if(activeIndex.get() != -1 && index < activeIndex.get()) activeIndex.set(activeIndex.get() + 1);

        updateQueue();

        this.getChildren().add(index, child);
        initialize(child);

        Timeline minHeightTransition = AnimationsClass.animateMinHeight(QueueItem.height, child);
        Timeline maxHeightTransition = AnimationsClass.animateMaxHeight(QueueItem.height, child);
        ParallelTransition parallelTransition = new ParallelTransition(minHeightTransition, maxHeightTransition);
        if(isDrag) parallelTransition.playFromStart();
        else {
            FadeTransition fadeTransition = AnimationsClass.fadeIn(child);
            SequentialTransition sequentialTransition = new SequentialTransition(parallelTransition, fadeTransition);
            sequentialTransition.playFromStart();
        }
    }

    public void addRand(QueueItem child) {
        Random random = new Random();
        int index;
        if(activeIndex.get() == -1) index = random.nextInt(queueOrder.size() + 1);
        else if(activeIndex.get() == queueOrder.size() -1) index = queueOrder.size(); // add to the end of the queue
        else index = activeIndex.get() + 1 + random.nextInt(queueOrder.size() + 1 - activeIndex.get());

        this.add(index, child, false);
    }

    public void add(QueueItem child, boolean isDrag){

        queue.add(child);
        queueOrder.add(queueOrder.size());

        if(child.isActive.get()) activeIndex.set(queue.size() - 1);

        updateQueue();

        // add item with opacity 0, then fade it in
        this.getChildren().add(child);
        initialize(child);

        Timeline minHeightTransition = AnimationsClass.animateMinHeight(QueueItem.height, child);
        Timeline maxHeightTransition = AnimationsClass.animateMaxHeight(QueueItem.height, child);
        ParallelTransition parallelTransition = new ParallelTransition(minHeightTransition, maxHeightTransition);
        if(isDrag) parallelTransition.playFromStart();
        else {
            FadeTransition fadeTransition = AnimationsClass.fadeIn(child);
            SequentialTransition sequentialTransition = new SequentialTransition(parallelTransition, fadeTransition);
            sequentialTransition.playFromStart();
        }
    }

    public void remove(QueueItem child, boolean isDrag){
        if(queue.contains(child)){
            this.remove(queue.indexOf(child), isDrag);
        }
    }

    public void remove(int index, boolean isDrag){



        if(index < 0 || queue.isEmpty() || index >= queue.size()) return;

        QueueItem queueItem = queue.get(index);

        queue.remove(index);
        queueOrder.remove((Integer) index);
        if(!isDrag) queuePage.selectedItems.remove(queueItem);

        if(queueOrder.size() != index){
            for(int i=0; i<queueOrder.size(); i++){
                if(queueOrder.get(i) > index) queueOrder.set(i, queueOrder.get(i) -1);
            }
        }

        if(activeIndex.get() != -1 && index < activeIndex.get()) activeIndex.set(activeIndex.get() - 1);

        updateQueue();

        if(isDrag){
            this.getChildren().remove(queueItem);
            return;
        }

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
        queuePage.selectedItems.clear();

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


        if(draggedNode != null){
            itemDragActive.set(true);
        }
        else if(!itemDragActive.get()){
            Dragboard dragboard = e.getDragboard();

            dragBoardFiles = dragboard.getFiles();

            for(File file : dragBoardFiles){
                String extension = Utilities.getFileExtension(file);
                if(MediaUtilities.mediaFormats.contains(extension))
                    dragBoardMedia.add(file);
            }

            if(dragBoardMedia.isEmpty()) return;


            dragAndDropActive.set(true);
        }
    }

    public void handleDragOver(DragEvent e){
        if((dragAndDropActive.get() && !dragBoardMedia.isEmpty()) || itemDragActive.get()) {
            e.acceptTransferModes(TransferMode.COPY_OR_MOVE);

            dropPositionController.updateY(e.getY());
        }
    }

    public void handleDragDropped(DragEvent e){

        if(dragAndDropActive.get()){
            handleFileDragDrop();
        }
        else if(itemDragActive.get()){
            handleItemDragDrop();
        }
    }

    public void handleDragExited(DragEvent e){
        cancelDragAndDrop();
    }


    public void cancelDragAndDrop(){
        dragAndDropActive.set(false);
        itemDragActive.set(false);
        dragBoardFiles.clear();
        dragBoardMedia.clear();
    }



    public void initialize(QueueItem queueItem){
        Platform.runLater(() -> {
            queueItem.playButtonTooltip = new ControlTooltip(menuController.mainController, "Play video", queueItem.playButton, 1000);
            queueItem.removeButtonTooltip = new ControlTooltip(menuController.mainController,"Remove video", queueItem.removeButton, 1000);
            queueItem.optionsButtonTooltip = new ControlTooltip(menuController.mainController, "Options", queueItem.optionsButton, 1000);
            queueItem.menuItemContextMenu = new QueueItemContextMenu(queueItem);

            QueueItem.height = queueItem.getBoundsInParent().getHeight();

        });
    }

    private void updateQueue(){

        queuePage.clearQueueButton.setDisable(queueOrder.isEmpty());

        for(int i=0; i < queueOrder.size(); i++){
            queue.get(queueOrder.get(i)).updateIndex(i);
        }

        menuController.controlBarController.updateNextAndPreviousVideoButtons();
    }

    private void startDragAction(){

        if(!dropPositionController.translateTransitions.isEmpty()){
            for(Transition transition : dropPositionController.translateTransitions){
                transition.stop();
            }

            dropPositionController.translateTransitions.clear();
        }

        for(Node node : this.getChildren()){
            node.setTranslateY(0);
        }


        ParallelTransition parallelTransition = new ParallelTransition();
        for(QueueItem queueItem : queue){
            if((queuePage.selectionActive.get() && queuePage.selectedItems.contains(draggedNode) && queuePage.selectedItems.contains(queueItem)) || draggedNode == queueItem) continue;
            FadeTransition fadeTransition = new FadeTransition(Duration.millis(300), queueItem);
            fadeTransition.setFromValue(queueItem.getOpacity());
            fadeTransition.setToValue(0.5);
            parallelTransition.getChildren().add(fadeTransition);
        }

        if(!parallelTransition.getChildren().isEmpty()) parallelTransition.playFromStart();

        if(itemDragActive.get() && draggedNode != null){

            for(QueueItem queueItem : queue){
                queueItem.setViewOrder(0);
            }

            draggedNode.setViewOrder(1);

            if(queuePage.selectionActive.get() && queuePage.selectedItems.contains(draggedNode)){ // drag multiple items
                ParallelTransition parallelRemove = new ParallelTransition();
                for(QueueItem queueItem : queuePage.selectedItems){
                    if(queueItem != draggedNode) queueItem.setMouseTransparent(true);

                    if(queueItem.getOpacity() == 0) continue;

                    FadeTransition fadeTransition = AnimationsClass.fadeOut(queueItem);

                    if(queueItem == draggedNode){
                        parallelRemove.getChildren().add(fadeTransition);
                    }
                    else {
                        Timeline minHeightTransition = AnimationsClass.animateMinHeight(0, queueItem);
                        Timeline maxHeightTransition = AnimationsClass.animateMaxHeight(0, queueItem);
                        ParallelTransition heightTransition = new ParallelTransition(minHeightTransition, maxHeightTransition);
                        SequentialTransition sequentialTransition = new SequentialTransition(fadeTransition, heightTransition);

                        parallelRemove.getChildren().add(sequentialTransition);
                    }
                }

                if(!parallelRemove.getChildren().isEmpty()){
                    dropPositionController.removeTransitions.add(parallelRemove);
                    parallelRemove.setOnFinished(e -> dropPositionController.removeTransitions.remove(parallelRemove));
                    parallelRemove.playFromStart();
                }
            }
            else { // drag just the single queueitem

                if(draggedNode.getOpacity() == 0) return;

                FadeTransition fadeTransition = AnimationsClass.fadeOut(draggedNode);
                dropPositionController.removeTransitions.add(fadeTransition);
                fadeTransition.setOnFinished(e -> dropPositionController.removeTransitions.remove(fadeTransition));

                fadeTransition.playFromStart();
            }
        }
    }

    private void stopDragAction(){

        if(!dropPositionController.translateTransitions.isEmpty()){
            for(Transition transition : dropPositionController.translateTransitions) transition.stop();

            dropPositionController.translateTransitions.clear();
        }

        for(Node node : this.getChildren()){
            node.setTranslateY(0);
        }

        for(QueueItem queueItem : queue){
            queueItem.setViewOrder(0);
        }

        if(menuController.extended) this.setPadding(new Insets(0, 50, 20, 50));
        else this.setPadding(new Insets(0, 0, 20, 0));

        dropPositionController.position = Integer.MAX_VALUE;

        ParallelTransition parallelTransition = new ParallelTransition();
        for(QueueItem queueItem : queue){
            if((queuePage.selectionActive.get() && queuePage.selectedItems.contains(draggedNode) && queuePage.selectedItems.contains(queueItem)) || draggedNode == queueItem) continue;
            FadeTransition fadeTransition = new FadeTransition(Duration.millis(300), queueItem);
            fadeTransition.setFromValue(queueItem.getOpacity());
            fadeTransition.setToValue(1);
            parallelTransition.getChildren().add(fadeTransition);
        }

        if(!parallelTransition.getChildren().isEmpty()) parallelTransition.playFromStart();
    }

    public void handleItemDragDrop(){

        if(!dropPositionController.removeTransitions.isEmpty()){
            for(Transition transition : dropPositionController.removeTransitions){
                transition.stop();
            }

            dropPositionController.removeTransitions.clear();
        }

        QueueItem hoverItem = null;
        int correction = 0;

        dropPositionController.position = Math.min(queue.size(), dropPositionController.position);

        for(int i = dropPositionController.position; i >= 0; i--){

            if(i >= queue.size()) continue;
            if(queue.get(queueOrder.get(i)) == draggedNode || queuePage.selectedItems.contains(queue.get(queueOrder.get(i)))) continue;

            hoverItem = queue.get(queueOrder.get(i));
            if(i != dropPositionController.position) correction = 1;

            break;
        }
        if(hoverItem == null){
            for(int i = dropPositionController.position; i<queue.size(); i++){
                if(queue.get(queueOrder.get(i)) == draggedNode || queuePage.selectedItems.contains(queue.get(queueOrder.get(i)))) continue;

                hoverItem = queue.get(queueOrder.get(i));
                correction = 0;

                break;
            }
        }


        if(queuePage.selectionActive.get() && queuePage.selectedItems.contains(draggedNode)){
            for(QueueItem queueItem : queuePage.selectedItems){
                remove(queueItem, true);
            }
        }
        else remove(draggedNode, true);

        int index = Integer.MAX_VALUE;
        if(hoverItem != null) index = queueOrder.indexOf(queue.indexOf(hoverItem)) + correction;

        if(queuePage.selectionActive.get() && queuePage.selectedItems.contains(draggedNode)){
            if(index >= queue.size()){
                for(int i=0; i<queuePage.selectedItems.size(); i++){
                    QueueItem queueItem = queuePage.selectedItems.get(i);

                    queueItem.setMinHeight(QueueItem.height);
                    queueItem.setMaxHeight(QueueItem.height);
                    queueItem.setOpacity(0);
                    queueItem.setMouseTransparent(false);

                    add(queueItem, true);
                }
            }
            else {
                for(int i=0; i<queuePage.selectedItems.size(); i++){
                    QueueItem queueItem = queuePage.selectedItems.get(i);
                    if(i == 0){
                        queueItem.setMinHeight(QueueItem.height);
                        queueItem.setMaxHeight(QueueItem.height);
                    }
                    else {
                        queueItem.setMinHeight(0);
                        queueItem.setMaxHeight(0);
                    }

                    queueItem.setOpacity(0);
                    queueItem.setMouseTransparent(false);

                    add(index + i, queueItem, true);
                }
            }

        }
        else {

            draggedNode.setMinHeight(QueueItem.height);
            draggedNode.setMaxHeight(QueueItem.height);
            draggedNode.setOpacity(0);
            draggedNode.setMouseTransparent(false);

            add(index, draggedNode, true);
        }

        draggedNode = null;
        itemDragActive.set(false);
    }

    public void handleFileDragDrop(){

        if (dragBoardMedia.isEmpty()){
            dragAndDropActive.set(false);
            return;
        }

        double translation = 0;
        if(queue.size() > dropPositionController.position) translation = queue.get(queueOrder.get(dropPositionController.position)).getTranslateY();

        int index = dropPositionController.position;

        dragAndDropActive.set(false);

        boolean last = index >= queue.size();

        for (int i=0; i < dragBoardMedia.size(); i++) {
            QueueItem queueItem;
            if(i == 0) queueItem = new QueueItem(dragBoardMedia.get(i), queuePage, menuController, menuController.mediaInterface, translation);
            else queueItem = new QueueItem(dragBoardMedia.get(i), queuePage, menuController, menuController.mediaInterface, 0);

            this.add(index + i, queueItem, false);
        }

        if(last){
            if(dragBoardMedia.size() == 1){
                Platform.runLater(() -> queuePage.queueScroll.setVvalue(1));
            }
        }

        dragBoardMedia.clear();
        dragBoardFiles.clear();
    }

    public void extend(){
        this.setPadding(new Insets(0, 50,20, 50));

        QueueItem.height = 100;

        for(QueueItem queueItem : this.queue){
            queueItem.updateHeight();
            queueItem.applyRoundStyling();
        }


    }

    public void shrink(){
        this.setPadding(new Insets(0, 0,20, 0));

        QueueItem.height = 100;

        for(QueueItem queueItem : this.queue){
            queueItem.updateHeight();
            queueItem.removeRoundStyling();
        }
    }

}
