CREATE TABLE systems (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    uri VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);