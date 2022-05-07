package hans;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;

public class HistoryBox extends VBox {

    double animationSpeed = 200;
    double height = 0;
    MenuController menuController;

    boolean open = true;
    int index = -1;
    final int CAPACITY = 20;

    StackPane historyWrapper;

    Timeline openHistory, closeHistory;


    HistoryBox(MenuController menuController, StackPane historyWrapper){

        this.historyWrapper = historyWrapper;
        this.menuController = menuController;
        this.setAlignment(Pos.TOP_CENTER);
        this.setStyle("-fx-background-color: white;");

        this.setFillWidth(true);

        this.setAlignment(Pos.TOP_LEFT);
    }

    public void add(HistoryItem historyItem){

        if(menuController.history.size() >= CAPACITY) menuController.history.remove(0);
        menuController.history.add(historyItem);

        if(!open){

            historyItem.setOpacity(1);

            if(getChildren().size() < CAPACITY){
                height += HistoryItem.height;

                if(getChildren().isEmpty()) {
                    this.getChildren().add(historyItem);
                    initialize(historyItem);
                    Platform.runLater(() -> {
                        HistoryItem.height = historyItem.getHeight();
                        height = HistoryItem.height * getChildren().size();
                    });
                }
                else {
                    this.getChildren().add(historyItem);
                    initialize(historyItem);
                }

            }
            else {
                getChildren().remove(0);
                getChildren().add(historyItem);
            }
        }
        else {

            if(getChildren().size() < CAPACITY){

                PauseTransition pauseTransition = new PauseTransition(Duration.millis(animationSpeed));

                pauseTransition.setOnFinished(k -> {
                    height+= HistoryItem.height;
                    historyWrapper.setMaxHeight(height);
                    Timeline heightAnimation = AnimationsClass.animateMinHeight(height, historyWrapper);
                    heightAnimation.setOnFinished((e) -> {
                        // add item with opacity 0, then fade it in
                        if(getChildren().isEmpty()) {
                            getChildren().add(historyItem);
                            initialize(historyItem);
                            Platform.runLater(() -> {
                                HistoryItem.height = historyItem.getHeight();
                                System.out.println(historyItem.getHeight());
                                height = HistoryItem.height * getChildren().size();
                                historyWrapper.setMaxHeight(height);
                            });
                        }
                        else {
                            getChildren().add(historyItem);
                            initialize(historyItem);
                        }
                        FadeTransition fadeTransition = AnimationsClass.fadeIn(historyItem);
                        menuController.animationsInProgress.remove(heightAnimation);
                        fadeTransition.playFromStart();
                    });
                    menuController.animationsInProgress.remove(pauseTransition);
                    menuController.animationsInProgress.add(heightAnimation);
                    heightAnimation.playFromStart();
                });

                menuController.animationsInProgress.add(pauseTransition);
                pauseTransition.playFromStart();
            }
            else {
                ParallelTransition parallelTranslate = new ParallelTransition();
                ArrayList<HistoryItem> itemsToBeTranslated = new ArrayList<>();
                FadeTransition fadeOut = AnimationsClass.fadeOut(getChildren().get(0));
                FadeTransition fadeIn = AnimationsClass.fadeIn(historyItem);
                fadeOut.setOnFinished(e -> {
                    for(int i = 1; i < CAPACITY; i++){
                        itemsToBeTranslated.add((HistoryItem) getChildren().get(i));
                        parallelTranslate.getChildren().add(AnimationsClass.animateUp(getChildren().get(i), HistoryItem.height));
                    }

                    parallelTranslate.setOnFinished(g -> {

                        getChildren().remove(0);
                        for(HistoryItem historyItem1 : itemsToBeTranslated){
                            historyItem1.setTranslateY(0);
                        }
                        getChildren().add(historyItem);
                        initialize(historyItem);

                        menuController.animationsInProgress.remove(parallelTranslate);
                        fadeIn.playFromStart();


                    });

                    menuController.animationsInProgress.remove(fadeOut);
                    menuController.animationsInProgress.add(parallelTranslate);
                    parallelTranslate.playFromStart();
                });

                menuController.animationsInProgress.add(fadeOut);
                fadeOut.playFromStart();
            }
        }

    }

    public void initialize(HistoryItem historyItem){
        Platform.runLater(() -> {
            historyItem.play = new ControlTooltip("Play video", historyItem.playButton, new VBox(), 1000, false);
            historyItem.options = new ControlTooltip("Options", historyItem.optionsButton, new VBox(), 1000, false);
            historyItem.optionsPopUp = new MenuItemOptionsPopUp(historyItem);
        });
    }

    public void open(){

        if(closeHistory != null && closeHistory.getStatus() == Animation.Status.RUNNING) return;

        openHistory = new Timeline(new KeyFrame(Duration.millis(300),
                new KeyValue(historyWrapper.minHeightProperty(), height, Interpolator.EASE_BOTH)));

        openHistory.setOnFinished(e -> {
            historyWrapper.setMinHeight(height);
            historyWrapper.setMaxHeight(height);
            open = true;
        });

        openHistory.playFromStart();
    }

    public void close(){

        if(openHistory != null && openHistory.getStatus() == Animation.Status.RUNNING) return;

        open = false;
        System.out.println("Closing history");

        historyWrapper.setMinHeight(0);

        closeHistory = new Timeline(new KeyFrame(Duration.millis(300),
                new KeyValue(historyWrapper.maxHeightProperty(), 0, Interpolator.EASE_BOTH)));

        closeHistory.setOnFinished(e -> {
            historyWrapper.setMaxHeight(0);
            historyWrapper.setMinHeight(0);
        });

        closeHistory.playFromStart();
    }

}