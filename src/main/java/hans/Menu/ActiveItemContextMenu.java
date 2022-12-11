package hans.Menu;

import hans.AnimationsClass;
import hans.App;
import hans.Captions.CaptionsController;
import hans.Captions.CaptionsTab;
import hans.SVG;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.FileChooser;

import java.io.File;

public class ActiveItemContextMenu extends MenuItemContextMenu {

    ActiveItem activeItem;
    CaptionsController captionsController;

    public Menu subtitles = new Menu("Subtitles");
    public MenuItem externalSubtitles = new MenuItem();


    SVGPath subtitlesPath = new SVGPath(), externalSubtitlesPath = new SVGPath();
    Region subtitlesIcon = new Region(), externalSubtitlesIcon = new Region();
    StackPane externalSubtitlesIconPane = new StackPane();
    HBox externalSubtitlesWrapper = new HBox();
    Label externalSubtitlesLabel = new Label("Add external subtitles");

    ActiveItemContextMenu(ActiveItem activeItem, CaptionsController captionsController){
        super(activeItem);

        this.activeItem = activeItem;
        this.captionsController = captionsController;

        subtitlesPath.setContent(App.svgMap.get(SVG.CAPTIONS_OUTLINE));
        subtitlesIcon.setShape(subtitlesPath);
        subtitlesIcon.getStyleClass().add("icon");
        subtitlesIcon.setPrefSize(20, 20);
        subtitlesIcon.setMaxSize(20, 20);

        subtitles.setGraphic(subtitlesIcon);
        subtitles.getStyleClass().add("popUpItem");

        externalSubtitlesPath.setContent(App.svgMap.get(SVG.MAGNIFY));
        externalSubtitlesWrapper.setMinSize(220, 39);
        externalSubtitlesWrapper.setPrefSize(220, 39);
        externalSubtitlesWrapper.setMaxSize(220, 39);

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
        externalSubtitlesLabel.setPrefSize(170, 39);
        externalSubtitlesLabel.setMaxSize(170, 39);

        externalSubtitlesWrapper.getChildren().addAll(externalSubtitlesIconPane, externalSubtitlesLabel);
        externalSubtitles.setOnAction(e -> openSubtitleChooser());
        externalSubtitles.setGraphic(externalSubtitlesWrapper);
        externalSubtitles.getStyleClass().add("subtitle-menu-item");

        subtitles.getItems().add(externalSubtitles);

        this.getItems().add(3, subtitles);
    }



    public void openSubtitleChooser(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select subtitles");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Subtitles", "*.srt"));
        fileChooser.setInitialDirectory(activeItem.getMediaItem().getFile().getParentFile());

        File selectedFile = fileChooser.showOpenDialog(App.stage);

        if(selectedFile != null){
            captionsController.captionsHome.createTab(selectedFile);
        }
    }

    public MenuItem createSubtitleItem(CaptionsTab captionsTab){

        MenuItem menuItem = new MenuItem();
        menuItem.getStyleClass().add("subtitle-menu-item");

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
        menuItemWrapper.setPrefSize(220, 39);
        menuItemWrapper.setMaxSize(220, 39);

        menuItemWrapper.setPadding(new Insets(0, 10, 0, 10));

        checkIconPane.setMinSize(30, 39);
        checkIconPane.setPrefSize(30, 39);
        checkIconPane.setMaxSize(30, 39);
        checkIconPane.setPadding(new Insets(0, 5, 0, 0));
        checkIconPane.getChildren().add(checkIcon);

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

        menuItem.setOnAction(e -> captionsTab.selectSubtitles());

        if(captionsTab.removable){
            valueLabel.setMinWidth(140);
            valueLabel.setPrefWidth(140);
            valueLabel.setMaxWidth(140);

            removePane.setMinSize(30, 39);
            removePane.setPrefSize(30, 39);
            removePane.setMaxSize(30, 39);

            removePane.getChildren().addAll(removeButton, removeIcon);

            removeButton.setMinSize(25, 29);
            removeButton.setPrefSize(25, 29);
            removeButton.setMaxSize(25, 29);
            removeButton.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> AnimationsClass.AnimateBackgroundColor(removeIcon, (Color) removeIcon.getBackground().getFills().get(0).getFill(), Color.rgb(255, 255, 255), 200));
            removeButton.addEventHandler(MouseEvent.MOUSE_EXITED, e -> AnimationsClass.AnimateBackgroundColor(removeIcon, (Color) removeIcon.getBackground().getFills().get(0).getFill(), Color.rgb(200, 200, 200), 200));
            removeButton.setCursor(Cursor.HAND);
            removeButton.setBackground(Background.EMPTY);
            removeButton.setOnAction(e -> {
                captionsTab.removeItem();
                e.consume();
                if(subtitles.isShowing()) subtitles.hide();
                if(this.showing) this.hide();
            });

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
            valueLabel.setPrefWidth(170);
            valueLabel.setMaxWidth(170);

            menuItemWrapper.getChildren().addAll(checkIconPane, valueLabel);
        }

        menuItem.setGraphic(menuItemWrapper);
        subtitles.getItems().add(menuItem);

        return menuItem;
    }



}
