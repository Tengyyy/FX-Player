package hans.Captions;

import hans.App;
import hans.SVG;
import hans.Settings.SettingsController;
import javafx.animation.*;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;
import org.controlsfx.control.CheckComboBox;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class OpenSubtitlesPane {

    ScrollPane scrollPane = new ScrollPane();
    VBox container = new VBox();

    StackPane titleContainer = new StackPane();
    HBox titlePane = new HBox();
    StackPane backIconPane = new StackPane();
    Region backIcon = new Region();
    Label titleLabel = new Label();
    SVGPath backSVG = new SVGPath();

    private Map<String, String> languages = new HashMap<>();
    private String[] supportedLanguages = {"Arabic", "Armenian", "Belarusian", "Bosnian", "Bulgarian", "Catalan", "Chinese", "Croatian", "Czech","Danish", "Dutch", "English", "Estonian", "Filipino", "Finnish", "French", "Georgian", "German", "Greek", "Hindi", "Hungarian", "Icelandic", "Indonesian", "Irish", "Italian", "Japanese", "Korean", "Latvian", "Lithuanian", "Macedonian", "Norwegian", "Polish", "Portuguese", "Romanian", "Russian", "Slovak", "Slovenian", "Spanish", "Swedish", "Thai", "Turkish", "Ukrainian", "Welsh"};
    public CheckComboBox<String> languageBox = new CheckComboBox<>();

    CaptionsHome captionsHome;
    CaptionsController captionsController;

    OpenSubtitlesPane(CaptionsHome captionsHome, CaptionsController captionsController){
        this.captionsHome = captionsHome;
        this.captionsController = captionsController;

        backSVG.setContent(App.svgMap.get(SVG.CHEVRON_LEFT));

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("settingsScroll");
        scrollPane.setPrefSize(400, 223);
        scrollPane.setMaxSize(400, 223);
        scrollPane.setContent(container);
        scrollPane.setVisible(false);
        scrollPane.setMouseTransparent(true);
        scrollPane.setFitToWidth(true);

        StackPane.setAlignment(scrollPane, Pos.BOTTOM_CENTER);

        container.setPrefSize(400, 220);
        container.setMaxSize(400, 220);
        container.getChildren().addAll(titleContainer);
        container.setAlignment(Pos.TOP_CENTER);

        titleContainer.setPadding(new Insets(0, 10, 0, 10));
        VBox.setMargin(titleContainer, new Insets(0, 0, 20, 0));
        titleContainer.getChildren().addAll(titlePane, languageBox);
        titleContainer.getStyleClass().add("settingsPaneTitle");

        titlePane.setMinHeight(40);
        titlePane.setPrefHeight(40);
        titlePane.setMaxHeight(40);
        titlePane.setPrefWidth(Region.USE_COMPUTED_SIZE);
        StackPane.setAlignment(titlePane, Pos.CENTER_LEFT);
        titlePane.getChildren().addAll(backIconPane, titleLabel);

        backIconPane.setMinSize(24, 40);
        backIconPane.setPrefSize(24, 40);
        backIconPane.setMaxSize(24, 40);
        backIconPane.setCursor(Cursor.HAND);
        backIconPane.getChildren().add(backIcon);
        backIconPane.setOnMouseClicked((e) -> closeOpenSubtitlesPane());

        backIcon.setMinSize(8, 13);
        backIcon.setPrefSize(8, 13);
        backIcon.setMaxSize(8, 13);
        backIcon.getStyleClass().add("settingsPaneIcon");
        backIcon.setShape(backSVG);

        titleLabel.setMinHeight(40);
        titleLabel.setPrefHeight(40);
        titleLabel.setMaxHeight(40);
        titleLabel.setText("OpenSubtitles");
        titleLabel.setCursor(Cursor.HAND);
        titleLabel.getStyleClass().add("settingsPaneText");
        titleLabel.setOnMouseClicked((e) -> closeOpenSubtitlesPane());


        StackPane.setAlignment(languageBox, Pos.CENTER_RIGHT);
        languageBox.setPrefWidth(200);
        languageBox.setMaxWidth(200);
        languageBox.setTitle("Languages");
        languageBox.setId("checkComboBox");
        ObservableList<Integer> observableList = languageBox.getCheckModel().getCheckedIndices();
        observableList.addListener((ListChangeListener<Integer>) change -> {


            if(observableList.isEmpty()) languageBox.setTitle("Languages");
            else {
                StringBuilder newTitle = new StringBuilder();
                for(int i = 0; i < observableList.size(); i ++){
                    Integer index = observableList.get(i);
                    String languageName = languageBox.getItems().get(index);
                    String languageCode = languages.get(languageName);
                    if(i < observableList.size() - 1){
                        newTitle.append(languageCode).append(", ");
                    }
                    else {
                        newTitle.append(languageCode);
                    }
                }

                languageBox.setTitle(newTitle.toString());
            }
        });

        initializeLanguageBox();


        captionsController.captionsPane.getChildren().add(scrollPane);
    }

    public void closeOpenSubtitlesPane() {
        if (captionsController.animating.get()) return;

        captionsController.captionsState = CaptionsState.HOME_OPEN;

        captionsController.captionsHome.scrollPane.setVisible(true);
        captionsController.captionsHome.scrollPane.setMouseTransparent(false);


        Timeline clipHeightTimeline = new Timeline();
        clipHeightTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.clip.heightProperty(), captionsController.captionsHome.scrollPane.getHeight())));


        Timeline clipWidthTimeline = new Timeline();
        clipWidthTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SettingsController.ANIMATION_SPEED), new KeyValue(captionsController.clip.widthProperty(), captionsController.captionsHome.scrollPane.getWidth())));


        TranslateTransition captionsPaneTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), captionsController.captionsHome.scrollPane);
        captionsPaneTransition.setFromX(-scrollPane.getWidth());
        captionsPaneTransition.setToX(0);

        TranslateTransition openSubtitlesTransition = new TranslateTransition(Duration.millis(SettingsController.ANIMATION_SPEED), scrollPane);
        openSubtitlesTransition.setFromX(0);
        openSubtitlesTransition.setToX(scrollPane.getWidth());


        ParallelTransition parallelTransition = new ParallelTransition(clipHeightTimeline, clipWidthTimeline, captionsPaneTransition, openSubtitlesTransition);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        parallelTransition.setOnFinished((e) -> {
            captionsController.animating.set(false);
            scrollPane.setVisible(false);
            scrollPane.setMouseTransparent(true);
            scrollPane.setTranslateX(0);
        });

        parallelTransition.play();
        captionsController.animating.set(true);
    }

    private void initializeLanguageBox(){

        for (Locale l : Locale.getAvailableLocales()) {
            languages.put(l.getDisplayLanguage(), l.getISO3Language());
        }

        for(String string : supportedLanguages){
            languageBox.getItems().add(string);
        }
    }
}
