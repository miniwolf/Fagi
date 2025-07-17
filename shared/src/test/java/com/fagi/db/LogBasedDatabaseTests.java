package com.fagi.db;

import com.fagi.BaseFagiTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class LogBasedDatabaseTests extends BaseFagiTest {
    private String dbPath;

    @BeforeEach
    public void setup(@TempDir Path tempDir) {
        dbPath = tempDir
                .resolve("test.db")
                .toString();
    }

    @Test
    public void constructingWithInvalidPath() throws DatabaseInitializeException {
        try (var ignored = new LogBasedDatabase(dbPath)) {
            var directory = Paths
                    .get(dbPath)
                    .getParent();
            var exception = Assertions.assertThrows(
                    DatabaseInitializeException.class,
                    () -> new LogBasedDatabase(directory.toString())
            );
            Assertions.assertEquals(
                    "Failed to initialize database",
                    exception.getMessage()
            );
        }
    }

    @Test
    public void constructingWithMissingFile(@TempDir Path tempDir) {
        Assertions.assertDoesNotThrow(() -> {
            var db = new LogBasedDatabase(tempDir.resolve("myFile.db").toString());
            db.close();
        });
    }

    @Test
    public void putAfterClose() throws DatabaseInitializeException {
        DatabaseUpdateException exception;
        try (var db = new LogBasedDatabase(dbPath)) {
            db.close();
            exception = Assertions.assertThrows(
                    DatabaseUpdateException.class,
                    () -> db.put(
                            "id",
                            "key",
                            "value"
                    )
            );
        }
        Assertions.assertEquals(
                "Failed to write to log",
                exception.getMessage()
        );
    }

    @Test
    public void putGet() throws DatabaseUpdateException, DatabaseInitializeException {
        var id = "user";
        var key = "name";
        var value = "dinMor";

        String result;
        try (var db = new LogBasedDatabase(dbPath)) {
            db.put(
                    id,
                    key,
                    value
            );
            result = db.get(
                    id,
                    key
            );
        }

        Assertions.assertEquals(
                value,
                result
        );
    }

    @Test
    public void doublePutGet() throws DatabaseUpdateException, DatabaseInitializeException {
        var id = "user";
        var key = "name";
        var value = "dinMor";

        String result;
        try (var db = new LogBasedDatabase(dbPath)) {
            db.put(
                    id,
                    key,
                    value
            );
            db.put(
                    id,
                    key,
                    value
            );
            result = db.get(
                    id,
                    key
            );
        }

        Assertions.assertEquals(
                value,
                result
        );
    }

    @Test
    public void overwriteValueGet() throws DatabaseUpdateException, DatabaseInitializeException {
        var id = "user";
        var key = "name";
        var oldValue = "dinMor";
        var newValue = "dinMor2";

        String result;
        try (var db = new LogBasedDatabase(dbPath)) {
            db.put(
                    id,
                    key,
                    oldValue
            );
            db.put(
                    id,
                    key,
                    newValue
            );
            result = db.get(
                    id,
                    key
            );
        }

        Assertions.assertEquals(
                newValue,
                result
        );
    }

    @Test
    public void getNonExistent() throws DatabaseInitializeException {
        var id = "user";
        var key = "name";

        String result;
        try (var db = new LogBasedDatabase(dbPath)) {
            result = db.get(
                    id,
                    key
            );
        }

        Assertions.assertNull(result);
    }

    @Test
    public void mapGetNonExistent() throws DatabaseInitializeException {
        var id = "user";

        Map<String, String> result;
        try (var db = new LogBasedDatabase(dbPath)) {
            result = db.get(id);
        }
        Assertions.assertNull(result);
    }

    @Test
    public void putNullCausesException() throws DatabaseInitializeException {
        var a = "";
        var b = "";
        try (var db = new LogBasedDatabase(dbPath)) {
            var exception = Assertions.assertThrows(
                    RuntimeException.class,
                    () -> db.put(
                            null,
                            a,
                            b
                    )
            );
            Assertions.assertEquals(
                    "ID, key, and value cannot be null",
                    exception.getMessage()
            );
            Assertions.assertThrows(
                    RuntimeException.class,
                    () -> db.put(
                            a,
                            null,
                            b
                    )
            );
            Assertions.assertThrows(
                    RuntimeException.class,
                    () -> db.put(
                            a,
                            b,
                            null
                    )
            );
        }
    }
}
