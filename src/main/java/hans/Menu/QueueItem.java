package hans.Menu;


import com.jfoenix.controls.JFXButton;
import hans.*;
import hans.MediaItems.MediaItem;
import javafx.animation.Animation;
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

public class QueueItem extends GridPane implements MenuObject {

    // layout constraints for the video item
    ColumnConstraints column1 = new ColumnConstraints(45, 45, 45);
    ColumnConstraints column2 = new ColumnConstraints(129,129,129);
    ColumnConstraints column3 = new ColumnConstraints(0,100,Double.MAX_VALUE);
    ColumnConstraints column4 = new ColumnConstraints(35,35,35);
    ColumnConstraints column5 = new ColumnConstraints(35,35,35);

    RowConstraints row1 = new RowConstraints(90, 90, 90);


    public Label videoTitle = new Label();

    HBox subTextWrapper = new HBox();
    Label artist = new Label();
    public Label duration = new Label();

    VBox textWrapper = new VBox();

    JFXButton optionsButton = new JFXButton();

    JFXButton removeButton = new JFXButton();

    MediaItem mediaItem;

    MenuController menuController;

    Region optionsIcon;

    ImageView coverImage = new ImageView();

    Region removeIcon;

    StackPane removeButtonWrapper = new StackPane();
    StackPane optionsButtonWrapper = new StackPane();
    StackPane imageWrapper = new StackPane();


    StackPane indexPane = new StackPane();
    Label indexLabel = new Label();
    int videoIndex;
    Region playIcon = new Region();


    int newPosition; // keeps track of the position where the queueitem should move to when being dragged
    double runningTranslate; // mirrors draggable nodes translateY value and if it goes over QueueItem.height or below -QueueItem.height will update the visual order of queueitems

    ControlTooltip removeButtonTooltip, optionsButtonTooltip;

    boolean mouseHover = false;



    SVGPath playSVG, removeSVG, optionsSVG;

    MenuItemOptionsPopUp optionsPopUp;

    MediaInterface mediaInterface;

    static double height = 90;

    QueueBox queueBox;

    double dragPosition = 0;
    double minimumY = 0;
    double maximumY = 0;

    QueueItem(MediaItem mediaItem, MenuController menuController, MediaInterface mediaInterface, QueueBox queueBox) {

        this.mediaItem = mediaItem;
        this.menuController = menuController;
        this.mediaInterface = mediaInterface;
        this.queueBox = queueBox;

        column3.setHgrow(Priority.ALWAYS); // makes the middle column (video title text) take up all available space
        this.getColumnConstraints().addAll(column1, column2, column3, column4, column5);
        this.getRowConstraints().addAll(row1);

        GridPane.setValignment(imageWrapper, VPos.CENTER);

        GridPane.setValignment(imageWrapper, VPos.CENTER);
        GridPane.setValignment(textWrapper, VPos.CENTER);
        GridPane.setValignment(removeButtonWrapper, VPos.CENTER);
        GridPane.setValignment(optionsButtonWrapper, VPos.CENTER);

        GridPane.setHalignment(imageWrapper, HPos.CENTER);
        GridPane.setHalignment(textWrapper, HPos.LEFT);
        GridPane.setHalignment(optionsButtonWrapper, HPos.CENTER);
        GridPane.setHalignment(removeButtonWrapper, HPos.CENTER);

        this.getStyleClass().add("queueItem");
        this.setOpacity(0);

        this.setCursor(Cursor.HAND);

        coverImage.setFitHeight(70);
        coverImage.setFitWidth(125);
        coverImage.setSmooth(true);
        coverImage.setPreserveRatio(true);


        indexLabel.setText(String.valueOf(videoIndex));
        indexLabel.setId("playText");
        indexLabel.setMouseTransparent(true);
        StackPane.setAlignment(indexLabel, Pos.CENTER);

        playSVG = new SVGPath();
        playSVG.setContent(App.svgMap.get(SVG.PLAY));
        playIcon.setShape(playSVG);
        playIcon.setPrefSize(13, 15);
        playIcon.setMaxSize(13, 15);
        playIcon.setId("playIcon");
        playIcon.setVisible(false);
        playIcon.setTranslateX(3);

        indexPane.getChildren().addAll(indexLabel, playIcon);

        removeSVG = new SVGPath();
        removeSVG.setContent(App.svgMap.get(SVG.REMOVE));

        optionsSVG = new SVGPath();
        optionsSVG.setContent(App.svgMap.get(SVG.OPTIONS));


        if(mediaItem.getCover() != null) {
            coverImage.setImage(mediaItem.getCover());
            imageWrapper.setStyle("-fx-background-color: rgba(" + Math.round(mediaItem.getCoverBackgroundColor().getRed() * 256) + "," + Math.round(mediaItem.getCoverBackgroundColor().getGreen() * 256) + "," + Math.round(mediaItem.getCoverBackgroundColor().getBlue() * 256) + ", 0.7);");
        }
        else {
            imageWrapper.setStyle("-fx-background-color: rgb(64,64,64);");
            coverImage.setImage(mediaItem.getPlaceholderCover());
        }


        imageWrapper.setMaxSize(125,70);
        imageWrapper.getChildren().addAll(coverImage);

        videoTitle.getStyleClass().add("videoTitle");

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

        videoTitle.setWrapText(true);
        videoTitle.setMaxHeight(40);

        artist.getStyleClass().add("subText");



        String formattedDuration = Utilities.getTime(mediaItem.getDuration());

        if(!artist.getText().isEmpty()){
            formattedDuration = formattedDuration + " • ";
        }

        if(mediaItem.getDuration() != null) duration.setText(formattedDuration);
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

        optionsButton.setOnAction((e) -> {
            if(optionsPopUp.isShowing()) optionsPopUp.hide();
            else optionsPopUp.showOptions();
        });

        this.setOnMouseClicked(e -> {

            if(!menuController.animationsInProgress.isEmpty()) return;

            if(optionsPopUp.isShowing()) optionsPopUp.hide();
            else if(e.getButton() == MouseButton.PRIMARY) play(true);
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

        this.add(indexPane, 0, 0);
        this.add(imageWrapper, 1, 0);
        this.add(textWrapper, 2, 0);
        this.add(removeButtonWrapper, 3, 0);
        this.add(optionsButtonWrapper, 4, 0);

        this.setViewOrder(1);


        this.setOnMouseEntered((e) -> {
            mouseHover = true;

            this.setStyle("-fx-background-color: rgba(70,70,70,0.6);");
            indexLabel.setVisible(false);
            playIcon.setVisible(true);

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
            this.setStyle("-fx-background-color: rgba(70,70,70,0.6);");

            playIcon.setVisible(true);
            indexLabel.setVisible(false);

            if (optionsPopUp.isShowing()) optionsPopUp.hide();

            dragPosition = e.getY();
            minimumY = this.getBoundsInParent().getMinY(); // this is the maximum negative translation that can be applied
            maximumY = queueBox.getChildren().get(queueBox.getChildren().size() - 1).getBoundsInParent().getMinY(); // the top border of the last element inside the vbox, dragged node cant move below that

            newPosition = videoIndex - 1;

            this.startFullDrag();
        });



        this.setOnMouseExited((e) -> {
            mouseHover = false;

            if(!queueBox.dragActive && !optionsPopUp.showing){
                this.setStyle("-fx-background-color: transparent;");

                indexLabel.setVisible(true);
                playIcon.setVisible(false);
            }
        });

        optionsButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> AnimationsClass.fadeAnimation(200, optionsButton, 0, 1, false, 1, true));

        optionsButton.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> AnimationsClass.fadeAnimation(200, optionsButton, 1, 0, false, 1, true));

        removeButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> AnimationsClass.fadeAnimation(200, removeButton, 0, 1, false, 1, true));

