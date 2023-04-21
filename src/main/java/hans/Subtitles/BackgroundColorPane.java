package hans.Subtitles;

import hans.*;
import hans.PlaybackSettings.CheckTab;
import hans.PlaybackSettings.PlaybackSettingsController;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

import java.util.ArrayList;

public class BackgroundColorPane {

    SubtitlesController subtitlesController;
    SubtitlesOptionsPane subtitlesOptionsPane;

    ScrollPane scrollPane = new ScrollPane();

    VBox backgroundColorBox = new VBox();
    HBox backgroundColorTitle = new HBox();

    StackPane backgroundColorBackPane = new StackPane();
    Region backgroundColorBackIcon = new Region();
    SVGPath backSVG = new SVGPath();

    Label backgroundColorTitleLabel = new Label();

    CheckTab whiteTab, yellowTab, greenTab, cyanTab, blueTab, magentaTab, redTab, blackTab;

    ArrayList<CheckTab> checkTabs = new ArrayList<>();

    BackgroundColorPane(SubtitlesController subtitlesController, SubtitlesOptionsPane subtitlesOptionsPane){
        this.subtitlesController = subtitlesController;
        this.subtitlesOptionsPane = subtitlesOptionsPane;

        backSVG.setContent(App.svgMap.get(SVG.CHEVRON_LEFT));

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("settingsScroll");
        scrollPane.setPrefSize(200, 349);
        scrollPane.setMaxSize(200, 349);
        scrollPane.setContent(backgroundColorBox);
        scrollPane.setVisible(false);
        scrollPane.setMouseTransparent(true);
        scrollPane.setFitToWidth(true);

        StackPane.setAlignment(scrollPane, Pos.BOTTOM_RIGHT);
        backgroundColorBox.setAlignment(Pos.BOTTOM_CENTER);


        backgroundColorBox.setPrefSize(200, 346);
        backgroundColorBox.setMaxSize(200, 346);
        backgroundColorBox.setPadding(new Insets(0, 0, 8, 0));
        backgroundColorBox.getChildren().add(backgroundColorTitle);
        backgroundColorBox.setFillWidth(true);

        backgroundColorTitle.setPrefSize(200, 48);
        backgroundColorTitle.setMaxSize(200, 48);
        backgroundColorTitle.setPadding(new Insets(0, 10, 0, 10));
        backgroundColorTitle.setAlignment(Pos.CENTER_LEFT);
        VBox.setMargin(backgroundColorTitle, new Insets(0, 0, 10, 0));

        backgroundColorTitle.getStyleClass().add("settingsPaneTitle");
        backgroundColorTitle.getChildren().addAll(backgroundColorBackPane, backgroundColorTitleLabel);

        backgroundColorBackPane.setMinSize(24, 40);
        backgroundColorBackPane.setPrefSize(24, 40);
        backgroundColorBackPane.setMaxSize(24, 40);
        backgroundColorBackPane.getChildren().add(backgroundColorBackIcon);
        backgroundColorBackPane.setCursor(Cursor.HAND);
        backgroundColorBackPane.setOnMouseClicked((e) -> closeBackgroundColorPane());

        backgroundColorBackIcon.setMinSize(8, 13);
        backgroundColorBackIcon.setPrefSize(8, 13);
        backgroundColorBackIcon.setMaxSize(8, 13);
        backgroundColorBackIcon.getStyleClass().add("settingsPaneIcon");
        backgroundColorBackIcon.setShape(backSVG);

        backgroundColorTitleLabel.setMinHeight(40);
        backgroundColorTitleLabel.setPrefHeight(40);
        backgroundColorTitleLabel.setMaxHeight(40);
        backgroundColorTitleLabel.setText("Background color");
        backgroundColorTitleLabel.setCursor(Cursor.HAND);
        backgroundColorTitleLabel.getStyleClass().add("settingsPaneText");
        backgroundColorTitleLabel.setOnMouseClicked((e) -> closeBackgroundColorPane());

        whiteTab = new CheckTab(false, "White");
        yellowTab = new CheckTab(false, "Yellow");
        greenTab = new CheckTab(false, "Green");
        cyanTab = new CheckTab(false, "Cyan");
        blueTab = new CheckTab(false, "Blue");
        magentaTab = new CheckTab(false, "Magenta");
        redTab = new CheckTab(false, "Red");
        blackTab = new CheckTab(true, "Black");

        backgroundColorBox.getChildren().addAll(whiteTab, yellowTab, greenTab, cyanTab, blueTab, magentaTab, redTab, blackTab);
        checkTabs.add(whiteTab);
        checkTabs.add(yellowTab);
        checkTabs.add(greenTab);
        checkTabs.add(cyanTab);
        checkTabs.add(blueTab);
        checkTabs.add(magentaTab);
        checkTabs.add(redTab);
        checkTabs.add(blackTab);


        whiteTab.setOnMouseClicked(e -> {

            for(CheckTab checkTab : checkTabs){
                checkTab.checkIcon.setVisible(false);
            }

            whiteTab.checkIcon.setVisible(true);
            subtitlesOptionsPane.backgroundColorTab.subText.setText("White");

            subtitlesController.subtitlesBox.currentBackgroundColor.set(Color.rgb(255, 255, 255, subtitlesController.subtitlesBox.currentBackgroundColor.get().getOpacity()));

            subtitlesController.subtitlesBox.showCaptions();
        });

        yellowTab.setOnMouseClicked(e -> {

            for(CheckTab checkTab : checkTabs){
                checkTab.checkIcon.setVisible(false);
            }

            yellowTab.checkIcon.setVisible(true);
            subtitlesOptionsPane.backgroundColorTab.subText.setText("Yellow");

            subtitlesController.subtitlesBox.currentBackgroundColor.set(Color.rgb(255, 255, 0, subtitlesController.subtitlesBox.currentBackgroundColor.get().getOpacity()));

            subtitlesController.subtitlesBox.showCaptions();
        });

        greenTab.setOnMouseClicked(e -> {

            for(CheckTab checkTab : checkTabs){
                checkTab.checkIcon.setVisible(false);
            }

            greenTab.checkIcon.setVisible(true);
            subtitlesOptionsPane.backgroundColorTab.subText.setText("Green");

            subtitlesController.subtitlesBox.currentBackgroundColor.set(Color.rgb(0, 255, 0, subtitlesController.subtitlesBox.currentBackgroundColor.get().getOpacity()));


            subtitlesController.subtitlesBox.showCaptions();
        });

        cyanTab.setOnMouseClicked(e -> {

            for(CheckTab checkTab : checkTabs){
                checkTab.checkIcon.setVisible(false);
            }

            cyanTab.checkIcon.setVisible(true);
            subtitlesOptionsPane.backgroundColorTab.subText.setText("Cyan");


            subtitlesController.subtitlesBox.currentBackgroundColor.set(Color.rgb(0, 255, 255, subtitlesController.subtitlesBox.currentBackgroundColor.get().getOpacity()));


            subtitlesController.subtitlesBox.showCaptions();
        });

        blueTab.setOnMouseClicked(e -> {

            for(CheckTab checkTab : checkTabs){
                checkTab.checkIcon.setVisible(false);
            }

            blueTab.checkIcon.setVisible(true);
            subtitlesOptionsPane.backgroundColorTab.subText.setText("Blue");


            subtitlesController.subtitlesBox.currentBackgroundColor.set(Color.rgb(0, 0, 255, subtitlesController.subtitlesBox.currentBackgroundColor.get().getOpacity()));

            subtitlesController.subtitlesBox.showCaptions();
        });

        magentaTab.setOnMouseClicked(e -> {

            for(CheckTab checkTab : checkTabs){
                checkTab.checkIcon.setVisible(false);
            }

            magentaTab.checkIcon.setVisible(true);
            subtitlesOptionsPane.backgroundColorTab.subText.setText("Magenta");

            subtitlesController.subtitlesBox.currentBackgroundColor.set(Color.rgb(255, 0, 255, subtitlesController.subtitlesBox.currentBackgroundColor.get().getOpacity()));


            subtitlesController.subtitlesBox.showCaptions();
        });

        redTab.setOnMouseClicked(e -> {

            for(CheckTab checkTab : checkTabs){
                checkTab.checkIcon.setVisible(false);
            }

            redTab.checkIcon.setVisible(true);
            subtitlesOptionsPane.backgroundColorTab.subText.setText("Red");


            subtitlesController.subtitlesBox.currentBackgroundColor.set(Color.rgb(255, 0, 0, subtitlesController.subtitlesBox.currentBackgroundColor.get().getOpacity()));


            subtitlesController.subtitlesBox.showCaptions();
        });

        blackTab.setOnMouseClicked(e -> {

            for(CheckTab checkTab : checkTabs){
                checkTab.checkIcon.setVisible(false);
            }

            blackTab.checkIcon.setVisible(true);
            subtitlesOptionsPane.backgroundColorTab.subText.setText("Black");

            subtitlesController.subtitlesBox.currentBackgroundColor.set(Color.rgb(0, 0, 0, subtitlesController.subtitlesBox.currentBackgroundColor.get().getOpacity()));


            subtitlesController.subtitlesBox.showCaptions();
        });

        subtitlesController.subtitlesPane.getChildren().add(scrollPane);
    }


