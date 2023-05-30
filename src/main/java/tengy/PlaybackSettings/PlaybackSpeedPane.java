package tengy.PlaybackSettings;

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
import javafx.scene.control.ScrollPane;
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

public class PlaybackSpeedPane{

    PlaybackSpeedController playbackSpeedController;

    ArrayList<PlaybackSpeedTab> speedTabs = new ArrayList<>();
    PlaybackSpeedTab customSpeedTab;

    ScrollPane scrollPane = new ScrollPane();

    VBox playbackSpeedBox = new VBox();
    HBox playbackSpeedTitle = new HBox();

    Button backButton = new Button();
    Region playbackSpeedBackIcon = new Region();
    SVGPath backSVG = new SVGPath();

    HBox titleLabelWrapper = new HBox();
    Label playbackSpeedTitleLabel = new Label();
    Button customButton = new Button();

    List<Node> focusNodes = new ArrayList<>();
    IntegerProperty focus = new SimpleIntegerProperty(-1);

    PlaybackSpeedPane(PlaybackSpeedController  playbackSpeedController){
        this.playbackSpeedController = playbackSpeedController;

        backSVG.setContent(SVG.CHEVRON_LEFT.getContent());

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("settingsScroll");
        scrollPane.setPrefSize(250, 349);
        scrollPane.setMaxSize(250, 349);
        scrollPane.setContent(playbackSpeedBox);
        scrollPane.setVisible(false);
        scrollPane.setMouseTransparent(true);
        scrollPane.setFitToWidth(true);

        StackPane.setAlignment(scrollPane, Pos.BOTTOM_RIGHT);
        playbackSpeedBox.setAlignment(Pos.TOP_LEFT);
        playbackSpeedBox.setPrefSize(250, 346);
        playbackSpeedBox.setMaxSize(250, 346);
        playbackSpeedBox.setPadding(new Insets(0, 0, 8, 0));
        playbackSpeedBox.getChildren().add(playbackSpeedTitle);
        playbackSpeedBox.setFillWidth(true);

        playbackSpeedTitle.setPrefSize(250, 48);
        playbackSpeedTitle.setMaxSize(250, 48);
        playbackSpeedTitle.setPadding(new Insets(0, 5, 0, 10));
        VBox.setMargin(playbackSpeedTitle, new Insets(0, 0, 10, 0));
        playbackSpeedTitle.setAlignment(Pos.CENTER_LEFT);
        playbackSpeedTitle.getStyleClass().add("settingsPaneTitle");
        playbackSpeedTitle.getChildren().addAll(titleLabelWrapper, customButton);

        backButton.setMinSize(30, 40);
        backButton.setPrefSize(30, 40);
        backButton.setMaxSize(30, 40);
        backButton.setFocusTraversable(false);
        backButton.getStyleClass().addAll("transparentButton", "settingsMenuButton");
        backButton.setGraphic(playbackSpeedBackIcon);
        backButton.setOnAction((e) -> closePlaybackSpeedPane());
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

        playbackSpeedBackIcon.setMinSize(8, 13);
        playbackSpeedBackIcon.setPrefSize(8, 13);
        playbackSpeedBackIcon.setMaxSize(8, 13);
        playbackSpeedBackIcon.getStyleClass().add("graphic");
        playbackSpeedBackIcon.setShape(backSVG);

        titleLabelWrapper.getChildren().addAll(backButton, playbackSpeedTitleLabel);
        titleLabelWrapper.setAlignment(Pos.CENTER_LEFT);
        titleLabelWrapper.setPrefWidth(145);

        playbackSpeedTitleLabel.setText("Playback speed");
        playbackSpeedTitleLabel.setCursor(Cursor.HAND);
        playbackSpeedTitleLabel.getStyleClass().add("settingsPaneText");
        playbackSpeedTitleLabel.setOnMouseClicked((e) -> closePlaybackSpeedPane());
        playbackSpeedTitleLabel.setPadding(new Insets(0, 0, 0, 4));



        customButton.getStyleClass().addAll("transparentButton", "settingsMenuButton");
        customButton.setText("Custom");
        customButton.setMinWidth(75);
        customButton.setPrefWidth(75);
        customButton.setMaxWidth(75);
        customButton.setOnAction((e) -> openCustomSpeedPane());
        customButton.setAlignment(Pos.CENTER);
        customButton.setFocusTraversable(false);
        customButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue) focus.set(1);
            else {
                keyboardFocusOff(customButton);
                focus.set(-1);
            }
        });

        customButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            customButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        customButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            customButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });

        focusNodes.add(backButton);
        focusNodes.add(customButton);


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

        Timeline clipHeightTimeline = new Timeline();
        clipHeightTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(playbackSpeedController.playbackSettingsController.clip.heightProperty(), playbackSpeedController.playbackSettingsController.playbackSettingsHomeController.playbackSettingsHomeScroll.getHeight())));

        Timeline clipWidthTimeline = new Timeline();
        clipWidthTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(playbackSpeedController.playbackSettingsController.clip.widthProperty(), playbackSpeedController.playbackSettingsController.playbackSettingsHomeController.playbackSettingsHomeScroll.getWidth())));

        TranslateTransition homeTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), playbackSpeedController.playbackSettingsController.playbackSettingsHomeController.playbackSettingsHomeScroll);
        homeTransition.setFromX(-playbackSpeedController.playbackSettingsController.playbackSettingsHomeController.playbackSettingsHomeScroll.getWidth());
        homeTransition.setToX(0);

        TranslateTransition speedTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), scrollPane);
        speedTransition.setFromX(0);
        speedTransition.setToX(scrollPane.getWidth());


        ParallelTransition parallelTransition = new ParallelTransition(clipWidthTimeline, clipHeightTimeline, homeTransition, speedTransition);
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


        Timeline clipWidthTimeline = new Timeline();
        clipWidthTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(playbackSpeedController.playbackSettingsController.clip.widthProperty(), playbackSpeedController.customSpeedPane.customSpeedBox.getWidth())));

        Timeline clipHeightTimeline = new Timeline();
        clipHeightTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(playbackSpeedController.playbackSettingsController.clip.heightProperty(), playbackSpeedController.customSpeedPane.customSpeedBox.getHeight())));

        TranslateTransition speedTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), scrollPane);
        speedTransition.setFromX(0);
        speedTransition.setToX(-scrollPane.getWidth());

        TranslateTransition customTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), playbackSpeedController.customSpeedPane.customSpeedBox);
        customTransition.setFromX(scrollPane.getWidth());
        customTransition.setToX(0);


        ParallelTransition parallelTransition = new ParallelTransition(clipWidthTimeline, clipHeightTimeline, speedTransition, customTransition);
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

    public void focusForward(){
        int newFocus;

        if(focus.get() >= focusNodes.size() - 1 || focus.get() == -1) newFocus = 0;
        else newFocus = focus.get() + 1;

        keyboardFocusOn(focusNodes.get(newFocus));

        if(newFocus == 0 || newFocus == 1) scrollPane.setVvalue(0);
        else Utilities.checkScrollDown(scrollPane, focusNodes.get(newFocus));
    }

    public void focusBackward() {
        int newFocus;

        if (focus.get() == 0) newFocus = focusNodes.size() - 1;
        else if (focus.get() == -1) newFocus = 0;
        else newFocus = focus.get() - 1;

        keyboardFocusOn(focusNodes.get(newFocus));

        if(newFocus == 0 || newFocus == 1) scrollPane.setVvalue(0);
        else if(newFocus == focusNodes.size() - 1) scrollPane.setVvalue(1);
        else Utilities.checkScrollUp(scrollPane, focusNodes.get(newFocus));
    }
}
