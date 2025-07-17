package com.fagi.utility;

import java.util.zip.CRC32;

/**
 * Utility class for calculating CRC32 checksums to ensure data integrity.
 *
 * <p>This class provides methods for generating checksums that can detect data corruption,
 * transmission errors, and accidental modifications. It uses the CRC32 algorithm which
 * provides a good balance between performance and error detection capabilities.</p>
 *
 * <h3>Algorithm Details:</h3>
 * <ul>
 *   <li><strong>Algorithm:</strong> CRC32 (Cyclic Redundancy Check, 32-bit)</li>
 *   <li><strong>Output Format:</strong> Hexadecimal string representation</li>
 *   <li><strong>Collision Resistance:</strong> 1 in 2^32 chance of false positive</li>
 *   <li><strong>Performance:</strong> Very fast, suitable for real-time applications</li>
 * </ul>
 *
 * <h3>Use Cases:</h3>
 * <ul>
 *   <li>Verifying data integrity in log files</li>
 *   <li>Detecting corruption in stored data</li>
 *   <li>Ensuring data hasn't been accidentally modified</li>
 *   <li>Network transmission error detection</li>
 * </ul>
 *
 * <h3>Limitations:</h3>
 * <ul>
 *   <li>Not cryptographically secure - should not be used for security purposes</li>
 *   <li>Designed for detecting accidental errors, not malicious tampering</li>
 *   <li>Uses default platform encoding for string-to-byte conversion</li>
 * </ul>
 *
 * <h3>Thread Safety:</h3>
 * <p>This class is thread-safe. All methods are static and use local variables only.</p>
 *
 * <h3>Usage Example:</h3>
 * <pre>{@code
 * // Calculate checksum for data integrity
 * String data = "PUT|user1|name|John Doe";
 * String checksum = Checksum.calculateChecksum(data);
 * LOGGER.info(() -> "Checksum: " + checksum); // e.g., "a1b2c3d4"
 *
 * // Verify data integrity
 * String receivedData = "PUT|user1|name|John Doe";
 * String receivedChecksum = "a1b2c3d4";
 * String calculatedChecksum = Checksum.calculateChecksum(receivedData);
 *
 * if (calculatedChecksum.equals(receivedChecksum)) {
 *     LOGGER.info(() -> "Data integrity verified");
 * } else {
 *     LOGGER.warning(() -> "Data corruption detected!");
 * }
 * }</pre>
 *
 * @author Nicklas Pingel
 * @see java.util.zip.CRC32
 * @see com.fagi.db.LogBasedDatabase
 */
public class Checksum {
    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * @throws UnsupportedOperationException if called via reflection
     */
    private Checksum() {
        throw new UnsupportedOperationException("Utility class - do not instantiate");
    }

    /**
     * Calculates a CRC32 checksum for the given string data.
     *
     * <p>This method computes a 32-bit cyclic redundancy check (CRC32) of the input string
     * and returns it as a hexadecimal string. The CRC32 algorithm is widely used for
     * detecting accidental changes to data.</p>
     *
     * <p><strong>Performance:</strong> O(n) where n is the length of the input string.
     * Very fast operation suitable for frequent use.</p>
     *
     * <p><strong>Deterministic:</strong> The same input will always produce the same
     * checksum, making it reliable for data verification.</p>
     *
     * <p><strong>Encoding:</strong> Uses the platform's default character encoding
     * to convert the string to bytes. For consistent results across platforms,
     * consider specifying the encoding explicitly if needed.</p>
     *
     * <h4>Checksum Properties:</h4>
     * <ul>
     *   <li>Fixed length output (8 hexadecimal characters or less)</li>
     *   <li>Case-sensitive input</li>
     *   <li>Detects single-bit errors with 100% probability</li>
     *   <li>Detects burst errors up to 32 bits with 100% probability</li>
     *   <li>Detects longer burst errors with high probability (1 - 2^-32)</li>
     * </ul>
     *
     * @param data the input string to calculate checksum for, must not be null
     * @return the CRC32 checksum as a hexadecimal string (1-8 characters),
     *         never null or empty
     * @throws NullPointerException if data is null
     *
     * @snippet
     * <pre>{@code
     * // Basic usage
     * String checksum1 = Checksum.calculateChecksum("Hello World");
     * String checksum2 = Checksum.calculateChecksum("Hello World");
     * assert checksum1.equals(checksum2); // Same input = same checksum
     *
     * // Different inputs produce different checksums
     * String checksum3 = Checksum.calculateChecksum("Hello world"); // lowercase 'w'
     * assert !checksum1.equals(checksum3); // Different inputs = different checksums
     *
     * // Empty string handling
     * String emptyChecksum = Checksum.calculateChecksum("");
     * LOGGER.info(() -> "Empty string checksum: " + emptyChecksum);
     *
     * // Database log entry checksumming
     * String logEntry = "PUT|user123|email|john@example.com";
     * String logChecksum = Checksum.calculateChecksum(logEntry);
     * String fullLogLine = logEntry + "|" + logChecksum;
     * }</pre>
     *
     * @implNote This implementation uses {@link java.util.zip.CRC32} from the
     *           Java standard library, which implements the CRC32 algorithm
     *           as defined in RFC 3309 and ISO 3309.
     *
     * @see java.util.zip.CRC32
     */
    public static String calculateChecksum(String data) {
        if (data == null) {
            throw new NullPointerException("Data cannot be null");
        }

        CRC32 crc = new CRC32();
        crc.update(data.getBytes());
        return Long.toHexString(crc.getValue());
    }

