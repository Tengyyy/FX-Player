package hans.Menu.Settings;

import hans.PlaybackSettings.PlaybackSettingsState;
import hans.Subtitles.SubtitlesState;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class AboutSection extends VBox {

    SettingsPage settingsPage;

    Label aboutSectionTitle = new Label("About FX Player");

    VBox labelBox = new VBox();
    Label descriptionLabel = new Label("Feature-rich open-source media player built with JavaFX and based on LibVLC");
    Label versionLabel = new Label("Version 1.0");
    Label copyrightLabel = new Label("© 2023 Tengy. GNU General Public License");

    VBox buttonBox = new VBox();
    Button licenseButton = new Button("License Terms");
    Button thirdPartyButton = new Button("Third-Party Software");
    Button helpButton = new Button("Help");
    Button feedbackButton = new Button("Feedback");


    AboutSection(SettingsPage settingsPage){
        this.settingsPage = settingsPage;

        aboutSectionTitle.getStyleClass().add("settingsSectionTitle");

        this.getChildren().addAll(aboutSectionTitle, labelBox, buttonBox);
        this.setSpacing(25);

        labelBox.setSpacing(10);
        labelBox.getChildren().addAll(descriptionLabel, versionLabel, copyrightLabel);

        descriptionLabel.setWrapText(true);
        descriptionLabel.getStyleClass().add("aboutSectionText");

        versionLabel.getStyleClass().add("aboutSectionText");

        copyrightLabel.getStyleClass().add("aboutSectionText");

        buttonBox.setSpacing(10);
        buttonBox.getChildren().addAll(licenseButton, thirdPartyButton, helpButton, feedbackButton);
        buttonBox.setTranslateX(-11);

        licenseButton.getStyleClass().add("linkButton");
        licenseButton.setOnAction(e -> {
            if(settingsPage.menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) settingsPage.menuController.subtitlesController.closeSubtitles();
            if(settingsPage.menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) settingsPage.menuController.playbackSettingsController.closeSettings();

            settingsPage.menuController.mainController.licenseWindow.show();
        });
        licenseButton.setFocusTraversable(false);

        thirdPartyButton.getStyleClass().add("linkButton");
        thirdPartyButton.setOnAction(e -> {
            if(settingsPage.menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) settingsPage.menuController.subtitlesController.closeSubtitles();
            if(settingsPage.menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) settingsPage.menuController.playbackSettingsController.closeSettings();

            settingsPage.menuController.mainController.thirdPartySoftwareWindow.show();
        });
        thirdPartyButton.setFocusTraversable(false);

        helpButton.getStyleClass().add("linkButton");
        helpButton.setOnAction(e -> {
            if(settingsPage.menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) settingsPage.menuController.subtitlesController.closeSubtitles();
            if(settingsPage.menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) settingsPage.menuController.playbackSettingsController.closeSettings();

            // open opensubtitles account creation page in web browser
            if(Desktop.isDesktopSupported()){
                try {
                    Desktop.getDesktop().browse(new URL("https://github.com/Tengyyy/FX-Player/wiki").toURI());
                } catch (IOException | URISyntaxException ex) {
                    ex.printStackTrace();
                }
            }
        });
        helpButton.setFocusTraversable(false);

        feedbackButton.getStyleClass().add("linkButton");
        feedbackButton.setOnAction(e -> {
            if(settingsPage.menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) settingsPage.menuController.subtitlesController.closeSubtitles();
            if(settingsPage.menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) settingsPage.menuController.playbackSettingsController.closeSettings();

            // open opensubtitles account creation page in web browser
            if(Desktop.isDesktopSupported()){
                try {
                    Desktop.getDesktop().browse(new URL("https://github.com/Tengyyy/FX-Player/issues").toURI());
                } catch (IOException | URISyntaxException ex) {
                    ex.printStackTrace();
                }
            }
        });
        feedbackButton.setFocusTraversable(false);
    }
}
