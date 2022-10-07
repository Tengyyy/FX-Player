package hans.Menu;

import com.jfoenix.controls.JFXButton;
import hans.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.FileChooser;

import java.io.File;

public class MetadataEditPage {

    MenuController menuController;

    SVGPath closeIconSVG = new SVGPath();
    SVGPath backIconSVG = new SVGPath();
    SVGPath editIconSVG = new SVGPath();

    StackPane closeButtonBar = new StackPane();
    StackPane closeButtonPane = new StackPane();

    StackPane footerPane = new StackPane();

    JFXButton applyButton = new JFXButton();

    JFXButton cancelButton = new JFXButton();

    Button closeButton = new Button();
    Region closeIcon = new Region();

    StackPane backButtonPane = new StackPane();
    Button backButton = new Button();
    Region backIcon = new Region();

    public VBox content = new VBox();

    StackPane imageViewWrapper = new StackPane();
    public StackPane imageViewContainer = new StackPane();
    public ImageView imageView = new ImageView();
    StackPane imageFilter = new StackPane();
    StackPane editImageButtonWrapper = new StackPane();
    JFXButton editImageButton = new JFXButton();
    Region editImageIcon = new Region();
    ControlTooltip editImageTooltip;

    public VBox textBox = new VBox();

    MenuObject menuObject = null;


    public boolean changesMade = false;


    Color newColor = null;
    Image newImage = null;

    MetadataEditPage(MenuController menuController){
        this.menuController = menuController;

        editIconSVG.setContent(App.svgMap.get(SVG.EDIT));

        backIconSVG.setContent(App.svgMap.get(SVG.ARROW_LEFT));
        backIcon.setShape(backIconSVG);
        backIcon.setPrefSize(20, 20);
        backIcon.setMaxSize(20, 20);
        backIcon.setId("backIcon");
        backIcon.setMouseTransparent(true);


        backButton.setPrefSize(40, 40);
        backButton.setMaxSize(40, 40);
        backButton.setCursor(Cursor.HAND);
        backButton.setBackground(Background.EMPTY);

        backButton.setOnAction(e -> exitMetadataEditPage());

        backButton.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> AnimationsClass.AnimateBackgroundColor(backIcon, (Color) backIcon.getBackground().getFills().get(0).getFill(), Color.rgb(255, 255, 255), 200));

        backButton.addEventHandler(MouseEvent.MOUSE_EXITED, e -> AnimationsClass.AnimateBackgroundColor(backIcon, (Color) backIcon.getBackground().getFills().get(0).getFill(), Color.rgb(200, 200, 200), 200));

        backButtonPane.setPrefSize(50, 50);
        backButtonPane.setMaxSize(50, 50);
        backButtonPane.getChildren().addAll(backButton, backIcon);
        StackPane.setAlignment(backButtonPane, Pos.CENTER_LEFT);



        closeIconSVG.setContent(App.svgMap.get(SVG.CLOSE));
        closeIcon.setShape(closeIconSVG);
        closeIcon.setPrefSize(20, 20);
        closeIcon.setMaxSize(20, 20);
        closeIcon.setId("closeIcon");
        closeIcon.setMouseTransparent(true);

        closeButton.setPrefSize(40, 40);
        closeButton.setMaxSize(40, 40);
        closeButton.setCursor(Cursor.HAND);
        closeButton.setBackground(Background.EMPTY);

        closeButton.setOnAction(e -> menuController.closeMenu());

