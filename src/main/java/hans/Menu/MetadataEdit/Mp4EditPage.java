package hans.Menu.MetadataEdit;

import hans.MediaItems.MediaItem;
import javafx.scene.layout.VBox;

import java.util.Arrays;
import java.util.Map;

public class Mp4EditPage {

    MetadataEditPage metadataEditPage;
    MediaItem mediaItem;

    TextAreaItem titleItem = null;
    ComboBoxItem comboboxItem = null;
    TextAreaItem seriesTitleItem = null;
    TextFieldItem seasonNumberItem = null;
    TextFieldItem episodeNumberItem = null;
    TextAreaItem networkItem = null;
    TextAreaItem artistItem = null;
    TwoTextFieldItem trackItem = null;
    TextAreaItem albumItem = null;
    TextAreaItem albumArtistItem = null;
    TextAreaItem composerItem = null;
    TextAreaItem genreItem = null;
    TextAreaItem descriptionItem = null;
    TextAreaItem synopsisItem = null;
    TextAreaItem lyricsItem = null;
    TextAreaItem releaseDateItem = null;
    TextAreaItem commentItem = null;

    VBox content = new VBox();

    String mediaType;
    Map<String, String> metadata;


    Mp4EditPage(MetadataEditPage metadataEditPage, MediaItem mediaItem){
        this.metadataEditPage = metadataEditPage;
        this.mediaItem = mediaItem;

        content.setSpacing(15);

        metadata = mediaItem.getMediaInformation();
        if(metadata != null && metadata.containsKey("media_type")){
            String type = metadata.get("media_type");

            switch (type) {
                case "6":
                    mediaType = "Music video";
                    break;
                case "9":
                    mediaType = "Movie";
                    break;
                case "10":
                    mediaType = "TV Show";
                    break;
                case "21":
                    mediaType = "Podcast";
                    break;
                default:
                    mediaType = "Home video";
                    break;
            }
        }
        else {
            mediaType = "Home video";
        }

        if(metadata != null) {
            titleItem = new TextAreaItem(metadataEditPage, "Title", metadata.containsKey("title") && !metadata.get("title").trim().isEmpty() ? metadata.get("title") : "", content, true);
            comboboxItem = new ComboBoxItem(metadataEditPage, content, true, mediaType, "Music video", "Movie", "TV Show", "Podcast", "Home video");
            comboboxItem.comboBox.valueProperty().addListener((observableValue, oldValue, newValue) -> {
                if (oldValue != null && !oldValue.equals(newValue)) updateMediaType(newValue);
            });

            seriesTitleItem = new TextAreaItem(metadataEditPage, "Series title", metadata.containsKey("show") && !metadata.get("show").trim().isEmpty() ? metadata.get("show") : "", content, false);
            seasonNumberItem = new TextFieldItem(metadataEditPage, "Season number", metadata.containsKey("season_number") && !metadata.get("season_number").trim().isEmpty() ? metadata.get("season_number") : "", content, false);
            episodeNumberItem = new TextFieldItem(metadataEditPage, "Episode number", metadata.containsKey("episode_sort") && !metadata.get("episode_sort").trim().isEmpty() ? metadata.get("episode_sort") : "", content, false);
            networkItem = new TextAreaItem(metadataEditPage, "Network", metadata.containsKey("network") && !metadata.get("network").trim().isEmpty() ? metadata.get("network") : "", content, false);

            if (mediaType.equals("TV Show")) {
                // TV Show fields
                content.getChildren().addAll(seriesTitleItem, seasonNumberItem, episodeNumberItem, networkItem);
            }


            if (mediaType.equals("TV Show") || mediaType.equals("Movie")) {
                artistItem = new TextAreaItem(metadataEditPage, "Cast", metadata.containsKey("artist") && !metadata.get("artist").trim().isEmpty() ? metadata.get("artist") : "", content, true);
            } else {
                artistItem = new TextAreaItem(metadataEditPage, "Artist", metadata.containsKey("artist") && !metadata.get("artist").trim().isEmpty() ? metadata.get("artist") : "", content, true);
            }

            trackItem = new TwoTextFieldItem(metadataEditPage, "Track number", metadata.containsKey("track") && !metadata.get("track").trim().isEmpty() ? metadata.get("track") : "", content, false);
            albumItem = new TextAreaItem(metadataEditPage, "Album", metadata.containsKey("album") && !metadata.get("album").trim().isEmpty() ? metadata.get("album") : "", content, false);

            if (mediaType.equals("Music video")) {
                content.getChildren().addAll(trackItem, albumItem);
            }

            albumArtistItem = new TextAreaItem(metadataEditPage, "Director", metadata.containsKey("album_artist") && !metadata.get("album_artist").trim().isEmpty() ? metadata.get("album_artist") : "", content, false);

            if (mediaType.equals("TV Show") || mediaType.equals("Movie")) {
                content.getChildren().add(albumArtistItem);
            } else if (mediaType.equals("Music video")) {
                albumArtistItem.label.setText("Album artist");
                content.getChildren().add(albumArtistItem);
            }



            composerItem = new TextAreaItem(metadataEditPage, "Writers", metadata.containsKey("composer") && !metadata.get("composer").trim().isEmpty() ? metadata.get("composer") : "", content, false);


            if (mediaType.equals("TV Show") || mediaType.equals("Movie")) {
                content.getChildren().add(composerItem);
            } else if (mediaType.equals("Music video")) {
                composerItem.label.setText("Composer");
                content.getChildren().add(composerItem);
            }

            genreItem = new TextAreaItem(metadataEditPage, "Genre", metadata.containsKey("genre") && !metadata.get("genre").trim().isEmpty() ? metadata.get("genre") : "", content, true);

            descriptionItem = new TextAreaItem(metadataEditPage, "Description", metadata.containsKey("description") && !metadata.get("description").trim().isEmpty() ? metadata.get("description") : "", content, true);

            synopsisItem = new TextAreaItem(metadataEditPage, "Synopsis", metadata.containsKey("synopsis") && !metadata.get("synopsis").trim().isEmpty() ? metadata.get("synopsis") : "", content, true);

            lyricsItem = new TextAreaItem(metadataEditPage, "Lyrics", metadata.containsKey("lyrics") && !metadata.get("lyrics").trim().isEmpty() ? metadata.get("lyrics") : "", content, false);
            if (mediaType.equals("Music video")) {
                content.getChildren().add(lyricsItem);
            }

            releaseDateItem = new TextAreaItem(metadataEditPage, "Release date", metadata.containsKey("date") && !metadata.get("date").trim().isEmpty() ? metadata.get("date") : "", content, true);
            commentItem = new TextAreaItem(metadataEditPage, "Comment", metadata.containsKey("comment") && !metadata.get("comment").trim().isEmpty() ? metadata.get("comment") : "", content, true);
        }

        metadataEditPage.textBox.getChildren().add(content);
    }

