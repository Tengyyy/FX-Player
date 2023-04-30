package hans.Subtitles;

import hans.*;
import hans.PlaybackSettings.PlaybackSettingsController;
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
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;

public class SubtitlesHome {

    SubtitlesController subtitlesController;



    ScrollPane scrollPane = new ScrollPane();
    VBox subtitlesWrapper = new VBox();

    HBox subtitlesTitle = new HBox();
    HBox subtitlesChooserTab = new HBox();

    Label subtitlesTitleLabel = new Label();
    Label subtitlesOptionsLabel = new Label();

    Label chooseSubtitlesLabel = new Label();

    HBox openSubtitlesTab = new HBox();
    Label openSubtitlesLabel = new Label();
    SVGPath globeSVG = new SVGPath();
    Region globeIcon = new Region();
    StackPane globeIconPane = new StackPane();

    HBox adjustDelayTab = new HBox();
    Label adjustDelayLabel = new Label();
    SVGPath timerSVG = new SVGPath();
    Region timerIcon = new Region();
    StackPane timerIconPane = new StackPane();

    StackPane chooseSubtitlesIconPane = new StackPane();
    Region chooseSubtitlesIcon = new Region();
    SVGPath folderSVG = new SVGPath();

    FileChooser fileChooser;

    ArrayList<SubtitlesTab> subtitlesTabs = new ArrayList<>();

    public SubtitlesHome(SubtitlesController subtitlesController){
        this.subtitlesController = subtitlesController;

        folderSVG.setContent(SVG.FOLDER.getContent());
        globeSVG.setContent(SVG.GLOBE.getContent());
        timerSVG.setContent(SVG.TIMER.getContent());

        fileChooser = new FileChooser();
        fileChooser.setTitle("Choose subtitle file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Subtitles", "*.srt"));


        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("settingsScroll");
        scrollPane.setPrefSize(245, 181);
        scrollPane.setMaxSize(245, 181);
        scrollPane.setContent(subtitlesWrapper);
        scrollPane.setVisible(false);
        scrollPane.setMouseTransparent(true);
        scrollPane.setFitToWidth(true);

        StackPane.setAlignment(scrollPane, Pos.BOTTOM_RIGHT);

        subtitlesWrapper.setPrefSize(245, 178);
        subtitlesWrapper.setMaxSize(245, 178);
        subtitlesWrapper.setPadding(new Insets(0, 0, 8, 0));
        subtitlesWrapper.setAlignment(Pos.BOTTOM_LEFT);

        subtitlesWrapper.getChildren().addAll(subtitlesTitle, subtitlesChooserTab, openSubtitlesTab, adjustDelayTab);

        subtitlesTitle.getChildren().addAll(subtitlesTitleLabel, subtitlesOptionsLabel);
        subtitlesTitle.setPrefSize(245, 48);
        subtitlesTitle.setMaxSize(245, 48);
        subtitlesTitle.setPadding(new Insets(0, 10, 0, 10));
        subtitlesTitle.setAlignment(Pos.CENTER_LEFT);
        VBox.setMargin(subtitlesTitle, new Insets(0, 0, 10, 0));
        subtitlesTitle.getStyleClass().add("settingsPaneTitle");

        subtitlesTitleLabel.setPrefSize(165, 40);
        subtitlesTitleLabel.setMaxSize(165, 40);
        subtitlesTitleLabel.setText("Subtitles");
        subtitlesTitleLabel.getStyleClass().add("settingsPaneText");


        subtitlesOptionsLabel.getStyleClass().addAll("settingsPaneText", "settingsPaneSubText");
        subtitlesOptionsLabel.setText("Options");
        subtitlesOptionsLabel.setUnderline(true);
        subtitlesOptionsLabel.setMinSize(60, 40);
        subtitlesOptionsLabel.setPrefSize(60, 40);
        subtitlesOptionsLabel.setMaxSize(60, 40);
        subtitlesOptionsLabel.setCursor(Cursor.HAND);
        subtitlesOptionsLabel.setOnMouseClicked((e) -> openCaptionsOptions());
        subtitlesOptionsLabel.setAlignment(Pos.CENTER_RIGHT);


        subtitlesChooserTab.setPrefSize(245, 35);
        subtitlesChooserTab.setMaxSize(245, 35);
        subtitlesChooserTab.setPadding(new Insets(0, 10, 0, 10));
        subtitlesChooserTab.getStyleClass().add("settingsPaneTab");
        subtitlesChooserTab.setId("captionsChooserTab");
        subtitlesChooserTab.getChildren().addAll(chooseSubtitlesIconPane, chooseSubtitlesLabel);
        subtitlesChooserTab.setOnMouseClicked(e -> openCaptionsChooser());

        chooseSubtitlesIconPane.setMinSize(30, 35);
        chooseSubtitlesIconPane.setPrefSize(30, 35);
        chooseSubtitlesIconPane.setMaxSize(30, 35);
        chooseSubtitlesIconPane.setPadding(new Insets(0, 5, 0, 0));
        chooseSubtitlesIconPane.getChildren().add(chooseSubtitlesIcon);

