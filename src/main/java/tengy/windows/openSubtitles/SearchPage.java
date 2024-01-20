package tengy.windows.openSubtitles;

import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.animation.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;
import tengy.*;
import tengy.openSubtitles.OpenSubtitles;
import tengy.openSubtitles.models.features.Subtitle;
import tengy.openSubtitles.models.subtitles.SubtitlesQuery;
import tengy.openSubtitles.models.subtitles.SubtitlesResult;
import tengy.skins.ClearableTextFieldSkin;
import tengy.windows.openSubtitles.tasks.LoginTask;
import tengy.windows.openSubtitles.tasks.SearchTask;
import tengy.windows.WindowState;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static tengy.openSubtitles.models.subtitles.SubtitlesQuery.Settings.*;
import static tengy.Utilities.*;

public class SearchPage extends VBox implements Page{

    public OpenSubtitlesWindow openSubtitlesWindow;

    public ScrollPane scrollPane;

    VBox content = new VBox();

    StackPane titleContainer = new StackPane();
    Label title = new Label("OpenSubtitles");

    public MultiSelectButton languageButton;

    public Button hashSearchButton = new Button("Search by hash");

    public static final String[] supportedLanguages = {"Afrikaans", "Albanian", "Arabic", "Aragonese", "Armenian", "Asturian", "Basque", "Belarusian", "Bengali", "Bosnian", "Breton", "Bulgarian", "Burmese", "Catalan", "Chinese (simplified)", "Chinese (traditional)", "Chinese bilingual", "Croatian", "Czech", "Danish", "Dutch", "English", "Esperanto", "Estonian", "Finnish", "French", "Galician", "Georgian", "German", "Greek", "Hebrew", "Hindi", "Hungarian", "Icelandic", "Indonesian", "Italian", "Japanese", "Kazakh", "Khmer", "Korean", "Latvian", "Lithuanian", "Luxembourgish", "Macedonian", "Malay", "Malayalam", "Manipuri", "Mongolian", "Montenegrin", "Norwegian", "Occitan", "Persian", "Polish", "Portuguese", "Portuguese (BR)", "Romanian", "Russian", "Serbian", "Sinhalese", "Slovak", "Slovenian", "Spanish", "Swahili", "Swedish", "Syriac", "Tagalog", "Tamil", "Telugu", "Thai", "Turkish", "Ukrainian", "Urdu", "Uzbek", "Vietnamese"};

    StackPane titleBox = new StackPane();

    Label titleLabel = new Label("Title:");
    public TextField titleField = new TextField();
    public Button titleSearchButton = new Button("Search");

    StackPane seasonBox = new StackPane();
    Label seasonLabel = new Label("Season:");
    public TextField seasonField = new TextField();

    StackPane episodeBox = new StackPane();
    Label episodeLabel = new Label("Episode:");
    public TextField episodeField = new TextField();

    StackPane advancedSearchOptionsPane = new StackPane();
    Button advancedOptionsButton = new Button("Advanced search");
    Region advancedOptionsIcon = new Region();
    SVGPath chevronDownSVG = new SVGPath();

    StackPane advancedOptionsBoxWrapper = new StackPane();
    ClippedNode clippedNode;
    VBox advancedOptionsBox = new VBox();

    StackPane imdbBox = new StackPane();
    Label imdbLabel = new Label("IMDb ID:");
    public TextField imdbField = new TextField();

    StackPane impairedHearingBox = new StackPane();
    Label impairedHearingLabel = new Label("Hearing impaired subtitles:");
    HBox impairedHearingButtonContainer = new HBox();
    public Button impairedHearingExcludeButton = new Button("Exclude");
    public Button impairedHearingIncludeButton = new Button("Include");
    public Button impairedHearingOnlyButton = new Button("Only");
    SubtitlesQuery.Settings impairedHearingState = INCLUDE;

    StackPane yearBox = new StackPane();
    Label yearLabel = new Label("Year:");
    public TextField yearField = new TextField();

    StackPane foreignPartsBox = new StackPane();
    Label foreignPartsLabel = new Label("Forced subtitles:");
    HBox foreignPartsButtonContainer = new HBox();
    public Button foreignPartsExcludeButton = new Button("Exclude");
    public Button foreignPartsIncludeButton = new Button("Include");
    public Button foreignPartsOnlyButton = new Button("Only");
    SubtitlesQuery.Settings foreignPartsState = INCLUDE;

    HBox toggleContainer = new HBox();

    StackPane movieOnlyBox = new StackPane();
    Label movieOnlyLabel = new Label("Only search for movies:");
    public MFXToggleButton movieOnlyToggle = new MFXToggleButton();
    Label movieOnlyToggleLabel = new Label("Off");

    StackPane aiTranslatedBox = new StackPane();
    Label aiTranslatedLabel = new Label("Include AI translated subtitles:");
    public MFXToggleButton aiTranslatedToggle = new MFXToggleButton();
    Label aiTranslatedToggleLabel = new Label("On");


    StackPane progressPane = new StackPane();
    MFXProgressSpinner searchSpinner = new MFXProgressSpinner();
    Label errorLabel = new Label();


    IntegerProperty focus = new SimpleIntegerProperty(-1);
    List<Node> focusNodes = new ArrayList<>();

    public BooleanProperty searchInProgress = new SimpleBooleanProperty(false);
    ExecutorService executorService = null;

    boolean movieTogglePressed = false;
    boolean aiTogglePressed = false;


    boolean advancedOptionsShowing = false;
    ParallelTransition advancedOptionsTransition = null;

