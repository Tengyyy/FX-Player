package hans.Menu.Settings;


import hans.App;
import hans.SVG;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class SubtitleSection extends VBox {

    SettingsPage settingsPage;

    Label subtitleSectionTitle = new Label("Subtitles");

    VBox toggleContainer = new VBox();
    Toggle extrationToggle;
    BooleanProperty extractionOn = new SimpleBooleanProperty();
    Toggle searchToggle;
    BooleanProperty searchOn = new SimpleBooleanProperty();

    StackPane openSubtitlesSectionWrapper = new StackPane();
    VBox openSubtitlesSection = new VBox();
    StackPane openSubtitlesSectionTitlePane = new StackPane();
    Label openSubtitlesSectionTitle = new Label("OpenSubtitles connection");
    Label infoToggle = new Label();
    SVGPath infoSVG = new SVGPath();
    Region infoIcon = new Region();
    Label infoLabel = new Label("Connect FXPlayer to OpenSubtitles to conveniently search for and download matching subtitles for your media files.");

    VBox openSubtitlesSectionInnerContainer = new VBox();

    HBox usernameBox = new HBox();
    Label usernameLabel = new Label("Username:");
    TextField usernameField = new TextField();

    HBox passwordBox = new HBox();
    Label passwordLabel = new Label("Password:");
    PasswordField passwordField = new PasswordField();

    StackPane openSubtitlesFooterPane = new StackPane();
    Label createAccountLabel = new Label("Create account");
    Button testConnectionButton = new Button("Test connection");

    SubtitleSection(SettingsPage settingsPage){
        this.settingsPage = settingsPage;

        this.getChildren().addAll(subtitleSectionTitle, toggleContainer, openSubtitlesSectionWrapper);
        this.setSpacing(25);

        toggleContainer.setPadding(new Insets(0, 0, 10, 0));
        toggleContainer.setSpacing(10);

        extrationToggle = new Toggle("Extract subtitles embedded into media file containers", extractionOn);
        searchToggle = new Toggle("Scan parent folder for subtitle file with matching name", searchOn);

        toggleContainer.getChildren().addAll(extrationToggle, searchToggle);

        openSubtitlesSectionWrapper.getChildren().addAll(openSubtitlesSection, infoLabel);
        openSubtitlesSectionWrapper.setPadding(new Insets(15, 20, 15, 20));
        openSubtitlesSectionWrapper.getStyleClass().add("highlightedSection");


        openSubtitlesSection.getChildren().addAll(openSubtitlesSectionTitlePane, openSubtitlesSectionInnerContainer);


        subtitleSectionTitle.getStyleClass().add("settingsSectionTitle");

        openSubtitlesSection.setSpacing(20);

        openSubtitlesSectionTitlePane.setAlignment(Pos.CENTER_LEFT);
        openSubtitlesSectionTitlePane.getChildren().addAll(openSubtitlesSectionTitle, infoToggle);

        StackPane.setAlignment(infoToggle, Pos.CENTER_RIGHT);

        infoSVG.setContent(App.svgMap.get(SVG.INFORMATION_OUTLINE));

        infoIcon.setShape(infoSVG);
        infoIcon.setPrefSize(25, 25);
        infoIcon.setMaxSize(25, 25);
        infoIcon.getStyleClass().add("infoIcon");

        infoToggle.setGraphic(infoIcon);
        infoToggle.getStyleClass().add("infoLabel");
        infoToggle.setOnMouseEntered(e -> infoLabel.setVisible(true));
        infoToggle.setOnMouseExited(e -> infoLabel.setVisible(false));

        infoLabel.setVisible(false);
        infoLabel.setMouseTransparent(true);
        infoLabel.getStyleClass().add("settingsInfoWindow");
        infoLabel.setWrapText(true);
        infoLabel.setPrefSize(280, 80);
        infoLabel.setMaxSize(280, 80);
        infoLabel.setPadding(new Insets(5, 10, 5, 10));

        StackPane.setAlignment(infoLabel, Pos.TOP_RIGHT);
        StackPane.setMargin(infoLabel, new Insets(35, 0, 0, 0));


        openSubtitlesSectionTitle.getStyleClass().add("settingsSubsectionTitle");

        openSubtitlesSectionInnerContainer.getChildren().addAll(usernameBox, passwordBox, openSubtitlesFooterPane);
        openSubtitlesSectionInnerContainer.setSpacing(15);

        usernameBox.getChildren().addAll(usernameLabel, usernameField);

        usernameLabel.setPrefWidth(150);
        usernameLabel.getStyleClass().add("settingsText");

        usernameField.setPrefWidth(300);
        usernameField.getStyleClass().add("customTextField");
        usernameField.setPrefHeight(36);
        usernameField.setMinHeight(36);
        usernameField.setMaxHeight(36);

        passwordBox.getChildren().addAll(passwordLabel, passwordField);

        passwordLabel.setPrefWidth(150);
        passwordLabel.getStyleClass().add("settingsText");

        passwordField.setSkin(new VisiblePasswordFieldSkin(passwordField));
        passwordField.setPrefWidth(300);
        passwordField.getStyleClass().add("customTextField");
        passwordField.setPrefHeight(36);
        passwordField.setMinHeight(36);
        passwordField.setMaxHeight(36);

        openSubtitlesFooterPane.getChildren().addAll(createAccountLabel, testConnectionButton);
        openSubtitlesFooterPane.setAlignment(Pos.CENTER_LEFT);

        createAccountLabel.getStyleClass().addAll("settingsText", "settingsLink");
        createAccountLabel.setOnMouseClicked(e -> {
            // open opensubtitles account creation page in web browser
            if(Desktop.isDesktopSupported()){
                try {
                    Desktop.getDesktop().browse(new URL("https://www.opensubtitles.org/en/newuser").toURI());
                } catch (IOException | URISyntaxException ex) {
                    ex.printStackTrace();
                }
            }
        });


        StackPane.setAlignment(testConnectionButton, Pos.CENTER_RIGHT);
        testConnectionButton.getStyleClass().add("mainButton");
        testConnectionButton.disableProperty().bind(usernameField.textProperty().isEmpty().or(passwordField.textProperty().isEmpty()));

    }
}
