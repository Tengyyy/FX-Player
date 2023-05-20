package tengy.Menu.Settings;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class SettingsMenu extends ContextMenu {

    MenuItem subtitlesItem = new MenuItem("Subtitles");
    MenuItem metadataItem = new MenuItem("Metadata editing");
    MenuItem preferencesItem = new MenuItem("Preferences");
    MenuItem musicLibrariesItem = new MenuItem("Music libraries");
    MenuItem controlsItem = new MenuItem("Controls");
    MenuItem aboutItem = new MenuItem("About");

    SettingsPage settingsPage;

    FadeTransition showTransition, hideTransition;

    double buttonWidth;

    final double popUpWidth = 200;

    public boolean showing = false;


    public SettingsMenu(SettingsPage settingsPage){

        this.settingsPage = settingsPage;

        this.getStyleClass().add("menu-context-menu");
        this.getItems().addAll(subtitlesItem, metadataItem, preferencesItem, musicLibrariesItem, controlsItem, aboutItem);

        this.getStyleableNode().setOpacity(0);

        buttonWidth = settingsPage.linksButton.getWidth();

        subtitlesItem.getStyleClass().addAll("popUpItem", "settingsMenuItem");
        subtitlesItem.setOnAction(e -> settingsPage.animateScroll(Section.SUBTITLES));

        metadataItem.getStyleClass().addAll("popUpItem", "settingsMenuItem");
        metadataItem.setOnAction(e -> settingsPage.animateScroll(Section.METADATA));

        preferencesItem.getStyleClass().addAll("popUpItem", "settingsMenuItem");
        preferencesItem.setOnAction(e -> settingsPage.animateScroll(Section.PREFERENCES));

        musicLibrariesItem.getStyleClass().addAll("popUpItem", "settingsMenuItem");
        musicLibrariesItem.setOnAction(e -> settingsPage.animateScroll(Section.LIBRARIES));

        controlsItem.getStyleClass().addAll("popUpItem", "settingsMenuItem");
        controlsItem.setOnAction(e -> settingsPage.animateScroll(Section.CONTROLS));

        aboutItem.getStyleClass().addAll("popUpItem", "settingsMenuItem");
        aboutItem.setOnAction(e -> settingsPage.animateScroll(Section.ABOUT));
    }

    public void showOptions(boolean animate){
        this.show(settingsPage.linksButton, // might not work
                settingsPage.linksButton.localToScreen(settingsPage.linksButton.getBoundsInLocal()).getMinX() + buttonWidth/2 - popUpWidth/2,
                settingsPage.linksButton.localToScreen(settingsPage.linksButton.getBoundsInLocal()).getMaxY() + 5, animate);
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