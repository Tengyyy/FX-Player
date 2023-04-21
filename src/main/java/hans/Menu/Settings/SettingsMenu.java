package hans.Menu.Settings;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class SettingsMenu extends VBox {


    Label subtitlesItem = new Label("Subtitles");
    Label metadataItem = new Label("Metadata editing");
    Label preferencesItem = new Label("Preferences");
    Label musicLibrariesItem = new Label("Music libraries");
    Label controlsItem = new Label("Controls");
    Label aboutItem = new Label("About");

    public boolean showing = false;

    SettingsPage settingsPage;

    FadeTransition showTransition;
    FadeTransition hideTransition;

    public SettingsMenu(SettingsPage settingsPage){

        this.settingsPage = settingsPage;

        this.getStyleClass().add("settingsMenu");
        this.getChildren().addAll(subtitlesItem, metadataItem, preferencesItem, musicLibrariesItem, controlsItem, aboutItem);
        this.setSpacing(3);
        this.setMaxHeight(150);
        this.setMaxWidth(170);
        this.setFillWidth(true);

        subtitlesItem.getStyleClass().add("item");
        subtitlesItem.setMinWidth(170);
        subtitlesItem.setOnMouseClicked(e -> {
            settingsPage.animateScroll(Section.SUBTITLES);
        });

        metadataItem.getStyleClass().add("item");
        metadataItem.setMinWidth(170);
        metadataItem.setOnMouseClicked(e -> {
            settingsPage.animateScroll(Section.METADATA);
        });

        preferencesItem.getStyleClass().add("item");
        preferencesItem.setMinWidth(170);
        preferencesItem.setOnMouseClicked(e -> {
            settingsPage.animateScroll(Section.PREFERENCES);
        });

        musicLibrariesItem.getStyleClass().add("item");
        musicLibrariesItem.setMinWidth(170);
        musicLibrariesItem.setOnMouseClicked(e -> {
            settingsPage.animateScroll(Section.LIBRARIES);
        });

        controlsItem.getStyleClass().add("item");
        controlsItem.setMinWidth(170);
        controlsItem.setOnMouseClicked(e -> {
            settingsPage.animateScroll(Section.CONTROLS);
        });

        aboutItem.getStyleClass().add("item");
        aboutItem.setMinWidth(170);
        aboutItem.setOnMouseClicked(e -> {
            settingsPage.animateScroll(Section.ABOUT);
        });

        this.setOpacity(0);
        this.setMouseTransparent(true);

        this.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(!newValue && showing) this.hide();
        });
    }



    public void show() {

        if(hideTransition != null && hideTransition.getStatus() == Animation.Status.RUNNING) hideTransition.stop();
        showing = true;

        showTransition = new FadeTransition(Duration.millis(150), this);
        showTransition.setFromValue(this.getOpacity());
        showTransition.setToValue(1);
        showTransition.setOnFinished(e -> {
            this.setMouseTransparent(false);
            this.requestFocus();
        });
        showTransition.playFromStart();
    }

    public void hide() {

        if(showTransition != null && showTransition.getStatus() == Animation.Status.RUNNING) showTransition.stop();

        showing = false;

        this.setMouseTransparent(true);

        FadeTransition hideTransition = new FadeTransition(Duration.millis(150), this);
        hideTransition.setFromValue(this.getOpacity());
        hideTransition.setToValue(0);
        hideTransition.playFromStart();
    }
}