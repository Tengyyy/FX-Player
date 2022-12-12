package hans.Menu;

import hans.App;
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

public class MenuItemContextMenu extends ContextMenu {

    MenuObject menuObject;

    MenuItem playNext = new MenuItem("Play next");
    MenuItem metadata = new MenuItem("Media metadata");
    MenuItem technicalDetails = new MenuItem("Technical details");
    MenuItem openFileLocation = new MenuItem("Open file in location");

    double buttonWidth;
    final double popUpWidth = 214; // calling getWidth on this pop-up window is inaccurate as it sometimes incorrectly shows 151, hard-coded value is used to always get the same result

    Node menuObjectNode;

    FadeTransition showTransition, hideTransition;

    SVGPath playNextPath = new SVGPath(), metadataPath = new SVGPath(), technicalDetailsPath = new SVGPath(), folderPath = new SVGPath();
    Region playNextIcon = new Region(), metadataIcon = new Region(), technicalDetailsIcon = new Region(), folderIcon = new Region();

    boolean isHistoryItem = false;
    boolean isActiveItem = false;

    boolean showing = false;

    MenuItemContextMenu(MenuObject menuObject){

        this.menuObject = menuObject;


        if (menuObject instanceof HistoryItem) isHistoryItem = true;
        else if(menuObject instanceof ActiveItem) isActiveItem = true;


        menuObjectNode = (Node) menuObject;


        menuObjectNode.getScene().getStylesheets().add(Objects.requireNonNull(menuObject.getMenuController().mainController.getClass().getResource("styles/optionsPopUp.css")).toExternalForm());


        playNextPath.setContent(App.svgMap.get(SVG.PLAY_CIRCLE));
        playNextIcon.setShape(playNextPath);
        playNextIcon.getStyleClass().add("icon");
        playNextIcon.setPrefSize(20, 20);
        playNextIcon.setMaxSize(20, 20);

        playNext.setGraphic(playNextIcon);
        playNext.getStyleClass().add("popUpItem");
        playNext.setOnAction((e) -> {
            if(!menuObject.getMenuController().animationsInProgress.isEmpty()) return;
            menuObject.playNext();
        });

        metadataPath.setContent(App.svgMap.get(SVG.INFORMATION_OUTLINE));
        metadataIcon.setShape(metadataPath);
        metadataIcon.getStyleClass().add("icon");
        metadataIcon.setPrefSize(20, 20);
        metadataIcon.setMaxSize(20, 20);

        metadata.setGraphic(metadataIcon);
        metadata.getStyleClass().add("popUpItem");
        metadata.setOnAction((e) -> menuObject.showMetadata());

        technicalDetailsPath.setContent(App.svgMap.get(SVG.COGS));
        technicalDetailsIcon.setShape(technicalDetailsPath);
        technicalDetailsIcon.getStyleClass().add("icon");
        technicalDetailsIcon.setPrefSize(20, 20);
        technicalDetailsIcon.setMaxSize(20, 20);

        technicalDetails.setGraphic(technicalDetailsIcon);
        technicalDetails.getStyleClass().add("popUpItem");
        technicalDetails.setOnAction((e) -> menuObject.showTechnicalDetails());

        folderPath.setContent(App.svgMap.get(SVG.FOLDER));
        folderIcon.setShape(folderPath);
        folderIcon.getStyleClass().add("icon");
        folderIcon.setPrefSize(20, 20);
        folderIcon.setMaxSize(20, 20);

        openFileLocation.setGraphic(folderIcon);
        openFileLocation.getStyleClass().add("popUpItem");
        openFileLocation.setOnAction((e) -> openFileLocation(menuObject.getMediaItem().getMediaDetails().get("path")));


        this.getItems().addAll(playNext, metadata, technicalDetails, openFileLocation);

        buttonWidth = menuObject.getOptionsButton().getWidth();

        this.getStyleableNode().setOpacity(0);
    }


    public void showOptions(boolean animate){
        this.show(menuObjectNode, // might not work
                menuObject.getOptionsButton().localToScreen(menuObject.getOptionsButton().getBoundsInLocal()).getMinX() + buttonWidth/2 - popUpWidth/2,
                menuObject.getOptionsButton().localToScreen(menuObject.getOptionsButton().getBoundsInLocal()).getMaxY() + 5, animate);
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

        menuObject.getMenuController().activeMenuItemContextMenu = this;
    }

    @Override
    public void hide() {

        showing = false;

        if(!menuObject.getHover()){
            if(isHistoryItem){
                HistoryItem historyItem = (HistoryItem) menuObject;
                if (historyItem.isActive.get()) menuObjectNode.setStyle("-fx-background-color: rgba(50,50,50,0.6);");
                else menuObjectNode.setStyle("-fx-background-color: transparent;");

                historyItem.playIcon.setVisible(false);
            }
            else if(isActiveItem){
                ActiveItem activeItem = (ActiveItem) menuObject;
                activeItem.playIcon.setVisible(false);
                activeItem.iconBackground.setVisible(false);

                activeItem.setStyle("-fx-background-color: transparent;");
            }
            else {
                QueueItem queueItem = (QueueItem) menuObjectNode;
                queueItem.setStyle("-fx-background-color: transparent;");
                queueItem.playIcon.setVisible(false);
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