    /**
     * Calculates a CRC32 checksum for the given string data using specified encoding.
     *
     * <p>This overloaded method allows explicit control over the character encoding
     * used to convert the string to bytes, ensuring consistent results across
     * different platforms and JVM configurations.</p>
     *
     * <p><strong>Recommended Usage:</strong> Use this method when you need consistent
     * checksums across different systems or when storing checksums that will be
     * verified on different platforms.</p>
     *
     * @param data the input string to calculate checksum for, must not be null
     * @param encoding the character encoding to use (e.g., "UTF-8", "UTF-16"), must not be null
     * @return the CRC32 checksum as a hexadecimal string (1-8 characters),
     *         never null or empty
     * @throws NullPointerException if data or encoding is null
     * @throws java.io.UnsupportedEncodingException if the encoding is not supported
     *
     * @snippet
     * <pre>{@code
     * // Ensure consistent encoding across platforms
     * String data = "Café münü"; // Contains non-ASCII characters
     * String checksum = Checksum.calculateChecksum(data, "UTF-8");
     *
     * // Compare with default encoding
     * String defaultChecksum = Checksum.calculateChecksum(data);
     * // May or may not be equal depending on platform default encoding
     * }</pre>
     */
    public static String calculateChecksum(String data, String encoding)
            throws java.io.UnsupportedEncodingException {
        if (data == null) {
            throw new NullPointerException("Data cannot be null");
        }
        if (encoding == null) {
            throw new NullPointerException("Encoding cannot be null");
        }

        CRC32 crc = new CRC32();
        crc.update(data.getBytes(encoding));
        return Long.toHexString(crc.getValue());
    }

    /**
     * Verifies that the given data matches the expected checksum.
     *
     * <p>This convenience method calculates the checksum of the provided data
     * and compares it with the expected checksum, returning true if they match.</p>
     *
     * <p><strong>Use Case:</strong> Ideal for data integrity verification where
     * you have both the data and its expected checksum.</p>
     *
     * @param data the data to verify, must not be null
     * @param expectedChecksum the expected checksum value, must not be null
     * @return true if the calculated checksum matches the expected checksum,
     *         false otherwise
     * @throws NullPointerException if data or expectedChecksum is null
     *
     * @snippet
     * <pre>{@code
     * // Verify data integrity
     * String originalData = "Important data";
     * String storedChecksum = Checksum.calculateChecksum(originalData);
     *
     * // Later, verify the data hasn't been corrupted
     * String retrievedData = "Important data";
     * if (Checksum.verifyChecksum(retrievedData, storedChecksum)) {
     *     LOGGER.info(() -> "Data integrity verified");
     * } else {
     *     LOGGER.warning(() -> "Data corruption detected!");
     * }
     * }</pre>
     */
    public static boolean verifyChecksum(String data, String expectedChecksum) {
        if (data == null) {
            throw new NullPointerException("Data cannot be null");
        }
        if (expectedChecksum == null) {
            throw new NullPointerException("Expected checksum cannot be null");
        }

        String actualChecksum = calculateChecksum(data);
        return actualChecksum.equals(expectedChecksum);
    }
}
