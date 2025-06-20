# Use platform-compatible images
FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace/app

COPY backend/mvnw .
COPY backend/.mvn .mvn
COPY backend/pom.xml .
COPY backend/src src

# Make mvnw executable
RUN chmod +x ./mvnw

# Build the JAR file
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy the JAR file from build stage
COPY --from=build /workspace/app/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
