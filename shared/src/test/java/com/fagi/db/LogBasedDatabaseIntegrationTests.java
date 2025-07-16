package com.fagi.db;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import static com.fagi.utility.Checksum.calculateChecksum;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

class LogBasedDatabaseIntegrationTests {

    @Test
    void testCloseIOException(@TempDir Path tempDir) throws IOException, DatabaseInitializeException {
        FileWriter mockWriter = Mockito.mock(FileWriter.class);
        doThrow(new IOException("Disk full"))
                .when(mockWriter)
                .close();
        Path dbPath = tempDir.resolve("test.db");
        var ignored = dbPath.toFile().createNewFile();

        LogBasedDatabase db = new LogBasedDatabase(
                dbPath.toString(),
                mockWriter
        );

        // Capture System.err output to verify error message
        ByteArrayOutputStream errOutput = new ByteArrayOutputStream();
        PrintStream originalErr = System.err;
        System.setErr(new PrintStream(errOutput));

        db.close();

        String errorOutput = errOutput.toString();
        Assertions.assertTrue(errorOutput.contains("Error closing database"));
        Assertions.assertTrue(errorOutput.contains("Disk full"));

        verify(mockWriter).close();
        System.setErr(originalErr);
    }

    @Test
    public void putWritesToDisk(@TempDir Path tempDir) throws IOException, DatabaseInitializeException, DatabaseUpdateException {
        Path dbPath = tempDir.resolve("user_management.db");
        LogBasedDatabase db = new LogBasedDatabase(dbPath.toString());

        db.put(
                "user001",
                "name",
                "Alice"
        );

        var lines = Files.readAllLines(dbPath);
        Assertions.assertEquals(
                "PUT|user001|name|Alice|90dcc7bd",
                lines.getFirst()
        );

        db.close();
    }

    @Test
    public void invalidOperation(@TempDir Path tempDir) throws IOException, DatabaseInitializeException, DatabaseUpdateException {
        var dbPath = tempDir.resolve("user_management.db");
        var db = new LogBasedDatabase(dbPath.toString());
        db.put(
                "user1",
                "name",
                "John"
        );
        db.close();

        Files.writeString(
                dbPath,
                "DELETE|user2|name|Valid|" + calculateChecksum("DELETE|user2|name|Valid"),
                StandardOpenOption.APPEND
        );

        try {
            db = new LogBasedDatabase(dbPath.toString());
            db.close();
            Assertions.fail();
        } catch (DatabaseInitializeException e) {
            Assertions.assertEquals(
                    "DELETE: not supported operation",
                    e
                            .getCause()
                            .getMessage()
            );
            db.close();
        }
    }

    @Test
    public void testUserManagementScenario(@TempDir Path tempDir) throws DatabaseInitializeException, DatabaseUpdateException {
        String dbPath = tempDir
                .resolve("user_management.db")
                .toString();
        LogBasedDatabase db = new LogBasedDatabase(dbPath);

        try {
            // Phase 1: User Registration
            db.put(
                    "user001",
                    "name",
                    "Alice Johnson"
            );
            db.put(
                    "user001",
                    "email",
                    "alice@example.com"
            );
            db.put(
                    "user001",
                    "role",
                    "admin"
            );
            db.put(
                    "user001",
                    "created",
                    "2024-01-01"
            );

            db.put(
                    "user002",
                    "name",
                    "Bob Smith"
            );
            db.put(
                    "user002",
                    "email",
                    "bob@example.com"
            );
            db.put(
                    "user002",
                    "role",
                    "user"
            );
            db.put(
                    "user002",
                    "created",
                    "2024-01-02"
            );

            // Phase 2: User Updates
            db.put(
                    "user001",
                    "last_login",
                    "2024-01-15"
            );
            db.put(
                    "user002",
                    "role",
                    "moderator"
            ); // Promotion

            // Phase 3: Query Operations
            List<Map<String, String>> admins = db.queryByField(
                    "role",
                    "admin"
            );
            List<Map<String, String>> moderators = db.queryByField(
                    "role",
                    "moderator"
            );

            Assertions.assertEquals(
                    1,
                    admins.size()
            );
            Assertions.assertEquals(
                    1,
                    moderators.size()
            );
            Assertions.assertEquals(
                    "Alice Johnson",
                    admins
                            .getFirst()
                            .get("name")
            );
            Assertions.assertEquals(
                    "Bob Smith",
                    moderators
                            .getFirst()
                            .get("name")
            );

            // Phase 4: Database Restart (simulate application restart)
            db.close();
            db = new LogBasedDatabase(dbPath);

            // Phase 5: Verify Persistence
            Assertions.assertEquals(
                    2,
                    db.count()
            );
            String alice = db.get(
                    "user001",
                    "role"
            );
            String alice_login = db.get(
                    "user001",
                    "last_login"
            );
            String bob = db.get(
                    "user002",
                    "role"
            );

            Assertions.assertEquals(
                    "admin",
                    alice
            );
            Assertions.assertEquals(
                    "2024-01-15",
                    alice_login
            );
            Assertions.assertEquals(
                    "moderator",
                    bob
            );

            // Phase 6: User Deletion
//            boolean deleted = db.delete("user002");
//            Assertions.assertTrue(deleted);
//            Assertions.assertEquals(1, db.count());
//            Assertions.assertNull(db.get("user002", "role"));

        } finally {
            db.close();
        }
    }
}