    public void closeBackgroundColorPane(){
        if(subtitlesController.animating.get()) return;

        subtitlesController.subtitlesState = SubtitlesState.CAPTIONS_OPTIONS_OPEN;

        subtitlesController.subtitlesOptionsPane.scrollPane.setVisible(true);
        subtitlesController.subtitlesOptionsPane.scrollPane.setMouseTransparent(false);


        Timeline clipHeightTimeline = new Timeline();
        clipHeightTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(subtitlesController.clip.heightProperty(), subtitlesController.subtitlesOptionsPane.scrollPane.getHeight())));


        Timeline clipWidthTimeline = new Timeline();
        clipWidthTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(subtitlesController.clip.widthProperty(), subtitlesController.subtitlesOptionsPane.scrollPane.getWidth())));



        TranslateTransition fontColorTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), scrollPane);
        fontColorTransition.setFromX(0);
        fontColorTransition.setToX(scrollPane.getWidth());

        TranslateTransition captionsOptionsTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), subtitlesController.subtitlesOptionsPane.scrollPane);
        captionsOptionsTransition.setFromX(-scrollPane.getWidth());
        captionsOptionsTransition.setToX(0);


        ParallelTransition parallelTransition = new ParallelTransition(clipHeightTimeline, clipWidthTimeline, fontColorTransition, captionsOptionsTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            subtitlesController.animating.set(false);
            scrollPane.setVisible(false);
            scrollPane.setMouseTransparent(true);
            scrollPane.setTranslateX(0);
            subtitlesController.clip.setHeight(subtitlesController.subtitlesOptionsPane.scrollPane.getPrefHeight());
        });

        parallelTransition.play();
        subtitlesController.animating.set(true);
    }
}

