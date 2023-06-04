package tengy.Menu;


import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;
import tengy.Chapters.ChapterController;
import tengy.*;
import tengy.MediaItems.MediaItem;
import tengy.Menu.MediaInformation.MediaInformationPage;
import tengy.Menu.Queue.QueuePage;
import tengy.Menu.Settings.SettingsPage;
import tengy.PlaybackSettings.PlaybackSettingsController;
import tengy.PlaybackSettings.PlaybackSettingsState;
import tengy.Subtitles.SubtitlesController;
import tengy.Subtitles.SubtitlesState;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static tengy.Utilities.keyboardFocusOff;


public class MenuController implements Initializable {


    @FXML
    public
    StackPane menu, menuWrapper, menuContent, sideBar, dragPane, queueContainer, settingsContainer, recentMediaContainer, musicLibraryContainer, playlistsContainer, mediaInformationContainer, chapterContainer;

    public MainController mainController;
    public ControlBarController controlBarController;
    public PlaybackSettingsController playbackSettingsController;
    public SubtitlesController subtitlesController;
    public MediaInterface mediaInterface;

    public ChapterController chapterController;

    public MediaInformationPage mediaInformationPage;
    public QueuePage queuePage;
    public SettingsPage settingsPage;
    public RecentMediaPage recentMediaPage;
    public MusicLibraryPage musicLibraryPage;
    public PlaylistsPage playlistsPage;

    public MenuState menuState = MenuState.CLOSED;

    public boolean menuInTransition = false;

    final public double MIN_WIDTH = 500;

    DragResizer dragResizer;

    public ArrayList<MediaItem> ongoingMediaEditProcesses = new ArrayList<>();

    public MenuBar menuBar;

    public BooleanProperty extended = new SimpleBooleanProperty(false);

    double shrinkedWidth = MIN_WIDTH;

    SVGPath collapseSVG = new SVGPath();
    SVGPath extendSVG = new SVGPath();
    Region extendIcon = new Region();
    public FocusableMenuButton extendButton = new FocusableMenuButton();
    ControlTooltip extendTooltip;

    SVGPath closeSVG = new SVGPath();
    Region closeIcon = new Region();
    public FocusableMenuButton closeButton = new FocusableMenuButton();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        queuePage = new QueuePage(this);
        settingsPage = new SettingsPage(this);
        recentMediaPage = new RecentMediaPage(this);
        musicLibraryPage = new MusicLibraryPage(this);
        playlistsPage = new PlaylistsPage(this);
        mediaInformationPage = new MediaInformationPage(this);
        menuBar = new MenuBar(this, sideBar);


        menu.setBackground(Background.EMPTY);

        menu.setPrefWidth(500);
        menu.setMaxWidth(500);
        menu.setViewOrder(1);
        menu.setId("menu");

        menu.widthProperty().addListener((observableValue, oldValue, newValue) -> {
            if(!extended.get()) return;

            if(oldValue.doubleValue() < 1200 && newValue.doubleValue() >= 1200) menuBar.extend();
            else if(oldValue.doubleValue() >= 1200 && newValue.doubleValue() < 1200) menuBar.shrink();
        });

        menu.setTranslateX(-500);

        menu.setMouseTransparent(true);
        Rectangle menuClip = new Rectangle();
        menuClip.widthProperty().bind(menu.widthProperty());
        menuClip.heightProperty().bind(menu.heightProperty());
        menu.setClip(menuClip);

        menu.setOnMouseClicked(e -> {
            if(subtitlesController.subtitlesState != SubtitlesState.CLOSED) subtitlesController.closeSubtitles();
            if(playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) playbackSettingsController.closeSettings();

            menu.requestFocus();
        });


        closeSVG.setContent(SVG.CLOSE.getContent());
        closeIcon.setShape(closeSVG);
        closeIcon.setPrefSize(20, 20);
        closeIcon.setMaxSize(20, 20);
        closeIcon.getStyleClass().add("graphic");

