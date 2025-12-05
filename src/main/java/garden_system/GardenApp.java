package garden_system;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.List;

public class GardenApp extends Application {

    private static VBox plantContainer;
    private static Label statusLabel;

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        // --- Header ---
        Label title = new Label("Computerized Garden System");
        title.setFont(new Font("Arial", 24));
        statusLabel = new Label("System Ready. Waiting for API initialization...");
        VBox header = new VBox(10, title, statusLabel);
        header.setPadding(new Insets(0,0,20,0));
        root.setTop(header);

        // --- Plant Display Area ---
        plantContainer = new VBox(10);
        ScrollPane scrollPane = new ScrollPane(plantContainer);
        scrollPane.setFitToWidth(true);
        root.setCenter(scrollPane);

        // --- Manual Test Controls (To simulate the script manually) ---
        VBox controls = new VBox(10);
        controls.setPadding(new Insets(20,0,0,0));
        controls.setStyle("-fx-border-color: #ccc; -fx-padding: 10;");

        Label controlTitle = new Label("Manual Script Simulation (Debug)");
        controlTitle.setStyle("-fx-font-weight: bold;");

        GertenSimulationAPI api = new GertenSimulationAPI();

        Button btnInit = new Button("Initialize Garden");
        btnInit.setOnAction(e -> api.initializeGarden());

        HBox eventButtons = new HBox(10);
        Button btnRain = new Button("Rain (5 units)");
        btnRain.setOnAction(e -> api.rain(5));

        Button btnHot = new Button("Temp (110F)");
        btnHot.setOnAction(e -> api.temperature(110));

        Button btnPest = new Button("Pest (aphids)");
        btnPest.setOnAction(e -> api.parasite("aphids"));

        Button btnReport = new Button("Get State (Log)");
        btnReport.setOnAction(e -> api.getState());

        eventButtons.getChildren().addAll(btnInit, btnRain, btnHot, btnPest, btnReport);
        controls.getChildren().addAll(controlTitle, eventButtons);
        root.setBottom(controls);

        Scene scene = new Scene(root, 600, 500);
        primaryStage.setTitle("Garden Simulation Team 3");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Static method to allow the API to trigger UI updates
    public static void refreshUI() {
        Platform.runLater(() -> {
            plantContainer.getChildren().clear();
            List<Plant> plants = GardenManager.getInstance().getPlants();

            if (plants.isEmpty()) {
                statusLabel.setText("Garden Empty. Initialize via API.");
                return;
            }

            statusLabel.setText("Garden Active: " + plants.size() + " plants monitored.");

            for (Plant p : plants) {
                HBox card = new HBox(15);
                card.setPadding(new Insets(10));
                card.setStyle("-fx-border-color: #aaa; -fx-border-radius: 5; -fx-background-color: #f9f9f9;");

                Circle statusDot = new Circle(8);
                if (!p.isAlive()) statusDot.setFill(Color.BLACK); // Dead
                else if (p.getHealth() < 50) statusDot.setFill(Color.ORANGE); // Sick
                else statusDot.setFill(Color.GREEN); // Healthy

                VBox info = new VBox(5);
                Label nameLbl = new Label(p.getName());
                nameLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

                Label detailLbl = new Label(String.format("Health: %d%% | Water: %d/%d",
                        p.getHealth(), p.getCurrentWaterLevel(), p.getWaterRequirement()));

                Label vulnLbl = new Label("Vulnerable to: " + p.getVulnerableTo().toString());
                vulnLbl.setStyle("-fx-text-fill: #666; -fx-font-size: 10;");

                info.getChildren().addAll(nameLbl, detailLbl, vulnLbl);
                card.getChildren().addAll(statusDot, info);

                plantContainer.getChildren().add(card);
            }
        });
    }

    public static void main(String[] args) {
        // Ensure log is started
        GardenLogger.log("SYSTEM STARTUP");
        launch(args);
    }
}
