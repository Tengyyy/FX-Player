package hans.Menu;

import hans.MediaItems.MediaItem;
import hans.Menu.MenuController;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

import java.io.File;

public interface MenuObject {

    MediaItem getMediaItem();

    void playNext();

    Button getOptionsButton();

    void showMetadata();

    void showTechnicalDetails();

    void addSubtitles(File file);

    MenuController getMenuController();

    String getTitle();

    boolean getHover();

}
