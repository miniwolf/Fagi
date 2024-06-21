import com.fagi.test.TestResultsService
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

allprojects {
    group = "com.fagi"
    version = "1.0-snapshot"
}

subprojects {
    apply(plugin = "java")

    repositories {
        mavenCentral()
    }

    tasks.withType<JavaCompile> {
        sourceCompatibility = JavaVersion.VERSION_21.toString()
        targetCompatibility = JavaVersion.VERSION_21.toString()
    }
}

// Register the custom build service
val testResultsServiceProvider = gradle.sharedServices.registerIfAbsent("testResultsService", TestResultsService::class.java) {}

// Configure all projects to use the custom build service
allprojects {
    tasks.withType<Test> {
        testLogging {
            events = setOf(
                TestLogEvent.FAILED,
                TestLogEvent.SKIPPED,
                TestLogEvent.STANDARD_OUT,
                TestLogEvent.STANDARD_ERROR
            )
            exceptionFormat = TestExceptionFormat.FULL
            showExceptions = true
            showCauses = true
            showStackTraces = true
        }

        // After a test suite is done running this is called to register the result
        afterSuite(KotlinClosure2<TestDescriptor, TestResult, Unit>({ desc, result ->
            if (desc.parent == null) {
                val testResultsService = testResultsServiceProvider.get()
                testResultsService.addTestResult(desc, result)
            }
        }))
    }
}