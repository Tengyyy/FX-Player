package tengy.PlaybackSettings;

import tengy.SVG;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;


public class PlaybackSettingsHomeTab extends HBox {

    PlaybackSettingsHomeController playbackSettingsHomeController;

    StackPane iconPane = new StackPane();
    StackPane arrowPane = new StackPane();
    Label mainText = new Label();
    Label subText = new Label();

    Region arrowIcon = new Region();
    Region mainIcon = new Region();

    SVGPath arrowSVG = new SVGPath();

    PlaybackSettingsHomeTab(PlaybackSettingsHomeController playbackSettingsHomeController, boolean requiresSubText, SVGPath iconSVG, String mainTextValue, String subTextValue){

        this.playbackSettingsHomeController = playbackSettingsHomeController;

        this.setMinSize(235, 35);
        this.setPrefSize(235, 35);
        this.setMaxSize(235, 35);
        this.setPadding(new Insets(0, 10, 0, 10));
        this.getStyleClass().add("settingsPaneTab");
        this.setCursor(Cursor.HAND);
        this.getChildren().addAll(iconPane, mainText, arrowPane);
        if(requiresSubText) this.getChildren().add(2, subText);

        arrowSVG.setContent(SVG.CHEVRON_RIGHT.getContent());

        iconPane.setMinSize(25, 35);
        iconPane.setPrefSize(25, 35);
        iconPane.setMaxSize(25, 35);
        iconPane.setPadding(new Insets(0, 5, 0, 0));
        iconPane.getChildren().add(mainIcon);

        mainIcon.setMinSize(15, 15);
        mainIcon.setPrefSize(15, 15);
        mainIcon.setMaxSize(15, 15);
        mainIcon.getStyleClass().add("settingsHomeIcon");
        mainIcon.setShape(iconSVG);

        mainText.setText(mainTextValue);
        mainText.getStyleClass().add("settingsPaneText");
        mainText.setMinSize(110, 35);
        mainText.setPrefSize(175, 35);
        mainText.setMaxSize(175,35);

        if(requiresSubText){
            subText.setText(subTextValue);
            subText.setMinSize(65, 35);
            subText.setPrefSize(65,35);
            subText.setMaxSize(65, 35);
            subText.setPadding(new Insets(0, 5, 0, 0));
            subText.setAlignment(Pos.CENTER_RIGHT);
            subText.getStyleClass().addAll("settingsPaneText", "settingsPaneSubText");
        }

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
}
