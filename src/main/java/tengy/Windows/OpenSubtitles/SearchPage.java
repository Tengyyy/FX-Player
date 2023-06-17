package tengy.Windows.OpenSubtitles;

import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import tengy.ClearableTextFieldSkin;
import tengy.MultiSelectButton;
import tengy.OpenSubtitles.OpenSubtitles;
import tengy.OpenSubtitles.models.features.Subtitle;
import tengy.OpenSubtitles.models.subtitles.SubtitlesResult;
import tengy.SVG;
import tengy.Windows.OpenSubtitles.Tasks.LoginTask;
import tengy.Windows.OpenSubtitles.Tasks.SearchTask;
import tengy.Windows.WindowState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static tengy.Utilities.keyboardFocusOff;
import static tengy.Utilities.keyboardFocusOn;

public class SearchPage extends VBox implements Page{

    OpenSubtitlesWindow openSubtitlesWindow;


    public ScrollPane scrollPane;

    VBox content = new VBox();

    StackPane titleContainer = new StackPane();
    Label title = new Label("OpenSubtitles");

    public MultiSelectButton languageButton;

    public Button hashSearchButton = new Button("Search by hash");

    public static HashMap<String, String> languageMap = new Languages();
    public static final String[] supportedLanguages = {"Abkhazian", "Afrikaans", "Albanian", "Arabic", "Aragonese", "Armenian", "Assamese", "Asturian", "Azerbaijani","Basque", "Belarusian", "Bengali", "Bosnian", "Breton", "Bulgarian", "Burmese", "Catalan", "Chinese (simplified)", "Chinese (traditional)", "Chinese bilingual", "Croatian", "Czech", "Danish", "Dari", "Dutch", "English", "Esperanto", "Estonian", "Extremaduran", "Finnish", "French", "Gaelic", "Galician", "Georgian", "German", "Greek", "Hebrew", "Hindi", "Hungarian", "Icelandic", "Igbo", "Indonesian", "Interlingua", "Irish", "Italian", "Japanese", "Kannada", "Kazakh", "Khmer", "Korean", "Kurdish", "Latvian", "Lithuanian", "Luxembourgish", "Macedonian", "Malay", "Malayalam", "Manipuri", "Marathi", "Mongolian", "Montenegrin", "Navajo", "Nepali", "Northern Sami", "Norwegian", "Occitan", "Odia", "Persian", "Polish", "Portuguese", "Portuguese (BR)", "Portuguese (MZ)", "Pushto", "Romanian", "Russian", "Santali", "Serbian", "Sindhi", "Sinhalese", "Slovak", "Slovenian", "Somali", "Spanish", "Spanish (EU)", "Spanish (LA)", "Swahili", "Swedish", "Syriac", "Tagalog", "Tamil", "Tatar", "Telugu", "Thai", "Toki Pona", "Turkish", "Turkmen", "Ukrainian", "Urdu", "Vietnamese", "Welsh"};

    StackPane titleBox = new StackPane();

    Label titleLabel = new Label("Title:");
    public TextField titleField = new TextField();
    public Button titleSearchButton = new Button("Search by title");

    StackPane seasonBox = new StackPane();
    Label seasonLabel = new Label("Season:");
    public TextField seasonField = new TextField();

    StackPane episodeBox = new StackPane();
    Label episodeLabel = new Label("Episode:");
    public TextField episodeField = new TextField();

    StackPane progressPane = new StackPane();
    MFXProgressSpinner searchSpinner = new MFXProgressSpinner();
    Label errorLabel = new Label();


    IntegerProperty focus = new SimpleIntegerProperty(-1);
    List<Node> focusNodes = new ArrayList<>();

    public OpenSubtitles os = null;

    public BooleanProperty searchInProgress = new SimpleBooleanProperty(false);
    ExecutorService executorService = null;

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

