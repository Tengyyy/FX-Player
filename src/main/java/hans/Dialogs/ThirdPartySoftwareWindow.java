package hans.Dialogs;

import hans.*;
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

public class ThirdPartySoftwareWindow {

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

    StackPane closeButtonPane = new StackPane();
    Region closeButtonIcon = new Region();
    SVGPath closeButtonSVG = new SVGPath();
    Button closeButton = new Button();

    boolean showing = false;

    public ThirdPartySoftwareWindow(MainController mainController){
        this.mainController = mainController;

        mainController.popupWindowContainer.getChildren().add(window);

        window.setAlignment(Pos.TOP_LEFT);

        window.prefWidthProperty().bind(Bindings.max(400, Bindings.min(500, mainController.videoImageViewWrapper.widthProperty().multiply(0.45))));
        window.maxWidthProperty().bind(Bindings.max(400, Bindings.min(500, mainController.videoImageViewWrapper.widthProperty().multiply(0.45))));

        window.prefHeightProperty().bind(Bindings.max(350, Bindings.min(1000, mainController.videoImageViewWrapper.heightProperty().multiply(0.8))));
        window.maxHeightProperty().bind(Bindings.max(350, Bindings.min(1000, mainController.videoImageViewWrapper.heightProperty().multiply(0.8))));

        window.getStyleClass().add("popupWindow");
        window.setVisible(false);
        window.getChildren().addAll(windowContainer, buttonContainer, closeButtonPane);

        StackPane.setAlignment(closeButtonPane, Pos.TOP_RIGHT);
        StackPane.setMargin(closeButtonPane, new Insets(15, 15, 0 ,0));
        closeButtonPane.setPrefSize(25, 25);
        closeButtonPane.setMaxSize(25, 25);
        closeButtonPane.getChildren().addAll(closeButton, closeButtonIcon);
        closeButtonPane.setTranslateX(5);

        closeButton.setPrefWidth(25);
        closeButton.setPrefHeight(25);
        closeButton.getStyleClass().add("popupWindowCloseButton");
        closeButton.setCursor(Cursor.HAND);
        closeButton.setOpacity(0);
        closeButton.setText(null);
        closeButton.setOnAction(e -> this.hide());

        closeButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> AnimationsClass.fadeAnimation(200, closeButton, 0, 1, false, 1, true));

