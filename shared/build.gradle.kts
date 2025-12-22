tasks.test {
    useJUnitPlatform()
}

dependencies {
    implementation(libs.gson)
    testImplementation(libs.bundles.junit.base)
    testImplementation(libs.junit.platform)
    testImplementation(libs.bundles.mockito)
}
