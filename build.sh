#!/bin/bash
# Build script for DigitalOcean App Platform

echo "Starting Maven build..."

# Ensure Maven wrapper is executable
chmod +x ./mvnw

# Clean and build the application
./mvnw clean package -DskipTests -Dmaven.compiler.source=21 -Dmaven.compiler.target=21

echo "Build completed successfully!"
echo "JAR file location: target/backend-0.0.1-SNAPSHOT.jar"
