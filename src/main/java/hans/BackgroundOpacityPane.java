package hans;

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

public class BackgroundOpacityPane {

    CaptionsController captionsController;
    CaptionsOptionsPane captionsOptionsPane;

    ScrollPane scrollPane = new ScrollPane();

    VBox backgroundOpacityBox = new VBox();
    HBox backgroundOpacityTitle = new HBox();

    StackPane backgroundOpacityBackPane = new StackPane();
    Region backgroundOpacityBackIcon = new Region();
    SVGPath backSVG = new SVGPath();

    Label backgroundOpacityTitleLabel = new Label();

    CheckTab _0Tab, _25Tab, _50Tab, _75Tab, _100Tab;

    ArrayList<CheckTab> checkTabs = new ArrayList<>();

    BackgroundOpacityPane(CaptionsController captionsController, CaptionsOptionsPane captionsOptionsPane){
        this.captionsController = captionsController;
        this.captionsOptionsPane = captionsOptionsPane;

        backSVG.setContent(App.svgMap.get(SVG.CHEVRON_LEFT));

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("settingsScroll");
        scrollPane.setPrefSize(190, 244);
        scrollPane.setMaxSize(190, 244);
        scrollPane.setContent(backgroundOpacityBox);
        scrollPane.setVisible(false);
        scrollPane.setMouseTransparent(true);

        StackPane.setAlignment(scrollPane, Pos.BOTTOM_RIGHT);
        backgroundOpacityBox.setAlignment(Pos.BOTTOM_CENTER);


        backgroundOpacityBox.setMinSize(185, 241);
        backgroundOpacityBox.setPrefSize(185, 241);
        backgroundOpacityBox.setMaxSize(185, 241);
        backgroundOpacityBox.setPadding(new Insets(8, 0, 8, 0));
        backgroundOpacityBox.getChildren().add(backgroundOpacityTitle);

        backgroundOpacityTitle.setMinSize(185, 40);
        backgroundOpacityTitle.setPrefSize(185, 40);
        backgroundOpacityTitle.setMaxSize(185, 40);
        backgroundOpacityTitle.setPadding(new Insets(0, 10, 0, 10));
        VBox.setMargin(backgroundOpacityTitle, new Insets(0, 0, 10, 0));

        backgroundOpacityTitle.getStyleClass().add("settingsPaneTitle");
        backgroundOpacityTitle.getChildren().addAll(backgroundOpacityBackPane, backgroundOpacityTitleLabel);

        backgroundOpacityBackPane.setMinSize(24, 40);
        backgroundOpacityBackPane.setPrefSize(24, 40);
        backgroundOpacityBackPane.setMaxSize(24, 40);
        backgroundOpacityBackPane.getChildren().add(backgroundOpacityBackIcon);
        backgroundOpacityBackPane.setCursor(Cursor.HAND);
        backgroundOpacityBackPane.setOnMouseClicked((e) -> closeBackgroundOpacityPane());

        backgroundOpacityBackIcon.setMinSize(8, 13);
        backgroundOpacityBackIcon.setPrefSize(8, 13);
        backgroundOpacityBackIcon.setMaxSize(8, 13);
        backgroundOpacityBackIcon.getStyleClass().add("settingsPaneIcon");
        backgroundOpacityBackIcon.setShape(backSVG);

        backgroundOpacityTitleLabel.setMinHeight(40);
        backgroundOpacityTitleLabel.setPrefHeight(40);
        backgroundOpacityTitleLabel.setMaxHeight(40);
        backgroundOpacityTitleLabel.setText("Background opacity");
        backgroundOpacityTitleLabel.setCursor(Cursor.HAND);
        backgroundOpacityTitleLabel.getStyleClass().add("settingsPaneText");
        backgroundOpacityTitleLabel.setOnMouseClicked((e) -> closeBackgroundOpacityPane());

        _0Tab = new CheckTab(false, "0%");
        _25Tab = new CheckTab(false, "25%");
        _50Tab = new CheckTab(false, "50%");
        _75Tab = new CheckTab(true, "75%");
        _100Tab = new CheckTab(false, "100%");

        backgroundOpacityBox.getChildren().addAll(_0Tab, _25Tab, _50Tab, _75Tab, _100Tab);
        checkTabs.add(_0Tab);
        checkTabs.add(_25Tab);
        checkTabs.add(_50Tab);
        checkTabs.add(_75Tab);
        checkTabs.add(_100Tab);

        _0Tab.setOnMouseClicked(e -> {

            for(CheckTab checkTab : checkTabs){
                checkTab.checkIcon.setVisible(false);
            }

            _0Tab.checkIcon.setVisible(true);
            captionsOptionsPane.backgroundOpacityTab.subText.setText("0%");

            captionsController.currentBackgroundOpacity = 0;
            captionsController.currentBackground = Color.rgb(captionsController.currentBackgroundRed, captionsController.currentBackgroundGreen, captionsController.currentBackgroundBlue, captionsController.currentBackgroundOpacity);

            captionsController.captionsLabel1.setBackground(new Background(new BackgroundFill(captionsController.currentBackground, CornerRadii.EMPTY, Insets.EMPTY)));
            captionsController.captionsLabel2.setBackground(new Background(new BackgroundFill(captionsController.currentBackground, CornerRadii.EMPTY, Insets.EMPTY)));

            captionsController.showCaptions();
        });

        _25Tab.setOnMouseClicked(e -> {

            for(CheckTab checkTab : checkTabs){
                checkTab.checkIcon.setVisible(false);
            }

            _25Tab.checkIcon.setVisible(true);
            captionsOptionsPane.backgroundOpacityTab.subText.setText("25%");

            captionsController.currentBackgroundOpacity = 0.25;
            captionsController.currentBackground = Color.rgb(captionsController.currentBackgroundRed, captionsController.currentBackgroundGreen, captionsController.currentBackgroundBlue, captionsController.currentBackgroundOpacity);

            captionsController.captionsLabel1.setBackground(new Background(new BackgroundFill(captionsController.currentBackground, CornerRadii.EMPTY, Insets.EMPTY)));
            captionsController.captionsLabel2.setBackground(new Background(new BackgroundFill(captionsController.currentBackground, CornerRadii.EMPTY, Insets.EMPTY)));

            captionsController.showCaptions();
        });

        _50Tab.setOnMouseClicked(e -> {

            for(CheckTab checkTab : checkTabs){
                checkTab.checkIcon.setVisible(false);
            }

            _50Tab.checkIcon.setVisible(true);
            captionsOptionsPane.backgroundOpacityTab.subText.setText("50%");

            captionsController.currentBackgroundOpacity = 0.5;
            captionsController.currentBackground = Color.rgb(captionsController.currentBackgroundRed, captionsController.currentBackgroundGreen, captionsController.currentBackgroundBlue, captionsController.currentBackgroundOpacity);

            captionsController.captionsLabel1.setBackground(new Background(new BackgroundFill(captionsController.currentBackground, CornerRadii.EMPTY, Insets.EMPTY)));
            captionsController.captionsLabel2.setBackground(new Background(new BackgroundFill(captionsController.currentBackground, CornerRadii.EMPTY, Insets.EMPTY)));

            captionsController.showCaptions();
        });

        _75Tab.setOnMouseClicked(e -> {

            for(CheckTab checkTab : checkTabs){
                checkTab.checkIcon.setVisible(false);
            }

            _75Tab.checkIcon.setVisible(true);
            captionsOptionsPane.backgroundOpacityTab.subText.setText("75%");

            captionsController.currentBackgroundOpacity = 0.75;
            captionsController.currentBackground = Color.rgb(captionsController.currentBackgroundRed, captionsController.currentBackgroundGreen, captionsController.currentBackgroundBlue, captionsController.currentBackgroundOpacity);

            captionsController.captionsLabel1.setBackground(new Background(new BackgroundFill(captionsController.currentBackground, CornerRadii.EMPTY, Insets.EMPTY)));
            captionsController.captionsLabel2.setBackground(new Background(new BackgroundFill(captionsController.currentBackground, CornerRadii.EMPTY, Insets.EMPTY)));

            captionsController.showCaptions();
        });

        _100Tab.setOnMouseClicked(e -> {

            for(CheckTab checkTab : checkTabs){
                checkTab.checkIcon.setVisible(false);
            }

            _100Tab.checkIcon.setVisible(true);
            captionsOptionsPane.backgroundOpacityTab.subText.setText("100%");

            captionsController.currentBackgroundOpacity = 1;
            captionsController.currentBackground = Color.rgb(captionsController.currentBackgroundRed, captionsController.currentBackgroundGreen, captionsController.currentBackgroundBlue, captionsController.currentBackgroundOpacity);

            captionsController.captionsLabel1.setBackground(new Background(new BackgroundFill(captionsController.currentBackground, CornerRadii.EMPTY, Insets.EMPTY)));
            captionsController.captionsLabel2.setBackground(new Background(new BackgroundFill(captionsController.currentBackground, CornerRadii.EMPTY, Insets.EMPTY)));

            captionsController.showCaptions();
        });

        captionsController.settingsController.settingsBuffer.getChildren().add(scrollPane);
    }


