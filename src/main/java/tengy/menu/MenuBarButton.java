package tengy.menu;

import javafx.css.PseudoClass;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import tengy.ControlTooltip;
import tengy.TooltipType;
import javafx.animation.Animation;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeLineCap;
import javafx.util.Duration;

import static tengy.Utilities.keyboardFocusOff;


public class MenuBarButton extends StackPane {

    MenuController menuController;

    Button button = new FocusableMenuButton();
    Line line = new Line();

    SVGPath svgPath = new SVGPath();
    public Region region = new Region();

    String text;

    boolean isActive = false;

    ScaleTransition scaleTransition = null;

    public ControlTooltip controlTooltip;

    MenuBar menuBar;

    MenuBarButton(MenuController menuController, MenuBar menuBar, String svg, int iconWidth, int iconHeight, String text){

        this.menuController = menuController;
        this.menuBar = menuBar;

        this.text = text;

        this.setPrefSize(50, 40);
        this.setMaxSize(50, 40);
        this.getChildren().addAll(button, line);
        this.setPadding(new Insets(0, 5, 0, 5));

        button.setPrefSize(40, 40);
        button.setMaxSize(40, 40);
        button.getStyleClass().add("menuBarButton");
        button.setAlignment(Pos.CENTER_LEFT);
        button.setGraphicTextGap(20);
        button.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                menuBar.focus.set(menuBar.focusNodes.indexOf(button));
            }
            else{
                keyboardFocusOff(button);
                menuBar.focus.set(-1);
            }
        });

        button.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            button.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        button.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            button.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });

        svgPath.setContent(svg);
        region.setShape(svgPath);
        region.setPrefSize(iconWidth, iconHeight);
        region.setMaxSize(iconWidth, iconHeight);
        region.getStyleClass().addAll("menuIcon", "graphic");

        button.setGraphic(region);

        line.setMouseTransparent(true);
        line.setStroke(Color.RED);
        line.setStrokeLineCap(StrokeLineCap.ROUND);
        line.setStrokeWidth(4);
        StackPane.setAlignment(line, Pos.CENTER_LEFT);
        line.setStartY(20);
        line.setEndY(30);
        line.setScaleY(0);
        line.setTranslateX(1);
    }


    public void setActive(){
        if(isActive) return;

        isActive = true;

        if(!button.getStyleClass().contains("activeMenuBarButton")) button.getStyleClass().add("activeMenuBarButton");

        if(scaleTransition != null && scaleTransition.getStatus() == Animation.Status.RUNNING) scaleTransition.stop();

        scaleTransition = new ScaleTransition(Duration.millis(100), line);
        scaleTransition.setFromY(line.getScaleY());
        scaleTransition.setToY(1);
        scaleTransition.playFromStart();
    }


    public void setInactive(){
        if(!isActive) return;

        isActive = false;

        button.getStyleClass().remove("activeMenuBarButton");

        if(scaleTransition != null && scaleTransition.getStatus() == Animation.Status.RUNNING) scaleTransition.stop();

        scaleTransition = new ScaleTransition(Duration.millis(300), line);
        scaleTransition.setFromY(line.getScaleY());
        scaleTransition.setToY(0);
        scaleTransition.playFromStart();
    }

    public void extend(){
        this.setPrefWidth(300);
        this.setMaxWidth(300);

        button.setPrefWidth(290);
        button.setMaxWidth(290);
        button.setText(text);

        controlTooltip.disableTooltip();
    }

    public void shrink(){
        this.setPrefWidth(50);
        this.setMaxWidth(50);

        button.setPrefWidth(40);
        button.setMaxWidth(40);
        button.setText("");

        controlTooltip.enableTooltip();
    }

    public void loadTooltip(String actionText, String hotkeyText){
        controlTooltip = new ControlTooltip(menuController.mainController, actionText, hotkeyText, this.button, 1000, TooltipType.MENUBAR_TOOLTIP);
    }
}
