package hans.Menu.Settings;

import hans.Subtitles.SubtitlesState;
import hans.PlaybackSettings.PlaybackSettingsState;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class PreferencesSection extends VBox {

    SettingsPage settingsPage;

    Label preferencesSectionTitle = new Label("Preferences");

    Toggle seekPreviewToggle;
    BooleanProperty seekPreviewOn = new SimpleBooleanProperty();

    StackPane languagePane = new StackPane();
    Label languageLabel = new Label("Preferred language for subtitles and audio");
    ComboBox<String> languageBox = new ComboBox<>();
    StringProperty languageProperty = new SimpleStringProperty();

    StackPane recentMediaSizePane = new StackPane();
    Label recentMediaSizeLabel = new Label("Recent media size");
    ComboBox<Integer> recentMediaSizeBox = new ComboBox<>();
    IntegerProperty recentMediaSizeProperty = new SimpleIntegerProperty();

    PreferencesSection(SettingsPage settingsPage){
        this.settingsPage = settingsPage;

        preferencesSectionTitle.getStyleClass().add("settingsSectionTitle");

        seekPreviewToggle = new Toggle(settingsPage, "Show frame preview above seekbar", seekPreviewOn);


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

        languageBox.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                if(settingsPage.menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) settingsPage.menuController.subtitlesController.closeSubtitles();
                if(settingsPage.menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) settingsPage.menuController.playbackSettingsController.closeSettings();
            }
        });

        languageProperty.bind(languageBox.getSelectionModel().selectedItemProperty());

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

        this.getChildren().addAll(preferencesSectionTitle, seekPreviewToggle, languagePane, recentMediaSizePane);
        this.setSpacing(25);
    }

    public void loadLanguageBox(){
        for(String string : settingsPage.menuController.subtitlesController.openSubtitlesPane.supportedLanguages){
            languageBox.getItems().add(string);
        }
    }
}
