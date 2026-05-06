CREATE TABLE idempotency_keys
(
    key        TEXT PRIMARY KEY,
    created_at TIMESTAMP DEFAULT now()
);