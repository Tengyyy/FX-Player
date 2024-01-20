package fxplayer.skins;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.skin.TextFieldSkin;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import fxplayer.SVG;

public class ClearableTextFieldSkin extends TextFieldSkin {

    private final Button actionButton = new Button();
    private final StackPane focusLine = new StackPane();

    private final Background focusedBackground = new Background(new BackgroundFill(Color.RED, new CornerRadii(0, 0, 5, 5, false), new Insets(0, 0, 0, -5)));
    private final Background defaultBackground = new Background(new BackgroundFill(Color.rgb(200,200,200), new CornerRadii(0, 0, 5, 5, false), new Insets(0, 0, 0, -5)));

    public ClearableTextFieldSkin(TextField textField) {

        super(textField);

        actionButton.getStyleClass().add("textFieldButton");
        actionButton.setMinSize(30,22);
        actionButton.setPrefSize(30,22);
        actionButton.setMaxSize(30,22);
        actionButton.setTranslateY(-2);
        actionButton.setTranslateX(-5);

        getChildren().addAll(focusLine, actionButton);
        focusLine.toFront();
        actionButton.toFront();

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
        actionButton.setGraphic(actionIcon);
        actionButton.setFocusTraversable(false);
        actionButton.setVisible(false);

        actionButton.setOnAction(event -> textField.clear());

        textField.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focusLine.setMinHeight(2);
                focusLine.setMaxHeight(2);
                focusLine.setBackground(focusedBackground);
                if(!textField.getText().isEmpty()) actionButton.setVisible(true);
            }
            else {
                focusLine.setMinHeight(1);
                focusLine.setMaxHeight(1);
                focusLine.setBackground(defaultBackground);
                actionButton.setVisible(false);
            }
        });

        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.isEmpty()) actionButton.setVisible(false);
            else if(textField.isFocused()) actionButton.setVisible(true);
        });

    }

    @Override
    protected void layoutChildren(double x, double y, double w, double h) {
        super.layoutChildren(x, y, w - 40, h);
        layoutInArea(actionButton, x, y, w, h,0, HPos.RIGHT, VPos.CENTER);

        layoutInArea(focusLine, x, y, w, h,0, HPos.CENTER, VPos.BOTTOM);
    }
}
