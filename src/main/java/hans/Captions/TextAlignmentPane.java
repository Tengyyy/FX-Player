package hans.Captions;

import hans.App;
import hans.Settings.CheckTab;
import hans.Settings.SettingsController;
import hans.Settings.SettingsState;
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

import java.util.ArrayList;

public class TextAlignmentPane {

    CaptionsController captionsController;
    CaptionsOptionsPane captionsOptionsPane;

    ScrollPane scrollPane = new ScrollPane();

    VBox textAlignmentBox = new VBox();
    HBox textAlignmentTitle = new HBox();

    StackPane textAlignmentBackPane = new StackPane();
    Region textAlignmentBackIcon = new Region();
    SVGPath backSVG = new SVGPath();

    Label textAlignmentTitleLabel = new Label();

    CheckTab leftTab, centerTab, rightTab;

    ArrayList<CheckTab> checkTabs = new ArrayList<>();

    TextAlignmentPane(CaptionsController captionsController, CaptionsOptionsPane captionsOptionsPane){
        this.captionsController = captionsController;
        this.captionsOptionsPane = captionsOptionsPane;

        backSVG.setContent(App.svgMap.get(SVG.CHEVRON_LEFT));

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("settingsScroll");
        scrollPane.setPrefSize(190, 174);
        scrollPane.setMaxSize(190, 174);
        scrollPane.setContent(textAlignmentBox);
        scrollPane.setVisible(false);
        scrollPane.setMouseTransparent(true);

        StackPane.setAlignment(scrollPane, Pos.BOTTOM_RIGHT);
        textAlignmentBox.setAlignment(Pos.BOTTOM_CENTER);


        textAlignmentBox.setMinSize(185, 171);
        textAlignmentBox.setPrefSize(185, 171);
        textAlignmentBox.setMaxSize(185, 171);
        textAlignmentBox.setPadding(new Insets(8, 0, 8, 0));
        textAlignmentBox.getChildren().add(textAlignmentTitle);

        textAlignmentTitle.setMinSize(185, 40);
        textAlignmentTitle.setPrefSize(185, 40);
        textAlignmentTitle.setMaxSize(185, 40);
        textAlignmentTitle.setPadding(new Insets(0, 10, 0, 10));
        VBox.setMargin(textAlignmentTitle, new Insets(0, 0, 10, 0));

        textAlignmentTitle.getStyleClass().add("settingsPaneTitle");
        textAlignmentTitle.getChildren().addAll(textAlignmentBackPane, textAlignmentTitleLabel);

        textAlignmentBackPane.setMinSize(24, 40);
        textAlignmentBackPane.setPrefSize(24, 40);
        textAlignmentBackPane.setMaxSize(24, 40);
        textAlignmentBackPane.getChildren().add(textAlignmentBackIcon);
        textAlignmentBackPane.setCursor(Cursor.HAND);
        textAlignmentBackPane.setOnMouseClicked((e) -> closeTextAlignmentPane());

        textAlignmentBackIcon.setMinSize(8, 13);
        textAlignmentBackIcon.setPrefSize(8, 13);
        textAlignmentBackIcon.setMaxSize(8, 13);
        textAlignmentBackIcon.getStyleClass().add("settingsPaneIcon");
        textAlignmentBackIcon.setShape(backSVG);

        textAlignmentTitleLabel.setMinHeight(40);
        textAlignmentTitleLabel.setPrefHeight(40);
        textAlignmentTitleLabel.setMaxHeight(40);
        textAlignmentTitleLabel.setText("Text alignment");
        textAlignmentTitleLabel.setCursor(Cursor.HAND);
        textAlignmentTitleLabel.getStyleClass().add("settingsPaneText");
        textAlignmentTitleLabel.setOnMouseClicked((e) -> closeTextAlignmentPane());

        leftTab = new CheckTab(false, "Left");
        centerTab = new CheckTab(true, "Center");
        rightTab = new CheckTab(false, "Right");


        textAlignmentBox.getChildren().addAll(leftTab, centerTab, rightTab);
        checkTabs.add(leftTab);
        checkTabs.add(centerTab);
        checkTabs.add(rightTab);

        leftTab.setOnMouseClicked(e -> {

            for(CheckTab checkTab : checkTabs){
                checkTab.checkIcon.setVisible(false);
            }

            leftTab.checkIcon.setVisible(true);
            captionsOptionsPane.textAlignmentTab.subText.setText("Left");

            captionsController.captionsBox.currentTextAlignment = Pos.CENTER_LEFT;
            captionsController.captionsBox.captionsContainer.setAlignment(captionsController.captionsBox.currentTextAlignment);

            captionsController.captionsBox.showCaptions();
        });

        centerTab.setOnMouseClicked(e -> {

            for(CheckTab checkTab : checkTabs){
                checkTab.checkIcon.setVisible(false);
            }

            centerTab.checkIcon.setVisible(true);
            captionsOptionsPane.textAlignmentTab.subText.setText("Center");

            captionsController.captionsBox.currentTextAlignment = Pos.CENTER;
            captionsController.captionsBox.captionsContainer.setAlignment(captionsController.captionsBox.currentTextAlignment);

            captionsController.captionsBox.showCaptions();
        });

        rightTab.setOnMouseClicked(e -> {

            for(CheckTab checkTab : checkTabs){
                checkTab.checkIcon.setVisible(false);
            }

            rightTab.checkIcon.setVisible(true);
            captionsOptionsPane.textAlignmentTab.subText.setText("Right");

            captionsController.captionsBox.currentTextAlignment = Pos.CENTER_RIGHT;
            captionsController.captionsBox.captionsContainer.setAlignment(captionsController.captionsBox.currentTextAlignment);

            captionsController.captionsBox.showCaptions();
        });


        captionsController.captionsPane.getChildren().add(scrollPane);
    }


    public void closeTextAlignmentPane(){
        if(captionsController.animating.get()) return;

        captionsController.captionsState = CaptionsState.CAPTIONS_OPTIONS_OPEN;

        captionsController.captionsOptionsPane.scrollPane.setVisible(true);
        captionsController.captionsOptionsPane.scrollPane.setMouseTransparent(false);


        Timeline clipHeightTimeline = new Timeline();
        clipHeightTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.clip.heightProperty(), captionsController.captionsOptionsPane.scrollPane.getHeight())));


        Timeline clipWidthTimeline = new Timeline();
        clipWidthTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.clip.widthProperty(), captionsController.captionsOptionsPane.scrollPane.getWidth())));



        TranslateTransition textAlignmentTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), scrollPane);
        textAlignmentTransition.setFromX(0);
        textAlignmentTransition.setToX(scrollPane.getWidth());

        TranslateTransition captionsOptionsTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), captionsController.captionsOptionsPane.scrollPane);
        captionsOptionsTransition.setFromX(-scrollPane.getWidth());
        captionsOptionsTransition.setToX(0);


        ParallelTransition parallelTransition = new ParallelTransition(clipHeightTimeline, clipWidthTimeline, textAlignmentTransition, captionsOptionsTransition);
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




