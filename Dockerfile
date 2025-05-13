# Build stage
FROM docker.io/openjdk:23-jdk-slim AS builder

WORKDIR /app

# Install Maven
RUN apt-get update && apt-get install -y maven && apt-get clean

# Copy Maven configuration and source code
COPY pom.xml .
COPY src ./src

# Build the application, skipping tests
RUN mvn clean package -DskipTests -e -X

# Debug: List contents of the target directory
RUN ls -l /app/target/

# Debug: Inspect the JAR file to verify MySQL Connector inclusion
RUN jar tf /app/target/*.jar | grep mysql || echo "MySQL Connector not found in JAR"