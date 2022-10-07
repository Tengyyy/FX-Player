package hans.Menu;

import com.jfoenix.controls.JFXButton;
import hans.*;
import hans.MediaItems.MediaItem;
import javafx.animation.Animation;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

import java.io.File;
import java.util.Map;

public class HistoryItem extends GridPane implements MenuObject {

    // history item tuleb enne valmis disainida kui saab teha mediainterface korda


    // layout constraints for the video item
    ColumnConstraints column1 = new ColumnConstraints(45, 45, 45);
    ColumnConstraints column2 = new ColumnConstraints(129,129,129);
    ColumnConstraints column3 = new ColumnConstraints(0,100,Double.MAX_VALUE);
    ColumnConstraints column4 = new ColumnConstraints(35,35,35);

    RowConstraints row1 = new RowConstraints(90, 90, 90);


    public Label videoTitle = new Label();

    public HBox subTextWrapper = new HBox();
    Label artist = new Label();
    public Label duration = new Label();

    VBox textWrapper = new VBox();

    JFXButton optionsButton = new JFXButton();


    MediaItem mediaItem;

    MenuController menuController;

    Region optionsIcon, playIcon, captionsIcon;

    StackPane playIconWrapper = new StackPane();

    ImageView coverImage = new ImageView();


    StackPane imageWrapper = new StackPane();
    StackPane optionsButtonWrapper = new StackPane();
    Region imageBorder = new Region();

    ControlTooltip optionsButtonTooltip;

    boolean mouseHover = false;
    BooleanProperty isActive = new SimpleBooleanProperty(false);


    SVGPath playSVG, optionsSVG, captionsPath;

    // the options popup for this queue item
    MenuItemOptionsPopUp optionsPopUp;

    MediaInterface mediaInterface;

    static double height = 90;

    HistoryBox historyBox;

    public StackPane captionsPane;


