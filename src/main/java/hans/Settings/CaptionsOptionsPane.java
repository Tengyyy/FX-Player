package hans.Settings;

import hans.*;
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
        scrollPane.setPrefSize(265, 384);
        scrollPane.setMaxSize(265, 384);
        scrollPane.setContent(captionsOptionsBox);
        scrollPane.setVisible(false);
        scrollPane.setMouseTransparent(true);

        StackPane.setAlignment(scrollPane, Pos.BOTTOM_RIGHT);


        captionsOptionsBox.setAlignment(Pos.BOTTOM_CENTER);
        captionsOptionsBox.setMinSize(265, 381);
        captionsOptionsBox.setPrefSize(265, 381);
        captionsOptionsBox.setMaxSize(265, 381);
        captionsOptionsBox.setPadding(new Insets(8, 5, 8, 0));
        captionsOptionsBox.getChildren().add(captionsOptionsTitle);


        captionsOptionsTitle.setMinSize(255, 40);
        captionsOptionsTitle.setPrefSize(255, 40);
        captionsOptionsTitle.setMaxSize(255, 40);
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


        captionsController.settingsController.settingsPane.getChildren().add(scrollPane);

    }



    public void closeCaptionsOptions(){
        if(captionsController.settingsController.animating.get()) return;

        captionsController.settingsController.settingsState = SettingsState.CAPTIONS_PANE_OPEN;

        captionsController.captionsPane.captionsBox.setVisible(true);
        captionsController.captionsPane.captionsBox.setMouseTransparent(false);


        Timeline clipHeightTimeline = new Timeline();
        clipHeightTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.settingsController.clip.heightProperty(), captionsController.captionsPane.captionsBox.getHeight())));


        Timeline clipWidthTimeline = new Timeline();
        clipWidthTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.settingsController.clip.widthProperty(), captionsController.captionsPane.captionsBox.getWidth())));



        TranslateTransition captionsPaneTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), captionsController.captionsPane.captionsBox);
        captionsPaneTransition.setFromX(-scrollPane.getWidth());
        captionsPaneTransition.setToX(0);

        TranslateTransition captionsOptionsTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), scrollPane);
        captionsOptionsTransition.setFromX(0);
        captionsOptionsTransition.setToX(scrollPane.getWidth());


        ParallelTransition parallelTransition = new ParallelTransition(clipHeightTimeline, clipWidthTimeline, captionsPaneTransition, captionsOptionsTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            captionsController.settingsController.animating.set(false);
            scrollPane.setVisible(false);
            scrollPane.setMouseTransparent(true);
            scrollPane.setTranslateX(0);
        });

        parallelTransition.play();
        captionsController.settingsController.animating.set(true);
    }


    public void openFontFamilyPane(){
        if(captionsController.settingsController.animating.get()) return;

        captionsController.settingsController.settingsState = SettingsState.FONT_FAMILY_OPEN;

        fontFamilyPane.scrollPane.setVisible(true);
        fontFamilyPane.scrollPane.setMouseTransparent(false);

        Timeline clipHeightTimeline = new Timeline();
        clipHeightTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.settingsController.clip.heightProperty(), fontFamilyPane.scrollPane.getHeight())));


        Timeline clipWidthTimeline = new Timeline();
        clipWidthTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.settingsController.clip.widthProperty(), fontFamilyPane.scrollPane.getWidth())));



        TranslateTransition fontFamilyTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), fontFamilyPane.scrollPane);
        fontFamilyTransition.setFromX(fontFamilyPane.scrollPane.getWidth());
        fontFamilyTransition.setToX(0);

        TranslateTransition captionsOptionsTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), scrollPane);
        captionsOptionsTransition.setFromX(0);
        captionsOptionsTransition.setToX(-fontFamilyPane.scrollPane.getWidth());


        ParallelTransition parallelTransition = new ParallelTransition(clipHeightTimeline, clipWidthTimeline, fontFamilyTransition, captionsOptionsTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            captionsController.settingsController.animating.set(false);
            scrollPane.setVisible(false);
            scrollPane.setMouseTransparent(true);
            scrollPane.setTranslateX(0);
            captionsController.settingsController.clip.setHeight(fontFamilyPane.scrollPane.getPrefHeight());
        });

        parallelTransition.play();
        captionsController.settingsController.animating.set(true);
    }

    public void openFontColorPane(){
        if(captionsController.settingsController.animating.get()) return;

        captionsController.settingsController.settingsState = SettingsState.FONT_COLOR_OPEN;

        fontColorPane.scrollPane.setVisible(true);
        fontColorPane.scrollPane.setMouseTransparent(false);

        Timeline clipHeightTimeline = new Timeline();
        clipHeightTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.settingsController.clip.heightProperty(), fontColorPane.scrollPane.getHeight())));


        Timeline clipWidthTimeline = new Timeline();
        clipWidthTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.settingsController.clip.widthProperty(), fontColorPane.scrollPane.getWidth())));



        TranslateTransition fontColorTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), fontColorPane.scrollPane);
        fontColorTransition.setFromX(fontColorPane.scrollPane.getWidth());
        fontColorTransition.setToX(0);

        TranslateTransition captionsOptionsTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), scrollPane);
        captionsOptionsTransition.setFromX(0);
        captionsOptionsTransition.setToX(-fontColorPane.scrollPane.getWidth());


        ParallelTransition parallelTransition = new ParallelTransition(clipHeightTimeline, clipWidthTimeline, fontColorTransition, captionsOptionsTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            captionsController.settingsController.animating.set(false);
            scrollPane.setVisible(false);
            scrollPane.setMouseTransparent(true);
            scrollPane.setTranslateX(0);
            captionsController.settingsController.clip.setHeight(fontColorPane.scrollPane.getPrefHeight());
        });

        parallelTransition.play();
        captionsController.settingsController.animating.set(true);
    }

    public void openFontSizePane(){
        if(captionsController.settingsController.animating.get()) return;

        captionsController.settingsController.settingsState = SettingsState.FONT_SIZE_OPEN;

        fontSizePane.scrollPane.setVisible(true);
        fontSizePane.scrollPane.setMouseTransparent(false);

        Timeline clipHeightTimeline = new Timeline();
        clipHeightTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.settingsController.clip.heightProperty(), fontSizePane.scrollPane.getHeight())));


        Timeline clipWidthTimeline = new Timeline();
        clipWidthTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.settingsController.clip.widthProperty(), fontSizePane.scrollPane.getWidth())));



        TranslateTransition fontSizeTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), fontSizePane.scrollPane);
        fontSizeTransition.setFromX(fontSizePane.scrollPane.getWidth());
        fontSizeTransition.setToX(0);

        TranslateTransition captionsOptionsTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), scrollPane);
        captionsOptionsTransition.setFromX(0);
        captionsOptionsTransition.setToX(-fontSizePane.scrollPane.getWidth());


        ParallelTransition parallelTransition = new ParallelTransition(clipHeightTimeline, clipWidthTimeline, fontSizeTransition, captionsOptionsTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            captionsController.settingsController.animating.set(false);
            scrollPane.setVisible(false);
            scrollPane.setMouseTransparent(true);
            scrollPane.setTranslateX(0);
            captionsController.settingsController.clip.setHeight(fontSizePane.scrollPane.getPrefHeight());
        });

        parallelTransition.play();
        captionsController.settingsController.animating.set(true);
    }

    public void openTextAlignmentPane(){
        if(captionsController.settingsController.animating.get()) return;

        captionsController.settingsController.settingsState = SettingsState.TEXT_ALIGNMENT_OPEN;

        textAlignmentPane.scrollPane.setVisible(true);
        textAlignmentPane.scrollPane.setMouseTransparent(false);

        Timeline clipHeightTimeline = new Timeline();
        clipHeightTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.settingsController.clip.heightProperty(), textAlignmentPane.scrollPane.getHeight())));


        Timeline clipWidthTimeline = new Timeline();
        clipWidthTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.settingsController.clip.widthProperty(), textAlignmentPane.scrollPane.getWidth())));



        TranslateTransition textAlignmentTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), textAlignmentPane.scrollPane);
        textAlignmentTransition.setFromX(textAlignmentPane.scrollPane.getWidth());
        textAlignmentTransition.setToX(0);

        TranslateTransition captionsOptionsTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), scrollPane);
        captionsOptionsTransition.setFromX(0);
        captionsOptionsTransition.setToX(-textAlignmentPane.scrollPane.getWidth());


        ParallelTransition parallelTransition = new ParallelTransition(clipHeightTimeline, clipWidthTimeline, textAlignmentTransition, captionsOptionsTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            captionsController.settingsController.animating.set(false);
            scrollPane.setVisible(false);
            scrollPane.setMouseTransparent(true);
            scrollPane.setTranslateX(0);
            captionsController.settingsController.clip.setHeight(textAlignmentPane.scrollPane.getPrefHeight());
        });

        parallelTransition.play();
        captionsController.settingsController.animating.set(true);
    }

    public void openBackgroundColorPane(){
        if(captionsController.settingsController.animating.get()) return;

        captionsController.settingsController.settingsState = SettingsState.BACKGROUND_COLOR_OPEN;

        backgroundColorPane.scrollPane.setVisible(true);
        backgroundColorPane.scrollPane.setMouseTransparent(false);

        Timeline clipHeightTimeline = new Timeline();
        clipHeightTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.settingsController.clip.heightProperty(), backgroundColorPane.scrollPane.getHeight())));


        Timeline clipWidthTimeline = new Timeline();
        clipWidthTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.settingsController.clip.widthProperty(), backgroundColorPane.scrollPane.getWidth())));



        TranslateTransition backgroundColorTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), backgroundColorPane.scrollPane);
        backgroundColorTransition.setFromX(backgroundColorPane.scrollPane.getWidth());
        backgroundColorTransition.setToX(0);

        TranslateTransition captionsOptionsTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), scrollPane);
        captionsOptionsTransition.setFromX(0);
        captionsOptionsTransition.setToX(-backgroundColorPane.scrollPane.getWidth());


        ParallelTransition parallelTransition = new ParallelTransition(clipHeightTimeline, clipWidthTimeline, backgroundColorTransition, captionsOptionsTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            captionsController.settingsController.animating.set(false);
            scrollPane.setVisible(false);
            scrollPane.setMouseTransparent(true);
            scrollPane.setTranslateX(0);
            captionsController.settingsController.clip.setHeight(backgroundColorPane.scrollPane.getPrefHeight());
        });

        parallelTransition.play();
        captionsController.settingsController.animating.set(true);
    }

    public void openBackgroundOpacityPane(){
        if(captionsController.settingsController.animating.get()) return;

        captionsController.settingsController.settingsState = SettingsState.BACKGROUND_OPACITY_OPEN;

        backgroundOpacityPane.scrollPane.setVisible(true);
        backgroundOpacityPane.scrollPane.setMouseTransparent(false);

        Timeline clipHeightTimeline = new Timeline();
        clipHeightTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.settingsController.clip.heightProperty(), backgroundOpacityPane.scrollPane.getHeight())));


        Timeline clipWidthTimeline = new Timeline();
        clipWidthTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.settingsController.clip.widthProperty(), backgroundOpacityPane.scrollPane.getWidth())));



        TranslateTransition backgroundOpacityTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), backgroundOpacityPane.scrollPane);
        backgroundOpacityTransition.setFromX(backgroundOpacityPane.scrollPane.getWidth());
        backgroundOpacityTransition.setToX(0);

        TranslateTransition captionsOptionsTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), scrollPane);
        captionsOptionsTransition.setFromX(0);
        captionsOptionsTransition.setToX(-backgroundOpacityPane.scrollPane.getWidth());


        ParallelTransition parallelTransition = new ParallelTransition(clipHeightTimeline, clipWidthTimeline, backgroundOpacityTransition, captionsOptionsTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            captionsController.settingsController.animating.set(false);
            scrollPane.setVisible(false);
            scrollPane.setMouseTransparent(true);
            scrollPane.setTranslateX(0);
            captionsController.settingsController.clip.setHeight(backgroundOpacityPane.scrollPane.getPrefHeight());
        });

        parallelTransition.play();
        captionsController.settingsController.animating.set(true);
    }

    public void openLineSpacingPane(){
        if(captionsController.settingsController.animating.get()) return;

        captionsController.settingsController.settingsState = SettingsState.LINE_SPACING_OPEN;

        lineSpacingPane.scrollPane.setVisible(true);
        lineSpacingPane.scrollPane.setMouseTransparent(false);

        Timeline clipHeightTimeline = new Timeline();
        clipHeightTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.settingsController.clip.heightProperty(), lineSpacingPane.scrollPane.getHeight())));


        Timeline clipWidthTimeline = new Timeline();
        clipWidthTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.settingsController.clip.widthProperty(), lineSpacingPane.scrollPane.getWidth())));



        TranslateTransition lineSpacingTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), lineSpacingPane.scrollPane);
        lineSpacingTransition.setFromX(lineSpacingPane.scrollPane.getWidth());
        lineSpacingTransition.setToX(0);

        TranslateTransition captionsOptionsTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), scrollPane);
        captionsOptionsTransition.setFromX(0);
        captionsOptionsTransition.setToX(-lineSpacingPane.scrollPane.getWidth());


        ParallelTransition parallelTransition = new ParallelTransition(clipHeightTimeline, clipWidthTimeline, lineSpacingTransition, captionsOptionsTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            captionsController.settingsController.animating.set(false);
            scrollPane.setVisible(false);
            scrollPane.setMouseTransparent(true);
            scrollPane.setTranslateX(0);
            captionsController.settingsController.clip.setHeight(lineSpacingPane.scrollPane.getPrefHeight());
        });

        parallelTransition.play();
        captionsController.settingsController.animating.set(true);
    }

    public void openFontOpacityPane(){
        if(captionsController.settingsController.animating.get()) return;

        captionsController.settingsController.settingsState = SettingsState.OPACITY_OPEN;

        fontOpacityPane.scrollPane.setVisible(true);
        fontOpacityPane.scrollPane.setMouseTransparent(false);

        Timeline clipHeightTimeline = new Timeline();
        clipHeightTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.settingsController.clip.heightProperty(), fontOpacityPane.scrollPane.getHeight())));


        Timeline clipWidthTimeline = new Timeline();
        clipWidthTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.settingsController.clip.widthProperty(), fontOpacityPane.scrollPane.getWidth())));



        TranslateTransition opacityTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), fontOpacityPane.scrollPane);
        opacityTransition.setFromX(fontOpacityPane.scrollPane.getWidth());
        opacityTransition.setToX(0);

        TranslateTransition captionsOptionsTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), scrollPane);
        captionsOptionsTransition.setFromX(0);
        captionsOptionsTransition.setToX(-fontOpacityPane.scrollPane.getWidth());


        ParallelTransition parallelTransition = new ParallelTransition(clipHeightTimeline, clipWidthTimeline, opacityTransition, captionsOptionsTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            captionsController.settingsController.animating.set(false);
            scrollPane.setVisible(false);
            scrollPane.setMouseTransparent(true);
            scrollPane.setTranslateX(0);
            captionsController.settingsController.clip.setHeight(fontOpacityPane.scrollPane.getPrefHeight());
        });

        parallelTransition.play();
        captionsController.settingsController.animating.set(true);
    }

    public void resetCaptions(){
        // revert to default settings

        for(CheckTab checkTab : fontFamilyPane.checkTabs){
            checkTab.checkIcon.setVisible(false);
        }

        fontFamilyPane.sansSerifMediumTab.checkIcon.setVisible(true);
        fontFamilyTab.subText.setText("Sans-Serif Medium");

        captionsController.currentFontFamily = captionsController.defaultFontFamily;

        for(CheckTab checkTab : fontColorPane.checkTabs){
            checkTab.checkIcon.setVisible(false);
        }

        fontColorPane.whiteTab.checkIcon.setVisible(true);
        fontColorTab.subText.setText("White");

        captionsController.currentTextFill = captionsController.defaultTextFill;

        for(CheckTab checkTab : fontSizePane.checkTabs){
            checkTab.checkIcon.setVisible(false);
        }

        fontSizePane._100Tab.checkIcon.setVisible(true);
        fontSizeTab.subText.setText("100%");

        captionsController.currentFontSize = captionsController.defaultFontSize;

        for(CheckTab checkTab : textAlignmentPane.checkTabs){
            checkTab.checkIcon.setVisible(false);
        }

        textAlignmentPane.centerTab.checkIcon.setVisible(true);
        textAlignmentTab.subText.setText("Center");

        captionsController.currentTextAlignment = captionsController.defaultTextAlignment;

        for(CheckTab checkTab : backgroundOpacityPane.checkTabs){
            checkTab.checkIcon.setVisible(false);
        }

        backgroundOpacityPane._75Tab.checkIcon.setVisible(true);
        backgroundOpacityTab.subText.setText("75%");

        captionsController.currentBackgroundOpacity = captionsController.defaultBackgroundOpacity;

        for(CheckTab checkTab : backgroundColorPane.checkTabs){
            checkTab.checkIcon.setVisible(false);
        }

        backgroundColorPane.blackTab.checkIcon.setVisible(true);
        backgroundColorTab.subText.setText("Black");

        captionsController.currentBackgroundRed = captionsController.defaultBackgroundRed;
        captionsController.currentBackgroundGreen = captionsController.defaultBackgroundGreen;
        captionsController.currentBackgroundBlue = captionsController.defaultBackgroundBlue;
        captionsController.currentBackground = captionsController.defaultBackground;

        for(CheckTab checkTab : lineSpacingPane.checkTabs){
            checkTab.checkIcon.setVisible(false);
        }

        lineSpacingPane._100Tab.checkIcon.setVisible(true);
        lineSpacingTab.subText.setText("100%");

        captionsController.currentSpacing = captionsController.defaultSpacing;

        for(CheckTab checkTab : fontOpacityPane.checkTabs){
            checkTab.checkIcon.setVisible(false);
        }

        fontOpacityPane._100Tab.checkIcon.setVisible(true);
        fontOpacityTab.subText.setText("100%");

        captionsController.currentTextOpacity = captionsController.defaultTextOpacity;

        captionsController.captionsBox.setSpacing(captionsController.defaultSpacing);
        captionsController.captionsBox.setOpacity(captionsController.defaultTextOpacity);
        captionsController.captionsBox.setAlignment(captionsController.defaultTextAlignment);

        captionsController.captionsLabel1.setTextFill(captionsController.defaultTextFill);
        captionsController.captionsLabel1.setBackground(new Background(new BackgroundFill(captionsController.defaultBackground, CornerRadii.EMPTY, Insets.EMPTY)));
        captionsController.captionsLabel1.setStyle("-fx-font-family: " + captionsController.defaultFontFamily + "; -fx-font-size: " + captionsController.mediaWidthMultiplier.multiply(captionsController.defaultFontSize).get());

        captionsController.captionsLabel2.setTextFill(captionsController.defaultTextFill);
        captionsController.captionsLabel2.setBackground(new Background(new BackgroundFill(captionsController.defaultBackground, CornerRadii.EMPTY, Insets.EMPTY)));
        captionsController.captionsLabel2.setStyle("-fx-font-family: " + captionsController.defaultFontFamily + "; -fx-font-size: " + captionsController.mediaWidthMultiplier.multiply(captionsController.defaultFontSize).get());



    }


}
