package hans.Menu.MediaInformation;

import hans.MediaItems.MediaItem;
import hans.MediaItems.MediaUtilities;
import hans.Utilities;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.util.Objects;

public class SaveOptionsContextMenu extends ContextMenu {

    MediaInformationPage mediaInformationPage;

    MenuItem currentItem = new MenuItem("Overwrite current file (default)");
    MenuItem newItem = new MenuItem("Save changes to new file");

    double buttonWidth;

    final double popUpWidth = 236;
    final double popUpHeight = 80;

    FadeTransition showTransition, hideTransition;

    public boolean showing = false;

    MediaItem mediaItem;

    FileChooser fileChooser = new FileChooser();

    public SaveOptionsContextMenu(MediaInformationPage mediaInformationPage){

        this.mediaInformationPage = mediaInformationPage;
        
        fileChooser.setTitle("Save file");

        this.getStyleClass().add("menu-context-menu");

        mediaInformationPage.saveButtonContainer.getScene().getStylesheets().add(Objects.requireNonNull(mediaInformationPage.menuController.mainController.getClass().getResource("styles/optionsPopUp.css")).toExternalForm());


        currentItem.getStyleClass().add("popUpItem");
        currentItem.setOnAction((e) -> mediaInformationPage.saveChanges());


        newItem.getStyleClass().add("popUpItem");
        newItem.setOnAction((e) -> {
            openFileChooser();
        });


        this.getItems().addAll(currentItem, newItem);

        buttonWidth = mediaInformationPage.saveButtonContainer.getWidth();

        this.getStyleableNode().setOpacity(0);
    }


    public void showOptions(boolean animate, MediaItem mediaItem){
        this.mediaItem = mediaItem;
        this.show(mediaInformationPage.saveButtonContainer, // might not work
                mediaInformationPage.saveButtonContainer.localToScreen(mediaInformationPage.saveButtonContainer.getBoundsInLocal()).getMinX() + buttonWidth/2 - popUpWidth/2,
                mediaInformationPage.saveButtonContainer.localToScreen(mediaInformationPage.saveButtonContainer.getBoundsInLocal()).getMinY() - 3 - popUpHeight, animate);
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
        this.mediaItem = null;

        if(showTransition != null && showTransition.getStatus() == Animation.Status.RUNNING) showTransition.stop();

        hideTransition = new FadeTransition(Duration.millis(150), this.getStyleableNode());
        hideTransition.setFromValue(1);
        hideTransition.setToValue(0);
        hideTransition.setOnFinished(e -> {
            super.hide();
        });
        hideTransition.playFromStart();
    }

    private void openFileChooser() {
        if(this.mediaItem == null) return;

        MediaItem item = this.mediaItem;

        String extension = Utilities.getFileExtension(item.getFile());

        fileChooser.setInitialFileName(findFileName());
        fileChooser.setInitialDirectory(item.getFile().getParentFile());
        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(extension + " files (*." + extension + ")", "*." + extension));

        File selectedFile = fileChooser.showSaveDialog(mediaInformationPage.imageView.getScene().getWindow());

        if(selectedFile != null){
            if (selectedFile.getName().endsWith("." + extension)) {
                if(!selectedFile.getAbsolutePath().equals(item.getFile().getAbsolutePath())){
                    mediaInformationPage.saveToNewFile(selectedFile);
                }
                else mediaInformationPage.saveChanges();
            }
            else {
                if(mediaInformationPage.saveLabelTimer.getStatus() == Animation.Status.RUNNING) mediaInformationPage.saveLabelTimer.stop();
                mediaInformationPage.popupTitle.setText("Invalid extension");
                mediaInformationPage.popupBody.getChildren().clear();
                mediaInformationPage.popupBody.getChildren().add(mediaInformationPage.createTextLabel("Output file has to have the same extension as the original file (*." + extension + ")"));
                mediaInformationPage.popup.setPrefSize(300, 120);
                mediaInformationPage.popup.setMaxSize(300, 120);
                mediaInformationPage.showPopup();
            }
        }
    }


    public String findFileName(){
        File parent = new File(mediaItem.getFile().getParent());

        String name = mediaItem.getFile().getName();

        File file = new File(parent, name);
        int index = 1;
        while(file.exists()){
            String extension = Utilities.getFileExtension(file);
            String newName;
            if(index == 1)
                newName = file.getName().substring(0, file.getName().lastIndexOf("." + extension)) + " (1)." + extension;
            else
                newName = file.getName().substring(0, file.getName().lastIndexOf(" (")) + " (" + index + ")." + extension;

            file = new File(file.getParentFile(), newName);
            index++;
        }

        return file.getName();
    }
}