package fxplayer.windows.mediaInformation.items;

import javafx.scene.layout.VBox;
import fxplayer.windows.mediaInformation.components.*;
import fxplayer.windows.mediaInformation.MediaInformationWindow;

import java.util.HashMap;
import java.util.Map;

public class MkvItem implements MediaInformationItem {

    MediaInformationWindow mediaInformationWindow;

    TextFieldItem titleItem = null;
    ComboBoxItem comboboxItem = null;
    TextFieldItem seriesTitleItem = null;
    TwoSpinnerItem seasonEpisodeItem = null;
    TextFieldItem artistItem = null;
    DoubleSpinnerItem trackItem = null;
    DoubleSpinnerItem discItem = null;
    TextFieldItem albumItem = null;
    TextAreaItem composerItem = null;
    TextAreaItem directorItem = null;
    TextAreaItem producerItem = null;
    TextAreaItem editorItem = null;
    TextAreaItem narratorItem = null;
    TextAreaItem authorItem = null;
    TextAreaItem castItem = null;
    TextFieldItem genreItem = null;
    TextFieldItem languageItem = null;
    TextAreaItem descriptionItem = null;
    TextAreaItem synopsisItem = null;
    TextAreaItem lyricsItem = null;
    TextAreaItem keywordsItem = null;
    DatePickerItem releaseDateItem = null;
    DatePickerItem recordingDateItem = null;
    PlaceholderTextFieldItem imdbItem = null;
    PlaceholderTextFieldItem tmdbItem = null;
    TextFieldItem publisherItem = null;
    TextAreaItem commentItem = null;


    VBox wrapper = new VBox();
    VBox mainContainer = new VBox();
    VBox customTagContainer = new VBox();


    String mediaType;
    Map<String, String> metadata;

