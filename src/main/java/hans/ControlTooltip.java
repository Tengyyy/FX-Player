package hans;


import javafx.animation.PauseTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class ControlTooltip extends Tooltip {

    String tooltipText;
    Button tooltipParent;

    double tooltipMiddle;
    double tooltipHeight;

    double nodeMiddleX;
    double nodeMiddleY;

    boolean menuTooltip;

    VBox controlBar;

    int delay;

    BooleanProperty mouseHover = new SimpleBooleanProperty(false); // if true the user has been hovering tooltip parent button for longer than the delay time
    PauseTransition countdown;

    ControlTooltip(String tooltipText, Button tooltipParent, boolean menuTooltip, VBox controlBar, int delay) {

        this.tooltipText = tooltipText;
        this.tooltipParent = tooltipParent;
        this.menuTooltip = menuTooltip;
        this.controlBar = controlBar;
        this.delay = delay;

        this.setText(tooltipText);

        this.show(tooltipParent, 0, 0);
        tooltipMiddle = (this.getWidth() - 18) / 2;
        tooltipHeight = this.getHeight();
        this.hide();

        mouseHover.addListener((obs, wasHover, isHover) -> {
            if(isHover){
                showTooltip();
            }
        });

        countdown = new PauseTransition(Duration.millis(delay));
        countdown.setOnFinished((e) -> mouseHover.set(true));

        tooltipParent.setOnMouseEntered((e) -> {
            countdown.playFromStart();
        });

        tooltipParent.setOnMouseExited((e) -> {
            this.hide();
            mouseHover.set(false);
            countdown.stop();
        });

    }


    public void showTooltip() {
        Bounds bounds = tooltipParent.localToScreen(tooltipParent.getBoundsInLocal());
        nodeMiddleX = tooltipParent.getWidth() / 2;
        nodeMiddleY = tooltipParent.getHeight() / 2;

        double translation = this.delay == 0 ? controlBar.getTranslateY() : 0;

        if (!menuTooltip)
            this.show(tooltipParent, bounds.getMinX() + nodeMiddleX - tooltipMiddle, bounds.getMinY() - tooltipHeight - translation);
        else
            this.show(tooltipParent, bounds.getMaxX() + 10, bounds.getMinY() + nodeMiddleY - ((tooltipHeight - 18) / 2));
    }
}
