package com.fagi.db;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import static com.fagi.utility.Checksum.calculateChecksum;

public class LogBasedDatabaseCorruptionTests {
    private LogBasedDatabase db;
    private String dbPath;

    @BeforeEach
    void setUp(@TempDir Path tempDir) {
        dbPath = tempDir
                .resolve("corruption_test.db")
                .toString();
    }

    @AfterEach
    void tearDown() {
        if (db != null) {
            db.close();
        }
    }

    @Test
    void testCorruptedChecksum() throws IOException, DatabaseInitializeException, DatabaseUpdateException {
        db = new LogBasedDatabase(dbPath);
        db.put(
                "user1",
                "name",
                "John"
        );
        db.put(
                "user2",
                "name",
                "Jane"
        );
        db.close();

        // Corrupt the checksum in the log file
        String content = Files.readString(Paths.get(dbPath));
        String corruptedContent = content.replaceFirst(
                "([a-f0-9]+)$",
                "corrupted"
        );
        Files.writeString(
                Paths.get(dbPath),
                corruptedContent
        );

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setErr(new PrintStream(out));

        db = new LogBasedDatabase(dbPath);

        Assertions.assertEquals(
                "Corruption detected at line 2" + System.lineSeparator(),
                out.toString()
        );

        Assertions.assertEquals(
                1,
                db.count(),
                "Should have skipped corrupted entry"
        );
    }

    @Test
    void testTruncatedEntries() throws IOException, DatabaseInitializeException, DatabaseUpdateException {
        db = new LogBasedDatabase(dbPath);
        db.put(
                "user1",
                "name",
                "John"
        );
        db.close();

        // Truncate the last line
        List<String> lines = Files.readAllLines(Paths.get(dbPath));
        if (!lines.isEmpty()) {
            String lastLine = lines.getLast();
            String truncated = lastLine.substring(
                    0,
                    lastLine.length() / 2
            );
            lines.set(
                    lines.size() - 1,
                    truncated
            );
            Files.write(
                    Paths.get(dbPath),
                    lines
            );
        }

        // Should handle gracefully
        Assertions.assertDoesNotThrow(() -> {
            db = new LogBasedDatabase(dbPath);
        });
    }

    @Test
    void testEmptyLogFile() throws IOException {
        Files.createFile(Paths.get(dbPath));

        Assertions.assertDoesNotThrow(() -> {
            db = new LogBasedDatabase(dbPath);
        });
        Assertions.assertEquals(
                0,
                db.count()
        );
    }

    @Test
    void testOperationsAfterRecovery() throws IOException, DatabaseInitializeException, DatabaseUpdateException {
        db = new LogBasedDatabase(dbPath);
        db.put(
                "user1",
                "name",
                "John"
        );
        db.close();

        Files.writeString(
                Paths.get(dbPath),
                "\nCORRUPTED_LINE\n",
                StandardOpenOption.APPEND
        );

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setErr(new PrintStream(out));

        // Reload and continue operations
        db = new LogBasedDatabase(dbPath);

        Assertions.assertEquals(
                "Malformed entry at line 3" + System.lineSeparator(),
                out.toString()
        );

        // Should be able to continue normal operations
        db.put(
                "user2",
                "name",
                "Jane"
        );
        db.put(
                "user3",
                "name",
                "Bob"
        );

        Assertions.assertEquals(
                3,
                db.count()
        );
        Assertions.assertEquals(
                "Jane",
                db.get(
                        "user2",
                        "name"
                )
        );
    }

    @Test
    void testMalformedEntries() throws IOException, DatabaseInitializeException, DatabaseUpdateException {
        db = new LogBasedDatabase(dbPath);
        db.put(
                "user1",
                "name",
                "John"
        );
        db.close();

        // Add malformed entries
        List<String> malformedEntries = Arrays.asList(
                "INVALID_LINE",
                "PUT|incomplete",
                "PUT|||missing_data|checksum",
                "|||||empty_fields",
                "PUT|user2|name|Valid|" + calculateChecksum("PUT|user2|name|Valid")
        );

        Files.write(
                Paths.get(dbPath),
                malformedEntries,
                StandardOpenOption.APPEND
        );

        db = new LogBasedDatabase(dbPath);

        Assertions.assertEquals(
                2,
                db.count(),
                "Should have two valid records"
        );
        Assertions.assertNotNull(db.get(
                "user1",
                "name"
        ));
        Assertions.assertNotNull(db.get(
                "user2",
                "name"
        ));
    }
}

