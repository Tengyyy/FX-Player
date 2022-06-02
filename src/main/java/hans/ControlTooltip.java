package hans;


import javafx.animation.PauseTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

public class ControlTooltip extends Tooltip {

    String tooltipText;
    Button tooltipParent;

    double tooltipMiddle;
    double tooltipHeight;

    double nodeMiddleX;
    double nodeMiddleY;

    boolean isMenu;

    VBox controlBar;

    int delay;

    BooleanProperty mouseHover = new SimpleBooleanProperty(false); // if true the user has been hovering tooltip parent button for longer than the delay time
    PauseTransition countdown;

    ControlTooltip(String tooltipText, Button tooltipParent, VBox controlBar, int delay, boolean isMenu) {

        this.tooltipText = tooltipText;
        this.tooltipParent = tooltipParent;
        this.controlBar = controlBar;
        this.delay = delay;
        this.isMenu = isMenu;
        this.getStyleClass().add("tooltip");

        this.setText(tooltipText);

        this.show(tooltipParent, 0, 0);
        tooltipMiddle = (this.getWidth() - 18) / 2; // 18 is the approximate width of the tooltip shadow
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

        if(!isMenu) this.show(tooltipParent, bounds.getMinX() + nodeMiddleX - tooltipMiddle, bounds.getMinY() - tooltipHeight - translation);
        else this.show(tooltipParent, bounds.getMinX() + nodeMiddleX - tooltipMiddle, bounds.getMinY() + tooltipHeight + translation);

    }


    public void updateText(String newText){

        if(this.getText() == newText) return;

        this.setText(newText);
        if(this.isShowing()){
            this.hide();
            this.show(tooltipParent, 0, 0);
            tooltipMiddle = (this.getWidth() - 18) / 2;
            this.hide();
            this.showTooltip();
        }
        else {
            this.show(tooltipParent, 0, 0);
            tooltipMiddle = (this.getWidth() - 18) / 2;
            this.hide();
        }
    }

}
