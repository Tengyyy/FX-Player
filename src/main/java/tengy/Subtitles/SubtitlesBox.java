package tengy.Subtitles;

import tengy.MainController;
import tengy.Menu.MenuState;
import tengy.PlaybackSettings.PlaybackSettingsState;
import javafx.animation.Animation;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
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

public class SubtitlesBox {

    SubtitlesController subtitlesController;
    MainController mainController;

    public VBox subtitlesContainer = new VBox();

    public final String defaultFontFamily = "\"Roboto Medium\"";
    public final double defaultFontSize = 30;
    public final double defaultTextOpacity = 1.0;
    public final int defaultSpacing = 10;
    public final Color defaultBackgroundColor = Color.rgb(0, 0, 0, 0.75);
    public final Color defaultTextColor = Color.WHITE;
    public final Pos defaultTextAlignment = Pos.CENTER;
    public final Pos defaultSubtitlesLocation = Pos.BOTTOM_CENTER;

    public DoubleProperty mediaWidthMultiplier = new SimpleDoubleProperty(0.4);


    //preferences keys
    public static final String SUBTITLES_FONT_FAMILY = "subtitles_font_family";
    public static final String SUBTITLES_FONT_SIZE = "subtitles_font_size";
    public static final String SUBTITLES_TEXT_OPACITY = "subtitles_text_opacity";
    public static final String SUBTITLES_SPACING = "subtitles_spacing";
    public static final String SUBTITLES_BACKGROUND_COLOR = "subtitles_background_color";
    public static final String SUBTITLES_TEXT_COLOR = "subtitles_text_color";
    public static final String SUBTITLES_TEXT_ALIGNMENT = "subtitles_text_alignment";
    public static final String SUBTITLES_LOCATION = "subtitles_location";


    public StringProperty currentFontFamily = new SimpleStringProperty();
    public DoubleProperty currentFontSize = new SimpleDoubleProperty();
    public DoubleProperty currentTextOpacity = new SimpleDoubleProperty();
    public IntegerProperty currentSpacing = new SimpleIntegerProperty();
    public ObjectProperty<Color> currentBackgroundColor = new SimpleObjectProperty<>();
    public ObjectProperty<Color> currentTextColor = new SimpleObjectProperty<>();
    public ObjectProperty<Pos> currentTextAlignment = new SimpleObjectProperty<>();
    public Pos subtitlesLocation;

    PauseTransition showSubtitlesTimer;

    public boolean subtitlesDragActive = false;
    public boolean subtitlesAnimating = false;
    public TranslateTransition subtitlesTransition;

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

