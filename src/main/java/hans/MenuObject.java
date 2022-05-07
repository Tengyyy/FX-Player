package hans;

import javafx.scene.Scene;
import javafx.scene.control.Button;

public interface MenuObject {

    MediaItem getMediaItem();

    void playNext();

    Button getOptionsButton();

    void showMetadata();

    MenuController getMenuController();
}
