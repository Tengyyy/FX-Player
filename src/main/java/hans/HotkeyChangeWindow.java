package hans;

import com.jfoenix.controls.JFXButton;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.TextAlignment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HotkeyChangeWindow {

    MainController mainController;

    VBox window = new VBox();
    Label title = new Label();
    Label hotkeyText = new Label("Hotkey:");
    HBox hotkeyBox = new HBox();
    Label warningLabel = new Label();
    StackPane buttonContainer = new StackPane();
    Button mainButton = new Button(), secondaryButton = new Button();

    StackPane closeButtonContainer = new StackPane();
    StackPane closeButtonPane = new StackPane();
    Region closeButtonIcon = new Region();
    SVGPath closeButtonSVG = new SVGPath();
    JFXButton closeButton = new JFXButton();

    boolean showing = false;

    public HotkeyChangeWindow(MainController mainController){
        this.mainController = mainController;

        mainController.popupWindowContainer.getChildren().add(window);

        window.setAlignment(Pos.TOP_LEFT);
        window.setPrefWidth(380);
        window.setMaxWidth(380);
        window.getStyleClass().add("popupWindow");
        window.setPrefHeight(Region.USE_COMPUTED_SIZE);
        window.setMaxHeight(Region.USE_PREF_SIZE);
        window.setVisible(false);

        closeButtonContainer.setPrefHeight(30);
        closeButtonContainer.getChildren().add(closeButtonPane);
        VBox.setMargin(closeButtonContainer, new Insets(0, 15, 0, 15));

        StackPane.setAlignment(closeButtonPane, Pos.BOTTOM_RIGHT);
        closeButtonPane.setPrefSize(25, 25);
        closeButtonPane.setMaxSize(25, 25);
        closeButtonPane.getChildren().addAll(closeButton, closeButtonIcon);
        closeButtonPane.setTranslateX(5);

        closeButton.setPrefWidth(25);
        closeButton.setPrefHeight(25);
        closeButton.setRipplerFill(Color.WHITE);
        closeButton.getStyleClass().add("popupWindowCloseButton");
        closeButton.setCursor(Cursor.HAND);
        closeButton.setOpacity(0);
        closeButton.setText(null);
        closeButton.setOnAction(e -> this.hide());

        closeButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> AnimationsClass.fadeAnimation(200, closeButton, 0, 1, false, 1, true));

        closeButton.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> AnimationsClass.fadeAnimation(200, closeButton, 1, 0, false, 1, true));

        closeButtonSVG.setContent(App.svgMap.get(SVG.CLOSE));

        closeButtonIcon.setShape(closeButtonSVG);
        closeButtonIcon.setMinSize(13, 13);
        closeButtonIcon.setPrefSize(13, 13);
        closeButtonIcon.setMaxSize(13, 13);
        closeButtonIcon.setMouseTransparent(true);
        closeButtonIcon.getStyleClass().add("menuIcon");

        title.setText("Edit hotkey");
        title.getStyleClass().add("popupWindowTitle");
        VBox.setMargin(title, new Insets(0, 15, 25, 15));

        buttonContainer.getChildren().addAll(mainButton, secondaryButton);
        VBox.setMargin(buttonContainer, new Insets(20, 0, 0, 0));
        buttonContainer.getStyleClass().add("buttonContainer");
        buttonContainer.setPadding(new Insets(20, 15, 20, 15));

        secondaryButton.setText("Cancel");
        secondaryButton.getStyleClass().add("menuButton");
        secondaryButton.setCursor(Cursor.HAND);
        secondaryButton.setOnAction(e -> this.hide());
        secondaryButton.setTextAlignment(TextAlignment.CENTER);
        secondaryButton.setPrefWidth(155);
        StackPane.setAlignment(secondaryButton, Pos.CENTER_RIGHT);

        mainButton.setText("Unset");
        mainButton.getStyleClass().add("mainButton");
        mainButton.setCursor(Cursor.HAND);
        mainButton.setTextAlignment(TextAlignment.CENTER);
        mainButton.setPrefWidth(155);
        StackPane.setAlignment(mainButton, Pos.CENTER_LEFT);

        window.getChildren().addAll(closeButtonContainer, title, hotkeyText, hotkeyBox, warningLabel, buttonContainer);
    }

    public void show(){
        if(mainController.closeConfirmationWindow.showing){
            mainController.closeConfirmationWindow.window.setVisible(false);
            mainController.closeConfirmationWindow.showing = false;
        }

        if(mainController.addYoutubeVideoWindow.showing){
            mainController.addYoutubeVideoWindow.window.setVisible(false);
            mainController.addYoutubeVideoWindow.showing = false;
        }

        this.showing = true;
        window.setVisible(true);

        mainController.popupWindowContainer.setMouseTransparent(false);
        AnimationsClass.fadeAnimation(100, mainController.popupWindowContainer, 0 , 1, false, 1, true);
    }

    public void hide(){
        this.showing = false;

        mainController.popupWindowContainer.setMouseTransparent(true);
        AnimationsClass.fadeAnimation(100, mainController.popupWindowContainer, 1, 0, false, 1, true);
    }
}
