package tengy.Menu.Settings;


import tengy.PlaybackSettings.PlaybackSettingsState;
import tengy.SVG;
import tengy.Subtitles.SubtitlesState;
import tengy.Utilities;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.prefs.Preferences;

public class SubtitleSection extends VBox {

    SettingsPage settingsPage;

    Label subtitleSectionTitle = new Label("Subtitles");

    VBox toggleContainer = new VBox();
    Toggle extrationToggle;
    public BooleanProperty extractionOn = new SimpleBooleanProperty(false);
    Toggle searchToggle;
    public BooleanProperty searchOn = new SimpleBooleanProperty(false);

    StackPane openSubtitlesSectionWrapper = new StackPane();
    VBox openSubtitlesSection = new VBox();
    StackPane openSubtitlesSectionTitlePane = new StackPane();
    Label openSubtitlesSectionTitle = new Label("OpenSubtitles connection");
    Label infoToggle = new Label();
    SVGPath infoSVG = new SVGPath();
    Region infoIcon = new Region();
    Label infoLabel = new Label("Connect FXPlayer to OpenSubtitles to conveniently search for and download matching subtitles for your media files.");

    VBox openSubtitlesSectionInnerContainer = new VBox();

    HBox usernameBox = new HBox();
    Label usernameLabel = new Label("Username:");
    public TextField usernameField = new TextField();

    HBox passwordBox = new HBox();
    Label passwordLabel = new Label("Password:");
    public PasswordField passwordField = new PasswordField();

    StackPane openSubtitlesFooterPane = new StackPane();
    Button createAccountButton = new Button("Create account");
    public Button saveButton = new Button("Save credentials");

    BooleanProperty credentialsChanged = new SimpleBooleanProperty(false);
    public String username = "";
    public String password = "";

    public static final String SUBTITLE_EXTRACTION_ON = "subtitle_extraction_on";
    public static final String SUBTITLE_PARENT_FOLDER_SCAN_ON = "subtitle_parent_folder_scan_on";


