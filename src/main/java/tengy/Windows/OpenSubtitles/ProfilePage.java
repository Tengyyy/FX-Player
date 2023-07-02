package tengy.Windows.OpenSubtitles;

import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import io.github.palexdev.materialfx.controls.MFXSpinner;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.util.Pair;
import tengy.ClearableTextFieldSkin;
import tengy.OpenSubtitles.OpenSubtitles;
import tengy.OpenSubtitles.models.infos.UserResult;
import tengy.VisiblePasswordFieldSkin;
import tengy.SVG;
import tengy.Utilities;
import tengy.Windows.OpenSubtitles.Tasks.LoginTask;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.prefs.Preferences;

import static tengy.Utilities.keyboardFocusOff;
import static tengy.Utilities.keyboardFocusOn;

public class ProfilePage extends VBox implements Page{

    public ScrollPane scrollPane;

    VBox content = new VBox();

    StackPane titleContainer = new StackPane();
    Label title = new Label("Profile");

    Button backButton = new Button();

    MFXProgressSpinner spinner = new MFXProgressSpinner();
    Label errorLabel = new Label();

    OpenSubtitlesWindow openSubtitlesWindow;

    IntegerProperty focus = new SimpleIntegerProperty(-1);
    List<Node> focusNodes = new ArrayList<>();

    ProfilePage(OpenSubtitlesWindow openSubtitlesWindow){
        this.openSubtitlesWindow = openSubtitlesWindow;
        this.setOpacity(0);

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
        backButton.setOnAction(e -> openSubtitlesWindow.openConnectionPage(false));
        backButton.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                focus.set(0);
                openSubtitlesWindow.focus.set(0);
            }
            else{
                keyboardFocusOff(backButton);
                focus.set(-1);
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
        scrollPane.setMinHeight(150);

        content.setPadding(new Insets(25, 20, 15, 35));
        content.setSpacing(15);
        content.setAlignment(Pos.BOTTOM_CENTER);
        content.getChildren().add(spinner);
        content.setMinHeight(150);

        spinner.setRadius(10);
        spinner.setColor1(Color.WHITE);
        spinner.setColor2(Color.WHITE);
        spinner.setColor3(Color.WHITE);
        spinner.setColor4(Color.WHITE);
        VBox.setMargin(spinner, new Insets(0, 0, 50, 0));


        VBox.setMargin(errorLabel, new Insets(20, 0, 0, 15));
        errorLabel.getStyleClass().add("toggleText");
        errorLabel.setWrapText(true);


        focusNodes.add(backButton);

        this.setVisible(false);
        this.getChildren().addAll(titleContainer, scrollPane);
        StackPane.setMargin(this, new Insets(0, 0, 70, 0));
    }

    public void loadProfile(){

        openSubtitlesWindow.openProfilePage();

        if(openSubtitlesWindow.os == null) openSubtitlesWindow.os = new OpenSubtitles(openSubtitlesWindow.connectionPage.username.get(), openSubtitlesWindow.connectionPage.password.get(), openSubtitlesWindow.connectionPage.apiKey);

        LoginTask loginTask = new LoginTask(openSubtitlesWindow.os, true);

        loginTask.setOnSucceeded(e -> {
            content.setAlignment(Pos.TOP_LEFT);
            content.getChildren().clear();

            Pair<Integer, UserResult.Data> result = loginTask.getValue();

            Integer status = result.getKey();

            if(status == -1){
                errorLabel.setText("Failed to login");
                content.getChildren().add(errorLabel);
            }
            else if(status == 400){
                errorLabel.setText("Error 400: invalid username/password - remember to use your username and not your email to authenticate");
                content.getChildren().add(errorLabel);
            }
            else if(status == 401){
                errorLabel.setText("Error 401: incorrect username/password");
                content.getChildren().add(errorLabel);
            }
            else if(status != 200){
                errorLabel.setText("Error " + status + ": failed to login");
                content.getChildren().add(errorLabel);
            }
            else {
                UserResult.Data data = result.getValue();

                if(data != null) {
                    createKeyValueLabel("Username", openSubtitlesWindow.connectionPage.username.getValue());
                    createKeyValueLabel("Rank", data.level);
                    createKeyValueLabel("Allowed downloads", String.valueOf(data.allowed_downloads));
                    createKeyValueLabel("Remaining downloads", String.valueOf(data.remaining_downloads));
                }
                else {
                    errorLabel.setText("Failed to load profile");
                    content.getChildren().add(errorLabel);
                }
            }

        });

        loginTask.setOnFailed(e -> {
            content.setAlignment(Pos.TOP_LEFT);
            content.getChildren().clear();

            errorLabel.setText("Failed to login");
            content.getChildren().add(errorLabel);
        });

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(loginTask);
        executorService.shutdown();
    }

    public void reset(){
        this.setOpacity(0);
        this.setVisible(false);
        scrollPane.setVvalue(0);

        content.setAlignment(Pos.BOTTOM_CENTER);
        content.getChildren().clear();
        content.getChildren().add(spinner);
    }

    private void createKeyValueLabel(String key, String value){
        Label keyLabel = new Label(key + ":");
        keyLabel.setMinWidth(240);
        keyLabel.setMaxWidth(240);
        keyLabel.getStyleClass().add("toggleText");


        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("toggleText");

        HBox box = new HBox(keyLabel, valueLabel);
        box.setAlignment(Pos.CENTER_LEFT);

        content.getChildren().add(box);
    }

    @Override
    public boolean focusForward(){
        if(focus.get() == focusNodes.size() -1) return true;

        keyboardFocusOn(focusNodes.get(focus.get() + 1));

        return false;
    }

    @Override
    public boolean focusBackward(){
        if(focus.get() == 0) return true;

        if(focus.get() == -1) keyboardFocusOn(focusNodes.get(focusNodes.size() - 1));
        else keyboardFocusOn(focusNodes.get(focus.get() - 1));

        return false;
    }

    private void updatePadding(boolean value){
        if(value) content.setPadding(new Insets(15, 8, 15, 15));
        else      content.setPadding(new Insets(15, 20, 15, 15));
    }
}
