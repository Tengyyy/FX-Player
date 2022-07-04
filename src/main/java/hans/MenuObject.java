package hans;

import javafx.scene.Scene;
import javafx.scene.control.Button;

import java.io.File;

public interface MenuObject {

    MediaItem getMediaItem();

    void playNext();

    Button getOptionsButton();

    void showMetadata();

    void addSubtitles(File file);

    MenuController getMenuController();
}
