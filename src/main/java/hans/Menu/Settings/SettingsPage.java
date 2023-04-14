package hans.Menu.Settings;

import hans.Menu.MenuController;
import hans.Menu.MenuState;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class SettingsPage {

    MenuController menuController;

    VBox settingsWrapper = new VBox();
    VBox settingsBar = new VBox();
    ScrollPane settingsScroll = new ScrollPane();
    VBox settingsContent = new VBox();

    public Label settingsTitle = new Label("Settings");

    SubtitleSection subtitleSection;
    MetadataSection metadataSection;
    public PreferencesSection preferencesSection;
    LibrariesSection librariesSection;

    public SettingsPage(MenuController menuController){

        this.menuController = menuController;

        subtitleSection = new SubtitleSection(this);
        metadataSection = new MetadataSection(this);
        preferencesSection = new PreferencesSection(this);
        librariesSection = new LibrariesSection(this);

        settingsWrapper.setBackground(Background.EMPTY);

        settingsBar.setFillWidth(true);

        settingsScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        settingsScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        settingsScroll.getStyleClass().add("menuScroll");
        settingsScroll.setFitToWidth(true);
        settingsScroll.setFitToHeight(true);
        settingsScroll.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        settingsScroll.setBackground(Background.EMPTY);

        settingsContent.setBackground(Background.EMPTY);
        settingsContent.setPadding(new Insets(0, 50,20, 50));

        VBox.setVgrow(settingsScroll, Priority.ALWAYS);

        settingsTitle.getStyleClass().add("menuTitle");

        VBox.setMargin(settingsTitle, new Insets(20, 40, 5, 50));
        settingsBar.setPadding(new Insets(35, 0, 0, 0));

        settingsBar.setAlignment(Pos.CENTER_LEFT);
        settingsBar.getChildren().addAll(settingsTitle);


        settingsScroll.setContent(settingsContent);
        settingsScroll.addEventFilter(KeyEvent.ANY, e -> {
            if(e.getCode() == KeyCode.UP || e.getCode() == KeyCode.DOWN){
                e.consume();
            }
        });


        settingsWrapper.getChildren().addAll(settingsBar, settingsScroll);
        menuController.settingsContainer.getChildren().add(settingsWrapper);


        settingsContent.getChildren().addAll(subtitleSection, metadataSection, preferencesSection, librariesSection);
        settingsContent.setSpacing(30);

    }

    public void openSettingsPage(){
        menuController.settingsContainer.setVisible(true);
    }

    public void closeSettingsPage(){
        menuController.settingsContainer.setVisible(false);
    }

    public void enter(){

        if(menuController.menuInTransition) return;

        menuController.menuBar.setActiveButton(menuController.menuBar.settingsButton);

        if(menuController.menuState == MenuState.CLOSED){
            if(!menuController.extended.get()) menuController.setMenuExtended(MenuState.SETTINGS_OPEN);
            menuController.openMenu(MenuState.SETTINGS_OPEN);
        }
        else {
            if(!menuController.extended.get()) menuController.extendMenu(MenuState.SETTINGS_OPEN);
            else menuController.animateStateSwitch(MenuState.SETTINGS_OPEN);
        }
    }

    private void animateScroll(double newValue){

    }

    private void getTargetScrollValue(Section section){

    }
}
