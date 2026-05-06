# Tasks Service

A REST service for tracking tasks and calculating average execution duration.

---

## Features

* Create tasks with duration
* Get average duration per task

---

## Technical Features
* Stateless/scalable
* Atomic upsert
* Idempotency
* OpenAPI / Swagger docs
* Observability (Prometheus + Grafana)
* Rate limiting

---

## API

### Create Task

POST `/v1/tasks`

Headers:

```
Idempotency-Key: <unique-key>
Content-Type: application/json
```

Body:

```
{
  "taskName": "task1",
  "duration": 100
}
```

Responses:

* `200 OK` → success
* `400 Bad Request` → invalid input / missing header
* `409 Conflict` → duplicate request (same idempotency key)

---

### Get Task Stats

GET `/v1/tasks/{taskName}/stats`

Response:

```
{
  "taskName": "task1",
  "counter": 2,
  "avgDuration": 50.0
}
```

---

## Design Decisions

### Data Model

tasks_avg:

* task_name (PK)
* counter
* avg_duration

idempotency_keys:

* key (PK)

---

### Performance

* O(1) upsert per request
* Suitable for high write throughput

---

### Idempotency

Each request must include an `Idempotency-Key`.

* First request → processed
* Duplicate → rejected with 409

Implemented via:

* DB insert with unique constraint
* If insert fails → request is duplicate

---

## Running Locally

```
./gradlew clean build
./gradlew bootRun
OR: docker compose up --build
OR: docker compose up --build -d 
```
Manual test:

```
curl -X POST http://localhost:8080/v1/tasks \
  -H "Content-Type: application/json" \
  -H "X-Idempotency-Key: abc123" \
  -d '{
    "taskName": "task1",
    "duration": 100
  }'
curl localhost:8080/v1/tasks/average/task1
curl localhost:8080/actuator/health
```


Test:

```
./gradlew test
```

---

## Future Improvements

* Kinesis / Kafka ingestion for async processing
* Redis cache for hot stats

Client
↓
API Gateway
↓
Kinesis / Kafka
↓
Consumers (ECS)
↓
Postgres (aggregates)
↓
Redis (read model)
---

## Tech Stack

* Java 21+
* Spring Boot
* JPA / JDBC
* Mockito / JUnit
* Docker, TestContainer (optional)
* AWS CDK + Lambda (optional, unfinished)
