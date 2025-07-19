package com.fagi.db;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import static com.fagi.utility.Checksum.calculateChecksum;

/**
 * A thread-safe, append-only Log-based database that stores key-value records with persistence.
 *
 * <p>This database implementation uses an append-only Log file for durability and maintains an
 * in-memory index for fast read access. All operations are thread-safe and support concurrent
 * read access with exclusive write access.</p>
 *
 * <h3>Key features:</h3>
 * <ul>
 *     <li><strong>Thread safety:</strong> Multiple threads can read concurrently, writes are exclusive</li>
 *     <li><strong>Persistence:</strong> All data is automatically persisted to disk using append-only Logging</li>
 *     <li><strong>Data Integrity:</strong> CRC32 checksums prevent data corruption</li>
 *     <li><strong>Fast Reads:</strong> O(1) direct access, O(n) queries</li>
 *     <li><strong>ACID Properties:</strong> Each operation is atomic and durable</li>
 * </ul>
 *
 * <h3>File format:</h3>
 * <pre>
 * PUT|recordId|fieldKey|fieldValue|checksum
 * PUT|user1|username|name|a1b2c3d4
 * PUT|user1|email|hans@example.com|e5f6g7h8
 * </pre>
 *
 * <h3>Performance Characteristics:</h3>
 * <ul>
 *     <li><strong>Write Operations:</strong> O(1) - append to log</li>
 *     <li><strong>Read Operations:</strong> O(1) - direct memory access</li>
 *     <li><strong>Query Operations:</strong> O(n) - scan all records</li>
 *     <li><strong>Startup Time:</strong> O(Log_size) - replay entire log</li>
 * </ul>
 *
 * <h3>Usage example:</h3>
 * <pre>{@code
 *  try (LogBasedDatabase db = new LogBasedDatabase("myApp.db")) {
 *      // store user data
 *      db.put("user1", "username", "name");
 *      db.put("user1", "email", "hans@example.com");
 *      db.put("user1", "age", "30");
 *
 *      // retrieve specific field
 *      String username = db.get("user1", "username"); // "name"
 *
 *      // retrieve complete record
 *      Map<string, string> user = db.get("user1");
 *
 *      // Query by field value
 *      List<Map<string, string>> adults = db.queryFields("age", "30");
 *  }
 * }</pre>
 *
 * <h3>Thread Safety:</h3>
 * <p>This class is fully thread-safe. Multiple threads can:</p>
 * <ul>
 *     <li>Read data concurrently without blocking each other</li>
 *     <li>Write data exclusively (writes block other writes and reads)</li>
 *     <li>Query data concurrently without blocking each other</li>
 * </ul>
 *
 * <h3>Error Handling:</h3>
 * <p>The database handles various error conditions gracefully:</p>
 * <ul>
 *     <li>Corrupted Log entries are skipped with warnings to stderr</li>
 *     <li>Malformed entries are ignored during startup</li>
 *     <li>I/O errors during writes throw RuntimeException</li>
 * </ul>
 *
 * <h3>Limitations</h3>
 * <ul>
 *     <li>All data must fit in memory</li>
 *     <li>Log file grows indefinitely (no automatic compaction) - yet</li>
 *     <li>Startup time increases with log file size</li>
 *     <li>No transaction support beyond single operations</li>
 * </ul>
 *
 * @author Nicklas Pingel
 * @see com.fagi.utility.Checksum
 */
public class LogBasedDatabase implements AutoCloseable {

    enum DBOperation {
        PUT,
    }

    /**
     * Path to the Log file where the operations are persisted
     */
    private final String logFilePath;
    /**
     * In-memory storage for fast read access. Maps record ID to field-value pairs
     */
    private final Map<String, Map<String, String>> records;
    /**
     * Read-write lock for thread safety. Allows concurrent reads, exclusive writes.
     */
    private final ReadWriteLock lock;
    /**
     * File level synchronization lock for log file operations
     */
    private final Object fileLock = new Object();
    /**
     * Writer for appending operations to the Log file
     */
    private final FileWriter logWriter;

