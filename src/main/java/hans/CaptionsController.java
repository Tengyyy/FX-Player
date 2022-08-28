package hans;


import hans.Menu.HistoryItem;
import hans.Menu.MenuController;
import hans.SRTParser.srt.SRTParser;
import hans.SRTParser.srt.Subtitle;
import hans.Settings.CaptionsOptionsPane;
import hans.Settings.CaptionsPane;
import hans.Settings.SettingsController;
import hans.Settings.SettingsState;
import javafx.animation.Animation;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.Event;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;

public class CaptionsController {

    public SettingsController settingsController;
    MainController mainController;
    MediaInterface mediaInterface;
    public ControlBarController controlBarController;
    public MenuController menuController;


    public CaptionsPane captionsPane;

    public CaptionsOptionsPane captionsOptionsPane;

    File captionsFile;

    ArrayList<Subtitle> subtitles  = new ArrayList<>();
    int captionsPosition = 0;

    public boolean captionsSelected = false;
    BooleanProperty captionsOn = new SimpleBooleanProperty();
    boolean showedCurrentCaption = false;


    public VBox captionsBox = new VBox();
    public Label captionsLabel1 = new Label();
    public Label captionsLabel2 = new Label();

    public int defaultFontSize = 30;
    public String defaultFontFamily = "\"Roboto Medium\"";

    public double defaultTextOpacity = 1.0;


    public Color defaultTextFill = Color.WHITE;

    public int defaultSpacing = 10;
    public int defaultBackgroundRed = 0;
    public int defaultBackgroundGreen = 0;
    public int defaultBackgroundBlue = 0;
    public double defaultBackgroundOpacity = 0.75;
    public Pos defaultTextAlignment = Pos.CENTER;

    public Color defaultBackground = Color.rgb(defaultBackgroundRed, defaultBackgroundGreen, defaultBackgroundBlue, defaultBackgroundOpacity);

    public DoubleProperty mediaWidthMultiplier = new SimpleDoubleProperty(0.4);


    public int currentFontSize = defaultFontSize;
    public String currentFontFamily = defaultFontFamily;

    public double currentTextOpacity = defaultTextOpacity;

    public Color currentTextFill = defaultTextFill;
    public int currentSpacing = defaultSpacing;
    public int currentBackgroundRed = defaultBackgroundRed;
    public int currentBackgroundGreen = defaultBackgroundGreen;
    public int currentBackgroundBlue = defaultBackgroundBlue;
    public double currentBackgroundOpacity = defaultBackgroundOpacity;
    public Pos currentTextAlignment = defaultTextAlignment;

    public Color currentBackground = defaultBackground;


    Pos captionsLocation = Pos.BOTTOM_CENTER;

    PauseTransition showCaptionsTimer;

    public boolean captionsDragActive = false;
    boolean captionsAnimating = false;
    TranslateTransition captionsTransition;

    double dragPositionY = 0;
    double dragPositionX = 0;

    double minimumY = 0;
    double maximumY = 0;

    double minimumX = 0;
    double maximumX = 0;

    double startY = 0;
    double startX = 0;

    double startTranslateY = 0;
    double startTranslateX = 0;


