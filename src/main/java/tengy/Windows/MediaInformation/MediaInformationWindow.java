package tengy.Windows.MediaInformation;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import tengy.*;
import tengy.MediaItems.MediaItem;
import tengy.Menu.MenuController;
import tengy.Windows.WindowController;
import tengy.Windows.WindowState;

import java.util.ArrayList;
import java.util.List;

import static tengy.Utilities.keyboardFocusOff;
import static tengy.Utilities.keyboardFocusOn;

public class MediaInformationWindow {

    WindowController windowController;
    MainController mainController;
    MenuController menuController;

    public StackPane window = new StackPane();

    VBox windowContainer = new VBox();


    StackPane buttonContainer = new StackPane();
    Button mainButton = new Button("Close");

    Region closeButtonIcon = new Region();
    SVGPath closeButtonSVG = new SVGPath();
    Button closeButton = new Button();

    StackPane titleContainer = new StackPane();
    Label title = new Label("Media information");
    Button chapterEditButton = new Button();
    Button technicalDetailsButton = new Button();
    ControlTooltip chapterEditTooltip, technicalDetailsTooltip;

    ScrollPane scrollPane;

    public MediaItem mediaItem = null;

    public VBox content = new VBox();

    public boolean showing = false;

    IntegerProperty focus = new SimpleIntegerProperty(-1);
    public List<Node> focusNodes = new ArrayList<>();

    public MediaInformationWindow(WindowController windowController){
        this.windowController = windowController;
        this.mainController = windowController.mainController;
        this.menuController = mainController.getMenuController();

        mainController.popupWindowContainer.getChildren().add(window);

        window.setAlignment(Pos.TOP_LEFT);

        window.prefWidthProperty().bind(Bindings.max(500, Bindings.min(700, mainController.videoImageViewWrapper.widthProperty().multiply(0.5))));
        window.maxWidthProperty().bind(Bindings.max(500, Bindings.min(700, mainController.videoImageViewWrapper.widthProperty().multiply(0.5))));

        window.prefHeightProperty().bind(Bindings.max(350, Bindings.min(1000, mainController.videoImageViewWrapper.heightProperty().multiply(0.8))));
        window.maxHeightProperty().bind(Bindings.max(350, Bindings.min(1000, mainController.videoImageViewWrapper.heightProperty().multiply(0.8))));

        window.getStyleClass().add("popupWindow");
        window.setVisible(false);
        window.getChildren().addAll(windowContainer, buttonContainer, closeButton);

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

        content.setPadding(new Insets(15, 30, 15, 15));
        content.setSpacing(15);

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


        windowContainer.setPadding(new Insets(15, 0, 0, 15));
        windowContainer.getChildren().addAll(titleContainer, scrollPane);
        windowContainer.setSpacing(5);
        StackPane.setMargin(windowContainer, new Insets(0, 0, 70, 0));

        titleContainer.getChildren().addAll(title, chapterEditButton, technicalDetailsButton);
        titleContainer.setAlignment(Pos.CENTER_LEFT);
        titleContainer.setPadding(new Insets(5, 0, 5, 0));

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

        StackPane.setAlignment(buttonContainer, Pos.BOTTOM_CENTER);
        buttonContainer.getChildren().add(mainButton);
        buttonContainer.getStyleClass().add("buttonContainer");
        buttonContainer.setPadding(new Insets(0, 15, 0, 15));
        buttonContainer.setPrefHeight(70);
        buttonContainer.setMaxHeight(70);

        mainButton.getStyleClass().add("menuButton");
        mainButton.setTextAlignment(TextAlignment.CENTER);
        mainButton.setPrefWidth(230);
        mainButton.setFocusTraversable(false);
        mainButton.setOnAction(e -> this.hide());
        mainButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(focusNodes.size() - 1);
            }
            else{
                keyboardFocusOff(mainButton);
                focus.set(-1);
            }
        });

        mainButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            mainButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        mainButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            mainButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });
        StackPane.setAlignment(mainButton, Pos.CENTER_RIGHT);

        Platform.runLater(() -> {
            chapterEditTooltip = new ControlTooltip(mainController, "View chapters", "", chapterEditButton, 1000);
            technicalDetailsTooltip = new ControlTooltip(mainController, "Technical details", "", technicalDetailsButton, 1000);
        });
    }


    public void show(MediaItem mediaItem){

        initializeWindow(mediaItem);

        windowController.updateState(WindowState.MEDIA_INFORMATION_WINDOW_OPEN);


        this.showing = true;
        window.setVisible(true);

        mainController.popupWindowContainer.setMouseTransparent(false);
        AnimationsClass.fadeAnimation(100, mainController.popupWindowContainer, mainController.popupWindowContainer.getOpacity(), 1, false, 1, true);
    }

    public void hide(){

        this.showing = false;

        windowController.windowState = WindowState.CLOSED;

        mainController.popupWindowContainer.setMouseTransparent(true);

        this.mediaItem = null;
        focusNodes.clear();

        FadeTransition fadeTransition = new FadeTransition(Duration.millis(100), mainController.popupWindowContainer);
        fadeTransition.setFromValue(mainController.popupWindowContainer.getOpacity());
        fadeTransition.setToValue(0);
        fadeTransition.setOnFinished(e -> {
            window.setVisible(false);

            scrollPane.setVvalue(0);
        });

        fadeTransition.play();
    }

    private void initializeWindow(MediaItem mediaItem){
        if(mediaItem == null) return;

        this.mediaItem = mediaItem;

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
        focusNodes.add(mainButton);
    }

    private void updatePadding(boolean value){
        if(value) content.setPadding(new Insets(15, 18, 15, 15));
        else      content.setPadding(new Insets(15, 30, 15, 15));
    }

    public void focusForward(){
        int newFocus;

        if(focus.get() >= focusNodes.size() - 1 || focus.get() == -1) newFocus = 0;
        else newFocus = focus.get() + 1;

        keyboardFocusOn(focusNodes.get(newFocus));
    }

    public void focusBackward(){
        int newFocus;

        if(focus.get() == 0) newFocus = focusNodes.size() - 1;
        else if(focus.get() == -1) newFocus = 0;
        else newFocus = focus.get() - 1;

        keyboardFocusOn(focusNodes.get(newFocus));
    }
}
