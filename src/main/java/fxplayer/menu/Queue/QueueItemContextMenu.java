package fxplayer.menu.Queue;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Region;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;
import fxplayer.App;
import fxplayer.mediaItems.MediaItem;
import fxplayer.SVG;
import fxplayer.Shell32Util;
import fxplayer.Utilities;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class QueueItemContextMenu extends ContextMenu {

    QueueItem queueItem;

    MenuItem playNext = new MenuItem("Play next");
    MenuItem mediaInformation = new MenuItem("Media information");
    MenuItem technicalDetails = new MenuItem("Technical details");
    MenuItem chapters = new MenuItem("View chapters");
    MenuItem openFileLocation = new MenuItem("Open file in location");

    double buttonWidth;
    final double popUpWidth = 186; // calling getWidth on this pop-up window is inaccurate as it sometimes incorrectly shows 151, hard-coded value is used to always get the same result

    FadeTransition showTransition, hideTransition;

    SVGPath playNextPath = new SVGPath(), infoPath = new SVGPath(), technicalDetailsPath = new SVGPath(), chapterPath = new SVGPath(), folderPath = new SVGPath();
    Region playNextIcon = new Region(), infoIcon = new Region(), technicalDetailsIcon = new Region(), chapterIcon = new Region(), folderIcon = new Region();

    public boolean showing = false;


    public QueueItemContextMenu(QueueItem queueItem){

        this.queueItem = queueItem;

        this.getStyleClass().add("menu-context-menu");

        queueItem.getScene().getStylesheets().add(Objects.requireNonNull(queueItem.getMenuController().mainController.getClass().getResource("styles/optionsPopUp.css")).toExternalForm());

        playNextPath.setContent(SVG.PLAY_CIRCLE.getContent());
        playNextIcon.setShape(playNextPath);
        playNextIcon.getStyleClass().add("icon");
        playNextIcon.setPrefSize(14, 14);
        playNextIcon.setMaxSize(14, 14);

        playNext.setGraphic(playNextIcon);
        playNext.getStyleClass().add("popUpItem");
        playNext.setOnAction((e) -> queueItem.playNext());

        infoPath.setContent(SVG.INFORMATION_OUTLINE.getContent());
        infoIcon.setShape(infoPath);
        infoIcon.getStyleClass().add("icon");
        infoIcon.setPrefSize(14, 14);
        infoIcon.setMaxSize(14, 14);

        mediaInformation.setGraphic(infoIcon);
        mediaInformation.getStyleClass().add("popUpItem");
        mediaInformation.setOnAction((e) -> queueItem.showMetadata());
        mediaInformation.disableProperty().bind(queueItem.mediaItemGenerated.not());

        technicalDetailsPath.setContent(SVG.COGS.getContent());
        technicalDetailsIcon.setShape(technicalDetailsPath);
        technicalDetailsIcon.getStyleClass().add("icon");
        technicalDetailsIcon.setPrefSize(14, 14);
        technicalDetailsIcon.setMaxSize(14, 14);

        technicalDetails.setGraphic(technicalDetailsIcon);
        technicalDetails.getStyleClass().add("popUpItem");
        technicalDetails.setOnAction((e) -> queueItem.showTechnicalDetails());
        technicalDetails.disableProperty().bind(queueItem.mediaItemGenerated.not());



        chapterPath.setContent(SVG.CHAPTER.getContent());
        chapterIcon.setShape(chapterPath);
        chapterIcon.getStyleClass().add("icon");
        chapterIcon.setPrefSize(14, 14);
        chapterIcon.setMaxSize(14, 14);

        chapters.setGraphic(chapterIcon);
        chapters.getStyleClass().add("popUpItem");
        chapters.setOnAction(e -> queueItem.showChapters());


        folderPath.setContent(SVG.FOLDER.getContent());
        folderIcon.setShape(folderPath);
        folderIcon.getStyleClass().add("icon");
        folderIcon.setPrefSize(14, 14);
        folderIcon.setMaxSize(14, 14);

        openFileLocation.setGraphic(folderIcon);
        openFileLocation.getStyleClass().add("popUpItem");
        openFileLocation.setOnAction((e) -> {
            if(queueItem.getMediaItem() != null) openFileLocation(queueItem.getMediaItem().getFile().getAbsolutePath());
        });


        this.getItems().addAll(playNext, mediaInformation, technicalDetails, openFileLocation);

        buttonWidth = queueItem.getOptionsButton().getWidth();

        this.getStyleableNode().setOpacity(0);

        if(queueItem.mediaItemGenerated.get()) loadChapterItem(queueItem.getMediaItem());
        else {
            queueItem.mediaItemGenerated.addListener((observableValue, oldValue, newValue) -> {
                if(newValue && queueItem.getMediaItem() != null) loadChapterItem(queueItem.getMediaItem());
            });
        }
    }


    public void showOptions(boolean animate){
        this.show(queueItem, // might not work
                queueItem.getOptionsButton().localToScreen(queueItem.getOptionsButton().getBoundsInLocal()).getMinX() + buttonWidth/2 - popUpWidth/2,
                queueItem.getOptionsButton().localToScreen(queueItem.getOptionsButton().getBoundsInLocal()).getMaxY() + 5, animate);
    }


    public void show(Node node, double v, double v1, boolean animate) {

        if(hideTransition != null && hideTransition.getStatus() == Animation.Status.RUNNING) hideTransition.stop();

        if(animate) this.getStyleableNode().setOpacity(0);
        else this.getStyleableNode().setOpacity(1);

        super.show(node, v, v1);
        showing = true;

        if(animate){
            showTransition = new FadeTransition(Duration.millis(150), this.getStyleableNode());
            showTransition.setFromValue(0);
            showTransition.setToValue(1);
            showTransition.playFromStart();
        }

        queueItem.queuePage.activeQueueItemContextMenu = this;
    }

    @Override
    public void hide() {

        showing = false;

        if(!queueItem.mouseHover && queueItem.focus.get() == -1 && !queueItem.queuePage.selectionActive.get()){

            queueItem.checkbox.setVisible(false);

            if(queueItem.isActive.get()) queueItem.columns.setVisible(true);
            else queueItem.indexLabel.setVisible(true);
        }

        if(showTransition != null && showTransition.getStatus() == Animation.Status.RUNNING) showTransition.stop();

        hideTransition = new FadeTransition(Duration.millis(150), this.getStyleableNode());
        hideTransition.setFromValue(1);
        hideTransition.setToValue(0);
        hideTransition.setOnFinished(e -> super.hide());
        hideTransition.playFromStart();

    }

    public void openFileLocation(String path){
        if(App.isWindows){
            Shell32Util.SHOpenFolderAndSelectItems(new File(path));
        }
        else if(Desktop.isDesktopSupported()){
            Desktop desktop = Desktop.getDesktop();
            File file = new File(path);

            if(desktop.isSupported(Desktop.Action.BROWSE_FILE_DIR)){
                desktop.browseFileDirectory(new File(path));
            }
            else if(desktop.isSupported(Desktop.Action.OPEN)){
                try {
                    desktop.open(file.getParentFile());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void loadChapterItem(MediaItem mediaItem){
        if(this.getItems().contains(chapters)) return;

        String extension = Utilities.getFileExtension(mediaItem.getFile());
        if(!extension.equals("mp4")
                && !extension.equals("mkv")
                && !extension.equals("mov")
                && !extension.equals("mp3")
                && !extension.equals("opus")
                && !extension.equals("m4a")
                && !extension.equals("wma")
                && !extension.equals("wmv")
                && !extension.equals("mka")
                && !extension.equals("webm")) return;

        this.getItems().add(3, chapters);
    }
}