    SubtitlesBox(SubtitlesController subtitlesController, MainController mainController){

        this.subtitlesController = subtitlesController;
        this.mainController = mainController;

        subtitlesContainer.spacingProperty().bind(mediaWidthMultiplier.multiply(currentSpacing));
        subtitlesContainer.minWidthProperty().bind(mediaWidthMultiplier.multiply(400));
        subtitlesContainer.minHeightProperty().bind(Bindings.createObjectBinding(() -> mediaWidthMultiplier.get() * (currentFontSize.get() * 3 + currentSpacing.get()) + 10, mediaWidthMultiplier, currentFontSize, currentSpacing));
        subtitlesContainer.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        subtitlesContainer.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        subtitlesContainer.alignmentProperty().bind(currentTextAlignment);
        subtitlesContainer.setVisible(false);
        subtitlesContainer.setPadding(new Insets(5, 10, 5, 10));
        subtitlesContainer.opacityProperty().bind(currentTextOpacity);
        subtitlesContainer.setCursor(Cursor.OPEN_HAND);


        subtitlesContainer.setOnMousePressed(e -> {

            if(subtitlesController.menuController.menuInTransition || subtitlesController.menuController.menuState != MenuState.CLOSED){
                e.consume();
                return;
            }

            if(subtitlesController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) subtitlesController.playbackSettingsController.closeSettings();
            else if(subtitlesController.subtitlesState != SubtitlesState.CLOSED) subtitlesController.closeSubtitles();

            subtitlesContainer.setCursor(Cursor.CLOSED_HAND);
            subtitlesContainer.setStyle("-fx-background-color: rgba(0,0,0,0.75);");
            subtitlesDragActive = true;

            if(subtitlesTransition != null && subtitlesTransition.getStatus() == Animation.Status.RUNNING) subtitlesTransition.stop();

            subtitlesController.controlBarController.controlBarWrapper.setMouseTransparent(true);
            subtitlesController.playbackSettingsController.playbackSettingsBuffer.setMouseTransparent(true);
            subtitlesController.subtitlesBuffer.setMouseTransparent(true);
            mainController.menuButtonPane.setMouseTransparent(true);
            mainController.videoImageViewInnerWrapper.setMouseTransparent(true);

            if(showSubtitlesTimer != null && showSubtitlesTimer.getStatus() == Animation.Status.RUNNING) showSubtitlesTimer.pause();

            e.consume();

        });

        subtitlesContainer.setOnMouseReleased(e -> {

            if(!subtitlesDragActive) return;

            subtitlesContainer.setCursor(Cursor.OPEN_HAND);
            subtitlesDragActive = false;
            subtitlesAnimating = true;

            subtitlesController.controlBarController.controlBarWrapper.setMouseTransparent(false);

            if(subtitlesController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) subtitlesController.playbackSettingsController.playbackSettingsBuffer.setMouseTransparent(false);
            else if(subtitlesController.subtitlesState != SubtitlesState.CLOSED) subtitlesController.subtitlesBuffer.setMouseTransparent(false);


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

            if(showSubtitlesTimer != null && showSubtitlesTimer.getStatus() == Animation.Status.PAUSED) showSubtitlesTimer.playFromStart();

            Pos newPosition = findClosestCaptionsPosition(subtitlesContainer.getBoundsInParent().getMinX() + subtitlesContainer.getLayoutBounds().getMaxX()/2, subtitlesContainer.getBoundsInParent().getMinY() + subtitlesContainer.getLayoutBounds().getMaxY()/2);

            Point2D translation = getTranslation(newPosition);

            subtitlesLocation = newPosition;
            StackPane.setAlignment(subtitlesContainer, newPosition);
            subtitlesContainer.setTranslateX(translation.getX());
            subtitlesContainer.setTranslateY(translation.getY());
            mainController.pref.preferences.put(SUBTITLES_LOCATION, subtitlesLocation.toString());

            subtitlesTransition = createTranslateTransition(newPosition);
            subtitlesTransition.setOnFinished(ev -> {
                subtitlesAnimating = false;
                subtitlesContainer.setStyle("-fx-background-color: transparent;");
            });

            subtitlesTransition.play();

            e.consume();

        });

        subtitlesContainer.setOnMouseClicked(Event::consume);

        subtitlesContainer.setOnDragDetected(e -> {

            if(!subtitlesDragActive) return;

            dragPositionY = e.getY();
            dragPositionX = e.getX();

            minimumY = 70; // maximum negative translation that can be applied (70px margin from top edge)

            minimumX = 30; // 70px from left edge due to the button in top left corner

            maximumY = mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxY() - 90;
            maximumX = mainController.videoImageViewInnerWrapper.getLayoutBounds().getMaxX() - 30;

            startX = subtitlesContainer.getBoundsInParent().getMinX();
            startY = subtitlesContainer.getBoundsInParent().getMinY();

            startTranslateY = subtitlesContainer.getTranslateY();
            startTranslateX = subtitlesContainer.getTranslateX();


            subtitlesContainer.startFullDrag();
        });

    }

    public void toggleVisibility(boolean newValue){
        if(newValue){
            subtitlesContainer.setVisible(true);

            if(subtitlesController.menuController.queuePage.queueBox.activeItem.get() != null && showSubtitlesTimer != null && showSubtitlesTimer.getStatus() == Animation.Status.RUNNING){
                showSubtitlesTimer.stop();
            }
        }
        else {
            if(showSubtitlesTimer == null || showSubtitlesTimer.getStatus() != Animation.Status.RUNNING) subtitlesContainer.setVisible(false);
        }
    }


