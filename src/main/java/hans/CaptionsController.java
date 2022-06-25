package hans;


import hans.SRTParser.srt.SRTParser;
import hans.SRTParser.srt.Subtitle;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.io.File;
import java.util.ArrayList;

public class CaptionsController {

    SettingsController settingsController;
    MainController mainController;
    MediaInterface mediaInterface;
    ControlBarController controlBarController;
    MenuController menuController;


    CaptionsPane captionsPane;

    File captionsFile;

    ArrayList<Subtitle> subtitles  = new ArrayList<>();
    int captionsPosition = 0;

    boolean captionsSelected = false;
    BooleanProperty captionsOn = new SimpleBooleanProperty();
    boolean showedCurrentCaption = false;


    VBox captionsBox = new VBox();
    Label captionsLabel1 = new Label();
    Label captionsLabel2 = new Label();

    static int fontSize = 25;
    static String fontFamily = "\"Roboto Medium\"";
    static Color textFill = Color.WHITE;
    static int spacing = 10;
    static int red = 0;
    static int green = 0;
    static int blue = 0;
    static double backgroundOpacity = 0.75;

    static Color background = Color.rgb(red, green, blue, backgroundOpacity);


    Pos captionsLocation = Pos.BOTTOM_CENTER;

    CaptionsController(SettingsController settingsController, MainController mainController, MediaInterface mediaInterface, ControlBarController controlBarController, MenuController menuController){
        this.settingsController = settingsController;
        this.mainController = mainController;
        this.mediaInterface = mediaInterface;
        this.controlBarController = controlBarController;
        this.menuController = menuController;

        captionsPane = new CaptionsPane(this);

        captionsOn.set(false);


        captionsLabel1.setBackground(new Background(new BackgroundFill(background, CornerRadii.EMPTY, Insets.EMPTY)));
        captionsLabel1.setTextFill(textFill);
        captionsLabel1.setText("Test TEST Test");
        captionsLabel1.getStyleClass().add("captionsLabel");
        captionsLabel1.setStyle("-fx-font-family: " + fontFamily + "; -fx-font-size: " + fontSize);
        captionsLabel1.setOpacity(0);


        captionsLabel2.setBackground(new Background(new BackgroundFill(background, CornerRadii.EMPTY, Insets.EMPTY)));
        captionsLabel2.setTextFill(textFill);
        captionsLabel2.setText("Halloosss!");
        captionsLabel2.getStyleClass().add("captionsLabel");
        captionsLabel2.setStyle("-fx-font-family: " + fontFamily + "; -fx-font-size: " + fontSize);
        captionsLabel2.setOpacity(0);


        captionsBox.setSpacing(spacing);
        captionsBox.setTranslateY(-50);
        captionsBox.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        captionsBox.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        captionsBox.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        captionsBox.getChildren().addAll(captionsLabel1, captionsLabel2);
        captionsBox.setAlignment(Pos.CENTER);
        captionsBox.setVisible(false);

        StackPane.setAlignment(captionsBox, Pos.BOTTOM_CENTER);
        mainController.mediaViewInnerWrapper.getChildren().add(1, captionsBox);


        captionsOn.addListener((observableValue, aBoolean, t1) -> {
            if(t1){
                captionsBox.setVisible(true);
            }
            else {
                captionsBox.setVisible(false);
            }
        });

    }



    public void loadCaptions(File file){

        if(!captionsSelected){
            // enable captions button
            controlBarController.captionsIcon.getStyleClass().clear();
            controlBarController.captionsIcon.getStyleClass().add("controlIcon");
            if(!settingsController.settingsOpen) controlBarController.captions.updateText("Subtitles/closed captions (c)");

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
    }


    public void removeCaptions(){
        if(captionsSelected){
            this.captionsFile = null;
            captionsSelected = false;

            subtitles.clear();
            captionsPosition = 0;
            showedCurrentCaption = false;

            if(captionsOn.get()) controlBarController.closeCaptions();

            controlBarController.captionsIcon.getStyleClass().clear();
            controlBarController.captionsIcon.getStyleClass().add("controlIconDisabled");
            if(!settingsController.settingsOpen) controlBarController.captions.updateText("Subtitles/CC not selected");

            captionsPane.currentCaptionsTab.getChildren().remove(captionsPane.currentCaptionsNameLabel);
            captionsPane.currentCaptionsLabel.setText("No subtitles active");

            captionsPane.captionsToggle.setSelected(false);
            captionsPane.captionsToggle.setDisable(true);

        }
    }

}
