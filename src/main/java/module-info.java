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
    requires com.sandec.mdfx;


    opens hans to javafx.graphics, javafx.fxml;
    exports hans;
    exports hans.MediaItems;
    opens hans.MediaItems to javafx.fxml, javafx.graphics;
    exports hans.PlaybackSettings;
    opens hans.PlaybackSettings to javafx.fxml, javafx.graphics;
    exports hans.Menu;
    opens hans.Menu to javafx.fxml, javafx.graphics;
    exports hans.Menu.MetadataEdit;
    opens hans.Menu.MetadataEdit to javafx.fxml, javafx.graphics;
    exports hans.Subtitles;
    opens hans.Subtitles to javafx.fxml, javafx.graphics;
    exports hans.windowstoolbar;
    exports hans.Chapters;
    opens hans.Chapters to javafx.fxml, javafx.graphics;
    exports hans.Subtitles.Tasks;
    opens hans.Subtitles.Tasks to javafx.fxml, javafx.graphics;
    exports hans.Menu.Queue;
    opens hans.Menu.Queue to javafx.fxml, javafx.graphics;
    exports hans.Menu.Settings;
    opens hans.Menu.Settings to javafx.fxml, javafx.graphics;
    exports hans.SRTParser.srt;
    exports hans.Dialogs;
    opens hans.Dialogs to javafx.fxml, javafx.graphics;
}