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
import javafx.scene.text.Font;
import javafx.stage.FileChooser;

import java.io.File;

public class ActiveItemContextMenu extends MenuItemContextMenu {

    ActiveItem activeItem;
    CaptionsController captionsController;

    public Menu subtitles = new Menu("Subtitles");
    MenuItem externalSubtitles = new MenuItem("Add external subtitles");


    SVGPath subtitlesPath = new SVGPath(), externalSubtitlesPath = new SVGPath();
    Region subtitlesIcon = new Region(), externalSubtitlesIcon = new Region();

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
        externalSubtitlesIcon.setShape(externalSubtitlesPath);
        externalSubtitlesIcon.getStyleClass().add("icon");
        externalSubtitlesIcon.setPrefSize(17, 17);
        externalSubtitlesIcon.setMaxSize(17, 17);

        externalSubtitles.setGraphic(externalSubtitlesIcon);
        externalSubtitles.getStyleClass().add("popUpItem");
        externalSubtitles.setOnAction(e -> openSubtitleChooser());

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

        menuItemWrapper.setMinSize(235, 35);
        menuItemWrapper.setPrefSize(235, 35);
        menuItemWrapper.setMaxSize(235, 35);

        menuItemWrapper.setPadding(new Insets(0, 10, 0, 10));

        checkIconPane.setMinSize(30, 30);
        checkIconPane.setPrefSize(30, 30);
        checkIconPane.setMaxSize(30, 30);
        checkIconPane.setPadding(new Insets(0, 5, 0, 0));
        checkIconPane.getChildren().add(checkIcon);
        checkIconPane.setOnMouseClicked(e -> {
            System.out.println("test");
            captionsTab.selectSubtitles();
        });

        checkIcon.setMinSize(17, 13);
        checkIcon.setPrefSize(17, 13);
        checkIcon.setMaxSize(17, 13);
        checkIcon.setShape(checkSVG);
        checkIcon.getStyleClass().add("icon");
        checkIcon.visibleProperty().bind(captionsTab.checkIcon.visibleProperty());

        valueLabel.setMinHeight(35);
        valueLabel.setPrefHeight(35);
        valueLabel.setMaxHeight(35);
        valueLabel.setText(captionsTab.valueLabel.getText());
        valueLabel.setOnMouseClicked(e -> {
            System.out.println("test");
            captionsTab.selectSubtitles();
        });

        if(captionsTab.removable){
            valueLabel.setMinWidth(150);
            valueLabel.setPrefWidth(150);
            valueLabel.setMaxWidth(150);

            removePane.setMinSize(35, 35);
            removePane.setPrefSize(35, 35);
            removePane.setMaxSize(35, 35);

            removePane.getChildren().addAll(removeButton, removeIcon);

            removeButton.setMinSize(25, 25);
            removeButton.setPrefSize(25, 25);
            removeButton.setMaxSize(25, 25);
            removeButton.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> AnimationsClass.AnimateBackgroundColor(removeIcon, (Color) removeIcon.getBackground().getFills().get(0).getFill(), Color.rgb(255, 255, 255), 200));
            removeButton.addEventHandler(MouseEvent.MOUSE_EXITED, e -> AnimationsClass.AnimateBackgroundColor(removeIcon, (Color) removeIcon.getBackground().getFills().get(0).getFill(), Color.rgb(200, 200, 200), 200));
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
            valueLabel.setMinWidth(185);
            valueLabel.setPrefWidth(185);
            valueLabel.setMaxWidth(185);

            menuItemWrapper.getChildren().addAll(checkIconPane, valueLabel);
        }

        menuItem.setGraphic(menuItemWrapper);
        subtitles.getItems().add(menuItem);

        return menuItem;
    }



}
