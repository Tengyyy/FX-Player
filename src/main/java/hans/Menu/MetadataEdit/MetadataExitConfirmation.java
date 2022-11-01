package hans.Menu.MetadataEdit;

import com.jfoenix.controls.JFXButton;
import hans.AnimationsClass;
import hans.App;
import hans.Menu.MenuController;
import hans.SVG;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

public class MetadataExitConfirmation {

    MetadataEditPage metadataEditPage;
    MenuController menuController;

    VBox window = new VBox();
    SVGPath windowSVG = new SVGPath();
    Region windowIcon = new Region();
    Label title = new Label(), text = new Label();
    StackPane buttonContainer = new StackPane();
    JFXButton mainButton = new JFXButton(), secondaryButton = new JFXButton();

    StackPane closeButtonContainer = new StackPane();
    StackPane closeButtonPane = new StackPane();
    Region closeButtonIcon = new Region();
    SVGPath closeButtonSVG = new SVGPath();
    JFXButton closeButton = new JFXButton();

    MetadataExitConfirmation(MenuController menuController, MetadataEditPage metadataEditPage){
        this.metadataEditPage = metadataEditPage;
        this.menuController = menuController;

        menuController.metadataExitConfirmationWindowContainer.setId("confirmationWindowContainer");
        menuController.metadataExitConfirmationWindowContainer.getChildren().add(window);
        menuController.metadataExitConfirmationWindowContainer.setOpacity(0);
        menuController.metadataExitConfirmationWindowContainer.setMouseTransparent(true);

        window.setAlignment(Pos.TOP_CENTER);
        window.setPrefSize(380, 280);
        window.setMaxSize(380, 280);
        window.setId("confirmationWindow");
        window.setPadding(new Insets(0, 15, 20, 15));

        closeButtonContainer.setPrefHeight(30);
        closeButtonContainer.getChildren().add(closeButtonPane);

        StackPane.setAlignment(closeButtonPane, Pos.BOTTOM_RIGHT);
        closeButtonPane.setPrefSize(25, 25);
        closeButtonPane.setMaxSize(25, 25);
        closeButtonPane.getChildren().addAll(closeButton, closeButtonIcon);
        closeButtonPane.setTranslateX(5);

        closeButton.setPrefWidth(25);
        closeButton.setPrefHeight(25);
        closeButton.setRipplerFill(Color.WHITE);
        closeButton.setId("confirmationWindowCloseButton");
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


        windowSVG.setContent(App.svgMap.get(SVG.REMOVE));
        windowIcon.setShape(windowSVG);
        windowIcon.getStyleClass().add("menuIcon");
        windowIcon.setMinSize(35, 35);
        windowIcon.setPrefSize(35, 35);
        windowIcon.setMaxSize(35, 35);
        VBox.setMargin(windowIcon, new Insets(0, 0, 25, 0));

        title.setText("Discard Changes");
        title.setId("confirmationWindowTitle");
        VBox.setMargin(title, new Insets(0, 0, 25, 0));

        text.setText("Unsaved changes found. Are you sure you want to exit?");
        text.setWrapText(true);
        text.setId("confirmationWindowText");
        VBox.setMargin(text, new Insets(0, 0, 25, 0));



        buttonContainer.getChildren().addAll(secondaryButton, mainButton);
        VBox.setMargin(buttonContainer, new Insets(10, 0, 0, 0));

        secondaryButton.setRipplerFill(Color.WHITE);
        secondaryButton.setText("No, keep editing");
        secondaryButton.getStyleClass().add("secondaryButton");
        secondaryButton.setPadding(new Insets(8, 6, 8, 6));
        secondaryButton.setCursor(Cursor.HAND);
        secondaryButton.setOnAction(e -> this.hide());
        StackPane.setAlignment(secondaryButton, Pos.CENTER_LEFT);

        mainButton.setRipplerFill(Color.WHITE);
        mainButton.setText("Yes. Discard changes");
        mainButton.getStyleClass().addAll("mainButton", "confirmButton");
        mainButton.setPadding(new Insets(8, 6, 8, 6));
        mainButton.setCursor(Cursor.HAND);
        StackPane.setAlignment(mainButton, Pos.CENTER_RIGHT);

        window.getChildren().addAll(closeButtonContainer, windowIcon, title, text,  buttonContainer);
    }

    public void show(boolean closeMenu){
        if(closeMenu){
            mainButton.setOnAction(e -> {
                this.hide();
                menuController.closeMenu();
            });
        }
        else {
            mainButton.setOnAction(e -> {
                this.hide();
                metadataEditPage.exitMetadataEditPage();
            });
        }

        menuController.metadataExitConfirmationWindowContainer.setMouseTransparent(false);
        AnimationsClass.fadeAnimation(100, menuController.metadataExitConfirmationWindowContainer, 0 , 1, false, 1, true);
    }

    public void hide(){
        menuController.metadataExitConfirmationWindowContainer.setMouseTransparent(true);
        AnimationsClass.fadeAnimation(100, menuController.metadataExitConfirmationWindowContainer, 1, 0, false, 1, true);
    }
}
