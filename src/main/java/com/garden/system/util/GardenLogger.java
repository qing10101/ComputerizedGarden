package com.garden.system.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class GardenLogger {
    private static final String LOG_FILE = "log.txt";
    // A 'listener' that the UI can hook into
    private static Consumer<String> uiCallback;

    public static void setUiCallback(Consumer<String> callback) {
        uiCallback = callback;
    }

    public static void log(String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String logEntry = String.format("[%s] %s", timestamp, message);

        System.out.println(logEntry); // Console

        // Write to File
        try (FileWriter fw = new FileWriter(LOG_FILE, true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println(logEntry);
        } catch (IOException e) {
            System.err.println("CRITICAL: Failed to write to log file: " + e.getMessage());
        }

        // Send to UI if connected
        if (uiCallback != null) {
            // Ensure UI updates happen on the correct thread
            javafx.application.Platform.runLater(() -> uiCallback.accept(logEntry));
        }
    }
}

