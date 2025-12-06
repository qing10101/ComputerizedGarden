# How to Run the Application

## Method 1: Using Maven JavaFX Plugin (Recommended)

This is the simplest method. Maven will automatically handle all dependencies and module paths:

```bash
./mvnw javafx:run
```

Or on Windows:
```bash
mvnw.cmd javafx:run
```

## Method 2: Compile Then Run

```bash
# 1. Compile the project
./mvnw clean compile

# 2. Run the application
./mvnw javafx:run
```

## Method 3: Run in IDE

### IntelliJ IDEA
1. Right-click on `GardenApp.java` file
2. Select "Run 'GardenApp.main()'"
3. Or click the green run button in the class

### Eclipse
1. Right-click on the project
2. Select "Run As" -> "Java Application"
3. Select `com.garden.system.ui.GardenApp`

### VS Code
1. Open `GardenApp.java`
2. Click the "Run" button above the `main` method

## Method 4: Direct Java Command (Advanced)

```bash
# 1. Compile
./mvnw clean compile

# 2. Run (module path needs to be specified)
java --module-path target/classes:$(./mvnw dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout) \
     --module com.garden.system/com.garden.system.ui.GardenApp
```

## Notes

- Ensure Java 21 or higher is installed
- Ensure JavaFX 21 is installed (Maven will download it automatically)
- If you encounter module path issues, use Method 1 (Maven JavaFX plugin) which is the simplest

## Troubleshooting

### If you encounter "Module not found" error:
- Ensure you have successfully compiled by running `./mvnw clean compile`
- Check that the module name in `module-info.java` is correct

### If you encounter JavaFX related errors:
- Ensure Maven dependencies are properly downloaded: `./mvnw dependency:resolve`
- Check Java version: `java -version` (Java 21+ required)

### If `javafx:run` fails:
You can use the fallback method to run:
```bash
./mvnw exec:java -Dexec.mainClass="com.garden.system.ui.GardenApp" -Dexec.classpathScope=runtime
```

Or use the provided run script:
```bash
./run.sh
```

### Known Issues:
- On macOS, you may see a "Timeout while waiting for app reactivation" warning. This is normal and does not affect application operation.
