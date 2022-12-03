package hans.Captions;

import hans.MainController;
import hans.Menu.MenuState;
import hans.Settings.SettingsState;
import javafx.animation.Animation;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.DoubleProperty;
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

import java.util.ArrayList;

public class CaptionsBox {

    CaptionsController captionsController;
    MainController mainController;


    public VBox captionsContainer = new VBox();
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


    public Pos captionsLocation = Pos.BOTTOM_CENTER;

    PauseTransition showCaptionsTimer;

    public boolean captionsDragActive = false;
    public boolean captionsAnimating = false;
    public TranslateTransition captionsTransition;

    public double dragPositionY = 0;
    public double dragPositionX = 0;

    public double minimumY = 0;
    public double maximumY = 0;

    public double minimumX = 0;
    public double maximumX = 0;

    public double startY = 0;
    public double startX = 0;

    public double startTranslateY = 0;
    public double startTranslateX = 0;

    CaptionsBox(CaptionsController captionsController, MainController mainController){

        this.captionsController = captionsController;
        this.mainController = mainController;

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


        captionsContainer.setSpacing(mediaWidthMultiplier.multiply(defaultSpacing).get());
        captionsContainer.setTranslateY(-50);
        captionsContainer.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        captionsContainer.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        captionsContainer.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        captionsContainer.getChildren().addAll(captionsLabel1, captionsLabel2);
        captionsContainer.setAlignment(defaultTextAlignment);
        captionsContainer.setVisible(false);
        captionsContainer.setPadding(new Insets(5, 10, 5, 10));
        captionsContainer.setOpacity(defaultTextOpacity);
        captionsContainer.setCursor(Cursor.OPEN_HAND);


        captionsContainer.setOnMousePressed(e -> {

            if(captionsController.menuController.menuInTransition || captionsController.menuController.menuState != MenuState.CLOSED){
                e.consume();
                return;
            }

            if(captionsController.settingsController.settingsState != SettingsState.CLOSED) captionsController.settingsController.closeSettings();
            else if(captionsController.captionsState != CaptionsState.CLOSED) captionsController.closeCaptions();

            captionsContainer.setCursor(Cursor.CLOSED_HAND);
            captionsContainer.setStyle("-fx-background-color: rgba(0,0,0,0.75);");
            captionsDragActive = true;

            if(captionsTransition != null && captionsTransition.getStatus() == Animation.Status.RUNNING) captionsTransition.stop();

            captionsController.controlBarController.controlBarWrapper.setMouseTransparent(true);
            captionsController.settingsController.settingsBuffer.setMouseTransparent(true);
            captionsController.captionsBuffer.setMouseTransparent(true);
            mainController.menuButtonPane.setMouseTransparent(true);
            mainController.videoImageViewInnerWrapper.setMouseTransparent(true);

            if(showCaptionsTimer != null && showCaptionsTimer.getStatus() == Animation.Status.RUNNING) showCaptionsTimer.pause();

            e.consume();

        });

        captionsContainer.setOnMouseReleased(e -> {

            if(!captionsDragActive) return;

            captionsContainer.setCursor(Cursor.OPEN_HAND);
            captionsDragActive = false;
            captionsAnimating = true;

            captionsController.controlBarController.controlBarWrapper.setMouseTransparent(false);

            if(captionsController.settingsController.settingsState != SettingsState.CLOSED) captionsController.settingsController.settingsBuffer.setMouseTransparent(false);
            else if(captionsController.captionsState != CaptionsState.CLOSED) captionsController.captionsBuffer.setMouseTransparent(false);


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

            Pos newPosition = findClosestCaptionsPosition(captionsContainer.getBoundsInParent().getMinX() + captionsContainer.getLayoutBounds().getMaxX()/2, captionsContainer.getBoundsInParent().getMinY() + captionsContainer.getLayoutBounds().getMaxY()/2);

            Point2D translation = getTranslation(newPosition);

            captionsLocation = newPosition;
            StackPane.setAlignment(captionsContainer, newPosition);
            captionsContainer.setTranslateX(translation.getX());
            captionsContainer.setTranslateY(translation.getY());

            captionsTransition = createTranslateTransition(newPosition);
            captionsTransition.setOnFinished(ev -> {
                captionsAnimating = false;
                captionsContainer.setStyle("-fx-background-color: transparent;");
            });

            captionsTransition.play();

            e.consume();

        });

        captionsContainer.setOnMouseClicked(Event::consume);

        captionsContainer.setOnDragDetected(e -> {

            if(!captionsDragActive) return;

            dragPositionY = e.getY();
            dragPositionX = e.getX();

            minimumY = 70; // maximum negative translation that can be applied (70px margin from top edge)

            minimumX = 70; // 70px from left edge due to the button in top left corner

            maximumY = mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxY() - 90;
            maximumX = mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxX() - 30;

            startX = captionsContainer.getBoundsInParent().getMinX();
            startY = captionsContainer.getBoundsInParent().getMinY();

            startTranslateY = captionsContainer.getTranslateY();
            startTranslateX = captionsContainer.getTranslateX();


            captionsContainer.startFullDrag();
        });


        StackPane.setAlignment(captionsContainer, Pos.BOTTOM_CENTER);
        mainController.videoImageViewInnerWrapper.getChildren().add(1, captionsContainer);


        captionsController.captionsOn.addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                captionsContainer.setVisible(true);

                if(captionsController.menuController.activeItem != null && captionsController.captionsSelected && showCaptionsTimer != null && showCaptionsTimer.getStatus() == Animation.Status.RUNNING){
                    showCaptionsTimer.stop();
                }

                captionsLabel1.setOpacity(0);
                captionsLabel2.setOpacity(0);
            }
            else {
                if(showCaptionsTimer == null || showCaptionsTimer.getStatus() != Animation.Status.RUNNING) captionsContainer.setVisible(false);
            }

