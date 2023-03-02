package hans.Captions;

import hans.*;
import hans.Settings.SettingsController;
import hans.Settings.SettingsState;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;

public class CaptionsHome {

    CaptionsController captionsController;



    ScrollPane scrollPane = new ScrollPane();
    VBox captionsWrapper = new VBox();

    HBox captionsTitle = new HBox();
    HBox captionsChooserTab = new HBox();

    Label captionsTitleLabel = new Label();
    Label captionsOptionsLabel = new Label();

    Label chooseCaptionsLabel = new Label();

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

    StackPane chooseCaptionsIconPane = new StackPane();
    Region chooseCaptionsIcon = new Region();
    SVGPath folderSVG = new SVGPath();

    FileChooser fileChooser;

    ArrayList<CaptionsTab> captionsTabs = new ArrayList<>();

    public CaptionsHome(CaptionsController captionsController){
        this.captionsController = captionsController;

        folderSVG.setContent(App.svgMap.get(SVG.FOLDER));
        globeSVG.setContent(App.svgMap.get(SVG.GLOBE));
        timerSVG.setContent(App.svgMap.get(SVG.TIMER));

        fileChooser = new FileChooser();
        fileChooser.setTitle("Choose subtitle file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Subtitles", "*.srt"));


        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("settingsScroll");
        scrollPane.setPrefSize(245, 181);
        scrollPane.setMaxSize(245, 181);
        scrollPane.setContent(captionsWrapper);
        scrollPane.setVisible(false);
        scrollPane.setMouseTransparent(true);
        scrollPane.setFitToWidth(true);

        StackPane.setAlignment(scrollPane, Pos.BOTTOM_RIGHT);

        captionsWrapper.setPrefSize(245, 178);
        captionsWrapper.setMaxSize(245, 178);
        captionsWrapper.setPadding(new Insets(8, 0, 8, 0));
        captionsWrapper.setAlignment(Pos.BOTTOM_LEFT);

        captionsWrapper.getChildren().addAll(captionsTitle, captionsChooserTab, openSubtitlesTab, adjustDelayTab);

        captionsTitle.getChildren().addAll(captionsTitleLabel, captionsOptionsLabel);
        captionsTitle.setPrefSize(245, 40);
        captionsTitle.setMaxSize(245, 40);
        captionsTitle.setPadding(new Insets(0, 10, 0, 10));
        VBox.setMargin(captionsTitle, new Insets(0, 0, 10, 0));
        captionsTitle.getStyleClass().add("settingsPaneTitle");

        captionsTitleLabel.setPrefSize(165, 40);
        captionsTitleLabel.setMaxSize(165, 40);
        captionsTitleLabel.setText("Subtitles/CC");
        captionsTitleLabel.getStyleClass().add("settingsPaneText");


        captionsOptionsLabel.getStyleClass().addAll("settingsPaneText", "settingsPaneSubText");
        captionsOptionsLabel.setText("Options");
        captionsOptionsLabel.setUnderline(true);
        captionsOptionsLabel.setMinSize(60, 40);
        captionsOptionsLabel.setPrefSize(60, 40);
        captionsOptionsLabel.setMaxSize(60, 40);
        captionsOptionsLabel.setCursor(Cursor.HAND);
        captionsOptionsLabel.setOnMouseClicked((e) -> openCaptionsOptions());
        captionsOptionsLabel.setAlignment(Pos.CENTER_RIGHT);


        captionsChooserTab.setPrefSize(245, 35);
        captionsChooserTab.setMaxSize(245, 35);
        captionsChooserTab.setPadding(new Insets(0, 10, 0, 10));
        captionsChooserTab.getStyleClass().add("settingsPaneTab");
        captionsChooserTab.setId("captionsChooserTab");
        captionsChooserTab.getChildren().addAll(chooseCaptionsIconPane, chooseCaptionsLabel);
        captionsChooserTab.setOnMouseClicked(e -> openCaptionsChooser());

        chooseCaptionsIconPane.setMinSize(30, 35);
        chooseCaptionsIconPane.setPrefSize(30, 35);
        chooseCaptionsIconPane.setMaxSize(30, 35);
        chooseCaptionsIconPane.setPadding(new Insets(0, 5, 0, 0));
        chooseCaptionsIconPane.getChildren().add(chooseCaptionsIcon);

        chooseCaptionsIcon.setPrefSize(15, 13);
        chooseCaptionsIcon.setMaxSize(15, 13);
        chooseCaptionsIcon.getStyleClass().add("captionsPaneIcon");
        chooseCaptionsIcon.setShape(folderSVG);

        chooseCaptionsLabel.setText("Choose subtitle file");
        chooseCaptionsLabel.getStyleClass().add("settingsPaneText");
        chooseCaptionsLabel.setPrefSize(195, 35);
        chooseCaptionsLabel.setMaxSize(195, 35);


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