    public void closeBackgroundOpacityPane(){
        if(captionsController.settingsController.animating.get()) return;

        captionsController.settingsController.settingsState = SettingsState.CAPTIONS_OPTIONS_OPEN;

        captionsController.captionsOptionsPane.scrollPane.setVisible(true);
        captionsController.captionsOptionsPane.scrollPane.setMouseTransparent(false);


        Timeline clipHeightTimeline = new Timeline();
        clipHeightTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.settingsController.clip.heightProperty(), captionsController.captionsOptionsPane.scrollPane.getHeight())));


        Timeline clipWidthTimeline = new Timeline();
        clipWidthTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.settingsController.clip.widthProperty(), captionsController.captionsOptionsPane.scrollPane.getWidth())));



        TranslateTransition fontColorTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), scrollPane);
        fontColorTransition.setFromX(0);
        fontColorTransition.setToX(scrollPane.getWidth());

        TranslateTransition captionsOptionsTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), captionsController.captionsOptionsPane.scrollPane);
        captionsOptionsTransition.setFromX(-scrollPane.getWidth());
        captionsOptionsTransition.setToX(0);


        ParallelTransition parallelTransition = new ParallelTransition(clipHeightTimeline, clipWidthTimeline, fontColorTransition, captionsOptionsTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            captionsController.settingsController.animating.set(false);
            scrollPane.setVisible(false);
            scrollPane.setMouseTransparent(true);
            scrollPane.setTranslateX(0);
            captionsController.settingsController.clip.setHeight(captionsController.captionsOptionsPane.scrollPane.getPrefHeight());
        });

        parallelTransition.play();
        captionsController.settingsController.animating.set(true);
    }
}



