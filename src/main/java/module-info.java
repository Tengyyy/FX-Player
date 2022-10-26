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
    requires uk.co.caprica.vlcj;
    requires uk.co.caprica.vlcj.javafx;
    requires vlcj.natives;
    requires org.bytedeco.javacv;
    requires com.sun.jna.platform;
    requires com.sun.jna;

    opens hans to javafx.graphics, javafx.fxml;
    exports hans;
    exports hans.MediaItems;
    opens hans.MediaItems to javafx.fxml, javafx.graphics;
    exports hans.Settings;
    opens hans.Settings to javafx.fxml, javafx.graphics;
    exports hans.Menu;
    opens hans.Menu to javafx.fxml, javafx.graphics;
    exports hans.Menu.MetadataEdit;
    opens hans.Menu.MetadataEdit to javafx.fxml, javafx.graphics;
}