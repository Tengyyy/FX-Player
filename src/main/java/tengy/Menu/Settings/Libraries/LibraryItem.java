package tengy.Menu.Settings.Libraries;

import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import tengy.ControlTooltip;
import tengy.Menu.FocusableMenuButton;
import tengy.PlaybackSettings.PlaybackSettingsState;
import tengy.SVG;
import tengy.Subtitles.SubtitlesState;
import tengy.Utilities;

import java.io.File;

import static tengy.Utilities.keyboardFocusOff;
import static tengy.Utilities.keyboardFocusOn;

public class LibraryItem extends StackPane {


    File file;
    SVGPath folderSVG = new SVGPath();
    Region folderIcon = new Region();
    Label pathLabel = new Label();

    FocusableMenuButton refreshButton = new FocusableMenuButton();
    SVGPath refreshSVG = new SVGPath();
    Region refreshIcon = new Region();
    ControlTooltip refreshTooltip;

    FocusableMenuButton removeButton = new FocusableMenuButton();
    SVGPath removeSVG = new SVGPath();
    Region removeIcon = new Region();
    ControlTooltip removeTooltip;

    LibraryContainer libraryContainer;

    int index = -1;

    boolean removePressed = false;

    LibraryItem(LibraryContainer libraryContainer, File file, int containerIndex){

        this.file = file;
        this.libraryContainer = libraryContainer;
        this.index = libraryContainer.container.getChildren().size();

        this.getChildren().addAll(folderIcon, pathLabel, refreshButton, removeButton);
        this.setPadding(new Insets(14, 10, 14, 10));
        this.getStyleClass().add("highlightedSection");
        this.setOpacity(0);
        this.setMinHeight(64);
        this.setMaxHeight(64);
        this.setMouseTransparent(true);

        VBox.setMargin(this, new Insets(3, 0, 0, 0));

        folderSVG.setContent(SVG.FOLDER.getContent());
        folderIcon.setShape(folderSVG);
        folderIcon.setPrefSize(15, 15);
        folderIcon.setMaxSize(15,15);
        folderIcon.getStyleClass().add("menuIcon");
        folderIcon.setMouseTransparent(true);
        StackPane.setAlignment(folderIcon, Pos.CENTER_LEFT);
        StackPane.setMargin(folderIcon, new Insets(0, 0, 0, 20));

        pathLabel.setText(file.getAbsolutePath());
        pathLabel.getStyleClass().add("toggleText");
        pathLabel.maxWidthProperty().bind(this.widthProperty().subtract(170));
        StackPane.setMargin(pathLabel, new Insets(0, 0, 0, 47));
        StackPane.setAlignment(pathLabel, Pos.CENTER_LEFT);

        refreshSVG.setContent(SVG.REFRESH.getContent());
        refreshIcon.setShape(refreshSVG);
        refreshIcon.setPrefSize(15, 15);
        refreshIcon.setMaxSize(15,15);
        refreshIcon.getStyleClass().addAll("menuIcon", "graphic");

        StackPane.setAlignment(refreshButton, Pos.CENTER_RIGHT);
        StackPane.setMargin(refreshButton, new Insets(0, 50, 0, 0));
        refreshButton.getStyleClass().add("menuButton");
        refreshButton.setGraphic(refreshIcon);
        refreshButton.setOnAction(e -> {
            refreshButton.requestFocus();
            if(libraryContainer.librariesSection.settingsPage.menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) libraryContainer.librariesSection.settingsPage.menuController.subtitlesController.closeSubtitles();
            if(libraryContainer.librariesSection.settingsPage.menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) libraryContainer.librariesSection.settingsPage.menuController.playbackSettingsController.closeSettings();

            libraryContainer.librariesSection.refreshLibrary(file);
        });

        refreshButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue) {
                libraryContainer.focus.set(index + 2);
                libraryContainer.librariesSection.focus.set(containerIndex);
                libraryContainer.librariesSection.settingsPage.focus.set(2);
            }
            else {
                keyboardFocusOff(refreshButton);
                libraryContainer.focus.set(-1);
                libraryContainer.librariesSection.focus.set(-1);
                libraryContainer.librariesSection.settingsPage.focus.set(-1);
            }
        });

        refreshButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            refreshButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        refreshButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            refreshButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });

        removeSVG.setContent(SVG.REMOVE.getContent());
        removeIcon.setShape(removeSVG);
        removeIcon.setPrefSize(15, 15);
        removeIcon.setMaxSize(15,15);
        removeIcon.getStyleClass().addAll("menuIcon", "graphic");

        StackPane.setAlignment(removeButton, Pos.CENTER_RIGHT);
        removeButton.getStyleClass().add("menuButton");
        removeButton.setGraphic(removeIcon);
        removeButton.setOnAction(e -> {
            removeButton.requestFocus();
            if(libraryContainer.librariesSection.settingsPage.menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) libraryContainer.librariesSection.settingsPage.menuController.subtitlesController.closeSubtitles();
            if(libraryContainer.librariesSection.settingsPage.menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) libraryContainer.librariesSection.settingsPage.menuController.playbackSettingsController.closeSettings();

            libraryContainer.remove(this);
        });

        removeButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue) {
                System.out.println("test");
                libraryContainer.focus.set(index + 2);
                libraryContainer.librariesSection.focus.set(containerIndex);
                libraryContainer.librariesSection.settingsPage.focus.set(2);
            }
            else {
                keyboardFocusOff(removeButton);
                libraryContainer.focus.set(-1);
                libraryContainer.librariesSection.focus.set(-1);
                libraryContainer.librariesSection.settingsPage.focus.set(-1);
                removePressed = false;
            }
        });

        removeButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            removeButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
            removePressed = true;
            e.consume();
        });

        removeButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            removeButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);

            if(removePressed){
                libraryContainer.remove(this);

                if(libraryContainer.container.getChildren().size() - 1 > index){
                    Node node = libraryContainer.container.getChildren().get(index + 1);
                    LibraryItem libraryItem = (LibraryItem) node;
                    keyboardFocusOn(libraryItem.removeButton);
                }
                else if(index > 0){
                    Node node = libraryContainer.container.getChildren().get(index - 1);
                    LibraryItem libraryItem = (LibraryItem) node;
                    keyboardFocusOn(libraryItem.removeButton);
                }
                else keyboardFocusOn(libraryContainer.header);
            }

            removePressed = false;

            e.consume();
        });

        Platform.runLater(() -> {
            refreshTooltip = new ControlTooltip(libraryContainer.librariesSection.settingsPage.menuController.mainController,"Refresh folder", "", refreshButton, 1000);
            removeTooltip = new ControlTooltip(libraryContainer.librariesSection.settingsPage.menuController.mainController,"Remove folder", "", removeButton, 1000);
        });
    }

    public boolean focusForward(){
        if(removeButton.isFocused()) return true;

        if(refreshButton.isFocused()) keyboardFocusOn(removeButton);
        else keyboardFocusOn(refreshButton);

        Utilities.checkScrollDown(libraryContainer.librariesSection.settingsPage.settingsScroll, this);


        return false;
    }

    public boolean focusBackward(){
        if(refreshButton.isFocused()) return true;

        if(removeButton.isFocused()) keyboardFocusOn(refreshButton);
        else keyboardFocusOn(removeButton);

        Utilities.checkScrollUp(libraryContainer.librariesSection.settingsPage.settingsScroll, this);

        return false;
    }
}
