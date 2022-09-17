package hans;

import hans.Settings.SettingsController;
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
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

public class PlaybackOptionsPopUp extends ContextMenu {

    MenuItem loopItem = new MenuItem();
    MenuItem autoplayItem = new MenuItem();
    MenuItem shuffleItem = new MenuItem();

    FadeTransition showTransition, hideTransition;

    SVGPath loopSVG = new SVGPath(), checkSVG = new SVGPath(), autoplaySVG = new SVGPath(), shuffleSVG = new SVGPath();
    Region loopIcon = new Region(), autoplayIcon = new Region(), shuffleIcon = new Region();
    public Region loopCheckIcon = new Region();
    public Region autoplayCheckIcon = new Region();
    public Region shuffleCheckIcon = new Region();



    HBox loopWrapper = new HBox();
    Label loopText = new Label();

    HBox autoplayWrapper = new HBox();
    Label autoplayText = new Label();

    HBox shuffleWrapper = new HBox();
    Label shuffleText = new Label();

    SettingsController settingsController;


    PlaybackOptionsPopUp(SettingsController settingsController){

        this.settingsController = settingsController;
        this.setId("playbackOptionsMenu");

        loopSVG.setContent(App.svgMap.get(SVG.REPEAT_ONCE));
        autoplaySVG.setContent(App.svgMap.get(SVG.REPEAT));
        shuffleSVG.setContent(App.svgMap.get(SVG.SHUFFLE));
        checkSVG.setContent(App.svgMap.get(SVG.CHECK));

        loopIcon.setShape(loopSVG);
        loopIcon.getStyleClass().add("icon");
        loopIcon.setPrefSize(20, 20);
        loopIcon.setMaxSize(20, 20);

        loopText.setText("Loop");
        loopText.getStyleClass().add("playbackOptionsPopUpLabel");

        loopCheckIcon.setShape(checkSVG);
        loopCheckIcon.getStyleClass().add("icon");
        loopCheckIcon.setPrefSize(18, 14);
        loopCheckIcon.setMaxSize(18, 14);
        loopCheckIcon.setVisible(false);

        loopWrapper.getChildren().addAll(loopIcon, loopText, loopCheckIcon);
        loopWrapper.setAlignment(Pos.CENTER_LEFT);
        loopWrapper.setSpacing(10);
        loopWrapper.setBackground(Background.EMPTY);


        loopItem.setGraphic(loopWrapper);
        loopItem.getStyleClass().add("menuItem");

        loopItem.setOnAction(e -> settingsController.playbackOptionsController.loopTab.toggle.fire());


        autoplayIcon.setShape(autoplaySVG);
        autoplayIcon.getStyleClass().add("icon");
        autoplayIcon.setPrefSize(20, 20);
        autoplayIcon.setMaxSize(20, 20);

        autoplayText.setText("Autoplay");
        autoplayText.getStyleClass().add("playbackOptionsPopUpLabel");

        autoplayCheckIcon.setShape(checkSVG);
        autoplayCheckIcon.getStyleClass().add("icon");
        autoplayCheckIcon.setPrefSize(18, 14);
        autoplayCheckIcon.setMaxSize(18, 14);
        autoplayCheckIcon.setVisible(true);

        autoplayWrapper.getChildren().addAll(autoplayIcon, autoplayText, autoplayCheckIcon);
        autoplayWrapper.setAlignment(Pos.CENTER_LEFT);
        autoplayWrapper.setSpacing(10);
        autoplayWrapper.setBackground(Background.EMPTY);

        autoplayItem.setGraphic(autoplayWrapper);
        autoplayItem.getStyleClass().add("menuItem");

        autoplayItem.setOnAction(e -> settingsController.playbackOptionsController.autoplayTab.toggle.fire());



        shuffleIcon.setShape(shuffleSVG);
        shuffleIcon.getStyleClass().add("icon");
        shuffleIcon.setPrefSize(20, 20);
        shuffleIcon.setMaxSize(20, 20);

        shuffleText.setText("Shuffle");
        shuffleText.getStyleClass().add("playbackOptionsPopUpLabel");

        shuffleCheckIcon.setShape(checkSVG);
        shuffleCheckIcon.getStyleClass().add("icon");
        shuffleCheckIcon.setPrefSize(18, 14);
        shuffleCheckIcon.setMaxSize(18, 14);
        shuffleCheckIcon.setVisible(false);

        shuffleWrapper.getChildren().addAll(shuffleIcon, shuffleText, shuffleCheckIcon);
        shuffleWrapper.setAlignment(Pos.CENTER_LEFT);
        shuffleWrapper.setSpacing(10);
        shuffleWrapper.setBackground(Background.EMPTY);

        shuffleItem.setGraphic(shuffleWrapper);
        shuffleItem.getStyleClass().add("menuItem");

        shuffleItem.setOnAction(e -> settingsController.playbackOptionsController.shuffleTab.toggle.fire());


        this.getItems().addAll(loopItem, autoplayItem, shuffleItem);
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
