package hans;

import hans.Menu.MenuController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import uk.co.caprica.vlcj.player.base.ChapterDescription;

import java.util.List;

public class ChapterController {

    MainController mainController;
    ControlBarController controlBarController;
    MenuController menuController;
    MediaInterface mediaInterface;

    List<ChapterDescription> chapterDescriptions = null;

    int activeChapter = -1;

    Label separatorLabel = new Label("•");

    StackPane chevronPane = new StackPane();
    Region chevronIcon = new Region();
    SVGPath chevronSVG = new SVGPath();


    HBox chapterLabelWrapper = new HBox();
    HBox chapterLabelBox = new HBox();
    Label chapterLabel = new Label();

    ChapterController(MainController mainController, ControlBarController controlBarController, MenuController menuController, MediaInterface mediaInterface){
        this.mainController = mainController;
        this.controlBarController = controlBarController;
        this.menuController = menuController;
        this.mediaInterface = mediaInterface;

        chapterLabelWrapper.setPrefHeight(30);
        chapterLabelWrapper.setMinWidth(0);
        HBox.setHgrow(chapterLabelWrapper, Priority.SOMETIMES);
        chapterLabelWrapper.getChildren().addAll(separatorLabel, chapterLabelBox);

        separatorLabel.getStyleClass().add("controlBarLabel");
        separatorLabel.setPadding(new Insets(0, 3, 0, 5));
        separatorLabel.setPrefHeight(30);

        chapterLabelBox.setPrefHeight(30);
        chapterLabelBox.setMinWidth(0);
        HBox.setHgrow(chapterLabelBox, Priority.SOMETIMES);
        chapterLabelBox.getChildren().addAll(chapterLabel, chevronPane);
        chapterLabelBox.setCursor(Cursor.HAND);

        chapterLabelBox.setOnMouseEntered(e -> {
                AnimationsClass.animateTextColor(chapterLabel, Color.WHITE, 200);
                AnimationsClass.animateBackgroundColor(chevronIcon, Color.rgb(200, 200, 200), Color.rgb(255, 255, 255), 200);
        });

        chapterLabelBox.setOnMouseExited(e -> {
            AnimationsClass.animateTextColor(chapterLabel, Color.rgb(200, 200, 200), 200);
            AnimationsClass.animateBackgroundColor(chevronIcon, Color.rgb(255, 255, 255), Color.rgb(200, 200, 200), 200);
        });

        chapterLabel.setPrefHeight(30);
        chapterLabel.getStyleClass().add("controlBarLabel");

        chevronSVG.setContent(App.svgMap.get(SVG.CHEVRON_RIGHT));

        chevronPane.setPrefSize(10, 30);
        chevronPane.setMaxSize(10, 30);
        chevronPane.getChildren().add(chevronIcon);
        chevronPane.setMouseTransparent(true);
        chevronPane.setAlignment(Pos.CENTER_RIGHT);

        chevronIcon.setMinSize(8, 15);
        chevronIcon.setPrefSize(8, 15);
        chevronIcon.setMaxSize(8, 15);
        chevronIcon.setShape(chevronSVG);
        chevronIcon.getStyleClass().add("controlIcon");

    }

    public void initializeChapters(List<ChapterDescription> chapterDescriptions){
        this.chapterDescriptions = chapterDescriptions;

        controlBarController.trackContainer.getChildren().clear();
        controlBarController.durationTracks.clear();

        if(chapterDescriptions.size() == 1){
            DurationTrack durationTrack = new DurationTrack(0, 1);
            controlBarController.durationTracks.add(durationTrack);
            controlBarController.trackContainer.getChildren().add(durationTrack.progressBar);
        }
        else {
            double lastChapterEnd = 0;
            for(int i=0; i < chapterDescriptions.size(); i++){
                ChapterDescription chapterDescription = chapterDescriptions.get(i);

                double endTime;
                if(i == chapterDescriptions.size() -1) endTime = 1;
                else endTime = Math.min((lastChapterEnd + chapterDescription.duration())/(1000 * controlBarController.durationSlider.getMax()), 1);

                DurationTrack durationTrack = new DurationTrack(lastChapterEnd, endTime);
                durationTrack.bindWidth(controlBarController.trackContainer, Math.max(0, endTime - lastChapterEnd));

                controlBarController.durationTracks.add(durationTrack);
                controlBarController.trackContainer.getChildren().add(durationTrack.progressBar);

                lastChapterEnd = endTime;

            }
        }

        if(!controlBarController.labelBox.getChildren().contains(chapterLabelWrapper)) controlBarController.labelBox.getChildren().add(chapterLabelWrapper);

        setActiveChapter(0);
    }

    public void resetChapters(){
        chapterDescriptions = null;
        controlBarController.durationTracks.clear();
        controlBarController.hoverTrack = null;
        controlBarController.activeTrack = null;
        controlBarController.trackContainer.getChildren().clear();
        controlBarController.trackContainer.getChildren().add(controlBarController.defaultTrack.progressBar);
        activeChapter = -1;
        chapterLabel.setText("");
        controlBarController.labelBox.getChildren().remove(chapterLabelWrapper);
    }

    public void setActiveChapter(int newChapter){
        if(chapterDescriptions != null){
            this.activeChapter = newChapter;
            ChapterDescription chapterDescription = chapterDescriptions.get(newChapter);
            chapterLabel.setText(chapterDescription.name());
        }
    }
}
