package tengy.Windows;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import tengy.*;
import javafx.animation.FadeTransition;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

import static tengy.Utilities.keyboardFocusOff;
import static tengy.Utilities.keyboardFocusOn;

public class ThirdPartySoftwareWindow {

    WindowController windowController;
    MainController mainController;


    StackPane window = new StackPane();

    VBox windowContainer = new VBox();

    VBox titleContainer = new VBox();
    Label title = new Label("Third-Party Software");

    ScrollPane textScroll = new ScrollPane();

    VBox textContainer = new VBox();
    Label descriptionLabel = new Label("The following is a list of third-party software that has enabled the creation of FX Player");

    VBox uiBox = new VBox();
    VBox mediaBox = new VBox();
    VBox subtitlesBox = new VBox();
    VBox mediaInfoBox = new VBox();
    VBox loggingBox = new VBox();

    StackPane buttonContainer = new StackPane();
    Button mainButton = new Button("Close");

    Region closeButtonIcon = new Region();
    SVGPath closeButtonSVG = new SVGPath();
    Button closeButton = new Button();

    boolean showing = false;

    IntegerProperty focus = new SimpleIntegerProperty(-1);
    List<Node> focusNodes = new ArrayList<>();

    public ThirdPartySoftwareWindow(WindowController windowController){
        this.windowController = windowController;
        this.mainController = windowController.mainController;

        mainController.popupWindowContainer.getChildren().add(window);

        window.setAlignment(Pos.TOP_LEFT);

        window.prefWidthProperty().bind(Bindings.max(400, Bindings.min(500, mainController.videoImageViewWrapper.widthProperty().multiply(0.4))));
        window.maxWidthProperty().bind(Bindings.max(400, Bindings.min(500, mainController.videoImageViewWrapper.widthProperty().multiply(0.4))));

        window.prefHeightProperty().bind(Bindings.max(350, Bindings.min(1000, mainController.videoImageViewWrapper.heightProperty().multiply(0.8))));
        window.maxHeightProperty().bind(Bindings.max(350, Bindings.min(1000, mainController.videoImageViewWrapper.heightProperty().multiply(0.8))));

        window.getStyleClass().add("popupWindow");
        window.setVisible(false);
        window.getChildren().addAll(windowContainer, buttonContainer, closeButton);

        StackPane.setAlignment(closeButton, Pos.TOP_RIGHT);
        StackPane.setMargin(closeButton, new Insets(10, 10, 0 ,0));
        closeButton.setPrefSize(25, 25);
        closeButton.getStyleClass().addAll("transparentButton", "popupWindowCloseButton");
        closeButton.setOnAction(e -> this.hide());
        closeButton.setFocusTraversable(false);
        closeButton.setGraphic(closeButtonIcon);
        closeButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(0);
            }
            else{
                keyboardFocusOff(closeButton);
                focus.set(-1);
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

        focusNodes.add(closeButton);

        closeButtonSVG.setContent(SVG.CLOSE.getContent());

        closeButtonIcon.setShape(closeButtonSVG);
        closeButtonIcon.setMinSize(13, 13);
        closeButtonIcon.setPrefSize(13, 13);
        closeButtonIcon.setMaxSize(13, 13);
        closeButtonIcon.setMouseTransparent(true);
        closeButtonIcon.getStyleClass().add("graphic");

        descriptionLabel.setWrapText(true);
        descriptionLabel.setPrefHeight(60);
        descriptionLabel.setMinHeight(60);
        descriptionLabel.getStyleClass().add("thirdPartyText");

        textContainer.setPadding(new Insets(15, 20, 15, 0));
        textContainer.setSpacing(15);
        textContainer.getChildren().addAll(descriptionLabel, uiBox, mediaBox, subtitlesBox, mediaInfoBox, loggingBox);

        uiBox.setSpacing(10);
        uiBox.getChildren().addAll(
                createTitleLabel("Application User Interface"),
                createLinkButton("JavaFX", "https://openjfx.io/", 1),
                createLinkButton("MaterialFX", "https://github.com/palexdev/MaterialFX", 2),
                createLinkButton("FX-BorderlessScene", "https://github.com/goxr3plus/FX-BorderlessScene", 3),
                createLinkButton("ControlsFX", "https://github.com/controlsfx/controlsfx", 4),
                createLinkButton("MDFX Markdown renderer for JavaFX", "https://github.com/JPro-one/markdown-javafx-renderer", 5)
        );

        mediaBox.setSpacing(10);
        mediaBox.getChildren().addAll(
                createTitleLabel("Media playback and parsing"),
                createLinkButton("LibVLC", "https://www.videolan.org/vlc/libvlc.html", 6),
                createLinkButton("VLCJ Java framework for VLC Media Player", "https://github.com/caprica/vlcj", 7),
                createLinkButton("FFmpeg", "https://ffmpeg.org/", 8),
                createLinkButton("JavaCV", "https://github.com/bytedeco/javacv", 9),
                createLinkButton("Jaffree FFmpeg command line wrapper", "https://github.com/kokorin/Jaffree", 10)
        );

        subtitlesBox.setSpacing(10);
        subtitlesBox.getChildren().addAll(
                createTitleLabel("Subtitles"),
                createLinkButton("SRTParser", "https://github.com/gusthavosouza/SRTParser", 11),
                createLinkButton("OpenSubtitles", "https://www.opensubtitles.org/", 12),
                createLinkButton("Java library for OpenSubtitles", "https://github.com/wtekiela/opensub4j", 13)
        );

        mediaInfoBox.setSpacing(10);
        mediaInfoBox.getChildren().addAll(
                createTitleLabel("Media information"),
                createLinkButton("The Movie Database - TMDb", "https://www.themoviedb.org/", 14),
                createLinkButton("TMDb Java wrapper", "https://github.com/UweTrottmann/tmdb-java", 15),
                createLinkButton("Discogs music database", "https://www.discogs.com/", 16),
                createLinkButton("Discogs-client-4j", "https://bitbucket.org/kristof_debruyne/discogs-client-4j/src/master/", 17)
        );


        loggingBox.setSpacing(10);
        loggingBox.getChildren().addAll(
                createTitleLabel("Logging"),
                createLinkButton("Log4J", "https://logging.apache.org/log4j/2.x/", 18),
                createLinkButton("SLF4J", "https://www.slf4j.org/", 19),
                createLinkButton("Logback", "https://logback.qos.ch/", 20)
        );


        textScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        textScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        textScroll.getStyleClass().add("menuScroll");
        textScroll.setFitToWidth(true);
        textScroll.setFitToHeight(true);
        textScroll.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        textScroll.setBackground(Background.EMPTY);
        textScroll.setContent(textContainer);


        windowContainer.setPadding(new Insets(15, 0, 0, 15));
        windowContainer.getChildren().addAll(titleContainer, textScroll);
        windowContainer.setSpacing(20);
        StackPane.setMargin(windowContainer, new Insets(0, 0, 70, 0));

        titleContainer.getChildren().addAll(title);
        titleContainer.setPadding(new Insets(5, 0, 5, 0));

        title.getStyleClass().addAll("popupWindowTitle", "licenseWindowTitle");

        StackPane.setAlignment(buttonContainer, Pos.BOTTOM_CENTER);
        buttonContainer.getChildren().add(mainButton);
        buttonContainer.getStyleClass().add("buttonContainer");
        buttonContainer.setPadding(new Insets(0, 15, 0, 15));
        buttonContainer.setPrefHeight(70);
        buttonContainer.setMaxHeight(70);

        mainButton.getStyleClass().add("menuButton");
        mainButton.setTextAlignment(TextAlignment.CENTER);
        mainButton.setPrefWidth(230);
        mainButton.setOnAction(e -> this.hide());
        mainButton.setFocusTraversable(false);
        mainButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(focusNodes.size() - 1);
            }
            else{
                keyboardFocusOff(mainButton);
                focus.set(-1);
            }
        });

        mainButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            mainButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        mainButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            mainButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });
        StackPane.setAlignment(mainButton, Pos.CENTER_RIGHT);

        focusNodes.add(mainButton);
    }

    public void show(){

        windowController.updateState(WindowState.THIRD_PARTY_SOFTWARE_WINDOW_OPEN);


        this.showing = true;
        window.setVisible(true);

        mainController.popupWindowContainer.setMouseTransparent(false);

        window.requestFocus();
        AnimationsClass.fadeAnimation(100, mainController.popupWindowContainer, 0 , 1, false, 1, true);
    }

    public void hide(){
        this.showing = false;

        windowController.windowState = WindowState.CLOSED;

        mainController.popupWindowContainer.setMouseTransparent(true);

        FadeTransition fadeTransition = new FadeTransition(Duration.millis(100), mainController.popupWindowContainer);
        fadeTransition.setFromValue(mainController.popupWindowContainer.getOpacity());
        fadeTransition.setToValue(0);
        fadeTransition.setOnFinished(e -> {
            window.setVisible(false);
            textScroll.setVvalue(0);
        });
        fadeTransition.play();
    }

    private Label createTitleLabel(String text){
        Label label = new Label(text);
        label.getStyleClass().add("thirdPartyTitle");
        return label;
    }

    private Button createLinkButton(String displayText, String url, int index){
        Button button = new Button();
        button.getStyleClass().addAll("linkButton");
        button.setText(displayText);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setFocusTraversable(false);
        button.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue) focus.set(index);
            else {
                keyboardFocusOff(button);
                focus.set(-1);
            }
        });

        button.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            button.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        button.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            button.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });

        button.setOnAction(e -> {
            button.requestFocus();
            Utilities.openBrowser(url);
        });

        focusNodes.add(button);

        return button;
    }

    public void focusForward(){
        int newFocus;

        if(focus.get() >= focusNodes.size() - 1 || focus.get() == -1) newFocus = 0;
        else newFocus = focus.get() + 1;

        keyboardFocusOn(focusNodes.get(newFocus));

        updateScroll(newFocus);
    }

    public void focusBackward(){
        int newFocus;

        if(focus.get() == 0) newFocus = focusNodes.size() - 1;
        else if(focus.get() == -1) newFocus = 0;
        else newFocus = focus.get() - 1;

        keyboardFocusOn(focusNodes.get(newFocus));

        updateScroll(newFocus);
    }

    private void updateScroll(int newFocus){
        if(newFocus < 1 || newFocus > 20) return;

        Node scrollTo;

        if(newFocus <= 5)
            scrollTo = uiBox;
        else if(newFocus <= 10)
            scrollTo = mediaBox;
        else if(newFocus <= 13)
            scrollTo = subtitlesBox;
        else if(newFocus <= 17)
            scrollTo = mediaInfoBox;
        else
            scrollTo = loggingBox;

        Utilities.setScrollToNodeTop(textScroll, scrollTo);
    }
}