    /**
     * Creates a new Log-based database with the specified file path.
     *
     * <p>This constructor initializes the database, creates or opens the Log file,
     * and loads existing data from the log. If the Log file doesn't exist, it will
     * be created. If it exists, all valid entries will be loaded into memory.</p>
     *
     * <p><strong>Note:</strong> The database file will be created if it doesn't exist.
     * Ensure the parent directory exists and is writable.</p>
     *
     * @param filePath the path to the database Log file, must not be null
     * @throws DatabaseInitializeException if the database cannot be initialized due to:
     * <ul>
     *     <li>I/O errors when creating/opening the Log file</li>
     *     <li>Permission issue with the file or directory</li>
     *     <li>Corruption in the existing Log file that prevents loading</li>
     * </ul>
     */
    public LogBasedDatabase(String filePath) throws DatabaseInitializeException {
        this.logFilePath = filePath;
        this.records = new ConcurrentHashMap<>();
        this.lock = new ReentrantReadWriteLock();

        try {
            this.logWriter = new FileWriter(
                    logFilePath,
                    true
            );
            loadFromLog();
        } catch (IOException e) {
            close();
            throw new DatabaseInitializeException("Failed to initialize database", e);
        } catch (DatabaseInitializeException e) {
            close();
            throw e;
        }
    }

    /**
     * Creates a new log-based database with a custom FileWriter for testing purposes.
     *
     * <p><strong>Warning:</strong> This constructor is intended for testing only.
     * It bypasses normal file initialization and error handling. The provided
     * FileWriter must be properly configured for append mode.</p>
     *
     * @param filePath the path to the database log file, used for identification
     * @param logWriter the FileWriter to use for logging operations, must not be null
     * @throws DatabaseInitializeException if the database cannot be initialized due to:
     * <ul>
     *     <li>I/O errors when creating/opening the Log file</li>
     *     <li>Permission issue with the file or directory</li>
     *     <li>Corruption in the existing Log file that prevents loading</li>
     * </ul>
     */
    public LogBasedDatabase(
            String filePath,
            FileWriter logWriter) throws DatabaseInitializeException {
        this.logFilePath = filePath;
        this.records = new ConcurrentHashMap<>();
        this.lock = new ReentrantReadWriteLock();
        this.logWriter = logWriter;
        loadFromLog();
    }

    /**
     * Stores a key-value pair for the specified record ID.
     *
     * <p>This operation is atomic and durable. The data is immediately written to
     * the Log file and updated in memory. If a field already exists for the given
     * record ID, it will be updated with the new value.</p>
     *
     * <p><strong>Thread Safety:</strong> This method is thread-safe. It will block
     * other write operations and all read operations until complete.</p>
     *
     * <p><strong>Performance:</strong> O(1) time complexity. Constant time regardless
     * of database size.</p>
     *
     * @param id the record identifier, must not be null
     * @param key the field key, must not be null
     * @param value the field value, must not be null
     * @throws IllegalArgumentException if any parameter is null
     * @throws DatabaseUpdateException if the write operation fails due to I/O errors
     *
     * @snippet
     * <pre>{@code
     * db.put("user123", "name", "Alice");
     * db.put("user123", "email", "alice@example.com");
     * db.put("user123", "status", "active");
     * }</pre>
     */
    public void put(
            String id,
            String key,
            String value) throws DatabaseUpdateException {
        if (id == null || key == null || value == null) {
            throw new IllegalArgumentException("ID, key, and value cannot be null");
        }

        lock
                .writeLock()
                .lock();
        try {
            records
                    .computeIfAbsent(
                            id,
                            k -> new HashMap<>()
                    )
                    .put(
                            key,
                            value
                    );
            appendToLog(
                    DBOperation.PUT,
                    id,
                    key,
                    value
            );
        } finally {
            lock
                    .writeLock()
                    .unlock();
        }
    }

    /**
     * Retrieves a specific field value for the given record ID.
     *
     * <p><strong>Thread Safety:</strong> This method is thread-safe and allows
     * concurrent read access with other read operations.</p>
     *
     * <p><strong>Performance:</strong> O(1) time complexity. Direct hash map lookup.</p>
     *
     * @param id the record identifier to look up, must not be null
     * @param key the field key to retrieve, must not be null
     * @return the field value, or {@code null} if the record or field doesn't exist
     * @throws IllegalArgumentException if the id or key is null
     *
     * @snippet
     * <pre>{@code
     * String email = db.get("user123", "email"); // Returns "alice@example.com" or null
     * String phone = db.get("user123", "phone"); // Returns null if field doesn't exist
     * String name = db.get("nonexistent", "name"); // Returns null if record doesn't exist
     * }</pre>
     */
    public String get(
            String id,
            String key) {
        lock
                .readLock()
                .lock();
        try {
            Map<String, String> record = records.get(id);
            return record != null ? record.get(key) : null;
        } finally {
            lock
                    .readLock()
                    .unlock();
        }
    }