    CaptionsController(SettingsController settingsController, MainController mainController, MediaInterface mediaInterface, ControlBarController controlBarController, MenuController menuController){
        this.settingsController = settingsController;
        this.mainController = mainController;
        this.mediaInterface = mediaInterface;
        this.controlBarController = controlBarController;
        this.menuController = menuController;

        captionsPane = new CaptionsPane(this);

        captionsOptionsPane = new CaptionsOptionsPane(this);

        captionsOn.set(false);


        captionsLabel1.setBackground(new Background(new BackgroundFill(defaultBackground, CornerRadii.EMPTY, Insets.EMPTY)));
        captionsLabel1.setTextFill(defaultTextFill);
        captionsLabel1.getStyleClass().add("captionsLabel");
        captionsLabel1.setStyle("-fx-font-family: " + defaultFontFamily + "; -fx-font-size: " + mediaWidthMultiplier.multiply(defaultFontSize).get());
        captionsLabel1.setOpacity(0);
        captionsLabel1.setPadding(new Insets(2, 4, 2, 4));

        captionsLabel2.setBackground(new Background(new BackgroundFill(defaultBackground, CornerRadii.EMPTY, Insets.EMPTY)));
        captionsLabel2.setTextFill(defaultTextFill);
        captionsLabel2.setText("Subtitles look like this");
        captionsLabel2.getStyleClass().add("captionsLabel"); // 4 sec timer
        captionsLabel2.setStyle("-fx-font-family: " + defaultFontFamily + "; -fx-font-size: " + mediaWidthMultiplier.multiply(defaultFontSize).get());
        captionsLabel2.setOpacity(0);
        captionsLabel2.setPadding(new Insets(2, 4, 2, 4));


        captionsBox.setSpacing(mediaWidthMultiplier.multiply(defaultSpacing).get());
        captionsBox.setTranslateY(-50);
        captionsBox.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        captionsBox.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        captionsBox.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        captionsBox.getChildren().addAll(captionsLabel1, captionsLabel2);
        captionsBox.setAlignment(defaultTextAlignment);
        captionsBox.setVisible(false);
        captionsBox.setPadding(new Insets(5, 10, 5, 10));
        captionsBox.setOpacity(defaultTextOpacity);
        captionsBox.setCursor(Cursor.OPEN_HAND);


        captionsBox.setOnMousePressed(e -> {

            if(menuController.menuInTransition || menuController.menuOpen){
                e.consume();
                return;
            }

            if(settingsController.settingsState != SettingsState.CLOSED) settingsController.closeSettings();

            captionsBox.setCursor(Cursor.CLOSED_HAND);
            captionsBox.setStyle("-fx-background-color: rgba(0,0,0,0.75);");
            captionsDragActive = true;

            if(captionsTransition != null && captionsTransition.getStatus() == Animation.Status.RUNNING) captionsTransition.stop();

            controlBarController.controlBarWrapper.setMouseTransparent(true);
            settingsController.settingsBuffer.setMouseTransparent(true);
            mainController.menuButtonPane.setMouseTransparent(true);
            mainController.videoImageViewInnerWrapper.setMouseTransparent(true);

            if(showCaptionsTimer != null && showCaptionsTimer.getStatus() == Animation.Status.RUNNING) showCaptionsTimer.pause();

            e.consume();

        });

        captionsBox.setOnMouseReleased(e -> {

            if(!captionsDragActive) return;

            captionsBox.setCursor(Cursor.OPEN_HAND);
            captionsDragActive = false;
            captionsAnimating = true;

            controlBarController.controlBarWrapper.setMouseTransparent(false);

            if(settingsController.settingsState != SettingsState.CLOSED) settingsController.settingsBuffer.setMouseTransparent(false);

            mainController.menuButtonPane.setMouseTransparent(false);
            mainController.videoImageViewInnerWrapper.setMouseTransparent(false);

            dragPositionY = 0;
            dragPositionX = 0;
            minimumY = 0;
            minimumX = 0;
            maximumY = 0;
            maximumX = 0;
            startY = 0;
            startX = 0;
            startTranslateY = 0;
            startTranslateX = 0;

            if(showCaptionsTimer != null && showCaptionsTimer.getStatus() == Animation.Status.PAUSED) showCaptionsTimer.playFromStart();

            Pos newPosition = findClosestCaptionsPosition(captionsBox.getBoundsInParent().getMinX() + captionsBox.getLayoutBounds().getMaxX()/2, captionsBox.getBoundsInParent().getMinY() + captionsBox.getLayoutBounds().getMaxY()/2);

            Point2D translation = getTranslation(newPosition);

            captionsLocation = newPosition;
            StackPane.setAlignment(captionsBox, newPosition);
            captionsBox.setTranslateX(translation.getX());
            captionsBox.setTranslateY(translation.getY());

            captionsTransition = createTranslateTransition(newPosition);
            captionsTransition.setOnFinished(ev -> {
                captionsAnimating = false;
                captionsBox.setStyle("-fx-background-color: transparent;");
            });

            captionsTransition.play();

            e.consume();

        });

        captionsBox.setOnMouseClicked(Event::consume);

        captionsBox.setOnDragDetected(e -> {

            if(!captionsDragActive) return;

            dragPositionY = e.getY();
            dragPositionX = e.getX();

            minimumY = 70; // maximum negative translation that can be applied (70px margin from top edge)

            minimumX = 70; // 70px from left edge due to the button in top left corner

            maximumY = mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxY() - 90;
            maximumX = mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxX() - 30;

            startX = captionsBox.getBoundsInParent().getMinX();
            startY = captionsBox.getBoundsInParent().getMinY();

            startTranslateY = captionsBox.getTranslateY();
            startTranslateX = captionsBox.getTranslateX();


            captionsBox.startFullDrag();
        });


        StackPane.setAlignment(captionsBox, Pos.BOTTOM_CENTER);
        mainController.videoImageViewInnerWrapper.getChildren().add(1, captionsBox);


        captionsOn.addListener((observableValue, aBoolean, t1) -> {
            if(t1){
                captionsBox.setVisible(true);

                if(menuController.activeItem != null && captionsSelected && showCaptionsTimer != null && showCaptionsTimer.getStatus() == Animation.Status.RUNNING){
                    showCaptionsTimer.stop();
                }

                captionsLabel1.setOpacity(0);
                captionsLabel2.setOpacity(0);
            }
            else {
                if(showCaptionsTimer == null || showCaptionsTimer.getStatus() != Animation.Status.RUNNING) captionsBox.setVisible(false);
            }

            if(menuController.activeItem != null){
                menuController.activeItem.getMediaItem().setSubtitlesOn(t1);
            }
        });

    }



