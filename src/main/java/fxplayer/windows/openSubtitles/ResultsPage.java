package fxplayer.windows.openSubtitles;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.css.PseudoClass;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;
import fxplayer.PressableNode;
import fxplayer.SVG;
import fxplayer.Utilities;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static fxplayer.Utilities.keyboardFocusOff;
import static fxplayer.Utilities.keyboardFocusOn;

public class ResultsPage extends VBox implements Page{

    OpenSubtitlesWindow openSubtitlesWindow;

    StackPane titleContainer = new StackPane();
    Label title = new Label("Results");
    Button backButton = new Button();

    public ScrollPane scrollPane;

    VBox content = new VBox();

    Label errorLabel = new Label("No results to display");

    GridPane tableHeader = new GridPane();

    ColumnConstraints column1 = new ColumnConstraints(60, 60, 60);
    ColumnConstraints column2 = new ColumnConstraints(0,100, Double.MAX_VALUE);
    ColumnConstraints column3 = new ColumnConstraints(110,110,110);
    ColumnConstraints column4 = new ColumnConstraints(110,110,110);
    ColumnConstraints column5 = new ColumnConstraints(70,70,70);

    HBox fileNameHeaderContainer = new HBox();
    PressableNode fileNameHeader = new PressableNode();
    Label fileNameHeaderLabel = new Label("File name");
    Region fileNameHeaderArrow = new Region();

    HBox languageHeaderContainer = new HBox();
    PressableNode languageHeader = new PressableNode();
    Label languageHeaderLabel = new Label("Language");
    Region languageHeaderArrow = new Region();

    HBox downloadsHeaderContainer = new HBox();
    PressableNode downloadsHeader = new PressableNode();
    Label downloadsHeaderLabel = new Label("Downloads");
    Region downloadsHeaderArrow = new Region();

    SVGPath triangleUpSVG = new SVGPath();
    SVGPath triangleDownSVG = new SVGPath();

    SortingMode sortingMode = SortingMode.DOWNLOADS;


    IntegerProperty focus = new SimpleIntegerProperty(-1);
    List<Node> focusNodes = new ArrayList<>();

    List<Result> results = new ArrayList<>();
    List<Integer> resultsOrder = new ArrayList<>();


    FadeTransition contentFade = null;

    boolean fileNameHeaderPressed = false;
    boolean languageHeaderPressed = false;
    boolean downloadsHeaderPressed = false;


