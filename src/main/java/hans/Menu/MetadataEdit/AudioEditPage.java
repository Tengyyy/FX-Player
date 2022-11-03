package hans.Menu.MetadataEdit;

import hans.MediaItems.AudioItem;
import hans.MediaItems.MediaItem;
import javafx.scene.layout.VBox;
import org.jaudiotagger.tag.FieldKey;

import java.util.Map;

public class AudioEditPage {

    MetadataEditPage metadataEditPage;
    AudioItem audioItem;

    TextAreaItem titleItem = null;
    TextAreaItem artistItem = null;
    TextAreaItem albumItem = null;
    TextAreaItem releaseDateItem = null;
    TwoTextFieldItem trackItem = null;
    TwoTextFieldItem discItem = null;
    TextAreaItem albumArtistItem = null;
    TextAreaItem composerItem = null;
    TextAreaItem publisherItem = null;
    TextAreaItem genreItem = null;
    TextAreaItem languageItem = null;
    TextAreaItem lyricsItem = null;
    TextAreaItem commentItem = null;


    VBox content = new VBox();

    Map<FieldKey, String> metadata;


    AudioEditPage(MetadataEditPage metadataEditPage, AudioItem audioItem){
        this.metadataEditPage = metadataEditPage;
        this.audioItem = audioItem;

        content.setSpacing(15);

        metadata = audioItem.getMediaInformation();

        if(metadata != null) {
            titleItem = new TextAreaItem(metadataEditPage, "Title", metadata.get(FieldKey.TITLE).isBlank() ? "" : metadata.get(FieldKey.TITLE), content, true);
            artistItem = new TextAreaItem(metadataEditPage, "Artist", metadata.get(FieldKey.ARTIST).isBlank() ? "" : metadata.get(FieldKey.ARTIST), content, true);
            albumItem = new TextAreaItem(metadataEditPage, "Album", metadata.get(FieldKey.ALBUM).isBlank() ? "" : metadata.get(FieldKey.ALBUM), content, true);
            releaseDateItem = new TextAreaItem(metadataEditPage, "Release date", metadata.get(FieldKey.YEAR).isBlank() ? "" : metadata.get(FieldKey.YEAR), content, true);
            trackItem = new TwoTextFieldItem(metadataEditPage, "Track number", metadata.get(FieldKey.TRACK) + "/" + metadata.get(FieldKey.TRACK_TOTAL), content, true); // keep an eye on potential problems for the twofield items
            discItem = new TwoTextFieldItem(metadataEditPage, "Disc number", metadata.get(FieldKey.DISC_NO) + "/" + metadata.get(FieldKey.DISC_TOTAL), content, true); // keep an eye on potential problems for the twofield items
            albumArtistItem = new TextAreaItem(metadataEditPage, "Album artist", metadata.get(FieldKey.ALBUM_ARTIST).isBlank() ? "" : metadata.get(FieldKey.ALBUM_ARTIST), content, true);
            composerItem = new TextAreaItem(metadataEditPage, "Composer", metadata.get(FieldKey.COMPOSER).isBlank() ? "" : metadata.get(FieldKey.COMPOSER), content, true);
            publisherItem = new TextAreaItem(metadataEditPage, "Publisher", metadata.get(FieldKey.RECORD_LABEL).isBlank() ? "" : metadata.get(FieldKey.RECORD_LABEL), content, true);
            genreItem = new TextAreaItem(metadataEditPage, "Genre", metadata.get(FieldKey.GENRE).isBlank() ? "" : metadata.get(FieldKey.GENRE), content, true);
            languageItem = new TextAreaItem(metadataEditPage, "Language", metadata.get(FieldKey.LANGUAGE).isBlank() ? "" : metadata.get(FieldKey.LANGUAGE), content, true);
            lyricsItem = new TextAreaItem(metadataEditPage, "Lyrics", metadata.get(FieldKey.LYRICS).isBlank() ? "" : metadata.get(FieldKey.LYRICS), content, true);
            commentItem = new TextAreaItem(metadataEditPage, "Comment", metadata.get(FieldKey.COMMENT).isBlank() ? "" : metadata.get(FieldKey.COMMENT), content, true);
        }

        metadataEditPage.textBox.getChildren().add(content);
    }


    public void saveMetadata(){

    }
}

