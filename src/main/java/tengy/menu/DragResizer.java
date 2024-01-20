package tengy.menu;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

public class DragResizer {


    StackPane menu;
    StackPane dragPane;
    MenuController menuController;


    public boolean dragging;
    private double correction = 0;

    public DragResizer(MenuController menuController) {
        this.menuController = menuController;
        this.menu = menuController.menu;
        dragPane = menuController.dragPane;

        dragPane.setOnMousePressed(this::mousePressed);
        dragPane.setOnMouseDragged(this::mouseDragged);
        dragPane.setOnMouseReleased(this::mouseReleased);
    }

    protected void mouseReleased(MouseEvent event) {
        dragging = false;
    }


    protected void mouseDragged(MouseEvent event) {
        if(!dragging) {
            return;
        }

        double requestedWidth = event.getSceneX() + correction;
        double newWidth = Math.max(
                Math.min(
                        (menuController.mainController.videoImageViewWrapper.getWidth() + 30)/2
                        , requestedWidth)
                , menuController.MIN_WIDTH);

        menu.setMaxWidth(newWidth);
        menuController.shrinkedWidth = newWidth;
    }

    protected void mousePressed(MouseEvent event) {

        dragging = true;
        correction = menu.getBoundsInLocal().getMaxX() - event.getSceneX();


    }

}
