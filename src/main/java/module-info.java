module hans {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.media;
    requires javafx.base;
    requires jcodec;
    requires MaterialFX;
    requires com.jfoenix;
    requires org.apache.logging.log4j;
    requires FX.BorderlessScene;

    opens hans to javafx.graphics, javafx.fxml;
    exports hans;
}