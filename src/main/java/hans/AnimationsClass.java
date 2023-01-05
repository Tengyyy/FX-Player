package hans;

import hans.Menu.MenuController;
import hans.Captions.CaptionsController;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.List;

public class AnimationsClass {

    public static final double ANIMATION_SPEED = 200;
    static TranslateTransition volumeSliderTranslateTransition1;
    static TranslateTransition volumeSliderTranslateTransition2;
    static TranslateTransition volumeSliderTranslateTransition3;


    static ParallelTransition nextVideoNotificationOnTransition;
    static ParallelTransition nextVideoNotificationOffTransition;




    public static void volumeSliderHoverOn(Slider volumeSlider, Label durationLabel, ProgressBar volumeTrack) {
        volumeSliderTranslateTransition1 = new TranslateTransition(Duration.millis(100), volumeSlider);
        volumeSliderTranslateTransition1.setFromX(-60);
        volumeSliderTranslateTransition1.setToX(0);
        volumeSliderTranslateTransition1.setInterpolator(Interpolator.EASE_OUT);
        volumeSliderTranslateTransition1.play();

        volumeSliderTranslateTransition2 = new TranslateTransition(Duration.millis(100), durationLabel);
        volumeSliderTranslateTransition2.setFromX(-60);
        volumeSliderTranslateTransition2.setToX(0);
        volumeSliderTranslateTransition2.setInterpolator(Interpolator.EASE_OUT);
        volumeSliderTranslateTransition2.play();

        volumeSliderTranslateTransition3 = new TranslateTransition(Duration.millis(100), volumeTrack);
        volumeSliderTranslateTransition3.setFromX(-60);
        volumeSliderTranslateTransition3.setToX(0);
        volumeSliderTranslateTransition3.setInterpolator(Interpolator.EASE_OUT);
        volumeSliderTranslateTransition3.play();
    }

    public static void volumeSliderHoverOff(Slider volumeSlider, Label durationLabel, ProgressBar volumeTrack) {
        volumeSliderTranslateTransition1.stop();
        volumeSliderTranslateTransition1 = new TranslateTransition(Duration.millis(100), volumeSlider);
        volumeSliderTranslateTransition1.setFromX(0);
        volumeSliderTranslateTransition1.setToX(-60);
        volumeSliderTranslateTransition1.setInterpolator(Interpolator.EASE_OUT);
        volumeSliderTranslateTransition1.play();

        volumeSliderTranslateTransition2.stop();
        volumeSliderTranslateTransition2 = new TranslateTransition(Duration.millis(100), durationLabel);
        volumeSliderTranslateTransition2.setFromX(0);
        volumeSliderTranslateTransition2.setToX(-60);
        volumeSliderTranslateTransition2.setInterpolator(Interpolator.EASE_OUT);
        volumeSliderTranslateTransition2.play();

        volumeSliderTranslateTransition3.stop();
        volumeSliderTranslateTransition3 = new TranslateTransition(Duration.millis(100), volumeTrack);
        volumeSliderTranslateTransition3.setFromX(0);
        volumeSliderTranslateTransition3.setToX(-60);
        volumeSliderTranslateTransition3.setInterpolator(Interpolator.EASE_OUT);
        volumeSliderTranslateTransition3.play();
    }

    public static void displayControls(ControlBarController controlBarController, CaptionsController captionsController, MainController mainController) {

        controlBarController.controlBarOpen = true;

        TranslateTransition captionsTransition = new TranslateTransition(Duration.millis(100), captionsController.captionsBox.captionsContainer);
        captionsTransition.setCycleCount(1);
        captionsTransition.setFromY(captionsController.captionsBox.captionsContainer.getTranslateY());

        if((captionsController.captionsBox.captionsLocation == Pos.BOTTOM_CENTER || captionsController.captionsBox.captionsLocation == Pos.BOTTOM_LEFT || captionsController.captionsBox.captionsLocation == Pos.BOTTOM_RIGHT) && !mainController.miniplayerActive){
            captionsTransition.setToY(-90);
        }
        else if((captionsController.captionsBox.captionsLocation == Pos.TOP_CENTER || captionsController.captionsBox.captionsLocation == Pos.TOP_LEFT || captionsController.captionsBox.captionsLocation == Pos.TOP_RIGHT) && !mainController.miniplayerActive){
            captionsTransition.setToY(70);
        }
        else {
            captionsTransition.setToY(captionsController.captionsBox.captionsContainer.getTranslateY());
        }
        FadeTransition controlBarFade = new FadeTransition(Duration.millis(100), controlBarController.controlBarWrapper);
        controlBarFade.setFromValue(controlBarController.controlBarWrapper.getOpacity());
        controlBarFade.setToValue(1);
        controlBarFade.setCycleCount(1);

        FadeTransition controlBarBackgroundFade = new FadeTransition(Duration.millis(100), controlBarController.controlBarBackground);
        controlBarBackgroundFade.setFromValue(controlBarController.controlBarBackground.getOpacity());
        controlBarBackgroundFade.setToValue(1);
        controlBarBackgroundFade.setCycleCount(1);


        mainController.videoTitleBox.setVisible(true);
        FadeTransition videoTitleTransition = new FadeTransition(Duration.millis(100), mainController.videoTitleBox);
        videoTitleTransition.setFromValue(mainController.videoTitleBox.getOpacity());
        videoTitleTransition.setToValue(1);

        mainController.videoTitleBackground.setVisible(true);
        FadeTransition videoTitleBackgroundTransition = new FadeTransition(Duration.millis(100), mainController.videoTitleBackground);
        videoTitleTransition.setFromValue(mainController.videoTitleBackground.getOpacity());
        videoTitleTransition.setToValue(1);


        ParallelTransition parallelTransition = new ParallelTransition(captionsTransition, controlBarFade, controlBarBackgroundFade, videoTitleTransition, videoTitleBackgroundTransition);
        parallelTransition.setInterpolator(Interpolator.LINEAR);

        parallelTransition.play();

        controlBarController.mainController.menuButton.setVisible(true);
    }

