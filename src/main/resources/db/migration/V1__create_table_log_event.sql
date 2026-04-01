CREATE TABLE IF NOT EXISTS log_event (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    correlation_id VARCHAR(64),
    event_type VARCHAR(64) NOT NULL,
    level VARCHAR(16) NOT NULL,
    subject VARCHAR(255),
    client_ip VARCHAR(64),
    http_method VARCHAR(16),
    http_path VARCHAR(2048),
    http_status INT,
    latency_ms INT,
    details_json JSONB
);

CREATE INDEX IF NOT EXISTS idx_log_event_created_at ON log_event (created_at DESC);
CREATE INDEX IF NOT EXISTS idx_log_event_event_type ON log_event (event_type);
CREATE INDEX IF NOT EXISTS idx_log_event_subject ON log_event (subject);

