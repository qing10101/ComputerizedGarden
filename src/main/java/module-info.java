module com.garden.system {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop; // Sometimes needed for logging

    // This grants JavaFX permission to run your GUI
    opens garden_system to javafx.fxml;
    exports garden_system;
}