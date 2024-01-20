package fxplayer.windows.openSubtitles;


import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import javafx.css.PseudoClass;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import fxplayer.openSubtitles.OpenSubtitles;
import fxplayer.openSubtitles.models.features.Subtitle;
import fxplayer.SVG;
import fxplayer.Utilities;
import fxplayer.windows.openSubtitles.tasks.DownloadTask;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static fxplayer.Utilities.keyboardFocusOff;

public class Result extends GridPane {

    OpenSubtitlesWindow openSubtitlesWindow;

    ColumnConstraints column1 = new ColumnConstraints(45, 45, 45);
    ColumnConstraints column2 = new ColumnConstraints(0, 100, Double.MAX_VALUE);
    ColumnConstraints column3 = new ColumnConstraints(110, 110, 110);
    ColumnConstraints column4 = new ColumnConstraints(110, 110, 110);
    ColumnConstraints column5 = new ColumnConstraints(50, 50, 50);

    public Label indexLabel = new Label();

    Label nameLabel = new Label();
    Label languageLabel = new Label();
    Label downloadsLabel = new Label();

    StackPane iconPane = new StackPane();
    Region downloadIcon = new Region();
    SVGPath downloadSVG = new SVGPath();

    SVGPath checkSVG = new SVGPath();
    Region checkIcon = new Region();

    SVGPath crossSVG = new SVGPath();
    Region crossIcon = new Region();
    MFXProgressSpinner spinner = new MFXProgressSpinner();

    OpenSubtitles os;

    Subtitle subtitle;
    String fileName;
    String language;
    int downloads;

    int index;

    static final int height = 44;

    boolean pressed = false;

