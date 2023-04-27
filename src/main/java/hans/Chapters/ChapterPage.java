package hans.Chapters;

import hans.Menu.MenuController;
import hans.Menu.MenuState;
import hans.Menu.Queue.QueueItem;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class ChapterPage {

    MenuController menuController;
    ChapterController chapterController;

    VBox chapterWrapper = new VBox();
    StackPane titlePane = new StackPane();
    Label title = new Label("Chapters");

    ScrollPane chapterScroll = new ScrollPane();

    public VBox chapterBox = new VBox();

    public List<ChapterItem> chapterItems = new ArrayList<>();

    ChapterPage(MenuController menuController, ChapterController chapterController){
        this.menuController = menuController;
        this.chapterController = chapterController;

        title.getStyleClass().add("menuTitle");
        StackPane.setAlignment(title, Pos.CENTER_LEFT);

        titlePane.getChildren().add(title);
        titlePane.setPadding(new Insets(20, 30, 100, 30));

        chapterScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        chapterScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        chapterScroll.getStyleClass().add("menuScroll");
        chapterScroll.setFitToWidth(true);
        chapterScroll.setFitToHeight(true);
        chapterScroll.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        chapterScroll.setBackground(Background.EMPTY);
        chapterScroll.setContent(chapterBox);


        chapterBox.setAlignment(Pos.TOP_CENTER);
        chapterBox.setFillWidth(true);
        chapterBox.setBackground(Background.EMPTY);
        chapterBox.setPadding(new Insets(0, 0, 20, 0));

        chapterWrapper.getChildren().addAll(titlePane, chapterScroll);
        menuController.chapterContainer.getChildren().add(chapterWrapper);
    }

    public void openChaptersPage(){
        menuController.chapterContainer.setVisible(true);
    }

    public void closeChaptersPage(){
        menuController.chapterContainer.setVisible(false);
    }



    public void extend(){

        titlePane.setPadding(new Insets(55, 50, 20, 50));

        chapterBox.setPadding(new Insets(0, 50,20, 50));

        ChapterItem.height = 100;

        for(ChapterItem chapterItem : this.chapterItems){
            chapterItem.updateHeight();
            chapterItem.applyRoundStyling();
        }

    }

    public void shrink(){

        titlePane.setPadding(new Insets(20, 30, 20, 30));

        chapterBox.setPadding(new Insets(0, 0,20, 0));

        ChapterItem.height = 90;

        for(ChapterItem chapterItem : this.chapterItems){
            chapterItem.updateHeight();
            chapterItem.removeRoundStyling();
        }
    }

    public void enter(){

        if(menuController.menuInTransition) return;

        menuController.menuBar.setActiveButton(null);

        if(menuController.menuState == MenuState.CLOSED) menuController.openMenu(MenuState.CHAPTERS_OPEN);
        else menuController.animateStateSwitch(MenuState.CHAPTERS_OPEN);
    }

    public void add(ChapterItem chapterItem){
        chapterItems.add(chapterItem);
        chapterBox.getChildren().add(chapterItem);
    }

    public void clear(){
        chapterItems.clear();
        chapterBox.getChildren().clear();
    }
}
