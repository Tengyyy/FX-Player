package hans.Captions;

import hans.*;
import hans.Settings.*;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;


public class CaptionsOptionsPane {

    CaptionsController captionsController;

    ScrollPane scrollPane = new ScrollPane();
    VBox captionsOptionsBox = new VBox();

    HBox captionsOptionsTitle = new HBox();

    StackPane captionsOptionsBackPane = new StackPane();
    Region captionsOptionsBackIcon = new Region();
    SVGPath backSVG = new SVGPath();

    Label captionsOptionsTitleLabel = new Label();

    CaptionsOptionsTab fontFamilyTab, fontColorTab, fontSizeTab, textAlignmentTab, backgroundColorTab, backgroundOpacityTab, lineSpacingTab, fontOpacityTab, resetTab;


    FontFamilyPane fontFamilyPane;
    FontColorPane fontColorPane;
    FontSizePane fontSizePane;
    TextAlignmentPane textAlignmentPane;
    BackgroundColorPane backgroundColorPane;
    BackgroundOpacityPane backgroundOpacityPane;
    LineSpacingPane lineSpacingPane;
    FontOpacityPane fontOpacityPane;


    public CaptionsOptionsPane(CaptionsController captionsController){
        this.captionsController = captionsController;

        backSVG.setContent(App.svgMap.get(SVG.CHEVRON_LEFT));

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("settingsScroll");
        scrollPane.setPrefSize(270, 384);
        scrollPane.setMaxSize(270, 384);
        scrollPane.setContent(captionsOptionsBox);
        scrollPane.setVisible(false);
        scrollPane.setMouseTransparent(true);
        scrollPane.setFitToWidth(true);

        StackPane.setAlignment(scrollPane, Pos.BOTTOM_RIGHT);


        captionsOptionsBox.setAlignment(Pos.BOTTOM_CENTER);
        captionsOptionsBox.setMinSize(270, 381);
        captionsOptionsBox.setPrefSize(270, 381);
        captionsOptionsBox.setMaxSize(270, 381);
        captionsOptionsBox.setPadding(new Insets(8, 0, 8, 0));
        captionsOptionsBox.getChildren().add(captionsOptionsTitle);

        captionsOptionsTitle.setMinSize(260, 40);
        captionsOptionsTitle.setPrefSize(270, 40);
        captionsOptionsTitle.setMaxSize(270, 40);
        captionsOptionsTitle.setPadding(new Insets(0, 10, 0, 10));
        VBox.setMargin(captionsOptionsTitle, new Insets(0, 0, 10, 0));

        captionsOptionsTitle.getStyleClass().add("settingsPaneTitle");
        captionsOptionsTitle.getChildren().addAll(captionsOptionsBackPane, captionsOptionsTitleLabel);


        captionsOptionsBackPane.setMinSize(24, 40);
        captionsOptionsBackPane.setPrefSize(24, 40);
        captionsOptionsBackPane.setMaxSize(24, 40);
        captionsOptionsBackPane.getChildren().add(captionsOptionsBackIcon);
        captionsOptionsBackPane.setCursor(Cursor.HAND);
        captionsOptionsBackPane.setOnMouseClicked((e) -> closeCaptionsOptions());

        captionsOptionsBackIcon.setMinSize(8, 13);
        captionsOptionsBackIcon.setPrefSize(8, 13);
        captionsOptionsBackIcon.setMaxSize(8, 13);
        captionsOptionsBackIcon.getStyleClass().add("settingsPaneIcon");
        captionsOptionsBackIcon.setShape(backSVG);

        captionsOptionsTitleLabel.setMinHeight(40);
        captionsOptionsTitleLabel.setPrefHeight(40);
        captionsOptionsTitleLabel.setMaxHeight(40);
        captionsOptionsTitleLabel.setText("Options");
        captionsOptionsTitleLabel.setCursor(Cursor.HAND);
        captionsOptionsTitleLabel.getStyleClass().add("settingsPaneText");
        captionsOptionsTitleLabel.setOnMouseClicked((e) -> closeCaptionsOptions());


        fontFamilyTab = new CaptionsOptionsTab(this, captionsController, true, true, "Font family", "Sans-Serif Medium");
        fontColorTab = new CaptionsOptionsTab(this, captionsController, true, true, "Font color", "White");
        fontSizeTab = new CaptionsOptionsTab(this, captionsController, true, true, "Font size", "100%");
        textAlignmentTab = new CaptionsOptionsTab(this, captionsController, true, true, "Text alignment", "Center");
        backgroundColorTab = new CaptionsOptionsTab(this, captionsController, true, true, "Background color", "Black");
        backgroundOpacityTab = new CaptionsOptionsTab(this, captionsController, true, true, "Background opacity", "75%");
        lineSpacingTab = new CaptionsOptionsTab(this, captionsController, true, true, "Line spacing", "100%");
        fontOpacityTab = new CaptionsOptionsTab(this, captionsController, true, true, "Font opacity", "100%");
        resetTab = new CaptionsOptionsTab(this, captionsController, false, false, "Reset", null);


        fontFamilyPane = new FontFamilyPane(captionsController, this);
        fontColorPane = new FontColorPane(captionsController, this);
        fontSizePane = new FontSizePane(captionsController, this);
        textAlignmentPane = new TextAlignmentPane(captionsController, this);
        backgroundColorPane = new BackgroundColorPane(captionsController, this);
        backgroundOpacityPane = new BackgroundOpacityPane(captionsController, this);
        lineSpacingPane = new LineSpacingPane(captionsController, this);
        fontOpacityPane = new FontOpacityPane(captionsController, this);


        fontFamilyTab.setOnMouseClicked(e -> openFontFamilyPane());
        fontColorTab.setOnMouseClicked(e -> openFontColorPane());
        fontSizeTab.setOnMouseClicked(e -> openFontSizePane());
        textAlignmentTab.setOnMouseClicked(e -> openTextAlignmentPane());
        backgroundColorTab.setOnMouseClicked(e -> openBackgroundColorPane());
        backgroundOpacityTab.setOnMouseClicked(e -> openBackgroundOpacityPane());
        lineSpacingTab.setOnMouseClicked(e -> openLineSpacingPane());
        fontOpacityTab.setOnMouseClicked(e -> openFontOpacityPane());
        resetTab.setOnMouseClicked(e -> resetCaptions());


        captionsController.captionsPane.getChildren().add(scrollPane);

    }



