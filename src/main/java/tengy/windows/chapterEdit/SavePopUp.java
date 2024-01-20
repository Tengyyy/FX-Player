package tengy.windows.chapterEdit;

import javafx.animation.FadeTransition;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import tengy.AnimationsClass;
import tengy.SVG;

import static tengy.Utilities.keyboardFocusOff;
import static tengy.Utilities.keyboardFocusOn;

public class SavePopUp extends StackPane{

    ScrollPane scrollPane;

    Region closeButtonIcon = new Region();
    SVGPath closeButtonSVG = new SVGPath();
    Button closeButton = new Button();

    VBox content = new VBox();
    Label label1 = new Label("Unable to save chapters");
    Label label2 = new Label("Make sure all the title fields are filled and chapter start times are in increasing order and don't exceed video duration.");

    StackPane buttonContainer = new StackPane();
    Button mainButton = new Button("Close");

    ChapterEditWindow chapterEditWindow;

    boolean showing = false;

    public SavePopUp(ChapterEditWindow chapterEditWindow){
        this.chapterEditWindow = chapterEditWindow;

        chapterEditWindow.popupContainer.getChildren().add(this);

        StackPane.setAlignment(closeButton, Pos.TOP_RIGHT);
        StackPane.setMargin(closeButton, new Insets(10, 10, 0 ,0));
        closeButton.setPrefWidth(25);
        closeButton.setPrefHeight(25);
        closeButton.getStyleClass().addAll("transparentButton", "popupWindowCloseButton");
        closeButton.setOnAction(e -> this.hide());
        closeButton.setFocusTraversable(false);
        closeButton.setGraphic(closeButtonIcon);
        closeButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(!newValue)
                keyboardFocusOff(closeButton);
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
        StackPane.setMargin(scrollPane, new Insets(0, 0, 70, 0));

        content.setSpacing(15);
        content.setPadding(new Insets(15, 30, 15, 15));
        content.getChildren().addAll(label1, label2);
        content.setAlignment(Pos.TOP_LEFT);

        label1.getStyleClass().add("chapterPopupTitle");
        label1.setWrapText(true);
        label1.setPadding(new Insets(5, 0, 5, 0));

        label2.getStyleClass().add("chapterPopupLabel");
        label2.setWrapText(true);


        StackPane.setAlignment(buttonContainer, Pos.BOTTOM_CENTER);
        buttonContainer.getChildren().add(mainButton);
        buttonContainer.getStyleClass().add("buttonContainer");
        buttonContainer.setPadding(new Insets(0, 15, 0, 15));
        buttonContainer.setPrefHeight(70);
        buttonContainer.setMaxHeight(70);

        mainButton.getStyleClass().add("menuButton");
        mainButton.setTextAlignment(TextAlignment.CENTER);
        mainButton.setPrefWidth(130);
        mainButton.setOnAction(e -> this.hide());
        mainButton.setFocusTraversable(false);
        mainButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(!newValue)
                keyboardFocusOff(mainButton);
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

        this.getStyleClass().add("popupWindow");
        this.setVisible(false);
        this.getChildren().addAll(scrollPane, buttonContainer, closeButton);
        this.setPrefSize(300, 260);
        this.setMaxSize(300, 260);
        this.setOnMouseClicked(e -> this.requestFocus());
    }

    public void show(){
        this.showing = true;
        this.setVisible(true);

        chapterEditWindow.popupContainer.setMouseTransparent(false);
        this.requestFocus();
        AnimationsClass.fadeAnimation(100, chapterEditWindow.popupContainer, 0 , 1, false, 1, true);
    }

    public void hide(){
        this.showing = false;

        chapterEditWindow.popupContainer.setMouseTransparent(true);

        chapterEditWindow.window.requestFocus();

        FadeTransition fadeTransition = new FadeTransition(Duration.millis(100),chapterEditWindow.popupContainer);
        fadeTransition.setFromValue(chapterEditWindow.popupContainer.getOpacity());
        fadeTransition.setToValue(0);
        fadeTransition.setOnFinished(e -> this.setVisible(false));
        fadeTransition.play();
    }


    private void updatePadding(boolean value){
        if(value) content.setPadding(new Insets(15, 18, 15, 15));
        else      content.setPadding(new Insets(15, 30, 15, 15));
    }

    public void changeFocus(){
        if(closeButton.isFocused()) keyboardFocusOn(mainButton);
        else keyboardFocusOn(closeButton);
    }

}
