package hans.Menu.Settings;

import hans.Subtitles.SubtitlesState;
import hans.PlaybackSettings.PlaybackSettingsState;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.prefs.Preferences;

public class PreferencesSection extends VBox {

    SettingsPage settingsPage;

    Label preferencesSectionTitle = new Label("Preferences");

    Toggle seekPreviewToggle;
    public BooleanProperty seekPreviewOn = new SimpleBooleanProperty();

    StackPane languagePane = new StackPane();
    Label languageLabel = new Label("Preferred language for subtitles");
    ComboBox<String> languageBox = new ComboBox<>();
    public StringProperty languageProperty = new SimpleStringProperty();

    StackPane recentMediaSizePane = new StackPane();
    Label recentMediaSizeLabel = new Label("Recent media size");
    ComboBox<Integer> recentMediaSizeBox = new ComboBox<>();
    public IntegerProperty recentMediaSizeProperty = new SimpleIntegerProperty();

    public static final String SEEKBAR_FRAME_PREVIEW_ON = "seekbar_frame_preview_on";
    public static final String HISTORY_SIZE = "history_size";
    public static final String SUBTITLES_LANGUAGE = "subtitles_language";



    PreferencesSection(SettingsPage settingsPage){
        this.settingsPage = settingsPage;

        preferencesSectionTitle.getStyleClass().add("settingsSectionTitle");

        seekPreviewToggle = new Toggle(settingsPage, "Show frame preview above seekbar", seekPreviewOn);

        seekPreviewOn.addListener((observableValue, oldValue, newValue) -> {
            settingsPage.menuController.mainController.pref.preferences.putBoolean(SEEKBAR_FRAME_PREVIEW_ON, newValue);

            if(!newValue){
                settingsPage.menuController.mainController.sliderHoverBox.getChildren().remove(settingsPage.menuController.mainController.sliderHoverBox.imagePane);
            }
            else {
                if(settingsPage.menuController.queuePage.queueBox.activeItem.get() != null
                        && settingsPage.menuController.queuePage.queueBox.activeItem.get().getMediaItem() != null
                        && settingsPage.menuController.queuePage.queueBox.activeItem.get().getMediaItem().hasVideo()
                        && !settingsPage.menuController.mainController.sliderHoverBox.getChildren().contains(settingsPage.menuController.mainController.sliderHoverBox.imagePane)){
                    settingsPage.menuController.mainController.sliderHoverBox.getChildren().add(0, settingsPage.menuController.mainController.sliderHoverBox.imagePane);
                }
            }
        });

        languagePane.getChildren().addAll(languageLabel, languageBox);
        languagePane.setPadding(new Insets(8, 10, 8, 10));
        languagePane.getStyleClass().add("highlightedSection");

        languageLabel.getStyleClass().add("toggleText");
        StackPane.setAlignment(languageLabel, Pos.CENTER_LEFT);

        StackPane.setAlignment(languageBox, Pos.CENTER_RIGHT);

        languageBox.setPrefWidth(150);
        languageBox.setMaxWidth(150);
        languageBox.setOnAction(e -> {
            if(settingsPage.menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) settingsPage.menuController.subtitlesController.closeSubtitles();
            if(settingsPage.menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) settingsPage.menuController.playbackSettingsController.closeSettings();
        });
        languageBox.setFocusTraversable(false);

        languageBox.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                if(settingsPage.menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) settingsPage.menuController.subtitlesController.closeSubtitles();
                if(settingsPage.menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) settingsPage.menuController.playbackSettingsController.closeSettings();
            }
        });

        languageProperty.bind(languageBox.getSelectionModel().selectedItemProperty());
        languageProperty.addListener((observableValue, oldValue, newValue) -> {
            settingsPage.menuController.mainController.pref.preferences.put(SUBTITLES_LANGUAGE, newValue);
        });

        recentMediaSizePane.getChildren().addAll(recentMediaSizeLabel, recentMediaSizeBox);
        recentMediaSizePane.setPadding(new Insets(8, 10, 8, 10));
        recentMediaSizePane.getStyleClass().add("highlightedSection");

        recentMediaSizeLabel.getStyleClass().add("toggleText");
        StackPane.setAlignment(recentMediaSizeLabel, Pos.CENTER_LEFT);

        StackPane.setAlignment(recentMediaSizeBox, Pos.CENTER_RIGHT);
        recentMediaSizeBox.getItems().add(10);
        recentMediaSizeBox.getItems().add(25);
        recentMediaSizeBox.getItems().add(50);
        recentMediaSizeBox.getItems().add(100);
        recentMediaSizeBox.setPrefWidth(150);
        recentMediaSizeBox.setMaxWidth(150);
        recentMediaSizeBox.setFocusTraversable(false);

        recentMediaSizeBox.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                if(settingsPage.menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) settingsPage.menuController.subtitlesController.closeSubtitles();
                if(settingsPage.menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) settingsPage.menuController.playbackSettingsController.closeSettings();
            }
        });

        recentMediaSizeBox.setOnAction(e -> {
            if(settingsPage.menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) settingsPage.menuController.subtitlesController.closeSubtitles();
            if(settingsPage.menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) settingsPage.menuController.playbackSettingsController.closeSettings();
        });


        recentMediaSizeProperty.bind(recentMediaSizeBox.getSelectionModel().selectedItemProperty());
        recentMediaSizeProperty.addListener((observableValue, oldValue, newValue) -> {
            settingsPage.menuController.mainController.pref.preferences.putInt(HISTORY_SIZE, newValue.intValue());
        });

        this.getChildren().addAll(preferencesSectionTitle, seekPreviewToggle, languagePane, recentMediaSizePane);
        this.setSpacing(25);
    }

    public void loadLanguageBox(){
        for(String string : settingsPage.menuController.subtitlesController.openSubtitlesPane.supportedLanguages){
            languageBox.getItems().add(string);
        }
    }

    public void loadPreferences(){
        Preferences preferences = settingsPage.menuController.mainController.pref.preferences;
        seekPreviewOn.set(preferences.getBoolean(SEEKBAR_FRAME_PREVIEW_ON, true));
        String language = preferences.get(SUBTITLES_LANGUAGE, "English");
        languageBox.getSelectionModel().select(language);
        recentMediaSizeBox.getSelectionModel().select((Integer) preferences.getInt(HISTORY_SIZE, 25));

        settingsPage.menuController.subtitlesController.openSubtitlesPane.languageBox.getCheckModel().check(settingsPage.menuController.subtitlesController.openSubtitlesPane.languageBox.getCheckModel().getItemIndex(language));
    }
}
