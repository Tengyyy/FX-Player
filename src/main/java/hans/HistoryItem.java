package hans;

import com.jfoenix.controls.JFXButton;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.animation.Animation;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import org.jcodec.containers.mp4.boxes.ColorExtension;

import java.io.File;

public class HistoryItem extends GridPane implements MenuObject{

    // history item tuleb enne valmis disainida kui saab teha mediainterface korda


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


    MediaItem mediaItem;

    MenuController menuController;

    Region optionsIcon, playIcon, captionsIcon;

    ImageView coverImage = new ImageView();


    StackPane playButtonWrapper = new StackPane();
    StackPane optionsButtonWrapper = new StackPane();

    ControlTooltip playButtonTooltip, optionsButtonTooltip;

    boolean mouseHover = false;
    BooleanProperty isActive = new SimpleBooleanProperty(false);


    SVGPath playSVG, pauseSVG, optionsSVG, captionsPath;

    // the options popup for this queue item
    MenuItemOptionsPopUp optionsPopUp;

    MediaInterface mediaInterface;

    static double height = 72;

    HistoryBox historyBox;

    StackPane captionsPane;


    HistoryItem(MediaItem mediaItem, MenuController menuController, MediaInterface mediaInterface, HistoryBox historyBox){
        this.mediaItem = mediaItem;
        this.menuController = menuController;
        this.mediaInterface = mediaInterface;
        this.historyBox = historyBox;

        isActive.addListener((observableValue, aBoolean, t1) -> {
            if(t1) playIcon.setVisible(true);
            else playIcon.setVisible(false);
        });


        column2.setHgrow(Priority.ALWAYS); // makes the middle column (video title text) to take up all available space
        this.getColumnConstraints().addAll(column1, column2, column3, column4);
        this.getRowConstraints().addAll(row1);

        GridPane.setValignment(playButtonWrapper, VPos.CENTER);
        GridPane.setValignment(textWrapper, VPos.CENTER);
        GridPane.setValignment(optionsButtonWrapper, VPos.CENTER);

        GridPane.setHalignment(playButtonWrapper, HPos.CENTER);
        GridPane.setHalignment(textWrapper, HPos.LEFT);
        GridPane.setHalignment(optionsButtonWrapper, HPos.CENTER);

        this.getStyleClass().add("queueItem");

        this.setOpacity(0);

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

        pauseSVG = new SVGPath();
        pauseSVG.setContent(App.svgMap.get(SVG.PAUSE_CIRCLE));


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


        playButtonWrapper.getChildren().addAll(coverImage,iconBackground, playButton, playIcon);


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
        artist.maxWidthProperty().bind(textWrapper.widthProperty().subtract(duration.widthProperty()));

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
        //videoTitleWrapper.setClip(clip);
        textWrapper.setPrefHeight(70);
        textWrapper.getChildren().addAll(videoTitle,subTextWrapper);

        optionsButton.setPrefWidth(30);
        optionsButton.setPrefHeight(30);
        optionsButton.setRipplerFill(Color.WHITE);
        optionsButton.getStyleClass().add("optionsButton");
        optionsButton.setCursor(Cursor.HAND);
        optionsButton.setOpacity(0);
        optionsButton.setText(null);


        this.setBorder(new Border(new BorderStroke(Color.web("#909090"), Color.web("#909090"), Color.web("#909090"), Color.web("#909090"),
                    BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
                    CornerRadii.EMPTY, new BorderWidths(1), new Insets(0, 1, 0, 1))));



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
        this.add(optionsButtonWrapper, 3, 0);

        this.getStyleClass().add("historyItem");

        this.setOnMouseEntered((e) -> {
            mouseHover = true;

            // hide the bouncing columns thingy and stop animation
            if(!isActive.get()) playIcon.setVisible(true);

            this.setStyle("-fx-background-color: #454545;");


        });

        this.setOnMouseExited((e) -> {
            mouseHover = false;

            // show bouncing columns and start animation
            if(!isActive.get()) playIcon.setVisible(false);

            this.setStyle("-fx-background-color: #353535;");
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
            if(this.isActive.getValue()){
                if(mediaInterface.atEnd) mediaInterface.replay();
                else if (mediaInterface.playing.get()) mediaInterface.pause();
                else mediaInterface.play();
            }
            else {
                if(!menuController.animationsInProgress.isEmpty()) return;
                play();
            }
        });

        optionsButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> {
            AnimationsClass.fadeAnimation(200, optionsButton, 0, 1, false, 1, true);
        });

        optionsButton.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> {
            AnimationsClass.fadeAnimation(200, optionsButton, 1, 0, false, 1, true);
        });

    }

    public void setActive(){

        if(historyBox.index != -1){
            HistoryItem historyItem = menuController.history.get(historyBox.index);
            historyItem.isActive.set(false);
            historyItem.videoTitle.setStyle("-fx-text-fill: white;");
            historyItem.playIcon.setShape(playSVG);

        }

        this.isActive.set(true);
        this.videoTitle.setStyle("-fx-text-fill: red");

        historyBox.index = historyBox.getChildren().indexOf(this);
    }

    public void setInactive(){
        this.isActive.set(false);
        this.videoTitle.setStyle("-fx-text-fill: white");
        playIcon.setShape(playSVG);

        historyBox.index = -1;
    }

    public void play(){

        if(mediaInterface.transitionTimer != null && mediaInterface.transitionTimer.getStatus() == Animation.Status.RUNNING){
            mediaInterface.transitionTimer.stop();
            mediaInterface.transitionTimer = null;
        }

        if(historyBox.index == -1 && menuController.activeItem != null){
            // add active item to history

            HistoryItem historyItem = new HistoryItem(menuController.activeItem.getMediaItem(), menuController, mediaInterface, historyBox);

            historyBox.add(historyItem);
        }

        ActiveItem newActive = new ActiveItem(this.getMediaItem(), menuController, mediaInterface, menuController.activeBox);

        if(mediaInterface.mediaActive.get()) mediaInterface.resetMediaPlayer();

        menuController.activeBox.set(newActive, true);

        this.setActive();

    }

    public void updateIconToPlay(){
        playIcon.setShape(menuController.activeItem.playSVG);
        playButtonTooltip.updateText("Play video");
    }

    public void updateIconToPause(){
        playIcon.setShape(menuController.activeItem.pauseSVG);
        playButtonTooltip.updateText("Pause video");
    }


    @Override
    public MediaItem getMediaItem() {
        return this.mediaItem;
    }


    @Override
    public void playNext() {
        QueueItem queueItem = new QueueItem(getMediaItem(), menuController, mediaInterface, menuController.queueBox);
        menuController.queueBox.add(0, queueItem);

        menuController.notificationText.setText("Video will play next");
        AnimationsClass.openMenuNotification(menuController);
    }

    @Override
    public Button getOptionsButton() {
        return this.optionsButton;
    }

    @Override
    public void showMetadata() {
        System.out.println("Showing metadata");
    }

    @Override
    public void addSubtitles(File file) {
        this.getMediaItem().setSubtitles(file);
        this.getMediaItem().setSubtitlesOn(true);

        if(!subTextWrapper.getChildren().contains(captionsPane)) subTextWrapper.getChildren().add(0, captionsPane);

        if(menuController.historyBox.index == menuController.history.indexOf(this)){
            // this historyitem is active, have to load captions to the mediaplayer
            // make subtitles selected icon visible for ActiveItem

            menuController.captionsController.loadCaptions(file, true);
        }
    }

    @Override
    public MenuController getMenuController() {
        return menuController;
    }
}
