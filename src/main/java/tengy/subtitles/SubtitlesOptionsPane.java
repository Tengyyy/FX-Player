package tengy.subtitles;

import javafx.animation.*;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;
import tengy.playbackSettings.PlaybackSettingsController;
import tengy.SVG;
import tengy.Utilities;

import java.util.ArrayList;
import java.util.List;

import static tengy.Utilities.keyboardFocusOff;
import static tengy.Utilities.keyboardFocusOn;


public class SubtitlesOptionsPane {

    SubtitlesController subtitlesController;

    ScrollPane scrollPane = new ScrollPane();
    VBox subtitlesOptionsBox = new VBox();

    HBox subtitlesOptionsTitle = new HBox();

    Button backButton = new Button();
    Region subtitlesOptionsBackIcon = new Region();
    SVGPath backSVG = new SVGPath();

    Label subtitlesOptionsTitleLabel = new Label();

    SubtitlesOptionsTab fontFamilyTab, fontColorTab, fontSizeTab, textAlignmentTab, backgroundColorTab, backgroundOpacityTab, lineSpacingTab, fontOpacityTab, resetTab;
    List<Node> focusNodes = new ArrayList<>();

    IntegerProperty focus = new SimpleIntegerProperty(-1);


    FontFamilyPane fontFamilyPane;
    FontColorPane fontColorPane;
    FontSizePane fontSizePane;
    TextAlignmentPane textAlignmentPane;
    BackgroundColorPane backgroundColorPane;
    BackgroundOpacityPane backgroundOpacityPane;
    LineSpacingPane lineSpacingPane;
    FontOpacityPane fontOpacityPane;


