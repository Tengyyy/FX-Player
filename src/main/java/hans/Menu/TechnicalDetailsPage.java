package hans.Menu;

import hans.App;
import hans.Menu.Queue.QueueItem;
import hans.Shell32Util;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class TechnicalDetailsPage {

    MenuController menuController;


    VBox technicalDetailsWrapper = new VBox();

    StackPane titlePane = new StackPane();
    Label title = new Label("Technical details");

    ScrollPane technicalDetailsScroll = new ScrollPane();

    StackPane imageViewWrapper = new StackPane();
    public StackPane imageViewContainer = new StackPane();
    public ImageView imageView = new ImageView();

    public VBox content = new VBox();

    public VBox textBox = new VBox();

    TechnicalDetailsPage(MenuController menuController){

        this.menuController = menuController;

        titlePane.setPadding(new Insets(55, 50, 20, 50));
        titlePane.getChildren().add(title);

        StackPane.setAlignment(title, Pos.CENTER_LEFT);
        title.getStyleClass().add("menuTitle");

        technicalDetailsScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        technicalDetailsScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        technicalDetailsScroll.getStyleClass().add("menuScroll");
        technicalDetailsScroll.setFitToWidth(true);
        technicalDetailsScroll.setFitToHeight(true);
        technicalDetailsScroll.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        technicalDetailsScroll.setBackground(Background.EMPTY);

        content.setBackground(Background.EMPTY);
        content.setPadding(new Insets(0, 50,20, 50));

        technicalDetailsScroll.setContent(content);

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
        content.getChildren().addAll(imageViewWrapper, textBox);
        content.setBackground(Background.EMPTY);

        textBox.setAlignment(Pos.TOP_LEFT);
        textBox.setPadding(new Insets(0, 15, 0, 15));
        textBox.setSpacing(10);

        technicalDetailsWrapper.getChildren().addAll(titlePane, technicalDetailsScroll);
        menuController.technicalDetailsContainer.getChildren().add(technicalDetailsWrapper);
    }

    public void loadTechnicalDetailsPage(QueueItem queueItem){

        if(queueItem.getMediaItem().getCover() != null){
            imageView.setImage(queueItem.getMediaItem().getCover());
            Color color = queueItem.getMediaItem().getCoverBackgroundColor();
            imageViewContainer.setStyle("-fx-background-color: rgba(" + color.getRed() * 256 +  "," + color.getGreen() * 256 + "," + color.getBlue() * 256 + ",0.7);");
        }
        else {
            imageView.setImage(queueItem.getMediaItem().getPlaceholderCover());
            imageViewContainer.setStyle("-fx-background-color: red;");
        }


        Map<String, String> map = queueItem.getMediaItem().getMediaDetails();

        if(map != null && !map.isEmpty()){
            createFileSection(map);
            if(map.containsKey("hasVideo") && Objects.equals(map.get("hasVideo"), "true")) createVideoSection(map);
            if(map.containsKey("hasAudio") && Objects.equals(map.get("hasAudio"), "true")) createAudioSection(map);
        }
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

    public void openTechnicalDetailsPage(){
        menuController.technicalDetailsContainer.setVisible(true);
    }

    public void closeTechnicalDetailsPage(){
        menuController.technicalDetailsContainer.setVisible(false);

        textBox.getChildren().clear();
        imageView.setImage(null);
        imageViewContainer.setStyle("-fx-background-color: transparent;");
    }

    public void enter(QueueItem queueItem){

        if(menuController.menuInTransition) return;

        menuController.menuBar.setActiveButton(null);

        loadTechnicalDetailsPage(queueItem);

        if(menuController.menuState == MenuState.CLOSED){
            if(!menuController.extended.get()) menuController.setMenuExtended(MenuState.TECHNICAL_DETAILS_OPEN);
            menuController.openMenu(MenuState.TECHNICAL_DETAILS_OPEN);
        }
        else {
            if(!menuController.extended.get()) menuController.extendMenu(MenuState.TECHNICAL_DETAILS_OPEN);
            else menuController.animateStateSwitch(MenuState.TECHNICAL_DETAILS_OPEN);
        }
    }
}