        closeButton.setPrefSize(40, 40);
        closeButton.setMaxSize(40, 40);
        closeButton.getStyleClass().addAll("transparentButton", "primaryMenuButton");
        closeButton.setGraphic(closeIcon);
        closeButton.setOnAction(e -> {

            if(subtitlesController.subtitlesState != SubtitlesState.CLOSED) subtitlesController.closeSubtitles();
            if(playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) playbackSettingsController.closeSettings();

            closeButton.requestFocus();

            closeMenu();
        });

        closeButton.visibleProperty().bind(extended);
        closeButton.mouseTransparentProperty().bind(extended.not());
        closeButton.visibleProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                if(!menuBar.focusNodes.contains(closeButton))
                    if(menuBar.focusNodes.contains(extendButton)){
                        menuBar.focusNodes.add(1, closeButton);
                        if(menuBar.focus.get() >= 1) menuBar.focus.set(menuBar.focus.get() + 1);
                    }
                    else {
                        menuBar.focusNodes.add(0, closeButton);
                        if(menuBar.focus.get() >= 0) menuBar.focus.set(menuBar.focus.get() + 1);
                    }
            }
            else {
                boolean removed = menuBar.focusNodes.remove(closeButton);
                if(menuBar.focusNodes.contains(extendButton)) {
                    if(removed && menuBar.focus.get() > 1) menuBar.focus.set(menuBar.focus.get() - 1);
                }
                else {
                    if(removed && menuBar.focus.get() > 0) menuBar.focus.set(menuBar.focus.get() - 1);
                }
            }
        });

        closeButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                menuBar.focus.set(menuBar.focusNodes.indexOf(closeButton));
            }
            else{
                keyboardFocusOff(closeButton);
                menuBar.focus.set(-1);
            }
        });

        closeButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            closeButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        closeButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            closeButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });

        StackPane.setAlignment(closeButton, Pos.TOP_RIGHT);
        StackPane.setMargin(closeButton, new Insets(10, 10 , 0, 0));

        collapseSVG.setContent(SVG.CHEVRON_LEFT.getContent());
        extendSVG.setContent(SVG.CHEVRON_RIGHT.getContent());
        extendIcon.setShape(extendSVG);
        extendIcon.setPrefSize(14, 20);
        extendIcon.setMaxSize(14, 20);
        extendIcon.getStyleClass().add("graphic");

        extendButton.setPrefSize(40, 40);
        extendButton.setMaxSize(40, 40);
        extendButton.getStyleClass().addAll("transparentButton", "primaryMenuButton");
        extendButton.setGraphic(extendIcon);
        extendButton.setVisible(false);
        extendButton.visibleProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                if(!menuBar.focusNodes.contains(extendButton))
                    menuBar.focusNodes.add(0, extendButton);
                    if(menuBar.focus.get() >= 0) menuBar.focus.set(menuBar.focus.get() + 1);
            }
            else {
                boolean removed = menuBar.focusNodes.remove(extendButton);
                if(removed && menuBar.focus.get() > 0) menuBar.focus.set(menuBar.focus.get() - 1);
            }
        });

        extendButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                menuBar.focus.set(0);
            }
            else{
                keyboardFocusOff(extendButton);
                menuBar.focus.set(-1);
            }
        });

        extendButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            extendButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        extendButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            extendButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });

        extendButton.setOnAction(e -> {
            if(subtitlesController.subtitlesState != SubtitlesState.CLOSED) subtitlesController.closeSubtitles();
            if(playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) playbackSettingsController.closeSettings();

            extendButton.requestFocus();

            extendMenu(menuState);
        });


        Platform.runLater(() -> extendTooltip = new ControlTooltip(mainController, "Extend menu", "", extendButton, 1000, TooltipType.MENU_TOOLTIP));

        StackPane.setAlignment(extendButton, Pos.TOP_RIGHT);
        StackPane.setMargin(extendButton, new Insets(5, 5 , 0, 0));

        menuWrapper.setId("menuWrapper");

        menuContent.getChildren().addAll(closeButton, extendButton);


        dragPane.setCursor(Cursor.W_RESIZE);
        dragResizer = new DragResizer(this);

        queueContainer.setVisible(false);
        queueContainer.setBackground(Background.EMPTY);

        settingsContainer.setVisible(false);
        settingsContainer.setBackground(Background.EMPTY);

        recentMediaContainer.setVisible(false);
        recentMediaContainer.setBackground(Background.EMPTY);

        musicLibraryContainer.setVisible(false);
        musicLibraryContainer.setBackground(Background.EMPTY);

        playlistsContainer.setVisible(false);
        playlistsContainer.setBackground(Background.EMPTY);

        mediaInformationContainer.setVisible(false);
        mediaInformationContainer.setBackground(Background.EMPTY);

        chapterContainer.setVisible(false);
        chapterContainer.setBackground(Background.EMPTY);
    }

    public void openMenu(MenuState newState) {

        if(        menuInTransition
                || controlBarController.durationSlider.isValueChanging()
                || controlBarController.volumeSlider.isValueChanging()
                || playbackSettingsController.playbackSpeedController.customSpeedPane.customSpeedSlider.isValueChanging()
                || subtitlesController.subtitlesBox.subtitlesDragActive
                || playbackSettingsController.equalizerController.sliderActive) return;

        if(newState != menuState) updateState(newState);

        menuInTransition = true;


        if(playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) playbackSettingsController.closeSettings();
        if (subtitlesController.subtitlesState != SubtitlesState.CLOSED) subtitlesController.closeSubtitles();

        if(controlBarController.titleShowing)
            controlBarController.hideTitle();

        mainController.videoImageView.getScene().setCursor(Cursor.DEFAULT);

        if(extended.get()){
            if(!controlBarController.controlBarShowing) controlBarController.showControls();
            openExtendedMenu();
        }
        else {
            if(controlBarController.controlBarShowing) controlBarController.hideControls();
            openShrinkedMenu();
        }
    }

    public void closeMenu(){

        if(menuInTransition) return;

        mainController.videoImageView.requestFocus();

        if(dragResizer.dragging) {
            dragResizer.dragging = false;
        }

        menuInTransition = true;
        menu.setMouseTransparent(true);

        if(extended.get()) closeExtendedMenu();
        else closeShrinkedMenu();
    }


    public void init(MainController mainController, ControlBarController controlBarController, PlaybackSettingsController playbackSettingsController, MediaInterface mediaInterface, SubtitlesController subtitlesController, ChapterController chapterController){
        this.mainController = mainController;
        this.controlBarController = controlBarController;
        this.playbackSettingsController = playbackSettingsController;
        this.mediaInterface = mediaInterface;
        this.subtitlesController = subtitlesController;
        this.chapterController = chapterController;


        settingsPage.subtitleSection.loadLanguageBox();
    }

    public void extendMenu(MenuState newState){
        if(extended.get() || menuInTransition || menuState == MenuState.CLOSED) return;

        menu.setMouseTransparent(true);

        menuInTransition = true;

        Duration animationDuration = Duration.millis(300);
        Timeline maxTimeline = new Timeline(new KeyFrame(animationDuration,
                new KeyValue(menu.maxWidthProperty(), mainController.videoImageViewWrapper.getWidth() + 15, Interpolator.EASE_BOTH)));

        Timeline prefTimeline = new Timeline(new KeyFrame(animationDuration,
                new KeyValue(menu.prefWidthProperty(), mainController.videoImageViewWrapper.getWidth() + 15, Interpolator.EASE_BOTH)));

        FadeTransition menuContentFade = new FadeTransition(animationDuration, menuContent);
        menuContentFade.setFromValue(menuContent.getOpacity());
        menuContentFade.setToValue(0);

        ParallelTransition parallelWidth = new ParallelTransition(maxTimeline, prefTimeline);


        SequentialTransition sequentialTransition = new SequentialTransition(menuContentFade, parallelWidth);

        sequentialTransition.setOnFinished(e -> {
            setMenuExtended(newState);
            menu.setMouseTransparent(false);
            FadeTransition fadeTransition = new FadeTransition(Duration.millis(300), menuContent);
            fadeTransition.setFromValue(menuContent.getOpacity());
            fadeTransition.setToValue(1);
            fadeTransition.setOnFinished(ev -> {
                menuInTransition = false;
            });
            fadeTransition.play();
        });

        sequentialTransition.playFromStart();
    }

    public void shrinkMenu(){
        if(!extended.get() || menuInTransition || menuState == MenuState.CLOSED) return;

        menu.setMouseTransparent(true);
        menuInTransition = true;

        menu.maxWidthProperty().unbind();
        menu.prefWidthProperty().unbind();

        menu.setPrefWidth(mainController.videoImageViewWrapper.getWidth() + 15);
        menu.setMaxWidth(mainController.videoImageViewWrapper.getWidth() + 15);

        shrinkedWidth = Math.max(MIN_WIDTH, Math.min(shrinkedWidth, (mainController.videoImageViewWrapper.getWidth() + 30)/2));


        Duration animationDuration = Duration.millis(300);
        Timeline maxTimeline = new Timeline(new KeyFrame(animationDuration,
                new KeyValue(menu.maxWidthProperty(), shrinkedWidth, Interpolator.EASE_BOTH)));

        Timeline prefTimeline = new Timeline(new KeyFrame(animationDuration,
                new KeyValue(menu.prefWidthProperty(), shrinkedWidth, Interpolator.EASE_BOTH)));

        ParallelTransition parallelWidth = new ParallelTransition(maxTimeline, prefTimeline);

        parallelWidth.setOnFinished(e -> {

            extended.set(false);

            StackPane.setMargin(extendButton, new Insets(5, 5, 0, 0));

            extendButton.setOnAction(ev -> {
                if(subtitlesController.subtitlesState != SubtitlesState.CLOSED) subtitlesController.closeSubtitles();
                if(playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) playbackSettingsController.closeSettings();
                extendButton.requestFocus();
                extendMenu(menuState);
            });
            extendIcon.setShape(extendSVG);
            extendTooltip.updateActionText("Extend menu");
            extendButton.setVisible(true);
            extendButton.setMouseTransparent(false);

            mainController.sliderHoverBox.setBackground(false);

            queuePage.shrink();
            chapterController.chapterPage.shrink();
            menuBar.shrink();

            StackPane.setMargin(queuePage.scrollUpButtonContainer, new Insets(130, 0, 0, 0));


            shrinkedWidth = Math.max(MIN_WIDTH, Math.min(shrinkedWidth, (mainController.videoImageViewWrapper.getWidth() + 30)/2));
            menu.setPrefWidth(shrinkedWidth);
            menu.setMaxWidth(shrinkedWidth);

            FadeTransition fadeTransition = new FadeTransition(Duration.millis(300), menuContent);
            fadeTransition.setFromValue(menuContent.getOpacity());
            fadeTransition.setToValue(1);
            fadeTransition.setOnFinished(ev -> {
                menuInTransition = false;
                menu.setMouseTransparent(false);
            });
            fadeTransition.play();
        });

        FadeTransition menuContentFade = new FadeTransition(animationDuration, menuContent);
        menuContentFade.setFromValue(menuContent.getOpacity());
        menuContentFade.setToValue(0);
        menuContentFade.setOnFinished(e -> {

            controlBarController.controlBarWrapper.setViewOrder(2);
            menu.setViewOrder(1);

            StackPane.setMargin(menuWrapper, new Insets(0, 5, 0, 0));

            dragPane.setMouseTransparent(false);
            dragPane.setVisible(true);

            menuWrapper.setStyle("-fx-border-color: #909090;");
            menuWrapper.setPadding(Insets.EMPTY);

            parallelWidth.playFromStart();
        });

        menuContentFade.playFromStart();
        if(controlBarController.titleShowing) controlBarController.hideTitle();
        if(controlBarController.controlBarShowing) controlBarController.hideControls();
    }

    public void setMenuExtended(MenuState newState){

        extended.set(true);

        if(newState != menuState) updateState(newState);

        StackPane.setMargin(extendButton, new Insets(10, 60, 0, 0));

        extendButton.setOnAction(e -> {
            if(subtitlesController.subtitlesState != SubtitlesState.CLOSED) subtitlesController.closeSubtitles();
            if(playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) playbackSettingsController.closeSettings();

            extendButton.requestFocus();
            shrinkMenu();
        });
        extendIcon.setShape(collapseSVG);
        extendTooltip.updateActionText("Collapse menu");
        if(newState == MenuState.QUEUE_OPEN || newState == MenuState.CHAPTERS_OPEN){
            extendButton.setVisible(true);
            extendButton.setMouseTransparent(false);
        }
        else {
            extendButton.setVisible(false);
            extendButton.setMouseTransparent(true);
        }

        mainController.sliderHoverBox.setBackground(true);


        menu.setTranslateX(0);
        if(menuState == MenuState.CLOSED) menu.setOpacity(0);
        menu.maxWidthProperty().bind(mainController.videoImageViewWrapper.widthProperty());
        menu.prefWidthProperty().bind(mainController.videoImageViewWrapper.widthProperty());

        dragPane.setMouseTransparent(true);
        dragPane.setVisible(false);
        StackPane.setMargin(menuWrapper, Insets.EMPTY);
        menuWrapper.setStyle("-fx-border-color: transparent;");

        if(mainController.videoImageViewWrapper.getWidth() >= 1200) {
            menuBar.extend();
        }

        queuePage.extend();
        chapterController.chapterPage.extend();

        controlBarController.controlBarWrapper.setViewOrder(1);
        menu.setViewOrder(2);

        menuWrapper.setPadding(new Insets(0, 0, 65, 0));

        StackPane.setMargin(queuePage.scrollUpButtonContainer, new Insets(190, 0, 0, 0));


        if(menuState != MenuState.CLOSED){
            if(!controlBarController.controlBarShowing) controlBarController.showControls();
        }
    }

    public void setMenuShrinked(){

        if(!extended.get()) return;

        extended.set(false);

        StackPane.setMargin(extendButton, new Insets(5, 5, 0, 0));

        extendButton.setOnAction(e -> extendMenu(menuState));
        extendIcon.setShape(extendSVG);
        extendTooltip.updateActionText("Extend menu");
        extendButton.setVisible(true);
        extendButton.setMouseTransparent(false);

        mainController.sliderHoverBox.setBackground(false);

        menu.setOpacity(1);
        if(menuState == MenuState.CLOSED) menu.setTranslateX(-menu.getWidth());

        StackPane.setMargin(menuWrapper, new Insets(0, 5, 0, 0));
        menuWrapper.setPadding(Insets.EMPTY);

        controlBarController.controlBarWrapper.setViewOrder(2);
        menu.setViewOrder(1);

        queuePage.shrink();
        menuBar.shrink();
        chapterController.chapterPage.shrink();

        menu.maxWidthProperty().unbind();
        menu.prefWidthProperty().unbind();

        shrinkedWidth = Math.max(MIN_WIDTH, Math.min(shrinkedWidth, (mainController.videoImageViewWrapper.getWidth() + 30)/2));
        menu.setPrefWidth(shrinkedWidth);
        menu.setMaxWidth(shrinkedWidth);

        dragPane.setMouseTransparent(false);
        dragPane.setVisible(true);
        menuWrapper.setStyle("-fx-border-color: #909090;");

        StackPane.setMargin(queuePage.scrollUpButtonContainer, new Insets(130, 0, 0, 0));

        if(controlBarController.controlBarShowing){
            controlBarController.hideControls();
        }
    }

    private void openExtendedMenu(){
        Duration animationDuration = Duration.millis(300);

        FadeTransition menuFade = new FadeTransition(animationDuration, menu);
        menuFade.setFromValue(menu.getOpacity());
        menuFade.setToValue(1);

        menuFade.setOnFinished(e -> {
            menu.setMouseTransparent(false);
            menuInTransition = false;

            mainController.sliderHoverBox.setBackground(true);
        });

        menuFade.play();
    }

    private void openShrinkedMenu(){
        TranslateTransition openMenu = new TranslateTransition(Duration.millis(300), menu);
        openMenu.setFromX(menu.getTranslateX());
        openMenu.setToX(0);
        openMenu.setInterpolator(Interpolator.EASE_OUT);

        openMenu.setOnFinished((e) -> {
            menu.setMouseTransparent(false);
            menuInTransition = false;
        });

        openMenu.play();
    }

    private void closeExtendedMenu(){

        Duration animationDuration = Duration.millis(300);

        FadeTransition menuFade = new FadeTransition(animationDuration, menu);
        menuFade.setFromValue(menu.getOpacity());
        menuFade.setToValue(0);

        menuFade.setOnFinished(e -> {
            menuInTransition = false;

            mainController.sliderHoverBox.setBackground(false);

            updateState(MenuState.CLOSED);

            if(!controlBarController.controlBarShowing) controlBarController.showControls();
            if(!controlBarController.titleShowing) controlBarController.showTitle();
            controlBarController.mouseEventTracker.move();
        });

        menuFade.play();
    }

    private void closeShrinkedMenu(){
        TranslateTransition closeMenu = new TranslateTransition(Duration.millis(300), menu);
        closeMenu.setFromX(menu.getTranslateX());
        closeMenu.setToX(-menu.getWidth());

        closeMenu.setOnFinished((e) -> {
            menuInTransition = false;

            updateState(MenuState.CLOSED);

            if(!controlBarController.controlBarShowing) controlBarController.showControls();
            if(!controlBarController.titleShowing) controlBarController.showTitle();
            controlBarController.mouseEventTracker.move();
        });

        closeMenu.play();
    }

    public void animateStateSwitch(MenuState newState){
        menuInTransition = true;

        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), menuContent);
        fadeOut.setFromValue(menuContent.getOpacity());
        fadeOut.setToValue(0);


        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), menuContent);
        fadeIn.setFromValue(menuContent.getOpacity());
        fadeIn.setToValue(1);
        fadeIn.setOnFinished(e -> menuInTransition = false);

        fadeOut.setOnFinished(e -> {
            updateState(newState);
            fadeIn.play();
        });

        fadeOut.play();
    }


    private void updateState(MenuState newState){
        MenuState oldState = this.menuState;

        this.menuState = newState;

        if(newState == MenuState.QUEUE_OPEN || newState == MenuState.CHAPTERS_OPEN){
            extendButton.setVisible(true);
            extendButton.setMouseTransparent(false);
        }
        else {
            extendButton.setVisible(false);
            extendButton.setMouseTransparent(true);
        }

        switch (oldState){
            case QUEUE_OPEN -> queuePage.closeQueuePage();
            case CHAPTERS_OPEN -> chapterController.chapterPage.closeChaptersPage();
            case SETTINGS_OPEN -> settingsPage.closeSettingsPage();
            case PLAYLISTS_OPEN -> playlistsPage.closePlaylistsPage();
            case RECENT_MEDIA_OPEN -> recentMediaPage.closeRecentMediaPage();
            case MUSIC_LIBRARY_OPEN -> musicLibraryPage.closeMusicLibraryPage();
        }

        switch (newState){
            case QUEUE_OPEN -> queuePage.openQueuePage();
            case CHAPTERS_OPEN -> chapterController.chapterPage.openChaptersPage();
            case SETTINGS_OPEN -> settingsPage.openSettingsPage();
            case PLAYLISTS_OPEN -> playlistsPage.openPlaylistsPage();
            case RECENT_MEDIA_OPEN -> recentMediaPage.openRecentMediaPage();
            case MUSIC_LIBRARY_OPEN -> musicLibraryPage.openMusicLibraryPage();
        }
    }


    public void handleFocusForward() {

        switch(menuState){
            case QUEUE_OPEN -> queuePage.focusForward();
            case CHAPTERS_OPEN -> chapterController.chapterPage.focusForward();
            case SETTINGS_OPEN -> settingsPage.focusForward();
            case PLAYLISTS_OPEN -> playlistsPage.focusForward();
            case RECENT_MEDIA_OPEN -> recentMediaPage.focusForward();
            case MUSIC_LIBRARY_OPEN -> musicLibraryPage.focusForward();
        }
    }

    public void handleFocusBackward() {

        switch(menuState){
            case QUEUE_OPEN -> queuePage.focusBackward();
            case CHAPTERS_OPEN -> chapterController.chapterPage.focusBackward();
            case SETTINGS_OPEN -> settingsPage.focusBackward();
            case PLAYLISTS_OPEN -> playlistsPage.focusBackward();
            case RECENT_MEDIA_OPEN -> recentMediaPage.focusBackward();
            case MUSIC_LIBRARY_OPEN -> musicLibraryPage.focusBackward();
        }
    }
}



