package tengy.Menu.Settings;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import tengy.PlaybackSettings.PlaybackSettingsState;
import tengy.SVG;
import tengy.Subtitles.SubtitlesState;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import static tengy.Utilities.keyboardFocusOn;

public class PreferencesSection extends VBox implements SettingsSection{

    SettingsPage settingsPage;

    Label preferencesSectionTitle = new Label("Preferences");

    Toggle darkModeToggle;
    public BooleanProperty darkModeOn = new SimpleBooleanProperty();

    Toggle preventSleepToggle;
    public BooleanProperty preventSleepOn = new SimpleBooleanProperty();

    Toggle seekPreviewToggle;
    public BooleanProperty seekPreviewOn = new SimpleBooleanProperty();

    ComboItem recentMediaSizeItem;
    public IntegerProperty recentMediaSizeProperty = new SimpleIntegerProperty();

    public static final String DARK_MODE_ON = "dark_mode_on";
    public static final String PREVENT_SLEEP_ON = "prevent_sleep_on";
    public static final String SEEKBAR_FRAME_PREVIEW_ON = "seekbar_frame_preview_on";
    public static final String HISTORY_SIZE = "history_size";

    IntegerProperty focus = new SimpleIntegerProperty(-1);
    List<Node> focusNodes = new ArrayList<>();


    PreferencesSection(SettingsPage settingsPage){
        this.settingsPage = settingsPage;

        preferencesSectionTitle.getStyleClass().add("settingsSectionTitle");

        darkModeToggle = new Toggle(settingsPage, SVG.PALETTE.getContent(), "App theme", darkModeOn, this, 1, 0);
        darkModeToggle.setText("Light", "Dark");
        darkModeToggle.stateLabel.setPrefWidth(45);

        darkModeOn.addListener((observableValue, oldValue, newValue) -> {
            settingsPage.menuController.mainController.pref.preferences.putBoolean(DARK_MODE_ON, newValue);

            if(newValue){
                //TODO: switch to dark mode
            }
            else {
                //TODO: switch to light mode
            }
        });

        preventSleepToggle = new Toggle(settingsPage, SVG.SLEEP.getContent(), "Prevent sleep when media active", preventSleepOn, this, 1, 1);
        preventSleepOn.addListener((observableValue, oldValue, newValue) -> {
            settingsPage.menuController.mainController.pref.preferences.putBoolean(PREVENT_SLEEP_ON, newValue);
        });

        seekPreviewToggle = new Toggle(settingsPage, SVG.SETTINGS.getContent(), "Show frame preview above seekbar", seekPreviewOn, this, 1, 2);

        seekPreviewOn.addListener((observableValue, oldValue, newValue) -> {
            settingsPage.menuController.mainController.pref.preferences.putBoolean(SEEKBAR_FRAME_PREVIEW_ON, newValue);

            if(!newValue){
                settingsPage.menuController.mainController.sliderHoverBox.getChildren().remove(settingsPage.menuController.mainController.sliderHoverBox.imagePane);
            }
            else {
                if(settingsPage.menuController.queuePage.queueBox.activeItem.get() != null
                        && settingsPage.menuController.queuePage.queueBox.activeItem.get().getMediaItem() != null
                        && settingsPage.menuController.queuePage.queueBox.activeItem.get().getMediaItem().hasVideo()
                        && !settingsPage.menuController.mainController.sliderHoverBox.getChildren().contains(settingsPage.menuController.mainController.sliderHoverBox.imagePane)){
                    settingsPage.menuController.mainController.sliderHoverBox.getChildren().add(0, settingsPage.menuController.mainController.sliderHoverBox.imagePane);
                }
            }
        });

        recentMediaSizeItem = new ComboItem(settingsPage, SVG.SETTINGS.getContent(), "Recent media size", this, 1, 3);
        recentMediaSizeItem.customMenuButton.setContextWidth(150);
        recentMediaSizeItem.customMenuButton.setContextHeight(115);
        recentMediaSizeItem.customMenuButton.setScrollOff();
        recentMediaSizeItem.add("10");
        recentMediaSizeItem.add("25");
        recentMediaSizeItem.add("50");
        recentMediaSizeItem.add("100");


        recentMediaSizeItem.customMenuButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                if(settingsPage.menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) settingsPage.menuController.subtitlesController.closeSubtitles();
                if(settingsPage.menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) settingsPage.menuController.playbackSettingsController.closeSettings();
            }
        });

        recentMediaSizeItem.customMenuButton.setOnMouseClicked(e -> {
            if (settingsPage.menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED)
                settingsPage.menuController.subtitlesController.closeSubtitles();
            if (settingsPage.menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED)
                settingsPage.menuController.playbackSettingsController.closeSettings();
        });

        recentMediaSizeItem.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            recentMediaSizeProperty.set(Integer.parseInt(newValue));
        });

        recentMediaSizeProperty.addListener((observableValue, oldValue, newValue) -> {
            settingsPage.menuController.mainController.pref.preferences.putInt(HISTORY_SIZE, newValue.intValue());
        });

        this.getChildren().addAll(preferencesSectionTitle, darkModeToggle, preventSleepToggle, seekPreviewToggle, recentMediaSizeItem);

        VBox.setMargin(darkModeToggle, new Insets(5, 0, 0, 0));
        VBox.setMargin(preventSleepToggle, new Insets(10, 0, 0, 0));
        VBox.setMargin(seekPreviewToggle, new Insets(10, 0, 0, 0));
        VBox.setMargin(recentMediaSizeItem, new Insets(10, 0, 0, 0));

        focusNodes.add(darkModeToggle);
        focusNodes.add(preventSleepToggle);
        focusNodes.add(seekPreviewToggle);
        focusNodes.add(recentMediaSizeItem.customMenuButton);
    }

    public void loadPreferences(){
        Preferences preferences = settingsPage.menuController.mainController.pref.preferences;
        darkModeOn.set(preferences.getBoolean(DARK_MODE_ON, true));
        preventSleepOn.set(preferences.getBoolean(PREVENT_SLEEP_ON, true));
        seekPreviewOn.set(preferences.getBoolean(SEEKBAR_FRAME_PREVIEW_ON, true));
        recentMediaSizeItem.customMenuButton.setValue(String.valueOf(preferences.getInt(HISTORY_SIZE, 25)));
    }

    @Override
    public boolean focusForward(){

        if(focus.get() >= focusNodes.size() - 1)
            return true;

        keyboardFocusOn(focusNodes.get(focus.get() + 1));

        return false;
    }

    @Override
    public boolean focusBackward(){

        if(focus.get() == 0)
            return true;

        if(focus.get() < 0) keyboardFocusOn(focusNodes.get(focusNodes.size() - 1));
        else keyboardFocusOn(focusNodes.get(focus.get() - 1));

        return false;
    }

    @Override
    public void setFocus(int value){
        this.focus.set(value);
    }
}
