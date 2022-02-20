module hans {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.media;
    requires javafx.base;
    requires com.jfoenix;
    requires animated;
    requires AnimateFX;

    opens hans to javafx.graphics, javafx.fxml;
    exports hans;
}