        removeButton.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> AnimationsClass.fadeAnimation(200, removeButton, 1, 0, false, 1, true));

        removeButton.setOnAction((e) -> {

            if(!menuController.animationsInProgress.isEmpty()) return;

            if(optionsPopUp.showing) optionsPopUp.hide();
            else queueBox.remove(this, true);

        });

    }

    public void updateIndex(int i){
        videoIndex = i + 1;
        indexLabel.setText(String.valueOf(videoIndex));
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

        if(mediaInterface.mediaActive.get()) mediaInterface.resetMediaPlayer(true);
        else {
            menuController.controlBarController.disablePreviousVideoButton();
            menuController.controlBarController.disableNextVideoButton();
        }

        menuController.activeBox.set(activeItem, true);

        if(menuController.settingsController.playbackOptionsController.shuffleOn || menuController.queue.indexOf(this) == menuController.queue.size() -1){
            queueBox.remove(this, false);
        }
        else {
            queueBox.removeAndMove(queueBox.getChildren().indexOf(this), false);
        }

    }

    @Override
    public void showMetadata(){

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
    public void playNext(){
        if(videoIndex > 1) queueBox.move(videoIndex -1, 0);

        menuController.notificationText.setText("Video will play next");
        if(menuController.menuNotificationOpen) menuController.closeTimer.playFromStart();
        else AnimationsClass.openMenuNotification(menuController);
    }

    @Override
    public Button getOptionsButton() {
        return this.optionsButton;
    }

    @Override
    public MediaItem getMediaItem() {
        return this.mediaItem;
    }

    @Override
    public void update(){

        if(mediaItem.getCover() != null) {
            coverImage.setImage(mediaItem.getCover());
            imageWrapper.setStyle("-fx-background-color: rgba(" + Math.round(mediaItem.getCoverBackgroundColor().getRed() * 256) + "," + Math.round(mediaItem.getCoverBackgroundColor().getGreen() * 256) + "," + Math.round(mediaItem.getCoverBackgroundColor().getBlue() * 256) + ", 0.7);");
        }
        else {
            imageWrapper.setStyle("-fx-background-color: rgb(64,64,64);");
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

}
