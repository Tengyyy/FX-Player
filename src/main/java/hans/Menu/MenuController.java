package hans.Menu;


import hans.*;
import hans.Captions.CaptionsController;
import hans.Captions.CaptionsState;
import hans.Chapters.ChapterController;
import hans.MediaItems.MediaItem;
import hans.Menu.MetadataEdit.MetadataEditPage;
import hans.Menu.Queue.QueuePage;
import hans.Settings.SettingsController;
import hans.Settings.SettingsState;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;




public class MenuController implements Initializable {


    @FXML
    public
    StackPane menu, menuWrapper, menuContent, sideBar, dragPane, queueContainer;

    Region dragIcon = new Region();

    @FXML
    public ScrollPane metadataEditScroll, technicalDetailsScroll, chapterScroll;

    public MainController mainController;
    public ControlBarController controlBarController;
    public SettingsController settingsController;
    public CaptionsController captionsController;
    public MediaInterface mediaInterface;

    public QueuePage queuePage;

    public ChapterController chapterController;

    public MetadataEditPage metadataEditPage;
    public TechnicalDetailsPage technicalDetailsPage;

    public MenuState menuState = MenuState.CLOSED;

    public boolean menuInTransition = false;

    final public double MIN_WIDTH = 500;

    DragResizer dragResizer;

    public ArrayList<MediaItem> ongoingMetadataEditProcesses = new ArrayList<>();

    MenuBar menuBar;

    public boolean extended = false;

    double shrinkedWidth = MIN_WIDTH;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        queuePage = new QueuePage(this);
        metadataEditPage = new MetadataEditPage(this);
        technicalDetailsPage = new TechnicalDetailsPage(this);
        menuBar = new MenuBar(this, sideBar);


        menu.setBackground(Background.EMPTY);

        menu.setPrefWidth(500);
        menu.setMaxWidth(500);
        menu.setViewOrder(1);
        menu.setId("menu");

        menu.widthProperty().addListener((observableValue, oldValue, newValue) -> {
            if(!extended) return;

            if(oldValue.doubleValue() < 1200 && newValue.doubleValue() >= 1200){
                StackPane.setMargin(menuContent, new Insets(0, 0, 0, 300));
                menuBar.extend();
            }
            else if(oldValue.doubleValue() >= 1200 && newValue.doubleValue() < 1200){
                StackPane.setMargin(menuContent, new Insets(0, 0, 0, 50));
                menuBar.shrink();
            }
        });

        menu.setTranslateX(-500);

        menu.setMouseTransparent(true);
        Rectangle menuClip = new Rectangle();
        menuClip.widthProperty().bind(menu.widthProperty());
        menuClip.heightProperty().bind(menu.heightProperty());
        menu.setClip(menuClip);

        menu.setOnMouseClicked(e -> {
            if(extended){
                if(captionsController.captionsState != CaptionsState.CLOSED) captionsController.closeCaptions();
                if(settingsController.settingsState != SettingsState.CLOSED) settingsController.closeSettings();
            }
        });

        menuWrapper.setId("menuWrapper");

        dragPane.setId("dragPane");
        dragPane.getChildren().add(dragIcon);
        dragPane.setCursor(Cursor.W_RESIZE);

        SVGPath dragSVG = new SVGPath();
        dragSVG.setContent(App.svgMap.get(SVG.DRAG_VERTICAL));
        dragIcon.setShape(dragSVG);
        dragIcon.getStyleClass().add("menuIcon2");
        dragIcon.setMouseTransparent(true);
        dragIcon.setPrefSize(5, 15);
        dragIcon.setMaxSize(5, 15);

        dragResizer = new DragResizer(this);

        metadataEditScroll.setVisible(false);
        metadataEditScroll.setBackground(Background.EMPTY);

        technicalDetailsScroll.setVisible(false);
        technicalDetailsScroll.setBackground(Background.EMPTY);

