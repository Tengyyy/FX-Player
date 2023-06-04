package tengy;

import tengy.Chapters.ChapterController;
import tengy.Menu.MenuController;
import tengy.Menu.Settings.Action;
import tengy.PlaybackSettings.PlaybackSettingsController;
import tengy.PlaybackSettings.PlaybackSettingsState;
import tengy.Subtitles.SubtitlesController;
import tengy.Subtitles.SubtitlesState;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ControlBarController implements Initializable {


    @FXML
    VBox controlBar;

    @FXML
    public
    StackPane controlBarWrapper;

    @FXML
    public
    Button fullScreenButton;
    @FXML
    Button playButton;
    @FXML
    Button volumeButton;
    @FXML
    public
    Button settingsButton;
    @FXML
    Button nextVideoButton;
    @FXML
    public
    Button subtitlesButton;
    @FXML
    Button previousVideoButton;
    @FXML
    public
    Button miniplayerButton;

    @FXML
    public Slider durationSlider;

    @FXML
    StackPane previousVideoPane;
    @FXML
    StackPane playButtonPane;
    @FXML
    StackPane nextVideoPane;
    @FXML
    StackPane volumeButtonPane;
    @FXML
    StackPane subtitlesButtonPane;
    @FXML
    StackPane settingsButtonPane;
    @FXML
    StackPane miniplayerButtonPane;
    @FXML
    StackPane fullScreenButtonPane;
    @FXML
    StackPane durationPane;
    @FXML
    StackPane labelBoxContainer;

    @FXML
    public
    Line subtitlesButtonLine;

    @FXML
    public Region previousVideoIcon, replayIcon, nextVideoIcon, volumeIcon, subtitlesIcon, settingsIcon, fullScreenIcon, miniplayerIcon;

    @FXML
    public
    HBox trackContainer;

    @FXML
    public GridPane buttonGrid;

    public ArrayList<DurationTrack> durationTracks = new ArrayList<>();
    public DurationTrack defaultTrack = new DurationTrack(0 , 1);
    public DurationTrack hoverTrack = null;
    public DurationTrack activeTrack = null;

    SVGPath previousVideoSVG, replaySVG, nextVideoSVG, lowVolumeSVG, subtitlesSVG, settingsSVG, maximizeSVG, minimizeSVG, miniplayerSVG;

    MainController mainController;
    PlaybackSettingsController playbackSettingsController;
    MenuController menuController;
    SubtitlesController subtitlesController;
    ChapterController chapterController;


    double volumeValue;

    boolean muted = false;
    boolean isExited = true;
    boolean showingTimeLeft = false;
    public boolean durationSliderHover = false;

    public boolean titleShowing = true;
    public boolean controlBarShowing = true;


    // variables to keep track of whether mouse is hovering any control button
    boolean previousVideoButtonHover = false;
    boolean playButtonHover = false;
    boolean nextVideoButtonHover = false;

    boolean volumeButtonHover = false;
    public boolean subtitlesButtonHover = false;
    public boolean settingsButtonHover = false;
    public boolean fullScreenButtonHover = false;
    public boolean miniplayerButtonHover = false;

    boolean previousVideoButtonEnabled = false;
    boolean playButtonEnabled = false;
    public boolean nextVideoButtonEnabled = false;



    public MouseEventTracker mouseEventTracker;

    public ControlTooltip play;
    public ControlTooltip mute;
    public ControlTooltip settings;
    public ControlTooltip fullScreen;
    public ControlTooltip subtitles;
    public VideoTooltip nextVideoTooltip;
    public VideoTooltip previousVideoTooltip;
    public ControlTooltip miniplayer;

    MediaInterface mediaInterface;

    ScaleTransition fullScreenButtonScaleTransition;

    PauseTransition seekTimer = new PauseTransition(Duration.millis(50));
    PauseTransition previewTimer = new PauseTransition(Duration.millis(200));

    StackPane controlBarBackground = new StackPane();


    public PauseTransition pauseTransition;

    double lastKnownSliderHoverPosition = -1000;

    double thumbScale = 0;

    TranslateTransition volumeSliderAnimation = null;

    ParallelTransition showControlsTransition = null;
    ParallelTransition hideControlsTransition = null;
    ParallelTransition showTitleTransition = null;
    ParallelTransition hideTitleTransition = null;

    MoveTo playPauseCorner1 = new MoveTo(0, 0);
    LineTo playPauseCorner2 = new LineTo(0, 22);
    LineTo playPauseCorner3 = new LineTo(9.5, 16.5);
    LineTo playPauseCorner4 = new LineTo(9.5, 5.5);
    MoveTo playPauseCorner5 = new MoveTo(9, 5.25);
    LineTo playPauseCorner6 = new LineTo(9, 16.75);
    LineTo playPauseCorner7 = new LineTo(18, 11);
    LineTo playPauseCorner8 = new LineTo(18, 11);

    Path playPauseLeftPath = new Path(playPauseCorner1, playPauseCorner2, playPauseCorner3, playPauseCorner4, new ClosePath());
    Path playPauseRightPath = new Path(playPauseCorner5, playPauseCorner6, playPauseCorner7, playPauseCorner8, new ClosePath());

    Pane playPausePane = new Pane();

    Timeline playPauseTransition = null;

    Arc volumeHighArc = new Arc();
    Line volumeMuteLine = new Line();

    ParallelTransition volumeIconShapeTransition = null;
    
    ClippedNode labelBoxWrapper;

    public HBox labelBox = new HBox();
    StackPane volumeSliderPane = new StackPane();

    Label durationLabel = new Label();
    public ProgressBar volumeTrack = new ProgressBar();
    public Slider volumeSlider = new Slider();

    ScaleTransition thumbTransition = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        trackContainer.getChildren().add(defaultTrack.progressBar);

        controlBarWrapper.setViewOrder(2);
        controlBarWrapper.getStyleClass().add("controlBarWrapper");

        previousVideoSVG = new SVGPath();
        previousVideoSVG.setContent(SVG.PREVIOUS_VIDEO.getContent());

        replaySVG = new SVGPath();
        replaySVG.setContent(SVG.REPLAY.getContent());

        nextVideoSVG = new SVGPath();
        nextVideoSVG.setContent(SVG.NEXT_VIDEO.getContent());

        lowVolumeSVG = new SVGPath();
        lowVolumeSVG.setContent(SVG.VOLUME_LOW.getContent());

        subtitlesSVG = new SVGPath();
        subtitlesSVG.setContent(SVG.SUBTITLES.getContent());

        settingsSVG = new SVGPath();
        settingsSVG.setContent(SVG.SETTINGS.getContent());

        maximizeSVG = new SVGPath();
        maximizeSVG.setContent(SVG.MAXIMIZE.getContent());

        minimizeSVG = new SVGPath();
        minimizeSVG.setContent(SVG.MINIMIZE.getContent());

        miniplayerSVG = new SVGPath();
        miniplayerSVG.setContent(SVG.MINIPLAYER.getContent());

        labelBox.setMinWidth(150);
        labelBox.setPadding(new Insets(0, 10, 0, 0));
        labelBox.setTranslateX(-83);
        labelBox.setFillHeight(true);
        labelBox.setAlignment(Pos.CENTER_LEFT);
        labelBox.getChildren().addAll(volumeSliderPane, durationLabel);


        volumeSliderPane.setPrefSize(68, 30);
        volumeSliderPane.setMaxSize(68, 30);
        volumeSliderPane.getChildren().addAll(volumeTrack, volumeSlider);
        volumeSliderPane.setAlignment(Pos.CENTER);
        HBox.setMargin(volumeSliderPane, new Insets(0, 15, 0, 0));

        volumeTrack.setProgress(0.5);
        volumeTrack.getStyleClass().add("volumeTrack");
        volumeTrack.setMinSize(48, 4);
        volumeTrack.setPrefSize(48, 4);
        volumeTrack.setMaxSize(48, 4);

        volumeSlider.setMin(0);
        volumeSlider.setMax(100);
        volumeSlider.setValue(50);
        volumeSlider.setBlockIncrement(5);
        volumeSlider.setCursor(Cursor.HAND);
        volumeSlider.getStyleClass().add("volumeSlider");
        volumeSlider.setMinSize(60, 8);
        volumeSlider.setPrefSize(60, 8);
        volumeSlider.setMaxSize(60, 8);
        volumeSlider.setFocusTraversable(false);


        durationLabel.setPrefHeight(30);
        durationLabel.setMaxHeight(30);
        durationLabel.setAlignment(Pos.CENTER_LEFT);
        durationLabel.getStyleClass().add("controlBarLabel");
        durationLabel.setText("00:00/00:00");

        durationLabel.setOnMouseClicked((e) -> toggleDurationLabel());
        durationLabel.setOnMouseEntered(e -> AnimationsClass.animateTextColor(durationLabel, Color.rgb(255, 255, 255), 200));
        durationLabel.setOnMouseExited(e -> AnimationsClass.animateTextColor(durationLabel, Color.rgb(200, 200, 200), 200));

        labelBoxWrapper = new ClippedNode(labelBox);

        labelBoxContainer.getChildren().add(labelBoxWrapper);

        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(labelBoxContainer.widthProperty());
        clip.heightProperty().bind(labelBoxContainer.heightProperty());

        labelBoxContainer.setClip(clip);

        durationPane.setMouseTransparent(true);

        controlBarBackground.setStyle("-fx-background-color: linear-gradient(to top, rgba(0,0,0,0.8), rgba(0,0,0,0));");
        controlBarBackground.setMouseTransparent(true);
        controlBarBackground.setPrefHeight(200);
        controlBarBackground.setMaxHeight(200);
        StackPane.setAlignment(controlBarBackground, Pos.BOTTOM_CENTER);

        controlBar.setOnMouseClicked(e -> {
            if(subtitlesController.subtitlesState != SubtitlesState.CLOSED) subtitlesController.closeSubtitles();
            if(playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) playbackSettingsController.closeSettings();

        });

        previousVideoIcon.setShape(previousVideoSVG);
        replayIcon.setShape(replaySVG);
        replayIcon.setVisible(false);
        replayIcon.setPrefSize(24, 24);
        nextVideoIcon.setShape(nextVideoSVG);
        volumeIcon.setShape(lowVolumeSVG);

        volumeIcon.setPrefSize(15, 19);
        volumeIcon.setTranslateX(-3);

        subtitlesIcon.setShape(subtitlesSVG);
        settingsIcon.setShape(settingsSVG);

        miniplayerIcon.setShape(miniplayerSVG);
        fullScreenIcon.setShape(maximizeSVG);

        playButton.setBackground(Background.EMPTY);
        nextVideoButton.setBackground(Background.EMPTY);
        miniplayerButton.setBackground(Background.EMPTY);
        fullScreenButton.setBackground(Background.EMPTY);
        settingsButton.setBackground(Background.EMPTY);
        volumeButton.setBackground(Background.EMPTY);
        subtitlesButton.setBackground(Background.EMPTY);

        previousVideoButton.setFocusTraversable(false);
        playButton.setFocusTraversable(false);
        nextVideoButton.setFocusTraversable(false);
        miniplayerButton.setFocusTraversable(false);
        fullScreenButton.setFocusTraversable(false);
        settingsButton.setFocusTraversable(false);
        volumeButton.setFocusTraversable(false);
        subtitlesButton.setFocusTraversable(false);



        previousVideoButton.setOnAction(e -> {
            previousVideoButton.requestFocus();
            previousVideoButtonClick();
        });
        playButton.setOnAction((e) -> {
            playButton.requestFocus();
            playButtonClick();
        });
        nextVideoButton.setOnAction(e -> {
            nextVideoButton.requestFocus();
            nextVideoButtonClick();
        });
        volumeButton.setOnAction(e -> {
            volumeButton.requestFocus();
            volumeButtonClick();
        });
        subtitlesButton.setOnAction(e -> {
            subtitlesButton.requestFocus();
            subtitlesButtonClick();
        });
        settingsButton.setOnAction(e -> {
            settingsButton.requestFocus();
            settingsButtonClick();
        });
        miniplayerButton.setOnAction(e -> {
            miniplayerButton.requestFocus();
            miniplayerButtonClick();
        });
        fullScreenButton.setOnAction(e -> {
            fullScreenButton.requestFocus();
            fullScreenButtonClick();
        });
        
        
        previousVideoButton.setOnMouseEntered(e -> previousVideoButtonHoverOn());
        previousVideoButton.setOnMouseExited(e -> previousVideoButtonHoverOff());


        playButton.setOnMouseEntered(e -> playButtonHoverOn());
        playButton.setOnMouseExited(e -> playButtonHoverOff());

        playButtonPane.getChildren().add(playPausePane);

        playPausePane.getChildren().addAll(playPauseLeftPath, playPauseRightPath);
        playPausePane.setMouseTransparent(true);
        playPausePane.setPrefSize(18,22);
        playPausePane.setMaxSize(18, 22);

        playPauseLeftPath.setFill(Color.rgb(100, 100, 100));
        playPauseLeftPath.setStroke(Color.TRANSPARENT);
        playPauseLeftPath.setStrokeWidth(0);

        playPauseRightPath.setStroke(Color.TRANSPARENT);
        playPauseRightPath.setStrokeWidth(0);
        playPauseRightPath.setFill(Color.rgb(100, 100, 100));


        nextVideoButton.setOnMouseEntered(e -> nextVideoButtonHoverOn());

        nextVideoButton.setOnMouseExited(e -> nextVideoButtonHoverOff());

        volumeButtonPane.getChildren().addAll(volumeHighArc, volumeMuteLine);

        volumeHighArc.setRadiusX(5);
        volumeHighArc.setRadiusY(6);
        volumeHighArc.setFill(Color.rgb(200, 200, 200));
        volumeHighArc.setStartAngle(270);
        volumeHighArc.setLength(180);
        volumeHighArc.setTranslateX(6);
        volumeHighArc.setTranslateY(0.5);
        volumeHighArc.setStroke(Color.TRANSPARENT);
        volumeHighArc.setMouseTransparent(true);

        Arc volumeHighArcOuterClip = new Arc();
        volumeHighArcOuterClip.setRadiusX(9);
        volumeHighArcOuterClip.setRadiusY(10);
        volumeHighArcOuterClip.setStartAngle(270);
        volumeHighArcOuterClip.setLength(180);

        Arc volumeHighInnerClip = new Arc();
        volumeHighInnerClip.setRadiusX(6);
        volumeHighInnerClip.setRadiusY(7);
        volumeHighInnerClip.setStartAngle(270);
        volumeHighInnerClip.setLength(180);

        Shape volumeHighClip = Shape.subtract(volumeHighArcOuterClip, volumeHighInnerClip);

        volumeHighArc.setClip(volumeHighClip);


        volumeMuteLine.setFill(Color.rgb(200, 200, 200));
        volumeMuteLine.setMouseTransparent(true);
        volumeMuteLine.setStroke(Color.rgb(200,200,200));
        volumeMuteLine.setStrokeWidth(0);
        volumeMuteLine.setStartX(0);
        volumeMuteLine.setStartY(0);
        volumeMuteLine.setTranslateX(15);
        volumeMuteLine.setTranslateY(3);
        volumeMuteLine.setEndX(0);
        volumeMuteLine.setEndY(0);
        StackPane.setAlignment(volumeMuteLine, Pos.TOP_LEFT);


        volumeButton.setOnMouseEntered(e -> {

            Rectangle rect = new Rectangle();
            rect.setFill(Color.rgb(200, 200, 200));

            FillTransition volumeIconTransition = new FillTransition();
            volumeIconTransition.setShape(rect);
            volumeIconTransition.setDuration(Duration.millis(200));
            volumeIconTransition.setFromValue(Color.rgb(200, 200, 200));
            volumeIconTransition.setToValue(Color.rgb(255, 255, 255));

            volumeIconTransition.setInterpolator(new Interpolator() {
                @Override
                protected double curve(double t) {
                    volumeIcon.setBackground(new Background(new BackgroundFill(rect.getFill(), CornerRadii.EMPTY, Insets.EMPTY)));
                    return t;
                }
            });


            FillTransition volumeHighArcTransition = new FillTransition(Duration.millis(200), volumeHighArc);
            volumeHighArcTransition.setFromValue(Color.rgb(200, 200, 200));
            volumeHighArcTransition.setToValue(Color.rgb(255, 255, 255));

            StrokeTransition volumeMuteLineTransition = new StrokeTransition(Duration.millis(200), volumeMuteLine);
            volumeMuteLineTransition.setFromValue(Color.rgb(200, 200, 200));
            volumeMuteLineTransition.setToValue(Color.rgb(255, 255, 255));

            ParallelTransition parallelTransition = new ParallelTransition();
            parallelTransition.getChildren().addAll(volumeIconTransition, volumeHighArcTransition, volumeMuteLineTransition);
            parallelTransition.play();

            volumeButtonHover = true;
        });

        volumeButton.setOnMouseExited(e -> {

            Rectangle rect = new Rectangle();
            rect.setFill(Color.rgb(255, 255, 255));

            FillTransition volumeIconTransition = new FillTransition();
            volumeIconTransition.setShape(rect);
            volumeIconTransition.setDuration(Duration.millis(200));
            volumeIconTransition.setFromValue(Color.rgb(255, 255, 255));
            volumeIconTransition.setToValue(Color.rgb(200, 200, 200));

            volumeIconTransition.setInterpolator(new Interpolator() {
                @Override
                protected double curve(double t) {
                    volumeIcon.setBackground(new Background(new BackgroundFill(rect.getFill(), CornerRadii.EMPTY, Insets.EMPTY)));
                    return t;
                }
            });


            FillTransition volumeHighArcTransition = new FillTransition(Duration.millis(200), volumeHighArc);
            volumeHighArcTransition.setFromValue(Color.rgb(255, 255, 255));
            volumeHighArcTransition.setToValue(Color.rgb(200, 200, 200));

            StrokeTransition volumeMuteLineTransition = new StrokeTransition(Duration.millis(200), volumeMuteLine);
            volumeMuteLineTransition.setFromValue(Color.rgb(255, 255, 255));
            volumeMuteLineTransition.setToValue(Color.rgb(200, 200, 200));

            ParallelTransition parallelTransition = new ParallelTransition();
            parallelTransition.getChildren().addAll(volumeIconTransition, volumeHighArcTransition, volumeMuteLineTransition);
            parallelTransition.play();

            volumeButtonHover = false;
        });

        subtitlesButton.setOnMouseEntered(e -> {
            controlButtonHoverOn(subtitlesButtonPane);
            subtitlesButtonHover = true;
        });

        subtitlesButton.setOnMouseExited(e -> {
            controlButtonHoverOff(subtitlesButtonPane);
            subtitlesButtonHover = false;
        });

        settingsButton.setOnMouseEntered(e -> {
            controlButtonHoverOn(settingsButtonPane);
            settingsButtonHover = true;
        });

        settingsButton.setOnMouseExited(e -> {
            controlButtonHoverOff(settingsButtonPane);
            settingsButtonHover = false;
        });

        miniplayerButton.setOnMouseEntered(e -> {
            controlButtonHoverOn(miniplayerButtonPane);
            miniplayerButtonHover = true;
        });

        miniplayerButton.setOnMouseExited(e -> {
            controlButtonHoverOff(miniplayerButtonPane);
            miniplayerButtonHover = false;
        });


        fullScreenButton.setOnMouseEntered(e -> {
            controlButtonHoverOn(fullScreenButtonPane);
            fullScreenButtonHover = true;
            fullScreenButtonScaleTransition = AnimationsClass.scaleAnimation(200, fullScreenIcon, 1, 1.3, 1, 1.3, true, 2, true);
        });

        fullScreenButton.setOnMouseExited(e -> {
            controlButtonHoverOff(fullScreenButtonPane);
            fullScreenButtonHover = false;
        });


        volumeSlider.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> volumeSlider.setValueChanging(true));
        volumeSlider.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> volumeSlider.setValueChanging(false));



        volumeSlider.valueChangingProperty().addListener((observable, oldValue, newValue) -> {

            if (!newValue) {
                if (playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) playbackSettingsController.closeSettings();
                if (subtitlesController.subtitlesState != SubtitlesState.CLOSED) subtitlesController.closeSubtitles();

                if (isExited) volumeSliderExit();
            }
        });


        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {

            mediaInterface.changeVolume(newValue.doubleValue());
            volumeTrack.setProgress(volumeSlider.getValue() / 100);

            if (newValue.doubleValue() == 0) {
                muted = true;
                mute.updateActionText("Unmute");

                if(volumeIconShapeTransition != null && volumeIconShapeTransition.getStatus() == Animation.Status.RUNNING) volumeIconShapeTransition.stop();

                Timeline arcWidthTimeline = new Timeline(new KeyFrame(Duration.millis(200),
                        new KeyValue(volumeHighArc.radiusXProperty(), 9, Interpolator.EASE_BOTH)));

                Timeline arcHeightTimeline = new Timeline(new KeyFrame(Duration.millis(200),
                        new KeyValue(volumeHighArc.radiusYProperty(), 10, Interpolator.EASE_BOTH)));

                Timeline muteXTimeline = new Timeline(new KeyFrame(Duration.millis(200),
                        new KeyValue(volumeMuteLine.endXProperty(), 19, Interpolator.EASE_BOTH)));

                Timeline muteYTimeline = new Timeline(new KeyFrame(Duration.millis(200),
                        new KeyValue(volumeMuteLine.endYProperty(), 22, Interpolator.EASE_BOTH)));

                volumeMuteLine.setStrokeWidth(3);

                volumeIconShapeTransition = new ParallelTransition(arcWidthTimeline, arcHeightTimeline, muteXTimeline, muteYTimeline);
                volumeIconShapeTransition.play();
            }
            else if (newValue.doubleValue() <= 50 && (oldValue.doubleValue() == 0 || oldValue.doubleValue() > 50)) {
                muted = false;
                mute.updateActionText("Mute");

                if(volumeIconShapeTransition != null && volumeIconShapeTransition.getStatus() == Animation.Status.RUNNING) volumeIconShapeTransition.stop();

                Timeline arcWidthTimeline = new Timeline(new KeyFrame(Duration.millis(200),
                        new KeyValue(volumeHighArc.radiusXProperty(), 5, Interpolator.EASE_BOTH)));

                Timeline arcHeightTimeline = new Timeline(new KeyFrame(Duration.millis(200),
                        new KeyValue(volumeHighArc.radiusYProperty(), 6, Interpolator.EASE_BOTH)));

                Timeline muteXTimeline = new Timeline(new KeyFrame(Duration.millis(200),
                        new KeyValue(volumeMuteLine.endXProperty(), 0, Interpolator.EASE_BOTH)));

                Timeline muteYTimeline = new Timeline(new KeyFrame(Duration.millis(200),
                        new KeyValue(volumeMuteLine.endYProperty(), 0, Interpolator.EASE_BOTH)));


                volumeIconShapeTransition = new ParallelTransition(arcWidthTimeline, arcHeightTimeline, muteXTimeline, muteYTimeline);
                volumeIconShapeTransition.setOnFinished(e -> {
                    volumeMuteLine.setStrokeWidth(0);
                });
                volumeIconShapeTransition.play();

            }
            else if(newValue.doubleValue() > 50 && oldValue.doubleValue() <= 50){
                muted = false;
                mute.updateActionText("Mute");

                if(volumeIconShapeTransition != null && volumeIconShapeTransition.getStatus() == Animation.Status.RUNNING) volumeIconShapeTransition.stop();

                Timeline arcWidthTimeline = new Timeline(new KeyFrame(Duration.millis(200),
                        new KeyValue(volumeHighArc.radiusXProperty(), 9, Interpolator.EASE_BOTH)));

                Timeline arcHeightTimeline = new Timeline(new KeyFrame(Duration.millis(200),
                        new KeyValue(volumeHighArc.radiusYProperty(), 10, Interpolator.EASE_BOTH)));

                Timeline muteXTimeline = new Timeline(new KeyFrame(Duration.millis(200),
                        new KeyValue(volumeMuteLine.endXProperty(), 0, Interpolator.EASE_BOTH)));

                Timeline muteYTimeline = new Timeline(new KeyFrame(Duration.millis(200),
                        new KeyValue(volumeMuteLine.endYProperty(), 0, Interpolator.EASE_BOTH)));


                volumeIconShapeTransition = new ParallelTransition(arcWidthTimeline, arcHeightTimeline, muteXTimeline, muteYTimeline);
                volumeIconShapeTransition.setOnFinished(e -> {
                    volumeMuteLine.setStrokeWidth(0);
                });
                volumeIconShapeTransition.play();
            }
        });

        previewTimer.setOnFinished(e -> {

            if(pauseTransition != null && pauseTransition.getStatus() == Animation.Status.RUNNING) pauseTransition.stop();

            if(mainController.miniplayerActive && mainController.miniplayer.miniplayerController.slider.isValueChanging()) mediaInterface.updatePreviewFrame(mainController.miniplayer.miniplayerController.slider.getValue()/mainController.miniplayer.miniplayerController.slider.getMax(), true);
            else if(durationSlider.isValueChanging() || (mainController.sliderHoverBox.isVisible() && menuController.settingsPage.preferencesSection.seekPreviewOn.get())) mediaInterface.updatePreviewFrame(lastKnownSliderHoverPosition, true);
        });

        durationSlider.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if (playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) {
                playbackSettingsController.closeSettings();
            }

            if (subtitlesController.subtitlesState != SubtitlesState.CLOSED) {
                subtitlesController.closeSubtitles();
            }


        });

        // this part has to be run later because the slider thumb loads in later than the slider itself
        Platform.runLater(() -> {

            durationSlider.lookup(".thumb").setMouseTransparent(true);

            durationSlider.lookup(".thumb").setScaleX(0);
            durationSlider.lookup(".thumb").setScaleY(0);
            durationSlider.lookup(".thumb").setStyle("-fx-background-color: red;");

            durationSlider.lookup(".track").setCursor(Cursor.HAND);

            durationSlider.lookup(".track").addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
                if (e.getButton() == MouseButton.PRIMARY){
                    durationSlider.setValueChanging(true);
                }
                else {
                    e.consume();
                }
            });
            durationSlider.lookup(".track").addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
                if (e.getButton() == MouseButton.PRIMARY){
                    durationSlider.setValueChanging(false);
                }
            });

            durationSlider.lookup(".track").setOnMouseMoved(e -> {
                previewTimer.playFromStart();

                updateSliderHover(e.getX()/durationSlider.lookup(".track").getBoundsInLocal().getWidth());

                String newTime = Utilities.durationToString(Duration.seconds((e.getX()) / (durationSlider.lookup(".track").getBoundsInLocal().getMaxX()) * durationSlider.getMax()));
                mainController.sliderHoverBox.timeLabel.setText(newTime);

                double minTranslation = (mainController.sliderHoverBox.localToScene(mainController.sliderHoverBox.getBoundsInLocal()).getMinX() - mainController.sliderHoverBox.getTranslateX() - durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMinX()) * -1;
                double maxTranslation = durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMaxX() - mainController.sliderHoverBox.localToScene(mainController.sliderHoverBox.getBoundsInLocal()).getMaxX() + mainController.sliderHoverBox.getTranslateX();

                double newTranslation = Math.max(minTranslation, Math.min(maxTranslation, e.getSceneX() - (mainController.sliderHoverBox.localToScene(mainController.sliderHoverBox.getBoundsInLocal()).getMinX() + mainController.sliderHoverBox.getBoundsInLocal().getMaxX() / 2) + mainController.sliderHoverBox.getTranslateX()));

                mainController.sliderHoverBox.setTranslateX(newTranslation);

                if (playbackSettingsController.playbackSettingsState == PlaybackSettingsState.CLOSED && subtitlesController.subtitlesState == SubtitlesState.CLOSED) {
                    mainController.sliderHoverBox.setVisible(true);
                }


                if ( menuController.settingsPage.preferencesSection.seekPreviewOn.get()
                        && menuController.queuePage.queueBox.activeItem.get() != null
                        && menuController.queuePage.queueBox.activeItem.get().getMediaItem() != null
                        && menuController.queuePage.queueBox.activeItem.get().getMediaItem().hasVideo()
                        && !mediaInterface.videoDisabled){

                    if(pauseTransition != null && pauseTransition.getStatus() == Animation.Status.RUNNING) return;

                    pauseTransition = new PauseTransition(Duration.millis(50));
                    pauseTransition.setOnFinished(j -> mediaInterface.updatePreviewFrame(lastKnownSliderHoverPosition, false));

                    pauseTransition.playFromStart();
                }

            });

            durationSlider.lookup(".track").setOnMouseEntered((e) -> {

                previewTimer.playFromStart();

                durationSliderHover = true;
                durationSliderHoverOn(e.getX()/durationSlider.lookup(".track").getBoundsInLocal().getWidth());

                String newTime = Utilities.durationToString(Duration.seconds(e.getX() / (durationSlider.lookup(".track").getBoundsInLocal().getMaxX()) * durationSlider.getMax()));
                mainController.sliderHoverBox.timeLabel.setText(newTime);

                double minTranslation = (mainController.sliderHoverBox.localToScene(mainController.sliderHoverBox.getBoundsInLocal()).getMinX() - mainController.sliderHoverBox.getTranslateX() - durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMinX()) * -1;
                double maxTranslation = durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMaxX() - mainController.sliderHoverBox.localToScene(mainController.sliderHoverBox.getBoundsInLocal()).getMaxX() + mainController.sliderHoverBox.getTranslateX();

                double newTranslation = Math.max(minTranslation, Math.min(maxTranslation, e.getSceneX() - (mainController.sliderHoverBox.localToScene(mainController.sliderHoverBox.getBoundsInLocal()).getMinX() + mainController.sliderHoverBox.getBoundsInLocal().getMaxX() / 2) + mainController.sliderHoverBox.getTranslateX()));

                mainController.sliderHoverBox.setTranslateX(newTranslation);


                if (playbackSettingsController.playbackSettingsState == PlaybackSettingsState.CLOSED && subtitlesController.subtitlesState == SubtitlesState.CLOSED) {
                    mainController.sliderHoverBox.setVisible(true);
                }


                if(menuController.settingsPage.preferencesSection.seekPreviewOn.get()
                        && menuController.queuePage.queueBox.activeItem.get() != null
                        && menuController.queuePage.queueBox.activeItem.get().getMediaItem() != null
                        && menuController.queuePage.queueBox.activeItem.get().getMediaItem().hasVideo()
                        && !mediaInterface.videoDisabled){
                    if(pauseTransition != null && pauseTransition.getStatus() == Animation.Status.RUNNING) return;

                    mediaInterface.updatePreviewFrame(lastKnownSliderHoverPosition, false);

                    pauseTransition = new PauseTransition(Duration.millis(50));

                    pauseTransition.playFromStart();
                }


            });

            durationSlider.lookup(".track").setOnMouseExited((e) -> {
                durationSliderHover = false;

                if (!e.isPrimaryButtonDown()) {
                    durationSliderHoverOff(e.getX()/durationSlider.lookup(".track").getBoundsInLocal().getMaxX());
                    mainController.sliderHoverBox.setVisible(false);
                    mainController.sliderHoverBox.chapterlabel.setText("");
                    mainController.sliderHoverBox.setImage(null);
                }
            });




            durationSlider.lookup(".track").addEventFilter(MouseEvent.MOUSE_DRAGGED, e -> {

                if (e.isPrimaryButtonDown()) return;

                previewTimer.playFromStart();

                updateSliderHover(e.getX()/durationSlider.lookup(".track").getBoundsInLocal().getMaxX());


                e.consume();

                String newTime = Utilities.durationToString(Duration.seconds(e.getX() / (durationSlider.lookup(".track").getBoundsInLocal().getMaxX()) * durationSlider.getMax()));
                mainController.sliderHoverBox.timeLabel.setText(newTime);

                double minTranslation = (mainController.sliderHoverBox.localToScene(mainController.sliderHoverBox.getBoundsInLocal()).getMinX() - mainController.sliderHoverBox.getTranslateX() - durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMinX()) * -1;
                double maxTranslation = durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMaxX() - mainController.sliderHoverBox.localToScene(mainController.sliderHoverBox.getBoundsInLocal()).getMaxX() + mainController.sliderHoverBox.getTranslateX();

                double newTranslation = Math.max(minTranslation, Math.min(maxTranslation, e.getSceneX() - (mainController.sliderHoverBox.localToScene(mainController.sliderHoverBox.getBoundsInLocal()).getMinX() + mainController.sliderHoverBox.getBoundsInLocal().getMaxX() / 2) + mainController.sliderHoverBox.getTranslateX()));

                mainController.sliderHoverBox.setTranslateX(newTranslation);


                if(menuController.settingsPage.preferencesSection.seekPreviewOn.get()
                        && menuController.queuePage.queueBox.activeItem.get() != null
                        && menuController.queuePage.queueBox.activeItem.get().getMediaItem() != null
                        && menuController.queuePage.queueBox.activeItem.get().getMediaItem().hasVideo()
                        && !mediaInterface.videoDisabled){

                    if(pauseTransition != null && pauseTransition.getStatus() == Animation.Status.RUNNING) return;

                    pauseTransition = new PauseTransition(Duration.millis(50));
                    pauseTransition.setOnFinished(j -> mediaInterface.updatePreviewFrame(lastKnownSliderHoverPosition, false));

                    pauseTransition.playFromStart();
                }
            });
        });



        durationSlider.valueProperty().addListener((observable, oldValue, newValue) -> {

            updateProgress(Math.min(1, Math.max(0, newValue.doubleValue() / durationSlider.getMax())));

            if (!mediaInterface.mediaActive.get()) return;

            mediaInterface.updateMedia(newValue.doubleValue());

            if (oldValue.doubleValue() <= 5 && newValue.doubleValue() > 5 && mediaInterface.mediaActive.get()) {

                if (!previousVideoButtonEnabled) enablePreviousVideoButton();
                else {
                    previousVideoTooltip.updateTooltip(null);

                    if (mainController.miniplayerActive){
                        mainController.miniplayer.miniplayerController.previousVideoButtonTooltip.updateActionText("Replay");
                        mainController.miniplayer.miniplayerController.previousVideoButtonTooltip.updateHotkeyText("");

                    }
                }
            }
            else if (oldValue.doubleValue() > 5 && newValue.doubleValue() <= 5 && mediaInterface.mediaActive.get()) {

                if (menuController.queuePage.queueBox.activeItem.get() == null || menuController.queuePage.queueBox.activeIndex.get() == 0) {
                    disablePreviousVideoButton();
                } else {
                    if (mainController.miniplayerActive){
                        mainController.miniplayer.miniplayerController.previousVideoButtonTooltip.updateActionText("Previous video");
                        mainController.miniplayer.miniplayerController.previousVideoButtonTooltip.updateHotkeyText(mainController.hotkeyController.getHotkeyString(Action.PREVIOUS));
                    }

                    previousVideoTooltip.updateTooltip(menuController.queuePage.queueBox.queue.get(menuController.queuePage.queueBox.queueOrder.get(menuController.queuePage.queueBox.activeIndex.get() - 1)));
                }

            }

            subtitlesController.updateSubtitles(newValue.doubleValue() * 1000);


            if (durationSlider.isValueChanging()) {

                previewTimer.playFromStart();

                updateSliderHover(Math.min(1, Math.max(0, newValue.doubleValue() / durationSlider.getMax())));

                mainController.sliderHoverBox.timeLabel.setText(Utilities.durationToString(Duration.seconds(durationSlider.getValue())));

                double minTranslation = (mainController.sliderHoverBox.localToScene(mainController.sliderHoverBox.getBoundsInLocal()).getMinX() - mainController.sliderHoverBox.getTranslateX() - durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMinX()) * -1;
                double maxTranslation = durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMaxX() - mainController.sliderHoverBox.localToScene(mainController.sliderHoverBox.getBoundsInLocal()).getMaxX() + mainController.sliderHoverBox.getTranslateX();

                double newTranslation = Math.max(minTranslation, Math.min(maxTranslation, durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMinX() + durationSlider.lookup(".track").getBoundsInLocal().getMaxX() * (newValue.doubleValue() / durationSlider.getMax()) - (mainController.sliderHoverBox.localToScene(mainController.sliderHoverBox.getBoundsInLocal()).getMinX() + mainController.sliderHoverBox.getBoundsInLocal().getMaxX() / 2) + mainController.sliderHoverBox.getTranslateX()));

                mainController.sliderHoverBox.setTranslateX(newTranslation);

                if(menuController.queuePage.queueBox.activeItem.get() != null
                        && menuController.queuePage.queueBox.activeItem.get().getMediaItem() != null
                        && menuController.queuePage.queueBox.activeItem.get().getMediaItem().hasVideo()
                        && !mediaInterface.videoDisabled){
                    if(pauseTransition != null && pauseTransition.getStatus() == Animation.Status.RUNNING) return;

                    pauseTransition = new PauseTransition(Duration.millis(50));
                    pauseTransition.setOnFinished(e -> mediaInterface.updatePreviewFrame(lastKnownSliderHoverPosition, false));

                    pauseTransition.playFromStart();
                }

            }

        });

        durationSlider.valueChangingProperty().addListener((observable, oldValue, newValue) -> {


            if (newValue) { // pause video when user starts seeking

                if(menuController.queuePage.queueBox.activeItem.get() != null
                        && menuController.queuePage.queueBox.activeItem.get().getMediaItem() != null
                        && menuController.queuePage.queueBox.activeItem.get().getMediaItem().hasVideo()
                        && !mediaInterface.videoDisabled){
                    if(mainController.miniplayerActive){
                        if(mediaInterface.playing.get()) mainController.miniplayer.miniplayerController.seekImageView.setImage(mainController.miniplayer.miniplayerController.videoImageView.getImage());
                        mainController.miniplayer.miniplayerController.seekImageView.setVisible(true);
                        mainController.miniplayer.miniplayerController.videoImageView.setVisible(false);
                    }
                    else {
                        if(mediaInterface.playing.get()) mainController.seekImageView.setImage(mainController.videoImageView.getImage());
                        mainController.seekImageView.setVisible(true);
                        mainController.videoImageView.setVisible(false);
                    }
                }
                seekTimer.playFromStart();
                if (mediaInterface.playing.get()) mediaInterface.embeddedMediaPlayer.controls().pause();
                mediaInterface.playing.set(false);

                updateProgress(Math.min(1, Math.max(0, durationSlider.getValue() / durationSlider.getMax())));

                mainController.sliderHoverBox.timeLabel.setText(Utilities.durationToString(Duration.seconds(durationSlider.getValue())));


                double minTranslation = (mainController.sliderHoverBox.localToScene(mainController.sliderHoverBox.getBoundsInLocal()).getMinX() - mainController.sliderHoverBox.getTranslateX() - durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMinX()) * -1;
                double maxTranslation = durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMaxX() - mainController.sliderHoverBox.localToScene(mainController.sliderHoverBox.getBoundsInLocal()).getMaxX() + mainController.sliderHoverBox.getTranslateX();

                double newTranslation = Math.max(minTranslation, Math.min(maxTranslation, durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMinX() + durationSlider.lookup(".track").getBoundsInLocal().getMaxX() * (durationSlider.getValue() / durationSlider.getMax()) - (mainController.sliderHoverBox.localToScene(mainController.sliderHoverBox.getBoundsInLocal()).getMinX() + mainController.sliderHoverBox.getBoundsInLocal().getMaxX() / 2) + mainController.sliderHoverBox.getTranslateX()));

                mainController.sliderHoverBox.setTranslateX(newTranslation);


                if (playbackSettingsController.playbackSettingsState == PlaybackSettingsState.CLOSED && subtitlesController.subtitlesState == SubtitlesState.CLOSED) {
                    mainController.sliderHoverBox.setVisible(true);
                }


                if(menuController.queuePage.queueBox.activeItem.get() != null
                        && menuController.queuePage.queueBox.activeItem.get().getMediaItem() != null
                        && menuController.queuePage.queueBox.activeItem.get().getMediaItem().hasVideo()
                        && !mediaInterface.videoDisabled){
                    if(pauseTransition != null && pauseTransition.getStatus() == Animation.Status.RUNNING) return;

                    pauseTransition = new PauseTransition(Duration.millis(50));
                    pauseTransition.setOnFinished(e -> mediaInterface.updatePreviewFrame(lastKnownSliderHoverPosition, false));

                    pauseTransition.playFromStart();
                }

            }
            else {

                mainController.sliderHoverBox.setVisible(false);

                if(!durationSliderHover){
                    mainController.sliderHoverBox.setImage(null);
                    mainController.sliderHoverBox.chapterlabel.setText("");
                    durationSliderHoverOff(Math.min(1, Math.max(0, durationSlider.getValue() / durationSlider.getMax())));
                }


                if (seekTimer.getStatus() == Animation.Status.RUNNING) seekTimer.stop();
                if (mainController.miniplayerActive && mainController.miniplayer.miniplayerController.seekTimer.getStatus() == Animation.Status.RUNNING)
                    mainController.miniplayer.miniplayerController.seekTimer.stop();



                if (playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) { // close settings pane after user finishes seeking media (if its open)
                    playbackSettingsController.closeSettings();
                }

                if (subtitlesController.subtitlesState != SubtitlesState.CLOSED) {
                    subtitlesController.closeSubtitles();
                }

                if (mediaInterface.atEnd) {
                    mediaInterface.defaultEnd();
                }
                else {
                    mediaInterface.seek(Duration.seconds(durationSlider.getValue())); // seeks to exact position when user finishes dragging
                    if (mediaInterface.wasPlaying) mediaInterface.play(false);
                }
            }
        });


        seekTimer.setOnFinished(e -> mediaInterface.pause());

    }

    public void init(MainController mainController, PlaybackSettingsController playbackSettingsController, MenuController menuController, MediaInterface mediaInterface, SubtitlesController subtitlesController, ChapterController chapterController) {
        this.mainController = mainController;
        this.playbackSettingsController = playbackSettingsController;
        this.menuController = menuController;
        this.mediaInterface = mediaInterface;
        this.subtitlesController = subtitlesController;
        this.chapterController = chapterController;

        mouseEventTracker = new MouseEventTracker(mainController, this, playbackSettingsController); // creates instance of the MouseEventTracker class which keeps track of when to hide and show the control-bar
    }

    public void toggleDurationLabel() {

        if (playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) playbackSettingsController.closeSettings();
        if (subtitlesController.subtitlesState != SubtitlesState.CLOSED) subtitlesController.closeSubtitles();


        if(mediaInterface.mediaActive.get()){
            if (showingTimeLeft) Utilities.setCurrentTimeLabel(durationLabel, Duration.seconds(durationSlider.getValue()), Duration.seconds(durationSlider.getMax()));
            else Utilities.setTimeLeftLabel(durationLabel, Duration.seconds(durationSlider.getValue()), Duration.seconds(durationSlider.getMax()));
        }
        else {
            if (showingTimeLeft) Utilities.setCurrentTimeLabel(durationLabel, Duration.ZERO, Duration.ZERO);
            else Utilities.setTimeLeftLabel(durationLabel, Duration.ZERO, Duration.ZERO);
        }

        showingTimeLeft = !showingTimeLeft;
    }


    public void playButtonClick() {
        if (playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) playbackSettingsController.closeSettings();
        if (subtitlesController.subtitlesState != SubtitlesState.CLOSED) subtitlesController.closeSubtitles();

        if (mediaInterface.atEnd) mediaInterface.replay();
        else if (mediaInterface.playing.get()) {
            mediaInterface.wasPlaying = false;
            mediaInterface.pause();
        } else mediaInterface.play(false);
    }

    public void enterArea() {
        if (isExited && !volumeSlider.isValueChanging()) {
            volumeSliderEnter();
        }
        isExited = false;
    }

    public void exitArea() {
        if (!volumeSlider.isValueChanging() && !isExited) {
            volumeSliderExit();
        }
        isExited = true;
    }

    public void volumeSliderEnter() {
        if(volumeSliderAnimation != null && volumeSliderAnimation.getStatus() == Animation.Status.RUNNING) volumeSliderAnimation.stop();

        volumeSliderAnimation = new TranslateTransition(Duration.millis(100), labelBox);
        volumeSliderAnimation.setFromX(labelBox.getTranslateX());
        volumeSliderAnimation.setToX(0);

        volumeSliderAnimation.play();
    }

    public void volumeSliderExit() {
        if(volumeSliderAnimation != null && volumeSliderAnimation.getStatus() == Animation.Status.RUNNING) volumeSliderAnimation.stop();

        volumeSliderAnimation = new TranslateTransition(Duration.millis(100), labelBox);
        volumeSliderAnimation.setFromX(labelBox.getTranslateX());
        volumeSliderAnimation.setToX(-83);

        volumeSliderAnimation.play();
    }

    public void toggleFullScreen() {

        subtitlesController.subtitlesBox.cancelDrag();
        App.stage.setFullScreen(!App.stage.isFullScreen());

        mainController.videoImageView.requestFocus();

        if (App.stage.isFullScreen()) {
            fullScreenIcon.setShape(minimizeSVG);
            App.fullScreen = true;

            fullScreen.updateActionText("Exit full screen");
        }
        else {
            fullScreenIcon.setShape(maximizeSVG);
            App.fullScreen = false;

            fullScreen.updateActionText("Full screen");
        }
    }


    public void volumeButtonClick() {
        if (playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) playbackSettingsController.closeSettings();
        if (subtitlesController.subtitlesState != SubtitlesState.CLOSED) subtitlesController.closeSubtitles();

        if (!muted)
            mute();
        else
            unmute();
    }

    public void mute() {
        muted = true;
        volumeValue = volumeSlider.getValue(); //stores the value of the volumeslider before setting it to 0
        volumeSlider.setValue(0);
    }

    public void unmute() {
        muted = false;
        volumeSlider.setValue(volumeValue); // sets volume back to the value it was at before muting
    }


    public void previousVideoButtonClick() {
        if (playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) playbackSettingsController.closeSettings();
        if (subtitlesController.subtitlesState != SubtitlesState.CLOSED) subtitlesController.closeSubtitles();

        if(durationSlider.getValue() > 5) mediaInterface.replay();
        else mediaInterface.playPrevious();
    }

    public void nextVideoButtonClick() {
        if (playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) playbackSettingsController.closeSettings();
        if (subtitlesController.subtitlesState != SubtitlesState.CLOSED) subtitlesController.closeSubtitles();

        mediaInterface.playNext();
    }


    public void settingsButtonClick() {

        if (playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) playbackSettingsController.closeSettings();
        else playbackSettingsController.openSettings();
    }


    public void fullScreenButtonClick() {
        if (playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) playbackSettingsController.closeSettings();
        if (subtitlesController.subtitlesState != SubtitlesState.CLOSED) subtitlesController.closeSubtitles();

        toggleFullScreen();
    }

    public void miniplayerButtonClick() {

        if (playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) playbackSettingsController.closeSettings();
        if (subtitlesController.subtitlesState != SubtitlesState.CLOSED) subtitlesController.closeSubtitles();

        if (mainController.miniplayerActive) mainController.closeMiniplayer();
        else mainController.openMiniplayer();
    }


    public void controlBarClick() {
        if (playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) playbackSettingsController.closeSettings();
        if (subtitlesController.subtitlesState != SubtitlesState.CLOSED) subtitlesController.closeSubtitles();
    }

    public void subtitlesButtonClick() {
        if (subtitlesController.subtitlesState != SubtitlesState.CLOSED) subtitlesController.closeSubtitles();
        else subtitlesController.openSubtitles();
    }

    public void durationSliderHoverOn(double value) {
        lastKnownSliderHoverPosition = value;


        if(durationSlider.isValueChanging()) return;

        ScaleTransition sliderTrackHoverOn = null;

        if(thumbTransition != null && thumbTransition.getStatus() == Animation.Status.RUNNING) thumbTransition.stop();
        thumbTransition = null;


        if(durationTracks.isEmpty()){
            if(thumbScale != 1.1) {
                thumbTransition = AnimationsClass.scaleAnimation(100, durationSlider.lookup(".thumb"), durationSlider.lookup(".thumb").getScaleX(), 1.1, durationSlider.lookup(".thumb").getScaleY(), 1.1, false, 1, false);
                thumbScale = 1.1;
            }

            sliderTrackHoverOn = AnimationsClass.scaleAnimation(100, defaultTrack.progressBar, 1, 1, defaultTrack.progressBar.getScaleY(), 1.8, false, 1, false);
        }
        else {
            for(DurationTrack durationTrack : durationTracks){
                if(durationTrack.startTime <= value && durationTrack.endTime >= value){
                    hoverTrack = durationTrack;
                    mainController.sliderHoverBox.chapterlabel.setText(chapterController.chapterDescriptions.get(durationTracks.indexOf(durationTrack)).name());
                    if(durationSlider.getValue()/durationSlider.getMax() >= durationTrack.startTime && durationSlider.getValue()/durationSlider.getMax() <= durationTrack.endTime && !durationSlider.isValueChanging() && thumbScale != 1.25) {
                        thumbTransition = AnimationsClass.scaleAnimation(100, durationSlider.lookup(".thumb"), durationSlider.lookup(".thumb").getScaleX(), 1.25, durationSlider.lookup(".thumb").getScaleY(), 1.25, false, 1, false);
                        thumbScale = 1.25;
                    }
                    sliderTrackHoverOn = AnimationsClass.scaleAnimation(100, durationTrack.progressBar, 1, 1, durationTrack.progressBar.getScaleY(), 3, false, 1 , false);
                    break;
                }
            }

            if(thumbTransition == null && thumbScale != 0.9){
                thumbTransition = AnimationsClass.scaleAnimation(100, durationSlider.lookup(".thumb"), durationSlider.lookup(".thumb").getScaleX(), 0.9, durationSlider.lookup(".thumb").getScaleY(), 0.9, false, 1, false);
                thumbScale = 0.9;
            }

        }
        if(thumbTransition != null) thumbTransition.play();
        if(sliderTrackHoverOn != null) sliderTrackHoverOn.play();
    }


    public void durationSliderHoverOff(double value) {
        if(durationSlider.isValueChanging()){
            lastKnownSliderHoverPosition = value;
            return;
        }

        if(thumbTransition != null && thumbTransition.getStatus() == Animation.Status.RUNNING) thumbTransition.stop();
        thumbTransition = null;


        thumbTransition = AnimationsClass.scaleAnimation(100, durationSlider.lookup(".thumb"), durationSlider.lookup(".thumb").getScaleX(), 0, durationSlider.lookup(".thumb").getScaleY(), 0, false, 1, false);
        thumbScale = 0;

        List<Transition> sliderHoverTransitions = new ArrayList<>();

        if(durationTracks.isEmpty()){
            sliderHoverTransitions.add(AnimationsClass.scaleAnimation(100, defaultTrack.progressBar, 1, 1, defaultTrack.progressBar.getScaleY(), 1, false, 1, false));
        }
        else {
            for(DurationTrack durationTrack : durationTracks){
                sliderHoverTransitions.add(AnimationsClass.scaleAnimation(100, durationTrack.progressBar, 1, 1, durationTrack.progressBar.getScaleY(), 1, false, 1 , false));
            }
        }

        AnimationsClass.parallelAnimation(true, sliderHoverTransitions);
        if(thumbTransition != null) thumbTransition.play();

        hoverTrack = null;
        lastKnownSliderHoverPosition = value;
    }

    public void updateSliderHover(double value){

        if(!durationTracks.isEmpty()){
            if(hoverTrack == null || (hoverTrack.startTime > value || hoverTrack.endTime < value)){

                if(thumbTransition != null && thumbTransition.getStatus() == Animation.Status.RUNNING) thumbTransition.stop();
                thumbTransition = null;

                ScaleTransition sliderTrackHoverOff = null;
                if(hoverTrack != null) sliderTrackHoverOff = AnimationsClass.scaleAnimation(100, hoverTrack.progressBar, 1, 1, hoverTrack.progressBar.getScaleY(), 1, false, 1 , false);
                ScaleTransition sliderTrackHoverOn = null;

                for(DurationTrack durationTrack : durationTracks){
                    if(durationTrack.startTime <= value && durationTrack.endTime >= value){
                        hoverTrack = durationTrack;
                        mainController.sliderHoverBox.chapterlabel.setText(chapterController.chapterDescriptions.get(durationTracks.indexOf(durationTrack)).name());
                        if(durationSlider.getValue()/durationSlider.getMax() >= durationTrack.startTime && durationSlider.getValue()/durationSlider.getMax() <= durationTrack.endTime && !durationSlider.isValueChanging() && thumbScale != 1.25){
                            thumbTransition = AnimationsClass.scaleAnimation(100, durationSlider.lookup(".thumb"), durationSlider.lookup(".thumb").getScaleX(), 1.25, durationSlider.lookup(".thumb").getScaleY(), 1.25, false, 1, false);
                            thumbScale = 1.25;
                        }
                        sliderTrackHoverOn = AnimationsClass.scaleAnimation(100, durationTrack.progressBar, 1, 1, durationTrack.progressBar.getScaleY(), 3, false, 1 , false);
                        break;
                    }
                }

                if(thumbTransition == null && !durationSlider.isValueChanging() && thumbScale != 0.9){
                    thumbTransition = AnimationsClass.scaleAnimation(100, durationSlider.lookup(".thumb"), durationSlider.lookup(".thumb").getScaleX(), 0.9, durationSlider.lookup(".thumb").getScaleY(), 0.9, false, 1, false);
                    thumbScale = 0.9;
                }


                AnimationsClass.parallelAnimation(true, sliderTrackHoverOn, sliderTrackHoverOff);
                if(thumbTransition != null) thumbTransition.play();
            }
        }

        lastKnownSliderHoverPosition = value;
    }

    public void controlButtonHoverOn(StackPane stackPane) {
        Region icon = (Region) stackPane.getChildren().get(1);

        AnimationsClass.animateBackgroundColor(icon, Color.rgb(200, 200, 200), Color.rgb(255, 255, 255), 200);
    }


    public void controlButtonHoverOff(StackPane stackPane) {
        Region icon = (Region) stackPane.getChildren().get(1);

        AnimationsClass.animateBackgroundColor(icon, Color.rgb(255, 255, 255), Color.rgb(200, 200, 200), 200);
    }

    public void previousVideoButtonHoverOn() {
        previousVideoButtonHover = true;

        if (previousVideoButtonEnabled) {
            AnimationsClass.animateBackgroundColor(previousVideoIcon, Color.rgb(200, 200, 200), Color.rgb(255, 255, 255), 200);
            previousVideoTooltip.mouseHover.set(true);
        } else {
            AnimationsClass.animateBackgroundColor(previousVideoIcon, Color.rgb(120, 120, 120), Color.rgb(150, 150, 150), 200);
        }
    }

    public void previousVideoButtonHoverOff() {
        previousVideoButtonHover = false;

        if (previousVideoButtonEnabled) {
            AnimationsClass.animateBackgroundColor(previousVideoIcon, Color.rgb(255, 255, 255), Color.rgb(200, 200, 200), 200);
        } else {
            AnimationsClass.animateBackgroundColor(previousVideoIcon, Color.rgb(150, 150, 150), Color.rgb(120, 120, 120), 200);
        }

        previousVideoTooltip.mouseHover.set(false);
    }

    public void playButtonHoverOn() {
        playButtonHover = true;

        Color fromColor;
        Color toColor;

        if (playButtonEnabled) {
            fromColor = Color.rgb(200, 200, 200);
            toColor = Color.rgb(255, 255, 255);

            play.mouseHover.set(true);
        }
        else {
            fromColor = Color.rgb(120, 120, 120);
            toColor = Color.rgb(150, 150, 150);
        }

        Rectangle rect = new Rectangle();
        rect.setFill(fromColor);

        FillTransition playIconTransition = new FillTransition();
        playIconTransition.setShape(rect);
        playIconTransition.setDuration(Duration.millis(200));
        playIconTransition.setFromValue(fromColor);
        playIconTransition.setToValue(toColor);

        playIconTransition.setInterpolator(new Interpolator() {
            @Override
            protected double curve(double t) {
                replayIcon.setBackground(new Background(new BackgroundFill(rect.getFill(), CornerRadii.EMPTY, Insets.EMPTY)));
                return t;
            }
        });

        FillTransition playPauseLeftTransition = new FillTransition(Duration.millis(200), playPauseLeftPath);
        playPauseLeftTransition.setFromValue(fromColor);
        playPauseLeftTransition.setToValue(toColor);

        FillTransition playPauseRightTransition = new FillTransition(Duration.millis(200), playPauseRightPath);
        playPauseRightTransition.setFromValue(fromColor);
        playPauseRightTransition.setToValue(toColor);

        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(playIconTransition, playPauseLeftTransition, playPauseRightTransition);
        parallelTransition.play();
    }

    public void playButtonHoverOff() {
        playButtonHover = false;

        Color fromColor;
        Color toColor;

        if (playButtonEnabled) {
            fromColor = Color.rgb(255, 255, 255);
            toColor = Color.rgb(200, 200, 200);
        }
        else {
            fromColor = Color.rgb(150, 150, 150);
            toColor = Color.rgb(120, 120, 120);
        }

        Rectangle rect = new Rectangle();
        rect.setFill(fromColor);

        FillTransition playIconTransition = new FillTransition();
        playIconTransition.setShape(rect);
        playIconTransition.setDuration(Duration.millis(200));
        playIconTransition.setFromValue(fromColor);
        playIconTransition.setToValue(toColor);

        playIconTransition.setInterpolator(new Interpolator() {
            @Override
            protected double curve(double t) {
                replayIcon.setBackground(new Background(new BackgroundFill(rect.getFill(), CornerRadii.EMPTY, Insets.EMPTY)));
                return t;
            }
        });

        FillTransition playPauseLeftTransition = new FillTransition(Duration.millis(200), playPauseLeftPath);
        playPauseLeftTransition.setFromValue(fromColor);
        playPauseLeftTransition.setToValue(toColor);

        FillTransition playPauseRightTransition = new FillTransition(Duration.millis(200), playPauseRightPath);
        playPauseRightTransition.setFromValue(fromColor);
        playPauseRightTransition.setToValue(toColor);

        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(playIconTransition, playPauseLeftTransition, playPauseRightTransition);
        parallelTransition.play();

        play.mouseHover.set(false);
    }

    public void nextVideoButtonHoverOn() {
        nextVideoButtonHover = true;

        if (nextVideoButtonEnabled) {
            AnimationsClass.animateBackgroundColor(nextVideoIcon, Color.rgb(200, 200, 200), Color.rgb(255, 255, 255), 200);
            nextVideoTooltip.mouseHover.set(true);
        } else {
            AnimationsClass.animateBackgroundColor(nextVideoIcon, Color.rgb(120, 120, 120), Color.rgb(150, 150, 150), 200);

        }
    }

    public void nextVideoButtonHoverOff() {
        nextVideoButtonHover = false;

        if (nextVideoButtonEnabled) {
            AnimationsClass.animateBackgroundColor(nextVideoIcon, Color.rgb(255, 255, 255), Color.rgb(200, 200, 200), 200);
        } else {
            AnimationsClass.animateBackgroundColor(nextVideoIcon, Color.rgb(150, 150, 150), Color.rgb(120, 120, 120), 200);
        }

        nextVideoTooltip.mouseHover.set(false);
    }

    public void enablePreviousVideoButton() {
        previousVideoButtonEnabled = true;

        if (previousVideoButtonHover) {
            previousVideoIcon.setStyle("-fx-background-color: rgb(255, 255, 255);");
        } else {
            previousVideoIcon.setStyle("-fx-background-color: rgb(200, 200, 200);");
        }

        if (mainController.miniplayerActive) mainController.miniplayer.miniplayerController.enablePreviousVideoButton();
        if(mainController.windowsTaskBarController != null) mainController.windowsTaskBarController.enablePreviousVideoButton();


        if (durationSlider.getValue() > 5)
            previousVideoTooltip.updateTooltip(null);
        else if(menuController.queuePage.queueBox.activeIndex.get() > 0){
            previousVideoTooltip.updateTooltip(menuController.queuePage.queueBox.queue.get(menuController.queuePage.queueBox.queueOrder.get(menuController.queuePage.queueBox.activeIndex.get() - 1)));
        }

        if (previousVideoButtonHover){
            previousVideoTooltip.mouseHover.set(true);
        }
    }

    public void disablePreviousVideoButton() {
        previousVideoButtonEnabled = false;

        if (previousVideoButtonHover) {
            previousVideoIcon.setStyle("-fx-background-color: rgb(150, 150, 150);");
        } else {
            previousVideoIcon.setStyle("-fx-background-color: rgb(120, 120, 120);");
        }

        if (mainController.miniplayerActive) mainController.miniplayer.miniplayerController.disablePreviousVideoButton();
        if(mainController.windowsTaskBarController != null) mainController.windowsTaskBarController.disablePreviousVideoButton();

        previousVideoTooltip.mouseHover.set(false);
    }

    public void enablePlayButton() {
        playButtonEnabled = true;

        if (playButtonHover) {
            replayIcon.setBackground(new Background(new BackgroundFill(Color.rgb(255, 255, 255), CornerRadii.EMPTY, Insets.EMPTY)));
            playPauseLeftPath.setFill(Color.rgb(255, 255, 255));
            playPauseRightPath.setFill(Color.rgb(255, 255, 255));
        }
        else {
            replayIcon.setBackground(new Background(new BackgroundFill(Color.rgb(200, 200, 200), CornerRadii.EMPTY, Insets.EMPTY)));
            playPauseLeftPath.setFill(Color.rgb(200, 200, 200));
            playPauseRightPath.setFill(Color.rgb(200, 200, 200));
        }

        if(mainController.windowsTaskBarController != null) mainController.windowsTaskBarController.enablePlayButton();

        if (mediaInterface.atEnd) play.updateActionText("Replay");
        else if (mediaInterface.playing.get()) play.updateActionText("Pause");
        else play.updateActionText("Play");

        play.enableTooltip();

        if (playButtonHover) play.mouseHover.set(true);
    }

    public void disablePlayButton() {
        playButtonEnabled = false;

        if (playButtonHover) {
            replayIcon.setBackground(new Background(new BackgroundFill(Color.rgb(150, 150, 150), CornerRadii.EMPTY, Insets.EMPTY)));
            playPauseLeftPath.setFill(Color.rgb(150, 150, 150));
            playPauseRightPath.setFill(Color.rgb(150, 150, 150));
        }
        else {
            replayIcon.setBackground(new Background(new BackgroundFill(Color.rgb(120, 120, 120), CornerRadii.EMPTY, Insets.EMPTY)));
            playPauseLeftPath.setFill(Color.rgb(120, 120, 120));
            playPauseRightPath.setFill(Color.rgb(120, 120, 120));
        }

        if(mainController.windowsTaskBarController != null) mainController.windowsTaskBarController.disablePlayButton();

        play.disableTooltip();
    }

    public void enableNextVideoButton() {
        nextVideoButtonEnabled = true;

        if (nextVideoButtonHover) {
            nextVideoIcon.setStyle("-fx-background-color: rgb(255, 255, 255);");
        } else {
            nextVideoIcon.setStyle("-fx-background-color: rgb(200, 200, 200);");
        }

        if (mainController.miniplayerActive) mainController.miniplayer.miniplayerController.enableNextVideoButton();
        if (mainController.windowsTaskBarController != null) mainController.windowsTaskBarController.enableNextVideoButton();



        if(menuController.queuePage.queueBox.activeItem.get() == null && !menuController.queuePage.queueBox.queue.isEmpty()) nextVideoTooltip.updateTooltip(menuController.queuePage.queueBox.queue.get(menuController.queuePage.queueBox.queueOrder.get(0)));
        else if(menuController.queuePage.queueBox.queue.size() > menuController.queuePage.queueBox.activeIndex.get()) nextVideoTooltip.updateTooltip(menuController.queuePage.queueBox.queue.get(menuController.queuePage.queueBox.queueOrder.get(menuController.queuePage.queueBox.activeIndex.get() + 1)));


        if (nextVideoButtonHover){
            nextVideoTooltip.mouseHover.set(true);
        }
    }

    public void disableNextVideoButton() {
        nextVideoButtonEnabled = false;

        if (nextVideoButtonHover) {
            nextVideoIcon.setStyle("-fx-background-color: rgb(150, 150, 150);");
        } else {
            nextVideoIcon.setStyle("-fx-background-color: rgb(120, 120, 120);");
        }

        if (mainController.miniplayerActive) mainController.miniplayer.miniplayerController.disableNextVideoButton();
        if(mainController.windowsTaskBarController != null) mainController.windowsTaskBarController.disableNextVideoButton();

        nextVideoTooltip.mouseHover.set(false);
    }

    public void updateNextAndPreviousVideoButtons(){
        if(menuController.queuePage.queueBox.queue.isEmpty()){
            disableNextVideoButton();
            disablePreviousVideoButton();
        }
        else {
            if(menuController.queuePage.queueBox.activeItem.get() != null){
                if(menuController.queuePage.queueBox.activeIndex.get() > 0 || durationSlider.getValue() > 5) enablePreviousVideoButton();
                else disablePreviousVideoButton();

                if(menuController.queuePage.queueBox.activeIndex.get() < menuController.queuePage.queueBox.queue.size() - 1) enableNextVideoButton();
                else disableNextVideoButton();
            }
            else {
                disablePreviousVideoButton();
                enableNextVideoButton();
            }
        }
    }

    public void updateProgress(double progress){
        if(durationTracks.isEmpty()){
            defaultTrack.progressBar.setProgress(progress);
        }
        else {
            if (activeTrack != null && progress >= activeTrack.startTime && (progress < activeTrack.endTime || progress == 1)){
                double max = activeTrack.endTime - activeTrack.startTime;
                double curr = progress - activeTrack.startTime;
                activeTrack.progressBar.setProgress(curr/max);
            }
            else {

                for(DurationTrack durationTrack : durationTracks){
                    if(progress > durationTrack.endTime || progress == 1){
                        durationTrack.progressBar.setProgress(1);
                        if(durationSliderHover && !durationSlider.isValueChanging() && hoverTrack == durationTrack){
                            if(thumbScale != 0.9){

                                if(thumbTransition != null && thumbTransition.getStatus() == Animation.Status.RUNNING) thumbTransition.stop();
                                thumbTransition = null;

                                durationSlider.lookup(".thumb").setScaleX(0.9);
                                durationSlider.lookup(".thumb").setScaleY(0.9);

                                thumbScale = 0.9;
                            }
                        }
                    }
                    else if(progress >= durationTrack.startTime){
                        double max = durationTrack.endTime - durationTrack.startTime;
                        double curr = progress - durationTrack.startTime;
                        durationTrack.progressBar.setProgress(curr/max);

                        if(durationSliderHover && hoverTrack == durationTrack){

                            if(thumbScale != 1.25){

                                if(thumbTransition != null && thumbTransition.getStatus() == Animation.Status.RUNNING) thumbTransition.stop();
                                thumbTransition = null;

                                durationSlider.lookup(".thumb").setScaleX(1.25);
                                durationSlider.lookup(".thumb").setScaleY(1.25);

                                thumbScale = 1.25;
                            }
                        }

                        activeTrack = durationTrack;
                        chapterController.setActiveChapter(durationTracks.indexOf(durationTrack));
                    }
                    else {
                        durationTrack.progressBar.setProgress(0);

                        if(durationSliderHover && !durationSlider.isValueChanging() && hoverTrack == durationTrack){

                            if(thumbScale != 0.9){

                                if(thumbTransition != null && thumbTransition.getStatus() == Animation.Status.RUNNING) thumbTransition.stop();
                                thumbTransition = null;

                                durationSlider.lookup(".thumb").setScaleX(0.9);
                                durationSlider.lookup(".thumb").setScaleY(0.9);

                                thumbScale = 0.9;
                            }
                        }
                    }
                }
            }
        }
    }


    public void loadTooltips(){
        play = new ControlTooltip(mainController, "Play", mainController.hotkeyController.getHotkeyString(Action.PLAY_PAUSE2), playButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
        mute = new ControlTooltip(mainController,"Mute", mainController.hotkeyController.getHotkeyString(Action.MUTE), volumeButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
        settings = new ControlTooltip(mainController,"Playback settings", mainController.hotkeyController.getHotkeyString(Action.PLAYBACK_SETTINGS), settingsButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
        fullScreen = new ControlTooltip(mainController,"Full screen", mainController.hotkeyController.getHotkeyString(Action.FULLSCREEN), fullScreenButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
        subtitles = new ControlTooltip(mainController,"Subtitles", mainController.hotkeyController.getHotkeyString(Action.SUBTITLES), subtitlesButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
        miniplayer = new ControlTooltip(mainController,"Miniplayer", mainController.hotkeyController.getHotkeyString(Action.MINIPLAYER), miniplayerButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
        previousVideoTooltip = new VideoTooltip(mainController, previousVideoButton, true, mainController.hotkeyController.getHotkeyString(Action.PREVIOUS));
        nextVideoTooltip = new VideoTooltip(mainController, nextVideoButton, false, mainController.hotkeyController.getHotkeyString(Action.NEXT));


        disablePreviousVideoButton();
        disablePlayButton();
        disableNextVideoButton();
    }


    public void showControls() {

        if(hideControlsTransition != null && hideControlsTransition.getStatus() == Animation.Status.RUNNING) hideControlsTransition.stop();
        if(showControlsTransition != null && showControlsTransition.getStatus() == Animation.Status.RUNNING) showControlsTransition.stop();

        controlBarShowing = true;
        controlBarWrapper.setMouseTransparent(false);

        if(subtitlesController.subtitlesBox.subtitlesTransition != null && subtitlesController.subtitlesBox.subtitlesTransition.getStatus() == Animation.Status.RUNNING) subtitlesController.subtitlesBox.subtitlesTransition.stop();

        if(subtitlesController.subtitlesSelected.get()) subtitlesController.subtitlesBox.subtitlesContainer.setMouseTransparent(false);

        TranslateTransition captionsTransition = new TranslateTransition(Duration.millis(100), subtitlesController.subtitlesBox.subtitlesContainer);
        captionsTransition.setCycleCount(1);
        captionsTransition.setFromY(subtitlesController.subtitlesBox.subtitlesContainer.getTranslateY());

        if((subtitlesController.subtitlesBox.subtitlesLocation == Pos.BOTTOM_CENTER || subtitlesController.subtitlesBox.subtitlesLocation == Pos.BOTTOM_LEFT || subtitlesController.subtitlesBox.subtitlesLocation == Pos.BOTTOM_RIGHT) && !mainController.miniplayerActive)
            captionsTransition.setToY(-90);
        else if((subtitlesController.subtitlesBox.subtitlesLocation == Pos.TOP_CENTER || subtitlesController.subtitlesBox.subtitlesLocation == Pos.TOP_LEFT || subtitlesController.subtitlesBox.subtitlesLocation == Pos.TOP_RIGHT) && !mainController.miniplayerActive)
            captionsTransition.setToY(70);
        else
            captionsTransition.setToY(subtitlesController.subtitlesBox.subtitlesContainer.getTranslateY());

        FadeTransition controlBarFade = new FadeTransition(Duration.millis(100), controlBar);
        controlBarFade.setFromValue(controlBar.getOpacity());
        controlBarFade.setToValue(1);
        controlBarFade.setCycleCount(1);

        FadeTransition controlBarBackgroundFade = new FadeTransition(Duration.millis(100), controlBarBackground);
        controlBarBackgroundFade.setFromValue(controlBarBackground.getOpacity());
        controlBarBackgroundFade.setToValue(1);
        controlBarBackgroundFade.setCycleCount(1);

        showControlsTransition = new ParallelTransition(captionsTransition, controlBarFade, controlBarBackgroundFade);
        showControlsTransition.setInterpolator(Interpolator.LINEAR);

        showControlsTransition.play();
    }

    public void hideControls() {

        if(hideControlsTransition != null && hideControlsTransition.getStatus() == Animation.Status.RUNNING) hideControlsTransition.stop();
        if(showControlsTransition != null && showControlsTransition.getStatus() == Animation.Status.RUNNING) showControlsTransition.stop();

        controlBarShowing = false;
        controlBarWrapper.setMouseTransparent(true);

        if(subtitlesController.subtitlesBox.subtitlesTransition != null && subtitlesController.subtitlesBox.subtitlesTransition.getStatus() == Animation.Status.RUNNING) subtitlesController.subtitlesBox.subtitlesTransition.stop();

        subtitlesController.subtitlesBox.subtitlesContainer.setMouseTransparent(true);

        TranslateTransition captionsTransition = new TranslateTransition(Duration.millis(100), subtitlesController.subtitlesBox.subtitlesContainer);
        captionsTransition.setCycleCount(1);
        captionsTransition.setFromY(subtitlesController.subtitlesBox.subtitlesContainer.getTranslateY());

        if((subtitlesController.subtitlesBox.subtitlesLocation == Pos.BOTTOM_CENTER || subtitlesController.subtitlesBox.subtitlesLocation == Pos.BOTTOM_LEFT || subtitlesController.subtitlesBox.subtitlesLocation == Pos.BOTTOM_RIGHT) && !mainController.miniplayerActive){
            captionsTransition.setToY(-30);
        }
        else if((subtitlesController.subtitlesBox.subtitlesLocation == Pos.TOP_CENTER || subtitlesController.subtitlesBox.subtitlesLocation == Pos.TOP_LEFT || subtitlesController.subtitlesBox.subtitlesLocation == Pos.TOP_RIGHT) && !mainController.miniplayerActive){
            captionsTransition.setToY(30);
        }
        else {
            captionsTransition.setToY(subtitlesController.subtitlesBox.subtitlesContainer.getTranslateY());
        }

        FadeTransition controlBarFade = new FadeTransition(Duration.millis(100), controlBar);
        controlBarFade.setFromValue(controlBar.getOpacity());
        controlBarFade.setToValue(0);
        controlBarFade.setCycleCount(1);

        FadeTransition controlBarBackgroundFade = new FadeTransition(Duration.millis(100), controlBarBackground);
        controlBarBackgroundFade.setFromValue(controlBarBackground.getOpacity());
        controlBarBackgroundFade.setToValue(0);
        controlBarBackgroundFade.setCycleCount(1);


        hideControlsTransition = new ParallelTransition(captionsTransition, controlBarFade, controlBarBackgroundFade);
        hideControlsTransition.setInterpolator(Interpolator.LINEAR);
        hideControlsTransition.setOnFinished((e) -> {
            subtitlesController.subtitlesBox.subtitlesAnimating = false;
            subtitlesController.subtitlesBox.subtitlesContainer.setStyle("-fx-background-color: transparent;");
        });

        hideControlsTransition.play();
    }

    public void showTitle(){

        if(hideTitleTransition != null && hideTitleTransition.getStatus() == Animation.Status.RUNNING) hideTitleTransition.stop();
        if(showTitleTransition != null && showTitleTransition.getStatus() == Animation.Status.RUNNING) showTitleTransition.stop();
        
        titleShowing = true;

        mainController.videoTitleBox.setVisible(true);
        FadeTransition videoTitleTransition = new FadeTransition(Duration.millis(100), mainController.videoTitleBox);
        videoTitleTransition.setFromValue(mainController.videoTitleBox.getOpacity());
        videoTitleTransition.setToValue(1);

        mainController.videoTitleBackground.setVisible(true);
        FadeTransition videoTitleBackgroundTransition = new FadeTransition(Duration.millis(100), mainController.videoTitleBackground);
        videoTitleBackgroundTransition.setFromValue(mainController.videoTitleBackground.getOpacity());
        videoTitleBackgroundTransition.setToValue(1);

        mainController.menuButtonPane.setVisible(true);
        FadeTransition menuButtonTransition = new FadeTransition(Duration.millis(100), mainController.menuButtonPane);
        menuButtonTransition.setFromValue(mainController.menuButtonPane.getOpacity());
        menuButtonTransition.setToValue(1);


        showTitleTransition = new ParallelTransition(videoTitleTransition, videoTitleBackgroundTransition, menuButtonTransition);
        showTitleTransition.setInterpolator(Interpolator.LINEAR);

        showTitleTransition.play();

    }

    public void hideTitle(){

        if(hideTitleTransition != null && hideTitleTransition.getStatus() == Animation.Status.RUNNING) hideTitleTransition.stop();
        if(showTitleTransition != null && showTitleTransition.getStatus() == Animation.Status.RUNNING) showTitleTransition.stop();

        titleShowing = false;

        FadeTransition videoTitleTransition = new FadeTransition(Duration.millis(100), mainController.videoTitleBox);
        videoTitleTransition.setFromValue(mainController.videoTitleBox.getOpacity());
        videoTitleTransition.setToValue(0);

        FadeTransition videoTitleBackgroundTransition = new FadeTransition(Duration.millis(100), mainController.videoTitleBackground);
        videoTitleBackgroundTransition.setFromValue(mainController.videoTitleBackground.getOpacity());
        videoTitleBackgroundTransition.setToValue(0);

        FadeTransition menuButtonTransition = new FadeTransition(Duration.millis(100), mainController.menuButtonPane);
        menuButtonTransition.setFromValue(mainController.menuButtonPane.getOpacity());
        menuButtonTransition.setToValue(0);


        hideTitleTransition = new ParallelTransition(videoTitleTransition, videoTitleBackgroundTransition, menuButtonTransition);
        hideTitleTransition.setInterpolator(Interpolator.LINEAR);
        hideTitleTransition.setOnFinished((e) -> {
            mainController.videoTitleBox.setVisible(false);
            mainController.videoTitleBackground.setVisible(false);
            mainController.menuButtonPane.setVisible(false);
        });

        hideTitleTransition.play();
    }

    public void play() {

        if(playPauseTransition != null && playPauseTransition.getStatus() == Animation.Status.RUNNING)
            playPauseTransition.stop();

        if(replayIcon.isVisible()){
            replayIcon.setVisible(false);

            playPauseCorner1.setX(0);
            playPauseCorner1.setY(0);

            playPauseCorner2.setX(0);
            playPauseCorner2.setY(22);

            playPauseCorner3.setX(6);
            playPauseCorner3.setY(22);

            playPauseCorner4.setX(6);
            playPauseCorner4.setY(0);

            playPauseCorner5.setX(12);
            playPauseCorner5.setY(0);

            playPauseCorner6.setX(12);
            playPauseCorner6.setY(22);

            playPauseCorner7.setX(18);
            playPauseCorner7.setY(22);

            playPauseCorner8.setX(18);
            playPauseCorner8.setY(0);

            playPausePane.setVisible(true);
        }
        else {
            playToPauseMorph();
        }

        play.updateActionText("Pause");

        if(mainController.windowsTaskBarController != null) mainController.windowsTaskBarController.play();
    }

    public void pause() {

        if(playPauseTransition != null && playPauseTransition.getStatus() == Animation.Status.RUNNING)
            playPauseTransition.stop();


        if(replayIcon.isVisible()){
            replayIcon.setVisible(false);

            playPauseCorner1.setX(0);
            playPauseCorner1.setY(0);

            playPauseCorner2.setX(0);
            playPauseCorner2.setY(22);

            playPauseCorner3.setX(9.5);
            playPauseCorner3.setY(16.5);

            playPauseCorner4.setX(9.5);
            playPauseCorner4.setY(5.5);

            playPauseCorner5.setX(9);
            playPauseCorner5.setY(5.25);

            playPauseCorner6.setX(9);
            playPauseCorner6.setY(16.75);

            playPauseCorner7.setX(18);
            playPauseCorner7.setY(11);

            playPauseCorner8.setX(18);
            playPauseCorner8.setY(11);

            playPausePane.setVisible(true);
        }
        else {
            pauseToPlayMorph();
        }

        play.updateActionText("Play");

        if(mainController.windowsTaskBarController != null) mainController.windowsTaskBarController.pause();
    }

    public void end() {

        if(playPauseTransition != null && playPauseTransition.getStatus() == Animation.Status.RUNNING)
            playPauseTransition.stop();

        playPausePane.setVisible(false);

        replayIcon.setVisible(true);

        play.updateActionText("Replay");

        if(mainController.windowsTaskBarController != null) mainController.windowsTaskBarController.end();
    }

    private void playToPauseMorph(){

        replayIcon.setVisible(false);
        playPausePane.setVisible(true);

        playPauseTransition = new Timeline(
            new KeyFrame(Duration.millis(250),
                new KeyValue(playPauseCorner1.xProperty(), 0),
                new KeyValue(playPauseCorner1.yProperty(), 0),

                new KeyValue(playPauseCorner2.xProperty(), 0),
                new KeyValue(playPauseCorner2.yProperty(), 22),

                new KeyValue(playPauseCorner3.xProperty(), 6),
                new KeyValue(playPauseCorner3.yProperty(), 22),

                new KeyValue(playPauseCorner4.xProperty(), 6),
                new KeyValue(playPauseCorner4.yProperty(), 0),

                new KeyValue(playPauseCorner5.xProperty(), 12),
                new KeyValue(playPauseCorner5.yProperty(), 0),

                new KeyValue(playPauseCorner6.xProperty(), 12),
                new KeyValue(playPauseCorner6.yProperty(), 22),

                new KeyValue(playPauseCorner7.xProperty(), 18),
                new KeyValue(playPauseCorner7.yProperty(), 22),

                new KeyValue(playPauseCorner8.xProperty(), 18),
                new KeyValue(playPauseCorner8.yProperty(), 0))
        );

        playPauseTransition.play();
    }

    private void pauseToPlayMorph(){

        replayIcon.setVisible(false);
        playPausePane.setVisible(true);

        playPauseTransition = new Timeline(
                new KeyFrame(Duration.millis(250),
                        new KeyValue(playPauseCorner1.xProperty(), 0),
                        new KeyValue(playPauseCorner1.yProperty(), 0),

                        new KeyValue(playPauseCorner2.xProperty(), 0),
                        new KeyValue(playPauseCorner2.yProperty(), 22),

                        new KeyValue(playPauseCorner3.xProperty(), 9.5),
                        new KeyValue(playPauseCorner3.yProperty(), 16.5),

                        new KeyValue(playPauseCorner4.xProperty(), 9.5),
                        new KeyValue(playPauseCorner4.yProperty(), 5.5),

                        new KeyValue(playPauseCorner5.xProperty(), 9),
                        new KeyValue(playPauseCorner5.yProperty(), 5.25),

                        new KeyValue(playPauseCorner6.xProperty(), 9),
                        new KeyValue(playPauseCorner6.yProperty(), 16.75),

                        new KeyValue(playPauseCorner7.xProperty(), 18),
                        new KeyValue(playPauseCorner7.yProperty(), 11),

                        new KeyValue(playPauseCorner8.xProperty(), 18),
                        new KeyValue(playPauseCorner8.yProperty(), 11))
        );

        playPauseTransition.play();
    }
}
