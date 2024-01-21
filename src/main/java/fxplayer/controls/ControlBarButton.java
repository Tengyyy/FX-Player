package fxplayer.controls;

public class ControlBarButton extends ButtonBase {

    public static ControlBarButton createButton(double width, double height) {
        return new ControlBarButton(width, height);
    }

    private ControlBarButton(double width, double height) {
        super(width, height, null);
    }
}
