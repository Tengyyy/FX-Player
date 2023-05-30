package tengy.Subtitles;

import com.github.wtekiela.opensub4j.api.OpenSubtitlesClient;
import com.github.wtekiela.opensub4j.impl.OpenSubtitlesClientImpl;
import com.github.wtekiela.opensub4j.response.SubtitleInfo;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import tengy.Menu.MenuState;
import tengy.Menu.Settings.Section;
import tengy.MultiSelectButton;
import tengy.PlaybackSettings.PlaybackSettingsController;
import tengy.SVG;
import tengy.Subtitles.Tasks.LoginTask;
import tengy.Subtitles.Tasks.SearchTask;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static tengy.Utilities.keyboardFocusOff;
import static tengy.Utilities.keyboardFocusOn;

public class OpenSubtitlesPane {

    ScrollPane scrollPane = new ScrollPane();
    VBox container = new VBox();

    StackPane titleContainer = new StackPane();
    HBox titlePane = new HBox();
    Button backButton = new Button();
    Region backIcon = new Region();
    Label titleLabel = new Label();

    SVGPath backSVG = new SVGPath();

    public static HashMap<String, String> languageMap = new Languages();
    public final String[] supportedLanguages = {"Abkhazian", "Afrikaans", "Albanian", "Arabic", "Aragonese", "Armenian", "Assamese", "Asturian", "Azerbaijani","Basque", "Belarusian", "Bengali", "Bosnian", "Breton", "Bulgarian", "Burmese", "Catalan", "Chinese (simplified)", "Chinese (traditional)", "Chinese bilingual", "Croatian", "Czech", "Danish", "Dari", "Dutch", "English", "Esperanto", "Estonian", "Extremaduran", "Finnish", "French", "Gaelic", "Galician", "Georgian", "German", "Greek", "Hebrew", "Hindi", "Hungarian", "Icelandic", "Igbo", "Indonesian", "Interlingua", "Irish", "Italian", "Japanese", "Kannada", "Kazakh", "Khmer", "Korean", "Kurdish", "Latvian", "Lithuanian", "Luxembourgish", "Macedonian", "Malay", "Malayalam", "Manipuri", "Marathi", "Mongolian", "Montenegrin", "Navajo", "Nepali", "Northern Sami", "Norwegian", "Occitan", "Odia", "Persian", "Polish", "Portuguese", "Portuguese (BR)", "Portuguese (MZ)", "Pushto", "Romanian", "Russian", "Santali", "Serbian", "Sindhi", "Sinhalese", "Slovak", "Slovenian", "Somali", "Spanish", "Spanish (EU)", "Spanish (LA)", "Swahili", "Swedish", "Syriac", "Tagalog", "Tamil", "Tatar", "Telugu", "Thai", "Toki Pona", "Turkish", "Turkmen", "Ukrainian", "Urdu", "Vietnamese", "Welsh"};
    public MultiSelectButton languageBox;

    VBox fieldContainer = new VBox();

    Label connectLabel = new Label();
    Button connectButton = new Button();

    HBox titleFieldContainer = new HBox();
    public TextField titleField = new TextField();
    StackPane titleFieldWrapper = new StackPane();
    StackPane titleFieldBorder = new StackPane();

    HBox seasonEpisodeContainer = new HBox();
    public TextField seasonField = new TextField();
    public TextField episodeField = new TextField();

    StackPane imdbFieldContainer = new StackPane();
    public TextField imdbField = new TextField();
    StackPane imdbFieldBorder = new StackPane();

    public VBox fileSearchLabelContainer = new VBox();
    public Label fileSearchLabel = new Label();
    public Label fileSearchExplanationLabel = new Label();

    StackPane searchButtonContainer = new StackPane();
    MFXProgressSpinner searchSpinner = new MFXProgressSpinner();
    HBox searchButtonWrapper = new HBox();
    public Button searchButton = new Button();
    Region searchIcon = new Region();
    SVGPath searchSVG = new SVGPath();