    HistoryItem(MediaItem mediaItem, MenuController menuController, MediaInterface mediaInterface, HistoryBox historyBox){
        this.mediaItem = mediaItem;
        this.menuController = menuController;
        this.mediaInterface = mediaInterface;
        this.historyBox = historyBox;

        isActive.addListener((observableValue, aBoolean, t1) -> {
            if(t1) playIcon.setVisible(true);
            else playIcon.setVisible(false);
        });


        column3.setHgrow(Priority.ALWAYS); // makes the middle column (video title text) take up all available space
        this.getColumnConstraints().addAll(column1, column2, column3, column4);
        this.getRowConstraints().addAll(row1);

        GridPane.setValignment(playIconWrapper, VPos.CENTER);
        GridPane.setValignment(imageWrapper, VPos.CENTER);
        GridPane.setValignment(textWrapper, VPos.CENTER);
        GridPane.setValignment(optionsButtonWrapper, VPos.CENTER);

        GridPane.setHalignment(playIconWrapper, HPos.CENTER);
        GridPane.setHalignment(imageWrapper, HPos.CENTER);
        GridPane.setHalignment(textWrapper, HPos.LEFT);
        GridPane.setHalignment(optionsButtonWrapper, HPos.CENTER);

        this.getStyleClass().add("queueItem");

        this.setOpacity(0);

        this.setCursor(Cursor.HAND);

        coverImage.setFitHeight(70);
        coverImage.setFitWidth(125);
        coverImage.setSmooth(true);
        coverImage.setImage(mediaItem.getCover());
        coverImage.setPreserveRatio(true);



        playSVG = new SVGPath();
        playSVG.setContent(App.svgMap.get(SVG.PLAY));



        optionsSVG = new SVGPath();
        optionsSVG.setContent(App.svgMap.get(SVG.OPTIONS));

        playIcon = new Region();
        playIcon.setShape(playSVG);
        playIcon.setMinSize(13, 15);
        playIcon.setPrefSize(13, 15);
        playIcon.setMaxSize(13, 15);
        playIcon.setMouseTransparent(true);
        playIcon.setId("playIcon");
        playIcon.setVisible(false);
        playIcon.setTranslateX(3);

        playIconWrapper.getChildren().add(playIcon);


        if(mediaItem.getCover() != null) {

            if(mediaItem.getCoverBackgroundColor() == null){

                double aspectRatio = mediaItem.getCover().getWidth() / mediaItem.getCover().getHeight();
                double realWidth = Math.min(coverImage.getFitWidth(), coverImage.getFitHeight() * aspectRatio);

                Color dominantColor = Utilities.findDominantColor(mediaItem.getCover(), realWidth < coverImage.getFitWidth());
                mediaItem.setCoverBackgroundColor(dominantColor);

                assert dominantColor != null;
                imageWrapper.setStyle("-fx-background-color: rgba(" + Math.round(dominantColor.getRed() * 256) + "," + Math.round(dominantColor.getGreen() * 256) + "," + Math.round(dominantColor.getBlue() * 256) + ", 0.7);");

            }
            else {
                imageWrapper.setStyle("-fx-background-color: rgba(" + Math.round(mediaItem.getCoverBackgroundColor().getRed() * 256) + "," + Math.round(mediaItem.getCoverBackgroundColor().getGreen() * 256) + "," + Math.round(mediaItem.getCoverBackgroundColor().getBlue() * 256) + ", 0.7);");
            }
        }
        else {
            imageWrapper.setStyle("-fx-background-color: rgba(0,0,0, 0.7);");

            //grab frame, set it as cover, calculate background color
        }


        imageWrapper.setPrefSize(129, 74); // has to be changed
        imageWrapper.setMaxSize(129, 74);
        imageWrapper.getChildren().addAll(coverImage, imageBorder);
        imageWrapper.setId("historyImageWrapper");

        imageBorder.setPrefSize(129, 74);
        imageBorder.setMaxSize(129, 74);
        imageBorder.setBackground(Background.EMPTY);
        imageBorder.setId("historyImageBorder");
        imageBorder.setMouseTransparent(true);
        imageBorder.setVisible(false);


        videoTitle.getStyleClass().add("videoTitle");

        Map<String, String> mediaInformation = mediaItem.getMediaInformation();

        if(mediaInformation != null){
            if(mediaInformation.containsKey("title")){
                videoTitle.setText(mediaInformation.get("title"));
            }
            else if(mediaInformation.containsKey("TITLE")){
                videoTitle.setText(mediaInformation.get("TITLE"));
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

        String formattedDuration = Utilities.getTime(mediaItem.getDuration());

        if(!artist.getText().isEmpty()){
            formattedDuration = formattedDuration + " • ";
        }

        if(mediaItem.getDuration() != null) duration.setText(formattedDuration);
        duration.getStyleClass().add("subText");

        subTextWrapper.setAlignment(Pos.CENTER_LEFT);
        subTextWrapper.getChildren().addAll(duration, artist);

        if(mediaItem.getSubtitles() != null) subTextWrapper.getChildren().add(0, captionsPane);

        textWrapper.setAlignment(Pos.CENTER_LEFT);
        textWrapper.setPrefHeight(70);
        textWrapper.getChildren().addAll(videoTitle,subTextWrapper);
        GridPane.setMargin(textWrapper, new Insets(0, 0, 0, 10));

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
            if(!this.isActive.get()) play();
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

        this.add(playIconWrapper, 0, 0);
        this.add(imageWrapper, 1, 0);
        this.add(textWrapper, 2, 0);
        this.add(optionsButtonWrapper, 3, 0);

        this.getStyleClass().add("historyItem");

        this.setOnMouseEntered((e) -> {
            mouseHover = true;

            // hide the bouncing columns thingy and stop animation
            if(!isActive.get()) playIcon.setVisible(true);

            this.setStyle("-fx-background-color: rgba(70,70,70,0.6);");


        });

        this.setOnMouseExited((e) -> {
            mouseHover = false;

            // show bouncing columns and start animation
            if(!isActive.get()) playIcon.setVisible(false);

            if(isActive.get()) this.setStyle("-fx-background-color: rgba(50,50,50,0.6);");
            else this.setStyle("-fx-background-color: transparent;");
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
            historyItem.playIcon.setStyle("-fx-background-color: rgb(200,200,200);");
            if(!historyItem.mouseHover) {
                historyItem.playIcon.setVisible(false);
                historyItem.setStyle("-fx-background-color: transparent;");
                historyItem.imageBorder.setVisible(false);
            }


            historyItem.setCursor(Cursor.HAND);

        }

        this.isActive.set(true);
        this.playIcon.setStyle("-fx-background-color: red");
        this.playIcon.setVisible(true);
        this.setCursor(Cursor.DEFAULT);

        if(this.coverImage.getImage() != null) this.imageBorder.setVisible(true);

        if(!mouseHover) this.setStyle("-fx-background-color: rgba(50,50,50,0.6);");

        historyBox.index = historyBox.getChildren().indexOf(this);
    }

    public void setInactive(){
        this.isActive.set(false);
        this.playIcon.setStyle("-fx-background-color: rgb(200,200,200)");
        if(!mouseHover) this.playIcon.setVisible(false);
        this.setCursor(Cursor.HAND);

        this.imageBorder.setVisible(false);

        if(!mouseHover) this.setStyle("-fx-background-color: transparent;");

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

        menuController.controlBarController.updateNextAndPrevTooltips();

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

        if(menuController.menuInTransition) return;

        menuController.metadataPage.enterMetadataPage(this);
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

    @Override
    public String getTitle() {
        return videoTitle.getText();
    }
}
