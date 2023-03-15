package hans.Captions;

import hans.*;
import hans.Settings.CheckTab;
import hans.Settings.SettingsController;
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
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.util.ArrayList;

public class FontFamilyPane {

    CaptionsController captionsController;
    CaptionsOptionsPane captionsOptionsPane;

    ScrollPane scrollPane = new ScrollPane();

    VBox fontFamilyBox = new VBox();
    HBox fontFamilyTitle = new HBox();

    StackPane fontFamilyBackPane = new StackPane();
    Region fontFamilyBackIcon = new Region();
    SVGPath backSVG = new SVGPath();

    Label fontFamilyTitleLabel = new Label();

    CheckTab sansSerifRegularTab, sansSerifMediumTab, sansSerifBoldTab, serifTab, casualTab, cursiveTab, smallCapitalsTab;

    ArrayList<CheckTab> checkTabs = new ArrayList<>();

    FontFamilyPane(CaptionsController captionsController, CaptionsOptionsPane captionsOptionsPane){
        this.captionsController = captionsController;
        this.captionsOptionsPane = captionsOptionsPane;

        backSVG.setContent(App.svgMap.get(SVG.CHEVRON_LEFT));

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("settingsScroll");
        scrollPane.setPrefSize(200, 314);
        scrollPane.setMaxSize(200, 314);
        scrollPane.setContent(fontFamilyBox);
        scrollPane.setVisible(false);
        scrollPane.setMouseTransparent(true);
        scrollPane.setFitToWidth(true);

        StackPane.setAlignment(scrollPane, Pos.BOTTOM_RIGHT);
        fontFamilyBox.setAlignment(Pos.BOTTOM_CENTER);


        fontFamilyBox.setPrefSize(200, 311);
        fontFamilyBox.setMaxSize(200, 311);
        fontFamilyBox.setPadding(new Insets(0, 0, 8, 0));
        fontFamilyBox.getChildren().add(fontFamilyTitle);
        fontFamilyBox.setFillWidth(true);

        fontFamilyTitle.setPrefSize(200, 48);
        fontFamilyTitle.setMaxSize(200, 48);
        fontFamilyTitle.setPadding(new Insets(0, 10, 0, 10));
        fontFamilyTitle.setAlignment(Pos.CENTER_LEFT);
        VBox.setMargin(fontFamilyTitle, new Insets(0, 0, 10, 0));

        fontFamilyTitle.getStyleClass().add("settingsPaneTitle");
        fontFamilyTitle.getChildren().addAll(fontFamilyBackPane, fontFamilyTitleLabel);

        fontFamilyBackPane.setMinSize(24, 40);
        fontFamilyBackPane.setPrefSize(24, 40);
        fontFamilyBackPane.setMaxSize(24, 40);
        fontFamilyBackPane.getChildren().add(fontFamilyBackIcon);
        fontFamilyBackPane.setCursor(Cursor.HAND);
        fontFamilyBackPane.setOnMouseClicked((e) -> closeFontFamilyPane());

        fontFamilyBackIcon.setMinSize(8, 13);
        fontFamilyBackIcon.setPrefSize(8, 13);
        fontFamilyBackIcon.setMaxSize(8, 13);
        fontFamilyBackIcon.getStyleClass().add("settingsPaneIcon");
        fontFamilyBackIcon.setShape(backSVG);

        fontFamilyTitleLabel.setMinHeight(40);
        fontFamilyTitleLabel.setPrefHeight(40);
        fontFamilyTitleLabel.setMaxHeight(40);
        fontFamilyTitleLabel.setText("Font family");
        fontFamilyTitleLabel.setCursor(Cursor.HAND);
        fontFamilyTitleLabel.getStyleClass().add("settingsPaneText");
        fontFamilyTitleLabel.setOnMouseClicked((e) -> closeFontFamilyPane());

        sansSerifRegularTab = new CheckTab(false, "Sans-Serif Regular");
        sansSerifMediumTab = new CheckTab(true, "Sans-Serif Medium");
        sansSerifBoldTab = new CheckTab(false, "Sans-Serif Bold");
        serifTab = new CheckTab(false, "Serif");
        casualTab = new CheckTab(false, "Casual");
        cursiveTab = new CheckTab(false, "Cursive");
        smallCapitalsTab = new CheckTab(false, "Small Capitals");

        fontFamilyBox.getChildren().addAll(sansSerifRegularTab, sansSerifMediumTab, sansSerifBoldTab, serifTab, casualTab, cursiveTab, smallCapitalsTab);
        checkTabs.add(sansSerifRegularTab);
        checkTabs.add(sansSerifMediumTab);
        checkTabs.add(sansSerifBoldTab);
        checkTabs.add(serifTab);
        checkTabs.add(casualTab);
        checkTabs.add(cursiveTab);
        checkTabs.add(smallCapitalsTab);

        sansSerifRegularTab.setOnMouseClicked(e -> {

            for(CheckTab checkTab : checkTabs){
                checkTab.checkIcon.setVisible(false);
            }

            sansSerifRegularTab.checkIcon.setVisible(true);
            captionsOptionsPane.fontFamilyTab.subText.setText("Sans-Serif Regular");

            captionsController.captionsBox.currentFontFamily.set("\"Roboto\"");
            captionsController.captionsBox.showCaptions();
        });

        sansSerifMediumTab.setOnMouseClicked(e -> {

            for(CheckTab checkTab : checkTabs){
                checkTab.checkIcon.setVisible(false);
            }

            sansSerifMediumTab.checkIcon.setVisible(true);
            captionsOptionsPane.fontFamilyTab.subText.setText("Sans-Serif Medium");

            captionsController.captionsBox.currentFontFamily.set("\"Roboto Medium\"");

            captionsController.captionsBox.showCaptions();
        });

        sansSerifBoldTab.setOnMouseClicked(e -> {

            for(CheckTab checkTab : checkTabs){
                checkTab.checkIcon.setVisible(false);
            }

            sansSerifBoldTab.checkIcon.setVisible(true);
            captionsOptionsPane.fontFamilyTab.subText.setText("Sans-Serif Bold");

            captionsController.captionsBox.currentFontFamily.set("\"Roboto Bold\"");


            captionsController.captionsBox.showCaptions();
        });

        serifTab.setOnMouseClicked(e -> {

            for(CheckTab checkTab : checkTabs){
                checkTab.checkIcon.setVisible(false);
            }

            serifTab.checkIcon.setVisible(true);
            captionsOptionsPane.fontFamilyTab.subText.setText("Serif");

            captionsController.captionsBox.currentFontFamily.set("\"EB Garamond Medium\"");

            captionsController.captionsBox.showCaptions();
        });


        casualTab.setOnMouseClicked(e -> {

            for(CheckTab checkTab : checkTabs){
                checkTab.checkIcon.setVisible(false);
            }

            casualTab.checkIcon.setVisible(true);
            captionsOptionsPane.fontFamilyTab.subText.setText("Casual");

            captionsController.captionsBox.currentFontFamily.set("\"Comic Neue Bold\"");

            captionsController.captionsBox.showCaptions();
        });


        cursiveTab.setOnMouseClicked(e -> {

            for(CheckTab checkTab : checkTabs){
                checkTab.checkIcon.setVisible(false);
            }

            cursiveTab.checkIcon.setVisible(true);
            captionsOptionsPane.fontFamilyTab.subText.setText("Cursive");

            captionsController.captionsBox.currentFontFamily.set("\"Kalam Bold\"");


            captionsController.captionsBox.showCaptions();
        });

        smallCapitalsTab.setOnMouseClicked(e -> {

            for(CheckTab checkTab : checkTabs){
                checkTab.checkIcon.setVisible(false);
            }

            smallCapitalsTab.checkIcon.setVisible(true);
            captionsOptionsPane.fontFamilyTab.subText.setText("Small Capitals");

            captionsController.captionsBox.currentFontFamily.set("\"Alegreya Sans SC Medium\"");


            captionsController.captionsBox.showCaptions();
        });


        captionsController.captionsPane.getChildren().add(scrollPane);
    }


    public void closeFontFamilyPane(){
        if(captionsController.animating.get()) return;

        captionsController.captionsState = CaptionsState.CAPTIONS_OPTIONS_OPEN;

        captionsController.captionsOptionsPane.scrollPane.setVisible(true);
        captionsController.captionsOptionsPane.scrollPane.setMouseTransparent(false);


        Timeline clipHeightTimeline = new Timeline();
        clipHeightTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.clip.heightProperty(), captionsController.captionsOptionsPane.scrollPane.getHeight())));


        Timeline clipWidthTimeline = new Timeline();
        clipWidthTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.clip.widthProperty(), captionsController.captionsOptionsPane.scrollPane.getWidth())));



        TranslateTransition fontFamilyTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), scrollPane);
        fontFamilyTransition.setFromX(0);
        fontFamilyTransition.setToX(scrollPane.getWidth());

        TranslateTransition captionsOptionsTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), captionsController.captionsOptionsPane.scrollPane);
        captionsOptionsTransition.setFromX(-scrollPane.getWidth());
        captionsOptionsTransition.setToX(0);


        ParallelTransition parallelTransition = new ParallelTransition(clipHeightTimeline, clipWidthTimeline, fontFamilyTransition, captionsOptionsTransition);
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
