package tengy.windows;

import com.github.kokorin.jaffree.Rational;
import com.github.kokorin.jaffree.ffprobe.Format;
import com.github.kokorin.jaffree.ffprobe.Stream;
import com.github.kokorin.jaffree.ffprobe.StreamDisposition;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import tengy.*;
import tengy.mediaItems.MediaItem;
import tengy.menu.MenuController;
import javafx.animation.FadeTransition;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static tengy.Utilities.keyboardFocusOff;
import static tengy.Utilities.keyboardFocusOn;

public class TechnicalDetailsWindow {

    WindowController windowController;
    MainController mainController;
    MenuController menuController;

    StackPane window = new StackPane();

    VBox windowContainer = new VBox();

    StackPane buttonContainer = new StackPane();
    Button mainButton = new Button("Close");

    Region closeButtonIcon = new Region();
    SVGPath closeButtonSVG = new SVGPath();
    Button closeButton = new Button();

    StackPane titleContainer = new StackPane();
    Label title = new Label("Technical details");
    Button chapterEditButton = new Button();
    Button mediaInformationButton = new Button();
    ControlTooltip chapterEditTooltip, mediaInformationTooltip;

    ScrollPane technicalDetailsScroll;

    public VBox content = new VBox();

    public VBox fileBox = new VBox();
    public VBox videoBox = new VBox();
    public VBox audioBox = new VBox();
    public VBox subtitlesBox = new VBox();
    public VBox attachmentsBox = new VBox();

    MediaItem mediaItem = null;

    boolean showing = false;

    IntegerProperty focus = new SimpleIntegerProperty(-1);
    List<Node> focusNodes = new ArrayList<>();

