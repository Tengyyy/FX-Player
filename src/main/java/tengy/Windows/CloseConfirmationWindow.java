package tengy.Windows;

import tengy.AnimationsClass;
import tengy.MainController;
import tengy.SVG;
import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

public class CloseConfirmationWindow {

    WindowController windowController;
    MainController mainController;

    VBox window = new VBox();
    HBox titleContainer = new HBox();
    Region warningIcon = new Region();
    SVGPath warningSVG = new SVGPath();
    Label title = new Label();
    Label text = new Label();
    StackPane buttonContainer = new StackPane();
    Button mainButton = new Button(), secondaryButton = new Button();

    StackPane closeButtonContainer = new StackPane();
    StackPane closeButtonPane = new StackPane();
    Region closeButtonIcon = new Region();
    SVGPath closeButtonSVG = new SVGPath();
    Button closeButton = new Button();

    public boolean showing = false;



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
        closeButton.getStyleClass().add("popupWindowCloseButton");
        closeButton.setCursor(Cursor.HAND);
        closeButton.setOpacity(0);
        closeButton.setText(null);
        closeButton.setOnAction(e -> this.hide());

        closeButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> AnimationsClass.fadeAnimation(200, closeButton, 0, 1, false, 1, true));

        closeButton.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> AnimationsClass.fadeAnimation(200, closeButton, 1, 0, false, 1, true));

        closeButtonSVG.setContent(SVG.CLOSE.getContent());

        closeButtonIcon.setShape(closeButtonSVG);
        closeButtonIcon.setMinSize(13, 13);
        closeButtonIcon.setPrefSize(13, 13);
        closeButtonIcon.setMaxSize(13, 13);
        closeButtonIcon.setMouseTransparent(true);
        closeButtonIcon.getStyleClass().add("menuIcon");

        titleContainer.getChildren().addAll(warningIcon, title);
        titleContainer.setSpacing(5);
        titleContainer.setAlignment(Pos.CENTER_LEFT);
        VBox.setMargin(titleContainer, new Insets(0, 15, 25, 15));

        warningSVG.setContent(SVG.WARNING.getContent());
        warningIcon.setMouseTransparent(true);
        warningIcon.getStyleClass().add("menuIcon");
        warningIcon.setShape(warningSVG);
        warningIcon.setMinSize(30, 30);
        warningIcon.setPrefSize(30, 30);
        warningIcon.setMaxSize(30, 30);

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
        secondaryButton.setCursor(Cursor.HAND);
        secondaryButton.setOnAction(e -> this.hide());
        secondaryButton.setTextAlignment(TextAlignment.CENTER);
        secondaryButton.setPrefWidth(155);
        StackPane.setAlignment(secondaryButton, Pos.CENTER_RIGHT);

        mainButton.setText("Close app");
        mainButton.getStyleClass().add("mainButton");
        mainButton.setCursor(Cursor.HAND);
        mainButton.setTextAlignment(TextAlignment.CENTER);
        mainButton.setPrefWidth(155);
        mainButton.setOnAction(e -> mainController.closeApp());
        StackPane.setAlignment(mainButton, Pos.CENTER_LEFT);

        window.getChildren().addAll(closeButtonContainer, titleContainer, text, buttonContainer);
    }

    public void show(){

        windowController.updateState(WindowState.CLOSE_CONFIRMATION_WINDOW_OPEN);

        this.showing = true;

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
        fadeTransition.play();    }
}
