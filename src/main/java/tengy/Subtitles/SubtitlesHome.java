package tengy.Subtitles;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import tengy.*;
import tengy.PlaybackSettings.PlaybackSettingsController;
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
import tengy.PlaybackSettings.SettingsTab;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static tengy.Utilities.keyboardFocusOff;
import static tengy.Utilities.keyboardFocusOn;

public class SubtitlesHome {

    SubtitlesController subtitlesController;



    ScrollPane scrollPane = new ScrollPane();
    VBox subtitlesWrapper = new VBox();

    HBox subtitlesTitle = new HBox();


    Label subtitlesTitleLabel = new Label();
    Button subtitlesOptionsButton = new Button();

    SettingsTab openSubtitlesTab;
    Label openSubtitlesLabel = new Label();
    SVGPath globeSVG = new SVGPath();
    Region globeIcon = new Region();
    StackPane globeIconPane = new StackPane();

    SettingsTab adjustDelayTab;
    Label adjustDelayLabel = new Label();
    SVGPath timerSVG = new SVGPath();
    Region timerIcon = new Region();
    StackPane timerIconPane = new StackPane();

    SettingsTab subtitlesChooserTab;
    Label chooseSubtitlesLabel = new Label();
    StackPane chooseSubtitlesIconPane = new StackPane();
    Region chooseSubtitlesIcon = new Region();
    SVGPath folderSVG = new SVGPath();

    FileChooser fileChooser;

    ArrayList<SubtitlesTab> subtitlesTabs = new ArrayList<>();

    List<Node> focusNodes = new ArrayList<>();
    IntegerProperty focus = new SimpleIntegerProperty(-1);


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
        scrollPane.setPrefSize(295, 181);
        scrollPane.setMaxSize(295, 181);
        scrollPane.setContent(subtitlesWrapper);
        scrollPane.setVisible(false);
        scrollPane.setMouseTransparent(true);
        scrollPane.setFitToWidth(true);

        StackPane.setAlignment(scrollPane, Pos.BOTTOM_RIGHT);

        subtitlesWrapper.setPrefSize(295, 178);
        subtitlesWrapper.setMaxSize(295, 178);
        subtitlesWrapper.setPadding(new Insets(0, 0, 8, 0));
        subtitlesWrapper.setAlignment(Pos.BOTTOM_LEFT);


        subtitlesTitle.getChildren().addAll(subtitlesTitleLabel, subtitlesOptionsButton);
        subtitlesTitle.setPrefSize(295, 48);
        subtitlesTitle.setMaxSize(295, 48);
        subtitlesTitle.setPadding(new Insets(0, 10, 0, 10));
        subtitlesTitle.setAlignment(Pos.CENTER_LEFT);
        VBox.setMargin(subtitlesTitle, new Insets(0, 0, 5, 0));
        subtitlesTitle.getStyleClass().add("settingsPaneTitle");

        subtitlesTitleLabel.setPrefSize(195, 40);
        subtitlesTitleLabel.setMaxSize(195, 40);
        subtitlesTitleLabel.setText("Subtitles");
        subtitlesTitleLabel.getStyleClass().add("settingsPaneText");


