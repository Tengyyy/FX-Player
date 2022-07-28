package hans;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

public class LoopPopUp extends ContextMenu {

    MenuItem loopItem = new MenuItem();

    FadeTransition showTransition, hideTransition;

    SVGPath loopSVG = new SVGPath(), checkSVG = new SVGPath();
    Region loopIcon = new Region(), checkIcon = new Region();

    HBox itemWrapper = new HBox();
    Label text = new Label();

    SettingsController settingsController;


    LoopPopUp(SettingsController settingsController){

        this.settingsController = settingsController;

        loopSVG.setContent(App.svgMap.get(SVG.REPEAT_ONCE));
        checkSVG.setContent(App.svgMap.get(SVG.CHECK));

        loopIcon.setShape(loopSVG);
        loopIcon.getStyleClass().add("icon");
        loopIcon.setPrefSize(20, 20);
        loopIcon.setMaxSize(20, 20);

        text.setText("Loop");
        text.setId("loopPopUpLabel");

        checkIcon.setShape(checkSVG);
        checkIcon.getStyleClass().add("icon");
        checkIcon.setPrefSize(18, 14);
        checkIcon.setMaxSize(18, 14);
        checkIcon.setVisible(false);

        itemWrapper.getChildren().addAll(loopIcon, text, checkIcon);
        itemWrapper.setAlignment(Pos.CENTER_LEFT);
        itemWrapper.setSpacing(10);
        itemWrapper.setBackground(Background.EMPTY);


        loopItem.setGraphic(itemWrapper);

        loopItem.setOnAction(e -> settingsController.playbackOptionsController.loopTab.toggle.fire());


        this.getItems().add(loopItem);
        this.getStyleableNode().setOpacity(0);
    }


    @Override
    public void show(Node node, double x, double y) {

        if(hideTransition != null && hideTransition.getStatus() == Animation.Status.RUNNING) hideTransition.stop();

        this.getStyleableNode().setOpacity(0);

        super.show(node, x, y);
        showTransition = new FadeTransition(Duration.millis(150), this.getStyleableNode());
        showTransition.setFromValue(0);
        showTransition.setToValue(1);
        showTransition.playFromStart();

    }

    @Override
    public void hide() {

        if(showTransition != null && showTransition.getStatus() == Animation.Status.RUNNING) showTransition.stop();

        hideTransition = new FadeTransition(Duration.millis(150), this.getStyleableNode());
        hideTransition.setFromValue(1);
        hideTransition.setToValue(0);
        hideTransition.setOnFinished(e -> {
            super.hide();
        });
        hideTransition.playFromStart();

    }

}
