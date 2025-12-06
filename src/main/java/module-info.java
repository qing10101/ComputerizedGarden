module com.garden.system {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics; // Explicitly require for Stage and other graphics classes
    requires java.desktop; // Sometimes needed for logging

    // This grants JavaFX permission to run your GUI
    opens com.garden.system.ui to javafx.fxml;
    exports com.garden.system.model;
    exports com.garden.system.manager;
    exports com.garden.system.api;
    exports com.garden.system.ui;
    exports com.garden.system.util;
}
