package fxplayer.menu.Settings;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import fxplayer.AnimationsClass;
import fxplayer.menu.MenuController;
import fxplayer.menu.MenuState;
import fxplayer.menu.Settings.Libraries.LibrariesSection;

import java.util.ArrayList;
import java.util.List;

import static fxplayer.Utilities.keyboardFocusOn;

public class SettingsPage {

    public MenuController menuController;

    StackPane settingsWrapper = new StackPane();
    VBox settingsContainer = new VBox();
    VBox settingsBar = new VBox();
    public ScrollPane settingsScroll = new ScrollPane();
    VBox settingsContent = new VBox();

    StackPane titleContainer = new StackPane();

    public Label settingsTitle = new Label("Settings");

    public SubtitleSection subtitleSection;
    public PreferencesSection preferencesSection;
    LibrariesSection librariesSection;
    public ControlsSection controlsSection;
    AboutSection aboutSection;

    public IntegerProperty focus = new SimpleIntegerProperty(-1);
    List<SettingsSection> focusNodes = new ArrayList<>();

    public SettingsPage(MenuController menuController){

        this.menuController = menuController;


        settingsContainer.setBackground(Background.EMPTY);

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

        titleContainer.getChildren().addAll(settingsTitle);

        StackPane.setAlignment(settingsTitle, Pos.CENTER_LEFT);
        settingsTitle.getStyleClass().add("menuTitle");

        settingsBar.setPadding(new Insets(55, 50, 20, 50));
        settingsBar.setSpacing(10);
        settingsBar.setAlignment(Pos.CENTER_LEFT);
        settingsBar.getChildren().add(titleContainer);


        subtitleSection = new SubtitleSection(this);
        preferencesSection = new PreferencesSection(this);
        librariesSection = new LibrariesSection(this);
        controlsSection = new ControlsSection(this);
        aboutSection = new AboutSection(this);

        focusNodes.add(subtitleSection);
        focusNodes.add(preferencesSection);
        focusNodes.add(librariesSection);
        focusNodes.add(controlsSection);
        focusNodes.add(aboutSection);

        settingsScroll.setContent(settingsContent);
        settingsScroll.addEventFilter(KeyEvent.ANY, e -> {
            if(e.getCode() == KeyCode.UP || e.getCode() == KeyCode.DOWN){
                e.consume();
            }
        });


        settingsContainer.getChildren().addAll(settingsBar, settingsScroll);
        settingsWrapper.getChildren().add(settingsContainer);
        menuController.settingsContainer.getChildren().add(settingsWrapper);


        settingsContent.getChildren().addAll(subtitleSection, preferencesSection, librariesSection, controlsSection, aboutSection);
        settingsContent.setSpacing(30);

    }


    public void openSettingsPage(){
        menuController.settingsContainer.setVisible(true);
    }

    public void closeSettingsPage(){
        menuController.settingsContainer.setVisible(false);
        subtitleSection.languageItem.customMenuButton.scrollPane.setVvalue(0);
        preferencesSection.recentMediaSizeItem.customMenuButton.scrollPane.setVvalue(0);
    }

    public void enter(){

        if(menuController.menuInTransition) return;

        menuController.menuBar.setActiveButton(menuController.menuBar.settingsButton);
        settingsScroll.setVvalue(0);

        if(menuController.menuState == MenuState.CLOSED){
            if(!menuController.extended.get()) menuController.setMenuExtended(MenuState.SETTINGS_OPEN);
            menuController.openMenu(MenuState.SETTINGS_OPEN);
        }
        else {
            AnimationsClass.rotateTransition(200, menuController.menuBar.settingsButton.region, 0, 120, false, 1, true);

            if(!menuController.extended.get()) menuController.extendMenu(MenuState.SETTINGS_OPEN);
            else menuController.animateStateSwitch(MenuState.SETTINGS_OPEN);
        }
    }

    public void animateScroll(Section section){
        double scrollTo = getTargetScrollValue(section);

        Timeline scrollTimeline = new Timeline(new KeyFrame(Duration.millis(300),
                new KeyValue(settingsScroll.vvalueProperty(), scrollTo, Interpolator.EASE_BOTH)));

        scrollTimeline.playFromStart();
    }

    public double getTargetScrollValue(Section section){

        switch(section){
            case SUBTITLES -> {
                return 0;
            }
            case PREFERENCES -> {
                return calculateVvalue(preferencesSection);
            }
            case LIBRARIES -> {
                return calculateVvalue(librariesSection);
            }
            case CONTROLS -> {
                return calculateVvalue(controlsSection);
            }
            case ABOUT -> {
                return calculateVvalue(aboutSection);
            }
            default -> {
                return 1;
            }
        }
    }

    private double calculateVvalue(Node node){
            double heightViewPort = settingsScroll.getViewportBounds().getHeight();
            double heightScrollPane = settingsScroll.getContent().getBoundsInLocal().getHeight();
            double y = node.getBoundsInParent().getMinY();

            return (y/(heightScrollPane-heightViewPort));

    }

    public void loadPreferences(){
        subtitleSection.loadPreferences();
        preferencesSection.loadPreferences();
    }

    public void focusForward() {
        if(focus.get() < 0){
            boolean skipFocus = menuController.menuBar.focusForward();
            if(!skipFocus) return;

            focusNodes.get(0).focusForward();
        }
        else {
            if(focus.get() > focusNodes.size() - 1) {
                keyboardFocusOn(menuController.menuBar.focusNodes.get(0));
            }
            else {
                boolean skipFocus = focusNodes.get(focus.get()).focusForward();
                if(!skipFocus) return;

                if(focus.get() < focusNodes.size() - 1){
                    focusNodes.get(focus.get() + 1).focusForward();
                }
                else keyboardFocusOn(menuController.menuBar.focusNodes.get(0));
            }
        }
    }

    public void focusBackward() {
        if(focus.get() < 0){
            if(menuController.menuBar.focus.get() > 0) {
                menuController.menuBar.focusBackward();
            }
            else {
                focusNodes.get(focusNodes.size() - 1).focusBackward();
            }
        }
        else {
            if(focus.get() > focusNodes.size() - 1){
                focusNodes.get(focusNodes.size() - 1).focusBackward();
            }
            else {
                boolean skipFocus = focusNodes.get(focus.get()).focusBackward();
                if(!skipFocus) return;

                if(focus.get() > 0){
                    focusNodes.get(focus.get() - 1).focusBackward();
                }
                else keyboardFocusOn(menuController.menuBar.focusNodes.get(menuController.menuBar.focusNodes.size() - 1));
            }
        }
    }
}
