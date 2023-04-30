package hans.Menu.MediaInformation;

import hans.App;
import hans.MediaItems.MediaItem;
import hans.SVG;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Region;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

import java.util.Objects;

public class EditImagePopUp extends ContextMenu {

    MediaInformationPage mediaInformationPage;
    MediaItem mediaItem;

    MenuItem removeItem = new MenuItem("Remove image");
    MenuItem chooseItem = new MenuItem("Choose image");

    double buttonWidth;
    final double popUpWidth = 154; // calling getWidth on this pop-up window is inaccurate as it sometimes incorrectly shows 151, hard-coded value is used to always get the same result


    FadeTransition showTransition, hideTransition;

    SVGPath removePath = new SVGPath(), imagePath = new SVGPath();
    Region removeIcon = new Region(), imageIcon = new Region();


    EditImagePopUp(MediaInformationPage mediaInformationPage){

        this.mediaInformationPage = mediaInformationPage;
        this.getStyleClass().add("menu-context-menu");


        mediaInformationPage.textBox.getScene().getStylesheets().add(Objects.requireNonNull(mediaInformationPage.menuController.mainController.getClass().getResource("styles/optionsPopUp.css")).toExternalForm());


        removePath.setContent(SVG.REMOVE.getContent());
        removeIcon.setShape(removePath);
        removeIcon.getStyleClass().add("icon");
        removeIcon.setPrefSize(15, 15);
        removeIcon.setMaxSize(15, 15);

        removeItem.setGraphic(removeIcon);
        removeItem.getStyleClass().add("popUpItem");
        removeItem.setOnAction((e) -> mediaInformationPage.removeImage());

        imagePath.setContent(SVG.IMAGE.getContent());
        imageIcon.setShape(imagePath);
        imageIcon.getStyleClass().add("icon");
        imageIcon.setPrefSize(15, 15);
        imageIcon.setMaxSize(15, 15);

        chooseItem.setGraphic(imageIcon);
        chooseItem.getStyleClass().add("popUpItem");
        chooseItem.setOnAction((e) -> mediaInformationPage.editImage());





        this.getItems().addAll(removeItem, chooseItem);

        buttonWidth = mediaInformationPage.editImageButton.getWidth();

        this.getStyleableNode().setOpacity(0);

    }

    public void showOptions(MediaItem mediaItem){
        this.mediaItem = mediaItem;
        this.show(mediaInformationPage.editImageButton, // might not work
                mediaInformationPage.editImageButton.localToScreen(mediaInformationPage.editImageButton.getBoundsInLocal()).getMinX() + buttonWidth/2 - popUpWidth/2,
                mediaInformationPage.editImageButton.localToScreen(mediaInformationPage.editImageButton.getBoundsInLocal()).getMaxY() + 5);
    }


    @Override
    public void show(Node node, double v, double v1) {
        if(hideTransition != null && hideTransition.getStatus() == Animation.Status.RUNNING) hideTransition.stop();

        this.getStyleableNode().setOpacity(0);

        super.show(node, v, v1);
        showTransition = new FadeTransition(Duration.millis(150), this.getStyleableNode());
        showTransition.setFromValue(0);
        showTransition.setToValue(1);
        showTransition.playFromStart();

    }

    @Override
    public void hide() {
        if(showTransition != null && showTransition.getStatus() == Animation.Status.RUNNING) showTransition.stop();

        hideTransition = new FadeTransition(Duration.millis(150), this.getStyleableNode());
        hideTransition.setFromValue(1);
        hideTransition.setToValue(0);
        hideTransition.setOnFinished(e -> super.hide());
        hideTransition.playFromStart();
    }

}