    public void updateMediaType(String value){
        mediaType = value;
        if(value.equals("TV Show")){
            content.getChildren().removeAll(trackItem, albumItem, lyricsItem);
            content.getChildren().addAll(2, Arrays.asList(seriesTitleItem, seasonNumberItem, episodeNumberItem, networkItem));
            artistItem.label.setText("Cast");
            albumArtistItem.label.setText("Director");
            if(!content.getChildren().contains(albumArtistItem)) content.getChildren().add(7, albumArtistItem);
            composerItem.label.setText("Writers");
            if(!content.getChildren().contains(composerItem)) content.getChildren().add(8, composerItem);
        }
        else if(value.equals("Movie")){
            content.getChildren().removeAll(trackItem, albumItem, lyricsItem, seriesTitleItem, seasonNumberItem, episodeNumberItem, networkItem);
            artistItem.label.setText("Cast");
            albumArtistItem.label.setText("Director");
            if(!content.getChildren().contains(albumArtistItem)) content.getChildren().add(3, albumArtistItem);
            composerItem.label.setText("Writers");
            if(!content.getChildren().contains(composerItem)) content.getChildren().add(4, composerItem);
        }
        else if(value.equals("Music video")){
            content.getChildren().removeAll(seriesTitleItem, seasonNumberItem, episodeNumberItem, networkItem);
            artistItem.label.setText("Artist");
            if(!content.getChildren().contains(trackItem)) content.getChildren().add(3, trackItem);
            if(!content.getChildren().contains(albumItem)) content.getChildren().add(4, albumItem);
            albumArtistItem.label.setText("Album artist");
            if(!content.getChildren().contains(albumArtistItem)) content.getChildren().add(5, albumArtistItem);
            composerItem.label.setText("Composer");
            if(!content.getChildren().contains(composerItem)) content.getChildren().add(6, composerItem);
            if(!content.getChildren().contains(lyricsItem)) content.getChildren().add(10, lyricsItem);
        }
        else if(value.equals("Podcast") || value.equals("Home video")){
            content.getChildren().removeAll(trackItem, albumItem, lyricsItem, seriesTitleItem, seasonNumberItem, episodeNumberItem, networkItem, albumArtistItem, composerItem);
            artistItem.label.setText("Artist");
        }
    }

    public void saveMetadata(){

    }
}

