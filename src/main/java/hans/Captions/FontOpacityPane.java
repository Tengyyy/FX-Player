package hans.Captions;

import hans.*;
import hans.Settings.CheckTab;
import hans.Settings.SettingsController;
import hans.Settings.SettingsState;
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

public class FontOpacityPane {

    CaptionsController captionsController;
    CaptionsOptionsPane captionsOptionsPane;

    ScrollPane scrollPane = new ScrollPane();

    VBox opacityBox = new VBox();
    HBox opacityTitle = new HBox();

    StackPane opacityBackPane = new StackPane();
    Region opacityBackIcon = new Region();
    SVGPath backSVG = new SVGPath();

    Label opacityTitleLabel = new Label();

    CheckTab _25Tab, _50Tab, _75Tab, _100Tab;

    ArrayList<CheckTab> checkTabs = new ArrayList<>();

    FontOpacityPane(CaptionsController captionsController, CaptionsOptionsPane captionsOptionsPane){
        this.captionsController = captionsController;
        this.captionsOptionsPane = captionsOptionsPane;

        backSVG.setContent(App.svgMap.get(SVG.CHEVRON_LEFT));

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("settingsScroll");
        scrollPane.setPrefSize(190, 209);
        scrollPane.setMaxSize(190, 209);
        scrollPane.setContent(opacityBox);
        scrollPane.setVisible(false);
        scrollPane.setMouseTransparent(true);

        StackPane.setAlignment(scrollPane, Pos.BOTTOM_RIGHT);
        opacityBox.setAlignment(Pos.BOTTOM_CENTER);


        opacityBox.setPrefSize(185, 206);
        opacityBox.setMaxSize(185, 206);
        opacityBox.setPadding(new Insets(8, 0, 8, 0));
        opacityBox.getChildren().add(opacityTitle);

        opacityTitle.setPrefSize(185, 40);
        opacityTitle.setMaxSize(185, 40);
        opacityTitle.setPadding(new Insets(0, 10, 0, 10));
        VBox.setMargin(opacityTitle, new Insets(0, 0, 10, 0));

        opacityTitle.getStyleClass().add("settingsPaneTitle");
        opacityTitle.getChildren().addAll(opacityBackPane, opacityTitleLabel);

        opacityBackPane.setMinSize(24, 40);
        opacityBackPane.setPrefSize(24, 40);
        opacityBackPane.setMaxSize(24, 40);
        opacityBackPane.getChildren().add(opacityBackIcon);
        opacityBackPane.setCursor(Cursor.HAND);
        opacityBackPane.setOnMouseClicked((e) -> closeOpacityPane());

        opacityBackIcon.setMinSize(8, 13);
        opacityBackIcon.setPrefSize(8, 13);
        opacityBackIcon.setMaxSize(8, 13);
        opacityBackIcon.getStyleClass().add("settingsPaneIcon");
        opacityBackIcon.setShape(backSVG);

        opacityTitleLabel.setMinHeight(40);
        opacityTitleLabel.setPrefHeight(40);
        opacityTitleLabel.setMaxHeight(40);
        opacityTitleLabel.setText("Opacity");
        opacityTitleLabel.setCursor(Cursor.HAND);
        opacityTitleLabel.getStyleClass().add("settingsPaneText");
        opacityTitleLabel.setOnMouseClicked((e) -> closeOpacityPane());

        _25Tab = new CheckTab(false, "25%");
        _50Tab = new CheckTab(false, "50%");
        _75Tab = new CheckTab(false, "75%");
        _100Tab = new CheckTab(true, "100%");

        opacityBox.getChildren().addAll(_25Tab, _50Tab, _75Tab, _100Tab);
        checkTabs.add(_25Tab);
        checkTabs.add(_50Tab);
        checkTabs.add(_75Tab);
        checkTabs.add(_100Tab);

        _25Tab.setOnMouseClicked(e -> {

            for(CheckTab checkTab : checkTabs){
                checkTab.checkIcon.setVisible(false);
            }

            _25Tab.checkIcon.setVisible(true);
            captionsOptionsPane.fontOpacityTab.subText.setText("25%");

            captionsController.captionsBox.currentTextOpacity.set(0.25);


            captionsController.captionsBox.showCaptions();
        });

        _50Tab.setOnMouseClicked(e -> {

            for(CheckTab checkTab : checkTabs){
                checkTab.checkIcon.setVisible(false);
            }

            _50Tab.checkIcon.setVisible(true);
            captionsOptionsPane.fontOpacityTab.subText.setText("50%");

            captionsController.captionsBox.currentTextOpacity.set(0.5);


            captionsController.captionsBox.showCaptions();
        });

        _75Tab.setOnMouseClicked(e -> {

            for(CheckTab checkTab : checkTabs){
                checkTab.checkIcon.setVisible(false);
            }

            _75Tab.checkIcon.setVisible(true);
            captionsOptionsPane.fontOpacityTab.subText.setText("75%");

            captionsController.captionsBox.currentTextOpacity.set(0.75);



            captionsController.captionsBox.showCaptions();
        });

        _100Tab.setOnMouseClicked(e -> {

            for(CheckTab checkTab : checkTabs){
                checkTab.checkIcon.setVisible(false);
            }

            _100Tab.checkIcon.setVisible(true);
            captionsOptionsPane.fontOpacityTab.subText.setText("100%");

            captionsController.captionsBox.currentTextOpacity.set(1.0);



            captionsController.captionsBox.showCaptions();
        });

        captionsController.captionsPane.getChildren().add(scrollPane);
    }


    public void closeOpacityPane(){
        if(captionsController.animating.get()) return;

        captionsController.captionsState = CaptionsState.CAPTIONS_OPTIONS_OPEN;

        captionsController.captionsOptionsPane.scrollPane.setVisible(true);
        captionsController.captionsOptionsPane.scrollPane.setMouseTransparent(false);


        Timeline clipHeightTimeline = new Timeline();
        clipHeightTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.clip.heightProperty(), captionsController.captionsOptionsPane.scrollPane.getHeight())));


        Timeline clipWidthTimeline = new Timeline();
        clipWidthTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.clip.widthProperty(), captionsController.captionsOptionsPane.scrollPane.getWidth())));



        TranslateTransition fontColorTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), scrollPane);
        fontColorTransition.setFromX(0);
        fontColorTransition.setToX(scrollPane.getWidth());

        TranslateTransition captionsOptionsTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), captionsController.captionsOptionsPane.scrollPane);
        captionsOptionsTransition.setFromX(-scrollPane.getWidth());
        captionsOptionsTransition.setToX(0);


        ParallelTransition parallelTransition = new ParallelTransition(clipHeightTimeline, clipWidthTimeline, fontColorTransition, captionsOptionsTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            captionsController.animating.set(false);
            scrollPane.setVisible(false);
            scrollPane.setMouseTransparent(true);
            scrollPane.setTranslateX(0);
            captionsController.clip.setHeight(captionsController.captionsOptionsPane.scrollPane.getPrefHeight());
        });

        parallelTransition.play();
        captionsController.animating.set(true);
    }
}




