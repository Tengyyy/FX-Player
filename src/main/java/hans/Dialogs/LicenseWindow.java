package hans.Dialogs;

import com.sandec.mdfx.MarkdownView;
import hans.AnimationsClass;
import hans.App;
import hans.MainController;
import hans.SVG;
import javafx.animation.FadeTransition;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
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
import java.util.Objects;

public class LicenseWindow {

    MainController mainController;


    StackPane window = new StackPane();

    VBox windowContainer = new VBox();

    VBox titleContainer = new VBox();
    Label title = new Label("License Terms");

    ScrollPane markdownScroll = new ScrollPane();
    MarkdownView markdownView;

    StackPane buttonContainer = new StackPane();
    Button mainButton = new Button("Close");

    StackPane closeButtonPane = new StackPane();
    Region closeButtonIcon = new Region();
    SVGPath closeButtonSVG = new SVGPath();
    Button closeButton = new Button();

    boolean showing = false;

    public LicenseWindow(MainController mainController){
        this.mainController = mainController;

        mainController.popupWindowContainer.getChildren().add(window);

        window.setAlignment(Pos.TOP_LEFT);

        window.prefWidthProperty().bind(Bindings.max(500, Bindings.min(800, mainController.videoImageViewWrapper.widthProperty().multiply(0.5))));
        window.maxWidthProperty().bind(Bindings.max(500, Bindings.min(800, mainController.videoImageViewWrapper.widthProperty().multiply(0.5))));

        window.prefHeightProperty().bind(Bindings.max(350, Bindings.min(1000, mainController.videoImageViewWrapper.heightProperty().multiply(0.8))));
        window.maxHeightProperty().bind(Bindings.max(350, Bindings.min(1000, mainController.videoImageViewWrapper.heightProperty().multiply(0.8))));

        window.getStyleClass().add("popupWindow");
        window.setVisible(false);
        window.getChildren().addAll(windowContainer, buttonContainer, closeButtonPane);

        StackPane.setAlignment(closeButtonPane, Pos.TOP_RIGHT);
        StackPane.setMargin(closeButtonPane, new Insets(15, 15, 0 ,0));
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

        initializeMarkdownView();

        markdownScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        markdownScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        markdownScroll.getStyleClass().add("menuScroll");
        markdownScroll.setFitToWidth(true);
        markdownScroll.setFitToHeight(true);
        markdownScroll.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        markdownScroll.setBackground(Background.EMPTY);
        markdownScroll.setContent(markdownView);


        windowContainer.setPadding(new Insets(15, 15, 15, 15));
        windowContainer.getChildren().addAll(titleContainer, markdownScroll);
        windowContainer.setSpacing(20);
        StackPane.setMargin(windowContainer, new Insets(0, 0, 80, 0));

        titleContainer.getChildren().addAll(title);
        titleContainer.setPadding(new Insets(5, 0, 5, 0));

        title.getStyleClass().addAll("popupWindowTitle", "licenseWindowTitle");




        StackPane.setAlignment(buttonContainer, Pos.BOTTOM_CENTER);
        buttonContainer.getChildren().add(mainButton);
        buttonContainer.getStyleClass().add("buttonContainer");
        buttonContainer.setPadding(new Insets(0, 15, 0, 15));
        buttonContainer.setPrefHeight(70);
        buttonContainer.setMaxHeight(70);

        mainButton.getStyleClass().add("mainButton");
        mainButton.setCursor(Cursor.HAND);
        mainButton.setTextAlignment(TextAlignment.CENTER);
        mainButton.setPrefWidth(230);
        mainButton.setOnAction(e -> this.hide());
        StackPane.setAlignment(mainButton, Pos.CENTER_RIGHT);
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

        if(mainController.hotkeyChangeWindow.showing){
            mainController.hotkeyChangeWindow.window.setVisible(false);
            mainController.hotkeyChangeWindow.showing = false;
        }

        if(mainController.thirdPartySoftwareWindow.showing){
            mainController.thirdPartySoftwareWindow.window.setVisible(false);
            mainController.thirdPartySoftwareWindow.showing = false;
        }

        this.showing = true;
        window.setVisible(true);

        mainController.popupWindowContainer.setMouseTransparent(false);
        AnimationsClass.fadeAnimation(100, mainController.popupWindowContainer, 0 , 1, false, 1, true);
    }

    public void hide(){
        this.showing = false;

        mainController.popupWindowContainer.setMouseTransparent(true);

        FadeTransition fadeTransition = new FadeTransition(Duration.millis(100), mainController.popupWindowContainer);
        fadeTransition.setFromValue(mainController.popupWindowContainer.getOpacity());
        fadeTransition.setToValue(0);
        fadeTransition.setOnFinished(e -> {
            window.setVisible(false);
            markdownScroll.setVvalue(0);
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
        markdownView.setPadding(new Insets(15, 0, 15, 0));
    }
}
