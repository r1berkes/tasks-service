-- extension for UUID
CREATE
EXTENSION IF NOT EXISTS "pgcrypto";

-- tables
CREATE TABLE tasks_avg
(
    id           UUID PRIMARY KEY          DEFAULT gen_random_uuid(),
    task_name    TEXT             NOT NULL UNIQUE,
    counter      BIGINT           NOT NULL,
    avg_duration DOUBLE PRECISION NOT NULL,
    created_at   TIMESTAMP        NOT NULL DEFAULT now(),
    updated_at   TIMESTAMP        NOT NULL DEFAULT now()
);

CREATE TABLE tasks_weekly_avg
(
    task_id      UUID PRIMARY KEY REFERENCES tasks_avg (id),
    counter      BIGINT           NOT NULL,
    avg_duration DOUBLE PRECISION NOT NULL,
    created_at   TIMESTAMP        NOT NULL DEFAULT now(),
    updated_at   TIMESTAMP        NOT NULL DEFAULT now()
);