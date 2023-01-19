package hans.Menu;

import com.jfoenix.controls.JFXButton;
import hans.*;
import hans.Chapters.ChapterFrameGrabberTask;
import hans.Chapters.ChapterItem;
import hans.MediaItems.MediaItem;
import javafx.animation.Animation;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;
import org.bytedeco.javacv.FFmpegFrameGrabber;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.bytedeco.ffmpeg.global.avformat.AV_DISPOSITION_DEFAULT;


public class ActiveItem extends GridPane implements MenuObject {

    // layout constraints for the video item
    ColumnConstraints column1 = new ColumnConstraints(45, 45, 45);
    ColumnConstraints column2 = new ColumnConstraints(129,129,129);
    ColumnConstraints column3 = new ColumnConstraints(0,100,Double.MAX_VALUE);
    ColumnConstraints column4 = new ColumnConstraints(35,35,35);
    ColumnConstraints column5 = new ColumnConstraints(35,35,35);

    RowConstraints row1 = new RowConstraints(90, 90, 90);


    Button playButton = new Button();
    public Label videoTitle = new Label();

    public HBox subTextWrapper = new HBox();
    Label artist = new Label();
    Label duration = new Label();

    VBox textWrapper = new VBox();

    JFXButton optionsButton = new JFXButton();

    JFXButton removeButton = new JFXButton();


    MenuController menuController;

    Region optionsIcon, playIcon, removeIcon, captionsIcon;

    ImageView coverImage = new ImageView();


    StackPane playButtonWrapper = new StackPane();
    StackPane removeButtonWrapper = new StackPane();
    StackPane optionsButtonWrapper = new StackPane();
    StackPane iconBackground;

    public StackPane captionsPane;

    ControlTooltip playButtonTooltip, removeButtonTooltip, optionsButtonTooltip;

    boolean mouseHover = false;


    SVGPath playSVG, pauseSVG, removeSVG, optionsSVG, captionsPath;

    // the options popup for this queue item
    public ActiveItemContextMenu activeItemContextMenu;

    MediaInterface mediaInterface;

    ActiveBox activeBox;

    Columns columns = new Columns();

    public File file;
    MediaItem mediaItem;
    BooleanProperty mediaItemGenerated = new SimpleBooleanProperty(false);

    public String captionGenerationTime = "";