    SubtitleSection(SettingsPage settingsPage){
        this.settingsPage = settingsPage;

        this.getChildren().addAll(subtitleSectionTitle, toggleContainer, openSubtitlesSectionWrapper);
        this.setSpacing(25);

        toggleContainer.setPadding(new Insets(0, 0, 10, 0));
        toggleContainer.setSpacing(10);

        extrationToggle = new Toggle(settingsPage, "Extract subtitles embedded into media file containers", extractionOn);
        searchToggle = new Toggle(settingsPage, "Scan parent folder for subtitle file with matching name", searchOn);

        extractionOn.addListener((observableValue, oldValue, newValue) -> settingsPage.menuController.mainController.pref.preferences.putBoolean(SUBTITLE_EXTRACTION_ON, newValue));

        searchOn.addListener((observableValue, oldValue, newValue) -> settingsPage.menuController.mainController.pref.preferences.putBoolean(SUBTITLE_PARENT_FOLDER_SCAN_ON, newValue));

        toggleContainer.getChildren().addAll(extrationToggle, searchToggle);

        openSubtitlesSectionWrapper.getChildren().addAll(openSubtitlesSection, infoLabel);
        openSubtitlesSectionWrapper.setPadding(new Insets(15, 20, 15, 20));
        openSubtitlesSectionWrapper.getStyleClass().add("highlightedSection");


        openSubtitlesSection.getChildren().addAll(openSubtitlesSectionTitlePane, openSubtitlesSectionInnerContainer);


        subtitleSectionTitle.getStyleClass().add("settingsSectionTitle");

        openSubtitlesSection.setSpacing(20);

        openSubtitlesSectionTitlePane.setAlignment(Pos.CENTER_LEFT);
        openSubtitlesSectionTitlePane.getChildren().addAll(openSubtitlesSectionTitle, infoToggle);

        StackPane.setAlignment(infoToggle, Pos.CENTER_RIGHT);

        infoSVG.setContent(SVG.INFORMATION_OUTLINE.getContent());

        infoIcon.setShape(infoSVG);
        infoIcon.setPrefSize(25, 25);
        infoIcon.setMaxSize(25, 25);
        infoIcon.getStyleClass().add("infoIcon");

        infoToggle.setGraphic(infoIcon);
        infoToggle.getStyleClass().add("infoLabel");
        infoToggle.setOnMouseEntered(e -> infoLabel.setVisible(true));
        infoToggle.setOnMouseExited(e -> infoLabel.setVisible(false));

        infoLabel.setVisible(false);
        infoLabel.setMouseTransparent(true);
        infoLabel.getStyleClass().add("settingsInfoWindow");
        infoLabel.setWrapText(true);
        infoLabel.setPrefSize(280, 80);
        infoLabel.setMaxSize(280, 80);
        infoLabel.setPadding(new Insets(5, 10, 5, 10));

        StackPane.setAlignment(infoLabel, Pos.TOP_RIGHT);
        StackPane.setMargin(infoLabel, new Insets(35, 0, 0, 0));


        openSubtitlesSectionTitle.getStyleClass().add("settingsSubsectionTitle");

        openSubtitlesSectionInnerContainer.getChildren().addAll(usernameBox, passwordBox, openSubtitlesFooterPane);
        openSubtitlesSectionInnerContainer.setSpacing(15);

        usernameBox.getChildren().addAll(usernameLabel, usernameField);

        usernameLabel.setPrefWidth(150);
        usernameLabel.getStyleClass().add("settingsText");

        usernameField.setPrefWidth(300);
        usernameField.getStyleClass().add("customTextField");
        usernameField.setPrefHeight(36);
        usernameField.setMinHeight(36);
        usernameField.setMaxHeight(36);
        usernameField.setOnAction(e -> {
            if(settingsPage.menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) settingsPage.menuController.subtitlesController.closeSubtitles();
            if(settingsPage.menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) settingsPage.menuController.playbackSettingsController.closeSettings();
        });

        usernameField.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                if(settingsPage.menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) settingsPage.menuController.subtitlesController.closeSubtitles();
                if(settingsPage.menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) settingsPage.menuController.playbackSettingsController.closeSettings();
            }
        });

        usernameField.textProperty().addListener((observableValue, oldValue, newValue) -> credentialsChanged.set(true));

        passwordBox.getChildren().addAll(passwordLabel, passwordField);

        passwordLabel.setPrefWidth(150);
        passwordLabel.getStyleClass().add("settingsText");

        passwordField.setSkin(new VisiblePasswordFieldSkin(passwordField));
        passwordField.setPrefWidth(300);
        passwordField.getStyleClass().add("customTextField");
        passwordField.setPrefHeight(36);
        passwordField.setMinHeight(36);
        passwordField.setMaxHeight(36);
        passwordField.setOnAction(e -> {
            if(settingsPage.menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) settingsPage.menuController.subtitlesController.closeSubtitles();
            if(settingsPage.menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) settingsPage.menuController.playbackSettingsController.closeSettings();
        });

        passwordField.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                if(settingsPage.menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) settingsPage.menuController.subtitlesController.closeSubtitles();
                if(settingsPage.menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) settingsPage.menuController.playbackSettingsController.closeSettings();
            }
        });

        passwordField.textProperty().addListener((observableValue, oldValue, newValue) -> credentialsChanged.set(true));

        openSubtitlesFooterPane.getChildren().addAll(createAccountButton, saveButton);
        openSubtitlesFooterPane.setAlignment(Pos.CENTER_LEFT);

        createAccountButton.setTranslateX(-11);
        createAccountButton.getStyleClass().add("linkButton");
        createAccountButton.setOnAction(e -> {
            if(settingsPage.menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) settingsPage.menuController.subtitlesController.closeSubtitles();
            if(settingsPage.menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) settingsPage.menuController.playbackSettingsController.closeSettings();

            // open opensubtitles account creation page in web browser
            Utilities.openBrowser("https://www.opensubtitles.org/en/newuser");
        });


        StackPane.setAlignment(saveButton, Pos.CENTER_RIGHT);
        saveButton.getStyleClass().add("mainButton");
        saveButton.disableProperty().bind(credentialsChanged.not());
        saveButton.setOnAction(e -> {
            if(settingsPage.menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) settingsPage.menuController.subtitlesController.closeSubtitles();
            if(settingsPage.menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) settingsPage.menuController.playbackSettingsController.closeSettings();

            try {
                saveCredentials();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

    }

    private void saveCredentials() throws IOException {

        this.username = usernameField.getText();
        this.password = passwordField.getText();

        File file = new File(new File(System.getProperty("user.home"), "FXPlayer"), "OpenSubtitles.txt");

        Files.writeString(file.toPath(), username + "\n" + password, StandardCharsets.UTF_8);

        credentialsChanged.set(false);

        if(!this.username.isEmpty() && !this.password.isEmpty() && !settingsPage.menuController.subtitlesController.openSubtitlesPane.defaultViewInitialized) settingsPage.menuController.subtitlesController.openSubtitlesPane.initializeDefaultView();
    }


    public void readCredentials(){
        File file = new File(new File(System.getProperty("user.home"), "FXPlayer"), "OpenSubtitles.txt");
        if(file.exists() && file.canRead()){
            try {
                List<String> lines = Files.readAllLines(Path.of(file.toURI()), StandardCharsets.UTF_8);
                if(lines.size() >= 2){
                    usernameField.setText(lines.get(0));
                    passwordField.setText(lines.get(1));

                    this.username = lines.get(0);
                    this.password = lines.get(1);

                    credentialsChanged.set(false);

                    if(!username.isEmpty() && !password.isEmpty()) settingsPage.menuController.subtitlesController.openSubtitlesPane.initializeDefaultView();
                }

            } catch (IOException ignored){}
        }
    }

    public void loadPreferences(){
        Preferences preferences = settingsPage.menuController.mainController.pref.preferences;
        extractionOn.set(preferences.getBoolean(SUBTITLE_EXTRACTION_ON, true));
        searchOn.set(preferences.getBoolean(SUBTITLE_PARENT_FOLDER_SCAN_ON, false));
    }
}
