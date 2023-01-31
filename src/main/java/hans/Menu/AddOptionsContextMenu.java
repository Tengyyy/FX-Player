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

public class AddOptionsContextMenu extends ContextMenu {

    MenuController menuController;

    MenuItem fileItem = new MenuItem("Add file(s) to play queue");
    MenuItem folderItem = new MenuItem("Add folder to play queue");
    MenuItem youtubeItem = new MenuItem("Add youtube video to play queue");

    double buttonWidth;

    final double popUpWidth = 292; // calling getWidth on this pop-up window is inaccurate as it sometimes incorrectly shows 151, hard-coded value is used to always get the same result

    FadeTransition showTransition, hideTransition;

    SVGPath filePath = new SVGPath(), folderPath = new SVGPath(), youtubePath = new SVGPath();
    Region fileIcon = new Region(), folderIcon = new Region(), youtubeIcon = new Region();

    boolean showing = false;

    AddOptionsContextMenu(MenuController menuController){

        this.menuController = menuController;

        this.getStyleClass().add("queue-context-menu");

        menuController.addButtonContainer.getScene().getStylesheets().add(Objects.requireNonNull(menuController.mainController.getClass().getResource("styles/optionsPopUp.css")).toExternalForm());

        filePath.setContent(App.svgMap.get(SVG.FOLDER));
        fileIcon.setShape(filePath);
        fileIcon.getStyleClass().add("icon");
        fileIcon.setPrefSize(20, 20);
        fileIcon.setMaxSize(20, 20);

        fileItem.setGraphic(fileIcon);
        fileItem.getStyleClass().add("popUpItem");
        fileItem.setOnAction((e) -> menuController.openVideoChooser());

        folderPath.setContent(App.svgMap.get(SVG.FOLDER_PLUS));
        folderIcon.setShape(folderPath);
        folderIcon.getStyleClass().add("icon");
        folderIcon.setPrefSize(20, 20);
        folderIcon.setMaxSize(20, 20);

        folderItem.setGraphic(folderIcon);
        folderItem.getStyleClass().add("popUpItem");
        folderItem.setOnAction((e) -> menuController.openFolderChooser());

        youtubePath.setContent(App.svgMap.get(SVG.YOUTUBE));
        youtubeIcon.setShape(youtubePath);
        youtubeIcon.getStyleClass().add("icon");
        youtubeIcon.setPrefSize(20, 20);
        youtubeIcon.setMaxSize(20, 20);

        youtubeItem.setGraphic(youtubeIcon);
        youtubeItem.getStyleClass().add("popUpItem");
        youtubeItem.setOnAction((e) -> System.out.println("Youtube test"));


        this.getItems().addAll(fileItem, folderItem, youtubeItem);

        buttonWidth = menuController.addButtonContainer.getWidth();

        this.getStyleableNode().setOpacity(0);
    }


    public void showOptions(boolean animate){
        this.show(menuController.addButtonContainer, // might not work
                menuController.addButtonContainer.localToScreen(menuController.addButtonContainer.getBoundsInLocal()).getMinX() + buttonWidth/2 - popUpWidth/2,
                menuController.addButtonContainer.localToScreen(menuController.addButtonContainer.getBoundsInLocal()).getMaxY() + 5, animate);
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
    }

    @Override
    public void hide() {

        showing = false;

        if(showTransition != null && showTransition.getStatus() == Animation.Status.RUNNING) showTransition.stop();

        hideTransition = new FadeTransition(Duration.millis(150), this.getStyleableNode());
        hideTransition.setFromValue(1);
        hideTransition.setToValue(0);
        hideTransition.setOnFinished(e -> super.hide());
        hideTransition.playFromStart();
    }
}