    public static void hideControls(ControlBarController controlBarController, CaptionsController captionsController, MainController mainController) {

        if(captionsController.captionsBox.captionsTransition != null && captionsController.captionsBox.captionsTransition.getStatus() == Animation.Status.RUNNING) captionsController.captionsBox.captionsTransition.stop();

        TranslateTransition captionsTransition = new TranslateTransition(Duration.millis(100), captionsController.captionsBox.captionsContainer);
        captionsTransition.setCycleCount(1);
        captionsTransition.setFromY(captionsController.captionsBox.captionsContainer.getTranslateY());

        if((captionsController.captionsBox.captionsLocation == Pos.BOTTOM_CENTER || captionsController.captionsBox.captionsLocation == Pos.BOTTOM_LEFT || captionsController.captionsBox.captionsLocation == Pos.BOTTOM_RIGHT) && !mainController.miniplayerActive){
            captionsTransition.setToY(-30);
        }
        else if((captionsController.captionsBox.captionsLocation == Pos.TOP_CENTER || captionsController.captionsBox.captionsLocation == Pos.TOP_LEFT || captionsController.captionsBox.captionsLocation == Pos.TOP_RIGHT) && !mainController.miniplayerActive){
            captionsTransition.setToY(30);
        }
        else {
            captionsTransition.setToY(captionsController.captionsBox.captionsContainer.getTranslateY());
        }

        FadeTransition controlBarFade = new FadeTransition(Duration.millis(100), controlBarController.controlBarWrapper);
        controlBarFade.setFromValue(controlBarController.controlBarWrapper.getOpacity());
        controlBarFade.setToValue(0);
        controlBarFade.setCycleCount(1);

        FadeTransition controlBarBackgroundFade = new FadeTransition(Duration.millis(100), controlBarController.controlBarBackground);
        controlBarBackgroundFade.setFromValue(controlBarController.controlBarBackground.getOpacity());
        controlBarBackgroundFade.setToValue(0);
        controlBarBackgroundFade.setCycleCount(1);


        FadeTransition videoTitleTransition = new FadeTransition(Duration.millis(100), mainController.videoTitleBox);
        videoTitleTransition.setFromValue(mainController.videoTitleBox.getOpacity());
        videoTitleTransition.setToValue(0);

        FadeTransition videoTitleBackgroundTransition = new FadeTransition(Duration.millis(100), mainController.videoTitleBackground);
        videoTitleTransition.setFromValue(mainController.videoTitleBackground.getOpacity());
        videoTitleTransition.setToValue(0);


        ParallelTransition parallelTransition = new ParallelTransition(captionsTransition, controlBarFade, controlBarBackgroundFade, videoTitleTransition, videoTitleBackgroundTransition);
        parallelTransition.setInterpolator(Interpolator.LINEAR);
        parallelTransition.setOnFinished((e) -> {
            controlBarController.controlBarOpen = false;
            controlBarController.mouseEventTracker.mouseMoving.set(false);
            mainController.videoTitleBox.setVisible(false);
            mainController.videoTitleBackground.setVisible(false);
            captionsController.captionsBox.captionsAnimating = false;
            captionsController.captionsBox.captionsContainer.setStyle("-fx-background-color: transparent;");
        });
        parallelTransition.play();

    }


