package fxplayer.menu.Settings.Libraries;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import fxplayer.menu.FocusableMenuButton;
import fxplayer.menu.Settings.SettingsPage;
import fxplayer.menu.Settings.SettingsSection;
import fxplayer.playbackSettings.PlaybackSettingsState;
import fxplayer.SVG;
import fxplayer.subtitles.SubtitlesState;
import fxplayer.Utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static fxplayer.Utilities.keyboardFocusOff;
import static fxplayer.Utilities.keyboardFocusOn;

public class LibrariesSection extends StackPane implements SettingsSection {

    SettingsPage settingsPage;

    VBox librariesSectionWrapper = new VBox();

    StackPane titlePane = new StackPane();
    Label librariesSectionTitle = new Label("Libraries");


    Label infoToggle = new Label();
    SVGPath infoSVG = new SVGPath();
    Region infoIcon = new Region();
    Label infoLabel = new Label("FXPlayer will scan selected folders for media files and organize them into a media library.\nIt is recommended to keep your device plugged in during the library indexing process as it may increase CPU and battery usage.");

    LibraryContainer musicContainer;
    LibraryContainer videoContainer;

    StackPane refreshAllPane = new StackPane();
    SVGPath wrenchSVG = new SVGPath();
    Region wrenchIcon = new Region();
    Label refreshAllLabel = new Label("Refresh all libraries");
    SVGPath refreshSVG = new SVGPath();
    Region refreshIcon = new Region();
    FocusableMenuButton refreshAllButton = new FocusableMenuButton();

    IntegerProperty focus = new SimpleIntegerProperty(-1);
    List<Node> focusNodes = new ArrayList<>();

    boolean infoToggleHover = false;