    public void loadCaptions(File file, boolean toggleOn){

        if(!captionsSelected){
            // enable captions button
            controlBarController.captionsIcon.getStyleClass().clear();
            controlBarController.captionsIcon.getStyleClass().add("controlIcon");
            if(settingsController.settingsState == SettingsState.CLOSED) controlBarController.captions.updateText("Subtitles/closed captions (c)");

            captionsPane.captionsToggle.setDisable(false);


            captionsPane.currentCaptionsTab.getChildren().add(captionsPane.currentCaptionsNameLabel);
            captionsPane.currentCaptionsLabel.setText("Active subtitles:");

            captionsPane.currentCaptionsNameLabel.setText(file.getName());
        }
        else {
            captionsPane.currentCaptionsNameLabel.setText(file.getName());
        }


        if(menuController.activeItem != null){
            menuController.activeItem.getMediaItem().setSubtitles(file);
            if(!menuController.activeItem.subTextWrapper.getChildren().contains(menuController.activeItem.captionsPane)) menuController.activeItem.subTextWrapper.getChildren().add(0, menuController.activeItem.captionsPane);
        }

        if(menuController.historyBox.index > -1){
            HistoryItem activeHistoryItem = menuController.history.get(menuController.historyBox.index);
            activeHistoryItem.getMediaItem().setSubtitles(file);
            if(toggleOn) activeHistoryItem.getMediaItem().setSubtitlesOn(true);
            if(!activeHistoryItem.subTextWrapper.getChildren().contains(activeHistoryItem.captionsPane)) activeHistoryItem.subTextWrapper.getChildren().add(0, activeHistoryItem.captionsPane);
        }

        this.captionsFile = file;

        subtitles = SRTParser.getSubtitlesFromFile(file.getPath(), true);

        captionsSelected = true;

        if(!captionsOn.get() && toggleOn){
            captionsPane.captionsToggle.fire();
        }
    }



