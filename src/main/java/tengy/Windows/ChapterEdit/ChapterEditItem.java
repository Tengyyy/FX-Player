package tengy.Windows.ChapterEdit;

import com.github.kokorin.jaffree.ffprobe.Chapter;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;
import tengy.ControlTooltip;
import tengy.SVG;
import tengy.Utilities;

public class ChapterEditItem extends StackPane {

    ChapterEditWindow chapterEditWindow;

    int index;
    Label indexLabel = new Label();

    VBox textFieldContainer = new VBox();
    HBox titleContainer = new HBox();
    Label titleLabel = new Label("Title");
    TextField titleField = new TextField();

    HBox startTimeContainer = new HBox();
    Label startTimeLabel = new Label("Start time");
    TextField startTimeField = new TextField();
    boolean timeValid = false;

    StackPane removeButtonContainer = new StackPane();
    Button removeButton = new Button();
    Region removeIcon = new Region();
    SVGPath removeSVG = new SVGPath();


    ChapterEditItem(ChapterEditWindow chapterEditWindow, Chapter chapter){
        this.chapterEditWindow = chapterEditWindow;

        this.getChildren().addAll(indexLabel, textFieldContainer, removeButtonContainer);
        this.getStyleClass().add("chapterContainer");

        StackPane.setAlignment(indexLabel, Pos.CENTER_LEFT);
        index = chapterEditWindow.chapterEditItems.size();

        indexLabel.setText((index + 1) + ".");
        indexLabel.getStyleClass().add("indexLabel");
        indexLabel.setMouseTransparent(true);
        indexLabel.setPrefWidth(50);
        indexLabel.setMaxWidth(50);
        indexLabel.setAlignment(Pos.CENTER);


        textFieldContainer.setSpacing(10);
        textFieldContainer.getChildren().addAll(titleContainer, startTimeContainer);
        textFieldContainer.setFillWidth(true);
        StackPane.setMargin(textFieldContainer, new Insets(0, 0, 0, 50));

        titleContainer.setSpacing(10);
        titleContainer.getChildren().addAll(titleLabel, titleField);
        titleContainer.setAlignment(Pos.CENTER_LEFT);

        titleLabel.getStyleClass().add("chapterText");
        titleLabel.setMinWidth(80);
        titleLabel.setPrefWidth(80);
        titleLabel.setMaxWidth(80);

        if(chapter != null){
            String title = chapter.getTag("title");
            titleField.setText(title);

            Double startTime = chapter.getStartTime();
            if(startTime != null) startTimeField.setText(Utilities.getTime(Duration.seconds(startTime)));
        }

        titleField.getStyleClass().add("customTextField");
        titleField.setPrefHeight(30);
        titleField.setMinHeight(30);
        titleField.setMaxHeight(30);
        titleField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if(!newValue.isEmpty()) chapterEditWindow.changesMade.set(true);
        });
        HBox.setHgrow(titleField, Priority.ALWAYS);

        startTimeContainer.setSpacing(10);
        startTimeContainer.getChildren().addAll(startTimeLabel, startTimeField);
        startTimeContainer.setAlignment(Pos.CENTER_LEFT);

        startTimeLabel.getStyleClass().add("chapterText");
        startTimeLabel.setMinWidth(80);
        startTimeLabel.setPrefWidth(80);
        startTimeLabel.setMaxWidth(80);

        startTimeField.getStyleClass().add("customTextField");
        startTimeField.setPromptText("hh:mm:ss");
        startTimeField.setPrefHeight(30);
        startTimeField.setMinHeight(30);
        startTimeField.setMaxHeight(30);
        startTimeField.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);");
        startTimeField.textProperty().addListener((observableValue, oldValue, newValue) -> {

        });
        HBox.setHgrow(startTimeField, Priority.ALWAYS);


        removeButtonContainer.getChildren().addAll(removeButton, removeIcon);
        StackPane.setAlignment(removeButtonContainer, Pos.TOP_RIGHT);
        removeButtonContainer.setTranslateY(-19);
        removeButtonContainer.setTranslateX(19);
        removeButtonContainer.setPrefSize(20, 20);
        removeButtonContainer.setMaxSize(20, 20);

        removeSVG.setContent(SVG.CLOSE.getContent());
        removeIcon.setShape(removeSVG);
        removeIcon.setPrefSize(10, 10);
        removeIcon.setMaxSize(10, 10);
        removeIcon.getStyleClass().add("menuIcon");
        removeIcon.setMouseTransparent(true);

        removeButton.setPrefSize(20, 20);
        removeButton.setMaxSize(20, 20);
        removeButton.getStyleClass().add("removeButton");
        removeButton.setOnAction(e -> remove());
        StackPane.setAlignment(removeButton, Pos.TOP_RIGHT);

    }


    private void remove(){
        for(int i = index+1; i < chapterEditWindow.chapterEditItems.size(); i++){
            ChapterEditItem chapterEditItem = chapterEditWindow.chapterEditItems.get(i);
            chapterEditItem.index--;
            chapterEditItem.indexLabel.setText(String.valueOf(chapterEditItem.index+1));
        }

        chapterEditWindow.chapterEditItems.remove(this);
        chapterEditWindow.content.getChildren().remove(this);
    }
}
