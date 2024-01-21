package fxplayer.controls;

public class TransparentButton extends ButtonBase {

    public static TransparentButton createButton(double width, double height) {
        return new TransparentButton(width, height);
    }

    private TransparentButton(double width, double height) {
        super(width, height, null);

        button.getStyleClass().addAll("transparentButton");
    }
}
