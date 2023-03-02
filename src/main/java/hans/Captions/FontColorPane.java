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
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

import java.util.ArrayList;

public class FontColorPane {

    CaptionsController captionsController;
    CaptionsOptionsPane captionsOptionsPane;

    ScrollPane scrollPane = new ScrollPane();

    VBox fontColorBox = new VBox();
    HBox fontColorTitle = new HBox();

    StackPane fontColorBackPane = new StackPane();
    Region fontColorBackIcon = new Region();
    SVGPath backSVG = new SVGPath();

    Label fontColorTitleLabel = new Label();

    CheckTab whiteTab, yellowTab, greenTab, cyanTab, blueTab, magentaTab, redTab, blackTab;

    ArrayList<CheckTab> checkTabs = new ArrayList<>();


    FontColorPane(CaptionsController captionsController, CaptionsOptionsPane captionsOptionsPane){
        this.captionsController = captionsController;
        this.captionsOptionsPane = captionsOptionsPane;

        backSVG.setContent(App.svgMap.get(SVG.CHEVRON_LEFT));

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("settingsScroll");
        scrollPane.setPrefSize(190, 349);
        scrollPane.setMaxSize(190, 349);
        scrollPane.setContent(fontColorBox);
        scrollPane.setVisible(false);
        scrollPane.setMouseTransparent(true);

        StackPane.setAlignment(scrollPane, Pos.BOTTOM_RIGHT);
        fontColorBox.setAlignment(Pos.BOTTOM_CENTER);


        fontColorBox.setPrefSize(185, 346);
        fontColorBox.setMaxSize(185, 346);
        fontColorBox.setPadding(new Insets(8, 0, 8, 0));
        fontColorBox.getChildren().add(fontColorTitle);

        fontColorTitle.setPrefSize(185, 40);
        fontColorTitle.setMaxSize(185, 40);
        fontColorTitle.setPadding(new Insets(0, 10, 0, 10));
        VBox.setMargin(fontColorTitle, new Insets(0, 0, 10, 0));

        fontColorTitle.getStyleClass().add("settingsPaneTitle");
        fontColorTitle.getChildren().addAll(fontColorBackPane, fontColorTitleLabel);

        fontColorBackPane.setMinSize(24, 40);
        fontColorBackPane.setPrefSize(24, 40);
        fontColorBackPane.setMaxSize(24, 40);
        fontColorBackPane.getChildren().add(fontColorBackIcon);
        fontColorBackPane.setCursor(Cursor.HAND);
        fontColorBackPane.setOnMouseClicked((e) -> closeFontColorPane());

        fontColorBackIcon.setMinSize(8, 13);
        fontColorBackIcon.setPrefSize(8, 13);
        fontColorBackIcon.setMaxSize(8, 13);
        fontColorBackIcon.getStyleClass().add("settingsPaneIcon");
        fontColorBackIcon.setShape(backSVG);

        fontColorTitleLabel.setMinHeight(40);
        fontColorTitleLabel.setPrefHeight(40);
        fontColorTitleLabel.setMaxHeight(40);
        fontColorTitleLabel.setText("Font color");
        fontColorTitleLabel.setCursor(Cursor.HAND);
        fontColorTitleLabel.getStyleClass().add("settingsPaneText");
        fontColorTitleLabel.setOnMouseClicked((e) -> closeFontColorPane());

        whiteTab = new CheckTab(true, "White");
        yellowTab = new CheckTab(false, "Yellow");
        greenTab = new CheckTab(false, "Green");
        cyanTab = new CheckTab(false, "Cyan");
        blueTab = new CheckTab(false, "Blue");
        magentaTab = new CheckTab(false, "Magenta");
        redTab = new CheckTab(false, "Red");
        blackTab = new CheckTab(false, "Black");

        fontColorBox.getChildren().addAll(whiteTab, yellowTab, greenTab, cyanTab, blueTab, magentaTab, redTab, blackTab);
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
            captionsOptionsPane.fontColorTab.subText.setText("White");

            captionsController.captionsBox.currentTextColor.set(Color.WHITE);


            captionsController.captionsBox.showCaptions();
        });

        yellowTab.setOnMouseClicked(e -> {

            for(CheckTab checkTab : checkTabs){
                checkTab.checkIcon.setVisible(false);
            }

            yellowTab.checkIcon.setVisible(true);
            captionsOptionsPane.fontColorTab.subText.setText("Yellow");

            captionsController.captionsBox.currentTextColor.set(Color.YELLOW);


            captionsController.captionsBox.showCaptions();
        });

        greenTab.setOnMouseClicked(e -> {

            for(CheckTab checkTab : checkTabs){
                checkTab.checkIcon.setVisible(false);
            }

            greenTab.checkIcon.setVisible(true);
            captionsOptionsPane.fontColorTab.subText.setText("Green");

            captionsController.captionsBox.currentTextColor.set(Color.LIME);


            captionsController.captionsBox.showCaptions();
        });

        cyanTab.setOnMouseClicked(e -> {

            for(CheckTab checkTab : checkTabs){
                checkTab.checkIcon.setVisible(false);
            }

            cyanTab.checkIcon.setVisible(true);
            captionsOptionsPane.fontColorTab.subText.setText("Cyan");

            captionsController.captionsBox.currentTextColor.set(Color.CYAN);


            captionsController.captionsBox.showCaptions();
        });

        blueTab.setOnMouseClicked(e -> {

            for(CheckTab checkTab : checkTabs){
                checkTab.checkIcon.setVisible(false);
            }

            blueTab.checkIcon.setVisible(true);
            captionsOptionsPane.fontColorTab.subText.setText("Blue");

            captionsController.captionsBox.currentTextColor.set(Color.BLUE);
;

            captionsController.captionsBox.showCaptions();
        });

        magentaTab.setOnMouseClicked(e -> {

            for(CheckTab checkTab : checkTabs){
                checkTab.checkIcon.setVisible(false);
            }

            magentaTab.checkIcon.setVisible(true);
            captionsOptionsPane.fontColorTab.subText.setText("Magenta");

            captionsController.captionsBox.currentTextColor.set(Color.MAGENTA);

            captionsController.captionsBox.showCaptions();
        });

        redTab.setOnMouseClicked(e -> {

            for(CheckTab checkTab : checkTabs){
                checkTab.checkIcon.setVisible(false);
            }

            redTab.checkIcon.setVisible(true);
            captionsOptionsPane.fontColorTab.subText.setText("Red");

            captionsController.captionsBox.currentTextColor.set(Color.RED);

            captionsController.captionsBox.showCaptions();
        });

        blackTab.setOnMouseClicked(e -> {

            for(CheckTab checkTab : checkTabs){
                checkTab.checkIcon.setVisible(false);
            }

            blackTab.checkIcon.setVisible(true);
            captionsOptionsPane.fontColorTab.subText.setText("Black");

            captionsController.captionsBox.currentTextColor.set(Color.BLACK);

            captionsController.captionsBox.showCaptions();
        });

        captionsController.captionsPane.getChildren().add(scrollPane);
    }


    public void closeFontColorPane(){
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
