package hans.Captions;

import com.github.wtekiela.opensub4j.api.OpenSubtitlesClient;
import com.github.wtekiela.opensub4j.response.SubtitleInfo;
import com.jfoenix.controls.JFXButton;
import hans.*;
import hans.Captions.Tasks.DownloadTask;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Result extends HBox {

    CaptionsController captionsController;
    OpenSubtitlesResultsPane openSubtitlesResultsPane;

    Label indexLabel = new Label();
    Label nameLabel = new Label();
    Label languageLabel = new Label();
    Label downloadsLabel = new Label();
    StackPane downloadButtonPane = new StackPane();
    JFXButton downloadButton = new JFXButton();
    Region downloadIcon = new Region();
    SVGPath downloadSVG = new SVGPath();

    SVGPath checkSVG = new SVGPath();
    Region checkIcon = new Region();

    ControlTooltip downloadTooltip;

    OpenSubtitlesClient osClient;
    int subtitleId;
    String fileName;
    String encoding;

    Result(CaptionsController captionsController, OpenSubtitlesResultsPane openSubtitlesResultsPane, SubtitleInfo subtitleInfo, OpenSubtitlesClient osClient){
        this.captionsController = captionsController;
        this.openSubtitlesResultsPane = openSubtitlesResultsPane;
        this.osClient = osClient;
        this.subtitleId = subtitleInfo.getSubtitleFileId();
        this.fileName = subtitleInfo.getFileName();
        this.encoding = subtitleInfo.getEncoding();

        this.setPadding(new Insets(5, 10, 5, 10));
        this.setAlignment(Pos.CENTER_LEFT);
        this.getChildren().addAll(indexLabel, nameLabel, languageLabel, downloadsLabel, downloadButtonPane);
        this.setPrefSize( 550, 50);
        this.setMaxSize(550, 50);
        if(openSubtitlesResultsPane.results.size() % 2 == 1)
            this.setStyle("-fx-background-color: rgba(40,40,40,0.8);");

        indexLabel.setPrefSize(25, 40);
        indexLabel.setMaxSize(25, 40);
        indexLabel.getStyleClass().add("resultTitle");
        indexLabel.setMouseTransparent(true);
        indexLabel.setTextAlignment(TextAlignment.LEFT);
        indexLabel.setAlignment(Pos.CENTER_LEFT);
        indexLabel.setText(String.valueOf(openSubtitlesResultsPane.results.size() + 1));

        nameLabel.getStyleClass().add("resultTitle");
        nameLabel.setMinSize(270, 40);
        nameLabel.setPrefSize(270, 40);
        nameLabel.setMaxSize(270, 40);
        nameLabel.setText(fileName);

        nameLabel.setOnMouseEntered(e -> nameLabel.setUnderline(true));
        nameLabel.setOnMouseExited(e -> nameLabel.setUnderline(false));

        Tooltip tooltip = new Tooltip(fileName);
        tooltip.setShowDelay(Duration.millis(1000));
        tooltip.setHideDelay(Duration.ZERO);
        tooltip.setShowDuration(Duration.seconds(4));
        Tooltip.install(nameLabel, tooltip);

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

        downloadButtonPane.getChildren().addAll(downloadButton, downloadIcon, checkIcon);
        downloadButtonPane.setPrefSize(40, 40);
        downloadButtonPane.setMaxSize(40, 40);
        downloadButton.setPrefWidth(30);
        downloadButton.setPrefHeight(30);
        downloadButton.setRipplerFill(Color.WHITE);
        downloadButton.getStyleClass().add("roundButton");
        downloadButton.setCursor(Cursor.HAND);
        downloadButton.setOpacity(0);
        downloadButton.setText(null);
        downloadButton.setOnAction(e -> downloadFile());

        checkSVG.setContent(App.svgMap.get(SVG.CHECK));
        downloadSVG.setContent(App.svgMap.get(SVG.DOWNLOAD));
        downloadIcon.setShape(downloadSVG);
        downloadIcon.setMinSize(16, 16);
        downloadIcon.setPrefSize(16, 16);
        downloadIcon.setMaxSize(16, 16);
        downloadIcon.setMouseTransparent(true);
        downloadIcon.getStyleClass().add("menuIcon");

        checkIcon.setShape(checkSVG);
        checkIcon.setMinSize(16, 10);
        checkIcon.setPrefSize(16, 10);
        checkIcon.setMaxSize(16, 10);
        checkIcon.setMouseTransparent(true);
        checkIcon.setVisible(false);
        checkIcon.getStyleClass().add("menuIcon");

        downloadButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> AnimationsClass.fadeAnimation(200, downloadButton, 0, 1, false, 1, true));

        downloadButton.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> AnimationsClass.fadeAnimation(200, downloadButton, 1, 0, false, 1, true));

        Platform.runLater(() -> downloadTooltip = new ControlTooltip(captionsController.mainController, "Download subtitle file", downloadButton, 1000));

    }

    private void downloadFile(){
        downloadButton.setDisable(true);
        //TODO: show loading animation
        if(osClient != null && osClient.isLoggedIn()){

            DownloadTask downloadTask  = new DownloadTask(captionsController, openSubtitlesResultsPane, this.fileName, this.subtitleId, this.encoding);
            downloadTask.setOnSucceeded(e -> {
                File file = downloadTask.getValue();
                if(file != null){
                    if(Utilities.getFileExtension(file).equals("srt") && captionsController.menuController.queueBox.activeItem.get() != null){
                        captionsController.captionsHome.createTab(file);
                    }

                    downloadIcon.setVisible(false);
                    checkIcon.setVisible(true);
                    //TODO: show checkmark to show that download is complete, maybe make it a timer and after a few seconds add a button to open subtitle file in folder
                }
                else {
                    //TODO: show cross icon indicating that download failed
                }
            });
            downloadTask.setOnFailed(e -> {
                //TODO: show cross icon indicating that download failed
            });

            ExecutorService executorService = Executors.newFixedThreadPool(1);
            executorService.execute(downloadTask);
            executorService.shutdown();
        }
    }
}