    public void closeCaptionsOptions(){
        if(captionsController.animating.get()) return;

        captionsController.captionsState = CaptionsState.HOME_OPEN;

        captionsController.captionsHome.scrollPane.setVisible(true);
        captionsController.captionsHome.scrollPane.setMouseTransparent(false);


        Timeline clipHeightTimeline = new Timeline();
        clipHeightTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.clip.heightProperty(), captionsController.captionsHome.scrollPane.getHeight())));


        Timeline clipWidthTimeline = new Timeline();
        clipWidthTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.clip.widthProperty(), captionsController.captionsHome.scrollPane.getWidth())));



        TranslateTransition captionsPaneTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), captionsController.captionsHome.scrollPane);
        captionsPaneTransition.setFromX(-scrollPane.getWidth());
        captionsPaneTransition.setToX(0);

        TranslateTransition captionsOptionsTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), scrollPane);
        captionsOptionsTransition.setFromX(0);
        captionsOptionsTransition.setToX(scrollPane.getWidth());


        ParallelTransition parallelTransition = new ParallelTransition(clipHeightTimeline, clipWidthTimeline, captionsPaneTransition, captionsOptionsTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            captionsController.animating.set(false);
            scrollPane.setVisible(false);
            scrollPane.setMouseTransparent(true);
            scrollPane.setTranslateX(0);
        });

        parallelTransition.play();
        captionsController.animating.set(true);
    }


    public void openFontFamilyPane(){
        if(captionsController.animating.get()) return;

        captionsController.captionsState = CaptionsState.FONT_FAMILY_OPEN;

        fontFamilyPane.scrollPane.setVisible(true);
        fontFamilyPane.scrollPane.setMouseTransparent(false);

        Timeline clipHeightTimeline = new Timeline();
        clipHeightTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.clip.heightProperty(), fontFamilyPane.scrollPane.getHeight())));


        Timeline clipWidthTimeline = new Timeline();
        clipWidthTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.clip.widthProperty(), fontFamilyPane.scrollPane.getWidth())));



        TranslateTransition fontFamilyTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), fontFamilyPane.scrollPane);
        fontFamilyTransition.setFromX(fontFamilyPane.scrollPane.getWidth());
        fontFamilyTransition.setToX(0);

        TranslateTransition captionsOptionsTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), scrollPane);
        captionsOptionsTransition.setFromX(0);
        captionsOptionsTransition.setToX(-fontFamilyPane.scrollPane.getWidth());


        ParallelTransition parallelTransition = new ParallelTransition(clipHeightTimeline, clipWidthTimeline, fontFamilyTransition, captionsOptionsTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            captionsController.animating.set(false);
            scrollPane.setVisible(false);
            scrollPane.setMouseTransparent(true);
            scrollPane.setTranslateX(0);
            captionsController.clip.setHeight(fontFamilyPane.scrollPane.getPrefHeight());
        });

        parallelTransition.play();
        captionsController.animating.set(true);
    }

    public void openFontColorPane(){
        if(captionsController.animating.get()) return;

        captionsController.captionsState = CaptionsState.FONT_COLOR_OPEN;

        fontColorPane.scrollPane.setVisible(true);
        fontColorPane.scrollPane.setMouseTransparent(false);

        Timeline clipHeightTimeline = new Timeline();
        clipHeightTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.clip.heightProperty(), fontColorPane.scrollPane.getHeight())));


        Timeline clipWidthTimeline = new Timeline();
        clipWidthTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.clip.widthProperty(), fontColorPane.scrollPane.getWidth())));



        TranslateTransition fontColorTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), fontColorPane.scrollPane);
        fontColorTransition.setFromX(fontColorPane.scrollPane.getWidth());
        fontColorTransition.setToX(0);

        TranslateTransition captionsOptionsTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), scrollPane);
        captionsOptionsTransition.setFromX(0);
        captionsOptionsTransition.setToX(-fontColorPane.scrollPane.getWidth());


        ParallelTransition parallelTransition = new ParallelTransition(clipHeightTimeline, clipWidthTimeline, fontColorTransition, captionsOptionsTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            captionsController.animating.set(false);
            scrollPane.setVisible(false);
            scrollPane.setMouseTransparent(true);
            scrollPane.setTranslateX(0);
            captionsController.clip.setHeight(fontColorPane.scrollPane.getPrefHeight());
        });

        parallelTransition.play();
        captionsController.animating.set(true);
    }

    public void openFontSizePane(){
        if(captionsController.animating.get()) return;

        captionsController.captionsState = CaptionsState.FONT_SIZE_OPEN;

        fontSizePane.scrollPane.setVisible(true);
        fontSizePane.scrollPane.setMouseTransparent(false);

        Timeline clipHeightTimeline = new Timeline();
        clipHeightTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.clip.heightProperty(), fontSizePane.scrollPane.getHeight())));


        Timeline clipWidthTimeline = new Timeline();
        clipWidthTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.clip.widthProperty(), fontSizePane.scrollPane.getWidth())));



        TranslateTransition fontSizeTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), fontSizePane.scrollPane);
        fontSizeTransition.setFromX(fontSizePane.scrollPane.getWidth());
        fontSizeTransition.setToX(0);

        TranslateTransition captionsOptionsTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), scrollPane);
        captionsOptionsTransition.setFromX(0);
        captionsOptionsTransition.setToX(-fontSizePane.scrollPane.getWidth());


        ParallelTransition parallelTransition = new ParallelTransition(clipHeightTimeline, clipWidthTimeline, fontSizeTransition, captionsOptionsTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            captionsController.animating.set(false);
            scrollPane.setVisible(false);
            scrollPane.setMouseTransparent(true);
            scrollPane.setTranslateX(0);
            captionsController.clip.setHeight(fontSizePane.scrollPane.getPrefHeight());
        });

        parallelTransition.play();
        captionsController.animating.set(true);
    }

    public void openTextAlignmentPane(){
        if(captionsController.animating.get()) return;

        captionsController.captionsState = CaptionsState.TEXT_ALIGNMENT_OPEN;

        textAlignmentPane.scrollPane.setVisible(true);
        textAlignmentPane.scrollPane.setMouseTransparent(false);

        Timeline clipHeightTimeline = new Timeline();
        clipHeightTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.clip.heightProperty(), textAlignmentPane.scrollPane.getHeight())));


        Timeline clipWidthTimeline = new Timeline();
        clipWidthTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.clip.widthProperty(), textAlignmentPane.scrollPane.getWidth())));



        TranslateTransition textAlignmentTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), textAlignmentPane.scrollPane);
        textAlignmentTransition.setFromX(textAlignmentPane.scrollPane.getWidth());
        textAlignmentTransition.setToX(0);

        TranslateTransition captionsOptionsTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), scrollPane);
        captionsOptionsTransition.setFromX(0);
        captionsOptionsTransition.setToX(-textAlignmentPane.scrollPane.getWidth());


        ParallelTransition parallelTransition = new ParallelTransition(clipHeightTimeline, clipWidthTimeline, textAlignmentTransition, captionsOptionsTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            captionsController.animating.set(false);
            scrollPane.setVisible(false);
            scrollPane.setMouseTransparent(true);
            scrollPane.setTranslateX(0);
            captionsController.clip.setHeight(textAlignmentPane.scrollPane.getPrefHeight());
        });

        parallelTransition.play();
        captionsController.animating.set(true);
    }

    public void openBackgroundColorPane(){
        if(captionsController.animating.get()) return;

        captionsController.captionsState = CaptionsState.BACKGROUND_COLOR_OPEN;

        backgroundColorPane.scrollPane.setVisible(true);
        backgroundColorPane.scrollPane.setMouseTransparent(false);

        Timeline clipHeightTimeline = new Timeline();
        clipHeightTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.clip.heightProperty(), backgroundColorPane.scrollPane.getHeight())));


        Timeline clipWidthTimeline = new Timeline();
        clipWidthTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.clip.widthProperty(), backgroundColorPane.scrollPane.getWidth())));



        TranslateTransition backgroundColorTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), backgroundColorPane.scrollPane);
        backgroundColorTransition.setFromX(backgroundColorPane.scrollPane.getWidth());
        backgroundColorTransition.setToX(0);

        TranslateTransition captionsOptionsTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), scrollPane);
        captionsOptionsTransition.setFromX(0);
        captionsOptionsTransition.setToX(-backgroundColorPane.scrollPane.getWidth());


        ParallelTransition parallelTransition = new ParallelTransition(clipHeightTimeline, clipWidthTimeline, backgroundColorTransition, captionsOptionsTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            captionsController.animating.set(false);
            scrollPane.setVisible(false);
            scrollPane.setMouseTransparent(true);
            scrollPane.setTranslateX(0);
            captionsController.clip.setHeight(backgroundColorPane.scrollPane.getPrefHeight());
        });

        parallelTransition.play();
        captionsController.animating.set(true);
    }

    public void openBackgroundOpacityPane(){
        if(captionsController.animating.get()) return;

        captionsController.captionsState = CaptionsState.BACKGROUND_OPACITY_OPEN;

        backgroundOpacityPane.scrollPane.setVisible(true);
        backgroundOpacityPane.scrollPane.setMouseTransparent(false);

        Timeline clipHeightTimeline = new Timeline();
        clipHeightTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.clip.heightProperty(), backgroundOpacityPane.scrollPane.getHeight())));


        Timeline clipWidthTimeline = new Timeline();
        clipWidthTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.clip.widthProperty(), backgroundOpacityPane.scrollPane.getWidth())));



        TranslateTransition backgroundOpacityTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), backgroundOpacityPane.scrollPane);
        backgroundOpacityTransition.setFromX(backgroundOpacityPane.scrollPane.getWidth());
        backgroundOpacityTransition.setToX(0);

        TranslateTransition captionsOptionsTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), scrollPane);
        captionsOptionsTransition.setFromX(0);
        captionsOptionsTransition.setToX(-backgroundOpacityPane.scrollPane.getWidth());


        ParallelTransition parallelTransition = new ParallelTransition(clipHeightTimeline, clipWidthTimeline, backgroundOpacityTransition, captionsOptionsTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            captionsController.animating.set(false);
            scrollPane.setVisible(false);
            scrollPane.setMouseTransparent(true);
            scrollPane.setTranslateX(0);
            captionsController.clip.setHeight(backgroundOpacityPane.scrollPane.getPrefHeight());
        });

        parallelTransition.play();
        captionsController.animating.set(true);
    }

    public void openLineSpacingPane(){
        if(captionsController.animating.get()) return;

        captionsController.captionsState = CaptionsState.LINE_SPACING_OPEN;

        lineSpacingPane.scrollPane.setVisible(true);
        lineSpacingPane.scrollPane.setMouseTransparent(false);

        Timeline clipHeightTimeline = new Timeline();
        clipHeightTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.clip.heightProperty(), lineSpacingPane.scrollPane.getHeight())));


        Timeline clipWidthTimeline = new Timeline();
        clipWidthTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.clip.widthProperty(), lineSpacingPane.scrollPane.getWidth())));



        TranslateTransition lineSpacingTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), lineSpacingPane.scrollPane);
        lineSpacingTransition.setFromX(lineSpacingPane.scrollPane.getWidth());
        lineSpacingTransition.setToX(0);

        TranslateTransition captionsOptionsTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), scrollPane);
        captionsOptionsTransition.setFromX(0);
        captionsOptionsTransition.setToX(-lineSpacingPane.scrollPane.getWidth());


        ParallelTransition parallelTransition = new ParallelTransition(clipHeightTimeline, clipWidthTimeline, lineSpacingTransition, captionsOptionsTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            captionsController.animating.set(false);
            scrollPane.setVisible(false);
            scrollPane.setMouseTransparent(true);
            scrollPane.setTranslateX(0);
            captionsController.clip.setHeight(lineSpacingPane.scrollPane.getPrefHeight());
        });

        parallelTransition.play();
        captionsController.animating.set(true);
    }

    public void openFontOpacityPane(){
        if(captionsController.animating.get()) return;

        captionsController.captionsState = CaptionsState.OPACITY_OPEN;

        fontOpacityPane.scrollPane.setVisible(true);
        fontOpacityPane.scrollPane.setMouseTransparent(false);

        Timeline clipHeightTimeline = new Timeline();
        clipHeightTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.clip.heightProperty(), fontOpacityPane.scrollPane.getHeight())));


        Timeline clipWidthTimeline = new Timeline();
        clipWidthTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.clip.widthProperty(), fontOpacityPane.scrollPane.getWidth())));



        TranslateTransition opacityTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), fontOpacityPane.scrollPane);
        opacityTransition.setFromX(fontOpacityPane.scrollPane.getWidth());
        opacityTransition.setToX(0);

        TranslateTransition captionsOptionsTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), scrollPane);
        captionsOptionsTransition.setFromX(0);
        captionsOptionsTransition.setToX(-fontOpacityPane.scrollPane.getWidth());


        ParallelTransition parallelTransition = new ParallelTransition(clipHeightTimeline, clipWidthTimeline, opacityTransition, captionsOptionsTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            captionsController.animating.set(false);
            scrollPane.setVisible(false);
            scrollPane.setMouseTransparent(true);
            scrollPane.setTranslateX(0);
            captionsController.clip.setHeight(fontOpacityPane.scrollPane.getPrefHeight());
        });

        parallelTransition.play();
        captionsController.animating.set(true);
    }

    public void resetCaptions(){
        // revert to default settings

        for(CheckTab checkTab : fontFamilyPane.checkTabs){
            checkTab.checkIcon.setVisible(false);
        }

        fontFamilyPane.sansSerifMediumTab.checkIcon.setVisible(true);
        fontFamilyTab.subText.setText("Sans-Serif Medium");

        captionsController.captionsBox.currentFontFamily = captionsController.captionsBox.defaultFontFamily;

        for(CheckTab checkTab : fontColorPane.checkTabs){
            checkTab.checkIcon.setVisible(false);
        }

        fontColorPane.whiteTab.checkIcon.setVisible(true);
        fontColorTab.subText.setText("White");

        captionsController.captionsBox.currentTextFill = captionsController.captionsBox.defaultTextFill;

        for(CheckTab checkTab : fontSizePane.checkTabs){
            checkTab.checkIcon.setVisible(false);
        }

        fontSizePane._100Tab.checkIcon.setVisible(true);
        fontSizeTab.subText.setText("100%");

        captionsController.captionsBox.currentFontSize = captionsController.captionsBox.defaultFontSize;

        for(CheckTab checkTab : textAlignmentPane.checkTabs){
            checkTab.checkIcon.setVisible(false);
        }

        textAlignmentPane.centerTab.checkIcon.setVisible(true);
        textAlignmentTab.subText.setText("Center");

        captionsController.captionsBox.currentTextAlignment = captionsController.captionsBox.defaultTextAlignment;

        for(CheckTab checkTab : backgroundOpacityPane.checkTabs){
            checkTab.checkIcon.setVisible(false);
        }

        backgroundOpacityPane._75Tab.checkIcon.setVisible(true);
        backgroundOpacityTab.subText.setText("75%");

        captionsController.captionsBox.currentBackgroundOpacity = captionsController.captionsBox.defaultBackgroundOpacity;

        for(CheckTab checkTab : backgroundColorPane.checkTabs){
            checkTab.checkIcon.setVisible(false);
        }

        backgroundColorPane.blackTab.checkIcon.setVisible(true);
        backgroundColorTab.subText.setText("Black");

        captionsController.captionsBox.currentBackgroundRed = captionsController.captionsBox.defaultBackgroundRed;
        captionsController.captionsBox.currentBackgroundGreen = captionsController.captionsBox.defaultBackgroundGreen;
        captionsController.captionsBox.currentBackgroundBlue = captionsController.captionsBox.defaultBackgroundBlue;
        captionsController.captionsBox.currentBackground = captionsController.captionsBox.defaultBackground;

        for(CheckTab checkTab : lineSpacingPane.checkTabs){
            checkTab.checkIcon.setVisible(false);
        }

        lineSpacingPane._100Tab.checkIcon.setVisible(true);
        lineSpacingTab.subText.setText("100%");

        captionsController.captionsBox.currentSpacing = captionsController.captionsBox.defaultSpacing;

        for(CheckTab checkTab : fontOpacityPane.checkTabs){
            checkTab.checkIcon.setVisible(false);
        }

        fontOpacityPane._100Tab.checkIcon.setVisible(true);
        fontOpacityTab.subText.setText("100%");

        captionsController.captionsBox.currentTextOpacity = captionsController.captionsBox.defaultTextOpacity;

        captionsController.captionsBox.captionsContainer.setSpacing(captionsController.captionsBox.defaultSpacing);
        captionsController.captionsBox.captionsContainer.setOpacity(captionsController.captionsBox.defaultTextOpacity);
        captionsController.captionsBox.captionsContainer.setAlignment(captionsController.captionsBox.defaultTextAlignment);

        captionsController.captionsBox.captionsLabel1.setTextFill(captionsController.captionsBox.defaultTextFill);
        captionsController.captionsBox.captionsLabel1.setBackground(new Background(new BackgroundFill(captionsController.captionsBox.defaultBackground, CornerRadii.EMPTY, Insets.EMPTY)));
        captionsController.captionsBox.captionsLabel1.setStyle("-fx-font-family: " + captionsController.captionsBox.defaultFontFamily + "; -fx-font-size: " + captionsController.captionsBox.mediaWidthMultiplier.multiply(captionsController.captionsBox.defaultFontSize).get());

        captionsController.captionsBox.captionsLabel2.setTextFill(captionsController.captionsBox.defaultTextFill);
        captionsController.captionsBox.captionsLabel2.setBackground(new Background(new BackgroundFill(captionsController.captionsBox.defaultBackground, CornerRadii.EMPTY, Insets.EMPTY)));
        captionsController.captionsBox.captionsLabel2.setStyle("-fx-font-family: " + captionsController.captionsBox.defaultFontFamily + "; -fx-font-size: " + captionsController.captionsBox.mediaWidthMultiplier.multiply(captionsController.captionsBox.defaultFontSize).get());



    }


}
