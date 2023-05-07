package hans.PlaybackSettings;

import hans.*;
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

import java.util.ArrayList;

public class PlaybackSpeedPane{

    PlaybackSpeedController playbackSpeedController;

    ArrayList<PlaybackSpeedTab> speedTabs = new ArrayList<>();
    PlaybackSpeedTab customSpeedTab;

    ScrollPane scrollPane = new ScrollPane();

    VBox playbackSpeedBox = new VBox();
    HBox playbackSpeedTitle = new HBox();

    StackPane playbackSpeedBackPane = new StackPane();
    Region playbackSpeedBackIcon = new Region();
    SVGPath backSVG = new SVGPath();

    HBox titleLabelWrapper = new HBox();
    Label playbackSpeedTitleLabel = new Label();
    Label playbackSpeedCustomLabel = new Label();

    PlaybackSpeedPane(PlaybackSpeedController  playbackSpeedController){
        this.playbackSpeedController = playbackSpeedController;

        backSVG.setContent(SVG.CHEVRON_LEFT.getContent());

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("settingsScroll");
        scrollPane.setPrefSize(235, 349);
        scrollPane.setMaxSize(235, 349);
        scrollPane.setContent(playbackSpeedBox);
        scrollPane.setVisible(false);
        scrollPane.setMouseTransparent(true);
        scrollPane.setFitToWidth(true);

        StackPane.setAlignment(scrollPane, Pos.BOTTOM_RIGHT);
        playbackSpeedBox.setAlignment(Pos.TOP_LEFT);
        playbackSpeedBox.setPrefSize(235, 346);
        playbackSpeedBox.setMaxSize(235, 346);
        playbackSpeedBox.setPadding(new Insets(0, 0, 8, 0));
        playbackSpeedBox.getChildren().add(playbackSpeedTitle);
        playbackSpeedBox.setFillWidth(true);

        playbackSpeedTitle.setPrefSize(235, 48);
        playbackSpeedTitle.setMaxSize(235, 48);
        playbackSpeedTitle.setPadding(new Insets(0, 5, 0, 10));
        VBox.setMargin(playbackSpeedTitle, new Insets(0, 0, 10, 0));
        playbackSpeedTitle.setAlignment(Pos.CENTER_LEFT);
        playbackSpeedTitle.getStyleClass().add("settingsPaneTitle");
        playbackSpeedTitle.getChildren().addAll(titleLabelWrapper, playbackSpeedCustomLabel);

        playbackSpeedBackPane.setMinSize(25, 40);
        playbackSpeedBackPane.setPrefSize(25, 40);
        playbackSpeedBackPane.setMaxSize(25, 40);
        playbackSpeedBackPane.getChildren().add(playbackSpeedBackIcon);
        playbackSpeedBackPane.setCursor(Cursor.HAND);
        playbackSpeedBackPane.setOnMouseClicked((e) -> closePlaybackSpeedPane());

        playbackSpeedBackIcon.setMinSize(8, 13);
        playbackSpeedBackIcon.setPrefSize(8, 13);
        playbackSpeedBackIcon.setMaxSize(8, 13);
        playbackSpeedBackIcon.getStyleClass().add("settingsPaneIcon");
        playbackSpeedBackIcon.setShape(backSVG);

        titleLabelWrapper.getChildren().addAll(playbackSpeedBackPane, playbackSpeedTitleLabel);
        titleLabelWrapper.setAlignment(Pos.CENTER_LEFT);
        titleLabelWrapper.setPrefWidth(155);

        playbackSpeedTitleLabel.setText("Playback speed");
        playbackSpeedTitleLabel.setCursor(Cursor.HAND);
        playbackSpeedTitleLabel.getStyleClass().add("settingsPaneText");
        playbackSpeedTitleLabel.setOnMouseClicked((e) -> closePlaybackSpeedPane());


        playbackSpeedCustomLabel.getStyleClass().addAll("settingsPaneText", "settingsPaneSubText");
        playbackSpeedCustomLabel.setText("Custom");
        playbackSpeedCustomLabel.setUnderline(true);
        playbackSpeedCustomLabel.setPrefWidth(50);
        playbackSpeedCustomLabel.setMaxWidth(50);
        playbackSpeedCustomLabel.setCursor(Cursor.HAND);
        playbackSpeedCustomLabel.setOnMouseClicked((e) -> openCustomSpeedPane());

        for(int i=0; i<8; i++){
            new PlaybackSpeedTab(playbackSpeedController, this, false);
        }

        playbackSpeedController.playbackSettingsController.playbackSettingsPane.getChildren().add(scrollPane);
    }

