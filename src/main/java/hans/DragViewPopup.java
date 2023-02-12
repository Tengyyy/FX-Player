package hans;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.Popup;


public class DragViewPopup extends Popup {

    MainController mainController;

    Label dragLabel = new Label();

    double width = 0;
    double height = 0;

    DragViewPopup(MainController mainController){
        this.mainController = mainController;


        dragLabel.setId("dragLabel");

        this.getContent().add(dragLabel);

        this.getScene().getRoot().setMouseTransparent(true);
    }

    @Override
    public void show(Node node, double x, double y){
        super.show(node, x - width - 30, y - height/2);
    }

    public void setText(String text){
        dragLabel.setText(text);
        if(this.isShowing()) this.hide();
        super.show(mainController.videoImageViewWrapper, 0, 0);
        this.width = this.getWidth();
        this.height = this.getHeight();
        this.hide();
    }

    public void setPosition(double x, double y){ // x and y coords of the cursor, popup will be to the left of the cursor
        this.setX(x - width - 30);
        this.setY(y - height/2);
    }
}
