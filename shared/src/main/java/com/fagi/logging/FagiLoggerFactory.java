package com.fagi.logging;

import com.fagi.logging.java.JavaLoggerConfigStrategy;
import com.fagi.logging.java.JavaLoggerCreationStrategy;

import java.nio.file.Path;

/**
 * This class is used to aid in creating and configuring {@link FagiLogger}s. This is to make it simpler to change
 * logging frameworks, both during testing and in cases where we want to change the entire framework.
 *
 * @author Marcus Haagh
 * @see FagiLogger
 * @see FagiLoggerCreationStrategy
 * @see FagiLoggerConfigStrategy
 */
public class FagiLoggerFactory {
    /**
     * This contains the creation strategy to be used. Should this change, then make sure that the config strategy is
     * changed as well.
     */
    private static FagiLoggerCreationStrategy loggerCreationStrategy = new JavaLoggerCreationStrategy();
    /**
     * This contains the config strategy to be used.Should this change, then make sure that the creation strategy is
     * changed as well.
     */
    private static FagiLoggerConfigStrategy loggerConfigStrategy = new JavaLoggerConfigStrategy();
    private static FagiLogger LOGGER = createLogger(FagiLoggerFactory.class);

    private FagiLoggerFactory() {
        throw new UnsupportedOperationException("Factory class - do not instantiate");
    }

    /**
     * Creates a FagiLogger for the given class
     *
     * @param tClass the class for the logger to be created for
     * @return a FagiLogger
     */
    public static <T> FagiLogger createLogger(Class<T> tClass) {
        return loggerCreationStrategy.createLogger(tClass);
    }

    /**
     * Determines if custom configuration has been provided by the user. If that's the case,
     * then configuration should not be overridden with the default configuration.
     *
     * @return true if logging configuration has been provided by the user
     */
    public static boolean isCustomConfigurationAvailable() {
        return loggerConfigStrategy.isCustomConfigurationAvailable();
    }

    /**
     * <p>
     * Sets up the default logging configuration.
     * </p>
     * <p>
     * Does nothing if {@link FagiLoggerFactory#isCustomConfigurationAvailable()} is true
     * </p>
     *
     * @param logFile the file where logs will be stored
     */
    public static void setupDefaultConfiguration(Path logFile) {
        if (!isCustomConfigurationAvailable()) {
            loggerConfigStrategy.setupDefaultConfiguration(logFile);
        } else {
            LOGGER.debug(() -> "Custom configuration has been provided. No further configuration is made.");
        }
    }

    public static void setLoggerCreationStrategy(FagiLoggerCreationStrategy strategy) {
        loggerCreationStrategy = strategy;
    }

    public static void setLoggerConfigStrategy(FagiLoggerConfigStrategy loggerConfigStrategy) {
        FagiLoggerFactory.loggerConfigStrategy = loggerConfigStrategy;
    }

    /**
     * Used for testing as the logger on the class is created before any test class can override the strategies.
     *
     * @param logger the logger to be used by the factory
     */
    static void setLogger(FagiLogger logger) {
        LOGGER = logger;
    }
}
