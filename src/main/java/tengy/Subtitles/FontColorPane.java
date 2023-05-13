package tengy.Subtitles;

import tengy.*;
import tengy.PlaybackSettings.CheckTab;
import tengy.PlaybackSettings.PlaybackSettingsController;
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

    SubtitlesController subtitlesController;
    SubtitlesOptionsPane subtitlesOptionsPane;

    ScrollPane scrollPane = new ScrollPane();

    VBox fontColorBox = new VBox();
    HBox fontColorTitle = new HBox();

    StackPane fontColorBackPane = new StackPane();
    Region fontColorBackIcon = new Region();
    SVGPath backSVG = new SVGPath();

    Label fontColorTitleLabel = new Label();

    CheckTab whiteTab, yellowTab, greenTab, cyanTab, blueTab, magentaTab, redTab, blackTab;

    ArrayList<CheckTab> checkTabs = new ArrayList<>();


    FontColorPane(SubtitlesController subtitlesController, SubtitlesOptionsPane subtitlesOptionsPane){
        this.subtitlesController = subtitlesController;
        this.subtitlesOptionsPane = subtitlesOptionsPane;

        backSVG.setContent(SVG.CHEVRON_LEFT.getContent());

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("settingsScroll");
        scrollPane.setPrefSize(200, 349);
        scrollPane.setMaxSize(200, 349);
        scrollPane.setContent(fontColorBox);
        scrollPane.setVisible(false);
        scrollPane.setMouseTransparent(true);
        scrollPane.setFitToWidth(true);

        StackPane.setAlignment(scrollPane, Pos.BOTTOM_RIGHT);
        fontColorBox.setAlignment(Pos.BOTTOM_CENTER);


        fontColorBox.setPrefSize(200, 346);
        fontColorBox.setMaxSize(200, 346);
        fontColorBox.setPadding(new Insets(0, 0, 8, 0));
        fontColorBox.getChildren().add(fontColorTitle);
        fontColorBox.setFillWidth(true);

        fontColorTitle.setPrefSize(200, 48);
        fontColorTitle.setMaxSize(200, 48);
        fontColorTitle.setPadding(new Insets(0, 10, 0, 10));
        fontColorTitle.setAlignment(Pos.CENTER_LEFT);
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

        whiteTab = new CheckTab(false, "White");
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

        whiteTab.setOnMouseClicked(e -> pressWhiteTab(false));
        yellowTab.setOnMouseClicked(e -> pressYellowTab(false));
        greenTab.setOnMouseClicked(e -> pressGreenTab(false));
        cyanTab.setOnMouseClicked(e -> pressCyanTab(false));
        blueTab.setOnMouseClicked(e -> pressBlueTab(false));
        magentaTab.setOnMouseClicked(e -> pressMagentaTab(false));
        redTab.setOnMouseClicked(e -> pressRedTab(false));
        blackTab.setOnMouseClicked(e -> pressBlackTab(false));

        subtitlesController.subtitlesPane.getChildren().add(scrollPane);
    }


    public void closeFontColorPane(){
        if(subtitlesController.animating.get()) return;

        subtitlesController.subtitlesState = SubtitlesState.CAPTIONS_OPTIONS_OPEN;

        subtitlesController.subtitlesOptionsPane.scrollPane.setVisible(true);
        subtitlesController.subtitlesOptionsPane.scrollPane.setMouseTransparent(false);


        Timeline clipHeightTimeline = new Timeline();
        clipHeightTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(subtitlesController.clip.heightProperty(), subtitlesController.subtitlesOptionsPane.scrollPane.getHeight())));


        Timeline clipWidthTimeline = new Timeline();
        clipWidthTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(subtitlesController.clip.widthProperty(), subtitlesController.subtitlesOptionsPane.scrollPane.getWidth())));



        TranslateTransition fontColorTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), scrollPane);
        fontColorTransition.setFromX(0);
        fontColorTransition.setToX(scrollPane.getWidth());

        TranslateTransition captionsOptionsTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), subtitlesController.subtitlesOptionsPane.scrollPane);
        captionsOptionsTransition.setFromX(-scrollPane.getWidth());
        captionsOptionsTransition.setToX(0);


        ParallelTransition parallelTransition = new ParallelTransition(clipHeightTimeline, clipWidthTimeline, fontColorTransition, captionsOptionsTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            subtitlesController.animating.set(false);
            scrollPane.setVisible(false);
            scrollPane.setMouseTransparent(true);
            scrollPane.setTranslateX(0);
            subtitlesController.clip.setHeight(subtitlesController.subtitlesOptionsPane.scrollPane.getPrefHeight());
        });

        parallelTransition.play();
        subtitlesController.animating.set(true);
    }

    public void updateValue(Color color, String displayText){
        for(CheckTab checkTab : checkTabs){
            checkTab.checkIcon.setVisible(false);
        }

        subtitlesOptionsPane.fontColorTab.subText.setText(displayText);

        subtitlesController.subtitlesBox.currentTextColor.set(color);
        subtitlesController.mainController.pref.preferences.put(SubtitlesBox.SUBTITLES_TEXT_COLOR, color.toString());

        subtitlesController.subtitlesBox.showCaptions();
    }

    private void initializeValue(Color color, String displayText){
        subtitlesOptionsPane.fontColorTab.subText.setText(displayText);
        subtitlesController.subtitlesBox.currentTextColor.set(color);
    }

    public void setInitialValue(Color color){
        if(color.equals(Color.WHITE)) pressWhiteTab(true);
        else if(color.equals(Color.YELLOW)) pressYellowTab(true);
        else if(color.equals(Color.LIME)) pressGreenTab(true);
        else if(color.equals(Color.CYAN)) pressCyanTab(true);
        else if(color.equals(Color.BLUE)) pressBlueTab(true);
        else if(color.equals(Color.MAGENTA)) pressMagentaTab(true);
        else if(color.equals(Color.RED)) pressRedTab(true);
        else pressBlackTab(true);

    }


    public void pressWhiteTab(boolean initial){
        if(initial) initializeValue(Color.WHITE, "White");
        else updateValue(Color.WHITE, "White");

        whiteTab.checkIcon.setVisible(true);
    }

    public void pressYellowTab(boolean initial){
        if(initial) initializeValue(Color.YELLOW, "Yellow");
        else updateValue(Color.YELLOW, "Yellow");

        yellowTab.checkIcon.setVisible(true);
    }

    public void pressGreenTab(boolean initial){
        if(initial) initializeValue(Color.LIME, "Green");
        else updateValue(Color.LIME, "Green");

        greenTab.checkIcon.setVisible(true);
    }

    public void pressCyanTab(boolean initial){
        if(initial) initializeValue(Color.CYAN, "Cyan");
        else updateValue(Color.CYAN, "Cyan");

        cyanTab.checkIcon.setVisible(true);
    }

    public void pressBlueTab(boolean initial){
        if(initial) initializeValue(Color.BLUE, "Blue");
        else updateValue(Color.BLUE, "Blue");

        blueTab.checkIcon.setVisible(true);
    }

    public void pressMagentaTab(boolean initial){
        if(initial) initializeValue(Color.MAGENTA, "Magenta");
        else updateValue(Color.MAGENTA, "Magenta");

        magentaTab.checkIcon.setVisible(true);
    }

    public void pressRedTab(boolean initial){
        if(initial) initializeValue(Color.RED, "Red");
        else updateValue(Color.RED, "Red");

        redTab.checkIcon.setVisible(true);
    }

    public void pressBlackTab(boolean initial){
        if(initial) initializeValue(Color.BLACK, "Black");
        else updateValue(Color.BLACK, "Black");

        blackTab.checkIcon.setVisible(true);
    }
}
