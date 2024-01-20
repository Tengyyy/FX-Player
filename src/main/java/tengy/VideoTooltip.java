package tengy;

import javafx.scene.shape.SVGPath;
import tengy.menu.Queue.QueueItem;
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
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;

import java.util.Objects;

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
    SVGPath imageSVG = new SVGPath();
    Region imageIcon = new Region();

    VBox textContainer = new VBox();
    Label mainTextLabel = new Label();
    Label titleLabel = new Label();

    MainController mainController;
    QueueItem queueItem;

    boolean isPrevious;

    Label graphicLabel = new Label();

    String hotkeyText;


    public VideoTooltip(MainController mainController, Region tooltipParent, boolean isPrevious, String hotkeyText){

        this.mainController = mainController;
        this.tooltipParent = tooltipParent;
        this.isPrevious = isPrevious;

        this.getStyleClass().add("tooltip");
        this.setStyle("-fx-padding: 0;");

        this.hotkeyText = hotkeyText;

        graphicLabel.getStyleClass().add("graphicLabel");


        graphicBackground.setPadding(new Insets(2));
        graphicBackground.setBackground(Background.EMPTY);
        graphicBackground.setPrefSize(350, 94);
        graphicBackground.setMaxSize(350, 94);
        graphicBackground.getChildren().addAll(imageViewBackground, textContainer);

        StackPane.setAlignment(imageViewBackground, Pos.CENTER_LEFT);
        imageViewBackground.setPrefSize(160, 90);
        imageViewBackground.setMaxSize(160, 90);
        imageViewBackground.getChildren().addAll(imageIcon, imageView, durationLabel);
        imageViewBackground.getStyleClass().add("imageViewBackground");

        imageSVG.setContent(SVG.IMAGE_WIDE.getContent());
        imageIcon.setShape(imageSVG);
        imageIcon.setPrefSize(50, 40);
        imageIcon.setMaxSize(50, 40);
        imageIcon.getStyleClass().add("imageIcon");

        imageView.setFitWidth(160);
        imageView.setFitHeight(90);
        imageView.setPreserveRatio(true);
        imageView.setVisible(false);

        Rectangle imageClip = new Rectangle();
        imageClip.setWidth(160);
        imageClip.setHeight(90);
        imageClip.setArcWidth(10);
        imageClip.setArcHeight(10);

        imageView.setClip(imageClip);

        StackPane.setAlignment(durationLabel, Pos.BOTTOM_RIGHT);


        durationLabel.getStyleClass().add("tooltipSubText");
        StackPane.setMargin(durationLabel, new Insets(0, 5, 3, 0));


        textContainer.setAlignment(Pos.TOP_CENTER);
        textContainer.setPrefSize(186, 90);
        textContainer.setMaxSize(186, 90);
        textContainer.getChildren().addAll(mainTextLabel, titleLabel);
        textContainer.setSpacing(3);
        textContainer.setPadding(new Insets(0, 5, 0, 5));
        StackPane.setAlignment(textContainer, Pos.TOP_RIGHT);

        mainTextLabel.setId("tooltipMainText");
        mainTextLabel.setPadding(new Insets(2, 0,0,0));
        if(isPrevious) mainTextLabel.setText("PREVIOUS" + hotkeyText);
        else mainTextLabel.setText("NEXT" + hotkeyText);

        titleLabel.setWrapText(true);
        titleLabel.setId("tooltipTitle");
        titleLabel.setTextAlignment(TextAlignment.CENTER);



        mouseHover.addListener((observableValue, oldValue, newValue) -> {
            if(newValue) showTooltip();
            else hide();
        });
    }


    public void updateTooltip(QueueItem queueItem){
        this.queueItem = queueItem;

        boolean isShowing = this.isShowing();

        if(isShowing) this.hide();

        if(queueItem == null){
            graphicLabel.setText("Replay");
            this.setGraphic(graphicLabel);
        }
        else {
            if(queueItem.getMediaItem() != null){
                loadTooltip();
            }
            else {

                if(isPrevious) graphicLabel.setText("Previous video");
                else graphicLabel.setText("Next video");

                this.setGraphic(graphicLabel);

                queueItem.getMediaItemGenerated().addListener((observableValue, oldValue, newValue) -> {
                    if((!isPrevious || mainController.getControlBarController().durationSlider.getValue() <= 5) && newValue && this.queueItem == queueItem){
                        if(this.isShowing()){
                            this.hide();

                            loadTooltip();

                            this.showTooltip();
                        }
                        else loadTooltip();
                    }
                });
            }
        }

        if(isShowing) this.showTooltip();
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
        titleLabel.setText(queueItem.getTitle());
        durationLabel.setText(Utilities.durationToString(queueItem.getMediaItem().getDuration()));

        imageSVG.setContent(queueItem.getMediaItem().icon.getContent());
        if(queueItem.getMediaItem().getCover() != null){
            imageView.setImage(queueItem.getMediaItem().getCover());
            Color color = queueItem.getMediaItem().getCoverBackgroundColor();
            imageViewBackground.setStyle("-fx-background-color: rgb(" +  color.getRed() * 255 + "," + color.getGreen() * 255 + "," + color.getBlue() * 255 + ");");
            imageIcon.setVisible(false);
            imageView.setVisible(true);
        }
        else {
            imageView.setVisible(false);
            imageIcon.setVisible(true);
            imageViewBackground.setStyle("-fx-background-color: rgb(30,30,30);");
        }

        this.setGraphic(graphicBackground);
    }


    public void updateHotkeyText(String newString){
        if(Objects.equals(hotkeyText, newString)) return;

        hotkeyText = newString;
        if(isPrevious) mainTextLabel.setText("PREVIOUS" + hotkeyText);
        else mainTextLabel.setText("NEXT" + hotkeyText);
    }
}


