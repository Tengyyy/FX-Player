package hans.Menu.MetadataEdit;

import com.jfoenix.controls.JFXButton;
import hans.*;
import hans.Menu.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.FileChooser;

import java.io.File;

public class MetadataEditPage {

    MenuController menuController;

    FileChooser fileChooser;

    SVGPath closeIconSVG = new SVGPath();
    SVGPath backIconSVG = new SVGPath();
    SVGPath editIconSVG = new SVGPath();
    SVGPath saveIconSVG = new SVGPath();

    StackPane closeButtonBar = new StackPane();
    StackPane closeButtonPane = new StackPane();

    StackPane footerPane = new StackPane();

    JFXButton applyButton = new JFXButton();
    Region saveIcon = new Region();

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
    EditImagePopUp editImagePopUp;


    public BooleanProperty changesMade = new SimpleBooleanProperty(false);


    boolean hasCover;
    Color newColor = null;
    Image newImage = null;

    Mp4EditPage mp4EditPage;
    Mp3EditPage mp3EditPage;
    OtherEditPage otherEditPage;


    public MetadataEditPage(MenuController menuController){
        this.menuController = menuController;

        fileChooser = new FileChooser();
        fileChooser.setTitle("Choose image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Supported images", "*.jpg", ".jpeg", ".png"));

        editIconSVG.setContent(App.svgMap.get(SVG.EDIT));
        saveIconSVG.setContent(App.svgMap.get(SVG.SAVE));

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

        Platform.runLater(() -> {
            editImageTooltip = new ControlTooltip(menuController.mainController, "Edit image", editImageButton, 1000);

            editImagePopUp = new EditImagePopUp(this);

            editImageButton.setOnAction(e -> {

                if(hasCover){
                    editImagePopUp.showOptions(menuObject);
                }
                else {
                    editImage();
                }
            });
        });

        content.setAlignment(Pos.TOP_CENTER);
        content.getChildren().addAll(closeButtonBar, imageViewWrapper, textBox, footerPane);
        content.setBackground(Background.EMPTY);
        content.setPadding(new Insets(0, 0, 20, 0));
        menuController.metadataEditScroll.setContent(content);

        textBox.setAlignment(Pos.TOP_LEFT);
        textBox.setPadding(new Insets(0, 15, 0, 15));

        footerPane.getChildren().addAll(cancelButton, applyButton);

        applyButton.setRipplerFill(Color.WHITE);
        applyButton.setText("Apply changes");
        applyButton.getStyleClass().add("mainButton");
        applyButton.setGraphicTextGap(7);
        applyButton.setPadding(new Insets(8, 10, 8, 8));
        applyButton.setCursor(Cursor.HAND);
        applyButton.setDisable(true);

        changesMade.addListener((observableValue, oldValue, newValue) -> applyButton.setDisable(!newValue));

        applyButton.setOnAction(e -> applyChanges());

        saveIcon.setShape(saveIconSVG);
        saveIcon.getStyleClass().add("menuIcon");
        saveIcon.setPrefSize(18, 18);
        saveIcon.setMaxSize(18, 18);

        applyButton.setGraphic(saveIcon);
        StackPane.setMargin(applyButton, new Insets(20, 20, 10, 0));
        StackPane.setAlignment(applyButton, Pos.CENTER_RIGHT);

        cancelButton.setRipplerFill(Color.WHITE);
        cancelButton.setCursor(Cursor.HAND);
        cancelButton.setText("Cancel");
        StackPane.setAlignment(cancelButton, Pos.CENTER_LEFT);
        cancelButton.getStyleClass().add("secondaryButton");

        StackPane.setMargin(cancelButton, new Insets(20, 0, 10, 20));
        cancelButton.setPadding(new Insets(8, 10, 8, 10));

        cancelButton.setOnAction(e -> exitMetadataEditPage());

    }

    public void enterMetadataEditPage(MenuObject menuObject){
        this.menuObject = menuObject;


        Color color;

        if(menuObject.getMediaItem().getCover() != null){
            imageView.setImage(menuObject.getMediaItem().getCover());
            color = menuObject.getMediaItem().getCoverBackgroundColor();
        }
        else {
            imageView.setImage(menuObject.getMediaItem().getPlaceholderCover());
            color = Color.rgb(64,64,64);
        }

        hasCover = menuObject.getMediaItem().hasCover();

        imageViewContainer.setStyle("-fx-background-color: rgba(" + color.getRed() * 256 +  "," + color.getGreen() * 256 + "," + color.getBlue() * 256 + ",0.7);");


        String extension = Utilities.getFileExtension(menuObject.getMediaItem().getFile());

        switch (extension) {
            case "mp4":
            case "mov":
                mp4EditPage = new Mp4EditPage(this, menuObject.getMediaItem());
                break;
            case "mp3":
                mp3EditPage = new Mp3EditPage(this, menuObject.getMediaItem());
                break;
            case "avi":
                //TODO: avi files dont allow free-form key-values so have to create custom edit page
                break;
            default:
                otherEditPage = new OtherEditPage(this, menuObject.getMediaItem());
                break;
        }


        menuController.metadataEditScroll.setVisible(true);
        menuController.queueScroll.setVisible(false);

        menuController.menuState = MenuState.METADATA_EDIT_OPEN;
    }


    public void exitMetadataEditPage(){
        this.menuObject = null;

        menuController.metadataEditScroll.setVisible(false);
        menuController.queueScroll.setVisible(true);

        changesMade.set(false);
        textBox.getChildren().clear();
        imageView.setImage(null);
        imageViewContainer.setStyle("-fx-background-color: transparent;");

        menuController.menuState = MenuState.METADATA_OPEN;
    }

    public void editImage(){
        File selectedFile = fileChooser.showOpenDialog(imageView.getScene().getWindow());
        if(selectedFile != null){
            changesMade.set(true);
            hasCover = true;
            newImage = new Image(String.valueOf(selectedFile));
            imageView.setImage(newImage);

            newColor = Utilities.findDominantColor(newImage);
            if(newColor != null) imageViewContainer.setStyle("-fx-background-color: rgba(" + newColor.getRed() * 256 +  "," + newColor.getGreen() * 256 + "," + newColor.getBlue() * 256 + ",0.7);");
        }
    }

    public void removeImage(MenuObject menuObject){
        changesMade.set(true);
        newImage = null;
        hasCover = false;
        imageView.setImage(menuObject.getMediaItem().getPlaceholderCover());
        imageViewContainer.setStyle("-fx-background-color: rgba(64,64,64,0.7);");
    }

    public void applyChanges(){
        changesMade.set(false);
        //TODO: actually make this functional

    }
}
