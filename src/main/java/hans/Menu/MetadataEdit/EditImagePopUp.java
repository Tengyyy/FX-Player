package hans.Menu.MetadataEdit;

import hans.App;
import hans.Menu.MenuObject;
import hans.Menu.MetadataEdit.MetadataEditPage;
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

    MetadataEditPage metadataEditPage;
    MenuObject menuObject;

    MenuItem removeItem = new MenuItem("Remove image");
    MenuItem chooseItem = new MenuItem("Choose image");

    double buttonWidth;
    final double popUpWidth = 162; // calling getWidth on this pop-up window is inaccurate as it sometimes incorrectly shows 151, hard-coded value is used to always get the same result


    FadeTransition showTransition, hideTransition;

    SVGPath removePath = new SVGPath(), imagePath = new SVGPath();
    Region removeIcon = new Region(), imageIcon = new Region();


    EditImagePopUp(MetadataEditPage metadataEditPage){

        this.metadataEditPage = metadataEditPage;

        metadataEditPage.textBox.getScene().getStylesheets().add(Objects.requireNonNull(metadataEditPage.menuController.mainController.getClass().getResource("styles/optionsPopUp.css")).toExternalForm());


        removePath.setContent(App.svgMap.get(SVG.REMOVE));
        removeIcon.setShape(removePath);
        removeIcon.getStyleClass().add("icon");
        removeIcon.setPrefSize(15, 15);
        removeIcon.setMaxSize(15, 15);

        removeItem.setGraphic(removeIcon);
        removeItem.getStyleClass().add("popUpItem");
        removeItem.setOnAction((e) -> {
            metadataEditPage.removeImage(menuObject);
        });

        imagePath.setContent(App.svgMap.get(SVG.IMAGE));
        imageIcon.setShape(imagePath);
        imageIcon.getStyleClass().add("icon");
        imageIcon.setPrefSize(15, 15);
        imageIcon.setMaxSize(15, 15);

        chooseItem.setGraphic(imageIcon);
        chooseItem.getStyleClass().add("popUpItem");
        chooseItem.setOnAction((e) -> metadataEditPage.editImage());





        this.getItems().addAll(removeItem, chooseItem);

        buttonWidth = metadataEditPage.editImageButton.getWidth();

        this.getStyleableNode().setOpacity(0);

    }

    public void showOptions(MenuObject menuObject){
        this.menuObject = menuObject;
        this.show(metadataEditPage.editImageButton, // might not work
                metadataEditPage.editImageButton.localToScreen(metadataEditPage.editImageButton.getBoundsInLocal()).getMinX() + buttonWidth/2 - popUpWidth/2,
                metadataEditPage.editImageButton.localToScreen(metadataEditPage.editImageButton.getBoundsInLocal()).getMaxY() + 5);
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

