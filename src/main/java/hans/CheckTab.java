package hans;

import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;

public class CheckTab extends HBox {

    StackPane checkIconPane = new StackPane();
    Region checkIcon = new Region();
    SVGPath checkSVG = new SVGPath();

    Label valueLabel = new Label();


    CheckTab(boolean selected, String value){

        checkSVG.setContent(App.svgMap.get(SVG.CHECK));

        this.setMinSize(190, 35);
        this.setPrefSize(190, 35);
        this.setMaxSize(190, 35);

        this.setPadding(new Insets(0, 10, 0, 10));

        this.getStyleClass().add("settingsPaneTab");

        this.setCursor(Cursor.HAND);


        checkIconPane.setMinSize(30, 35);
        checkIconPane.setPrefSize(30, 35);
        checkIconPane.setMaxSize(30, 35);
        checkIconPane.setPadding(new Insets(0, 5, 0, 0));
        checkIconPane.getChildren().add(checkIcon);

        checkIcon.setMinSize(14, 11);
        checkIcon.setPrefSize(14, 11);
        checkIcon.setMaxSize(14, 11);
        checkIcon.setShape(checkSVG);
        checkIcon.getStyleClass().add("settingsPaneIcon");
        checkIcon.setVisible(selected);

        valueLabel.setFont(new Font(15));
        valueLabel.setPrefHeight(35);
        valueLabel.setPrefWidth(140);
        valueLabel.setText(value);
        valueLabel.getStyleClass().add("settingsPaneText");

        this.getChildren().addAll(checkIconPane, valueLabel);
    }
}
