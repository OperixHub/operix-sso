package com.operix.auth.logging;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.logs.persistence")
public record LogPersistenceProperties(
        int queueCapacity,
        int batchSize,
        long flushIntervalMs
) {
}