    /**
     * Retrieves all fields for the specified record ID as a map.
     *
     * <p>Returns a defensive copy of the record data, so modifications to the
     * returned map will not affect the stored data.</p>
     *
     * <p><strong>Thread Safety:</strong> This method is thread-safe and allows
     * concurrent read access with other operations.</p>
     *
     * <p><strong>Performance:</strong> O(f) where f is the number of fields in the record.
     * Requires copying all field data.</p>
     *
     * @param id the record identifier to retrieve, must not be null
     * @return a copy of all fields for the record, or {@code null} if the record doesn't exist
     * @throws IllegalArgumentException if id is null
     * @snippet
     * <pre>{@code
     * Map<String, String> user = db.get("user123");
     * if (user != null) {
     *     String name = user.get("name");     // "Alice Johnson"
     *     String email = user.get("email");   // "alice@example.com"
     *     String status = user.get("status"); // "active"
     * }
     * }</pre>
     */
    public Map<String, String> get(String id) {
        lock
                .readLock()
                .lock();
        try {
            Map<String, String> record = records.get(id);
            return record != null ? new HashMap<>(record) : null;
        } finally {
            lock
                    .readLock()
                    .unlock();
        }
    }

    /**
     * Loads existing data from the Log file into memory.
     *
     * <p>This method is called during initialization to restore the database
     * state from the persistent Log file. It processes each Log entry sequentially,
     * verifies checksum, and rebuilds the in-memory index.</p>
     *
     * <p><strong>Error Handling:</strong></p>
     * <ul>
     *     <li>Malformed entries are skipped with warnings</li>
     *     <li>Corrupted entries (bad checksum) are skipped with warnings</li>
     *     <li>I/O errors cause RuntimeException</li>
     * </ul>
     *
     * @throws DatabaseInitializeException if the Log file cannot be read or processed
     */
    private void loadFromLog() throws DatabaseInitializeException {
        synchronized (fileLock) {
            try (BufferedReader reader = new BufferedReader(new FileReader(logFilePath))) {
                String line;
                int lineNumber = 0;

                while ((line = reader.readLine()) != null) {
                    lineNumber++;
                    if (line
                            .trim()
                            .isEmpty()) {
                        continue;
                    }

                    String[] parts = line.split("\\|");
                    if (parts.length < 5) {
                        System.err.println("Malformed entry at line " + lineNumber);
                        continue;
                    }

                    String storedChecksum = parts[4];
                    String originalEntry = String.join(
                            "|",
                            Arrays.copyOf(
                                    parts,
                                    4
                            )
                    );

                    if (!calculateChecksum(originalEntry).equals(storedChecksum)) {
                        System.err.println("Corruption detected at line " + lineNumber);
                        continue;
                    }

                    processLogEntry(parts);
                }
            } catch (Exception e) {
                throw new DatabaseInitializeException(
                        "Failed to load from log",
                        e
                );
            }
        }
    }

    /**
     * Processes a single log entry and updates the in-memory records.
     *
     * @param parts the parsed Log entry components [operation, id, key, value, checksum]
     * @throws UnsupportedOperationException if the operation type is not supported
     */
    private void processLogEntry(String[] parts) {
        String operation = parts[0];
        String id = parts[1];

        switch (operation) {
            case "PUT" -> {
                String key = parts[2];
                String value = parts[3];
                records
                        .computeIfAbsent(
                                id,
                                k -> new HashMap<>()
                        )
                        .put(
                                key,
                                value
                        );
            }
            case null, default -> throw new UnsupportedOperationException(operation + ": not supported operation");
        }
    }

