package hans;

import hans.Captions.CaptionsState;
import hans.Menu.HistoryItem;
import hans.Menu.MenuController;
import hans.Menu.QueueItem;
import hans.Settings.SettingsController;
import hans.Settings.SettingsState;
import hans.Captions.CaptionsController;
import javafx.animation.Animation;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
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
import javafx.scene.layout.Background;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

import java.net.URL;
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
    public ProgressBar volumeTrack, durationTrack;

    @FXML
    StackPane volumeSliderPane, previousVideoPane, playButtonPane, nextVideoPane, volumeButtonPane, captionsButtonPane, settingsButtonPane, miniplayerButtonPane, fullScreenButtonPane, durationPane;

    @FXML
    Label durationLabel;

    @FXML
    public
    Line captionsButtonLine;

    @FXML
    public Region previousVideoIcon, playIcon, nextVideoIcon, volumeIcon, captionsIcon, settingsIcon, fullScreenIcon, miniplayerIcon;


    SVGPath previousVideoSVG, playSVG, pauseSVG, replaySVG, nextVideoSVG, highVolumeSVG, lowVolumeSVG, volumeMutedSVG, captionsSVG, settingsSVG, maximizeSVG, minimizeSVG, miniplayerSVG;

    MainController mainController;
    SettingsController settingsController;
    MenuController menuController;
    CaptionsController captionsController;


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
    ControlTooltip nextVideoTooltip;
    ControlTooltip previousVideoTooltip;
    public ControlTooltip miniplayer;

    MediaInterface mediaInterface;

    ScaleTransition fullScreenButtonScaleTransition;

    PauseTransition seekTimer = new PauseTransition(Duration.millis(50));


    StackPane controlBarBackground = new StackPane();


    PauseTransition pauseTransition;

    double lastKnownSliderHoverPosition = -1000;



    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Platform.runLater(() -> {
            mute = new ControlTooltip(mainController,"Mute (m)", volumeButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
            settings = new ControlTooltip(mainController,"Settings (s)", settingsButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
            fullScreen = new ControlTooltip(mainController,"Full screen (f)", fullScreenButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
            captions = new ControlTooltip(mainController,"Subtitles/closed captions (c)", captionsButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
            miniplayer = new ControlTooltip(mainController,"Miniplayer (i)", miniplayerButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
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

        volumeSliderPane.setClip(new Rectangle(68, 30));

        volumeSlider.setTranslateX(-60);
        volumeTrack.setTranslateX(-60);

        durationLabel.setTranslateX(-60);
        durationLabel.setOnMouseClicked((e) -> toggleDurationLabel());
        durationLabel.setMouseTransparent(true);

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
                if(mainController.playbackOptionsPopUp.isShowing()) mainController.playbackOptionsPopUp.hide();
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


                double offset = 0;
                if(menuController.activeItem != null && menuController.activeItem.getMediaItem().hasVideo()) offset = (mainController.sliderHoverPreview.pane.getLayoutBounds().getMaxX() - mainController.sliderHoverLabel.label.getLayoutBounds().getMaxX())/2;

                double labelMinTranslation = (mainController.sliderHoverLabel.label.localToScene(mainController.sliderHoverLabel.label.getBoundsInLocal()).getMinX() - mainController.sliderHoverLabel.label.getTranslateX() - durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMinX()) * -1 + offset - 5;
                double labelMaxTranslation = durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMaxX() - mainController.sliderHoverLabel.label.localToScene(mainController.sliderHoverLabel.label.getBoundsInLocal()).getMaxX() + mainController.sliderHoverLabel.label.getTranslateX() - offset + 13;

                double labelNewTranslation = Math.max(labelMinTranslation, Math.min(labelMaxTranslation, e.getSceneX() - (mainController.sliderHoverLabel.label.localToScene(mainController.sliderHoverLabel.label.getBoundsInLocal()).getMinX() + mainController.sliderHoverLabel.label.getBoundsInLocal().getMaxX() / 2) + mainController.sliderHoverLabel.label.getTranslateX() - 4));

                mainController.sliderHoverLabel.label.setTranslateX(labelNewTranslation);

                if (settingsController.settingsState == SettingsState.CLOSED && captionsController.captionsState == CaptionsState.CLOSED) {
                    mainController.sliderHoverLabel.label.setVisible(true);
                    if (menuController.activeItem != null && menuController.activeItem.getMediaItem().hasVideo()) mainController.sliderHoverPreview.pane.setVisible(true);

                }

                double paneMinTranslation = (mainController.sliderHoverPreview.pane.localToScene(mainController.sliderHoverPreview.pane.getBoundsInLocal()).getMinX() - mainController.sliderHoverPreview.pane.getTranslateX() - durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMinX()) * -1;
                double paneMaxTranslation = durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMaxX() - mainController.sliderHoverPreview.pane.localToScene(mainController.sliderHoverPreview.pane.getBoundsInLocal()).getMaxX() + mainController.sliderHoverPreview.pane.getTranslateX();

                double paneNewTranslation = Math.max(paneMinTranslation, Math.min(paneMaxTranslation, e.getSceneX() - (mainController.sliderHoverPreview.pane.localToScene(mainController.sliderHoverPreview.pane.getBoundsInLocal()).getMinX() + mainController.sliderHoverPreview.pane.getBoundsInLocal().getMaxX() / 2) + mainController.sliderHoverPreview.pane.getTranslateX() - 4));

                mainController.sliderHoverPreview.pane.setTranslateX(paneNewTranslation);


                String newTime = Utilities.getTime(Duration.seconds((e.getX()) / (durationSlider.lookup(".track").getBoundsInLocal().getMaxX()) * durationSlider.getMax()));
                mainController.sliderHoverLabel.label.setText(newTime);

                lastKnownSliderHoverPosition = e.getX() / durationSlider.lookup(".track").getBoundsInLocal().getMaxX();


                if(menuController.activeItem != null && menuController.activeItem.getMediaItem().hasVideo()){

                    if(pauseTransition != null && pauseTransition.getStatus() == Animation.Status.RUNNING) return;

                    pauseTransition = new PauseTransition(Duration.millis(50));
                    pauseTransition.setOnFinished(j -> {
                        mediaInterface.updatePreviewFrame();
                    });

                    pauseTransition.playFromStart();
                }

            });

            durationSlider.lookup(".track").setOnMouseEntered((e) -> {
                durationSliderHover = true;
                durationSliderHoverOn();

                double offset = 0;
                if(menuController.activeItem != null && menuController.activeItem.getMediaItem().hasVideo()) offset = (mainController.sliderHoverPreview.pane.getLayoutBounds().getMaxX() - mainController.sliderHoverLabel.label.getLayoutBounds().getMaxX())/2;

                double labelMinTranslation = (mainController.sliderHoverLabel.label.localToScene(mainController.sliderHoverLabel.label.getBoundsInLocal()).getMinX() - mainController.sliderHoverLabel.label.getTranslateX() - durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMinX()) * -1 + offset - 5;
                double labelMaxTranslation = durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMaxX() - mainController.sliderHoverLabel.label.localToScene(mainController.sliderHoverLabel.label.getBoundsInLocal()).getMaxX() + mainController.sliderHoverLabel.label.getTranslateX() - offset + 13;

                double labelNewTranslation = Math.max(labelMinTranslation, Math.min(labelMaxTranslation, e.getSceneX() - (mainController.sliderHoverLabel.label.localToScene(mainController.sliderHoverLabel.label.getBoundsInLocal()).getMinX() + mainController.sliderHoverLabel.label.getBoundsInLocal().getMaxX() / 2) + mainController.sliderHoverLabel.label.getTranslateX() - 4));

                mainController.sliderHoverLabel.label.setTranslateX(labelNewTranslation);

                double paneMinTranslation = (mainController.sliderHoverPreview.pane.localToScene(mainController.sliderHoverPreview.pane.getBoundsInLocal()).getMinX() - mainController.sliderHoverPreview.pane.getTranslateX() - durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMinX()) * -1;
                double paneMaxTranslation = durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMaxX() - mainController.sliderHoverPreview.pane.localToScene(mainController.sliderHoverPreview.pane.getBoundsInLocal()).getMaxX() + mainController.sliderHoverPreview.pane.getTranslateX();

                double paneNewTranslation = Math.max(paneMinTranslation, Math.min(paneMaxTranslation, e.getSceneX() - (mainController.sliderHoverPreview.pane.localToScene(mainController.sliderHoverPreview.pane.getBoundsInLocal()).getMinX() + mainController.sliderHoverPreview.pane.getBoundsInLocal().getMaxX() / 2) + mainController.sliderHoverPreview.pane.getTranslateX() - 4));

                mainController.sliderHoverPreview.pane.setTranslateX(paneNewTranslation);



                String newTime = Utilities.getTime(Duration.seconds(e.getX() / (durationSlider.lookup(".track").getBoundsInLocal().getMaxX()) * durationSlider.getMax()));
                mainController.sliderHoverLabel.label.setText(newTime);

                if (settingsController.settingsState == SettingsState.CLOSED && captionsController.captionsState == CaptionsState.CLOSED) {
                    mainController.sliderHoverLabel.label.setVisible(true);
                    if (menuController.activeItem != null && menuController.activeItem.getMediaItem().hasVideo()) mainController.sliderHoverPreview.pane.setVisible(true);
                }

                lastKnownSliderHoverPosition = e.getX() / durationSlider.lookup(".track").getBoundsInLocal().getMaxX();


                if(menuController.activeItem != null && menuController.activeItem.getMediaItem().hasVideo()){
                    if(pauseTransition != null && pauseTransition.getStatus() == Animation.Status.RUNNING) return;

                    mediaInterface.updatePreviewFrame();

                    pauseTransition = new PauseTransition(Duration.millis(50));

                    pauseTransition.playFromStart();
                }


            });

            durationSlider.lookup(".track").setOnMouseExited((e) -> {
                durationSliderHover = false;


                if (!e.isPrimaryButtonDown()) {

                    durationSliderHoverOff();
                    mainController.sliderHoverLabel.label.setVisible(false);
                    mainController.sliderHoverPreview.pane.setVisible(false);
                    mainController.sliderHoverPreview.setImage(null);
                }
            });




            durationSlider.lookup(".track").addEventFilter(MouseEvent.MOUSE_DRAGGED, e -> {
                if (!e.isPrimaryButtonDown()) {

                    e.consume();

                    double offset = 0;
                    if(menuController.activeItem != null && menuController.activeItem.getMediaItem().hasVideo()) offset = (mainController.sliderHoverPreview.pane.getLayoutBounds().getMaxX() - mainController.sliderHoverLabel.label.getLayoutBounds().getMaxX())/2;

                    double labelMinTranslation = (mainController.sliderHoverLabel.label.localToScene(mainController.sliderHoverLabel.label.getBoundsInLocal()).getMinX() - mainController.sliderHoverLabel.label.getTranslateX() - durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMinX()) * -1 + offset - 5;
                    double labelMaxTranslation = durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMaxX() - mainController.sliderHoverLabel.label.localToScene(mainController.sliderHoverLabel.label.getBoundsInLocal()).getMaxX() + mainController.sliderHoverLabel.label.getTranslateX() - offset + 13;

                    double labelNewTranslation = Math.max(labelMinTranslation, Math.min(labelMaxTranslation, e.getSceneX() - (mainController.sliderHoverLabel.label.localToScene(mainController.sliderHoverLabel.label.getBoundsInLocal()).getMinX() + mainController.sliderHoverLabel.label.getBoundsInLocal().getMaxX() / 2) + mainController.sliderHoverLabel.label.getTranslateX() - 4));

                    mainController.sliderHoverLabel.label.setTranslateX(labelNewTranslation);


                    double paneMinTranslation = (mainController.sliderHoverPreview.pane.localToScene(mainController.sliderHoverPreview.pane.getBoundsInLocal()).getMinX() - mainController.sliderHoverPreview.pane.getTranslateX() - durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMinX()) * -1;
                    double paneMaxTranslation = durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMaxX() - mainController.sliderHoverPreview.pane.localToScene(mainController.sliderHoverPreview.pane.getBoundsInLocal()).getMaxX() + mainController.sliderHoverPreview.pane.getTranslateX();

                    double paneNewTranslation = Math.max(paneMinTranslation, Math.min(paneMaxTranslation, e.getSceneX() - (mainController.sliderHoverPreview.pane.localToScene(mainController.sliderHoverPreview.pane.getBoundsInLocal()).getMinX() + mainController.sliderHoverPreview.pane.getBoundsInLocal().getMaxX() / 2) + mainController.sliderHoverPreview.pane.getTranslateX() - 4));

                    mainController.sliderHoverPreview.pane.setTranslateX(paneNewTranslation);


                    String newTime = Utilities.getTime(Duration.seconds(e.getX() / (durationSlider.lookup(".track").getBoundsInLocal().getMaxX()) * durationSlider.getMax()));
                    mainController.sliderHoverLabel.label.setText(newTime);


                    lastKnownSliderHoverPosition = e.getX() / durationSlider.lookup(".track").getBoundsInLocal().getMaxX();


                    if(menuController.activeItem != null && menuController.activeItem.getMediaItem().hasVideo()){
                        if(pauseTransition != null && pauseTransition.getStatus() == Animation.Status.RUNNING) return;

                        pauseTransition = new PauseTransition(Duration.millis(50));
                        pauseTransition.setOnFinished(j -> {
                            mediaInterface.updatePreviewFrame();
                        });

                        pauseTransition.playFromStart();
                    }
                }

            });


        });



        durationSlider.valueProperty().addListener((observable, oldValue, newValue) -> {

            durationTrack.setProgress(durationSlider.getValue() / durationSlider.getMax());

            if (mediaInterface.mediaActive.get()) {

                if (mainController.miniplayerActive) {
                    mainController.miniplayer.miniplayerController.progressBar.setProgress(durationSlider.getValue() / durationSlider.getMax());
                    if (mainController.miniplayer.miniplayerController.slider.getValue() != newValue.doubleValue() || newValue.doubleValue() == 0) {
                        mainController.miniplayer.miniplayerController.slider.setValue(newValue.doubleValue());
                    }
                }

                mediaInterface.updateMedia(newValue.doubleValue());

                if (oldValue.doubleValue() <= 5 && newValue.doubleValue() > 5) {

                    if (!previousVideoButtonEnabled) enablePreviousVideoButton();
                    else {
                        if(previousVideoTooltip != null && previousVideoTooltip.isShowing()) previousVideoTooltip.hide();

                        previousVideoTooltip = new ControlTooltip(mainController, "Replay", previousVideoButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
                        if(previousVideoButtonHover) previousVideoTooltip.showTooltip();


                        if (mainController.miniplayerActive)
                            mainController.miniplayer.miniplayerController.previousVideoButtonTooltip.updateText("Replay");
                    }
                } else if (oldValue.doubleValue() > 5 && newValue.doubleValue() <= 5) {

                    if ((menuController.history.isEmpty() || menuController.historyBox.index == 0) && previousVideoButtonEnabled) {
                        disablePreviousVideoButton();
                    } else {
                        if (mainController.miniplayerActive) mainController.miniplayer.miniplayerController.previousVideoButtonTooltip.updateText("Previous video (SHIFT + P)");

                        HistoryItem historyItem = null;
                        if (menuController.historyBox.index == -1 && !menuController.history.isEmpty()) {
                            historyItem = menuController.history.get(menuController.history.size() - 1);

                        }
                        else if(menuController.historyBox.index > 0){
                            historyItem = menuController.history.get(menuController.historyBox.index - 1);

                        }

                        if(previousVideoTooltip != null && previousVideoTooltip.isShowing()) previousVideoTooltip.hide();
                        if(historyItem != null){
                            if(historyItem.getMediaItem().getCover() != null) previousVideoTooltip = new ControlTooltip(mainController, "PREVIOUS (SHIFT + P)", historyItem.videoTitle.getText(), historyItem.duration.getText(), historyItem.getMediaItem().getCover(), historyItem.getMediaItem().getCoverBackgroundColor(), previousVideoButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
                            else previousVideoTooltip = new ControlTooltip(mainController, "PREVIOUS (SHIFT + P)", historyItem.videoTitle.getText(), historyItem.duration.getText(), historyItem.getMediaItem().getPlaceholderCover(), Color.rgb(64,64,64), previousVideoButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
                        }
                        if(previousVideoButtonHover && previousVideoTooltip != null) previousVideoTooltip.showTooltip();
                    }

                }

                captionsController.updateCaptions(newValue.doubleValue() * 1000);


                if (durationSlider.isValueChanging() && !mainController.seekingWithKeys) {

                    double offset = 0;
                    if(menuController.activeItem != null && menuController.activeItem.getMediaItem().hasVideo()) offset = (mainController.sliderHoverPreview.pane.getLayoutBounds().getMaxX() - mainController.sliderHoverLabel.label.getLayoutBounds().getMaxX())/2;

                    double labelMinTranslation = (mainController.sliderHoverLabel.label.localToScene(mainController.sliderHoverLabel.label.getBoundsInLocal()).getMinX() - mainController.sliderHoverLabel.label.getTranslateX() - durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMinX()) * -1 + offset - 5;
                    double labelMaxTranslation = durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMaxX() - mainController.sliderHoverLabel.label.localToScene(mainController.sliderHoverLabel.label.getBoundsInLocal()).getMaxX() + mainController.sliderHoverLabel.label.getTranslateX() - offset + 13;

                    double labelNewTranslation = Math.max(labelMinTranslation, Math.min(labelMaxTranslation, durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMinX() + durationSlider.lookup(".track").getBoundsInLocal().getMaxX() * (newValue.doubleValue() / durationSlider.getMax()) - (mainController.sliderHoverLabel.label.localToScene(mainController.sliderHoverLabel.label.getBoundsInLocal()).getMinX() + mainController.sliderHoverLabel.label.getBoundsInLocal().getMaxX() / 2) + mainController.sliderHoverLabel.label.getTranslateX() - 4));

                    mainController.sliderHoverLabel.label.setTranslateX(labelNewTranslation);


                    double paneMinTranslation = (mainController.sliderHoverPreview.pane.localToScene(mainController.sliderHoverPreview.pane.getBoundsInLocal()).getMinX() - mainController.sliderHoverPreview.pane.getTranslateX() - durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMinX()) * -1;
                    double paneMaxTranslation = durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMaxX() - mainController.sliderHoverPreview.pane.localToScene(mainController.sliderHoverPreview.pane.getBoundsInLocal()).getMaxX() + mainController.sliderHoverPreview.pane.getTranslateX();

                    double paneNewTranslation = Math.max(paneMinTranslation, Math.min(paneMaxTranslation, durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMinX() + durationSlider.lookup(".track").getBoundsInLocal().getMaxX() * (newValue.doubleValue() / durationSlider.getMax()) - (mainController.sliderHoverPreview.pane.localToScene(mainController.sliderHoverPreview.pane.getBoundsInLocal()).getMinX() + mainController.sliderHoverPreview.pane.getBoundsInLocal().getMaxX() / 2) + mainController.sliderHoverPreview.pane.getTranslateX() - 4));

                    mainController.sliderHoverPreview.pane.setTranslateX(paneNewTranslation);


                    mainController.sliderHoverLabel.label.setText(Utilities.getTime(Duration.seconds(durationSlider.getValue())));


                    lastKnownSliderHoverPosition = newValue.doubleValue()/durationSlider.getMax();


                    if(menuController.activeItem != null && menuController.activeItem.getMediaItem().hasVideo()){
                        if(pauseTransition != null && pauseTransition.getStatus() == Animation.Status.RUNNING) return;

                        pauseTransition = new PauseTransition(Duration.millis(50));
                        pauseTransition.setOnFinished(e -> {
                            mediaInterface.updatePreviewFrame();
                        });

                        pauseTransition.playFromStart();
                    }

                }
            }

        });

        durationSlider.valueChangingProperty().addListener((observable, oldValue, newValue) -> {


            if (newValue) { // pause video when user starts seeking
                seekTimer.playFromStart();
                //replace with mediainterface.pause() after done refactoring
                if (mediaInterface.playing.get()) mediaInterface.embeddedMediaPlayer.controls().pause();
                mediaInterface.playing.set(false);

                double offset = 0;
                if(menuController.activeItem != null && menuController.activeItem.getMediaItem().hasVideo()) offset = (mainController.sliderHoverPreview.pane.getLayoutBounds().getMaxX() - mainController.sliderHoverLabel.label.getLayoutBounds().getMaxX())/2;

                double labelMinTranslation = (mainController.sliderHoverLabel.label.localToScene(mainController.sliderHoverLabel.label.getBoundsInLocal()).getMinX() - mainController.sliderHoverLabel.label.getTranslateX() - durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMinX()) * -1 + offset - 5;
                double labelMaxTranslation = durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMaxX() - mainController.sliderHoverLabel.label.localToScene(mainController.sliderHoverLabel.label.getBoundsInLocal()).getMaxX() + mainController.sliderHoverLabel.label.getTranslateX() - offset + 13;

                double labelNewTranslation = Math.max(labelMinTranslation, Math.min(labelMaxTranslation, durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMinX() + durationSlider.lookup(".track").getBoundsInLocal().getMaxX() * (durationSlider.getValue() / durationSlider.getMax()) - (mainController.sliderHoverLabel.label.localToScene(mainController.sliderHoverLabel.label.getBoundsInLocal()).getMinX() + mainController.sliderHoverLabel.label.getBoundsInLocal().getMaxX() / 2) + mainController.sliderHoverLabel.label.getTranslateX() - 4));

                mainController.sliderHoverLabel.label.setTranslateX(labelNewTranslation);


                double paneMinTranslation = (mainController.sliderHoverPreview.pane.localToScene(mainController.sliderHoverPreview.pane.getBoundsInLocal()).getMinX() - mainController.sliderHoverPreview.pane.getTranslateX() - durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMinX()) * -1;
                double paneMaxTranslation = durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMaxX() - mainController.sliderHoverPreview.pane.localToScene(mainController.sliderHoverPreview.pane.getBoundsInLocal()).getMaxX() + mainController.sliderHoverPreview.pane.getTranslateX();

                double paneNewTranslation = Math.max(paneMinTranslation, Math.min(paneMaxTranslation, durationSlider.lookup(".track").localToScene(durationSlider.lookup(".track").getBoundsInLocal()).getMinX() + durationSlider.lookup(".track").getBoundsInLocal().getMaxX() * (durationSlider.getValue() / durationSlider.getMax()) - (mainController.sliderHoverPreview.pane.localToScene(mainController.sliderHoverPreview.pane.getBoundsInLocal()).getMinX() + mainController.sliderHoverPreview.pane.getBoundsInLocal().getMaxX() / 2) + mainController.sliderHoverPreview.pane.getTranslateX() - 4));

                mainController.sliderHoverPreview.pane.setTranslateX(paneNewTranslation);


                mainController.sliderHoverLabel.label.setText(Utilities.getTime(Duration.seconds(durationSlider.getValue())));

                if (settingsController.settingsState == SettingsState.CLOSED && captionsController.captionsState == CaptionsState.CLOSED) {
                    mainController.sliderHoverLabel.label.setVisible(true);
                    if (menuController.activeItem != null && menuController.activeItem.getMediaItem().hasVideo()) mainController.sliderHoverPreview.pane.setVisible(true);
                }


                lastKnownSliderHoverPosition = durationSlider.getValue()/durationSlider.getMax();


                if(menuController.activeItem != null && menuController.activeItem.getMediaItem().hasVideo()){
                    if(pauseTransition != null && pauseTransition.getStatus() == Animation.Status.RUNNING) return;

                    pauseTransition = new PauseTransition(Duration.millis(50));
                    pauseTransition.setOnFinished(e -> mediaInterface.updatePreviewFrame());

                    pauseTransition.playFromStart();
                }



            } else {

                mainController.sliderHoverPreview.pane.setVisible(false);
                mainController.sliderHoverLabel.label.setVisible(false);

                if(!durationSliderHover){
                    mainController.sliderHoverPreview.setImage(null);
                    durationSliderHoverOff();
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
                    mediaInterface.endMedia();
                } else if (mediaInterface.wasPlaying) { // starts playing the video in the new position when user finishes seeking with the slider
                    mediaInterface.play();
                    mediaInterface.seek(Duration.seconds(durationSlider.getValue())); // seeks to exact position when user finishes dragging
                }
            }
        });


        seekTimer.setOnFinished(e -> mediaInterface.pause());

    }

    public void init(MainController mainController, SettingsController settingsController, MenuController menuController, MediaInterface mediaInterface, CaptionsController captionsController) {
        this.mainController = mainController;
        this.settingsController = settingsController;
        this.menuController = menuController;
        this.mediaInterface = mediaInterface;
        this.captionsController = captionsController;

        mouseEventTracker = new MouseEventTracker(4, mainController, this, settingsController); // creates instance of the MouseEventTracker class which keeps track of when to hide and show the control-bar
    }

    public void toggleDurationLabel() {

        if (settingsController.settingsState != SettingsState.CLOSED) settingsController.closeSettings();
        if (captionsController.captionsState != CaptionsState.CLOSED) captionsController.closeCaptions();
        if(mainController.playbackOptionsPopUp.isShowing()) mainController.playbackOptionsPopUp.hide();

        if (showingTimeLeft && mediaInterface.mediaActive.get()) {
            Utilities.setCurrentTimeLabel(durationLabel, durationSlider, Duration.millis(mediaInterface.embeddedMediaPlayer.media().info().duration()));
            showingTimeLeft = false;
        } else if (!showingTimeLeft && mediaInterface.mediaActive.get()) {
            Utilities.setTimeLeftLabel(durationLabel, durationSlider, Duration.millis(mediaInterface.embeddedMediaPlayer.media().info().duration()));
            showingTimeLeft = true;
        }
    }


    public void playButtonClick() {
        if (settingsController.settingsState != SettingsState.CLOSED) settingsController.closeSettings();
        if (captionsController.captionsState != CaptionsState.CLOSED) captionsController.closeCaptions();
        if(mainController.playbackOptionsPopUp.isShowing()) mainController.playbackOptionsPopUp.hide();

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
        AnimationsClass.volumeSliderHoverOn(volumeSlider, durationLabel, volumeTrack);
    }

    public void volumeSliderExit() {
        AnimationsClass.volumeSliderHoverOff(volumeSlider, durationLabel, volumeTrack);
    }

    public void toggleFullScreen() {

        captionsController.captionsBox.cancelDrag();
        App.stage.setFullScreen(!App.stage.isFullScreen());

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
        if(mainController.playbackOptionsPopUp.isShowing()) mainController.playbackOptionsPopUp.hide();

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
        if(mainController.playbackOptionsPopUp.isShowing()) mainController.playbackOptionsPopUp.hide();

        if(durationSlider.getValue() > 5){
            mediaInterface.replay();
        }
        else {
            if (!menuController.animationsInProgress.isEmpty()) return;
            mediaInterface.playPrevious(); // reset styling of current active history item, decrement historyposition et
        }
    }

    public void nextVideoButtonClick() {
        if (settingsController.settingsState != SettingsState.CLOSED) settingsController.closeSettings();
        if (captionsController.captionsState != CaptionsState.CLOSED) captionsController.closeCaptions();
        if(mainController.playbackOptionsPopUp.isShowing()) mainController.playbackOptionsPopUp.hide();

        if (!menuController.animationsInProgress.isEmpty()) return;
        mediaInterface.playNext();
    }


    public void settingsButtonClick() {
        if(mainController.playbackOptionsPopUp.isShowing()) mainController.playbackOptionsPopUp.hide();

        if (settingsController.settingsState != SettingsState.CLOSED) settingsController.closeSettings();
        else settingsController.openSettings();
    }


    public void fullScreenButtonClick() {
        if (settingsController.settingsState != SettingsState.CLOSED) settingsController.closeSettings();
        if (captionsController.captionsState != CaptionsState.CLOSED) captionsController.closeCaptions();
        if(mainController.playbackOptionsPopUp.isShowing()) mainController.playbackOptionsPopUp.hide();

        toggleFullScreen();
    }

    public void miniplayerButtonClick() {

        if (settingsController.settingsState != SettingsState.CLOSED) settingsController.closeSettings();
        if (captionsController.captionsState != CaptionsState.CLOSED) captionsController.closeCaptions();
        if(mainController.playbackOptionsPopUp.isShowing()) mainController.playbackOptionsPopUp.hide();

        if (mainController.miniplayerActive) mainController.closeMiniplayer();
        else mainController.openMiniplayer();
    }


    public void controlBarClick() {
        if (settingsController.settingsState != SettingsState.CLOSED) settingsController.closeSettings();
        if (captionsController.captionsState != CaptionsState.CLOSED) captionsController.closeCaptions();
        if(mainController.playbackOptionsPopUp.isShowing()) mainController.playbackOptionsPopUp.hide();
    }

    public void captionsButtonClick() {

        if(mainController.playbackOptionsPopUp.isShowing()) mainController.playbackOptionsPopUp.hide();

        if (captionsController.captionsState != CaptionsState.CLOSED) captionsController.closeCaptions();
        else captionsController.openCaptions();
    }

    public void durationSliderHoverOn() {
        ScaleTransition sliderThumbHoverOn = AnimationsClass.scaleAnimation(100, durationSlider.lookup(".thumb"), durationSlider.lookup(".thumb").getScaleX(), 1, durationSlider.lookup(".thumb").getScaleY(), 1, false, 1, false);
        ScaleTransition sliderTrackHoverOn = AnimationsClass.scaleAnimation(100, durationTrack, 1, 1, durationTrack.getScaleY(), 1.6, false, 1, false);
        AnimationsClass.parallelAnimation(true, sliderThumbHoverOn, sliderTrackHoverOn);
    }


    public void durationSliderHoverOff() {
        ScaleTransition sliderThumbHoverOff = AnimationsClass.scaleAnimation(100, durationSlider.lookup(".thumb"), durationSlider.lookup(".thumb").getScaleX(), 0, durationSlider.lookup(".thumb").getScaleY(), 0, false, 1, false);
        ScaleTransition sliderTrackHoverOff = AnimationsClass.scaleAnimation(100, durationTrack, 1, 1, durationTrack.getScaleY(), 1, false, 1, false);
        AnimationsClass.parallelAnimation(true, sliderThumbHoverOff, sliderTrackHoverOff);
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

        AnimationsClass.AnimateBackgroundColor(icon, Color.rgb(200, 200, 200), Color.rgb(255, 255, 255), 200);
    }


    public void controlButtonHoverOff(StackPane stackPane) {
        Region icon = (Region) stackPane.getChildren().get(1);

        AnimationsClass.AnimateBackgroundColor(icon, Color.rgb(255, 255, 255), Color.rgb(200, 200, 200), 200);
    }

    public void previousVideoButtonHoverOn() {
        previousVideoButtonHover = true;

        if (previousVideoButtonEnabled) {
            AnimationsClass.AnimateBackgroundColor(previousVideoIcon, Color.rgb(200, 200, 200), Color.rgb(255, 255, 255), 200);
        } else {
            AnimationsClass.AnimateBackgroundColor(previousVideoIcon, Color.rgb(120, 120, 120), Color.rgb(150, 150, 150), 200);
        }
    }

    public void previousVideoButtonHoverOff() {
        previousVideoButtonHover = false;

        if (previousVideoButtonEnabled) {
            AnimationsClass.AnimateBackgroundColor(previousVideoIcon, Color.rgb(255, 255, 255), Color.rgb(200, 200, 200), 200);
        } else {
            AnimationsClass.AnimateBackgroundColor(previousVideoIcon, Color.rgb(150, 150, 150), Color.rgb(120, 120, 120), 200);
        }
    }

    public void playButtonHoverOn() {
        playButtonHover = true;

        if (playButtonEnabled) {
            AnimationsClass.AnimateBackgroundColor(playIcon, Color.rgb(200, 200, 200), Color.rgb(255, 255, 255), 200);
        } else {
            AnimationsClass.AnimateBackgroundColor(playIcon, Color.rgb(120, 120, 120), Color.rgb(150, 150, 150), 200);
        }
    }

    public void playButtonHoverOff() {
        playButtonHover = false;

        if (playButtonEnabled) {
            AnimationsClass.AnimateBackgroundColor(playIcon, Color.rgb(255, 255, 255), Color.rgb(200, 200, 200), 200);
        } else {
            AnimationsClass.AnimateBackgroundColor(playIcon, Color.rgb(150, 150, 150), Color.rgb(120, 120, 120), 200);
        }
    }

    public void nextVideoButtonHoverOn() {
        nextVideoButtonHover = true;

        if (nextVideoButtonEnabled) {
            AnimationsClass.AnimateBackgroundColor(nextVideoIcon, Color.rgb(200, 200, 200), Color.rgb(255, 255, 255), 200);
        } else {
            AnimationsClass.AnimateBackgroundColor(nextVideoIcon, Color.rgb(120, 120, 120), Color.rgb(150, 150, 150), 200);

        }
    }

    public void nextVideoButtonHoverOff() {
        nextVideoButtonHover = false;

        if (nextVideoButtonEnabled) {
            AnimationsClass.AnimateBackgroundColor(nextVideoIcon, Color.rgb(255, 255, 255), Color.rgb(200, 200, 200), 200);
        } else {
            AnimationsClass.AnimateBackgroundColor(nextVideoIcon, Color.rgb(150, 150, 150), Color.rgb(120, 120, 120), 200);
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


        Platform.runLater(() -> {
            if (durationSlider.getValue() > 5)
                previousVideoTooltip = new ControlTooltip(mainController,"Replay", previousVideoButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
            else {
                HistoryItem historyItem = null;
                if (menuController.historyBox.index == -1 && !menuController.history.isEmpty()) {
                    historyItem = menuController.history.get(menuController.history.size() - 1);

                } else if(!menuController.history.isEmpty()){
                    historyItem = menuController.history.get(menuController.historyBox.index - 1);

                }
                if(historyItem != null){
                    if(historyItem.getMediaItem().getCover() != null) previousVideoTooltip = new ControlTooltip(mainController,"PREVIOUS (SHIFT+P)", historyItem.videoTitle.getText(), historyItem.duration.getText(), historyItem.getMediaItem().getCover(), historyItem.getMediaItem().getCoverBackgroundColor(), previousVideoButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
                    else previousVideoTooltip = new ControlTooltip(mainController, "PREVIOUS (SHIFT + P)", historyItem.videoTitle.getText(), historyItem.duration.getText(), historyItem.getMediaItem().getPlaceholderCover(), Color.rgb(64,64,64), previousVideoButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
                }
            }

            if (previousVideoButtonHover) previousVideoTooltip.showTooltip();
        });
    }

    public void disablePreviousVideoButton() {
        previousVideoButtonEnabled = false;

        if (previousVideoButtonHover) {
            previousVideoIcon.setStyle("-fx-background-color: rgb(150, 150, 150);");
        } else {
            previousVideoIcon.setStyle("-fx-background-color: rgb(120, 120, 120);");
        }

        if (mainController.miniplayerActive)
            mainController.miniplayer.miniplayerController.disablePreviousVideoButton();

        if(mainController.windowsTaskBarController != null) mainController.windowsTaskBarController.disablePreviousVideoButton();


        previousVideoButton.setOnMouseEntered(null);
        if (previousVideoTooltip != null && previousVideoTooltip.isShowing()) previousVideoTooltip.hide();
    }

    public void enablePlayButton() {
        playButtonEnabled = true;

        if (playButtonHover) {
            playIcon.setStyle("-fx-background-color: rgb(255, 255, 255);");
        } else {
            playIcon.setStyle("-fx-background-color: rgb(200, 200, 200);");
        }

        if(mainController.windowsTaskBarController != null) mainController.windowsTaskBarController.enablePlayButton();



        Platform.runLater(() -> {
            if (mediaInterface.atEnd) play = new ControlTooltip(mainController,"Replay (k)", playButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
            else if (mediaInterface.playing.get()) play = new ControlTooltip(mainController,"Pause (k)", playButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
            else play = new ControlTooltip(mainController,"Play (k)", playButton, 0, TooltipType.CONTROLBAR_TOOLTIP);

            if (playButtonHover) play.showTooltip();
        });
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
        if(mainController.windowsTaskBarController != null) mainController.windowsTaskBarController.enableNextVideoButton();


        Platform.runLater(() -> {

            if ((menuController.historyBox.index == -1 || menuController.historyBox.index == menuController.history.size() - 1) && !menuController.queue.isEmpty()) {
                QueueItem queueItem = menuController.queue.get(0);
                if(queueItem.getMediaItem().getCover() != null) nextVideoTooltip = new ControlTooltip(mainController,"NEXT (SHIFT+N)", queueItem.videoTitle.getText(), queueItem.duration.getText(), queueItem.getMediaItem().getCover(), queueItem.getMediaItem().getCoverBackgroundColor(), nextVideoButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
                else nextVideoTooltip = new ControlTooltip(mainController,"NEXT (SHIFT+N)", queueItem.videoTitle.getText(), queueItem.duration.getText(), queueItem.getMediaItem().getPlaceholderCover(), Color.rgb(64,64,64), nextVideoButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
            } else if (menuController.historyBox.index < menuController.history.size() - 1 && !menuController.history.isEmpty()) {
                HistoryItem historyItem = menuController.history.get(menuController.historyBox.index + 1);
                if(historyItem.getMediaItem().getCover() != null) nextVideoTooltip = new ControlTooltip(mainController,"NEXT (SHIFT+N)", historyItem.videoTitle.getText(), historyItem.duration.getText(), historyItem.getMediaItem().getCover(), historyItem.getMediaItem().getCoverBackgroundColor(), nextVideoButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
                else nextVideoTooltip = new ControlTooltip(mainController,"NEXT (SHIFT+N)", historyItem.videoTitle.getText(), historyItem.duration.getText(), historyItem.getMediaItem().getPlaceholderCover(), Color.rgb(64,64,64), nextVideoButton, 0, TooltipType.CONTROLBAR_TOOLTIP);

            }


            if (nextVideoButtonHover) nextVideoTooltip.showTooltip();
        });
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
        if (nextVideoTooltip != null){
            nextVideoTooltip.hide();
        }
    }

    public void updateTooltips(){
        if((menuController.historyBox.index == -1  || menuController.historyBox.index == menuController.history.size() -1) && menuController.queue.isEmpty()){
            disableNextVideoButton();
        }
        else {
            enableNextVideoButton();
        }


        if((menuController.history.isEmpty() || menuController.historyBox.index == 0) && durationSlider.getValue() <= 5){
            disablePreviousVideoButton();
        }
        else {
            enablePreviousVideoButton();
        }
    }

}
