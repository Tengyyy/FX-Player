package hans;

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
        scrollPane.setPrefSize(190, 314);
        scrollPane.setMaxSize(190, 314);
        scrollPane.setContent(fontFamilyBox);
        scrollPane.setVisible(false);
        scrollPane.setMouseTransparent(true);

        StackPane.setAlignment(scrollPane, Pos.BOTTOM_RIGHT);
        fontFamilyBox.setAlignment(Pos.BOTTOM_CENTER);


        fontFamilyBox.setMinSize(185, 311);
        fontFamilyBox.setPrefSize(185, 311);
        fontFamilyBox.setMaxSize(185, 311);
        fontFamilyBox.setPadding(new Insets(8, 0, 8, 0));
        fontFamilyBox.getChildren().add(fontFamilyTitle);

        fontFamilyTitle.setMinSize(185, 40);
        fontFamilyTitle.setPrefSize(185, 40);
        fontFamilyTitle.setMaxSize(185, 40);
        fontFamilyTitle.setPadding(new Insets(0, 10, 0, 10));
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

            captionsController.currentFontFamily = "\"Roboto\"";

            captionsController.captionsLabel1.setStyle("-fx-font-family: " + captionsController.currentFontFamily + "; -fx-font-size: " + captionsController.mediaWidthMultiplier.multiply(captionsController.currentFontSize).get());
            captionsController.captionsLabel2.setStyle("-fx-font-family: " + captionsController.currentFontFamily + "; -fx-font-size: " + captionsController.mediaWidthMultiplier.multiply(captionsController.currentFontSize).get());

            captionsController.showCaptions();
        });

        sansSerifMediumTab.setOnMouseClicked(e -> {

            for(CheckTab checkTab : checkTabs){
                checkTab.checkIcon.setVisible(false);
            }

            sansSerifMediumTab.checkIcon.setVisible(true);
            captionsOptionsPane.fontFamilyTab.subText.setText("Sans-Serif Medium");

            captionsController.currentFontFamily = "\"Roboto Medium\"";

            captionsController.captionsLabel1.setStyle("-fx-font-family: " + captionsController.currentFontFamily + "; -fx-font-size: " + captionsController.mediaWidthMultiplier.multiply(captionsController.currentFontSize).get());
            captionsController.captionsLabel2.setStyle("-fx-font-family: " + captionsController.currentFontFamily + "; -fx-font-size: " + captionsController.mediaWidthMultiplier.multiply(captionsController.currentFontSize).get());

            captionsController.showCaptions();
        });

        sansSerifBoldTab.setOnMouseClicked(e -> {

            for(CheckTab checkTab : checkTabs){
                checkTab.checkIcon.setVisible(false);
            }

            sansSerifBoldTab.checkIcon.setVisible(true);
            captionsOptionsPane.fontFamilyTab.subText.setText("Sans-Serif Bold");

            captionsController.currentFontFamily = "\"Roboto Bold\"";

            captionsController.captionsLabel1.setStyle("-fx-font-family: " + captionsController.currentFontFamily + "; -fx-font-size: " + captionsController.mediaWidthMultiplier.multiply(captionsController.currentFontSize).get());
            captionsController.captionsLabel2.setStyle("-fx-font-family: " + captionsController.currentFontFamily + "; -fx-font-size: " + captionsController.mediaWidthMultiplier.multiply(captionsController.currentFontSize).get());

            captionsController.showCaptions();
        });

        serifTab.setOnMouseClicked(e -> {

            for(CheckTab checkTab : checkTabs){
                checkTab.checkIcon.setVisible(false);
            }

            serifTab.checkIcon.setVisible(true);
            captionsOptionsPane.fontFamilyTab.subText.setText("Serif");

            captionsController.currentFontFamily = "\"EB Garamond Medium\"";

            captionsController.captionsLabel1.setStyle("-fx-font-family: " + captionsController.currentFontFamily + "; -fx-font-size: " + captionsController.mediaWidthMultiplier.multiply(captionsController.currentFontSize).get());
            captionsController.captionsLabel2.setStyle("-fx-font-family: " + captionsController.currentFontFamily + "; -fx-font-size: " + captionsController.mediaWidthMultiplier.multiply(captionsController.currentFontSize).get());

            captionsController.showCaptions();
        });


        casualTab.setOnMouseClicked(e -> {

            for(CheckTab checkTab : checkTabs){
                checkTab.checkIcon.setVisible(false);
            }

            casualTab.checkIcon.setVisible(true);
            captionsOptionsPane.fontFamilyTab.subText.setText("Casual");

            captionsController.currentFontFamily = "\"Comic Neue Bold\"";

            captionsController.captionsLabel1.setStyle("-fx-font-family: " + captionsController.currentFontFamily + "; -fx-font-size: " + captionsController.mediaWidthMultiplier.multiply(captionsController.currentFontSize).get());
            captionsController.captionsLabel2.setStyle("-fx-font-family: " + captionsController.currentFontFamily + "; -fx-font-size: " + captionsController.mediaWidthMultiplier.multiply(captionsController.currentFontSize).get());

            captionsController.showCaptions();
        });


        cursiveTab.setOnMouseClicked(e -> {

            for(CheckTab checkTab : checkTabs){
                checkTab.checkIcon.setVisible(false);
            }

            cursiveTab.checkIcon.setVisible(true);
            captionsOptionsPane.fontFamilyTab.subText.setText("Cursive");

            captionsController.currentFontFamily = "\"Kalam Bold\"";

            captionsController.captionsLabel1.setStyle("-fx-font-family: " + captionsController.currentFontFamily + "; -fx-font-size: " + captionsController.mediaWidthMultiplier.multiply(captionsController.currentFontSize).get());
            captionsController.captionsLabel2.setStyle("-fx-font-family: " + captionsController.currentFontFamily + "; -fx-font-size: " + captionsController.mediaWidthMultiplier.multiply(captionsController.currentFontSize).get());

            captionsController.showCaptions();
        });

        smallCapitalsTab.setOnMouseClicked(e -> {

            for(CheckTab checkTab : checkTabs){
                checkTab.checkIcon.setVisible(false);
            }

            smallCapitalsTab.checkIcon.setVisible(true);
            captionsOptionsPane.fontFamilyTab.subText.setText("Small Capitals");

            captionsController.currentFontFamily = "\"Alegreya Sans SC Medium\"";

            captionsController.captionsLabel1.setStyle("-fx-font-family: " + captionsController.currentFontFamily + "; -fx-font-size: " + captionsController.mediaWidthMultiplier.multiply(captionsController.currentFontSize).get());
            captionsController.captionsLabel2.setStyle("-fx-font-family: " + captionsController.currentFontFamily + "; -fx-font-size: " + captionsController.mediaWidthMultiplier.multiply(captionsController.currentFontSize).get());

            captionsController.showCaptions();
        });


        captionsController.settingsController.settingsBuffer.getChildren().add(scrollPane);
    }


    public void closeFontFamilyPane(){
        if(captionsController.settingsController.animating.get()) return;

        captionsController.settingsController.settingsState = SettingsState.CAPTIONS_OPTIONS_OPEN;

        captionsController.captionsOptionsPane.scrollPane.setVisible(true);
        captionsController.captionsOptionsPane.scrollPane.setMouseTransparent(false);


        Timeline clipHeightTimeline = new Timeline();
        clipHeightTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.settingsController.clip.heightProperty(), captionsController.captionsOptionsPane.scrollPane.getHeight())));


        Timeline clipWidthTimeline = new Timeline();
        clipWidthTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.settingsController.clip.widthProperty(), captionsController.captionsOptionsPane.scrollPane.getWidth())));



        TranslateTransition fontFamilyTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), scrollPane);
        fontFamilyTransition.setFromX(0);
        fontFamilyTransition.setToX(scrollPane.getWidth());

        TranslateTransition captionsOptionsTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), captionsController.captionsOptionsPane.scrollPane);
        captionsOptionsTransition.setFromX(-scrollPane.getWidth());
        captionsOptionsTransition.setToX(0);


        ParallelTransition parallelTransition = new ParallelTransition(clipHeightTimeline, clipWidthTimeline, fontFamilyTransition, captionsOptionsTransition);
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