    public void showCaptions(){
        // if necessary, show captions with text "Captions look like this"

        if(subtitlesController.menuController.queuePage.queueBox.activeItem.get() != null && subtitlesController.subtitlesSelected.get()) return;

        if(showSubtitlesTimer != null && showSubtitlesTimer.getStatus() == Animation.Status.RUNNING){
            showSubtitlesTimer.playFromStart();
        }
        else {
             subtitlesContainer.getChildren().clear();
             subtitlesContainer.getChildren().add(createLabel("Captions look like this"));
            subtitlesContainer.setVisible(true);


            showSubtitlesTimer = new PauseTransition(Duration.millis(4000));
            showSubtitlesTimer.setOnFinished(e -> {
                subtitlesContainer.setVisible(false);
                subtitlesContainer.getChildren().clear();
            });

            showSubtitlesTimer.playFromStart();
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

        Bounds bounds = subtitlesContainer.getBoundsInParent();
        Bounds layoutBounds = subtitlesContainer.getLayoutBounds();

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

        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(300), subtitlesContainer);

        translateTransition.setFromX(subtitlesContainer.getTranslateX());
        translateTransition.setFromY(subtitlesContainer.getTranslateY());

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
        if(!subtitlesDragActive) return;

        subtitlesContainer.setCursor(Cursor.OPEN_HAND);
        subtitlesDragActive = false;

        subtitlesController.controlBarController.controlBarWrapper.setMouseTransparent(false);

        if(subtitlesController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) subtitlesController.playbackSettingsController.playbackSettingsBuffer.setMouseTransparent(false);
        else if(subtitlesController.subtitlesState != SubtitlesState.CLOSED) subtitlesController.subtitlesBuffer.setMouseTransparent(false);


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

        if(showSubtitlesTimer != null && showSubtitlesTimer.getStatus() == Animation.Status.PAUSED) showSubtitlesTimer.playFromStart();

        Pos newPosition = findClosestCaptionsPosition(subtitlesContainer.getBoundsInParent().getMinX() + subtitlesContainer.getLayoutBounds().getMaxX()/2, subtitlesContainer.getBoundsInParent().getMinY() + subtitlesContainer.getLayoutBounds().getMaxY()/2);

        Point2D translation = getTranslation(newPosition);

        subtitlesLocation = newPosition;
        StackPane.setAlignment(subtitlesContainer, newPosition);
        subtitlesContainer.setTranslateX(translation.getX());
        subtitlesContainer.setTranslateY(translation.getY());

        subtitlesTransition = createTranslateTransition(newPosition);
        subtitlesTransition.setOnFinished(ev -> {
            subtitlesAnimating = false;
            subtitlesContainer.setStyle("-fx-background-color: transparent;");
        });

        subtitlesTransition.play();
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
        subtitlesContainer.setMouseTransparent(true);

        if(subtitlesTransition != null && subtitlesTransition.getStatus() == Animation.Status.RUNNING) {
            subtitlesTransition.stop();
        }

        mainController.videoImageViewInnerWrapper.getChildren().remove(subtitlesContainer);
        mainController.miniplayer.miniplayerController.videoImageViewInnerWrapper.getChildren().add(subtitlesContainer);

        switch(subtitlesLocation){
            case BOTTOM_RIGHT: {
                subtitlesContainer.setTranslateX(-10);
                subtitlesContainer.setTranslateY(-10);
            }
            break;
            case BOTTOM_CENTER: {
                subtitlesContainer.setTranslateX(0);
                subtitlesContainer.setTranslateY(-10);
            }
            break;
            case BOTTOM_LEFT: {
                subtitlesContainer.setTranslateX(10);
                subtitlesContainer.setTranslateY(-10);
            }
            break;
            case CENTER_RIGHT: {
                subtitlesContainer.setTranslateX(-10);
                subtitlesContainer.setTranslateY(0);
            }
            break;
            case CENTER_LEFT: {
                subtitlesContainer.setTranslateX(10);
                subtitlesContainer.setTranslateY(0);
            }
            break;
            case TOP_LEFT: {
                subtitlesContainer.setTranslateX(10);
                subtitlesContainer.setTranslateY(10);
            }
            break;
            case TOP_CENTER: {
                subtitlesContainer.setTranslateX(0);
                subtitlesContainer.setTranslateY(10);
            }
            case TOP_RIGHT: {
                subtitlesContainer.setTranslateX(-10);
                subtitlesContainer.setTranslateY(10);
            }
        }
    }

