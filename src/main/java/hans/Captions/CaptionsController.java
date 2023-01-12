package hans.Captions;

import hans.*;
import hans.MediaItems.MediaItem;
import hans.MediaItems.MediaUtilities;
import hans.Menu.*;
import hans.SRTParser.srt.SRTParser;
import hans.SRTParser.srt.Subtitle;
import hans.Settings.SettingsController;
import hans.Settings.SettingsState;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import static hans.AnimationsClass.ANIMATION_SPEED;

public class CaptionsController {

    public SettingsController settingsController;
    MainController mainController;
    MediaInterface mediaInterface;
    public ControlBarController controlBarController;
    public MenuController menuController;

    public CaptionsHome captionsHome;
    public CaptionsOptionsPane captionsOptionsPane;
    public CaptionsBox captionsBox;

    public File captionsFile;
    ArrayList<Subtitle> subtitles  = new ArrayList<>();
    int captionsPosition = 0;

    public BooleanProperty captionsSelected = new SimpleBooleanProperty(false);
    boolean showedCurrentCaption = false;

    public StackPane captionsBuffer = new StackPane();
    public StackPane captionsPane = new StackPane();
    StackPane captionsBackground = new StackPane();


    Rectangle clip = new Rectangle();

    public BooleanProperty animating = new SimpleBooleanProperty(); // animating state of the captions pane

    public CaptionsState captionsState = CaptionsState.CLOSED;

    public CaptionsController(SettingsController settingsController, MainController mainController, ControlBarController controlBarController, MenuController menuController){
        this.settingsController = settingsController;
        this.mainController = mainController;
        this.controlBarController = controlBarController;
        this.menuController = menuController;

        animating.set(false);

        captionsBuffer.setPrefSize(245, 106);
        captionsBuffer.setMaxWidth(270);
        captionsBuffer.setClip(clip);
        captionsBuffer.getChildren().add(captionsBackground);
        captionsBuffer.setMouseTransparent(true);
        captionsBackground.getStyleClass().add("settingsBackground");
        captionsBackground.setVisible(false);
        captionsBackground.setMouseTransparent(true);
        captionsBackground.setOpacity(0);
        StackPane.setMargin(captionsBuffer, new Insets(0, 20, 80, 0));
        StackPane.setAlignment(captionsBackground, Pos.BOTTOM_RIGHT);


        Platform.runLater(() -> {
            captionsBuffer.maxHeightProperty().bind(Bindings.min(Bindings.subtract(mainController.videoImageViewHeight, 120), 400));
            clip.setHeight(captionsHome.scrollPane.getHeight());
            clip.translateYProperty().bind(Bindings.subtract(captionsBuffer.heightProperty(), clip.heightProperty()));
            captionsBackground.maxHeightProperty().bind(clip.heightProperty());

            clip.setWidth(captionsHome.scrollPane.getWidth());
            clip.translateXProperty().bind(Bindings.subtract(captionsBuffer.widthProperty(), clip.widthProperty()));
            captionsBackground.maxWidthProperty().bind(clip.widthProperty());
        });

        captionsBuffer.setPickOnBounds(false);
        captionsBuffer.getChildren().add(captionsPane);
        StackPane.setAlignment(captionsBuffer, Pos.BOTTOM_RIGHT);

        captionsPane.setPrefSize(245, 106);

        captionsBox = new CaptionsBox(this, mainController);
        captionsHome = new CaptionsHome(this);
        captionsOptionsPane = new CaptionsOptionsPane(this);

        captionsSelected.addListener((observableValue, oldValue, newValue) -> {
            captionsBox.toggleVisibility(newValue);
            if(newValue) AnimationsClass.scaleAnimation(100, controlBarController.captionsButtonLine, 0, 1, 1, 1, false, 1, true);
            else AnimationsClass.scaleAnimation(100, controlBarController.captionsButtonLine, 1, 0, 1, 1, false, 1, true);
        });

    }

    public void init(MediaInterface mediaInterface){
        this.mediaInterface = mediaInterface;
    }

    public void extractCaptions(ActiveItem activeItem){
        MediaItem mediaItem = activeItem.getMediaItem();
        if(mediaItem != null && mediaItem.numberOfSubtitleStreams > 0){
            activeItem.captionGenerationTime = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss-SSS").format(new Date()) + "-";
            MediaUtilities.extractSubtitles(mediaItem, activeItem.captionGenerationTime);
        }
    }

