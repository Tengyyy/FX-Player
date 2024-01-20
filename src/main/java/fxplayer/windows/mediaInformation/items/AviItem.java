package fxplayer.windows.mediaInformation.items;

import fxplayer.Utilities;
import javafx.scene.layout.VBox;
import fxplayer.windows.mediaInformation.components.DatePickerItem;
import fxplayer.windows.mediaInformation.components.DoubleSpinnerItem;
import fxplayer.windows.mediaInformation.components.TextAreaItem;
import fxplayer.windows.mediaInformation.components.TextFieldItem;
import fxplayer.windows.mediaInformation.MediaInformationWindow;

import java.util.HashMap;
import java.util.Map;

public class AviItem implements MediaInformationItem {

    MediaInformationWindow mediaInformationWindow;

    TextFieldItem titleItem = null;
    TextFieldItem artistItem = null;
    TextFieldItem albumItem = null;
    DoubleSpinnerItem trackItem = null;
    TextFieldItem genreItem = null;
    TextFieldItem languageItem = null;
    DatePickerItem releaseDateItem = null;
    TextFieldItem copyrightItem = null;
    TextAreaItem commentItem = null;

    VBox content = new VBox();

    Map<String, String> metadata;


    public AviItem(MediaInformationWindow mediaInformationWindow, Map<String, String> metadata){
        this.mediaInformationWindow = mediaInformationWindow;
        this.metadata = metadata;

        content.setSpacing(15);

        if(metadata != null) {
            titleItem = new TextFieldItem(mediaInformationWindow, "Title", metadata.containsKey("title") && !metadata.get("title").isBlank() ? metadata.get("title") : "", content, true);
            artistItem = new TextFieldItem(mediaInformationWindow, "Artist", metadata.containsKey("artist") && !metadata.get("artist").isBlank() ? metadata.get("artist") : "", content, true);
            albumItem = new TextFieldItem(mediaInformationWindow, "Album", metadata.containsKey("product") && !metadata.get("product").isBlank() ? metadata.get("product") : "", content, true);

            String[] trackString = Utilities.splitString(metadata.getOrDefault("track", ""));
            trackItem = new DoubleSpinnerItem(mediaInformationWindow, "Track number", trackString[0], trackString[1], content, true);

            genreItem = new TextFieldItem(mediaInformationWindow, "Genre", metadata.containsKey("genre") && !metadata.get("genre").isBlank() ? metadata.get("genre") : "", content, true);
            languageItem = new TextFieldItem(mediaInformationWindow, "Language", metadata.containsKey("language") && !metadata.get("language").isBlank() ? metadata.get("language") : "", content, true);
            releaseDateItem = new DatePickerItem(mediaInformationWindow, metadata.containsKey("date") && !metadata.get("date").isBlank() ? metadata.get("date") : "", content, true);
            copyrightItem = new TextFieldItem(mediaInformationWindow, "Copyright", metadata.containsKey("copyright") && !metadata.get("copyright").isBlank() ? metadata.get("copyright") : "", content, true);
            commentItem = new TextAreaItem(mediaInformationWindow, "Comment", metadata.containsKey("comment") && !metadata.get("comment").isBlank() ? metadata.get("comment") : "", content, true);
        }

        mediaInformationWindow.textBox.getChildren().add(content);
    }

    @Override
    public Map<String, String> createMetadataMap(){
        Map<String, String> mediaInformation = new HashMap<>();

        if(!titleItem.textField.getText().isBlank()) mediaInformation.put("title", titleItem.textField.getText());
        if(!artistItem.textField.getText().isBlank()) mediaInformation.put("artist", artistItem.textField.getText());
        if(!albumItem.textField.getText().isBlank()) mediaInformation.put("IPRD", albumItem.textField.getText());
        String date = releaseDateItem.parseDate();
        if(!date.isEmpty()) mediaInformation.put("date", date);
        if(trackItem.numberSpinner1.spinner.getValue() != 0){
            String trackString = String.valueOf(trackItem.numberSpinner1.spinner.getValue());
            if(trackItem.numberSpinner2.spinner.getValue() != 0) trackString = trackString.concat("/" + trackItem.numberSpinner2.spinner.getValue());
            mediaInformation.put("track", trackString);
        }
        if(!genreItem.textField.getText().isBlank()) mediaInformation.put("genre", genreItem.textField.getText());
        if(!languageItem.textField.getText().isBlank()) mediaInformation.put("language", languageItem.textField.getText());
        if(!copyrightItem.textField.getText().isBlank())  mediaInformation.put("copyright", copyrightItem.textField.getText());
        if(!commentItem.textArea.getText().isBlank())  mediaInformation.put("comment", commentItem.textArea.getText());

        return mediaInformation;
    }
}

