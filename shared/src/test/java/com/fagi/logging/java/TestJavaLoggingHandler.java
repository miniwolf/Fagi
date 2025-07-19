package com.fagi.logging.java;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * This class is used to record all logs sent to a logger
 */
public class TestJavaLoggingHandler extends Handler {
    private final List<LogRecord> logRecords = new ArrayList<>();

    @Override
    public void publish(LogRecord record) {
        logRecords.add(record);
    }

    /**
     * Does nothing
     */
    @Override
    public void flush() {
    }

    /**
     * Does nothing
     */
    @Override
    public void close() throws SecurityException {
    }

    public List<LogRecord> getLogRecords() {
        return logRecords;
    }
}
