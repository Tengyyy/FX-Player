package hans.PlaybackSettings;

import hans.SVG;
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
import uk.co.caprica.vlcj.player.base.TrackDescription;

import java.util.List;

public class VideoTrackChooserController {

    PlaybackSettingsController playbackSettingsController;

    ScrollPane scrollPane = new ScrollPane();

    VBox videoTrackChooserBox = new VBox();
    HBox titleBox = new HBox();

    StackPane backPane = new StackPane();
    Region backIcon = new Region();
    SVGPath backSVG = new SVGPath();

    Label titleLabel = new Label();

    final int DEFAULT_HEIGHT = 136;

    public VideoTrackTab selectedTab = null;


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
        titleBox.getChildren().addAll(backPane, titleLabel);

        backPane.setMinSize(25, 40);
        backPane.setPrefSize(25, 40);
        backPane.setMaxSize(25, 40);
        backPane.getChildren().add(backIcon);
        backPane.setCursor(Cursor.HAND);
        backPane.setOnMouseClicked((e) -> closeVideoTrackChooser());

        backIcon.setMinSize(8, 13);
        backIcon.setPrefSize(8, 13);
        backIcon.setMaxSize(8, 13);
        backIcon.getStyleClass().add("settingsPaneIcon");
        backIcon.setShape(backSVG);

        titleLabel.setText("Video tracks");
        titleLabel.setCursor(Cursor.HAND);
        titleLabel.getStyleClass().add("settingsPaneText");
        titleLabel.setOnMouseClicked((e) -> closeVideoTrackChooser());



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
        for(TrackDescription trackDescription : trackDescriptions){
            if(trackDescription.id() == defaultTrack){
                this.selectedTab = new VideoTrackTab(this, trackDescription, true);
            }
            else new VideoTrackTab(this, trackDescription, false);
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

        scrollPane.setPrefHeight(DEFAULT_HEIGHT + 3);
        scrollPane.setMaxHeight(DEFAULT_HEIGHT + 3);

        videoTrackChooserBox.setPrefHeight(DEFAULT_HEIGHT);
        videoTrackChooserBox.setMaxHeight(DEFAULT_HEIGHT);

        if(playbackSettingsController.playbackSettingsState == PlaybackSettingsState.VIDEO_TRACK_CHOOSER_OPEN)
            playbackSettingsController.clip.setHeight(DEFAULT_HEIGHT + 3);
    }
}
