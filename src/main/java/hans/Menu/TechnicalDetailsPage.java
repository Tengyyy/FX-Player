package hans.Menu;

import hans.AnimationsClass;
import hans.App;
import hans.SVG;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.Map;
import java.util.Objects;

public class TechnicalDetailsPage {

    MenuController menuController;

    Button closeButton = new Button();
    Region closeIcon = new Region();

    StackPane backButtonPane = new StackPane();
    Button backButton = new Button();
    Region backIcon = new Region();

    SVGPath closeIconSVG = new SVGPath();
    SVGPath backIconSVG = new SVGPath();

    StackPane closeButtonBar = new StackPane();
    StackPane closeButtonPane = new StackPane();

    StackPane imageViewWrapper = new StackPane();
    public StackPane imageViewContainer = new StackPane();
    public ImageView imageView = new ImageView();

    public VBox content = new VBox();

    public VBox textBox = new VBox();

    TechnicalDetailsPage(MenuController menuController){

        this.menuController = menuController;

        backIconSVG.setContent(App.svgMap.get(SVG.ARROW_LEFT));
        backIcon.setShape(backIconSVG);
        backIcon.setPrefSize(20, 20);
        backIcon.setMaxSize(20, 20);
        backIcon.setId("backIcon");
        backIcon.setMouseTransparent(true);


        backButton.setPrefSize(40, 40);
        backButton.setMaxSize(40, 40);
        backButton.setCursor(Cursor.HAND);
        backButton.setBackground(Background.EMPTY);

        backButton.setOnAction(e -> exitTechnicalDetailsPage());

        backButton.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> AnimationsClass.AnimateBackgroundColor(backIcon, (Color) backIcon.getBackground().getFills().get(0).getFill(), Color.rgb(255, 255, 255), 200));

        backButton.addEventHandler(MouseEvent.MOUSE_EXITED, e -> AnimationsClass.AnimateBackgroundColor(backIcon, (Color) backIcon.getBackground().getFills().get(0).getFill(), Color.rgb(200, 200, 200), 200));

        backButtonPane.setPrefSize(50, 50);
        backButtonPane.setMaxSize(50, 50);
        backButtonPane.getChildren().addAll(backButton, backIcon);
        StackPane.setAlignment(backButtonPane, Pos.CENTER_LEFT);



        closeIconSVG.setContent(App.svgMap.get(SVG.CLOSE));
        closeIcon.setShape(closeIconSVG);
        closeIcon.setPrefSize(20, 20);
        closeIcon.setMaxSize(20, 20);
        closeIcon.setId("closeIcon");
        closeIcon.setMouseTransparent(true);

        closeButton.setPrefSize(40, 40);
        closeButton.setMaxSize(40, 40);
        closeButton.setCursor(Cursor.HAND);
        closeButton.setBackground(Background.EMPTY);

        closeButton.setOnAction(e -> menuController.closeMenu());

