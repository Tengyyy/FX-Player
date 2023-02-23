package hans;

import com.jfoenix.controls.JFXButton;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.TextAlignment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddYoutubeVideoWindow {

    MainController mainController;

    VBox window = new VBox();
    Label title = new Label();
    TextField textField = new TextField();
    StackPane buttonContainer = new StackPane();
    JFXButton mainButton = new JFXButton(), secondaryButton = new JFXButton();

    StackPane closeButtonContainer = new StackPane();
    StackPane closeButtonPane = new StackPane();
    Region closeButtonIcon = new Region();
    SVGPath closeButtonSVG = new SVGPath();
    JFXButton closeButton = new JFXButton();

    boolean showing = false;


    String pattern = "^(?:https?:)?(?:\\/\\/)?(?:youtu\\.be\\/|(?:www\\.|m\\.)?youtube\\.com\\/(?:watch|v|embed)(?:\\.php)?(?:\\?.*v=|\\/))([a-zA-Z0-9\\_-]{7,15})(?:[\\?&][a-zA-Z0-9\\_-]+=[a-zA-Z0-9\\_-]+)*(?:[&\\/\\#].*)?$";
    Pattern regexPatern = Pattern.compile(pattern);

    public AddYoutubeVideoWindow(MainController mainController){
        this.mainController = mainController;

        mainController.addYoutubeVideoWindowContainer.setId("youtubeWindowContainer");
        mainController.addYoutubeVideoWindowContainer.getChildren().add(window);
        mainController.addYoutubeVideoWindowContainer.setOpacity(0);
        mainController.addYoutubeVideoWindowContainer.setMouseTransparent(true);

        window.setAlignment(Pos.TOP_LEFT);
        window.setPrefWidth(380);
        window.setMaxWidth(380);
        window.setId("youtubeWindow");
        window.setPrefHeight(Region.USE_COMPUTED_SIZE);
        window.setMaxHeight(Region.USE_PREF_SIZE);

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
        closeButton.setId("youtubeWindowCloseButton");
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

        title.setText("Add YouTube video(s)");
        title.setId("youtubeWindowTitle");
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
        VBox.setMargin(textField, new Insets(0, 15, 0, 15));


        buttonContainer.getChildren().addAll(mainButton, secondaryButton);
        VBox.setMargin(buttonContainer, new Insets(20, 0, 0, 0));
        buttonContainer.setId("buttonContainer");
        buttonContainer.setPadding(new Insets(20, 15, 20, 15));

        secondaryButton.setText("Cancel");
        secondaryButton.getStyleClass().add("menuButton");
        secondaryButton.setCursor(Cursor.HAND);
        secondaryButton.setOnAction(e -> this.hide());
        secondaryButton.setTextAlignment(TextAlignment.CENTER);
        secondaryButton.setPrefWidth(155);
        secondaryButton.setRipplerFill(Color.TRANSPARENT);
        StackPane.setAlignment(secondaryButton, Pos.CENTER_RIGHT);

        mainButton.setText("Add");
        mainButton.getStyleClass().addAll("mainButton");
        mainButton.setCursor(Cursor.HAND);
        mainButton.setTextAlignment(TextAlignment.CENTER);
        mainButton.setPrefWidth(155);
        mainButton.setRipplerFill(Color.TRANSPARENT);
        mainButton.setDisable(true);
        StackPane.setAlignment(mainButton, Pos.CENTER_LEFT);

        window.getChildren().addAll(closeButtonContainer, title, textField, buttonContainer);
    }

    public void show(){

        this.showing = true;

        mainController.addYoutubeVideoWindowContainer.setMouseTransparent(false);
        AnimationsClass.fadeAnimation(100, mainController.addYoutubeVideoWindowContainer, 0 , 1, false, 1, true);
    }

    public void hide(){

        this.showing = false;

        mainController.addYoutubeVideoWindowContainer.setMouseTransparent(true);
        AnimationsClass.fadeAnimation(100, mainController.addYoutubeVideoWindowContainer, 1, 0, false, 1, true);
    }
}
