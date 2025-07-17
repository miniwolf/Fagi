package com.fagi.db;

import com.fagi.BaseFagiTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

public class LogBasedDatabaseResourceTests extends BaseFagiTest {
    @Test
    void testMemoryUsage(@TempDir Path tempDir) throws DatabaseInitializeException, DatabaseUpdateException {
        String dbPath = tempDir
                .resolve("memory_test.db")
                .toString();

        try (LogBasedDatabase db = new LogBasedDatabase(dbPath)) {
            // Record initial memory
            Runtime runtime = Runtime.getRuntime();
            runtime.gc();
            long initialMemory = runtime.totalMemory() - runtime.freeMemory();

            // Add many records
            int recordCount = 10000;
            for (int i = 0; i < recordCount; i++) {
                db.put(
                        "record" + i,
                        "data",
                        "This is record number " + i + " with some data"
                );
            }

            // Check memory usage
            runtime.gc();
            long finalMemory = runtime.totalMemory() - runtime.freeMemory();
            long memoryUsed = finalMemory - initialMemory;

            System.out.printf(
                    "Memory used for %d records: %d bytes (%.2f bytes per record)%n",
                    recordCount,
                    memoryUsed,
                    (double) memoryUsed / recordCount
            );

            // Memory usage should be reasonable (less than 1KB per record)
            Assertions.assertTrue(
                    memoryUsed < recordCount * 1024,
                    "Memory usage too high: " + memoryUsed + " bytes"
            );
        }
    }

    @Test
    void testResourceCleanup(@TempDir Path tempDir) throws DatabaseInitializeException, DatabaseUpdateException {
        String dbPath = tempDir
                .resolve("cleanup_test.db")
                .toString();

        // This should not throw exceptions
        for (int i = 0; i < 10; i++) {
            LogBasedDatabase db = new LogBasedDatabase(dbPath);
            db.put(
                    "test",
                    "iteration",
                    String.valueOf(i)
            );
            db.close();
        }
    }
}