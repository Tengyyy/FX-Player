package hans.Captions;

import hans.App;
import hans.SVG;
import hans.Settings.SettingsController;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

import java.util.ArrayList;

public class OpenSubtitlesResultsPane {

    ScrollPane scrollPane = new ScrollPane();
    VBox container = new VBox();

    StackPane titleContainer = new StackPane();
    HBox titlePane = new HBox();
    StackPane backIconPane = new StackPane();
    Region backIcon = new Region();
    Label titleLabel = new Label();
    SVGPath backSVG = new SVGPath();

    HBox tableHeader = new HBox();
    Label fileNameHeader = new Label();
    Label languageHeader = new Label();
    Label downloadsHeader = new Label();


    VBox resultBox = new VBox();
    ArrayList<Result> results = new ArrayList<>();

    CaptionsHome captionsHome;
    CaptionsController captionsController;

    final int DEFAULT_HEIGHT = 170;

    OpenSubtitlesResultsPane(CaptionsHome captionsHome, CaptionsController captionsController){
        this.captionsHome = captionsHome;
        this.captionsController = captionsController;

        backSVG.setContent(App.svgMap.get(SVG.CHEVRON_LEFT));

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("settingsScroll");
        scrollPane.setPrefSize(550, DEFAULT_HEIGHT + 3);
        scrollPane.setMaxSize(550, DEFAULT_HEIGHT + 3);
        // 95 real min width
        scrollPane.setContent(container);
        scrollPane.setVisible(false);
        scrollPane.setMouseTransparent(true);
        scrollPane.setFitToWidth(true);

        StackPane.setAlignment(scrollPane, Pos.BOTTOM_RIGHT);

        container.setPrefSize(535, DEFAULT_HEIGHT);
        container.setMaxSize(535, DEFAULT_HEIGHT);
        container.getChildren().addAll(titleContainer, resultBox);
        container.setAlignment(Pos.TOP_CENTER);

        titleContainer.setPadding(new Insets(0, 10, 0, 10));
        VBox.setMargin(titleContainer, new Insets(0, 0, 5, 0));
        titleContainer.getChildren().addAll(titlePane);
        titleContainer.getStyleClass().add("settingsPaneTitle");

        titlePane.setMinHeight(50);
        titlePane.setPrefHeight(50);
        titlePane.setMaxHeight(50);
        titlePane.setPrefWidth(Region.USE_COMPUTED_SIZE);
        StackPane.setAlignment(titlePane, Pos.CENTER_LEFT);
        titlePane.getChildren().addAll(backIconPane, titleLabel);

        backIconPane.setMinSize(24, 50);
        backIconPane.setPrefSize(24, 50);
        backIconPane.setMaxSize(24, 50);
        backIconPane.setCursor(Cursor.HAND);
        backIconPane.getChildren().add(backIcon);
        backIconPane.setOnMouseClicked((e) -> closeOpenSubtitlesResultsPane());

        backIcon.setMinSize(8, 13);
        backIcon.setPrefSize(8, 13);
        backIcon.setMaxSize(8, 13);
        backIcon.getStyleClass().add("settingsPaneIcon");
        backIcon.setShape(backSVG);

        titleLabel.setMinHeight(50);
        titleLabel.setPrefHeight(50);
        titleLabel.setMaxHeight(50);
        titleLabel.setText("Search Results");
        titleLabel.setCursor(Cursor.HAND);
        titleLabel.getStyleClass().add("settingsPaneText");
        titleLabel.setOnMouseClicked((e) -> closeOpenSubtitlesResultsPane());

        tableHeader.setPadding(new Insets(0, 50, 0, 35));
        tableHeader.setAlignment(Pos.CENTER_LEFT);
        tableHeader.setPrefSize(535, 40);
        tableHeader.setMaxSize(535, 40);
        tableHeader.getChildren().addAll(fileNameHeader, languageHeader, downloadsHeader);

        fileNameHeader.setText("File name");
        fileNameHeader.getStyleClass().add("settingsPaneText");
        fileNameHeader.setMinSize(270, 40);
        fileNameHeader.setPrefSize(270, 40);
        fileNameHeader.setMaxSize(270, 40);

        languageHeader.setText("Language");
        languageHeader.getStyleClass().add("settingsPaneText");
        languageHeader.setMinSize(75, 40);
        languageHeader.setPrefSize(75, 40);
        languageHeader.setMaxSize(75, 40);
        HBox.setMargin(languageHeader, new Insets(0, 0, 0, 10));

        downloadsHeader.setText("Downloads");
        downloadsHeader.getStyleClass().add("settingsPaneText");
        downloadsHeader.setMinSize(75, 40);
        downloadsHeader.setPrefSize(75, 40);
        downloadsHeader.setMaxSize(75, 40);
        HBox.setMargin(downloadsHeader, new Insets(0, 10, 0, 10));


        captionsController.captionsPane.getChildren().add(scrollPane);

    }

