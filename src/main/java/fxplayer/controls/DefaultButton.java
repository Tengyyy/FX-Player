package fxplayer.controls;

import javafx.scene.text.TextAlignment;

public class DefaultButton extends ButtonBase {

    public enum Type {
        MAIN,
        SECONDARY
    }

    public static DefaultButton createButton(Type buttonType, double width, double height, String text) {
        return new DefaultButton(buttonType, width, height, text);
    }

    private DefaultButton(Type buttonType, double width, double height, String text) {
        super(width, height, text);

        if (buttonType == Type.MAIN)
            button.getStyleClass().add("mainButton");
        else
            button.getStyleClass().add("secondaryButton");

        button.setTextAlignment(TextAlignment.CENTER);
    }


}
