package tengy.Windows.ChapterEdit;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import tengy.AnimationsClass;
import tengy.SVG;

public class SavePopUp extends StackPane{

    ScrollPane scrollPane;

    StackPane closeButtonPane = new StackPane();
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

    SavePopUp(ChapterEditWindow chapterEditWindow){
        this.chapterEditWindow = chapterEditWindow;

        chapterEditWindow.popupContainer.getChildren().add(this);

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

        mainButton.getStyleClass().add("mainButton");
        mainButton.setCursor(Cursor.HAND);
        mainButton.setTextAlignment(TextAlignment.CENTER);
        mainButton.setPrefWidth(130);
        mainButton.setOnAction(e -> this.hide());

        StackPane.setAlignment(mainButton, Pos.CENTER_RIGHT);

        this.getStyleClass().add("popupWindow");
        this.setVisible(false);
        this.getChildren().addAll(scrollPane, buttonContainer, closeButtonPane);
        this.setPrefSize(300, 260);
        this.setMaxSize(300, 260);
    }

    public void show(){
        this.showing = true;
        this.setVisible(true);

        chapterEditWindow.popupContainer.setMouseTransparent(false);
        AnimationsClass.fadeAnimation(100, chapterEditWindow.popupContainer, 0 , 1, false, 1, true);
    }

    public void hide(){
        this.showing = false;

        chapterEditWindow.popupContainer.setMouseTransparent(true);

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

}
