package tengy.Menu.MediaInformation;

import tengy.*;
import tengy.MediaItems.MediaItem;
import tengy.MediaItems.MediaUtilities;
import tengy.Menu.MenuController;
import tengy.Menu.MenuState;
import tengy.Menu.Queue.QueueItem;
import tengy.PlaybackSettings.PlaybackSettingsState;
import tengy.Subtitles.SubtitlesState;
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
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MediaInformationPage {

    MenuController menuController;

    VBox mediaInformationWrapper = new VBox();
    ScrollPane mediaInformationScroll = new ScrollPane();

    StackPane titlePane = new StackPane();
    Label title = new Label("Media information");

    FileChooser fileChooser;

    SVGPath editIconSVG = new SVGPath();
    SVGPath editIconOffSVG = new SVGPath();
    SVGPath saveIconSVG = new SVGPath();

    GridPane footerPane = new GridPane();

    HBox saveButtonContainer = new HBox();

    Button saveButton = new Button();
    Region saveIcon = new Region();

    Button saveOptionsButton = new Button();
    Region chevronUpIcon = new Region();
    SVGPath chevronUpSVG = new SVGPath();

    Button discardButton = new Button();

    public VBox content = new VBox();

    StackPane imageViewWrapper = new StackPane();
    public StackPane imageViewContainer = new StackPane();
    public ImageView imageView = new ImageView();
    StackPane imageFilter = new StackPane();
    StackPane editImageButtonWrapper = new StackPane();
    Button editImageButton = new Button();
    Region editImageIcon = new Region();
    ControlTooltip editImageTooltip;
    boolean imageHover = false;

    public VBox textBox = new VBox();

    EditImagePopUp editImagePopUp;

    public MediaInformationItem mediaInformationItem = null;

    boolean imageEditEnabled = false;

    StackPane popup = new StackPane();
    StackPane popupTitleBar = new StackPane();
    StackPane closeButtonPane = new StackPane();
    Button closeButton = new Button();
    Region closeButtonIcon = new Region();
    SVGPath closeSVG = new SVGPath();
    Label popupTitle = new Label();
    VBox popupBody = new VBox();


    StackPane progressPane = new StackPane();
    MFXProgressBar progressBar = new MFXProgressBar();
    Label savedLabel = new Label();


    ColumnConstraints column1 = new ColumnConstraints(170, 170, 170);
    ColumnConstraints column2 = new ColumnConstraints(0,100,Double.MAX_VALUE);
    ColumnConstraints column3 = new ColumnConstraints(170,170,170);

    RowConstraints row1 = new RowConstraints(90, 90, 90);

    BooleanProperty fieldsDisabledProperty = new SimpleBooleanProperty(false);

    MediaItem mediaItem = null;

    PauseTransition saveLabelTimer = new PauseTransition(Duration.millis(1000));
    Timeline progressAnimation = null;

    PauseTransition popupTimer = new PauseTransition(Duration.millis(5000));
    FadeTransition popupFadeOut;
    FadeTransition popupFadeIn;

    boolean savingToNewFile = false;

    ChangeListener<Boolean> editActiveListener = (observableValue, oldValue, newValue) -> {
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
                if(savingToNewFile) showPopup();
                savingToNewFile = false;
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

    SaveOptionsContextMenu saveOptionsContextMenu;


    public MediaInformationPage(MenuController menuController){


        this.menuController = menuController;

        fileChooser = new FileChooser();
        fileChooser.setTitle("Choose image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Supported images", "*.jpg", "*.jpeg", "*.png"));

        editIconSVG.setContent(SVG.EDIT.getContent());
        editIconOffSVG.setContent(SVG.EDIT_OFF.getContent());
        saveIconSVG.setContent(SVG.SAVE.getContent());

        titlePane.getChildren().addAll(title);
        titlePane.setPadding(new Insets(55, 50, 20, 50));

        StackPane.setAlignment(title, Pos.CENTER_LEFT);
        title.getStyleClass().add("menuTitle");


        mediaInformationScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        mediaInformationScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        mediaInformationScroll.getStyleClass().add("menuScroll");
        mediaInformationScroll.setFitToWidth(true);
        mediaInformationScroll.setFitToHeight(true);
        mediaInformationScroll.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        mediaInformationScroll.setBackground(Background.EMPTY);

        content.setAlignment(Pos.TOP_CENTER);
        content.getChildren().addAll(imageViewWrapper, textBox, footerPane);
        content.setBackground(Background.EMPTY);
        content.setPadding(new Insets(0, 50, 20, 50));
        mediaInformationScroll.setContent(content);

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
            imageHover = true;
            AnimationsClass.fadeAnimation(200, imageFilter, imageFilter.getOpacity(), 0.5, false, 1, true);
            AnimationsClass.fadeAnimation(200, editImageIcon, editImageIcon.getOpacity(), 1, false, 1, true);
        });
        imageViewContainer.setOnMouseExited(e -> {
            imageHover = false;

            if(!editImagePopUp.isShowing()){
                AnimationsClass.fadeAnimation(200, imageFilter, imageFilter.getOpacity(), 0, false, 1, true);
                AnimationsClass.fadeAnimation(200, editImageIcon, editImageIcon.getOpacity(), 0, false, 1, true);
            }
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
        editImageButton.setId("editImageButton");
        editImageButton.setOpacity(0);
        editImageButton.setCursor(Cursor.HAND);
        editImageButton.disableProperty().bind(fieldsDisabledProperty);

        editImageButton.setOnAction(e -> editImageButtonClick());

        Platform.runLater(() -> {
            editImageTooltip = new ControlTooltip(menuController.mainController, "Edit cover", "", editImageButton, 1000);
            editImagePopUp = new EditImagePopUp(this);
            saveOptionsContextMenu = new SaveOptionsContextMenu(this);
        });

        textBox.setAlignment(Pos.TOP_LEFT);
        textBox.setPadding(new Insets(0, 15, 0, 15));

        footerPane.add(discardButton, 0, 0);
        footerPane.add(progressPane, 1, 0);
        footerPane.add(saveButtonContainer, 2, 0);

        footerPane.setPadding(new Insets(20, 12, 10, 10));
        column2.setHgrow(Priority.ALWAYS);
        footerPane.getColumnConstraints().addAll(column1, column2, column3);
        footerPane.getRowConstraints().addAll(row1);

        GridPane.setValignment(discardButton, VPos.CENTER);
        GridPane.setValignment(progressPane, VPos.CENTER);
        GridPane.setValignment(saveButtonContainer, VPos.CENTER);

        GridPane.setHalignment(discardButton, HPos.CENTER);
        GridPane.setHalignment(progressPane, HPos.CENTER);
        GridPane.setHalignment(saveButtonContainer, HPos.CENTER);


        saveButton.setText("Save changes");
        saveButton.getStyleClass().add("mainButton");
        saveButton.setId("saveButton");
        saveButton.setCursor(Cursor.HAND);
        saveButton.setDisable(true);
        saveButton.setOnAction(e -> {
            if(menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) menuController.subtitlesController.closeSubtitles();
            if(menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) menuController.playbackSettingsController.closeSettings();

            saveChanges();
        });

        saveIcon.setShape(saveIconSVG);
        saveIcon.getStyleClass().addAll("menuIcon", "graphic");
        saveIcon.setPrefSize(14, 14);
        saveIcon.setMaxSize(14, 14);
        saveButton.setGraphic(saveIcon);

        chevronUpSVG.setContent(SVG.CHEVRON_UP.getContent());

        chevronUpIcon.setShape(chevronUpSVG);
        chevronUpIcon.setPrefSize(14, 8);
        chevronUpIcon.setMaxSize(14,8);
        chevronUpIcon.setId("saveOptionsIcon");
        chevronUpIcon.setMouseTransparent(true);

        saveOptionsButton.setCursor(Cursor.HAND);
        saveOptionsButton.getStyleClass().add("mainButton");
        saveOptionsButton.setId("saveOptionsButton");
        saveOptionsButton.setGraphic(chevronUpIcon);
        saveOptionsButton.setDisable(true);

        TranslateTransition chevronDownAnimation = new TranslateTransition(Duration.millis(100), chevronUpIcon);
        chevronDownAnimation.setFromY(chevronUpIcon.getTranslateY());
        chevronDownAnimation.setToY(3);

        TranslateTransition chevronUpAnimation = new TranslateTransition(Duration.millis(100), chevronUpIcon);
        chevronUpAnimation.setFromY(3);
        chevronUpAnimation.setToY(0);

        saveOptionsButton.setOnMousePressed(e -> chevronDownAnimation.play());
        saveOptionsButton.setOnMouseReleased(e -> {
            if(chevronDownAnimation.statusProperty().get() == Animation.Status.RUNNING){
                chevronDownAnimation.setOnFinished(ev -> {
                    chevronUpAnimation.playFromStart();
                    chevronDownAnimation.setOnFinished(null);
                });
            }
            else chevronUpAnimation.playFromStart();
        });

        saveOptionsButton.setOnAction(e -> {

            if(menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) menuController.subtitlesController.closeSubtitles();
            if(menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) menuController.playbackSettingsController.closeSettings();

            if(this.mediaItem == null) return;

            if(saveOptionsContextMenu.showing) saveOptionsContextMenu.hide();
            else saveOptionsContextMenu.showOptions(true, mediaItem);
        });

        saveButtonContainer.getChildren().addAll(saveButton, saveOptionsButton);
        saveButtonContainer.setMaxWidth(Region.USE_PREF_SIZE);
        saveButtonContainer.setMaxHeight(Region.USE_PREF_SIZE);
        saveButtonContainer.setAlignment(Pos.CENTER);


        discardButton.setCursor(Cursor.HAND);
        discardButton.setText("Discard changes");
        discardButton.getStyleClass().add("menuButton");
        discardButton.setDisable(true);
        discardButton.setPrefWidth(170);


        discardButton.setOnAction(e -> {
            if(menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) menuController.subtitlesController.closeSubtitles();
            if(menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) menuController.playbackSettingsController.closeSettings();

            if(mediaItem.metadataEditActive.get()) return;
            reloadMediaInformation();
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


        mediaInformationWrapper.getChildren().addAll(titlePane, mediaInformationScroll);
        menuController.mediaInformationContainer.getChildren().addAll(mediaInformationWrapper, popup);


        StackPane.setAlignment(popup, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(popup, new Insets(0, 30, 30, 0));

        popup.setId("mediaInfoWindow");
        popup.getChildren().addAll(popupTitleBar, popupBody, closeButtonPane);
        popup.setPadding(new Insets(5, 10, 5, 10));
        popup.setVisible(false);
        popup.setOpacity(0);
        popup.setEffect(new DropShadow());
        popup.setPrefSize(300, 120);
        popup.setMaxSize(300, 120);

        StackPane.setAlignment(popupTitleBar, Pos.TOP_CENTER);
        popupTitleBar.setPrefHeight(35);
        popupTitleBar.setMaxHeight(35);
        popupTitleBar.getChildren().addAll(popupTitle, closeButtonPane);

        StackPane.setAlignment(popupTitle, Pos.CENTER_LEFT);
        popupTitle.setId("mediaInfoWindowTitle");
        
        
        StackPane.setAlignment(popupBody, Pos.TOP_LEFT);
        StackPane.setMargin(popupBody, new Insets(40, 0, 0, 0));


        popupTimer.setOnFinished(e -> {
            hidePopup();
        });


        StackPane.setAlignment(closeButtonPane, Pos.CENTER_RIGHT);
        closeButtonPane.setPrefSize(25, 25);
        closeButtonPane.setMaxSize(25, 25);
        closeButtonPane.getChildren().addAll(closeButton, closeButtonIcon);

        closeButton.setPrefWidth(25);
        closeButton.setPrefHeight(25);
        closeButton.getStyleClass().add("popupWindowCloseButton");
        closeButton.setCursor(Cursor.HAND);
        closeButton.setOpacity(0);
        closeButton.setText(null);
        closeButton.setOnAction(e -> hidePopup());

        closeButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> AnimationsClass.fadeAnimation(200, closeButton, 0, 1, false, 1, true));

        closeButton.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> AnimationsClass.fadeAnimation(200, closeButton, 1, 0, false, 1, true));

        closeSVG.setContent(SVG.CLOSE.getContent());

        closeButtonIcon.setShape(closeSVG);
        closeButtonIcon.setMinSize(13, 13);
        closeButtonIcon.setPrefSize(13, 13);
        closeButtonIcon.setMaxSize(13, 13);
        closeButtonIcon.setMouseTransparent(true);
        closeButtonIcon.getStyleClass().add("menuIcon");
    }

    public void loadMediaInformationPage(MediaItem mediaItem){

        if(mediaItem == null) return;

        this.mediaItem = mediaItem;

        progressBar.setProgress(mediaItem.metadataEditProgress.get());
        if(mediaItem.metadataEditActive.get()) progressBar.setVisible(true);
        mediaItem.metadataEditProgress.addListener(progressListener);
        mediaItem.metadataEditActive.addListener(editActiveListener);
        saveButton.disableProperty().bind(mediaItem.changesMade.not());
        saveOptionsButton.disableProperty().bind(mediaItem.changesMade.not());
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
                mediaInformationItem = mediaItem.changesMade.get() ? new Mp4Item(this, mediaItem.newMetadata) : new Mp4Item(this, mediaItem.getMediaInformation());
                enableImageEdit();
            }
            case "m4a" -> {
                mediaInformationItem = mediaItem.changesMade.get() ? new M4aItem(this, mediaItem.newMetadata) : new M4aItem(this, mediaItem.getMediaInformation());
                enableImageEdit();
            }
            case "mp3", "aiff" -> {
                mediaInformationItem = mediaItem.changesMade.get() ? new Mp3Item(this, mediaItem.newMetadata) : new Mp3Item(this, mediaItem.getMediaInformation());
                enableImageEdit();
            }
            case "aac" -> {
                mediaInformationItem = mediaItem.changesMade.get() ? new Mp3Item(this, mediaItem.newMetadata) : new Mp3Item(this, mediaItem.getMediaInformation());
                disableImageEdit();
            }
            case "flac" -> {
                mediaInformationItem = mediaItem.changesMade.get() ? new FlacItem(this, mediaItem.newMetadata) : new FlacItem(this, mediaItem.getMediaInformation());
                enableImageEdit();
            }
            case "ogg", "opus" -> {
                mediaInformationItem = mediaItem.changesMade.get() ? new OggItem(this, mediaItem.newMetadata) : new OggItem(this, mediaItem.getMediaInformation());
                disableImageEdit();
            }
            case "avi" -> {
                mediaInformationItem = mediaItem.changesMade.get() ? new AviItem(this, mediaItem.newMetadata) : new AviItem(this, mediaItem.getMediaInformation());
                disableImageEdit();
            }
            case "flv", "wma" -> {
                mediaInformationItem = mediaItem.changesMade.get() ? new OtherItem(this, mediaItem.newMetadata) : new OtherItem(this, mediaItem.getMediaInformation());
                disableImageEdit();
            }
            case "wav" -> {
                mediaInformationItem = mediaItem.changesMade.get() ? new WavItem(this, mediaItem.newMetadata) : new WavItem(this, mediaItem.getMediaInformation());
                disableImageEdit();
            }
            default -> {
                mediaInformationItem = mediaItem.changesMade.get() ? new OtherItem(this, mediaItem.newMetadata) : new OtherItem(this, mediaItem.getMediaInformation());
                enableImageEdit();
            }
        }
    }

    private void enableImageEdit(){
        imageEditEnabled = true;
        editImageButton.setOnAction(e -> {
            if(menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) menuController.subtitlesController.closeSubtitles();
            if(menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) menuController.playbackSettingsController.closeSettings();

            editImageButtonClick();
        });
        editImageIcon.setShape(editIconSVG);
        editImageTooltip.updateDelay(Duration.seconds(1));
        editImageTooltip.updateActionText("Edit cover");
    }

    private void disableImageEdit(){
        imageEditEnabled = false;
        editImageButton.setOnAction(e -> {
            if(menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) menuController.subtitlesController.closeSubtitles();
            if(menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) menuController.playbackSettingsController.closeSettings();
        });
        editImageIcon.setShape(editIconOffSVG);
        editImageTooltip.updateDelay(Duration.ZERO);
        editImageTooltip.updateActionText("Cover editing unavailable for this media format");
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

    public void saveChanges(){

        if(mediaItem.metadataEditActive.get() || !mediaItem.changesMade.get()) return;

        mediaItem.newMetadata = mediaInformationItem.createMetadataMap();

        if(menuController.queuePage.queueBox.activeItem.get() != null && menuController.queuePage.queueBox.activeItem.get().getMediaItem() == mediaItem){
            menuController.mediaInterface.resetMediaPlayer();
        }

        EditTask editTask = new EditTask(mediaItem);
        editTask.setOnSucceeded(e -> {
            if(editTask.getValue()){
                for(QueueItem queueItem : menuController.queuePage.queueBox.queue){
                    if(queueItem.getMediaItem() == mediaItem){
                        queueItem.update();
                    }
                }

                menuController.mainController.getControlBarController().updateNextAndPreviousVideoButtons();

                if(menuController.queuePage.queueBox.activeItem.get() != null && menuController.queuePage.queueBox.activeItem.get().getMediaItem() == mediaItem) menuController.mediaInterface.createMedia(menuController.queuePage.queueBox.activeItem.get());
            }
        });

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(editTask);
        executorService.shutdown();

    }

    public void saveToNewFile(File file){


        if(mediaItem.metadataEditActive.get() || !mediaItem.changesMade.get()) return;

        mediaItem.newMetadata = mediaInformationItem.createMetadataMap();

        savingToNewFile = true;

        EditTask editTask = new EditTask(mediaItem, file);
        editTask.setOnSucceeded(e -> {
            popup.setPrefSize(400, 130);
            popup.setMaxSize(400, 130);
            popupTitle.setText("File created");
            popupBody.getChildren().clear();
            popupBody.getChildren().add(createTextLabel("Created an output file with updated metadata at"));
            popupBody.getChildren().add(createLinkLabel(file.getAbsolutePath(), file));
        });

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(editTask);
        executorService.shutdown();
    }

    private void reloadMediaInformation(){

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
        savingToNewFile = false;

        loadMediaInformationPage(mediaItem);
    }

    public void openMediaInformationPage(){
        menuController.mediaInformationContainer.setVisible(true);
    }

    public void closeMediaInformationPage(){
        menuController.mediaInformationContainer.setVisible(false);

        textBox.getChildren().clear();
        imageView.setImage(null);
        imageViewContainer.setStyle("-fx-background-color: transparent;");

        if(mediaItem != null) mediaItem.metadataEditProgress.removeListener(progressListener);
        if(mediaItem != null) mediaItem.metadataEditActive.removeListener(editActiveListener);
        if(progressAnimation != null && progressAnimation.getStatus() == Animation.Status.RUNNING) progressAnimation.stop();
        if(saveLabelTimer.getStatus() == Animation.Status.RUNNING) saveLabelTimer.stop();
        progressBar.setProgress(0);
        saveButton.disableProperty().unbind();
        saveOptionsButton.disableProperty().unbind();
        editImageButton.disableProperty().unbind();
        discardButton.disableProperty().unbind();
        fieldsDisabledProperty.unbind();

        savedLabel.setVisible(false);
        progressBar.setVisible(false);

        savingToNewFile = false;

        popup.setVisible(false);

        if(!mediaItem.metadataEditActive.get() && mediaItem.changesMade.get()){
            mediaItem.newMetadata = mediaInformationItem.createMetadataMap();
        }

        mediaInformationItem = null;
    }

    public void enter(MediaItem mediaItem){

        if(menuController.menuInTransition) return;

        menuController.menuBar.setActiveButton(null);

        loadMediaInformationPage(mediaItem);

        if(menuController.menuState == MenuState.CLOSED){
            if(!menuController.extended.get()) menuController.setMenuExtended(MenuState.MEDIA_INFORMATION_OPEN);
            menuController.openMenu(MenuState.MEDIA_INFORMATION_OPEN);
        }
        else {
            if(!menuController.extended.get()) menuController.extendMenu(MenuState.MEDIA_INFORMATION_OPEN);
            else menuController.animateStateSwitch(MenuState.MEDIA_INFORMATION_OPEN);
        }
    }
    
    public Label createTextLabel(String text){
        Label label = new Label(text);
        label.getStyleClass().add("mediaInfoWindowText");
        label.setWrapText(true);
        label.setMinHeight(25);
        VBox.setVgrow(label, Priority.ALWAYS);
        
        return label;
    }
    
    public Label createLinkLabel(String text, File file){
        Label label = new Label(text);
        label.getStyleClass().add("mediaInfoWindowText");
        label.setWrapText(true);
        label.setUnderline(false);
        label.setOnMouseEntered(e -> label.setUnderline(true));
        label.setOnMouseExited(e -> label.setUnderline(false));
        label.setCursor(Cursor.HAND);
        label.setMaxHeight(55);
        label.setOnMouseClicked(e -> {
            if(App.isWindows){
                Shell32Util.SHOpenFolderAndSelectItems(file);
            }
            else if(Desktop.isDesktopSupported()){
                Desktop desktop = Desktop.getDesktop();

                if(desktop.isSupported(Desktop.Action.BROWSE_FILE_DIR)){
                    desktop.browseFileDirectory(file);
                }
                else if(desktop.isSupported(Desktop.Action.OPEN)){
                    try {
                        desktop.open(file.getParentFile());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });


        return label;
    }

    public void showPopup(){
        if(popupTimer.getStatus() == Animation.Status.RUNNING) popupTimer.stop();
        if(popupFadeOut != null && popupFadeOut.getStatus() == Animation.Status.RUNNING) popupFadeOut.stop();

        popup.setVisible(true);
        popupFadeIn = new FadeTransition(Duration.millis(200), popup);
        popupFadeIn.setFromValue(popup.getOpacity());
        popupFadeIn.setToValue(1);
        popupFadeIn.playFromStart();

        popupTimer.playFromStart();
    }

    public void hidePopup(){

        if(popupFadeIn != null && popupFadeIn.getStatus() == Animation.Status.RUNNING) popupFadeIn.stop();
        if(popupTimer.getStatus() == Animation.Status.RUNNING) popupTimer.stop();

        popupFadeOut = new FadeTransition(Duration.millis(200), popup);
        popupFadeOut.setFromValue(popup.getOpacity());
        popupFadeOut.setToValue(0);
        popupFadeOut.setOnFinished(ev -> popup.setVisible(false));
        popupFadeOut.playFromStart();
    }
}
