package fxplayer.controls;

public class MenuButton extends ButtonBase {

    public static MenuButton createButton(double width, double height, String text) {
        return new MenuButton(width, height, text);
    }

    private MenuButton(double width, double height, String text) {
        super(width, height, text);
    }
}