    public void createSubtitleTabs(ActiveItem activeItem){

        MediaItem mediaItem = activeItem.getMediaItem();
        if(mediaItem != null && mediaItem.numberOfSubtitleStreams > 0 && mediaItem.subtitleStreamLanguages.size() == mediaItem.numberOfSubtitleStreams){
            activeItem.addSubtitlesIcon();

            for(int i =0; i < mediaItem.numberOfSubtitleStreams; i++){
                // add subtitle tab to captions home

                CaptionsTab captionsTab;
                if(i == mediaItem.defaultSubtitleStream){
                    captionsTab = new CaptionsTab(this, captionsHome, mediaItem.subtitleStreamLanguages.get(i) + " (Default)", new File(System.getProperty("user.home").concat("/FXPlayer/subtitles/").concat(activeItem.captionGenerationTime + i + ".srt")), false);
                    captionsTab.selectSubtitles(true);
                }
                else {
                    captionsTab = new CaptionsTab(this, captionsHome, mediaItem.subtitleStreamLanguages.get(i), new File(System.getProperty("user.home").concat("/FXPlayer/subtitles/").concat(activeItem.captionGenerationTime + i + ".srt")), false);
                }

                captionsHome.captionsWrapper.getChildren().add(i + 1, captionsTab);
                captionsHome.captionsTabs.add(captionsTab);
            }
        }
    }

    public void resetCaptions(){
        Utilities.cleanDirectory(System.getProperty("user.home").concat("/FXPlayer/subtitles/"));
    }


    public void loadCaptions(File file){

        captionsBox.captionsContainer.getChildren().clear();

        this.captionsFile = file;
        subtitles = SRTParser.getSubtitlesFromFile(file.getPath(), true);

        captionsSelected.set(true);
    }


    public void clearCaptions(){

        if(menuController.activeItem != null && menuController.activeItem.activeItemContextMenu != null){
            menuController.activeItem.activeItemContextMenu.subtitleContainer.getChildren().clear();
            menuController.activeItem.activeItemContextMenu.subtitleContainer.getChildren().add(menuController.activeItem.activeItemContextMenu.externalSubtitlesWrapper);
            menuController.activeItem.activeItemContextMenu.subtitleScroll.setPrefHeight(39);
            menuController.activeItem.activeItemContextMenu.subtitleContainer.setPrefHeight(39);
        }

        for(CaptionsTab captionsTab : captionsHome.captionsTabs) {
            captionsHome.captionsWrapper.getChildren().remove(captionsTab);
        }
        captionsHome.captionsTabs.clear();

        captionsHome.captionsWrapper.setPrefHeight(103);
        captionsHome.captionsWrapper.setMaxHeight(103);

        captionsHome.scrollPane.setPrefHeight(106);
        captionsHome.scrollPane.setMaxHeight(106);


        if(captionsState == CaptionsState.HOME_OPEN || captionsState == CaptionsState.CLOSED){
            clip.setHeight(106);
        }

        removeCaptions();
    }


    public void removeCaptions(){
        this.captionsFile = null;
        captionsSelected.set(false);

        subtitles.clear();
        captionsPosition = 0;
        showedCurrentCaption = false;

        captionsBox.captionsContainer.getChildren().clear();
    }