    // this constructor will be used when playing media directly and not through the queue
    public ActiveItem(File file, MenuController menuController, MediaInterface mediaInterface, ActiveBox activeBox){
        this.file = file;
        this.menuController = menuController;
        this.mediaInterface = mediaInterface;
        this.activeBox = activeBox;

        initialize();

        MediaItemTask mediaItemTask = new MediaItemTask(this.file, menuController);

        mediaItemTask.setOnSucceeded((succeededEvent) -> {
            this.mediaItem = mediaItemTask.getValue();
            applyMediaItem();
            mediaItemGenerated.set(true);

            if(menuController.activeItem == this){
                menuController.activeMediaItemGenerated.set(true);

                if(mediaItem.hasVideo()){

                    mediaInterface.fFmpegFrameGrabber = new FFmpegFrameGrabber(file);
                    mediaInterface.fFmpegFrameGrabber.setVideoDisposition(AV_DISPOSITION_DEFAULT);
                    mediaInterface.fFmpegFrameGrabber.setVideoOption("vcodec", "copy");

                    double ratio;
                    Image image = null;
                    if(menuController.mainController.videoImageView.getImage() != null){
                        image = menuController.mainController.videoImageView.getImage();
                        ratio = image.getWidth() / image.getHeight();
                    }
                    else if(menuController.mainController.miniplayerActive && menuController.mainController.miniplayer.miniplayerController.videoImageView.getImage() != null){
                        image = menuController.mainController.miniplayer.miniplayerController.videoImageView.getImage();
                        ratio = image.getWidth() / image.getHeight();
                    }
                    else {
                        ratio = mediaItem.width/mediaItem.height;
                    }

                    int newWidth = (int) Math.min(160, 90 * ratio);
                    int newHeight = (int) Math.min(90, 160/ratio);

                    mediaInterface.fFmpegFrameGrabber.setImageWidth(newWidth);
                    mediaInterface.fFmpegFrameGrabber.setImageHeight(newHeight);

                    try {
                        mediaInterface.fFmpegFrameGrabber.start();
                    } catch (FFmpegFrameGrabber.Exception e) {
                        e.printStackTrace();
                    }

                    if(!menuController.chapterController.chapterPage.chapterBox.getChildren().isEmpty()){
                        ExecutorService executorService = Executors.newFixedThreadPool(1);
                        for(Node node : menuController.chapterController.chapterPage.chapterBox.getChildren()){
                            ChapterItem chapterItem = (ChapterItem) node;
                            Duration startTime = chapterItem.startTime;
                            ChapterFrameGrabberTask chapterFrameGrabberTask;
                            if(startTime.greaterThan(Duration.ZERO)) chapterFrameGrabberTask = new ChapterFrameGrabberTask(mediaInterface.fFmpegFrameGrabber, startTime.toSeconds()/menuController.controlBarController.durationSlider.getMax());
                            else {
                                Duration endTime = chapterItem.endTime;
                                chapterFrameGrabberTask = new ChapterFrameGrabberTask(mediaInterface.fFmpegFrameGrabber, (Math.min(endTime.toSeconds()/10, 5))/menuController.controlBarController.durationSlider.getMax());
                            }
                            chapterFrameGrabberTask.setOnSucceeded((event) -> chapterItem.coverImage.setImage(chapterFrameGrabberTask.getValue()));

                            executorService.execute(chapterFrameGrabberTask);
                        }
                        executorService.shutdown();
                    }

                    if(menuController.mainController.miniplayerActive){
                        menuController.mainController.miniplayerActiveText.setVisible(true);
                    }
                }
                else {
                    menuController.mainController.setCoverImageView(this);
                }

                menuController.mainController.videoTitleLabel.setText(this.getTitle());

                mediaInterface.executorService = Executors.newFixedThreadPool(1);
                mediaInterface.subtitleExtractionTask = new SubtitleExtractionTask(menuController.captionsController, this);
                mediaInterface.subtitleExtractionTask.setOnSucceeded(e -> {
                    if(menuController.activeItem == this) menuController.captionsController.createSubtitleTabs(this);
                });
                mediaInterface.executorService.execute(mediaInterface.subtitleExtractionTask);
                mediaInterface.executorService.shutdown();
            }
        });

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(mediaItemTask);
        executorService.shutdown();
    }

