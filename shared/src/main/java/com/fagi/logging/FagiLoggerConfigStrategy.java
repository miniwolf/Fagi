package com.fagi.logging;

import java.nio.file.Path;

/**
 * <p>
 *     This strategy can check if a default configuration should be used and create the default logging configuration.
 * </p>
 * <p>
 *     The strategy must be set in the {@link FagiLoggerFactory} in order to be used.
 * </p>
 *
 * @author Marcus Haagh
 * @see FagiLoggerFactory
 */
public interface FagiLoggerConfigStrategy {
    /**
     * Checks if custom configuration has been provided.
     *
     * @return true if logging configuration has been provided by the user
     */
    boolean isCustomConfigurationAvailable();

    /**
     * Sets up the logging in a default manner. This ensures that proper logging is configured, even if the user hasn't
     * provided a log config file.
     *
     * @param logFile the path of the resulting log file.
     */
    void setupDefaultConfiguration(Path logFile);
}
