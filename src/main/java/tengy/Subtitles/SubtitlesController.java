package tengy.Subtitles;

import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.UrlInput;
import com.github.kokorin.jaffree.ffmpeg.UrlOutput;
import com.github.kokorin.jaffree.ffprobe.Stream;
import tengy.*;
import tengy.MediaItems.MediaItem;
import tengy.Menu.*;
import tengy.SRTParser.srt.SRTParser;
import tengy.SRTParser.srt.Subtitle;
import tengy.PlaybackSettings.PlaybackSettingsController;
import tengy.PlaybackSettings.PlaybackSettingsState;
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
import tengy.Windows.WindowState;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static tengy.AnimationsClass.ANIMATION_SPEED;
import static tengy.MediaItems.MediaUtilities.FFMPEG_PATH;

public class SubtitlesController {

    public PlaybackSettingsController playbackSettingsController;
    MainController mainController;
    MediaInterface mediaInterface;
    public ControlBarController controlBarController;
    public MenuController menuController;

    public SubtitlesHome subtitlesHome;
    public SubtitlesOptionsPane subtitlesOptionsPane;
    public OpenSubtitlesPane openSubtitlesPane;
    public OpenSubtitlesResultsPane openSubtitlesResultsPane;
    public SubtitlesBox subtitlesBox;
    public TimingPane timingPane;

    public File subtitlesFile;
    public ArrayList<Subtitle> subtitles  = new ArrayList<>();
    int subtitlesPosition = 0;

    public BooleanProperty subtitlesSelected = new SimpleBooleanProperty(false);
    boolean showedCurrentSubtitle = false;

    public StackPane subtitlesBuffer = new StackPane();
    public StackPane subtitlesPane = new StackPane();
    StackPane subtitlesBackground = new StackPane();


    Rectangle clip = new Rectangle();

    public BooleanProperty animating = new SimpleBooleanProperty(); // animating state of the captions pane

    public SubtitlesState subtitlesState = SubtitlesState.CLOSED;

    public int subtitleDelay = 0;

    public SubtitlesController(PlaybackSettingsController playbackSettingsController, MainController mainController, ControlBarController controlBarController, MenuController menuController){
        this.playbackSettingsController = playbackSettingsController;
        this.mainController = mainController;
        this.controlBarController = controlBarController;
        this.menuController = menuController;

        animating.set(false);

        subtitlesBuffer.setPrefSize(245, 181);
        subtitlesBuffer.setMaxWidth(550);
        subtitlesBuffer.setClip(clip);
        subtitlesBuffer.getChildren().addAll(subtitlesBackground, subtitlesPane);
        subtitlesBuffer.setMouseTransparent(true);
        subtitlesBackground.getStyleClass().add("settingsBackground");
        subtitlesBackground.setVisible(false);
        subtitlesBackground.setMouseTransparent(true);
        subtitlesBackground.setOpacity(0);
        StackPane.setMargin(subtitlesBuffer, new Insets(0, 20, 80, 0));
        StackPane.setAlignment(subtitlesBackground, Pos.BOTTOM_RIGHT);


        Platform.runLater(() -> {
            subtitlesBuffer.maxHeightProperty().bind(Bindings.min(Bindings.subtract(mainController.videoImageView.fitHeightProperty(), 120), 400));
            clip.setHeight(subtitlesHome.scrollPane.getHeight());
            clip.translateYProperty().bind(Bindings.subtract(subtitlesBuffer.heightProperty(), clip.heightProperty()));
            subtitlesBackground.maxHeightProperty().bind(clip.heightProperty());

            clip.setWidth(subtitlesHome.scrollPane.getWidth());
            clip.translateXProperty().bind(Bindings.subtract(subtitlesBuffer.widthProperty(), clip.widthProperty()));
            subtitlesBackground.maxWidthProperty().bind(clip.widthProperty());
        });

        subtitlesBuffer.setPickOnBounds(false);
        StackPane.setAlignment(subtitlesBuffer, Pos.BOTTOM_RIGHT);

        subtitlesPane.setPrefSize(245, 181);

        subtitlesBox = new SubtitlesBox(this, mainController);
        subtitlesHome = new SubtitlesHome(this);
        subtitlesOptionsPane = new SubtitlesOptionsPane(this);
        openSubtitlesPane = new OpenSubtitlesPane(subtitlesHome, this);
        openSubtitlesResultsPane = new OpenSubtitlesResultsPane(subtitlesHome, this);
        timingPane = new TimingPane(subtitlesHome, this);

        subtitlesBox.loadSubtitlePreferences();

        subtitlesSelected.addListener((observableValue, oldValue, newValue) -> {
            subtitlesBox.toggleVisibility(newValue);
            if(newValue) AnimationsClass.scaleAnimation(100, controlBarController.subtitlesButtonLine, 0, 1, 1, 1, false, 1, true);
            else AnimationsClass.scaleAnimation(100, controlBarController.subtitlesButtonLine, 1, 0, 1, 1, false, 1, true);
        });

    }

