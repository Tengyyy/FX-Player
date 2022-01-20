package hans;

import java.util.ArrayList;

import javafx.scene.Node;

public class FocusTraversalEngine {


    ArrayList<Node> focusableNodes; // arraylist to keep track of all nodes that can gain focus

    FocusTraversalEngine() {


        focusableNodes = new ArrayList<Node>();

    }

}
