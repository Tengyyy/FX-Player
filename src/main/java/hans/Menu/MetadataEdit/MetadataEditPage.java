package hans.Menu.MetadataEdit;

import com.jfoenix.controls.JFXButton;
import hans.*;
import hans.MediaItems.AudioItem;
import hans.Menu.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

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
    public boolean imageRemoved = false;
    public Color newColor = null;
    public Image newImage = null;
    public File newFile = null;

    public MetadataEditItem metadataEditItem = null;

    MetadataExitConfirmation metadataExitConfirmation;


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

        backButton.setOnAction(e -> requestExitMetadataEditPage(false));

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

        closeButton.setOnAction(e -> {
            requestExitMetadataEditPage(true);
        });

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
        applyButton.setOnAction(e -> {
            saveMetadata();
        });

        changesMade.addListener((observableValue, oldValue, newValue) -> applyButton.setDisable(!newValue));

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

        cancelButton.setOnAction(e -> requestExitMetadataEditPage(false));

        metadataExitConfirmation = new MetadataExitConfirmation(menuController, this);

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
                metadataEditItem = new Mp4EditItem(this, menuObject.getMediaItem());
                break;
            case "mp3":
            case "flac":
                metadataEditItem = new AudioEditItem(this, (AudioItem) menuObject.getMediaItem());
                break;
            case "avi":
                metadataEditItem = new AviEditItem(this, menuObject.getMediaItem());
                break;
            default:
                //TODO: find out if ffmpeg supports metadata for wav files
                metadataEditItem = new OtherEditItem(this, menuObject.getMediaItem());
                break;
        }


        menuController.metadataEditScroll.setVisible(true);
        menuController.queueScroll.setVisible(false);

        if(menuController.menuState == MenuState.CLOSED) menuController.mainController.openMenu();

        menuController.menuState = MenuState.METADATA_EDIT_OPEN;
    }

    public void requestExitMetadataEditPage(boolean closeMenu){
        if(changesMade.get()){
            metadataExitConfirmation.show(closeMenu);
        }
        else {
            if(closeMenu) menuController.closeMenu();
            else exitMetadataEditPage();
        }
    }

    public void exitMetadataEditPage(){
        this.menuObject = null;

        imageRemoved = false;
        metadataEditItem = null;
        newImage = null;
        newColor = null;
        newFile = null;

        menuController.metadataEditScroll.setVisible(false);
        menuController.queueScroll.setVisible(true);

        changesMade.set(false);
        textBox.getChildren().clear();
        imageView.setImage(null);
        imageViewContainer.setStyle("-fx-background-color: transparent;");

        menuController.menuState = MenuState.QUEUE_OPEN;
    }

    public void editImage(){
        File selectedFile = fileChooser.showOpenDialog(imageView.getScene().getWindow());
        if(selectedFile != null){
            imageRemoved = false;
            hasCover = true;
            newImage = new Image(String.valueOf(selectedFile));
            newFile = selectedFile;
            imageView.setImage(newImage);

            newColor = Utilities.findDominantColor(newImage);
            if(newColor != null) imageViewContainer.setStyle("-fx-background-color: rgba(" + newColor.getRed() * 256 +  "," + newColor.getGreen() * 256 + "," + newColor.getBlue() * 256 + ",0.7);");

            changesMade.set(true);
        }
    }

    public void removeImage(MenuObject menuObject){
        imageRemoved = true;
        newImage = null;
        newColor = null;
        newFile = null;
        hasCover = false;
        imageView.setImage(menuObject.getMediaItem().getPlaceholderCover());
        imageViewContainer.setStyle("-fx-background-color: rgba(64,64,64,0.7);");

        changesMade.set(true);
    }

    public void saveMetadata(){
        Map<String,String> mediaInformation = metadataEditItem.saveMetadata();

        boolean metadataEditSuccess = menuObject.getMediaItem().setMediaInformation(mediaInformation, true);

        boolean imageEditSuccess = true;
        if(imageRemoved){
            imageEditSuccess = menuObject.getMediaItem().setCover(null, null, true);
            if(imageEditSuccess){
                menuObject.getMediaItem().setCoverBackgroundColor(null);
                menuObject.getMediaItem().setHasCover(false);
            }
        }
        else if(newImage != null){
            imageEditSuccess = menuObject.getMediaItem().setCover(newFile, newImage, true);
            if(imageEditSuccess){
                menuObject.getMediaItem().setCoverBackgroundColor(newColor);
                menuObject.getMediaItem().setHasCover(true);
            }
        }

        if(metadataEditSuccess && imageEditSuccess){
            menuObject.update();
            ArrayList<MenuObject> duplicates = Utilities.findDuplicates(menuObject, menuController);

            for(MenuObject duplicate : duplicates){
                duplicate.getMediaItem().setMediaInformation(mediaInformation, false);
                if(imageRemoved){
                    duplicate.getMediaItem().setCover(null, null, false);
                    duplicate.getMediaItem().setCoverBackgroundColor(null);
                    duplicate.getMediaItem().setHasCover(false);
                }
                else if(newImage != null){
                    duplicate.getMediaItem().setCover(newFile, newImage, false);
                    duplicate.getMediaItem().setCoverBackgroundColor(newColor);
                    duplicate.getMediaItem().setHasCover(true);
                }
                duplicate.update();
            }

            menuController.mainController.getControlBarController().updateTooltips();
            if(menuController.activeItem != null && menuController.activeItem == menuObject){
                menuController.mainController.videoTitleLabel.setText(menuObject.getTitle());
            }

            if(!menuObject.getMediaItem().hasVideo()){
                if(menuController.mainController.miniplayerActive){
                    if(menuObject.getMediaItem().hasCover()) menuController.mainController.miniplayer.miniplayerController.videoImageView.setImage(menuObject.getMediaItem().getCover());
                    else menuController.mainController.miniplayer.miniplayerController.videoImageView.setImage(menuObject.getMediaItem().getPlaceholderCover());
                }
                else {
                    if(menuObject.getMediaItem().hasCover()) menuController.mainController.videoImageView.setImage(menuObject.getMediaItem().getCover());
                    else menuController.mainController.videoImageView.setImage(menuObject.getMediaItem().getPlaceholderCover());
                }
            }

            changesMade.set(false);
        }

    }

}
