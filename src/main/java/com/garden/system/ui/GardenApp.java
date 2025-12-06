package com.garden.system.ui;

import com.garden.system.api.GertenSimulationAPI;
import com.garden.system.manager.GardenManager;
import com.garden.system.model.Plant;
import com.garden.system.util.GardenLogger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GardenApp extends Application {

    private static TilePane gardenGrid;
    private static TextArea logArea; // The new UI Log
    private static Label dayLabel;
    private static int dayCount = 1;

    private final GertenSimulationAPI api = new GertenSimulationAPI();
    private final Random random = new Random();

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));

        // --- Load CSS ---
        try {
            String css = getClass().getResource("/style.css").toExternalForm();
            root.getStylesheets().add(css);
        } catch (Exception e) { /* Ignore */ }

        // --- 1. SETUP LOGGING ---
        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setWrapText(true);
        logArea.setStyle("-fx-font-family: 'Consolas', monospace; -fx-font-size: 11px;");
        // Connect the Logger class to this Text Area
        GardenLogger.setUiCallback(msg -> logArea.appendText(msg + "\n"));

        // --- 2. HEADER ---
        Label title = new Label("🌿 Computerized Garden System");
        title.getStyleClass().add("header-label");
        dayLabel = new Label("Day: " + dayCount);
        dayLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2e7d32;");
        VBox header = new VBox(5, title, dayLabel);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(0, 0, 15, 0));
        root.setTop(header);

        // --- 3. LEFT SIDEBAR (Planting) ---
        VBox sidebar = createPlantingSidebar();
        root.setLeft(sidebar);

        // --- 4. CENTER (SplitPane: Garden Grid + Log) ---
        gardenGrid = new TilePane();
        gardenGrid.setHgap(15);
        gardenGrid.setVgap(15);
        gardenGrid.setPrefColumns(3);
        gardenGrid.setPadding(new Insets(10));
        gardenGrid.setAlignment(Pos.TOP_LEFT);

        ScrollPane gridScroll = new ScrollPane(gardenGrid);
        gridScroll.setFitToWidth(true);
        gridScroll.setStyle("-fx-background-color: transparent;");

        // Container for Log (Added Style for background opacity)
        VBox logContainer = new VBox(5, new Label("📋 System Activity Log"), logArea);
        logContainer.setPadding(new Insets(10));
        // NEW: Semi-transparent white background for the log area
        logContainer.setStyle("-fx-background-color: rgba(255, 255, 255, 0.85); -fx-background-radius: 10;");

        VBox.setVgrow(logArea, Priority.ALWAYS);

        // SplitPane lets user resize between Grid and Log
        SplitPane splitPane = new SplitPane();
        splitPane.setStyle("-fx-background-color: transparent;"); // FORCE TRANSPARENCY
        splitPane.setOrientation(Orientation.VERTICAL);
        splitPane.getItems().addAll(gridScroll, logContainer);
        splitPane.setDividerPositions(0.7); // 70% Grid, 30% Log

        root.setCenter(splitPane);

        // --- 5. BOTTOM CONTROLS ---
        HBox controls = createSimulationControls();
        root.setBottom(controls);

        Scene scene = new Scene(root, 1100, 800);
        primaryStage.setTitle("Garden Simulation Team 3");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Initial Log
        GardenLogger.log("SYSTEM STARTUP: Ready for simulation.");
    }

    // --- Sidebar ---
    private VBox createPlantingSidebar() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(15));
        box.setPrefWidth(240);
        box.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 1);");

        Label sideTitle = new Label("🌱 New Plant");
        sideTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TextField nameInput = new TextField();
        nameInput.setPromptText("Plant Name");

        ComboBox<String> typeSelect = new ComboBox<>();
        typeSelect.getItems().addAll("Rose", "Tomato", "Basil", "Cactus", "Sunflower", "Corn", "Tulip");
        typeSelect.setPromptText("Select Type");
        typeSelect.setMaxWidth(Double.MAX_VALUE);

        Button addBtn = new Button("Plant It");
        addBtn.getStyleClass().add("btn-init");
        addBtn.setMaxWidth(Double.MAX_VALUE);

        addBtn.setOnAction(e -> {
            String name = nameInput.getText();
            String type = typeSelect.getValue();

            if (name.isEmpty() || type == null) {
                GardenLogger.log("UI: Cannot plant without Name and Type.");
                return;
            }

            // Create Plant based on Type
            Plant newPlant = createPlantByType(name, type);
            GardenManager.getInstance().addPlant(newPlant);
            GardenLogger.log("UI: Planted new " + type + " named '" + name + "'");
            nameInput.clear();
            refreshUI();
        });

        box.getChildren().addAll(sideTitle, new Label("Name:"), nameInput, new Label("Type:"), typeSelect, addBtn);
        return box;
    }

    private Plant createPlantByType(String name, String type) {
        // Define stats based on type
        switch (type) {
            case "Rose": return new Plant(name, type, 10, Arrays.asList("aphids", "beetles"));
            case "Tomato": return new Plant(name, type, 15, Arrays.asList("worms", "blight"));
            case "Basil": return new Plant(name, type, 8, Arrays.asList("slugs"));
            case "Cactus": return new Plant(name, type, 2, Arrays.asList("rot"));
            case "Sunflower": return new Plant(name, type, 12, Arrays.asList("birds"));
            case "Corn": return new Plant(name, type, 20, Arrays.asList("locusts"));
            case "Tulip": return new Plant(name, type, 10, Arrays.asList("aphids"));
            default: return new Plant(name, "Generic", 10, Arrays.asList("bugs"));
        }
    }

    // --- Controls ---
    private HBox createSimulationControls() {
        HBox controls = new HBox(15);
        controls.setAlignment(Pos.CENTER);
        controls.setPadding(new Insets(15, 0, 0, 0));

        Button btnSimulate = new Button("🌞 Simulate Full Day");
        btnSimulate.setStyle("-fx-background-color: #673ab7; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        btnSimulate.setOnAction(e -> simulateDayCycle());

        Button btnRain = createStyledButton("🌧 Rain", "btn-rain");
        btnRain.setOnAction(e -> api.rain(10));

        Button btnHot = createStyledButton("🔥 Heat", "btn-sun");
        btnHot.setOnAction(e -> api.temperature(105));

        Button btnPest = createStyledButton("🐛 Pest", "btn-pest");
        btnPest.setOnAction(e -> api.parasite("aphids"));

        controls.getChildren().addAll(btnSimulate, new Separator(), btnRain, btnHot, btnPest);
        return controls;
    }

    private Button createStyledButton(String text, String styleClass) {
        Button btn = new Button(text);
        btn.getStyleClass().add(styleClass);
        return btn;
    }

    // --- Simulation Logic ---
    private void simulateDayCycle() {
        dayCount++;
        dayLabel.setText("Day: " + dayCount);
        GardenLogger.log("--- STARTING DAY " + dayCount + " ---");

        int randomTemp = 40 + random.nextInt(70);
        api.temperature(randomTemp);

        if (random.nextInt(100) < 30) {
            int rain = 5 + random.nextInt(10);
            api.rain(rain);
        } else {
            GardenLogger.log("WEATHER: Sunny day.");
        }

        if (random.nextInt(100) < 20) {
            String[] pests = {"aphids", "worms", "slugs", "rot", "locusts"};
            api.parasite(pests[random.nextInt(pests.length)]);
        }

        refreshUI();
    }

    // --- Render Cards ---
    public static void refreshUI() {
        Platform.runLater(() -> {
            gardenGrid.getChildren().clear();
            List<Plant> plants = GardenManager.getInstance().getPlants();

            if (plants.isEmpty()) {
                Label empty = new Label("Garden Empty.");
                empty.setStyle("-fx-font-size: 18px; -fx-text-fill: #aaa;");
                gardenGrid.getChildren().add(empty);
                return;
            }

            for (Plant p : plants) {
                gardenGrid.getChildren().add(createPlantCard(p));
            }
        });
    }

    private static VBox createPlantCard(Plant p) {
        VBox card = new VBox(5);
        card.getStyleClass().add("plant-card");

        // ICON LOGIC: Now uses p.getType() instead of Name
        Label icon = new Label(getPlantEmoji(p.getType()));
        icon.getStyleClass().add("plant-icon");

        Label name = new Label(p.getName());
        name.getStyleClass().add("plant-name");

        Label typeLbl = new Label("(" + p.getType() + ")");
        typeLbl.setStyle("-fx-font-size: 10px; -fx-text-fill: #777;");

        ProgressBar healthBar = new ProgressBar(p.getHealth() / 100.0);
        healthBar.setPrefWidth(120);
        if (p.getHealth() > 70) healthBar.getStyleClass().add("health-bar-high");
        else if (p.getHealth() > 30) healthBar.getStyleClass().add("health-bar-med");
        else healthBar.getStyleClass().add("health-bar-low");

        // Show pest status if infested
        String statusText = "Health: " + p.getHealth() + "%\nWater: " + p.getCurrentWaterLevel() + "/" + p.getWaterRequirement();
        if (p.hasPest()) {
            statusText += "\n⚠️ Infested: " + p.getCurrentPest();
        }
        Label detail = new Label(statusText);
        detail.getStyleClass().add("status-text");

        if (!p.isAlive()) {
            card.setStyle("-fx-background-color: #cfd8dc; -fx-opacity: 0.7; -fx-background-radius: 15;");
            icon.setText("💀");
            name.setText(p.getName() + " (Dead)");
        } else {
            // Add click handler for alive plants
            card.setOnMouseClicked(e -> showPlantActionsDialog(p));
            card.setStyle("-fx-cursor: hand;"); // Show hand cursor on hover
        }

        card.getChildren().addAll(icon, name, typeLbl, healthBar, detail);
        return card;
    }

    private static String getPlantEmoji(String type) {
        if (type == null) return "🌱";
        switch (type) {
            case "Rose": return "🌹";
            case "Tomato": return "🍅";
            case "Basil": return "🌿";
            case "Cactus": return "🌵";
            case "Sunflower": return "🌻";
            case "Corn": return "🌽";
            case "Tulip": return "🌷";
            default: return "🌱";
        }
    }

    // --- Plant Action Dialog ---
    private static void showPlantActionsDialog(Plant plant) {
        if (!plant.isAlive()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Plant Status");
            alert.setHeaderText(plant.getName() + " is Dead");
            alert.setContentText("This plant has died and cannot be treated.");
            alert.showAndWait();
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Plant Care Actions");
        dialog.setHeaderText("Care for: " + plant.getName() + " (" + plant.getType() + ")");
        
        // Create action buttons
        VBox buttonBox = new VBox(10);
        buttonBox.setPadding(new Insets(20));
        buttonBox.setSpacing(10);
        buttonBox.setAlignment(Pos.CENTER);

        GardenManager manager = GardenManager.getInstance();

        // Remove Pest button (only if infested)
        if (plant.hasPest()) {
            Button removePestBtn = new Button("🐛 Remove Pest (" + plant.getCurrentPest() + ")");
            removePestBtn.setStyle("-fx-background-color: #ff9800; -fx-text-fill: white; -fx-font-weight: bold; -fx-pref-width: 250px;");
            removePestBtn.setOnAction(e -> {
                manager.removePestFromPlant(plant.getName());
                refreshUI();
                dialog.close();
            });
            buttonBox.getChildren().add(removePestBtn);
        }

        // Water Plant button
        Button waterBtn = new Button("💧 Water Plant (+10)");
        waterBtn.setStyle("-fx-background-color: #2196f3; -fx-text-fill: white; -fx-font-weight: bold; -fx-pref-width: 250px;");
        waterBtn.setOnAction(e -> {
            manager.waterPlant(plant.getName(), 10);
            refreshUI();
            dialog.close();
        });
        buttonBox.getChildren().add(waterBtn);

        // Heal Plant button
        Button healBtn = new Button("💚 Heal Plant (+20 HP)");
        healBtn.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white; -fx-font-weight: bold; -fx-pref-width: 250px;");
        healBtn.setOnAction(e -> {
            manager.healPlant(plant.getName(), 20);
            refreshUI();
            dialog.close();
        });
        buttonBox.getChildren().add(healBtn);

        // Apply Fertilizer button
        Button fertilizerBtn = new Button("🌿 Apply Fertilizer");
        fertilizerBtn.setStyle("-fx-background-color: #8bc34a; -fx-text-fill: white; -fx-font-weight: bold; -fx-pref-width: 250px;");
        fertilizerBtn.setOnAction(e -> {
            manager.applyFertilizerToPlant(plant.getName());
            refreshUI();
            dialog.close();
        });
        buttonBox.getChildren().add(fertilizerBtn);

        // Emergency Treatment button (only if health is low)
        if (plant.getHealth() < 50) {
            Button emergencyBtn = new Button("🚑 Emergency Treatment");
            emergencyBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-pref-width: 250px;");
            emergencyBtn.setOnAction(e -> {
                manager.emergencyTreatmentForPlant(plant.getName());
                refreshUI();
                dialog.close();
            });
            buttonBox.getChildren().add(emergencyBtn);
        }

        dialog.getDialogPane().setContent(buttonBox);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        // Style the built-in close button to align with custom buttons
        Button closeButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CLOSE);
        closeButton.setPrefWidth(50);

        dialog.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
