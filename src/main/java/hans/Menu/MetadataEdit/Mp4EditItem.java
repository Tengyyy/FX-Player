package hans.Menu.MetadataEdit;

import hans.MediaItems.MediaItem;
import hans.Utilities;
import javafx.scene.layout.VBox;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Mp4EditItem implements MetadataEditItem{

    MetadataEditPage metadataEditPage;
    MediaItem mediaItem;

    TextAreaItem titleItem = null;
    ComboBoxItem comboboxItem = null;
    TextAreaItem seriesTitleItem = null;
    SpinnerItem seasonNumberItem = null;
    SpinnerItem episodeNumberItem = null;
    TextAreaItem networkItem = null;
    TextAreaItem artistItem = null;
    TwoSpinnerItem trackItem = null;
    TextAreaItem albumItem = null;
    TextAreaItem albumArtistItem = null;
    TextAreaItem composerItem = null;
    TextAreaItem genreItem = null;
    TextAreaItem descriptionItem = null;
    TextAreaItem synopsisItem = null;
    TextAreaItem lyricsItem = null;
    DatePickerItem releaseDateItem = null;
    TextAreaItem commentItem = null;

    VBox content = new VBox();

    String mediaType;
    Map<String, String> metadata;


    Mp4EditItem(MetadataEditPage metadataEditPage, MediaItem mediaItem){
        this.metadataEditPage = metadataEditPage;
        this.mediaItem = mediaItem;

        content.setSpacing(15);

        metadata = mediaItem.getMediaInformation();
        if(metadata != null && metadata.containsKey("media_type")){
            String type = metadata.get("media_type");

            switch (type) {
                case "6" -> mediaType = "Music video";
                case "9" -> mediaType = "Movie";
                case "10" -> mediaType = "TV Show";
                case "21" -> mediaType = "Podcast";
                default -> mediaType = "Home video";
            }
        }
        else {
            mediaType = "Home video";
        }

        if(metadata != null) {
            titleItem = new TextAreaItem(metadataEditPage, "Title", metadata.containsKey("title") && !metadata.get("title").isBlank() ? metadata.get("title") : "", content, true);
            comboboxItem = new ComboBoxItem(metadataEditPage, content, true, mediaType, "Music video", "Movie", "TV Show", "Podcast", "Home video");
            comboboxItem.comboBox.valueProperty().addListener((observableValue, oldValue, newValue) -> {
                if (oldValue != null && !oldValue.equals(newValue)) updateMediaType(newValue);
            });

            seriesTitleItem = new TextAreaItem(metadataEditPage, "Series title", metadata.containsKey("show") && !metadata.get("show").isBlank() ? metadata.get("show") : "", content, false);
            seasonNumberItem = new SpinnerItem(metadataEditPage, "Season number", metadata.containsKey("season_number") && !metadata.get("season_number").isBlank() ? metadata.get("season_number") : "", content, false);
            episodeNumberItem = new SpinnerItem(metadataEditPage, "Episode number", metadata.containsKey("episode_sort") && !metadata.get("episode_sort").isBlank() ? metadata.get("episode_sort") : "", content, false);
            networkItem = new TextAreaItem(metadataEditPage, "Network", metadata.containsKey("network") && !metadata.get("network").isBlank() ? metadata.get("network") : "", content, false);

            if (mediaType.equals("TV Show")) {
                // TV Show fields
                content.getChildren().addAll(seriesTitleItem, seasonNumberItem, episodeNumberItem, networkItem);
            }


            if (mediaType.equals("TV Show") || mediaType.equals("Movie")) {
                artistItem = new TextAreaItem(metadataEditPage, "Cast", metadata.containsKey("artist") && !metadata.get("artist").isBlank() ? metadata.get("artist") : "", content, true);
            } else {
                artistItem = new TextAreaItem(metadataEditPage, "Artist", metadata.containsKey("artist") && !metadata.get("artist").isBlank() ? metadata.get("artist") : "", content, true);
            }

            String[] trackString = Utilities.splitString(metadata.getOrDefault("track", ""));
            trackItem = new TwoSpinnerItem(metadataEditPage, "Track number", trackString[0], trackString[1], content, false);

            albumItem = new TextAreaItem(metadataEditPage, "Album", metadata.containsKey("album") && !metadata.get("album").isBlank() ? metadata.get("album") : "", content, false);

            if (mediaType.equals("Music video")) {
                content.getChildren().addAll(trackItem, albumItem);
            }

            albumArtistItem = new TextAreaItem(metadataEditPage, "Director", metadata.containsKey("album_artist") && !metadata.get("album_artist").isBlank() ? metadata.get("album_artist") : "", content, false);

            if (mediaType.equals("TV Show") || mediaType.equals("Movie")) {
                content.getChildren().add(albumArtistItem);
            } else if (mediaType.equals("Music video")) {
                albumArtistItem.label.setText("Album artist");
                content.getChildren().add(albumArtistItem);
            }



            composerItem = new TextAreaItem(metadataEditPage, "Writers", metadata.containsKey("composer") && !metadata.get("composer").isBlank() ? metadata.get("composer") : "", content, false);


            if (mediaType.equals("TV Show") || mediaType.equals("Movie")) {
                content.getChildren().add(composerItem);
            } else if (mediaType.equals("Music video")) {
                composerItem.label.setText("Composer");
                content.getChildren().add(composerItem);
            }

            genreItem = new TextAreaItem(metadataEditPage, "Genre", metadata.containsKey("genre") && !metadata.get("genre").isBlank() ? metadata.get("genre") : "", content, true);

            descriptionItem = new TextAreaItem(metadataEditPage, "Description", metadata.containsKey("description") && !metadata.get("description").isBlank() ? metadata.get("description") : "", content, true);

            synopsisItem = new TextAreaItem(metadataEditPage, "Synopsis", metadata.containsKey("synopsis") && !metadata.get("synopsis").isBlank() ? metadata.get("synopsis") : "", content, true);

            lyricsItem = new TextAreaItem(metadataEditPage, "Lyrics", metadata.containsKey("lyrics") && !metadata.get("lyrics").isBlank() ? metadata.get("lyrics") : "", content, false);
            if (mediaType.equals("Music video")) {
                content.getChildren().add(lyricsItem);
            }

            releaseDateItem = new DatePickerItem(metadataEditPage, metadata.containsKey("date") && !metadata.get("date").isBlank() ? metadata.get("date") : "", content, true);
            commentItem = new TextAreaItem(metadataEditPage, "Comment", metadata.containsKey("comment") && !metadata.get("comment").isBlank() ? metadata.get("comment") : "", content, true);
        }

        metadataEditPage.textBox.getChildren().add(content);
    }

    public void updateMediaType(String value){
        mediaType = value;
        switch (value) {
            case "TV Show" -> {
                content.getChildren().removeAll(trackItem, albumItem, lyricsItem);
                content.getChildren().addAll(2, Arrays.asList(seriesTitleItem, seasonNumberItem, episodeNumberItem, networkItem));
                artistItem.label.setText("Cast");
                albumArtistItem.label.setText("Director");
                if (!content.getChildren().contains(albumArtistItem)) content.getChildren().add(7, albumArtistItem);
                composerItem.label.setText("Writers");
                if (!content.getChildren().contains(composerItem)) content.getChildren().add(8, composerItem);
            }
            case "Movie" -> {
                content.getChildren().removeAll(trackItem, albumItem, lyricsItem, seriesTitleItem, seasonNumberItem, episodeNumberItem, networkItem);
                artistItem.label.setText("Cast");
                albumArtistItem.label.setText("Director");
                if (!content.getChildren().contains(albumArtistItem)) content.getChildren().add(3, albumArtistItem);
                composerItem.label.setText("Writers");
                if (!content.getChildren().contains(composerItem)) content.getChildren().add(4, composerItem);
            }
            case "Music video" -> {
                content.getChildren().removeAll(seriesTitleItem, seasonNumberItem, episodeNumberItem, networkItem);
                artistItem.label.setText("Artist");
                if (!content.getChildren().contains(trackItem)) content.getChildren().add(3, trackItem);
                if (!content.getChildren().contains(albumItem)) content.getChildren().add(4, albumItem);
                albumArtistItem.label.setText("Album artist");
                if (!content.getChildren().contains(albumArtistItem)) content.getChildren().add(5, albumArtistItem);
                composerItem.label.setText("Composer");
                if (!content.getChildren().contains(composerItem)) content.getChildren().add(6, composerItem);
                if (!content.getChildren().contains(lyricsItem)) content.getChildren().add(10, lyricsItem);
            }
            case "Podcast", "Home video" -> {
                content.getChildren().removeAll(trackItem, albumItem, lyricsItem, seriesTitleItem, seasonNumberItem, episodeNumberItem, networkItem, albumArtistItem, composerItem);
                artistItem.label.setText("Artist");
            }
        }
    }

    @Override
    public Map<String, String> saveMetadata(){
        Map<String, String> mediaInformation = new HashMap<>();

        if(!titleItem.textArea.getText().isBlank()) mediaInformation.put("title", titleItem.textArea.getText());
        if(!artistItem.textArea.getText().isBlank()) mediaInformation.put("artist", artistItem.textArea.getText());

        String mediaTypeString = switch (mediaType) {
            case "Music video" -> "6";
            case "Movie" -> "9";
            case "TV Show" -> "10";
            case "Podcast" -> "21";
            default -> "0";
        };

        mediaInformation.put("media_type", mediaTypeString);

        if(mediaType.equals("TV Show")){
            if(!seriesTitleItem.textArea.getText().isBlank()) mediaInformation.put("show", seriesTitleItem.textArea.getText());
            if(seasonNumberItem.numberSpinner.spinner.getValue() != 0) mediaInformation.put("season_number", String.valueOf(seasonNumberItem.numberSpinner.spinner.getValue()));
            if(episodeNumberItem.numberSpinner.spinner.getValue() != 0) mediaInformation.put("episode_sort", String.valueOf(episodeNumberItem.numberSpinner.spinner.getValue()));
            if(!networkItem.textArea.getText().isBlank()) mediaInformation.put("network", networkItem.textArea.getText());
        }
        else {
           seriesTitleItem.textArea.setText("");
           seasonNumberItem.numberSpinner.spinner.getValueFactory().setValue(0);
           episodeNumberItem.numberSpinner.spinner.getValueFactory().setValue(0);
           networkItem.textArea.setText("");
        }

        if(mediaType.equals("Music video") || mediaType.equals("Movie") || mediaType.equals("TV Show")){
            if(!composerItem.textArea.getText().isBlank()) mediaInformation.put("composer", composerItem.textArea.getText());
            if(!albumArtistItem.textArea.getText().isBlank()) mediaInformation.put("album_artist", albumArtistItem.textArea.getText());
        }
        else {
            composerItem.textArea.setText("");
            albumArtistItem.textArea.setText("");
        }
        if(!genreItem.textArea.getText().isBlank()) mediaInformation.put("genre", genreItem.textArea.getText());
        if(!descriptionItem.textArea.getText().isBlank()) mediaInformation.put("description", descriptionItem.textArea.getText());
        if(!synopsisItem.textArea.getText().isBlank()) mediaInformation.put("synopsis", synopsisItem.textArea.getText());

        if(mediaType.equals("Music video")) {
            if(!lyricsItem.textArea.getText().isBlank()) mediaInformation.put("lyrics", lyricsItem.textArea.getText());
            if(!albumItem.textArea.getText().isBlank()) mediaInformation.put("album", albumItem.textArea.getText());
            if(trackItem.numberSpinner1.spinner.getValue() != 0){
                String trackString = String.valueOf(trackItem.numberSpinner1.spinner.getValue());
                if(trackItem.numberSpinner2.spinner.getValue() != 0) trackString = trackString.concat("/" + trackItem.numberSpinner2.spinner.getValue());
                mediaInformation.put("track", trackString);
            }
        }
        else {
            lyricsItem.textArea.setText("");
            albumItem.textArea.setText("");
            trackItem.numberSpinner1.spinner.getValueFactory().setValue(0);
            trackItem.numberSpinner2.spinner.getValueFactory().setValue(0);
        }
        if(releaseDateItem.datePicker.getValue() != null) mediaInformation.put("date", releaseDateItem.datePicker.getValue().format(releaseDateItem.dateTimeFormatter));
        if(!commentItem.textArea.getText().isBlank()) mediaInformation.put("comment", commentItem.textArea.getText());

        return mediaInformation;

    }
}

