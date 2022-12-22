package hans;


import javafx.animation.PauseTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.util.Objects;

public class ControlTooltip extends Tooltip {

    String tooltipText;

    Region tooltipParent;

    double tooltipMiddle;
    double tooltipHeight;

    double nodeMiddleX;
    double nodeMiddleY;

    int delay;

    BooleanProperty mouseHover = new SimpleBooleanProperty(false); // if true the user has been hovering tooltip parent button for longer than the delay time
    PauseTransition countdown;

    TooltipType tooltipType = null;

    MainController mainController;




    public ControlTooltip(MainController mainController, String tooltipText, Region tooltipParent, int delay) {
        createTooltip(mainController, tooltipText, tooltipParent, delay);
    }

    public ControlTooltip(MainController mainController, String tooltipText, Region tooltipParent, int delay, TooltipType tooltipType){
        this.tooltipType = tooltipType;
        createTooltip(mainController, tooltipText, tooltipParent, delay);
    }

    public void showTooltip() {

        this.show(tooltipParent, 0, 0);
        tooltipMiddle = (this.getWidth() - 18) / 2;
        tooltipHeight = this.getHeight();
        this.hide();


        Bounds bounds = tooltipParent.localToScreen(tooltipParent.getLayoutBounds());
        nodeMiddleX = tooltipParent.getWidth() / 2;
        nodeMiddleY = tooltipParent.getHeight() / 2;

        double minX, maxX;
        if(tooltipType == TooltipType.MINIPLAYER_TOOLTIP){
            minX = mainController.miniplayer.miniplayerController.videoImageViewWrapper.localToScreen(mainController.miniplayer.miniplayerController.videoImageViewWrapper.getLayoutBounds()).getMinX() + 20;
            maxX = mainController.miniplayer.miniplayerController.videoImageViewWrapper.localToScreen(mainController.miniplayer.miniplayerController.videoImageViewWrapper.getLayoutBounds()).getMaxX() - 20 - (this.getWidth() - 18);
        }
        else {
            minX = mainController.videoImageViewWrapper.localToScreen(mainController.videoImageViewWrapper.getLayoutBounds()).getMinX() + 20;
            maxX = mainController.videoImageViewWrapper.localToScreen(mainController.videoImageViewWrapper.getLayoutBounds()).getMaxX() - 20 - (this.getWidth() - 18);
        }

        if(tooltipType == TooltipType.CONTROLBAR_TOOLTIP){
            this.show(tooltipParent, Math.max(minX, Math.min(maxX, bounds.getMinX() + nodeMiddleX - tooltipMiddle)), bounds.getMinY() - tooltipHeight - 10);
        }
        else if(tooltipType == TooltipType.MENU_TOOLTIP){
            this.show(tooltipParent, Math.max(minX, Math.min(maxX, bounds.getMinX() + nodeMiddleX - tooltipMiddle)), bounds.getMaxY() + 10);
        }
        else this.show(tooltipParent, Math.max(minX, Math.min(maxX, bounds.getMinX() + nodeMiddleX - tooltipMiddle)), bounds.getMinY() - tooltipHeight);
    }


    public void updateText(String newText){

        if(Objects.equals(this.getText(), newText)) return;

        this.setText(newText);

        if(this.isShowing()){
            this.hide();
            this.showTooltip();
        }
    }

    public void updateDelay(Duration duration){
        delay = (int) duration.toSeconds();
        countdown.setDuration(duration);
    }


    private void createTooltip(MainController mainController, String tooltipText, Region tooltipParent, int delay){
        this.tooltipText = tooltipText;
        this.tooltipParent = tooltipParent;
        this.delay = delay;
        this.mainController = mainController;

        this.getStyleClass().add("tooltip");
        this.setStyle("-fx-padding: 10;");
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

}

