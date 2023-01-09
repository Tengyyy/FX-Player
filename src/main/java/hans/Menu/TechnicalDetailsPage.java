package hans.Menu;

import com.jfoenix.controls.JFXButton;
import hans.AnimationsClass;
import hans.App;
import hans.SVG;
import hans.Shell32Util;
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

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
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

        backButton.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> AnimationsClass.animateBackgroundColor(backIcon, (Color) backIcon.getBackground().getFills().get(0).getFill(), Color.rgb(255, 255, 255), 200));

        backButton.addEventHandler(MouseEvent.MOUSE_EXITED, e -> AnimationsClass.animateBackgroundColor(backIcon, (Color) backIcon.getBackground().getFills().get(0).getFill(), Color.rgb(200, 200, 200), 200));

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

        closeButton.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> AnimationsClass.animateBackgroundColor(closeIcon, (Color) closeIcon.getBackground().getFills().get(0).getFill(), Color.rgb(255, 255, 255), 200));

        closeButton.addEventHandler(MouseEvent.MOUSE_EXITED, e -> AnimationsClass.animateBackgroundColor(closeIcon, (Color) closeIcon.getBackground().getFills().get(0).getFill(), Color.rgb(200, 200, 200), 200));

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


        if(menuObject.getMediaItem().getCover() != null){
            imageView.setImage(menuObject.getMediaItem().getCover());
            Color color = menuObject.getMediaItem().getCoverBackgroundColor();
            imageViewContainer.setStyle("-fx-background-color: rgba(" + color.getRed() * 256 +  "," + color.getGreen() * 256 + "," + color.getBlue() * 256 + ",0.7);");
        }
        else {
            imageView.setImage(menuObject.getMediaItem().getPlaceholderCover());
            imageViewContainer.setStyle("-fx-background-color: red;");
        }


        Map<String, String> map = menuObject.getMediaItem().getMediaDetails();
        if(map != null && !map.isEmpty()){
            createFileSection(map);
            if(map.containsKey("hasVideo") && Objects.equals(map.get("hasVideo"), "true")) createVideoSection(map);
            if(map.containsKey("hasAudio") && Objects.equals(map.get("hasAudio"), "true")) createAudioSection(map);
        }

        menuController.queueScroll.setVisible(false);
        menuController.technicalDetailsScroll.setVisible(true);

        menuController.menuState = MenuState.TECHNICAL_DETAILS_OPEN;
    }


    public void exitTechnicalDetailsPage(){
        menuController.queueScroll.setVisible(true);
        menuController.technicalDetailsScroll.setVisible(false);

        textBox.getChildren().clear();
        imageView.setImage(null);
        imageViewContainer.setStyle("-fx-background-color: transparent;");

        menuController.menuState = MenuState.QUEUE_OPEN;
    }

    private void createTitle(String title){
        Label label = new Label(title);
        label.getStyleClass().add("metadataTitle");
        label.setMaxWidth(Double.MAX_VALUE);
        label.setWrapText(true);
        label.setLineSpacing(5);
        label.setPadding(new Insets(20, 0, 5, 0));

        textBox.getChildren().add(label);
    }

    private  Text createItem(String key, String value){
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.TOP_LEFT);

        Label keyText = new Label(key);
        keyText.getStyleClass().add("keyText");
        keyText.setMinWidth(125);
        keyText.setMaxWidth(125);
        hBox.setPadding(new Insets(0, 0, 0, 20));

        TextFlow textFlow = new TextFlow();
        Text valueText = new Text(value);
        valueText.getStyleClass().add("valueText");
        textFlow.getChildren().add(valueText);
        hBox.getChildren().addAll(keyText, textFlow);
        textBox.getChildren().add(hBox);

        return valueText;

    }

    private void createFileSection(Map<String, String> map){
        createTitle("File");
        if(map.containsKey("name")) createItem("File name:", map.get("name"));
        if(map.containsKey("path")){
            Text text = createItem("File path:", map.get("path"));
            text.setCursor(Cursor.HAND);
            text.setOnMouseEntered(e -> text.setUnderline(true));
            text.setOnMouseExited(e -> text.setUnderline(false));

            text.setOnMouseClicked(e -> {
                if(App.isWindows){
                    Shell32Util.SHOpenFolderAndSelectItems(new File(map.get("path")));
                }
                else if(Desktop.isDesktopSupported()){
                    Desktop desktop = Desktop.getDesktop();
                        File file = new File(map.get("path"));

                        if(desktop.isSupported(Desktop.Action.BROWSE_FILE_DIR)){
                            desktop.browseFileDirectory(new File(map.get("path")));
                        }
                        else if(desktop.isSupported(Desktop.Action.OPEN)){
                            try {
                                desktop.open(file.getParentFile());
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
            });
        }
        if(map.containsKey("size")) createItem("File size:", map.get("size"));
        if(map.containsKey("modified")) createItem("Last modified:", map.get("modified"));
        if(map.containsKey("format")) createItem("Format:", map.get("format"));

    }

    private void createVideoSection(Map<String, String> map){
        createTitle("Video");

        if(map.containsKey("videoDuration")) createItem("Duration:", map.get("videoDuration"));
        else if(map.containsKey("duration")) createItem("Duration:", map.get("duration"));
        if(map.containsKey("videoCodec")) createItem("Codec:", map.get("videoCodec"));
        if(map.containsKey("frameRate")) createItem("Frame rate:", map.get("frameRate"));
        if(map.containsKey("resolution")) createItem("Resolution:", map.get("resolution"));
        if(map.containsKey("videoBitrate")) createItem("Bitrate:", map.get("videoBitrate"));
    }

    private void createAudioSection(Map<String, String> map){
        createTitle("Audio");
        if(map.containsKey("audioDuration")) createItem("Duration:", map.get("audioDuration"));
        else if((!map.containsKey("hasVideo") || Objects.equals(map.get("hasVideo"), "false")) && map.containsKey("duration")){
            createItem("Duration:", map.get("duration"));
        }
        if(map.containsKey("audioCodec")) createItem("Codec:", map.get("audioCodec"));
        if(map.containsKey("audioBitrate")) createItem("Bitrate:", map.get("audioBitrate"));
        if(map.containsKey("audioBitDepth")) createItem("Bit depth", map.get("audioBitDepth"));
        if(map.containsKey("sampleRate")) createItem("Sampling rate:", map.get("sampleRate"));
        if(map.containsKey("audioChannels")) createItem("Channels:", map.get("audioChannels"));

    }

}
