package hans.Menu.Settings;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class MetadataSection extends VBox {

    SettingsPage settingsPage;

    Label metadataSectionTitle = new Label("Metadata editing");


    MetadataSection(SettingsPage settingsPage){
        this.settingsPage = settingsPage;

        metadataSectionTitle.getStyleClass().add("settingsSectionTitle");


        this.getChildren().addAll(metadataSectionTitle);
        this.setSpacing(25);
    }
}
