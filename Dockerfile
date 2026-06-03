# Step 1: Use a modern, stable Maven image with JDK 17
FROM maven:3.8.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Step 2: Use the official Temurin runtime image to run the app
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/AI-Integration-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 9600
ENTRYPOINT ["java", "-jar", "app.jar"]