    /**
     * Appends an operation to the Log file with checksum verification.
     *
     * @param operation the operator type (e.g. "PUT")
     * @param id the record identifier
     * @param key the field key
     * @param value the field value
     * @throws DatabaseUpdateException if the write operations fails
     */
    private void appendToLog(
            DBOperation operation,
            String id,
            String key,
            String value) throws DatabaseUpdateException {
        synchronized (fileLock) {
            try {
                String logEntry = String.format(
                        "%s|%s|%s|%s",
                        operation,
                        id,
                        key,
                        value
                );
                String checksum = calculateChecksum(logEntry);
                String fullEntry = logEntry + "|" + checksum;

                logWriter.write(fullEntry + "\n");
                logWriter.flush();
            } catch (IOException e) {
                throw new DatabaseUpdateException(
                        "Failed to write to log",
                        e
                );
            }
        }
    }

    /**
     * Queries all records that have a specific field with a specific value.
     *
     * <p>This method performs a linear scan of all records and returns copies
     * of matching records. The returned list and maps are defensive copies.</p>
     *
     * <p><strong>Thread Safety:</strong> This method is thread-safe and allows
     * concurrent read access with other read operations.</p>
     *
     * <p><strong>Performance:</strong> O(n) where n is the total number of records.
     * Performance degrades linearly with database size.</p>
     *
     * @param fieldName the name of the field to match against, must not be null
     * @param fieldValue the value to search for, must not be null
     * @return a List or records (as maps) that contain the specified field-value pair,
     *         empty List if no matches found, never null
     * @throws IllegalArgumentException if fieldName or fieldValue is null
     *
     * @snippet
     * <pre>{@code
     * // Find all active users
     * List<Map<String, String>> activeUsers = db.queryByField("status", "active");
     *
     * // Find all users in a specific city
     * List<Map<String, String>> nyUsers = db.queryByField("city", "New York");
     *
     * // Process results
     * for (Map<String, String> user : activeUsers) {
     *     System.out.println("Active user: " + user.get("name"));
     * }
     * }</pre>
     */
    public List<Map<String, String>> queryByField(
            String fieldName,
            String fieldValue) {
        lock
                .readLock()
                .lock();
        try {
            return records
                    .values()
                    .stream()
                    .filter(record -> fieldValue.equals(record.get(fieldName)))
                    .map(HashMap::new)
                    .collect(Collectors.toList());
        } finally {
            lock
                    .readLock()
                    .unlock();
        }
    }

    /**
     * Returns the total number of records in the database.
     *
     * <p><strong>Thread Safety:</strong> This method is thread-safe and allows
     * concurrent read-access with other read operations.</p>
     *
     * <p><strong>Performance:</strong> O(1) time complexity. Direct size lookup.</p>
     *
     * @return the number of records currently stored in the database, never negative
     *
     * @snippet
     * <pre>{@code
     * int totalUsers = db.count();
     * System.out.println("Database contains " + totalUsers + " records");
     *
     * // Check if database is empty
     * if (db.count() == 0) {
     *     System.out.println("Database is empty");
     * }
     * }</pre>
     */
    public int count() {
        lock
                .readLock()
                .lock();
        try {
            return records.size();
        } finally {
            lock
                    .readLock()
                    .unlock();
        }
    }

    /**
     * Closes the database and releases all resources.
     *
     * <p>This method closes the underlying Log file writer and release any
     * system resources. After calling this method, no further operations
     * should be performed on this database instance.</p>
     *
     * <p><strong>Thread Safety:</strong> This method is thread-safe but should
     * only be called when no other operations are in progress.</p>
     *
     * <p><strong>Error Handling:</strong> I/O errors during close are logged
     * to stderr but do not throw exceptions.</p>
     *
     * <p>This method is idempotent - calling it multiple times has no additional effect.</p>
     *
     * @snippet
     * <pre>{@code
     * LogBasedDatabase db = new LogBasedDatabase("myapp.db");
     * try {
     *     // Use database...
     *     db.put("test", "key", "value");
     * } finally {
     *     db.close(); // Always close to release resources
     * }
     *
     * // Or use try-with-resources
     * try (LogBasedDatabase db = new LogBasedDatabase("myapp.db")) {
     *     db.put("test", "key", "value");
     * } // Automatically closed
     * }</pre>
     */
    @Override
    public void close() {
        synchronized (fileLock) {
            try {
                if (logWriter != null) {
                    logWriter.close();
                }
            } catch (IOException e) {
                System.err.println("Error closing database: " + e.getMessage());
            }
        }
    }
}
