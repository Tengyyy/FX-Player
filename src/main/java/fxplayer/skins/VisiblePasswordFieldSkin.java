package fxplayer.skins;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.skin.TextFieldSkin;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import fxplayer.SVG;

public class VisiblePasswordFieldSkin extends TextFieldSkin {

    public static final char BULLET = '\u2022';

    private final Button actionButton = new Button();

    private final StackPane focusLine = new StackPane();

    private final Background focusedBackground = new Background(new BackgroundFill(Color.RED, new CornerRadii(0, 0, 5, 5, false), new Insets(0, 0, 0, -5)));
    private final Background defaultBackground = new Background(new BackgroundFill(Color.rgb(200,200,200), new CornerRadii(0, 0, 5, 5, false), new Insets(0, 0, 0, -5)));

    private boolean mask = true;

    public VisiblePasswordFieldSkin(PasswordField textField) {

        super(textField);

        actionButton.getStyleClass().add("textFieldButton");
        actionButton.setMinSize(30,22);
        actionButton.setPrefSize(30,22);
        actionButton.setMaxSize(30,22);
        actionButton.setFocusTraversable(false);
        actionButton.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        actionButton.setTranslateY(-2);
        actionButton.setTranslateX(-5);

        getChildren().addAll(focusLine, actionButton);
        focusLine.toFront();
        actionButton.setCursor(Cursor.HAND);
        actionButton.toFront();

        focusLine.setMinHeight(1);
        focusLine.setMaxHeight(1);
        focusLine.setMouseTransparent(true);
        focusLine.setBackground(defaultBackground);

        SVGPath onPath = new SVGPath();
        SVGPath offPath = new SVGPath();

        onPath.setContent(SVG.EYE.getContent());
        offPath.setContent(SVG.EYE_OFF.getContent());

        Region actionIcon = new Region();
        actionIcon.setShape(onPath);
        actionIcon.getStyleClass().add("graphic");
        actionIcon.setMinSize(16, 13);
        actionIcon.setPrefSize(16, 13);
        actionIcon.setMaxSize(16, 13);
        actionButton.setGraphic(actionIcon);

        actionButton.setVisible(false);

        actionButton.setOnAction(event -> {

            if(mask) {
                actionIcon.setShape(offPath);
                actionIcon.setMinSize(16, 15.5);
                actionIcon.setPrefSize(16, 15.5);
                actionIcon.setMaxSize(16, 15.5);
                mask = false;
            } else {
                actionIcon.setMinSize(16, 13);
                actionIcon.setPrefSize(20, 13);
                actionIcon.setMaxSize(16, 13);
                actionIcon.setShape(onPath);
                mask = true;
            }

            textField.setText(textField.getText());
            textField.end();

        });

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

    @Override
    protected String maskText(String txt) {
        if (getSkinnable() instanceof PasswordField && mask) {
            int n = txt.length();
            return String.valueOf(BULLET).repeat(n);
        } else {

            return txt;
        }
    }
}
