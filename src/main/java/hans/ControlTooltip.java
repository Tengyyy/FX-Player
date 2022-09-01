package hans;


import javafx.animation.PauseTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

import java.util.Objects;

public class ControlTooltip extends Tooltip {

    String lastTextValue = "";
    String tooltipText;


    String tooltipTitle;
    String tooltipSubText;
    Image tooltipImage;
    Color imageBackground;

    Region tooltipParent;

    double tooltipMiddle;
    double tooltipHeight;

    double nodeMiddleX;
    double nodeMiddleY;

    int delay;

    BooleanProperty mouseHover = new SimpleBooleanProperty(false); // if true the user has been hovering tooltip parent button for longer than the delay time
    PauseTransition countdown;

    boolean isControlBarTooltip = false;



    public ControlTooltip(String tooltipText, Region tooltipParent, int delay) {

        this.tooltipText = tooltipText;
        this.tooltipParent = tooltipParent;
        this.delay = delay;
        this.getStyleClass().add("tooltip");

        this.setText(tooltipText);

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

    public ControlTooltip(String tooltipText, Region tooltipParent, int delay, boolean isControlBarTooltip){
        this.tooltipText = tooltipText;
        this.tooltipParent = tooltipParent;
        this.delay = delay;
        this.isControlBarTooltip = isControlBarTooltip;
        this.getStyleClass().add("tooltip");

        this.setText(tooltipText);

        mouseHover.addListener((obs, wasHover, isHover) -> {
            if(isHover){
                showTooltip();
            }
        });

        countdown = new PauseTransition(Duration.millis(delay));
        countdown.setOnFinished((e) -> mouseHover.set(true));

        tooltipParent.setOnMouseEntered((e) -> countdown.playFromStart());

        tooltipParent.setOnMouseExited((e) -> {
            this.hide();
            mouseHover.set(false);
            countdown.stop();
        });
    }

    public ControlTooltip(String tooltipText, String tooltipTitle, String tooltipSubText, Image tooltipImage, Color imageBackground, Region tooltipParent, int delay, boolean isControlBarTooltip){
        this.tooltipText = tooltipText;
        this.tooltipTitle = tooltipTitle;
        this.tooltipSubText = tooltipSubText;
        this.tooltipImage = tooltipImage;
        this.imageBackground = imageBackground;
        this.tooltipParent = tooltipParent;
        this.delay = delay;
        this.isControlBarTooltip = isControlBarTooltip;

        this.getStyleClass().add("tooltip");

        mouseHover.addListener((obs, wasHover, isHover) -> {
            if(isHover){
                showTooltip();
            }
        });

        countdown = new PauseTransition(Duration.millis(delay));
        countdown.setOnFinished((e) -> mouseHover.set(true));

        tooltipParent.setOnMouseEntered((e) -> countdown.playFromStart());

        tooltipParent.setOnMouseExited((e) -> {
            this.hide();
            mouseHover.set(false);
            countdown.stop();
        });

    }

    public void showTooltip() {

        if(!Objects.equals(this.getText(), lastTextValue)){
            this.show(tooltipParent, 0, 0);
            tooltipMiddle = (this.getWidth() - 18) / 2;
            tooltipHeight = this.getHeight();
            this.hide();

            lastTextValue = this.getText();
        }

        Bounds bounds = tooltipParent.localToScreen(tooltipParent.getLayoutBounds());
        nodeMiddleX = tooltipParent.getWidth() / 2;
        nodeMiddleY = tooltipParent.getHeight() / 2;

        if(isControlBarTooltip) this.show(tooltipParent, bounds.getMinX() + nodeMiddleX - tooltipMiddle, bounds.getMinY() - tooltipHeight - 10);
        else this.show(tooltipParent, bounds.getMinX() + nodeMiddleX - tooltipMiddle, bounds.getMinY() - tooltipHeight);
    }


    public void updateText(String newText){

        this.setGraphic(null);

        if(Objects.equals(this.getText(), newText)) return;

        this.setText(newText);

        if(this.isShowing()){
            this.hide();
            this.showTooltip();
        }
    }

    // update previous and next video tooltips
    public void updateGraphic(String newTitle, String newSubText, Image newImage, Color newImageBackground){
        this.setText(null);
    }

}
