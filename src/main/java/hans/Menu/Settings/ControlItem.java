package hans.Menu.Settings;

import hans.App;
import hans.Captions.CaptionsState;
import hans.ControlTooltip;
import hans.HotkeyChangeWindow;
import hans.SVG;
import hans.Settings.SettingsState;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;


import java.util.Map;

public class ControlItem extends StackPane {

    public ControlsSection controlsSection;

    SVGPath editSVG = new SVGPath();
    Region editIcon = new Region();
    Button editButton = new Button();
    ControlTooltip editTooltip;

    public Action action;
    StackPane actionPane = new StackPane();
    Label actionLabel = new Label();

    StackPane keybindPane = new StackPane();
    public HBox keybindBox = new HBox();

    public static final Map<KeyCode, String> symbols = Map.ofEntries(
            Map.entry(KeyCode.RIGHT, "\u2192"),
            Map.entry(KeyCode.UP, "\u2191"),
            Map.entry(KeyCode.LEFT, "\u2190"),
            Map.entry(KeyCode.DOWN, "\u2193"),
            Map.entry(KeyCode.COMMA, ","),
            Map.entry(KeyCode.PERIOD, "."),
            Map.entry(KeyCode.SLASH, "/"),
            Map.entry(KeyCode.BACK_SLASH, "\\"),
            Map.entry(KeyCode.QUOTE, "'"),
            Map.entry(KeyCode.SEMICOLON, ";"),
            Map.entry(KeyCode.COLON, ":"),
            Map.entry(KeyCode.EQUALS, "="),
            Map.entry(KeyCode.MINUS, "-"),
            Map.entry(KeyCode.PLUS, "+"),
            Map.entry(KeyCode.AMPERSAND, "&"),
            Map.entry(KeyCode.OPEN_BRACKET, "["),
            Map.entry(KeyCode.CLOSE_BRACKET, "]"),
            Map.entry(KeyCode.BACK_QUOTE, "`"),
            Map.entry(KeyCode.LEFT_PARENTHESIS, "("),
            Map.entry(KeyCode.RIGHT_PARENTHESIS, ")")
        );

    ControlItem(ControlsSection controlsSection, Action action, KeyCode[] keyCodes, boolean isOdd){

        this.controlsSection = controlsSection;

        this.action = action;
        this.getChildren().addAll(editButton, actionPane, keybindPane);
        this.setMinHeight(47);


        this.setPadding(new Insets(7, 5, 7, 5));
        if(isOdd) this.getStyleClass().add("controlItemOdd");

        this.setOnMouseEntered(e -> editButton.setVisible(true));
        this.setOnMouseExited(e -> editButton.setVisible(false));

        editSVG.setContent(App.svgMap.get(SVG.EDIT));
        editIcon.setShape(editSVG);
        editIcon.setMouseTransparent(true);
        editIcon.setPrefSize(19, 19);
        editIcon.setMaxSize(19, 19);
        editIcon.getStyleClass().add("graphic");

        editButton.setCursor(Cursor.HAND);
        editButton.getStyleClass().add("transparentButton");
        editButton.setGraphic(editIcon);
        editButton.setVisible(false);
        editButton.setOnAction(e -> {
            if(controlsSection.settingsPage.menuController.captionsController.captionsState != CaptionsState.CLOSED) controlsSection.settingsPage.menuController.captionsController.closeCaptions();
            if(controlsSection.settingsPage.menuController.settingsController.settingsState != SettingsState.CLOSED) controlsSection.settingsPage.menuController.settingsController.closeSettings();

            openKeyBindEditScreen();
        });

        StackPane.setAlignment(editButton, Pos.CENTER_LEFT);

        StackPane.setAlignment(actionPane, Pos.CENTER_LEFT);
        StackPane.setMargin(actionPane, new Insets(0, 0, 0, 40));

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

        Platform.runLater(() -> editTooltip = new ControlTooltip(controlsSection.settingsPage.menuController.mainController,"Edit hotkey", editButton, 1000));
    }

    public void loadKeyLabel(KeyCode[] keyCodes){

        for (KeyCode keyCode : keyCodes) {
            Label keyLabel;
            keyLabel = new Label(symbols.getOrDefault(keyCode, keyCode.getName()));
            keyLabel.getStyleClass().add("keycap");
            if(keyCode.equals(KeyCode.SHIFT)) keyLabel.setMinWidth(50);

            StackPane keycapContainer = new StackPane();
            keycapContainer.getStyleClass().add("keycapContainer");
            keycapContainer.getChildren().add(keyLabel);
            keycapContainer.setPadding(new Insets(0, 0, 4, 0));
            keycapContainer.setBackground(new Background(new BackgroundFill(Color.rgb(55, 55, 55), new CornerRadii(6), Insets.EMPTY)));
            HBox.setHgrow(keycapContainer, Priority.NEVER);

            Label plus = new Label("+");
            plus.setMinWidth(15);
            HBox.setHgrow(plus, Priority.NEVER);
            plus.getStyleClass().add("toggleText");

            keybindBox.getChildren().addAll(keycapContainer, plus);
        }

        if(!keybindBox.getChildren().isEmpty()) keybindBox.getChildren().remove(keybindBox.getChildren().size() - 1);
    }

    private void openKeyBindEditScreen(){
        HotkeyChangeWindow hotkeyChangeWindow = controlsSection.settingsPage.menuController.mainController.hotkeyChangeWindow;

        hotkeyChangeWindow.show(this);
    }
}
