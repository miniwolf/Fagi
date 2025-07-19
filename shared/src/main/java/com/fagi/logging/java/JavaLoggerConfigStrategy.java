package com.fagi.logging.java;

import com.fagi.logging.FagiLogger;
import com.fagi.logging.FagiLoggerConfigStrategy;
import com.fagi.logging.FagiLoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This strategy configures {@link Logger} from the Java Standard Library.
 *
 * @author Marcus Haagh
 */
public class JavaLoggerConfigStrategy implements FagiLoggerConfigStrategy {
    private static final FagiLogger LOGGER = FagiLoggerFactory.createLogger(JavaLoggerConfigStrategy.class);
    static final String LOGGING_CONFIG_FILE_SYSTEM_PROPERTY_NAME = "java.util.logging.config.file";

    @Override
    public boolean isCustomConfigurationAvailable() {
        return System.getProperty(LOGGING_CONFIG_FILE_SYSTEM_PROPERTY_NAME) != null;
    }

    @Override
    public void setupDefaultConfiguration(Path logFile) {
        var logLevel = Level.INFO;
        var rootLogger = Logger.getLogger("");
        rootLogger.setLevel(logLevel);

        for (var handler : rootLogger.getHandlers()) {
            rootLogger.removeHandler(handler);
        }

        var consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(logLevel);
        consoleHandler.setFormatter(new JavaLoggerFormatter());

        rootLogger.addHandler(consoleHandler);

        try {
            var fileHandler = new FileHandler(logFile.toString());
            fileHandler.setLevel(logLevel);
            fileHandler.setFormatter(new JavaLoggerFormatter());
            rootLogger.addHandler(fileHandler);
        } catch (IOException e) {
            LOGGER.error(
                    e,
                    () -> "Failed to setup file handler for logger. Log statements will not be saved in files."
            );
        }
    }
}
