# Uber-Like App Backend

This repository contains the backend of an Uber-style app for motorcycle couriers, developed with Java and Spring Boot, using RBAC, JWT for security, WebSockets for real-time communication, Docker for containerization, PostgreSQL for persistence and Maven for dependency management, following the clean architecture.

## Technologies Used

- Java 17
- Spring Boot 3
- Spring Security (JWT and RBAC)
- Spring WebSockets
- Spring Data JPA
- PostgreSQL
- Docker
- Maven
- Clean Architecture

## Main Features

- **Authentication and Authorization**: JWT with RBAC for role-based access control (user, courier, admin).
- **Profile Management**: Editing and viewing profiles.
- **Geolocation and Mapping**: Integration for location selection.
- **Race Request**: Creation, acceptance/rejection of requests.
- **Real-Time Update**: WebSockets for status tracking.

## How to Run the Project

### Clone the Repository
```sh
 git clone https://github.com/samuelbaldasso/UberLike.git
 cd UberLike
```

### 2. Configure the Database
Create a PostgreSQL database and adjust the `application.yml`:
```yaml
spring:
 datasource:
 url: jdbc:postgresql://localhost:5432/uber_like_db
 username: postgres
 password: your_password
```

### 3. Run with Docker
```sh
docker-compose up -d
```

### 4. Run Backend
```sh
mvn clean install
mvn spring-boot:run
```

## API documentation
The API is documented with **Swagger**. After starting the backend, go to:
```sh
http://localhost:8080/swagger-ui.html
```

## Project Structure
```
Uber_Like-Java-Spring/
├── src/main/java/com/uberlike/
│ ├── config/ # General system settings
│ ├── controller/ # REST API controllers
│ ├── service/ # Services and business rules
│ ├── repository/ # JPA repositories
│ ├── security/ # Security and JWT configuration
│ ├── model/ # Entities and models
│ └── websocket/ # WebSockets configuration and service
├── src/main/resources/
│ ├── application. yml # Main configurations
│ ├── db/migration/ # Database migration scripts
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```

## How to contribute
1. Fork the project.
2. Create a branch with the feature (`git checkout -b feature-new`).
3. Commit your changes (`git commit -m ‘Add new feature’`).
4. Push to the branch (`git push origin feature-new`).
5. Open a Pull Request.

## License
This project is licensed under the MIT license. See the `LICENSE` file for more details.
