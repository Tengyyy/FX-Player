package hans.Menu;

import hans.MediaItems.MediaItem;
import hans.Menu.MenuController;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

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

    void update();

}
