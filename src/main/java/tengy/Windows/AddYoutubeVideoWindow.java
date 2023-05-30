package tengy.Windows;

import javafx.animation.FadeTransition;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import tengy.AnimationsClass;
import tengy.MainController;
import tengy.SVG;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static tengy.Utilities.keyboardFocusOff;
import static tengy.Utilities.keyboardFocusOn;

public class AddYoutubeVideoWindow {

    WindowController windowController;
    MainController mainController;

    VBox window = new VBox();
    Label title = new Label();
    TextField textField = new TextField();
    StackPane buttonContainer = new StackPane();
    Button mainButton = new Button(), secondaryButton = new Button();

    StackPane closeButtonContainer = new StackPane();
    Region closeButtonIcon = new Region();
    SVGPath closeButtonSVG = new SVGPath();
    Button closeButton = new Button();

    public boolean showing = false;


    String pattern = "^(?:https?:)?(?:\\/\\/)?(?:youtu\\.be\\/|(?:www\\.|m\\.)?youtube\\.com\\/(?:watch|v|embed)(?:\\.php)?(?:\\?.*v=|\\/))([a-zA-Z0-9\\_-]{7,15})(?:[\\?&][a-zA-Z0-9\\_-]+=[a-zA-Z0-9\\_-]+)*(?:[&\\/\\#].*)?$";
    Pattern regexPatern = Pattern.compile(pattern);

    IntegerProperty focus = new SimpleIntegerProperty(-1);
    List<Node> focusNodes = new ArrayList<>();

    public AddYoutubeVideoWindow(WindowController windowController){
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
        closeButton.setPrefSize(25, 25);
        closeButton.setMaxSize(25, 25);
        closeButton.setTranslateY(5);
        closeButton.getStyleClass().addAll("transparentButton", "popupWindowCloseButton");
        closeButton.setOnAction(e -> this.hide());
        closeButton.setGraphic(closeButtonIcon);
        closeButton.setFocusTraversable(false);
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

        title.setText("Add YouTube video(s)");
        title.setFocusTraversable(false);
        title.getStyleClass().add("popupWindowTitle");
        VBox.setMargin(title, new Insets(0, 15, 25, 15));

        textField.getStyleClass().add("customTextField");
        textField.setPromptText("Enter the URL for a YouTube video or playlist");
        textField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            Matcher matcher = regexPatern.matcher(newValue);
            if(matcher.find()){
                mainButton.setDisable(false);
                System.out.println(matcher.group(1)); // see peaks olema youtube video id
            }
            else mainButton.setDisable(true);
        });
        textField.setPrefHeight(36);
        textField.setMinHeight(36);
        textField.setMaxHeight(36);
        textField.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);");
        textField.setFocusTraversable(false);
        textField.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue) focus.set(1);
            else {
                keyboardFocusOff(textField);
                focus.set(-1);
            }
        });

        VBox.setMargin(textField, new Insets(0, 15, 0, 15));


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
                focus.set(focusNodes.size() - 1);
            }
            else {
                keyboardFocusOff(secondaryButton);
                focus.set(-1);
            }
        });

        StackPane.setAlignment(secondaryButton, Pos.CENTER_RIGHT);

        mainButton.setText("Add");
        mainButton.getStyleClass().add("mainButton");
        mainButton.setTextAlignment(TextAlignment.CENTER);
        mainButton.setPrefWidth(155);
        mainButton.setDisable(true);
        mainButton.disabledProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue) focusNodes.remove(mainButton);
            else if(!focusNodes.contains(mainButton)) focusNodes.add(focusNodes.size() - 1, mainButton);
        });
        mainButton.setFocusTraversable(false);
        StackPane.setAlignment(mainButton, Pos.CENTER_LEFT);

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
                focus.set(focusNodes.size() - 2);
            }
            else {
                keyboardFocusOff(mainButton);
                focus.set(-1);
            }
        });




        window.getChildren().addAll(closeButtonContainer, title, textField, buttonContainer);
        focusNodes.add(closeButton);
        focusNodes.add(textField);
        focusNodes.add(secondaryButton);
    }

    public void show(){

        windowController.updateState(WindowState.ADD_YOUTUBE_VIDEO_WINDOW_OPEN);

        this.showing = true;
        window.setVisible(true);

        mainController.popupWindowContainer.setMouseTransparent(false);
        AnimationsClass.fadeAnimation(100, mainController.popupWindowContainer, 0 , 1, false, 1, true);
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
