CREATE TABLE users_systems (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    system_id INTEGER NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (system_id) REFERENCES systems(id)
);  