    Result(OpenSubtitlesWindow openSubtitlesWindow, Subtitle subtitle, OpenSubtitles os) {
        this.openSubtitlesWindow = openSubtitlesWindow;
        this.os = os;
        this.subtitle = subtitle;
        this.fileName = subtitle.attributes.files[0].file_name + ".srt";
        this.downloads = subtitle.attributes.download_count;
        this.language = Language.getThreeLetterCodeFromTwoLetterCode(subtitle.attributes.language);

        column2.setHgrow(Priority.ALWAYS); // makes the middle column (video title text) take up all available space
        this.getColumnConstraints().addAll(column1, column2, column3, column4, column5);

        GridPane.setValignment(indexLabel, VPos.CENTER);
        GridPane.setValignment(nameLabel, VPos.CENTER);
        GridPane.setValignment(languageLabel, VPos.CENTER);
        GridPane.setValignment(downloadsLabel, VPos.CENTER);
        GridPane.setValignment(iconPane, VPos.CENTER);

        GridPane.setHalignment(indexLabel, HPos.CENTER);
        GridPane.setHalignment(nameLabel, HPos.LEFT);
        GridPane.setHalignment(languageLabel, HPos.CENTER);
        GridPane.setHalignment(downloadsLabel, HPos.CENTER);
        GridPane.setHalignment(iconPane, HPos.CENTER);

        this.setFocusTraversable(false);

        this.add(indexLabel, 0, 0);
        this.add(nameLabel, 1, 0);
        this.add(languageLabel, 2, 0);
        this.add(downloadsLabel, 3, 0);
        this.add(iconPane, 4, 0);

        this.setMinHeight(height);
        this.setMaxHeight(height);
        this.getStyleClass().add("resultItem");

        this.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                openSubtitlesWindow.focus.set(0);
                openSubtitlesWindow.resultsPage.focus.set(openSubtitlesWindow.resultsPage.focusNodes.indexOf(this));
            }
            else {
                openSubtitlesWindow.focus.set(-1);
                openSubtitlesWindow.resultsPage.focus.set(-1);
                pressed = false;
                this.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
            }
        });

        this.setOnMouseClicked(e -> {
            this.requestFocus();
            downloadFile();
        });
        this.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;

            pressed = true;
            this.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);

            e.consume();
        });

        this.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;

            if(pressed) downloadFile();

            pressed = false;
            this.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);

            e.consume();
        });

        index = openSubtitlesWindow.resultsPage.results.size();

        indexLabel.setText(String.valueOf(index + 1));
        indexLabel.getStyleClass().add("indexLabel");
        indexLabel.setMouseTransparent(true);
        indexLabel.setPrefWidth(45);
        indexLabel.setMaxWidth(45);
        indexLabel.setAlignment(Pos.CENTER);

        nameLabel.getStyleClass().add("resultTitle");
        nameLabel.setText(fileName);

        languageLabel.getStyleClass().add("resultTitle");
        languageLabel.setText(language);

        downloadsLabel.getStyleClass().add("resultTitle");
        downloadsLabel.setText(String.valueOf(downloads));

        iconPane.getChildren().addAll(downloadIcon, spinner, checkIcon, crossIcon);
        iconPane.setPrefSize(40, height);
        iconPane.setMaxSize(40, height);


        checkSVG.setContent(SVG.CHECK.getContent());
        crossSVG.setContent(SVG.CLOSE.getContent());
        downloadSVG.setContent(SVG.DOWNLOAD.getContent());

        downloadIcon.setShape(downloadSVG);
        downloadIcon.setMinSize(16, 16);
        downloadIcon.setPrefSize(16, 16);
        downloadIcon.setMaxSize(16, 16);
        downloadIcon.setMouseTransparent(true);
        downloadIcon.getStyleClass().add("graphic");

        spinner.setRadius(8);
        spinner.setColor1(Color.WHITE);
        spinner.setColor2(Color.WHITE);
        spinner.setColor3(Color.WHITE);
        spinner.setColor4(Color.WHITE);
        spinner.setVisible(false);
        spinner.setMouseTransparent(true);

        checkIcon.setShape(checkSVG);
        checkIcon.setMinSize(16, 11);
        checkIcon.setPrefSize(16, 11);
        checkIcon.setMaxSize(16, 11);
        checkIcon.setMouseTransparent(true);
        checkIcon.setVisible(false);
        checkIcon.getStyleClass().add("menuIcon");

        crossIcon.setShape(crossSVG);
        crossIcon.setMinSize(16, 16);
        crossIcon.setPrefSize(16, 16);
        crossIcon.setMaxSize(16, 16);
        crossIcon.setMouseTransparent(true);
        crossIcon.setVisible(false);
        crossIcon.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
    }

    private void downloadFile() {
        downloadIcon.setVisible(false);

        openSubtitlesWindow.resultsPage.focusNodes.remove(this);

        this.setMouseTransparent(true);
        keyboardFocusOff(this);
        this.pseudoClassStateChanged(PseudoClass.getPseudoClass("focused"), false);
        this.pseudoClassStateChanged(PseudoClass.getPseudoClass("hover"), false);
        this.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);

        openSubtitlesWindow.focus.set(-1);
        openSubtitlesWindow.resultsPage.focus.set(-1);

        spinner.setVisible(true);
        if (os != null && os.isLoggedIn()) {

            File parentFile;
            String name;
            if (openSubtitlesWindow.mainController.getMenuController().queuePage.queueBox.activeItem.get() != null) {
                File mediaFile = openSubtitlesWindow.mainController.getMenuController().queuePage.queueBox.activeItem.get().file;
                parentFile = mediaFile.getParentFile();
                String mediaName = mediaFile.getName();
                name = mediaName.substring(0, mediaName.length() - Utilities.getFileExtension(mediaFile).length() - 1) + ".srt";
            }
            else {
                parentFile = new File(System.getProperty("user.home"), "Downloads");
                name = this.fileName;
            }

            DownloadTask downloadTask = new DownloadTask(os, parentFile, name, subtitle);
            downloadTask.setOnSucceeded(e -> {
                File file = downloadTask.getValue();
                if (file != null) {
                    if (Utilities.getFileExtension(file).equals("srt") && openSubtitlesWindow.mainController.getMenuController().queuePage.queueBox.activeItem.get() != null) {
                        openSubtitlesWindow.mainController.getSubtitlesController().subtitlesHome.createTab(file);
                    }

                    spinner.setVisible(false);
                    checkIcon.setVisible(true);
                } else {
                    spinner.setVisible(false);
                    crossIcon.setVisible(true);
                    downloadsLabel.setVisible(false);
                    languageLabel.setVisible(false);
                    nameLabel.setText("Download failed");
                    nameLabel.setTextFill(Color.RED);
                    indexLabel.setTextFill(Color.RED);
                }
            });
            downloadTask.setOnFailed(e -> {
                spinner.setVisible(false);
                crossIcon.setVisible(true);
                checkIcon.setVisible(false);
                downloadsLabel.setVisible(false);
                languageLabel.setVisible(false);
                nameLabel.setText("Download failed");
                nameLabel.setTextFill(Color.RED);
                indexLabel.setTextFill(Color.RED);
            });

            ExecutorService executorService = Executors.newFixedThreadPool(1);
            executorService.execute(downloadTask);
            executorService.shutdown();
        }
    }
}