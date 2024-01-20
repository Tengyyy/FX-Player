package tengy.windows.mediaInformation;

import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import tengy.*;
import tengy.mediaItems.MediaItem;
import tengy.mediaItems.MediaUtilities;
import tengy.menu.MenuController;
import tengy.menu.Queue.QueueItem;
import tengy.windows.mediaInformation.items.*;
import tengy.windows.WindowController;
import tengy.windows.WindowState;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static tengy.Utilities.keyboardFocusOff;
import static tengy.Utilities.keyboardFocusOn;

public class MediaInformationWindow {

    WindowController windowController;
    MainController mainController;
    public MenuController menuController;

    public StackPane window = new StackPane();

    VBox windowContainer = new VBox();

    StackPane buttonContainer = new StackPane();

    Region closeButtonIcon = new Region();
    SVGPath closeButtonSVG = new SVGPath();
    Button closeButton = new Button();

    StackPane titleContainer = new StackPane();
    Label title = new Label("Media information");
    Button chapterEditButton = new Button();
    Button technicalDetailsButton = new Button();
    ControlTooltip chapterEditTooltip, technicalDetailsTooltip;

    public ScrollPane scrollPane;

    public MediaItem mediaItem = null;

    public VBox content = new VBox();

    public boolean showing = false;

    IntegerProperty focus = new SimpleIntegerProperty(-1);
    public List<Node> focusNodes = new ArrayList<>();


    FileChooser imageChooser;

    SVGPath editIconSVG = new SVGPath();
    SVGPath editIconOffSVG = new SVGPath();
    SVGPath saveIconSVG = new SVGPath();

    Button saveButton = new Button("Save changes");
    Button createNewFileButton = new Button("New file");

    Button discardButton = new Button("Discard");



    StackPane imageViewWrapper = new StackPane();
    public StackPane imageViewContainer = new StackPane();
    SVGPath imageSVG = new SVGPath();
    Region imageIcon = new Region();
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

    SavePopUp savePopUp;
    StackPane popupContainer = new StackPane();


    public BooleanProperty changesMade = new SimpleBooleanProperty(false);
    public BooleanProperty saveAllowed = new SimpleBooleanProperty(false);
    BooleanProperty editActiveProperty = new SimpleBooleanProperty(false);

    public boolean coverRemoved = false;
    public Image newCover = null;
    public File newCoverFile = null;
    public Color newColor = null;

    PauseTransition showTimer = new PauseTransition(Duration.millis(2500));
    ParallelTransition showTransition = null;
    ParallelTransition hideTransition = null;


    StackPane loadingContainer = new StackPane();
    HBox loadingPane = new HBox();
    StackPane spinnerWrapper = new StackPane();
    MFXProgressSpinner progressSpinner = new MFXProgressSpinner();
    SVGPath checkSVG = new SVGPath();
    SVGPath crossSVG = new SVGPath();
    Region statusIcon = new Region();
    Label savingLabel = new Label("Saving media information..");

