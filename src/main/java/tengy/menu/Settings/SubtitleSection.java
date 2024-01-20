package tengy.menu.Settings;

import javafx.beans.property.*;
import javafx.scene.Node;
import tengy.playbackSettings.PlaybackSettingsState;
import tengy.SVG;
import tengy.subtitles.SubtitlesState;
import tengy.Utilities;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import tengy.windows.openSubtitles.SearchPage;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import static tengy.Utilities.keyboardFocusOn;

public class SubtitleSection extends VBox implements SettingsSection{

    SettingsPage settingsPage;

    Label subtitleSectionTitle = new Label("Subtitles");

    VBox toggleContainer = new VBox();
    
    Toggle extrationToggle;
    public BooleanProperty extractionOn = new SimpleBooleanProperty(false);
    
    Toggle searchToggle;
    public BooleanProperty searchOn = new SimpleBooleanProperty(false);

    ComboItem languageItem;
    public StringProperty languageProperty = new SimpleStringProperty();


    public static final String SUBTITLE_EXTRACTION_ON = "subtitle_extraction_on";
    public static final String SUBTITLE_PARENT_FOLDER_SCAN_ON = "subtitle_parent_folder_scan_on";
    public static final String SUBTITLES_LANGUAGE = "subtitles_language";

    IntegerProperty focus = new SimpleIntegerProperty(-1);
    List<Node> focusNodes = new ArrayList<>();

    boolean infoToggleHover = false;


    SubtitleSection(SettingsPage settingsPage){
        this.settingsPage = settingsPage;

        this.getChildren().addAll(subtitleSectionTitle, toggleContainer);

        subtitleSectionTitle.getStyleClass().add("settingsSectionTitle");

        toggleContainer.setPadding(new Insets(0, 0, 10, 0));
        toggleContainer.setSpacing(10);
        VBox.setMargin(toggleContainer, new Insets(5, 0 , 0, 0));


        extrationToggle = new Toggle(settingsPage, SVG.SETTINGS.getContent(), "Extract subtitles embedded into media file containers", extractionOn, this, 0, 0);
        searchToggle = new Toggle(settingsPage, SVG.SETTINGS.getContent(), "Scan parent folder for subtitle file with matching name", searchOn, this, 0, 1);

        extractionOn.addListener((observableValue, oldValue, newValue) -> settingsPage.menuController.mainController.pref.preferences.putBoolean(SUBTITLE_EXTRACTION_ON, newValue));
        searchOn.addListener((observableValue, oldValue, newValue) -> settingsPage.menuController.mainController.pref.preferences.putBoolean(SUBTITLE_PARENT_FOLDER_SCAN_ON, newValue));

        languageItem = new ComboItem(settingsPage, SVG.MESSAGE.getContent(), "Preferred language for subtitles", this, 0, 2);

        languageItem.customMenuButton.setContextWidth(180);
        languageItem.customMenuButton.setContextHeight(200);

        languageItem.customMenuButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue) {
                if (settingsPage.menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED)
                    settingsPage.menuController.subtitlesController.closeSubtitles();
                if (settingsPage.menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED)
                    settingsPage.menuController.playbackSettingsController.closeSettings();
            }
        });

        languageItem.customMenuButton.setOnMouseClicked(e -> {
            if (settingsPage.menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED)
                settingsPage.menuController.subtitlesController.closeSubtitles();
            if (settingsPage.menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED)
                settingsPage.menuController.playbackSettingsController.closeSettings();
        });

        languageProperty.bind(languageItem.valueProperty());
        languageProperty.addListener((observableValue, oldValue, newValue) -> {
            settingsPage.menuController.mainController.pref.preferences.put(SUBTITLES_LANGUAGE, newValue);
        });



        toggleContainer.getChildren().addAll(extrationToggle, searchToggle, languageItem);

        focusNodes.add(extrationToggle);
        focusNodes.add(searchToggle);
        focusNodes.add(languageItem.customMenuButton);
    }

    public void loadLanguageBox(){
        for(String string : SearchPage.supportedLanguages){
            languageItem.add(string);
        }
    }

    public void loadPreferences(){
        Preferences preferences = settingsPage.menuController.mainController.pref.preferences;
        extractionOn.set(preferences.getBoolean(SUBTITLE_EXTRACTION_ON, true));
        searchOn.set(preferences.getBoolean(SUBTITLE_PARENT_FOLDER_SCAN_ON, false));

        String language = preferences.get(SUBTITLES_LANGUAGE, "English");
        languageItem.customMenuButton.setValue(language);

        settingsPage.menuController.mainController.windowController.openSubtitlesWindow.searchPage.languageButton.select(language);
    }

    @Override
    public boolean focusForward(){

        if(focus.get() == 2) return true;

        if(focus.get() == 1){
            Utilities.checkScrollDown(settingsPage.settingsScroll, languageItem);
            keyboardFocusOn(languageItem.customMenuButton);
        }
        else {
            Node node = focusNodes.get(focus.get() + 1);
            keyboardFocusOn(node);
            Utilities.checkScrollDown(settingsPage.settingsScroll, node);
        }

        return false;
    }

    @Override
    public boolean focusBackward(){

        if(focus.get() == 0)
            return true;


        if(focus.get() == -1){
            Utilities.checkScrollDown(settingsPage.settingsScroll, languageItem);
            keyboardFocusOn(languageItem.customMenuButton);
        }
        else {
            Node node = focusNodes.get(focus.get() - 1);

            keyboardFocusOn(node);
            Utilities.checkScrollDown(settingsPage.settingsScroll, node);
        }

        return false;
    }

    @Override
    public void setFocus(int value){
        this.focus.set(value);
    }
}
