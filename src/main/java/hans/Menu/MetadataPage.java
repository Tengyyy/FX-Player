package hans.Menu;

import com.jfoenix.controls.JFXButton;
import hans.AnimationsClass;
import hans.App;
import hans.MediaItems.MediaItem;
import hans.SVG;
import hans.Utilities;
import javafx.animation.Animation;
import javafx.animation.PauseTransition;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class MetadataPage {

    MenuController menuController;

    SVGPath closeIconSVG = new SVGPath();
    SVGPath backIconSVG = new SVGPath();

    StackPane closeButtonBar = new StackPane();
    StackPane closeButtonPane = new StackPane();

    Button closeButton = new Button();
    Region closeIcon = new Region();

    StackPane backButtonPane = new StackPane();
    Button backButton = new Button();
    Region backIcon = new Region();

    public VBox content = new VBox();

    StackPane imageViewWrapper = new StackPane();
    public StackPane imageViewContainer = new StackPane();
    public ImageView imageView = new javafx.scene.image.ImageView();

    public VBox textBox = new VBox();

    public SVGPath copySVG = new SVGPath(), checkSVG = new SVGPath();

    MetadataPage(MenuController menuController){
        this.menuController = menuController;

        copySVG.setContent(App.svgMap.get(SVG.COPY));
        checkSVG.setContent(App.svgMap.get(SVG.CHECK));

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

        backButton.setOnAction(e -> exitMetadataPage());

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
        menuController.metadataScroll.setContent(content);

        textBox.setAlignment(Pos.TOP_LEFT);
        textBox.setPadding(new Insets(0, 15, 0, 15));
        textBox.setSpacing(10);

    }


    public void enterMetadataPage(MenuObject menuObject){

        String extension = Utilities.getFileExtension(menuObject.getMediaItem().getFile());

        switch (extension) {
            case "mp4":
            case "mov":
                createMp4(menuObject.getMediaItem());
                break;
            case "mp3":
                createMp3(menuObject.getMediaItem());
                break;
            case "avi":
                createAvi(menuObject.getMediaItem());
                break;
            default:
                createOther(menuObject.getMediaItem());
                break;
        }

        imageView.setImage(menuObject.getMediaItem().getCover());

        Color color = menuObject.getMediaItem().getCoverBackgroundColor();

        if(color != null) imageViewContainer.setStyle("-fx-background-color: rgba(" + color.getRed() * 256 +  "," + color.getGreen() * 256 + "," + color.getBlue() * 256 + ",0.7);");

        menuController.metadataScroll.setVisible(true);
        menuController.queueScroll.setVisible(false);

        if(!menuController.menuOpen) menuController.mainController.openMenu();
    }

    public void exitMetadataPage(){
        menuController.metadataScroll.setVisible(false);
        menuController.queueScroll.setVisible(true);

        textBox.getChildren().clear();
        menuController.metadataPage.imageView.setImage(null);
        menuController.metadataPage.imageViewContainer.setStyle("-fx-background-color: transparent;");
    }


    private void createMp4(MediaItem mediaItem){
        if(mediaItem.getMediaInformation() != null){
            Map<String, String> metadata = mediaItem.getMediaInformation();

            if(metadata.containsKey("title") && !metadata.get("title").trim().isEmpty()){
                createTitle(metadata.get("title"));
            }

            if(metadata.containsKey("media_type")){

                String value = metadata.get("media_type");
                if(!value.isEmpty()){
                    if(value.equals("10")){
                        if(metadata.containsKey("show") && !metadata.get("show").trim().isEmpty()){
                            Label label = createItem("Series title", metadata.get("show"));
                            if(metadata.containsKey("season_number") && !metadata.get("season_number").trim().isEmpty() && metadata.containsKey("episode_sort") && !metadata.get("episode_sort").trim().isEmpty()){
                                label.setText(label.getText().concat("(S" + metadata.get("season_number") + "E" + metadata.get("episode_sort") + ")"));
                            }
                        }

                        if(metadata.containsKey("network") && !metadata.get("network").trim().isEmpty()){
                            createItem("Network", metadata.get("network"));
                        }
                    }

                    switch(metadata.get("media_type")){
                        case "6": createItem("Media type", "Music video");
                            break;
                        case "9": createItem("Media type", "Movie");
                            break;
                        case "10": createItem("Media type", "TV Show");
                            break;
                        case "21": createItem("Media type", "Podcast");
                            break;
                        default: createItem("Media type", "Home video");
                            break;
                    }

                }
                else {
                    createItem("Media type", "Home video");
                }
            }
            else {
                createItem("Media type", "Home video");
            }

            if(metadata.containsKey("artist") && !metadata.get("artist").trim().isEmpty()){
                if(metadata.containsKey("media_type") && (metadata.get("media_type").equals("9") || metadata.get("media_type").equals("10"))){
                    createItem("Cast", metadata.get("artist"));
                }
                else {
                    createItem("Artist", metadata.get("artist"));
                }
            }

            if(metadata.containsKey("media_type") && metadata.get("media_type").equals("6") && metadata.containsKey("track") && !metadata.get("track").trim().isEmpty()){
                createItem("Track number", metadata.get("track"));
            }

            if(metadata.containsKey("media_type") && metadata.get("media_type").equals("6") && metadata.containsKey("album") && !metadata.get("album").trim().isEmpty()){
                createItem("Album", metadata.get("album"));
            }

            if(metadata.containsKey("album_artist") && !metadata.get("album_artist").trim().isEmpty()){
                if(metadata.containsKey("media_type") && (metadata.get("media_type").equals("9") || metadata.get("media_type").equals("10"))){
                    createItem("Director", metadata.get("album_artist"));
                }
                else if(metadata.containsKey("media_type") && metadata.get("media_type").equals("6")){
                    createItem("Album artist", metadata.get("album_artist"));
                }
            }

            if(metadata.containsKey("composer") && !metadata.get("composer").trim().isEmpty()){
                if(metadata.containsKey("media_type") && (metadata.get("media_type").equals("9") || metadata.get("media_type").equals("10"))){
                    createItem("Writers", metadata.get("composer"));
                }
                else if(metadata.containsKey("media_type") && metadata.get("media_type").equals("6")){
                    createItem("Composer", metadata.get("composer"));
                }
            }

            if(metadata.containsKey("genre") && !metadata.get("genre").trim().isEmpty()){
                createItem("Genre", metadata.get("genre"));

            }

            if(metadata.containsKey("description") && !metadata.get("description").trim().isEmpty()){
                createItem("Description", metadata.get("description"));

            }

            if(metadata.containsKey("synopsis") && !metadata.get("synopsis").trim().isEmpty()){
                createItem("Synopsis", metadata.get("synopsis"));

            }

            if(metadata.containsKey("media_type") && metadata.get("media_type").equals("6") && metadata.containsKey("lyrics") && !metadata.get("lyrics").trim().isEmpty()){
                createItem("Lyrics", metadata.get("lyrics"));

            }

            if(metadata.containsKey("date") && !metadata.get("date").trim().isEmpty()){
                createItem("Release date", metadata.get("date"));
            }

            if(metadata.containsKey("comment") && !metadata.get("comment").trim().isEmpty()){
                createItem("Comment", metadata.get("comment"));
            }
        }
    }

    private void createMp3(MediaItem mediaItem){
        if(mediaItem.getMediaInformation() != null){
            Map<String, String> metadata = mediaItem.getMediaInformation();

            if(metadata.containsKey("title") && !metadata.get("title").trim().isEmpty()){
                createTitle(metadata.get("title"));
            }

            if(metadata.containsKey("artist") && !metadata.get("artist").trim().isEmpty()){
                createItem("Artist", metadata.get("artist"));
            }
            if(metadata.containsKey("album") && !metadata.get("album").trim().isEmpty()){
                createItem("Album", metadata.get("album"));
            }
            if(metadata.containsKey("track") && !metadata.get("track").trim().isEmpty()){
                Label label = createItem("Track", metadata.get("track"));
                if(metadata.containsKey("disc") && !metadata.get("disc").trim().isEmpty()){
                    label.setText(label.getText().concat(" (Disc "+ metadata.get("disc") + ")"));
                }
            }
            if(metadata.containsKey("album_artist") && !metadata.get("album_artist").trim().isEmpty()){
                createItem("Album artist", metadata.get("album_artist"));
            }
            if(metadata.containsKey("composer") && !metadata.get("composer").trim().isEmpty()){
                createItem("Composer", metadata.get("composer"));
            }
            if(metadata.containsKey("performer") && !metadata.get("performer").trim().isEmpty()){
                createItem("Performer", metadata.get("performer"));
            }
            if(metadata.containsKey("publisher") && !metadata.get("publisher").trim().isEmpty()){
                createItem("Publisher", metadata.get("publisher"));
            }
            if(metadata.containsKey("genre") && !metadata.get("genre").trim().isEmpty()){
                createItem("Genre", metadata.get("genre"));
            }
            if(metadata.containsKey("language") && !metadata.get("language").trim().isEmpty()){
                createItem("Language", metadata.get("language"));
            }
            if(metadata.containsKey("date") && !metadata.get("date").trim().isEmpty()){
                createItem("Release date", metadata.get("date"));
            }
            if(metadata.containsKey("lyrics") && !metadata.get("lyrics").trim().isEmpty()){
                createItem("Lyrics", metadata.get("lyrics"));
            }


        }
    }

    private void createAvi(MediaItem mediaItem){
        // TODO: Figure this out once I get an avi file with metadata

        createOther(mediaItem);
    }

    private void createOther(MediaItem mediaItem){
        if(mediaItem.getMediaInformation() != null){
            Map<String, String> metadata = mediaItem.getMediaInformation();

            if(metadata.containsKey("title") && !metadata.get("title").trim().isEmpty()){
                createTitle(metadata.get("title"));
            }

            if(metadata.containsKey("artist") && !metadata.get("artist").trim().isEmpty()){
                createItem("Artist", metadata.get("artist"));
            }
            if(metadata.containsKey("description") && !metadata.get("description").trim().isEmpty()){
                createItem("Description", metadata.get("description"));
            }

            for(Map.Entry<String, String> entry : metadata.entrySet()){
                if(!entry.getKey().equals("title") && !entry.getKey().equals("artist") && !entry.getKey().equals("description") && !entry.getValue().trim().isEmpty()){
                    String filtered = entry.getKey().replaceAll("[_]", " ");
                    createItem(filtered.substring(0, 1).toUpperCase() + filtered.substring(1), entry.getValue());
                }
            }
        }
    }


    private Label createItem(String key, String value){

        Label keyLabel = new Label(key);
        keyLabel.getStyleClass().add("metadataKey");

        StackPane copyButtonPane = new StackPane();
        copyButtonPane.setPrefSize(30, 30);
        copyButtonPane.setMaxSize(30, 30);
        HBox.setMargin(copyButtonPane, new Insets(0, 0, 0, 20));

        JFXButton copyButton = new JFXButton();
        copyButton.setPrefWidth(30);
        copyButton.setPrefHeight(30);
        copyButton.setRipplerFill(Color.WHITE);
        copyButton.getStyleClass().add("copyButton");
        copyButton.setCursor(Cursor.HAND);
        copyButton.setOpacity(0);
        copyButton.setText(null);

        copyButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> AnimationsClass.fadeAnimation(200, copyButton, 0, 1, false, 1, true));
        copyButton.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> AnimationsClass.fadeAnimation(200, copyButton, 1, 0, false, 1, true));

        Region copyIcon = new Region();
        copyIcon.setShape(copySVG);
        copyIcon.setMinSize(15, 15);
        copyIcon.setPrefSize(15, 15);
        copyIcon.setMaxSize(15, 15);
        copyIcon.setMouseTransparent(true);
        copyIcon.getStyleClass().add("copyIcon");


        copyButtonPane.getChildren().addAll(copyButton, copyIcon);


        HBox keyHbox = new HBox();
        keyHbox.setAlignment(Pos.CENTER_LEFT);
        keyHbox.getChildren().addAll(keyLabel, copyButtonPane);

        keyLabel.prefWidthProperty().bind(keyHbox.widthProperty().subtract(50));


        Label label = new Label(value);
        label.getStyleClass().add("metadataValue");
        label.setMaxWidth(Double.MAX_VALUE);
        label.setWrapText(true);
        label.setLineSpacing(5);
        label.setTextAlignment(TextAlignment.JUSTIFY);
        label.setMaxHeight(80);
        label.setAlignment(Pos.TOP_LEFT);

        AtomicReference<PauseTransition> pauseTransition = new AtomicReference<>();
        copyButton.setOnAction(e -> {
            pauseTransition.set(copyText(label, copyIcon, pauseTransition.get()));
        });

        Button button = new Button("Show more");
        button.setUnderline(true);
        button.setCursor(Cursor.HAND);
        button.setBackground(Background.EMPTY);
        button.setTranslateY(-15);
        button.getStyleClass().add("expandButton");
        button.setVisible(false);
        button.setMouseTransparent(true);


        button.setOnAction(e -> {
            if(label.getMaxHeight() == 80){
                label.setMaxHeight(Double.MAX_VALUE);
                button.setText("Show less");
            }
            else {
                label.setMaxHeight(80);
                button.setText("Show more");
            }

        });

        HBox hBox = new HBox(button);
        hBox.setAlignment(Pos.CENTER_RIGHT);

        textBox.getChildren().addAll(keyHbox, label, hBox);

        label.widthProperty().addListener((observable, oldValue, newValue) -> {
            updateLabel(label, button);
        });

        return label;
    }

    private void createTitle(String title){
        Label label = new Label(title);
        label.getStyleClass().add("metadataTitle");
        label.setMaxWidth(Double.MAX_VALUE);
        label.setWrapText(true);
        label.setLineSpacing(5);
        label.setPadding(new Insets(0, 0, 15, 0));


        textBox.getChildren().add(label);
    }

    private void updateLabel(Label label, Button button){

        if(label.getHeight() == 0){

            PauseTransition pauseTransition = new PauseTransition(Duration.millis(10));
            pauseTransition.setOnFinished(e -> updateLabel(label, button));
            pauseTransition.playFromStart();
        }
        else {
            String originalString = label.getText();
            Text textNode = (Text) label.lookup(".text"); // "text" is the style class of Text
            String actualString = textNode.getText();

            boolean clipped = !actualString.isEmpty() && !originalString.equals(actualString);

            if(!clipped && label.getHeight() <= 80){
                button.setVisible(false);
                button.setMouseTransparent(true);
            }
            else {
                button.setVisible(true);
                button.setMouseTransparent(false);
            }
        }
    }


    private PauseTransition copyText(Label label, Region region, PauseTransition pauseTransition){
        region.setShape(checkSVG);

        final ClipboardContent content = new ClipboardContent();
        content.putString(label.getText());
        Clipboard.getSystemClipboard().setContent(content);

        if(pauseTransition != null && pauseTransition.getStatus() == Animation.Status.RUNNING) {
            pauseTransition.stop();
        }

        pauseTransition = new PauseTransition(Duration.millis(3000));
        pauseTransition.setOnFinished(e -> {
            region.setShape(copySVG);
        });

        pauseTransition.playFromStart();

        return pauseTransition;
    }

}