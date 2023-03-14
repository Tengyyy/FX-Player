module hans {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.media;
    requires javafx.base;
    requires MaterialFX;
    requires com.jfoenix;
    requires org.apache.logging.log4j;
    requires FX.BorderlessScene;
    requires uk.co.caprica.vlcj;
    requires uk.co.caprica.vlcj.javafx;
    requires com.sun.jna.platform;
    requires java.logging;
    requires jaffree;
    requires org.bytedeco.javacv;
    requires org.bytedeco.javacpp;
    requires org.bytedeco.ffmpeg;
    requires opensub4j;
    requires org.controlsfx.controls;
    requires xmlrpc.common;


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
    exports hans.Captions;
    opens hans.Captions to javafx.fxml, javafx.graphics;
    exports hans.windowstoolbar;
    exports hans.Chapters;
    opens hans.Chapters to javafx.fxml, javafx.graphics;
    exports hans.Captions.Tasks;
    opens hans.Captions.Tasks to javafx.fxml, javafx.graphics;
}