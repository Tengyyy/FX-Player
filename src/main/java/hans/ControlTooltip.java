package hans;


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

    ControlTooltip(String tooltipText, Button tooltipParent, boolean menuTooltip, VBox controlBar) {

        this.tooltipText = tooltipText;
        this.tooltipParent = tooltipParent;
        this.menuTooltip = menuTooltip;
        this.controlBar = controlBar;

        this.setText(tooltipText);
        this.setShowDelay(Duration.ZERO);


        this.show(tooltipParent, 0, 0);
        tooltipMiddle = (this.getWidth() - 18) / 2;
        tooltipHeight = this.getHeight();
        this.hide();

        tooltipParent.setOnMouseEntered((e) -> {

            if (tooltipParent.getScene().getWindow().isFocused()) {
                Bounds bounds = tooltipParent.localToScreen(tooltipParent.getBoundsInLocal());
                nodeMiddleX = tooltipParent.getWidth() / 2;
                nodeMiddleY = tooltipParent.getHeight() / 2;


                if (!menuTooltip) {
                    this.show(tooltipParent, bounds.getMinX() + nodeMiddleX - tooltipMiddle, bounds.getMinY() - tooltipHeight - controlBar.getTranslateY());
                } else
                    this.show(tooltipParent, bounds.getMaxX() + 10, bounds.getMinY() + nodeMiddleY - ((tooltipHeight - 18) / 2));
            }
        });

        tooltipParent.setOnMouseExited((e) -> {
            this.hide();
        });

    }


    public void showTooltip() {
        Bounds bounds = tooltipParent.localToScreen(tooltipParent.getBoundsInLocal());
        nodeMiddleX = tooltipParent.getWidth() / 2;
        nodeMiddleY = tooltipParent.getHeight() / 2;

        if (!menuTooltip)
            this.show(tooltipParent, bounds.getMinX() + nodeMiddleX - tooltipMiddle, bounds.getMinY() - tooltipHeight - controlBar.getTranslateY());
        else
            this.show(tooltipParent, bounds.getMaxX() + 10, bounds.getMinY() + nodeMiddleY - ((tooltipHeight - 18) / 2));
    }
}
