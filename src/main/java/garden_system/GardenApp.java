package garden_system;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.List;

public class GardenApp extends Application {

    private static TilePane gardenGrid; // Use TilePane for Grid Layout
    private static Label statusLabel;

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        // --- Load CSS ---
        // This line loads the style.css file from your resources folder
        String css = getClass().getResource("/style.css").toExternalForm();
        root.getStylesheets().add(css);

        // --- Header ---
        Label title = new Label("🌿 Computerized Garden System");
        title.getStyleClass().add("header-label");

        statusLabel = new Label("System Ready. Waiting for initialization...");
        statusLabel.setStyle("-fx-text-fill: #555; -fx-font-style: italic;");

        VBox header = new VBox(10, title, statusLabel);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(0, 0, 20, 0));
        root.setTop(header);

        // --- Main Garden Grid Area ---
        gardenGrid = new TilePane();
        gardenGrid.setHgap(20);
        gardenGrid.setVgap(20);
        gardenGrid.setPrefColumns(4); // Try to fit 4 cards per row
        gardenGrid.setAlignment(Pos.TOP_CENTER);
        gardenGrid.setPadding(new Insets(10));

        ScrollPane scrollPane = new ScrollPane(gardenGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        root.setCenter(scrollPane);

        // --- Control Panel ---
        HBox controls = new HBox(15);
        controls.setAlignment(Pos.CENTER);
        controls.setPadding(new Insets(20, 0, 0, 0));

        GertenSimulationAPI api = new GertenSimulationAPI();

        Button btnInit = createStyledButton("🌱 Initialize", "btn-init");
        btnInit.setOnAction(e -> api.initializeGarden());

        Button btnRain = createStyledButton("🌧 Rain", "btn-rain");
        btnRain.setOnAction(e -> api.rain(10));

        Button btnHot = createStyledButton("🔥 Heat Wave", "btn-sun");
        btnHot.setOnAction(e -> api.temperature(110));

        Button btnPest = createStyledButton("🐛 Pest Attack", "btn-pest");
        btnPest.setOnAction(e -> api.parasite("aphids"));

        Button btnReport = createStyledButton("📋 Log State", "btn-log");
        btnReport.setOnAction(e -> api.getState());

        controls.getChildren().addAll(btnInit, btnRain, btnHot, btnPest, btnReport);
        root.setBottom(controls);

        Scene scene = new Scene(root, 900, 700); // Larger window for grid
        primaryStage.setTitle("Garden Simulation Team 3");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Button createStyledButton(String text, String styleClass) {
        Button btn = new Button(text);
        btn.getStyleClass().add(styleClass);
        return btn;
    }

    // --- Static Method to Update UI from API ---
    public static void refreshUI() {
        Platform.runLater(() -> {
            gardenGrid.getChildren().clear();
            List<Plant> plants = GardenManager.getInstance().getPlants();

            if (plants.isEmpty()) {
                statusLabel.setText("Garden is empty. Click Initialize.");
                return;
            }

            statusLabel.setText("Garden Active: " + plants.size() + " plants growing.");

            for (Plant p : plants) {
                gardenGrid.getChildren().add(createPlantCard(p));
            }
        });
    }

    // --- Helper to Create a Colorful Plant Card ---
    private static VBox createPlantCard(Plant p) {
        VBox card = new VBox(8);
        card.getStyleClass().add("plant-card");

        // 1. Icon (Emoji based on name)
        Label icon = new Label(getPlantEmoji(p.getName()));
        icon.getStyleClass().add("plant-icon");

        // 2. Name
        Label name = new Label(p.getName());
        name.getStyleClass().add("plant-name");

        // 3. Health Bar
        ProgressBar healthBar = new ProgressBar(p.getHealth() / 100.0);
        healthBar.setPrefWidth(120);
        // Dynamic color based on health
        if (p.getHealth() > 70) healthBar.getStyleClass().add("health-bar-high");
        else if (p.getHealth() > 30) healthBar.getStyleClass().add("health-bar-med");
        else healthBar.getStyleClass().add("health-bar-low");

        Label healthLbl = new Label("Health: " + p.getHealth() + "%");
        healthLbl.getStyleClass().add("status-text");

        // 4. Water Level
        Label waterLbl = new Label("💧 Water: " + p.getCurrentWaterLevel() + " / " + p.getWaterRequirement());
        waterLbl.getStyleClass().add("status-text");
        if(Math.abs(p.getCurrentWaterLevel() - p.getWaterRequirement()) > 10) {
            waterLbl.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        }

        // 5. Alive/Dead Status
        if (!p.isAlive()) {
            card.setStyle("-fx-background-color: #cfd8dc; -fx-opacity: 0.7; -fx-background-radius: 15;");
            icon.setText("💀");
            name.setText(p.getName() + " (Dead)");
        }

        card.getChildren().addAll(icon, name, healthBar, healthLbl, waterLbl);
        return card;
    }

    private static String getPlantEmoji(String name) {
        String lower = name.toLowerCase();
        if (lower.contains("rose")) return "🌹";
        if (lower.contains("tomato")) return "🍅";
        if (lower.contains("basil")) return "🌿";
        if (lower.contains("cactus")) return "🌵";
        if (lower.contains("sunflower")) return "🌻";
        return "🌱"; // Default
    }

    public static void main(String[] args) {
        launch(args);
    }
}