    public void updateCaptions(double time){

        int delay = 140;
        double adjustedTime = time + delay;
        if(!subtitles.isEmpty() &&
                captionsPosition >= 0 &&
                captionsPosition < subtitles.size() &&
                captionsSelected.get() &&
                !captionsBox.captionsDragActive) {


            if (adjustedTime < menuController.captionsController.subtitles.get(menuController.captionsController.captionsPosition).timeIn && menuController.captionsController.captionsPosition > 0) {

                do {
                    menuController.captionsController.captionsPosition--;
                    menuController.captionsController.showedCurrentCaption = false;
                }
                while (adjustedTime < menuController.captionsController.subtitles.get(menuController.captionsController.captionsPosition).timeIn && menuController.captionsController.captionsPosition > 0);
            } else if (menuController.captionsController.captionsPosition < menuController.captionsController.subtitles.size() - 1 && adjustedTime >= menuController.captionsController.subtitles.get(menuController.captionsController.captionsPosition + 1).timeIn) {
                do {
                    menuController.captionsController.captionsPosition++;
                    menuController.captionsController.showedCurrentCaption = false;
                }
                while (menuController.captionsController.captionsPosition < menuController.captionsController.subtitles.size() - 1 && adjustedTime >= menuController.captionsController.subtitles.get(menuController.captionsController.captionsPosition + 1).timeIn);
            }


            if (adjustedTime >= menuController.captionsController.subtitles.get(menuController.captionsController.captionsPosition).timeIn && adjustedTime < menuController.captionsController.subtitles.get(menuController.captionsController.captionsPosition).timeOut && !menuController.captionsController.showedCurrentCaption) {
                String text = menuController.captionsController.subtitles.get(menuController.captionsController.captionsPosition).text;

                // if the subtitle contains a new line character then split the subtitle into two and add the part after the new line onto another label

                String[] subtitleLines = Utilities.splitLines(text);

                captionsBox.captionsContainer.getChildren().clear();

                for(String line : subtitleLines){
                    captionsBox.captionsContainer.getChildren().add(captionsBox.createLabel(line));
                }

                menuController.captionsController.showedCurrentCaption = true;
            } else if ((adjustedTime >= menuController.captionsController.subtitles.get(menuController.captionsController.captionsPosition).timeOut && menuController.captionsController.captionsPosition >= menuController.captionsController.subtitles.size() - 1) || (adjustedTime >= menuController.captionsController.subtitles.get(menuController.captionsController.captionsPosition).timeOut && adjustedTime < menuController.captionsController.subtitles.get(menuController.captionsController.captionsPosition + 1).timeIn) || (adjustedTime < menuController.captionsController.subtitles.get(menuController.captionsController.captionsPosition).timeIn && menuController.captionsController.captionsPosition <= 0)) {
                    captionsBox.captionsContainer.getChildren().clear();
            }
        }
    }