        captionsController.captionsPane.getChildren().add(scrollPane);
    }

    public void openCaptionsOptions(){
        if(captionsController.animating.get()) return;

        captionsController.captionsState = CaptionsState.CAPTIONS_OPTIONS_OPEN;

        captionsController.captionsOptionsPane.scrollPane.setVisible(true);
        captionsController.captionsOptionsPane.scrollPane.setMouseTransparent(false);

        Timeline clipHeightTimeline = new Timeline();
        clipHeightTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.clip.heightProperty(), captionsController.captionsOptionsPane.scrollPane.getHeight())));


        Timeline clipWidthTimeline = new Timeline();
        clipWidthTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.clip.widthProperty(), captionsController.captionsOptionsPane.scrollPane.getWidth())));



        TranslateTransition captionsTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), scrollPane);
        captionsTransition.setFromX(0);
        captionsTransition.setToX(-captionsController.captionsOptionsPane.scrollPane.getWidth());

        TranslateTransition captionsOptionsTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), captionsController.captionsOptionsPane.scrollPane);
        captionsOptionsTransition.setFromX(captionsController.captionsOptionsPane.scrollPane.getWidth());
        captionsOptionsTransition.setToX(0);


        ParallelTransition parallelTransition = new ParallelTransition(clipHeightTimeline, clipWidthTimeline, captionsTransition, captionsOptionsTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            captionsController.animating.set(false);
            scrollPane.setVisible(false);
            scrollPane.setMouseTransparent(true);
            scrollPane.setTranslateX(0);
            scrollPane.setVvalue(0);
            captionsController.clip.setHeight(captionsController.captionsOptionsPane.scrollPane.getPrefHeight());
        });

        parallelTransition.play();
        captionsController.animating.set(true);
    }


    public void openCaptionsChooser(){
        if(captionsController.menuController.queueBox.activeItem.get() != null){
            fileChooser.setInitialDirectory(captionsController.menuController.queueBox.activeItem.get().file.getParentFile()); // search for subtitles inside the same directory as the current media item
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
        if(captionsController.animating.get()) return;

        captionsController.captionsState = CaptionsState.OPENSUBTITLES_OPEN;

        captionsController.openSubtitlesPane.scrollPane.setVisible(true);
        captionsController.openSubtitlesPane.scrollPane.setMouseTransparent(false);

        Timeline clipHeightTimeline = new Timeline();
        clipHeightTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.clip.heightProperty(), captionsController.openSubtitlesPane.scrollPane.getHeight())));


        Timeline clipWidthTimeline = new Timeline();
        clipWidthTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.clip.widthProperty(), captionsController.openSubtitlesPane.scrollPane.getWidth())));



        TranslateTransition captionsTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), scrollPane);
        captionsTransition.setFromX(0);
        captionsTransition.setToX(-captionsController.openSubtitlesPane.scrollPane.getWidth());

        TranslateTransition openSubtitlesTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), captionsController.openSubtitlesPane.scrollPane);
        openSubtitlesTransition.setFromX(captionsController.openSubtitlesPane.scrollPane.getWidth());
        openSubtitlesTransition.setToX(0);


        ParallelTransition parallelTransition = new ParallelTransition(clipHeightTimeline, clipWidthTimeline, captionsTransition, openSubtitlesTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            captionsController.animating.set(false);
            scrollPane.setVisible(false);
            scrollPane.setMouseTransparent(true);
            scrollPane.setTranslateX(0);
            scrollPane.setVvalue(0);
            captionsController.clip.setHeight(captionsController.openSubtitlesPane.scrollPane.getPrefHeight());
        });

        parallelTransition.play();
        captionsController.animating.set(true);
    }

    public void openTimingPane(){
        if(captionsController.animating.get()) return;

        captionsController.captionsState = CaptionsState.TIMING_OPEN;

        captionsController.timingPane.container.setVisible(true);
        captionsController.timingPane.container.setMouseTransparent(false);

        Timeline clipHeightTimeline = new Timeline();
        clipHeightTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.clip.heightProperty(), captionsController.timingPane.container.getHeight())));


        Timeline clipWidthTimeline = new Timeline();
        clipWidthTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.clip.widthProperty(), captionsController.timingPane.container.getWidth())));



        TranslateTransition captionsTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), scrollPane);
        captionsTransition.setFromX(0);
        captionsTransition.setToX(-captionsController.timingPane.container.getWidth());

        TranslateTransition timingTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), captionsController.timingPane.container);
        timingTransition.setFromX(captionsController.timingPane.container.getWidth());
        timingTransition.setToX(0);


        ParallelTransition parallelTransition = new ParallelTransition(clipHeightTimeline, clipWidthTimeline, captionsTransition, timingTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            captionsController.animating.set(false);
            scrollPane.setVisible(false);
            scrollPane.setMouseTransparent(true);
            scrollPane.setTranslateX(0);
            scrollPane.setVvalue(0);
            captionsController.clip.setHeight(captionsController.timingPane.container.getPrefHeight());
        });

        parallelTransition.play();
        captionsController.animating.set(true);
    }

    public void createTab(File selectedFile){

        for(CaptionsTab captionsTab : captionsTabs){
            if(captionsTab.captionFile.getAbsolutePath().equals(selectedFile.getAbsolutePath())){
                captionsTab.selectSubtitles(false);
                return;
            }
        }

        CaptionsTab captionsTab = new CaptionsTab(captionsController, this, selectedFile.getName(), selectedFile, true);
        captionsWrapper.getChildren().add(1, captionsTab);

        if(captionsTabs.isEmpty()) captionsChooserTab.setStyle("-fx-border-width: 1 0 0 0;");
        captionsTabs.add(captionsTab);
        scrollPane.setVvalue(0);
        captionsTab.selectSubtitles(true);
    }
}
