package hans;

import hans.Menu.MenuObject;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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

    boolean isPrevious;

    Label graphicLabel = new Label();


    public VideoTooltip(MainController mainController, Region tooltipParent, boolean isPrevious){

        this.mainController = mainController;
        this.tooltipParent = tooltipParent;
        this.isPrevious = isPrevious;

        this.getStyleClass().add("tooltip");
        this.setStyle("-fx-padding: 0;");

        graphicLabel.getStyleClass().add("graphicLabel");


        graphicBackground.setPadding(new Insets(2));
        graphicBackground.setBackground(Background.EMPTY);
        graphicBackground.setPrefSize(350, 94);
        graphicBackground.setMaxSize(350, 94);
        graphicBackground.getChildren().addAll(imageViewBackground, textContainer);

        StackPane.setAlignment(imageViewBackground, Pos.CENTER_LEFT);
        imageViewBackground.setPrefSize(160, 90);
        imageViewBackground.setMaxSize(160, 90);
        imageViewBackground.getChildren().addAll(imageView, durationLabel);
        imageViewBackground.getStyleClass().add("imageViewBackground");


        imageView.setFitWidth(160);
        imageView.setFitHeight(90);
        imageView.setPreserveRatio(true);

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
        if(isPrevious) mainTextLabel.setText("PREVIOUS (SHIFT+P)");
        else mainTextLabel.setText("NEXT (SHIFT+N)");

        titleLabel.setWrapText(true);
        titleLabel.setId("tooltipTitle");
        titleLabel.setTextAlignment(TextAlignment.CENTER);



        mouseHover.addListener((observableValue, oldValue, newValue) -> {
            if(newValue) showTooltip();
            else hide();
        });
    }


    public void updateTooltip(MenuObject menuObject){
        this.menuObject = menuObject;

        boolean isShowing = this.isShowing();

        if(isShowing) this.hide();

        if(menuObject == null){
            graphicLabel.setText("Replay");
            this.setGraphic(graphicLabel);
        }
        else {
            if(menuObject.getMediaItem() != null){
                loadTooltip();
            }
            else {

                if(isPrevious) graphicLabel.setText("Previous video");
                else graphicLabel.setText("Next video");

                this.setGraphic(graphicLabel);

                menuObject.getMediaItemGenerated().addListener((observableValue, oldValue, newValue) -> {
                    if((!isPrevious || mainController.getControlBarController().durationSlider.getValue() <= 5) && newValue && this.menuObject == menuObject){
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
        titleLabel.setText(menuObject.getTitle());
        durationLabel.setText(Utilities.getTime(menuObject.getMediaItem().getDuration()));
        if(menuObject.getMediaItem().getCover() != null){
            imageView.setImage(menuObject.getMediaItem().getCover());
            Color color = menuObject.getMediaItem().getCoverBackgroundColor();
            imageViewBackground.setStyle("-fx-background-color: rgba(" +  color.getRed() * 255 + "," + color.getGreen() * 255 + "," + color.getBlue() * 255 + ",0.7);");
        }
        else {
            imageView.setImage(menuObject.getMediaItem().getPlaceholderCover());
            imageViewBackground.setStyle("-fx-background-color: rgba(255, 0, 0, 0.7);");
        }

        this.setGraphic(graphicBackground);
    }
}


