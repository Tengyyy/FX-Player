package hans;

import hans.Menu.MenuObject;
import javafx.animation.PauseTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class VideoTooltip extends Tooltip {

    Region tooltipParent;

    double tooltipMiddle;
    double tooltipHeight;

    double nodeMiddleX;
    double nodeMiddleY;


    BooleanProperty mouseHover = new SimpleBooleanProperty(false); // if true the user has been hovering tooltip parent button for longer than the delay time


    StackPane graphicBackground = new StackPane();

    StackPane imageViewBackground = new StackPane();
    Label durationLabel = new Label();
    ImageView imageView = new ImageView();

    VBox textContainer = new VBox();
    Label mainTextLabel = new Label();
    Label titleLabel = new Label();

    MainController mainController;
    MenuObject menuObject;

    public VideoTooltip(MainController mainController, String tooltipText, Region tooltipParent, MenuObject menuObject){

        this.mainController = mainController;
        this.menuObject = menuObject;
        this.tooltipParent = tooltipParent;

        this.setStyle("-fx-padding: 0;");

        this.getStyleClass().add("tooltip");

        graphicBackground.setPadding(new Insets(2));
        graphicBackground.setBackground(Background.EMPTY);
        graphicBackground.setPrefSize(350, 94);
        graphicBackground.setMaxSize(350, 94);
        graphicBackground.getChildren().addAll(imageViewBackground, textContainer);

        StackPane.setAlignment(imageViewBackground, Pos.CENTER_LEFT);
        imageViewBackground.setPrefSize(160, 90);
        imageViewBackground.setMaxSize(160, 90);

        if(menuObject.getMediaItem() == null) imageViewBackground.setStyle("-fx-background-color: rgba(64, 64, 64, 0.7);");

        imageViewBackground.getChildren().addAll(imageView, durationLabel);


        imageView.setFitWidth(160);
        imageView.setFitHeight(90);
        imageView.setPreserveRatio(true);

        StackPane.setAlignment(durationLabel, Pos.BOTTOM_RIGHT);


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
        titleLabel.setTextAlignment(TextAlignment.CENTER);


        this.setGraphic(graphicBackground);

        mouseHover.addListener((observableValue, oldValue, newValue) -> {
            if(newValue) showTooltip();
        });


        tooltipParent.setOnMouseEntered((e) -> mouseHover.set(true));

        tooltipParent.setOnMouseExited((e) -> {
            this.hide();
            mouseHover.set(false);
        });

        if(menuObject.getMediaItem() != null){
            loadTooltip();
        }
        else menuObject.getMediaItemGenerated().addListener((observableValue, oldValue, newValue) -> {
            if(newValue) loadTooltip();
        });
    }

    // previous video button tooltip with text replay, shown when video has played for 5 secs or longer
    public VideoTooltip(MainController mainController, ControlBarController controlBarController){
        this.getStyleClass().add("tooltip");
        this.setStyle("-fx-padding: 10;");
        this.setText("Replay");

        this.mainController = mainController;
        this.tooltipParent = controlBarController.previousVideoButton;

        mouseHover.addListener((observableValue, oldValue, newValue) -> {
            if(newValue) showTooltip();
        });

        controlBarController.previousVideoButton.setOnMouseEntered((e) -> mouseHover.set(true));

        controlBarController.previousVideoButton.setOnMouseExited((e) -> {
            this.hide();
            mouseHover.set(false);
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

        this.show(tooltipParent, Math.max(minX, Math.min(maxX, bounds.getMinX() + nodeMiddleX - tooltipMiddle)), bounds.getMinY() - tooltipHeight - 10);

    }

    private void loadTooltip(){
        titleLabel.setText(menuObject.getTitle());
        durationLabel.setText(Utilities.getTime(menuObject.getMediaItem().getDuration()));
        if(menuObject.getMediaItem().getCover() != null){
            imageView.setImage(menuObject.getMediaItem().getCover());
            Color color = menuObject.getMediaItem().getCoverBackgroundColor();
            imageViewBackground.setStyle("-fx-background-color: rgba(" +  color.getRed() * 256 + "," + color.getGreen() * 256 + "," + color.getBlue() + ",0.7);");
        }
        else {
            imageView.setImage(menuObject.getMediaItem().getPlaceholderCover());
        }
    }
}


