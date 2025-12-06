#!/bin/bash
# Run script for Garden System Application

echo "=========================================="
echo "Starting Garden System Application..."
echo "=========================================="

# Method 1: Try javafx:run
echo "Attempting to run with javafx:run..."
./mvnw javafx:run

# If that fails, use exec:java as fallback
if [ $? -ne 0 ]; then
    echo ""
    echo "javafx:run failed, trying exec:java..."
    ./mvnw exec:java -Dexec.mainClass="com.garden.system.ui.GardenApp" -Dexec.classpathScope=runtime
fi

