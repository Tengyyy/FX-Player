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
import javafx.scene.image.PixelBuffer;
import javafx.scene.image.PixelReader;
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
import java.util.HashMap;
import java.util.Map;

public class QueueItem extends GridPane implements MenuObject{

    // layout constraints for the video item
    ColumnConstraints column1 = new ColumnConstraints(45, 45, 45);
    ColumnConstraints column2 = new ColumnConstraints(129,129,129);
    ColumnConstraints column3 = new ColumnConstraints(0,100,Double.MAX_VALUE);
    ColumnConstraints column4 = new ColumnConstraints(35,35,35);
    ColumnConstraints column5 = new ColumnConstraints(35,35,35);

    RowConstraints row1 = new RowConstraints(90, 90, 90);


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

    StackPane captionsPane;
    Region captionsIcon;
    SVGPath captionsPath;

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
        coverImage.setImage(mediaItem.getCover());
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

            if(mediaItem.getCoverBackgroundColor() == null){


                final PixelReader pr = mediaItem.getCover().getPixelReader();
                final Map<Color, Long> colCount = new HashMap<>();

                for(int x = 0; x < Math.min(mediaItem.getCover().getWidth(), 5); x++) {
                    for(int y = 0; y < mediaItem.getCover().getHeight(); y++) {
                        final Color col = pr.getColor(x, y);
                        if(colCount.containsKey(col)) {
                            colCount.put(col, colCount.get(col) + 1);
                        } else {
                            colCount.put(col, 1L);
                        }
                    }
                }

                if(mediaItem.getCover().getWidth() > 5){
                    for(int x = (int) Math.max((mediaItem.getCover().getWidth() - 5), 5); x < mediaItem.getCover().getWidth(); x++){
                        for(int y = 0; y < mediaItem.getCover().getHeight(); y++) {
                            final Color col = pr.getColor(x, y);
                            if(colCount.containsKey(col)) {
                                colCount.put(col, colCount.get(col) + 1);
                            } else {
                                colCount.put(col, 1L);
                            }
                        }
                    }
                }

                // Get the color with the highest number of occurrences .

                final Color dominantCol = colCount.entrySet().stream().max(Map.Entry.comparingByValue()).get().getKey();
                mediaItem.setCoverBackgroundColor(dominantCol);

                imageWrapper.setStyle("-fx-background-color: rgba(" + Math.round(dominantCol.getRed() * 256) + "," + Math.round(dominantCol.getGreen() * 256) + "," + Math.round(dominantCol.getBlue() * 256) + ", 0.7);");

            }
            else {
                imageWrapper.setStyle("-fx-background-color: rgba(" + Math.round(mediaItem.getCoverBackgroundColor().getRed() * 256) + "," + Math.round(mediaItem.getCoverBackgroundColor().getGreen() * 256) + "," + Math.round(mediaItem.getCoverBackgroundColor().getBlue() * 256) + ", 0.7);");
            }
        }
        else {
            imageWrapper.setStyle("-fx-background-color: rgba(0,0,0, 0.7);");

            //grab frame, set it as cover, calculate background color
        }


        imageWrapper.setMaxSize(125,70);
        imageWrapper.getChildren().addAll(coverImage);

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
        textWrapper.setPrefHeight(90);
        textWrapper.getChildren().addAll(videoTitle,subTextWrapper);
        GridPane.setMargin(textWrapper, new Insets(0, 0, 0, 10));

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

            if(!menuController.animationsInProgress.isEmpty()) return;
            play(true);
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


            if(!queueBox.dragActive){
                this.setStyle("-fx-background-color: transparent;");

                indexLabel.setVisible(true);
                playIcon.setVisible(false);
            }
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

        if(mediaInterface.mediaActive.get()) mediaInterface.resetMediaPlayer();

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
