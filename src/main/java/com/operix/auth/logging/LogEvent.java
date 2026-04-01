package com.operix.auth.logging;

import java.time.Instant;
import java.util.Map;

public record LogEvent(
        Instant createdAt,
        String correlationId,
        String eventType,
        String level,
        String subject,
        String clientIp,
        String httpMethod,
        String httpPath,
        Integer httpStatus,
        Integer latencyMs,
        Map<String, Object> details
) {
}

