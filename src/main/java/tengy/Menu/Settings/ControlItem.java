package tengy.Menu.Settings;

import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import tengy.HotkeyController;
import tengy.PlaybackSettings.PlaybackSettingsState;
import tengy.PressableNode;
import tengy.SVG;
import tengy.Subtitles.SubtitlesState;

import static tengy.Utilities.keyboardFocusOff;

public class ControlItem extends PressableNode {

    public ControlsSection controlsSection;

    SVGPath editSVG = new SVGPath();
    Region editIcon = new Region();

    public Action action;
    StackPane actionPane = new StackPane();
    Label actionLabel = new Label();

    StackPane keybindPane = new StackPane();
    public HBox keybindBox = new HBox();

    boolean hover = false;
    boolean pressed = false;

    ControlItem(ControlsSection controlsSection, Action action, KeyCode[] keyCodes, int focusValue){

        this.controlsSection = controlsSection;

        this.action = action;
        this.getChildren().addAll(editIcon, actionPane, keybindPane);
        this.setMinHeight(47);
        this.getStyleClass().addAll("highlightedSection", "settingsToggle");
        this.setCursor(Cursor.HAND);

        this.setPadding(new Insets(10, 10, 10, 15));

        this.setOnMouseEntered(e -> {
            hover = true;
            editIcon.setVisible(true);
        });

        this.setOnMouseExited(e -> {
            hover = false;
            if(!this.isFocused()) editIcon.setVisible(false);
        });

        this.setOnMouseClicked(e -> {
            if(controlsSection.settingsPage.menuController.subtitlesController.subtitlesState != SubtitlesState.CLOSED) controlsSection.settingsPage.menuController.subtitlesController.closeSubtitles();
            if(controlsSection.settingsPage.menuController.playbackSettingsController.playbackSettingsState != PlaybackSettingsState.CLOSED) controlsSection.settingsPage.menuController.playbackSettingsController.closeSettings();

            this.requestFocus();

            openKeyBindEditScreen();

            e.consume();
        });

        this.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                controlsSection.focus.set(focusValue);
                controlsSection.settingsPage.focus.set(3);
                editIcon.setVisible(true);
            }
            else {
                keyboardFocusOff(this);
                controlsSection.focus.set(-1);
                controlsSection.settingsPage.focus.set(-1);
                if(!hover) editIcon.setVisible(false);
                pressed = false;
            }
        });

        this.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            this.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);

            pressed = true;

            e.consume();
        });

        this.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            this.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);

            if(pressed) openKeyBindEditScreen();

            pressed = false;

            e.consume();
        });

        editSVG.setContent(SVG.EDIT.getContent());
        editIcon.setShape(editSVG);
        editIcon.setMouseTransparent(true);
        editIcon.setPrefSize(19, 19);
        editIcon.setMaxSize(19, 19);
        editIcon.getStyleClass().add("menuIcon");
        editIcon.setVisible(false);
        StackPane.setAlignment(editIcon, Pos.CENTER_LEFT);


        StackPane.setAlignment(actionPane, Pos.CENTER_LEFT);
        StackPane.setMargin(actionPane, new Insets(0, 0, 0, 35));

        actionPane.getChildren().add(actionLabel);

        actionLabel.setText(action.getContent());
        actionLabel.getStyleClass().add("toggleText");
        actionLabel.prefWidthProperty().bind(this.widthProperty().subtract(60).subtract(keybindPane.widthProperty()));
        actionLabel.maxWidthProperty().bind(this.widthProperty().subtract(60).subtract(keybindPane.widthProperty()));
        StackPane.setAlignment(actionLabel, Pos.CENTER_LEFT);

        StackPane.setAlignment(keybindPane, Pos.CENTER_RIGHT);
        keybindPane.prefWidthProperty().bind(this.widthProperty().subtract(10).divide(2));
        keybindPane.maxWidthProperty().bind(this.widthProperty().subtract(10).divide(2));
        keybindPane.getChildren().add(keybindBox);

        StackPane.setAlignment(keybindBox, Pos.CENTER_RIGHT);
        keybindBox.setSpacing(5);
        keybindBox.setPadding(new Insets(0, 10, 0, 10));
        keybindBox.setAlignment(Pos.CENTER_LEFT);

        loadKeyLabel(keyCodes);
    }

    public void loadKeyLabel(KeyCode[] keyCodes){

        for (KeyCode keyCode : keyCodes) {
            Label keyLabel;
            keyLabel = new Label(HotkeyController.symbols.getOrDefault(keyCode, keyCode.getName()));
            keyLabel.getStyleClass().add("keycap");
            if(keyCode.equals(KeyCode.SHIFT)) keyLabel.setMinWidth(50);

            StackPane keycapContainer = new StackPane();
            keycapContainer.getStyleClass().add("keycapContainer");
            keycapContainer.getChildren().add(keyLabel);
            keycapContainer.setPadding(new Insets(0, 0, 4, 0));
            keycapContainer.setBackground(new Background(new BackgroundFill(Color.rgb(55, 55, 55), new CornerRadii(6), Insets.EMPTY)));
            keycapContainer.setAlignment(Pos.CENTER);
            HBox.setHgrow(keycapContainer, Priority.NEVER);

            Label plus = new Label("+");
            plus.setAlignment(Pos.CENTER);
            plus.setMinWidth(15);
            HBox.setHgrow(plus, Priority.NEVER);
            plus.getStyleClass().add("toggleText");

            keybindBox.getChildren().addAll(keycapContainer, plus);
        }

        if(!keybindBox.getChildren().isEmpty()) keybindBox.getChildren().remove(keybindBox.getChildren().size() - 1);
    }

    private void openKeyBindEditScreen(){
        controlsSection.settingsPage.menuController.mainController.windowController.hotkeyChangeWindow.show(this);
    }
}
