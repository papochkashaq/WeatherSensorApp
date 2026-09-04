# WeatherSensorApp

REST API для регистрации метеорологических датчиков и учёта их показаний (температура, наличие осадков).

Проект реализован на Spring Boot и хранит данные в PostgreSQL. Каждый датчик (`Sensor`) может иметь множество измерений (`Measurement`), связанных с ним по внешнему ключу.

## Стек технологий

- **Java 11**
- **Spring Boot 2.5.3**
  - Spring Web (REST-контроллеры)
  - Spring Data JPA (доступ к БД)
  - Spring Boot Validation (валидация входных данных)
  - Thymeleaf (шаблонизатор)
- **PostgreSQL** — основная СУБД
- **ModelMapper** — маппинг между сущностями и DTO
- **Lombok** — уменьшение шаблонного кода
- **Maven** — сборка проекта

## Структура проекта

```
src/main/java/com/alderson/WeatherSensor/
├── WeatherSensorAppApplication.java   # точка входа Spring Boot
├── config/
│   └── AppConfig.java                 # бин ModelMapper
├── controllers/
│   ├── SensorController.java          # REST-эндпоинты для датчиков
│   └── MeasurementController.java     # REST-эндпоинты для измерений
├── dto/
│   ├── SensorDTO.java                 # DTO датчика + валидация
│   └── MeasurementDTO.java            # DTO измерения + валидация
├── models/
│   ├── Sensor.java                    # JPA-сущность "sensor"
│   └── Measurement.java               # JPA-сущность "measurement"
├── repositories/
│   ├── SensorRepository.java          # Spring Data JPA репозиторий
│   └── MeasurementRepository.java     # Spring Data JPA репозиторий
├── services/
│   ├── SensorService.java             # бизнес-логика датчиков
│   └── MeasurementService.java        # бизнес-логика измерений
└── utils/
    ├── GlobalExceptionHandler.java        # централизованная обработка ошибок
    ├── SensorDuplicateNameException.java  # исключение: датчик уже существует
    ├── SensorNotFoundException.java       # исключение: датчик не найден
    └── SensorErrorResponse.java           # модель тела ответа об ошибке
```

## Требования

- JDK 11+
- Maven 3.6+ (либо использовать входящий в проект Maven Wrapper `mvnw` / `mvnw.cmd`)
- PostgreSQL (запущенный локально или доступный по сети)

## Настройка базы данных

Настройки подключения к БД задаются в [`application.properties`](src/main/resources/application.properties:1):

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

Перед запуском:
1. Убедитесь, что PostgreSQL запущен и доступна база данных `postgres` (или измените `spring.datasource.url` на нужную).
2. При необходимости обновите `spring.datasource.username` и `spring.datasource.password` под свои учётные данные.
3. Таблицы `sensor` и `measurement` создаются/обновляются автоматически благодаря `spring.jpa.hibernate.ddl-auto=update`.

## Запуск приложения

С помощью Maven Wrapper:

```bash
# Windows
mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

Либо через установленный Maven:

```bash
mvn spring-boot:run
```

После запуска приложение будет доступно на `http://localhost:8081` (порт задан в `application.properties`).

## API

### Датчики (`/sensors`)

| Метод | Путь              | Описание                     | Тело запроса |
|-------|--------------------|-------------------------------|---------------|
| POST  | `/sensors/registration` | Регистрация нового датчика | [`SensorDTO`](src/main/java/com/alderson/WeatherSensor/dto/SensorDTO.java:13) |

**Пример запроса:**

```json
{
  "name": "sensor-1"
}
```

**Валидация:**
- `name` — обязателен, от 3 до 30 символов.

**Возможные ошибки:**
- `409 Conflict` — датчик с таким именем уже существует ([`SensorDuplicateNameException`](src/main/java/com/alderson/WeatherSensor/utils/SensorDuplicateNameException.java:1)).
- `400 Bad Request` — ошибка валидации полей.

### Измерения (`/measurements`)

| Метод | Путь                          | Описание                                             | Параметры / Тело |
|-------|-------------------------------|-------------------------------------------------------|-------------------|
| GET   | `/measurements`               | Получить список всех измерений либо измерений конкретного датчика | Query-параметр `sensorName` (опционально) |
| POST  | `/measurements/add`           | Добавить новое измерение                              | [`MeasurementDTO`](src/main/java/com/alderson/WeatherSensor/dto/MeasurementDTO.java:15) |
| GET   | `/measurements/rainyDaysCount` | Получить количество измерений с осадками (`raining = true`) | — |

**Пример запроса на добавление измерения:**

```json
{
  "value": 21.5,
  "raining": true,
  "sensor": {
    "name": "sensor-1"
  }
}
```

**Валидация:**
- `value` — обязателен, диапазон от -100 до 100.
- `raining` — обязателен (`true`/`false`).
- `sensor.name` — обязателен, датчик должен быть предварительно зарегистрирован.

**Возможные ошибки:**
- `404 Not Found` — датчик с указанным именем не найден ([`SensorNotFoundException`](src/main/java/com/alderson/WeatherSensor/utils/SensorNotFoundException.java:1)).
- `400 Bad Request` — ошибка валидации полей.

## Обработка ошибок

Все исключения перехватываются централизованно в [`GlobalExceptionHandler`](src/main/java/com/alderson/WeatherSensor/utils/GlobalExceptionHandler.java:12) и возвращаются клиенту в едином формате [`SensorErrorResponse`](src/main/java/com/alderson/WeatherSensor/utils/SensorErrorResponse.java:1):

```json
{
  "message": "Текст ошибки",
  "timestamp": "2026-09-04T10:00:00"
}
```

| Исключение | HTTP-статус |
|---|---|
| `SensorDuplicateNameException` | 409 Conflict |
| `SensorNotFoundException` | 404 Not Found |
| `MethodArgumentNotValidException` (ошибка валидации `@Valid`) | 400 Bad Request |

## Модель данных

**Sensor**
- `id` — идентификатор (генерируется автоматически)
- `name` — уникальное имя датчика
- `measurements` — список связанных измерений (`OneToMany`)

**Measurement**
- `id` — идентификатор (генерируется автоматически)
- `value` — значение показания (например, температура)
- `raining` — признак осадков
- `sensor` — ссылка на датчик (`ManyToOne`)
- `time` — время создания записи (проставляется автоматически при сохранении)

## Тестирование

Запуск тестов проекта:

```bash
mvnw.cmd test
```

## Сборка

Сборка исполняемого JAR-файла:

```bash
mvnw.cmd clean package
```

Собранный артефакт можно запустить командой:

```bash
java -jar target/WeatherSensorApp-0.0.1-SNAPSHOT.jar
```
