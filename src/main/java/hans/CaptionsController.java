package hans;


import hans.SRTParser.srt.SRTParser;
import hans.SRTParser.srt.Subtitle;
import javafx.animation.Animation;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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

    Color defaultBackground = Color.rgb(defaultBackgroundRed, defaultBackgroundGreen, defaultBackgroundBlue, defaultBackgroundOpacity);

    DoubleProperty mediaWidthMultiplier = new SimpleDoubleProperty(0.6);


    int currentFontSize = defaultFontSize;
    String currentFontFamily = defaultFontFamily;

    double currentTextOpacity = defaultTextOpacity;

    Color currentTextFill = defaultTextFill;
    int currentSpacing = defaultSpacing;
    int currentBackgroundRed = defaultBackgroundRed;
    int currentBackgroundGreen = defaultBackgroundGreen;
    int currentBackgroundBlue = defaultBackgroundBlue;
    double currentBackgroundOpacity = defaultBackgroundOpacity;

    Color currentBackground = defaultBackground;


    Pos captionsLocation = Pos.BOTTOM_CENTER;

    PauseTransition showCaptionsTimer;

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
        captionsBox.setAlignment(Pos.CENTER);
        captionsBox.setVisible(false);
        captionsBox.setPadding(new Insets(5, 10, 5, 10));
        captionsBox.setOpacity(defaultTextOpacity);

        StackPane.setAlignment(captionsBox, Pos.BOTTOM_CENTER);
        mainController.mediaViewInnerWrapper.getChildren().add(1, captionsBox);


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
                System.out.println(temp);
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

}
