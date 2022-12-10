package hans.Menu.MetadataEdit;

import hans.MediaItems.MediaItem;
import hans.Utilities;
import javafx.scene.layout.VBox;

import java.time.DateTimeException;
import java.util.HashMap;
import java.util.Map;

public class AviEditItem implements  MetadataEditItem{

    MetadataEditPage metadataEditPage;
    MediaItem mediaItem;

    TextAreaItem titleItem = null;
    TextAreaItem artistItem = null;
    TextAreaItem albumItem = null;
    TwoSpinnerItem trackItem = null;
    TextAreaItem genreItem = null;
    TextAreaItem languageItem = null;
    DatePickerItem releaseDateItem = null;
    TextAreaItem copyrightItem = null;
    TextAreaItem commentItem = null;

    VBox content = new VBox();

    Map<String, String> metadata;


    AviEditItem(MetadataEditPage metadataEditPage, MediaItem mediaItem){
        this.metadataEditPage = metadataEditPage;
        this.mediaItem = mediaItem;

        content.setSpacing(15);

        metadata = mediaItem.getMediaInformation();

        if(metadata != null) {
            titleItem = new TextAreaItem(metadataEditPage, "Title", metadata.containsKey("title") && !metadata.get("title").isBlank() ? metadata.get("title") : "", content, true);
            artistItem = new TextAreaItem(metadataEditPage, "Artist", metadata.containsKey("artist") && !metadata.get("artist").isBlank() ? metadata.get("artist") : "", content, true);
            albumItem = new TextAreaItem(metadataEditPage, "Album", metadata.containsKey("product") && !metadata.get("product").isBlank() ? metadata.get("product") : "", content, true);

            String[] trackString = Utilities.splitString(metadata.getOrDefault("track", ""));
            trackItem = new TwoSpinnerItem(metadataEditPage, "Track number", trackString[0], trackString[1], content, true);

            genreItem = new TextAreaItem(metadataEditPage, "Genre", metadata.containsKey("genre") && !metadata.get("genre").isBlank() ? metadata.get("genre") : "", content, true);
            languageItem = new TextAreaItem(metadataEditPage, "Language", metadata.containsKey("language") && !metadata.get("language").isBlank() ? metadata.get("language") : "", content, true);
            releaseDateItem = new DatePickerItem(metadataEditPage, metadata.containsKey("date") && !metadata.get("date").isBlank() ? metadata.get("date") : "", content, true);
            copyrightItem = new TextAreaItem(metadataEditPage, "Copyright", metadata.containsKey("copyright") && !metadata.get("copyright").isBlank() ? metadata.get("copyright") : "", content, true);
            commentItem = new TextAreaItem(metadataEditPage, "Comment", metadata.containsKey("comment") && !metadata.get("comment").isBlank() ? metadata.get("comment") : "", content, true);
        }

        metadataEditPage.textBox.getChildren().add(content);
    }

    @Override
    public Map<String, String> saveMetadata(){
        Map<String, String> mediaInformation = new HashMap<>();

        if(!titleItem.textArea.getText().isBlank()) mediaInformation.put("title", titleItem.textArea.getText());
        if(!artistItem.textArea.getText().isBlank()) mediaInformation.put("artist", artistItem.textArea.getText());
        if(!albumItem.textArea.getText().isBlank()) mediaInformation.put("IPRD", albumItem.textArea.getText());
        if(releaseDateItem.datePicker.getValue() != null) mediaInformation.put("date", releaseDateItem.datePicker.getValue().format(releaseDateItem.dateTimeFormatter));
        if(trackItem.numberSpinner1.spinner.getValue() != 0){
            String trackString = String.valueOf(trackItem.numberSpinner1.spinner.getValue());
            if(trackItem.numberSpinner2.spinner.getValue() != 0) trackString = trackString.concat("/" + trackItem.numberSpinner2.spinner.getValue());
            mediaInformation.put("track", trackString);
        }
        if(!genreItem.textArea.getText().isBlank()) mediaInformation.put("genre", genreItem.textArea.getText());
        if(!languageItem.textArea.getText().isBlank()) mediaInformation.put("language", languageItem.textArea.getText());
        if(!copyrightItem.textArea.getText().isBlank())  mediaInformation.put("copyright", copyrightItem.textArea.getText());
        if(!commentItem.textArea.getText().isBlank())  mediaInformation.put("comment", commentItem.textArea.getText());

        return mediaInformation;
    }
}

