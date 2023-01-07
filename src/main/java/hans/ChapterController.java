package hans;

import hans.Menu.MenuController;
import uk.co.caprica.vlcj.player.base.ChapterDescription;

import java.util.List;

public class ChapterController {

    MainController mainController;
    ControlBarController controlBarController;
    MenuController menuController;
    MediaInterface mediaInterface;

    List<ChapterDescription> chapterDescriptions = null;

    int activeChapter = -1;

    ChapterController(MainController mainController, ControlBarController controlBarController, MenuController menuController, MediaInterface mediaInterface){
        this.mainController = mainController;
        this.controlBarController = controlBarController;
        this.menuController = menuController;
        this.mediaInterface = mediaInterface;



    }

    public void initializeChapters(List<ChapterDescription> chapterDescriptions){
        this.chapterDescriptions = chapterDescriptions;

        controlBarController.trackContainer.getChildren().clear();
        controlBarController.durationTracks.clear();

        if(chapterDescriptions.size() == 1){
            ChapterDescription chapterDescription = chapterDescriptions.get(0);
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
        controlBarController.chapterLabel.setText("");
    }

    public void setActiveChapter(int newChapter){
        if(chapterDescriptions != null){
            this.activeChapter = newChapter;
            ChapterDescription chapterDescription = chapterDescriptions.get(newChapter);
            controlBarController.chapterLabel.setText(" • " + chapterDescription.name());
        }
    }
}
