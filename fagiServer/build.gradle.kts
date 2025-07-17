plugins {
    id("application")
}

application {
    mainClass.set("com.fagi.main.Main")
}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    implementation(project(":shared"))

    testImplementation(testFixtures(project(":shared")))
    testImplementation(libs.bundles.junit.base)
    testImplementation(libs.bundles.mockito)
}
