package tengy.Menu;

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

public class PlaylistsPage {

    MenuController menuController;

    VBox playlistsWrapper = new VBox();
    VBox playlistsBar = new VBox();
    ScrollPane playlistsScroll = new ScrollPane();
    VBox playlistsContent = new VBox();

    public Label playlistsTitle = new Label("Playlists");

    IntegerProperty focus = new SimpleIntegerProperty(-1);
    List<Node> focusNodes = new ArrayList<>();

    PlaylistsPage(MenuController menuController){

        this.menuController = menuController;

        playlistsWrapper.setBackground(Background.EMPTY);

        playlistsBar.setFillWidth(true);

        playlistsScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        playlistsScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        playlistsScroll.getStyleClass().add("menuScroll");
        playlistsScroll.setFitToWidth(true);
        playlistsScroll.setFitToHeight(true);
        playlistsScroll.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        playlistsScroll.setBackground(Background.EMPTY);


        playlistsContent.setBackground(Background.EMPTY);
        playlistsContent.setPadding(new Insets(0, 50,20, 50));

        VBox.setVgrow(playlistsScroll, Priority.ALWAYS);


        playlistsTitle.getStyleClass().add("menuTitle");

        VBox.setMargin(playlistsTitle, new Insets(20, 40, 5, 50));
        playlistsBar.setPadding(new Insets(35, 0, 0, 0));



        playlistsBar.setAlignment(Pos.CENTER_LEFT);
        playlistsBar.getChildren().addAll(playlistsTitle);


        playlistsScroll.setContent(playlistsContent);
        playlistsScroll.addEventFilter(KeyEvent.ANY, e -> {
            if(e.getCode() == KeyCode.UP || e.getCode() == KeyCode.DOWN){
                e.consume();
            }
        });


        playlistsWrapper.getChildren().addAll(playlistsBar, playlistsScroll);
        menuController.playlistsContainer.getChildren().add(playlistsWrapper);
    }

    public void openPlaylistsPage(){
        menuController.playlistsContainer.setVisible(true);
    }

    public void closePlaylistsPage(){
        menuController.playlistsContainer.setVisible(false);
    }

    public void enter(){

        if(menuController.menuInTransition) return;

        menuController.menuBar.setActiveButton(menuController.menuBar.playlistsButton);

        if(menuController.menuState == MenuState.CLOSED){
            if(!menuController.extended.get()) menuController.setMenuExtended(MenuState.PLAYLISTS_OPEN);
            menuController.openMenu(MenuState.PLAYLISTS_OPEN);
        }
        else {
            if(!menuController.extended.get()) menuController.extendMenu(MenuState.PLAYLISTS_OPEN);
            else menuController.animateStateSwitch(MenuState.PLAYLISTS_OPEN);
        }
    }

    public void focusForward() {
    }

    public void focusBackward() {
    }
}
