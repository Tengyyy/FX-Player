package tengy.windows.mediaInformation.items;

import tengy.Utilities;
import javafx.scene.layout.VBox;
import tengy.windows.mediaInformation.components.*;
import tengy.windows.mediaInformation.MediaInformationWindow;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Mp4Item implements MediaInformationItem {

    MediaInformationWindow mediaInformationWindow;

    TextFieldItem titleItem = null;
    ComboBoxItem comboboxItem = null;
    TextFieldItem seriesTitleItem = null;
    TwoSpinnerItem seasonEpisodeItem = null;
    TextFieldItem networkItem = null;
    TextAreaItem artistItem = null;
    DoubleSpinnerItem trackItem = null;
    TextAreaItem albumItem = null;
    TextAreaItem albumArtistItem = null;
    TextAreaItem composerItem = null;
    TextFieldItem genreItem = null;
    TextAreaItem descriptionItem = null;
    TextAreaItem synopsisItem = null;
    TextAreaItem lyricsItem = null;
    DatePickerItem releaseDateItem = null;
    TextAreaItem commentItem = null;

    VBox content = new VBox();

    String mediaType;
    Map<String, String> metadata;



    public Mp4Item(MediaInformationWindow mediaInformationWindow, Map<String, String> metadata){
        this.mediaInformationWindow = mediaInformationWindow;
        this.metadata = metadata;

        content.setSpacing(15);

        if(metadata != null && metadata.containsKey("media_type")){
            String type = metadata.get("media_type");

            switch (type) {
                case "6", "1" -> mediaType = "Music video";
                case "9", "0" -> mediaType = "Movie";
                case "10" -> mediaType = "TV Show";
                case "21" -> mediaType = "Podcast";
                case "2" -> mediaType = "Audiobook";
                case "14" -> mediaType = "Ringtone";
                default -> mediaType = "Home video";
            }
        }
        else {
            mediaType = "Home video";
        }

        if(metadata != null) {
            titleItem = new TextFieldItem(mediaInformationWindow, "Title", metadata.containsKey("title") && !metadata.get("title").isBlank() ? metadata.get("title") : "", content, true);
            comboboxItem = new ComboBoxItem(mediaInformationWindow, content, true, mediaType, "Music video", "Movie", "TV Show", "Podcast", "Audiobook", "Ringtone", "Home video");
            comboboxItem.comboBox.valueProperty().addListener((observableValue, oldValue, newValue) -> {
                if (oldValue != null && !oldValue.equals(newValue)) updateMediaType(newValue);
            });

            seriesTitleItem = new TextFieldItem(mediaInformationWindow, "Series title", metadata.containsKey("show") && !metadata.get("show").isBlank() ? metadata.get("show") : "", content, false);
            seasonEpisodeItem = new TwoSpinnerItem(mediaInformationWindow, "Season number", metadata.containsKey("season_number") && !metadata.get("season_number").isBlank() ? metadata.get("season_number") : "", "Episode number", metadata.containsKey("episode_sort") && !metadata.get("episode_sort").isBlank() ? metadata.get("episode_sort") : "", content, false);
            networkItem = new TextFieldItem(mediaInformationWindow, "Network", metadata.containsKey("network") && !metadata.get("network").isBlank() ? metadata.get("network") : "", content, false);

            if (mediaType.equals("TV Show")) {
                // TV Show fields
                content.getChildren().addAll(seriesTitleItem, seasonEpisodeItem, networkItem);
            }

            artistItem = new TextAreaItem(mediaInformationWindow, "Artist", metadata.containsKey("artist") && !metadata.get("artist").isBlank() ? metadata.get("artist") : "", content, true);
            if (mediaType.equals("TV Show") || mediaType.equals("Movie")) artistItem.label.setText("Cast");
            else if (mediaType.equals("Audiobook")) artistItem.label.setText("Author");

            String[] trackString = Utilities.splitString(metadata.getOrDefault("track", ""));
            trackItem = new DoubleSpinnerItem(mediaInformationWindow, "Track number", trackString[0], trackString[1], content, false);

            albumItem = new TextAreaItem(mediaInformationWindow, "Album", metadata.containsKey("album") && !metadata.get("album").isBlank() ? metadata.get("album") : "", content, false);

            if (mediaType.equals("Music video") || mediaType.equals("Ringtone")) {
                content.getChildren().addAll(trackItem, albumItem);
            }

            albumArtistItem = new TextAreaItem(mediaInformationWindow, "Director", metadata.containsKey("album_artist") && !metadata.get("album_artist").isBlank() ? metadata.get("album_artist") : "", content, false);

            if (mediaType.equals("TV Show") || mediaType.equals("Movie")) {
                content.getChildren().add(albumArtistItem);
            } else if (mediaType.equals("Music video") || mediaType.equals("Ringtone")) {
                albumArtistItem.label.setText("Album artist");
                content.getChildren().add(albumArtistItem);
            }



            composerItem = new TextAreaItem(mediaInformationWindow, "Writers", metadata.containsKey("composer") && !metadata.get("composer").isBlank() ? metadata.get("composer") : "", content, false);


            if (mediaType.equals("TV Show") || mediaType.equals("Movie")) {
                content.getChildren().add(composerItem);
            } else if (mediaType.equals("Music video") || mediaType.equals("Ringtone")) {
                composerItem.label.setText("Composer");
                content.getChildren().add(composerItem);
            }

            genreItem = new TextFieldItem(mediaInformationWindow, "Genre", metadata.containsKey("genre") && !metadata.get("genre").isBlank() ? metadata.get("genre") : "", content, true);

            descriptionItem = new TextAreaItem(mediaInformationWindow, "Description", metadata.containsKey("description") && !metadata.get("description").isBlank() ? metadata.get("description") : "", content, true);

            synopsisItem = new TextAreaItem(mediaInformationWindow, "Synopsis", metadata.containsKey("synopsis") && !metadata.get("synopsis").isBlank() ? metadata.get("synopsis") : "", content, true);

            lyricsItem = new TextAreaItem(mediaInformationWindow, "Lyrics", metadata.containsKey("lyrics") && !metadata.get("lyrics").isBlank() ? metadata.get("lyrics") : "", content, false);
            if (mediaType.equals("Music video") || mediaType.equals("Ringtone")) {
                content.getChildren().add(lyricsItem);
            }

            releaseDateItem = new DatePickerItem(mediaInformationWindow, metadata.containsKey("date") && !metadata.get("date").isBlank() ? metadata.get("date") : "", content, true);
            commentItem = new TextAreaItem(mediaInformationWindow, "Comment", metadata.containsKey("comment") && !metadata.get("comment").isBlank() ? metadata.get("comment") : "", content, true);
        }

        mediaInformationWindow.textBox.getChildren().add(content);
    }

    public void updateMediaType(String value){
        mediaType = value;
        switch (value) {
            case "TV Show" -> {
                content.getChildren().removeAll(trackItem, albumItem, lyricsItem);
                content.getChildren().addAll(2, Arrays.asList(seriesTitleItem, seasonEpisodeItem, networkItem));
                artistItem.label.setText("Cast");
                albumArtistItem.label.setText("Director");
                if (!content.getChildren().contains(albumArtistItem)) content.getChildren().add(6, albumArtistItem);
                composerItem.label.setText("Writers");
                if (!content.getChildren().contains(composerItem)) content.getChildren().add(7, composerItem);
            }
            case "Movie" -> {
                content.getChildren().removeAll(trackItem, albumItem, lyricsItem, seriesTitleItem, seasonEpisodeItem, networkItem);
                artistItem.label.setText("Cast");
                albumArtistItem.label.setText("Director");
                if (!content.getChildren().contains(albumArtistItem)) content.getChildren().add(3, albumArtistItem);
                composerItem.label.setText("Writers");
                if (!content.getChildren().contains(composerItem)) content.getChildren().add(4, composerItem);
            }
            case "Music video", "Ringtone" -> {
                content.getChildren().removeAll(seriesTitleItem, seasonEpisodeItem, networkItem);
                artistItem.label.setText("Artist");
                if (!content.getChildren().contains(trackItem)) content.getChildren().add(3, trackItem);
                if (!content.getChildren().contains(albumItem)) content.getChildren().add(4, albumItem);
                albumArtistItem.label.setText("Album artist");
                if (!content.getChildren().contains(albumArtistItem)) content.getChildren().add(5, albumArtistItem);
                composerItem.label.setText("Composer");
                if (!content.getChildren().contains(composerItem)) content.getChildren().add(6, composerItem);
                if (!content.getChildren().contains(lyricsItem)) content.getChildren().add(10, lyricsItem);
            }
            case "Audiobook" -> {
                content.getChildren().removeAll(trackItem, albumItem, lyricsItem, seriesTitleItem, seasonEpisodeItem, networkItem, albumArtistItem, composerItem);
                artistItem.label.setText("Author");
            }
            case "Home video", "Podcast" -> {
                content.getChildren().removeAll(trackItem, albumItem, lyricsItem, seriesTitleItem, seasonEpisodeItem, networkItem, albumArtistItem, composerItem);
                artistItem.label.setText("Artist");
            }
        }
    }

    @Override
    public Map<String, String> createMetadataMap(){
        Map<String, String> mediaInformation = new HashMap<>();

        if(!titleItem.textField.getText().isBlank()) mediaInformation.put("title", titleItem.textField.getText());
        if(!artistItem.textArea.getText().isBlank()) mediaInformation.put("artist", artistItem.textArea.getText());

        String mediaTypeString = switch (mediaType) {
            case "Music video" -> "6";
            case "Movie" -> "9";
            case "TV Show" -> "10";
            case "Podcast" -> "21";
            case "Audiobook" -> "2";
            case "Ringtone" -> "14";
            default -> "0";
        };

        mediaInformation.put("media_type", mediaTypeString);

        if(mediaType.equals("TV Show")){
            if(!seriesTitleItem.textField.getText().isBlank()) mediaInformation.put("show", seriesTitleItem.textField.getText());
            if(seasonEpisodeItem.numberSpinner1.spinner.getValue() > 0) mediaInformation.put("season_number", String.valueOf(seasonEpisodeItem.numberSpinner1.spinner.getValue()));
            if(seasonEpisodeItem.numberSpinner2.spinner.getValue() > 0) mediaInformation.put("episode_sort", String.valueOf(seasonEpisodeItem.numberSpinner2.spinner.getValue()));
            if(!networkItem.textField.getText().isBlank()) mediaInformation.put("network", networkItem.textField.getText());
        }
        else {
           seriesTitleItem.textField.setText("");
           seasonEpisodeItem.numberSpinner1.spinner.getValueFactory().setValue(0);
           seasonEpisodeItem.numberSpinner2.spinner.getValueFactory().setValue(0);
           networkItem.textField.setText("");
        }

        if(mediaType.equals("Music video") || mediaType.equals("Movie") || mediaType.equals("TV Show")){
            if(!composerItem.textArea.getText().isBlank()) mediaInformation.put("composer", composerItem.textArea.getText());
            if(!albumArtistItem.textArea.getText().isBlank()) mediaInformation.put("album_artist", albumArtistItem.textArea.getText());
        }
        else {
            composerItem.textArea.setText("");
            albumArtistItem.textArea.setText("");
        }

        if(!genreItem.textField.getText().isBlank()) mediaInformation.put("genre", genreItem.textField.getText());
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
        String date = releaseDateItem.parseDate();
        if(!date.isEmpty()) mediaInformation.put("date", date);
        if(!commentItem.textArea.getText().isBlank()) mediaInformation.put("comment", commentItem.textArea.getText());

        return mediaInformation;

    }
}

