package hans.Menu;

import hans.AnimationsClass;
import hans.App;
import hans.MediaItems.MediaItem;
import hans.SVG;
import hans.Utilities;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

import java.util.Map;

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



    MetadataPage(MenuController menuController){
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

            if(metadata.containsKey("title")){
                String value = metadata.get("title");
                if(!value.trim().isEmpty()){
                    TextFlow textFlow = new TextFlow();
                    textFlow.setMaxWidth(Double.MAX_VALUE);
                    textFlow.setPrefWidth(Double.MAX_VALUE);
                    textFlow.setTextAlignment(TextAlignment.LEFT);
                    textFlow.setPadding(new Insets(0, 0, 15, 0));

                    Text text = new Text(metadata.get("title"));
                    text.getStyleClass().add("metadataTitle");

                    textFlow.getChildren().add(text);
                    textBox.getChildren().add(textFlow);
                }
            }

            if(metadata.containsKey("media_type")){


                String value = metadata.get("media_type");
                if(!value.isEmpty()){

                    if(value.equals("10")){
                        if(metadata.containsKey("show") && !metadata.get("show").trim().isEmpty()){
                            Label key = new Label("TV Show");
                            key.getStyleClass().add("metadataKey");
                            textBox.getChildren().add(key);

                            TextFlow textFlow = new TextFlow();
                            textFlow.setMaxWidth(Double.MAX_VALUE);
                            textFlow.setPrefWidth(Double.MAX_VALUE);
                            textFlow.setTextAlignment(TextAlignment.JUSTIFY);

                            Text text = new Text(metadata.get("show").concat(" "));
                            text.getStyleClass().add("metadataValue");
                            textFlow.getChildren().add(text);

                            if(metadata.containsKey("season_number") && !metadata.get("season_number").trim().isEmpty() && metadata.containsKey("episode_sort") && !metadata.get("episode_sort").trim().isEmpty()){
                                Text seasonText = new Text("(S" + metadata.get("season_number") + "E" + metadata.get("episode_sort") + ")");
                                seasonText.getStyleClass().add("metadataEpisode");
                                textFlow.getChildren().add(seasonText);
                            }

                            textBox.getChildren().add(textFlow);
                        }

                        if(metadata.containsKey("network") && !metadata.get("network").trim().isEmpty()){
                            Label key = new Label("Network");
                            key.getStyleClass().add("metadataKey");
                            textBox.getChildren().add(key);

                            TextFlow textFlow = new TextFlow();
                            textFlow.setMaxWidth(Double.MAX_VALUE);
                            textFlow.setPrefWidth(Double.MAX_VALUE);
                            textFlow.setTextAlignment(TextAlignment.JUSTIFY);

                            Text text = new Text(metadata.get("network"));
                            text.getStyleClass().add("metadataValue");
                            textFlow.getChildren().add(text);

                            textBox.getChildren().add(textFlow);
                        }
                    }

                    Label key = new Label("Media type");
                    key.getStyleClass().add("metadataKey");
                    textBox.getChildren().add(key);

                    TextFlow textFlow = new TextFlow();
                    textFlow.setMaxWidth(Double.MAX_VALUE);
                    textFlow.setPrefWidth(Double.MAX_VALUE);
                    textFlow.setTextAlignment(TextAlignment.JUSTIFY);

                    String formattedValue;

                    switch(metadata.get("media_type")){
                        case "6": formattedValue = "Music video";
                            break;
                        case "9": formattedValue = "Movie";
                            break;
                        case "10": formattedValue = "TV Show";
                            break;
                        case "21": formattedValue = "Podcast";
                            break;
                        default: formattedValue = "Home video";
                            break;
                    }

                    Text text = new Text(formattedValue);
                    text.getStyleClass().add("metadataValue");

                    textFlow.getChildren().add(text);
                    textBox.getChildren().add(textFlow);
                }
                else {
                    Label key = new Label("Media type");
                    key.getStyleClass().add("metadataKey");
                    textBox.getChildren().add(key);

                    TextFlow textFlow = new TextFlow();
                    textFlow.setMaxWidth(Double.MAX_VALUE);
                    textFlow.setPrefWidth(Double.MAX_VALUE);
                    textFlow.setTextAlignment(TextAlignment.JUSTIFY);

                    Text text = new Text("Home video");
                    text.getStyleClass().add("metadataValue");

                    textFlow.getChildren().add(text);
                    textBox.getChildren().add(textFlow);
                }
            }
            else {
                Label key = new Label("Media type");
                key.getStyleClass().add("metadataKey");
                textBox.getChildren().add(key);

                TextFlow textFlow = new TextFlow();
                textFlow.setMaxWidth(Double.MAX_VALUE);
                textFlow.setPrefWidth(Double.MAX_VALUE);
                textFlow.setTextAlignment(TextAlignment.JUSTIFY);

                Text text = new Text("Home video");
                text.getStyleClass().add("metadataValue");

                textFlow.getChildren().add(text);
                textBox.getChildren().add(textFlow);
            }

            if(metadata.containsKey("artist") && !metadata.get("artist").trim().isEmpty()){
                if(metadata.containsKey("media_type") && (metadata.get("media_type").equals("9") || metadata.get("media_type").equals("10"))){
                    Label key = new Label("Cast");
                    key.getStyleClass().add("metadataKey");
                    textBox.getChildren().add(key);
                }
                else {
                    Label key = new Label("Artist");
                    key.getStyleClass().add("metadataKey");
                    textBox.getChildren().add(key);
                }

                TextFlow textFlow = new TextFlow();
                textFlow.setMaxWidth(Double.MAX_VALUE);
                textFlow.setPrefWidth(Double.MAX_VALUE);
                textFlow.setTextAlignment(TextAlignment.JUSTIFY);

                Text text = new Text(metadata.get("artist"));
                text.getStyleClass().add("metadataValue");

                textFlow.getChildren().add(text);
                textBox.getChildren().add(textFlow);
            }

            if(metadata.containsKey("media_type") && metadata.get("media_type").equals("6") && metadata.containsKey("track") && !metadata.get("track").trim().isEmpty()){
                Label key = new Label("Track number");
                key.getStyleClass().add("metadataKey");
                textBox.getChildren().add(key);

                TextFlow textFlow = new TextFlow();
                textFlow.setMaxWidth(Double.MAX_VALUE);
                textFlow.setPrefWidth(Double.MAX_VALUE);
                textFlow.setTextAlignment(TextAlignment.JUSTIFY);

                Text text = new Text(metadata.get("track"));
                text.getStyleClass().add("metadataValue");

                textFlow.getChildren().add(text);
                textBox.getChildren().add(textFlow);
            }

            if(metadata.containsKey("media_type") && metadata.get("media_type").equals("6") && metadata.containsKey("album") && !metadata.get("album").trim().isEmpty()){
                Label key = new Label("Album");
                key.getStyleClass().add("metadataKey");
                textBox.getChildren().add(key);

                TextFlow textFlow = new TextFlow();
                textFlow.setMaxWidth(Double.MAX_VALUE);
                textFlow.setPrefWidth(Double.MAX_VALUE);
                textFlow.setTextAlignment(TextAlignment.JUSTIFY);

                Text text = new Text(metadata.get("album"));
                text.getStyleClass().add("metadataValue");

                textFlow.getChildren().add(text);
                textBox.getChildren().add(textFlow);
            }

            if(metadata.containsKey("album_artist") && !metadata.get("album_artist").trim().isEmpty()){
                if(metadata.containsKey("media_type") && (metadata.get("media_type").equals("9") || metadata.get("media_type").equals("10"))){
                    Label key = new Label("Director");
                    key.getStyleClass().add("metadataKey");
                    textBox.getChildren().add(key);
                }
                else if(metadata.containsKey("media_type") && metadata.get("media_type").equals("6")){
                    Label key = new Label("Album Artist");
                    key.getStyleClass().add("metadataKey");
                    textBox.getChildren().add(key);
                }

                if(metadata.containsKey("media_type") && (metadata.get("media_type").equals("9") || metadata.get("media_type").equals("10") || metadata.get("media_type").equals("6"))) {
                    TextFlow textFlow = new TextFlow();
                    textFlow.setMaxWidth(Double.MAX_VALUE);
                    textFlow.setPrefWidth(Double.MAX_VALUE);
                    textFlow.setTextAlignment(TextAlignment.JUSTIFY);

                    Text text = new Text(metadata.get("album_artist"));
                    text.getStyleClass().add("metadataValue");

                    textFlow.getChildren().add(text);
                    textBox.getChildren().add(textFlow);
                }
            }

            if(metadata.containsKey("composer") && !metadata.get("composer").trim().isEmpty()){
                if(metadata.containsKey("media_type") && (metadata.get("media_type").equals("9") || metadata.get("media_type").equals("10"))){
                    Label key = new Label("Writers");
                    key.getStyleClass().add("metadataKey");
                    textBox.getChildren().add(key);
                }
                else if(metadata.containsKey("media_type") && metadata.get("media_type").equals("6")){
                    Label key = new Label("Composer");
                    key.getStyleClass().add("metadataKey");
                    textBox.getChildren().add(key);
                }

                if(metadata.containsKey("media_type") && (metadata.get("media_type").equals("9") || metadata.get("media_type").equals("10") || metadata.get("media_type").equals("6"))) {
                    TextFlow textFlow = new TextFlow();
                    textFlow.setMaxWidth(Double.MAX_VALUE);
                    textFlow.setPrefWidth(Double.MAX_VALUE);
                    textFlow.setTextAlignment(TextAlignment.JUSTIFY);

                    Text text = new Text(metadata.get("composer"));
                    text.getStyleClass().add("metadataValue");

                    textFlow.getChildren().add(text);
                    textBox.getChildren().add(textFlow);
                }
            }

            if(metadata.containsKey("genre") && !metadata.get("genre").trim().isEmpty()){
                Label key = new Label("Genre");
                key.getStyleClass().add("metadataKey");
                textBox.getChildren().add(key);

                TextFlow textFlow = new TextFlow();
                textFlow.setMaxWidth(Double.MAX_VALUE);
                textFlow.setPrefWidth(Double.MAX_VALUE);
                textFlow.setTextAlignment(TextAlignment.JUSTIFY);

                Text text = new Text(metadata.get("genre"));
                text.getStyleClass().add("metadataValue");

                textFlow.getChildren().add(text);
                textBox.getChildren().add(textFlow);
            }

            if(metadata.containsKey("description") && !metadata.get("description").trim().isEmpty()){
                Label key = new Label("Description");
                key.getStyleClass().add("metadataKey");
                textBox.getChildren().add(key);

                TextFlow textFlow = new TextFlow();
                textFlow.setMaxWidth(Double.MAX_VALUE);
                textFlow.setPrefWidth(Double.MAX_VALUE);
                textFlow.setTextAlignment(TextAlignment.JUSTIFY);

                Text text = new Text(metadata.get("description"));
                text.getStyleClass().add("metadataValue");

                textFlow.getChildren().add(text);
                textBox.getChildren().add(textFlow);
            }

            if(metadata.containsKey("synopsis") && !metadata.get("synopsis").trim().isEmpty()){
                Label key = new Label("Synopsis");
                key.getStyleClass().add("metadataKey");
                textBox.getChildren().add(key);

                TextFlow textFlow = new TextFlow();
                textFlow.setMaxWidth(Double.MAX_VALUE);
                textFlow.setPrefWidth(Double.MAX_VALUE);
                textFlow.setTextAlignment(TextAlignment.JUSTIFY);

                Text text = new Text(metadata.get("synopsis"));
                text.getStyleClass().add("metadataValue");

                textFlow.getChildren().add(text);
                textBox.getChildren().add(textFlow);
            }

            if(metadata.containsKey("media_type") && metadata.get("media_type").equals("6") && metadata.containsKey("lyrics") && !metadata.get("lyrics").trim().isEmpty()){
                Label key = new Label("Lyrics");
                key.getStyleClass().add("metadataKey");
                textBox.getChildren().add(key);

                TextFlow textFlow = new TextFlow();
                textFlow.setMaxWidth(Double.MAX_VALUE);
                textFlow.setPrefWidth(Double.MAX_VALUE);
                textFlow.setTextAlignment(TextAlignment.JUSTIFY);

                Text text = new Text(metadata.get("lyrics"));
                text.getStyleClass().add("metadataValue");

                textFlow.getChildren().add(text);
                textBox.getChildren().add(textFlow);
            }

            if(metadata.containsKey("date") && !metadata.get("date").trim().isEmpty()){
                Label key = new Label("Release date");
                key.getStyleClass().add("metadataKey");
                textBox.getChildren().add(key);

                TextFlow textFlow = new TextFlow();
                textFlow.setMaxWidth(Double.MAX_VALUE);
                textFlow.setPrefWidth(Double.MAX_VALUE);
                textFlow.setTextAlignment(TextAlignment.LEFT);

                Text text = new Text(metadata.get("date"));
                text.getStyleClass().add("metadataValue");

                textFlow.getChildren().add(text);
                textBox.getChildren().add(textFlow);
            }

            if(metadata.containsKey("comment") && !metadata.get("comment").trim().isEmpty()){
                Label key = new Label("Comment");
                key.getStyleClass().add("metadataKey");
                textBox.getChildren().add(key);

                TextFlow textFlow = new TextFlow();
                textFlow.setMaxWidth(Double.MAX_VALUE);
                textFlow.setPrefWidth(Double.MAX_VALUE);
                textFlow.setTextAlignment(TextAlignment.JUSTIFY);

                Text text = new Text(metadata.get("comment"));
                text.getStyleClass().add("metadataValue");

                textFlow.getChildren().add(text);
                textBox.getChildren().add(textFlow);
            }
        }
    }

    private void createMp3(MediaItem mediaItem){
        if(mediaItem.getMediaInformation() != null){
            Map<String, String> metadata = mediaItem.getMediaInformation();

            if(metadata.containsKey("title")){
                String value = metadata.get("title");
                if(!value.trim().isEmpty()){
                    TextFlow textFlow = new TextFlow();
                    textFlow.setMaxWidth(Double.MAX_VALUE);
                    textFlow.setPrefWidth(Double.MAX_VALUE);
                    textFlow.setTextAlignment(TextAlignment.LEFT);
                    textFlow.setPadding(new Insets(0, 0, 15, 0));

                    Text text = new Text(metadata.get("title"));
                    text.getStyleClass().add("metadataTitle");

                    textFlow.getChildren().add(text);
                    textBox.getChildren().add(textFlow);
                }
            }

            if(metadata.containsKey("artist") && !metadata.get("artist").trim().isEmpty()){
                createItem(metadata, "artist", "Artist");
            }
            if(metadata.containsKey("album") && !metadata.get("album").trim().isEmpty()){
                createItem(metadata, "album", "Album");
            }
            if(metadata.containsKey("track") && !metadata.get("track").trim().isEmpty()){
                Text text = createItem(metadata, "track", "Track");
                if(metadata.containsKey("disc") && !metadata.get("disc").trim().isEmpty()){
                    text.setText(text.getText().concat(" (Disc "+ metadata.get("disc") + ")"));
                }
            }
            if(metadata.containsKey("album_artist") && !metadata.get("album_artist").trim().isEmpty()){
                createItem(metadata, "album_artist", "Album artist");
            }
            if(metadata.containsKey("composer") && !metadata.get("composer").trim().isEmpty()){
                createItem(metadata, "composer", "Composer");
            }
            if(metadata.containsKey("performer") && !metadata.get("performer").trim().isEmpty()){
                createItem(metadata, "performer", "Performer");
            }
            if(metadata.containsKey("publisher") && !metadata.get("publisher").trim().isEmpty()){
                createItem(metadata, "publisher", "Publisher");
            }
            if(metadata.containsKey("genre") && !metadata.get("genre").trim().isEmpty()){
                createItem(metadata, "genre", "Genre");
            }
            if(metadata.containsKey("language") && !metadata.get("language").trim().isEmpty()){
                createItem(metadata, "language", "Language");
            }
            if(metadata.containsKey("date") && !metadata.get("date").trim().isEmpty()){
                createItem(metadata, "date", "Release Date");
            }
            if(metadata.containsKey("lyrics") && !metadata.get("lyrics").trim().isEmpty()){
                createItem(metadata, "lyrics", "Lyrics");
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

            if(metadata.containsKey("title")){
                String value = metadata.get("title");
                if(!value.trim().isEmpty()){
                    TextFlow textFlow = new TextFlow();
                    textFlow.setMaxWidth(Double.MAX_VALUE);
                    textFlow.setPrefWidth(Double.MAX_VALUE);
                    textFlow.setTextAlignment(TextAlignment.LEFT);
                    textFlow.setPadding(new Insets(0, 0, 15, 0));

                    Text text = new Text(metadata.get("title"));
                    text.getStyleClass().add("metadataTitle");

                    textFlow.getChildren().add(text);
                    textBox.getChildren().add(textFlow);
                }
            }

            if(metadata.containsKey("artist") && !metadata.get("artist").trim().isEmpty()){
                createItem(metadata, "artist", "Artist");
            }
            if(metadata.containsKey("description") && !metadata.get("description").trim().isEmpty()){
                createItem(metadata, "description", "Description");
            }

            for(Map.Entry<String, String> entry : metadata.entrySet()){
                if(!entry.getKey().equals("title") && !entry.getKey().equals("artist") && !entry.getKey().equals("description") && !entry.getValue().trim().isEmpty()){
                    Label keyLabel = new Label((entry.getKey().substring(0, 1).toUpperCase() + entry.getKey().substring(1)).replaceAll("[_]", " "));
                    keyLabel.getStyleClass().add("metadataKey");
                    textBox.getChildren().add(keyLabel);

                    TextFlow textFlow = new TextFlow();
                    textFlow.setMaxWidth(Double.MAX_VALUE);
                    textFlow.setPrefWidth(Double.MAX_VALUE);
                    textFlow.setTextAlignment(TextAlignment.JUSTIFY);

                    Text text = new Text(entry.getValue());
                    text.getStyleClass().add("metadataValue");

                    textFlow.getChildren().add(text);
                    textBox.getChildren().add(textFlow);
                }
            }
        }
    }


    private Text createItem(Map<String, String> metadata, String key, String keyDisplayText){
        Label keyLabel = new Label(keyDisplayText);
        keyLabel.getStyleClass().add("metadataKey");
        textBox.getChildren().add(keyLabel);

        TextFlow textFlow = new TextFlow();
        textFlow.setMaxWidth(Double.MAX_VALUE);
        textFlow.setPrefWidth(Double.MAX_VALUE);
        textFlow.setTextAlignment(TextAlignment.JUSTIFY);

        Text text = new Text(metadata.get(key));
        text.getStyleClass().add("metadataValue");

        textFlow.getChildren().add(text);
        textBox.getChildren().add(textFlow);

        return text;
    }



}