package hans;


import com.jfoenix.controls.JFXButton;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.animation.Animation;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.*;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.jcodec.codecs.common.biari.BitIO;

import java.io.File;
import java.util.ArrayList;

public class QueueItem extends GridPane implements MenuObject{

    // layout constraints for the video item
    ColumnConstraints column1 = new ColumnConstraints(70,70,70);
    ColumnConstraints column2 = new ColumnConstraints(0,100,Double.MAX_VALUE);
    ColumnConstraints column3 = new ColumnConstraints(35,35,35);
    ColumnConstraints column4 = new ColumnConstraints(35,35,35);

    RowConstraints row1 = new RowConstraints(70, 70, 70);


    Button playButton = new Button();
    Label videoTitle = new Label();

    HBox subTextWrapper = new HBox();
    Label artist = new Label();
    Label duration = new Label();

    VBox textWrapper = new VBox();

    JFXButton optionsButton = new JFXButton();

    JFXButton removeButton = new JFXButton();

    MediaItem mediaItem;

    MenuController menuController;

    Region optionsIcon;

    Region playIcon;
    ImageView coverImage = new ImageView();

    Region removeIcon;

    StackPane playButtonWrapper = new StackPane();
    StackPane removeButtonWrapper = new StackPane();
    StackPane optionsButtonWrapper = new StackPane();


    Text playText = new Text();

    int videoIndex;


    int newPosition; // keeps track of the position where the queueitem should move to when being dragged
    double runningTranslate; // mirrors draggable nodes translateY value and if it goes over QueueItem.height or below -QueueItem.height will update the visual order of queueitems

    ControlTooltip play, remove, options;

    boolean mouseHover = false;



    SVGPath playSVG, removeSVG, optionsSVG;

    MenuItemOptionsPopUp optionsPopUp;

    MediaInterface mediaInterface;

    static double height = 70;

    QueueBox queueBox;

    double dragPosition = 0;
    double minimumY = 0;
    double maximumY = 0;

    StackPane captionsPane;
    Region captionsIcon;
    SVGPath captionsPath;

