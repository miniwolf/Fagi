plugins {
    id("application")
    alias(libs.plugins.javafx)
}

application {
    mainClass.set("com.fagi.main.FagiApp")
}

tasks.test {
    useJUnitPlatform()
}

javafx {
    version = "25.0.3"
    modules = mutableListOf("javafx.controls", "javafx.fxml", "javafx.web", "javafx.graphics")
}

dependencies {
    implementation(project(":shared"))
    testImplementation(testFixtures(project(":shared")))

    testImplementation(libs.bundles.junit.base)
    testImplementation(libs.bundles.mockito)
    testImplementation(libs.hamcrest)
    testImplementation(libs.bundles.testfx)
}