        content.setPadding(new Insets(15, 20, 15, 15));
        content.setSpacing(15);
        content.getChildren().addAll(titleBox, seasonBox, episodeBox, progressPane);



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
            titleSearchButton.setDisable(newValue.isEmpty());
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
                    if(hashSearchButton.isDisabled()) focusNodes.add(titleSearchButton);
                    else focusNodes.add(focusNodes.size() - 1, titleSearchButton);
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
            else if(!focusNodes.contains(hashSearchButton)) focusNodes.add(hashSearchButton);
        });
        hashSearchButton.setGraphic(hashSearchIcon);
        hashSearchButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(focusNodes.size() - 1);
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

        episodeBox.getChildren().addAll(episodeLabel, episodeField);
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

        progressPane.getChildren().addAll(searchSpinner, errorLabel);;

        StackPane.setAlignment(searchSpinner, Pos.CENTER);
        searchSpinner.setRadius(10);
        searchSpinner.setColor1(Color.WHITE);
        searchSpinner.setColor2(Color.WHITE);
        searchSpinner.setColor3(Color.WHITE);
        searchSpinner.setColor4(Color.WHITE);
        searchSpinner.visibleProperty().bind(searchInProgress);

        StackPane.setAlignment(errorLabel, Pos.CENTER_LEFT);
        errorLabel.getStyleClass().addAll("toggleText", "searchErrorLabel");

        focusNodes.add(languageButton);
        focusNodes.add(titleField);
        focusNodes.add(seasonField);
        focusNodes.add(episodeField);

        this.getChildren().addAll(titleContainer, scrollPane);
        StackPane.setMargin(this, new Insets(0, 0, 70, 0));
    }

    private void updatePadding(boolean value){
        if(value) content.setPadding(new Insets(15, 8, 15, 15));
        else      content.setPadding(new Insets(15, 20, 15, 15));
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
        || (!fileSearch && titleField.getText().isEmpty())) return;

        searchInProgress.set(true);

        if(os == null) os = new OpenSubtitles(openSubtitlesWindow.connectionPage.username, openSubtitlesWindow.connectionPage.password, openSubtitlesWindow.connectionPage.apiKey);

        LoginTask loginTask = new LoginTask(this);

        loginTask.setOnSucceeded(e -> {
            Integer result = loginTask.getValue();
            if(result == -1) searchFail("Failed to login");
            else if(result == 400) searchFail("Error 400: invalid username/password - remember to use your username and not your email to authenticate");
            else if(result == 401) searchFail("Error 401: invalid username/password");
            else if(result != 200) searchFail("Error " + result + ": failed to login");
            else {
                SearchTask searchTask;
                if(fileSearch) searchTask = new SearchTask(this, openSubtitlesWindow.mainController.getMenuController().queuePage.queueBox.activeItem.get().file);
                else searchTask = new SearchTask(this, titleField.getText(), seasonField.getText(), episodeField.getText());

                searchTask.setOnSucceeded(successEvent -> {

                    if(openSubtitlesWindow.openSubtitlesState != OpenSubtitlesState.SEARCH_OPEN || openSubtitlesWindow.windowController.windowState != WindowState.OPEN_SUBTITLES_OPEN) return;

                    SubtitlesResult subtitlesResult = searchTask.getValue();

                    if(subtitlesResult.data.length > 0) {
                        openSubtitlesWindow.resultsPage.setNotEmpty();
                        for (Subtitle subtitle : subtitlesResult.data) {
                            openSubtitlesWindow.resultsPage.addResult(new Result(openSubtitlesWindow, subtitle, os));
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

        errorLabel.setVisible(false);

        searchInProgress.set(false);

        if(executorService != null){
            executorService.shutdown();
            executorService = null;
        }

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

    @Override
    public boolean focusForward(){
        if(focus.get() == focusNodes.size() -1) return true;

        keyboardFocusOn(focusNodes.get(focus.get() + 1));

        return false;
    }

    @Override
    public boolean focusBackward(){
        if(focus.get() == 0) return true;

        if(focus.get() == -1) keyboardFocusOn(focusNodes.get(focusNodes.size() - 1));
        else keyboardFocusOn(focusNodes.get(focus.get() - 1));

        return false;
    }
}
