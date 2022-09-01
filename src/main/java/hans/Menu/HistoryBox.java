package hans.Menu;

import hans.*;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.util.ArrayList;

public class HistoryBox extends VBox {

    double animationSpeed = 200;
    double height = 0;
    MenuController menuController;

    boolean open = false;
    public int index = -1;
    final int CAPACITY = 20;

    StackPane historyWrapper;

    ParallelTransition openHistory, closeHistory;

    HistoryBox(MenuController menuController, StackPane historyWrapper){

        this.historyWrapper = historyWrapper;
        this.menuController = menuController;
        this.setAlignment(Pos.TOP_CENTER);
        this.setFillWidth(true);

    }

    public void add(HistoryItem historyItem){

        if(menuController.history.isEmpty()) menuController.enableHistoryButton();

        if(menuController.history.size() >= CAPACITY) menuController.history.remove(0);
        menuController.history.add(historyItem);

        if(menuController.history.size() == 1) menuController.historySizeText.setText(String.format("(%d item)", menuController.history.size()));
        else menuController.historySizeText.setText(String.format("(%d items)", menuController.history.size()));
        System.out.println(menuController.history.size());

        if(!open){
            if(getChildren().size() < CAPACITY){
                height += HistoryItem.height;

                if(getChildren().size() == 1) {
                    Platform.runLater(() -> {
                        HistoryItem.height = menuController.history.get(0).getHeight();
                        height = HistoryItem.height * getChildren().size();
                    });
                }

                getChildren().add(historyItem);
                initialize(historyItem);
            }
            else {
                getChildren().remove(0);
                getChildren().add(historyItem);
            }

            historyItem.setOpacity(1);

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
            historyItem.optionsButtonTooltip = new ControlTooltip("Options", historyItem.optionsButton, 1000);
            historyItem.optionsPopUp = new MenuItemOptionsPopUp(historyItem);
        });
    }

    public void open(){

        if((closeHistory != null && closeHistory.getStatus() == Animation.Status.RUNNING) || menuController.history.isEmpty()) return;

        menuController.historyTooltip.updateText("Close history");

        open = true;

        Timeline minTimeline = new Timeline(new KeyFrame(Duration.millis(300),
                new KeyValue(historyWrapper.minHeightProperty(), height, Interpolator.EASE_BOTH)));
        Timeline maxTimeline = new Timeline(new KeyFrame(Duration.millis(300),
                new KeyValue(historyWrapper.maxHeightProperty(), height, Interpolator.EASE_BOTH)));

        openHistory = new ParallelTransition(minTimeline, maxTimeline);


        openHistory.playFromStart();
        menuController.historyIconPath.setContent(App.svgMap.get(SVG.CHEVRON_UP));
        menuController.historyIcon.setShape(menuController.historyIconPath);
    }

    public void close(){

        if(openHistory != null && openHistory.getStatus() == Animation.Status.RUNNING) return;

        menuController.historyTooltip.updateText("Open history");

        open = false;

        Timeline minTimeline = new Timeline(new KeyFrame(Duration.millis(300),
                new KeyValue(historyWrapper.minHeightProperty(), 0, Interpolator.EASE_BOTH)));
        Timeline maxTimeline = new Timeline(new KeyFrame(Duration.millis(300),
                new KeyValue(historyWrapper.maxHeightProperty(), 0, Interpolator.EASE_BOTH)));

        closeHistory = new ParallelTransition(minTimeline, maxTimeline);


        closeHistory.playFromStart();
        menuController.historyIconPath.setContent(App.svgMap.get(SVG.CHEVRON_DOWN));
        menuController.historyIcon.setShape(menuController.historyIconPath);
    }

}