    public SubtitlesOptionsPane(SubtitlesController subtitlesController){
        this.subtitlesController = subtitlesController;

        backSVG.setContent(SVG.CHEVRON_LEFT.getContent());

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("settingsScroll");
        scrollPane.setPrefSize(270, 384);
        scrollPane.setMaxSize(270, 384);
        scrollPane.setContent(subtitlesOptionsBox);
        scrollPane.setVisible(false);
        scrollPane.setMouseTransparent(true);
        scrollPane.setFitToWidth(true);

        StackPane.setAlignment(scrollPane, Pos.BOTTOM_RIGHT);


        subtitlesOptionsBox.setAlignment(Pos.BOTTOM_CENTER);
        subtitlesOptionsBox.setPrefSize(270, 381);
        subtitlesOptionsBox.setMaxSize(270, 381);
        subtitlesOptionsBox.setPadding(new Insets(0, 0, 8, 0));
        subtitlesOptionsBox.getChildren().add(subtitlesOptionsTitle);

        subtitlesOptionsTitle.setPrefSize(270, 48);
        subtitlesOptionsTitle.setMaxSize(270, 48);
        subtitlesOptionsTitle.setPadding(new Insets(0, 10, 0, 10));
        subtitlesOptionsTitle.setAlignment(Pos.CENTER_LEFT);
        VBox.setMargin(subtitlesOptionsTitle, new Insets(0, 0, 10, 0));

        subtitlesOptionsTitle.getStyleClass().add("settingsPaneTitle");
        subtitlesOptionsTitle.getChildren().addAll(backButton, subtitlesOptionsTitleLabel);


        backButton.setMinSize(30, 40);
        backButton.setPrefSize(30, 40);
        backButton.setMaxSize(30, 40);
        backButton.setFocusTraversable(false);
        backButton.getStyleClass().addAll("transparentButton", "settingsMenuButton");
        backButton.setGraphic(subtitlesOptionsBackIcon);
        backButton.setOnAction((e) -> closeCaptionsOptions());
        backButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(0);
            }
            else {
                keyboardFocusOff(backButton);
                focus.set(-1);
            }
        });

        backButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            backButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        backButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            backButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });

        subtitlesOptionsBackIcon.setMinSize(8, 13);
        subtitlesOptionsBackIcon.setPrefSize(8, 13);
        subtitlesOptionsBackIcon.setMaxSize(8, 13);
        subtitlesOptionsBackIcon.getStyleClass().add("graphic");
        subtitlesOptionsBackIcon.setShape(backSVG);

        subtitlesOptionsTitleLabel.setMinHeight(40);
        subtitlesOptionsTitleLabel.setPrefHeight(40);
        subtitlesOptionsTitleLabel.setMaxHeight(40);
        subtitlesOptionsTitleLabel.setText("Options");
        subtitlesOptionsTitleLabel.setCursor(Cursor.HAND);
        subtitlesOptionsTitleLabel.setPadding(new Insets(0, 0, 0, 4));
        subtitlesOptionsTitleLabel.getStyleClass().add("settingsPaneText");
        subtitlesOptionsTitleLabel.setOnMouseClicked((e) -> closeCaptionsOptions());


        fontFamilyTab = new SubtitlesOptionsTab(this, subtitlesController, true, true, "Font family", "Sans-Serif Medium", 1, this::openFontFamilyPane);
        fontColorTab = new SubtitlesOptionsTab(this, subtitlesController, true, true, "Font color", "White", 2, this::openFontColorPane);
        fontSizeTab = new SubtitlesOptionsTab(this, subtitlesController, true, true, "Font size", "100%", 3, this::openFontSizePane);
        textAlignmentTab = new SubtitlesOptionsTab(this, subtitlesController, true, true, "Text alignment", "Center", 4, this::openTextAlignmentPane);
        backgroundColorTab = new SubtitlesOptionsTab(this, subtitlesController, true, true, "Background color", "Black", 5, this::openBackgroundColorPane);
        backgroundOpacityTab = new SubtitlesOptionsTab(this, subtitlesController, true, true, "Background opacity", "75%", 6, this::openBackgroundOpacityPane);
        lineSpacingTab = new SubtitlesOptionsTab(this, subtitlesController, true, true, "Line spacing", "100%", 7, this::openLineSpacingPane);
        fontOpacityTab = new SubtitlesOptionsTab(this, subtitlesController, true, true, "Font opacity", "100%", 8, this::openFontOpacityPane);
        resetTab = new SubtitlesOptionsTab(this, subtitlesController, false, false, "Reset", null, 9, this::resetCaptions);

        focusNodes.add(backButton);
        focusNodes.add(fontFamilyTab);
        focusNodes.add(fontColorTab);
        focusNodes.add(fontSizeTab);
        focusNodes.add(textAlignmentTab);
        focusNodes.add(backgroundColorTab);
        focusNodes.add(backgroundOpacityTab);
        focusNodes.add(lineSpacingTab);
        focusNodes.add(fontOpacityTab);
        focusNodes.add(resetTab);


        fontFamilyPane = new FontFamilyPane(subtitlesController, this);
        fontColorPane = new FontColorPane(subtitlesController, this);
        fontSizePane = new FontSizePane(subtitlesController, this);
        textAlignmentPane = new TextAlignmentPane(subtitlesController, this);
        backgroundColorPane = new BackgroundColorPane(subtitlesController, this);
        backgroundOpacityPane = new BackgroundOpacityPane(subtitlesController, this);
        lineSpacingPane = new LineSpacingPane(subtitlesController, this);
        fontOpacityPane = new FontOpacityPane(subtitlesController, this);

        subtitlesController.subtitlesPane.getChildren().add(scrollPane);
    }



    public void closeCaptionsOptions(){
        if(subtitlesController.animating.get()) return;

        subtitlesController.subtitlesState = SubtitlesState.HOME_OPEN;

        subtitlesController.subtitlesHome.scrollPane.setVisible(true);
        subtitlesController.subtitlesHome.scrollPane.setMouseTransparent(false);


        Timeline clipHeightTimeline = new Timeline();
        clipHeightTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(subtitlesController.clip.heightProperty(), subtitlesController.subtitlesHome.scrollPane.getHeight())));


        Timeline clipWidthTimeline = new Timeline();
        clipWidthTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(subtitlesController.clip.widthProperty(), subtitlesController.subtitlesHome.scrollPane.getWidth())));



        TranslateTransition captionsPaneTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), subtitlesController.subtitlesHome.scrollPane);
        captionsPaneTransition.setFromX(-scrollPane.getWidth());
        captionsPaneTransition.setToX(0);

        TranslateTransition captionsOptionsTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), scrollPane);
        captionsOptionsTransition.setFromX(0);
        captionsOptionsTransition.setToX(scrollPane.getWidth());


        ParallelTransition parallelTransition = new ParallelTransition(clipHeightTimeline, clipWidthTimeline, captionsPaneTransition, captionsOptionsTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            subtitlesController.animating.set(false);
            scrollPane.setVisible(false);
            scrollPane.setMouseTransparent(true);
            scrollPane.setTranslateX(0);
            subtitlesController.clip.setHeight(subtitlesController.subtitlesHome.scrollPane.getPrefHeight());
        });

        parallelTransition.play();
        subtitlesController.animating.set(true);
    }


    public void openFontFamilyPane(){
        if(subtitlesController.animating.get()) return;

        subtitlesController.subtitlesState = SubtitlesState.FONT_FAMILY_OPEN;

        fontFamilyPane.scrollPane.setVisible(true);
        fontFamilyPane.scrollPane.setMouseTransparent(false);


        Timeline clipHeightTimeline = new Timeline();
        clipHeightTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(subtitlesController.clip.heightProperty(), fontFamilyPane.scrollPane.getHeight())));


        Timeline clipWidthTimeline = new Timeline();
        clipWidthTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(subtitlesController.clip.widthProperty(), fontFamilyPane.scrollPane.getWidth())));



        TranslateTransition fontFamilyTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), fontFamilyPane.scrollPane);
        fontFamilyTransition.setFromX(fontFamilyPane.scrollPane.getWidth());
        fontFamilyTransition.setToX(0);

        TranslateTransition captionsOptionsTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), scrollPane);
        captionsOptionsTransition.setFromX(0);
        captionsOptionsTransition.setToX(-fontFamilyPane.scrollPane.getWidth());


        ParallelTransition parallelTransition = new ParallelTransition(clipHeightTimeline, clipWidthTimeline, fontFamilyTransition, captionsOptionsTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            subtitlesController.animating.set(false);
            scrollPane.setVisible(false);
            scrollPane.setMouseTransparent(true);
            scrollPane.setTranslateX(0);
            subtitlesController.clip.setHeight(fontFamilyPane.scrollPane.getPrefHeight());
        });

        parallelTransition.play();
        subtitlesController.animating.set(true);
    }

    public void openFontColorPane(){
        if(subtitlesController.animating.get()) return;

        subtitlesController.subtitlesState = SubtitlesState.FONT_COLOR_OPEN;

        fontColorPane.scrollPane.setVisible(true);
        fontColorPane.scrollPane.setMouseTransparent(false);

        Timeline clipHeightTimeline = new Timeline();
        clipHeightTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(subtitlesController.clip.heightProperty(), fontColorPane.scrollPane.getHeight())));


        Timeline clipWidthTimeline = new Timeline();
        clipWidthTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(subtitlesController.clip.widthProperty(), fontColorPane.scrollPane.getWidth())));



        TranslateTransition fontColorTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), fontColorPane.scrollPane);
        fontColorTransition.setFromX(fontColorPane.scrollPane.getWidth());
        fontColorTransition.setToX(0);

        TranslateTransition captionsOptionsTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), scrollPane);
        captionsOptionsTransition.setFromX(0);
        captionsOptionsTransition.setToX(-fontColorPane.scrollPane.getWidth());


        ParallelTransition parallelTransition = new ParallelTransition(clipHeightTimeline, clipWidthTimeline, fontColorTransition, captionsOptionsTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            subtitlesController.animating.set(false);
            scrollPane.setVisible(false);
            scrollPane.setMouseTransparent(true);
            scrollPane.setTranslateX(0);
            subtitlesController.clip.setHeight(fontColorPane.scrollPane.getPrefHeight());
        });

        parallelTransition.play();
        subtitlesController.animating.set(true);
    }

    public void openFontSizePane(){
        if(subtitlesController.animating.get()) return;

        subtitlesController.subtitlesState = SubtitlesState.FONT_SIZE_OPEN;

        fontSizePane.scrollPane.setVisible(true);
        fontSizePane.scrollPane.setMouseTransparent(false);


        Timeline clipHeightTimeline = new Timeline();
        clipHeightTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(subtitlesController.clip.heightProperty(), fontSizePane.scrollPane.getHeight())));


        Timeline clipWidthTimeline = new Timeline();
        clipWidthTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(subtitlesController.clip.widthProperty(), fontSizePane.scrollPane.getWidth())));

        TranslateTransition fontSizeTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), fontSizePane.scrollPane);
        fontSizeTransition.setFromX(fontSizePane.scrollPane.getWidth());
        fontSizeTransition.setToX(0);

        TranslateTransition captionsOptionsTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), scrollPane);
        captionsOptionsTransition.setFromX(0);
        captionsOptionsTransition.setToX(-fontSizePane.scrollPane.getWidth());


        ParallelTransition parallelTransition = new ParallelTransition(clipHeightTimeline, clipWidthTimeline, fontSizeTransition, captionsOptionsTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            subtitlesController.animating.set(false);
            scrollPane.setVisible(false);
            scrollPane.setMouseTransparent(true);
            scrollPane.setTranslateX(0);
            subtitlesController.clip.setHeight(fontSizePane.scrollPane.getPrefHeight());
        });

        parallelTransition.play();
        subtitlesController.animating.set(true);
    }

    public void openTextAlignmentPane(){
        if(subtitlesController.animating.get()) return;

        subtitlesController.subtitlesState = SubtitlesState.TEXT_ALIGNMENT_OPEN;

        textAlignmentPane.scrollPane.setVisible(true);
        textAlignmentPane.scrollPane.setMouseTransparent(false);

        Timeline clipHeightTimeline = new Timeline();
        clipHeightTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(subtitlesController.clip.heightProperty(), textAlignmentPane.scrollPane.getHeight())));


        Timeline clipWidthTimeline = new Timeline();
        clipWidthTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(subtitlesController.clip.widthProperty(), textAlignmentPane.scrollPane.getWidth())));



        TranslateTransition textAlignmentTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), textAlignmentPane.scrollPane);
        textAlignmentTransition.setFromX(textAlignmentPane.scrollPane.getWidth());
        textAlignmentTransition.setToX(0);

        TranslateTransition captionsOptionsTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), scrollPane);
        captionsOptionsTransition.setFromX(0);
        captionsOptionsTransition.setToX(-textAlignmentPane.scrollPane.getWidth());


        ParallelTransition parallelTransition = new ParallelTransition(clipHeightTimeline, clipWidthTimeline, textAlignmentTransition, captionsOptionsTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            subtitlesController.animating.set(false);
            scrollPane.setVisible(false);
            scrollPane.setMouseTransparent(true);
            scrollPane.setTranslateX(0);
            subtitlesController.clip.setHeight(textAlignmentPane.scrollPane.getPrefHeight());
        });

        parallelTransition.play();
        subtitlesController.animating.set(true);
    }

    public void openBackgroundColorPane(){
        if(subtitlesController.animating.get()) return;

        subtitlesController.subtitlesState = SubtitlesState.BACKGROUND_COLOR_OPEN;

        backgroundColorPane.scrollPane.setVisible(true);
        backgroundColorPane.scrollPane.setMouseTransparent(false);


        Timeline clipHeightTimeline = new Timeline();
        clipHeightTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(subtitlesController.clip.heightProperty(), backgroundColorPane.scrollPane.getHeight())));


        Timeline clipWidthTimeline = new Timeline();
        clipWidthTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(subtitlesController.clip.widthProperty(), backgroundColorPane.scrollPane.getWidth())));



        TranslateTransition backgroundColorTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), backgroundColorPane.scrollPane);
        backgroundColorTransition.setFromX(backgroundColorPane.scrollPane.getWidth());
        backgroundColorTransition.setToX(0);

        TranslateTransition captionsOptionsTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), scrollPane);
        captionsOptionsTransition.setFromX(0);
        captionsOptionsTransition.setToX(-backgroundColorPane.scrollPane.getWidth());


        ParallelTransition parallelTransition = new ParallelTransition(clipHeightTimeline, clipWidthTimeline, backgroundColorTransition, captionsOptionsTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            subtitlesController.animating.set(false);
            scrollPane.setVisible(false);
            scrollPane.setMouseTransparent(true);
            scrollPane.setTranslateX(0);
            subtitlesController.clip.setHeight(backgroundColorPane.scrollPane.getPrefHeight());
        });

        parallelTransition.play();
        subtitlesController.animating.set(true);
    }

    public void openBackgroundOpacityPane(){
        if(subtitlesController.animating.get()) return;

        subtitlesController.subtitlesState = SubtitlesState.BACKGROUND_OPACITY_OPEN;

        backgroundOpacityPane.scrollPane.setVisible(true);
        backgroundOpacityPane.scrollPane.setMouseTransparent(false);

        Timeline clipHeightTimeline = new Timeline();
        clipHeightTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(subtitlesController.clip.heightProperty(), backgroundOpacityPane.scrollPane.getHeight())));


        Timeline clipWidthTimeline = new Timeline();
        clipWidthTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(subtitlesController.clip.widthProperty(), backgroundOpacityPane.scrollPane.getWidth())));



        TranslateTransition backgroundOpacityTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), backgroundOpacityPane.scrollPane);
        backgroundOpacityTransition.setFromX(backgroundOpacityPane.scrollPane.getWidth());
        backgroundOpacityTransition.setToX(0);

        TranslateTransition captionsOptionsTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), scrollPane);
        captionsOptionsTransition.setFromX(0);
        captionsOptionsTransition.setToX(-backgroundOpacityPane.scrollPane.getWidth());


        ParallelTransition parallelTransition = new ParallelTransition(clipHeightTimeline, clipWidthTimeline, backgroundOpacityTransition, captionsOptionsTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            subtitlesController.animating.set(false);
            scrollPane.setVisible(false);
            scrollPane.setMouseTransparent(true);
            scrollPane.setTranslateX(0);
            subtitlesController.clip.setHeight(backgroundOpacityPane.scrollPane.getPrefHeight());
        });

        parallelTransition.play();
        subtitlesController.animating.set(true);
    }

    public void openLineSpacingPane(){
        if(subtitlesController.animating.get()) return;

        subtitlesController.subtitlesState = SubtitlesState.LINE_SPACING_OPEN;

        lineSpacingPane.scrollPane.setVisible(true);
        lineSpacingPane.scrollPane.setMouseTransparent(false);

        Timeline clipHeightTimeline = new Timeline();
        clipHeightTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(subtitlesController.clip.heightProperty(), lineSpacingPane.scrollPane.getHeight())));


        Timeline clipWidthTimeline = new Timeline();
        clipWidthTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(subtitlesController.clip.widthProperty(), lineSpacingPane.scrollPane.getWidth())));



        TranslateTransition lineSpacingTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), lineSpacingPane.scrollPane);
        lineSpacingTransition.setFromX(lineSpacingPane.scrollPane.getWidth());
        lineSpacingTransition.setToX(0);

        TranslateTransition captionsOptionsTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), scrollPane);
        captionsOptionsTransition.setFromX(0);
        captionsOptionsTransition.setToX(-lineSpacingPane.scrollPane.getWidth());


        ParallelTransition parallelTransition = new ParallelTransition(clipHeightTimeline, clipWidthTimeline, lineSpacingTransition, captionsOptionsTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            subtitlesController.animating.set(false);
            scrollPane.setVisible(false);
            scrollPane.setMouseTransparent(true);
            scrollPane.setTranslateX(0);
            subtitlesController.clip.setHeight(lineSpacingPane.scrollPane.getPrefHeight());
        });

        parallelTransition.play();
        subtitlesController.animating.set(true);
    }

    public void openFontOpacityPane(){
        if(subtitlesController.animating.get()) return;

        subtitlesController.subtitlesState = SubtitlesState.OPACITY_OPEN;

        fontOpacityPane.scrollPane.setVisible(true);
        fontOpacityPane.scrollPane.setMouseTransparent(false);

        Timeline clipHeightTimeline = new Timeline();
        clipHeightTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(subtitlesController.clip.heightProperty(), fontOpacityPane.scrollPane.getHeight())));


        Timeline clipWidthTimeline = new Timeline();
        clipWidthTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(subtitlesController.clip.widthProperty(), fontOpacityPane.scrollPane.getWidth())));



        TranslateTransition opacityTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), fontOpacityPane.scrollPane);
        opacityTransition.setFromX(fontOpacityPane.scrollPane.getWidth());
        opacityTransition.setToX(0);

        TranslateTransition captionsOptionsTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), scrollPane);
        captionsOptionsTransition.setFromX(0);
        captionsOptionsTransition.setToX(-fontOpacityPane.scrollPane.getWidth());


        ParallelTransition parallelTransition = new ParallelTransition(clipHeightTimeline, clipWidthTimeline, opacityTransition, captionsOptionsTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            subtitlesController.animating.set(false);
            scrollPane.setVisible(false);
            scrollPane.setMouseTransparent(true);
            scrollPane.setTranslateX(0);
            subtitlesController.clip.setHeight(fontOpacityPane.scrollPane.getPrefHeight());
        });

        parallelTransition.play();
        subtitlesController.animating.set(true);
    }

    public void resetCaptions(){
        // revert to default settings

        backgroundColorPane.pressBlackTab(false);
        backgroundOpacityPane.press_75Tab(false);
        fontColorPane.pressWhiteTab(false);
        fontFamilyPane.pressSansSerifMediumTab(false);
        fontOpacityPane.press_100Tab(false);
        fontSizePane.press_100Tab(false);
        lineSpacingPane.press_100Tab(false);
        textAlignmentPane.pressCenterTab(false);
    }


    public void focusForward() {
        int newFocus;

        if(focus.get() == 9 || focus.get() == -1) newFocus = 0;
        else newFocus = focus.get() + 1;

        keyboardFocusOn(focusNodes.get(newFocus));
        if(newFocus == 0) scrollPane.setVvalue(0);
        else Utilities.checkScrollDown(scrollPane, focusNodes.get(newFocus));
    }

    public void focusBackward() {
        int newFocus;

        if(focus.get() == 0) newFocus = 9;
        else if(focus.get() == -1) newFocus = 0;
        else newFocus = focus.get() - 1;

        keyboardFocusOn(focusNodes.get(newFocus));
        if(newFocus == 0) scrollPane.setVvalue(0);
        else Utilities.checkScrollUp(scrollPane, focusNodes.get(newFocus));
    }
}
