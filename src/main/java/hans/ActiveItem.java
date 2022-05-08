package hans;

import com.jfoenix.controls.JFXButton;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.net.PasswordAuthentication;

public class ActiveItem extends GridPane implements MenuObject {

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

    Region optionsIcon, playIcon, removeIcon;

    ImageView coverImage = new ImageView();


    StackPane playButtonWrapper = new StackPane();
    StackPane removeButtonWrapper = new StackPane();
    StackPane optionsButtonWrapper = new StackPane();

    ControlTooltip play, remove, options;

    boolean mouseHover = false;



    SVGPath playSVG, pauseSVG, removeSVG, optionsSVG;

    // the options popup for this queue item
    MenuItemOptionsPopUp optionsPopUp;

    MediaInterface mediaInterface;

    ActiveBox activeBox;

    ActiveItem(MediaItem mediaItem, MenuController menuController, MediaInterface mediaInterface, ActiveBox activeBox){
        this.mediaItem = mediaItem;
        this.menuController = menuController;
        this.mediaInterface = mediaInterface;
        this.activeBox = activeBox;


        column2.setHgrow(Priority.ALWAYS); // makes the middle column (video title text) to take up all available space
        this.getColumnConstraints().addAll(column1, column2, column3, column4);
        this.getRowConstraints().addAll(row1);

        this.setOpacity(0);

        GridPane.setValignment(playButtonWrapper, VPos.CENTER);
        GridPane.setValignment(textWrapper, VPos.CENTER);
        GridPane.setValignment(removeButtonWrapper, VPos.CENTER);
        GridPane.setValignment(optionsButtonWrapper, VPos.CENTER);

        GridPane.setHalignment(playButtonWrapper, HPos.CENTER);
        GridPane.setHalignment(textWrapper, HPos.LEFT);
        GridPane.setHalignment(optionsButtonWrapper, HPos.CENTER);
        GridPane.setHalignment(removeButtonWrapper, HPos.CENTER);

        this.getStyleClass().add("queueItem");


        coverImage.setFitHeight(50);
        coverImage.setFitWidth(50);
        coverImage.setSmooth(true);
        coverImage.setImage(mediaItem.getCover());
        coverImage.setPreserveRatio(true);

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


        playButtonWrapper.getChildren().addAll(coverImage, playButton, playIcon);


        videoTitle.getStyleClass().add("activeVideoTitle");

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
        artist.maxWidthProperty().bind(textWrapper.widthProperty().subtract(duration.widthProperty()));

        String formattedDuration = Utilities.getTime(mediaItem.getDuration());

        if(artist.getText() != null){
            formattedDuration = " • " + formattedDuration;
        }

        if(mediaItem.getDuration() != null) duration.setText(formattedDuration);
        duration.getStyleClass().add("subText");

        subTextWrapper.setAlignment(Pos.TOP_LEFT);
        subTextWrapper.getChildren().addAll(artist, duration);


        textWrapper.setAlignment(Pos.CENTER_LEFT);
        //videoTitleWrapper.setClip(clip);
        textWrapper.setPrefHeight(70);
        textWrapper.getChildren().addAll(videoTitle,subTextWrapper);

        removeButton.setPrefWidth(30);
        removeButton.setPrefHeight(30);
        removeButton.setRipplerFill(Color.WHITE);
        removeButton.getStyleClass().add("removeButton");
        removeButton.setCursor(Cursor.HAND);
        removeButton.setOpacity(0);

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

        this.getStyleClass().add("activeItem");

        this.setOnMouseEntered((e) -> {
            mouseHover = true;

            // hide the bouncing columns thingy and stop animation
            playIcon.setVisible(true);

            this.setStyle("-fx-background-color: #2C2C2C;");


        });

        this.setOnMouseExited((e) -> {
            mouseHover = false;

            // show bouncing columns and start animation
            playIcon.setVisible(false);

            this.setStyle("-fx-background-color: transparent;");

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
            if(mediaInterface.atEnd) menuController.controlBarController.replayMedia();
            else if (mediaInterface.playing) menuController.controlBarController.pause();
            else menuController.controlBarController.play();
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
            remove();
        });

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

        if(menuController.historyBox.index == -1 && menuController.activeItem != null && addToHistory){
            // add active item to history

            HistoryItem historyItem = new HistoryItem(menuController.activeItem.getMediaItem(), menuController, mediaInterface, menuController.historyBox);

            menuController.historyBox.add(historyItem);
        }
        else if(addToHistory && menuController.historyBox.index != -1){
            HistoryItem historyItem = menuController.history.get(menuController.historyBox.index);
            historyItem.setInactive();
        }

        activeBox.set(this, true);

        if(menuController.activeItem != null) mediaInterface.resetMediaPlayer();

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
    }

    @Override
    public Button getOptionsButton() {
        return this.optionsButton;
    }

    @Override
    public void showMetadata() {
        System.out.println("Showing metadata\n");
    }

    @Override
    public MenuController getMenuController() {
        return menuController;
    }
}