    public void moveToMainplayer(){
        subtitlesContainer.setMouseTransparent(false);


        mainController.miniplayer.miniplayerController.videoImageViewInnerWrapper.getChildren().remove(subtitlesContainer);
        mainController.videoImageViewInnerWrapper.getChildren().add(subtitlesContainer);

        setTranslation();
    }

    private void setTranslation(){
        switch(subtitlesLocation){
            case BOTTOM_RIGHT: {
                subtitlesContainer.setTranslateX(-30);

                if(subtitlesController.controlBarController.controlBarShowing) subtitlesContainer.setTranslateY(-90);
                else subtitlesContainer.setTranslateY(-30);
            }
            break;
            case BOTTOM_CENTER: {
                subtitlesContainer.setTranslateX(0);

                if(subtitlesController.controlBarController.controlBarShowing) subtitlesContainer.setTranslateY(-90);
                else subtitlesContainer.setTranslateY(-30);
            }
            break;
            case BOTTOM_LEFT: {
                subtitlesContainer.setTranslateX(30);

                if(subtitlesController.controlBarController.controlBarShowing) subtitlesContainer.setTranslateY(-90);
                else subtitlesContainer.setTranslateY(-30);
            }
            break;
            case CENTER_RIGHT: {
                subtitlesContainer.setTranslateX(-30);
                subtitlesContainer.setTranslateY(0);
            }
            break;
            case CENTER_LEFT: {
                subtitlesContainer.setTranslateX(30);
                subtitlesContainer.setTranslateY(0);
            }
            break;
            case TOP_LEFT: {
                subtitlesContainer.setTranslateX(30);
                subtitlesContainer.setTranslateY(70);
            }
            break;
            case TOP_CENTER: {
                subtitlesContainer.setTranslateX(0);
                subtitlesContainer.setTranslateY(70);
            }
            case TOP_RIGHT: {
                subtitlesContainer.setTranslateX(-30);
                subtitlesContainer.setTranslateY(70);
            }
        }
    }


    public void loadSubtitlePreferences(){
        String fontFamily = mainController.pref.preferences.get(SUBTITLES_FONT_FAMILY, defaultFontFamily);
        double fontSize = mainController.pref.preferences.getDouble(SUBTITLES_FONT_SIZE, 1.0);
        double textOpacity = mainController.pref.preferences.getDouble(SUBTITLES_TEXT_OPACITY, defaultTextOpacity);
        double spacing = mainController.pref.preferences.getDouble(SUBTITLES_SPACING, 1.0);
        Color backgroundColor = Color.valueOf(mainController.pref.preferences.get(SUBTITLES_BACKGROUND_COLOR, defaultBackgroundColor.toString()));
        Color textColor = Color.valueOf(mainController.pref.preferences.get(SUBTITLES_TEXT_COLOR, defaultTextColor.toString()));
        Pos textAlignment = Pos.valueOf(mainController.pref.preferences.get(SUBTITLES_TEXT_ALIGNMENT, defaultTextAlignment.toString()));
        subtitlesLocation = Pos.valueOf(mainController.pref.preferences.get(SUBTITLES_LOCATION, defaultSubtitlesLocation.toString()));

        StackPane.setAlignment(subtitlesContainer, subtitlesLocation);
        setTranslation();

        currentBackgroundColor.set(backgroundColor);

        subtitlesController.subtitlesOptionsPane.fontFamilyPane.setInitialValue(fontFamily);
        subtitlesController.subtitlesOptionsPane.fontSizePane.setInitialValue(fontSize);
        subtitlesController.subtitlesOptionsPane.fontOpacityPane.setInitialValue(textOpacity);
        subtitlesController.subtitlesOptionsPane.lineSpacingPane.setInitialValue(spacing);
        subtitlesController.subtitlesOptionsPane.backgroundOpacityPane.setInitialValue(backgroundColor.getOpacity());
        subtitlesController.subtitlesOptionsPane.backgroundColorPane.setInitialValue(backgroundColor);
        subtitlesController.subtitlesOptionsPane.fontColorPane.setInitialValue(textColor);
        subtitlesController.subtitlesOptionsPane.textAlignmentPane.setInitialValue(textAlignment);

    }
}
