package tengy.Subtitles;

import tengy.SVG;
import tengy.PlaybackSettings.PlaybackSettingsController;
import tengy.Utilities;
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
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.io.File;
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

    Label errorLabel = new Label();

    HBox tableHeader = new HBox();
    Label fileNameHeader = new Label();
    Label languageHeader = new Label();
    Label downloadsHeader = new Label();


    VBox resultBox = new VBox();
    ArrayList<Result> results = new ArrayList<>();

    SubtitlesHome subtitlesHome;
    SubtitlesController subtitlesController;

    final int DEFAULT_HEIGHT = 170;

    OpenSubtitlesResultsPane(SubtitlesHome subtitlesHome, SubtitlesController subtitlesController){
        this.subtitlesHome = subtitlesHome;
        this.subtitlesController = subtitlesController;

        backSVG.setContent(SVG.CHEVRON_LEFT.getContent());

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("settingsScroll");
        scrollPane.setPrefSize(550, DEFAULT_HEIGHT + 3);
        scrollPane.setMaxSize(550, DEFAULT_HEIGHT + 3);
        scrollPane.setContent(container);
        scrollPane.setVisible(false);
        scrollPane.setMouseTransparent(true);
        scrollPane.setFitToWidth(true);

        StackPane.setAlignment(scrollPane, Pos.BOTTOM_RIGHT);

        container.setPrefSize(550, DEFAULT_HEIGHT);
        container.setMaxSize(550, DEFAULT_HEIGHT);
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

        resultBox.setPrefWidth(550);
        resultBox.setMinWidth(535);
        resultBox.setMaxWidth(550);
        resultBox.setMinHeight(100);
        resultBox.setAlignment(Pos.CENTER);

        errorLabel.getStyleClass().add("settingsPaneText");
        errorLabel.setWrapText(true);
        errorLabel.setTextAlignment(TextAlignment.CENTER);
        VBox.setMargin(errorLabel, new Insets(0, 20, 0, 20));

        tableHeader.setPadding(new Insets(0, 0, 0, 35));
        tableHeader.setAlignment(Pos.CENTER_LEFT);
        tableHeader.setPrefSize(550, 40);
        tableHeader.setMaxSize(550, 40);
        tableHeader.getChildren().addAll(fileNameHeader, languageHeader, downloadsHeader);

        fileNameHeader.setText("File name");
        fileNameHeader.getStyleClass().add("settingsPaneText");
        fileNameHeader.setMinSize(275, 40);
        fileNameHeader.setPrefSize(275, 40);
        fileNameHeader.setMaxSize(275, 40);

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


        subtitlesController.subtitlesPane.getChildren().add(scrollPane);

    }

    public void closeOpenSubtitlesResultsPane() {
        if (subtitlesController.animating.get()) return;

        subtitlesController.openSubtitlesPane.languageBox.requestFocus();

        subtitlesController.subtitlesState = SubtitlesState.OPENSUBTITLES_OPEN;

        subtitlesController.openSubtitlesPane.scrollPane.setVisible(true);
        subtitlesController.openSubtitlesPane.scrollPane.setMouseTransparent(false);


        Timeline clipHeightTimeline = new Timeline();
        clipHeightTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(subtitlesController.clip.heightProperty(), subtitlesController.openSubtitlesPane.scrollPane.getHeight())));


        Timeline clipWidthTimeline = new Timeline();
        clipWidthTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(subtitlesController.clip.widthProperty(), subtitlesController.openSubtitlesPane.scrollPane.getWidth())));


        TranslateTransition openSubtitlesTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), subtitlesController.openSubtitlesPane.scrollPane);
        openSubtitlesTransition.setFromX(-scrollPane.getWidth());
        openSubtitlesTransition.setToX(0);

        TranslateTransition openSubtitlesResultsTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), scrollPane);
        openSubtitlesResultsTransition.setFromX(0);
        openSubtitlesResultsTransition.setToX(scrollPane.getWidth());


        ParallelTransition parallelTransition = new ParallelTransition(clipHeightTimeline, clipWidthTimeline, openSubtitlesTransition, openSubtitlesResultsTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            subtitlesController.animating.set(false);
            scrollPane.setVisible(false);
            scrollPane.setMouseTransparent(true);
            scrollPane.setTranslateX(0);
            subtitlesController.clip.setHeight(subtitlesController.openSubtitlesPane.scrollPane.getPrefHeight());
        });

        parallelTransition.play();
        subtitlesController.animating.set(true);
    }

    public void clearResults(){
        results.clear();
        resultBox.getChildren().clear();
        resultBox.setMinHeight(100);
        scrollPane.setPrefHeight(DEFAULT_HEIGHT + 3);
        scrollPane.setMaxHeight(DEFAULT_HEIGHT + 3);

        container.setPrefHeight(DEFAULT_HEIGHT);
        container.setMaxHeight(DEFAULT_HEIGHT);
    }

    public void addResult(Result result){

        resultBox.setMinHeight(Region.USE_COMPUTED_SIZE);

        if(results.isEmpty() && !resultBox.getChildren().contains(tableHeader)){
            resultBox.getChildren().add(tableHeader);
        }
        results.add(result);
        resultBox.getChildren().add(result);

        scrollPane.setPrefHeight(Math.max(DEFAULT_HEIGHT + 3, 98 + results.size() * 50));
        scrollPane.setMaxHeight(Math.max(DEFAULT_HEIGHT + 3, 98 + results.size() * 50));

        container.setPrefHeight(Math.max(DEFAULT_HEIGHT, 95 + results.size() * 50));
        container.setMaxHeight(Math.max(DEFAULT_HEIGHT, 95 + results.size() * 50));


        if(subtitlesController.subtitlesState == SubtitlesState.OPENSUBTITLES_RESULTS_OPEN){
            subtitlesController.clip.setHeight(Math.max(DEFAULT_HEIGHT + 3, 98 + results.size() * 50));
        }
    }

    public File findFileName(String name){
        File parent;
        if(subtitlesController.menuController.queuePage.queueBox.activeItem.get() != null){
            // save subtitle file to parent folder of active media item
            parent = new File(subtitlesController.menuController.queuePage.queueBox.activeItem.get().file.getParent());
        }
        else {
            // save to Downloads folder
            parent = new File(System.getProperty("user.home"), "Downloads");
        }

        File file = new File(parent, name);
        int index = 1;
        while(file.exists()){
            String extension = Utilities.getFileExtension(file);
            String newName;
            if(index == 1)
                newName = file.getName().substring(0, file.getName().lastIndexOf("." + extension)) + " (1)." + extension;
            else
                newName = file.getName().substring(0, file.getName().lastIndexOf(" (")) + " (" + index + ")." + extension;

            file = new File(file.getParentFile(), newName);
            index++;
        }

        return file;
    }
}