        chooseSubtitlesIcon.setPrefSize(15, 13);
        chooseSubtitlesIcon.setMaxSize(15, 13);
        chooseSubtitlesIcon.getStyleClass().add("captionsPaneIcon");
        chooseSubtitlesIcon.setShape(folderSVG);

        chooseSubtitlesLabel.setText("Choose subtitle file");
        chooseSubtitlesLabel.getStyleClass().add("settingsPaneText");
        chooseSubtitlesLabel.setPrefSize(195, 35);
        chooseSubtitlesLabel.setMaxSize(195, 35);


        openSubtitlesTab.setPrefSize(245, 35);
        openSubtitlesTab.setMaxSize(245, 35);
        openSubtitlesTab.setPadding(new Insets(0, 10, 0, 10));
        openSubtitlesTab.getStyleClass().add("settingsPaneTab");
        openSubtitlesTab.setId("openSubtitlesTab");
        openSubtitlesTab.getChildren().addAll(globeIconPane, openSubtitlesLabel);
        openSubtitlesTab.setOnMouseClicked(e -> openOpenSubtitlesPane());

        globeIconPane.setMinSize(30, 35);
        globeIconPane.setPrefSize(30, 35);
        globeIconPane.setMaxSize(30, 35);
        globeIconPane.setPadding(new Insets(0, 5, 0, 0));
        globeIconPane.getChildren().add(globeIcon);

        globeIcon.setPrefSize(15, 15);
        globeIcon.setMaxSize(15, 15);
        globeIcon.getStyleClass().add("captionsPaneIcon");
        globeIcon.setShape(globeSVG);

        openSubtitlesLabel.setText("Search OpenSubtitles");
        openSubtitlesLabel.getStyleClass().add("settingsPaneText");
        openSubtitlesLabel.setPrefSize(195, 35);
        openSubtitlesLabel.setMaxSize(195, 35);

        adjustDelayTab.setPrefSize(245, 35);
        adjustDelayTab.setMaxSize(245, 35);
        adjustDelayTab.setPadding(new Insets(0, 10, 0, 10));
        adjustDelayTab.getStyleClass().add("settingsPaneTab");
        adjustDelayTab.getChildren().addAll(timerIconPane, adjustDelayLabel);
        adjustDelayTab.setOnMouseClicked(e -> openTimingPane());
        VBox.setMargin(adjustDelayTab, new Insets(5, 0, 0, 0));

        timerIconPane.setMinSize(30, 35);
        timerIconPane.setPrefSize(30, 35);
        timerIconPane.setMaxSize(30, 35);
        timerIconPane.setPadding(new Insets(0, 5, 0, 0));
        timerIconPane.getChildren().add(timerIcon);

        timerIcon.setPrefSize(14, 15);
        timerIcon.setMaxSize(14, 15);
        timerIcon.getStyleClass().add("captionsPaneIcon");
        timerIcon.setShape(timerSVG);

        adjustDelayLabel.setText("Adjust subtitle timing");
        adjustDelayLabel.getStyleClass().add("settingsPaneText");
        adjustDelayLabel.setPrefSize(195, 35);
        adjustDelayLabel.setMaxSize(195, 35);


