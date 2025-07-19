package com.fagi.logging;

import java.nio.file.Path;

/**
 * Used to help test logging
 */
public class TestLoggerConfigurationStrategy implements FagiLoggerConfigStrategy {
    private final boolean customConfigurationAvailable;
    private boolean configured = false;
    private Path logFile;

    public TestLoggerConfigurationStrategy(boolean customConfigurationAvailable) {
        this.customConfigurationAvailable = customConfigurationAvailable;
    }

    @Override
    public boolean isCustomConfigurationAvailable() {
        return customConfigurationAvailable;
    }

    @Override
    public void setupDefaultConfiguration(Path logFile) {
        configured = true;
        this.logFile = logFile;
    }

    public boolean isConfigured() {
        return configured;
    }

    public Path getLogFile() {
        return logFile;
    }
}