    SearchPage(OpenSubtitlesWindow openSubtitlesWindow){
        this.openSubtitlesWindow = openSubtitlesWindow;


        titleContainer.setPadding(new Insets(15, 20, 15, 0));
        titleContainer.setOnMouseClicked(e -> openSubtitlesWindow.window.requestFocus());


        StackPane.setAlignment(title, Pos.CENTER_LEFT);
        StackPane.setMargin(title, new Insets(0, 0, 0, 50));
        title.getStyleClass().add("popupWindowTitle");


        languageButton = new MultiSelectButton(openSubtitlesWindow.mainController, "Languages");
        languageButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                openSubtitlesWindow.focus.set(0);
                focus.set(0);
            }
            else {
                keyboardFocusOff(languageButton);
                openSubtitlesWindow.focus.set(-1);
                focus.set(-1);
            }
        });

        StackPane.setAlignment(languageButton, Pos.CENTER_RIGHT);

        initializeLanguageBox();


        titleContainer.getChildren().addAll(title, languageButton);

        scrollPane = new ScrollPane() {
            ScrollBar vertical;

            @Override
            protected void layoutChildren() {
                super.layoutChildren();
                if (vertical == null) {
                    vertical = (ScrollBar) lookup(".scroll-bar:vertical");
                    vertical.visibleProperty().addListener((obs, old, val) -> updatePadding(val));
                }
            }
        };

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("menuScroll");
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        scrollPane.setBackground(Background.EMPTY);
        scrollPane.setContent(content);

        content.setPadding(new Insets(15, 20, 0, 15));
        content.setSpacing(15);

        clippedNode = new ClippedNode(advancedOptionsBox);
        Rectangle advancedOptionsClip = new Rectangle();
        advancedOptionsClip.widthProperty().bind(advancedOptionsBoxWrapper.widthProperty());
        advancedOptionsClip.heightProperty().bind(advancedOptionsBoxWrapper.heightProperty());
        advancedOptionsBoxWrapper.setClip(advancedOptionsClip);
        advancedOptionsBoxWrapper.getChildren().add(clippedNode);

        content.getChildren().addAll(titleBox, seasonBox, episodeBox, advancedSearchOptionsPane, advancedOptionsBoxWrapper);

        advancedOptionsBox.setSpacing(15);
        advancedOptionsBox.setPadding(new Insets(0, 0, 10, 0));
        advancedOptionsBox.getChildren().addAll(imdbBox, yearBox, toggleContainer);

        SVGPath searchSVG = new SVGPath();
        searchSVG.setContent(SVG.MAGNIFY.getContent());

        titleBox.getChildren().addAll(titleLabel, titleField, titleSearchButton);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        titleLabel.getStyleClass().add("toggleText");
        titleLabel.setMinWidth(80);
        titleLabel.setPrefWidth(80);
        titleLabel.setMaxWidth(80);
        titleLabel.setMouseTransparent(true);

        StackPane.setMargin(titleField, new Insets(0, 0, 0, 80));
        titleField.setSkin(new ClearableTextFieldSkin(titleField));
        titleField.getStyleClass().add("customTextField");
        titleField.setPrefHeight(36);
        titleField.setMinHeight(36);
        titleField.setMaxHeight(36);
        titleField.setFocusTraversable(false);
        titleField.setPrefWidth(300);
        titleField.setMaxWidth(300);
        titleField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            titleSearchButton.setDisable(newValue.isEmpty() && imdbField.getText().isEmpty());
        });

        titleField.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(1);
                openSubtitlesWindow.focus.set(0);
            }
            else {
                keyboardFocusOff(titleField);
                focus.set(-1);
                openSubtitlesWindow.focus.set(-1);
            }
        });

        Region titleSearchIcon = new Region();
        titleSearchIcon.setShape(searchSVG);
        titleSearchIcon.setPrefSize(14, 14);
        titleSearchIcon.setMaxSize(14, 14);
        titleSearchIcon.setMouseTransparent(true);
        titleSearchIcon.getStyleClass().addAll("menuIcon", "graphic");

        StackPane.setAlignment(titleSearchButton, Pos.CENTER_RIGHT);
        titleSearchButton.setGraphic(titleSearchIcon);
        titleSearchButton.getStyleClass().add("menuButton");
        titleSearchButton.setAlignment(Pos.CENTER_LEFT);
        titleSearchButton.setPrefWidth(150);
        titleSearchButton.setOnMouseClicked(e -> titleSearchButton.requestFocus());
        titleSearchButton.setOnAction(e -> search(false));
        titleSearchButton.setFocusTraversable(false);
        titleSearchButton.setDisable(true);

        titleSearchButton.disabledProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focusNodes.remove(titleSearchButton);
            }
            else {
                if(!focusNodes.contains(titleSearchButton)){
                    focusNodes.add(4,  titleSearchButton);
                }
            }
        });

        titleSearchButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(4);
                openSubtitlesWindow.focus.set(0);
            }
            else {
                keyboardFocusOff(titleSearchButton);
                focus.set(-1);
                openSubtitlesWindow.focus.set(-1);
            }
        });

        titleSearchButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            titleSearchButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        titleSearchButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            titleSearchButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });

        seasonBox.getChildren().addAll(seasonLabel, seasonField, hashSearchButton);
        seasonBox.setAlignment(Pos.CENTER_LEFT);

        seasonLabel.getStyleClass().add("toggleText");
        seasonLabel.setMinWidth(80);
        seasonLabel.setPrefWidth(80);
        seasonLabel.setMaxWidth(80);
        seasonLabel.setMouseTransparent(true);

        StackPane.setMargin(seasonField, new Insets(0, 0, 0, 80));
        seasonField.setSkin(new ClearableTextFieldSkin(seasonField));
        seasonField.getStyleClass().add("customTextField");
        seasonField.setPrefHeight(36);
        seasonField.setMinHeight(36);
        seasonField.setMaxHeight(36);
        seasonField.setFocusTraversable(false);
        seasonField.setPrefWidth(300);
        seasonField.setMaxWidth(300);
        seasonField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if(!newValue.matches("\\d*")) seasonField.setText(oldValue);
        });

        seasonField.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(2);
                openSubtitlesWindow.focus.set(0);
            }
            else {
                keyboardFocusOff(seasonField);
                focus.set(-1);
                openSubtitlesWindow.focus.set(-1);
            }
        });

        Region hashSearchIcon = new Region();
        hashSearchIcon.setShape(searchSVG);
        hashSearchIcon.setPrefSize(14, 14);
        hashSearchIcon.setMaxSize(14, 14);
        hashSearchIcon.setMouseTransparent(true);
        hashSearchIcon.getStyleClass().addAll("menuIcon", "graphic");

        StackPane.setAlignment(hashSearchButton, Pos.CENTER_RIGHT);
        hashSearchButton.getStyleClass().add("menuButton");
        hashSearchButton.setAlignment(Pos.CENTER_LEFT);
        hashSearchButton.setPrefWidth(150);
        hashSearchButton.setOnMouseClicked(e -> hashSearchButton.requestFocus());
        hashSearchButton.setOnAction(e -> search(true));
        hashSearchButton.setFocusTraversable(false);
        hashSearchButton.setDisable(true);
        hashSearchButton.disabledProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue) focusNodes.remove(hashSearchButton);
            else if(!focusNodes.contains(hashSearchButton)){
                if(titleSearchButton.isDisabled()) focusNodes.add(4, hashSearchButton);
                else focusNodes.add(5, hashSearchButton);
            }
        });

        hashSearchButton.setGraphic(hashSearchIcon);
        hashSearchButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                if(titleSearchButton.isDisabled()) focus.set(4);
                else focus.set(5);
                openSubtitlesWindow.focus.set(0);
            }
            else{
                keyboardFocusOff(hashSearchButton);
                focus.set(-1);
                openSubtitlesWindow.focus.set(-1);
            }
        });

        hashSearchButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            hashSearchButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        hashSearchButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            hashSearchButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });

        episodeBox.getChildren().addAll(episodeLabel, episodeField, progressPane);
        episodeBox.setAlignment(Pos.CENTER_LEFT);

        episodeLabel.getStyleClass().add("toggleText");
        episodeLabel.setMinWidth(80);
        episodeLabel.setPrefWidth(80);
        episodeLabel.setMaxWidth(80);
        episodeLabel.setMouseTransparent(true);

        StackPane.setMargin(episodeField, new Insets(0, 0, 0, 80));
        episodeField.setSkin(new ClearableTextFieldSkin(episodeField));
        episodeField.getStyleClass().add("customTextField");
        episodeField.setPrefHeight(36);
        episodeField.setMinHeight(36);
        episodeField.setMaxHeight(36);
        episodeField.setFocusTraversable(false);
        episodeField.setPrefWidth(300);
        episodeField.setMaxWidth(300);
        episodeField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if(!newValue.matches("\\d*")) episodeField.setText(oldValue);
        });
        episodeField.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(3);
                openSubtitlesWindow.focus.set(0);
            }
            else {
                keyboardFocusOff(episodeField);
                focus.set(-1);
                openSubtitlesWindow.focus.set(-1);
            }
        });

        StackPane.setAlignment(progressPane, Pos.CENTER_RIGHT);
        progressPane.getChildren().addAll(searchSpinner, errorLabel);
        progressPane.setAlignment(Pos.CENTER);
        progressPane.setPrefWidth(150);
        progressPane.setMaxWidth(150);

        searchSpinner.setRadius(10);
        searchSpinner.setColor1(Color.WHITE);
        searchSpinner.setColor2(Color.WHITE);
        searchSpinner.setColor3(Color.WHITE);
        searchSpinner.setColor4(Color.WHITE);
        searchSpinner.visibleProperty().bind(searchInProgress);

        errorLabel.getStyleClass().addAll("toggleText", "searchErrorLabel");

        advancedSearchOptionsPane.getChildren().add(advancedOptionsButton);

        StackPane.setAlignment(advancedOptionsButton, Pos.CENTER_RIGHT);

        chevronDownSVG.setContent(SVG.CHEVRON_DOWN.getContent());

        advancedOptionsIcon.setShape(chevronDownSVG);
        advancedOptionsIcon.setMinSize(16, 9);
        advancedOptionsIcon.setMaxSize(16, 9);
        advancedOptionsIcon.setTranslateY(1);
        advancedOptionsIcon.getStyleClass().add("graphic");

        advancedOptionsButton.setContentDisplay(ContentDisplay.RIGHT);
        advancedOptionsButton.setGraphic(advancedOptionsIcon);
        advancedOptionsButton.setGraphicTextGap(7);
        advancedOptionsButton.getStyleClass().add("transparentButton");
        advancedOptionsButton.setStyle("-fx-border-radius: 5;");

        advancedOptionsButton.setOnAction(e -> {
            advancedOptionsButton.requestFocus();

            if(advancedOptionsShowing) hideAdvancedOptions();
            else showAdvancedOptions();
        });

        advancedOptionsButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(focusNodes.indexOf(advancedOptionsButton));
                openSubtitlesWindow.focus.set(0);
            }
            else{
                keyboardFocusOff(advancedOptionsButton);
                focus.set(-1);
                openSubtitlesWindow.focus.set(-1);
            }
        });

        advancedOptionsButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            advancedOptionsButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        advancedOptionsButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            advancedOptionsButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });

        advancedOptionsBoxWrapper.setMinHeight(0);
        advancedOptionsBoxWrapper.setMaxHeight(0);
        clippedNode.setMinHeight(0);
        clippedNode.setMaxHeight(0);

        imdbBox.getChildren().addAll(imdbLabel, imdbField, impairedHearingBox);
        imdbBox.setAlignment(Pos.TOP_LEFT);

        imdbLabel.getStyleClass().add("toggleText");
        imdbLabel.setMouseTransparent(true);

        StackPane.setMargin(imdbField, new Insets(30, 0, 0, 0));
        imdbField.setSkin(new ClearableTextFieldSkin(imdbField));
        imdbField.getStyleClass().add("customTextField");
        imdbField.setPrefHeight(36);
        imdbField.setMinHeight(36);
        imdbField.setMaxHeight(36);
        imdbField.setFocusTraversable(false);
        imdbField.setPrefWidth(250);
        imdbField.setMaxWidth(250);
        imdbField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if(!newValue.matches("\\d*")) imdbField.setText(oldValue);
            titleSearchButton.setDisable(newValue.isEmpty() && titleField.getText().isEmpty());
        });
        imdbField.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(focusNodes.size() - 10);
                openSubtitlesWindow.focus.set(0);
            }
            else {
                keyboardFocusOff(imdbField);
                focus.set(-1);
                openSubtitlesWindow.focus.set(-1);
            }
        });

        StackPane.setAlignment(impairedHearingBox, Pos.CENTER_RIGHT);
        impairedHearingBox.getChildren().addAll(impairedHearingLabel, impairedHearingButtonContainer);
        impairedHearingBox.setAlignment(Pos.TOP_LEFT);
        impairedHearingBox.setMaxWidth(270);

        impairedHearingLabel.getStyleClass().add("toggleText");
        impairedHearingLabel.setMouseTransparent(true);

        StackPane.setMargin(impairedHearingButtonContainer, new Insets(30, 0, 0, 0));
        impairedHearingButtonContainer.setSpacing(2);
        impairedHearingButtonContainer.getChildren().addAll(impairedHearingExcludeButton, impairedHearingIncludeButton, impairedHearingOnlyButton);

        impairedHearingExcludeButton.setMinHeight(36);
        impairedHearingExcludeButton.setMaxHeight(36);
        impairedHearingExcludeButton.getStyleClass().addAll("menuButton", "multiselectToggle", "leftButton");
        impairedHearingExcludeButton.setPrefWidth(90);
        impairedHearingExcludeButton.setOnMouseClicked(e -> impairedHearingExcludeButton.requestFocus());
        impairedHearingExcludeButton.setOnAction(e -> updateImpairedHearingState(EXCLUDE));
        impairedHearingExcludeButton.setFocusTraversable(false);
        impairedHearingExcludeButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(focusNodes.size() - 8);
                openSubtitlesWindow.focus.set(0);
            }
            else{
                keyboardFocusOff(impairedHearingExcludeButton);
                focus.set(-1);
                openSubtitlesWindow.focus.set(-1);
            }
        });

        impairedHearingExcludeButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            impairedHearingExcludeButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        impairedHearingExcludeButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            impairedHearingExcludeButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });

        impairedHearingIncludeButton.setMinHeight(36);
        impairedHearingIncludeButton.setMaxHeight(36);
        impairedHearingIncludeButton.getStyleClass().addAll("menuButton", "multiselectToggle", "middleButton", "toggleActive");
        impairedHearingIncludeButton.setPrefWidth(90);
        impairedHearingIncludeButton.setOnMouseClicked(e -> impairedHearingIncludeButton.requestFocus());
        impairedHearingIncludeButton.setOnAction(e -> updateImpairedHearingState(INCLUDE));
        impairedHearingIncludeButton.setFocusTraversable(false);
        impairedHearingIncludeButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(focusNodes.size() - 7);
                openSubtitlesWindow.focus.set(0);
            }
            else{
                keyboardFocusOff(impairedHearingIncludeButton);
                focus.set(-1);
                openSubtitlesWindow.focus.set(-1);
            }
        });

        impairedHearingIncludeButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            impairedHearingIncludeButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        impairedHearingIncludeButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            impairedHearingIncludeButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });

        impairedHearingOnlyButton.setMinHeight(36);
        impairedHearingOnlyButton.setMaxHeight(36);
        impairedHearingOnlyButton.getStyleClass().addAll("menuButton", "multiselectToggle", "rightButton");
        impairedHearingOnlyButton.setPrefWidth(90);
        impairedHearingOnlyButton.setOnMouseClicked(e -> impairedHearingOnlyButton.requestFocus());
        impairedHearingOnlyButton.setOnAction(e -> updateImpairedHearingState(ONLY));
        impairedHearingOnlyButton.setFocusTraversable(false);
        impairedHearingOnlyButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(focusNodes.size() - 6);
                openSubtitlesWindow.focus.set(0);
            }
            else{
                keyboardFocusOff(impairedHearingOnlyButton);
                focus.set(-1);
                openSubtitlesWindow.focus.set(-1);
            }
        });

        impairedHearingOnlyButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            impairedHearingOnlyButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        impairedHearingOnlyButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            impairedHearingOnlyButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });



        yearBox.getChildren().addAll(yearLabel, yearField, foreignPartsBox);
        yearBox.setAlignment(Pos.TOP_LEFT);

        yearLabel.getStyleClass().add("toggleText");
        yearLabel.setMinWidth(80);
        yearLabel.setPrefWidth(80);
        yearLabel.setMaxWidth(80);
        yearLabel.setMouseTransparent(true);

        StackPane.setMargin(yearField, new Insets(30, 0, 0, 0));
        yearField.setSkin(new ClearableTextFieldSkin(yearField));
        yearField.getStyleClass().add("customTextField");
        yearField.setPrefHeight(36);
        yearField.setMinHeight(36);
        yearField.setMaxHeight(36);
        yearField.setFocusTraversable(false);
        yearField.setPrefWidth(250);
        yearField.setMaxWidth(250);

        yearField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if(!newValue.matches("\\d*")) yearField.setText(oldValue);
        });

        yearField.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(focusNodes.size() - 9);
                openSubtitlesWindow.focus.set(0);
            }
            else {
                keyboardFocusOff(yearField);
                focus.set(-1);
                openSubtitlesWindow.focus.set(-1);
            }
        });

        StackPane.setAlignment(foreignPartsBox, Pos.CENTER_RIGHT);
        foreignPartsBox.getChildren().addAll(foreignPartsLabel, foreignPartsButtonContainer);
        foreignPartsBox.setAlignment(Pos.TOP_LEFT);
        foreignPartsBox.setMaxWidth(270);

        foreignPartsLabel.getStyleClass().add("toggleText");
        foreignPartsLabel.setMouseTransparent(true);

        StackPane.setMargin(foreignPartsButtonContainer, new Insets(30, 0, 0, 0));
        foreignPartsButtonContainer.setSpacing(2);
        foreignPartsButtonContainer.getChildren().addAll(foreignPartsExcludeButton, foreignPartsIncludeButton, foreignPartsOnlyButton);

        foreignPartsExcludeButton.setMinHeight(36);
        foreignPartsExcludeButton.setMaxHeight(36);
        foreignPartsExcludeButton.getStyleClass().addAll("menuButton", "multiselectToggle", "leftButton");
        foreignPartsExcludeButton.setPrefWidth(90);
        foreignPartsExcludeButton.setOnMouseClicked(e -> foreignPartsExcludeButton.requestFocus());
        foreignPartsExcludeButton.setOnAction(e -> updateForeignPartsState(EXCLUDE));
        foreignPartsExcludeButton.setFocusTraversable(false);
        foreignPartsExcludeButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(focusNodes.size() - 5);
                openSubtitlesWindow.focus.set(0);
            }
            else{
                keyboardFocusOff(foreignPartsExcludeButton);
                focus.set(-1);
                openSubtitlesWindow.focus.set(-1);
            }
        });

        foreignPartsExcludeButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            foreignPartsExcludeButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        foreignPartsExcludeButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            foreignPartsExcludeButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });

        foreignPartsIncludeButton.setMinHeight(36);
        foreignPartsIncludeButton.setMaxHeight(36);
        foreignPartsIncludeButton.getStyleClass().addAll("menuButton", "multiselectToggle", "middleButton", "toggleActive");
        foreignPartsIncludeButton.setPrefWidth(90);
        foreignPartsIncludeButton.setOnMouseClicked(e -> foreignPartsIncludeButton.requestFocus());
        foreignPartsIncludeButton.setOnAction(e -> updateForeignPartsState(INCLUDE));
        foreignPartsIncludeButton.setFocusTraversable(false);
        foreignPartsIncludeButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(focusNodes.size() - 4);
                openSubtitlesWindow.focus.set(0);
            }
            else{
                keyboardFocusOff(foreignPartsIncludeButton);
                focus.set(-1);
                openSubtitlesWindow.focus.set(-1);
            }
        });

        foreignPartsIncludeButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            foreignPartsIncludeButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        foreignPartsIncludeButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            foreignPartsIncludeButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });


        foreignPartsOnlyButton.setMinHeight(36);
        foreignPartsOnlyButton.setMaxHeight(36);
        foreignPartsOnlyButton.getStyleClass().addAll("menuButton", "multiselectToggle", "rightButton");
        foreignPartsOnlyButton.setPrefWidth(90);
        foreignPartsOnlyButton.setOnMouseClicked(e -> foreignPartsOnlyButton.requestFocus());
        foreignPartsOnlyButton.setOnAction(e -> updateForeignPartsState(ONLY));
        foreignPartsOnlyButton.setFocusTraversable(false);
        foreignPartsOnlyButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(focusNodes.size() - 3);
                openSubtitlesWindow.focus.set(0);
            }
            else{
                keyboardFocusOff(foreignPartsOnlyButton);
                focus.set(-1);
                openSubtitlesWindow.focus.set(-1);
            }
        });

        foreignPartsOnlyButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            foreignPartsOnlyButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        foreignPartsOnlyButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            foreignPartsOnlyButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });


        toggleContainer.getChildren().addAll(movieOnlyBox, aiTranslatedBox);

        movieOnlyBox.setMinWidth(280);
        movieOnlyBox.setMaxWidth(280);
        movieOnlyBox.getChildren().addAll(movieOnlyLabel, movieOnlyToggle, movieOnlyToggleLabel);
        movieOnlyBox.setAlignment(Pos.TOP_LEFT);

        movieOnlyLabel.getStyleClass().add("toggleText");
        movieOnlyLabel.setMouseTransparent(true);

        movieOnlyToggleLabel.getStyleClass().add("toggleText");
        movieOnlyToggleLabel.setPrefWidth(35);
        movieOnlyToggleLabel.setMouseTransparent(true);
        StackPane.setMargin(movieOnlyToggleLabel, new Insets(35, 0, 0, 100));

        StackPane.setMargin(movieOnlyToggle, new Insets(35, 0, 0, 50));

        movieOnlyToggle.setRadius(10);
        movieOnlyToggle.setCursor(Cursor.HAND);
        movieOnlyToggle.setSelected(false);
        movieOnlyToggle.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(focusNodes.size() - 2);
                openSubtitlesWindow.focus.set(0);
            }
            else {
                keyboardFocusOff(movieOnlyToggle);
                focus.set(-1);
                movieTogglePressed = false;
            }
        });

        movieOnlyToggle.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;

            movieTogglePressed = true;

            e.consume();
        });

        movieOnlyToggle.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;

            if(movieTogglePressed){
                movieOnlyToggle.fire();
            }

            movieTogglePressed = false;

            e.consume();
        });

        movieOnlyToggle.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue) movieOnlyToggleLabel.setText("On");
            else movieOnlyToggleLabel.setText("Off");
        });


        aiTranslatedBox.setMinWidth(260);
        aiTranslatedBox.setMaxWidth(260);
        aiTranslatedBox.getChildren().addAll(aiTranslatedLabel, aiTranslatedToggle, aiTranslatedToggleLabel);
        aiTranslatedBox.setAlignment(Pos.TOP_LEFT);

        aiTranslatedLabel.getStyleClass().add("toggleText");
        aiTranslatedLabel.setMouseTransparent(true);

        aiTranslatedToggleLabel.getStyleClass().add("toggleText");
        aiTranslatedToggleLabel.setPrefWidth(35);
        aiTranslatedToggleLabel.setMouseTransparent(true);
        StackPane.setMargin(aiTranslatedToggleLabel, new Insets(35, 0, 0, 100));

        StackPane.setMargin(aiTranslatedToggle, new Insets(35, 0, 0, 50));

        aiTranslatedToggle.setRadius(10);
        aiTranslatedToggle.setCursor(Cursor.HAND);
        aiTranslatedToggle.setSelected(true);
        aiTranslatedToggle.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(focusNodes.size() - 1);
                openSubtitlesWindow.focus.set(0);
            }
            else {
                keyboardFocusOff(aiTranslatedToggle);
                focus.set(-1);
                aiTogglePressed = false;
            }
        });

        aiTranslatedToggle.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;

            aiTogglePressed = true;

            e.consume();
        });

        aiTranslatedToggle.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;

            if(aiTogglePressed){
                aiTranslatedToggle.fire();
            }

            aiTogglePressed = false;

            e.consume();
        });

        aiTranslatedToggle.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue) {
                aiTranslatedToggleLabel.setText("On");
            }
            else {
                aiTranslatedToggleLabel.setText("Off");
            }
        });

        focusNodes.add(languageButton);
        focusNodes.add(titleField);
        focusNodes.add(seasonField);
        focusNodes.add(episodeField);
        focusNodes.add(advancedOptionsButton);

        this.getChildren().addAll(titleContainer, scrollPane);
        StackPane.setMargin(this, new Insets(0, 0, 70, 0));
    }

    private void updatePadding(boolean value){
        if (value) content.setPadding(new Insets(15, 8, 0, 15));
        else       content.setPadding(new Insets(15, 20, 0, 15));
    }

    private void initializeLanguageBox(){
        for(String string : supportedLanguages){
            languageButton.addItem(string);
        }
    }

    private void search(boolean fileSearch) {

        errorLabel.setVisible(false);

        if(searchInProgress.get()
        || (fileSearch && (openSubtitlesWindow.mainController.getMenuController().queuePage.queueBox.activeItem.get() == null || openSubtitlesWindow.mainController.getMenuController().queuePage.queueBox.activeItem.get().file == null))
        || (!fileSearch && titleField.getText().isEmpty() && imdbField.getText().isEmpty())) return;

        searchInProgress.set(true);

        if(openSubtitlesWindow.os == null) openSubtitlesWindow.os = new OpenSubtitles(openSubtitlesWindow.connectionPage.username.get(), openSubtitlesWindow.connectionPage.password.get(), openSubtitlesWindow.connectionPage.apiKey);

        LoginTask loginTask = new LoginTask(openSubtitlesWindow.os);

        loginTask.setOnSucceeded(e -> {
            Integer result = loginTask.getValue().getKey();
            if(result == -1) searchFail("Error");
            else if(result != 200) searchFail("Error " + result);
            else {
                SearchTask searchTask;
                if(fileSearch) searchTask = new SearchTask(this, openSubtitlesWindow.mainController.getMenuController().queuePage.queueBox.activeItem.get().file);
                else searchTask = new SearchTask(this, titleField.getText(), seasonField.getText(), episodeField.getText(), imdbField.getText(), yearField.getText(), impairedHearingState, foreignPartsState, movieOnlyToggle.isSelected(), aiTranslatedToggle.isSelected());

                searchTask.setOnSucceeded(successEvent -> {

                    if(openSubtitlesWindow.openSubtitlesState != OpenSubtitlesState.SEARCH_OPEN || openSubtitlesWindow.windowController.windowState != WindowState.OPEN_SUBTITLES_OPEN) return;

                    SubtitlesResult subtitlesResult = searchTask.getValue();

                    openSubtitlesWindow.resultsPage.reset();

                    if(subtitlesResult != null && subtitlesResult.data != null && subtitlesResult.data.length > 0) {
                        openSubtitlesWindow.resultsPage.setNotEmpty();
                        for (Subtitle subtitle : subtitlesResult.data) {
                            openSubtitlesWindow.resultsPage.addResult(new Result(openSubtitlesWindow, subtitle, openSubtitlesWindow.os));
                        }
                    }

                    openSubtitlesWindow.openResultsPage();
                    searchInProgress.set(false);
                });

                searchTask.setOnFailed(failEvent -> {
                    searchFail("Subtitle search failed.");
                    searchInProgress.set(false);
                });

                if(executorService != null){
                    executorService.execute(searchTask);
                    executorService.shutdown();
                    executorService = null;
                }
            }
        });

        loginTask.setOnFailed(e -> {
            searchFail("Failed to login to OpenSubtitles.");
            searchInProgress.set(false);
            if(executorService != null){
                executorService.shutdown();
                executorService = null;
            }
        });

        executorService = Executors.newFixedThreadPool(1);
        executorService.execute(loginTask);
    }

    public void reset(){
        titleField.clear();
        seasonField.clear();
        episodeField.clear();

        imdbField.clear();
        yearField.clear();
        updateImpairedHearingState(INCLUDE);
        updateForeignPartsState(EXCLUDE);
        movieOnlyToggle.setSelected(false);
        aiTranslatedToggle.setSelected(true);

        errorLabel.setVisible(false);

        searchInProgress.set(false);

        if(executorService != null){
            executorService.shutdown();
            executorService = null;
        }

        if(advancedOptionsTransition != null && advancedOptionsTransition.getStatus() == Animation.Status.RUNNING) advancedOptionsTransition.stop();
        advancedOptionsTransition = null;

        resetAdvancedOptions();
        advancedOptionsBoxWrapper.setMinHeight(0);
        advancedOptionsBoxWrapper.setMaxHeight(0);

        clippedNode.setMinHeight(0);
        clippedNode.setMaxHeight(0);

        advancedOptionsIcon.setRotate(0);

        scrollPane.setVvalue(0);
        this.setOpacity(0);
        this.setVisible(false);
    }

    private void searchFail(String text){

        searchInProgress.set(false);

        errorLabel.setText(text);
        errorLabel.setVisible(true);

        if(executorService != null) executorService.shutdown();
    }

    private void showAdvancedOptions() {

        if(advancedOptionsShowing || (advancedOptionsTransition != null && advancedOptionsTransition.getStatus() == Animation.Status.RUNNING)) return;

        advancedOptionsShowing = true;

        openSubtitlesWindow.window.minHeightProperty().unbind();
        openSubtitlesWindow.window.maxHeightProperty().unbind();

        imdbField.setMouseTransparent(false);
        yearField.setMouseTransparent(false);

        impairedHearingExcludeButton.setMouseTransparent(false);
        impairedHearingIncludeButton.setMouseTransparent(false);
        impairedHearingOnlyButton.setMouseTransparent(false);

        foreignPartsExcludeButton.setMouseTransparent(false);
        foreignPartsIncludeButton.setMouseTransparent(false);
        foreignPartsOnlyButton.setMouseTransparent(false);

        movieOnlyToggle.setMouseTransparent(false);
        aiTranslatedToggle.setMouseTransparent(false);

        if(!focusNodes.contains(imdbField)) focusNodes.add(imdbField);
        if(!focusNodes.contains(yearField)) focusNodes.add(yearField);

        if(!focusNodes.contains(impairedHearingExcludeButton)) focusNodes.add(impairedHearingExcludeButton);
        if(!focusNodes.contains(impairedHearingIncludeButton)) focusNodes.add(impairedHearingIncludeButton);
        if(!focusNodes.contains(impairedHearingOnlyButton)) focusNodes.add(impairedHearingOnlyButton);

        if(!focusNodes.contains(foreignPartsExcludeButton)) focusNodes.add(foreignPartsExcludeButton);
        if(!focusNodes.contains(foreignPartsIncludeButton)) focusNodes.add(foreignPartsIncludeButton);
        if(!focusNodes.contains(foreignPartsOnlyButton)) focusNodes.add(foreignPartsOnlyButton);

        if(!focusNodes.contains(movieOnlyToggle)) focusNodes.add(movieOnlyToggle);
        if(!focusNodes.contains(aiTranslatedToggle)) focusNodes.add(aiTranslatedToggle);


        double windowTargetHeight = Math.max(355, Math.min(600, openSubtitlesWindow.mainController.videoImageViewWrapper.getHeight() * 0.8));

        Timeline windowMinHeightTransition = AnimationsClass.animateMinHeight(windowTargetHeight, openSubtitlesWindow.window);
        Timeline windowMaxHeightTransition = AnimationsClass.animateMaxHeight(windowTargetHeight, openSubtitlesWindow.window);
        Timeline advancedOptionsBoxMinHeightTransition = AnimationsClass.animateMinHeight(245, advancedOptionsBoxWrapper);
        Timeline advancedOptionsBoxMaxHeightTransition = AnimationsClass.animateMaxHeight(245, advancedOptionsBoxWrapper);
        Timeline clippedNodeMinHeightTransition = AnimationsClass.animateMinHeight(245, clippedNode);
        Timeline clippedNodeMaxHeightTransition = AnimationsClass.animateMaxHeight(245, clippedNode);
        Timeline scrollTimeline = new Timeline(new KeyFrame(Duration.millis(200), new KeyValue(scrollPane.vvalueProperty(), 1, Interpolator.EASE_BOTH)));

        RotateTransition rotateTransition = AnimationsClass.rotateTransition(200, advancedOptionsIcon, advancedOptionsIcon.getRotate(), 180, false, 1, false);

        advancedOptionsTransition = new ParallelTransition(advancedOptionsBoxMinHeightTransition, advancedOptionsBoxMaxHeightTransition, windowMinHeightTransition, windowMaxHeightTransition, clippedNodeMaxHeightTransition, clippedNodeMinHeightTransition, rotateTransition, scrollTimeline);

        advancedOptionsTransition.setOnFinished(e -> {
            openSubtitlesWindow.window.minHeightProperty().bind(Bindings.max(355, Bindings.min(600, openSubtitlesWindow.mainController.videoImageViewWrapper.heightProperty().multiply(0.8))));
            openSubtitlesWindow.window.maxHeightProperty().bind(Bindings.max(355, Bindings.min(600, openSubtitlesWindow.mainController.videoImageViewWrapper.heightProperty().multiply(0.8))));
        });

        advancedOptionsTransition.playFromStart();
    }

    public void resetAdvancedOptions(){
        advancedOptionsShowing = false;

        imdbField.setMouseTransparent(true);
        yearField.setMouseTransparent(true);

        impairedHearingExcludeButton.setMouseTransparent(true);
        impairedHearingIncludeButton.setMouseTransparent(true);
        impairedHearingOnlyButton.setMouseTransparent(true);

        foreignPartsExcludeButton.setMouseTransparent(true);
        foreignPartsIncludeButton.setMouseTransparent(true);
        foreignPartsOnlyButton.setMouseTransparent(true);

        movieOnlyToggle.setMouseTransparent(true);
        aiTranslatedToggle.setMouseTransparent(true);

        focusNodes.remove(imdbField);
        focusNodes.remove(yearField);

        focusNodes.remove(impairedHearingExcludeButton);
        focusNodes.remove(impairedHearingIncludeButton);
        focusNodes.remove(impairedHearingOnlyButton);

        focusNodes.remove(foreignPartsExcludeButton);
        focusNodes.remove(foreignPartsIncludeButton);
        focusNodes.remove(foreignPartsOnlyButton);

        focusNodes.remove(movieOnlyToggle);
        focusNodes.remove(aiTranslatedToggle);
    }

    private void hideAdvancedOptions() {
        if(!advancedOptionsShowing || (advancedOptionsTransition != null && advancedOptionsTransition.getStatus() == Animation.Status.RUNNING)) return;

        resetAdvancedOptions();


        openSubtitlesWindow.window.minHeightProperty().unbind();
        openSubtitlesWindow.window.maxHeightProperty().unbind();

        Timeline windowMinHeightTransition = AnimationsClass.animateMinHeight(355, openSubtitlesWindow.window);
        Timeline windowMaxHeightTransition = AnimationsClass.animateMaxHeight(355, openSubtitlesWindow.window);
        Timeline advancedOptionsBoxMinHeightTransition = AnimationsClass.animateMinHeight(0, advancedOptionsBoxWrapper);
        Timeline advancedOptionsBoxMaxHeightTransition = AnimationsClass.animateMaxHeight(0, advancedOptionsBoxWrapper);
        Timeline clippedNodeMinHeightTransition = AnimationsClass.animateMinHeight(0, clippedNode);
        Timeline clippedNodeMaxHeightTransition = AnimationsClass.animateMaxHeight(0, clippedNode);

        RotateTransition rotateTransition = AnimationsClass.rotateTransition(200, advancedOptionsIcon, advancedOptionsIcon.getRotate(), 0, false, 1, false);

        advancedOptionsTransition = new ParallelTransition(advancedOptionsBoxMinHeightTransition, advancedOptionsBoxMaxHeightTransition, windowMinHeightTransition, windowMaxHeightTransition, clippedNodeMaxHeightTransition, clippedNodeMinHeightTransition, rotateTransition);

        advancedOptionsTransition.playFromStart();
    }


    @Override
    public boolean focusForward(){
        if(focus.get() == focusNodes.size() -1) return true;

        Node node = focusNodes.get(focus.get() + 1);
        keyboardFocusOn(node);
        if(node != languageButton) Utilities.checkScrollDown(scrollPane, node);

        return false;
    }

    @Override
    public boolean focusBackward(){
        if(focus.get() == 0) return true;

        Node node;
        if(focus.get() == -1) node = focusNodes.get(focusNodes.size() - 1);
        else node = focusNodes.get(focus.get() - 1);

        keyboardFocusOn(node);
        if(node != languageButton) Utilities.checkScrollUp(scrollPane, node);

        return false;
    }

    private void updateImpairedHearingState(SubtitlesQuery.Settings newState){

        impairedHearingExcludeButton.getStyleClass().remove("toggleActive");
        impairedHearingIncludeButton.getStyleClass().remove("toggleActive");
        impairedHearingOnlyButton.getStyleClass().remove("toggleActive");

        switch (newState){
            case EXCLUDE -> impairedHearingExcludeButton.getStyleClass().add("toggleActive");
            case INCLUDE -> impairedHearingIncludeButton.getStyleClass().add("toggleActive");
            case ONLY -> impairedHearingOnlyButton.getStyleClass().add("toggleActive");
        }

        impairedHearingState = newState;
    }

    private void updateForeignPartsState(SubtitlesQuery.Settings newState){

        foreignPartsExcludeButton.getStyleClass().remove("toggleActive");
        foreignPartsIncludeButton.getStyleClass().remove("toggleActive");
        foreignPartsOnlyButton.getStyleClass().remove("toggleActive");

        switch (newState){
            case EXCLUDE -> foreignPartsExcludeButton.getStyleClass().add("toggleActive");
            case INCLUDE -> foreignPartsIncludeButton.getStyleClass().add("toggleActive");
            case ONLY -> foreignPartsOnlyButton.getStyleClass().add("toggleActive");
        }

        foreignPartsState = newState;
    }
}