    QueueItem(MediaItem mediaItem, MenuController menuController, MediaInterface mediaInterface, QueueBox queueBox) {

        this.mediaItem = mediaItem;
        this.menuController = menuController;
        this.mediaInterface = mediaInterface;
        this.queueBox = queueBox;

        column2.setHgrow(Priority.ALWAYS); // makes the middle column (video title text) take up all available space
        this.getColumnConstraints().addAll(column1, column2, column3, column4);
        this.getRowConstraints().addAll(row1);

        GridPane.setValignment(playButtonWrapper, VPos.CENTER);
        GridPane.setValignment(textWrapper, VPos.CENTER);
        GridPane.setValignment(removeButtonWrapper, VPos.CENTER);
        GridPane.setValignment(optionsButtonWrapper, VPos.CENTER);

        GridPane.setHalignment(playButtonWrapper, HPos.CENTER);
        GridPane.setHalignment(textWrapper, HPos.LEFT);
        GridPane.setHalignment(optionsButtonWrapper, HPos.CENTER);
        GridPane.setHalignment(removeButtonWrapper, HPos.CENTER);

        this.getStyleClass().add("queueItem");
        this.setOpacity(0);

        coverImage.setFitHeight(50);
        coverImage.setFitWidth(50);
        coverImage.setSmooth(true);
        coverImage.setImage(mediaItem.getCover());
        coverImage.setPreserveRatio(true);

        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(15);
        dropShadow.setSpread(0.5);

        playText.setText(String.valueOf(videoIndex));
        playText.setId("playText");
        playText.setMouseTransparent(true);
        playText.setEffect(dropShadow);

        playButton.setPrefWidth(40);
        playButton.setPrefHeight(40);
        playButton.getStyleClass().add("playButton");
        playButton.setCursor(Cursor.HAND);


        playSVG = new SVGPath();
        playSVG.setContent(App.svgMap.get(SVG.PLAY_CIRCLE));


        removeSVG = new SVGPath();
        removeSVG.setContent(App.svgMap.get(SVG.REMOVE));

        optionsSVG = new SVGPath();
        optionsSVG.setContent(App.svgMap.get(SVG.OPTIONS));

        playIcon = new Region();
        playIcon.setShape(playSVG);
        playIcon.setMinSize(40, 40);
        playIcon.setPrefSize(40, 40);
        playIcon.setMaxSize(40, 40);
        playIcon.setMouseTransparent(true);
        playIcon.getStyleClass().add("menuIcon");
        playIcon.setVisible(false);
        // TODO: create semi-transparent dark background for the playicon

        StackPane iconBackground = new StackPane();

        if(mediaItem.getCover() != null) {
            double aspectRatio = mediaItem.getCover().getWidth() / mediaItem.getCover().getHeight();
            double realWidth = Math.min(coverImage.getFitWidth(), coverImage.getFitHeight() * aspectRatio);
            double realHeight = Math.min(coverImage.getFitHeight(), coverImage.getFitWidth() / aspectRatio);

            iconBackground.setMinSize(realWidth, realHeight);
            iconBackground.setPrefSize(realWidth, realHeight);
            iconBackground.setMaxSize(realWidth, realHeight);
        }
        else {
            iconBackground.setMinSize(0, 0);
            iconBackground.setPrefSize(0, 0);
            iconBackground.setMaxSize(0, 0);
        }

        iconBackground.getStyleClass().add("iconBackground");
        iconBackground.setMouseTransparent(true);

        iconBackground.visibleProperty().bind(playIcon.visibleProperty());


        playButtonWrapper.getChildren().addAll(coverImage,iconBackground, playText, playButton, playIcon);

        videoTitle.getStyleClass().add("videoTitle");

       if(mediaItem.getTitle() == null){
            videoTitle.setText(mediaItem.getFile().getName());
        }
        else {
            videoTitle.setText(mediaItem.getTitle());
        }
        videoTitle.setWrapText(true);
        videoTitle.setMaxHeight(40);

        artist.setText(mediaItem.getArtist());
        artist.getStyleClass().add("subText");

        captionsPane = new StackPane();
        captionsPane.setMinSize(21, 14);
        captionsPane.setPrefSize(21, 14);
        captionsPane.setMaxSize(21, 14);
        captionsPane.setPadding(new Insets(1, 6, 1, 0));
        captionsPane.setMouseTransparent(true);

        captionsIcon = new Region();
        captionsIcon.setId("captionsSelectedIcon");
        captionsIcon.setMinSize(15, 12);
        captionsIcon.setPrefSize(15,12);
        captionsIcon.setMaxSize(15, 12);

        captionsPath = new SVGPath();
        captionsPath.setContent(App.svgMap.get(SVG.CAPTIONS));

        captionsIcon.setShape(captionsPath);
        captionsPane.getChildren().add(captionsIcon);


        String formattedDuration = Utilities.getTime(mediaItem.getDuration());

        if(artist.getText() != null){
            formattedDuration = " • " + formattedDuration;
        }

        if(mediaItem.getDuration() != null) duration.setText(formattedDuration);
        duration.getStyleClass().add("subText");

        subTextWrapper.setAlignment(Pos.CENTER_LEFT);
        subTextWrapper.getChildren().addAll(artist, duration);

        if(mediaItem.getSubtitles() != null) subTextWrapper.getChildren().add(0, captionsPane);


        textWrapper.setAlignment(Pos.CENTER_LEFT);
        textWrapper.setPrefHeight(70);
        textWrapper.getChildren().addAll(videoTitle,subTextWrapper);

        artist.maxWidthProperty().bind(textWrapper.widthProperty().subtract(duration.widthProperty()).subtract(captionsPane.widthProperty()));

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

        optionsButton.setOnAction((e) -> {
            if(optionsPopUp.isShowing()) optionsPopUp.hide();
            else optionsPopUp.showOptions();
        });

        this.setOnMouseClicked(e -> {
            if(optionsPopUp.isShowing()) optionsPopUp.hide();
        });

        this.setOnContextMenuRequested(e -> optionsPopUp.show(this, e.getScreenX(), e.getScreenY()));

        optionsIcon = new Region();
        optionsIcon.setShape(optionsSVG);
        optionsIcon.setMinSize(4, 17);
        optionsIcon.setPrefSize(4, 17);
        optionsIcon.setMaxSize(4, 17);
        optionsIcon.setMouseTransparent(true);
        optionsIcon.getStyleClass().add("menuIcon");

        optionsButtonWrapper.getChildren().addAll(optionsButton, optionsIcon);

        this.setPadding(new Insets(0, 10, 0, 0));

        this.add(playButtonWrapper, 0, 0);
        this.add(textWrapper, 1, 0);
        this.add(removeButtonWrapper, 2, 0);
        this.add(optionsButtonWrapper, 3, 0);

        this.setViewOrder(1);


        this.setOnMouseEntered((e) -> {
            mouseHover = true;

            playText.setVisible(false);
            playIcon.setVisible(true);

            this.setStyle("-fx-background-color: #2C2C2C;");


        });

        this.addEventHandler(DragEvent.DRAG_OVER, e -> {

            if(queueBox.dragAndDropActive) {
                //code to handle adding items to queue
                if (e.getY() > height / 2) {
                    // position queueline below this item
                    if (queueBox.getChildren().indexOf(queueBox.queueLine) != menuController.queue.indexOf(this) + 1) {
                        queueBox.queueLine.setPosition(menuController.queue.indexOf(this) + 1);
                    }
                } else {
                    if (queueBox.getChildren().indexOf(queueBox.queueLine) != menuController.queue.indexOf(this)) {
                        queueBox.queueLine.setPosition(menuController.queue.indexOf(this));
                    }
                }
            }
        });


        this.setOnDragDetected((e) -> {
            if(!queueBox.dragAnimationsInProgress.isEmpty()) return;

            this.setMouseTransparent(true);
            queueBox.dragActive = true;
            queueBox.draggedNode = this;

            this.setViewOrder(0);
            this.setStyle("-fx-background-color: #2C2C2C;");

            if (optionsPopUp.isShowing()) optionsPopUp.hide();

            dragPosition = e.getY();
            minimumY = this.getBoundsInParent().getMinY(); // this is the maximum negative translation that can be applied
            maximumY = queueBox.getChildren().get(queueBox.getChildren().size() - 1).getBoundsInParent().getMinY(); // the top border of the last element inside the vbox, dragged node cant move below that

            newPosition = videoIndex - 1;

            this.startFullDrag();
        });



        this.setOnMouseExited((e) -> {
            mouseHover = false;

            playText.setVisible(true);
            playIcon.setVisible(false);

            if(!queueBox.dragActive) this.setStyle("-fx-background-color: transparent;");
        });


        playButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> {
               // AnimationsClass.queuePlayHoverOn(playButtonWrapper);

                AnimationsClass.parallelAnimation(true,AnimationsClass.scaleAnimation(100, playIcon, 1, 1.1, 1, 1.1, false, 1, false),AnimationsClass.scaleAnimation(100, playButton, 1, 1.1, 1, 1.1, false, 1, false));

        });

