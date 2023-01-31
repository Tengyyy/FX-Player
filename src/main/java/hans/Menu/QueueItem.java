package hans.Menu;


import com.jfoenix.controls.JFXButton;
import hans.*;
import hans.MediaItems.MediaItem;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QueueItem extends GridPane {

    // layout constraints for the video item
    ColumnConstraints column1 = new ColumnConstraints(45, 45, 45);
    ColumnConstraints column2 = new ColumnConstraints(129,129,129);
    ColumnConstraints column3 = new ColumnConstraints(0,100, Double.MAX_VALUE);
    ColumnConstraints column4 = new ColumnConstraints(35,35,35);
    ColumnConstraints column5 = new ColumnConstraints(35,35,35);

    public Label videoTitle = new Label();

    HBox subTextWrapper = new HBox();
    Label artist = new Label();
    public Label duration = new Label();

    VBox textWrapper = new VBox();

    JFXButton optionsButton = new JFXButton();

    JFXButton removeButton = new JFXButton();

    MenuController menuController;

    Region optionsIcon;

    ImageView coverImage = new ImageView();

    Region removeIcon;

    StackPane removeButtonWrapper = new StackPane();
    StackPane optionsButtonWrapper = new StackPane();
    StackPane imageWrapper = new StackPane();
    Region imageBorder = new Region();


    StackPane indexPane = new StackPane();
    public Region playIcon = new Region();
    public Label indexLabel = new Label();

    Button playButton = new Button();
    Region playButtonIcon = new Region();

    StackPane playButtonBackground = new StackPane();
    Columns columns = new Columns();

    public StackPane captionsPane = new StackPane();
    public Region captionsIcon = new Region();


    public int newPosition; // keeps track of the position where the queueitem should move to when being dragged
    public double runningTranslate; // mirrors draggable nodes translateY value and if it goes over QueueItem.height or below -QueueItem.height will update the visual order of queueitems

    ControlTooltip playButtonTooltip, removeButtonTooltip, optionsButtonTooltip;

    boolean mouseHover = false;

    SVGPath playSVG = new SVGPath(), removeSVG = new SVGPath(), optionsSVG = new SVGPath(), pauseSVG = new SVGPath(), captionsSVG = new SVGPath();

    //TODO: incorporate activeitemcontextmenu to this
    public MenuItemContextMenu menuItemContextMenu;

    MediaInterface mediaInterface;

    static double height = 90;

    public double dragPosition = 0;
    public double minimumY = 0;
    public double maximumY = 0;


    public File file;
    MediaItem mediaItem;
    BooleanProperty mediaItemGenerated = new SimpleBooleanProperty(false);

    BooleanProperty isActive = new SimpleBooleanProperty(false);

    public int videoIndex = -1;

    QueueBox queueBox;

    public QueueItem(File file, MenuController menuController, MediaInterface mediaInterface) {

        this.file = file;
        this.menuController = menuController;
        this.mediaInterface = mediaInterface;
        this.queueBox = menuController.queueBox;

        initialize();

        QueueItem queueItem = null;
        for(QueueItem item : queueBox.queue){
            if(item.file.getAbsolutePath().equals(this.file.getAbsolutePath())){
                queueItem = item;
                break;
            }
        }

        if(queueItem != null){
            if(queueItem.mediaItemGenerated.get()){
                this.mediaItem = queueItem.mediaItem;
                applyMediaItem();
                mediaItemGenerated.set(true);
            }
            else {
                QueueItem finalQueueItem = queueItem;
                queueItem.mediaItemGenerated.addListener((observableValue, oldValue, newValue) -> {
                    if(newValue){
                        this.mediaItem = finalQueueItem.mediaItem;
                        applyMediaItem();
                        mediaItemGenerated.set(true);
                    }
                });
            }
        }
        else {
            MediaItemTask mediaItemTask = new MediaItemTask(this.file, menuController);

            mediaItemTask.setOnSucceeded((succeededEvent) -> {
                this.mediaItem = mediaItemTask.getValue();
                applyMediaItem();
                mediaItemGenerated.set(true);
            });

            ExecutorService executorService = Executors.newFixedThreadPool(1);
            executorService.execute(mediaItemTask);
            executorService.shutdown();
        }
    }

    QueueItem(MediaItem mediaItem, MenuController menuController, MediaInterface mediaInterface) {

        this.file = mediaItem.getFile();
        this.mediaItem = mediaItem;
        this.menuController = menuController;
        this.mediaInterface = mediaInterface;
        this.queueBox = menuController.queueBox;

        mediaItemGenerated.set(true);

        initialize();
        applyMediaItem();

    }

    private void initialize(){
        column3.setHgrow(Priority.ALWAYS); // makes the middle column (video title text) take up all available space
        this.getColumnConstraints().addAll(column1, column2, column3, column4, column5);

        GridPane.setValignment(indexPane, VPos.CENTER);
        GridPane.setValignment(imageWrapper, VPos.CENTER);
        GridPane.setValignment(textWrapper, VPos.CENTER);
        GridPane.setValignment(removeButtonWrapper, VPos.CENTER);
        GridPane.setValignment(optionsButtonWrapper, VPos.CENTER);

        GridPane.setHalignment(indexPane, HPos.CENTER);
        GridPane.setHalignment(imageWrapper, HPos.CENTER);
        GridPane.setHalignment(textWrapper, HPos.LEFT);
        GridPane.setHalignment(optionsButtonWrapper, HPos.CENTER);
        GridPane.setHalignment(removeButtonWrapper, HPos.CENTER);

        this.getStyleClass().add("queueItem");
        this.setOpacity(0);
        this.setMinHeight(0);
        this.setPrefHeight(0);
        this.setMaxHeight(0);


        playSVG.setContent(App.svgMap.get(SVG.PLAY));
        removeSVG.setContent(App.svgMap.get(SVG.REMOVE));
        optionsSVG.setContent(App.svgMap.get(SVG.OPTIONS));
        pauseSVG.setContent(App.svgMap.get(SVG.PAUSE));
        captionsSVG.setContent(App.svgMap.get(SVG.CAPTIONS));

        coverImage.setFitHeight(70);
        coverImage.setFitWidth(125);
        coverImage.setSmooth(true);
        coverImage.setPreserveRatio(true);
        String fileExtension = Utilities.getFileExtension(file);
        if(fileExtension.equals("mp4") || fileExtension.equals("mov") || fileExtension.equals("mkv") || fileExtension.equals("flv") || fileExtension.equals("avi")) coverImage.setImage(new Image(Objects.requireNonNull(menuController.mainController.getClass().getResource("images/video.png")).toExternalForm()));
        else if(fileExtension.equals("mp3") || fileExtension.equals("flac") || fileExtension.equals("wav")) coverImage.setImage(new Image(Objects.requireNonNull(menuController.mainController.getClass().getResource("images/music.png")).toExternalForm()));


        indexLabel.getStyleClass().add("indexLabel");
        indexLabel.setMouseTransparent(true);
        StackPane.setAlignment(indexLabel, Pos.CENTER);

        playIcon.setShape(playSVG);
        playIcon.setPrefSize(13, 15);
        playIcon.setMaxSize(13, 15);
        playIcon.setId("playIcon");
        playIcon.setVisible(false);
        playIcon.setTranslateX(3);

        this.columns.setVisible(false);

        indexPane.setPrefWidth(45);
        indexPane.setMaxWidth(45);
        indexPane.setAlignment(Pos.CENTER);
        indexPane.getChildren().addAll(indexLabel, playIcon, columns);

        playButton.setPrefWidth(125);
        playButton.setPrefHeight(70);
        playButton.getStyleClass().add("playButton");
        playButton.setCursor(Cursor.HAND);
        playButton.setMouseTransparent(true);


        playButtonIcon = new Region();
        playButtonIcon.setShape(playSVG);
        playButtonIcon.setPrefSize(30, 32);
        playButtonIcon.setMaxSize(30, 32);
        playButtonIcon.setMouseTransparent(true);
        playButtonIcon.setId("playIcon");
        playButtonIcon.setVisible(false);

        playButtonBackground = new StackPane();
        playButtonBackground.setPrefSize(125, 70);
        playButtonBackground.setMaxSize(125, 70);


        playButtonBackground.getStyleClass().add("iconBackground");
        playButtonBackground.setMouseTransparent(true);
        playButtonBackground.setVisible(false);

        imageWrapper.setStyle("-fx-background-color: red;");
        imageWrapper.setPrefSize(129, 74);
        imageWrapper.setMaxSize(129, 74);
        imageWrapper.getChildren().addAll(coverImage, imageBorder, playButtonBackground, playButton, playButtonIcon);
        imageWrapper.getStyleClass().add("imageWrapper");

        imageBorder.setPrefSize(129, 74);
        imageBorder.setMaxSize(129, 74);
        imageBorder.setBackground(Background.EMPTY);
        imageBorder.getStyleClass().add("imageBorder");
        imageBorder.setMouseTransparent(true);
        imageBorder.setVisible(false);

        videoTitle.getStyleClass().add("videoTitle");
        videoTitle.setWrapText(true);
        videoTitle.setMaxHeight(40);

        captionsPane.setMinSize(21, 14);
        captionsPane.setPrefSize(21, 14);
        captionsPane.setMaxSize(21, 14);
        captionsPane.setPadding(new Insets(1, 6, 1, 0));
        captionsPane.setMouseTransparent(true);


        captionsIcon.setId("captionsSelectedIcon");
        captionsIcon.setMinSize(15, 12);
        captionsIcon.setPrefSize(15,12);
        captionsIcon.setMaxSize(15, 12);
        captionsIcon.setShape(captionsSVG);
        captionsPane.getChildren().add(captionsIcon);

        artist.getStyleClass().add("subText");

        duration.getStyleClass().add("subText");

        subTextWrapper.setAlignment(Pos.CENTER_LEFT);
        subTextWrapper.getChildren().addAll(duration, artist);


        textWrapper.setAlignment(Pos.CENTER_LEFT);
        textWrapper.setPrefHeight(90);
        textWrapper.getChildren().addAll(videoTitle,subTextWrapper);
        GridPane.setMargin(textWrapper, new Insets(0, 0, 0, 10));

        artist.maxWidthProperty().bind(textWrapper.widthProperty().subtract(duration.widthProperty()));

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
            if(menuController.activeMenuItemContextMenu != null && menuController.activeMenuItemContextMenu.showing && !menuController.activeMenuItemContextMenu.equals(menuItemContextMenu)) menuController.activeMenuItemContextMenu.hide();
            if(menuItemContextMenu.showing) menuItemContextMenu.hide();
            else menuItemContextMenu.showOptions(true);
        });

        this.setOnMouseClicked(e -> {
            if(menuController.activeMenuItemContextMenu != null && menuController.activeMenuItemContextMenu.showing) menuController.activeMenuItemContextMenu.hide();
            else if (e.getButton() == MouseButton.PRIMARY && !isActive.get()) play();
        });

        optionsIcon = new Region();
        optionsIcon.setShape(optionsSVG);
        optionsIcon.setMinSize(4, 17);
        optionsIcon.setPrefSize(4, 17);
        optionsIcon.setMaxSize(4, 17);
        optionsIcon.setMouseTransparent(true);
        optionsIcon.getStyleClass().add("menuIcon");

        optionsButtonWrapper.getChildren().addAll(optionsButton, optionsIcon);


        this.add(indexPane, 0, 0);
        this.add(imageWrapper, 1, 0);
        this.add(textWrapper, 2, 0);
        this.add(removeButtonWrapper, 3, 0);
        this.add(optionsButtonWrapper, 4, 0);

        this.setPadding(new Insets(0, 10, 0, 0));
        this.setViewOrder(1);

        this.setOnMouseEntered((e) -> {
            mouseHover = true;

            this.setStyle("-fx-background-color: rgba(70,70,70,0.6);");

            if(isActive.get()){
                playButtonIcon.setVisible(true);
                playButtonBackground.setVisible(true);
            }
            else {
                playIcon.setVisible(true);
                indexLabel.setVisible(false);
            }
        });

        this.setOnMouseExited((e) -> {
            mouseHover = false;

            if(queueBox.dragActive || menuItemContextMenu != null && menuItemContextMenu.showing) return;

            if(isActive.get()) this.setStyle("-fx-background-color: rgba(50,50,50,0.6);");
            else this.setStyle("-fx-background-color: transparent;");

            playIcon.setVisible(false);
            playButtonIcon.setVisible(false);
            playButtonBackground.setVisible(false);
            if(!isActive.get()) indexLabel.setVisible(true);
        });

        this.addEventHandler(DragEvent.DRAG_OVER, e -> {

            if(!queueBox.dragAndDropActive) return;

            //code to handle adding items to queue
            if (e.getY() > height / 2) {
                // position queueline below this item
                if (queueBox.getChildren().indexOf(queueBox.queueLine) != this.videoIndex + 1) {
                    queueBox.queueLine.setPosition(this.videoIndex + 1);
                }
            } else {
                if (queueBox.getChildren().indexOf(queueBox.queueLine) != this.videoIndex) {
                    queueBox.queueLine.setPosition(this.videoIndex);
                }
            }
        });


        this.setOnDragDetected((e) -> {

            this.setMouseTransparent(true);
            queueBox.dragActive = true;
            queueBox.draggedNode = this;

            this.setViewOrder(0);
            this.setStyle("-fx-background-color: rgba(70,70,70,0.6);");

            indexLabel.setVisible(false);
            playButtonIcon.setVisible(false);
            playButtonBackground.setVisible(false);

            if(!isActive.get()) playIcon.setVisible(true);


            if (menuItemContextMenu != null && menuItemContextMenu.isShowing()) menuItemContextMenu.hide();

            dragPosition = e.getY();
            minimumY = this.getBoundsInParent().getMinY(); // this is the maximum negative translation that can be applied
            maximumY = queueBox.getChildren().get(queueBox.getChildren().size() - 1).getBoundsInParent().getMinY(); // the top border of the last element inside the vbox, dragged node cant move below that


            this.startFullDrag();
        });

        playButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> AnimationsClass.animateBackgroundColor(playIcon, Color.rgb(200, 200, 200), Color.rgb(255, 255, 255), 200));

        playButton.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> AnimationsClass.animateBackgroundColor(playIcon, Color.rgb(255, 255, 255), Color.rgb(200, 200, 200), 200));

        playButton.setOnAction((e) -> {

            if(menuController.activeMenuItemContextMenu != null && menuController.activeMenuItemContextMenu.showing) menuController.activeMenuItemContextMenu.hide();

            if(!this.isActive.get()) return;

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

    private void remove() {
        if (this.isActive.get()) {

            this.setInactive();
            queueBox.activeItem.set(null);

            if(menuController.settingsController.playbackOptionsController.autoplayOn){
                if(queueBox.queue.size() > this.videoIndex + 1) queueBox.queue.get(this.videoIndex + 1).play();
                else if(this.videoIndex > 0) queueBox.queue.get(this.videoIndex - 1).play();
                else mediaInterface.resetMediaPlayer();
            }
            else mediaInterface.resetMediaPlayer();
        }
        queueBox.remove(this);

    }


    private void applyMediaItem(){
        if(mediaItem == null) return;
        if(mediaItem.getCover() != null) {
            coverImage.setImage(mediaItem.getCover());
            imageWrapper.setStyle("-fx-background-color: rgba(" + Math.round(mediaItem.getCoverBackgroundColor().getRed() * 256) + "," + Math.round(mediaItem.getCoverBackgroundColor().getGreen() * 256) + "," + Math.round(mediaItem.getCoverBackgroundColor().getBlue() * 256) + ", 0.7);");
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

        if(mediaItem.numberOfSubtitleStreams > 0){
            artist.maxWidthProperty().bind(textWrapper.widthProperty().subtract(duration.widthProperty()).subtract(captionsPane.widthProperty()));
            if (!subTextWrapper.getChildren().contains(captionsPane)) subTextWrapper.getChildren().add(0, captionsPane);
        }
    }





    public void updateIndex(int i){
        videoIndex = i;
        indexLabel.setText(String.valueOf(videoIndex + 1));
    }

    public void play(){

        if(queueBox.activeItem.get() == this) return;

        if(mediaInterface.mediaActive.get()) mediaInterface.resetMediaPlayer();
        else {
            menuController.controlBarController.disablePreviousVideoButton();
            menuController.controlBarController.disableNextVideoButton();
        }

        if(queueBox.activeItem.get() != null) queueBox.activeItem.get().setInactive();
        this.setActive();

        menuController.mediaInterface.createMedia(this);

    }

    public void showMetadata(){
        // must be disabled until ffprobe has returned
        if(menuController.menuInTransition) return;

        menuController.metadataEditPage.enterMetadataEditPage(this);
    }

    public void showTechnicalDetails() {
        // must be disabled until ffprobe has returned
        if(menuController.menuInTransition) return;

        menuController.technicalDetailsPage.enterTechnicalDetailsPage(this);
    }


    public MenuController getMenuController() {
        return menuController;
    }

    public String getTitle() {
        return videoTitle.getText();
    }

    public boolean getHover() {
        return mouseHover;
    }


    public void playNext(){
        QueueItem newItem;
        if(this.mediaItemGenerated.get()) newItem = new QueueItem(this.mediaItem, menuController, mediaInterface);
        else newItem = new QueueItem(this.file, menuController, mediaInterface);

        if(queueBox.activeItem.get() != null) queueBox.add(queueBox.activeItem.get().videoIndex + 1, newItem);
        else queueBox.add(0, newItem);
    }

    public Button getOptionsButton() {
        return this.optionsButton;
    }

    public MediaItem getMediaItem() {
        return this.mediaItem;
    }

    public void update(){

        if(mediaItem == null) return;

        if(mediaItem.getCover() != null) {
            coverImage.setImage(mediaItem.getCover());
            if(mediaItem.getCoverBackgroundColor() != null) imageWrapper.setStyle("-fx-background-color: rgba(" + Math.round(mediaItem.getCoverBackgroundColor().getRed() * 255) + "," + Math.round(mediaItem.getCoverBackgroundColor().getGreen() * 255) + "," + Math.round(mediaItem.getCoverBackgroundColor().getBlue() * 255) + ", 0.7);");
        }
        else {
            imageWrapper.setStyle("-fx-background-color: red;");
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

    public BooleanProperty getMediaItemGenerated() {
        return mediaItemGenerated;
    }

    public void setActive(){
        queueBox.activeItem.set(this);
        isActive.set(true);

        playIcon.setVisible(false);
        indexLabel.setVisible(false);
        columns.setVisible(true);
        imageBorder.setVisible(true);
        playButton.setMouseTransparent(false);

        if(mouseHover){
            playButtonIcon.setVisible(true);
            playButtonBackground.setVisible(true);
        }
        else this.setStyle("-fx-background-color: rgba(50,50,50,0.6);");
    }

    public void setInactive(){
        isActive.set(false);

        playButtonIcon.setVisible(false);
        playButtonBackground.setVisible(false);

        columns.setVisible(false);
        columns.pause();
        imageBorder.setVisible(false);
        playButton.setMouseTransparent(true);

        if(mouseHover) playIcon.setVisible(true);
        else {
            indexLabel.setVisible(true);
            this.setStyle("-fx-background-color: transparent;");
        }
    }

    public void updateIconToPlay(){

        playButtonIcon.setTranslateX(4);

        playButtonIcon.setPrefSize(30, 32);
        playButtonIcon.setMaxSize(30, 32);
        playButtonIcon.setShape(playSVG);
        if(playButtonTooltip != null) playButtonTooltip.updateText("Play video");
        columns.pause();
    }

    public void updateIconToPause(){

        playButtonIcon.setTranslateX(0);

        playButtonIcon.setPrefSize(30, 30);
        playButtonIcon.setMaxSize(30, 30);
        playButtonIcon.setShape(pauseSVG);
        if(playButtonTooltip != null) playButtonTooltip.updateText("Pause video");
        columns.play();
    }
}