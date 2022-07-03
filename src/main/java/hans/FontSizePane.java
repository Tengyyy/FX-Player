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
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;
import org.jcodec.common.Tuple;

import java.util.ArrayList;

public class FontSizePane {

    CaptionsController captionsController;
    CaptionsOptionsPane captionsOptionsPane;

    ScrollPane scrollPane = new ScrollPane();

    VBox fontSizeBox = new VBox();
    HBox fontSizeTitle = new HBox();

    StackPane fontSizeBackPane = new StackPane();
    Region fontSizeBackIcon = new Region();
    SVGPath backSVG = new SVGPath();

    Label fontSizeTitleLabel = new Label();

    CheckTab _50Tab, _75Tab, _100Tab, _150Tab, _200Tab, _300Tab, _400Tab;

    ArrayList<CheckTab> checkTabs = new ArrayList<>();


    FontSizePane(CaptionsController captionsController, CaptionsOptionsPane captionsOptionsPane){
        this.captionsController = captionsController;
        this.captionsOptionsPane = captionsOptionsPane;

        backSVG.setContent(App.svgMap.get(SVG.CHEVRON_LEFT));

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("settingsScroll");
        scrollPane.setPrefSize(190, 314);
        scrollPane.setMaxSize(190, 314);
        scrollPane.setContent(fontSizeBox);
        scrollPane.setVisible(false);
        scrollPane.setMouseTransparent(true);

        StackPane.setAlignment(scrollPane, Pos.BOTTOM_RIGHT);
        fontSizeBox.setAlignment(Pos.BOTTOM_CENTER);


        fontSizeBox.setMinSize(185, 311);
        fontSizeBox.setPrefSize(185, 311);
        fontSizeBox.setMaxSize(185, 311);
        fontSizeBox.setPadding(new Insets(8, 0, 8, 0));
        fontSizeBox.getChildren().add(fontSizeTitle);

        fontSizeTitle.setMinSize(185, 40);
        fontSizeTitle.setPrefSize(185, 40);
        fontSizeTitle.setMaxSize(185, 40);
        fontSizeTitle.setPadding(new Insets(0, 10, 0, 10));
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
            captionsOptionsPane.fontSizeTab.subText.setText("50%");

            captionsController.currentFontSize = (int) (captionsController.defaultFontSize * 0.75);

            captionsController.captionsLabel1.setStyle("-fx-font-family: " + captionsController.currentFontFamily + "; -fx-font-size: " + captionsController.mediaWidthMultiplier.multiply(captionsController.currentFontSize).get());
            captionsController.captionsLabel2.setStyle("-fx-font-family: " + captionsController.currentFontFamily + "; -fx-font-size: " + captionsController.mediaWidthMultiplier.multiply(captionsController.currentFontSize).get());

            captionsController.showCaptions();
        });

        _75Tab.setOnMouseClicked(e -> {

            for(CheckTab checkTab : checkTabs){
                checkTab.checkIcon.setVisible(false);
            }

            _75Tab.checkIcon.setVisible(true);
            captionsOptionsPane.fontSizeTab.subText.setText("75%");

            captionsController.currentFontSize = (int) (captionsController.defaultFontSize * 0.875);

            captionsController.captionsLabel1.setStyle("-fx-font-family: " + captionsController.currentFontFamily + "; -fx-font-size: " + captionsController.mediaWidthMultiplier.multiply(captionsController.currentFontSize).get());
            captionsController.captionsLabel2.setStyle("-fx-font-family: " + captionsController.currentFontFamily + "; -fx-font-size: " + captionsController.mediaWidthMultiplier.multiply(captionsController.currentFontSize).get());

            captionsController.showCaptions();
        });

        _100Tab.setOnMouseClicked(e -> {

            for(CheckTab checkTab : checkTabs){
                checkTab.checkIcon.setVisible(false);
            }

            _100Tab.checkIcon.setVisible(true);
            captionsOptionsPane.fontSizeTab.subText.setText("100%");


            captionsController.currentFontSize = (int) (captionsController.defaultFontSize * 1);

            captionsController.captionsLabel1.setStyle("-fx-font-family: " + captionsController.currentFontFamily + "; -fx-font-size: " + captionsController.mediaWidthMultiplier.multiply(captionsController.currentFontSize).get());
            captionsController.captionsLabel2.setStyle("-fx-font-family: " + captionsController.currentFontFamily + "; -fx-font-size: " + captionsController.mediaWidthMultiplier.multiply(captionsController.currentFontSize).get());

            captionsController.showCaptions();
        });

        _150Tab.setOnMouseClicked(e -> {

            for(CheckTab checkTab : checkTabs){
                checkTab.checkIcon.setVisible(false);
            }

            _150Tab.checkIcon.setVisible(true);
            captionsOptionsPane.fontSizeTab.subText.setText("150%");


            captionsController.currentFontSize = (int) (captionsController.defaultFontSize * 1.25);

            captionsController.captionsLabel1.setStyle("-fx-font-family: " + captionsController.currentFontFamily + "; -fx-font-size: " + captionsController.mediaWidthMultiplier.multiply(captionsController.currentFontSize).get());
            captionsController.captionsLabel2.setStyle("-fx-font-family: " + captionsController.currentFontFamily + "; -fx-font-size: " + captionsController.mediaWidthMultiplier.multiply(captionsController.currentFontSize).get());

            captionsController.showCaptions();
        });

        _200Tab.setOnMouseClicked(e -> {

            for(CheckTab checkTab : checkTabs){
                checkTab.checkIcon.setVisible(false);
            }

            _200Tab.checkIcon.setVisible(true);
            captionsOptionsPane.fontSizeTab.subText.setText("200%");


            captionsController.currentFontSize = (int) (captionsController.defaultFontSize * 1.5);

            captionsController.captionsLabel1.setStyle("-fx-font-family: " + captionsController.currentFontFamily + "; -fx-font-size: " + captionsController.mediaWidthMultiplier.multiply(captionsController.currentFontSize).get());
            captionsController.captionsLabel2.setStyle("-fx-font-family: " + captionsController.currentFontFamily + "; -fx-font-size: " + captionsController.mediaWidthMultiplier.multiply(captionsController.currentFontSize).get());

            captionsController.showCaptions();
        });

        _300Tab.setOnMouseClicked(e -> {

            for(CheckTab checkTab : checkTabs){
                checkTab.checkIcon.setVisible(false);
            }

            _300Tab.checkIcon.setVisible(true);
            captionsOptionsPane.fontSizeTab.subText.setText("300%");


            captionsController.currentFontSize = (int) (captionsController.defaultFontSize * 2);

            captionsController.captionsLabel1.setStyle("-fx-font-family: " + captionsController.currentFontFamily + "; -fx-font-size: " + captionsController.mediaWidthMultiplier.multiply(captionsController.currentFontSize).get());
            captionsController.captionsLabel2.setStyle("-fx-font-family: " + captionsController.currentFontFamily + "; -fx-font-size: " + captionsController.mediaWidthMultiplier.multiply(captionsController.currentFontSize).get());

            captionsController.showCaptions();
        });

        _400Tab.setOnMouseClicked(e -> {

            for(CheckTab checkTab : checkTabs){
                checkTab.checkIcon.setVisible(false);
            }

            _400Tab.checkIcon.setVisible(true);
            captionsOptionsPane.fontSizeTab.subText.setText("400%");


            captionsController.currentFontSize = (int) (captionsController.defaultFontSize * 2.25);

            captionsController.captionsLabel1.setStyle("-fx-font-family: " + captionsController.currentFontFamily + "; -fx-font-size: " + captionsController.mediaWidthMultiplier.multiply(captionsController.currentFontSize).get());
            captionsController.captionsLabel2.setStyle("-fx-font-family: " + captionsController.currentFontFamily + "; -fx-font-size: " + captionsController.mediaWidthMultiplier.multiply(captionsController.currentFontSize).get());

            captionsController.showCaptions();
        });


        captionsController.settingsController.settingsBuffer.getChildren().add(scrollPane);
    }


    public void closeFontSizePane(){
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

