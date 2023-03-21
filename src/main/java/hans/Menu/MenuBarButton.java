package hans.Menu;

import hans.App;
import hans.SVG;
import javafx.animation.Animation;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeLineCap;
import javafx.util.Duration;


public class MenuBarButton extends StackPane {

    Button button = new Button();
    Line line = new Line();

    SVGPath svgPath = new SVGPath();
    Region region = new Region();

    String text;

    boolean isActive = false;

    ScaleTransition scaleTransition = null;

    MenuBarButton(String svg, int iconWidth, int iconHeight, String text){
        this.text = text;

        this.setPrefSize(50, 40);
        this.setMaxSize(50, 40);
        this.getChildren().addAll(button, line);
        this.setPadding(new Insets(0, 5, 0, 5));

        button.setPrefSize(40, 40);
        button.setMaxSize(40, 40);
        button.setCursor(Cursor.HAND);
        button.getStyleClass().add("menuBarButton");

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
}
