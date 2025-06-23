# Stage 1: Build the application using Maven
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# Copy the Maven wrapper and project definition from the backend directory
COPY backend/mvnw .
COPY backend/.mvn .mvn
COPY backend/pom.xml .
COPY backend/src ./src

# Build the application, creating the executable JAR
RUN ./mvnw clean package -DskipTests

# Stage 2: Create the final, smaller production image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the built JAR from the 'build' stage
COPY --from=build /app/target/stock-evaluator-backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]