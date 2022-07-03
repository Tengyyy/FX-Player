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

public class BackgroundColorPane {

    CaptionsController captionsController;
    CaptionsOptionsPane captionsOptionsPane;

    ScrollPane scrollPane = new ScrollPane();

    VBox backgroundColorBox = new VBox();
    HBox backgroundColorTitle = new HBox();

    StackPane backgroundColorBackPane = new StackPane();
    Region backgroundColorBackIcon = new Region();
    SVGPath backSVG = new SVGPath();

    Label backgroundColorTitleLabel = new Label();

    CheckTab whiteTab, yellowTab, greenTab, cyanTab, blueTab, magentaTab, redTab, blackTab;

    ArrayList<CheckTab> checkTabs = new ArrayList<>();

    BackgroundColorPane(CaptionsController captionsController, CaptionsOptionsPane captionsOptionsPane){
        this.captionsController = captionsController;
        this.captionsOptionsPane = captionsOptionsPane;

        backSVG.setContent(App.svgMap.get(SVG.CHEVRON_LEFT));

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("settingsScroll");
        scrollPane.setPrefSize(190, 349);
        scrollPane.setMaxSize(190, 349);
        scrollPane.setContent(backgroundColorBox);
        scrollPane.setVisible(false);
        scrollPane.setMouseTransparent(true);

        StackPane.setAlignment(scrollPane, Pos.BOTTOM_RIGHT);
        backgroundColorBox.setAlignment(Pos.BOTTOM_CENTER);


        backgroundColorBox.setMinSize(185, 346);
        backgroundColorBox.setPrefSize(185, 346);
        backgroundColorBox.setMaxSize(185, 346);
        backgroundColorBox.setPadding(new Insets(8, 0, 8, 0));
        backgroundColorBox.getChildren().add(backgroundColorTitle);

        backgroundColorTitle.setMinSize(185, 40);
        backgroundColorTitle.setPrefSize(185, 40);
        backgroundColorTitle.setMaxSize(185, 40);
        backgroundColorTitle.setPadding(new Insets(0, 10, 0, 10));
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
            captionsOptionsPane.backgroundColorTab.subText.setText("White");

            captionsController.currentBackgroundRed = 255;
            captionsController.currentBackgroundGreen = 255;
            captionsController.currentBackgroundBlue = 255;

            captionsController.currentBackground = Color.rgb(captionsController.currentBackgroundRed, captionsController.currentBackgroundGreen, captionsController.currentBackgroundBlue, captionsController.currentBackgroundOpacity);

            captionsController.captionsLabel1.setBackground(new Background(new BackgroundFill(captionsController.currentBackground, CornerRadii.EMPTY, Insets.EMPTY)));
            captionsController.captionsLabel2.setBackground(new Background(new BackgroundFill(captionsController.currentBackground, CornerRadii.EMPTY, Insets.EMPTY)));

            captionsController.showCaptions();
        });

        yellowTab.setOnMouseClicked(e -> {

            for(CheckTab checkTab : checkTabs){
                checkTab.checkIcon.setVisible(false);
            }

            yellowTab.checkIcon.setVisible(true);
            captionsOptionsPane.backgroundColorTab.subText.setText("Yellow");

            captionsController.currentBackgroundRed = 255;
            captionsController.currentBackgroundGreen = 255;
            captionsController.currentBackgroundBlue = 0;

            captionsController.currentBackground = Color.rgb(captionsController.currentBackgroundRed, captionsController.currentBackgroundGreen, captionsController.currentBackgroundBlue, captionsController.currentBackgroundOpacity);

            captionsController.captionsLabel1.setBackground(new Background(new BackgroundFill(captionsController.currentBackground, CornerRadii.EMPTY, Insets.EMPTY)));
            captionsController.captionsLabel2.setBackground(new Background(new BackgroundFill(captionsController.currentBackground, CornerRadii.EMPTY, Insets.EMPTY)));

            captionsController.showCaptions();
        });

        greenTab.setOnMouseClicked(e -> {

            for(CheckTab checkTab : checkTabs){
                checkTab.checkIcon.setVisible(false);
            }

            greenTab.checkIcon.setVisible(true);
            captionsOptionsPane.backgroundColorTab.subText.setText("Green");

            captionsController.currentBackgroundRed = 0;
            captionsController.currentBackgroundGreen = 255;
            captionsController.currentBackgroundBlue = 0;

            captionsController.currentBackground = Color.rgb(captionsController.currentBackgroundRed, captionsController.currentBackgroundGreen, captionsController.currentBackgroundBlue, captionsController.currentBackgroundOpacity);

            captionsController.captionsLabel1.setBackground(new Background(new BackgroundFill(captionsController.currentBackground, CornerRadii.EMPTY, Insets.EMPTY)));
            captionsController.captionsLabel2.setBackground(new Background(new BackgroundFill(captionsController.currentBackground, CornerRadii.EMPTY, Insets.EMPTY)));

            captionsController.showCaptions();
        });

        cyanTab.setOnMouseClicked(e -> {

            for(CheckTab checkTab : checkTabs){
                checkTab.checkIcon.setVisible(false);
            }

            cyanTab.checkIcon.setVisible(true);
            captionsOptionsPane.backgroundColorTab.subText.setText("Cyan");

            captionsController.currentBackgroundRed = 0;
            captionsController.currentBackgroundGreen = 255;
            captionsController.currentBackgroundBlue = 255;

            captionsController.currentBackground = Color.rgb(captionsController.currentBackgroundRed, captionsController.currentBackgroundGreen, captionsController.currentBackgroundBlue, captionsController.currentBackgroundOpacity);

            captionsController.captionsLabel1.setBackground(new Background(new BackgroundFill(captionsController.currentBackground, CornerRadii.EMPTY, Insets.EMPTY)));
            captionsController.captionsLabel2.setBackground(new Background(new BackgroundFill(captionsController.currentBackground, CornerRadii.EMPTY, Insets.EMPTY)));

            captionsController.showCaptions();
        });

        blueTab.setOnMouseClicked(e -> {

            for(CheckTab checkTab : checkTabs){
                checkTab.checkIcon.setVisible(false);
            }

            blueTab.checkIcon.setVisible(true);
            captionsOptionsPane.backgroundColorTab.subText.setText("Blue");

            captionsController.currentBackgroundRed = 0;
            captionsController.currentBackgroundGreen = 0;
            captionsController.currentBackgroundBlue = 255;

            captionsController.currentBackground = Color.rgb(captionsController.currentBackgroundRed, captionsController.currentBackgroundGreen, captionsController.currentBackgroundBlue, captionsController.currentBackgroundOpacity);

            captionsController.captionsLabel1.setBackground(new Background(new BackgroundFill(captionsController.currentBackground, CornerRadii.EMPTY, Insets.EMPTY)));
            captionsController.captionsLabel2.setBackground(new Background(new BackgroundFill(captionsController.currentBackground, CornerRadii.EMPTY, Insets.EMPTY)));

            captionsController.showCaptions();
        });

        magentaTab.setOnMouseClicked(e -> {

            for(CheckTab checkTab : checkTabs){
                checkTab.checkIcon.setVisible(false);
            }

            magentaTab.checkIcon.setVisible(true);
            captionsOptionsPane.backgroundColorTab.subText.setText("Magenta");

            captionsController.currentBackgroundRed = 255;
            captionsController.currentBackgroundGreen = 0;
            captionsController.currentBackgroundBlue = 255;

            captionsController.currentBackground = Color.rgb(captionsController.currentBackgroundRed, captionsController.currentBackgroundGreen, captionsController.currentBackgroundBlue, captionsController.currentBackgroundOpacity);

            captionsController.captionsLabel1.setBackground(new Background(new BackgroundFill(captionsController.currentBackground, CornerRadii.EMPTY, Insets.EMPTY)));
            captionsController.captionsLabel2.setBackground(new Background(new BackgroundFill(captionsController.currentBackground, CornerRadii.EMPTY, Insets.EMPTY)));

            captionsController.showCaptions();
        });

        redTab.setOnMouseClicked(e -> {

            for(CheckTab checkTab : checkTabs){
                checkTab.checkIcon.setVisible(false);
            }

            redTab.checkIcon.setVisible(true);
            captionsOptionsPane.backgroundColorTab.subText.setText("Red");

            captionsController.currentBackgroundRed = 255;
            captionsController.currentBackgroundGreen = 0;
            captionsController.currentBackgroundBlue = 0;

            captionsController.currentBackground = Color.rgb(captionsController.currentBackgroundRed, captionsController.currentBackgroundGreen, captionsController.currentBackgroundBlue, captionsController.currentBackgroundOpacity);

            captionsController.captionsLabel1.setBackground(new Background(new BackgroundFill(captionsController.currentBackground, CornerRadii.EMPTY, Insets.EMPTY)));
            captionsController.captionsLabel2.setBackground(new Background(new BackgroundFill(captionsController.currentBackground, CornerRadii.EMPTY, Insets.EMPTY)));

            captionsController.showCaptions();
        });

        blackTab.setOnMouseClicked(e -> {

            for(CheckTab checkTab : checkTabs){
                checkTab.checkIcon.setVisible(false);
            }

            blackTab.checkIcon.setVisible(true);
            captionsOptionsPane.backgroundColorTab.subText.setText("Black");

            captionsController.currentBackgroundRed = 0;
            captionsController.currentBackgroundGreen = 0;
            captionsController.currentBackgroundBlue = 0;

            captionsController.currentBackground = Color.rgb(captionsController.currentBackgroundRed, captionsController.currentBackgroundGreen, captionsController.currentBackgroundBlue, captionsController.currentBackgroundOpacity);

            captionsController.captionsLabel1.setBackground(new Background(new BackgroundFill(captionsController.currentBackground, CornerRadii.EMPTY, Insets.EMPTY)));
            captionsController.captionsLabel2.setBackground(new Background(new BackgroundFill(captionsController.currentBackground, CornerRadii.EMPTY, Insets.EMPTY)));

            captionsController.showCaptions();
        });

        captionsController.settingsController.settingsBuffer.getChildren().add(scrollPane);
    }


    public void closeBackgroundColorPane(){
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


