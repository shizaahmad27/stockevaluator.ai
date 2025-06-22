# Stage 1: Build the application using Maven
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# Copy the Maven wrapper and project definition
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Copy the source code
COPY src ./src

# Build the application, creating the executable JAR
RUN ./mvnw clean package -DskipTests

# Stage 2: Create the final, smaller production image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the built JAR from the 'build' stage
COPY --from=build /app/target/stock-evaluator-backend-0.0.1-SNAPSHOT.jar app.jar

# Expose the port the application will run on
EXPOSE 8080

# The command to start the application when the container launches
ENTRYPOINT ["java", "-jar", "app.jar"]
