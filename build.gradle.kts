import com.fagi.test.TestResultsService
import info.solidsoft.gradle.pitest.PitestPluginExtension
import org.gradle.api.tasks.testing.TestDescriptor
import org.gradle.api.tasks.testing.TestListener
import org.gradle.api.tasks.testing.TestResult
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    alias(libs.plugins.pitest) apply false
}

allprojects {
    group = "com.fagi"
    version = "1.0-snapshot"
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "info.solidsoft.pitest")

    tasks.withType<JavaCompile> {
        sourceCompatibility = JavaVersion.VERSION_25.toString()
        targetCompatibility = JavaVersion.VERSION_25.toString()
    }

    configure<PitestPluginExtension> {
        threads.set(4)
        outputFormats.set(listOf("XML", "HTML"))
        exportLineCoverage.set(true)
        timestampedReports.set(false)
        junit5PluginVersion.set("1.2.2")
        verbose.set(false)

        if (project.name in listOf("shared")) {
            failWhenNoMutations = false
        }
    }
}

// Register the custom build service
val testResultsServiceProvider = gradle.sharedServices.registerIfAbsent("testResultsService", TestResultsService::class.java) {}

// Configure all projects to use the custom build service
allprojects {
    repositories {
        mavenCentral()
    }

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
        addTestListener(object : TestListener {
            override fun beforeSuite(suite: TestDescriptor) {}
            override fun beforeTest(testDescriptor: TestDescriptor) {}
            override fun afterTest(testDescriptor: TestDescriptor, result: TestResult) {}
            override fun afterSuite(suite: TestDescriptor, result: TestResult) {
                if (suite.parent == null) {
                    testResultsServiceProvider.get().addTestResult(suite, result)
                }
            }
        })
    }
}