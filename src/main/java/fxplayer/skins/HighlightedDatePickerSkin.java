package fxplayer.skins;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.DatePicker;
import javafx.scene.control.skin.DatePickerSkin;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class HighlightedDatePickerSkin extends DatePickerSkin {

    private final StackPane focusLine = new StackPane();

    private final Background focusedBackground = new Background(new BackgroundFill(Color.RED, new CornerRadii(0, 0, 5, 5, false), new Insets(0, 0, 0, 0)));
    private final Background defaultBackground = new Background(new BackgroundFill(Color.rgb(200,200,200), new CornerRadii(0, 0, 5, 5, false), new Insets(0, 0, 0, 0)));

    public HighlightedDatePickerSkin(DatePicker datePicker) {

        super(datePicker);

        getChildren().addAll(focusLine);
        focusLine.toFront();

        focusLine.setMinHeight(1);
        focusLine.setMaxHeight(1);
        focusLine.setMouseTransparent(true);
        focusLine.setBackground(defaultBackground);

        datePicker.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
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
