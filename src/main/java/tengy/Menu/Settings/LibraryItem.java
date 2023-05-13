package tengy.Menu.Settings;

import tengy.Subtitles.SubtitlesState;
import tengy.ControlTooltip;
import tengy.SVG;
import tengy.PlaybackSettings.PlaybackSettingsState;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;

import java.io.File;

public class LibraryItem extends StackPane {

    LibrariesSection librariesSection;

    File file;
    SVGPath folderSVG = new SVGPath();
    Region folderIcon = new Region();
    Label pathLabel = new Label();

    Button refreshButton = new Button();
    SVGPath refreshSVG = new SVGPath();
    Region refreshIcon = new Region();
    ControlTooltip refreshTooltip;

    Button removeButton = new Button();
    SVGPath removeSVG = new SVGPath();
    Region removeIcon = new Region();
    ControlTooltip removeTooltip;


    LibraryItem(LibrariesSection librariesSection, File file){

        this.librariesSection = librariesSection;
        this.file = file;

        this.getChildren().addAll(pathLabel, refreshButton, removeButton);
        this.setPadding(new Insets(8, 10, 8, 10));
        this.getStyleClass().add("highlightedSection");

        folderSVG.setContent(SVG.FOLDER.getContent());
        folderIcon.setShape(folderSVG);
        folderIcon.setPrefSize(14, 12);
        folderIcon.setMaxSize(14,12);
        folderIcon.getStyleClass().addAll("menuIcon", "graphic");
        folderIcon.setMouseTransparent(true);

        pathLabel.setPadding(new Insets(0, 0, 0, 10));
        pathLabel.setGraphic(folderIcon);
        pathLabel.setText(file.getAbsolutePath());
        pathLabel.getStyleClass().add("toggleText");
        pathLabel.maxWidthProperty().bind(this.widthProperty().subtract(130));
        StackPane.setAlignment(pathLabel, Pos.CENTER_LEFT);

        refreshSVG.setContent(SVG.REFRESH.getContent());
        refreshIcon.setShape(refreshSVG);
        refreshIcon.setPrefSize(15, 15);
        refreshIcon.setMaxSize(15,15);
        refreshIcon.getStyleClass().addAll("menuIcon", "graphic");
        refreshIcon.setMouseTransparent(true);

        StackPane.setAlignment(refreshButton, Pos.CENTER_RIGHT);
        StackPane.setMargin(refreshButton, new Insets(0, 50, 0, 0));
        refreshButton.setCursor(Cursor.HAND);
        refreshButton.getStyleClass().add("menuButton");
        refreshButton.setGraphic(refreshIcon);
        refreshButton.setOnAction(e -> {
            if(librariesSection.settingsPage.menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) librariesSection.settingsPage.menuController.subtitlesController.closeSubtitles();
            if(librariesSection.settingsPage.menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) librariesSection.settingsPage.menuController.playbackSettingsController.closeSettings();
            librariesSection.refreshLibrary(file);
        });
        refreshButton.setFocusTraversable(false);

        removeSVG.setContent(SVG.REMOVE.getContent());
        removeIcon.setShape(removeSVG);
        removeIcon.setPrefSize(15, 15);
        removeIcon.setMaxSize(15,15);
        removeIcon.getStyleClass().addAll("menuIcon", "graphic");
        removeIcon.setMouseTransparent(true);

        StackPane.setAlignment(removeButton, Pos.CENTER_RIGHT);
        removeButton.setCursor(Cursor.HAND);
        removeButton.getStyleClass().add("menuButton");
        removeButton.setGraphic(removeIcon);
        removeButton.setOnAction(e -> {
            if(librariesSection.settingsPage.menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) librariesSection.settingsPage.menuController.subtitlesController.closeSubtitles();
            if(librariesSection.settingsPage.menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) librariesSection.settingsPage.menuController.playbackSettingsController.closeSettings();
            librariesSection.removeLibrary(this);
        });
        refreshButton.setFocusTraversable(false);

        Platform.runLater(() -> {
            refreshTooltip = new ControlTooltip(librariesSection.settingsPage.menuController.mainController,"Refresh folder", "", refreshButton, 1000);
            removeTooltip = new ControlTooltip(librariesSection.settingsPage.menuController.mainController,"Remove folder", "", removeButton, 1000);
        });
    }
}