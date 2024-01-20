package fxplayer;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.MenuButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import fxplayer.windows.openSubtitles.Language;

public class MultiSelectButton extends MenuButton {

    MainController mainController;

    ObservableList<String> selectedItems = FXCollections.observableArrayList();

    CustomMenuItem customMenuItem = new CustomMenuItem();
    public ScrollPane scrollPane = new ScrollPane();
    VBox content = new VBox();

    public MultiSelectButton(MainController mainController, String text){

        this.mainController = mainController;

        this.setPrefWidth(200);
        this.setMaxWidth(200);
        this.setFocusTraversable(false);
        this.setText(text);
        this.getStyleClass().add("multi-select-button");

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setPrefSize(187, 258);
        scrollPane.setMaxSize(187, 258);
        scrollPane.setBackground(Background.EMPTY);
        scrollPane.setContent(content);
        scrollPane.getStyleClass().add("context-menu-scroll");

        content.setFillWidth(true);
        content.setBackground(Background.EMPTY);

        customMenuItem.setContent(scrollPane);
        customMenuItem.setHideOnClick(false);

        this.getItems().add(customMenuItem);


        selectedItems.addListener((ListChangeListener<String>) change -> {

            if(selectedItems.isEmpty()) this.setText("Languages");
            else {
                if(selectedItems.size() >= 5){
                    this.setText(selectedItems.size() + " selected");
                }
                else if(selectedItems.size() == 1){
                    this.setText(selectedItems.get(0));
                }
                else {
                    StringBuilder newTitle = new StringBuilder();
                    for (int i = 0; i < selectedItems.size(); i++) {
                        String languageName = selectedItems.get(i);
                        Language language = Language.get(languageName);
                        String languageCode = language.getThreeLetterCode();
                        if (i < selectedItems.size() - 1) {
                            newTitle.append(languageCode).append(", ");
                        } else {
                            newTitle.append(languageCode);
                        }
                    }

                    this.setText(newTitle.toString());
                }
            }
        });

        this.setOnMouseClicked(e -> this.requestFocus());

        this.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            this.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        });

        this.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() != KeyCode.SPACE) return;
            this.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        });
    }

    public void addItem(String string){

        content.getChildren().add(new MultiSelectItem(this, string));

    }

    public void select(String value){
        for(Node node : content.getChildren()){
            MultiSelectItem multiSelectItem = (MultiSelectItem) node;
            if(multiSelectItem.value.equals(value)) multiSelectItem.checkBox.setSelected(true);
        }
    }

    public ObservableList<String> getSelectedItems(){
        return selectedItems;
    }

}
