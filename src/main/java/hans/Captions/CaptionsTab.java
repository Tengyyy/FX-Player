package hans.Captions;

import hans.AnimationsClass;
import hans.App;
import hans.Menu.ActiveItem;
import hans.SVG;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;

import java.io.File;

public class CaptionsTab extends HBox {

    CaptionsController captionsController;
    CaptionsHome captionsHome;

    File captionFile;

    StackPane checkIconPane = new StackPane();
    public Region checkIcon = new Region();
    SVGPath checkSVG = new SVGPath();
    SVGPath removeSVG = new SVGPath();

    Region removeIcon = new Region();
    StackPane removePane = new StackPane();
    Button removeButton = new Button();
    public Label valueLabel = new Label();

    public boolean selected = false;

    public boolean removable;

    MenuItem menuItem;

    CaptionsTab(CaptionsController captionsController, CaptionsHome captionsHome, String value, File file, boolean removable){

        this.captionsController = captionsController;
        this.captionsHome = captionsHome;
        this.captionFile = file;
        this.removable = removable;


        checkSVG.setContent(App.svgMap.get(SVG.CHECK));
        removeSVG.setContent(App.svgMap.get(SVG.CLOSE));

        this.setMinSize(235, 35);
        this.setPrefSize(235, 35);
        this.setMaxSize(235, 35);

        this.setPadding(new Insets(0, 10, 0, 10));

        this.getStyleClass().add("settingsPaneTab");

        checkIconPane.setMinSize(30, 35);
        checkIconPane.setPrefSize(30, 35);
        checkIconPane.setMaxSize(30, 35);
        checkIconPane.setPadding(new Insets(0, 5, 0, 0));
        checkIconPane.getChildren().add(checkIcon);
        checkIconPane.setOnMouseClicked(e -> selectSubtitles());

        checkIcon.setMinSize(14, 11);
        checkIcon.setPrefSize(14, 11);
        checkIcon.setMaxSize(14, 11);
        checkIcon.setShape(checkSVG);
        checkIcon.getStyleClass().add("settingsPaneIcon");
        checkIcon.setVisible(false);

        valueLabel.setFont(new Font(15));
        valueLabel.setMinHeight(35);
        valueLabel.setPrefHeight(35);
        valueLabel.setMaxHeight(35);
        valueLabel.setText(value);
        valueLabel.getStyleClass().add("settingsPaneText");
        valueLabel.setOnMouseClicked(e -> selectSubtitles());
        if(removable){
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
            removeButton.setOnAction(e -> removeItem());

            removeIcon.setMinSize(16, 16);
            removeIcon.setPrefSize(16, 16);
            removeIcon.setMaxSize(16, 16);
            removeIcon.setShape(removeSVG);
            removeIcon.getStyleClass().add("settingsPaneIcon");
            removeIcon.setMouseTransparent(true);

            this.getChildren().addAll(checkIconPane, valueLabel, removePane);
        }
        else {
            valueLabel.setMinWidth(185);
            valueLabel.setPrefWidth(185);
            valueLabel.setMaxWidth(185);

            this.getChildren().addAll(checkIconPane, valueLabel);
        }


        double height = captionsHome.scrollPane.getHeight();
        captionsHome.scrollPane.setPrefHeight(height + 35);
        captionsHome.scrollPane.setMaxHeight(height + 35);

        captionsHome.captionsWrapper.setPrefHeight(height + 32);
        captionsHome.captionsWrapper.setMaxHeight(height + 32);

        if(captionsController.captionsState == CaptionsState.HOME_OPEN || captionsController.captionsState == CaptionsState.CLOSED){
            captionsController.clip.setHeight(height + 35);
        }

        ActiveItem activeItem = captionsController.menuController.activeItem;
        if(activeItem != null){
            this.menuItem = activeItem.activeItemContextMenu.createSubtitleItem(this);
        }

    }

    public void selectSubtitles(){
        if(selected) return;

        for(CaptionsTab captionsTab : captionsHome.captionsTabs){
            captionsTab.checkIcon.setVisible(false);
            captionsTab.selected = false;
        }

        checkIcon.setVisible(true);
        selected = true;
        captionsController.loadCaptions(this.captionFile);
    }

    public void removeItem(){

        if(selected){
            captionsController.removeCaptions();
        }

        captionsHome.captionsTabs.remove(this);
        ActiveItem activeItem = captionsController.menuController.activeItem;
        if(activeItem != null){
            activeItem.activeItemContextMenu.subtitles.getItems().remove(menuItem);
        }
        boolean removed = captionsHome.captionsWrapper.getChildren().remove(this);
        if(removed){
            double height = captionsHome.scrollPane.getHeight();
            captionsHome.scrollPane.setPrefHeight(height - 35);
            captionsHome.scrollPane.setMaxHeight(height - 35);

            captionsHome.captionsWrapper.setPrefHeight(height - 38);
            captionsHome.captionsWrapper.setMaxHeight(height - 38);

            captionsController.clip.setHeight(height - 35);
        }
    }
}
