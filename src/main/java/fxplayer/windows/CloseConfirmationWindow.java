package fxplayer.windows;

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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

import static fxplayer.Utilities.keyboardFocusOff;
import static fxplayer.Utilities.keyboardFocusOn;

public class CloseConfirmationWindow {

    WindowController windowController;
    MainController mainController;

    VBox window = new VBox();
    HBox titleContainer = new HBox();
    Label title = new Label();
    Label text = new Label();

    StackPane buttonContainer = new StackPane();
    Button mainButton = new Button(), secondaryButton = new Button();

    StackPane closeButtonContainer = new StackPane();
    Region closeButtonIcon = new Region();
    SVGPath closeButtonSVG = new SVGPath();
    Button closeButton = new Button();

    public boolean showing = false;

    IntegerProperty focus = new SimpleIntegerProperty(-1);
    List<Node> focusNodes = new ArrayList<>();


    public CloseConfirmationWindow(WindowController windowController){
        this.windowController = windowController;
        this.mainController = windowController.mainController;

        mainController.popupWindowContainer.getChildren().add(window);

        window.setAlignment(Pos.TOP_LEFT);
        window.setPrefWidth(380);
        window.setMaxWidth(380);
        window.getStyleClass().add("popupWindow");
        window.setPrefHeight(Region.USE_COMPUTED_SIZE);
        window.setMaxHeight(Region.USE_PREF_SIZE);
        window.setVisible(false);
        window.setOnMouseClicked(e -> window.requestFocus());

        closeButtonContainer.setPrefHeight(30);
        closeButtonContainer.getChildren().add(closeButton);
        VBox.setMargin(closeButtonContainer, new Insets(0, 10, 0, 15));

        StackPane.setAlignment(closeButton, Pos.BOTTOM_RIGHT);
        closeButton.setTranslateY(5);

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

        titleContainer.getChildren().add(title);
        titleContainer.setSpacing(5);
        titleContainer.setAlignment(Pos.CENTER_LEFT);
        VBox.setMargin(titleContainer, new Insets(0, 15, 25, 15));

        title.setText("Media edit active");
        title.getStyleClass().add("popupWindowTitle");

        text.setText("Please wait for ongoing media edit processes to finish before closing FXPlayer.");
        text.setWrapText(true);
        text.getStyleClass().add("popupWindowText");
        VBox.setMargin(text, new Insets(0, 15, 10, 15));

        buttonContainer.getChildren().addAll(mainButton, secondaryButton);
        VBox.setMargin(buttonContainer, new Insets(20, 0, 0, 0));
        buttonContainer.getStyleClass().add("buttonContainer");
        buttonContainer.setPadding(new Insets(20, 15, 20, 15));

        secondaryButton.setText("Cancel");
        secondaryButton.getStyleClass().add("menuButton");
        secondaryButton.setOnAction(e -> this.hide());
        secondaryButton.setTextAlignment(TextAlignment.CENTER);
        secondaryButton.setPrefWidth(155);
        secondaryButton.setFocusTraversable(false);
        StackPane.setAlignment(secondaryButton, Pos.CENTER_RIGHT);
        secondaryButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            secondaryButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        secondaryButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            secondaryButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });

        secondaryButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(2);
            }
            else {
                keyboardFocusOff(secondaryButton);
                focus.set(-1);
            }
        });



        mainButton.setText("Close app");
        mainButton.getStyleClass().add("mainButton");
        mainButton.setTextAlignment(TextAlignment.CENTER);
        mainButton.setPrefWidth(155);
        mainButton.setFocusTraversable(false);
        mainButton.setOnAction(e -> {
            mainButton.requestFocus();
            mainController.closeApp();
        });
        mainButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            mainButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        mainButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            mainButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });

        mainButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(1);
            }
            else {
                keyboardFocusOff(mainButton);
                focus.set(-1);
            }
        });
        StackPane.setAlignment(mainButton, Pos.CENTER_LEFT);

        window.getChildren().addAll(closeButtonContainer, titleContainer, text, buttonContainer);

        focusNodes.add(closeButton);
        focusNodes.add(mainButton);
        focusNodes.add(secondaryButton);
    }

    public void show(){

        windowController.updateState(WindowState.CLOSE_CONFIRMATION_WINDOW_OPEN);

        this.showing = true;

        window.setVisible(true);

        mainController.popupWindowContainer.setMouseTransparent(false);
        AnimationsClass.fadeAnimation(100, mainController.popupWindowContainer, mainController.popupWindowContainer.getOpacity(), 1, false, 1, true);
    }

    public void hide(){

        this.showing = false;

        windowController.windowState = WindowState.CLOSED;

        mainController.popupWindowContainer.setMouseTransparent(true);

        FadeTransition fadeTransition = new FadeTransition(Duration.millis(100), mainController.popupWindowContainer);
        fadeTransition.setFromValue(mainController.popupWindowContainer.getOpacity());
        fadeTransition.setToValue(0);
        fadeTransition.setOnFinished(e -> window.setVisible(false));
        fadeTransition.play();
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