    public MediaInformationWindow(WindowController windowController){
        this.windowController = windowController;
        this.mainController = windowController.mainController;
        this.menuController = mainController.getMenuController();

        savePopUp = new SavePopUp(this);

        mainController.popupWindowContainer.getChildren().add(window);

        window.setAlignment(Pos.TOP_LEFT);

        window.setPrefWidth(650);
        window.setMaxWidth(650);

        window.prefHeightProperty().bind(Bindings.max(450, Bindings.min(1000, mainController.videoImageViewWrapper.heightProperty().multiply(0.8))));
        window.maxHeightProperty().bind(Bindings.max(450, Bindings.min(1000, mainController.videoImageViewWrapper.heightProperty().multiply(0.8))));

        window.getStyleClass().add("popupWindow");
        window.setVisible(false);
        window.getChildren().addAll(windowContainer, buttonContainer, closeButton, loadingContainer, popupContainer);

        StackPane.setAlignment(closeButton, Pos.TOP_RIGHT);
        StackPane.setMargin(closeButton, new Insets(10, 10, 0 ,0));
        closeButton.setPrefWidth(25);
        closeButton.setPrefHeight(25);
        closeButton.getStyleClass().addAll("transparentButton", "popupWindowCloseButton");
        closeButton.setOnAction(e -> this.hide());
        closeButton.setFocusTraversable(false);
        closeButton.setGraphic(closeButtonIcon);
        closeButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(0);
            }
            else{
                keyboardFocusOff(closeButton);
                focus.set(-1);
            }
        });

        closeButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            closeButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        closeButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            closeButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });

        closeButtonSVG.setContent(SVG.CLOSE.getContent());

        closeButtonIcon.setShape(closeButtonSVG);
        closeButtonIcon.setMinSize(13, 13);
        closeButtonIcon.setPrefSize(13, 13);
        closeButtonIcon.setMaxSize(13, 13);
        closeButtonIcon.setMouseTransparent(true);
        closeButtonIcon.getStyleClass().add("graphic");

        content.setPadding(new Insets(15, 30, 15, 30));
        content.setSpacing(15);
        content.getChildren().addAll(imageViewWrapper, textBox);


        scrollPane = new ScrollPane() {
            ScrollBar vertical;

            @Override
            protected void layoutChildren() {
                super.layoutChildren();
                if (vertical == null) {
                    vertical = (ScrollBar) lookup(".scroll-bar:vertical");
                    vertical.visibleProperty().addListener((obs, old, val) -> updatePadding(val));
                }
            }
        };

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("menuScroll");
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        scrollPane.setBackground(Background.EMPTY);
        scrollPane.setContent(content);


        windowContainer.setPadding(new Insets(15, 0, 0, 0));
        windowContainer.getChildren().addAll(titleContainer, scrollPane);
        windowContainer.setSpacing(5);
        StackPane.setMargin(windowContainer, new Insets(0, 0, 70, 0));

        titleContainer.getChildren().addAll(title, chapterEditButton, technicalDetailsButton);
        titleContainer.setAlignment(Pos.CENTER_LEFT);
        titleContainer.setPadding(new Insets(5, 0, 5, 15));

        StackPane.setAlignment(title, Pos.CENTER_LEFT);
        title.getStyleClass().addAll("popupWindowTitle", "chapterWindowTitle");


        SVGPath chapterEditSVG = new SVGPath();
        chapterEditSVG.setContent(SVG.CHAPTER.getContent());
        Region chapterEditIcon = new Region();
        chapterEditIcon.setShape(chapterEditSVG);
        chapterEditIcon.getStyleClass().addAll("menuIcon", "graphic");
        chapterEditIcon.setPrefSize(16, 16);
        chapterEditIcon.setMaxSize(16, 16);

        StackPane.setAlignment(chapterEditButton, Pos.CENTER_RIGHT);
        StackPane.setMargin(chapterEditButton, new Insets(0, 110, 0 , 0));
        chapterEditButton.setFocusTraversable(false);
        chapterEditButton.setGraphic(chapterEditIcon);
        chapterEditButton.getStyleClass().add("menuButton");
        chapterEditButton.setOnAction(e -> {
            chapterEditButton.requestFocus();

            if(mediaItem == null) return;

            mainController.windowController.chapterEditWindow.show(mediaItem);
        });

        chapterEditButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(1);
            }
            else{
                keyboardFocusOff(chapterEditButton);
                focus.set(-1);
            }
        });

        chapterEditButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            chapterEditButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        chapterEditButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            chapterEditButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });

        SVGPath technicalDetailsSVG = new SVGPath();
        technicalDetailsSVG.setContent(SVG.COGS.getContent());
        Region technicalDetailsIcon = new Region();
        technicalDetailsIcon.setShape(technicalDetailsSVG);
        technicalDetailsIcon.getStyleClass().addAll("menuIcon", "graphic");
        technicalDetailsIcon.setPrefSize(16, 16);
        technicalDetailsIcon.setMaxSize(16, 16);

        StackPane.setAlignment(technicalDetailsButton, Pos.CENTER_RIGHT);
        StackPane.setMargin(technicalDetailsButton, new Insets(0, 60, 0, 0));
        HBox.setMargin(technicalDetailsButton, new Insets(0, 0, 0, 50));
        technicalDetailsButton.setFocusTraversable(false);
        technicalDetailsButton.setGraphic(technicalDetailsIcon);
        technicalDetailsButton.getStyleClass().add("menuButton");
        technicalDetailsButton.setOnAction(e -> {
            technicalDetailsButton.requestFocus();

            if(mediaItem == null) return;

            mainController.windowController.technicalDetailsWindow.show(mediaItem);
        });

        technicalDetailsButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                if(chapterEditButton.isVisible()) focus.set(2);
                else focus.set(1);
            }
            else{
                keyboardFocusOff(technicalDetailsButton);
                focus.set(-1);
            }
        });

        technicalDetailsButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            technicalDetailsButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        technicalDetailsButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            technicalDetailsButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });



        imageChooser = new FileChooser();
        imageChooser.setTitle("Choose image");
        imageChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Supported images", "*.jpg", "*.jpeg", "*.png"));

        editIconSVG.setContent(SVG.EDIT.getContent());
        editIconOffSVG.setContent(SVG.EDIT_OFF.getContent());
        saveIconSVG.setContent(SVG.SAVE.getContent());


        imageViewWrapper.getChildren().add(imageViewContainer);
        imageViewWrapper.setPadding(new Insets(20, 0, 50, 0));
        imageViewWrapper.setBackground(Background.EMPTY);

        imageViewContainer.getChildren().addAll(imageIcon, imageView, imageFilter, editImageButtonWrapper);
        imageViewContainer.setId("imageViewContainer");
        imageViewContainer.maxWidthProperty().bind(Bindings.min(400, menuController.menu.widthProperty().multiply(0.7)));
        imageViewContainer.setStyle("-fx-background-color: rgb(30,30,30);");

        imageSVG.setContent(SVG.IMAGE_WIDE.getContent());
        imageIcon.setShape(imageSVG);
        imageIcon.setPrefSize(100, 80);
        imageIcon.setMaxSize(100, 80);
        imageIcon.getStyleClass().add("imageIcon");
        imageIcon.setMouseTransparent(true);


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

        imageView.setVisible(false);
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

        editImageButton.setOnAction(e -> editImageButtonClick());

        Platform.runLater(() -> {
            editImageTooltip = new ControlTooltip(menuController.mainController, "Edit cover", "", editImageButton, 1000);
            editImagePopUp = new EditImagePopUp(this);
        });

        textBox.setAlignment(Pos.TOP_LEFT);
        textBox.setPadding(new Insets(0, 15, 0, 15));

        StackPane.setAlignment(discardButton, Pos.CENTER_LEFT);
        discardButton.getStyleClass().add("menuButton");
        discardButton.disableProperty().bind(changesMade.not().or(editActiveProperty));
        discardButton.setPrefWidth(230);


        discardButton.setOnAction(e -> {
            if(mediaItem.editActive.get()) return;
            discardChanges();
        });

        discardButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue) {
                focus.set(focusNodes.indexOf(discardButton));
            }
            else {
                keyboardFocusOff(discardButton);
                focus.set(-1);
            }
        });

        discardButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            discardButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        discardButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            discardButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });

        discardButton.disabledProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue) focusNodes.remove(discardButton);
            else {
                if(focusNodes.contains(saveButton)) focusNodes.add(focusNodes.indexOf(saveButton), discardButton);
                else focusNodes.add(discardButton);
            }
        });

        StackPane.setAlignment(saveButton, Pos.CENTER_RIGHT);
        StackPane.setMargin(saveButton, new Insets(0, 160, 0, 0));
        saveButton.getStyleClass().add("mainButton");
        saveButton.setPrefWidth(150);
        saveButton.disableProperty().bind(saveAllowed.not().or(editActiveProperty));
        saveButton.setOnAction(e -> {
            saveButton.requestFocus();
            boolean keysCorrect = checkKeys();
            if(keysCorrect) {
                saveChanges();
            }
            else {
                savePopUp.label2.setText("Make sure all the title fields are filled and chapter start times are in increasing order and don't exceed video duration.");
                saveAllowed.set(false);
                savePopUp.show();
            }
        });

        saveButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue) {
                focus.set(focusNodes.size() - 2);
            }
            else {
                keyboardFocusOff(saveButton);
                focus.set(-1);
            }
        });

        saveButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            saveButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        saveButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            saveButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });

        StackPane.setAlignment(createNewFileButton, Pos.CENTER_RIGHT);
        createNewFileButton.getStyleClass().add("mainButton");
        createNewFileButton.setPrefWidth(150);
        createNewFileButton.disableProperty().bind(saveAllowed.not().or(editActiveProperty));
        createNewFileButton.setOnAction(e -> {
            createNewFileButton.requestFocus();
            boolean keysCorrect = checkKeys();
            if(keysCorrect) {
                openFileChooser();
            }
            else {
                savePopUp.label2.setText("Make sure all the title fields are filled and chapter start times are in increasing order and don't exceed video duration.");
                saveAllowed.set(false);
                savePopUp.show();
            }
        });

        createNewFileButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue) {
                focus.set(focusNodes.size() - 1);
            }
            else {
                keyboardFocusOff(createNewFileButton);
                focus.set(-1);
            }
        });

        createNewFileButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            createNewFileButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        createNewFileButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            createNewFileButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });

        createNewFileButton.disabledProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focusNodes.remove(saveButton);
                focusNodes.remove(createNewFileButton);
            }
            else {
                focusNodes.add(saveButton);
                focusNodes.add(createNewFileButton);
            }
        });


        StackPane.setAlignment(buttonContainer, Pos.BOTTOM_CENTER);
        buttonContainer.getChildren().addAll(discardButton, saveButton, createNewFileButton);
        buttonContainer.getStyleClass().add("buttonContainer");
        buttonContainer.setPadding(new Insets(0, 15, 0, 15));
        buttonContainer.setPrefHeight(70);
        buttonContainer.setMaxHeight(70);

        StackPane.setAlignment(loadingContainer, Pos.BOTTOM_CENTER);
        StackPane.setMargin(loadingContainer, new Insets(0, 0, 80, 0));
        loadingContainer.prefWidthProperty().bind(Bindings.max(500, Bindings.min(650, window.widthProperty().subtract(50))));
        loadingContainer.maxWidthProperty().bind(Bindings.max(500, Bindings.min(650, window.widthProperty().subtract(50))));
        loadingContainer.setPrefHeight(50);
        loadingContainer.setMaxHeight(50);
        loadingContainer.setVisible(false);

        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(loadingContainer.widthProperty());
        clip.setHeight(50);

        loadingContainer.setClip(clip);
        loadingContainer.getChildren().add(loadingPane);

        loadingPane.prefWidthProperty().bind(loadingContainer.widthProperty());
        loadingPane.maxWidthProperty().bind(loadingContainer.widthProperty());
        loadingPane.setAlignment(Pos.CENTER_LEFT);
        loadingPane.setSpacing(20);
        loadingPane.setPadding(new Insets(10, 20, 10, 20));
        loadingPane.getChildren().addAll(spinnerWrapper, savingLabel);
        loadingPane.getStyleClass().add("savePopup");
        loadingPane.setPrefHeight(50);
        loadingPane.setMaxHeight(50);
        loadingPane.setTranslateY(50);
        loadingPane.setOpacity(0.3);

        spinnerWrapper.getChildren().addAll(progressSpinner, statusIcon);

        checkSVG.setContent(SVG.CHECK.getContent());
        crossSVG.setContent(SVG.CLOSE.getContent());

        statusIcon.setShape(checkSVG);
        statusIcon.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
        statusIcon.setPrefSize(20, 14);
        statusIcon.setMaxSize(20, 14);
        statusIcon.setVisible(false);

        progressSpinner.setRadius(10);
        progressSpinner.setColor1(Color.RED);
        progressSpinner.setColor2(Color.RED);
        progressSpinner.setColor3(Color.RED);
        progressSpinner.setColor4(Color.RED);

        savingLabel.getStyleClass().add("settingsText");

        popupContainer.setId("chapterPopupBackground");
        popupContainer.setOpacity(0);
        popupContainer.setMouseTransparent(true);

        showTimer.setOnFinished(e -> {
            if(showTransition != null && showTransition.getStatus() == Animation.Status.RUNNING) showTransition.stop();
            if(hideTransition != null && hideTransition.getStatus() == Animation.Status.RUNNING) hideTransition.stop();
            hideLoadingSpinner();
        });

        editActiveProperty.addListener((observableValue, oldValue, newValue) -> {

            if(newValue){
                setSpinnerToLoading();
                if(showTimer.getStatus() == Animation.Status.RUNNING) showTimer.stop();
                if(showTransition != null && showTransition.getStatus() == Animation.Status.RUNNING) showTransition.stop();
                if(hideTransition != null && hideTransition.getStatus() == Animation.Status.RUNNING) hideTransition.stop();

                showLoadingSpinner();
            }
            else {
                if(showTimer.getStatus() == Animation.Status.RUNNING) showTimer.stop();
                if(hideTransition != null && hideTransition.getStatus() == Animation.Status.RUNNING) hideTransition.stop();

                if(mediaItem == null){
                    if(showTransition != null && showTransition.getStatus() == Animation.Status.RUNNING) showTransition.stop();
                    loadingContainer.setVisible(false);
                }
            }
        });

        Platform.runLater(() -> {
            chapterEditTooltip = new ControlTooltip(mainController, "View chapters", "", chapterEditButton, 1000);
            technicalDetailsTooltip = new ControlTooltip(mainController, "Technical details", "", technicalDetailsButton, 1000);
        });
    }


    public void show(MediaItem mediaItem){

        windowController.updateState(WindowState.MEDIA_INFORMATION_WINDOW_OPEN);

        initializeWindow(mediaItem);

        this.showing = true;
        window.setVisible(true);

        mainController.popupWindowContainer.setMouseTransparent(false);

        window.requestFocus();

        AnimationsClass.fadeAnimation(100, mainController.popupWindowContainer, mainController.popupWindowContainer.getOpacity(), 1, false, 1, true);
    }

    public void resetWindow(){
        window.setVisible(false);
        scrollPane.setVvalue(0);
        saveAllowed.set(false);
        changesMade.set(false);
        editActiveProperty.set(false);
        textBox.getChildren().clear();

        focusNodes.clear();

        imageView.setImage(null);

        mediaInformationItem = null;
        coverRemoved = false;
        newCover = null;
        newCoverFile = null;
        newColor = null;
    }

    public void hide(){
        this.showing = false;

        this.mediaItem = null;
        windowController.windowState = WindowState.CLOSED;

        mainController.popupWindowContainer.setMouseTransparent(true);

        FadeTransition fadeTransition = new FadeTransition(Duration.millis(100), mainController.popupWindowContainer);
        fadeTransition.setFromValue(mainController.popupWindowContainer.getOpacity());
        fadeTransition.setToValue(0);
        fadeTransition.setOnFinished(e -> {
            resetWindow();
        });

        fadeTransition.play();
    }

    private void initializeWindow(MediaItem mediaItem){
        if(mediaItem == null) return;

        focusNodes.add(closeButton);

        String extension = Utilities.getFileExtension(mediaItem.getFile());
        if(!extension.equals("mp4")
                && !extension.equals("mkv")
                && !extension.equals("mov")
                && !extension.equals("mp3")
                && !extension.equals("opus")
                && !extension.equals("m4a")
                && !extension.equals("wma")
                && !extension.equals("wmv")
                && !extension.equals("mka")
                && !extension.equals("webm")){
            chapterEditButton.setVisible(false);
        }
        else {
            chapterEditButton.setVisible(true);
            focusNodes.add(chapterEditButton);
        }

        focusNodes.add(technicalDetailsButton);

        loadMediaInformationPage(mediaItem);
    }

    private void updatePadding(boolean value){
        if(value) content.setPadding(new Insets(15, 18, 15, 30));
        else      content.setPadding(new Insets(15, 30, 15, 30));
    }

    public void focusForward(){

        if(savePopUp.showing){
            savePopUp.changeFocus();
            return;
        }

        int newFocus;

        if(focus.get() >= focusNodes.size() - 1 || focus.get() == -1) newFocus = 0;
        else newFocus = focus.get() + 1;

        keyboardFocusOn(focusNodes.get(newFocus));
    }

    public void focusBackward(){

        if(savePopUp.showing){
            savePopUp.changeFocus();
            return;
        }

        int newFocus;

        if(focus.get() == 0) newFocus = focusNodes.size() - 1;
        else if(focus.get() == -1) newFocus = 0;
        else newFocus = focus.get() - 1;

        keyboardFocusOn(focusNodes.get(newFocus));
    }



    public void loadMediaInformationPage(MediaItem mediaItem){

        if(mediaItem == null) return;

        this.mediaItem = mediaItem;

        if(mediaItem.editActive.get()){
            editActiveProperty.set(true);

            if(mediaItem.newCover != null){
                imageView.setImage(mediaItem.newCover);
                imageView.setVisible(true);
            }
            else if(mediaItem.coverRemoved || mediaItem.getCover() == null){
                imageView.setVisible(false);
            }
            else {
                imageView.setVisible(true);
                imageView.setImage(mediaItem.getCover());
            }
        }
        else {
            if(mediaItem.getCover() != null){
                imageView.setImage(mediaItem.getCover());
                imageView.setVisible(true);
            }
            else imageView.setVisible(false);
        }


        String extension = Utilities.getFileExtension(mediaItem.getFile());

        switch (extension) {
            case "mp4", "mov" -> {
                mediaInformationItem = mediaItem.editActive.get() ? new Mp4Item(this, mediaItem.newMetadata) : new Mp4Item(this, mediaItem.getMediaInformation());
                enableImageEdit();
            }
            case "m4a" -> {
                mediaInformationItem = mediaItem.editActive.get() ? new M4aItem(this, mediaItem.newMetadata) : new M4aItem(this, mediaItem.getMediaInformation());
                enableImageEdit();
            }
            case "mp3", "aiff" -> {
                mediaInformationItem = mediaItem.editActive.get() ? new Mp3Item(this, mediaItem.newMetadata) : new Mp3Item(this, mediaItem.getMediaInformation());
                enableImageEdit();
            }
            case "aac" -> {
                mediaInformationItem = mediaItem.editActive.get() ? new Mp3Item(this, mediaItem.newMetadata) : new Mp3Item(this, mediaItem.getMediaInformation());
                disableImageEdit();
            }
            case "flac" -> {
                mediaInformationItem = mediaItem.editActive.get() ? new FlacItem(this, mediaItem.newMetadata) : new FlacItem(this, mediaItem.getMediaInformation());
                enableImageEdit();
            }
            case "ogg", "opus" -> {
                mediaInformationItem = mediaItem.editActive.get() ? new OggItem(this, mediaItem.newMetadata) : new OggItem(this, mediaItem.getMediaInformation());
                disableImageEdit();
            }
            case "avi" -> {
                mediaInformationItem = mediaItem.editActive.get() ? new AviItem(this, mediaItem.newMetadata) : new AviItem(this, mediaItem.getMediaInformation());
                disableImageEdit();
            }
            case "flv", "wma" -> {
                mediaInformationItem = mediaItem.editActive.get() ? new OtherItem(this, mediaItem.newMetadata) : new OtherItem(this, mediaItem.getMediaInformation());
                disableImageEdit();
            }
            case "wav" -> {
                mediaInformationItem = mediaItem.editActive.get() ? new WavItem(this, mediaItem.newMetadata) : new WavItem(this, mediaItem.getMediaInformation());
                disableImageEdit();
            }
            default -> {
                mediaInformationItem = mediaItem.editActive.get() ? new OtherItem(this, mediaItem.newMetadata) : new OtherItem(this, mediaItem.getMediaInformation());
                enableImageEdit();
            }
        }
    }

    private void enableImageEdit(){
        imageEditEnabled = true;
        editImageButton.setOnAction(e -> {
            editImageButtonClick();
        });
        editImageIcon.setShape(editIconSVG);
        editImageTooltip.updateDelay(Duration.seconds(1));
        editImageTooltip.updateActionText("Edit cover");
    }

    private void disableImageEdit(){
        imageEditEnabled = false;

        editImageIcon.setShape(editIconOffSVG);
        editImageTooltip.updateDelay(Duration.ZERO);
        editImageTooltip.updateActionText("Cover editing unavailable for this media format");
    }

    private void editImageButtonClick(){

        if(mediaItem.editActive.get()) return;
        if(mediaItem.newCover != null || (mediaItem.getCover() != null && !mediaItem.coverRemoved)){
            if(editImagePopUp.isShowing()) editImagePopUp.hide();
            else editImagePopUp.showOptions(mediaItem);
        }
        else editImage();
    }

    public void editImage(){
        File selectedFile = imageChooser.showOpenDialog(imageView.getScene().getWindow());
        if(selectedFile != null && !editActiveProperty.get()){
            coverRemoved = false;
            newCover = new Image(String.valueOf(selectedFile));
            newCoverFile = selectedFile;
            imageView.setImage(newCover);

            newColor = MediaUtilities.findDominantColor(newCover);

            changesMade.set(true);
            saveAllowed.set(true);
        }
    }

    public void removeImage(){
        if(mediaItem.editActive.get()) return;

        coverRemoved = true;
        newCover = null;
        newColor = null;
        newCoverFile = null;
        imageView.setImage(null);
        imageView.setVisible(false);

        changesMade.set(true);
        saveAllowed.set(true);
    }

    public void saveChanges(){

        if(!saveAllowed.get() || editActiveProperty.get()) return;

        mediaItem.newMetadata = mediaInformationItem.createMetadataMap();
        mediaItem.coverRemoved = coverRemoved;
        mediaItem.newCover = newCover;
        mediaItem.newCoverFile = newCoverFile;
        mediaItem.newColor = newColor;

        saveAllowed.set(false);
        changesMade.set(false);
        editActiveProperty.set(true);

        if(menuController.queuePage.queueBox.activeItem.get() != null && menuController.queuePage.queueBox.activeItem.get().getMediaItem() == mediaItem){
            menuController.mediaInterface.resetMediaPlayer();
        }

        EditTask editTask = new EditTask(mediaItem);
        editTask.setOnSucceeded(e -> {

            if(editTask.mediaItem == mediaItem){
                editActiveProperty.set(false);
                if(editTask.getValue()) setSpinnerToDone();
                else setSpinnerToFailed();

                showTimer.playFromStart();
            }

            if(editTask.getValue()){

                for(QueueItem queueItem : menuController.queuePage.queueBox.queue){
                    if(queueItem.getMediaItem() == editTask.mediaItem){
                        queueItem.update();
                    }
                }

                menuController.mainController.getControlBarController().updateNextAndPreviousVideoButtons();

                if(menuController.queuePage.queueBox.activeItem.get() != null && menuController.queuePage.queueBox.activeItem.get().getMediaItem() == editTask.mediaItem)
                    menuController.mediaInterface.createMedia(menuController.queuePage.queueBox.activeItem.get());
            }
        });

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(editTask);
        executorService.shutdown();
    }


    public void saveToNewFile(File file){

        if(!saveAllowed.get() || editActiveProperty.get()) return;

        saveAllowed.set(false);
        changesMade.set(false);
        editActiveProperty.set(true);

        mediaItem.newMetadata = mediaInformationItem.createMetadataMap();
        mediaItem.coverRemoved = coverRemoved;
        mediaItem.newCover = newCover;
        mediaItem.newCoverFile = newCoverFile;
        mediaItem.newColor = newColor;

        EditTask editTask = new EditTask(mediaItem, file);
        editTask.setOnSucceeded(e -> {

            if(editTask.mediaItem == mediaItem){
                editActiveProperty.set(false);
                if(editTask.getValue()) setSpinnerToDone();
                else setSpinnerToFailed();

                showTimer.playFromStart();
            }
        });

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(editTask);
        executorService.shutdown();
    }

    private void discardChanges(){

        if(editActiveProperty.get() || !changesMade.get()) return;

        saveAllowed.set(false);
        changesMade.set(false);

        textBox.getChildren().clear();
        focusNodes.clear();

        coverRemoved = false;
        newCover = null;
        newColor = null;
        newCoverFile = null;

        imageView.setImage(null);

        initializeWindow(mediaItem);
    }

    private void openFileChooser() {
        if(this.mediaItem == null) return;

        MediaItem item = this.mediaItem;

        String extension = Utilities.getFileExtension(item.getFile());

        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Save file");
        fileChooser.setInitialFileName(Utilities.findFileName(mediaItem));
        fileChooser.setInitialDirectory(item.getFile().getParentFile());
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(extension + " files (*." + extension + ")", "*." + extension));

        File selectedFile = fileChooser.showSaveDialog(imageView.getScene().getWindow());

        if(selectedFile != null){
            if (selectedFile.getName().endsWith("." + extension)) {
                if(!selectedFile.getAbsolutePath().equals(item.getFile().getAbsolutePath())) saveToNewFile(selectedFile);
                else saveChanges();
            }
            else {
                savePopUp.label2.setText("Output file has to have the same extension as the original file (*." + extension + ")");
                savePopUp.show();
            }
        }
    }

    private void setSpinnerToLoading(){
        savingLabel.setText("Saving media information..");
        statusIcon.setVisible(false);
        progressSpinner.setVisible(true);
    }

    private void setSpinnerToDone(){
        savingLabel.setText("Media information saved");
        progressSpinner.setVisible(false);
        statusIcon.setPrefSize(20, 14);
        statusIcon.setMaxSize(20, 14);
        statusIcon.setShape(checkSVG);
        statusIcon.setVisible(true);
    }

    private void setSpinnerToFailed(){
        savingLabel.setText("Failed to save media information");
        progressSpinner.setVisible(false);
        statusIcon.setPrefSize(19, 19);
        statusIcon.setMaxSize(19, 19);
        statusIcon.setShape(crossSVG);
        statusIcon.setVisible(true);
    }

    private void showLoadingSpinner(){

        FadeTransition fadeTransition = new FadeTransition(Duration.millis(250), loadingPane);
        fadeTransition.setFromValue(0.3);
        fadeTransition.setToValue(1);

        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(250), loadingPane);
        translateTransition.setFromY(50);
        translateTransition.setToY(0);

        showTransition = new ParallelTransition(fadeTransition, translateTransition);

        loadingContainer.setVisible(true);
        showTransition.play();
    }


    private void hideLoadingSpinner(){

        FadeTransition fadeTransition = new FadeTransition(Duration.millis(250), loadingPane);
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0.3);

        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(250), loadingPane);
        translateTransition.setFromY(0);
        translateTransition.setToY(50);

        hideTransition = new ParallelTransition(fadeTransition, translateTransition);
        hideTransition.setOnFinished(e -> loadingContainer.setVisible(false));
        hideTransition.play();
    }

    private boolean checkKeys(){
        return true;
    }
}
