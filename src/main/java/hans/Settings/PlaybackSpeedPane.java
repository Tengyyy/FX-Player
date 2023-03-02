package hans.Settings;

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
    StackPane playbackSpeedTitle = new StackPane();

    StackPane playbackSpeedBackPane = new StackPane();
    Region playbackSpeedBackIcon = new Region();
    SVGPath backSVG = new SVGPath();

    HBox titleLabelWrapper = new HBox();
    Label playbackSpeedTitleLabel = new Label();
    Label playbackSpeedCustomLabel = new Label();

    PlaybackSpeedPane(PlaybackSpeedController  playbackSpeedController){
        this.playbackSpeedController = playbackSpeedController;

        backSVG.setContent(App.svgMap.get(SVG.CHEVRON_LEFT));

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("settingsScroll");
        scrollPane.setPrefSize(235, 349);
        scrollPane.setMaxSize(235, 349);
        scrollPane.setContent(playbackSpeedBox);
        scrollPane.setVisible(false);
        scrollPane.setMouseTransparent(true);

        StackPane.setAlignment(scrollPane, Pos.BOTTOM_RIGHT);
        playbackSpeedBox.setAlignment(Pos.BOTTOM_CENTER);


        playbackSpeedBox.setPrefSize(220, 346);
        playbackSpeedBox.setMaxSize(220, 346);
        playbackSpeedBox.setPadding(new Insets(8, 0, 8, 0));
        playbackSpeedBox.getChildren().add(playbackSpeedTitle);

        playbackSpeedTitle.setPrefSize(220, 40);
        playbackSpeedTitle.setMaxSize(220, 40);
        playbackSpeedTitle.setPadding(new Insets(0, 5, 0, 10));
        VBox.setMargin(playbackSpeedTitle, new Insets(0, 0, 10, 0));

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

        StackPane.setAlignment(titleLabelWrapper, Pos.CENTER_LEFT);
        titleLabelWrapper.getChildren().addAll(playbackSpeedBackPane, playbackSpeedTitleLabel);
        titleLabelWrapper.setAlignment(Pos.CENTER_LEFT);

        playbackSpeedTitleLabel.setText("Playback speed");
        playbackSpeedTitleLabel.setCursor(Cursor.HAND);
        playbackSpeedTitleLabel.getStyleClass().add("settingsPaneText");
        playbackSpeedTitleLabel.setOnMouseClicked((e) -> closePlaybackSpeedPane());


        StackPane.setAlignment(playbackSpeedCustomLabel, Pos.CENTER_RIGHT);
        playbackSpeedCustomLabel.getStyleClass().addAll("settingsPaneText", "settingsPaneSubText");
        playbackSpeedCustomLabel.setText("Custom");
        playbackSpeedCustomLabel.setUnderline(true);
        playbackSpeedCustomLabel.setPrefSize(50,40);
        playbackSpeedCustomLabel.setMaxSize(50, 40);
        playbackSpeedCustomLabel.setCursor(Cursor.HAND);
        playbackSpeedCustomLabel.setOnMouseClicked((e) -> openCustomSpeedPane());

        for(int i=0; i<8; i++){
            new PlaybackSpeedTab(playbackSpeedController, this, false);
        }

        playbackSpeedController.settingsController.settingsPane.getChildren().add(scrollPane);
    }

    public void closePlaybackSpeedPane(){
        if(playbackSpeedController.settingsController.animating.get()) return;

        playbackSpeedController.settingsController.settingsState = SettingsState.HOME_OPEN;

        playbackSpeedController.settingsController.settingsHomeController.settingsHome.setVisible(true);
        playbackSpeedController.settingsController.settingsHomeController.settingsHome.setMouseTransparent(false);

        Timeline clipTimeline = new Timeline();
        clipTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(playbackSpeedController.settingsController.clip.heightProperty(), playbackSpeedController.settingsController.settingsHomeController.settingsHome.getHeight())));

        TranslateTransition homeTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), playbackSpeedController.settingsController.settingsHomeController.settingsHome);
        homeTransition.setFromX(-playbackSpeedController.settingsController.settingsHomeController.settingsHome.getWidth());
        homeTransition.setToX(0);

        TranslateTransition speedTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), scrollPane);
        speedTransition.setFromX(0);
        speedTransition.setToX(scrollPane.getWidth());


        ParallelTransition parallelTransition = new ParallelTransition(clipTimeline, homeTransition, speedTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            playbackSpeedController.settingsController.animating.set(false);
            scrollPane.setVisible(false);
            scrollPane.setMouseTransparent(true);
            scrollPane.setTranslateX(0);
        });

        parallelTransition.play();
        playbackSpeedController.settingsController.animating.set(true);

    }

    public void openCustomSpeedPane(){
        if(playbackSpeedController.settingsController.animating.get()) return;

        playbackSpeedController.settingsController.settingsState = SettingsState.CUSTOM_SPEED_OPEN;

        playbackSpeedController.customSpeedPane.customSpeedBox.setVisible(true);
        playbackSpeedController.customSpeedPane.customSpeedBox.setMouseTransparent(false);

        playbackSpeedController.settingsController.clip.setHeight(scrollPane.getHeight());


        Timeline clipTimeline = new Timeline();
        clipTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(playbackSpeedController.settingsController.clip.heightProperty(), playbackSpeedController.customSpeedPane.customSpeedBox.getHeight())));

        TranslateTransition speedTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), scrollPane);
        speedTransition.setFromX(0);
        speedTransition.setToX(-scrollPane.getWidth());

        TranslateTransition customTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), playbackSpeedController.customSpeedPane.customSpeedBox);
        customTransition.setFromX(scrollPane.getWidth());
        customTransition.setToX(0);


        ParallelTransition parallelTransition = new ParallelTransition(clipTimeline, speedTransition, customTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            playbackSpeedController.settingsController.animating.set(false);
            scrollPane.setVisible(false);
            scrollPane.setMouseTransparent(true);
            scrollPane.setTranslateX(0);
        });

        playbackSpeedController.settingsController.animating.set(true);
        parallelTransition.play();
    }
}
