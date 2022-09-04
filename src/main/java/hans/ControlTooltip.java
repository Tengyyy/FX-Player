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
import javafx.util.Duration;

import java.util.Objects;

public class ControlTooltip extends Tooltip {

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

    StackPane graphicBackground = new StackPane();

    StackPane imageViewBackground = new StackPane();
    Label durationLabel = new Label();
    ImageView imageView = new ImageView();

    HBox textContainer = new HBox();
    Label mainTextLabel = new Label();
    Label titleLabel = new Label();

    MainController mainController;




    public ControlTooltip(MainController mainController, String tooltipText, Region tooltipParent, int delay) {

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

    public ControlTooltip(MainController mainController, String tooltipText, Region tooltipParent, int delay, boolean isControlBarTooltip){
        this.tooltipText = tooltipText;
        this.tooltipParent = tooltipParent;
        this.delay = delay;
        this.isControlBarTooltip = isControlBarTooltip;
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

        tooltipParent.setOnMouseEntered((e) -> countdown.playFromStart());

        tooltipParent.setOnMouseExited((e) -> {
            this.hide();
            mouseHover.set(false);
            countdown.stop();
        });

    }

    public ControlTooltip(MainController mainController, String tooltipText, String tooltipTitle, String tooltipSubText, Image tooltipImage, Color imageBackground, Region tooltipParent, int delay, boolean isControlBarTooltip){
        this.tooltipText = tooltipText;
        this.tooltipTitle = tooltipTitle;
        this.tooltipSubText = tooltipSubText;
        this.tooltipImage = tooltipImage;
        this.imageBackground = imageBackground;
        this.tooltipParent = tooltipParent;
        this.delay = delay;
        this.isControlBarTooltip = isControlBarTooltip;
        this.mainController = mainController;

        this.setStyle("-fx-padding: 0;");

        this.getStyleClass().add("tooltip");


        mouseHover.addListener((obs, wasHover, isHover) -> {
            if(isHover){
                showTooltip();
            }
        });

        countdown = new PauseTransition(Duration.millis(delay));
        countdown.setOnFinished((e) -> mouseHover.set(true));

        graphicBackground.setPadding(new Insets(2));
        graphicBackground.setBackground(Background.EMPTY);
        graphicBackground.setPrefSize(250, 74);
        graphicBackground.setMaxSize(250, 74);
        graphicBackground.getChildren().addAll(imageViewBackground, textContainer);

        StackPane.setAlignment(imageViewBackground, Pos.CENTER_LEFT);
        imageViewBackground.setPrefSize(125, 70);
        imageViewBackground.setMaxSize(125, 70);

        imageViewBackground.setStyle("-fx-background-color: rgba(" + imageBackground.getRed() * 256 + "," + imageBackground.getGreen() * 256 + "," + imageBackground.getBlue() * 256 + ", 0.7);");
        imageViewBackground.getChildren().addAll(imageView, durationLabel);


        imageView.setFitWidth(125);
        imageView.setFitHeight(70);
        imageView.setImage(tooltipImage);
        imageView.setPreserveRatio(true);


        this.setGraphic(graphicBackground);



        tooltipParent.setOnMouseEntered((e) -> countdown.playFromStart());

        tooltipParent.setOnMouseExited((e) -> {
            this.hide();
            mouseHover.set(false);
            countdown.stop();
        });
    }

    public void showTooltip() {

        this.show(tooltipParent, 0, 0);
        tooltipMiddle = (this.getWidth() - 18) / 2;
        tooltipHeight = this.getHeight();
        this.hide();


        Bounds bounds = tooltipParent.localToScreen(tooltipParent.getLayoutBounds());
        nodeMiddleX = tooltipParent.getWidth() / 2;
        nodeMiddleY = tooltipParent.getHeight() / 2;

        double minX = mainController.videoImageViewWrapper.localToScreen(mainController.videoImageViewWrapper.getLayoutBounds()).getMinX() + 20;
        double maxX = mainController.videoImageViewWrapper.localToScreen(mainController.videoImageViewWrapper.getLayoutBounds()).getMaxX() - 20 - (this.getWidth() - 18);

        if(isControlBarTooltip){
            this.show(tooltipParent, Math.max(minX, Math.min(maxX, bounds.getMinX() + nodeMiddleX - tooltipMiddle)), bounds.getMinY() - tooltipHeight - 10);
        }
        else this.show(tooltipParent, bounds.getMinX() + nodeMiddleX - tooltipMiddle, bounds.getMinY() - tooltipHeight);
    }


    public void updateText(String newText){

        if(Objects.equals(this.getText(), newText)) return;

        this.setText(newText);

        if(this.isShowing()){
            this.hide();
            this.showTooltip();
        }
    }

}
