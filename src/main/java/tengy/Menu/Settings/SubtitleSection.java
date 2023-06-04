package tengy.Menu.Settings;

import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import tengy.CustomMenuButton;
import tengy.Menu.FocusableMenuButton;
import tengy.PlaybackSettings.PlaybackSettingsState;
import tengy.SVG;
import tengy.Subtitles.SubtitlesState;
import tengy.Utilities;
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
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import static tengy.Utilities.keyboardFocusOff;
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
    FocusableMenuButton createAccountButton = new FocusableMenuButton();
    public FocusableMenuButton saveButton = new FocusableMenuButton();

    BooleanProperty credentialsChanged = new SimpleBooleanProperty(false);
    public String username = "";
    public String password = "";

    public static final String SUBTITLE_EXTRACTION_ON = "subtitle_extraction_on";
    public static final String SUBTITLE_PARENT_FOLDER_SCAN_ON = "subtitle_parent_folder_scan_on";
    public static final String SUBTITLES_LANGUAGE = "subtitles_language";

    IntegerProperty focus = new SimpleIntegerProperty(-1);
    List<Node> focusNodes = new ArrayList<>();

    boolean infoToggleHover = false;


    SubtitleSection(SettingsPage settingsPage){
        this.settingsPage = settingsPage;

        this.getChildren().addAll(subtitleSectionTitle, toggleContainer, openSubtitlesSectionWrapper);

        toggleContainer.setPadding(new Insets(0, 0, 10, 0));
        toggleContainer.setSpacing(10);
        VBox.setMargin(toggleContainer, new Insets(5, 0 , 0, 0));

        VBox.setMargin(openSubtitlesSectionWrapper, new Insets(10, 0 , 0, 0));


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
        infoIcon.getStyleClass().add("graphic");

        infoToggle.setGraphic(infoIcon);
        infoToggle.getStyleClass().add("infoLabel");

        infoToggle.setOnMouseEntered(e -> {
            infoToggleHover = true;
            infoLabel.setVisible(true);
        });
        infoToggle.setOnMouseExited(e -> {
            infoToggleHover = false;
            if(!infoToggle.isFocused()) infoLabel.setVisible(false);
        });

        infoToggle.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(3);
                settingsPage.focus.set(0);

                infoLabel.setVisible(true);
            }
            else {
                keyboardFocusOff(infoToggle);
                focus.set(-1);
                settingsPage.focus.set(-1);

                if(!infoToggleHover) infoLabel.setVisible(false);
            }
        });

        infoLabel.setVisible(false);
        infoLabel.setMouseTransparent(true);
        infoLabel.getStyleClass().add("settingsInfoWindow");
        infoLabel.setWrapText(true);
        infoLabel.setPrefSize(290, 80);
        infoLabel.setMaxSize(290, 80);
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
        usernameField.setFocusTraversable(false);
        usernameField.setOnAction(e -> {
            if(settingsPage.menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) settingsPage.menuController.subtitlesController.closeSubtitles();
            if(settingsPage.menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) settingsPage.menuController.playbackSettingsController.closeSettings();
        });

        usernameField.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                if(settingsPage.menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) settingsPage.menuController.subtitlesController.closeSubtitles();
                if(settingsPage.menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) settingsPage.menuController.playbackSettingsController.closeSettings();

                focus.set(4);
                settingsPage.focus.set(0);
            }
            else {
                focus.set(-1);
                settingsPage.focus.set(-1);
                keyboardFocusOff(usernameField);
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
        passwordField.setFocusTraversable(false);
        passwordField.setOnAction(e -> {
            if(settingsPage.menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) settingsPage.menuController.subtitlesController.closeSubtitles();
            if(settingsPage.menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) settingsPage.menuController.playbackSettingsController.closeSettings();
        });

        passwordField.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                if(settingsPage.menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) settingsPage.menuController.subtitlesController.closeSubtitles();
                if(settingsPage.menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) settingsPage.menuController.playbackSettingsController.closeSettings();

                focus.set(5);
                settingsPage.focus.set(0);
            }
            else {
                focus.set(-1);
                settingsPage.focus.set(-1);
                keyboardFocusOff(passwordField);
            }
        });

        passwordField.textProperty().addListener((observableValue, oldValue, newValue) -> credentialsChanged.set(true));

        openSubtitlesFooterPane.getChildren().addAll(createAccountButton, saveButton);
        openSubtitlesFooterPane.setAlignment(Pos.CENTER_LEFT);

        createAccountButton.setText("Create account");
        createAccountButton.setTranslateX(-11);
        createAccountButton.getStyleClass().add("linkButton");
        createAccountButton.setOnAction(e -> {
            createAccountButton.requestFocus();
            if(settingsPage.menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) settingsPage.menuController.subtitlesController.closeSubtitles();
            if(settingsPage.menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) settingsPage.menuController.playbackSettingsController.closeSettings();

            // open opensubtitles account creation page in web browser
            Utilities.openBrowser("https://www.opensubtitles.org/en/newuser");
        });

        createAccountButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(6);
                settingsPage.focus.set(0);
            }
            else{
                keyboardFocusOff(createAccountButton);
                focus.set(-1);
                settingsPage.focus.set(-1);
            }
        });

        createAccountButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            createAccountButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        createAccountButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            createAccountButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });


        StackPane.setAlignment(saveButton, Pos.CENTER_RIGHT);
        saveButton.setText("Save credentials");
        saveButton.getStyleClass().add("mainButton");
        saveButton.disableProperty().bind(credentialsChanged.not());
        saveButton.disabledProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue) focusNodes.remove(saveButton);
            else if(!focusNodes.contains(saveButton)) focusNodes.add(saveButton);
        });

        saveButton.setOnAction(e -> {
            saveButton.requestFocus();
            if(settingsPage.menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) settingsPage.menuController.subtitlesController.closeSubtitles();
            if(settingsPage.menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) settingsPage.menuController.playbackSettingsController.closeSettings();

            try {
                saveCredentials();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        saveButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(7);
                settingsPage.focus.set(0);
            }
            else{
                keyboardFocusOff(saveButton);
                focus.set(-1);
                settingsPage.focus.set(-1);
            }
        });

        saveButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            saveButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        saveButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            saveButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });

        focusNodes.add(extrationToggle);
        focusNodes.add(searchToggle);
        focusNodes.add(languageItem.customMenuButton);
        focusNodes.add(infoToggle);
        focusNodes.add(usernameField);
        focusNodes.add(passwordField);
        focusNodes.add(createAccountButton);
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

    public void loadLanguageBox(){
        for(String string : settingsPage.menuController.subtitlesController.openSubtitlesPane.supportedLanguages){
            languageItem.add(string);
        }
    }

    public void loadPreferences(){
        Preferences preferences = settingsPage.menuController.mainController.pref.preferences;
        extractionOn.set(preferences.getBoolean(SUBTITLE_EXTRACTION_ON, true));
        searchOn.set(preferences.getBoolean(SUBTITLE_PARENT_FOLDER_SCAN_ON, false));

        String language = preferences.get(SUBTITLES_LANGUAGE, "English");
        languageItem.customMenuButton.setValue(language);

        settingsPage.menuController.subtitlesController.openSubtitlesPane.languageBox.select(language);
    }

    @Override
    public boolean focusForward(){

        if(focus.get() >= focusNodes.size() - 1)
            return true;

        Node node = focusNodes.get(focus.get() + 1);
        if(node instanceof CustomMenuButton){
            Utilities.checkScrollDown(settingsPage.settingsScroll, languageItem);
        }
        else if(focus.get() > 1){
            Utilities.checkScrollDown(settingsPage.settingsScroll, openSubtitlesSectionWrapper);
        }
        else Utilities.checkScrollDown(settingsPage.settingsScroll, node);

        keyboardFocusOn(node);

        return false;
    }

    @Override
    public boolean focusBackward(){

        if(focus.get() == 0)
            return true;

        if(focus.get() < 0){
            keyboardFocusOn(focusNodes.get(focusNodes.size() - 1));
            Utilities.checkScrollUp(settingsPage.settingsScroll, openSubtitlesSectionWrapper);
        }
        else {
            Node node = focusNodes.get(focus.get() - 1);
            if(node instanceof CustomMenuButton){
                Utilities.checkScrollUp(settingsPage.settingsScroll, languageItem);
            }
            else if(focus.get() > 3){
                Utilities.checkScrollUp(settingsPage.settingsScroll, openSubtitlesSectionWrapper);
            }
            else Utilities.checkScrollDown(settingsPage.settingsScroll, node);


            keyboardFocusOn(node);
        }

        return false;
    }

    @Override
    public void setFocus(int value){
        this.focus.set(value);
    }
}
