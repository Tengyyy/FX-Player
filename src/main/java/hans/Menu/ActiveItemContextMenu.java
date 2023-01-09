package hans.Menu;

import hans.AnimationsClass;
import hans.App;
import hans.Captions.CaptionsController;
import hans.Captions.CaptionsTab;
import hans.SVG;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;

public class ActiveItemContextMenu extends MenuItemContextMenu {

    ActiveItem activeItem;
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

        this.getItems().add(3, subtitles);

    }



    public void openSubtitleChooser(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select subtitles");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Subtitles", "*.srt"));
        fileChooser.setInitialDirectory(activeItem.getMediaItem().getFile().getParentFile());

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

    @Override
    public void hide(){
        subtitleScroll.setVvalue(0);
        super.hide();
    }

}
