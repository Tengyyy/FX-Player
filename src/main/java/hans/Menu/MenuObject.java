package hans.Menu;

import hans.MediaItems.MediaItem;
import javafx.beans.property.BooleanProperty;
import javafx.scene.control.Button;

public interface MenuObject {

    MediaItem getMediaItem();

    void playNext();

    Button getOptionsButton();

    void showMetadata();

    void showTechnicalDetails();

    MenuController getMenuController();

    String getTitle();

    boolean getHover();

    void update();

    BooleanProperty getMediaItemGenerated();

}