    public void init(MediaInterface mediaInterface){
        this.mediaInterface = mediaInterface;
    }

    public Boolean extractSubtitles(MediaItem mediaItem){
        if(mediaItem == null || mediaItem.subtitleStreams.isEmpty() || !mediaItem.subtitlesGenerationTime.isEmpty() && !mediaItem.subtitlesExtractionInProgress.get()) return false;

        String subtitlesDirectory = System.getProperty("user.home").concat("/FXPlayer/subtitles/");
        mediaItem.subtitlesGenerationTime = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss-SSS").format(new Date()) + "-";
        mediaItem.subtitlesExtractionInProgress.set(true);
        try {
            Files.createDirectory(Paths.get(subtitlesDirectory));
        } catch (IOException ignored){
        }

        FFmpeg fFmpeg = FFmpeg.atPath(Paths.get(FFMPEG_PATH))
                .addInput(UrlInput.fromUrl(mediaItem.getFile().getAbsolutePath()));

        for(int i =0; i < mediaItem.subtitleStreams.size(); i++){
            fFmpeg.addArguments("-map", "0:s:" + i);
            fFmpeg.addOutput(UrlOutput.toUrl(subtitlesDirectory.concat(mediaItem.subtitlesGenerationTime + i + ".srt")));
        }

        fFmpeg.execute();

        mediaItem.subtitlesExtractionInProgress.set(false);
        return true;
    }

    public void createSubtitleTabs(MediaItem mediaItem){

        if(mediaItem != null && !mediaItem.subtitleStreams.isEmpty()){

            subtitlesHome.subtitlesChooserTab.setStyle("-fx-border-width: 1 0 0 0;");

            String preferredLanguage = menuController.settingsPage.subtitleSection.languageProperty.get();
            String preferredLanguageCode = OpenSubtitlesPane.languageMap.get(preferredLanguage);

            boolean selected = false;

            SubtitlesTab defaultSubtitlesTab = null;
            SubtitlesTab preferredLanguageSubtitlesTab = null;

            for(int i = 0 ; i < mediaItem.subtitleStreams.size(); i++){
                // add subtitle tab to captions home

                Stream stream = mediaItem.subtitleStreams.get(i);
                String languageCode = stream.getTag("language");
                String tabTitle;
                if(languageCode == null || languageCode.equals("und")) tabTitle = "Undefined";
                else tabTitle = Locale.forLanguageTag(languageCode).getDisplayLanguage();

                if(stream.getDisposition().getDefault() == 1) tabTitle += " (Default)";

                SubtitlesTab subtitlesTab = new SubtitlesTab(this, subtitlesHome, tabTitle, new File(System.getProperty("user.home").concat("/FXPlayer/subtitles/").concat(mediaItem.subtitlesGenerationTime + i + ".srt")), false);

                if(stream.getDisposition().getDefault() == 1){
                    defaultSubtitlesTab = subtitlesTab;
                    if(preferredLanguageCode.equals(languageCode)) {
                        subtitlesTab.selectSubtitles(true);
                        preferredLanguageSubtitlesTab = subtitlesTab;
                        selected = true;
                    }
                }

                if(preferredLanguageCode.equals(languageCode)) preferredLanguageSubtitlesTab = subtitlesTab;

                subtitlesHome.subtitlesWrapper.getChildren().add(i + 1, subtitlesTab);
                subtitlesHome.subtitlesTabs.add(subtitlesTab);
                subtitlesHome.focusNodes.add(i + 1, subtitlesTab);
            }

            if(!selected){
                if(preferredLanguageSubtitlesTab != null) preferredLanguageSubtitlesTab.selectSubtitles(true);
                else if(defaultSubtitlesTab != null) defaultSubtitlesTab.selectSubtitles(true);
            }

            if(subtitlesState == SubtitlesState.HOME_OPEN) subtitlesHome.subtitlesWrapper.requestFocus();
        }
    }


    public void scanParentFolderForMatchingSubtitles(MediaItem mediaItem){
        File mediaFile = mediaItem.getFile();
        String name = mediaFile.getName().substring(0, mediaFile.getName().lastIndexOf("." + Utilities.getFileExtension(mediaFile)));
        File subtitleFile = new File(mediaFile.getParentFile(), name + ".srt");

        if(subtitleFile.exists() && subtitleFile.canRead()){
            subtitlesHome.createTab(subtitleFile);
        }
    }


    public void resetSubtitles() {
        Utilities.cleanDirectory(System.getProperty("user.home").concat("/FXPlayer/subtitles/"));
    }


