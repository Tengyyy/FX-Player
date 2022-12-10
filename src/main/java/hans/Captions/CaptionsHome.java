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

    HBox toggleBox = new HBox();
    Label toggleLabel = new Label();
    public MFXToggleButton captionsToggle = new MFXToggleButton();


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
        scrollPane.setPrefSize(245, 162);
        scrollPane.setMaxSize(245, 162);
        scrollPane.setContent(captionsWrapper);
        scrollPane.setVisible(false);
        scrollPane.setMouseTransparent(true);

        StackPane.setAlignment(scrollPane, Pos.BOTTOM_RIGHT);

        captionsWrapper.setPrefSize(235, 159);
        captionsWrapper.setMaxSize(235, 159);
        captionsWrapper.setPadding(new Insets(8, 0, 8, 0));
        captionsWrapper.setAlignment(Pos.BOTTOM_LEFT);
        StackPane.setAlignment(captionsWrapper, Pos.BOTTOM_RIGHT);

        captionsWrapper.getChildren().addAll(captionsTitle, captionsChooserTab, toggleBox);

        captionsTitle.getChildren().addAll(captionsTitleLabel, captionsOptionsLabel);
        captionsTitle.setMinSize(235, 40);
        captionsTitle.setPrefSize(235, 40);
        captionsTitle.setMaxSize(235, 40);
        captionsTitle.setPadding(new Insets(0, 10, 0, 10));
        VBox.setMargin(captionsTitle, new Insets(0, 0, 10, 0));
        captionsTitle.getStyleClass().add("settingsPaneTitle");

        captionsTitleLabel.setMinHeight(40);
        captionsTitleLabel.setPrefHeight(40);
        captionsTitleLabel.setMaxHeight(40);
        captionsTitleLabel.setPrefWidth(124);
        captionsTitleLabel.setText("Subtitles/CC");
        captionsTitleLabel.getStyleClass().add("settingsPaneText");


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
        captionsChooserTab.getChildren().addAll(chooseCaptionsIconPane, chooseCaptionsLabel);
        captionsChooserTab.setOnMouseClicked(e -> openCaptionsChooser());


        chooseCaptionsIconPane.setMinSize(30, 35);
        chooseCaptionsIconPane.setPrefSize(30, 35);
        chooseCaptionsIconPane.setMaxSize(30, 35);
        chooseCaptionsIconPane.setPadding(new Insets(0, 5, 0, 0));
        chooseCaptionsIconPane.getChildren().add(chooseCaptionsIcon);

        chooseCaptionsIcon.setMinSize(15, 15);
        chooseCaptionsIcon.setPrefSize(15, 15);
        chooseCaptionsIcon.setMaxSize(15, 15);
        chooseCaptionsIcon.setId("captionsSearchIcon");
        chooseCaptionsIcon.setShape(searchSVG);

        chooseCaptionsLabel.setText("Add subtitles");
        chooseCaptionsLabel.getStyleClass().add("settingsPaneText");
        chooseCaptionsLabel.setMinSize(190, 35);
        chooseCaptionsLabel.setPrefSize(190, 35);
        chooseCaptionsLabel.setMaxSize(190,35);

        toggleBox.setMinSize(235, 56);
        toggleBox.setPrefSize(235, 56);
        toggleBox.setMaxSize(235,56);
        toggleBox.setPadding(new Insets(15, 10, 0, 10));
        toggleBox.getChildren().addAll(toggleLabel, captionsToggle);
        toggleBox.setAlignment(Pos.CENTER_RIGHT);


        toggleLabel.setText("Toggle subtitles");
        toggleLabel.getStyleClass().add("settingsPaneText");

        captionsToggle.setLength(38);
        captionsToggle.setRadius(10);
        captionsToggle.setDisable(true);
        captionsToggle.setCursor(Cursor.HAND);
        captionsToggle.setContentDisposition(ContentDisplay.RIGHT);
        captionsToggle.selectedProperty().addListener((observableValue, oldValue, newValue) -> captionsController.captionsOn.set(newValue));

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
        CaptionsTab captionsTab = new CaptionsTab(captionsController, this, selectedFile.getName(), selectedFile, true);
        captionsWrapper.getChildren().add(captionsWrapper.getChildren().size() -2, captionsTab);
        captionsTabs.add(captionsTab);
        captionsTab.selectSubtitles();
    }
}
