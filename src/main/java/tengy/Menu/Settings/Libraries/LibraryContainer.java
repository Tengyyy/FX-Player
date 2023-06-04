package tengy.Menu.Settings.Libraries;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.stage.DirectoryChooser;
import tengy.*;
import tengy.Menu.FocusableMenuButton;
import tengy.PlaybackSettings.PlaybackSettingsState;
import tengy.Subtitles.SubtitlesState;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static tengy.Utilities.keyboardFocusOff;
import static tengy.Utilities.keyboardFocusOn;

public class LibraryContainer extends VBox {


    PressableNode header = new PressableNode();
    SVGPath svg = new SVGPath();
    Region icon = new Region();
    Label label = new Label();
    SVGPath folderSVG = new SVGPath();
    Region folderIcon = new Region();
    FocusableMenuButton addButton = new FocusableMenuButton();
    SVGPath expandSVG = new SVGPath();
    Region expandIcon = new Region();
    StackPane containerWrapper = new StackPane();
    ClippedNode clippedNode;
    VBox container = new VBox();

    List<File> folders = new ArrayList<>();

    boolean showing = false;
    boolean headerPressed = false;

    LibrariesSection librariesSection;

    DirectoryChooser libraryChooser = new DirectoryChooser();

    IntegerProperty focus = new SimpleIntegerProperty(-1);
    List<Node> focusNodes = new ArrayList<>();

    int index;

