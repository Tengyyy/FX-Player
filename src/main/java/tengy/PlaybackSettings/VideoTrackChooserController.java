package tengy.PlaybackSettings;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import tengy.SVG;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;
import tengy.Utilities;
import uk.co.caprica.vlcj.player.base.TrackDescription;

import java.util.ArrayList;
import java.util.List;

import static tengy.Utilities.keyboardFocusOff;
import static tengy.Utilities.keyboardFocusOn;

public class VideoTrackChooserController {

    PlaybackSettingsController playbackSettingsController;

    ScrollPane scrollPane = new ScrollPane();

    public VBox videoTrackChooserBox = new VBox();
    HBox titleBox = new HBox();

    Button backButton = new Button();
    Region backIcon = new Region();
    SVGPath backSVG = new SVGPath();

    Label titleLabel = new Label();

    final int DEFAULT_HEIGHT = 136;

    public VideoTrackTab selectedTab = null;

    List<Node> focusNodes = new ArrayList<>();
    IntegerProperty focus = new SimpleIntegerProperty(-1);

    VideoTrackChooserController(PlaybackSettingsController playbackSettingsController){
        this.playbackSettingsController = playbackSettingsController;


        backSVG.setContent(SVG.CHEVRON_LEFT.getContent());

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("settingsScroll");
        scrollPane.setPrefSize(235, DEFAULT_HEIGHT + 3);
        scrollPane.setMaxSize(235, DEFAULT_HEIGHT + 3);
        scrollPane.setContent(videoTrackChooserBox);
        scrollPane.setVisible(false);
        scrollPane.setMouseTransparent(true);
        scrollPane.setFitToWidth(true);


        StackPane.setAlignment(scrollPane, Pos.BOTTOM_RIGHT);
        videoTrackChooserBox.setAlignment(Pos.TOP_LEFT);
        videoTrackChooserBox.setPrefSize(235, DEFAULT_HEIGHT);
        videoTrackChooserBox.setMaxSize(235, DEFAULT_HEIGHT);
        videoTrackChooserBox.setPadding(new Insets(0, 0, 8, 0));
        videoTrackChooserBox.getChildren().add(titleBox);
        videoTrackChooserBox.setFillWidth(true);


        titleBox.setPrefSize(235, 48);
        titleBox.setMaxSize(235, 48);
        titleBox.setPadding(new Insets(0, 5, 0, 10));
        VBox.setMargin(titleBox, new Insets(0, 0, 10, 0));
        titleBox.setAlignment(Pos.CENTER_LEFT);
        titleBox.getStyleClass().add("settingsPaneTitle");
        titleBox.getChildren().addAll(backButton, titleLabel);

        backButton.setMinSize(30, 40);
        backButton.setPrefSize(30, 40);
        backButton.setMaxSize(30, 40);
        backButton.setFocusTraversable(false);
        backButton.getStyleClass().addAll("transparentButton", "settingsMenuButton");
        backButton.setGraphic(backIcon);
        backButton.setOnAction((e) -> closeVideoTrackChooser());
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

        focusNodes.add(backButton);

        backIcon.setMinSize(8, 13);
        backIcon.setPrefSize(8, 13);
        backIcon.setMaxSize(8, 13);
        backIcon.getStyleClass().add("graphic");
        backIcon.setShape(backSVG);

        titleLabel.setText("Video tracks");
        titleLabel.setCursor(Cursor.HAND);
        titleLabel.getStyleClass().add("settingsPaneText");
        titleLabel.setOnMouseClicked((e) -> closeVideoTrackChooser());
        titleLabel.setPadding(new Insets(0, 0, 0, 4));


        playbackSettingsController.playbackSettingsPane.getChildren().add(scrollPane);
    }

