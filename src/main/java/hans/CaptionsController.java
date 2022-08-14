package hans;


import hans.SRTParser.srt.SRTParser;
import hans.SRTParser.srt.Subtitle;
import javafx.animation.Animation;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;

public class CaptionsController {

    SettingsController settingsController;
    MainController mainController;
    MediaInterface mediaInterface;
    ControlBarController controlBarController;
    MenuController menuController;


    CaptionsPane captionsPane;

    CaptionsOptionsPane captionsOptionsPane;

    File captionsFile;

    ArrayList<Subtitle> subtitles  = new ArrayList<>();
    int captionsPosition = 0;

    boolean captionsSelected = false;
    BooleanProperty captionsOn = new SimpleBooleanProperty();
    boolean showedCurrentCaption = false;


    VBox captionsBox = new VBox();
    Label captionsLabel1 = new Label();
    Label captionsLabel2 = new Label();

    int defaultFontSize = 30;
    String defaultFontFamily = "\"Roboto Medium\"";

    double defaultTextOpacity = 1.0;


    Color defaultTextFill = Color.WHITE;

    int defaultSpacing = 10;
    int defaultBackgroundRed = 0;
    int defaultBackgroundGreen = 0;
    int defaultBackgroundBlue = 0;
    double defaultBackgroundOpacity = 0.75;
    Pos defaultTextAlignment = Pos.CENTER;

    Color defaultBackground = Color.rgb(defaultBackgroundRed, defaultBackgroundGreen, defaultBackgroundBlue, defaultBackgroundOpacity);

    DoubleProperty mediaWidthMultiplier = new SimpleDoubleProperty(0.4);


    int currentFontSize = defaultFontSize;
    String currentFontFamily = defaultFontFamily;

    double currentTextOpacity = defaultTextOpacity;

    Color currentTextFill = defaultTextFill;
    int currentSpacing = defaultSpacing;
    int currentBackgroundRed = defaultBackgroundRed;
    int currentBackgroundGreen = defaultBackgroundGreen;
    int currentBackgroundBlue = defaultBackgroundBlue;
    double currentBackgroundOpacity = defaultBackgroundOpacity;
    Pos currentTextAlignment = defaultTextAlignment;

    Color currentBackground = defaultBackground;


    Pos captionsLocation = Pos.BOTTOM_CENTER;

    PauseTransition showCaptionsTimer;

    boolean captionsDragActive = false;
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

            if(menuController.menuInTransition){
                e.consume();
                return;
            }

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

        captionsBox.setOnMouseClicked(e -> e.consume());

        captionsBox.setOnDragDetected(e -> {

            dragPositionY = e.getY();
            dragPositionX = e.getX();

            minimumY = 70; // maximum negative translation that can be applied (70px margin from top edge)

            if(menuController.menuOpen) minimumX = 20;
            else minimumX = 70; // 70px from left edge due to the button in top left corner

            maximumY = mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxY() - 80;
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

        Point2D topLeft;
        if(menuController.menuOpen) topLeft = new Point2D(20, 70);
        else topLeft = new Point2D(70, 70);

        Point2D topCenter = new Point2D(mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxX()/2, 70);
        Point2D topRight = new Point2D(mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxX() - 30, 70);
        Point2D centerRight = new Point2D(mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxX() - 30, mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxY()/2);
        Point2D bottomRight = new Point2D(mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxX() - 30,mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxY() - 80);
        Point2D bottomCenter = new Point2D(mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxX()/2, mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxY() - 80);

        Point2D bottomLeft;
        if(menuController.menuOpen) bottomLeft = new Point2D(20, mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxY() - 80);
        else bottomLeft = new Point2D(70, mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxY() - 80);

        Point2D centerLeft;
        if(menuController.menuOpen) centerLeft = new Point2D(20, mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxY()/2);
        else centerLeft = new Point2D(70, mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxY()/2);

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
            if(menuController.menuOpen) translateTransition.setToX(20);
            else translateTransition.setToX(70);
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
            translateTransition.setToY(-80);
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

        if(captionsTransition != null && captionsTransition.getStatus() == Animation.Status.RUNNING){
            captionsTransition.stop();
        }
        if(mainController.captionsLeftTranslate != null && mainController.captionsLeftTranslate.getStatus() == Animation.Status.RUNNING){
            mainController.captionsLeftTranslate.stop();
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

                if(controlBarController.controlBarOpen) captionsBox.setTranslateY(-80);
                else captionsBox.setTranslateY(-30);
            }
            break;
            case BOTTOM_CENTER: {
                captionsBox.setTranslateX(0);

                if(controlBarController.controlBarOpen) captionsBox.setTranslateY(-80);
                else captionsBox.setTranslateY(-30);
            }
            break;
            case BOTTOM_LEFT: {
                if(menuController.menuOpen) captionsBox.setTranslateX(20);
                else captionsBox.setTranslateX(70);

                if(controlBarController.controlBarOpen) captionsBox.setTranslateY(-80);
                else captionsBox.setTranslateY(-30);
            }
            break;
            case CENTER_RIGHT: {
                captionsBox.setTranslateX(-30);
                captionsBox.setTranslateY(0);
            }
            break;
            case CENTER_LEFT: {
                if(menuController.menuOpen) captionsBox.setTranslateX(20);
                else captionsBox.setTranslateX(70);
                captionsBox.setTranslateY(0);
            }
            break;
            case TOP_LEFT: {
                if(menuController.menuOpen) captionsBox.setTranslateX(20);
                else captionsBox.setTranslateX(70);
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

}
