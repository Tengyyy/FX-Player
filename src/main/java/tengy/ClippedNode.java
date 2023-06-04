package tengy;

import javafx.scene.Node;
import javafx.scene.layout.Region;

public class ClippedNode extends Region {

    public ClippedNode(Node content) {

        getChildren().setAll(content);
    }

}