    public void closeOpenSubtitlesResultsPane() {
        if (captionsController.animating.get()) return;

        captionsController.captionsState = CaptionsState.OPENSUBTITLES_OPEN;

        captionsController.openSubtitlesPane.scrollPane.setVisible(true);
        captionsController.openSubtitlesPane.scrollPane.setMouseTransparent(false);


        Timeline clipHeightTimeline = new Timeline();
        clipHeightTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.clip.heightProperty(), captionsController.openSubtitlesPane.scrollPane.getHeight())));


        Timeline clipWidthTimeline = new Timeline();
        clipWidthTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.clip.widthProperty(), captionsController.openSubtitlesPane.scrollPane.getWidth())));


        TranslateTransition openSubtitlesTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), captionsController.openSubtitlesPane.scrollPane);
        openSubtitlesTransition.setFromX(-scrollPane.getWidth());
        openSubtitlesTransition.setToX(0);

        TranslateTransition openSubtitlesResultsTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), scrollPane);
        openSubtitlesResultsTransition.setFromX(0);
        openSubtitlesResultsTransition.setToX(scrollPane.getWidth());


        ParallelTransition parallelTransition = new ParallelTransition(clipHeightTimeline, clipWidthTimeline, openSubtitlesTransition, openSubtitlesResultsTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            captionsController.animating.set(false);
            scrollPane.setVisible(false);
            scrollPane.setMouseTransparent(true);
            scrollPane.setTranslateX(0);
            captionsController.clip.setHeight(captionsController.openSubtitlesPane.scrollPane.getPrefHeight());
        });

        parallelTransition.play();
        captionsController.animating.set(true);
    }

    public void clearResults(){
        results.clear();
        resultBox.getChildren().clear();
        scrollPane.setPrefHeight(DEFAULT_HEIGHT + 3);
        scrollPane.setMaxHeight(DEFAULT_HEIGHT + 3);

        container.setPrefHeight(DEFAULT_HEIGHT);
        container.setMaxHeight(DEFAULT_HEIGHT);
    }

    public void addResult(Result result){
        if(results.isEmpty() && !resultBox.getChildren().contains(tableHeader)) resultBox.getChildren().add(tableHeader);
        results.add(result);
        resultBox.getChildren().add(result);

        scrollPane.setPrefHeight(Math.max(DEFAULT_HEIGHT + 3, 98 + results.size() * 40));
        scrollPane.setMaxHeight(Math.max(DEFAULT_HEIGHT + 3, 98 + results.size() * 40));

        container.setPrefHeight(Math.max(DEFAULT_HEIGHT, 95 + results.size() * 40));
        container.setMaxHeight(Math.max(DEFAULT_HEIGHT, 95 + results.size() * 40));


        if(captionsController.captionsState == CaptionsState.OPENSUBTITLES_RESULTS_OPEN){
            captionsController.clip.setHeight(Math.max(DEFAULT_HEIGHT + 3, 98 + results.size() * 40));
        }
    }

}
