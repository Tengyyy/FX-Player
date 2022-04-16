package hans;

import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;

public class DragResizer {


    private Region parentNode;
    private static Region dragTarget;
    MenuController menuController;


    public boolean dragging;
    private double correction = 0;

    public DragResizer(MenuController menuController) {
        this.menuController = menuController;
        this.parentNode = menuController.menu;
        this.dragTarget = menuController.dragPane;

        dragTarget.setOnMousePressed(this::mousePressed);
        dragTarget.setOnMouseDragged(this::mouseDragged);
        dragTarget.setOnMouseReleased(this::mouseReleased);
    }

    protected void mouseReleased(MouseEvent event) {
        dragging = false;
        menuController.dragPane.setCursor(Cursor.H_RESIZE);
    }


    protected void mouseDragged(MouseEvent event) {
        if(!dragging) {
            return;
        }

        if(event.getSceneX() + correction >= 350) {
            parentNode.setPrefWidth(event.getSceneX() + correction);
            menuController.prefWidth = Double.max(350, event.getSceneX() + correction);
        }

    }

    protected void mousePressed(MouseEvent event) {

        dragging = true;
        correction = parentNode.getPrefWidth() - event.getSceneX();


    }

}