        subtitlesController.subtitlesPane.getChildren().add(scrollPane);
    }

    public void openCaptionsOptions(){
        if(subtitlesController.animating.get()) return;

        subtitlesController.subtitlesState = SubtitlesState.CAPTIONS_OPTIONS_OPEN;

        subtitlesController.subtitlesOptionsPane.scrollPane.setVisible(true);
        subtitlesController.subtitlesOptionsPane.scrollPane.setMouseTransparent(false);

        Timeline clipHeightTimeline = new Timeline();
        clipHeightTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(subtitlesController.clip.heightProperty(), subtitlesController.subtitlesOptionsPane.scrollPane.getHeight())));


        Timeline clipWidthTimeline = new Timeline();
        clipWidthTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(subtitlesController.clip.widthProperty(), subtitlesController.subtitlesOptionsPane.scrollPane.getWidth())));



        TranslateTransition captionsTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), scrollPane);
        captionsTransition.setFromX(0);
        captionsTransition.setToX(-subtitlesController.subtitlesOptionsPane.scrollPane.getWidth());

        TranslateTransition captionsOptionsTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), subtitlesController.subtitlesOptionsPane.scrollPane);
        captionsOptionsTransition.setFromX(subtitlesController.subtitlesOptionsPane.scrollPane.getWidth());
        captionsOptionsTransition.setToX(0);


        ParallelTransition parallelTransition = new ParallelTransition(clipHeightTimeline, clipWidthTimeline, captionsTransition, captionsOptionsTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            subtitlesController.animating.set(false);
            scrollPane.setVisible(false);
            scrollPane.setMouseTransparent(true);
            scrollPane.setTranslateX(0);
            scrollPane.setVvalue(0);
            subtitlesController.clip.setHeight(subtitlesController.subtitlesOptionsPane.scrollPane.getPrefHeight());
        });

        parallelTransition.play();
        subtitlesController.animating.set(true);
    }


    public void openCaptionsChooser(){
        if(subtitlesController.menuController.queuePage.queueBox.activeItem.get() != null){
            fileChooser.setInitialDirectory(subtitlesController.menuController.queuePage.queueBox.activeItem.get().file.getParentFile()); // search for subtitles inside the same directory as the current media item
        }
        else {
            fileChooser.setInitialDirectory(null);
        }
        File selectedFile = fileChooser.showOpenDialog(App.stage);

        if (selectedFile != null){
            createTab(selectedFile);
        }
    }

    public void openOpenSubtitlesPane(){
        if(subtitlesController.animating.get()) return;

        subtitlesController.subtitlesState = SubtitlesState.OPENSUBTITLES_OPEN;

        subtitlesController.openSubtitlesPane.languageBox.requestFocus();

        subtitlesController.openSubtitlesPane.scrollPane.setVisible(true);
        subtitlesController.openSubtitlesPane.scrollPane.setMouseTransparent(false);

        Timeline clipHeightTimeline = new Timeline();
        clipHeightTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(subtitlesController.clip.heightProperty(), subtitlesController.openSubtitlesPane.scrollPane.getHeight())));


        Timeline clipWidthTimeline = new Timeline();
        clipWidthTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(subtitlesController.clip.widthProperty(), subtitlesController.openSubtitlesPane.scrollPane.getWidth())));



        TranslateTransition captionsTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), scrollPane);
        captionsTransition.setFromX(0);
        captionsTransition.setToX(-subtitlesController.openSubtitlesPane.scrollPane.getWidth());

        TranslateTransition openSubtitlesTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), subtitlesController.openSubtitlesPane.scrollPane);
        openSubtitlesTransition.setFromX(subtitlesController.openSubtitlesPane.scrollPane.getWidth());
        openSubtitlesTransition.setToX(0);


        ParallelTransition parallelTransition = new ParallelTransition(clipHeightTimeline, clipWidthTimeline, captionsTransition, openSubtitlesTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            subtitlesController.animating.set(false);
            scrollPane.setVisible(false);
            scrollPane.setMouseTransparent(true);
            scrollPane.setTranslateX(0);
            scrollPane.setVvalue(0);
            subtitlesController.clip.setHeight(subtitlesController.openSubtitlesPane.scrollPane.getPrefHeight());
        });

        parallelTransition.play();
        subtitlesController.animating.set(true);
    }

    public void openTimingPane(){
        if(subtitlesController.animating.get()) return;

        subtitlesController.subtitlesState = SubtitlesState.TIMING_OPEN;

        subtitlesController.timingPane.container.setVisible(true);
        subtitlesController.timingPane.container.setMouseTransparent(false);

        Timeline clipHeightTimeline = new Timeline();
        clipHeightTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(subtitlesController.clip.heightProperty(), subtitlesController.timingPane.container.getHeight())));


        Timeline clipWidthTimeline = new Timeline();
        clipWidthTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(subtitlesController.clip.widthProperty(), subtitlesController.timingPane.container.getWidth())));



        TranslateTransition captionsTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), scrollPane);
        captionsTransition.setFromX(0);
        captionsTransition.setToX(-subtitlesController.timingPane.container.getWidth());

        TranslateTransition timingTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), subtitlesController.timingPane.container);
        timingTransition.setFromX(subtitlesController.timingPane.container.getWidth());
        timingTransition.setToX(0);


        ParallelTransition parallelTransition = new ParallelTransition(clipHeightTimeline, clipWidthTimeline, captionsTransition, timingTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            subtitlesController.animating.set(false);
            scrollPane.setVisible(false);
            scrollPane.setMouseTransparent(true);
            scrollPane.setTranslateX(0);
            scrollPane.setVvalue(0);
            subtitlesController.clip.setHeight(subtitlesController.timingPane.container.getPrefHeight());
        });

        parallelTransition.play();
        subtitlesController.animating.set(true);
    }

    public void createTab(File selectedFile){

        for(SubtitlesTab subtitlesTab : subtitlesTabs){
            if(subtitlesTab.subtitleFile.getAbsolutePath().equals(selectedFile.getAbsolutePath())){
                subtitlesTab.selectSubtitles(false);
                return;
            }
        }

        SubtitlesTab subtitlesTab = new SubtitlesTab(subtitlesController, this, selectedFile.getName(), selectedFile, true);
        subtitlesWrapper.getChildren().add(1, subtitlesTab);

        if(subtitlesTabs.isEmpty()) subtitlesChooserTab.setStyle("-fx-border-width: 1 0 0 0;");
        subtitlesTabs.add(subtitlesTab);
        scrollPane.setVvalue(0);
        subtitlesTab.selectSubtitles(true);
    }
}