    public static FadeTransition fadeAnimation(double duration, Region node, double fromValue, double toValue, boolean autoReverse, int cycleCount, boolean play){
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setDuration(Duration.millis(duration));
        fadeTransition.setNode(node);
        fadeTransition.setFromValue(fromValue);
        fadeTransition.setToValue(toValue);
        fadeTransition.setAutoReverse(autoReverse);
        fadeTransition.setCycleCount(cycleCount);
        if(play) fadeTransition.play();

        return fadeTransition;
    }

    public static ScaleTransition scaleAnimation(double duration, Node node, double fromX, double toX, double fromY, double toY, boolean autoReverse, int cycleCount, boolean play){
        ScaleTransition scaleTransition = new ScaleTransition();
        scaleTransition.setDuration(Duration.millis(duration));
        scaleTransition.setNode(node);
        scaleTransition.setFromX(fromX);
        scaleTransition.setToX(toX);
        scaleTransition.setFromY(fromY);
        scaleTransition.setToY(toY);
        scaleTransition.setAutoReverse(autoReverse);
        scaleTransition.setCycleCount(cycleCount);
        if(play) scaleTransition.play();

        return scaleTransition;
    }

    public static void rotateTransition(double duration, Node node, double fromValue, double toValue, boolean autoReverse, int cycleCount, boolean play){
        RotateTransition rotateTransition = new RotateTransition();
        rotateTransition.setDuration(Duration.millis(duration));
        rotateTransition.setNode(node);
        rotateTransition.setFromAngle(fromValue);
        rotateTransition.setToAngle(toValue);
        rotateTransition.setAutoReverse(autoReverse);
        rotateTransition.setCycleCount(cycleCount);
        if(play) rotateTransition.play();

    }

    public static void parallelAnimation(boolean play, List<Transition> transitions){
        ParallelTransition parallelTransition = new ParallelTransition();
        for(Transition transition :  transitions){
            if(transition != null) parallelTransition.getChildren().add(transition);
        }

        if(play) parallelTransition.play();

    }

    public static ParallelTransition parallelAnimation(boolean play, Transition... transitions){
        ParallelTransition parallelTransition = new ParallelTransition();
        for(Transition transition :  transitions){
            if(transition != null) parallelTransition.getChildren().add(transition);
        }

        if(play) parallelTransition.play();

        return parallelTransition;
    }

    public static SequentialTransition sequentialAnimation(boolean play, Transition... transitions){
        SequentialTransition sequentialTransition = new SequentialTransition();
        for(Transition transition :  transitions){
            sequentialTransition.getChildren().add(transition);
        }

        if(play) sequentialTransition.play();

        return sequentialTransition;
    }


    public static void openMenuNotification(MenuController menuController){

        menuController.menuNotificationOpen = true;

        if(nextVideoNotificationOffTransition != null && nextVideoNotificationOffTransition.getStatus() == Animation.Status.RUNNING){
            nextVideoNotificationOffTransition.stop();
        }

        menuController.notificationPane.setOpacity(1);

        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(300), menuController.notificationPane);
        translateTransition.setFromY(menuController.notificationPane.getTranslateY());
        translateTransition.setToY(0);
        translateTransition.setCycleCount(1);
        translateTransition.setInterpolator(Interpolator.EASE_OUT);

        FadeTransition fadeTransition = new FadeTransition(Duration.millis(300), menuController.notificationPane);
        fadeTransition.setFromValue(menuController.notificationPane.getOpacity());
        fadeTransition.setToValue(1);
        fadeTransition.setCycleCount(1);
        fadeTransition.setInterpolator(Interpolator.EASE_OUT);

