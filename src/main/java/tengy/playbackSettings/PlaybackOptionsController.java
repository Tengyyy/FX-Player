package tengy.playbackSettings;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import tengy.*;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

import static tengy.Utilities.keyboardFocusOff;
import static tengy.Utilities.keyboardFocusOn;

public class PlaybackOptionsController {

    PlaybackSettingsController playbackSettingsController;


    public boolean loopOn = false;
    public boolean autoplayOn = false;
    public boolean shuffleOn = false;

    public PlaybackOptionsTab loopTab;
    public PlaybackOptionsTab shuffleTab;
    public PlaybackOptionsTab autoplayTab;

    VBox playbackOptionsBox = new VBox();

    HBox titleBox = new HBox();
    Button backButton = new Button();
    Region backIcon = new Region();
    Label titleLabel = new Label();
    SVGPath backSVG = new SVGPath();

    List<Node> focusNodes = new ArrayList<>();
    IntegerProperty focus = new SimpleIntegerProperty(-1);


    PlaybackOptionsController(PlaybackSettingsController playbackSettingsController){
        this.playbackSettingsController = playbackSettingsController;

        backSVG.setContent(SVG.CHEVRON_LEFT.getContent());

        playbackOptionsBox.setPrefSize(235, 171);
        playbackOptionsBox.setMaxSize(235, 171);
        playbackOptionsBox.setPadding(new Insets(0, 0, 8, 0));
        playbackOptionsBox.getChildren().add(titleBox);
        playbackOptionsBox.setOnMouseClicked(e -> playbackOptionsBox.requestFocus());
        StackPane.setAlignment(playbackOptionsBox, Pos.BOTTOM_RIGHT);

        playbackOptionsBox.setVisible(false);
        playbackOptionsBox.setMouseTransparent(true);

        titleBox.setMinSize(235, 48);
        titleBox.setPrefSize(235, 48);
        titleBox.setMaxSize(235, 48);
        titleBox.getStyleClass().add("settingsPaneTitle");
        titleBox.setPadding(new Insets(0, 10, 0, 10));
        VBox.setMargin(titleBox, new Insets(0, 0, 10, 0));
        titleBox.getChildren().addAll(backButton, titleLabel);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        backButton.setMinSize(30, 40);
        backButton.setPrefSize(30, 40);
        backButton.setMaxSize(30, 40);
        backButton.setFocusTraversable(false);
        backButton.getStyleClass().addAll("transparentButton", "settingsMenuButton");
        backButton.setGraphic(backIcon);
        backButton.setOnAction((e) -> closePlaybackOptions());
        backButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue) focus.set(0);
            else {
                keyboardFocusOff(backButton);
                focus.set(-1);
            }
        });

        backButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            backButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        backButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            backButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });

        backIcon.setMinSize(8, 13);
        backIcon.setPrefSize(8, 13);
        backIcon.setMaxSize(8, 13);
        backIcon.getStyleClass().add("graphic");
        backIcon.setShape(backSVG);


        titleLabel.setMinHeight(35);
        titleLabel.setPrefHeight(35);
        titleLabel.setMaxHeight(35);
        titleLabel.setCursor(Cursor.HAND);
        titleLabel.setText("Playback options");
        titleLabel.getStyleClass().add("settingsPaneText");
        titleLabel.setAlignment(Pos.CENTER_LEFT);
        titleLabel.setOnMouseClicked((e) -> closePlaybackOptions());
        titleLabel.setPadding(new Insets(0, 0, 0, 4));

        shuffleTab = new PlaybackOptionsTab(this, "Shuffle", 1);
        loopTab = new PlaybackOptionsTab(this, "Loop video", 2);
        autoplayTab = new PlaybackOptionsTab(this, "Autoplay", 3);

        focusNodes.add(backButton);
        focusNodes.add(shuffleTab);
        focusNodes.add(loopTab);
        focusNodes.add(autoplayTab);

        playbackSettingsController.playbackSettingsPane.getChildren().add(playbackOptionsBox);


        shuffleTab.toggle.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // OFF
                shuffleOn = false;

                if(!autoplayTab.toggle.isSelected() && !loopTab.toggle.isSelected()) {
                    playbackSettingsController.playbackSettingsHomeController.playbackOptionsTab.mainIcon.setShape(playbackSettingsController.playbackSettingsHomeController.tuneSVG);
                }
                else if(autoplayTab.toggle.isSelected()) playbackSettingsController.playbackSettingsHomeController.playbackOptionsTab.mainIcon.setShape(playbackSettingsController.playbackSettingsHomeController.repeatSVG);

                playbackSettingsController.menuController.queuePage.shuffleTooltip.updateActionText("Shuffle is off");

                if(!playbackSettingsController.menuController.queuePage.queueBox.queue.isEmpty()) playbackSettingsController.menuController.queuePage.queueBox.shuffleOff();

                playbackSettingsController.menuController.queuePage.shuffleToggle.getStyleClass().remove("toggleActive");

            } else { // ON
                shuffleOn = true;


                if(!loopTab.toggle.isSelected()) {
                    playbackSettingsController.playbackSettingsHomeController.playbackOptionsTab.mainIcon.setShape(playbackSettingsController.playbackSettingsHomeController.shuffleSVG);
                }

                playbackSettingsController.menuController.queuePage.shuffleTooltip.updateActionText("Shuffle is on");


                if(!playbackSettingsController.menuController.queuePage.queueBox.queue.isEmpty()) playbackSettingsController.menuController.queuePage.queueBox.shuffleOn();

                if(!playbackSettingsController.menuController.queuePage.shuffleToggle.getStyleClass().contains("toggleActive")) playbackSettingsController.menuController.queuePage.shuffleToggle.getStyleClass().add("toggleActive");
            }

        });

        loopTab.toggle.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // OFF
                loopOn = false;
                playbackSettingsController.mediaInterface.embeddedMediaPlayer.controls().setRepeat(false);


                if(shuffleTab.toggle.isSelected()) playbackSettingsController.playbackSettingsHomeController.playbackOptionsTab.mainIcon.setShape(playbackSettingsController.playbackSettingsHomeController.shuffleSVG);
                else if(autoplayTab.toggle.isSelected()) playbackSettingsController.playbackSettingsHomeController.playbackOptionsTab.mainIcon.setShape(playbackSettingsController.playbackSettingsHomeController.repeatSVG);
                else playbackSettingsController.playbackSettingsHomeController.playbackOptionsTab.mainIcon.setShape(playbackSettingsController.playbackSettingsHomeController.tuneSVG);

            } else { // ON
                loopOn = true;
                playbackSettingsController.mediaInterface.embeddedMediaPlayer.controls().setRepeat(true);


                playbackSettingsController.playbackSettingsHomeController.playbackOptionsTab.mainIcon.setShape(playbackSettingsController.playbackSettingsHomeController.repeatOnceSVG);
            }
        });

        autoplayTab.toggle.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // OFF

                autoplayOn = false;



                if(!shuffleTab.toggle.isSelected() && !loopTab.toggle.isSelected()) {
                    playbackSettingsController.playbackSettingsHomeController.playbackOptionsTab.mainIcon.setShape(playbackSettingsController.playbackSettingsHomeController.tuneSVG);
                }
            } else { // ON

                autoplayOn = true;



                if(!shuffleTab.toggle.isSelected() && !loopTab.toggle.isSelected()) {
                    playbackSettingsController.playbackSettingsHomeController.playbackOptionsTab.mainIcon.setShape(playbackSettingsController.playbackSettingsHomeController.repeatSVG);
                }
            }
        });

        autoplayTab.toggle.setSelected(true);

    }


    public void closePlaybackOptions(){
        if(playbackSettingsController.animating.get()) return;

        playbackSettingsController.playbackSettingsState = PlaybackSettingsState.HOME_OPEN;

        playbackSettingsController.playbackSettingsHomeController.playbackSettingsHomeScroll.setVisible(true);
        playbackSettingsController.playbackSettingsHomeController.playbackSettingsHomeScroll.setMouseTransparent(false);

        Timeline clipTimeline = new Timeline();
        clipTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(playbackSettingsController.clip.heightProperty(), playbackSettingsController.playbackSettingsHomeController.playbackSettingsHomeScroll.getHeight())));

        TranslateTransition homeTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), playbackSettingsController.playbackSettingsHomeController.playbackSettingsHomeScroll);
        homeTransition.setFromX(-playbackSettingsController.playbackSettingsHomeController.playbackSettingsHomeScroll.getWidth());
        homeTransition.setToX(0);

        TranslateTransition optionsTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), playbackSettingsController.playbackOptionsController.playbackOptionsBox);
        optionsTransition.setFromX(0);
        optionsTransition.setToX(playbackSettingsController.playbackSettingsHomeController.playbackSettingsHomeScroll.getWidth());


        ParallelTransition parallelTransition = new ParallelTransition(clipTimeline, homeTransition, optionsTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            playbackSettingsController.animating.set(false);
            playbackOptionsBox.setVisible(false);
            playbackOptionsBox.setMouseTransparent(true);
        });

        parallelTransition.play();
        playbackSettingsController.animating.set(true);

    }

    public void focusForward(){
        int newFocus;

        if(focus.get() >= 3 || focus.get() == -1) newFocus = 0;
        else newFocus = focus.get() + 1;

        keyboardFocusOn(focusNodes.get(newFocus));
    }

    public void focusBackward() {
        int newFocus;

        if (focus.get() == 0) newFocus = 3;
        else if (focus.get() == -1) newFocus = 0;
        else newFocus = focus.get() - 1;

        keyboardFocusOn(focusNodes.get(newFocus));
    }
}