    public void closePlaybackSpeedPane(){
        if(playbackSpeedController.playbackSettingsController.animating.get()) return;

        playbackSpeedController.playbackSettingsController.playbackSettingsState = PlaybackSettingsState.HOME_OPEN;

        playbackSpeedController.playbackSettingsController.playbackSettingsHomeController.playbackSettingsHomeScroll.setVisible(true);
        playbackSpeedController.playbackSettingsController.playbackSettingsHomeController.playbackSettingsHomeScroll.setMouseTransparent(false);

        Timeline clipTimeline = new Timeline();
        clipTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(playbackSpeedController.playbackSettingsController.clip.heightProperty(), playbackSpeedController.playbackSettingsController.playbackSettingsHomeController.playbackSettingsHomeScroll.getHeight())));

        TranslateTransition homeTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), playbackSpeedController.playbackSettingsController.playbackSettingsHomeController.playbackSettingsHomeScroll);
        homeTransition.setFromX(-playbackSpeedController.playbackSettingsController.playbackSettingsHomeController.playbackSettingsHomeScroll.getWidth());
        homeTransition.setToX(0);

        TranslateTransition speedTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), scrollPane);
        speedTransition.setFromX(0);
        speedTransition.setToX(scrollPane.getWidth());


        ParallelTransition parallelTransition = new ParallelTransition(clipTimeline, homeTransition, speedTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            playbackSpeedController.playbackSettingsController.animating.set(false);
            scrollPane.setVisible(false);
            scrollPane.setMouseTransparent(true);
            scrollPane.setTranslateX(0);
            playbackSpeedController.playbackSettingsController.clip.setHeight(playbackSpeedController.playbackSettingsController.playbackSettingsHomeController.playbackSettingsHomeScroll.getPrefHeight());

        });

        parallelTransition.play();
        playbackSpeedController.playbackSettingsController.animating.set(true);

    }

    public void openCustomSpeedPane(){
        if(playbackSpeedController.playbackSettingsController.animating.get()) return;

        playbackSpeedController.playbackSettingsController.playbackSettingsState = PlaybackSettingsState.CUSTOM_SPEED_OPEN;

        playbackSpeedController.customSpeedPane.customSpeedBox.setVisible(true);
        playbackSpeedController.customSpeedPane.customSpeedBox.setMouseTransparent(false);

        playbackSpeedController.playbackSettingsController.clip.setHeight(scrollPane.getHeight());


        Timeline clipTimeline = new Timeline();
        clipTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(playbackSpeedController.playbackSettingsController.clip.heightProperty(), playbackSpeedController.customSpeedPane.customSpeedBox.getHeight())));

        TranslateTransition speedTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), scrollPane);
        speedTransition.setFromX(0);
        speedTransition.setToX(-scrollPane.getWidth());

        TranslateTransition customTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), playbackSpeedController.customSpeedPane.customSpeedBox);
        customTransition.setFromX(scrollPane.getWidth());
        customTransition.setToX(0);


        ParallelTransition parallelTransition = new ParallelTransition(clipTimeline, speedTransition, customTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            playbackSpeedController.playbackSettingsController.animating.set(false);
            scrollPane.setVisible(false);
            scrollPane.setMouseTransparent(true);
            scrollPane.setTranslateX(0);
        });

        playbackSpeedController.playbackSettingsController.animating.set(true);
        parallelTransition.play();
    }
}
