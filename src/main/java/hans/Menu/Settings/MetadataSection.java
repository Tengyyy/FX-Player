package hans.Menu.Settings;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class MetadataSection extends VBox {

    SettingsPage settingsPage;

    Label metadataSectionTitle = new Label("Metadata editing");

    Toggle overwriteToggle;
    BooleanProperty overwriteOn = new SimpleBooleanProperty();

    MetadataSection(SettingsPage settingsPage){
        this.settingsPage = settingsPage;

        metadataSectionTitle.getStyleClass().add("settingsSectionTitle");

        overwriteToggle = new Toggle(settingsPage, "Overwrite original file when editing metadata", overwriteOn);

        this.getChildren().addAll(metadataSectionTitle, overwriteToggle);
        this.setSpacing(25);
    }
}
