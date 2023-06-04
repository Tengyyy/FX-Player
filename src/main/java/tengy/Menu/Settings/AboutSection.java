package tengy.Menu.Settings;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import tengy.Menu.FocusableMenuButton;
import tengy.PlaybackSettings.PlaybackSettingsState;
import tengy.Subtitles.SubtitlesState;
import tengy.Utilities;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

import static tengy.Utilities.keyboardFocusOff;
import static tengy.Utilities.keyboardFocusOn;

public class AboutSection extends VBox implements SettingsSection {

    SettingsPage settingsPage;

    Label aboutSectionTitle = new Label("About FX Player");

    VBox labelBox = new VBox();
    Label descriptionLabel = new Label("Feature-rich open-source media player built with JavaFX and based on LibVLC");
    Label versionLabel = new Label("Version 1.0");
    Label copyrightLabel = new Label("© 2023 Tengy. GNU General Public License");

    VBox buttonBox = new VBox();
    FocusableMenuButton licenseButton = new FocusableMenuButton();
    FocusableMenuButton thirdPartyButton = new FocusableMenuButton();
    FocusableMenuButton helpButton = new FocusableMenuButton();
    FocusableMenuButton feedbackButton = new FocusableMenuButton();

    IntegerProperty focus = new SimpleIntegerProperty(-1);
    List<Node> focusNodes = new ArrayList<>();

    AboutSection(SettingsPage settingsPage){
        this.settingsPage = settingsPage;

        aboutSectionTitle.getStyleClass().add("settingsSectionTitle");

        this.getChildren().addAll(aboutSectionTitle, labelBox, buttonBox);

        VBox.setMargin(labelBox, new Insets(20, 0, 0, 0));
        labelBox.setSpacing(10);
        labelBox.getChildren().addAll(descriptionLabel, versionLabel, copyrightLabel);

        descriptionLabel.setWrapText(true);
        descriptionLabel.getStyleClass().add("aboutSectionText");

        versionLabel.getStyleClass().add("aboutSectionText");

        copyrightLabel.getStyleClass().add("aboutSectionText");

        VBox.setMargin(buttonBox, new Insets(30, 0, 0, 0));
        buttonBox.setSpacing(10);
        buttonBox.getChildren().addAll(licenseButton, thirdPartyButton, helpButton, feedbackButton);
        buttonBox.setTranslateX(-11);

        licenseButton.setText("License Terms");
        licenseButton.getStyleClass().add("linkButton");
        licenseButton.setOnAction(e -> {
            if(settingsPage.menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) settingsPage.menuController.subtitlesController.closeSubtitles();
            if(settingsPage.menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) settingsPage.menuController.playbackSettingsController.closeSettings();

            licenseButton.requestFocus();

            settingsPage.menuController.mainController.windowController.licenseWindow.show();
        });

        licenseButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(0);
                settingsPage.focus.set(4);
            }
            else {
                keyboardFocusOff(licenseButton);
                focus.set(-1);
                settingsPage.focus.set(-1);
            }
        });

        licenseButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            licenseButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        licenseButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            licenseButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });

        thirdPartyButton.setText("Third-Party Software");
        thirdPartyButton.getStyleClass().add("linkButton");
        thirdPartyButton.setOnAction(e -> {
            if(settingsPage.menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) settingsPage.menuController.subtitlesController.closeSubtitles();
            if(settingsPage.menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) settingsPage.menuController.playbackSettingsController.closeSettings();

            thirdPartyButton.requestFocus();

            settingsPage.menuController.mainController.windowController.thirdPartySoftwareWindow.show();
        });

        thirdPartyButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(1);
                settingsPage.focus.set(4);
            }
            else {
                keyboardFocusOff(thirdPartyButton);
                focus.set(-1);
                settingsPage.focus.set(-1);
            }
        });

        thirdPartyButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            thirdPartyButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        thirdPartyButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            thirdPartyButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });

        helpButton.setText("Help");
        helpButton.getStyleClass().add("linkButton");
        helpButton.setOnAction(e -> {
            if(settingsPage.menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) settingsPage.menuController.subtitlesController.closeSubtitles();
            if(settingsPage.menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) settingsPage.menuController.playbackSettingsController.closeSettings();

            helpButton.requestFocus();

            Utilities.openBrowser("https://github.com/Tengyyy/FX-Player/wiki");

        });

        helpButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(2);
                settingsPage.focus.set(4);
            }
            else {
                keyboardFocusOff(helpButton);
                focus.set(-1);
                settingsPage.focus.set(-1);
            }
        });

        helpButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            helpButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        helpButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            helpButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });

        feedbackButton.setText("Feedback");
        feedbackButton.getStyleClass().add("linkButton");
        feedbackButton.setOnAction(e -> {
            if(settingsPage.menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) settingsPage.menuController.subtitlesController.closeSubtitles();
            if(settingsPage.menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) settingsPage.menuController.playbackSettingsController.closeSettings();

            feedbackButton.requestFocus();

            Utilities.openBrowser("https://github.com/Tengyyy/FX-Player/issues");
        });

        feedbackButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(3);
                settingsPage.focus.set(4);
            }
            else {
                keyboardFocusOff(feedbackButton);
                focus.set(-1);
                settingsPage.focus.set(-1);
            }
        });

        feedbackButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            feedbackButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        feedbackButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            feedbackButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });

        focusNodes.add(licenseButton);
        focusNodes.add(thirdPartyButton);
        focusNodes.add(helpButton);
        focusNodes.add(feedbackButton);
    }

    @Override
    public boolean focusForward(){

        if(focus.get() == focusNodes.size() - 1) return true;

        keyboardFocusOn(focusNodes.get(focus.get() + 1));

        return false;
    }

    @Override
    public boolean focusBackward(){

        if(focus.get() == 0) return true;
        else if(focus.get() == -1) keyboardFocusOn(focusNodes.get(focusNodes.size() - 1));
        else keyboardFocusOn(focusNodes.get(focus.get() - 1));

        return false;
    }

    @Override
    public void setFocus(int value){
        this.focus.set(value);
    }
}
