module com.main {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens com.main to javafx.fxml, javafx.base, javafx.graphics, javafx.controls;
    opens com.main.model to javafx.base;
    exports com.main.controller;
    exports com.main.utils.observer;
}