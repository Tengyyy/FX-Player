package hans;

import hans.Menu.Settings.Action;
import hans.Subtitles.SubtitlesState;
import hans.Chapters.ChapterController;
import hans.Menu.MenuController;
import hans.PlaybackSettings.PlaybackSettingsController;
import hans.PlaybackSettings.PlaybackSettingsState;
import hans.Subtitles.SubtitlesController;
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
    Button subtitlesButton;
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
    public
    Label durationLabel;

    @FXML
    public
    Line subtitlesButtonLine;

    @FXML
    public Region previousVideoIcon, playIcon, nextVideoIcon, volumeIcon, subtitlesIcon, settingsIcon, fullScreenIcon, miniplayerIcon;

    @FXML
    public
    HBox trackContainer;
    @FXML
    public HBox labelBox;

    public ArrayList<DurationTrack> durationTracks = new ArrayList<>();
    public DurationTrack defaultTrack = new DurationTrack(0 , 1);
    public DurationTrack hoverTrack = null;
    public DurationTrack activeTrack = null;

    SVGPath previousVideoSVG, playSVG, pauseSVG, replaySVG, nextVideoSVG, highVolumeSVG, lowVolumeSVG, volumeMutedSVG, subtitlesSVG, settingsSVG, maximizeSVG, minimizeSVG, miniplayerSVG;

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
    public boolean controlBarOpen = true;


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

    Timeline volumeSliderAnimation = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        trackContainer.getChildren().add(defaultTrack.progressBar);

        controlBarWrapper.setViewOrder(2);
        controlBarWrapper.getStyleClass().add("controlBarWrapper");

        previousVideoSVG = new SVGPath();
        previousVideoSVG.setContent(SVG.PREVIOUS_VIDEO.getContent());

        playSVG = new SVGPath();
        playSVG.setContent(SVG.PLAY.getContent());

        pauseSVG = new SVGPath();
        pauseSVG.setContent(SVG.PAUSE.getContent());

        replaySVG = new SVGPath();
        replaySVG.setContent(SVG.REPLAY.getContent());

        nextVideoSVG = new SVGPath();
        nextVideoSVG.setContent(SVG.NEXT_VIDEO.getContent());

        highVolumeSVG = new SVGPath();
        highVolumeSVG.setContent(SVG.VOLUME_HIGH.getContent());

        lowVolumeSVG = new SVGPath();
        lowVolumeSVG.setContent(SVG.VOLUME_LOW.getContent());

        volumeMutedSVG = new SVGPath();
        volumeMutedSVG.setContent(SVG.VOLUME_MUTED.getContent());

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

        controlBar.setOnMouseClicked(e -> {
            if(subtitlesController.subtitlesState != SubtitlesState.CLOSED) subtitlesController.closeSubtitles();
            if(playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) playbackSettingsController.closeSettings();

            if(menuController.settingsPage.settingsMenu.showing) menuController.settingsPage.settingsMenu.hide();
        });

        previousVideoIcon.setShape(previousVideoSVG);
        playIcon.setShape(playSVG);
        nextVideoIcon.setShape(nextVideoSVG);
        volumeIcon.setShape(lowVolumeSVG);

        volumeIcon.setPrefSize(15, 18);
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
        

        previousVideoButton.setOnAction(e -> previousVideoButtonClick());
        playButton.setOnAction((e) -> playButtonClick());
        nextVideoButton.setOnAction(e -> nextVideoButtonClick());
        volumeButton.setOnAction(e -> volumeButtonClick());
        subtitlesButton.setOnAction(e -> subtitlesButtonClick());
        settingsButton.setOnAction(e -> settingsButtonClick());
        miniplayerButton.setOnAction(e -> miniplayerButtonClick());
        fullScreenButton.setOnAction(e -> fullScreenButtonClick());
        
        
        previousVideoButton.setOnMouseEntered(e -> previousVideoButtonHoverOn());

        previousVideoButton.setOnMouseExited(e -> previousVideoButtonHoverOff());


        playButton.setOnMouseEntered(e -> playButtonHoverOn());

        playButton.setOnMouseExited(e -> playButtonHoverOff());

        nextVideoButton.setOnMouseEntered(e -> nextVideoButtonHoverOn());

        nextVideoButton.setOnMouseExited(e -> nextVideoButtonHoverOff());

        volumeButton.setOnMouseEntered(e -> {
            controlButtonHoverOn(volumeButtonPane);
            volumeButtonHover = true;
        });

        volumeButton.setOnMouseExited(e -> {
            controlButtonHoverOff(volumeButtonPane);
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
                volumeIcon.setShape(volumeMutedSVG);
                volumeIcon.setPrefSize(20, 20);
                volumeIcon.setTranslateX(0);
                muted = true;
                mute.updateActionText("Unmute");
            } else if (newValue.doubleValue() < 50) {
                volumeIcon.setShape(lowVolumeSVG);
                volumeIcon.setPrefSize(15, 18);
                volumeIcon.setTranslateX(-3);
                muted = false;
                mute.updateActionText("Mute");
            } else {
                volumeIcon.setShape(highVolumeSVG);
                volumeIcon.setPrefSize(20, 20);
                volumeIcon.setTranslateX(0);
                muted = false;
                mute.updateActionText("Mute");
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

                String newTime = Utilities.getTime(Duration.seconds(e.getX() / (durationSlider.lookup(".track").getBoundsInLocal().getMaxX()) * durationSlider.getMax()));
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

                String newTime = Utilities.getTime(Duration.seconds(e.getX() / (durationSlider.lookup(".track").getBoundsInLocal().getMaxX()) * durationSlider.getMax()));
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

                mainController.sliderHoverBox.timeLabel.setText(Utilities.getTime(Duration.seconds(durationSlider.getValue())));

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

                mainController.sliderHoverBox.timeLabel.setText(Utilities.getTime(Duration.seconds(durationSlider.getValue())));


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

        mouseEventTracker = new MouseEventTracker(4, mainController, this, playbackSettingsController); // creates instance of the MouseEventTracker class which keeps track of when to hide and show the control-bar
    }

    public void toggleDurationLabel() {

        if (playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) playbackSettingsController.closeSettings();
        if (subtitlesController.subtitlesState != SubtitlesState.CLOSED) subtitlesController.closeSubtitles();
        if(menuController.settingsPage.settingsMenu.showing) menuController.settingsPage.settingsMenu.hide();


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


    public void play() {

        playIcon.setShape(pauseSVG);
        playIcon.setPrefSize(20, 20);

        play.updateActionText("Pause");

        if(mainController.windowsTaskBarController != null) mainController.windowsTaskBarController.play();
    }

    public void pause() {

        playIcon.setShape(playSVG);
        playIcon.setPrefSize(20, 20);

        play.updateActionText("Play");

        if(mainController.windowsTaskBarController != null) mainController.windowsTaskBarController.pause();
    }

    public void end() {
        playIcon.setShape(replaySVG);
        playIcon.setPrefSize(24, 24);

        play.updateActionText("Replay");

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
                    mainController.sliderHoverBox.chapterlabel.setText(chapterController.chapterDescriptions.get(durationTracks.indexOf(durationTrack)).name());
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
                        mainController.sliderHoverBox.chapterlabel.setText(chapterController.chapterDescriptions.get(durationTracks.indexOf(durationTrack)).name());
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

        if (playButtonEnabled) {
            AnimationsClass.animateBackgroundColor(playIcon, Color.rgb(200, 200, 200), Color.rgb(255, 255, 255), 200);
            play.mouseHover.set(true);
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
            playIcon.setStyle("-fx-background-color: rgb(255, 255, 255);");
        } else {
            playIcon.setStyle("-fx-background-color: rgb(200, 200, 200);");
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
            playIcon.setStyle("-fx-background-color: rgb(150, 150, 150);");
        } else {
            playIcon.setStyle("-fx-background-color: rgb(120, 120, 120);");
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
}
