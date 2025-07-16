package com.fagi.db;

/**
 * Exception thrown when database initialization fails.
 *
 * <p>This exception is used to indicate that the database could not be properly
 * initialized or started. It typically occurs during database construction or
 * startup operations when critical initialization steps fail.</p>
 *
 * <h3>Common Scenarios:</h3>
 * <ul>
 *   <li>Database file cannot be created or opened</li>
 *   <li>Insufficient permissions to access the database directory</li>
 *   <li>Corrupted database files that cannot be loaded</li>
 *   <li>Disk space exhaustion preventing file creation</li>
 *   <li>Invalid database file paths or missing parent directories</li>
 *   <li>Log file corruption preventing data recovery</li>
 * </ul>
 *
 * <h3>Exception Hierarchy:</h3>
 * <pre>
 * java.lang.Exception
 *   └── DatabaseInitializeException
 * </pre>
 *
 * <p><strong>Checked Exception:</strong> This is a checked exception that must be
 * explicitly handled by calling code, ensuring that database initialization
 * failures are properly managed during application startup.</p>
 *
 * <h3>Usage Pattern:</h3>
 * <pre>{@code
 * try {
 *     LogBasedDatabase database = new LogBasedDatabase("app.db");
 *     // Database ready for use
 * } catch (DatabaseInitializeException e) {
 *     logger.error("Database initialization failed: " + e.getMessage(), e);
 *
 *     // Handle initialization failure
 *     if (e.getCause() instanceof FileNotFoundException) {
 *         // Try to create parent directories
 *         createDatabaseDirectory();
 *     } else {
 *         // Fatal error - cannot continue
 *         System.exit(1);
 *     }
 * }
 * }</pre>
 *
 * <h3>Recovery Strategies:</h3>
 * <ul>
 *   <li><strong>File System Issues:</strong> Create missing directories, check permissions</li>
 *   <li><strong>Corrupted Data:</strong> Restore from backup, initialize empty database</li>
 * </ul>
 *
 * <h3>Best Practices:</h3>
 * <ul>
 *   <li>Always handle this exception during database creation</li>
 *   <li>Log the full exception with stack trace for troubleshooting</li>
 *   <li>Implement graceful degradation or fallback mechanisms</li>
 *   <li>Consider retry logic for transient initialization failures</li>
 * </ul>
 *
 * @author Nicklas Pingel
 * @see com.fagi.db.LogBasedDatabase
 * @see java.lang.Exception
 */
public class DatabaseInitializeException extends Exception {

    /**
     * Serial version UID for serialization compatibility
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new database initialization exception with the specified detail message and cause.
     *
     * <p>This constructor creates a new exception that wraps an underlying cause
     * while providing a specific error message describing the initialization failure.
     * The cause exception is preserved for detailed error analysis and debugging.</p>
     *
     * <p><strong>Message Guidelines:</strong> The message should clearly describe
     * what aspect of initialization failed. Examples of good messages:</p>
     * <ul>
     *   <li>"Failed to create database file at /path/to/database.db"</li>
     *   <li>"Cannot load existing database due to file corruption"</li>
     *   <li>"Database initialization failed: insufficient disk space"</li>
     *   <li>"Unable to open database file: permission denied"</li>
     * </ul>
     *
     * <p><strong>Cause Preservation:</strong> The underlying exception cause is
     * preserved and can be accessed via {@link #getCause()} for detailed
     * error analysis, recovery strategies, and debugging.</p>
     *
     * @param message the detail message describing the initialization failure,
     *                should not be null or empty for meaningful error reporting
     * @param cause   the underlying exception that caused the initialization failure,
     *                typically an IOException, SecurityException, or other system-level exception
     * @snippet <pre>{@code
     * try {
     *     FileWriter writer = new FileWriter(databasePath, true);
     *     loadExistingData();
     * } catch (IOException ioException) {
     *     throw new DatabaseInitializeException(
     *         "Failed to create or open database file: " + databasePath,
     *         ioException
     *     );
     * }
     *
     * try {
     *     validateDatabaseFile();
     *     loadLogEntries();
     * } catch (CorruptedDataException corruptionException) {
     *     throw new DatabaseInitializeException(
     *         "Database file is corrupted and cannot be loaded",
     *         corruptionException
     *     );
     * }
     * }</pre>
     * @see #getMessage()
     * @see #getCause()
     * @see Throwable#printStackTrace()
     */
    public DatabaseInitializeException(
            String message,
            Exception cause) {
        super(
                message,
                cause
        );
    }

    /**
     * Constructs a new database initialization exception with the specified detail message.
     *
     * <p>This constructor creates a new exception with a descriptive message but
     * without an underlying cause. Use this when the initialization failure is
     * detected directly rather than being caused by another exception.</p>
     *
     * @param message the detail message describing the initialization failure,
     *                should not be null or empty for meaningful error reporting
     * @snippet <pre>{@code
     * if (databasePath == null || databasePath.isEmpty()) {
     *     throw new DatabaseInitializeException(
     *         "Database path cannot be null or empty"
     *     );
     * }
     *
     * if (getDiskSpace() < MIN_REQUIRED_SPACE) {
     *     throw new DatabaseInitializeException(
     *         "Insufficient disk space for database initialization"
     *     );
     * }
     * }</pre>
     */
    public DatabaseInitializeException(String message) {
        super(message);
    }

    /**
     * Constructs a new database initialization exception with the specified cause.
     *
     * <p>This constructor creates a new exception that wraps an underlying cause
     * without providing an additional message. The message will be derived from
     * the cause exception.</p>
     *
     * @param cause the underlying exception that caused the initialization failure,
     *              must not be null
     */
    public DatabaseInitializeException(Exception cause) {
        super(cause);
    }

    /**
     * Returns a string representation of this exception for debugging purposes.
     *
     * <p>This method provides a formatted string that includes the exception type,
     * message, and information about the underlying cause if present. This is
     * particularly useful for logging during application startup when database
     * initialization fails.</p>
     *
     * @return a string representation of this exception including cause information
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());

        String message = getMessage();
        if (message != null && !message.isEmpty()) {
            sb
                    .append(": ")
                    .append(message);
        }

        Throwable cause = getCause();
        if (cause != null) {
            sb
                    .append(" (caused by: ")
                    .append(cause
                                    .getClass()
                                    .getSimpleName());
            String causeMessage = cause.getMessage();
            if (causeMessage != null && !causeMessage.isEmpty()) {
                sb
                        .append(": ")
                        .append(causeMessage);
            }
            sb.append(")");
        }

        return sb.toString();
    }
}