        playButton.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> {
            //AnimationsClass.queuePlayHoverOff(playButtonWrapper);

            AnimationsClass.parallelAnimation(true,AnimationsClass.scaleAnimation(100, playIcon, 1.1, 1, 1.1, 1, false, 1, false),AnimationsClass.scaleAnimation(100, playButton, 1.1, 1, 1.1, 1, false, 1, false));

        });

        playButton.setOnAction((e) -> {
            if(!menuController.animationsInProgress.isEmpty()) return;
            this.play(true);
        });

        optionsButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> {
            AnimationsClass.fadeAnimation(200, optionsButton, 0, 1, false, 1, true);
        });

        optionsButton.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> {
            AnimationsClass.fadeAnimation(200, optionsButton, 1, 0, false, 1, true);
        });

        removeButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> {
            AnimationsClass.fadeAnimation(200, removeButton, 0, 1, false, 1, true);
        });

        removeButton.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> {
            AnimationsClass.fadeAnimation(200, removeButton, 1, 0, false, 1, true);
        });

        removeButton.setOnAction((e) -> {

            if(!menuController.animationsInProgress.isEmpty()) return;

            queueBox.remove(this);

        });

    }

    public void updateIndex(int i){
        videoIndex = i + 1;
        playText.setText(String.valueOf(videoIndex));
    }

    public void play(boolean addToHistory){

        if(mediaInterface.transitionTimer != null && mediaInterface.transitionTimer.getStatus() == Animation.Status.RUNNING){
            mediaInterface.transitionTimer.stop();
            mediaInterface.transitionTimer = null;
        }

        if(menuController.historyBox.index == -1 && menuController.mediaActive.get() && addToHistory){
            // add active item to history

            HistoryItem historyItem = new HistoryItem(menuController.activeItem.getMediaItem(), menuController, mediaInterface, menuController.historyBox);

            menuController.historyBox.add(historyItem);
        }
        else if(addToHistory && menuController.historyBox.index != -1){
            HistoryItem historyItem = menuController.history.get(menuController.historyBox.index);
            historyItem.setInactive();
        }

        ActiveItem activeItem = new ActiveItem(getMediaItem(), menuController, mediaInterface, menuController.activeBox);

        if(menuController.mediaActive.get()) mediaInterface.resetMediaPlayer();

        menuController.activeBox.set(activeItem, true);

        if(menuController.settingsController.playbackOptionsController.shuffleOn || menuController.queue.indexOf(this) == menuController.queue.size() -1){
            queueBox.remove(this);
        }
        else {
            queueBox.removeAndMove(queueBox.getChildren().indexOf(this));
        }
    }

    @Override
    public void showMetadata(){

        System.out.println("Showing metadata\n");

    }

    @Override
    public void addSubtitles(File file) {
        this.getMediaItem().setSubtitles(file);
        this.getMediaItem().setSubtitlesOn(true);

        if(!subTextWrapper.getChildren().contains(captionsPane))subTextWrapper.getChildren().add(0, captionsPane);
        //make subtitles selected icon active
    }

    @Override
    public MenuController getMenuController() {
        return menuController;
    }

    @Override
    public void playNext(){
        if(videoIndex > 1) queueBox.move(videoIndex -1, 0);

        menuController.notificationText.setText("Video will play next");
        AnimationsClass.openMenuNotification(menuController);
    }

    @Override
    public Button getOptionsButton() {
        return this.optionsButton;
    }

    @Override
    public MediaItem getMediaItem() {
        return this.mediaItem;
    }

}