    public void removeCaptions(){
        if(captionsSelected){
            this.captionsFile = null;
            captionsSelected = false;

            subtitles.clear();
            captionsPosition = 0;
            showedCurrentCaption = false;

            boolean temp = false;
            if(menuController.activeItem != null){
                temp = menuController.activeItem.getMediaItem().getSubtitlesOn();
            }


            if(captionsOn.get()) controlBarController.closeCaptions();

            captionsLabel1.setOpacity(0);
            captionsLabel2.setOpacity(0);

            controlBarController.captionsIcon.getStyleClass().clear();
            controlBarController.captionsIcon.getStyleClass().add("controlIconDisabled");
            if(settingsController.settingsState == SettingsState.CLOSED) controlBarController.captions.updateText("Subtitles/CC not selected");

            captionsPane.currentCaptionsTab.getChildren().remove(captionsPane.currentCaptionsNameLabel);
            captionsPane.currentCaptionsLabel.setText("No subtitles active");

            captionsPane.captionsToggle.setSelected(false);
            captionsPane.captionsToggle.setDisable(true);

            if(temp && menuController.activeItem != null){
                menuController.activeItem.getMediaItem().setSubtitlesOn(true);
            }

        }
    }


    public void resizeCaptions(){

        captionsLabel1.setStyle("-fx-font-family: " + currentFontFamily + "; -fx-font-size: " + mediaWidthMultiplier.multiply(currentFontSize).get());
        captionsLabel2.setStyle("-fx-font-family: " + currentFontFamily + "; -fx-font-size: " + mediaWidthMultiplier.multiply(currentFontSize).get());

        captionsBox.setSpacing(mediaWidthMultiplier.multiply(currentSpacing).get());
    }


    public void showCaptions(){
        // if necessary, show captions with text "Captions look like this"

        if(menuController.activeItem != null && captionsSelected && captionsOn.get()) return;

        if(showCaptionsTimer != null && showCaptionsTimer.getStatus() == Animation.Status.RUNNING){
            showCaptionsTimer.playFromStart();
        }
        else {

            captionsBox.setVisible(true);
            captionsLabel1.setOpacity(0);

            captionsLabel2.setOpacity(1);
            captionsLabel2.setText("Captions look like this");

            showCaptionsTimer = new PauseTransition(Duration.millis(4000));
            showCaptionsTimer.setOnFinished(e -> {
                captionsBox.setVisible(false);
                captionsLabel1.setOpacity(0);
                captionsLabel2.setOpacity(0);
            });

            showCaptionsTimer.playFromStart();
        }
    }

    public Pos findClosestCaptionsPosition(double x, double y){

        Point2D topLeft = new Point2D(70, 70);
        Point2D topCenter = new Point2D(mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxX()/2, 70);
        Point2D topRight = new Point2D(mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxX() - 30, 70);
        Point2D centerRight = new Point2D(mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxX() - 30, mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxY()/2);
        Point2D bottomRight = new Point2D(mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxX() - 30,mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxY() - 90);
        Point2D bottomCenter = new Point2D(mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxX()/2, mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxY() - 90);
        Point2D bottomLeft = new Point2D(70, mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxY() - 90);
        Point2D centerLeft = new Point2D(70, mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxY()/2);

        ArrayList<Point2D> captionsPositions = new ArrayList<>();
        captionsPositions.add(topLeft);
        captionsPositions.add(topCenter);
        captionsPositions.add(topRight);
        captionsPositions.add(centerRight);
        captionsPositions.add(bottomRight);
        captionsPositions.add(bottomCenter);
        captionsPositions.add(bottomLeft);
        captionsPositions.add(centerLeft);

        double currentShortestDistance = Double.MAX_VALUE;
        Pos currentClosestPosition = null;

        for(int i = 0; i < captionsPositions.size(); i++){
            double distance = Math.hypot(captionsPositions.get(i).getX() - x, captionsPositions.get(i).getY() - y);
            if(distance < currentShortestDistance){
                currentShortestDistance = distance;

                switch (i){
                    case 0: currentClosestPosition = Pos.TOP_LEFT;
                        break;
                    case 1: currentClosestPosition = Pos.TOP_CENTER;
                        break;
                    case 2: currentClosestPosition = Pos.TOP_RIGHT;
                        break;
                    case 3: currentClosestPosition = Pos.CENTER_RIGHT;
                        break;
                    case 4: currentClosestPosition = Pos.BOTTOM_RIGHT;
                        break;
                    case 5: currentClosestPosition = Pos.BOTTOM_CENTER;
                        break;
                    case 6: currentClosestPosition = Pos.BOTTOM_LEFT;
                        break;
                    case 7: currentClosestPosition = Pos.CENTER_LEFT;
                        break;
                    default: currentClosestPosition = null;
                        break;
                }
            }
        }

        return currentClosestPosition;
    }