            if(captionsController.menuController.activeItem != null){
                captionsController.menuController.activeItem.getMediaItem().setSubtitlesOn(newValue);
            }
        });
    }


    public void resizeCaptions(){

        captionsLabel1.setStyle("-fx-font-family: " + currentFontFamily + "; -fx-font-size: " + mediaWidthMultiplier.multiply(currentFontSize).get());
        captionsLabel2.setStyle("-fx-font-family: " + currentFontFamily + "; -fx-font-size: " + mediaWidthMultiplier.multiply(currentFontSize).get());

        captionsContainer.setSpacing(mediaWidthMultiplier.multiply(currentSpacing).get());
    }


    public void showCaptions(){
        // if necessary, show captions with text "Captions look like this"

        if(captionsController.menuController.activeItem != null && captionsController.captionsSelected && captionsController.captionsOn.get()) return;

        if(showCaptionsTimer != null && showCaptionsTimer.getStatus() == Animation.Status.RUNNING){
            showCaptionsTimer.playFromStart();
        }
        else {

            captionsContainer.setVisible(true);
            captionsLabel1.setOpacity(0);

            captionsLabel2.setOpacity(1);
            captionsLabel2.setText("Captions look like this");

            showCaptionsTimer = new PauseTransition(Duration.millis(4000));
            showCaptionsTimer.setOnFinished(e -> {
                captionsContainer.setVisible(false);
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

                currentClosestPosition = switch (i) {
                    case 0 -> Pos.TOP_LEFT;
                    case 1 -> Pos.TOP_CENTER;
                    case 2 -> Pos.TOP_RIGHT;
                    case 3 -> Pos.CENTER_RIGHT;
                    case 4 -> Pos.BOTTOM_RIGHT;
                    case 5 -> Pos.BOTTOM_CENTER;
                    case 6 -> Pos.BOTTOM_LEFT;
                    case 7 -> Pos.CENTER_LEFT;
                    default -> null;
                };
            }
        }

        return currentClosestPosition;
    }

    public Point2D getTranslation(Pos position){

        Bounds bounds = captionsContainer.getBoundsInParent();
        Bounds layoutBounds = captionsContainer.getLayoutBounds();

        Point2D translation = null;

        switch (position) {
            case TOP_LEFT -> translation = new Point2D(bounds.getMinX(), bounds.getMinY());
            case TOP_CENTER -> translation = new Point2D(bounds.getMinX() - (mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxX() / 2 - layoutBounds.getMaxX() / 2), bounds.getMinY());
            case TOP_RIGHT -> translation = new Point2D(bounds.getMinX() - (mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxX() - layoutBounds.getMaxX()), bounds.getMinY());
            case CENTER_RIGHT -> translation = new Point2D(bounds.getMinX() - (mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxX() - layoutBounds.getMaxX()), bounds.getMinY() - (mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxY() / 2 - layoutBounds.getMaxY() / 2));
            case BOTTOM_RIGHT -> translation = new Point2D(bounds.getMinX() - (mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxX() - layoutBounds.getMaxX()), bounds.getMinY() - (mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxY() - layoutBounds.getMaxY()));
            case BOTTOM_CENTER -> translation = new Point2D(bounds.getMinX() - (mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxX() / 2 - layoutBounds.getMaxX() / 2), bounds.getMinY() - (mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxY() - layoutBounds.getMaxY()));
            case BOTTOM_LEFT -> translation = new Point2D(bounds.getMinX(), bounds.getMinY() - (mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxY() - layoutBounds.getMaxY()));
            case CENTER_LEFT -> translation = new Point2D(bounds.getMinX(), bounds.getMinY() - (mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxY() / 2 - layoutBounds.getMaxY() / 2));
            default -> {
            }
        }

        return translation;
    }


    public TranslateTransition createTranslateTransition(Pos position){

        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(300), captionsContainer);

        translateTransition.setFromX(captionsContainer.getTranslateX());
        translateTransition.setFromY(captionsContainer.getTranslateY());

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

        captionsContainer.setCursor(Cursor.OPEN_HAND);
        captionsDragActive = false;

        captionsController.controlBarController.controlBarWrapper.setMouseTransparent(false);

        if(captionsController.settingsController.settingsState != SettingsState.CLOSED) captionsController.settingsController.settingsBuffer.setMouseTransparent(false);
        else if(captionsController.captionsState != CaptionsState.CLOSED) captionsController.captionsBuffer.setMouseTransparent(false);


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

        Pos newPosition = findClosestCaptionsPosition(captionsContainer.getBoundsInParent().getMinX() + captionsContainer.getLayoutBounds().getMaxX()/2, captionsContainer.getBoundsInParent().getMinY() + captionsContainer.getLayoutBounds().getMaxY()/2);

        Point2D translation = getTranslation(newPosition);

        captionsLocation = newPosition;
        StackPane.setAlignment(captionsContainer, newPosition);
        captionsContainer.setTranslateX(translation.getX());
        captionsContainer.setTranslateY(translation.getY());

        captionsTransition = createTranslateTransition(newPosition);
        captionsTransition.setOnFinished(ev -> {
            captionsAnimating = false;
            captionsContainer.setStyle("-fx-background-color: transparent;");
        });

        captionsTransition.play();
    }

    public void moveToMiniplayer(){
        captionsContainer.setMouseTransparent(true);

        if(captionsTransition != null && captionsTransition.getStatus() == Animation.Status.RUNNING) {
            captionsTransition.stop();
        }

        mainController.videoImageViewInnerWrapper.getChildren().remove(captionsContainer);
        mainController.miniplayer.miniplayerController.videoImageViewInnerWrapper.getChildren().add(captionsContainer);

        switch(captionsLocation){
            case BOTTOM_RIGHT: {
                captionsContainer.setTranslateX(-10);
                captionsContainer.setTranslateY(-10);
            }
            break;
            case BOTTOM_CENTER: {
                captionsContainer.setTranslateX(0);
                captionsContainer.setTranslateY(-10);
            }
            break;
            case BOTTOM_LEFT: {
                captionsContainer.setTranslateX(10);
                captionsContainer.setTranslateY(-10);
            }
            break;
            case CENTER_RIGHT: {
                captionsContainer.setTranslateX(-10);
                captionsContainer.setTranslateY(0);
            }
            break;
            case CENTER_LEFT: {
                captionsContainer.setTranslateX(10);
                captionsContainer.setTranslateY(0);
            }
            break;
            case TOP_LEFT: {
                captionsContainer.setTranslateX(10);
                captionsContainer.setTranslateY(10);
            }
            break;
            case TOP_CENTER: {
                captionsContainer.setTranslateX(0);
                captionsContainer.setTranslateY(10);
            }
            case TOP_RIGHT: {
                captionsContainer.setTranslateX(-10);
                captionsContainer.setTranslateY(10);
            }
        }
    }

    public void moveToMainplayer(){
        captionsContainer.setMouseTransparent(false);


        mainController.miniplayer.miniplayerController.videoImageViewInnerWrapper.getChildren().remove(captionsContainer);
        mainController.videoImageViewInnerWrapper.getChildren().add(captionsContainer);

        switch(captionsLocation){
            case BOTTOM_RIGHT: {
                captionsContainer.setTranslateX(-30);

                if(captionsController.controlBarController.controlBarOpen) captionsContainer.setTranslateY(-90);
                else captionsContainer.setTranslateY(-30);
            }
            break;
            case BOTTOM_CENTER: {
                captionsContainer.setTranslateX(0);

                if(captionsController.controlBarController.controlBarOpen) captionsContainer.setTranslateY(-90);
                else captionsContainer.setTranslateY(-30);
            }
            break;
            case BOTTOM_LEFT: {
                captionsContainer.setTranslateX(70);

                if(captionsController.controlBarController.controlBarOpen) captionsContainer.setTranslateY(-90);
                else captionsContainer.setTranslateY(-30);
            }
            break;
            case CENTER_RIGHT: {
                captionsContainer.setTranslateX(-30);
                captionsContainer.setTranslateY(0);
            }
            break;
            case CENTER_LEFT: {
                captionsContainer.setTranslateX(70);
                captionsContainer.setTranslateY(0);
            }
            break;
            case TOP_LEFT: {
                captionsContainer.setTranslateX(70);
                captionsContainer.setTranslateY(70);
            }
            break;
            case TOP_CENTER: {
                captionsContainer.setTranslateX(0);
                captionsContainer.setTranslateY(70);
            }
            case TOP_RIGHT: {
                captionsContainer.setTranslateX(-30);
                captionsContainer.setTranslateY(70);
            }
        }
    }
}