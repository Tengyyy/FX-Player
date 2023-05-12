package hans;

import javafx.scene.Node;
import javafx.scene.layout.Region;

public class ClippedNode extends Region {

    ClippedNode(Node content) {

        getChildren().setAll(content);
    }

}