    MkvItem(MediaInformationWindow mediaInformationWindow, Map<String, String> metadata){
        this.mediaInformationWindow = mediaInformationWindow;
        this.metadata = metadata;

        mainContainer.setSpacing(15);
        customTagContainer.setSpacing(15);


        if(metadata != null) {

            mediaType = metadata.getOrDefault("CONTENT_TYPE", "Video");

            titleItem = new TextFieldItem(mediaInformationWindow, "Title", metadata.containsKey("TITLE") && !metadata.get("TITLE").isBlank() ? metadata.get("TITLE") : "", mainContainer, false);
            comboboxItem = new ComboBoxItem(mediaInformationWindow, mainContainer, false, mediaType, "Music video", "Music", "Movie", "TV Show", "Podcast", "Audiobook", "Ringtone", "Video");
            comboboxItem.comboBox.valueProperty().addListener((observableValue, oldValue, newValue) -> {
                if (oldValue != null && !oldValue.equals(newValue)) updateMediaType(newValue);
            });

            seriesTitleItem = new TextFieldItem(mediaInformationWindow, "Series title", metadata.containsKey("SERIES_TITLE") && !metadata.get("SERIES_TITLE").isBlank() ? metadata.get("SERIES_TITLE") : "", mainContainer, false);
            seasonEpisodeItem = new TwoSpinnerItem(mediaInformationWindow, "Season number", metadata.containsKey("SEASON") && !metadata.get("SEASON").isBlank() ? metadata.get("SEASON") : "", "Episode number", metadata.containsKey("EPISODE") && !metadata.get("EPISODE").isBlank() ? metadata.get("EPISODE") : "", mainContainer, false);
            publisherItem = new TextFieldItem(mediaInformationWindow, "Publisher", metadata.containsKey("PUBLISHER") && !metadata.get("PUBLISHER").isBlank() ? metadata.get("PUBLISHER") : "", mainContainer, false);
            trackItem = new DoubleSpinnerItem(mediaInformationWindow, "Track number", metadata.containsKey("TRACK") && !metadata.get("TRACK").isBlank() ? metadata.get("TRACK") : "", metadata.containsKey("TRACK_TOTAL") && !metadata.get("TRACK_TOTAL").isBlank() ? metadata.get("TRACK_TOTAL") : "", mainContainer, false);
            discItem = new DoubleSpinnerItem(mediaInformationWindow, "Disc number", metadata.containsKey("DISC") && !metadata.get("DISC").isBlank() ? metadata.get("DISC") : "", metadata.containsKey("DISC_TOTAL") && !metadata.get("DISC_TOTAL").isBlank() ? metadata.get("DISC_TOTAL") : "", mainContainer, false);
            albumItem = new TextFieldItem(mediaInformationWindow, "Album", metadata.containsKey("ALBUM") && !metadata.get("ALBUM").isBlank() ? metadata.get("ALBUM") : "", mainContainer, false);
            artistItem = new TextFieldItem(mediaInformationWindow, "Artist", metadata.containsKey("ARTIST") && !metadata.get("ARTIST").isBlank() ? metadata.get("ARTIST") : "", mainContainer, false);
            composerItem = new TextAreaItem(mediaInformationWindow, "Composer", metadata.containsKey("COMPOSER") && !metadata.get("COMPOSER").isBlank() ? metadata.get("COMPOSER") : "", mainContainer, false);
            directorItem = new TextAreaItem(mediaInformationWindow, "Director", metadata.containsKey("DIRECTOR") && !metadata.get("DIRECTOR").isBlank() ? metadata.get("DIRECTOR") : "", mainContainer, false);
            producerItem = new TextAreaItem(mediaInformationWindow, "Producer", metadata.containsKey("PRODUCER") && !metadata.get("PRODUCER").isBlank() ? metadata.get("PRODUCER") : "", mainContainer, false);
            editorItem = new TextAreaItem(mediaInformationWindow, "Edited by", metadata.containsKey("EDITED_BY") && !metadata.get("EDITED_BY").isBlank() ? metadata.get("EDITED_BY") : "", mainContainer, false);
            narratorItem = new TextAreaItem(mediaInformationWindow, "Narrator", metadata.containsKey("_NARRATOR") && !metadata.get("_NARRATOR").isBlank() ? metadata.get("_NARRATOR") : "", mainContainer, false);
            authorItem = new TextAreaItem(mediaInformationWindow, "Book author", metadata.containsKey("_AUTHOR") && !metadata.get("_AUTHOR").isBlank() ? metadata.get("_AUTHOR") : "", mainContainer, false);
            castItem = new TextAreaItem(mediaInformationWindow, "Cast", metadata.containsKey("CAST") && !metadata.get("CAST").isBlank() ? metadata.get("CAST") : "", mainContainer, false);
            keywordsItem = new TextAreaItem(mediaInformationWindow, "Keywords", metadata.containsKey("KEYWORDS") && !metadata.get("KEYWORDS").isBlank() ? metadata.get("KEYWORDS") : "", mainContainer, false);
            genreItem = new TextFieldItem(mediaInformationWindow, "Genre", metadata.containsKey("GENRE") && !metadata.get("GENRE").isBlank() ? metadata.get("GENRE") : "", mainContainer, false);
            descriptionItem = new TextAreaItem(mediaInformationWindow, "Description", metadata.containsKey("DESCRIPTION") && !metadata.get("DESCRIPTION").isBlank() ? metadata.get("DESCRIPTION") : "", mainContainer, false);
            synopsisItem = new TextAreaItem(mediaInformationWindow, "Synopsis", metadata.containsKey("SYNOPSIS") && !metadata.get("SYNOPSIS").isBlank() ? metadata.get("SYNOPSIS") : "", mainContainer, false);
            languageItem = new TextFieldItem(mediaInformationWindow, "Language", metadata.containsKey("_LANGUAGE") && !metadata.get("_LANGUAGE").isBlank() ? metadata.get("_LANGUAGE") : "", mainContainer, false);
            lyricsItem = new TextAreaItem(mediaInformationWindow, "Lyrics", metadata.containsKey("LYRICS") && !metadata.get("LYRICS").isBlank() ? metadata.get("LYRICS") : "", mainContainer, false);
            releaseDateItem = new DatePickerItem(mediaInformationWindow, metadata.containsKey("DATE_RELEASED") && !metadata.get("DATE_RELEASED").isBlank() ? metadata.get("DATE_RELEASED") : "", mainContainer, false);
            recordingDateItem = new DatePickerItem(mediaInformationWindow, "Recording date", metadata.containsKey("DATE_RECORDED") && !metadata.get("DATE_RECORDED").isBlank() ? metadata.get("DATE_RECORDED") : "", mainContainer, false);
            commentItem = new TextAreaItem(mediaInformationWindow, "Comment", metadata.containsKey("COMMENT") && !metadata.get("COMMENT").isBlank() ? metadata.get("COMMENT") : "", mainContainer, false);
            imdbItem = new PlaceholderTextFieldItem(mediaInformationWindow, "IMDb identifier",  metadata.containsKey("IMDB") && !metadata.get("IMDB").isBlank() ? metadata.get("IMDB") : "", "tt0000000", mainContainer, false);
            tmdbItem = new PlaceholderTextFieldItem(mediaInformationWindow, "TMDb identifier",  metadata.containsKey("TMDB") && !metadata.get("TMDB").isBlank() ? metadata.get("TMDB") : "", "movie/00000", mainContainer, false);


            mainContainer.getChildren().addAll(titleItem, comboboxItem);
            switch (mediaType){
                case "Music video", "Music" -> mainContainer.getChildren().addAll(albumItem, artistItem, composerItem, producerItem, trackItem, discItem, genreItem, releaseDateItem, recordingDateItem, publisherItem, lyricsItem, languageItem, keywordsItem, commentItem);
                case "Movie" -> mainContainer.getChildren().addAll(castItem, directorItem, producerItem, keywordsItem, genreItem, releaseDateItem, descriptionItem, synopsisItem, imdbItem, tmdbItem, commentItem);
                case "TV Show" ->  mainContainer.getChildren().addAll(seriesTitleItem, seasonEpisodeItem, castItem, directorItem, producerItem, keywordsItem, genreItem, releaseDateItem, descriptionItem, synopsisItem, imdbItem, tmdbItem, commentItem);
                case "Podcast" -> mainContainer.getChildren().addAll(seriesTitleItem, seasonEpisodeItem, artistItem, producerItem, editorItem, keywordsItem, genreItem, descriptionItem, releaseDateItem, commentItem);
                case "Audiobook" -> mainContainer.getChildren().addAll(narratorItem, authorItem, keywordsItem, genreItem, languageItem, descriptionItem, synopsisItem, releaseDateItem, publisherItem, commentItem);
                case "Ringtone" -> mainContainer.getChildren().addAll(artistItem, keywordsItem, lyricsItem, languageItem, releaseDateItem, commentItem);
                case "Video" -> mainContainer.getChildren().addAll(artistItem, producerItem, editorItem, keywordsItem, genreItem, descriptionItem, releaseDateItem, recordingDateItem, commentItem);
            }
        }

        wrapper.getChildren().addAll(mainContainer, customTagContainer);
        mediaInformationWindow.textBox.getChildren().add(wrapper);
    }

