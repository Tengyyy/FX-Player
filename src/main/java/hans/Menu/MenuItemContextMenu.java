package hans.Menu;

import hans.AnimationsClass;
import hans.App;
import hans.Captions.CaptionsController;
import hans.Captions.CaptionsTab;
import hans.SVG;
import hans.Shell32Util;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class MenuItemContextMenu extends ContextMenu {

    QueueItem queueItem;

    MenuItem playNext = new MenuItem("Play next");
    MenuItem metadata = new MenuItem("Media metadata");
    MenuItem technicalDetails = new MenuItem("Technical details");
    MenuItem openFileLocation = new MenuItem("Open file in location");

    double buttonWidth;
    final double popUpWidth = 214; // calling getWidth on this pop-up window is inaccurate as it sometimes incorrectly shows 151, hard-coded value is used to always get the same result

    FadeTransition showTransition, hideTransition;

    SVGPath playNextPath = new SVGPath(), metadataPath = new SVGPath(), technicalDetailsPath = new SVGPath(), folderPath = new SVGPath();
    Region playNextIcon = new Region(), metadataIcon = new Region(), technicalDetailsIcon = new Region(), folderIcon = new Region();

    CaptionsController captionsController;

    public Menu subtitles = new Menu("Subtitles");
    public CustomMenuItem subtitleContent;

    public ScrollPane subtitleScroll = new ScrollPane();
    public VBox subtitleContainer = new VBox();

    SVGPath subtitlesPath = new SVGPath(), externalSubtitlesPath = new SVGPath();
    Region subtitlesIcon = new Region(), externalSubtitlesIcon = new Region();
    StackPane externalSubtitlesIconPane = new StackPane();
    public HBox externalSubtitlesWrapper = new HBox();
    Label externalSubtitlesLabel = new Label("Add external subtitles");

    final int SUBMENU_HEIGHT = 200;

    boolean showing = false;


    MenuItemContextMenu(QueueItem queueItem){

        this.queueItem = queueItem;
        this.captionsController = queueItem.menuController.captionsController;

        queueItem.getScene().getStylesheets().add(Objects.requireNonNull(queueItem.getMenuController().mainController.getClass().getResource("styles/optionsPopUp.css")).toExternalForm());

        playNextPath.setContent(App.svgMap.get(SVG.PLAY_CIRCLE));
        playNextIcon.setShape(playNextPath);
        playNextIcon.getStyleClass().add("icon");
        playNextIcon.setPrefSize(20, 20);
        playNextIcon.setMaxSize(20, 20);

        playNext.setGraphic(playNextIcon);
        playNext.getStyleClass().add("popUpItem");
        playNext.setOnAction((e) -> queueItem.playNext());

        metadataPath.setContent(App.svgMap.get(SVG.INFORMATION_OUTLINE));
        metadataIcon.setShape(metadataPath);
        metadataIcon.getStyleClass().add("icon");
        metadataIcon.setPrefSize(20, 20);
        metadataIcon.setMaxSize(20, 20);

        metadata.setGraphic(metadataIcon);
        metadata.getStyleClass().add("popUpItem");
        metadata.setOnAction((e) -> queueItem.showMetadata());

        technicalDetailsPath.setContent(App.svgMap.get(SVG.COGS));
        technicalDetailsIcon.setShape(technicalDetailsPath);
        technicalDetailsIcon.getStyleClass().add("icon");
        technicalDetailsIcon.setPrefSize(20, 20);
        technicalDetailsIcon.setMaxSize(20, 20);

        technicalDetails.setGraphic(technicalDetailsIcon);
        technicalDetails.getStyleClass().add("popUpItem");
        technicalDetails.setOnAction((e) -> queueItem.showTechnicalDetails());

        subtitlesPath.setContent(App.svgMap.get(SVG.CAPTIONS_OUTLINE));
        subtitlesIcon.setShape(subtitlesPath);
        subtitlesIcon.getStyleClass().add("icon");
        subtitlesIcon.setPrefSize(20, 20);
        subtitlesIcon.setMaxSize(20, 20);

        subtitles.setGraphic(subtitlesIcon);
        subtitles.getStyleClass().add("popUpItem");

        externalSubtitlesPath.setContent(App.svgMap.get(SVG.MAGNIFY));
        externalSubtitlesWrapper.setMinSize(220, 39);
        externalSubtitlesWrapper.setPrefSize(240, 39);
        externalSubtitlesWrapper.setMaxSize(240, 39);
        externalSubtitlesWrapper.getStyleClass().add("subtitle-menu-item");
        externalSubtitlesWrapper.setOnMouseClicked(e -> openSubtitleChooser());


        externalSubtitlesWrapper.setPadding(new Insets(0, 10, 0, 10));

        externalSubtitlesIconPane.setMinSize(30, 39);
        externalSubtitlesIconPane.setPrefSize(30, 39);
        externalSubtitlesIconPane.setMaxSize(30, 39);
        externalSubtitlesIconPane.setPadding(new Insets(0, 5, 0, 0));
        externalSubtitlesIconPane.getChildren().add(externalSubtitlesIcon);

        externalSubtitlesIcon.setMinSize(17, 17);
        externalSubtitlesIcon.setPrefSize(17, 17);
        externalSubtitlesIcon.setMaxSize(17, 17);
        externalSubtitlesIcon.setShape(externalSubtitlesPath);
        externalSubtitlesIcon.getStyleClass().add("icon");

        externalSubtitlesLabel.setMinSize(170, 39);
        externalSubtitlesLabel.setPrefSize(190, 39);
        externalSubtitlesLabel.setMaxSize(190, 39);

        externalSubtitlesWrapper.getChildren().addAll(externalSubtitlesIconPane, externalSubtitlesLabel);

        subtitleScroll.setMinHeight(39);
        subtitleScroll.setPrefSize(240, 39);

        subtitleScroll.setContent(subtitleContainer);
        subtitleScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        subtitleScroll.getStyleClass().add("subtitle-menu-scroll");

        subtitleContainer.setMinHeight(39);
        subtitleContainer.setPrefSize(240, 39);

        subtitleContainer.getChildren().add(externalSubtitlesWrapper);
        subtitleContent = new CustomMenuItem(subtitleScroll, false);
        subtitleContent.getStyleClass().add("subtitle-menu");


        subtitles.getItems().add(subtitleContent);
        folderPath.setContent(App.svgMap.get(SVG.FOLDER));
        folderIcon.setShape(folderPath);
        folderIcon.getStyleClass().add("icon");
        folderIcon.setPrefSize(20, 20);
        folderIcon.setMaxSize(20, 20);

        openFileLocation.setGraphic(folderIcon);
        openFileLocation.getStyleClass().add("popUpItem");
        openFileLocation.setOnAction((e) -> openFileLocation(queueItem.getMediaItem().getMediaDetails().get("path")));


        this.getItems().addAll(playNext, metadata, technicalDetails, subtitles, openFileLocation);

        buttonWidth = queueItem.getOptionsButton().getWidth();

        this.getStyleableNode().setOpacity(0);
    }


    public void showOptions(boolean animate){
        this.show(queueItem, // might not work
                queueItem.getOptionsButton().localToScreen(queueItem.getOptionsButton().getBoundsInLocal()).getMinX() + buttonWidth/2 - popUpWidth/2,
                queueItem.getOptionsButton().localToScreen(queueItem.getOptionsButton().getBoundsInLocal()).getMaxY() + 5, animate);
    }


    public void show(Node node, double v, double v1, boolean animate) {

        if(hideTransition != null && hideTransition.getStatus() == Animation.Status.RUNNING) hideTransition.stop();

        if(animate) this.getStyleableNode().setOpacity(0);
        else this.getStyleableNode().setOpacity(1);

        super.show(node, v, v1);
        showing = true;

        if(animate){
            showTransition = new FadeTransition(Duration.millis(150), this.getStyleableNode());
            showTransition.setFromValue(0);
            showTransition.setToValue(1);
            showTransition.playFromStart();
        }

        queueItem.getMenuController().activeMenuItemContextMenu = this;
    }

    @Override
    public void hide() {

        showing = false;

        if(!queueItem.getHover()){
            queueItem.playButtonIcon.setVisible(false);
            queueItem.playButtonBackground.setVisible(false);
            queueItem.playIcon.setVisible(false);

            if(queueItem.isActive.get()) queueItem.setStyle("-fx-background-color: rgba(50, 50, 50, 0.6);");
            else {
                queueItem.setStyle("-fx-background-color: transparent;");
                queueItem.indexLabel.setVisible(true);
            }
        }

        if(showTransition != null && showTransition.getStatus() == Animation.Status.RUNNING) showTransition.stop();

        hideTransition = new FadeTransition(Duration.millis(150), this.getStyleableNode());
        hideTransition.setFromValue(1);
        hideTransition.setToValue(0);
        hideTransition.setOnFinished(e -> {
            subtitleScroll.setVvalue(0);
            super.hide();
        });
        hideTransition.playFromStart();

    }

    public void openFileLocation(String path){
        if(App.isWindows){
            Shell32Util.SHOpenFolderAndSelectItems(new File(path));
        }
        else if(Desktop.isDesktopSupported()){
            Desktop desktop = Desktop.getDesktop();
            File file = new File(path);

            if(desktop.isSupported(Desktop.Action.BROWSE_FILE_DIR)){
                desktop.browseFileDirectory(new File(path));
            }
            else if(desktop.isSupported(Desktop.Action.OPEN)){
                try {
                    desktop.open(file.getParentFile());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }


    public void openSubtitleChooser(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select subtitles");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Subtitles", "*.srt"));
        fileChooser.setInitialDirectory(queueItem.getMediaItem().getFile().getParentFile());

        File selectedFile = fileChooser.showOpenDialog(this.getOwnerWindow());

        subtitleScroll.setVvalue(1);
        this.showOptions(false);

        if(selectedFile != null){
            captionsController.captionsHome.createTab(selectedFile);
        }

    }

    public HBox createSubtitleItem(CaptionsTab captionsTab){


        HBox menuItemWrapper = new HBox();

        StackPane checkIconPane = new StackPane();
        Region checkIcon = new Region();
        SVGPath checkSVG = new SVGPath();
        SVGPath removeSVG = new SVGPath();

        Region removeIcon = new Region();
        StackPane removePane = new StackPane();
        Button removeButton = new Button();
        Label valueLabel = new Label();

        checkSVG.setContent(App.svgMap.get(SVG.CHECK));
        removeSVG.setContent(App.svgMap.get(SVG.CLOSE));

        menuItemWrapper.setMinSize(220, 39);
        menuItemWrapper.setPrefSize(240, 39);
        menuItemWrapper.setMaxSize(240, 39);
        menuItemWrapper.getStyleClass().add("subtitle-menu-item");
        menuItemWrapper.setPadding(new Insets(0, 10, 0, 10));

        checkIconPane.setMinSize(30, 39);
        checkIconPane.setPrefSize(30, 39);
        checkIconPane.setMaxSize(30, 39);
        checkIconPane.setPadding(new Insets(0, 5, 0, 0));
        checkIconPane.getChildren().add(checkIcon);
        checkIconPane.setOnMouseClicked(e -> captionsTab.selectSubtitles(true));

        checkIcon.setMinSize(17, 13);
        checkIcon.setPrefSize(17, 13);
        checkIcon.setMaxSize(17, 13);
        checkIcon.setShape(checkSVG);
        checkIcon.getStyleClass().add("icon");
        checkIcon.visibleProperty().bind(captionsTab.checkIcon.visibleProperty());

        valueLabel.setMinHeight(39);
        valueLabel.setPrefHeight(39);
        valueLabel.setMaxHeight(39);
        valueLabel.setText(captionsTab.valueLabel.getText());

        valueLabel.setOnMouseClicked(e -> captionsTab.selectSubtitles(true));

        if(captionsTab.removable){
            valueLabel.setMinWidth(140);
            valueLabel.setPrefWidth(160);
            valueLabel.setMaxWidth(160);

            removePane.setMinSize(30, 39);
            removePane.setPrefSize(30, 39);
            removePane.setMaxSize(30, 39);

            removePane.getChildren().addAll(removeButton, removeIcon);

            removeButton.setMinSize(25, 29);
            removeButton.setPrefSize(25, 29);
            removeButton.setMaxSize(25, 29);
            removeButton.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> AnimationsClass.animateBackgroundColor(removeIcon, (Color) removeIcon.getBackground().getFills().get(0).getFill(), Color.rgb(255, 255, 255), 200));
            removeButton.addEventHandler(MouseEvent.MOUSE_EXITED, e -> AnimationsClass.animateBackgroundColor(removeIcon, (Color) removeIcon.getBackground().getFills().get(0).getFill(), Color.rgb(200, 200, 200), 200));
            removeButton.setCursor(Cursor.HAND);
            removeButton.setBackground(Background.EMPTY);
            removeButton.setOnAction(e -> captionsTab.removeItem());

            removeIcon.setMinSize(16, 16);
            removeIcon.setPrefSize(16, 16);
            removeIcon.setMaxSize(16, 16);
            removeIcon.setShape(removeSVG);
            removeIcon.getStyleClass().add("icon");
            removeIcon.setMouseTransparent(true);

            menuItemWrapper.getChildren().addAll(checkIconPane, valueLabel, removePane);
        }
        else {
            valueLabel.setMinWidth(170);
            valueLabel.setPrefWidth(190);
            valueLabel.setMaxWidth(190);

            menuItemWrapper.getChildren().addAll(checkIconPane, valueLabel);
        }
        double newHeight = subtitleContainer.getPrefHeight() + 39;
        subtitleContainer.getChildren().add(1, menuItemWrapper);
        subtitleContainer.setPrefHeight(newHeight);
        subtitleScroll.setPrefHeight(Math.min(SUBMENU_HEIGHT, newHeight));
        subtitleScroll.setVvalue(1);

        Tooltip tooltip = new Tooltip(captionsTab.valueLabel.getText());
        tooltip.setShowDelay(Duration.millis(1000));
        tooltip.setHideDelay(Duration.ZERO);
        tooltip.setShowDuration(Duration.seconds(4));
        Tooltip.install(menuItemWrapper, tooltip);

        return menuItemWrapper;
    }
}