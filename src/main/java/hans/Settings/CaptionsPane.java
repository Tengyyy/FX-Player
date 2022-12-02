package hans.Settings;

import hans.*;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;

public class CaptionsPane {

    CaptionsController captionsController;



    VBox captionsBox = new VBox();

    HBox captionsTitle = new HBox();
    HBox captionsChooserTab = new HBox();
    public VBox currentCaptionsTab = new VBox();

    HBox toggleBox = new HBox();
    Label toggleLabel = new Label();
    public MFXToggleButton captionsToggle = new MFXToggleButton();

    StackPane captionsBackPane = new StackPane();
    Region captionsBackIcon = new Region();
    SVGPath backSVG = new SVGPath();

    Label captionsTitleLabel = new Label();
    Label captionsOptionsLabel = new Label();


    Label chooseCaptionsLabel = new Label();

    public Label currentCaptionsLabel = new Label();
    public Label currentCaptionsNameLabel = new Label();

    StackPane chooseCaptionsIconPane = new StackPane();
    Region chooseCaptionsIcon = new Region();
    SVGPath searchSVG = new SVGPath();

    FileChooser fileChooser;

    public CaptionsPane(CaptionsController captionsController){
        this.captionsController = captionsController;

        backSVG.setContent(App.svgMap.get(SVG.CHEVRON_LEFT));
        searchSVG.setContent(App.svgMap.get(SVG.MAGNIFY));

        fileChooser = new FileChooser();
        fileChooser.setTitle("Select subtitles");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Subtitles", "*.srt"));

        captionsBox.setPrefSize(235, 214);
        captionsBox.setMaxSize(235, 214);
        captionsBox.setPadding(new Insets(8, 0, 8, 0));
        captionsBox.setAlignment(Pos.BOTTOM_CENTER);
        captionsBox.setVisible(false);
        captionsBox.setMouseTransparent(true);
        StackPane.setAlignment(captionsBox, Pos.BOTTOM_RIGHT);

        captionsBox.getChildren().addAll(captionsTitle, captionsChooserTab, currentCaptionsTab, toggleBox);

        captionsTitle.getChildren().addAll(captionsBackPane, captionsTitleLabel, captionsOptionsLabel);
        captionsTitle.setMinSize(235, 40);
        captionsTitle.setPrefSize(235, 40);
        captionsTitle.setMaxSize(235, 40);
        captionsTitle.setPadding(new Insets(0, 10, 0, 10));
        VBox.setMargin(captionsTitle, new Insets(0, 0, 10, 0));
        captionsTitle.getStyleClass().add("settingsPaneTitle");

        captionsBackPane.setMinSize(24, 40);
        captionsBackPane.setPrefSize(24, 40);
        captionsBackPane.setMaxSize(24, 40);
        captionsBackPane.getChildren().add(captionsBackIcon);
        captionsBackPane.setCursor(Cursor.HAND);
        captionsBackPane.setOnMouseClicked((e) -> closeCaptionsPane());

        captionsBackIcon.setMinSize(8, 13);
        captionsBackIcon.setPrefSize(8, 13);
        captionsBackIcon.setMaxSize(8, 13);
        captionsBackIcon.getStyleClass().add("settingsPaneIcon");
        captionsBackIcon.setShape(backSVG);

        captionsTitleLabel.setMinHeight(40);
        captionsTitleLabel.setPrefHeight(40);
        captionsTitleLabel.setMaxHeight(40);
        captionsTitleLabel.setPrefWidth(100);
        captionsTitleLabel.setText("Subtitles/CC");
        captionsTitleLabel.setCursor(Cursor.HAND);
        captionsTitleLabel.getStyleClass().add("settingsPaneText");
        captionsTitleLabel.setOnMouseClicked((e) -> closeCaptionsPane());


        captionsOptionsLabel.getStyleClass().addAll("settingsPaneText", "settingsPaneSubText");
        captionsOptionsLabel.setText("Options");
        captionsOptionsLabel.setUnderline(true);
        captionsOptionsLabel.setMinHeight(40);
        captionsOptionsLabel.setPrefHeight(40);
        captionsOptionsLabel.setMaxHeight(40);
        captionsOptionsLabel.setPrefWidth(60);
        captionsOptionsLabel.setCursor(Cursor.HAND);
        captionsOptionsLabel.setOnMouseClicked((e) -> openCaptionsOptions());
        captionsOptionsLabel.setAlignment(Pos.CENTER_RIGHT);
        HBox.setMargin(captionsOptionsLabel, new Insets(0, 0, 0, 31));



        captionsChooserTab.setMinSize(235, 35);
        captionsChooserTab.setPrefSize(235, 35);
        captionsChooserTab.setMaxSize(235, 35);
        captionsChooserTab.setPadding(new Insets(0, 10, 0, 10));
        captionsChooserTab.getStyleClass().add("settingsPaneTab");
        captionsChooserTab.setCursor(Cursor.HAND);
        captionsChooserTab.getChildren().addAll(chooseCaptionsIconPane, chooseCaptionsLabel);
        captionsChooserTab.setOnMouseClicked(e -> openCaptionsChooser());


        chooseCaptionsIconPane.setMinSize(25, 35);
        chooseCaptionsIconPane.setPrefSize(25, 35);
        chooseCaptionsIconPane.setMaxSize(25, 35);
        chooseCaptionsIconPane.setPadding(new Insets(0, 5, 0, 0));
        chooseCaptionsIconPane.getChildren().add(chooseCaptionsIcon);

        chooseCaptionsIcon.setMinSize(15, 15);
        chooseCaptionsIcon.setPrefSize(15, 15);
        chooseCaptionsIcon.setMaxSize(15, 15);
        chooseCaptionsIcon.setId("captionsSearchIcon");
        chooseCaptionsIcon.setShape(searchSVG);

        chooseCaptionsLabel.setText("Select subtitles");
        chooseCaptionsLabel.getStyleClass().add("settingsPaneText");
        chooseCaptionsLabel.setMinSize(190, 35);
        chooseCaptionsLabel.setPrefSize(190, 35);
        chooseCaptionsLabel.setMaxSize(190,35);

        currentCaptionsTab.setMinSize(235, 70);
        currentCaptionsTab.setPrefSize(235, 70);
        currentCaptionsTab.setMaxSize(235, 70);
        currentCaptionsTab.setPadding(new Insets(0, 10, 0, 10));
        currentCaptionsTab.setId("captionsFileTab");
        currentCaptionsTab.getChildren().add(currentCaptionsLabel);
        currentCaptionsTab.setAlignment(Pos.TOP_CENTER);
        currentCaptionsTab.setPadding(new Insets(10, 0, 0, 0));

        currentCaptionsLabel.setMinSize(215, 20);
        currentCaptionsLabel.setPrefSize(215, 20);
        currentCaptionsLabel.setMaxSize(215, 20);
        currentCaptionsLabel.setText("No subtitles active");
        currentCaptionsLabel.getStyleClass().add("settingsPaneText");


        currentCaptionsNameLabel.setPrefSize(215, 20);
        currentCaptionsNameLabel.setMaxHeight(40);
        currentCaptionsNameLabel.getStyleClass().addAll("settingsPaneText","settingsPaneSubText");
        currentCaptionsNameLabel.setWrapText(true);


        toggleBox.setMinSize(235, 41);
        toggleBox.setPrefSize(235, 41);
        toggleBox.setMaxSize(235,41);
        toggleBox.setPadding(new Insets(0, 10, 0, 10));
        toggleBox.getChildren().addAll(toggleLabel, captionsToggle);
        toggleBox.setAlignment(Pos.CENTER_RIGHT);


        toggleLabel.setText("Toggle subtitles");
        toggleLabel.getStyleClass().add("settingsPaneText");

        captionsToggle.setLength(38);
        captionsToggle.setRadius(10);
        captionsToggle.setDisable(true);
        captionsToggle.setCursor(Cursor.HAND);
        captionsToggle.setContentDisposition(ContentDisplay.RIGHT);
        captionsToggle.setOnAction(e -> {
            if(captionsToggle.isSelected()){
                captionsController.controlBarController.openCaptions();
            }
            else {
                captionsController.controlBarController.closeCaptions();
            }
        });

        captionsController.settingsController.settingsPane.getChildren().add(captionsBox);
    }