    ResultsPage(OpenSubtitlesWindow openSubtitlesWindow){
        this.openSubtitlesWindow = openSubtitlesWindow;
        this.setOpacity(0);
        this.setVisible(false);

        titleContainer.setPadding(new Insets(15, 20, 25, 0));
        titleContainer.setOnMouseClicked(e -> openSubtitlesWindow.window.requestFocus());

        StackPane.setAlignment(title, Pos.CENTER_LEFT);
        StackPane.setMargin(title, new Insets(0, 0, 0, 50));
        title.getStyleClass().add("popupWindowTitle");

        titleContainer.getChildren().addAll(backButton, title);

        SVGPath backSVG = new SVGPath();
        backSVG.setContent(SVG.ARROW_LEFT.getContent());
        Region backIcon = new Region();
        backIcon.setShape(backSVG);
        backIcon.setPrefSize(20, 20);
        backIcon.setMaxSize(20, 20);
        backIcon.setMouseTransparent(true);
        backIcon.getStyleClass().add("graphic");

        backButton.setPrefWidth(25);
        backButton.setPrefHeight(25);

        backButton.getStyleClass().addAll("transparentButton", "popupWindowCloseButton");
        backButton.setFocusTraversable(false);
        backButton.setGraphic(backIcon);
        backButton.setOnAction(e -> openSubtitlesWindow.openSearchPage());
        backButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                openSubtitlesWindow.focus.set(0);
                focus.set(0);
            }
            else{
                keyboardFocusOff(backButton);
                openSubtitlesWindow.focus.set(-1);
                focus.set(-1);
            }
        });

        backButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            backButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        backButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            backButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });

        StackPane.setAlignment(backButton, Pos.CENTER_LEFT);
        StackPane.setMargin(backButton, new Insets(0, 0, 0, 10));

        scrollPane = new ScrollPane() {
            ScrollBar vertical;

            @Override
            protected void layoutChildren() {
                super.layoutChildren();
                if (vertical == null) {
                    vertical = (ScrollBar) lookup(".scroll-bar:vertical");
                    vertical.visibleProperty().addListener((obs, old, val) -> updatePadding(val));
                }
            }
        };

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("menuScroll");
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        scrollPane.setBackground(Background.EMPTY);
        scrollPane.setContent(content);

        content.setPadding(new Insets(3, 20, 15, 15));
        content.setSpacing(5);
        content.setAlignment(Pos.CENTER);
        content.setFillWidth(true);
        content.setBackground(Background.EMPTY);

        content.getChildren().add(errorLabel);

        errorLabel.getStyleClass().add("resultTitle");
        errorLabel.setAlignment(Pos.BOTTOM_CENTER);
        errorLabel.setMinHeight(80);

        column2.setHgrow(Priority.ALWAYS);

        tableHeader.getColumnConstraints().addAll(column1, column2, column3, column4, column5);

        GridPane.setHalignment(fileNameHeaderContainer, HPos.LEFT);
        GridPane.setHalignment(languageHeaderContainer, HPos.CENTER);
        GridPane.setHalignment(downloadsHeaderContainer, HPos.CENTER);

        GridPane.setValignment(fileNameHeaderContainer, VPos.CENTER);
        GridPane.setValignment(languageHeaderContainer, VPos.CENTER);
        GridPane.setValignment(downloadsHeaderContainer, VPos.CENTER);

        triangleDownSVG.setContent(SVG.TRIANGLE_DOWN.getContent());
        triangleUpSVG.setContent(SVG.TRIANGLE_UP.getContent());

        tableHeader.add(fileNameHeaderContainer, 1, 0);
        tableHeader.add(languageHeaderContainer, 2, 0);
        tableHeader.add(downloadsHeaderContainer, 3, 0);
        tableHeader.setPadding(new Insets(0, 0, 10, 0));

        fileNameHeaderContainer.getChildren().add(fileNameHeader);
        fileNameHeaderContainer.setAlignment(Pos.CENTER_LEFT);

        fileNameHeader.getChildren().addAll(fileNameHeaderLabel, fileNameHeaderArrow);
        fileNameHeader.getStyleClass().add("resultsTableHeader");
        fileNameHeader.setPrefWidth(85);
        fileNameHeader.setMaxWidth(85);
        fileNameHeader.setMouseTransparent(true);
        fileNameHeader.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                openSubtitlesWindow.focus.set(0);
                focus.set(1);
            }
            else{
                keyboardFocusOff(fileNameHeader);
                openSubtitlesWindow.focus.set(-1);
                focus.set(-1);
                fileNameHeaderPressed = false;
            }
        });

        fileNameHeader.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            fileNameHeader.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);

            fileNameHeaderPressed = true;

            e.consume();
        });

        fileNameHeader.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            fileNameHeader.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);

            if(fileNameHeaderPressed){
                if(sortingMode == SortingMode.FILE) sortResults(SortingMode.FILE_REVERSE);
                else if(sortingMode == SortingMode.FILE_REVERSE) sortResults(SortingMode.DEFAULT);
                else sortResults(SortingMode.FILE);
            }

            fileNameHeaderPressed = false;

            e.consume();
        });

        fileNameHeader.setOnMouseClicked(e -> {
            fileNameHeader.requestFocus();

            if(sortingMode == SortingMode.FILE) sortResults(SortingMode.FILE_REVERSE);
            else if(sortingMode == SortingMode.FILE_REVERSE) sortResults(SortingMode.DEFAULT);
            else sortResults(SortingMode.FILE);
        });

        StackPane.setAlignment(fileNameHeaderLabel, Pos.CENTER_LEFT);
        fileNameHeaderLabel.getStyleClass().add("resultsTableLabel");

        StackPane.setAlignment(fileNameHeaderArrow, Pos.CENTER_RIGHT);
        fileNameHeaderArrow.setShape(triangleDownSVG);
        fileNameHeaderArrow.getStyleClass().add("graphic");
        fileNameHeaderArrow.setPrefSize(10, 6);
        fileNameHeaderArrow.setMaxSize(10, 6);
        fileNameHeaderArrow.setVisible(false);

        languageHeaderContainer.getChildren().add(languageHeader);
        languageHeaderContainer.setAlignment(Pos.CENTER);

        languageHeader.getChildren().addAll(languageHeaderLabel, languageHeaderArrow);
        languageHeader.getStyleClass().add("resultsTableHeader");
        languageHeader.setPrefWidth(85);
        languageHeader.setMaxWidth(85);
        languageHeader.setTranslateX(5);
        languageHeader.setMouseTransparent(true);

        languageHeader.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                openSubtitlesWindow.focus.set(0);
                focus.set(2);
            }
            else{
                keyboardFocusOff(languageHeader);
                openSubtitlesWindow.focus.set(-1);
                focus.set(-1);
                languageHeaderPressed = false;
            }
        });

        languageHeader.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            languageHeader.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);

            languageHeaderPressed = true;

            e.consume();
        });

        languageHeader.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            languageHeader.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);

            if(languageHeaderPressed){
                if(sortingMode == SortingMode.LANGUAGE) sortResults(SortingMode.LANGUAGE_REVERSE);
                else if(sortingMode == SortingMode.LANGUAGE_REVERSE) sortResults(SortingMode.DEFAULT);
                else sortResults(SortingMode.LANGUAGE);
            }

            languageHeaderPressed = false;

            e.consume();
        });

        languageHeader.setOnMouseClicked(e -> {
            languageHeader.requestFocus();

            if(sortingMode == SortingMode.LANGUAGE) sortResults(SortingMode.LANGUAGE_REVERSE);
            else if(sortingMode == SortingMode.LANGUAGE_REVERSE) sortResults(SortingMode.DEFAULT);
            else sortResults(SortingMode.LANGUAGE);
        });

        StackPane.setAlignment(languageHeaderLabel, Pos.CENTER_LEFT);
        languageHeaderLabel.getStyleClass().add("resultsTableLabel");

        StackPane.setAlignment(languageHeaderArrow, Pos.CENTER_RIGHT);
        languageHeaderArrow.setShape(triangleDownSVG);
        languageHeaderArrow.getStyleClass().add("graphic");
        languageHeaderArrow.setPrefSize(10, 6);
        languageHeaderArrow.setMaxSize(10, 6);
        languageHeaderArrow.setVisible(false);

        downloadsHeaderContainer.getChildren().add(downloadsHeader);
        downloadsHeaderContainer.setAlignment(Pos.CENTER);

        downloadsHeader.getChildren().addAll(downloadsHeaderLabel, downloadsHeaderArrow);
        downloadsHeader.getStyleClass().add("resultsTableHeader");
        downloadsHeader.setPrefWidth(95);
        downloadsHeader.setMaxWidth(95);
        downloadsHeader.setTranslateX(10);
        downloadsHeader.setMouseTransparent(true);

        downloadsHeader.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                openSubtitlesWindow.focus.set(0);
                focus.set(3);
            }
            else{
                keyboardFocusOff(downloadsHeader);
                openSubtitlesWindow.focus.set(-1);
                focus.set(-1);
                downloadsHeaderPressed = false;
            }
        });

        downloadsHeader.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            downloadsHeader.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);

            downloadsHeaderPressed = true;

            e.consume();
        });

        downloadsHeader.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            downloadsHeader.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);

            if(downloadsHeaderPressed){
                if(sortingMode == SortingMode.DOWNLOADS) sortResults(SortingMode.DOWNLOADS_REVERSE);
                else if(sortingMode == SortingMode.DOWNLOADS_REVERSE) sortResults(SortingMode.DEFAULT);
                else sortResults(SortingMode.DOWNLOADS);
            }

            downloadsHeaderPressed = false;

            e.consume();
        });

        downloadsHeader.setOnMouseClicked(e -> {
            downloadsHeader.requestFocus();

            if(sortingMode == SortingMode.DOWNLOADS) sortResults(SortingMode.DOWNLOADS_REVERSE);
            else if(sortingMode == SortingMode.DOWNLOADS_REVERSE) sortResults(SortingMode.DEFAULT);
            else sortResults(SortingMode.DOWNLOADS);
        });

        StackPane.setAlignment(downloadsHeaderLabel, Pos.CENTER_LEFT);
        downloadsHeaderLabel.getStyleClass().add("resultsTableLabel");

        StackPane.setAlignment(downloadsHeaderArrow, Pos.CENTER_RIGHT);
        downloadsHeaderArrow.setShape(triangleDownSVG);
        downloadsHeaderArrow.getStyleClass().add("graphic");
        downloadsHeaderArrow.setPrefSize(10, 6);
        downloadsHeaderArrow.setMaxSize(10, 6);

        this.getChildren().addAll(titleContainer, tableHeader, scrollPane);
        StackPane.setMargin(this, new Insets(0, 0, 70, 0));

        focusNodes.add(backButton);
    }

    public void reset(){
        this.setOpacity(0);
        this.setVisible(false);
        if(contentFade != null && contentFade.getStatus() == Animation.Status.RUNNING) contentFade.stop();

        content.setMouseTransparent(false);
        content.setOpacity(1);
        content.getChildren().clear();
        content.getChildren().add(errorLabel);
        content.setAlignment(Pos.CENTER);

        downloadsHeader.setMouseTransparent(true);
        languageHeader.setMouseTransparent(true);
        fileNameHeader.setMouseTransparent(true);

        results.clear();
        resultsOrder.clear();

        focusNodes.clear();
        focusNodes.add(backButton);

        downloadsHeaderArrow.setVisible(true);
        downloadsHeaderArrow.setShape(triangleDownSVG);
        languageHeaderArrow.setVisible(false);
        fileNameHeaderArrow.setVisible(false);

        sortingMode = SortingMode.DOWNLOADS;
    }

    private void updatePadding(boolean value){
        if(value) {
            content.setPadding(new Insets(3, 8, 15, 15));
        }
        else {
            content.setPadding(new Insets(3, 20, 15, 15));
        }
    }

    public void setNotEmpty(){
        focusNodes.add(fileNameHeader);
        focusNodes.add(languageHeader);
        focusNodes.add(downloadsHeader);

        content.getChildren().remove(errorLabel);
        content.setAlignment(Pos.TOP_LEFT);

        fileNameHeader.setMouseTransparent(false);
        languageHeader.setMouseTransparent(false);
        downloadsHeader.setMouseTransparent(false);
    }

    public void addResult(Result result){
        resultsOrder.add(results.size());
        results.add(result);
        content.getChildren().add(result);

        focusNodes.add(result);
    }

    public void sortResults(SortingMode newMode){

        fileNameHeaderArrow.setVisible(false);
        languageHeaderArrow.setVisible(false);
        downloadsHeaderArrow.setVisible(false);

        sortingMode = newMode;

        content.setMouseTransparent(true);

        if(contentFade != null && contentFade.getStatus() == Animation.Status.RUNNING) contentFade.stop();

        focusNodes.clear();
        focusNodes.add(backButton);
        focusNodes.add(fileNameHeader);
        focusNodes.add(languageHeader);
        focusNodes.add(downloadsHeader);

        contentFade = new FadeTransition(Duration.millis(200), content);
        contentFade.setFromValue(content.getOpacity());
        contentFade.setToValue(0);
        contentFade.setOnFinished(e -> {
            switch (newMode){
                case DEFAULT -> sortDefault();
                case FILE -> sortFile();
                case FILE_REVERSE -> sortFileReverse();
                case LANGUAGE -> sortLanguage();
                case LANGUAGE_REVERSE -> sortLanguageReverse();
                case DOWNLOADS -> sortDownloads();
                case DOWNLOADS_REVERSE -> sortDownloadsReverse();
            }

            contentFade = new FadeTransition(Duration.millis(200), content);
            contentFade.setFromValue(content.getOpacity());
            contentFade.setToValue(1);
            contentFade.setOnFinished(ev -> content.setMouseTransparent(false));

            contentFade.playFromStart();
        });

        contentFade.playFromStart();
    }

    private void sortDefault(){

        resultsOrder.clear();
        content.getChildren().clear();

        for(int i = 0 ; i < results.size() ; i++){
            Result result = results.get(i);
            result.index = i;
            result.indexLabel.setText(String.valueOf(i + 1));
            content.getChildren().add(result);
            resultsOrder.add(i);

            if(!result.isMouseTransparent()) focusNodes.add(result);
        }
    }

    private void sortFile(){
        fileNameHeaderArrow.setShape(triangleDownSVG);
        fileNameHeaderArrow.setVisible(true);

        resultsOrder.clear();
        content.getChildren().clear();

        List<Result> copy = new ArrayList<>(results);
        copy.sort(Comparator.comparing(result -> result.fileName.toLowerCase()));

        for(int i = 0; i < copy.size(); i++){
            Result result = copy.get(i);
            result.index = i;
            result.indexLabel.setText(String.valueOf(i + 1));
            content.getChildren().add(result);
            resultsOrder.add(results.indexOf(result));

            if(!result.isMouseTransparent()) focusNodes.add(result);
        }
    }

    private void sortFileReverse(){
        fileNameHeaderArrow.setShape(triangleUpSVG);
        fileNameHeaderArrow.setVisible(true);

        resultsOrder.clear();
        content.getChildren().clear();

        List<Result> copy = new ArrayList<>(results);
        copy.sort((result1, result2) -> result2.fileName.toLowerCase().compareTo(result1.fileName.toLowerCase()));

        for(int i = 0; i < copy.size(); i++){
            Result result = copy.get(i);
            result.index = i;
            result.indexLabel.setText(String.valueOf(i + 1));
            content.getChildren().add(result);
            resultsOrder.add(results.indexOf(result));

            if(!result.isMouseTransparent()) focusNodes.add(result);
        }
    }

    private void sortLanguage(){
        languageHeaderArrow.setShape(triangleDownSVG);
        languageHeaderArrow.setVisible(true);

        resultsOrder.clear();
        content.getChildren().clear();

        List<Result> copy = new ArrayList<>(results);
        copy.sort(Comparator.comparing(result -> result.language));

        for(int i = 0; i < copy.size(); i++){
            Result result = copy.get(i);
            result.index = i;
            result.indexLabel.setText(String.valueOf(i + 1));
            content.getChildren().add(result);
            resultsOrder.add(results.indexOf(result));

            if(!result.isMouseTransparent()) focusNodes.add(result);
        }
    }

    private void sortLanguageReverse(){
        languageHeaderArrow.setShape(triangleUpSVG);
        languageHeaderArrow.setVisible(true);

        resultsOrder.clear();
        content.getChildren().clear();

        List<Result> copy = new ArrayList<>(results);
        copy.sort((result1, result2) -> result2.language.compareTo(result1.language));

        for(int i = 0; i < copy.size(); i++){
            Result result = copy.get(i);
            result.index = i;
            result.indexLabel.setText(String.valueOf(i + 1));
            content.getChildren().add(result);
            resultsOrder.add(results.indexOf(result));

            if(!result.isMouseTransparent()) focusNodes.add(result);
        }
    }

    private void sortDownloads(){
        downloadsHeaderArrow.setShape(triangleDownSVG);
        downloadsHeaderArrow.setVisible(true);

        resultsOrder.clear();
        content.getChildren().clear();

        List<Result> copy = new ArrayList<>(results);
        copy.sort((result1, result2) -> Integer.compare(result2.downloads, result1.downloads));

        for(int i = 0; i < copy.size(); i++){
            Result result = copy.get(i);
            result.index = i;
            result.indexLabel.setText(String.valueOf(i + 1));
            content.getChildren().add(result);
            resultsOrder.add(results.indexOf(result));

            if(!result.isMouseTransparent()) focusNodes.add(result);
        }
    }

    private void sortDownloadsReverse(){
        downloadsHeaderArrow.setShape(triangleUpSVG);
        downloadsHeaderArrow.setVisible(true);

        resultsOrder.clear();
        content.getChildren().clear();

        List<Result> copy = new ArrayList<>(results);
        copy.sort(Comparator.comparing(result -> result.downloads));

        for(int i = 0; i < copy.size(); i++){
            Result result = copy.get(i);
            result.index = i;
            result.indexLabel.setText(String.valueOf(i + 1));
            content.getChildren().add(result);
            resultsOrder.add(results.indexOf(result));

            if(!result.isMouseTransparent()) focusNodes.add(result);
        }
    }

    @Override
    public boolean focusForward(){
        if(focus.get() == focusNodes.size() - 1) return true;
        Node target = focusNodes.get(focus.get() + 1);

        keyboardFocusOn(target);
        if(target instanceof Result) Utilities.checkScrollDown(scrollPane, target);

        return false;
    }

    @Override
    public boolean focusBackward(){
        if(focus.get() == 0) return true;

        Node target;

        if(focus.get() == -1) target = focusNodes.get(focusNodes.size() - 1);
        else target = focusNodes.get(focus.get() - 1);

        keyboardFocusOn(target);
        if(target instanceof Result) Utilities.checkScrollUp(scrollPane, target);

        return false;
    }
}