        closeButton.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> AnimationsClass.AnimateBackgroundColor(closeIcon, (Color) closeIcon.getBackground().getFills().get(0).getFill(), Color.rgb(255, 255, 255), 200));

        closeButton.addEventHandler(MouseEvent.MOUSE_EXITED, e -> AnimationsClass.AnimateBackgroundColor(closeIcon, (Color) closeIcon.getBackground().getFills().get(0).getFill(), Color.rgb(200, 200, 200), 200));

        closeButtonPane.setPrefSize(50, 50);
        closeButtonPane.setMaxSize(50, 50);
        closeButtonPane.getChildren().addAll(closeButton, closeIcon);
        StackPane.setAlignment(closeButtonPane, Pos.CENTER_RIGHT);

        closeButtonBar.setPrefHeight(60);
        closeButtonBar.setMinHeight(60);
        closeButtonBar.getChildren().addAll(backButtonPane, closeButtonPane);

        imageViewWrapper.getChildren().add(imageViewContainer);
        imageViewWrapper.setPadding(new Insets(20, 0, 50, 0));
        imageViewWrapper.setBackground(Background.EMPTY);

        imageViewContainer.getChildren().add(imageView);
        imageViewContainer.setId("imageViewContainer");
        imageViewContainer.maxWidthProperty().bind(Bindings.min(400, menuController.menu.widthProperty().multiply(0.7)));

        imageView.setPreserveRatio(true);
        imageView.fitWidthProperty().bind(Bindings.min(400, menuController.menu.widthProperty().multiply(0.7)));
        imageView.fitHeightProperty().bind(Bindings.min(225, imageView.fitWidthProperty().multiply(9).divide(16)));

        content.setAlignment(Pos.TOP_CENTER);
        content.getChildren().addAll(closeButtonBar, imageViewWrapper, textBox);
        content.setBackground(Background.EMPTY);
        content.setPadding(new Insets(0, 0, 20, 0));
        menuController.technicalDetailsScroll.setContent(content);

        textBox.setAlignment(Pos.TOP_LEFT);
        textBox.setPadding(new Insets(0, 15, 0, 15));
        textBox.setSpacing(10);
    }

    public void enterTechnicalDetailsPage(MenuObject menuObject){
        imageView.setImage(menuObject.getMediaItem().getCover());

        Color color = menuObject.getMediaItem().getCoverBackgroundColor();

        if(color != null) imageViewContainer.setStyle("-fx-background-color: rgba(" + color.getRed() * 256 +  "," + color.getGreen() * 256 + "," + color.getBlue() * 256 + ",0.7);");

        Map<String, String> map = menuObject.getMediaItem().getMediaDetails();
        if(map != null && !map.isEmpty()){
            createFileSection(map);
            if(map.containsKey("hasVideo") && Objects.equals(map.get("hasVideo"), "true")) createVideoSection(map);
            if(map.containsKey("hasAudio") && Objects.equals(map.get("hasAudio"), "true")) createAudioSection(map);
        }

        menuController.metadataScroll.setVisible(false);
        menuController.technicalDetailsScroll.setVisible(true);
    }


    public void exitTechnicalDetailsPage(){
        menuController.metadataScroll.setVisible(true);
        menuController.technicalDetailsScroll.setVisible(false);

        textBox.getChildren().clear();
        imageView.setImage(null);
        imageViewContainer.setStyle("-fx-background-color: transparent;");
    }

    private void createTitle(String title){
        Label label = new Label(title);
        label.getStyleClass().add("metadataTitle");
        label.setMaxWidth(Double.MAX_VALUE);
        label.setWrapText(true);
        label.setLineSpacing(5);
        label.setPadding(new Insets(0, 0, 5, 0));

        textBox.getChildren().add(label);
    }

    private  void createItem(String key, double keyWidth, String value){
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.TOP_LEFT);

        Label keyText = new Label(key);
        keyText.getStyleClass().add("keyText");
        keyText.setMinWidth(keyWidth);
        keyText.setMaxWidth(keyWidth);

        TextFlow textFlow = new TextFlow();
        Text valueText = new Text(value);
        valueText.getStyleClass().add("valueText");
        textFlow.getChildren().add(valueText);
        hBox.getChildren().addAll(keyText, textFlow);
        textBox.getChildren().add(hBox);

    }

    private void createFileSection(Map<String, String> map){
        createTitle("File");
        if(map.containsKey("name")) createItem("File name:", 120, map.get("name"));
        if(map.containsKey("path")) createItem("File path:", 120, map.get("path"));
        if(map.containsKey("size")) createItem("File size:", 120, map.get("size"));
        if(map.containsKey("modified")) createItem("Last modified:", 120, map.get("modified"));

    }

    private void createVideoSection(Map<String, String> map){
        createTitle("Video");

        if(map.containsKey("duration")) createItem("Duration:", 120, map.get("duration"));
        if(map.containsKey("format")) createItem("Format:", 120, map.get("format"));

        if(map.containsKey("videoCodec")) createItem("Video codec:", 120, map.get("videoCodec"));
        if(map.containsKey("frameRate")) createItem("Frame rate:", 120, map.get("frameRate"));
        if(map.containsKey("resolution")) createItem("Resolution:", 120, map.get("resolution"));
        if(map.containsKey("videoBitrate")) createItem("Video Bitrate:", 120, map.get("videoBitrate"));
    }

    private void createAudioSection(Map<String, String> map){
        createTitle("Audio");
        if(!map.containsKey("hasVideo") || Objects.equals(map.get("hasVideo"), "false")){
            if(map.containsKey("duration")) createItem("Duration:", 120, map.get("duration"));
            if(map.containsKey("format")) createItem("Format:", 120, map.get("format"));
        }

        if(map.containsKey("audioCodec")) createItem("Audio codec:", 120, map.get("audioCodec"));
        if(map.containsKey("audioBitrate")) createItem("Audio bitrate:", 120, map.get("audioBitrate"));
        if(map.containsKey("sampleRate")) createItem("Sampling rate:", 120, map.get("sampleRate"));
        if(map.containsKey("audioChannels")) createItem("Audio channels:", 120, map.get("audioChannels"));

    }

}