    LibraryContainer(LibrariesSection librariesSection, String text, String iconPath, int index){

        this.index = index;

        this.librariesSection = librariesSection;

        libraryChooser.setTitle("Select folder");


        container.prefWidthProperty().bind(librariesSection.librariesSectionWrapper.widthProperty());

        clippedNode = new ClippedNode(container);
        Rectangle musicClip = new Rectangle();
        musicClip.widthProperty().bind(containerWrapper.widthProperty());
        musicClip.heightProperty().bind(containerWrapper.heightProperty());
        containerWrapper.setClip(musicClip);
        containerWrapper.getChildren().add(clippedNode);

        expandSVG.setContent(SVG.CHEVRON_DOWN.getContent());
        folderSVG.setContent(SVG.FOLDER.getContent());

        header.getChildren().addAll(icon, label, addButton, expandIcon);
        header.setPadding(new Insets(20, 10, 20, 10));
        header.getStyleClass().addAll("highlightedSection", "settingsToggle", "libraryHeader");
        header.setOnMouseClicked(e -> {

            if(librariesSection.settingsPage.menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) librariesSection.settingsPage.menuController.subtitlesController.closeSubtitles();
            if(librariesSection.settingsPage.menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) librariesSection.settingsPage.menuController.playbackSettingsController.closeSettings();

            if(showing) hide();
            else expand();

            header.requestFocus();

            e.consume();
        });

        header.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(0);
                librariesSection.focus.set(index);
                librariesSection.settingsPage.focus.set(2);
                header.pseudoClassStateChanged(PseudoClass.getPseudoClass("focus"), true);
            }
            else {
                keyboardFocusOff(header);
                focus.set(-1);
                librariesSection.focus.set(-1);
                librariesSection.settingsPage.focus.set(-1);
                headerPressed = false;
                header.pseudoClassStateChanged(PseudoClass.getPseudoClass("focus"), false);
            }
        });

        header.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            headerPressed = true;
            header.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);

            e.consume();
        });

        header.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            if(headerPressed){
                if(showing) hide();
                else expand();
            }

            headerPressed = false;

            header.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);

            e.consume();
        });


        svg.setContent(iconPath);
        icon.setShape(svg);
        icon.setPrefSize(17, 17);
        icon.setMaxSize(17, 17);
        icon.getStyleClass().add("menuIcon");
        icon.setMouseTransparent(true);
        StackPane.setMargin(icon, new Insets(0, 0, 0, 8));
        StackPane.setAlignment(icon, Pos.CENTER_LEFT);

        label.setText(text);
        label.getStyleClass().add("toggleText");
        StackPane.setMargin(label, new Insets(0, 0, 0, 35));
        StackPane.setAlignment(label, Pos.CENTER_LEFT);

        folderIcon.setShape(folderSVG);
        folderIcon.setPrefSize(14, 12);
        folderIcon.setMaxSize(14,12);
        folderIcon.getStyleClass().addAll("menuIcon", "graphic");

        addButton.setText("Add folder");

        StackPane.setAlignment(addButton, Pos.CENTER_RIGHT);
        StackPane.setMargin(addButton, new Insets(0, 40, 0, 0));
        addButton.getStyleClass().add("menuButton");
        addButton.setGraphic(folderIcon);

        addButton.setOnMousePressed(e -> {
            header.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });

        addButton.setOnAction(e -> {
            if(librariesSection.settingsPage.menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) librariesSection.settingsPage.menuController.subtitlesController.closeSubtitles();
            if(librariesSection.settingsPage.menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) librariesSection.settingsPage.menuController.playbackSettingsController.closeSettings();

            openLibraryChooser();
            e.consume();
        });

        addButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(1);
                librariesSection.focus.set(index);
                librariesSection.settingsPage.focus.set(2);
            }
            else {
                keyboardFocusOff(addButton);
                focus.set(-1);
                librariesSection.focus.set(-1);
                librariesSection.settingsPage.focus.set(-1);
            }
        });

        addButton.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            addButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        addButton.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            addButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });

        expandIcon.setShape(expandSVG);
        expandIcon.setPrefSize(13, 7);
        expandIcon.setMaxSize(13, 7);
        expandIcon.getStyleClass().addAll("menuIcon", "graphic");
        StackPane.setMargin(expandIcon, new Insets(0, 5, 0, 0));
        StackPane.setAlignment(expandIcon, Pos.CENTER_RIGHT);

        this.setPadding(new Insets(15, 0, 0, 0));
        this.getChildren().addAll(header, containerWrapper);

        focusNodes.add(header);
        focusNodes.add(addButton);
    }

    private void expand(){

        showing = true;

        focusNodes.addAll(container.getChildren());

        Timeline regionMin = AnimationsClass.animateMinHeight(folders.size() * 67, clippedNode);
        Timeline regionMax = AnimationsClass.animateMaxHeight(folders.size() * 67, clippedNode);
        Timeline wrapperMin = AnimationsClass.animateMinHeight(folders.size() * 67, containerWrapper);
        Timeline wrapperMax = AnimationsClass.animateMaxHeight(folders.size() * 67, containerWrapper);

        RotateTransition rotateTransition = AnimationsClass.rotateTransition(200, expandIcon, expandIcon.getRotate(), 180, false, 1, false);

        ParallelTransition parallelTransition = new ParallelTransition(regionMin, regionMax, wrapperMin, wrapperMax, rotateTransition);
        parallelTransition.playFromStart();
    }

    private void hide(){

        showing = false;

        focusNodes.clear();
        focusNodes.add(header);
        focusNodes.add(addButton);

        Timeline regionMin = AnimationsClass.animateMinHeight(0, clippedNode);
        Timeline regionMax = AnimationsClass.animateMaxHeight(0, clippedNode);
        Timeline wrapperMin = AnimationsClass.animateMinHeight(0, containerWrapper);
        Timeline wrapperMax = AnimationsClass.animateMaxHeight(0, containerWrapper);

        RotateTransition rotateTransition = AnimationsClass.rotateTransition(200, expandIcon, expandIcon.getRotate(), 0, false, 1, false);

        ParallelTransition parallelTransition = new ParallelTransition(regionMin, regionMax, wrapperMin, wrapperMax, rotateTransition);
        parallelTransition.playFromStart();
    }

    private void add(File folder){
        if(folders.contains(folder)) return;

        folders.add(folder);

        librariesSection.refreshAllButton.setDisable(false);


        LibraryItem libraryItem = new LibraryItem(this, folder, index);

        if(showing){

            focusNodes.add(libraryItem);

            Timeline regionMin = AnimationsClass.animateMinHeight(folders.size() * 67, clippedNode);
            Timeline regionMax = AnimationsClass.animateMaxHeight(folders.size() * 67, clippedNode);
            Timeline wrapperMin = AnimationsClass.animateMinHeight(folders.size() * 67, containerWrapper);
            Timeline wrapperMax = AnimationsClass.animateMaxHeight(folders.size() * 67, containerWrapper);

            ParallelTransition parallelTransition = new ParallelTransition(regionMin, regionMax, wrapperMin, wrapperMax);
            parallelTransition.setOnFinished(j -> {
                container.getChildren().add(libraryItem);
                FadeTransition fadeTransition = AnimationsClass.fadeAnimation(300, libraryItem, libraryItem.getOpacity(), 1,false, 1, false);
                fadeTransition.setOnFinished(e -> {
                    libraryItem.setMouseTransparent(false);
                });

                fadeTransition.playFromStart();
            });
            parallelTransition.playFromStart();
        }
        else {
            libraryItem.setOpacity(1);
            libraryItem.setMouseTransparent(false);

            container.getChildren().add(libraryItem);
        }
    }

    public void remove(LibraryItem libraryItem){

        int index = container.getChildren().indexOf(libraryItem);
        for(int i=index+1; i < container.getChildren().size(); i++){
            LibraryItem libraryItem1 = (LibraryItem) container.getChildren().get(i);
            libraryItem1.index = libraryItem1.index - 1;
        }

        folders.remove(libraryItem.file);

        if(showing){

            focusNodes.remove(libraryItem);
            libraryItem.setMouseTransparent(true);
            FadeTransition fadeTransition = AnimationsClass.fadeAnimation(300, libraryItem, libraryItem.getOpacity(), 0, false, 1, false);
            fadeTransition.setOnFinished(e -> {

                Timeline regionMin = AnimationsClass.animateMinHeight(folders.size() * 67, clippedNode);
                Timeline regionMax = AnimationsClass.animateMaxHeight(folders.size() * 67, clippedNode);
                Timeline wrapperMin = AnimationsClass.animateMinHeight(folders.size() * 67, containerWrapper);
                Timeline wrapperMax = AnimationsClass.animateMaxHeight(folders.size() * 67, containerWrapper);
                Timeline itemMin = AnimationsClass.animateMinHeight(0, libraryItem);
                Timeline itemMax = AnimationsClass.animateMaxHeight(0, libraryItem);

                ParallelTransition parallelTransition = new ParallelTransition(regionMin, regionMax, wrapperMin, wrapperMax, itemMin, itemMax);
                parallelTransition.setOnFinished(j -> {
                    container.getChildren().remove(libraryItem);

                });
                parallelTransition.playFromStart();
            });

            fadeTransition.playFromStart();
        }
        else
            container.getChildren().remove(libraryItem);
    }

    private void openLibraryChooser(){
        File folder = libraryChooser.showDialog(this.getScene().getWindow());

        if(folder != null){
            add(folder);
        }
    }

    public boolean focusForward(){

        if(focus.get() == -1){
            keyboardFocusOn(header);
            Utilities.checkScrollDown(librariesSection.settingsPage.settingsScroll, header);
        }
        else if(focus.get() == 0){
            keyboardFocusOn(addButton);
            Utilities.checkScrollDown(librariesSection.settingsPage.settingsScroll, header);
        }
        else if(focus.get() == 1 && focusNodes.size() > 2){
            LibraryItem libraryItem = (LibraryItem) focusNodes.get(2);
            libraryItem.focusForward();
        }
        else {
            if(focus.get() == focusNodes.size() - 1){
                if(focusNodes.get(focus.get()) instanceof LibraryItem libraryItem){
                    return libraryItem.focusForward();
                }
                else return true;
            }
            else {
                LibraryItem libraryItem = (LibraryItem) focusNodes.get(focus.get());
                boolean skipFocus = libraryItem.focusForward();
                if (skipFocus) {
                    LibraryItem libraryItem1 = (LibraryItem) focusNodes.get(focus.get() + 1);
                    libraryItem1.focusForward();
                }
            }
        }

        return false;
    }

    public boolean focusBackward(){

        if(focus.get() == 0){
            return true;
        }
        else if(focus.get() == 1){
            keyboardFocusOn(header);
            Utilities.checkScrollUp(librariesSection.settingsPage.settingsScroll, header);
        }
        else if(focus.get() == -1){
            Node node = focusNodes.get(focusNodes.size() - 1);
            if(node instanceof LibraryItem libraryItem)
                libraryItem.focusBackward();
            else {
                keyboardFocusOn(node);
                Utilities.checkScrollUp(librariesSection.settingsPage.settingsScroll, header);
            }
        }
        else {
            if(focusNodes.get(focus.get()) instanceof LibraryItem libraryItem){
                boolean skipFocus =  libraryItem.focusBackward();
                if(skipFocus){
                    Node node = focusNodes.get(focus.get() - 1);
                    if(node instanceof LibraryItem libraryItem1){
                        libraryItem1.focusBackward();
                    }
                    else {
                        keyboardFocusOn(node);
                        Utilities.checkScrollUp(librariesSection.settingsPage.settingsScroll, header);
                    }
                }
            }
            else{
                keyboardFocusOn(focusNodes.get(focus.get() - 1));
                Utilities.checkScrollUp(librariesSection.settingsPage.settingsScroll, header);
            }
        }

        return false;
    }
}