    public void loadSubtitles(File file){

        subtitlesBox.subtitlesContainer.getChildren().clear();
        subtitlesBox.subtitlesContainer.setMouseTransparent(false);

        subtitlesPosition = 0;
        showedCurrentSubtitle = false;

        this.subtitlesFile = file;
        subtitles.clear();
        subtitles = SRTParser.getSubtitlesFromFile(file.getPath(), true);

        subtitlesSelected.set(true);
    }


    public void clearSubtitles(){

        for(SubtitlesTab subtitlesTab : subtitlesHome.subtitlesTabs) subtitlesHome.subtitlesWrapper.getChildren().remove(subtitlesTab);

        subtitlesHome.subtitlesTabs.clear();
        subtitlesHome.subtitlesWrapper.setPrefHeight(178);
        subtitlesHome.subtitlesWrapper.setMaxHeight(178);

        subtitlesHome.resetFocusNodes();

        subtitlesHome.scrollPane.setPrefHeight(181);
        subtitlesHome.scrollPane.setMaxHeight(181);

        subtitlesHome.subtitlesChooserTab.setStyle("-fx-border-width: 0;");

        if(subtitlesState == SubtitlesState.HOME_OPEN || subtitlesState == SubtitlesState.CLOSED) clip.setHeight(181);

        removeSubtitles();
    }


    public void removeSubtitles(){

        this.subtitlesFile = null;
        subtitlesSelected.set(false);

        subtitles.clear();
        subtitlesPosition = 0;
        showedCurrentSubtitle = false;

        subtitlesBox.subtitlesContainer.getChildren().clear();

        timingPane.saveButton.setDisable(true);
    }


    public void updateSubtitles(double time){

        // 140 ms is about the delay of vlc time changed events
        int adjustment = 140;
        double adjustedTime = time + adjustment + subtitleDelay;
        if(!subtitles.isEmpty() &&
                subtitlesPosition >= 0 &&
                subtitlesPosition < subtitles.size() &&
                subtitlesSelected.get() &&
                !subtitlesBox.subtitlesDragActive) {


            if (adjustedTime < menuController.subtitlesController.subtitles.get(menuController.subtitlesController.subtitlesPosition).timeIn && menuController.subtitlesController.subtitlesPosition > 0) {

                do {
                    menuController.subtitlesController.subtitlesPosition--;
                    menuController.subtitlesController.showedCurrentSubtitle = false;
                }
                while (adjustedTime < menuController.subtitlesController.subtitles.get(menuController.subtitlesController.subtitlesPosition).timeIn && menuController.subtitlesController.subtitlesPosition > 0);
            } else if (menuController.subtitlesController.subtitlesPosition < menuController.subtitlesController.subtitles.size() - 1 && adjustedTime >= menuController.subtitlesController.subtitles.get(menuController.subtitlesController.subtitlesPosition + 1).timeIn) {
                do {
                    menuController.subtitlesController.subtitlesPosition++;
                    menuController.subtitlesController.showedCurrentSubtitle = false;
                }
                while (menuController.subtitlesController.subtitlesPosition < menuController.subtitlesController.subtitles.size() - 1 && adjustedTime >= menuController.subtitlesController.subtitles.get(menuController.subtitlesController.subtitlesPosition + 1).timeIn);
            }


            if (adjustedTime >= menuController.subtitlesController.subtitles.get(menuController.subtitlesController.subtitlesPosition).timeIn && adjustedTime < menuController.subtitlesController.subtitles.get(menuController.subtitlesController.subtitlesPosition).timeOut && !menuController.subtitlesController.showedCurrentSubtitle) {
                String text = menuController.subtitlesController.subtitles.get(menuController.subtitlesController.subtitlesPosition).text;

                // if the subtitle contains a new line character then split the subtitle into two and add the part after the new line onto another label

                String[] subtitleLines = Utilities.splitLines(text);

                subtitlesBox.subtitlesContainer.getChildren().clear();

                for(String line : subtitleLines){
                    subtitlesBox.subtitlesContainer.getChildren().add(subtitlesBox.createLabel(line));
                }


                menuController.subtitlesController.showedCurrentSubtitle = true;
            } else if ((adjustedTime >= menuController.subtitlesController.subtitles.get(menuController.subtitlesController.subtitlesPosition).timeOut && menuController.subtitlesController.subtitlesPosition >= menuController.subtitlesController.subtitles.size() - 1) || (adjustedTime >= menuController.subtitlesController.subtitles.get(menuController.subtitlesController.subtitlesPosition).timeOut && adjustedTime < menuController.subtitlesController.subtitles.get(menuController.subtitlesController.subtitlesPosition + 1).timeIn) || (adjustedTime < menuController.subtitlesController.subtitles.get(menuController.subtitlesController.subtitlesPosition).timeIn && menuController.subtitlesController.subtitlesPosition <= 0)) {
                    subtitlesBox.subtitlesContainer.getChildren().clear();
            }
        }
    }


