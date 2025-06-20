# Use platform-compatible images
FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace/app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

# Make mvnw executable
RUN chmod +x ./mvnw

# Build the JAR file
RUN ./mvnw clean package -DskipTests

# List the target directory to see what was created (for debugging)
RUN ls -la target/

FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy the specific JAR file from build stage
COPY --from=build /workspace/app/target/stock-evaluator-backend-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
