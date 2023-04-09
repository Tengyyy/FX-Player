package hans.Menu.Settings;

import hans.App;
import hans.SVG;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.PasswordField;
import javafx.scene.control.skin.TextFieldSkin;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

public class VisiblePasswordFieldSkin extends TextFieldSkin {

    public static final char BULLET = '\u2022';

    private final Button actionButton = new Button("View");
    private final SVGPath actionIcon = new SVGPath();

    private boolean mask = true;

    public VisiblePasswordFieldSkin(PasswordField textField) {

        super(textField);

        actionButton.setId("actionButton");
        actionButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        actionButton.setPrefSize(30,30);
        actionButton.setFocusTraversable(false);
        actionButton.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));

        getChildren().add(actionButton);
        actionButton.setCursor(Cursor.HAND);
        actionButton.toFront();

        actionIcon.setContent(App.svgMap.get(SVG.EYE));
        actionIcon.setFill(Color.rgb(200, 200, 200));
        actionButton.setGraphic(actionIcon);

        actionButton.setVisible(false);

        actionButton.setOnMouseClicked(event -> {

            if(mask) {
                actionIcon.setContent(App.svgMap.get(SVG.EYE_OFF));
                mask = false;
            } else {
                actionIcon.setContent(App.svgMap.get(SVG.EYE));
                mask = true;
            }
            textField.setText(textField.getText());

            textField.end();

        });

        textField.textProperty().addListener((observable, oldValue, newValue) -> actionButton.setVisible(!newValue.isEmpty()));

    }

    @Override
    protected void layoutChildren(double x, double y, double w, double h) {
        super.layoutChildren(x, y, w, h);

        layoutInArea(actionButton, x, y, w, h,0, HPos.RIGHT, VPos.CENTER);
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
