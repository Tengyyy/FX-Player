package hans.Chapters;

import hans.AnimationsClass;
import hans.App;
import hans.Captions.CaptionsState;
import hans.Menu.MenuController;
import hans.Menu.MenuState;
import hans.SVG;
import hans.Settings.SettingsState;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

public class ChapterPage {

    MenuController menuController;
    ChapterController chapterController;

    Button closeButton = new Button();
    Region closeIcon = new Region();

    SVGPath closeIconSVG = new SVGPath();

    StackPane closeButtonBar = new StackPane();
    StackPane closeButtonPane = new StackPane();

    public VBox content = new VBox();

    Label titleLabel = new Label("Chapters");

    public VBox chapterBox = new VBox();

    ChapterPage(MenuController menuController, ChapterController chapterController){
        this.menuController = menuController;
        this.chapterController = chapterController;

        closeIconSVG.setContent(App.svgMap.get(SVG.CLOSE));
        closeIcon.setShape(closeIconSVG);
        closeIcon.setPrefSize(20, 20);
        closeIcon.setMaxSize(20, 20);
        closeIcon.setId("closeIcon");
        closeIcon.setMouseTransparent(true);

        closeButton.setPrefSize(40, 40);
        closeButton.setMaxSize(40, 40);
        closeButton.setCursor(Cursor.HAND);
        closeButton.setBackground(Background.EMPTY);

        closeButton.setOnAction(e -> {
            if(menuController.extended){
                if(menuController.captionsController.captionsState != CaptionsState.CLOSED) menuController.captionsController.closeCaptions();
                if(menuController.settingsController.settingsState != SettingsState.CLOSED) menuController.settingsController.closeSettings();
            }

            menuController.closeMenu();
        });

        closeButton.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> AnimationsClass.animateBackgroundColor(closeIcon, (Color) closeIcon.getBackground().getFills().get(0).getFill(), Color.rgb(255, 255, 255), 200));

        closeButton.addEventHandler(MouseEvent.MOUSE_EXITED, e -> AnimationsClass.animateBackgroundColor(closeIcon, (Color) closeIcon.getBackground().getFills().get(0).getFill(), Color.rgb(200, 200, 200), 200));

        closeButtonPane.setPrefSize(50, 50);
        closeButtonPane.setMaxSize(50, 50);
        closeButtonPane.getChildren().addAll(closeButton, closeIcon);
        StackPane.setAlignment(closeButtonPane, Pos.CENTER_RIGHT);

        titleLabel.getStyleClass().add("chapterTitle");
        titleLabel.setPadding(new Insets(0, 0, 0, 20));
        StackPane.setAlignment(titleLabel, Pos.CENTER_LEFT);

        closeButtonBar.setPrefHeight(60);
        closeButtonBar.setMinHeight(60);
        closeButtonBar.getChildren().addAll(titleLabel, closeButtonPane);

        chapterBox.setAlignment(Pos.TOP_CENTER);
        chapterBox.setFillWidth(true);

        content.setAlignment(Pos.TOP_CENTER);
        content.getChildren().addAll(closeButtonBar, chapterBox);
        content.setBackground(Background.EMPTY);
        content.setPadding(new Insets(0, 0, 20, 0));
        menuController.chapterScroll.setContent(content);

    }

    public void enterChaptersPage(){

        menuController.chapterScroll.setVisible(true);
        menuController.queueContainer.setVisible(false);

        if(menuController.menuState == MenuState.CLOSED) menuController.openMenu();

        menuController.menuState = MenuState.CHAPTERS_OPEN;
    }
}
