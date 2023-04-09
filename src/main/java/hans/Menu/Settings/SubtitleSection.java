package hans.Menu.Settings;


import hans.App;
import hans.SVG;
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

import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class SubtitleSection extends VBox {

    SettingsPage settingsPage;

    Label subtitleSectionTitle = new Label("Subtitles");

    StackPane openSubtitlesSectionWrapper = new StackPane();
    VBox openSubtitlesSection = new VBox();
    StackPane openSubtitlesSectionTitlePane = new StackPane();
    Label openSubtitlesSectionTitle = new Label("OpenSubtitles connection");
    Label openSubtitlesInfoLabel = new Label();
    SVGPath infoSVG = new SVGPath();
    Region infoIcon = new Region();

    VBox openSubtitlesSectionInnerContainer = new VBox();

    HBox usernameBox = new HBox();
    Label usernameLabel = new Label("Username: ");
    TextField usernameField = new TextField();

    HBox passwordBox = new HBox();
    Label passwordLabel = new Label("Password:");
    PasswordField passwordField = new PasswordField();

    StackPane openSubtitlesFooterPane = new StackPane();
    Label createAccountLabel = new Label("Create account");
    Button testConnectionButton = new Button("Test connection");

    SubtitleSection(SettingsPage settingsPage){
        this.settingsPage = settingsPage;

        this.getChildren().addAll(subtitleSectionTitle, openSubtitlesSectionWrapper);
        this.setSpacing(25);

        openSubtitlesSectionWrapper.getChildren().add(openSubtitlesSection);
        openSubtitlesSectionWrapper.setPadding(new Insets(15, 20, 15, 20));
        openSubtitlesSectionWrapper.getStyleClass().add("highlightedSection");


        openSubtitlesSection.getChildren().addAll(openSubtitlesSectionTitlePane, openSubtitlesSectionInnerContainer);


        subtitleSectionTitle.getStyleClass().add("settingsSectionTitle");

        openSubtitlesSection.setSpacing(20);

        openSubtitlesSectionTitlePane.setAlignment(Pos.CENTER_LEFT);
        openSubtitlesSectionTitlePane.getChildren().addAll(openSubtitlesSectionTitle, openSubtitlesInfoLabel);

        StackPane.setAlignment(openSubtitlesInfoLabel, Pos.CENTER_RIGHT);

        infoSVG.setContent(App.svgMap.get(SVG.INFORMATION_OUTLINE));

        infoIcon.setShape(infoSVG);
        infoIcon.setPrefSize(25, 25);
        infoIcon.setMaxSize(25, 25);
        infoIcon.getStyleClass().add("infoIcon");

        openSubtitlesInfoLabel.setGraphic(infoIcon);
        openSubtitlesInfoLabel.getStyleClass().add("infoLabel");


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

    }
}
