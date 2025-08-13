# Stock Evaluator

Stock Evaluator is a web application designed to evaluate stock market data and provide insights using various APIs and tools. The project is built using **Java**, **Spring Boot**, **MySQL**, and **Docker**. It also integrates with external APIs like Reddit and Alpha Vantage for additional data.

---

## Features

- **User Authentication**: Login and signup functionality with JWT-based authentication.
- **Stock Analysis**: Fetch and display stock data using the Alpha Vantage API.
- **Reddit Integration**: Retrieve and analyze Reddit posts related to stocks.
- **Database Management**: Persistent storage of user and stock data using MySQL.
- **Health Monitoring**: Exposes health check endpoints for monitoring the backend.
- **CORS Support**: Configurable CORS settings for frontend-backend communication.

---

## Prerequisites

- **Java 17+**
- **Maven 3.8+**
- **Docker & Docker Compose**
- **Node.js** (for frontend, if applicable)

---

## Project Structure
# Stock Evaluator Backend

This is the backend service for the Stock Evaluator AI application, built with Spring Boot.

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

## Getting Started

1. Clone the repository
2. Navigate to the project directory
3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

The application will start on port 8080.

## Features

- RESTful API endpoints for stock evaluation
- H2 in-memory database for development
- JPA for data persistence
- Spring Security (to be implemented)
- Swagger/OpenAPI documentation (to be implemented)

## API Documentation

Once the application is running, you can access:
- H2 Console: http://localhost:8080/h2-console
- API Documentation: Coming soon

## Development

The project uses:
- Spring Boot 3.2.3
- Spring Data JPA
- H2 Database
- Lombok
- Spring Web
- Spring Validation
