package fxplayer.skins;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.TextField;
import javafx.scene.control.skin.TextFieldSkin;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import fxplayer.SVG;

public class HighlightedTextFieldSkin extends TextFieldSkin {

    private final StackPane focusLine = new StackPane();

    private final Background focusedBackground = new Background(new BackgroundFill(Color.RED, new CornerRadii(0, 0, 5, 5, false), new Insets(0, 0, 0, -5)));
    private final Background defaultBackground = new Background(new BackgroundFill(Color.rgb(200,200,200), new CornerRadii(0, 0, 5, 5, false), new Insets(0, 0, 0, -5)));

    public HighlightedTextFieldSkin(TextField textField) {

        super(textField);

        getChildren().add(focusLine);
        focusLine.toFront();

        focusLine.setMinHeight(1);
        focusLine.setMaxHeight(1);
        focusLine.setMouseTransparent(true);
        focusLine.setBackground(defaultBackground);

        SVGPath actionSVG = new SVGPath();
        actionSVG.setContent(SVG.CLOSE.getContent());

        Region actionIcon = new Region();
        actionIcon.setShape(actionSVG);
        actionIcon.getStyleClass().add("graphic");
        actionIcon.setMinSize(15, 15);
        actionIcon.setPrefSize(15, 15);
        actionIcon.setMaxSize(15, 15);

        textField.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focusLine.setMinHeight(2);
                focusLine.setMaxHeight(2);
                focusLine.setBackground(focusedBackground);
            }
            else {
                focusLine.setMinHeight(1);
                focusLine.setMaxHeight(1);
                focusLine.setBackground(defaultBackground);
            }
        });
    }

    @Override
    protected void layoutChildren(double x, double y, double w, double h) {
        super.layoutChildren(x, y, w, h);
        layoutInArea(focusLine, x, y, w, h,0, HPos.CENTER, VPos.BOTTOM);
    }
}
