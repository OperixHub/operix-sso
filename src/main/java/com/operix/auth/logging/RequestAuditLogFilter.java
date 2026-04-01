package com.operix.auth.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Component
public class RequestAuditLogFilter extends OncePerRequestFilter {

    private static final String CORRELATION_HEADER = "X-Correlation-Id";

    private final LogEventWriter logEventWriter;

    public RequestAuditLogFilter(LogEventWriter logEventWriter) {
        this.logEventWriter = logEventWriter;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        long startNs = System.nanoTime();
        String correlationId = getOrCreateCorrelationId(request);
        response.setHeader(CORRELATION_HEADER, correlationId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            int status = response.getStatus();
            int latencyMs = (int) ((System.nanoTime() - startNs) / 1_000_000L);

            String subject = resolveSubject();
            String clientIp = resolveClientIp(request);

            logEventWriter.enqueue(new LogEvent(
                    Instant.now(),
                    correlationId,
                    "http_request",
                    status >= 500 ? "ERROR" : "INFO",
                    subject,
                    clientIp,
                    request.getMethod(),
                    request.getRequestURI(),
                    status,
                    latencyMs,
                    Map.of(
                            "userAgent", safeHeader(request, "User-Agent")
                    )
            ));
        }
    }

    private String getOrCreateCorrelationId(HttpServletRequest request) {
        String existing = request.getHeader(CORRELATION_HEADER);
        if (existing != null && !existing.isBlank()) return existing.trim();
        return UUID.randomUUID().toString();
    }

    private String resolveSubject() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;
        Object principal = auth.getPrincipal();
        if (principal instanceof Jwt jwt) return jwt.getSubject();
        return auth.getName();
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            // primeiro IP da lista
            int idx = forwarded.indexOf(',');
            return (idx > 0 ? forwarded.substring(0, idx) : forwarded).trim();
        }
        return request.getRemoteAddr();
    }

    private String safeHeader(HttpServletRequest request, String name) {
        String value = request.getHeader(name);
        if (value == null) return null;
        // evita payloads enormes
        return value.length() > 300 ? value.substring(0, 300) : value;
    }
}