    public TechnicalDetailsWindow(WindowController windowController){
        this.windowController = windowController;
        this.mainController = windowController.mainController;
        this.menuController = mainController.getMenuController();

        mainController.popupWindowContainer.getChildren().add(window);

        window.setAlignment(Pos.TOP_LEFT);

        window.prefWidthProperty().bind(Bindings.max(500, Bindings.min(700, mainController.videoImageViewWrapper.widthProperty().multiply(0.5))));
        window.maxWidthProperty().bind(Bindings.max(500, Bindings.min(700, mainController.videoImageViewWrapper.widthProperty().multiply(0.5))));

        window.prefHeightProperty().bind(Bindings.max(350, Bindings.min(1000, mainController.videoImageViewWrapper.heightProperty().multiply(0.8))));
        window.maxHeightProperty().bind(Bindings.max(350, Bindings.min(1000, mainController.videoImageViewWrapper.heightProperty().multiply(0.8))));

        window.getStyleClass().add("popupWindow");
        window.setVisible(false);
        window.getChildren().addAll(windowContainer, buttonContainer, closeButton);

        StackPane.setAlignment(closeButton, Pos.TOP_RIGHT);
        StackPane.setMargin(closeButton, new Insets(10, 10, 0 ,0));
        closeButton.setPrefWidth(25);
        closeButton.setPrefHeight(25);
        closeButton.getStyleClass().addAll("transparentButton", "popupWindowCloseButton");
        closeButton.setOnAction(e -> this.hide());
        closeButton.setFocusTraversable(false);
        closeButton.setGraphic(closeButtonIcon);
        closeButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(0);
            }
            else{
                keyboardFocusOff(closeButton);
                focus.set(-1);
            }
        });

        closeButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            closeButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        closeButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            closeButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });

        closeButtonSVG.setContent(SVG.CLOSE.getContent());

        closeButtonIcon.setShape(closeButtonSVG);
        closeButtonIcon.setMinSize(13, 13);
        closeButtonIcon.setPrefSize(13, 13);
        closeButtonIcon.setMaxSize(13, 13);
        closeButtonIcon.setMouseTransparent(true);
        closeButtonIcon.getStyleClass().add("graphic");

        content.setPadding(new Insets(15, 30, 15, 30));
        content.setSpacing(15);
        content.getChildren().addAll(fileBox, videoBox, audioBox, subtitlesBox, attachmentsBox);

        fileBox.setSpacing(10);
        videoBox.setSpacing(10);
        audioBox.setSpacing(10);
        subtitlesBox.setSpacing(10);
        attachmentsBox.setSpacing(10);

        technicalDetailsScroll = new ScrollPane() {
            ScrollBar vertical;

            @Override
            protected void layoutChildren() {
                super.layoutChildren();
                if (vertical == null) {
                    vertical = (ScrollBar) lookup(".scroll-bar:vertical");
                    vertical.visibleProperty().addListener((obs, old, val) -> updatePadding(val));
                }
            }
        };

        technicalDetailsScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        technicalDetailsScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        technicalDetailsScroll.getStyleClass().add("menuScroll");
        technicalDetailsScroll.setFitToWidth(true);
        technicalDetailsScroll.setFitToHeight(true);
        technicalDetailsScroll.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        technicalDetailsScroll.setBackground(Background.EMPTY);
        technicalDetailsScroll.setContent(content);


        windowContainer.setPadding(new Insets(15, 0, 0, 0));
        windowContainer.getChildren().addAll(titleContainer, technicalDetailsScroll);
        windowContainer.setSpacing(5);
        StackPane.setMargin(windowContainer, new Insets(0, 0, 70, 0));

        titleContainer.getChildren().addAll(title, chapterEditButton, mediaInformationButton);
        titleContainer.setAlignment(Pos.CENTER_LEFT);
        titleContainer.setPadding(new Insets(5, 0, 5, 15));

        StackPane.setAlignment(title, Pos.CENTER_LEFT);
        title.getStyleClass().addAll("popupWindowTitle", "chapterWindowTitle");

        SVGPath chapterEditSVG = new SVGPath();
        chapterEditSVG.setContent(SVG.CHAPTER.getContent());
        Region chapterEditIcon = new Region();
        chapterEditIcon.setShape(chapterEditSVG);
        chapterEditIcon.getStyleClass().addAll("menuIcon", "graphic");
        chapterEditIcon.setPrefSize(16, 16);
        chapterEditIcon.setMaxSize(16, 16);

        StackPane.setAlignment(chapterEditButton, Pos.CENTER_RIGHT);
        StackPane.setMargin(chapterEditButton, new Insets(0, 110, 0, 0));
        HBox.setMargin(chapterEditButton, new Insets(0, 0, 0, 50));
        chapterEditButton.setFocusTraversable(false);
        chapterEditButton.setGraphic(chapterEditIcon);
        chapterEditButton.getStyleClass().add("menuButton");
        chapterEditButton.setOnAction(e -> {
            chapterEditButton.requestFocus();

            if(mediaItem == null) return;

            mainController.windowController.chapterEditWindow.show(mediaItem);
        });

        chapterEditButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(1);
            }
            else{
                keyboardFocusOff(chapterEditButton);
                focus.set(-1);
            }
        });

        chapterEditButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            chapterEditButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        chapterEditButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            chapterEditButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });



        SVGPath mediaInformationSVG = new SVGPath();
        mediaInformationSVG.setContent(SVG.INFORMATION.getContent());
        Region mediaInformationIcon = new Region();
        mediaInformationIcon.setShape(mediaInformationSVG);
        mediaInformationIcon.getStyleClass().addAll("menuIcon", "graphic");
        mediaInformationIcon.setPrefSize(16, 16);
        mediaInformationIcon.setMaxSize(16, 16);

        StackPane.setAlignment(mediaInformationButton, Pos.CENTER_RIGHT);
        StackPane.setMargin(mediaInformationButton, new Insets(0, 60, 0 , 0));
        mediaInformationButton.setFocusTraversable(false);
        mediaInformationButton.setGraphic(mediaInformationIcon);
        mediaInformationButton.getStyleClass().add("menuButton");
        mediaInformationButton.setOnAction(e -> {
            mediaInformationButton.requestFocus();

            if(mediaItem == null) return;

            mainController.windowController.mediaInformationWindow.show(mediaItem);
        });

        mediaInformationButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                if(chapterEditButton.isVisible()) focus.set(2);
                else focus.set(1);
            }
            else{
                keyboardFocusOff(mediaInformationButton);
                focus.set(-1);
            }
        });

        mediaInformationButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            mediaInformationButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        mediaInformationButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            mediaInformationButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });


        StackPane.setAlignment(buttonContainer, Pos.BOTTOM_CENTER);
        buttonContainer.getChildren().add(mainButton);
        buttonContainer.getStyleClass().add("buttonContainer");
        buttonContainer.setPadding(new Insets(0, 15, 0, 15));
        buttonContainer.setPrefHeight(70);
        buttonContainer.setMaxHeight(70);

        mainButton.getStyleClass().add("menuButton");
        mainButton.setTextAlignment(TextAlignment.CENTER);
        mainButton.setPrefWidth(230);
        mainButton.setFocusTraversable(false);
        mainButton.setOnAction(e -> this.hide());
        mainButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(focusNodes.size() - 1);
            }
            else{
                keyboardFocusOff(mainButton);
                focus.set(-1);
            }
        });

        mainButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            mainButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        mainButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            mainButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });
        StackPane.setAlignment(mainButton, Pos.CENTER_RIGHT);

        Platform.runLater(() -> {
            chapterEditTooltip = new ControlTooltip(mainController, "View chapters", "", chapterEditButton, 1000);
            mediaInformationTooltip = new ControlTooltip(mainController, "Media information", "", mediaInformationButton, 1000);
        });
    }


    public void show(MediaItem mediaItem){

        initializeWindow(mediaItem);

        windowController.updateState(WindowState.TECHNICAL_DETAILS_WINDOW_OPEN);


        this.showing = true;
        window.setVisible(true);

        mainController.popupWindowContainer.setMouseTransparent(false);
        AnimationsClass.fadeAnimation(100, mainController.popupWindowContainer, mainController.popupWindowContainer.getOpacity(), 1, false, 1, true);
    }

    public void hide(){

        this.showing = false;
        this.mediaItem = null;

        focusNodes.clear();

        windowController.windowState = WindowState.CLOSED;

        mainController.popupWindowContainer.setMouseTransparent(true);

        FadeTransition fadeTransition = new FadeTransition(Duration.millis(100), mainController.popupWindowContainer);
        fadeTransition.setFromValue(mainController.popupWindowContainer.getOpacity());
        fadeTransition.setToValue(0);
        fadeTransition.setOnFinished(e -> {
            window.setVisible(false);

            fileBox.getChildren().clear();
            videoBox.getChildren().clear();
            audioBox.getChildren().clear();
            subtitlesBox.getChildren().clear();
            attachmentsBox.getChildren().clear();

            technicalDetailsScroll.setVvalue(0);
        });

        fadeTransition.play();
    }

    private void initializeWindow(MediaItem mediaItem){
        if(mediaItem == null) return;

        this.mediaItem = mediaItem;

        focusNodes.add(closeButton);

        String extension = Utilities.getFileExtension(mediaItem.getFile());
        if(!extension.equals("mp4")
        && !extension.equals("mkv")
        && !extension.equals("mov")
        && !extension.equals("mp3")
        && !extension.equals("opus")
        && !extension.equals("m4a")
        && !extension.equals("wma")
        && !extension.equals("wmv")
        && !extension.equals("mka")
        && !extension.equals("webm")){
            chapterEditButton.setVisible(false);
        }
        else {
            chapterEditButton.setVisible(true);
            focusNodes.add(chapterEditButton);
        }

        focusNodes.add(mediaInformationButton);
        focusNodes.add(mainButton);



        createFileSection(mediaItem);
        if(!mediaItem.videoStreams.isEmpty()) createVideoSection(mediaItem);
        if(!mediaItem.audioStreams.isEmpty()) createAudioSection(mediaItem);
        if(!mediaItem.subtitleStreams.isEmpty()) createSubtitleSection(mediaItem);
        if(!mediaItem.attachmentStreams.isEmpty()) createAttachmentSection(mediaItem);
    }

    private void createTitle(String title, VBox parent){
        Label label = new Label(title);
        label.getStyleClass().add("metadataTitle");
        label.setMaxWidth(Double.MAX_VALUE);
        label.setWrapText(true);
        label.setLineSpacing(5);
        label.setPadding(new Insets(20, 0, 5, 0));

        parent.getChildren().add(label);
    }

    private void createSubHeader(String title, VBox parent){
        Label label = new Label(title);
        label.getStyleClass().add("metadataSubheader");
        label.setMaxWidth(Double.MAX_VALUE);
        label.setWrapText(true);
        label.setLineSpacing(5);
        label.setPadding(new Insets(10, 0, 5, 0));

        parent.getChildren().add(label);
    }

    private Text createItem(String key, String value, VBox parent){
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.TOP_LEFT);

        Label keyText = new Label(key + ":");
        keyText.getStyleClass().add("keyText");
        keyText.setMinWidth(125);
        keyText.setMaxWidth(125);

        TextFlow textFlow = new TextFlow();
        Text valueText = new Text(value);
        valueText.getStyleClass().add("valueText");
        textFlow.getChildren().add(valueText);
        hBox.getChildren().addAll(keyText, textFlow);
        parent.getChildren().add(hBox);

        return valueText;
    }

    private void createFileSection(MediaItem mediaItem){

        createTitle("File", fileBox);

        VBox fileInnerContainer = new VBox();
        fileInnerContainer.setSpacing(10);
        fileInnerContainer.setPadding(new Insets(0, 0, 0, 20));

        fileBox.getChildren().add(fileInnerContainer);

        File file = mediaItem.getFile();

        createItem("File name", file.getName(), fileInnerContainer);

        Text text = createItem("File path", file.getAbsolutePath(), fileInnerContainer);
        text.setCursor(Cursor.HAND);
        text.setOnMouseEntered(e -> text.setUnderline(true));
        text.setOnMouseExited(e -> text.setUnderline(false));

        text.setOnMouseClicked(e -> {
            if(App.isWindows){
                Shell32Util.SHOpenFolderAndSelectItems(file);
            }
            else if(Desktop.isDesktopSupported()){
                Desktop desktop = Desktop.getDesktop();

                if(desktop.isSupported(Desktop.Action.BROWSE_FILE_DIR)){
                    desktop.browseFileDirectory(file);
                }
                else if(desktop.isSupported(Desktop.Action.OPEN)){
                    try {
                        desktop.open(file.getParentFile());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        createItem("File size", Utilities.formatFileSize(file.length()), fileInnerContainer);
        createItem("Last modified", DateFormat.getDateInstance().format(new Date(file.lastModified())), fileInnerContainer);

        Format format = mediaItem.getProbeResult().getFormat();


        createItem("Format", format.getFormatLongName(), fileInnerContainer);

        Long bitrate = format.getBitRate();

        if(bitrate != null) createItem("Bitrate", Utilities.formatBitrate(bitrate), fileInnerContainer);

        Float durationLong = format.getDuration();
        if(durationLong != null) createItem("Duration", Utilities.durationToString(Duration.seconds(durationLong)), fileInnerContainer);

    }

    private void createVideoSection(MediaItem mediaItem){
        createTitle("Video", videoBox);

        VBox videoInnerContainer = new VBox();
        videoInnerContainer.setSpacing(10);
        videoInnerContainer.setPadding(new Insets(0, 0, 0, 20));

        videoBox.getChildren().add(videoInnerContainer);

        if(mediaItem.videoStreams.size() == 1)
            createVideoTrack(0, mediaItem.videoStreams.get(0), videoInnerContainer, false);
        else {
            for (int i = 0; i < mediaItem.videoStreams.size(); i++) {
                Stream videoStream = mediaItem.videoStreams.get(i);
                createVideoTrack(i, videoStream, videoInnerContainer, true);
            }
        }
    }

    private void createVideoTrack(int index, Stream videoStream, VBox parent, boolean createSubheader){

        VBox trackContainer = new VBox();
        trackContainer.setSpacing(10);
        parent.getChildren().add(trackContainer);

        if(createSubheader) {
            boolean isDefault = videoStream.getDisposition().getDefault() == 1;

            String trackTitle = "Track #" + (index + 1);
            if (isDefault) trackTitle += " (Default)";

            createSubHeader(trackTitle, trackContainer);
        }

        Long durationLong = videoStream.getDuration(TimeUnit.SECONDS);
        if(durationLong != null) createItem("Duration", Utilities.durationToString(Duration.seconds(durationLong)), trackContainer);

        String videoCodec = videoStream.getCodecLongName();
        if(videoCodec != null) createItem("Codec", videoCodec,trackContainer);

        Rational rational = videoStream.getAvgFrameRate();
        if(rational != null) createItem("Frame rate", rational.intValue() + " fps", trackContainer);

        Integer width = videoStream.getWidth();
        Integer height = videoStream.getHeight();

        if(width != null && height != null) createItem("Resolution", width + "x" + height, trackContainer);

        Long bitrate = videoStream.getBitRate();

        if(bitrate != null) createItem("Bitrate", Utilities.formatBitrate(bitrate), trackContainer);

        Long bitDepth = videoStream.getBitsPerRawSample();

        if(bitDepth != null) createItem("Bit depth", bitDepth + " bits", trackContainer);

        String pixelFormat = videoStream.getPixFmt();

        if(pixelFormat != null) createItem("Pixel format", pixelFormat, trackContainer);

        String colorRange = videoStream.getColorRange();

        if(colorRange != null) createItem("Color range", colorRange, trackContainer);

        String colorSpace = videoStream.getColorSpace();

        if(colorSpace != null) createItem("Color space", colorSpace, trackContainer);

        String disposition = "";

        StreamDisposition streamDisposition = videoStream.getDisposition();
        if(streamDisposition.getDefault() == 1) disposition += "Default, ";
        if(streamDisposition.getAttachedPic() == 1) disposition += "Attached Picture, ";
        if(streamDisposition.getTimedThumbnails() == 1) disposition += "Timed Thumbnails, ";
        if(streamDisposition.getVisualImpaired() == 1) disposition += "Visual Impaired, ";

        if(!disposition.isEmpty()){
            disposition = disposition.substring(0, disposition.length() - 2);

            createItem("Disposition", disposition, trackContainer);
        }
    }

    private void createAudioSection(MediaItem mediaItem){
        createTitle("Audio", audioBox);

        VBox audioInnerContainer = new VBox();
        audioInnerContainer.setSpacing(10);
        audioInnerContainer.setPadding(new Insets(0, 0, 0, 20));

        audioBox.getChildren().add(audioInnerContainer);

        if(mediaItem.audioStreams.size() == 1)
            createAudioTrack(0, mediaItem.audioStreams.get(0), audioInnerContainer, false);
        else {
            for (int i = 0; i < mediaItem.audioStreams.size(); i++) {
                Stream audioStream = mediaItem.audioStreams.get(i);
                createAudioTrack(i, audioStream, audioInnerContainer, true);
            }
        }
    }

    private void createAudioTrack(int index, Stream audioStream, VBox parent, boolean createSubheader){

        VBox trackContainer = new VBox();
        trackContainer.setSpacing(10);
        parent.getChildren().add(trackContainer);

        if(createSubheader) {
            boolean isDefault = audioStream.getDisposition().getDefault() == 1;

            String trackTitle = "Track #" + (index + 1);
            if (isDefault) trackTitle += " (Default)";

            createSubHeader(trackTitle, trackContainer);
        }

        String languageCode = audioStream.getTag("language");
        String language;
        if(languageCode == null || languageCode.equals("und")) language = "Undefined";
        else language = Locale.forLanguageTag(languageCode.toUpperCase(Locale.ROOT)).getDisplayLanguage();

        createItem("Language", language, trackContainer);

        Long durationLong = audioStream.getDuration(TimeUnit.SECONDS);
        if(durationLong != null) createItem("Duration", Utilities.durationToString(Duration.seconds(durationLong)), trackContainer);

        String audioCodec = audioStream.getCodecLongName();
        if(audioCodec != null) createItem("Codec", audioCodec,trackContainer);

        Long bitrate = audioStream.getBitRate();

        if(bitrate != null) createItem("Bitrate", Utilities.formatBitrate(bitrate), trackContainer);

        Long bitDepth = audioStream.getBitsPerRawSample();

        if(bitDepth != null) createItem("Bit depth", bitDepth + " bits", trackContainer);

        Integer sampleRate = audioStream.getSampleRate();

        if(sampleRate != null) createItem("Sampling rate", NumberFormat.getInstance().format(sampleRate) + " Hz", trackContainer);

        Integer channels = audioStream.getChannels();
        String channelLayout = audioStream.getChannelLayout();

        if(channels != null){
            if(channelLayout != null && !channelLayout.isEmpty()){
                if(Character.isDigit(channelLayout.charAt(0))) createItem("Channels", channelLayout, trackContainer);
                else createItem("Channels", channels + " (" + channelLayout + ")", trackContainer);
            }
            else createItem("Channels", String.valueOf(channels), trackContainer);
        }

        String disposition = "";

        StreamDisposition streamDisposition = audioStream.getDisposition();
        if(streamDisposition.getDefault() == 1) disposition += "Default, ";
        if(streamDisposition.getHearingImpaired() == 1) disposition += "Hearing Impaired, ";
        if(streamDisposition.getKaraoke() == 1) disposition += "Karaoke, ";
        if(streamDisposition.getLyrics() == 1) disposition += "Lyrics, ";
        if(streamDisposition.getDub() == 1) disposition += "Dub, ";

        if(!disposition.isEmpty()){
            disposition = disposition.substring(0, disposition.length() - 2);

            createItem("Disposition", disposition, trackContainer);
        }

    }

    private void createSubtitleSection(MediaItem mediaItem){
        createTitle("Subtitles", subtitlesBox);

        VBox subtitlesInnerContainer = new VBox();
        subtitlesInnerContainer.setSpacing(10);
        subtitlesInnerContainer.setPadding(new Insets(0, 0, 0, 20));

        subtitlesBox.getChildren().add(subtitlesInnerContainer);

        if(mediaItem.subtitleStreams.size() == 1)
            createSubtitleTrack(0, mediaItem.subtitleStreams.get(0), subtitlesInnerContainer, false);
        else {
            for (int i = 0; i < mediaItem.subtitleStreams.size(); i++) {
                Stream subtitleStream = mediaItem.subtitleStreams.get(i);
                createSubtitleTrack(i, subtitleStream, subtitlesInnerContainer, true);
            }
        }
    }

    private void createSubtitleTrack(int index, Stream subtitleStream, VBox parent, boolean createSubheader){

        VBox trackContainer = new VBox();
        trackContainer.setSpacing(10);
        parent.getChildren().add(trackContainer);

        boolean isDefault = subtitleStream.getDisposition().getDefault() == 1;

        if(createSubheader) {
            String trackTitle = "Track #" + (index + 1);
            if (isDefault) trackTitle += " (Default)";

            createSubHeader(trackTitle, trackContainer);
        }

        String languageCode = subtitleStream.getTag("language");
        String language;
        if(languageCode == null || languageCode.equals("und")) language = "Undefined";
        else language = Locale.forLanguageTag(languageCode.toUpperCase(Locale.ROOT)).getDisplayLanguage();

        createItem("Language", language, trackContainer);

        String title = subtitleStream.getTag("title");
        if(title != null) createItem("Title", title, trackContainer);

        Long durationLong = subtitleStream.getDuration(TimeUnit.SECONDS);
        if(durationLong != null) createItem("Duration", Utilities.durationToString(Duration.seconds(durationLong)), trackContainer);

        String subtitleCodec = subtitleStream.getCodecLongName();
        if(subtitleCodec != null) createItem("Codec", subtitleCodec, trackContainer);

        Long bitrate = subtitleStream.getBitRate();
        if(bitrate != null) createItem("Bitrate", Utilities.formatBitrate(bitrate), trackContainer);

        String disposition = "";

        StreamDisposition streamDisposition = subtitleStream.getDisposition();
        if(streamDisposition.getDefault() == 1) disposition += "Default, ";
        if(streamDisposition.getLyrics() == 1) disposition += "Lyrics, ";
        if(streamDisposition.getForced() == 1) disposition+= "Forced, ";

        if(!disposition.isEmpty()){
            disposition = disposition.substring(0, disposition.length() - 2);

            createItem("Disposition", disposition, trackContainer);
        }
    }

    private void createAttachmentSection(MediaItem mediaItem){
        createTitle("Attachments", attachmentsBox);

        VBox attachmentsInnerContainer = new VBox();
        attachmentsInnerContainer.setSpacing(10);
        attachmentsInnerContainer.setPadding(new Insets(0, 0, 0, 20));

        attachmentsBox.getChildren().add(attachmentsInnerContainer);

        for(int i=0; i < mediaItem.attachmentStreams.size(); i++){
            Stream attachmentStream = mediaItem.attachmentStreams.get(i);
            createAttachmentTrack(i, attachmentStream, attachmentsInnerContainer);
        }
    }

    private void createAttachmentTrack(int index, Stream attachmentStream, VBox parent){

        VBox trackContainer = new VBox();
        trackContainer.setSpacing(10);
        parent.getChildren().add(trackContainer);

        createSubHeader("Attachment #" + (index+1), trackContainer);

        String title = attachmentStream.getTag("title");
        if(title != null) createItem("Title", title, trackContainer);

        String description = attachmentStream.getTag("description");
        if(description != null) createItem("Description", description, trackContainer);

        Long durationLong = attachmentStream.getDuration(TimeUnit.SECONDS);
        if(durationLong != null) createItem("Duration", Utilities.durationToString(Duration.seconds(durationLong)), trackContainer);

        String codec = attachmentStream.getCodecLongName();
        if(codec != null) createItem("Codec", codec, trackContainer);

        Long bitrate = attachmentStream.getBitRate();
        if(bitrate != null) createItem("Bitrate", Utilities.formatBitrate(bitrate), trackContainer);
    }

    private void updatePadding(boolean value){
        if(value) content.setPadding(new Insets(15, 18, 15, 30));
        else      content.setPadding(new Insets(15, 30, 15, 30));
    }

    public void focusForward(){
        int newFocus;

        if(focus.get() >= focusNodes.size() - 1 || focus.get() == -1) newFocus = 0;
        else newFocus = focus.get() + 1;

        keyboardFocusOn(focusNodes.get(newFocus));
    }

    public void focusBackward(){
        int newFocus;

        if(focus.get() == 0) newFocus = focusNodes.size() - 1;
        else if(focus.get() == -1) newFocus = 0;
        else newFocus = focus.get() - 1;

        keyboardFocusOn(focusNodes.get(newFocus));
    }
}