    public Point2D getTranslation(Pos position){

        Bounds bounds = captionsBox.getBoundsInParent();
        Bounds layoutBounds = captionsBox.getLayoutBounds();

        Point2D translation = null;

        switch(position){
            case TOP_LEFT: translation = new Point2D(bounds.getMinX(), bounds.getMinY());
                break;
            case TOP_CENTER: translation = new Point2D(bounds.getMinX() - (mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxX()/2 - layoutBounds.getMaxX()/2), bounds.getMinY());
                break;
            case TOP_RIGHT: translation = new Point2D(bounds.getMinX() - (mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxX() - layoutBounds.getMaxX()), bounds.getMinY());
                break;
            case CENTER_RIGHT: translation = new Point2D(bounds.getMinX() - (mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxX() - layoutBounds.getMaxX()), bounds.getMinY() - (mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxY()/2 - layoutBounds.getMaxY()/2));
                break;
            case BOTTOM_RIGHT: translation = new Point2D(bounds.getMinX() - (mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxX() - layoutBounds.getMaxX()), bounds.getMinY() - (mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxY() - layoutBounds.getMaxY()));
                break;
            case BOTTOM_CENTER: translation = new Point2D(bounds.getMinX() - (mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxX()/2 - layoutBounds.getMaxX()/2), bounds.getMinY() - (mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxY() - layoutBounds.getMaxY()));
                break;
            case BOTTOM_LEFT: translation = new Point2D(bounds.getMinX(), bounds.getMinY() - (mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxY() - layoutBounds.getMaxY()));
                break;
            case CENTER_LEFT: translation = new Point2D(bounds.getMinX(), bounds.getMinY() - (mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxY()/2 - layoutBounds.getMaxY()/2));
                break;
            default: break;
        }

        return translation;
    }


    public TranslateTransition createTranslateTransition(Pos position){

        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(300), captionsBox);

        translateTransition.setFromX(captionsBox.getTranslateX());
        translateTransition.setFromY(captionsBox.getTranslateY());

        if(position == Pos.CENTER_LEFT || position == Pos.TOP_LEFT || position == Pos.BOTTOM_LEFT){
            translateTransition.setToX(70);
        }
        else if(position == Pos.TOP_RIGHT || position == Pos.CENTER_RIGHT || position == Pos.BOTTOM_RIGHT){
            translateTransition.setToX(-30);
        }
        else {
            translateTransition.setToX(0);
        }

        if(position == Pos.TOP_LEFT || position == Pos.TOP_CENTER || position == Pos.TOP_RIGHT){
            translateTransition.setToY(70);
        }
        else if(position == Pos.BOTTOM_LEFT || position == Pos.BOTTOM_CENTER || position == Pos.BOTTOM_RIGHT){
            translateTransition.setToY(-90);
        }
        else {
            translateTransition.setToY(0);
        }

