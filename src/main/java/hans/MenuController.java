package hans;


import java.net.URL;

import java.util.ArrayList;
import java.util.ResourceBundle;


import com.jfoenix.controls.JFXTabPane;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;


public class MenuController implements Initializable {

    @FXML
    JFXTabPane menuTabPane;

    ArrayList<Tab> tabs = new ArrayList<Tab>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        menuTabPane.tabMinWidthProperty().bind(menuTabPane.widthProperty().divide(menuTabPane.getTabs().size()).subtract(20));
    }


}