    public void openCaptions(){

        if(animating.get() || controlBarController.volumeSlider.isValueChanging() || controlBarController.durationSlider.isValueChanging() || menuController.menuState != MenuState.CLOSED || captionsBox.captionsDragActive || settingsController.animating.get() || settingsController.playbackSpeedController.customSpeedPane.customSpeedSlider.isValueChanging() || settingsController.equalizerController.sliderActive) return;

        if(settingsController.settingsState != SettingsState.CLOSED) settingsController.closeSettings();

        captionsState = CaptionsState.HOME_OPEN;

        mainController.sliderHoverLabel.timeLabel.setVisible(false);
        mainController.sliderHoverLabel.chapterlabel.setVisible(false);
        mainController.sliderHoverPreview.pane.setVisible(false);

        if(controlBarController.captions.isShowing()) controlBarController.captions.hide();
        if(controlBarController.settings.isShowing()) controlBarController.settings.hide();
        if(controlBarController.miniplayer.isShowing()) controlBarController.miniplayer.hide();
        if(controlBarController.fullScreen.isShowing()) controlBarController.fullScreen.hide();


        controlBarController.captionsButton.setOnMouseEntered(null);
        controlBarController.settingsButton.setOnMouseEntered(null);
        controlBarController.miniplayerButton.setOnMouseEntered(null);
        controlBarController.fullScreenButton.setOnMouseEntered(null);

        captionsBuffer.setMouseTransparent(false);
        captionsBackground.setVisible(true);
        captionsBackground.setMouseTransparent(false);
        captionsHome.scrollPane.setVisible(true);
        captionsHome.scrollPane.setMouseTransparent(false);

        FadeTransition backgroundTranslate = new FadeTransition(Duration.millis(ANIMATION_SPEED), captionsBackground);
        backgroundTranslate.setFromValue(0);
        backgroundTranslate.setToValue(1);

        FadeTransition homeTranslate = new FadeTransition(Duration.millis(ANIMATION_SPEED), captionsHome.scrollPane);
        homeTranslate.setFromValue(0);
        homeTranslate.setToValue(1);

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, homeTranslate);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> animating.set(false));
        parallelTransition.play();
        animating.set(true);
    }

    public void closeCaptions(){

        if(animating.get()) return;


        if (controlBarController.settingsButtonHover) {
            controlBarController.settings = new ControlTooltip(mainController, "Settings (s)", controlBarController.settingsButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
            controlBarController.settings.showTooltip();

            controlBarController.miniplayer = new ControlTooltip(mainController, "Miniplayer (i)", controlBarController.miniplayerButton, 0, TooltipType.CONTROLBAR_TOOLTIP);

            controlBarController.captions = new ControlTooltip(mainController,"Subtitles/closed captions (c)", controlBarController.captionsButton, 0, TooltipType.CONTROLBAR_TOOLTIP);

            if (App.fullScreen)
                controlBarController.fullScreen = new ControlTooltip(mainController,"Exit full screen (f)", controlBarController.fullScreenButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
            else
                controlBarController.fullScreen = new ControlTooltip(mainController,"Full screen (f)", controlBarController.fullScreenButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
        }
        else if (controlBarController.captionsButtonHover) {
            controlBarController.captions = new ControlTooltip(mainController,"Subtitles/closed captions (c)", controlBarController.captionsButton, 0, TooltipType.CONTROLBAR_TOOLTIP);

            controlBarController.captions.showTooltip();

            controlBarController.miniplayer = new ControlTooltip(mainController,"Miniplayer (i)", controlBarController.miniplayerButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
            controlBarController.settings = new ControlTooltip(mainController,"Settings (s)", controlBarController.settingsButton, 0, TooltipType.CONTROLBAR_TOOLTIP);

            if (App.fullScreen)
                controlBarController.fullScreen = new ControlTooltip(mainController,"Exit full screen (f)", controlBarController.fullScreenButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
            else
                controlBarController.fullScreen = new ControlTooltip(mainController,"Full screen (f)", controlBarController.fullScreenButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
        }
        else if(controlBarController.miniplayerButtonHover){
            if (App.fullScreen) {
                controlBarController.fullScreen = new ControlTooltip(mainController,"Exit full screen (f)", controlBarController.fullScreenButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
            } else {
                controlBarController.fullScreen = new ControlTooltip(mainController,"Full screen (f)", controlBarController.fullScreenButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
            }

            controlBarController.captions = new ControlTooltip(mainController,"Subtitles/closed captions (c)", controlBarController.captionsButton, 0, TooltipType.CONTROLBAR_TOOLTIP);

            controlBarController.miniplayer = new ControlTooltip(mainController,"Miniplayer (i)", controlBarController.miniplayerButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
            controlBarController.miniplayer.showTooltip();

            controlBarController.settings = new ControlTooltip(mainController,"Settings (s)", controlBarController.settingsButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
        }
        else if (controlBarController.fullScreenButtonHover) {
            if (App.fullScreen) {
                controlBarController.fullScreen = new ControlTooltip(mainController,"Exit full screen (f)", controlBarController.fullScreenButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
            } else {
                controlBarController.fullScreen = new ControlTooltip(mainController,"Full screen (f)", controlBarController.fullScreenButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
            }
            controlBarController.fullScreen.showTooltip();

            controlBarController.captions = new ControlTooltip(mainController,"Subtitles/closed captions (c)", controlBarController.captionsButton, 0, TooltipType.CONTROLBAR_TOOLTIP);

            controlBarController.miniplayer = new ControlTooltip(mainController,"Miniplayer (i)", controlBarController.miniplayerButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
            controlBarController.settings = new ControlTooltip(mainController,"Settings (s)", controlBarController.settingsButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
        }
        else {
            controlBarController.captions = new ControlTooltip(mainController,"Subtitles/closed captions (c)", controlBarController.captionsButton, 0, TooltipType.CONTROLBAR_TOOLTIP);

            controlBarController.settings = new ControlTooltip(mainController,"Settings (s)", controlBarController.settingsButton, 0, TooltipType.CONTROLBAR_TOOLTIP);

            controlBarController.miniplayer = new ControlTooltip(mainController,"Miniplayer (i)", controlBarController.miniplayerButton, 0, TooltipType.CONTROLBAR_TOOLTIP);

            if (App.fullScreen)
                controlBarController.fullScreen = new ControlTooltip(mainController,"Exit full screen (f)", controlBarController.fullScreenButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
            else
                controlBarController.fullScreen = new ControlTooltip(mainController,"Full screen (f)", controlBarController.fullScreenButton, 0, TooltipType.CONTROLBAR_TOOLTIP);
        }

        switch (captionsState) {
            case HOME_OPEN -> closeCaptionsFromHome();
            case CAPTIONS_OPTIONS_OPEN -> closeCaptionsFromOptions();
            case FONT_FAMILY_OPEN -> closeCaptionsFromFontFamily();
            case FONT_COLOR_OPEN -> closeCaptionsFromFontColor();
            case FONT_SIZE_OPEN -> closeCaptionsFromFontSize();
            case TEXT_ALIGNMENT_OPEN -> closeCaptionsFromTextAlignment();
            case BACKGROUND_COLOR_OPEN -> closeCaptionsFromBackgroundColor();
            case BACKGROUND_OPACITY_OPEN -> closeCaptionsFromBackgroundOpacity();
            case LINE_SPACING_OPEN -> closeCaptionsFromLineSpacing();
            case OPACITY_OPEN -> closeCaptionsFromOpacity();
            default -> {
            }
        }

        captionsState = CaptionsState.CLOSED;

        if(controlBarController.durationSliderHover || controlBarController.durationSlider.isValueChanging()){
            mainController.sliderHoverLabel.timeLabel.setVisible(true);
            if(mainController.chapterController.activeChapter != -1) mainController.sliderHoverLabel.chapterlabel.setVisible(true);
            if(menuController.activeItem != null && menuController.activeItem.getMediaItem().hasVideo()) mainController.sliderHoverPreview.pane.setVisible(true);
        }

    }

    public void closeCaptionsFromHome(){
        FadeTransition backgroundTranslate = new FadeTransition(Duration.millis(ANIMATION_SPEED), captionsBackground);
        backgroundTranslate.setFromValue(1);
        backgroundTranslate.setToValue(0);

        FadeTransition captionsHomeTransition = new FadeTransition(Duration.millis(ANIMATION_SPEED), captionsHome.scrollPane);
        captionsHomeTransition.setFromValue(1);
        captionsHomeTransition.setToValue(0);

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, captionsHomeTransition);
        parallelTransition.setOnFinished((e) -> {
            animating.set(false);

            captionsBuffer.setMouseTransparent(true);
            captionsBackground.setVisible(false);
            captionsBackground.setMouseTransparent(true);
            captionsHome.scrollPane.setVisible(false);
            captionsHome.scrollPane.setMouseTransparent(true);
            captionsHome.scrollPane.setOpacity(1);
            captionsHome.scrollPane.setVvalue(0);
        });

        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.play();
        animating.set(true);
    }

    public void closeCaptionsFromOptions(){
        FadeTransition backgroundTranslate = new FadeTransition(Duration.millis(ANIMATION_SPEED), captionsBackground);
        backgroundTranslate.setFromValue(1);
        backgroundTranslate.setToValue(0);

        FadeTransition captionsOptionsTransition = new FadeTransition(Duration.millis(ANIMATION_SPEED), captionsOptionsPane.scrollPane);
        captionsOptionsTransition.setFromValue(1);
        captionsOptionsTransition.setToValue(0);

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, captionsOptionsTransition);
        parallelTransition.setOnFinished((e) -> {
            animating.set(false);

            captionsBuffer.setMouseTransparent(true);
            captionsBackground.setVisible(false);
            captionsBackground.setMouseTransparent(true);
            captionsOptionsPane.scrollPane.setVisible(false);
            captionsOptionsPane.scrollPane.setMouseTransparent(true);
            captionsOptionsPane.scrollPane.setOpacity(1);
            clip.setHeight(captionsHome.scrollPane.getHeight());
            clip.setWidth(captionsHome.scrollPane.getWidth());
        });

        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.play();
        animating.set(true);
    }

    public void closeCaptionsFromFontFamily(){
        FadeTransition backgroundTranslate = new FadeTransition(Duration.millis(ANIMATION_SPEED), captionsBackground);
        backgroundTranslate.setFromValue(1);
        backgroundTranslate.setToValue(0);

        FadeTransition fontFamilyTransition = new FadeTransition(Duration.millis(ANIMATION_SPEED), captionsOptionsPane.fontFamilyPane.scrollPane);
        fontFamilyTransition.setFromValue(1);
        fontFamilyTransition.setToValue(0);

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, fontFamilyTransition);
        parallelTransition.setOnFinished((e) -> {
            animating.set(false);

            captionsBuffer.setMouseTransparent(true);
            captionsBackground.setVisible(false);
            captionsBackground.setMouseTransparent(true);
            captionsOptionsPane.fontFamilyPane.scrollPane.setVisible(false);
            captionsOptionsPane.fontFamilyPane.scrollPane.setMouseTransparent(true);
            captionsOptionsPane.fontFamilyPane.scrollPane.setOpacity(1);
            clip.setHeight(captionsHome.scrollPane.getHeight());
            clip.setWidth(captionsHome.scrollPane.getWidth());
        });

        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.play();
        animating.set(true);
    }

    public void closeCaptionsFromFontColor(){
        FadeTransition backgroundTranslate = new FadeTransition(Duration.millis(ANIMATION_SPEED), captionsBackground);
        backgroundTranslate.setFromValue(1);
        backgroundTranslate.setToValue(0);

        FadeTransition fontColorTransition = new FadeTransition(Duration.millis(ANIMATION_SPEED), captionsOptionsPane.fontColorPane.scrollPane);
        fontColorTransition.setFromValue(1);
        fontColorTransition.setToValue(0);

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, fontColorTransition);
        parallelTransition.setOnFinished((e) -> {
            animating.set(false);

            captionsBuffer.setMouseTransparent(true);
            captionsBackground.setVisible(false);
            captionsBackground.setMouseTransparent(true);
            captionsOptionsPane.fontColorPane.scrollPane.setVisible(false);
            captionsOptionsPane.fontColorPane.scrollPane.setMouseTransparent(true);
            captionsOptionsPane.fontColorPane.scrollPane.setOpacity(1);
            clip.setHeight(captionsHome.scrollPane.getHeight());
            clip.setWidth(captionsHome.scrollPane.getWidth());
        });

        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.play();
        animating.set(true);
    }

    public void closeCaptionsFromFontSize(){
        FadeTransition backgroundTranslate = new FadeTransition(Duration.millis(ANIMATION_SPEED), captionsBackground);
        backgroundTranslate.setFromValue(1);
        backgroundTranslate.setToValue(0);

        FadeTransition fontSizeTransition = new FadeTransition(Duration.millis(ANIMATION_SPEED), captionsOptionsPane.fontSizePane.scrollPane);
        fontSizeTransition.setFromValue(1);
        fontSizeTransition.setToValue(0);

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, fontSizeTransition);
        parallelTransition.setOnFinished((e) -> {
            animating.set(false);

            captionsBuffer.setMouseTransparent(true);
            captionsBackground.setVisible(false);
            captionsBackground.setMouseTransparent(true);
            captionsOptionsPane.fontSizePane.scrollPane.setVisible(false);
            captionsOptionsPane.fontSizePane.scrollPane.setMouseTransparent(true);
            captionsOptionsPane.fontSizePane.scrollPane.setOpacity(1);
            clip.setHeight(captionsHome.scrollPane.getHeight());
            clip.setWidth(captionsHome.scrollPane.getWidth());
        });

        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.play();
        animating.set(true);
    }

    public void closeCaptionsFromTextAlignment(){
        FadeTransition backgroundTranslate = new FadeTransition(Duration.millis(ANIMATION_SPEED), captionsBackground);
        backgroundTranslate.setFromValue(1);
        backgroundTranslate.setToValue(0);

        FadeTransition textAlignmentTransition = new FadeTransition(Duration.millis(ANIMATION_SPEED), captionsOptionsPane.textAlignmentPane.scrollPane);
        textAlignmentTransition.setFromValue(1);
        textAlignmentTransition.setToValue(0);

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, textAlignmentTransition);
        parallelTransition.setOnFinished((e) -> {
            animating.set(false);

            captionsBuffer.setMouseTransparent(true);
            captionsBackground.setVisible(false);
            captionsBackground.setMouseTransparent(true);
            captionsOptionsPane.textAlignmentPane.scrollPane.setVisible(false);
            captionsOptionsPane.textAlignmentPane.scrollPane.setMouseTransparent(true);
            captionsOptionsPane.textAlignmentPane.scrollPane.setOpacity(1);
            clip.setHeight(captionsHome.scrollPane.getHeight());
            clip.setWidth(captionsHome.scrollPane.getWidth());
        });

        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.play();
        animating.set(true);
    }

    public void closeCaptionsFromBackgroundColor(){
        FadeTransition backgroundTranslate = new FadeTransition(Duration.millis(ANIMATION_SPEED), captionsBackground);
        backgroundTranslate.setFromValue(1);
        backgroundTranslate.setToValue(0);

        FadeTransition backgroundColorTransition = new FadeTransition(Duration.millis(ANIMATION_SPEED), captionsOptionsPane.backgroundColorPane.scrollPane);
        backgroundColorTransition.setFromValue(1);
        backgroundColorTransition.setToValue(0);

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, backgroundColorTransition);
        parallelTransition.setOnFinished((e) -> {
            animating.set(false);

            captionsBuffer.setMouseTransparent(true);
            captionsBackground.setVisible(false);
            captionsBackground.setMouseTransparent(true);
            captionsOptionsPane.backgroundColorPane.scrollPane.setVisible(false);
            captionsOptionsPane.backgroundColorPane.scrollPane.setMouseTransparent(true);
            captionsOptionsPane.backgroundColorPane.scrollPane.setOpacity(1);
            clip.setHeight(captionsHome.scrollPane.getHeight());
            clip.setWidth(captionsHome.scrollPane.getWidth());
        });

        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.play();
        animating.set(true);
    }

    public void closeCaptionsFromBackgroundOpacity(){
        FadeTransition backgroundTranslate = new FadeTransition(Duration.millis(ANIMATION_SPEED), captionsBackground);
        backgroundTranslate.setFromValue(1);
        backgroundTranslate.setToValue(0);

        FadeTransition backgroundOpacityTransition = new FadeTransition(Duration.millis(ANIMATION_SPEED), captionsOptionsPane.backgroundOpacityPane.scrollPane);
        backgroundOpacityTransition.setFromValue(1);
        backgroundOpacityTransition.setToValue(0);

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, backgroundOpacityTransition);
        parallelTransition.setOnFinished((e) -> {
            animating.set(false);

            captionsBuffer.setMouseTransparent(true);
            captionsBackground.setVisible(false);
            captionsBackground.setMouseTransparent(true);
            captionsOptionsPane.backgroundOpacityPane.scrollPane.setVisible(false);
            captionsOptionsPane.backgroundOpacityPane.scrollPane.setMouseTransparent(true);
            captionsOptionsPane.backgroundOpacityPane.scrollPane.setOpacity(1);
            clip.setHeight(captionsHome.scrollPane.getHeight());
            clip.setWidth(captionsHome.scrollPane.getWidth());
        });

        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.play();
        animating.set(true);
    }

    public void closeCaptionsFromLineSpacing(){
        FadeTransition backgroundTranslate = new FadeTransition(Duration.millis(ANIMATION_SPEED), captionsBackground);
        backgroundTranslate.setFromValue(1);
        backgroundTranslate.setToValue(0);

        FadeTransition lineSpacingTransition = new FadeTransition(Duration.millis(ANIMATION_SPEED), captionsOptionsPane.lineSpacingPane.scrollPane);
        lineSpacingTransition.setFromValue(1);
        lineSpacingTransition.setToValue(0);

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, lineSpacingTransition);
        parallelTransition.setOnFinished((e) -> {
            animating.set(false);

            captionsBuffer.setMouseTransparent(true);
            captionsBackground.setVisible(false);
            captionsBackground.setMouseTransparent(true);
            captionsOptionsPane.lineSpacingPane.scrollPane.setVisible(false);
            captionsOptionsPane.lineSpacingPane.scrollPane.setMouseTransparent(true);
            captionsOptionsPane.lineSpacingPane.scrollPane.setOpacity(1);
            clip.setHeight(captionsHome.scrollPane.getHeight());
            clip.setWidth(captionsHome.scrollPane.getWidth());
        });

        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.play();
        animating.set(true);
    }

    public void closeCaptionsFromOpacity(){
        FadeTransition backgroundTranslate = new FadeTransition(Duration.millis(ANIMATION_SPEED), captionsBackground);
        backgroundTranslate.setFromValue(1);
        backgroundTranslate.setToValue(0);

        FadeTransition opacityTransition = new FadeTransition(Duration.millis(ANIMATION_SPEED), captionsOptionsPane.fontOpacityPane.scrollPane);
        opacityTransition.setFromValue(1);
        opacityTransition.setToValue(0);

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, opacityTransition);
        parallelTransition.setOnFinished((e) -> {
            animating.set(false);

            captionsBuffer.setMouseTransparent(true);
            captionsBackground.setVisible(false);
            captionsBackground.setMouseTransparent(true);
            captionsOptionsPane.fontOpacityPane.scrollPane.setVisible(false);
            captionsOptionsPane.fontOpacityPane.scrollPane.setMouseTransparent(true);
            captionsOptionsPane.fontOpacityPane.scrollPane.setOpacity(1);
            clip.setHeight(captionsHome.scrollPane.getHeight());
            clip.setWidth(captionsHome.scrollPane.getWidth());
        });

        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.play();
        animating.set(true);
    }
}
