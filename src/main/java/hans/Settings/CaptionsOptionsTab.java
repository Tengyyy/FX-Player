package hans.Settings;

import hans.App;
import hans.CaptionsController;
import hans.SVG;
import hans.Settings.CaptionsOptionsPane;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.TextAlignment;

public class CaptionsOptionsTab extends HBox {

    CaptionsController captionsController;
    CaptionsOptionsPane captionsOptionsPane;

    StackPane arrowPane = new StackPane();
    Label mainText = new Label();
    Label subText = new Label();

    Region arrowIcon = new Region();
    SVGPath arrowSVG = new SVGPath();


    CaptionsOptionsTab(CaptionsOptionsPane captionsOptionsPane, CaptionsController captionsController, boolean requiresSubText, boolean requiresArrow, String mainTextValue, String subTextValue){
        this.captionsController = captionsController;
        this.captionsOptionsPane = captionsOptionsPane;

        arrowSVG.setContent(App.svgMap.get(SVG.CHEVRON_RIGHT));

        this.setMinSize(255, 35);
        this.setPrefSize(255, 35);
        this.setMaxSize(255, 35);
        this.setPadding(new Insets(0, 10, 0, 10));
        this.getStyleClass().add("settingsPaneTab");
        this.setCursor(Cursor.HAND);
        this.getChildren().add(mainText);

        if(requiresSubText) this.getChildren().add(subText);
        if(requiresArrow) this.getChildren().add(arrowPane);


        mainText.setText(mainTextValue);
        mainText.getStyleClass().add("settingsPaneText");
        mainText.setPrefSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
        mainText.setMaxSize(235,35);

        if(requiresSubText){
            subText.setText(subTextValue);
            subText.setPrefHeight(35);
            Platform.runLater(() -> subText.setPrefWidth(220 - mainText.getWidth()));
            subText.setPadding(new Insets(0, 5, 0, 0));
            subText.setAlignment(Pos.CENTER_RIGHT);
            subText.setTextAlignment(TextAlignment.RIGHT);
            subText.getStyleClass().addAll("settingsPaneText", "settingsPaneSubText");
        }

        if(requiresArrow) {
            arrowPane.setMinSize(15, 35);
            arrowPane.setPrefSize(15, 35);
            arrowPane.setMaxSize(15, 35);
            arrowPane.getChildren().add(arrowIcon);

            arrowIcon.setMinSize(8, 13);
            arrowIcon.setPrefSize(8, 13);
            arrowIcon.setMaxSize(8, 13);
            arrowIcon.setShape(arrowSVG);
            arrowIcon.getStyleClass().add("settingsPaneIcon");
        }


        captionsOptionsPane.captionsOptionsBox.getChildren().add(this);
    }
}