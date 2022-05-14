package hans;


import com.jfoenix.controls.JFXButton;
import javafx.animation.Animation;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.util.Duration;

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

    ControlTooltip play, remove, options;

    boolean mouseHover = false;



    SVGPath playSVG, removeSVG, optionsSVG;

    MenuItemOptionsPopUp optionsPopUp;

    MediaInterface mediaInterface;

    static double height = 70;

    QueueBox queueBox;

    QueueItem(MediaItem mediaItem, MenuController menuController, MediaInterface mediaInterface, QueueBox queueBox) {

        this.mediaItem = mediaItem;
        this.menuController = menuController;
        this.mediaInterface = mediaInterface;
        this.queueBox = queueBox;

        column2.setHgrow(Priority.ALWAYS); // makes the middle column (video title text) to take up all available space
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


        this.setOnMouseEntered((e) -> {
            mouseHover = true;

            playText.setVisible(false);
            playIcon.setVisible(true);

            this.setStyle("-fx-background-color: #2C2C2C;");


        });

        this.addEventHandler(DragEvent.DRAG_OVER, e -> {
            if(!queueBox.dragActive) return;

            if(e.getY() > height/2){
                // position queueline below this item
                if(queueBox.getChildren().indexOf(queueBox.queueLine) != menuController.queue.indexOf(this) + 1){
                    queueBox.queueLine.setPosition(menuController.queue.indexOf(this) + 1);
                }
            }
            else {


                if(queueBox.getChildren().indexOf(queueBox.queueLine) != menuController.queue.indexOf(this)){
                    queueBox.queueLine.setPosition(menuController.queue.indexOf(this));
                }
            }
        });

        this.setOnMouseExited((e) -> {
            mouseHover = false;

            playText.setVisible(true);
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

        if(menuController.historyBox.index == -1 && menuController.activeItem != null && addToHistory){
            // add active item to history

            HistoryItem historyItem = new HistoryItem(menuController.activeItem.getMediaItem(), menuController, mediaInterface, menuController.historyBox);

            menuController.historyBox.add(historyItem);
        }
        else if(addToHistory && menuController.historyBox.index != -1){
            HistoryItem historyItem = menuController.history.get(menuController.historyBox.index);
            historyItem.setInactive();
        }

        ActiveItem activeItem = new ActiveItem(getMediaItem(), menuController, mediaInterface, menuController.activeBox);

        if(menuController.activeItem != null) mediaInterface.resetMediaPlayer();

        menuController.activeBox.set(activeItem, true);

        if(menuController.settingsController.shuffleOn || menuController.queue.indexOf(this) == menuController.queue.size() -1){
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
    public MenuController getMenuController() {
        return menuController;
    }

    @Override
    public void playNext(){
        if(videoIndex > 1) queueBox.move(videoIndex -1, 0);

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
