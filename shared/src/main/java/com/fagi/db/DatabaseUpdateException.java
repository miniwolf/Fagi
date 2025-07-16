package com.fagi.db;

/**
 * Exception thrown when a database update operation fails.
 *
 * <p>This exception is used to indicate that a database modification operation
 * (such as insert, update, or delete) could not be completed successfully.
 * It wraps the underlying cause of the failure while providing a clear,
 * database-specific error context.</p>
 *
 * <h3>Common Scenarios:</h3>
 * <ul>
 *   <li>I/O errors when writing to the database log file</li>
 *   <li>Disk space exhaustion during write operations</li>
 *   <li>File permission issues preventing database updates</li>
 * </ul>
 *
 * <h3>Exception Hierarchy:</h3>
 * <pre>
 * java.lang.Exception
 *   └── DatabaseUpdateException
 * </pre>
 *
 * <p><strong>Checked Exception:</strong> This is a checked exception that must be
 * explicitly handled by calling code, forcing developers to consider database
 * update failure scenarios.</p>
 *
 * <h3>Usage Pattern:</h3>
 * <pre>{@code
 * try {
 *     database.put("user1", "name", "John Doe");
 *     database.put("user1", "email", "john@example.com");
 * } catch (DatabaseUpdateException e) {
 *     logger.error("Failed to update user record: " + e.getMessage(), e);
 *     // Handle the failure - retry, fallback, or propagate
 * }
 * }</pre>
 *
 * <h3>Best Practices:</h3>
 * <ul>
 *   <li>Always log the full exception with stack trace for debugging</li>
 *   <li>Provide meaningful error messages to help with troubleshooting</li>
 *   <li>Preserve the original cause exception for detailed error analysis</li>
 * </ul>
 *
 * @author Nicklas Pingel
 * @see com.fagi.db.LogBasedDatabase
 * @see java.lang.Exception
 */
public class DatabaseUpdateException extends Exception {
    /**
     * Serial version UID for serialization compatibility
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new database update exception with the specified detail message and cause.
     *
     * <p>This constructor creates a new exception that wraps an underlying cause
     * while providing a specific error message describing the database update failure.
     * The cause exception is preserved for detailed error analysis and stack trace
     * information.</p>
     *
     * <p><strong>Message Guidelines:</strong> The message should be descriptive
     * and provide context about what operation failed. Avoid generic messages
     * like "error occurred" - instead use specific descriptions like
     * "Failed to update user record in database" or "Database write operation failed".</p>
     *
     * <p><strong>Cause Preservation:</strong> The underlying exception cause is
     * preserved and can be accessed via {@link #getCause()} for detailed
     * error analysis and debugging.</p>
     *
     * @param message the detail message describing the update failure,
     *                should not be null or empty for meaningful error reporting
     * @param cause   the underlying exception that caused the update failure,
     *                typically an IOException, or other database-related exception
     * @see #getMessage()
     * @see #getCause()
     */
    public DatabaseUpdateException(
            String message,
            Exception cause) {
        super(
                message,
                cause
        );
    }

    /**
     * Constructs a new database update exception with the specified detail message.
     *
     * <p>This constructor creates a new exception with a descriptive message but
     * without an underlying cause. Use this when the failure is detected directly
     * rather than being caused by another exception.</p>
     *
     * @param message the detail message describing the update failure,
     *                should not be null or empty for meaningful error reporting
     */
    public DatabaseUpdateException(String message) {
        super(message);
    }

    /**
     * Constructs a new database update exception with the specified cause.
     *
     * <p>This constructor creates a new exception that wraps an underlying cause
     * without providing an additional message. The message will be derived from
     * the cause exception.</p>
     *
     * @param cause the underlying exception that caused the update failure,
     *              must not be null
     */
    public DatabaseUpdateException(Exception cause) {
        super(cause);
    }

    /**
     * Returns a string representation of this exception for debugging purposes.
     *
     * <p>This method provides a formatted string that includes the exception type,
     * message, and information about the underlying cause if present. This is
     * useful for logging and debugging database update failures.</p>
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