    public SearchOptionsContextMenu searchOptionsContextMenu;
    Button searchOptionsButton = new Button();
    SVGPath chevronUpSVG = new SVGPath();
    Region chevronUpIcon = new Region();

    SubtitlesHome subtitlesHome;
    SubtitlesController subtitlesController;

    public OpenSubtitlesClient osClient = null;

    public int searchState = 0; // 0 - query search (default), 1 - imdb search, 2 - file search

    public BooleanProperty searchInProgress = new SimpleBooleanProperty(false);
    ExecutorService executorService = null;

    public boolean defaultViewInitialized;

    boolean searchOptionsPressed = false;

    List<Node> focusNodes = new ArrayList<>();
    IntegerProperty focus = new SimpleIntegerProperty(-1);


    OpenSubtitlesPane(SubtitlesHome subtitlesHome, SubtitlesController subtitlesController){
        this.subtitlesHome = subtitlesHome;
        this.subtitlesController = subtitlesController;

        backSVG.setContent(SVG.CHEVRON_LEFT.getContent());
        searchSVG.setContent(SVG.MAGNIFY.getContent());

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("settingsScroll");
        scrollPane.setPrefSize(400, 173);
        scrollPane.setMaxSize(400, 173);
        scrollPane.setContent(container);
        scrollPane.setVisible(false);
        scrollPane.setMouseTransparent(true);
        scrollPane.setFitToWidth(true);

        StackPane.setAlignment(scrollPane, Pos.BOTTOM_RIGHT);

        container.setPrefSize(400, 170);
        container.setMaxSize(400, 170);
        container.getChildren().addAll(titleContainer, connectLabel, connectButton);
        container.setAlignment(Pos.TOP_CENTER);

        titleContainer.setPadding(new Insets(0, 10, 0, 10));
        titleContainer.getChildren().add(titlePane);
        titleContainer.getStyleClass().add("settingsPaneTitle");

        titlePane.setMinHeight(50);
        titlePane.setPrefHeight(50);
        titlePane.setMaxHeight(50);
        titlePane.setPrefWidth(Region.USE_COMPUTED_SIZE);
        StackPane.setAlignment(titlePane, Pos.CENTER_LEFT);
        titlePane.getChildren().addAll(backButton, titleLabel);

        backButton.setMinSize(30, 50);
        backButton.setPrefSize(30, 50);
        backButton.setMaxSize(30, 50);
        backButton.getStyleClass().addAll("transparentButton", "settingsMenuButton");
        backButton.setGraphic(backIcon);
        backButton.setFocusTraversable(false);
        backButton.setOnAction((e) -> {
            backButton.requestFocus();
            closeOpenSubtitlesPane();
        });
        backButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(0);
            }
            else{
                keyboardFocusOff(backButton);
                focus.set(-1);
            }
        });

        backButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            backButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        backButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            backButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });

        backIcon.setMinSize(8, 13);
        backIcon.setPrefSize(8, 13);
        backIcon.setMaxSize(8, 13);
        backIcon.getStyleClass().add("graphic");
        backIcon.setShape(backSVG);

        titleLabel.setMinHeight(50);
        titleLabel.setPrefHeight(50);
        titleLabel.setMaxHeight(50);
        titleLabel.setText("OpenSubtitles");
        titleLabel.setCursor(Cursor.HAND);
        titleLabel.getStyleClass().add("settingsPaneText");
        titleLabel.setPadding(new Insets(0, 0, 0, 4));
        titleLabel.setOnMouseClicked((e) -> closeOpenSubtitlesPane());

        languageBox = new MultiSelectButton(subtitlesController.mainController, "Languages");

        StackPane.setAlignment(languageBox, Pos.CENTER_RIGHT);

        languageBox.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(1);
            }
            else {
                keyboardFocusOff(languageBox);
                focus.set(-1);
            }
        });

        initializeLanguageBox();

        VBox.setMargin(connectLabel, new Insets(30, 20, 20, 20));
        connectLabel.setText("Connect to OpenSubtitles to search for subtitles");
        connectLabel.setTextAlignment(TextAlignment.CENTER);
        connectLabel.getStyleClass().add("settingsPaneText");
        connectLabel.setWrapText(true);

        connectButton.getStyleClass().add("mainButton");
        connectButton.setCursor(Cursor.HAND);
        connectButton.setText("Connect");
        connectButton.setPrefWidth(120);
        connectButton.setFocusTraversable(false);

        connectButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            connectButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        connectButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            connectButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });

        connectButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
               focus.set(1);
            }
            else {
                keyboardFocusOff(connectButton);
                focus.set(-1);
            }
        });

        focusNodes.add(backButton);
        focusNodes.add(connectButton);

        connectButton.setOnAction(e -> {
            subtitlesController.closeSubtitles();

            if(subtitlesController.menuController.menuState == MenuState.SETTINGS_OPEN){
                subtitlesController.menuController.settingsPage.animateScroll(Section.SUBTITLES);
            }
            else {
                subtitlesController.menuController.settingsPage.enter();
                subtitlesController.menuController.settingsPage.settingsScroll.setVvalue(subtitlesController.menuController.settingsPage.getTargetScrollValue(Section.SUBTITLES));
            }
        });
        connectButton.setFocusTraversable(false);

        fieldContainer.setPrefHeight(132);
        fieldContainer.setMaxHeight(132);
        fieldContainer.setAlignment(Pos.CENTER);
        fieldContainer.setSpacing(20);
        fieldContainer.getChildren().addAll(titleFieldContainer, seasonEpisodeContainer);

        titleFieldContainer.getChildren().add(titleFieldWrapper);
        titleFieldContainer.setAlignment(Pos.CENTER_LEFT);
        titleFieldContainer.setPadding(new Insets(0, 10, 0, 10));

        titleFieldWrapper.getChildren().addAll(titleField, titleFieldBorder);

        titleFieldBorder.getStyleClass().add("field-border");
        titleFieldBorder.setMouseTransparent(true);
        titleFieldBorder.setVisible(false);
        titleFieldBorder.prefWidthProperty().bind(titleFieldWrapper.widthProperty());
        titleFieldBorder.prefHeightProperty().bind(titleFieldWrapper.heightProperty());

        titleField.getStyleClass().add("customTextField");
        titleField.setPromptText("Enter movie or series title");
        titleField.setPrefHeight(36);
        titleField.setMinHeight(36);
        titleField.setMaxHeight(36);
        titleField.setFocusTraversable(false);
        titleField.setPrefWidth(380);
        titleField.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);");
        titleField.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                titleFieldBorder.setVisible(false);
                focus.set(2);
            }
            else {
                keyboardFocusOff(titleField);
                focus.set(-1);
            }
        });


        seasonEpisodeContainer.getChildren().addAll(seasonField, episodeField);
        seasonEpisodeContainer.setAlignment(Pos.CENTER_LEFT);
        seasonEpisodeContainer.setPadding(new Insets(0, 10, 0, 10));
        seasonEpisodeContainer.setSpacing(30);

        seasonField.getStyleClass().add("customTextField");
        seasonField.setPromptText("Season");
        seasonField.setPrefHeight(36);
        seasonField.setMinHeight(36);
        seasonField.setFocusTraversable(false);
        seasonField.setMaxHeight(36);
        seasonField.setPrefWidth(190);
        seasonField.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);");
        seasonField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if(!newValue.matches("\\d*")) seasonField.setText(oldValue);
        });
        seasonField.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
               focus.set(3);
            }
            else {
                keyboardFocusOff(seasonField);
                focus.set(-1);
            }
        });

        episodeField.getStyleClass().add("customTextField");
        episodeField.setPromptText("Episode");
        episodeField.setPrefHeight(36);
        episodeField.setMinHeight(36);
        episodeField.setMaxHeight(36);
        episodeField.setFocusTraversable(false);
        episodeField.setPrefWidth(190);
        episodeField.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);");
        episodeField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if(!newValue.matches("\\d*")) episodeField.setText(oldValue);
        });
        episodeField.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
               focus.set(4);
            }
            else {
                keyboardFocusOff(episodeField);
                focus.set(-1);
            }
        });


        imdbFieldContainer.getChildren().addAll(imdbField, imdbFieldBorder);
        imdbFieldContainer.setPrefWidth(Region.USE_COMPUTED_SIZE);
        imdbFieldContainer.setPrefHeight(Region.USE_COMPUTED_SIZE);

        imdbFieldBorder.getStyleClass().add("field-border");
        imdbFieldBorder.setMouseTransparent(true);
        imdbFieldBorder.setVisible(false);
        imdbFieldBorder.setPrefSize(300, 36);
        imdbFieldBorder.setMaxSize(300, 36);

        imdbField.getStyleClass().add("customTextField");
        imdbField.setPromptText("Enter IMDb ID of movie/episode");
        imdbField.setPrefHeight(36);
        imdbField.setMinHeight(36);
        imdbField.setMaxHeight(36);
        imdbField.setFocusTraversable(false);
        imdbField.setPrefWidth(300);
        imdbField.setMaxWidth(300);
        imdbField.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);");

        imdbField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if(!newValue.matches("\\d*")) imdbField.setText(oldValue);
        });

        imdbField.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                imdbFieldBorder.setVisible(false);
                focus.set(2);
            }
            else {
                keyboardFocusOff(imdbField);
                focus.set(-1);
            }
        });

        fileSearchLabelContainer.setPadding(new Insets(0, 20, 0, 20));
        fileSearchLabelContainer.getChildren().add(fileSearchLabel);
        fileSearchLabelContainer.setAlignment(Pos.CENTER);
        fileSearchLabelContainer.setSpacing(20);

        fileSearchLabel.setWrapText(true);
        fileSearchLabel.getStyleClass().add("settingsPaneText");
        fileSearchLabel.setText("Select a media file to use this feature");
        fileSearchLabel.setMaxHeight(40);

        fileSearchExplanationLabel.setWrapText(true);
        fileSearchExplanationLabel.getStyleClass().add("settingsPaneText");
        fileSearchExplanationLabel.setText("OpenSubtitles will search for matching subtitles based on file name and size");

        searchButtonContainer.getChildren().addAll(searchSpinner, searchButtonWrapper);
        searchButtonContainer.setPadding(new Insets(0, 10, 10, 10));

        StackPane.setAlignment(searchSpinner, Pos.CENTER);
        searchSpinner.setRadius(10);
        searchSpinner.setColor1(Color.RED);
        searchSpinner.setColor2(Color.RED);
        searchSpinner.setColor3(Color.RED);
        searchSpinner.setColor4(Color.RED);
        searchSpinner.visibleProperty().bind(searchInProgress);


        StackPane.setAlignment(searchButtonWrapper, Pos.CENTER_RIGHT);
        searchButtonWrapper.getChildren().addAll(searchButton, searchOptionsButton);
        searchButtonWrapper.setMaxWidth(Region.USE_PREF_SIZE);
        searchButtonWrapper.setMaxHeight(Region.USE_PREF_SIZE);
        searchButtonWrapper.setAlignment(Pos.CENTER);


        searchIcon.setShape(searchSVG);
        searchIcon.setPrefSize(14, 14);
        searchIcon.setMaxSize(14, 14);
        searchIcon.setMouseTransparent(true);
        searchIcon.getStyleClass().addAll("menuIcon", "graphic");
        searchButton.setCursor(Cursor.HAND);
        searchButton.getStyleClass().add("menuButton");
        searchButton.setId("searchButton");
        searchButton.setText("Search");
        searchButton.setGraphic(searchIcon);
        searchButton.setFocusTraversable(false);

        searchButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            searchButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        searchButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            searchButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });

        searchButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(focusNodes.size() - 2);
            }
            else {
                keyboardFocusOff(searchButton);
                focus.set(-1);
            }
        });

        searchButton.disableProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focusNodes.remove(searchButton);
                focus.set(Math.min(focus.get(), focusNodes.size() - 1));
            }
            else {
                if(defaultViewInitialized){
                    if(!focusNodes.contains(searchButton)) focusNodes.add(focusNodes.size()-1, searchButton);
                    if(searchOptionsButton.isFocused()) focus.set(focusNodes.size()-1);
                }
            }
        });

        searchButton.setOnAction(e -> {
            searchButton.requestFocus();
            attemptSearch();
        });

        chevronUpSVG.setContent(SVG.CHEVRON_UP.getContent());

        chevronUpIcon.setShape(chevronUpSVG);
        chevronUpIcon.setPrefSize(14, 8);
        chevronUpIcon.setMaxSize(14,8);
        chevronUpIcon.setId("chevronUpIcon");
        searchOptionsButton.getStyleClass().add("menuButton");
        searchOptionsButton.setId("searchOptionsButton");
        searchOptionsButton.setGraphic(chevronUpIcon);
        searchOptionsButton.setFocusTraversable(false);

        TranslateTransition chevronDownAnimation = new TranslateTransition(Duration.millis(100), chevronUpIcon);
        chevronDownAnimation.setFromY(chevronUpIcon.getTranslateY());
        chevronDownAnimation.setToY(3);

        TranslateTransition chevronUpAnimation = new TranslateTransition(Duration.millis(100), chevronUpIcon);
        chevronUpAnimation.setFromY(3);
        chevronUpAnimation.setToY(0);

        searchOptionsButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            if(searchOptionsPressed) return;
            searchOptionsPressed = true;
            searchOptionsButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);

            if(!searchOptionsContextMenu.showing) chevronDownAnimation.play();
        });

        searchOptionsButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;

            if(!searchOptionsPressed) return;

            searchOptionsPressed = false;

            searchOptionsButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);

            if(chevronDownAnimation.statusProperty().get() == Animation.Status.RUNNING){
                chevronDownAnimation.setOnFinished(ev -> {
                    chevronUpAnimation.playFromStart();
                    chevronDownAnimation.setOnFinished(null);
                });
            }
            else if(chevronUpIcon.getTranslateY() != 0) chevronUpAnimation.playFromStart();
        });

        searchOptionsButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(focusNodes.size() - 1);
            }
            else {
                keyboardFocusOff(searchOptionsButton);
                focus.set(-1);

                searchOptionsPressed = false;

                if(chevronDownAnimation.statusProperty().get() == Animation.Status.RUNNING){
                    chevronDownAnimation.setOnFinished(ev -> {
                        chevronUpAnimation.playFromStart();
                        chevronDownAnimation.setOnFinished(null);
                    });
                }
                else if(chevronUpIcon.getTranslateY() != 0) chevronUpAnimation.playFromStart();
            }
        });

        searchOptionsButton.setOnMousePressed(e -> chevronDownAnimation.play());
        searchOptionsButton.setOnMouseReleased(e -> {
            if(chevronDownAnimation.statusProperty().get() == Animation.Status.RUNNING){
                chevronDownAnimation.setOnFinished(ev -> {
                    chevronUpAnimation.playFromStart();
                    chevronDownAnimation.setOnFinished(null);
                });
            }
            else if(chevronUpIcon.getTranslateY() != 0) chevronUpAnimation.playFromStart();
        });

        searchOptionsButton.setOnAction(e -> {
            searchOptionsButton.requestFocus();

            if(searchOptionsContextMenu.showing) searchOptionsContextMenu.hide();
            else searchOptionsContextMenu.showOptions(true);
        });

        subtitlesController.subtitlesPane.getChildren().add(scrollPane);

        searchInProgress.addListener((observableValue, oldValue, newValue) -> {
            if(newValue) {
                searchButton.setDisable(true);
            }
            else if(searchState != 2 || subtitlesController.menuController.queuePage.queueBox.activeItem.get() != null){
                searchButton.setDisable(false);
            }
        });


        Platform.runLater(() -> searchOptionsContextMenu = new SearchOptionsContextMenu(this));
    }

    public void closeOpenSubtitlesPane() {
        if (subtitlesController.animating.get()) return;

        subtitlesController.subtitlesState = SubtitlesState.HOME_OPEN;

        subtitlesController.subtitlesHome.scrollPane.setVisible(true);
        subtitlesController.subtitlesHome.scrollPane.setMouseTransparent(false);


        Timeline clipHeightTimeline = new Timeline();
        clipHeightTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(subtitlesController.clip.heightProperty(), subtitlesController.subtitlesHome.scrollPane.getHeight())));


        Timeline clipWidthTimeline = new Timeline();
        clipWidthTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(subtitlesController.clip.widthProperty(), subtitlesController.subtitlesHome.scrollPane.getWidth())));


        TranslateTransition captionsPaneTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), subtitlesController.subtitlesHome.scrollPane);
        captionsPaneTransition.setFromX(-scrollPane.getWidth());
        captionsPaneTransition.setToX(0);

        TranslateTransition openSubtitlesTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), scrollPane);
        openSubtitlesTransition.setFromX(0);
        openSubtitlesTransition.setToX(scrollPane.getWidth());


        ParallelTransition parallelTransition = new ParallelTransition(clipHeightTimeline, clipWidthTimeline, captionsPaneTransition, openSubtitlesTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            subtitlesController.animating.set(false);
            scrollPane.setVisible(false);
            scrollPane.setMouseTransparent(true);
            scrollPane.setTranslateX(0);
            subtitlesController.clip.setHeight(subtitlesController.subtitlesHome.scrollPane.getPrefHeight());

            languageBox.scrollPane.setVvalue(0);

            imdbFieldBorder.setVisible(false);
            titleFieldBorder.setVisible(false);
        });

        parallelTransition.play();
        subtitlesController.animating.set(true);
    }

    private void initializeLanguageBox(){
        for(String string : supportedLanguages){
            languageBox.addItem(string);
        }
    }

    private void attemptSearch(){

        if(searchInProgress.get()) return;

        if(searchState == 0){
            if(!titleField.getText().isEmpty()) search();
            else {
                titleFieldBorder.setVisible(true);
            }
        }
        else if(searchState == 1){
            if(!imdbField.getText().isEmpty()) search();
            else {
                imdbFieldBorder.setVisible(true);
            }
        }
        else if(searchState == 2){
            if(subtitlesController.menuController.queuePage.queueBox.activeItem.get() != null) search();
        }
    }

    private void search() {

        searchInProgress.set(true);

        subtitlesController.openSubtitlesResultsPane.clearResults();

        if(osClient == null){
            try {
                URL serverUrl = new URL("https", "api.opensubtitles.org", 443, "/xml-rpc");
                osClient = new OpenSubtitlesClientImpl(serverUrl);
            } catch (MalformedURLException e) {
                searchFail("OpenSubtitles server URL malformed.");
                return;
            }
        }

        ExecutorService executorService = Executors.newFixedThreadPool(1);

        LoginTask loginTask = new LoginTask(subtitlesController, this,
                subtitlesController.menuController.settingsPage.subtitleSection.username,
                subtitlesController.menuController.settingsPage.subtitleSection.password);

        loginTask.setOnSucceeded(e -> {
            Integer result = loginTask.getValue();
            if(result == -1) searchFail("Unable to connect to OpenSubtitles service.");
            else if(result == 0) searchFail("Failed to login to OpenSubtitles. Make sure your login credentials are correct.");
            else {
                SearchTask searchTask = new SearchTask(subtitlesController, this);
                searchTask.setOnSucceeded(successEvent -> {

                    List<SubtitleInfo> subtitleInfoList = searchTask.getValue();
                    List<SubtitleInfo> filteredList = new ArrayList<>();
                    for(SubtitleInfo subtitleInfo : subtitleInfoList){
                        if(subtitleInfo.getFormat().equals("srt")) filteredList.add(subtitleInfo);
                    }
                    if(filteredList.isEmpty()) searchFail("No subtitles found.");
                    else {
                        for(SubtitleInfo subtitleInfo : filteredList){
                            subtitlesController.openSubtitlesResultsPane.addResult(
                                    new Result(subtitlesController,
                                            subtitlesController.openSubtitlesResultsPane,
                                            subtitleInfo,
                                            osClient
                                    )
                            );
                        }

                        if(subtitlesController.subtitlesState == SubtitlesState.OPENSUBTITLES_OPEN) openResultsPane();
                        searchInProgress.set(false);
                        executorService.shutdown();
                    }
                });

                searchTask.setOnFailed(failEvent -> searchFail("Subtitle search failed. Make sure your search parameters are correct."));
                executorService.execute(searchTask);
            }
        });

        loginTask.setOnFailed(e -> searchFail("OpenSubtitles login failed."));

        executorService.execute(loginTask);
    }

    private void openResultsPane(){

        if(subtitlesController.animating.get()) return;

        subtitlesController.subtitlesState = SubtitlesState.OPENSUBTITLES_RESULTS_OPEN;

        subtitlesController.openSubtitlesResultsPane.scrollPane.setVisible(true);
        subtitlesController.openSubtitlesResultsPane.scrollPane.setMouseTransparent(false);

        Timeline clipHeightTimeline = new Timeline();
        clipHeightTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(subtitlesController.clip.heightProperty(), subtitlesController.openSubtitlesResultsPane.scrollPane.getPrefHeight())));

        Timeline clipWidthTimeline = new Timeline();
        clipWidthTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), new KeyValue(subtitlesController.clip.widthProperty(), subtitlesController.openSubtitlesResultsPane.scrollPane.getWidth())));


        TranslateTransition captionsTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), scrollPane);
        captionsTransition.setFromX(0);
        captionsTransition.setToX(-subtitlesController.openSubtitlesResultsPane.scrollPane.getWidth());

        TranslateTransition openSubtitlesResultsTransition = new TranslateTransition(Duration.millis(PlaybackSettingsController.ANIMATION_SPEED), subtitlesController.openSubtitlesResultsPane.scrollPane);
        openSubtitlesResultsTransition.setFromX(subtitlesController.openSubtitlesResultsPane.scrollPane.getWidth());
        openSubtitlesResultsTransition.setToX(0);


        ParallelTransition parallelTransition = new ParallelTransition(clipHeightTimeline, clipWidthTimeline, captionsTransition, openSubtitlesResultsTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            subtitlesController.animating.set(false);
            scrollPane.setVisible(false);
            scrollPane.setMouseTransparent(true);
            scrollPane.setTranslateX(0);
            scrollPane.setVvalue(0);
            subtitlesController.clip.setHeight(subtitlesController.openSubtitlesResultsPane.scrollPane.getPrefHeight());

            languageBox.scrollPane.setVvalue(0);
        });

        parallelTransition.play();
        subtitlesController.animating.set(true);
    }

    public void setFileSearch(){
        searchOptionsContextMenu.queryCheck.setVisible(false);
        searchOptionsContextMenu.imdbCheck.setVisible(false);
        searchOptionsContextMenu.fileCheck.setVisible(true);

        searchState = 2;

        fieldContainer.getChildren().clear();
        fieldContainer.getChildren().add(fileSearchLabelContainer);

        focusNodes.clear();
        focusNodes.add(backButton);
        focusNodes.add(languageBox);
        focusNodes.add(searchButton);
        focusNodes.add(searchOptionsButton);

        focus.set(3);

        imdbFieldBorder.setVisible(false);
        titleFieldBorder.setVisible(false);

        if(subtitlesController.menuController.queuePage.queueBox.activeItem.get() == null){
            searchButton.setDisable(true);
        }
    }

    public void setImdbSearch(){
        searchOptionsContextMenu.queryCheck.setVisible(false);
        searchOptionsContextMenu.imdbCheck.setVisible(true);
        searchOptionsContextMenu.fileCheck.setVisible(false);

        searchState = 1;

        fieldContainer.getChildren().clear();
        fieldContainer.getChildren().add(imdbFieldContainer);

        focusNodes.clear();
        focusNodes.add(backButton);
        focusNodes.add(languageBox);
        focusNodes.add(imdbField);
        focusNodes.add(searchButton);
        focusNodes.add(searchOptionsButton);

        focus.set(4);

        titleFieldBorder.setVisible(false);

        if(!searchInProgress.get()) searchButton.setDisable(false);
    }


    public void setQuerySearch(){
        searchOptionsContextMenu.queryCheck.setVisible(true);
        searchOptionsContextMenu.imdbCheck.setVisible(false);
        searchOptionsContextMenu.fileCheck.setVisible(false);

        searchState = 0;

        fieldContainer.getChildren().clear();
        fieldContainer.getChildren().addAll(titleFieldContainer, seasonEpisodeContainer);

        focusNodes.clear();
        focusNodes.add(backButton);
        focusNodes.add(languageBox);
        focusNodes.add(titleField);
        focusNodes.add(seasonField);
        focusNodes.add(episodeField);
        focusNodes.add(searchButton);
        focusNodes.add(searchOptionsButton);

        focus.set(6);

        imdbFieldBorder.setVisible(false);

        if(!searchInProgress.get()) searchButton.setDisable(false);
    }

    public void initializeDefaultView(){

        if(defaultViewInitialized) return;

        defaultViewInitialized = true;

        container.getChildren().clear();
        if(!titleContainer.getChildren().contains(languageBox)) titleContainer.getChildren().add(languageBox);

        container.setPrefHeight(230);
        container.setMaxHeight(230);

        scrollPane.setPrefHeight(233);
        scrollPane.setMaxHeight(233);

        if(subtitlesController.subtitlesState == SubtitlesState.OPENSUBTITLES_OPEN) subtitlesController.clip.setHeight(233);

        container.getChildren().addAll(titleContainer, fieldContainer, searchButtonContainer);

        focusNodes.clear();
        focusNodes.add(backButton);
        focusNodes.add(languageBox);
        focusNodes.add(titleField);
        focusNodes.add(seasonField);
        focusNodes.add(episodeField);
        focusNodes.add(searchButton);
        focusNodes.add(searchOptionsButton);

        focus.set(-1);
    }

    private void searchFail(String text){
        subtitlesController.openSubtitlesResultsPane.errorLabel.setText(text);
        subtitlesController.openSubtitlesResultsPane.clearResults();
        subtitlesController.openSubtitlesResultsPane.resultBox.getChildren().add(subtitlesController.openSubtitlesResultsPane.errorLabel);

        if(subtitlesController.subtitlesState == SubtitlesState.OPENSUBTITLES_OPEN) openResultsPane();
        searchInProgress.set(false);

        if(executorService != null) executorService.shutdown();
    }

    public void focusForward(){
        int newFocus;

        if(focus.get() >= focusNodes.size() - 1 || focus.get() == -1) newFocus = 0;
        else newFocus = focus.get() + 1;

        keyboardFocusOn(focusNodes.get(newFocus));
    }

    public void focusBackward(){
        int newFocus;

        if(focus.get() == 0) newFocus = focusNodes.size() - 1;
        else if(focus.get() == -1) newFocus = 0;
        else newFocus = focus.get() - 1;

        keyboardFocusOn(focusNodes.get(newFocus));
    }
}
