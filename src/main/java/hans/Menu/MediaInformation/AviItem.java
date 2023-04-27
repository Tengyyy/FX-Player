package hans.Menu.MediaInformation;

import hans.Utilities;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;

public class AviItem implements MediaInformationItem {

    MediaInformationPage mediaInformationPage;

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


    AviItem(MediaInformationPage mediaInformationPage, Map<String, String> metadata){
        this.mediaInformationPage = mediaInformationPage;
        this.metadata = metadata;

        content.setSpacing(15);

        if(metadata != null) {
            titleItem = new TextAreaItem(mediaInformationPage, "Title", metadata.containsKey("title") && !metadata.get("title").isBlank() ? metadata.get("title") : "", content, true);
            artistItem = new TextAreaItem(mediaInformationPage, "Artist", metadata.containsKey("artist") && !metadata.get("artist").isBlank() ? metadata.get("artist") : "", content, true);
            albumItem = new TextAreaItem(mediaInformationPage, "Album", metadata.containsKey("product") && !metadata.get("product").isBlank() ? metadata.get("product") : "", content, true);

            String[] trackString = Utilities.splitString(metadata.getOrDefault("track", ""));
            trackItem = new TwoSpinnerItem(mediaInformationPage, "Track number", trackString[0], trackString[1], content, true);

            genreItem = new TextAreaItem(mediaInformationPage, "Genre", metadata.containsKey("genre") && !metadata.get("genre").isBlank() ? metadata.get("genre") : "", content, true);
            languageItem = new TextAreaItem(mediaInformationPage, "Language", metadata.containsKey("language") && !metadata.get("language").isBlank() ? metadata.get("language") : "", content, true);
            releaseDateItem = new DatePickerItem(mediaInformationPage, metadata.containsKey("date") && !metadata.get("date").isBlank() ? metadata.get("date") : "", content, true);
            copyrightItem = new TextAreaItem(mediaInformationPage, "Copyright", metadata.containsKey("copyright") && !metadata.get("copyright").isBlank() ? metadata.get("copyright") : "", content, true);
            commentItem = new TextAreaItem(mediaInformationPage, "Comment", metadata.containsKey("comment") && !metadata.get("comment").isBlank() ? metadata.get("comment") : "", content, true);
        }

        mediaInformationPage.textBox.getChildren().add(content);
    }

    @Override
    public Map<String, String> createMetadataMap(){
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

