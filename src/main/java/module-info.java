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

    opens hans to javafx.graphics, javafx.fxml;
    exports hans;
}