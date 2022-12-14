package hans.Captions;

import hans.*;
import hans.Settings.SettingsController;
import hans.Settings.SettingsState;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;

public class CaptionsHome {

    CaptionsController captionsController;



    ScrollPane scrollPane = new ScrollPane();
    VBox captionsWrapper = new VBox();

    HBox captionsTitle = new HBox();
    HBox captionsChooserTab = new HBox();

    Label captionsTitleLabel = new Label();
    Label captionsOptionsLabel = new Label();

    Label chooseCaptionsLabel = new Label();
    
    StackPane chooseCaptionsIconPane = new StackPane();
    Region chooseCaptionsIcon = new Region();
    SVGPath searchSVG = new SVGPath();

    FileChooser fileChooser;

    ArrayList<CaptionsTab> captionsTabs = new ArrayList<>();

    public CaptionsHome(CaptionsController captionsController){
        this.captionsController = captionsController;

        searchSVG.setContent(App.svgMap.get(SVG.MAGNIFY));

        fileChooser = new FileChooser();
        fileChooser.setTitle("Select subtitles");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Subtitles", "*.srt"));


        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("settingsScroll");
        scrollPane.setPrefSize(245, 106);
        scrollPane.setMaxSize(245, 106);
        scrollPane.setContent(captionsWrapper);
        scrollPane.setVisible(false);
        scrollPane.setMouseTransparent(true);
        scrollPane.setFitToWidth(true);

        StackPane.setAlignment(scrollPane, Pos.BOTTOM_RIGHT);

        captionsWrapper.setMinSize(225, 103);
        captionsWrapper.setPrefSize(245, 103);
        captionsWrapper.setMaxSize(245, 103);
        captionsWrapper.setPadding(new Insets(8, 0, 8, 0));
        captionsWrapper.setAlignment(Pos.BOTTOM_LEFT);

        captionsWrapper.getChildren().addAll(captionsTitle, captionsChooserTab);

        captionsTitle.getChildren().addAll(captionsTitleLabel, captionsOptionsLabel);
        captionsTitle.setMinSize(225, 40);
        captionsTitle.setPrefSize(245, 40);
        captionsTitle.setMaxSize(245, 40);
        captionsTitle.setPadding(new Insets(0, 10, 0, 10));
        VBox.setMargin(captionsTitle, new Insets(0, 0, 10, 0));
        captionsTitle.getStyleClass().add("settingsPaneTitle");

        captionsTitleLabel.setMinSize(145, 40);
        captionsTitleLabel.setPrefSize(165, 40);
        captionsTitleLabel.setMaxSize(165, 40);
        captionsTitleLabel.setText("Subtitles/CC");
        captionsTitleLabel.getStyleClass().add("settingsPaneText");


        captionsOptionsLabel.getStyleClass().addAll("settingsPaneText", "settingsPaneSubText");
        captionsOptionsLabel.setText("Options");
        captionsOptionsLabel.setUnderline(true);
        captionsOptionsLabel.setMinSize(60, 40);
        captionsOptionsLabel.setPrefSize(60, 40);
        captionsOptionsLabel.setMaxSize(60, 40);
        captionsOptionsLabel.setCursor(Cursor.HAND);
        captionsOptionsLabel.setOnMouseClicked((e) -> openCaptionsOptions());
        captionsOptionsLabel.setAlignment(Pos.CENTER_RIGHT);


        captionsChooserTab.setMinSize(225, 35);
        captionsChooserTab.setPrefSize(245, 35);
        captionsChooserTab.setMaxSize(245, 35);
        captionsChooserTab.setPadding(new Insets(0, 10, 0, 10));
        captionsChooserTab.getStyleClass().add("settingsPaneTab");
        captionsChooserTab.getChildren().addAll(chooseCaptionsIconPane, chooseCaptionsLabel);
        captionsChooserTab.setOnMouseClicked(e -> openCaptionsChooser());

        chooseCaptionsIconPane.setMinSize(30, 35);
        chooseCaptionsIconPane.setPrefSize(30, 35);
        chooseCaptionsIconPane.setMaxSize(30, 35);
        chooseCaptionsIconPane.setPadding(new Insets(0, 5, 0, 0));
        chooseCaptionsIconPane.getChildren().add(chooseCaptionsIcon);

        chooseCaptionsIcon.setPrefSize(15, 15);
        chooseCaptionsIcon.setMaxSize(15, 15);
        chooseCaptionsIcon.setId("captionsSearchIcon");
        chooseCaptionsIcon.setShape(searchSVG);

        chooseCaptionsLabel.setText("Add subtitles");
        chooseCaptionsLabel.getStyleClass().add("settingsPaneText");
        chooseCaptionsLabel.setMinSize(175, 35);
        chooseCaptionsLabel.setPrefSize(195, 35);
        chooseCaptionsLabel.setMaxSize(195, 35);

        captionsController.captionsPane.getChildren().add(scrollPane);
    }



    public void openCaptionsOptions(){
        if(captionsController.animating.get()) return;

        captionsController.captionsState = CaptionsState.CAPTIONS_OPTIONS_OPEN;

        captionsController.captionsOptionsPane.scrollPane.setVisible(true);
        captionsController.captionsOptionsPane.scrollPane.setMouseTransparent(false);

        Timeline clipHeightTimeline = new Timeline();
        clipHeightTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.clip.heightProperty(), captionsController.captionsOptionsPane.scrollPane.getHeight())));


        Timeline clipWidthTimeline = new Timeline();
        clipWidthTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.clip.widthProperty(), captionsController.captionsOptionsPane.scrollPane.getWidth())));



        TranslateTransition captionsTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), scrollPane);
        captionsTransition.setFromX(0);
        captionsTransition.setToX(-captionsController.captionsOptionsPane.scrollPane.getWidth());

        TranslateTransition captionsOptionsTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), captionsController.captionsOptionsPane.scrollPane);
        captionsOptionsTransition.setFromX(captionsController.captionsOptionsPane.scrollPane.getWidth());
        captionsOptionsTransition.setToX(0);


        ParallelTransition parallelTransition = new ParallelTransition(clipHeightTimeline, clipWidthTimeline, captionsTransition, captionsOptionsTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            captionsController.animating.set(false);
            scrollPane.setVisible(false);
            scrollPane.setMouseTransparent(true);
            scrollPane.setTranslateX(0);
            scrollPane.setVvalue(0);
            captionsController.clip.setHeight(captionsController.captionsOptionsPane.scrollPane.getPrefHeight());
        });

        parallelTransition.play();
        captionsController.animating.set(true);
    }


    public void openCaptionsChooser(){
        if(captionsController.menuController.activeItem != null){
            fileChooser.setInitialDirectory(captionsController.menuController.activeItem.getMediaItem().getFile().getParentFile()); // search for subtitles inside the same directory as the current media item
        }
        else {
            fileChooser.setInitialDirectory(null);
        }
        File selectedFile = fileChooser.showOpenDialog(App.stage);

        if (selectedFile != null){
            createTab(selectedFile);
        }
    }

    public void createTab(File selectedFile){

        for(CaptionsTab captionsTab : captionsTabs){
            if(captionsTab.captionFile.getAbsolutePath().equals(selectedFile.getAbsolutePath())){
                captionsTab.selectSubtitles(false);
                return;
            }
        }

        CaptionsTab captionsTab = new CaptionsTab(captionsController, this, selectedFile.getName(), selectedFile, true);
        captionsWrapper.getChildren().add(2, captionsTab);
        captionsTabs.add(captionsTab);
        scrollPane.setVvalue(0);
        captionsTab.selectSubtitles(true);
    }
}
