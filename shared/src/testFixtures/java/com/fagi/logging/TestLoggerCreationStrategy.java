package com.fagi.logging;

import java.util.Map;

/**
 * Test creation strategy that stores created loggers to simulate the same behaviour as normal logger creation
 */
public class TestLoggerCreationStrategy implements FagiLoggerCreationStrategy {
    private final Map<Class<?>, TestLogger<?>> loggers;

    public TestLoggerCreationStrategy(Map<Class<?>, TestLogger<?>> loggers) {
        this.loggers = loggers;
    }

    @Override
    public <T> FagiLogger createLogger(Class<T> tClass) {
        return loggers.computeIfAbsent(
                tClass,
                (k) -> new TestLogger<>(tClass)
        );
    }
}
