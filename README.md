# WeatherSensorApp

A REST API for registering meteorological sensors and tracking their readings (temperature, precipitation).

The project is built with Spring Boot and stores data in PostgreSQL. Each sensor (`Sensor`) can have multiple measurements (`Measurement`) linked to it via a foreign key.

## Technology Stack

- **Java 11**
- **Spring Boot 2.5.3**
  - Spring Web (REST controllers)
  - Spring Data JPA (database access)
  - Spring Boot Validation (input data validation)
  - Thymeleaf (template engine)
- **PostgreSQL** — primary database
- **ModelMapper** — mapping between entities and DTOs
- **Lombok** — reducing boilerplate code
- **Maven** — project build tool

## Project Structure

```
src/main/java/com/alderson/WeatherSensor/
├── WeatherSensorAppApplication.java   # Spring Boot entry point
├── config/
│   └── AppConfig.java                 # ModelMapper bean
├── controllers/
│   ├── SensorController.java          # REST endpoints for sensors
│   └── MeasurementController.java     # REST endpoints for measurements
├── dto/
│   ├── SensorDTO.java                 # Sensor DTO + validation
│   └── MeasurementDTO.java            # Measurement DTO + validation
├── models/
│   ├── Sensor.java                    # JPA entity "sensor"
│   └── Measurement.java               # JPA entity "measurement"
├── repositories/
│   ├── SensorRepository.java          # Spring Data JPA repository
│   └── MeasurementRepository.java     # Spring Data JPA repository
├── services/
│   ├── SensorService.java             # Sensor business logic
│   └── MeasurementService.java        # Measurement business logic
└── utils/
    ├── GlobalExceptionHandler.java        # centralized error handling
    ├── SensorDuplicateNameException.java  # exception: sensor already exists
    ├── SensorNotFoundException.java       # exception: sensor not found
    └── SensorErrorResponse.java           # error response body model
```

## Requirements

- JDK 11+
- Maven 3.6+ (or use the bundled Maven Wrapper `mvnw` / `mvnw.cmd`)
- PostgreSQL (running locally or accessible over the network)

## Database Configuration

Database connection settings are defined in [`application.properties`](src/main/resources/application.properties:1):

```properties
server.port=8081

spring.datasource.driverClassName=org.postgresql.Driver
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=0451

spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.show_sql=true

spring.sql.init.mode=always
spring.jpa.hibernate.ddl-auto=update
```

Before running the application:
1. Make sure PostgreSQL is running and the `postgres` database is accessible (or change `spring.datasource.url` to the desired one).
2. If necessary, update `spring.datasource.username` and `spring.datasource.password` with your own credentials.
3. The `sensor` and `measurement` tables are created/updated automatically thanks to `spring.jpa.hibernate.ddl-auto=update`.

## Running the Application

Using the Maven Wrapper:

```bash
# Windows
mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

Or with a locally installed Maven:

```bash
mvn spring-boot:run
```

Once started, the application will be available at `http://localhost:8081` (the port is set in `application.properties`).

## API

### Sensors (`/sensors`)

| Method | Path              | Description                     | Request Body |
|--------|--------------------|-----------------------------------|---------------|
| POST   | `/sensors/registration` | Register a new sensor | [`SensorDTO`](src/main/java/com/alderson/WeatherSensor/dto/SensorDTO.java:13) |

**Example request:**

```json
{
  "name": "sensor-1"
}
```

**Validation:**
- `name` — required, from 3 to 30 characters.

**Possible errors:**
- `409 Conflict` — a sensor with this name already exists ([`SensorDuplicateNameException`](src/main/java/com/alderson/WeatherSensor/utils/SensorDuplicateNameException.java:1)).
- `400 Bad Request` — field validation error.

### Measurements (`/measurements`)

| Method | Path                          | Description                                             | Parameters / Body |
|--------|-------------------------------|-------------------------------------------------------|-------------------|
| GET    | `/measurements`               | Get a list of all measurements or measurements for a specific sensor | Query parameter `sensorName` (optional) |
| POST   | `/measurements/add`           | Add a new measurement                              | [`MeasurementDTO`](src/main/java/com/alderson/WeatherSensor/dto/MeasurementDTO.java:15) |
| GET    | `/measurements/rainyDaysCount` | Get the count of measurements with precipitation (`raining = true`) | — |

**Example request for adding a measurement:**

```json
{
  "value": 21.5,
  "raining": true,
  "sensor": {
    "name": "sensor-1"
  }
}
```

**Validation:**
- `value` — required, range from -100 to 100.
- `raining` — required (`true`/`false`).
- `sensor.name` — required, the sensor must be registered beforehand.

**Possible errors:**
- `404 Not Found` — no sensor found with the specified name ([`SensorNotFoundException`](src/main/java/com/alderson/WeatherSensor/utils/SensorNotFoundException.java:1)).
- `400 Bad Request` — field validation error.

## Error Handling

All exceptions are handled centrally in [`GlobalExceptionHandler`](src/main/java/com/alderson/WeatherSensor/utils/GlobalExceptionHandler.java:12) and returned to the client in a unified format [`SensorErrorResponse`](src/main/java/com/alderson/WeatherSensor/utils/SensorErrorResponse.java:1):

```json
{
  "message": "Error text",
  "timestamp": "2026-09-04T10:00:00"
}
```

| Exception | HTTP Status |
|---|---|
| `SensorDuplicateNameException` | 409 Conflict |
| `SensorNotFoundException` | 404 Not Found |
| `MethodArgumentNotValidException` (`@Valid` validation error) | 400 Bad Request |

## Data Model

**Sensor**
- `id` — identifier (generated automatically)
- `name` — unique sensor name
- `measurements` — list of related measurements (`OneToMany`)

**Measurement**
- `id` — identifier (generated automatically)
- `value` — reading value (e.g., temperature)
- `raining` — precipitation flag
- `sensor` — reference to the sensor (`ManyToOne`)
- `time` — record creation time (set automatically on save)

## Testing

Run the project tests:

```bash
mvnw.cmd test
```

## Build

Build the executable JAR file:

```bash
mvnw.cmd clean package
```

The built artifact can be run with the command:

```bash
java -jar target/WeatherSensorApp-0.0.1-SNAPSHOT.jar
```
