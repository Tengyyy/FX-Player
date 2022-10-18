package hans.Menu;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class ExpandableTextArea extends TextArea {

    private final double DEFAULT_HEIGHT = 20.0;

    public ExpandableTextArea() {
        this.getStyleClass().add("expandable-text-area");
        setMinHeight(DEFAULT_HEIGHT);
        setPrefHeight(DEFAULT_HEIGHT);
        setMaxHeight(DEFAULT_HEIGHT);

        disableEnter();
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();


        setWrapText(true);
        setPadding(new Insets(0, 0, 0, 0));

        ScrollPane scrollPane = (ScrollPane)lookup(".scroll-pane");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setPadding(new Insets(0, 0, 0, 0));

        StackPane viewport = (StackPane) scrollPane.lookup(".viewport");
        viewport.setPadding(new Insets(0, 0, 0, 0));

        Region content = (Region) viewport.lookup(".content");
        content.setPadding(new Insets(-1, 1, 0, 1));

        Text text = (Text) content.lookup(".text");



        text.layoutBoundsProperty().addListener((observableBoundsAfter, boundsBefore, boundsAfter) -> {
            if(boundsBefore.getHeight() != boundsAfter.getHeight()){
                setMinHeight(boundsAfter.getHeight());
                setPrefHeight(boundsAfter.getHeight());
                setMaxHeight(boundsAfter.getHeight());

                Platform.runLater(this::requestLayout);
            }
        });
        /*text.textProperty().addListener((property) -> {

            double textHeight = text.getLayoutBounds().getHeight();

                oldTextHeight = textHeight;
                if (textHeight < DEFAULT_HEIGHT) {
                    textHeight = DEFAULT_HEIGHT;
                }

                textHeight = textHeight + 1;

                setMinHeight(textHeight);
                setPrefHeight(textHeight);
                setMaxHeight(textHeight);
        });

        this.widthProperty().addListener((observableValue, number, t1) -> {
            double textHeight = text.getLayoutBounds().getHeight();

            if(oldTextHeight != textHeight){
                oldTextHeight = textHeight;
                if (textHeight < DEFAULT_HEIGHT) {
                    textHeight = DEFAULT_HEIGHT;
                }

                textHeight = textHeight + 1;

                setMinHeight(textHeight);
                setPrefHeight(textHeight);
                setMaxHeight(textHeight);

                requestFocus();
            }
        });*/


    }

    private void disableEnter() {
        setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                event.consume();
            }
        });
    }
}