    public ActiveItem(QueueItem queueItem, MenuController menuController, MediaInterface mediaInterface, ActiveBox activeBox){
        this.menuController = menuController;
        this.mediaInterface = mediaInterface;
        this.activeBox = activeBox;
        this.file = queueItem.file;

        initialize();

        activeBox.set(this, true);

        if(queueItem.mediaItemGenerated.get()){
            this.mediaItem = queueItem.mediaItem;

            menuController.activeMediaItemGenerated.set(true);

            applyMediaItem();
            mediaItemGenerated.set(true);
        }
        else {
            queueItem.mediaItemGenerated.addListener((observableValue, oldValue, newValue) -> {
                if(newValue){
                    this.mediaItem = queueItem.mediaItem;


                    applyMediaItem();
                    mediaItemGenerated.set(true);

                    if(menuController.activeItem == this){ // if we never enter this code block, it means that this activeitem was skipped over before it could properly load

                        menuController.activeMediaItemGenerated.set(true);

                        if(mediaItem.hasVideo()){

                            mediaInterface.fFmpegFrameGrabber = new FFmpegFrameGrabber(file);
                            mediaInterface.fFmpegFrameGrabber.setVideoDisposition(AV_DISPOSITION_DEFAULT);
                            mediaInterface.fFmpegFrameGrabber.setVideoOption("vcodec", "copy");

                            double ratio;
                            Image image = null;
                            if(menuController.mainController.videoImageView.getImage() != null){
                                image = menuController.mainController.videoImageView.getImage();
                                ratio = image.getWidth() / image.getHeight();
                            }
                            else if(menuController.mainController.miniplayerActive && menuController.mainController.miniplayer.miniplayerController.videoImageView.getImage() != null){
                                image = menuController.mainController.miniplayer.miniplayerController.videoImageView.getImage();
                                ratio = image.getWidth() / image.getHeight();
                            }
                            else {
                                ratio = mediaItem.width/mediaItem.height;
                            }

                            int newWidth = (int) Math.min(160, 90 * ratio);
                            int newHeight = (int) Math.min(90, 160/ratio);

                            mediaInterface.fFmpegFrameGrabber.setImageWidth(newWidth);
                            mediaInterface.fFmpegFrameGrabber.setImageHeight(newHeight);

                            try {
                                mediaInterface.fFmpegFrameGrabber.start();
                            } catch (FFmpegFrameGrabber.Exception e) {
                                e.printStackTrace();
                            }

                            if(!menuController.chapterController.chapterPage.chapterBox.getChildren().isEmpty()){
                                ExecutorService executorService = Executors.newFixedThreadPool(1);
                                for(Node node : menuController.chapterController.chapterPage.chapterBox.getChildren()){
                                    ChapterItem chapterItem = (ChapterItem) node;
                                    Duration startTime = chapterItem.startTime;
                                    ChapterFrameGrabberTask chapterFrameGrabberTask;
                                    if(startTime.greaterThan(Duration.ZERO)) chapterFrameGrabberTask = new ChapterFrameGrabberTask(mediaInterface.fFmpegFrameGrabber, startTime.toSeconds()/menuController.controlBarController.durationSlider.getMax());
                                    else {
                                        Duration endTime = chapterItem.endTime;
                                        chapterFrameGrabberTask = new ChapterFrameGrabberTask(mediaInterface.fFmpegFrameGrabber, (Math.min(endTime.toSeconds()/10, 5))/menuController.controlBarController.durationSlider.getMax());
                                    }
                                    chapterFrameGrabberTask.setOnSucceeded((event) -> chapterItem.coverImage.setImage(chapterFrameGrabberTask.getValue()));

                                    executorService.execute(chapterFrameGrabberTask);
                                }
                                executorService.shutdown();
                            }

                            if(menuController.mainController.miniplayerActive){
                                menuController.mainController.miniplayerActiveText.setVisible(true);
                            }
                        }
                        else {
                            menuController.mainController.setCoverImageView(this);
                        }

                        menuController.mainController.videoTitleLabel.setText(this.getTitle());

                        mediaInterface.executorService = Executors.newFixedThreadPool(1);
                        mediaInterface.subtitleExtractionTask = new SubtitleExtractionTask(menuController.captionsController, this);
                        mediaInterface.subtitleExtractionTask.setOnSucceeded(e -> {
                            if(menuController.activeItem == this) menuController.captionsController.createSubtitleTabs(this);
                        });
                        mediaInterface.executorService.execute(mediaInterface.subtitleExtractionTask);
                        mediaInterface.executorService.shutdown();
                    }
                }
            });
        }
    }