    public void updateMediaType(String value){
        mainContainer.getChildren().clear();
        mainContainer.getChildren().addAll(titleItem, comboboxItem);
        mediaType = value;
        switch (value){
            case "Music video", "Music" -> mainContainer.getChildren().addAll(albumItem, artistItem, composerItem, producerItem, trackItem, discItem, genreItem, releaseDateItem, recordingDateItem, publisherItem, lyricsItem, languageItem, keywordsItem, commentItem);
            case "Movie" -> mainContainer.getChildren().addAll(castItem, directorItem, producerItem, keywordsItem, genreItem, releaseDateItem, descriptionItem, synopsisItem, imdbItem, tmdbItem, commentItem);
            case "TV Show" ->  mainContainer.getChildren().addAll(seriesTitleItem, seasonEpisodeItem, castItem, directorItem, producerItem, keywordsItem, genreItem, releaseDateItem, descriptionItem, synopsisItem, imdbItem, tmdbItem, commentItem);
            case "Podcast" -> mainContainer.getChildren().addAll(seriesTitleItem, seasonEpisodeItem, artistItem, producerItem, editorItem, keywordsItem, genreItem, descriptionItem, releaseDateItem, commentItem);
            case "Audiobook" -> mainContainer.getChildren().addAll(narratorItem, authorItem, keywordsItem, genreItem, languageItem, descriptionItem, synopsisItem, releaseDateItem, publisherItem, commentItem);
            case "Ringtone" -> mainContainer.getChildren().addAll(artistItem, keywordsItem, lyricsItem, languageItem, releaseDateItem, commentItem);
            case "Video" -> mainContainer.getChildren().addAll(artistItem, producerItem, editorItem, keywordsItem, genreItem, descriptionItem, releaseDateItem, recordingDateItem, commentItem);
        }
    }

    @Override
    public Map<String, String> createMetadataMap(){
        Map<String, String> mediaInformation = new HashMap<>();


        return mediaInformation;
    }
}

