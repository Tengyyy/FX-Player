package hans.Captions;

import com.github.wtekiela.opensub4j.api.OpenSubtitlesClient;
import com.github.wtekiela.opensub4j.response.ListResponse;
import com.github.wtekiela.opensub4j.response.SubtitleFile;
import com.jfoenix.controls.JFXButton;
import hans.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.TextAlignment;
import org.apache.xmlrpc.XmlRpcException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

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

    ControlTooltip downloadTooltip;

    OpenSubtitlesClient osClient;
    int subtitleId;
    String fileName;

    Result(CaptionsController captionsController, OpenSubtitlesResultsPane openSubtitlesResultsPane, String fileName, String language, String downloads, OpenSubtitlesClient osClient, int subtitleId){
        this.captionsController = captionsController;
        this.openSubtitlesResultsPane = openSubtitlesResultsPane;
        this.osClient = osClient;
        this.subtitleId = subtitleId;
        this.fileName = fileName;

        this.setPadding(new Insets(5, 10, 5, 10));
        this.setAlignment(Pos.CENTER_LEFT);
        this.getChildren().addAll(indexLabel, nameLabel, languageLabel, downloadsLabel, downloadButtonPane);
        this.setPrefSize( 535, 50);
        this.setMaxSize(535, 50);
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

        languageLabel.getStyleClass().add("resultTitle");
        languageLabel.setMinSize(75, 40);
        languageLabel.setPrefSize(75, 40);
        languageLabel.setMaxSize(75, 40);
        languageLabel.setText(language);
        languageLabel.setAlignment(Pos.CENTER);
        languageLabel.setTextAlignment(TextAlignment.CENTER);
        HBox.setMargin(languageLabel, new Insets(0, 0, 0, 10));

        downloadsLabel.getStyleClass().add("resultTitle");
        downloadsLabel.setMinSize(75, 40);
        downloadsLabel.setPrefSize(75, 40);
        downloadsLabel.setMaxSize(75, 40);
        downloadsLabel.setText(downloads);
        downloadsLabel.setAlignment(Pos.CENTER);
        downloadsLabel.setTextAlignment(TextAlignment.CENTER);
        HBox.setMargin(downloadsLabel, new Insets(0, 10, 0, 10));

        downloadButtonPane.getChildren().addAll(downloadButton, downloadIcon);
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

        downloadSVG.setContent(App.svgMap.get(SVG.DOWNLOAD));
        downloadIcon.setShape(downloadSVG);
        downloadIcon.setMinSize(16, 16);
        downloadIcon.setPrefSize(16, 16);
        downloadIcon.setMaxSize(16, 16);
        downloadIcon.setMouseTransparent(true);
        downloadIcon.getStyleClass().add("menuIcon");

        downloadButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> AnimationsClass.fadeAnimation(200, downloadButton, 0, 1, false, 1, true));

        downloadButton.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> AnimationsClass.fadeAnimation(200, downloadButton, 1, 0, false, 1, true));

        Platform.runLater(() -> downloadTooltip = new ControlTooltip(captionsController.mainController, "Download and apply subtitle file", downloadButton, 1000));

    }

    private void downloadFile(){
        if(osClient != null && osClient.isLoggedIn()){
            try {
                ListResponse<SubtitleFile> downloadResponse = osClient.downloadSubtitles(subtitleId);
                if(downloadResponse.getData().isPresent()){
                    List<SubtitleFile> subtitleFiles = downloadResponse.getData().get();
                    System.out.println("Subtitle files size: " + subtitleFiles.size());
                    SubtitleFile subtitleFile = subtitleFiles.get(0);
                    File file = findFileName(fileName);
                    Files.write(file.toPath(), Collections.singleton(subtitleFile.getContent().getContent()));

                }
            } catch (XmlRpcException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    private File findFileName(String name){
        File parent;
        if(captionsController.menuController.queueBox.activeItem.get() != null){
            // save subtitle file to parent folder of active media item
            parent = new File(captionsController.menuController.queueBox.activeItem.get().file.getParent());
        }
        else {
            // save to Downloads folder
            parent = new File(System.getProperty("user.home"), "Downloads");
        }

        File file = new File(parent, name);
        int index = 1;
        while(file.exists()){
            String extension = Utilities.getFileExtension(file);
            String newName;
            if(index == 1)
                newName = file.getName().substring(0, file.getName().lastIndexOf("." + extension)) + " (1)." + extension;
            else
                newName = file.getName().substring(0, file.getName().lastIndexOf(" (")) + " (" + index + ")." + extension;

            file = new File(file.getParentFile(), newName);
            index++;
        }

        return file;
    }
}