    public ActiveItem(HistoryItem historyItem, MenuController menuController, MediaInterface mediaInterface, ActiveBox activeBox){
        this.menuController = menuController;
        this.mediaInterface = mediaInterface;
        this.activeBox = activeBox;
        this.file = historyItem.file;

        initialize();

        activeBox.set(this, true);

        if(historyItem.mediaItemGenerated.get()){
            this.mediaItem = historyItem.mediaItem;
            this.file = historyItem.file;

            menuController.activeMediaItemGenerated.set(true);

            applyMediaItem();
            mediaItemGenerated.set(true);

        }
        else {
            historyItem.mediaItemGenerated.addListener((observableValue, oldValue, newValue) -> {
                if(newValue){
                    this.mediaItem = historyItem.mediaItem;

                    applyMediaItem();
                    mediaItemGenerated.set(true);

                    if(menuController.activeItem == this){ // if we never enter this code block, it means that this activeitem was skipped over before it could properly load

                        menuController.activeMediaItemGenerated.set(true);

                        if(mediaItem.hasVideo()){

                            mediaInterface.fFmpegFrameGrabber = new FFmpegFrameGrabber(file);
                            mediaInterface.fFmpegFrameGrabber.setVideoDisposition(AV_DISPOSITION_DEFAULT);
                            mediaInterface.fFmpegFrameGrabber.setVideoOption("vcodec", "copy");

                            double ratio;
                            Image image = null;
                            if(menuController.mainController.videoImageView.getImage() != null){
                                image = menuController.mainController.videoImageView.getImage();
                                ratio = image.getWidth() / image.getHeight();
                            }
                            else if(menuController.mainController.miniplayerActive && menuController.mainController.miniplayer.miniplayerController.videoImageView.getImage() != null){
                                image = menuController.mainController.miniplayer.miniplayerController.videoImageView.getImage();
                                ratio = image.getWidth() / image.getHeight();
                            }
                            else {
                                ratio = mediaItem.width/mediaItem.height;
                            }

                            int newWidth = (int) Math.min(160, 90 * ratio);
                            int newHeight = (int) Math.min(90, 160/ratio);

                            mediaInterface.fFmpegFrameGrabber.setImageWidth(newWidth);
                            mediaInterface.fFmpegFrameGrabber.setImageHeight(newHeight);

                            try {
                                mediaInterface.fFmpegFrameGrabber.start();
                            } catch (FFmpegFrameGrabber.Exception e) {
                                e.printStackTrace();
                            }

                            if(!menuController.chapterController.chapterPage.chapterBox.getChildren().isEmpty()){
                                ExecutorService executorService = Executors.newFixedThreadPool(1);
                                for(Node node : menuController.chapterController.chapterPage.chapterBox.getChildren()){
                                    ChapterItem chapterItem = (ChapterItem) node;
                                    Duration startTime = chapterItem.startTime;
                                    ChapterFrameGrabberTask chapterFrameGrabberTask;
                                    if(startTime.greaterThan(Duration.ZERO)) chapterFrameGrabberTask = new ChapterFrameGrabberTask(mediaInterface.fFmpegFrameGrabber, startTime.toSeconds()/menuController.controlBarController.durationSlider.getMax());
                                    else {
                                        Duration endTime = chapterItem.endTime;
                                        chapterFrameGrabberTask = new ChapterFrameGrabberTask(mediaInterface.fFmpegFrameGrabber, (Math.min(endTime.toSeconds()/10, 5))/menuController.controlBarController.durationSlider.getMax());
                                    }
                                    chapterFrameGrabberTask.setOnSucceeded((event) -> chapterItem.coverImage.setImage(chapterFrameGrabberTask.getValue()));

                                    executorService.execute(chapterFrameGrabberTask);
                                }
                                executorService.shutdown();
                            }

                            if(menuController.mainController.miniplayerActive){
                                menuController.mainController.miniplayerActiveText.setVisible(true);
                            }
                        }
                        else {
                            menuController.mainController.setCoverImageView(this);
                        }

                        menuController.mainController.videoTitleLabel.setText(this.getTitle());

                        mediaInterface.executorService = Executors.newFixedThreadPool(1);
                        mediaInterface.subtitleExtractionTask = new SubtitleExtractionTask(menuController.captionsController, this);
                        mediaInterface.subtitleExtractionTask.setOnSucceeded(e -> {
                            if(menuController.activeItem == this) menuController.captionsController.createSubtitleTabs(this);
                        });
                        mediaInterface.executorService.execute(mediaInterface.subtitleExtractionTask);
                        mediaInterface.executorService.shutdown();
                    }
                }
            });
        }
    }

