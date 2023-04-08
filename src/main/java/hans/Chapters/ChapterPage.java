package hans.Chapters;

import hans.Menu.MenuController;
import hans.Menu.MenuState;
import hans.Menu.Queue.QueueItem;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class ChapterPage {

    MenuController menuController;
    ChapterController chapterController;

    StackPane closeButtonBar = new StackPane();
    StackPane closeButtonPane = new StackPane();

    public VBox content = new VBox();

    Label titleLabel = new Label("Chapters");

    public VBox chapterBox = new VBox();

    ChapterPage(MenuController menuController, ChapterController chapterController){
        this.menuController = menuController;
        this.chapterController = chapterController;

        closeButtonPane.setPrefSize(50, 50);
        closeButtonPane.setMaxSize(50, 50);
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

    public void openChaptersPage(){
        menuController.chapterScroll.setVisible(true);
    }

    public void closeChaptersPage(){
        menuController.chapterScroll.setVisible(false);
    }

    public void extend(){

    }

    public void shrink(){

    }

    public void enter(){

        if(menuController.menuInTransition) return;

        menuController.menuBar.setActiveButton(null);

        if(menuController.menuState == MenuState.CLOSED) menuController.openMenu(MenuState.CHAPTERS_OPEN);
        else menuController.animateStateSwitch(MenuState.CHAPTERS_OPEN);
    }
}
