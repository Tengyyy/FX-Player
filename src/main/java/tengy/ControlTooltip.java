package tengy;


import javafx.animation.PauseTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.util.Objects;

public class ControlTooltip extends Tooltip {

    String actionText;
    String hotkeyText;

    Region tooltipParent;

    double tooltipMiddle;
    double tooltipHeight;

    double nodeMiddleX;
    double nodeMiddleY;

    int delay;

    public BooleanProperty mouseHover = new SimpleBooleanProperty(false); // if true the user has been hovering tooltip parent button for longer than the delay time
    public PauseTransition countdown;

    TooltipType tooltipType = null;

    MainController mainController;


    EventHandler<MouseEvent> enterHandler;
    EventHandler<MouseEvent> exitHandler;


    public ControlTooltip(MainController mainController, String actionText, String hotkeyText, Region tooltipParent, int delay) {
        createTooltip(mainController, actionText, hotkeyText, tooltipParent, delay);
    }

    public ControlTooltip(MainController mainController, String actionText, String hotkeyText, Region tooltipParent, int delay, TooltipType tooltipType){
        this.tooltipType = tooltipType;
        createTooltip(mainController, actionText, hotkeyText, tooltipParent, delay);
    }


    private void createTooltip(MainController mainController, String actionText, String hotkeyText, Region tooltipParent, int delay){
        this.actionText = actionText;
        this.hotkeyText = hotkeyText;
        this.tooltipParent = tooltipParent;
        this.delay = delay;
        this.mainController = mainController;

        this.getStyleClass().add("tooltip");
        this.setStyle("-fx-padding: 10;");
        this.setText(actionText + hotkeyText);


        this.enterHandler = mouseEvent -> {
            countdown.playFromStart();
        };

        this.exitHandler = mouseEvent -> {
            this.hide();
            mouseHover.set(false);
            countdown.stop();
        };


        mouseHover.addListener((obs, wasHover, isHover) -> {
            if(isHover){
                showTooltip();
            }
        });

        countdown = new PauseTransition(Duration.millis(delay));
        countdown.setOnFinished((e) -> mouseHover.set(true));

        tooltipParent.addEventHandler(MouseEvent.MOUSE_ENTERED, enterHandler);
        tooltipParent.addEventHandler(MouseEvent.MOUSE_EXITED, exitHandler);
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
        else if(tooltipType == TooltipType.MENUBAR_TOOLTIP){
            this.show(tooltipParent, Math.max(minX, Math.min(maxX, bounds.getMaxX() + 10)), bounds.getMinY() + (nodeMiddleY - (tooltipHeight-18)/2));
        }
        else if(tooltipType == TooltipType.MENU_TOOLTIP){
            this.show(tooltipParent, Math.max(minX, Math.min(maxX, bounds.getMinX() + nodeMiddleX - tooltipMiddle)), bounds.getMaxY() + 10);
        }
        else this.show(tooltipParent, Math.max(minX, Math.min(maxX, bounds.getMinX() + nodeMiddleX - tooltipMiddle)), bounds.getMinY() - tooltipHeight);
    }


    public void updateActionText(String newText){

        if(Objects.equals(actionText, newText)) return;

        actionText = newText;

        this.setText(actionText + hotkeyText);

        if(this.isShowing()){
            this.hide();
            this.showTooltip();
        }
    }

    public void updateHotkeyText(String newText){
        if(Objects.equals(hotkeyText, newText)) return;

        hotkeyText = newText;

        this.setText(actionText + hotkeyText);

        if(this.isShowing()){
            this.hide();
            this.showTooltip();
        }
    }



    public void disableTooltip(){
        tooltipParent.removeEventHandler(MouseEvent.MOUSE_ENTERED, enterHandler);

        this.hide();
        mouseHover.set(false);
        countdown.stop();
    }

    public void enableTooltip(){
        tooltipParent.addEventHandler(MouseEvent.MOUSE_ENTERED, enterHandler);
    }



    public void updateDelay(Duration duration){
        delay = (int) duration.toSeconds();
        countdown.setDuration(duration);
    }

}

