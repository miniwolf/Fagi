package com.fagi.db;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class LogBasedDatabaseConcurrencyTests {
    private LogBasedDatabase db;

    @BeforeEach
    void setUp(@TempDir Path tempDir) {
        String dbPath = tempDir
                .resolve("concurrency_test.db")
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
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    void testConcurrentWrites() throws InterruptedException {
        int threadCount = 10;
        int operationsPerThread = 100;
        try (ExecutorService executor = Executors.newFixedThreadPool(threadCount)) {
            CountDownLatch latch = new CountDownLatch(threadCount);

            for (int t = 0; t < threadCount; t++) {
                final int threadId = t;
                executor.submit(() -> {
                    try {
                        for (int i = 0; i < operationsPerThread; i++) {
                            String id = "thread" + threadId + "_record" + i;
                            db.put(
                                    id,
                                    "threadId",
                                    String.valueOf(threadId)
                            );
                            db.put(
                                    id,
                                    "recordId",
                                    String.valueOf(i)
                            );
                            db.put(
                                    id,
                                    "data",
                                    "Data from thread " + threadId
                            );
                        }
                    } catch (DatabaseUpdateException e) {
                        throw new RuntimeException(e);
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await(
                    30,
                    TimeUnit.SECONDS
            );
            executor.shutdown();

        }

        // Verify all records were written
        int expectedRecords = threadCount * operationsPerThread;
        Assertions.assertEquals(
                expectedRecords,
                db.count()
        );

        // Verify data integrity
        for (int t = 0; t < threadCount; t++) {
            List<Map<String, String>> threadRecords = db.queryByField(
                    "threadId",
                    String.valueOf(t)
            );
            Assertions.assertEquals(
                    operationsPerThread,
                    threadRecords.size()
            );
        }
    }

    @Test
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    void testConcurrentReadsAndWrites() throws InterruptedException {
        int readerThreads = 5;
        int writerThreads = 3;
        int operationsPerThread = 200;
        CountDownLatch latch = new CountDownLatch(readerThreads + writerThreads);
        AtomicInteger readCount = new AtomicInteger(0);
        AtomicInteger writeCount = new AtomicInteger(0);

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
                            db.count();

                            for (int w = 0; w < writerThreads; w++) {
                                String id = "writer" + w + "_" + (i % 50);
                                db.get(id);
                                db.get(
                                        id,
                                        "data"
                                );
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

        System.out.printf(
                "Completed %d reads and %d writes concurrently%n",
                readCount.get(),
                writeCount.get()
        );

        // Verify final state
        int expectedWrites = writerThreads * operationsPerThread;
        int expectedReads = readerThreads * operationsPerThread;
        Assertions.assertEquals(
                expectedWrites,
                db.count()
        );
        Assertions.assertEquals(
                expectedReads,
                readCount.get()
        );
        Assertions.assertEquals(
                expectedWrites,
                writeCount.get()
        );
    }

    @Test
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    void testConcurrentQueriesAndWrites() throws InterruptedException {
        int queryThreads = 5;
        int writerThreads = 3;
        int operationsPerThread = 200;

        CountDownLatch latch = new CountDownLatch(queryThreads + writerThreads);
        AtomicInteger queryCounts = new AtomicInteger(0);
        AtomicInteger writeCount = new AtomicInteger(0);

        try (ExecutorService executor = Executors.newFixedThreadPool(queryThreads + writerThreads)) {

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
            for (int t = 0; t < queryThreads; t++) {
                executor.submit(() -> {
                    try {
                        for (int i = 0; i < operationsPerThread; i++) {
                            db.count();

                            for (int w = 0; w < writerThreads; w++) {
                                String id = "writer" + w + "_" + (i % 50);
                                db.queryByField(
                                        id,
                                        "data"
                                );
                            }

                            queryCounts.incrementAndGet();

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

        System.out.printf(
                "Completed %d queries and %d writes concurrently%n",
                queryCounts.get(),
                writeCount.get()
        );

        // Verify final state
        int expectedWrites = writerThreads * operationsPerThread;
        int expectedQueries = queryThreads * operationsPerThread;
        Assertions.assertEquals(
                expectedWrites,
                db.count()
        );
        Assertions.assertEquals(
                expectedQueries,
                queryCounts.get()
        );
        Assertions.assertEquals(
                expectedWrites,
                writeCount.get()
        );
    }
}
