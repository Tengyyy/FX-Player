package tengy.Subtitles;

import com.github.wtekiela.opensub4j.api.OpenSubtitlesClient;
import com.github.wtekiela.opensub4j.response.SubtitleInfo;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.TextAlignment;
import tengy.*;
import tengy.Subtitles.Tasks.DownloadTask;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static tengy.Utilities.keyboardFocusOff;

public class Result extends HBox {

    SubtitlesController subtitlesController;
    OpenSubtitlesResultsPane openSubtitlesResultsPane;

    Label indexLabel = new Label();
    Label nameLabel = new Label();
    Label languageLabel = new Label();
    Label downloadsLabel = new Label();
    StackPane downloadButtonPane = new StackPane();
    Button downloadButton = new Button();
    Region downloadIcon = new Region();
    SVGPath downloadSVG = new SVGPath();

    SVGPath checkSVG = new SVGPath();
    Region checkIcon = new Region();

    SVGPath crossSVG = new SVGPath();
    Region crossIcon = new Region();
    MFXProgressSpinner spinner = new MFXProgressSpinner();

    ControlTooltip downloadTooltip;

    OpenSubtitlesClient osClient;
    int subtitleId;
    String fileName;
    String encoding;


    Result(SubtitlesController subtitlesController, OpenSubtitlesResultsPane openSubtitlesResultsPane, SubtitleInfo subtitleInfo, OpenSubtitlesClient osClient){
        this.subtitlesController = subtitlesController;
        this.openSubtitlesResultsPane = openSubtitlesResultsPane;
        this.osClient = osClient;
        this.subtitleId = subtitleInfo.getSubtitleFileId();
        this.fileName = subtitleInfo.getFileName();
        this.encoding = subtitleInfo.getEncoding();

        this.setPadding(new Insets(5, 10, 5, 10));
        this.setAlignment(Pos.CENTER_LEFT);
        this.getChildren().addAll(indexLabel, nameLabel, languageLabel, downloadsLabel, downloadButtonPane);
        this.setPrefSize( 525, 54);
        this.setMaxSize(525, 54);
        this.getStyleClass().add("settingsPaneTab");

        nameLabel.getStyleClass().add("resultTitle");
        nameLabel.setMinSize(270, 40);
        nameLabel.setPrefSize(270, 40);
        nameLabel.setMaxSize(270, 40);
        nameLabel.setText(fileName);

        languageLabel.getStyleClass().add("resultTitle");
        languageLabel.setMinSize(75, 40);
        languageLabel.setPrefSize(75, 40);
        languageLabel.setMaxSize(75, 40);
        languageLabel.setText(OpenSubtitlesPane.languageMap.get(subtitleInfo.getLanguage()));
        languageLabel.setAlignment(Pos.CENTER);
        languageLabel.setTextAlignment(TextAlignment.CENTER);
        HBox.setMargin(languageLabel, new Insets(0, 0, 0, 10));

        downloadsLabel.getStyleClass().add("resultTitle");
        downloadsLabel.setMinSize(75, 40);
        downloadsLabel.setPrefSize(75, 40);
        downloadsLabel.setMaxSize(75, 40);
        downloadsLabel.setText(String.valueOf(subtitleInfo.getDownloadsNo()));
        downloadsLabel.setAlignment(Pos.CENTER);
        downloadsLabel.setTextAlignment(TextAlignment.CENTER);
        HBox.setMargin(downloadsLabel, new Insets(0, 10, 0, 10));

        downloadButtonPane.getChildren().addAll(downloadButton, spinner, checkIcon, crossIcon);
        downloadButtonPane.setPrefSize(40, 40);
        downloadButtonPane.setMaxSize(40, 40);
        downloadButton.setPrefWidth(30);
        downloadButton.setPrefHeight(30);
        downloadButton.getStyleClass().addAll("transparentButton", "settingsMenuButton");
        downloadButton.setOnAction(e -> {
            downloadButton.requestFocus();
            downloadFile();
        });
        downloadButton.setGraphic(downloadIcon);
        downloadButton.setFocusTraversable(false);
        downloadButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue) openSubtitlesResultsPane.focus.set(openSubtitlesResultsPane.focusNodes.indexOf(downloadButton));
            else {
                keyboardFocusOff(downloadButton);
                openSubtitlesResultsPane.focus.set(-1);
            }
        });

        downloadButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            downloadButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        downloadButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            downloadButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });

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
        crossIcon.getStyleClass().add("menuIcon");

        Platform.runLater(() -> downloadTooltip = new ControlTooltip(subtitlesController.mainController, "Download subtitle file", "", downloadButton, 1000));
    }

    private void downloadFile(){
        int focus = Math.max(-1, openSubtitlesResultsPane.focus.get() -1);
        downloadButton.setDisable(true);
        openSubtitlesResultsPane.focusNodes.remove(downloadButton);
        openSubtitlesResultsPane.focus.set(focus);


        downloadIcon.setVisible(false);
        spinner.setVisible(true);
        if(osClient != null && osClient.isLoggedIn()){

            DownloadTask downloadTask  = new DownloadTask(subtitlesController, openSubtitlesResultsPane, this.fileName, this.subtitleId, this.encoding);
            downloadTask.setOnSucceeded(e -> {
                File file = downloadTask.getValue();
                if(file != null){
                    if(Utilities.getFileExtension(file).equals("srt") && subtitlesController.menuController.queuePage.queueBox.activeItem.get() != null){
                        subtitlesController.subtitlesHome.createTab(file);
                    }

                    spinner.setVisible(false);
                    checkIcon.setVisible(true);
                    nameLabel.setCursor(Cursor.HAND);
                    nameLabel.setOnMouseClicked(event -> {
                        if(!file.exists()) return;
                        if(App.isWindows){
                            Shell32Util.SHOpenFolderAndSelectItems(file);
                        }
                        else if(Desktop.isDesktopSupported()){
                            Desktop desktop = Desktop.getDesktop();

                            if(desktop.isSupported(Desktop.Action.BROWSE_FILE_DIR)){
                                desktop.browseFileDirectory(file);
                            }
                            else if(desktop.isSupported(Desktop.Action.OPEN)){
                                try {
                                    desktop.open(file.getParentFile());
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                    });
                }
                else {
                    spinner.setVisible(false);
                    crossIcon.setVisible(true);
                    downloadsLabel.setVisible(false);
                    languageLabel.setVisible(false);
                    nameLabel.setText("Download failed");
                }
            });
            downloadTask.setOnFailed(e -> {
                spinner.setVisible(false);
                crossIcon.setVisible(true);
                checkIcon.setVisible(false);
                downloadsLabel.setVisible(false);
                languageLabel.setVisible(false);
                nameLabel.setText("Download failed");
            });

            ExecutorService executorService = Executors.newFixedThreadPool(1);
            executorService.execute(downloadTask);
            executorService.shutdown();
        }
    }
}
