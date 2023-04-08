package hans.Menu;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class RecentMediaPage {

    MenuController menuController;

    VBox recentMediaWrapper = new VBox();
    VBox recentMediaBar = new VBox();
    ScrollPane recentMediaScroll = new ScrollPane();
    VBox recentMediaContent = new VBox();

    public Label recentMediaTitle = new Label("Recent Media");

    RecentMediaPage(MenuController menuController){

        this.menuController = menuController;

        recentMediaWrapper.setBackground(Background.EMPTY);

        recentMediaBar.setFillWidth(true);

        recentMediaScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        recentMediaScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        recentMediaScroll.getStyleClass().add("menuScroll");
        recentMediaScroll.setFitToWidth(true);
        recentMediaScroll.setFitToHeight(true);
        recentMediaScroll.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        recentMediaScroll.setBackground(Background.EMPTY);

        recentMediaContent.setBackground(Background.EMPTY);
        recentMediaContent.setPadding(new Insets(0, 50,20, 50));

        VBox.setVgrow(recentMediaScroll, Priority.ALWAYS);


        recentMediaTitle.getStyleClass().add("menuTitle");

        VBox.setMargin(recentMediaTitle, new Insets(20, 40, 5, 50));
        recentMediaBar.setPadding(new Insets(35, 0, 0, 0));



        recentMediaBar.setAlignment(Pos.CENTER_LEFT);
        recentMediaBar.getChildren().addAll(recentMediaTitle);


        recentMediaScroll.setContent(recentMediaContent);
        recentMediaScroll.addEventFilter(KeyEvent.ANY, e -> {
            if(e.getCode() == KeyCode.UP || e.getCode() == KeyCode.DOWN){
                e.consume();
            }
        });


        recentMediaWrapper.getChildren().addAll(recentMediaBar, recentMediaScroll);
        menuController.recentMediaContainer.getChildren().add(recentMediaWrapper);

    }

    public void openRecentMediaPage(){
        menuController.recentMediaContainer.setVisible(true);
    }

    public void closeRecentMediaPage(){
        menuController.recentMediaContainer.setVisible(false);
    }

    public void enter(){

        if(menuController.menuInTransition) return;

        menuController.menuBar.setActiveButton(menuController.menuBar.historyButton);

        if(menuController.menuState == MenuState.CLOSED){
            if(!menuController.extended.get()) menuController.setMenuExtended(MenuState.RECENT_MEDIA_OPEN);
            menuController.openMenu(MenuState.RECENT_MEDIA_OPEN);
        }
        else {
            if(!menuController.extended.get()) menuController.extendMenu(MenuState.RECENT_MEDIA_OPEN);
            else menuController.animateStateSwitch(MenuState.RECENT_MEDIA_OPEN);
        }
    }
}
