package hans.Menu;


import hans.AnimationsClass;
import hans.Captions.CaptionsController;
import hans.Chapters.ChapterController;
import hans.ControlBarController;
import hans.MainController;
import hans.MediaInterface;
import hans.MediaItems.MediaItem;
import hans.Menu.MetadataEdit.MetadataEditPage;
import hans.Menu.Queue.QueuePage;
import hans.Settings.SettingsController;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;




public class MenuController implements Initializable {


    @FXML
    public
    StackPane menu, menuContent, sideBar;

    @FXML
    StackPane dragPane;

    @FXML
    public ScrollPane metadataEditScroll, technicalDetailsScroll, chapterScroll;

    @FXML
    public VBox queueWrapper;

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

    final double MIN_WIDTH = 500;

    DragResizer dragResizer;

    public ArrayList<MediaItem> ongoingMetadataEditProcesses = new ArrayList<>();

    MenuBar menuBar;

    public boolean extended = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        queuePage = new QueuePage(this);
        metadataEditPage = new MetadataEditPage(this);
        technicalDetailsPage = new TechnicalDetailsPage(this);
        menuBar = new MenuBar(this, sideBar);


        menu.setBackground(Background.EMPTY);

        menu.setPrefWidth(500);
        menu.setMaxWidth(500);

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

        Platform.runLater(() -> menu.setTranslateX(-menu.getWidth()));

        menu.setMouseTransparent(true);
        Rectangle menuClip = new Rectangle();
        menuClip.widthProperty().bind(menu.widthProperty());
        menuClip.heightProperty().bind(menu.heightProperty());
        menu.setClip(menuClip);

        dragResizer = new DragResizer(this);

        metadataEditScroll.setVisible(false);
        metadataEditScroll.setBackground(Background.EMPTY);

        technicalDetailsScroll.setVisible(false);
        technicalDetailsScroll.setBackground(Background.EMPTY);

        chapterScroll.setVisible(false);
        chapterScroll.setBackground(Background.EMPTY);
    }

    public void closeMenu(){

        if(menuInTransition) return;

        mainController.videoImageView.requestFocus();


        if(dragResizer.dragging) {
            dragResizer.dragging = false;
            dragPane.setCursor(Cursor.DEFAULT);
        }

        menuInTransition = true;
        menuState = MenuState.CLOSED;
        menu.setMouseTransparent(true);
        AnimationsClass.closeMenu(this);
        controlBarController.mouseEventTracker.move();

        captionsController.captionsBox.captionsContainer.setMouseTransparent(false);

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
        if(extended || menuInTransition) return;

        extended = true;
        menu.setMouseTransparent(true);

        dragPane.setMouseTransparent(true);
        dragPane.setVisible(false);

        if(menuState != MenuState.CLOSED){
            menuInTransition = true;

            Duration animationDuration = Duration.millis(300);
            Timeline maxTimeline = new Timeline(new KeyFrame(animationDuration,
                    new KeyValue(menu.maxWidthProperty(), mainController.videoImageViewWrapper.getWidth(), Interpolator.EASE_BOTH)));

            Timeline prefTimeline = new Timeline(new KeyFrame(animationDuration,
                    new KeyValue(menu.prefWidthProperty(), mainController.videoImageViewWrapper.getWidth(), Interpolator.EASE_BOTH)));

            ParallelTransition parallelTransition = new ParallelTransition(maxTimeline, prefTimeline);

            parallelTransition.setOnFinished(e -> {
                setMenuExtended();
                menu.setMouseTransparent(false);
            });

            parallelTransition.playFromStart();
        }
    }

    public void shrinkMenu(){

    }

    private void setMenuExtended(){
        menuInTransition = false;

        menu.maxWidthProperty().bind(mainController.videoImageViewWrapper.widthProperty());
        menu.prefWidthProperty().bind(mainController.videoImageViewWrapper.widthProperty());

        if(mainController.videoImageViewWrapper.getWidth() < 1200) StackPane.setMargin(menuContent, new Insets(0, 0, 0, 50));
        else {
            StackPane.setMargin(menuContent, new Insets(0, 0, 0, 300));
            menuBar.extend();
        }

        queuePage.extend();
    }

    private void setMenuShrinked(){

        StackPane.setMargin(menuContent, new Insets(0, 3, 0, 50));

        queuePage.shrink();
        menuBar.shrink();
    }
}



