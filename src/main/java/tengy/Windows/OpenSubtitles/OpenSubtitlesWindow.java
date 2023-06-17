package tengy.Windows.OpenSubtitles;

import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import tengy.*;
import javafx.animation.FadeTransition;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import tengy.Menu.Queue.QueueBox;
import tengy.Subtitles.SubtitlesState;
import tengy.Windows.WindowController;
import tengy.Windows.WindowState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static tengy.Utilities.keyboardFocusOff;
import static tengy.Utilities.keyboardFocusOn;

public class OpenSubtitlesWindow {

    WindowController windowController;
    MainController mainController;


    public StackPane window = new StackPane();



    StackPane buttonContainer = new StackPane();
    public Button helpButton = new Button("Help");
    public Button connectionButton = new Button("Connection");
    public Button mainButton = new Button("Close");

    public boolean showing = false;

    public boolean animating = false;

    IntegerProperty focus = new SimpleIntegerProperty(-1);
    public List<Node> focusNodes = new ArrayList<>();

    public OpenSubtitlesState openSubtitlesState = OpenSubtitlesState.SEARCH_OPEN;

    public SearchPage searchPage;
    public ConnectionPage connectionPage;
    public HelpPage helpPage;
    public ResultsPage resultsPage;

    public OpenSubtitlesWindow(WindowController windowController){
        this.windowController = windowController;
        this.mainController = windowController.mainController;

        searchPage = new SearchPage(this);
        connectionPage = new ConnectionPage(this);
        helpPage = new HelpPage(this);
        resultsPage = new ResultsPage(this);

        mainController.popupWindowContainer.getChildren().add(window);

        window.setAlignment(Pos.TOP_LEFT);

        window.setMinSize(600, 350);
        window.setMaxSize(600, 350);
        window.setOnMouseClicked(e -> window.requestFocus());
        window.getStyleClass().add("popupWindow");
        window.setVisible(false);


        StackPane.setAlignment(buttonContainer, Pos.BOTTOM_CENTER);
        buttonContainer.getChildren().addAll(helpButton, connectionButton, mainButton);
        buttonContainer.getStyleClass().add("buttonContainer");
        buttonContainer.setPadding(new Insets(0, 15, 0, 15));
        buttonContainer.setPrefHeight(70);
        buttonContainer.setMaxHeight(70);

        helpButton.getStyleClass().add("menuButton");
        helpButton.setTextAlignment(TextAlignment.CENTER);
        helpButton.setPrefWidth(140);
        helpButton.setOnAction(e -> openHelpPage());
        helpButton.setFocusTraversable(false);
        helpButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(1);
            }
            else{
                keyboardFocusOff(helpButton);
                focus.set(-1);
            }
        });

        helpButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            helpButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        helpButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            helpButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });
        StackPane.setAlignment(helpButton, Pos.CENTER_LEFT);



        connectionButton.getStyleClass().add("menuButton");
        connectionButton.setTextAlignment(TextAlignment.CENTER);
        connectionButton.setPrefWidth(140);
        connectionButton.setOnAction(e -> openConnectionPage());
        connectionButton.setFocusTraversable(false);
        connectionButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(focusNodes.size() - 2);
            }
            else{
                keyboardFocusOff(connectionButton);
                focus.set(-1);
            }
        });

        connectionButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            connectionButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        connectionButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            connectionButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });
        StackPane.setAlignment(connectionButton, Pos.CENTER_LEFT);
        StackPane.setMargin(connectionButton, new Insets(0, 0, 0, 150));

        mainButton.getStyleClass().add("menuButton");
        mainButton.setTextAlignment(TextAlignment.CENTER);
        mainButton.setPrefWidth(180);
        mainButton.setOnAction(e -> this.hide());
        mainButton.setFocusTraversable(false);
        mainButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(focusNodes.size() - 1);
            }
            else{
                keyboardFocusOff(mainButton);
                focus.set(-1);
            }
        });

        mainButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            mainButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        mainButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            mainButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });
        StackPane.setAlignment(mainButton, Pos.CENTER_RIGHT);


        window.getChildren().addAll(searchPage, connectionPage, helpPage, resultsPage, buttonContainer);

        focusNodes.add(searchPage);
        focusNodes.add(helpButton);
        focusNodes.add(connectionButton);
        focusNodes.add(mainButton);
    }

    public void show(){

        windowController.updateState(WindowState.OPEN_SUBTITLES_OPEN);

        this.showing = true;
        window.setVisible(true);

        mainController.popupWindowContainer.setMouseTransparent(false);

        QueueBox queueBox = mainController.getMenuController().queuePage.queueBox;

        if(queueBox.activeItem.get() != null && queueBox.activeItem.get().getMediaItem() != null){
            Map<String, String> metadata = queueBox.activeItem.get().getMediaItem().getMediaInformation();
            if(metadata.containsKey("title") && !metadata.get("title").isBlank()) searchPage.titleField.setText(metadata.get("title"));
            if(metadata.containsKey("season") && !metadata.get("season").isBlank()) searchPage.seasonField.setText(metadata.get("season"));
            if(metadata.containsKey("episode") && !metadata.get("episode").isBlank()) searchPage.episodeField.setText(metadata.get("episode"));
        }

        window.requestFocus();
        AnimationsClass.fadeAnimation(100, mainController.popupWindowContainer, 0 , 1, false, 1, true);
    }

    public void hide(){
        this.showing = false;

        windowController.windowState = WindowState.CLOSED;
        OpenSubtitlesState oldState = openSubtitlesState;
        openSubtitlesState = OpenSubtitlesState.SEARCH_OPEN;

        mainController.popupWindowContainer.setMouseTransparent(true);

        FadeTransition fadeTransition = new FadeTransition(Duration.millis(100), mainController.popupWindowContainer);
        fadeTransition.setFromValue(mainController.popupWindowContainer.getOpacity());
        fadeTransition.setToValue(0);
        fadeTransition.setOnFinished(e -> {
            window.setVisible(false);

            window.minHeightProperty().unbind();
            window.maxHeightProperty().unbind();
            window.setMinHeight(350);
            window.setMaxHeight(350);

            window.minWidthProperty().unbind();
            window.maxWidthProperty().unbind();
            window.setMinWidth(600);
            window.setMaxWidth(600);

            switch (oldState){
                case SEARCH_OPEN -> searchPage.reset();
                case CONNECTION_OPEN ->  connectionPage.reset();
                case HELP_OPEN -> helpPage.reset();
                case RESULTS_OPEN -> resultsPage.reset();
            }

            searchPage.setOpacity(1);
            searchPage.setVisible(true);
            focusNodes.clear();
            focusNodes.add(searchPage);
            focusNodes.add(helpButton);
            focusNodes.add(connectionButton);
            focusNodes.add(mainButton);
        });
        fadeTransition.play();
    }

    public void focusForward(){
        if(focus.get() == -1 || focus.get() == 0 || focus.get() >= focusNodes.size() - 1){
            Page page = (Page) focusNodes.get(0);
            boolean skipFocus = page.focusForward();

            if(skipFocus) keyboardFocusOn(focusNodes.get(1));
        }
        else {
            keyboardFocusOn(focusNodes.get(focus.get() + 1));
        }
    }

    public void focusBackward(){
        if(focus.get() == 0 || focus.get() == 1){
            Page page = (Page) focusNodes.get(0);
            boolean skipFocus = page.focusBackward();

            if(skipFocus) keyboardFocusOn(focusNodes.get(focusNodes.size() - 1));
        }
        else if(focus.get() == -1) keyboardFocusOn(focusNodes.get(focusNodes.size() - 1));
        else keyboardFocusOn(focusNodes.get(focus.get() - 1));

    }

    private void openConnectionPage(){
        if(openSubtitlesState == OpenSubtitlesState.CONNECTION_OPEN || animating) return;

        animating = true;
        window.setMouseTransparent(true);
        connectionButton.setDisable(true);

        window.minWidthProperty().unbind();
        window.maxWidthProperty().unbind();
        window.minHeightProperty().unbind();
        window.maxHeightProperty().unbind();


        OpenSubtitlesState oldState = openSubtitlesState;
        openSubtitlesState = OpenSubtitlesState.CONNECTION_OPEN;

        ParallelTransition parallelTransition = new ParallelTransition();

        FadeTransition contentFade = new FadeTransition(Duration.millis(200));
        switch (oldState){
            case SEARCH_OPEN -> contentFade.setNode(searchPage);
            case HELP_OPEN -> contentFade.setNode(helpPage);
            case RESULTS_OPEN -> contentFade.setNode(resultsPage);
        }

        contentFade.setFromValue(1);
        contentFade.setToValue(0);

        Timeline minHeightTransition = AnimationsClass.animateMinHeight(350, window);
        Timeline maxHeightTransition = AnimationsClass.animateMaxHeight(350, window);
        Timeline minWidthTransition = AnimationsClass.animateMinWidth(600, window);
        Timeline maxWidthTransition = AnimationsClass.animateMaxWidth(600, window);

        parallelTransition.getChildren().addAll(contentFade, minHeightTransition, maxHeightTransition, minWidthTransition, maxWidthTransition);


        parallelTransition.setOnFinished(e -> {
            switch (oldState){
                case SEARCH_OPEN -> {
                    searchPage.searchInProgress.set(false);

                    if(searchPage.executorService != null){
                        searchPage.executorService.shutdown();
                        searchPage.executorService = null;
                    }

                    searchPage.setOpacity(0);
                    searchPage.setVisible(false);
                    searchPage.scrollPane.setVvalue(0);
                    searchPage.errorLabel.setVisible(false);
                }
                case HELP_OPEN -> helpPage.reset();
                case RESULTS_OPEN -> resultsPage.reset();
            }

                connectionPage.setVisible(true);
                focusNodes.clear();
                focusNodes.add(connectionPage);
                focusNodes.add(helpButton);
                focusNodes.add(mainButton);

                FadeTransition fadeIn = new FadeTransition(Duration.millis(200), connectionPage);
                fadeIn.setFromValue(0);
                fadeIn.setToValue(1);
                fadeIn.setOnFinished(event -> {
                    animating = false;
                    window.setMouseTransparent(false);
                });

                fadeIn.playFromStart();
        });

        parallelTransition.playFromStart();
    }


    public void openSearchPage(){
        if(openSubtitlesState == OpenSubtitlesState.SEARCH_OPEN || animating) return;

        animating = true;
        window.setMouseTransparent(true);

        window.minWidthProperty().unbind();
        window.maxWidthProperty().unbind();
        window.minHeightProperty().unbind();
        window.maxHeightProperty().unbind();

        OpenSubtitlesState oldState = openSubtitlesState;
        openSubtitlesState = OpenSubtitlesState.SEARCH_OPEN;

        ParallelTransition parallelTransition = new ParallelTransition();

        FadeTransition contentFade = new FadeTransition(Duration.millis(200));
        switch (oldState){
            case CONNECTION_OPEN -> contentFade.setNode(connectionPage);
            case HELP_OPEN -> contentFade.setNode(helpPage);
            case RESULTS_OPEN -> contentFade.setNode(resultsPage);
        }

        contentFade.setFromValue(1);
        contentFade.setToValue(0);

        Timeline minHeightTransition = AnimationsClass.animateMinHeight(350, window);
        Timeline maxHeightTransition = AnimationsClass.animateMaxHeight(350, window);
        Timeline minWidthTransition = AnimationsClass.animateMinWidth(600, window);
        Timeline maxWidthTransition = AnimationsClass.animateMaxWidth(600, window);

        parallelTransition.getChildren().addAll(contentFade, minHeightTransition, maxHeightTransition, minWidthTransition, maxWidthTransition);

        parallelTransition.setOnFinished(e -> {
            switch (oldState){
                case CONNECTION_OPEN -> connectionPage.reset();
                case HELP_OPEN -> helpPage.reset();
                case RESULTS_OPEN -> resultsPage.reset();
            }


            searchPage.setVisible(true);
            focusNodes.clear();
            focusNodes.add(searchPage);
            focusNodes.add(helpButton);
            focusNodes.add(connectionButton);
            focusNodes.add(mainButton);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), searchPage);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.setOnFinished(event -> {
                animating = false;
                window.setMouseTransparent(false);
            });

            fadeIn.playFromStart();
        });

        parallelTransition.playFromStart();
    }

    private void openHelpPage(){
        if(openSubtitlesState == OpenSubtitlesState.HELP_OPEN || animating) return;

        animating = true;
        window.setMouseTransparent(true);
        helpButton.setDisable(true);

        window.minWidthProperty().unbind();
        window.maxWidthProperty().unbind();
        window.minHeightProperty().unbind();
        window.maxHeightProperty().unbind();

        OpenSubtitlesState oldState = openSubtitlesState;
        openSubtitlesState = OpenSubtitlesState.HELP_OPEN;

        ParallelTransition parallelTransition = new ParallelTransition();

        FadeTransition contentFade = new FadeTransition(Duration.millis(200));
        switch (oldState){
            case SEARCH_OPEN -> contentFade.setNode(searchPage);
            case CONNECTION_OPEN -> contentFade.setNode(connectionPage);
            case RESULTS_OPEN -> contentFade.setNode(resultsPage);
        }

        contentFade.setFromValue(1);
        contentFade.setToValue(0);

        double target = Math.min(Math.max(350, mainController.videoImageViewWrapper.getHeight() * 0.8), 1000);

        Timeline minHeightTransition = AnimationsClass.animateMinHeight(target, window);
        Timeline maxHeightTransition = AnimationsClass.animateMaxHeight(target, window);
        Timeline minWidthTransition = AnimationsClass.animateMinWidth(600, window);
        Timeline maxWidthTransition = AnimationsClass.animateMaxWidth(600, window);

        parallelTransition.getChildren().addAll(contentFade, minHeightTransition, maxHeightTransition, minWidthTransition, maxWidthTransition);

        parallelTransition.setOnFinished(e -> {
            switch (oldState){
                case SEARCH_OPEN -> {
                    searchPage.searchInProgress.set(false);

                    if(searchPage.executorService != null){
                        searchPage.executorService.shutdown();
                        searchPage.executorService = null;
                    }

                    searchPage.setOpacity(0);
                    searchPage.setVisible(false);
                    searchPage.scrollPane.setVvalue(0);
                    searchPage.errorLabel.setVisible(false);
                }
                case CONNECTION_OPEN -> connectionPage.reset();
                case RESULTS_OPEN -> resultsPage.reset();
            }

            window.minHeightProperty().bind(Bindings.min(1000, Bindings.max(350, mainController.videoImageViewWrapper.heightProperty().multiply(0.8))));
            window.maxHeightProperty().bind(Bindings.min(1000, Bindings.max(350, mainController.videoImageViewWrapper.heightProperty().multiply(0.8))));


            helpPage.setVisible(true);
            focusNodes.clear();
            focusNodes.add(helpPage);
            focusNodes.add(connectionButton);
            focusNodes.add(mainButton);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), helpPage);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.setOnFinished(event -> {
                animating = false;
                window.setMouseTransparent(false);
            });

            fadeIn.playFromStart();
        });

        parallelTransition.playFromStart();
    }

    void openResultsPage(){
        if(openSubtitlesState == OpenSubtitlesState.RESULTS_OPEN || animating) return;

        animating = true;
        window.setMouseTransparent(true);

        window.minWidthProperty().unbind();
        window.maxWidthProperty().unbind();
        window.minHeightProperty().unbind();
        window.maxHeightProperty().unbind();

        OpenSubtitlesState oldState = openSubtitlesState;
        openSubtitlesState = OpenSubtitlesState.RESULTS_OPEN;

        ParallelTransition parallelTransition = new ParallelTransition();

        FadeTransition contentFade = new FadeTransition(Duration.millis(200));
        switch (oldState){
            case SEARCH_OPEN -> contentFade.setNode(searchPage);
            case CONNECTION_OPEN -> contentFade.setNode(connectionPage);
            case HELP_OPEN -> contentFade.setNode(helpPage);
        }

        contentFade.setFromValue(1);
        contentFade.setToValue(0);

        double targetHeight = Math.min(Math.max(350, mainController.videoImageViewWrapper.getHeight() * 0.8), 1000);

        Timeline minHeightTransition = AnimationsClass.animateMinHeight(targetHeight, window);
        Timeline maxHeightTransition = AnimationsClass.animateMaxHeight(targetHeight, window);

        double targetWidth = Math.min(Math.max(600, mainController.videoImageViewWrapper.getWidth() * 0.7), 1200);

        Timeline minWidthTransition = AnimationsClass.animateMinWidth(targetWidth, window);
        Timeline maxWidthTransition = AnimationsClass.animateMaxWidth(targetWidth, window);

        parallelTransition.getChildren().addAll(contentFade, minHeightTransition, maxHeightTransition, minWidthTransition, maxWidthTransition);

        parallelTransition.setOnFinished(e -> {
            switch (oldState){
                case SEARCH_OPEN -> {
                    searchPage.searchInProgress.set(false);

                    if(searchPage.executorService != null){
                        searchPage.executorService.shutdown();
                        searchPage.executorService = null;
                    }

                    searchPage.setOpacity(0);
                    searchPage.setVisible(false);
                    searchPage.scrollPane.setVvalue(0);
                    searchPage.errorLabel.setVisible(false);
                }
                case CONNECTION_OPEN -> connectionPage.reset();
                case HELP_OPEN -> helpPage.reset();
            }

            window.minHeightProperty().bind(Bindings.min(1000, Bindings.max(350, mainController.videoImageViewWrapper.heightProperty().multiply(0.8))));
            window.maxHeightProperty().bind(Bindings.min(1000, Bindings.max(350, mainController.videoImageViewWrapper.heightProperty().multiply(0.8))));

            window.minWidthProperty().bind(Bindings.min(1200, Bindings.max(600, mainController.videoImageViewWrapper.widthProperty().multiply(0.7))));
            window.maxWidthProperty().bind(Bindings.min(1200, Bindings.max(600, mainController.videoImageViewWrapper.widthProperty().multiply(0.7))));


            resultsPage.setVisible(true);
            focusNodes.clear();
            focusNodes.add(resultsPage);
            focusNodes.add(helpButton);
            focusNodes.add(connectionButton);
            focusNodes.add(mainButton);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), resultsPage);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.setOnFinished(event -> {
                animating = false;
                window.setMouseTransparent(false);
            });

            fadeIn.playFromStart();
        });

        parallelTransition.playFromStart();
    }

}