        return translateTransition;
    }

    public void cancelDrag(){
        if(!captionsDragActive) return;

        captionsBox.setCursor(Cursor.OPEN_HAND);
        captionsDragActive = false;

        controlBarController.controlBarWrapper.setMouseTransparent(false);

        if(settingsController.settingsState != SettingsState.CLOSED) settingsController.settingsBuffer.setMouseTransparent(false);

        mainController.menuButtonPane.setMouseTransparent(false);
        mainController.videoImageViewInnerWrapper.setMouseTransparent(false);

        dragPositionY = 0;
        dragPositionX = 0;
        minimumY = 0;
        minimumX = 0;
        maximumY = 0;
        maximumX = 0;
        startY = 0;
        startX = 0;
        startTranslateY = 0;
        startTranslateX = 0;

        if(showCaptionsTimer != null && showCaptionsTimer.getStatus() == Animation.Status.PAUSED) showCaptionsTimer.playFromStart();

        Pos newPosition = findClosestCaptionsPosition(captionsBox.getBoundsInParent().getMinX() + captionsBox.getLayoutBounds().getMaxX()/2, captionsBox.getBoundsInParent().getMinY() + captionsBox.getLayoutBounds().getMaxY()/2);

        Point2D translation = getTranslation(newPosition);

        captionsLocation = newPosition;
        StackPane.setAlignment(captionsBox, newPosition);
        captionsBox.setTranslateX(translation.getX());
        captionsBox.setTranslateY(translation.getY());

        captionsTransition = createTranslateTransition(newPosition);
        captionsTransition.setOnFinished(ev -> {
            captionsAnimating = false;
            captionsBox.setStyle("-fx-background-color: transparent;");
        });

        captionsTransition.play();
    }

    public void moveToMiniplayer(){
        captionsBox.setMouseTransparent(true);

        if(captionsTransition != null && captionsTransition.getStatus() == Animation.Status.RUNNING) {
            captionsTransition.stop();
        }

        mainController.videoImageViewInnerWrapper.getChildren().remove(captionsBox);
        mainController.miniplayer.miniplayerController.videoImageViewInnerWrapper.getChildren().add(captionsBox);

        switch(captionsLocation){
            case BOTTOM_RIGHT: {
                captionsBox.setTranslateX(-10);
                captionsBox.setTranslateY(-10);
            }
            break;
            case BOTTOM_CENTER: {
                captionsBox.setTranslateX(0);
                captionsBox.setTranslateY(-10);
            }
            break;
            case BOTTOM_LEFT: {
                captionsBox.setTranslateX(10);
                captionsBox.setTranslateY(-10);
            }
            break;
            case CENTER_RIGHT: {
                captionsBox.setTranslateX(-10);
                captionsBox.setTranslateY(0);
            }
            break;
            case CENTER_LEFT: {
                captionsBox.setTranslateX(10);
                captionsBox.setTranslateY(0);
            }
            break;
            case TOP_LEFT: {
                captionsBox.setTranslateX(10);
                captionsBox.setTranslateY(10);
            }
            break;
            case TOP_CENTER: {
                captionsBox.setTranslateX(0);
                captionsBox.setTranslateY(10);
            }
            case TOP_RIGHT: {
                captionsBox.setTranslateX(-10);
                captionsBox.setTranslateY(10);
            }
        }
    }

    public void moveToMainplayer(){
        captionsBox.setMouseTransparent(false);


        mainController.miniplayer.miniplayerController.videoImageViewInnerWrapper.getChildren().remove(captionsBox);
        mainController.videoImageViewInnerWrapper.getChildren().add(captionsBox);

        switch(captionsLocation){
            case BOTTOM_RIGHT: {
                captionsBox.setTranslateX(-30);

                if(controlBarController.controlBarOpen) captionsBox.setTranslateY(-90);
                else captionsBox.setTranslateY(-30);
            }
            break;
            case BOTTOM_CENTER: {
                captionsBox.setTranslateX(0);

                if(controlBarController.controlBarOpen) captionsBox.setTranslateY(-90);
                else captionsBox.setTranslateY(-30);
            }
            break;
            case BOTTOM_LEFT: {
                captionsBox.setTranslateX(70);

                if(controlBarController.controlBarOpen) captionsBox.setTranslateY(-90);
                else captionsBox.setTranslateY(-30);
            }
            break;
            case CENTER_RIGHT: {
                captionsBox.setTranslateX(-30);
                captionsBox.setTranslateY(0);
            }
            break;
            case CENTER_LEFT: {
                captionsBox.setTranslateX(70);
                captionsBox.setTranslateY(0);
            }
            break;
            case TOP_LEFT: {
                captionsBox.setTranslateX(70);
                captionsBox.setTranslateY(70);
            }
            break;
            case TOP_CENTER: {
                captionsBox.setTranslateX(0);
                captionsBox.setTranslateY(70);
            }
            case TOP_RIGHT: {
                captionsBox.setTranslateX(-30);
                captionsBox.setTranslateY(70);
            }
        }
    }


    public void updateCaptions(double time){
        if(!menuController.captionsController.subtitles.isEmpty() &&
                menuController.captionsController.captionsPosition >= 0 &&
                menuController.captionsController.captionsPosition < menuController.captionsController.subtitles.size() &&
                menuController.captionsController.captionsOn.get() &&
                !menuController.captionsController.captionsDragActive) {


            if (time < menuController.captionsController.subtitles.get(menuController.captionsController.captionsPosition).timeIn && menuController.captionsController.captionsPosition > 0) {

                do {
                    menuController.captionsController.captionsPosition--;
                    menuController.captionsController.showedCurrentCaption = false;
                }
                while (time < menuController.captionsController.subtitles.get(menuController.captionsController.captionsPosition).timeIn && menuController.captionsController.captionsPosition > 0);
            } else if (menuController.captionsController.captionsPosition < menuController.captionsController.subtitles.size() - 1 && time >= menuController.captionsController.subtitles.get(menuController.captionsController.captionsPosition + 1).timeIn) {
                do {
                    menuController.captionsController.captionsPosition++;
                    menuController.captionsController.showedCurrentCaption = false;
                }
                while (menuController.captionsController.captionsPosition < menuController.captionsController.subtitles.size() - 1 && time >= menuController.captionsController.subtitles.get(menuController.captionsController.captionsPosition + 1).timeIn);
            }


            if (time >= menuController.captionsController.subtitles.get(menuController.captionsController.captionsPosition).timeIn && time < menuController.captionsController.subtitles.get(menuController.captionsController.captionsPosition).timeOut && !menuController.captionsController.showedCurrentCaption) {
                String text = menuController.captionsController.subtitles.get(menuController.captionsController.captionsPosition).text;

                // if the subtitle contains a new line character then split the subtitle into two and add the part after the new line onto another label

                String[] subtitleLines = Utilities.splitLines(text);

                if (subtitleLines.length == 2) {
                        menuController.captionsController.captionsLabel1.setOpacity(1);
                        menuController.captionsController.captionsLabel2.setOpacity(1);
                        menuController.captionsController.captionsLabel1.setText(subtitleLines[0]);
                        menuController.captionsController.captionsLabel2.setText(subtitleLines[1]);
                } else {
                        menuController.captionsController.captionsLabel1.setOpacity(0);
                        menuController.captionsController.captionsLabel2.setOpacity(1);
                        menuController.captionsController.captionsLabel2.setText(subtitleLines[0]);
                }

                menuController.captionsController.showedCurrentCaption = true;
            } else if ((time >= menuController.captionsController.subtitles.get(menuController.captionsController.captionsPosition).timeOut && menuController.captionsController.captionsPosition >= menuController.captionsController.subtitles.size() - 1) || (time >= menuController.captionsController.subtitles.get(menuController.captionsController.captionsPosition).timeOut && time < menuController.captionsController.subtitles.get(menuController.captionsController.captionsPosition + 1).timeIn) || (time < menuController.captionsController.subtitles.get(menuController.captionsController.captionsPosition).timeIn && menuController.captionsController.captionsPosition <= 0)) {
                    menuController.captionsController.captionsLabel1.setOpacity(0);
                    menuController.captionsController.captionsLabel2.setOpacity(0);
            }
        }
    }

}
