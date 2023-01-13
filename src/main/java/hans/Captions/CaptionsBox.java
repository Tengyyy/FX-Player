package hans.Captions;

import hans.MainController;
import hans.Menu.MenuState;
import hans.Settings.SettingsState;
import javafx.animation.Animation;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
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

import java.util.ArrayList;

public class CaptionsBox {

    CaptionsController captionsController;
    MainController mainController;

    public VBox captionsContainer = new VBox();

    public String defaultFontFamily = "\"Roboto Medium\"";
    public int defaultFontSize = 30;
    public double defaultTextOpacity = 1.0;
    public int defaultSpacing = 10;
    public Color defaultBackgroundColor = Color.rgb(0, 0, 0, 0.75);
    public Color defaultTextColor = Color.WHITE;
    public Pos defaultTextAlignment = Pos.CENTER;

    public DoubleProperty mediaWidthMultiplier = new SimpleDoubleProperty(0.4);

    public StringProperty currentFontFamily = new SimpleStringProperty(defaultFontFamily);
    public DoubleProperty currentFontSize = new SimpleDoubleProperty(defaultFontSize);
    public DoubleProperty currentTextOpacity = new SimpleDoubleProperty(defaultTextOpacity);
    public IntegerProperty currentSpacing = new SimpleIntegerProperty(defaultSpacing);
    public ObjectProperty<Color> currentBackgroundColor = new SimpleObjectProperty<>(defaultBackgroundColor);
    public ObjectProperty<Color> currentTextColor = new SimpleObjectProperty<>(defaultTextColor);
    public ObjectProperty<Pos> currentTextAlignment = new SimpleObjectProperty<>(defaultTextAlignment);

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

        captionsContainer.spacingProperty().bind(mediaWidthMultiplier.multiply(currentSpacing));
        captionsContainer.setTranslateY(-90);
        captionsContainer.minWidthProperty().bind(mediaWidthMultiplier.multiply(400));
        captionsContainer.minHeightProperty().bind(Bindings.createObjectBinding(() -> mediaWidthMultiplier.get() * (currentFontSize.get() * 3 + currentSpacing.get()) + 10, mediaWidthMultiplier, currentFontSize, currentSpacing));
        captionsContainer.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        captionsContainer.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        captionsContainer.alignmentProperty().bind(currentTextAlignment);
        captionsContainer.setVisible(false);
        captionsContainer.setPadding(new Insets(5, 10, 5, 10));
        captionsContainer.opacityProperty().bind(currentTextOpacity);
        captionsContainer.setCursor(Cursor.OPEN_HAND);


        captionsContainer.setOnMousePressed(e -> {

            if (mainController.playbackOptionsPopUp.isShowing()) mainController.playbackOptionsPopUp.hide();

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

            minimumX = 30; // 70px from left edge due to the button in top left corner

            maximumY = mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxY() - 90;
            maximumX = mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxX() - 30;

            startX = captionsContainer.getBoundsInParent().getMinX();
            startY = captionsContainer.getBoundsInParent().getMinY();

            startTranslateY = captionsContainer.getTranslateY();
            startTranslateX = captionsContainer.getTranslateX();


            captionsContainer.startFullDrag();
        });


        StackPane.setAlignment(captionsContainer, Pos.BOTTOM_CENTER);
        mainController.videoImageViewInnerWrapper.getChildren().add(captionsContainer);
    }

    public void toggleVisibility(boolean newValue){
        if(newValue){
            captionsContainer.setVisible(true);

            if(captionsController.menuController.activeItem != null && showCaptionsTimer != null && showCaptionsTimer.getStatus() == Animation.Status.RUNNING){
                showCaptionsTimer.stop();
            }
        }
        else {
            if(showCaptionsTimer == null || showCaptionsTimer.getStatus() != Animation.Status.RUNNING) captionsContainer.setVisible(false);
        }
    }


    public void showCaptions(){
        // if necessary, show captions with text "Captions look like this"

        if(captionsController.menuController.activeItem != null && captionsController.captionsSelected.get()) return;

        if(showCaptionsTimer != null && showCaptionsTimer.getStatus() == Animation.Status.RUNNING){
            showCaptionsTimer.playFromStart();
        }
        else {
             captionsContainer.getChildren().clear();
             captionsContainer.getChildren().add(createLabel("Captions look like this"));
            captionsContainer.setVisible(true);


            showCaptionsTimer = new PauseTransition(Duration.millis(4000));
            showCaptionsTimer.setOnFinished(e -> {
                captionsContainer.setVisible(false);
                captionsContainer.getChildren().clear();
            });

            showCaptionsTimer.playFromStart();
        }
    }

    public Pos findClosestCaptionsPosition(double x, double y){


        Point2D topLeft = new Point2D(30, 70);
        Point2D topCenter = new Point2D(mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxX()/2, 70);
        Point2D topRight = new Point2D(mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxX() - 30, 70);
        Point2D centerRight = new Point2D(mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxX() - 30, mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxY()/2);
        Point2D bottomRight = new Point2D(mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxX() - 30,mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxY() - 90);
        Point2D bottomCenter = new Point2D(mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxX()/2, mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxY() - 90);
        Point2D bottomLeft = new Point2D(30, mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxY() - 90);
        Point2D centerLeft = new Point2D(30, mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxY()/2);

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
            translateTransition.setToX(30);
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

    public Label createLabel(String text){
        Label label = new Label(text);

        ObjectProperty<Background> background = label.backgroundProperty();
        background.bind(Bindings.createObjectBinding(() -> {
            BackgroundFill fill = new BackgroundFill(currentBackgroundColor.getValue(), CornerRadii.EMPTY, Insets.EMPTY);
            return new Background(fill);
        }, currentBackgroundColor));

        label.styleProperty().bind(Bindings.concat("-fx-font-family: ", currentFontFamily, ";",
                                                  "-fx-font-size: ", mediaWidthMultiplier.multiply(currentFontSize).asString(), ";"));
        label.textFillProperty().bind(currentTextColor);
        label.setPadding(new Insets(2, 4, 2, 4));

        return label;
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
                captionsContainer.setTranslateX(30);

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
                captionsContainer.setTranslateX(30);
                captionsContainer.setTranslateY(0);
            }
            break;
            case TOP_LEFT: {
                captionsContainer.setTranslateX(30);
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