    public void closeVideoTrackChooser(){
        if(playbackSettingsController.animating.get()) return;

        playbackSettingsController.playbackSettingsState = PlaybackSettingsState.HOME_OPEN;

        playbackSettingsController.playbackSettingsHomeController.playbackSettingsHomeScroll.setVisible(true);
        playbackSettingsController.playbackSettingsHomeController.playbackSettingsHomeScroll.setMouseTransparent(false);

        Timeline clipTimeline = new Timeline();
        clipTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(playbackSettingsController.clip.heightProperty(), playbackSettingsController.playbackSettingsHomeController.playbackSettingsHomeScroll.getHeight())));

        TranslateTransition homeTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), playbackSettingsController.playbackSettingsHomeController.playbackSettingsHomeScroll);
        homeTransition.setFromX(-playbackSettingsController.playbackSettingsHomeController.playbackSettingsHomeScroll.getWidth());
        homeTransition.setToX(0);

        TranslateTransition trackTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), scrollPane);
        trackTransition.setFromX(0);
        trackTransition.setToX(scrollPane.getWidth());


        ParallelTransition parallelTransition = new ParallelTransition(clipTimeline, homeTransition, trackTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            playbackSettingsController.animating.set(false);
            scrollPane.setVisible(false);
            scrollPane.setMouseTransparent(true);
            scrollPane.setTranslateX(0);
            playbackSettingsController.clip.setHeight(playbackSettingsController.playbackSettingsHomeController.playbackSettingsHomeScroll.getPrefHeight());

        });

        parallelTransition.play();
        playbackSettingsController.animating.set(true);

    }


    public void initializeTracks(List<TrackDescription> trackDescriptions, int defaultTrack){
        for(int i=0; i<trackDescriptions.size(); i++){
            TrackDescription trackDescription = trackDescriptions.get(i);
            if(trackDescription.id() == defaultTrack){
                this.selectedTab = new VideoTrackTab(this, trackDescription, true, i+1);
            }
            else new VideoTrackTab(this, trackDescription, false, i+1);
        }


        scrollPane.setPrefHeight(Math.max(DEFAULT_HEIGHT + 3, 69 + trackDescriptions.size() * 35));
        scrollPane.setMaxHeight(Math.max(DEFAULT_HEIGHT + 3, 69 + trackDescriptions.size() * 35));

        videoTrackChooserBox.setPrefHeight(Math.max(DEFAULT_HEIGHT, 66 + trackDescriptions.size() * 35));
        videoTrackChooserBox.setMaxHeight(Math.max(DEFAULT_HEIGHT, 66 + trackDescriptions.size() * 35));


        if(playbackSettingsController.playbackSettingsState == PlaybackSettingsState.VIDEO_TRACK_CHOOSER_OPEN){
            playbackSettingsController.clip.setHeight(Math.max(DEFAULT_HEIGHT + 3, 69 + trackDescriptions.size() * 35));
        }
    }

    public void clearTracks(){

        selectedTab = null;

        videoTrackChooserBox.getChildren().clear();
        videoTrackChooserBox.getChildren().add(titleBox);

        focusNodes.clear();
        focusNodes.add(backButton);

        scrollPane.setPrefHeight(DEFAULT_HEIGHT + 3);
        scrollPane.setMaxHeight(DEFAULT_HEIGHT + 3);

        videoTrackChooserBox.setPrefHeight(DEFAULT_HEIGHT);
        videoTrackChooserBox.setMaxHeight(DEFAULT_HEIGHT);

        if(playbackSettingsController.playbackSettingsState == PlaybackSettingsState.VIDEO_TRACK_CHOOSER_OPEN)
            playbackSettingsController.clip.setHeight(DEFAULT_HEIGHT + 3);
    }

    public void focusForward(){
        int newFocus;

        if(focus.get() >= focusNodes.size() - 1 || focus.get() == -1) newFocus = 0;
        else newFocus = focus.get() + 1;

        keyboardFocusOn(focusNodes.get(newFocus));

        if(newFocus == 0) scrollPane.setVvalue(0);
        else Utilities.setScroll(scrollPane, focusNodes.get(newFocus));
    }

    public void focusBackward() {
        int newFocus;

        if (focus.get() == 0) newFocus = focusNodes.size() - 1;
        else if (focus.get() == -1) newFocus = 0;
        else newFocus = focus.get() - 1;

        keyboardFocusOn(focusNodes.get(newFocus));

        if(newFocus == 0) scrollPane.setVvalue(0);
        else Utilities.setScroll(scrollPane, focusNodes.get(newFocus));
    }
}