        closeButton.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> AnimationsClass.fadeAnimation(200, closeButton, 1, 0, false, 1, true));

        closeButtonSVG.setContent(SVG.CLOSE.getContent());

        closeButtonIcon.setShape(closeButtonSVG);
        closeButtonIcon.setMinSize(13, 13);
        closeButtonIcon.setPrefSize(13, 13);
        closeButtonIcon.setMaxSize(13, 13);
        closeButtonIcon.setMouseTransparent(true);
        closeButtonIcon.getStyleClass().add("menuIcon");

        descriptionLabel.setWrapText(true);
        descriptionLabel.setPrefHeight(60);
        descriptionLabel.setMinHeight(60);
        descriptionLabel.getStyleClass().add("thirdPartyText");

        textContainer.setPadding(new Insets(15, 0, 15, 0));
        textContainer.setSpacing(15);
        textContainer.getChildren().addAll(descriptionLabel, uiBox, mediaBox, subtitlesBox, mediaInfoBox, loggingBox);

        uiBox.setSpacing(10);
        uiBox.getChildren().addAll(
                createTitleLabel("Application User Interface"),
                createLinkLabel("JavaFX", "https://openjfx.io/"),
                createLinkLabel("MaterialFX", "https://github.com/palexdev/MaterialFX"),
                createLinkLabel("FX-BorderlessScene", "https://github.com/goxr3plus/FX-BorderlessScene"),
                createLinkLabel("ControlsFX", "https://github.com/controlsfx/controlsfx"),
                createLinkLabel("MDFX Markdown renderer for JavaFX", "https://github.com/JPro-one/markdown-javafx-renderer")
        );


        mediaBox.setSpacing(10);
        mediaBox.getChildren().addAll(
                createTitleLabel("Media playback and parsing"),
                createLinkLabel("LibVLC", "https://www.videolan.org/vlc/libvlc.html"),
                createLinkLabel("VLCJ Java framework for VLC Media Player", "https://github.com/caprica/vlcj"),
                createLinkLabel("FFmpeg", "https://ffmpeg.org/"),
                createLinkLabel("JavaCV", "https://github.com/bytedeco/javacv"),
                createLinkLabel("Jaffree FFmpeg command line wrapper", "https://github.com/kokorin/Jaffree")
        );


        subtitlesBox.setSpacing(10);
        subtitlesBox.getChildren().addAll(
                createTitleLabel("Subtitles"),
                createLinkLabel("SRTParser", "https://github.com/gusthavosouza/SRTParser"),
                createLinkLabel("OpenSubtitles", "https://www.opensubtitles.org/"),
                createLinkLabel("Java library for OpenSubtitles", "https://github.com/wtekiela/opensub4j")
        );


        mediaInfoBox.setSpacing(10);
        mediaInfoBox.getChildren().addAll(
                createTitleLabel("Media information"),
                createLinkLabel("The Movie Database - TMDb", "https://www.themoviedb.org/"),
                createLinkLabel("TMDb Java wrapper", "https://github.com/UweTrottmann/tmdb-java"),
                createLinkLabel("Discogs music database", "https://www.discogs.com/"),
                createLinkLabel("Discogs-client-4j", "https://bitbucket.org/kristof_debruyne/discogs-client-4j/src/master/")
        );


        loggingBox.setSpacing(10);
        loggingBox.getChildren().addAll(
                createTitleLabel("Logging"),
                createLinkLabel("Log4J", "https://logging.apache.org/log4j/2.x/"),
                createLinkLabel("SLF4J", "https://www.slf4j.org/"),
                createLinkLabel("Logback", "https://logback.qos.ch/")
        );


        textScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        textScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        textScroll.getStyleClass().add("menuScroll");
        textScroll.setFitToWidth(true);
        textScroll.setFitToHeight(true);
        textScroll.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        textScroll.setBackground(Background.EMPTY);
        textScroll.setContent(textContainer);


        windowContainer.setPadding(new Insets(15, 15, 15, 15));
        windowContainer.getChildren().addAll(titleContainer, textScroll);
        windowContainer.setSpacing(20);
        StackPane.setMargin(windowContainer, new Insets(0, 0, 80, 0));

        titleContainer.getChildren().addAll(title);
        titleContainer.setPadding(new Insets(5, 0, 5, 0));

        title.getStyleClass().addAll("popupWindowTitle", "licenseWindowTitle");




        StackPane.setAlignment(buttonContainer, Pos.BOTTOM_CENTER);
        buttonContainer.getChildren().add(mainButton);
        buttonContainer.getStyleClass().add("buttonContainer");
        buttonContainer.setPadding(new Insets(0, 15, 0, 15));
        buttonContainer.setPrefHeight(70);
        buttonContainer.setMaxHeight(70);

        mainButton.getStyleClass().add("mainButton");
        mainButton.setCursor(Cursor.HAND);
        mainButton.setTextAlignment(TextAlignment.CENTER);
        mainButton.setPrefWidth(230);
        mainButton.setOnAction(e -> this.hide());
        StackPane.setAlignment(mainButton, Pos.CENTER_RIGHT);
    }

    public void show(){



        if(mainController.closeConfirmationWindow.showing){
            mainController.closeConfirmationWindow.window.setVisible(false);
            mainController.closeConfirmationWindow.showing = false;
        }

        if(mainController.addYoutubeVideoWindow.showing){
            mainController.addYoutubeVideoWindow.window.setVisible(false);
            mainController.addYoutubeVideoWindow.showing = false;
        }

        if(mainController.hotkeyChangeWindow.showing){
            mainController.hotkeyChangeWindow.window.setVisible(false);
            mainController.hotkeyChangeWindow.showing = false;
        }

        if(mainController.licenseWindow.showing){
            mainController.licenseWindow.window.setVisible(false);
            mainController.licenseWindow.showing = false;
        }



        this.showing = true;
        window.setVisible(true);

        mainController.popupWindowContainer.setMouseTransparent(false);
        AnimationsClass.fadeAnimation(100, mainController.popupWindowContainer, 0 , 1, false, 1, true);
    }

    public void hide(){
        this.showing = false;

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


    private Label createLinkLabel(String displayText, String url){
        Label label = new Label(displayText);
        label.getStyleClass().addAll("thirdPartyText", "thirdPartyLink");
        label.setOnMouseClicked(e -> Utilities.openBrowser(url));
        label.setOnMouseEntered(e -> label.setUnderline(true));
        label.setOnMouseExited(e -> label.setUnderline(false));

        return label;
    }
}
