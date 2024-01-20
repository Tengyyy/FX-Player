package fxplayer.windows;

import com.sandec.mdfx.MarkdownView;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import fxplayer.AnimationsClass;
import fxplayer.MainController;
import fxplayer.SVG;
import javafx.animation.FadeTransition;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static fxplayer.Utilities.keyboardFocusOff;
import static fxplayer.Utilities.keyboardFocusOn;

public class LicenseWindow {

    WindowController windowController;
    MainController mainController;

    StackPane window = new StackPane();

    VBox windowContainer = new VBox();

    VBox titleContainer = new VBox();
    Label title = new Label("License Terms");

    ScrollPane markdownScroll = new ScrollPane();
    MarkdownView markdownView;

    StackPane buttonContainer = new StackPane();
    Button mainButton = new Button("Close");

    Region closeButtonIcon = new Region();
    SVGPath closeButtonSVG = new SVGPath();
    Button closeButton = new Button();

    boolean showing = false;

    IntegerProperty focus = new SimpleIntegerProperty(-1);
    List<Node> focusNodes = new ArrayList<>();

    public LicenseWindow(WindowController windowController){
        this.windowController = windowController;
        this.mainController = windowController.mainController;

        mainController.popupWindowContainer.getChildren().add(window);

        window.setAlignment(Pos.TOP_LEFT);

        window.prefWidthProperty().bind(Bindings.max(500, Bindings.min(800, mainController.videoImageViewWrapper.widthProperty().multiply(0.5))));
        window.maxWidthProperty().bind(Bindings.max(500, Bindings.min(800, mainController.videoImageViewWrapper.widthProperty().multiply(0.5))));

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

        initializeMarkdownView();

        markdownScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        markdownScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        markdownScroll.getStyleClass().add("menuScroll");
        markdownScroll.setFitToWidth(true);
        markdownScroll.setFitToHeight(true);
        markdownScroll.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        markdownScroll.setBackground(Background.EMPTY);
        markdownScroll.setContent(markdownView);




        windowContainer.setPadding(new Insets(15, 0, 0, 0));
        windowContainer.getChildren().addAll(titleContainer, markdownScroll);
        windowContainer.setSpacing(20);
        StackPane.setMargin(windowContainer, new Insets(0, 0, 70, 0));

        titleContainer.getChildren().addAll(title);
        titleContainer.setPadding(new Insets(5, 0, 5, 15));

        title.getStyleClass().addAll("popupWindowTitle", "licenseWindowTitle");




        StackPane.setAlignment(buttonContainer, Pos.BOTTOM_CENTER);
        buttonContainer.getChildren().add(mainButton);
        buttonContainer.getStyleClass().add("buttonContainer");
        buttonContainer.setPadding(new Insets(0, 15, 0, 15));
        buttonContainer.setPrefHeight(70);
        buttonContainer.setMaxHeight(70);

        mainButton.getStyleClass().add("menuButton");
        mainButton.setTextAlignment(TextAlignment.CENTER);
        mainButton.setPrefWidth(230);
        mainButton.setOnAction(e -> this.hide());
        mainButton.setFocusTraversable(false);
        mainButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(1);
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

        focusNodes.add(closeButton);
        focusNodes.add(mainButton);
    }

    public void show(){

        windowController.updateState(WindowState.LICENSE_WINDOW_OPEN);


        this.showing = true;
        window.setVisible(true);

        mainController.popupWindowContainer.setMouseTransparent(false);

        window.requestFocus();
        AnimationsClass.fadeAnimation(100, mainController.popupWindowContainer, 0 , 1, false, 1, true);
    }

    public void hide(){
        this.showing = false;

        windowController.windowState = WindowState.CLOSED;

        mainController.popupWindowContainer.setMouseTransparent(true);

        FadeTransition fadeTransition = new FadeTransition(Duration.millis(100), mainController.popupWindowContainer);
        fadeTransition.setFromValue(mainController.popupWindowContainer.getOpacity());
        fadeTransition.setToValue(0);
        fadeTransition.setOnFinished(e -> {
            window.setVisible(false);
            //markdownScroll.setVvalue(0);
        });
        fadeTransition.play();
    }


    private void initializeMarkdownView(){

        String text;
        try {
            text = Files.readString(Path.of("LICENSE.md"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            text = "https://www.gnu.org/licenses/gpl-3.0.txt";
        }

        markdownView = new MarkdownView(text);
        markdownView.getStylesheets().clear();
        markdownView.getStylesheets().add(Objects.requireNonNull(mainController.getClass().getResource("styles/mdfx-custom.css")).toExternalForm());
        markdownView.getStylesheets().add(Objects.requireNonNull(mainController.getClass().getResource("styles/mdfx-style.css")).toExternalForm());
        markdownView.setPadding(new Insets(15, 20, 15, 15));
    }

    public void focusForward(){
        int newFocus;

        if(focus.get() >= 1 || focus.get() == -1) newFocus = 0;
        else newFocus = focus.get() + 1;

        keyboardFocusOn(focusNodes.get(newFocus));
    }

    public void focusBackward(){
        int newFocus;

        if(focus.get() == 0) newFocus = 1;
        else if(focus.get() == -1) newFocus = 0;
        else newFocus = focus.get() - 1;

        keyboardFocusOn(focusNodes.get(newFocus));
    }
}
