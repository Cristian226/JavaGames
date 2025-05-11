FROM docker.io/openjdk:23-jdk-slim AS builder

WORKDIR /app

RUN apt-get update && apt-get install -y maven

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests -e -X

FROM docker.io/openjdk:23-jdk-slim

WORKDIR /app

COPY --from=builder /app/target/*.jar ./app.jar

