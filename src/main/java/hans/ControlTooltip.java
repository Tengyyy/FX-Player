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

    TooltipType tooltipType = null;

    StackPane graphicBackground = new StackPane();

    StackPane imageViewBackground = new StackPane();
    Label durationLabel = new Label();
    ImageView imageView = new ImageView();

    VBox textContainer = new VBox();
    Label mainTextLabel = new Label();
    Label titleLabel = new Label();

    MainController mainController;




    public ControlTooltip(MainController mainController, String tooltipText, Region tooltipParent, int delay) {
        createTooltip(mainController, tooltipText, tooltipParent, delay);
    }

    public ControlTooltip(MainController mainController, String tooltipText, Region tooltipParent, int delay, TooltipType tooltipType){
        this.tooltipType = tooltipType;
        createTooltip(mainController, tooltipText, tooltipParent, delay);
    }

    public ControlTooltip(MainController mainController, String tooltipText, String tooltipTitle, String tooltipSubText, Image tooltipImage, Color imageBackground, Region tooltipParent, int delay, TooltipType tooltipType){
        this.tooltipText = tooltipText;
        this.tooltipTitle = tooltipTitle;
        this.tooltipSubText = tooltipSubText;
        this.tooltipImage = tooltipImage;
        this.imageBackground = imageBackground;
        this.tooltipParent = tooltipParent;
        this.delay = delay;
        this.tooltipType = tooltipType;
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
        graphicBackground.setPrefSize(350, 94);
        graphicBackground.setMaxSize(350, 94);
        graphicBackground.getChildren().addAll(imageViewBackground, textContainer);

        StackPane.setAlignment(imageViewBackground, Pos.CENTER_LEFT);
        imageViewBackground.setPrefSize(160, 90);
        imageViewBackground.setMaxSize(160, 90);

        if(imageBackground != null) imageViewBackground.setStyle("-fx-background-color: rgba(" + imageBackground.getRed() * 256 + "," + imageBackground.getGreen() * 256 + "," + imageBackground.getBlue() * 256 + ", 0.7);");

        imageViewBackground.getChildren().addAll(imageView, durationLabel);


        imageView.setFitWidth(160);
        imageView.setFitHeight(90);
        if(tooltipImage != null) imageView.setImage(tooltipImage);
        imageView.setPreserveRatio(true);

        StackPane.setAlignment(durationLabel, Pos.BOTTOM_RIGHT);

        char[] durationArray = tooltipSubText.toCharArray();

        if(durationArray[durationArray.length - 2] == '•'){
            durationLabel.setText(tooltipSubText.substring(0, durationArray.length - 3));
        }
        else durationLabel.setText(tooltipSubText);

        durationLabel.setId("tooltipSubText");
        StackPane.setMargin(durationLabel, new Insets(0, 5, 3, 0));


        textContainer.setAlignment(Pos.TOP_CENTER);
        textContainer.setPrefSize(186, 90);
        textContainer.setMaxSize(186, 90);
        textContainer.getChildren().addAll(mainTextLabel, titleLabel);
        textContainer.setSpacing(3);
        textContainer.setPadding(new Insets(0, 5, 0, 5));
        StackPane.setAlignment(textContainer, Pos.TOP_RIGHT);

        mainTextLabel.setText(tooltipText);
        mainTextLabel.setId("tooltipMainText");
        mainTextLabel.setPadding(new Insets(2, 0,0,0));

        titleLabel.setWrapText(true);
        titleLabel.setId("tooltipTitle");
        titleLabel.setText(tooltipTitle);
        titleLabel.setTextAlignment(TextAlignment.CENTER);


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

