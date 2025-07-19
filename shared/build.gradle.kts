plugins {
    id("java-test-fixtures")
}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    implementation(libs.gson)

    testFixturesImplementation(libs.junit.jupiter.api)

    testImplementation(testFixtures(project(":shared")))
    testImplementation(libs.bundles.junit.base)
    testImplementation(libs.junit.platform)
    testImplementation(libs.bundles.mockito)
}
