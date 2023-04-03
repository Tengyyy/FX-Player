package hans.Menu;

import hans.App;
import hans.Menu.Queue.QueueItem;
import hans.SVG;
import hans.Shell32Util;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Region;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class QueueItemContextMenu extends ContextMenu {

    QueueItem queueItem;

    MenuItem playNext = new MenuItem("Play next");
    MenuItem metadata = new MenuItem("Media metadata");
    MenuItem technicalDetails = new MenuItem("Technical details");
    MenuItem openFileLocation = new MenuItem("Open file in location");

    double buttonWidth;
    final double popUpWidth = 186; // calling getWidth on this pop-up window is inaccurate as it sometimes incorrectly shows 151, hard-coded value is used to always get the same result

    FadeTransition showTransition, hideTransition;

    SVGPath playNextPath = new SVGPath(), metadataPath = new SVGPath(), technicalDetailsPath = new SVGPath(), folderPath = new SVGPath();
    Region playNextIcon = new Region(), metadataIcon = new Region(), technicalDetailsIcon = new Region(), folderIcon = new Region();

    public boolean showing = false;


    public QueueItemContextMenu(QueueItem queueItem){

        this.queueItem = queueItem;

        this.getStyleClass().add("menu-context-menu");

        queueItem.getScene().getStylesheets().add(Objects.requireNonNull(queueItem.getMenuController().mainController.getClass().getResource("styles/optionsPopUp.css")).toExternalForm());

        playNextPath.setContent(App.svgMap.get(SVG.PLAY_CIRCLE));
        playNextIcon.setShape(playNextPath);
        playNextIcon.getStyleClass().add("icon");
        playNextIcon.setPrefSize(14, 14);
        playNextIcon.setMaxSize(14, 14);

        playNext.setGraphic(playNextIcon);
        playNext.getStyleClass().add("popUpItem");
        playNext.setOnAction((e) -> queueItem.playNext());

        metadataPath.setContent(App.svgMap.get(SVG.INFORMATION_OUTLINE));
        metadataIcon.setShape(metadataPath);
        metadataIcon.getStyleClass().add("icon");
        metadataIcon.setPrefSize(14, 14);
        metadataIcon.setMaxSize(14, 14);

        metadata.setGraphic(metadataIcon);
        metadata.getStyleClass().add("popUpItem");
        metadata.setOnAction((e) -> queueItem.showMetadata());
        metadata.disableProperty().bind(queueItem.mediaItemGenerated.not());

        technicalDetailsPath.setContent(App.svgMap.get(SVG.COGS));
        technicalDetailsIcon.setShape(technicalDetailsPath);
        technicalDetailsIcon.getStyleClass().add("icon");
        technicalDetailsIcon.setPrefSize(14, 14);
        technicalDetailsIcon.setMaxSize(14, 14);

        technicalDetails.setGraphic(technicalDetailsIcon);
        technicalDetails.getStyleClass().add("popUpItem");
        technicalDetails.setOnAction((e) -> queueItem.showTechnicalDetails());
        technicalDetails.disableProperty().bind(queueItem.mediaItemGenerated.not());


        folderPath.setContent(App.svgMap.get(SVG.FOLDER));
        folderIcon.setShape(folderPath);
        folderIcon.getStyleClass().add("icon");
        folderIcon.setPrefSize(14, 14);
        folderIcon.setMaxSize(14, 14);

        openFileLocation.setGraphic(folderIcon);
        openFileLocation.getStyleClass().add("popUpItem");
        openFileLocation.setOnAction((e) -> openFileLocation(queueItem.getMediaItem().getMediaDetails().get("path")));


        this.getItems().addAll(playNext, metadata, technicalDetails, openFileLocation);

        buttonWidth = queueItem.getOptionsButton().getWidth();

        this.getStyleableNode().setOpacity(0);
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

        if(!queueItem.getHover()){
            queueItem.playButtonIcon.setVisible(false);
            queueItem.playButtonBackground.setVisible(false);
            queueItem.checkbox.setVisible(false);

            if(queueItem.isActive.get()) {
                queueItem.setStyle("-fx-background-color: rgba(50, 50, 50, 0.6);");
                queueItem.columns.setVisible(true);
            }
            else {
                queueItem.setStyle("-fx-background-color: transparent;");
                queueItem.indexLabel.setVisible(true);
            }
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
}