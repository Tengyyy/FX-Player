package hans.Subtitles;

import hans.App;
import hans.SVG;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

public class SearchOptionsContextMenu extends ContextMenu {

    OpenSubtitlesPane openSubtitlesPane;

    SVGPath checkSVG = new SVGPath();

    MenuItem fileItem = new MenuItem();
    HBox fileItemContainer = new HBox();
    SVGPath filePath = new SVGPath();
    StackPane fileIconPane = new StackPane();
    Region fileIcon = new Region();
    Label fileLabel = new Label();
    Region fileCheck = new Region();

    MenuItem imdbItem = new MenuItem();
    HBox imdbItemContainer = new HBox();
    SVGPath imdbPath = new SVGPath();
    StackPane imdbIconPane = new StackPane();
    Region imdbIcon = new Region();
    Label imdbLabel = new Label();
    Region imdbCheck = new Region();

    MenuItem queryItem = new MenuItem();
    HBox queryItemContainer = new HBox();
    SVGPath queryPath = new SVGPath();
    StackPane queryIconPane = new StackPane();
    Region queryIcon = new Region();
    Label queryLabel = new Label();
    Region queryCheck = new Region();


    double buttonWidth;

    final double popUpWidth = 297; // calling getWidth on this pop-up window is inaccurate as it sometimes incorrectly shows 151, hard-coded value is used to always get the same result
    final double popUpHeight = 109;

    FadeTransition showTransition, hideTransition;


    public boolean showing = false;

    SearchOptionsContextMenu(OpenSubtitlesPane openSubtitlesPane){

        this.openSubtitlesPane = openSubtitlesPane;

        checkSVG.setContent(SVG.CHECK.getContent());

        this.getStyleClass().add("menu-context-menu");

        filePath.setContent(SVG.VIDEO_FILE.getContent());
        fileIcon.setShape(filePath);
        fileIcon.getStyleClass().add("icon");
        fileIcon.setPrefSize(12, 15);
        fileIcon.setMaxSize(12, 15);

        fileIconPane.getChildren().add(fileIcon);
        fileIconPane.setPrefSize(18, 18);

        fileLabel.setText("Search by active file data");
        fileLabel.setPrefWidth(200);
        fileLabel.getStyleClass().add("customPopUpLabel");


        fileCheck.setShape(checkSVG);
        fileCheck.getStyleClass().add("icon");
        fileCheck.setPrefSize(18, 14);
        fileCheck.setMaxSize(18, 14);
        fileCheck.setVisible(false);

        fileItemContainer.getChildren().addAll(fileIconPane, fileLabel, fileCheck);
        fileItemContainer.setAlignment(Pos.CENTER_LEFT);
        fileItemContainer.setSpacing(10);
        fileItemContainer.setBackground(Background.EMPTY);

        fileItem.setGraphic(fileItemContainer);
        fileItem.getStyleClass().add("popUpItem");
        fileItem.setOnAction(e -> {
            if(fileCheck.isVisible()) return;
            openSubtitlesPane.setFileSearch();
        });

        imdbPath.setContent(SVG.IMDB.getContent());
        imdbIcon.setShape(imdbPath);
        imdbIcon.getStyleClass().add("icon");
        imdbIcon.setPrefSize(17, 17);
        imdbIcon.setMaxSize(17, 17);

        imdbIconPane.getChildren().add(imdbIcon);
        imdbIconPane.setPrefSize(18, 18);

        imdbLabel.setText("Search by IMDb ID");
        imdbLabel.setPrefWidth(200);
        imdbLabel.getStyleClass().add("customPopUpLabel");


        imdbCheck.setShape(checkSVG);
        imdbCheck.getStyleClass().add("icon");
        imdbCheck.setPrefSize(18, 14);
        imdbCheck.setMaxSize(18, 14);
        imdbCheck.setVisible(false);

        imdbItemContainer.getChildren().addAll(imdbIconPane, imdbLabel, imdbCheck);
        imdbItemContainer.setAlignment(Pos.CENTER_LEFT);
        imdbItemContainer.setSpacing(10);
        imdbItemContainer.setBackground(Background.EMPTY);

        imdbItem.setGraphic(imdbItemContainer);
        imdbItem.getStyleClass().add("popUpItem");
        imdbItem.setOnAction(e -> {
            if(imdbCheck.isVisible()) return;
            openSubtitlesPane.setImdbSearch();
        });

        queryPath.setContent(SVG.FILM.getContent());
        queryIcon.setShape(queryPath);
        queryIcon.getStyleClass().add("icon");
        queryIcon.setPrefSize(15, 15);
        queryIcon.setMaxSize(15, 15);

        queryIconPane.getChildren().add(queryIcon);
        queryIconPane.setPrefSize(18, 18);

        queryLabel.setText("Search by Movie/Series title");
        queryLabel.setPrefWidth(200);
        queryLabel.getStyleClass().add("customPopUpLabel");

        queryCheck.setShape(checkSVG);
        queryCheck.getStyleClass().add("icon");
        queryCheck.setPrefSize(18, 14);
        queryCheck.setMaxSize(18, 14);
        queryCheck.setVisible(true);

        queryItemContainer.getChildren().addAll(queryIconPane, queryLabel, queryCheck);
        queryItemContainer.setAlignment(Pos.CENTER_LEFT);
        queryItemContainer.setSpacing(10);
        queryItemContainer.setBackground(Background.EMPTY);

        queryItem.setGraphic(queryItemContainer);
        queryItem.getStyleClass().add("popUpItem");
        queryItem.setOnAction(e -> {
            if(queryCheck.isVisible()) return;
            openSubtitlesPane.setQuerySearch();
        });

        this.getItems().addAll(queryItem, imdbItem, fileItem);

        buttonWidth = openSubtitlesPane.searchButtonWrapper.getWidth();

        this.getStyleableNode().setOpacity(0);
    }


    public void showOptions(boolean animate){
        this.show(openSubtitlesPane.searchButtonWrapper, // might not work
                openSubtitlesPane.searchButtonWrapper.localToScreen(openSubtitlesPane.searchButtonWrapper.getBoundsInLocal()).getMinX() + buttonWidth/2 - popUpWidth/2,
                openSubtitlesPane.searchButtonWrapper.localToScreen(openSubtitlesPane.searchButtonWrapper.getBoundsInLocal()).getMinY() - 3 - popUpHeight, animate);
    }


    public void show(Node node, double v, double v1, boolean animate) {

        if(hideTransition != null && hideTransition.getStatus() == Animation.Status.RUNNING) hideTransition.stop();

        if(animate) this.getStyleableNode().setOpacity(0);
        else this.getStyleableNode().setOpacity(1);

        super.show(node, v, v1);
        showing = true;

        if(animate){
            showTransition = new FadeTransition(Duration.millis(150), this.getStyleableNode());
            showTransition.setFromValue(0);
            showTransition.setToValue(1);
            showTransition.playFromStart();
        }
    }

    @Override
    public void hide() {
        showing = false;

        if(showTransition != null && showTransition.getStatus() == Animation.Status.RUNNING) showTransition.stop();

        hideTransition = new FadeTransition(Duration.millis(150), this.getStyleableNode());
        hideTransition.setFromValue(1);
        hideTransition.setToValue(0);
        hideTransition.setOnFinished(e -> super.hide());
        hideTransition.playFromStart();
    }
}