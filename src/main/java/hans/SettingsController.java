package hans;

import java.io.File;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Duration;

import io.github.palexdev.materialfx.controls.MFXToggleButton;

public class SettingsController implements Initializable {


    @FXML
    VBox settingsHome, playbackSpeedPage, customSpeedBox, playbackOptionsVBox;

    @FXML
    StackPane bufferPane, customSpeedBuffer, customSpeedPane, playbackOptionsBuffer, playbackOptionsPane, settingsPane, playbackOptionsArrowPane, videoArrowPane, playbackValueArrowPane, playbackSpeedBackPane, checkBox1Pane, checkBox2Pane, checkBox3Pane, checkBox4Pane, checkBox5Pane, checkBox6Pane, checkBox7Pane, checkBox8Pane, customSpeedBackPane, playbackOptionsBackPane;

    @FXML
    Region playbackOptionsArrowIcon, videoArrowIcon, playbackValueArrowIcon, playbackSpeedBackIcon, checkBox1Icon, checkBox2Icon, checkBox3Icon, checkBox4Icon, checkBox5Icon, checkBox6Icon, checkBox7Icon, checkBox8Icon, customSpeedBackIcon, playbackOptionsBackIcon;

    @FXML
    Pane settingsBackgroundPane;

    @FXML
    Slider customSpeedSlider;

    @FXML
    ProgressBar customSpeedTrack;

    @FXML
    Label playbackValueLabel, playbackSpeedTitleLabel, playbackSpeedCustom, customSpeedTitleLabel, customSpeedLabel, playbackOptionsTitleText, shuffleLabel, loopLabel, autoplayLabel;


    @FXML
    ScrollPane playbackSpeedScroll;

    @FXML
    MFXToggleButton shuffleSwitch, loopSwitch, autoplaySwitch;


    @FXML
    HBox playbackSpeedBox, playbackOptionsBox, videoBox, playbackSpeedTitle, playbackSpeed1, playbackSpeed2, playbackSpeed3, playbackSpeed4, playbackSpeed5, playbackSpeed6, playbackSpeed7, playbackSpeed8, customSpeedTitle, shuffleBox, loopBox, autoplayBox, playbackOptionsTitle;



    MainController mainController;
    ControlBarController controlBarController;
    MenuController menuController;


    CustomSpeedTab playbackCustom;


    // variables to keep track of playback option toggles:
    boolean shuffleOn = false;
    boolean loopOn = false;
    boolean autoplayOn = false;
    /////////////////////////////////////////////////////

    double formattedValue;
    double formattedValue2;

    DecimalFormat df = new DecimalFormat("#.##"); // makes it so that only the minimal amount of digits wil be displayed, e.g. 2 not 2.00

    FileChooser fileChooser;


    // counter to keep track of which playback speed field is selected in the settings menu
    int playbackSpeedTracker = 4; // 0 == custom speed tab, 1-8 default selections


    public boolean settingsOpen = false;
    public boolean settingsHomeOpen = false;
    boolean playbackSpeedOpen = false;
    boolean playbackOptionsOpen = false;
    boolean customSpeedOpen = false;

    boolean loadCustomSpeed = false; // if true the custom speed value pane needs to be rendered when entering the speed selection menu
    boolean isDefaultValue = false;


    HBox[] playbackSpeedBoxesArray; // array containing playback speed selection tabs

    MediaInterface mediaInterface;

    Timeline marqueeTimeline;

    BooleanProperty titleHover = new SimpleBooleanProperty(false);

    PauseTransition countdown;

    SVGPath leftArrowSVG = new SVGPath();
    SVGPath rightArrowSVG = new SVGPath();
    SVGPath checkSVG = new SVGPath();