    public void openSubtitles(){

        if(animating.get() || controlBarController.volumeSlider.isValueChanging() || controlBarController.durationSlider.isValueChanging() || (menuController.menuState != MenuState.CLOSED && !menuController.extended.get()) || subtitlesBox.subtitlesDragActive || playbackSettingsController.animating.get() || playbackSettingsController.playbackSpeedController.customSpeedPane.customSpeedSlider.isValueChanging() || mainController.windowController.windowState != WindowState.CLOSED) return;

        mainController.videoImageView.requestFocus();

        if(playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) playbackSettingsController.closeSettings();

        subtitlesState = SubtitlesState.HOME_OPEN;

        mainController.sliderHoverBox.setVisible(false);


        controlBarController.subtitles.disableTooltip();
        controlBarController.settings.disableTooltip();
        controlBarController.miniplayer.disableTooltip();
        controlBarController.fullScreen.disableTooltip();

        subtitlesBuffer.setMouseTransparent(false);
        subtitlesBackground.setVisible(true);
        subtitlesBackground.setMouseTransparent(false);
        subtitlesHome.scrollPane.setVisible(true);
        subtitlesHome.scrollPane.setMouseTransparent(false);

        FadeTransition backgroundTranslate = new FadeTransition(Duration.millis(ANIMATION_SPEED), subtitlesBackground);
        backgroundTranslate.setFromValue(0);
        backgroundTranslate.setToValue(1);

        FadeTransition homeTranslate = new FadeTransition(Duration.millis(ANIMATION_SPEED), subtitlesHome.scrollPane);
        homeTranslate.setFromValue(0);
        homeTranslate.setToValue(1);

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, homeTranslate);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> animating.set(false));
        parallelTransition.play();
        animating.set(true);
    }

    public void closeSubtitles(){

        if(animating.get()) return;

        mainController.videoImageView.requestFocus();

        controlBarController.subtitles.enableTooltip();
        controlBarController.settings.enableTooltip();
        controlBarController.miniplayer.enableTooltip();
        controlBarController.fullScreen.enableTooltip();

        if (controlBarController.settingsButtonHover) controlBarController.settings.mouseHover.set(true);
        else if (controlBarController.subtitlesButtonHover) controlBarController.subtitles.mouseHover.set(true);
        else if(controlBarController.miniplayerButtonHover) controlBarController.miniplayer.mouseHover.set(true);
        else if (controlBarController.fullScreenButtonHover) controlBarController.fullScreen.mouseHover.set(true);

        switch (subtitlesState) {
            case HOME_OPEN -> closeSubtitlesFromHome();
            case CAPTIONS_OPTIONS_OPEN -> closeSubtitlesFromOptions();
            case FONT_FAMILY_OPEN -> closeSubtitlesFromFontFamily();
            case FONT_COLOR_OPEN -> closeSubtitlesFromFontColor();
            case FONT_SIZE_OPEN -> closeSubtitlesFromFontSize();
            case TEXT_ALIGNMENT_OPEN -> closeSubtitlesFromTextAlignment();
            case BACKGROUND_COLOR_OPEN -> closeSubtitlesFromBackgroundColor();
            case BACKGROUND_OPACITY_OPEN -> closeSubtitlesFromBackgroundOpacity();
            case LINE_SPACING_OPEN -> closeSubtitlesFromLineSpacing();
            case OPACITY_OPEN -> closeSubtitlesFromOpacity();
            case OPENSUBTITLES_OPEN -> closeSubtitlesFromOpenSubtitles();
            case OPENSUBTITLES_RESULTS_OPEN -> closeSubtitlesFromOpenSubtitlesResults();
            case TIMING_OPEN -> closeSubtitlesFromTimingPane();
            default -> {
            }
        }

        subtitlesState = SubtitlesState.CLOSED;

        if(controlBarController.durationSliderHover || controlBarController.durationSlider.isValueChanging()){
            mainController.sliderHoverBox.setVisible(true);
        }

    }

    public void closeSubtitlesFromHome(){
        FadeTransition backgroundTranslate = new FadeTransition(Duration.millis(ANIMATION_SPEED), subtitlesBackground);
        backgroundTranslate.setFromValue(1);
        backgroundTranslate.setToValue(0);

        FadeTransition captionsHomeTransition = new FadeTransition(Duration.millis(ANIMATION_SPEED), subtitlesHome.scrollPane);
        captionsHomeTransition.setFromValue(1);
        captionsHomeTransition.setToValue(0);

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, captionsHomeTransition);
        parallelTransition.setOnFinished((e) -> {
            animating.set(false);

            subtitlesBuffer.setMouseTransparent(true);
            subtitlesBackground.setVisible(false);
            subtitlesBackground.setMouseTransparent(true);
            subtitlesHome.scrollPane.setVisible(false);
            subtitlesHome.scrollPane.setMouseTransparent(true);
            subtitlesHome.scrollPane.setOpacity(1);
            subtitlesHome.scrollPane.setVvalue(0);

            openSubtitlesResultsPane.clearResults();
        });

        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.play();
        animating.set(true);
    }

    public void closeSubtitlesFromOptions(){
        FadeTransition backgroundTranslate = new FadeTransition(Duration.millis(ANIMATION_SPEED), subtitlesBackground);
        backgroundTranslate.setFromValue(1);
        backgroundTranslate.setToValue(0);

        FadeTransition captionsOptionsTransition = new FadeTransition(Duration.millis(ANIMATION_SPEED), subtitlesOptionsPane.scrollPane);
        captionsOptionsTransition.setFromValue(1);
        captionsOptionsTransition.setToValue(0);

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, captionsOptionsTransition);
        parallelTransition.setOnFinished((e) -> {
            animating.set(false);

            subtitlesBuffer.setMouseTransparent(true);
            subtitlesBackground.setVisible(false);
            subtitlesBackground.setMouseTransparent(true);
            subtitlesOptionsPane.scrollPane.setVisible(false);
            subtitlesOptionsPane.scrollPane.setMouseTransparent(true);
            subtitlesOptionsPane.scrollPane.setOpacity(1);
            clip.setHeight(subtitlesHome.scrollPane.getHeight());
            clip.setWidth(subtitlesHome.scrollPane.getWidth());

            openSubtitlesResultsPane.clearResults();
        });

        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.play();
        animating.set(true);
    }

    public void closeSubtitlesFromFontFamily(){
        FadeTransition backgroundTranslate = new FadeTransition(Duration.millis(ANIMATION_SPEED), subtitlesBackground);
        backgroundTranslate.setFromValue(1);
        backgroundTranslate.setToValue(0);

        FadeTransition fontFamilyTransition = new FadeTransition(Duration.millis(ANIMATION_SPEED), subtitlesOptionsPane.fontFamilyPane.scrollPane);
        fontFamilyTransition.setFromValue(1);
        fontFamilyTransition.setToValue(0);

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, fontFamilyTransition);
        parallelTransition.setOnFinished((e) -> {
            animating.set(false);

            subtitlesBuffer.setMouseTransparent(true);
            subtitlesBackground.setVisible(false);
            subtitlesBackground.setMouseTransparent(true);
            subtitlesOptionsPane.fontFamilyPane.scrollPane.setVisible(false);
            subtitlesOptionsPane.fontFamilyPane.scrollPane.setMouseTransparent(true);
            subtitlesOptionsPane.fontFamilyPane.scrollPane.setOpacity(1);
            clip.setHeight(subtitlesHome.scrollPane.getHeight());
            clip.setWidth(subtitlesHome.scrollPane.getWidth());

            openSubtitlesResultsPane.clearResults();
        });

        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.play();
        animating.set(true);
    }

    public void closeSubtitlesFromFontColor(){
        FadeTransition backgroundTranslate = new FadeTransition(Duration.millis(ANIMATION_SPEED), subtitlesBackground);
        backgroundTranslate.setFromValue(1);
        backgroundTranslate.setToValue(0);

        FadeTransition fontColorTransition = new FadeTransition(Duration.millis(ANIMATION_SPEED), subtitlesOptionsPane.fontColorPane.scrollPane);
        fontColorTransition.setFromValue(1);
        fontColorTransition.setToValue(0);

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, fontColorTransition);
        parallelTransition.setOnFinished((e) -> {
            animating.set(false);

            subtitlesBuffer.setMouseTransparent(true);
            subtitlesBackground.setVisible(false);
            subtitlesBackground.setMouseTransparent(true);
            subtitlesOptionsPane.fontColorPane.scrollPane.setVisible(false);
            subtitlesOptionsPane.fontColorPane.scrollPane.setMouseTransparent(true);
            subtitlesOptionsPane.fontColorPane.scrollPane.setOpacity(1);
            clip.setHeight(subtitlesHome.scrollPane.getHeight());
            clip.setWidth(subtitlesHome.scrollPane.getWidth());

            openSubtitlesResultsPane.clearResults();
        });

        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.play();
        animating.set(true);
    }

    public void closeSubtitlesFromFontSize(){
        FadeTransition backgroundTranslate = new FadeTransition(Duration.millis(ANIMATION_SPEED), subtitlesBackground);
        backgroundTranslate.setFromValue(1);
        backgroundTranslate.setToValue(0);

        FadeTransition fontSizeTransition = new FadeTransition(Duration.millis(ANIMATION_SPEED), subtitlesOptionsPane.fontSizePane.scrollPane);
        fontSizeTransition.setFromValue(1);
        fontSizeTransition.setToValue(0);

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, fontSizeTransition);
        parallelTransition.setOnFinished((e) -> {
            animating.set(false);

            subtitlesBuffer.setMouseTransparent(true);
            subtitlesBackground.setVisible(false);
            subtitlesBackground.setMouseTransparent(true);
            subtitlesOptionsPane.fontSizePane.scrollPane.setVisible(false);
            subtitlesOptionsPane.fontSizePane.scrollPane.setMouseTransparent(true);
            subtitlesOptionsPane.fontSizePane.scrollPane.setOpacity(1);
            clip.setHeight(subtitlesHome.scrollPane.getHeight());
            clip.setWidth(subtitlesHome.scrollPane.getWidth());

            openSubtitlesResultsPane.clearResults();
        });

        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.play();
        animating.set(true);
    }

    public void closeSubtitlesFromTextAlignment(){
        FadeTransition backgroundTranslate = new FadeTransition(Duration.millis(ANIMATION_SPEED), subtitlesBackground);
        backgroundTranslate.setFromValue(1);
        backgroundTranslate.setToValue(0);

        FadeTransition textAlignmentTransition = new FadeTransition(Duration.millis(ANIMATION_SPEED), subtitlesOptionsPane.textAlignmentPane.scrollPane);
        textAlignmentTransition.setFromValue(1);
        textAlignmentTransition.setToValue(0);

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, textAlignmentTransition);
        parallelTransition.setOnFinished((e) -> {
            animating.set(false);

            subtitlesBuffer.setMouseTransparent(true);
            subtitlesBackground.setVisible(false);
            subtitlesBackground.setMouseTransparent(true);
            subtitlesOptionsPane.textAlignmentPane.scrollPane.setVisible(false);
            subtitlesOptionsPane.textAlignmentPane.scrollPane.setMouseTransparent(true);
            subtitlesOptionsPane.textAlignmentPane.scrollPane.setOpacity(1);
            clip.setHeight(subtitlesHome.scrollPane.getHeight());
            clip.setWidth(subtitlesHome.scrollPane.getWidth());

            openSubtitlesResultsPane.clearResults();
        });

        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.play();
        animating.set(true);
    }

    public void closeSubtitlesFromBackgroundColor(){
        FadeTransition backgroundTranslate = new FadeTransition(Duration.millis(ANIMATION_SPEED), subtitlesBackground);
        backgroundTranslate.setFromValue(1);
        backgroundTranslate.setToValue(0);

        FadeTransition backgroundColorTransition = new FadeTransition(Duration.millis(ANIMATION_SPEED), subtitlesOptionsPane.backgroundColorPane.scrollPane);
        backgroundColorTransition.setFromValue(1);
        backgroundColorTransition.setToValue(0);

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, backgroundColorTransition);
        parallelTransition.setOnFinished((e) -> {
            animating.set(false);

            subtitlesBuffer.setMouseTransparent(true);
            subtitlesBackground.setVisible(false);
            subtitlesBackground.setMouseTransparent(true);
            subtitlesOptionsPane.backgroundColorPane.scrollPane.setVisible(false);
            subtitlesOptionsPane.backgroundColorPane.scrollPane.setMouseTransparent(true);
            subtitlesOptionsPane.backgroundColorPane.scrollPane.setOpacity(1);
            clip.setHeight(subtitlesHome.scrollPane.getHeight());
            clip.setWidth(subtitlesHome.scrollPane.getWidth());

            openSubtitlesResultsPane.clearResults();
        });

        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.play();
        animating.set(true);
    }

    public void closeSubtitlesFromBackgroundOpacity(){
        FadeTransition backgroundTranslate = new FadeTransition(Duration.millis(ANIMATION_SPEED), subtitlesBackground);
        backgroundTranslate.setFromValue(1);
        backgroundTranslate.setToValue(0);

        FadeTransition backgroundOpacityTransition = new FadeTransition(Duration.millis(ANIMATION_SPEED), subtitlesOptionsPane.backgroundOpacityPane.scrollPane);
        backgroundOpacityTransition.setFromValue(1);
        backgroundOpacityTransition.setToValue(0);

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, backgroundOpacityTransition);
        parallelTransition.setOnFinished((e) -> {
            animating.set(false);

            subtitlesBuffer.setMouseTransparent(true);
            subtitlesBackground.setVisible(false);
            subtitlesBackground.setMouseTransparent(true);
            subtitlesOptionsPane.backgroundOpacityPane.scrollPane.setVisible(false);
            subtitlesOptionsPane.backgroundOpacityPane.scrollPane.setMouseTransparent(true);
            subtitlesOptionsPane.backgroundOpacityPane.scrollPane.setOpacity(1);
            clip.setHeight(subtitlesHome.scrollPane.getHeight());
            clip.setWidth(subtitlesHome.scrollPane.getWidth());

            openSubtitlesResultsPane.clearResults();
        });

        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.play();
        animating.set(true);
    }

    public void closeSubtitlesFromLineSpacing(){
        FadeTransition backgroundTranslate = new FadeTransition(Duration.millis(ANIMATION_SPEED), subtitlesBackground);
        backgroundTranslate.setFromValue(1);
        backgroundTranslate.setToValue(0);

        FadeTransition lineSpacingTransition = new FadeTransition(Duration.millis(ANIMATION_SPEED), subtitlesOptionsPane.lineSpacingPane.scrollPane);
        lineSpacingTransition.setFromValue(1);
        lineSpacingTransition.setToValue(0);

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, lineSpacingTransition);
        parallelTransition.setOnFinished((e) -> {
            animating.set(false);

            subtitlesBuffer.setMouseTransparent(true);
            subtitlesBackground.setVisible(false);
            subtitlesBackground.setMouseTransparent(true);
            subtitlesOptionsPane.lineSpacingPane.scrollPane.setVisible(false);
            subtitlesOptionsPane.lineSpacingPane.scrollPane.setMouseTransparent(true);
            subtitlesOptionsPane.lineSpacingPane.scrollPane.setOpacity(1);
            clip.setHeight(subtitlesHome.scrollPane.getHeight());
            clip.setWidth(subtitlesHome.scrollPane.getWidth());

            openSubtitlesResultsPane.clearResults();
        });

        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.play();
        animating.set(true);
    }

    public void closeSubtitlesFromOpacity(){
        FadeTransition backgroundTranslate = new FadeTransition(Duration.millis(ANIMATION_SPEED), subtitlesBackground);
        backgroundTranslate.setFromValue(1);
        backgroundTranslate.setToValue(0);

        FadeTransition opacityTransition = new FadeTransition(Duration.millis(ANIMATION_SPEED), subtitlesOptionsPane.fontOpacityPane.scrollPane);
        opacityTransition.setFromValue(1);
        opacityTransition.setToValue(0);

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, opacityTransition);
        parallelTransition.setOnFinished((e) -> {
            animating.set(false);

            subtitlesBuffer.setMouseTransparent(true);
            subtitlesBackground.setVisible(false);
            subtitlesBackground.setMouseTransparent(true);
            subtitlesOptionsPane.fontOpacityPane.scrollPane.setVisible(false);
            subtitlesOptionsPane.fontOpacityPane.scrollPane.setMouseTransparent(true);
            subtitlesOptionsPane.fontOpacityPane.scrollPane.setOpacity(1);
            clip.setHeight(subtitlesHome.scrollPane.getHeight());
            clip.setWidth(subtitlesHome.scrollPane.getWidth());

            openSubtitlesResultsPane.clearResults();
        });

        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.play();
        animating.set(true);
    }

    public void closeSubtitlesFromOpenSubtitles(){
        FadeTransition backgroundTranslate = new FadeTransition(Duration.millis(ANIMATION_SPEED), subtitlesBackground);
        backgroundTranslate.setFromValue(1);
        backgroundTranslate.setToValue(0);

        FadeTransition openSubtitlesTransition = new FadeTransition(Duration.millis(ANIMATION_SPEED), openSubtitlesPane.scrollPane);
        openSubtitlesTransition.setFromValue(1);
        openSubtitlesTransition.setToValue(0);

        openSubtitlesPane.languageBox.hide();

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, openSubtitlesTransition);
        parallelTransition.setOnFinished((e) -> {
            animating.set(false);

            subtitlesBuffer.setMouseTransparent(true);
            subtitlesBackground.setVisible(false);
            subtitlesBackground.setMouseTransparent(true);
            openSubtitlesPane.scrollPane.setVisible(false);
            openSubtitlesPane.scrollPane.setMouseTransparent(true);
            openSubtitlesPane.scrollPane.setOpacity(1);
            clip.setHeight(subtitlesHome.scrollPane.getHeight());
            clip.setWidth(subtitlesHome.scrollPane.getWidth());

            openSubtitlesPane.imdbFieldBorder.setVisible(false);
            openSubtitlesPane.titleFieldBorder.setVisible(false);

            openSubtitlesPane.languageBox.scrollPane.setVvalue(0);


            openSubtitlesResultsPane.clearResults();
        });

        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.play();
        animating.set(true);
    }

    public void closeSubtitlesFromOpenSubtitlesResults(){
        FadeTransition backgroundTranslate = new FadeTransition(Duration.millis(ANIMATION_SPEED), subtitlesBackground);
        backgroundTranslate.setFromValue(1);
        backgroundTranslate.setToValue(0);

        FadeTransition openSubtitlesResultsTransition = new FadeTransition(Duration.millis(ANIMATION_SPEED), openSubtitlesResultsPane.scrollPane);
        openSubtitlesResultsTransition.setFromValue(1);
        openSubtitlesResultsTransition.setToValue(0);

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, openSubtitlesResultsTransition);
        parallelTransition.setOnFinished((e) -> {
            animating.set(false);

            subtitlesBuffer.setMouseTransparent(true);
            subtitlesBackground.setVisible(false);
            subtitlesBackground.setMouseTransparent(true);
            openSubtitlesResultsPane.scrollPane.setVisible(false);
            openSubtitlesResultsPane.scrollPane.setMouseTransparent(true);
            openSubtitlesResultsPane.scrollPane.setOpacity(1);
            clip.setHeight(subtitlesHome.scrollPane.getHeight());
            clip.setWidth(subtitlesHome.scrollPane.getWidth());

            openSubtitlesResultsPane.clearResults();
        });

        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.play();
        animating.set(true);
    }

    public void closeSubtitlesFromTimingPane(){
        FadeTransition backgroundTranslate = new FadeTransition(Duration.millis(ANIMATION_SPEED), subtitlesBackground);
        backgroundTranslate.setFromValue(1);
        backgroundTranslate.setToValue(0);

        FadeTransition timingTransition = new FadeTransition(Duration.millis(ANIMATION_SPEED), timingPane.container);
        timingTransition.setFromValue(1);
        timingTransition.setToValue(0);

        ParallelTransition parallelTransition = new ParallelTransition(backgroundTranslate, timingTransition);
        parallelTransition.setOnFinished((e) -> {
            animating.set(false);

            subtitlesBuffer.setMouseTransparent(true);
            subtitlesBackground.setVisible(false);
            subtitlesBackground.setMouseTransparent(true);
            timingPane.container.setVisible(false);
            timingPane.container.setMouseTransparent(true);
            timingPane.container.setOpacity(1);
            clip.setHeight(subtitlesHome.scrollPane.getHeight());
            clip.setWidth(subtitlesHome.scrollPane.getWidth());

            openSubtitlesResultsPane.clearResults();
        });

        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.play();
        animating.set(true);
    }

    public void handleFocusForward() {
        switch (subtitlesState){
            case HOME_OPEN -> subtitlesHome.focusForward();
            case OPENSUBTITLES_OPEN -> openSubtitlesPane.focusForward();
            case CAPTIONS_OPTIONS_OPEN -> subtitlesOptionsPane.focusForward();
            case OPENSUBTITLES_RESULTS_OPEN -> openSubtitlesResultsPane.focusForward();
            case TIMING_OPEN -> timingPane.focusForward();
            case OPACITY_OPEN -> subtitlesOptionsPane.fontOpacityPane.focusForward();
            case FONT_SIZE_OPEN -> subtitlesOptionsPane.fontSizePane.focusForward();
            case FONT_COLOR_OPEN -> subtitlesOptionsPane.fontColorPane.focusForward();
            case FONT_FAMILY_OPEN -> subtitlesOptionsPane.fontFamilyPane.focusForward();
            case LINE_SPACING_OPEN -> subtitlesOptionsPane.lineSpacingPane.focusForward();
            case TEXT_ALIGNMENT_OPEN -> subtitlesOptionsPane.textAlignmentPane.focusForward();
            case BACKGROUND_COLOR_OPEN -> subtitlesOptionsPane.backgroundColorPane.focusForward();
            case BACKGROUND_OPACITY_OPEN -> subtitlesOptionsPane.backgroundOpacityPane.focusForward();
        }
    }

    public void handleFocusBackward() {
        switch (subtitlesState){
            case HOME_OPEN -> subtitlesHome.focusBackward();
            case OPENSUBTITLES_OPEN -> openSubtitlesPane.focusBackward();
            case CAPTIONS_OPTIONS_OPEN -> subtitlesOptionsPane.focusBackward();
            case OPENSUBTITLES_RESULTS_OPEN -> openSubtitlesResultsPane.focusBackward();
            case TIMING_OPEN -> timingPane.focusBackward();
            case OPACITY_OPEN -> subtitlesOptionsPane.fontOpacityPane.focusBackward();
            case FONT_SIZE_OPEN -> subtitlesOptionsPane.fontSizePane.focusBackward();
            case FONT_COLOR_OPEN -> subtitlesOptionsPane.fontColorPane.focusBackward();
            case FONT_FAMILY_OPEN -> subtitlesOptionsPane.fontFamilyPane.focusBackward();
            case LINE_SPACING_OPEN -> subtitlesOptionsPane.lineSpacingPane.focusBackward();
            case TEXT_ALIGNMENT_OPEN -> subtitlesOptionsPane.textAlignmentPane.focusBackward();
            case BACKGROUND_COLOR_OPEN -> subtitlesOptionsPane.backgroundColorPane.focusBackward();
            case BACKGROUND_OPACITY_OPEN -> subtitlesOptionsPane.backgroundOpacityPane.focusBackward();
        }
    }
}