        closeButton.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> AnimationsClass.AnimateBackgroundColor(closeIcon, (Color) closeIcon.getBackground().getFills().get(0).getFill(), Color.rgb(255, 255, 255), 200));

        closeButton.addEventHandler(MouseEvent.MOUSE_EXITED, e -> AnimationsClass.AnimateBackgroundColor(closeIcon, (Color) closeIcon.getBackground().getFills().get(0).getFill(), Color.rgb(200, 200, 200), 200));

        closeButtonPane.setPrefSize(50, 50);
        closeButtonPane.setMaxSize(50, 50);
        closeButtonPane.getChildren().addAll(closeButton, closeIcon);
        StackPane.setAlignment(closeButtonPane, Pos.CENTER_RIGHT);

        closeButtonBar.setPrefHeight(60);
        closeButtonBar.setMinHeight(60);
        closeButtonBar.getChildren().addAll(backButtonPane, closeButtonPane);

        imageViewWrapper.getChildren().add(imageViewContainer);
        imageViewWrapper.setPadding(new Insets(20, 0, 50, 0));
        imageViewWrapper.setBackground(Background.EMPTY);

        imageViewContainer.getChildren().addAll(imageView, imageFilter, editImageButtonWrapper);
        imageViewContainer.setId("imageViewContainer");
        imageViewContainer.maxWidthProperty().bind(Bindings.min(400, menuController.menu.widthProperty().multiply(0.7)));

        imageFilter.setStyle("-fx-background-color: black;");
        imageFilter.setOpacity(0);
        imageFilter.setMouseTransparent(true);
        imageViewContainer.setOnMouseEntered(e -> {
            AnimationsClass.fadeAnimation(200, imageFilter, imageFilter.getOpacity(), 0.5, false, 1, true);
            AnimationsClass.fadeAnimation(200, editImageIcon, editImageIcon.getOpacity(), 1, false, 1, true);
        });
        imageViewContainer.setOnMouseExited(e -> {
            AnimationsClass.fadeAnimation(200, imageFilter, imageFilter.getOpacity(), 0, false, 1, true);
            AnimationsClass.fadeAnimation(200, editImageIcon, editImageIcon.getOpacity(), 0, false, 1, true);

        });

        imageView.setMouseTransparent(true);
        imageView.setPreserveRatio(true);
        imageView.fitWidthProperty().bind(Bindings.min(400, menuController.menu.widthProperty().multiply(0.7)));
        imageView.fitHeightProperty().bind(Bindings.min(225, imageView.fitWidthProperty().multiply(9).divide(16)));

        editImageButtonWrapper.getChildren().addAll(editImageButton, editImageIcon);
        editImageButtonWrapper.setPrefSize(80, 80);
        editImageButtonWrapper.setMaxSize(80, 80);
        editImageButtonWrapper.setOnMouseEntered(e -> {
            AnimationsClass.fadeAnimation(200, editImageButton, editImageButton.getOpacity(), 0.7, false, 1, true);
            AnimationsClass.AnimateBackgroundColor(editImageIcon, (Color) editImageIcon.getBackground().getFills().get(0).getFill(), Color.rgb(255, 255, 255), 200);
        });
        editImageButtonWrapper.setOnMouseExited(e -> {
            AnimationsClass.fadeAnimation(200, editImageButton, editImageButton.getOpacity(), 0, false, 1, true);
            AnimationsClass.AnimateBackgroundColor(editImageIcon, (Color) editImageIcon.getBackground().getFills().get(0).getFill(), Color.rgb(200, 200, 200), 200);
        });

        editImageIcon.getStyleClass().add("menuIcon2");
        editImageIcon.setOpacity(0);
        editImageIcon.setShape(editIconSVG);
        editImageIcon.setPrefSize(40, 40);
        editImageIcon.setMaxSize(40, 40);
        editImageIcon.setMouseTransparent(true);

        editImageButton.setPrefSize(80, 80);
        editImageButton.setMaxSize(80, 80);
        editImageButton.setRipplerFill(Color.WHITE);
        editImageButton.setId("editImageButton");
        editImageButton.setOpacity(0);
        editImageButton.setCursor(Cursor.HAND);

        editImageButton.setOnAction(e -> {
            editImage();
        });

        Platform.runLater(() -> {
            editImageTooltip = new ControlTooltip(menuController.mainController, "Edit image", editImageButton, 1000);
        });

        content.setAlignment(Pos.TOP_CENTER);
        content.getChildren().addAll(closeButtonBar, imageViewWrapper, textBox, footerPane);
        content.setBackground(Background.EMPTY);
        content.setPadding(new Insets(0, 0, 20, 0));
        menuController.metadataEditScroll.setContent(content);

        textBox.setAlignment(Pos.TOP_LEFT);
        textBox.setPadding(new Insets(0, 15, 0, 15));
        textBox.setSpacing(10);

        footerPane.getChildren().addAll(cancelButton, applyButton);
    }

    public void enterMetadataEditPage(MenuObject menuObject){
        this.menuObject = menuObject;


        imageView.setImage(menuObject.getMediaItem().getCover());

        Color color = menuObject.getMediaItem().getCoverBackgroundColor();

        if(color != null) imageViewContainer.setStyle("-fx-background-color: rgba(" + color.getRed() * 256 +  "," + color.getGreen() * 256 + "," + color.getBlue() * 256 + ",0.7);");

        menuController.metadataEditScroll.setVisible(true);
        menuController.metadataScroll.setVisible(false);

        menuController.menuState = MenuState.METADATA_EDIT_OPEN;
    }


    public void exitMetadataEditPage(){
        this.menuObject = null;

        menuController.metadataEditScroll.setVisible(false);
        menuController.metadataScroll.setVisible(true);

        changesMade = false;
        textBox.getChildren().clear();
        imageView.setImage(null);
        imageViewContainer.setStyle("-fx-background-color: transparent;");

        menuController.menuState = MenuState.METADATA_OPEN;
    }

    public void editImage(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Supported images", "*.jpg", ".jpeg", ".png"));

        File selectedFile = fileChooser.showOpenDialog(imageView.getScene().getWindow());
        if(selectedFile != null){
            changesMade = true;
            newImage = new Image(String.valueOf(selectedFile));
            imageView.setImage(newImage);

            double aspectRatio = newImage.getWidth() / newImage.getHeight();
            double realWidth = Math.min(imageView.getFitWidth(), imageView.getFitHeight() * aspectRatio);

            newColor = Utilities.findDominantColor(newImage, realWidth < imageView.getFitWidth());

            if(newColor != null) imageViewContainer.setStyle("-fx-background-color: rgba(" + newColor.getRed() * 256 +  "," + newColor.getGreen() * 256 + "," + newColor.getBlue() * 256 + ",0.7);");
        }

    }
}
