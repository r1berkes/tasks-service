-- extension for UUID
CREATE
EXTENSION IF NOT EXISTS "pgcrypto";

-- tables
CREATE TABLE tasks_avg
(
    task_name    VARCHAR(255) PRIMARY KEY,
    counter      BIGINT           NOT NULL,
    avg_duration DOUBLE PRECISION NOT NULL,
    created_at   TIMESTAMP        NOT NULL DEFAULT now(),
    updated_at   TIMESTAMP        NOT NULL DEFAULT now()
);

CREATE TABLE tasks_weekly_avg
(
    task_id      VARCHAR(255) PRIMARY KEY REFERENCES tasks_avg (task_name),
    counter      BIGINT           NOT NULL,
    avg_duration DOUBLE PRECISION NOT NULL,
    created_at   TIMESTAMP        NOT NULL DEFAULT now(),
    updated_at   TIMESTAMP        NOT NULL DEFAULT now()
);