    public void closeCaptionsPane(){
        if(captionsController.settingsController.animating.get()) return;

        captionsController.settingsController.settingsState = SettingsState.HOME_OPEN;

        captionsController.settingsController.settingsHomeController.settingsHome.setVisible(true);
        captionsController.settingsController.settingsHomeController.settingsHome.setMouseTransparent(false);

        Timeline clipTimeline = new Timeline();
        clipTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.settingsController.clip.heightProperty(), captionsController.settingsController.settingsHomeController.settingsHome.getHeight())));

        TranslateTransition homeTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), captionsController.settingsController.settingsHomeController.settingsHome);
        homeTransition.setFromX(-captionsController.settingsController.settingsHomeController.settingsHome.getWidth());
        homeTransition.setToX(0);

        TranslateTransition captionsTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), captionsBox);
        captionsTransition.setFromX(0);
        captionsTransition.setToX(captionsController.settingsController.settingsHomeController.settingsHome.getWidth());


        ParallelTransition parallelTransition = new ParallelTransition(clipTimeline, homeTransition, captionsTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            captionsController.settingsController.animating.set(false);
            captionsBox.setVisible(false);
            captionsBox.setMouseTransparent(true);
        });

        parallelTransition.play();
        captionsController.settingsController.animating.set(true);

    }


    public void openCaptionsOptions(){
        if(captionsController.settingsController.animating.get()) return;

        captionsController.settingsController.settingsState = SettingsState.CAPTIONS_OPTIONS_OPEN;

        captionsController.captionsOptionsPane.scrollPane.setVisible(true);
        captionsController.captionsOptionsPane.scrollPane.setMouseTransparent(false);

        Timeline clipHeightTimeline = new Timeline();
        clipHeightTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.settingsController.clip.heightProperty(), captionsController.captionsOptionsPane.scrollPane.getHeight())));


        Timeline clipWidthTimeline = new Timeline();
        clipWidthTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.settingsController.clip.widthProperty(), captionsController.captionsOptionsPane.scrollPane.getWidth())));



        TranslateTransition captionsTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), captionsBox);
        captionsTransition.setFromX(0);
        captionsTransition.setToX(-captionsController.captionsOptionsPane.scrollPane.getWidth());

        TranslateTransition captionsOptionsTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), captionsController.captionsOptionsPane.scrollPane);
        captionsOptionsTransition.setFromX(captionsController.captionsOptionsPane.scrollPane.getWidth());
        captionsOptionsTransition.setToX(0);


        ParallelTransition parallelTransition = new ParallelTransition(clipHeightTimeline, clipWidthTimeline, captionsTransition, captionsOptionsTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            captionsController.settingsController.animating.set(false);
            captionsBox.setVisible(false);
            captionsBox.setMouseTransparent(true);
            captionsBox.setTranslateX(0);
            captionsController.settingsController.clip.setHeight(captionsController.captionsOptionsPane.scrollPane.getPrefHeight());
        });

        parallelTransition.play();
        captionsController.settingsController.animating.set(true);
    }


    public void openCaptionsChooser(){
        if(captionsController.menuController.activeItem != null){
            fileChooser.setInitialDirectory(captionsController.menuController.activeItem.getMediaItem().getFile().getParentFile()); // search for subtitles inside the same directory as the current media item
        }
        else {
            fileChooser.setInitialDirectory(null);
        }
        File selectedFile = fileChooser.showOpenDialog(App.stage);

        if (selectedFile != null) captionsController.loadCaptions(selectedFile, true);
    }
}
