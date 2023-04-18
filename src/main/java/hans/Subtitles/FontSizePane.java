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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

import java.util.ArrayList;

public class FontSizePane {

    SubtitlesController subtitlesController;
    SubtitlesOptionsPane subtitlesOptionsPane;

    ScrollPane scrollPane = new ScrollPane();

    VBox fontSizeBox = new VBox();
    HBox fontSizeTitle = new HBox();

    StackPane fontSizeBackPane = new StackPane();
    Region fontSizeBackIcon = new Region();
    SVGPath backSVG = new SVGPath();

    Label fontSizeTitleLabel = new Label();

    CheckTab _50Tab, _75Tab, _100Tab, _150Tab, _200Tab, _300Tab, _400Tab;

    ArrayList<CheckTab> checkTabs = new ArrayList<>();


    FontSizePane(SubtitlesController subtitlesController, SubtitlesOptionsPane subtitlesOptionsPane){
        this.subtitlesController = subtitlesController;
        this.subtitlesOptionsPane = subtitlesOptionsPane;

        backSVG.setContent(App.svgMap.get(SVG.CHEVRON_LEFT));

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("settingsScroll");
        scrollPane.setPrefSize(200, 314);
        scrollPane.setMaxSize(200, 314);
        scrollPane.setContent(fontSizeBox);
        scrollPane.setVisible(false);
        scrollPane.setMouseTransparent(true);
        scrollPane.setFitToWidth(true);

        StackPane.setAlignment(scrollPane, Pos.BOTTOM_RIGHT);
        fontSizeBox.setAlignment(Pos.BOTTOM_CENTER);


        fontSizeBox.setPrefSize(200, 311);
        fontSizeBox.setMaxSize(200, 311);
        fontSizeBox.setPadding(new Insets(0, 0, 8, 0));
        fontSizeBox.getChildren().add(fontSizeTitle);
        fontSizeBox.setFillWidth(true);

        fontSizeTitle.setPrefSize(200, 48);
        fontSizeTitle.setMaxSize(200, 48);
        fontSizeTitle.setPadding(new Insets(0, 10, 0, 10));
        fontSizeTitle.setAlignment(Pos.CENTER_LEFT);
        VBox.setMargin(fontSizeTitle, new Insets(0, 0, 10, 0));

        fontSizeTitle.getStyleClass().add("settingsPaneTitle");
        fontSizeTitle.getChildren().addAll(fontSizeBackPane, fontSizeTitleLabel);

        fontSizeBackPane.setMinSize(24, 40);
        fontSizeBackPane.setPrefSize(24, 40);
        fontSizeBackPane.setMaxSize(24, 40);
        fontSizeBackPane.getChildren().add(fontSizeBackIcon);
        fontSizeBackPane.setCursor(Cursor.HAND);
        fontSizeBackPane.setOnMouseClicked((e) -> closeFontSizePane());

        fontSizeBackIcon.setMinSize(8, 13);
        fontSizeBackIcon.setPrefSize(8, 13);
        fontSizeBackIcon.setMaxSize(8, 13);
        fontSizeBackIcon.getStyleClass().add("settingsPaneIcon");
        fontSizeBackIcon.setShape(backSVG);

        fontSizeTitleLabel.setMinHeight(40);
        fontSizeTitleLabel.setPrefHeight(40);
        fontSizeTitleLabel.setMaxHeight(40);
        fontSizeTitleLabel.setText("Font size");
        fontSizeTitleLabel.setCursor(Cursor.HAND);
        fontSizeTitleLabel.getStyleClass().add("settingsPaneText");
        fontSizeTitleLabel.setOnMouseClicked((e) -> closeFontSizePane());

        _50Tab = new CheckTab(false, "50%");
        _75Tab = new CheckTab(false, "75%");
        _100Tab = new CheckTab(true, "100%");
        _150Tab = new CheckTab(false, "150%");
        _200Tab = new CheckTab(false, "200%");
        _300Tab = new CheckTab(false, "300%");
        _400Tab = new CheckTab(false, "400%");

        fontSizeBox.getChildren().addAll(_50Tab, _75Tab, _100Tab, _150Tab, _200Tab, _300Tab, _400Tab);
        checkTabs.add(_50Tab);
        checkTabs.add(_75Tab);
        checkTabs.add(_100Tab);
        checkTabs.add(_150Tab);
        checkTabs.add(_200Tab);
        checkTabs.add(_300Tab);
        checkTabs.add(_400Tab);

        _50Tab.setOnMouseClicked(e -> {

            for(CheckTab checkTab : checkTabs){
                checkTab.checkIcon.setVisible(false);
            }

            _50Tab.checkIcon.setVisible(true);
            subtitlesOptionsPane.fontSizeTab.subText.setText("50%");

            subtitlesController.subtitlesBox.currentFontSize.set(0.75 * subtitlesController.subtitlesBox.defaultFontSize);

            subtitlesController.subtitlesBox.showCaptions();
        });

        _75Tab.setOnMouseClicked(e -> {

            for(CheckTab checkTab : checkTabs){
                checkTab.checkIcon.setVisible(false);
            }

            _75Tab.checkIcon.setVisible(true);
            subtitlesOptionsPane.fontSizeTab.subText.setText("75%");

            subtitlesController.subtitlesBox.currentFontSize.set(0.875 * subtitlesController.subtitlesBox.defaultFontSize);


            subtitlesController.subtitlesBox.showCaptions();
        });

        _100Tab.setOnMouseClicked(e -> {

            for(CheckTab checkTab : checkTabs){
                checkTab.checkIcon.setVisible(false);
            }

            _100Tab.checkIcon.setVisible(true);
            subtitlesOptionsPane.fontSizeTab.subText.setText("100%");


            subtitlesController.subtitlesBox.currentFontSize.set(subtitlesController.subtitlesBox.defaultFontSize);


            subtitlesController.subtitlesBox.showCaptions();
        });

        _150Tab.setOnMouseClicked(e -> {

            for(CheckTab checkTab : checkTabs){
                checkTab.checkIcon.setVisible(false);
            }

            _150Tab.checkIcon.setVisible(true);
            subtitlesOptionsPane.fontSizeTab.subText.setText("150%");


            subtitlesController.subtitlesBox.currentFontSize.set(1.25 * subtitlesController.subtitlesBox.defaultFontSize);


            subtitlesController.subtitlesBox.showCaptions();
        });

        _200Tab.setOnMouseClicked(e -> {

            for(CheckTab checkTab : checkTabs){
                checkTab.checkIcon.setVisible(false);
            }

            _200Tab.checkIcon.setVisible(true);
            subtitlesOptionsPane.fontSizeTab.subText.setText("200%");


            subtitlesController.subtitlesBox.currentFontSize.set(1.5 * subtitlesController.subtitlesBox.defaultFontSize);


            subtitlesController.subtitlesBox.showCaptions();
        });

        _300Tab.setOnMouseClicked(e -> {

            for(CheckTab checkTab : checkTabs){
                checkTab.checkIcon.setVisible(false);
            }

            _300Tab.checkIcon.setVisible(true);
            subtitlesOptionsPane.fontSizeTab.subText.setText("300%");


            subtitlesController.subtitlesBox.currentFontSize.set(2 * subtitlesController.subtitlesBox.defaultFontSize);


            subtitlesController.subtitlesBox.showCaptions();
        });

        _400Tab.setOnMouseClicked(e -> {

            for(CheckTab checkTab : checkTabs){
                checkTab.checkIcon.setVisible(false);
            }

            _400Tab.checkIcon.setVisible(true);
            subtitlesOptionsPane.fontSizeTab.subText.setText("400%");


            subtitlesController.subtitlesBox.currentFontSize.set(2.25 * subtitlesController.subtitlesBox.defaultFontSize);


            subtitlesController.subtitlesBox.showCaptions();
        });


        subtitlesController.subtitlesPane.getChildren().add(scrollPane);
    }


    public void closeFontSizePane(){
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