    @Override
    public void initialize(URL location, ResourceBundle resources) {


        fileChooser = new FileChooser();
        fileChooser.setTitle("Open video");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Videos", "*.mp4"));
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Audio", "*.mp3"));

        playbackSpeedBoxesArray = new HBox[]{playbackSpeed1, playbackSpeed2, playbackSpeed3, playbackSpeed4, playbackSpeed5, playbackSpeed6, playbackSpeed7, playbackSpeed8};

        bufferPane.setBackground(Background.EMPTY);
        settingsPane.setBackground(Background.EMPTY);

        settingsPane.setStyle("-fx-background-color: rgba(35,35,35,0.8)");

        playbackSpeedScroll.setBackground(Background.EMPTY);

        playbackSpeedScroll.setStyle("-fx-background-color: rgba(35,35,35,0.8)");

        customSpeedPane.setStyle("-fx-background-color: rgba(35,35,35,0.8)");

        playbackOptionsPane.setStyle("-fx-background-color: rgba(35,35,35,0.8)");

        leftArrowSVG.setContent(App.svgMap.get(SVG.CHEVRON_LEFT));
        rightArrowSVG.setContent(App.svgMap.get(SVG.CHEVRON_RIGHT));
        checkSVG.setContent(App.svgMap.get(SVG.CHECK));


        playbackOptionsArrowIcon.setShape(rightArrowSVG);
        videoArrowIcon.setShape(rightArrowSVG);
        playbackValueArrowIcon.setShape(rightArrowSVG);
        playbackSpeedBackIcon.setShape(leftArrowSVG);
        checkBox1Icon.setShape(checkSVG);
        checkBox2Icon.setShape(checkSVG);
        checkBox3Icon.setShape(checkSVG);
        checkBox4Icon.setShape(checkSVG);
        checkBox5Icon.setShape(checkSVG);
        checkBox6Icon.setShape(checkSVG);
        checkBox7Icon.setShape(checkSVG);
        checkBox8Icon.setShape(checkSVG);
        customSpeedBackIcon.setShape(leftArrowSVG);
        playbackOptionsBackIcon.setShape(leftArrowSVG);


        playbackOptionsBox.setOnMouseClicked((e) -> {
            openPlaybackOptions();
        });

        videoBox.setOnMouseClicked((e) -> {
            openVideoChooser();
        });

        shuffleBox.setOnMouseClicked((e) -> {
            shuffleSwitch.fire();

            if (loopSwitch.isSelected()) { // turns other switches off if this one is toggled on. makes it so only one
                // switch can be selected
                loopSwitch.fire();
            }

            if (autoplaySwitch.isSelected()) {
                autoplaySwitch.fire();
            }

        });
        loopBox.setOnMouseClicked((e) -> {
            loopSwitch.fire();

            if (shuffleSwitch.isSelected()) {
                shuffleSwitch.fire();
            }

            if (autoplaySwitch.isSelected()) {
                autoplaySwitch.fire();
            }
        });
        autoplayBox.setOnMouseClicked((e) -> {
            autoplaySwitch.fire();

            if (loopSwitch.isSelected()) {
                loopSwitch.fire();
            }

            if (shuffleSwitch.isSelected()) {
                shuffleSwitch.fire();
            }
        });

        shuffleSwitch.setOnMouseClicked((e) -> { // in addition to the hbox, also add same logic to the switch itself
            // (minus the .fire() part cause in that case the switch would
            // toggle twice in a row)

            if (loopSwitch.isSelected()) {
                loopSwitch.fire();
            }

            if (autoplaySwitch.isSelected()) {
                autoplaySwitch.fire();
            }
        });

        loopSwitch.setOnMouseClicked((e) -> {

            if (shuffleSwitch.isSelected()) {
                shuffleSwitch.fire();
            }

            if (autoplaySwitch.isSelected()) {
                autoplaySwitch.fire();
            }
        });

        autoplaySwitch.setOnMouseClicked((e) -> {

            if (loopSwitch.isSelected()) {
                loopSwitch.fire();
            }

            if (shuffleSwitch.isSelected()) {
                shuffleSwitch.fire();
            }
        });

        shuffleSwitch.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) { // ON
                shuffleOn = true;

                if(!menuController.animationsInProgress.isEmpty()) return;

                if(!menuController.queue.isEmpty()) menuController.queueBox.shuffle();

            } else { // OFF
                shuffleOn = false;
            }
        });

        loopSwitch.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) { // ON
                loopOn = true;
            } else { // OFF
                loopOn = false;
            }
        });

        autoplaySwitch.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) { // ON
                autoplayOn = true;
            } else { // OFF
                autoplayOn = false;
            }
        });

        customSpeedTrack.setProgress(0.75 / 1.75);


        customSpeedSlider.setOnMousePressed((e) -> {
            customSpeedSlider.setValueChanging(true);
        });
        customSpeedSlider.setOnMouseReleased((e) -> {
            customSpeedSlider.setValueChanging(false);
        });

        customSpeedSlider.valueChangingProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                if(menuController.activeItem != null) mediaInterface.mediaPlayer.setRate(formattedValue);
            }
        });

        customSpeedSlider.valueProperty().addListener((observable, oldValue, newValue) -> {


            formattedValue = Math.floor(newValue.doubleValue() * 20) / 20; // floors it to .05 precision

            double progress = (newValue.doubleValue() - 0.25) * 1 / 1.75; // adjust the slider scale ( 0.25 - 2 ) to
            // match with the progress bar scale ( 0 - 1 )

            customSpeedTrack.setProgress(progress);

            customSpeedLabel.setText(df.format(formattedValue) + "x");

            isDefaultValue = true;

            if (formattedValue * 4 != Math.round(formattedValue * 4)) { // filters out all default playback speed values that could be selected from the selection pane (0.25, 0.75, 1.5 etc), so that this int could only be an actual customly selected value
                formattedValue2 = formattedValue;


                loadCustomSpeed = true; // whether the custom speed has to be created/updated or not
                isDefaultValue = false; // when the user finishes seeking the custom speed slider and isDefaultValue stays true then it means that the slider landed on one of the default values (1,25; 1,5 etc) and therefore the according playback speed tab must be made active
            }

        });


        settingsBackgroundPane.setPickOnBounds(false);

        bufferPane.prefWidthProperty().bind(settingsBackgroundPane.widthProperty());
        bufferPane.prefHeightProperty().bind(settingsBackgroundPane.heightProperty());


        customSpeedBuffer.prefHeightProperty().bind(settingsBackgroundPane.heightProperty());
        customSpeedBuffer.prefWidthProperty().bind(settingsBackgroundPane.widthProperty());


        playbackSpeedScroll.translateYProperty()
                .bind(Bindings.subtract(settingsBackgroundPane.heightProperty(), playbackSpeedScroll.heightProperty()));

        Platform.runLater(() -> {

            playbackSpeedScroll.maxHeightProperty().bind(Bindings.subtract(mainController.mediaViewHeight, 100));

            //this can surely be improved
            playbackOptionsBuffer.setTranslateX(settingsBackgroundPane.getWidth());
            playbackSpeedScroll.setTranslateX(settingsBackgroundPane.getWidth());
            customSpeedBuffer.setTranslateX(settingsBackgroundPane.getWidth());

        });
        playbackOptionsBuffer.translateYProperty().bind(
                Bindings.subtract(settingsBackgroundPane.heightProperty(), playbackOptionsBuffer.heightProperty()));


        bufferPane.setTranslateY(170);

        // Clipping for the settings pane
        Rectangle rectangle = new Rectangle(settingsBackgroundPane.getWidth(), settingsBackgroundPane.getHeight());
        rectangle.widthProperty().bind(settingsBackgroundPane.widthProperty());
        rectangle.heightProperty().bind(settingsBackgroundPane.heightProperty());
        settingsBackgroundPane.setClip(rectangle);

        /////////////////////////////////
        // mouse listeners for playback speed

        for (int i = 0; i < playbackSpeedBoxesArray.length; i++) {

            final double I = i + 1;

            if(i != 3) {
                StackPane pane1 = (StackPane) playbackSpeedBoxesArray[(int) (I - 1)].getChildren().get(0);
                Region region1 = (Region) pane1.getChildren().get(0);
                region1.setVisible(false);
            }

            playbackSpeedBoxesArray[i].setOnMouseClicked((e) -> {

                StackPane pane = (StackPane) playbackSpeedBoxesArray[(int) (I - 1)].getChildren().get(0);
                Region region = (Region) pane.getChildren().get(0);

                updatePlaybackSpeed((int) I, I / 4, region, false);

                if(menuController.activeItem != null) mediaInterface.mediaPlayer.setRate(I / 4);
            });
        }

        countdown = new PauseTransition(Duration.millis(1000));
        countdown.setOnFinished((e) -> {
            titleHover.set(true);
        });

        Rectangle videoNameClip = new Rectangle(195, 50);

        videoBox.setOnMouseEntered((e) -> {

            if(marqueeTimeline != null && marqueeTimeline.getStatus() == Animation.Status.RUNNING) titleHover.set(true);
            else countdown.playFromStart();
        });

        videoBox.setOnMouseExited((e) -> {
            titleHover.set(false);
            if(countdown.getStatus() == Animation.Status.RUNNING) {
                countdown.stop();
            }
        });

    }

    public void init(MainController mainController, ControlBarController controlBarController, MenuController menuController, MediaInterface mediaInterface) {
        this.mainController = mainController;
        this.controlBarController = controlBarController;
        this.menuController = menuController;
        this.mediaInterface = mediaInterface;
    }


    public void openSettings() {

        AnimationsClass.rotateTransition(100, controlBarController.settingsIcon, 0, 45, false, 1, true);


        settingsOpen = true;
        settingsHomeOpen = true;

        if (mainController.captionsOpen) {
            controlBarController.closeCaptions();
        }

        AnimationsClass.openSettings(bufferPane);

        if (controlBarController.captions.isShowing() || controlBarController.settings.isShowing() || controlBarController.fullScreen.isShowing() || controlBarController.exitFullScreen.isShowing()) {
            controlBarController.captions.hide();
            controlBarController.settings.hide();
            controlBarController.fullScreen.hide();
            controlBarController.exitFullScreen.hide();
        }
        controlBarController.captionsButton.setOnMouseEntered(null);
        controlBarController.settingsButton.setOnMouseEntered(null);
        controlBarController.fullScreenButton.setOnMouseEntered(null);

    }

    public void closeSettings() {

        AnimationsClass.rotateTransition(100, controlBarController.settingsIcon, 45, 0, false, 1, true);

        settingsOpen = false;

        if (loadCustomSpeed) createCustomSpeedTab();

        // this part will be run if the custom speed tab doesnt need to be updated but the rest of the tabs do
        if (isDefaultValue){

            StackPane pane = (StackPane) playbackSpeedBoxesArray[(int) (formattedValue * 4 -1)].getChildren().get(0);
            Region region = (Region) pane.getChildren().get(0);

            updatePlaybackSpeed((int) formattedValue * 4, formattedValue, region, true);
        }


        if (controlBarController.settingsButtonHover) {
            controlBarController.settings = new ControlTooltip("Settings (s)", controlBarController.settingsButton, controlBarController.controlBar, 0, false);
            controlBarController.settings.showTooltip();

            controlBarController.captions = new ControlTooltip("Subtitles/closed captions (c)", controlBarController.captionsButton, controlBarController.controlBar, 0, false);

            if (App.fullScreen)
                controlBarController.exitFullScreen = new ControlTooltip("Exit full screen (f)", controlBarController.fullScreenButton, controlBarController.controlBar, 0, false);
            else
                controlBarController.fullScreen = new ControlTooltip("Full screen (f)", controlBarController.fullScreenButton, controlBarController.controlBar, 0, false);
        } else if (controlBarController.captionsButtonHover) {
            controlBarController.captions = new ControlTooltip("Subtitles/closed captions (c)", controlBarController.captionsButton, controlBarController.controlBar, 0, false);
            controlBarController.captions.showTooltip();

            controlBarController.settings = new ControlTooltip("Settings (s)", controlBarController.settingsButton, controlBarController.controlBar, 0, false);

            if (App.fullScreen)
                controlBarController.exitFullScreen = new ControlTooltip("Exit full screen (f)", controlBarController.fullScreenButton, controlBarController.controlBar, 0, false);
            else
                controlBarController.fullScreen = new ControlTooltip("Full screen (f)", controlBarController.fullScreenButton, controlBarController.controlBar, 0, false);
        } else if (controlBarController.fullScreenButtonHover) {
            if (App.fullScreen) {
                controlBarController.exitFullScreen = new ControlTooltip("Exit full screen (f)", controlBarController.fullScreenButton, controlBarController.controlBar, 0, false);
                controlBarController.exitFullScreen.showTooltip();
            } else {
                controlBarController.fullScreen = new ControlTooltip("Full screen (f)", controlBarController.fullScreenButton, controlBarController.controlBar, 0, false);
                controlBarController.fullScreen.showTooltip();
            }

            controlBarController.captions = new ControlTooltip("Subtitles/closed captions (c)", controlBarController.captionsButton, controlBarController.controlBar, 0, false);

            controlBarController.settings = new ControlTooltip("Settings (s)", controlBarController.settingsButton, controlBarController.controlBar, 0, false);
        } else {
            controlBarController.captions = new ControlTooltip("Subtitles/closed captions (c)", controlBarController.captionsButton, controlBarController.controlBar, 0, false);

            controlBarController.settings = new ControlTooltip("Settings (s)", controlBarController.settingsButton, controlBarController.controlBar, 0, false);

            if (App.fullScreen)
                controlBarController.exitFullScreen = new ControlTooltip("Exit full screen (f)", controlBarController.fullScreenButton, controlBarController.controlBar, 0, false);
            else
                controlBarController.fullScreen = new ControlTooltip("Full screen (f)", controlBarController.fullScreenButton, controlBarController.controlBar, 0, false);
        }


        if (settingsHomeOpen) {
            settingsHomeOpen = false;
            AnimationsClass.closeSettings(bufferPane);
        } else if (playbackOptionsOpen) {
            playbackOptionsOpen = false;
            AnimationsClass.closeSettingsFromPlaybackOptions(settingsBackgroundPane, playbackOptionsBuffer, bufferPane);
        } else if (playbackSpeedOpen) {
            playbackSpeedOpen = false;
            AnimationsClass.closeSettingsFromPlaybackSpeed(settingsBackgroundPane, playbackSpeedScroll, bufferPane);
        } else if (customSpeedOpen) {
            customSpeedOpen = false;
            AnimationsClass.closeSettingsFromCustomSpeed(settingsBackgroundPane, playbackSpeedScroll, customSpeedBuffer, bufferPane);

            // render custom speed tab if necessary
        }
    }


    public void openPlaybackSpeedPage() {

        playbackSpeedOpen = true;
        settingsHomeOpen = false;

        double toHeight;
        if (playbackCustom != null)
            toHeight = mainController.mediaView.sceneProperty().get().getHeight() < 637 ? mainController.mediaView.sceneProperty().get().getHeight() - 100 : 537;
        else
            toHeight = mainController.mediaView.sceneProperty().get().getHeight() < 587 ? mainController.mediaView.sceneProperty().get().getHeight() - 100 : 487;

        AnimationsClass.openPlaybackSpeed(bufferPane, settingsBackgroundPane, playbackSpeedScroll, toHeight);

    }

    public void closePlaybackSpeedPage() {

        playbackSpeedOpen = false;
        settingsHomeOpen = true;

        AnimationsClass.closePlaybackSpeed(settingsBackgroundPane, playbackSpeedScroll, bufferPane);
    }

    public void openCustomSpeed() {
        customSpeedOpen = true;
        playbackSpeedOpen = false;

        AnimationsClass.openCustomSpeed(settingsBackgroundPane, customSpeedBuffer, playbackSpeedScroll);
    }

    public void closeCustomSpeed() {
        customSpeedOpen = false;
        playbackSpeedOpen = true;

        if (loadCustomSpeed) createCustomSpeedTab();

        if (isDefaultValue) {
            StackPane pane = (StackPane) playbackSpeedBoxesArray[(int) (formattedValue * 4 -1)].getChildren().get(0);
            Region region = (Region) pane.getChildren().get(0);

            updatePlaybackSpeed((int) formattedValue * 4, formattedValue, region, true);
        }

        double toHeight;
        if (playbackCustom != null)
            toHeight = mainController.mediaView.sceneProperty().get().getHeight() < 637 ? mainController.mediaView.sceneProperty().get().getHeight() - 100 : 537;
        else
            toHeight = mainController.mediaView.sceneProperty().get().getHeight() < 587 ? mainController.mediaView.sceneProperty().get().getHeight() - 100 : 487;

        AnimationsClass.closeCustomSpeed(customSpeedBuffer, settingsBackgroundPane, playbackSpeedScroll, toHeight);


    }

    public void openPlaybackOptions() {

        playbackOptionsOpen = true;
        settingsHomeOpen = false;

        AnimationsClass.openPlaybackOptions(settingsBackgroundPane, playbackOptionsBuffer, bufferPane);

    }

    public void closePlaybackOptions() {

        playbackOptionsOpen = false;
        settingsHomeOpen = true;

        AnimationsClass.closePlaybackOptions(playbackOptionsBuffer, settingsBackgroundPane, bufferPane);
    }

    public void openVideoChooser() {
        File selectedFile = fileChooser.showOpenDialog(App.stage);

        if (selectedFile != null) {

            MediaItem temp = null;

            if(Utilities.getFileExtension(selectedFile).equals("mp4")) temp = new Mp4Item(selectedFile);
            else if(Utilities.getFileExtension(selectedFile).equals("mp3")) temp = new Mp3Item(selectedFile);


            ActiveItem activeItem = new ActiveItem(temp, menuController, mediaInterface, menuController.activeBox);
            activeItem.play(true);

        }
    }

    public void createCustomSpeedTab() {
        loadCustomSpeed = false;

        if (playbackCustom != null) playbackSpeedPage.getChildren().remove(2);

        playbackCustom = new CustomSpeedTab(this, formattedValue == formattedValue2 ? true : false);
    }

    public void updatePlaybackSpeed(int trackerValue, double speedValue, Region activeCheckBox, boolean updateScroll) {

        isDefaultValue = false;

        playbackSpeedTracker = trackerValue;

        int scrollValue = 0;
        if (trackerValue > 1) {
            scrollValue = 50 + 60 * trackerValue;
        }

        if (playbackCustom != null) {
            if (trackerValue == 1) scrollValue = 150;
            else scrollValue += 60;

            playbackCustom.playbackCustomCheckIcon.setVisible(false);
        }

        if(updateScroll) playbackSpeedScroll.setVvalue(scrollValue / playbackSpeedPage.getHeight());

        for (HBox hbox : playbackSpeedBoxesArray) {
            StackPane pane = (StackPane) hbox.getChildren().get(0);
            Region region = (Region) pane.getChildren().get(0);

            region.setVisible(false);
        }

        activeCheckBox.setVisible(true);

        if (speedValue == 1) playbackValueLabel.setText("Normal");
        else playbackValueLabel.setText(df.format(speedValue));

        // update scroll pane scroll value inside this method
    }
}
