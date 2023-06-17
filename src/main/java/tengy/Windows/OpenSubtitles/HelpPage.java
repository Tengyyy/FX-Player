package tengy.Windows.OpenSubtitles;

import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import tengy.SVG;
import tengy.Utilities;

import java.util.List;

import static tengy.Utilities.keyboardFocusOff;
import static tengy.Utilities.keyboardFocusOn;

public class HelpPage extends VBox implements Page{

    OpenSubtitlesWindow openSubtitlesWindow;

    public ScrollPane scrollPane;

    VBox content = new VBox();

    StackPane titleContainer = new StackPane();
    Label title = new Label("Help");
    Button backButton = new Button();

    TextFlow textFlow = new TextFlow();
    Text text1 = new Text("Download subtitles from");
    Hyperlink hyperlink = new Hyperlink("opensubtitles.com");
    Text text3 = new Text("and display them while watching a video");
    Text text4 = new Text("Usage:");
    Text text5 = new Text("Start your video. If you download subtitles without first playing a video then the subtitle file will be saved to your Downloads folder but it won't be applied automatically.\n" +
            "Choose the languages you would like to download subtitles in and use one of the two methods for subtitle searching.");
    Text text6 = new Text("Method 1: Search by file hash");
    Text text7 = new Text("It is recommended to try this method first, because it performs a search based on the video file print, so you are more likely to find high quality subtitles that are synchronized with your video. \n" +
            "Keep in mind that this method will only work if you have not modified/edited the video file.");
    Text text8 = new Text("Method 2: Search by title");
    Text text9 = new Text("Use this method to manually search for subtitles by specifying the movie title if you have no luck with the first method. When searching for subtitles for a TV Show/Series, you can also provide a season and episode number.");
    Text text10 = new Text("Downloading subtitles");
    Text text11 = new Text("""
            Click on one of subtitle files to download it.
            The subtitle file will be saved to the same directory as the currently active video and with the same name (different extension) so FXPlayer will be able to load the subtitles automatically every time you play the video.
            Make sure the option 'Scan parent folder for subtitle file with matching name' is turned on in the settings menu for the subtitles to load automatically next time you play the video.\s""");


    HelpPage(OpenSubtitlesWindow openSubtitlesWindow){
        this.openSubtitlesWindow = openSubtitlesWindow;
        this.setOpacity(0);
        this.setVisible(false);

        titleContainer.setPadding(new Insets(15, 20, 15, 0));
        titleContainer.setOnMouseClicked(e -> openSubtitlesWindow.window.requestFocus());

        StackPane.setAlignment(title, Pos.CENTER_LEFT);
        StackPane.setMargin(title, new Insets(0, 0, 0, 50));
        title.getStyleClass().add("popupWindowTitle");

        titleContainer.getChildren().addAll(backButton, title);


        SVGPath backSVG = new SVGPath();
        backSVG.setContent(SVG.ARROW_LEFT.getContent());
        Region backIcon = new Region();
        backIcon.setShape(backSVG);
        backIcon.setPrefSize(20, 20);
        backIcon.setMaxSize(20, 20);
        backIcon.setMouseTransparent(true);
        backIcon.getStyleClass().add("graphic");

        backButton.setPrefWidth(25);
        backButton.setPrefHeight(25);

        backButton.getStyleClass().addAll("transparentButton", "popupWindowCloseButton");
        backButton.setFocusTraversable(false);
        backButton.setGraphic(backIcon);
        backButton.setOnAction(e -> openSubtitlesWindow.openSearchPage());
        backButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                openSubtitlesWindow.focus.set(0);
            }
            else{
                keyboardFocusOff(backButton);
                openSubtitlesWindow.focus.set(-1);
            }
        });

        backButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            backButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        backButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            backButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });

        StackPane.setAlignment(backButton, Pos.CENTER_LEFT);
        StackPane.setMargin(backButton, new Insets(0, 0, 0, 10));

        scrollPane = new ScrollPane() {
            ScrollBar vertical;

            @Override
            protected void layoutChildren() {
                super.layoutChildren();
                if (vertical == null) {
                    vertical = (ScrollBar) lookup(".scroll-bar:vertical");
                    vertical.visibleProperty().addListener((obs, old, val) -> updatePadding(val));
                }
            }
        };

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("menuScroll");
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        scrollPane.setBackground(Background.EMPTY);
        scrollPane.setContent(content);

        List<Text> titles = List.of(text4, text6,text8,text10);
        List<Text> paragraphs = List.of(text1, text3,text5,text7,text9,text11);

        for(Text text : titles){
            text.setWrappingWidth(540);
            text.setFill(Color.WHITE);
            text.setLineSpacing(15);
            text.getStyleClass().add("blockTitle");
        }

        for(Text text : paragraphs){
            text.setWrappingWidth(540);
            text.setFill(Color.rgb(200,200,200));
            text.setLineSpacing(15);
            text.getStyleClass().add("blockText");
        }

        hyperlink.setFocusTraversable(false);


        content.setPadding(new Insets(15, 20, 15, 15));
        content.setSpacing(15);
        textFlow.getChildren().addAll(text1, hyperlink, text3);
        textFlow.setMaxWidth(540);

        content.getChildren().addAll(textFlow, text4, text5, text6, text7, text8, text9, text10, text11);

        hyperlink.setOnAction(e -> {
            Utilities.openBrowser("https://www.opensubtitles.com/en");
            hyperlink.setVisited(true);
        });

        this.getChildren().addAll(titleContainer, scrollPane);
        StackPane.setMargin(this, new Insets(0, 0, 70, 0));


    }

    public void reset(){
        this.setOpacity(0);
        this.setVisible(false);
        scrollPane.setVvalue(0);

        openSubtitlesWindow.helpButton.setDisable(false);
    }


    @Override
    public boolean focusForward(){
        if(backButton.isFocused()) return true;

        keyboardFocusOn(backButton);
        return false;
    }

    @Override
    public boolean focusBackward(){
        if(backButton.isFocused()) return true;

        keyboardFocusOn(backButton);
        return false;
    }


    private void updatePadding(boolean value){
        if(value) {
            content.setPadding(new Insets(15, 8, 15, 15));
            content.setMaxWidth(scrollPane.getWidth() - 12);
        }
        else {
            content.setPadding(new Insets(15, 20, 15, 15));
            content.setMaxWidth(scrollPane.getWidth());
        }
    }
}
