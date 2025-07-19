package com.fagi.logging;

/**
 * <p>
 * A strategy to help create a {@link FagiLogger}.
 * </p>
 * <p>
 * The strategy should hold the details on how to instantiate a new instance of a {@link FagiLogger}. The strategy must be set in the {@link FagiLoggerFactory} in order to be used.
 * </p>
 *
 * @author Marcus Haagh
 * @see FagiLogger
 */
public interface FagiLoggerCreationStrategy {
    /**
     * Creates a logger for the given class.
     *
     * @param tClass the class the new logger should be associated with.
     * @return a {@link FagiLogger}
     */
    <T> FagiLogger createLogger(Class<T> tClass);
}