        nextVideoNotificationOnTransition = new ParallelTransition(translateTransition, fadeTransition);
        nextVideoNotificationOnTransition.setOnFinished((e) -> menuController.closeTimer.playFromStart());
        nextVideoNotificationOnTransition.playFromStart();

    }

    public static void closeMenuNotification(MenuController menuController){
        menuController.menuNotificationOpen = false;

        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(300), menuController.notificationPane);
        translateTransition.setFromY(menuController.notificationPane.getTranslateY());
        translateTransition.setToY(60);
        translateTransition.setCycleCount(1);
        translateTransition.setInterpolator(Interpolator.EASE_OUT);

        FadeTransition fadeTransition = new FadeTransition(Duration.millis(300), menuController.notificationPane);
        fadeTransition.setFromValue(menuController.notificationPane.getOpacity());
        fadeTransition.setToValue(0);
        fadeTransition.setCycleCount(1);
        fadeTransition.setInterpolator(Interpolator.EASE_OUT);

        nextVideoNotificationOnTransition = new ParallelTransition(translateTransition, fadeTransition);
        nextVideoNotificationOnTransition.playFromStart();
    }


    public static void openMenu(MenuController menuController, MainController mainController){

        TranslateTransition openMenu = new TranslateTransition(Duration.millis(300), menuController.menu);
        openMenu.setFromX(menuController.menu.getTranslateX());
        openMenu.setToX(0);
        openMenu.setInterpolator(Interpolator.EASE_OUT);

        openMenu.setOnFinished((e) -> {
            menuController.menu.setMouseTransparent(false);
            menuController.menuInTransition = false;
        });

        openMenu.play();

    }

    public static void closeMenu(MenuController menuController, MainController mainController) {

        TranslateTransition closeMenu = new TranslateTransition(Duration.millis(300), menuController.menu);
        closeMenu.setFromX(menuController.menu.getTranslateX());
        closeMenu.setToX(-menuController.menu.getWidth());

        closeMenu.setOnFinished((e) -> {
            //TODO: only reset the variables relevant to the current menu state
            menuController.menuInTransition = false;
            menuController.metadataEditScroll.setVisible(false);
            menuController.technicalDetailsScroll.setVisible(false);
            menuController.queueScroll.setVisible(true);

            menuController.metadataEditPage.imageRemoved = false;
            menuController.metadataEditPage.newColor = null;
            menuController.metadataEditPage.newImage = null;
            menuController.metadataEditPage.newFile = null;
            menuController.metadataEditPage.metadataEditItem = null;
            menuController.metadataEditPage.textBox.getChildren().clear();
            menuController.metadataEditPage.imageView.setImage(null);
            menuController.metadataEditPage.imageViewContainer.setStyle("-fx-background-color: transparent;");
            menuController.metadataEditPage.changesMade.set(false);

            menuController.technicalDetailsPage.textBox.getChildren().clear();
            menuController.technicalDetailsPage.imageView.setImage(null);
            menuController.technicalDetailsPage.imageViewContainer.setStyle("-fx-background-color: transparent;");
        });
        closeMenu.play();
    }

    public static FadeTransition fadeIn(Node child){
        Duration animationDuration = Duration.millis(ANIMATION_SPEED);
        FadeTransition fadeTransition = new FadeTransition(animationDuration, child);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        return fadeTransition;
    }

    public static FadeTransition fadeOut(Node child){
        Duration animationDuration = Duration.millis(ANIMATION_SPEED);
        FadeTransition fadeTransition = new FadeTransition(animationDuration, child);
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0);
        return fadeTransition;
    }

    public static TranslateTransition animateUp(Node child, double translate){
        Duration animationDuration = Duration.millis(ANIMATION_SPEED);
        TranslateTransition translateTransition = new TranslateTransition(animationDuration, child);
        translateTransition.setFromY(0);
        translateTransition.setToY(-translate);
        return translateTransition;
    }

    public static TranslateTransition animateDown(Node child, double translate){
        Duration animationDuration = Duration.millis(ANIMATION_SPEED);
        TranslateTransition translateTransition = new TranslateTransition(animationDuration, child);
        translateTransition.setFromY(0);
        translateTransition.setToY(translate);
        return translateTransition;
    }


    public static Timeline animateMinHeight(double newHeight, Region region){
        Duration animationDuration = Duration.millis(ANIMATION_SPEED);
        return new Timeline(new KeyFrame(animationDuration,
                new KeyValue(region.minHeightProperty(),newHeight, Interpolator.EASE_BOTH)));
    }

    public static Timeline animateMaxHeight(double newHeight, Region region){
        Duration animationDuration = Duration.millis(ANIMATION_SPEED);
        return new Timeline(new KeyFrame(animationDuration,
                new KeyValue(region.maxHeightProperty(),newHeight, Interpolator.EASE_BOTH)));
    }


    public static void AnimateBackgroundColor(Region icon, Color fromColor, Color toColor, int duration) {

        Rectangle rect = new Rectangle();
        rect.setFill(fromColor);

        FillTransition tr = new FillTransition();
        tr.setShape(rect);
        tr.setDuration(Duration.millis(duration));
        tr.setFromValue(fromColor);
        tr.setToValue(toColor);

        tr.setInterpolator(new Interpolator() {
            @Override
            protected double curve(double t) {
                icon.setBackground(new Background(new BackgroundFill(rect.getFill(), CornerRadii.EMPTY, Insets.EMPTY)));
                return t;
            }
        });

        tr.play();
    }


    public static void AnimateTextColor(Label label, Color toColor, int duration) {
        Duration animationDuration = Duration.millis(duration);
        Timeline timeline = new Timeline(new KeyFrame(animationDuration,
                new KeyValue(label.textFillProperty(), toColor, Interpolator.LINEAR)));

        timeline.play();

    }
}
