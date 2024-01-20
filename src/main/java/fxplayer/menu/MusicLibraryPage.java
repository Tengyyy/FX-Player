package fxplayer.menu;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class MusicLibraryPage {

    MenuController menuController;

    VBox musicLibraryWrapper = new VBox();
    VBox musicLibraryBar = new VBox();
    ScrollPane musicLibraryScroll = new ScrollPane();
    VBox musicLibraryContent = new VBox();

    public Label musicLibraryTitle = new Label("Music");

    IntegerProperty focus = new SimpleIntegerProperty(-1);
    List<Node> focusNodes = new ArrayList<>();

    MusicLibraryPage(MenuController menuController){

        this.menuController = menuController;

        musicLibraryWrapper.setBackground(Background.EMPTY);

        musicLibraryBar.setFillWidth(true);

        musicLibraryScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        musicLibraryScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        musicLibraryScroll.getStyleClass().add("menuScroll");
        musicLibraryScroll.setFitToWidth(true);
        musicLibraryScroll.setFitToHeight(true);
        musicLibraryScroll.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        musicLibraryScroll.setBackground(Background.EMPTY);


        musicLibraryContent.setBackground(Background.EMPTY);
        musicLibraryContent.setPadding(new Insets(0, 50,20, 50));

        VBox.setVgrow(musicLibraryScroll, Priority.ALWAYS);


        musicLibraryTitle.getStyleClass().add("menuTitle");

        VBox.setMargin(musicLibraryTitle, new Insets(20, 40, 5, 50));
        musicLibraryBar.setPadding(new Insets(35, 0, 0, 0));



        musicLibraryBar.setAlignment(Pos.CENTER_LEFT);
        musicLibraryBar.getChildren().addAll(musicLibraryTitle);


        musicLibraryScroll.setContent(musicLibraryContent);
        musicLibraryScroll.addEventFilter(KeyEvent.ANY, e -> {
            if(e.getCode() == KeyCode.UP || e.getCode() == KeyCode.DOWN){
                e.consume();
            }
        });


        musicLibraryWrapper.getChildren().addAll(musicLibraryBar, musicLibraryScroll);
        menuController.musicLibraryContainer.getChildren().add(musicLibraryWrapper);
    }

    public void openMusicLibraryPage(){
        menuController.musicLibraryContainer.setVisible(true);
    }

    public void closeMusicLibraryPage(){
        menuController.musicLibraryContainer.setVisible(false);
    }

    public void enter(){

        if(menuController.menuInTransition) return;

        menuController.menuBar.setActiveButton(menuController.menuBar.musicLibraryButton);

        if(menuController.menuState == MenuState.CLOSED){
            if(!menuController.extended.get()) menuController.setMenuExtended(MenuState.MUSIC_LIBRARY_OPEN);
            menuController.openMenu(MenuState.MUSIC_LIBRARY_OPEN);
        }
        else {
            if(!menuController.extended.get()) menuController.extendMenu(MenuState.MUSIC_LIBRARY_OPEN);
            else menuController.animateStateSwitch(MenuState.MUSIC_LIBRARY_OPEN);
        }
    }

    public void focusForward() {
    }

    public void focusBackward() {
    }
}
