package com.fagi.db;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        LogBasedDatabaseTests.class,
        LogBasedDatabasePerformanceTests.class,
        LogBasedDatabaseConcurrencyTests.class,
        LogBasedDatabaseCorruptionTests.class,
        LogBasedDatabaseIntegrationTests.class,
        LogBasedDatabaseResourceTests.class
})
public class LogBasedDatabaseAllTests {
}
