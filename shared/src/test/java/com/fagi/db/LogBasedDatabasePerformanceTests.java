package com.fagi.db;

import com.fagi.BaseFagiTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class LogBasedDatabasePerformanceTests extends BaseFagiTest {
    private LogBasedDatabase db;
    private String dbPath;

    @BeforeEach
    void setUp(@TempDir Path tempDir) {
        dbPath = tempDir
                .resolve("perf_test.db")
                .toString();
        try {
            db = new LogBasedDatabase(dbPath);
        } catch (DatabaseInitializeException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    void tearDown() {
        if (db != null) {
            db.close();
        }
    }

    @Test
    void testWritePerformance() throws DatabaseUpdateException {
        int recordCount = 10000;

        long startTime = System.nanoTime();

        for (int i = 0; i < recordCount; i++) {
            db.put(
                    "user" + i,
                    "name",
                    "User " + i
            );
            db.put(
                    "user" + i,
                    "email",
                    "user" + i + "@example.com"
            );
            db.put(
                    "user" + i,
                    "age",
                    String.valueOf(20 + (i % 50))
            );
        }

        long endTime = System.nanoTime();
        long durationMs = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);

        System.out.printf(
                "Wrote %d records in %d ms (%.2f records/sec)%n",
                recordCount,
                durationMs,
                (recordCount * 1000.0) / durationMs
        );

        // Verify
        Assertions.assertEquals(
                recordCount,
                db.count()
        );

        // Performance assertion - should write at least 1000 records per second
        double recordsPerSecond = (recordCount * 1000.0) / durationMs;
        Assertions.assertTrue(
                recordsPerSecond > 1000,
                "Write performance too slow: " + recordsPerSecond + " records/sec"
        );
    }

    @Test
    void testReadPerformance() throws DatabaseUpdateException {
        // Setup data
        int recordCount = 10000;
        for (int i = 0; i < recordCount; i++) {
            db.put(
                    "user" + i,
                    "name",
                    "User " + i
            );
        }

        // Test random reads
        Random random = new Random(42); // Fixed seed for reproducibility
        long startTime = System.nanoTime();

        for (int i = 0; i < recordCount; i++) {
            int randomId = random.nextInt(recordCount);
            String result = db.get(
                    "user" + randomId,
                    "name"
            );
            Assertions.assertNotNull(result);
        }

        long endTime = System.nanoTime();
        long durationMs = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);

        System.out.printf(
                "Read %d records in %d ms (%.2f reads/sec)%n",
                recordCount,
                durationMs,
                (recordCount * 1000.0) / durationMs
        );

        // Performance assertion
        double readsPerSecond = (recordCount * 1000.0) / durationMs;
        Assertions.assertTrue(
                readsPerSecond > 10000,
                "Read performance too slow: " + readsPerSecond + " reads/sec"
        );
    }

    @Test
    void testConcurrentReadsAndWrites() throws InterruptedException {
        int readerThreads = 5;
        int writerThreads = 3;
        int operationsPerThread = 600;
        CountDownLatch latch = new CountDownLatch(readerThreads + writerThreads);
        AtomicInteger readCount = new AtomicInteger(0);
        AtomicInteger writeCount = new AtomicInteger(0);

        long startTime = System.nanoTime();
        try (ExecutorService executor = Executors.newFixedThreadPool(readerThreads + writerThreads)) {

            // Start writer threads
            for (int t = 0; t < writerThreads; t++) {
                final int threadId = t;
                executor.submit(() -> {
                    try {
                        for (int i = 0; i < operationsPerThread; i++) {
                            String id = "writer" + threadId + "_" + i;
                            db.put(
                                    id,
                                    "data",
                                    "value" + i
                            );
                            writeCount.incrementAndGet();

                            if (i % 10 == 0) {
                                Thread.yield(); // Give readers a chance
                            }
                        }
                    } catch (DatabaseUpdateException e) {
                        throw new RuntimeException(e);
                    } finally {
                        latch.countDown();
                    }
                });
            }

            // Start reader threads
            for (int t = 0; t < readerThreads; t++) {
                executor.submit(() -> {
                    try {
                        for (int i = 0; i < operationsPerThread; i++) {
                            // Try to read various records
                            db.count();
                            //db.getAllIds();

                            // Try to read specific records (might not exist yet)
                            for (int w = 0; w < writerThreads; w++) {
                                String id = "writer" + w + "_" + (i % 50);
                                db.get(id);
                            }

                            readCount.incrementAndGet();

                            if (i % 20 == 0) {
                                Thread.yield();
                            }
                        }
                    } finally {
                        latch.countDown();
                    }
                });
            }
            boolean completed = latch.await(
                    60,
                    TimeUnit.SECONDS
            );
            executor.shutdown();

            Assertions.assertTrue(
                    completed,
                    "Test didn't complete in time"
            );
        }

        long endTime = System.nanoTime();
        long durationMs = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);


        System.out.printf(
                "Completed %d reads and %d writes concurrently%n",
                readCount.get(),
                writeCount.get()
        );


        double readsPerSecond = (readCount.get() * 1000.0) / durationMs;
        double writesPerSecond = (writeCount.get() * 1000.0) / durationMs;


        System.out.printf(
                "Performed %d reads in %d ms (%.2f reads/sec)%n",
                readCount.get(),
                durationMs,
                readsPerSecond
        );
        System.out.printf(
                "Performed %d writes in %d ms (%.2f writes/sec)%n",
                writeCount.get(),
                durationMs,
                writesPerSecond
        );

        // Verify final state
        int expectedWrites = writerThreads * operationsPerThread;
        Assertions.assertEquals(
                expectedWrites,
                db.count()
        );
    }

    @Test
    void testQueryPerformance() throws DatabaseUpdateException {
        String[] cities = {"New York", "Los Angeles", "Chicago", "Houston", "Phoenix"};
        int recordCount = 5000;

        for (int i = 0; i < recordCount; i++) {
            db.put(
                    "user" + i,
                    "name",
                    "User " + i
            );
            db.put(
                    "user" + i,
                    "city",
                    cities[i % cities.length]
            );
        }

        long startTime = System.nanoTime();

        for (String city : cities) {
            List<Map<String, String>> results = db.queryByField(
                    "city",
                    city
            );
            Assertions.assertEquals(
                    recordCount / cities.length,
                    results.size()
            );
        }

        long endTime = System.nanoTime();
        long durationMs = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);

        System.out.printf(
                "Performed %d queries in %d ms (%.2f queries/sec)%n",
                cities.length,
                durationMs,
                (cities.length * 1000.0) / durationMs
        );

        double queriesPerSecond = (cities.length * 1000.0) / durationMs;
        Assertions.assertTrue(
                queriesPerSecond >= 200,
                "Query performance too slow: " + queriesPerSecond + " queries/sec"
        );
    }

    @Test
    void testStartupPerformance() throws DatabaseUpdateException {
        int recordCount = 5000;
        for (int i = 0; i < recordCount; i++) {
            db.put(
                    "user" + i,
                    "name",
                    "User " + i
            );
            db.put(
                    "user" + i,
                    "email",
                    "user" + i + "@example.com"
            );
        }
        db.close();

        long startTime = System.nanoTime();
        try {
            db = new LogBasedDatabase(dbPath);
        } catch (DatabaseInitializeException e) {
            throw new RuntimeException(e);
        }
        long endTime = System.nanoTime();

        long durationMs = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);

        System.out.printf(
                "Startup with %d records took %d ms%n",
                recordCount,
                durationMs
        );

        // Verify data loaded correctly
        Assertions.assertEquals(
                recordCount,
                db.count()
        );

        Assertions.assertTrue(
                durationMs < 100,
                "Startup too slow: " + durationMs + " ms"
        );
    }
}