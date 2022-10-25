package hans.Menu;

import com.jfoenix.controls.JFXButton;
import hans.*;
import hans.MediaItems.MediaItem;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.FileChooser;

import java.io.File;
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

    JFXButton saveButton = new JFXButton();
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


    MetadataEditPage(MenuController menuController){
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
        textBox.setSpacing(15);

        footerPane.getChildren().addAll(cancelButton, saveButton);

        saveButton.setRipplerFill(Color.WHITE);
        saveButton.setText("Save changes");
        saveButton.getStyleClass().add("mainButton");
        saveButton.setGraphicTextGap(7);
        saveButton.setPadding(new Insets(8, 10, 8, 8));
        saveButton.setCursor(Cursor.HAND);
        saveButton.setDisable(true);

        changesMade.addListener((observableValue, oldValue, newValue) -> {
            saveButton.setDisable(!newValue);
        });

        saveButton.setOnAction(e -> saveChanges());

        saveIcon.setShape(saveIconSVG);
        saveIcon.getStyleClass().add("menuIcon");
        saveIcon.setPrefSize(18, 18);
        saveIcon.setMaxSize(18, 18);

        saveButton.setGraphic(saveIcon);
        StackPane.setMargin(saveButton, new Insets(20, 20, 10, 0));
        StackPane.setAlignment(saveButton, Pos.CENTER_RIGHT);

        cancelButton.setRipplerFill(Color.WHITE);
        cancelButton.setCursor(Cursor.HAND);
        cancelButton.setText("Cancel");
        StackPane.setAlignment(cancelButton, Pos.CENTER_LEFT);
        cancelButton.getStyleClass().add("secondaryButton");

        StackPane.setMargin(cancelButton, new Insets(20, 0, 10, 20));
        cancelButton.setPadding(new Insets(8, 10, 8, 10));

        cancelButton.setOnAction(e -> {
            exitMetadataEditPage();
        });

    }

    public void enterMetadataEditPage(MenuObject menuObject){
        this.menuObject = menuObject;


        Color color;

        if(menuObject.getMediaItem().hasCover()){
            hasCover = true;
            imageView.setImage(menuObject.getMediaItem().getCover());
            color = menuObject.getMediaItem().getCoverBackgroundColor();
        }
        else {
            hasCover = false;
            imageView.setImage(menuObject.getMediaItem().getPlaceholderCover());
            color = Color.rgb(64,64,64);
        }

        imageViewContainer.setStyle("-fx-background-color: rgba(" + color.getRed() * 256 +  "," + color.getGreen() * 256 + "," + color.getBlue() * 256 + ",0.7);");


        String extension = Utilities.getFileExtension(menuObject.getMediaItem().getFile());

        switch (extension) {
            case "mp4":
            case "mov":
                if(menuObject.getMediaItem().getMediaInformation().containsKey("media_type")){
                    String type = menuObject.getMediaItem().getMediaInformation().get("media_type");

                    switch (type) {
                        case "6":
                            createMp4(menuObject.getMediaItem().getMediaInformation(), "Music video");
                            break;
                        case "9":
                            createMp4(menuObject.getMediaItem().getMediaInformation(), "Movie");
                            break;
                        case "10":
                            createMp4(menuObject.getMediaItem().getMediaInformation(), "TV Show");
                            break;
                        case "21":
                            createMp4(menuObject.getMediaItem().getMediaInformation(), "Podcast");
                            break;
                        default:
                            createMp4(menuObject.getMediaItem().getMediaInformation(), "Home video");
                            break;
                    }
                }
                else {
                    createMp4(menuObject.getMediaItem().getMediaInformation(), "Home video");
                }
                break;
            case "mp3":
                createMp3(menuObject.getMediaItem());
                break;
            case "avi":
                //createAvi(menuObject.getMediaItem());
                break;
            default:
                //createOther(menuObject.getMediaItem());
                break;
        }


        menuController.metadataEditScroll.setVisible(true);
        menuController.metadataScroll.setVisible(false);

        menuController.menuState = MenuState.METADATA_EDIT_OPEN;
    }


    public void exitMetadataEditPage(){
        this.menuObject = null;

        menuController.metadataEditScroll.setVisible(false);
        menuController.metadataScroll.setVisible(true);

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

    public void saveChanges(){
        changesMade.set(false);
        //TODO: actually make this functional

    }

    private void createMp4(Map<String, String> metadata, String mediaType){

        if(metadata != null){

            createTextArea("Title", metadata.containsKey("title") && !metadata.get("title").trim().isEmpty() ? metadata.get("title") : "");
            ComboBox<String> comboBox = createComboBox(mediaType, "Music video", "Movie", "TV Show", "Podcast", "Home video");

            if(mediaType.equals("TV Show")){
                // TV Show fields
                createTextArea("Series title", metadata.containsKey("show") && !metadata.get("show").trim().isEmpty() ? metadata.get("show") : "");
                createTextField("Season number", metadata.containsKey("season_number") && !metadata.get("season_number").trim().isEmpty() ? metadata.get("season_number") : "");
                createTextField("Episode number", metadata.containsKey("episode_sort") && !metadata.get("episode_sort").trim().isEmpty() ? metadata.get("episode_sort") : "");
                createTextArea("Network", metadata.containsKey("network") && !metadata.get("network").trim().isEmpty() ? metadata.get("network") : "");
            }



            if(mediaType.equals("TV Show") || mediaType.equals("Movie")){
                createTextArea("Cast", metadata.containsKey("artist") && !metadata.get("artist").trim().isEmpty() ? metadata.get("artist") : "");
            }
            else {
                createTextArea("Artist", metadata.containsKey("artist") && !metadata.get("artist").trim().isEmpty() ? metadata.get("artist") : "");
            }

            if(mediaType.equals("Music video")){
                createTrackField(metadata.containsKey("track") && !metadata.get("track").trim().isEmpty() ? metadata.get("track") : "");
            }

            if(mediaType.equals("Music video")){
                createTextArea("Album", metadata.containsKey("album") && !metadata.get("album").trim().isEmpty() ? metadata.get("album") : "");
            }

            if(mediaType.equals("TV Show") || mediaType.equals("Movie")){
                createTextArea("Director", metadata.containsKey("album_artist") && !metadata.get("album_artist").trim().isEmpty() ? metadata.get("album_artist") : "");
            }
            else if(mediaType.equals("Music video")){
                createTextArea("Album artist", metadata.containsKey("album_artist") && !metadata.get("album_artist").trim().isEmpty() ? metadata.get("album_artist") : "");
            }

            if(mediaType.equals("TV Show") || mediaType.equals("Movie")){
                createTextArea("Writers", metadata.containsKey("composer") && !metadata.get("composer").trim().isEmpty() ? metadata.get("composer") : "");
            }
            else if(mediaType.equals("Music video")){
                createTextArea("Composer", metadata.containsKey("composer") && !metadata.get("composer").trim().isEmpty() ? metadata.get("composer") : "");
            }

            createTextArea("Genre", metadata.containsKey("genre") && !metadata.get("genre").trim().isEmpty() ? metadata.get("genre") : "");

            createTextArea("Description", metadata.containsKey("description") && !metadata.get("description").trim().isEmpty() ? metadata.get("description") : "");

            createTextArea("Synopsis", metadata.containsKey("synopsis") && !metadata.get("synopsis").trim().isEmpty() ? metadata.get("synopsis") : "");


            if(mediaType.equals("Music video")){
                createTextArea("Lyrics", metadata.containsKey("lyrics") && !metadata.get("lyrics").trim().isEmpty() ? metadata.get("lyrics") : "");
            }

            createTextArea("Release date", metadata.containsKey("date") && !metadata.get("date").trim().isEmpty() ? metadata.get("date") : "");


            createTextArea("Comment", metadata.containsKey("comment") && !metadata.get("comment").trim().isEmpty() ? metadata.get("comment") : "");
        }
    }

    private void createMp3(MediaItem mediaItem){

    }

    private void createTextField(String key, String value){

        Label keyLabel = new Label(key);
        keyLabel.getStyleClass().add("metadataKey");
        VBox.setMargin(keyLabel, new Insets(0, 0, 3, 0));

        TextField textField = new TextField(value);
        textField.textProperty().addListener((observableValue, s, t1) -> {
            changesMade.set(true);
        });
        textField.setPrefHeight(36);
        textField.setMinHeight(36);
        textField.setMaxHeight(36);

        VBox vBox = new VBox(keyLabel, textField);

        textBox.getChildren().add(vBox);

    }

    private void createTrackField(String value){
        Label label = new Label("Track number");
        label.getStyleClass().add("metadataKey");

        String firstValue = "";
        String secondValue = "";

        if(value.contains("/") && value.indexOf('/') > 0 && value.indexOf('/') < value.length() - 1){
            firstValue = value.substring(0, value.indexOf('/'));
            secondValue = value.substring(value.indexOf('/'));
        }
        else {
            firstValue = value;
        }

        TextField textField1 = new TextField(firstValue);
        textField1.textProperty().addListener((observableValue, s, t1) -> {
            changesMade.set(true);
        });
        textField1.setPrefHeight(36);
        textField1.setMinHeight(36);
        textField1.setMaxHeight(36);

        Label slash = new Label("/");
        slash.getStyleClass().add("metadataKey");

        TextField textField2 = new TextField(secondValue);
        textField2.textProperty().addListener((observableValue, s, t1) -> {
            changesMade.set(true);
        });
        textField2.setPrefHeight(36);
        textField2.setMinHeight(36);
        textField2.setMaxHeight(36);

        HBox hBox = new HBox(textField1, slash, textField2);
        hBox.setSpacing(5);


        VBox vBox = new VBox(label, hBox);

        textBox.getChildren().add(vBox);
    }

    private ComboBox<String> createComboBox(String initialValue, String... values){

        Label label = new Label("Media type");
        label.getStyleClass().add("metadataKey");
        VBox.setMargin(label, new Insets(0, 0, 3, 0));

        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setMinHeight(36);
        comboBox.setPrefHeight(36);
        comboBox.setMaxHeight(36);
        for(String value : values){
            comboBox.getItems().add(value);
        }
        comboBox.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            System.out.println(oldValue);
            System.out.println(newValue);
            changesMade.set(true);
        });

        comboBox.setValue(initialValue);

        VBox vBox = new VBox(label, comboBox);
        textBox.getChildren().add(vBox);

        return comboBox;
    }

    private void createTextArea(String key, String value){
        Label keyLabel = new Label(key);
        VBox.setMargin(keyLabel, new Insets(0, 0, 3, 0));
        keyLabel.getStyleClass().add("metadataKey");

        ExpandableTextArea textArea = new ExpandableTextArea();
        textArea.setText(value);
        /*textArea.textProperty().addListener((observableValue, s, t1) -> {
            changesMade.set(true);
        });*/

        VBox vBox = new VBox(keyLabel, textArea);
        textBox.getChildren().add(vBox);

    }

}