        chapterScroll.setVisible(false);
        chapterScroll.setBackground(Background.EMPTY);
    }

    public void openMenu() {

        if(        menuInTransition
                || controlBarController.durationSlider.isValueChanging()
                || controlBarController.volumeSlider.isValueChanging()
                || settingsController.playbackSpeedController.customSpeedPane.customSpeedSlider.isValueChanging()
                || captionsController.captionsBox.captionsDragActive
                || settingsController.equalizerController.sliderActive) return;

        queuePage.queueBox.requestFocus();

        menuInTransition = true;
        menuState = MenuState.QUEUE_OPEN;

        if(queuePage.queueBox.activeItem.get() != null){
            double heightViewPort = queuePage.queueScroll.getViewportBounds().getHeight();
            double heightScrollPane = queuePage.queueScroll.getContent().getBoundsInLocal().getHeight();
            double y = queuePage.queueBox.activeItem.get().getBoundsInParent().getMaxY();
            if (y<(heightViewPort/2)){
                queuePage.queueScroll.setVvalue(0);
            }
            else if ((y>=(heightViewPort/2))&(y<=(heightScrollPane-heightViewPort/2))){
                queuePage.queueScroll.setVvalue((y-(heightViewPort/2))/(heightScrollPane-heightViewPort));
            }
            else if( y>= (heightScrollPane-(heightViewPort/2))){
                queuePage.queueScroll.setVvalue(1);
            }
        }

        if(settingsController.settingsState != SettingsState.CLOSED) settingsController.closeSettings();
        if (captionsController.captionsState != CaptionsState.CLOSED) captionsController.closeCaptions();

        if(!extended){
            controlBarController.controlBarWrapper.setMouseTransparent(true);

            if(controlBarController.controlBarOpen) AnimationsClass.hideControlsAndTitle(controlBarController, captionsController, mainController);
        }
        else if(controlBarController.controlBarOpen) AnimationsClass.hideTitle(mainController);

        mainController.videoImageViewWrapper.getScene().setCursor(Cursor.DEFAULT);

        if(extended) openExtendedMenu();
        else openShrinkedMenu();

        captionsController.captionsBox.captionsContainer.setMouseTransparent(true);


    }

    public void closeMenu(){

        if(menuInTransition) return;

        mainController.videoImageView.requestFocus();

        if(dragResizer.dragging) {
            dragResizer.dragging = false;
        }

        menuInTransition = true;
        menuState = MenuState.CLOSED;
        menu.setMouseTransparent(true);

        if(extended) closeExtendedMenu();
        else closeShrinkedMenu();

        controlBarController.mouseEventTracker.move();

        AnimationsClass.displayTitle(mainController);
        mainController.videoTitleLabel.getScene().setCursor(Cursor.DEFAULT);
        mainController.videoTitleBox.setMouseTransparent(false);
        if(captionsController.captionsSelected.get()) captionsController.captionsBox.captionsContainer.setMouseTransparent(false);
    }


    public void init(MainController mainController, ControlBarController controlBarController, SettingsController settingsController, MediaInterface mediaInterface, CaptionsController captionsController, ChapterController chapterController){
        this.mainController = mainController;
        this.controlBarController = controlBarController;
        this.settingsController = settingsController;
        this.mediaInterface = mediaInterface;
        this.captionsController = captionsController;
        this.chapterController = chapterController;
    }

    public void extendMenu(){
        if(extended || menuInTransition || menuState == MenuState.CLOSED) return;

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
            setMenuExtended();
            menu.setMouseTransparent(false);
            FadeTransition fadeTransition = new FadeTransition(Duration.millis(300), menuContent);
            fadeTransition.setFromValue(menuContent.getOpacity());
            fadeTransition.setToValue(1);
            fadeTransition.play();
        });

        sequentialTransition.playFromStart();
    }

    public void shrinkMenu(){
        if(!extended || menuInTransition || menuState == MenuState.CLOSED) return;

        menu.setMouseTransparent(true);
        menuInTransition = true;

        menu.maxWidthProperty().unbind();
        menu.prefWidthProperty().unbind();

        menu.setPrefWidth(mainController.videoImageViewWrapper.getWidth() + 15);
        menu.setMaxWidth(mainController.videoImageViewWrapper.getWidth() + 15);

        menuBar.shrink();
        StackPane.setMargin(menuContent, new Insets(0, 0, 0, 50));
        StackPane.setMargin(menuWrapper, new Insets(0, 15, 0, 0));
        StackPane.setMargin(menu, Insets.EMPTY);

        controlBarController.controlBarWrapper.setStyle("-fx-background-color: transparent;");

        dragPane.setMouseTransparent(false);
        dragPane.setVisible(true);

        controlBarController.controlBarWrapper.setViewOrder(2);
        menu.setViewOrder(1);

        menuWrapper.setStyle("-fx-border-color: #909090;");

        shrinkedWidth = Math.max(MIN_WIDTH, Math.min(shrinkedWidth, (mainController.videoImageViewWrapper.getWidth() + 30)/2));


        Duration animationDuration = Duration.millis(300);
        Timeline maxTimeline = new Timeline(new KeyFrame(animationDuration,
                new KeyValue(menu.maxWidthProperty(), shrinkedWidth, Interpolator.EASE_BOTH)));

        Timeline prefTimeline = new Timeline(new KeyFrame(animationDuration,
                new KeyValue(menu.prefWidthProperty(), shrinkedWidth, Interpolator.EASE_BOTH)));

        FadeTransition menuContentFade = new FadeTransition(animationDuration, menuContent);
        menuContentFade.setFromValue(menuContent.getOpacity());
        menuContentFade.setToValue(0);

        ParallelTransition parallelWidth = new ParallelTransition(maxTimeline, prefTimeline);


        SequentialTransition sequentialTransition = new SequentialTransition(menuContentFade, parallelWidth);

        sequentialTransition.setOnFinished(e -> {
            setMenuShrinked();
            menu.setMouseTransparent(false);
            FadeTransition fadeTransition = new FadeTransition(Duration.millis(300), menuContent);
            fadeTransition.setFromValue(menuContent.getOpacity());
            fadeTransition.setToValue(1);
            fadeTransition.play();
        });

        sequentialTransition.playFromStart();
        AnimationsClass.hideControlsAndTitle(controlBarController, captionsController, mainController);
    }

    private void setMenuExtended(){
        menuInTransition = false;
        extended = true;

        queuePage.collapseButton.setVisible(true);
        queuePage.collapseButton.setMouseTransparent(false);

        menu.setTranslateX(0);
        if(menuState == MenuState.CLOSED) menu.setOpacity(0);
        menu.maxWidthProperty().bind(mainController.videoImageViewWrapper.widthProperty());
        menu.prefWidthProperty().bind(mainController.videoImageViewWrapper.widthProperty());

        dragPane.setMouseTransparent(true);
        dragPane.setVisible(false);
        StackPane.setMargin(menuWrapper, Insets.EMPTY);
        menuWrapper.setStyle("-fx-border-color: transparent;");

        if(mainController.videoImageViewWrapper.getWidth() < 1200) StackPane.setMargin(menuContent, new Insets(0, 0, 0, 50));
        else {
            StackPane.setMargin(menuContent, new Insets(0, 0, 0, 300));
            menuBar.extend();
        }

        queuePage.extend();

        controlBarController.controlBarWrapper.setMouseTransparent(false);
        controlBarController.controlBarWrapper.setViewOrder(1);
        menu.setViewOrder(2);

        StackPane.setMargin(menu, new Insets(0, 0, controlBarController.controlBarWrapper.getHeight() - 6, 0));
        if(menuState != MenuState.CLOSED){
            controlBarController.controlBarWrapper.setStyle("-fx-background-color: rgba(0,0,0,0.8); -fx-background-insets: 6 0 0 0;");
            AnimationsClass.displayControls(controlBarController, captionsController, mainController);
        }
    }

    private void setMenuShrinked(){

        menuInTransition = false;
        extended = false;

        queuePage.collapseButton.setVisible(false);
        queuePage.collapseButton.setMouseTransparent(true);

        menu.setOpacity(1);
        if(menuState == MenuState.CLOSED) menu.setTranslateX(-menu.getWidth());

        StackPane.setMargin(menuContent, new Insets(0, 0, 0, 50));
        StackPane.setMargin(menuWrapper, new Insets(0, 15, 0, 0));
        StackPane.setMargin(menu, Insets.EMPTY);

        controlBarController.controlBarWrapper.setStyle("-fx-background-color: transparent;");

        controlBarController.controlBarWrapper.setViewOrder(2);
        menu.setViewOrder(1);

        queuePage.shrink();
        menuBar.shrink();

        menu.maxWidthProperty().unbind();
        menu.prefWidthProperty().unbind();

        shrinkedWidth = Math.max(MIN_WIDTH, Math.min(shrinkedWidth, (mainController.videoImageViewWrapper.getWidth() + 30)/2));
        menu.setPrefWidth(shrinkedWidth);
        menu.setMaxWidth(shrinkedWidth);

        dragPane.setMouseTransparent(false);
        dragPane.setVisible(true);
        menuWrapper.setStyle("-fx-border-color: #909090;");

        if(controlBarController.controlBarOpen) AnimationsClass.hideControlsAndTitle(controlBarController, captionsController, mainController);
    }

    private void openExtendedMenu(){
        Duration animationDuration = Duration.millis(300);

        Rectangle rect = new Rectangle();
        rect.setFill(Color.rgb(0,0,0,0));

        FillTransition controlBarFade = new FillTransition();
        controlBarFade.setShape(rect);
        controlBarFade.setDuration(animationDuration);
        controlBarFade.setFromValue(Color.rgb(0,0,0,0));
        controlBarFade.setToValue(Color.rgb(0,0,0,0.8));

        controlBarFade.setInterpolator(new Interpolator() {
            @Override
            protected double curve(double t) {
                controlBarController.controlBarWrapper.setBackground(new Background(new BackgroundFill(rect.getFill(), CornerRadii.EMPTY, Insets.EMPTY)));
                return t;
            }
        });

        FadeTransition menuFade = new FadeTransition(animationDuration, menu);
        menuFade.setFromValue(menu.getOpacity());
        menuFade.setToValue(1);

        ParallelTransition parallelTransition = new ParallelTransition(controlBarFade, menuFade);
        parallelTransition.setOnFinished(e -> {
            menu.setMouseTransparent(false);
            menuInTransition = false;
        });

        parallelTransition.play();
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

        Rectangle rect = new Rectangle();
        rect.setFill(Color.rgb(0,0,0,0.8));

        FillTransition controlBarFade = new FillTransition();
        controlBarFade.setShape(rect);
        controlBarFade.setDuration(animationDuration);
        controlBarFade.setFromValue(Color.rgb(0,0,0,0.8));
        controlBarFade.setToValue(Color.rgb(0,0,0,0));

        controlBarFade.setInterpolator(new Interpolator() {
            @Override
            protected double curve(double t) {
                controlBarController.controlBarWrapper.setBackground(new Background(new BackgroundFill(rect.getFill(), CornerRadii.EMPTY, Insets.EMPTY)));
                return t;
            }
        });

        FadeTransition menuFade = new FadeTransition(animationDuration, menu);
        menuFade.setFromValue(menu.getOpacity());
        menuFade.setToValue(0);

        ParallelTransition parallelTransition = new ParallelTransition(controlBarFade, menuFade);
        parallelTransition.setOnFinished(e -> {
            menuInTransition = false;
            metadataEditScroll.setVisible(false);
            technicalDetailsScroll.setVisible(false);
            chapterScroll.setVisible(false);
            queueContainer.setVisible(true);

            metadataEditPage.metadataEditItem = null;
            metadataEditPage.textBox.getChildren().clear();
            metadataEditPage.imageView.setImage(null);
            metadataEditPage.imageViewContainer.setStyle("-fx-background-color: transparent;");

            technicalDetailsPage.textBox.getChildren().clear();
            technicalDetailsPage.imageView.setImage(null);
            technicalDetailsPage.imageViewContainer.setStyle("-fx-background-color: transparent;");
        });

        parallelTransition.play();
    }

    private void closeShrinkedMenu(){
        TranslateTransition closeMenu = new TranslateTransition(Duration.millis(300), menu);
        closeMenu.setFromX(menu.getTranslateX());
        closeMenu.setToX(-menu.getWidth());

        closeMenu.setOnFinished((e) -> {
            //TODO: only reset the variables relevant to the current menu state
            menuInTransition = false;
            metadataEditScroll.setVisible(false);
            technicalDetailsScroll.setVisible(false);
            chapterScroll.setVisible(false);
            queueContainer.setVisible(true);

            metadataEditPage.metadataEditItem = null;
            metadataEditPage.textBox.getChildren().clear();
            metadataEditPage.imageView.setImage(null);
            metadataEditPage.imageViewContainer.setStyle("-fx-background-color: transparent;");

            technicalDetailsPage.textBox.getChildren().clear();
            technicalDetailsPage.imageView.setImage(null);
            technicalDetailsPage.imageViewContainer.setStyle("-fx-background-color: transparent;");
        });
        closeMenu.play();
    }
}