    private void initialize(){
        column3.setHgrow(Priority.ALWAYS); // makes the middle column (video title text) take up all available space
        this.getColumnConstraints().addAll(column1, column2, column3, column4, column5);
        this.getRowConstraints().addAll(row1);

        this.setOpacity(0);

        GridPane.setValignment(columns, VPos.CENTER);
        GridPane.setValignment(playButtonWrapper, VPos.CENTER);
        GridPane.setValignment(textWrapper, VPos.CENTER);
        GridPane.setValignment(removeButtonWrapper, VPos.CENTER);
        GridPane.setValignment(optionsButtonWrapper, VPos.CENTER);

        GridPane.setHalignment(columns, HPos.CENTER);
        GridPane.setHalignment(playButtonWrapper, HPos.CENTER);
        GridPane.setHalignment(textWrapper, HPos.LEFT);
        GridPane.setHalignment(optionsButtonWrapper, HPos.CENTER);
        GridPane.setHalignment(removeButtonWrapper, HPos.CENTER);

        this.getStyleClass().add("queueItem");


        coverImage.setFitHeight(70);
        coverImage.setFitWidth(125);
        coverImage.setSmooth(true);
        coverImage.setPreserveRatio(true);
        String fileExtension = Utilities.getFileExtension(file);
        if(fileExtension.equals("mp4") || fileExtension.equals("mov") || fileExtension.equals("mkv") || fileExtension.equals("flv") || fileExtension.equals("avi")) coverImage.setImage(new Image(Objects.requireNonNull(menuController.mainController.getClass().getResource("images/video.png")).toExternalForm()));
        else if(fileExtension.equals("mp3") || fileExtension.equals("flac") || fileExtension.equals("wav")) coverImage.setImage(new Image(Objects.requireNonNull(menuController.mainController.getClass().getResource("images/music.png")).toExternalForm()));


        playButton.setPrefWidth(125);
        playButton.setPrefHeight(70);
        playButton.getStyleClass().add("playButton");
        playButton.setCursor(Cursor.HAND);


        playSVG = new SVGPath();
        playSVG.setContent(App.svgMap.get(SVG.PLAY));

        pauseSVG = new SVGPath();
        pauseSVG.setContent(App.svgMap.get(SVG.PAUSE));


        removeSVG = new SVGPath();
        removeSVG.setContent(App.svgMap.get(SVG.REMOVE));

        optionsSVG = new SVGPath();
        optionsSVG.setContent(App.svgMap.get(SVG.OPTIONS));

        playIcon = new Region();
        playIcon.setShape(playSVG);
        playIcon.setPrefSize(30, 32);
        playIcon.setMaxSize(30, 32);
        playIcon.setMouseTransparent(true);
        playIcon.setId("playIcon");
        playIcon.setVisible(false);

        iconBackground = new StackPane();
        iconBackground.setPrefSize(125, 70);
        iconBackground.setMaxSize(125, 70);


        iconBackground.getStyleClass().add("iconBackground");
        iconBackground.setMouseTransparent(true);
        iconBackground.setVisible(false);

        playButtonWrapper.setStyle("-fx-background-color: red;");
        playButtonWrapper.getChildren().addAll(coverImage, iconBackground, playButton, playIcon);
        playButtonWrapper.setMaxSize(125,70);



        videoTitle.getStyleClass().add("videoTitle");
        videoTitle.setWrapText(true);
        videoTitle.setMaxHeight(40);

        artist.getStyleClass().add("subText");

        captionsPane = new StackPane();
        captionsPane.setMinSize(21, 14);
        captionsPane.setPrefSize(21, 14);
        captionsPane.setMaxSize(21, 14);
        captionsPane.setPadding(new Insets(1, 6, 1, 0));
        captionsPane.setMouseTransparent(true);

        artist.maxWidthProperty().bind(textWrapper.widthProperty().subtract(duration.widthProperty()).subtract(captionsPane.widthProperty()));

        captionsIcon = new Region();
        captionsIcon.setId("captionsSelectedIcon");
        captionsIcon.setMinSize(15, 12);
        captionsIcon.setPrefSize(15,12);
        captionsIcon.setMaxSize(15, 12);

        captionsPath = new SVGPath();
        captionsPath.setContent(App.svgMap.get(SVG.CAPTIONS));

        captionsIcon.setShape(captionsPath);
        captionsPane.getChildren().add(captionsIcon);


        duration.getStyleClass().add("subText");

        subTextWrapper.setAlignment(Pos.CENTER_LEFT);
        subTextWrapper.getChildren().addAll(duration, artist);


        textWrapper.setAlignment(Pos.CENTER_LEFT);
        textWrapper.setPrefHeight(70);
        textWrapper.getChildren().addAll(videoTitle,subTextWrapper);
        GridPane.setMargin(textWrapper, new Insets(0, 0, 0, 10));

        removeButton.setPrefWidth(30);
        removeButton.setPrefHeight(30);
        removeButton.setRipplerFill(Color.WHITE);
        removeButton.getStyleClass().add("removeButton");
        removeButton.setCursor(Cursor.HAND);
        removeButton.setOpacity(0);
        removeButton.setText(null);

        removeIcon = new Region();
        removeIcon.setShape(removeSVG);
        removeIcon.setMinSize(15, 15);
        removeIcon.setPrefSize(15, 15);
        removeIcon.setMaxSize(15, 15);
        removeIcon.setMouseTransparent(true);
        removeIcon.getStyleClass().add("menuIcon");

        removeButtonWrapper.getChildren().addAll(removeButton, removeIcon);

        optionsButton.setPrefWidth(30);
        optionsButton.setPrefHeight(30);
        optionsButton.setRipplerFill(Color.WHITE);
        optionsButton.getStyleClass().add("optionsButton");
        optionsButton.setCursor(Cursor.HAND);
        optionsButton.setOpacity(0);
        optionsButton.setText(null);
        optionsButton.disableProperty().bind(mediaItemGenerated.not());


        optionsButton.setOnAction((e) -> {
            if(menuController.activeMenuItemContextMenu != null && menuController.activeMenuItemContextMenu.showing) menuController.activeMenuItemContextMenu.hide();
            activeItemContextMenu.showOptions(true);
        });

        this.setOnMouseClicked(e -> {
            if(menuController.activeMenuItemContextMenu != null && menuController.activeMenuItemContextMenu.showing) menuController.activeMenuItemContextMenu.hide();
        });

        optionsIcon = new Region();
        optionsIcon.setShape(optionsSVG);
        optionsIcon.setMinSize(4, 17);
        optionsIcon.setPrefSize(4, 17);
        optionsIcon.setMaxSize(4, 17);
        optionsIcon.setMouseTransparent(true);
        optionsIcon.getStyleClass().add("menuIcon");

        optionsButtonWrapper.getChildren().addAll(optionsButton, optionsIcon);

        this.setPadding(new Insets(0, 10, 0, 0));

        this.add(columns, 0, 0);
        this.add(playButtonWrapper, 1, 0);
        this.add(textWrapper, 2, 0);
        this.add(removeButtonWrapper, 3, 0);
        this.add(optionsButtonWrapper, 4, 0);

        this.getStyleClass().add("activeItem");

        this.setOnMouseEntered((e) -> {
            mouseHover = true;

            // hide the bouncing columns thingy and stop animation
            playIcon.setVisible(true);
            iconBackground.setVisible(true);

            this.setStyle("-fx-background-color: rgba(70,70,70,0.6);");


        });

        this.setOnMouseExited((e) -> {
            mouseHover = false;

            // show bouncing columns and start animation
            if(!activeItemContextMenu.showing) {
                playIcon.setVisible(false);
                iconBackground.setVisible(false);

                this.setStyle("-fx-background-color: transparent;");
            }
        });


        playButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> AnimationsClass.animateBackgroundColor(playIcon, Color.rgb(200, 200, 200), Color.rgb(255, 255, 255), 200));

