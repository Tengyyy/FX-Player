package hans;

import hans.Captions.CaptionsState;
import hans.Chapters.ChapterController;
import hans.Menu.MenuController;
import hans.Settings.SettingsController;
import hans.Settings.SettingsState;
import hans.Captions.CaptionsController;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
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
    Button captionsButton;
    @FXML
    Button previousVideoButton;
    @FXML
    public
    Button miniplayerButton;

    @FXML
    public Slider volumeSlider, durationSlider;

    @FXML
    public ProgressBar volumeTrack;

    @FXML
    public
    StackPane volumeSliderPane;
    @FXML
    StackPane previousVideoPane;
    @FXML
    StackPane playButtonPane;
    @FXML
    StackPane nextVideoPane;
    @FXML
    StackPane volumeButtonPane;
    @FXML
    StackPane captionsButtonPane;
    @FXML
    StackPane settingsButtonPane;
    @FXML
    StackPane miniplayerButtonPane;
    @FXML
    StackPane fullScreenButtonPane;
    @FXML
    StackPane durationPane;

    @FXML
    public
    Label durationLabel;

    @FXML
    public
    Line captionsButtonLine;

    @FXML
    public Region previousVideoIcon, playIcon, nextVideoIcon, volumeIcon, captionsIcon, settingsIcon, fullScreenIcon, miniplayerIcon;

    @FXML
    public
    HBox trackContainer;
    @FXML
    public HBox labelBox;

    public ArrayList<DurationTrack> durationTracks = new ArrayList<>();
    public DurationTrack defaultTrack = new DurationTrack(0 , 1);
    public DurationTrack hoverTrack = null;
    public DurationTrack activeTrack = null;

    SVGPath previousVideoSVG, playSVG, pauseSVG, replaySVG, nextVideoSVG, highVolumeSVG, lowVolumeSVG, volumeMutedSVG, captionsSVG, settingsSVG, maximizeSVG, minimizeSVG, miniplayerSVG;

    MainController mainController;
    SettingsController settingsController;
    MenuController menuController;
    CaptionsController captionsController;
    ChapterController chapterController;


    double volumeValue;

    boolean muted = false;
    boolean isExited = true;
    boolean showingTimeLeft = false;
    public boolean durationSliderHover = false;
    public boolean controlBarOpen = true;


    // variables to keep track of whether mouse is hovering any control button
    boolean previousVideoButtonHover = false;
    boolean playButtonHover = false;
    boolean nextVideoButtonHover = false;

    boolean volumeButtonHover = false;
    public boolean captionsButtonHover = false;
    public boolean settingsButtonHover = false;
    public boolean fullScreenButtonHover = false;
    public boolean miniplayerButtonHover = false;

    boolean previousVideoButtonEnabled = false;
    boolean playButtonEnabled = false;
    public boolean nextVideoButtonEnabled = false;



    public MouseEventTracker mouseEventTracker;

    ControlTooltip play;
    ControlTooltip mute;
    public ControlTooltip settings;
    public ControlTooltip fullScreen;
    public ControlTooltip captions;
    VideoTooltip nextVideoTooltip;
    VideoTooltip previousVideoTooltip;
    public ControlTooltip miniplayer;

    MediaInterface mediaInterface;

    ScaleTransition fullScreenButtonScaleTransition;

    PauseTransition seekTimer = new PauseTransition(Duration.millis(50));
    PauseTransition previewTimer = new PauseTransition(Duration.millis(200));

    StackPane controlBarBackground = new StackPane();


    public PauseTransition pauseTransition;

    double lastKnownSliderHoverPosition = -1000;

    double thumbScale = 0;

    Timeline volumeSliderAnimation = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        trackContainer.getChildren().add(defaultTrack.progressBar);

        Platform.runLater(() -> {
            mute = new ControlTooltip(mainController,"Mute (m)", volumeButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
            settings = new ControlTooltip(mainController,"Settings (s)", settingsButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
            fullScreen = new ControlTooltip(mainController,"Full screen (f)", fullScreenButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
            captions = new ControlTooltip(mainController,"Subtitles/closed captions (c)", captionsButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
            miniplayer = new ControlTooltip(mainController,"Miniplayer (i)", miniplayerButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
            previousVideoTooltip = new VideoTooltip(mainController, previousVideoButton, true);
            nextVideoTooltip = new VideoTooltip(mainController, nextVideoButton, false);
        });

        previousVideoSVG = new SVGPath();
        previousVideoSVG.setContent(App.svgMap.get(SVG.PREVIOUS_VIDEO));

        playSVG = new SVGPath();
        playSVG.setContent(App.svgMap.get(SVG.PLAY));

        pauseSVG = new SVGPath();
        pauseSVG.setContent(App.svgMap.get(SVG.PAUSE));

        replaySVG = new SVGPath();
        replaySVG.setContent(App.svgMap.get(SVG.REPLAY));

        nextVideoSVG = new SVGPath();
        nextVideoSVG.setContent(App.svgMap.get(SVG.NEXT_VIDEO));

        highVolumeSVG = new SVGPath();
        highVolumeSVG.setContent(App.svgMap.get(SVG.VOLUME_HIGH));

        lowVolumeSVG = new SVGPath();
        lowVolumeSVG.setContent(App.svgMap.get(SVG.VOLUME_LOW));

        volumeMutedSVG = new SVGPath();
        volumeMutedSVG.setContent(App.svgMap.get(SVG.VOLUME_MUTED));

        captionsSVG = new SVGPath();
        captionsSVG.setContent(App.svgMap.get(SVG.CAPTIONS));

        settingsSVG = new SVGPath();
        settingsSVG.setContent(App.svgMap.get(SVG.SETTINGS));

        maximizeSVG = new SVGPath();
        maximizeSVG.setContent(App.svgMap.get(SVG.MAXIMIZE));

        minimizeSVG = new SVGPath();
        minimizeSVG.setContent(App.svgMap.get(SVG.MINIMIZE));

        miniplayerSVG = new SVGPath();
        miniplayerSVG.setContent(App.svgMap.get(SVG.MINIPLAYER));

        volumeSliderPane.setClip(new Rectangle(0, 30));

        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(volumeSliderPane.widthProperty());
        clip.heightProperty().bind(volumeSliderPane.heightProperty());

        volumeSliderPane.setClip(clip);

        Rectangle labelClip = new Rectangle();
        labelClip.widthProperty().bind(labelBox.widthProperty().subtract(10));
        labelClip.heightProperty().bind(labelBox.heightProperty());
        labelBox.setClip(labelClip);


        durationLabel.setOnMouseClicked((e) -> toggleDurationLabel());
        durationLabel.setOnMouseEntered(e -> AnimationsClass.animateTextColor(durationLabel, Color.rgb(255, 255, 255), 200));
        durationLabel.setOnMouseExited(e -> AnimationsClass.animateTextColor(durationLabel, Color.rgb(200, 200, 200), 200));

        durationPane.setMouseTransparent(true);

        controlBarBackground.setStyle("-fx-background-color: linear-gradient(to top, rgba(0,0,0,0.8), rgba(0,0,0,0));");
        controlBarBackground.setMouseTransparent(true);
        controlBarBackground.setPrefHeight(200);
        controlBarBackground.setMaxHeight(200);
        StackPane.setAlignment(controlBarBackground, Pos.BOTTOM_CENTER);

        previousVideoIcon.setShape(previousVideoSVG);
        playIcon.setShape(playSVG);
        nextVideoIcon.setShape(nextVideoSVG);
        volumeIcon.setShape(lowVolumeSVG);

        volumeIcon.setPrefSize(15, 18);
        volumeIcon.setTranslateX(-3);

        captionsIcon.setShape(captionsSVG);
        settingsIcon.setShape(settingsSVG);

        miniplayerIcon.setShape(miniplayerSVG);
        fullScreenIcon.setShape(maximizeSVG);

        playButton.setBackground(Background.EMPTY);
        nextVideoButton.setBackground(Background.EMPTY);
        miniplayerButton.setBackground(Background.EMPTY);
        fullScreenButton.setBackground(Background.EMPTY);
        settingsButton.setBackground(Background.EMPTY);
        volumeButton.setBackground(Background.EMPTY);
        captionsButton.setBackground(Background.EMPTY);

        previousVideoButton.setOnAction(e -> previousVideoButtonClick());
        playButton.setOnAction((e) -> playButtonClick());
        nextVideoButton.setOnAction(e -> nextVideoButtonClick());
        volumeButton.setOnAction(e -> volumeButtonClick());
        captionsButton.setOnAction(e -> captionsButtonClick());
        settingsButton.setOnAction(e -> settingsButtonClick());
        miniplayerButton.setOnAction(e -> miniplayerButtonClick());
        fullScreenButton.setOnAction(e -> fullScreenButtonClick());


        volumeSlider.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> volumeSlider.setValueChanging(true));
        volumeSlider.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> volumeSlider.setValueChanging(false));


        previousVideoPane.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> previousVideoButtonHoverOn());
        previousVideoPane.addEventHandler(MouseEvent.MOUSE_EXITED, e -> previousVideoButtonHoverOff());

        playButtonPane.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> playButtonHoverOn());
        playButtonPane.addEventHandler(MouseEvent.MOUSE_EXITED, e -> playButtonHoverOff());

        nextVideoPane.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> nextVideoButtonHoverOn());
        nextVideoPane.addEventHandler(MouseEvent.MOUSE_EXITED, e -> nextVideoButtonHoverOff());

        volumeButtonPane.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> controlButtonHoverOn(volumeButtonPane));
        volumeButtonPane.addEventHandler(MouseEvent.MOUSE_EXITED, e -> controlButtonHoverOff(volumeButtonPane));

        captionsButtonPane.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> controlButtonHoverOn(captionsButtonPane));
        captionsButtonPane.addEventHandler(MouseEvent.MOUSE_EXITED, e -> controlButtonHoverOff(captionsButtonPane));

        settingsButtonPane.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> controlButtonHoverOn(settingsButtonPane));
        settingsButtonPane.addEventHandler(MouseEvent.MOUSE_EXITED, e -> controlButtonHoverOff(settingsButtonPane));

        miniplayerButtonPane.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> controlButtonHoverOn(miniplayerButtonPane));
        miniplayerButtonPane.addEventHandler(MouseEvent.MOUSE_EXITED, e -> controlButtonHoverOff(miniplayerButtonPane));

        fullScreenButtonPane.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> controlButtonHoverOn(fullScreenButtonPane));
        fullScreenButtonPane.addEventHandler(MouseEvent.MOUSE_EXITED, e -> controlButtonHoverOff(fullScreenButtonPane));

        Platform.runLater(() -> {
            disablePreviousVideoButton();
            disablePlayButton();
            disableNextVideoButton();
        });


        volumeSlider.valueChangingProperty().addListener((observable, oldValue, newValue) -> {

            if (!newValue) {
                if (settingsController.settingsState != SettingsState.CLOSED) settingsController.closeSettings();
                if (captionsController.captionsState != CaptionsState.CLOSED) captionsController.closeCaptions();

                if (isExited) volumeSliderExit();
            }
        });


        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {

            mediaInterface.changeVolume(newValue.doubleValue());
            volumeTrack.setProgress(volumeSlider.getValue() / 100);

            if (newValue.doubleValue() == 0) {
                volumeIcon.setShape(volumeMutedSVG);
                volumeIcon.setPrefSize(20, 20);
                volumeIcon.setTranslateX(0);
                muted = true;
                mute.updateText("Unmute (m)");
            } else if (newValue.doubleValue() < 50) {
                volumeIcon.setShape(lowVolumeSVG);
                volumeIcon.setPrefSize(15, 18);
                volumeIcon.setTranslateX(-3);
                muted = false;
                mute.updateText("Mute (m)");
            } else {
                volumeIcon.setShape(highVolumeSVG);
                volumeIcon.setPrefSize(20, 20);
                volumeIcon.setTranslateX(0);
                muted = false;
                mute.updateText("Mute (m)");
            }
        });

        previewTimer.setOnFinished(e -> {

            if(pauseTransition != null && pauseTransition.getStatus() == Animation.Status.RUNNING) pauseTransition.stop();

            if(mainController.sliderHoverPreview.pane.isVisible() || (mainController.miniplayerActive && mainController.miniplayer.miniplayerController.seekImageView.isVisible())){
                if(mainController.miniplayerActive && mainController.miniplayer.miniplayerController.slider.isValueChanging()) mediaInterface.updatePreviewFrame(mainController.miniplayer.miniplayerController.slider.getValue()/mainController.miniplayer.miniplayerController.slider.getMax(), true);
                else mediaInterface.updatePreviewFrame(lastKnownSliderHoverPosition, true);
            }
        });

        durationSlider.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if (settingsController.settingsState != SettingsState.CLOSED) {
                settingsController.closeSettings();
            }

            if (captionsController.captionsState != CaptionsState.CLOSED) {
                captionsController.closeCaptions();
            }


        });


        // this part has to be run later because the slider thumb loads in later than the slider itself
        Platform.runLater(() -> {

            durationSlider.lookup(".thumb").setScaleX(0);
            durationSlider.lookup(".thumb").setScaleY(0);

            durationSlider.lookup(".thumb").setMouseTransparent(true);

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

                String newTime = Utilities.getTime(Duration.seconds((e.getX()) / (durationSlider.lookup(".track").getBoundsInLocal().getMaxX()) * durationSlider.getMax()));
                mainController.sliderHoverLabel.timeLabel.setText(newTime);

                double offset = 0;
                if(menuController.queueBox.activeItem.get() != null && menuController.queueBox.activeItem.get().getMediaItem() != null &&  menuController.queueBox.activeItem.get().getMediaItem().hasVideo()) offset = (mainController.sliderHoverPreview.pane.getLayoutBounds().getMaxX() - mainController.sliderHoverLabel.timeLabel.getLayoutBounds().getMaxX())/2;

                double timeLabelMinTranslation = (mainController.sliderHoverLabel.timeLabel.localToScene(mainController.sliderHoverLabel.timeLabel.getBoundsInLocal()).getMinX() - mainController.sliderHoverLabel.timeLabel.getTranslateX() - durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMinX()) * -1 + offset - 5;
                double timeLabelMaxTranslation = durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMaxX() - mainController.sliderHoverLabel.timeLabel.localToScene(mainController.sliderHoverLabel.timeLabel.getBoundsInLocal()).getMaxX() + mainController.sliderHoverLabel.timeLabel.getTranslateX() - offset + 13;

                double timeLabelNewTranslation = Math.max(timeLabelMinTranslation, Math.min(timeLabelMaxTranslation, e.getSceneX() - (mainController.sliderHoverLabel.timeLabel.localToScene(mainController.sliderHoverLabel.timeLabel.getBoundsInLocal()).getMinX() + mainController.sliderHoverLabel.timeLabel.getBoundsInLocal().getMaxX() / 2) + mainController.sliderHoverLabel.timeLabel.getTranslateX() - 4));

                mainController.sliderHoverLabel.timeLabel.setTranslateX(timeLabelNewTranslation);

                double chapterOffset = 0;
                if(menuController.queueBox.activeItem.get() != null && menuController.queueBox.activeItem.get().getMediaItem() != null &&  menuController.queueBox.activeItem.get().getMediaItem().hasVideo() && mainController.sliderHoverLabel.chapterlabel.getLayoutBounds().getMaxX() < mainController.sliderHoverPreview.pane.getLayoutBounds().getMaxX()) chapterOffset = (mainController.sliderHoverPreview.pane.getLayoutBounds().getMaxX() - mainController.sliderHoverLabel.chapterlabel.getLayoutBounds().getMaxX())/2;


                double chapterLabelMinTranslation = (mainController.sliderHoverLabel.chapterlabel.localToScene(mainController.sliderHoverLabel.chapterlabel.getBoundsInLocal()).getMinX() - mainController.sliderHoverLabel.chapterlabel.getTranslateX() - durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMinX()) * -1 + chapterOffset - 5;
                double chapterLabelMaxTranslation = durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMaxX() - mainController.sliderHoverLabel.chapterlabel.localToScene(mainController.sliderHoverLabel.chapterlabel.getBoundsInLocal()).getMaxX() + mainController.sliderHoverLabel.chapterlabel.getTranslateX() - chapterOffset + 13;

                double chapterLabelNewTranslation = Math.max(chapterLabelMinTranslation, Math.min(chapterLabelMaxTranslation, e.getSceneX() - (mainController.sliderHoverLabel.chapterlabel.localToScene(mainController.sliderHoverLabel.chapterlabel.getBoundsInLocal()).getMinX() + mainController.sliderHoverLabel.chapterlabel.getBoundsInLocal().getMaxX() / 2) + mainController.sliderHoverLabel.chapterlabel.getTranslateX() - 4));

                mainController.sliderHoverLabel.chapterlabel.setTranslateX(chapterLabelNewTranslation);


                if (settingsController.settingsState == SettingsState.CLOSED && captionsController.captionsState == CaptionsState.CLOSED) {
                    mainController.sliderHoverLabel.timeLabel.setVisible(true);
                    if (menuController.queueBox.activeItem.get() != null && menuController.queueBox.activeItem.get().getMediaItem() != null && menuController.queueBox.activeItem.get().getMediaItem().hasVideo()) mainController.sliderHoverPreview.pane.setVisible(true);

                }

                double paneMinTranslation = (mainController.sliderHoverPreview.pane.localToScene(mainController.sliderHoverPreview.pane.getBoundsInLocal()).getMinX() - mainController.sliderHoverPreview.pane.getTranslateX() - durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMinX()) * -1;
                double paneMaxTranslation = durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMaxX() - mainController.sliderHoverPreview.pane.localToScene(mainController.sliderHoverPreview.pane.getBoundsInLocal()).getMaxX() + mainController.sliderHoverPreview.pane.getTranslateX();

                double paneNewTranslation = Math.max(paneMinTranslation, Math.min(paneMaxTranslation, e.getSceneX() - (mainController.sliderHoverPreview.pane.localToScene(mainController.sliderHoverPreview.pane.getBoundsInLocal()).getMinX() + mainController.sliderHoverPreview.pane.getBoundsInLocal().getMaxX() / 2) + mainController.sliderHoverPreview.pane.getTranslateX() - 4));

                mainController.sliderHoverPreview.pane.setTranslateX(paneNewTranslation);


                if(menuController.queueBox.activeItem.get() != null && menuController.queueBox.activeItem.get().getMediaItem() != null && menuController.queueBox.activeItem.get().getMediaItem().hasVideo()){

                    if(pauseTransition != null && pauseTransition.getStatus() == Animation.Status.RUNNING) return;

                    pauseTransition = new PauseTransition(Duration.millis(50));
                    pauseTransition.setOnFinished(j -> mediaInterface.updatePreviewFrame(lastKnownSliderHoverPosition, false));

                    pauseTransition.playFromStart();
                }

            });

            durationSlider.lookup(".track").setOnMouseEntered((e) -> {

                previewTimer.playFromStart();

                durationSliderHover = true;
                durationSliderHoverOn(e.getX()/durationSlider.lookup(".track").getBoundsInLocal().getMaxX());

                String newTime = Utilities.getTime(Duration.seconds(e.getX() / (durationSlider.lookup(".track").getBoundsInLocal().getMaxX()) * durationSlider.getMax()));
                mainController.sliderHoverLabel.timeLabel.setText(newTime);

                double offset = 0;
                if(menuController.queueBox.activeItem.get() != null && menuController.queueBox.activeItem.get().getMediaItem() != null && menuController.queueBox.activeItem.get().getMediaItem().hasVideo()) offset = (mainController.sliderHoverPreview.pane.getLayoutBounds().getMaxX() - mainController.sliderHoverLabel.timeLabel.getLayoutBounds().getMaxX())/2;

                double timeLabelMinTranslation = (mainController.sliderHoverLabel.timeLabel.localToScene(mainController.sliderHoverLabel.timeLabel.getBoundsInLocal()).getMinX() - mainController.sliderHoverLabel.timeLabel.getTranslateX() - durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMinX()) * -1 + offset - 5;
                double timeLabelMaxTranslation = durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMaxX() - mainController.sliderHoverLabel.timeLabel.localToScene(mainController.sliderHoverLabel.timeLabel.getBoundsInLocal()).getMaxX() + mainController.sliderHoverLabel.timeLabel.getTranslateX() - offset + 13;

                double timeLabelNewTranslation = Math.max(timeLabelMinTranslation, Math.min(timeLabelMaxTranslation, e.getSceneX() - (mainController.sliderHoverLabel.timeLabel.localToScene(mainController.sliderHoverLabel.timeLabel.getBoundsInLocal()).getMinX() + mainController.sliderHoverLabel.timeLabel.getBoundsInLocal().getMaxX() / 2) + mainController.sliderHoverLabel.timeLabel.getTranslateX() - 4));

                mainController.sliderHoverLabel.timeLabel.setTranslateX(timeLabelNewTranslation);

                double chapterOffset = 0;
                if(menuController.queueBox.activeItem.get() != null && menuController.queueBox.activeItem.get().getMediaItem() != null &&  menuController.queueBox.activeItem.get().getMediaItem().hasVideo() && mainController.sliderHoverLabel.chapterlabel.getLayoutBounds().getMaxX() < mainController.sliderHoverPreview.pane.getLayoutBounds().getMaxX()) chapterOffset = (mainController.sliderHoverPreview.pane.getLayoutBounds().getMaxX() - mainController.sliderHoverLabel.chapterlabel.getLayoutBounds().getMaxX())/2;


                double chapterLabelMinTranslation = (mainController.sliderHoverLabel.chapterlabel.localToScene(mainController.sliderHoverLabel.chapterlabel.getBoundsInLocal()).getMinX() - mainController.sliderHoverLabel.chapterlabel.getTranslateX() - durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMinX()) * -1 + chapterOffset - 5;
                double chapterLabelMaxTranslation = durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMaxX() - mainController.sliderHoverLabel.chapterlabel.localToScene(mainController.sliderHoverLabel.chapterlabel.getBoundsInLocal()).getMaxX() + mainController.sliderHoverLabel.chapterlabel.getTranslateX() - chapterOffset + 13;

                double chapterLabelNewTranslation = Math.max(chapterLabelMinTranslation, Math.min(chapterLabelMaxTranslation, e.getSceneX() - (mainController.sliderHoverLabel.chapterlabel.localToScene(mainController.sliderHoverLabel.chapterlabel.getBoundsInLocal()).getMinX() + mainController.sliderHoverLabel.chapterlabel.getBoundsInLocal().getMaxX() / 2) + mainController.sliderHoverLabel.chapterlabel.getTranslateX() - 4));

                mainController.sliderHoverLabel.chapterlabel.setTranslateX(chapterLabelNewTranslation);

                double paneMinTranslation = (mainController.sliderHoverPreview.pane.localToScene(mainController.sliderHoverPreview.pane.getBoundsInLocal()).getMinX() - mainController.sliderHoverPreview.pane.getTranslateX() - durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMinX()) * -1;
                double paneMaxTranslation = durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMaxX() - mainController.sliderHoverPreview.pane.localToScene(mainController.sliderHoverPreview.pane.getBoundsInLocal()).getMaxX() + mainController.sliderHoverPreview.pane.getTranslateX();

                double paneNewTranslation = Math.max(paneMinTranslation, Math.min(paneMaxTranslation, e.getSceneX() - (mainController.sliderHoverPreview.pane.localToScene(mainController.sliderHoverPreview.pane.getBoundsInLocal()).getMinX() + mainController.sliderHoverPreview.pane.getBoundsInLocal().getMaxX() / 2) + mainController.sliderHoverPreview.pane.getTranslateX() - 4));

                mainController.sliderHoverPreview.pane.setTranslateX(paneNewTranslation);


                if (settingsController.settingsState == SettingsState.CLOSED && captionsController.captionsState == CaptionsState.CLOSED) {
                    mainController.sliderHoverLabel.timeLabel.setVisible(true);
                    if(chapterController.activeChapter != -1) mainController.sliderHoverLabel.chapterlabel.setVisible(true);
                    if (menuController.queueBox.activeItem.get() != null && menuController.queueBox.activeItem.get().getMediaItem() != null && menuController.queueBox.activeItem.get().getMediaItem().hasVideo()) mainController.sliderHoverPreview.pane.setVisible(true);
                }


                if(menuController.queueBox.activeItem.get() != null && menuController.queueBox.activeItem.get().getMediaItem() != null && menuController.queueBox.activeItem.get().getMediaItem().hasVideo()){
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
                    mainController.sliderHoverLabel.timeLabel.setVisible(false);
                    mainController.sliderHoverLabel.chapterlabel.setVisible(false);
                    mainController.sliderHoverLabel.chapterlabel.setText("");
                    mainController.sliderHoverPreview.pane.setVisible(false);
                    mainController.sliderHoverPreview.setImage(null);
                }
            });




            durationSlider.lookup(".track").addEventFilter(MouseEvent.MOUSE_DRAGGED, e -> {

                if (e.isPrimaryButtonDown()) return;

                previewTimer.playFromStart();

                updateSliderHover(e.getX()/durationSlider.lookup(".track").getBoundsInLocal().getMaxX());


                e.consume();

                String newTime = Utilities.getTime(Duration.seconds(e.getX() / (durationSlider.lookup(".track").getBoundsInLocal().getMaxX()) * durationSlider.getMax()));
                mainController.sliderHoverLabel.timeLabel.setText(newTime);

                double offset = 0;
                if(menuController.queueBox.activeItem.get() != null && menuController.queueBox.activeItem.get().getMediaItem() != null && menuController.queueBox.activeItem.get().getMediaItem().hasVideo()) offset = (mainController.sliderHoverPreview.pane.getLayoutBounds().getMaxX() - mainController.sliderHoverLabel.timeLabel.getLayoutBounds().getMaxX())/2;

                double timeLabelMinTranslation = (mainController.sliderHoverLabel.timeLabel.localToScene(mainController.sliderHoverLabel.timeLabel.getBoundsInLocal()).getMinX() - mainController.sliderHoverLabel.timeLabel.getTranslateX() - durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMinX()) * -1 + offset - 5;
                double timeLabelMaxTranslation = durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMaxX() - mainController.sliderHoverLabel.timeLabel.localToScene(mainController.sliderHoverLabel.timeLabel.getBoundsInLocal()).getMaxX() + mainController.sliderHoverLabel.timeLabel.getTranslateX() - offset + 13;

                double timeLabelNewTranslation = Math.max(timeLabelMinTranslation, Math.min(timeLabelMaxTranslation, e.getSceneX() - (mainController.sliderHoverLabel.timeLabel.localToScene(mainController.sliderHoverLabel.timeLabel.getBoundsInLocal()).getMinX() + mainController.sliderHoverLabel.timeLabel.getBoundsInLocal().getMaxX() / 2) + mainController.sliderHoverLabel.timeLabel.getTranslateX() - 4));

                mainController.sliderHoverLabel.timeLabel.setTranslateX(timeLabelNewTranslation);

                double chapterOffset = 0;
                if(menuController.queueBox.activeItem.get() != null && menuController.queueBox.activeItem.get().getMediaItem() != null &&  menuController.queueBox.activeItem.get().getMediaItem().hasVideo() && mainController.sliderHoverLabel.chapterlabel.getLayoutBounds().getMaxX() < mainController.sliderHoverPreview.pane.getLayoutBounds().getMaxX()) chapterOffset = (mainController.sliderHoverPreview.pane.getLayoutBounds().getMaxX() - mainController.sliderHoverLabel.chapterlabel.getLayoutBounds().getMaxX())/2;


                double chapterLabelMinTranslation = (mainController.sliderHoverLabel.chapterlabel.localToScene(mainController.sliderHoverLabel.chapterlabel.getBoundsInLocal()).getMinX() - mainController.sliderHoverLabel.chapterlabel.getTranslateX() - durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMinX()) * -1 + chapterOffset - 5;
                double chapterLabelMaxTranslation = durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMaxX() - mainController.sliderHoverLabel.chapterlabel.localToScene(mainController.sliderHoverLabel.chapterlabel.getBoundsInLocal()).getMaxX() + mainController.sliderHoverLabel.chapterlabel.getTranslateX() - chapterOffset + 13;

                double chapterLabelNewTranslation = Math.max(chapterLabelMinTranslation, Math.min(chapterLabelMaxTranslation, e.getSceneX() - (mainController.sliderHoverLabel.chapterlabel.localToScene(mainController.sliderHoverLabel.chapterlabel.getBoundsInLocal()).getMinX() + mainController.sliderHoverLabel.chapterlabel.getBoundsInLocal().getMaxX() / 2) + mainController.sliderHoverLabel.chapterlabel.getTranslateX() - 4));

                mainController.sliderHoverLabel.chapterlabel.setTranslateX(chapterLabelNewTranslation);

                double paneMinTranslation = (mainController.sliderHoverPreview.pane.localToScene(mainController.sliderHoverPreview.pane.getBoundsInLocal()).getMinX() - mainController.sliderHoverPreview.pane.getTranslateX() - durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMinX()) * -1;
                double paneMaxTranslation = durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMaxX() - mainController.sliderHoverPreview.pane.localToScene(mainController.sliderHoverPreview.pane.getBoundsInLocal()).getMaxX() + mainController.sliderHoverPreview.pane.getTranslateX();

                double paneNewTranslation = Math.max(paneMinTranslation, Math.min(paneMaxTranslation, e.getSceneX() - (mainController.sliderHoverPreview.pane.localToScene(mainController.sliderHoverPreview.pane.getBoundsInLocal()).getMinX() + mainController.sliderHoverPreview.pane.getBoundsInLocal().getMaxX() / 2) + mainController.sliderHoverPreview.pane.getTranslateX() - 4));

                mainController.sliderHoverPreview.pane.setTranslateX(paneNewTranslation);


                if(menuController.queueBox.activeItem.get() != null && menuController.queueBox.activeItem.get().getMediaItem() != null && menuController.queueBox.activeItem.get().getMediaItem().hasVideo()){
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

                    if (mainController.miniplayerActive)
                        mainController.miniplayer.miniplayerController.previousVideoButtonTooltip.updateText("Replay");
                }
            }
            else if (oldValue.doubleValue() > 5 && newValue.doubleValue() <= 5 && mediaInterface.mediaActive.get()) {

                if (menuController.queueBox.activeItem.get() == null || menuController.queueBox.activeIndex.get() == 0) {
                    disablePreviousVideoButton();
                } else {
                    if (mainController.miniplayerActive) mainController.miniplayer.miniplayerController.previousVideoButtonTooltip.updateText("Previous video (SHIFT + P)");

                    previousVideoTooltip.updateTooltip(menuController.queueBox.queue.get(menuController.queueBox.queueOrder.get(menuController.queueBox.activeIndex.get() - 1)));
                }

            }

            captionsController.updateCaptions(newValue.doubleValue() * 1000);


            if (durationSlider.isValueChanging()) {

                previewTimer.playFromStart();

                updateSliderHover(Math.min(1, Math.max(0, newValue.doubleValue() / durationSlider.getMax())));

                mainController.sliderHoverLabel.timeLabel.setText(Utilities.getTime(Duration.seconds(durationSlider.getValue())));

                double offset = 0;
                if(menuController.queueBox.activeItem.get() != null && menuController.queueBox.activeItem.get().getMediaItem() != null && menuController.queueBox.activeItem.get().getMediaItem().hasVideo()) offset = (mainController.sliderHoverPreview.pane.getLayoutBounds().getMaxX() - mainController.sliderHoverLabel.timeLabel.getLayoutBounds().getMaxX())/2;

                double timeLabelMinTranslation = (mainController.sliderHoverLabel.timeLabel.localToScene(mainController.sliderHoverLabel.timeLabel.getBoundsInLocal()).getMinX() - mainController.sliderHoverLabel.timeLabel.getTranslateX() - durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMinX()) * -1 + offset - 5;
                double timeLabelMaxTranslation = durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMaxX() - mainController.sliderHoverLabel.timeLabel.localToScene(mainController.sliderHoverLabel.timeLabel.getBoundsInLocal()).getMaxX() + mainController.sliderHoverLabel.timeLabel.getTranslateX() - offset + 13;

                double timeLabelNewTranslation = Math.max(timeLabelMinTranslation, Math.min(timeLabelMaxTranslation, durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMinX() + durationSlider.lookup(".track").getBoundsInLocal().getMaxX() * (newValue.doubleValue() / durationSlider.getMax()) - (mainController.sliderHoverLabel.timeLabel.localToScene(mainController.sliderHoverLabel.timeLabel.getBoundsInLocal()).getMinX() + mainController.sliderHoverLabel.timeLabel.getBoundsInLocal().getMaxX() / 2) + mainController.sliderHoverLabel.timeLabel.getTranslateX() - 4));

                mainController.sliderHoverLabel.timeLabel.setTranslateX(timeLabelNewTranslation);


                double chapterOffset = 0;
                if(menuController.queueBox.activeItem.get() != null && menuController.queueBox.activeItem.get().getMediaItem() != null && menuController.queueBox.activeItem.get().getMediaItem().hasVideo() && mainController.sliderHoverLabel.chapterlabel.getLayoutBounds().getMaxX() < mainController.sliderHoverPreview.pane.getLayoutBounds().getMaxX()) chapterOffset = (mainController.sliderHoverPreview.pane.getLayoutBounds().getMaxX() - mainController.sliderHoverLabel.chapterlabel.getLayoutBounds().getMaxX())/2;

                double chapterLabelMinTranslation = (mainController.sliderHoverLabel.chapterlabel.localToScene(mainController.sliderHoverLabel.chapterlabel.getBoundsInLocal()).getMinX() - mainController.sliderHoverLabel.chapterlabel.getTranslateX() - durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMinX()) * -1 + chapterOffset - 5;
                double chapterLabelMaxTranslation = durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMaxX() - mainController.sliderHoverLabel.chapterlabel.localToScene(mainController.sliderHoverLabel.chapterlabel.getBoundsInLocal()).getMaxX() + mainController.sliderHoverLabel.chapterlabel.getTranslateX() - chapterOffset + 13;

                double chapterLabelNewTranslation = Math.max(chapterLabelMinTranslation, Math.min(chapterLabelMaxTranslation, durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMinX() + durationSlider.lookup(".track").getBoundsInLocal().getMaxX() * (newValue.doubleValue() / durationSlider.getMax()) - (mainController.sliderHoverLabel.chapterlabel.localToScene(mainController.sliderHoverLabel.chapterlabel.getBoundsInLocal()).getMinX() + mainController.sliderHoverLabel.chapterlabel.getBoundsInLocal().getMaxX() / 2) + mainController.sliderHoverLabel.chapterlabel.getTranslateX() - 4));

                mainController.sliderHoverLabel.chapterlabel.setTranslateX(chapterLabelNewTranslation);


                double paneMinTranslation = (mainController.sliderHoverPreview.pane.localToScene(mainController.sliderHoverPreview.pane.getBoundsInLocal()).getMinX() - mainController.sliderHoverPreview.pane.getTranslateX() - durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMinX()) * -1;
                double paneMaxTranslation = durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMaxX() - mainController.sliderHoverPreview.pane.localToScene(mainController.sliderHoverPreview.pane.getBoundsInLocal()).getMaxX() + mainController.sliderHoverPreview.pane.getTranslateX();

                double paneNewTranslation = Math.max(paneMinTranslation, Math.min(paneMaxTranslation, durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMinX() + durationSlider.lookup(".track").getBoundsInLocal().getMaxX() * (newValue.doubleValue() / durationSlider.getMax()) - (mainController.sliderHoverPreview.pane.localToScene(mainController.sliderHoverPreview.pane.getBoundsInLocal()).getMinX() + mainController.sliderHoverPreview.pane.getBoundsInLocal().getMaxX() / 2) + mainController.sliderHoverPreview.pane.getTranslateX() - 4));

                mainController.sliderHoverPreview.pane.setTranslateX(paneNewTranslation);

                if(menuController.queueBox.activeItem.get() != null && menuController.queueBox.activeItem.get().getMediaItem() != null && menuController.queueBox.activeItem.get().getMediaItem().hasVideo()){
                    if(pauseTransition != null && pauseTransition.getStatus() == Animation.Status.RUNNING) return;

                    pauseTransition = new PauseTransition(Duration.millis(50));
                    pauseTransition.setOnFinished(e -> mediaInterface.updatePreviewFrame(lastKnownSliderHoverPosition, false));

                    pauseTransition.playFromStart();
                }

            }

        });

        durationSlider.valueChangingProperty().addListener((observable, oldValue, newValue) -> {


            if (newValue) { // pause video when user starts seeking

                if(menuController.queueBox.activeItem.get() != null && menuController.queueBox.activeItem.get().getMediaItem() != null && menuController.queueBox.activeItem.get().getMediaItem().hasVideo()){
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

                mainController.sliderHoverLabel.timeLabel.setText(Utilities.getTime(Duration.seconds(durationSlider.getValue())));

                double offset = 0;
                if(menuController.queueBox.activeItem.get() != null && menuController.queueBox.activeItem.get().getMediaItem() != null && menuController.queueBox.activeItem.get().getMediaItem().hasVideo()) offset = (mainController.sliderHoverPreview.pane.getLayoutBounds().getMaxX() - mainController.sliderHoverLabel.timeLabel.getLayoutBounds().getMaxX())/2;

                double labelMinTranslation = (mainController.sliderHoverLabel.timeLabel.localToScene(mainController.sliderHoverLabel.timeLabel.getBoundsInLocal()).getMinX() - mainController.sliderHoverLabel.timeLabel.getTranslateX() - durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMinX()) * -1 + offset - 5;
                double labelMaxTranslation = durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMaxX() - mainController.sliderHoverLabel.timeLabel.localToScene(mainController.sliderHoverLabel.timeLabel.getBoundsInLocal()).getMaxX() + mainController.sliderHoverLabel.timeLabel.getTranslateX() - offset + 13;

                double labelNewTranslation = Math.max(labelMinTranslation, Math.min(labelMaxTranslation, durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMinX() + durationSlider.lookup(".track").getBoundsInLocal().getMaxX() * (durationSlider.getValue() / durationSlider.getMax()) - (mainController.sliderHoverLabel.timeLabel.localToScene(mainController.sliderHoverLabel.timeLabel.getBoundsInLocal()).getMinX() + mainController.sliderHoverLabel.timeLabel.getBoundsInLocal().getMaxX() / 2) + mainController.sliderHoverLabel.timeLabel.getTranslateX() - 4));

                mainController.sliderHoverLabel.timeLabel.setTranslateX(labelNewTranslation);


                double chapterOffset = 0;
                if(menuController.queueBox.activeItem.get() != null && menuController.queueBox.activeItem.get().getMediaItem() != null && menuController.queueBox.activeItem.get().getMediaItem().hasVideo() && mainController.sliderHoverLabel.chapterlabel.getLayoutBounds().getMaxX() < mainController.sliderHoverPreview.pane.getLayoutBounds().getMaxX()) chapterOffset = (mainController.sliderHoverPreview.pane.getLayoutBounds().getMaxX() - mainController.sliderHoverLabel.chapterlabel.getLayoutBounds().getMaxX())/2;

                double chapterLabelMinTranslation = (mainController.sliderHoverLabel.chapterlabel.localToScene(mainController.sliderHoverLabel.chapterlabel.getBoundsInLocal()).getMinX() - mainController.sliderHoverLabel.chapterlabel.getTranslateX() - durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMinX()) * -1 + chapterOffset - 5;
                double chapterLabelMaxTranslation = durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMaxX() - mainController.sliderHoverLabel.chapterlabel.localToScene(mainController.sliderHoverLabel.chapterlabel.getBoundsInLocal()).getMaxX() + mainController.sliderHoverLabel.chapterlabel.getTranslateX() - chapterOffset + 13;

                double chapterLabelNewTranslation = Math.max(chapterLabelMinTranslation, Math.min(chapterLabelMaxTranslation, durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMinX() + durationSlider.lookup(".track").getBoundsInLocal().getMaxX() * (durationSlider.getValue() / durationSlider.getMax()) - (mainController.sliderHoverLabel.chapterlabel.localToScene(mainController.sliderHoverLabel.chapterlabel.getBoundsInLocal()).getMinX() + mainController.sliderHoverLabel.chapterlabel.getBoundsInLocal().getMaxX() / 2) + mainController.sliderHoverLabel.chapterlabel.getTranslateX() - 4));

                mainController.sliderHoverLabel.chapterlabel.setTranslateX(chapterLabelNewTranslation);


                double paneMinTranslation = (mainController.sliderHoverPreview.pane.localToScene(mainController.sliderHoverPreview.pane.getBoundsInLocal()).getMinX() - mainController.sliderHoverPreview.pane.getTranslateX() - durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMinX()) * -1;
                double paneMaxTranslation = durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMaxX() - mainController.sliderHoverPreview.pane.localToScene(mainController.sliderHoverPreview.pane.getBoundsInLocal()).getMaxX() + mainController.sliderHoverPreview.pane.getTranslateX();

                double paneNewTranslation = Math.max(paneMinTranslation, Math.min(paneMaxTranslation, durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMinX() + durationSlider.lookup(".track").getBoundsInLocal().getMaxX() * (durationSlider.getValue() / durationSlider.getMax()) - (mainController.sliderHoverPreview.pane.localToScene(mainController.sliderHoverPreview.pane.getBoundsInLocal()).getMinX() + mainController.sliderHoverPreview.pane.getBoundsInLocal().getMaxX() / 2) + mainController.sliderHoverPreview.pane.getTranslateX() - 4));

                mainController.sliderHoverPreview.pane.setTranslateX(paneNewTranslation);


                if (settingsController.settingsState == SettingsState.CLOSED && captionsController.captionsState == CaptionsState.CLOSED) {
                    mainController.sliderHoverLabel.timeLabel.setVisible(true);
                    if (menuController.queueBox.activeItem.get() != null && menuController.queueBox.activeItem.get().getMediaItem() != null && menuController.queueBox.activeItem.get().getMediaItem().hasVideo()) mainController.sliderHoverPreview.pane.setVisible(true);
                }


                if(menuController.queueBox.activeItem.get() != null && menuController.queueBox.activeItem.get().getMediaItem() != null && menuController.queueBox.activeItem.get().getMediaItem().hasVideo()){
                    if(pauseTransition != null && pauseTransition.getStatus() == Animation.Status.RUNNING) return;

                    pauseTransition = new PauseTransition(Duration.millis(50));
                    pauseTransition.setOnFinished(e -> mediaInterface.updatePreviewFrame(lastKnownSliderHoverPosition, false));

                    pauseTransition.playFromStart();
                }

            }
            else {

                mainController.sliderHoverPreview.pane.setVisible(false);
                mainController.sliderHoverLabel.timeLabel.setVisible(false);
                mainController.sliderHoverLabel.chapterlabel.setVisible(false);

                if(!durationSliderHover){
                    mainController.sliderHoverPreview.setImage(null);
                    mainController.sliderHoverLabel.chapterlabel.setText("");
                    durationSliderHoverOff(Math.min(1, Math.max(0, durationSlider.getValue() / durationSlider.getMax())));
                }


                if (seekTimer.getStatus() == Animation.Status.RUNNING) seekTimer.stop();
                if (mainController.miniplayerActive && mainController.miniplayer.miniplayerController.seekTimer.getStatus() == Animation.Status.RUNNING)
                    mainController.miniplayer.miniplayerController.seekTimer.stop();



                if (settingsController.settingsState != SettingsState.CLOSED) { // close settings pane after user finishes seeking media (if its open)
                    settingsController.closeSettings();
                }

                if (captionsController.captionsState != CaptionsState.CLOSED) {
                    captionsController.closeCaptions();
                }

                if (mediaInterface.atEnd) {
                    mediaInterface.defaultEnd();
                }
                else {
                    mediaInterface.seek(Duration.seconds(durationSlider.getValue())); // seeks to exact position when user finishes dragging
                    if (mediaInterface.wasPlaying) mediaInterface.play();
                }
            }
        });


        seekTimer.setOnFinished(e -> mediaInterface.pause());

    }

    public void init(MainController mainController, SettingsController settingsController, MenuController menuController, MediaInterface mediaInterface, CaptionsController captionsController, ChapterController chapterController) {
        this.mainController = mainController;
        this.settingsController = settingsController;
        this.menuController = menuController;
        this.mediaInterface = mediaInterface;
        this.captionsController = captionsController;
        this.chapterController = chapterController;

        mouseEventTracker = new MouseEventTracker(4, mainController, this, settingsController); // creates instance of the MouseEventTracker class which keeps track of when to hide and show the control-bar
    }

    public void toggleDurationLabel() {

        if (settingsController.settingsState != SettingsState.CLOSED) settingsController.closeSettings();
        if (captionsController.captionsState != CaptionsState.CLOSED) captionsController.closeCaptions();

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
        if (settingsController.settingsState != SettingsState.CLOSED) settingsController.closeSettings();
        if (captionsController.captionsState != CaptionsState.CLOSED) captionsController.closeCaptions();

        if (mediaInterface.atEnd) mediaInterface.replay();
        else if (mediaInterface.playing.get()) {
            mediaInterface.wasPlaying = false;
            mediaInterface.pause();
        } else mediaInterface.play();
    }


    public void play() {

        playIcon.setShape(pauseSVG);
        playIcon.setPrefSize(20, 20);

        if(play != null) play.updateText("Pause (k)");

        if(mainController.windowsTaskBarController != null) mainController.windowsTaskBarController.play();
    }

    public void pause() {

        playIcon.setShape(playSVG);
        playIcon.setPrefSize(20, 20);

        if (play != null) play.updateText("Play (k)");

        if(mainController.windowsTaskBarController != null) mainController.windowsTaskBarController.pause();
    }

    public void end() {
        playIcon.setShape(replaySVG);
        playIcon.setPrefSize(24, 24);

        if (play != null) play.updateText("Replay (k)");

        if(mainController.windowsTaskBarController != null) mainController.windowsTaskBarController.end();
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

        volumeSliderAnimation = new Timeline(
                new KeyFrame(Duration.millis(100),
                        new KeyValue(volumeSliderPane.prefWidthProperty(), 68, Interpolator.EASE_BOTH))
        );


        volumeSliderAnimation.play();
    }

    public void volumeSliderExit() {
        if(volumeSliderAnimation != null && volumeSliderAnimation.getStatus() == Animation.Status.RUNNING) volumeSliderAnimation.stop();

        volumeSliderAnimation = new Timeline(
                new KeyFrame(Duration.millis(100),
                        new KeyValue(volumeSliderPane.prefWidthProperty(), 0, Interpolator.EASE_BOTH))
        );

        volumeSliderAnimation.play();
    }

    public void toggleFullScreen() {

        captionsController.captionsBox.cancelDrag();
        App.stage.setFullScreen(!App.stage.isFullScreen());

        mainController.videoImageView.requestFocus();

        if (App.stage.isFullScreen()) {
            fullScreenIcon.setShape(minimizeSVG);
            App.fullScreen = true;

            if (settingsController.settingsState == SettingsState.CLOSED && captionsController.captionsState == CaptionsState.CLOSED) {
                if (fullScreen.isShowing()) {
                    fullScreen.hide();
                    fullScreen = new ControlTooltip(mainController,"Exit full screen (f)", fullScreenButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
                    fullScreen.showTooltip();
                } else {
                    fullScreen = new ControlTooltip(mainController,"Exit full screen (f)", fullScreenButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
                }
            }
        }
        else {
            fullScreenIcon.setShape(maximizeSVG);
            App.fullScreen = false;

            if (settingsController.settingsState == SettingsState.CLOSED && captionsController.captionsState == CaptionsState.CLOSED) {
                if (fullScreen.isShowing()) {
                    fullScreen.hide();
                    fullScreen = new ControlTooltip(mainController,"Full screen (f)", fullScreenButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
                    fullScreen.showTooltip();
                } else {
                    fullScreen = new ControlTooltip(mainController,"Full screen (f)", fullScreenButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
                }
            }
        }
    }

    public void fullScreenButtonHoverOn() {
        fullScreenButtonHover = true;
        fullScreenButtonScaleTransition = AnimationsClass.scaleAnimation(200, fullScreenIcon, 1, 1.3, 1, 1.3, true, 2, true);

    }

    public void fullScreenButtonHoverOff() {
        fullScreenButtonHover = false;
        if (fullScreenButtonScaleTransition != null && fullScreenButtonScaleTransition.getStatus() == Animation.Status.RUNNING)
            fullScreenButtonScaleTransition.stop();
        fullScreenIcon.setScaleX(1);
        fullScreenIcon.setScaleY(1);
    }

    public void miniplayerButtonHoverOn() {
        miniplayerButtonHover = true;
    }

    public void miniplayerButtonHoverOff() {
        miniplayerButtonHover = false;
    }


    public void volumeButtonClick() {
        if (settingsController.settingsState != SettingsState.CLOSED) settingsController.closeSettings();
        if (captionsController.captionsState != CaptionsState.CLOSED) captionsController.closeCaptions();

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
        if (settingsController.settingsState != SettingsState.CLOSED) settingsController.closeSettings();
        if (captionsController.captionsState != CaptionsState.CLOSED) captionsController.closeCaptions();

        if(durationSlider.getValue() > 5) mediaInterface.replay();
        else mediaInterface.playPrevious(); // reset styling of current active history item, decrement historyposition et
    }

    public void nextVideoButtonClick() {
        if (settingsController.settingsState != SettingsState.CLOSED) settingsController.closeSettings();
        if (captionsController.captionsState != CaptionsState.CLOSED) captionsController.closeCaptions();

        mediaInterface.playNext();
    }


    public void settingsButtonClick() {

        if (settingsController.settingsState != SettingsState.CLOSED) settingsController.closeSettings();
        else settingsController.openSettings();
    }


    public void fullScreenButtonClick() {
        if (settingsController.settingsState != SettingsState.CLOSED) settingsController.closeSettings();
        if (captionsController.captionsState != CaptionsState.CLOSED) captionsController.closeCaptions();

        toggleFullScreen();
    }

    public void miniplayerButtonClick() {

        if (settingsController.settingsState != SettingsState.CLOSED) settingsController.closeSettings();
        if (captionsController.captionsState != CaptionsState.CLOSED) captionsController.closeCaptions();

        if (mainController.miniplayerActive) mainController.closeMiniplayer();
        else mainController.openMiniplayer();
    }


    public void controlBarClick() {
        if (settingsController.settingsState != SettingsState.CLOSED) settingsController.closeSettings();
        if (captionsController.captionsState != CaptionsState.CLOSED) captionsController.closeCaptions();
    }

    public void captionsButtonClick() {


        if (captionsController.captionsState != CaptionsState.CLOSED) captionsController.closeCaptions();
        else captionsController.openCaptions();
    }

    public void durationSliderHoverOn(double value) {
        lastKnownSliderHoverPosition = value;

        if(durationSlider.isValueChanging()) return;
        ScaleTransition sliderThumbHoverOn = null;
        ScaleTransition sliderTrackHoverOn = null;
        if(durationTracks.isEmpty()){
            if(thumbScale != 1.1) {
                sliderThumbHoverOn = AnimationsClass.scaleAnimation(100, durationSlider.lookup(".thumb"), durationSlider.lookup(".thumb").getScaleX(), 1.1, durationSlider.lookup(".thumb").getScaleY(), 1.1, false, 1, false);
                thumbScale = 1.1;
            }
            sliderTrackHoverOn = AnimationsClass.scaleAnimation(100, defaultTrack.progressBar, 1, 1, defaultTrack.progressBar.getScaleY(), 1.8, false, 1, false);
        }
        else {
            for(DurationTrack durationTrack : durationTracks){
                if(durationTrack.startTime <= value && durationTrack.endTime >= value){
                    hoverTrack = durationTrack;
                    mainController.sliderHoverLabel.chapterlabel.setText(chapterController.chapterDescriptions.get(durationTracks.indexOf(durationTrack)).name());
                    if(durationSlider.getValue()/durationSlider.getMax() >= durationTrack.startTime && durationSlider.getValue()/durationSlider.getMax() <= durationTrack.endTime && !durationSlider.isValueChanging() && thumbScale != 1.25) {
                        sliderThumbHoverOn = AnimationsClass.scaleAnimation(100, durationSlider.lookup(".thumb"), durationSlider.lookup(".thumb").getScaleX(), 1.25, durationSlider.lookup(".thumb").getScaleY(), 1.25, false, 1, false);
                        thumbScale = 1.25;
                    }
                    sliderTrackHoverOn = AnimationsClass.scaleAnimation(100, durationTrack.progressBar, 1, 1, durationTrack.progressBar.getScaleY(), 3, false, 1 , false);
                    break;
                }
            }

            if(sliderThumbHoverOn == null && thumbScale != 0.9){
                sliderThumbHoverOn = AnimationsClass.scaleAnimation(100, durationSlider.lookup(".thumb"), durationSlider.lookup(".thumb").getScaleX(), 0.9, durationSlider.lookup(".thumb").getScaleY(), 0.9, false, 1, false);
                thumbScale = 0.9;
            }

        }
        AnimationsClass.parallelAnimation(true, sliderThumbHoverOn, sliderTrackHoverOn);
    }


    public void durationSliderHoverOff(double value) {
        if(durationSlider.isValueChanging()){
            lastKnownSliderHoverPosition = value;
            return;
        }

        List<Transition> sliderHoverTransitions = new ArrayList<>();
        sliderHoverTransitions.add(AnimationsClass.scaleAnimation(100, durationSlider.lookup(".thumb"), durationSlider.lookup(".thumb").getScaleX(), 0, durationSlider.lookup(".thumb").getScaleY(), 0, false, 1, false));
        thumbScale = 0;

        if(durationTracks.isEmpty()){
            sliderHoverTransitions.add(AnimationsClass.scaleAnimation(100, defaultTrack.progressBar, 1, 1, defaultTrack.progressBar.getScaleY(), 1, false, 1, false));
        }
        else {
            for(DurationTrack durationTrack : durationTracks){
                sliderHoverTransitions.add(AnimationsClass.scaleAnimation(100, durationTrack.progressBar, 1, 1, durationTrack.progressBar.getScaleY(), 1, false, 1 , false));
            }
        }

        AnimationsClass.parallelAnimation(true, sliderHoverTransitions);

        hoverTrack = null;
        lastKnownSliderHoverPosition = value;
    }

    public void updateSliderHover(double value){

        if(!durationTracks.isEmpty()){
            if(hoverTrack == null || (hoverTrack.startTime > value || hoverTrack.endTime < value)){

                ScaleTransition sliderThumbHover = null;
                ScaleTransition sliderTrackHoverOff = null;
                if(hoverTrack != null) sliderTrackHoverOff = AnimationsClass.scaleAnimation(100, hoverTrack.progressBar, 1, 1, hoverTrack.progressBar.getScaleY(), 1, false, 1 , false);
                ScaleTransition sliderTrackHoverOn = null;

                for(DurationTrack durationTrack : durationTracks){
                    if(durationTrack.startTime <= value && durationTrack.endTime >= value){
                        hoverTrack = durationTrack;
                        mainController.sliderHoverLabel.chapterlabel.setText(chapterController.chapterDescriptions.get(durationTracks.indexOf(durationTrack)).name());
                        if(durationSlider.getValue()/durationSlider.getMax() >= durationTrack.startTime && durationSlider.getValue()/durationSlider.getMax() <= durationTrack.endTime && !durationSlider.isValueChanging() && thumbScale != 1.25){
                            sliderThumbHover = AnimationsClass.scaleAnimation(100, durationSlider.lookup(".thumb"), durationSlider.lookup(".thumb").getScaleX(), 1.25, durationSlider.lookup(".thumb").getScaleY(), 1.25, false, 1, false);
                            thumbScale = 1.25;
                        }
                        sliderTrackHoverOn = AnimationsClass.scaleAnimation(100, durationTrack.progressBar, 1, 1, durationTrack.progressBar.getScaleY(), 3, false, 1 , false);
                        break;
                    }
                }

                if(sliderThumbHover == null && !durationSlider.isValueChanging() && thumbScale != 0.9){
                    sliderThumbHover = AnimationsClass.scaleAnimation(100, durationSlider.lookup(".thumb"), durationSlider.lookup(".thumb").getScaleX(), 0.9, durationSlider.lookup(".thumb").getScaleY(), 0.9, false, 1, false);
                    thumbScale = 0.9;
                }


                AnimationsClass.parallelAnimation(true, sliderTrackHoverOn, sliderTrackHoverOff, sliderThumbHover);
            }
        }

        lastKnownSliderHoverPosition = value;
    }

    public void enterCaptionsButton() {
        captionsButtonHover = true;
    }

    public void exitCaptionsButton() {
        captionsButtonHover = false;
    }

    public void enterSettingsButton() {
        settingsButtonHover = true;
    }

    public void exitSettingsButton() {
        settingsButtonHover = false;
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
    }

    public void playButtonHoverOn() {
        playButtonHover = true;

        if (playButtonEnabled) {
            AnimationsClass.animateBackgroundColor(playIcon, Color.rgb(200, 200, 200), Color.rgb(255, 255, 255), 200);
        } else {
            AnimationsClass.animateBackgroundColor(playIcon, Color.rgb(120, 120, 120), Color.rgb(150, 150, 150), 200);
        }
    }

    public void playButtonHoverOff() {
        playButtonHover = false;

        if (playButtonEnabled) {
            AnimationsClass.animateBackgroundColor(playIcon, Color.rgb(255, 255, 255), Color.rgb(200, 200, 200), 200);
        } else {
            AnimationsClass.animateBackgroundColor(playIcon, Color.rgb(150, 150, 150), Color.rgb(120, 120, 120), 200);
        }
    }

    public void nextVideoButtonHoverOn() {
        nextVideoButtonHover = true;

        if (nextVideoButtonEnabled) {
            AnimationsClass.animateBackgroundColor(nextVideoIcon, Color.rgb(200, 200, 200), Color.rgb(255, 255, 255), 200);
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
        else if(menuController.queueBox.activeIndex.get() > 0){
            previousVideoTooltip.updateTooltip(menuController.queueBox.queue.get(menuController.queueBox.queueOrder.get(menuController.queueBox.activeIndex.get() - 1)));
        }

        previousVideoButton.setOnMouseEntered((e) -> previousVideoTooltip.mouseHover.set(true));
        previousVideoButton.setOnMouseExited((e) -> previousVideoTooltip.mouseHover.set(false));

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

        previousVideoButton.setOnMouseEntered(null);
        previousVideoButton.setOnMouseExited(null);

        previousVideoTooltip.mouseHover.set(false);
    }

    public void enablePlayButton() {
        playButtonEnabled = true;

        if (playButtonHover) {
            playIcon.setStyle("-fx-background-color: rgb(255, 255, 255);");
        } else {
            playIcon.setStyle("-fx-background-color: rgb(200, 200, 200);");
        }

        if(mainController.windowsTaskBarController != null) mainController.windowsTaskBarController.enablePlayButton();


        if (mediaInterface.atEnd) play = new ControlTooltip(mainController,"Replay (k)", playButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
        else if (mediaInterface.playing.get()) play = new ControlTooltip(mainController,"Pause (k)", playButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
        else play = new ControlTooltip(mainController,"Play (k)", playButton, 0, TooltipType.CONTROLBAR_TOOLTIP);

        if (playButtonHover) play.showTooltip();
    }

    public void disablePlayButton() {
        playButtonEnabled = false;

        if (playButtonHover) {
            playIcon.setStyle("-fx-background-color: rgb(150, 150, 150);");
        } else {
            playIcon.setStyle("-fx-background-color: rgb(120, 120, 120);");
        }

        if(mainController.windowsTaskBarController != null) mainController.windowsTaskBarController.disablePlayButton();



        playButton.setOnMouseEntered(null);
        if (play != null && play.isShowing()) play.hide();
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



        if(menuController.queueBox.activeItem.get() == null && !menuController.queueBox.queue.isEmpty()) nextVideoTooltip.updateTooltip(menuController.queueBox.queue.get(menuController.queueBox.queueOrder.get(0)));
        else if(menuController.queueBox.queue.size() > menuController.queueBox.activeIndex.get()) nextVideoTooltip.updateTooltip(menuController.queueBox.queue.get(menuController.queueBox.queueOrder.get(menuController.queueBox.activeIndex.get() + 1)));

        nextVideoButton.setOnMouseEntered((e) -> nextVideoTooltip.mouseHover.set(true));
        nextVideoButton.setOnMouseExited((e) -> nextVideoTooltip.mouseHover.set(false));

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


        nextVideoButton.setOnMouseEntered(null);
        nextVideoButton.setOnMouseExited(null);

        nextVideoTooltip.mouseHover.set(false);
    }

    public void updateNextAndPreviousVideoButtons(){
        if(menuController.queueBox.queue.isEmpty()){
            disableNextVideoButton();
            disablePreviousVideoButton();
        }
        else {
            if(menuController.queueBox.activeItem.get() != null){
                if(menuController.queueBox.activeIndex.get() > 0 || durationSlider.getValue() > 5) enablePreviousVideoButton();
                else disablePreviousVideoButton();

                if(menuController.queueBox.activeIndex.get() < menuController.queueBox.queue.size() - 1) enableNextVideoButton();
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
}
