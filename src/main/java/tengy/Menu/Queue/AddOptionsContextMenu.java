package tengy.Menu.Queue;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import tengy.SVG;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Region;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

import java.util.Objects;

public class AddOptionsContextMenu extends ContextMenu {

    QueuePage queuePage;

    MenuItem fileItem = new MenuItem("Add file(s) to play queue");
    MenuItem folderItem = new MenuItem("Add folder to play queue");
    MenuItem youtubeItem = new MenuItem("Add YouTube video(s) to play queue");

    double buttonWidth;

    final double popUpWidth = 294; // calling getWidth on this pop-up window is inaccurate as it sometimes incorrectly shows 151, hard-coded value is used to always get the same result

    FadeTransition showTransition, hideTransition;

    SVGPath filePath = new SVGPath(), folderPath = new SVGPath(), youtubePath = new SVGPath();
    Region fileIcon = new Region(), folderIcon = new Region(), youtubeIcon = new Region();

    public boolean showing = false;

    public AddOptionsContextMenu(QueuePage queuePage){

        this.queuePage = queuePage;

        this.getStyleClass().add("menu-context-menu");

        queuePage.addButtonContainer.getScene().getStylesheets().add(Objects.requireNonNull(queuePage.menuController.mainController.getClass().getResource("styles/optionsPopUp.css")).toExternalForm());

        filePath.setContent(SVG.FOLDER.getContent());
        fileIcon.setShape(filePath);
        fileIcon.getStyleClass().add("icon");
        fileIcon.setPrefSize(14, 14);
        fileIcon.setMaxSize(14, 14);

        fileItem.setGraphic(fileIcon);
        fileItem.getStyleClass().add("popUpItem");
        fileItem.setOnAction((e) -> queuePage.openVideoChooser());

        folderPath.setContent(SVG.FOLDER_PLUS.getContent());
        folderIcon.setShape(folderPath);
        folderIcon.getStyleClass().add("icon");
        folderIcon.setPrefSize(14, 14);
        folderIcon.setMaxSize(14, 14);

        folderItem.setGraphic(folderIcon);
        folderItem.getStyleClass().add("popUpItem");
        folderItem.setOnAction((e) -> queuePage.openFolderChooser());

        youtubePath.setContent(SVG.YOUTUBE.getContent());
        youtubeIcon.setShape(youtubePath);
        youtubeIcon.getStyleClass().add("icon");
        youtubeIcon.setPrefSize(14, 12);
        youtubeIcon.setMaxSize(14, 12);

        youtubeItem.setGraphic(youtubeIcon);
        youtubeItem.getStyleClass().add("popUpItem");
        youtubeItem.setOnAction((e) -> queuePage.menuController.mainController.windowController.addYoutubeVideoWindow.show());


        this.getItems().addAll(fileItem, folderItem, youtubeItem);

        buttonWidth = queuePage.addButtonContainer.getWidth();

        this.getStyleableNode().setOpacity(0);
    }


    public void showOptions(boolean animate){
        this.show(queuePage.addButtonContainer, // might not work
                queuePage.addButtonContainer.localToScreen(queuePage.addButtonContainer.getBoundsInLocal()).getMinX() + buttonWidth/2 - popUpWidth/2,
                queuePage.addButtonContainer.localToScreen(queuePage.addButtonContainer.getBoundsInLocal()).getMaxY() + 5, animate);
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