    public LibrariesSection(SettingsPage settingsPage){
        this.settingsPage = settingsPage;

        this.getChildren().addAll(librariesSectionWrapper, infoLabel);

        StackPane.setAlignment(librariesSectionWrapper, Pos.TOP_CENTER);

        titlePane.getChildren().addAll(librariesSectionTitle, infoToggle);

        StackPane.setAlignment(librariesSectionTitle, Pos.CENTER_LEFT);
        librariesSectionTitle.getStyleClass().add("settingsSectionTitle");

        musicContainer = new LibraryContainer(this, "Music library locations", SVG.MUSIC.getContent(), 1);
        videoContainer = new LibraryContainer(this, "Video library locations", SVG.VIDEO.getContent(), 2);

        musicContainer.header.setStyle("-fx-border-radius: 10 10 3 3; -fx-background-radius: 10 10 3 3;");
        musicContainer.setPadding(new Insets(10, 0, 0, 0));

        VBox.setMargin(refreshAllPane, new Insets(15, 0, 0, 0));
        refreshAllPane.getChildren().addAll(wrenchIcon, refreshAllLabel, refreshAllButton);
        refreshAllPane.setPadding(new Insets(20, 10, 20, 10));
        refreshAllPane.getStyleClass().add("highlightedSection");
        refreshAllPane.setStyle("-fx-border-radius: 3 3 10 10; -fx-background-radius: 3 3 10 10;");


        wrenchSVG.setContent(SVG.WRENCH.getContent());
        wrenchIcon.setShape(wrenchSVG);
        wrenchIcon.setPrefSize(17, 17);
        wrenchIcon.setMaxSize(17,17);
        wrenchIcon.getStyleClass().addAll("menuIcon", "graphic");
        wrenchIcon.setMouseTransparent(true);
        StackPane.setAlignment(wrenchIcon, Pos.CENTER_LEFT);
        StackPane.setMargin(wrenchIcon, new Insets(0, 0, 0, 8));

        StackPane.setAlignment(refreshAllLabel, Pos.CENTER_LEFT);
        StackPane.setMargin(refreshAllLabel, new Insets(0, 0, 0, 35));
        refreshAllLabel.getStyleClass().add("toggleText");

        StackPane.setAlignment(refreshAllButton, Pos.CENTER_RIGHT);
        StackPane.setMargin(refreshAllButton, new Insets(0, 40, 0, 0));
        refreshSVG.setContent(SVG.REFRESH.getContent());
        refreshIcon.setShape(refreshSVG);
        refreshIcon.setPrefSize(15, 15);
        refreshIcon.setMaxSize(15,15);
        refreshIcon.getStyleClass().addAll("menuIcon", "graphic");

        refreshAllButton.setText("Refresh all");
        refreshAllButton.setCursor(Cursor.HAND);
        refreshAllButton.getStyleClass().add("menuButton");
        refreshAllButton.setGraphic(refreshIcon);
        refreshAllButton.setOnAction(e -> {
            refreshAllButton.requestFocus();
            if(settingsPage.menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) settingsPage.menuController.subtitlesController.closeSubtitles();
            if(settingsPage.menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) settingsPage.menuController.playbackSettingsController.closeSettings();
            refreshAll();
        });
        refreshAllButton.setDisable(true);
        refreshAllButton.disabledProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue) focusNodes.remove(refreshAllButton);
            else if(!focusNodes.contains(refreshAllButton)) focusNodes.add(refreshAllButton);
        });

        refreshAllButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(3);
                settingsPage.focus.set(2);
            }
            else {
                keyboardFocusOff(refreshAllButton);
                focus.set(-1);
                settingsPage.focus.set(-1);
            }
        });

        refreshAllButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            refreshAllButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        refreshAllButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            refreshAllButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });

        infoSVG.setContent(SVG.INFORMATION_OUTLINE.getContent());

        infoIcon.setShape(infoSVG);
        infoIcon.setPrefSize(25, 25);
        infoIcon.setMaxSize(25, 25);
        infoIcon.getStyleClass().add("graphic");

        StackPane.setAlignment(infoToggle, Pos.CENTER_RIGHT);
        StackPane.setMargin(infoToggle, new Insets(0, 10, 0, 0));
        infoToggle.setGraphic(infoIcon);
        infoToggle.getStyleClass().add("infoLabel");
        infoToggle.setOnMouseEntered(e -> {
            infoToggleHover = true;
            infoLabel.setVisible(true);
        });
        infoToggle.setOnMouseExited(e -> {
            infoToggleHover = false;
            if(!infoToggle.isFocused()) infoLabel.setVisible(false);
        });

        infoToggle.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(0);
                settingsPage.focus.set(2);

                infoLabel.setVisible(true);
            }
            else {
                keyboardFocusOff(infoToggle);
                focus.set(-1);
                settingsPage.focus.set(-1);

                if(!infoToggleHover) infoLabel.setVisible(false);
            }
        });

        infoLabel.setVisible(false);
        infoLabel.setMouseTransparent(true);
        infoLabel.getStyleClass().add("settingsInfoWindow");
        infoLabel.setWrapText(true);
        infoLabel.setPrefSize(380, 126);
        infoLabel.setMaxSize(380, 126);
        infoLabel.setPadding(new Insets(5, 10, 5, 10));

        StackPane.setAlignment(infoLabel, Pos.TOP_RIGHT);
        StackPane.setMargin(infoLabel, new Insets(40, 0, 0, 0));

        librariesSectionWrapper.getChildren().addAll(titlePane, musicContainer, videoContainer, refreshAllPane);
        focusNodes.add(infoToggle);
        focusNodes.add(musicContainer);
        focusNodes.add(videoContainer);
    }


    public void refreshLibrary(File folder){
        System.out.println("Refresh library");
    }

    public void refreshAll(){
        System.out.println("Refresh all");
    }


    @Override
    public boolean focusForward(){


        if(focus.get() == 1){
            boolean skipFocus = musicContainer.focusForward();
            if(skipFocus) videoContainer.focusForward();
        }
        else if(focus.get() == 2){
            boolean skipFocus = videoContainer.focusForward();
            if(skipFocus){
                if(focusNodes.size() == 4) {
                    keyboardFocusOn(refreshAllButton);
                    Utilities.checkScrollDown(settingsPage.settingsScroll, refreshAllPane);
                }
                else return true;
            }
        }
        else if(focus.get() == 0){
            musicContainer.focusForward();
        }
        else if(focus.get() == -1) {
            keyboardFocusOn(infoToggle);
            Utilities.checkScrollDown(settingsPage.settingsScroll, titlePane);
        }
        else
            return true;

        return false;
    }

    @Override
    public boolean focusBackward(){
        if(focus.get() == 0) return true;
        else if(focus.get() == 1){
            boolean skipFocus = musicContainer.focusBackward();
            if(skipFocus){
                keyboardFocusOn(infoToggle);
                Utilities.checkScrollUp(settingsPage.settingsScroll, titlePane);
            }
        }
        else if(focus.get() == 2){
            boolean skipFocus = videoContainer.focusBackward();
            if(skipFocus) musicContainer.focusBackward();
        }
        else if(focus.get() == 3){
            videoContainer.focusBackward();
        }
        else {
            Node node = focusNodes.get(focusNodes.size() - 1);
            if(node instanceof LibraryContainer) videoContainer.focusBackward();
            else {
                keyboardFocusOn(refreshAllButton);
                Utilities.checkScrollUp(settingsPage.settingsScroll, refreshAllPane);
            }
        }

        return false;
    }

    @Override
    public void setFocus(int value){
        this.focus.set(value);
    }

}
