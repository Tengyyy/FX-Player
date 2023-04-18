package hans.Menu.Settings;

import hans.App;
import hans.Subtitles.SubtitlesState;
import hans.SVG;
import hans.PlaybackSettings.PlaybackSettingsState;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LibrariesSection extends StackPane {

    SettingsPage settingsPage;

    VBox librariesSectionWrapper = new VBox();

    StackPane titlePane = new StackPane();
    Label librariesSectionTitle = new Label("Music libraries");
    Button addFolderButton = new Button("Add folder");
    SVGPath folderSVG = new SVGPath();
    Region folderIcon = new Region();

    Label infoToggle = new Label();
    SVGPath infoSVG = new SVGPath();
    Region infoIcon = new Region();
    Label infoLabel = new Label("FX Player will scan selected libraries for audio files and save metadata like artists, albums etc. to make your music listening experience more enjoyable.\nIt is recommended to keep your device plugged in during the library indexing process as it may increase CPU and battery usage.");

    VBox librariesContainer = new VBox();
    List<File> libraries = new ArrayList<>();

    StackPane refreshAllPane = new StackPane();
    SVGPath wrenchSVG = new SVGPath();
    Region wrenchIcon = new Region();
    Label refreshAllLabel = new Label("Refresh all libraries");
    SVGPath refreshSVG = new SVGPath();
    Region refreshIcon = new Region();
    Button refreshAllButton = new Button("Refresh");

    DirectoryChooser libraryChooser = new DirectoryChooser();

    final int MIN_HEIGHT = 200;

    LibrariesSection(SettingsPage settingsPage){
        this.settingsPage = settingsPage;

        this.getChildren().addAll(librariesSectionWrapper, infoLabel);
        this.setMinHeight(MIN_HEIGHT);

        StackPane.setAlignment(librariesSectionWrapper, Pos.TOP_CENTER);
        librariesSectionWrapper.getChildren().addAll(titlePane, librariesContainer, refreshAllPane);

        libraryChooser.setTitle("Select folder");

        VBox.setMargin(titlePane, new Insets(0, 0, 20, 0));
        titlePane.getChildren().addAll(librariesSectionTitle, addFolderButton, infoToggle);

        StackPane.setAlignment(librariesSectionTitle, Pos.CENTER_LEFT);
        librariesSectionTitle.getStyleClass().add("settingsSectionTitle");

        folderSVG.setContent(App.svgMap.get(SVG.FOLDER));
        folderIcon.setShape(folderSVG);
        folderIcon.setPrefSize(14, 12);
        folderIcon.setMaxSize(14,12);
        folderIcon.getStyleClass().addAll("menuIcon", "graphic");
        folderIcon.setMouseTransparent(true);

        StackPane.setAlignment(addFolderButton, Pos.CENTER_RIGHT);
        StackPane.setMargin(addFolderButton, new Insets(0, 50, 0, 0));
        addFolderButton.setCursor(Cursor.HAND);
        addFolderButton.getStyleClass().add("menuButton");
        addFolderButton.setGraphic(folderIcon);
        addFolderButton.setOnAction(e -> {
            if(settingsPage.menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) settingsPage.menuController.subtitlesController.closeSubtitles();
            if(settingsPage.menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) settingsPage.menuController.playbackSettingsController.closeSettings();
            openLibraryChooser();
        });

        librariesContainer.setSpacing(8);

        VBox.setMargin(refreshAllPane, new Insets(8, 0, 0, 0));
        refreshAllPane.getChildren().addAll(refreshAllLabel, refreshAllButton);
        refreshAllPane.setPadding(new Insets(8, 10, 8, 10));
        refreshAllPane.getStyleClass().add("highlightedSection");

        StackPane.setAlignment(refreshAllLabel, Pos.CENTER_LEFT);

        wrenchSVG.setContent(App.svgMap.get(SVG.WRENCH));
        wrenchIcon.setShape(wrenchSVG);
        wrenchIcon.setPrefSize(17, 17);
        wrenchIcon.setMaxSize(17,17);
        wrenchIcon.getStyleClass().addAll("menuIcon", "graphic");
        wrenchIcon.setMouseTransparent(true);

        refreshAllLabel.setGraphic(wrenchIcon);
        refreshAllLabel.getStyleClass().add("toggleText");
        refreshAllLabel.setPadding(new Insets(0, 0, 0, 8));

        StackPane.setAlignment(refreshAllButton, Pos.CENTER_RIGHT);
        refreshSVG.setContent(App.svgMap.get(SVG.REFRESH));
        refreshIcon.setShape(refreshSVG);
        refreshIcon.setPrefSize(15, 15);
        refreshIcon.setMaxSize(15,15);
        refreshIcon.getStyleClass().addAll("menuIcon", "graphic");
        refreshIcon.setMouseTransparent(true);

        refreshAllButton.setCursor(Cursor.HAND);
        refreshAllButton.getStyleClass().add("menuButton");
        refreshAllButton.setGraphic(refreshIcon);
        refreshAllButton.setOnAction(e -> {
            if(settingsPage.menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) settingsPage.menuController.subtitlesController.closeSubtitles();
            if(settingsPage.menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) settingsPage.menuController.playbackSettingsController.closeSettings();
            refreshAll();
        });
        refreshAllButton.setDisable(true);

        infoSVG.setContent(App.svgMap.get(SVG.INFORMATION_OUTLINE));

        infoIcon.setShape(infoSVG);
        infoIcon.setPrefSize(25, 25);
        infoIcon.setMaxSize(25, 25);
        infoIcon.getStyleClass().add("infoIcon");

        StackPane.setAlignment(infoToggle, Pos.CENTER_RIGHT);
        StackPane.setMargin(infoToggle, new Insets(0, 10, 0, 0));
        infoToggle.setGraphic(infoIcon);
        infoToggle.getStyleClass().add("infoLabel");
        infoToggle.setOnMouseEntered(e -> infoLabel.setVisible(true));
        infoToggle.setOnMouseExited(e -> infoLabel.setVisible(false));

        infoLabel.setVisible(false);
        infoLabel.setMouseTransparent(true);
        infoLabel.getStyleClass().add("settingsInfoWindow");
        infoLabel.setWrapText(true);
        infoLabel.setPrefSize(380, 146);
        infoLabel.setMaxSize(380, 146);
        infoLabel.setPadding(new Insets(5, 10, 5, 10));

        StackPane.setAlignment(infoLabel, Pos.TOP_RIGHT);
        StackPane.setMargin(infoLabel, new Insets(40, 0, 0, 0));
    }

    private void openLibraryChooser(){
        File folder = libraryChooser.showDialog(titlePane.getScene().getWindow());

        if(folder != null) addLibrary(folder);
    }

    public void addLibrary(File folder){
        for(File file : libraries) if(file.equals(folder)) return;

        librariesContainer.getChildren().add(new LibraryItem(this, folder));
        libraries.add(folder);
        refreshAllButton.setDisable(false);

        this.setMinHeight(Math.max(MIN_HEIGHT, libraries.size() * 60 +  100));
    }

    public void removeLibrary(LibraryItem libraryItem){

        librariesContainer.getChildren().remove(libraryItem);
        libraries.remove(libraryItem.file);

        this.setMinHeight(Math.max(MIN_HEIGHT, libraries.size() * 60 +  100));

        if(libraries.isEmpty()) refreshAllButton.setDisable(true);
    }

    public void refreshLibrary(File folder){
        System.out.println("Refresh library");
    }

    public void refreshAll(){
        System.out.println("Refresh all");
    }



}
