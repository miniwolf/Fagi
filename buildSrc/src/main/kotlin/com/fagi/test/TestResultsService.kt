package com.fagi.test

import groovy.time.TimeCategory
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import org.gradle.api.tasks.testing.TestDescriptor
import org.gradle.api.tasks.testing.TestResult
import java.util.*

abstract class TestResultsService : BuildService<BuildServiceParameters.None>, AutoCloseable {
    private val testsResults = mutableListOf<String>()

    fun addTestResult(desc: TestDescriptor, result: TestResult) {
        val summary = "${desc.name} results: ${result.resultType} " +
        "(" +
            "${result.testCount} tests, " +
            "${result.successfulTestCount} successes, " +
            "${result.failedTestCount} failures, " +
            "${result.skippedTestCount} skipped" +
            ") " +
            "in ${TimeCategory.minus(Date(result.endTime), Date(result.startTime))}"
        testsResults.add(summary)
    }

    override fun close() {
        if (testsResults.isNotEmpty()) {
            printResults(testsResults)
        }
    }

    private fun printResults(allResults: List<String>) {
        println(allResults.joinToString("\n\n") {
            it.lines().joinToString("\n")
        })
    }
}
