package com.operix.auth.logging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Types;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Component
@EnableScheduling
@EnableConfigurationProperties(LogPersistenceProperties.class)
public class JdbcLogEventWriter implements LogEventWriter {

    private static final String INSERT_SQL = """
            INSERT INTO log_event (
              created_at,
              correlation_id,
              event_type,
              level,
              subject,
              client_ip,
              http_method,
              http_path,
              http_status,
              latency_ms,
              details_json
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?::jsonb)
            """;

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;
    private final LogPersistenceProperties props;
    private final BlockingQueue<LogEvent> queue;

    public JdbcLogEventWriter(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper, LogPersistenceProperties props) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
        this.props = props;
        this.queue = new ArrayBlockingQueue<>(Math.max(10, props.queueCapacity()));
    }

    @Override
    public void enqueue(LogEvent event) {
        if (event == null) return;
        // não bloquear request; em overload, descartamos
        queue.offer(event);
    }

    @Scheduled(fixedDelayString = "${app.logs.persistence.flush-interval-ms:250}")
    public void flush() {
        int batchSize = Math.max(1, props.batchSize());
        List<LogEvent> drained = new ArrayList<>(batchSize);
        queue.drainTo(drained, batchSize);
        if (drained.isEmpty()) return;

        jdbcTemplate.batchUpdate(
                INSERT_SQL,
                drained,
                drained.size(),
                (ps, e) -> {
                    Instant createdAt = Objects.requireNonNullElseGet(e.createdAt(), Instant::now);
                    ps.setObject(1, createdAt);
                    ps.setString(2, e.correlationId());
                    ps.setString(3, e.eventType());
                    ps.setString(4, e.level());
                    ps.setString(5, e.subject());
                    ps.setString(6, e.clientIp());
                    ps.setString(7, e.httpMethod());
                    ps.setString(8, e.httpPath());
                    if (e.httpStatus() == null) ps.setNull(9, Types.INTEGER); else ps.setInt(9, e.httpStatus());
                    if (e.latencyMs() == null) ps.setNull(10, Types.INTEGER); else ps.setInt(10, e.latencyMs());
                    ps.setString(11, toJson(e.details()));
                }
        );
    }

    private String toJson(Object value) {
        if (value == null) return null;
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            // falha de serialização não pode derrubar request
            return null;
        }
    }
}