        subtitlesOptionsButton.getStyleClass().addAll("transparentButton", "settingsMenuButton");
        subtitlesOptionsButton.setText("Options");
        subtitlesOptionsButton.setMinSize(80, 40);
        subtitlesOptionsButton.setPrefSize(80, 40);
        subtitlesOptionsButton.setMaxSize(80, 40);
        subtitlesOptionsButton.setOnAction((e) -> openCaptionsOptions());
        subtitlesOptionsButton.setAlignment(Pos.CENTER);
        subtitlesOptionsButton.setFocusTraversable(false);
        subtitlesOptionsButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue) focus.set(0);
            else {
                keyboardFocusOff(subtitlesOptionsButton);
                focus.set(-1);
            }
        });

        subtitlesOptionsButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            subtitlesOptionsButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        subtitlesOptionsButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            subtitlesOptionsButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });


        subtitlesChooserTab = new SettingsTab(focus, focusNodes, this::openCaptionsChooser);
        subtitlesChooserTab.setPrefSize(295, 35);
        subtitlesChooserTab.setMaxSize(295, 35);
        subtitlesChooserTab.setPadding(new Insets(0, 10, 0, 10));
        subtitlesChooserTab.setId("captionsChooserTab");
        subtitlesChooserTab.getChildren().addAll(chooseSubtitlesIconPane, chooseSubtitlesLabel);
        VBox.setMargin(subtitlesChooserTab, new Insets(5, 0, 0, 0));

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
        chooseSubtitlesLabel.setPrefSize(245, 35);
        chooseSubtitlesLabel.setMaxSize(245, 35);


        openSubtitlesTab = new SettingsTab(focus, focusNodes, this::openOpenSubtitlesPane);
        openSubtitlesTab.setPrefSize(295, 35);
        openSubtitlesTab.setMaxSize(295, 35);
        openSubtitlesTab.setPadding(new Insets(0, 10, 0, 10));
        openSubtitlesTab.setId("openSubtitlesTab");
        openSubtitlesTab.getChildren().addAll(globeIconPane, openSubtitlesLabel);

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
        openSubtitlesLabel.setPrefSize(245, 35);
        openSubtitlesLabel.setMaxSize(245, 35);

        adjustDelayTab = new SettingsTab(focus, focusNodes, this::openTimingPane);
        adjustDelayTab.setPrefSize(295, 35);
        adjustDelayTab.setMaxSize(295, 35);
        adjustDelayTab.setPadding(new Insets(0, 10, 0, 10));
        adjustDelayTab.getChildren().addAll(timerIconPane, adjustDelayLabel);
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
        adjustDelayLabel.setPrefSize(245, 35);
        adjustDelayLabel.setMaxSize(245, 35);

        subtitlesWrapper.getChildren().addAll(subtitlesTitle, subtitlesChooserTab, openSubtitlesTab, adjustDelayTab);
        subtitlesController.subtitlesPane.getChildren().add(scrollPane);

        focusNodes.add(subtitlesOptionsButton);
        focusNodes.add(subtitlesChooserTab);
        focusNodes.add(openSubtitlesTab);
        focusNodes.add(adjustDelayTab);
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


        subtitlesController.openSubtitlesPane.scrollPane.setVisible(true);
        subtitlesController.openSubtitlesPane.scrollPane.setMouseTransparent(false);

        subtitlesController.openSubtitlesPane.container.requestFocus();


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
        focusNodes.add(1, subtitlesTab);
        focusNodes.add(2, subtitlesTab.removeButton);

        if(subtitlesTabs.isEmpty()) subtitlesChooserTab.setStyle("-fx-border-width: 1 0 0 0;");
        subtitlesTabs.add(subtitlesTab);
        scrollPane.setVvalue(0);
        subtitlesTab.selectSubtitles(true);
        subtitlesWrapper.requestFocus();
    }

    public void focusForward(){
        int newFocus;

        if(focus.get() >= focusNodes.size() - 1 || focus.get() == -1) newFocus = 0;
        else newFocus = focus.get() + 1;

        keyboardFocusOn(focusNodes.get(newFocus));
        if(newFocus == 0) scrollPane.setVvalue(0);
        else {
            Node node = focusNodes.get(newFocus);
            if(node instanceof Button) Utilities.setScroll(scrollPane, focusNodes.get(newFocus - 1));
            else Utilities.setScroll(scrollPane, focusNodes.get(newFocus));
        }
    }

    public void focusBackward(){
        int newFocus;

        if(focus.get() == 0) newFocus = focusNodes.size() - 1;
        else if(focus.get() == -1) newFocus = 0;
        else newFocus = focus.get() - 1;

        keyboardFocusOn(focusNodes.get(newFocus));
        if(newFocus == 0) scrollPane.setVvalue(0);
        else {
            Node node = focusNodes.get(newFocus);
            if(node instanceof Button) Utilities.setScroll(scrollPane, focusNodes.get(newFocus - 1));
            else Utilities.setScroll(scrollPane, focusNodes.get(newFocus));
        }
    }

    public void resetFocusNodes(){
        focusNodes.clear();
        focusNodes.add(subtitlesOptionsButton);
        focusNodes.add(subtitlesChooserTab);
        focusNodes.add(openSubtitlesTab);
        focusNodes.add(adjustDelayTab);
    }
}