        playButton.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> AnimationsClass.animateBackgroundColor(playIcon, Color.rgb(255, 255, 255), Color.rgb(200, 200, 200), 200));

        playButton.setOnAction((e) -> {

            if(menuController.activeMenuItemContextMenu != null && menuController.activeMenuItemContextMenu.showing) menuController.activeMenuItemContextMenu.hide();

            if(mediaInterface.atEnd) mediaInterface.replay();
            else if (mediaInterface.playing.get()){
                mediaInterface.wasPlaying = false;
                mediaInterface.pause();
            }
            else mediaInterface.play();
        });

        optionsButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> AnimationsClass.fadeAnimation(200, optionsButton, 0, 1, false, 1, true));

        optionsButton.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> AnimationsClass.fadeAnimation(200, optionsButton, 1, 0, false, 1, true));

        removeButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> AnimationsClass.fadeAnimation(200, removeButton, 0, 1, false, 1, true));

        removeButton.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> AnimationsClass.fadeAnimation(200, removeButton, 1, 0, false, 1, true));

        removeButton.setOnAction((e) -> {
            if(menuController.activeMenuItemContextMenu != null && menuController.activeMenuItemContextMenu.showing) menuController.activeMenuItemContextMenu.hide();
            remove();
        });
    }


    private void applyMediaItem(){
        if(mediaItem.getCover() != null) {
            coverImage.setImage(mediaItem.getCover());
            playButtonWrapper.setStyle("-fx-background-color: rgba(" + Math.round(mediaItem.getCoverBackgroundColor().getRed() * 256) + "," + Math.round(mediaItem.getCoverBackgroundColor().getGreen() * 256) + "," + Math.round(mediaItem.getCoverBackgroundColor().getBlue() * 256) + ", 0.7);");
        }
        else {
            coverImage.setImage(mediaItem.getPlaceholderCover());
        }

        Map<String, String> mediaInformation = mediaItem.getMediaInformation();

        if(mediaInformation != null){
            if(mediaInformation.containsKey("title") && !mediaInformation.get("title").isBlank()){
                videoTitle.setText(mediaInformation.get("title"));
            }
            else {
                videoTitle.setText(mediaItem.getFile().getName());
            }

            if(mediaInformation.containsKey("media_type") && mediaInformation.containsKey("artist")){
                if(mediaInformation.get("media_type").equals("6")){
                    artist.setText(mediaInformation.get("artist"));
                }
            }
            else {
                String fileExtension = Utilities.getFileExtension(mediaItem.getFile());
                if((fileExtension.equals("mp3") || fileExtension.equals("flac") || fileExtension.equals("wav")) && mediaInformation.containsKey("artist")){
                    artist.setText(mediaInformation.get("artist"));
                }
            }
        }

        String formattedDuration = Utilities.getTime(mediaItem.getDuration());

        if(!artist.getText().isEmpty()){
            formattedDuration = formattedDuration + " • ";
        }

        if(mediaItem.getDuration() != null) duration.setText(formattedDuration);
    }

    public void remove() {
        if (menuController.historyBox.index == -1 && !menuController.queue.isEmpty()) {
            // play next item from queue
            QueueItem queueItem = menuController.queue.get(0);
            queueItem.play(false);
        } else if (menuController.historyBox.index != -1 && menuController.historyBox.index < menuController.history.size() - 1) {
            // play next item from history
            HistoryItem historyItem = menuController.history.get(menuController.historyBox.index + 1);
            historyItem.play();
        } else if (menuController.historyBox.index == menuController.historyBox.getChildren().size() - 1 && !menuController.queueBox.getChildren().isEmpty()) {
            // play next item from queue, set last item in history inactive
            QueueItem queueItem = menuController.queue.get(0);
            queueItem.play(true);
        } else {

            if(menuController.historyBox.index != -1) menuController.history.get(menuController.historyBox.index).setInactive();

            mediaInterface.resetMediaPlayer();
            activeBox.clear();
        }
    }


    public void play(boolean addToHistory){

        // called only if this media item is added straight to the mediaplayer, and therefore made active, either by dragging and dropping to the mediaview or via the settings tab

        if(mediaInterface.transitionTimer != null && mediaInterface.transitionTimer.getStatus() == Animation.Status.RUNNING){
            mediaInterface.transitionTimer.stop();
            mediaInterface.transitionTimer = null;
        }

        if(menuController.historyBox.index == -1 && mediaInterface.mediaActive.get() && addToHistory){
            // add active item to history

            HistoryItem historyItem = new HistoryItem(menuController.activeItem, menuController, mediaInterface, menuController.historyBox);

            menuController.historyBox.add(historyItem);
        }
        else if(addToHistory && menuController.historyBox.index != -1){
            HistoryItem historyItem = menuController.history.get(menuController.historyBox.index);
            historyItem.setInactive();
        }

        if(mediaInterface.mediaActive.get()) mediaInterface.resetMediaPlayer();
        else {
            menuController.controlBarController.disablePreviousVideoButton();
            menuController.controlBarController.disableNextVideoButton();
        }
        activeBox.set(this, true);


    }


    public void updateIconToPlay(){

        playIcon.setTranslateX(4);

        playIcon.setPrefSize(30, 32);
        playIcon.setMaxSize(30, 32);
        playIcon.setShape(menuController.activeItem.playSVG);
        if(playButtonTooltip != null) playButtonTooltip.updateText("Play video");
        columns.pause();
    }

    public void updateIconToPause(){

        playIcon.setTranslateX(0);

        playIcon.setPrefSize(30, 30);
        playIcon.setMaxSize(30, 30);
        playIcon.setShape(menuController.activeItem.pauseSVG);
        if(playButtonTooltip != null) playButtonTooltip.updateText("Pause video");
        columns.play();
    }

    public void addSubtitlesIcon(){
        artist.maxWidthProperty().bind(textWrapper.widthProperty().subtract(duration.widthProperty()).subtract(captionsPane.widthProperty()));
        if (!subTextWrapper.getChildren().contains(captionsPane)) subTextWrapper.getChildren().add(0, captionsPane);
    }

    @Override
    public MediaItem getMediaItem() {
        return mediaItem;
    }


    @Override
    public void playNext() {
        // create new queueitem and insert it at index 0
        QueueItem temp = new QueueItem(this.mediaItem, this.menuController, this.mediaInterface, menuController.queueBox);
        menuController.queueBox.add(0, temp);

        menuController.notificationText.setText("Video will play next");
        AnimationsClass.openMenuNotification(menuController);
    }

    @Override
    public Button getOptionsButton() {
        return this.optionsButton;
    }

    @Override
    public void showMetadata() {

        if(menuController.menuInTransition) return;

        menuController.metadataEditPage.enterMetadataEditPage(this);

    }

    @Override
    public void showTechnicalDetails() {
        if(menuController.menuInTransition) return;

        menuController.technicalDetailsPage.enterTechnicalDetailsPage(this);
    }

    @Override
    public MenuController getMenuController() {
        return menuController;
    }

    @Override
    public String getTitle() {
        return videoTitle.getText();
    }

    @Override
    public boolean getHover() {
        return mouseHover;
    }

    @Override
    public void update(){

        if(mediaItem.getCover() != null) {
            coverImage.setImage(mediaItem.getCover());
            playButtonWrapper.setStyle("-fx-background-color: rgba(" + Math.round(mediaItem.getCoverBackgroundColor().getRed() * 256) + "," + Math.round(mediaItem.getCoverBackgroundColor().getGreen() * 256) + "," + Math.round(mediaItem.getCoverBackgroundColor().getBlue() * 256) + ", 0.7);");
        }
        else {
            playButtonWrapper.setStyle("-fx-background-color: red;");
            coverImage.setImage(mediaItem.getPlaceholderCover());
        }

        Map<String, String> mediaInformation = mediaItem.getMediaInformation();
        if(mediaInformation != null){
            if(mediaInformation.containsKey("title") && !mediaInformation.get("title").isBlank()){
                videoTitle.setText(mediaInformation.get("title"));
            }
            else {
                videoTitle.setText(mediaItem.getFile().getName());
            }

            if(mediaInformation.containsKey("media_type") && mediaInformation.get("media_type").equals("6") && mediaInformation.containsKey("artist")){
                artist.setText(mediaInformation.get("artist"));
            }
            else {
                String fileExtension = Utilities.getFileExtension(mediaItem.getFile());
                if((fileExtension.equals("mp3") || fileExtension.equals("flac") || fileExtension.equals("wav")) && mediaInformation.containsKey("artist")){
                    artist.setText(mediaInformation.get("artist"));
                }
            }
        }

        if(!artist.getText().isBlank() && !duration.getText().contains(" • ")){
            duration.setText(duration.getText() + " • ");
        }
        else if(artist.getText().isBlank() && duration.getText().contains(" • ")){
            duration.setText(duration.getText().substring(0, duration.getText().length() - 3));
        }
    }

    @Override
    public BooleanProperty getMediaItemGenerated() {
        return mediaItemGenerated;
    }

}
