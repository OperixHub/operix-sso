package com.operix.auth.logging;

public interface LogEventWriter {
    void enqueue(LogEvent event);
}

