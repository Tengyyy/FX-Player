package hans.Menu.MetadataEdit;

import com.jfoenix.controls.JFXButton;
import hans.*;
import hans.Captions.CaptionsState;
import hans.MediaItems.MediaItem;
import hans.MediaItems.MediaUtilities;
import hans.Menu.*;
import hans.Menu.Queue.QueueItem;
import hans.Settings.SettingsState;
import io.github.palexdev.materialfx.controls.MFXProgressBar;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
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
import javafx.util.Duration;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MetadataEditPage {

    MenuController menuController;

    FileChooser fileChooser;

    SVGPath editIconSVG = new SVGPath();
    SVGPath editIconOffSVG = new SVGPath();
    SVGPath saveIconSVG = new SVGPath();

    StackPane closeButtonBar = new StackPane();
    StackPane closeButtonPane = new StackPane();

    GridPane footerPane = new GridPane();

    JFXButton applyButton = new JFXButton();
    Region saveIcon = new Region();

    JFXButton discardButton = new JFXButton();

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

    EditImagePopUp editImagePopUp;

    public MetadataEditItem metadataEditItem = null;

    boolean imageEditEnabled = false;

    StackPane progressPane = new StackPane();
    MFXProgressBar progressBar = new MFXProgressBar();
    Label savedLabel = new Label();


    ColumnConstraints column1 = new ColumnConstraints(140, 140, 140);
    ColumnConstraints column2 = new ColumnConstraints(0,100,Double.MAX_VALUE);
    ColumnConstraints column3 = new ColumnConstraints(140,140,140);

    RowConstraints row1 = new RowConstraints(90, 90, 90);

    BooleanProperty fieldsDisabledProperty = new SimpleBooleanProperty(false);

    MediaItem mediaItem = null;

    PauseTransition saveLabelTimer = new PauseTransition(Duration.millis(1000));
    Timeline progressAnimation = null;

    ChangeListener<Boolean> metadataEditActiveListener = (observableValue, oldValue, newValue) -> {
        if(newValue){
            if(saveLabelTimer.getStatus() == Animation.Status.RUNNING) saveLabelTimer.stop();
            if(progressAnimation != null && progressAnimation.getStatus() == Animation.Status.RUNNING) progressAnimation.stop();
            savedLabel.setVisible(false);
            progressBar.setVisible(true);
        }
        else {
            if(saveLabelTimer.getStatus() == Animation.Status.RUNNING) saveLabelTimer.stop();
            if(progressAnimation != null && progressAnimation.getStatus() == Animation.Status.RUNNING) progressAnimation.stop();
            progressAnimation = new Timeline(new KeyFrame(Duration.millis(300),
                    new KeyValue(progressBar.progressProperty(), 1, Interpolator.LINEAR)));
            progressAnimation.setOnFinished(e -> {
                progressBar.setVisible(false);
                progressBar.setProgress(0);
                savedLabel.setVisible(true);
                saveLabelTimer.playFromStart();
            });
            progressAnimation.playFromStart();
        }
    };

    ChangeListener<Number> progressListener = (observableValue, oldValue, newValue) -> {
        if(newValue.doubleValue() <= 0  || !mediaItem.metadataEditActive.get()) return;
        if(progressAnimation != null && progressAnimation.getStatus() == Animation.Status.RUNNING) progressAnimation.stop();
        progressAnimation = new Timeline(new KeyFrame(Duration.millis(300),
                new KeyValue(progressBar.progressProperty(), newValue, Interpolator.LINEAR)));
        progressAnimation.playFromStart();
    };


    public MetadataEditPage(MenuController menuController){


        this.menuController = menuController;

        fileChooser = new FileChooser();
        fileChooser.setTitle("Choose image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Supported images", "*.jpg", "*.jpeg", "*.png"));

        editIconSVG.setContent(App.svgMap.get(SVG.EDIT));
        editIconOffSVG.setContent(App.svgMap.get(SVG.EDIT_OFF));
        saveIconSVG.setContent(App.svgMap.get(SVG.SAVE));


        closeButtonPane.setPrefSize(50, 50);
        closeButtonPane.setMaxSize(50, 50);
        StackPane.setAlignment(closeButtonPane, Pos.CENTER_RIGHT);

        closeButtonBar.setPrefHeight(60);
        closeButtonBar.setMinHeight(60);
        closeButtonBar.getChildren().addAll(closeButtonPane);

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
            AnimationsClass.animateBackgroundColor(editImageIcon, (Color) editImageIcon.getBackground().getFills().get(0).getFill(), Color.rgb(255, 255, 255), 200);
        });
        editImageButtonWrapper.setOnMouseExited(e -> {
            AnimationsClass.fadeAnimation(200, editImageButton, editImageButton.getOpacity(), 0, false, 1, true);
            AnimationsClass.animateBackgroundColor(editImageIcon, (Color) editImageIcon.getBackground().getFills().get(0).getFill(), Color.rgb(200, 200, 200), 200);
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
        editImageButton.disableProperty().bind(fieldsDisabledProperty);

        editImageButton.setOnAction(e -> editImageButtonClick());

        Platform.runLater(() -> {
            editImageTooltip = new ControlTooltip(menuController.mainController, "Edit cover", editImageButton, 1000);
            editImagePopUp = new EditImagePopUp(this);
        });

        content.setAlignment(Pos.TOP_CENTER);
        content.getChildren().addAll(closeButtonBar, imageViewWrapper, textBox, footerPane);
        content.setBackground(Background.EMPTY);
        menuController.metadataEditScroll.setContent(content);

        textBox.setAlignment(Pos.TOP_LEFT);
        textBox.setPadding(new Insets(0, 15, 0, 15));

        footerPane.add(discardButton, 0, 0);
        footerPane.add(progressPane, 1, 0);
        footerPane.add(applyButton, 2, 0);


        footerPane.setPadding(new Insets(20, 12, 10, 10));
        column2.setHgrow(Priority.ALWAYS);
        footerPane.getColumnConstraints().addAll(column1, column2, column3);
        footerPane.getRowConstraints().addAll(row1);


        GridPane.setValignment(discardButton, VPos.CENTER);
        GridPane.setValignment(progressPane, VPos.CENTER);
        GridPane.setValignment(applyButton, VPos.CENTER);


        GridPane.setHalignment(discardButton, HPos.CENTER);
        GridPane.setHalignment(progressPane, HPos.CENTER);
        GridPane.setHalignment(applyButton, HPos.CENTER);

        applyButton.setRipplerFill(Color.WHITE);
        applyButton.setText("Save changes");
        applyButton.getStyleClass().add("mainButton");
        applyButton.setCursor(Cursor.HAND);
        applyButton.setDisable(true);
        applyButton.setOnAction(e -> {
            if(menuController.captionsController.captionsState != CaptionsState.CLOSED) menuController.captionsController.closeCaptions();
            if(menuController.settingsController.settingsState != SettingsState.CLOSED) menuController.settingsController.closeSettings();

            saveMetadata();
        });
        applyButton.setDisable(true);

        saveIcon.setShape(saveIconSVG);
        saveIcon.getStyleClass().add("menuIcon");
        saveIcon.setPrefSize(18, 18);
        saveIcon.setMaxSize(18, 18);
        applyButton.setGraphic(saveIcon);

        discardButton.setRipplerFill(Color.TRANSPARENT);
        discardButton.setCursor(Cursor.HAND);
        discardButton.setText("Discard changes");
        discardButton.getStyleClass().add("menuButton");
        discardButton.setDisable(true);

        discardButton.setOnAction(e -> {
            if(menuController.captionsController.captionsState != CaptionsState.CLOSED) menuController.captionsController.closeCaptions();
            if(menuController.settingsController.settingsState != SettingsState.CLOSED) menuController.settingsController.closeSettings();

            if(mediaItem.metadataEditActive.get()) return;
            reloadMetadata();
        });


        progressPane.getChildren().addAll(progressBar, savedLabel);
        progressPane.setPadding(new Insets(0, 20, 0, 20));
        savedLabel.setText("SAVED");
        savedLabel.getStyleClass().add("savedLabel");
        savedLabel.setVisible(false);
        StackPane.setAlignment(savedLabel, Pos.CENTER);

        saveLabelTimer.setOnFinished(e -> savedLabel.setVisible(false));

        progressBar.setMaxWidth(250);
        progressBar.setPrefHeight(11);
        progressBar.setVisible(false);
        StackPane.setAlignment(progressBar, Pos.CENTER);

    }

    public void enterMetadataEditPage(MediaItem mediaItem){

        if(mediaItem == null) return;

        this.mediaItem = mediaItem;

        progressBar.setProgress(mediaItem.metadataEditProgress.get());
        if(mediaItem.metadataEditActive.get()) progressBar.setVisible(true);
        mediaItem.metadataEditProgress.addListener(progressListener);
        mediaItem.metadataEditActive.addListener(metadataEditActiveListener);
        applyButton.disableProperty().bind(mediaItem.changesMade.not());
        discardButton.disableProperty().bind(mediaItem.changesMade.not());
        fieldsDisabledProperty.bind(mediaItem.metadataEditActive);


        if(mediaItem.newCoverImage != null){
            imageView.setImage(mediaItem.newCoverImage);
            imageViewContainer.setStyle("-fx-background-color: rgba(" + mediaItem.newColor.getRed() * 256 +  "," + mediaItem.newColor.getGreen() * 256 + "," + mediaItem.newColor.getBlue() * 256 + ",0.7);");
        }
        else if(mediaItem.coverRemoved || mediaItem.getCover() == null){
            imageView.setImage(mediaItem.getPlaceholderCover());
            imageViewContainer.setStyle("-fx-background-color: red;");
        }
        else {
            imageView.setImage(mediaItem.getCover());
            Color color = mediaItem.getCoverBackgroundColor();
            imageViewContainer.setStyle("-fx-background-color: rgba(" + color.getRed() * 256 +  "," + color.getGreen() * 256 + "," + color.getBlue() * 256 + ",0.7);");
        }

        String extension = Utilities.getFileExtension(mediaItem.getFile());

        switch (extension) {
            case "mp4", "mov" -> {
                metadataEditItem = mediaItem.changesMade.get() ? new Mp4EditItem(this, mediaItem.newMetadata) : new Mp4EditItem(this, mediaItem.getMediaInformation());
                enableImageEdit();
            }
            case "m4a" -> {
                metadataEditItem = mediaItem.changesMade.get() ? new M4aEditItem(this, mediaItem.newMetadata) : new M4aEditItem(this, mediaItem.getMediaInformation());
                enableImageEdit();
            }
            case "mp3", "aiff" -> {
                metadataEditItem = mediaItem.changesMade.get() ? new Mp3EditItem(this, mediaItem.newMetadata) : new Mp3EditItem(this, mediaItem.getMediaInformation());
                enableImageEdit();
            }
            case "aac" -> {
                metadataEditItem = mediaItem.changesMade.get() ? new Mp3EditItem(this, mediaItem.newMetadata) : new Mp3EditItem(this, mediaItem.getMediaInformation());
                disableImageEdit();
            }
            case "flac" -> {
                metadataEditItem = mediaItem.changesMade.get() ? new FlacEditItem(this, mediaItem.newMetadata) : new FlacEditItem(this, mediaItem.getMediaInformation());
                enableImageEdit();
            }
            case "ogg", "opus" -> {
                metadataEditItem = mediaItem.changesMade.get() ? new OggEditItem(this, mediaItem.newMetadata) : new OggEditItem(this, mediaItem.getMediaInformation());
                disableImageEdit();
            }
            case "avi" -> {
                metadataEditItem = mediaItem.changesMade.get() ? new AviEditItem(this, mediaItem.newMetadata) : new AviEditItem(this, mediaItem.getMediaInformation());
                disableImageEdit();
            }
            case "flv", "wma" -> {
                metadataEditItem = mediaItem.changesMade.get() ? new OtherEditItem(this, mediaItem.newMetadata) : new OtherEditItem(this, mediaItem.getMediaInformation());
                disableImageEdit();
            }
            case "wav" -> {
                metadataEditItem = mediaItem.changesMade.get() ? new WavEditItem(this, mediaItem.newMetadata) : new WavEditItem(this, mediaItem.getMediaInformation());
                disableImageEdit();
            }
            default -> {
                metadataEditItem = mediaItem.changesMade.get() ? new OtherEditItem(this, mediaItem.newMetadata) : new OtherEditItem(this, mediaItem.getMediaInformation());
                enableImageEdit();
            }
        }


        menuController.metadataEditScroll.setVisible(true);
        menuController.queueContainer.setVisible(false);

        if(menuController.menuState == MenuState.CLOSED) menuController.openMenu();

        menuController.menuState = MenuState.METADATA_EDIT_OPEN;
    }

    public void exitMetadataEditPage(){

        mediaItem.metadataEditProgress.removeListener(progressListener);
        mediaItem.metadataEditActive.removeListener(metadataEditActiveListener);
        if(progressAnimation != null && progressAnimation.getStatus() == Animation.Status.RUNNING) progressAnimation.stop();
        if(saveLabelTimer.getStatus() == Animation.Status.RUNNING) saveLabelTimer.stop();
        progressBar.setProgress(0);
        applyButton.disableProperty().unbind();
        editImageButton.disableProperty().unbind();
        discardButton.disableProperty().unbind();
        fieldsDisabledProperty.unbind();

        savedLabel.setVisible(false);
        progressBar.setVisible(false);

        if(!mediaItem.metadataEditActive.get() && mediaItem.changesMade.get()){
            mediaItem.newMetadata = metadataEditItem.createMetadataMap();
        }

        metadataEditItem = null;

        menuController.metadataEditScroll.setVisible(false);
        menuController.queueContainer.setVisible(true);

        textBox.getChildren().clear();
        imageView.setImage(null);
        imageViewContainer.setStyle("-fx-background-color: transparent;");

        menuController.menuState = MenuState.QUEUE_OPEN;
    }

    private void enableImageEdit(){
        imageEditEnabled = true;
        editImageButton.setOnAction(e -> {
            if(menuController.captionsController.captionsState != CaptionsState.CLOSED) menuController.captionsController.closeCaptions();
            if(menuController.settingsController.settingsState != SettingsState.CLOSED) menuController.settingsController.closeSettings();

            editImageButtonClick();
        });
        editImageIcon.setShape(editIconSVG);
        editImageTooltip.updateDelay(Duration.seconds(1));
        editImageTooltip.updateText("Edit cover");
    }

    private void disableImageEdit(){
        imageEditEnabled = false;
        editImageButton.setOnAction(e -> {
            if(menuController.captionsController.captionsState != CaptionsState.CLOSED) menuController.captionsController.closeCaptions();
            if(menuController.settingsController.settingsState != SettingsState.CLOSED) menuController.settingsController.closeSettings();
        });
        editImageIcon.setShape(editIconOffSVG);
        editImageTooltip.updateDelay(Duration.ZERO);
        editImageTooltip.updateText("Cover editing unavailable for this media format");
    }

    private void editImageButtonClick(){

        if(mediaItem.metadataEditActive.get()) return;
        if(mediaItem.newCoverImage != null || (mediaItem.getCover() != null && !mediaItem.coverRemoved)){
            if(editImagePopUp.isShowing()) editImagePopUp.hide();
            else editImagePopUp.showOptions(mediaItem);
        }
        else editImage();
    }

    public void editImage(){
        File selectedFile = fileChooser.showOpenDialog(imageView.getScene().getWindow());
        if(selectedFile != null && !mediaItem.metadataEditActive.get()){
            mediaItem.coverRemoved = false;
            mediaItem.newCoverImage = new Image(String.valueOf(selectedFile));
            mediaItem.newCoverFile = selectedFile;
            imageView.setImage(mediaItem.newCoverImage);

            mediaItem.newColor = MediaUtilities.findDominantColor(mediaItem.newCoverImage);
            if(mediaItem.newColor != null) imageViewContainer.setStyle("-fx-background-color: rgba(" + mediaItem.newColor.getRed() * 256 +  "," + mediaItem.newColor.getGreen() * 256 + "," + mediaItem.newColor.getBlue() * 256 + ",0.7);");

            mediaItem.changesMade.set(true);
        }
    }

    public void removeImage(){
        if(mediaItem.metadataEditActive.get()) return;

        mediaItem.coverRemoved = true;
        mediaItem.newCoverImage = null;
        mediaItem.newColor = null;
        mediaItem.newCoverFile = null;
        imageView.setImage(mediaItem.getPlaceholderCover());
        imageViewContainer.setStyle("-fx-background-color: red;");

        mediaItem.changesMade.set(true);
    }

    public void saveMetadata(){

        if(mediaItem.metadataEditActive.get() || !mediaItem.changesMade.get()) return;

        mediaItem.newMetadata = metadataEditItem.createMetadataMap();

        if(menuController.queuePage.queueBox.activeItem.get() != null && menuController.queuePage.queueBox.activeItem.get().getMediaItem() == mediaItem){
            menuController.mediaInterface.resetMediaPlayer();
        }

        MetadataEditTask metadataEditTask = new MetadataEditTask(mediaItem);
        metadataEditTask.setOnSucceeded(e -> {
            if(metadataEditTask.getValue()){
                for(QueueItem queueItem : menuController.queuePage.queueBox.queue){
                    if(queueItem.getMediaItem() == mediaItem){
                        for(Map.Entry<String, String> entry : mediaItem.getMediaInformation().entrySet()){
                            System.out.println(entry.getKey());
                            System.out.println(entry.getValue());
                        }
                        queueItem.update();
                    }
                }

                menuController.mainController.getControlBarController().updateNextAndPreviousVideoButtons();

                if(menuController.queuePage.queueBox.activeItem.get() != null && menuController.queuePage.queueBox.activeItem.get().getMediaItem() == mediaItem) menuController.mediaInterface.createMedia(menuController.queuePage.queueBox.activeItem.get());
            }
        });

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(metadataEditTask);
        executorService.shutdown();

    }

    private void reloadMetadata(){

        if(mediaItem.metadataEditActive.get()) return;

        mediaItem.changesMade.set(false);
        mediaItem.metadataEditActive.set(false);
        mediaItem.metadataEditProgress.set(0);
        mediaItem.newMetadata = null;
        mediaItem.coverRemoved = false;
        mediaItem.newCoverImage = null;
        mediaItem.newColor = null;
        mediaItem.newCoverFile = null;

        textBox.getChildren().clear();
        imageView.setImage(null);
        imageViewContainer.setStyle("-fx-background-color: transparent;");

        enterMetadataEditPage(mediaItem);
    }

}
